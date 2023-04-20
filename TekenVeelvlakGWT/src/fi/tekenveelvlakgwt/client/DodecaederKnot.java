package fi.tekenveelvlakgwt.client;

/**
 * niet gebruikt
 * @author Peter Boon
 */
public class DodecaederKnot extends Veelvlak
{	
	Hoekpunt[] p;
	
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
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	Vlak[] hv = {vlakken[12+i/3],vlakken[pdata[3*i]],vlakken[pdata[3*i+1]]};
			hoekpunten[i].maakVlakken(3,hv);
		}

	}
}
