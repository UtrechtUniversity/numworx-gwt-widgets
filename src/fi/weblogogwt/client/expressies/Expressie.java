package fi.weblogogwt.client.expressies;


import com.google.gwt.canvas.dom.client.Context2d;

import fi.weblogogwt.client.UF;

public class Expressie 
{	
	public Expressie kind1, kind2;
	public String operatorString;
	boolean isVeelterm;
	boolean isProdukt;
	boolean isBasis;

	public static String fontType =  "sans-serif";
	public static int fontSize = 12; //px
	
	public static boolean hoekGraden = true;
	
	public Expressie()
	{	
	}
	public static String format(double d)
	{	
		return UF.format0(d,8);
		
	}

	public void zetMaat(int fs, Context2d c2d)
  	{
	}

	public void teken(Context2d g, int x, int y)
  	{ 
	}
	public double geefWaarde()
	{	return Double.NaN;
	}
	
	public double geefWaarde(double subst)
	{	return Double.NaN;
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return Double.NaN;
	}
	
	public Expressie substitueer(double subst, String var)
	{	return null;
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return null;
	}
	
	public boolean isWaarde(double subst)
	{	return true;
	}
	public String geefVarNaam()
	{	return null;
	}
	
	public boolean isVar()
	{	return this instanceof BasisExpressie;
	}
	
	public boolean isWaarde()
	{	return !Double.isNaN(geefWaarde());
	}
	
	public String toString()
	{	return null;
	}
	public String toStringStrikt()
	{	return null;
	}
	
	
}
