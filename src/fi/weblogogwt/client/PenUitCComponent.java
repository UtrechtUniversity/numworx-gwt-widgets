package fi.weblogogwt.client;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

public class PenUitCComponent extends SimpleCommandComponent
{
	
	public PenUitCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		commandName = "penUit";
		commandNameTranslated = "penOff"; //JavaLogoWeb.rb.getString(commandName);
	}
		
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	ub.penUit();
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if ( traceKleur ) trb.setCommandInfo(getCommandNameTranslated(), varSet);
		return traceKleur;
	}
	
}
