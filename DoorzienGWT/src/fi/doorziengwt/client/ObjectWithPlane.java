package fi.doorziengwt.client;

import java.util.Vector;

/**
 * cutting a 3d-object with a given plane in 3-space: the 3d-object to be cut is an ObjectGroup3D
 * called origObjectGroup and is of the following type:<br>
 * a) a basic Object3D called origObject, which has no planes or lines, origObject is located at origObjectGroup.objects.elementAt(0); <br>
 * b) an ObjectWithLine, see class ObjectWithLine; <br>
 * c) an ObjectWithPlane, see this class; <br>
 * an instance of ObjectWithPlane (an ObjectGroup3D) contains the cutting plane as Plane3D (see class Plane3D), and <br>
 * in objects.elementAt(0) a (reference to) origObjectGroup <br>
 * in objects.elementAt(1) an Object3D called replacement: for each Facet3D of origObjectGroup3D that itself has not been replaced 
 * in the cutting hierarchy, and that is cut by the plane, replacement contains the two new Facet3D, which result from the cutting; 
 * note that the Facet3D being cut by the plane can also be a line or the cut of a plane already present in origObjectGroup; the attribute
 * isReplacementOf of all Facet3D in replacement will point to the Facet3D in origObjectGroup they replace<br>
 * in objects.elementAt(2) an Object3D called cut: cut contains all (new) Facet3D which together form the intersection of the
 * 3d-plane with origObjectGroup<br>
 * note that any ObjectGroup3D in the hierarchy is completely determined by origObject and a ordered list of planes and lines by which
 * origObject has been cut; this list of lines and planes is called the construction(list);<br>
 * to obtain the cut, use a little trick: while cutting facets of origObjectGroup, for determining the cut, use only those facets
 * replacing a facet of origObject; this results in a correct cut along the sides of origObject (with edges cut if facets of 
 * origObject were cut); however, the cut itself was not cut by planes (or lines lying in the cut) higher in the hierarchy, so use 
 * the construction to further the "cut the cut".             
 * @author huub
 */

public class ObjectWithPlane extends ObjectGroup3D
{   
	/**
	 * starting Object3D, no lines, no planes
	 */
    Object3D origObject;
    /**
     * the ObjectGroup3D to be cut by the plane 
     */
    ObjectGroup3D origObjectGroup;
    
    /**
     * the new object at objects.elementAt(1), contains the facets of origObjectGroup being replaced by the cutting
     */
    Object3D replacement; 
    /**
     * the new object at objects.elementAt(2), contains all facets which form the cut
     */
    Object3D cut;
    /**
     * the outline color of the (facets in the) cut
     */
    int planeColorIndex;
    /**
     * should the (facets making up the) cut be filled? 
     */
    boolean cutFilled = false;
    /**
     * the Plane3D that cuts origObjectGroup
     */
    Plane3D plane;
    /**
     * "empty" constructor for copying
     */
    public ObjectWithPlane()
    {}
    /**
     * constructor, assume the cutting plane is given by three non-collinear(!) points
     * @param og the ObjectGroup3D to be cut
     * @param point1 first point of cutting plane
     * @param point2 second point of cutting plane
     * @param point3 third point of cutting plane
     * @param pcIndex index of outline color of the (facets in the) cut (see class DrawConstants) 
     * @param makeCut should the cut be calculated?
     */
    public ObjectWithPlane(ObjectGroup3D og, 
                           Vector3D point1, Vector3D point2, Vector3D point3, 
                           int pcIndex, boolean makeCut)
    {   origObjectGroup = og;
        // goes to objects.elementAt(0)
        addObject3D(origObjectGroup);
        origObject = origObjectGroup.leftMostLeaf();                
        planeColorIndex = pcIndex;
        // create plane
        plane = new Plane3D(point1, point2, point3);
        // create replacement
        replacement = new EmptyObject3D();
        // create cut
        cut = new EmptyObject3D();  
        // loop through facets of origObjectGroup
        // and find the ones not having a replacement
        // cut these with plane and create vertices and facets of replacement
        for (int i = 0; i < origObjectGroup.numFacets; i++)
        {   // shortcut
            Facet3D facet = origObjectGroup.facets[i];

            boolean hasRep = false;
            if (origObjectGroup instanceof ObjectWithPlane)
               hasRep = ((ObjectWithPlane) origObjectGroup).hasReplacement(facet);
            else if (origObjectGroup instanceof ObjectWithLine)
               hasRep = ((ObjectWithLine) origObjectGroup).hasReplacement(facet);
            
            if (!hasRep)
            {   
            	// replacing a facet of origObject?
                boolean oReplace = replacesOrigObject(facet);
                // determine the number of vertices to the "left",
                // to the "right" and in the plane
                int leftCnt = 0, rightCnt = 0, onCnt = 0;
                for (int j = 0; j < facet.numPoints; j++)
                {   int pp = plane.planePosition(facet.points[j]);
                    if (pp == -1)
                        leftCnt++;
                    else if (pp == 1)
                        rightCnt++;
                    else // pp == 0
                        onCnt++;
                }    
                // counter for number of new points which
                // will end up "left" resp "right"
                int leftIndCnt = 0;
                int rightIndCnt = 0;
                // shortcut
                int pts = facet.numPoints;
                // arrays for indices of these new points (maximum)
                int[] leftInds = new int[pts + 1];
                int[] rightInds = new int[pts + 1];
                // whole facet to the left of plane
                if ((onCnt == 0) && (rightCnt == 0))
                {   // nothing to do
                }
                // whole facet to the right of plane
                else if ((leftCnt == 0) && (onCnt == 0))
                {   // nothing to do
                }
                // facet touches plane from the left in one vertex
                else if ((onCnt == 1) && (rightCnt == 0))
                {   
                	// if the facet is a segment and the cut should be created,
                	// replace the facet by an identical facet, so that the 
                	// vertex in the plane will be highlighted
                    if ((facet.numPoints == 2) && makeCut)
                    {   int index = replacement.numVertices;
                        replacement.addVertex(new Vector3D(facet.points[0]), null);
                        replacement.addVertex(new Vector3D(facet.points[1]), null);
                        int[] inds = new int[2];
                        inds[0] = index;
                        inds[1] = index + 1;
                        Facet3D lFacet = new Facet3D(replacement.vertices, inds, DrawConstants.black);
                        Facet3D.copyAttributes(facet, lFacet, true);
                        replacement.addFacet(lFacet);
                        lFacet.isReplacementOf = facet;
                        facet.visible = false;
                        if (plane.planePosition(lFacet.points[0]) == 0)
                            lFacet.vertexLabels[0] = "XX";
                        if (plane.planePosition(lFacet.points[1]) == 0)
                            lFacet.vertexLabels[1] = "XX";
                    }
                                  
                }
                // facet touches plane from the right in one vertex
                else if ((leftCnt == 0) && (onCnt == 1))
                {   
                	// if the facet is a segment and the cut should be created,
                	// replace the facet by an identical facet, so that the 
                	// vertex in the plane will be highlighted
                    if ((facet.numPoints == 2) && makeCut)
                    {   int index = replacement.numVertices;
                        replacement.addVertex(new Vector3D(facet.points[0]), null);
                        replacement.addVertex(new Vector3D(facet.points[1]), null);
                        int[] inds = new int[2];
                        inds[0] = index;
                        inds[1] = index + 1;
                        Facet3D rFacet = new Facet3D(replacement.vertices, inds, DrawConstants.black);
                        Facet3D.copyAttributes(facet, rFacet, true);
                        replacement.addFacet(rFacet);
                        rFacet.isReplacementOf = facet;
                        facet.visible = false;
                        if (plane.planePosition(rFacet.points[0]) == 0)
                        {    rFacet.vertexLabels[0] = "XX";
                        }
                        if (plane.planePosition(rFacet.points[1]) == 0)
                        {    rFacet.vertexLabels[1] = "XX";
                        }
                    
                    }
                }
                // facet has one or more edges in (on) the plane
                // and is to the left or right of the plane
                // or facet cuts the plane
                else
                {
                    // now walk along the facet edgewise, if edge v1->v2 
                    // is studied v1 is updated
                    for (int j = 0; j < pts; j++)
                    {   // find current side vj -> v(j+1)
                        // vj, last is v(pts-1)
                        Vector3D v1 = new Vector3D(facet.points[j]);
                        // position of v1
                        int pos1 = plane.planePosition(v1);                    
                        // v(j+1), last is v0
                        Vector3D v2 = new Vector3D(facet.points[(j + 1) % pts]);
                        // position of v2
                        int pos2 = plane.planePosition(v2);                                        
                        // both "left" of plane
                        if ((pos1 == -1) && (pos2 == -1))
                        {   // add copy of vj to replacement
                            // keep track of index
                            replacement.addVertex(v1, null);
                            // point to current last
                            leftInds[leftIndCnt] = replacement.numVertices - 1;
                            leftIndCnt++;
                        }
                        // both "right" of plane
                        else if ((pos1 == 1) && (pos2 == 1))
                        {   // add copy of vj to replacement
                            // keep track of index
                            replacement.addVertex(v1, null);
                            // point to current last                            
                            rightInds[rightIndCnt] = replacement.numVertices - 1;
                            rightIndCnt++;
                        }
                        // vj "left" v(j+1) "right"; find the intersection
                        // v of vj->v(j+1) with the plane
                        // add vj->v to left, a copy of v to right
                        // and a copy of v to cut
                        else if ((pos1 == -1) && (pos2 == 1))
                        {   replacement.addVertex(v1, null);
                            leftInds[leftIndCnt] = replacement.numVertices - 1;
                            leftIndCnt++;
                            // find v
                            Vector3D v = Plane3D.getIntersectionPoint(
                                new Line3D(v1, v2), plane);
                            // add to left
                            replacement.addVertex(v, null);
                            leftInds[leftIndCnt] = replacement.numVertices - 1;
                            leftIndCnt++;
                            // add a copy(!) of v to right
                            Vector3D vc = new Vector3D(v);
                            replacement.addVertex(vc, null);
                            rightInds[rightIndCnt] = replacement.numVertices - 1;
                            rightIndCnt++;
                            // add another copy to the cut, avoid replicates
                            Vector3D vcc = new Vector3D(v);
                            if (makeCut && oReplace &&
                                (cut.containsVertex(vcc) < 0)
                               ) 
                                cut.addVertex(vcc, null);
                        }
                        // vj "right" v(j+1) "left" find the intersection
                        // v of vj->v(j+1) with the plane
                        // add vj->v to right, a copy of v to left
                        // and a copy of v to cut
                        else if ((pos1 == 1) && (pos2 == -1))
                        {   replacement.addVertex(v1, null);
                            rightInds[rightIndCnt] = replacement.numVertices - 1;
                            rightIndCnt++;
                            // find v
                            Vector3D v = Plane3D.getIntersectionPoint(
                                new Line3D(v1, v2), plane);
                            // add to left
                            replacement.addVertex(v, null);
                            leftInds[leftIndCnt] = replacement.numVertices - 1;
                            leftIndCnt++;
                            // add a of v copy to right 
                            Vector3D vc = new Vector3D(v);                        
                            replacement.addVertex(vc, null);
                            rightInds[rightIndCnt] = replacement.numVertices - 1;
                            rightIndCnt++;
                            // add another copy to the cut
                            Vector3D vcc = new Vector3D(v);
                            if (makeCut && oReplace &&
                                (cut.containsVertex(vcc) < 0)
                               ) 
                                cut.addVertex(vcc, null);
                        }
                  
                        // vj "on" v(j+1) "right"
                        // add vj left and a copy of vj right
                        // another copy to the cut
                        else if ((pos1 == 0) && (pos2 == 1))
                        {   
                            // don't add to left if leftCnt == 0    
                            if (leftCnt > 0)
                            {
                                // add to left
                                replacement.addVertex(v1, null);
                                leftInds[leftIndCnt] = replacement.numVertices - 1;
                                leftIndCnt++;
                            }
                            // add a copy to right
                            Vector3D v1c = new Vector3D(v1);                        
                            replacement.addVertex(v1c, null);                            
                            rightInds[rightIndCnt] = replacement.numVertices - 1;
                            rightIndCnt++;
                            // add another copy to the cut
                            Vector3D v1cc = new Vector3D(v1c);
                            if (makeCut && oReplace &&
                                (cut.containsVertex(v1cc) < 0)
                               ) 
                                cut.addVertex(v1cc, null);
                        
                        }
                        
                        // vj "right" v(j+1) "on"
                        // add vj to right
                        else if ((pos1 == 1) && (pos2 == 0))
                        {   replacement.addVertex(v1, null);
                            rightInds[rightIndCnt] = replacement.numVertices - 1;
                            rightIndCnt++;
                        }
                        // vj "left" v(j+1) "on"
                        // add vj to left
                        else if ((pos1 == -1) && (pos2 == 0))
                        {   replacement.addVertex(v1, null);
                            leftInds[leftIndCnt] = replacement.numVertices - 1;
                            leftIndCnt++;
                        }
                        
                        // vj "on" v(j+1) "left"
                        // add vj left, a copy to right
                        // and another copy to the cut
                        else if ((pos1 == 0) && (pos2 == -1))
                        {   
                            // add to left
                            replacement.addVertex(v1, null);
                            leftInds[leftIndCnt] = replacement.numVertices - 1;
                            leftIndCnt++;
                            // don't add to right if  rightCnt == 0                                                        
                            if (rightCnt > 0)
                            {
                                // add to right
                                Vector3D v1c = new Vector3D(v1);                        
                                replacement.addVertex(v1c, null);
                                rightInds[rightIndCnt] = replacement.numVertices - 1;
                                rightIndCnt++;
                            }
                            // add another copy to the cut
                            Vector3D v1cc = new Vector3D(v1);
                            if (makeCut && oReplace && 
                                (cut.containsVertex(v1cc) < 0)
                               ) 
                                cut.addVertex(v1cc, null);
                        
                        }
                        // vj->v(j+1) is "in" the plane
                        // i.e. vj "on" and v(j+1) "on"
                        // add vj left, a copy to right
                        // and another copy to the cut
                        else if ((pos1 == 0) && (pos2 == 0))
                        {   
                            // don't add to left if leftCnt == 0     
                            // if leftCnt > 0 is then necessarily rightCnt = 0 
                            // since facets are convex
                            if (leftCnt > 0)
                            {
                                // add to left
                                replacement.addVertex(v1, null);
                                leftInds[leftIndCnt] = replacement.numVertices - 1;
                                leftIndCnt++;
                            }
                            // don't add to left if rightCnt == 0                            
                            // als rightCnt > 0 is then necessarily leftCnt = 0
                            // since facets are convex
                            if (rightCnt > 0)
                            {
                                // add to right
                                Vector3D v1c = new Vector3D(v1);                        
                                replacement.addVertex(v1c, null);                            
                                rightInds[rightIndCnt] = replacement.numVertices - 1;
                                rightIndCnt++;
                            }
                            // since leftCnt > 0 and rightCnt > 0 is not possible (see above)
                            // one vertex for the cut is enough
                            if ((leftCnt > 0) || (rightCnt > 0))
                            {
                                // add another copy to the cut
                                Vector3D v1cc = new Vector3D(v1);
                                if (makeCut && oReplace &&
                                    (cut.containsVertex(v1cc) < 0)
                                   ) 
                                    cut.addVertex(v1cc, null);
                            }                                
                            // case leftCnt == 0 && rightCnt == 0:
                            // 2 possibilities 
                            // 1) this is a facet i.e. numPoints > 2
                            // then this must be a facet replacing a facet of origObject
                            // and the cut was made along a facet of origObject 
                            // since one cannot cut an object twice by the same plane (see class DrawingPanel) 
                            // then look at plane position of v1 + facet.normal
                            // if < 0 add to the right (nothing to the left, normal points to the outside of origObject)
                            // if  > 0 add to the left (similar argument)
                            // add nothing to the cut
                            // 2) this is a segment that should later end up in the cut,
                            // but this will automatically happen after "cutting the cut"                                
                            if ((leftCnt == 0) && (rightCnt == 0))
                            {   if (facet.numPoints >= 3)
                                {   Vector3D normalPoint = 
                                        Vector3D.plus(v1, facet.normal);
                                    int nppp = plane.planePosition(normalPoint);
                                    if (nppp == -1)
                                   {   // add right
                                        replacement.addVertex(v1, null);                            
                                        rightInds[rightIndCnt] = replacement.numVertices - 1;
                                        rightIndCnt++;
                                    }
                                    else if (nppp == 1)
                                    {
                                        // add to left
                                        replacement.addVertex(v1, null);
                                        leftInds[leftIndCnt] = replacement.numVertices - 1;
                                        leftIndCnt++;
                                    }    
                                    makeCut = false;
                                }
                            } //(leftCnt == 0) && (rightCnt == 0)
                        } // last else if
                    } // for - points    
                } // else position    
                
                // arrange index arrays to correct length
                // create new facets for replacement
                if (leftIndCnt > 0)
                {   int[] finalLeftInds = new int[leftIndCnt];
                    // trim indices
                    for (int k = 0; k < leftIndCnt; k++)
                        finalLeftInds[k] = leftInds[k];
                    Facet3D leftFacet =     
                        new Facet3D(replacement.vertices, 
                            finalLeftInds, facet.color);
                    Facet3D.copyAttributes(facet, leftFacet, false);        
                    leftFacet.isReplacementOf = facet;                                            
                    replacement.addFacet(leftFacet); 
                    // segment
                    if (facet.numPoints == 2)                    
                    {
                    	// copy the normal so that segment is treated as an INNER segment
                    	// by the new paint algorithm
                    	leftFacet.normal = new Vector3D(facet.normal);
                    	if (plane.planePosition(leftFacet.points[0]) == 0)
                    		leftFacet.vertexLabels[0] = "XX";
                    	if (plane.planePosition(leftFacet.points[1]) == 0)
                    		leftFacet.vertexLabels[1] = "XX";
                    }
                } // left
                if (rightIndCnt > 0)
                {   int[] finalRightInds = new int[rightIndCnt];
                    // trim indices                
                    for (int k = 0; k < rightIndCnt; k++)
                        finalRightInds[k] = rightInds[k];
                    Facet3D rightFacet =     
                        new Facet3D(replacement.vertices, 
                            finalRightInds, facet.color);
                    Facet3D.copyAttributes(facet, rightFacet, false);                                    
                    rightFacet.isReplacementOf = facet;                    
                    replacement.addFacet(rightFacet);
                    // segment
                    if (facet.numPoints == 2)                    
                    {
                    	// copy the normal so that segment is treated as an INNER segment
                    	// by the new paint algorithm
                    	rightFacet.normal = new Vector3D(facet.normal);    
                    	if (plane.planePosition(rightFacet.points[0]) == 0)
                    		rightFacet.vertexLabels[0] = "XX";
                    	if (plane.planePosition(rightFacet.points[1]) == 0)
                    		rightFacet.vertexLabels[1] = "XX";
                    }
                    
                } // right
                if ((leftIndCnt > 0) || (rightIndCnt > 0))
                {    facet.visible = false;
                }
            } // facet.visible
        } // for facet loop

        // fix replacement vertex codes and labels      
        for (int cnt = 0; cnt < replacement.numFacets; cnt++)
        {   Facet3D repFacet = replacement.facets[cnt];
            Facet3D facet = repFacet.isReplacementOf;
            for (int pCnt = 0; pCnt < repFacet.numPoints; pCnt++)
            {   int index = Facet3D.containsVertex(
                            facet, repFacet.points[pCnt]);
                boolean isOn = 
                    (plane.planePosition(repFacet.points[pCnt]) == 0);
                            
                if (index >= 0)
                {    if (!isOn)
                         repFacet.vertexCodes[pCnt] = facet.vertexCodes[index];
                     else
                     {   if ((facet.vertexCodes[index] % Facet3D.HIDDENSHIFT) ==
                             DrawConstants.lineColorIndex)
                             repFacet.vertexCodes[pCnt] = facet.vertexCodes[index];
                         else    
                             repFacet.vertexCodes[pCnt] = planeColorIndex;
                     }    
                     if (facet.vertexLabels[index] != null)
                         repFacet.vertexLabels[pCnt] = new String(
                                facet.vertexLabels[index]);
                }
                else // new point
                {   
                    // a segment was cut
                    if (repFacet.numPoints == 2)
                        repFacet.vertexCodes[pCnt] = DrawConstants.lineColorIndex +
                                                     Facet3D.HIDDENSHIFT;
                    else
                    {   
                        int eIndex = Facet3D.edgeContainsSegment(
                            facet, repFacet.points[pCnt], repFacet.points[pCnt]);
                        if (eIndex >= 0)
                        {   
                            if (facet.edgeCodes[eIndex] == DrawConstants.lineColorIndex)
                                repFacet.vertexCodes[pCnt] = DrawConstants.lineColorIndex;
                            else    
                                repFacet.vertexCodes[pCnt] = planeColorIndex;
                        }    
                        else
                            repFacet.vertexCodes[pCnt] = planeColorIndex;
                        
                    }    
                }    
            }    
        }    
        

        // fix replacement edge codes        
        for (int cnt = 0; cnt < replacement.numFacets; cnt++)
        {   
            Facet3D repFacet = replacement.facets[cnt];
            Facet3D facet = repFacet.isReplacementOf;
            for (int pCnt = 0; pCnt < repFacet.numPoints; pCnt++)
            {   
                int index = Facet3D.edgeContainsDirSegment(
                    facet, repFacet.points[pCnt], 
                           repFacet.points[(pCnt + 1) % repFacet.numPoints]);
                boolean isOn = 
                    (plane.planePosition(repFacet.points[pCnt]) == 0) &&
                    (plane.planePosition(
                        repFacet.points[(pCnt + 1) % repFacet.numPoints]) == 0);
                if ((index >= 0) && !isOn)
                    repFacet.edgeCodes[pCnt] = facet.edgeCodes[index];
                else // index < 0 || isOn
                {   Vector construction = new Vector();
                    boolean isOnLine = false;
                    if (origObjectGroup instanceof ObjectWithLine)
                        construction = ((ObjectWithLine) origObjectGroup).getConstruction();
                    else if (origObjectGroup instanceof ObjectWithPlane)
                        construction = ((ObjectWithPlane) origObjectGroup).getConstruction();
                    for (int i = 0; i < construction.size(); i++)
                    {   Object conObj = construction.elementAt(i);
                        if (conObj instanceof Line3D)
                        {   Line3D aLine = (Line3D) conObj;
                            boolean onThisLine = aLine.contains(repFacet.points[pCnt]) &&
                                aLine.contains(repFacet.points[(pCnt + 1) % repFacet.numPoints]);
                            isOnLine = isOnLine || onThisLine;
                        }
                    }
                    if (isOnLine)                    
                        repFacet.edgeCodes[pCnt] = DrawConstants.lineColorIndex;                    
                    else
                        repFacet.edgeCodes[pCnt] = planeColorIndex;
                }
            }    
        }    
        
        // set properties of replacement
        replacement.outlined = origObjectGroup.outlined; // locally overridden
        replacement.filled = origObjectGroup.filled; // locally overridden
        replacement.visible = origObjectGroup.visible;
        // fix center and diameter as of originalObject
        replacement.initObject3D(true, new Vector3D(origObjectGroup.center),
                                 origObjectGroup.diameter, false);
        // puts replacement at objects.elementAt(1)                         
        if (replacement.numVertices > 0)        
        {    addObject3D(replacement);                         
        }

        // creating the cut - part 1: fix the edge colors of facets which replace cuts higher up   
        if (makeCut)        
        {
        	for (int i = 0; i < replacement.numFacets; i++)
        	{   Facet3D cut = replacesCut(replacement.facets[i]);
            	if (cut != null)
            	{   
            		// any edge of replacement.facets[i] which is
            		// on a cut higher up has no outline (inherited via edgeLabels)
            		// any edge of replacement.facets[i] which is
            		// not on this cut should be outlined
            		// use containsDirSegment!!
            		for (int j = 0; j < replacement.facets[i].numPoints; j++)
            		{   Vector3D start = replacement.facets[i].points[j];
                    	Vector3D end = 
                    			replacement.facets[i].points[(j + 1) % replacement.facets[i].numPoints];
                    	int index = Facet3D.edgeContainsDirSegment(cut, start, end);
                    
                    	// original cut does not contain the segment
                    	// so segment must be colored
                    	if (index < 0) // forced hidden
                    	{
                    		Vector construction = new Vector();
                    		boolean isOnLine = false;
                    		if (origObjectGroup instanceof ObjectWithLine)
                    			construction = ((ObjectWithLine) origObjectGroup).getConstruction();
                    		else if (origObjectGroup instanceof ObjectWithPlane)
                    			construction = ((ObjectWithPlane) origObjectGroup).getConstruction();
                    		for (int k = 0; k < construction.size(); k++)
                    		{   Object conObj = construction.elementAt(k);
                            	if (conObj instanceof Line3D)
                            	{   Line3D aLine = (Line3D) conObj;
                                	boolean onThisLine = aLine.contains(start) && aLine.contains(end);
                                	isOnLine = isOnLine || onThisLine;
                            	}
                    		}                        
                    		if (isOnLine)
                    			replacement.facets[i].edgeCodes[j] = 
                                	DrawConstants.lineColorIndex + Facet3D.HIDDENSHIFT;
                    		else
                    			replacement.facets[i].edgeCodes[j] = 
                                	planeColorIndex + Facet3D.HIDDENSHIFT;
                    	}        
                    	// original cut contains the segment
                    	// so segment should be hidden                            
                    	else if (cut.edgeCodes[index] == -1)
                    		replacement.facets[i].edgeCodes[j] = -1;
                	}    
                	for (int j = 0; j < replacement.facets[i].numPoints; j++)
                	{   Vector3D start = replacement.facets[i].points[j];
                    	int index = Facet3D.containsVertex(cut, start);
                    	if (index < 0) // forced hidden
                        	replacement.facets[i].vertexCodes[j] = 
                            	planeColorIndex + Facet3D.HIDDENSHIFT;
                    	else if (cut.vertexCodes[index] == -1)
                    		replacement.facets[i].vertexCodes[j] = -1;        
                	}    
                
            	} // cut != null 
        	} // for 

        } //if (makeCut)
        

        Vector orientedCut = new Vector();
        Vector orientedCutLabels = new Vector();

        // creating the cut - part 2 find the cut (that is the cut of the original object)
        // as one or more facets from the vertices that are on the cut (saved above);
        // Note: since all original objects are convex, the cut consists of one facet
        if ((cut.numVertices > 2) && makeCut)
        {
        	// now find the cut, vertices are known
        	// make de cut with the "left" side (the other side is the reverse)
            boolean[] verticesUsed = new boolean[cut.numVertices];
            // this loop is only used once when origibal object are convex  
            while (firstNotUsed(verticesUsed) >= 0)
            {   
                // choose an unused vertex
                int startIndex = firstNotUsed(verticesUsed);
                int[] newInds = new int[cut.numVertices]; 
                int indCnt = 0;
                Facet3D leftFacet = null;
                while (verticesUsed[startIndex] == false)
                {   
                    // label as use
                    verticesUsed[startIndex] = true;
                    // set index
                    newInds[indCnt] = startIndex;
                    indCnt++;
                    // get the point
                    Vector3D v1 = cut.vertices[startIndex];

                    // add to orientedCut
                    orientedCut.addElement(new Vector3D(v1));
                    // replacement exists as uninitialised object!
                    // all its facets are visible
                    // find a facet from replacement that contains this vertex,
                    // has no points to the right and such that the point after v1
                    // is also a vertex of the cut
                    // replacement must also replace a facet of origObject!!
                    Vector v1Facets = replacement.facetsContaining(v1);

                    int fCount = 0;
                    boolean found = false;
                    while ((fCount < v1Facets.size()) && !found)
                    {   Facet3D f = (Facet3D) v1Facets.elementAt(fCount);
                        // determine right points
                        int rightCnt = 0;
                        for (int i = 0; i < f.numPoints; i++)
                        {   int pp = plane.planePosition(f.points[i]);
                            if (pp == 1)
                                 rightCnt++;
                        }
                        if (rightCnt == 0)
                        {   int v1Index = Facet3D.containsVertex(f, v1);
                            Vector3D nextPoint = 
                                f.points[(v1Index + 1) % f.numPoints];
                            int npp = plane.planePosition(nextPoint);
                            if (npp == 0)
                            {   if (replacesOrigObject(f))
                                {
                                found = true;
                                leftFacet = f;
                                }
                            }
                        }
                        fCount++;
                    }
                
                    // index v1 in leftFacet
                    int v1Index = Facet3D.containsVertex(leftFacet, v1);
                    // if this cut point has a label, it is inside leftFacet
                    if (leftFacet.vertexLabels[v1Index] != null)
                    {   // add the label
                    	orientedCutLabels.addElement(leftFacet.vertexLabels[v1Index]);
                    }
                    else // no label
                    {   // to be labeled later
                    	if (vertexOnOrigObject(v1, leftFacet))
                    		orientedCutLabels.addElement("");
                    	else // skip later
                    		orientedCutLabels.addElement("XX");
                    }    
                    // index next point
                    Vector3D v2 = leftFacet.points[(v1Index + 1) % leftFacet.numPoints];
                    startIndex = indexOf(v2, cut.vertices);
                    // loop terminates if 
                    // verticesUsed[startIndex] = true
                } // while for one cut-facet
                        
                int[] finalCutInds = new int[indCnt];
                for (int k = 0; k < indCnt; k++)
                    finalCutInds[k] = newInds[k];
                    
                Facet3D cutFacet = new Facet3D(cut.vertices, 
                    finalCutInds, DrawConstants.planeColor);
                Facet3D.copyAttributes(leftFacet, cutFacet, false);
                // override!!
                cutFacet.color = DrawConstants.planeColor;
                
                for (int i = 0; i < cutFacet.numPoints; i++)
                    cutFacet.vertexCodes[i] = - 1; 
                // locally override outline to no outline
                for (int i = 0; i < cutFacet.numPoints; i++)
                    cutFacet.edgeCodes[i] = -1;
                cut.addFacet(cutFacet);    
                
            } // while multicut                

            // set properties of cut and create it as an object
            cut.outlined = false; 
            cut.setFilled(false); 
            cut.visible = origObjectGroup.visible;
            // fix center and diameter as of originalObject
            cut.initObject3D(true, new Vector3D(origObjectGroup.center),
                         origObjectGroup.diameter, false);
                         

            // now cut the cut with all higher planes and lines:
            // the plane through this cut has already cut all cuts higher up,
            // thus the intersection segment of this cut with a higher cut is part of 
            // the higher cut, and passes through 2 vertices of this cut; 
            // however, this cut was not cut along this intersection line
            // use construction
            Vector construction = null;
            if (origObjectGroup instanceof ObjectWithLine)
            	construction = ((ObjectWithLine) origObjectGroup).getConstruction();
            else if (origObjectGroup instanceof ObjectWithPlane)
            	construction = ((ObjectWithPlane) origObjectGroup).getConstruction();
            if (construction != null)
            {   for (int k = 0; k < construction.size(); k++)
            	{   Object ob = construction.elementAt(k);
            		// other planes
                	if (ob instanceof Plane3D)
                	{   
                		Plane3D pl = (Plane3D) ob;
                		cut = cutWithPlane(cut, pl, planeColorIndex);
                	} //if ob instanceof Plane3D
                	// lines
                	else if (ob instanceof Line3D)
                	{   Line3D li = (Line3D) ob;
                		// we already know the plane through the cut!
                		Plane3D testPlane = plane.copy();
                		int isType = Plane3D.intersectionType(li, testPlane);    
                		if (isType == 2)
                		{   
                			cut = cutWithLine(cut, li, DrawConstants.lineColorIndex);
                			for (int i = 0; i < origObjectGroup.numFacets; i++)
                			{   // shortcut
                				Facet3D f = origObjectGroup.facets[i];
                				// check for a segment
                				if (f.numPoints == 2)
                				{   
                					boolean hasRep = false;
                					if (origObjectGroup instanceof ObjectWithPlane)
                						hasRep = ((ObjectWithPlane) origObjectGroup).hasReplacement(f);
                					else if (origObjectGroup instanceof ObjectWithLine)
                						hasRep = ((ObjectWithLine) origObjectGroup).hasReplacement(f);
                					// segment was not replaced    
                					if (!hasRep)
                					{   Vector3D p1 = f.points[0];
                                    	Vector3D p2 = f.points[1];
                                    	boolean isOn = li.contains(p1) && li.contains(p2);
                                    	// segment on the line
                                    	// could be an extension
                                    	if (isOn)
                                    	{   for (int j = 0; j < cut.numFacets; j++)
                                        	{   if (Facet3D.edgeContainsDirSegment(cut.facets[j], p1, p2) >= 0)
                                            	{   f.visible = false;
                                                	f.vertexCodes[0] = -1;
                                                	f.vertexCodes[1] = -1;
                                            	}
                    
                                        	}
                                    	} // isOn   
                					} // !hasRep
                				} // numPoints == 2
                			} // for facets of origObjectGroup
                        
                			for (int j = 0; j < cut.numFacets; j++)
                			{   
                            
                				Facet3D cFacet = cut.facets[j];
                				for (int n = 0; n < cFacet.numPoints; n++)
                				{
                					Vector3D start = cFacet.points[n];
                					boolean edgeContains = false;
                					for (int i = 0; i < origObject.numFacets; i++)
                					{   edgeContains = edgeContains || 
                							(Facet3D.edgeContainsSegment(origObject.facets[i], start, start) >= 0);
                					}
                					boolean isOn = li.contains(start);                            
                					if (isOn)
                					{   if (edgeContains)
                							cFacet.vertexCodes[n] = -1;
                					}
                				}
                			}
                        
                        
                		} // if isType == 2
                		else
                		{
                		}
                	} //if ob instanceof Line3D   
            	}
        
            } // construction != null
        
            // 	puts cut at objects.elementAt(2)                         
        	addObject3D(cut);                         
        } // (cut.numVertices > 2) && makeCut
        
        // properties of this ObjectWithPlane        
        sortSubArrays = origObjectGroup.sortSubArrays;        
        outlined = origObjectGroup.outlined;
        setFilled(origObjectGroup.filled);
        visible = origObjectGroup.visible;
        // fix center and diameter
        initObject3D(true, new Vector3D(origObjectGroup.center),
                     origObjectGroup.diameter, false);
                     
        
        // RESTRICTIE EPN
        if (DoorzienGWT.version == DoorzienGWT.EPN)
        	hideNonOrigVertices();
        // EINDE RESTRICTIE epn

        inheritTickMarks();

        // lettering
		if (makeCut)
		{

			Vector newVertices = new Vector();
			int labelCnt = origObjectGroup.numVertexLabels;

			// label the vertices of the oriented cut if they do not yet have a label
			// add them to the list
 
			for (int oCnt = 0; oCnt < orientedCut.size(); oCnt++)
			{   Vector3D cVertex = (Vector3D) orientedCut.elementAt(oCnt);
				String oLabel = (String) orientedCutLabels.elementAt(oCnt);
				if (oLabel.equals(""))
				{   
					labelCnt++;
					oLabel = getLabel(labelCnt);
					orientedCutLabels.setElementAt(oLabel, oCnt);
					newVertices.addElement(cVertex);
				}
			}

			for (int fCnt = 0; fCnt < replacement.numFacets; fCnt++)
			{   
				for (int vCnt = 0; vCnt < replacement.facets[fCnt].numPoints; vCnt++)
				{   String aLabel = replacement.facets[fCnt].vertexLabels[vCnt];
					if ((replacement.facets[fCnt].vertexCodes[vCnt] >= 0) &&
							((aLabel == null) || aLabel.equals("")) 
						)
					{   // get the vertex
						Vector3D aVertex = replacement.facets[fCnt].points[vCnt];
						int index = newVertices.indexOf(aVertex);
						// first occurence of this unlabeled vertex
						if (index < 0)
						{   
							// give it a NEW label and add to the list
							labelCnt++;
							replacement.facets[fCnt].vertexLabels[vCnt] = getLabel(labelCnt);
							newVertices.addElement(aVertex);                    
						}
						// this is an unlabeled vertex which was given a label before
						else
						{   replacement.facets[fCnt].vertexLabels[vCnt] = 
						getLabel(origObjectGroup.numVertexLabels + index + 1);
						}
					}    
				} // for facetpoints
    
    
			} // for facets       
			numVertexLabels = labelCnt;
        
		} // if (makeCut)        
		else
			numVertexLabels = origObjectGroup.numVertexLabels;
    } // constructor   

    
    /**
     * given an index, return a label corresponding to this index as follows:
     * A corresponds to 1, Z corresponds to 26, AA corresponds to 27 etc.
     * @param i label index
     * @return the label
     */
    public static String getLabel(int i)
    {   String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int cycles = (i - 1) / 26;
        int character = (i - 1) % 26;
        String result = "";
        // assume maximum 26*26=276 labels
        if (cycles >= 1)
            result += alphabet.charAt(cycles - 1);
        result += alphabet.charAt(character);
    
        return result;
    }

    /**
     * given an arry of boolean, find the index of the
     * first false (if any) 
     * @param useArray bolean array to check
     * @return -1 (no false in the array) or the index of the first false
     */
    int firstNotUsed(boolean[] useArray)
    {   int result = -1;
        for (int i = 0; i < useArray.length; i++)
        {   if (!useArray[i])
                return i;    
        }
        return result;
    }    

    /**
     * given a vertex and an array of vertices, find the
     * index of the vertex in that array (if any)
     * @param v vertex to check
     * @param vert array to check
     * @return -1 (not in array) or the index in the array 
     */
    int indexOf(Vector3D v, Vector3D[] vert)
    {   int result = -1;
        for (int i = 0; i < vert.length; i++)
        {   if (Vector3D.equals(vert[i], v))
                return i;
        }
        return result;
    }    

    
    /**
     * check if repFacet is a facet of a cut  
     * @param repFacet facet to check
     * @return facet of the cut or null
     */
    public Facet3D replacesCut(Facet3D repFacet)
    {   // repFacet replaces nothing, thus cannot replace a cut
        if (repFacet.isReplacementOf == null)
            return null;
        Facet3D facet = repFacet;         
        // find top replacement
        while (facet.isReplacementOf != null)
            facet = facet.isReplacementOf;
        // find out what kind of object contains this facet 
        Object3D ob = objectContains(facet);
        if (ob == null)
        	return null;
        // not part of a group, then this must be origObject
        if (ob.parent == null)
            return null;
        else // the object is part of a group
        {   ObjectGroup3D obg = (ObjectGroup3D) ob.parent;
            if (obg instanceof ObjectWithPlane)
            {   // cut not empty
                if (obg.objects.size() > 2)
                {
                    Object3D cut = (Object3D) obg.objects.elementAt(2);
                    int index = cut.containsFacet(facet);
                    if (index >= 0)
                        return cut.facets[index];
                    else 
                        return null;
                }    
                else
                    return null;
            }
            else 
                return null;
        }
    }    

    /**
     * check if the point vertex is located on a line extension
     * @param vertex vertex to check
     * @return true/false
     */
    public boolean vertexOnLineExtension(Vector3D vertex)
    {   boolean isOnExt = false;
        boolean isOnInner = false;
        // check through all facets
        for (int i = 0; i < numFacets; i++)
        {   // facet should not have a replacement
            if (!hasReplacement(facets[i]))
            {   // check if facet contains the vertex
                if (Facet3D.containsVertex(facets[i], vertex) >= 0)
                {   if (replacesLineExtension(facets[i]) != null)
                        isOnExt = true;
                    else
                        isOnInner = true;
                }
            }
        }
        return isOnExt && !isOnInner;
    }    

    /**
     * check if the segment [eStart,eEnd] is located on a line extension
     * @param eStart start of segment to check
     * @param eEnd end of segment to check
     * @return true/false
     */
    public boolean edgeOnLineExtension(Vector3D eStart, Vector3D eEnd)
    {   boolean isOnExt = false;
        // check through all facets
        for (int i = 0; i < numFacets; i++)
        {   // facet should not have a replacement
            if (!hasReplacement(facets[i]))
            {   // check if facet contains the vertex
                if (Facet3D.containsEdge(facets[i], eStart, eEnd) >= 0)
                {   if (replacesLineExtension(facets[i]) != null)
                        isOnExt = true;
                }
            }
        }
        return isOnExt;
    }    

    /**
     * recursively get the line extension factor this ObjectWithPlane  
     * @return the line extension factor
     */
    public double getLlFactor()
    {   if (origObjectGroup instanceof ObjectWithLine)
            return ((ObjectWithLine) origObjectGroup).getLlFactor();
        else if (origObjectGroup instanceof ObjectWithPlane)
            return ((ObjectWithPlane) origObjectGroup).getLlFactor();    
        else
            return 0;
    }
    
    /**
     * check if repFacet (assumed a  segment) is a line extension or replaces
     * a line extension
     * @param repFacet segment to check
     * @return true/false
     */
    public Facet3D replacesLineExtension(Facet3D repFacet)
    {   
        Facet3D facet = repFacet;         
        // find top replacement
        while (facet.isReplacementOf != null)
            facet = facet.isReplacementOf;
        // find out what kind of object contains this facet 
        Object3D ob = objectContains(facet);
        // not part of a group, then this must be origObject
        if (ob.parent == null)
            return null;
        else // the object is part of a group
        {   ObjectGroup3D obg = (ObjectGroup3D) ob.parent;
            if (obg instanceof ObjectWithLine)
            {   // line extension not empty
                if (obg.objects.size() > 2)
                {
                    Object3D ext = (Object3D) obg.objects.elementAt(2);
                    int index = ext.containsFacet(facet);
                    if (index >= 0)
                        return ext.facets[index];
                    else // keep looking
                        return null;
                }    
                else
                    return null;
            }
            else // keep looking
                return null;
        }
    }    
    
    /**
     * check if Facet3D repFacet ultimately replaces a facet of origObject
     * (it could also be a facet of some cut)
     * @param repFacet the Facte3D to be checked
     * @return true/false
     */
    public boolean replacesOrigObject(Facet3D repFacet)
    {   Facet3D facet = repFacet;         
        // find top replacement or continue with the facet itself
        while (facet.isReplacementOf != null)
            facet = facet.isReplacementOf;
        // find out what kind of object contains facet
        Object3D ob = objectContains(facet);
        if (ob == origObject)
            return true;
        else
            return false;
    }    

    
    /**
     * if there are tick marks on the edges of the facets and a facet is
     * replaced (by two facets), inherit these tick marks to the
     * replacint facets  
     */
    public void inheritTickMarks()
    {   // nothing to do
        if (DrawConstants.TICKNUM == 0)
            return; 
        for (int fCnt = 0; fCnt < replacement.numFacets; fCnt++)
        {   Facet3D repF = replacement.facets[fCnt];
            Facet3D f = repF.isReplacementOf;
            if (f != null)
            {
                for (int eCnt = 0; eCnt < repF.numPoints; eCnt++)
                {   Vector3D eStart = repF.points[eCnt];
                    Vector3D eEnd = repF.points[(eCnt + 1) % repF.numPoints];
                    // check if f contains the whole segment
                    int eIndex = Facet3D.containsEdge(f, eStart, eEnd);
                    if (eIndex >= 0)
                    {   repF.drawTicks[eCnt] = f.drawTicks[eIndex];
                        repF.numTicks[eCnt] = f.numTicks[eIndex];
                        if (repF.numTicks[eCnt] > 0)
                            repF.tickStart[eCnt] = new Vector3D(f.tickStart[eIndex]);
                        if (repF.numTicks[eCnt] > 1)
                            repF.tickStep[eCnt] = new Vector3D(f.tickStep[eIndex]);
                    }
                    else // check if f contains edge as subsegment
                    {   eIndex = Facet3D.edgeContainsDirSegment(f, eStart, eEnd);
                        if (eIndex >= 0)
                        {   
                            if (f.numTicks[eIndex] == 0)
                            {   repF.drawTicks[eCnt] = false;
                                repF.numTicks[eCnt] = 0;
                            }    
                            else
                            {   // generate the tick marks of edge eIndex of f
                                Vector3D[] fTicks = new Vector3D[f.numTicks[eIndex]];
                                fTicks[0] = f.tickStart[eIndex];
                                if (f.numTicks[eIndex] > 1)
                                {   fTicks[1] = f.tickStep[eIndex];
                                    Vector3D tick = Vector3D.minus(fTicks[1], fTicks[0]);
                                    Vector3D lastTick = new Vector3D(fTicks[1]); 
                                    // create next tickmarks (if any)
                                    for (int sCnt = 2; sCnt < f.numTicks[eIndex]; sCnt++)
                                    {   Vector3D nextTick = Vector3D.plus(lastTick, tick);
                                        fTicks[sCnt] = nextTick;
                                        lastTick = new Vector3D(nextTick);
                                    }
                                }
                                // put them "on top of" the subsegment
                                int firstIndex = -1;
                                int lastIndex = -1;
                                for (int tCnt = 0; tCnt < f.numTicks[eIndex]; tCnt++)
                                {   if (Line3D.segmentContainsPoint(eStart, eEnd, fTicks[tCnt]))
                                    {   
                                        if (firstIndex < 0)
                                        {   firstIndex = tCnt;
                                            lastIndex = tCnt;
                                        }    
                                        else // first point found, set last
                                        {   lastIndex = tCnt;
                                        }    
                                    }    
                                }
                                repF.drawTicks[eCnt] = f.drawTicks[eIndex];
                                if (firstIndex < 0)
                                    repF.numTicks[eCnt] = 0;
                                else    
                                {    repF.numTicks[eCnt] = lastIndex - firstIndex + 1;
                                     repF.tickStart[eCnt] = new Vector3D(fTicks[firstIndex]);
                                     if (repF.numTicks[eCnt] > 1)
                                        repF.tickStep[eCnt] = new Vector3D(fTicks[firstIndex + 1]);
                                }
                            }
                        }
                        // else do nothing
                    }    
                } // for vertices
            }  // if f != null  
        } // for facets
    }

    // RESTRICTIE TOT ORIGINAL OBJECT VOOR EPN
    /**
     * check if 1) facet is a facet of the original object <br>
     * or 2) vertex is a vertex of the original object<br>
     * or 3) vertex is on an edge of a facet of the original object 
     * @param vertex vertex to be checked
     * @param facet facet to be checked
     * @return true/false
     */
    public boolean vertexOnOrigObject(Vector3D vertex, Facet3D facet)
    {   boolean isOnOrig = false;
        if (origObject.containsFacet(facet) >= 0)
            return true;
        if (origObject.containsVertex(vertex) >= 0)
            return true;        
        for (int i = 0; i < origObject.numFacets; i++)
        {   // check if facet contains the point
            if (Facet3D.edgeContainsPoint(origObject.facets[i], vertex) >= 0)
                return true;
        }
        return isOnOrig;
    }
    /**
     * check if 1) facet is a facet of the original object <br>
     * or 2) the segment [eStart,eEnd] is part of an edge of the original object
     * @param eStart start of segment to check
     * @param eEnd end of segment to check
     * @param facet facet to check
     * @return true/false
     */
    public boolean edgeOnOrigObject(Vector3D eStart, Vector3D eEnd, Facet3D facet)
    {   boolean isOnOrig = false;
        if (origObject.containsFacet(facet) >= 0)
            return true;
        // check through all facets
        for (int i = 0; i < origObject.numFacets; i++)
        {   // check if facet contains the edge
            if (Facet3D.edgeContainsSegment(origObject.facets[i], eStart, eEnd) >= 0)
            {    return true;
            }
        }
        return isOnOrig;
    }    
    
    /**
     * set the vertexCode of any vertex that is not on the original
     * object to -1, so that this vertex is not visible 
     */
    public void hideNonOrigVertices()
    {   
        fixFacetArray();
        for (int i = 0; i < numFacets; i++)
        {   // facet must not have a replacement
            if (!hasReplacement(facets[i]))
            {   if (!replacesOrigObject(facets[i]))
                {   
                    for (int j = 0; j < facets[i].numPoints; j++)
                    {   facets[i].vertexCodes[j] = -1;
                    }
                }
                else
                {   for (int j = 0; j < facets[i].numPoints; j++)
                    {   if (!vertexOnOrigObject(facets[i].points[j], facets[i]))
                            facets[i].vertexCodes[j] = -1;
                    }
                }    
            }        
        }
    }    
    // EINDE RESTRICTIE TOT ORIGINAL OBJECT VOOR EPN

    /**
     * redefined from ObjectGroup3D, do not fill/unfill cuts (if the objectgroup is
     * filled, cuts are not visible, is the objectgroup is not filled, cuts should
     * be filled)  
     */
    public void setFilled(boolean fill)
    {   // this group
        filled = fill;
        origObjectGroup.setFilled(fill);
        for (int i = 0; i < replacement.numFacets; i++)
        {   
            if (!hasReplacement(replacement.facets[i]))
            {
                
            	// for EPN, unhide points on a line/plane intersection                
            	if (DoorzienGWT.version == DoorzienGWT.EPN)
            	{
            		for (int vCnt = 0; vCnt < replacement.facets[i].numPoints; vCnt++)
            		{   if ((replacement.facets[i].vertexLabels[vCnt] != null) &&
            				replacement.facets[i].vertexLabels[vCnt].equals("XX")
            				)
            			if (!filled)
            				replacement.facets[i].vertexCodes[vCnt] = DrawConstants.planeOutlineColorIndex + Facet3D.HIDDENSHIFT;
            		}    
            	}                
                
                if (replacesOrigObject(replacement.facets[i]))
                {   
                    // this should always be the case
                    if (replacement.facets[i].numPoints > 2)
                    {   if (filled)
                        {   replacement.facets[i].filled = filled;
                            replacement.facets[i].color = DrawConstants.objectColor;
                        }
                        else // surrounding object will not be filled    
                        {   if (cutFilled)
                            {   
                        		// could be collinear!                                
                                Plane3D fPlane = new Plane3D(
                                    replacement.facets[i].normal.x,
                                    replacement.facets[i].normal.y,
                                    replacement.facets[i].normal.z,
                                    Vector3D.dotProduct(
                                        replacement.facets[i].normal,
                                        replacement.facets[i].points[0]));  

                                Vector construct = getConstruction();
                                boolean isInPlane = false;
                                for (int cnt = 0; cnt < construct.size(); cnt++)
                                {   Object conObj = construct.elementAt(cnt);
                                    if (conObj instanceof Plane3D)
                                    {   isInPlane = isInPlane ||
                                            fPlane.equals((Plane3D) conObj);
                                    }    
                                
                                }
                                if (isInPlane)
                                {
                                    replacement.facets[i].filled = true;
                                    replacement.facets[i].color = DrawConstants.planeColor;
                                }
                                else
                                {   replacement.facets[i].filled = false;
                                    replacement.facets[i].color = DrawConstants.planeColor;
                                }
                            }    
                            else
                                replacement.facets[i].filled = false;            
                        }    
                    }
                    
                } // replacesOrigObject
                
                else if (replacesCut(replacement.facets[i]) != null)
                {   if (filled)
                    // now this thing is never filled and even invisible if
                    // the "surrounding object" is 
                        replacement.facets[i].visible = false;
                    else // surrounding object will not be filled    
                    {   replacement.facets[i].visible = true;
                        if (cutFilled)
                        {   replacement.facets[i].color = DrawConstants.planeColor; 
                            replacement.facets[i].filled = true; // testing filled cuts
                        }
                        else    
                        {    replacement.facets[i].filled = false;            
                        }
                    }   
                }
                // internal segment
                else if ((replacement.facets[i].numPoints == 2) &&
                         (replacesLineExtension(facets[i]) == null)
                         //facets[i].normal.equals(new Vector3D(0,0,0))
                         )
                {
                   // now this thing is never filled and even invisible if
                   // the "surrounding object" is 
                   replacement.facets[i].visible = !filled;
                }   

            } // if !hasReplacement
        } // for replacement.facets
        
        for (int j = 0; j < cut.numFacets; j++)
        {   if (!hasReplacement(cut.facets[j]))
            {
        		// surrounding object will be filled, do not fill the cut
            	if (filled)
            		cut.facets[j].visible = false;
            	else // surrounding object will not be filled    
            	{   cut.facets[j].visible = true;
                	if (cutFilled)
                	{   
                		cut.facets[j].color = DrawConstants.planeColor;
                		cut.facets[j].filled = true; // testing filled cuts
                	}    
                	else    
                		cut.facets[j].filled = false;            
            	}
            }
        }
    }
    
    /**
     * find the top (largest) ObjectGroup3D in the tree,
     * create its facet array and check if any of these 
     * facets is replacing Facet3D f
     * @param f Facet3D to be checked for replacement
     * @return true/false
     */
    public boolean hasReplacement(Facet3D f)
    {   Object3D top = topParent();
        top.fixFacetArray();
        boolean result = false;
        for (int i = 0; i < top.numFacets; i++)
        {   if ((top.facets[i].isReplacementOf != null) &&
                (top.facets[i].isReplacementOf == f)
               )
            return true;   
    
        }
        return result;
    }
    /**
     * find all all facets replacing Facet3D f which themselves are not replaced
     * (ultimate replacements
     * @param f Facet3D whose ultimate replacements should be found
     * @return Vector containing all ultimate replacements
     */
    public Vector getReplacements(Facet3D f)
    {   Vector result = new Vector();
        findReplacements(f, result);
        return result;
    }    
    
    /**
     * find the top (largest) ObjectGroup3D in the tree,
     * create its facet array and find recursively all facets
     * replacing Facet3D f which themselves are not replaced
     * @param f Facet3D to be checked
     * @param replacements Vector to which facets replacing Facet3D f
     * are added, necessary for recursion
     */
    public void findReplacements(Facet3D f, Vector replacements)
    {   Object3D top = topParent();
        top.fixFacetArray();
        for (int i = 0; i < top.numFacets; i++)
        {   // find a facet replacing f
            if ((top.facets[i].isReplacementOf != null) &&
                (top.facets[i].isReplacementOf == f)
               )
            {   // if this is not replaced again, include it
                if (!hasReplacement(top.facets[i]))   
                    replacements.addElement(top.facets[i]);
                else // topfacets[i] is again replaced, find its replacements
                    findReplacements(top.facets[i], replacements);
            }
        }
    }
    
    
    /**
     * recursively set cutFilled in the whole OWP/OWL hierarchy
     * @param b true/false
     */
    public void fillCuts(boolean b)
    {   cutFilled = b;
        if (origObjectGroup instanceof ObjectWithPlane)
            ((ObjectWithPlane) origObjectGroup).fillCuts(b);
        else if (origObjectGroup instanceof ObjectWithLine)
            ((ObjectWithLine) origObjectGroup).fillCuts(b);        
        // else do nothing    
    }    
    
    
    /**
     * check if the construction recipe of this OWP contains line l
     * @param l the line
     * @return true/false
     */
    public boolean containsLine(Line3D l)
    {    Vector construction = getConstruction();
         boolean result = false;
         for (int i = 0; i < construction.size(); i++)
         {  Object ci = construction.elementAt(i);
            if (ci instanceof Line3D)
            {   Line3D line = (Line3D) ci;
                if (line.equals(l))
                    return true;
            }    
         }
         return result;
    }

    /**
     * check if the construction recipe of this OWP contains plane p
     * @param p the plane
     * @return true/false
     */
    public boolean containsPlane(Plane3D p)
    {    Vector construction = getConstruction();
         boolean result = false;
         for (int i = 0; i < construction.size(); i++)
         {  Object ci = construction.elementAt(i);
            if (ci instanceof Plane3D)
            {   Plane3D plane = (Plane3D) ci;
                if (plane.equals(p))
                    return true;
            }    
         }
         return result;
    }

    /**
     * recursively find the recipe for this OWP; note that
     * the list is in reverse order (last operation first in list)
     * @param recipe Vector containing planes and lines used for recursion
     */
    public void getConstructionList(Vector recipe)
    {   recipe.addElement(plane.copy());
        if (origObjectGroup instanceof ObjectWithPlane)
            ((ObjectWithPlane) origObjectGroup).getConstructionList(recipe);
        else if (origObjectGroup instanceof ObjectWithLine)
            ((ObjectWithLine) origObjectGroup).getConstructionList(recipe);        
        else if (origObjectGroup instanceof ObjectWithPoint)
            ((ObjectWithPoint) origObjectGroup).getConstructionList(recipe);        
        // else do nothing    
    }    
    
    /**
     * get the final construction sequence, that is find the reverse
     * list by recursion and reverse it
     * @return final recipe
     */
    public Vector getConstruction()
    {   Vector recipe = new Vector();
        getConstructionList(recipe);
        Vector result = new Vector();
        for (int i = 0; i < recipe.size(); i++)
            result.addElement(recipe.elementAt(recipe.size() - 1 - i));
        return result;    
    }

    /**
     * recursively find the color recipe for this OWP; note that
     * the list is in reverse order (last operation first in list)
     * @param recipe Vector containing plane and line colors used for recursion
     */
    public void getConstructionColorList(Vector recipe)
    {   recipe.addElement(new Integer(planeColorIndex));
        if (origObjectGroup instanceof ObjectWithPlane)
            ((ObjectWithPlane) origObjectGroup).getConstructionColorList(recipe);
        else if (origObjectGroup instanceof ObjectWithLine)
            ((ObjectWithLine) origObjectGroup).getConstructionColorList(recipe);        
        else if (origObjectGroup instanceof ObjectWithPoint)
            ((ObjectWithPoint) origObjectGroup).getConstructionColorList(recipe);        
            
        // else do nothing    
    }    

    /**
     * get the final constructionColor sequence, that is find the reverse
     * list by recursion and reverse it
     * @return final recipe
     */
    public Vector getConstructionColors()
    {   Vector recipe = new Vector();
        getConstructionColorList(recipe);
        Vector result = new Vector();
        for (int i = 0; i < recipe.size(); i++)
            result.addElement(recipe.elementAt(recipe.size() - 1 - i));
        return result;    
    }
    
    /**
     * make a deep copy of this ObjetWithPlane; might not work
     * correctly (not tested); use method rebuild instead 
     */
    public Object3D deepCopy()
    {   // find the indices of the replaced facets
        // first find the facets which were replaced by
        // the facets in objects.elementAt(1)
        int[] replacedIndices = new int[replacement.numFacets];
        for (int i = 0; i < replacement.numFacets; i++)
        {   Facet3D fReplaced = replacement.facets[i].isReplacementOf;
            if (fReplaced != null)
            {
                int index = origObjectGroup.containsFacet(fReplaced);
                // just in case
                if (index >= 0)
                    replacedIndices[i] = index;
                else
                    replacedIndices[i] = -1;
            }    
            else
                replacedIndices[i] = -1;
        }    
        // make a deep group copy
        ObjectWithPlane copy = new ObjectWithPlane();
        makeDeepGroupCopy(copy);
        copy.origObjectGroup = (ObjectGroup3D) copy.objects.elementAt(0);
        copy.origObject = copy.origObjectGroup.leftMostLeaf();                
        if (copy.objects.size() >= 2)
            copy.replacement = (Object3D) copy.objects.elementAt(1);
        else
            copy.replacement = new EmptyObject3D();
        if (copy.objects.size() >= 3)
            copy.cut = (Object3D) copy.objects.elementAt(2);
        else
            copy.cut = new EmptyObject3D();
        copy.planeColorIndex = planeColorIndex;
        // constructor of Plane3D deep copies line.point1 and line.point2
        copy.plane = plane.copy();                
        // fix the facet array, since nothing drawn yet!        
        copy.fixFacetArray();
        // note: replacedIndices.length = copy.replacement.numFacets
        for (int j = 0; j < replacedIndices.length; j++)
        {   int index = replacedIndices[j];
            if (index >= 0)
            {   copy.replacement.facets[j].isReplacementOf =
                    copy.origObjectGroup.facets[index];
            }    
            else
                copy.replacement.facets[j].isReplacementOf = null;                
        }
        return copy;          
    }   
    
    /**
     * trick: to cut a Facet3D with a plane, turn the Facet3D into an Object3D and cut it with the plane;
     * @param facet Facet3D to be cut
     * @param plane plane to cut with
     * @param cutColorIndex outline color of facets in cut  
     * @return the replacement Object3d, this will contain 2 facets
     */
    public static Object3D cutFacetWithPlane(Facet3D facet, Plane3D plane, int cutColorIndex)
    {   // turn facet into an object
        Object3D fObject = new EmptyObject3D();
        fObject.numVertices = facet.numPoints;
        fObject.vertices = new Vector3D[fObject.numVertices];
        fObject.trVertices = new Vector3D[fObject.numVertices];
        for (int i = 0; i < facet.numPoints; i++)
            fObject.vertices[i] = new Vector3D(facet.points[i]);
        fObject.numFacets = 1;
        fObject.facets = new Facet3D[fObject.numFacets];
        int[] newInds = new int[fObject.numVertices];
        for (int j = 0; j < fObject.numVertices; j++)
            newInds[j] = j;
        Facet3D oFacet = new Facet3D(fObject.vertices, newInds, facet.color);
        fObject.facets[0] = oFacet;
        Facet3D.copyAttributes(facet, oFacet, true);
        fObject.filled = oFacet.filled; //!!!
        // do not center, diameter is irrelevant
        fObject.initObject3D(true, false);
        // dummy object group
        ObjectGroup3D fObjectGroup = new ObjectGroup3D(fObject, false);   
        fObjectGroup.filled = fObject.filled;
        fObjectGroup.fixFacetArray(); //!!!
        // do not make a cut, we are only interested in owp's replacement 
        ObjectWithPlane owp = new ObjectWithPlane(fObjectGroup, 
            plane.support, 
            Vector3D.plus(plane.support, plane.direction1),
            Vector3D.plus(plane.support, plane.direction2), 
            cutColorIndex, false);    
        // now the only interesting part of owp is replacement
        return owp.replacement;
    }

    
    /**
     * given the cut (an Object3D lying in a plane), cut it with a plane: so turn it into
     * an ObjectGroup3D, and cut this with the plane; the result is a new Object3D 
     * consisting of the facets not hit by the plane (those which are not replaced)
     * and the facets being replaced    
     * @param cut Object3D to be cut
     * @param plane plane to cut by
     * @param cutColorIndex outline color of facets in cut
     * @return the cut Object3D
     */
    public static Object3D cutWithPlane(Object3D cut, Plane3D plane, int cutColorIndex)
    {   
        // dummy object group
        ObjectGroup3D cutObjectGroup = new ObjectGroup3D(cut, false);   
        cutObjectGroup.filled = cut.filled; 
        cutObjectGroup.fixFacetArray(); //!!!
        // do not make a cut, we are only interested in owp's replacement
        ObjectWithPlane owp = new ObjectWithPlane(cutObjectGroup, 
            plane.support, 
            Vector3D.plus(plane.support, plane.direction1),
            Vector3D.plus(plane.support, plane.direction2), 
            cutColorIndex, false);    
        Object3D cutCut = new EmptyObject3D();
        int firstIndex = cutCut.numVertices;
        for (int m = 0; m < owp.origObjectGroup.numFacets; m++)
        {   // shortcut
            Facet3D oFacet = owp.origObjectGroup.facets[m];
            if (!owp.hasReplacement(oFacet))
            {   
                firstIndex = cutCut.numVertices;
                // add vertices of oFacet
                for (int i = 0; i < oFacet.numPoints; i++)
                    cutCut.addVertex(new Vector3D(oFacet.points[i]), null);
                // add oFacet, no need to make a new one?
                // 1) make new indices and replace
                int[] newInds = new int[oFacet.numPoints];
                for (int k = 0; k < oFacet.numPoints; k++)
                    newInds[k] = k + firstIndex;
                oFacet.indices = newInds;    
                // 2) update points (just in case)
                oFacet.updatePoints(cutCut.vertices);
                // 2a add facet
                cutCut.addFacet(oFacet);
            } // if !hasReplacement
        } // for for facets of owp.originalObjectGroup    
        
        for (int n = 0; n < owp.replacement.numFacets; n++)
        {   
            // shortcut
            Facet3D rFacet = owp.replacement.facets[n];
            firstIndex = cutCut.numVertices;
            // add vertices of oFacet
            for (int i = 0; i < rFacet.numPoints; i++)
                cutCut.addVertex(new Vector3D(rFacet.points[i]), null);
            // add rFacet, no need to make a new one?
            // 1) make new indices and replace
            int[] newInds = new int[rFacet.numPoints];
            for (int k = 0; k < rFacet.numPoints; k++)
                newInds[k] = k + firstIndex;
            rFacet.indices = newInds;    
            // 2) update points (just in case)
            rFacet.updatePoints(cutCut.vertices);
            // 2a add facet
            cutCut.addFacet(rFacet);
            // 3) set isReplacementOf to null
            rFacet.isReplacementOf = null;
            // red vertex/edgecodes shoud be set to hidden
            for (int pCnt = 0; pCnt < rFacet.numPoints; pCnt++)
            {   if (rFacet.edgeCodes[pCnt] == cutColorIndex)
                    rFacet.edgeCodes[pCnt] += 10;     
                if (rFacet.vertexCodes[pCnt] == cutColorIndex)
                    rFacet.vertexCodes[pCnt] += 10;         
            }  // for edgecodes  

        } // for for facets of owp.replacement

        // set properties of cutCut
        cutCut.outlined = cut.outlined; 
        cutCut.filled = cut.filled; 
        cutCut.visible = cut.visible;
        // fix center and diameter as of cut
        cutCut.initObject3D(true, new Vector3D(cut.center),
                            cut.diameter, false);
        return cutCut;
    }

    /**
     * given the cut (an Object3D, lying in a plane), cut it with a line which lies in that plane: 
     * so turn it into an ObjectGroup3D,and cut this with the line; the result is a new Object3D
     * consisting of the facets not hit by the line (those which are not replaced) and the facets
     * being replaced    
     * @param cut Object3D to be cut
     * @param line line to cut by
     * @param lineColorIndex outline color of edges on line
     * @return the cut Object3D
     */
    public static Object3D cutWithLine(Object3D cut, Line3D line, int lineColorIndex)
    {
        // dummy object group
        ObjectGroup3D cutObjectGroup = new ObjectGroup3D(cut, false);   
        cutObjectGroup.filled = cut.filled; // niet relevant?
        cutObjectGroup.fixFacetArray(); //!!!
        // do not make extensions, these are already elsewhere
        ObjectWithLine owl = new ObjectWithLine(cutObjectGroup, 
            line.point1, line.point2, lineColorIndex, -1);    
        Object3D cutCut = new EmptyObject3D();
        int firstIndex = cutCut.numVertices;
        for (int m = 0; m < owl.origObjectGroup.numFacets; m++)
        {   // shortcut
            Facet3D oFacet = owl.origObjectGroup.facets[m];
            if (!owl.hasReplacement(oFacet))
            {   firstIndex = cutCut.numVertices;
                // add vertices of oFacet
                for (int i = 0; i < oFacet.numPoints; i++)
                    cutCut.addVertex(new Vector3D(oFacet.points[i]), null);
                // add oFacet, no need to make a new one?
                // 1) make new indices and replace
                int[] newInds = new int[oFacet.numPoints];
                for (int k = 0; k < oFacet.numPoints; k++)
                    newInds[k] = k + firstIndex;
                oFacet.indices = newInds;    
                // 2) update points (just in case)
                oFacet.updatePoints(cutCut.vertices);
                // 2a add facet
                cutCut.addFacet(oFacet);
            } // if !hasReplacement
        } // for for facets of owp.originalObjectGroup    
        
        for (int n = 0; n < owl.replacement.numFacets; n++)
        {   // shortcut
            Facet3D rFacet = owl.replacement.facets[n];
            firstIndex = cutCut.numVertices;
            // add vertices of oFacet
            for (int i = 0; i < rFacet.numPoints; i++)
                cutCut.addVertex(new Vector3D(rFacet.points[i]), null);
            // add rFacet, no need to make a new one?
            // 1) make new indices and replace
            int[] newInds = new int[rFacet.numPoints];
            for (int k = 0; k < rFacet.numPoints; k++)
                newInds[k] = k + firstIndex;
            rFacet.indices = newInds;    
            // 2) update points (just in case)
            rFacet.updatePoints(cutCut.vertices);
            // 2a add facet
            cutCut.addFacet(rFacet);
            // set edgecodes with blue (lineColor) to hidden
            for (int pCnt = 0; pCnt < rFacet.numPoints; pCnt++)
            {   if (rFacet.edgeCodes[pCnt] == lineColorIndex)
                    rFacet.edgeCodes[pCnt] += 10;     
            }  // for edgecodes  
            // set vertexcodes with blue (lineColor) to hidden
            for (int pCnt = 0; pCnt < rFacet.numPoints; pCnt++)
            {   if (rFacet.vertexCodes[pCnt] == lineColorIndex)
                    rFacet.vertexCodes[pCnt] += 10;     
            }  // for vertexcodes  
            // 3) set isReplacementOf to null
            rFacet.isReplacementOf = null;
        } // for for facets of owl.replacement

        // set properties of cutCut
        cutCut.outlined = cut.outlined; 
        cutCut.filled = cut.filled; 
        cutCut.visible = cut.visible;
        // fix center and diameter as of cut
        cutCut.initObject3D(true, new Vector3D(cut.center),
                            cut.diameter, false);
        return cutCut;
    }

    /**
     * given an ObjectGroup3D which must contain plane, find all facets which are not
     * replaced and which lie in the plane (these are the facets contained in the cut by the plane),
     * copy them into a separate Object3D and rotate this Object3D so that it will be a flat
     * Object3D
     * @param ob Objectgroup3D containing plane
     * @param plane plane for cut
     * @return flat cut by plane as ObjectGroup3D
     */
    public static ObjectGroup3D getCut(ObjectGroup3D ob, Plane3D plane)
    {   Object3D cut = new EmptyObject3D();
        ob.fixFacetArray();
        for (int i = 0; i < ob.numFacets; i++)
        {   boolean hasReplacement = false;
            if (ob instanceof ObjectWithLine)
                hasReplacement = ((ObjectWithLine) ob).hasReplacement(ob.facets[i]);
            else if (ob instanceof ObjectWithPlane)
                hasReplacement = ((ObjectWithPlane) ob).hasReplacement(ob.facets[i]);
            if (!hasReplacement)
            {   boolean inPlane = false;
                if (ob.facets[i].numPoints == 2) // should not happen?
                    inPlane = plane.contains(ob.facets[i].points[0]) &&
                              plane.contains(ob.facets[i].points[1]);
                else if (ob.facets[i].numPoints > 2) 
                {   Plane3D obPlane = new Plane3D(
                        ob.facets[i].normal.x, ob.facets[i].normal.y,
                        ob.facets[i].normal.z,
                        Vector3D.dotProduct(ob.facets[i].normal,
                            ob.facets[i].points[0]));
                    inPlane = plane.equals(obPlane);        
                    // carefull, first three points of facet could be collinear!                    
                }              
                if (inPlane)
                {   
                    int firstIndex = cut.numVertices;
                    for (int j = 0; j < ob.facets[i].numPoints; j++)
                        cut.addVertex(new Vector3D(ob.facets[i].points[j]), null);
                    int[] inds = new int[ob.facets[i].numPoints];
                    for (int k = 0; k < ob.facets[i].numPoints; k++)
                        inds[k] = k + firstIndex;
                    Facet3D cutFacet = new Facet3D(cut.vertices, inds, ob.facets[i].color);
                    cut.addFacet(cutFacet);
                    Facet3D.copyAttributes(ob.facets[i], cutFacet, true);
                    cutFacet.visible = true;
                    cutFacet.filled = false;
                    // update not outlined
                    for (int m = 0; m < cutFacet.numPoints; m++)
                    {    if (cutFacet.edgeCodes[m] < 0)
                            cutFacet.edgeCodes[m] = DrawConstants.planeOutlineColorIndex + 40;
                         // override color through normal   
                         else if ((cutFacet.edgeCodes[m] >= 0) && (cutFacet.edgeCodes[m] < 10))     
                            cutFacet.edgeCodes[m] += 40;
                         // override externally hidden   
                         else if ((cutFacet.edgeCodes[m] >= 10) && (cutFacet.edgeCodes[m] < 20))  
                            cutFacet.edgeCodes[m] += 30;                         
                    }
                } // if inPlane
            } // if !hasReplacement   
        } // for facets   

        // vertexLabels for the cut
        String[] cutLabels = new String[cut.numVertices];
        for (int vCnt = 0; vCnt < cut.numVertices; vCnt++)
        {   Vector3D aVertex = cut.vertices[vCnt];
            Vector obFacets = ob.facetsContaining(aVertex);
            for (int fCnt = 0; fCnt < obFacets.size(); fCnt++)
            {   Facet3D aFacet = (Facet3D) obFacets.elementAt(fCnt);
                int index = Facet3D.containsVertex(aFacet, aVertex);
                if (index >= 0)
                {   if (aFacet.vertexLabels[index] != null)
                        cutLabels[vCnt] = new String(
                            aFacet.vertexLabels[index]);
                }    
        
            }
        }
        
        for (int cfCnt = 0; cfCnt < cut.numFacets; cfCnt++)
        {   for (int fvCnt = 0; fvCnt < cut.facets[cfCnt].numPoints; fvCnt++)
            {   cut.facets[cfCnt].vertexLabels[fvCnt] =
                    cutLabels[cut.facets[cfCnt].indices[fvCnt]];
            }    
        }    

       // fixing the cut object

       Plane3D cutPlane = plane.copy();                                       
       // normal of whole cut
       Vector3D cutNormal = new Vector3D(cutPlane.normal);

       // take the plane z = 0 in world space
       Plane3D parPlane = new Plane3D(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0),
                                      new Vector3D(0, 1, 0));

       // find intersectionline (if any)
       int isType = Plane3D.intersectionType(parPlane, cutPlane);
       Line3D axis = null;
       if (isType == 1)
       {    axis = Plane3D.getIntersectionLine(parPlane, cutPlane);
       }
      
       double cp = Vector3D.dotProduct(cutNormal, new Vector3D(0, 0, 1));
       // between 0 and pi
       double angle = Math.acos(cp);

       // rotate                                       
       if (axis != null)
       {
            // this is the correct axis            
            // since it is orthogonal to (0, 0, 1) and cutNormal;
            Line3D zeroAxis = new Line3D(axis.direction, new Vector3D(0,0,0));            
            Vector3D rotNormal = zeroAxis.rotateBy(cutNormal, angle);            
            // if wrong angle, take minus
            if (!rotNormal.equals(new Vector3D(0, 0, 1)))
            {    angle = - angle;
            }            
            for (int i = 0; i < cut.numVertices; i++)
            {   
                cut.vertices[i] = zeroAxis.rotateBy(cut.vertices[i], angle);
            }
        
            for (int j = 0; j < cut.numFacets; j++)
            {   cut.facets[j].updatePoints(cut.vertices);
            }    
        } // if axis != null
        
        else // axis == null
        {   // nothing to rotate, so project on z = 0
            for (int i = 0; i < cut.numVertices; i++)
            {   
                cut.vertices[i] = new Vector3D(cut.vertices[i].x, cut.vertices[i].y, 0);
            }
            for (int j = 0; j < cut.numFacets; j++)
            {   cut.facets[j].updatePoints(cut.vertices);
            }    
        }

        cut.findCenter();        
        cut.initObject3D(true, cut.center, ob.diameter, false);        
        
        ObjectGroup3D cutGroup = new ObjectGroup3D(cut, false);
        cutGroup.filled = cut.filled; 
        return cutGroup;
        
    }    

    /**
     * starting with an ObjectGroup3D ob and a plane, cut the starting Object3D of ob 
     * (thus the one without lines and planes) by the plane and make sure to also
     * create the cut; then subdivide this starting Object3D into two pieces,
     * that "on the left" and that "on the right" of the plane, and add a copy of the cut to
     * each of these; then translate the two halves left resp. right to separate them;  
     * then get the (translated) recipe of ob and cut resp. intersect both translated halves
     * with all lines and planes in the recipe (except plane); 
     * @param ob ObjectGroup3D to be cut into two ObjectGroup3D 
     * @param plane plane to cut ob with  
     * @return an ObjectGroup3D containing the two halves (each an ObjectGroup3D): 
     * leftGroup at objects.elementAt(0) and rightGroup at objects.elementAt(1)  
     */
    public static ObjectGroup3D cutObjectGroup(ObjectGroup3D ob, Plane3D plane)
    {   
        Object3D start = ob.leftMostLeaf().deepCopy();
        start.setVisible(true);
        start.setFilled(ob.filled);
        ObjectGroup3D startGroup = new ObjectGroup3D(start, false);
        startGroup.filled = start.filled;
        startGroup.visible = start.visible;
        startGroup.numVertexLabels = start.numVertexLabels;
        startGroup.fixFacetArray();
        // create start cut by plane, with cut
        ObjectWithPlane owp = new ObjectWithPlane(startGroup, plane.support,
            Vector3D.plus(plane.support, plane.direction1),
            Vector3D.plus(plane.support, plane.direction2),
            0, true);
        // throw all facets together
        owp.fixFacetArray();    
        // left and right
        Object3D left = new EmptyObject3D();
        Object3D right = new EmptyObject3D();
        ObjectGroup3D leftGroup, rightGroup;
  
        // look only at facets not replaced, these are 
        // 1) facets of start not cut by the plane
        // 2) new facets resulting from facets of start cut by the plane
        // 3) facet(s) in the cut
        for (int i = 0; i < owp.numFacets; i++)
        {   
            if (!owp.hasReplacement(owp.facets[i]))
            {   // use planepos to see 
                // where facet should end up
                int leftPos = 0;
                int onPos = 0;
                int rightPos = 0;
                for (int j = 0; j < owp.facets[i].numPoints; j++)
                {   int pPos = plane.planePosition(owp.facets[i].points[j]);
                    if (pPos == -1)
                        leftPos++;
                    else if (pPos == 1)
                        rightPos++;    
                    else // pPos == 0
                        onPos++;
                } // points of facet[i]
                // facet is left of cutting plane, so add it to the left object
                if ((leftPos > 0) && (onPos >= 0))
                {   
                    int firstIndex = left.numVertices;
                    for (int j = 0; j < owp.facets[i].numPoints; j++)
                        left.addVertex(new Vector3D(owp.facets[i].points[j]), null);
                    int[] inds = new int[owp.facets[i].numPoints];
                    for (int k = 0; k < owp.facets[i].numPoints; k++)
                        inds[k] = k + firstIndex;
                    Facet3D leftFacet = new Facet3D(left.vertices, inds, owp.facets[i].color);
                    left.addFacet(leftFacet);
                    Facet3D.copyAttributes(owp.facets[i], leftFacet, false);
                    
                }    
                // facet is right right of cutting plane, so add it to the right object
                else if ((rightPos > 0) && (onPos >= 0))
                {   // add facet to right
                    int firstIndex = right.numVertices;
                    for (int j = 0; j < owp.facets[i].numPoints; j++)
                        right.addVertex(new Vector3D(owp.facets[i].points[j]), null);
                    int[] inds = new int[owp.facets[i].numPoints];
                    for (int k = 0; k < owp.facets[i].numPoints; k++)
                        inds[k] = k + firstIndex;
                    Facet3D rightFacet = new Facet3D(right.vertices, inds, owp.facets[i].color);
                    right.addFacet(rightFacet);
                    Facet3D.copyAttributes(owp.facets[i], rightFacet, false);
                    
                }    
                // facet is in the cutting plane, so part of the cut,
                // add the facet to the right and the reverse of the facet to the left
                else if ((leftPos == 0) && (rightPos == 0))
                {   // facet is the cut, add to right
                    int firstIndex = right.numVertices;
                    for (int j = 0; j < owp.facets[i].numPoints; j++)
                        right.addVertex(new Vector3D(owp.facets[i].points[j]), null);
                    int[] inds = new int[owp.facets[i].numPoints];
                    for (int k = 0; k < owp.facets[i].numPoints; k++)
                        inds[k] = k + firstIndex;
                    Facet3D rightCutFacet = new Facet3D(right.vertices, inds, DrawConstants.objectColor);
                    right.addFacet(rightCutFacet);
                    Facet3D.copyAttributes(owp.facets[i], rightCutFacet, false);
                    // update cut colors and not outlined
                    rightCutFacet.color = DrawConstants.objectColor;                    
                    // note: there is only one cut!
                    for (int m = 0; m < rightCutFacet.numPoints; m++)
                        rightCutFacet.edgeCodes[m] = 0;
                    
                    // add reverse facet to left
                    firstIndex = left.numVertices;
                    for (int j = owp.facets[i].numPoints - 1; j >= 0; j--)
                        left.addVertex(new Vector3D(owp.facets[i].points[j]), null);
                    inds = new int[owp.facets[i].numPoints];
                    for (int k = 0; k < owp.facets[i].numPoints; k++)
                        inds[k] = k + firstIndex;
                    Facet3D leftCutFacet = new Facet3D(left.vertices, inds, DrawConstants.objectColor);
                    left.addFacet(leftCutFacet);
                    Facet3D.copyAttributes(owp.facets[i], leftCutFacet, false);
                    leftCutFacet.color = DrawConstants.objectColor;
                    for (int m = 0; m < leftCutFacet.numPoints; m++)
                        leftCutFacet.edgeCodes[m] = 0;
                } // allocation of facet[i]   
            
            } // !hasReplacement facet[i]
            
        } // owp facet loop    

        left.initObject3D(true, false);
        right.initObject3D(true, false);

        // note: up to here the labelling of the two basic halves is consistent
        // with that of the original basic object
        // now find all OTHER labels present in the original object

        Vector otherVerticesLabeled = new Vector();
        Vector otherVertexLabels = new Vector();
        // assume ob's facetArray is fixed
        for (int obFCnt = 0; obFCnt < ob.numFacets; obFCnt++)
        {   for (int obVCnt = 0; obVCnt < ob.facets[obFCnt].numPoints; obVCnt++)
            {   Vector3D oVertex = ob.facets[obFCnt].points[obVCnt];
                String oLabel = ob.facets[obFCnt].vertexLabels[obVCnt];
                if ((oLabel != null) && 
                    !oLabel.equals("") && !oLabel.equals("XX")
                   ) 
                {    if (!otherVerticesLabeled.contains(oVertex))
                     {    otherVerticesLabeled.addElement(oVertex);
                          otherVertexLabels.addElement(oLabel); 
                         
                     }  
                }   
            }
        }
        // find maximum labelindex of ob
        int otherIndex = 0;
        for (int oCnt = 0; oCnt < otherVertexLabels.size(); oCnt++)
        {   otherIndex = Math.max(otherIndex,
                getLabelIndex((String) otherVertexLabels.elementAt(oCnt)));
        }    
        
        if ((start.modelCode == DoorzienGWT.CYLINDER) ||
            (start.modelCode == DoorzienGWT.CONE1) ||
            (start.modelCode == DoorzienGWT.CONE2) ||
            (start.modelCode == DoorzienGWT.CONE3) ||
            (start.modelCode == DoorzienGWT.CONE4)
            )
        {   if (isCylinderType(left))    
                left.modelCode = start.modelCode;
            if (isCylinderType(right))        
                right.modelCode = start.modelCode;
        }

        // separation
        Vector3D trVector = new Vector3D(plane.normal);
        Vector3D.scaleBy(trVector, ob.diameter / 3);
               
        Vector3D minTrVector = Vector3D.minus(new Vector3D(0,0,0), trVector);

        double trPos = Vector3D.dotProduct(plane.normal, trVector) -
                       Vector3D.dotProduct(plane.normal, plane.point);
        double minTrPos = Vector3D.dotProduct(plane.normal, minTrVector) -
                                  Vector3D.dotProduct(plane.normal, plane.point);

        if (trPos < minTrPos)
        {   left.translateBy(trVector.x, trVector.y, trVector.z);
            right.translateBy(minTrVector.x, minTrVector.y, minTrVector.z);
        }
        else
        {   left.translateBy(minTrVector.x, minTrVector.y, minTrVector.z);
            right.translateBy(trVector.x, trVector.y, trVector.z);
                
        }

        Vector origConstruction = new Vector();   
        if (ob instanceof ObjectWithPlane)
            origConstruction = ((ObjectWithPlane) ob).getConstruction();
        else if (ob instanceof ObjectWithLine)
            origConstruction = ((ObjectWithLine) ob).getConstruction();
        origConstruction.removeElement(plane);            
        Vector trConstruction = new Vector();
        Vector minTrConstruction = new Vector();
        for (int i = 0; i < origConstruction.size(); i++)
        {   Object conObject = origConstruction.elementAt(i);
            if (conObject instanceof Line3D)
            {   Line3D trLine = ((Line3D) conObject).translateBy(trVector);
                trConstruction.addElement(trLine);
                Line3D minTrLine = ((Line3D) conObject).translateBy(minTrVector);
                minTrConstruction.addElement(minTrLine);            
            
            }
            else if (conObject instanceof Plane3D)
            {   Plane3D trPlane = ((Plane3D) conObject).translateBy(trVector);
                trConstruction.addElement(trPlane);
                Plane3D minTrPlane = ((Plane3D) conObject).translateBy(minTrVector);
                minTrConstruction.addElement(minTrPlane);            
                    
            }    
        }        
        
        if (trPos < minTrPos)
        {   leftGroup = rebuild(left, trConstruction);
            leftGroup.fixFacetArray();
            int labelCnt = otherIndex;
            // fix vertex labels
            for (int lFCnt = 0; lFCnt < leftGroup.numFacets; lFCnt ++)
            {   for (int lVCnt = 0; lVCnt < leftGroup.facets[lFCnt].numPoints; lVCnt ++)
                {   // study this vertex
                    Vector3D lVertex = leftGroup.facets[lFCnt].points[lVCnt];
                    String lLabel = leftGroup.facets[lFCnt].vertexLabels[lVCnt];
                    Vector3D trLVertex = new Vector3D(lVertex);
                    // translate back
                    Vector3D.translateBy(trLVertex, -trVector.x, -trVector.y, -trVector.z);
                    // if lVertex has a Label
                    if ((lLabel != null) && !lLabel.equals("") && !lLabel.equals("XX"))
                    {   if (otherVerticesLabeled.contains(trLVertex))
                        {   // relabel as in ob-group
                            int index = otherVerticesLabeled.indexOf(trLVertex);
                            String newLabel = (String) otherVertexLabels.elementAt(index);
                            leftGroup.facets[lFCnt].vertexLabels[lVCnt] = new String(newLabel);
                        } 
                        else // a new label, which should reappear in right
                        {   labelCnt++;
                            lLabel = getLabel(labelCnt);
                            leftGroup.facets[lFCnt].vertexLabels[lVCnt] = new String(lLabel);
                            otherVerticesLabeled.addElement(trLVertex);
                            otherVertexLabels.addElement(new String (lLabel));
                        }    
                        
                    }
                }    
            }
            
            rightGroup = rebuild(right, minTrConstruction);
            rightGroup.fixFacetArray();            
            // fix vertex labels
            for (int rFCnt = 0; rFCnt < rightGroup.numFacets; rFCnt ++)
            {   for (int rVCnt = 0; rVCnt < rightGroup.facets[rFCnt].numPoints; rVCnt ++)
                {   // study this vertex
                    Vector3D rVertex = rightGroup.facets[rFCnt].points[rVCnt];
                    String rLabel = rightGroup.facets[rFCnt].vertexLabels[rVCnt];
                    Vector3D trRVertex = new Vector3D(rVertex);
                    // translate back
                    Vector3D.translateBy(trRVertex, -minTrVector.x, -minTrVector.y, -minTrVector.z);
                    // if rVertex has a Label
                    if ((rLabel != null) && !rLabel.equals("") && !rLabel.equals("XX"))
                    {   if (otherVerticesLabeled.contains(trRVertex))
                        {   // relabel as in ob-group
                            int index = otherVerticesLabeled.indexOf(trRVertex);
                            String newLabel = (String) otherVertexLabels.elementAt(index);
                            rightGroup.facets[rFCnt].vertexLabels[rVCnt] = new String(newLabel);
                        } 
                        else // a new label, which should reappear in right
                        // cannot happen?
                        {   labelCnt++;
                            rLabel = getLabel(labelCnt);
                            rightGroup.facets[rFCnt].vertexLabels[rVCnt] = new String(rLabel);
                            otherVerticesLabeled.addElement(trRVertex);
                            otherVertexLabels.addElement(new String (rLabel));
                        }    
                        
                    }
                }    
            }
            
        }
        else
        {   

            leftGroup = rebuild(left, minTrConstruction);
            leftGroup.fixFacetArray();            
            int labelCnt = otherIndex;
            // vertex labels
            for (int lFCnt = 0; lFCnt < leftGroup.numFacets; lFCnt ++)
            {   for (int lVCnt = 0; lVCnt < leftGroup.facets[lFCnt].numPoints; lVCnt ++)
                {   // study this vertex
                    Vector3D lVertex = leftGroup.facets[lFCnt].points[lVCnt];
                    String lLabel = leftGroup.facets[lFCnt].vertexLabels[lVCnt];
                    Vector3D trLVertex = new Vector3D(lVertex);
                    Vector3D.translateBy(trLVertex, -minTrVector.x, -minTrVector.y, -minTrVector.z);
                    // if lVertex has a Label
                    if ((lLabel != null) && !lLabel.equals("") && !lLabel.equals("XX"))
                    {   if (otherVerticesLabeled.contains(trLVertex))
                        {   // relabel                        
                            int index = otherVerticesLabeled.indexOf(trLVertex);
                            String newLabel = (String) otherVertexLabels.elementAt(index);
                            leftGroup.facets[lFCnt].vertexLabels[lVCnt] = new String(newLabel);
                        } 
                        else // a new label, which should reappear in right
                        {   labelCnt++;
                            lLabel = getLabel(labelCnt);
                            leftGroup.facets[lFCnt].vertexLabels[lVCnt] = new String(lLabel);
                            otherVerticesLabeled.addElement(trLVertex);
                            otherVertexLabels.addElement(new String (lLabel));
                        }    
                        
                    }
                }    
            }
            
            rightGroup = rebuild(right, trConstruction);
            rightGroup.fixFacetArray();            

            // fix vertex labels
            for (int rFCnt = 0; rFCnt < rightGroup.numFacets; rFCnt ++)
            {   for (int rVCnt = 0; rVCnt < rightGroup.facets[rFCnt].numPoints; rVCnt ++)
                {   // study this vertex
                    Vector3D rVertex = rightGroup.facets[rFCnt].points[rVCnt];
                    String rLabel = rightGroup.facets[rFCnt].vertexLabels[rVCnt];
                    Vector3D trRVertex = new Vector3D(rVertex);
                    // translate back
                    Vector3D.translateBy(trRVertex, -trVector.x, -trVector.y, -trVector.z);
                    // if rVertex has a Label
                    if ((rLabel != null) && !rLabel.equals("") && !rLabel.equals("XX"))
                    {   if (otherVerticesLabeled.contains(trRVertex))
                        {   // relabel as in ob-group
                            int index = otherVerticesLabeled.indexOf(trRVertex);
                            String newLabel = (String) otherVertexLabels.elementAt(index);
                            rightGroup.facets[rFCnt].vertexLabels[rVCnt] = new String(newLabel);
                        } 
                        else // a new label, which should reappear in right
                        // cannot happen?
                        {   labelCnt++;
                            rLabel = getLabel(labelCnt);
                            rightGroup.facets[rFCnt].vertexLabels[rVCnt] = new String(rLabel);
                            otherVerticesLabeled.addElement(trRVertex);
                            otherVertexLabels.addElement(new String (rLabel));
                        }    
                        
                    }
                }    
            }
                
        }


        // leftGroup, rightGroup have correct diameter and translated center
        // rebuild the two pieces
        ObjectGroup3D result = new ObjectGroup3D();
        result.addObject3D(leftGroup);
        result.addObject3D(rightGroup);
        // force center and diameter
        result.initObject3D(true, new Vector3D(ob.center), ob.diameter, false);
        return result;
        
    }    

    /**
     * given a label of the form A,...,Z,AA,... find the index 
     * of this label: A corresponds to 1, Z corresponds to 26, AA corresponds to 27 etc.
     * @param s label whose index should be found
     * @return index of s
     */
    public static int getLabelIndex(String s)
    {   String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (s.length() == 1)
        {   char sChar = s.charAt(0);
            return alphabet.indexOf(sChar) + 1;
        }    
        else if (s.length() > 1)
        {   char sChar0 = s.charAt(0);
            char sChar1 = s.charAt(1);
            return alphabet.indexOf(sChar0) * 26 +
                   alphabet.indexOf(sChar1) + 1; 
        }    
        else
            return 1;
    }

    /**
     * determine if an Object3D is a cylinder or a cone,
     * used after cutting a cylinder of a cone in two pieces 
     * @param ob Object3D to be tested
     * @return true/false
     */
    public static boolean isCylinderType(Object3D ob)
    {   int greaterThenFour = 0;
        int threeCnt = 0;
        int fourCnt = 0;
        for (int i = 0; i < ob.numFacets; i++)
        {   if (ob.facets[i].numPoints > 4)
                greaterThenFour++;
            else if (ob.facets[i].numPoints == 4)    
                fourCnt++;
            else if (ob.facets[i].numPoints == 3)    
                threeCnt++;
                
        }
        boolean isCylinder = (greaterThenFour == 2) &&
            (threeCnt == 0) && (fourCnt > 0);
        boolean isCone = (greaterThenFour == 1) &&
            (threeCnt > 0) && (fourCnt == 0);    
        return isCylinder || isCone;
    }
    
    /**
     * add letters to the vertices of an Object3D; note that vertices
     * can belong to multiple facets 
     * @param ob Object3D to be lettered
     */
    public static void letterObject(Object3D ob)
    {   Vector newVertices = new Vector();
        int labelCnt = 0;
        for (int fCnt = 0; fCnt < ob.numFacets; fCnt++)
            for (int fvCnt = 0; fvCnt < ob.facets[fCnt].numPoints; fvCnt++)
            {   int index = newVertices.indexOf(ob.facets[fCnt].points[fvCnt]);
                if (index < 0)
                {   newVertices.addElement(ob.facets[fCnt].points[fvCnt]);
                    labelCnt++;
                    ob.facets[fCnt].vertexLabels[fvCnt] = getLabel(labelCnt);
                }    
                else
                    ob.facets[fCnt].vertexLabels[fvCnt] = getLabel(index + 1);
                    
            }        
        ob.numVertexLabels = labelCnt;
    }    

    /**
     * given an Object3D and a "recipe", that is a Vector of planes and lines,
     * create a new ObjectGroup3D by succesively cutting by the planes or
     * intersecting by the lines in recipe; this method can replace deepCopy()  
     * @param sObject initial Object3D
     * @param recipe list of planes and lines
     * @return the ObjectGroup3D obtained following the recipe
     */
    public static ObjectGroup3D rebuild(Object3D sObject, Vector recipe)
    {   Object3D start = sObject.deepCopy();
        start.setVisible(true);
        // dummy object group
        ObjectGroup3D startGroup = new ObjectGroup3D(start, false);   
        startGroup.numVertexLabels = sObject.numVertexLabels;
        startGroup.setFilled(start.filled); 
        startGroup.numVertexLabels = start.numVertexLabels;        
        startGroup.fixFacetArray(); //!!!
        // now build according to the recipe
        for (int i = 0; i < recipe.size(); i++)
        {   Object ob = recipe.elementAt(i);
            if (ob instanceof Plane3D)
            {   Plane3D pl = (Plane3D) ob;
                ObjectGroup3D tempStartGroup = new ObjectWithPlane(startGroup, 
                    pl.support, 
                    Vector3D.plus(pl.support, pl.direction1),
                    Vector3D.plus(pl.support, pl.direction2), 
                    DrawConstants.planeOutlineColorIndex, true);    
                // replacement made    
                if (tempStartGroup.objects.size() > 1)    
                {   startGroup = tempStartGroup;
                    startGroup.fixFacetArray(); //!!!    
                }
            }
            else if (ob instanceof Line3D)
            {   Line3D li = (Line3D) ob;
                ObjectGroup3D tempStartGroup = new ObjectWithLine(startGroup, 
                    li.point1, li.point2,
                    DrawConstants.lineColorIndex, DrawConstants.llFactor);    
                // replacement made    
                if (tempStartGroup.objects.size() > 1)    
                {   startGroup = tempStartGroup;
                    startGroup.fixFacetArray(); //!!!    
                }

            }    

        }
        return startGroup;
    }

}   // class ObjectWithPlane
