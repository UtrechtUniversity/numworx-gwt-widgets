package fi.grafiek3dgwt.client;

import java.awt.Color;
import java.util.Vector;

import com.google.gwt.canvas.dom.client.CssColor;

public class ObjectWithPoint extends ObjectGroup3D
{   
	public CssColor objectColor = Grafiek3DComponent.yellow;
	public CssColor planeColor = Grafiek3DComponent.yellow;
	
	public static int planeOutlineColorIndex = 2;	
	
    public static int HIDDENSHIFT = 10;
    public static int HIGHLIGHTSHIFT = 20;
	
	// attributes
    Object3D origObject;
    ObjectGroup3D origObjectGroup;
    Object3D replacement; // the new object at objects.elementAt(1);
    Vector3D vertex = null;
    Vector3D[] edgeWithPoint = null; 
    int pointColorIndex;
    // filling cuts
    boolean cutFilled = false;
    
    // "empty constructor for copying
    public ObjectWithPoint()
    {}
    // constructor-1
    // v is a vertex of origObjectGroup
    public ObjectWithPoint(ObjectGroup3D og, Vector3D v, int pcIndex)
    {   origObjectGroup = og;
        // objects.elementAt(0)
        addObject3D(origObjectGroup);
        origObject = origObjectGroup.leftMostLeaf();                
        vertex = new Vector3D(v);
        pointColorIndex = pcIndex;
        // find all visible(!) facets of origObjectGroup containing v
        // if some facet of origObjectGroup containing v replaces another
        // higher up containing v, then the latter is invisible,
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
// tick mark info??            
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
// dit maar even laten        
//public Matrix3D oMat;
        // fix center and diameter
        replacement.initObject3D(true, new Vector3D(origObjectGroup.center),
                                 origObjectGroup.diameter, false);

// vertexLabels of object replacement hier?? NA init                                
                                 
        // puts replacement at objects.elementAt(1)                         
        addObject3D(replacement);                         
        // properties of this ObjectWithPoint        
        sortSubArrays = origObjectGroup.sortSubArrays;        
        outlined = origObjectGroup.outlined;
        filled = origObjectGroup.filled;
        visible = origObjectGroup.visible;
// dit maar even laten        
//public Matrix3D oMat;
        // fix center and diameter
        initObject3D(true, new Vector3D(origObjectGroup.center),
                     origObjectGroup.diameter, false);
        
// RESTRICTIE EPN
//if (Grafiek3DComponent.version == Grafiek3DComponent.EPN)
    hideNonOrigVertices();
// EINDE RESTRICTIE epn
// alleen EPN?
inheritTickMarks();        
        
    }
    // constructor-2
    // v is a point on edge from->to of origObjectGroup (not a vertex), 
    public ObjectWithPoint(ObjectGroup3D og, Vector3D[] ftv, int pcIndex)
    {   origObjectGroup = og;
        // objects.elementAt(0)
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
        // higher up containing the (reversed) edge, then the latter is invisible,
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
// tick mark info??        
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
// tick mark info        
        replacement.facets[1].isReplacementOf = facet2;        
        facet2.visible = false;

// vertexLabels of object replacement hier??  NA init                                       
// je moet door de hele boom zoeken
        
        // set properties of replacement
        replacement.outlined = origObjectGroup.outlined;
        replacement.filled = origObjectGroup.filled;
        replacement.visible = origObjectGroup.visible;
// dit maar even laten        
//public Matrix3D oMat;
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
// dit maar even laten        
//public Matrix3D oMat;
        // fix center and diameter
        initObject3D(true, new Vector3D(origObjectGroup.center),
                     origObjectGroup.diameter, false);


// RESTRICTIE EPN
//if (Grafiek3DComponent.version == Grafiek3DComponent.EPN)
    hideNonOrigVertices();
// EINDE RESTRICTIE epn
// alleen EPN?
inheritTickMarks();
                
    } // constructor 2


    

    // redefine, do not fill cuts    
    public void setFilled(boolean fill)
    {   // this group
        filled = fill;
        origObjectGroup.setFilled(fill);
        for (int i = 0; i < replacement.numFacets; i++)
        {   
            if (!hasReplacement(replacement.facets[i]))
            {
                
// for EPN, unhide points on a line/plane intersection                
//if (Grafiek3DComponent.version == Grafiek3DComponent.EPN)
{
    for (int vCnt = 0; vCnt < replacement.facets[i].numPoints; vCnt++)
    {   if ((replacement.facets[i].vertexLabels[vCnt] != null) &&
             replacement.facets[i].vertexLabels[vCnt].equals("XX")
           )
           if (!filled)// & cutFilled)
               replacement.facets[i].vertexCodes[vCnt] = planeOutlineColorIndex + HIDDENSHIFT;
//           else if (!filled & !cutFilled)
//               replacement.facets[i].vertexCodes[vCnt] = -1;
    }    
}                
                
                if (replacesOrigObject(replacement.facets[i]))
                {   
                    // this should always be the case
                    if (replacement.facets[i].numPoints > 2)
                    {   if (filled)
                        {   replacement.facets[i].filled = filled;
                            replacement.facets[i].color = objectColor;
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
//System.out.println("replaces orig");                                                        
                                    replacement.facets[i].filled = true;
                                    replacement.facets[i].color = planeColor;
                                }
                                else
                                {   replacement.facets[i].filled = false;
                                    replacement.facets[i].color = planeColor;
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
                        {   replacement.facets[i].color = planeColor; 
                            replacement.facets[i].filled = true; // testing filled cuts
//System.out.println("replaces cut");                                                                                    
                        }
                        else    
                            replacement.facets[i].filled = false;            
                    }   
                }
                // internal segment
                // kan alleen bij FI
                else if ((replacement.facets[i].numPoints == 2) &&
                         (replacesLineExtension(facets[i]) == null)
                         //facets[i].normal.equals(new Vector3D(0,0,0))
                         )
                {
//System.out.println("internal segment");                    
                         // now this thing is never filled and even invisible if
                         // the "surrounding object" is 
                   replacement.facets[i].visible = !filled;
                }   
                
            } // if !hasReplacement
        } // for replacement.facets
        
    }

    public void fillCuts(boolean b)
    {   cutFilled = b;
        if (origObjectGroup instanceof ObjectWithPlane)
            ((ObjectWithPlane) origObjectGroup).fillCuts(b);
        else if (origObjectGroup instanceof ObjectWithLine)
            ((ObjectWithLine) origObjectGroup).fillCuts(b);        
        // else do nothing    
        
    }    
    
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
// als dit niet de top is, dan zit facet sowieso in replacement en niet in cut            
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
                    else // keep looking
                        //return replacesCut(repFacet);
                        return null;
                }    
                else
                    return null;
            }
            else // keep looking
                //return replacesCut(repFacet);
                return null;
        }
    }    
    
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

    public void inheritTickMarks()
    {   // nothing to do
        //if (Grafiek3DComponent.TICKNUM == 0)
        //    return; 
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
//System.out.println("inheriting edge");                        
                    }
                    else // check if f contains edge as subsegment
                    {   eIndex = Facet3D.edgeContainsDirSegment(f, eStart, eEnd);
                        if (eIndex >= 0)
                        {   
//System.out.println("inheriting subedge");                                                
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
//System.out.println("tick on subseg");                                    
                                        if (firstIndex < 0)
                                        {   firstIndex = tCnt;
                                            lastIndex = tCnt;
                                        }    
                                        else // first point found, set last
                                        {   lastIndex = tCnt;
                                        }    
                                    }    
//System.out.println("first = " + firstIndex);
//System.out.println("last = " + lastIndex);
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
    // clicking vertices
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
    // clicking edges
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
    public void hideNonOrigVertices()
    {   
//DrawingPanel.setStart();        
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
//DrawingPanel.showTime("OWP hiding vertices");        
    }    
// EINDE RESTRICTIE TOT ORIGINAL OBJECT VOOR EPN

    // check if repFacet (a segment) IS or replaces 
    // a line extension
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
    
    // put the "recipe" for origObjectGroup in Vector recipe
    // list is in reverse order!!
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
    // get the final construction sequence
    public Vector getConstruction()
    {   Vector recipe = new Vector();
        getConstructionList(recipe);
        Vector result = new Vector();
        for (int i = 0; i < recipe.size(); i++)
            result.addElement(recipe.elementAt(recipe.size() - 1 - i));
        return result;    
    }
    
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
    // get the final construction sequence
    public Vector getConstructionColors()
    {   Vector recipe = new Vector();
        getConstructionColorList(recipe);
        Vector result = new Vector();
        for (int i = 0; i < recipe.size(); i++)
            result.addElement(recipe.elementAt(recipe.size() - 1 - i));
        return result;    
    }
    
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


