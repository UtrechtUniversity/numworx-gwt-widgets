package fi.heksgwt.client.vectortek;

public class Point 
{
	public int x, y;
	
	public Point(int x, int y)
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
