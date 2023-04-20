package fi.weblogogwt.client;


import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.parameters.ColorParameter;
import fi.weblogogwt.client.formuleobjects.StringUtils;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

import com.google.gwt.canvas.dom.client.CssColor;

/**
 * class representing the penAan(color) command: put the pen on the paper in the
 * drawing area, i.e. if making moves, connect begin and end positions
 * with a line; see class TekenBlad; <br>
 * the command has one parameter: a String representing an RGB-color,
 * or a String representing the name of a color, see class
 * ColorParameter   
 */

public class PenAanCComponent extends ParameterCommandComponent
{
	/**
	 * constructor
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param sv instance of JavaLogoSchuifVeld sv containing the drawing Canvas; necessary for superclass constructor
	 */
	
	public PenAanCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		noParameters = 1;
		parameters[0] = new ColorParameter();
		commandName = WebLogoGWT.rb.penAanTekst(); 
		commandNameTranslated = "penOn"; ;
	}
	
	/**
	 * check the color parameter of this command for correctness; 
	 * execute this command, if tracing, change its color and display
	 * the command and parameter; see class TraceBeheerder
	 */

	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		if (!parameters[0].isCorrect(varSet)) 
			return false; 
		CssColor cl = ((ColorParameter)parameters[0]).getColor();
		
		String clString = cl.toString().substring(4, cl.toString().length() - 1);
		String[] kleurenStr = StringUtils.split(clString,",");

		int clBlue =  Integer.parseInt(kleurenStr[2]);
		int clGreen = Integer.parseInt(kleurenStr[1]);
		int clRed =   Integer.parseInt(kleurenStr[0]);

		ub.penAan(clRed, clGreen, clBlue);
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}
	
}
