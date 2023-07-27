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

/**
 * a PopupPanel for handling input of flow in a root or input of labels
 * for any Vertex; note that the PopupPanel adapts its size to the 
 * TextBox in the PopupPanel   
 */
public class VertexPopup extends PopupPanel
{
	/**
	 * Vertex owning the PopuupPanel
	 */
	Vertex owner;
	/*
	 * TextBox for input in the PopupPanel
	 */
	TextBox textBox;
	/**
	 * minimum width of the TextBox/PopupPanel in pixels
	 */
	private int minimumWidth = 60;
	/**
	 * TextMetrics for determining width of Strings	
	 */
	TextMetrics tm;
	/**
	 * Context2d whose TextMetrics will be used
	 */
	Context2d context2d;
	/**
	 * width of textBox/PopupPanel
	 */
	int breedte;
	/**
	 * height of textBox/PopupPanel
	 */
	int hoogte;
	/**
	 * is the PopupPanel used for the input of a label? 
	 */
	boolean isLabel = false;
	/**
	 * necessary for requesting a global repaint
	 */
	DrawingPanel drawingPanel;

	/**
	 * constructor
	 * @param b required width
	 * @param o the Vertex owning the PopupPanel
	 * @param c2d Context2d for measuring String width
	 * @param label input for label or flow?
	 * @param dp the DrawingPanel painting it all
	 */
	public VertexPopup(int b, Vertex o, Context2d c2d, boolean label, DrawingPanel dp)
	{	
		super(true);
		owner = o;
		context2d = c2d;
		isLabel = label;
		drawingPanel = dp;
	    // width is adapted in vulIn		
		breedte = b;
		hoogte = 25;
		textBox = new TextBox();
		textBox.setSize("" + breedte + "px", "" + hoogte + "px");
		textBox.addKeyDownHandler(new TextBoxKeyDownHandler());
		setWidget(textBox);
		addCloseHandler(new PopupCloseHandler());
	}

	/**
	 * set text in the Textbox and adapt the width of the TextBox (and thus the width
	 * of the PopupPanel) if necessary
	 * @param text text 
	 */
	public void vulIn(String text)
	{	
		setText(text);
			
		tm = context2d.measureText(text);
		breedte = Math.max(minimumWidth, (int) Math.round(tm.getWidth())+40);
		textBox.setSize("" + breedte + "px", "" + hoogte + "px");
	}
	
	/**
	 * get the text from the TextBox
	 * @return the text in the TextBox
	 */
	public String getText()
	{
		return textBox.getText();
	}
	/**
	 * set the text in the TextBox
	 * @param text text to be set
	 */
	public void setText(String text)
	{
		textBox.setText(text);
	}

	/**
	 * process the input in TextBox when user presses the
	 * Enter key; hide the PopUpPanel
	 */
	class TextBoxKeyDownHandler implements KeyDownHandler
	{
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				if (isLabel)
					owner.labelText = getText();
				else
					owner.processInput(getText());
				hide();
				drawingPanel.paint();
			}
		}
	}

	/**
	 * process the input in TextBox upon closing of the PopupPanel
	 * this happens when clicking elsewhere 
	 */
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


