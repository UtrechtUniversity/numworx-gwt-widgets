package fi.grafiek3dgwt.client;

/**
 * class containing real 3-vector utilities
 * @author huub
 */
public class Vector3D 
{   
	/**
	 * constant: a very small number
	 */
    public static final double NZero = 1e-9d;
    /**
     * constant: a very big number
     */
    public static final double NInf = 1e10d;
    /**
     * x-, y- and z-coordinate of this Vector3D; public for easy access
     */
    public double x, y, z;
    /**
     * default constructor gives (0, 0, 0)
     */
    public Vector3D()
    {   x = 0; y = 0; z = 0; // redundant code
    }
    /**
     * construct a Vector3D with given x-, y- and z-coordinates  
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     */
    public Vector3D(double x, double y, double z)
    {   this.x = x; this.y = y; this.z = z;
    }
    /**
     * construct a Vector3D from an array of doubles:
     * use only the first three doubles, if the array is too short,
     * initialize a zero-vector
     * @param coef double array with coordinates 
     */
    public Vector3D(double[] coef)
    {   // avoid ArrayOutOfBoundsException
        if (coef.length >= 3)
        {   x = coef[0]; y = coef[1]; z = coef[2];
        }
        else // redundant
        {   x = 0; y = 0; z = 0;
        }
    }
    /**
     * make a hard copy of Vector3D v  
     * @param v Vector3D to be copied
     */
    public Vector3D(Vector3D v)
    {   x = v.x; y = v.y; z = v.z;
    }
    // static methods to save memory
    /**
     * scale Vector3D v by s,that is multiply each coordinate  by s 
     * @param v Vector3D to be scaled
     * @param s scaling factor
     */
    public static void scaleBy(Vector3D v, double s)
    {   v.x *= s; v.y *= s; v.z *= s;
    }
    /**
     * scale the coordinates of vector v by different scaling factors
     * @param v Vector3D to be scaled
     * @param sx scaling factor for x-coordinate
     * @param sy scaling factor for y-coordinate
     * @param sz scaling factor for z-coordinate
     */
    public static void scaleBy(Vector3D v, double sx, double sy, double sz)
    {   v.x *= sx; v.y *= sy; v.z *= sz;
    }
    /**
     * translate Vector3D v over sx, sy, sz respectively, that is 
     * add vector (sx, sy, sz)
     * @param v Vector3D to translate
     * @param sx x-translation
     * @param sy y-translation
     * @param sz z-translation
     */
    public static void translateBy(Vector3D v, double sx, double sy, double sz)
    {   v.x += sx; v.y += sy; v.z += sz;
    }
    /**
     * translate Vector3D v by Vectro3D s, that is, add Vector3D s to v;
     * same as plus(), included for conceptual reasons
     * @param v Vector3D to translate
     * @param s translate Vector3D
     */
    public static void translateBy(Vector3D v, Vector3D s)
    {   v.x += s.x; v.y += s.y; v.z += s.z;
    }
    /**
     * find the dot product of Vector3D u with Vector3D v
     * @param u first Vector3D 
     * @param v second Vector3D
     * @return dot product
     */
    public static double dotProduct(Vector3D u, Vector3D v)
    {   return u.x * v.x + u.y * v.y + u.z * v.z;
    }
    /**
     * find the cross product of Vector3D u with Vector3D v
     * @param u first Vector3D
     * @param v second Vector3D
     * @return cross product (a Vector3D)
     */
    public static Vector3D crossProduct(Vector3D u, Vector3D v)
    {   return new Vector3D(u.y * v.z - v.y * u.z,
                            u.z * v.x - v.z * u.x,
                            u.x * v.y - v.x * u.y);
    }
    // length of vector v
    /**
     * find the length of Vector3D v
     * @param v Vector3D whose length should be calculated
     * @return length of Vector3D v
     */
    public static double length(Vector3D v)
    {   return Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
    }
    /**
     * make Vector3D v unitary (that is with length equal 1)
     * if v is nearly the zero-vector, v is set to (0,0,0)
     * @param v Vector3D v to be made unitary
     */
    public static void makeUnitary(Vector3D v)
    {   double ln = length(v);
        if (ln > NZero)
        {    v.x /= ln; v.y /= ln; v.z /= ln;
        }
        else
        {    v.x = 0; v.y = 0; v.z = 0;
        }
     }
    /**
     * check if this Vector3D equals Object3D (assumed to be a Vecotr3D)
     * in the sense that their distance is very small; 
     * redefined for use in method contains() in class Vector
     */
    public boolean equals(Object obj)
    {    if (obj instanceof Vector3D)
             return distance(this, (Vector3D) obj) < NZero;
         return false;    
    }
    /**
     * check if Vector3D u equals Vector3D v
     * in the sense that their distance is very small;
     * @param u first Vector3D 
     * @param v second Vector3D
     * @return true/false
     */
    public static boolean equals(Vector3D u, Vector3D v)
    {    return distance(u, v) < NZero;
    }
    /**
     * check if Vector3d u is a multiple of Vector3D v
     * true if u or v is the zero vector
     * @param u first Vector3D
     * @param v second Vector3D
     * @return true/false
     */
    public static boolean isMultipleOf(Vector3D u, Vector3D v)
    {   // make copies, makeUnitary(Vector3D v) changes the vector! 
        Vector3D u1 = new Vector3D(u);
        Vector3D v1 = new Vector3D(v);
        Vector3D z = new Vector3D(); // zero vector
        makeUnitary(u1);
        makeUnitary(v1);
        // opposite of v1 is zero vector minus v1,
        // still unitary
        Vector3D minv1 = Vector3D.minus(new Vector3D(), v1);        
        return equals(u1, z) || equals(v1, z) ||
               equals(u1, v1) || equals(u1, minv1); 
    }    
    /**
     * find the difference of Vector3D u with Vector3D v
     * @param u first Vector3D
     * @param v second Vector3D
     * @return u minus v
     */
    public static Vector3D minus(Vector3D u, Vector3D v)
    {   return new Vector3D(u.x - v.x, u.y - v.y, u.z - v.z);
    }
    /**
     * find the sum of Vector3D u with Vector3D v
     * @param u first Vector3D
     * @param v second Vector3D
     * @return u plus v
     */
    public static Vector3D plus(Vector3D u, Vector3D v)
    {   return new Vector3D(u.x + v.x, u.y + v.y, u.z + v.z);
    }
    /**
     * distance between Vector3D u and Vector3D v as points, 
     * or equivalently the length of their difference
     * @param u first Vector3D
     * @param v second Vector3D
     * @return distance u-v
     */
    public static double distance(Vector3D u, Vector3D v)
    {   return Math.sqrt((u.x - v.x) * (u.x - v.x) +
                         (u.y - v.y) * (u.y - v.y) +
                         (u.z - v.z) * (u.z - v.z)) ;
    }
    /**
     * find the projection of the Vector3D v on the axis with
     * direction Vector3D axis
     * the Vector3D axis should be nonzero 
     * @param v Vector3D to be projected
     * @param axis Vector3D defining the axis to be projected upon
     * @return projection vector
     */
    public static Vector3D projectOn(Vector3D v, Vector3D axis)
    {   Vector3D result = new Vector3D(axis);
        double s = Vector3D.dotProduct(v, axis) /
                   Vector3D.dotProduct(axis, axis);
        Vector3D.scaleBy(result, s);
        return result;
    }    
    
    /**
     * return ths Vector3D as a String, redefined 
     */
    public String toString()
    {   return UF.format(x, 2) + " & " +
               UF.format(y, 2) + " & " + 
               UF.format(z, 2);
        
    }
    /**
     * make a hard copy of Vector3D v 
     * @param v Vector3D to be copied
     * @return copy of v
     */
    public static Vector3D copyVector3D(Vector3D v)
    {  	if (v == null)
    		return null;
    	else
    		return new Vector3D(v);
    }
    
} // class Vector3D



  
