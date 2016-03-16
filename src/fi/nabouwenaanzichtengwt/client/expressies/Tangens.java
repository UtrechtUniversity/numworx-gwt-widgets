package fi.nabouwenaanzichtengwt.client.expressies;

import java.awt.*;

public class Tangens extends Expressie  
{	
	
	public Tangens(Expressie e1 )
	{	kind1 = e1;
		isVeelterm = false;
		isProdukt = false;
		isBasis = true;
	}
	
	public double geefWaarde()
	{	return Math.tan(kind1.geefWaarde());
	}
	
	public double geefWaarde(double subst)
	{	return Math.tan(kind1.geefWaarde(subst));
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return Math.tan(kind1.geefWaarde(subst,vars));
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Tangens(kind1.substitueer(subst,var));
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
	{	return "tan" + "$h" + kind1.toString() + "@";
	}
}
