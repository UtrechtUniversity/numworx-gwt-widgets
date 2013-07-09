package fi.grafiek3dgwt.client;

import java.util.Vector;


public class ObjectGroup3D extends Object3D
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
        	if (ob.numFacets > 0)
        		System.arraycopy(ob.facets, 0, facets, fCount, ob.numFacets);
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

