package fi.nabouwenaanzichtengwt.client;

/**
 * klasse die een vlak van een 3d-figuur representeert;
 * de klasse bevat de hoekpunten van het vlak (moeten in hetzelfde vlak liggen)
 * en de kleuren (vul- en outline) van dit vlak<br> 
 * @author Peter Boon
 */
public class Vlak 
{	
	int aantalHoekpunten;
	Hoekpunt[] punten;
	public String vulkleur,lijnkleur,vorigeKleur;
	
	public Vlak(int n,Hoekpunt[] pnt)
	{	vulkleur = "oranje";
		lijnkleur = "zwart";
		vorigeKleur = "oranje";
		aantalHoekpunten=n;
		punten = new Hoekpunt[aantalHoekpunten];
		for(int i=0 ; i<aantalHoekpunten ; i++)
		{	punten[i] = pnt[i];
		}
	}
	
	public void zetVulkleur(String kleur)
	{	vulkleur = kleur;
		vorigeKleur = kleur;
	}
}
