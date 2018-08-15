package fi.nabouwenaanzichtengwt.client;

/**
 * klasse die een aangeklikt vlakje van een kubusje representeert:
 * de klasse bevat de positie van het kubusje en een code voor 
 * de positie van het vlakje binnen het kubusje 
 * @author Peter Boon
 */
public class Klikvlak
{	
	/**
	 * positie van het kubusje
	 */
	int i,j,k;
	/**
	 * aangeklikt vlakje van het kubusje
	 */
	int m;
	
	/**
	 * constructor
	 * @param i x-positie kubusje
	 * @param j y-positie kubusje
	 * @param k z-positie kubusje
	 * @param m index vlakje in kubusje
	 */
	public Klikvlak(int i,int j,int k,int m)
	{	this.i = i;
		this.j = j;
		this.k = k;
		this.m = m;
	}
}
