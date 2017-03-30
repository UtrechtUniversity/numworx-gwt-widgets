package fi.weblogo3dgwt.client;

//import java.awt.Color;
//import java.awt.FontMetrics;
//import java.awt.Graphics;

import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.logotekenap3d.StringUtils;
import fi.weblogo3dgwt.client.expressies.BasisExpressie;
import fi.weblogo3dgwt.client.expressies.Expressie;
import fi.weblogo3dgwt.client.formuleobjects.FormuleParser;
import fi.weblogo3dgwt.client.parameters.NumericParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

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
		
		commandName = WebLogo3dGWT.rb.stapTekst(); //"stap";
		commandNameTranslated = "step"; //JavaLogoWeb.rb.getString(commandName);
		noParameters = 2;
		parameters[0] = new NumericParameter();
		parameters[1] = new NumericParameter();
	}

	@Override
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{
		if ( !(parameters[0].isCorrect(varSet) && parameters[1].isCorrect(varSet)) ) return false; 
		double valueX = ((NumericParameter)parameters[0]).getValue();
		double valueY = ((NumericParameter)parameters[1]).getValue();
		ub.stap(valueX, valueY);
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if ( traceKleur ) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}

}
