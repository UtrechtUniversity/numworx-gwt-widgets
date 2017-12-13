package fi.weblogo3dgwt.client;



import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.parameters.NumericParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * CC for a 'deeltaak call' so this is not the deeltaak itself but the little block you
 * can drag around to drop in your algorithm
 * see also class DeeltaakCallCComponent in WebLogoGWT
 * @author Berge020
 */
public class DeeltaakCallCComponent extends ParameterCommandComponent
{
	private DeeltaakBodyComponent deeltaakBody;
	
	/**
	 * Creates a new DeeltaakCallCComponent witn zero parameters (typically called at startup)
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param index index number
	 * @param sv instance of JavaLogoSchuifVeld
	 */
	public DeeltaakCallCComponent(int x, int y, int b, int h, int index, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		String commandNameBase = WebLogo3dGWT.rb.deeltaakTekst(); //"deeltaak";
		commandName = commandNameBase+index;
		commandNameTranslated = "subroutine" + index; //JavaLogoWeb.rb.getString(commandNameBase)+index;
		noParameters = 0;
		// no explicit construction of body. This is done separately in JavaLogoSchuifVeld.initialize()
		// and ProgrammaImporter, because in the latter case it's 'names first, bodies later'
	}
	
	/**
	 * Creates a DCC from another one, with the right number of parameters
	 * Typically used when picking up a DDC from the 'pile'
	 * @param dcc DeeltaakCallCComponent to be copied
	 * @param sv instance of JavaLogoSchuifVeld
	 */
	public DeeltaakCallCComponent(DeeltaakCallCComponent dcc, JavaLogoSchuifVeld sv)
	{
		super(dcc.getX(), dcc.getY(), dcc.getWidth(), dcc.getHeight(), sv);
		commandName = dcc.getCommandName();
		commandNameTranslated = dcc.getCommandNameTranslated();
		
		deeltaakBody = dcc.getBody();
		// noParameters will be zero after call of constructor of ParameterCC.
		checkParameterList();
	}
	
	
	public DeeltaakBodyComponent getBody()
	{	
		return deeltaakBody;
	}
	
	
	// vanuit Importer..
	public void setBody(DeeltaakBodyComponent pc)
	{	
		deeltaakBody = pc;
		checkParameterList();
	}
	
	/**
	 * Gets the command name from the DeeltaakBodyComponent, since it is there that the user
	 * can change the name of a Deeltaak.
	 * Name may not yet created (import). In that case, return the default name 'deeltaaki'.
	 * Note: also return incorrect name for editing.
	 * @return		the name of the deeltaak
	 */
	public String getCommandName()
	{
		
		if ( deeltaakBody != null )
		{
			return deeltaakBody.getProgramName();
		} 
		return commandName ;		
	}
	
	public String getCommandNameTranslated()
	{	
		
		if ( deeltaakBody != null )
		{
			return deeltaakBody.getProgramName();
		} 
		return commandNameTranslated ;		
	}
	
	protected boolean isCorrect()
	{
		
		if ( !deeltaakBody.isHeaderValid() ) 
			return false;
		return super.isCorrect();
	}
	
	protected String getFullParameterText()
	{
		checkParameterList();						// this implies checking for updates at every repaint
		return super.getFullParameterText();
	}
	
	protected void paintBackground(Context2d g)
	{
		super.paintBackground(g);
		g.strokeRect(xPos+5,yPos+1,getSize().width-11,getSize().height-1);
		g.strokeRect(xPos+6,yPos+1,getSize().width-13,getSize().height-3);
	}
	
	private void checkParameterList()
	{

		int np = deeltaakBody.getParameterCount();
		if ( np > noParameters )
		{
			for ( int i=noParameters; i<np; i++ )
			{
				parameters[i] = new NumericParameter();
			}
		}
		noParameters = np;
	}

	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	
		
		// don't run a deeltaak with invalid name
		if ( !deeltaakBody.isHeaderValid() ) 
			return false;
		
		// don't start execution if any of the parameters is incorrect with the given VarSet
		for ( int i=0; i<noParameters; i++ )
		{
			if ( !parameters[i].isCorrect(varSet) ) return false;
		}

		varSet.increaseLevel("-- "+getActualCall(), true);
		for ( int i=0; i<noParameters; i++ )
		{
			// get parameter name from body and add local var with value of parameter (call by value)
			varSet.setParameter(deeltaakBody.getParameterName(i), ((NumericParameter)parameters[i]).getValue());		
		}
		
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if ( traceKleur ) 
		{	
			trb.setCommandInfo(getActualCall(), varSet);
			return traceKleur;
		}
		// execution may also stop at a command in the body. Call will be pink then, too.
		traceKleur = deeltaakBody.execute(trb, ub, varSet);
		varSet.decreaseLevel();
		return traceKleur;
	}
	
}