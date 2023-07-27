package fi.weblogo3dgwt.client;

import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.VarSet;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

/**
 * the command represented by this class has the following effect (see class TekenBlad3D)
 * after executing the command vulAan() (see class vulAanComponent) 
 * all moves in the drawing area are considered as the sides of a 3d-polygon;
 * at vulUit this 3d-polygon is created and filled with the color specified in the last vulAan()
 * command   
 */

public class VulUitCComponent extends SimpleCommandComponent
{
	public VulUitCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	super(x,y,b,h,sv);
		commandName = WebLogo3dGWT.rb.vulUitTekst(); 
		commandNameTranslated = "fillOff"; 
	}
	
	@Override
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	
		ub.vulUit();
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) trb.setCommandInfo(getCommandName(), varSet);
		return traceKleur;
	}
	
}