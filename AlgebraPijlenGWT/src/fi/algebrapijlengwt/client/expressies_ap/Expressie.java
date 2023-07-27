package fi.algebrapijlengwt.client.expressies_ap;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * super class voor alle Expressies 
 */
public class Expressie 
{	
	public int breedte;
	public int hoogte;
	public int ashoogte;
	public Expressie kind1, kind2;
	public String operatorString;
	boolean isVeelterm;
	boolean isProdukt;
	boolean isBasis;
	boolean isAsym;
	public static String fontType =  "sans-serif";
	public static int fontSize = 12; //px
	
	public static boolean isInteger(double d)
	{	return Math.abs(Math.rint(d)-d)<0.000000001;
	}
	
	public Expressie()
	{
	}
	/**
	 * 
	 * @param fs de font size (pixels)
	 * @param c2d de Contex2d die gebruikt wordt om te tekenen
	 */
	public void zetMaat(int fs, Context2d c2d)
  	{
	}

	public void teken(Context2d g, int x, int y)
  	{ 
	}
	
	public Double geefWaarde()
	{	return null;
	}
	
	public double geefW(double subst)
	{	return 0;
	}
	
	public boolean isWaarde(double subst)
	{	return true;
	}
	
	public boolean isWaarde()
	{	return !Double.isNaN(geefWaarde().doubleValue());
	}
	
	public String geefVarNaam()
	{	return null;
	}
	
	public Expressie substitueer(double subst, String var)
	{	return null;
	}
	
	public String toString()
	{	return null;
	}
	
	public String toStringStrikt()
	{	return null;
	}
	
}
