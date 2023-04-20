package fi.tekenveelvlakgwt.client;

import com.google.gwt.user.client.ui.LayoutPanel;

/**
 * een LayoutPanel beheerd door TekenApplet3D  
 * @author Peter Boon
 */
public class Regelaar extends LayoutPanel
{	
	private TekenApplet3D eigenaar;
	
	public Regelaar(TekenApplet3D ap)
	{	eigenaar = ap;
	}	
}
