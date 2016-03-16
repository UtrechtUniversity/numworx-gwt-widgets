package fi.nabouwenaanzichtengwt.client.expressies;

//import java.awt.*;
//import java.util.*;

public class Aftrekking extends Expressie  
{	
	
	public Aftrekking(Expressie e1,Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		isVeelterm = true;
		isProdukt = false;
		isBasis = false;
	}

	public double geefWaarde()
	{	return kind1.geefWaarde()-kind2.geefWaarde();
	}
	
	public double geefWaarde(double subst)
	{	return kind1.geefWaarde(subst)-kind2.geefWaarde(subst);
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return kind1.geefWaarde(subst,vars)-kind2.geefWaarde(subst,vars);
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Aftrekking(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
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
		if(kind1 instanceof BasisExpressie && ((BasisExpressie)kind1).geefWaarde()==0)s1 = "";
		if(kind2.isVeelterm)s2 = "$h" + s2 + "@";
		return s1 + "-" + s2;
	}
	
	public String toStringStrikt()
	{	String s1 = kind1.toStringStrikt();
		String s2 = kind2.toStringStrikt();
		//if(kind1 instanceof BasisExpressie && ((BasisExpressie)kind1).geefWaarde()==0)s1 = "";
		if(kind2.isVeelterm)s2 = "$h" + s2 + "@";
		return "$a" + s1 + "$n" + s2 + "@@";
	}
}
