package fi.geomalggwt.client.expressies;


public class Deling extends Expressie  
{	
	
	public Deling(Expressie e1,Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		isVeelterm = false;
		isProdukt = true;
		isBasis = false;
	}

	public double geefWaarde()
	{	double d1 = kind1.geefWaarde();
		double d2 = kind2.geefWaarde();
		if(d2!=0)
		{	return d1/d2;
		}
		else return Double.NaN;
	}
	
	public double geefWaarde(double subst)
	{	return kind1.geefWaarde(subst)/kind2.geefWaarde(subst);
	}
	
	public Complex geefWaardeComplex()
	{	Complex c1 = kind1.geefWaardeComplex();
		Complex c2 = kind2.geefWaardeComplex();
		if(c1==null || c2==null) return null;
		return Complex.over(c1,c2);
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return kind1.geefWaarde(subst,vars)/kind2.geefWaarde(subst,vars);
	}
	
	public Complex geefWaardeComplex(Complex[] subst, String[] vars)
	{	return Complex.over(kind1.geefWaardeComplex(subst,vars),kind2.geefWaardeComplex(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Deling(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	return Complex.over(kind1.geefWaardeComplex(subst),kind2.geefWaardeComplex(subst));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new Deling(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst) && kind2.isWaarde(subst) && kind2.geefWaarde(subst)!=0;
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
		return "$b" + s1 + "$n" + s2 + "@@";
	}
	
	public String toStringStrikt()
	{	String s1 = kind1.toStringStrikt();
		String s2 = kind2.toStringStrikt();
		return "$b" + s1 + "$n" + s2 + "@@";
	}
    
    public String toStringCAS()
    {   String s1 = kind1.toStringCAS();
        String s2 = kind2.toStringCAS();
        return "(" + s1 + ")/(" + s2 + ")" ;
    }
}
