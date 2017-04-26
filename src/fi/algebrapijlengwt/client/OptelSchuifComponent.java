package fi.algebrapijlengwt.client;

import fi.algebrapijlengwt.client.expressies_ap.*;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * een BewerkingSchuifComponent die bij de invoer een getal optelt 
 */

public class OptelSchuifComponent extends BewerkingSchuifComponent 
{	
	/**
	 * constructor
	 * @param asv het werkveld
	 * @param x x-coordinaat
	 * @param y y-coordinaat
	 * @param b breedte
	 * @param h hoogte
	 */
	public OptelSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(asv, x, y, b, h);
	}
	
	/**
	 * teken de OptelSchuifComponent
	 */
	public void paint(Context2d g)
  	{ 	// doosje
		super.paint(g);
		if (!visible)
  			return;
		// waarde
  		g.setFillStyle(CssColor.make(0,0,0));
		String s = "+ " + UF.format0(beginw.geefWaarde(),3);
		String fontString = "14px sans-serif";
		g.setFont(fontString);
		TextMetrics tm = g.measureText(s);
		int w = (int) Math.round(tm.getWidth());
		if (!links)
		{	g.fillText(s,xPos + 5+(breedte-w)/2, yPos + hoogte-4);
		}
		else 
		{	g.fillText(s,xPos -5+(breedte-w)/2,yPos + hoogte-4);
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
			if (pijlIn1 == null)
				return null;
			Expressie e1 = pijlIn1.zender.geefUitvoer(max - 1);
			Expressie e2 = beginw;
			if (e1 == null)
				return null;
			double d = 0;
			if (e1 instanceof Optelling)
			{	d = e1.kind2.geefWaarde().doubleValue() + e2.geefWaarde().doubleValue();
			}
			else if (e1 instanceof Aftrekking && Double.isNaN(e1.kind1.geefWaarde().doubleValue()))
			{	d = -e1.kind2.geefWaarde().doubleValue() + e2.geefWaarde().doubleValue();
			}
			else
			{	d = e2.geefWaarde().doubleValue();
				if (d == 0)
					uitv = e1;
				else if (d > 0)
					uitv = new Optelling(e1, e2);
				else //d<0
				{	e2 = new BasisExpressie(UF.format0(-d,3));
					uitv = new Aftrekking(e1, e2);
				}
				return uitv;
			}
			if (d > 0)
			{	e2 = new BasisExpressie(UF.format0(d,3));
				uitv = new Optelling(e1.kind1, e2);
			}
			else if(d < 0)
			{	e2 = new BasisExpressie(UF.format0(-d,3));
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
	/**
	 * bepaal de verborgen uitvoerExpressie a.d.h.v. de verborgen
	 * uitvoerExpressie van de ASC (if any) verbonden via pijlIn  
	 */
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
