package fi.nabouwenaanzichtengwt.client;

/**
 * een rotatie in 3d-space.
 * Een 'record' (java 14)
 * @author Peter Boon
 */
public class Rotatie3D
{
	public final int as;
	public final double rotatieHoek;

	public Rotatie3D(int as, double rotatieHoek)
	{	this.as = as;
		this.rotatieHoek = rotatieHoek;
	}
}

