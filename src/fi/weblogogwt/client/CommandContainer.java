package fi.weblogogwt.client;

//import java.awt.*;

//import javax.swing.JPanel;

/**
 * CommandContainer is a JPanel that will hold a list of CommandComponents, corresponding to a 'block'
 * in programming languages. It is used by HerhaalCC, KeuzeCC, DeeltaakCC, ProgrammaCC...
 * Note: CommandComponent is NOT a CommandComponent, it can not be manipulated apart from its owner.
 * 
 * Height of the container is always (at least) the sum of the heights of its components.
 * Scrolling will be managed by the owner CommandComponent
 * 
 * @author Berge020
 */
import java.util.*;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;

public class CommandContainer // extends JPanel
{
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
	 * position for the insertion of a new CC, or -1 if there is none and the CC should go as last.
	 */
	private int insertPos;
	
	int xPos, yPos, breedte, hoogte;
	
	Vector<Object> components = new Vector();
	
	CommandComponent parent = null;
	
	String containerName = "";
	
	boolean componentsVisible = true;
		
	public CommandContainer(int x, int y, int b, int h, CompositeCommandComponent cc)
	{	
		//setBounds(x,y,b,h);
		xPos = x; yPos = y; breedte = b; hoogte = h;
		//setLayout(null);
		owner = cc;
		// we assume we create this object at its minimum height
		minimumHeight = h;
		caretUp = false;
		insertPos = 0; //-1;
	}
	
	public int getComponentCount()
	{
		return components.size();
	}
	
	public void add(Object o, int insertPos)
	{
		
//System.out.println("add " + insertPos);		
		components.insertElementAt(o, insertPos);
	}
	
	public Object getComponent(int index)
	{
		return components.elementAt(index);
	}
	
	public Dimension getSize()
	{
		return new Dimension(breedte,hoogte);
	}

	public void setSize(int b, int h)
	{
		breedte = b; hoogte = h;
		int maxCnt = getComponentCount();
		for (int cCnt = 0; cCnt < maxCnt; cCnt++)
		{	CommandComponent c = (CommandComponent) components.elementAt(cCnt);
			c.setWidth(b);
		}

	}

	public void setLocation(int x, int y)
	{

		int dx = x - xPos;
		int dy = y - yPos;
		xPos = x; yPos = y;
		int maxCnt = getComponentCount();
		for (int cCnt = 0; cCnt < maxCnt; cCnt++)
		{	CommandComponent c = (CommandComponent) components.elementAt(cCnt);
			c.setLocation(c.getX()+dx, c.getY()+dy);
		}
	}

	public void setBounds(int x, int y, int b, int h)
	{
		
		
		//xPos = x; yPos = y; breedte = b; hoogte = h;
		setLocation(x,y);
		setSize(b,h);
		
	}

	public Point getLocation()
	{
		return new Point(xPos, yPos);
	}

	public int getHeight()
	{
		return hoogte;
	}

	public void setHeight(int h)
	{
		hoogte = h;
	}

	public int getWidth()
	{
		return breedte;
	}

	public int getX()
	{
		return xPos;
	}

	public CompositeCommandComponent getOwner()
	{
		return owner;
	}
	
	public void addCComponent(CommandComponent cc)
	{	
		
//System.out.println("CCont addCC " + getComponentCount());

		cc.setBounds(xPos,yPos, getSize().width, cc.getSize().height);
		//super.add(cc, insertPos);
		add(cc, insertPos);
		 
//WebLogoGWT.logger.info("CCont addCC " + cc.commandName + " at " + insertPos);

		cc.parent = this;
		reArrange();
	}

	public void addCCompAtBottom(CommandComponent cc)
	{
		cc.setBounds(xPos,yPos, getSize().width, cc.getSize().height);
		components.addElement(cc);
		
//WebLogoGWT.logger.info("CCont addCC " + cc.commandName + " at bottom");		
		cc.parent = this;
		reArrange();
	}
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
//System.out.println("in loopblock");				
					tResult = lcc.loopBlock.findCContainerAt(x,y);
					if (tResult != null)
					{	result = tResult;
//System.out.println("tResult != null");					
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

	public boolean contains(int x, int y)
	{
		Rectangle rect = new Rectangle(xPos, yPos, breedte, hoogte);
		return rect.contains(x, y);
	}
	
	
	/**
	 * Set the caret on the CommandContainer itself.
	 * This is called by JavaLogoSchuifVeld.traceComponent when the dragged CC is hovering
	 * over the empty space of the container. Set Caret at the top of the container if it
	 * is empty, or at the bottom of the last CC if it is not.
	 * Note: in the latter case insertPos will be set through call from CommandComponent.setCaret: setInsert
	 * 
	 * @param y		y-pos of mouse on hovering CC. x is irrelevant
	 */
	public void setCaret(int y)
	{
		if (getComponentCount() == 0)
		{
			caretUp = true;
			insertPos = 0;//-1;
//System.out.println("CC setCaret cnt == 0");			
		} 
		else
		{
			//Component c = getComponent(getComponentCount()-1);
			Object c = getComponent(getComponentCount()-1);
			((CommandComponent) c).setCaret(y);
//System.out.println("CC setCaret cnt > 0");			
		}
	}

	/**
	 * Set the insertPos for this container when caret is set on a CC inside it
	 * 
	 * @param commandComponent		the CC that gets a caret
	 * @param downcaret				top or bottom, causes a difference of 1 in insertPos
	 */
	public void setInsert(CommandComponent commandComponent, boolean downcaret)
	{
		for (int i = 0; i < getComponentCount(); i++)
		{
			if (getComponent(i) == commandComponent)
			{
				insertPos = i;
				if (downcaret) 
					insertPos++;
				
//System.out.println("cc " + insertPos);

				return;
			}
		}
		insertPos = 0; //-1;
	}
	
	public void removeAll()
	{
		components.removeAllElements();
		insertPos = 0; //-1;
		reArrange();
	}
	
	//public void remove(Component c)
	public void remove(Object c)
	{	
//System.out.println("remove " + containerName);		
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
	 * 
	 * @param h the new minimum height
	 */
	public void setMinimumHeight(int h)
	{
		minimumHeight = h;
	}
	
	public void setWidth(int b)
	{	
		
//System.out.println("" + containerName + " " + b);		
		breedte = b; 
		for (int i = 0; i < getComponentCount(); i++)
		{	//Component c = getComponent(i);
			Object c = getComponent(i);
			if(c instanceof CommandComponent)
			{	CommandComponent cc = (CommandComponent) c;
				cc.setWidth(b);
			
			}
		}
		//setSize(b, hoogte);
	}
	
	/**
	 * Reposition the CC's and calculate contentHeight after insert/delete of a CC.
	 * Notify owner of the change, so it can change its own height.
	 */
	public void reArrange()
	{	
		
		int hoogte = 0;
		for (int i = 0; i < getComponentCount(); i++)
		{	//Component c = getComponent(i);
			Object c = getComponent(i);
			if (c instanceof CommandComponent)
			{	CommandComponent cc = (CommandComponent) c;
				//getComponent(i).setLocation(0,hoogte);
				cc.setLocation(xPos,yPos+hoogte);
				//hoogte += getComponent(i).getSize().height-2;
				hoogte += cc.getSize().height-2;
				
			}
		}
		contentHeight = hoogte;
		// hoogte is som van de hoogte van zijn CComponenten, met minimum
		int h = Math.max(contentHeight+12, minimumHeight);
		setSize(getSize().width, h);
		owner.containerHeightChanged(h);
		
		
	}
	
	int getContentHeight()
	{
		return contentHeight;
	}
	
	
	//protected void paintComponent(Graphics g)
	protected void paintComponent(Context2d g)
	{
		
		//g.setColor(new Color(255,255,220));
		g.setFillStyle(CssColor.make(238,238,170));
		g.fillRect(xPos+1,yPos+1,getSize().width-1,getSize().height-1);
		
		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		//g.drawRect(0,0,getSize().width-1,getSize().height-1);
		g.strokeRect(xPos,yPos,getSize().width-1,getSize().height-1);
		//g.drawRect(1,1,getSize().width-3,getSize().height-3);
		g.strokeRect(xPos+1,yPos+1,getSize().width-3,getSize().height-3);
		if(caretUp)
		{	
			//g.setColor(Color.green);
			g.setStrokeStyle(CssColor.make(0,255,0));
			//g.drawLine(2,2,getSize().width-3,2);
			g.beginPath();
			g.moveTo(xPos+2,yPos+2);
			g.lineTo(xPos+getSize().width-3,yPos+2);
			g.stroke();
			
			//g.drawLine(2,3,getSize().width-3,3);
			g.beginPath();
			g.moveTo(xPos+2,yPos+3);
			g.lineTo(xPos+getSize().width-3,yPos+3);
			g.stroke();
			
			caretUp = false;
		}
		
		if (componentsVisible)
		{	
			for (int cCnt = 0; cCnt < components.size(); cCnt++)
			{
				((CommandComponent) components.elementAt(cCnt)).paintComponent(g);
			}
		}

	}
	
	public String getCode(String tab)
	{
		String s = "";
		for(int i=0 ; i<getComponentCount(); i++)
		{	//Component c = getComponent(i);
			Object c = getComponent(i);
			if(c instanceof CommandComponent)
			{	
				s = s +((CommandComponent)c).getCode(tab);
			}
		}
		return s;
	}
}
