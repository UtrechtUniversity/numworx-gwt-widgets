package fi.weblogo3dgwt.client.logotekenap3d;


import fi.weblogo3dgwt.client.JavaLogoSchuifVeld;
import fi.weblogo3dgwt.client.WebLogo3dGWT;
import fi.weblogo3dgwt.client.VarSet;
import com.google.gwt.dom.client.Style;

/**
 * see class TraceBeheerder in WebLogoGWT
 */
public class TraceBeheerder 
{
	private int maxAantalStappen,aantalStappen;
	private TekenApplet3D tb;
	private JavaLogoSchuifVeld jlsveld;
	private boolean traceAan;
	private boolean isVartracing = false;
	
	private int currentlevel;
	private boolean isSkipping;
	private int skipLevel;
	
	WebLogo3dGWT owner; 
	
	public TraceBeheerder(TekenApplet3D tb, JavaLogoSchuifVeld v, WebLogo3dGWT o)
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
		// clear old tracekleur
		traceAan = true;
		aantalStappen = 0;
		if ( maxAantalStappen > 0 )
		{
			jlsveld.execute(this, tb);
		}
		jlsveld.paint();
	}
	
	/**
	 * Callback method for execution of a program while tracing. CommandComponents will call
	 * this method when they have completed (or started: deeltaak, loop, if...)
	 * TraceBeheerder will signal the end of execution when maxAaantalStappen is reached.
	 * @param commandLevel the command level
	 * @return	true, if execution of the program must stop at this point, false otherwise.
	 */
	public boolean commandExecuted(int commandLevel)
	{
		if ( !traceAan ) return false;
		aantalStappen++;
		if ( aantalStappen == maxAantalStappen )
		{
			if ( isSkipping )
			{											// check level of this command
				if ( commandLevel < skipLevel )
				{										// lower then skipLevel, return to normal tracing
					isSkipping = false;
					return true;
				} else
				{										// in skipped block				
					maxAantalStappen++;					// new max after skipping this command, increase max here
					return false;						// ... to make next command satify the first if
				}
			} else
			{											// not skipping, stop at this command
				return true;
			}
		} else
		{
			return false;
		}
	}

	/**
	 * Set text of 'methodeVeld' and (if var tracing is on) the varset in the vartracer.
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
	
	public void stapAction()
	{
		maxAantalStappen++;
		tb.paintDrawing(true);
	}
	public void terugAction()
	{
		maxAantalStappen--;
		if(maxAantalStappen<0)maxAantalStappen=0;
		tb.paintDrawing(true);
	}
	public void skipAction()
	{
		skipLevel = currentlevel;
		isSkipping = true;
		tb.paintDrawing(true);
	}
	public void beginAction()
	{
			isSkipping = false;					// previous trace may have stopped in skip
			maxAantalStappen = 0;
			tb.paintDrawing(true);
	}
	public void traceAanAction()
	{
		traceAan = true;
		isSkipping = false;
		maxAantalStappen = 0;
		tb.paintDrawing(true);
	}
	public void traceUitAction()
	{
		traceAan = false;
		tb.paintDrawing(false);
		setVartracing(false);
	}
	
	public void setVartracing(boolean b)
	{
		if ( b )
		{
			isVartracing = true;
			jlsveld.add(owner.vartracer);
			jlsveld.setWidgetLeftWidth(owner.vartracer, JavaLogoSchuifVeld.ccx-1, Style.Unit.PX, owner.vartracerWidth, Style.Unit.PX);
			jlsveld.setWidgetTopHeight(owner.vartracer, JavaLogoSchuifVeld.ccy - 1, Style.Unit.PX, owner.vartracerHeight, Style.Unit.PX);
			//vartracer.setBounds(ccx, ccy, 2*ccsw+10, 515);
			
		} else
		{
			isVartracing = false;
			jlsveld.remove(owner.vartracer);
			
			owner.vartracer.setContent("");
		}
		jlsveld.paint();
	}
}