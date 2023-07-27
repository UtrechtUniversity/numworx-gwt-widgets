package fi.tekenveelvlakgwt.client;

/**
 * klasse die een vlak van een 3d-figuur representeerd;
 * de klasse bevat de hoekpunten van het vlak (moeten in hetzelfde vlak liggen)<br> 
 * construeer een 3d-figuur door eerst de hoekpunten te construeren
 * (specificeer de coordinaten), dan de vlakken  
 * en vervolgens deze vlakken weer toe te wijzen aan de hoekpunten
 * (zie klasse Hoekpunt).  
 * @author Peter Boon
 */
public class Vlak 
{	
	/**
	 * Hoekpunten gebruikt bij dualiseren van een 3d-figuur, zie klasse Veelvlak
	 */
	Hoekpunt midden, middenb;
	/**
	 * 3d-coordinaten van midden en middenb
	 */
	double x,y,z,xb,yb,zb;
	/**
	 * het aantal hoekpunten van dit Vlak
	 */
	int aantalHoekpunten;
	/**
	 * de hoekpunten van dit vlak
	 */
	Hoekpunt[] punten;
	/**
	 * vulkleur, uitlijnkleur en een reserve-vulkleur dit Vlak
	 */
	public String vulkleur,lijnkleur,vorigeKleur;
	
	/**
	 * constructor: zet kleuren en initialiseer alleen het Hoekpunt-array
	 * @param n aantal hoekpunten
	 */
	public Vlak(int n)
	{	vulkleur = "oranje";
		lijnkleur = "zwart";
		vorigeKleur = "oranje";
		aantalHoekpunten=n;
		punten = new Hoekpunt[aantalHoekpunten];
	}
	/**
	 * constructor: zet kleuren en initialiseer de hoekpunten
	 * @param n aantal hoekpunten
	 * @param pnt array met de hoekpunten
	 */
	public Vlak(int n ,Hoekpunt[] pnt)
	{	vulkleur = "oranje";
		lijnkleur = "zwart";
		vorigeKleur = "oranje";
		aantalHoekpunten=n;
		punten = new Hoekpunt[aantalHoekpunten];
		//bereken coordinaten midden
		double somx=0;double somy=0;double somz=0;
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	punten[i] = pnt[i];
			somx = somx+punten[i].x;
			somy = somy+punten[i].y;
			somz = somz+punten[i].z;
		}
		x = somx/aantalHoekpunten;
		y = somy/aantalHoekpunten;
		z = somz/aantalHoekpunten;
		midden = new Hoekpunt(x,y,z);
		//bereken coordinaten middenb
		double a0 = punten[0].x; double b0 = punten[0].y; double c0 = punten[0].z;
		double a1 = punten[1].x; double b1 = punten[1].y; double c1 = punten[1].z;
		double a2 = punten[2].x; double b2 = punten[2].y; double c2 = punten[2].z;
		double d = a0*(b1*c2-b2*c1)-a1*(b0*c2-b2*c0)+a2*(b0*c1-b1*c0);
		xb = ((b1*c2-b2*c1)-(b0*c2-b2*c0)+(b0*c1-b1*c0))/d;
		yb = (-(a1*c2-a2*c1)+(a0*c2-a2*c0)-(a0*c1-a1*c0))/d;
		zb = ((a1*b2-a2*b1)-(a0*b2-a2*b0)+(a0*b1-a1*b0))/d;
		middenb = new Hoekpunt(xb, yb, zb);

	}
	
	/**
	 * zet de vulkleur van dit Vlak
	 * @param vk de vulkleur
	 */
	public void zetVulkleur(String vk)
	{
		vulkleur = vk;
	}
}
