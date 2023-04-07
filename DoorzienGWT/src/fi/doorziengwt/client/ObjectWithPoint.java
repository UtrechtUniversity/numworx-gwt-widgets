package fi.doorziengwt.client;

import java.util.Vector;

/**
 * "selecting" a point on a 3d-object: note how this "selecting" works:<br> 
 * case 1: the selected point is a vertex: in this case all facets containing this vertex are replaced with (identical)
 * facets in which the color of the selected vertex is set to pointColorIndex<br>
 * case 2: the selected point is on an edge (but not a vertex): in this case there must be two facets containing this
 * edge; these two facets are replaced by two facets to which the point is added as a vertex (so that the edge containing
 * the vertex is divided into two edges); <br>
 * the 3d-object of which a point is to be selected is an ObjectGroup3D called origObjectGroup and is of the following type:<br>
 * a) a basic Object3D called origObject, which has no planes or lines, origObject is located at origObjectGroup.objects.elementAt(0); <br>
 * b) an ObjectWithLine, see class ObjectWithLine; <br>
 * c) an ObjectWithPlane, see class ObjectWithPlane; <br>
 * an instance of ObjectWithPoint (an ObjectGroup3D) contains the point as a Vector3D (case vertex) or an EdgeWithPoint 
 * (case point on edge)<br>
 * in objects.elementAt(0) a (reference to) origObjectGroup <br>
 * in objects.elementAt(1) an Object3D called replacement: all facets replacing facets containing the point as described above;  
 * note that any ObjectGroup3D in the hierarchy is completely determined by origObject and a ordered list of planes and lines by which
 * origObject has been cut; this list of lines and planes is called the construction(list);<br>
 * note that instances of the class ObjectWithPoint are only used temporarily when drawing a line (once) or when drawing a plane (twice),
 * see class DrawingPanel2.  
 * @author huub
 */

public class ObjectWithPoint extends ObjectGroup3D
{   
	/**
	 * starting Object3D, no lines, no planes
	 */
	Object3D origObject;
    /**
     * the ObjectGroup3D to which a point is "added" 
     */
    ObjectGroup3D origObjectGroup;
    /**
     * the new object at objects.elementAt(1), contains the facets of origObjectGroup being replaced by adding a point
     */
    Object3D replacement;
    /**
     * the point in case it is a vertex
     */
    Vector3D vertex = null;
    /**
     * the point in case it is on an edge (but not a vertex)
     */
    Vector3D[] edgeWithPoint = null;
    /**
     * the color of the line 
     */
    int pointColorIndex;
    /**
     * should facets making up cuts by planes be filled? 
     */
    boolean cutFilled = false;
    
    /**
     * "empty" constructor for copying
     */
    public ObjectWithPoint()
    {}
    
    /**
     * constructor-1: the point to be added is a vertex of the 3d-object   
     * @param og  the 3d-object 
     * @param v the points 
     * @param pcIndex the index of the color of the point to be added
     */
    public ObjectWithPoint(ObjectGroup3D og, Vector3D v, int pcIndex)
    {   origObjectGroup = og;
        // set objects.elementAt(0)
        addObject3D(origObjectGroup);
        origObject = origObjectGroup.leftMostLeaf();                
        vertex = new Vector3D(v);
        pointColorIndex = pcIndex;
        // find all visible(!) facets of origObjectGroup containing v
        // if some facet of origObjectGroup containing v replaces another
        // higher up containing v, then the one higher up is invisible,
        // so not found
        Vector replacedFacets = origObjectGroup.facetsContaining(vertex);
        // now create a new object at objects.elementAt(1)
        // consisting of deep(!) copies of replacedFacets
        // (force redundancy in the vertices)
        // with the vertex label of vertex set to pointColorIndex
        // set the replaced facets invisible (at the end)
        replacement = new EmptyObject3D();
        // find numVertices
        replacement.numVertices = 0;
        for (int i = 0; i < replacedFacets.size(); i++)
        {   Facet3D f = (Facet3D) replacedFacets.elementAt(i);
            replacement.numVertices += f.numPoints;
        }
        // initialize
        replacement.vertices = new Vector3D[replacement.numVertices];
        replacement.trVertices = new Vector3D[replacement.numVertices];
        replacement.vertexLabels = new String[replacement.numVertices];
        int vertexCount = 0;
        // in one go create vertices and facets
        replacement.numFacets = replacedFacets.size();
        replacement.facets = new Facet3D[replacement.numFacets];
        for (int j = 0; j < replacedFacets.size(); j++)
        {   Facet3D f = (Facet3D) replacedFacets.elementAt(j);
            int[] newInds = new int[f.numPoints];
            for (int k = 0; k < f.numPoints; k++)
            {   replacement.vertices[vertexCount] = new Vector3D(f.points[k]);
                newInds[k] = vertexCount;
                vertexCount++;
            }
            // replacement.facets[j].points is updated in constructor!
            replacement.facets[j] = new Facet3D(replacement.vertices, newInds, f.color);
            // this copies vertex and edge labels
            Facet3D.copyAttributes(f, replacement.facets[j], true);
            // index of vertex in original
            int pointIndex = Facet3D.containsVertex(f, vertex);
            // should be positive
            // reset
            replacement.facets[j].vertexCodes[pointIndex] = pointColorIndex;
            replacement.facets[j].isReplacementOf = f;
            f.visible = false;
        }
        // set properties of replacement
        replacement.outlined = origObjectGroup.outlined;
        replacement.filled = origObjectGroup.filled;
        replacement.visible = origObjectGroup.visible;
        // fix center and diameter
        replacement.initObject3D(true, new Vector3D(origObjectGroup.center),
                                 origObjectGroup.diameter, false);
        // puts replacement at objects.elementAt(1)                         
        addObject3D(replacement);                         
        // properties of this ObjectWithPoint        
        sortSubArrays = origObjectGroup.sortSubArrays;        
        outlined = origObjectGroup.outlined;
        filled = origObjectGroup.filled;
        visible = origObjectGroup.visible;
        // fix center and diameter
        initObject3D(true, new Vector3D(origObjectGroup.center),
                     origObjectGroup.diameter, false);
        
        // RESTRICTIE EPN
        if (DoorzienGWT.version == DoorzienGWT.EPN)
        	hideNonOrigVertices();
        // EINDE RESTRICTIE epn

        inheritTickMarks();        
        
    }
    
    /**
     * constructor-2: the point to be added is on an edge of the 3d-object (but not a vertex)
     * @param og the 3d-object
     * @param ftv the edge with the point (see class EdgeWithPoint)
     * @param pcIndex the index of the color of the point to be added (see class DrawConstants)
     */
    public ObjectWithPoint(ObjectGroup3D og, Vector3D[] ftv, int pcIndex)
    {   origObjectGroup = og;
        // set objects.elementAt(0)
        addObject3D(origObjectGroup);
        origObject = origObjectGroup.leftMostLeaf();                
        // deep copy
        edgeWithPoint = new Vector3D[3];
        edgeWithPoint[0] = new Vector3D(ftv[0]);
        edgeWithPoint[1] = new Vector3D(ftv[1]);
        edgeWithPoint[2] = new Vector3D(ftv[2]);
        pointColorIndex = pcIndex;
        // find the 2 visible(!) facets of origObjectGroup containing
        // the edge edgeWithPoint[0]->edgeWithPoint[1]
        // resp. edgeWithPoint[1]->edgeWithPoint[0]
        // if some facet of origObjectGroup containing the (reversed) edge replaces another
        // higher up containing the (reversed) edge, then the one higher up is invisible,
        // so not found
        Facet3D facet1 = origObjectGroup.facetContaining(
                            edgeWithPoint[0], edgeWithPoint[1], false);
        Facet3D facet2 = origObjectGroup.facetContaining(
                            edgeWithPoint[1], edgeWithPoint[0], false);
        // now create a new object at objects.elementAt(1)
        // consisting of deep(!) copies of replacedFacets
        // with the new point added and with the vertex label of 
        // this point set to pointColorIndex
        // (force redundancy in the vertices)        
        // set the replaced facets invisible (at the end)
        replacement = new EmptyObject3D();
        // find numVertices
        replacement.numVertices = facet1.numPoints + facet2.numPoints + 2;
        // initialize
        replacement.vertices = new Vector3D[replacement.numVertices];
        replacement.trVertices = new Vector3D[replacement.numVertices];
        replacement.vertexLabels = new String[replacement.numVertices];
        // create vertices and facets
        replacement.numFacets = 2; 
        replacement.facets = new Facet3D[replacement.numFacets];        
        // index of edgeWithPoint[0] in facet1
        int newIndex1 = Facet3D.containsEdge(facet1, edgeWithPoint[0], edgeWithPoint[1]);
        // index of edgeWithPoint[1] in facet2
        int newIndex2 = Facet3D.containsEdge(facet2, edgeWithPoint[1], edgeWithPoint[0]);        
        // facet1
        // first vertex becomes new point
        int vertexCount = 0;        
        int indexCount = 0;
        int[] newInds = new int[facet1.numPoints + 1];
        replacement.vertices[vertexCount] = new Vector3D(edgeWithPoint[2]);        
        newInds[indexCount] = vertexCount;
        vertexCount++;
        indexCount++;
        for (int k = newIndex1 + 1; k < facet1.numPoints + newIndex1 + 1; k++)
        {   replacement.vertices[vertexCount] = 
                new Vector3D(facet1.points[k % facet1.numPoints]);
            newInds[indexCount] = vertexCount;
            vertexCount++;
            indexCount++;
        }
        replacement.facets[0] = new Facet3D(replacement.vertices, newInds, facet1.color);
        Facet3D.copyAttributes(facet1, replacement.facets[0], false);
        // labels of new point/edge
        replacement.facets[0].vertexCodes[0] = pointColorIndex;
        replacement.facets[0].edgeCodes[0] = 
            facet1.edgeCodes[newIndex1];
        // copy labels of "old points"
        // note the shift!!
        for (int m = 0; m < facet1.numPoints; m++)            
        {   replacement.facets[0].vertexCodes[m + 1] = 
                facet1.vertexCodes[(newIndex1 + 1 + m) % facet1.numPoints];
            replacement.facets[0].edgeCodes[m + 1] = 
                facet1.edgeCodes[(newIndex1 + 1 + m) % facet1.numPoints];
        }    
        replacement.facets[0].isReplacementOf = facet1;
        facet1.visible = false;
        // facet2
        // first vertex becomes new point
        indexCount = 0;
        newInds = new int[facet2.numPoints + 1];
        replacement.vertices[vertexCount] = new Vector3D(edgeWithPoint[2]);        
        newInds[indexCount] = vertexCount;
        vertexCount++;
        indexCount++;
        for (int k = newIndex2 + 1; k < facet2.numPoints + newIndex2 + 1; k++)
        {   replacement.vertices[vertexCount] = 
                new Vector3D(facet2.points[k % facet2.numPoints]);
            newInds[indexCount] = vertexCount;
            vertexCount++;
            indexCount++;
        }
        replacement.facets[1] = new Facet3D(replacement.vertices, newInds, facet2.color);
        Facet3D.copyAttributes(facet2, replacement.facets[1], false);
        replacement.facets[1].vertexCodes[0] = pointColorIndex;
        replacement.facets[1].edgeCodes[0] = 
            facet2.edgeCodes[newIndex2];
        // copy labels of "old points"
        // note the shift!!
        for (int m = 0; m < facet2.numPoints; m++)            
        {   replacement.facets[1].vertexCodes[m + 1] = 
                facet2.vertexCodes[(newIndex2 + 1 + m) % facet2.numPoints];
            replacement.facets[1].edgeCodes[m + 1] = 
                facet2.edgeCodes[(newIndex2 + 1 + m) % facet2.numPoints];
        }    
       
        replacement.facets[1].isReplacementOf = facet2;        
        facet2.visible = false;

        // set properties of replacement
        replacement.outlined = origObjectGroup.outlined;
        replacement.filled = origObjectGroup.filled;
        replacement.visible = origObjectGroup.visible;
        // fix center and diameter
        replacement.initObject3D(true, new Vector3D(origObjectGroup.center),
                                 origObjectGroup.diameter, false);
        // puts replacement at objects.elementAt(1)                         
        addObject3D(replacement);                         
        // properties of this ObjectWithPoint        
        sortSubArrays = origObjectGroup.sortSubArrays;        
        outlined = origObjectGroup.outlined;
        filled = origObjectGroup.filled;
        visible = origObjectGroup.visible;
        // fix center and diameter
        initObject3D(true, new Vector3D(origObjectGroup.center),
                     origObjectGroup.diameter, false);

        // RESTRICTIE EPN
        if (DoorzienGWT.version == DoorzienGWT.EPN)
        	hideNonOrigVertices();
        // EINDE RESTRICTIE epn

        inheritTickMarks();
                
    } // constructor 2

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
                        		// first three points could be collinear!                                
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
                            replacement.facets[i].filled = false;            
                    }   
                }
                // internal segment
                else if ((replacement.facets[i].numPoints == 2) &&
                         (replacesLineExtension(facets[i]) == null)
                         )
                {
                    // now this thing is never filled and even invisible if
                    // the "surrounding object" is 
                	replacement.facets[i].visible = !filled;
                }   
                
            } // if !hasReplacement
        } // for replacement.facets
        
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
     * check if a vertex is on a line extension   
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
     * check if an edge is on a line extension
     * @param eStart start point of edge to check
     * @param eEnd end point of edge to check
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
                    //else
                    //    isOnInner = true;
                }
            }
        }
        return isOnExt;
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
            {   // do not show at all
                if (!replacesOrigObject(facets[i]))
                {   for (int j = 0; j < facets[i].numPoints; j++)
                    {   facets[i].vertexCodes[j] = -1;
                    }
                }
                else // check if vertex is on an edge of original object
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
     * check if repFacet (a segment) IS or replaces a line extension
     * @param repFacet facet (2-dimensional) to check
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
     * check if the construction recipe of this OWL contains line l
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
     * recursively find the recipe for this OWP; note that
     * the list is in reverse order (last operation first in list)
     * @param recipe Vector containing planes and lines used for recursion
     */
     public void getConstructionList(Vector recipe)
    {   if (vertex != null)
            recipe.addElement(new Vector3D(vertex));
        else if (edgeWithPoint != null)
        {   Vector3D[] recipeEWP = new Vector3D[3];
            recipeEWP[0] = new Vector3D(edgeWithPoint[0]);
            recipeEWP[1] = new Vector3D(edgeWithPoint[1]);
            recipeEWP[2] = new Vector3D(edgeWithPoint[2]);
            recipe.addElement(new EWP(recipeEWP));
        }    
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
    {   recipe.addElement(new Integer(pointColorIndex));
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
     * make a deep copy of this ObjetWithPoint 
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
        ObjectWithPoint copy = new ObjectWithPoint();
        makeDeepGroupCopy(copy);
        copy.origObjectGroup = (ObjectGroup3D) copy.objects.elementAt(0);
        copy.origObject = copy.origObjectGroup.leftMostLeaf();                
        copy.replacement = (Object3D) copy.objects.elementAt(1);
        if (vertex != null)
            copy.vertex = new Vector3D(vertex);
        else
            copy.vertex = null;
        if (edgeWithPoint != null)
        {   copy.edgeWithPoint = new Vector3D[3];
            copy.edgeWithPoint[0] = new Vector3D(edgeWithPoint[0]);
            copy.edgeWithPoint[1] = new Vector3D(edgeWithPoint[1]);
            copy.edgeWithPoint[2] = new Vector3D(edgeWithPoint[2]);
        }
        else
            copy.edgeWithPoint = null;
        copy.pointColorIndex = pointColorIndex;
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
    
}  // class ObjectWithPoint

