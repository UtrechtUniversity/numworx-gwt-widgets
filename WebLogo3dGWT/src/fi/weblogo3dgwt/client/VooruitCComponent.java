package fi.weblogo3dgwt.client;

import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.parameters.NumericParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

/**
 * class implementing the vooruit(dis) command: in the cursor plane 
 * move distance dis in the direction in which the cursor is pointing
 * or equivalent move the current x-y-z coordinate system distance dis in the
 * direction of the positive y-axis; see class TekenApplet3D;
 * see also class VooruitCComponent in WebLogoGWT  
 */

public class VooruitCComponent extends ParameterCommandComponent
{
		
	public VooruitCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		noParameters = 1;
		parameters[0] = new NumericParameter();
		commandName = WebLogo3dGWT.rb.vooruitTekst(); 
		commandNameTranslated = "forward";
	}
	
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	
		if ( !parameters[0].isCorrect(varSet) ) 
			return false; 
		ub.vooruit( ((NumericParameter)parameters[0]).getValue() );
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}
		
}
