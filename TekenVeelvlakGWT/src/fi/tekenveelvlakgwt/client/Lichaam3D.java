package fi.tekenveelvlakgwt.client;

import com.google.gwt.canvas.dom.client.CssColor;

/**
 * klasse die een 3D configuratie bestaande uit 3d-polygons (zie klasse
 * Polygon3D) representeert; tijdens het tekenen worden 3d-punten toegevoegd aan 
 * de arrays met coordinaten; aanroepen van methode VoegPolygonToe voegt al deze
 * punten toe aan een Polygon3D en maakt de arrays met coordinaten weer leeg.
 */

public class Lichaam3D 
{
	/**
	 * integer x-coordinaten van de punten
	 */
	public int[] xcoor;
	/**
	 * integer y-coordinaten van de punten
	 */
	public int[] ycoor;
	/**
	 * integer z-coordinaten van de punten
	 */
	public int[] zcoor;
	/**
	 * double x-coordinaten van de punten
	 */
	public double[] xcoord;
	/**
	 * double y-coordinaten van de punten
	 */
	public double[] ycoord;
	/**
	 * double z-coordinaten van de punten
	 */
	public double[] zcoord;
	/**
	 * index-array gebruikt in methode sorteer()
	 */
	public int[] sorteerRij;

	/**
	 * alle 3d-polygons in dit Lichaam3D 
	 */
	public Polygon3D[] vlakken;

	/**
	 * actuele aantal punten 
	 */
	public int aantalPunten;
	/**
	 * actuele aantal 3d-polygons
	 */
	public int aantalPolygonen;
	/**
	 * een tijdelijk 3d-polygon tijdens constructir
	 */
	private Polygon3D huidigePolygon;
	/**
	 * de projectie factor voor projectie vanuit het oog op de z-as 
	 * met coordianten (0,0,afstand)  op het x-y-vlak
	 */
	private double pf;
	/**
	 * z-coordinaat van het oog op de z-as
	 */
	public double afstand;
	/**
	 * oorsprong van het coordinaten-systeem
	 */
	Punt3D nulpunt;

	/**
	 * constructor, initialiseer arrays, nulpunt en afstand 
	 */
	public Lichaam3D()
	{	xcoor = new int[2000];
		ycoor = new int[2000];
		zcoor = new int[2000];
		xcoord = new double[2000];
		ycoord = new double[2000];
		zcoord = new double[2000];

		vlakken = new Polygon3D[2000];
		sorteerRij = new int[200];
		aantalPunten = 0;
		aantalPolygonen = 0;
		nulpunt = new Punt3D(0,0,0);
		afstand = 1000;
	}
	/**
	 * zet de afstand
	 * @param afst nieuwe waarde voor de afstand
	 */
	public void zetAfstand(double afst)
	{	afstand = afst;
	}
	
	/**
	 * zet een nieuwe oorsprong
	 * @param x nieuwe x-coordinaat
	 * @param y nieuwe y-coordinaat
	 * @param z nieuwe z-coordinaat
	 */
	public void maakNulpunt(double x,double y,double z)
	{	nulpunt.x = x;
		nulpunt.y = y;
		nulpunt.z = z;
	}
	
	/**
	 * voeg een 3d-punt toe, als x- en y-coordinaat save de 
	 * projectie op het x-y-vlak 
	 * @param p nieuw 3d-punt
	 */
	public void voegPuntToe(Punt3D p)
	{	pf = (afstand-p.z)/afstand;
		xcoord[aantalPunten] = nulpunt.x + (p.x-nulpunt.x)/pf;
		ycoord[aantalPunten] = nulpunt.y + (p.y-nulpunt.y)/pf;
		zcoord[aantalPunten] = p.z;
		xcoor[aantalPunten] = (int)xcoord[aantalPunten];
		ycoor[aantalPunten] = (int)ycoord[aantalPunten];
		zcoor[aantalPunten] = (int)p.z;

		aantalPunten++;
	}
	
	/**
	 * maak van alle punten in de arrays met coordinaten een Polygon3D en
	 * maak de arrays met coordinaten leeg;
	 * @param vulkl vulkleur
	 * @param lijnkl kleur van de omlijning
	 * @param isOmlnd moet de omlijning getekend worden?
	 * @param isLg is dit 3d-Polygon leeg?
	 */
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

	/**
	 * bubble sort de 3d-polygons op gemiddelde z-waarde van de punten
	 */
	public void sorteer()
	{	
		for (int i = 0; i < 200 ; i++)
		{	sorteerRij[i] = i;
		}
		for (int j = 0; j < aantalPolygonen; j++)
		{
			for(int i = j+1; i < aantalPolygonen; i++)
			{
				if (vlakken[j].gemz > vlakken[i].gemz)
				{	int res = sorteerRij[j];
					sorteerRij[j] = sorteerRij[i];
					sorteerRij[i] = res;
					huidigePolygon = vlakken[j];
					vlakken[j] = vlakken[i];
					vlakken[i] = huidigePolygon;
				}
			}
		}
	}
}