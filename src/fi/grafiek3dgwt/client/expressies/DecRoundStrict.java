package fi.grafiek3dgwt.client.expressies;

import java.awt.*;

public class DecRoundStrict extends Expressie  
{	
	
	public DecRoundStrict(Expressie e1, Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}
	
	public double geefWaarde()
	{	double d = 0;
		d = kind1.geefWaarde()*1.000000000000001;
		//if(kind1.geefWaarde()<0) d = kind1.geefWaarde()-0.000000000000001;
		//else d = kind1.geefWaarde()+0.000000000000001;
		System.out.println(""+Math.rint(d*Math.pow(10, kind2.geefWaarde()))/Math.pow(10, kind2.geefWaarde()));
		double w = Math.rint(d*Math.pow(10, kind2.geefWaarde()))/Math.pow(10, kind2.geefWaarde());
		if(kind2.geefWaarde() < 0) w = Math.rint(w);
		return w;
	}
	
	public Complex geefWaardeComplex(double subst)
	{	double d = 0;
		d = kind1.geefWaarde()*1.000000000000001;
		return new Complex(Math.rint(kind1.geefWaarde(subst)*Math.pow(10, kind2.geefWaarde(subst)))/Math.pow(10, kind2.geefWaarde(subst)));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return Math.rint(kind1.geefWaarde(subst,vars)*Math.pow(10, kind2.geefWaarde(subst,vars)))/Math.pow(10, kind2.geefWaarde(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	DecRoundStrict exp = new DecRoundStrict(kind1.substitueer(subst,var), kind2.substitueer(subst,var));
		//if(exp.isWaarde())return new BasisExpressie(exp.geefWaarde());
		//else 
			return exp;
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	DecRoundStrict exp = new DecRoundStrict(kind1.substitueer(subst,var), kind2.substitueer(subst,var));
		//if(exp.isWaarde())return new BasisExpressie(exp.geefWaarde());
		//else 
			return exp;
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst) && kind2.isWaarde(subst);
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
		return "rnq" + "$h" + kind1.toString() + "_" + kind2.toString() + "@";
	}
	
	public String toStringStrikt()
	{	//if(isWaarde())return new BasisExpressie(geefWaarde()).toString();
		return "rnq" + "$h" + kind1.toStringStrikt() + "_" + kind2.toStringStrikt() + "@";
	}
    
    public String toStringCAS()
    {   return "N" + "[" + kind1.toStringCAS() + "," + kind2.toStringCAS() + "]";
    }
}
