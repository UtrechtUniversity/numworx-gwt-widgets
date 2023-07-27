package fi.weblogo3dgwt.client;

import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.parameters.NumericParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

/**
 * class representing the links(ang) command: in the cursor plane, 
 * turn the cursor left over ang degrees relative to the direction 
 * to which the cursor is pointing or equivalent rotate the current 
 * x-y-z coordinate system ang degrees anti-clockwise around 
 * the positive z-axis; see class TekenApplet3D; <br>
 */

public class LinksCComponent  extends ParameterCommandComponent
{
	public LinksCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		noParameters = 1;
		parameters[0] = new NumericParameter();
		commandName = WebLogo3dGWT.rb.linksTekst(); 
		commandNameTranslated = "left"; 
	}
		
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	
		if ( !parameters[0].isCorrect(varSet) ) return false; 
		ub.links( ((NumericParameter)parameters[0]).getValue());
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}
	
}

