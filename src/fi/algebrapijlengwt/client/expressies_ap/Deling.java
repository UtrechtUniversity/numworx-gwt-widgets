package fi.algebrapijlengwt.client.expressies_ap;


import com.google.gwt.canvas.dom.client.Context2d;

public class Deling extends Expressie  
{	
	
	public Deling(Expressie e1,Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		isVeelterm = false;
		isProdukt = true;
		isBasis = false;
	}
	
	public void teken(Context2d g, int x, int y)
  	{ 		
		kind1.teken(g, x + (breedte-kind1.breedte)/2, y);
		int y1 = kind1.hoogte+1;
		
		g.beginPath();
		g.moveTo(x,y+y1-9);
		g.lineTo(x+breedte-1,y+y1-9);
		
		g.stroke();
		
		int y2 = y1+2;
		kind2.teken(g ,x + (breedte-kind2.breedte)/2, y+y2);
		
	}
	
	public void zetMaat(int fs, Context2d c2d)
  	{	
		fontSize = fs;
		kind1.zetMaat(fs, c2d);
		kind2.zetMaat(fs, c2d);
		
		if(kind1.hoogte != kind2.hoogte)isAsym = true;
		
		breedte = Math.max(kind1.breedte, kind2.breedte)+4;
		hoogte = kind1.hoogte + kind2.hoogte + 3;
		ashoogte = kind1.hoogte+1;
	}
	
	public Double geefWaarde()
	{	
		if (!Double.isNaN(kind1.geefWaarde().doubleValue()) && !Double.isNaN(kind2.geefWaarde().doubleValue()))
		{	double d1 = kind1.geefWaarde().doubleValue();
			double d2 = kind2.geefWaarde().doubleValue();
			if(d2!=0)return new Double(d1/d2);
			else return new Double(Double.NaN); 
		}
		else return new Double(Double.NaN); 
	}
	
	public double geefW(double subst)
	{	return kind1.geefW(subst)/kind2.geefW(subst);
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst) && kind2.isWaarde(subst) && kind2.geefW(subst)!=0;
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Deling(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
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
		return "$b" + s1 + "$n" + s2 + "@@";
	}
	
	public String toStringStrikt()
	{	String s1 = kind1.toStringStrikt();
		String s2 = kind2.toStringStrikt();
		return "$b" + s1 + "$n" + s2 + "@@";
	}
	
}
