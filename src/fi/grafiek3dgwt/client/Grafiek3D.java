package fi.grafiek3dgwt.client;

//import java.awt.Color;
import fi.grafiek3dgwt.client.expressies.*;


public class Grafiek3D extends Object3D
{
	final double VERYBIG = 1e10d;
	final double NZERO = 1e-5d;
	boolean trimTop = false;
	boolean trimBottom = false;
	Vector3D topMaxVertex = null;
	Vector3D bottomMinVertex = null;
	Vector3D insideVertex = null;
	double centerDis = 0;
	double bigPos = 0;
	double bigMin = 0;
	
//	String[] vLabels; 
	
    public Grafiek3D()
    {}
    public Grafiek3D(Expressie exp,
    			double xMin, double xMax, double xStep, 
    		    double yMin, double yMax, double yStep,
    		    double zMin, double zMax, double zStep,
    		    String varNaamX, String varNaamY, int xFinerSteps, int yFinerSteps)
    {
    	
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
		
		bigPos = 10 * zMax;
		bigMin = 10 * zMin;
		
		Vector3D centerPoint = new Vector3D((xMin + xMax) / 2, (yMin + yMax) / 2, (zMin + zMax) / 2);
		Vector3D cornerPoint = new Vector3D(xMax, yMax, zMax);
		centerDis = Vector3D.distance(centerPoint, cornerPoint) + 1; 
		
		double xStepFine = xStep / xFinerSteps;
		double yStepFine = yStep / yFinerSteps;
		
    	int numXFacets = (int) Math.round((xMax - xMin) / xStepFine);
    	int numYFacets = (int) Math.round((yMax - yMin) / yStepFine);
    	int numGraphFacets = numXFacets * numYFacets;
//System.out.println("numGraphfacets = " + numGraphFacets);    	
   		int numGraphVertices = (numXFacets + 1) * (numYFacets + 1);
    	numVertices = numGraphVertices;
		numVertexLabels = numVertices;
		vertices = new Vector3D[numVertices];
		// do NOT forget this
		trVertices = new Vector3D[numVertices];
//		vLabels = new String[numVertices];

		double[] subst = new double[2];
		String[] vars = new String[2];
		vars[0] = varNaamX;
		vars[1] = varNaamY;
		for (int yCnt = 0; yCnt < (numYFacets + 1); yCnt++)			
			for (int xCnt = 0; xCnt < (numXFacets + 1); xCnt++)
			{	subst[0] = xMin + xCnt * xStepFine;
				subst[1] = yMin + yCnt * yStepFine;
				double expWaarde = exp.geefWaarde(subst, vars);

// LATER "oneindige" vertices omlabelen tot iets onschuldigs
				
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
		
//if (insideVertex == null)		
//System.out.println("insideVertex = null");
//else
//System.out.println("insideVertex = " + insideVertex.toString());

		// maximale aantal
	    int tempNumFacets = 2 * numGraphFacets;
	    int numNonNullFacets = 0;
	    Facet3D[] tempFacets = new Facet3D[tempNumFacets];
	    int facetCount = 0;
	        
	    for (int yCnt = 0; yCnt < numYFacets; yCnt++)
	     	for (int xCnt = 0; xCnt < numXFacets; xCnt++)
	       	{	// indices van de 4 vertices
	     		int[] indices = new int[4];
	       		indices[0] = xCnt + (numXFacets + 1) * yCnt;
	       		indices[1] = xCnt + 1 + (numXFacets + 1) * yCnt;
	       		indices[2] = xCnt + 1 + (numXFacets + 1) * (yCnt + 1);
	       		indices[3] = xCnt + (numXFacets + 1) * (yCnt + 1);	

	       		// 4 mogelijke deeldriehoejes
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
	       		
	       		// hier voor de "oneindige" vertices geen facet maken
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
	       		
	       		if (unDefinedCnt == 1)
	       		{
//System.out.println("unDefinedCnt = 1");	       			
	       			if (unDefinedIndex == 0)
	       			{	
	       				
//CHECK UNWANTED	       				
	       				numNonNullFacets++;
	       				tempFacets[facetCount] = new Facet3D(vertices, indices4, Grafiek3DComponent.graphColor);
	       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
	       				//tempFacets[facetCount].edgeCodes[0] = 50;
	       				//tempFacets[facetCount].edgeCodes[1] = 50;
	       				//tempFacets[facetCount].edgeCodes[2] = 51;
	       				facetCount++;
	       				
	       			}
	       			if (unDefinedIndex == 1)
	       			{
//CHECK UNWANTED	       				
	       				numNonNullFacets++;
	       				tempFacets[facetCount] = new Facet3D(vertices, indices2, Grafiek3DComponent.graphColor);
	       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
	       				//tempFacets[facetCount].edgeCodes[0] = 50;
	       				//tempFacets[facetCount].edgeCodes[1] = 50;
	       				//tempFacets[facetCount].edgeCodes[2] = 51;
	       				facetCount++;
	       			}
	       			if (unDefinedIndex == 2)
	       			{
//CHECK UNWANTED	       				
	       				numNonNullFacets++;
	       				tempFacets[facetCount] = new Facet3D(vertices, indices3, Grafiek3DComponent.graphColor);
	       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
	       				//tempFacets[facetCount].edgeCodes[0] = 50;
	       				//tempFacets[facetCount].edgeCodes[1] = 50;
	       				//tempFacets[facetCount].edgeCodes[2] = 51;
	       				facetCount++;
	       			}
	       			if (unDefinedIndex == 3)
	       			{
//CHECK UNWANTED	       				
	       				numNonNullFacets++;
	       				tempFacets[facetCount] = new Facet3D(vertices, indices1, Grafiek3DComponent.graphColor);
	       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
	       				//tempFacets[facetCount].edgeCodes[0] = 50;
	       				//tempFacets[facetCount].edgeCodes[1] = 50;
	       				//tempFacets[facetCount].edgeCodes[2] = 51;
	       				facetCount++;
	       			}
	       		}
	       		
	       		boolean vertexUnDefined = (unDefinedCnt > 0);
//if (vertexUnDefined)
//System.out.println("vertexUnDefined = " + unDefinedCnt);	       		

				int unWantedCnt = 0;
	        	if (!vertexUnDefined)
	        	{	
	        		int[] unWantedIndices = new int[6]; // 4 zijden + 2 diagonalen
	        		//for (int i = 0; i < unWantedIndices.length; i++)
	        		//	unWantedIndices[i] = -1;
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
	        			{	// neem driehoek 0,2,3 (indices2)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices2, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				//tempFacets[facetCount].edgeCodes[0] = 50;
		       				//tempFacets[facetCount].edgeCodes[1] = 50;
		       				//tempFacets[facetCount].edgeCodes[2] = 51;
		       				facetCount++;
	        				
	        			}
	        			if ((unWantedIndices[1] == 1) && (unWantedIndices[2] == 1))
	        			{	// neem drieheoek 0,1,3 (indices3)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices3, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				//tempFacets[facetCount].edgeCodes[0] = 50;
		       				//tempFacets[facetCount].edgeCodes[1] = 50;
		       				//tempFacets[facetCount].edgeCodes[2] = 51;
		       				facetCount++;
	        				
	        			}
	        			if ((unWantedIndices[2] == 1) && (unWantedIndices[3] == 1))
	        			{   // neem driehoeh 0,1,2 (indices1)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices1, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				//tempFacets[facetCount].edgeCodes[0] = 50;
		       				//tempFacets[facetCount].edgeCodes[1] = 50;
		       				//tempFacets[facetCount].edgeCodes[2] = 51;
		       				facetCount++;
	        				
	        			}
	        			if ((unWantedIndices[3] == 1) && (unWantedIndices[0] == 1))
	        			{	// neem driehoek 1,2,3 (indices4)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices4, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				//tempFacets[facetCount].edgeCodes[0] = 50;
		       				//tempFacets[facetCount].edgeCodes[1] = 50;
		       				//tempFacets[facetCount].edgeCodes[2] = 51;
		       				facetCount++;
	        				
	        			}
//HIER NOG MEER?	        			
	        			
	        		}
	        		else if (unWantedCnt == 3)
	        		{
	        			if ((unWantedIndices[0] == 1) && (unWantedIndices[1] == 1) && (unWantedIndices[5] == 1))
	        			{	// neem driehoek 0,2,3 (indices2)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices2, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				//tempFacets[facetCount].edgeCodes[0] = 50;
		       				//tempFacets[facetCount].edgeCodes[1] = 50;
		       				//tempFacets[facetCount].edgeCodes[2] = 51;
		       				facetCount++;
	        				
	        			}
	        			if ((unWantedIndices[1] == 1) && (unWantedIndices[2] == 1) && (unWantedIndices[4] == 1))
	        			{	// neem drieheoek 0,1,3 (indices3)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices3, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				//tempFacets[facetCount].edgeCodes[0] = 50;
		       				//tempFacets[facetCount].edgeCodes[1] = 50;
		       				//tempFacets[facetCount].edgeCodes[2] = 51;
		       				facetCount++;
	        				
	        			}
	        			if ((unWantedIndices[2] == 1) && (unWantedIndices[3] == 1) && (unWantedIndices[5] == 1))
	        			{   // neem driehoeh 0,1,2 (indices1)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices1, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				//tempFacets[facetCount].edgeCodes[0] = 50;
		       				//tempFacets[facetCount].edgeCodes[1] = 50;
		       				//tempFacets[facetCount].edgeCodes[2] = 51;
		       				facetCount++;
	        				
	        			}
	        			if ((unWantedIndices[3] == 1) && (unWantedIndices[0] == 1) && (unWantedIndices[4] == 1))
	        			{	// neem driehoek 1,2,3 (indices4)
		       				numNonNullFacets++;
		       				tempFacets[facetCount] = new Facet3D(vertices, indices4, Grafiek3DComponent.graphColor);
		       				tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       				//tempFacets[facetCount].edgeCodes[0] = 50;
		       				//tempFacets[facetCount].edgeCodes[1] = 50;
		       				//tempFacets[facetCount].edgeCodes[2] = 51;
		       				facetCount++;
	        				
	        			}
	        			
	        			
	        		}

	        		
	        	}
	        	boolean edgeUnWanted = (unWantedCnt > 0);
//if (edgeUnWanted)
//System.out.println("edgeUnWanted = " + unWantedCnt);	       		
	        	
	       		if (!vertexUnDefined && !edgeUnWanted)
	       		{
	       			
	       			double distance1 = Vector3D.distance(vertices[indices[0]], vertices[indices[2]]);
	       			double distance2 = Vector3D.distance(vertices[indices[1]], vertices[indices[3]]);
	       			
	       			//if (dotProduct1 < dotProduct2)
	       			if (distance1 < distance2)
	       			{	// neem 1 en 2 
		       			numNonNullFacets++;
		       			tempFacets[facetCount] = new Facet3D(vertices, indices1, Grafiek3DComponent.graphColor);
		       			tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       			tempFacets[facetCount].edgeCodes[0] = 50;
		       			tempFacets[facetCount].edgeCodes[1] = 50;
		       			tempFacets[facetCount].edgeCodes[2] = 51;
		       			facetCount++;
		       			
		       			numNonNullFacets++;
		       			tempFacets[facetCount] = new Facet3D(vertices, indices2, Grafiek3DComponent.graphColor);
		       			tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       			tempFacets[facetCount].edgeCodes[0] = 51;
		       			tempFacets[facetCount].edgeCodes[1] = 50;
		       			tempFacets[facetCount].edgeCodes[2] = 50;
		       			facetCount++;
		       			
	       				
	       				
	       			}
	       			else // distance1 > distance2
	       			{	// neem 3 en 4
		       			numNonNullFacets++;
		       			tempFacets[facetCount] = new Facet3D(vertices, indices3, Grafiek3DComponent.graphColor);
		       			tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       			tempFacets[facetCount].edgeCodes[0] = 50;
		       			tempFacets[facetCount].edgeCodes[1] = 51;
		       			tempFacets[facetCount].edgeCodes[2] = 50;

		       			facetCount++;
		       			
		       			numNonNullFacets++;
		       			tempFacets[facetCount] = new Facet3D(vertices, indices4, Grafiek3DComponent.graphColor);
		       			tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
		       			tempFacets[facetCount].edgeCodes[0] = 50;
		       			tempFacets[facetCount].edgeCodes[1] = 50;
		       			tempFacets[facetCount].edgeCodes[2] = 51;

		       			facetCount++;
	       				
	       			}
	       			
	       			//numNonNullFacets++;
	       			//tempFacets[facetCount] = new Facet3D(vertices, indices, Grafiek3DComponent.graphColor);
	       			//tempFacets[facetCount].outlineColor = Grafiek3DComponent.graphOutlineColor;
	       			//facetCount++;
	       		}
	        }

// hier de null-facets opruimen en numFacets aanpassen	        
	        
//System.out.println("tempNumFacets = " + tempNumFacets);
//System.out.println("numNonNullFacets = " + numNonNullFacets);
	    
	    numFacets = numNonNullFacets;
	    facets = new Facet3D[numFacets];
	    int nonNullCnt = 0;
	    for (int fCnt = 0; fCnt < tempNumFacets; fCnt++)
	    {  	if (tempFacets[fCnt] != null)
	    	{	facets[nonNullCnt] = tempFacets[fCnt];
	    	    nonNullCnt++;
	    	
	    	}
	    }
	    
//for (int fCnt = 0; fCnt < numFacets; fCnt++)
//{	if (facets[fCnt] == null)
//	System.out.println("" + fCnt + " null");
//}
	    
	    // label "oneindige" vertices tot iets onschuldigs	    
        for (int vCnt = 0; vCnt < numVertices; vCnt++)
        {	if (isUnDefined(vertices[vCnt].z))
        		vertices[vCnt] = new Vector3D(vertices[vCnt].x, vertices[vCnt].y, (zMin + zMax) / 2);
        }
	    
    	
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] = "";
//                    vLabels[facets[fCnt].indices[vCnt]];
                    

        // find the center !!
        Vector3D center = new Vector3D((xMin + xMax) / 2, (yMin + yMax) / 2, (zMin + zMax) / 2);
        //initObject3D(true, false);
        initObject3D(true, center, false);
                
    	
    }
    
    public boolean isUnDefined(double d)
    {
    	boolean unDefined = false;
    	
    	unDefined = Double.isNaN(d) || Double.isInfinite(d); 
    	
    	return unDefined;
    }
    
    public boolean isUnWanted(double d)
    {	boolean unWanted = false;
    	
    	unWanted = Double.isNaN(d) || Double.isInfinite(d) || (Math.abs(d) > VERYBIG); 
    	
    	return unWanted;
    }
    
    public boolean isUnWanted(Vector3D v1, Vector3D v2, Expressie exp, String varNaamX, String varNaamY)
    {	boolean unWanted = false;
    
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
//System.out.println("unWanted " + v1.toString() + " " + v2.toString());    	
    	}
    
    	return unWanted;	
    }
    
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
