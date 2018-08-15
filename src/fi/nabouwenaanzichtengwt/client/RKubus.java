package fi.nabouwenaanzichtengwt.client;

/**
 * klasse die een kubusje representeert 
 * @author Peter Boon
 */
public class RKubus extends Veelvlak 
{
	/**
	 * lengte halve ribbe van het kubusje
	 */
	double d;
	/**
	 * isOnbedekt[i] == true als er zich naast vlakje i geen ander kubusje
	 * bevindt 
	 */
	boolean[] isOnbedekt;
	/**
	 * de vulkleur van de vlakjes van het kubusje
	 */
	String vulkleur;
	
	/**
	 * constructor
	 * @param r lengte ribbe
	 * @param x x-coordinaat centrum kubusje
	 * @param y y-coordinaat centrum kubusje
	 * @param z z-coordinaat centrum kubusje
	 */
	public RKubus(double r, double x, double y, double z)
	{	vulkleur = "geel";
		aantalHoekpunten = 8;
		aantalVlakken = 6;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		isOnbedekt = new boolean[6];
		for (int i = 0; i < 6; i++)
		{	isOnbedekt[i] = true;
		}
		d = r/2;
		hoekpunten[0] = new Hoekpunt(-d,  d, -d);
		hoekpunten[1] = new Hoekpunt( d,  d, -d);
		hoekpunten[2] = new Hoekpunt( d,  d,  d);
		hoekpunten[3] = new Hoekpunt(-d,  d,  d);
		hoekpunten[4] = new Hoekpunt(-d, -d, -d);
		hoekpunten[5] = new Hoekpunt( d, -d, -d);
		hoekpunten[6] = new Hoekpunt( d, -d,  d);
		hoekpunten[7] = new Hoekpunt(-d, -d,  d);
		
		verschuif(x,y,z);
		
		int[] vlakdata = {0,3,2,1,	0,1,5,4,	1,2,6,5,	2,3,7,6,	3,0,4,7,	4,5,6,7};
		for (int i = 0; i < aantalVlakken; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[4 * i]], hoekpunten[vlakdata[4 * i + 1]],
							 hoekpunten[vlakdata[4 * i + 2]], hoekpunten[vlakdata[4 * i + 3]]};
			vlakken[i] = new Vlak(4,vp);
			vlakken[i].vorigeKleur = vulkleur;
			vlakken[i].vulkleur = vulkleur;
		}
		
		int[] hoekpuntdata = {0,1,4,	0,2,1,	0,3,2,	0,4,3,	5,4,1,	5,1,2,	5,2,3,	5,3,4};
		for (int i = 0; i < aantalHoekpunten; i++)
		{	Vlak[] hv = {vlakken[hoekpuntdata[3 * i]],vlakken[hoekpuntdata[3 * i + 1]],
						 vlakken[hoekpuntdata[3 * i + 2]]};
		}
		
	}

	/**
	 * redefined: zet de vulkleur van de vlakjes van dit kubusje 
	 */
	public void zetVulkleur(String kleur)
	{	vulkleur = kleur;
		super.zetVulkleur(kleur);
	}
	
	/**
	 * verschuif dit kubusje over (dx,dy,dz)   
	 * @param dx x-translatie
	 * @param dy y-translatie
	 * @param dz z-translatie
	 */
	public void verschuif(double dx,double dy,double dz)
	{	for (int i = 0; i < aantalHoekpunten; i++)
		{	hoekpunten[i].x += dx;
			hoekpunten[i].y += dy;
			hoekpunten[i].z += dz;
		}
	}
	
}

