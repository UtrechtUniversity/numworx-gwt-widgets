package fi.weblogogwt.client;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.parameters.TAParameter;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * superclass for the for-loop and while-loop command containing a CommandContainer for the code
 * within the loop
 */
public abstract class LoopCommandComponent extends CompositeCommandComponent implements ParameterEditorListener
{
	/**
	 * the loop condition
	 */
	protected TAParameter loopCondition;
	/**
	 * CContainer containing the code within the loop
	 */
	protected CommandContainer loopBlock;
	/**
	 * x-offset of loopBlock (relative to the x-position of this LoopCC) 
	 */
	public static final int blockX = 25;
	/** 
	 * y-offset of loopBlock (relative to the y-position of this LoopCC)
	 */
	public static final int blockY = 25;

	/**
	 * loop condition being edited?
	 */
	private boolean isEditing = false;
	
	/**
	 * PopupPanel for editing loop condition, see class ParameterTextField 
	 */
	private ParameterTextField loopEditor;
	/**
	 * text after loop condition: NL: "keer" or "herhaal", EN: "times" or "repaeat"
	 */
	protected String naString;
	/**
	 * not used
	 */
	protected String naStringTranslated;
	
	/**
	 * constructor
	 * @param x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param sv instance of JavaLogoSchuifVeld for drawing
	 */
	public LoopCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		loopBlock = new CommandContainer(xPos+blockX, yPos+blockY, b-blockX, h-JavaLogoSchuifVeld.ccsh, this);
		loopBlock.parent = this;
		// for testing
		loopBlock.containerName = "loop";
	}

	/**
	 * 1. check if loopBlock contains a CC not equal to sc containing the coordinates (x,y), take the deepest CC in the tree
	 * 2. else check if the non-loopBlock part does not equal sc and contains (x,y)
	 * @return CC found or null
	 */
	public CommandComponent findCComponentAt(int x, int y, CommandComponent sc)
	{
		if (loopBlock.contains(x,y))
		{	
			return loopBlock.findCComponentAt(x,y,sc);
		}
		else if ((sc != this) && contains(x,y)) 
			return this;
		else
			return null;
	}

	/**
	 * check if loopBlock contains a CContainer containing the coordinates (x,y)
	 * @return CContainer found or null
	 */
	public CommandContainer findCContainerAt(int x, int y)
	{
		if (loopBlock.contains(x,y))
			return loopBlock.findCContainerAt(x,y);
		else
			return null;
	}
 
	/**
	 * add a CC at the bottom of loopBlock, used by ProgrammaImporter
	 */
	void addCComponent(CommandComponent cc)
	{	
		loopBlock.addCCompAtBottom(cc);
	}

	/**
	 * simulating a Java Component, also sets the new size of the loopBlock
	 */
	public void setSize(int w, int h)
	{	
		loopBlock.setWidth(w-blockX);
		super.setSize(w,h);
	}

	/**
	 * simulating a Java Component, also sets the new width of the loopBlock
	 */
	public void setWidth(int w)
	{	
		loopBlock.setWidth(w-blockX);
		super.setWidth(w);
	}

	/**
	 * simulating a Java Component, also sets the new width of the loopBlock
	 */
	public void setBounds(int x, int y, int w, int h)
	{	// check if loopBlock already exists
		if ( loopBlock != null )		
		{	loopBlock.setWidth(w-blockX);			
		}
		super.setBounds(x,y,w,h);
	}

	/**
	 * simulating a Java Component, also sets the new location of the loopBlock
	 */
	public void setLocation(int x, int y)
	{
		xPos = x; yPos = y;
		// check if loopBlock already exists
		if (loopBlock != null)
			loopBlock.setLocation(xPos+blockX, yPos+blockY);
	}

	/**
	 * callback from loopBlock whose height has been changed; set the new height and 
	 * rearrange the parent CContainer containing this loopBlock
	 */
	void containerHeightChanged(int h)
	{
		super.setSize(getWidth(), h+blockY);
		parent.reArrange();
	}
	
	/**
	 * move this loopCC over (dx,dy), redefined setLocation should be used 
	 */
	public void moveComponent(int dx, int dy)
	{	
		int x = startCompx + dx;				
		int y = startCompy + dy;		
		if (schuifveld.isGesloten())
		{	x = Math.max(0, Math.min(x, schuifveld.getSize().width-getSize().width));
			y = Math.max(0, Math.min(y, schuifveld.getSize().height-getSize().height));
		}
		setLocation(x,y);
	}

	/**
	 * redefined from class CommandComponent: when dragging the width of this loopCC is made smaller,
	 * do the same for the loopBlock
	 */
	public void mouseDragged(int x, int y, int modifiers)
	{
		super.mouseDragged(x, y, modifiers);
		if (dragging)
		{	loopBlock.setHeight(Math.max(loopBlock.getHeight(), JavaLogoSchuifVeld.ccsh));
			loopBlock.setWidth(breedte - blockX);
		}
	}
	/**
	 * Set loop count directly (programmaImporter)
	 * @param s loop count as String
	 */
	void setLoopCount(String s)
	{
		loopCondition.setParameter(s);
	}
	
	/**
	 * new parameter String (from PopupPanel) is text
	 */
	public void parameterEdited(String text)
	{
		loopCondition.setParameter(text);
		isEditing = false;
		// remove PopupPanel
		if (loopEditor != null)
		{	loopEditor.hide();
		
		}
		schuifveld.paint();
	}

	/**
	 * start editing loop condition if top part of loopCC was clicked
	 */
	public void parameterComponentClicked(int x, int y)
	{
		if (y < blockY )
		{
			isEditing = true;
			showLoopEditor();
		}
		schuifveld.paint();
	}

	/**
	 * show the PopupPanel for editing loop condition; first check if any other
	 * PopupPanel is open, process and close this
	 */
	public void showLoopEditor()
	{
		int popupX = xPos + schuifveld.getAbsoluteLeft();
		int popupY = yPos + blockY + schuifveld.getAbsoluteTop();
		if ((schuifveld.paramEditor != null) && schuifveld.paramEditor.isVisible())
		{
			schuifveld.paramEditor.owner.parameterEdited(schuifveld.paramEditor.getText());
		}
		schuifveld.paramEditor = new ParameterTextField(breedte, hoogte, this, schuifveld);
		loopEditor = schuifveld.paramEditor; 
		loopEditor.vulIn(loopCondition.getParameterText());
		loopEditor.setPopupPosition(popupX, popupY);
		loopEditor.show();
		loopEditor.textBox.setFocus(true);
	}

	/**
	 * paint background and outline, color depending on tracing
	 */
	protected void paintBackground(Context2d g)
	{
		if(traceKleur)
		{	g.setFillStyle(traceActiveColor);
			if (traceKleurCnt >= 2)
				traceKleur = false;
		} 
		else
		{	// orange
			g.setFillStyle(CssColor.make(255, 127, 0));
		}	
		g.fillRect(xPos+0,yPos+0,getSize().width-1,getSize().height-1);
		// black;
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.strokeRect(xPos+0,yPos+0,getSize().width-1,getSize().height-1);
		g.strokeRect(xPos+1,yPos+1,getSize().width-3,getSize().height-3);
	}

	/**
	 * paint the loop condition and the loopBlock
	 */
	protected void paintCommand(Context2d g)
	{
		g.setFont(WebLogoGWT.fontString);
		// black
		g.setFillStyle(CssColor.make(0,0,0));
		if (isEditing )
		{
			g.fillText(commandName+" ",xPos+10, yPos+18);
			TextMetrics tm = g.measureText(commandName);
			int textWidth = (int) Math.round(tm.getWidth());
			g.fillText(naString, xPos+textWidth + 40, yPos+18);
		} 
		else // adapt width of text to width of loopCC
		{
			if (!loopCondition.isCorrect())
				g.setFillStyle(CssColor.make(255,0,0));
			String fullText = commandName+" "+loopCondition.getParameterText()+naString;
			TextMetrics tm = g.measureText(fullText);
			int textWidth = (int) Math.round(tm.getWidth());
			if (textWidth > breedte - 10)
			{	
				// omit characters until fit
				fullText = fullText.substring(0, fullText.length() - 1);
				tm = g.measureText(fullText);
				textWidth = (int) Math.round(tm.getWidth());
				while (textWidth > breedte - 10)
				{
					fullText = fullText.substring(0, fullText.length() - 1);
					tm = g.measureText(fullText);
					textWidth = (int) Math.round(tm.getWidth());
				}
				g.fillText(fullText,xPos+10,yPos+18);
/*				
				tm = g.measureText(commandName);
				textWidth = (int) Math.round(tm.getWidth()); 
				if (textWidth > breedte - 10)
				{	g.fillText(commandName.substring(0,1),xPos+10,yPos+18);
				}
				else
					g.fillText(commandName,xPos+10,yPos+18);
*/					
			}
			else // display full text
				g.fillText(fullText,xPos+10, yPos+18);
		}
		// paint loopBlock
		if (loopBlock != null)
			loopBlock.paintComponent(g);
	}
	
	/**
	 * see subclasses
	 * @param trb the TraceBeheerder
	 * @param ub the drawing area
	 * @param varSet the current variable set
	 * @return true/false depending on successfull execution  
	 */
	public abstract boolean executeContent(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet);
	
	/**
	 * execute the commands in loopBlock, temporarily increase the level in the execution stack;
	 * see class VarSet
	 */
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		varSet.increaseLevel("-- in loop", false);
		boolean b = executeContent(trb, ub, varSet);
		varSet.decreaseLevel();
		return b;
	}
		
	/**
	 * create a String containing the loop condition and the code from the loop block, all
	 * lines prefixed by String tab; note the format used and see class ProgrammaImporter
	 */
	public String getCode(String tab)
	{	String s = tab + commandName + " " + loopCondition.getParameterText()+naString + "\n" + tab +"{\n";
		String tabNieuw = tab + "    ";
		s= s+ loopBlock.getCode(tabNieuw);
		s = s + tab + "}\n";
		return s;
	}

}
