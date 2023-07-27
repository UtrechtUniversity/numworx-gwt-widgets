package fi.tekenveelvlakgwt.client;

/**
 * niet gebruikt
 * @author Peter Boon
 */
public class Pyramide extends Veelvlak 
{	
	public Pyramide(double f, int n, double h)
	{	aantalHoekpunten=n+1;
		aantalVlakken=n+1;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		double hoogte = 2*f*h;		
		for(int i=0 ; i<n ; i++)
		{	double hoek = 2*i*Math.PI/n;
			double a = f*Math.cos(hoek);
			double b = f*Math.sin(hoek);
			
			hoekpunten[i] = new Hoekpunt(a,-0.5*hoogte,b);
		}
		hoekpunten[n] = new Hoekpunt(0,0.5*hoogte,0);
		for(int i=0 ; i<n ; i++)
		{	Hoekpunt[] vp = {hoekpunten[n],hoekpunten[(i+1)%n],hoekpunten[i]};
			vlakken[i] = new Vlak(3,vp);
			vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
		Hoekpunt[] vp = new Hoekpunt[n];
		for(int i=0 ; i<n ; i++)
		{	vp[i] = hoekpunten[i];
		}
		vlakken[n] = new Vlak(n,vp);
				
		for(int i=0 ; i<aantalVlakken ; i++)
		{	vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
	}
}
