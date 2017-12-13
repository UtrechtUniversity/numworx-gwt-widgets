package fi.weblogo3dgwt.client;



import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.parameters.TAParameter;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * see class LoopCommandComponent in WebLogoGWT
 */

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
	
	
	public LoopCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		
		loopBlock = new CommandContainer(xPos+blockX, yPos+blockY, b-blockX, h-JavaLogoSchuifVeld.ccsh, this);
		loopBlock.parent = this;
		loopBlock.containerName = "loop";
		
	}

	public CommandComponent findCComponentAt(int x, int y, CommandComponent sc)
	{
		if (loopBlock.contains(x,y))
		{	
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
 
	
	void addCComponent(CommandComponent cc)
	{	
		loopBlock.addCCompAtBottom(cc);
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
		parent.reArrange();
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
		super.mouseDragged(x, y, modifiers);
		if (dragging)
		{	loopBlock.setHeight(Math.max(loopBlock.getHeight(), JavaLogoSchuifVeld.ccsh));
			loopBlock.setWidth(breedte - blockX);
		}
	}
	/**
	 * Set loop count directly (programmaImporter)
	 * @param s String version of loop count
	 */
	void setLoopCount(String s)
	{
		loopCondition.setParameter(s);
	}
	
	public void parameterEdited(String text)
	{
		loopCondition.setParameter(text);
		isEditing = false;
		// tekstPopup weg
		if (loopEditor != null)
		{	
			loopEditor.hide();
		}

		schuifveld.paint();
	}

	public void parameterComponentClicked(int x, int y)
	{
		if (y < blockY )
		{
			isEditing = true;
			showLoopEditor();
		}
		schuifveld.paint();
	}
	
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

	protected void paintBackground(Context2d g)
	{
		if(traceKleur)
		{
			g.setFillStyle(traceActiveColor);
			traceKleur = false;
		} 
		else
		{
			g.setFillStyle(CssColor.make(255, 127, 0));
		}	
		
		
		
		g.fillRect(xPos+0,yPos+0,getSize().width-1,getSize().height-1);
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.strokeRect(xPos+0,yPos+0,getSize().width-1,getSize().height-1);
		g.strokeRect(xPos+1,yPos+1,getSize().width-3,getSize().height-3);

	}

	protected void paintCommand(Context2d g)
	{
		g.setFont(WebLogo3dGWT.fontString);
		g.setFillStyle(CssColor.make(0,0,0));
		if ( isEditing )
		{
			g.fillText(commandName+" ",xPos+10, yPos+18);
			TextMetrics tm = g.measureText(commandName);
			int textWidth = (int) Math.round(tm.getWidth());
			g.fillText(naString, xPos+textWidth + 40, yPos+18);
		} 
		else
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
			}	
			else
				g.fillText(fullText,xPos+10,yPos+18);
		}
		
		if (loopBlock != null)
			loopBlock.paintComponent(g);
	}
	
	public abstract boolean executeContent(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet);
	
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	
		varSet.increaseLevel("-- in loop", false);
		boolean b = executeContent(trb, ub, varSet);
		varSet.decreaseLevel();
		return b;
	}
		
	public String getCode(String tab)
	{	String s = tab + commandName + " " + loopCondition.getParameterText()+naString + "\n" + tab +"{\n";
		String tabNieuw = tab + "    ";
		s= s+ loopBlock.getCode(tabNieuw);
		s = s + tab + "}\n";
		return s;
	}


}
