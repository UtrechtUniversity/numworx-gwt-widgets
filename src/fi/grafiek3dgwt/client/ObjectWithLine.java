package fi.grafiek3dgwt.client;

//import java.awt.Color;
import java.util.Vector;

import com.google.gwt.canvas.dom.client.CssColor;

public class ObjectWithLine extends ObjectGroup3D
{   
	public static CssColor objectColor = Grafiek3DComponent.yellow;
    public CssColor planeColor = Grafiek3DComponent.yellow;
	
    public int lColorIndex = 1;
    public int planeOutlineColorIndex = 2;
    public int pointColorIndex = 3;
    
    public static int HIDDENSHIFT = 10;
    public static int HIGHLIGHTSHIFT = 20;
    
	// attributes
    Object3D origObject;
    ObjectGroup3D origObjectGroup;
    Object3D replacement; // the new object at objects.elementAt(1);
    // contains the facets being replaced to create the
    // line in origObjectGroup
    Object3D extensions;
    // objects.elementAt(2) contains the line extensions (2 pieces)
    int lineColorIndex;
    // the line as object, see class Line3D
    Line3D line;
    Vector3D lineExtStart1, lineExtStart2;
    // filling cuts
    boolean cutFilled = false;
    // lengthen factor
    double llFactor;
    // "empty constructor for copying
    public ObjectWithLine()
    {}
    // constructor
    public ObjectWithLine(ObjectGroup3D og, 
                          Vector3D vStart, Vector3D vEnd, int lcIndex,
                          double lengthenFactor)
    {   origObjectGroup = og;
        // objects.elementAt(0)
        addObject3D(origObjectGroup);
        origObject = origObjectGroup.leftMostLeaf();                
        lineColorIndex = lcIndex;        
        llFactor = lengthenFactor;
        line = new Line3D(vStart, vEnd);
        replacement = new EmptyObject3D();
        // for finding start points of line extensions (2 pieces)
        // and segments
        Vector linePoints = new Vector();
        Vector lineExtPoints = new Vector();
        for (int fCnt = 0; fCnt < origObjectGroup.numFacets; fCnt++)
        {   // shortcut
            Facet3D facet = origObjectGroup.facets[fCnt];
            
            boolean hasRep = false;
            if (origObjectGroup instanceof ObjectWithPlane)
                hasRep = ((ObjectWithPlane) origObjectGroup).hasReplacement(facet);
            else if (origObjectGroup instanceof ObjectWithLine)
                hasRep = ((ObjectWithLine) origObjectGroup).hasReplacement(facet);
            
            // if (facet.visible)
            if (!hasRep)
            {   // facet is een segment
                Facet3D facet1 = null, facet2 = null;
                if (facet.numPoints == 2)
                {   
//System.out.println("intersecting a segment");                                                
                    Line3D tLine = new Line3D(facet.points[0], facet.points[1]);
                    int isType = Line3D.intersectionType(line, tLine);
                    if (isType == 0)
                    {   // nothing to do
//System.out.println("crossing");                                                                    
                    }
                    else if (isType == 1)
                    {   Vector3D isPoint = Line3D.getIntersectionPoint(line, tLine);
                        // intersection point on the facet (segment)
                        if (tLine.segmentContains(isPoint))
                        {   
//System.out.println("point on segment");                            
                            // on a vertex
                            if (isPoint.equals(facet.points[0]))
                            {   // nothing to do
//System.out.println("seg & line cuts in vertex");                            

// toevoegen aan linepoints?
                            }    
                            else if (isPoint.equals(facet.points[1]))
                            {   // nothing to do
//System.out.println("seg & line cuts in vertex");                                                        

// toevoegen aan linepoints?
                            }    
                            // cut the segment in 2 pieces here
                            else // isPoint on inside of segment
                            {   // maak 2 nieuwe facets

                                // v0->isPoint
                                // vertices toevoegen aan replacement
                                // gaat ook OK als facet een line extension is
                                int newInds[] = new int[2];
                                newInds[0] = replacement.numVertices;
                                replacement.addVertex(new Vector3D(facet.points[0]), null);
                                newInds[1] = replacement.numVertices;
                                replacement.addVertex(new Vector3D(isPoint), null);
                                facet1 = new Facet3D(replacement.vertices,
                                    newInds, facet.color);
                                Facet3D.copyAttributes(facet, facet1, false);
                                

//                               facet1.edgeCodes[0] = lineColorIndex + Facet3D.HIDDENSHIFT;                                
//                               facet1.edgeCodes[1] = lineColorIndex + Facet3D.HIDDENSHIFT;                 
                               if (facet.vertexLabels[0] != null)
                                   facet1.vertexLabels[0] = new String(facet.vertexLabels[0]);                               
// kleur blijft OK?
// tick marks
                                facet1.isReplacementOf = facet;
                                replacement.addFacet(facet1);
                                
                                
                                // isPoint->v1
                                // vertices toevoegen aan replacement
                                newInds = new int[2];
                                newInds[0] = replacement.numVertices;
                                replacement.addVertex(new Vector3D(isPoint), null);
                                newInds[1] = replacement.numVertices;
                                replacement.addVertex(new Vector3D(facet.points[1]), null);
                                facet2 = new Facet3D(replacement.vertices,
                                    newInds, facet.color);
                                Facet3D.copyAttributes(facet, facet2, false);
                                
//                                facet2.edgeCodes[0] = lineColorIndex + Facet3D.HIDDENSHIFT;                                
//                                facet2.edgeCodes[1] = lineColorIndex + Facet3D.HIDDENSHIFT;                 
                                if (facet.vertexLabels[1] != null)
                                    facet2.vertexLabels[1] = new String(facet.vertexLabels[1]);
// kleur snijpunt                                
//                                facet2.vertexCodes[0] = lineColorIndex;//DrawingPanel.pointColorIndex;                                
// kleur blijft OK?
// tick marks
                                facet2.isReplacementOf = facet;
                                replacement.addFacet(facet2);
                                facet.visible = false;                                
//System.out.println("seg & line cuts in edge");                                                            
                            }
                            boolean replacesLineExtension = false;
                            if (origObjectGroup instanceof ObjectWithLine)
                            {   replacesLineExtension = 
                                    ((ObjectWithLine) origObjectGroup).replacesLineExtension(facet) != null;
                            }    
                            else if (origObjectGroup instanceof ObjectWithPlane)
                            {   replacesLineExtension = 
                                    ((ObjectWithPlane) origObjectGroup).replacesLineExtension(facet) != null;
                            }    
// point toevoegen?                            
                            // add isPoint to lineExtPoints resp linePoints
                            if (replacesLineExtension)
                            {   
                                // mark isPoint if a line extension was cut
                                // for the moment forced unhidden
                                if (facet1 != null)
                                {   facet1.edgeCodes[0] = lineColorIndex + 40;                                
                                    facet1.edgeCodes[1] = lineColorIndex + 40;                 
                                    
                                    //facet1.vertexCodes[1] = lineColorIndex + 40;
                                    facet1.vertexCodes[1] = -1;
                                }    
                                if (facet2 != null)    
                                {   
                                    facet2.edgeCodes[0] = lineColorIndex + 40;                                
                                    facet2.edgeCodes[1] = lineColorIndex + 40;                 
                                    
                                    //facet2.vertexCodes[0] = lineColorIndex + 40;
                                    facet2.vertexCodes[0] = -1;
                                }
                                //if (!lineExtPoints.contains(isPoint))
                                //    lineExtPoints.addElement(new Vector3D(isPoint));
                                
                            }
                            else
                            {   
                                // forced hidden
                                if (facet1 != null)
                                {    
                                     facet1.edgeCodes[0] = lineColorIndex + HIDDENSHIFT;                                
                                     facet1.edgeCodes[1] = lineColorIndex + HIDDENSHIFT;                                                     
                                     facet1.vertexCodes[1] = lineColorIndex + 10;
                                     facet1.normal = new Vector3D();   
                                }
                                if (facet2 != null)    
                                {    
                                     facet2.edgeCodes[0] = lineColorIndex + HIDDENSHIFT;                                
                                     facet2.edgeCodes[1] = lineColorIndex + HIDDENSHIFT;                 
                                    
                                     facet2.vertexCodes[0] = lineColorIndex + 10;
                                     facet2.normal = new Vector3D();   
                                }
                                
                                if (!linePoints.contains(isPoint))
                                    linePoints.addElement(new Vector3D(isPoint));
                            }    
// let op:
// dit facet komt van een andere lijn en deze wordt nu doorgesneden
// de nieuwe lijn wordt ook doorgesneden vanwege isPoint
                        } // line intersects facet   
                    } // isType == 1
                    // dit kan niet: else if (isType == 2)
                    // omdat je niet 2 keer met dezlefde lijn mag snijden
                } // facet.numPoints == 2
                
                // facet has at least 3 non-collinear points                
                else if (facet.numPoints > 2) 
                {   
//System.out.println("intersecting a facet");                                                                                
//System.out.println("numPoints = " + facet.numPoints);                                                                                
                    // find the plane throught the facet
                    
                    Plane3D tPlane = new Plane3D(
                        facet.normal.x, facet.normal.y, facet.normal.z,
                        Vector3D.dotProduct(facet.normal, facet.points[0]));

// wrong, points can be collinear and an arbitrary plane will be generated                    
//                    Plane3D tPlane = new Plane3D(
//                        facet.points[0], facet.points[1], facet.points[2]);
                        
//System.out.println("right " + tPlane.toString()); 
//System.out.println(tPlane.normal.toString());
                        
                    // get intersection type of line and this plane
                    int isType = Plane3D.intersectionType(line, tPlane);
                    // tPlane contains line
                    if (isType == 2)
                    {   
           
                        
//System.out.println("isType = 2");                                                                                                        
//System.out.println(line.toString());
//System.out.println(tPlane.toString());
                        // find plane orthogonal to facet and through line
                        // passes through line.support, line.support + line.direction
                        // and line.support + facet.normal
                        Plane3D oPlane = new Plane3D(
                            line.support,
                            Vector3D.plus(line.support, line.direction),
                            Vector3D.plus(line.support, facet.normal));
                        Object3D rep = ObjectWithPlane.cutFacetWithPlane(facet, oPlane, lineColorIndex);
//System.out.println("repvert = " + rep.numVertices);                        
                        // vertices van rep toevoegen aan replacement
                        int firstIndex = replacement.numVertices;
//System.out.println("first = " + firstIndex);                        
                        for (int i = 0; i < rep.numVertices; i++)
                            replacement.addVertex(rep.vertices[i], null);
                        // voor de facets van rep
                        for (int j = 0; j < rep.numFacets; j++)
                        {   // 0 reference updaten since we don't make a new facet
//                            rep.facets[j].allPoints = replacement.vertices;
                            // 1) nieuwe indices maken en vervangen
                            int[] newInds = new int[rep.facets[j].numPoints];
                            for (int k = 0; k < rep.facets[j].numPoints; k++)
                                newInds[k] = rep.facets[j].indices[k] + firstIndex;
                            rep.facets[j].indices = newInds;    
                            // 2) points updaten (voor de zekerheid)
                            rep.facets[j].updatePoints(replacement.vertices);
                            // 2a facets toevoegen
                            replacement.addFacet(rep.facets[j]);
                            // 3) isReplacementOf zetten
                            rep.facets[j].isReplacementOf = facet;
//3A edgeCodes opnieuw bepalen, dwz deelsegmenten van onzichtbare                             
// in facet weer onzichtbaar maken
// all edges of rep on the line are already blue
// if the line cuts facet in an edge, and this edge of facet is not outlined
// do the same for the replacement??
// ook moet je kijken of je de hidden kleur moet gebruiken
            for (int pCnt = 0; pCnt < rep.facets[j].numPoints; pCnt++)
            {   // find edge
                Vector3D start = rep.facets[j].points[pCnt];
                Vector3D end = rep.facets[j].points[(pCnt + 1) % rep.facets[j].numPoints];
                int index = Facet3D.edgeContainsDirSegment(facet, start, end);
                boolean isOn = line.contains(start) && line.contains(end);           
                // line trough an edge of facet
                if ((index >= 0) && isOn)
                {   if (facet.edgeCodes[index] < 0)
                        rep.facets[j].edgeCodes[pCnt] = facet.edgeCodes[index];
                    else if ((facet.edgeCodes[index] >= 10) && 
                             (facet.edgeCodes[index] < 20))
                        rep.facets[j].edgeCodes[pCnt] += 10;     
                }
                // new edge relative to before
                else if ((index < 0) && isOn)
                {   // segment replaces a cut, hide
                    if (!replacesOrigObject(facet))
                        rep.facets[j].edgeCodes[pCnt] += 10;     
                }    
            }  // for edgeCodes
// vertex codes            
            for (int pCnt = 0; pCnt < rep.facets[j].numPoints; pCnt++)
            {   // find point
                Vector3D start = rep.facets[j].points[pCnt];
                //int index = Facet3D.containsVertex(facet, start);
                boolean isOn = line.contains(start);           
                // vertex is on the outside
                if (replacesOrigObject(facet) && isOn)
                {   rep.facets[j].vertexCodes[pCnt] = lineColorIndex;     
//System.out.println("outside");                
                }
                // vertex inside
                else if (!replacesOrigObject(facet) && isOn)
                {   
                    
                    boolean edgeContains = false;
                    for (int i = 0; i < origObject.numFacets; i++)
                    {   edgeContains = edgeContains || 
                            (Facet3D.edgeContainsSegment(origObject.facets[i], start, start) >= 0);
                    }
                    if (edgeContains)
                        rep.facets[j].vertexCodes[pCnt] = -1;                         
                    else
                        // vertex inside and on  a cut, hide
                        rep.facets[j].vertexCodes[pCnt] = lColorIndex + 10;     
//System.out.println("inside");                                    
                }   // inside 
                
            } // for vertex codes
            // vertex labels
            for (int pCnt = 0; pCnt < rep.facets[j].numPoints; pCnt++)
            {   // find point
                Vector3D start = rep.facets[j].points[pCnt];
                int index = Facet3D.containsVertex(facet, start);
                if (index >= 0)
                {   if (facet.vertexLabels[index] != null)
                        rep.facets[j].vertexLabels[pCnt] = 
                            new String(facet.vertexLabels[index]);
                }       
            }
            
            
                            // 4) voeg de vertices van dit nieuwe facet die op
                            // de lijn liggen aan linePoints
                            for (int m = 0; m < rep.facets[j].numPoints; m++)
                            {   if (line.contains(rep.facets[j].points[m]) &&
                                    !linePoints.contains(rep.facets[j].points[m])
                                   )
                                    linePoints.addElement(new Vector3D(rep.facets[j].points[m]));
                            }
                        } //for (int j = 0; j < rep.numFacets; j++)
                        if (rep.numVertices > 0)
                            facet.visible = false;
                    }
                    // line intersects tPlane in a point
                    else if (isType == 1)
                    {   
                        
                        // vindt snijpunt
                        Vector3D isPoint = Plane3D.getIntersectionPoint(line, tPlane);
                        // kijk eerst of de lijn echt het facet (binnenin 
                        // of op de rand dus) snijdt
                        if (Facet3D.containsPoint(facet, isPoint))
                        {   
                            
//System.out.println("tPlane = " + tPlane.toString());                                                                            
//System.out.println("isPoint = " + isPoint.toString());                                                    
                            
                            // check if isPoint is a vertex
                            int vIndex = Facet3D.containsVertex(facet, isPoint);
                            if (vIndex >= 0)
                            {   // copy the facet into a new one
                                // with a blue vertex
                                int[] newInds = new int[facet.numPoints];                                    
                                int indexCount = 0;                                                                        
                                for (int k = 0; k < facet.numPoints; k++)
                                {   newInds[indexCount] = replacement.numVertices;
                                    indexCount++;
                                    replacement.addVertex(
                                        new Vector3D(facet.points[k]), null);
                                }
                                Facet3D eFacet = new Facet3D(replacement.vertices, newInds, facet.color);
                                Facet3D.copyAttributes(facet, eFacet, true);
                                // labels of new point/edge 
                                // just copied
                                if (facet.vertexCodes[vIndex] < 0)                                    
                                    eFacet.vertexCodes[vIndex] = - 1;
                                else if (facet.vertexCodes[vIndex] < 10)                                    
                                    eFacet.vertexCodes[vIndex] = lineColorIndex; 
                                else if (facet.vertexCodes[vIndex] < 20)                                    
                                    eFacet.vertexCodes[vIndex] = lineColorIndex + 10; 
                                    
                                eFacet.isReplacementOf = facet;
                                replacement.addFacet(eFacet);
                                facet.visible = false;

                                // add isPoint to linePoints
                                if (!linePoints.contains(isPoint))
                                    linePoints.addElement(new Vector3D(isPoint));
//System.out.println("isT = 1 and vertex");                                     
                            } // isPoints is a vertex    
                            else // isPoint not a vertex
                            {   // check if isPoint is on an edge
                                int eIndex = Facet3D.edgeContainsPoint(
                                                facet, isPoint);
                                if (eIndex >= 0)
                                {   // make a new facet by adding isPoint 
                                    // on edge eIndex
// note: the facet containing isPoint on the reversed edge is 
// treated identically when found
//System.out.println("isT = 1 and edge");                                                                         
                                    //first vertex becomes isPoint
                                    int[] newInds = new int[facet.numPoints + 1];                                    
                                    int indexCount = 0;                                                                        
                                    newInds[indexCount] = replacement.numVertices;
                                    indexCount++;
                                    replacement.addVertex(new Vector3D(isPoint), null);                                    
                                    for (int k = eIndex + 1; 
                                         k < facet.numPoints + eIndex + 1; k++)
                                    {   newInds[indexCount] = replacement.numVertices;
                                        indexCount++;
                                        replacement.addVertex(
                                            new Vector3D(facet.points[k % facet.numPoints]), null);
                                    }
                                    Facet3D eFacet = new Facet3D(replacement.vertices, newInds, facet.color);
                                    Facet3D.copyAttributes(facet, eFacet, false);
                                    // labels of new point/edge 
                                    // just copied
                                    if (facet.edgeCodes[eIndex] < 0)                                    
                                        eFacet.vertexCodes[0] = - 1;
                                    else
                                        eFacet.vertexCodes[0] = lineColorIndex; //facet.vertexCodes[eIndex];                                    
                                    eFacet.edgeCodes[0] = facet.edgeCodes[eIndex];
                                    // copy labels of "old points"
                                    // note the shift!!
                                    for (int m = 0; m < facet.numPoints; m++)            
                                    {   eFacet.vertexCodes[m + 1] = 
                                            facet.vertexCodes[(eIndex + 1 + m) % facet.numPoints];
                                        eFacet.edgeCodes[m + 1] = 
                                            facet.edgeCodes[(eIndex + 1 + m) % facet.numPoints];
                                        if (facet.vertexLabels[(eIndex + 1 + m) % facet.numPoints] != null)    
                                            eFacet.vertexLabels[m + 1] = new String(
                                                facet.vertexLabels[(eIndex + 1 + m) % facet.numPoints]);    
                                    } 
                                    
                                    // tick mark info??        
        
                                    eFacet.isReplacementOf = facet;
                                    replacement.addFacet(eFacet);
                                    facet.visible = false;
// opmerking: dit zie je helemaal niet
// maar wel als je later snijpunten will laten zien
                                    // add isPoint to linePoints
                                    if (!linePoints.contains(isPoint))
                                        linePoints.addElement(new Vector3D(isPoint));
                                } // isPoint on inside of an edge
                                else // isPoint inside the facet
                                {   // hier alleen punt aan stukje toevoegen
                                    // later: facet uitbreiden met hulplijnen?
                                    // add isPoint to linePoints
                                    if (!linePoints.contains(isPoint))
                                        linePoints.addElement(new Vector3D(isPoint));
//System.out.println("isT = 1 and inner");                                                                                                                 
                                }       
                            } // isPoint not a vertex
                        } // isPoint on facet
                        // else do nothing
                    }
                    // line parallel to tPlane
                    else // isType == 0
                    {   // nothing to do
//System.out.println("isType = 0");                    
//System.out.println("tPlane = " + tPlane.toString());                                                                            
                    }
                } // if (facet.numPoints > 2)
            } // facet.visible
        } // facet loop
        // make array of linePoints
        // note that linePoints is completely redundant
        Vector3D[] points = new Vector3D[linePoints.size()];

//System.out.println("lpoints = " + linePoints.size());   
//System.out.println("lextpoints = " + lineExtPoints.size());   

        for (int i = 0; i < linePoints.size(); i++)
        {    points[i] = (Vector3D) linePoints.elementAt(i);
//System.out.println("lp[" + i + "]= " + UF.format(points[i].x, 1) +
//                   " & " + UF.format(points[i].y, 1) +
//                   " & " + UF.format(points[i].z, 1));
        }
        // take a plane orthogonal to line, normal vector is direction
        // plane goes through line.support thus equation 
        // line.direction.x*X + line.direction.y*Y + line.direction.z*Z =
        //                                  (line.direction, line.support)
        // exact position of any point p ON the line relative to this plane
        // is (line.direction, p) - (line.direction, line.support)
        double[] positions = new double[linePoints.size()];
        for (int j = 0; j < linePoints.size(); j++)
            positions[j] = Vector3D.dotProduct(line.direction, points[j]) -
                           Vector3D.dotProduct(line.direction, line.support); 
        positionSort(positions, points);

        // het eerste en laatste punt van points zijn nu de beginpunten van 
        // de verlengde lijnen
        if (points.length >= 2)
        {    lineExtStart1 = new Vector3D(points[0]);
             lineExtStart2 = new Vector3D(points[points.length - 1]);
        }
        
        // nu: de lijn wordt door het extended object in de stukjes
        // points[j]->points[j+1] gehakt
        // die worden alleen een 2-dim facet als
        // ze niet al een edge van een facet van replacement zijn
        for (int k = 0; k < points.length - 1; k++)
        {   Vector3D start = points[k];
            Vector3D end = points[k + 1];
            // here replacement is an object of which the 
            // facet-array can be accessed, check all facets to enable drawing
            // in solid mode
            Facet3D rf = replacement.facetContaining(start, end, true);
            if (rf == null)
            {   int[] newInds = new int[2];
                newInds[0] = replacement.numVertices;
                replacement.addVertex(new Vector3D(start), null);
                newInds[1] = replacement.numVertices;
                replacement.addVertex(new Vector3D(end), null);
                Facet3D segFacet = new Facet3D(replacement.vertices,
                    newInds, Grafiek3DComponent.black); // any color OK never filled
                // attributes OK
                // inner segment
                segFacet.normal = new Vector3D();
                // override edgeCodes
                segFacet.edgeCodes[0] = lineColorIndex + HIDDENSHIFT;
                segFacet.edgeCodes[1] = lineColorIndex + HIDDENSHIFT;                
// determine vertexCodes

                Vector construction = null;
                if (origObjectGroup instanceof ObjectWithLine)
                    construction = ((ObjectWithLine) origObjectGroup).getConstruction();
                else if (origObjectGroup instanceof ObjectWithPlane)
                    construction = ((ObjectWithPlane) origObjectGroup).getConstruction();
                if (Vector3D.equals(start, points[0]))
                {    
// check if any segment of origObjectGroup contains the segFacet.points[0]
boolean edgeContains0 = false;
for (int i = 0; i < origObjectGroup.numFacets; i++)
{   edgeContains0 = edgeContains0 || 
        (Facet3D.edgeContainsPoint(origObjectGroup.facets[i], segFacet.points[0]) >= 0);
}
                    
                    if (edgeContains0)                    
                        segFacet.vertexCodes[0] = -1;
                    else // "isolated vertex" inner point of an outer facet
                        segFacet.vertexCodes[0] = pointColorIndex;
                
                
                }
                else  // point is an inner vertex  
                {   segFacet.vertexCodes[0] = lineColorIndex + HIDDENSHIFT;                
                    boolean onPlane = false;
                    if (construction != null)
                    {   for (int conCnt = 0; conCnt < construction.size(); conCnt++)
                        {   Object ob = construction.elementAt(conCnt);
                            // other planes
                            if (ob instanceof Plane3D)
                            {   Plane3D pl = (Plane3D) ob;
                                int plp = pl.planePosition(segFacet.points[0]);
                                if (plp == 0)
                                    onPlane = true;
                            }    
                        }
                    }    
                
                    if (onPlane)
                        segFacet.vertexLabels[0] = "XX";                
                }

                if (Vector3D.equals(end, points[points.length - 1]))
                {    
boolean edgeContains1 = false;
for (int i = 0; i < origObjectGroup.numFacets; i++)
{   edgeContains1 = edgeContains1 || 
        (Facet3D.edgeContainsPoint(origObjectGroup.facets[i], segFacet.points[1]) >= 0);    
}
                    
                    if (edgeContains1)                    
                        segFacet.vertexCodes[1] = -1;
                    else    // "isolated vertex" on inside of outer facet
                        segFacet.vertexCodes[1] = pointColorIndex;
                }
                else  // inner vertex  
                {    segFacet.vertexCodes[1] = lineColorIndex + HIDDENSHIFT;                
                     boolean onPlane = false;
                     if (construction != null)
                     {   for (int conCnt = 0; conCnt < construction.size(); conCnt++)
                         {   Object ob = construction.elementAt(conCnt);
                             // other planes
                             if (ob instanceof Plane3D)
                             {   Plane3D pl = (Plane3D) ob;
                                 int plp = pl.planePosition(segFacet.points[1]);
                                 if (plp == 0)
                                     onPlane = true;
                             }    
                         }
                     }    
                
                    if (onPlane)
                        segFacet.vertexLabels[1] = "XX";                                
                }
// fix vertexLabels
int startIndex = -1;
int endIndex = -1;
Facet3D startFacet = null;
Facet3D endFacet = null;
for (int fCnt = 0; fCnt < origObjectGroup.numFacets; fCnt++)
{   // keep looking
    if ((startFacet == null) || (endFacet == null))
    {   if (startFacet == null)
        {   int index = Facet3D.containsVertex(origObjectGroup.facets[fCnt], start);
            if (index >= 0)
            {   startIndex = index;
                startFacet = origObjectGroup.facets[fCnt];
            }    
        }
        if (endFacet == null)
        {   int index = Facet3D.containsVertex(origObjectGroup.facets[fCnt], end);
            if (index >= 0)
            {   endIndex = index;
                endFacet = origObjectGroup.facets[fCnt];
            }    
        }    
    }    
}
if ((startFacet != null) && (startFacet.vertexLabels[startIndex] != null))
    segFacet.vertexLabels[0] = new String(startFacet.vertexLabels[startIndex]);
if ((endFacet != null) && (endFacet.vertexLabels[endIndex] != null))
    segFacet.vertexLabels[1] = new String(endFacet.vertexLabels[endIndex]);

                // segFacet replaces nothing? NO
                replacement.addFacet(segFacet);
                
//System.out.println("segfacet created");                

            } // if (rf = null)
            
            //else nothing to do
        } // for creating segments (if any)
        
// wat als lijn objectgroup niet snijdt?
  
/*        
System.out.println(" owl-rep-v = " + replacement.numVertices +
                   " owl-rep-f = " + replacement.numFacets);       
*/        
        // set properties of replacement
        replacement.outlined = origObjectGroup.outlined;
        replacement.filled = origObjectGroup.filled;
        replacement.visible = origObjectGroup.visible;
// dit maar even laten        
//public Matrix3D oMat;
        // fix center and diameter as of originalObjectGroup
        replacement.initObject3D(true, new Vector3D(origObjectGroup.center),
                                 origObjectGroup.diameter, false);
        // puts replacement at objects.elementAt(1)                         
        if (replacement.numVertices > 0)        
            addObject3D(replacement);                         
    
        
        // het eerste en laatste punt van points zijn nu de beginpunten van 
        // de verlengde lijnen
        if ((points.length >= 2) && (lengthenFactor > 0))
        {    //lineExtStart1 = new Vector3D(points[0]);
             //lineExtStart2 = new Vector3D(points[points.length - 1]);
             extensions = new EmptyObject3D();                
             double length = Vector3D.distance(lineExtStart1, lineExtStart2);
             double extLength = lengthenFactor * length;
             // "uncut" extensions:
             // directional vector
             Vector3D lineExtEnd1 = Vector3D.minus(lineExtStart1, lineExtStart2);
             Vector3D.makeUnitary(lineExtEnd1);
             Vector3D.scaleBy(lineExtEnd1, extLength);
             lineExtEnd1 = Vector3D.plus(lineExtStart1, lineExtEnd1);
             
             Vector3D lineExtEnd2 = Vector3D.minus(lineExtStart2, lineExtStart1);
             Vector3D.makeUnitary(lineExtEnd2);
             Vector3D.scaleBy(lineExtEnd2, extLength);
             lineExtEnd2 = Vector3D.plus(lineExtStart2, lineExtEnd2);
             
             int index = extensions.numVertices;
             extensions.addVertex(new Vector3D(lineExtStart1), null);
             extensions.addVertex(new Vector3D(lineExtEnd1), null);
             int[] inds = new int[2];
             inds[0] = index;
             inds[1] = index + 1;
             Facet3D extFacet1 = new Facet3D(extensions.vertices, inds, Grafiek3DComponent.black);
             
             extFacet1.edgeCodes[0] = lineColorIndex + 40;
             extFacet1.edgeCodes[1] = lineColorIndex + 40;
             // color by normal
             extFacet1.vertexCodes[0] = -1;//lineColorIndex;
             // no thickening
             extFacet1.vertexCodes[1] = -1;
             
             
             extensions.addFacet(extFacet1);
             
             index = extensions.numVertices;
             extensions.addVertex(new Vector3D(lineExtStart2), null);
             extensions.addVertex(new Vector3D(lineExtEnd2), null);
             inds = new int[2];
             inds[0] = index;
             inds[1] = index + 1;
             Facet3D extFacet2 = new Facet3D(extensions.vertices, inds, Grafiek3DComponent.black);
             extFacet2.edgeCodes[0] = lineColorIndex + 40;
             extFacet2.edgeCodes[1] = lineColorIndex + 40;
             
             // color by normal
             extFacet2.vertexCodes[0] = -1;//lineColorIndex;
             // no thickening
             extFacet2.vertexCodes[1] = -1;
             
             extensions.addFacet(extFacet2);

// Opmerking: de nieuwe extensies zijn nu niet correct doorgesneden
// een "vorige" extensie is wel doorgesneden
// is dit erg?


            // set properties of extension
            extensions.visible = origObjectGroup.visible;
        // dit maar even laten        
        //public Matrix3D oMat;
            // fix center and diameter as of originalObjectGroup
            extensions.initObject3D(true, new Vector3D(origObjectGroup.center),
                                    origObjectGroup.diameter, false);
            // puts extensions at objects.elementAt(2)                         
            if (extensions.numVertices > 0)        
                addObject3D(extensions);                         
             
        } // creation of line extensions


        // objects.elementAt(2)        
        // verlengde links en rechts
        // an object consisting of 1 two-dimensional facet
        // lengte lengthStep, vis rebiuld
// NB als je in een rebuild verlengden maakt dan kan 
// points[0] of points[points.length - 1] op een "vorige" verlengde
// liggen
// dus hoe uit te vinden waar de verlengen van deze lijn
// starten?
// OPLOSSING: kijk of de punten STRICT op een hogere verlengde liggen

// de "vorige" lijnen zijn al verlengd en als ze door deze lijn gesneden worden
// is "vorige" verlenging al versneden
// die punten zitten in lineExtPoints, nl. die != lineExtStart1 en lineExtStart2 (if any)
// dus nu:
// bepaal de EINDEN van de gewenste verlenging (llFactor)
// en kijk of de punten in lineExtPoints hier nog "tussen" liggen
// a.d.h.d. maak je 2 of meer stukjes



        
        // properties of this ObjectWithLine        
        sortSubArrays = origObjectGroup.sortSubArrays;        
        outlined = origObjectGroup.outlined;
        filled = origObjectGroup.filled;
        visible = origObjectGroup.visible;
// dit maar even laten        
//public Matrix3D oMat;
        // fix center and diameter as of originalObjectGroup
// lijnen verlengen: opnieuw diameter bepalen                
// dit gebeurt via een rebuild
        initObject3D(true, new Vector3D(origObjectGroup.center),
                     origObjectGroup.diameter, false);

// RESTRICTIE EPN
//if (Grafiek3DComponent.version == Grafiek3DComponent.EPN)
    hideNonOrigVertices();
// EINDE RESTRICTIE epn
// alleen voor EPN
inheritTickMarks();
    
// voor EPN OOK kijken of de vertex verborgen is,
// dan NIET labelen, OK!
//fixFacetArray();    
// lettering
// avoid lettering when OrigObjectGroup is a cut
if (lengthenFactor >= 0)
{
    
Vector newVertices = new Vector();
int labelCnt = origObjectGroup.numVertexLabels;
//System.out.println("labelCnt = " + labelCnt);
for (int fCnt = 0; fCnt < replacement.numFacets; fCnt++)
{   for (int vCnt = 0; vCnt < replacement.facets[fCnt].numPoints; vCnt++)
    {   String aLabel = replacement.facets[fCnt].vertexLabels[vCnt];
        if ((replacement.facets[fCnt].vertexCodes[vCnt] >= 0) &&
            ((aLabel == null) || aLabel.equals(""))
           ) 
        {   Vector3D aVertex = replacement.facets[fCnt].points[vCnt];
            int index = newVertices.indexOf(aVertex);
            // first occurence of this unlabeled vertex
            if (index < 0)
            {   newVertices.addElement(aVertex);
                labelCnt++;
                replacement.facets[fCnt].vertexLabels[vCnt] = getLabel(labelCnt);
            }
            // this is an unlabeled vertex which was given a label before
            else
            {   replacement.facets[fCnt].vertexLabels[vCnt] = 
                    getLabel(origObjectGroup.numVertexLabels + index + 1);
            }
        }    
//        else
//System.out.println("already labeled");        
        // else nothing to do  
    } // for facetpoints
} // for facets       
numVertexLabels = labelCnt;
//System.out.println("labelCnt = " + labelCnt);

} // if (lengthenFactor >= 0)

    }  // constructor  
    
    // A is label number 1, Z is number 26
    // AA is number 27
    public String getLabel(int i)
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
    // support
    public int positionSort(double[] positions, Vector3D[] points)
    {   // perform a Bubble sort on positions, sort also points
        double tDouble;
        Vector3D tPoint;
        boolean swapped;
        int swaps = 0;
        for (int i = positions.length - 1; i >= 0; i--)
        {   swapped = false;
            for (int j = 0; j < i; j++)
            {   
                if (positions[j] > positions[j + 1])
                {   tDouble = positions[j]; 
                    positions[j] = positions[j + 1];
                    positions[j + 1] = tDouble;
                    tPoint = points[j];
                    points[j] = points[j + 1];
                    points[j + 1] = tPoint;
                    swapped = true;
                    swaps++;
                }
            } // for       
            if (!swapped)
                return swaps;
        } // for
        return swaps;

    }


    // NB moet zo, repFacet kan ook de uiteindelijke replacement
    // van een lijn zijn, maar dat is dan een lijn in een facet
    // van origObject of een lijn in een cut!!
    // dus ga meteen maar naar de top replacement
    // dus niet noodz. als replacesOrigObject=false dan replacesCut=true
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
// hier op tijd eruit                        
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
// hier op tijd eruit                                                
                    //else
                    //    isOnInner = true;
                }
            }
        }
        return isOnExt;
    }    

    public double getLlFactor()
    {   return llFactor;
        
    }    
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

    //public boolean 

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
    
    
    public void inheritTickMarks()
    {   // nothing to do
//        if (Grafiek3DComponent.TICKNUM == 0)
//            return; 
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
            {   if (!replacesOrigObject(facets[i]))
                {   for (int j = 0; j < facets[i].numPoints; j++)
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
//DrawingPanel.showTime("OWL hiding vertices");        
    }    
    
// EINDE RESTRICTIE TOT ORIGINAL OBJECT VOOR EPN

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

    // must be bottom!
    public Vector getReplacements(Facet3D f)
    {   Vector result = new Vector();
        findReplacements(f, result);
        return result;
    }    
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
        // no replacements
//        if (result.size() == 0)
//            result.addElement(f);
//        return result;
    }

    // redefined replacements other than those of origObject should not    
    // filled (can be invisible) when origObject is filled
    public void setFilled(boolean fill)
    {   // this group
        filled = fill;
        origObjectGroup.setFilled(fill);
        for (int i = 0; i < replacement.numFacets; i++)
        {   // only tree leaves
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
                }    
                
                else if (replacesCut(replacement.facets[i]) != null)
                {   if (filled)
                    // now this thing is never filled and even invisible if
                    // the "surrounding object" is 
                        replacement.facets[i].visible = false;
                    else // surrounding object will not be filled    
                    {   replacement.facets[i].visible = true;
                        if (cutFilled)
                        {   replacement.facets[i].filled = true; // testing filled cuts
                            replacement.facets[i].color = planeColor;
                        }
                        else    
                            replacement.facets[i].filled = false;            
                    }   
                }
                // internal segment
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
                    
            }    
        } // for replacement facets
    }

    public void fillCuts(boolean b)
    {   cutFilled = b;
        if (origObjectGroup instanceof ObjectWithPlane)
            ((ObjectWithPlane) origObjectGroup).fillCuts(b);
        else if (origObjectGroup instanceof ObjectWithLine)
            ((ObjectWithLine) origObjectGroup).fillCuts(b);        
        // else do nothing    

        // only the largest object can be a ObjectWithPoint
    }    

// hier: uitvinden of een (2-dim)-facet een verlengde lijn is
// ook in ObjectWithPlane
// dan kan je voorkomen dat die aangeklikt worden


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

    // put the "recipe" for this OwL in Vector recipe
    // list is in reverse order!!
    public void getConstructionList(Vector recipe)
    {   recipe.addElement(new Line3D(line.point1, line.point2));
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
    {   recipe.addElement(new Integer(lineColorIndex));
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
        ObjectWithLine copy = new ObjectWithLine();
        makeDeepGroupCopy(copy);
        copy.origObjectGroup = (ObjectGroup3D) copy.objects.elementAt(0);
        copy.origObject = copy.origObjectGroup.leftMostLeaf();                
        copy.replacement = (Object3D) copy.objects.elementAt(1);
        copy.lineColorIndex = lineColorIndex;
        // constructor of Line3D deep copies line.point1 and line.point2
        copy.line = new Line3D(line.point1, line.point2);
        copy.llFactor = llFactor;
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

}   // class ObjectWithLine


