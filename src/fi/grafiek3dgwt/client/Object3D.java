package fi.grafiek3dgwt.client;

import java.util.*;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;

/**
 * an abstract class representing a 3-dimensional object consisting of vertices (vectors in 3-space) and
 * facets (polygons in 3-space); to build a specific 3-dimensional object, create a subclass of Object3D, which must
 * initialize the vertex array and with the help of this vertex array initializes the facet array (see class Facet3D),     
 * then call one of the methods initObject3D(); showing the Object3D in view space is taken care of by method
 * paintObject3D().    
 * @author huub
 */
public abstract class Object3D
{   
	/**
	 * the number of vertices
	 */
    int numVertices;
    /**
     * the vertex array (in work space)
     */
    Vector3D[] vertices;
    /**
     * the transformed vertices (in view space)
     */
    Vector3D[] trVertices;
    /**
     * text labels for the vertices
     */
    String[] vertexLabels;
    /**
     * the number of facets
     */
    int numFacets;
    /**
     * the facet array
     */
    Facet3D[] facets;
    /**
     * should all facets be outlined?
     */
    boolean outlined = true;
    /**
     * should all facets be filled?
     */
    boolean filled = true;
    /**
     * is this Oject3D visible?
     */
    boolean visible = true;
    /**
     * was the center set externally? if yes, it cannot be recalculated
     */
    boolean centerSet = false;
    /**
     * was the diameter set externally? if yes, it cannot be recalculated
     */
    boolean diamSet = false;
    /**
     * the diameter of this Obkect3D
     */
    double diameter = -1;
    /**
     * the center of this Object3D
     */
    Vector3D center = null;
    /**
     * in case this Object3D is an Objectgroup3D: the objectgroup's parent in the tree, could be an Objectgroup3D; <br> 
     * see class ObjectGroup3D 
     */
    Object3D parent = null;  
    /**
     * the number of vertex labels, redundant
     */
    int numVertexLabels;

    /**
     * if numTicks is positive create numTicks tickmarks on all edges of this Object3D, which can also be an 
     * ObjectGroup3D, since tickmarks exist on facet level; numTicks == 0 removes the existing tickmarks;
     * note that in case numTicks is larger then 1, the first two tickmarks determine all others 
     * @param numTicks the number of tickmarks wanted
     */
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
    /**
     * show/hide the tickmarks, works also for an ObjectGroup3D
     * @param b show/hide tickmarks?
     */
    public void setTickMarksVisible(boolean b)
    {	fixFacetArray();
    	for (int fCnt = 0; fCnt < numFacets; fCnt++)
    	{   facets[fCnt].ticksVisible = b;
    	}
    }	
    /**
     * should the vertices be drawn thickened? works also for an ObjectGroup3D  
     * @param b tickened/normal vertices
     */
    public void setThickenVertices(boolean b)
    {	fixFacetArray();
    	for (int fCnt = 0; fCnt < numFacets; fCnt++)
    	{   facets[fCnt].thickenVertices = b;
    	}
    }	
    /**
     * should the vertex labels be shown? works also for an ObjectGroup3D
     * @param b show/hide the vertex labels
     */
    public void setLetters(boolean b)
    {	fixFacetArray();
    	for (int fCnt = 0; fCnt < numFacets; fCnt++)
    	{   facets[fCnt].letters = b;
    	}
    }	
    /**
     * find the volume of this Object3D, works only for 3d-objects, not for objectgroups
     * @return volume of this Object3D
     */
    public double getVolume()
    {   double volume = 0;
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
    /**
     * update the points of all facets of this Object3D after changing the vertices; <br>
     * redefined for subclass ObjectGroup3D 
     */
    public void updatePoints()
    {   for (int fCnt = 0; fCnt < numFacets; fCnt++)
            facets[fCnt].updatePoints(vertices);
    }
    /**
     * return a Vector containing a reference to the vertex array of this Object3D;
     * used in enumerating the vertices of an ObjectGroup3D
     * redefined for subclass ObjectGroup3D
     * @return a one-element Vector containing a reference to the vertex array
     */
    public Vector<Vector3D[]> enumerateVertices()
    {   Vector<Vector3D[]> enumer = new Vector<Vector3D[]>();
        enumer.addElement(vertices);
        return enumer;
    }
    /**
     * add a vertex (with label) to this Object3D, leave everything else unchanged; 
     * <br>use only for 3d-objects, since object groups have no vertex arrays; 
     * adjust transformed vertices array and vertexLabel array; 
     * <br> use this method when creating the vertex array 
     * of an Object3D, before creating any facets 
     * @param v vertex to be added
     * @param label label (if any) of the vertex to be added
     */
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
        numVertexLabels++;
        // only needed for painting
        trVertices = new Vector3D[numVertices];
    }    
    /**
     * add a facet to this Object3D, leave everything else unchanged; use only for 3d-objects, since object groups (initilally) have no
     * facet arrays; 
     * <br> use this method when creating the facet array of an Object3D, after creating the vertex array
     * @param f facet to be added
     */
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
    /**
     * find all VISIBLE facets of this object(group) containing vertex v
     * works also for object groups
     * @param v the search vertex
     * @return a Vector with the facets containing vertex v   
     */
    public Vector<Facet3D> facetsContaining(Vector3D v)
    {   fixFacetArray();
        Vector<Facet3D> result = new Vector<Facet3D>();
        for (int i = 0; i < numFacets; i++)
        {   if (facets[i].visible &&
                (Facet3D.containsVertex(facets[i], v) >= 0)
               ) 
               result.addElement(facets[i]);
        }
        return result;
    }
    /**
     * find a VISIBLE (visible or invisible) facet of this object(group) containing
     * the directed edge v1v2; works also for object groups
     * @param v1 start of the directed edge 
     * @param v2 end of the directed edge
     * @param includeInvisibles search also among invisible facets
     * @return the Facet3D containing the directed edge v1v2 or null 
     */
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
    /**
     * find the text associated with vertex v
     * redefined for class ObjectGroup3D
     * @param vertex vertex whose text is required
     * @return the vertex text or null
     */
    public String vertexText(Vector3D vertex)
    {   String result = null;
        int index = containsVertex(vertex);
        if (index >= 0)
            return vertexLabels[index];
        return result;
    }
    /**
     * find the index of vertex v in the vertex array of this Object3D
     * works only for objects, since objectgroups have no vertex array
     * @param vertex the search vertex
     * @return the index of vertex v or minus 1
     */
    public int containsVertex(Vector3D vertex)
    {   int result = -1;
        for (int i = 0; i < numVertices; i++)
        {   if (Vector3D.equals(vertices[i], vertex))
               return i;
        }
        return result;
    }
    /**
     * find the index of Facet3D f in the facet array of this Object(group)3D
     * works also for objectgroups 
     * @param f the search facet
     * @return the index of facet f or minus 1
     */
    public int containsFacet(Facet3D f)
    {   fixFacetArray();
        int result = -1;
        for (int i = 0; i < numFacets; i++)
        {   if (facets[i] == f)
               return i;
        }
        return result;
    }
    /**
     * initialize an Object(Group)3D, in case of an Object3D after 
     * creating vertices and facets; determine center and diameter when not set externally
     * works also for objectgroups 
     * @param newObject is this a new Object(Group)3D?
     * @param centerObject should this Object(Group)3D be centered?
     */
    public void initObject3D(boolean newObject, boolean centerObject)
    {   if (newObject)
        {	if (numVertices > 0)
            	vertexLabels = new String[numVertices];
        }
        if (center == null)
            findCenter();
        if (centerObject)    
            center();
        if (diameter < 0)
            findDiameter();
            
    }
    /**
     * initialize an Object(Group)3D, in case of an Object3D after
     * creating vertices and facets; set the center to c a and determine the diameter
     * when not set externally
     * works also for objectgroups
     * @param newObject is this a new Object(Group)3D?
     * @param c the center of the Object(Group)3D (externally set) 
     * @param centerObject should this Object(Group)3D be centered?
     */
    public void initObject3D(boolean newObject, Vector3D c, boolean centerObject)
    {   center = c;
    	// this prevents recalculating the center 
        centerSet = true;
        initObject3D(newObject, centerObject);
    }
    /**
     * initialize an Object(Group)3D, in case of an Object3D after
     * creating vertices and facets; set the center to c a and diameter to d
     * when not set externally
     * works also for objectgroups
     * @param newObject is this a new Object(Group)3D?
     * @param c the center of the Object(Group)3D (externally set)
     * @param d the diameter of the Object(Group)3D (externally set)
     * @param centerObject should this Object(Group)3D be centered?
     */
    public void initObject3D(boolean newObject, Vector3D c, double d,
                             boolean centerObject)
    {   center = c;
    	// this prevents recalculating the center
        centerSet = true;
        diameter = d;
        // this prevents recalculating the diameter
        diamSet = true;
        initObject3D(newObject, centerObject);
    }
    /**
     * to make a deep copy of a subclass of Object3D maintaining the
     * subclass type, instantiate an empty object of that subclass type
     * and call this method; redefined for class ObjectGroup3D;
     * @param copy an empty Object3D of the required subclass type
     */
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
        copy.centerSet = centerSet;
        copy.diamSet = diamSet;
        copy.diameter = diameter;
        copy.center = new Vector3D(center);
    }    
    /**
     * must be redefined for all subclasses of Object3D
     * @return the deep copy of this Object3D
     */
    public abstract Object3D deepCopy();
    
    /**
     * set the attribute diameter as the maximum distance between any vertex and the center
     * of the object (which must have been set or calculated);
     * do nothing if diamSet == true; redefined for subclass ObjectGroup3D
     */
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
    /**
     * calculate the diameter this Object3D as the maximum distance between any vertex and the center
     * of the object (which must have been set or calculated);
     * do not change the attribute diameter; redefined for subclass ObjectGroup3D
     * @return the calculated diameter
     */
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
    /**
     * set the attribute center of this Object3D to the barycenter of all vertices;
     * do nothing if centerSet == true; redefined for subclass ObjectGroup3D 
     */
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
    /**
     * calculate the center of this Object3D as the barycenter of all vertices;
     * do not change the attribute center; redefined for subclass ObjectGroup3D
     * @return the calculated center
     */
    public Vector3D getCenter()
    {   double cx = 0;
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
    /**
     * set the center of this Object(Group)3D externally
     * @param cx x-coordinate of center
     * @param cy y-coordinate of center
     * @param cz z-coordinate of center
     */
    public void setCenter(double cx, double cy, double cz)
    {   center = new Vector3D(cx, cy, cz);
        centerSet = true;
    }    
    /**
     * translate this Object3D in world space over minus center where center is set or calculated;
     * the new center of the object will be (0,0,0); use in construction of specific 3d-objects 
     * redefined for subclass ObjectGroup3D 
     */
    public void center()
    {   for (int i = 0; i < numVertices; i++)
            Vector3D.translateBy(vertices[i], - center.x, - center.y, - center.z);
        center = new Vector3D(); // (0,0,0)
        for (int j = 0; j < numFacets; j++)
            facets[j].updatePoints(vertices);
        
    }
    /**
     * translate this Object3D and its center in world space over the vector (cx,cy,cz);
     * if the center was (0,0,0) the new center is (cx, cy, cz) 
     * redefined for subclass ObjectGroup3D
     * @param cx x-translation
     * @param cy y-translation
     * @param cz x-translation 
     */
    public void translateBy (double cx, double cy, double cz)
    {   Vector3D.translateBy(center, cx, cy, cz);
        for (int i = 0; i < numVertices; i++)
            Vector3D.translateBy(vertices[i], cx, cy, cz);
        for (int j = 0; j < numFacets; j++)
            facets[j].updatePoints(vertices);
    }

    /**
     * translate this Object3D in world space so that (cx,cy,cz) will be its new center,
     * that is, translate over the vector (cx,cy,cz) minus center;
     * redefined for subclass ObjectGroup3D
     * @param cx x-coordinate new center 
     * @param cy y-coordinate new center
     * @param cz x-coordinate new center 
     */    
    public void centerAt (double cx, double cy, double cz)
    {   // vertices FIRST!!
        for (int i = 0; i < numVertices; i++)
            Vector3D.translateBy(vertices[i], cx - center.x, cy - center.y, cz - center.z);
        Vector3D.translateBy(center, cx - center.x, cy - center.y, cz - center.z);            
        for (int j = 0; j < numFacets; j++)
            facets[j].updatePoints(vertices);
    }
    /**
     * redefined in the subclass ObjectGroup3D
     */
    public void fixFacetArray()
    {}    
    /**
     * find the topmost Objectgroup3D in the object tree
     * no need to redefine in the subclass ObjectGroup3D
     * @return the topmost Objectgroup3D (always a group)
     */
    public Object3D topParent()
    {   if (parent == null)
            return this;
        else // recurse
            return parent.topParent();
    }    
    /**
     * redefined in the subclass ObjectGroup3D
     * @return this Object3D
     */
    public Object3D leftChild()
    {   return this;
    }    
    /**
     * redefined in the subclass ObjectGroup3D
     * @return this Obejct3D
     */
    public Object3D leftMostLeaf()
    {   return this;
    }    
    

    /**
     * paint this Object(Group)3D
     * @param g Context2d for painting
     * @param shadow use shadow colors
     * @param inside show the inside of the facets in gray (with or without shadow)
     * @param dis viewing distance
     * @param mat matrix transforming world space to view space
     * @param paintType paintType: <br>
     * NZMINFIRST<br> use for filled objects with line extensions, in this case inner planes and inner
     * segments can be skipped (sort is by zvalue)<br>
     * PUREZ<br> draw all facets (sort is by zvalue)<br>
     * NONZMIN <br> use for filled objects without line extensions, in this case inner planes, inner 
     * segments and backwards pointing facets can be skipped (object is convex)(sort is by zvalue)<br>
     * HYBRID1 <br> use when the object is not filled and planesFilled = false; in this case draw in three passes:
     * backwards pointing facets, inner segments, forward pointing facets (sort is by zvalue)<br>
     * HYBRID2 <br> locally corrected HYBBRID1 when planesFilled = true: in this case draw in three passes:
     * backwards pointing facets, inner planes and inner segments, forward pointing facets (sort is by zvalue)<br>
     * SEMIEXACT <br> recalculate zValues of inner planes and inner segments using Aad Goddijn's algorithm, 
     * backwards and foreward pointing facets stay sorted by zValue <br> 
     * EXACT <br> recalculate all zValues using Aad Goddijn's algorithm, do not use, is very slow<br>
     * @param retransform transform world object to view space
     */
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
            
        // use when the object is filled and there are lengthened lines
        // NB all planes and inner segments are invisible!
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

        }
        else if (paintType == Object3DContainer.PUREZ)
        {
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible)
                {   facets[i].paintFacet3D(g, shadow, inside, dis, mat, null);
                } // if (facets[i].visible)
            } // for facets
            
        }
        // use when the object is filled without lengthened lines
        // for lengthened lines use  NZMINFIRST
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
            
        }
        
        // use when the object is not filled
        else if (paintType == Object3DContainer.HYBRID1)
        {   
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
        }

        // HYBRID2 is HYBRID1 locally corrected for planesFilled
        // filled facets are now inner planes (filled)
        else if (paintType == Object3DContainer.HYBRID2)
        {   
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
            // this includes line extensions 
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
            
        } // HYBRID2 locally corrected        
       
        // recalculate zValues for inner planes, segments and line extension 
        else if (paintType == Object3DContainer.SEMIEXACT)
        {   

            int[] tRecalcFacets = new int[numFacets];
            int recalcNum = 0; 
            
            // find maximum zValue of non-filled back-facing facets which are 
            // not inner segments, note that any segment is 
            // never filled
            double maxBackZ = - Vector3D.NInf;            
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
                {                   
                    maxBackZ = Math.max(maxBackZ, facets[i].zValue);
                } // if (facets[i].visible) etc.
            } // for back facing facets

            // zValues of all filled facets and inner segments, must be recalculated
            for (int i = 0; i < numFacets; i++)
            {   if (facets[i].visible 
                    && 
                    (facets[i].filled || 
                     ((facets[i].numPoints == 2) && 
                      facets[i].unitNormal.equals(new Vector3D())
                     )
                    )  
                   )
                {   
                    tRecalcFacets[recalcNum] = i;
                    recalcNum++;    
                } // if (facets[i].visible) etc.
            } // for middle facets

            // find minimum zValue for all non-filled front facing facets, which are not segments
            // and the front facing line extensions
            double minFrontZ = Vector3D.NInf;
            
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
                {   
                    minFrontZ = Math.min(minFrontZ, facets[i].zValue);
                } // if (facets[i].visible) etc.
            } // for front facets
            
            double correction = minFrontZ - maxBackZ - 3;
            double minMiddleZ = maxBackZ + 1;
            double maxMiddleZ = minFrontZ - correction - 1;
            
            if (correction < 0)
            {   // backward unchanged
                // shift foreward over -correction
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
                    
                    } // if (facets[i].visible) etc.
                } // for
                
                // middle part gets zValues between
                // maxBackZ+1 and minFrontZ-correction-1            
                minMiddleZ = maxBackZ + 1;
                maxMiddleZ = minFrontZ - correction - 1;

            }    
            else
            {
                // middle part gets zValues between
                // maxBackZ+1 and minFrontZ-1            
                minMiddleZ = maxBackZ + 1;
                maxMiddleZ = minFrontZ - 1;
                
            }    

            int[] recalcFacets = new int[recalcNum];
            System.arraycopy(tRecalcFacets, 0, recalcFacets, 0, recalcNum);
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

        // experimental algorithm from Aad Goddijn    
        // recalculate z-values for all visible facets
        // compare semi-exact
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
               
            int[] recalcFacets = new int[recalcNum];
            System.arraycopy(tRecalcFacets, 0, recalcFacets, 0, recalcNum);
            // these are symbolic
            double zMin = 1d;
            double zMax = (double) numFacets;

            // this is very slow
            if (recalcNum > 1)
            	recalcZValues(recalcFacets, dis, mat.origin, zMin, zMax);
            
            zMergeSort();
            // now paint
            for (int j = 0; j < numFacets; j++)
            {   if (facets[j].visible) 
                {   facets[j].paintFacet3D(g, shadow, inside, dis, mat, null);
                } // if (facets[i].visible) etc.
            } // for facets
        } // EXACT
        
    } // paint
    /**
     * check if the transformed Facet3D in view space is "visible": <br>
     * the transformed Facet3D in view space is "visible" from the view point (origin.x, origin.y, d) if the point 
	 * (origin.x, origin.y, d) is at the same side of the transformed Facet3D as the point of the
	 * transformed normal (thus "visible" means that from the view point one sees the outside of the facet); 
	 * the transformed Facet3D is of vector form <br> 
	 * f.trPoints[0] + "some plane through (0,0,0)" and that plane through (0,0,0) has normal vector
	 * f.unitNormal, so "visible" if the angle between (origin.x, origin.y, d) minus f.trPoints[0] and f.unitNormal
	 * is smaller then or equals 90 degrees
     * @param f the Facet3D f
     * @param dis the viewing distance in view space
     * @param origin the origin in view space
     * @return true/false
     */
    public boolean visFromD(Facet3D f, double dis, Vector3D origin)
    {   Vector3D eye = new Vector3D(origin.x, origin.y, dis);
        // work with normal support here
        Vector3D support = new Vector3D(f.trPoints[0]);
        eye = Vector3D.minus(eye, support);
        Vector3D.makeUnitary(eye);
        return Vector3D.dotProduct(eye, f.unitNormal) >= -Vector3D.NZero;
    }

    /**
     * recalculate the zValues of the facets in recalcFacets
     * using Aad's algorithm 
     * @param recalcFacets facets whose zValues should be recalculated
     * @param dis viewing distance
     * @param origin origin of drawing plane
     * @param zMin new zValue should not be smaller than zMin
     * @param zMax new zValue should not be larger than zMax
     */
    public void recalcZValues(int[] recalcFacets, double dis,
        Vector3D origin, double zMin, double zMax)
    {   
        int recalcNum = recalcFacets.length;
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
        // any 2 facets with A > B and B < A or A < B and B > A
        // must be separated, so put them to 0
        // this results in a symmetric matrix
        im.relax();
        // use additional separation info
        // note that im is symmetric
        for (int row = 0; row < recalcNum; row++)
            for (int col = row + 1; col < recalcNum; col++)
            {   // find non-zero elements
                if (im.matrix[row][col] != 0)
                {   // check for separation
                    if (projectionsSeparated(recalcFacets[row], recalcFacets[col], dis, origin))
                    {
                        im.matrix[row][col] = 0;
                        im.matrix[col][row] = 0;

                    }    
                }    
            }
        // try to find on order on the rows of the incidence matrix
        int[] rowNumsIn = new int[recalcNum];
        for (int i = 0; i < recalcNum; i++)
            rowNumsIn[i] = i;
        int[] newOrder = findOrder(im, rowNumsIn);    
        // order found
        if (newOrder != null)
        {
            // now find the new zValues
            double zStep = (zMax - zMin) / (recalcNum - 1); // was checked for dividing by 0!
            double currentZ = zMax;
            for (int i = recalcNum - 1; i >=0 ; i--)
            {   facets[recalcFacets[newOrder[i]]].zValue = currentZ;
                currentZ -= zStep;
            }
        } // newOrder != null
//      else // this happens if no order is consistent!
//          System.out.println("no consistent final order");
    }
    
    /**
     * recursively find an order on the row numbers of the matrix 
     * @param im this incidence matrix
     * @param rowNumbers row numbers still in the game
     * @return the order or null
     */
    public int[] findOrder(IncidenceMatrix im, int[] rowNumbers)
    {   // find how many rows still in the game
        int numRows = rowNumbers.length;
        // last row: finished
        if (numRows == 1)
            return rowNumbers;
        // find rows containing no -1's (top rows)  
        im.findMaximalElements(rowNumbers, 0);
        // if no top rows, find rows containing a minimum number of -1's (max rows)
        if (im.maxCnt == 0)
        {    im.findMaximalElements(rowNumbers, 1);
        }
        // make hard copies!
        int maxCnt = im.maxCnt;
        int[] maxRows = new int[maxCnt];
        System.arraycopy(im.maxRows, 0, maxRows, 0, maxCnt);
        // maxRows contains a list of positions in rowNumbers
        // where top or max rows are found
        // the INDEX of the j-th top/max row 
        // is rowNumbers[maxRows[j]]
        // not possible anymore
        if (maxCnt == 0)
        {
            return null;
        }    
        for (int j = 0; j < maxCnt; j++)
        {   
            int[] tRowNumbers = new int[numRows];
            System.arraycopy(rowNumbers, 0, tRowNumbers, 0, numRows);
            // the row with index in the last position is not a top or max row
            // so put the top/max row as last by swapping
            if (maxRows[j] != (numRows - 1))
            {   int temp = tRowNumbers[maxRows[j]];
                tRowNumbers[maxRows[j]] = tRowNumbers[numRows - 1];
                tRowNumbers[numRows - 1] = temp;
            }    
            // now copy all but last of tRowNumbers
            // and find an order on these
            int[] newRowNumbers = new int[numRows - 1];
            System.arraycopy(tRowNumbers, 0, newRowNumbers, 0, numRows - 1);
            int[] result = findOrder(im, newRowNumbers);
            if (result != null)
            {   // if an order was found, copy it into tRowNumbers
                System.arraycopy(result, 0, tRowNumbers, 0, numRows - 1);
                // check for a consistent order here 
                if (numRows == im.size)
                {   
                    boolean consistent = isConsistent(im, tRowNumbers);
                    if (!consistent)
                    {   // continue
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

    /**
     * experimental algorithm from Aad Goddijn to determine if in view space the
     * transformed facet fA with index "index" is "after" the transformed facet fB with
     * index "cnt"; "after": check if the segment(!) from the eye (origin.x,origin.y,dis)
     * to the barycenter of fA intersects fB, that is, fB is "on top" fA    
     * @param index index of facet fA 
     * @param cnt index of facet fB
     * @param dis the viewing distance
     * @param origin the origin of view space
     * @return +1 (on top), 0 (undecided) or -1 (not on top)
     */
    private int isOnTop(int index, int cnt, double dis, Vector3D origin)
    {   int result = 0;
        // get the transformed facets
        Facet3D fA = facets[index];
        Facet3D fB = facets[cnt];
        // two planes
        if ((fA.numPoints > 2) && (fB.numPoints > 2))
        {   // Aad's A after B (i.e. fB on top of fA)
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
                if (fA.zValue < fB.zValue)
                    result = 1;
                else
                    result = -1;
            }
            // line through eye O and barycenter P of fA 
            // intersects the plane through fB in one point Q
            // note that P = Q is impossible since 
            // P is an inside point and all is cut up
            else if (isType == 1)
            {   Vector3D pointQ = Plane3D.getIntersectionPoint(lineOP, planeB);
                Line3D lineOQ = new Line3D(pointO, pointQ);
                if (lineOQ.segmentContains(pointP))
                    result = 1;
                else    
                    result = -1;
            }    
            // line through eye and barycenter of fA 
            // is in the the plane through fB
            // but then eye is in the plane through fB
            // thus we are looking at fB from the side
            // AND (all cut up) fB is completely behind
            // fA or completely before fA
            else if (isType == 2)
            {   if (fA.zValue < fB.zValue)
                    result = 1;
                else
                    result = -1;
            }    
        }
        // segment and plane
        else if ((fA.numPoints == 2) && (fB.numPoints > 2))
        {   // Aad's A after B (i.e. f1 on top of f2)
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
                if (fA.zValue < fB.zValue)
                    result = 1;
                else
                    result = -1;
            }
            // line through eye and barycenter of fA 
            // intersects the plane through fB in one point
            else if (isType == 1)
            {   Vector3D pointQ = Plane3D.getIntersectionPoint(lineOP, planeB);
                Line3D lineOQ = new Line3D(pointO, pointQ);
                if (lineOQ.segmentContains(pointP))
                    result = 1; 
                else
                    result = -1;
            }    
            // line through eye and barycenter of fA 
            // is in the the plane through fB
            // but then eye is in the plane through fB
            // thus we are looking at fB from the side
            // AND (all cut up) fB is completely behind
            // fA of completely before fA
            else if (isType == 2)
            {   if (fA.zValue < fB.zValue)
                    result = 1;
                else
                    result = -1;
            }    
        }
        // plane and segment
        else if ((fA.numPoints > 2) && (fB.numPoints == 2))
        {    result = - isOnTop(cnt, index, dis, origin);
        }
        // 2 segments
        else if ((fA.numPoints == 2) && (fB.numPoints == 2))
        {   
            Vector3D pointO = new Vector3D(origin.x, origin.y, dis);
            // not possible?
            if (fA.trPoints[0].equals(fA.trPoints[1]))
            {   if (fA.zValue < fB.zValue)
                    result = 1;
                else
                    result = -1;
                return result;
            }
            Line3D tLine = new Line3D(fA.trPoints[0], fA.trPoints[1]);
            // we are seeing only a point of fA
            if (tLine.contains(pointO))
            {   if (fA.zValue < fB.zValue)
                    result = 1;
                else
                    result = -1;
                return result;
            }
            // not possible
            if (fB.trPoints[0].equals(fB.trPoints[1]))
            {   if (fA.zValue < fB.zValue)
                    result = 1;
                else
                    result = -1;
                return result;
            }    
            Line3D lineB = new Line3D(fB.trPoints[0], fB.trPoints[1]);    
            Plane3D planeOA = new Plane3D(pointO, fA.trPoints[0], fA.trPoints[1]);
            int isType = Plane3D.intersectionType(lineB, planeOA);                
            // lineB parallel to planeOA
            // sowieso separated            
            if (isType == 0)
            {   // try this
                // zValue sufficient, segments are cut up?
                if (fA.zValue < fB.zValue)
                    result = 1;
                else
                    result = -1;
                return result;
            }
            // lineB cuts planeOA in a point
            else if (isType == 1)
            {    Vector3D pointQ = Plane3D.getIntersectionPoint(lineB, planeOA);       
                boolean triangleOUVcontainsQ = 
                    Plane3D.triangleContainsPoint(pointO, 
                        fA.trPoints[0], fA.trPoints[1], pointQ);
                if (!triangleOUVcontainsQ)
                    result = 1;
                else    
                    result = -1;
            }
            // lineB is in planeOA
            else if (isType == 2)
            {   // fA en fB on the same line
                if (lineB.contains(fA.trPoints[0]) &&
                    lineB.contains(fA.trPoints[1]))
                    return 0;    
                else  // fA cannot intersect fB (all cut up)
                {   if (fA.zValue < fB.zValue)
                       result = 1;
                    else
                        result = -1;
                    return result;
                }    
            }    
        }
        return result;
    }    
    /**
     * in view space project the transformed facet with index indexA and the transformed facet with
     * indexB from (origin.x,origin.y,dis) on the plane z = 0 and check if the polygonal projections
     * are separated (i.e. they do not intersect)
     * @param indexA first transformed facet
     * @param indexB second transformed facet
     * @param dis viewing distance
     * @param o origin of view space
     * @return true/false
     */
    public boolean projectionsSeparated(int indexA, int indexB,
                                        double dis, Vector3D o)
    {   Facet3D fA = facets[indexA];
        Facet3D fB = facets[indexB];
        Polygon2D pA = fA.project2D(dis, o);
        Polygon2D pB = fB.project2D(dis, o);
        return pA.isSeparatedFrom(pB, true);            
    }
    /**
     * transform all vertices to view space using the matrix m
     * leave world space vertices unchanged
     * if zZort == true, sort the facet array by zValue
     * taking m == null only sorts the facets if zZort == true 
     * redefined for subclass ObjectGroup3D
     * @param m transformation matrix
     * @param dis viewing distance in view space
     * @param zSort should the facet array be sorted (by zValue)? 
     */
    public void transformBy(Matrix3D m, double dis, boolean zSort)
    {   int lNumVertices = numVertices;
    	Vector3D[] lVertices = vertices;
    	Vector3D[] lTrVertices = trVertices;
    	int lNumFacets = numFacets;
    	Facet3D[] lFacets = facets;
    	Object3D topParent = topParent();
        if (m != null)
        {   for (int i = lNumVertices - 1; i >= 0; i--)
            {   // center the object(group) in world space here!!
                lTrVertices[i] = m.transform(
                    Vector3D.minus(lVertices[i], topParent.center));
            }
        	for (int i = lNumFacets - 1; i >= 0; i--)
            {   // notify facet[i] to reference the correct transformed 3space-points
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
    /**
     *  merge sort ALL facets by zValue, speed O(n log_2 n)
     */
    public void zMergeSort()
    {   facets = mergeSort(facets);
    }    
    /**
     * standard merge sort algorithm, recursive method 
     * @param list an array of Facet3D to be sorted
     * @return an array containing the same Facet3D, now sorted by zValue 
     */
    public Facet3D[] mergeSort(Facet3D[] list)
    {   if (list == null)
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
    /**
     * mergesort two arrays of Facet3D by zValue   
     * @param list1 first array
     * @param list2 second array
     * @return the merged array
     */
    public Facet3D[] merge(Facet3D[] list1, Facet3D[] list2)
    { 	int list1Length = list1.length;
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
    /**
     * set facets[i] visible, no error check for i between zero and numFacets;
     * works also for objectgroups after calling fixFacetArray()
     * @param i index of facet to be set visible/invisible
     * @param visible true/false
     */
    public void setVisible(int i, boolean visible)
    {   facets[i].visible = visible;
    }
    /**
     * set this object visible/invisible by setting its facets visible/invisible
     * redefined for subclass ObjectGroup3D
     * @param vis true/false
     */
    public void setVisible(boolean vis)
    {   visible = vis;
        for (int i = 0; i < numFacets; i++)
            setVisible(i, vis);
    }
    /**
     * check if SOME facets of this Object(Group)3D are visible
     * in this case the Object(Group)3D itself is visible
     * but one or more or all of the facets have been hidden
     * to speed up drawing
     * @return true/false
     */
    public boolean someFacetsVisible()
    {   boolean result = false;
        for (int i = 0; i < numFacets; i++)
        {   if (facets[i].visible)
                return true;
        }
        return result;
    }    
    /**
     * if empty = true, set facets[i] to empty.
     * if empty = false restore the state of facets[i]
     * to the state of this Object(Group)3D
     * note: empty is not visible, but still clickable,
     * visible == false is not visible and not clickable 
     * @param i index of facet
     * @param empty true/false
     */
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
    /**
     * set all facets of this Object3D to outlined/not outlined; omit the outline only when the
     * facet is filled; redefined for subclass ObjectGroup3D
     * @param outline true/false
     */
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
                {   // 2-dim facets are never filled, correct here
                    if (facets[i].numPoints > 2)
                        facets[i].outlined = false;
                }
            }
        }
    }
    
    /**
     * set the outline color of all facets 
     * also works for groups
     * @param olc the outline colro
     */
    public void setOutlineColor(CssColor olc)
    {	for (int i = 0; i < numFacets; i++)
    	{	facets[i].outlineColor = olc;
    		facets[i].edgeColors[0] = olc;
    	}
    	
    }

    /**
     * set the fill color of all facets,
     * also works for groups
     * @param flc fill color
     */
    public void setFillColor(CssColor flc)
    {	for (int i = 0; i < numFacets; i++)
    	{	facets[i].color = flc;
    	}
    	
    }
    /**
     * set all facets of this Object3D to filled/not filled; omit filling only when the
     * facet is outlined; redefined for subclass ObjectGroup3D
     * @param fill true/false
     */
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
    
    /**
     * reset all empty facets, see method setEmpty
     * @param o this parameter seems redundant
     */
    public void reSetEmpty(boolean o)
    {   for (int i = 0; i < numFacets; i++)
        {   setEmpty(i, false);
        }
    }
    /**
     * check if this Object3D contains facet f
     * redefined for subclass ObjectGroup3D
     * @param f the facet to be checked
     * @return true/false
     */
    public Object3D objectContains(Facet3D f)
    {   Object3D result = null;
        if (containsFacet(f) >= 0)
        {   result = this;
            return result;
        }
        return result;
    }
    
    /**
     * set the facet, whose projection was clicked in the view plane,
     * to empty; works also for groups 
     * @param x x-coordinate
     * @param y y-coordinate
     * @param dis viewing distance
     * @param origin origin of drawing plane
     * @param paintType type of painting used
     */
    public void setEmpty(int x, int y, double dis, Vector3D origin, int paintType)
    {   int index = facetClicked(x, y, dis, origin, paintType);
        setEmpty(index, true);
    }
    
    /**
     * find the Oject3D that, after projection in the view plane,
     * contains the coordinated (x,y); also works for groups, 
     * since objectContains was redefined for ObjectGroups3D;'
     * use only after calling method fixFacetArray (see class ObjectGroup3D),
     * since this is necessary for correctly using method facetClicked 
     * @param x x-coordinate
     * @param y y-coordinate
     * @param dis viewing distance
     * @param origin origin of drawing plane
     * @param paintType type of painting used
     * @return the Object3D or null
     */
    public Object3D objectClicked(int x, int y, double dis, Vector3D origin, 
                                  int paintType)
    {   Object3D result = null;
        int index = facetClicked(x, y, dis, origin, paintType);
        if (index >= 0)        
            return objectContains(facets[index]);
        return result;    
    }

    /**
     * find the index of the facet that, after projection in the view plane,
     * contains the coordinated (x,y); also works for groups, but use only 
     * after calling method fixFacetArray (see class ObjectGroup3D);
     * use method objectClicked to find the Object3D the facet belongs to  
     * @param x x-coordinate
     * @param y y-coordinate
     * @param dis viewing distance
     * @param origin origin of drawing plane
     * @param paintType type of painting used
     * @return index of facet or -1
     */
    public int facetClicked(int x, int y, double dis, Vector3D origin, 
                            int paintType)
    {   Polygon p;
        int index = -1;
        // start on top, facets are already sorted by zValue(!)
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
        // start on top, facets are already sorted, 
        // make sure the facets are sorted in the order they are drawn
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
    /**
     * find the facet that, after projection in the view plane,
     * contains the coordinated (x,y); also works for groups, but use only 
     * after calling method fixFacetArray (see class ObjectGroup3D);
     * use method objectClicked to find the Object3D the facet belongs to  
     * @param x x-coordinate
     * @param y y-coordinate
     * @param dis viewing distance
     * @param origin origin of drawing plane
     * @param paintType type of painting used
     * @return the Facet3D or null
     */
    public Facet3D clickedFacet(int x, int y, double dis, Vector3D origin, 
                                int paintType)    
    {   Facet3D result = null;
        int index = facetClicked(x, y, dis, origin, paintType);
        if (index >= 0)
            result = facets[index];
        return result;
    }
    
    /**
     * given a point clicked in the drawing plane, check if this point is 
     * the projection of some vertex and return the facet to which the vertex
     * belongs and the vertex in 3-space or null; see class FacetWithVertex;
     * make sure the facet and its vertex are "topmost" if facets are
     * filled  
     * @param x x-coordinate of point clicked in drawing plane
     * @param y y-coordinate of point clicked in drawing plane
     * @param dis viewing distance
     * @param origin origin of view space
     * @return Facet and vertex clicked or null
     */
    public FacetWithVertex facetWithVertexClicked(int x, int y, double dis, 
            Vector3D origin)
    {   FacetWithVertex result = null;
        Polygon p;
        // point clicked in drawing plane        
        Point pClicked = new Point(x,y);        
        // start on top, facets are already sorted, 
        // make sure the facets are sorted in the order they are drawn
        for (int i = numFacets - 1; i >= 0; i--)
        {   
            if (facets[i].visible) 
            {   p = facets[i].project(dis, origin);
                for (int cnt = 0; cnt < facets[i].numPoints; cnt++)
                {   // projected vertex
                    Point projVertex = 
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

    /**
     * distance between two integer points
     * @param p1 first point
     * @param p2 second point
     * @return distance
     */
    public double distance(Point p1, Point p2)
    {   return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) +
                         (p1.y - p2.y) * (p1.y - p2.y));   
    }    
    /**
     * given a vertex belonging to the facet with index fIndex,
     * check if another projected facet covers the projected
     * vertex (assuming all facets are filled);
     * assume facets are sorted in the order they are drawn,
     * so necessarily, any projected facet covering the
     * projected vertex has index larger than fIndex  
     * @param dis viewing distance
     * @param origin origin of view space
     * @param fIndex index of facet containing projVertex
     * @param projVertex vertex to be checked
     * @return true/false
     */
    public boolean vertexCovered(double dis, Vector3D origin,
                   int fIndex, Point projVertex)
    {   boolean result = false;
        Polygon p;
        // start on top, facets are already sorted, 
        // make sure the facets are sorted in the order they are drawn
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

    /**
     * given a point clicked in the drawing plane, check if this point is on
     * a projected edge from some facet and return the edge with clicked point
     * in 3-space or null; make sure the edge is the "topmost" edge and the 
     * clicked point is visible if facets are filled   
     * @param x x-coordinate of point clicked in drawing plane
     * @param y y-coordinate of point clicked in drawing plane
     * @param dis viewing distance
     * @param origin origin of view space
     * @return starting point of edge clicked, end point of edge clicked and the
     * actual point on the edge which was clicked (see class EWP) or null
     */
    public Vector3D[] edgeClicked(int x, int y, double dis, Vector3D origin)
    {   Vector3D[] edgeWithPoint = null;
        Polygon p;
        // point clicked in drawing plane
        Point pClicked = new Point(x,y);        
        // start on top, facets are already sorted but make sure
        // the facet array is sorted in the order the facets are drawn
        for (int i = numFacets - 1; i >= 0; i--)
        {   // includeInvisibles determines which vertices can be clicked
            if (facets[i].visible)
            {   p = facets[i].project(dis, origin);
                for (int cnt = 0; cnt < facets[i].numPoints; cnt++)
                {   // find projected edge
                    Point projVertexFrom = new Point(p.puntenX[cnt], p.puntenY[cnt]);
                    Point projVertexTo = 
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
                            Vector3D isPoint = Line3D.getIntersectionPoint(line1, line2);
                            double lambda1 = 
                                Vector3D.distance(isPoint, 
                                    facets[i].trPoints[(cnt + 1) % facets[i].numPoints]) /
                                Vector3D.distance(facets[i].trPoints[cnt], 
                                    facets[i].trPoints[(cnt + 1) % facets[i].numPoints]);
                            // in view space y-axis is reversed!        
                            lambda1 = 1 - lambda1;                                    
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

    /**
     * given a point clicked in the drawing plane, check if this point is on
     * a projected edge from some facet and return the facet together with the 
     * clicked point in 3-space or null, see class FacetWithEdgePoint 
     * make sure the edge is the "topmost" edge and the clicked point is visible 
     * if facets are filled; if the edge has tick marks, only the projectons of 
     * the tick marks can be clicked   
     * @param x x-coordinate of point clicked in drawing plane
     * @param y y-coordinate of point clicked in drawing plane
     * @param dis viewing distance
     * @param origin origin of view space
     * @return facet and the point of the edge of this facet that was clicked or null
     */
    public FacetWithEdgePoint facetWithEdgePointClicked(int x, int y, double dis, Vector3D origin)
    {   FacetWithEdgePoint result = null;
        Polygon p;
        Polygon2D p2;
        // point clicked in drawing plane
        Point pClicked = new Point(x,y);        
        Vector2D p2Clicked = new Vector2D((double) x, (double) y);
        // start on top, facets are already sorted but make sure
        // the facet array is sorted in the order the facets are drawn
        for (int i = numFacets - 1; i >= 0; i--)
        {   
            if (facets[i].visible)
            {   // get the projected polygon
                p = facets[i].project(dis, origin);
                p2 = facets[i].project2D(dis, origin);
                for (int cnt = 0; cnt < facets[i].numPoints; cnt++)
                {   
                    // find projected edge in integer plane
                    Point projVertexFrom = 
                    	new Point(p.puntenX[cnt], p.puntenY[cnt]);
                    Point projVertexTo = 
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
                        double projIntersectX = (1 - lambda) * projVertexFrom2.x + lambda * projVertexTo2.x;
                        // real(!) y-coordinate of projection    
                        double projIntersectY = (1 - lambda) * projVertexFrom2.y + lambda * projVertexTo2.y;
                        // distance clicked point - projection of ibidem (in real plane!)   
                        double distance = Math.sqrt(    
                            (p2Clicked.x - projIntersectX) * (p2Clicked.x - projIntersectX) + 
                            (p2Clicked.y - projIntersectY) * (p2Clicked.y - projIntersectY));
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
                            // check for tickmarks
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
                                    if (tDis <= Object3DContainer.SSTT)
                                        tIndex = chCnt;
                                }
                                // tickmark found
                                if (tIndex >= 0)
                                {   
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
                                // 1)
                                // embed the real version of the clicked point in the z-plane in view space
                                Vector3D realClicked = new Vector3D(projIntersectX, projIntersectY, 0);
                                // 2)
                                // now the line through (origin.x, origin,y, dis) and
                                // realClicked should intersect the transformed edge
                                // this corrects for the central projection
                                Line3D line1 = new Line3D(
                                    new Vector3D(origin.x, origin.y, dis), realClicked);
                                Line3D line2 = new Line3D(facets[i].trPoints[cnt],
                                    facets[i].trPoints[(cnt + 1) % facets[i].numPoints]);
                                Vector3D isPoint = Line3D.getIntersectionPoint(line1, line2);
                                // get the position of isPoint
                                lambda1 = 
                                    Vector3D.distance(isPoint, 
                                        facets[i].trPoints[(cnt + 1) % facets[i].numPoints]) /
                                    Vector3D.distance(facets[i].trPoints[cnt], 
                                        facets[i].trPoints[(cnt + 1) % facets[i].numPoints]);
                            } // if !tickFound
                            // 3)
                            // now find the untransformed version of the clicked point
                            // in view space y-axis is reversed!        
                            lambda1 = 1 - lambda1;
                            // (1-lambda)*"from"+lambda*"to"
                            Vector3D from = new Vector3D(edgeWithPoint[0]); // new!!
                            Vector3D.scaleBy(from, 1 - lambda1);
                            Vector3D to = new Vector3D(edgeWithPoint[1]); // new!! 
                            Vector3D.scaleBy(to, lambda1);
                            edgeWithPoint[2] = Vector3D.plus(from, to);
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

    /**
     * given a segment with integer end points and a point with integer coordinates in the plane, 
     * find the projection of the point on the line through the segment in the form
     * (1-lambda)from+(lambda)to; note that the projection is on the segment if lambda between 0 and 1 
     * assume from != to
     * @param from starting point of segment
     * @param to end point of segment
     * @param pClicked the integer point in the plane
     * @return lambda
     */
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

    /**
     * given a segment with real end points and a point with real coordinates in the plane, 
     * find the projection of the point on the line through the segment in the form
     * (1-lambda)from+(lambda)to; note that the projection is on the segment if lambda between 0 and 1
     * assume from != to
     * @param from starting point of segment
     * @param to end point of segment
     * @param pClicked the real point in the plane
     * @return lambda
     */
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
    
    /**
     * make all vertices redundant, that is, make sure that facets sharing 
     * a vertex never point to the same instance of this vertex;
     * do this by adding a copy of this shared vertex to this object3D 
     * used for fold-outs
     */
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


    
