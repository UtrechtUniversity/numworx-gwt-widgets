package fi.doorziengwt.client;

/**
 * class representing an affine transformation; see class Matrix3D in Grafiek3DGWT
 * @author huub
 */

public class Matrix3D 
{   
	    // public for easy access
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

