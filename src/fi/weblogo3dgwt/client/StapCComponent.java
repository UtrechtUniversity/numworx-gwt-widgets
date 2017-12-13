package fi.weblogo3dgwt.client;


import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.parameters.NumericParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

/**
 * class representing the stap(dx,dy) command: in the cursor plane move (dx,dy) 
 * relative to the direction of the cursor or equivalent: 
 * move the current x-y-z coordinate system over (dx,dy) in the x-y-plane
 * see class TekenApplet3D; <br>
 */
public class StapCComponent extends ParameterCommandComponent implements ParameterEditorListener
{
	protected int separatorX;
	private boolean editingFirstParam;
	
	public StapCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, sv);
		
		commandName = WebLogo3dGWT.rb.stapTekst(); 
		commandNameTranslated = "step"; 
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
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}

}
