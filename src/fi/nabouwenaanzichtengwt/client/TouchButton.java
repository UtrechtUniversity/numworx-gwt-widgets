package fi.nabouwenaanzichtengwt.client;

import nl.uu.fi.dwo.interaction.client.touch.TouchPanel;

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
