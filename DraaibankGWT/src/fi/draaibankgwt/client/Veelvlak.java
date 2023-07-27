package fi.draaibankgwt.client;

//import java.awt.*;


class Prisma extends Veelvlak 
{	
public Prisma(double f, int n, double h)
	{	aantalHoekpunten=2*n;
		aantalVlakken=n+2;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		
		for(int i=0 ; i<n ; i++)
		{	double hoek = 2*i*Math.PI/n;
			double a = f*Math.cos(hoek);
			double b = f*Math.sin(hoek);
			double hoogte = 2*f*h;
			hoekpunten[2*i] = new Hoekpunt(a,0.5*hoogte,b);
			hoekpunten[2*i+1] = new Hoekpunt(a,-0.5*hoogte,b);
		}
		for(int i=0 ; i<n ; i++)
		{	Hoekpunt[] vp = {hoekpunten[2*i],hoekpunten[(2*(i+1))%(2*n)],hoekpunten[(2*(i+1)+1)%(2*n)],hoekpunten[(2*i+1)%(2*n)]};
			vlakken[i] = new Vlak(4,vp);
			vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
		Hoekpunt[] vp = new Hoekpunt[n];
		for(int i=0 ; i<n ; i++)
		{	vp[i] = hoekpunten[2*i+1];
		}
		vlakken[n] = new Vlak(n,vp);
		for(int i=0 ; i<n ; i++)
		{	vp[n-1-i] = hoekpunten[2*i];
		}
		vlakken[n+1] = new Vlak(n,vp);
		
		for(int i=0 ; i<aantalVlakken ; i++)
		{	vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
	}
}
class DraaiObject extends Veelvlak 
{	Hoekpunt[][] hoekptn;
	
	public DraaiObject(int n, int aantalPtn, Punt[] ptn)
	{	aantalHoekpunten=aantalPtn*n;
		aantalVlakken=n*(aantalPtn-1);
		hoekptn = new Hoekpunt[aantalPtn][n];
		vlakken = new Vlak[aantalVlakken];
		
		
		for(int j=0 ; j<aantalPtn ; j++)	
		{	for(int i=0 ; i<n ; i++)
			{	double hoek = 2*i*Math.PI/n;
				double a = Math.cos(hoek);
				double b = Math.sin(hoek);
				hoekptn[j][i] = new Hoekpunt(a*ptn[j].y,ptn[j].x,b*ptn[j].y);
			}
		}
		for(int j=0 ; j<aantalPtn-1 ; j++)
		{	for(int i=0 ; i<n ; i++)
			{	Hoekpunt[] vp = {hoekptn[j][i],hoekptn[j+1][i],hoekptn[j+1][(i+1)%n],hoekptn[j][(i+1)%n]};
				vlakken[j*n+i] = new Vlak(4,vp);
				vlakken[j*n+i].vorigeKleur = "geel";
				vlakken[j*n+i].vulkleur = "geel";
			}
		}
	}
}

class Veelvlak
{	int aantalHoekpunten;
	int aantalVlakken;
	int aantalLijnen;
	Hoekpunt[] hoekpunten;
	Vlak[] vlakken;
	Lijn[] lijnen;
	
	
	public Veelvlak()
	{	aantalHoekpunten = 0;
		aantalVlakken = 0;
		aantalLijnen = 0;
		lijnen = new Lijn[100];
		vlakken = new Vlak[100];
	}
	public Veelvlak(Veelvlak v)
	{	aantalVlakken = 0;
		aantalLijnen = 0;
		lijnen = new Lijn[500];
		vlakken = new Vlak[500];
		hoekpunten = new Hoekpunt[500];
		aantalHoekpunten = v.aantalHoekpunten;
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	hoekpunten[i]=v.hoekpunten[i];
		}
		
	}
	
	public void maakLijn(int m, int n, String kl)
	{	lijnen[aantalLijnen] = new Lijn(hoekpunten[m],hoekpunten[n],kl);
		aantalLijnen++;
	}
	 
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
		}return vd;
	}
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
	public void schaal(double f)
	{	for(int i=0 ; i<aantalHoekpunten ; i++)
		{	hoekpunten[i].x *= f;
			hoekpunten[i].y *= f;
			hoekpunten[i].z *= f;
		}
	}

}
class Lijn
{	Hoekpunt hpunt1,hpunt2;
	String kleur;
	
	public Lijn(Hoekpunt h1, Hoekpunt h2, String kl)
	{	hpunt1 = h1;
		hpunt2 = h2;
		kleur = kl;
	}
}
class Vlak
{	Hoekpunt midden, middenb;
	double x,y,z,xb,yb,zb;
	int aantalHoekpunten;
	Hoekpunt[] punten;
	public String vulkleur,lijnkleur,vorigeKleur;
	
	public Vlak(int n)
	{	vulkleur = "oranje";
		lijnkleur = "zwart";
		vorigeKleur = "oranje";
		aantalHoekpunten=n;
		punten = new Hoekpunt[aantalHoekpunten];
	}
	public Vlak(int n ,Hoekpunt[] pnt)
	{	vulkleur = "oranje";
		lijnkleur = "zwart";
		vorigeKleur = "oranje";
		aantalHoekpunten=n;
		punten = new Hoekpunt[aantalHoekpunten];
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
		
		double a0 = punten[0].x; double b0 = punten[0].y; double c0 = punten[0].z;
		double a1 = punten[1].x; double b1 = punten[1].y; double c1 = punten[1].z;
		double a2 = punten[2].x; double b2 = punten[2].y; double c2 = punten[2].z;
		double d = a0*(b1*c2-b2*c1)-a1*(b0*c2-b2*c0)+a2*(b0*c1-b1*c0);
		xb = ((b1*c2-b2*c1)-(b0*c2-b2*c0)+(b0*c1-b1*c0))/d;
		yb = (-(a1*c2-a2*c1)+(a0*c2-a2*c0)-(a0*c1-a1*c0))/d;
		zb = ((a1*b2-a2*b1)-(a0*b2-a2*b0)+(a0*b1-a1*b0))/d;
		middenb = new Hoekpunt(xb, yb, zb);

	}
}
class Hoekpunt
{	Vlak midden;
	double x,y,z;
	int aantalVlakken;
	Vlak[] vlakken;
	
	public Hoekpunt(double x, double y, double z)
	{	this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void maakVlakken(int n ,Vlak[] vlk)
	{	aantalVlakken=n;
		vlakken = new Vlak[aantalVlakken];
		for(int i=0 ; i<aantalVlakken ; i++)
		{	vlakken[i] = vlk[i];
		}
	}
}

