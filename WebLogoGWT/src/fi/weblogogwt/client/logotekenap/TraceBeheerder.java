package fi.weblogogwt.client.logotekenap;


import fi.weblogogwt.client.JavaLogoSchuifVeld;
import fi.weblogogwt.client.WebLogoGWT;
import fi.weblogogwt.client.VarSet;
import com.google.gwt.dom.client.Style;

/**
 * class implementing tracing through the current program; tracing is activated by clicking
 * traceAanKnop in class WebLogoGWT, which also activates the cursor in class TekenBlad;
 * clicking traceAanKnop activates traceUit-, begin-, start-, terug- and skipbuttons, 
 * a Label for displaying the current command and a CheckBox for tracing variables
 */
public class TraceBeheerder 
{
	/** 
	 * keeping track of program steps
	 */
	private int maxAantalStappen, aantalStappen;
	/**
	 * drawing area
	 */
	private Uitvoerblad tb;
	/**
	 * instance of JavaLogoSchuifVeld
	 */
	private JavaLogoSchuifVeld jlsveld;
	/** 
	 * flagg for tracing
	 */
	private boolean traceAan;
	
	/**
	 * flagg for tracing variables
	 */
	private boolean isVartracing = false;
	
	/**
	 * the current program level
	 */
	private int currentlevel;
	/** 
	 * flagg for skipping commands
	 */
	private boolean isSkipping;
	/**
	 * level at which commands are skipped
	 */
	private int skipLevel;

	/**
	 * class containing all steering buttons for tracing
	 */
	WebLogoGWT owner; 

	/**
	 * 
	 * @param tb drawing area
	 * @param v instance of JavaLogoSchuifVeld
	 * @param o instance of WebLogoGWT
	 */
	public TraceBeheerder(Uitvoerblad tb, JavaLogoSchuifVeld v, WebLogoGWT o)
	{	
		owner = o;
		aantalStappen = 0;
		maxAantalStappen = 0;
		isSkipping = false;
		skipLevel = 0;
		this.tb = tb;
		jlsveld = v;
		traceAan = false;
	}
	
	//-------------------------------------------------------------------------------------------
	// Execution and tracing of programs 
	//-------------------------------------------------------------------------------------------
	
	/**
	 * Will execute and paint a program without tracing
	 */
	public void executeProgram()
	{
		traceAan = false;
		jlsveld.execute(this, tb);
	}

	/**
	 * getter for traceAan
	 * @return traceAan
	 */
	public boolean isTraceAan()
	{
		return traceAan;
	}

	/**
	 * Will execute and paint a program when tracing, up to the number of steps
	 * indicated by the variable maxAantalStappen.
	 */
	public void traceProgram()
	{
		traceAan = true;
		aantalStappen = 0;
		if ( maxAantalStappen > 0 )
		{
			// this puts last command in methodeLabel in WebLogoGWT
			jlsveld.execute(this, tb);
		}
		// show traced CC in trace color pink
		jlsveld.paint();				
	}
	
	/**
	 * Callback method for execution of a program while tracing. CommandComponents will call
	 * this method when they have completed (or started: deeltaak, loop, if...)
	 * TraceBeheerder will signal the end of execution when maxAaantalStappen is reached.
	 * @param commandLevel level of the command
	 * @return	true, if execution of the program must stop at this point, false otherwise.
	 */
	public boolean commandExecuted(int commandLevel)
	{
		if (!traceAan ) 
			return false;
		aantalStappen++;
		if ( aantalStappen == maxAantalStappen )
		{
			if ( isSkipping )
			{	// check level of this command
				// lower than skipLevel, return to normal tracing
				if ( commandLevel < skipLevel )
				{	isSkipping = false;
					return true;
				} 
				else // in skipped block
				{	// new max after skipping this command, increase max here to make next command satisfy the first if									
					maxAantalStappen++;	
					return false;		
				}
			} 
			else // not skipping, stop at this command
			{	return true;
			}
		} 
		else
		{	return false;
		}
	}

	/**
	 * Set text of 'methodeVeld' and (if var tracing is on) the current variable set in the vartracer.
	 * This method will be called from the execute-methods in the CC's, when trace is on
	 * and execution stops at that command.
	 * @param actualCommand the actual command
	 * @param varset	the current set of variables in tracing mode
	 */
	public void setCommandInfo(String actualCommand, VarSet varset)
	{
		currentlevel = varset.getLevel();
		if (owner.methodeLabel != null)
			owner.methodeLabel.setText(actualCommand);
		if ( isVartracing )
		{
			owner.vartracer.setContent(varset.toString());
		}
	}
	
	//-------------------------------------------------------------------------------------------
	// button actions, buttons are property of and listened to in class WebLogoGWT 
	//-------------------------------------------------------------------------------------------
	/**
	 * action at clicking stapKnop in WebLogoGWT
	 */
	public void stapAction()
	{
		maxAantalStappen++;
		tb.paintDrawing(true);
	}
	/**
	 * action at clicking terugKnop in WebLogoGWT
	 */
	public void terugAction()
	{
		maxAantalStappen--;
		if (maxAantalStappen < 0)
			maxAantalStappen=0;
		tb.paintDrawing(true);
	}
	/**
	 * action at clicking skipKnop in WebLogoGWT
	 */
	public void skipAction()
	{
		skipLevel = currentlevel;
		isSkipping = true;
		tb.paintDrawing(true);
	}
	/**
	 * action at clicking beginKnop in WebLogoGWT
	 */
	public void beginAction()
	{	// previous trace may have stopped in skip
		isSkipping = false;					
		maxAantalStappen = 0;
		tb.paintDrawing(true);
	}
	/**
	 * action at clicking traceAanKnop in WebLogoGWT
	 */
	public void traceAanAction()
	{
		traceAan = true;
		isSkipping = false;
		maxAantalStappen = 0;
		tb.paintDrawing(true);
	}
	/**
	 * action at clicking traceAanKnop in WebLogoGWT
	 */
	public void traceUitAction()
	{
			traceAan = false;
			tb.paintDrawing(false);
			setVartracing(false);
	}
	
	/**
	 * enable/disable tracing of variables: add/remove the vartracer in JavaLogoSchuifVels  
	 * @param b enable/disable
	 */
	public void setVartracing(boolean b)
	{
		if ( b )
		{	isVartracing = true;
			jlsveld.add(owner.vartracer);
			jlsveld.setWidgetLeftWidth(owner.vartracer, JavaLogoSchuifVeld.ccx-1, Style.Unit.PX, owner.vartracerWidth, Style.Unit.PX);
			jlsveld.setWidgetTopHeight(owner.vartracer, JavaLogoSchuifVeld.ccy - 1, Style.Unit.PX, owner.vartracerHeight, Style.Unit.PX);
		} 
		else
		{
			isVartracing = false;
			jlsveld.remove(owner.vartracer);
			// remove, so that upon starting tracing variables, vartracer is empty
			owner.vartracer.setContent("");
		}
		jlsveld.paint();
	}
}