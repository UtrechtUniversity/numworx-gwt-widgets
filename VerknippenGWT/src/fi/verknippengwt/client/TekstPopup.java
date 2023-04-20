package fi.verknippengwt.client;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;

public class TekstPopup extends PopupPanel
{
	DrawingPanel2 owner;
	TextBox textBox;
	//int tekstX, tekstY;
	int maxVisibleCharacters = 10;
	int maxCharacters = 30;
	KnipPolygon2 labelPolygon;
	
	public TekstPopup(DrawingPanel2 o, KnipPolygon2 labelPolygon)
	{
		super(true);
		
		this.labelPolygon = labelPolygon;
		
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
//GWT				
				//if ((owner instanceof UitvoerSchuifComponent) && isForLabel)
				//	((UitvoerSchuifComponent) owner).zetLabelTekst();
				//else
				//	owner.zetInvulWaarde();
				String text = textBox.getText();
				boolean error = false;
				int oNum = 0;
				try
				{	oNum = Integer.parseInt(text);
				}
				catch (NumberFormatException nfe)
				{	error = true;
				}
				if (!error)
				{	labelPolygon.oppervlakte = oNum;
//System.out.println("oNum " + oNum);				
				}
				TekstPopup.this.setVisible(false);
				owner.paint();
			}
		}
	}
}	