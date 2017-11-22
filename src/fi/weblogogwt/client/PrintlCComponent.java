package fi.weblogogwt.client;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;
import fi.weblogogwt.client.parameters.TextParameter;

/**
 * class representing the println(arg) command, where arg can be
 * 1) a piece of text enclosed in quotes or
 * 2) a valid variable name in which case its value will be printed
 * see class TekenBlad; <br>
 * the command has one parameter: a String representing arg
 * see class TextParameter   
 */

public class PrintlCComponent extends ParameterCommandComponent
{
	/**
	 * constructor
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param sv instance of JavaLogoSchuifVeld sv containing the drawing Canvas; necessary for superclass constructor
	 */
	public PrintlCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		noParameters = 1;
		parameters[0] = new TextParameter();
		commandName = WebLogoGWT.rb.printlnTekst(); //"println";
		commandNameTranslated = "println"; //JavaLogoWeb.rb.getString(commandName);
	}
	/**
	 * check the text parameter of this command for correctness;
	 * execute this command, if tracing, change its color and display
	 * the command and parameter; see class TraceBeheerder
	 */
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		if ( !parameters[0].isCorrect(varSet) ) return false; 
		ub.printl( ((TextParameter)parameters[0]).getValueText());
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}	
}