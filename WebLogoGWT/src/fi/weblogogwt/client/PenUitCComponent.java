package fi.weblogogwt.client;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

/**
 * class representing the penUit() command: lift the pen off the paper in the
 * drawing area, i.e. if making moves, do not connect begin and end positions
 * with a line; see class TekenBlad  
 */
public class PenUitCComponent extends SimpleCommandComponent
{
	/**
	 * constructor
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param sv parent containing the drawing Canvas
	 */
	public PenUitCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		commandName = WebLogoGWT.rb.penUitTekst(); 
		commandNameTranslated = "penOff"; 
	}
		
	/**
	 * execute this command, if tracing, change its color and display
	 * the command; see class TraceBeheerder
	 */
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	ub.penUit();
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) trb.setCommandInfo(getCommandName(), varSet);
		return traceKleur;
	}
	
}
