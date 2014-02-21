package fi.algebraexprgwt.client;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;

public class TekstPopup extends PopupPanel
{
	//AlgebraSchuifComponent owner;
	UitvoerSchuifComponent owner;
	TextBox textBox;
	//int tekstX, tekstY;
	int maxVisibleCharacters = 10;
	int maxCharacters = 30;
	
	public TekstPopup(UitvoerSchuifComponent o)
	{
		super(true);
		
		owner = o;
		//tekstX = eventX;
		//tekstY = eventY;
		
		textBox = new TextBox();
		
		//textBox.setText("text here");
		textBox.setMaxLength(maxCharacters);
		textBox.setVisibleLength(maxVisibleCharacters);
		textBox.addKeyDownHandler(new TextBoxKeyDownHandler());
		setWidget(textBox);
		
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
				
				owner.zetInvulWaarde();
			}
		}
	}
}	