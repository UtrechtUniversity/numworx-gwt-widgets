package fi.grafiek3dgwt.client;

public class Trim 
{
	static double NZERO = 1e-5d;
	
	static final int XMIN = 0; 
	static final int XMAX = 1;
	static final int YMIN = 2; 
	static final int YMAX = 3;
	static final int ZMIN = 4; 
	static final int ZMAX = 5;
	
	public static Object3D trimObject3D(Object3D ob, double planeValue, int planeType)
	{
		Object3D trimmedObject = new EmptyObject3D();
		
        if (ob instanceof Grafiek3D)
        {
        	trimmedObject = new Grafiek3D();
        	((Grafiek3D) trimmedObject).trimTop = ((Grafiek3D) ob).trimTop;
        	((Grafiek3D) trimmedObject).trimBottom = ((Grafiek3D) ob).trimBottom;
        	((Grafiek3D) trimmedObject).topMaxVertex  = Vector3D.copyVector3D(((Grafiek3D) ob).topMaxVertex);
        	((Grafiek3D) trimmedObject).bottomMinVertex  = Vector3D.copyVector3D(((Grafiek3D) ob).bottomMinVertex);
        	((Grafiek3D) trimmedObject).insideVertex  = Vector3D.copyVector3D(((Grafiek3D) ob).insideVertex);
        	
        }
        
        if (ob instanceof Surface3D)
        {
        	trimmedObject = new Surface3D();
        	((Surface3D) trimmedObject).trimTop = ((Surface3D) ob).trimTop;
        	((Surface3D) trimmedObject).trimBottom = ((Surface3D) ob).trimBottom;
        	((Surface3D) trimmedObject).trimFront = ((Surface3D) ob).trimFront;
        	((Surface3D) trimmedObject).trimBack = ((Surface3D) ob).trimBack;
        	((Surface3D) trimmedObject).trimLeft = ((Surface3D) ob).trimLeft;
        	((Surface3D) trimmedObject).trimRight = ((Surface3D) ob).trimRight;
        	
        	((Surface3D) trimmedObject).topMaxVertex  = Vector3D.copyVector3D(((Surface3D) ob).topMaxVertex);
        	((Surface3D) trimmedObject).bottomMinVertex  = Vector3D.copyVector3D(((Surface3D) ob).bottomMinVertex);
        	((Surface3D) trimmedObject).frontMinVertex  = Vector3D.copyVector3D(((Surface3D) ob).frontMinVertex);
        	((Surface3D) trimmedObject).backMaxVertex  = Vector3D.copyVector3D(((Surface3D) ob).backMaxVertex);
        	((Surface3D) trimmedObject).leftMinVertex  = Vector3D.copyVector3D(((Surface3D) ob).leftMinVertex);
        	((Surface3D) trimmedObject).rightMaxVertex  = Vector3D.copyVector3D(((Surface3D) ob).rightMaxVertex);
        	
        	((Surface3D) trimmedObject).insideVertex  = Vector3D.copyVector3D(((Surface3D) ob).insideVertex);
        	
        }

        if (ob instanceof Curve3D)
        {
        	trimmedObject = new Curve3D();
        	((Curve3D) trimmedObject).trimTop = ((Curve3D) ob).trimTop;
        	((Curve3D) trimmedObject).trimBottom = ((Curve3D) ob).trimBottom;
        	((Curve3D) trimmedObject).trimFront = ((Curve3D) ob).trimFront;
        	((Curve3D) trimmedObject).trimBack = ((Curve3D) ob).trimBack;
        	((Curve3D) trimmedObject).trimLeft = ((Curve3D) ob).trimLeft;
        	((Curve3D) trimmedObject).trimRight = ((Curve3D) ob).trimRight;
        	
        	((Curve3D) trimmedObject).topMaxVertex  = Vector3D.copyVector3D(((Curve3D) ob).topMaxVertex);
        	((Curve3D) trimmedObject).bottomMinVertex  = Vector3D.copyVector3D(((Curve3D) ob).bottomMinVertex);
        	((Curve3D) trimmedObject).frontMinVertex  = Vector3D.copyVector3D(((Curve3D) ob).frontMinVertex);
        	((Curve3D) trimmedObject).backMaxVertex  = Vector3D.copyVector3D(((Curve3D) ob).backMaxVertex);
        	((Curve3D) trimmedObject).leftMinVertex  = Vector3D.copyVector3D(((Curve3D) ob).leftMinVertex);
        	((Curve3D) trimmedObject).rightMaxVertex  = Vector3D.copyVector3D(((Curve3D) ob).rightMaxVertex);
        	
        	((Curve3D) trimmedObject).insideVertex  = Vector3D.copyVector3D(((Curve3D) ob).insideVertex);
        	
        }

		for (int fCnt = 0; fCnt < ob.numFacets; fCnt++)
		{
			int[] vertexPositions = getVertexPositions(ob.facets[fCnt], planeValue, planeType);
			
            // counter for number of new points which
            // will end up "inside" 
            int insideIndCnt = 0;
            // shortcuts
            Facet3D facet = ob.facets[fCnt];
            int pts = facet.numPoints;
            // arrays for indices of these points (maximum)
            int[] insideInds = new int[pts + 1];

			// facet ligt inside of op de rand
			// maak een nieuw facet voor trimmedObject 
			if (vertexPositions[1] == 0)
			{
				// add facet to trimmedObject
                int firstIndex = trimmedObject.numVertices;
                for (int j = 0; j < ob.facets[fCnt].numPoints; j++)
                	trimmedObject.addVertex(new Vector3D(ob.facets[fCnt].points[j]), null);
                int[] inds = new int[ob.facets[fCnt].numPoints];
                for (int k = 0; k < ob.facets[fCnt].numPoints; k++)
                    inds[k] = k + firstIndex;
                Facet3D insideFacet = new Facet3D(trimmedObject.vertices, inds, ob.facets[fCnt].color);
                trimmedObject.addFacet(insideFacet);
                if (ob.facets[fCnt].numPoints == insideFacet.numPoints)
                	Facet3D.copyAttributes(ob.facets[fCnt], insideFacet, true);
                else
                	Facet3D.copyAttributes(ob.facets[fCnt], insideFacet, false);
                
                
			}
			// facet cuts the plane en 
			// moet doorgesneden worden
			else if ((vertexPositions[0] > 0) && (vertexPositions[1] > 0)) 
			{
				// now walk along the facet edgewise, if edge v1->v2 
                // is studied v1 is updated
                for (int j = 0; j < pts; j++)
                {   // find current side vj -> v(j+1)
                        // vj, last is v(pts-1)
                        Vector3D v1 = new Vector3D(facet.points[j]);
                        // position of v1
                        //int pos1 = plane.planePosition(v1);
                        boolean inside1 = insideValue(v1, planeValue, planeType);
                        // v(j+1), last is v0
                        Vector3D v2 = new Vector3D(facet.points[(j + 1) % pts]);
                        // position of v2
                        //int pos2 = plane.planePosition(v2);                      
                        boolean inside2 = insideValue(v2, planeValue, planeType);
                        // both "inside" 
                        //if ((pos1 == -1) && (pos2 == -1))
                        if (inside1 && inside2)
                        {   // add copy of vj to replacement
                            // keep track of index
                            trimmedObject.addVertex(v1, null);
                            // point to current last
                            insideInds[insideIndCnt] = trimmedObject.numVertices - 1;
                            insideIndCnt++;
                           
                        }
                        
                        // vj "inside" v(j+1) "outside"
                        // add vj->v to left, copy of v to right
                        // and copy of v to cut
                        //else if ((pos1 == -1) && (pos2 == 1))
                        else if (inside1 && !inside2)
                        {   trimmedObject.addVertex(v1, null);
                            insideInds[insideIndCnt] = trimmedObject.numVertices - 1;
                            insideIndCnt++;
                            // find v
                            Plane3D plane = getPlane(planeValue, planeType); 
                            Vector3D v = Plane3D.getIntersectionPoint(new Line3D(v1, v2), plane);
                            // add to left
                            trimmedObject.addVertex(v, null);
                            insideInds[insideIndCnt] = trimmedObject.numVertices - 1;
                            insideIndCnt++;
                           
                        }
                        // vj "outside" v(j+1) "inside"
                        // add vj->v to right, a copy of v to left
                        // and a copy of v to cut
                        //else if ((pos1 == 1) && (pos2 == -1))
                        else if (!inside1 && inside2)	
                        {   
                            // find v
                        	Plane3D plane = getPlane(planeValue, planeType);
                            Vector3D v = Plane3D.getIntersectionPoint(new Line3D(v1, v2), plane);
                            // add to left
                            trimmedObject.addVertex(v, null);
                            insideInds[insideIndCnt] = trimmedObject.numVertices - 1;
                            insideIndCnt++;
          
                        }
                    } // for - points

			} // else facet doorsnijden
	
        	// arrange indices to correct length
        	// create new facets
        	if (insideIndCnt > 1)
        	{   int[] finalInsideInds = new int[insideIndCnt];
            	// trim indices
            	for (int k = 0; k < insideIndCnt; k++)
            		finalInsideInds[k] = insideInds[k];
            	Facet3D insideFacet =  
            		new Facet3D(trimmedObject.vertices, finalInsideInds, facet.color);
            	Facet3D.copyAttributes(facet, insideFacet, false);        
            	//leftFacet.isReplacementOf = facet;
            	
            	trimmedObject.addFacet(insideFacet);
            	
            	
            	/*                
            	if (facet.numPoints == 2)                    
            	{
            	//copy the normal so that segment is treated as an INNER segment
            	//by the new Painter's
            	leftFacet.normal = new Vector3D(facet.normal);
            	if (plane.planePosition(leftFacet.points[0]) == 0)
            	leftFacet.vertexLabels[0] = "XX";
            	if (plane.planePosition(leftFacet.points[1]) == 0)
            	leftFacet.vertexLabels[1] = "XX";
            	}
            	*/
        	} // inside
			
			
		} // for facet loop
		
		
		// attributen ob overnemen
		trimmedObject.filled = ob.filled;
		trimmedObject.outlined = ob.outlined;
		
		trimmedObject.initObject3D(true, false);
		
		return trimmedObject;
	}
	
	
	
	public static int[] getVertexPositions(Facet3D f, double planeValue, int planeType)
	{	int[] result = new int[2];
		for (int vCnt = 0; vCnt < f.numPoints; vCnt++)
		{	if (insideValue(f.points[vCnt], planeValue, planeType))
				result[0]++;
			else
				result[1]++;
		}
		return result;
	}

			
	// inside of op de rand
	public static boolean insideValue(Vector3D v, double planeValue, double planeType)
	{	if (planeType == XMIN)
			return v.x >= planeValue - NZERO;
		else if (planeType == XMAX)
			return v.x <= planeValue + NZERO;	
		else if (planeType == YMIN)
			return v.y >= planeValue - NZERO;
		else if (planeType == YMAX)
			return v.y <= planeValue + NZERO;
		else if (planeType == ZMIN)
			return v.z >= planeValue - NZERO;
		else if (planeType == ZMAX)
			return v.z <= planeValue + NZERO;
			
		return false;	
	}
	
	public static Plane3D getPlane(double planeValue, double planeType)
	{
		if ((planeType == XMIN) || (planeType == XMAX)) 
			return new Plane3D(1,0,0,planeValue);
		else if ((planeType == YMIN) || (planeType == YMAX))
			return new Plane3D(0,1,0,planeValue);
		else if ((planeType == ZMIN) || (planeType == ZMAX))
			return new Plane3D(0,0,1,planeValue);
		
		return new Plane3D(1,0,0,0);
	}
	
	
}
