package fi.tekenveelvlakgwt.client;

/**
 * nier gebruikt
 * @author Peter Boon
 */
public class Icosaeder extends Veelvlak
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
