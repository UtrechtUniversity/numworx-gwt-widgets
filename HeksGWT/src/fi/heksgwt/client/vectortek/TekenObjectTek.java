package fi.heksgwt.client.vectortek;

//import java.awt.*;
//import java.awt.event.*;
//import java.io.*;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;

public class TekenObjectTek //extends Component 
{
	int xPos; int yPos; int breedte; int hoogte;
	
	public void setSize(int b, int h)
	{
		breedte = b;
		hoogte = h;
	}

	public void setBounds(int x, int y, int b, int h)
	{
		xPos = x;
		yPos = y;
		breedte = b;
		hoogte = h;
	}

	public void setLocation(int x, int y)
	{
		xPos = x;
		yPos = y;
	}

	public Point getLocation()
	{
		return new Point(xPos,yPos);
	}
	
	public void schaal(double factorX, double factorY) 
	{
	}

	public void draai(double h) 
	{
	}

	public void verplaats(int dx, int dy) 
	{
	}

	public void zetVulkleur(CssColor c) 
	{
	}

	public void zetLijnkleur(CssColor c) 
	{
	}
	
	public boolean contains(int x, int y)
	{
		return false;
	}
	
	public void paint(Context2d gr)
	{
	}

}
