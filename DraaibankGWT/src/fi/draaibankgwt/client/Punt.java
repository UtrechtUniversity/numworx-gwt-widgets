package fi.draaibankgwt.client;

public class Punt
{	
	double x, y;
	int intX, intY;
		
	Punt(double x, double y)
	{	this.x = x;
		this.y = y;
		intX = (int) Math.round(x);
		intY = (int) Math.round(y);
	}
		
	Punt(Punt p)
	{	this.x = p.x;
		this.y = p.y;
		intX = p.intX;
		intY = p.intY;
	}
	
	
}

