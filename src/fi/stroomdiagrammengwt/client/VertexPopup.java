package fi.stroomdiagrammengwt.client;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;

import com.google.gwt.canvas.dom.client.TextMetrics;

import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.CloseEvent;

import com.google.gwt.canvas.dom.client.Context2d;


public class VertexPopup extends PopupPanel //JTextField implements FocusListener, ActionListener
{
	Vertex owner;
	TextBox textBox;
	private int minimumWidth = 60;
		
	TextMetrics tm;
		
	int breedte, hoogte;
	
	boolean isLabel = false;
	
	Context2d context2d;
	
	DrawingPanel drawingPanel;
		
	public VertexPopup(int b, int h, Vertex o, Context2d c2d, boolean label, DrawingPanel dp)
	{	
		super(true);
		owner = o;
		context2d = c2d;
		isLabel = label;
		drawingPanel = dp;
			
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
			
		tm = context2d.measureText(text);
			
		breedte = Math.max(minimumWidth, (int) Math.round(tm.getWidth())+40);
			
	//System.out.println("vulIn " + text + " b = " + breedte);		
			
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
	//System.out.println("enter");
				if (isLabel)
					owner.labelText = getText();
				else
					owner.processInput(getText());
				hide();
				drawingPanel.paint();
			}
		}
	}
		
	class PopupCloseHandler implements CloseHandler<PopupPanel>
	{
		public void onClose(CloseEvent<PopupPanel> e)
		{
			if (isLabel)
				owner.labelText = getText();
			else
				owner.processInput(getText());
			drawingPanel.paint();
		}
	}
		
}


