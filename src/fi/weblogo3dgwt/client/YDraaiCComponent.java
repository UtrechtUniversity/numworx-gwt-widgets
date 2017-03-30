package fi.weblogo3dgwt.client;

import fi.weblogo3dgwt.client.parameters.NumericParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

public class YDraaiCComponent  extends ParameterCommandComponent
{
	public YDraaiCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		noParameters = 1;
		parameters[0] = new NumericParameter();
		commandName = WebLogo3dGWT.rb.yDraaiTekst();
		commandNameTranslated = "yturn";
	}
		
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	
		if (!parameters[0].isCorrect(varSet)) 
			return false; 
		ub.ydraai(((NumericParameter) parameters[0]).getValue());
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) 
			trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}
	
}
