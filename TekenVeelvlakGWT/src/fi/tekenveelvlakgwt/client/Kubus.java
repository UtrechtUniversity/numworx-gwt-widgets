package fi.tekenveelvlakgwt.client;

/**
 * gebruikt voor basisfiguur
 * @author Peter Boon
 */
public class Kubus extends Veelvlak 
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
