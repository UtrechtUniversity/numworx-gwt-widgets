package fi.doorziengwt.client;

//import java.awt.Polygon;
//import java.io.Serializable;
/*
    real 3-vector utilities
    matrix utilities
    geometric classes and utilities
*/    

// a (row or column) vector, 3D real point
public class Vector3D //implements Serializable
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

// class representing an affine transformation
class Matrix3D //implements Serializable
{   // public for easy access
    // 3x3 matrix, consisting of 3 row vectors
    public Vector3D row1, row2, row3;
    // a vector representing the translation
    public Vector3D origin = new Vector3D(); // (0, 0, 0)
    // scaling factors
    public double scaleX = 1, scaleY = 1, scaleZ = 1;
    // general constructor for 3x3 matrix
    // origin will be (0,0,0), scale factors 1
    public Matrix3D(double t11, double t12, double t13,
              double t21, double t22, double t23,
              double t31, double t32, double t33)
    {   row1 = new Vector3D(t11, t12, t13);
        row2 = new Vector3D(t21, t22, t23);
        row3 = new Vector3D(t31, t32, t33);
    }
    // overloaded constructor for 3x3 unit matrix and origin (0,0,0)
    // and scale factors 1
    public Matrix3D()
    {   this(1,0,0, 0,1,0, 0,0,1);
    }
    // static methods    
    // return column i of the 3x3 matrix mat as a Vector3D
    public static Vector3D column(int i, Matrix3D mat)
    {   Vector3D col = null;
        switch (i)
        {   case 1: col = new Vector3D(mat.row1.x, mat.row2.x, mat.row3.x);
            break;
            case 2: col = new Vector3D(mat.row1.y, mat.row2.y, mat.row3.y);
            break;
            case 3: col = new Vector3D(mat.row1.z, mat.row2.z, mat.row3.z);
            break;
            default: // returns null
        }
        return col;
    }
    // make a deep copy of Matrix3D mat
    public static Matrix3D copy(Matrix3D mat)
    {   Matrix3D result = new Matrix3D(mat.row1.x, mat.row1.y, mat.row1.z,
                                       mat.row2.x, mat.row2.y, mat.row2.z,
                                       mat.row3.x, mat.row3.y, mat.row3.z);
        result.origin = new Vector3D(mat.origin);
        result.scaleX = mat.scaleX;
        result.scaleY = mat.scaleY;
        result.scaleZ = mat.scaleZ;
        return result;
    }
    // check for equality
    public static boolean equals(Matrix3D mat1, Matrix3D mat2)
    {   return mat1.row1.equals(mat2.row1) &&
               mat1.row2.equals(mat2.row2) && 
               mat1.row3.equals(mat2.row3) && 
               mat1.origin.equals(mat2.origin) &&
               ((mat1.scaleX - mat2.scaleX) < Vector3D.NZero) &&
               ((mat1.scaleY - mat2.scaleY) < Vector3D.NZero) &&
               ((mat1.scaleZ - mat2.scaleZ) < Vector3D.NZero);
    }

    // matrix back to identity, origin and scale unchanged    
    public void reset()
    {   row1 = new Vector3D(1, 0, 0);
        row2 = new Vector3D(0, 1, 0);
        row3 = new Vector3D(0, 0, 1);
    }    
    // set scaling factors
    public void setScale(double sx, double sy, double sz)
    {   scaleX = sx; scaleY = sy; scaleZ = sz;
    }
    // scaling successively
    public void scaleBy(double sx, double sy, double sz)
    {   scaleX *= sx; scaleY *= sy; scaleZ *= sz;
    }
    // set a new origin
    public void setOrigin(double sx, double sy, double sz)
    {   origin.x = sx; origin.y = sy; origin.z = sz;
    }
    // translate the origin by (sx,sy,sz) use this for
    // for successive translations
    public void translateBy(double sx, double sy, double sz)
    {   Vector3D.translateBy(origin, sx, sy, sz);
    }
    // multiply Matrix3D mat on the left by Matrix3D leftMat
    // use a deep copy of this matrix BEFORE multiplication
    // do not change the origin and the scale
    public static void leftMultiplyBy(Matrix3D mat, Matrix3D leftMat)
    {   Matrix3D temp = copy(mat);
        mat.row1.x = Vector3D.dotProduct(leftMat.row1, column(1, temp));
        mat.row1.y = Vector3D.dotProduct(leftMat.row1, column(2, temp));
        mat.row1.z = Vector3D.dotProduct(leftMat.row1, column(3, temp));
        mat.row2.x = Vector3D.dotProduct(leftMat.row2, column(1, temp));
        mat.row2.y = Vector3D.dotProduct(leftMat.row2, column(2, temp));
        mat.row2.z = Vector3D.dotProduct(leftMat.row2, column(3, temp));
        mat.row3.x = Vector3D.dotProduct(leftMat.row3, column(1, temp));
        mat.row3.y = Vector3D.dotProduct(leftMat.row3, column(2, temp));
        mat.row3.z = Vector3D.dotProduct(leftMat.row3, column(3, temp));
    }
    
    // leftmultiply this matrix by a theta degree rotation around the x-axis
    // clockwise when looking from x+
    public void xRotateBy(double theta)
    {   // convert degrees to radians
        theta *= (Math.PI / 180);
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        Matrix3D.leftMultiplyBy(this, 
            new Matrix3D(1,         0,        0,
                         0,  cosTheta, sinTheta,
                         0, -sinTheta, cosTheta));
    }
    // leftmultiply this matrix by a theta degree rotation around the y-axis
    // clockwise when looking from y+
    public void yRotateBy(double theta)
    {   // convert degrees to radians
        theta *= (Math.PI / 180);
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        Matrix3D.leftMultiplyBy(this,
            new Matrix3D(cosTheta, 0, -sinTheta,
                          0,        1,        0,
                          sinTheta, 0, cosTheta));
    }
    // leftmultiply this matrix by a theta degree rotation around the z-axis
    // clockwise when looking from z+
    public void zRotateBy(double theta)
    {   // convert degrees to radians
        theta *= (Math.PI / 180);
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        Matrix3D.leftMultiplyBy(this, 
            new Matrix3D( cosTheta, sinTheta, 0,
                          -sinTheta, cosTheta, 0,
                           0,        0,        1));
    }
    
    // leftmultiply this matrix by the rotation which
    // turns vector v into a (multiple of) vector w
    // clockwise when looking from the direction of
    // v X w (crossproduct)
    // assume v and w not zero-vector
    public void vwRotate(Vector3D vVec, Vector3D wVec)
    {   Vector3D v = new Vector3D(vVec);
        Vector3D w = new Vector3D(wVec);
        Vector3D.makeUnitary(v);
        Vector3D.makeUnitary(w);
        if (v.equals(w))
            return; // identity
        Vector3D cross = Vector3D.crossProduct(v, w);    
        Line3D axis = new Line3D(new Vector3D(), cross);
        double angle = Math.acos(Vector3D.dotProduct(v, w));
        Vector3D check = axis.rotateBy(v, angle);            
        // if wrong angle, take minus
        if (!check.equals(w))
        {    angle = - angle;
        }            
        Vector3D column1 = axis.rotateBy(new Vector3D(1, 0, 0), angle);            
        Vector3D column2 = axis.rotateBy(new Vector3D(0, -1, 0), angle);                    
        Vector3D column3 = axis.rotateBy(new Vector3D(0, 0, 1), angle);                            
        Matrix3D vwRot = new Matrix3D(
            column1.x, column2.x, column3.x,
            column1.y, column2.y, column3.y,        
            column1.z, column2.z, column3.z);            
        Matrix3D.leftMultiplyBy(this, vwRot);    
        
    }

    // find the determinant of the matrix with rows (or columns!)
    // vec1, vec2, vec3
    public static double determinant(Vector3D vec1, Vector3D vec2, Vector3D vec3)
    {   return
            vec1.x * vec2.y * vec3.z + 
            vec1.y * vec2.z * vec3.x +
            vec1.z * vec2.x * vec3.y - 
            vec1.z * vec2.y * vec3.x -
            vec1.x * vec2.z * vec3.y - 
            vec1.y * vec2.x * vec3.z;
    }
    // find the inverse of the 3x3 matrix mat (and add origin (0,0,0)
    public static Matrix3D invert(Matrix3D mat)
    {   // find determinant
        double det = determinant(mat.row1, mat.row2, mat.row3);
        if (Math.abs(det) <= Vector3D.NZero)
            return null;
         // use Cramer's rule
        Matrix3D result = new Matrix3D(
            mat.row2.y * mat.row3.z - mat.row3.y * mat.row2.z, // C11:
            - mat.row1.y * mat.row3.z + mat.row3.y * mat.row1.z, // C21:
            mat.row1.y * mat.row2.z - mat.row2.y * mat.row1.z, // C31:
            - mat.row2.x * mat.row3.z + mat.row3.x * mat.row2.z, // C12:
            mat.row1.x * mat.row3.z - mat.row3.x * mat.row1.z, // C22:
            - mat.row1.x * mat.row2.z + mat.row2.x * mat.row1.z, // C32:
            mat.row2.x * mat.row3.y - mat.row3.x * mat.row2.y, // C13:
            - mat.row1.x * mat.row3.y + mat.row3.x * mat.row1.y, // C23:
            mat.row1.x * mat.row2.y - mat.row2.x * mat.row1.y); // C33:
        Vector3D.scaleBy(result.row1, 1 / det);
        Vector3D.scaleBy(result.row2, 1 / det);
        Vector3D.scaleBy(result.row3, 1 / det);
        return result;
    }
    // transforming world space to view space    
    // given a Vector3D (point) vec, leftmultiply it with this matrix
    // scale the result, then translate the result over the origin
    public Vector3D transform(Vector3D vec)
    {   Vector3D res = new Vector3D();
        res.x = Vector3D.dotProduct(row1, vec);
        res.y = Vector3D.dotProduct(row2, vec);
        res.z = Vector3D.dotProduct(row3, vec);
        Vector3D.scaleBy(res, scaleX, scaleY, scaleZ);
        Vector3D.translateBy(res, origin);
        return res;
    }
    // transforming normal vectors which do not have to     
    // be translated
    // note: scale because in view space 
    // y-axis is reversed
    public Vector3D nTransform(Vector3D vec)
    {   Vector3D res = new Vector3D();
        res.x = Vector3D.dotProduct(row1, vec);
        res.y = Vector3D.dotProduct(row2, vec);
        res.z = Vector3D.dotProduct(row3, vec);
        Vector3D.scaleBy(res, scaleX, scaleY, scaleZ);        
        return res;
    }

// niet duidelijk!!    
    
    // transformations in world space    
    // given a Vector3D (point) vec, assumed to be 
    // positioned at origin, translate it back to (0, 0, 0)
    // leftmultiply it with the 3x3 matrix
    // then translate the result back to origin
    
// dit voor schalen en translaties

// voor rotaties OUDE versie

// DUS: oMat fungeert als een cumulatief geheugen wat je met
// het object in world space hebt gedaan!!!
// niet goed, als je na een verandering van oMat oTranform doet
// moet oMat eigenlijk weer identity worden 

    public Vector3D oTransform(Vector3D vec)
    {   Vector3D res = new Vector3D();
        Vector3D temp = Vector3D.minus(vec, origin);
        res.x = Vector3D.dotProduct(row1, temp) + origin.x;
        res.y = Vector3D.dotProduct(row2, temp) + origin.y;
        res.z = Vector3D.dotProduct(row3, temp) + origin.z;
        Vector3D.scaleBy(res, scaleX, scaleY, scaleZ);                
        return res;
    }
    // idem for normal vectors which are and should stay in 
    // (0, 0, 0) no scaling necessary
    public Vector3D onTransform(Vector3D vec)
    {   Vector3D res = new Vector3D();
        res.x = Vector3D.dotProduct(row1, vec);
        res.y = Vector3D.dotProduct(row2, vec);
        res.z = Vector3D.dotProduct(row3, vec);
        return res;
    }
} // class Matrix3D

class Plane3D //implements Serializable
{   // attributes
    // equation representation
    // all vectors x with (n,x)=(n,p) where n normal vector
    // to the plane, with ||n|| = 1 and p a point of the plane
    Vector3D normal, point;
    // vector representation
    // all vectors of the form
    // support+lambda*direction1+mu*direction2
    Vector3D support, direction1, direction2;
    // constructor using the equation representation
    // plane with equation ax+by+cz=d
    public Plane3D(double a, double b, double c, double d)
    {   // equation representation
        // rewrite
        normal = new Vector3D(a, b, c);
        double nl = Vector3D.length(normal);
        Vector3D.makeUnitary(normal);
        double newd = d / nl;
        if (Math.abs(normal.x) > Vector3D.NZero)
            point = new Vector3D(newd / normal.x, 0, 0);
        else if (Math.abs(normal.y) > Vector3D.NZero)
            point = new Vector3D(0, newd / normal.y, 0);        
        else // Math.abs(normal.z) > Vector3D.NZero
            point = new Vector3D(0, 0, newd / normal.z);                
        // find vector representation
        support = new Vector3D(point);
        // find two vectors independent of normal
        Vector3D xUnit = new Vector3D(1, 0, 0);
        Vector3D yUnit = new Vector3D(0, 1, 0);        
        Vector3D zUnit = new Vector3D(0, 0, 1);        
        Vector3D proj1, proj2;
        // normal not in x-y-plane
        if (Math.abs(Matrix3D.determinant(normal, xUnit, yUnit))
                > Vector3D.NZero)
        {   proj1 = Vector3D.projectOn(xUnit, normal);
            proj2 = Vector3D.projectOn(yUnit, normal);
            // independent since otherwise the plane through
            // normal and x and the plane through normal and y
            // coincide
            direction1 = Vector3D.minus(xUnit, proj1);
            direction2 = Vector3D.minus(yUnit, proj2);
        }
        // normal not in x-z-plane
        else if (Math.abs(Matrix3D.determinant(normal, xUnit, zUnit))
                    > Vector3D.NZero)
        {   proj1 = Vector3D.projectOn(xUnit, normal);
            proj2 = Vector3D.projectOn(zUnit, normal);
            direction1 = Vector3D.minus(xUnit, proj1);
            direction2 = Vector3D.minus(zUnit, proj2);
        }
        else  // normal in x-y-plane and x-z-plane THUS not in y-z-plane                
        {   proj1 = Vector3D.projectOn(yUnit, normal);
            proj2 = Vector3D.projectOn(zUnit, normal);
            direction1 = Vector3D.minus(yUnit, proj1);
            direction2 = Vector3D.minus(zUnit, proj2);
        }
        
    }    
    // constructor using 3 non-collinear(!) points 
    public Plane3D(Vector3D vec1, Vector3D vec2, Vector3D vec3)    
    {   // vector representation
        support = new Vector3D(vec1); // new!!
        direction1 = Vector3D.minus(vec2, vec1);
        direction2 = Vector3D.minus(vec3, vec1);
        // find equation representation
        // note: normal corresponds to orientation v1->v2->v3
        normal = Vector3D.crossProduct(direction1, direction2);
        Vector3D.makeUnitary(normal);
        point = new Vector3D(support);
    }
    
    public Plane3D copy()
    {   return new Plane3D(support,
                    Vector3D.plus(support, direction1),
                    Vector3D.plus(support, direction2));
    }    
    
    // find the translation of this plane by the vector v
    public Plane3D translateBy(Vector3D v)
    {   Vector3D point1 = Vector3D.plus(support, v);
        Vector3D point2 = Vector3D.plus(support, direction1);
        point2 = Vector3D.plus(point2, v);
        Vector3D point3 = Vector3D.plus(support, direction2);
        point3 = Vector3D.plus(point3, v);
        return new Plane3D(point1, point2, point3);
    }    
    // find the rotation of this plane around Line3D line over an angle
    // of theta radians
    public Plane3D rotateBy(Line3D line, double theta)
    {   Vector3D point1 = line.rotateBy(support, theta);
        Vector3D point2 = Vector3D.plus(support, direction1);
        point2 = line.rotateBy(point2, theta);
        Vector3D point3 = Vector3D.plus(support, direction2);
        point3 = line.rotateBy(point3, theta);
        return new Plane3D(point1, point2, point3);
    }    
    
    
    // check if this plane contains vector v
    // i.e. (n,v)-(n,p) "small"
    public boolean contains(Vector3D v)
    {   return (Math.abs(Vector3D.dotProduct(normal, v) - 
                         Vector3D.dotProduct(normal, point)))
                             < Vector3D.NZero;
    }    


    // find the position of Vector3d relative to this plane
    // use representation (n,x)=(n,p) or (n,x)-(n,p)=0
    public int planePosition(Vector3D v)
    {   int result = 0;
        // find (n,v)-(n,p)
        double temp = Vector3D.dotProduct(normal, v) -
                      Vector3D.dotProduct(normal, point);
        if (temp < -Vector3D.NZero)
            result = -1; // "left"
        else if (temp > Vector3D.NZero)
            result = 1; // "right"
        else // temp very small
            result = 0; // "on"
        return result;
    }    

    
    // find type of intersection type of a line and a plane
    // 0 = line parallel to plane
    // 1 = a point
    // 2 = line is in the plane
    // first look if line.direction is in the plane through
    // plane.direction1, plane.direction2 (and zero)
    // then the line is in the plane if line.support is in the plane
    // else it is parallel to the plane
    // if not, intersection is a point
    public static int intersectionType(Line3D line, Plane3D plane)
    {   // plane through (0,0,0) parallel to this plane
        Plane3D tplane = new Plane3D(plane.direction1, plane.direction2,
                                     new Vector3D(0,0,0));
        if (tplane.contains(line.direction))
        {   if (plane.contains(line.support))
                return 2;
            else
                return 0;
        }
        else
            return 1;
    }    

    // get the intersection point of a line and a plane
    // assume they DO intersect in 1(!) point
    public static Vector3D getIntersectionPoint(Line3D line, Plane3D plane)
    {   // points on the line are of the form
        // line.support+lambda*line.direction
        // to be on the plane they need to satisfy
        // (plane.normal, line.support+lambda*line.direction)=
        // (plane.normal, plane.point), thus
        // lambda*(plane.normal, line.direction) =
        // (plane.normal, plane.point-line.support)
        // since the line is not in the plane or parallel to the plane
        // we can divide by (plane.normal, line.direction)
        double lambda = Vector3D.dotProduct(
                            plane.normal,
                            Vector3D.minus(plane.point, line.support)) /
                        Vector3D.dotProduct(
                            plane.normal, line.direction);
        Vector3D lineSupport = new Vector3D(line.support);
        Vector3D lineDirection = new Vector3D(line.direction);
        Vector3D.scaleBy(lineDirection, lambda);
        Vector3D result = Vector3D.plus(lineSupport, lineDirection);
        return result;
    }    
    // find type of intersection type of two planes
    // 0 = parallel
    // 1 = a line
    // 2 = the planes coincide
    // first look at the normals, if they are equal or opposite
    // the planes are parallel or coincide by checking if
    // plane1.support is on plane2
    // else the planes intersect in a line
    public static int intersectionType(Plane3D plane1, Plane3D plane2)
    {   // note normals are unitary
        Vector3D minnormal1 = 
            Vector3D.minus(new Vector3D(0,0,0), plane1.normal);
        if (Vector3D.equals(plane1.normal, plane2.normal) ||
            Vector3D.equals(minnormal1, plane2.normal))
        {   if (plane2.contains(plane1.support))
                return 2;
            else
                return 0;
        }
        else
            return 1;
    }    

    // redefine equals in Object for method contains in Vector
    public boolean equals(Object obj)
    {   if (obj instanceof Plane3D)
            return intersectionType(this, (Plane3D) obj) == 2;
        return false;    
    }    

    // get the intersection line of two planes
    // assume they DO intersect in 1(!) line
    public static Line3D getIntersectionLine(Plane3D plane1, Plane3D plane2)
    {   // points on plane1 are of the form
        // plane1.support+lambda*plane1.direction1+mu*plane1.direction2
        // to be on plane2 they need to satisfy
        // (plane2.normal,
        //  plane1.support+lambda*plane1.direction1+mu*plane1.direction2)= 
        // (plane2.normal, plane2.point) thus
        // lambda*(plane2.normal, plane1.direction1) +
        // mu*(plane2.normal, plane1.direction2) =
        // (plane2.normal, plane2.point-plane1.support)
        // two cases (must be one of these since the planes do
        // not coincide or are parallel
        
        // case 1: (plane2.normal, plane1.direction1) not zero
        // lambda = - mu * (plane2.normal, plane1.direction2) /
        //                 (plane2.normal, plane1.direction1) +
        //          (plane2.normal, plane2.point-plane1.support) /
        //          (plane2.normal, plane1.direction1)
        // and points on the line are of the form
        // plane1.support +
        // [(plane2.normal, plane2.point-plane1.support)/
        // (plane2.normal, plane1.direction1)] * plane1.direction1 + 
        // mu * [plane1.direction2 - 
        //       (plane2.normal, plane1.direction2) /
        //       (plane2.normal, plane1.direction1) * plane1.direction1]
        double n2d11 = Vector3D.dotProduct(plane2.normal, plane1.direction1);
        double n2d12 = Vector3D.dotProduct(plane2.normal, plane1.direction2);        
        double n2p2mins1 = Vector3D.dotProduct(plane2.normal,
                               Vector3D.minus(plane2.point, plane1.support));                           
        if (Math.abs(n2d11) > Math.abs(n2d12))
        {   
            // support of intersection line, is on line                                    
            Vector3D vec1 = new Vector3D(plane1.direction1);
            Vector3D.scaleBy(vec1, n2p2mins1 / n2d11);
            vec1 = Vector3D.plus(plane1.support, vec1);
            // direction of intersection line
            Vector3D dir = new Vector3D(plane1.direction1);
            Vector3D.scaleBy(dir, n2d12 / n2d11);
            dir = Vector3D.minus(plane1.direction2, dir);            
            Vector3D vec2 = Vector3D.plus(vec1, dir);
            Line3D result = new Line3D(vec1, vec2);
            return result;
            
        }
        // case 2: (plane2.normal, plane1.direction2) not zero
        // mu = - lambda * (plane2.normal, plane1.direction1) /
        //                 (plane2.normal, plane1.direction2) +
        //          (plane2.normal, plane2.point-plane1.support) /
        //          (plane2.normal, plane1.direction2)
        // and points on the line are of the form
        // plane1.support +
        // [(plane2.normal, plane2.point-plane1.support)/
        // (plane2.normal, plane1.direction2)] * plane1.direction2 + 
        // lambda * [plane1.direction1 - 
        //          (plane2.normal, plane1.direction1) /
        //          (plane2.normal, plane1.direction2) * plane1.direction2]
        else
        {
            // support of intersection line, is on line                                    
            Vector3D vec1 = new Vector3D(plane1.direction2);
            Vector3D.scaleBy(vec1, n2p2mins1 / n2d12);
            Vector3D.plus(plane1.support, vec1);
            // direction of intersection line
            Vector3D dir = new Vector3D(plane1.direction2);
            Vector3D.scaleBy(dir, n2d11 / n2d12);
            dir = Vector3D.minus(plane1.direction1, dir);            
            Vector3D vec2 = Vector3D.plus(vec1, dir);
            Line3D result = new Line3D(vec1, vec2);
            return result;
        }    
    }    


    // check if the point v which is in the plane through v1, v2, v3
    // is an inner point of the 3D triangle v1, v2, v3
    // v1, v2, v3 MUST span a plane
    public static boolean triangleContainsPoint(
        Vector3D v1, Vector3D v2, Vector3D v3, Vector3D v)
    {   
        Plane3D tPlane = new Plane3D(v1, v2, v3);
        // find barycenter of triangle    
        double xSum = v1.x + v2.x + v3.x;
        double ySum = v1.y + v2.y + v3.y;
        double zSum = v1.z + v2.z + v3.z;
        Vector3D bc = new Vector3D(xSum / 3, ySum / 3, zSum / 3);
        Line3D line1 = new Line3D(v1, v2);        
        if (line1.contains(v))
            return false;
        Line3D line2 = new Line3D(v1, v3);                
        if (line2.contains(v))
            return false;
        Line3D line3 = new Line3D(v2, v3);                
        if (line3.contains(v))
            return false;
        Plane3D oPlane1 = new Plane3D(line1.support,
            Vector3D.plus(line1.support, line1.direction),
            Vector3D.plus(line1.support, tPlane.normal));
        Plane3D oPlane2 = new Plane3D(line2.support,
            Vector3D.plus(line2.support, line2.direction),
            Vector3D.plus(line2.support, tPlane.normal));
        Plane3D oPlane3 = new Plane3D(line3.support,
            Vector3D.plus(line3.support, line3.direction),
            Vector3D.plus(line3.support, tPlane.normal));
        int ppbc1 = oPlane1.planePosition(bc);
        int ppv1 = oPlane1.planePosition(v);
        int ppbc2 = oPlane2.planePosition(bc);
        int ppv2 = oPlane2.planePosition(v);
        int ppbc3 = oPlane3.planePosition(bc);
        int ppv3 = oPlane3.planePosition(v);
        return (ppbc1 == ppv1) && (ppbc2 == ppv2) && (ppbc3 == ppv3);
    }

    // tijdelijk
    public String toString()
    {   Vector3D point1 = new Vector3D(support);
        Vector3D point2 = Vector3D.plus(support, direction1);
        Vector3D point3 = Vector3D.plus(support, direction2);
        return "plane through " + UF.format(point1.x, 1) + " & " +
                                  UF.format(point1.y, 1) + " & " +
                                  UF.format(point1.z, 1) + " and " +
                                  UF.format(point2.x, 1) + " & " +
                                  UF.format(point2.y, 1) + " & " +
                                  UF.format(point2.z, 1) + " and " +
                                  UF.format(point3.x, 1) + " & " +
                                  UF.format(point3.y, 1) + " & " +
                                  UF.format(point3.z, 1);                                  
    }
    
    
} // class Plane3D   

class Line3D //implements Serializable
{   // attributes
    // segment representation
    Vector3D point1, point2;
    // vector representation
    Vector3D support, direction;
    // constructor using segment representation
    public Line3D(Vector3D vec1, Vector3D vec2)
    {   point1 = new Vector3D(vec1);
        point2 = new Vector3D(vec2);
        support = new Vector3D(vec1);
        direction = Vector3D.minus(vec2, vec1);    
    }    
    // note: constructor with vector representation not allowed    

    // find the translation of this line the vector v
    public Line3D translateBy(Vector3D v)
    {   Vector3D pt1 = Vector3D.plus(point1, v);
        Vector3D pt2 = Vector3D.plus(point2, v);
        return new Line3D(pt1, pt2);
    }    
    
    // check if vector v is on this line
    public boolean contains(Vector3D v)
    {   // write the line as intersection of two planes
        // find two independent vectors orthogonal to direction
        // first find two vectors independent of direction
        Vector3D xUnit = new Vector3D(1, 0, 0);
        Vector3D yUnit = new Vector3D(0, 1, 0);        
        Vector3D zUnit = new Vector3D(0, 0, 1);        
        Vector3D proj1, proj2, direction1, direction2;
        // direction not in x-y-plane
        if (Math.abs(Matrix3D.determinant(direction, xUnit, yUnit))
                > Vector3D.NZero)
        {   proj1 = Vector3D.projectOn(xUnit, direction);
            proj2 = Vector3D.projectOn(yUnit, direction);
            // independent since otherwise the plane through
            // direction and x and the plane through direction and y
            // coincide
            // these are orthogonal to direction
            direction1 = Vector3D.minus(xUnit, proj1);
            direction2 = Vector3D.minus(yUnit, proj2);
        }
        // direction not in x-z-plane 
        else if (Math.abs(Matrix3D.determinant(direction, xUnit, zUnit))
                > Vector3D.NZero)
        {   proj1 = Vector3D.projectOn(xUnit, direction);
            proj2 = Vector3D.projectOn(zUnit, direction);
            // these are orthogonal to direction            
            direction1 = Vector3D.minus(xUnit, proj1);
            direction2 = Vector3D.minus(zUnit, proj2);
        }
        else // direction in x-y-plane and x-z-plane thus not in y-z-plane
        {   proj1 = Vector3D.projectOn(yUnit, direction);
            proj2 = Vector3D.projectOn(zUnit, direction);
            // these are orthogonal to direction            
            direction1 = Vector3D.minus(yUnit, proj1);
            direction2 = Vector3D.minus(zUnit, proj2);
        }
        // now this line is the intersection of the planes
        // through support, support+direction, support+direction1
        // and support, support+direction, support+direction2
        Plane3D plane1 = new Plane3D(support, 
                                     Vector3D.plus(support, direction),
                                     Vector3D.plus(support, direction1));
        Plane3D plane2 = new Plane3D(support, 
                                     Vector3D.plus(support, direction),
                                     Vector3D.plus(support, direction2));
        return plane1.contains(v) && plane2.contains(v);                             
                                     
    }    
    
    // check if vector v is on this line segment
    public boolean segmentContains(Vector3D v)
    {   boolean cont = contains(v);
        // v is on the line, v is on the segment if v is
        // between point1 and point2 (or equals one of both)
        if (cont)
        {   double summedDistance = 
                Vector3D.distance(point1, v) +
                Vector3D.distance(point2, v);
            return Math.abs(summedDistance - Vector3D.distance(point1, point2)) <
                   Vector3D.NZero;    
        }
        else
            return cont;
    }

    public static boolean segmentContainsPoint(Vector3D v1, Vector3D v2, 
        Vector3D v)
    {   // v MUST be on the line, v is inner point if v does not equal
        // the endpoints and is between point1 and point2
        if (v1.equals(v))
            return false;
        if (v2.equals(v))
            return false;
        double summedDistance = 
            Vector3D.distance(v1, v) + Vector3D.distance(v2, v);
        return Math.abs(Vector3D.distance(v1, v2) - summedDistance) <
                   Vector3D.NZero;    
    }
    
    // points assumed different!
    public static boolean areCollinear(Vector3D v1, Vector3D v2, Vector3D v3)
    {   Line3D tLine = new Line3D(v1, v2);
        return tLine.contains(v3);
    }    
    // find type of intersection type of two lines
    // 0 = crossing
    // 1 = a point
    // 2 = lines coincide
    // trick: 
    // first check if the directional vectors are multiples of each other
    // if they are AND the support of line1 is on line2 then the lines 
    // coincide, otherwise they cross
    // if the directional vectors are no multiples of each other
    // they span a plane P(through zero); if the planes
    // line1.support+P and line2.support+P coincide, the lines intersect
    // (in that plane), else they cross
    public static int intersectionType(Line3D line1, Line3D line2)
    {   
        if (Vector3D.isMultipleOf(line1.direction, line2.direction))
        {   if (line2.contains(line1.support))
                return 2;
            else
                return 0;
        }
        // line1.direction, line2.direction span a plane
        else
        {   Plane3D plane1 = new Plane3D(line1.support,
                                         Vector3D.plus(line1.support, line1.direction),
                                         Vector3D.plus(line1.support, line2.direction));
            Plane3D plane2 = new Plane3D(line2.support,
                                         Vector3D.plus(line2.support, line1.direction),
                                         Vector3D.plus(line2.support, line2.direction));                                         
            if (Plane3D.intersectionType(plane1, plane2) == 2)
                return 1;
            else
                return 0;
        }    
        
        
    }    
    // redefine equals on Object for contains in Vector
    public boolean equals(Object obj)
    {   if (obj instanceof Line3D)
            return intersectionType(this, (Line3D) obj) == 2;
        return false;
    }    
    // get the intersection point of 2 lines, assuming this exists    
    // find a plane through line1 not containing line2
    // intersect this with line2
    public static Vector3D getIntersectionPoint(Line3D line1, Line3D line2)
    {   // support vector of plane
        Vector3D sup = new Vector3D(line1.support);
        // directional vectors of plane
        Vector3D dir1 = new Vector3D(line1.direction);
        // second orthogonal to the plane through 
        // line1.direction and line2.direction
        Vector3D dir2 = Vector3D.crossProduct(line1.direction, line2.direction);
        Vector3D vec2 = Vector3D.plus(sup, dir1);
        Vector3D vec3 = Vector3D.plus(sup, dir2);        
        Plane3D plane = new Plane3D(sup, vec2, vec3);
        
        return Plane3D.getIntersectionPoint(line2, plane);
    }
    
    // roteer vector v over een hoek theta rond deze lijn,
    // kloksgewijs wanneer je tegen de richting van de 
    // lijn inkijkt
    // theta in radialen
    public Vector3D rotateBy(Vector3D v, double theta)
    {   if (contains(v))
            return new Vector3D(v);
        if (Math.abs(Math.cos(theta) - 1) < Vector3D.NZero)
            return new Vector3D(v);
        // find a plane through v orthogonal to this line
        // plane has normalvector direction and v is on this 
        // plane, so the equation is
        // direction.x*X + direction.y*Y +
        // direction.z*Z = (direction, v)
        Plane3D oPlane = new Plane3D(direction.x, direction.y,
            direction.z, Vector3D.dotProduct(direction, v));
        Vector3D shift = Plane3D.getIntersectionPoint(this, oPlane);     
//System.out.println("shift = " + UF.format(shift.x, 1) + 
//                   " & " +  UF.format(shift.y, 1) + 
//                   " & " +  UF.format(shift.z, 1)); 
        
        Vector3D newV = Vector3D.minus(v, shift);
        // now we rotate newV around the line through (0,0,0)
        // and this.direction
        // newV will lie in the following plane
        Plane3D zeroPlane = new Plane3D(direction.x, direction.y,
            direction.z, 0);
//System.out.println("zeroSup = " + UF.format(zeroPlane.support.x, 1) + 
//                   " & " +  UF.format(zeroPlane.support.y, 1) + 
//                   " & " +  UF.format(zeroPlane.support.z, 1)); 
//System.out.println("zeroDir1 = " + UF.format(zeroPlane.direction1.x, 1) + 
//                   " & " +  UF.format(zeroPlane.direction1.y, 1) + 
//                   " & " +  UF.format(zeroPlane.direction1.z, 1)); 
//System.out.println("zeroDir2 = " + UF.format(zeroPlane.direction2.x, 1) + 
//                   " & " +  UF.format(zeroPlane.direction2.y, 1) + 
//                   " & " +  UF.format(zeroPlane.direction2.z, 1)); 
            
            
        // and that is where the rotated vector must be sought    
        Vector3D newVUnit = new Vector3D(newV);
        double newVLength = Vector3D.length(newV);
//System.out.println("newVLength = " + newVLength);        
        Vector3D.makeUnitary(newVUnit);
//System.out.println("newVUnitLength = " + Vector3D.length(newVUnit));                
//System.out.println("newVUnit = " + UF.format(newVUnit.x, 1) + 
//                   " & " +  UF.format(newVUnit.y, 1) + 
//                   " & " +  UF.format(newVUnit.z, 1)); 
        
        // now find a vector rotV satisfying
        // 1) rotV is in zeroPlane
        // 2) length(rotV) = 1;
        // 3) angle(newVUnit, rotV)=theta
        // thus:
        // 1) direction.x*rotV.x + direction.y*rotV.y + direction.z*rotV.z = 0
        // 2) ||rotV|| = 1
        // 3) cos(theta)=(newVUnit, rotV)/(||newVUnit||*||rotV||)
        // i.e. cos(theta)=(newVUnit, rotV)=
        // newVUnit.x*rotV.x+newVUnit.y*rotV.y+newVUnit.z*rotV.z=cos(theta)
        // so basically we are intersecting the plane zeroPlane
        // with the plane with equation
        // newVUnit.x*X+newVUnit.y*Y+newVUnit.z*Z=cos(theta)
        // thus
        double cosTheta = Math.cos(theta);
//        if (Math.abs(Math.cos(theta) - 1) < Vector3D.NZero)
//            cosTheta = 0;
//System.out.println("costheta " + cosTheta);                   
        Plane3D thetaPlane = new Plane3D(newVUnit.x, newVUnit.y, 
            newVUnit.z, cosTheta);
//System.out.println("isType = " + Plane3D.intersectionType(zeroPlane, thetaPlane));            
        Line3D rotVLine = Plane3D.getIntersectionLine(zeroPlane, thetaPlane);
//System.out.println("rotVLineS = " + UF.format(rotVLine.support.x, 1) + 
//                   " & " +  UF.format(rotVLine.support.y, 1) + 
//                   " & " +  UF.format(rotVLine.support.z, 1)); 
//System.out.println("rotVLineD = " + UF.format(rotVLine.direction.x, 1) + 
//                   " & " +  UF.format(rotVLine.direction.y, 1) + 
//                   " & " +  UF.format(rotVLine.direction.z, 1)); 
        
        // vectors on this line are of the form
        // rotVLine.support + lambda*rotVLine.direction
        // and we need lambda such that
        // ||rotVLine.support + lambda*rotVLine.direction||=1
        // thus solve: (rotVLine.support,rotVLine.support)+
        // 2*lambda*(rotVLine.support,rotVLine.direction)+
        // lambda^2*(rotVLine.direction,rotVLine.direction)=1
        double supsup = Vector3D.dotProduct(rotVLine.support,rotVLine.support);
        double supdir = Vector3D.dotProduct(rotVLine.support,rotVLine.direction);        
        double dirdir = Vector3D.dotProduct(rotVLine.direction,rotVLine.direction);                
        // lambda^2*dirdir+2*lambda*supdir+supsup=1
        // i.e. lambda^2*dirdir+2*lambda*supdir+supsup-1=0
        double sqrRoot = Math.sqrt(supdir*supdir - dirdir*(supsup-1));
        double lambda1 = (- supdir + sqrRoot) / dirdir;
        double lambda2 = (- supdir - sqrRoot) / dirdir;
//System.out.println("l-1 = " + UF.format(lambda1, 2));
//System.out.println("l-2 = " + UF.format(lambda2, 2));
        // two vectors
        Vector3D rotV1 = new Vector3D(rotVLine.direction);
        Vector3D.scaleBy(rotV1, lambda1);
        rotV1 = Vector3D.plus(rotV1, rotVLine.support);
//System.out.println("rotV1 = " + UF.format(rotV1.x, 1) + 
//                   " & " +  UF.format(rotV1.y, 1) + 
//                   " & " +  UF.format(rotV1.z, 1)); 
        
        
        Vector3D rotV2 = new Vector3D(rotVLine.direction);
        Vector3D.scaleBy(rotV2, lambda2);
        rotV2 = Vector3D.plus(rotV2, rotVLine.support);
//System.out.println("rotV2 = " + UF.format(rotV2.x, 1) + 
//                   " & " +  UF.format(rotV2.y, 1) + 
//                   " & " +  UF.format(rotV2.z, 1)); 
        
        
        // if we are rotating clockwise when looking agains direction
        // we need newVUnit crossproduct rotV to be a negative
        // multiple of direction, or
        int ppCross = zeroPlane.planePosition(
            Vector3D.crossProduct(newVUnit, rotV1));
        int ppDir = zeroPlane.planePosition(direction);
        Vector3D rotV; 
        // we can have -1 and 1 or both -1 or 1
        if (ppCross == ppDir)
        {   if (theta < 0)
                rotV = new Vector3D(rotV1);
            else
                rotV = new Vector3D(rotV2);
        }    
        else
        {   if (theta < 0)
                rotV = new Vector3D(rotV2);        
            else
                rotV = new Vector3D(rotV1);
        }    
        // ||rotV|| = 1, rescale to length of newV
        Vector3D.scaleBy(rotV, newVLength);
//System.out.println("rotVLength = " + Vector3D.length(rotV));        
        // shift back
        return Vector3D.plus(rotV, shift);
        
    }
    // tijdelijk
    public String toString()
    {
        return "line from " + UF.format(point1.x, 1) + " & " +
                              UF.format(point1.y, 1) + " & " +
                              UF.format(point1.z, 1) + " to " +
                              UF.format(point2.x, 1) + " & " +
                              UF.format(point2.y, 1) + " & " +
                              UF.format(point2.z, 1);
    }
    
}  // class Line3D  

// polygon in real plane
class Polygon2D
{   double[] xpoints;
    double[] ypoints;
    int npoints;
    double baX, baY; // barycenter coordinates
    
    public Polygon2D(double[] xpts, double[] ypts, int npts) 
    {   xpoints = xpts;
        ypoints = ypts;
        npoints = npts;
    }
    
    boolean equals(double p1X, double p1Y, double p2X, double p2Y)
    {   return Math.sqrt((p1X - p2X) * (p1X - p2X) +
                         (p1Y - p2Y) * (p1Y - p2Y)) < Vector3D.NZero;
    }
    
    void findBarycenter()
    {   baX = 0;
        baY = 0;
        for (int i = 0; i < npoints; i++)
        {   baX += xpoints[i];
            baY += ypoints[i];
        }    
        baX /= npoints;
        baY /= npoints;
    }    
    
    
    // this MOET een vlakje zijn
    public boolean isInternal(double x, double y) 
    {
        
// later: sneller        
//        if (getBoundingBox().inside(x, y)) 
//        {
            findBarycenter();
            
            boolean result = true;
            
            // Walk the edges of the polygon
            for (int n = 0; n < npoints; n++) 
            {
                double xStart =  xpoints[n];
                double yStart = ypoints[n];
                double xEnd = xpoints[(n + 1) % npoints];
                double yEnd = ypoints[(n + 1) % npoints];
                
                // line through start and end has directional
                // vector (xEnd-xStart, yEnd-yStart)
                // thus normal vector 
                // (yEnd-yStart, -(xEnd-xStart)) when (xEnd-xStart) not 0
                // (-(yEnd-yStart), xEnd-xStart) when (yEnd-yStart) not 0
                double dx = xEnd-xStart;
                double dy = yEnd-yStart;
                double nX = 0;
                double nY = 0;
                if (Math.abs(dx) > Vector3D.NZero)
                {   nX = dy;
                    nY = -dx;
                }
                else // points supposed different
                {   nX = -dy;
                    nY = dx;
                }
                // equation of the line is
                // nX * X + nY * Y = nX * xStart + nY * yStart
                // the position of the barycenter relative to the
                // line is given by the double(!)
                // nX * baX + nY * baY - nX * xStart - nY * yStart
                // which is either > 0 or < 0
                double baPos = nX * baX + nY * baY 
                             - nX * xStart - nY * yStart;
                
                double pointPos = 
                    nX * x + nY * y - nX * xStart - nY * yStart;
                    
                result = result && 
                   (
                    (
                     ((baPos > Vector3D.NZero) && (pointPos > Vector3D.NZero)) ||
                     ((baPos < -Vector3D.NZero) && (pointPos < -Vector3D.NZero))
                    )
                   );
                // exit if (x,y) not on same side of ba for some edge    
                if (!result)
                    return false;
            } // for

            return result;
            
//        } // if (getBoundingBox().inside(x, y))
    }
    
    public boolean isOutside(int edgeIndex, Polygon2D pB)
    {   double xStart =  xpoints[edgeIndex];
        double yStart = ypoints[edgeIndex];
        double xEnd = xpoints[(edgeIndex + 1) % npoints];
        double yEnd = ypoints[(edgeIndex + 1) % npoints];
                
        // line through start and end has directional
        // vector (xEnd-xStart, yEnd-yStart)
        // thus normal vector 
        // (yEnd-yStart, -(xEnd-xStart)) when (xEnd-xStart) not 0
        // (-(yEnd-yStart), xEnd-xStart) when (yEnd-yStart) not 0
        double dx = xEnd-xStart;
        double dy = yEnd-yStart;
        double nX = 0;
        double nY = 0;
        if (Math.abs(dx) > Vector3D.NZero)
        {   nX = dy;
            nY = -dx;
        }
        else // points supposed different
        {   nX = -dy;
            nY = dx;
        }
        // equation of the line is
        // nX * X + nY * Y = nX * xStart + nY * yStart
        // the position of the barycenter relative to the
        // line is given by the double(!)
        // nX * baX + nY * baY - nX * xStart - nY * yStart
        // which is either > 0 or < 0
        double baPos = nX * baX + nY * baY 
                     - nX * xStart - nY * yStart;
        boolean result = true;             
        for (int i = 0; i < pB.npoints; i++)
        {
            double pointPos = nX * pB.xpoints[i] + nY * pB.ypoints[i]
                            - nX * xStart - nY * yStart;

            result = result &&
                     ((baPos > Vector3D.NZero) && 
                      (pointPos <= Vector3D.NZero)) ||
                     ((baPos < -Vector3D.NZero) && 
                      (pointPos >= -Vector3D.NZero));
            if (!result)
                return false;    
        }
        return result;
    }

    
    public boolean isOnOneSide(int edgeIndex, Polygon2D pB)
    {   double xStart =  xpoints[edgeIndex];
        double yStart = ypoints[edgeIndex];
        double xEnd = xpoints[(edgeIndex + 1) % npoints];
        double yEnd = ypoints[(edgeIndex + 1) % npoints];
                
        // line through start and end has directional
        // vector (xEnd-xStart, yEnd-yStart)
        // thus normal vector 
        // (yEnd-yStart, -(xEnd-xStart)) when (xEnd-xStart) not 0
        // (-(yEnd-yStart), xEnd-xStart) when (yEnd-yStart) not 0
        double dx = xEnd-xStart;
        double dy = yEnd-yStart;
        double nX = 0;
        double nY = 0;
        if (Math.abs(dx) > Vector3D.NZero)
        {   nX = dy;
            nY = -dx;
        }
        else // points supposed different
        {   nX = -dy;
            nY = dx;
        }
        // equation of the line is
        // nX * X + nY * Y = nX * xStart + nY * yStart
        // the position of the first point of pB relative to the
        // line is given by the double(!)
        // nX * pB.xpoints[0] + nY * pB.ypoints[0]
        //  - nX * xStart - nY * yStart;
        // which is either > 0 or < 0
        double firstPointPos = nX * pB.xpoints[0] + nY * pB.ypoints[0]
                             - nX * xStart - nY * yStart;
                     
        boolean result = true;             
        for (int i = 1; i < pB.npoints; i++)
        {
            double pointPos = nX * pB.xpoints[i] + nY * pB.ypoints[i]
                            - nX * xStart - nY * yStart;

            result = result &&
                     ((firstPointPos > -Vector3D.NZero) && 
                      (pointPos > -Vector3D.NZero)) ||
                     ((firstPointPos < Vector3D.NZero) && 
                      (pointPos < Vector3D.NZero));
            if (!result)
                return false;    
        }
        return result;
    }

    public boolean areOnOneSide(int edgeIndex, 
            double firstX, double firstY,
            double nextX, double nextY)
    {   double xStart =  xpoints[edgeIndex];
        double yStart = ypoints[edgeIndex];
        double xEnd = xpoints[(edgeIndex + 1) % npoints];
        double yEnd = ypoints[(edgeIndex + 1) % npoints];
                
        // line through start and end has directional
        // vector (xEnd-xStart, yEnd-yStart)
        // thus normal vector 
        // (yEnd-yStart, -(xEnd-xStart)) when (xEnd-xStart) not 0
        // (-(yEnd-yStart), xEnd-xStart) when (yEnd-yStart) not 0
        double dx = xEnd-xStart;
        double dy = yEnd-yStart;
        double nX = 0;
        double nY = 0;
        if (Math.abs(dx) > Vector3D.NZero)
        {   nX = dy;
            nY = -dx;
        }
        else // points supposed different
        {   nX = -dy;
            nY = dx;
        }
        // equation of the line is
        // nX * X + nY * Y = nX * xStart + nY * yStart
        // the position of the first point of pB relative to the
        // line is given by the double(!)
        // nX * pB.xpoints[0] + nY * pB.ypoints[0]
        //  - nX * xStart - nY * yStart;
        // which is either > 0 or < 0
        double firstPointPos = nX * firstX + nY * firstY
                             - nX * xStart - nY * yStart;
        double nextPointPos = nX * nextX + nY * nextY
                            - nX * xStart - nY * yStart;
                     
        boolean result = ((firstPointPos > -Vector3D.NZero) && 
                          (nextPointPos > -Vector3D.NZero)) ||
                         ((firstPointPos < Vector3D.NZero) && 
                          (nextPointPos < Vector3D.NZero));
        return result;
    }
    
    // 
    public boolean isSeparatedFrom(Polygon2D pB, boolean firstTry)
    {   // this (pA) een vlakje
        if (npoints >= 3) // pB vlakje of segment
        {    findBarycenter();
            // voldoende met 1 edge
            for (int eCnt = 0; eCnt < npoints; eCnt++)
            {   boolean outSideE = isOutside(eCnt, pB);
                if (outSideE)
                    return true;
            } // for
            // false result here
            if (firstTry)
                return pB.isSeparatedFrom(this, false);
            else
                return false;
        } // if (npoints >= 3)
        // pA een segment
        else if (npoints == 2)
        {   // pB een vlakje, use only one side
            if (pB.npoints >= 3)
            {   boolean result = isOnOneSide(0, pB);
                if (result)
                    return result;
                else
                {   if (firstTry)
                        return pB.isSeparatedFrom(this, false);
                    else
                        return false;
                }    
            }
            // pB ook een segment
            else if (pB.npoints == 2)
            {   
                
                // kijk of de segmenten raken
                if (equals(xpoints[0], ypoints[0],
                           pB.xpoints[0], pB.ypoints[0]) || 
                    equals(xpoints[0], ypoints[0],
                           pB.xpoints[1], pB.ypoints[1]) || 
                    equals(xpoints[1], ypoints[1],
                           pB.xpoints[0], pB.ypoints[0]) || 
                    equals(xpoints[1], ypoints[1],
                           pB.xpoints[1], pB.ypoints[1])
                   )
                    return false;                
                    
                    
                boolean result = areOnOneSide(0,
                    pB.xpoints[0], pB.ypoints[0],
                    pB.xpoints[1], pB.ypoints[1]);
                if (result)    
                    return result;
                else
                {   if (firstTry)
                        return pB.isSeparatedFrom(this, false);
                    else    
                        return false;
                }    
                
            }
        }    
        return false;
        
        
    }
} // class Polygon2D  
    