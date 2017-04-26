package fi.algebrapijlengwt.client;

//import java.awt.Polygon;
//import java.awt.*;
//import java.awt.event.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


import fi.algebrapijlengwt.client.expressies_ap.*;

public class WortelSchuifComponent extends AlgebraSchuifComponent 
{	
	Expressie waarde;
	//Font f;
	//FontMetrics fm;
	
	public WortelSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	//super(1,asv,x,y,b,h);
		super(asv,x,y,b,h);
		
		//f = new Font("SansSerrif",Font.PLAIN,14);
		//fm = getFontMetrics(f);
	}
	
	public void paint(Context2d g)
	//public void paint(Graphics g)
  	{ 	super.paint(g);
		if (!visible)
  			return; 

		if(!links)
		{	//g.setColor(Color.orange);
			g.setFillStyle(CssColor.make(255, 200, 0));
			
			//g.fillRoundRect(10,0,getSize().width-11,getSize().height-1,8,8);
			g.fillRect(xPos + 10,yPos + 0,breedte-11,hoogte-1);
			
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			
			//g.drawRoundRect(10,0,getSize().width-11,getSize().height-1,8,8);
			g.strokeRect(xPos + 10,yPos + 0,breedte-11,hoogte-1);
		}
		else
		{	//g.setColor(Color.orange);
			g.setFillStyle(CssColor.make(255, 200, 0));
			
			//g.fillRoundRect(0,0,getSize().width-11,getSize().height-1,8,8);
			g.fillRect(xPos + 0,yPos + 0,breedte-11,hoogte-1);
			
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			
			//g.drawRoundRect(0,0,getSize().width-11,getSize().height-1,8,8);
			g.strokeRect(xPos + 0,yPos + 0,breedte-11,hoogte-1);
		}	
		
		String fontString = "14px sans-serif";
		g.setFont(fontString);
		
		String s1 = "...";
		TextMetrics tm = g.measureText(s1);
		
		int w = (int) Math.round(tm.getWidth());
		
		//Font f1 = new Font("SansSerrif",Font.PLAIN,14);
		//g.setFont(f1);
		
		if(!links)
		{	
			//g.drawString(s1,24,getSize().height-4);
			g.fillText(s1,xPos + 24,yPos + hoogte-4);
			
			//(new Wortelteken(20,12)).paint(g, 17,getSize().height-15);
			(new Wortelteken(20,12)).paint(g, xPos + 17,yPos + 4);// + hoogte-15);
		}
		else 
		{	//g.drawString(s1,14,getSize().height-4);
			g.fillText(s1,xPos + 14,yPos + hoogte-4);
			//(new Wortelteken(20,12)).paint(g, 7,getSize().height-15);
			(new Wortelteken(20,12)).paint(g, xPos + 7,yPos + 4);// + hoogte-15);
		}
	}
	
	
	public Expressie geefUitvoer(int max)
	{	if (AlgebraPijlenGWT.simplify)
		{	Expressie uitv = new Expressie();
			if (pijlIn1==null  || max < 0)
				return null;
			Expressie e1 = pijlIn1.zender.geefUitvoer(max - 1);
			if (e1 == null)
				return null;
			if ((e1 instanceof Macht) && (e1.kind2 instanceof BasisExpressie) && 
				!Double.isNaN(e1.kind2.geefWaarde().doubleValue()) && (e1.kind2.geefWaarde().doubleValue() == 2))
				uitv = e1.kind1;
			else	
				uitv = new Wortel(e1);
			return uitv;
		}
		else
		{	Expressie uitv = new Expressie();
			if(pijlIn1==null  || max<0)return null;
			Expressie e1 = pijlIn1.zender.geefUitvoer(max-1);
			if(e1==null)return null;
			uitv = new Wortel(e1);
			return uitv;
		}
	}
	
	public Expressie geefVerborgenUitvoer(int max)
	{	Expressie uitv = new Expressie();
		if (pijlIn1 == null  || max < 0)
			return null;
		Expressie e1 = pijlIn1.zender.geefVerborgenUitvoer(max - 1);
		if (e1 == null)
			return null;
		uitv = new Wortel(e1);
		return uitv;
	}
}
