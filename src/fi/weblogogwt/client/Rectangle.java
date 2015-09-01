package fi.weblogogwt.client;

public class Rectangle 
{
	int x; int y; int width; int height;
	
	public Rectangle(int x, int y, int w, int h)
	{
		this.x = x; this.y = y;
		width = w; height = h;
	}
	
	public Rectangle(Rectangle r)
	{
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
	}
	
	public boolean contains(int px, int py)
	{
		return (px >= x) && (px <= (x + width)) &&
		       (py >= y) && (py <= (y + height));
	}
	
	public void translate(int dx, int dy)
	{
		x += dx;
		y += dy;
	}
	
	public boolean intersects(Rectangle r)
	{
		int cCnt = 0;
		if (r.contains(x,y))
			cCnt++;
		if (r.contains(x+width,y))
			cCnt++;	
		if (r.contains(x+width,y+height))
			cCnt++;	
		if (r.contains(x,y+height))
			cCnt++;	

		if ((cCnt == 0) || (cCnt == 4))
			return false;
		else
			return true;
		
	}
	
	public void setLocation(int newX, int newY)
	{
		x = newX; y = newY;
	}
}