package fi.weblogogwt.client;

//import java.awt.Color;
//import java.awt.FontMetrics;
//import java.awt.Graphics;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.formuleobjects.StringUtils;
import fi.weblogogwt.client.expressies.BasisExpressie;
import fi.weblogogwt.client.expressies.Expressie;
import fi.weblogogwt.client.formuleobjects.FormuleParser;
import fi.weblogogwt.client.parameters.NumericParameter;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

/**
 * 
 * @author berge020
 */
public class StapCComponent extends ParameterCommandComponent implements ParameterEditorListener
{
	protected int separatorX;
	private boolean editingFirstParam;
	
	public StapCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, sv);
		
		commandName = WebLogoGWT.rb.stapTekst(); //"stap";
		commandNameTranslated = "step"; //JavaLogoWeb.rb.getString(commandName);
		noParameters = 2;
		parameters[0] = new NumericParameter();
		parameters[1] = new NumericParameter();
	}

	@Override
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{
		if ( !(parameters[0].isCorrect(varSet) && parameters[1].isCorrect(varSet)) ) return false; 
		double valueX = ((NumericParameter)parameters[0]).getValue();
		double valueY = ((NumericParameter)parameters[1]).getValue();
		ub.stap(valueX, valueY);
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}

}
