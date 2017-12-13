package fi.weblogo3dgwt.client;


import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.CommandComponent;
import fi.weblogo3dgwt.client.VarSet;
import fi.weblogo3dgwt.client.parameters.NumericParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

/**
 * see class ForLoopCommandComponent in WebLogoGWT
 */
public class ForLoopCommandComponent extends LoopCommandComponent
{

	public ForLoopCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, sv);
		
		loopCondition = new NumericParameter();
		commandName = WebLogo3dGWT.rb.herhaal1Tekst(); 
		commandNameTranslated = "Repeat"; 
		naString = " " + WebLogo3dGWT.rb.keerTekst(); 
		naStringTranslated = " times"; 
	}

	public boolean executeContent(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	

		if ( !loopCondition.isCorrect(varSet) ) return false; 
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
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
