package fi.weblogogwt.client;

//import java.awt.Color;
//import java.awt.Component;
//import java.awt.FontMetrics;
//import java.awt.Graphics;
//import java.awt.Insets;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;

//import javax.swing.BorderFactory;
//import javax.swing.ImageIcon;
//import javax.swing.JToggleButton;

//import java.awt.Component;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.CommandComponent;
import fi.weblogogwt.client.VarSet;
import fi.weblogogwt.client.parameters.BooleanParameter;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

public class KeuzeCommandComponent extends CompositeCommandComponent implements ParameterEditorListener //, ActionListener
{
	private BooleanParameter condition;
	
	private String alsString = "Als"; //JavaLogoWeb.rb.getString("alsLabel");
	private String andersString = "Anders"; //JavaLogoWeb.rb.getString("andersLabel");
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
	
	
	//private JToggleButton elseVisibleButton;
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
		//if(elseVisible)
		//	add(elseBlock);
		
		schuifveld.jlsvContext2d.setFont(WebLogoGWT.fontString);
		TextMetrics tm = schuifveld.jlsvContext2d.measureText("Als "); //commandName+" ");
		int width = (int) Math.round(tm.getWidth()); 
		
		//FontMetrics fm = getFontMetrics(JavaLogoWeb.defaultfont);
		conditionEditorX = 10+width; //fm.stringWidth(commandNameTranslated+" ");
		
		
		//conditionEditor = new ParameterTextField(conditionEditorX, 4, 80, 17, this);
		//add(conditionEditor);
		

/*		
		elseVisibleButton = new JToggleButton();
		elseVisibleButton.setOpaque(false);
		elseVisibleButton.setIcon(new ImageIcon(JavaLogoWeb.class.getResource("resources/klapuit1.png")));
		elseVisibleButton.setSelectedIcon(new ImageIcon(JavaLogoWeb.class.getResource("resources/klapuit2.png")));
		elseVisibleButton.setBounds(5, elseBlockY()-ifBlockY, 15, 15);
		elseVisibleButton.addActionListener(this);
		elseVisibleButton.setBackground(Color.orange);
		elseVisibleButton.setBorder(BorderFactory.createEmptyBorder());
		elseVisibleButton.setMargin(new Insets(0,0,0,0));
		add(elseVisibleButton);
*/		
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
		
		//elseVisibleButton.setSelected(b);
	}
	
	/**
	 * xpos of elseBlock cannot be a constant
	 * 
	 * @return	elseBlockX
	 */
		
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
	 * 
	 * @param expression	the boolean expression as a String
	 */
	public void setBoolExpression (String expression)
	{
		condition.setParameter(expression);
	}

	/**
	 * Select a container for future adding of components. Only relevant for scripting (ProgrammaImporter)
	 * 
	 * @param inIfBlock		true/false for obvious container selection
	 */
	public void setInIfBlock(boolean inIfBlock)
	{
		this.inIfBlock = inIfBlock;
	}

	/**
	 * Add a CommandComponent from source code, for instance by the ProgarammaImporter
	 * Note: Drag&drop will add a CC directly into one of the CommandContainers
	 * 
	 * @param cc			CommandComponent to be added
	 * @param inIfBlock		in if- or elseBlock
	 */
	@Override
	void addCComponent(CommandComponent cc)
	{	
		if ( inIfBlock )
		{
			//ifBlock.addCComponent(cc);
			ifBlock.addCCompAtBottom(cc);
		} 
		else
		{	
			//elseBlock.addCComponent(cc);
			elseBlock.addCCompAtBottom(cc);
		}
	}
	
	public CommandComponent findCComponentAt(int x, int y, CommandComponent sc)
	{
		if (ifBlock.contains(x,y))
		{	
//System.out.println("ifBlock contains");			
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
		
//System.out.println("move keuzeCC");

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
		//super.mouseDragged(x, y, modifiers);
		super.mouseDragged(x, y, modifiers);
		if (dragging)
			ifBlock.setHeight(JavaLogoSchuifVeld.ccsh);
	}

	@Override
	public void setSize(int w, int h)
	{	
		super.setSize(w,h);
		ifBlock.setWidth(w-blockX);
		// also move elseBlock to the middle
		if (elseBlock != null)
		{	elseBlock.setLocation(blockX, elseBlockY());
			elseBlock.setWidth(w-blockX);
		}	
		
		//elseVisibleButton.setBounds(5, elseBlockY()-ifBlockY-18, 15, 15);
		elseVisibleButton = new Rectangle(xPos+5, yPos + elseBlockY()-ifBlockY -18, 15, 15);
		createVArrows();
	}

	public void setWidth(int w)
	{	
		super.setWidth(w);
		ifBlock.setWidth(w-blockX);
		// also move elseBlock to the middle
		if (elseBlock != null)
		{	//elseBlock.setLocation(blockX, elseBlockY());
			elseBlock.setWidth(w-blockX);
		}	
		
		//elseVisibleButton.setBounds(5, elseBlockY()-ifBlockY-18, 15, 15);
		elseVisibleButton = new Rectangle(xPos+5, yPos + elseBlockY()-ifBlockY -18, 15, 15);
		createVArrows();
	}

	@Override
	public void setBounds(int x, int y, int w, int h)
	{	
		super.setBounds(x,y,w,h);
		if (ifBlock != null) // && elseBlock != null)		// constructor will call setBounds before containers are made
		{
			ifBlock.setWidth(w-blockX);
			if (elseBlock != null)
			{	elseBlock.setLocation(blockX, elseBlockY());
				elseBlock.setWidth(w-blockX);
			}	
			
			//elseVisibleButton.setBounds(5, elseBlockY()-ifBlockY-18, 15, 15);
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

	@Override
	void containerHeightChanged(int h)
	{
		if (elseVisible)
			super.setSize(getWidth(), 2*ifBlockY + ifBlock.getHeight() + elseBlock.getHeight());
		else
			super.setSize(getWidth(), ifBlockY + ifBlock.getHeight());
		//((CommandContainer)getParent()).reArrange();
		parent.reArrange();
	}
	
	@Override
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

	@Override
	public void parameterComponentClicked(int x, int y)
	{
//		if ( isEditing )
//		{
//			
			//condition.setParameter(conditionEditor.getText());
			//conditionEditor.setVisible(false);
			//conditionEditor.setEditable(false);
			isEditing = false;
//		} else
//		{	
			if ( y < ifBlockY )
			{
				isEditing = true;
				
				showConditionEditor();
//				
				//conditionEditor.vulIn(condition.getParameterText());
				//conditionEditor.setLocation(conditionEditorX, 4);
			}
//		}
		schuifveld.paint();
	}

	public void showConditionEditor()
	{
		
//System.out.println("showLoopEditor");

		int popupX = xPos + schuifveld.getAbsoluteLeft();
		
//System.out.println("xPos = " + xPos);
//System.out.println("popupX = " + popupX);
		
		int popupY = yPos + ifBlockY + schuifveld.getAbsoluteTop();

//System.out.println("yPos = " + yPos);
//System.out.println("popupY = " + popupY);
		
		//if ((conditionEditor != null) && conditionEditor.isVisible())
		if ((schuifveld.paramEditor != null) && schuifveld.paramEditor.isVisible())
		{
			
			schuifveld.paramEditor.owner.parameterEdited(schuifveld.paramEditor.getText());
		}
//System.out.println("breedte = " + breedte);
//System.out.println("hoogte = " + hoogte);
		schuifveld.paramEditor = new ParameterTextField(breedte, hoogte, this, schuifveld);
		conditionEditor = schuifveld.paramEditor; //new ParameterTextField(breedte, hoogte, this, schuifveld);
		conditionEditor.vulIn(condition.getParameterText());
		conditionEditor.setPopupPosition(popupX, popupY);
		conditionEditor.show();
		conditionEditor.textBox.setFocus(true);
		
//System.out.println("loopEditor visible = " + conditionEditor.isVisible());		

	}

	@Override
	//protected void paintBackground(Graphics g)
	protected void paintBackground(Context2d g)
	{
		//g.setColor(Color.orange);
		g.setFillStyle(CssColor.make(255, 127, 0));
		if (traceKleur)
		{	//g.setColor(traceActiveColor);
			g.setFillStyle(traceActiveColor);
		}
		g.fillRect(xPos+0, yPos+0, getSize().width-1, getSize().height-1);
		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		//g.drawRect(0, 0, getSize().width-1, getSize().height-1);
		g.strokeRect(xPos+0, yPos+0, getSize().width-1, getSize().height-1);
		//g.drawRect(1, 1, getSize().width-3, getSize().height-3);
		g.strokeRect(xPos+1, yPos+1, getSize().width-3, getSize().height-3);
		//g.drawLine(0, 0, blockWidth(), blockY);
		//g.drawLine(0, 1, blockWidth(), blockY+1);
		//g.drawLine(getSize().width, 0, blockWidth(), blockY);
		//g.drawLine(getSize().width, 1, blockWidth(), blockY+1);
		//g.setFont(JavaLogoWeb.defaultfont);
		g.setFont(WebLogoGWT.fontString);
		g.setFillStyle(CssColor.make(0,0,0));
		//g.drawString(alsString, 10, 18);
		g.fillText(alsString, xPos+10, yPos+18);
		if (elseVisible)
		{	
			//g.drawString(andersString, 10, ifBlockY+ifBlock.getHeight() +18);
			g.fillText(andersString, xPos+10, yPos+ifBlockY+ifBlock.getHeight() +18);
		}
	}

	@Override
	//protected void paintCommand(Graphics g)
	protected void paintCommand(Context2d g)
	{
		if ( isEditing ) return;			// nothing to paint, only the TextField
		//g.setFont(JavaLogoWeb.defaultfont);
		g.setFont(WebLogoGWT.fontString);
		if ( condition.isCorrect() )
		{
			//g.setColor(Color.black);
			g.setFillStyle(CssColor.make(0,0,0));
		} 
		else
		{
			//g.setColor(Color.RED);
			g.setFillStyle(CssColor.make(255,0,0));
		}
		
		TextMetrics tm = g.measureText(condition.getParameterText());
		int textWidth = (int) Math.round(tm.getWidth());
		if (textWidth > breedte - 10)
		{	tm = g.measureText("Als");
			textWidth = (int) Math.round(tm.getWidth()); 
			if (textWidth > breedte - 10)
			{	g.fillText("A",xPos+10,yPos+18);
			}
			else
				g.fillText(commandName,xPos+10,yPos+18);
		}
		else
			g.fillText(condition.getParameterText(),xPos+conditionEditorX, yPos+18);

		
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
	
	public boolean executeContent(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
//System.out.println("kCC executeContent");

		boolean value = condition.getValue();
		traceKleur = trb.commandExecuted(varSet.getLevel());
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
	
	@Override
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		
//System.out.println("kCC execute");

		if ( !condition.isCorrect(varSet) ) 
			return false; 
		
		varSet.increaseLevel("-- " + alsString + " " + condition.getValueText());
		boolean b = executeContent(trb, ub, varSet);
		varSet.decreaseLevel();
		return b;
	}
		
	@Override
	public String getCode(String tab)
	{	String s = tab + "Keuze: Als "+condition.getParameterText()+" Dan\n" + tab +"{\n";
		String tabNieuw = tab + "    ";
		s = s + ifBlock.getCode(tabNieuw);
		s = s + tab + "}\n";
		if ( elseBlock.getComponentCount() > 0 )
		{
			s = s + tab + "Anders\n" + tab +"{\n";
			s = s + elseBlock.getCode(tabNieuw);
			s = s + tab + "}\n";
		}
		return s;
	}

	boolean arrowPressed = false;
	public void mousePressed(int x, int y, int modifiers) 
	{	
		
//System.out.println("kc mousePressed");		
		arrowPressed = false;
		if (elseVisibleButton.contains(x, y))
		{
//System.out.println("kc arrowPressed");			
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
		//schuifveld.mouseReleased(getAbsoluteLocation().x+e.getX(), getAbsoluteLocation().y+e.getY(), e.getModifiersEx());
		super.mouseReleased(x, y, modifiers);
	}
	

	

/*	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==elseVisibleButton)
		{	elseVisible = elseVisibleButton.isSelected();
			if(elseVisible)
				add(elseBlock);
			else
			{	remove(elseBlock);
				elseBlock.removeAll();
			}
			containerHeightChanged(0);
		}
		
		
	}
*/	

}
