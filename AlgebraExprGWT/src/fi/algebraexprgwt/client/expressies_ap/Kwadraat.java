package fi.algebraexprgwt.client.expressies_ap;

import java.awt.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


public class Kwadraat extends Expressie  
{	Expressie operatorExpr;
	
	public Kwadraat(Expressie e1 )
	{	kind1 = e1;
		operatorString = "2";
		operatorExpr = new BasisExpressie(operatorString);
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
		if(!(kind1.isBasis && !Double.isNaN(kind1.geefWaarde().doubleValue()) && kind1.geefWaarde().doubleValue()>0))
		{	hl.teken(g, x, y+ashoogte-kind1.ashoogte-1);
			g1 = HaakjeLinks.geefHBreedte(fontSize);
		}
		else g1 = 0;	
		kind1.teken(g, x+g1, y + ashoogte-kind1.ashoogte);
		int g2 = g1 + kind1.breedte;
		int g3;
		if(!(kind1.isBasis && !Double.isNaN(kind1.geefWaarde().doubleValue()) && kind1.geefWaarde().doubleValue()>0))
		{	hr.teken(g, x+g2, y+ashoogte-kind1.ashoogte-1);
			g3 = g2 + HaakjeLinks.geefHBreedte(fontSize);
		}
		else g3 = g2;
		operatorExpr.teken(g, x+g3, y);
	}
	
	//public void zetMaat(FontMetrics fm)
	public void zetMaat(int fs, Context2d c2d)
  	{	//this.fm = fm;
		fontSize = fs;
		//kind1.zetMaat(fm);
		kind1.zetMaat(fs, c2d);
		//operatorExpr.zetMaat(fm);
		operatorExpr.zetMaat(fs, c2d);
		if(!kind1.isBasis)
		{	int hb = HaakjeLinks.geefHBreedte(fs);
		
			TextMetrics tm = c2d.measureText(operatorString);
			int stringWidth = (int) Math.round(tm.getWidth());
		
			//breedte = kind1.breedte + fm.stringWidth(operatorString) + 2*hb;
			breedte = kind1.breedte + stringWidth + 2*hb;
			hoogte = kind1.hoogte + 4 + operatorExpr.hoogte-10;
		}
		else
		{	breedte = kind1.breedte + operatorExpr.breedte;
			hoogte =  kind1.hoogte + 2 + operatorExpr.hoogte-10;
		}
		
		{	ashoogte =  kind1.ashoogte + 2 + operatorExpr.hoogte-10;
			isAsym = true;
		}
	}
	
	public Double geefWaarde()
	{	//if(kind1.geefWaarde()!=null)
		if (!Double.isNaN(kind1.geefWaarde().doubleValue()))
		{	double d1 = kind1.geefWaarde().doubleValue();
			return new Double(d1*d1);
		}
		else return new Double(Double.NaN); //null;
	}
	public double geefW(double subst)
	{	return kind1.geefW(subst)*kind1.geefW(subst);
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst);
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Kwadraat(kind1.substitueer(subst,var));
	}
	
	public String geefVarNaam()
	{	String s1 = kind1.geefVarNaam();
		if(s1!=null)return s1;
		return null;
	}
	
	public String toString()
	{	String s1 = kind1.toString();
		String s2 = "2";
	
		if(!kind1.isBasis)
			s1 = "$h" + s1 + "@";
		return s1 + "$m" + s2 + "@";
	}
	
	public String toStringStrikt()
	{	String s1 = kind1.toStringStrikt();
		String s2 = "2";
		if(!kind1.isBasis)
			s1 = "$h" + s1 + "@";
		return "$p" + s1 + "$n" + s2 + "@@";
	}
	
}
