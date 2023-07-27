package fi.kladjegwt.client;

import java.awt.Point;

/**
 * punt in het vlak met double coordinaten 
 * @author huub
 */
public class DoublePoint 
{
	/**
	 * x- en y-coordinaat of double punt
	 */
	double x; double y;
	
	/**
	 * constructor
	 * @param x x-coordinaat
	 * @param y y-coordinaat
	 */
	public DoublePoint(double x, double y) 
	{
		this.x = x; this.y = y;
	}
	
	public double distance2(DoublePoint p)
	{
		return (x-p.x)*(x-p.x)+(y-p.y)*(y-p.y);
	}
	
	
}
