package fi.grafiek3dgwt.client;

import com.google.gwt.canvas.dom.client.CssColor;

/**
 * superclass for creating examples of 3d-graphs: 
 * the superclass sets all 3d-graph-attributes
 * (see class Grafiek3DComponent);<br> 
 * subclasses can reset some of these, but must
 * redefine the function defining the 3d-graph (GraphString),
 * the name of the 3d-graph (nlNaam and enNaam)
 * and method geefWaarde(x,y)  
 * @author huub
 */

public class GrafiekVoorbeeld
{
	double angleXG = 75;
	double angleZG = 25;
	
	int zoomFactorG = 0;

	int translateXFactorG = 0;
	int translateYFactorG = 0;
	int translateZFactorG = 0;
	
	boolean wireFrameG = false;
	
	int finerFactorG = 0;
	
	boolean noAxesG = false;
	
	int floorTypeG = Grafiek3DComponent.NOFLOOR;
	
	int labelTypeG = Grafiek3DComponent.ENDLABELS;
	
	boolean centraleProjG = true;
	
	CssColor graphColor = Grafiek3DComponent.transYellow;
	
	String graphString = "$f@";
	
	String nlNaam = "";
	String enNaam = "";
	
	boolean checkForAsymptotes = false;
	
	public double geefWaarde(double x, double y)
	{	return Double.NaN;
	}

	
}
class Paraboloide extends GrafiekVoorbeeld
{
	public Paraboloide()
	{
		angleXG = 65;
		angleZG = 45;
		translateZFactorG = 1;
		
		graphColor = Grafiek3DComponent.transGreen;
		
		graphString = "$fxx+yy-1@";
		
		nlNaam = "paraboloide";
		enNaam = "paraboloide";
		
		checkForAsymptotes = false;

	}
	public double geefWaarde(double x, double y)
	{	return x*x+y*y-1;
	}

}
class Zadel extends GrafiekVoorbeeld
{
	public Zadel()
	{
		angleXG = 70;
		angleZG = 35;
		zoomFactorG = -1;
		
		graphColor = Grafiek3DComponent.transMagenta;
		
		graphString = "$fxx-yy@";
		
		nlNaam = "zadel";
		enNaam = "saddle";
		
		checkForAsymptotes = false;
	}
	public double geefWaarde(double x, double y)
	{	return x*x-y*y;
	}

}
class ReciprokeTrumpet extends GrafiekVoorbeeld
{
	public ReciprokeTrumpet()
	{
		angleXG = 55;
		angleZG = 15;
		
		finerFactorG = 1;
		
		graphColor = Grafiek3DComponent.transCyan;
		
		graphString = "$f1/(xx+yy)-1@";
		
		nlNaam = "1/ toeter";
		enNaam = "1/ trumpet";
		
		checkForAsymptotes = true;
	}

	public double geefWaarde(double x, double y)
	{	return 1/(x*x+y*y)-1;
	}
	
}
class LnTrumpet extends GrafiekVoorbeeld
{
	public LnTrumpet()
	{
		angleXG = 55;
		angleZG = 30;
		
		finerFactorG = 1;
		
		graphString = "$fln(xx+yy)@";
		
		nlNaam = "ln toeter";
		enNaam = "ln trumpet";
		
		checkForAsymptotes = true;
	}
	public double geefWaarde(double x, double y)
	{	return Math.log(x*x+y*y);
	}

		
}
class SineHat extends GrafiekVoorbeeld
{
	public SineHat()
	{
		angleXG = 55;
		angleZG = 30;
		
		finerFactorG = 1;
		
		graphColor = Grafiek3DComponent.transGreen;
		
		graphString = "$fsin(xx+yy)@";
		
		nlNaam = "sinus hoed";
		enNaam = "sine hat";
		
		checkForAsymptotes = false;
	}
	public double geefWaarde(double x, double y)
	{	return Math.sin(x*x+y*y);
	}

		
}
class TangensChaos extends GrafiekVoorbeeld
{
	public TangensChaos()
	{
		angleXG = 55;
		angleZG = 30;
		
		finerFactorG = 1;
		
		
		graphString = "$ftan(xx+yy)@";
		
		nlNaam = "tangens chaos";
		enNaam = "tangens chaos";
		
		checkForAsymptotes = true;
	}

	public double geefWaarde(double x, double y)
	{	return Math.tan(x*x+y*y);
	}
	
}
