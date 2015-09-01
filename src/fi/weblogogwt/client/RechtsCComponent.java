package fi.weblogogwt.client;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.parameters.NumericParameter;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

public class RechtsCComponent extends ParameterCommandComponent
{
	public RechtsCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, sv);
		noParameters = 1;
		parameters[0] = new NumericParameter();
		commandName = "rechts";
		commandNameTranslated = "right"; //JavaLogoWeb.rb.getString(commandName);
	}

	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{
		if ( !parameters[0].isCorrect(varSet) ) return false; 
		ub.rechts( ((NumericParameter)parameters[0]).getValue() );
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if ( traceKleur ) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}
}