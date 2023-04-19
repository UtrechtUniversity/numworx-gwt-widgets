package fi.nabouwenaanzichtengwt.client;

/**
 * een punt in 3d-space met double coordinaten 
 */

public class Punt3D

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
