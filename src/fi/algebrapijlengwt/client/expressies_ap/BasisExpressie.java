package fi.algebrapijlengwt.client.expressies_ap;

//import java.awt.*;

import fi.algebrapijlengwt.client.StringUtils;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

public class BasisExpressie extends Expressie  
{	public String basisString;
	double w;
	boolean isWaarde;
	
	public BasisExpressie(String s)
	{	super();
		basisString = s;
		isVeelterm = false;
		isProdukt = false;
		isBasis = true;
		isAsym = false;

		if (!Double.isNaN(geefWaarde().doubleValue()))
		{	isWaarde = true;
			w = geefWaarde().doubleValue();
		}
	}
	
	//public void teken(Graphics g, int x, int y)
	public void teken(Context2d g, int x, int y)
  	{ 	
		//g.drawString(basisString, x, y+fm.getAscent()-1);
		g.setFillStyle(CssColor.make(0,0,0));
		g.fillText(basisString, x, y);
		
	}
	
	//public void zetMaat(FontMetrics fm)
	public void zetMaat(int fs, Context2d c2d)
  	{	//this.fm = fm;
		fontSize = fs;
		//hoogte = fm.getHeight()-2;
		hoogte = fs - 2;
		//breedte = fm.stringWidth(basisString);
		TextMetrics tm = c2d.measureText(basisString);
		breedte = (int) Math.round(tm.getWidth());
		ashoogte = hoogte/2;
	}
	
	public Double geefWaarde()
	{	Double waarde = new Double(Double.NaN); //null;
		try
		{	waarde = Double.valueOf(basisString);
		}
		catch(NumberFormatException e)
		{	
		}
		return waarde;
	}
	
	public Double geefWaarde(double subst)
	{	if (Double.isNaN(geefWaarde().doubleValue()))
			return new Double(subst);
		else 
			return geefWaarde();
	}
	
	public double geefW(double subst)
	{	if (!isWaarde)
			return subst;
		else 
			return w;
	}
	
	public Expressie substitueer(double subst, String var)
	{	if (basisString.equals(var))
		{	return new BasisExpressie("" + subst);
		}
		else return new BasisExpressie(basisString);
	}
	
	public String geefVarNaam()
	{	if (Double.isNaN(geefWaarde().doubleValue()))
			return basisString;
		return null;
	}

	public String toString()
	{	
		String basisStringUit = StringUtils.replaceStr(basisString,"?(","$s");
		basisStringUit = StringUtils.replaceStr(basisStringUit,")","@");
		
		if(!Double.isNaN(w) && (!Algebra.withinLongRange((long)w) || basisString.indexOf('E')>-1))basisStringUit = StringUtils.replaceStr(basisString,"E","*$p10$n") + "@@";
        
        //if(AlgebraPijlenOpdr.language.toString().equals("nl"))
        	basisStringUit = basisStringUit.replace('.',',');
        
		 
        return basisStringUit;
	}
	
	public String toStringStrikt()
	{	
		String basisStringUit = StringUtils.replaceStr(basisString,"?(","$s");
        basisStringUit = StringUtils.replaceStr(basisStringUit,")","@");
        
        if(!Double.isNaN(w) && (!Algebra.withinLongRange((long)w) || basisString.indexOf('E')>-1))basisStringUit = StringUtils.replaceStr(basisString,"E","*$p10$n") + "@@";
        
        //if(AlgebraPijlenOpdr.language.toString().equals("nl"))
        	basisStringUit = basisStringUit.replace('.',',');
        return basisStringUit;
	}
	
}
