package fi.nabouwenaanzichtengwt.client.expressies;

//import java.awt.*;
import java.util.*;

import fi.nabouwenaanzichtengwt.client.UF;

public class BasisExpressie extends Expressie  
{	String 	basisString;
	double waarde;
	
	public BasisExpressie()
	{
	}
	
	public BasisExpressie(String s)
	{	super();
		s = s.replace(',','.');
		basisString = s;
		isVeelterm = false;
		isProdukt = false;
		isBasis = true;
		waarde = geefW();
	}

	public BasisExpressie(double d)
	{	super();
		waarde = d;
		basisString = UF.format(d,8); //df.format(d);
		isVeelterm = false;
		isProdukt = false;
		isBasis = true;
	}
	
	public double geefW()
	{	double waarde = Double.NaN;
		try
		{	waarde = Double.valueOf(basisString).doubleValue();
		}
		catch(NumberFormatException e)
		{	
		}
		return waarde;
	}
	
	public double geefWaarde()
	{	return waarde;
	}

	public double geefWaarde(double subst)
	{	if(Double.isNaN(waarde))return subst;
		else return waarde;
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	if(!Double.isNaN(waarde))return waarde;
		else 
		{	for(int i=0 ; i<vars.length ; i++)
			{	if(basisString.equals(vars[i]))
				{	return subst[i];
				}
			}
			return Double.NaN;
		}
	}
	
	public Expressie substitueer(double subst, String var)
	{	if(basisString.equals(var))
		{	return new BasisExpressie(subst);
		}
		else return new BasisExpressie(basisString);
	}
	
	public boolean isWaarde(double subst)
	{	if(Double.isNaN(geefWaarde()))return false;
		return true;
	}
	
	public String geefVarNaam()
	{	if(Double.isNaN(geefWaarde()))return basisString;
		return null;
	}
	
	public Vector geefVarNamen()
	{	Vector v = new Vector();
		if(Double.isNaN(waarde))v.addElement(basisString);
		return v;
	}
	
	public String toString()
	{	basisString = basisString.replace('.',',');
		return basisString;
	}
	
	public String toStringStrikt()
	{	basisString = basisString.replace('.',',');
		return basisString;
	}

}
