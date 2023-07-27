package fi.tekenveelvlakgwt.client;

/**
 * niet gebruikt
 * @author Peter Boon
 */
public class Prisma extends Veelvlak 
{	
    public Prisma(double f, int n, double h)
	{	aantalHoekpunten=2*n;
		aantalVlakken=n+2;
		hoekpunten = new Hoekpunt[aantalHoekpunten];
		vlakken = new Vlak[aantalVlakken];
		
		for(int i=0 ; i<n ; i++)
		{	double hoek = 2*i*Math.PI/n;
			double a = f*Math.cos(hoek);
			double b = f*Math.sin(hoek);
			double hoogte = 2*f*h;
			hoekpunten[2*i] = new Hoekpunt(a,0.5*hoogte,b);
			hoekpunten[2*i+1] = new Hoekpunt(a,-0.5*hoogte,b);
		}
		for(int i=0 ; i<n ; i++)
		{	Hoekpunt[] vp = {hoekpunten[2*i],hoekpunten[(2*(i+1))%(2*n)],hoekpunten[(2*(i+1)+1)%(2*n)],hoekpunten[(2*i+1)%(2*n)]};
			vlakken[i] = new Vlak(4,vp);
			vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
		Hoekpunt[] vp = new Hoekpunt[n];
		for(int i=0 ; i<n ; i++)
		{	vp[i] = hoekpunten[2*i+1];
		}
		vlakken[n] = new Vlak(n,vp);
		for(int i=0 ; i<n ; i++)
		{	vp[n-1-i] = hoekpunten[2*i];
		}
		vlakken[n+1] = new Vlak(n,vp);
		
		for(int i=0 ; i<aantalVlakken ; i++)
		{	vlakken[i].vorigeKleur = "geel";
			vlakken[i].vulkleur = "geel";
		}
	}
}
