package fi.kladjegwt.client;

/**
 * een rechthoek in het vlak (GWT heeft geen klasse Rectangle) 
 * @author huub
 */
public class Rectangle 
{
	/**
	 * afmetingen van de rechthoek
	 */
	int x; int y; int width; int height;
	
	/**
	 * constructor
	 * @param x x-coordinaat linksboven
	 * @param y y-coordinaat linksboven
	 * @param w breedte
	 * @param h hoogte
	 */
	public Rectangle(int x, int y, int w, int h)
	{
		this.x = x; this.y = y;
		width = w; height = h;
	}
	
	/**
	 * constructor, kopieert de rechthoek r
	 * @param r te kopieren rechthoek 
	 */
	public Rectangle(Rectangle r)
	{
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
	}
	
	/**
	 * check of deze rechthoek het punt (px,py) bevat
	 * @param px x-coordinaat te checken punt
	 * @param py y-coordinaat te checken punt
	 * @return true/false
	 */
	public boolean contains(int px, int py)
	{
		return (px >= x) && (px <= (x + width)) &&
		       (py >= y) && (py <= (y + height));
	}
	
	/**
	 * verschuif (transleer) deze rechthoek over de vector (dx,dy)
	 * @param dx x-translatie
	 * @param dy y-translatie
	 */
	public void translate(int dx, int dy)
	{
		x += dx;
		y += dy;
	}

}
