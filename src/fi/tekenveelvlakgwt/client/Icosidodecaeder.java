package fi.tekenveelvlakgwt.client;

/**
 * niet gebruikt
 * @author Peter Boon
 */
public class Icosidodecaeder extends Veelvlak
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
