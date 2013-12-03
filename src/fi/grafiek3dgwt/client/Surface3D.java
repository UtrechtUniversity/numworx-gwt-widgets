package fi.grafiek3dgwt.client;

import java.awt.Color;
import fi.grafiek3dgwt.client.expressies.*;


public class Surface3D extends Object3D
{
	final double VERYBIG = 1e10d;
	final double NZERO = 1e-5d;

	boolean trimTop = false;
	boolean trimBottom = false;
	boolean trimFront = false;
	boolean trimBack = false;
	boolean trimLeft = false;
	boolean trimRight = false;
	
	Vector3D topMaxVertex = null;
	Vector3D bottomMinVertex = null;
	Vector3D frontMinVertex = null;
	Vector3D backMaxVertex = null;
	Vector3D leftMinVertex = null;
	Vector3D rightMaxVertex = null;
	
	Vector3D insideVertex = null;
	
	double centerDis = 0;
	
	double bigPosX = 0;
	double bigMinX = 0;
	double bigPosY = 0;
	double bigMinY = 0;
	double bigPosZ = 0;
	double bigMinZ = 0;
	
//	String[] vLabels; 
	
	boolean checkForAsymptotes = false;
	
    public Surface3D()
    {}
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

// LATER "oneindige" vertices omlabelen tot iets onschuldigs
				
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
//System.out.println("ez>zMax " + expZWaarde);						
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
		
//if (insideVertex == null)		
//System.out.println("insideVertex = null");
//else
//System.out.println("insideVertex = " + insideVertex.toString());
		

		// maximale aantal
	    int tempNumFacets = 2 * numSurfaceFacets;
	    int numNonNullFacets = 0;
	    Facet3D[] tempFacets = new Facet3D[tempNumFacets];
	    int facetCount = 0;
	        
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
	       		
	       		// hier voor de "oneindige" vertices geen facet maken
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
//if (vertexUnDefined)
//System.out.println("vertexUnDefined = " + unDefinedCnt);	       		
	       		

				int unWantedCnt = 0;
	        	if (!vertexUnDefined)
	        	{	
	        		int[] unWantedIndices = new int[6]; // 4 zijden + 2 diagonalen
	        		//for (int i = 0; i < unWantedIndices.length; i++)
	        		//	unWantedIndices[i] = -1;
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
//if (edgeUnWanted)
//System.out.println("edgeUnWanted = " + unWantedCnt);	       		
	       		
	       		
	       		if (!vertexUnDefined && !edgeUnWanted)
	       		{
	       			
	       			// check op dubbele verices
	       			Facet3D tempFacet = new Facet3D(vertices, indices, Grafiek3DComponent.surfaceColor);
	       			//tempFacet.reverseNormal();
	       			
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
	       			
	       			//if (dotProduct1 < dotProduct2)
	       				if (distance1 < distance2)
	       				{	// neem 1 en 2 
	       					numNonNullFacets++;
	       					tempFacets[facetCount] = new Facet3D(vertices, indices1, Grafiek3DComponent.surfaceColor);
	       					tempFacets[facetCount].outlineColor = Grafiek3DComponent.surfaceOutlineColor;
	       					//tempFacet.reverseNormal();
	       					tempFacets[facetCount].edgeCodes[0] = 52;
	       					tempFacets[facetCount].edgeCodes[1] = 52;
	       					tempFacets[facetCount].edgeCodes[2] = 51;
	       					facetCount++;
		       			
	       					numNonNullFacets++;
	       					tempFacets[facetCount] = new Facet3D(vertices, indices2, Grafiek3DComponent.surfaceColor);
	       					tempFacets[facetCount].outlineColor = Grafiek3DComponent.surfaceOutlineColor;
	       					//tempFacet.reverseNormal();
	       					tempFacets[facetCount].edgeCodes[0] = 51;
	       					tempFacets[facetCount].edgeCodes[1] = 52;
	       					tempFacets[facetCount].edgeCodes[2] = 52;
	       					facetCount++;
		       			
	       				       				
	       				}
	       				else // distance1 > distance2
	       				{	// neem 3 en 4
	       					numNonNullFacets++;
	       					tempFacets[facetCount] = new Facet3D(vertices, indices3, Grafiek3DComponent.surfaceColor);
	       					tempFacets[facetCount].outlineColor = Grafiek3DComponent.surfaceOutlineColor;
	       					//tempFacet.reverseNormal();
	       					tempFacets[facetCount].edgeCodes[0] = 52;
	       					tempFacets[facetCount].edgeCodes[1] = 51;
	       					tempFacets[facetCount].edgeCodes[2] = 52;
	       					facetCount++;
		       			
	       					numNonNullFacets++;
	       					tempFacets[facetCount] = new Facet3D(vertices, indices4, Grafiek3DComponent.surfaceColor);
	       					tempFacets[facetCount].outlineColor = Grafiek3DComponent.surfaceOutlineColor;
	       					//tempFacet.reverseNormal();
	       					tempFacets[facetCount].edgeCodes[0] = 52;
	       					tempFacets[facetCount].edgeCodes[1] = 52;
	       					tempFacets[facetCount].edgeCodes[2] = 51;
	       					facetCount++;
	       				
	       				}
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
        {	if (isUnDefined(vertices[vCnt]))
        		vertices[vCnt] = new Vector3D((xMin + xMax) / 2, (yMin + yMax) / 2, (zMin + zMax) / 2);
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
    
    public boolean isUnDefined(Vector3D v)
    {
    	return isUnDefined(v.x) || isUnDefined(v.y) || isUnDefined(v.z);
    }
  
/*    
    public boolean isUnWanted(double d)
    {	boolean unWanted = false;
    	
    	unWanted = Double.isNaN(d) || Double.isInfinite(d) || (Math.abs(d) > VERYBIG); 
    	
    	return unWanted;
    }
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
