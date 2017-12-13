package fi.weblogo3dgwt.client;


import java.util.ArrayList;
import java.util.List;

import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.VarSet;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;
import fi.weblogo3dgwt.client.logotekenap3d.Rectangle;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;

/**
 * see class CommandComponent in WebLogoGWT
 */

public abstract class CommandComponent  
{
	JavaLogoSchuifVeld schuifveld;
	
	protected boolean vast;
	protected boolean caretUp, caretDown;
	protected boolean isStapel = true;
		
	protected String commandName;
	protected String commandNameTranslated;
			
	public boolean traceKleur;
	int traceKleurCnt = 0;
	public CssColor traceActiveColor = CssColor.make(255,200,200);
	
	// variables for handling mouse events: editting & dragging
	protected boolean dragging = false;
	private int startx = 0;			// PBgv: position of mousePressed
	private int starty = 0;;
	protected int startCompx = 0;		// PBgv: start position of CommandComponent at mousePressed
	protected int startCompy = 0;
	private int dx = 0;				// PBgv: displacement through mouseDragged
	private int dy = 0;
	
	int xPos, yPos, breedte, hoogte;
	
	CommandContainer parent = null;
	
	boolean visible = true;
	
	protected boolean press;
    protected long taptime;
    protected List<Long> doubletap = new ArrayList<Long>();
	
	public CommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		xPos = x; yPos = y; breedte = b; hoogte = h;
		schuifveld = sv;
	}
	
	public void setBounds(int x, int y, int b, int h)
	{
		xPos = x; yPos = y; breedte = b; hoogte = h;
	}
	
	public Point getLocation()
	{
		return new Point(xPos, yPos);
	}
	
	public void setLocation(int x, int y)
	{
		xPos = x; yPos = y;
	}
	
	public Dimension getSize()
	{ 
		return new Dimension(breedte, hoogte);
	}

	public void setSize(int b, int h)
	{ 
		breedte = b; hoogte = h;
	}

	public int getHeight()
	{
		return hoogte;
	}

	public int getWidth()
	{
		return breedte;
	}

	public void setWidth(int b)
	{
		breedte = b;
	}

	public int getX()
	{
		return xPos;
	}

	public int getY()
	{
		return yPos;
	}

	public boolean contains(int x, int y)
	{
		if (!visible)
			return false;
		Rectangle rect = new Rectangle(xPos, yPos, breedte, hoogte);
		return rect.contains(x, y);
	}
	
	public CommandComponent findCComponentAt(int x, int y)
	{
		if (contains(x,y))
			return this;
		else
			return null;
	}

	public CommandComponent findCComponentAt(int x, int y, CommandComponent sc)
	{
		if ((sc != this) && contains(x,y))
			return this;
		else
			return null;
	}

	public CommandContainer findCContainerAt(int x, int y)
	{
		return null;
	}


	public String getCommandName()
	{
		return commandName;
	}
	
	public String getCommandNameTranslated()
	{
		return commandNameTranslated;
	}
	/**
	 * Standaard hebben nieuw gemaakte CC's de stapeleigenschap op true staan. Dat is onhandig
	 * bij importeren (maar handig bij pakken van de stapel). ProgrammaImporter moet het uit kunnen zetten.
	 */
	void clearStapel()
	{
		isStapel = false;
	}
	
	public boolean isStapel()
	{
		return isStapel;
	}
	
	public void zetVast(boolean b)
	{	vast = b;
	}
	
	public void removeCaret()
	{
		caretUp = false;
		caretDown = false;
	}
	
	/**
	 * Set caret on this CC
	 * Note: ProgrammaComponent will override to avoid carets.
	 * @param y		the absolute ypos of the middle of the CC hovering over this CC
	 */
	public void setCaret(int y)
	{
		
		boolean downcaret = (y-getAbsoluteLocation().y > getHeight()/2 );
		caretUp = !downcaret;
		caretDown = downcaret;
		if (parent != null)
		{	parent.setInsert(this, downcaret);
		
		}

	}
	
	
	/**
	 * Geeft absolute positie van deze Component in het JavaLogoSchuifVeld
	 * PBgv: omdat muisevents nu absoluut zijn, hebben we ook de absolute positie van componenten nodig.
	 * @return absolute positie
	 */
	public Point getAbsoluteLocation()
	{	//alles is al absoluut in GWT
		Point p = getLocation();
		return p;
	}
	
    protected boolean isLongClick() 
    {
    	return System.currentTimeMillis() - taptime > 300;
	}

	protected boolean isDoubleClick() 
	{
	    return doubletap.size() >= 2 && doubletap.get(1) - doubletap.get(0) < 700;
	}

	public void mousePressed(int x, int y, int modifiers)
	{
		
		if (vast)
			return;
		startx = x;								// PBgv: '+getLocation.x of y' removed 4x, ook bij dragged
		starty = y;
		Point p = getAbsoluteLocation();		// PBgv: remember startposition of component for further mouse action
		startCompx = p.x;
		startCompy = p.y;
		dx = 0;
		dy = 0;
		dragging = false;
		
		if (this instanceof ParameterEditorListener)
		{
			taptime = System.currentTimeMillis();
	        doubletap.add(taptime);
		}
	}
	
	public void moveComponent(int dx, int dy)
	{	
		int x = startCompx + dx;				// PBgv: new Location = original + mouse displacement
		int y = startCompy + dy;		
		if (schuifveld.isGesloten())
		{	x = Math.max(0, Math.min(x, schuifveld.getSize().width-getSize().width));
			y = Math.max(0, Math.min(y, schuifveld.getSize().height-getSize().height));
		}
		setLocation(x,y);
	}
	
	public void mouseDragged(int x, int y, int modifiers)
	{	if (vast)
			return;
		dx = x-startx;
		dy = y-starty;
		if (dx*dx+dy*dy>=20 || dragging) 
		{	
			if ( !dragging )		// start dragging a CC
			{	
				dragging = true;
				if (isStapel)
				{	schuifveld.zetStapel(this);		// get new copy from pile in GUI
					isStapel = false;
					
				}				
				if (parent != null)
				{
					parent.remove(this);
					parent = null;
				}
				schuifveld.zetSchuiver(this);
				startCompx = Math.max(x-getDragWidth()+10, startCompx);
			}
			schuifveld.traceComponent(this, x, y);
			moveComponent(dx, dy);
			schuifveld.paint();
		}
	}
	
	/**
	 * Get the width of this CC when it is being dragged.
	 * Normally it will b e small, so you can see where you're putting it. This is not needed
	 * when arranging deeltaken in the ProgrammaPanel, so DeeltaakBodyc will override to retain 
	 * its original width
	 * @return	width of this component when dragging it
	 */
	int getDragWidth()
	{
		return JavaLogoSchuifVeld.ccsw;
	}
	
	/**
	 * Standard CC's enable tracing (carets), but DeeltaakBody's won't (will override to return false)
	 * @return true, if we want to seee carets while dragging
	 */
	boolean isTraceable()
	{
		return true;
	}
	
	/**
	 * Drop this component on the JavaLogoSchuifVeld. Usually this means finding the CommandContainer
	 * that will receive this component.
	 * DeeltaakBody's will override to allow the user to move the bodies in the programmaPanel
	 * @param x x-position of mouseReleased
	 * @param y y-position of mouseReleased
	 */
	protected void dropComponent(int x, int y)
	{
		schuifveld.losSchuiver(this, x, y);
		if (parent == null && !isStapel)
		{	
			schuifveld.verwijder(this);
		}
	}
	
	public void mouseReleased(int x, int y, int modifiers)
	{
		if( !dragging && !isStapel) // PBgv: !isStapel toegevoegd: niet editten van componenten links
		{
			// editing of CCs that are 'vast' is allowed: name of 'deeltaak'.
			if (this instanceof ParameterEditorListener)
			{	
				if (isDoubleClick())	
				{ 	
					ParameterEditorListener pel = (ParameterEditorListener) this; 
					pel.parameterComponentClicked(x-getAbsoluteLocation().x, y-getAbsoluteLocation().y);
				
					doubletap.clear();
					
				}
				else if (isLongClick() && !dragging)	
				{ 	
					ParameterEditorListener pel = (ParameterEditorListener) this; 
					pel.parameterComponentClicked(x-getAbsoluteLocation().x, y-getAbsoluteLocation().y);
				
					doubletap.clear();
				}
				
				else
				{
					if (doubletap.size() >= 2)
						doubletap.remove(0);
				}
			}
		}
		else if (!vast && dragging )
		{ 	
			dropComponent(x, y);
		}
	}
		
	public void tekenOpnieuw()
	{	schuifveld.paint();
	}
	
	/**
	 * Paint the background of the CommandComponent: rectangles, bgcolor
	 * @param g the Graphics context
	 */
	protected abstract void paintBackground(Context2d g);
	
	/**
	 * Paint the text of the CommandComponent: command name and parameters that are not being editted
	 * For the composite components this will be: repetitions for loop / condition / deeltaaknaam / tekenalgoritme
	 * @param g Context2d for drawing
	 */
	protected abstract void paintCommand(Context2d g);

	/**
	 * Paint caret lines (when dragging a CommandComponent)
	 * Can be implemented here, since we only draw carets at top or bottom of CComponent.
	 * @param g Context2d for drawing
	 */
	private void paintCaret(Context2d g)
	{
		g.setStrokeStyle(CssColor.make(0,255,0));
		if(caretUp)
		{	
			g.beginPath();
			g.moveTo(xPos+2,yPos+2);
			g.lineTo(xPos+getSize().width-3,yPos+2);
			g.stroke();
		
			g.beginPath();
			g.moveTo(xPos+2,yPos+3);
			g.lineTo(xPos+getSize().width-3,yPos+3);
			g.stroke();
			
			//caretUp = false;
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
			
			
			//caretDown = false;
		}
	}
	
	public boolean isVisible()
	{
		return visible;
	}
	
	public void setVisible(boolean b)
	{
		visible = b;
	}

	/**
	 * Painting of the CComponent in three parts, that are implemented at various levels in class hierarchy
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
	
	
	public void paint(Context2d g)
	{
		paintCaret(g);
	}
	
	public abstract boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet);
	
	public abstract String getCode(String tab);


}
