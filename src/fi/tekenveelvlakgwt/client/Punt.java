package fi.tekenveelvlakgwt.client;

/**
 * een punt in het vlak met double coordinaten 
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

