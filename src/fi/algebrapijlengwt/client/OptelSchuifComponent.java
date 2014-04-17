package fi.algebrapijlengwt.client;

//import java.awt.Polygon;
//import java.awt.*;
//import java.awt.event.*;
import fi.algebrapijlengwt.client.expressies_ap.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


public class OptelSchuifComponent extends BewerkingSchuifComponent 
{	
	
	public OptelSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(asv, x, y, b, h);
		
//GWT		
//		tf.setText("3");
	}
	
	//public void paint(Graphics g)
	public void paint(Context2d g)
  	{ 	super.paint(g);
		if (!visible)
  			return; 
		
		//g.setColor(Color.black);
  		g.setFillStyle(CssColor.make(0,0,0));
		
		//String s = "+ " + Expressie.df.format(beginw.geefWaarde());
		String s = "+ " + UF.format0(beginw.geefWaarde(),3);
		
//		Font f = new Font("SansSerrif",Font.PLAIN,14);
//		g.setFont(f);
//		FontMetrics fm = g.getFontMetrics();
//		int w = fm.stringWidth(s);
		
		String fontString = "14px sans-serif";
		g.setFont(fontString);
		TextMetrics tm = g.measureText(s);
		int w = (int) Math.round(tm.getWidth());
		
		int sccrollCorr = 0;
		if (scrollable)
			sccrollCorr = 10;
		
//GWT		
//		if (!tf.isVisible())
//		{	
			if (!links)
			{	//g.drawString(s,5+(getSize().width-w-sccrollCorr)/2,getSize().height-4);
				g.fillText(s,xPos + 5+(breedte-w-sccrollCorr)/2, yPos + hoogte-4);
			
			}
			else 
			{	//g.drawString(s,-5+(getSize().width-w-sccrollCorr)/2,getSize().height-4);
				g.fillText(s,xPos -5+(breedte-w-sccrollCorr)/2,yPos + hoogte-4);
			
			}
//		}
		
//GWT		
/*		
		else
		{	
			if (!links)
			{	//g.drawString("+ ",5+(getSize().width-w-sccrollCorr)/2,getSize().height-4);
				g.fillText("+ ",5+(breedte-w-sccrollCorr)/2,hoogte-4);
			}
			else 
			{	//g.drawString("+ ",-5+(getSize().width-w-sccrollCorr)/2,getSize().height-4);
				g.fillText("+ ",-5+(breedte-w-sccrollCorr)/2,hoogte-4);
			}
		}
*/		
			
	}
	
	
	public Expressie geefUitvoer(int max)
	{	if (AlgebraPijlenGWT.simplify)
		{	Expressie uitv = new Expressie();
			if (pijlIn1 == null)
				return null;
			Expressie e1 = pijlIn1.zender.geefUitvoer(max - 1);
			Expressie e2 = beginw;
			if (e1 == null)
				return null;
			//if(e2.geefWaarde().doubleValue()==0)uitv = e1;
			double d = 0;
			if (e1 instanceof Optelling)
			{	d = e1.kind2.geefWaarde().doubleValue() + e2.geefWaarde().doubleValue();
			}
			else if (e1 instanceof Aftrekking && Double.isNaN(e1.kind1.geefWaarde().doubleValue()))
			{	d = -e1.kind2.geefWaarde().doubleValue() + e2.geefWaarde().doubleValue();
			}
			else
			{	
				d = e2.geefWaarde().doubleValue();
				if (d == 0)
					uitv = e1;
				else if (d > 0)
					uitv = new Optelling(e1, e2);
				else //d<0
				{	//e2 = new BasisExpressie(Expressie.df.format(-d));
					e2 = new BasisExpressie(UF.format0(-d,3));
					uitv = new Aftrekking(e1, e2);
				}
				return uitv;
			}
			if (d > 0)
			{	//e2 = new BasisExpressie(Expressie.df.format(d));
				e2 = new BasisExpressie(UF.format0(d,3));
				uitv = new Optelling(e1.kind1, e2);
			}
			else if(d < 0)
			{	//e2 = new BasisExpressie(Expressie.df.format(-d));
				e2 = new BasisExpressie(UF.format0(-d,3));
				uitv = new Aftrekking(e1.kind1, e2);
			}
			else 
				uitv = e1.kind1;
			return uitv;
		}
		else
		{	Expressie uitv = new Expressie();
			if (pijlIn1 == null)
				return null;
			Expressie e1 = pijlIn1.zender.geefUitvoer(max - 1);
			Expressie e2 = beginw;
			if (e1 == null)
				return null;
			if (e2.geefWaarde().doubleValue() == 0)
				uitv = e1;
			else 
				uitv = new Optelling(e1, e2);
			return uitv;
		}
	}
	
	public Expressie geefVerborgenUitvoer(int max)
	{	Expressie uitv = new Expressie();
		if (pijlIn1 == null)
			return null;
		Expressie e1 = pijlIn1.zender.geefVerborgenUitvoer(max - 1);
		Expressie e2 = beginw;
		if (e1 == null)
			return null;
		if (e2.geefWaarde().doubleValue() == 0)
			uitv = e1;
		else 
			uitv = new Optelling(e1, e2);
		return uitv;
	}
}
