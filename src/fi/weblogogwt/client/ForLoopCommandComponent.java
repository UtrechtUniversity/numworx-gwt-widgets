package fi.weblogogwt.client;


import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.CommandComponent;
import fi.weblogogwt.client.VarSet;
import fi.weblogogwt.client.parameters.NumericParameter;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

/**
 * class implementing the for loop command;  
 */
public class ForLoopCommandComponent extends LoopCommandComponent
{
	/**
	 * constructor
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param sv instance of JavaLogoSchuifVeld for drawing
	 */
	public ForLoopCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, sv);
		loopCondition = new NumericParameter();
		commandName = WebLogoGWT.rb.herhaal1Tekst(); 
		commandNameTranslated = "Repeat"; 
		naString = " " + WebLogoGWT.rb.keerTekst(); 
		naStringTranslated = " times"; 
	}

	/**
	 * check correctness of loop condition, inform traceBeheerder that a loop has started 
	 * and execute the code in loopBlock multiple times as specified in loop condition 
	 */
	public boolean executeContent(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		if ( !loopCondition.isCorrect(varSet) ) 
		{	return false;
		}
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) 
		{
			trb.setCommandInfo(getCommandName()+" "+loopCondition.getValueText()+" "+naString, varSet);
			return traceKleur;
		}
		for (int i = 0 ; i < (int) ((NumericParameter)loopCondition).getValue() ; i++)
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
