package fi.graphtoolgwt.client;


import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;

import fi.wiskopdr.FormuleParser;


public class TekstPopup extends PopupPanel
{
	//AlgebraSchuifComponent owner;
	boolean isXAsNaam;
	GraphToolGWT interactiePanel;
	TextBox textBox;
	//int tekstX, tekstY;
	int maxVisibleCharacters = 10;
	int maxCharacters = 30;
	
	public TekstPopup(boolean x)
	{
		super(true);
		
		isXAsNaam = x;
		
		textBox = new TextBox();
		
		textBox.setMaxLength(maxCharacters);
		textBox.setVisibleLength(maxVisibleCharacters);
		textBox.addKeyUpHandler(new TextBoxKeyUpHandler());
		setWidget(textBox);
		
	}
	
	public void zetInteractiePanel(GraphToolGWT panel)
	{
		interactiePanel = panel;
	}
	
	public String getText()
	{
		return textBox.getText();
	}
	public void setText(String text)
	{
		textBox.setText(text);
	}
	
	class TextBoxKeyUpHandler implements KeyUpHandler
	//class TextBoxKeyDownHandler implements KeyDownHandler
	{
		public void onKeyUp(KeyUpEvent e)
		{
			if(isXAsNaam)
				interactiePanel.updateXAsNaam(textBox.getText().trim());
			else
				interactiePanel.updateYAsNaam(textBox.getText().trim());
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				textBox.setVisible(false);
				
			}
			//else
				
		}
	}
}	