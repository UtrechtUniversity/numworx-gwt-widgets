package fi.grafiek3dgwt.client.expressies;

import java.awt.*;

public class Bin extends Expressie  
{	
	
	public Bin(Expressie e1, Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		isVeelterm = false;
		isProdukt = false;
		isBasis = true;
	}
	
	public static double binom(int n, int k)
	{	if(n<k)return Double.NaN;
		double[] b = new double[n+1];
		b[0] = 1;
		for(int i=1 ; i<n+1 ; i++)
		{	b[i] = 1;
			for(int j=i-1 ; j>0 ; j--)
			{	b[j] += b[j-1];
			}
		}
		return b[k];
	}
	
	public double geefWaarde()
	{	int w1 = (int)Math.round(kind1.geefWaarde());
		int w2 = (int)Math.round(kind2.geefWaarde());
		/*
		long teller = 1;
		for(int i=w1 ; i>w1-w2 ; i--)	teller = teller*i;
		long noemer = 1;
		for(int i=w2 ; i>0 ; i--)	noemer = noemer*i;
		System.out.println("" + teller + "/" + noemer);
		return teller/noemer;
		*/
		return binom(w1, w2);
	}
	
	public double geefWaarde(double subst)
	{	
		int w1 = (int)Math.rint(kind1.geefWaarde(subst));
		int w2 = (int)Math.rint(kind2.geefWaarde(subst));
		return binom(w1, w2);
		
		
	}
	
	public Complex geefWaardeComplex()
	{	
		int w1 = (int)Math.rint(kind1.geefWaardeComplex().getReal());
		int w2 = (int)Math.rint(kind2.geefWaardeComplex().getReal());
		return new Complex(binom(w1, w2));
		
	}
	
	public Complex geefWaardeComplex(Complex subst)
	{	
		int w1 = (int)Math.rint(kind1.geefWaardeComplex().getReal());
		int w2 = (int)Math.rint(kind2.geefWaardeComplex().getReal());
		return new Complex(binom(w1, w2));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	
		int w1 = (int)Math.rint(kind1.geefWaarde(subst,vars));
		int w2 = (int)Math.rint(kind2.geefWaarde(subst,vars));
		return binom(w1, w2);
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Bin(kind1.substitueer(subst,var), kind2.substitueer(subst, var));
	}
	
	public Expressie substitueer(Expressie subst, String var)
	{	return new Bin(kind1.substitueer(subst,var), kind2.substitueer(subst, var));
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst) && kind2.isWaarde(subst);
	}
	
	public String geefVarNaam()
	{	String s1 = kind1.geefVarNaam();
		if(s1!=null)return s1;
		return null;
	}
	
	public String toString()
	{	return "$y" + kind1.toString() + "$n" + kind2.toString() + "@@";
	}
	
	public String toStringStrikt()
	{	return "$y" + kind1.toStringStrikt() + "$n" + kind2.toStringStrikt() + "@@"; 
	}
    
    public String toStringCAS()
    {   return "Fac" + "[" + kind1.toStringCAS() + "]";
    }
}
