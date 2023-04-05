package fi.algebraexprgwt.client;

//import java.awt.*;
//import java.awt.event.*;
import fi.algebraexprgwt.client.expressies_ap.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


public class MachtSchuifComponent extends AlgebraSchuifComponent 
{	
	Expressie waarde;
	//Font f;
	//FontMetrics fm;
	
	public MachtSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(2,asv,x,y,b,h);
		
		//f = new Font("SansSerrif",Font.PLAIN,14);
		//fm = getFontMetrics(f);
	}
	
	//public void paint(Graphics g)
	public void paint(Context2d g)
  	{ 	super.paint(g);
		if (!visible)
  			return; 
	
//		g.setColor(Color.orange);
//		g.fillRect(0,10,getSize().width-1,getSize().height-11);
//		g.setColor(Color.black);
//		g.drawRect(0,10,getSize().width-1,getSize().height-11);
		
		g.setFillStyle(CssColor.make(255, 200, 0));
		g.fillRect(xPos,yPos+10,breedte-1,hoogte-11);
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.strokeRect(xPos,yPos+10,breedte-1,hoogte-11);

		g.setFillStyle(CssColor.make(0, 0, 0));
		String s1 = "...";
		String s2 = "...";
		
		String fontString1 = "14px sans-serif";
		String fontString2 = "10px sans-serif";

		//Font f1 = new Font("SansSerrif",Font.PLAIN,14);
		//Font f2 = new Font("SansSerrif",Font.PLAIN,10);
		g.setFont(fontString1);
		//g.drawString(s1,10,getSize().height-4);
		g.fillText(s1,xPos+10,yPos+hoogte-4);
		
		g.setFont(fontString2);
		//g.drawString(s2,25,getSize().height-12);
		g.fillText(s2,xPos+25,yPos+hoogte-12);
	}
	
	
	public Expressie geefUitvoer(int max)
	{	Expressie uitv = new Expressie();
		if(pijlIn1==null || pijlIn2==null || max<0)return null;
		Expressie e1 = pijlIn1.zender.geefUitvoer(max-1);
		Expressie e2 = pijlIn2.zender.geefUitvoer(max-1);
		if(e1==null || e2==null)return null;
		uitv = new Macht(e1,e2);
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
		{	uitv = new Macht(e1, ve2);
		}
		else if (e1 == null && ve1 != null && e2 != null)
		{	uitv = new Macht(ve1, e2);
		}
		else if (ve1 != null && ve2 != null)
			uitv = new Macht(ve1, ve2);
		else
			return null; 

		return uitv;
	}	
}
