package fi.weblogogwt.client;


import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.parameters.NumericParameter;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

/**
 * class representing the stap(dx,dy) command: move (dx,dy) 
 * relative to the direction of the cursor; 
 * note how this works: the direction of the cursor defines the positive y-axis
 * of a coordinate system; in this coordinate system change the position by (dx,dy)
 * see class TekenBlad; <br>
 * the command has two parameters: Strings representing
 * the doubles dx and dy, see class NumericParameter   
 */
public class StapCComponent extends ParameterCommandComponent implements ParameterEditorListener
{
	/**
	 * constructor
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param sv instance of JavaLogoSchuifVeld sv containing the drawing Canvas; necessary for superclass constructor
	 */
	public StapCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, sv);
		
		commandName = WebLogoGWT.rb.stapTekst(); 
		commandNameTranslated = "step"; 
		noParameters = 2;
		parameters[0] = new NumericParameter();
		parameters[1] = new NumericParameter();
	}

	/**
	 * check the numeric parameters of this command for correctness;
	 * execute this command, if tracing, change its color and display
	 * the command and parameter; see class TraceBeheerder
	 */

	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{
		if (!(parameters[0].isCorrect(varSet) && parameters[1].isCorrect(varSet)) ) 
			return false; 
		double valueX = ((NumericParameter)parameters[0]).getValue();
		double valueY = ((NumericParameter)parameters[1]).getValue();
		ub.stap(valueX, valueY);
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if (traceKleur) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}

}
