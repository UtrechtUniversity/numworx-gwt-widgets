package fi.weblogogwt.client.expressies;

class PointLong

{	long x, y;
		
	PointLong(long x, long y)
	{	this.x = x;
		this.y = y;
		
	}
		
	PointLong(PointLong p)
	{	this.x = p.x;
		this.y = p.y;
	}
}
