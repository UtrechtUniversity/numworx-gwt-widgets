package fi.weblogo3dgwt.client;

//import java.awt.Color;
//import java.awt.FontMetrics;
//import java.awt.Graphics;

import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;
import fi.weblogo3dgwt.client.logotekenap3d.StringUtils;
import fi.weblogo3dgwt.client.expressies.*;
import fi.weblogo3dgwt.client.parameters.Identifier;
import fi.weblogo3dgwt.client.parameters.NumericParameter;

public class VarCComponent extends SimpleCommandComponent implements ParameterEditorListener
{
	private NumericParameter waarde;
	private Identifier varnaamParam;
	
	private boolean editingName = false;
	private boolean editingValue = false;
	
	ParameterTextField paramEditor;
	
	private int separatorX;
	private String equalsString = " = ";
	private int equalsWidth;
	//FontMetrics fm;
	TextMetrics tm;
	
	public VarCComponent(int x, int y, int b, int h, JavaLogoSchuifVeld sv)
	{	
		super(x,y,b,h,sv);
		commandName = WebLogo3dGWT.rb.variabeleTekst(); //"variabele";				// is virtually irrelevant, parameter holds the real name
		commandNameTranslated = "variable"; //JavaLogoWeb.rb.getString(commandName);
		waarde = new NumericParameter();
		varnaamParam = new Identifier(commandName);
		
		//fm = getFontMetrics(JavaLogoWeb.defaultfont);
		schuifveld.jlsvContext2d.setFont(WebLogo3dGWT.fontString);
		
		tm = schuifveld.jlsvContext2d.measureText(commandName+equalsString);
		int width = (int) Math.round(tm.getWidth());
				
		separatorX = 10+width; // fm.stringWidth(commandName+equalsString);
		
		tm = schuifveld.jlsvContext2d.measureText(equalsString);
		equalsWidth = (int) Math.round(tm.getWidth()); //fm.stringWidth(equalsString);
		
		//paramEditor = new ParameterTextField(10, 4, 60, 17, this);
		//add(paramEditor);
		
//System.out.println("VarCC constr sepX = " + separatorX);		
	}
	
	@Override
	public void parameterEdited(String text)
	{
		if ( editingName )
		{
//System.out.println("paramEdit name " + editingName + " " + text);

			varnaamParam.setParameter(text);
			
			commandName = text + " = " + waarde.getParameterText();
			
			editingName = false;
			
			schuifveld.jlsvContext2d.setFont(WebLogo3dGWT.fontString);
			
			tm = schuifveld.jlsvContext2d.measureText(varnaamParam.getParameterText()+equalsString);
			int width = (int) Math.round(tm.getWidth());
			
			separatorX = 10+width; //fm.stringWidth(varnaamParam.getParameterText()+equalsString);
			
		} 
		else if ( editingValue )
		{
//System.out.println("paramEdit value " + editingValue + " " + text);			
			waarde.setParameter(text);
			
			commandName = varnaamParam.getParameterText() + " = " + text;
			
			editingValue = false;
		}
		// tekstPopup weg
		if (paramEditor != null)
		{	paramEditor.hide();
		}

		schuifveld.paint();
	}

	public void editParameter(boolean name)
	{
		if ( name )
		{
			//separatorX = breedte / 2;

// zie showParamEditor(name)			
			//paramEditor.setLocation(10, 4);
			//paramEditor.vulIn(varnaamParam.getParameterText());
			//separatorX = paramEditor.getX()+paramEditor.getWidth()+1;
			editingName = true;
		}
		else
		{
			
// zie showParamEditor(name)		
			//paramEditor.setLocation(separatorX+2, 4);
			//paramEditor.vulIn(waarde.getParameterText());
			editingValue = true;
		}
		
		showParamEditor(name);
		//schuifveld.tekenOpnieuw();
	}

	public void showParamEditor(boolean name)
	{
		int popupX = xPos + schuifveld.getAbsoluteLeft();
		if (!name)
			popupX = xPos + separatorX + schuifveld.getAbsoluteLeft();
		
		int popupY = yPos + hoogte + schuifveld.getAbsoluteTop();
		
		// kijk of er ergens nog een popup open is
		if ((schuifveld.paramEditor != null) && schuifveld.paramEditor.isVisible())
		{
			if ((!editingName && !editingValue) || (schuifveld.paramEditor != paramEditor))
				schuifveld.paramEditor.owner.parameterEdited(schuifveld.paramEditor.getText());
		}

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
	 * Determine what to edit given the click on pos x,y
	 * 
	 * This method looks a bit messy because the flow is:
	 * 1. when currently not editing: edit the part nearest to x
	 * 2. when editing a part and x is near the other part: switch editing to the other part
	 * 3. when editing a part and x is near the same part: stop editing.
	 * 
	 * @see fi.javalogoweb.ParameterEditorListener#parameterComponentClicked(int, int)
	 */
	@Override
	public void parameterComponentClicked(int x, int y)
	{
		boolean newEdit;
		boolean onName = ( x < separatorX );
		
//System.out.println("VarCC paramCCClicked " + x + " sepX " + separatorX);
//System.out.println("VarCC paramCCClicked " + editingName + " -- " + editingValue);
		if ( editingName )
		{
			newEdit = !onName;				// newEdit true: going from name to value
			
			parameterEdited(paramEditor.getText());
		}
		else if ( editingValue )
		{
			newEdit = onName;				// newEdit true: going from value to name
			
			parameterEdited(paramEditor.getText());
		} 
		else
		{
			newEdit = true;					// we weren't editing anything, so start
		}
		if ( newEdit )
		{	
			editParameter(onName);
		} 
		else
		{
			// tekstPopup weg
			if (paramEditor != null)
			{	paramEditor.hide();
			
			}

			//paramEditor.setVisible(false);
			//paramEditor.setEnabled(false);
			
		}
		schuifveld.paint();
	}
	
	/**
	 * Set varnamee & expression directly (ProgrammaImporter)
	 * 
	 * @param name
	 * @param exp
	 */
	void setVariable(String name, String exp)
	{
		varnaamParam.setParameter(name.trim());
		
		schuifveld.jlsvContext2d.setFont(WebLogo3dGWT.fontString);
		
		tm = schuifveld.jlsvContext2d.measureText(varnaamParam.getParameterText()+equalsString);
		int width = (int) Math.round(tm.getWidth());
		
		separatorX = 10+width; //fm.stringWidth(varnaamParam.getParameterText()+equalsString);
		waarde.setParameter(exp);
	}
	
	public boolean execute(TraceBeheerder trb, TekenApplet3D ub, VarSet varSet)
	{	
		// don't add to VarSet when name is wrong or expression is wrong 
		// determine the correctness of the expression for real, with the current varSet!
		if ( !(varnaamParam.isCorrect()  && waarde.isCorrect(varSet)) ) return false; 
		varSet.setVar(varnaamParam.getParameterText(), waarde.getExpressie());		
		
		traceKleur = trb.commandExecuted(varSet.getLevel());
		if ( traceKleur ) 
		{
			trb.setCommandInfo(varnaamParam.getParameterText()+" = "+waarde.getValueText(), varSet);
		}
		return traceKleur;
	}
	
	@Override
	//protected void paintCommand(Graphics g)
	protected void paintCommand(Context2d g)
	{
		//g.setFont(JavaLogoWeb.defaultfont);
		g.setFont(WebLogo3dGWT.fontString);
		//g.setColor(Color.BLACK);
		g.setFillStyle(CssColor.make(0,0,0));
		if ( editingName )
		{
			//g.drawString(equalsString+waarde.getParameterText(), separatorX, 18);
			g.fillText(equalsString+waarde.getParameterText(), xPos+separatorX, yPos+18);
			
		}
		else if ( editingValue )
		{
			//g.drawString(varnaamParam.getParameterText()+equalsString, 10, 18);
			g.fillText(varnaamParam.getParameterText()+equalsString, xPos+10, yPos+18);
		} 
		else
		{	
			if ((parent == null) && dragging)
			{	g.setFillStyle(CssColor.make(0,0,0));
				g.fillText(commandName, xPos+10, yPos+18);
			}
			
			else
			{	
			// paint parts of the equation in RED if they are incorrect;
			if ( !varnaamParam.isCorrect() ) 
			{	//g.setColor(Color.RED);
				g.setFillStyle(CssColor.make(255,0,0));
			}	
			//g.drawString(varnaamParam.getParameterText(), 10, 18);
			g.fillText(varnaamParam.getParameterText(), xPos+10, yPos+18);
			
			//g.setColor(Color.BLACK);
			g.setFillStyle(CssColor.make(0,0,0));
			if ( !(varnaamParam.isCorrect()  && waarde.isCorrect()) ) 
			{	//g.setColor(Color.RED);
				g.setFillStyle(CssColor.make(255,0,0));
			}	

			//g.drawString(equalsString, separatorX-equalsWidth, 18);
			g.fillText(equalsString, xPos+separatorX-equalsWidth, yPos+18);
			
			//g.setColor(Color.BLACK);
			g.setFillStyle(CssColor.make(0,0,0));
			if ( !waarde.isCorrect() )
			{	//g.setColor(Color.RED);
				g.setFillStyle(CssColor.make(255,0,0));
			}	

			//g.drawString(waarde.getParameterText(), separatorX, 18);
			g.fillText(waarde.getParameterText(), xPos+separatorX, yPos+18);
			}
		}
	}
	
	public String getCode(String tab)
	{	String s = tab + varnaamParam.getParameterText()+equalsString+waarde.getParameterText() + "\n";
		return s;
	}	
}
