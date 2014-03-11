package fi.doorziengwt.client;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.Serializable;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;


// abstract class, subclasses MUST initialize facets in some way and call
// initObject3D(true) for initializing the matrix and finding sizes
// a 3D object
public abstract class Object3D implements Serializable
{   // essential attributes    
    
    // number of vertices
    int numVertices;
    // the vertices
    Vector3D[] vertices;
    // transformed 
    Vector3D[] trVertices;
    // some kind of text label for the vertices
    String[] vertexLabels;
    // number of facets
    int numFacets;
    // facets
    Facet3D[] facets;
    
    // look and feel
    
    // true of all facets are supposed to be outlined
    // also used in setting facets empty
    boolean outlined = true;
    // true of all facets are supposed to be filled
    // also used in setting facets empty
    boolean filled = true;
    // true if visible (as part of a group)
    boolean visible = true;
    
    // affine transformations world space -> view space
    // world space -> world space
    public Matrix3D oMat;
    
    // position and size
    
    // true if center set externally
    boolean centerSet = false;
    // true if diameter set externally
    boolean diamSet = false;
    // diameter set or as maximum distance any vertex to center
    double diameter = -1;
    // center set or as barycenter
    Vector3D center = null;
    
    // location in object tree
    
    // the objectgroup's parent in the tree
    Object3D parent = null;  // could be a group  
    
    // constructing the object

    int numVertexLabels;

    
    // voor EPN
    int modelCode;


    // also works for groups
    // for numTicks > 0 produces INDIVIDUAL tickmarks
    public void setTickMarks(int numTicks)
    {   fixFacetArray();
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
        {   Facet3D f = facets[fCnt];
            for (int eCnt = 0; eCnt < f.numPoints; eCnt++)
            {   if (numTicks == 0)
                {   f.tickStart[eCnt] = null;
                    f.tickStep[eCnt] = null;
                    f.numTicks[eCnt] = 0;
                    f.drawTicks[eCnt] = false;
                }
                else // numTicks > 0
                {   Vector3D eStart = f.points[eCnt];
                    Vector3D eEnd = f.points[(eCnt + 1) % f.numPoints];
                    if (!eStart.equals(eEnd))
                    {   f.numTicks[eCnt] = numTicks;
                        f.drawTicks[eCnt] = true;                    
                        Vector3D edgeDir = Vector3D.minus(eEnd, eStart);
                        double tLength = Vector3D.length(edgeDir) /
                                         (numTicks + 1);
                        Vector3D tick = new Vector3D(edgeDir);
                        Vector3D.makeUnitary(tick);
                        Vector3D.scaleBy(tick, tLength);
                        f.tickStart[eCnt] = Vector3D.plus(eStart, tick);
                        if (numTicks > 1)
                            f.tickStep[eCnt] = Vector3D.plus(f.tickStart[eCnt], tick);
                    }
                    else // reset
                    {   f.numTicks[eCnt] = 0;
                        f.drawTicks[eCnt] = false;                    
                    }    
                }    
    
            }
        }
    }
    
    // only for objects!!!
    // redefine as sum for groups
    public double getVolume()
    {   
//System.out.println("facets = " + numFacets);        
        double volume = 0;
        // find barycenter
        double bx = 0, by = 0, bz = 0;
        for (int vCnt = 0; vCnt < numVertices; vCnt++)
        {   bx += vertices[vCnt].x;
            by += vertices[vCnt].y;
            bz += vertices[vCnt].z;
        }    
        Vector3D bary = new Vector3D(bx / numVertices, 
                                     by / numVertices, 
                                     bz / numVertices);
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
        {   Facet3D f = facets[fCnt];
            // find line through bary perpendicular to facet
            // line goes through bary and bary+f.normal
            // assume f has non-zero normal and spans a plane
            Vector3D point = Vector3D.plus(bary, f.normal);
            Line3D perpLine = new Line3D(bary, point);
            // cut the line with the plane through f
            Plane3D fPlane = new Plane3D(f.normal.x, f.normal.y, f.normal.z,
                                         Vector3D.dotProduct(f.normal, f.points[0]));
            Vector3D perpPoint = Plane3D.getIntersectionPoint(perpLine, fPlane);
            double baseArea = Facet3D.getSurfaceArea(f);
            double height = Vector3D.distance(bary, perpPoint);
            volume += baseArea * height / 3;
        }
        return volume;
    }
    
    // updating an object
    // redefine for object groups
    public void updatePoints()
    {   for (int fCnt = 0; fCnt < numFacets; fCnt++)
            facets[fCnt].updatePoints(vertices);
    }
    // enumerating the vertices, we only need REFERENCE(S) to the actual array
    // redefine for object groups
    public Vector enumerateVertices()
    {   Vector enumer = new Vector();
        enumer.addElement(vertices);
        return enumer;
    }
    // add a vertex, everything else unchanged
    // use only for objects, since object groups have no 
    // vertex arrays
    // take care of transformed vertices(!) and
    // vertexLabels
    public void addVertex(Vector3D v, String label)
    {   // no vertices yet
        if (numVertices == 0)
        {   vertices = new Vector3D[1];
            vertexLabels = new String[1];
        }    
        else // enlarge
        {   Vector3D[] temp = vertices;
            String[] tLabels = vertexLabels;
            vertices = new Vector3D[numVertices + 1];
            vertexLabels = new String[numVertices + 1];
            System.arraycopy(temp, 0, vertices, 0, numVertices);
            System.arraycopy(tLabels, 0, vertexLabels, 0, numVertices);
        }
        vertices[numVertices] = v;
        vertexLabels[numVertices] = label;
        numVertices++;
        // only needed for painting
        trVertices = new Vector3D[numVertices];
    }    

    // add a facet, everything else unchanged
    // use only for objects, since object-group facet arrays are 
    // constructed from object facet arrays
    public void addFacet(Facet3D f)
    {   // no facets yet
        if (numFacets == 0)
            facets = new Facet3D[1];
        else // enlarge   
        {   Facet3D[] temp = facets;
            facets = new Facet3D[numFacets + 1];
            System.arraycopy(temp, 0, facets, 0, numFacets);
        }
        facets[numFacets] = f;
        numFacets++;
    }    
    
    // finding special facets
    
    // find all VISIBLE facets of this object (group) containing vertex v
    // works also for object groups
    public Vector facetsContaining(Vector3D v)
    {   fixFacetArray();
        Vector result = new Vector();
        for (int i = 0; i < numFacets; i++)
        {   if (facets[i].visible &&
                (Facet3D.containsVertex(facets[i], v) >= 0)
               ) 
               result.addElement(facets[i]);
        }
        return result;
    }
    // find a VISIBLE (invisible) facet of this object (group) containing 
    // the directed edge v1->v2
    // works also for object groups
    public Facet3D facetContaining(Vector3D v1, Vector3D v2,
                                   boolean includeInvisibles)
    {   fixFacetArray();
        Facet3D result = null;
        for (int i = 0; i < numFacets; i++)
        {   if ((facets[i].visible || includeInvisibles) &&
                (Facet3D.containsEdge(facets[i], v1, v2) >= 0)
               ) 
               return facets[i];
        }
        return result;
    }
    
    
    
    
    
    // finding vertex properties
    
    // find the text associated with vertex v
    // redefine
    public String vertexText(Vector3D vertex)
    {   String result = null;
        int index = containsVertex(vertex);
        if (index >= 0)
            return vertexLabels[index];
        return result;
    }


    // find index of Vertex v if any
    // compare absolutely
    // only for objects!
    public int containsVertex(Vector3D vertex)
    {   int result = -1;
        for (int i = 0; i < numVertices; i++)
        {   if (Vector3D.equals(vertices[i], vertex))
               return i;
        }
        return result;
    }

    // find index of facet f if any
    // compare by reference
    // works also for groups
    public int containsFacet(Facet3D f)
    {   fixFacetArray();
        int result = -1;
        for (int i = 0; i < numFacets; i++)
        {   if (facets[i] == f)
               return i;
        }
        return result;
    }
    
    // initializing object(groups), still some redundancy here
    // as to setting center and diameter
    
    // works for groups since relevant methods redefined
    public void initObject3D(boolean newObject, boolean centerObject)
    {   if (newObject)
        {  
//            oMat = new TMatrix3D();
            if (numVertices > 0)
            vertexLabels = new String[numVertices];
        }
        if (center == null)
            findCenter();
        if (centerObject)    
            center();
        if (diameter < 0)
            findDiameter();
            
    }
    
    // (some) parametrized surfaces (tubed helix, sea shell)
    // center can be set, force recalculation of diameter(s) 
    // by putting diameter of top object group back to -1
    // centerSet prevents the center from being recalculated
    // note: the object(group) is centered at c when
    // centerObject is true
    // works for groups since relevant methods redefined    
    public void initObject3D(boolean newObject, Vector3D c, boolean centerObject)
    {   center = c;
        centerSet = true;
        initObject3D(newObject, centerObject);
    }
    // (some) parametrized surfaces
    // centerSet prevents the center from being recalculated    
    // diamSet prevents the diameter from being recalculated
    // works for groups since relevant methods redefined    
    public void initObject3D(boolean newObject, Vector3D c, double d,
                             boolean centerObject)
    {   center = c;
        centerSet = true;
        diameter = d;
        diamSet = true;
        initObject3D(newObject, centerObject);
    }
    
    
    // note: we can (and should not) instanciate Object3D
    // to make a copy of an object AND maintain its subclass
    // type (as a subclass of Object3D) instanciate an empty
    // object of the required subclass type (which is allowed)
    // and call the following method
    public void makeDeepObjectCopy(Object3D copy)
    {   // deep copy of vertices
        copy.numVertices = numVertices;
        copy.vertices = new Vector3D[numVertices];
        for (int i = 0; i < numVertices; i++)
            copy.vertices[i] = new Vector3D(vertices[i]);
        copy.trVertices = new Vector3D[numVertices];
        // deep copy of labels
        copy.numVertexLabels = numVertexLabels;
        copy.vertexLabels = new String[numVertices];
        for (int j = 0; j < numVertices; j++)
        {   if (vertexLabels[j] != null)
                copy.vertexLabels[j] = new String(vertexLabels[j]);
        }
        copy.numFacets = numFacets;
        copy.facets = new Facet3D[numFacets];
        // now make COMPLETELY new facets
        for (int k = 0; k < numFacets; k++)
        {   int[] inds = new int[facets[k].numPoints];
            for (int l = 0; l < facets[k].numPoints; l++)
                inds[l] = facets[k].indices[l];
            copy.facets[k] = new Facet3D(copy.vertices, inds,
                                         facets[k].color);
            // now copy attributes of facets[k]                             
            Facet3D.copyAttributes(facets[k], copy.facets[k], true); 
        }
        // copy object attributes 
        copy.outlined = outlined;
        copy.filled = filled;
        copy.visible = visible;
// dit maar even laten        
//public Matrix3D oMat;
        copy.centerSet = centerSet;
        copy.diamSet = diamSet;
        copy.diameter = diameter;
        copy.center = new Vector3D(center);
        
        copy.modelCode = modelCode;
        //Object3D parent = null;
        
        
    }    
    // redefine this in the subclasses
    public abstract Object3D deepCopy();
    
    // find the maximum distance between any vertex and the objects center
    // redefine for subclass ObjectGroup3D
    public void findDiameter()
    {   if (diamSet)
            return;
        diameter = 0;
        double temp;
        for (int i = 0; i < numVertices; i++)
        {   temp = 2 * Vector3D.distance(vertices[i], center);
            if (temp > diameter)
                diameter = temp;
        }
    }
    

    public double getDiameter()
    {   double diam = 0;
        double temp;
        for (int i = 0; i < numVertices; i++)
        {   temp = 2 * Vector3D.distance(vertices[i], center);
            if (temp > diam)
                diam = temp;
        }
        return diam;
    }
    
    // find the center of the object as the barycenter of all vertices
    // redefine for subclass ObjectGroup3D
    public void findCenter()
    {   if (centerSet)
            return;
        double cx = 0;
        double cy = 0;
        double cz = 0;
        for (int i = 0; i < numVertices; i++)
        {   cx += vertices[i].x;
            cy += vertices[i].y;
            cz += vertices[i].z;
        }
        cx /= numVertices;
        cy /= numVertices;
        cz /= numVertices;
        center = new Vector3D(cx, cy, cz);
    }

    public Vector3D getCenter()
    {   
        double cx = 0;
        double cy = 0;
        double cz = 0;
        for (int i = 0; i < numVertices; i++)
        {   cx += vertices[i].x;
            cy += vertices[i].y;
            cz += vertices[i].z;
        }
        cx /= numVertices;
        cy /= numVertices;
        cz /= numVertices;
        return new Vector3D(cx, cy, cz);
    }
    
    // redundant? 
    // set the object(group)'s center externally
    public void setCenter(double cx, double cy, double cz)
    {   center = new Vector3D(cx, cy, cz);
        centerSet = true;
    }    

    // basic manipulations in world space
    // bij het samenstellen van een groep
    // doen we nog iets met oMat?

    // gebruik dit b.v. bij constructie van objecten    
    // translate the object over -center where center is set or calculated
    // the new center of the object will be  (0,0,0)
    // redefine for subclass ObjectGroup3D
    public void center()
    {   for (int i = 0; i < numVertices; i++)
            Vector3D.translateBy(vertices[i], - center.x, - center.y, - center.z);
        center = new Vector3D(); // (0,0,0)
        for (int j = 0; j < numFacets; j++)
            facets[j].updatePoints(vertices);
        
    }
    // move the object and its center over (cx, cy, cz)
    // if the center was (0,0,0) the new center is (cx, cy, cz)
    // redefine for subclass ObjectGroup3D    
    public void translateBy (double cx, double cy, double cz)
    {   Vector3D.translateBy(center, cx, cy, cz);
        for (int i = 0; i < numVertices; i++)
            Vector3D.translateBy(vertices[i], cx, cy, cz);
        for (int j = 0; j < numFacets; j++)
            facets[j].updatePoints(vertices);
    }

    // put the center of the object at (cx, cy, cz)
    // redefine for subclass ObjectGroup3D        
    public void centerAt (double cx, double cy, double cz)
    {   // vertices FIRST!!
        for (int i = 0; i < numVertices; i++)
            Vector3D.translateBy(vertices[i], cx - center.x, cy - center.y, cz - center.z);
        Vector3D.translateBy(center, cx - center.x, cy - center.y, cz - center.z);            
        for (int j = 0; j < numFacets; j++)
            facets[j].updatePoints(vertices);
        
    }

    
// hier iets met transleren om een versneden object uit elkaar
// te halen, doe dit door stapsgewijs oMat te veranderen
// elders centrum en diameter opnieuw uitrekenen
// of: maak een deep copy in world space en haal die uit elkaar
// irreversable
    
    
    // redefine for subclass ObjectGroup3D        
    public void fixFacetArray()
    {
     // nothing to do, to be redefined   
    }    
    // find the topmost objectgroup in the object tree
    // no need to redefine
    public Object3D topParent()
    {   if (parent == null)
            return this;
        else // recurse
            return parent.topParent();
    }    
    // redefine!!
    // could be an object group
    public Object3D leftChild()
    {   return this;
    }    
    // redefine!!
    // always an object
    public Object3D leftMostLeaf()
    {   return this;
    }    
    
    // painting
    public void paintObject3D(Context2d g, boolean shadow, boolean inside, 
                              double dis, Matrix3D mat, int paintType,
                              boolean retransform)
    {   
        fixFacetArray();
        // transform world object to view space, sort the facets
        if (retransform)
            transformBy(mat, dis, true);
        else
            transformBy(null, dis, true);
            
        // gebruikt bij een gevulde figuur
        // NB alle vlakken en inner segments zijn invisible!
        if (paintType == Object3DContainer.NZMINFIRST)
        {   // back facing facets first
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible && 
                    !visFromD(facets[i], dis, mat.origin)
                   )
                {   facets[i].paintFacet3D(g, shadow, inside, dis, mat, null);
                } // if (facets[i].visible)
            } // for back facing facets

            // front facing facets after that
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible && 
                    visFromD(facets[i], dis, mat.origin)
                    )
                {   facets[i].paintFacet3D(g, shadow, inside, dis, mat, null);
                } // if (facets[i].visible)
            } // for front facing facets

            // als de objecten convex en gevuld zijn dan is XXXX-clicked 
            // correct vanwege isCovered en de onzichtbare 
            // binnenzooi
        }
        else if (paintType == Object3DContainer.PUREZ)
        {
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible)
                {   facets[i].paintFacet3D(g, shadow, inside, dis, mat, null);
                } // if (facets[i].visible)
            } // for facets
            // nothing to correct here        
            // dus bij XXXX-clicked geen mergeSort
        }
        // niet gebruikt?
        // nee, voor verlengde lijnen MOET je NZMINFIRST
        // gebruiken
        else if (paintType == Object3DContainer.NONZMIN)
        {
            // only front facing facets
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible && 
                    visFromD(facets[i], dis, mat.origin)
                    )
                {   facets[i].paintFacet3D(g, shadow, inside, dis, mat, null);
                } // if (facets[i].visible)
            } // for front facing facets
            
            // als de objecten convex en gevuld zijn dan is XXXX-clicked 
            // correct vanwege isCovered en de onzichtbare 
            // binnenzooi

        }
        
        // dit wordt gebruikt als de vlakken niet gevuld zijn en bij 
        // de doorzichtige fold-out
        // de vlakken worden dan in het eerste of derde deel getekend
        // de line extensions en inner segments worden in het tweede 
        // deel getekend voor XXXX-clicked is dit correct
        // zolang je niet op het snijpunt van twee getekende
        // edges klikt
        // bij de doorzichtige fold-out is dit hetzelfde als
        // NZMINFIRST
        else if (paintType == Object3DContainer.HYBRID1)
        {   
//System.out.println("HYBRID1");            
            // first the non-filled back-facing facets which are not segments
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible && !facets[i].filled &&
                    (facets[i].numPoints > 2) &&
                     !visFromD(facets[i], dis, mat.origin)
                    )
                {   facets[i].paintFacet3D(g, shadow, inside, dis, mat, null);
                } // if (facets[i].visible) etc.
            } // for facets
            // segments if any
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible && 
                    (facets[i].filled || (facets[i].numPoints == 2))
                    )
                {   facets[i].paintFacet3D(g, shadow, inside, dis, mat, null);
                } // if (facets[i].visible) etc.
            } // for facets
            // then the non-filled front facing facets, which are not segments
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible && !facets[i].filled &&
                    (facets[i].numPoints > 2) &&
                    visFromD(facets[i], dis, mat.origin)
                    )
                {   facets[i].paintFacet3D(g, shadow, inside, dis, mat, null);
                } // if (facets[i].visible) etc.
            } // for facets
            // wel corectie bij XXXX-clicked als je CABRI wilt
        }

        // HYBRID2 is HYBRID1 locally corrected                
        // for planesFilled
        // in deel twee worden de line extensions,
        // de inner segments en de gevulde vlakken
        // getekend
        // volgorde binnen elk deel: zValue
        // zie SEMIEXACT
        // niet correct voor XXXX-clicked
        else if (paintType == Object3DContainer.HYBRID2)
        {   
            
            // zie boven, de line extensions zouden meemoeten in het eerste
            // of in het derde deel

//System.out.println("HYBRID2");            
            // first the non-filled back-facing facets which are not segments
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible && !facets[i].filled &&
                    (facets[i].numPoints > 2) &&
                     !visFromD(facets[i], dis, mat.origin)
                    )
                {   facets[i].paintFacet3D(g, shadow, inside, dis, mat, null);
                } // if (facets[i].visible) etc.
            } // for facets
            // then the filled facets and INNER segments in ONE GO, will be PUREZ
            // line extensions zijn hier ook
            // segments are never filled
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible && 
                    (facets[i].filled || (facets[i].numPoints == 2))
                    )
                {   facets[i].paintFacet3D(g, shadow, inside, dis, mat, null);
                } // if (facets[i].visible) etc.
            } // for facets
            // then the non-filled front facing facets, which are not segments
            // and the front facing line extensions
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible && !facets[i].filled &&
                    (facets[i].numPoints > 2) &&
                    visFromD(facets[i], dis, mat.origin)
                    )
                {   facets[i].paintFacet3D(g, shadow, inside, dis, mat, null);
                } // if (facets[i].visible) etc.
            } // for facets
            
            // wel correctie voor XXXX-clicked nodig           
        } // HYBRID2 locally corrected        
       
        
        // vlakdeeltjes en inner segments zitten altijd in deel 2
        // de line extensions in deel 1 of deel 3, maar soms
        // verkeerd, check visFromD voor line extensions
        else if (paintType == Object3DContainer.SEMIEXACT)
        {   
            // hier een deel van de z-waarden verbeteren als volgt:
            // zorg dat maxBackZ < minFrontZ en dat je daartussinin
            // nog een range overhoud voor het gecorrigeerde middendeel
            // opnieuw sorteren i.v.m. rekentijd
            // check wat sneller is
//System.out.println("SEMIEXACT");            

            int[] tRecalcFacets = new int[numFacets];
            int recalcNum = 0; 
            
            // first the non-filled back-facing facets which are 
            // not inner segments, note that any segment is 
            // never filled
            double maxBackZ = - Vector3D.NInf;            
//int fCnt = 0;            
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible 
                    && 
                    !facets[i].filled 
                    &&
                    ((facets[i].numPoints > 2) ||
                     ((facets[i].numPoints == 2) &&
                      !facets[i].unitNormal.equals(new Vector3D()))
                    )  
                    &&
                    !visFromD(facets[i], dis, mat.origin)
                   )
                {   //facets[i].paintFacet3D(g, shadow, inside, dis, mat, null);
//fCnt++;                
                    maxBackZ = Math.max(maxBackZ, facets[i].zValue);
                } // if (facets[i].visible) etc.
            } // for back facing facets

//System.out.println("back = " + fCnt);            
//System.out.println("maxBackZ = " + UF.format(maxBackZ, 2));            

            // then the filled facets and INNER segments in ONE GO, will be PUREZ
            // line extensions zijn hier ook
            // segments are never filled
//fCnt = 0;            
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible 
                    && 
                    (facets[i].filled || 
                     ((facets[i].numPoints == 2) && 
                      facets[i].unitNormal.equals(new Vector3D())
                     )
                    )  
                   )
                {   //facets[i].paintFacet3D(g, shadow, inside, dis, mat, null);
                    tRecalcFacets[recalcNum] = i;
                    recalcNum++;    
//fCnt++;                
                } // if (facets[i].visible) etc.
            } // for middle facets
//System.out.println("middle = " + fCnt);                        

            // then the non-filled front facing facets, which are not segments
            // and the front facing line extensions
            double minFrontZ = Vector3D.NInf;
//fCnt = 0;            
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible 
                    && 
                    !facets[i].filled 
                    &&
                    ((facets[i].numPoints > 2) ||
                     ((facets[i].numPoints == 2) &&
                      !facets[i].unitNormal.equals(new Vector3D()))
                    )  
                    &&
                    visFromD(facets[i], dis, mat.origin)
                   )
                {   //facets[i].paintFacet3D(g, shadow, inside, dis, mat, null);
//fCnt++;                
                    minFrontZ = Math.min(minFrontZ, facets[i].zValue);
                } // if (facets[i].visible) etc.
            } // for front facets
            
//System.out.println("front = " + fCnt);                        
//System.out.println("minFrontZ = " + UF.format(minFrontZ, 2));                        

            double correction = minFrontZ - maxBackZ - 3;
            double minMiddleZ = maxBackZ + 1;
            double maxMiddleZ = minFrontZ - correction - 1;
            
//double newMinFrontZ = Vector3D.NInf;            
            if (correction < 0)
            {   // laat back onveranderd
                // verschuif front over -correction
                for (int i = 0; i < numFacets; i++)
                {   if (facets[i].visible 
                        && 
                        !facets[i].filled 
                        &&
                        ((facets[i].numPoints > 2) ||
                         ((facets[i].numPoints == 2) &&
                          !facets[i].unitNormal.equals(new Vector3D()))
                        )  
                        &&
                        visFromD(facets[i], dis, mat.origin)
                       )
                    {   facets[i].zValue -= correction;
//newMinFrontZ = Math.min(newMinFrontZ, facets[i].zValue);                    
                    } // if (facets[i].visible) etc.
                } // for
                
                // middendeel krijgt zValues tussen
                // maxBackZ+1 en minFrontZ-correction-1            
                minMiddleZ = maxBackZ + 1;
                maxMiddleZ = minFrontZ - correction - 1;

//System.out.println("new minFrontZ = " + UF.format(newMinFrontZ, 2));                                        

            }    
            else
            {
                // middendeel krijgt zValues tussen
                // maxBackZ+1 en minFrontZ-1            
                minMiddleZ = maxBackZ + 1;
                maxMiddleZ = minFrontZ - 1;
                
            }    

//System.out.println("minMiddleZ = " + UF.format(minMiddleZ, 2));                        
//System.out.println("maxMiddleZ = " + UF.format(maxMiddleZ, 2));                        
            
//System.out.println("recalcNum = " + recalcNum);                
            int[] recalcFacets = new int[recalcNum];
            System.arraycopy(tRecalcFacets, 0, recalcFacets, 0, recalcNum);
/*            
for (int cnt = 0; cnt < recalcFacets.length; cnt++)
System.out.println("recalcIndex = " + recalcFacets[cnt]);                
*/
            if (recalcNum > 1)
                recalcZValues(recalcFacets, dis, mat.origin, 
                    minMiddleZ, maxMiddleZ);
            
            zMergeSort();
            // now paint
            for (int j = 0; j < numFacets; j++)
            {   if (facets[j].visible) 
                {   facets[j].paintFacet3D(g, shadow, inside, dis, mat, null);
                } // if (facets[i].visible) etc.
            } // for facets
            
                
        } // SEMIEXACT        

// "exacte" algorithme volgens Aad voor ALLE facets    
// dit zal straks te langzaam worden? JA
// zie semi-exact
        else if (paintType == Object3DContainer.EXACT)
        {   
            int[] tRecalcFacets = new int[numFacets];
            int recalcNum = 0;
            // skip invisible facets
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible)
                {  tRecalcFacets[recalcNum] = i;
                    recalcNum++;
                }
            }    
            
//System.out.println("recalcNum = " + recalcNum);                
            int[] recalcFacets = new int[recalcNum];
            System.arraycopy(tRecalcFacets, 0, recalcFacets, 0, recalcNum);
/*            
for (int cnt = 0; cnt < recalcFacets.length; cnt++)
System.out.println("recalcIndex = " + recalcFacets[cnt]);                
*/
            // these are symbolic
            double zMin = 1d;
            double zMax = (double) numFacets;
        
            //if (recalcNum > 1)
            //recalcZValues(facetsUsed, dis, mat.origin, zMin, zMax);
            
            zMergeSort();
            // now paint
            for (int j = 0; j < numFacets; j++)
            {   if (facets[j].visible) 
                {   facets[j].paintFacet3D(g, shadow, inside, dis, mat, null);
                } // if (facets[i].visible) etc.
            } // for facets
            
        } // EXACT
        
    } // paint
    
    public boolean visFromD(Facet3D f, double dis, Vector3D origin)
    {

// wat gebeurt hier precies
// idee is dat de kant van het vlak waar de unit normal
// heen wijst zichtbaar is vanuit eye, lijkt OK?
// wat gebeurt hier met segmenten?
// als segment naar oog toewijst: true?
// als segment van ook afwijst: false?

        Vector3D eye = new Vector3D(origin.x, origin.y, dis);
        // work with normal support here
        Vector3D support = new Vector3D(f.trPoints[0]);
        eye = Vector3D.minus(eye, support);
        Vector3D.makeUnitary(eye);
        return Vector3D.dotProduct(eye, f.unitNormal) >= -Vector3D.NZero;
    }
    
    public void recalcZValues(int[] recalcFacets, double dis,
        Vector3D origin, double zMin, double zMax)
    {   
        int recalcNum = recalcFacets.length;
//System.out.println("recalcNum = " + recalcNum);

        if (recalcNum == 1)
        {   facets[recalcFacets[0]].zValue = zMax;
            return;
        }    
        // create new incidence matrix        
        IncidenceMatrix im = new IncidenceMatrix(recalcNum);
        // calculate Aad's relation
        for (int row = 0; row < recalcNum; row++)
            for (int col = 0; col < recalcNum; col++)
            {   if (row != col)
                {   im.matrix[row][col] =
                        isOnTop(recalcFacets[row], recalcFacets[col], dis, origin);
                }    
            }
            
//System.out.println("im \n" + im.toString());      
        // any 2 facets with A > B and B < A or A < B and B > A
        // are separated, so put them to 0
        // this results in a symmetric matrix??
        im.relax();
//System.out.println("relaxed im \n" + im.toString());              
        
        // use additional separation info
        // assume im is symmetric
        for (int row = 0; row < recalcNum; row++)
            for (int col = row + 1; col < recalcNum; col++)
            {   // find non-zero elements
                if (im.matrix[row][col] != 0)
                {   // check for separation
                    if (projectionsSeparated(recalcFacets[row], recalcFacets[col], dis, origin))
                    {
//System.out.println("" + recalcFacets[row] + " is sep from " + recalcFacets[col]);                        
                        im.matrix[row][col] = 0;
                        im.matrix[col][row] = 0;

                    }    
//                    else
//System.out.println("" + recalcFacets[row] + " is not sep from " + recalcFacets[col]);                                            
                }    
            }
//System.out.println("separated im \n" + im.toString());                
  
  
/*        
System.out.println("initial order front/back");
String s = "";
for (int i = recalcNum - 1; i >=0 ; i--)
{   s += " " + recalcFacets[i];
}
System.out.println(s);
*/


        int[] rowNumsIn = new int[recalcNum];
        for (int i = 0; i < recalcNum; i++)
            rowNumsIn[i] = i;
            
        int[] newOrder = findOrder(im, rowNumsIn);    

        if (newOrder != null)
        {
/*            
System.out.println("ordered row numbers back/front");
String nos = "";
for (int i = 0; i < recalcNum ; i++)
    nos += " " + newOrder[i];
System.out.println(nos);
*/
/*
System.out.println("new order front/back");
String nos2 = "";
for (int i = recalcNum - 1; i >=0 ; i--)
    nos2 += " " + recalcFacets[newOrder[i]];
System.out.println(nos2);
*/
            // hier pas de gecorrigeerde zValues uitdelen
            double zStep = (zMax - zMin) / (recalcNum - 1); // checked for 0!
            double currentZ = zMax;
            for (int i = recalcNum - 1; i >=0 ; i--)
            {   facets[recalcFacets[newOrder[i]]].zValue = currentZ;
                currentZ -= zStep;
            }


        } // newOrder != null
//        else // dit komt voor als geen enkele order consistent is!
//            System.out.println("no consistent final order");
        


    }
    
    
    
// recursief
// we are looking for an order on the row numbers of the
// matrix, later goedmaken
// rowNumbers bevat de indices van de rijen die nog mee doen

    public int[] findOrder(IncidenceMatrix im, int[] rowNumbers)
    {   // kijk hoeveel rijen/kolommen nog in het spel
        int numRows = rowNumbers.length;
        // laatste rij: klaar
        if (numRows == 1)
            return rowNumbers;
        // find top rows
        im.findMaximalElements(rowNumbers, 0);

/*        
if (im.maxCnt > 0)
System.out.println("" + im.maxCnt + " top rows at rows = " + numRows); 
*/

        // if no top rows, take max rows
// dit komt nier meer voor?        
        if (im.maxCnt == 0)
        {    im.findMaximalElements(rowNumbers, 1);
//System.out.println("" + im.maxCnt + " max rows at rows = " + numRows);         

        }
        
        // make hard copies!
        int maxCnt = im.maxCnt;
        int[] maxRows = new int[maxCnt];
        System.arraycopy(im.maxRows, 0, maxRows, 0, maxCnt);
        
        // maxRows contains a list of positions in rowNumbers
        // where top or maximal elements are found
        // the INDEX of the j-th top/maximal row 
        // is rowNumbers[maxRows[j]]
        
//System.out.println("maxCnt = " + maxCnt + " at" + numRows + " rows");                    

        // not possible anymore
        if (maxCnt == 0)
        {
//System.out.println("stuck at " + numRows + " rows");            
            return null;
        }    
        for (int j = 0; j < maxCnt; j++)
        {   
            
            int[] tRowNumbers = new int[numRows];
            System.arraycopy(rowNumbers, 0, tRowNumbers, 0, numRows);
            // the row with index in the last position is not
            // a top or maximal row
            // so put the top/maximal row as last by swapping
            if (maxRows[j] != (numRows - 1))
            {   int temp = tRowNumbers[maxRows[j]];
                tRowNumbers[maxRows[j]] = tRowNumbers[numRows - 1];
                tRowNumbers[numRows - 1] = temp;
//System.out.println("swapped at " + numRows + " rows");                            
            }    
//            else
//System.out.println("trying last at " + numRows + " rows");                                        

            // now copy all but last of tRowNumbers
            // and find an order on these
            int[] newRowNumbers = new int[numRows - 1];
            System.arraycopy(tRowNumbers, 0, newRowNumbers, 0, numRows - 1);
            int[] result = findOrder(im, newRowNumbers);
            if (result != null)
            {   // if an order was found, copy it into tRowNumbers
                System.arraycopy(result, 0, tRowNumbers, 0, numRows - 1);
                // check for a consistent order here als dit de laatste recursiestap was
                // of meteen maar overal?
                if (numRows == im.size)
                {   
                    boolean consistent = isConsistent(im, tRowNumbers);
//System.out.println("consistent = " + consistent);
                    if (!consistent)
                    {   // continue
//System.out.println("im \n" + im.toString());     
//System.out.println("consistent = " + consistent);
                    }
                    else
                        return tRowNumbers;
                }                
                else
                    return tRowNumbers;
            
            }
        }    
        return null;
    }
    
    // dit is natuurlijk NOOIT goed, tenzij je im
    // verandert, is gebeurd
    private boolean isConsistent(IncidenceMatrix im, int[] newOrder)
    {   boolean result = true;
        int size = newOrder.length;
        for (int cnt1 = 0; cnt1 < size; cnt1++)
            for (int cnt2 = cnt1 + 1; cnt2 < size; cnt2++)
                result = result && 
                    (im.matrix[newOrder[cnt1]][newOrder[cnt2]] <= 0);
        return result;
    }

    private int isOnTop(int index, int cnt, double dis, Vector3D origin)
    {   int result = 0;
        // get the facets
        Facet3D fA = facets[index];
        Facet3D fB = facets[cnt];
        // twee vlakjes
        if ((fA.numPoints > 2) && (fB.numPoints > 2))
        {   
            
//System.out.println("" + index + " is separated from " + cnt + " = " +
//                   projectionsSeparated(fA, fB, dis, origin)); 
            
            // Aad's A na B (i.e. f1 on top of f2)
            Vector3D pointO = new Vector3D(origin.x, origin.y, dis);
            fA.calculateBarycenter();
            Vector3D pointP = fA.barycenter;
            Line3D lineOP = new Line3D(pointO, pointP);
            Plane3D planeB = new Plane3D(
                fB.unitNormal.x, fB.unitNormal.y, fB.unitNormal.z,
                Vector3D.dotProduct(fB.unitNormal, fB.trPoints[0]));
            int isType = Plane3D.intersectionType(lineOP, planeB);    
            // line through eye and barycenter of fA 
            // is parallel to the plane through fB
            if (isType == 0)
            {   // try this
                if (fA.zValue > fB.zValue)
                    result = 1;
                else
                    result = -1;
            }
            // line through eye O and barycenter P of fA 
            // intersects the plane through fB in one point Q
            // note that P = Q is impossible since 
            // P is inwendig en alles versneden
            else if (isType == 1)
            {   
//System.out.println("isType = 1");                
                Vector3D pointQ = Plane3D.getIntersectionPoint(lineOP, planeB);
                Line3D lineOQ = new Line3D(pointO, pointQ);
                if (lineOQ.segmentContains(pointP))
                    result = 1;
                else    
                    result = -1;
                
//System.out.println("O = " + pointO.toString());
//System.out.println("P = " + pointP.toString());
//System.out.println("Q = " + pointQ.toString());
/*
if (result)
System.out.println("" + index + " is on top of " + cnt);                                    
else
System.out.println("" + index + " is not on top of " + cnt);                                    
*/
                
            }    
            // line through eye and barycenter of fA 
            // is in the the plane through fB
            // but then eye is in the plane through fB
            // thus we are looking at fB from the side
            // AND (alles versneden) fB is geheel achter
            // fA of geheel ervoor
            else if (isType == 2)
            {   if (fA.zValue > fB.zValue)
                    result = 1;
                else
                    result = -1;
            }    
            
        }
        else if ((fA.numPoints == 2) && (fB.numPoints > 2))
        {   
/*            
if (fA.numPoints == 2)
{
    
System.out.println("comparing segment with plane");    
System.out.println("index " + index + " point0 = " + fA.points[0].toString());
System.out.println("index " + index + " point1 = " + fA.points[1].toString());
}
*/
            // Aad's A na B (i.e. f1 on top of f2)
            Vector3D pointO = new Vector3D(origin.x, origin.y, dis);
            fA.calculateBarycenter();
            Vector3D pointP = fA.barycenter;
            Line3D lineOP = new Line3D(pointO, pointP);
            Plane3D planeB = new Plane3D(
                fB.unitNormal.x, fB.unitNormal.y, fB.unitNormal.z,
                Vector3D.dotProduct(fB.unitNormal, fB.trPoints[0]));
            int isType = Plane3D.intersectionType(lineOP, planeB);    
            // line through eye and barycenter of fA 
            // is parallel to the plane through fB
            if (isType == 0)
            {   // try this
                if (fA.zValue > fB.zValue)
                    result = 1;
                else
                    result = -1;
            }
            // line through eye and barycenter of fA 
            // intersects the plane through fB in one point
            else if (isType == 1)
            {   
//System.out.println("isType = 1");                
                Vector3D pointQ = Plane3D.getIntersectionPoint(lineOP, planeB);
                Line3D lineOQ = new Line3D(pointO, pointQ);
                if (lineOQ.segmentContains(pointP))
                    result = 1; 
                else
                    result = -1;
                
//System.out.println("O = " + pointO.toString());
//System.out.println("P = " + pointP.toString());
//System.out.println("Q = " + pointQ.toString());
/*
if (result)
System.out.println("" + index + " is on top of " + cnt);                                    
else
System.out.println("" + index + " is not on top of " + cnt);                                    
*/
                
            }    
            // line through eye and barycenter of fA 
            // is in the the plane through fB
            // but then eye is in the plane through fB
            // thus we are looking at fB from the side
            // AND (alles versneden) fB is geheel achter
            // fA of geheel ervoor
            else if (isType == 2)
            {   if (fA.zValue > fB.zValue)
                    result = 1;
                else
                    result = -1;
            }    
            
        }
        
        else if ((fA.numPoints > 2) && (fB.numPoints == 2))
        {   
//System.out.println("plane and segment interchanged");            
            result = - isOnTop(cnt, index, dis, origin);
        }
        else if ((fA.numPoints == 2) && (fB.numPoints == 2))
        {   
            
//System.out.println("comparing segments");

            Vector3D pointO = new Vector3D(origin.x, origin.y, dis);
            // dit kan eigenlijk niet
            if (fA.trPoints[0].equals(fA.trPoints[1]))
            {   if (fA.zValue > fB.zValue)
                    result = 1;
                else
                    result = -1;
//System.out.println("fA a point");                
                return result;
            }
            Line3D tLine = new Line3D(fA.trPoints[0], fA.trPoints[1]);
            // we are seeing only a point of fA
            if (tLine.contains(pointO))
            {    
//System.out.println("O on fA");                                
                
                if (fA.zValue > fB.zValue)
                    result = 1;
                else
                    result = -1;
//System.out.println("fA a point");                
                return result;
            }
            // dit kan eigenlijk niet
            if (fB.trPoints[0].equals(fB.trPoints[1]))
            {
//System.out.println("fB a point");                                
                if (fA.zValue > fB.zValue)
                    result = 1;
                else
                    result = -1;
//System.out.println("fA a point");                
                return result;
            }    
            Line3D lineB = new Line3D(fB.trPoints[0], fB.trPoints[1]);    
            Plane3D planeOA = new Plane3D(pointO, fA.trPoints[0], fA.trPoints[1]);
            int isType = Plane3D.intersectionType(lineB, planeOA);                
            // lineB parallel to planeOA
            // sowieso separated            
            if (isType == 0)
            {   
//System.out.println("isType = 0");                                
                // try this
                // zValue voldoende want de lijnstukken zijn versneden?
                if (fA.zValue > fB.zValue)
                    result = 1;
                else
                    result = -1;
//System.out.println("fA a point");                
                return result;
            }
            // lineB cuts planeOA in a point
            else if (isType == 1)
            {   
//System.out.println("isType = 1");                
                Vector3D pointQ = Plane3D.getIntersectionPoint(lineB, planeOA);       
                boolean triangleOUVcontainsQ = 
                    Plane3D.triangleContainsPoint(pointO, 
                        fA.trPoints[0], fA.trPoints[1], pointQ);
                boolean pointQinsidefB = Line3D.segmentContainsPoint(
                    fB.trPoints[0], fB.trPoints[1], pointQ);
// dit gebeurt bijna nooit!                    
                //return !(triangleOUVcontainsQ && pointQinsidefB);    
                if (!triangleOUVcontainsQ)
                    result = 1;
                else    
                    result = -1;
/*                
                if (triangleOUVcontainsQ)
                    return false;
                else 
                    return (fA.zValue > fB.zValue);
*/                    
                
            }
            // lineB is in planeOA
            else if (isType == 2)
            {   
//System.out.println("isType = 2");                                                                
                // randgeval: fA en fB op een lijn
                if (lineB.contains(fA.trPoints[0]) &&
                    lineB.contains(fA.trPoints[1]))
                    return 0;    
                else  // anders dit, want fA kan fB niet snijden
                {   
                    
                    if (fA.zValue > fB.zValue)
                       result = 1;
                    else
                        result = -1;
//System.out.println("fA a point");                
                    return result;
                    
                }    
            }    
            // mijn eenvoudige oplossing
            //result = (fA.zValue > fB.zValue);
        }
        
        return result;
    }    
    
    public boolean projectionsSeparated(int indexA, int indexB,
                                        double dis, Vector3D o)
    {   Facet3D fA = facets[indexA];
        Facet3D fB = facets[indexB];
        Polygon2D pA = fA.project2D(dis, o);
        Polygon2D pB = fB.project2D(dis, o);
        return pA.isSeparatedFrom(pB, true);            

    }
    // transform all vertices to view space using the matrix mat
    // leave world space vertices unchanged
    // redefine for subclass ObjectGroup3D        
    public void transformBy(Matrix3D m, double dis, boolean zSort)
    {   // taking m == null only sorts
        if (m != null)
        {   Vector3D temp;
            for (int i = 0; i < numVertices; i++)
            {   Object3D topParent = topParent();
                // center the object(group) here!!
                trVertices[i] = m.transform(
                    Vector3D.minus(vertices[i], topParent.center));
            }
            for (int i = 0; i < numFacets; i++)
            {   // notify facet[i] to reference the correct transformed 3-points
                facets[i].updateTrPoints(trVertices);
                facets[i].calculateZValue(m.origin, dis);
                // unit normal stays in  (0, 0, 0)
                facets[i].unitNormal = m.nTransform(facets[i].normal);
                Vector3D.makeUnitary(facets[i].unitNormal);
            }
        } // if (m != null)
        if (zSort)
            zMergeSort();
    }
    
// nodig?    
/*
    // transform the object in world space (irreversible)
    public void oTransform()
    {   for (int i = 0; i < numVertices; i++)
            vertices[i] = oMat.oTransform(vertices[i]);
        for (int i = 0; i < numFacets; i++)
        {   // notify facet[i] to reference the correct o-transformed 3-points
            facets[i].updatePoints(vertices);
            facets[i].normal = oMat.onTransform(facets[i].normal);
        }
    }
*/    

    // merge sort ALL facets by zValue, speed O(n log_2 n)    
    public void zMergeSort()
    {   facets = mergeSort(facets);
    }    
    public Facet3D[] mergeSort(Facet3D[] list)
    {   if (list.length <= 1)
            return list;
        int half = list.length / 2;
        Facet3D[] list1 = new Facet3D[half];
        Facet3D[] list2 = new Facet3D[list.length - half];
        // fast copy            
        System.arraycopy(list, 0, list1, 0, half);    
        // fast copy
        System.arraycopy(list, half, list2, 0, list.length - half);                
        list1 = mergeSort(list1);
        list2 = mergeSort(list2);
        return merge(list1, list2);
    }    
    public Facet3D[] merge(Facet3D[] list1, Facet3D[] list2)
    {   Facet3D[] list = new Facet3D[list1.length + list2.length];
        int index1 = 0;
        int index2 = 0;
        int index = 0;
        while ((index1 < list1.length) || (index2 < list2.length))
        {   // check if list1 is empty
            if ((index1 == list1.length) && (index2 < list2.length))
            {   // fast copy                
                System.arraycopy(list2, index2, 
                                 list, index, 
                                 list2.length - index2);
                return list;
            }
            // check if list2 is empty
            else if ((index1 < list1.length) && (index2 == list2.length))
            {   // fast copy                
                System.arraycopy(list1, index1, 
                                 list, index, 
                                 list1.length - index1);                
                return list;            
            }
            else // both list not empty
            {   if (list1[index1].zValue < list2[index2].zValue)
                {   list[index] = list1[index1];
                    index++;
                    index1++;
                }
                else
                {   list[index] = list2[index2];
                    index++;
                    index2++;
                }    
            }    
        } // while
        return list;
    }  
    // end merge sort methods
    
    // set facets[i] visible
    // works also for groups
    public void setVisible(int i, boolean visible)
    {   facets[i].visible = visible;
    }
    
    // set this object visible
    // redefine to set object flaggs correctly
    public void setVisible(boolean vis)
    {   visible = vis;
        for (int i = 0; i < numFacets; i++)
            setVisible(i, vis);
    }
    // check if SOME facets of the object are visible
    // the object itself is visible in this case
    // but one or more or all of the facets have been hidden
    // to speed up drawing
    public boolean someFacetsVisible()
    {   boolean result = false;
        for (int i = 0; i < numFacets; i++)
        {   if (facets[i].visible)
                return true;
        }
        return result;
    }    
    // set facets[i] empty.
    // if empty = false restore old object(!) state
    public void setEmpty(int i, boolean empty)
    {   if ((i >= 0) && (i < numFacets))
        {   if (empty)
            {   facets[i].empty = true;
                facets[i].filled = false;
                facets[i].outlined = false;
            }
            else // reset empty
            {   facets[i].empty = false;
                // if the whole object is outlined
                // outline the facet
                if (outlined)
                     facets[i].outlined = true;
                // if the whole object is filled
                // outline the facet
                if (filled)
                     facets[i].filled = true;
            }
        }
    }
    // set this object outlined, skip empty and 2-dim facets
    // avoid disappearance
    // REDEFINE
    public void setOutlined(boolean outline)
    {   if (outline)
        {   outlined = true;
            for (int i = 0; i < numFacets; i++)
            {   if (!facets[i].empty)
                    facets[i].outlined = true;
            }
        }
        else // remove outline only when filled
        {   if (filled)
            {   outlined = false;
                for (int i = 0; i < numFacets; i++)
                {   
                    // 2-dim facets are never filled, correct here
                    if (facets[i].numPoints > 2)
                        facets[i].outlined = false;
                }
            }
        }
    }
    // fill the facets of this object
    // avoid disappearance
    // MUST be redefined to set correctly set the object(group) flaggs
    public void setFilled(boolean fill)
    {   if (fill)
        {   filled = true;
            for (int i = 0; i < numFacets; i++)
            {   if (!facets[i].empty && (facets[i].numPoints > 2))
                    facets[i].filled = true;
            }
        }
        else // remove filling only when outlined
        {   if (outlined)
            {   filled = false;
                for (int i = 0; i < numFacets; i++)
                {   facets[i].filled = false;
                }
            }
        }
    }
    
    // reset all empty facets
    // works also for groups
    public void reSetEmpty(boolean o)
    {   for (int i = 0; i < numFacets; i++)
        {   setEmpty(i, false);
        }
    }
    // check if this Object3D contains f
    // redefine for subclass ObjectGroup3D
    // necesary for ObjectClicked
    public Object3D objectContains(Facet3D f)
    {   Object3D result = null;
        if (containsFacet(f) >= 0)
        {   result = this;
            return result;
        }
        return result;
    }
    
    // method for user interaction through the mouse
    
    // works also for groups
    // set the clicked facet to empty
    public void setEmpty(int x, int y, double dis, Vector3D origin, int paintType)
    {   int index = facetClicked(x, y, dis, origin, paintType);
        setEmpty(index, true);
    }
    
    // no need to redefine for subclass ObjectGroup3D
    // since objectContains was redefined
    // return type always an Object!!
    public Object3D objectClicked(int x, int y, double dis, Vector3D origin, 
                                  int paintType)
    {   Object3D result = null;
        int index = facetClicked(x, y, dis, origin, paintType);
        if (index >= 0)        
            return objectContains(facets[index]);
        return result;    
    }

// alleen gebruiken voor het TOP Object
// wil je de zaak beperken doe dit dan door te checken of 
// je keuze ook behoort tot een deelobject

    // faster then tracing (x,y) in view plane
    // back to a facet in word space!!
    // also works for a group
    // but use only on top parent facet array since the facet found
    // might be covered by other facets not in the subgroup array
    // find index of clicked facet
    // use objectClicked to find the object
    public int facetClicked(int x, int y, double dis, Vector3D origin, 
                            int paintType)
    {   Polygon p;
        int index = -1;
        // start on top, facets are already sorted, always by zValue(!)
// ook hier, netter        
        if (paintType == Object3DContainer.NONZMIN)
        {   for (int i = numFacets - 1; i >= 0; i--)
            {   // only visible facets that were drawn(!) can be clicked
                if (facets[i].visible && 
                    visFromD(facets[i], dis, origin)                    
                    )
                {   p = facets[i].project(dis, origin);
                    if (p.contains(x, y))
                        return i;
                }    
            }
        }
// zorg er hier voor dat de facet array gesorteerd is naar
// de volgorde waarin je ook TEKENT!
        else // all facets were drawn
        {   for (int i = numFacets - 1; i >= 0; i--)
            {   // only visible facets can be clicked
                if (facets[i].visible)
                {   p = facets[i].project(dis, origin);
                    if (p.contains(x, y))
                        return i;
                }    
            }
        }
        return index;
    }
    // find reference to clicked facet  
    // see remarks above
    public Facet3D clickedFacet(int x, int y, double dis, Vector3D origin, 
                                int paintType)    
    {   Facet3D result = null;
        int index = facetClicked(x, y, dis, origin, paintType);
        if (index >= 0)
            result = facets[index];
        return result;
    }
/*    
    // no need to redefine since we are using the facet array
    // can be called for any object subgroup when the topmost 
    // objectgroup has sortSubArray = true
    // but see remarks at facetClicked!!
    // return actual point in world space!
    public Vector3D vertexClicked(int x, int y, double dis, 
                                  //Vector3D origin, boolean includeInvisibles)
                                  Vector3D origin)
    {   Vector3D result = null;
        Polygon p;
        // point clicked in drawing plane        
        Point pClicked = new Point(x,y);        
        // start on top, facets are already sorted, always by zValue
// zorg er hier voor dat de facet array gesorteerd is naar
// de volgorde waarin je ook TEKENT!
        for (int i = numFacets - 1; i >= 0; i--)
        {   // includeInvisibles determines which vertices can be clicked
            if (facets[i].visible) // || includeInvisibles)
            {   p = facets[i].project(dis, origin);
                for (int cnt = 0; cnt < facets[i].numPoints; cnt++)
                {   // projected vertex
                    Point projVertex = 
                        new Point(p.xpoints[cnt], p.ypoints[cnt]);
                    if (distance(pClicked, projVertex) <= 
                        Object3DContainer.SSTT)
                    {    result = new Vector3D(facets[i].points[cnt]);                            
                         // this is the topmost vertex which was clicked on
                         // if the object is filled, check if this
                         // vertex is covered, if it is, exit since
                         // nothing more can be found
                         // asuming the object is convex!
                        if (filled)
                        {   if (vertexCovered(dis, origin, i, projVertex))
                            {    
                                return null;
                            }
                            else // not covered
                            {    
                                return result;
                            }
                        }
                        else // not filled
                            return result;
                    } // close enough
                    // else keep looking
                } // edge loop
                // else keep looking
            } // facet loop
            // else keep looking
        }
        return result;
    }
*/    
    
// precieze versie voor vertices heeft geen zin:
// gebruik de geprojecteerde vertices in the integer plane
        public FacetWithVertex facetWithVertexClicked(int x, int y, double dis, 
            //Vector3D origin, boolean includeInvisibles)
            Vector3D origin)
    {   FacetWithVertex result = null;
        Polygon p;
        // point clicked in drawing plane        
        //Point pClicked = new Point(x,y);
        int pClickedX = x;
        int pClickedY = y;
        // start on top, facets are already sorted, always by zValue
// zorg er hier voor dat de facet array gesorteerd is naar
// de volgorde waarin je ook TEKENT!
        for (int i = numFacets - 1; i >= 0; i--)
        {   // includeInvisibles determines which vertices can be clicked
            if (facets[i].visible) // || includeInvisibles)
            {   p = facets[i].project(dis, origin);
                for (int cnt = 0; cnt < facets[i].numPoints; cnt++)
                {   // projected vertex
                    //Point projVertex = new Point(p.puntenX[cnt], p.puntenY[cnt]);
                    int projVertexX = p.puntenX[cnt];
                    int projVertexY = p.puntenY[cnt];
// is dit wel goed??                    
                    //if (distance(pClickedX, pClickedY, projVertexX, projVertexY) <= Object3DContainer.SSTT)
                    if (distance(pClickedX, pClickedY, projVertexX, projVertexY) <= DrawConstants.SSTT)
                    {    result = new FacetWithVertex(
                            facets[i].points[cnt],
                            facets[i]);                            
                         // this is the topmost vertex which was clicked on
                         // if the object is filled, check if this
                         // vertex is covered, if it is, exit since
                         // nothing more can be found
                         // asuming the object is convex!
                        if (filled)
                        {   if (vertexCovered(dis, origin, i, projVertexX, projVertexY))
                            {    
                                return null;
                            }
                            else // not covered
                            {    
                                return result;
                            }
                        }
                        else // not filled
                            return result;
                    } // close enough
                    // else keep looking
                } // edge loop
                // else keep looking
            } // facet loop
            // else keep looking
        }
        return result;
    }

    
    // distance between two integer points
    public double distance(int p1x, int p1y, int p2x, int p2y)
    {   return Math.sqrt((p1x - p2x) * (p1x - p2x) +
                         (p1y - p2y) * (p1y - p2y));   
    }
        
    // given a vertex belonging to the facet with index i,
    // check if a projected facet covers its projection
    // (assuming the facets are filled)
    // this is a facet with index > i (!)
    //public boolean vertexCovered(double dis, Vector3D origin,int fIndex, Point projVertex)
    public boolean vertexCovered(double dis, Vector3D origin,int fIndex, int projVertexX, int projVertexY)
    {   boolean result = false;
        Polygon p;
        // start on top, facets are already sorted, always by zValue
        for (int i = numFacets - 1; i > fIndex; i--)
        {   // only visible facets can cover
            if (facets[i].visible)
            {   p = facets[i].project(dis, origin);
                if (p.contains(projVertexX, projVertexY))
                    return true;
            }
        }    
        return result;    
    }    

// versie 1, aanwijzen van edges via aangeklikt punt erop

    // no need to redefine since we are using the facet array
    // can be called for any object subgroup when the topmost 
    // objectgroup has sortSubArray = true
    // return edge from, edge to and point as actual points!
    public Vector3D[] edgeClicked(int x, int y, double dis, 
                                  //Vector3D origin, boolean includeInvisibles)
                                  Vector3D origin)
    {   Vector3D[] edgeWithPoint = null;
        Polygon p;
        // point clicked in drawing plane
        //Point pClicked = new Point(x,y);
        int pClickedX = x;
        int pClickedY = y;
        // start on top, facets are already sorted, always by zValue
// zorg er hier voor dat de facet array gesorteerd is naar
// de volgorde waarin je ook TEKENT!
        for (int i = numFacets - 1; i >= 0; i--)
        {   // includeInvisibles determines which vertices can be clicked
            if (facets[i].visible)// || includeInvisibles)
            {   p = facets[i].project(dis, origin);
                for (int cnt = 0; cnt < facets[i].numPoints; cnt++)
                {   // find projected edge
                    //Point projVertexFrom = new Point(p.puntenX[cnt], p.puntenY[cnt]);
                    //Point projVertexTo = new Point(p.puntenX[(cnt + 1) % facets[i].numPoints], 
                    //              				   p.puntenY[(cnt + 1) % facets[i].numPoints]);
                    int projVertexFromX = p.puntenX[cnt];
                    int projVertexFromY = p.puntenY[cnt];
                    int projVertexToX = p.puntenX[(cnt + 1) % facets[i].numPoints];
                    int projVertexToY = p.puntenY[(cnt + 1) % facets[i].numPoints];
                    
                    // if projected segment is a point skip this
                    double lambda;
                    //if (projVertexFrom.equals(projVertexTo))
                    if ((projVertexFromX == projVertexToX) && (projVertexFromY == projVertexToY))
                    {   lambda = 2;
                    }    
                    else
                    {
                        // find the projection of the clicked point
                        // on the projected edge(line), i.e. a point of the form
                        // (1-lambda)*projVertexFrom+lambda*projVertexTo
                        lambda = projectionOnSegment(projVertexFromX, projVertexFromY, projVertexToX,
                                                     projVertexToY, pClickedX, pClickedY);
                    }
                    // if projection is ON the segment                                    
                    if ((lambda > 0) && (lambda < 1))
                    {   // real(!) x-coordinate of projection
                        double projIntersectX = (1 - lambda) * projVertexFromX + lambda * projVertexToX;
                        // real(!) y-coordinate of projection    
                        double projIntersectY = (1 - lambda) * projVertexFromY + lambda * projVertexToY;
                        // distance clicked - projection (in plane!)   
                        double distance = Math.sqrt(    
                            (pClickedX - projIntersectX) * 
                            (pClickedX - projIntersectX) + 
                            (pClickedY - projIntersectY) * 
                            (pClickedY - projIntersectY));
                        // check if clicked point is close enough    
                        //if (distance <= Object3DContainer.SSTT)
                        if (distance <= DrawConstants.SSTT)
                        {   
                            edgeWithPoint = new Vector3D[3];
                            edgeWithPoint[0] = new Vector3D(facets[i].points[cnt]);
                            edgeWithPoint[1] = new Vector3D(
                                facets[i].points[(cnt + 1) % facets[i].numPoints]);
                            // the exact point we are looking for is obtained as follows
                            // everything in view space
                            // 1)
                            // find the real(!) projection of the edge in the
                            // plane z = 0, thus in view space!
                            // start
                            double temp =  dis / (dis - facets[i].trPoints[cnt].z);
                            double startX = (origin.x + (facets[i].trPoints[cnt].x - origin.x) * temp);
                            double startY = (origin.y + (facets[i].trPoints[cnt].y - origin.y) * temp);
                            Vector3D start = new Vector3D(startX, startY, 0);
                            temp =  dis / (dis - facets[i].trPoints[(cnt + 1) % facets[i].numPoints].z);
                            double endX = (origin.x + (facets[i].trPoints[(cnt + 1) % facets[i].numPoints].x - origin.x) * temp);
                            double endY = (origin.y + (facets[i].trPoints[(cnt + 1) % facets[i].numPoints].y - origin.y) * temp);
                            Vector3D end = new Vector3D(endX, endY, 0);
                            // 2)
                            // create the real version of the clicked point
                            // (1-lambda)*"start"+lambda*"end"                            
                            Vector3D.scaleBy(start, 1 - lambda);
                            Vector3D.scaleBy(end, lambda);
                            Vector3D realClicked = Vector3D.plus(start, end);
                            // 3)
                            // now the line through (origin.x, origin,y, dis) and
                            // realClicked should intersect the transformed edge
                            Line3D line1 = new Line3D(
                                new Vector3D(origin.x, origin.y, dis), realClicked);
                            Line3D line2 = new Line3D(facets[i].trPoints[cnt],
                                facets[i].trPoints[(cnt + 1) % facets[i].numPoints]);
                            int ist = Line3D.intersectionType(line1, line2);
//System.out.println("ist = " + ist);                            
                            Vector3D isPoint = Line3D.getIntersectionPoint(line1, line2);
                            double lambda1 = 
                                Vector3D.distance(isPoint, 
                                    facets[i].trPoints[(cnt + 1) % facets[i].numPoints]) /
                                Vector3D.distance(facets[i].trPoints[cnt], 
                                    facets[i].trPoints[(cnt + 1) % facets[i].numPoints]);
                            // in view space y-axis is reversed!        
                            lambda1 = 1 - lambda1;                                    
                            
//System.out.println("l = " + lambda);                                    
//System.out.println("l-1 = " + lambda1);                                                                
                            
                            // 4)
                            // now find the untransformed version of this point
                            
                            
                            // (1-lambda)*"from"+lambda*"to"
                            Vector3D from = new Vector3D(edgeWithPoint[0]); // new!!
                            Vector3D.scaleBy(from, 1 - lambda1);
                            Vector3D to = new Vector3D(edgeWithPoint[1]); // new!! 
                            Vector3D.scaleBy(to, lambda1);
                            edgeWithPoint[2] = Vector3D.plus(from, to);
                            
                            
                            // find the integer(!) projection of the new point in
                            // the plane
                            //Point projVertex = new Point((int) Math.round(projIntersectX),(int) Math.round(projIntersectY));
                            int projVertexX = (int) Math.round(projIntersectX);
                            int projVertexY = (int) Math.round(projIntersectY);
                            // if the new point is covered reject it
                            // see VertexClicked
                            if (filled)
                            {   if (vertexCovered(dis, origin, i, projVertexX, projVertexY))
                                {    
                                    return null;
                                }
                                else // not covered
                                {    
                                    return edgeWithPoint;
                                }
                            }
                            else // object is not filled
                                return edgeWithPoint;
                        } // close enough
                        // else keep looking
                    } // projection the on segment
                    // else keep looking
                } // edge loop   
            } // facet loop    
        }
        return edgeWithPoint;
    }

// versie 2: aanwijzen van een punt op en edge met mogelijkheid tot tick marks
// en in de return ook het Factet3D om te kijken of het punt uberhaupt aangeklikt mag
// worden!
    public FacetWithEdgePoint facetWithEdgePointClicked(
            int x, int y, double dis, 
            //Vector3D origin, boolean includeInvisibles)
            Vector3D origin)
    {   FacetWithEdgePoint result = null;
        Polygon p;
        Polygon2D p2;
        // point clicked in drawing plane
//        Point pClicked = new Point(x,y);
        int pClickedX = x;
        int pClickedY = y;
        Vector2D p2Clicked = new Vector2D((double) x, (double) y);
        // start on top, facets are already sorted, always by zValue
// zorg er hier voor dat de facet array gesorteerd is naar
// de volgorde waarin je ook TEKENT!
        for (int i = numFacets - 1; i >= 0; i--)
        {   // includeInvisibles determines which vertices can be clicked
            if (facets[i].visible)// || includeInvisibles)
            {   // get the projected polygon
                p = facets[i].project(dis, origin);
                p2 = facets[i].project2D(dis, origin);
                for (int cnt = 0; cnt < facets[i].numPoints; cnt++)
                {   
// moet dit nog preciezer? je zou de reeele versie van de geprojecteerde edge 
// kunnen gebruiken
                    // find projected edge in integer plane
                    //Point projVertexFrom = new Point(p.puntenX[cnt], p.puntenY[cnt]);
                    //Point projVertexTo = new Point(p.puntenX[(cnt + 1) % facets[i].numPoints], 
                    //             				   p.puntenY[(cnt + 1) % facets[i].numPoints]);
                    int projVertexFromX = p.puntenX[cnt];
                    int projVertexFromY = p.puntenY[cnt];
                    int projVertexToX = p.puntenX[(cnt + 1) % facets[i].numPoints];
                    int projVertexToY = p.puntenY[(cnt + 1) % facets[i].numPoints];

                    // find projected edge in real plane
                    Vector2D projVertexFrom2 = new Vector2D(p2.xpoints[cnt], p2.ypoints[cnt]);
                    Vector2D projVertexTo2 = new Vector2D(p2.xpoints[(cnt + 1) % facets[i].numPoints], 
                                     p2.ypoints[(cnt + 1) % facets[i].numPoints]);
                    // if projected segment is a point skip this
                    double lambda;
                    //if (projVertexFrom.equals(projVertexTo))
                    if ((projVertexFromX == projVertexToX) && (projVertexFromY == projVertexToY))
                    {   lambda = 2;
                    }    
                    else
                    {
                        // find the projection of the clicked point
                        // on the projected edge(line), i.e. a point of the form
                        // (1-lambda)*projVertexFrom+lambda*projVertexTo
                        lambda = projectionOnSegment(projVertexFrom2, projVertexTo2,
                                                     p2Clicked);
                    }
                    // if projection is ON the segment as INNER point                                    
                    if ((lambda > 0) && (lambda < 1))
                    {   // real(!) x-coordinate of projection
                        double projIntersectX = (1 - lambda) * projVertexFrom2.x + lambda * projVertexTo2.x;
                        // real(!) y-coordinate of projection    
                        double projIntersectY = (1 - lambda) * projVertexFrom2.y + lambda * projVertexTo2.y;
                        // distance clicked point - projection of ibidem (in real plane!)   
                        double distance = Math.sqrt(    
                            (p2Clicked.x - projIntersectX) * 
                            (p2Clicked.x - projIntersectX) + 
                            (p2Clicked.y - projIntersectY) * 
                            (p2Clicked.y - projIntersectY));
                        // check if clicked point is close enough to projected edge    
                        //if (distance <= Object3DContainer.SSTT)
                        if (distance <= DrawConstants.SSTT)
                        {   
                            // create first part of the return object
                            Vector3D[] edgeWithPoint = new Vector3D[3];
                            edgeWithPoint[0] = new Vector3D(facets[i].points[cnt]);
                            edgeWithPoint[1] = new Vector3D(
                                facets[i].points[(cnt + 1) % facets[i].numPoints]);

                            double lambda1 = 0;
                            boolean tickFound = false;
                            // we are looking at edge cnt of facets[i]
                            // hier: check for tickmarks
                            // als de edge tickmarks heeft
                            if (facets[i].numTicks[cnt] > 0)
                            {   // find array of ticks in view space
                                Vector3D[] viewSpaceTicks = facets[i].findTransformedTicks(cnt);
                                double[] xTicks = new double[viewSpaceTicks.length];
                                double[] yTicks = new double[viewSpaceTicks.length];
                                // project ticks onto real plane z = 0
                                for (int tCnt = 0; tCnt < viewSpaceTicks.length; tCnt++)
                                {
                                    double temp =  dis / (dis - viewSpaceTicks[tCnt].z);
                                    xTicks[tCnt] = (origin.x + (viewSpaceTicks[tCnt].x - origin.x) * temp);
                                    yTicks[tCnt] = (origin.y + (viewSpaceTicks[tCnt].y - origin.y) * temp);                                    
                                }
                                // check if clicked point is close enough to a tick mark
                                // should be only one, if not last is taken
                                int tIndex = -1;
                                for (int chCnt = 0; chCnt < viewSpaceTicks.length; chCnt++)
                                {   double tDis = Math.sqrt(    
                                        (p2Clicked.x - xTicks[chCnt]) * 
                                        (p2Clicked.x - xTicks[chCnt]) + 
                                        (p2Clicked.y - yTicks[chCnt]) * 
                                        (p2Clicked.y - yTicks[chCnt]));
//System.out.println("tDis = " + UF.format(tDis, 2));                                        
                                    //if (tDis <= Object3DContainer.SSTT)
                                	if (tDis <= DrawConstants.SSTT)
                                        tIndex = chCnt;
                                }
                                // tickmark found
                                if (tIndex >= 0)
                                {   
//System.out.println("tick found");                                    
                                    // adjust projIntersect for covered check
                                    projIntersectX = xTicks[tIndex];
                                    projIntersectY = yTicks[tIndex];
                                    // find lambda1 directly in view space
                                    Vector3D viewFrom = facets[i].trPoints[cnt];
                                    Vector3D viewTo = facets[i].trPoints[(cnt + 1) % facets[i].numPoints];
                                    Vector3D viewTick = viewSpaceTicks[tIndex];
                                    lambda1 = Vector3D.distance(viewTick, viewFrom) /
                                              Vector3D.distance(viewTo, viewFrom); 
                                    tickFound = true;          
                                    lambda1 = 1 - lambda1;
                                }
                            }
                            if (!tickFound)
                            {
                                // the exact point we are looking for is obtained as follows
                                // everything in view space
                                // 1)
                                // find the real(!) projection of the edge in the
                                // plane z = 0, thus embed in view space!
                                // start
//                            double temp =  dis / (dis - facets[i].trPoints[cnt].z);
//                            double startX = (origin.x + (facets[i].trPoints[cnt].x - origin.x) * temp);
//                            double startY = (origin.y + (facets[i].trPoints[cnt].y - origin.y) * temp);
//                            Vector3D start = new Vector3D(projVertexFrom2.x, projVertexFrom2.y, 0);
//                            temp =  dis / (dis - facets[i].trPoints[(cnt + 1) % facets[i].numPoints].z);
//                            double endX = (origin.x + (facets[i].trPoints[(cnt + 1) % facets[i].numPoints].x - origin.x) * temp);
//                            double endY = (origin.y + (facets[i].trPoints[(cnt + 1) % facets[i].numPoints].y - origin.y) * temp);
//                            Vector3D end = new Vector3D(projVertexTo2.x, projVertexTo2.y, 0);
                                // 2)
                                // create the real version of the clicked point
                                // in the z-plane in view space
                                // (1-lambda)*"start"+lambda*"end"                            
//                            Vector3D.scaleBy(start, 1 - lambda);
//                            Vector3D.scaleBy(end, lambda);
//                            Vector3D realClicked = Vector3D.plus(start, end);
                                // embed in view space
                                Vector3D realClicked = new Vector3D(projIntersectX, projIntersectY, 0);
                                // 3)
                                // now the line through (origin.x, origin,y, dis) and
                                // realClicked should intersect the transformed edge
                                // this corrects for the central projection
                                Line3D line1 = new Line3D(
                                    new Vector3D(origin.x, origin.y, dis), realClicked);
                                Line3D line2 = new Line3D(facets[i].trPoints[cnt],
                                    facets[i].trPoints[(cnt + 1) % facets[i].numPoints]);
                                int ist = Line3D.intersectionType(line1, line2);
//System.out.println("ist = " + ist);                            
                                Vector3D isPoint = Line3D.getIntersectionPoint(line1, line2);
                                // get the position of isPoint
                                lambda1 = 
                                    Vector3D.distance(isPoint, 
                                        facets[i].trPoints[(cnt + 1) % facets[i].numPoints]) /
                                    Vector3D.distance(facets[i].trPoints[cnt], 
                                        facets[i].trPoints[(cnt + 1) % facets[i].numPoints]);
//System.out.println("l = " + lambda);                                    
//System.out.println("l-1 = " + lambda1);                                                                
                            } // if !tickFound
                            
                            // 4)
                            // now find the untransformed version of the clicked point
                            // in view space y-axis is reversed!        
                            lambda1 = 1 - lambda1;
//System.out.println("lambda1 = " + UF.format(lambda1, 5));                            
                            // (1-lambda)*"from"+lambda*"to"
                            Vector3D from = new Vector3D(edgeWithPoint[0]); // new!!
                            Vector3D.scaleBy(from, 1 - lambda1);
                            Vector3D to = new Vector3D(edgeWithPoint[1]); // new!! 
                            Vector3D.scaleBy(to, lambda1);
                            edgeWithPoint[2] = Vector3D.plus(from, to);
//System.out.println(edgeWithPoint[2].toString());
                            result = new FacetWithEdgePoint(
                                            edgeWithPoint, facets[i]);
                            
                            // find the projection of the new point in
                            // the plane
                            //Point projVertex = new Point((int) Math.round(projIntersectX),(int) Math.round(projIntersectY));
                            int projVertexX = (int) Math.round(projIntersectX);
                            int projVertexY = (int) Math.round(projIntersectY);
                            // if the new point is covered reject it
                            // see VertexClicked
                            if (filled)
                            {   if (vertexCovered(dis, origin, i, projVertexX, projVertexY))
                                {    
                                    return null;
                                }
                                else // not covered
                                {    
                                    return result;
                                }
                            }
                            else // object is not filled
                                return result;
                        } // close enough
                        // else keep looking
                    } // projection the on segment
                    // else keep looking
                } // edge loop   
            } // facet loop    
        }
        return result;
    }

    // give a segment and a point in the plane, find the projection
    // of the point on the line through the segment of the form
    // (1-lambda)*from+lambda*to
    // projection is on the segment if 0<lambda<1
    // assume from != to
    // integer version
    //public double projectionOnSegment(Point from, Point to, Point pClicked)
    public double projectionOnSegment(int fromX, int fromY, int toX, int toY, int pClickedX, int pClickedY)
    {   // directional vector of segment
        //Point dir = new Point(toX - fromX, toY - fromY);
        int dirX = toX - fromX;
        int dirY = toY - fromY;
        double cClicked = dirX * pClickedX + dirY * pClickedY;
        // line through pClicked perpendicular to segment
        // has equation dir.x*X + dir.y*Y = cClicked
        // segment is from + lambda*dir, find lambda such that 
        // dir.x*from.x + lambda*dir.x*dir.x +
        // dir.y*from.y + lambda*dir.y*dir.y = cClicked
        // or lambda = (cClicked - dir.x*from.x - dir.y*from.y) /
        //             (dir.x*dir.x + dir.y*dir.y)
        double lambda =  (cClicked - dirX*fromX - dirY*fromY) /
                         (dirX*dirX + dirY*dirY);
        return lambda;
    }    
    // real version
    public double projectionOnSegment(Vector2D from, Vector2D to, Vector2D pClicked)
    {   // directional vector of segment
        Vector2D dir = new Vector2D(to.x - from.x, to.y - from.y);
        double cClicked = dir.x * pClicked.x + dir.y * pClicked.y;
        // line through pClicked perpendicular to segment
        // has equation dir.x*X + dir.y*Y = cClicked
        // segment is from + lambda*dir, find lambda such that 
        // dir.x*from.x + lambda*dir.x*dir.x +
        // dir.y*from.y + lambda*dir.y*dir.y = cClicked
        // or lambda = (cClicked - dir.x*from.x - dir.y*from.y) /
        //             (dir.x*dir.x + dir.y*dir.y)
        double lambda =  (cClicked - dir.x*from.x - dir.y*from.y) /
                         (dir.x*dir.x + dir.y*dir.y);
        return lambda;
    }    
    
    // make all vertices redundant!
    // for objects
// to be redefined for object groups??
// not if you define additional objects already
// redundant from the first, i.e. objects cannot 
// contain references to the same instance of a Vector3D
    public void loosenVertices()
    {   // initialized as false
        boolean[] verticesUsed = new boolean[numVertices];
        // facet loop
        for (int i = 0; i < numFacets; i++)
            // points loop
            for (int j = 0; j < facets[i].numPoints; j++)
            {   // points[j] refers to vertex at index 
                int index = facets[i].indices[j];
                // this vertex is already referenced
                if (verticesUsed[index])
                {   // copy and add vertex and label  
                    // at position numVertices-1 (last)
                    if (vertexLabels[index] != null)
                        addVertex(new Vector3D(vertices[index]),
                                  new String(vertexLabels[index]));
                    else
                        addVertex(new Vector3D(vertices[index]), null);
                    // update indices AND points          
                    facets[i].indices[j] = numVertices - 1;
                    facets[i].points[j] = vertices[numVertices - 1];
                }
                else // mark it as referenced
                    verticesUsed[index] = true;
            }
    }
} // class Object3D

class Vector2D
{   public double x;
    public double y;
    public Vector2D(double x, double y)
    {   this.x = x;
        this.y = y;
    }    
    public double distance(Vector2D v)
    {   return Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y));
    }    
}

class FacetWithVertex
{   public Vector3D vertex;
    public Facet3D facet;
    public FacetWithVertex(Vector3D v, Facet3D f)
    {   vertex = v;
        facet = f;
    }
}
class FacetWithEdgePoint
{   public Vector3D[] edgeWithPoint;
    public Facet3D facet;
    public FacetWithEdgePoint(Vector3D[] ewp, Facet3D f)
    {   edgeWithPoint = ewp;
        facet = f;
    }
}
    
// just a wrapper
class EWP
{   public Vector3D[] edgeWithPoint;
    public EWP(Vector3D[] ewp)
    {   edgeWithPoint = ewp;
    }
}

    
class IncidenceMatrix
{   int size;
    int[][] matrix;
    int maxCnt;
    int[] maxRows;
    int[] rowSums;
    int rowMax;
    public IncidenceMatrix(int s)
    {   size = s;
        matrix = new int[size][size];
    }    
    public void setElement(int i, int j, int val)
    {   if ((i >= 0) && (i < size) &&
            (j >= 0) && (j < size))
            matrix[i][j] = val;
    }    
    public int getElement(int i, int j)
    {   if ((i >= 0) && (i < size) &&
            (j >= 0) && (j < size))
            return matrix[i][j];
        else
            return 0;
    }    
    public void relax()
    {   for (int i = 0; i < size; i++)
            for (int j = i + 1; j < size; j++)        
            {   //if (i != j)
                //{   
                    if (matrix[i][j] == matrix[j][i])
                    {   matrix[i][j] = 0;
                        matrix[j][i] = 0;
                    }    
                //}    
            }
    }
    // checkColumns bevat de indices van de rijen die nog meedoen
    public void findMaximalElements(int[] checkColumns, int maxType)
    {   int numRows = checkColumns.length;
        switch (maxType)
        {   case 0:
            {   maxCnt = 0;
                // te groot
                maxRows = new int[numRows];
                // vindt indices in checkColums van rijen waarin 
                // alleen nullen of enen staan in de kolommen
                // in checkColumns
                for (int fCnt = numRows - 1; fCnt >= 0; fCnt--)
                {   if (isTopRow(checkColumns[fCnt], checkColumns))
                    {   maxRows[maxCnt] = fCnt;
                        maxCnt++;
                    }    
                }

            }    
            break;
            case 1:
            {   maxCnt = 0;
                // te groot
                maxRows = new int[numRows];
// lijkt iets beter met lijntjes erbij                
                findRowSums2(checkColumns);
                for (int fCnt = numRows - 1; fCnt >= 0; fCnt--)
                {   if (rowSums[fCnt] == rowMax)
                    {   maxRows[maxCnt] = fCnt;
                        maxCnt++;
                    }    
                }                
            }    
            break;
            default: //none?
        }
    }
    // checkColumns bevat de indices van de rijen die nog meedoen
    // i is an index from checkColumns
    public boolean isTopRow(int i, int[] checkColumns)
    {   boolean result = true;
        for (int cnt = 0; cnt < checkColumns.length; cnt++)
        {   result = result && (matrix[i][checkColumns[cnt]] >= 0);
        }
        return result;
    }    
    // find the rowSums of the matrix consisting only of
    // checkColumns rows AND columns
    public void findRowSums(int[] checkColumns)
    {   int numRows = checkColumns.length;
        rowMax = -10;
        rowSums = new int[numRows];
        for (int rowCnt = 0; rowCnt < numRows; rowCnt++)
        {   int sum = 0;
            for (int colCnt = 0; colCnt < numRows; colCnt++)
                sum += matrix[checkColumns[rowCnt]][checkColumns[colCnt]];
            rowSums[rowCnt] = sum;
            if (sum > rowMax)
                rowMax = sum;
        }
    }
    
    // find the rows of the matrix consisting only of
    // checkColumns rows AND columns, which
    // contain the minimum number of occurences of -1
    public void findRowSums2(int[] checkColumns)
    {   int numRows = checkColumns.length;
        rowMax = -10000;
        rowSums = new int[numRows];
        for (int rowCnt = 0; rowCnt < numRows; rowCnt++)
        {   int sum = 0;
            for (int colCnt = 0; colCnt < numRows; colCnt++)
            {   if (matrix[checkColumns[rowCnt]][checkColumns[colCnt]] == -1)
                    sum += matrix[checkColumns[rowCnt]][checkColumns[colCnt]];
            }    
            rowSums[rowCnt] = sum;
            if (sum > rowMax)
                rowMax = sum;
        }
    }
    
    public String toString()
    {   String result = "";
        for (int i = 0; i < size; i++)
        {   result += "row " + i + " = ";
            for (int j = 0; j < size; j++)
                result += matrix[i][j] + " ";                
            result += "\n";
        }
        return result;
    }    
}    


// initializing an empty Object3D
class EmptyObject3D extends Object3D
{   // default constructor
    public EmptyObject3D()
    {}
    public Object3D deepCopy()
    {   EmptyObject3D copy = new EmptyObject3D();
        makeDeepObjectCopy(copy);
        return copy;
    }    
}    

class ObjectGroup3D extends Object3D
{   // the objects in the group
    Vector objects = new Vector();
    // flagg for sorting subgroup facet arrays
    
// voor Doorzien op true zetten!    
    boolean sortSubArrays = true;

    
    // updating 
    // redefined
    public void updatePoints()
    {   for (int oCnt = 0; oCnt < objects.size(); oCnt++)
        {   Object3D ob = (Object3D) objects.elementAt(oCnt);
            ob.updatePoints();
        }
    }
    // enumerating the vertices, we only need REFERENCE(S) to the actual array
    // redefine for object groups
    public Vector enumerateVertices()
    {   Vector enumer = new Vector();
        for (int oCnt = 0; oCnt < objects.size(); oCnt++)
        {   Object3D ob = (Object3D) objects.elementAt(oCnt);
            Vector obEnum = ob.enumerateVertices();
            for (int eCnt = 0; eCnt < obEnum.size(); eCnt++)
            {   Object o = obEnum.elementAt(eCnt);
                enumer.addElement(o);
            }
        
        }        
        return enumer;
    }
    
    // redefined
    public Object3D leftChild()
    {   return (Object3D) objects.elementAt(0);
    }    
    // redefined
    public Object3D leftMostLeaf()
    {   return ((Object3D) objects.elementAt(0)).leftMostLeaf();
    }    
    
    // redefined
    public void findDiameter()
    {   if (diamSet)
            return;
        // assume the whole object tree is centered
        Object3D ob;
        diameter = 0;
        double temp;
        for (int i = 0; i < objects.size(); i++)
        {   ob = (Object3D) objects.elementAt(i);
            ob.findDiameter();
            temp = 2 * Vector3D.distance(center, ob.center) + ob.diameter;
            if (temp > diameter)
                diameter = temp;
        }
    }
    
    // redefined
    public double getDiameter()
    {   
        // assume the whole object tree is centered
        Object3D ob;
        double diam = 0;
        double temp;
        for (int i = 0; i < objects.size(); i++)
        {   ob = (Object3D) objects.elementAt(i);
            double obdiam = ob.getDiameter();
            temp = 2 * Vector3D.distance(center, ob.center) + obdiam;
            if (temp > diam)
                diam = temp;
        }
        return diam;
    }
    
    // default constructor for creating, then adding objects
    // then calling initObject3D(true, centerObject)
    public ObjectGroup3D()
    {
    }
    // turning a single object in an object group
    public ObjectGroup3D(Object3D ob, boolean centerObject)
    {   addObject3D(ob);
        filled = ob.filled;
        initObject3D(true, centerObject);
        fixFacetArray();
    }
    
    // find the text associated with vertex v
    // redefined, search the objects in the tree via objectContains
    public String vertexText(Vector3D vertex)
    {   String result = null;
        Object3D ob = objectContains(vertex);
        if (ob != null)
            return ob.vertexText(vertex);
        return result;
    }
    
    
    // define separately for objectgroups, treat as subclass
    // see remarks in class Object3D, treat ObjectGroup as a subclass 
    public void makeDeepGroupCopy(ObjectGroup3D copy)
    {   
        for (int i = 0; i < objects.size(); i++)
        {   Object3D ob = (Object3D) objects.elementAt(i);
            copy.addObject3D(ob.deepCopy());
// dit zorgt voor de goede topParent()!            
        }
        // attributes of the group as top parent object
        copy.outlined = outlined;
        copy.filled = filled;
        copy.visible = visible;
// dit maar even laten        
//public Matrix3D oMat;
        copy.centerSet = centerSet;
        copy.diamSet = diamSet;
        copy.diameter = diameter;
        copy.center = new Vector3D(center);
        // attributes of the group as top parent objectgroup        
        copy.sortSubArrays = sortSubArrays;
        
        copy.numVertexLabels = numVertexLabels;
// testing the deep copy
//copy.setFilled(!filled);
// copy some attributes here    
    
        copy.fixFacetArray(); 
//        return copy;
    }    
    
    
    // use something like this in every subclass
    // note that any deepCopy of an ObjectGroup3D is returned
    // as an Object, so cast necessary
    public Object3D deepCopy()
    {   ObjectGroup3D copy = new ObjectGroup3D();
        makeDeepGroupCopy(copy);
        return copy;
    }    
    
    // redefined 
    // centers are found recursively
    public void findCenter()
    {   if (centerSet)
            return;
        double cx = 0;
        double cy = 0;
        double cz = 0;
        for (int i = 0; i < objects.size(); i++)
        {   Object3D ob = (Object3D) objects.elementAt(i);
            ob.findCenter();
            cx += ob.center.x;
            cy += ob.center.y;
            cz += ob.center.z;
        }
        cx /= objects.size();
        cy /= objects.size();
        cz /= objects.size();
        center = new Vector3D(cx, cy, cz);
    }
    
    // redefined 
    // centers are found recursively
    public Vector3D getCenter()
    {   
        double cx = 0;
        double cy = 0;
        double cz = 0;
        for (int i = 0; i < objects.size(); i++)
        {   Object3D ob = (Object3D) objects.elementAt(i);
            Vector3D obcenter = ob.getCenter();
            cx += obcenter.x;
            cy += obcenter.y;
            cz += obcenter.z;
        }
        cx /= objects.size();
        cy /= objects.size();
        cz /= objects.size();
        return new Vector3D(cx, cy, cz);
    }
    
    // redefined
    // all centers of the tree must have been be calculated
    public void center()
    {   for (int i = 0; i < objects.size(); i++)
        {   Object3D ob = (Object3D) objects.elementAt(i);
            ob.translateBy(-center.x, -center.y, -center.z);
        }
        center = new Vector3D(); // (0,0,0)
    }
    // redefined    
    // move the objectgroup, its members and its center over (cx, cy, cz)
    // if the center was (0,0,0) the new center is (cx, cy, cz)
    // all centers of the tree must have been calculated
    public void translateBy (double cx, double cy, double cz)
    {   for (int i = 0; i < objects.size(); i++)
        {   Object3D ob = (Object3D) objects.elementAt(i);
            ob.translateBy(cx, cy, cz);
        }        
        Vector3D.translateBy(center, cx, cy, cz);
    }
    // redefined
    // all centers of the tree must have been calculated
    public void centerAt (double cx, double cy, double cz)
    {   // objects FIRST       
        for (int i = 0; i < objects.size(); i++)
        {   Object3D ob = (Object3D) objects.elementAt(i);
            ob.translateBy(cx - center.x, cy - center.y, cz - center.z);
        }        
        Vector3D.translateBy(center, cx - center.x, cy - center.y, cz - center.z);
    }
    
    // add an object to this group
    public void addObject3D(Object3D od)
    {   objects.addElement(od);
        od.parent = this;
    }

    // search recursively
    public void removeObject3D(Object3D od)
    {   objects.removeElement(od);
        for (int i = 0; i < objects.size(); i++)
        {   Object3D ob = (Object3D) objects.elementAt(i);
            if (ob instanceof ObjectGroup3D)
                ((ObjectGroup3D) ob).removeObject3D(od);
        }    
    }    

    // redefined
    // concatenate the facet arrays of the whole tree(!) recurse
    public void fixFacetArray()
    {   // determine total facet number
        numFacets = 0;
        for (int j = 0; j < objects.size(); j++)
        {   Object3D ob = (Object3D) objects.elementAt(j);
            // recursively
            if (ob instanceof ObjectGroup3D)
            {   ObjectGroup3D og = (ObjectGroup3D) ob;
                og.fixFacetArray();
                numFacets += og.numFacets;
            }    
            else
                numFacets += ob.numFacets;
        }    
        // now all subgroups also have a facet array!
        facets = new Facet3D[numFacets];
        int fCount = 0;
        for (int j = 0; j < objects.size(); j++)
        {   Object3D ob = (Object3D) objects.elementAt(j);
            // fast copy
            System.arraycopy(ob.facets, 0, facets, fCount,
                             ob.numFacets);
            fCount += ob.numFacets;                 
        } 
    }    

    // redefined
    public void transformBy(Matrix3D m, double dis, boolean zSort)
    {   Object3D ob;
        // transform vertices and update facets of individual objects    
        // no sorting necessary at higher levels in the tree!!
        for (int j = 0; j < objects.size(); j++)
        {   ob = (Object3D) objects.elementAt(j);
            if (sortSubArrays)
                ob.transformBy(m, dis, true);
            else
                ob.transformBy(m, dis, false);
        }    
        if (zSort)
            zMergeSort();
    }

/*
// nodig?
    // redefine oTransform (multiple selection!)
    // the objectgroup has NO vertexarray!!
    public void oTransform()
    {   Object3D ob;
        // transform vertices and facets of individual objects    
        for (int j = 0; j < objects.size(); j++)
        {   ob = (Object3D) objects.elementAt(j);
            ob.oTransform();
        }    
    }
*/

/*
// nodig?
    public void setVisible(Object3D ob, boolean visible)
    {   ob.setVisible(visible);
    }
*/    


    // set the fill mode the facets of this objectgroup
    // avoid disappearance
    // redefined to set correctly set the object(group) flaggs
    public void setFilled(boolean fill)
    {   // this group
        filled = fill;
        // all object(group)s below
        for (int i = 0; i < objects.size(); i++) 
        {   Object3D ob = (Object3D) objects.elementAt(i);
            ob.setFilled(fill);
        }
    }

    // set an (objectgroup) to visible/invisible
    // avoid disappearance
    // redefined to set correctly set the object(group) flaggs
    public void setVisible(boolean vis)
    {   // this group
        visible = vis;
        // all object(group)s below        
        for (int i = 0; i < objects.size(); i++) 
        {   Object3D ob = (Object3D) objects.elementAt(i);
            ob.setVisible(vis);
        }
    }

    // set an (objectgroup) to outlined/not outlined
    // avoid disappearance
    // redefined to set correctly set the object(group) flaggs
    public void setOutlined(boolean outline)
    {   // this group
        outlined = outline;
        // all object(group)s below        
        for (int i = 0; i < objects.size(); i++) 
        {   Object3D ob = (Object3D) objects.elementAt(i);
            ob.setOutlined(outline);
        }
    }

    public void setVisible(int index, boolean visible)
    {   if ((index >= 0) && (index <= (objects.size() - 1)))
        {   Object3D ob = (Object3D) objects.elementAt(index);
            ob.setVisible(visible);
        }
    }
    
    // find the object containing vertex v (if any)
    // assume object groups have NO vertices
    // search recursively
    public Object3D objectContains(Vector3D v)
    {   Object3D result = null;
        for (int i = 0; i < objects.size(); i++)
        {   Object3D o = (Object3D) objects.elementAt(i);
            if (o instanceof ObjectGroup3D)
            {   result = ((ObjectGroup3D) o).objectContains(v);   
                if (result != null)
                    return result;
            }    
            else
            {   if (o.containsVertex(v) >= 0)
                {   result = o;
                    return result;
                }
            }    
        }
        return result;
    }


    // search recursively
    // note: this group contains ALL facets, but we
    // want to find the object(!) containing the facet 
    public Object3D objectContains(Facet3D f)
    {   Object3D result = null;
        for (int i = 0; i < objects.size(); i++)
        {   Object3D o = (Object3D) objects.elementAt(i);
            if (o instanceof ObjectGroup3D)
            {   result = ((ObjectGroup3D) o).objectContains(f);   
                if (result != null)
                    return result;
            }    
            else
            {   if (o.containsFacet(f) >= 0)
                {   result = o;
                    return result;
                }
            }    
        }
        return result;
    }



} // class Object3DGroup


class ObjectWithPoint extends ObjectGroup3D
{   // attributes
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
if (DoorzienGWT.version == DoorzienGWT.EPN)
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
if (DoorzienGWT.version == DoorzienGWT.EPN)
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
if (DoorzienGWT.version == DoorzienGWT.EPN)
{
    for (int vCnt = 0; vCnt < replacement.facets[i].numPoints; vCnt++)
    {   if ((replacement.facets[i].vertexLabels[vCnt] != null) &&
             replacement.facets[i].vertexLabels[vCnt].equals("XX")
           )
           if (!filled)// & cutFilled)
               replacement.facets[i].vertexCodes[vCnt] = DrawConstants.planeOutlineColorIndex + Facet3D.HIDDENSHIFT;
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
//System.out.println("replaces orig");                                                        
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



class ObjectWithLine extends ObjectGroup3D
{   // attributes
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
                                     facet1.edgeCodes[0] = lineColorIndex + Facet3D.HIDDENSHIFT;                                
                                     facet1.edgeCodes[1] = lineColorIndex + Facet3D.HIDDENSHIFT;                                                     
                                     facet1.vertexCodes[1] = lineColorIndex + 10;
                                     facet1.normal = new Vector3D();   
                                }
                                if (facet2 != null)    
                                {    
                                     facet2.edgeCodes[0] = lineColorIndex + Facet3D.HIDDENSHIFT;                                
                                     facet2.edgeCodes[1] = lineColorIndex + Facet3D.HIDDENSHIFT;                 
                                    
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
                        rep.facets[j].vertexCodes[pCnt] = DrawConstants.lineColorIndex + 10;     
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
                //Facet3D segFacet = new Facet3D(replacement.vertices,
                //    newInds, Color.black); // any color OK never filled
                Facet3D segFacet = new Facet3D(replacement.vertices,
                        newInds, DrawConstants.black); // any color OK never filled
                    
                
                // attributes OK
                // inner segment
                segFacet.normal = new Vector3D();
                // override edgeCodes
                segFacet.edgeCodes[0] = lineColorIndex + Facet3D.HIDDENSHIFT;
                segFacet.edgeCodes[1] = lineColorIndex + Facet3D.HIDDENSHIFT;                
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
                        segFacet.vertexCodes[0] = DrawConstants.pointColorIndex;
                
                
                }
                else  // point is an inner vertex  
                {   segFacet.vertexCodes[0] = lineColorIndex + Facet3D.HIDDENSHIFT;                
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
                        segFacet.vertexCodes[1] = DrawConstants.pointColorIndex;
                }
                else  // inner vertex  
                {    segFacet.vertexCodes[1] = lineColorIndex + Facet3D.HIDDENSHIFT;                
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
             //Facet3D extFacet1 = new Facet3D(extensions.vertices, inds, Color.black);
             Facet3D extFacet1 = new Facet3D(extensions.vertices, inds, DrawConstants.black);
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
             //Facet3D extFacet2 = new Facet3D(extensions.vertices, inds, Color.black);
             Facet3D extFacet2 = new Facet3D(extensions.vertices, inds, DrawConstants.black);
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
if (DoorzienGWT.version == DoorzienGWT.EPN)
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
if (DoorzienGWT.version == DoorzienGWT.EPN)
{
    for (int vCnt = 0; vCnt < replacement.facets[i].numPoints; vCnt++)
    {   if ((replacement.facets[i].vertexLabels[vCnt] != null) &&
             replacement.facets[i].vertexLabels[vCnt].equals("XX")
           )
           if (!filled)// & cutFilled)
               replacement.facets[i].vertexCodes[vCnt] = DrawConstants.planeOutlineColorIndex + Facet3D.HIDDENSHIFT;
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
                            replacement.facets[i].color = DrawConstants.objectColor;
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
                            replacement.facets[i].color = DrawConstants.planeColor;
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



class ObjectWithPlane extends ObjectGroup3D
{   // attributes
    Object3D origObject;
    ObjectGroup3D origObjectGroup;
    Object3D replacement; // the new object at objects.elementAt(1);
    // contains the facets being replaced to create the
    // plane in origObjectGroup
    // objects.elementAt(2) contains the cut
    Object3D cut;
    int planeColorIndex;
    boolean cutFilled = false;
    // the plane as object, see class Plane3D
    Plane3D plane;
    // "empty" constructor for copying
    public ObjectWithPlane()
    {}
    // constructor
    // assumed non-collinear
    public ObjectWithPlane(ObjectGroup3D og, 
                           Vector3D point1, Vector3D point2, Vector3D point3, 
                           int pcIndex, boolean makeCut)
    {   origObjectGroup = og;
        // objects.elementAt(0)
        addObject3D(origObjectGroup);
        origObject = origObjectGroup.leftMostLeaf();                
        planeColorIndex = pcIndex;        
        plane = new Plane3D(point1, point2, point3);
        replacement = new EmptyObject3D();
        cut = new EmptyObject3D();  
        // remembering lines in the cut
        Vector cutLines = new Vector();
//System.out.println("OWP");        
//System.out.println("OOG numF = " + origObjectGroup.numFacets);
        for (int i = 0; i < origObjectGroup.numFacets; i++)
        {   // shortcut
            Facet3D facet = origObjectGroup.facets[i];

            boolean hasRep = false;
            if (origObjectGroup instanceof ObjectWithPlane)
               hasRep = ((ObjectWithPlane) origObjectGroup).hasReplacement(facet);
            else if (origObjectGroup instanceof ObjectWithLine)
               hasRep = ((ObjectWithLine) origObjectGroup).hasReplacement(facet);
            
            //if (facet.visible)
            if (!hasRep)
            {   
//System.out.println("not hasRep");
                // method starts looking in origObjectGroup
                boolean oReplace = replacesOrigObject(facet);
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
//System.out.println("pts = " + pts);                
                // arrays for indices of these points (maximum)
                int[] leftInds = new int[pts + 1];
                int[] rightInds = new int[pts + 1];
                // whole facet left of plane
                if ((onCnt == 0) && (rightCnt == 0))
                {   // nothing to do
//System.out.println("left");                
                }
                // whole facet right of plane
                else if ((leftCnt == 0) && (onCnt == 0))
                {   // nothing to do
//System.out.println("right");                                
                }
                // facet touches plane from the left
                else if ((onCnt == 1) && (rightCnt == 0))
                {   
                    
                    // bij een segment replacen met een vlak gekleurd punt
                    if ((facet.numPoints == 2) && makeCut)
                    {   int index = replacement.numVertices;
                        replacement.addVertex(new Vector3D(facet.points[0]), null);
                        replacement.addVertex(new Vector3D(facet.points[1]), null);
                        int[] inds = new int[2];
                        inds[0] = index;
                        inds[1] = index + 1;
                        //Facet3D lFacet = new Facet3D(replacement.vertices, inds, Color.black);
                        Facet3D lFacet = new Facet3D(replacement.vertices, inds, DrawConstants.black);
                        Facet3D.copyAttributes(facet, lFacet, true);
                        replacement.addFacet(lFacet);
                        lFacet.isReplacementOf = facet;
                        facet.visible = false;
                        if (plane.planePosition(lFacet.points[0]) == 0)
                            lFacet.vertexLabels[0] = "XX";
                        if (plane.planePosition(lFacet.points[1]) == 0)
                            lFacet.vertexLabels[1] = "XX";
//System.out.println("segment touches from left");                                    
                    
                    }
//System.out.println("touches from left");                                    
                }
                // facet touches plane from the right
                else if ((leftCnt == 0) && (onCnt == 1))
                {   
                    
                    // bij een segment replacen met een vlak gekleurd punt
                    if ((facet.numPoints == 2) && makeCut)
                    {   int index = replacement.numVertices;
                        replacement.addVertex(new Vector3D(facet.points[0]), null);
                        replacement.addVertex(new Vector3D(facet.points[1]), null);
                        int[] inds = new int[2];
                        inds[0] = index;
                        inds[1] = index + 1;
                        //Facet3D rFacet = new Facet3D(replacement.vertices, inds, Color.black);
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
//System.out.println("segment touches from right");                                    
                    
                    }
                    
                    
                    // for the moment nothing to do
                    // snijvlak een punt gebeurt niet?                
//System.out.println("touches from right");                                    
                }
                // facet has one or more edges on the plane
                // and is to the left or right
                // or facet cuts the plane
                else
                {
//System.out.println("to be cut");                    
                    // now walk along the facet edgewise, if edge v1->v2 
                    // is studied v1 is updated
                    for (int j = 0; j < pts; j++)
                    {   // find current side vj -> v(j+1)
                        // vj, last is v(pts-1)
                        Vector3D v1 = new Vector3D(facet.points[j]);
                        // position of v1
                        int pos1 = plane.planePosition(v1);                    
                        // v(j+1), last is v0
                        Vector3D v2 = new Vector3D(
                            facet.points[(j + 1) % pts]);
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
                        
                        // vj "left" v(j+1) "right"
                        // add vj->v to left, copy of v to right
                        // and copy of v to cut
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
                            // add another copy to the cut
                            Vector3D vcc = new Vector3D(v);
                            if (makeCut && oReplace &&
                                (cut.containsVertex(vcc) < 0)
                               ) 
                                cut.addVertex(vcc, null);
                        }
                        // vj "right" v(j+1) "left"
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
                            // niet aan links toevoegen als leftCnt == 0    
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
                            // niet aan rechts toevoegen als rightCnt == 0                                                        
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
                            // niet aan links toevoegen als leftCnt == 0     
                            // als leftCnt > 0 is (omdat facetten convex zijn)
                            // noodzakelijkerwijs rightCnt = 0
                            if (leftCnt > 0)
                            {
                                // add to left
                                replacement.addVertex(v1, null);
                                leftInds[leftIndCnt] = replacement.numVertices - 1;
                                leftIndCnt++;
                            }
                            // niet aan rechts toevoegen als rightCount == 0                            
                            // als rightCnt > 0 is (omdat facetten convex zijn)
                            // noodzakelijkerwijs leftCnt = 0
                            if (rightCnt > 0)
                            {
                                // add to right
                                Vector3D v1c = new Vector3D(v1);                        
                                replacement.addVertex(v1c, null);                            
                                rightInds[rightIndCnt] = replacement.numVertices - 1;
                                rightIndCnt++;
                            }
                            // omdat leftCnt > 0 en rightCnt > 0 niet kan
                            // is een vertex voor de cut genoeg
                            if ((leftCnt > 0) || (rightCnt > 0))
                            {
                                // add another copy to the cut
                                Vector3D v1cc = new Vector3D(v1);
                                if (makeCut && oReplace &&
                                    (cut.containsVertex(v1cc) < 0)
                                   ) 
                                    cut.addVertex(v1cc, null);
                            }                                
// geval leftCnt == 0 && rightCnt == 0:
// 2 mogelijkheden: 
// 1) dit is een facet i.e. numPoints > 2
// dan moet dit een deel van een facet van origObject zijn omdat je niet 2 keer 
// dezelfde cut mag maken (moet tegengehouden worden!)
// kijk dan naar plane position van v1 + facet.normal
// als < 0 rechts toevoegen (er kan links niets meer zijn, normaal wijst naar buiten)
// als > 0 links toevoegen (iets dergelijks)
// niets toevoegen aan cut, dat wordt wel gedaan, maar zet de cut uit
// 2) dit is en lijn(segment) dat t.z.t. in de cut terecht moet komen
// je moet het dus wel bewaren!!??
// echter, niets toevoegen aan cut, het segment kan ook (half) binnenin de cut liggen
// zie verder bij na het maken van de cut                                
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
                                else if (facet.numPoints == 2)
                                {   
//System.out.println("segment in cut");                                    
                                    Line3D line = new Line3D(v1, v2);
                                    if (!cutLines.contains(line))
                                        cutLines.addElement(line);
                                }
                            } //(leftCnt == 0) && (rightCnt == 0)
                        } // last else if
                    } // for - points    
                } // else position    
                
                // object could touch the plane! nee
                // dat is even weggelaten
                // arrange indices to correct length
                // create new facets
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
if (facet.numPoints == 2)                    
{
// copy the normal so that segment is treated as an INNER segment
// by the new Painter's
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
if (facet.numPoints == 2)                    
{
// copy the normal so that segment is treated as an INNER segment
// by the new Painter's
rightFacet.normal = new Vector3D(facet.normal);    
if (plane.planePosition(rightFacet.points[0]) == 0)
    rightFacet.vertexLabels[0] = "XX";
if (plane.planePosition(rightFacet.points[1]) == 0)
    rightFacet.vertexLabels[1] = "XX";
}
                    
                } // right
                if ((leftIndCnt > 0) || (rightIndCnt > 0))
                {    facet.visible = false;
if (facet.numPoints == 2)                         
{ // wat is dit?
}
                }
            } // facet.visible
        } // for facet loop
        
//System.out.println("repFacets = " + replacement.numFacets);        
int twoCnt = 0;
for (int k = 0; k < replacement.numFacets; k++)
{   if (replacement.facets[k].numPoints == 2)
        twoCnt++;
}    
//System.out.println("repsegs = " + twoCnt);        
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
// testing for lines                    
                }
                else // new point
                {   
//if (makeCut)                    
//System.out.println("index < 0");                                                
                    // a segment was cut
                    if (repFacet.numPoints == 2)
                        repFacet.vertexCodes[pCnt] = DrawConstants.lineColorIndex +
                                                     Facet3D.HIDDENSHIFT;
                    else
                    {   
                        int eIndex = Facet3D.edgeContainsSegment(
                            facet, repFacet.points[pCnt], repFacet.points[pCnt]);
//if (makeCut)                    
//System.out.println("eIndex = " + eIndex);                                                
                            
                        if (eIndex >= 0)
                        {   
//System.out.println("eIndex >= 0");                            
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
//if (index >= 0)
//indexCnt++;
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
                
// hier corrigeren voor een zijvlak                
                
//                    redCnt++;
                }
                        
// denk even:
// numPoints > 2: vererf van facet als facet segment bevat EN segment niet in 
// het vlak
// anders het segment rood maken, hier, niet in de cut!!
                        
            }    
        }    
        
// tick marks
// object vertexlabels NA init
        // set properties of replacement
        replacement.outlined = origObjectGroup.outlined; // locally overridden
        replacement.filled = origObjectGroup.filled; // locally overridden
        replacement.visible = origObjectGroup.visible;
// dit maar even laten        
//public Matrix3D oMat;
        // fix center and diameter as of originalObject
        replacement.initObject3D(true, new Vector3D(origObjectGroup.center),
                                 origObjectGroup.diameter, false);
        // puts replacement at objects.elementAt(1)                         
        if (replacement.numVertices > 0)        
        {    addObject3D(replacement);                         
        }

        // kan niet voor facets replacing an original facet omdat je niet
        // twee keer hetzelfde kan snijden
        // check if any elements of replacement were part of a cut "higher up"






if (makeCut)        
{
    
int repCut = 0;        
        for (int i = 0; i < replacement.numFacets; i++)
        {   Facet3D cut = replacesCut(replacement.facets[i]);
            if (cut != null)
            {   
repCut++;       
//cut.visible = false;
                // any edge of replacement.facets[i] which is
                // on a cut higher up has no outline (inherited via edgeLabels)
                // any edge of replacement.facets[i] which is
                // not on this cut should be outlined
                // use containsDirSegment!!
// dit is overbodig want gebeurt nooit???????                
// de bedoeling is dat de doorsnede van een higher cut en this cut wel outlined is
// werkt dit nu OK?
                for (int j = 0; j < replacement.facets[i].numPoints; j++)
                {   Vector3D start = replacement.facets[i].points[j];
                    Vector3D end = 
                        replacement.facets[i].points[(j + 1) % replacement.facets[i].numPoints];
                    int index = Facet3D.edgeContainsDirSegment(cut, start, end);
//System.out.println("repCut-index = " + index);                    
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
                
            }
        } 
//System.out.println("repCut = " + repCut);        

} //if (makeCut)
        
        // algo NIET correct als origObjectGroup of equivalent origObject
        // helemaal in een(1) vlak ligt, 
        // maar in dat geval hebben we de cut ook niet nodig
        // nl. de gevallen cutting a facet or cutting a cut
        // algo ook niet correct als de cut punten "binnenin" bevat,
        // dus alleen punten saven als ze OP origObject liggen
        // snijsels worden later toegevoegd

Vector orientedCut = new Vector();
Vector orientedCutLabels = new Vector();

        if ((cut.numVertices > 2) && makeCut)
        {
            
        // nu nog de cut vinden
        // vertices zijn er al, dit worden 1 of meer facets
        // maak de cut met de "linker" kant, andere kant is de reverse
        // gaat niet op bij torus??
            boolean[] verticesUsed = new boolean[cut.numVertices];
        // loop voor meerdere facets
// gaat fout bij de torus, zonder dit krijg je maar een vlakje vd torusdoorsnede
// toch redundancy?? kan eigenlijk niet
        while (firstNotUsed(verticesUsed) >= 0)
        {   
//System.out.println("fnu = " + firstNotUsed(verticesUsed));                            
                // kies een ongebruikte vertex
                int startIndex = firstNotUsed(verticesUsed);
                int[] newInds = new int[cut.numVertices]; // genoeg??
                int indCnt = 0;
                Facet3D leftFacet = null;
                while (verticesUsed[startIndex] == false)
                {   
                
//System.out.println("si = " + startIndex);                
                    // label als gebruikt
                    verticesUsed[startIndex] = true;
                    // komt op index indCnt
                    newInds[indCnt] = startIndex;
                    indCnt++;
                    // get the point
                    Vector3D v1 = cut.vertices[startIndex];
//System.out.println("v1 = " + v1.toString());                

// add to orientedCut
orientedCut.addElement(new Vector3D(v1));
                    // replacement bestaat als ongeinitialiseerd object!
                    // all its facets are visible
                    // vindt een facet uit replacement dat deze vertex bevat
                    // geen punten rechts heeft en het punt na v1
                    // ook op de cut heeft
                    // het moet ook origObject vervangen!!
                    Vector v1Facets = replacement.facetsContaining(v1);
//System.out.println("v1Facets = " + v1Facets.size());                
                    //Facet3D leftFacet = null;
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
                                // else continue looking
                            }
                            // else continue looking
                        }
                        fCount++;
                        // else continue looking
                    }
//System.out.println("found = " + found);                                                
                
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
                    // index volgende punt
                    Vector3D v2 = leftFacet.points[(v1Index + 1) % leftFacet.numPoints];
//System.out.println("v2 = " + v2.toString());                


                    startIndex = indexOf(v2, cut.vertices);
                    // loop terminates if 
                    // verticesUsed[startIndex] = true
                    // dan zijn we 1 keer rond
                
                } // while for one cut-facet
                        
// hier newInds trimmen, nodig?  nee, alleen verschillende toegevoegd         
// doe maar wel
                int[] finalCutInds = new int[indCnt];
//System.out.println("indCnt = " + indCnt);                
                for (int k = 0; k < indCnt; k++)
                    finalCutInds[k] = newInds[k];
                    
                Facet3D cutFacet = new Facet3D(cut.vertices, 
                    finalCutInds, DrawConstants.planeColor);
                Facet3D.copyAttributes(leftFacet, cutFacet, false);
                // override!!
                cutFacet.color = DrawConstants.planeColor;
                
                for (int i = 0; i < cutFacet.numPoints; i++)
                    cutFacet.vertexCodes[i] = - 1; //DrawingPanel.planeOutlineColorIndex;
                
                
                // locally override outline to no outline
                for (int i = 0; i < cutFacet.numPoints; i++)
                    cutFacet.edgeCodes[i] = -1;
                cut.addFacet(cutFacet);    
//System.out.println("facet constructed");                
                
        } // while multicut                

//System.out.println(cut.numVertices);
//System.out.println(orientedCut.size());

        // set properties of cut
        // later vullen, kijk even hoe        
        cut.outlined = false; 
        cut.setFilled(false); 
        cut.visible = origObjectGroup.visible;
// dit maar even laten        
//public Matrix3D oMat;
        // fix center and diameter as of originalObject
        cut.initObject3D(true, new Vector3D(origObjectGroup.center),
                         origObjectGroup.diameter, false);
                         
// EERST
// hier de cut snijden met alle(?) hogere planes
// alle vorige cuts zijn door deze cut en degenen na hen al doorgesneden
// dus ligt gaat elke snijlijn met zo'n plane noodzakelijkerwijs door
// 2 vertices van de deze cut, maar deze cut is niet doorgesneden
// lost dit het fill probleem op??

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
//System.out.println("plane found" + k);                    
//System.out.println("before recut facets = " + cut.numFacets);            
//int beforeFacets = cut.numFacets;

// dit leidt tot onzin-resultaten

                    Plane3D pl = (Plane3D) ob;
                    cut = cutWithPlane(cut, pl, planeColorIndex);
//int afterFacets = tCut.numFacets;
//if (afterFacets > beforeFacets)
//    cut = tCut;
//System.out.println("after recut facets = " + cut.numFacets);            
/*                    
// de cut van de cut moet van hidden kleur zijn, mits deze niet op origObject ligt!!
*/
                } //if ob instanceof Plane3D
                // lines
                else if (ob instanceof Line3D)
                {   Line3D li = (Line3D) ob;
//System.out.println("line found" + k);                                    
// we already know the plane through the cut!
                    Plane3D testPlane = plane.copy();
                    int isType = Plane3D.intersectionType(li, testPlane);    
                    if (isType == 2)
                    {   
                        
                        
//System.out.println("iT = 2");                        
//System.out.println("before recut facets = " + cut.numFacets);            


                        cut = cutWithLine(cut, li, DrawConstants.lineColorIndex);
                        
//System.out.println("after recut facets = " + cut.numFacets);                                    
                        
                        for (int i = 0; i < origObjectGroup.numFacets; i++)
                        {   // shortcut
                            Facet3D f = origObjectGroup.facets[i];
                            // check for a segment
                            if (f.numPoints == 2)
                            {   
                    // na snijding alle 2-dim facets van origObjectGroup
                    // die op de lijn liggen
                    // en geen replacement hebben
                    // onzichtbaar maken, maar hebben deze dan een replacement?
                    // NEE
                                
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
// hier nog minibug?                                            
// ja, de je kan het segment "vergeten" 
//                                                cut.facets[j].isReplacementOf = f;
//System.out.println("segment hidden");
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
//System.out.println("cut-vertex-hidden");                                        
                                }
                            }
                        }
                        
                        
                    } // if isType == 2
                    else
                    {
//System.out.println("line rejected" + k);                                                        
                    }
                } //if ob instanceof Line3D   
            }
        
        } // construction != null
        

// DAN
// nu lijnen IN deze cut, zo'n lijn kan een klein stukje zijn want hij is al doorgesneden door alle
// cuts hogerop, dus EERST kan eerst
// deze cut is echter nog niet volgens deze lijnen onderverdeeld
// doe dit alsnog, merk op dat de cut altijd "hele" lijnen bevat, d.w.z. er 
// is een string van segmentjes die een hele lijn door de cut vormen,
// dit komt omdat je een lijn altijd "helemaal" tekent
// de eindpunten van deze lijnenstrings zijn niet noodzakelijk punten
// op de cut!!
// d.w.z. ook al markeer je een of ander segmentje, je snijdt de hele handel
// altijd met de lijn
// je kan de lijnen zelf (als 2-dim facets) weggooien, de cut wordt "blauw" versneden
// NB snijdt niet de cut met de lijnen maar het hele ding opnieuw??
// nee dat mag niet (nl 2 keer dezelfde lijn)
// niet nodig, de cut is aanvankelijk een(1) enkel facet en dat worden er gewoon meer

// NB lijnen niet IN de cut zijn OK doorgesneden door de cut


// is het resultaat nu een replacement van iets of een multi-facetted cut
// liefst het laatste
// NB eerdere planes zijn door deze cut WEL replaced (asymmetrie?)
// dat moet zo wel, want je mag het hele ding niet twee keer volgens
// een hoger vlak doorsnijden

//System.out.println("cut added");                         
        // puts cut at objects.elementAt(2)                         
        addObject3D(cut);                         
    } // (cut.numVertices > 2) && makeCut
        
        // properties of this ObjectWithPlane        
        sortSubArrays = origObjectGroup.sortSubArrays;        
        outlined = origObjectGroup.outlined;
        setFilled(origObjectGroup.filled);
        visible = origObjectGroup.visible;
// dit maar even laten        
//public Matrix3D oMat;
        // fix center and diameter
        initObject3D(true, new Vector3D(origObjectGroup.center),
                     origObjectGroup.diameter, false);
                     
//System.out.println("os = " + objects.size());        
// RESTRICTIE EPN
if (DoorzienGWT.version == DoorzienGWT.EPN)
    hideNonOrigVertices();
// EINDE RESTRICTIE epn
// alleen voor EPN?
inheritTickMarks();

        
// voor EPN OOK kijken of de vertex verborgen is,
// dan NIET labelen
//fixFacetArray();    
// lettering replacement
if (makeCut)
{

Vector newVertices = new Vector();
int labelCnt = origObjectGroup.numVertexLabels;

// label the vertices of the oriented cut if they do not yet have a label
// add them to the list
// 
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
//System.out.println("labelCnt = " + labelCnt);
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
/*                
                // check if label can be found from the oriented cut
                int oCutIndex = orientedCut.indexOf(aVertex);
                if (oCutIndex >= 0)
                {   // give it the label from the oriented cut
                    // and add to the list
                    String cutLabel = (String) orientedCutLabels.elementAt(oCutIndex);
                    replacement.facets[fCnt].vertexLabels[vCnt] = cutLabel;
                    newVertices.addElement(aVertex);                    
                }
                else // should not happen for EPN
                {   
*/                    
                    // give it a NEW label and add to the list
                    labelCnt++;
                    replacement.facets[fCnt].vertexLabels[vCnt] = getLabel(labelCnt);
                    newVertices.addElement(aVertex);                    
//                }
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

        
        
        
} // if (makeCut)        
else
    numVertexLabels = origObjectGroup.numVertexLabels;
    } // constructor   

    
    // A is label number 1, Z is number 26
    // AA is number 27
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

    // support
    int firstNotUsed(boolean[] useArray)
    {   int result = -1;
        for (int i = 0; i < useArray.length; i++)
        {   if (!useArray[i])
                return i;    
        }
        return result;
    }    

    int indexOf(Vector3D v, Vector3D[] vert)
    {   int result = -1;
        for (int i = 0; i < vert.length; i++)
        {   if (Vector3D.equals(vert[i], v))
                return i;
        }
        return result;
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

    public double getLlFactor()
    {   if (origObjectGroup instanceof ObjectWithLine)
            return ((ObjectWithLine) origObjectGroup).getLlFactor();
        else if (origObjectGroup instanceof ObjectWithPlane)
            return ((ObjectWithPlane) origObjectGroup).getLlFactor();    
        else
            return 0;
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
//DrawingPanel.showTime("OWP hiding vertices");        
    }    
    
// EINDE RESTRICTIE TOT ORIGINAL OBJECT VOOR EPN




// gegeven een facet van origObject, zoek alle zichtbare facets die dit
// facet op dit moment replacen
// gebruik fixFacetArray
// loop door de array en gebruik iets als replacesOrigFacet
// zie boven
// stop het resultaat in een vector
    
    
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
if (DoorzienGWT.version == DoorzienGWT.EPN)
{
    for (int vCnt = 0; vCnt < replacement.facets[i].numPoints; vCnt++)
    {   if ((replacement.facets[i].vertexLabels[vCnt] != null) &&
             replacement.facets[i].vertexLabels[vCnt].equals("XX")
           )
           if (!filled)// & cutFilled)
               replacement.facets[i].vertexCodes[vCnt] = DrawConstants.planeOutlineColorIndex + Facet3D.HIDDENSHIFT;
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
//System.out.println("replaces orig");                                                        
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
//System.out.println("replaces cut");                                                                                    
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
//System.out.println("internal segment");                    
                         // now this thing is never filled and even invisible if
                         // the "surrounding object" is 
                   replacement.facets[i].visible = !filled;
                }   

            } // if !hasReplacement
        } // for replacement.facets
        
// dit moet erbij, waarom??  misschien ook niet, laat maar even staan      
//System.out.println("sf cut-facets = " + cut.numFacets +
//                   " sf cut-vertices = " + cut.numVertices);

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
//System.out.println("isCut");                    
                    cut.facets[j].color = DrawConstants.planeColor;
                    cut.facets[j].filled = true; // testing filled cuts
                }    
                else    
                    cut.facets[j].filled = false;            
            }
            }
// dus hier de zaak onderscheppen als de de cuts in niet lege toestand
// wilt vullen
        }


// kijk even hoe te veranderen als je de cut wilt vullen        
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
    
    
    
    public void fillCuts(boolean b)
    {   cutFilled = b;
        if (origObjectGroup instanceof ObjectWithPlane)
            ((ObjectWithPlane) origObjectGroup).fillCuts(b);
        else if (origObjectGroup instanceof ObjectWithLine)
            ((ObjectWithLine) origObjectGroup).fillCuts(b);        
        // else do nothing    
    }    
    
// nodig? elders?    
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

// nodig? elders?
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
    // put the "recipe" for this OwP in Vector recipe
    // list is in reverse order!!
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
    {   recipe.addElement(new Integer(planeColorIndex));
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
    
    // als dit niet werkt, gebruik construction
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
// wat als geen replacement?
        if (copy.objects.size() >= 2)
            copy.replacement = (Object3D) copy.objects.elementAt(1);
        else
            copy.replacement = new EmptyObject3D();
// wat als geen cut?        
//System.out.println("os = " + objects.size());
//System.out.println("cos = " + copy.objects.size());
        if (copy.objects.size() >= 3)
            copy.cut = (Object3D) copy.objects.elementAt(2);
        else
            copy.cut = new EmptyObject3D();
        copy.planeColorIndex = planeColorIndex;
        // constructor of Plane3D deep copies line.point1 and line.point2
//        copy.plane = new Plane3D(plane.support,
//                        Vector3D.plus(plane.support, plane.direction1),
//                        Vector3D.plus(plane.support, plane.direction1));
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
        // do not make a cut
        ObjectWithPlane owp = new ObjectWithPlane(fObjectGroup, 
            plane.support, 
            Vector3D.plus(plane.support, plane.direction1),
            Vector3D.plus(plane.support, plane.direction2), 
            cutColorIndex, false);    
        // now the only interesting part of owp is replacement
        return owp.replacement;
    }

    public static Object3D cutWithPlane(Object3D cut, Plane3D plane, int cutColorIndex)
    {   
        
//System.out.println("before " + cut.numFacets);        
        // dummy object group
        ObjectGroup3D cutObjectGroup = new ObjectGroup3D(cut, false);   
        cutObjectGroup.filled = cut.filled; // niet relevant?
        cutObjectGroup.fixFacetArray(); //!!!
        // do not make a cut
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
//System.out.println("uncut facet");                
                firstIndex = cutCut.numVertices;
                // add vertices of oFacet
                for (int i = 0; i < oFacet.numPoints; i++)
                    cutCut.addVertex(new Vector3D(oFacet.points[i]), null);
                // add oFacet, no need to make a new one?
                // 1) nieuwe indices maken en vervangen
                int[] newInds = new int[oFacet.numPoints];
                for (int k = 0; k < oFacet.numPoints; k++)
                    newInds[k] = k + firstIndex;
                oFacet.indices = newInds;    
                // 2) points updaten (voor de zekerheid)
                oFacet.updatePoints(cutCut.vertices);
                // 2a facet toevoegen
                cutCut.addFacet(oFacet);
            } // if !hasReplacement
        } // for for facets of owp.originalObjectGroup    
        
        for (int n = 0; n < owp.replacement.numFacets; n++)
        {   
//System.out.println("cut facet");                            
            // shortcut
            Facet3D rFacet = owp.replacement.facets[n];
            firstIndex = cutCut.numVertices;
            // add vertices of oFacet
            for (int i = 0; i < rFacet.numPoints; i++)
                cutCut.addVertex(new Vector3D(rFacet.points[i]), null);
            // add rFacet, no need to make a new one?
            // 1) nieuwe indices maken en vervangen
            int[] newInds = new int[rFacet.numPoints];
            for (int k = 0; k < rFacet.numPoints; k++)
                newInds[k] = k + firstIndex;
            rFacet.indices = newInds;    
            // 2) points updaten (voor de zekerheid)
            rFacet.updatePoints(cutCut.vertices);
            // 2a facet toevoegen
            cutCut.addFacet(rFacet);
            // 3) isReplacementOf zetten
            rFacet.isReplacementOf = null;
            // vertex/edgecodes die rood zijn op hidden zetten
            for (int pCnt = 0; pCnt < rFacet.numPoints; pCnt++)
            {   if (rFacet.edgeCodes[pCnt] == cutColorIndex)
                    rFacet.edgeCodes[pCnt] += 10;     
                if (rFacet.vertexCodes[pCnt] == cutColorIndex)
                    rFacet.vertexCodes[pCnt] += 10;         
            }  // for edgecodes  

        } // for for facets of owp.replacement

        // set properties of cutCut
        // later vullen, kijk even hoe        
        cutCut.outlined = cut.outlined; 
        cutCut.filled = cut.filled; 
        cutCut.visible = cut.visible;
// dit maar even laten        
//public Matrix3D oMat;
        // fix center and diameter as of cut
        cutCut.initObject3D(true, new Vector3D(cut.center),
                            cut.diameter, false);

//System.out.println("after " + cutCut.numFacets);                    

        return cutCut;
    }


// note: line is in the plane of the cut
    public static Object3D cutWithLine(Object3D cut, Line3D line, int lineColorIndex)
    {
//System.out.println("before " + cut.numFacets);        
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
                // 1) nieuwe indices maken en vervangen
                int[] newInds = new int[oFacet.numPoints];
                for (int k = 0; k < oFacet.numPoints; k++)
                    newInds[k] = k + firstIndex;
                oFacet.indices = newInds;    
                // 2) points updaten (voor de zekerheid)
                oFacet.updatePoints(cutCut.vertices);
                // 2a facet toevoegen
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
            // 1) nieuwe indices maken en vervangen
            int[] newInds = new int[rFacet.numPoints];
            for (int k = 0; k < rFacet.numPoints; k++)
                newInds[k] = k + firstIndex;
            rFacet.indices = newInds;    
            // 2) points updaten (voor de zekerheid)
            rFacet.updatePoints(cutCut.vertices);
            // 2a facet toevoegen
            cutCut.addFacet(rFacet);
            // edgecodes die blauw zijn op hidden zetten(?)
            for (int pCnt = 0; pCnt < rFacet.numPoints; pCnt++)
            {   if (rFacet.edgeCodes[pCnt] == lineColorIndex)
                    rFacet.edgeCodes[pCnt] += 10;     
            }  // for edgecodes  
            // vertexcodes die blauw zijn op hidden zetten(?)
            for (int pCnt = 0; pCnt < rFacet.numPoints; pCnt++)
            {   if (rFacet.vertexCodes[pCnt] == lineColorIndex)
                    rFacet.vertexCodes[pCnt] += 10;     
            }  // for vertexcodes  

            
            // 3) isReplacementOf zetten
            rFacet.isReplacementOf = null;
            

        } // for for facets of owl.replacement

        // set properties of cutCut
        // later vullen, kijk even hoe        
        cutCut.outlined = cut.outlined; 
        cutCut.filled = cut.filled; 
        cutCut.visible = cut.visible;
// dit maar even laten        
//public Matrix3D oMat;
        // fix center and diameter as of cut
        cutCut.initObject3D(true, new Vector3D(cut.center),
                            cut.diameter, false);

//System.out.println("after " + cutCut.numFacets);                    

        return cutCut;
    }


    // gegeven een ObjectGroup3D (die minstens plane bevat!), 
    // zoek alle facets die geen replacement hebben en
    // stop kopien in een object3D
    // roteer dit object 
    // en maak er een dummy group van


    public static ObjectGroup3D getCut(ObjectGroup3D ob, Plane3D plane)
    {   Object3D cut = new EmptyObject3D();
        ob.fixFacetArray();
        
//System.out.println("ob-nf = " + ob.numFacets);        
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
// wrong, points could be collinear!                    
//                    inPlane = plane.contains(ob.facets[i].points[0]) &&
//                              plane.contains(ob.facets[i].points[1]) &&
//                              plane.contains(ob.facets[i].points[2]);              
                }              
                if (inPlane)
                {   
                    
//System.out.println("inPlane");                    
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
//System.out.println("" + obFacets.size());            
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
       // normaal van hele cut
       Vector3D cutNormal = new Vector3D(cutPlane.normal);
       //Vector3D.makeUnitary(cutNormal);
       
            
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

    



        // gegeven een ObjectGroup3D (die minstens plane bevat!), 
        // neem deep copy van origObject
        // maak ObjectWithPlane MET cut

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
        ObjectWithPlane owp = new ObjectWithPlane(startGroup, plane.support,
            Vector3D.plus(plane.support, plane.direction1),
            Vector3D.plus(plane.support, plane.direction2),
            0, true);
        owp.fixFacetArray();    
        // maak de cut meteen zwart?
        Object3D left = new EmptyObject3D();
        Object3D right = new EmptyObject3D();
        ObjectGroup3D leftGroup, rightGroup;
  
//        Vector leftVerticesLabeled = new Vector();
//        Vector leftVertexLabels = new Vector();
//        Vector rightVerticesLabeled = new Vector();
//        Vector rightVertexLabels = new Vector();
  
//        Facet3D leftCutFacet = null;
//        Facet3D rightCutFacet = null;
        
        for (int i = 0; i < owp.numFacets; i++)
        {   
            if (!owp.hasReplacement(owp.facets[i]))
            {   // planepos gebruiken
                // om te kijken waar facet heen moet
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
// cut apart bekijken, kom je vanzelf tegen
// de cut hoort rechts(!)
// zijn omgekeerde links
            
                } // points of facet[i]
                // left of cut
                if ((leftPos > 0) && (onPos >= 0))
                {   // add facet to left
                    int firstIndex = left.numVertices;
                    for (int j = 0; j < owp.facets[i].numPoints; j++)
                        left.addVertex(new Vector3D(owp.facets[i].points[j]), null);
                    int[] inds = new int[owp.facets[i].numPoints];
                    for (int k = 0; k < owp.facets[i].numPoints; k++)
                        inds[k] = k + firstIndex;
                    Facet3D leftFacet = new Facet3D(left.vertices, inds, owp.facets[i].color);
                    left.addFacet(leftFacet);
                    Facet3D.copyAttributes(owp.facets[i], leftFacet, false);
/*                    
                    for (int m = 0; m < leftFacet.numPoints; m++)
                    {   if (owp.facets[i].vertexLabels[m] != null)
                            leftFacet.vertexLabels[m] = new String(owp.facets[i].vertexLabels[m]);
                            
                        if (!leftVerticesLabeled.contains(leftFacet.points[m]))
                        {   leftVerticesLabeled.addElement(leftFacet.points[m]);
                            leftVertexLabels.addElement(leftFacet.vertexLabels[m]);
                        }   
                        
                    }    
*/                    
                    // update cut colors?
                    
                }    
                // right of cut
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
/*                    
                    for (int m = 0; m < rightFacet.numPoints; m++)
                    {   if (owp.facets[i].vertexLabels[m] != null)
                            rightFacet.vertexLabels[m] = new String(owp.facets[i].vertexLabels[m]);
                            
                        if (!rightVerticesLabeled.contains(rightFacet.points[m]))
                        {   rightVerticesLabeled.addElement(rightFacet.points[m]);
                            rightVertexLabels.addElement(rightFacet.vertexLabels[m]);
                        }   
                        
                            
                    }    
*/                    
                    // update cut colors?
                    
                }    
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

//System.out.println("left " + leftVerticesLabeled.size());            
//for (int lft = 0; lft < leftVertexLabels.size(); lft++)
//System.out.println((String) leftVertexLabels.elementAt(lft));
//System.out.println("right " + rightVerticesLabeled.size());                        
        // find true center and diameter    
/*        
        for (int lft = 0; lft < leftCutFacet.numPoints; lft++)
        {   int lIndex = leftVerticesLabeled.indexOf(leftCutFacet.points[lft]);
            if (lIndex >= 0)
                leftCutFacet.vertexLabels[lft] = 
                    new String((String) leftVertexLabels.elementAt(lIndex));
        
        }
*/
/*
        for (int rgt = 0; rgt < rightCutFacet.numPoints; rgt++)
        {   int rIndex = rightVerticesLabeled.indexOf(rightCutFacet.points[rgt]);
            if (rIndex >= 0)
                rightCutFacet.vertexLabels[rgt] = 
                    new String((String) rightVertexLabels.elementAt(rIndex));
        
        }
*/
/*
        int leftIndex = 0;
        for (int lCnt = 0; lCnt < leftVertexLabels.size(); lCnt++)
        {   leftIndex = Math.max(leftIndex,
                getLabelIndex((String) leftVertexLabels.elementAt(lCnt)));
        }    
*/
/*
        int rightIndex = 0;
        for (int rCnt = 0; rCnt < rightVertexLabels.size(); rCnt++)
        {   rightIndex = Math.max(rightIndex,
                getLabelIndex((String) rightVertexLabels.elementAt(rCnt)));
        }    
*/
/*
        left.numVertexLabels = leftIndex; // not relevant?
        right.numVertexLabels = rightIndex; // not relevant?
*/        
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
        
//        letterObject(left);
//        letterObject(right);
//System.out.println("left-vert = " + left.numVertices);
//System.out.println("right-vert = " + right.numVertices);

                
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
//System.out.println("" + trConstruction.size());            
//System.out.println("" + minTrConstruction.size());            
        
        if (trPos < minTrPos)
        {   leftGroup = rebuild(left, trConstruction);
            leftGroup.fixFacetArray();
            int labelCnt = otherIndex;
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
        
        // rebuild de twee stukken
        ObjectGroup3D result = new ObjectGroup3D();
        result.addObject3D(leftGroup);
        result.addObject3D(rightGroup);
        // force center and diameter
        result.initObject3D(true, new Vector3D(ob.center), ob.diameter, false);
        return result;
        
    }    


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
    
    // deel opnieuw(!) letters uit
    public static void letterObject(Object3D ob)
    {   Vector newVertices = new Vector();
        int labelCnt = 0;
        for (int fCnt = 0; fCnt < ob.numFacets; fCnt++)
            for (int fvCnt = 0; fvCnt < ob.facets[fCnt].numPoints; fvCnt++)
            {   int index = newVertices.indexOf(
                    ob.facets[fCnt].points[fvCnt]);
                if (index < 0)
                {   newVertices.addElement(ob.facets[fCnt].points[fvCnt]);
                    labelCnt++;
                    ob.facets[fCnt].vertexLabels[fvCnt] = getLabel(labelCnt);
                }    
                else
                    ob.facets[fCnt].vertexLabels[fvCnt] =
                        getLabel(index + 1);
                    
            }        
        ob.numVertexLabels = labelCnt;
        
        
    }    

    public static ObjectGroup3D rebuild(Object3D sObject, Vector recipe)
    {   Object3D start = sObject.deepCopy();
        start.setVisible(true);
        // dummy object group
        ObjectGroup3D startGroup = new ObjectGroup3D(start, false);   
        startGroup.numVertexLabels = sObject.numVertexLabels;
        startGroup.setFilled(start.filled); 
        startGroup.numVertexLabels = start.numVertexLabels;        
        startGroup.fixFacetArray(); //!!!
        // nu bouwen volgens recipe
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
// dit kan ook deep copy in OWL en OWP vervangen
// wanneer die methode niet goed werkt
    }


    
}   // class ObjectWithPlane

