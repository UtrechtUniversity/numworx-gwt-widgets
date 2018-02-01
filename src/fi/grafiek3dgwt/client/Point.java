package fi.grafiek3dgwt.client;

public class Point 
{
	int x; int y;
	
	public Point()
	{
		x = 0; y = 0;
	}
	
	public Point(int x, int y)
	{
		this.x = x; this.y = y;
	}
	
    // redefine for method contains in Vector     
    // equality of this Point and Point obj
    public boolean equals(Object obj)
    {    if (obj instanceof Point)
             return (x == ((Point) obj).x) && (y == ((Point) obj).y);
         return false;    
    }

}
