package fi.grafiek3dgwt.client;

import fi.grafiek3dgwt.client.expressies.*;

/**
 * class representing a graph in 3-space as an Object3D;
 * note how this is done: the distance between the minimum
 * xMin and the maximum xMax of the x-axis is divided into 
 * (xMax-xMin)/xStep segments (which can be taken smaller); 
 * the same for the y-axis, so that the x-y-plane is divided
 * into squares; calculating function values at the corners
 * of each square, gives vertices of the form (u,v,f(u,v)),
 * which result in square 3d-facets for the graph; 
 * these square facets are not accurate enough to
 * smoothly approximate the graph, so each of them is subdivided
 * into two triangular facets (choice depending on the longest
 * diagonal of the square facet); <br>     
 * note that zMin and zMax are also given so that it can be determined
 * if the graph should be cut at z = zMax or z = zMin; undefined
 * vertices are omitted and not used for making facets; near asymptotes
 * facets should be omitted when one of their edges crosses an
 * asymptote, but the algorithm for detecting this is very inefficient. 
 */

public class Grafiek3D extends Object3D
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
	 * should the top of the graph be trimmed?
	 */
	boolean trimTop = false;
	/**
	 * should the bottom of the graph be trimmed?
	 */
	boolean trimBottom = false;
	/**
	 * the vertex with the largest z value smaller then zMax
	 */
	Vector3D topMaxVertex = null;
	/**
	 * the vertex with the smallest z value larger then zMin
	 */
	Vector3D bottomMinVertex = null;
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
	 * some big positive zValue, see method isUnwanted 
	 */
	double bigPos = 0;
	/**
	 * some big negative zValue, see method isUnwanted
	 */
	double bigMin = 0;
	
	/**
	 * check if the defining function has asymptotes,
	 * see class Grafiek3DComponent 
	 */
	boolean checkForAsymptotes = false;
	
	/**
	 * default constructor
	 */
    public Grafiek3D()
    {}
    /**
     * constructor
     * @param exp function expression
     * @param cfa checking for asymptotes
     * @param xMin minimum x-axis
     * @param xMax maximum x-axis
     * @param xStep step size on x-axis
     * @param yMin minimum y-axis
     * @param yMax maximum y-axis
     * @param yStep step size on y-axis
     * @param zMin minimum z-axis
     * @param zMax maximum z-axis
     * @param zStep step size on z-axis
     * @param varNaamX name x-variable
     * @param varNaamY name y-variable
     * @param xFinerSteps finer factor for x-axis (see class Grafiek3DComponent)
     * @param yFinerSteps finer factor for y-axis (see class Grafiek3DComponent)
     */
    public Grafiek3D(Expressie exp, boolean cfa,
    			double xMin, double xMax, double xStep, 
    		    double yMin, double yMax, double yStep,
    		    double zMin, double zMax, double zStep,
    		    String varNaamX, String varNaamY, int xFinerSteps, int yFinerSteps)
    {
    	
    	checkForAsymptotes = cfa;

		bigPos = 10 * zMax;
		bigMin = 10 * zMin;
		
		Vector3D centerPoint = new Vector3D((xMin + xMax) / 2, (yMin + yMax) / 2, (zMin + zMax) / 2);
		Vector3D cornerPoint = new Vector3D(xMax, yMax, zMax);
		centerDis = Vector3D.distance(centerPoint, cornerPoint) + 1; 
		
		double xStepFine = xStep / xFinerSteps;
		double yStepFine = yStep / yFinerSteps;
		
		// determine the number of facets needed
    	int numXFacets = (int) Math.round((xMax - xMin) / xStepFine);
    	int numYFacets = (int) Math.round((yMax - yMin) / yStepFine);
    	int numGraphFacets = numXFacets * numYFacets;

    	// determine the number of vertices needed
		int numGraphVertices = (numXFacets + 1) * (numYFacets + 1);
    	numVertices = numGraphVertices;
		numVertexLabels = numVertices;
		vertices = new Vector3D[numVertices];
		// do NOT forget this
		trVertices = new Vector3D[numVertices];

		double[] subst = new double[2];
		String[] vars = new String[2];
		vars[0] = varNaamX;
		vars[1] = varNaamY;

		// create the vertices by calculating the function
		// values at the points of the x-y-grid; 
		for (int yCnt = numYFacets; yCnt >= 0; yCnt--)			
			for (int xCnt = numXFacets; xCnt >= 0; xCnt--)
			{	subst[0] = xMin + xCnt * xStepFine;
				subst[1] = yMin + yCnt * yStepFine;
				double expWaarde = exp.geefWaarde(subst, vars);
				
				vertices[xCnt + (numXFacets + 1) * yCnt] = 
					new Vector3D(subst[0], subst[1], expWaarde);
				
				if (!isUnDefined(expWaarde))
				{
					if (expWaarde > (zMax + NZERO))
					{	trimTop = true;
						if ((topMaxVertex == null) || (topMaxVertex.z < (expWaarde - NZERO)))
							topMaxVertex = new Vector3D(vertices[xCnt + (numXFacets + 1) * yCnt]);
					}
				
					if (expWaarde < (zMin - NZERO))
					{	trimBottom = true;
						if ((bottomMinVertex == null) || (bottomMinVertex.z > (expWaarde + NZERO)))
							bottomMinVertex = new Vector3D(vertices[xCnt + (numXFacets + 1) * yCnt]);
					}
					
					if ((expWaarde < (zMax - NZERO)) && (expWaarde > (zMin + NZERO)))
					{
						if ((insideVertex == null) && 
							(centerDis > Vector3D.distance(vertices[xCnt + (numXFacets + 1) * yCnt], centerPoint))
						   )	
						{	insideVertex = new Vector3D(vertices[xCnt + (numXFacets + 1) * yCnt]);
							centerDis = Vector3D.distance(insideVertex, centerPoint);
						}
						else if ((insideVertex != null) && 
								 (centerDis > Vector3D.distance(vertices[xCnt + (numXFacets + 1) * yCnt], centerPoint))
							    )  
						{	insideVertex = new Vector3D(vertices[xCnt + (numXFacets + 1) * yCnt]);
							centerDis = Vector3D.distance(insideVertex, centerPoint);
						}
					}
				}
					
			}
		
		// maximum number (each square facet divided in to 2 triangles
	    int tempNumFacets = 2 * numGraphFacets;
	    int numNonNullFacets = 0;
	    Facet3D[] tempFacets = new Facet3D[tempNumFacets];
	    int facetCount = 0;
	        
	    // for each square facet check if one or more vertices are undefined
	    // and create one triangle if one vertex is undefined and two triangles if
	    // all 4 vertices are defined 
	    for (int yCnt = numYFacets - 1; yCnt >= 0; yCnt--)
	    	for (int xCnt = numXFacets - 1; xCnt >= 0; xCnt--)
	       	{	// indices of the 4 vertices
	     		int[] indices = new int[4];
	       		indices[0] = xCnt + (numXFacets + 1) * yCnt;
	       		indices[1] = xCnt + 1 + (numXFacets + 1) * yCnt;
	       		indices[2] = xCnt + 1 + (numXFacets + 1) * (yCnt + 1);
	       		indices[3] = xCnt + (numXFacets + 1) * (yCnt + 1);	

	       		// 4 possible triangles
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
	       		
	       		// check which of the 4 vertices in undefined
	       		int unDefinedCnt = 0;
	       		int unDefinedIndex = -1;
	       		if (isUnDefined(vertices[indices[0]].z))
	       		{	unDefinedCnt++;
	       			unDefinedIndex = 0;
	       		}
	       		if (isUnDefined(vertices[indices[1]].z))
	       		{	unDefinedCnt++;
	       			unDefinedIndex = 1;
	       		}
	       		if (isUnDefined(vertices[indices[2]].z))
	       		{	unDefinedCnt++;
	       			unDefinedIndex = 2;
	       		}
	       		if (isUnDefined(vertices[indices[3]].z))
	       		{	unDefinedCnt++;
	       			unDefinedIndex = 3;
	       		}
	       		
	       		// only one undefined, create a triangle out of the other 3
	       		if (unDefinedCnt == 1)
	       		{
	       			if (unDefinedIndex == 0)
	       			{	
//CHECK UNWANTED	       				
	       				numNonNullFacets++;
	       				tempFacets[facetCount] = new Facet3D(vertices, indices4, Grafiek3DComponent.graphColor);
	       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
	       				facetCount++;
	       				
	       			}
	       			if (unDefinedIndex == 1)
	       			{
//CHECK UNWANTED	       				
	       				numNonNullFacets++;
	       				tempFacets[facetCount] = new Facet3D(vertices, indices2, Grafiek3DComponent.graphColor);
	       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
	       				facetCount++;
	       			}
	       			if (unDefinedIndex == 2)
	       			{
//CHECK UNWANTED	       				
	       				numNonNullFacets++;
	       				tempFacets[facetCount] = new Facet3D(vertices, indices3, Grafiek3DComponent.graphColor);
	       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
	       				facetCount++;
	       			}
	       			if (unDefinedIndex == 3)
	       			{
//CHECK UNWANTED	       				
	       				numNonNullFacets++;
	       				tempFacets[facetCount] = new Facet3D(vertices, indices1, Grafiek3DComponent.graphColor);
	       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
	       				facetCount++;
	       			}
	       		}
	       		
	       		boolean vertexUnDefined = (unDefinedCnt > 0);

				int unWantedCnt = 0;
				// all 4 vertices are defined 
	        	if (!vertexUnDefined)
	        	{	
	        		int[] unWantedIndices = new int[6]; // 4 sides + 2 diagonals
	        		// check for asymptotes on the 6 possible sides 
	        		if (isUnWanted(vertices[indices[0]], vertices[indices[1]], exp, varNaamX, varNaamY))
	        		{	unWantedCnt++;
	        			unWantedIndices[0] = 1;
	        		}
	        		if (isUnWanted(vertices[indices[1]], vertices[indices[2]], exp, varNaamX, varNaamY))
	        		{	unWantedCnt++;
	        			unWantedIndices[1] = 1;
	        		}
	        		if (isUnWanted(vertices[indices[2]], vertices[indices[3]], exp, varNaamX, varNaamY))
	        		{	unWantedCnt++;
	        			unWantedIndices[2] = 1;
	        		}
	        		if (isUnWanted(vertices[indices[3]], vertices[indices[0]], exp, varNaamX, varNaamY))
	        		{	unWantedCnt++;
	        			unWantedIndices[3] = 1;
	        		}
	        		if (isUnWanted(vertices[indices[0]], vertices[indices[2]], exp, varNaamX, varNaamY))
	        		{	unWantedCnt++;
	        			unWantedIndices[4] = 1;
	        		}
	        		if (isUnWanted(vertices[indices[1]], vertices[indices[3]], exp, varNaamX, varNaamY))
	        		{	unWantedCnt++;
	        			unWantedIndices[5] = 1;
	        		}

	        		if (unWantedCnt == 1)
	        		{
//CHECK UNWANTED	        			
	        		}
	        		else if (unWantedCnt == 2)
	        		{
	        			if ((unWantedIndices[0] == 1) && (unWantedIndices[1] == 1))
	        			{	// take triangle  0,2,3 (indices2)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices2, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				facetCount++;
	        				
	        			}
	        			if ((unWantedIndices[1] == 1) && (unWantedIndices[2] == 1))
	        			{	// take triangle  0,1,3 (indices3)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices3, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				facetCount++;
	        				
	        			}
	        			if ((unWantedIndices[2] == 1) && (unWantedIndices[3] == 1))
	        			{   // take triangle  0,1,2 (indices1)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices1, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				facetCount++;
	        				
	        			}
	        			if ((unWantedIndices[3] == 1) && (unWantedIndices[0] == 1))
	        			{	// take triangle  1,2,3 (indices4)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices4, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				facetCount++;
	        				
	        			}
//HERE MORE?	        			
	        			
	        		}
	        		else if (unWantedCnt == 3)
	        		{
	        			if ((unWantedIndices[0] == 1) && (unWantedIndices[1] == 1) && (unWantedIndices[5] == 1))
	        			{	// take triangle  0,2,3 (indices2)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices2, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				facetCount++;
	        				
	        			}
	        			if ((unWantedIndices[1] == 1) && (unWantedIndices[2] == 1) && (unWantedIndices[4] == 1))
	        			{	// take triangle  0,1,3 (indices3)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices3, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				facetCount++;
	        				
	        			}
	        			if ((unWantedIndices[2] == 1) && (unWantedIndices[3] == 1) && (unWantedIndices[5] == 1))
	        			{   // take triangle 0,1,2 (indices1)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices1, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				facetCount++;
	        				
	        			}
	        			if ((unWantedIndices[3] == 1) && (unWantedIndices[0] == 1) && (unWantedIndices[4] == 1))
	        			{	// take triangle 1,2,3 (indices4)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices4, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				facetCount++;
	        				
	        			}
	        			
	        			
	        		}

	        		
	        	}
	        	boolean edgeUnWanted = (unWantedCnt > 0);
	        	// no problems
	       		if (!vertexUnDefined && !edgeUnWanted)
	       		{
	       			
	       			double distance1 = Vector3D.distance(vertices[indices[0]], vertices[indices[2]]);
	       			double distance2 = Vector3D.distance(vertices[indices[1]], vertices[indices[3]]);
	       			
	       			if (distance1 < distance2)
	       			{	// take 1 and 2 
		       			numNonNullFacets++;
		       			tempFacets[facetCount] = new Facet3D(vertices, indices1, Grafiek3DComponent.graphColor);
		       			tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       			// force edge colors, see class Facet3D
		       			tempFacets[facetCount].edgeCodes[0] = 50;
		       			tempFacets[facetCount].edgeCodes[1] = 50;
		       			tempFacets[facetCount].edgeCodes[2] = 51;
		       			facetCount++;
		       			
		       			numNonNullFacets++;
		       			tempFacets[facetCount] = new Facet3D(vertices, indices2, Grafiek3DComponent.graphColor);
		       			tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       			// force edge colors, see class Facet3D
		       			tempFacets[facetCount].edgeCodes[0] = 51;
		       			tempFacets[facetCount].edgeCodes[1] = 50;
		       			tempFacets[facetCount].edgeCodes[2] = 50;
		       			facetCount++;
		       			
	       				
	       				
	       			}
	       			else // distance1 > distance2
	       			{	// take 3 and 4
		       			numNonNullFacets++;
		       			tempFacets[facetCount] = new Facet3D(vertices, indices3, Grafiek3DComponent.graphColor);
		       			tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       			// force edge colors, see class Facet3D
		       			tempFacets[facetCount].edgeCodes[0] = 50;
		       			tempFacets[facetCount].edgeCodes[1] = 51;
		       			tempFacets[facetCount].edgeCodes[2] = 50;

		       			facetCount++;
		       			
		       			numNonNullFacets++;
		       			tempFacets[facetCount] = new Facet3D(vertices, indices4, Grafiek3DComponent.graphColor);
		       			tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       			// force edge colors, see class Facet3D
		       			tempFacets[facetCount].edgeCodes[0] = 50;
		       			tempFacets[facetCount].edgeCodes[1] = 50;
		       			tempFacets[facetCount].edgeCodes[2] = 51;

		       			facetCount++;
	       				
	       			}
	       		}
	        }


	    // remove null-facets and adapt numFacets
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
        {	if (isUnDefined(vertices[vCnt].z))
        		vertices[vCnt] = new Vector3D(vertices[vCnt].x, vertices[vCnt].y, (zMin + zMax) / 2);
        }
	    
    	
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] = "";

        // find the center !!
        Vector3D center = new Vector3D((xMin + xMax) / 2, (yMin + yMax) / 2, (zMin + zMax) / 2);
        //initObject3D(true, false);
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
     * very inefficient method to locate asymptotes: given two points in the x-y-plane
     * evaluate 100 function values along the segment [v1,v2] and determine their 
     * maximum and minimum; if the maximum is large positive and the minimum is
     * large negative, there must be an asymptote between v1 and v2   
     * @param v1 first point in x-y-plane
     * @param v2 second point in x-y-plane
     * @param exp the function expression
     * @param varNaamX name of x-variable
     * @param varNaamY name of y-variable
     * @return true/false
     */
    public boolean isUnWanted(Vector3D v1, Vector3D v2, Expressie exp, String varNaamX, String varNaamY)
    {	
    	if (!checkForAsymptotes)
    	{
    		return false;
    	}	
    	
    	boolean unWanted = false;
    
    	double[] subst = new double[2];
    	String[] vars = new String[2];
    	vars[0] = varNaamX;
    	vars[1] = varNaamY;
    	subst[0] = v1.x;
		subst[1] = v1.y;
		double expWaarde = exp.geefWaarde(subst, vars);
    
    	double max = expWaarde;
    	double min = expWaarde;
    	int steps = 100;
    	double stepX = (v2.x - v1.x) / steps;
    	double stepY = (v2.y - v1.y) / steps;
    	for (int stepCnt = 1; stepCnt <= steps; stepCnt++)
    	{
    		subst[0] = v1.x + stepCnt * stepX;
    		subst[1] = v1.y + stepCnt * stepY;
    		expWaarde = exp.geefWaarde(subst, vars);
    		
    		if (!Double.isNaN(expWaarde) && !Double.isInfinite(expWaarde))
    		{
    			max = Math.max(max, expWaarde);
    			min = Math.min(min, expWaarde);
    		}
    		
    	}

    	
    	if ((max > bigPos) && (min < bigMin))
    	{	unWanted = true;
    	}
    
    	return unWanted;	
    }
    
    /**
     * make a deep copy of this Grafiek3D
     */
    public Object3D deepCopy()
    {   Grafiek3D copy = new Grafiek3D();
        makeDeepObjectCopy(copy);
        copy.trimTop = trimTop;
        copy.trimBottom = trimBottom;
        copy.topMaxVertex = Vector3D.copyVector3D(topMaxVertex);
        copy.bottomMinVertex = Vector3D.copyVector3D(bottomMinVertex);
        copy.insideVertex = Vector3D.copyVector3D(insideVertex);
        return copy;        
    }   
    
}
