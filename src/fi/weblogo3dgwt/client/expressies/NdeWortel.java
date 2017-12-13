package fi.weblogo3dgwt.client.expressies;


public class NdeWortel extends Expressie  
{		
	public NdeWortel(Expressie e1, Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		isVeelterm = false;
		isProdukt = true;
		isBasis = false;
	}
	
	public double geefWaarde()
	{	double d1 = kind1.geefWaarde();
		double d2 = kind2.geefWaarde();
		if(d2!=0 && d1>=0)
		{	return Math.pow(d1,1/d2);
		}
		else if(d2-Math.rint(d2)==0 && (int)Math.rint(d2)%2==1 && d1<=0)
		{	return -Math.pow(-d1,1/d2);
		}
		else return Double.NaN;
	}
	
	public double geefWaarde(double subst)
	{	double d1 = kind1.geefWaarde(subst);
		double d2 = kind2.geefWaarde(subst);
		if(d2!=0 && d1>=0)
		{	return Math.pow(d1,1/d2);
		}
		else if(d2-Math.rint(d2)==0 && (int)Math.rint(d2)%2==1 && d1<=0)
		{	return -Math.pow(-d1,1/d2);
		}
		else return Double.NaN;
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	double d1 = kind1.geefWaarde(subst,vars);
		double d2 = kind2.geefWaarde(subst,vars);
		if(d2!=0 && d1>=0)
		{	return Math.pow(d1,1/d2);
		}
		else if(d2-Math.rint(d2)==0 && (int)Math.rint(d2)%2==1 && d1<=0)
		{	return -Math.pow(-d1,1/d2);
		}
		else return Double.NaN;
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new NdeWortel(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new NdeWortel(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst) && kind2.isWaarde(subst)&& kind2.geefWaarde(subst)!=0 && kind1.geefWaarde(subst)>=0;
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
		return "$W" + s1 + "$n" + s2 + "@@";
	}
	
	public String toStringStrikt()
	{	String s1 = kind1.toStringStrikt();
		String s2 = kind2.toStringStrikt();
		return "$W" + s1 + "$n" + s2 + "@@";
	}
}
