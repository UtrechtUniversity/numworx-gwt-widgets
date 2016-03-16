package fi.nabouwenaanzichtengwt.client.expressies;

import java.awt.*;

public class Kwadraat extends Expressie  
{	//Expressie operatorExpr;
	
	public Kwadraat(Expressie e1 )
	{	kind1 = e1;
		//operatorExpr = new BasisExpressie(operatorString);
		isVeelterm = false;
		isProdukt = true;
		isBasis = false;
	}
	
	
	public double geefWaarde()
	{	return kind1.geefWaarde()*kind1.geefWaarde();
	}
	
	public double geefWaarde(double subst)
	{	return kind1.geefWaarde(subst)*kind1.geefWaarde(subst);
	}
	
	public double geefWaarde(double[] subst, String[] vars)
	{	return kind1.geefWaarde(subst,vars)*kind1.geefWaarde(subst,vars);
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Kwadraat(kind1.substitueer(subst,var));
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst);
	}
	
	public String geefVarNaam()
	{	String s1 = kind1.geefVarNaam();
		if(s1!=null)return s1;
		return null;
	}
}
