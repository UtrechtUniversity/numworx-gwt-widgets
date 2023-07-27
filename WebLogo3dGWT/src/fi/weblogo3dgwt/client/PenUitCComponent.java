package fi.weblogo3dgwt.client;

import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

/**
 * see class PenUitCComponent in WebLogoGWT 
 */

public class PenUitCComponent extends SimpleCommandComponent
{
	
	public PenUitCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		commandName = WebLogo3dGWT.rb.penUitTekst(); 
		commandNameTranslated = "penOff"; 
	}
		
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	ub.penUit();
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) trb.setCommandInfo(getCommandName(), varSet);
		return traceKleur;
	}
	
}
