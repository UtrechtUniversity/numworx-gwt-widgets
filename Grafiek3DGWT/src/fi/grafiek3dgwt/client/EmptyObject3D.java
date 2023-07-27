package fi.grafiek3dgwt.client;

/**
 * class facilitating the construction of an empty
 * Object3D, that is none of the attributes are 
 * initialized; see superclass Object3D 
 * @author huub
 */
class EmptyObject3D extends Object3D
{   
	/**
	 * default contructor
	 */
	public EmptyObject3D()
	{}
	/**
	 * overridden from class Object3D		
	 */
	public Object3D deepCopy()
	{   EmptyObject3D copy = new EmptyObject3D();
		makeDeepObjectCopy(copy);
		return copy;
	}    
}    
