package fi.nabouwenaanzichtengwt.client;

public class RVierkant extends Veelvlak{

	Punt3D positie;
	double d;
	public RVierkant(double r, double x, double y, double z)
	{	aantalHoekpunten=4;
		aantalVlakken=1;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		d = r/2;
		hoekpunten[0] = new Hoekpunt(-d,0,-d);
		hoekpunten[1] = new Hoekpunt(d,0,-d);
		hoekpunten[2] = new Hoekpunt(d,0,d);
		hoekpunten[3] = new Hoekpunt(-d,0,d);
		
		
		verschuif(x,y,z);
		
		int[] vlakdata = {0,3,2,1};
		for(int i=0 ; i<aantalVlakken ; i++)
		{	Hoekpunt[] vp = {hoekpunten[vlakdata[4*i]],hoekpunten[vlakdata[4*i+1]],hoekpunten[vlakdata[4*i+2]],hoekpunten[vlakdata[4*i+3]]};
			vlakken[i] = new Vlak(4,vp);
			vlakken[i].vorigeKleur = "lichtgrijs";
			vlakken[i].vulkleur = "lichtgrijs";
		}
		
		//int[] hoekpuntdata = {0,1,4,	0,2,1,	0,3,2,	0,4,3,	5,4,1,	5,1,2,	5,2,3,	5,3,4};
		//for(int i=0 ; i<aantalHoekpunten ; i++)
		//{	Vlak[] hv = {vlakken[hoekpuntdata[3*i]],vlakken[hoekpuntdata[3*i+1]],vlakken[hoekpuntdata[3*i+2]]};
		//	hoekpunten[i].maakVlakken(3,hv);
		//}
		
		
															 
	}
	public void verschuif(double dx,double dy,double dz)
	{	for(int i=0 ; i<aantalHoekpunten ; i++)
		{	hoekpunten[i].x += dx;
			hoekpunten[i].y += dy;
			hoekpunten[i].z += dz;
		}
	}
}
