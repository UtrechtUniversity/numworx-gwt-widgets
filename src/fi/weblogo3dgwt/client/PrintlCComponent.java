package fi.weblogo3dgwt.client;

import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;
import fi.weblogo3dgwt.client.parameters.TextParameter;

/**
 * not implemented as command block, see class PrintlCComponent in WebLogoGWT 
 */

public class PrintlCComponent extends ParameterCommandComponent
{
	
	public PrintlCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		noParameters = 1;
		parameters[0] = new TextParameter();
		commandName = WebLogo3dGWT.rb.printlnTekst(); 
		commandNameTranslated = "println"; 
	}
	
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	
		if ( !parameters[0].isCorrect(varSet) ) return false; 
		ub.printl( ((TextParameter)parameters[0]).getValueText());
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}	
}