package fi.algebraexprgwt.client;

//import java.awt.Polygon;
//import java.awt.*;
//import java.awt.event.*;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

import fi.algebraexprgwt.client.expressies_ap.*;

public class OptelSchuifComponent extends AlgebraSchuifComponent 
{	
	Expressie waarde;
	//Font f;
	//FontMetrics fm;
	
	public OptelSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(2,asv,x,y,b,h);
		
		//f = new Font("SansSerrif",Font.PLAIN,14);
		//fm = getFontMetrics(f);
	}
	
	//public void paint(Graphics g)
	public void paint(Context2d g)
  	{ 	
  		if (!visible)
  			return;
  		
  		super.paint(g);
		
		//g.setColor(Color.orange);
  		g.setFillStyle(CssColor.make(255, 200, 0));
  		
		//g.fillRect(0,10,getSize().width-1,getSize().height-11);
  		g.fillRect(xPos,yPos+10,breedte-1,hoogte-11);
  		
  		//g.setColor(Color.black);
  		g.setStrokeStyle(CssColor.make(0,0,0));
  		
  		//g.drawRect(0,10,getSize().width-1,getSize().height-11);
  		g.strokeRect(xPos,yPos+10,breedte-1,hoogte-11);
		
  		g.setFillStyle(CssColor.make(0, 0, 0));
//		g.setFont(f);
		String s = "...+...";
		String fontString = "14px sans-serif";
		g.setFont(fontString);
		TextMetrics tm = g.measureText(s);
		int w = (int) Math.round(tm.getWidth());
		
		//int w = fm.stringWidth(s); 
		
		//g.drawString(s,(getSize().width-w)/2,getSize().height-4);
		g.fillText(s,xPos + (breedte-w)/2,yPos + hoogte-4);
	}
	
	
	public Expressie geefUitvoer(int max)
	{	
//System.out.println("OSC geef uitvoer");		
			Expressie uitv = new Expressie();
			if (pijlIn1 == null || pijlIn2 == null || max < 0)
			{	
//if (pijlIn1 == null)
//System.out.println("pi1 = null");
//if (pijlIn2 == null)
//System.out.println("pi2 = null");
			
				return null;
			}	
			Expressie e1 = pijlIn1.zender.geefUitvoer(max - 1);
			Expressie e2 = pijlIn2.zender.geefUitvoer(max - 1);
			
//if (e1 == null)
//System.out.println("e1 = null");
//if (e2 == null)
//System.out.println("e2 = null");
			
			if (e1 == null || e2 == null)
				return null;
			uitv = new Optelling(e1, e2);
			return uitv;
		
	}
	
	public Expressie geefVerborgenUitvoer(int max)
	{	Expressie uitv = new Expressie();
		if (pijlIn1 == null || pijlIn2 == null || max < 0)
			return null;
		Expressie e1 = pijlIn1.zender.geefUitvoer(max - 1);
		Expressie e2 = pijlIn2.zender.geefUitvoer(max - 1);
		Expressie ve1 = pijlIn1.zender.geefVerborgenUitvoer(max - 1);
		Expressie ve2 = pijlIn2.zender.geefVerborgenUitvoer(max - 1);
		if (e1 != null && e2 == null && ve2 != null)
		{	uitv = new Optelling(e1, ve2);
		}
		else if (e1 == null && ve1 != null && e2 != null)
		{	uitv = new Optelling(ve1, e2);
		}
		else if (ve1 != null && ve2 != null)
			uitv = new Optelling(ve1, ve2);
		else
			return null;
		return uitv;
	}
	
}
