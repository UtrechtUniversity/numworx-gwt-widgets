package fi.weblogogwt.client;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;
import fi.weblogogwt.client.expressies.*;
import fi.weblogogwt.client.parameters.TextParameter;

public class PrintlCComponent extends ParameterCommandComponent
{
	
	public PrintlCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		noParameters = 1;
		parameters[0] = new TextParameter();
		commandName = WebLogoGWT.rb.printlnTekst(); //"println";
		commandNameTranslated = "println"; //JavaLogoWeb.rb.getString(commandName);
	}
	
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		if ( !parameters[0].isCorrect(varSet) ) return false; 
		ub.printl( ((TextParameter)parameters[0]).getValueText());
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}	
}