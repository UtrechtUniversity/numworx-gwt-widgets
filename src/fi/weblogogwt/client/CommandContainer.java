package fi.weblogogwt.client;

import java.util.*;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;

/**
 * class CommandContainer simulates a Java Containerthat will hold a list of CommandComponents, corresponding to a 'block'
 * in programming languages. It is used by HerhaalCC, KeuzeCC, DeeltaakCC, ProgrammaCC...
 * Note: CommandComponent is NOT a CommandComponent, it can not be manipulated apart from its owner.
 * the height of the container is always (at least) the sum of the heights of its components.
 * @author Berge020
 */
public class CommandContainer 
{
	/**
	 * the CommandComponent owning this CommandContainer
	 */
	private CompositeCommandComponent owner;

	/**
	 * Minimum height of the container. Will be a small number for the containers in control structures,
	 * more for the deeltaken and the 'tekenalgoritme'.
	 */
	private int minimumHeight;
	
	/**
	 * sum of heights of the CC's in this container
	 */
	private int contentHeight;
	
	/**
	 * Since the container is not a CommandComponent, it must handle carets itself
	 * Caret can only be 'up', you never drop a CC at the bottom of a container.
	 */
	private boolean caretUp;
	
	/**
	 * position for the insertion of a new CC, or 0 if this CContainer is empty
	 */
	private int insertPos;
	
	/**
	 * simulating a Java Component
	 */
	int xPos, yPos, breedte, hoogte;
	
	/**
	 * the CommandComponents contained in this CommandContainer
	 */
	Vector<Object> components = new Vector();

	/**
	 * the CommandComponent owning this CommandContainer, duplicating owner 
	 */
	CommandComponent parent = null;
	
	/**
	 * name of this CommandContainer for testing
	 */
	String containerName = "";
	
	/**
	 * are the components in this CommandContainer visible? used for Deeltaken when only header is displayed
	 */
	boolean componentsVisible = true;
		
	/**
	 * constructor; note that constructor sets owner and that parent is set after owner creates 
	 * this CommandContainer; this is redundant code
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param cc CommandComponent owning this CommandContainer 
	 */
	public CommandContainer(int x, int y, int b, int h, CompositeCommandComponent cc)
	{	
		xPos = x; yPos = y; breedte = b; hoogte = h;
		owner = cc;
		// we assume we create this object at its minimum height
		minimumHeight = h;
		caretUp = false;
		insertPos = 0; 
	}
	
	/**
	 * simulating a Java Container
	 * @return number of CC's in this CommandContainer
	 */
	public int getComponentCount()
	{	return components.size();
	}
	
	/**
	 * add an Object (always a CC) at position insertPos
	 * @param o Object to be added
	 * @param insertPos insert position
	 */
	public void add(Object o, int insertPos)
	{	components.insertElementAt(o, insertPos);
	}
	
	/**
	 * get the CC at index "index"
	 * @param index index of CC
	 * @return CC at this index
	 */
	public Object getComponent(int index)
	{	return components.elementAt(index);
	}
	
	/**
	 * simulating a Java Component
	 * @return size of this CContainer
	 */
	public Dimension getSize()
	{	return new Dimension(breedte,hoogte);
	}

	/**
	 * simulating a Java Component, the 
	 * CommandComponents inside this CContainer only need a new width
	 * @param b new width
	 * @param h new height
	 */
	public void setSize(int b, int h)
	{	breedte = b; hoogte = h;
		int maxCnt = getComponentCount();
		for (int cCnt = 0; cCnt < maxCnt; cCnt++)
		{	CommandComponent c = (CommandComponent) components.elementAt(cCnt);
			c.setWidth(b);
		}
	}

	/**
	 * simulating a Java Component, do not forget to move the 
	 * CommandComponenets inside this CContainer 
	 * @param x new x-position
	 * @param y new y-position
	 */
	public void setLocation(int x, int y)
	{	int dx = x - xPos;
		int dy = y - yPos;
		xPos = x; yPos = y;
		int maxCnt = getComponentCount();
		for (int cCnt = 0; cCnt < maxCnt; cCnt++)
		{	CommandComponent c = (CommandComponent) components.elementAt(cCnt);
			c.setLocation(c.getX()+dx, c.getY()+dy);
		}
	}

	/**
	 * simulating a Java Component, do not forget the CC's
	 * inside this CContainer
	 * @param x new x-position
	 * @param y new y-position
	 * @param b new width
	 * @param h new height
	 */
	public void setBounds(int x, int y, int b, int h)
	{	setLocation(x,y);
		setSize(b,h);
		
	}

	/**
	 * simulating a Java Container
	 * @return location of this CContainer
	 */
	public Point getLocation()
	{	return new Point(xPos, yPos);
	}

	/**
	 * simulating a Java Container
	 * @return height of this CContainer
	 */
	public int getHeight()
	{	return hoogte;
	}

	/**
	 * simulating a Java Container
	 * @param h new height of this CContainer
	 */
	public void setHeight(int h)
	{	hoogte = h;
	}

	/**
	 * simulating a Java Container
	 * @return width of this CContainer
	 */
	public int getWidth()
	{	return breedte;
	}
	/**
	 * simulating a Java Container
	 * @return x-position of this CContainer
	 */
	public int getX()
	{	return xPos;
	}

	/**
	 * getter for owner
	 * @return owner
	 */
	public CompositeCommandComponent getOwner()
	{	return owner;
	}

	/**
	 * add a CC at the current value of insertPos, set the width and parent of this CC
	 * and rearrange this CContainer
	 * @param cc CC to be inserted
	 */
	public void addCComponent(CommandComponent cc)
	{	
		cc.setBounds(xPos,yPos, getSize().width, cc.getSize().height);
		add(cc, insertPos);
		cc.parent = this;
		reArrange();
	}

	/**
	 * add a CC as last CC in this CContainer, set the width and parent of this CC
	 * and rearrange this CContainer
	 * @param cc CC to be inserted
	 */
	public void addCCompAtBottom(CommandComponent cc)
	{
		cc.setBounds(xPos,yPos, getSize().width, cc.getSize().height);
		components.addElement(cc);
		cc.parent = this;
		reArrange();
	}
	
	/**
	 * check if this CContainer contains a CC that is NOT equal to CC sc and that contains 
	 * the coordinates (x,y); note that if a CC contains (x,y) it can be a composite containing
	 * other CC's containing (x,y), so find the "deepest" CC in the tree   
	 * @param x x-coordinate for search
	 * @param y y-coordinate for search
	 * @param sc CC to be excluded
	 * @return CC sough or null
	 */
	public CommandComponent findCComponentAt(int x, int y, CommandComponent sc)
	{
		CommandComponent result = null;
		for (int cCnt = 0; cCnt < components.size(); cCnt++)
		{	Object o = components.elementAt(cCnt);
			CommandComponent tResult = null;
			if (o instanceof CommandComponent)
			{	CommandComponent cc = (CommandComponent) o;
				if ((cc != sc) && cc.contains(x,y))
				{	result = cc;
					tResult = cc.findCComponentAt(x, y, sc);
					if (tResult != null)
						result = tResult;
				}
			}
		}
		return result;
	}	

	/**
	 * check if this CContainer contains a CC containing a CContainer that contains 
	 * the coordinates (x,y); note that CC must be a LoopCC or a 
	 * KeuzeCC and that the latter can contain two CContainers
	 * note that if a CC contains (x,y) it can be a composite containing
	 * another CContainer containing (x,y), so find the "deepest" CContainer in the tree
	 * @param x x-coordinate for search
	 * @param y y-coordinate for search
	 * @return CContainer sought or null
	 */
	public CommandContainer findCContainerAt(int x, int y)
	{
		CommandContainer result = null;
		for (int cCnt = 0; cCnt < components.size(); cCnt++)
		{
			Object o = components.elementAt(cCnt);
			CommandContainer tResult = null;
			if ((o instanceof LoopCommandComponent) && ((CommandComponent) o).contains(x, y))
			{	LoopCommandComponent lcc = (LoopCommandComponent) o;
				if (lcc.loopBlock.contains(x,y))
				{	result = lcc.loopBlock;
					tResult = lcc.loopBlock.findCContainerAt(x,y);
					if (tResult != null)
					{	result = tResult;
					}
				}	
			}	
			else if ((o instanceof KeuzeCommandComponent) && ((CommandComponent) o).contains(x, y))
			{	KeuzeCommandComponent kcc = (KeuzeCommandComponent) o;
				if (kcc.ifBlock.contains(x,y))
				{	result = kcc.ifBlock;
					tResult = kcc.ifBlock.findCContainerAt(x, y);
					if (tResult != null)
					{	result = tResult;
					}
				}
				else if ((kcc.elseBlock != null) && kcc.elseBlock.contains(x, y))
				{
					result = kcc.elseBlock;
					tResult = kcc.elseBlock.findCContainerAt(x, y);
					if (tResult != null)
					{	result = tResult;
					}
				}
			}
		}
		return result;
		
	}

	/**
	 * check if this CContainer contains the point (x,y)
	 * @param x x to be checked
	 * @param y y to be checked
	 * @return true/false
	 */
	public boolean contains(int x, int y)
	{
		Rectangle rect = new Rectangle(xPos, yPos, breedte, hoogte);
		return rect.contains(x, y);
	}
	
	/**
	 * remove the caret of this CContainer (if any) and any
	 * carets of the CC's contained in this CContainer 
	 */
	public void removeCaret()
	{
		caretUp = false;
		for (int cnt = 0; cnt < getComponentCount(); cnt++)
		{	CommandComponent c = (CommandComponent) components.elementAt(cnt);
			c.removeCaret();
		}
//System.out.println("CCont removeCaret " + containerName);		
	}
	/**
	 * Set the caret on the CommandContainer itself.
	 * This is called by JavaLogoSchuifVeld.traceComponent when the dragged CC is hovering
	 * over the empty space of the container. Set Caret at the top of the container if it
	 * is empty, or set caret at the bottom of the last CC if CContainer is not empty.
	 * Note: in the latter case insertPos will be set through call from CommandComponent.setCaret: setInsert
	 * @param y	y-position of mouse on hovering CC. x is irrelevant
	 */
	public void setCaret(int y)
	{
		//CContainer is empty
		if (getComponentCount() == 0)
		{
			caretUp = true;
			insertPos = 0;
		} 
		else
		{	// last CC
			Object c = getComponent(getComponentCount()-1);
			((CommandComponent) c).setCaret(y);
		}
	}

	/**
	 * Set the insertPos for this container when caret is set on a CC inside it
	 * @param commandComponent		the CC that gets a caret
	 * @param downcaret				top or bottom, causes a difference of 1 in insertPos
	 */
	public void setInsert(CommandComponent commandComponent, boolean downcaret)
	{
		for (int i = 0; i < getComponentCount(); i++)
		{	if (getComponent(i) == commandComponent)
			{	insertPos = i;
				if (downcaret) 
					insertPos++;
				return;
			}
		}
		insertPos = 0; 
	}
	
	/**
	 * remove all CC's form this CContainer, rearrange
	 * to adapt height
	 */
	public void removeAll()
	{	components.removeAllElements();
		insertPos = 0; 
		reArrange();
	}
	
	/**
	 * remove Object c (always a CC) from this CContainer; rearrange
	 * @param c Object (CC) to be removed
	 */
	public void remove(Object c)
	{	
		components.remove(c);
		reArrange();
		if (c instanceof CommandComponent)
		{	((CommandComponent) c).parent = null;
			
		}
	}
		
	/**
	 * Set the minimum height. This method should not be called on containers for control structures,
	 * their height should be equal to the sum of heights of the CC's inside (with small minimum)
	 * Must be called for deeltaakbodies, when resize arrows are used.
	 * @param h the new minimum height
	 */
	public void setMinimumHeight(int h)
	{	minimumHeight = h;
	}
	
	/**
	 * simulating a Java Component, the 
	 * CommandComponenets inside this CContainer only need a new width
	 * @param b new width
	 */
	public void setWidth(int b)
	{	
		breedte = b; 
		for (int i = 0; i < getComponentCount(); i++)
		{	Object c = getComponent(i);
			if(c instanceof CommandComponent)
			{	CommandComponent cc = (CommandComponent) c;
				cc.setWidth(b);
			
			}
		}
	}
	
	/**
	 * Reposition the CC's and calculate new contentHeight after inserting or deleting of a CC.
	 * Notify owner of the change, so it can change its own height.
	 */
	public void reArrange()
	{	
		int hoogte = 0;
		// vertically realign the CC's 
		for (int i = 0; i < getComponentCount(); i++)
		{	Object c = getComponent(i);
			if (c instanceof CommandComponent)
			{	CommandComponent cc = (CommandComponent) c;
				cc.setLocation(xPos,yPos+hoogte);
				hoogte += cc.getSize().height-2;
			}
		}
		contentHeight = hoogte;
		// find and set new height of this CommandContainer
		int h = Math.max(contentHeight+12, minimumHeight);
		setSize(getSize().width, h);
		// notify owner
		owner.containerHeightChanged(h);
	}
	
	/**
	 * getter for contentHeight
	 * @return contentHeight
	 */
	int getContentHeight()
	{
		return contentHeight;
	}
	
	/**
	 * paint the CommandContainer and its CC's (if visible)
	 * @param g Contect2d for painting
	 */
	protected void paintComponent(Context2d g)
	{	// yellowish background	
		g.setFillStyle(CssColor.make(255,255,220));
		g.fillRect(xPos+1,yPos+1,getSize().width-1,getSize().height-1);
		// outline in black
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.strokeRect(xPos,yPos,getSize().width-1,getSize().height-1);
		//g.strokeRect(xPos+1,yPos+1,getSize().width-3,getSize().height-3);
		if(caretUp)
		{	
			g.setStrokeStyle(CssColor.make(0,255,0));
			g.beginPath();
			g.moveTo(xPos+2,yPos+2);
			g.lineTo(xPos+getSize().width-3,yPos+2);
			g.stroke();
			
			g.beginPath();
			g.moveTo(xPos+2,yPos+3);
			g.lineTo(xPos+getSize().width-3,yPos+3);
			g.stroke();
			// omit this, see paintCaret() in class CommandComponent
			//caretUp = false;
		}
		// paint CC's
		if (componentsVisible)
		{	for (int cCnt = 0; cCnt < components.size(); cCnt++)
			{	((CommandComponent) components.elementAt(cCnt)).paintComponent(g);
			}
		}
	}

	/**
	 * create a String containing all the code from the CC's inside
	 * this CContainer, each code line prefixed by String tab
	 * @param tab prefix String
	 * @return code String
	 */
	public String getCode(String tab)
	{
		String s = "";
		for(int i=0 ; i<getComponentCount(); i++)
		{	Object c = getComponent(i);
			if(c instanceof CommandComponent)
			{	s = s +((CommandComponent)c).getCode(tab);
			}
		}
		return s;
	}
}
