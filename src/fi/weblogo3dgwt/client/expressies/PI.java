package fi.weblogo3dgwt.client.expressies;

import java.awt.*;
import java.util.Vector;

public class PI extends BasisExpressie  
{	
	
	public PI()
	{	isVeelterm = false;
		isProdukt = false;
		isBasis = true;
	}
	
	public double geefWaarde()
	{	return Math.PI;
	}
	
	public double geefWaarde(double subst)
	{	return Math.PI;
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return Math.PI;
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new PI();
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new PI();
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
	{	return "\u03C0";
	}
	
	public String toStringStrikt()
	{	return "\u03C0";
	}
}
