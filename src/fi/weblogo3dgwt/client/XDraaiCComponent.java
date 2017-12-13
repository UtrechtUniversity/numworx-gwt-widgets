package fi.weblogo3dgwt.client;

import fi.weblogo3dgwt.client.parameters.NumericParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

/**
 * class representing the xdraai(ang) command: rotate the current 
 * x-y-z coordinate system ang degrees around the positive x-axis; 
 * see class TekenApplet3D; <br>
 */

public class XDraaiCComponent  extends ParameterCommandComponent
{
	public XDraaiCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		noParameters = 1;
		parameters[0] = new NumericParameter();
		commandName = WebLogo3dGWT.rb.xDraaiTekst();
		commandNameTranslated = "xturn";
	}
		
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	
		if (!parameters[0].isCorrect(varSet)) 
			return false; 
		ub.xdraai(((NumericParameter) parameters[0]).getValue());
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if (traceKleur) 
			trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}
	
}
