package fi.tekenveelvlakgwt.client;

/**
 * niet gebruikt
 * @author Peter Boon
 */
public class IcosaederKnot extends Veelvlak
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
