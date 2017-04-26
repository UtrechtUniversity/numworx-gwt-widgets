package fi.algebrapijlengwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

import fi.algebrapijlengwt.client.expressies_ap.*;

/**
 * AlgebraSchuifComponent die de inkomende Expressie e (if any) verandert in 1/e   
 */
public class OmkeringSchuifComponent extends AlgebraSchuifComponent 
{	
	/**
	 * constructor
	 * @param asv het werkveld
	 * @param x de x-positie van deze OmkeerSchuifComponent
	 * @param y de y-positie van deze OmkeerSchuifComponent
	 * @param b de breedte van deze OmkeerSchuifComponent
	 * @param h de hoogte van deze OmkeerSchuifComponent
	 */
	public OmkeringSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(asv,x,y,b,h);
	}
	
	//public void paint(Graphics g)
	public void paint(Context2d g)
  	{ 	
		if (!visible)
  			return; 
		// dit tekent het balletje voor de ingaande pijl		
		super.paint(g);

		// oranje achtergrond, zwarte rand
		if(!links)
		{	
			g.setFillStyle(CssColor.make(255, 200, 0));
			g.fillRect(xPos + 10,yPos + 0,breedte-11,hoogte-1);
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			g.strokeRect(xPos + 10,yPos + 0,breedte-11,hoogte-1);
		}
		else
		{	
			g.setFillStyle(CssColor.make(255, 200, 0));
			g.fillRect(xPos + 0,yPos + 0,breedte-11,hoogte-1);
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			g.strokeRect(xPos + 0,yPos + 0,breedte-11,hoogte-1);
		}		
		
		// opschrift
		g.setFillStyle(CssColor.make(0,0,0));
		String s = "1/...";
		String fontString = "14px sans-serif";
		g.setFont(fontString);
		TextMetrics tm = g.measureText(s);
		int w = (int) Math.round(tm.getWidth());
		
		if (!links)
		{	g.fillText(s,xPos + 5+(breedte-w)/2,yPos + hoogte-4);
		}
		else 
		{	g.fillText(s,xPos -5+(breedte-w)/2,yPos + hoogte-4);
		}
	}
	
	/**
	 * vind
	 */
	public Expressie geefUitvoer(int max)
	{	if (AlgebraPijlenGWT.simplify)
		{	Expressie uitv = new Expressie();
			if (pijlIn1 == null || max < 0)
				return null;
			Expressie e1 = pijlIn1.zender.geefUitvoer(max - 1);
			if (e1 == null)
				return null;
			// e1 = -1/e1.kind2 dus uitv = e1.kind2
			if (e1 instanceof Deling && !Double.isNaN(e1.kind1.geefWaarde().doubleValue()) && 
				e1.kind1.geefWaarde().doubleValue() == 1)
				uitv = e1.kind2;
			// e1 = -1/e1.kind2 dus uitv = -e1.kind2
			else if (e1 instanceof Deling && !Double.isNaN(e1.kind1.geefWaarde().doubleValue()) && 
				e1.kind1.geefWaarde().doubleValue() == -1)
				uitv = new Aftrekking(new BasisExpressie("0"), e1.kind2);
			// e1 = e1.kind1/e1.kind2, dus uitv = e1.kind2/e1.kind1
			else if (e1 instanceof Deling) 
				uitv = new Deling(e1.kind2, e1.kind1);
			else // uitv = 1/e1 
				uitv = new Deling(new BasisExpressie("1"), e1);
			return uitv;
		}
		else
		{	Expressie uitv = new Expressie();
			if (pijlIn1 == null || max < 0)
				return null;
			Expressie e1 = pijlIn1.zender.geefUitvoer(max-1);
			if (e1 == null)
				return null;
			uitv = new Deling(new BasisExpressie("1"),e1);
			return uitv;
		}
	}
	
	
	public Expressie geefVerborgenUitvoer(int max)
	{	Expressie uitv = new Expressie();
		if (pijlIn1 == null || max < 0)
			return null;
		Expressie e1 = pijlIn1.zender.geefVerborgenUitvoer(max-1);
		if (e1 == null)
			return null;
		uitv = new Deling(new BasisExpressie("1"),e1);
		return uitv;
	}
}
