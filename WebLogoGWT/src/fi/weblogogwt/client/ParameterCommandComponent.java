package fi.weblogogwt.client;


import fi.weblogogwt.client.formuleobjects.*;
import fi.weblogogwt.client.parameters.TAParameter;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * superclass for simple commands which contain one or more parameters, which can be edited 
 * (see interface ParameterEditorListener); editing is started by a double or long click
 * on the relevant parameter, which opens a PopupPanel just below the parameter to be edited;
 * see class ParameterTextField (confusing name inherited from the Java-version) 
 */

public abstract class ParameterCommandComponent extends SimpleCommandComponent implements ParameterEditorListener
{
	/**
	 * Array of parameters.
	 * Maximum number is JavaLogoSchuifVeld.maxParamCount, a deeltaak may use parameters up to this number.
	 * Standard commands use 1 or 2.
	 */
	protected TAParameter[] parameters;
	/**
	 * the actual number of parameters
	 */
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

	/**
	 * is one of the parameters being edited?
	 */
	protected boolean isEditing = false;
	/**
	 * index of parameter that is currently being edited
	 */
	protected int epi;
	
	/**
	 * the device for editing a parameter; not that this is NOT a TextField, but a PopupPanel;
	 * see class ParameterTextField
	 */
	protected ParameterTextField paramEditor;

	/**
	 * instance of TextMetrics for measuring width of Strings
	 */
	protected TextMetrics tm;

	/**
	 * Strings opening and closing the parameter part of the string representation of this CC.
	 */
	protected static final String strOpen = "( ";
	protected static final String strClose = " )";
	
	/**
	 * String for the part of the parameter section to be painted in front of the TextField when editing.
	 * Generally this is just strOpen, but when there are 2+ parameters, this will also hold the first parameter
	 * when editing the second.
	 */
	protected String strBeforeEditor;
	/**
	 * String for the part of the parameter section to be painted after the TextField when editing.
	 * Generally this is just strClose, but when there are 2+ parameters, this will also hold the second parameter
	 * when editing the first.
	 */
	protected String strAfterEditor;
	
	/**
	 * constructor
	 * @param x x-position 
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param sv instance of JavaLogoSchuifVeld containing the drawing Canvas
	 */
	public ParameterCommandComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	super(x, y, b, h, sv);
		parameters = new TAParameter[JavaLogoSchuifVeld.maxParamCount];
		parameterPos = new int[JavaLogoSchuifVeld.maxParamCount+1];
		strBeforeEditor = strOpen;
		strAfterEditor = strClose;
	}
	
	/**
	 * Find the index of the parameter to be edited
	 * Pre: noParameters larger than 0
	 * @param x		x-position of mouse click
	 * @return		index of parameter in array parameters
	 */
	private int findEditIndex(int x)
	{
		if (noParameters <= 1) 
			return 0;
		for (int i = 1; i <noParameters; i++)
		{	// in front of pos[i] means parameter i-1!
			if (x < parameterPos[i]) 
				return i-1;
		}
		return noParameters-1;
	}
	
	/**
	 * set the positions for all parameters, taking into account that param[editIndex] should have the same width
	 * as the PopupPanel for editing
	 * @param editIndex		the index of the parameter to be edited, or -1 if none is being edited.
	 */
	private void setParameterPositions(int editIndex)
	{	schuifveld.jlsvContext2d.setFont(WebLogoGWT.fontString);
		tm = schuifveld.jlsvContext2d.measureText(getCommandName()+strOpen);
		int width = (int) Math.round(tm.getWidth());
		int currentX = 10+width;
		for (int i = 0; i < noParameters; i++)
		{	parameterPos[i] = currentX;
			if (i == editIndex )
			{	// add width of PopupPanel
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
		// position of closing bracket when editing last parameter
		parameterPos[noParameters] = currentX;	
	}

	/**
	 * determine strBeforeEditor and strAfterEditor given the index of the parameter
	 * to be edited; see description of strBeforeEditor and strAfterEditor
	 * @param index index of parameter to be edited
	 */
	private void setBeforeAndAfterStrings(int index)
	{
		strBeforeEditor = strOpen;
		strAfterEditor = strClose;
		// with 0 or 1 parameters we just have brackets, quick return
		if ( noParameters <= 1) 
			return;		
		// add parameters in front of the edited parameter to before-string
		for (int i = 0; i<index; i++)
		{	strBeforeEditor = strBeforeEditor+parameters[i].getParameterText()+", ";
		}
		// add parameters after the edited parameter to after-string, reverse order!
		for ( int i=noParameters-1; i>index; i--)
		{	strAfterEditor = ", "+parameters[i].getParameterText()+strAfterEditor;
		}
	}
	
	/**
	 * set the value of the parameter being edited to text; close the
	 * input PopupPanel
	 */
	@Override
	public void parameterEdited(String text)
	{	parameters[epi].setParameter(text);
		isEditing = false;
		// remove PopupPanel for editing
		if (paramEditor != null)
		{	paramEditor.hide();
		}
		schuifveld.paint();
	}

	/**
	 * action at double or long click at this ParameterCommandComponent
	 * at position (x,y); determine whether editing of one of the parameters should 
	 * start or editing is switched to another parameter;
	 */
	@Override
	public void parameterComponentClicked(int x, int y)
	{	boolean newEdit = false;
		if (isEditing)
		{	// finish editing parameters[epi]
			parameters[epi].setParameter(paramEditor.getText());
			// possible switch to other parameter
			if ( noParameters > 1 )
			{	int newepi = findEditIndex(x);
				newEdit = ( newepi != epi );
				epi = newepi;
			} 
			else // just the one parameter: stop editing
			{	newEdit = false;
			}
		} 
		else
		{	// Note that deeltaak may have zero params, nothing to edit
			newEdit = (noParameters > 0);		
			// calculate positions now (deeltaak name may have changed!)
			setParameterPositions(-1);			
			epi = findEditIndex(x);
		}
		if ( newEdit )
		{	isEditing = true;
			// calculate positions with width of PopupPanel for current param
			setParameterPositions(epi);			
			setBeforeAndAfterStrings(epi);
			// show PopupPanel
			showParamEditor();
		} 
		else
		{	isEditing = false;
			// remove PopupPanel
			if (paramEditor != null)
			{	paramEditor.hide();
			}
		}
		schuifveld.paint();
	}

	/** 
	 * show the PopupPanel for input below the parameter being edited
	 */
	public void showParamEditor()
	{	int popupX = xPos + parameterPos[epi] + schuifveld.getAbsoluteLeft();
		int popupY = yPos + hoogte + schuifveld.getAbsoluteTop();
		// check if some other PopupPanel is open, process the content and close it
		if ((schuifveld.paramEditor != null) && schuifveld.paramEditor.isVisible())
		{	if (schuifveld.paramEditor != paramEditor)
				schuifveld.paramEditor.owner.parameterEdited(schuifveld.paramEditor.getText());
		}
		// make a new PopupPanel
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
	 * @param s the String
	 */
	void setParameter(String s)
	{
		if (noParameters == 0) 
			return;
		if (noParameters == 1) 
		{	parameters[0].setParameter(s);
			return;
		}
		// so we're left with just the more than 1 parameter lot
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
		if (noParameters == 0) 
			return "";
		String s = parameters[0].getParameterText();
		for ( int i=1; i<noParameters; i++ )
		{	s = s + ", " + parameters[i].getParameterText();
		}
		return s;
	}
	
	/**
	 * Checks if all parameters are ok. Again, before running this is only a syntax check
	 * @return	boolean
	 */
	protected boolean isCorrect()
	{	for (int i = 0; i < noParameters; i++)
		{	if (!parameters[i].isCorrect() ) 
			return false;
		}
		return true;
	}

	/**
	 * paint the command, check if any parameter is being edited
	 */
	protected void paintCommand(Context2d g)
	{	g.setFont(WebLogoGWT.fontString);
		g.setFillStyle(CssColor.make(0,0,0));
		if (isEditing)
		{	// before edit
			g.fillText(getCommandName()+strBeforeEditor, xPos+10, yPos+18);
			// after edit
			g.fillText(strAfterEditor, xPos+parameterPos[epi+1], yPos+18);
		} 
		else // not editing
		{	// command is not correct
			if (!isCorrect())
			{	g.setFillStyle(CssColor.make(255,0,0));
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
				
/*				
				tm = g.measureText(getCommandName());
				textWidth = (int) Math.round(tm.getWidth()); 
				if (textWidth > breedte - 10)
				{	g.fillText(getCommandName().substring(0,1),xPos+10,yPos+18);
				}
				else
					g.fillText(getCommandName(),xPos+10,yPos+18);
*/					
			}
			else // display full text
				g.fillText(fullText,xPos+10, yPos+18);
		}
	}

	/**
	 * Get a string value of the command in this component. 
	 * Implemented here for convenience so simple parameter commands don't need to override.
	 * More complex ones (color?), however, must override
	 */
	@Override
	public String getCode(String tab)
	{	String s = tab + getCommandName() + strOpen +  getFullParameterText() + strClose + "\n";
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
		for (int i = 0; i < noParameters; i++)
		{	s = s + parameters[i].getValueText();
		// add comma, but not after last one
			if ( i < noParameters-1 )
				s = s+", ";
		}
		s = s + strClose;
		return s;
	}
}
