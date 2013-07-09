package fi.grafiek3dgwt.client;

import java.awt.*;

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
		
	}
	
}

