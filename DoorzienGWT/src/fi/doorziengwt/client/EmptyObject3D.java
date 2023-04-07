package fi.doorziengwt.client;

/**
 * an empty Object3D, no vertices or facets are initialized; see class EmptyObject3D
 * in Grafiek3DGWT 
 * @author huub
 */

public class EmptyObject3D extends Object3D
{   // default constructor
    public EmptyObject3D()
    {}
    public Object3D deepCopy()
    {   EmptyObject3D copy = new EmptyObject3D();
        makeDeepObjectCopy(copy);
        return copy;
    }    
}    
