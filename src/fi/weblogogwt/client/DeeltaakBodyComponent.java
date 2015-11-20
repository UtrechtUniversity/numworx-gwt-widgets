package fi.weblogogwt.client;

//import java.awt.Color;
//import java.awt.FontMetrics;
//import java.awt.Graphics;
//import java.awt.Rectangle;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;
import fi.weblogogwt.client.formuleobjects.StringUtils;
import fi.weblogogwt.client.parameters.Identifier;
import fi.weblogogwt.client.parameters.IdentifierList;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;


public class DeeltaakBodyComponent extends ProgrammaComponent implements ParameterEditorListener
{
	private ParameterTextField naamEditor;
	private int separatorX = 0;
	//private FontMetrics fm;
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
		
//GWT4		
		//naamEditor = new ParameterTextField(10, 4, 80, 17, this);
		//fm = getFontMetrics(JavaLogoWeb.boldfont);
		//separatorX = 10+fm.stringWidth(pn+"(");
		//add(naamEditor);
		
		schuifveld.jlsvContext2d.setFont(WebLogoGWT.fontString);
		
		tm = schuifveld.jlsvContext2d.measureText(commandName+"(");
		int width = (int) Math.round(tm.getWidth());
				
		separatorX = 10+width;
		
		isHeightFixed = false; 			// allow height changes
	}
	
	/**
	 * Check validity of entire deeltaak header.
	 * This includes testing if name is an identifier, all parameters are identifiers
	 * and number of parameters <= maximum.
	 * 
	 * @return true if all checks are ok.
	 */
	boolean isHeaderValid()
	{
		return deeltaaknaamParam.isCorrect() && pmParam.isCorrect();
	}

	@Override
	public void parameterEdited(String text)
	{
		if ( isEditingName )
		{
			deeltaaknaamParam.setParameter(text);
			isEditingName = false;
			tm = schuifveld.jlsvContext2d.measureText(deeltaaknaamParam.getParameterText()+"(");
			int tWidth = (int) Math.round(tm.getWidth());
			//separatorX = 10+fm.stringWidth(deeltaaknaamParam.getParameterText()+"(");
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
//GWT3			
			//naamEditor.setLocation(10, 4);
			//naamEditor.vulIn(deeltaaknaamParam.getParameterText());
			//separatorX = naamEditor.getX()+naamEditor.getWidth()+1;
			isEditingName = true;
		}
		else
		{
//GWT2			
			//naamEditor.setLocation(separatorX+2, 4);
			//naamEditor.vulIn(pmParam.getParameterText());
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
		naamEditor = schuifveld.paramEditor; //new ParameterTextField(breedte, hoogte, this, schuifveld);
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
	 * @see fi.javalogoweb.ParameterEditorListener#parameterComponentClicked(int, int)
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
			
			//naamEditor.setVisible(false);
			//naamEditor.setEnabled(false);
		}
		schuifveld.paint();
	}
	
	/**
	 * Set name and parameter directly (ProgrammaImporter)
	 * @param s
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
	@Override
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
	 * 
	 * @return	parametername or empty string
	 */
	String getParameterName(int n)
	{
		return pmParam.getIdentifier(n);
	}
	
	/**
	 * DeeltaakBodyComponents retains its width when being dragged
	 * 
	 * @see fi.javalogoweb.CommandComponent#getDragWidth()
	 */
	@Override
	int getDragWidth()
	{
		return getWidth();
	}
	
	/**
	 * Deeltaak will not be traced ( no carets while arranging the DTB's in the ProgrammaPanel)
	 * 
	 * @see fi.javalogoweb.CommandComponent#isTraceable()
	 */
	@Override
	boolean isTraceable()
	{
		return false;
	}
	
	@Override
	void containerHeightChanged(int h)
	{	
		
//System.out.println("containerHeightChanged " + commandName);

		if ( !isHeightFixed && isOpen )
		{	int newh;
			newh = Math.min(schuifveld.getHeight()-20, Math.max(pcminoh, commandBlock.getContentHeight()+headerHeight+20));
			setSize(getWidth(), newh);
			setLocation(getX(), Math.min(getY(), Math.max(0,JavaLogoSchuifVeld.pph-newh)));
		}
		
		int heightSurplus = commandBlock.getHeight()-(getHeight()-headerHeight);
		if ( heightSurplus > 0 )
		{	
			int newY = yPos + headerHeight - heightSurplus;//Math.max(-heightSurplus, Math.min(0, commandBlock.getY()+8));
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
		//Rectangle r = new Rectangle(JavaLogoSchuifVeld.ppx, JavaLogoSchuifVeld.ppy, 
		//		                    schuifveld.getWidth(), schuifveld.getHeight());
		Rectangle r = new Rectangle(JavaLogoSchuifVeld.ppx, JavaLogoSchuifVeld.ppy, 
                schuifveld.breedte, schuifveld.hoogte);

		if ( r.contains(x, y) )
		{
			// Note: the actual mouse position, given by the parameters, is irrelevant, because we want to position
			// the DBC exactly where it is now. So, just translate the current (absolute-JLSV) coordinates 
			// to the ProgrammaPanel and apply min/max rules to keep it inside.
			newX = getX(); //-JavaLogoSchuifVeld.ppx;
			newX = Math.max(80, Math.min(newX, schuifveld.getWidth()-getWidth()));
			newY = getY(); //-JavaLogoSchuifVeld.ppy;
			newY = Math.max(0, Math.min(newY, schuifveld.getHeight()-getHeight()));
		}
		setLocation(newX, newY);
//GWT? 		
		schuifveld.addToProgrammaPanel(this);
		schuifveld.paint();
	}
	
	
	@Override
	//protected void paintCommand(Graphics g)
	protected void paintCommand(Context2d g)
	{
		//g.setFont(JavaLogoWeb.boldfont);
		g.setFont(WebLogoGWT.boldFontString);
		//g.setColor(Color.BLACK);
		g.setFillStyle(CssColor.make(0,0,0));
		
		if ( isEditingName )
		{
			//g.drawString("("+pmParam.getParameterText()+")", separatorX, 18);
			g.fillText("(" + pmParam.getParameterText() + ")", xPos+ separatorX, yPos+18);
		}
		else if ( isEditingParamName )
		{
			//g.drawString(deeltaaknaamParam.getParameterText()+"(", 10, 18);
			g.fillText(deeltaaknaamParam.getParameterText() + "(", xPos+10, yPos+18);
			
			//g.drawString(" )", separatorX+naamEditor.getWidth()+3, 18);
			g.fillText(" )", xPos+separatorX+60+3, yPos+18);
		} 
		else
		{	// paint parts of the declaration in RED if they are incorrect;
			int xpos = 10;
			if ( !deeltaaknaamParam.isCorrect() ) 
			{	//g.setColor(Color.RED);
				g.setFillStyle(CssColor.make(255,0,0));
			}
			//g.drawString(deeltaaknaamParam.getParameterText(), 10, 18);
			g.fillText(deeltaaknaamParam.getParameterText(), xPos+10, yPos+18);
			
			tm = schuifveld.jlsvContext2d.measureText(deeltaaknaamParam.getParameterText());
			int tWidth = (int) Math.round(tm.getWidth());
			//xpos = xpos+fm.stringWidth(deeltaaknaamParam.getParameterText());
			xpos = xpos+tWidth;
			//g.setColor(Color.BLACK);
			g.setFillStyle(CssColor.make(0,0,0));
			//g.drawString("( ", xpos, 18);
			g.fillText("( ", xPos+xpos, yPos+18);
			
			tm = schuifveld.jlsvContext2d.measureText("(");
			tWidth = (int) Math.round(tm.getWidth());
			//xpos = xpos + fm.stringWidth("(");
			xpos = xpos + tWidth;
			if ( !pmParam.isCorrect() )
			{	//g.setColor(Color.RED);
				g.setFillStyle(CssColor.make(255,0,0));
			}
			//g.drawString(pmParam.getParameterText(), xpos, 18);
			g.fillText(pmParam.getParameterText(), xPos+xpos, yPos+18);
			
			tm = schuifveld.jlsvContext2d.measureText(pmParam.getParameterText());
			tWidth = (int) Math.round(tm.getWidth());
			//xpos = xpos + fm.stringWidth(pmParam.getParameterText());
			xpos = xpos + tWidth;
			//g.setColor(Color.BLACK);
			g.setFillStyle(CssColor.make(0,0,0));
			//g.drawString(")", xpos, 18);
			g.fillText(")", xPos+xpos, yPos+18);
		}
	}
	
	@Override
	public boolean execute(TraceBeheerder trb, Uitvoerblad ub, VarSet varSet)
	{
		return executeContent(trb, ub, varSet);
	}

	public String getCode(String tab)
	{	
		String s = "\nDeeltaak: " + getProgramName() + "( " + pmParam.getParameterText() + " )" + "\n";
		return s+ super.getCode(tab)+"\n";
	}

}
