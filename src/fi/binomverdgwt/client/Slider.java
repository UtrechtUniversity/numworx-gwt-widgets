package fi.binomverdgwt.client;

//import java.awt.*;
//import java.awt.event.*;

//import javax.swing.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Touch;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;


public class Slider //extends JComponent implements MouseListener, MouseMotionListener 
{
	//private Image im;
	//private Graphics gIm;
	
	Context2d sliderContext2d;
	Canvas sliderCanvas;
	
	int xPos, yPos, breedte, hoogte;
	Rectangle sliderRectangle;
	Rectangle raakRectangle;
	
	private boolean resize;
//	protected ActionListener actionListener = null;

	private int lengte;
	private int stand;
	private int minimum = -2;
	private int maximum;
	private int muisStartX, muisStartY;
//	private Polygon schuifKnop;
	private boolean raak;

	private boolean editable;

	private boolean showLine = true;

	private CssColor knopColor = CssColor.make(255,0,0);

	private boolean enabled = true;
	public static final int HOOGTE = 13;
	
	BinomVerdPanel owner;
	String param = "";
	
	boolean mouseDown;
	
	public Slider(BinomVerdPanel o, int aantalPix, int beginst, int x, int y, Context2d c2d, String p) 
	{
		owner = o;
		param = p;
		
		lengte = aantalPix;
		maximum = lengte;
		this.editable = true;
		stand = beginst;
		
		//addMouseListener(this);
		//addMouseMotionListener(this);
		//setSize(lengte + 10, Slider.HOOGTE);
		
		xPos = x;
		yPos = y;
		breedte = lengte + 10;
		hoogte = 13;
		sliderRectangle = new Rectangle(xPos, yPos, breedte, hoogte);
		
		if (c2d == null)
		{
			
			
			sliderCanvas = Canvas.createIfSupported();
			
			sliderCanvas.setWidth(breedte + "px");
			sliderCanvas.setHeight(hoogte + "px");
			sliderCanvas.setCoordinateSpaceWidth(breedte);
			sliderCanvas.setCoordinateSpaceHeight(hoogte);

			sliderContext2d = sliderCanvas.getContext2d();
			
			MouseHandler mouseHandler = new MouseHandler();
			sliderCanvas.addMouseDownHandler(mouseHandler);
			sliderCanvas.addMouseMoveHandler(mouseHandler);
			sliderCanvas.addMouseUpHandler(mouseHandler);
			
			TouchHandler touchHandler = new TouchHandler();
			sliderCanvas.addTouchStartHandler(touchHandler);
			sliderCanvas.addTouchMoveHandler(touchHandler);
			sliderCanvas.addTouchEndHandler(touchHandler);

		}
		else
		{	
			sliderContext2d = c2d;
			
		}	
	}

	public void setLocation(int x, int y)
	{
		xPos = x;
		yPos = y;
	}
	
	public void zetLengte(int aantalPix) 
	{
		lengte = aantalPix;
		maximum = lengte;
		//setSize(lengte + 10, Slider.HOOGTE);
		breedte = lengte + 10;
		sliderRectangle = new Rectangle(xPos, yPos, breedte, hoogte);
		resize = true;
		paint();
	}

	public void setEditable(boolean b) 
	{
		this.editable = b;
	}

	public void zetShowLine(boolean b) 
	{
		showLine = b;
	}

	public void zetKnopColor(CssColor c) 
	{
		knopColor = c;
	}

	public void zetEnabled(boolean b) 
	{
		enabled = b;
	}

	public void paint()
	{
		tekenSlider(sliderContext2d);
	}
	
/*	
	public void paintComponent(Graphics g) {
		if (im == null || resize) {
			im = createImage(getSize().width, getSize().height);
			gIm = im.getGraphics();
		}
		gIm.setColor(getBackground());
		// gIm.fillRect(0, 0, getSize().width, getSize().height);
		// tekenSlider(gIm);
		// g.drawImage(im, 0, 0, null);
		tekenSlider(g);
	}
*/
/*	
	public void update(Graphics g) {
		paint(g);
	}
*/

	//public void tekenSlider(Graphics g)
	public void tekenSlider(Context2d g)
	{
		
		if (enabled && !param.equals("grens"))
		{	
			g.setFillStyle(CssColor.make(220,239,247));
			g.fillRect(xPos, yPos, breedte, hoogte);
		}

		if (showLine && enabled)
		{	//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			//g.drawLine(5, 5, lengte + 5, 5);
			g.beginPath();
			g.moveTo(xPos + 5, yPos + 5);
			g.lineTo(xPos + lengte + 5, yPos + 5);
			g.stroke();
		
		}
		
		if (enabled) 
		{
			//g.setColor(knopColor);
			//g.fillOval(5 + stand - 3, 2, 6, 6);
			g.setFillStyle(knopColor);
			g.beginPath();
            g.arc(xPos + 5 + stand, yPos + 5, 3, 0, 2 * Math.PI);
       	 	g.fill();
			
			
			//g.setColor(Color.black);
			//g.drawOval(5 + stand - 3, 2, 6, 6);
			g.setStrokeStyle(CssColor.make(0,0,0));
			g.beginPath();
            g.arc(xPos + 5 + stand, yPos + 5, 3, 0, 2 * Math.PI);
       	 	g.stroke();
       	 	
//raakRectangle = new Rectangle(xPos + stand - 3, yPos, 16, 13);
//g.rect(raakRectangle.x, raakRectangle.y, raakRectangle.width, raakRectangle.height);
//g.stroke();
		}

	}

//	public void addActionListener(ActionListener l) {
//		actionListener = AWTEventMulticaster.add(actionListener, l);
//	}

//	public void removeActionListener(ActionListener l) {
//		actionListener = AWTEventMulticaster.remove(actionListener, l);
//	}

	public int geefStand() 
	{
		return stand;
	}

	public void zetStand(int std) 
	{
		if (std > maximum)
			stand = maximum;
		else if (std < minimum)
			stand = minimum;
		else
			stand = std;
		paint();
	}

	public void setMaximum(int max) 
	{
		maximum = max;
	}

	public void setMinimum(int min) 
	{
		minimum = min - 2;
	}

	public int getMaximum() 
	{
		return maximum;
	}

	public int getMinimum() 
	{
		return this.minimum;
	}
	
	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		if (this.editable) 
		{
			raak = enabled && (new Rectangle(xPos + stand - 3, yPos, 16, 13)).contains(eventX, eventY);
			muisStartX = eventX;
			muisStartY = eventY;
			//if (raak && actionListener != null) 
			//{	actionListener.actionPerformed(new ActionEvent(this, 0, "start"));
			//}
		}
	}

	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{
		if (this.editable) 
		{
			if (!raak && enabled && (new Rectangle(xPos + stand - 3, yPos, 16, 13).contains(eventX, eventY))) 
			{
				raak = true;
				muisStartX = eventX;
				//if (raak && actionListener != null) 
				//{
				//	actionListener.actionPerformed(new ActionEvent(this, 0,"start"));
				//}
			}
			if (raak) 
			{
				int x = eventX;
				int dx = x - muisStartX;
				stand = stand + dx;
				if (stand > maximum) 
				{
					stand = maximum;
				} 
				else if (stand < minimum) 
				{
					stand = minimum;
				}
				if (x < 5 || x > lengte + 20) 
				{
					raak = false;
				}
				paint();
		
				
				if (param.equals("n"))
					owner.processNSlider(false);
				else if (param.equals("p"))
					owner.processPSlider();
				else if (param.equals("M"))
					owner.processMSlider();
				else if (param.equals("populatie"))
					owner.processPopulatieSlider();
				else if (param.equals("grens"))
					owner.processGrensSlider(false);

				
				
				//if (actionListener != null) 
				//{
				//	actionListener.actionPerformed(new ActionEvent(this, 0,
				//			"verschoven"));
				//}
				muisStartX = x;
			}
		}
	}

	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction()
	{
		if (this.editable) 
		{
			
			if (param.equals("n"))
				owner.processNSlider(true);
			else if (param.equals("grens"))
				owner.processGrensSlider(true);
			
			//if (actionListener != null) 
			//{
			//	actionListener.actionPerformed(new ActionEvent(this, 0, "stop"));
			//}
		}
	}

	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		
		//public void mousePressed(MouseEvent e)
		public void onMouseDown(MouseDownEvent e)
		{
			//e.preventDefault();
			
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDown = true;
			
			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		//public void mouseDragged(MouseEvent e)
		public void onMouseMove(MouseMoveEvent e)	
		{
			//e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
//System.out.println("mouse move veld");			
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();
			
//System.out.println("sp = " + shiftPressed);

			mouseMoveTouchMoveAction(eventX, eventY);
			
			
			
		} // onMouseMove
		
		//public void mouseReleased(MouseEvent e)
		public void onMouseUp(MouseUpEvent e)	
		{
			//e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			mouseUpTouchEndAction();

		}

	} //MLMML


	// tablet, dwo 
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				
				int eventX = touch.getPageX() - owner.getAbsoluteLeft();
				int eventY = touch.getPageY() - owner.getAbsoluteTop();				
				
				mouseDownTouchStartAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				int eventX = touch.getPageX() - owner.getAbsoluteLeft();
				int eventY = touch.getPageY() - owner.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			mouseUpTouchEndAction();
		}

	}

//	public void mouseClicked(MouseEvent e) 
//	{}

//	public void mouseExited(MouseEvent e) 
//	{}

//	public void mouseEntered(MouseEvent e) 
//	{}

//	public void mouseMoved(MouseEvent e) 
//	{}
}
