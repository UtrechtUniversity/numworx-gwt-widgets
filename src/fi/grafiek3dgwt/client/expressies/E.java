package fi.grafiek3dgwt.client.expressies;

import java.awt.*;
import java.util.Vector;

public class E extends BasisExpressie  
{	
	
	public E()
	{	isVeelterm = false;
		isProdukt = false;
		isBasis = true;
	}
	
	public double geefWaarde()
	{	return Math.E;
	}
	
	public Complex geefWaardeComplex()
	{	return new Complex(Math.E);
	}
	
	public double geefWaarde(double subst)
	{	return Math.E;
	}
	
	public Complex geefWaardeComplex(double subst)
	{	return new Complex(Math.E);
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return Math.E;
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new E();
	}
	
	
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new E();
	}
	
	public boolean isWaarde(double subst)
	{	return true;
	}
	
	public String geefVarNaam()
	{	return null;
	}
	
	public Vector geefVarNamen()
	{	Vector v = new Vector();
		return v;
	}
	
	public String toString()
	{	return "e";
	}
	
	public String toStringStrikt()
	{	return "e";
	}
    
    public String toStringCAS()
    {   return "E";
    }
}
