package fi.tekenveelvlakgwt.client;

/**
 * niet gebruikt
 * @author Peter Boon
 */
public class OctaederKnot extends Veelvlak 
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
