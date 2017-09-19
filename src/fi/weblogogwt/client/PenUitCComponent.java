package fi.weblogogwt.client;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

public class PenUitCComponent extends SimpleCommandComponent
{
	
	public PenUitCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		commandName = WebLogoGWT.rb.penUitTekst(); //"penUit";
		commandNameTranslated = "penOff"; //JavaLogoWeb.rb.getString(commandName);
	}
		
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	ub.penUit();
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) trb.setCommandInfo(getCommandName(), varSet);
		return traceKleur;
	}
	
}
