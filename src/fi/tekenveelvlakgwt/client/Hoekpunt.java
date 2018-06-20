package fi.tekenveelvlakgwt.client;

/**
 * klasse die een hoekpunt van een 3d-figuur representeerd;
 * de klasse bevat de coordinaten van het hoekpunt, het aantal vlakken 
 * dat dit hoekpunt als hoekpunt heeft en een verwijzing naar
 * deze vlakken; <br> 
 * construeer een 3d-figuur door eerst de hoekpunten te construeren
 * (specificeer decoordinaten), dan de vlakken (zie klasse Vlak) 
 * en vervolgens deze vlakken weer toe te wijzen aan de hoekpunten.  
 * @author Peter Boon
 */
public class Hoekpunt 
{	
	/**
	 * Vlak gebruikt bij dualiseren van een 3d-figuur (zie klasse Veelvlak)
	 */
	Vlak midden;
	/**
	 * 3d-coordinaten van dit Hoekpunt 
	 */
	double x,y,z;
	/**
	 * aantal Vlakken dat dit Hoekpunt als hoekpunt heeft
	 */
	int aantalVlakken;
	/**
	 * de Vlakken die dit Hoekpunt als hoekpunt hebben
	 */
	Vlak[] vlakken;

	/**
	 * constructor
	 * @param x x-coordinaat hoekpunt
	 * @param y y-coordinaat hoekpunt
	 * @param z z-coordinaat hoekpunt
	 */
	public Hoekpunt(double x, double y, double z)
	{	this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * zet het aantal en de actuele Vlakken die dit Hoekpunt 
	 * als hoekpunt hebben
	 * @param n aantal Vlakken dat dit Hoekpunt als hoekpunt heeft
	 * @param vlk de Vlakken die dit Hoekpunt als hoekpunt hebben
	 */
	public void maakVlakken(int n ,Vlak[] vlk)
	{	aantalVlakken=n;
		vlakken = new Vlak[aantalVlakken];
		for(int i=0 ; i<aantalVlakken ; i++)
		{	vlakken[i] = vlk[i];
		}
	}
}
