package fi.grafiek3dgwt.client;

import java.util.Vector;

/**
 * A flexible class for grouping 3d-objects: each ObjectGroup3D contains a Vector containing instances of Object3D (each of which can
 * be an ObjectGroup3D); note that for an ObjectGroup3D (which is also an Object3D), the vertex and facet arrays are NOT 
 * initialized; to access all vertices of all 3d-objects in the ObjectGroup3D, use method enumerateVertices(); the method
 * fixFacetArray() initializes the facet array by concatenating recursively the facet arrays of all 3d-objects in the ObjectGroup3D;
 * in Doorzien this is necessary for drawing the Objectgroup3D, since before drawing, all facets must be sorted at the same time;
 * in general, in most methods the individual 3d-objects in the ObjectGroup3D are accessed recursively.         
 * @author huub
 */
public class ObjectGroup3D extends Object3D
{   /**
	 * the 3d-objects in this ObjectGroup3D (each of which can be an ObjectGroup3D)
	 */
    Vector<Object3D> objects = new Vector<Object3D>();
    /**
     * Doorzien: should the facet arrays of the objects in this ObjectGroup3D be sorted when transforming the 
     * vertices to view space, see method transformBy  
     */
    boolean sortSubArrays = true;
    /**
     * update the points of all facets of all objects in this ObjectGroups 3D after changing the vertices of some or all objects; <br>
     * redefined from class Object3D 
     */
    public void updatePoints()
    {   for (int oCnt = 0; oCnt < objects.size(); oCnt++)
        {   Object3D ob = (Object3D) objects.elementAt(oCnt);
            ob.updatePoints();
        }
    }
    /**
     * create a Vector containing references to the vertex arrays of all 3d-objects in this 
     * ObjectGroup3D; redefined from class Object3D   
     */
    public Vector<Vector3D[]> enumerateVertices()
    {   Vector<Vector3D[]> enumer = new Vector<Vector3D[]>();
        for (int oCnt = 0; oCnt < objects.size(); oCnt++)
        {   Object3D ob = objects.elementAt(oCnt);
            Vector<Vector3D[]> obEnum = ob.enumerateVertices();
            for (int eCnt = 0; eCnt < obEnum.size(); eCnt++)
            {   Vector3D[] o = obEnum.elementAt(eCnt);
                enumer.addElement(o);
            }
        }        
        return enumer;
    }
    /**
     * Doorzien: find the Object3D or ObjectGroup3D at index 0;
     * redefined from class Object3D 
     */
    public Object3D leftChild()
    {   return (Object3D) objects.elementAt(0);
    }    
    /**
     * Doorzien: find recursively the Object3D at index 0;
     * redefined from class Object3D 
     */
    public Object3D leftMostLeaf()
    {   return ((Object3D) objects.elementAt(0)).leftMostLeaf();
    }    
    /**
     * set the attribute diameter of this ObjectGroup3D as the maximum over all object(groups) of 
     * 2 times distance(center,object.center) plus object.diameter;   
     * centers of all object(group)s must have been set or calculated;
     * do nothing if diamSet == true; redefined from superclass Object3D
     */
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
    /**
     * calculate the diameter of this ObjectGroup3D as the maximum over all object(groups) of 
     * 2 times distance(center,object.center) plus object.diameter;   
     * centers of all object(group)s must have been set or calculated;
     * do not change the attribute diameter; redefined from superclass Object3D
     */
    public double getDiameter()
    {   // assume the whole object tree is centered
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
    /**
     * default constructor for creating an empty ObjectGroup3D, then 
     * (repeatedly) use addObject3D() and finally initObject3D() 
     */
    public ObjectGroup3D()
    {
    }
    /**
     * constructor for turning a single Object3D into an ObjectGroup3D 
     * @param ob the Object3D to be turned into an ObjectGroup3D
     * @param centerObject should the ObjectGroup3D be centered?
     */
    public ObjectGroup3D(Object3D ob, boolean centerObject)
    {   addObject3D(ob);
        filled = ob.filled;
        initObject3D(true, centerObject);
        fixFacetArray();
    }
    /**
     * find the text associated with vertex v
     * redefined from class Object3D
     * @param vertex vertex whose text is required
     * @return the vertex text or null
     */
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
        copy.centerSet = centerSet;
        copy.diamSet = diamSet;
        copy.diameter = diameter;
        copy.center = new Vector3D(center);
        // attributes of the group as top parent objectgroup        
        copy.sortSubArrays = sortSubArrays;
        copy.numVertexLabels = numVertexLabels;
        copy.fixFacetArray(); 
    }    
    /**
     * make a deep copy of this ObjectGroup3D, redefined from superclass Object3D; <br>
     * note that a deep copy of an ObjectGroup3D is returned as an Object3D, so cast if necessary
     */
    public Object3D deepCopy()
    {   ObjectGroup3D copy = new ObjectGroup3D();
        makeDeepGroupCopy(copy);
        return copy;
    }    
    /**
     * set the attribute center of this ObjectGroup3D to the barycenter of the centers of all its objects(groups);
     * centers are found recursively; do nothing if centerSet == true; redefined from superclass Object3D 
     */
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
    /**
     * calculate the center of this ObjectGroup3D as the barycenter of the centers of all its objects(groups);
     * centers are found recursively; do not change the attribute center; redefined from superclass Object3D
     * @return the calculated center
     */
    public Vector3D getCenter()
    {   double cx = 0;
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
    /**
     * translate this ObjectGroup3D in world space over minus center where center is set or must have been calculated;
     * the new center of the object will be (0,0,0);  
     * redefined from superclass Object3D 
     */
    public void center()
    {   for (int i = 0; i < objects.size(); i++)
        {   Object3D ob = (Object3D) objects.elementAt(i);
            ob.translateBy(-center.x, -center.y, -center.z);
        }
        center = new Vector3D(); // (0,0,0)
    }
    /**
     * translate this Object3D and its center in world space over the vector (cx,cy,cz);
     * if the center was (0,0,0) the new center is (cx, cy, cz); note that center must have been set or calculated 
     * redefined from superclass Object3D 
     */
    public void translateBy (double cx, double cy, double cz)
    {   for (int i = 0; i < objects.size(); i++)
        {   Object3D ob = (Object3D) objects.elementAt(i);
            ob.translateBy(cx, cy, cz);
        }        
        Vector3D.translateBy(center, cx, cy, cz);
    }
    /**
     * translate this ObjectGroup3D in world space so that (cx,cy,cz) will be its new center,
     * that is, translate over the vector (cx,cy,cz) minus center; note that center must have been set or calculated
     * redefined from superclass Object3D 
     */    
    public void centerAt (double cx, double cy, double cz)
    {   // objects FIRST       
        for (int i = 0; i < objects.size(); i++)
        {   Object3D ob = (Object3D) objects.elementAt(i);
            ob.translateBy(cx - center.x, cy - center.y, cz - center.z);
        }        
        Vector3D.translateBy(center, cx - center.x, cy - center.y, cz - center.z);
    }
    /**
     * add an Object(Group)3D to this group
     * @param od the Object(Group)3D to be added 
     */
    public void addObject3D(Object3D od)
    {   objects.addElement(od);
        od.parent = this;
    }
    /**
     * remove Object(Group)3D from this objectgroup
     * @param od the Object(Group)3D to be removed 
     */
    public void removeObject3D(Object3D od)
    {   objects.removeElement(od);
        for (int i = 0; i < objects.size(); i++)
        {   Object3D ob = (Object3D) objects.elementAt(i);
            if (ob instanceof ObjectGroup3D)
                ((ObjectGroup3D) ob).removeObject3D(od);
        }    
    }    
    /**
     * recursively concatenate the facet arrays of all objects in this ObjectGroup3D
     * redefined from superclass Object3D 
     */
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
        // now all subgroups also have a facet array, so concatenate
        facets = new Facet3D[numFacets];
        int fCount = 0;
        for (int j = 0; j < objects.size(); j++)
        {   Object3D ob = (Object3D) objects.elementAt(j);
            // fast copy
        	if (ob.numFacets > 0)
        		System.arraycopy(ob.facets, 0, facets, fCount, ob.numFacets);
            fCount += ob.numFacets;                 
        } 
    }    
    /**
     * transform all vertices of all obects in this ObjectGroup3D to view space using the matrix m
     * leave world space vertices unchanged
     * if zZort == true, sort the facet array by zValue, note that in this case the facet array of this ObejctGroup3D
     * must have been initialized by method fixFacetArray()
     * taking m == null only sorts the facet array if zZort == true 
     * redefined from superclass Object3D
     * @param m transformation matrix
     * @param dis viewing distance in view space
     * @param zSort should the facet array be sorted (by zValue)? 
     */
    public void transformBy(Matrix3D m, double dis, boolean zSort)
    {   Object3D ob;
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
    /**
     * set all facets of this ObjectGroup3D) filled/not filled and do the same for all
     * sub-object(group)s
     * redefined from superclass Object3D
     */
    public void setFilled(boolean fill)
    {   // this group
        filled = fill;
        // all sub-object(group)s
        for (int i = 0; i < objects.size(); i++) 
        {   Object3D ob = (Object3D) objects.elementAt(i);
            ob.setFilled(fill);
        }
    }
    /**
     * set this ObjectGroup3D) visible/invisible and do the same for all
     * sub-object(group)s (to avoid disappearance of objects when setting vis = true)
     * redefined from superclass Object3D
     */
    public void setVisible(boolean vis)
    {   // this group
        visible = vis;
        // all sub-object(group)s        
        for (int i = 0; i < objects.size(); i++) 
        {   Object3D ob = (Object3D) objects.elementAt(i);
            ob.setVisible(vis);
        }
    }
    /**
     * set all facets of this ObjectGroup3D) outlined/not outlined and do the same for all
     * sub-object(group)s
     * redefined from superclass Object3D
     */
    public void setOutlined(boolean outline)
    {   // this group
        outlined = outline;
        // all object(group)s below        
        for (int i = 0; i < objects.size(); i++) 
        {   Object3D ob = (Object3D) objects.elementAt(i);
            ob.setOutlined(outline);
        }
    }
    /**
     * set the sub-object(group) with index i of this ObjectGroup3D) visible/invisible
     * @param index the index is the sub-object(group) whose visibility is changed  
     * @param visible true/false
     */
    public void setVisible(int index, boolean visible)
    {   if ((index >= 0) && (index <= (objects.size() - 1)))
        {   Object3D ob = (Object3D) objects.elementAt(index);
            ob.setVisible(visible);
        }
    }
    /**
     * find (recursively) the Object3D in this ObjectGroupe3D containing vertex v
     * works only for object groups
     * @param v the search vertex
     * @return the Object3D containing vertex v or null 
     */
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
    /**
     * find (recursively) the Object3D in this ObjectGroupe3D containing facet f
     * works only for object groups
     * @param f the search facet
     * @return the Object3D containing facet f or null 
     */
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

