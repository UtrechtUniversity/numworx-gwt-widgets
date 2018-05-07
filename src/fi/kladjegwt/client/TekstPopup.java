package fi.kladjegwt.client;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * een PopupPanel met een TextBox erin om tekst in te voeren;
 * de breedte van het PopupPanel past zich aan aan de breedte vasn de TextBox 
 * de TekstPopup verschijnt op het werkveld bij mouseDown/touchStart
 * in modus teksttekenen (zie methode mouseDownTouchStartAction 
 * in klasse KladjeGWTVeld)  
 * @author huub
 */

public class TekstPopup extends PopupPanel
{
	/**
	 * eigenaar van deze TekstPopup
	 */
	KladjeGWTVeld owner;
	/**
	 * de Textbox voor invoer
	 */
	TextBox textBox;
	/**
	 * x- en y-coordinaat in het werkveld waar de ingevoerde tekst moet
	 * verschijnen, zie methode hideTekstVeld in klasse KladjeGWTVeld)   
	 */
	int tekstX, tekstY;
	/**
	 * minimum aantal zichtbare characteres in de TekstBox
	 */
	int minVisibleCharacters = 10;
	/**
	 * maximale lengte van de TextBox in characters 
	 */
	int maxCharacters = 300;
	/**
	 * breedte van de TextBox in pixels
	 */
	int breedte = 50;
	/**
	 * hoogte van de TextBox in pixels
	 */
	int hoogte = 20;

	/**
	 * constructor
	 * @param o eigenaar
	 * @param eventX x-coordinaat in werkveld waar tekst moet verschijnen
	 * @param eventY y-coordinaat in werkveld waar tekst moet verschijnen
	 */
	public TekstPopup(KladjeGWTVeld o, int eventX, int eventY)
	{
		super(true);
		
		owner = o;
		tekstX = eventX;
		tekstY = eventY;
		
		textBox = new TextBox();
		textBox.setMaxLength(maxCharacters);

		textBox.setWidth("" + breedte + "px");
		textBox.setHeight("" + hoogte + "px");
		textBox.addStyleName(KladjeGWT.kladjeCss.textbox());
		
		textBox.addKeyDownHandler(new TextBoxKeyDownHandler());
		textBox.addKeyPressHandler(new TextBoxKeyPressHandler());
		setWidget(textBox);
		
		
	}
	
	/**
	 * zet de kleur van de tekst in de TextBox: N.B. dit kan alleen via een
	 * style (i.t.t. Java)
	 * @param tColorString kleur van de tekst als String
	 */
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

	/**
	 * get de tekst in de TextBox
	 * @return de tekst in de TextBox 
	 */
	public String getText()
	{
		return textBox.getText();
	}
	
	/**
	 * zet de tekst in de TextBox, pas de breedte van de TextBox aan
	 * (dit verandert ook de breedte van de Popup)
	 * @param text nieuwe tekst in de TekstBox
	 */
	public void setText(String text)
	{
		textBox.setText(text);
		String fontString = "16px bold, sans-serif";
		//KladjeGWTVeld.gIm.setFont(fontString);
		//TextMetrics tm = KladjeGWTVeld.gIm.measureText(text);
		//int tekstBreedte = Math.max(breedte - 10, (int) Math.round(tm.getWidth())) + 10;

		//textBox.setWidth("" + tekstBreedte + "px");

	}
	
	/**
	 * inner class die de breedte van de Textbox (en dus van dit PopupPanel)
	 * aanpast tijdens tekstinvoer  
	 * @author huub
	 */
	class TextBoxKeyPressHandler implements KeyPressHandler
	{
		public void onKeyPress(KeyPressEvent e)
		{
			String tekst = textBox.getText();
			String fontString = "16px bold, sans-serif";
			//KladjeGWTVeld.gIm.setFont(fontString);
			//TextMetrics tm = KladjeGWTVeld.gIm.measureText(tekst);
			//int tekstBreedte = Math.max(breedte - 10, (int) Math.round(tm.getWidth())) + 10;

			//textBox.setWidth("" + tekstBreedte + "px");
			
		
			
		}
	}
	
	/**
	 * inner class die de tekst uit de TextBox verwerkt en vervolgens 
	 * dit PopupPanel verbergt 
	 * @author huub
	 */
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
