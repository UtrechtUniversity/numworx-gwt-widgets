package fi.grafiek3dgwt.client.expressies;


public class Log extends Expressie  
{	
	
	public Log(Expressie e1 )
	{	kind1 = e1;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}
	
	public double geefWaarde()
	{	return Math.log(kind1.geefWaarde())/Math.log(10);
	}
	
	public Complex geefWaardeComplex()
	{	Complex c1 = kind1.geefWaardeComplex();
		if(c1==null) return null;
		return Complex.over(Complex.log(c1),Math.log(10));
	}
	
	public double geefWaarde(double subst)
	{	return Math.log(kind1.geefWaarde(subst))/Math.log(10);
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return Math.log(kind1.geefWaarde(subst,vars))/Math.log(10);
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Log(kind1.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new Log(kind1.substitueer(subst,var));
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
	{	return "log" + "$h" + kind1.toString() + "@";
	}
	
	public String toStringStrikt()
	{	return "log" + "$h" + kind1.toStringStrikt() + "@";
	}
    
    public String toStringCAS()
    {   return "Log" + "["+ "10," + kind1.toStringCAS() + "]";
    }
}
