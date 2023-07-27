package fi.draaibankgwt.client;

class Punt3D

{	double x, y, z;
		
	Punt3D(double x, double y,double z)
	{	this.x = x;
		this.y = y;
		this.z = z;
	}
		
	Punt3D(Punt3D p)
	{	this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}
}
