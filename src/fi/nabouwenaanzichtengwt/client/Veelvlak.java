package fi.nabouwenaanzichtengwt.client;

class RKubus extends Veelvlak 
{	Punt3D positie;
	double d;
	boolean[] isOnbedekt;
	String vulkleur;
	
	public RKubus(double r, double x, double y, double z)
	{	vulkleur = "geel";
		aantalHoekpunten=8;
		aantalVlakken=6;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		isOnbedekt = new boolean[6];
		for(int i=0 ; i<6 ; i++)
		{	isOnbedekt[i] = true;
		}
		d = r/2;
		hoekpunten[0] = new Hoekpunt(-d,d,-d);
		hoekpunten[1] = new Hoekpunt(d,d,-d);
		hoekpunten[2] = new Hoekpunt(d,d,d);
		hoekpunten[3] = new Hoekpunt(-d,d,d);
		hoekpunten[4] = new Hoekpunt(-d,-d,-d);
		hoekpunten[5] = new Hoekpunt(d,-d,-d);
		hoekpunten[6] = new Hoekpunt(d,-d,d);
		hoekpunten[7] = new Hoekpunt(-d,-d,d);
		
		verschuif(x,y,z);
		
		int[] vlakdata = {0,3,2,1,	0,1,5,4,	1,2,6,5,	2,3,7,6,	3,0,4,7,	4,5,6,7};
		for(int i=0 ; i<aantalVlakken ; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[4*i]],hoekpunten[vlakdata[4*i+1]],hoekpunten[vlakdata[4*i+2]],hoekpunten[vlakdata[4*i+3]]};
			vlakken[i] = new Vlak(4,vp);
			vlakken[i].vorigeKleur = vulkleur;
			vlakken[i].vulkleur = vulkleur;
		}
		
		int[] hoekpuntdata = {0,1,4,	0,2,1,	0,3,2,	0,4,3,	5,4,1,	5,1,2,	5,2,3,	5,3,4};
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	Vlak[] hv = {vlakken[hoekpuntdata[3*i]],vlakken[hoekpuntdata[3*i+1]],vlakken[hoekpuntdata[3*i+2]]};
			//hoekpunten[i].maakVlakken(3,hv);
		}
		
		
															 
	}
	
	public void zetVulkleur(String kleur)
	{	vulkleur = kleur;
		super.zetVulkleur(kleur);
	}
	
	public void verschuif(double dx,double dy,double dz)
	{	for(int i=0 ; i<aantalHoekpunten ; i++)
		{	hoekpunten[i].x += dx;
			hoekpunten[i].y += dy;
			hoekpunten[i].z += dz;
		}
	}
	
}
class RVierkant extends Veelvlak 
{	
	Punt3D positie;
	double d;
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
		
		//int[] hoekpuntdata = {0,1,4,	0,2,1,	0,3,2,	0,4,3,	5,4,1,	5,1,2,	5,2,3,	5,3,4};
		//for(int i=0 ; i<aantalHoekpunten ; i++)
		//{	Vlak[] hv = {vlakken[hoekpuntdata[3*i]],vlakken[hoekpuntdata[3*i+1]],vlakken[hoekpuntdata[3*i+2]]};
		//	hoekpunten[i].maakVlakken(3,hv);
		//}
		
		
															 
	}
	public void verschuif(double dx,double dy,double dz)
	{	for(int i=0 ; i<aantalHoekpunten ; i++)
		{	hoekpunten[i].x += dx;
			hoekpunten[i].y += dy;
			hoekpunten[i].z += dz;
		}
	}
}
class Veelvlak
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