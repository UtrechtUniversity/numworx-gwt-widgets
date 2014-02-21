package fi.algebraexprgwt.client.expressies_ap;

//import java.awt.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

public class Aftrekking extends Expressie  
{	
	
	public Aftrekking(Expressie e1,Expressie e2 )
	{	kind1 = e1;
		kind2 = e2;
		operatorString = " - ";
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
		
		if(kind1 instanceof BasisExpressie && !Double.isNaN(kind1.geefWaarde().doubleValue()) && kind1.geefWaarde().doubleValue()==0 )
		{	
			g.fillText(operatorString, x , y + ashoogte-fontSize / 2);
			//g.drawString(operatorString, x , y + ashoogte-fm.getHeight()/2 + fm.getAscent());
		}
		else
		{	kind1.teken(g, x, y + ashoogte-kind1.ashoogte);
			g.fillText(operatorString, x + kind1.breedte, y + ashoogte-fontSize/2);
			//g.drawString(operatorString, x + kind1.breedte, y + ashoogte-fm.getHeight()/2 + fm.getAscent());
		}	
		if(kind1 instanceof BasisExpressie && !Double.isNaN(kind1.geefWaarde().doubleValue()) && kind1.geefWaarde().doubleValue()==0 )		
		{	if(kind2.isVeelterm)
			{	HaakjeLinks hl= new HaakjeLinks(kind2.hoogte+2);
				HaakjeRechts hr= new HaakjeRechts(kind2.hoogte+2);
				//hl.teken(g,x+fm.stringWidth(operatorString), y+ashoogte-kind2.ashoogte-1);
				hl.teken(g,x+opBreedte, y+ashoogte-kind2.ashoogte-1);
				//kind2.teken(g, x+fm.stringWidth(operatorString)+ hl.breedte, y + ashoogte-kind2.ashoogte);
				kind2.teken(g, x+opBreedte+ hl.breedte, y + ashoogte-kind2.ashoogte);
				//hr.teken(g,x+fm.stringWidth(operatorString)+ hl.breedte + kind2.breedte, y+ashoogte-kind2.ashoogte-1);
				hr.teken(g,x+opBreedte + hl.breedte + kind2.breedte, y+ashoogte-kind2.ashoogte-1);

			}
			else
			{	//kind2.teken(g, x+fm.stringWidth(operatorString), y + ashoogte-kind2.ashoogte);
				kind2.teken(g, x+opBreedte, y + ashoogte-kind2.ashoogte);
			}
		}
		else
		{	if(kind2.isVeelterm)
			{	HaakjeLinks hl= new HaakjeLinks(kind2.hoogte+2);
				HaakjeRechts hr= new HaakjeRechts(kind2.hoogte+2);
				//hl.teken(g,x+kind1.breedte+fm.stringWidth(operatorString), y+ashoogte-kind2.ashoogte-1);
				hl.teken(g,x+kind1.breedte+opBreedte, y+ashoogte-kind2.ashoogte-1);
				//kind2.teken(g, x+kind1.breedte+fm.stringWidth(operatorString)+ hl.breedte, y + ashoogte-kind2.ashoogte);
				kind2.teken(g, x+kind1.breedte+opBreedte+ hl.breedte, y + ashoogte-kind2.ashoogte);
				//hr.teken(g,x+kind1.breedte+fm.stringWidth(operatorString)+ hl.breedte + kind2.breedte, y+ashoogte-kind2.ashoogte-1);
				hr.teken(g,x+kind1.breedte+opBreedte + hl.breedte + kind2.breedte, y+ashoogte-kind2.ashoogte-1);

			}	
			else
			{	//kind2.teken(g, x+kind1.breedte+fm.stringWidth(operatorString), y + ashoogte-kind2.ashoogte);
				kind2.teken(g, x+kind1.breedte+opBreedte, y + ashoogte-kind2.ashoogte);
			}
		}
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
		
		if(kind1 instanceof BasisExpressie && !Double.isNaN(kind1.geefWaarde().doubleValue()) && kind1.geefWaarde().doubleValue()==0 )		
		{	if(kind2.isVeelterm)
			{	int hb = HaakjeLinks.geefHBreedte(fs);
				//breedte = fm.stringWidth(operatorString) + kind2.breedte + 2*hb;
				breedte = opBreedte + kind2.breedte + 2*hb;
				hoogte = Math.max(kind1.hoogte, kind2.hoogte) + 2;
			}
			else
			{	//breedte = fm.stringWidth(operatorString) + kind2.breedte;
				breedte = opBreedte + kind2.breedte;
				hoogte = Math.max(kind1.hoogte, kind2.hoogte);
			}
		}
		else
		{	if(kind2.isVeelterm)
			{	int hb = HaakjeLinks.geefHBreedte(fs);
				//breedte = kind1.breedte + fm.stringWidth(operatorString) + kind2.breedte + 2*hb;
				breedte = kind1.breedte + opBreedte + kind2.breedte + 2*hb;
				hoogte = Math.max(kind1.hoogte, kind2.hoogte) + 2;
			}
			else
			{	//breedte = kind1.breedte + fm.stringWidth(operatorString) + kind2.breedte;
				breedte = kind1.breedte + opBreedte + kind2.breedte;
				hoogte = Math.max(kind1.hoogte, kind2.hoogte);
			}
		}	
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
	{	
		if (!Double.isNaN(kind1.geefWaarde().doubleValue()) && !Double.isNaN(kind2.geefWaarde().doubleValue()))
		{	double d1 = kind1.geefWaarde().doubleValue();
			double d2 = kind2.geefWaarde().doubleValue();
			return new Double(d1-d2);
		}
		else 
			return new Double(Double.NaN);
	}
	
	public double geefW(double subst)
	{	return kind1.geefW(subst)-kind2.geefW(subst);
	}
	
	public boolean isWaarde(double subst)
	{	return kind1.isWaarde(subst) && kind2.isWaarde(subst);
	}
	
	public Expressie substitueer(double subst, String var)
	{	return new Aftrekking(kind1.substitueer(subst,var),kind2.substitueer(subst,var));
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
		if (kind1 instanceof BasisExpressie && ((BasisExpressie) kind1).geefWaarde().doubleValue() == 0)
			s1 = "";
		if (kind2.isVeelterm)
			s2 = "$h" + s2 + "@";
		return 
			s1 + "-" + s2;
	}
	
	public String toStringStrikt()
	{	String s1 = kind1.toStringStrikt();
		String s2 = kind2.toStringStrikt();

		if(kind2.isVeelterm)s2 = "$h" + s2 + "@";
		return "$a" + s1 + "$n" + s2 + "@@";
	}
	
}
