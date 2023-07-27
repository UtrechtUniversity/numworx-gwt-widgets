package fi.grafiek3dgwt.client.expressies;

import java.util.*;

import fi.grafiek3dgwt.client.formuleobjects.FormuleParser;

public class Vermenigvuldiging extends Expressie  
{	
	
	public Vermenigvuldiging(Expressie e1,Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		isVeelterm = false;
		isProdukt = true;
		isBasis = false;
	}

	public double geefWaarde()
	{	return kind1.geefWaarde()*kind2.geefWaarde();
	}
	
	public Complex geefWaardeComplex()
	{	Complex c1 = kind1.geefWaardeComplex();
		Complex c2 = kind2.geefWaardeComplex();
		if(c1==null || c2==null) return null;
		return Complex.times(c1,c2);
	}
	
	public double geefWaarde(double subst)
	{	return kind1.geefWaarde(subst)*kind2.geefWaarde(subst);
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	return Complex.times(kind1.geefWaardeComplex(subst),kind2.geefWaardeComplex(subst));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return kind1.geefWaarde(subst,vars)*kind2.geefWaarde(subst,vars);
	}
	
	public Complex geefWaardeComplex(Complex[] subst, String[] vars)
	{	return Complex.times(kind1.geefWaardeComplex(subst,vars),kind2.geefWaardeComplex(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Vermenigvuldiging(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new Vermenigvuldiging(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
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
	{	String s1 = kind1.toString();
		String s2 = kind2.toString();
		String op = "";
		
		Vector v1 = Algebra.geefFactorenBeperkt(kind1,new Vector());
		Vector v2 = Algebra.geefFactorenBeperkt(kind2,new Vector());
		Expressie g2 = null;
		if(v2.size()>0) g2 = ((Expressie)v2.elementAt(0));
		if(g2 instanceof BasisExpressie && !Double.isNaN(g2.geefWaarde()) 	&& !(g2 instanceof PI) 	&& !(g2 instanceof E)
		   || g2 instanceof Macht && !Double.isNaN(g2.kind1.geefWaarde()) 
		   || Algebra.isBreukPlusGetal(g2)
		   || FormuleParser.isWoordFormule()
		   || FormuleParser.isTweeHoofdletterVariabele())
		{	op = "*";
		}
		
		if(kind1.isVeelterm)s1 = "$h" + s1 + "@";
		if(kind2.isVeelterm)s2 = "$h" + s2 + "@";
		
		return s1 + op + s2;
		//return s1 + "*" + s2;
	}
	
	public String toStringStrikt()
	{	String s1 = kind1.toStringStrikt();
		String s2 = kind2.toStringStrikt();
		if(kind1.isVeelterm)s1 = "$h" + s1 + "@";
		if(kind2.isVeelterm)s2 = "$h" + s2 + "@";
		return "$v" + s1 + "$n" + s2 + "@@";
	}
    
    public String toStringCAS()
    {   return "(" + kind1.toStringCAS() + ")*(" + kind2.toStringCAS() + ")";
    }
}
