package fi.kladjegwt.client;

/**
 * een punt in het vlak met gehele coordinaten (GWT heeft geen klasse Point)  
 * @author huub
 */
public class Point 
{
	/**
	 * coordinaten van het punt
	 */
	int x; int y;
	
	/**
	 * constructor
	 * @param x x-coordinaat
	 * @param y y-coordinaat
	 */
	public Point(int x, int y)
	{
		this.x = x; this.y = y;
	}

}
