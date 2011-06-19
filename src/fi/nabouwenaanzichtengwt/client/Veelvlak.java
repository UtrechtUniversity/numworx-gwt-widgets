package fi.nabouwenaanzichtengwt.client;


public class Veelvlak
{	int aantalHoekpunten;
	int aantalVlakken;
	Hoekpunt[] hoekpunten;
	Vlak[] vlakken;
	
	public Veelvlak()
	{	aantalHoekpunten = 0;
		aantalVlakken = 0;
		hoekpunten = new Hoekpunt[100];
		vlakken = new Vlak[50];
	}
	
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

	public void voegHoekpuntToe(double x, double y, double z)
	{	hoekpunten[aantalHoekpunten] = new Hoekpunt(x,y,z);
		aantalHoekpunten++;
	}
	
	public void voegVlakToe(int n, int[] hpnrs)
	{	Hoekpunt[]hpv = new Hoekpunt[n];
		for(int i=0 ; i<n ; i++)
		{	hpv[i] = hoekpunten[hpnrs[i]];
		}
		vlakken[aantalVlakken] = new Vlak(n,hpv);
		aantalVlakken++;
	}
	
	public void zetVulkleur(String kleur)
	{	for(int i=0 ; i<aantalVlakken ; i++)
		{	vlakken[i].zetVulkleur(kleur);
		}
	}
}

class Vlak
{	
	int aantalHoekpunten;
	Hoekpunt[] punten;
	public String vulkleur,lijnkleur,vorigeKleur;
	
	public Vlak(int n ,Hoekpunt[] pnt)
	{	vulkleur = "oranje";
		lijnkleur = "zwart";
		vorigeKleur = "oranje";
		aantalHoekpunten=n;
		punten = new Hoekpunt[aantalHoekpunten];
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	punten[i] = pnt[i];
		}
	}
	
	public void zetVulkleur(String kleur)
	{	vulkleur = kleur;
		vorigeKleur = kleur;
	}
}
class Hoekpunt
{	double x,y,z;
	
	public Hoekpunt(double x, double y, double z)
	{	this.x = x;
		this.y = y;
		this.z = z;
	}
}