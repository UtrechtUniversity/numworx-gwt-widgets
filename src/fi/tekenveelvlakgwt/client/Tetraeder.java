package fi.tekenveelvlakgwt.client;

/**
 * niet gebruikt
 * @author Peter Boon
 */
public class Tetraeder extends Veelvlak 
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
