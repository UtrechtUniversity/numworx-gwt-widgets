package fi.doorziengwt.client;

/**
 * a line in 3-space, see class Line3D in Grafiek3DGWT 
 * @author huub
 */

public class Line3D 
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
        
        Vector3D newV = Vector3D.minus(v, shift);
        // now we rotate newV around the line through (0,0,0)
        // and this.direction
        // newV will lie in the following plane
        Plane3D zeroPlane = new Plane3D(direction.x, direction.y,
            direction.z, 0);
            
        // and that is where the rotated vector must be sought    
        Vector3D newVUnit = new Vector3D(newV);
        double newVLength = Vector3D.length(newV);
        
        Vector3D.makeUnitary(newVUnit);
        
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
        Plane3D thetaPlane = new Plane3D(newVUnit.x, newVUnit.y, 
            newVUnit.z, cosTheta);
            
        Line3D rotVLine = Plane3D.getIntersectionLine(zeroPlane, thetaPlane);
        
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
        // two vectors
        Vector3D rotV1 = new Vector3D(rotVLine.direction);
        Vector3D.scaleBy(rotV1, lambda1);
        rotV1 = Vector3D.plus(rotV1, rotVLine.support);
        
        Vector3D rotV2 = new Vector3D(rotVLine.direction);
        Vector3D.scaleBy(rotV2, lambda2);
        rotV2 = Vector3D.plus(rotV2, rotVLine.support);
        
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

