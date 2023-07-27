package fi.weblogo3dgwt.client;


import fi.weblogo3dgwt.client.parameters.TAParameter;
import fi.weblogo3dgwt.client.logotekenap3d.StringUtils;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * see class ParameterCommandComponent in WebLogoGWT
 */

public abstract class ParameterCommandComponent extends SimpleCommandComponent implements ParameterEditorListener
{
	/**
	 * Array of parameters.
	 * Maximum number is JavaLogoSchuifVeld.maxParamCount, a deeltaak may use parameters up to this number.
	 * Standard commands use 1 or 2.
	 */
	protected TAParameter[] parameters;
	protected int noParameters = 0;
	/**
	 * array of x-pos of the parameters, positions of TextField when editing param[i]
	 * The first one holds the position of the first parameter and is not used as separator.
	 * The others are also used to decide which parameter to edit, separator[i], i larger than 0 is the border
	 * between param{i-1] and param[i]
	 * An extra entry is used to put the closing bracket in the right place when editing the last parameter
	 * Note: values change when editting (or when a deeltaak name is changed by user), so they
	 * are calculated dynamically.
	 */
	protected int[] parameterPos;

	protected boolean isEditing = false;
	/**
	 * index of parameter that is currently being editted
	 */
	protected int epi;
	
	protected ParameterTextField paramEditor;

	protected TextMetrics tm;

	/**
	 * Strings opening and closing the parameterpart of the string representation of this CC.
	 */
	protected static final String strOpen = "( ";
	protected static final String strClose = " )";
	
	/**
	 * String for the part of the parameter section to be painted in front of the TextField when editing.
	 * Generally this is just strOpen, but when there are 2+ parameters, this will also hold the first param
	 * when editting the second.
	 */
	protected String strBeforeEditor;
	/**
	 * String for the part of the parameter section to be painted after the TextField when editing.
	 * Generally this is just strClose, but when there are 2+ parameters, this will also hold the second param
	 * when editting the first.
	 */
	protected String strAfterEditor;
	

	public ParameterCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{
		super(x, y, b, h, sv);
		
		parameters = new TAParameter[JavaLogoSchuifVeld.maxParamCount];
		parameterPos = new int[JavaLogoSchuifVeld.maxParamCount+1];
		strBeforeEditor = strOpen;
		strAfterEditor = strClose;
		
	}
	
	/**
	 * Find the indez of the parameter to be editted
	 * Pre: noParameters larger than 0
	 * @param x		x-pos of mouse click
	 * @return		index of parameter in array parameters
	 */
	private int findEditIndex(int x)
	{
		if ( noParameters <= 1) return 0;
		for ( int i=1; i<noParameters; i++)
		{
			// in front of pos[i] means parameter i-1!
			if ( x<parameterPos[i] ) return i-1;
		}
		return noParameters-1;
	}
	
	/**
	 * set the positions for all parameters, taking into account that param[editIndex] will be a TextField
	 * @param editIndex		the index of the parameter to be edit, or -1 if none is being edited.
	 */
	private void setParameterPositions(int editIndex)
	{
		schuifveld.jlsvContext2d.setFont(WebLogo3dGWT.fontString);
		
		tm = schuifveld.jlsvContext2d.measureText(getCommandName()+strOpen);
		int width = (int) Math.round(tm.getWidth());
		int currentX = 10+width;
		for ( int i=0; i<noParameters; i++ )
		{
			parameterPos[i]=currentX;
			if ( i == editIndex )
			{	// add width of TextField
				currentX = currentX+60;
			} 
			else
			{	tm = schuifveld.jlsvContext2d.measureText(parameters[i].getParameterText());
				width = (int) Math.round(tm.getWidth());
				// add width of parameter value printed
				currentX = currentX+width;
			}	
			tm = schuifveld.jlsvContext2d.measureText(", ");
			width = (int) Math.round(tm.getWidth());
			// add space for interpunction
			currentX = currentX+width;
		}
		parameterPos[noParameters]=currentX;	// position of closing bracket when editing last parameter
	}
	
	private void setBeforeAndAfterStrings(int index)
	{
		strBeforeEditor = strOpen;
		strAfterEditor = strClose;
		if ( noParameters <= 1) return;		// with 0 or 1 parameters we just have brackets, quick return
		// add parameters in front of editor to before-string
		for ( int i=0; i<index; i++)
		{
			strBeforeEditor = strBeforeEditor+parameters[i].getParameterText()+", ";
		}
		// add parameters in front of editor to after-string, reverse order!
		for ( int i=noParameters-1; i>index; i--)
		{
			strAfterEditor = ", "+parameters[i].getParameterText()+strAfterEditor;
		}
	}
	
	public void parameterEdited(String text)
	{
		parameters[epi].setParameter(text);
		isEditing = false;
		// tekstPopup weg
		if (paramEditor != null)
		{	paramEditor.hide();
		
		}

		schuifveld.paint();
	}

	public void parameterComponentClicked(int x, int y)
	{
		boolean newEdit = false;
		if ( isEditing )
		{
			parameters[epi].setParameter(paramEditor.getText());
			
			if ( noParameters > 1 )
			{									// possible switch to other parameter
				int newepi = findEditIndex(x);
				newEdit = ( newepi != epi );
				epi = newepi;
			} 
			else
			{									// just the one parameter: stop editing
				newEdit = false;
			}
		} 
		else
		{
			newEdit = (noParameters > 0);		// Note that deeltaak may have zero params, nothing to edit
			setParameterPositions(-1);			// calculate positions now (deeltaak name may have changed!)
			epi = findEditIndex(x);
		}
		if ( newEdit )
		{
			isEditing = true;
			setParameterPositions(epi);			// calculate positions with TextField for current param
			setBeforeAndAfterStrings(epi);
	
			showParamEditor();
		} 
		else
		{
			isEditing = false;
			
			// tekstPopup weg
			if (paramEditor != null)
			{	paramEditor.hide();
			
			}
			
		}
		schuifveld.paint();
	}

	public void showParamEditor()
	{
		int popupX = xPos + parameterPos[epi] + schuifveld.getAbsoluteLeft();
		
		int popupY = yPos + hoogte + schuifveld.getAbsoluteTop();
		
		// kijk of er ergens nog een popup open is
		if ((schuifveld.paramEditor != null) && schuifveld.paramEditor.isVisible())
		{
			if (schuifveld.paramEditor != paramEditor)
				schuifveld.paramEditor.owner.parameterEdited(schuifveld.paramEditor.getText());
		}

		schuifveld.paramEditor = new ParameterTextField(breedte, hoogte, this, schuifveld);
		paramEditor = schuifveld.paramEditor; 
		paramEditor.vulIn(parameters[epi].getParameterText());
		paramEditor.setPopupPosition(popupX, popupY);
		paramEditor.show();
		paramEditor.textBox.setFocus(true);
		
		schuifveld.paint();

	}
 
	/**
	 * Set parameter(s) directly (ProgrammaImporter)
	 * Will import nothing if the number of parameters does not match the number of comma separated strings.
	 * Note: there's a little bit of mess here. Color parameters may have commas that are NOT separators,
	 * Strings for print commands may contain commas. So a QUICK FIX is applied. If we have 1 parameter
	 * we import the lot into that parameter without looking for commas. :-)
	 * @param s String for parameter
	 */
	void setParameter(String s)
	{
		if ( noParameters == 0 ) return;
		if ( noParameters == 1 ) 
		{
			parameters[0].setParameter(s);
			return;
		}
		// so we're left with just the >1 parameter lot
		String[] params = StringUtils.split(s, ",");
		if ( params.length != noParameters ) return;
		for ( int i=0; i<noParameters; i++ )
		{
			parameters[i].setParameter(params[i].trim());
		}
	}
	
	/**
	 * Get the parameter text. 
	 * @return 	combined parameter texts
	 */
	protected String getFullParameterText()
	{
		if ( noParameters == 0 ) return "";
		String s = parameters[0].getParameterText();
		for ( int i=1; i<noParameters; i++ )
		{
			s = s + ", " + parameters[i].getParameterText();
		}
		return s;
	}
	
	/**
	 * Checks if parameters are ok. Again, before running this is only a syntax check
	 * @return	boolean
	 */
	protected boolean isCorrect()
	{
		for ( int i=0; i<noParameters; i++ )
		{
			if ( !parameters[i].isCorrect() ) return false;
		}
		return true;
	}
	
	protected void paintCommand(Context2d g)
	{
		g.setFont(WebLogo3dGWT.fontString);
		g.setFillStyle(CssColor.make(0,0,0));
		if (isEditing)
		{
			g.fillText(getCommandName()+strBeforeEditor, xPos+10, yPos+18);

			g.fillText(strAfterEditor, xPos+parameterPos[epi+1], yPos+18);
		} 
		else
		{
			if (!isCorrect())
			{	
				g.setFillStyle(CssColor.make(255,0,0));
			}
			// display command text or part of it depending on breedte
			String fullText = getCommandName() + strOpen +  getFullParameterText() + strClose;
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
				g.fillText(fullText,xPos+10, yPos+18);

		}
	}

	/**
	 * Get a string value of the command in this component. 
	 * Implemented here for convenience so simple parameter commands don't need to override.
	 * More complex ones (color?), however, must override
	 */
	public String getCode(String tab)
	{	
		String s = tab + getCommandName() + strOpen +  getFullParameterText() + strClose + "\n";
		return s;
	}
	/**
	 * Returns the actual command with values of parameters.
	 * Note: this method MUST only be called after parameters have been checked for correctness
	 * @return		String, command with calculated values of parameters
	 */
	protected String getActualCall()
	{
		String s = getCommandName() + strOpen;
		for ( int i=0; i<noParameters; i++ )
		{
			s = s + parameters[i].getValueText();
			if ( i < noParameters-1 )		// add comma, but not after last one
				s = s+", ";
		}
		s = s + strClose;
		return s;
	}
}
