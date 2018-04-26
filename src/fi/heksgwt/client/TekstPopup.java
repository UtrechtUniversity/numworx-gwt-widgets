package fi.heksgwt.client;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

import fi.heksgwt.client.scobjects.ScContainer;


public class TekstPopup extends PopupPanel
{
	GetalComponent owner;
	TextBox textBox;
	//int tekstX, tekstY;
	int maxVisibleCharacters = 10;
	int maxCharacters = 30;
	boolean isForLabel = false;
	
	ScContainer bigOwner;
	
	public TekstPopup(GetalComponent o, ScContainer bigO)
	{
		super(true);
		
		owner = o;
		bigOwner = bigO;
		//tekstX = eventX;
		//tekstY = eventY;
		
		textBox = new TextBox();
		
		//textBox.setText("text here");
		textBox.setMaxLength(maxCharacters);
		textBox.setVisibleLength(maxVisibleCharacters);
		textBox.addKeyDownHandler(new TextBoxKeyDownHandler());
		setWidget(textBox);
		
		addCloseHandler(new PopupCloseHandler());
	}
	
	
	public String getText()
	{
		return textBox.getText();
	}
	public void setText(String text)
	{
		textBox.setText(text);
	}
	
	public void exitAction()
	{
		
System.out.println("tekstPopup exitAction");

		if (getText().equals(""))
			return;

		owner.zetWaarde(getText());
		if (bigOwner instanceof Pagina21Panel)
		{	((Pagina21Panel) bigOwner).opnieuwAction();
		}
		else if (bigOwner instanceof Pagina22Panel)
		{	((Pagina22Panel) bigOwner).opnieuwAction();
		}
		else if (bigOwner instanceof Pagina23Panel)
		{	((Pagina23Panel) bigOwner).instellingAction(owner);
		}
	
	}
	
	class TextBoxKeyDownHandler implements KeyDownHandler
	{
		public void onKeyDown(KeyDownEvent e)
		{
			
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
//System.out.println("enter");
				exitAction();
				setVisible(false);
				
			}
		}
	}
	
	class PopupCloseHandler implements CloseHandler<PopupPanel>
	{
		public void onClose(CloseEvent<PopupPanel> e)
		{
			exitAction();
			
		}
	}

}	