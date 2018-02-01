package fi.grafiek3dgwt.client;


import fi.grafiek3dgwt.client.expressies.*;

/**
 * class representing a curve in 3-space as an Object3D;
 * note how this is done: points on the curve are given
 * in the form (expX(t),expY(t),expZ(t)), where 
 * for t tPoints values (given) are taken between a given
 * tMin and tMax; the vertices of the curve are then given
 * by the points (expX(t),expY(t),expZ(t)) which connected 
 * by facets consisting of 2 vertices by calculating them
 * at subsequent t-values<br>     
 * note that minima and maxima of x-, y- and z- axis are also
 * given, so that it can be determined if the curve should
 * be cut at x=xMax, x=xMin, y=yMax, y=yMin, z = zMax or z = zMin
 * in order to for in the coordinate cube; undefined vertices are
 * omitted and not used for making facets; near asymptotes
 * facets should be omitted when one of their edges crosses an
 * asymptote, but the algorithm for detecting this is very inefficient. 
 */


public class Curve3D extends Object3D
{
	/**
	 * a very large double
	 */
	final double VERYBIG = 1e10d;
	/**
	 * a very small double
	 */
	final double NZERO = 1e-5d;

	/**
	 * should the top of the curve be trimmed?
	 */
	boolean trimTop = false;
	/**
	 * should the bottom of the curve be trimmed?
	 */
	boolean trimBottom = false;
	/**
	 * should the front of the curve be trimmed?
	 */
	boolean trimFront = false;
	/**
	 * should the back of the curve be trimmed?
	 */
	boolean trimBack = false;
	/**
	 * should the left of the curve be trimmed?
	 */
	boolean trimLeft = false;
	/**
	 * should the right of the curve be trimmed?
	 */
	boolean trimRight = false;
	
	/**
	 * the vertex with the largest z value smaller then zMax
	 */
	Vector3D topMaxVertex = null;
	/**
	 * the vertex with the smallest z value larger then zMin
	 */
	Vector3D bottomMinVertex = null;
	/**
	 * the vertex with the smallest y value larger then yMin (negative y-axis point to the front)
	 */
	Vector3D frontMinVertex = null;
	/**
	 * the vertex with the largest y value smaller then yMax (positive y-axis point to the back)
	 */
	Vector3D backMaxVertex = null;
	/**
	 * the vertex with the smallest x value larger then xMin (negative x-axis point to the left)
	 */
	Vector3D leftMinVertex = null;
	/**
	 * the vertex with the largest x value smaller then xMax (positive x-axis point to the right)
	 */
	Vector3D rightMaxVertex = null;
	/**
	 * a vertex of the graph inside the axes-cube
	 * with maximum distance to the center of the axes-cube
	 */
	Vector3D insideVertex = null;
	
	/**
	 * distance between insideVertex and the center of the axes-cube
	 */
	double centerDis = 0;

	/**
	 * some big positive xValue, see method isUnwanted 
	 */
	double bigPosX = 0;
	/**
	 * some big negative xValue, see method isUnwanted 
	 */
	double bigMinX = 0;
	/**
	 * some big positive yValue, see method isUnwanted 
	 */
	double bigPosY = 0;
	/**
	 * some big negative yValue, see method isUnwanted 
	 */
	double bigMinY = 0;
	/**
	 * some big positive zValue, see method isUnwanted 
	 */
	double bigPosZ = 0;
	/**
	 * some big negative zValue, see method isUnwanted 
	 */
	double bigMinZ = 0;
	
	/**
	 * check if the defining functions have asymptotes,
	 * see class Grafiek3DComponent 
	 */
	boolean checkForAsymptotes = false;

	/**
	 * default constructor
	 */
    public Curve3D()
    {}
    /**
     * constructor
     * @param expX in t for x-coordinates 
     * @param expY in t for y-coordinates
     * @param expZ in t for z-coordinates
     * @param cfa checking for asymptotes
     * @param tMin minimum t-value
     * @param tMax maximum t-value
     * @param tPoints number of points between tMin and tMax
     * @param xMin minimum x-axis
     * @param xMax maximum x-axis
     * @param yMin minimum y-axis
     * @param yMax maximum y-axis
     * @param zMin minimum z-axis
     * @param zMax maximum z-axis
     * @param paramNaam name of t-variable
     */
    public Curve3D(Expressie expX, Expressie expY, Expressie expZ,
    			   boolean cfa,
    			   double tMin, double tMax, int tPoints, 
   				   double xMin, double xMax,  
    			   double yMin, double yMax, 
    			   double zMin, double zMax, 
    			   String paramNaam)
    {
    	checkForAsymptotes = cfa;
		bigPosX = 10 * xMax;
		bigMinX = 10 * xMin;
		bigPosY = 10 * yMax;
		bigMinY = 10 * yMin;
		bigPosZ = 10 * zMax;
		bigMinZ = 10 * zMin;
		
		Vector3D centerPoint = new Vector3D((xMin + xMax) / 2, (yMin + yMax) / 2, (zMin + zMax) / 2);
		Vector3D cornerPoint = new Vector3D(xMax, yMax, zMax);
		centerDis = Vector3D.distance(centerPoint, cornerPoint) + 1; 
		
		double tStep = (tMax - tMin) / tPoints;
		
		numVertices = (tPoints + 1);
		numVertexLabels = numVertices;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
		
    	int numCurveFacets = tPoints;
    	
		double[] subst = new double[1];
		String[] vars = new String[1];
		vars[0] = paramNaam;
		for (int tCnt = 0; tCnt < (tPoints + 1); tCnt++)			
		{	subst[0] = tMin + tCnt * tStep;
			double expXWaarde = expX.geefWaarde(subst, vars);
			double expYWaarde = expY.geefWaarde(subst, vars);
			double expZWaarde = expZ.geefWaarde(subst, vars);

			vertices[tCnt] = new Vector3D(expXWaarde, expYWaarde, expZWaarde);

			if (!isUnDefined(expXWaarde))
			{
				if (expXWaarde > (xMax + NZERO))
				{	trimRight = true;
					if ((rightMaxVertex == null) || (rightMaxVertex.x < (expXWaarde - NZERO)))
						rightMaxVertex = new Vector3D(vertices[tCnt]);
				}
				
				if (expXWaarde < (xMin - NZERO))
				{	trimLeft = true;
					if ((leftMinVertex == null) || (leftMinVertex.x > (expXWaarde + NZERO)))
						leftMinVertex = new Vector3D(vertices[tCnt]);
				}
					
				if ((expXWaarde < (xMax - NZERO)) && (expXWaarde > (xMin + NZERO)))
				{
					if ((insideVertex == null) && 
						(centerDis > Vector3D.distance(vertices[tCnt], centerPoint))
					   )	
					{	insideVertex = new Vector3D(vertices[tCnt]);
						centerDis = Vector3D.distance(insideVertex, centerPoint);
					}
					else if ((insideVertex != null) && 
							 (centerDis > Vector3D.distance(vertices[tCnt], centerPoint))
						    )  
					{	insideVertex = new Vector3D(vertices[tCnt]);
						centerDis = Vector3D.distance(insideVertex, centerPoint);
					}
				}
			}

			if (!isUnDefined(expYWaarde))
			{
				if (expYWaarde > (yMax + NZERO))
				{	trimBack = true;
					if ((backMaxVertex == null) || (backMaxVertex.y < (expYWaarde - NZERO)))
						backMaxVertex = new Vector3D(vertices[tCnt]);
				}
				
				if (expYWaarde < (yMin - NZERO))
				{	trimFront = true;
					if ((frontMinVertex == null) || (frontMinVertex.y> (expYWaarde + NZERO)))
						frontMinVertex = new Vector3D(vertices[tCnt]);
				}
					
				if ((expYWaarde < (yMax - NZERO)) && (expYWaarde > (yMin + NZERO)))
				{
					if ((insideVertex == null) && 
						(centerDis > Vector3D.distance(vertices[tCnt], centerPoint))
					   )	
					{	insideVertex = new Vector3D(vertices[tCnt]);
						centerDis = Vector3D.distance(insideVertex, centerPoint);
					}
					else if ((insideVertex != null) && 
							 (centerDis > Vector3D.distance(vertices[tCnt], centerPoint))
						    )  
					{	insideVertex = new Vector3D(vertices[tCnt]);
						centerDis = Vector3D.distance(insideVertex, centerPoint);
					}
				}
			}
				
			if (!isUnDefined(expZWaarde))
			{
				if (expZWaarde > (zMax + NZERO))
				{	trimTop = true;
					if ((topMaxVertex == null) || (topMaxVertex.z < (expZWaarde - NZERO)))
						topMaxVertex = new Vector3D(vertices[tCnt]);
				}
				
				if (expZWaarde < (zMin - NZERO))
				{	trimBottom = true;
					if ((bottomMinVertex == null) || (bottomMinVertex.z > (expZWaarde + NZERO)))
						bottomMinVertex = new Vector3D(vertices[tCnt]);
				}
					
				if ((expZWaarde < (zMax - NZERO)) && (expZWaarde > (zMin + NZERO)))
				{
					if ((insideVertex == null) && 
						(centerDis > Vector3D.distance(vertices[tCnt], centerPoint))
					   )	
					{	insideVertex = new Vector3D(vertices[tCnt]);
						centerDis = Vector3D.distance(insideVertex, centerPoint);
					}
					else if ((insideVertex != null) && 
							 (centerDis > Vector3D.distance(vertices[tCnt], centerPoint))
						    )  
					{	insideVertex = new Vector3D(vertices[tCnt]);
						centerDis = Vector3D.distance(insideVertex, centerPoint);
					}
				}
			}
					
			}
		
		// maximum number
	    int tempNumFacets = numCurveFacets;
	    int numNonNullFacets = 0;
	    Facet3D[] tempFacets = new Facet3D[tempNumFacets];
	    int facetCount = 0;
	        
	    for (int tCnt = 0; tCnt < tPoints; tCnt++)
       	{	int[] indices = new int[2];
       		indices[0] = tCnt;
       		indices[1] = tCnt + 1;

   			boolean equal = false;
   			
       		int unDefinedCnt = 0;
       		int unDefinedIndex = -1;
       		if (isUnDefined(vertices[indices[0]]))
       		{	unDefinedCnt++;
      			unDefinedIndex = 0;
       		}
       		if (isUnDefined(vertices[indices[1]]))
       		{	unDefinedCnt++;
       			unDefinedIndex = 1;
       		}
	       		
       		boolean vertexUnDefined = (unDefinedCnt > 0);
       		if (!vertexUnDefined)
       			equal = vertices[indices[0]].equals(vertices[indices[1]]);
       		
      		
       		int unWantedCnt = 0;
       		if (!vertexUnDefined)
       		{	if (isUnWanted(tMin + tCnt * tStep, tMin + tCnt * tStep + tStep, expX, expY, expZ, paramNaam))
       				unWantedCnt++;
       		}
	       		
       		boolean edgeUnWanted = (unWantedCnt > 0);       		
	        	
	       	if (!equal && !vertexUnDefined && !edgeUnWanted)
	       	{
	       			
	       			// creating tempFacet eliminates subsequent identical vertices in tempFacet, see class Facet3D 
	       			Facet3D tempFacet = new Facet3D(vertices, indices, Grafiek3DComponent.curveColor);
	       			
       				numNonNullFacets++;
	       			tempFacets[facetCount] = tempFacet;
	       			tempFacets[facetCount].outlineColor = Grafiek3DComponent.curveOutlineColor;
	       			facetCount++;
	       			
       		}
        }

	    
	    numFacets = numNonNullFacets;
	    facets = new Facet3D[numFacets];
	    int nonNullCnt = 0;
	    for (int fCnt = 0; fCnt < tempNumFacets; fCnt++)
	    {  	if (tempFacets[fCnt] != null)
	    	{	facets[nonNullCnt] = tempFacets[fCnt];
	    	    nonNullCnt++;
	    	
	    	}
	    }
	    
        for (int vCnt = 0; vCnt < numVertices; vCnt++)
        {	if (isUnDefined(vertices[vCnt]))
        		vertices[vCnt] = new Vector3D((xMin + xMax) / 2, (yMin + yMax) / 2, (zMin + zMax) / 2);
        }
	    
    	
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] = "";

        // find the center !!
        Vector3D center = new Vector3D((xMin + xMax) / 2, (yMin + yMax) / 2, (zMin + zMax) / 2);

        initObject3D(true, center, false);
                
    	
    }

    /**
     * check if double d is NotaNumber or is Infinite
     * @param d double to be checked
     * @return true/false
     */
    public boolean isUnDefined(double d)
    {
    	boolean unDefined = false;
    	
    	unDefined = Double.isNaN(d) || Double.isInfinite(d); 
    	
    	return unDefined;
    }

    /**
     * check if one or more of the coordinates of Vector3D v are undefined
     * @param v Vector3D to be checked
     * @return true/false
     */
    public boolean isUnDefined(Vector3D v)
    {
    	return isUnDefined(v.x) || isUnDefined(v.y) || isUnDefined(v.z);
    }

    /**
     * very inefficient method to locate asymptotes: given two t-values
     * evaluate 100 values of expX, expY and expZ along the segment [t1,t2] and 
     * determine their maximum and minimum; if one of the maxima is large positive and
     * corresponding minimum is large negative, there must be an asymptote between t1 and t2   
     * @param t1 first t-value
     * @param t2 second t-value
     * @param expX x-expression
     * @param expY y-expression
     * @param expZ z-expression
     * @param paramNaam name of t-variable
     * @return true/false
     */
    public boolean isUnWanted(double t1, double t2, Expressie expX, Expressie expY, Expressie expZ, String paramNaam)
    {	
    	if (!checkForAsymptotes)
    	{
    		return false;
    	}    	
    	
    	boolean unWanted = false;
    
    	double[] subst = new double[1];
    	String[] vars = new String[1];
    	vars[0] = paramNaam;
    	subst[0] = t1;
		double expXWaarde = expX.geefWaarde(subst, vars);
		double expYWaarde = expY.geefWaarde(subst, vars);
		double expZWaarde = expZ.geefWaarde(subst, vars);
    
    	double maxX = expXWaarde;
    	double minX = expXWaarde;
    	double maxY = expYWaarde;
    	double minY = expYWaarde;
    	double maxZ = expZWaarde;
    	double minZ = expZWaarde;
    	
    	int steps = 100;
    	
    	double stepT = (t2 - t1) / steps;
    	
    	for (int stepCnt = 1; stepCnt <= steps; stepCnt++)
    	{
    		subst[0] = t1 + stepCnt * stepT;
    		expXWaarde = expX.geefWaarde(subst, vars);
    		expYWaarde = expY.geefWaarde(subst, vars);
    		expZWaarde = expZ.geefWaarde(subst, vars);
    		
    		if (!Double.isNaN(expXWaarde) && !Double.isInfinite(expXWaarde))
    		{
    			maxX = Math.max(maxX, expXWaarde);
    			minX = Math.min(minX, expXWaarde);
    		}
    		if (!Double.isNaN(expYWaarde) && !Double.isInfinite(expYWaarde))
    		{
    			maxY = Math.max(maxY, expYWaarde);
    			minY = Math.min(minY, expYWaarde);
    		}
    		if (!Double.isNaN(expZWaarde) && !Double.isInfinite(expZWaarde))
    		{
    			maxZ = Math.max(maxZ, expZWaarde);
    			minZ = Math.min(minZ, expZWaarde);
    		}
    		
    	}

    	if ((maxX > bigPosX) && (minX < bigMinX))
    	{	unWanted = true;
    	}
    	if ((maxY > bigPosY) && (minY < bigMinY))
    	{	unWanted = true;
    	}
    	if ((maxZ > bigPosZ) && (minZ < bigMinZ))
    	{	unWanted = true;
    	}
    
    	return unWanted;	
    }
    
    /**
     * make a deep copy of this Curve3D
     */
    public Object3D deepCopy()
    {   Curve3D copy = new Curve3D();
        makeDeepObjectCopy(copy);
        
        copy.trimTop = trimTop;
        copy.trimBottom = trimBottom;
        copy.trimFront = trimFront;
        copy.trimBack = trimBack;
        copy.trimLeft = trimLeft;
        copy.trimRight = trimRight;
        
        copy.topMaxVertex = Vector3D.copyVector3D(topMaxVertex);
        copy.bottomMinVertex = Vector3D.copyVector3D(bottomMinVertex);
        copy.frontMinVertex = Vector3D.copyVector3D(frontMinVertex);
        copy.backMaxVertex = Vector3D.copyVector3D(backMaxVertex);
        copy.leftMinVertex = Vector3D.copyVector3D(leftMinVertex);
        copy.rightMaxVertex = Vector3D.copyVector3D(rightMaxVertex);
        
        copy.insideVertex = Vector3D.copyVector3D(insideVertex);
        return copy;        
    }   
    
}
