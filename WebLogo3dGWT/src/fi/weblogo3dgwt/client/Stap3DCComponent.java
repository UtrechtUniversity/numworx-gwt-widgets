package fi.weblogo3dgwt.client;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import fi.weblogo3dgwt.client.parameters.NumericParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

/**
 * class representing the stap3d(dx,dy,dz) command: in the cursor plane move (dx,dy) 
 * and translate the cursor plane parallel to itself over dz 
 * move the current x-y-z coordinate system over (dx,dy,dz) in 3-space
 * see class TekenApplet3D; <br>
 */
public class Stap3DCComponent extends ParameterCommandComponent implements ParameterEditorListener
{
	
	public Stap3DCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, sv);
		
		commandName = WebLogo3dGWT.rb.stap3DTekst();
		commandNameTranslated = "step3d";
		noParameters = 3;
		parameters[0] = new NumericParameter();
		parameters[1] = new NumericParameter();
		parameters[2] = new NumericParameter();
	}

	protected void paintCommand(Context2d g)
	{
		if (xPos < JavaLogoSchuifVeld.ppx)
		{	
			g.setFont(WebLogo3dGWT.fontString);
			g.setFillStyle(CssColor.make(0,0,0));
			g.fillText(getCommandName() + strOpen + strClose, xPos+10, yPos+18);
		}
		else 
			super.paintCommand(g);
	}

	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{
		if (!(parameters[0].isCorrect(varSet) && parameters[1].isCorrect(varSet) && parameters[2].isCorrect(varSet))) 
			return false; 
		double valueX = ((NumericParameter)parameters[0]).getValue();
		double valueY = ((NumericParameter)parameters[1]).getValue();
		double valueZ = ((NumericParameter)parameters[2]).getValue();
		ub.stap(valueX, valueY,valueZ);
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if (traceKleur ) 
			trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}

}
