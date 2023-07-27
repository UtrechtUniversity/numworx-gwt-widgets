package fi.grafiek3dgwt.client;

/**
 * superclass for creating examples of 3d-curvess: 
 * the superclass sets all 3d-curve-attributes
 * (see class Grafiek3DComponent);<br> 
 * subclasses can reset some of these, but must
 * redefine the Strings defining the curve:
 * curveXString, curveYString, curveZString,
 * tMinString, tMaxnString, tPointsString,
 * and the name of the 3d-curve (nlNaam and enNaam)
 * @author huub
 */

public class KrommeVoorbeeld 
{
	double angleXC = 75;
	double angleZC = 25;
	
	int zoomFactorC = 0;
	
	int translateXFactorC = 0;
	int translateYFactorC = 0;
	int translateZFactorC = 0;
	
	boolean noAxesC = false;
	
	int floorTypeC = Grafiek3DComponent.NOFLOOR;
	
	int labelTypeC = Grafiek3DComponent.ENDLABELS;
	
	boolean centraleProjC = true;
	
	String curveXString = "$f@";
	String curveYString = "$f@";
	String curveZString = "$f@";
	String tMinString = "$f@";
	String tMaxString = "$f@";
	String tPointsString = "$f@";

	String nlNaam = "";
	String enNaam = "";
	
	boolean checkForAsymptotes = false;
}
class Helix extends KrommeVoorbeeld
{
	public Helix()
	{
		angleXC = 65;
		angleZC = 20;
		
		curveXString = "$fcos(10t)@";
		curveYString = "$fsin(10t)@";
		curveZString = "$ft@";
		tMinString = "$f-\u03C0/2@";
		tMaxString = "$f\u03C0/2@";
		tPointsString = "$f200@";
		
		nlNaam = "helix";
		enNaam = "helix";
		
		checkForAsymptotes = false;
		
	}
}
class ConeHelix extends KrommeVoorbeeld
{
	public ConeHelix()
	{
		angleXC = 65;
		angleZC = 20;
		
		zoomFactorC = 2;
		
		curveXString = "$ft*cos(10t)@";
		curveYString = "$ft*sin(10t)@";
		curveZString = "$ft@";
		//tMinString = "$f-2\u03C0@";
		tMinString = "$f-2*\u03C0@";
		//tMaxString = "$f2\u03C0@";
		tMaxString = "$f2*\u03C0@";
		tPointsString = "$f400@";
		
		nlNaam = "kegels helix";
		enNaam = "cones helix";
		
		checkForAsymptotes = false;
		
	}
}
class TorusHelix extends KrommeVoorbeeld
{
	public TorusHelix()
	{
		angleXC = 60;
		angleZC = 40;
		
		zoomFactorC = 1;
		
		curveXString = "$f(3+cos(20t))*cost@";
		curveYString = "$f(3+cos(20t))*sint@";
		curveZString = "$fsin(20t)@";
		tMinString = "$f0@";
		//tMaxString = "$f2\u03C0@";
		tMaxString = "$f2*\u03C0@";
		tPointsString = "$f400@";
		
		nlNaam = "torus helix";
		enNaam = "torus helix";
		
		checkForAsymptotes = false;
		
	}
	
}
class FlowerLeaves extends KrommeVoorbeeld
{
	public FlowerLeaves()
	{
		angleXC = 45;
		angleZC = 35;
		
		curveXString = "$f2*sin(3t)*cost@";
		curveYString = "$f2*sin(3t)*sint@";
		curveZString = "$fsin(3t)@";
		tMinString = "$f0@";
		//tMaxString = "$f2\u03C0@";
		tMaxString = "$f2*\u03C0@";
		tPointsString = "$f100@";
		
		nlNaam = "bloembladen";
		enNaam = "flower leaves";
		
		checkForAsymptotes = false;
		
	}
	
}

