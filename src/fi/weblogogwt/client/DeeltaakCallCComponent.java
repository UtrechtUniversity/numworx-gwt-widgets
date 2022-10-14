package fi.weblogogwt.client;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.parameters.NumericParameter;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * CommmandComponent implementing a call to a 'deeltaak' (subroutine); this is not the subroutine itself but 
 * a little block you can drag around and drop in your program (or another subroutine) where it will call
 * the actual subroutine (see class DeeltaakBodyComponent); note that DeeltaakBodyComponent has name and (optional)
 * one or more parameters, which will also be shown on this DeelTaalCC 
 * @author Berge020
 */
public class DeeltaakCallCComponent extends ParameterCommandComponent
{
	/**
	 * the CC implementing the body of the subroutine
	 */
	private DeeltaakBodyComponent deeltaakBody;
	
	/**
	 * Creates a new DeeltaakCallCComponent with zero parameters (typically called at startup);
	 * the constructor does not explicitly create the subroutine body: this is done separately in JavaLogoSchuifVeld.initialize()
	 * and ProgrammaImporter, because in the latter case it's 'names first, bodies later'
	 * @param x x-location
	 * @param y y-location
	 * @param b width
	 * @param h height 
	 * @param index index of DeeltaakCallCComponent
	 * @param sv the JavaLogoSchuifVeld
	 */
	public DeeltaakCallCComponent(int x, int y, int b, int h, int index, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		String commandNameBase = WebLogoGWT.rb.deeltaakTekst(); 
		commandName = commandNameBase+index;
		commandNameTranslated = "subroutine" + index; 
		noParameters = 0;
	}
	
	/**
	 * Creates a DCC from another one, with the right number of parameters
	 * Typically used when picking up a DDC from the 'pile' and adding a new one to the 'pile'
	 * @param dcc a DeeltaakCallCComponent
	 * @param sv the JavaLogoSchuifVeld
	 */
	public DeeltaakCallCComponent(DeeltaakCallCComponent dcc, JavaLogoSchuifVeld sv)
	{
		super(dcc.getX(), dcc.getY(), dcc.getWidth(), dcc.getHeight(), sv);
		commandName = dcc.getCommandName();
		commandNameTranslated = dcc.getCommandNameTranslated();
		deeltaakBody = dcc.getBody();
		// synchronize number of parameters with deeltaakBody 
		checkParameterList();
	}
	
	/**
	 * getter for the corresponding DeeltaakBodyComponent 
	 * @return deeltaakBody
	 */
	public DeeltaakBodyComponent getBody()
	{	
		return deeltaakBody;
	}
	
	/**
	 * getter for the corresponding DeeltaakBodyComponent
	 * used by ProgrammaImporter
	 * @param pc the DeeltaakBodyComponent
	 */
	public void setBody(DeeltaakBodyComponent pc)
	{	
		deeltaakBody = pc;
		checkParameterList();
	}
	
	/**
	 * Gets the command name from the DeeltaakBodyComponent, since it is there that the user
	 * can change the name of a Deeltaak.
	 * Name may not yet have been created (import). In that case, return the default name 'deeltaaki'.
	 * Note: also return incorrect name for editing.
	 * @return		the name of the deeltaak
	 */
	public String getCommandName()
	{	if ( deeltaakBody != null )
		{	return deeltaakBody.getProgramName();
		} 
		return commandName;		
	}

	/** 
	 * similar to getCommandName(), not used
	 */
	public String getCommandNameTranslated()
	{	if ( deeltaakBody != null )
		{	return deeltaakBody.getProgramName();
		} 
		return commandNameTranslated;		
	}
	
	/**
	 * check correctness of subroutine name and 
	 * subroutine parameters
	 */
	protected boolean isCorrect()
	{	if ( !deeltaakBody.isHeaderValid() ) 
			return false;
		return super.isCorrect();
	}
	
	/** 
	 * update parameters with the deeltaakBody and list them
	 * in a String
	 */
	protected String getFullParameterText()
	{	// this implies checking for updates at every repaint
		checkParameterList();						
		return super.getFullParameterText();
	}

	/**
	 * paint background (depends on tracing) and outline
	 */
	protected void paintBackground(Context2d g)
	{
		super.paintBackground(g);
		g.strokeRect(xPos+5,yPos,getSize().width-11,getSize().height-1);
		//g.strokeRect(xPos+6,yPos+1,getSize().width-13,getSize().height-3);
	}

	/**
	 * add parameters to this DCC until the number of parameters equals that of
	 * the deeltaakBody
	 */
	private void checkParameterList()
	{
		int np = deeltaakBody.getParameterCount();
		if ( np > noParameters )
		{	for ( int i=noParameters; i<np; i++ )
			{	parameters[i] = new NumericParameter();
			}
		}
		noParameters = np;
	}

	/**
	 * check header and parameters of deeltaakBody for correctness; <br>
	 * increase the level in varSet and add local parameters to varSet; <br>
	 * execute deeltaakBody
	 */
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		// don't run a deeltaak with invalid name
		if ( !deeltaakBody.isHeaderValid() ) 
			return false;
		// don't start execution if any of the parameters is incorrect with the given VarSet
		for ( int i=0; i<noParameters; i++ )
		{	if ( !parameters[i].isCorrect(varSet) ) 
			return false;
		}

		// increaseLevel in varSet
		varSet.increaseLevel("-- "+getActualCall(), true);
		// for each parameter, get the parameter name from the subroutine body and add 
		// a local variable to varSet with value of parameter (call by value)
		for ( int i=0; i<noParameters; i++ )
		{	varSet.setParameter(deeltaakBody.getParameterName(i), ((NumericParameter)parameters[i]).getValue());		
		}
		
		// inform TraceBeheerder of start of deeltaak
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if ( traceKleur ) 
		{	trb.setCommandInfo(getActualCall(), varSet);
			return traceKleur;
		}
		// execution may also stop at a command in the body. Call will be pink then, too.
		traceKleur = deeltaakBody.execute(trb, ub, varSet);
		if (traceKleur) traceKleurCnt = 0;
		varSet.decreaseLevel();
		return traceKleur;
	}
	
}