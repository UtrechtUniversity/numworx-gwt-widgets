package fi.weblogo3dgwt.client.logotekenap3d;

import com.google.gwt.canvas.dom.client.CssColor;

/**
 * class representing a 3D configuration consisting of 3d-polygons (see class
 * Polygon3D); during drawing points in 3d-space are added to arrays of coordinates,
 * then calling method VoegPolygonToe turns these points into a Polygon3D and
 * resets the coordinate arrays; for adding lines or a cursor during drawing,
 * add the line or cursor directly as a Polygon3D, in this way not interrupting
 * the collection of points in the coordinate arrays.
 */
public class Lichaam3D
{
	/**
	 * integer x-coordinates
	 */
	public int[] xcoor;
	/**
	 * integer y-coordinates
	 */
	public int[] ycoor;
	/**
	 * integer z-coordinates
	 */
	public int[] zcoor;
	/**
	 * double x-coordinates
	 */
	public double[] xcoord;
	/**
	 * double y-coordinates
	 */
	public double[] ycoord;
	/**
	 * double z-coordinates
	 */
	public double[] zcoord;

	/**
	 * all 3d-polygons in this Lichaam3D 
	 */
	public Polygon3D[] vlakken;
	/**
	 * additional Polygon3D array for bubble sort
	 */
	Polygon3D[] vlakkenSort;
	
	/**
	 * the current number of points in the coordinate arrays
	 */
	public int aantalPunten;
	/**
	 * the current number of Polygon3D
	 */
	public int aantalPolygonen;
	
	/**
	 * a temporary Polygon3D being constructed
	 */
	private Polygon3D huidigePolygon;
	/**
	 * the projection factor for projecting from the eye on the z-axis
	 * (0,0,1000) onto the x-y-plane
	 */
	private double pf;
	/**
	 * origin of x-y-z coordinate system  
	 */
	Punt3D nulpunt;

	/**
	 * constructor, initialize arrays and nulpunt 
	 */
	public Lichaam3D()
	{	xcoor = new int[1000];
		ycoor = new int[1000];
		zcoor = new int[1000];
		xcoord = new double[1000];
		ycoord = new double[1000];
		zcoord = new double[1000];

		vlakken = new Polygon3D[1000];
		aantalPunten = 0;
		aantalPolygonen = 0;
		nulpunt = new Punt3D(0,0,0);
	}
	
	/**
	 * set a new origin
	 * @param x new x-coordinate
	 * @param y new y-coordinate
	 * @param z new z-coordinate
	 */
	public void maakNulpunt(double x,double y,double z)
	{	nulpunt.x = x;
		nulpunt.y = y;
		nulpunt.z = z;
	}
	
	/**
	 * add a 3d-point, as x- and y-coordinate save its 
	 * projection on the x-y-plane 
	 * @param p 3d point to add
	 */
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

	/**
	 * create and add a Polygon3D representing a line between two 3d-points
	 * @param p1 line starts at 3d-point p1
	 * @param p2 line ends at 3d-point p2
	 * @param lijnkl the line color
	 * @param vulkl the fill color (ignored)
	 */
	public void voegLijnToe(Punt3D p1, Punt3D p2, CssColor lijnkl, CssColor vulkl)
	{
//System.out.println("voegLijnToe");
		
		double pf1 = (1000-p1.z)/1000;
		double pf2 = (1000-p2.z)/1000;
		double[] lxd = new double[2];
		double[] lyd = new double[2];
		int[] lx = new int[2];
		int[] ly = new int[2];
		lxd[0] = nulpunt.x + (p1.x-nulpunt.x)/pf1;
		lxd[1] = nulpunt.x + (p2.x-nulpunt.x)/pf2;
		lyd[0] = nulpunt.y + (p1.y-nulpunt.y)/pf1;
		lyd[1] = nulpunt.y + (p2.y-nulpunt.y)/pf2;
		lx[0] = (int) lxd[0];
		lx[1] = (int) lxd[1];
		ly[0] = (int) lyd[0];
		ly[1] = (int) lyd[1];
		double gz = p1.z + p2.z;
		huidigePolygon = new Polygon3D();
		huidigePolygon.pol = new Polygon(lx,ly,2);
		huidigePolygon.normaal = new Punt3D(0,0,1);
		huidigePolygon.isLijn = true;
		huidigePolygon.gemz = gz/aantalPunten;
		huidigePolygon.vulkleur = vulkl;
		huidigePolygon.lijnkleur = lijnkl;
		vlakken[aantalPolygonen] = huidigePolygon;
		aantalPolygonen++;
		
	}

	/**
	 * create and add a Polygon3D representing a 3d-cursor, see class TekenBlad3D
	 * @param cursorPunten the 3d-points making up the cursor
	 * @param lijnkl the outline color of the cursor
	 * @param vulkl the fill color of the cursor
	 */
	public void voegCursorToe(Punt3D[] cursorPunten, CssColor lijnkl, CssColor vulkl)
	{
		
//System.out.println("voegCursorToe");

		double[] pf = new double[cursorPunten.length];
		double[] cxd = new double[cursorPunten.length];
		double[] cyd = new double[cursorPunten.length];
		double[] czd = new double[cursorPunten.length];
		int[] cx = new int[cursorPunten.length];
		int[] cy = new int[cursorPunten.length];
		double gz = 0;
		for (int pCnt = 0; pCnt < cursorPunten.length; pCnt++)
		{	pf[pCnt] = (1000-cursorPunten[pCnt].z)/1000;
			cxd[pCnt] = nulpunt.x + (cursorPunten[pCnt].x-nulpunt.x)/pf[pCnt];
			cyd[pCnt] = nulpunt.y + (cursorPunten[pCnt].y-nulpunt.y)/pf[pCnt];
			czd[pCnt] = cursorPunten[pCnt].z;
			gz += cursorPunten[pCnt].z;
			cx[pCnt] = (int) cxd[pCnt];
			cy[pCnt] = (int) cyd[pCnt];
		}

		huidigePolygon = new Polygon3D();
		huidigePolygon.pol = new Polygon(cx,cy,cursorPunten.length);
		if (cursorPunten.length < 3)
		{
			huidigePolygon.normaal = new Punt3D(0,0,1);
			huidigePolygon.isLijn = true;
		}
		else
		{	double ux = cxd[1] - cxd[0];
			double uy = cyd[1] - cyd[0];
			double uz = czd[1] - czd[0];
			double vx = cxd[2] - cxd[1];
			double vy = cyd[2] - cyd[1];
			double vz = czd[2] - czd[1];
			double nx = uy*vz - uz*vy;	
			double ny = uz*vx - ux*vz;
			double nz = ux*vy - uy*vx;
			double ln = Math.sqrt(nx*nx + ny*ny + nz*nz);
			double nex = nx/ln;
			double ney = ny/ln;
			double nez = nz/ln;
			huidigePolygon.normaal = new Punt3D(nex,ney,nez);
		}
		huidigePolygon.gemz = gz/cursorPunten.length;
		huidigePolygon.vulkleur = vulkl;
		huidigePolygon.lijnkleur = lijnkl;
		huidigePolygon.isOmlijnd = true;
		huidigePolygon.naam = "cursor";
		vlakken[aantalPolygonen] = huidigePolygon;
		aantalPolygonen++;
		
	}

	/**
	 * turns the points saved in the coordinate arrays into a Polygon3D and
	 * reset the coordinate arrays;
	 * @param vulkl fill color
	 * @param lijnkl outline color
	 * @param isOmlnd should the outline be drawn
	 */
	public void voegPolygonToe(CssColor vulkl, CssColor lijnkl, boolean isOmlnd )
	{	
//System.out.println("voegPolygonToe");

		huidigePolygon = new Polygon3D();
		huidigePolygon.pol = new Polygon(xcoor,ycoor,aantalPunten);
		if (aantalPunten < 3)
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
		for (int i = 0; i < aantalPunten; i++)
		{	gz = gz + zcoor[i];
		}
		huidigePolygon.gemz = gz/aantalPunten;
		huidigePolygon.vulkleur = vulkl;
		huidigePolygon.lijnkleur = lijnkl;
		huidigePolygon.isOmlijnd = isOmlnd;
		aantalPunten = 0;
		vlakken[aantalPolygonen] = huidigePolygon;
		aantalPolygonen++;
	}

	/**
	 * bubble sort the 3d-polygons on average z-value
	 */
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
