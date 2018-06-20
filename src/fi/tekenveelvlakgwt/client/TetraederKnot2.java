package fi.tekenveelvlakgwt.client;

/**
 * niet gebruikt
 * @author Peter Boon
 */
public class TetraederKnot2 extends Veelvlak 
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
