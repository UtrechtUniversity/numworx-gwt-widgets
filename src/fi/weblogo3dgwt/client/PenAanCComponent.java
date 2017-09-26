package fi.weblogo3dgwt.client;

//import java.awt.Color;

import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.parameters.ColorParameter;
import fi.weblogo3dgwt.client.logotekenap3d.StringUtils;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

import com.google.gwt.canvas.dom.client.CssColor;

public class PenAanCComponent extends ParameterCommandComponent
{
	
	public PenAanCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		noParameters = 1;
		parameters[0] = new ColorParameter();
		commandName = WebLogo3dGWT.rb.penAanTekst(); //"penAan";
		commandNameTranslated = "penOn"; //JavaLogoWeb.rb.getString(commandName);
	}
	
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
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
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}
	
}
