package fi.weblogogwt.client;

//import java.awt.Color;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.parameters.ColorParameter;
import fi.weblogogwt.client.formuleobjects.StringUtils;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

import com.google.gwt.canvas.dom.client.CssColor;

public class PenAanCComponent extends ParameterCommandComponent
{
	
	public PenAanCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		noParameters = 1;
		parameters[0] = new ColorParameter();
		commandName = WebLogoGWT.rb.penAanTekst(); //"penAan";
		commandNameTranslated = "penOn"; //JavaLogoWeb.rb.getString(commandName);
	}
	
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		if ( !parameters[0].isCorrect(varSet) ) return false; 
		CssColor cl = ((ColorParameter)parameters[0]).getColor();
		
		String clString = cl.toString().substring(4, cl.toString().length() - 1);
		String[] kleurenStr = StringUtils.split(clString,",");

		int clBlue =  Integer.parseInt(kleurenStr[2]);
		int clGreen = Integer.parseInt(kleurenStr[1]);
		int clRed =   Integer.parseInt(kleurenStr[0]);

		ub.penAan(clRed, clGreen, clBlue);
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if ( traceKleur ) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}
	
}
