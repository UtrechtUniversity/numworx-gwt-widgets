package fi.tekenveelvlakgwt.client;

/**
 * niet gebruikt 
 * @author Peter Boon
 */
public class AntiPrisma extends Veelvlak 
{	
	public AntiPrisma(double f, int n, double h)
	{	
		aantalHoekpunten=2*n;
		aantalVlakken=2*n+2;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		
		for(int i=0 ; i<n ; i++)
		{	double hoek = 2*i*Math.PI/n;
			double a = f*Math.cos(hoek);
			double b = f*Math.sin(hoek);
			double hoogte = 2*f*h;
			hoekpunten[i] = new Hoekpunt(a,0.5*hoogte,b);
		}
		for(int i=0 ; i<n ; i++)
		{	double hoek = Math.PI/n + 2*i*Math.PI/n;
			double a = f*Math.cos(hoek);
			double b = f*Math.sin(hoek);
			double hoogte = 2*f*h;
			hoekpunten[n+i] = new Hoekpunt(a,-0.5*hoogte,b);
		}
		for(int i=0 ; i<n ; i++)
		{	Hoekpunt[] vp = {hoekpunten[i],hoekpunten[(i+1)%n],hoekpunten[n+i]};
			vlakken[i] = new Vlak(3,vp);
			vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
		for(int i=0 ; i<n ; i++)
		{	Hoekpunt[] vp = {hoekpunten[n+i],hoekpunten[n+((i-1+n)%n)],hoekpunten[i]};
			vlakken[n+i] = new Vlak(3,vp);
			vlakken[n+i].vorigeKleur = "geel";
			vlakken[n+i].vulkleur = "geel";
		}
		Hoekpunt[] vp = new Hoekpunt[n];
		for(int i=0 ; i<n ; i++)
		{	vp[i] = hoekpunten[n+i];
		}
		vlakken[2*n] = new Vlak(n,vp);
		for(int i=0 ; i<n ; i++)
		{	vp[n-1-i] = hoekpunten[i];
		}
		vlakken[2*n+1] = new Vlak(n,vp);
		
		for(int i=0 ; i<aantalVlakken ; i++)
		{	vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
	}
}
