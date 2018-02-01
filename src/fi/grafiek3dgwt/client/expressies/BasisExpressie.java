package fi.grafiek3dgwt.client.expressies;

import java.util.*;

import fi.grafiek3dgwt.client.StringUtils;
import fi.grafiek3dgwt.client.Grafiek3DGWT;
import fi.grafiek3dgwt.client.UF;
import fi.grafiek3dgwt.client.formuleobjects.Letter;

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
		basisString = UF.format(d,10);
		if (!Algebra.withinLongRange((long) waarde))
		{	
			basisString = UF.format(d,10);
		}
		
		
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
	
	public boolean isVar()
	{	return Letter.isLetter(basisString.charAt(0));
	}
	
	public double geefWaarde()
	{	return waarde;
	}
	
	public Complex geefWaardeComplex()
	{	if(!Double.isNaN(waarde)) return new Complex(waarde);
		else if(Double.isNaN(waarde) &&  basisString.equals("i"))return new Complex(0,1);
		else return null;
	}

	public double geefWaarde(double subst)
	{	if(Double.isNaN(waarde))return subst;
		else return waarde;
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	if(basisString.equals("i")) return new Complex(0,1);
		else if(Double.isNaN(waarde))return new Complex(subst);
		else return new Complex(waarde);
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
	
	public Complex geefWaardeComplex(Complex[] subst, String[] vars)
	{	if(!Double.isNaN(waarde))return new Complex(waarde);
		else 
		{	for(int i=0 ; i<vars.length ; i++)
			{	if(basisString.equals("i") &&  basisString.equals(vars[i]))
				{	return new Complex(0,1);
				}
				else if(basisString.equals(vars[i]))
				{	return new Complex(subst[i]);
					
				}
			}
			return null;
		}
	}
	
	public Expressie substitueer(double subst, String var)
	{	if (basisString.equals(var))
		{	return new BasisExpressie(subst);
		}
		else return new BasisExpressie(basisString);
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	if (basisString.equals(var))
		{	return subst;
		}
		else 
			return new BasisExpressie(basisString);
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
	{	
		String basisStringUit = StringUtils.replaceStr(basisString, "?(", "$s");
		basisStringUit = StringUtils.replaceStr(basisStringUit, ")" , "@");
		
		if (!Double.isNaN(waarde) && (!Algebra.withinLongRange((long)waarde) || basisString.indexOf('E') > -1))
			basisStringUit = StringUtils.replaceStr(basisString, "E", "*$p10$n") + "@@";
		if(Grafiek3DGWT.languageString.equals("nl"))
        	basisStringUit = basisStringUit.replace('.',',');
        
		 
        return basisStringUit;
	}
	
	public String toStringStrikt()
	{	
		String basisStringUit = StringUtils.replaceStr(basisString,"?(","$s");
        basisStringUit = StringUtils.replaceStr(basisStringUit,")","@");
        
        if(!Double.isNaN(waarde) && (!Algebra.withinLongRange((long)waarde) || basisString.indexOf('E')>-1))basisStringUit = StringUtils.replaceStr(basisString,"E","*$p10$n") + "@@";

        if(Grafiek3DGWT.languageString.equals("nl"))
        	basisStringUit = basisStringUit.replace('.',',');
        return basisStringUit;
	}
    
    public String toStringCAS()
    {   basisString = basisString.replace(',','.');
    	if(basisString.equals("\u221e")) return("Infinity");
    	if(basisString.equals("-\u221e")) return("-Infinity");
        return basisString;
    }

}
