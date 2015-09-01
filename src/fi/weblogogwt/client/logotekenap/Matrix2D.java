package fi.weblogogwt.client.logotekenap;

public class Matrix2D
{
	//-------------------------------------------------------------------------------------------
	//deze klasse onthoudt, en berekent steeds opnieuw de tekenrichting, en berekent voor het 
	//Tekenblad aan de hand van een dx en dy het volgende eindpunt van de tekenlijn.
	//-------------------------------------------------------------------------------------------
	
	private double  starthoek, hoek, radHoek, cosHoek, sinHoek, startschaal, schaal;
	private double a11,a12,a21,a22;
 	private static double pi = Math.PI;

	public Matrix2D()
	{	starthoek = 0;
		hoek = 0;
		startschaal = 1;
		schaal = 1;
		radHoek = 0; 
		cosHoek = 1; 
		sinHoek = 0;
		a11 = 1; a12 = 0; a21 = 0; a22 = 1;
	}
	
	public Matrix2D(double hk, double schl)
	{	initialiseer(hk, schl);
	}
	
	public void initialiseer(double hk,double schl)
	{	starthoek = hk;
		startschaal = schl;
		initialiseer();
	}
	
	public void initialiseer()
	{	hoek = starthoek;
		schaal = startschaal;
		draai(0);
	}
	
	private void maakMatrix()
	{	a11 = schaal*cosHoek;
		a12 = -schaal*sinHoek;
		a21 = schaal*sinHoek;
		a22 = schaal*cosHoek;
	}
	
	public void draai(double dHoek)
	{ 	hoek = hoek - dHoek;
		radHoek = hoek/180*pi;
		cosHoek = (double)Math.cos(radHoek);
		sinHoek = (double)Math.sin(radHoek);
		maakMatrix();
	}
	
	public void schaal(double s)
	{	schaal = startschaal*s;
		maakMatrix();
	}
	
	public Punt geefVolgendPunt(Punt beginp, double dx, double dy)
	{	Punt eindp = new Punt(beginp.x + a11*dx + a12*dy , beginp.y + a21*dx + a22*dy);
		return eindp;
	}	
}	

