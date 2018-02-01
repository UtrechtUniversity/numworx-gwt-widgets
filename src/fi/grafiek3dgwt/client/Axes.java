package fi.grafiek3dgwt.client;


/**
 * class representing (different kinds of axes) as an Object3D;
 * note that, instead of the traditional x- and y-axis, the
 * x-y-plane can be shown as a grid (a "floor"), but this is not used;
 * for the label codes see class Facet3D
 * @author huub
 */
public class Axes extends Object3D
{
	/**
	 * a very small double
	 */
	final double NZERO = 1e-5d;
	
	/**
	 * axes labels
	 */
	String[] vLabels; 
	
	/**
	 * default floor type
	 */
	int floorType = Grafiek3DComponent.NOFLOOR;
	
    public Axes()
    {}
    
    /**
     * constructor
     * @param xMin minimum of x-axis
     * @param xMax maximum of x-axis
     * @param xStep step size on x-axis
     * @param yMin minimum of y-axis
     * @param yMax maximum of y-axis
     * @param yStep step size on y-axis
     * @param zMin minimum of z-axis
     * @param zMax maximum of z-axis
     * @param zStep step size on z-axis
     * @param floorType type of floor
     * @param labelType type of labels
     * @param xFinerSteps x refinement
     * @param yFinerSteps y refinement
     */
    public Axes(double xMin, double xMax, double xStep, 
    		    double yMin, double yMax, double yStep,
    		    double zMin, double zMax, double zStep,
    		    int floorType, int labelType, int xFinerSteps, int yFinerSteps)
    {
    	this.floorType = floorType;
    	
    	// determine position of axis relative to the other two axes
		double xAsyPos = 0;
		double xAszPos = 0;
		double yAsxPos = 0;
		double yAszPos = 0;
		double zAsxPos = 0;
		double zAsyPos = 0;
		
		if (xMin > NZERO)
		{	yAsxPos = xMin;
			zAsxPos = xMin;
		}
		if (xMax < -NZERO)
		{	yAsxPos = xMax;
			zAsxPos = xMax;
		}
		if (yMin > NZERO)
		{	xAsyPos = yMin;
			zAsyPos = yMin;
		}
		if (yMax < -NZERO)
		{	xAsyPos = yMax;
			zAsyPos = yMax;
		}
		if (zMin > NZERO)
		{	xAszPos = zMin;
			yAszPos = zMin;
		}
		if (zMax < -NZERO)
		{	xAszPos = zMax;
			yAszPos = zMax;
		}
		
		double xStepFine = xStep / xFinerSteps;
		double yStepFine = yStep / yFinerSteps;
		
    	int numXFacets = (int) Math.round((xMax - xMin) / xStep);
    	int numXFineFacets = (int) Math.round((xMax - xMin) / xStepFine);
    	int numYFacets = (int) Math.round((yMax - yMin) / yStep);
    	int numYFineFacets = (int) Math.round((yMax - yMin) / yStepFine);
    	int numZFacets = (int) Math.round((zMax - zMin) / zStep);
    	    	
    	int numPointFacets = 2 * xFinerSteps + 2 * yFinerSteps;
    	
    	int numFloorFacets = (numXFineFacets - 2 * xFinerSteps) * (numYFineFacets - 2 * yFinerSteps + 1) +
    						 (numXFineFacets - 2 * xFinerSteps + 1) * (numYFineFacets - 2 * yFinerSteps);
    	
    	if (floorType == Grafiek3DComponent.NOFLOOR)
    	{	
    		numVertices = numXFineFacets + 1 + numYFineFacets + 1 + numZFacets + 1;
    		numVertexLabels = numVertices;
    		vertices = new Vector3D[numVertices];
    		// do NOT forget this
    		trVertices = new Vector3D[numVertices];
    		vLabels = new String[numVertices];
        
    		for (int xCnt = 0; xCnt < (numXFineFacets + 1); xCnt++)
    		{
    			vertices[xCnt] = new Vector3D(xMin + xCnt * xStepFine, xAsyPos, xAszPos);
    			if (labelType == Grafiek3DComponent.ALLLABELS)
    			{	if ((xCnt == 0) || (xCnt == numXFineFacets))
    					vLabels[xCnt] = "x = " + format(xMin + xCnt * xStepFine);
        			else if ((xCnt % xFinerSteps) == 0)
        				vLabels[xCnt] = format(xMin + xCnt * xStepFine);
        			else
        				vLabels[xCnt] = "";
    			}
    			else if (labelType == Grafiek3DComponent.ENDLABELS)
    			{	if ((xCnt == 0) || (xCnt == numXFineFacets))
    					vLabels[xCnt] = "x = " + format(xMin + xCnt * xStepFine);
        			else if ((xCnt % xFinerSteps) == 0)
        				vLabels[xCnt] = "XX"; 
        			else
        				vLabels[xCnt] = "";
    			}
    			else if (labelType == Grafiek3DComponent.NOLABELS)
    			{	if ((xCnt % xFinerSteps) == 0)
    					vLabels[xCnt] = "XX";
        			else
        				vLabels[xCnt] = "";
    			}
    		}
    		for (int yCnt = 0; yCnt < (numYFineFacets + 1); yCnt++)
    		{
    			vertices[numXFineFacets + 1 + yCnt] = new Vector3D(yAsxPos, yMin + yCnt * yStepFine, yAszPos);
    			if (labelType == Grafiek3DComponent.ALLLABELS)
    			{	if ((yCnt == 0) || (yCnt == numYFineFacets))
    					vLabels[numXFineFacets + 1 + yCnt] = "y = " + format(yMin + yCnt * yStepFine);
        			else if ((yCnt % yFinerSteps) == 0)
        				vLabels[numXFineFacets + 1 + yCnt] = format(yMin + yCnt * yStepFine);
    				else
    					vLabels[yCnt] = "";
    			}
    			else if (labelType == Grafiek3DComponent.ENDLABELS)
    			{	if ((yCnt == 0) || (yCnt == numYFineFacets))
        				vLabels[numXFineFacets + 1 + yCnt] = "y = " + format(yMin + yCnt * yStepFine);
        			else if ((yCnt % yFinerSteps) == 0)
        				vLabels[numXFineFacets + 1 + yCnt] = "XX";
					else
						vLabels[yCnt] = "";

    			}
    			else if (labelType == Grafiek3DComponent.NOLABELS)
    			{	if ((yCnt % yFinerSteps) == 0)
    					vLabels[numXFineFacets + 1 + yCnt] = "XX";
    				else
    					vLabels[numXFineFacets + 1 + yCnt] = "";
    			}
        	
    		}
    		for (int zCnt = 0; zCnt < (numZFacets + 1); zCnt++)
    		{
    			vertices[numXFineFacets + 1 + numYFineFacets + 1 + zCnt] = new Vector3D(zAsxPos, zAsyPos, zMin + zCnt * zStep);
    			if (labelType == Grafiek3DComponent.ALLLABELS)
    			{	if ((zCnt == 0) || (zCnt == numZFacets))
    					vLabels[numXFineFacets + 1 + numYFineFacets + 1 + zCnt] = "z = " + format(zMin + zCnt * zStep);
        			else
        				vLabels[numXFineFacets + 1 + numYFineFacets + 1 + zCnt] = format(zMin + zCnt * zStep);
    			}	
    			else if (labelType == Grafiek3DComponent.ENDLABELS)
    			{	if ((zCnt == 0) || (zCnt == numZFacets))
    					vLabels[numXFineFacets + 1 + numYFineFacets + 1 + zCnt] = "z = " + format(zMin + zCnt * zStep);
        			else
        				vLabels[numXFineFacets + 1 + numYFineFacets + 1 + zCnt] = "XX"; 
    			}
    			else if (labelType == Grafiek3DComponent.NOLABELS)
    				vLabels[numXFineFacets + 1 + numYFineFacets + 1 + zCnt] = "XX";
        	
    		}
    	
    		numFacets = numXFineFacets + numYFineFacets + numZFacets;
    		facets = new Facet3D[numFacets];
        
    		for (int xCnt = 0; xCnt < numXFineFacets; xCnt++)
    		{	int[] indices = new int[2];
        		indices[0] = xCnt;
        		indices[1] = xCnt + 1;
        		facets[xCnt] = new Facet3D(vertices, indices, Grafiek3DComponent.axesColor);
        		facets[xCnt].isOnAxis = true;

    		}

    		for (int yCnt = 0; yCnt < numYFineFacets; yCnt++)
    		{	int[] indices = new int[2];
        		indices[0] = numXFineFacets + 1 + yCnt;
        		indices[1] = numXFineFacets + 1 + yCnt + 1;
        		facets[numXFineFacets + yCnt] = new Facet3D(vertices, indices, Grafiek3DComponent.axesColor);
        		facets[numXFineFacets + yCnt].isOnAxis = true;

    		}
        
    		for (int zCnt = 0; zCnt < numZFacets; zCnt++)
    		{	int[] indices = new int[2];
        		indices[0] = numXFineFacets + 1 + numYFineFacets + 1 + zCnt;
        		indices[1] = numXFineFacets + 1 + numYFineFacets + 1 + zCnt + 1;
        		facets[numXFineFacets + numYFineFacets + zCnt] = new Facet3D(vertices, indices, Grafiek3DComponent.axesColor);
        		facets[numXFineFacets + numYFineFacets + zCnt].isOnAxis = true;

    		}
        
    	} // NOFLOOR
    	else // FLOOR
    	{	
    		int numPointVertices = 2 * (xFinerSteps + 1) + 2 * (yFinerSteps + 1);
    		
    		int numFloorVertices = (numXFineFacets - 2 * xFinerSteps + 1) * (numYFineFacets - 2 * yFinerSteps + 1);	
    		
    		numVertices = numPointVertices + numFloorVertices + numZFacets + 1;
			numVertexLabels = numVertices;
			vertices = new Vector3D[numVertices];
			// do NOT forget this
			trVertices = new Vector3D[numVertices];
			vLabels = new String[numVertices];
			
			// points
			for (int xMinCnt = 0; xMinCnt < (xFinerSteps + 1); xMinCnt++)
			{	
				vertices[xMinCnt] = new Vector3D(xMin + xMinCnt * xStepFine, (yMin + yMax) / 2, xAszPos);
				if (labelType == Grafiek3DComponent.ALLLABELS)
				{	if (xMinCnt == 0)
						vLabels[xMinCnt] = "F" + "x = " + format(xMin + xMinCnt * xStepFine);
					else
						vLabels[xMinCnt] = "";
				}
				else if (labelType == Grafiek3DComponent.ENDLABELS)
				{	if (xMinCnt == 0)
						vLabels[xMinCnt] = "F" + "x = " + format(xMin + xMinCnt * xStepFine);
					else
						vLabels[xMinCnt] = "";
				}
				else if (labelType == Grafiek3DComponent.NOLABELS)
					vLabels[xMinCnt] = "";
			}	

			for (int xMaxCnt = 0; xMaxCnt < (xFinerSteps + 1); xMaxCnt++)
			{	
				vertices[(xFinerSteps + 1) + xMaxCnt] = new Vector3D(xMax - xStep + xMaxCnt * xStepFine, (yMin + yMax) / 2, xAszPos);
				if (labelType == Grafiek3DComponent.ALLLABELS)
				{	if (xMaxCnt == xFinerSteps)
						vLabels[(xFinerSteps + 1) + xMaxCnt] = "F" + "x = " + format(xMax - xStep + xMaxCnt * xStepFine);
					else
						vLabels[(xFinerSteps + 1) + xMaxCnt] = "";
				}
				else if (labelType == Grafiek3DComponent.ENDLABELS)
				{	if (xMaxCnt == xFinerSteps)
						vLabels[(xFinerSteps + 1) + xMaxCnt] = "F" + "x = " + format(xMax - xStep + xMaxCnt * xStepFine);
					else
						vLabels[(xFinerSteps + 1) + xMaxCnt] = "";
				}
				else if (labelType == Grafiek3DComponent.NOLABELS)
					vLabels[(xFinerSteps + 1) + xMaxCnt] = "";
			}	

			for (int yMinCnt = 0; yMinCnt < (yFinerSteps + 1); yMinCnt++)
			{	
				vertices[2 * (xFinerSteps + 1) + yMinCnt] = new Vector3D((xMin + xMax) / 2, yMin + yMinCnt * yStepFine, yAszPos);
				if (labelType == Grafiek3DComponent.ALLLABELS)
				{	if (yMinCnt == 0)
						vLabels[2 * (xFinerSteps + 1) + yMinCnt] = "F" + "y = " + format(yMin + yMinCnt * yStepFine);
					else
						vLabels[2 * (xFinerSteps + 1) + yMinCnt] = "";
				}
				else if (labelType == Grafiek3DComponent.ENDLABELS)
				{	if (yMinCnt == 0)
						vLabels[2 * (xFinerSteps + 1) + yMinCnt] = "F" + "y = " + format(yMin + yMinCnt * yStepFine);
					else
						vLabels[2 * (xFinerSteps + 1) + yMinCnt] = "";
				}
				else if (labelType == Grafiek3DComponent.NOLABELS)
					vLabels[2 * (xFinerSteps + 1) + yMinCnt] = "";
			}	

			for (int yMaxCnt = 0; yMaxCnt < (yFinerSteps + 1); yMaxCnt++)
			{	vertices[2 * (xFinerSteps + 1) + (yFinerSteps + 1) + yMaxCnt] = 
					new Vector3D((xMin + xMax) / 2, yMax - yStep + yMaxCnt * yStepFine, yAszPos);
				if (labelType == Grafiek3DComponent.ALLLABELS)
				{	if (yMaxCnt == yFinerSteps)
						vLabels[2 * (xFinerSteps + 1) + (yFinerSteps + 1) + yMaxCnt] = 
							"F" + "y = " + format(yMax - yStep + yMaxCnt * yStepFine);
					else
						vLabels[2 * (xFinerSteps + 1) + (yFinerSteps + 1) + yMaxCnt] = "";
				}
				else if (labelType == Grafiek3DComponent.ENDLABELS)
				{	if (yMaxCnt == yFinerSteps)
						vLabels[2 * (xFinerSteps + 1) + (yFinerSteps + 1) + yMaxCnt] = 
							"F" + "y = " + format(yMax - yStep + yMaxCnt * yStepFine);
					else
						vLabels[2 * (xFinerSteps + 1) + (yFinerSteps + 1) + yMaxCnt] = "";
				}
				else if (labelType == Grafiek3DComponent.NOLABELS)
					vLabels[2 * (xFinerSteps + 1) + (yFinerSteps + 1) + yMaxCnt] = "";
			}	
			

			for (int yCnt = yFinerSteps; yCnt < (numYFineFacets - yFinerSteps + 1); yCnt++)			
				for (int xCnt = xFinerSteps; xCnt < (numXFineFacets - xFinerSteps + 1); xCnt++)
				{	vertices[numPointVertices + (xCnt - xFinerSteps) + (numXFineFacets - 2 * xFinerSteps + 1) * (yCnt - yFinerSteps)] = 
						new Vector3D(xMin + xCnt * xStepFine, yMin + yCnt * yStepFine, xAszPos);
					
					if (labelType == Grafiek3DComponent.ALLLABELS)
					{	vLabels[numPointVertices + (xCnt - xFinerSteps) + (numXFineFacets - 2 * xFinerSteps + 1) * (yCnt - yFinerSteps)] = "";
					}
					else if (labelType == Grafiek3DComponent.ENDLABELS)
					{	vLabels[numPointVertices + (xCnt - xFinerSteps) + (numXFineFacets - 2 * xFinerSteps + 1) * (yCnt - yFinerSteps)] = "";
					}
		        	else if (labelType == Grafiek3DComponent.NOLABELS)
		        		vLabels[numPointVertices + (xCnt - xFinerSteps) + (numXFineFacets - 2 * xFinerSteps + 1) * (yCnt - yFinerSteps)] = "";
					
				}
			
	        for (int zCnt = 0; zCnt < (numZFacets + 1); zCnt++)
	        {
	        	vertices[numPointVertices + numFloorVertices + zCnt] = new Vector3D(zAsxPos, zAsyPos, zMin + zCnt * zStep);
	        	if (labelType == Grafiek3DComponent.ALLLABELS)
	        	{	if ((zCnt == 0) || (zCnt == numZFacets))
        				vLabels[numPointVertices + numFloorVertices + zCnt] = "z = " + format(zMin + zCnt * zStep);
	        		else
	        			vLabels[numPointVertices + numFloorVertices + zCnt] = format(zMin + zCnt * zStep);
	        	}	
	        	else if (labelType == Grafiek3DComponent.ENDLABELS)
	        	{	if ((zCnt == 0) || (zCnt == numZFacets))
	        			vLabels[numPointVertices + numFloorVertices + zCnt] = "z = " + format(zMin + zCnt * zStep);
	        		else
	        			vLabels[numPointVertices + numFloorVertices + zCnt] = "XX"; 
	        	}
	        	else if (labelType == Grafiek3DComponent.NOLABELS)
	        		vLabels[numPointVertices + numFloorVertices + zCnt] = "XX";
	        }

	        numFacets = numPointFacets + numFloorFacets + numZFacets;
	        facets = new Facet3D[numFacets];
	        int[] pIndices = new int[2];
	        for (int xMinCnt = 0; xMinCnt < xFinerSteps; xMinCnt++)
	        {	pIndices = new int[2];
	        	pIndices[0] = xMinCnt;
	        	pIndices[1] = xMinCnt + 1;
	        	facets[xMinCnt] = new Facet3D(vertices, pIndices, Grafiek3DComponent.floorOutlineColor);
		        facets[xMinCnt].isOnAxis = true;
	        }
	        for (int xMaxCnt = 0; xMaxCnt < xFinerSteps; xMaxCnt++)
	        {	pIndices = new int[2];
	        	pIndices[0] = (xFinerSteps + 1) + xMaxCnt;
	        	pIndices[1] = (xFinerSteps + 1) + xMaxCnt + 1;
	        	facets[xFinerSteps + xMaxCnt] = new Facet3D(vertices, pIndices, Grafiek3DComponent.floorOutlineColor);
		        facets[xFinerSteps + xMaxCnt].isOnAxis = true;
	        }
	        for (int yMinCnt = 0; yMinCnt < yFinerSteps; yMinCnt++)
	        {	pIndices = new int[2];
	        	pIndices[0] = 2 * (xFinerSteps + 1) + yMinCnt;
	        	pIndices[1] = 2 * (xFinerSteps + 1) + yMinCnt + 1;
	        	facets[2 * xFinerSteps + yMinCnt] = new Facet3D(vertices, pIndices, Grafiek3DComponent.floorOutlineColor);
		        facets[2 * xFinerSteps + yMinCnt].isOnAxis = true;
	        }
	        for (int yMaxCnt = 0; yMaxCnt < yFinerSteps; yMaxCnt++)
	        {	pIndices = new int[2];
	        	pIndices[0] = 2 * (xFinerSteps + 1) + (yFinerSteps + 1) + yMaxCnt;
	        	pIndices[1] = 2 * (xFinerSteps + 1) + (yFinerSteps + 1) + yMaxCnt + 1;
	        	facets[2 * xFinerSteps + yFinerSteps + yMaxCnt] = new Facet3D(vertices, pIndices, Grafiek3DComponent.floorOutlineColor);
		        facets[2 * xFinerSteps + yFinerSteps + yMaxCnt].isOnAxis = true;
	        }
	        
	        // "horizontals" of floor
	        for (int yCnt = yFinerSteps; yCnt < (numYFineFacets - yFinerSteps + 1); yCnt++)
	        	for (int xCnt = xFinerSteps; xCnt < (numXFineFacets - xFinerSteps); xCnt++)
	        	{	int[] indicesH = new int[2];
	        		indicesH[0] = numPointVertices + (xCnt - xFinerSteps) + 
	        					  (numXFineFacets - 2 * xFinerSteps + 1) * (yCnt - yFinerSteps);
	        		indicesH[1] = numPointVertices + (xCnt - xFinerSteps + 1) + 
	        					  (numXFineFacets - 2 * xFinerSteps + 1) * (yCnt - yFinerSteps);
	        		int facetIndex = numPointFacets + (xCnt - xFinerSteps) + (numXFineFacets - 2 * xFinerSteps) * (yCnt - yFinerSteps);
	        		facets[facetIndex] = new Facet3D(vertices, indicesH, Grafiek3DComponent.floorColor);
	        		if (yCnt % yFinerSteps == 0)
	        			facets[facetIndex].outlineColor = Grafiek3DComponent.floorOutlineColor;
	        		else
	        		{	facets[facetIndex].visible = false;
	        		}
	        			
	        		if (floorType == Grafiek3DComponent.TRANSFLOOR)
	        		{	facets[facetIndex].filled = false;
	        		}
	        }

	        
	        // "verticals" of floor
	        for (int xCnt = xFinerSteps; xCnt < (numXFineFacets - xFinerSteps + 1); xCnt++)
	        	for (int yCnt = yFinerSteps; yCnt < (numYFineFacets - yFinerSteps); yCnt++)
	        	{	int[] indicesV = new int[2];
	        		indicesV[0] = numPointVertices + (yCnt - yFinerSteps) * (numXFineFacets - 2 * xFinerSteps + 1) + 
	        										 (xCnt - xFinerSteps);
	        		indicesV[1] = numPointVertices + (yCnt - yFinerSteps + 1) * (numXFineFacets - 2 * xFinerSteps + 1) + 
	        										 (xCnt - xFinerSteps);
	        		int facetIndex = numPointFacets + (numXFineFacets - 2 * xFinerSteps) * (numYFineFacets - 2 * yFinerSteps + 1) +
     		       					 (yCnt - yFinerSteps) + (numYFineFacets - 2 * yFinerSteps) * (xCnt - xFinerSteps);
	        		
	        		facets[facetIndex] = new Facet3D(vertices, indicesV, Grafiek3DComponent.floorColor);
	        		if (xCnt % xFinerSteps == 0)
	        			facets[facetIndex].outlineColor = Grafiek3DComponent.floorOutlineColor;
	        		else
	        		{	facets[facetIndex].visible = false;
	        		}
	        		
	        		if (floorType == Grafiek3DComponent.TRANSFLOOR)
	        		{	facets[facetIndex].filled = false;
	        		}
	        }
	        
	        
	        for (int zCnt = 0; zCnt < numZFacets; zCnt++)
	        {	int[] indices = new int[2];
	        	indices[0] = numPointVertices + numFloorVertices + zCnt;
	        	indices[1] = numPointVertices + numFloorVertices + zCnt + 1;
	        	facets[numPointFacets + numFloorFacets + zCnt] = new Facet3D(vertices, indices, Grafiek3DComponent.axesColor);
	        	facets[numPointFacets + numFloorFacets + zCnt].isOnAxis = true;
	        }
	        
    	} // FLOOR
        
    	
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];


        setLetters(true);

        // find the center !!
        Vector3D center = new Vector3D((xMin + xMax) / 2, (yMin + yMax) / 2, (zMin + zMax) / 2);
        Vector3D corner = new Vector3D(xMax, yMax, zMax);
        double diam = Vector3D.distance(corner, center);
        initObject3D(true, center, diam, false);
                
    	
    }

    /**
     * trim trailing zeros of a String representing a fraction
     * @param s String representing a fraction 
     * @param decSep decimal separator
     * @return trimmed String
     */
	public String trimTrailingZeros(String s, char decSep)
	{	String txt = new String(s);
		if (txt.indexOf(decSep) < 0)
			return txt;
		char c = txt.charAt(txt.length() - 1);
		while (c == '0')
		{	txt = removeCharAt(txt, txt.length() - 1);
			c = txt.charAt(txt.length() - 1);
		}	
		c = txt.charAt(txt.length() - 1);
		if (c == decSep)
			txt = removeCharAt(txt, txt.length() - 1);
		return txt;		
	}				
    
	/**
	 * remove the character at a given index from a String
	 * @param s String from which character should be removed
	 * @param index index of character to be removed
	 * @return modified String
	 */
	public String removeCharAt(String s, int index)
	{	String txt = new String(s);
		// first
		if (index == 0)
			txt = txt.substring(1);
		// last	
		else if (index == (txt.length() - 1))
			txt = txt.substring(0, txt.length() - 1);
		// somewhere in the middle	
		else
		{	String txt1 = txt.substring(0, index);
			String txt2 = txt.substring(index + 1);
			txt = txt1 + txt2;
		}
		return txt;
	}		
    
	/**
	 * format a double with a maximum of 4 decimals
	 * @param d double to be formatted
	 * @return formatted double (a String)
	 */
    public String format(double d)
    {	
    	String result = UF.format(d, 4);
    	result = trimTrailingZeros(result, ',');
    	return result;
    }
    
    /**
     * make a deep copy of this Axes Object3D
     */
    public Object3D deepCopy()
    {   Axes copy = new Axes();
        makeDeepObjectCopy(copy);
        return copy;        
    }   
    
}
