package fi.weblogogwt.client;

/**
 * there is no class Point in GWT 
 */
public class Point 
{
	int x, y;

	Point()
	{
	}
	Point(int x, int y)
	{	this.x = x;
		this.y = y;
	}
		
	Point(Point p)
	{	this.x = p.x;
		this.y = p.y;
	}
	
	public void translate(int dx, int dy)
	{
		x += dx;
		y += dy;
	}
}

