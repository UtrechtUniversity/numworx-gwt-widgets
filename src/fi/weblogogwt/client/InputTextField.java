package fi.weblogogwt.client;


import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;

import com.google.gwt.canvas.dom.client.TextMetrics;

import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.CloseEvent;


/**
 * PopupPanel containing a TextBox for editing parameters and other changeable values in CommandComponents.
 * It must be initialized by the owner CC at a double or long click on the parameter to be edited;
 * it will disappear after using the Enter key or losing focus, but will first update the value of the parameter
 * being edited.   
 */
public class InputTextField extends PopupPanel
{
	/**
	 * the CommandComponent implementing the interface ParameterEditorListener owning this PopupPanel
	 */
	ParameterEditorListener owner;
	/**
	 * the TextBox for input
	 */
	public TextBox textBox;
	/**
	 * minimum (starting) width
	 */
	private int minimumWidth = 60;
	/**
	 * instance of TextMetrics for measuring width of Strings
	 */
	TextMetrics tm;
	
	/**
	 * width and height of this PopupPanel
	 */
	int breedte, hoogte;
	/**
	 * instance of JavaLogoSchuifVeld containing the drawing Context2d (necessary for TextMetrics)
	 */
	JavaLogoSchuifVeld schuifveld;
	

	/**
	 * constructor 
	 * @param b width
	 * @param h height
	 * @param o the CommandComponent implementing the interface ParameterEditorListener owning this PopupPanel
	 * @param sv instance of JavaLogoSchuifVeld containing the drawing Context2d
	 */
	public InputTextField(int b, int h, ParameterEditorListener o)
	{	
		super(true);
		owner = o;
		// width is adapted by method vulIn()		
		breedte = b;
		hoogte = 25;
		textBox = new TextBox();
		textBox.setSize("" + breedte + "px", "" + hoogte + "px");
		textBox.addKeyDownHandler(new TextBoxKeyDownHandler());
		setWidget(textBox);
		addCloseHandler(new PopupCloseHandler());
	}
	
	/**
	 * put the String text in textBox, change width if necessary
	 * @param text new content of textBox
	 */
	public void vulIn(String text)
	{	setText(text);
		tm = schuifveld.jlsvContext2d.measureText(text);
		breedte = Math.max(minimumWidth, (int) Math.round(tm.getWidth())+40);
		textBox.setSize("" + breedte + "px", "" + hoogte + "px");
	}
	
	/**
	 * get the text from TextBox
	 * @return content of textBox
	 */
	public String getText()
	{
		return textBox.getText();
	}
	
	/**
	 * set the text in textBox
	 * @param text new content of textBox
	 */
	public void setText(String text)
	{
		textBox.setText(text);
	}

	/**
	 * inner class: process input on Enter key 
	 * @author huub
	 */
	class TextBoxKeyDownHandler implements KeyDownHandler
	{
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				owner.parameterEdited(getText());
			}
		}
	}
	
	/**
	 * process input on closing the PopUpPanel; note: the PopupPanel will be closed
	 * when losing focus, in particular when another PopupPanel is opened 
	 * @author huub
	 */
	class PopupCloseHandler implements CloseHandler<PopupPanel>
	{
		public void onClose(CloseEvent<PopupPanel> e)
		{
			owner.parameterEdited(getText());
		}
	}
	
}
