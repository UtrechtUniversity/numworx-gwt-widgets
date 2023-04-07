package fi.doorziengwt.client;

/**
 * real points in the plane, see class Vector2D in Grafiek3DGWT 
 * @author huub
 */
public class Vector2D
{   public double x;
    public double y;
    public Vector2D(double x, double y)
    {   this.x = x;
        this.y = y;
    }    
    public double distance(Vector2D v)
    {   return Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y));
    }    
}
