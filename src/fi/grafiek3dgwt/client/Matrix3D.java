package fi.grafiek3dgwt.client;

/**
 * class containing utilities for affine transformations in 3-space,
 * that is transformations of the form Tx = Ax+t where A is a 3 by 3
 * square matrix and t a translation vector
 * @author huub
 */
class Matrix3D
{   
	/**
	 * rows of the 3 by 3 square matrix, public for easy access 
	 */
    public Vector3D row1, row2, row3;
    /** 
     * translation vector, initialized as the zero-vector
     */
    public Vector3D origin = new Vector3D(); // (0, 0, 0)
    /**
     * scaling factors for each coordinate direction 
     */
    public double scaleX = 1, scaleY = 1, scaleZ = 1;
    /**
     * general constructor, origin will be (0,0,0), all scale factors 1
     * @param t11 element at position 1,1
     * @param t12 element at position 1,2
     * @param t13 element at position 1,3
     * @param t21 element at position 2,1
     * @param t22 element at position 2,2
     * @param t23 element at position 2,3
     * @param t31 element at position 3,1
     * @param t32 element at position 3,2
     * @param t33 element at position 3,3
     */
    public Matrix3D(double t11, double t12, double t13,
              double t21, double t22, double t23,
              double t31, double t32, double t33)
    {   row1 = new Vector3D(t11, t12, t13);
        row2 = new Vector3D(t21, t22, t23);
        row3 = new Vector3D(t31, t32, t33);
    }
    /**
     * overloaded constructor: 3x3 matrix will be the identity matrix, origin (0,0,0)
     * and all scale factors 1
     */
    public Matrix3D()
    {   this(1,0,0, 0,1,0, 0,0,1);
    }
    // static methods    
    /**
     * return column i of the 3x3 matrix in Matrix3D mat as a Vector3D
     * @param i index of column to be returned
     * @param mat Matrix3D whose column i should be returned 
     * @return column i (as a Vector3D)
     */
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
    /**
     * make a deep copy of Matrix3D mat
     * @param mat Matrix3D to be hard copied
     * @return hard copy
     */
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
    /**
     * check if Matrix3D mat1 equals Matrix3D mat2, that is, the
     * coefficients of the 3 by matrices are (nearly) equal,
     * the translation vectors are (nearly) equal and the 
     * scaling factors ate (nearly) equals 
     * @param mat1 first Matrix3D
     * @param mat2 second Matrix3D
     * @return true/false
     */
    public static boolean equals(Matrix3D mat1, Matrix3D mat2)
    {   return mat1.row1.equals(mat2.row1) &&
               mat1.row2.equals(mat2.row2) && 
               mat1.row3.equals(mat2.row3) && 
               mat1.origin.equals(mat2.origin) &&
               ((mat1.scaleX - mat2.scaleX) < Vector3D.NZero) &&
               ((mat1.scaleY - mat2.scaleY) < Vector3D.NZero) &&
               ((mat1.scaleZ - mat2.scaleZ) < Vector3D.NZero);
    }

    /**
     * reset the 3 by 3 matrix of this Matrix3D to the identity matrix, 
     * leave origin and scale unchanged
     */
    public void reset()
    {   row1 = new Vector3D(1, 0, 0);
        row2 = new Vector3D(0, 1, 0);
        row3 = new Vector3D(0, 0, 1);
    }    
    /**
     * set the scaling factors of this Matrix3D
     * @param sx new value of sx
     * @param sy new value of sy
     * @param sz new value of sz
     */
    public void setScale(double sx, double sy, double sz)
    {   scaleX = sx; scaleY = sy; scaleZ = sz;
    }
    /**
     * scale this Matrix3D by (sx,sy,sz), that is multiply
     * the scaling factors by sx resp. sy resp. sz, used
     * for successive scaling 
     * @param sx scaling factor for x-direction
     * @param sy scaling factor for y-direction
     * @param sz scaling factor for z-direction
     */
    public void scaleBy(double sx, double sy, double sz)
    {   scaleX *= sx; scaleY *= sy; scaleZ *= sz;
    }
    /**
     * set the translation vector of this Matrix3D to (sx,sy,sz)   
     * @param sx new x-coordinate of translation vector
     * @param sy new y-coordinate of translation vector
     * @param sz new z-coordinate of translation vector
     */
    public void setOrigin(double sx, double sy, double sz)
    {   origin.x = sx; origin.y = sy; origin.z = sz;
    }
    /**
     * translate the translation vector by (sx,sy,sz) use this for
     * for successive translations
     * @param sx translate for x-coordinate of translation vector
     * @param sy translate for y-coordinate of translation vector
     * @param sz translate for z-coordinate of translation vector
     */
    public void translateBy(double sx, double sy, double sz)
    {   Vector3D.translateBy(origin, sx, sy, sz);
    }
    // multiply Matrix3D mat on the left by Matrix3D leftMat
    // use a deep copy of this matrix BEFORE multiplication
    // do not change the origin and the scale
    /**
     * multiply the 3 by 3 matrix of Matrix3D mat on the left by 
     * the 3 by 3 marix of Matrix3D leftMat
     * make a deep copy of mat BEFORE multiplication
     * do not change the origin and the scale of mat
     * @param mat Matrix3D to be multiplied on the left
     * @param leftMat Matrix3D use for left multiplying
     */
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
    
    /**
     * leftmultiply the 3 by 3 matrix of this Matrix3D by a theta degree rotation
     * around the x-axis, clockwise when looking from the positive x-axis
     * @param theta required rotation in degrees
     */
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
    /**
     * leftmultiply the 3 by 3 matrix of this Matrix3D by a theta degree rotation
     * around the y-axis, clockwise when looking from the positive y-axis
     * @param theta required rotation in degrees
     */
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
    /**
     * leftmultiply the 3 by 3 matrix of this Matrix3D by a theta degree rotation
     * around the z-axis, clockwise when looking from the positive z-axis
     * @param theta required rotation in degrees
     */
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
    
    /**
     * leftmultiply the 3 by 3 matrix of this Matrix3D by a rotation
     * which turns Vector3D v into a multiple of Vector3D w
     * clockwise when looking from the direction of
     * v X w (the crossproduct); assume v and w not zero-vectors
     * @param vVec Vector3D to be rotated 
     * @param wVec Vector3D  determining the target direction
     */
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

    /**
     * find the determinant of the 3 by 3 matrix with given rows (or columns!)
     * @param vec1 row 1
     * @param vec2 row 2
     * @param vec3 row 3
     * @return determinant
     */
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
    /**
     * find the inverse of the 3x3 matrix in Matrix3D mat and return
     * a Matrix3D with this inverse, translation vector (0,0,0) and all
     * scaling factors 1
     * @param mat Matrix3D whose 3 by 3 matrix should be inverted
     * @return Matrix 3d with inverse 3 by 3 matrix
     */
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
    /**
     * transforming world space to view space: given a Vector3D (point) vec,
     * leftmultiply it with the 3 by 3 matrix in this Matrix3D,
     * scale the result, then translate the result over the translation vector
     * @param vec Vector3D to be transformed
     * @return transformed Vector 3d
     */
    public Vector3D transform(Vector3D vec)
    {   Vector3D res = new Vector3D();
        res.x = Vector3D.dotProduct(row1, vec);
        res.y = Vector3D.dotProduct(row2, vec);
        res.z = Vector3D.dotProduct(row3, vec);
        Vector3D.scaleBy(res, scaleX, scaleY, scaleZ);
        Vector3D.translateBy(res, origin);
        return res;
    }
    /**
     * transforming normal vectors from world space to view space, these do not have to     
     * be translated in view space; note: do scale because in view space the y-axis is reversed
     * @param vec normal vector to be transformed
     * @return transformed normal vector
     */
    public Vector3D nTransform(Vector3D vec)
    {   Vector3D res = new Vector3D();
        res.x = Vector3D.dotProduct(row1, vec);
        res.y = Vector3D.dotProduct(row2, vec);
        res.z = Vector3D.dotProduct(row3, vec);
        Vector3D.scaleBy(res, scaleX, scaleY, scaleZ);        
        return res;
    }
    
} // class Matrix3D
