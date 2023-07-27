package fi.weblogo3dgwt.client;


import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;
import fi.weblogo3dgwt.client.logotekenap3d.Polygon;
import fi.weblogo3dgwt.client.CommandComponent;
import fi.weblogo3dgwt.client.WebLogo3dGWT;
import fi.weblogo3dgwt.client.VarSet;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;

/**
 * see class ProgrammaComponent in WebLogoGWT
 */

public class ProgrammaComponent extends CompositeCommandComponent 
{	
	protected String defaultName = "";
	protected CommandContainer commandBlock;
	
	private Polygon arrowOut;
	private Polygon arrowIn;
	
	/**
	 * Height of header (name+buttons)
	 */
	public static final int headerHeight = 25;
	
	/**
	 * ProgrammaComponent Small Width: width when PC is narrow
	 */
	public static final int pcsw = 190; //180;
	
	/**
	 * ProgrammaComponent Large Width: width when PC is made wide
	 */
	public static final int pclw = 320;
	
	/**
	 * ProgrammaComponent height when closed
	 */
	public static final int pcclosedh = headerHeight+10;
	
	/**
	 * ProgrammaComponent minimum open height 
	 */
	public static final int pcminoh = 160;
	
	/**
	 * ProgrammaComponent maximum open height 
	 */
	public static final int pcmaxoh = 320;
	
	/**
	 * Indicates if the user can change te height of this PC. False for the main algorithm, true for deeltaken.
	 * Note: we handle height changes in this class, rather than in DeeltaakBody, to concentrate all associated
	 * mouse handling in one class...
	 */
	protected boolean isHeightFixed = true;
	
	/**
	 * indicates if this component has been collapsed to it's header 
	 */
	protected boolean isOpen = false;
	
	/**
	 * 
	 */
	private boolean isWide;
	/**
	 * narrowX remembers the original xpos of the PC, when the PC is made wide, because
	 * generally the PC will have to move left to fit inside the ProgrammaPanel
	 */
	private int narrowX;
	
	/**
	 * previousX and -Y remember the old location of the component when it is dragged.
	 * When the user drags the PC outside the programmaPanel, it is simply put back
	 */
	protected int previousX;
	protected int previousY;
	
	boolean widthIsChangable = false;
	
	public ProgrammaComponent(int x, int y, int b, int h, String pn, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		defaultName = pn;
		isStapel = false;
		isWide = false;
		narrowX = x;
		
		widthIsChangable = schuifveld.breedte == WebLogo3dGWT.jlsBreedteGroot;
		
		commandName = defaultName;
		
		commandBlock = new CommandContainer(xPos+0, yPos+headerHeight, b, h-headerHeight, this);

		commandBlock.parent = this;
		commandBlock.containerName = "program";
		
		createHArrows();
	}
	
	void clearProgram()
	{
		commandBlock.removeAll();
	}
	
	/**
	 * Gets the name of this ProgrammaComponent.
	 * In the base class this is the default name. Subclasses must override to return the name
	 * typed by the user from their IdentifierParameter.
	 * @return	the default name for this ProgrammaComponent
	 */
	public String getProgramName()
	{
		return defaultName;
	}

	public void addCComponent(CommandComponent cc)
	{	
		commandBlock.addCCompAtBottom(cc);
	}
	
	/**
	 * PC's don't resize when user drops or removes CC's. Resizing is left to the user,
	 * when the containers get too big, the scroll wheel will work.
	 */
	public void setLocation(int x, int y)
	{
		xPos = x; yPos = y;
		if (commandBlock != null)
		{
			commandBlock.setLocation(xPos, yPos+headerHeight);
		}
		createHArrows();
	}

	
	void containerHeightChanged(int h)
	{	}
	
	/**
	 * This method also adjusts the sizes of the CommandContainer
	 * @see java.awt.Component#setSize(int, int)
	 */
	@Override
	public void setSize(int w, int h)
	{
		
		super.setSize(w, h);
		commandBlock.setSize(commandBlock.getWidth(), h-headerHeight);
		commandBlock.setLocation(xPos, yPos+headerHeight);
		commandBlock.setWidth(w);
	}
	
	/**
	 * Change the width when arrow in header is clicked: alternate between two fixed widths
	 */
	private void changeWidth()
	{
		if ( isWide )
		{
			setSize(pcsw, getHeight());
			isWide = false;
		} 
		else
		{
			setSize(pclw, getHeight());
			narrowX = getX();
			isWide = true;
		}
	}
	
	void changeHeight()
	{
		if ( isHeightFixed ) 
			return;
		int newh;
		if ( isOpen )
		{
			newh = pcclosedh;
			commandBlock.componentsVisible = false;
		} 
		else
		{
			newh = Math.min(schuifveld.getHeight()-20, Math.max(pcminoh, commandBlock.getContentHeight()+headerHeight+20));
			commandBlock.componentsVisible = true;
		}
		isOpen = !isOpen;
		setSize(getWidth(), newh);
		setLocation(getX(), Math.min(getY(), Math.max(0,JavaLogoSchuifVeld.pph-newh)));
	}
	
	private void createHArrows()
	{
		arrowOut = new Polygon();
		arrowOut.addPoint(xPos+pcsw-14, yPos+7);
		arrowOut.addPoint(xPos+pcsw-14, yPos+19);
		arrowOut.addPoint(xPos+pcsw-6, yPos+13);
		arrowIn = new Polygon();
		arrowIn.addPoint(xPos+pclw-6, yPos+7);
		arrowIn.addPoint(xPos+pclw-6, yPos+19);
		arrowIn.addPoint(xPos+pclw-14, yPos+13);
	}
	
	/**
	 * Cannot set caret on a ProgrammaComponent, because it is the one and only CC that's not in a
	 * CommandContainer, it's the root of the tree!
	 */
	public void setCaret(int y)
	{  }

	protected void paintBackground(Context2d g)
	{
		g.setFillStyle(CssColor.make(187,221,255));
		g.fillRect(xPos+1,yPos+1,getWidth()-1,headerHeight-1);
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.strokeRect(xPos+0,yPos+0,getWidth()-1,headerHeight);
		g.strokeRect(xPos+1,yPos+1,getWidth()-3,headerHeight-2);
		// always draw a line at the bottom, so the PC won't be 'open' when scrolling
		//g.drawLine(0, getHeight()-1, getWidth()-1, getHeight()-1);
		g.beginPath();
		g.moveTo(xPos+0, yPos+getHeight()-1);
		g.lineTo(xPos+getWidth()-1, yPos+getHeight()-1);
		g.stroke();
		
		if ( isWide )
		{
			g.setFillStyle(CssColor.make(0,0,0));
			g.beginPath();		
			g.moveTo(arrowIn.doubleX[0], arrowIn.doubleY[0]);
			for (int k = 1; k < arrowIn.aantalPunten; k++) 
			{	g.lineTo(arrowIn.doubleX[k], arrowIn.doubleY[k]);
			}
			g.lineTo(arrowIn.doubleX[0], arrowIn.doubleY[0]);
			g.closePath();
			g.fill();

		} 
		else if (widthIsChangable)
		{
			g.setFillStyle(CssColor.make(0,0,0));
			g.beginPath();		
			g.moveTo(arrowOut.doubleX[0], arrowOut.doubleY[0]);
			for (int k = 1; k < arrowOut.aantalPunten; k++) 
			{	g.lineTo(arrowOut.doubleX[k], arrowOut.doubleY[k]);
			}
			g.lineTo(arrowOut.doubleX[0], arrowOut.doubleY[0]);
			g.closePath();
			g.fill();

		}
		if ( !isHeightFixed )
		{	
			if ( isOpen )
			{
				g.setStrokeStyle(CssColor.make(0,0,0));
				g.beginPath();
				g.moveTo(xPos+getWidth()-45, yPos+12);
				g.lineTo(xPos+getWidth()-29, yPos+12);
				g.stroke();
				
				g.beginPath();
				g.moveTo(xPos+getWidth()-45, yPos+13);
				g.lineTo(xPos+getWidth()-29, yPos+13);
				g.stroke();
			} 
			else
			{
				g.setStrokeStyle(CssColor.make(0,0,0));
				g.strokeRect(xPos+getWidth()-45, yPos+6, 14, 14);
			}
		}
	}

	protected void paintCommand(Context2d g)
	{
		g.setFont(WebLogo3dGWT.boldFontString);
		g.setFillStyle(CssColor.make(0,0,0));
		g.fillText(defaultName,xPos+10,yPos+18);
	}
	
	public void setVisible(boolean b)
	{
		visible = b;
	}
	
	public void paintComponent(Context2d g)
	{

		if (!visible)
			return;
		
		paintBackground(g);
		paintCommand(g);
		commandBlock.paintComponent(g);
			
	}
	
	/**
	 * Execute the content of this ProgrammaComponent. Will be called by execute-methods.
	 * DeeltaakBodyCC will just execute the content.
	 * Programma will add a line to indicate that program has finished and clean up vartracer
	 * @param trb tracebeheerder
	 * @param ub drawing area
	 * @param varSet current variable set
	 * @return true if successfully executed, false if not 
	 */
	public boolean executeContent(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	

		for(int i=0 ; i<commandBlock.getComponentCount() ; i++)
		{	Object c = commandBlock.getComponent(i);
			if (c instanceof CommandComponent)
			{	boolean tracekleur = ((CommandComponent)c).execute(trb, ub, varSet);
				if(tracekleur)return true;
			}
		}
		return false;
	}	

	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{
		boolean b = executeContent(trb, ub, varSet);
		if ( b ) return b;
		// When tracing, add 'finished' message AFTER last command
		trb.setCommandInfo(WebLogo3dGWT.rb.klaarTekst(), varSet);
		return false;
	}
	
	public String getCode(String tab)
	{	
		return commandBlock.getCode(tab);
	}

	public CommandComponent findCComponentAt(int x, int y, CommandComponent sc)
	{
		if (commandBlock.contains(x,y))
			return commandBlock.findCComponentAt(x,y, sc);
		else if ((sc != this) && contains(x,y))
			return this;
		else
			return null;
	}

	public CommandContainer findCContainerAt(int x, int y)
	{
		if (commandBlock.contains(x,y))
			return commandBlock.findCContainerAt(x,y);
		else
			return null;
		
	}

	
	boolean widthChanged = false;
	boolean heightChanged = false;
	/**
	 * The mousePressed event is used (also) to bring the ProgrammaComponent to the front of the ProgrammaPanel
	 * and to handle actions that resize the PC.
	 */
	public void mousePressed(int x, int y, int modifiers) 
	{
		widthChanged = false;
		heightChanged = false;
		
		schuifveld.putOnTop(this);
		
		// remember location in case of dragging
		previousX = getX();
		previousY = getY();
		// check if click is inside rectangle in the top right corner of the PC for resize.
		if (x > xPos+getWidth()-2*headerHeight && y < yPos + headerHeight )
		{
			// gedrukt op out of in arrow
			if (widthIsChangable && x > xPos+getWidth()-headerHeight )
			{
				changeWidth();
				widthChanged = true;
				schuifveld.paint();
				// after resize, no further mouse handling
				return;
			} 
			else //gedrukt op - of blokje
			{
				if ( !isHeightFixed )
				{	changeHeight();
					heightChanged = true;
					schuifveld.paint();
					// after resize, no further mouse handling
					return;
				}
			}
		}
		schuifveld.paint();
		// when PC has been brought to front, continue normal mouse event handling
		super.mousePressed(x, y, modifiers);
	}
	
	public void mouseReleased(int x, int y, int modifiers)
	{	
		
		if (widthChanged || heightChanged)
			return;
		super.mouseReleased(x, y, modifiers);
	}
	
	
	public void mouseDragged(int x, int y, int modifiers)
	{
		if (widthChanged || heightChanged)
			return;

		super.mouseDragged(x, y, modifiers);
		
	}
	

}
