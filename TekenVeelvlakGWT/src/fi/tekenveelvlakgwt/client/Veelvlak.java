package fi.tekenveelvlakgwt.client; 

/**
 * klasse die een 3d-veelvlak representeert
 * @author Peter Boon
 */
public class Veelvlak
{	
	/**
	 * het aantal hoekpunten van dit Veelvlak
	 */
	int aantalHoekpunten;
	/**
	 * het aantal vlakken van dit Veelvlak
	 */
	int aantalVlakken;
	/**
	 * het aantal lijnen in dit veelvlak
	 */
	int aantalLijnen;
	/**
	 * de hoekpunten van dit Veelvlak
	 */
	Hoekpunt[] hoekpunten;
	/**
	 * de vlakken van dit Veelvlak
	 */
	Vlak[] vlakken;
	/**
	 * de lijnen in dit veelvlak
	 */
	Lijn[] lijnen;
	
	/**
	 * het huidige aantal doubles in hpRij (3 voor elk hoekpunt)  
	 */
	int hpRijAantal;
	
	/**
	 * het huidige aantal integers in vlRij (1 voor het aantal vlakken plus n+1 voor elk vlak met n hoekpunten)
	 */
	int vlRijAantal;
	/**
	 * het huidige aantal integers in lnRij (1 voor het aantal lijnen plus 2 voor elke lijn) 
	 */
	int lnRijAantal;
	/**
	 * de coordinaten van alle hoekpunten op een rij (dus drie doubles per hoekpunt) 
	 */
	double[] hpRij;
	
	/**
	 * in vlRij[0] het aantal vlakken, gevolgd door n+1 integers voor elk vlak met n hoekpunten, nl.
	 * het aantal hoekpunten n gevolgd door n indices in hoekpunten[]
	 */
	int[] vlRij;
	/**
	 * in lnRij[0] het aantal lijnen, gevolgd door twee indices in hoekpunten[] vopr elke lijn 
	 */
	int[] lnRij;
	/**
	 * aantal hoekpunten van het laatst toegevoegde vlak
	 */
	int nVorigVlak;
	
	/**
	 * constructor, initialiseer attributen
	 */
	public Veelvlak()
	{	aantalHoekpunten = 0;
		aantalVlakken = 0;
		aantalLijnen = 0;
		hpRijAantal = 0;
		vlRijAantal = 1;
		lnRijAantal = 1;
		hpRij = new double[1];
		vlRij = new int[1];
		lnRij = new int[1];
		vlRij[0] = 0;
		lnRij[0] = 0;
		hoekpunten = new Hoekpunt[500];
		lijnen = new Lijn[500];
		vlakken = new Vlak[500];
	}
	/**
	 * overloaded constructor: maak een deep copy van Veelvlak v
	 * @param v Veelvlak dat gekopieerd wordt
	 */
	public Veelvlak(Veelvlak v)
	{	aantalVlakken = 0;
		aantalLijnen = 0;
		hpRijAantal = 0;
		vlRijAantal = 1;
		lnRijAantal = 1;
		hpRij = new double[1];
		vlRij = new int[1];
		lnRij = new int[1];
		lijnen = new Lijn[500];
		vlakken = new Vlak[500];
		hoekpunten = new Hoekpunt[500];
		for(int i=0 ; i<v.aantalHoekpunten ; i++)
		{	voegHoekpuntToe(v.hoekpunten[i].x, v.hoekpunten[i].y, v.hoekpunten[i].z);
		}
	}
	/**
	 * overloaded constuctor: hoekpunten gegegeven, vlakken gegeven als 
	 * het aantal vlakken in vl[0], gevolgd door n+1 integers voor elk vlak met n hoekpunten, nl.
	 * het aantal hoekpunten n gevolgd door n indices in hoekpunten[] 
	 * @param hp hoekpunten array
	 * @param vl integer array met format als boven
	 */
	public Veelvlak(Hoekpunt[] hp, int[] vl)
	{	aantalLijnen = 0;
		lijnen = new Lijn[100];
		
		aantalHoekpunten = hp.length;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	hoekpunten[i]=hp[i];
		}
		aantalVlakken = vl[0];
		vlakken = new Vlak[aantalVlakken];
		int teller = 1;
		for(int i=0 ; i<aantalVlakken ; i++)
		{	int aantalHpV = vl[teller];
			Hoekpunt[] hpv = new Hoekpunt[aantalHpV];
			for(int j=0 ; j<aantalHpV ; j++)
			{	hpv[j] = hp[vl[teller+1+j]];
				
			}
			vlakken[i] = new Vlak(aantalHpV,hpv);
			teller = teller + aantalHpV + 1;
		}
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
		aantalLijnen = 0;
		hpRijAantal = 0;
		vlRijAantal = 1;
		lnRijAantal = 1;
		nVorigVlak = 0;
		hpRij = new double[1];
		vlRij = new int[1];
		lnRij = new int[1];
		lijnen = new Lijn[500];
		vlakken = new Vlak[500];
		hoekpunten = new Hoekpunt[1000];

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
	 * overloaded constuctor: hoekpunten gegegeven als een array van doubles (3 per hoekpunt),
	 * vlakken gegeven als het aantal vlakken in vl[0], gevolgd door n+1 integers voor elk vlak met n hoekpunten, nl.
	 * het aantal hoekpunten n gevolgd door n indices in hoekpunten[], 
	 * lijnen gegeven als aantal lijnen in ln[0], gevolgd 2 indices per lijn in het (nog te construeren)
	 * hoekpunten array 
	 * @param hp double array voor de hoekpunten
	 * @param vl integer array voor vlakken met format als boven
	 * @param ln integer array voor lijnen met format als boven
	 */
	public Veelvlak(double[] hp, int[] vl, int[] ln)
	{	aantalVlakken = 0;
		aantalLijnen = 0;
		hpRijAantal = 0;
		vlRijAantal = 1;
		lnRijAantal = 1;
		nVorigVlak = 0;
		hpRij = new double[1];
		vlRij = new int[1];
		lnRij = new int[1];
		lijnen = new Lijn[500];
		vlakken = new Vlak[500];
		hoekpunten = new Hoekpunt[1000];

		for (int i = 0; i < hp.length / 3; i++)
		{	voegHoekpuntToe(hp[3*i], hp[3*i+1], hp[3*i+2]);
		}
		int teller = 1;
		for (int i = 0; i < vl[0]; i++)
		{	int aantalHpV = vl[teller];
			int[] hpv = new int[aantalHpV]; 
			for (int j = 0; j < aantalHpV; j++)
			{	hpv[j] = vl[teller+1+j];
			}
			voegVlakToe(aantalHpV, hpv);
			teller = teller + aantalHpV + 1;
		}
		for (int i = 0; i < ln[0]; i++)
		{	maakLijn(ln[2*i+1], ln[2*i+2], "rood");
		}
	}

	/**
	 * voeg een hoekpunt toe met coordinaten (x,y,z), zowel aan hpRij als aan hoekpunten[]  
	 * @param x x-coordinaat hoekpunt
	 * @param y y-coordinaat hoekpunt
	 * @param z z-coordinaat hoekpunt
	 */
	public void voegHoekpuntToe(double x, double y, double z)
	{	
		double[] hpRijRes = hpRij;
		
		hpRij = new double[hpRijAantal + 3];
		
		for(int i=0 ; i<hpRijAantal ; i++)
		{	hpRij[i] = hpRijRes[i];
		}
		hpRij[hpRijAantal] = x;
		hpRijAantal++;
		hpRij[hpRijAantal] = y;
		hpRijAantal++;
		hpRij[hpRijAantal] = z;
		hpRijAantal++;
		
		hoekpunten[aantalHoekpunten] = new Hoekpunt(x,y,z);
		aantalHoekpunten++;
	}
	
	/**
	 * voeg een vlak toe met n hoekpunten, zowel aan vlRij als aan vlakken[]
	 * @param n aantal hoekpunten van het toe te voegen vlak
	 * @param hpnrs de indices van de hoekpunten van het toe te voegen vlak in hoekpunten[]
	 */
	public void voegVlakToe(int n, int[] hpnrs)
	{	nVorigVlak = n;
		
		int[] vlRijRes = vlRij;
		
		vlRij = new int[vlRijAantal+n+1];
		
		for(int i=0 ; i < vlRijAantal ; i++)
		{	vlRij[i] = vlRijRes[i];
		}
		
		vlRij[0]++;
		vlRij[vlRijAantal] = n;
		vlRijAantal++;
		for(int i=0 ; i<n ; i++)
		{	vlRij[vlRijAantal+i] = hpnrs[i];
		}
		vlRijAantal = vlRijAantal + n;
		
		Hoekpunt[] hpv = new Hoekpunt[n];
		for(int i=0 ; i<n ; i++)
		{	hpv[i] = hoekpunten[hpnrs[i]];
		}
		vlakken[aantalVlakken] = new Vlak(n, hpv);
		aantalVlakken++;
	}

	/**
	 * verwijder het laatst toegevoegde vlak uit dit Veelvlak
	 */
	public void wisVorigVlak()
	{	vlRij[0]--;
		vlRijAantal = vlRijAantal-nVorigVlak-1;
		aantalVlakken--;
	}
	
	/**
	 * verwijder de laatst toegevoegde lijn uit dit Veelvlak
	 */
	public void wisVorigeLijn()
	{	lnRij[0]--;
		lnRijAantal = lnRijAantal-2;
		aantalLijnen--;
	}

	/**
	 * verwijder alle vlakken uit dit Veelvlak
	 */
	public void wisVlakken()
	{	vlRij[0] = 0;
		vlRijAantal = 1;
		aantalVlakken = 0;
	}
	
	/**
	 * verwijder alle lijnen uit dit Veelvlak
	 */
	public void wisLijnen()
	{	lnRij[0] = 0;
		lnRijAantal = 1;
		aantalLijnen = 0;
	}
	
	/**
	 * voeg een lijn toe (aan lnRij en lijnen[]) door de 
	 * indices van twee hoekpunten te specificeren
	 * @param m eerste index
	 * @param n tweede index
	 * @param kl kleur van de lijn
	 */
	public void maakLijn(int m, int n, String kl)
	{	int[] lnRijRes = lnRij;
		lnRij = new int[lnRijAantal+2];
		for(int i=0 ; i<lnRijAantal ; i++)
		{	lnRij[i] = lnRijRes[i];
		}
		lnRij[0]++;
		lnRij[lnRijAantal] = m;
		lnRijAantal++;
		lnRij[lnRijAantal] = n;
		lnRijAantal++;
		
		lijnen[aantalLijnen] = new Lijn(hoekpunten[m],hoekpunten[n],kl);
		aantalLijnen++;
		
	}
	 
	/**
	 * dualiseer dit veelvlak m.b.v. het midden van elk Vlak (zie klasse Vlak) 
	 * @return het gedualisserde Veelvlak
	 */
	Veelvlak dualiseer()
	{	Veelvlak vd = new Veelvlak();

		vd.aantalVlakken = aantalHoekpunten;
		vd.vlakken = new Vlak[vd.aantalVlakken];
		for(int i=0 ; i<vd.aantalVlakken ; i++)
		{	Hoekpunt[] pr = new Hoekpunt[hoekpunten[i].aantalVlakken];
			for(int j=0 ; j<hoekpunten[i].aantalVlakken ; j++)
			{	pr[j]= hoekpunten[i].vlakken[j].midden;
			}
			vd.vlakken[i]=new Vlak(hoekpunten[i].aantalVlakken,pr);
			hoekpunten[i].midden = vd.vlakken[i];												
		}
		vd.aantalHoekpunten = aantalVlakken;
		vd.hoekpunten = new Hoekpunt[vd.aantalHoekpunten];
		for(int i=0 ; i<vd.aantalHoekpunten ; i++)
		{	vd.hoekpunten[i] = vlakken[i].midden;
			vd.hoekpunten[i].aantalVlakken = vlakken[i].aantalHoekpunten;
		
			Vlak[] vh = new Vlak[vlakken[i].aantalHoekpunten];
			for(int j=0 ; j<vlakken[i].aantalHoekpunten ; j++)
			{	vh[j] = vlakken[i].punten[j].midden;
			}	
			vd.hoekpunten[i].maakVlakken(vlakken[i].aantalHoekpunten,vh);
		}
		return vd;
	}
	
	/**
	 * dualiseer dit veelvlak m.b.v. middenb van elk Vlak (zie klasse Vlak) 
	 * @return het gedualisserde Veelvlak
	 */
	Veelvlak dualiseerb()
	{	Veelvlak vd = new Veelvlak();

		vd.aantalVlakken = aantalHoekpunten;
		vd.vlakken = new Vlak[vd.aantalVlakken];
		for(int i=0 ; i<vd.aantalVlakken ; i++)
		{	Hoekpunt[] pr = new Hoekpunt[hoekpunten[i].aantalVlakken];
			for(int j=0 ; j<hoekpunten[i].aantalVlakken ; j++)
			{	pr[j]= hoekpunten[i].vlakken[j].middenb;
			}
			vd.vlakken[i]=new Vlak(hoekpunten[i].aantalVlakken,pr);
			hoekpunten[i].midden = vd.vlakken[i];												
		}
		vd.aantalHoekpunten = aantalVlakken;
		vd.hoekpunten = new Hoekpunt[vd.aantalHoekpunten];
		for(int i=0 ; i<vd.aantalHoekpunten ; i++)
		{	vd.hoekpunten[i] = vlakken[i].middenb;
			vd.hoekpunten[i].aantalVlakken = vlakken[i].aantalHoekpunten;
		
			Vlak[] vh = new Vlak[vlakken[i].aantalHoekpunten];
			for(int j=0 ; j<vlakken[i].aantalHoekpunten ; j++)
			{	vh[j] = vlakken[i].punten[j].midden;
			}	
			vd.hoekpunten[i].maakVlakken(vlakken[i].aantalHoekpunten,vh);
		}return vd;
	}
	
	/**
	 * schaal dit veelvlak met een factor f
	 * @param f schaalfactor
	 */
	public void schaal(double f)
	{	for(int i=0 ; i<aantalHoekpunten ; i++)
		{	hoekpunten[i].x *= f;
			hoekpunten[i].y *= f;
			hoekpunten[i].z *= f;
		}
	}

}

