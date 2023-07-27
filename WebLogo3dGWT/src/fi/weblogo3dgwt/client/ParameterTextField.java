package fi.weblogo3dgwt.client;


import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;

import com.google.gwt.canvas.dom.client.TextMetrics;

import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.CloseEvent;


/**
 * see class ParameterTextField in WebLogoGWT; note that this is a PopupPanel !
 */
public class ParameterTextField extends PopupPanel 
{
	ParameterEditorListener owner;
	TextBox textBox;
	private int minimumWidth = 60;
	
	TextMetrics tm;
	
	int breedte, hoogte;
	
	JavaLogoSchuifVeld schuifveld;
	
	public ParameterTextField(int b, int h, ParameterEditorListener o, JavaLogoSchuifVeld sv)
	{	
		super(true);
		owner = o;
		schuifveld = sv;
		
// breedte wordt meteen bij vulIn aangepast		
		breedte = b;
		hoogte = 25;

		textBox = new TextBox();

		textBox.setSize("" + breedte + "px", "" + hoogte + "px");
		
		textBox.addKeyDownHandler(new TextBoxKeyDownHandler());
		setWidget(textBox);
		
		addCloseHandler(new PopupCloseHandler());
		
	}
	
	public void vulIn(String text)
	{	
		setText(text);
		
		tm = schuifveld.jlsvContext2d.measureText(text);
		
		breedte = Math.max(minimumWidth, (int) Math.round(tm.getWidth())+40);
		
		textBox.setSize("" + breedte + "px", "" + hoogte + "px");
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
	
}
