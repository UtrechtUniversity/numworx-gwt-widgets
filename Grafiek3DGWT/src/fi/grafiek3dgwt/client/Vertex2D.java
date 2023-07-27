package fi.grafiek3dgwt.client;

/**
 * a real vector in the x-y-plane 
 * @author huub
 */
class Vector2D
{   public double x;
    public double y;
    public Vector2D(double x, double y)
    {   this.x = x;
        this.y = y;
    }    
    /**
     * find the distance to another Vector2D v
     * @param v the other Vector2D  
     * @return distance this Vector2D to Vector2D v
     */
    public double distance(Vector2D v)
    {   return Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y));
    }    
}
