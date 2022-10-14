package fi.weblogogwt.client.logotekenap;

import com.google.gwt.user.client.ui.LayoutPanel;

import fi.weblogogwt.client.VarInputComponent;

import com.google.gwt.canvas.client.Canvas;

/**
 * abstract superclass for all output panels (now 'teken' en 'reken')
 * this resolves the problem of the 'teken'...
 * 
 * Note:	design drawback is that all subclaasses must implement ALL commands, 
 * 			the irrelevant ones will be dummies or error messages
 * Note2:	(July 2015) This abstract superclass is now unnecessary, since there is no separate 'Rekenblad'
 * 			Superclass retained because of Peter's future plans for a RekenApplet.
 * 
 * @author Berge020
 */
public abstract class Uitvoerblad extends LayoutPanel
{
	public abstract void links(double dHoek);
	
  	public abstract void rechts(double dHoek);

	public abstract void vooruit(double dy);
  	
	public abstract void stap(double dx,double dy);
  	
	public abstract void penAan(int r, int g, int b);
  	
	public abstract void penUit();
	
	public abstract void vulAan(int r, int g, int b);
	
	public abstract void vulUit();
	
	public abstract void vulBlad(int r, int g, int b);
	
	public abstract void printl(String s);
	
	public abstract void print(String s);
	
	public abstract String input(String s);
	
	public abstract void prepareInput(VarInputComponent vic, String s);
	
 	public abstract void initializeDrawing(boolean b);
 	
 	public abstract void paintDrawing(boolean cursor);
 	
 	public abstract Canvas getCanvas();
 	
 	public abstract void initContext2d();
	
}
