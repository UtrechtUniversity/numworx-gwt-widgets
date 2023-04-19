package fi.nabouwenaanzichtengwt.client;

/**
 * klasse die een 3d-veelvlak representeert
 * @author Peter Boon
 */

public class Veelvlak
{	
	/**
	 * het aantal hoekpunten in dit veelvlak
	 */
	int aantalHoekpunten;
	/**
	 * het aantal vlakken in dit Veelvlak
	 */
	int aantalVlakken;
	/**
	 * de hoekpunten in dit Veelvlak
	 */
	Hoekpunt[] hoekpunten;
	/**
	 * de vlakken in dit Veelvlak
	 */
	Vlak[] vlakken;

	/**
	 * constructor voor een leeg veelvlak, initialiseer attributen
	 */
	public Veelvlak()
	{	aantalHoekpunten = 0;
		aantalVlakken = 0;
		hoekpunten = new Hoekpunt[100];
		vlakken = new Vlak[50];
	}
	
	/**
	 * overloaded constuctor: hoekpunten gegegeven als een array van doubles (3 per hoekpunt),
	 * vlakken gegeven als het aantal vlakken in vl[0], gevolgd door n+1 integers voor elk vlak met n hoekpunten, nl.
	 * het aantal hoekpunten n gevolgd door n indices in hoekpunten[] 
	 * @param hp double array voor de hoekpunten
	 * @param vl integer array voor vlakken met format als boven
	 */
	public Veelvlak(double[] hp, int[] vl)
	{	aantalVlakken = 0;
		hoekpunten = new Hoekpunt[100];
		vlakken = new Vlak[50];
		
		for(int i=0 ; i<hp.length/3 ; i++)
		{	voegHoekpuntToe(hp[3*i],hp[3*i+1],hp[3*i+2]);
		}
		int teller = 1;
		for(int i=0 ; i<vl[0] ; i++)
		{	int aantalHpV = vl[teller];
			int[] hpv = new int[aantalHpV]; 
			for(int j=0 ; j<aantalHpV ; j++)
			{	hpv[j] = vl[teller+1+j];
			}
			voegVlakToe(aantalHpV,hpv);
			teller = teller + aantalHpV + 1;
		}
	}

	/**
	 * voeg een hoekpunt met coordinaten (x,y,z) toe  aan hoekpunten[]  
	 * @param x x-coordinaat hoekpunt
	 * @param y y-coordinaat hoekpunt
	 * @param z z-coordinaat hoekpunt
	 */
	public void voegHoekpuntToe(double x, double y, double z)
	{	hoekpunten[aantalHoekpunten] = new Hoekpunt(x,y,z);
		aantalHoekpunten++;
	}
	
	/**
	 * voeg een vlak met n hoekpunten toe aan vlakken[]
	 * @param n aantal hoekpunten van het toe te voegen vlak
	 * @param hpnrs de indices van de hoekpunten van het toe te voegen vlak in hoekpunten[]
	 */
	public void voegVlakToe(int n, int[] hpnrs)
	{	Hoekpunt[]hpv = new Hoekpunt[n];
		for(int i=0 ; i<n ; i++)
		{	hpv[i] = hoekpunten[hpnrs[i]];
		}
		vlakken[aantalVlakken] = new Vlak(n,hpv);
		aantalVlakken++;
	}
	
	/**
	 * zet de vulkleur van alle vlakken 
	 * @param kleur nieuwe vulkleur
	 */
	public void zetVulkleur(String kleur)
	{	for(int i=0 ; i<aantalVlakken ; i++)
		{	vlakken[i].zetVulkleur(kleur);
		}
	}
}

