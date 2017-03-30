package fi.weblogo3dgwt.client;

import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.parameters.TextParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

public class PrintCComponent extends ParameterCommandComponent
{
	
	public PrintCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		noParameters = 1;
		parameters[0] = new TextParameter();
		commandName = WebLogo3dGWT.rb.printTekst(); //"print";
		commandNameTranslated = "print"; //JavaLogoWeb.rb.getString(commandName);
	}
	
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	
		if ( !parameters[0].isCorrect(varSet) ) 
		{
//System.out.println("!correct");			
			return false;
		}	
		ub.print( ((TextParameter)parameters[0]).getValueText());
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if ( traceKleur ) trb.setCommandInfo(getActualCall(), varSet);
		return traceKleur;
	}	
}