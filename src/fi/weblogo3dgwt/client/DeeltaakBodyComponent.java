package fi.weblogo3dgwt.client;


import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;
import fi.weblogo3dgwt.client.logotekenap3d.Rectangle;
import fi.weblogo3dgwt.client.parameters.Identifier;
import fi.weblogo3dgwt.client.parameters.IdentifierList;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * see class DeeltaakBodyComponent in WebLogoGWT
 */
public class DeeltaakBodyComponent extends ProgrammaComponent implements ParameterEditorListener
{
	private ParameterTextField naamEditor;
	private int separatorX = 0;
	TextMetrics tm;
	
	private boolean isEditingName = false;
	private boolean isEditingParamName = false;
	private Identifier deeltaaknaamParam;
	private IdentifierList pmParam;
	
	public DeeltaakBodyComponent(int x, int y, int b, int h, String pn, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, pn, sv);
		deeltaaknaamParam = new Identifier(pn);
		pmParam = new IdentifierList();
		
		schuifveld.jlsvContext2d.setFont(WebLogo3dGWT.fontString);
		
		tm = schuifveld.jlsvContext2d.measureText(commandName+"(");
		int width = (int) Math.round(tm.getWidth());
				
		separatorX = 10+width;
		
		isHeightFixed = false; 			// allow height changes
	}
	
	/**
	 * Check validity of entire deeltaak header.
	 * This includes testing if name is an identifier, all parameters are identifiers
	 * and number of parameters less than or equel maximum.
	 * @return true if all checks are ok.
	 */
	boolean isHeaderValid()
	{
		return deeltaaknaamParam.isCorrect() && pmParam.isCorrect();
	}

	public void parameterEdited(String text)
	{
		if ( isEditingName )
		{
			deeltaaknaamParam.setParameter(text);
			isEditingName = false;
			tm = schuifveld.jlsvContext2d.measureText(deeltaaknaamParam.getParameterText()+"(");
			int tWidth = (int) Math.round(tm.getWidth());
			separatorX = 10+tWidth;
		} 
		else if ( isEditingParamName )
		{
			pmParam.setParameter(text);
			isEditingParamName = false;
		}
		
		// tekstPopup weg
		if (naamEditor != null)
		{	naamEditor.hide();
		}

		schuifveld.paint();
	}

	public void editParameter(boolean name)
	{
		if ( name )
		{
			isEditingName = true;
		}
		else
		{
			isEditingParamName = true;
		}
		
		showParamEditor(name);
	}

	public void showParamEditor(boolean name)
	{
		int popupX = xPos + schuifveld.getAbsoluteLeft();
		if (!name)
			popupX = xPos + separatorX + schuifveld.getAbsoluteLeft();
		
		int popupY = yPos + headerHeight + schuifveld.getAbsoluteTop();
		
		// kijk of er ergens nog een popup open is
		if ((schuifveld.paramEditor != null) && schuifveld.paramEditor.isVisible())
		{
			if ((!isEditingName && !isEditingParamName) || (schuifveld.paramEditor != naamEditor))
				schuifveld.paramEditor.owner.parameterEdited(schuifveld.paramEditor.getText());
		}

		schuifveld.paramEditor = new ParameterTextField(breedte, hoogte, this, schuifveld);
		naamEditor = schuifveld.paramEditor; 
		if (name)
			naamEditor.vulIn(deeltaaknaamParam.getParameterText());
		else
			naamEditor.vulIn(pmParam.getParameterText());
		naamEditor.setPopupPosition(popupX, popupY);
		naamEditor.show();
		naamEditor.textBox.setFocus(true);

	}

	/**
	 * Handle mouse actions tos start and stop editting parameters.
	 * Note: all resize operations are handled directly by MouseListener in ProgrammaComponent, 
	 * no need to consider them here.
	 */
	@Override
	public void parameterComponentClicked(int x, int y)
	{
		boolean newEdit;
		boolean onName = ( x < separatorX );
		if ( isEditingName )
		{
			newEdit = !onName;				// newEdit true: going from name to value
			
			parameterEdited(naamEditor.getText());
		}
		else if ( isEditingParamName )
		{
			newEdit = onName;				// newEdit true: going from value to name
			
			parameterEdited(naamEditor.getText());
		} 
		else
		{
			newEdit = true;					// we weren't editing anything, so start
		}
		if ( newEdit && y <= headerHeight)
		{	
			editParameter(onName);
		} 
		else
		{
		// tekstPopup weg
			if (naamEditor != null)
			{	naamEditor.hide();
			
			}	
			
		}
		schuifveld.paint();
	}
	
	/**
	 * Set name and parameter directly (ProgrammaImporter)
	 * @param n name of parameter
	 * @param p parameter text
	 */
	void setDeeltaakHeader(String n, String p)
	{
		deeltaaknaamParam.setParameter(n);
		pmParam.setParameter(p);
	}
	
	/**
	 * Gets the name of this ProgrammaComponent, regardless of its correctness
	 * Note: when printing or exporting, we also want incorrect names. in execution
	 * 
	 * @return	the given name for this deeltaak
	 */
	public String getProgramName()
	{
		return deeltaaknaamParam.getParameterText();
	}
	
	/**
	 * Get the number of parameters of this deeltaak
	 * Note: returns the number also when one or more identifiers are incorrect!
	 * 
	 * @return	number of parameters
	 */
	int getParameterCount()
	{
		return pmParam.getIdCount();
	}
	
	/**
	 * Get name of the parameter. Will return empty string if there isn't one.
	 * @param n number of parameter
	 * @return	parameter name or empty string
	 */
	String getParameterName(int n)
	{
		return pmParam.getIdentifier(n);
	}
	
	/**
	 * DeeltaakBodyComponents retains its width when being dragged
	 */
	int getDragWidth()
	{
		return getWidth();
	}
	
	/**
	 * Deeltaak will not be traced ( no carets while arranging the DTB's in the ProgrammaPanel)
	 */
	boolean isTraceable()
	{
		return false;
	}
	
	void containerHeightChanged(int h)
	{	
		if ( !isHeightFixed && isOpen )
		{	int newh;
			newh = Math.min(schuifveld.getHeight()-20, Math.max(pcminoh, commandBlock.getContentHeight()+headerHeight+20));
			setSize(getWidth(), newh);
			setLocation(getX(), Math.min(getY(), Math.max(0,JavaLogoSchuifVeld.pph-newh)));
		}
		
		int heightSurplus = commandBlock.getHeight()-(getHeight()-headerHeight);
		if ( heightSurplus > 0 )
		{	
			int newY = yPos + headerHeight - heightSurplus;
			commandBlock.setLocation(commandBlock.getX(), newY);
		}
		else
			commandBlock.setLocation(commandBlock.getX(), yPos+headerHeight);
	}
	
	@Override
	protected void dropComponent(int x, int y)
	{
		int newX = previousX;
		int newY = previousY;
		Rectangle r = new Rectangle(JavaLogoSchuifVeld.ppx, JavaLogoSchuifVeld.ppy, 
                schuifveld.breedte, schuifveld.hoogte);

		if ( r.contains(x, y) )
		{
			newX = getX(); 
			newX = Math.max(80, Math.min(newX, schuifveld.getWidth()-getWidth()));
			newY = getY(); 
			newY = Math.max(0, Math.min(newY, schuifveld.getHeight()-getHeight()));
		}
		setLocation(newX, newY);
 		
		schuifveld.addToProgrammaPanel(this);
		schuifveld.paint();
	}
	
	
	protected void paintCommand(Context2d g)
	{

		g.setFont(WebLogo3dGWT.boldFontString);
		g.setFillStyle(CssColor.make(0,0,0));
		
		if ( isEditingName )
		{
			g.fillText("(" + pmParam.getParameterText() + ")", xPos+ separatorX, yPos+18);
		}
		else if ( isEditingParamName )
		{
			g.fillText(deeltaaknaamParam.getParameterText() + "(", xPos+10, yPos+18);
			
			g.fillText(" )", xPos+separatorX+60+3, yPos+18);
		} 
		else
		{	// paint parts of the declaration in RED if they are incorrect;
			int xpos = 10;
			if ( !deeltaaknaamParam.isCorrect() ) 
			{	g.setFillStyle(CssColor.make(255,0,0));
			}
			// subroutine name
			g.fillText(deeltaaknaamParam.getParameterText(), xPos+10, yPos+18);
			
			tm = schuifveld.jlsvContext2d.measureText(deeltaaknaamParam.getParameterText());
			int tWidth = (int) Math.round(tm.getWidth());
			xpos = xpos+tWidth;
			g.setFillStyle(CssColor.make(0,0,0));
			g.fillText("( ", xPos+xpos, yPos+18);
			tm = schuifveld.jlsvContext2d.measureText("(");
			tWidth = (int) Math.round(tm.getWidth());
			xpos = xpos + tWidth;
			if ( !pmParam.isCorrect() )
			{	g.setFillStyle(CssColor.make(255,0,0));
			}
			
			// parameter list
			// assume up to here fits
			String paramString = pmParam.getParameterText();
			tm = schuifveld.jlsvContext2d.measureText(pmParam.getParameterText());
			int paramWidth = (int) Math.round(tm.getWidth());
			int textWidth = xpos+paramWidth;
			if (textWidth > breedte-50)
			{	
				while (textWidth > breedte - 50)
				{
					paramString = paramString.substring(0, paramString.length() - 1);
					tm = g.measureText(paramString);
					paramWidth = (int) Math.round(tm.getWidth());
					textWidth = xpos+paramWidth;
				}
				g.fillText(paramString,xPos+xpos,yPos+18);
				
			}
			else
			{	g.fillText(pmParam.getParameterText(), xPos+xpos, yPos+18);
				xpos = xpos + paramWidth;
				// black
				g.setFillStyle(CssColor.make(0,0,0));
				// closing parenthesis
				g.fillText(")", xPos+xpos, yPos+18);
			}

		}
		
	}
	
	@Override
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{
		return executeContent(trb, ub, varSet);
	}

	public String getCode(String tab)
	{	
		String s = "\n" + WebLogo3dGWT.rb.deeltaak1Tekst() + " " + getProgramName() + "( " + pmParam.getParameterText() + " )" + "\n";
		return s+ super.getCode(tab)+"\n";
	}

}
