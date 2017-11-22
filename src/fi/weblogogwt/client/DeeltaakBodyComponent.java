package fi.weblogogwt.client;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;
import fi.weblogogwt.client.parameters.Identifier;
import fi.weblogogwt.client.parameters.IdentifierList;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * class implementing the body of a subroutine; note that a subroutine has a name (header) optionally followed by
 * a number of parameters; editing the name and parameters of a deeltaakBody is done via double or long click
 * at the relevant location; note that this is handled in the ultimate super class CommandComponent;
 * DeeltaakBodyComponent can be dragged in the usual way; <br>
 * the header part of a DeeltaakBodyComponent contains a right arrow: clicking the right arrow doubles the width
 * of the DeeltaakBodyComponent while the right arrow is replaced by a left arrow with obvious function;
 * the height of the DeeltaakBodyComponent adapts to its content; clicking the '--' in the header part 
 * hides all content and replaces the '--' by a small square with reverse function;
 * note that these width and height changes are handled in superclass ProgrammaComponent; <br>
 *calling a subroutine is done via class DeeltaakCallComponent
 */
public class DeeltaakBodyComponent extends ProgrammaComponent implements ParameterEditorListener
{
	/**
	 * PopupPanel for editing name or parameters
	 */
	private ParameterTextField naamEditor;
	/**
	 * x-position (pixels) where parameter list should start
	 */
	private int separatorX = 0;
	/**
	 * instance of TextMetrics for measuring width of Strings
	 */
	TextMetrics tm;
	
	/**
	 * editing name?
	 */
	private boolean isEditingName = false;
	/**
	 * editing parameters?
	 */
	private boolean isEditingParamName = false;
	/**
	 * name of subroutine (must be a valid identifier)
	 */
	private Identifier deeltaaknaamParam;
	/**
	 * list of parameter names (valid identifiers)
	 */
	private IdentifierList pmParam;
	
    /**
     * constructor
     * @param x x-position
     * @param y y-position
     * @param b width
     * @param h height
     * @param pn name of subroutine
     * @param sv JavaLogoSchuifVeld containing the Canvas for painting
     */
	public DeeltaakBodyComponent(int x, int y, int b, int h, String pn, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, pn, sv);
		deeltaaknaamParam = new Identifier(pn);
		pmParam = new IdentifierList();
		// for testing
		commandBlock.containerName = pn;		
		schuifveld.jlsvContext2d.setFont(WebLogoGWT.fontString);
		// header plus opening parenthesis
		tm = schuifveld.jlsvContext2d.measureText(commandName+"(");
		int width = (int) Math.round(tm.getWidth());
		// set separatorX		
		separatorX = 10+width;
		// allow height changes (body visible/invisible)
		isHeightFixed = false; 
	}
	
	/**
	 * Check validity of entire deeltaak header.
	 * This includes testing if name is an identifier, all parameters are identifiers
	 * and number of parameters smaller than or equal to maximum.
	 * @return true if all checks are ok.
	 */
	boolean isHeaderValid()
	{
		return deeltaaknaamParam.isCorrect() && pmParam.isCorrect();
	}

	/**
	 * process the text entered in the PopupPanel for editing
	 */
	public void parameterEdited(String text)
	{
		// name
		if ( isEditingName )
		{
			deeltaaknaamParam.setParameter(text);
			isEditingName = false;
			tm = schuifveld.jlsvContext2d.measureText(deeltaaknaamParam.getParameterText()+"(");
			int tWidth = (int) Math.round(tm.getWidth());
			// adjust separatorX
			separatorX = 10+tWidth;
		} 
		// parameter from list
		else if ( isEditingParamName )
		{
			pmParam.setParameter(text);
			isEditingParamName = false;
		}
		// remove input PopupPanel
		if (naamEditor != null)
		{	naamEditor.hide();
		}
		schuifveld.paint();
	}

	/**
	 * start editing subroutine name or one of its parameters
	 * @param name flagg for name/parameters
	 */
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
		// show input PopupPanel
		showParamEditor(name);
	}

	/**
	 * show PopupPanel for editing name or one of the parameters
	 * @param name flagg for name/parameters
	 */
	public void showParamEditor(boolean name)
	{
		int popupX = xPos + schuifveld.getAbsoluteLeft();
		if (!name)
			popupX = xPos + separatorX + schuifveld.getAbsoluteLeft();
		
		int popupY = yPos + headerHeight + schuifveld.getAbsoluteTop();
		
		// check if some other PopupPanel is open: process and close it
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
	 * Handle mouse actions to start and stop editing parameters.
	 * Note: all resize operations are handled directly by MouseListener in superclass ProgrammaComponent, 
	 */
	public void parameterComponentClicked(int x, int y)
	{
		boolean newEdit;
		// clicked on name
		boolean onName = ( x < separatorX );
		if ( isEditingName )
		{	// newEdit true: going from name to value
			newEdit = !onName;				
			parameterEdited(naamEditor.getText());
		}
		else if ( isEditingParamName )
		{	// newEdit true: going from value to name
			newEdit = onName;			
			parameterEdited(naamEditor.getText());
		} 
		else
		{	// we weren't editing anything, so start
			newEdit = true;					
		}
		// clicked on header part
		if ( newEdit && y <= headerHeight)
		{	
			editParameter(onName);
		} 
		else 
		{	// remove input PopupPanel
			if (naamEditor != null)
			{	naamEditor.hide();
			}	
		}
		schuifveld.paint();
	}
	
	/**
	 * Set name and parameter directly (ProgrammaImporter)
	 * @param n name of subroutine
	 * @param p subroutine parameter
	 */
	void setDeeltaakHeader(String n, String p)
	{
		deeltaaknaamParam.setParameter(n);
		pmParam.setParameter(p);
	}
	
	/**
	 * Gets the name of this ProgrammaComponent, regardless of its correctness
	 * Note: when printing or exporting, we also want incorrect names. 
	 * @return	the given name for this deeltaak
	 */
	public String getProgramName()
	{
		return deeltaaknaamParam.getParameterText();
	}
	
	/**
	 * Get the number of parameters of this deeltaak
	 * Note: returns the number also when one or more identifiers are incorrect!
	 * @return	number of parameters
	 */
	int getParameterCount()
	{
		return pmParam.getIdCount();
	}
	
	/**
	 * Get name of the parameter. Will return empty string if there isn't one.
	 * @param n name of parameter number n
	 * @return	parameter name or empty string
	 */
	String getParameterName(int n)
	{
		return pmParam.getIdentifier(n);
	}
	
	/**
	 * DeeltaakBodyComponents retains its width when being dragged
	 * @see fi.weblogogwt.client.CommandComponent#getDragWidth()
	 */
	int getDragWidth()
	{
		return getWidth();
	}
	
	/**
	 * DeeltaakBody will not be traced ( no carets while arranging the DTB's in the ProgrammaPanel)
	 * @see fi.weblogogwt.client.CommandComponent#isTraceable()
	 */
	boolean isTraceable()
	{
		return false;
	}
	
	/**
	 *	adapt height of deeltaakBody (that is the height of header+body);
	 *  called by CContainer commandBlock when its height has changed
	 *  h is not used here   
	 */
	void containerHeightChanged(int h)
	{	
		// if necessary make this deeltaakBody as hight as possible  
		if ( !isHeightFixed && isOpen )
		{	int newh;
			newh = Math.min(schuifveld.getHeight()-20, Math.max(pcminoh, commandBlock.getContentHeight()+headerHeight+20));
			setSize(getWidth(), newh);
			setLocation(getX(), Math.min(getY(), Math.max(0,JavaLogoSchuifVeld.pph-newh)));
		}
		
		int heightSurplus = commandBlock.getHeight()-(getHeight()-headerHeight);
		if ( heightSurplus > 0 )
		{	int newY = yPos + headerHeight - heightSurplus;
			commandBlock.setLocation(commandBlock.getX(), newY);
		}
		else
			commandBlock.setLocation(commandBlock.getX(), yPos+headerHeight);
	}
	
	/**
	 * redefined from CommandComponent, prevent this deeltaakBody from being dragged
	 * (partially) outside of the programPanel    
	 */
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
	
	/**
	 * paint name + parameterlist
	 */
	protected void paintCommand(Context2d g)
	{
		g.setFont(WebLogoGWT.boldFontString);
		g.setFillStyle(CssColor.make(0,0,0));

		// extra space above PopupPanel for input when editing
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
			{	// red
				g.setFillStyle(CssColor.make(255,0,0));
			}
			// subroutine name
			g.fillText(deeltaaknaamParam.getParameterText(), xPos+10, yPos+18);
			tm = schuifveld.jlsvContext2d.measureText(deeltaaknaamParam.getParameterText());
			int tWidth = (int) Math.round(tm.getWidth());
			xpos = xpos+tWidth;
			// black
			g.setFillStyle(CssColor.make(0,0,0));
			// opening parenthesis
			g.fillText("( ", xPos+xpos, yPos+18);
			tm = schuifveld.jlsvContext2d.measureText("(");
			tWidth = (int) Math.round(tm.getWidth());
			xpos = xpos + tWidth;
			if ( !pmParam.isCorrect() )
			{	// red
				g.setFillStyle(CssColor.make(255,0,0));
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
	
	/**
	 * Execute the content of this DeeltaakBodyComponent
	 */
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{
		return executeContent(trb, ub, varSet);
	}

	/**
	 * get the code of this subroutine (see ProgrammaExporter)
	 */
	public String getCode(String tab)
	{	
		String s = "\n" + WebLogoGWT.rb.deeltaak1Tekst() + " " + getProgramName() + "( " + pmParam.getParameterText() + " )" + "\n";
		return s+ super.getCode(tab)+"\n";
	}

}
