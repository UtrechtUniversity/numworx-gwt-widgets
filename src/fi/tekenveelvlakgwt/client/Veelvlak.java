package fi.tekenveelvlakgwt.client; 

class Kubus extends Veelvlak 
{	
	public Kubus(double f)
	{	aantalHoekpunten=8;
		aantalVlakken=6;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		double w3 = f*Math.sqrt(3)/3;
		hoekpunten[0] = new Hoekpunt(-w3,w3,-w3);
		hoekpunten[1] = new Hoekpunt(w3,w3,-w3);
		hoekpunten[2] = new Hoekpunt(w3,w3,w3);
		hoekpunten[3] = new Hoekpunt(-w3,w3,w3);
		hoekpunten[4] = new Hoekpunt(-w3,-w3,-w3);
		hoekpunten[5] = new Hoekpunt(w3,-w3,-w3);
		hoekpunten[6] = new Hoekpunt(w3,-w3,w3);
		hoekpunten[7] = new Hoekpunt(-w3,-w3,w3);
		
		int[] vlakdata = {0,3,2,1,	0,1,5,4,	1,2,6,5,	2,3,7,6,	3,0,4,7,	4,5,6,7};
		for(int i=0 ; i<aantalVlakken ; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[4*i]],hoekpunten[vlakdata[4*i+1]],hoekpunten[vlakdata[4*i+2]],hoekpunten[vlakdata[4*i+3]]};
			vlakken[i] = new Vlak(4,vp);
			vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
		
		int[] hoekpuntdata = {0,1,4,	0,2,1,	0,3,2,	0,4,3,	5,4,1,	5,1,2,	5,2,3,	5,3,4};
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	Vlak[] hv = {vlakken[hoekpuntdata[3*i]],vlakken[hoekpuntdata[3*i+1]],vlakken[hoekpuntdata[3*i+2]]};
			hoekpunten[i].maakVlakken(3,hv);
		}
	}
}
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
class AntiPrisma extends Veelvlak 
{	
public AntiPrisma(double f, int n, double h)
	{	aantalHoekpunten=2*n;
		aantalVlakken=2*n+2;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		
		for(int i=0 ; i<n ; i++)
		{	double hoek = 2*i*Math.PI/n;
			double a = f*Math.cos(hoek);
			double b = f*Math.sin(hoek);
			double hoogte = 2*f*h;
			hoekpunten[i] = new Hoekpunt(a,0.5*hoogte,b);
		}
		for(int i=0 ; i<n ; i++)
		{	double hoek = Math.PI/n + 2*i*Math.PI/n;
			double a = f*Math.cos(hoek);
			double b = f*Math.sin(hoek);
			double hoogte = 2*f*h;
			hoekpunten[n+i] = new Hoekpunt(a,-0.5*hoogte,b);
		}
		for(int i=0 ; i<n ; i++)
		{	Hoekpunt[] vp = {hoekpunten[i],hoekpunten[(i+1)%n],hoekpunten[n+i]};
			vlakken[i] = new Vlak(3,vp);
			vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
		for(int i=0 ; i<n ; i++)
		{	Hoekpunt[] vp = {hoekpunten[n+i],hoekpunten[n+((i-1+n)%n)],hoekpunten[i]};
			vlakken[n+i] = new Vlak(3,vp);
			vlakken[n+i].vorigeKleur = "geel";
			vlakken[n+i].vulkleur = "geel";
		}
		Hoekpunt[] vp = new Hoekpunt[n];
		for(int i=0 ; i<n ; i++)
		{	vp[i] = hoekpunten[n+i];
		}
		vlakken[2*n] = new Vlak(n,vp);
		for(int i=0 ; i<n ; i++)
		{	vp[n-1-i] = hoekpunten[i];
		}
		vlakken[2*n+1] = new Vlak(n,vp);
		
		for(int i=0 ; i<aantalVlakken ; i++)
		{	vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
	}
}
class Pyramide extends Veelvlak 
{	
public Pyramide(double f, int n, double h)
	{	aantalHoekpunten=n+1;
		aantalVlakken=n+1;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		double hoogte = 2*f*h;		
		for(int i=0 ; i<n ; i++)
		{	double hoek = 2*i*Math.PI/n;
			double a = f*Math.cos(hoek);
			double b = f*Math.sin(hoek);
			
			hoekpunten[i] = new Hoekpunt(a,-0.5*hoogte,b);
		}
		hoekpunten[n] = new Hoekpunt(0,0.5*hoogte,0);
		for(int i=0 ; i<n ; i++)
		{	Hoekpunt[] vp = {hoekpunten[n],hoekpunten[(i+1)%n],hoekpunten[i]};
			vlakken[i] = new Vlak(3,vp);
			vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
		Hoekpunt[] vp = new Hoekpunt[n];
		for(int i=0 ; i<n ; i++)
		{	vp[i] = hoekpunten[i];
		}
		vlakken[n] = new Vlak(n,vp);
				
		for(int i=0 ; i<aantalVlakken ; i++)
		{	vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
	}
}
class Tetraeder extends Veelvlak 
{	
	public Tetraeder(double f)
	{	aantalHoekpunten=4;
		aantalVlakken=4;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		double w3 = f*Math.sqrt(3)/3;
		hoekpunten[0] = new Hoekpunt(w3,w3,-w3);
		hoekpunten[1] = new Hoekpunt(-w3,w3,w3);
		hoekpunten[2] = new Hoekpunt(w3,-w3,w3);
		hoekpunten[3] = new Hoekpunt(-w3,-w3,-w3);
		
		
		int[] vlakdata = {0,1,2,	1,0,3,	2,1,3,	3,0,2};
		for(int i=0 ; i<aantalVlakken ; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[3*i]],hoekpunten[vlakdata[3*i+1]],hoekpunten[vlakdata[3*i+2]]};
			vlakken[i] = new Vlak(3,vp);
		}
		int[] hoekpuntdata = {0,3,1,	0,1,2,	0,2,3,	1,3,2};
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	Vlak[] hv = {vlakken[hoekpuntdata[3*i]],vlakken[hoekpuntdata[3*i+1]],vlakken[hoekpuntdata[3*i+2]]};
			hoekpunten[i].maakVlakken(3,hv);
		}
	}
}
class TetraederKnot extends Veelvlak 
{	
	public TetraederKnot(double f, double a)
	{	aantalHoekpunten=12;
		aantalVlakken=8;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		double v = f*Math.sqrt(3)/3;
		double w = f*(1-a)*Math.sqrt(3)/3;
		hoekpunten[0] = new Hoekpunt(w,v,-w);
		hoekpunten[1] = new Hoekpunt(v,w,-w);
		hoekpunten[2] = new Hoekpunt(w,w,-v);
		hoekpunten[3] = new Hoekpunt(-w,v,w);
		hoekpunten[4] = new Hoekpunt(-v,w,w);
		hoekpunten[5] = new Hoekpunt(-w,w,v);
		hoekpunten[6] = new Hoekpunt(-w,-w,-v);
		hoekpunten[7] = new Hoekpunt(-w,-v,-w);
		hoekpunten[8] = new Hoekpunt(-v,-w,-w);
		hoekpunten[9] = new Hoekpunt(v,-w,w);
		hoekpunten[10] = new Hoekpunt(w,-w,v);
		hoekpunten[11] = new Hoekpunt(w,-v,w);
		
		int[] vlakdata = {0,1,2,3,4,5,6,7,8,9,10,11};
		for(int i=0 ; i<4 ; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[3*i]],hoekpunten[vlakdata[3*i+1]],hoekpunten[vlakdata[3*i+2]]};
			vlakken[i] = new Vlak(3,vp);
			vlakken[i].vorigeKleur = "groen";
			vlakken[i].vulkleur = "groen";
		}
		int[] vlakdata1 = {0,2,6,8,4,3,
						   1,9,11,7,6,2,
							3,5,10,9,1,0,
							4,8,7,11,10,5};
		for(int i=0 ; i<4 ; i++)
		{	Hoekpunt[] vp1 = {hoekpunten[vlakdata1[6*i]],hoekpunten[vlakdata1[6*i+1]],hoekpunten[vlakdata1[6*i+2]],hoekpunten[vlakdata1[6*i+3]],
			hoekpunten[vlakdata1[6*i+4]],hoekpunten[vlakdata1[6*i+5]]};
			vlakken[i+4] = new Vlak(6,vp1);
			vlakken[i+4].vorigeKleur = "geel";
			vlakken[i+4].vulkleur = "geel";
		}

		int[] hoekpuntdata = {0,4,6,	0,6,5,	0,5,4,	1,6,4,	1,4,7,	1,7,6,	2,4,5,	2,5,7,	2,7,4,	3,5,6,	3,6,7,	3,7,5};
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	Vlak[] hv = {vlakken[hoekpuntdata[3*i]],vlakken[hoekpuntdata[3*i+1]],vlakken[hoekpuntdata[3*i+2]]};
			hoekpunten[i].maakVlakken(3,hv);
		}
	}
}
class TetraederKnot2 extends Veelvlak 
{	
	public TetraederKnot2(double f, double a)
	{	aantalHoekpunten=12;
		aantalVlakken=8;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		double v = f*Math.sqrt(3)/3;
		double w = f*(1-a)*Math.sqrt(3)/3;
		hoekpunten[0] = new Hoekpunt(w,v,w);
		hoekpunten[1] = new Hoekpunt(w,w,v);
		hoekpunten[2] = new Hoekpunt(v,w,w);
		hoekpunten[3] = new Hoekpunt(-w,v,-w);
		hoekpunten[4] = new Hoekpunt(-w,w,-v);
		hoekpunten[5] = new Hoekpunt(-v,w,-w);
		hoekpunten[6] = new Hoekpunt(v,-w,-w);
		hoekpunten[7] = new Hoekpunt(w,-v,-w);
		hoekpunten[8] = new Hoekpunt(w,-w,-v);
		hoekpunten[9] = new Hoekpunt(-w,-w,v);
		hoekpunten[10] = new Hoekpunt(-v,-w,w);
		hoekpunten[11] = new Hoekpunt(-w,-v,w);
		
		int[] vlakdata = {0,1,2,3,4,5,6,7,8,9,10,11};
		for(int i=0 ; i<4 ; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[3*i]],hoekpunten[vlakdata[3*i+1]],hoekpunten[vlakdata[3*i+2]]};
			vlakken[i] = new Vlak(3,vp);
			vlakken[i].vorigeKleur = "groen";
			vlakken[i].vulkleur = "groen";
		}
		int[] vlakdata1 = {0,2,6,8,4,3,
						   1,9,11,7,6,2,
							3,5,10,9,1,0,
							4,8,7,11,10,5};
		for(int i=0 ; i<4 ; i++)
		{	Hoekpunt[] vp1 = {hoekpunten[vlakdata1[6*i]],hoekpunten[vlakdata1[6*i+1]],hoekpunten[vlakdata1[6*i+2]],hoekpunten[vlakdata1[6*i+3]],
			hoekpunten[vlakdata1[6*i+4]],hoekpunten[vlakdata1[6*i+5]]};
			vlakken[i+4] = new Vlak(6,vp1);
			vlakken[i+4].vorigeKleur = "geel";
			vlakken[i+4].vulkleur = "geel";
		}

		int[] hoekpuntdata = {0,4,6,	0,6,5,	0,5,4,	1,6,4,	1,4,7,	1,7,6,	2,4,5,	2,5,7,	2,7,4,	3,5,6,	3,6,7,	3,7,5};
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	Vlak[] hv = {vlakken[hoekpuntdata[3*i]],vlakken[hoekpuntdata[3*i+1]],vlakken[hoekpuntdata[3*i+2]]};
			hoekpunten[i].maakVlakken(3,hv);
		}
	}
}
class Icosaeder extends Veelvlak
{		
	public Icosaeder(double f)
	{	aantalHoekpunten=12;
		aantalVlakken=20;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		
		
		double pi = Math.PI;
		double ct = (Math.cos(2*pi/5))/(1-Math.cos(2*pi/5));
		double theta = Math.acos(ct);
		double st = Math.sin(theta);
				
		hoekpunten[0] = new Hoekpunt(0,f*1,0);
		hoekpunten[1] = new Hoekpunt(f*st,f*ct,0);
		hoekpunten[2] = new Hoekpunt(f*st*Math.cos(2*pi/5), f*ct, f*st*Math.sin(2*pi/5));
		hoekpunten[3] = new Hoekpunt(f*st*Math.cos(4*pi/5), f*ct, f*st*Math.sin(4*pi/5));
		hoekpunten[4] = new Hoekpunt(f*st*Math.cos(6*pi/5), f*ct, f*st*Math.sin(6*pi/5));
		hoekpunten[5] = new Hoekpunt(f*st*Math.cos(8*pi/5), f*ct, f*st*Math.sin(8*pi/5));
		hoekpunten[6] = new Hoekpunt(f*st*Math.cos(1*pi/5), -f*ct,f*st*Math.sin(1*pi/5));
		hoekpunten[7] = new Hoekpunt(f*st*Math.cos(3*pi/5), -f*ct, f*st*Math.sin(3*pi/5));
		hoekpunten[8] = new Hoekpunt(f*st*Math.cos(5*pi/5), -f*ct, f*st*Math.sin(5*pi/5));
		hoekpunten[9] = new Hoekpunt(f*st*Math.cos(7*pi/5), -f*ct, f*st*Math.sin(7*pi/5));
		hoekpunten[10] = new Hoekpunt(f*st*Math.cos(9*pi/5), -f*ct, f*st*Math.sin(9*pi/5));
		hoekpunten[11] = new Hoekpunt(0, -f*1, 0);
	
		int[] vlakdata = {0,2,1,	0,3,2,	0,4,3,	0,5,4,	0,1,5,	
							1,2,6,	2,3,7,	3,4,8,	4,5,9,	5,1,10,
							2,7,6,	3,8,7,	4,9,8,	5,10,9,	1,6,10,
							6,7,11,	7,8,11,	8,9,11,	9,10,11,10,6,11	};
		for(int i=0 ; i<aantalVlakken ; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[3*i]],hoekpunten[vlakdata[3*i+1]],hoekpunten[vlakdata[3*i+2]]};
			vlakken[i] = new Vlak(3,vp);
		}
		
		int[] hoekpuntdata = {4,3,2,1,0,		
							  0,5,14,9,4,	
							  1,6,10,5,0,	
							  2,7,11,6,1,	
							  3,8,12,7,2,	
							  4,9,13,8,3,
							  5,10,15,19,14,
							  6,11,16,15,10,
							  7,12,17,16,11,
							  8,13,18,17,12,
							  9,14,19,18,13,
							  15,16,17,18,19};
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	Vlak[] hv = {vlakken[hoekpuntdata[5*i]],vlakken[hoekpuntdata[5*i+1]],vlakken[hoekpuntdata[5*i+2]],vlakken[hoekpuntdata[5*i+3]],vlakken[hoekpuntdata[5*i+4]]};
			hoekpunten[i].maakVlakken(5,hv);
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
	int hpRijAantal;
	int vlRijAantal;
	int lnRijAantal;
	double[] hpRij;
	int[] vlRij;
	int[] lnRij;
	int nVorigVlak;
	
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
		//aantalHoekpunten = v.aantalHoekpunten;
		for(int i=0 ; i<v.aantalHoekpunten ; i++)
		{	//hoekpunten[i]=v.hoekpunten[i];
			voegHoekpuntToe(v.hoekpunten[i].x, v.hoekpunten[i].y, v.hoekpunten[i].z);
		}
	}
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

	public void voegHoekpuntToe(double x, double y, double z)
	{	double[] hpRijRes = hpRij;
		hpRij = new double[hpRijAantal + 3];
		for(int i=0 ; i<hpRijAantal ; i++)
		{	hpRij[i] = hpRijRes[i];
		}
		hpRij[hpRijAantal] = x; hpRijAantal++;
		hpRij[hpRijAantal] = y; hpRijAantal++;
		hpRij[hpRijAantal] = z; hpRijAantal++;
		
		hoekpunten[aantalHoekpunten] = new Hoekpunt(x,y,z);
		aantalHoekpunten++;
	}
	
	public void voegVlakToe(int n, int[] hpnrs)
	{	nVorigVlak = n;
		int[] vlRijRes = vlRij;
		vlRij = new int[vlRijAantal+n+1];
		for(int i=0 ; i<vlRijAantal ; i++)
		{	vlRij[i] = vlRijRes[i];
		}
		vlRij[0]++;
		vlRij[vlRijAantal] = n;
		vlRijAantal++;
		for(int i=0 ; i<n ; i++)
		{	vlRij[vlRijAantal+i] = hpnrs[i];
		}
		vlRijAantal = vlRijAantal + n;
		
		Hoekpunt[]hpv = new Hoekpunt[n];
		for(int i=0 ; i<n ; i++)
		{	hpv[i] = hoekpunten[hpnrs[i]];
		}
		vlakken[aantalVlakken] = new Vlak(n, hpv);
		aantalVlakken++;
	}
	
	public void wisVorigVlak()
	{	vlRij[0]--;
		vlRijAantal = vlRijAantal-nVorigVlak-1;
		aantalVlakken--;
		//new Veelvlak(hpRij,vlRij);
	}
	
	public void wisVorigeLijn()
	{	lnRij[0]--;
		lnRijAantal = lnRijAantal-2;
		aantalLijnen--;
		//new Veelvlak(hpRij,vlRij);
	}
	
	public void wisVlakken()
	{	vlRij[0] = 0;
		vlRijAantal = 1;
		aantalVlakken = 0;
		//new Veelvlak(hpRij,vlRij);
	}
	
	public void wisLijnen()
	{	lnRij[0] = 0;
		lnRijAantal = 1;
		aantalLijnen = 0;
		//new Veelvlak(hpRij,vlRij);
	}
	
	public void maakLijn(int m, int n, String kl)
	{	int[] lnRijRes = lnRij;
		lnRij = new int[lnRijAantal+2];
		for(int i=0 ; i<lnRijAantal ; i++)
		{	lnRij[i] = lnRijRes[i];
		}
		lnRij[0]++;
		lnRij[lnRijAantal] = m; lnRijAantal++;
		lnRij[lnRijAantal] = n; lnRijAantal++;
		
		lijnen[aantalLijnen] = new Lijn(hoekpunten[m],hoekpunten[n],kl);
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
	
	public void zetVulkleur(String vk)
	{
		vulkleur = vk;
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

class KubusKnot extends Veelvlak 
{	
	
	public KubusKnot(double f, double a)
	{	aantalHoekpunten=24;
		aantalVlakken=14;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		double v = f*Math.sqrt(3)/3;
		double w = f*(1-a)*Math.sqrt(3)/3;
		hoekpunten[0] = new Hoekpunt(-w,v,-v);
		hoekpunten[1] = new Hoekpunt(-v,w,-v);
		hoekpunten[2] = new Hoekpunt(-v,v,-w);
		hoekpunten[3] = new Hoekpunt(w,v,-v);
		hoekpunten[4] = new Hoekpunt(v,v,-w);
		hoekpunten[5] = new Hoekpunt(v,w,-v);
		hoekpunten[6] = new Hoekpunt(v,v,w);
		hoekpunten[7] = new Hoekpunt(w,v,v);
		hoekpunten[8] = new Hoekpunt(v,w,v);
		hoekpunten[9] = new Hoekpunt(-w,v,v);
		hoekpunten[10] = new Hoekpunt(-v,v,w);
		hoekpunten[11] = new Hoekpunt(-v,w,v);
		hoekpunten[12] = new Hoekpunt(-v,-w,-v);
		hoekpunten[13] = new Hoekpunt(-w,-v,-v);
		hoekpunten[14] = new Hoekpunt(-v,-v,-w);
		hoekpunten[15] = new Hoekpunt(v,-w,-v);
		hoekpunten[16] = new Hoekpunt(v,-v,-w);
		hoekpunten[17] = new Hoekpunt(w,-v,-v);
		hoekpunten[18] = new Hoekpunt(v,-w,v);
		hoekpunten[19] = new Hoekpunt(w,-v,v);
		hoekpunten[20] = new Hoekpunt(v,-v,w);
		hoekpunten[21] = new Hoekpunt(-v,-w,v);
		hoekpunten[22] = new Hoekpunt(-v,-v,w);
		hoekpunten[23] = new Hoekpunt(-w,-v,v);
		
		int[] vlakdata = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
		for(int i=0 ; i<8 ; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[3*i]],hoekpunten[vlakdata[3*i+1]],hoekpunten[vlakdata[3*i+2]]};
			vlakken[i+6] = new Vlak(3,vp);
			vlakken[i+6].vorigeKleur = "groen";
			vlakken[i+6].vulkleur = "groen";
		}
		int[] vlakdata1 = {0,2,10,9,7,6,4,3,
						   1,0,3,5,15,17,13,12,
							5,4,6,8,18,20,16,15,
							8,7,9,11,21,23,19,18,
							11,10,2,1,12,14,22,21,
							14,13,17,16,20,19,23,22	};
		for(int i=0 ; i<6 ; i++)
		{	Hoekpunt[] vp1 = {hoekpunten[vlakdata1[8*i]],hoekpunten[vlakdata1[8*i+1]],hoekpunten[vlakdata1[8*i+2]],hoekpunten[vlakdata1[8*i+3]],
			hoekpunten[vlakdata1[8*i+4]],hoekpunten[vlakdata1[8*i+5]],hoekpunten[vlakdata1[8*i+6]],hoekpunten[vlakdata1[8*i+7]]};
			vlakken[i] = new Vlak(8,vp1);
			vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
		int[] hoekpuntdata = {0,1,6, 1,4,6, 4,0,6, 1,0,7, 0,2,7, 2,1,7, 2,0,8, 0,3,8, 3,2,8, 3,0,9, 0,4,9, 4,3,9, 4,1,10, 1,5,10, 5,4,10, 1,2,11, 2,5,11, 5,1,11, 2,3,12, 3,5,12, 5,2,12, 3,4,13, 4,5,13, 5,3,13};
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	Vlak[] hv = {vlakken[hoekpuntdata[3*i]],vlakken[hoekpuntdata[3*i+1]],vlakken[hoekpuntdata[3*i+2]]};
			hoekpunten[i].maakVlakken(3,hv);
			
		}
	}
	
}
class OctaederKnot extends Veelvlak 
{	
	public OctaederKnot(double f, double a)
	{	aantalHoekpunten=24;
		aantalVlakken=14;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		double v = f*Math.sqrt(3)/3;
		double w = f*(1-a)*Math.sqrt(3)/3;
		hoekpunten[0] = new Hoekpunt(-w,v,0);
		hoekpunten[1] = new Hoekpunt(0,v,w);
		hoekpunten[2] = new Hoekpunt(w,v,0);
		hoekpunten[3] = new Hoekpunt(0,v,-w);
		hoekpunten[4] = new Hoekpunt(0,w,-v);
		hoekpunten[5] = new Hoekpunt(w,0,-v);
		hoekpunten[6] = new Hoekpunt(0,-w,-v);
		hoekpunten[7] = new Hoekpunt(-w,0,-v);
		hoekpunten[8] = new Hoekpunt(v,w,0);
		hoekpunten[9] = new Hoekpunt(v,0,w);
		hoekpunten[10] = new Hoekpunt(v,-w,0);
		hoekpunten[11] = new Hoekpunt(v,0,-w);
		hoekpunten[12] = new Hoekpunt(0,w,v);
		hoekpunten[13] = new Hoekpunt(-w,0,v);
		hoekpunten[14] = new Hoekpunt(0,-w,v);
		hoekpunten[15] = new Hoekpunt(w,0,v);
		hoekpunten[16] = new Hoekpunt(-v,w,0);
		hoekpunten[17] = new Hoekpunt(-v,0,-w);
		hoekpunten[18] = new Hoekpunt(-v,-w,0);
		hoekpunten[19] = new Hoekpunt(-v,0,w);
		hoekpunten[20] = new Hoekpunt(0,-v,-w);
		hoekpunten[21] = new Hoekpunt(w,-v,0);
		hoekpunten[22] = new Hoekpunt(0,-v,w);
		hoekpunten[23] = new Hoekpunt(-w,-v,0);
		
		int[] vlakdata = {0,1,2,3, 4,5,6,7, 8,9,10,11, 12,13,14,15, 16,17,18,19, 20,21,22,23};
		for(int i=0 ; i<6 ; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[4*i]],hoekpunten[vlakdata[4*i+1]],hoekpunten[vlakdata[4*i+2]],hoekpunten[vlakdata[4*i+3]]};
			vlakken[i] = new Vlak(4,vp);
			vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
		int[] vlakdata1 = {0,3,4,7,17,16, 3,2,8,11,5,4, 2,1,12,15,9,8, 1,0,16,19,13,12, 17,7,6,20,23,18, 5,11,10,21,20,6, 9,15,14,22,21,10, 13,19,18,23,22,14};
		for(int i=0 ; i<8 ; i++)
		{	Hoekpunt[] vp1 = {hoekpunten[vlakdata1[6*i]],hoekpunten[vlakdata1[6*i+1]],hoekpunten[vlakdata1[6*i+2]],hoekpunten[vlakdata1[6*i+3]],
			hoekpunten[vlakdata1[6*i+4]],hoekpunten[vlakdata1[6*i+5]]};
			vlakken[i+6] = new Vlak(6,vp1);
			vlakken[i+6].vorigeKleur = "groen";
			vlakken[i+6].vulkleur = "groen";
		}
		int[] hoekpuntdata = {0,6,9, 0,9,8, 0,8,7, 0,7,6, 1,6,7, 1,7,11, 1,11,10, 1,10,6, 2,7,8, 2,8,12, 2,12,11, 2,11,7, 3,8,9, 3,9,13, 3,13,12, 3,12,8, 4,9,6, 4,6,10, 4,10,13, 4,13,9, 5,10,11, 5,11,12, 5,12,13, 5,13,10};
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	Vlak[] hv = {vlakken[hoekpuntdata[3*i]],vlakken[hoekpuntdata[3*i+1]],vlakken[hoekpuntdata[3*i+2]]};
			hoekpunten[i].maakVlakken(3,hv);
			
		}
	}
	
}
class Kuboctaeder extends Veelvlak 
{	
	
	public Kuboctaeder(double f)
	{	aantalHoekpunten=12;
		aantalVlakken=14;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		double v = f*Math.sqrt(3)/3;
		hoekpunten[0] = new Hoekpunt(-v,v,0);
		hoekpunten[1] = new Hoekpunt(0,v,-v);
		hoekpunten[2] = new Hoekpunt(v,v,0);
		hoekpunten[3] = new Hoekpunt(0,v,v);
		hoekpunten[4] = new Hoekpunt(-v,0,-v);
		hoekpunten[5] = new Hoekpunt(v,0,-v);
		hoekpunten[6] = new Hoekpunt(v,0,v);
		hoekpunten[7] = new Hoekpunt(-v,0,v);
		hoekpunten[8] = new Hoekpunt(-v,-v,0);
		hoekpunten[9] = new Hoekpunt(0,-v,-v);
		hoekpunten[10] = new Hoekpunt(v,-v,0);
		hoekpunten[11] = new Hoekpunt(0,-v,v);
		
		
		int[] vlakdata = {0,1,4, 1,2,5, 2,3,6, 3,0,7, 4,9,8, 5,10,9, 6,11,10, 7,8,11};
		for(int i=0 ; i<8 ; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[3*i]],hoekpunten[vlakdata[3*i+1]],hoekpunten[vlakdata[3*i+2]]};
			vlakken[i] = new Vlak(3,vp);
			vlakken[i].vorigeKleur = "groen";
			vlakken[i].vulkleur = "groen";
		}
		int[] vlakdata1 = {0,3,2,1, 1,5,9,4, 2,6,10,5, 3,7,11,6, 0,4,8,7, 8,9,10,11};
		for(int i=0 ; i<6 ; i++)
		{	Hoekpunt[] vp1 = {hoekpunten[vlakdata1[4*i]],hoekpunten[vlakdata1[4*i+1]],hoekpunten[vlakdata1[4*i+2]],hoekpunten[vlakdata1[4*i+3]]};
			vlakken[i+8] = new Vlak(4,vp1);
			vlakken[i+8].vorigeKleur = "geel";
			vlakken[i+8].vulkleur = "geel";
		}
		int[] hoekpuntdata = {0,12,3,8, 1,9,0,8, 2,10,1,8, 3,11,2,8, 0,9,4,12, 1,10,5,9, 2,11,6,10, 3,12,7,11, 12,4,13,7, 9,5,13,4, 10,6,13,5, 11,7,13,6};
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	Vlak[] hv = {vlakken[hoekpuntdata[4*i]],vlakken[hoekpuntdata[4*i+1]],vlakken[hoekpuntdata[4*i+2]],vlakken[hoekpuntdata[4*i+3]]};
			hoekpunten[i].maakVlakken(4,hv);
		}
	}
	
}
class IcosaederKnot extends Veelvlak
{	Hoekpunt[] p;
	
	public IcosaederKnot(double f,double a)
	{	aantalHoekpunten=60;
		aantalVlakken=32;
		p = new Hoekpunt[12];
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		
		
		double pi = Math.PI;
		double ct = (Math.cos(2*pi/5))/(1-Math.cos(2*pi/5));
		double theta = Math.acos(ct);
		double st = Math.sin(theta);
				
		p[0] = new Hoekpunt(0,f*1,0);
		p[1] = new Hoekpunt(f*st,f*ct,0);
		p[2] = new Hoekpunt(f*st*Math.cos(2*pi/5), f*ct, f*st*Math.sin(2*pi/5));
		p[3] = new Hoekpunt(f*st*Math.cos(4*pi/5), f*ct, f*st*Math.sin(4*pi/5));
		p[4] = new Hoekpunt(f*st*Math.cos(6*pi/5), f*ct, f*st*Math.sin(6*pi/5));
		p[5] = new Hoekpunt(f*st*Math.cos(8*pi/5), f*ct, f*st*Math.sin(8*pi/5));
		p[6] = new Hoekpunt(f*st*Math.cos(1*pi/5), -f*ct,f*st*Math.sin(1*pi/5));
		p[7] = new Hoekpunt(f*st*Math.cos(3*pi/5), -f*ct, f*st*Math.sin(3*pi/5));
		p[8] = new Hoekpunt(f*st*Math.cos(5*pi/5), -f*ct, f*st*Math.sin(5*pi/5));
		p[9] = new Hoekpunt(f*st*Math.cos(7*pi/5), -f*ct, f*st*Math.sin(7*pi/5));
		p[10] = new Hoekpunt(f*st*Math.cos(9*pi/5), -f*ct, f*st*Math.sin(9*pi/5));
		p[11] = new Hoekpunt(0, -f*1, 0);
	
		int[] pdata = {0,1,		0,5,	0,4,	0,3,	0,2,
					   1,0,		1,2,	1,6,	1,10,	1,5,
					   2,0,		2,3,	2,7,	2,6,	2,1,
					   3,0,		3,4,	3,8,	3,7,	3,2,
					   4,0,		4,5,	4,9,	4,8,	4,3,
					   5,0,		5,1,	5,10,	5,9,	5,4,
					   6,1,		6,2,	6,7,	6,11,	6,10,
					   7,2,		7,3,	7,8,	7,11,	7,6,
					   8,3,		8,4,	8,9,	8,11,	8,7,
					   9,4,		9,5,	9,10,	9,11,	9,8,
					   10,5,	10,1,	10,6,	10,11,	10,9,
					   11,6,	11,7,	11,8,	11,9,	11,10};
		
		for(int i=0 ; i<aantalHoekpunten ; i++)	
		{	double x = p[pdata[2*i]].x + 0.5*a*(p[pdata[2*i+1]].x - p[pdata[2*i]].x);
			double y = p[pdata[2*i]].y + 0.5*a*(p[pdata[2*i+1]].y - p[pdata[2*i]].y);
			double z = p[pdata[2*i]].z + 0.5*a*(p[pdata[2*i+1]].z - p[pdata[2*i]].z);
			hoekpunten[i] = new Hoekpunt(x,y,z);
		}
			
	
		for(int i=0 ; i<12 ; i++)
		{	Hoekpunt[] vp = {hoekpunten[5*i],hoekpunten[5*i+1],hoekpunten[5*i+2],hoekpunten[5*i+3],hoekpunten[5*i+4]};
			vlakken[i] = new Vlak(5,vp);
			vlakken[i].vorigeKleur = "groen";
			vlakken[i].vulkleur = "groen";
		}
		int[] vlakdata = {0,4,10,14,6,5,	4,3,15,19,11,10,	3,2,20,24,16,15,	2,1,25,29,21,20,	1,0,5,9,26,25,
						  6,14,13,31,30,7,	11,19,18,36,35,12,	16,24,23,41,40,17,	21,29,28,46,45,22,	26,9,8,51,50,27,
						  13,12,35,39,32,31,18,17,40,44,37,36,	23,22,45,49,42,41,	28,27,50,54,47,46,	8,7,30,34,52,51,
						  32,39,38,56,55,33,37,44,43,57,56,38,	42,49,48,58,57,43,	47,54,53,59,58,48,	52,34,33,55,59,53};
		
		for(int i=0 ; i<20 ; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[6*i]],hoekpunten[vlakdata[6*i+1]],hoekpunten[vlakdata[6*i+2]],hoekpunten[vlakdata[6*i+3]],hoekpunten[vlakdata[6*i+4]],hoekpunten[vlakdata[6*i+5]]};
			vlakken[i+12] = new Vlak(6,vp);
			vlakken[i+12].vorigeKleur = "geel";
			vlakken[i+12].vulkleur = "geel";
		}
		int[] hoekpuntdata = {0,12,16, 0,16,15, 0,15,14, 0,14,13, 0,13,12, 1,16,12, 1,12,17, 1,17,26, 1,26,21, 1,21,16,
							  2,12,13, 2,13,18, 2,18,22, 2,22,17, 2,17,12, 3,13,14, 3,14,19, 3,19,23, 3,23,18, 3,18,13,
							  4,14,15, 4,15,20, 4,20,24, 4,24,19, 4,19,14, 5,15,16, 5,16,21, 5,21,25, 5,25,20, 5,20,15,
							  6,26,17, 6,17,22, 6,22,27, 6,27,31, 6,31,26, 7,22,18, 7,18,23, 7,23,28, 7,28,27, 7,27,22,
							  8,23,19, 8,19,24, 8,24,29, 8,29,28, 8,28,23, 9,24,20, 9,20,25, 9,25,30, 9,30,29, 9,29,24,
							  10,25,21,10,21,26,10,26,31,10,31,30,10,30,25,11,31,27,11,27,28,11,28,29,11,29,30,11,30,31,};
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	Vlak[] hv = {vlakken[hoekpuntdata[3*i]],vlakken[hoekpuntdata[3*i+1]],vlakken[hoekpuntdata[3*i+2]]};
			hoekpunten[i].maakVlakken(3,hv);
		}

	}
}
class DodecaederKnot extends Veelvlak
{	Hoekpunt[] p;
	
	public DodecaederKnot(double f,double a)
	{	aantalHoekpunten=60;
		aantalVlakken=32;
		p = new Hoekpunt[12];
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		
		
		double pi = Math.PI;
		double ct = (Math.cos(2*pi/5))/(1-Math.cos(2*pi/5));
		double theta = Math.acos(ct);
		double st = Math.sin(theta);
				
		p[0] = new Hoekpunt(0,f*1,0);
		p[1] = new Hoekpunt(f*st,f*ct,0);
		p[2] = new Hoekpunt(f*st*Math.cos(2*pi/5), f*ct, f*st*Math.sin(2*pi/5));
		p[3] = new Hoekpunt(f*st*Math.cos(4*pi/5), f*ct, f*st*Math.sin(4*pi/5));
		p[4] = new Hoekpunt(f*st*Math.cos(6*pi/5), f*ct, f*st*Math.sin(6*pi/5));
		p[5] = new Hoekpunt(f*st*Math.cos(8*pi/5), f*ct, f*st*Math.sin(8*pi/5));
		p[6] = new Hoekpunt(f*st*Math.cos(1*pi/5), -f*ct,f*st*Math.sin(1*pi/5));
		p[7] = new Hoekpunt(f*st*Math.cos(3*pi/5), -f*ct, f*st*Math.sin(3*pi/5));
		p[8] = new Hoekpunt(f*st*Math.cos(5*pi/5), -f*ct, f*st*Math.sin(5*pi/5));
		p[9] = new Hoekpunt(f*st*Math.cos(7*pi/5), -f*ct, f*st*Math.sin(7*pi/5));
		p[10] = new Hoekpunt(f*st*Math.cos(9*pi/5), -f*ct, f*st*Math.sin(9*pi/5));
		p[11] = new Hoekpunt(0, -f*1, 0);
	
		int[] pdata = {1,0,2, 0,2,1, 2,1,0,		2,0,3, 0,3,2, 3,2,0,	3,0,4, 0,4,3, 4,3,0,	4,0,5, 0,5,4, 5,4,0,	5,0,1, 0,1,5, 1,5,0,	1,2,6, 2,6,1, 6,1,2,	2,3,7, 3,7,2, 7,2,3,
					   3,4,8, 4,8,3, 8,3,4,		4,5,9, 5,9,4, 9,4,5,	5,1,10,1,10,5,10,5,1,	6,2,7, 2,7,6, 7,6,2,	7,3,8, 3,8,7, 8,7,3,	8,4,9, 4,9,8, 9,8,4,	9,5,10,5,10,9,10,9,5,
					   10,1,6,1,6,10,6,10,1,	6,7,11,7,11,6,11,6,7,	7,8,11,8,11,7,11,7,8,	8,9,11,9,11,8,11,8,9,	9,10,11,10,11,9,11,9,10,10,6,11,6,11,10,11,10,6	};
		
		for(int i=0 ; i<aantalHoekpunten ; i++)	
		{	double x = p[pdata[3*i]].x + 0.5*(p[pdata[3*i+1]].x - p[pdata[3*i]].x) + a*((p[pdata[3*i+2]].x - p[pdata[3*i]].x) + (p[pdata[3*i+2]].x - p[pdata[3*i+1]].x))/6;
			double y = p[pdata[3*i]].y + 0.5*(p[pdata[3*i+1]].y - p[pdata[3*i]].y) + a*((p[pdata[3*i+2]].y - p[pdata[3*i]].y) + (p[pdata[3*i+2]].y - p[pdata[3*i+1]].y))/6;
			double z = p[pdata[3*i]].z + 0.5*(p[pdata[3*i+1]].z - p[pdata[3*i]].z) + a*((p[pdata[3*i+2]].z - p[pdata[3*i]].z) + (p[pdata[3*i+2]].z - p[pdata[3*i+1]].z))/6;
			hoekpunten[i] = new Hoekpunt(x,y,z);
		}
			
	
		for(int i=0 ; i<20 ; i++)
		{	Hoekpunt[] vp = {hoekpunten[3*i],hoekpunten[3*i+1],hoekpunten[3*i+2]};
			vlakken[i+12] = new Vlak(3,vp);
			vlakken[i+12].vorigeKleur = "geel";
			vlakken[i+12].vulkleur = "geel";
		}
		int[] vlakdata = {13,12,10,9,7,		6,4,3,1,0,
						  0,2,15,17,43,		42,28,27,14,13,
						  3,5,18,20,31,		30,16,15,2,1,
						  6,8,21,23,34,		33,19,18,5,4,
						  9,11,24,26,37,	36,22,21,8,7,	
						  12,14,27,29,40,	39,25,24,11,10,
						  17,16,30,32,45,	47,58,57,44,43,
						  20,19,33,35,48,	50,46,45,32,31,
						  23,22,36,38,51,	53,49,48,35,34,
						  26,25,39,41,54,	56,52,51,38,37,
						  29,28,42,44,57,	59,55,54,41,40,
						  47,46,50,49,53,	52,56,55,59,58};
		
		for(int i=0 ; i<12 ; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[10*i]],hoekpunten[vlakdata[10*i+1]],hoekpunten[vlakdata[10*i+2]],hoekpunten[vlakdata[10*i+3]],hoekpunten[vlakdata[10*i+4]],hoekpunten[vlakdata[10*i+5]],hoekpunten[vlakdata[10*i+6]],hoekpunten[vlakdata[10*i+7]],hoekpunten[vlakdata[10*i+8]],hoekpunten[vlakdata[10*i+9]]};
			vlakken[i] = new Vlak(10,vp);
			vlakken[i].vorigeKleur = "groen";
			vlakken[i].vulkleur = "groen";
		}
		//int[] hoekpuntdata = {0,12,16, 0,16,15, 0,15,14, 0,14,13, 0,13,12, 1,16,12, 1,12,17, 1,17,26, 1,26,21, 1,21,16,
		//					  2,12,13, 2,13,18, 2,18,22, 2,22,17, 2,17,12, 3,13,14, 3,14,19, 3,19,23, 3,23,18, 3,18,13,
		//					  4,14,15, 4,15,20, 4,20,24, 4,24,19, 4,19,14, 5,15,16, 5,16,21, 5,21,25, 5,25,20, 5,20,15,
		//					  6,26,17, 6,17,22, 6,22,27, 6,27,31, 6,31,26, 7,22,18, 7,18,23, 7,23,28, 7,28,27, 7,27,22,
		//					  8,23,19, 8,19,24, 8,24,29, 8,29,28, 8,28,23, 9,24,20, 9,20,25, 9,25,30, 9,30,29, 9,29,24,
		//					  10,25,21,10,21,26,10,26,31,10,31,30,10,30,25,11,31,27,11,27,28,11,28,29,11,29,30,11,30,31,};
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	Vlak[] hv = {vlakken[12+i/3],vlakken[pdata[3*i]],vlakken[pdata[3*i+1]]};
			hoekpunten[i].maakVlakken(3,hv);
		}

	}
}
class Icosidodecaeder extends Veelvlak
{	Hoekpunt[] p;
	
	public Icosidodecaeder(double f)
	{	aantalHoekpunten=30;
		aantalVlakken=32;
		p = new Hoekpunt[12];
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		
		
		double pi = Math.PI;
		double ct = (Math.cos(2*pi/5))/(1-Math.cos(2*pi/5));
		double theta = Math.acos(ct);
		double st = Math.sin(theta);
				
		p[0] = new Hoekpunt(0,f*1,0);
		p[1] = new Hoekpunt(f*st,f*ct,0);
		p[2] = new Hoekpunt(f*st*Math.cos(2*pi/5), f*ct, f*st*Math.sin(2*pi/5));
		p[3] = new Hoekpunt(f*st*Math.cos(4*pi/5), f*ct, f*st*Math.sin(4*pi/5));
		p[4] = new Hoekpunt(f*st*Math.cos(6*pi/5), f*ct, f*st*Math.sin(6*pi/5));
		p[5] = new Hoekpunt(f*st*Math.cos(8*pi/5), f*ct, f*st*Math.sin(8*pi/5));
		p[6] = new Hoekpunt(f*st*Math.cos(1*pi/5), -f*ct,f*st*Math.sin(1*pi/5));
		p[7] = new Hoekpunt(f*st*Math.cos(3*pi/5), -f*ct, f*st*Math.sin(3*pi/5));
		p[8] = new Hoekpunt(f*st*Math.cos(5*pi/5), -f*ct, f*st*Math.sin(5*pi/5));
		p[9] = new Hoekpunt(f*st*Math.cos(7*pi/5), -f*ct, f*st*Math.sin(7*pi/5));
		p[10] = new Hoekpunt(f*st*Math.cos(9*pi/5), -f*ct, f*st*Math.sin(9*pi/5));
		p[11] = new Hoekpunt(0, -f*1, 0);
	
		int[] pdata = {0,1,0,5,0,4,0,3,0,2,		1,2,2,3,3,4,4,5,1,5,	1,6,2,6,2,7,3,7,3,8,	
					   4,8,4,9,5,9,5,10,1,10,	6,7,7,8,8,9,9,10,6,10,	6,11,7,11,8,11,9,11,10,11};
		
		for(int i=0 ; i<aantalHoekpunten ; i++)	
		{	double x = p[pdata[2*i]].x + 0.5*(p[pdata[2*i+1]].x - p[pdata[2*i]].x);
			double y = p[pdata[2*i]].y + 0.5*(p[pdata[2*i+1]].y - p[pdata[2*i]].y);
			double z = p[pdata[2*i]].z + 0.5*(p[pdata[2*i+1]].z - p[pdata[2*i]].z);
			hoekpunten[i] = new Hoekpunt(x,y,z);
		}
		
		int[] vlakdata = {0,1,2,3,4,		0,5,10,19,9,	4,6,12,11,5,	3,7,14,13,6,	2,8,16,15,7,	1,9,18,17,8,
						  10,11,20,25,24,	12,13,21,26,20,	14,15,22,27,21,	16,17,23,28,22,	18,19,24,29,23,	25,26,27,28,29,};
		
		for(int i=0 ; i<12 ; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[5*i]],hoekpunten[vlakdata[5*i+1]],hoekpunten[vlakdata[5*i+2]],hoekpunten[vlakdata[5*i+3]],hoekpunten[vlakdata[5*i+4]]};
			vlakken[i] = new Vlak(5,vp);
			vlakken[i].vorigeKleur = "groen";
			vlakken[i].vulkleur = "groen";
		}
		
		int[] vlakdata1 = {0,4,5,		4,3,6,		3,2,7,		2,1,8,		1,0,9,		5,11,10,	6,13,12,	7,15,14,	8,17,16,	 9,19,18,
						   11,12,20,	13,14,21,	15,16,22,	17,18,23,	19,10,24,	20,26,25,	21,27,26,	22,28,27,	23,29,28,	24,25,29};
		
		for(int i=0 ; i<20 ; i++)
		{	Hoekpunt[] vp1 = {hoekpunten[vlakdata1[3*i]],hoekpunten[vlakdata1[3*i+1]],hoekpunten[vlakdata1[3*i+2]]};
			vlakken[i+12] = new Vlak(3,vp1);
			vlakken[i+12].vorigeKleur = "geel";
			vlakken[i+12].vulkleur = "geel";
		}
		int[] hoekpuntdata = {0,12,1,16,	0,16,5,15,	0,15,4,14,	0,14,3,13,	0,13,2,12,	1,12,2,17,	2,13,3,18,	3,14,4,19,	4,15,5,20,	5,16,1,21,
							  1,17,6,26,	2,22,6,17,	2,18,7,22,	3,23,7,18,	3,19,8,23,	4,24,8,19,	4,20,9,24,	5,25,9,20,	5,21,10,25,	1,26,10,21,
							  6,22,7,27,	7,23,8,28,	8,24,9,29,	9,25,10,30,	10,26,6,31,	6,27,11,31,	7,28,11,27,	8,29,11,28,	9,30,11,29,	10,31,11,30};
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	Vlak[] hv = {vlakken[hoekpuntdata[4*i]],vlakken[hoekpuntdata[4*i+1]],vlakken[hoekpuntdata[4*i+2]],vlakken[hoekpuntdata[4*i+3]]};
			hoekpunten[i].maakVlakken(4,hv);
		}

	}
}
