package fi.graphtoolgwt.client.ui;

import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;


/**
 * Bug with MGWT Button, use this button instead
 * 
 * @author Danny Hendrix
 * 
 */

public class TouchButton extends TouchPanel
{
	public void setText(String text)
	{
		this.getElement().setInnerText(text);
	}

}
