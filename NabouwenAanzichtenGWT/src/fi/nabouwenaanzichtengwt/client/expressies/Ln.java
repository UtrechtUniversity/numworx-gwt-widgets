package fi.nabouwenaanzichtengwt.client.expressies;


public class Ln extends Expressie  
{	
	
	public Ln(Expressie e1 )
	{	kind1 = e1;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}
	
	public double geefWaarde()
	{	return Math.log(kind1.geefWaarde());
	}
	
	public double geefWaarde(double subst)
	{	return Math.log(kind1.geefWaarde(subst));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return Math.log(kind1.geefWaarde(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Ln(kind1.substitueer(subst,var));
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
	{	return "ln" + "$h" + kind1.toString() + "@";
	}
	
	public String toStringStrikt()
	{	return "ln" + "$h" + kind1.toStringStrikt() + "@";
	}
}
