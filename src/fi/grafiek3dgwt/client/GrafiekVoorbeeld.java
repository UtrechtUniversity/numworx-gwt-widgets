package fi.grafiek3dgwt.client;

import com.google.gwt.canvas.dom.client.CssColor;

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
	}

		
}
