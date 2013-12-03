package fi.grafiek3dgwt.client;

import java.awt.*;

import com.google.gwt.canvas.dom.client.CssColor;


public class OppervlakVoorbeeld 
{
	double angleXS = 75;
	double angleZS = 25;		
	
	int zoomFactorS = 0;
	
	int translateXFactorS = 0;
	int translateYFactorS = 0;
	int translateZFactorS = 0;

	boolean wireFrameS = false;
	
	boolean noAxesS = false;
	
	int floorTypeS = Grafiek3DComponent.NOFLOOR;;
	
	int labelTypeS = Grafiek3DComponent.ENDLABELS;
	
	boolean centraleProjS = true;
	
	CssColor surfaceColor = Grafiek3DComponent.transYellow;
		
	String surfaceXString = "$f@";
	String surfaceYString = "$f@";
	String surfaceZString = "$f@";
	String uMinString = "$f@";
	String uMaxString = "$f@";
	String uPointsString = "$f@";
	String vMinString = "$f@";
	String vMaxString = "$f@";
	String vPointsString = "$f@";
	
	String nlNaam = "";
	String enNaam = "";
	
	boolean checkForAsymptotes = false;

}
class Cylinder extends OppervlakVoorbeeld
{
	public Cylinder()
	{
		angleXS = 60;
		angleZS = 30;
		
		surfaceColor = Grafiek3DComponent.transGreen;

		surfaceXString = "$fcosu@";
		surfaceYString = "$fsinu@";
		surfaceZString = "$fv@";
		uMinString = "$f0@";
		//uMaxString = "$f2\u03C0@";
		uMaxString = "$f2*\u03C0@";
		uPointsString = "$f25@";
		vMinString = "$f-\u03C0/2@";
		vMaxString = "$f\u03C0/2@";
		vPointsString = "$f10@";
		
		nlNaam = "cylinder";
		enNaam = "cylinder";
		
		checkForAsymptotes = false;
		
	}
}
class Cones extends OppervlakVoorbeeld
{
	public Cones()
	{
		angleXS = 60;
		angleZS = 30;
		
		surfaceColor = Grafiek3DComponent.transMagenta;
		
		surfaceXString = "$fv*cosu@";
		surfaceYString = "$fv*sinu@";
		surfaceZString = "$fv@";
		uMinString = "$f0@";
		//uMaxString = "$f2\u03C0@";
		uMaxString = "$f2*\u03C0@";
		uPointsString = "$f25@";
		vMinString = "$f-\u03C0/2@";
		vMaxString = "$f\u03C0/2@";
		vPointsString = "$f10@";
		
		nlNaam = "kegels";
		enNaam = "cones";
		
		checkForAsymptotes = false;
		
	}
}
class Helicoide extends OppervlakVoorbeeld
{
	public Helicoide()
	{
		angleXS = 50;
		angleZS = 210;
		
		surfaceColor = Grafiek3DComponent.transMagenta;
		
		surfaceXString = "$fv*cos(2u)@";
		surfaceYString = "$fv*sin(2u)@";
		surfaceZString = "$fu@";
		uMinString = "$f-\u03C0/2@";
		uMaxString = "$f\u03C0/2@";
		uPointsString = "$f25@";
		vMinString = "$f-2@";
		vMaxString = "$f2@";
		vPointsString = "$f20@";
		
		nlNaam = "helicoide";
		enNaam = "helicoide";
		
		checkForAsymptotes = false;
		
	}
}
class Sphere extends OppervlakVoorbeeld
{
	public Sphere()
	{
		surfaceColor = Grafiek3DComponent.transCyan;
		
		surfaceXString = "$f1.5*cosv*cosu@";
		surfaceYString = "$f1.5*cosv*sinu@";
		surfaceZString = "$f1.5*sinv@";
		uMinString = "$f0@";
		//uMaxString = "$f2\u03C0@";
		uMaxString = "$f2*\u03C0@";
		uPointsString = "$f25@";
		vMinString = "$f-\u03C0/2@";
		vMaxString = "$f\u03C0/2@";
		vPointsString = "$f25@";
		
		nlNaam = "bol";
		enNaam = "sphere";
		
		checkForAsymptotes = false;
	}
}
class Ellipsoid extends OppervlakVoorbeeld
{
	public Ellipsoid()
	{
		angleXS = 83;
		angleZS = 25;
		
		surfaceXString = "$f2*cosv*cosu@";
		surfaceYString = "$f3*cosv*sinu@";
		surfaceZString = "$f1.5*sinv@";
		uMinString = "$f0@";
		//uMaxString = "$f2\u03C0@";
		uMaxString = "$f2*\u03C0@";
		uPointsString = "$f25@";
		vMinString = "$f-\u03C0/2@";
		vMaxString = "$f\u03C0/2@";
		vPointsString = "$f25@";
		
		nlNaam = "ellipsoide";
		enNaam = "ellipsoid";
		
		checkForAsymptotes = false;
		
	}
}
class Torus extends OppervlakVoorbeeld
{
	public Torus()
	{
		angleXS = 60;
		angleZS = 40;
		
		zoomFactorS = 1;
		
		surfaceXString = "$f(3+cosv)*cosu@";
		surfaceYString = "$f(3+cosv)*sinu@";
		surfaceZString = "$fsinv@";
		uMinString = "$f0@";
		//uMaxString = "$f2\u03C0@";
		uMaxString = "$f2*\u03C0@";
		uPointsString = "$f35@";
		vMinString = "$f0@";
		//vMaxString = "$f2\u03C0@";
		vMaxString = "$f2*\u03C0@";
		vPointsString = "$f25@";
		
		nlNaam = "donut";
		enNaam = "donut";
		
		checkForAsymptotes = false;
		
	}
	
}
class Trumpet extends OppervlakVoorbeeld
{
	public Trumpet()
	{
		angleXS = 60;
		angleZS = 30;
		
		zoomFactorS = -1;
		translateZFactorS = 4;
		
		surfaceColor = Grafiek3DComponent.transGreen;
				
		surfaceXString = "$f(v/2)*(v/2)*cosu@";
		surfaceYString = "$f(v/2)*(v/2)*sinu@";
		surfaceZString = "$fv@";
		uMinString = "$f0@";
		//uMaxString = "$f2\u03C0@";
		uMaxString = "$f2*\u03C0@";
		uPointsString = "$f25@";
		vMinString = "$f0@";
		vMaxString = "$f\u03C0@";
		vPointsString = "$f20@";
		
		nlNaam = "toeter";
		enNaam = "trumpet";
		
		checkForAsymptotes = false;
		
	}
}
class EightSurface extends OppervlakVoorbeeld
{
	public EightSurface()
	{
		angleXS = 60;
		angleZS = 20;
		
		zoomFactorS = -2;
		translateZFactorS = 4; 
		
		surfaceColor = Grafiek3DComponent.transCyan;
		
		surfaceXString = "$fcosu*cosv*sinv@";
		surfaceYString = "$fsinu*cosv*sinv@";
		surfaceZString = "$fsinv@";
		uMinString = "$f0@";
		//uMaxString = "$f2\u03C0@";
		uMaxString = "$f2*\u03C0@";
		uPointsString = "$f25@";
		vMinString = "$f0@";
		vMaxString = "$f\u03C0/2@";
		vPointsString = "$f25@";
		
		nlNaam = "luchtballon";
		enNaam = "balloon";
		
		checkForAsymptotes = false;
		
	}	
}

class Shell extends OppervlakVoorbeeld
{
	public Shell()
	{
		angleXS = 60;
		angleZS = 20;
		
		translateXFactorS = -1;
		
		surfaceXString = "$f(4/3)^u*sinv*sinv*cosu@";
		surfaceYString = "$f(4/3)^u*sinv*sinv*sinu@";
		surfaceZString = "$f(4/3)^u*sinv*cosv@";
		uMinString = "$f-6@";
		uMaxString = "$f\u03C0@";
		uPointsString = "$f25@";
		vMinString = "$f0@";
		vMaxString = "$f\u03C0@";
		vPointsString = "$f20@";
		
		nlNaam = "schelp";
		enNaam = "shell";
		
		checkForAsymptotes = false;
		
	}

}
class KleinBagel extends OppervlakVoorbeeld
{
	public KleinBagel()
	{
		angleXS = 60;
		angleZS = 40;
		
		zoomFactorS = 1;
		
		surfaceColor = Grafiek3DComponent.transCyan;
		
		surfaceXString = "$f(2.5+cos(u/2)*cosv-sin(u/2)*sin(2v))*cosu@";
		surfaceYString = "$f(2.5+cos(u/2)*cosv-sin(u/2)*sin(2v))*sinu@";
		surfaceZString = "$fsin(u/2)*cosv+cos(u/2)*sin(2v)@";
		uMinString = "$f0@";
		//uMaxString = "$f2\u03C0@";
		uMaxString = "$f2*\u03C0@";
		uPointsString = "$f35@";
		vMinString = "$f0@";
		//vMaxString = "$f2\u03C0@";
		vMaxString = "$f2*\u03C0@";
		vPointsString = "$f25@";
		
		nlNaam = "Klein bagel";
		enNaam = "Klein bagel";
		
		checkForAsymptotes = false;
		
	}
	
}
