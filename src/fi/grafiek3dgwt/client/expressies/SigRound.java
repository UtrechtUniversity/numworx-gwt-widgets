package fi.grafiek3dgwt.client.expressies;

import java.awt.*;

public class SigRound extends Expressie  
{	
	
	public SigRound(Expressie e1, Expressie e2, Expressie e3 )
	{	kind1 = e1;
		kind2 = e2;
		kind3 = e3;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}
	
	public static double roundToSignificantFigures(double num, int n) {
	    if(num == 0) {
	        return 0;
	    }

	    final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
	    final int power = n - (int) d;

	    final double magnitude = Math.pow(10, power);
	    final long shifted = Math.round(num*magnitude);
	    return shifted/magnitude;
	}

	
	public double geefWaarde()
	{	
		double d = kind1.geefWaarde()*1.000000000000001;
		int aantal = (int)kind3.geefWaarde();
		return roundToSignificantFigures(d,aantal);
	}
	
	public Complex geefWaardeComplex(double subst)
	{	return new Complex(roundToSignificantFigures(kind1.geefWaarde(subst), (int)kind3.geefWaarde(subst)));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return roundToSignificantFigures(kind1.geefWaarde(subst,vars), (int)kind3.geefWaarde(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	SigRound exp = new SigRound(kind1.substitueer(subst,var), kind2.substitueer(subst,var), kind3.substitueer(subst,var));
		//if(exp.isWaarde())return new BasisExpressie(exp.geefWaarde());
		//else 
			return exp;
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	SigRound exp = new SigRound(kind1.substitueer(subst,var), kind2.substitueer(subst,var), kind3.substitueer(subst,var));
		//if(exp.isWaarde())return new BasisExpressie(exp.geefWaarde());
		//else 
			return exp;
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst) && kind2.isWaarde(subst) && kind3.isWaarde(subst);
	}
	
	public String geefVarNaam()
	{	String s1 = kind1.geefVarNaam();
		String s2 = kind2.geefVarNaam();
		if(s1!=null && s2!=null && (s1.equals("") || s2.equals("")))return "";
		else if(s1!=null && s2!=null && !s1.equals(s2))return "";
		else if(s1!=null && s2!=null && s1.equals(s2))return s1;
		else if(s1!=null && s2==null)return s1;
		else if(s1==null && s2!=null)return s2;
		else return null;
	}
	
	public String toString()
	{	//if(isWaarde())return new BasisExpressie(geefWaarde()).toString();
		return "rns" + "$h" + kind1.toString() + "_" + kind2.toString() + "_" + kind3.toString() + "@";
	}
	
	public String toStringStrikt()
	{	//if(isWaarde())return new BasisExpressie(geefWaarde()).toString();
		return "rns" + "$h" + kind1.toStringStrikt() + "_" + kind2.toStringStrikt() + "_" + kind3.toStringStrikt() + "@";
	}
    
    public String toStringCAS()
    {   return "N" + "[" + kind1.toStringCAS() + "," + kind2.toStringCAS() + "]";
    }
}
