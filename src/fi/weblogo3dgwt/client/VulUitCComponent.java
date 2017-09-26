package fi.weblogo3dgwt.client;

import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.VarSet;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

public class VulUitCComponent extends SimpleCommandComponent
{
	public VulUitCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	super(x,y,b,h,sv);
		commandName = WebLogo3dGWT.rb.vulUitTekst(); //"vulUit";
		commandNameTranslated = "fillOff"; //JavaLogoWeb.rb.getString(commandName);
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