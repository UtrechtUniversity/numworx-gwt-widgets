package fi.weblogogwt.client;

//import java.awt.Component;

//import java.awt.Component;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.CommandComponent;
import fi.weblogogwt.client.VarSet;
import fi.weblogogwt.client.parameters.NumericParameter;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

public class ForLoopCommandComponent extends LoopCommandComponent
{

	public ForLoopCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, sv);
		
		loopCondition = new NumericParameter();
		commandName = "Herhaal";
		commandNameTranslated = "Repeat"; //JavaLogoWeb.rb.getString(commandName);
		naString = " keer";
		naStringTranslated = " times"; //JavaLogoWeb.rb.getString(naString);
		createLoopEditor();
	}

	@Override
	public boolean executeContent(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		
//System.out.println("flCC executeContent");

		if ( !loopCondition.isCorrect(varSet) ) return false; 
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if ( traceKleur ) 
		{
			trb.setCommandInfo(getCommandName()+" "+loopCondition.getValueText()+" "+naString, varSet);
			return traceKleur;
		}
		for(int i=0 ; i<(int) ((NumericParameter)loopCondition).getValue() ; i++)
		{	
			for(int j=0 ; j<loopBlock.getComponentCount() ; j++)
			{	
				Object c = loopBlock.getComponent(j);
				if(c instanceof CommandComponent)
				{	boolean tracekleur = ((CommandComponent)c).execute(trb, ub, varSet);
					if(tracekleur) return true;
				}
			}
		}
		return false;
	}
	

}
