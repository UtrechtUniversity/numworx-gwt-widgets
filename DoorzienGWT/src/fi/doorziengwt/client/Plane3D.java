package fi.doorziengwt.client;

/**
 * a plane in 3-space, see class Plane3D in Grafiek3DGWT
 * @author huub
 */

public class Plane3D 
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

