package fi.weblogo3dgwt.client;

//import java.awt.Component;

//import java.awt.Component;

import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.CommandComponent;
import fi.weblogo3dgwt.client.WebLogo3dGWT;
import fi.weblogo3dgwt.client.VarSet;
import fi.weblogo3dgwt.client.parameters.BooleanParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

public class WhileLoopCommandComponent extends LoopCommandComponent
{

	public WhileLoopCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, sv);
		
		loopCondition = new BooleanParameter();
		commandName = WebLogo3dGWT.rb.zolangTekst(); //"Zolang";
		commandNameTranslated = "While"; //JavaLogoWeb.rb.getString(commandName);
		naString = " " + WebLogo3dGWT.rb.herhaal2Tekst(); //" herhaal";
		naStringTranslated = " repeat"; //JavaLogoWeb.rb.getString(naString);
		createLoopEditor();
	}

	@Override
	public boolean executeContent(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	
//System.out.println("wlCC executeContent");		
		
		// ToDo: Find a nicer way to get out of a neverending loop. Right now, just maximize the number of
		// cycles to 100. If we were to leave this out, tekenapplet will 'hang' indefinitely.
		int loopcount = 0;
		if ( !loopCondition.isCorrect(varSet) ) return false; 
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) 
		{
			trb.setCommandInfo(getCommandNameTranslated()+" "+loopCondition.getValueText(), varSet);
			return traceKleur;
		}
		boolean conditionValue = ((BooleanParameter)loopCondition).getValue();
		while ( conditionValue && loopcount < 100)
		{	
			loopcount++;
			for(int j=0 ; j<loopBlock.getComponentCount() ; j++)
			{	
				Object c = loopBlock.getComponent(j);
				if(c instanceof CommandComponent)
				{	boolean tracekleur = ((CommandComponent)c).execute(trb, ub, varSet);
					if(tracekleur) return true;
				}
			}
			// Re-evaluate the condition using the isCorrect(VarSet) method. Note that the condition
			// may have become incorrect due to changes in variables (division by zero, or ...)
			if ( !loopCondition.isCorrect(varSet) ) return false; 
			conditionValue = ((BooleanParameter)loopCondition).getValue();
		}
		if ( loopcount >= 100 )
		{
			ub.print("loopexcess");
		}
		return false;
	}
}
