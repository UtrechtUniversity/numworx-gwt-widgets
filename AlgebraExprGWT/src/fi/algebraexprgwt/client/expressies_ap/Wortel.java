package fi.algebraexprgwt.client.expressies_ap;

//import java.awt.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

public class Wortel extends Expressie  
{	
	
	public Wortel(Expressie e1 )
	{	kind1 = e1;
		isVeelterm = false;
		isProdukt = false;
		isBasis = true;
		isAsym = false;
	}
	
	//public void teken(Graphics g, int x, int y)
	public void teken(Context2d g, int x, int y)
  	{ 	Wortelteken wt = new Wortelteken(breedte,hoogte-4);
		wt.paint(g,x,y+2);
		kind1.teken(g,x+(hoogte-4)/2 + 2, y+ashoogte-kind1.ashoogte);
	}
	
	//public void zetMaat(FontMetrics fm)
	public void zetMaat(int fs, Context2d c2d)
  	{	//this.fm = fm;
		fontSize = fs;
		//kind1.zetMaat(fm);
		kind1.zetMaat(fs, c2d);
		breedte = kind1.breedte + kind1.hoogte/2 + 2;
		hoogte =  kind1.hoogte+4;
		ashoogte =  kind1.ashoogte + 2;
		if(kind1.isAsym)isAsym = true;
	}
	
	public Double geefWaarde()
	{	
		if (!Double.isNaN(kind1.geefWaarde().doubleValue()))	
		{	double d1 = kind1.geefWaarde().doubleValue();
			if(d1>=0)return new Double(Math.sqrt(d1));
			else return new Double(Double.NaN); //null;
		}
		else return new Double(Double.NaN); //null;
	}
	
	public double geefW(double subst)
	{	return Math.sqrt(kind1.geefW(subst));
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst) && kind1.geefW(subst)>=0;
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Wortel(kind1.substitueer(subst,var));
	}
	
	public String geefVarNaam()
	{	String s1 = kind1.geefVarNaam();
		if(s1!=null)return s1;
		return null;
	}
	
	public String toString()
	{	return "$w" + kind1.toString() + "@";
	}
	
	public String toStringStrikt()
	{	return "$w" + kind1.toStringStrikt() + "@";
	}
	
}
