package fi.algebrapijlengwt.client;

//import java.awt.Polygon;
//import java.awt.*;
//import java.awt.event.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

import fi.algebrapijlengwt.client.expressies_ap.*;

public class OmkeringSchuifComponent extends AlgebraSchuifComponent 
{	
	Expressie waarde;
//	Font f;
//	FontMetrics fm;
	
	public OmkeringSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(1,asv,x,y,b,h);
		
//		f = new Font("SansSerrif",Font.PLAIN,14);
//		fm = getFontMetrics(f);
	}
	
	//public void paint(Graphics g)
	public void paint(Context2d g)
  	{ 	super.paint(g);
		if (!visible)
  			return; 

		if(!links)
		{	
			//g.setColor(Color.orange);
			//g.fillRoundRect(10,0,getSize().width-11,getSize().height-1,8,8);
			//g.setColor(Color.black);
			//g.drawRoundRect(10,0,getSize().width-11,getSize().height-1,8,8);
			
			g.setFillStyle(CssColor.make(255, 200, 0));
			g.fillRect(xPos + 10,yPos + 0,breedte-11,hoogte-1);
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			g.strokeRect(xPos + 10,yPos + 0,breedte-11,hoogte-1);
		}
		else
		{	//g.setColor(Color.orange);
			//g.fillRoundRect(0,0,getSize().width-11,getSize().height-1,8,8);
			//g.setColor(Color.black);
			//g.drawRoundRect(0,0,getSize().width-11,getSize().height-1,8,8);
			
			g.setFillStyle(CssColor.make(255, 200, 0));
			g.fillRect(xPos + 0,yPos + 0,breedte-11,hoogte-1);
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			g.strokeRect(xPos + 0,yPos + 0,breedte-11,hoogte-1);

		}		
		
		//g.setColor(Color.black);
		g.setFillStyle(CssColor.make(0,0,0));
		
		String s = "1/...";
		
//		Font f = new Font("SansSerrif",Font.PLAIN,14);
//		g.setFont(f);
//		FontMetrics fm = g.getFontMetrics();
//		int w = fm.stringWidth(s);
		
		String fontString = "14px sans-serif";
		g.setFont(fontString);
		TextMetrics tm = g.measureText(s);
		int w = (int) Math.round(tm.getWidth());
		
		
		if(!links)
		{	//g.drawString(s,5+(getSize().width-w)/2,getSize().height-4);
			g.fillText(s,xPos + 5+(breedte-w)/2,yPos + hoogte-4);
		
		}
		else 
		{	//g.drawString(s,-5+(getSize().width-w)/2,getSize().height-4);
			g.fillText(s,xPos -5+(breedte-w)/2,yPos + hoogte-4);
		}
	}
	
	
	public Expressie geefUitvoer(int max)
	{	if (AlgebraPijlenGWT.simplify)
		{	Expressie uitv = new Expressie();
			if (pijlIn1 == null || max < 0)
				return null;
			Expressie e1 = pijlIn1.zender.geefUitvoer(max - 1);
			if (e1 == null)
				return null;
			if (e1 instanceof Deling && !Double.isNaN(e1.kind1.geefWaarde().doubleValue()) && 
				e1.kind1.geefWaarde().doubleValue() == 1)
				uitv = e1.kind2;
			else if (e1 instanceof Deling && !Double.isNaN(e1.kind1.geefWaarde().doubleValue()) && 
				e1.kind1.geefWaarde().doubleValue() == -1)
				uitv = new Aftrekking(new BasisExpressie("0"), e1.kind2);
			else if (e1 instanceof Deling) 
				uitv = new Deling(e1.kind2, e1.kind1);
			else 
				uitv = new Deling(new BasisExpressie("1"), e1);
			return uitv;
		}
		else
		{	Expressie uitv = new Expressie();
			if(pijlIn1==null || max<0)return null;
			Expressie e1 = pijlIn1.zender.geefUitvoer(max-1);
			if(e1==null)return null;
			uitv = new Deling(new BasisExpressie("1"),e1);
			return uitv;
		}
	}
	
	public Expressie geefVerborgenUitvoer(int max)
	{	Expressie uitv = new Expressie();
		if(pijlIn1==null || max<0)return null;
		Expressie e1 = pijlIn1.zender.geefVerborgenUitvoer(max-1);
		if(e1==null)return null;
		uitv = new Deling(new BasisExpressie("1"),e1);
		return uitv;
	}
}
