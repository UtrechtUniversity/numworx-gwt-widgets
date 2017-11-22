package fi.weblogogwt.client;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.parameters.NumericParameter;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

/**
 * class representing the links(ang) command: turn the cursor left  
 * over ang degrees relative to the direction to which the cursor
 * is pointing; see class TekenBlad; <br>
 * the command has one parameter: a String representing
 * the angle , see class NumericParameter   
 */

public class LinksCComponent  extends ParameterCommandComponent
{
	/**
	 * constructor
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param sv instance of JavaLogoSchuifVeld sv containing the drawing Canvas; necessary for superclass constructor
	 */
	public LinksCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		noParameters = 1;
		parameters[0] = new NumericParameter();
		commandName = WebLogoGWT.rb.linksTekst(); 
		commandNameTranslated = "left"; 
	}
		
	/**
	 * check the numeric parameter of this command for correctness;
	 * execute this command, if tracing, change its color and display
	 * the command and parameter; see class TraceBeheerder
	 */
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		if ( !parameters[0].isCorrect(varSet) ) return false; 
		ub.links( ((NumericParameter)parameters[0]).getValue());
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}
	
}

