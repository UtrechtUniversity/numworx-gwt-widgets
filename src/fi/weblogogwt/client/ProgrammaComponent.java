package fi.weblogogwt.client;


import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;
import fi.weblogogwt.client.CommandComponent;
import fi.weblogogwt.client.WebLogoGWT;
import fi.weblogogwt.client.VarSet;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;

/**
 * class implementing the main program; note that the main program is fixed in location (that is, it cannot be
 * moved) and size; the subclass DeeltaakBodyComponent however, can be dragged, its default width can be nearly doubled 
 * (and this double width again reversed to normal), while its height can be changed in the sense that all code
 * lines in DeeltaakBodyComponent are either hidden or visible; note that all mouse/touch actions for the subclass 
 * DeeltaakBodyComponent are handled in this class.  
 */

public class ProgrammaComponent extends CompositeCommandComponent 
{	
	/**
	 * fixed for ProgrammaComponent, editable for DeeltaakBodyComponent
	 */
	protected String defaultName = "";
	/**
	 * the actual code lines
	 */
	protected CommandContainer commandBlock;
	
	/**
	 * arrow for doubling width (DeeltaakBodyComponent)
	 */
	private Polygon arrowOut;
	/**
	 * arrow for resetting width to default
	 */
	private Polygon arrowIn;
	
	/**
	 * Height of header
	 */
	public static final int headerHeight = 25;
	
	/**
	 * DeeltaakBodyComponent default width
	 */
	public static final int pcsw = 190; 
	
	/**
	 * DeeltaakBodyComponent large width
	 */
	public static final int pclw = 320;
	
	/**
	 * DeeltaakBodyComponent height when closed (program lines hidden)
	 */
	public static final int pcclosedh = headerHeight+10;
	
	/**
	 * DeeltaakBodyComponent minimum open height 
	 */
	public static final int pcminoh = 160;
	
	/**
	 * Indicates if the user can change the height of this PC. False for the main algorithm, true for deeltaken.
	 * Note: we handle height changes in this class, rather than in DeeltaakBody, to concentrate all associated
	 * mouse handling in one class...
	 */
	protected boolean isHeightFixed = true;
	
	/**
	 * DeeltaakBodyComponent: indicates if this component has been collapsed to it's header 
	 */
	protected boolean isOpen = false;
	
	/**
	 * DeeltaakBodyComponent: indicates if this components width had been more or less doubled
	 */
	private boolean isWide;
	/**
	 * DeeltaakBodyComponent: narrowX remembers the original x-position of the PC, when the PC is made wide, because
	 * generally the PC will have to move left to fit inside the ProgrammaPanel
	 */
	private int narrowX;
	
	/**
	 * previousX and -Y remember the old location of the component when it is dragged.
	 * note that the user cannot dragg the PC over the edges of the programmaPanel
	 */
	protected int previousX;
	protected int previousY;
	/**
	 * Indicates if the user can double/reset the width of this PC. False for the main algorithm, true for deeltaken.
	 */
	boolean widthIsChangable = false;
	
	/**
	 * constructor for main program 
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param pn program name (fixed here)
	 * @param sv instance of JavaLogoSchuifveld
	 */
	public ProgrammaComponent(int x, int y, int b, int h, String pn, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		defaultName = pn;
		isStapel = false;
		isWide = false;
		narrowX = x;
		// subroutines available if equality
		widthIsChangable = schuifveld.breedte == WebLogoGWT.jlsBreedteGroot;
		commandName = defaultName;
		commandBlock = new CommandContainer(xPos+0, yPos+headerHeight, b, h-headerHeight, this);
		commandBlock.parent = this;
		// for testing
		commandBlock.containerName = "program";
		createHArrows();
	}
	
	/**
	 * clear all code lines in this PC
	 */
	void clearProgram()
	{	commandBlock.removeAll();
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

	/**
	 * add a CC at the bottom of the commandBlock (ProgrammaImporter)
	 */
	public void addCComponent(CommandComponent cc)
	{	
		commandBlock.addCCompAtBottom(cc);
	}
	
	/**
	 * DeeltaakBodyComponents
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

	/**
	 * ProgrammaComponent, redefine for DeeltaakBodyComponent
	 */
	void containerHeightChanged(int h)
	{	}
	
	/**
	 * This method also adjusts the size of the CommandContainer
	 */
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		commandBlock.setSize(commandBlock.getWidth(), h-headerHeight);
		commandBlock.setLocation(xPos, yPos+headerHeight);
		commandBlock.setWidth(w);
	}
	
	/**
	 * DeeltaakBodyComponent: change the width when arrow in header is clicked:
	 * alternate between two fixed widths
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

	/**
	 * DeeltaakBodyComponent: change the height when '--' or square in header is clicked:
	 * alternate between two optiones: header only and real height;
	 * if DBC has been moved while closed, and on open the open height of DBC is larger then
	 * the height of JavaLogoSchuifVeld minus the y-position of DBC, move DBC up 
	 */
	void changeHeight()
	{
		if ( isHeightFixed ) 
			return;
		int newh;
		if ( isOpen )
		{	newh = pcclosedh;
			commandBlock.componentsVisible = false;
		} 
		else
		{	newh = Math.min(schuifveld.getHeight()-20, Math.max(pcminoh, commandBlock.getContentHeight()+headerHeight+20));
			commandBlock.componentsVisible = true;
		}
		isOpen = !isOpen;
		setSize(getWidth(), newh);
		setLocation(getX(), Math.min(getY(), Math.max(0,JavaLogoSchuifVeld.pph-newh)));
	}
	
	/**
	 * create right and left arrows for changing width of DeeltaakBodyComponents;
	 * arrows are not drawn for main program
	 */
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
	 * CommandContainer, it's the root of the tree! for DeeltaakBodyComponents the caret is set
	 * at the corresponding DeeltaakCallComponent
	 */
	public void setCaret(int y)
	{  }

	/**
	 * background consists of: header background, header border, commandblock background
	 * commandblock border and (DeeltaakBodyComponent) arrows, '--' or square  
	 */
	protected void paintBackground(Context2d g)
	{
		// blueish
		g.setFillStyle(CssColor.make(187,221,255));
		// header background
		g.fillRect(xPos+1,yPos+1,getWidth()-1,headerHeight-1);
		// black
		g.setStrokeStyle(CssColor.make(0,0,0));
		// header border
		g.strokeRect(xPos+0,yPos+0,getWidth()-1,headerHeight);
		g.strokeRect(xPos+1,yPos+1,getWidth()-3,headerHeight-2);
		// always draw a line at the bottom, so the PC won't be 'open' when scrolling
		g.beginPath();
		g.moveTo(xPos+0, yPos+getHeight()-1);
		g.lineTo(xPos+getWidth()-1, yPos+getHeight()-1);
		g.stroke();
		// left arrow
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
		else if (widthIsChangable) // right aroow
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
		{	// '--'
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
			else // square
			{
				g.setStrokeStyle(CssColor.make(0,0,0));
				g.strokeRect(xPos+getWidth()-45, yPos+6, 14, 14);
			}
		}
	}

	/**
	 * main program title or subroutine name plus variable list 
	 */
	protected void paintCommand(Context2d g)
	{
		g.setFont(WebLogoGWT.boldFontString);
		g.setFillStyle(CssColor.make(0,0,0));
		g.fillText(defaultName,xPos+10,yPos+18);
	}

	/**
	 * set this PC visible, redundant
	 */
	public void setVisible(boolean b)
	{	visible = b;
	}
	
	/**
	 * paint this PC, redefined to also paint commandBlock
	 */
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
	 * if called from Programma will add a line to indicate that program has finished and clean up vartracer
	 * if called from DeeltaakBodyCC will just execute the content.
	 * @param trb the trace manager
	 * @param ub the drawing area
	 * @param varSet the current varSet
	 * @return true if succeeded
	 */
	public boolean executeContent(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		for(int i=0 ; i<commandBlock.getComponentCount() ; i++)
		{	Object c = commandBlock.getComponent(i);
			if (c instanceof CommandComponent)
			{	boolean tracekleur = ((CommandComponent)c).execute(trb, ub, varSet);
				if (tracekleur) return true;
			}
		}
		return false;
	}	

	/**
	 * execute method for this ProgrammaComponent; redefined for DeeltaakBodyComponent  
	 * @param trb the trace manager
	 * @param ub the drawing area
	 * @param varSet the current varSet
	 * @return true if succeeded
	 */
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{
		boolean b = executeContent(trb, ub, varSet);
		if ( b ) return b;
		// When tracing, add 'finished' message AFTER last command
		trb.setCommandInfo(WebLogoGWT.rb.klaarTekst(), varSet);
		return false;
	}
	
	/**
	 * get all code of this PC, redefined for DeeltaakBodyComponent
	 */
	public String getCode(String tab)
	{	
		return commandBlock.getCode(tab);
	}
	
	/**
	 * 1. check if commandBlock contains a CC not equal to sc containing the coordinates (x,y), take the deepest CC in the tree
	 * 2. else check if the header part does not equal sc and contains (x,y)
	 * @return CC found or null
	 */
	public CommandComponent findCComponentAt(int x, int y, CommandComponent sc)
	{
		if (commandBlock.contains(x,y))
			return commandBlock.findCComponentAt(x,y, sc);
		else if ((sc != this) && contains(x,y))
			return this;
		else
			return null;
	}

	/**
	 * check if commandBlock contains a CContainer containing the coordinates (x,y)
	 * @return CContainer found or null
	 */
	public CommandContainer findCContainerAt(int x, int y)
	{
		if (commandBlock.contains(x,y))
			return commandBlock.findCContainerAt(x,y);
		else
			return null;
		
	}

	/**
	 * was the width of this PC (DeeltaakBodyComponent) changed?
	 */
	boolean widthChanged = false;
	/**
	 * was the height of this PC (DeeltaakBodyComponent) changed?
	 */
	boolean heightChanged = false;
	/**
	 * action on mousePressed/touchStart at coordinates (x,y);
	 * note that the mousePressed/touchStart action is also used to bring the ProgrammaComponent
	 * to the front of the ProgrammaPanel and to handle actions that resize the PC.
	 * @param x x-position mousePressed/touchStart event
	 * @param y y-position mousePressed/touchStart event
	 * @param modifiers not used
	 */
	public void mousePressed(int x, int y, int modifiers) 
	{
		widthChanged = false;
		heightChanged = false;
		// bring the component to the front
		schuifveld.putOnTop(this);
		
		// remember location in case of dragging
		previousX = getX();
		previousY = getY();
		// check if click is inside rectangle in the top right corner of the PC for resize.
		if (x > xPos+getWidth()-2*headerHeight && y < yPos + headerHeight )
		{
			// clicked on left or right arrow (DeeltaakBodyComponent)
			if (widthIsChangable && x > xPos+getWidth()-headerHeight )
			{
				changeWidth();
				widthChanged = true;
				schuifveld.paint();
				// after resize, no further mouse handling
				return;
			} 
			else // clicked on '--'or square
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
		// when PC has been brought to front, continue normal mouse/touch event handling
		super.mousePressed(x, y, modifiers);
	}
	
	/**
	 * action on mouseUp/touchEnd at coordinates (x,y);
	 * no action at mouseUp/touchEnd after resizing
	 * @param x x-position mousePressed/touchStart event
	 * @param y y-position mousePressed/touchStart event
	 * @param modifiers not used
	 */
	public void mouseReleased(int x, int y, int modifiers)
	{	
		if (widthChanged || heightChanged)
			return;
		super.mouseReleased(x, y, modifiers);
	}
	
	/**
	 * action on mouseMove/touchMove at coordinates (x,y);
	 * no action at mouseMove/touchMove after resizing
	 * @param x x-position mousePressed/touchStart event
	 * @param y y-position mousePressed/touchStart event
	 * @param modifiers not used
	 */
	public void mouseDragged(int x, int y, int modifiers)
	{
		if (widthChanged || heightChanged)
			return;
		super.mouseDragged(x, y, modifiers);
	}
	
}
