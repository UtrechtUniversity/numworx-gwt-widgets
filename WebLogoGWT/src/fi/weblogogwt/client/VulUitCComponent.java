package fi.weblogogwt.client;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.VarSet;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

/**
 * the command represented by this class has the following effect (see class TekenBlad)
 * after executing the command vulAan() (see class vulAanComponent) a new polygon is created
 * and all moves in the drawing area are considered as the sides of this polygon;
 * at vulUit this polygon is filled with the color specified in the last vulAan()
 * command   
 */
public class VulUitCComponent extends SimpleCommandComponent
{
	public VulUitCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	super(x,y,b,h,sv);
		commandName = WebLogoGWT.rb.vulUitTekst(); 
		commandNameTranslated = "fillOff"; 
	}
	
	/**
	 * execute this command, if tracing, change its color and display
	 * the command; see class TraceBeheerder
	 */
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		ub.vulUit();
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if (traceKleur) trb.setCommandInfo(getCommandName(), varSet);
		return traceKleur;
	}
	
}