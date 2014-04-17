package fi.algebrapijlengwt.client.expressies_ap;

//import java.awt.*;

import com.google.gwt.canvas.dom.client.Context2d;

public class Functie extends Expressie  
{	
	
	public Functie(Expressie e1,Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		operatorString = " -> ";
		isVeelterm = true;
		isProdukt = false;
		isBasis = false;
		isAsym = false;
	}
	
	//public void teken(Graphics g, int x, int y)
	public void teken(Context2d g, int x, int y)
  	{
		kind1.teken(g, x, y + ashoogte - kind1.ashoogte);
		
		//g.drawLine(x + kind1.breedte+5, y + ashoogte, x + kind1.breedte+15, y + ashoogte);
		g.beginPath();
		g.moveTo(x + kind1.breedte+5, y + ashoogte -10);
		g.lineTo(x + kind1.breedte+15, y + ashoogte -10);
		g.stroke();
		
		//g.drawLine(x + kind1.breedte+12, y + ashoogte+3, x + kind1.breedte+15, y + ashoogte);
		g.beginPath();
		g.moveTo(x + kind1.breedte+12, y + ashoogte+3 -10);
		g.lineTo(x + kind1.breedte+15, y + ashoogte -10);
		g.stroke();
		
		//g.drawLine(x + kind1.breedte+12, y + ashoogte-3, x + kind1.breedte+15, y + ashoogte);
		g.beginPath();
		g.moveTo(x + kind1.breedte+12, y + ashoogte-3 -10);
		g.lineTo(x + kind1.breedte+15, y + ashoogte -10);
		g.stroke();
		
		kind2.teken(g, x+kind1.breedte+20, y + ashoogte-kind2.ashoogte);
	}	
	
	//public void zetMaat(FontMetrics fm)
	public void zetMaat(int fs, Context2d c2d)
  	{	//this.fm = fm;
		//kind1.zetMaat(fm);
		//kind2.zetMaat(fm);
		this.fontSize = fs;
		kind1.zetMaat(fs, c2d);
		kind2.zetMaat(fs, c2d);
		
		hoogte = Math.max(kind1.hoogte, kind2.hoogte);
		breedte = kind1.breedte + 20 + kind2.breedte;
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
	{	return null;
	}
	
	public double geefW(double subst)
	{	return Double.NaN;
	}
	
	public boolean isWaarde(double subst)
	{	return false;
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
}
