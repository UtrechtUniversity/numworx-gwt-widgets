package fi.algebrapijlengwt.client;

import fi.algebrapijlengwt.client.expressies_ap.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * een BewerkingSchuifComponent die de invoer tot een macht verheft 
 */

public class MachtSchuifComponent extends BewerkingSchuifComponent 
{	
	/**
	 * constructor
	 * @param asv het werkveld
	 * @param x x-coordinaat
	 * @param y y-coordinaat
	 * @param b breedte
	 * @param h hoogte
	 */
	public MachtSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(asv,x,y,b,h);
		beginw = new BasisExpressie("2");
	}
	
	/**
	 * teken de MachtSchuifComponent
	 */
	public void paint(Context2d g)
  	{ 	// kastje
		super.paint(g);
		if (!visible)
  			return;
		// stippeltjes en macht
		g.setFillStyle(CssColor.make(0,0,0));
		String s1 = "...";
		String s2 = UF.format0(beginw.geefWaarde(),3);
		String fontString1 = "14px sans-serif";
		String fontString2 = "10px sans-serif";
		if(!links)
		{	g.setFont(fontString1);
			g.fillText(s1,xPos + 20,yPos + hoogte-4);
			g.setFont(fontString2);
			g.fillText(s2,xPos + 35,yPos + hoogte-8);
		}
		else 
		{	g.setFont(fontString1);
			g.fillText(s1,xPos + 10,yPos + hoogte-4);
			g.setFont(fontString2);
			g.fillText(s2,xPos + 25,yPos + hoogte-8);
		}
	}
	
	/**
	 * bepaal de uitvoerExpressie a.d.h.v. de 
	 * uitvoerExpressie van de ASC (if any) verbonden via pijlIn
	 * vereenvoudig de Expressie indien gewenst  
	 */
	public Expressie geefUitvoer(int max)
	{	if(AlgebraPijlenGWT.simplify)
		{	Expressie uitv = new Expressie();
			if (pijlIn1 == null)
				return null;
			Expressie e1 = pijlIn1.zender.geefUitvoer(max-1);
			Expressie e2 = beginw;
			if (e1 == null)
				return null;
			if (e2.geefWaarde().doubleValue() == 0)
				uitv = new BasisExpressie("1");
			else if (e2.geefWaarde().doubleValue() == 1)
				uitv = e1;
			else if ((e1 instanceof Wortel) && (e2.geefWaarde().doubleValue() == 2))
				uitv = e1.kind1;
			else 
				uitv = new Macht(e1, e2);
			return uitv;
		}
		else
		{	Expressie uitv = new Expressie();
			if(pijlIn1==null)
				return null;
			Expressie e1 = pijlIn1.zender.geefUitvoer(max-1);
			Expressie e2 = beginw;
			if(e1==null)
				return null;
			if(e2.geefWaarde().doubleValue()==0)
				uitv = new BasisExpressie("1");
			else if(e2.geefWaarde().doubleValue()==1)
				uitv = e1;
			else uitv = new Macht(e1,e2);
			return uitv;
		}
	}

	/**
	 * bepaal de verborgen uitvoerExpressie a.d.h.v. de verborgen
	 * uitvoerExpressie van de ASC (if any) verbonden via pijlIn  
	 */
	public Expressie geefVerborgenUitvoer(int max)
	{	Expressie uitv = new Expressie();
		if(pijlIn1==null)
			return null;
		Expressie e1 = pijlIn1.zender.geefVerborgenUitvoer(max-1);
		Expressie e2 = beginw;
		if(e1==null)
			return null;
		if(e2.geefWaarde().doubleValue()==0)
			uitv = new BasisExpressie("1");
		else if(e2.geefWaarde().doubleValue()==1)
			uitv = e1;
		else uitv = new Macht(e1,e2);
		return uitv;
	}
	
	/**
	 * kijk of de String in de TekstPopup een bona fide
	 * invoerwaarde is en verwerk die; update de pijlenketting 
	 */
	public void zetInvulWaarde()
	{	boolean isGeldigeInvoer=true;
		{	try
			{	Double w = Double.valueOf(tf.getText());
			}
			catch(NumberFormatException ex)
			{	isGeldigeInvoer = false;
				tf.setText(UF.format0(beginw.geefWaarde(),3));
			}
		}
		if(isGeldigeInvoer)
		{	beginw = new BasisExpressie( tf.getText());
			beginw.zetMaat(fontSize,asv.asvContext2d);
		}
		else
		{	beginw = new BasisExpressie("2");
			beginw.zetMaat(fontSize,asv.asvContext2d);
		}
		zetMaat();
		zetVeranderd(20);
		tf.setVisible(false);
		inputOwner.remove(tf);
		asv.tekenOpnieuw();
	}
}
