package fi.algebrapijlengwt.client;


import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

import fi.algebrapijlengwt.client.expressies_ap.*;

/**
 * een BewerkingSchuifComponent die de wortel neemt van de invoer 
 */

public class WortelSchuifComponent extends AlgebraSchuifComponent 
{
	/**
	 * de waarde na het nemen van de wortel (if any)
	 */
	Expressie waarde;

	/**
	 * constructor
	 * @param asv het werkveld
	 * @param x x-coordinaat
	 * @param y y-coordinaat
	 * @param b breedte
	 * @param h hoogte
	 */
	public WortelSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(asv,x,y,b,h);
	}
	
	/**
	 * teken de WortelSchuifComponent
	 */
	public void paint(Context2d g)
  	{ 	// balletje
		super.paint(g);
		if (!visible)
  			return; 
		// doosje
		if(!links)
		{	// oranje
			g.setFillStyle(CssColor.make(255, 200, 0));
			g.fillRect(xPos + 10,yPos + 0,breedte-11,hoogte-1);
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			g.strokeRect(xPos + 10,yPos + 0,breedte-11,hoogte-1);
		}
		else
		{	// oranje
			g.setFillStyle(CssColor.make(255, 200, 0));
			g.fillRect(xPos + 0,yPos + 0,breedte-11,hoogte-1);
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			g.strokeRect(xPos + 0,yPos + 0,breedte-11,hoogte-1);
		}	

		// stippeltjes en wortelteken 
		String fontString = "14px sans-serif";
		g.setFont(fontString);
		String s1 = "...";
		TextMetrics tm = g.measureText(s1);
		int w = (int) Math.round(tm.getWidth());
		if(!links)
		{	g.fillText(s1,xPos + 24,yPos + hoogte-4);
			(new Wortelteken(20,12)).paint(g, xPos + 17,yPos + 4);// + hoogte-15);
		}
		else 
		{	g.fillText(s1,xPos + 14,yPos + hoogte-4);
			(new Wortelteken(20,12)).paint(g, xPos + 7,yPos + 4);// + hoogte-15);
		}
	}
	
	/**
	 * bepaal de uitvoerExpressie a.d.h.v. de 
	 * uitvoerExpressie van de ASC (if any) verbonden via pijlIn
	 * vereenvoudig de Expressie indien gewenst  
	 */
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
	/**
	 * bepaal de verborgen uitvoerExpressie a.d.h.v. de verborgen
	 * uitvoerExpressie van de ASC (if any) verbonden via pijlIn  
	 */
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
