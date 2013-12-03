package fi.grafiek3dgwt.client;

//import java.awt.Color;
import fi.grafiek3dgwt.client.expressies.*;

public class Curve3D extends Object3D
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
	
    public Curve3D()
    {}
    public Curve3D(Expressie expX, Expressie expY, Expressie expZ,
    			   boolean cfa,
    			   double tMin, double tMax, int tPoints, 
   				   double xMin, double xMax,  
    			   double yMin, double yMax, 
    			   double zMin, double zMax, 
    			   String paramNaam)
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

// LATER "oneindige" vertices omlabelen tot iets onschuldigs
				
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
		
//if (insideVertex == null)		
//System.out.println("insideVertex = null");
//else
//System.out.println("insideVertex = " + insideVertex.toString());

		// maximale aantal
	    int tempNumFacets = numCurveFacets;
	    int numNonNullFacets = 0;
	    Facet3D[] tempFacets = new Facet3D[tempNumFacets];
	    int facetCount = 0;
	        
	    for (int tCnt = 0; tCnt < tPoints; tCnt++)
       	{	int[] indices = new int[2];
       		indices[0] = tCnt;
       		indices[1] = tCnt + 1;

   			boolean equal = false;
   			
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
	       		
       		boolean vertexUnDefined = (unDefinedCnt > 0);
       		if (!vertexUnDefined)
       			equal = vertices[indices[0]].equals(vertices[indices[1]]);
       		
      		
       		// test alleen als de vertices "normaal" zijn
       		int unWantedCnt = 0;
       		if (!vertexUnDefined)
       		{	if (isUnWanted(tMin + tCnt * tStep, tMin + tCnt * tStep + tStep, expX, expY, expZ, paramNaam))
       				unWantedCnt++;
       		}
	       		
       		boolean edgeUnWanted = (unWantedCnt > 0);       		
	        	
	       	if (!equal && !vertexUnDefined && !edgeUnWanted)
	       	{
	       			
	       			// check op dubbele verices was hierboven 
	       			Facet3D tempFacet = new Facet3D(vertices, indices, Grafiek3DComponent.curveColor);

	       			
       				numNonNullFacets++;
	       			tempFacets[facetCount] = tempFacet;
	       			tempFacets[facetCount].outlineColor = Grafiek3DComponent.curveOutlineColor;
	       			facetCount++;
	       			
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
