package fi.heksgwt.client.scobjects;

//import java.awt.*;

import java.util.Vector;
import fi.heksgwt.client.vectortek.TekenObjectTek;
import fi.heksgwt.client.vectortek.Point;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

//public class ScContainer extends Container implements ScObject
public class ScContainer implements ScObject
{
	int componenetCnt = 0;
	Vector<Object> components = new Vector();
	
	public double schaal;
	public double relx, rely, relb, relh;
	public boolean resized;

	public int xPos, yPos, breedte, hoogte;
	
	public boolean refresh = true;
	
	public ScContainer() 
	{
//		setLayout(null);
		schaal = 1;
	}

	public ScContainer(int x, int y, int b, int h) 
	{
//		setLayout(null);
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
//System.out.println("ScCont setBounds " + x + " " + y + " " + b + " " + h);		
	}

	public void setLocation(int x, int y)
	{
		xPos = x; yPos = y;
	}
	
	public Point getLocation()
	{
		return new Point(xPos,yPos);
	}
	
	public void setSize(int b, int h)
	{
		breedte = b; hoogte = h;
	}

	public Point getSize()
	{
		return new Point(breedte,hoogte);
	}
	
	
	public int getComponentCount()
	{
		return components.size();
	}

	public Object getComponent(int index)
	{
		return components.elementAt(index);
	}

	public void add(Object o)
	{
//		if (o instanceof TekenObjectTek)
//		{
//			((TekenObjectTek) o).verplaats(xPos,yPos); 
//		}
		
		components.addElement(o);
	}

	public void add(Object o, int insertPos)
	{
		
//		if (o instanceof TekenObjectTek)
//		{
//			((TekenObjectTek) o).verplaats(xPos,yPos); 
//		}
		
		components.insertElementAt(o, insertPos);
	}

	public void remove(Object o)
	{
		components.remove(o);
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

		int n = getComponentCount();
		for (int i = 0; i < n; i++) 
		{
			//Component c = getComponent(i);
			Object c = getComponent(i);
			ScObject scc = null;
			try 
			{
				scc = (ScObject) c;
			} 
			catch (ClassCastException ce) 
			{
			}
			if (scc != null)
				scc.schaal(schaal);

		}
	}
	
	public void paint(Context2d g)
	{
		if (refresh)
		{	
			g.setFillStyle(CssColor.make(255,255,255));
			g.fillRect(xPos, yPos, breedte, hoogte);
		}	

		int n = getComponentCount();
		
//System.out.println("cc = " + n);		
		for (int i = 0; i < n; i++) 
		{
			Object c = getComponent(i);
			if (c instanceof ScObject)
			{
				((ScObject) c).paint(g);
			}
		}
	}
	
	public boolean contains(int x, int y)
	{
		return (x >= xPos) && (x <= (xPos + breedte)) &&
			   (y >= yPos) && (y <= (yPos + hoogte));
	}

	
}
