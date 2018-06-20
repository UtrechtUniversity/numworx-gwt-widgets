package fi.tekenveelvlakgwt.client;

/**
 * niet gebruikt
 * @author Peter Boon
 */
public class Kuboctaeder extends Veelvlak 
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
