package fi.grafiek3dgwt.client.expressies;


public class ArcSinus extends Expressie  
{	
	
	public ArcSinus(Expressie e1 )
	{	kind1 = e1;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}
	
	public double geefWaarde()
	{	if(hoekGraden) return 180.0/Math.PI*Math.asin(kind1.geefWaarde());
		return Math.asin(kind1.geefWaarde());
	}
	
	public Complex geefWaardeComplex()
	{	Complex c1 = kind1.geefWaardeComplex();
		if(c1==null) return null;
		return Complex.asin(c1);
	}
	
	public double geefWaarde(double subst)
	{	if(hoekGraden) return 180.0/Math.PI*Math.asin(kind1.geefWaarde(subst));
		return Math.asin(kind1.geefWaarde(subst));
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	return Complex.asin(kind1.geefWaardeComplex(subst));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	if(hoekGraden) return 180.0/Math.PI*Math.asin(kind1.geefWaarde(subst,vars));
		return Math.asin(kind1.geefWaarde(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new ArcSinus(kind1.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new ArcSinus(kind1.substitueer(subst,var));
	}
		
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst);
	}
	
	public String geefVarNaam()
	{	String s1 = kind1.geefVarNaam();
		if(s1!=null)return s1;
		return null;
	}
	
	public String toString()
	{	return "arcsin" + "$h" + kind1.toString() + "@";
	}
	
	public String toStringStrikt()
	{	return "arcsin" + "$h" + kind1.toStringStrikt() + "@";
	}
    
    public String toStringCAS()
    {   return "ArcSin" + "[" + kind1.toStringCAS() + "]";
    }
}
