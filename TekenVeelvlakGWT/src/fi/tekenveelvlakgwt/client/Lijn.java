package fi.tekenveelvlakgwt.client;

/**
 * een lijn in 3d-space: de lijn word bepaals door 
 * twee verschillende punten
 * @author Peter Boon
 */
public class Lijn 
{	
	/**
	 * twee verschillende punten op de lijn
	 */
	Hoekpunt hpunt1,hpunt2;
	/**
	 * de kleur van de lijn
	 */
	String kleur;
	
	/**
	 * constructor
	 * @param h1 eerste punt op de lijn
	 * @param h2 tweede punt op de lijn
	 * @param kl kleur van de lijn
	 */
	public Lijn(Hoekpunt h1, Hoekpunt h2, String kl)
	{	hpunt1 = h1;
		hpunt2 = h2;
		kleur = kl;
	}
}
