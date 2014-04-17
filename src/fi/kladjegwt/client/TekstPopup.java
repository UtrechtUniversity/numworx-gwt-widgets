package fi.kladjegwt.client;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.canvas.dom.client.TextMetrics;


public class TekstPopup extends PopupPanel
{
	KladjeGWTVeld owner;
	TextBox textBox;
	int tekstX, tekstY;
	int minVisibleCharacters = 10;
	int maxCharacters = 300;
	int breedte = 50;
	int hoogte = 20;

	public TekstPopup(KladjeGWTVeld o, int eventX, int eventY)
	{
		super(true);
		
		owner = o;
		tekstX = eventX;
		tekstY = eventY;
		
		textBox = new TextBox();
		//textBox.setText("text here");
		textBox.setMaxLength(maxCharacters);
		//textBox.setVisibleLength(maxVisibleCharacters);
		textBox.setWidth("" + breedte + "px");
		textBox.setHeight("" + hoogte + "px");
		textBox.addStyleName(KladjeGWT.kladjeCss.textbox());
		
		textBox.addKeyDownHandler(new TextBoxKeyDownHandler());
		textBox.addKeyPressHandler(new TextBoxKeyPressHandler());
		setWidget(textBox);
		
		
	}
	
	public void setTextColor(String tColorString)
	{
		if (tColorString.equals(KladjeGWTVeld.zwart.toString()))
		{	textBox.setStyleName(KladjeGWT.kladjeCss.textblack());
		}
		else if (tColorString.equals(KladjeGWTVeld.grijs.toString()))
		{	textBox.setStyleName(KladjeGWT.kladjeCss.textgray());
		}
		else if (tColorString.equals(KladjeGWTVeld.rood.toString()))
		{	textBox.setStyleName(KladjeGWT.kladjeCss.textred());
		}
		else if (tColorString.equals(KladjeGWTVeld.oranje.toString()))
		{	textBox.setStyleName(KladjeGWT.kladjeCss.textorange());
		}
		else if (tColorString.equals(KladjeGWTVeld.groen.toString()))
		{	textBox.setStyleName(KladjeGWT.kladjeCss.textgreen());
		}
		else if (tColorString.equals(KladjeGWTVeld.cyaan.toString()))
		{	textBox.setStyleName(KladjeGWT.kladjeCss.textcyaan());
		}
		else if (tColorString.equals(KladjeGWTVeld.blauw.toString()))
		{	textBox.setStyleName(KladjeGWT.kladjeCss.textblue());
		}
		else if (tColorString.equals(KladjeGWTVeld.magenta.toString()))
		{	textBox.setStyleName(KladjeGWT.kladjeCss.textmagenta());
		}
	}
	
	public String getText()
	{
		return textBox.getText();
	}
	public void setText(String text)
	{
		textBox.setText(text);
		String fontString = "16px bold, sans-serif";
		owner.gIm.setFont(fontString);
		TextMetrics tm = owner.gIm.measureText(text);
		int tekstBreedte = Math.max(breedte - 10, (int) Math.round(tm.getWidth())) + 10;

		textBox.setWidth("" + tekstBreedte + "px");

	}
	
	class TextBoxKeyPressHandler implements KeyPressHandler
	{
		public void onKeyPress(KeyPressEvent e)
		{
			String tekst = textBox.getText();
			String fontString = "16px bold, sans-serif";
			owner.gIm.setFont(fontString);
			TextMetrics tm = owner.gIm.measureText(tekst);
			int tekstBreedte = Math.max(breedte - 10, (int) Math.round(tm.getWidth())) + 10;

			textBox.setWidth("" + tekstBreedte + "px");
			
		
			
		}
	}
	
	class TextBoxKeyDownHandler implements KeyDownHandler
	{
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				owner.hideTekstVeld(true);
			}
		}
	}
}
