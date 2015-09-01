package fi.weblogogwt.client;

//import javax.swing.JTextField;

//import java.awt.Font;
//import java.awt.FontMetrics;
//import java.awt.Graphics;
//import java.awt.event.*;

//import fi.algebrapijlengwt.client.UitvoerSchuifComponent;
//import fi.algebrapijlengwt.client.TekstPopup.TextBoxKeyDownHandler;
import fi.weblogogwt.client.expressies.*;
import fi.weblogogwt.client.formuleobjects.*;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;

import com.google.gwt.canvas.dom.client.TextMetrics;

import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.CloseEvent;


/**
 * TextField for editing parameters and other changeable values in CommandComponents.
 * It must be initialized by the owner CC, will appear by call to vulIn(..) and disappear
 * with call back to owner when it loses focus.
 * 
 * Focus is a bit complicated. You will typically start editing when clicking the CC,
 * but when you click the same CC while editing, you will want to finish editing and not edit again.
 * So loss of focus is arranged by requestFocus() when:
 * (1) clicking any component that's not a CC (JavaLogoSchuifVeld.mousePressed, buttons)
 * (2) dragging any CC (CommandComponent.mouseDragged)
 * (3) clicking a CC without parameters (CommandComponent.mouseReleased)
 * (4) clicking a different editable CC (automatically by focus request of that CC's TextField)
 * So a second click on the owner CC will reach that CC without loss of focus and CC
 * may decide what to do (end edit, edit second parameter...)
 * 
 * @author berge020
 */
public class ParameterTextField extends PopupPanel //JTextField implements FocusListener, ActionListener
{
	ParameterEditorListener owner;
	TextBox textBox;
	private int minimumWidth = 60;
	
	//private FontMetrics fm;
	TextMetrics tm;
	
	int breedte, hoogte;
	
	JavaLogoSchuifVeld schuifveld;
	
	//public ParameterTextField(int x, int y, int b, int h, ParameterEditorListener o)
	public ParameterTextField(int b, int h, ParameterEditorListener o, JavaLogoSchuifVeld sv)
	{	
		super(true);
		owner = o;
		schuifveld = sv;
		
// breedte wordt meteen bij vulIn aangepast		
		breedte = b;
		hoogte = 25;

		textBox = new TextBox();

		//setBounds(x,y,b,h);
		textBox.setSize("" + breedte + "px", "" + hoogte + "px");
		
		//addActionListener(this);
		//addFocusListener(this);
		//setVisible(false);
		//setEnabled(false);
		//setFont(JavaLogoWeb.defaultfont);
		//fm = getFontMetrics(JavaLogoWeb.defaultfont);
		
		textBox.addKeyDownHandler(new TextBoxKeyDownHandler());
		setWidget(textBox);
		
		addCloseHandler(new PopupCloseHandler());
		
	}
	
	public void vulIn(String text)
	{	
		setText(text);
		
		tm = schuifveld.jlsvContext2d.measureText(text);
		
		//int breedte = Math.max(minimumWidth, fm.stringWidth(getText())+40);
		breedte = Math.max(minimumWidth, (int) Math.round(tm.getWidth())+40);
		
//System.out.println("vulIn " + text + " b = " + breedte);		
		
/*		
		int space = getParent().getWidth()-getX();
		if ( space < minimumWidth )
		{
			// less than minimumSpace space at separatorrX, move to left a bit
			setBounds(getParent().getWidth()-minimumWidth, getY(), minimumWidth, getHeight());
		} else
		{
			// adjust width to prevent the TextField from going outside its parent
			breedte = Math.min(breedte, space);
			//setSize(breedte,getSize().height);
			setSize(breedte,hoogte);
		}
*/		

		textBox.setSize("" + breedte + "px", "" + hoogte + "px");
		//setVisible(true);
		//setEnabled(true);
		//selectAll();
		//requestFocus();
	}
	
	public String getText()
	{
		return textBox.getText();
	}
	public void setText(String text)
	{
		textBox.setText(text);
	}

	class TextBoxKeyDownHandler implements KeyDownHandler
	{
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
//System.out.println("enter");
				owner.parameterEdited(getText());
			}
		}
	}
	
	class PopupCloseHandler implements CloseHandler<PopupPanel>
	{
		public void onClose(CloseEvent<PopupPanel> e)
		{
			owner.parameterEdited(getText());
		}
	}
	//public void actionPerformed(ActionEvent e)
	//{
		// Just disable. This will trigger a 'focus lost' event that will report the change.
	//	setEnabled(false);
	//}
	
	//public void focusLost(FocusEvent e)
	//{	
	//	owner.parameterEdited(getText());
	//	setVisible(false);
	//	setEnabled(false);
	//}
	
	//public void focusGained(FocusEvent e)
	//{ }
	
}
