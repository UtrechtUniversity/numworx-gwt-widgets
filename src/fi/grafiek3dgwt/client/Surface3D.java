package fi.grafiek3dgwt.client;

import fi.grafiek3dgwt.client.expressies.*;

/**
 * class representing a surface in 3-space as an Object3D;
 * note how this is done: points on the surface are given
 * in the form (expX(u,v),expY(u,v),expZ(u,v)), where 
 * for u uPoints values (given) are taken between a given
 * uMin and uMax, and similar for v; the vertices of   
 *  the surface are then given by the points 
 * (expX(u,v),expY(u,v),expZ(u,v)) which result in square 
 * 3d-facets for the surface calculating them on the corners of 
 * the u-v-grid; these square facets are not accurate enough to
 * smoothly approximate the surface, so each of them is subdivided
 * into two triangular facets (choice depending on the longest diagonal
 * of the square facet); <br>     
 * note that minima and maxima of x-, y- and z- axis are also
 * given, so that it can be determined if the surface should
 * be cut at x=xMax, x=xMin, y=yMax, y=yMin, z=zMax or z=zMin
 * in order to for in the coordinate cube; undefined vertices are
 * omitted and not used for making facets; near asymptotes
 * facets should be omitted when one of their edges crosses an
 * asymptote, but the algorithm for detecting this is very inefficient. 
 */

public class Surface3D extends Object3D
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
	 * should the top of the surface be trimmed?
	 */
	boolean trimTop = false;
	/**
	 * should the bottom of the surface be trimmed?
	 */
	boolean trimBottom = false;
	/**
	 * should the front of the surface be trimmed?
	 */
	boolean trimFront = false;
	/**
	 * should the back of the surface be trimmed?
	 */
	boolean trimBack = false;
	/**
	 * should the left of the surface be trimmed?
	 */
	boolean trimLeft = false;
	/**
	 * should the right of the surface be trimmed?
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
    public Surface3D()
    {}
    /**
     * constructor
     * @param expX expression in u and v for x-coordinates
     * @param expY expression in u and v for y-coordinates
     * @param expZ expression in u and v for z-coordinates
     * @param cfa checking for asymptotes
     * @param uMin minimum u-value
     * @param uMax maximum u-value 
     * @param uPoints number of points between uMin and uMax
     * @param vMin minimum v-value
     * @param vMax maximum v-value
     * @param vPoints number of points between vMin and vMax
     * @param xMin minimum x-axis
     * @param xMax maximum x-axis
     * @param yMin minimum y-axis
     * @param yMax maximum y-axis
     * @param zMin minimum z-axis
     * @param zMax maximum z-axis
     * @param paramNaamU name of u-variable
     * @param paramNaamV name of v-variable
     */
    public Surface3D(Expressie expX, Expressie expY, Expressie expZ,
    				 boolean cfa,
    				 double uMin, double uMax, int uPoints, 
    				 double vMin, double vMax, int vPoints,
    				 double xMin, double xMax,  
    				 double yMin, double yMax, 
    				 double zMin, double zMax, 
    				 String paramNaamU, String paramNaamV)
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
		
		double uStep = (uMax - uMin) / uPoints;
		double vStep = (vMax - vMin) / vPoints;
		
		numVertices = (uPoints + 1) * (vPoints + 1);
		numVertexLabels = numVertices;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
		
    	int numSurfaceFacets = uPoints * vPoints;
    	
        // generate vertices, compare class Grafiek3D
		double[] subst = new double[2];
		String[] vars = new String[2];
		vars[0] = paramNaamU;
		vars[1] = paramNaamV;
		for (int vCnt = 0; vCnt < (vPoints + 1); vCnt++)			
			for (int uCnt = 0; uCnt < (uPoints + 1); uCnt++)
			{	subst[0] = uMin + uCnt * uStep;
				subst[1] = vMin + vCnt * vStep;
				double expXWaarde = expX.geefWaarde(subst, vars);
				double expYWaarde = expY.geefWaarde(subst, vars);
				double expZWaarde = expZ.geefWaarde(subst, vars);

				vertices[uCnt + (uPoints + 1) * vCnt] = 
					new Vector3D(expXWaarde, expYWaarde, expZWaarde);

				if (!isUnDefined(expXWaarde))
				{
					if (expXWaarde > (xMax + NZERO))
					{	trimRight = true;
						if ((rightMaxVertex == null) || (rightMaxVertex.x < (expXWaarde - NZERO)))
							rightMaxVertex = new Vector3D(vertices[uCnt + (uPoints + 1) * vCnt]);
					}
				
					if (expXWaarde < (xMin - NZERO))
					{	trimLeft = true;
						if ((leftMinVertex == null) || (leftMinVertex.x > (expXWaarde + NZERO)))
							leftMinVertex = new Vector3D(vertices[uCnt + (uPoints + 1) * vCnt]);
					}
					
					if ((expXWaarde < (xMax - NZERO)) && (expXWaarde > (xMin + NZERO)))
					{
						if ((insideVertex == null) && 
							(centerDis > Vector3D.distance(vertices[uCnt + (uPoints + 1) * vCnt], centerPoint))
						   )	
						{	insideVertex = new Vector3D(vertices[uCnt + (uPoints + 1) * vCnt]);
							centerDis = Vector3D.distance(insideVertex, centerPoint);
						}
						else if ((insideVertex != null) && 
								 (centerDis > Vector3D.distance(vertices[uCnt + (uPoints + 1) * vCnt], centerPoint))
							    )  
						{	insideVertex = new Vector3D(vertices[uCnt + (uPoints + 1) * vCnt]);
							centerDis = Vector3D.distance(insideVertex, centerPoint);
						}
					}
				}

				if (!isUnDefined(expYWaarde))
				{
					if (expYWaarde > (yMax + NZERO))
					{	trimBack = true;
						if ((backMaxVertex == null) || (backMaxVertex.y < (expYWaarde - NZERO)))
							backMaxVertex = new Vector3D(vertices[uCnt + (uPoints + 1) * vCnt]);
					}
				
					if (expYWaarde < (yMin - NZERO))
					{	trimFront = true;
						if ((frontMinVertex == null) || (frontMinVertex.y> (expYWaarde + NZERO)))
							frontMinVertex = new Vector3D(vertices[uCnt + (uPoints + 1) * vCnt]);
					}
					
					if ((expYWaarde < (yMax - NZERO)) && (expYWaarde > (yMin + NZERO)))
					{
						if ((insideVertex == null) && 
							(centerDis > Vector3D.distance(vertices[uCnt + (uPoints + 1) * vCnt], centerPoint))
						   )	
						{	insideVertex = new Vector3D(vertices[uCnt + (uPoints + 1) * vCnt]);
							centerDis = Vector3D.distance(insideVertex, centerPoint);
						}
						else if ((insideVertex != null) && 
								 (centerDis > Vector3D.distance(vertices[uCnt + (uPoints + 1) * vCnt], centerPoint))
							    )  
						{	insideVertex = new Vector3D(vertices[uCnt + (uPoints + 1) * vCnt]);
							centerDis = Vector3D.distance(insideVertex, centerPoint);
						}
					}
				}
				
				if (!isUnDefined(expZWaarde))
				{
					if (expZWaarde > (zMax + NZERO))
					{	trimTop = true;
						if ((topMaxVertex == null) || (topMaxVertex.z < (expZWaarde - NZERO)))
							topMaxVertex = new Vector3D(vertices[uCnt + (uPoints + 1) * vCnt]);
					}
				
					if (expZWaarde < (zMin - NZERO))
					{	trimBottom = true;
						if ((bottomMinVertex == null) || (bottomMinVertex.z > (expZWaarde + NZERO)))
							bottomMinVertex = new Vector3D(vertices[uCnt + (uPoints + 1) * vCnt]);
					}
					
					if ((expZWaarde < (zMax - NZERO)) && (expZWaarde > (zMin + NZERO)))
					{
						if ((insideVertex == null) && 
							(centerDis > Vector3D.distance(vertices[uCnt + (uPoints + 1) * vCnt], centerPoint))
						   )	
						{	insideVertex = new Vector3D(vertices[uCnt + (uPoints + 1) * vCnt]);
							centerDis = Vector3D.distance(insideVertex, centerPoint);
						}
						else if ((insideVertex != null) && 
								 (centerDis > Vector3D.distance(vertices[uCnt + (uPoints + 1) * vCnt], centerPoint))
							    )  
						{	insideVertex = new Vector3D(vertices[uCnt + (uPoints + 1) * vCnt]);
							centerDis = Vector3D.distance(insideVertex, centerPoint);
						}
					}
				}
					
			}
		
		// maximum number
	    int tempNumFacets = 2 * numSurfaceFacets;
	    int numNonNullFacets = 0;
	    Facet3D[] tempFacets = new Facet3D[tempNumFacets];
	    int facetCount = 0;
	    
	    // compare class Grafiek3D
	    for (int vCnt = 0; vCnt < vPoints; vCnt++)
	     	for (int uCnt = 0; uCnt < uPoints; uCnt++)
	       	{	int[] indices = new int[4];
	       		indices[0] = uCnt + (uPoints + 1) * vCnt;
	       		indices[1] = uCnt + 1 + (uPoints + 1) * vCnt;
	       		indices[2] = uCnt + 1 + (uPoints + 1) * (vCnt + 1);
	       		indices[3] = uCnt + (uPoints + 1) * (vCnt + 1);	

       			int[] indices1 = new int[3];
       			indices1[0] = indices[0];
       			indices1[1] = indices[1];
       			indices1[2] = indices[2];
       			int[] indices2 = new int[3];
       			indices2[0] = indices[0];
       			indices2[1] = indices[2];
       			indices2[2] = indices[3];
       			int[] indices3 = new int[3];
       			indices3[0] = indices[0];
       			indices3[1] = indices[1];
       			indices3[2] = indices[3];
       			int[] indices4 = new int[3];
       			indices4[0] = indices[1];
       			indices4[1] = indices[2];
       			indices4[2] = indices[3];
	       		
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
	       		if (isUnDefined(vertices[indices[2]]))
	       		{	unDefinedCnt++;
	       			unDefinedIndex = 2;
	       		}
	       		if (isUnDefined(vertices[indices[3]]))
	       		{	unDefinedCnt++;
	       			unDefinedIndex = 3;
	       		}
	       		
	       		if (unDefinedCnt == 1)
	       		{
	       			if (unDefinedIndex == 0)
	       			{	
	       				Facet3D tempFacet = new Facet3D(vertices, indices4, Grafiek3DComponent.surfaceColor);
	       				
	       				if (tempFacet.numPoints > 2)
	       				{	
//CHECK UNWANTED	       					
	       					numNonNullFacets++;
	       					tempFacets[facetCount] = tempFacet;
	       					tempFacets[facetCount].outlineColor = Grafiek3DComponent.surfaceOutlineColor;
	       					tempFacets[facetCount].edgeCodes[0] = 52;
	       					tempFacets[facetCount].edgeCodes[1] = 52;
	       					tempFacets[facetCount].edgeCodes[2] = 51;
	       					facetCount++;
	       				}	
	       				
	       			}
	       			if (unDefinedIndex == 1)
	       			{
	       				Facet3D tempFacet = new Facet3D(vertices, indices2, Grafiek3DComponent.surfaceColor);
	       				
	       				if (tempFacet.numPoints > 2)
	       				{
//CHECK UNWANTED	       					
	       					numNonNullFacets++;
	       					tempFacets[facetCount] = tempFacet;
	       					tempFacets[facetCount].outlineColor = Grafiek3DComponent.surfaceOutlineColor;
	       					tempFacets[facetCount].edgeCodes[0] = 52;
	       					tempFacets[facetCount].edgeCodes[1] = 52;
	       					tempFacets[facetCount].edgeCodes[2] = 51;
	       					facetCount++;
	       				}	
	       			}
	       			if (unDefinedIndex == 2)
	       			{
	       				Facet3D tempFacet = new Facet3D(vertices, indices3, Grafiek3DComponent.surfaceColor);
	       				
	       				if (tempFacet.numPoints > 2)
	       				{
//CHECK UNWANTED	       					
	       					numNonNullFacets++;
	       					tempFacets[facetCount] = tempFacet;
	       					tempFacets[facetCount].outlineColor = Grafiek3DComponent.surfaceOutlineColor;
	       					tempFacets[facetCount].edgeCodes[0] = 52;
	       					tempFacets[facetCount].edgeCodes[1] = 52;
	       					tempFacets[facetCount].edgeCodes[2] = 51;
	       					facetCount++;
	       				}	
	       			}
	       			if (unDefinedIndex == 3)
	       			{
	       				Facet3D tempFacet = new Facet3D(vertices, indices1, Grafiek3DComponent.surfaceColor);
	       				
	       				if (tempFacet.numPoints > 2)
	       				{	
	       					numNonNullFacets++;
	       					tempFacets[facetCount] = tempFacet;
	       					tempFacets[facetCount].outlineColor = Grafiek3DComponent.surfaceOutlineColor;
	       					tempFacets[facetCount].edgeCodes[0] = 52;
	       					tempFacets[facetCount].edgeCodes[1] = 52;
	       					tempFacets[facetCount].edgeCodes[2] = 51;
	       					facetCount++;
	       				}	
	       			}
	       		}
	       		
	       		
	       		boolean vertexUnDefined = (unDefinedCnt > 0);

				int unWantedCnt = 0;
	        	if (!vertexUnDefined)
	        	{	
	        		int[] unWantedIndices = new int[6]; // 4 sides + 2 diagonals
	        		if (isUnWanted(uMin + uCnt * uStep, vMin + vCnt * vStep,
	        					   uMin + uCnt * uStep + uStep, vMin + vCnt * vStep,
	        					   expX, expY, expZ, paramNaamU, paramNaamV))
	        		{	unWantedCnt++;
	        			unWantedIndices[0] = 1;
	        		}
	        		if (isUnWanted(uMin + uCnt * uStep + uStep, vMin + vCnt * vStep,
     					           uMin + uCnt * uStep + uStep, vMin + vCnt * vStep + vStep,
     					           expX, expY, expZ, paramNaamU, paramNaamV))
	        		{	unWantedCnt++;
	        			unWantedIndices[1] = 1;
	        		}
	        		if (isUnWanted(uMin + uCnt * uStep + uStep, vMin + vCnt * vStep + vStep,
     					   		   uMin + uCnt * uStep, vMin + vCnt * vStep + vStep + vStep,
     					   		   expX, expY, expZ, paramNaamU, paramNaamV))
	        		{	unWantedCnt++;
	        			unWantedIndices[2] = 1;
	        		}
	        		if (isUnWanted(uMin + uCnt * uStep, vMin + vCnt * vStep + vStep,
     					   		   uMin + uCnt * uStep, vMin + vCnt * vStep,
     					   		   expX, expY, expZ, paramNaamU, paramNaamV))
	        		{	unWantedCnt++;
	        			unWantedIndices[3] = 1;
	        		}
	        		if (isUnWanted(uMin + uCnt * uStep, vMin + vCnt * vStep,
     					   		   uMin + uCnt * uStep + uStep, vMin + vCnt * vStep + vStep,
     					   		   expX, expY, expZ, paramNaamU, paramNaamV))
	        		{	unWantedCnt++;
	        			unWantedIndices[4] = 1;
	        		}
	        		if (isUnWanted(uMin + uCnt * uStep + uStep, vMin + vCnt * vStep,
     					   		   uMin + uCnt * uStep, vMin + vCnt * vStep + vStep,
     					   		   expX, expY, expZ, paramNaamU, paramNaamV))
	        		{	unWantedCnt++;
	        			unWantedIndices[5] = 1;
	        		}
	        	}	
	       		
	        	
	        	boolean edgeUnWanted = (unWantedCnt > 0);
	       		
	       		if (!vertexUnDefined && !edgeUnWanted)
	       		{
	       			
	       			// creating tempFacet eliminates subsequent identical vertices in tempFacet, see class Facet3D 
	       			Facet3D tempFacet = new Facet3D(vertices, indices, Grafiek3DComponent.surfaceColor);
	       			
	       			if (tempFacet.numPoints == 3)
	       			{	numNonNullFacets++;
		       			tempFacets[facetCount] = tempFacet;
		       			tempFacets[facetCount].outlineColor = Grafiek3DComponent.surfaceOutlineColor;
		       			facetCount++;
	       			}
	       			else if (tempFacet.numPoints > 3)
	       			{	
	       				double distance1 = Vector3D.distance(vertices[indices[0]], vertices[indices[2]]);
	       				double distance2 = Vector3D.distance(vertices[indices[1]], vertices[indices[3]]);
	       			
	       				if (distance1 < distance2)
	       				{	// take 1 and 2 
	       					numNonNullFacets++;
	       					tempFacets[facetCount] = new Facet3D(vertices, indices1, Grafiek3DComponent.surfaceColor);
	       					tempFacets[facetCount].outlineColor = Grafiek3DComponent.surfaceOutlineColor;
			       			// force edge colors, see class Facet3D
	       					tempFacets[facetCount].edgeCodes[0] = 52;
	       					tempFacets[facetCount].edgeCodes[1] = 52;
	       					tempFacets[facetCount].edgeCodes[2] = 51;
	       					facetCount++;
		       			
	       					numNonNullFacets++;
	       					tempFacets[facetCount] = new Facet3D(vertices, indices2, Grafiek3DComponent.surfaceColor);
	       					tempFacets[facetCount].outlineColor = Grafiek3DComponent.surfaceOutlineColor;
			       			// force edge colors, see class Facet3D
	       					tempFacets[facetCount].edgeCodes[0] = 51;
	       					tempFacets[facetCount].edgeCodes[1] = 52;
	       					tempFacets[facetCount].edgeCodes[2] = 52;
	       					facetCount++;
		       			
	       				       				
	       				}
	       				else // distance1 > distance2
	       				{	// take 3 and 4
	       					numNonNullFacets++;
	       					tempFacets[facetCount] = new Facet3D(vertices, indices3, Grafiek3DComponent.surfaceColor);
	       					tempFacets[facetCount].outlineColor = Grafiek3DComponent.surfaceOutlineColor;
			       			// force edge colors, see class Facet3D
	       					tempFacets[facetCount].edgeCodes[0] = 52;
	       					tempFacets[facetCount].edgeCodes[1] = 51;
	       					tempFacets[facetCount].edgeCodes[2] = 52;
	       					facetCount++;
		       			
	       					numNonNullFacets++;
	       					tempFacets[facetCount] = new Facet3D(vertices, indices4, Grafiek3DComponent.surfaceColor);
	       					tempFacets[facetCount].outlineColor = Grafiek3DComponent.surfaceOutlineColor;
			       			// force edge colors, see class Facet3D
	       					tempFacets[facetCount].edgeCodes[0] = 52;
	       					tempFacets[facetCount].edgeCodes[1] = 52;
	       					tempFacets[facetCount].edgeCodes[2] = 51;
	       					facetCount++;
	       				
	       				}
	       			}	
	       			
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
	    
	    // change "undefined" vertices to something innocent	    
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
     * very inefficient method to locate asymptotes: given two points in the u-v-plane
     * evaluate 100 values of expX, expY and expZ along the segment [v1,v2] and 
     * determine their maximum and minimum; if one of the maxima is large positive and
     * corresponding minimum is large negative, there must be an asymptote between v1 and v2   
     * @param u1 u-coordinate first point in u-v-plane 
     * @param v1 v-coordinate first point in u-v-plane
     * @param u2 u-coordinate second point in u-v-plane
     * @param v2 v-coordinate second point in u-v-plane
     * @param expX x-expression
     * @param expY y-expression
     * @param expZ z-expression
     * @param paramNaamU name of u-variable
     * @param paramNaamV name of v-variable
     * @return true/false
     */
    public boolean isUnWanted(double u1, double v1, double u2, double v2, 
    						  Expressie expX, Expressie expY, Expressie expZ, String paramNaamU, String paramNaamV)
    {	
    	
    	if (!checkForAsymptotes)
    	{
    		return false;
    	}    	
    	
    	boolean unWanted = false;
    
    	double[] subst = new double[2];
    	String[] vars = new String[2];
    	vars[0] = paramNaamU;
    	vars[1] = paramNaamV;
    	subst[0] = u1;
		subst[1] = v1;
		double expXWaarde = expX.geefWaarde(subst, vars);
		double expYWaarde = expY.geefWaarde(subst, vars);
		double expZWaarde = expZ.geefWaarde(subst, vars);
    
    	double maxX = expXWaarde;
    	double minX = expXWaarde;
    	double maxY = expYWaarde;
    	double minY = expYWaarde;
    	double maxZ = expZWaarde;
    	double minZ = expZWaarde;
    	
    	if (u2 < u1)
    	{	double temp = u1;
    		u1 = u2;
    		u2 = temp;
    	}
    	if (v2 < v1)
    	{	double temp = v1;
    		v1 = v2;
    		v2 = temp;
    	}
    	
    	int steps = 100;
    	double stepU = (u2 - u1) / steps;
    	double stepV = (v2 - v1) / steps;
    	
    	for (int stepCnt = 1; stepCnt <= steps; stepCnt++)
    	{
    		subst[0] = u1 + stepCnt * stepU;
    		subst[1] = v1 + stepCnt * stepV;
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
     * make a deep copy of this Surface3D
     */
    public Object3D deepCopy()
    {   Surface3D copy = new Surface3D();
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
