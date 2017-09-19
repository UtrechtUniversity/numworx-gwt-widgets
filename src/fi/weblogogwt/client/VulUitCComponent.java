package fi.weblogogwt.client;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.VarSet;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

public class VulUitCComponent extends SimpleCommandComponent
{
	public VulUitCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	super(x,y,b,h,sv);
		commandName = WebLogoGWT.rb.vulUitTekst(); //"vulUit";
		commandNameTranslated = "fillOff"; //JavaLogoWeb.rb.getString(commandName);
	}
	
	@Override
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		ub.vulUit();
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if (traceKleur) trb.setCommandInfo(getCommandName(), varSet);
		return traceKleur;
	}
	
}