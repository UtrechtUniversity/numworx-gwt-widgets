package fi.weblogogwt.client;

import java.util.ArrayList;
import java.util.List;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.VarSet;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;

/**
 * superclass for all CommandComponents; note that in GWT CommandComponents are not Components
 * in the sense of Java, but just simulate being a Component; the class implements mouse (touch)
 * events on the CC (after determining in JavaLogoSchuifVeld on which CC such event took place);
 * if necessary, methods must be redefined in the subclasses     
 */

public abstract class CommandComponent 
{
	/**
	 * JavaLogoSchuifVeld containing the Canvas for painting 
	 */
	JavaLogoSchuifVeld schuifveld;
	
	/**
	 * can this CC be dragged?
	 */
	protected boolean vast;

	/**
	 * show caret at top?
	 */
	protected boolean caretUp;
	/**
	 * show caret at bottom?
	 */
	protected boolean caretDown;
	
	/**
	 * is this CC simulating a pile? that is, after dragging this CC away,
	 * a new instance should appear on the original location
	 */
	protected boolean isStapel = true;
		
	/**
	 * name of the command that this CC implements
	 */
	protected String commandName;
	
	/**
	 * translated name of the command that this CC implements, not used
	 */
	protected String commandNameTranslated;

	/**
	 * true if CComponent is being traced
	 */
	public boolean traceKleur;
	/**
	 * number of paints after activating tracing on this CC; tracekleur is removed
	 * after three paints 
	 */
	int traceKleurCnt = 0;
	/**
	 * color when tracing (pink)
	 */
	public CssColor traceActiveColor = CssColor.make(255,200,200); 
	
	/**
	 * is this CC being dragged?
	 */
	protected boolean dragging = false;
	/**
	 * x-position of mousePressed (touchDown)
	 */
	private int startx = 0;
	/**
	 * y-position of mousePressed (touchDown)
	 */
	private int starty = 0;;
	/**
	 * x-position of this CC at mousePressed (touchDown)
	 */
	protected int startCompx = 0;
	/**
	 * y-position of this CC at mousePressed (touchDown)
	 */
	protected int startCompy = 0;
	/**
	 * x-displacement mouseDragged (touchMoved)
	 */
	private int dx = 0;				
	/**
	 * y-displacement mouseDragged (touchMoved)
	 */
	private int dy = 0;
	
	/**
	 * simulating a Java-Component
	 */
	int xPos, yPos, breedte, hoogte;
	
	/**
	 * the CommandContainer to which this CC belongs (if any)
	 */
	CommandContainer parent = null;
	
	/**
	 * is this CC visible?
	 */
	boolean visible = true;
	
	/**
	 * System time of last mousePressed (touchDown) event
	 */
    protected long taptime;
    /**
     * System times of most recent mousePressed (touchDown) events
     */
    protected List<Long> doubletap = new ArrayList<Long>();

    /**
     * constructor
     * @param x x-position
     * @param y y-position
     * @param b width
     * @param h height
     * @param sv JavaLogoSchuifVeld containing the Canvas for painting
     */
	public CommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	xPos = x; yPos = y; breedte = b; hoogte = h;
		schuifveld = sv;
	}
	
	/**
	 * simulating a Java-Component
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 */
	public void setBounds(int x, int y, int b, int h)
	{	xPos = x; yPos = y; breedte = b; hoogte = h;
	}
	
	/**
	 * simulating a Java-Component
	 * @return location of this CC
	 */
	public Point getLocation()
	{	return new Point(xPos, yPos);
	}
	
	/**
	 * simulating a Java-Component
	 * @param x new x-position
	 * @param y new y-position
	 */
	public void setLocation(int x, int y)
	{	xPos = x; yPos = y;
	}
	
	/**
	 * simulating a Java-Component
	 * @return dimension of this CC
	 */
	public Dimension getSize()
	{ 	return new Dimension(breedte, hoogte);
	}

	/**
	 * simulating a Java-Component
	 * @param b new width
	 * @param h new height
	 */
	public void setSize(int b, int h)
	{ 	breedte = b; hoogte = h;
	}

	/**
	 * simulating a Java-Component
	 * @return height of this CC
	 */
	public int getHeight()
	{	return hoogte;
	}

	/**
	 * simulating a Java-Component
	 * @return breedte
	 */
	public int getWidth()
	{	return breedte;
	}

	/**
	 * simulating a Java-Component
	 * @param b new value of breeedte
	 */
	public void setWidth(int b)
	{	breedte = b;
	}

	/**
	 * simulating a Java-Component
	 * @return x-position
	 */
	public int getX()
	{	return xPos;
	}

	/**
	 * simulating a Java-Component
	 * @return y-position
	 */
	public int getY()
	{	return yPos;
	}

	/**
	 * check if this CC contains the poit (x,y)
	 * @param x x to be checked
	 * @param y y to be checked
	 * @return true/false
	 */
	public boolean contains(int x, int y)
	{	if (!visible)
			return false;
		Rectangle rect = new Rectangle(xPos, yPos, breedte, hoogte);
		return rect.contains(x, y);
	}
	
	/**
	 * recursively looking for the CC at position (x,y), see class JavaLogoSchuifVeld,
	 * class CommandContainer and the subclasses containing a CommandContainer; <br>
	 * @param x x to be checked
	 * @param y y to be checked
	 * @return this CC or null
	 */
	public CommandComponent findCComponentAt(int x, int y)
	{	if (contains(x,y))
			return this;
		else
			return null;
	}
	/**
	 * recursively looking for the CC at position (x,y) which does not equal CC sc;
	 * see class JavaLogoSchuifVeld, class CommandContainer and the subclasses containing a CommandContainer; <br>
	 * @param x x to be checked
	 * @param y y to be checked
	 * @param sc CC to be checked
	 * @return this CC or null
	 */
	public CommandComponent findCComponentAt(int x, int y, CommandComponent sc)
	{	if ((sc != this) && contains(x,y))
			return this;
		else
			return null;
	}

	/**
	 * recursively looking for the CommandContainer at position (x,y), see class JavaLogoSchuifVeld,
	 * class CommandContainer and the subclasses containing a CommandContainer; <br>
	 * @param x x to be checked
	 * @param y y to be checked
	 * @return null 
	 */
	public CommandContainer findCContainerAt(int x, int y)
	{	return null;
	}

	/**
	 * getter for commandName
	 * @return commandName
	 */
	public String getCommandName()
	{	return commandName;
	}
	
	/**
	 * getter for commandNameTranslated
	 * @return commandNameTranslated
	 */
	public String getCommandNameTranslated()
	{	return commandNameTranslated;
	}
	/**
	 * set the stapel-property of this CC to false;
	 * the CC's in JavaLogoSchuifVeld have stapel == true (they are piles); when importing code,
	 * ProgrammaImporter should be able to put stapel-property to false;
	 */
	void clearStapel()
	{	isStapel = false;
	}
	
	/**
	 * is this CC part of a pile?
	 * @return true/false
	 */
	public boolean isStapel()
	{	return isStapel;
	}
	
	/**
	 * can this CC be dragged?
	 * @param b value of vast
	 */
	public void zetVast(boolean b)
	{	vast = b;
	}
	
	/**
	 * remove the caret of this CC
	 */
	public void removeCaret()
	{	caretUp = false;
		caretDown = false;
	}
	
	/**
	 * Set the caret on this CC
	 * up or down caret depending on y
	 * @param y the absolute ypos of the middle of the CC hovering over this CC
	 */
	public void setCaret(int y)
	{	boolean downcaret = (y-getAbsoluteLocation().y > getHeight()/2 );
		caretUp = !downcaret;
		caretDown = downcaret;
		if (parent != null)
		{	parent.setInsert(this, downcaret);
		}
	}
	
	/**
	 * give the position of this CC in the JavaLogoSchuifVeld
	 * change from Java: positions are always absolute 
	 * @return position of this CC
	 */
	public Point getAbsoluteLocation()
	{	Point p = getLocation();
		return p;
	}

	/**
	 * check for long click
	 * @return true/false
	 */
    protected boolean isLongClick() 
    {  	return System.currentTimeMillis() - taptime > 300;
	}

    /**
     * check for double click
     * @return true/false
     */
	protected boolean isDoubleClick() 
	{	return doubletap.size() >= 2 && doubletap.get(1) - doubletap.get(0) < 700;
	}

	/**
	 * mouseDown/touchStart action on this CommandCompoment
	 * @param x x-coordinate of mouseDown/touchStart Event 
	 * @param y y-coordinate of mouseDown/touchStart Event
	 * @param modifiers not used
	 */
	public void mousePressed(int x, int y, int modifiers)
	{
		if (vast)
			return;
		// position of mouseDown/touchStart on this CC 
		startx = x;								
		starty = y;
		// position of CC at mousePressed
		Point p = getAbsoluteLocation();		
		startCompx = p.x;
		startCompy = p.y;
		dx = 0;
		dy = 0;
		dragging = false;
		// set these for determining double or long click at mouseReleased
		if (this instanceof ParameterEditorListener)
		{	taptime = System.currentTimeMillis();
	        doubletap.add(taptime);
		}
	}

	/**
	 * move this CC over (dx,dy);
	 * @param dx x-translation
	 * @param dy y-translation
	 */
	public void moveComponent(int dx, int dy)
	{	// new x- and y-position
		int x = startCompx + dx;
		int y = startCompy + dy;	
		if (schuifveld.isGesloten())
		{	// avoid dragging outside schuifveld	
			if (x <= 0)
				x = 0;
			else
				x = Math.max(0, Math.min(x, schuifveld.getSize().width-getSize().width));
			if (y <= 0)
				y = 0;
			else
				y = Math.max(0, Math.min(y, schuifveld.getSize().height-getSize().height));
		}
		setLocation(x,y);
	}
	
	/**
	 * mouseMove/touchMove action on this CommandCompoment
	 * @param x x-coordinate of mouseMove/touchMove Event 
	 * @param y y-coordinate of mouseMove/touchMove Event
	 * @param modifiers not used
	 */
	public void mouseDragged(int x, int y, int modifiers)
	{	if (vast)
			return;
		dx = x-startx;
		dy = y-starty;
		if (dx*dx+dy*dy>=20 || dragging) 
		{	// start dragging this CC
			if ( !dragging )	
			{	
				dragging = true;
				if (isStapel)
				{	// make a new copy of this CC on the pile
					// note that this CC stays part of the CC's belonging to schuifveld
					schuifveld.zetStapel(this);		
					isStapel = false;
				}				
				if (parent != null)
				{	// remove CC from the CommandContainer it belongs to
					parent.remove(this);
					parent = null;
				}
				// give this CC its draggWidth, put on top when drawing and 
				// rearrange the CommandContainer (if any) it belonged to
				// see method zetSchuiver in class JavaLogoSchuifVeld
				schuifveld.zetSchuiver(this);
				startCompx = Math.max(x-getDragWidth()+10, startCompx);
			}
			// find underlying CC or CContainer, display its caret 
			// see method traceComponent in JavaLogoSchuifVeld 
			schuifveld.traceComponent(this, x, y);
			moveComponent(dx, dy);
			schuifveld.paint();
		}
	}
	
	/**
	 * Get the width of this CC when it is being dragged.
	 * Normally it will be small, so you can see where you're putting it. This is not needed
	 * when arranging deeltaken in the ProgrammaPanel, so DeeltaakBodyc will override to retain 
	 * its original width
	 * @return	width of this component when dragging it
	 */
	int getDragWidth()
	{	return JavaLogoSchuifVeld.ccsw;
	}
	
	/**
	 * Standard CC's enable tracing (carets), but DeeltaakBody's won't (will override to return false)
	 * @return true, if we want to see carets while dragging
	 */
	boolean isTraceable()
	{	return true;
	}
	
	/**
	 * Drop this component on the JavaLogoSchuifVeld. Usually this means finding the CommandContainer
	 * that will receive this component and adding this CC to that CommandContainer.
	 * DeeltaakBody's will override to allow the user to move the bodies in the programmaPanel
	 * @param x x-position of drop
	 * @param y y-position of drop
	 */
	protected void dropComponent(int x, int y)
	{
		// find the CommandContainer (if any) at drop-coordinates (x,y)
		// see method losSchuiver in JavaLogoSchuifVeld
		schuifveld.losSchuiver(this, x, y);
		// if CC is not contained in a CommandContainer, so is a floating CC, remove from schuifveld 
		if (parent == null && !isStapel)
		{	schuifveld.verwijder(this);
		}
	}

	/**
	 * mouseUp/touchEnd action on this CommandCompoment
	 * @param x x-coordinate of mouseUp/touchEnd Event 
	 * @param y y-coordinate of mouseUp/touchEnd Event
	 * @param modifiers not used
	 */
	public void mouseReleased(int x, int y, int modifiers)
	{
		// PBgv: !isStapel added: CC's on a pile cannot be edited
		if( !dragging && !isStapel) 
		{
			// editing of CCs that are 'vast' is allowed: name of 'deeltaak'.
			// check if this CC is editable
			if (this instanceof ParameterEditorListener)
			{	// check if a double click was performed		
				if (isDoubleClick())	
				{	ParameterEditorListener pel = (ParameterEditorListener) this;
					// start edit
					pel.parameterComponentClicked(x-getAbsoluteLocation().x, y-getAbsoluteLocation().y);
					doubletap.clear();
				}
				else if (isLongClick() && !dragging)	
				{ 	// check if a long click was performed
					ParameterEditorListener pel = (ParameterEditorListener) this;
					// start edit
					pel.parameterComponentClicked(x-getAbsoluteLocation().x, y-getAbsoluteLocation().y);
					doubletap.clear();
				}
				else // clear 
				{	if (doubletap.size() >= 2)
						doubletap.remove(0);
				}
			}
		}
		else if (!vast && dragging )
		{ 	
			dropComponent(x,y);
		}
		tekenOpnieuw();
	}
	
	/**
	 * repaint
	 */
	public void tekenOpnieuw()
	{	schuifveld.paint();
	}
	
	/**
	 * Paint the background of the CommandComponent: rectangles, bgcolor
	 * @param g the Context2d for drawing
	 */
	protected abstract void paintBackground(Context2d g);
	
	/**
	 * Paint the text of the CommandComponent: command name and parameters that are not being editted
	 * For the composite components this will be: repetitions for loop / condition / deeltaaknaam / tekenalgoritme
	 * @param g Context2d for drawing
	 */
	protected abstract void paintCommand(Context2d g);

	/**
	 * Paint caret lines indicating if a dragged CommandComponent should be inserted above or below
	 * at mouse release; Can be implemented here, since we only draw carets at top or bottom of CComponent.<br>
	 * Do not reset the caret line after painting (as in Java), since multiple paints might occur in which
	 * case the caret lines do not appear; reset caret lines in traceComponent in class JavaLogoSchuifVeld
	 * @param g Context2d for drawing
	 */
	private void paintCaret(Context2d g)
	{
		// green
		g.setStrokeStyle(CssColor.make(0,255,0));
		if(caretUp)
		{	g.beginPath();
			g.moveTo(xPos+2,yPos+2);
			g.lineTo(xPos+getSize().width-3,yPos+2);
			g.stroke();
		
			g.beginPath();
			g.moveTo(xPos+2,yPos+3);
			g.lineTo(xPos+getSize().width-3,yPos+3);
			g.stroke();
			// omit
//			caretUp = false;
		}
		if(caretDown)
		{	
			g.beginPath();
			g.moveTo(xPos+2,yPos+getSize().height-3);
			g.lineTo(xPos+getSize().width-3,yPos+getSize().height-3);
			g.stroke();
			
			g.beginPath();
			g.moveTo(xPos+2,yPos+getSize().height-4);
			g.lineTo(xPos+getSize().width-3,yPos+getSize().height-4);
			g.stroke();
			// omit
//			caretDown = false;
		}
	}
	
	/**
	 * get the visibility of this CComponenet
	 * @return true/false
	 */
	public boolean isVisible()
	{
		return visible;
	}
	/**
	 * set the visibility of this CComponenet
	 * @param b visible or not
	 */
	public void setVisible(boolean b)
	{	visible = b;
	}
	/**
	 * Painting of the CComponent in three parts, parts that are implemented/redefined at various levels in class hierarchy
	 * @param g Context2d for drawing
	 */
	public void paintComponent(Context2d g)
	{
		if (!visible)
			return;
		paintBackground(g);
		paintCommand(g);
		paintCaret(g);
	}
	
	/**
	 * this just paints the Caret if the CC (if any)
	 * @param g Context2d for drawing
	 */
	public void paint(Context2d g)
	{	paintCaret(g);
	}
	
	/**
	 * check the parameters (if any) of this command for correctness;
	 * execute this command, if tracing, change its color and display
	 * the command and parameter; see class TraceBeheerder
	 * @param trb the TraceBeheerder, see that class
	 * @param ub the drawing area, see class Uitvoerblad/Tekenblad
	 * @param varSet the current variable set
	 * @return true if this command is correct and can be executed
	 */
	public abstract boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet);

	/**
	 * return the code represented by this CC, prefixed with the String tab
	 * @param tab prefix
	 * @return tab+code
	 */
	public abstract String getCode(String tab);


}
