package fi.weblogo3dgwt.client;


import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.CommandComponent;
import fi.weblogo3dgwt.client.VarSet;
import fi.weblogo3dgwt.client.parameters.BooleanParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;
import fi.weblogo3dgwt.client.logotekenap3d.Rectangle;
import fi.weblogo3dgwt.client.logotekenap3d.Polygon;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * see class KeuzeCommandComponent in WebLogoGWT
 */

public class KeuzeCommandComponent extends CompositeCommandComponent implements ParameterEditorListener //, ActionListener
{
	private BooleanParameter condition;
	
	private String alsString = WebLogo3dGWT.rb.alsTekst(); 
	private String andersString = WebLogo3dGWT.rb.andersTekst(); 
	public static final int blockX = 25;
	// note: elseBlockX is variable, given by a method
	public static final int ifBlockY = 25;
	
	private boolean inIfBlock = true;
	
	CommandContainer ifBlock;
	CommandContainer elseBlock;
	
	private boolean isEditing = false;
	
	private ParameterTextField conditionEditor;
	private int conditionEditorX;
	
	private boolean elseVisible = false;
	
	Rectangle elseVisibleButton;
	
	Polygon arrowDown, arrowUp;
	
	public KeuzeCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		commandName = "Keuze";
		condition = new BooleanParameter();
		
		ifBlock = new CommandContainer(xPos+blockX, yPos+ifBlockY, blockWidth(), h-JavaLogoSchuifVeld.ccsh, this);
		ifBlock.parent = this;
		ifBlock.containerName = "if";
		//add(ifBlock);
		if (elseVisible)
		{	elseBlock = new CommandContainer(xPos+blockX, yPos+elseBlockY(), blockWidth(), h-JavaLogoSchuifVeld.ccsh, this);
			elseBlock.parent = this;
			elseBlock.containerName = "else";
		}
		
		schuifveld.jlsvContext2d.setFont(WebLogo3dGWT.fontString);
		TextMetrics tm = schuifveld.jlsvContext2d.measureText("Als "); //commandName+" ");
		int width = (int) Math.round(tm.getWidth()); 
		
		conditionEditorX = 10+width; 

		elseVisibleButton = new Rectangle(xPos+5, yPos+ifBlockY + 2, 15, 15);
		
		createVArrows();
	}

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

	public void setElseVisible(boolean b)
	{
		elseVisible = b;
		if (elseVisible)
		{	//add(elseBlock);
			elseBlock = new CommandContainer(xPos+blockX, yPos+elseBlockY(), blockWidth(), JavaLogoSchuifVeld.ccsh, this);
			elseBlock.parent = this;
			elseBlock.containerName = "else";
		}
		else
		{	//remove(elseBlock);
			elseBlock = null;
		}
		
	}
	
		
	public final int elseBlockY()
	{
		return 2*ifBlockY + ifBlock.getHeight();
	}
	
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

	public CommandContainer findCContainerAt(int x, int y)
	{
		if (ifBlock.contains(x,y))
			return ifBlock.findCContainerAt(x,y);
		else if ((elseBlock != null) && elseBlock.contains(x,y))
			return ifBlock.findCContainerAt(x,y);
		else
			return null;
		
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
	{
		if (arrowPressed)
			return;
		super.mouseDragged(x, y, modifiers);
		if (dragging)
			ifBlock.setHeight(JavaLogoSchuifVeld.ccsh);
	}

	public void setSize(int w, int h)
	{	
		super.setSize(w,h);
		ifBlock.setWidth(w-blockX);
		// also move elseBlock to the middle
		if (elseBlock != null)
		{	elseBlock.setLocation(blockX, elseBlockY());
			elseBlock.setWidth(w-blockX);
		}	
		
		elseVisibleButton = new Rectangle(xPos+5, yPos + elseBlockY()-ifBlockY -18, 15, 15);
		createVArrows();
	}

	public void setWidth(int w)
	{	
		super.setWidth(w);
		ifBlock.setWidth(w-blockX);
		// also move elseBlock to the middle
		if (elseBlock != null)
		{	elseBlock.setWidth(w-blockX);
		}	
		
		elseVisibleButton = new Rectangle(xPos+5, yPos + elseBlockY()-ifBlockY -18, 15, 15);
		createVArrows();
	}

	public void setBounds(int x, int y, int w, int h)
	{	
		super.setBounds(x,y,w,h);
		if (ifBlock != null) // constructor will call setBounds before containers are made
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

	void containerHeightChanged(int h)
	{
		if (elseVisible)
			super.setSize(getWidth(), 2*ifBlockY + ifBlock.getHeight() + elseBlock.getHeight());
		else
			super.setSize(getWidth(), ifBlockY + ifBlock.getHeight());
		parent.reArrange();
	}
	
	public void parameterEdited(String text)
	{
		condition.setParameter(text);
		isEditing = false;
		// tekstPopup weg
		if (conditionEditor != null)
		{	conditionEditor.hide();	
		
		}

		schuifveld.paint();
	}

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

	public void showConditionEditor()
	{
		int popupX = xPos + schuifveld.getAbsoluteLeft();
		
		int popupY = yPos + ifBlockY + schuifveld.getAbsoluteTop();

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

	protected void paintBackground(Context2d g)
	{
		// orange
		g.setFillStyle(CssColor.make(255, 127, 0));
		if (traceKleur)
		{	
			g.setFillStyle(traceActiveColor);
		}
		g.fillRect(xPos+0, yPos+0, getSize().width-1, getSize().height-1);

		g.setStrokeStyle(CssColor.make(0,0,0));
		g.strokeRect(xPos+0, yPos+0, getSize().width-1, getSize().height-1);
		g.strokeRect(xPos+1, yPos+1, getSize().width-3, getSize().height-3);
		g.setFont(WebLogo3dGWT.fontString);
		g.setFillStyle(CssColor.make(0,0,0));
		g.fillText(alsString, xPos+10, yPos+18);
		if (elseVisible)
		{	
			g.fillText(andersString, xPos+10, yPos+ifBlockY+ifBlock.getHeight() +18);
		}
	}

	protected void paintCommand(Context2d g)
	{
		if ( isEditing ) return;			// nothing to paint, only the TextField
		g.setFont(WebLogo3dGWT.fontString);
		if ( condition.isCorrect() )
		{
			g.setFillStyle(CssColor.make(0,0,0));
		} 
		else
		{
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
	
	public boolean executeContent(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	

		boolean value = condition.getValue();
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) 
		{
			trb.setCommandInfo(alsString+" "+condition.getValueText(), varSet);
			return traceKleur;
		}
		if(value)
		{	for(int j=0 ; j<ifBlock.getComponentCount() ; j++)
			{	Object c = ifBlock.getComponent(j);
				if(c instanceof CommandComponent)
				{	boolean tracekleur = ((CommandComponent)c).execute(trb, ub, varSet);
					if(tracekleur) return true;
				}
			}
		}
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
	
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	
		
		if ( !condition.isCorrect(varSet) ) 
			return false; 
		
		varSet.increaseLevel("-- " + alsString + " " + condition.getValueText(), false);
		boolean b = executeContent(trb, ub, varSet);
		varSet.decreaseLevel();
		return b;
	}
		
	public String getCode(String tab)
	{	
		String s = tab + WebLogo3dGWT.rb.keuzeTekst() + " " + alsString + " " + condition.getParameterText()+ " " + 
					WebLogo3dGWT.rb.danTekst() + "\n" + tab +"{\n";
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

	boolean arrowPressed = false;
	public void mousePressed(int x, int y, int modifiers) 
	{	
		
		arrowPressed = false;
		if (elseVisibleButton.contains(x, y))
		{
			setElseVisible(!elseVisible);
			containerHeightChanged(0);
			arrowPressed = true;
			
			schuifveld.paint();
			
			return;
		}
		super.mousePressed(x, y, modifiers);
	}
 
	public void mouseReleased(int x, int y, int modifiers)
	{	
		if (arrowPressed)
			return;
		super.mouseReleased(x, y, modifiers);
	}
	

}
