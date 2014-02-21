package fi.algebraexprgwt.client.expressies_ap;

//import java.awt.*;

import com.google.gwt.canvas.dom.client.Context2d;


public class Expressie 
{	
	public int breedte;
	public int hoogte;
	public int ashoogte;
	public Expressie kind1, kind2;
	public String operatorString;
	boolean isVeelterm;
	boolean isProdukt;
	boolean isBasis;
	boolean isAsym;

//	static DecimalFormatSymbols dfs;
//	public static DecimalFormat df;
//	public static FontMetrics fm;
	public static String fontType =  "sans-serif";
	public static int fontSize = 12; //px
	
	public static boolean isInteger(double d)
	{	return Math.abs(Math.rint(d)-d)<0.000000001;
	}
	
	public Expressie()
	{	//dfs = new DecimalFormatSymbols();
		//dfs.setDecimalSeparator('.');
		//df = new DecimalFormat("0.###", dfs);
	}
	//public void zetMaat(FontMetrics fm)
	public void zetMaat(int fs, Context2d c2d)
  	{
	}
	//public void teken(Graphics g, int x, int y)
	public void teken(Context2d g, int x, int y)
  	{ 
	}
	public Double geefWaarde()
	{	return null;
	}
	public double geefW(double subst)
	{	return 0;
	}
	public boolean isWaarde(double subst)
	{	return true;
	}
	public boolean isWaarde()
	{	return !Double.isNaN(geefWaarde().doubleValue());
	}
	public String geefVarNaam()
	{	return null;
	}
	public Expressie substitueer(double subst, String var)
	{	return null;
	}
	
	public String toString()
	{	return null;
	}
    
	public String toStringStrikt()
	{	return null;
	}
	
}
