package fi.weblogo3dgwt.client;

import fi.weblogo3dgwt.client.parameters.NumericParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

/**
 * class representing the stapy(dy) command: in the cursor plane move dy 
 * relative to the direction of the cursor or equivalent: 
 * move the current x-y-z coordinate system dy in the x-y-plane
 * in the direction of the positive y-axis; see class TekenApplet3D; <br>
 * Note: not available as command block.
 */

public class StapYCComponent  extends ParameterCommandComponent
{
	public StapYCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		noParameters = 1;
		parameters[0] = new NumericParameter();
		commandName = WebLogo3dGWT.rb.stapYTekst();
		commandNameTranslated = "stepy";
	}
		
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	
		if (!parameters[0].isCorrect(varSet)) 
			return false; 
		ub.stapy(((NumericParameter) parameters[0]).getValue());
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if (traceKleur) 
			trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}
	
}
