package fi.heksgwt.client.scobjects;

//import java.awt.*;

import com.google.gwt.canvas.dom.client.Context2d;

import fi.heksgwt.client.vectortek.Point;

//public class ScComponent extends Component implements ScObject
public class ScComponent implements ScObject
{
	public double schaal;
	public double relx, rely, relb, relh;
	public boolean resized;
	
	public int xPos, yPos, breedte, hoogte;

	public ScComponent(int x, int y, int b, int h) 
	{
		schaal = 1;
		relx = x;
		rely = y;
		relb = b;
		relh = h;
		setBounds(x, y, b, h);
	}

	public void setBounds(int x, int y, int b, int h)
	{
		xPos = x; yPos = y; breedte = b; hoogte = h;
	}

	public void setLocation(int x, int y)
	{
		xPos = x; yPos = y;
	}

	public Point getLocation()
	{
		return new Point(xPos,yPos);
	}

	public Point getSize()
	{
		return new Point(breedte,hoogte);
	}

	public void setResized(boolean b) 
	{
		resized = b;
	}

	public void schaal(double s) 
	{
		schaal = s;
		int x = (int) (schaal * relx);
		int y = (int) (schaal * rely);
		int b = (int) (schaal * relb);
		int h = (int) (schaal * relh);
		setBounds(x, y, b, h);
		resized = true;
	}
	public void paint(Context2d g)
	{}
	
	public boolean contains(int x, int y)
	{
		return (x >= xPos) && (x <= (xPos + breedte)) &&
			   (y >= yPos) && (y <= (yPos + hoogte));
	}

}
