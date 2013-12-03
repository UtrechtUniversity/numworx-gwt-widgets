package fi.grafiek3dgwt.client;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
//import java.io.Serializable;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;


// abstract class, subclasses MUST initialize facets in some way and call
// initObject3D(true) for initializing the matrix and finding sizes
// a 3D object
public abstract class Object3D //implements Serializable
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

    public void setTickMarksVisible(boolean b)
    {	fixFacetArray();
    	for (int fCnt = 0; fCnt < numFacets; fCnt++)
    	{   facets[fCnt].ticksVisible = b;
	
    	}
    }	

    public void setThickenVertices(boolean b)
    {	fixFacetArray();
    	for (int fCnt = 0; fCnt < numFacets; fCnt++)
    	{   facets[fCnt].thickenVertices = b;
    	
    	}
    }	
    
    public void setLetters(boolean b)
    {	fixFacetArray();
    	for (int fCnt = 0; fCnt < numFacets; fCnt++)
    	{   facets[fCnt].letters = b;
    	
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
    {   int lNumVertices = numVertices;
    	Vector3D[] lVertices = vertices;
    	Vector3D[] lTrVertices = trVertices;
    	int lNumFacets = numFacets;
    	Facet3D[] lFacets = facets;
    	Object3D topParent = topParent();
    	
    	// taking m == null only sorts
        if (m != null)
        {   Vector3D temp;
            //for (int i = 0; i < lNumVertices; i++)
        	for (int i = lNumVertices - 1; i >= 0; i--)
            {   //Object3D topParent = topParent();
                // center the object(group) here!!
                lTrVertices[i] = m.transform(
                    Vector3D.minus(lVertices[i], topParent.center));
            }
            //for (int i = 0; i < lNumFacets; i++)
        	for (int i = lNumFacets - 1; i >= 0; i--)
            {   // notify facet[i] to reference the correct transformed 3-points
                lFacets[i].updateTrPoints(lTrVertices);
                lFacets[i].calculateZValue(m.origin, dis);
                // unit normal stays in  (0, 0, 0)
                lFacets[i].unitNormal = m.nTransform(lFacets[i].normal);
                Vector3D.makeUnitary(lFacets[i].unitNormal);
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
    {   
    	if (list == null)
    		return list;
    	int listLength = list.length;
    	if (listLength == 1)
            return list;
        int half = listLength / 2;
        Facet3D[] list1 = new Facet3D[half];
        Facet3D[] list2 = new Facet3D[listLength - half];
        // fast copy            
        System.arraycopy(list, 0, list1, 0, half);    
        // fast copy
        System.arraycopy(list, half, list2, 0, listLength - half);                
        list1 = mergeSort(list1);
        list2 = mergeSort(list2);
        return merge(list1, list2);
    }    
    public Facet3D[] merge(Facet3D[] list1, Facet3D[] list2)
    {   
    	int list1Length = list1.length;
    	int list2Length = list2.length;
    	
    	Facet3D[] list = new Facet3D[list1Length + list2Length];
        int index1 = 0;
        int index2 = 0;
        int index = 0;
        while ((index1 < list1Length) || (index2 < list2Length))
        {   // check if list1 is empty
            if ((index1 == list1Length) && (index2 < list2Length))
            {   // fast copy                
                System.arraycopy(list2, index2, 
                                 list, index, 
                                 list2Length - index2);
                return list;
            }
            // check if list2 is empty
            else if ((index1 < list1Length) && (index2 == list2Length))
            {   // fast copy                
                System.arraycopy(list1, index1, 
                                 list, index, 
                                 list1Length - index1);                
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
    
    public void setOutlineColor(CssColor olc)
    {	for (int i = 0; i < numFacets; i++)
    	{	facets[i].outlineColor = olc;
    		facets[i].edgeColors[0] = olc;
    	}
    	
    }
    
    public void setFillColor(CssColor flc)
    {	for (int i = 0; i < numFacets; i++)
    	{	facets[i].color = flc;
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
        public FacetWithVertex facetWithVertexClicked(
            int x, int y, double dis, 
            //Vector3D origin, boolean includeInvisibles)
            Vector3D origin)
    {   FacetWithVertex result = null;
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
                        //new Point(p.xpoints[cnt], p.ypoints[cnt]);
                    	new Point(p.puntenX[cnt], p.puntenY[cnt]);
                    if (distance(pClicked, projVertex) <= 
                        Object3DContainer.SSTT)
                    {    result = new FacetWithVertex(
                            facets[i].points[cnt],
                            facets[i]);                            
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

    
    // distance between two integer points
    public double distance(Point p1, Point p2)
    {   return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) +
                         (p1.y - p2.y) * (p1.y - p2.y));   
    }    
    // given a vertex belonging to the facet with index i,
    // check if a projected facet covers its projection
    // (assuming the facets are filled)
    // this is a facet with index > i (!)
    public boolean vertexCovered(double dis, Vector3D origin,
                   int fIndex, Point projVertex)
    {   boolean result = false;
        Polygon p;
        // start on top, facets are already sorted, always by zValue
        for (int i = numFacets - 1; i > fIndex; i--)
        {   // only visible facets can cover
            if (facets[i].visible)
            {   p = facets[i].project(dis, origin);
                if (p.contains(projVertex))
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
        Point pClicked = new Point(x,y);        
        // start on top, facets are already sorted, always by zValue
// zorg er hier voor dat de facet array gesorteerd is naar
// de volgorde waarin je ook TEKENT!
        for (int i = numFacets - 1; i >= 0; i--)
        {   // includeInvisibles determines which vertices can be clicked
            if (facets[i].visible)// || includeInvisibles)
            {   p = facets[i].project(dis, origin);
                for (int cnt = 0; cnt < facets[i].numPoints; cnt++)
                {   // find projected edge
                    Point projVertexFrom = 
                        //new Point(p.xpoints[cnt], p.ypoints[cnt]);
                    	new Point(p.puntenX[cnt], p.puntenY[cnt]);
                    Point projVertexTo = 
                        //new Point(p.xpoints[(cnt + 1) % facets[i].numPoints], 
                        //          p.ypoints[(cnt + 1) % facets[i].numPoints]);
                        new Point(p.puntenX[(cnt + 1) % facets[i].numPoints], 
                                  p.puntenY[(cnt + 1) % facets[i].numPoints]);
                    	
                    // if projected segment is a point skip this
                    double lambda;
                    if (projVertexFrom.equals(projVertexTo))
                    {   lambda = 2;
                    }    
                    else
                    {
                        // find the projection of the clicked point
                        // on the projected edge(line), i.e. a point of the form
                        // (1-lambda)*projVertexFrom+lambda*projVertexTo
                        lambda = projectionOnSegment(projVertexFrom, projVertexTo,
                                                     pClicked);
                    }
                    // if projection is ON the segment                                    
                    if ((lambda > 0) && (lambda < 1))
                    {   // real(!) x-coordinate of projection
                        double projIntersectX = 
                            (1 - lambda) * projVertexFrom.x +
                            lambda * projVertexTo.x;
                        // real(!) y-coordinate of projection    
                        double projIntersectY = 
                            (1 - lambda) * projVertexFrom.y +
                            lambda * projVertexTo.y;
                        // distance clicked - projection (in plane!)   
                        double distance = Math.sqrt(    
                            (pClicked.x - projIntersectX) * 
                            (pClicked.x - projIntersectX) + 
                            (pClicked.y - projIntersectY) * 
                            (pClicked.y - projIntersectY));
                        // check if clicked point is close enough    
                        if (distance <= Object3DContainer.SSTT)
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
                            Point projVertex = new Point(
                                (int) Math.round(projIntersectX),
                                (int) Math.round(projIntersectY));
                            // if the new point is covered reject it
                            // see VertexClicked
                            if (filled)
                            {   if (vertexCovered(dis, origin, i, projVertex))
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
        Point pClicked = new Point(x,y);        
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
                    Point projVertexFrom = 
                        //new Point(p.xpoints[cnt], p.ypoints[cnt]);
                    	new Point(p.puntenX[cnt], p.puntenY[cnt]);
                    Point projVertexTo = 
                        //new Point(p.xpoints[(cnt + 1) % facets[i].numPoints], 
                        //          p.ypoints[(cnt + 1) % facets[i].numPoints]);
                        new Point(p.puntenX[(cnt + 1) % facets[i].numPoints], 
                                  p.puntenY[(cnt + 1) % facets[i].numPoints]);
                    	
                    // find projected edge in real plane
                    Vector2D projVertexFrom2 = 
                        new Vector2D(p2.xpoints[cnt], p2.ypoints[cnt]);
                    Vector2D projVertexTo2 = 
                        new Vector2D(p2.xpoints[(cnt + 1) % facets[i].numPoints], 
                                     p2.ypoints[(cnt + 1) % facets[i].numPoints]);
                    // if projected segment is a point skip this
                    double lambda;
                    if (projVertexFrom.equals(projVertexTo))
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
                        double projIntersectX = 
                            (1 - lambda) * projVertexFrom2.x +
                            lambda * projVertexTo2.x;
                        // real(!) y-coordinate of projection    
                        double projIntersectY = 
                            (1 - lambda) * projVertexFrom2.y +
                            lambda * projVertexTo2.y;
                        // distance clicked point - projection of ibidem (in real plane!)   
                        double distance = Math.sqrt(    
                            (p2Clicked.x - projIntersectX) * 
                            (p2Clicked.x - projIntersectX) + 
                            (p2Clicked.y - projIntersectY) * 
                            (p2Clicked.y - projIntersectY));
                        // check if clicked point is close enough to projected edge    
                        if (distance <= Object3DContainer.SSTT)
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
                                    if (tDis <= Object3DContainer.SSTT)
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
                            Point projVertex = new Point(
                                (int) Math.round(projIntersectX),
                                (int) Math.round(projIntersectY));
                            // if the new point is covered reject it
                            // see VertexClicked
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
    public double projectionOnSegment(Point from, Point to, Point pClicked)
    {   // directional vector of segment
        Point dir = new Point(to.x - from.x, to.y - from.y);
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

