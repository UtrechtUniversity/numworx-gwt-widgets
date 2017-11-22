package fi.weblogogwt.client;


import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.CommandComponent;
import fi.weblogogwt.client.VarSet;
import fi.weblogogwt.client.parameters.BooleanParameter;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * class implementing the if-else command; note that the else-block must be added (removed) by pressing the down arrow
 * (up arrow); note also that the CommandContainer elseBlock equals null if it is not there (in Java it is just added/removed 
 */

public class KeuzeCommandComponent extends CompositeCommandComponent implements ParameterEditorListener //, ActionListener
{
	/**
	 * the if-else condition
	 */
	private BooleanParameter condition;
	
	private String alsString = WebLogoGWT.rb.alsTekst(); 
	private String andersString = WebLogoGWT.rb.andersTekst(); 
	/**
	 * x-position of if-block
	 */
	public static final int blockX = 25;
	/**
	 * y position of if-block
	 */
	public static final int ifBlockY = 25;
	
	/**
	 * flagg for addingt commands to if or else-block (ProgrammaImporter)
	 */
	private boolean inIfBlock = true;
	
	/**
	 * the if-block
	 */
	CommandContainer ifBlock;
	/**
	 * the else-block
	 */
	CommandContainer elseBlock;
	
	/**
	 * flagg for editing
	 */
	private boolean isEditing = false;
	
	/**
	 * PopupPanel for editing condition 
	 */
	private ParameterTextField conditionEditor;
	
	/**
	 * x-position of condition text relative to xPos
	 */
	private int conditionEditorX;
	
	/**
	 * flagg for presence of else-block
	 */
	private boolean elseVisible = false;
	/**
	 * sensitive rectangle for pressing up or down arrow 
	 */
	Rectangle elseVisibleButton;
	/**
	 * up and down arrow
	 */
	Polygon arrowDown, arrowUp;
	
	/**
	 * constructor
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param sv instance of JavaLogoSchuifVeld
	 */
	public KeuzeCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		commandName = "Keuze";
		condition = new BooleanParameter();

		// create if-block
		ifBlock = new CommandContainer(xPos+blockX, yPos+ifBlockY, blockWidth(), h-JavaLogoSchuifVeld.ccsh, this);
		ifBlock.parent = this;
		ifBlock.containerName = "if";
		// create else-block if necessary
		if (elseVisible)
		{	elseBlock = new CommandContainer(xPos+blockX, yPos+elseBlockY(), blockWidth(), h-JavaLogoSchuifVeld.ccsh, this);
			elseBlock.parent = this;
			elseBlock.containerName = "else";
		}
		// determine conditionEditorX
		schuifveld.jlsvContext2d.setFont(WebLogoGWT.fontString);
		TextMetrics tm = schuifveld.jlsvContext2d.measureText("Als ");
		int width = (int) Math.round(tm.getWidth()); 
		conditionEditorX = 10+width;
		// set press rectangle
		elseVisibleButton = new Rectangle(xPos+5, yPos+ifBlockY + 2, 15, 15);
		// create arrow Polygons
		createVArrows();
	}

	/**
	 * create up and down arrow Polygons
	 */
	private void createVArrows()
	{
		arrowDown = new Polygon();
		arrowDown.addPoint(elseVisibleButton.x, elseVisibleButton.y);
		arrowDown.addPoint(elseVisibleButton.x + 15, elseVisibleButton.y);
		arrowDown.addPoint(elseVisibleButton.x + 7, elseVisibleButton.y + 15);
		arrowUp = new Polygon();
		arrowUp.addPoint(elseVisibleButton.x + 7, elseVisibleButton.y);
		arrowUp.addPoint(elseVisibleButton.x + 15, elseVisibleButton.y+15);
		arrowUp.addPoint(elseVisibleButton.x, elseVisibleButton.y+15);
	}

	/**
	 * create the else-block or remove it by nulling it
	 * @param b true/false
	 */
	public void setElseVisible(boolean b)
	{
		elseVisible = b;
		if (elseVisible)
		{	elseBlock = new CommandContainer(xPos+blockX, yPos+elseBlockY(), blockWidth(), JavaLogoSchuifVeld.ccsh, this);
			elseBlock.parent = this;
			elseBlock.containerName = "else";
		}
		else
		{	elseBlock = null;
		}
	}
	
	/**
	 * find the y-posistion of elseBlock: depends on content of ifBlock 
	 * @return y-position of elseBlock	
	 */
	public final int elseBlockY()
	{	// 2 headers
		return 2*ifBlockY + ifBlock.getHeight();
	}
	
	/**
	 * find the width of ifBlock and elseBlock
	 * @return width of ifBlock and elseBlock
	 */
	public final int blockWidth()
	{
		return getWidth()-blockX+1;
	}
	
	/**
	 * PBgv: set the boolean expression (from programImporter).
	 * @param expression	the boolean expression as a String
	 */
	public void setBoolExpression (String expression)
	{
		condition.setParameter(expression);
	}

	/**
	 * Select a container for future adding of components. Only relevant for scripting (ProgrammaImporter)
	 * @param inIfBlock		true/false for obvious container selection
	 */
	public void setInIfBlock(boolean inIfBlock)
	{
		this.inIfBlock = inIfBlock;
	}

	/**
	 * Add a CommandComponent from source code, for instance by the ProgarammaImporter
	 * Note: Drag and drop will add a CC directly into one of the CommandContainers
	 * @param cc			CommandComponent to be added
	 */
	void addCComponent(CommandComponent cc)
	{	
		if ( inIfBlock )
		{
			ifBlock.addCCompAtBottom(cc);
		} 
		else
		{	
			elseBlock.addCCompAtBottom(cc);
		}
	}

	
	/**
	 * 1. check if ifBlock contains a CC not equal to sc containing the coordinates (x,y), take the deepest CC in the tree
	 * 2. check if elseBlock (if present) contains a CC not equal to sc containing the coordinates (x,y), take the deepest CC in the tree
	 * 3. else check if the non-if-elseBlock part does not equal sc and contains (x,y)
	 * @return CC found or null
	 */
	public CommandComponent findCComponentAt(int x, int y, CommandComponent sc)
	{
		if (ifBlock.contains(x,y))
		{	
			return ifBlock.findCComponentAt(x,y,sc);
		}
		else if ((elseBlock != null) && elseBlock.contains(x,y))
			return elseBlock.findCComponentAt(x,y,sc);
		else if ((sc != this) && contains(x,y))
			return this;
		else
			return null;
	}

	/**
	 * 1. check if ifBlock contains a CContainer containing the coordinates (x,y)
	 * 2. check if elseBlock (if prsent) contains a CContainer containing the coordinates (x,y)
	 * @return CContainer found or null
	 */
	public CommandContainer findCContainerAt(int x, int y)
	{
		if (ifBlock.contains(x,y))
			return ifBlock.findCContainerAt(x,y);
		else if ((elseBlock != null) && elseBlock.contains(x,y))
			return ifBlock.findCContainerAt(x,y);
		else
			return null;
		
	}

	/**
	 * redefined,since if- and elseBlock should also be moved
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
	 * redefined: intercept pressing the down- or up arrow and
	 * decrease height while dragging 
	 */
	public void mouseDragged(int x, int y, int modifiers)
	{
		if (arrowPressed)
			return;
		super.mouseDragged(x, y, modifiers);
		if (dragging)
			ifBlock.setHeight(JavaLogoSchuifVeld.ccsh);
	}

	/**
	 * adapt size and size of if- and elseBlock;
	 * redefine elseVisibleButton and recreate arrows 
	 */
	public void setSize(int w, int h)
	{	
		super.setSize(w,h);
		ifBlock.setWidth(w-blockX);
		// also move elseBlock vertically 
		if (elseBlock != null)
		{	elseBlock.setLocation(blockX, elseBlockY());
			elseBlock.setWidth(w-blockX);
		}	
		elseVisibleButton = new Rectangle(xPos+5, yPos + elseBlockY()-ifBlockY -18, 15, 15);
		createVArrows();
	}

	/**
	 * adapt the width and the width  of if- and elseBlock;
	 * redefine elseVisibleButton and recreate arrows 
	 */
	public void setWidth(int w)
	{	
		super.setWidth(w);
		ifBlock.setWidth(w-blockX);
		if (elseBlock != null)
		{	
			elseBlock.setWidth(w-blockX);
		}	
		elseVisibleButton = new Rectangle(xPos+5, yPos + elseBlockY()-ifBlockY -18, 15, 15);
		createVArrows();
	}

	/**
	 * change the bounds and the bounds of if- and elseBlock;
	 * redefine elseVisibleButton and recreate arrows 
	 */
	public void setBounds(int x, int y, int w, int h)
	{	
		super.setBounds(x,y,w,h);
		// constructor will call setBounds before containers are made
		if (ifBlock != null) 		
		{
			ifBlock.setWidth(w-blockX);
			if (elseBlock != null)
			{	elseBlock.setLocation(blockX, elseBlockY());
				elseBlock.setWidth(w-blockX);
			}	
			elseVisibleButton = new Rectangle(xPos+5, yPos + elseBlockY()-ifBlockY -18, 15, 15);
			createVArrows();
		}
	}
	
	/**
	 * adapt position and the position of if- and elseBlock;
	 * redefine elseVisibleButton and recreate arrows 
	 */
	public void setLocation(int x, int y)
	{
		xPos = x; yPos = y;
		if (ifBlock != null)
			ifBlock.setLocation(xPos+blockX, yPos+ifBlockY);
		if (elseBlock != null)
			elseBlock.setLocation(xPos+blockX, yPos+elseBlockY());
		elseVisibleButton = new Rectangle(xPos+5, yPos + elseBlockY()-ifBlockY -18, 15, 15);
		createVArrows();
	}

	/**
	 * redefined: one of if- or elseBlock has changed height
	 */
	void containerHeightChanged(int h)
	{
		if (elseVisible)
			super.setSize(getWidth(), 2*ifBlockY + ifBlock.getHeight() + elseBlock.getHeight());
		else
			super.setSize(getWidth(), ifBlockY + ifBlock.getHeight());
		parent.reArrange();
	}
	
	/**
	 * process text from for input PopupPanel,
	 * remove input PopupPanel
	 */
	public void parameterEdited(String text)
	{
		condition.setParameter(text);
		isEditing = false;
		if (conditionEditor != null)
		{	conditionEditor.hide();	
		}
		schuifveld.paint();
	}

	/**
	 * check if editing condition should be started 
	 * after clicking at (x,y); open input PopupPanel when necessary
	 */
	public void parameterComponentClicked(int x, int y)
	{
		isEditing = false;
		if ( y < ifBlockY )
		{
			isEditing = true;
			showConditionEditor();
		}
		schuifveld.paint();
	}

	/**
	 * show input PopupPanel for editing condition
	 */
	public void showConditionEditor()
	{
		int popupX = xPos + schuifveld.getAbsoluteLeft();
		int popupY = yPos + ifBlockY + schuifveld.getAbsoluteTop();
		// process and close any other open PopupPanal
		if ((schuifveld.paramEditor != null) && schuifveld.paramEditor.isVisible())
		{
			schuifveld.paramEditor.owner.parameterEdited(schuifveld.paramEditor.getText());
		}
		
		schuifveld.paramEditor = new ParameterTextField(breedte, hoogte, this, schuifveld);
		conditionEditor = schuifveld.paramEditor; 
		conditionEditor.vulIn(condition.getParameterText());
		conditionEditor.setPopupPosition(popupX, popupY);
		conditionEditor.show();
		conditionEditor.textBox.setFocus(true);
	}

	/** 
	 * paint background (orange) and border, note that
	 * if and else block are painted on top of this
	 */
	protected void paintBackground(Context2d g)
	{
		//orange
		g.setFillStyle(CssColor.make(255, 127, 0));
		if (traceKleur)
		{	g.setFillStyle(traceActiveColor);
		}
		g.fillRect(xPos+0, yPos+0, getSize().width-1, getSize().height-1);
		// black border
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.strokeRect(xPos+0, yPos+0, getSize().width-1, getSize().height-1);
		g.strokeRect(xPos+1, yPos+1, getSize().width-3, getSize().height-3);
		// Als/If
		g.setFont(WebLogoGWT.fontString);
		g.setFillStyle(CssColor.make(0,0,0));
		g.fillText(alsString, xPos+10, yPos+18);
		// Else
		if (elseVisible)
		{	g.fillText(andersString, xPos+10, yPos+ifBlockY+ifBlock.getHeight() +18);
		}
	}

	/**
	 * paint the text of the command, note that Als/If and Else are painted in paintBackground;
	 * if editing, paint only Als/If
	 */
	protected void paintCommand(Context2d g)
	{
		if ( isEditing ) return;	
		g.setFont(WebLogoGWT.fontString);
		if ( condition.isCorrect() )
		{ 	//black
			g.setFillStyle(CssColor.make(0,0,0));
		} 
		else
		{	// red
			g.setFillStyle(CssColor.make(255,0,0));
		}
		
		// Als/If is painted in paintBackground, condition starts at xPos+conditionEditorX 
		String conditionText = condition.getParameterText();
		TextMetrics tm = g.measureText(conditionText);
		int textWidth = (int) Math.round(tm.getWidth()) + conditionEditorX;
		if (textWidth > breedte - 10)
		{	
			// omit characters until fit
			conditionText = conditionText.substring(0, conditionText.length() - 1);
			tm = g.measureText(conditionText);
			textWidth = (int) Math.round(tm.getWidth()) + conditionEditorX;
			while (textWidth > breedte - 10)
			{
				conditionText = conditionText.substring(0, conditionText.length() - 1);
				tm = g.measureText(conditionText);
				textWidth = (int) Math.round(tm.getWidth()) + conditionEditorX;
			}
			g.fillText(conditionText,xPos+conditionEditorX,yPos+18);
			
		}
		else // Als/If + condition
			g.fillText(conditionText,xPos+conditionEditorX, yPos+18);

		if (ifBlock != null)
			ifBlock.paintComponent(g);
		if (elseBlock != null)
			elseBlock.paintComponent(g);

		// up arrow, only if KeuzeCC in program or subroutine
		if ((parent != null) && elseVisible)
		{	g.setFillStyle(CssColor.make(0,0,0));
			g.beginPath();		
			g.moveTo(arrowUp.doubleX[0], arrowUp.doubleY[0]);
			for (int k = 1; k < arrowUp.aantalPunten; k++) 
			{	g.lineTo(arrowUp.doubleX[k], arrowUp.doubleY[k]);
			}
			g.lineTo(arrowUp.doubleX[0], arrowUp.doubleY[0]);
			g.closePath();
			g.fill();
		}
		// down arrow, only if KeuzeCc in program or subroutine
		else if ((parent != null) && !elseVisible)
		{
			g.setFillStyle(CssColor.make(0,0,0));
			g.beginPath();		
			g.moveTo(arrowDown.doubleX[0], arrowDown.doubleY[0]);
			for (int k = 1; k < arrowDown.aantalPunten; k++) 
			{	g.lineTo(arrowDown.doubleX[k], arrowDown.doubleY[k]);
			}
			g.lineTo(arrowDown.doubleX[0], arrowDown.doubleY[0]);
			g.closePath();
			g.fill();
		}
	}
	
	/**
	 * execute the content of the if-else statement 
	 * @param trb trace controlling
	 * @param ub drawing area
	 * @param varSet current variable set
	 * @return true if succesfull
	 */
	public boolean executeContent(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		boolean value = condition.getValue();
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) 
		{
			trb.setCommandInfo(alsString+" "+condition.getValueText(), varSet);
			return traceKleur;
		}
		// execute if-block if condition is true
		if(value)
		{	for(int j=0 ; j<ifBlock.getComponentCount() ; j++)
			{	Object c = ifBlock.getComponent(j);
				if(c instanceof CommandComponent)
				{	boolean tracekleur = ((CommandComponent)c).execute(trb, ub, varSet);
					if(tracekleur) return true;
				}
			}
		}
		// execute else-block if it is there
		else if (elseBlock != null)
		{	for(int j=0 ; j<elseBlock.getComponentCount() ; j++)
			{	Object c = elseBlock.getComponent(j);
				if(c instanceof CommandComponent)
				{	boolean tracekleur = ((CommandComponent)c).execute(trb, ub, varSet);
					if(tracekleur) return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * execute the content of the if-else statement after checking if the condition is valid
	 */
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		if ( !condition.isCorrect(varSet) ) 
			return false; 
		varSet.increaseLevel("-- " + alsString + " " + condition.getValueText(), false);
		boolean b = executeContent(trb, ub, varSet);
		varSet.decreaseLevel();
		return b;
	}
		
	/**
	 * get the total code of the if-block folowed by the code of the else-block (if any);
	 * note the format (see also class ProgrammaImporter) 
	 */
	public String getCode(String tab)
	{	
		String s = tab + WebLogoGWT.rb.keuzeTekst() + " " + alsString + " " + condition.getParameterText()+ " " + 
					WebLogoGWT.rb.danTekst() + "\n" + tab +"{\n";
		String tabNieuw = tab + "    ";
		s = s + ifBlock.getCode(tabNieuw);
		s = s + tab + "}\n";
		if (elseBlock != null && elseBlock.getComponentCount() > 0 )
		{
			s = s + tab + andersString + "\n" + tab +"{\n";
			s = s + elseBlock.getCode(tabNieuw);
			s = s + tab + "}\n";
		}
		return s;
	}

	/**
	 * flagg for arrow being clicked
	 */
	boolean arrowPressed = false;
	/**
	 * redefined: intercept pressing the down- or uparrow
	 */
	public void mousePressed(int x, int y, int modifiers) 
	{	
		arrowPressed = false;
		if (elseVisibleButton.contains(x, y))
		{	setElseVisible(!elseVisible);
			containerHeightChanged(0);
			arrowPressed = true;
			schuifveld.paint();
			// and finished
			return;
		}
		// normal mousePressed
		super.mousePressed(x, y, modifiers);
	}
 
	/**
	 * redefined: if down- or uparrow was pressed, nothing to do 
	 */
	public void mouseReleased(int x, int y, int modifiers)
	{	
		if (arrowPressed)
			return;
		// normal mouseReleased
		super.mouseReleased(x, y, modifiers);
	}
	
}
