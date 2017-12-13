package fi.weblogo3dgwt.client.expressies;


public class ArcCosinus extends Expressie  
{	
	
	public ArcCosinus(Expressie e1 )
	{	kind1 = e1;
		isVeelterm = false;
		isProdukt = false;
		isBasis = false;
	}

	public double geefWaarde()
	{	if(hoekGraden) return 180.0/Math.PI*Math.acos(kind1.geefWaarde());
		return Math.acos(kind1.geefWaarde());
	}
	
	public double geefWaarde(double subst)
	{	if(hoekGraden) return 180.0/Math.PI*Math.acos(kind1.geefWaarde(subst));
		return Math.acos(kind1.geefWaarde(subst));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	if(hoekGraden) return 180.0/Math.PI*Math.acos(kind1.geefWaarde(subst,vars));
		return Math.acos(kind1.geefWaarde(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new ArcCosinus(kind1.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new ArcCosinus(kind1.substitueer(subst,var));
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
	{	return "arccos" + "$h" + kind1.toString() + "@";
	}
	
	public String toStringStrikt()
	{	return "arccos" + "$h" + kind1.toStringStrikt() + "@";
	}
}
