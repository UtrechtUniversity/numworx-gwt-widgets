package fi.weblogogwt.client;

//import java.awt.Color;
//import java.awt.Component;
//import java.awt.Graphics;
//import java.awt.Polygon;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.event.MouseMotionListener;
//import java.awt.event.MouseWheelEvent;
//import java.awt.event.MouseWheelListener;

//import javax.swing.JPanel;

//import java.awt.Component;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;
import fi.weblogogwt.client.CommandComponent;
import fi.weblogogwt.client.WebLogoGWT;
import fi.weblogogwt.client.VarSet;
//import fi.weblogogwt.client.logotekenap.*;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;

public class ProgrammaComponent extends CompositeCommandComponent //implements MouseWheelListener, MouseListener, MouseMotionListener
{	
	protected String defaultName = "";
	protected CommandContainer commandBlock;
	
	//protected JPanel maskPanel;
	
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
	 * previousX & -Y remember the old location of the component when it is dragged.
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
		
		widthIsChangable = schuifveld.breedte == WebLogoGWT.jlsBreedteGroot;
		
		commandName = defaultName;
		
//GWT??		
		//maskPanel = new JPanel();
		//maskPanel.setBounds(0, headerHeight, b, h-headerHeight-1);
		//maskPanel.setLayout(null);
		//add(maskPanel);
		//addMouseWheelListener(this);
		//addMouseListener(this);
		//addMouseMotionListener(this);
		
		commandBlock = new CommandContainer(xPos+0, yPos+headerHeight, b, h-headerHeight, this);
		//maskPanel.add(commandBlock);
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
	 * 
	 * @return	the default name for this ProgrammaComponent
	 */
	public String getProgramName()
	{
		return defaultName;
	}

	public void addCComponent(CommandComponent cc)
	{	
		//commandBlock.addCComponent(cc);
		commandBlock.addCCompAtBottom(cc);
	}
	
	/**
	 * PC's don't resize when user drops or removes CC's. Resizing is left to the user,
	 * when the containers get too big, the scroll wheel will work.
	 * 
	 * @see fi.javalogoweb.CompositeCommandComponent#containerHeightChanged(int)
	 */
	
	public void setLocation(int x, int y)
	{
		xPos = x; yPos = y;
		if (commandBlock != null)
		{
//System.out.println("setLoc " + commandName);			
			commandBlock.setLocation(xPos, yPos+headerHeight);
		}
		createHArrows();
	}

	
	@Override
	void containerHeightChanged(int h)
	{	}
	
	/**
	 * This method also adjusts the sizes of the CommandContainer and the maskPanel.
	 * 
	 * @see java.awt.Component#setSize(int, int)
	 */
	@Override
	public void setSize(int w, int h)
	{
		
//if (commandName.indexOf("3")>= 0)
//System.out.println("setSize " + commandName);	
		
		int cbh = commandBlock.getHeight();
		super.setSize(w, h);
//GWT?		
		//maskPanel.setSize(w, h-headerHeight-1);
		
		//if ( h-headerHeight-1 > cbh )
		//{
			// on open, heightt may be greater than the content. Increase height of container
			// to avoid 'grey rectangle' in this component.
			commandBlock.setSize(commandBlock.getWidth(), h-headerHeight);
		//}
		commandBlock.setLocation(xPos, yPos+headerHeight);
		commandBlock.setWidth(w);
		// also set minimum height, so container won't reduce height when rearranging the components
		//commandBlock.setMinimumHeight(h-headerHeight-1);
	}
	
	/**
	 * Change the width when arrow in header is clicked: alternate between two fixed widths
	 */
	private void changeWidth()
	{
		if ( isWide )
		{
			setSize(pcsw, getHeight());
			//setLocation(narrowX, getY());
			isWide = false;
		} 
		else
		{
			setSize(pclw, getHeight());
			narrowX = getX();
			//setLocation(Math.min(getX(), JavaLogoSchuifVeld.ppw-pclw), getY());
			isWide = true;
		}
	}
	
	void changeHeight()
	{
//System.out.println("PCC changeHeight");

		if ( isHeightFixed ) 
			return;
		int newh;
		if ( isOpen )
		{
			
//System.out.println("isOpen");			
			newh = pcclosedh;
			commandBlock.componentsVisible = false;
		} 
		else
		{
//System.out.println("!isOpen");			
			newh = Math.min(schuifveld.getHeight()-20, Math.max(pcminoh, commandBlock.getContentHeight()+headerHeight+20));
			commandBlock.componentsVisible = true;
		}
		isOpen = !isOpen;
		setSize(getWidth(), newh);
//GWT?		
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
	 * 
	 * @see fi.javalogoweb.CommandComponent#setCaret(int)
	 */
	@Override
	public void setCaret(int y)
	{  }

	@Override
	//protected void paintBackground(Graphics g)
	protected void paintBackground(Context2d g)
	{
		//g.setColor(new Color(187,221,255));//new Color(230,240,255);
		g.setFillStyle(CssColor.make(187,221,255));
		g.fillRect(xPos+1,yPos+1,getWidth()-1,headerHeight-1);
		//g.setColor(Color.BLACK);
		g.setStrokeStyle(CssColor.make(0,0,0));
		//g.drawRect(0,0,getWidth()-1,headerHeight);
		g.strokeRect(xPos+0,yPos+0,getWidth()-1,headerHeight);
		//g.drawRect(1,1,getWidth()-3,headerHeight-2);
		g.strokeRect(xPos+1,yPos+1,getWidth()-3,headerHeight-2);
		// always draw a line at the bottom, so the PC won't be 'open' when scrolling
		//g.drawLine(0, getHeight()-1, getWidth()-1, getHeight()-1);
		g.beginPath();
		g.moveTo(xPos+0, yPos+getHeight()-1);
		g.lineTo(xPos+getWidth()-1, yPos+getHeight()-1);
		g.stroke();
		
		if ( isWide )
		{
			//g.fillPolygon(arrowIn);
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
			//g.fillPolygon(arrowOut);
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
				//g.drawLine(getWidth()-45, 12, getWidth()-29, 12);
				g.beginPath();
				g.moveTo(xPos+getWidth()-45, yPos+12);
				g.lineTo(xPos+getWidth()-29, yPos+12);
				g.stroke();
				
				//g.drawLine(getWidth()-45, 13, getWidth()-29, 13);
				g.beginPath();
				g.moveTo(xPos+getWidth()-45, yPos+13);
				g.lineTo(xPos+getWidth()-29, yPos+13);
				g.stroke();
			} 
			else
			{
				g.setStrokeStyle(CssColor.make(0,0,0));
				//g.drawRect(getWidth()-45, 6, 14, 14);
				g.strokeRect(xPos+getWidth()-45, yPos+6, 14, 14);
			}
		}
		if (commandName.indexOf("3") >= 0)
		{
//System.out.println("dt3 CB ypos " + commandBlock.yPos);			
		}
	}

	@Override
	//protected void paintCommand(Graphics g)
	protected void paintCommand(Context2d g)
	{
		//g.setFont(JavaLogoWeb.boldfont);
		g.setFont(WebLogoGWT.boldFontString);
		//g.setColor(Color.BLACK);
		g.setFillStyle(CssColor.make(0,0,0));
		//g.drawString(defaultName,10,18);
		g.fillText(defaultName,xPos+10,yPos+18);
	}
	
	public void setVisible(boolean b)
	{
		visible = b;
	}
	
	public void paintComponent(Context2d g)
	{
		
//System.out.println("pc paintComp");

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
	 * 
	 * @param trb
	 * @param ub
	 * @param varSet
	 * @return
	 */
	public boolean executeContent(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		
//System.out.println("pCC executeContent");

		for(int i=0 ; i<commandBlock.getComponentCount() ; i++)
		{	Object c = commandBlock.getComponent(i);
			if (c instanceof CommandComponent)
			{	boolean tracekleur = ((CommandComponent)c).execute(trb, ub, varSet);
				if(tracekleur)return true;
			}
		}
		return false;
	}	

	@Override
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{
//System.out.println("pCC execute");		
		boolean b = executeContent(trb, ub, varSet);
		if ( b ) return b;
		// When tracing, add 'finished' message AFTER last command
		trb.setCommandInfo(WebLogoGWT.rb.klaarTekst(), varSet);
		return false;
	}
	
	@Override
	public String getCode(String tab)
	{	
		return commandBlock.getCode(tab);
	}
/*	
	public CommandComponent findCComponentAt(int x, int y)
	{
		if (commandBlock.contains(x,y))
			return commandBlock.findCComponentAt(x,y);
		else if (contains(x,y))
			return this;
		else
			return null;
	}
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

	public CommandContainer findCContainerAt(int x, int y)
	{
		if (commandBlock.contains(x,y))
			return commandBlock.findCContainerAt(x,y);
		else
			return null;
		
	}

/* 
 * Implement MouseListeners: wheel is scrolling, pass on other events
 */
	
//GWT??	
/*	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		int heightSurplus = commandBlock.getHeight()-(getHeight()-headerHeight);
		if ( heightSurplus > 0 )
		{	
			int newY = Math.max(-heightSurplus, Math.min(0, commandBlock.getY()+8*e.getWheelRotation()));
			commandBlock.setLocation(commandBlock.getX(), newY);
		}
	}
*/	
	
	boolean widthChanged = false;
	boolean heightChanged = false;
	/**
	 * The mousePressed event is used (also) to bring the ProgrammaComponent to the front of the ProgrammaPanel
	 * and to handle actions that resize the PC.
	 */
	@Override
	public void mousePressed(int x, int y, int modifiers) 
	{
		widthChanged = false;
		heightChanged = false;
//GWT		
//voor deeltaken		
		// Bring the component to the front, only if it's not already in front, because of focus
		//if ( this != getParent().getComponent(0) )
		//{	
		//	getParent().setComponentZOrder(this, 0);
		//	schuifveld.paint();
		//}
		
		schuifveld.putOnTop(this);
		
//System.out.println("PCC mousePressed " + commandName);		
		// remember location in case of dragging
		previousX = getX();
		previousY = getY();
		// check if click is inside rectangle in the top right corner of the PC for resize.
		//if ( e.getX() > getWidth()-2*headerHeight && e.getY() < headerHeight )
		if (x > xPos+getWidth()-2*headerHeight && y < yPos + headerHeight )
		{
			//if ( e.getX() > getWidth()-headerHeight )
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
//GWT?		
		super.mousePressed(x, y, modifiers);
	}
	
	@Override
	//public void mouseReleased(MouseEvent e)
	public void mouseReleased(int x, int y, int modifiers)
	{	
		
//System.out.println("pc mouseReleased " + widthChanged);		
		if (widthChanged || heightChanged)
			return;
		//schuifveld.mouseReleased(getAbsoluteLocation().x+e.getX(), getAbsoluteLocation().y+e.getY(), e.getModifiersEx());
//GWT schuifveld/super ?		
		super.mouseReleased(x, y, modifiers);
	}
	
	
	@Override
	//public void mouseDragged(MouseEvent e)
	public void mouseDragged(int x, int y, int modifiers)
	{
//System.out.println("pc mouseDragged " + widthChanged);		
		if (widthChanged || heightChanged)
			return;

		//schuifveld.mouseDragged(getAbsoluteLocation().x+e.getX(), getAbsoluteLocation().y+e.getY(), e.getModifiersEx());
		super.mouseDragged(x, y, modifiers);
		
	}
	
//	@Override
//	public void mouseMoved(MouseEvent e) 
//	{
		// unused		
//	}
	
//	@Override
//	public void mouseClicked(MouseEvent e) 
//	{
		// unused		
//	}
	
//	@Override
//	public void mouseEntered(MouseEvent e) 
//	{
		// unused
//	}
	
//	@Override
//	public void mouseExited(MouseEvent e) 
//	{
		// unused
//	}

}
