package fi.weblogogwt.client.logotekenap;

/**
 * there is no class Point in GWT
 */
public class Punt
{	
	double x, y;
		
	Punt(double x, double y)
	{	this.x = x;
		this.y = y;
	}
		
	Punt(Punt p)
	{	this.x = p.x;
		this.y = p.y;
	}
}