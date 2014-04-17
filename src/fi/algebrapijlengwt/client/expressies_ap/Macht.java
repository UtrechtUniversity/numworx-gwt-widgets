package fi.algebrapijlengwt.client.expressies_ap;

//import java.awt.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

public class Macht extends Expressie  
{		
	public Macht(Expressie e1, Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		isVeelterm = false;
		isProdukt = true;
		isBasis = false;
		isAsym = false;
	}
	
	//public void teken(Graphics g, int x, int y)
	public void teken(Context2d g, int x, int y)
  	{ 	HaakjeLinks hl= new HaakjeLinks(kind1.hoogte+2);
		HaakjeRechts hr= new HaakjeRechts(kind1.hoogte+2);
		int g1;
		if (!(kind1.isBasis && (Double.isNaN(kind1.geefWaarde().doubleValue()) || 
				(!Double.isNaN(kind1.geefWaarde().doubleValue()) && kind1.geefWaarde().doubleValue()>0))))
		{	hl.teken(g, x, y+ashoogte-kind1.ashoogte-1);
			g1 = HaakjeLinks.geefHBreedte(fontSize);
		}
		else g1 = 0;	
		kind1.teken(g, x+g1, y + ashoogte-kind1.ashoogte);
		int g2 = g1 + kind1.breedte;
		int g3;
		if (!(kind1.isBasis && (Double.isNaN(kind1.geefWaarde().doubleValue()) || 
				(!Double.isNaN(kind1.geefWaarde().doubleValue()) && kind1.geefWaarde().doubleValue()>0))))
		{	hr.teken(g, x+g2, y+ashoogte-kind1.ashoogte-1);
			g3 = g2 + HaakjeLinks.geefHBreedte(fontSize);
		}
		else g3 = g2;
		kind2.teken(g, x+g3, y);
	}
	
	//public void zetMaat(FontMetrics fm)
	public void zetMaat(int fs, Context2d c2d)
  	{	//this.fm = fm;
		fontSize = fs;
		//kind1.zetMaat(fm);
		kind1.zetMaat(fs, c2d);
		//kind2.zetMaat(fm);
		kind2.zetMaat(fs, c2d);
		if(!(kind1.isBasis && (Double.isNaN(kind1.geefWaarde().doubleValue()) || 
				(!Double.isNaN(kind1.geefWaarde().doubleValue()) && kind1.geefWaarde().doubleValue()>0))))
		{	int hb = HaakjeLinks.geefHBreedte(fs);
			breedte = kind1.breedte + kind2.breedte + 2*hb;
			hoogte = kind1.hoogte + 4 + kind2.hoogte-10;
		}
		
		else
		{	breedte = kind1.breedte + kind2.breedte;
			hoogte =  kind1.hoogte + 2 + kind2.hoogte-10;
		}
		
		{	ashoogte =  kind1.ashoogte + 2 + kind2.hoogte-10;
			isAsym = true;
		}
		
	}
	
	public Double geefWaarde()
	{	
		if (!Double.isNaN(kind1.geefWaarde().doubleValue()) && !Double.isNaN(kind2.geefWaarde().doubleValue()))
		{	double d1 = kind1.geefWaarde().doubleValue();
			double d2 = kind2.geefWaarde().doubleValue();
			return new Double(Math.pow(d1,d2));
		}
		else return new Double(Double.NaN); //null;
	}
	
	public double geefW(double subst)
	{	return Math.pow(kind1.geefW(subst),kind2.geefW(subst));
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst) && kind2.isWaarde(subst);
	}
	public Expressie substitueer(double subst, String var)
	{	return new Macht(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
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
	
		if(!kind1.isBasis)
			s1 = "$h" + s1 + "@";
		return s1 + "$m" + s2 + "@";
	}
	
	public String toStringStrikt()
	{	String s1 = kind1.toStringStrikt();
		String s2 = kind2.toStringStrikt();
		if(!kind1.isBasis)
			s1 = "$h" + s1 + "@";
		return "$p" + s1 + "$n" + s2 + "@@";
	}
	
}
