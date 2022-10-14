package fi.weblogogwt.client;


import fi.weblogogwt.client.expressies.BasisExpressie;
import fi.weblogogwt.client.expressies.Expressie;
import fi.weblogogwt.client.formuleobjects.FormuleParser;
import fi.weblogogwt.client.logotekenap.TraceBeheerder;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

import fi.weblogogwt.client.logotekenap.Uitvoerblad;
import fi.weblogogwt.client.parameters.Identifier;
import fi.weblogogwt.client.parameters.NumericParameter;
import fi.weblogogwt.client.parameters.TextParameter;

/**
 * class representing the declaration of a variable; the class has two parameters:
 * an Identifier containing the variable name (default "variable") and a numeric
 * parameter containing an Expression evaluating to the value of the variable; 
 *
 */

public class VarInputComponent extends SimpleCommandComponent implements ParameterEditorListener
{
	/**
	 * Expression evaluating to the value of the variable
	 */
	private TextParameter waarde;
	/**
	 * variable name
	 */
	private Identifier varnaamParam;
	
	/**
	 * editing the name?
	 */
	private boolean editingName = false;
	/**
	 * editing the value (expression)?
	 */
	private boolean editingValue = false;
	
	/**
	 * PopupPanel for editing, see class ParameterTextField 
	 */
	ParameterTextField paramEditor;
	
	/**
	 * x-position of value-part
	 */
	private int separatorX;
	/**
	 * String separating name and value
	 */
	private String equalsString = " = ";
	/**
	 * width of equalsString in pixels
	 */
	private int equalsWidth;
	/**
	 * instance of TextMetrics for determining width of Strings
	 */
	TextMetrics tm;
	
	/**
	 * constructor
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 * @param sv instance of JavaLogoSchuifVeld sv containing the drawing Canvas; necessary for superclass constructor
	 */
	public VarInputComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		// is virtually irrelevant, parameter holds the real name
		commandName = WebLogoGWT.rb.inputTekst()  + " = input( )";			
		commandNameTranslated = "input"; 
		waarde = new TextParameter();
		varnaamParam = new Identifier(WebLogoGWT.rb.variabeleTekst());
		schuifveld.jlsvContext2d.setFont(WebLogoGWT.fontString);
		tm = schuifveld.jlsvContext2d.measureText(commandName+equalsString);
		int width = (int) Math.round(tm.getWidth());
		separatorX = 10+width; 
		tm = schuifveld.jlsvContext2d.measureText(equalsString);
		equalsWidth = (int) Math.round(tm.getWidth()); 
	}
	
	/**
	 * process the String text (from the input PopupPanel)
	 */
	public void parameterEdited(String text)
	{
		if ( editingName )
		{
			varnaamParam.setParameter(text);
			commandName = varnaamParam.getParameterText();
			editingName = false;
			schuifveld.jlsvContext2d.setFont(WebLogoGWT.fontString);
			tm = schuifveld.jlsvContext2d.measureText(varnaamParam.getParameterText()+equalsString);
			int width = (int) Math.round(tm.getWidth());
			separatorX = 10+width; 
		} 
		else if ( editingValue )
		{
			waarde.setParameter(text);
			commandName = varnaamParam.getParameterText();
			editingValue = false;
		}
		// remove PopupPanel for editing
		if (paramEditor != null)
		{	paramEditor.hide();
		}
		schuifveld.paint();
	}

	/**
	 * set editint to name or value 
	 * @param name name or value
	 */
	public void editParameter(boolean name)
	{
		if ( name )
		{	editingName = true;
		}
		else
		{	editingValue = true;
		}
		// show PopuPanel for input
		showParamEditor(name);
	}

	/**
	 * show PopupPanel for input below name of value; process and close any other open PopupPnales
	 * @param name editing name or value
	 */
	public void showParamEditor(boolean name)
	{
		int popupX = xPos + schuifveld.getAbsoluteLeft();
		if (!name)
			popupX = xPos + separatorX + schuifveld.getAbsoluteLeft();
		int popupY = yPos + hoogte + schuifveld.getAbsoluteTop();
		// check if otehr PopupPanels are open
		if ((schuifveld.paramEditor != null) && schuifveld.paramEditor.isVisible())
		{	if ((!editingName && !editingValue) || (schuifveld.paramEditor != paramEditor))
				schuifveld.paramEditor.owner.parameterEdited(schuifveld.paramEditor.getText());
		}
		// create a new PopupPanel
		schuifveld.paramEditor = new ParameterTextField(breedte, hoogte, this, schuifveld);
		paramEditor = schuifveld.paramEditor; //new ParameterTextField(breedte, hoogte, this, schuifveld);
		if (name)
			paramEditor.vulIn(varnaamParam.getParameterText());
		else
			paramEditor.vulIn(waarde.getParameterText());
		paramEditor.setPopupPosition(popupX, popupY);
		paramEditor.show();
		paramEditor.textBox.setFocus(true);
	}

	/**
	 * Determine what to edit given the click on position x,y
	 * This method looks a bit messy because the flow is:
	 * 1. when currently not editing: edit the part nearest to x
	 * 2. when editing a part and x is near the other part: switch editing to the other part
	 * 3. when editing a part and x is near the same part: stop editing.
	 */
	public void parameterComponentClicked(int x, int y)
	{
		boolean newEdit;
		boolean onName = ( x < separatorX );
		if ( editingName )
		{	// newEdit true: going from name to value
			newEdit = !onName;				
			parameterEdited(paramEditor.getText());
		}
		else if ( editingValue )
		{	// newEdit true: going from value to name
			newEdit = onName;	
			parameterEdited(paramEditor.getText());
		} 
		else
		{	// we weren't editing anything, so start
			newEdit = true;				
		}
		if ( newEdit )
		{	editParameter(onName);
		} 
		else
		{	// remove PopuPanel
			if (paramEditor != null)
			{	paramEditor.hide();
			}
		}
		schuifveld.paint();
	}
	
	/**
	 * Set varname and expression directly (ProgrammaImporter)
	 * @param name name of variable
	 * @param exp expression for variable (String)
	 */
	void setVariable(String name, String exp)
	{
		varnaamParam.setParameter(name.trim());
		schuifveld.jlsvContext2d.setFont(WebLogoGWT.fontString);
		tm = schuifveld.jlsvContext2d.measureText(varnaamParam.getParameterText()+equalsString);
		int width = (int) Math.round(tm.getWidth());
		separatorX = 10+width; 
		waarde.setParameter(exp);
	}
	
	/**
	 * check the name and value of this command for correctness; if correct,
	 * add this variable to the current variable set;
	 * execute this command, if tracing, change its color and display
	 * the command and parameter; see class TraceBeheerder
	 */

	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{	return false;
	}
	
	TraceBeheerder trb;
	ProgrammaComponent pc;
	VarSet varSet;
	public boolean execute(Uitvoerblad ub)
	{	
		// determine the correctness of the expression for real, with the current varSet!
		if ( !(varnaamParam.isCorrect()  && waarde.isCorrect(varSet)) ) 
			return false; 
		String inputString = ub.input( ((TextParameter)waarde).getValueText());
		Expressie inputWaarde = FormuleParser.parse(inputString);
		varSet.setVar(varnaamParam.getParameterText(), inputWaarde);	// TODO aanpassen: moet van ub komen	
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if (traceKleur) traceKleurCnt = 0;
		if ( traceKleur ) 
		{	trb.setCommandInfo(varnaamParam.getParameterText()+" = "+inputWaarde.toString(), varSet);
		}
		return traceKleur;
	}
	
	public void preExecute(ProgrammaComponent pc, TraceBeheerder trb, Uitvoerblad ub, VarSet varSet) {
		this.pc = pc;
		this.trb = trb;
		this.varSet = varSet;
		ub.prepareInput(this,((TextParameter)waarde).getValueText());
	}
	
	/**
	 * paint the command; use a different format when editing name or value 
	 */
	protected void paintCommand(Context2d g)
	{
		g.setFont(WebLogoGWT.fontString);
		g.setFillStyle(CssColor.make(0,0,0));
		if ( editingName )
		{	g.fillText(equalsString+"input( " + waarde.getParameterText() + " )", xPos+separatorX, yPos+18);
			
		}
		else if ( editingValue )
		{	g.fillText(varnaamParam.getParameterText()+equalsString + "input( )", xPos+10, yPos+18);
		} 
		else
		{	// smaller painted command when dragging
			if ((parent == null) && dragging)
			{	g.setFillStyle(CssColor.make(0,0,0));
				g.fillText(commandName, xPos+10, yPos+18);
			}
			else
			{	// paint parts of the equation in RED if they are incorrect;
				if ( !varnaamParam.isCorrect() ) 
				{	g.setFillStyle(CssColor.make(255,0,0));
				}
				String varPart = varnaamParam.getParameterText()+equalsString;
				TextMetrics tm = g.measureText(varPart);
				int varPartWidth = (int) Math.round(tm.getWidth());
				//g.fillText(varnaamParam.getParameterText(), xPos+10, yPos+18);
				g.fillText(varPart, xPos+10, yPos+18);
				//g.setFillStyle(CssColor.make(0,0,0));
				//if (!(varnaamParam.isCorrect() && waarde.isCorrect()) ) 
				//{	g.setFillStyle(CssColor.make(255,0,0));
				//}	
				//g.fillText(equalsString, xPos+separatorX-equalsWidth, yPos+18);
				g.setFillStyle(CssColor.make(0,0,0));
				if ( !waarde.isCorrect() )
				{	//g.setColor(Color.RED);
					g.setFillStyle(CssColor.make(255,0,0));
				}
				String waardePart = "input( " + waarde.getParameterText() + " )";
				tm = g.measureText(waardePart);
				int waardePartWidth = (int) Math.round(tm.getWidth());
				int textWidth = varPartWidth + waardePartWidth;
				if (textWidth > breedte - 10)
				{	
					// omit characters until fit
					while (textWidth > breedte - 10)
					{
						waardePart = waardePart.substring(0, waardePart.length() - 1);
						tm = g.measureText(waardePart);
						waardePartWidth = (int) Math.round(tm.getWidth());
						textWidth = varPartWidth + waardePartWidth;
					}
					g.fillText(waardePart, xPos+10+varPartWidth, yPos+18);
				}	
				else
					g.fillText(waardePart, xPos+10+varPartWidth, yPos+18);
			}
		}
	}
	
	public String getCode(String tab)
	{	String s = tab + varnaamParam.getParameterText()+equalsString+waarde.getParameterText() + "\n";
		return s;
	}	
}
