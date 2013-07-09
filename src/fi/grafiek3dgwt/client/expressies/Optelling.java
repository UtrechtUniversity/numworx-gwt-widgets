package fi.grafiek3dgwt.client.expressies;

import java.awt.*;

public class Optelling extends Expressie  
{	
	
	public Optelling(Expressie e1,Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		isVeelterm = true;
		isProdukt = false;
		isBasis = false;
	}
	
	public double geefWaarde()
	{	return kind1.geefWaarde()+kind2.geefWaarde();
	}
	
	public Complex geefWaardeComplex()
	{	Complex c1 = kind1.geefWaardeComplex();
		Complex c2 = kind2.geefWaardeComplex();
		if(c1==null || c2==null) return null;
		return Complex.plus(c1,c2);
	}
		
	public double geefWaarde(double subst)
	{	return kind1.geefWaarde(subst)+kind2.geefWaarde(subst);
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	return Complex.plus(kind1.geefWaardeComplex(subst),kind2.geefWaardeComplex(subst));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return kind1.geefWaarde(subst,vars)+kind2.geefWaarde(subst,vars);
	}
	
	public Complex geefWaardeComplex(Complex[] subst, String[] vars)
	{	return Complex.plus(kind1.geefWaardeComplex(subst,vars),kind2.geefWaardeComplex(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Optelling(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new Optelling(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
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
	{	if(kind2 instanceof Deling && kind1 instanceof BasisExpressie && kind2.kind1 instanceof BasisExpressie && kind2.kind2 instanceof BasisExpressie)
		{	int getal,teller,noemer;
			boolean integerBreuk = true;
			try
			{	getal = Integer.parseInt(((BasisExpressie)kind1).basisString);
				teller = Integer.parseInt(((BasisExpressie)kind2.kind1).basisString);
				noemer = Integer.parseInt(((BasisExpressie)kind2.kind2).basisString);
			}
			catch(NumberFormatException e)
			{	integerBreuk = false;
			}
			if(integerBreuk)
			{	isVeelterm = false;
				return kind1.toString() + kind2.toString();
			}
		}
		return kind1.toString() + "+" + kind2.toString();
	}
	
	public String toStringStrikt()
	{	if(kind2 instanceof Deling && kind1 instanceof BasisExpressie && kind2.kind1 instanceof BasisExpressie && kind2.kind2 instanceof BasisExpressie)
		{	int getal,teller,noemer;
			boolean integerBreuk = true;
			try
			{	getal = Integer.parseInt(((BasisExpressie)kind1).basisString);
				teller = Integer.parseInt(((BasisExpressie)kind2.kind1).basisString);
				noemer = Integer.parseInt(((BasisExpressie)kind2.kind2).basisString);
			}
			catch(NumberFormatException e)
			{	integerBreuk = false;
			}
			if(integerBreuk)
			{	isVeelterm = false;
				return kind1.toString() + kind2.toString();
			}
		}
		return "$o" + kind1.toStringStrikt() + "$n" + kind2.toStringStrikt() + "@@";
	}
    
    public String toStringCAS()
    {   if(kind2 instanceof Deling && kind1 instanceof BasisExpressie && kind2.kind1 instanceof BasisExpressie && kind2.kind2 instanceof BasisExpressie)
		{	int getal,teller,noemer;
			boolean integerBreuk = true;
			try
			{	getal = Integer.parseInt(((BasisExpressie)kind1).basisString);
				teller = Integer.parseInt(((BasisExpressie)kind2.kind1).basisString);
				noemer = Integer.parseInt(((BasisExpressie)kind2.kind2).basisString);
			}
			catch(NumberFormatException e)
			{	integerBreuk = false;
			}
			if(integerBreuk)
			{	isVeelterm = false;
				return "(" + kind1.toStringCAS() + "+" + kind2.toStringCAS() + ")";
			}
		}
        return kind1.toStringCAS() + "+" + kind2.toStringCAS();
    }
}
