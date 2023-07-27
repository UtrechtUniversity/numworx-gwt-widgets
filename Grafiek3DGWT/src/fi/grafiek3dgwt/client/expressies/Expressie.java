package fi.grafiek3dgwt.client.expressies;


import com.google.gwt.canvas.dom.client.Context2d;


public class Expressie 
{	
	public Expressie kind1, kind2, kind3, kind4;
	public String operatorString;
	boolean isVeelterm;
	boolean isProdukt;
	boolean isBasis;

	
	static boolean hoekGraden;
	
	public Expressie()
	{	
		
	}
	public static void zetHoekGraden(boolean b)
  	{	hoekGraden=b;
	}
	
	public void teken(Context2d g, int x, int y)
  	{ 
	}
	public double geefWaarde()
	{	return Double.NaN;
	}
	
	public Complex geefWaardeComplex()
	{	return null;
	}
	
	public double geefWaarde(double subst)
	{	return Double.NaN;
	}
	
	
	public Complex geefWaardeComplex(Complex subst)
	{	return null;
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return Double.NaN;
	}
	
	public Complex geefWaardeComplex(Complex[] subst, String[] vars)
	{	return null;
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
    
    public String toStringCAS()
    {   return null;
    }
    
	public String toStringStrikt()
	{	return null;
	}
	
	
}
