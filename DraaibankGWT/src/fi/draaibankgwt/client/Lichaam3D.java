package fi.draaibankgwt.client;

//import java.awt.Color;
//import java.awt.Polygon;

import com.google.gwt.canvas.dom.client.CssColor;

class Lichaam3D
{
	public int[] xcoor;
	public int[] ycoor;
	public int[] zcoor;
	public double[] xcoord;
	public double[] ycoord;
	public double[] zcoord;

	public Polygon3D[] vlakken, vlakkenSort;
	public int aantalPunten, aantalPolygonen;
	private Polygon3D huidigePolygon;
	private double pf;
	Punt3D nulpunt;

	public Lichaam3D()
	{	xcoor = new int[20];
		ycoor = new int[20];
		zcoor = new int[20];
		xcoord = new double[20];
		ycoord = new double[20];
		zcoord = new double[20];

		vlakken = new Polygon3D[1000];
		aantalPunten = 0;
		aantalPolygonen = 0;
		nulpunt = new Punt3D(0,0,0);
	}
	public void maakNulpunt(double x,double y,double z)
	{	nulpunt.x = x;
		nulpunt.y = y;
		nulpunt.z = z;
	}
	
	public void voegPuntToe(Punt3D p)
	{	pf = (1000-p.z)/1000;
		xcoord[aantalPunten] = nulpunt.x + (p.x-nulpunt.x)/pf;
		ycoord[aantalPunten] = nulpunt.y + (p.y-nulpunt.y)/pf;
		zcoord[aantalPunten] = p.z;
		xcoor[aantalPunten] = (int)xcoord[aantalPunten];
		ycoor[aantalPunten] = (int)ycoord[aantalPunten];
		zcoor[aantalPunten] = (int)p.z;

		aantalPunten++;
	}
	
	public void voegPolygonToe(CssColor vulkl, CssColor lijnkl, boolean isOmlnd, boolean isLg )
	{	huidigePolygon = new Polygon3D();
		huidigePolygon.pol = new Polygon(xcoor,ycoor,aantalPunten);
		if(aantalPunten<3)
		{
			huidigePolygon.normaal = new Punt3D(0,0,1);
			huidigePolygon.isLijn = true;
		}
		else
		{	double ux = xcoord[1] - xcoord[0];
			double uy = ycoord[1] - ycoord[0];
			double uz = zcoord[1] - zcoord[0];
			double vx = xcoord[2] - xcoord[1];
			double vy = ycoord[2] - ycoord[1];
			double vz = zcoord[2] - zcoord[1];
			double nx = uy*vz - uz*vy;	
			double ny = uz*vx - ux*vz;
			double nz = ux*vy - uy*vx;
			double ln = Math.sqrt(nx*nx + ny*ny + nz*nz);
			double nex = nx/ln;
			double ney = ny/ln;
			double nez = nz/ln;
			huidigePolygon.normaal = new Punt3D(nex,ney,nez);
		}
		double gz = 0;
		for(int i=0 ; i<aantalPunten ; i++)
		{	gz = gz + zcoor[i];
		}
		huidigePolygon.gemz = gz/aantalPunten;
		huidigePolygon.vulkleur = vulkl;
		huidigePolygon.lijnkleur = lijnkl;
		huidigePolygon.isOmlijnd = isOmlnd;
		huidigePolygon.isLeeg = isLg;
		aantalPunten = 0;
		vlakken[aantalPolygonen] = huidigePolygon;
		aantalPolygonen++;
		
	}

	public void sorteer()
	{	for(int j=0 ; j<aantalPolygonen ;j++)
		{
			for(int i=j+1 ; i<aantalPolygonen ; i++)
			{
				if(vlakken[j].gemz > vlakken[i].gemz)
				{
					huidigePolygon = vlakken[j];
					vlakken[j] = vlakken[i];
					vlakken[i] = huidigePolygon;
				}
			}
		}
	}
}