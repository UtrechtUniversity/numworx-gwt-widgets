package fi.tekenveelvlakgwt.client;

/**
 * niet gebruikt
 * @author Peter Boon
 */
public class KubusKnot extends Veelvlak 
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
