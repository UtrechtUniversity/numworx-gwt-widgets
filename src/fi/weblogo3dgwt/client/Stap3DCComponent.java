package fi.weblogo3dgwt.client;

//import java.awt.Color;
//import java.awt.Graphics;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;



import fi.weblogo3dgwt.client.parameters.NumericParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

/**
 * 
 * @author huub
 */
public class Stap3DCComponent extends ParameterCommandComponent implements ParameterEditorListener
{
	//protected int separatorX;
	//private boolean editingFirstParam;
	
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
	//protected void paintCommand(Graphics g)
	{
		//if (getParent() == schuifveld)
		if (xPos < JavaLogoSchuifVeld.ppx)
		{	
			g.setFont(WebLogo3dGWT.fontString);
			//g.setColor(Color.black);
			g.setFillStyle(CssColor.make(0,0,0));
			//g.drawString(getCommandNameTranslated() + strOpen + strClose, 10, 18);
			g.fillText(getCommandName() + strOpen + strClose, xPos+10, yPos+18);
		}
		else 
			super.paintCommand(g);
	}

	@Override
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
