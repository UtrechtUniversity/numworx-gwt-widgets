package fi.algebrapijlengwt.client.expressies_ap;

import java.awt.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


public class Optelling extends Expressie  
{	
	
	public Optelling(Expressie e1,Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		operatorString = " + ";
		isVeelterm = true;
		isProdukt = false;
		isBasis = false;
		isAsym = false;
	}
	
	//public void teken(Graphics g, int x, int y)
	public void teken(Context2d g, int x, int y)
  	{
		TextMetrics tm = g.measureText(operatorString);
		int opBreedte = (int) Math.round(tm.getWidth());
		g.setFillStyle(CssColor.make(0,0,0));

		kind1.teken(g, x, y + ashoogte - kind1.ashoogte);
		
		//g.drawString(operatorString, x + kind1.breedte, y + ashoogte-fm.getHeight()/2 + fm.getAscent());
		g.fillText(operatorString, x + kind1.breedte, y + ashoogte-fontSize/2);
		
		//kind2.teken(g, x+kind1.breedte+fm.stringWidth(operatorString), y + ashoogte-kind2.ashoogte);
		kind2.teken(g, x+kind1.breedte+opBreedte, y + ashoogte-kind2.ashoogte);
	}
	
	//public void zetMaat(FontMetrics fm)
	public void zetMaat(int fs, Context2d c2d)
  	{	//this.fm = fm;
  		fontSize = fs;
		//kind1.zetMaat(fm);
  		kind1.zetMaat(fs, c2d);
		//kind2.zetMaat(fm);
  		kind2.zetMaat(fs, c2d);
  		
		TextMetrics tm = c2d.measureText(operatorString);
		int opBreedte = (int) Math.round(tm.getWidth());
  		
		hoogte = Math.max(kind1.hoogte, kind2.hoogte);
		//breedte = kind1.breedte + fm.stringWidth(operatorString) + kind2.breedte;
		breedte = kind1.breedte + opBreedte + kind2.breedte;
		if(!kind1.isAsym && !kind2.isAsym)
		{	ashoogte = hoogte/2;
		}
		if(kind1.isAsym || kind2.isAsym)
		{	ashoogte = Math.max(kind1.ashoogte, kind2.ashoogte);
			hoogte = ashoogte + Math.max(kind1.hoogte-kind1.ashoogte, kind2.hoogte-kind2.ashoogte);
			isAsym = true;
		}
	}
	
	public Double geefWaarde()
	{	//if(kind1.geefWaarde()!=null && kind2.geefWaarde()!=null)
		if (!Double.isNaN(kind1.geefWaarde().doubleValue()) && !Double.isNaN(kind2.geefWaarde().doubleValue()))
		{	double d1 = kind1.geefWaarde().doubleValue();
			double d2 = kind2.geefWaarde().doubleValue();
			return new Double(d1+d2);
		}
		else 
			return new Double(Double.NaN);
			//return null;
	}
	
	public double geefW(double subst)
	{	return kind1.geefW(subst)+kind2.geefW(subst);
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst) && kind2.isWaarde(subst);
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Optelling(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
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
	{	if (kind2 instanceof Deling && kind1 instanceof BasisExpressie && kind2.kind1 instanceof BasisExpressie && kind2.kind2 instanceof BasisExpressie)
		{	int getal,teller,noemer;
			boolean integerBreuk = true;
			try
			{	getal = Integer.parseInt(((BasisExpressie)kind1).basisString);
				teller = Integer.parseInt(((BasisExpressie)kind2.kind1).basisString);
				noemer = Integer.parseInt(((BasisExpressie)kind2.kind2).basisString);
			}
			catch(NumberFormatException e)
			{	integerBreuk = false;
			}
			if(integerBreuk)
			{	isVeelterm = false;
				return kind1.toString() + kind2.toString();
			}
		}
		return kind1.toString() + "+" + kind2.toString();
	}
	
	public String toStringStrikt()
	{	if(kind2 instanceof Deling && kind1 instanceof BasisExpressie && kind2.kind1 instanceof BasisExpressie && kind2.kind2 instanceof BasisExpressie)
		{	int getal,teller,noemer;
			boolean integerBreuk = true;
			try
			{	getal = Integer.parseInt(((BasisExpressie)kind1).basisString);
				teller = Integer.parseInt(((BasisExpressie)kind2.kind1).basisString);
				noemer = Integer.parseInt(((BasisExpressie)kind2.kind2).basisString);
			}
			catch(NumberFormatException e)
			{	integerBreuk = false;
			}
			if(integerBreuk)
			{	isVeelterm = false;
				return kind1.toString() + kind2.toString();
			}
		}
		return "$o" + kind1.toStringStrikt() + "$n" + kind2.toStringStrikt() + "@@";
	}
	
}
