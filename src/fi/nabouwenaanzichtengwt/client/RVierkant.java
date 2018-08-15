package fi.nabouwenaanzichtengwt.client;

/**
 * klasse die een vierkant representeert; het kale grondvlak
 * van het kubusbouwsel is zo'n RVierkant;
 * hieraan worden nog een aantal RVierkanten toegevoegd. <br>
 * zie klasse KubusRooster 
 * @author Peter Boon
 */
public class RVierkant extends Veelvlak
{
	/**
	 * lengte halve zijde grondvlak 
	 */
	double d;
	/**
	 * constructor
	 * @param r lengte zijde grondvlak
	 * @param x x-coordinaat centrum
	 * @param y y-coordinaat centrum 
	 * @param z z-coordinaat centrum
	 */
	public RVierkant(double r, double x, double y, double z)
	{	aantalHoekpunten=4;
		aantalVlakken=1;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		d = r/2;
		hoekpunten[0] = new Hoekpunt(-d,0,-d);
		hoekpunten[1] = new Hoekpunt(d,0,-d);
		hoekpunten[2] = new Hoekpunt(d,0,d);
		hoekpunten[3] = new Hoekpunt(-d,0,d);
		
		verschuif(x,y,z);
		
		int[] vlakdata = {0,3,2,1};
		for(int i=0 ; i<aantalVlakken ; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[4*i]],hoekpunten[vlakdata[4*i+1]],hoekpunten[vlakdata[4*i+2]],hoekpunten[vlakdata[4*i+3]]};
			vlakken[i] = new Vlak(4,vp);
			vlakken[i].vorigeKleur = "lichtgrijs";
			vlakken[i].vulkleur = "lichtgrijs";
		}
		
	}
	/**
	 * verschuif dit RVierkant over (dx,dy,dz)
	 * @param dx x-translatie
	 * @param dy y-translatie
	 * @param dz z-translatie
	 */
	public void verschuif(double dx,double dy,double dz)
	{	for(int i=0 ; i<aantalHoekpunten ; i++)
		{	hoekpunten[i].x += dx;
			hoekpunten[i].y += dy;
			hoekpunten[i].z += dz;
		}
	}
}
