package fi.algebrapijlengwt.client;


import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

import fi.algebrapijlengwt.client.expressies_ap.*;

/**
 * een BewerkingSchuifComponent die de invoer door een getal deelt 
 */

public class DeelSchuifComponent extends BewerkingSchuifComponent 
{	
	/**
	 * constructor
	 * @param asv het werkveld
	 * @param x x-coordinaat
	 * @param y y-coordinaat
	 * @param b breedte
	 * @param h hoogte
	 */
	public DeelSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(asv,x,y,b,h);
	}
	
	/**
	 * teken de DeelSchuifComponent
	 */
	public void paint(Context2d g)
  	{ 	// doosje
		super.paint(g);
  		if (!visible)
  			return; 
  		// wwarde
  		g.setFillStyle(CssColor.make(0,0,0));
		String s = "/ " + UF.format0(beginw.geefWaarde(),3);
		String fontString = "14px sans-serif";
		g.setFont(fontString);
		TextMetrics tm = g.measureText(s);
		int w = (int) Math.round(tm.getWidth());
		
		if(!links)
		{	g.fillText(s,xPos + 5+(breedte-w)/2,yPos + hoogte-4);
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
			Expressie e1 = pijlIn1.zender.geefUitvoer(max-1);
			Expressie e2 = beginw;
			if (e1 == null)
				return null;
			double d = 1;
			if (e1 instanceof Vermenigvuldiging)
			{	d = e1.kind1.geefWaarde().doubleValue() / e2.geefWaarde().doubleValue();
				double dn = e2.geefWaarde().doubleValue() / e1.kind1.geefWaarde().doubleValue();
				if (d == 1)
					uitv = e1.kind2;
				else if (d == -1)
					uitv = new Aftrekking(new BasisExpressie("0"), e1.kind2);
				else if (d > 0 && Expressie.isInteger(d))
					uitv = new Vermenigvuldiging(new BasisExpressie(UF.format0(d,3)), e1.kind2);
				else if (d > 0 && Expressie.isInteger(dn))
					uitv = new Deling(e1.kind2, new BasisExpressie(UF.format0(dn,3)));
				else if (d <0 && Expressie.isInteger(d))
					uitv = new Aftrekking(new BasisExpressie("0"),
							              new Vermenigvuldiging(new BasisExpressie(UF.format0(-d,3)), e1.kind2));
				else if (d < 0 && Expressie.isInteger(dn))
					uitv = new Deling(e1.kind2,new BasisExpressie(UF.format0(dn,3)));
				else uitv = new Deling(e1,e2);
				return uitv;
			}
			else if(e1 instanceof Aftrekking && !Double.isNaN(e1.kind1.geefWaarde().doubleValue()) && 
					e1.kind1.geefWaarde().doubleValue()==0 && e1.kind2 instanceof Vermenigvuldiging)
			{	d = -e1.kind2.kind1.geefWaarde().doubleValue() / e2.geefWaarde().doubleValue();
				double dn = -e2.geefWaarde().doubleValue() / e1.kind2.kind1.geefWaarde().doubleValue();
				if(d==1)
					uitv = e1.kind2.kind2;
				else if(d==-1)
					uitv = new Aftrekking(new BasisExpressie("0"),e1.kind2.kind2);
				else if(d>0 && Expressie.isInteger(d))
					uitv = new Vermenigvuldiging(new BasisExpressie(UF.format0(d,3)),e1.kind2.kind2);
				else if(d>0 && Expressie.isInteger(dn))
					uitv = new Deling(e1.kind2.kind2,new BasisExpressie(UF.format0(dn,3)));
				else if(d<0 && Expressie.isInteger(d))
					uitv = new Aftrekking(new BasisExpressie("0"),new Vermenigvuldiging(new BasisExpressie(UF.format0(-d,3)),e1.kind2.kind2));
				else if(d<0 && Expressie.isInteger(dn))
					uitv = new Deling(e1.kind2.kind2,new BasisExpressie(UF.format0(dn,3)));
				else uitv = new Deling(e1,e2);
				return uitv;
			}
			else if(e1 instanceof Deling && !Double.isNaN(e1.kind2.geefWaarde().doubleValue()))
			{	d = e2.geefWaarde().doubleValue() * e1.kind2.geefWaarde().doubleValue();
				if(d==1)
					uitv = e1.kind1;
				else if(d==-1)
					uitv = new Aftrekking(new BasisExpressie("0"),e1.kind1);
				else uitv = new Deling(e1.kind1,new BasisExpressie(UF.format0(d,3)));
				return uitv;
			}
			else if(e1 instanceof Deling && !Double.isNaN(e1.kind2.geefWaarde().doubleValue()))
			{	d = e1.kind1.geefWaarde().doubleValue() / e2.geefWaarde().doubleValue();
				double dn = e2.geefWaarde().doubleValue() / e1.kind1.geefWaarde().doubleValue();
				if(Expressie.isInteger(d))
					uitv = new Deling(new BasisExpressie(UF.format0(d,3)),e1.kind2);
				else if(Expressie.isInteger(dn))
					uitv = new Deling(new BasisExpressie("1"),new Vermenigvuldiging(new BasisExpressie(UF.format0(dn,3)),e1.kind2));
				else uitv = new Deling(e1.kind1,new Vermenigvuldiging(e2,e1.kind2));
				return uitv;
			}
			else
			{	if(e2.geefWaarde().doubleValue()==1)
					uitv = e1;
				else if(e2.geefWaarde().doubleValue()==-1)
					uitv = new Aftrekking(new BasisExpressie("0"),e1);
				else uitv = new Deling(e1,e2);
				return uitv;
			}
		}
		else
		{	Expressie uitv = new Expressie();
			if(pijlIn1==null)return null;
			Expressie e1 = pijlIn1.zender.geefUitvoer(max-1);
			Expressie e2 = beginw;
			if(e1==null)return null;
			if(e2.geefWaarde().doubleValue()==1)uitv = e1;
			else uitv = new Deling(e1,e2);
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
		Expressie e1 = pijlIn1.zender.geefVerborgenUitvoer(max-1);
		Expressie e2 = beginw;
		if (e1 == null)
			return null;
		if (e2.geefWaarde().doubleValue() == 1)
			uitv = e1;
		else uitv = new Deling(e1,e2);
		return uitv;
	}
}
