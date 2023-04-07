package fi.doorziengwt.client;

/**
    real 3-vector utilities; see class Vector3D in Grafiek3DGWT
*/    

public class Vector3D 
{   // constants
    // a small number
    public static final double NZero = 1e-9d;
    // a very big number
    public static final double NInf = 1e10d;
    // public attributes for easy access
    public double x, y, z;
    // default constructor gives (0, 0, 0)
    public Vector3D()
    {   x = 0; y = 0; z = 0; // redundant
    }
    // overloaded constructor
    public Vector3D(double x, double y, double z)
    {   this.x = x; this.y = y; this.z = z;
    }
    // overloaded constructor
    public Vector3D(double[] coef)
    {   // avoid ArrayOutOfBoundsException
        if (coef.length >= 3)
        {   x = coef[0]; y = coef[1]; z = coef[2];
        }
        else // redundant
        {   x = 0; y = 0; z = 0;
        }
    }
    // overloaded constructor, makes a copy of vector v
    public Vector3D(Vector3D v)
    {   x = v.x; y = v.y; z = v.z;
    }
    // static methods to save memory
    // multiply each coordinate of vector v by s
    public static void scaleBy(Vector3D v, double s)
    {   v.x *= s; v.y *= s; v.z *= s;
    }
    // multiply coordinates of vector v by sx, sy, sz respectively
    public static void scaleBy(Vector3D v, double sx, double sy, double sz)
    {   v.x *= sx; v.y *= sy; v.z *= sz;
    }
    // translate vector v over sx, sy, sz respectively
    // i.e. add vector (sx, sy, sz)
    public static void translateBy(Vector3D v, double sx, double sy, double sz)
    {   v.x += sx; v.y += sy; v.z += sz;
    }
    // translate vector v over s i.e. add vector s
    // same as plus, for conceptual reasons
    public static void translateBy(Vector3D v, Vector3D s)
    {   v.x += s.x; v.y += s.y; v.z += s.z;
    }
    // return the dot product of vector u with vector v
    public static double dotProduct(Vector3D u, Vector3D v)
    {   return u.x * v.x + u.y * v.y + u.z * v.z;
    }
    // return the cross product of vector u with vector v
    public static Vector3D crossProduct(Vector3D u, Vector3D v)
    {   return new Vector3D(u.y * v.z - v.y * u.z,
                            u.z * v.x - v.z * u.x,
                            u.x * v.y - v.x * u.y);
    }
    // length of vector v
    public static double length(Vector3D v)
    {   return Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
    }
    // make vector v unitary
    // a (nearly) 0-vector is set to (0,0,0)
    public static void makeUnitary(Vector3D v)
    {   double ln = length(v);
        if (ln > NZero)
        {    v.x /= ln; v.y /= ln; v.z /= ln;
        }
        else
        {    v.x = 0; v.y = 0; v.z = 0;
        }
     }
    // redefine for method contains in Vector     
    // equality of this vector and vector v in Euclidean metric NZero
    public boolean equals(Object obj)
    {    if (obj instanceof Vector3D)
             return distance(this, (Vector3D) obj) < NZero;
         return false;    
    }
    // equality of vector u and vector v in Euclidean metric NZero
    public static boolean equals(Vector3D u, Vector3D v)
    {    return distance(u, v) < NZero;
    }
    // check if vector u is a multiple of vector v
    // also true if u or v is the zero vector
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
    // difference of vector u with vector v
    public static Vector3D minus(Vector3D u, Vector3D v)
    {   return new Vector3D(u.x - v.x, u.y - v.y, u.z - v.z);
    }
    // sum of vector u with vector v
    public static Vector3D plus(Vector3D u, Vector3D v)
    {   return new Vector3D(u.x + v.x, u.y + v.y, u.z + v.z);
    }
    // distance between vector u and vector v (as points)
    public static double distance(Vector3D u, Vector3D v)
    {   return Math.sqrt((u.x - v.x) * (u.x - v.x) +
                         (u.y - v.y) * (u.y - v.y) +
                         (u.z - v.z) * (u.z - v.z)) ;
    }
    // find the projection of the vector v on the axis with 
    // directional vector axis
    public static Vector3D projectOn(Vector3D v, Vector3D axis)
    {   Vector3D result = new Vector3D(axis);
        double s = Vector3D.dotProduct(v, axis) /
                   Vector3D.dotProduct(axis, axis);
        Vector3D.scaleBy(result, s);
        return result;
    }    
    
    public String toString()
    {   return UF.format(x, 2) + " & " +
               UF.format(y, 2) + " & " + 
               UF.format(z, 2);
        
    }    
    
} // class Vector3D

    