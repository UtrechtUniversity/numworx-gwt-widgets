package fi.grafiek3dgwt.client.expressies;


public class Macht extends Expressie  
{		
	public Macht(Expressie e1, Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		isVeelterm = false;
		isProdukt = true;
		isBasis = false;
	}
	
	public double geefWaarde()
	{	
		if(kind1.geefWaarde()<0 && kind2 instanceof Deling)
		{	Expressie e = new NdeWortel(new Macht(kind1,kind2.kind1),kind2.kind2);
			return e.geefWaarde();
		}
		else if(kind1.geefWaarde()<0 && kind2 instanceof Aftrekking && kind2.kind1.geefWaarde()==0 && kind2.kind2 instanceof Deling)
		{	Expressie e = new Deling(new BasisExpressie(1),new NdeWortel(new Macht(kind1,kind2.kind2.kind1),kind2.kind2.kind2));
			return e.geefWaarde();
		}
		return Math.pow(kind1.geefWaarde(),kind2.geefWaarde());
	
	}
	
	public double geefWaarde(double subst)
	{	if(kind1.geefWaarde()<0 && kind2 instanceof Deling)
		{	Expressie e = new NdeWortel(new Macht(kind1,kind2.kind1),kind2.kind2);
			return e.geefWaarde(subst);
		}
		else if(kind1.geefWaarde()<0 && kind2 instanceof Aftrekking && kind2.kind1.geefWaarde()==0 && kind2.kind2 instanceof Deling)
		{	Expressie e = new Deling(new BasisExpressie(1),new NdeWortel(new Macht(kind1,kind2.kind2.kind1),kind2.kind2.kind2));
			return e.geefWaarde(subst);
		}
		return Math.pow(kind1.geefWaarde(subst),kind2.geefWaarde(subst));
	}
	
	public Complex geefWaardeComplex()
	{	Complex c1 = kind1.geefWaardeComplex();
		Complex c2 = kind2.geefWaardeComplex();
		if(c1==null || c2==null) return null;
		return Complex.pow(c1,c2);
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	if(kind1.geefWaarde()<0 && kind2 instanceof Deling)
		{	Expressie e = new NdeWortel(new Macht(kind1,kind2.kind1),kind2.kind2);
			return e.geefWaarde(subst,vars);
		}
		else if(kind1.geefWaarde()<0 && kind2 instanceof Aftrekking && kind2.kind1.geefWaarde()==0 && kind2.kind2 instanceof Deling)
		{	Expressie e = new Deling(new BasisExpressie(1),new NdeWortel(new Macht(kind1,kind2.kind2.kind1),kind2.kind2.kind2));
			return e.geefWaarde(subst,vars);
		}
		return Math.pow(kind1.geefWaarde(subst,vars),kind2.geefWaarde(subst,vars));
	}
	
	public Complex geefWaardeComplex(Complex[] subst, String[] vars)
	{	return Complex.pow(kind1.geefWaardeComplex(subst,vars),kind2.geefWaardeComplex(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Macht(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	return Complex.pow(kind1.geefWaardeComplex(subst),kind2.geefWaardeComplex(subst));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new Macht(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
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
	
		if(kind1 instanceof Sinus)
		{	String s11 = kind1.kind1.toString();
			s11 = "$h" + s11 + "@";
			return "sin" + "$m" + s2 + "@" + s11 ;
		}
		if(kind1 instanceof Cosinus)
		{	String s11 = kind1.kind1.toString();
			s11 = "$h" + s11 + "@";
			return "cos" + "$m" + s2 + "@" + s11 ;
		}
		if(kind1 instanceof Tangens)
		{	String s11 = kind1.kind1.toString();
			s11 = "$h" + s11 + "@";
			return "tan" + "$m" + s2 + "@" + s11 ;
		}
		if(kind1 instanceof Log)
		{	String s11 = kind1.kind1.toString();
			s11 = "$h" + s11 + "@";
			return "log" + "$m" + s2 + "@" + s11 ;
		}
		if(kind1 instanceof Ln)
		{	String s11 = kind1.kind1.toString();
			s11 = "$h" + s11 + "@";
			return "ln" + "$m" + s2 + "@" + s11 ;
		}
		if(!kind1.isBasis)s1 = "$h" + s1 + "@";
		return s1 + "$m" + s2 + "@";
	}
	
	public String toStringStrikt()
	{	String s1 = kind1.toStringStrikt();
		String s2 = kind2.toStringStrikt();
		if(!kind1.isBasis)s1 = "$h" + s1 + "@";
		return "$p" + s1 + "$n" + s2 + "@@";
	}
    
    public String toStringCAS()
    {   String s1 = kind1.toStringCAS();
        String s2 = kind2.toStringCAS();
        if(!kind1.isBasis)s1 = "(" + s1 + ")";
        return s1 + "^(" + s2 + ")";
    }
}
