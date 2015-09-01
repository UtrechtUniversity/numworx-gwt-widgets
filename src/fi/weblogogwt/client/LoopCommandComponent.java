package fi.weblogogwt.client;

//import java.awt.Color;
//import java.awt.FontMetrics;
//import java.awt.Graphics;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.parameters.TAParameter;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.user.client.ui.LayoutPanel;

//import com.google.gwt.user.client.ui.LayoutPanel;

public abstract class LoopCommandComponent extends CompositeCommandComponent implements ParameterEditorListener
{
	protected TAParameter loopCondition;
	
	protected CommandContainer loopBlock;
	public static final int blockX = 25;
	public static final int blockY = 25;

	private boolean isEditing = false;
	
	private ParameterTextField loopEditor;
	protected String naString;
	protected String naStringTranslated;
	
	//LayoutPanel inputOwner;
	
	public LoopCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		
		//inputOwner = sv;
		//loopBlock = new CommandContainer(xPos+blockX, yPos+blockY, b-blockX, JavaLogoSchuifVeld.ccsh, this);
		loopBlock = new CommandContainer(xPos+blockX, yPos+blockY, b-blockX, h-JavaLogoSchuifVeld.ccsh, this);
		loopBlock.parent = this;
		//add(loopBlock);
		loopBlock.containerName = "loop";
		
		
	}

	/**
	 * Create the textfield for editing loop count or while-condition. Must be called by subclasses
	 * after the commandName has been set
	 */
	protected void createLoopEditor()
	{	
// niet nodig in GWT		
		//FontMetrics fm = getFontMetrics(JavaLogoWeb.defaultfont);
		//int tfx = 10+fm.stringWidth(commandName+" ");
		//loopEditor = new ParameterTextField(tfx, 4, 80, 17, this);
		//add(loopEditor);
	}

	
	public CommandComponent findCComponentAt(int x, int y, CommandComponent sc)
	{
		if (loopBlock.contains(x,y))
		{	
//System.out.println("loopBlock contains");
//CommandComponent c = loopBlock.findCComponentAt(x,y,sc);
//if (c == null) System.out.println("c == null");
			return loopBlock.findCComponentAt(x,y,sc);
		}
		else if ((sc != this) && contains(x,y)) // && !loopBlock.contains(x,y))
			return this;
		else
			return null;
	}

	public CommandContainer findCContainerAt(int x, int y)
	{
		if (loopBlock.contains(x,y))
			return loopBlock.findCContainerAt(x,y);
		else
			return null;
		
	}
 
	
	@Override
	void addCComponent(CommandComponent cc)
	{	
		loopBlock.addCComponent(cc);
	}
	
	public void setSize(int w, int h)
	{	
	
		loopBlock.setWidth(w-blockX);
		super.setSize(w,h);
	}

	public void setWidth(int w)
	{	
		loopBlock.setWidth(w-blockX);
		super.setWidth(w);
	}

	public void setBounds(int x, int y, int w, int h)
	{	
		if ( loopBlock != null )		// for constructor only: loopBlock made later on
		{
			loopBlock.setWidth(w-blockX);			
		}
		super.setBounds(x,y,w,h);
	}

	public void setLocation(int x, int y)
	{
		xPos = x; yPos = y;
		if (loopBlock != null)
			loopBlock.setLocation(xPos+blockX, yPos+blockY);
	}

	@Override
	void containerHeightChanged(int h)
	{
		// this is callback from CommandContainer that has been adjusted. Just change height of this component
		super.setSize(getWidth(), h+blockY);
		//((CommandContainer)getParent()).reArrange();
		parent.reArrange();
	}
	
	public void moveComponent(int dx, int dy)
	{	
		
//System.out.println("move loopCC");

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
		super.mouseDragged(x, y, modifiers);
		if (dragging)
		{	loopBlock.setHeight(Math.max(loopBlock.getHeight(), JavaLogoSchuifVeld.ccsh));
			loopBlock.setWidth(breedte - blockX);
		}
	}
	/**
	 * Set loop count directly (programmaImporter)
	 * 
	 * @param s
	 */
	void setLoopCount(String s)
	{
		loopCondition.setParameter(s);
	}
	
	@Override
	public void parameterEdited(String text)
	{
		loopCondition.setParameter(text);
		isEditing = false;
		// tekstPopup weg
		if (loopEditor != null)
		{	
//System.out.println("loopEditor weg");			
			loopEditor.hide();
		
		}

		schuifveld.paint();
	}

	@Override
	public void parameterComponentClicked(int x, int y)
	{
		//if ( isEditing )
		//{

			//loopCondition.setParameter(loopEditor.getText());
			//loopEditor.setVisible(false);
			//loopEditor.setEditable(false);
			//isEditing = false;
		//} 
		//else
		//{	
//System.out.println("loopCCClicked " + y);		
			if (y < blockY )
			{
			
				isEditing = true;
				//loopEditor.vulIn(loopCondition.getParameterText());
				showLoopEditor();
			}
		//}
		schuifveld.paint();
	}
	
	public void showLoopEditor()
	{
		
//System.out.println("showLoopEditor");

		int popupX = xPos + schuifveld.getAbsoluteLeft();
		
//System.out.println("xPos = " + xPos);
//System.out.println("popupX = " + popupX);
		
		int popupY = yPos + blockY + schuifveld.getAbsoluteTop();

//System.out.println("yPos = " + yPos);
//System.out.println("popupY = " + popupY);
		
		//if ((loopEditor != null) && loopEditor.isVisible())
		if ((schuifveld.paramEditor != null) && schuifveld.paramEditor.isVisible())
		{
			
			schuifveld.paramEditor.owner.parameterEdited(schuifveld.paramEditor.getText());
		}

//System.out.println("breedte = " + breedte);
//System.out.println("hoogte = " + hoogte);
		
		schuifveld.paramEditor = new ParameterTextField(breedte, hoogte, this, schuifveld);
		loopEditor = schuifveld.paramEditor; //new ParameterTextField(breedte, hoogte, this, schuifveld);
		loopEditor.vulIn(loopCondition.getParameterText());
		loopEditor.setPopupPosition(popupX, popupY);
		loopEditor.show();
		loopEditor.textBox.setFocus(true);
		
//System.out.println("loopEditor visible = " + loopEditor.isVisible());		

	}

	@Override
	//protected void paintBackground(Graphics g)
	protected void paintBackground(Context2d g)
	{
		//g.setColor(Color.orange);
		g.setFillStyle(CssColor.make(255, 127, 0));
		g.fillRect(xPos+0,yPos+0,getSize().width-1,getSize().height-1);
		
		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		//g.drawRect(0,0,getSize().width-1,getSize().height-1);
		g.strokeRect(xPos+0,yPos+0,getSize().width-1,getSize().height-1);
		
		//g.drawRect(1,1,getSize().width-3,getSize().height-3);
		g.strokeRect(xPos+1,yPos+1,getSize().width-3,getSize().height-3);

	}

	@Override
	//protected void paintCommand(Graphics g)
	protected void paintCommand(Context2d g)
	{
//System.out.println("c loop paintComm");		
		
		//g.setFont(JavaLogoWeb.defaultfont);
		g.setFont(WebLogoGWT.fontString);
		//g.setColor(Color.black);
		g.setFillStyle(CssColor.make(0,0,0));
		if ( isEditing )
		{
			
			//g.drawString(commandName+" ", 10, 18);
			g.fillText(commandName+" ",xPos+10, yPos+18);
			//g.drawString(naStringTranslated, loopEditor.getX()+loopEditor.getWidth()+1, 18);		
		} 
		else
		{
			if (!loopCondition.isCorrect())
				//g.setColor(Color.RED);
				g.setFillStyle(CssColor.make(255,0,0));
			//g.drawString(commandNameTranslated+" "+loopCondition.getParameterText()+naStringTranslated, 10, 18);
//GWT			
			//g.fillText(commandNameTranslated+" "+loopCondition.getParameterText()+naStringTranslated, xPos+10, yPos+18);
			TextMetrics tm = g.measureText(commandName+" "+loopCondition.getParameterText()+naString);
			int textWidth = (int) Math.round(tm.getWidth());
			if (textWidth > breedte - 10)
			{	tm = g.measureText(commandName);
				textWidth = (int) Math.round(tm.getWidth()); 
				if (textWidth > breedte - 10)
				{	g.fillText(commandName.substring(0,1),xPos+10,yPos+18);
				}
				else
					g.fillText(commandName,xPos+10,yPos+18);
			}
			else
				g.fillText(commandName+" "+loopCondition.getParameterText()+naString,xPos+10, yPos+18);
		}
		
		if (loopBlock != null)
			loopBlock.paintComponent(g);
	}
	
	public abstract boolean executeContent(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet);
	
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	
		
//System.out.println("lCC execute");

		varSet.increaseLevel("-- in loop");
		boolean b = executeContent(trb, ub, varSet);
		varSet.decreaseLevel();
		return b;
	}
		
	@Override
	public String getCode(String tab)
	{	String s = tab + commandName + " " + loopCondition.getParameterText()+naString + "\n" + tab +"{\n";
		String tabNieuw = tab + "    ";
		s= s+ loopBlock.getCode(tabNieuw);
		s = s + tab + "}\n";
		return s;
	}


}
