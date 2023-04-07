package fi.doorziengwt.client;

//import java.awt.*;
//import java.awt.event.*;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;

import fi.doorziengwt.client.DrawingPanel2.MouseHandler;
import fi.doorziengwt.client.DrawingPanel2.TouchHandler;

//class representing a horizontal slider
public class Slider2 //extends Component 
{  
	Canvas sliderCanvas;
	Context2d sliderContext2d;
	
	// sizes, hard coded
    static final int vertSize = 20;
    static final int horSize = 120;
    static final int buttonWidth = 7;
    static final int offSet = 10;
    // the parameter being adjusted
    //private Parameter sliderValue;
    //double sliderValue;
    double minValue, maxValue;
    // slider position, a value from 0 to 1
    private double currentPosition;
    // slider colors
	private CssColor enabledColor = CssColor.make(255,0,0);
	private CssColor disabledColor = CssColor.make(190,190,190);
	private CssColor sliderColor = enabledColor;
	// flag for being enabled
	// NOT private, must be accessable from inner class MLMML
	boolean enabled = true;
	// the owner
	DrawingPanel2 owner;
	DrawingShell owner2;
    // constructor
	public Slider2(DrawingPanel2 o, double min, double max)
	{	owner = o;
		initSlider(min, max);
	}
	
	public Slider2(DrawingShell o, double min, double max)
	{	owner2 = o;
		initSlider(min, max);
	}
	
	public void initSlider(double min, double max)
	{
		sliderCanvas = Canvas.createIfSupported();
		sliderCanvas.setWidth(horSize + "px");
		sliderCanvas.setHeight(vertSize + "px");
		sliderCanvas.setCoordinateSpaceWidth(horSize);
		sliderCanvas.setCoordinateSpaceHeight(vertSize);

		sliderCanvas.addStyleName(DoorzienGWT.doorzienGWTCss.canvas());
		
		sliderContext2d = sliderCanvas.getContext2d();
		
    	MouseHandler mouseHandler = new MouseHandler();
    	sliderCanvas.addMouseDownHandler(mouseHandler);
    	sliderCanvas.addMouseMoveHandler(mouseHandler);
    	sliderCanvas.addMouseUpHandler(mouseHandler);

      	TouchHandler touchHandler = new TouchHandler();
      	sliderCanvas.addTouchStartHandler(touchHandler);
      	sliderCanvas.addTouchMoveHandler(touchHandler);
      	sliderCanvas.addTouchEndHandler(touchHandler);

	
//	    setSize(horSize, vertSize);
	    minValue = min;
	    maxValue = max;
	    
//		MLMML listener = new MLMML();
//		addMouseListener(listener);
//		addMouseMotionListener(listener);
		
		//setPosition(owner.sliderValue);
	}
	
	public void setMinMax(double min, double max)
	{
		minValue = min;
	    maxValue = max;
	}
	
	public void setVisible(boolean b)
	{
		sliderCanvas.setVisible(b);
	}

// nog tekst links en rechts?
	
	public void paint()
	{
		paintSlider(sliderContext2d);
	}

	
    // paint method
	public void paintSlider(Context2d g)
	{	
		g.setFillStyle(CssColor.make(255,255,255));
		g.fillRect(0, 0, horSize, vertSize);
		
		//g.setColor(Color.lightGray);
		g.setStrokeStyle(CssColor.make(190,190,190));
	    // outline
		//g.drawRect(0, 0, horSize - 1, vertSize - 1);
		g.strokeRect(0, 0, horSize, vertSize);
	    
	    // draw rectangle
        //g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		
//		g.drawRect(offSet, vertSize / 4,
//		           getSize().width - 2 * offSet - 1, vertSize / 2);
		g.strokeRect(offSet, vertSize / 4,
		             horSize - 2 * offSet - 1, vertSize / 2);
		           
        // draw button
		//g.setColor(sliderColor);
		g.setFillStyle(sliderColor);
		
		// NOTE: slider extends from
		// offSet to getSize().width - offSet - 1
		// thus has length getSize().width - 2 * offSet - 1
		// this corresponds to currentPosition 0.0 through 1.0
//		g.fillOval((int) Math.round(offSet - (buttonWidth / 2) +
//		                currentPosition * (getSize().width - 2 * offSet - 1)
//		                 ),
//		           0, buttonWidth, getSize().height);
		
		
		g.beginPath();
		g.arc((int) Math.round(offSet + currentPosition * (horSize - 2 * offSet - 1)),
					vertSize / 2 - 1, vertSize / 2 - 1, 0, 2 * Math.PI);
		g.fill();
		
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.beginPath();
		g.arc((int) Math.round(offSet + currentPosition * (horSize - 2 * offSet - 1)),
					vertSize / 2 - 1, vertSize / 2 - 1, 0, 2 * Math.PI);
		g.stroke();
		           
	}
	

	
	// change parameter value, prevent button from leaving
	// rectangle
    public void setValue(int mousePosition)
    {   
        currentPosition = 
        	((double) (mousePosition - offSet)) / (horSize - 2 * offSet);
	            
	    if (currentPosition > 1.0d)
	        currentPosition = 1.0d;
	    else if (currentPosition < 0.0d)
	        currentPosition = 0.0d;
	    if (owner2 == null)
	    	owner.processSlider(minValue + currentPosition * (maxValue - minValue));
	    else
	    	owner2.processSlider(minValue + currentPosition * (maxValue - minValue));
        paint();
    }
    // find button position
	public void setPosition(double val)
	{	if (owner2 == null)
			currentPosition = (owner.sliderValue - minValue) / (maxValue - minValue);
		else
			currentPosition = (owner2.sliderValue - minValue) / (maxValue - minValue);
		paint();
	}
	
	
	public void setEnabled(boolean e)
	{	if (e)
		{	sliderColor = enabledColor;
		    enabled = true;
		}
		else
		{	sliderColor = disabledColor;
		    enabled = false;
		}
		paint();
	}

	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		if (enabled)
        {   //requestFocus();
            int xPos = eventX;
            setValue(xPos);
        }
	}
	
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{
		if (enabled)
        {   int xPos = eventX;
            int yPos = eventY;
            // limit dragg events to slider
            //if (contains(xPos, yPos))
                setValue(xPos);
        }
	}
	
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		boolean mouseDown = false;
		
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
		
		public void onMouseMove(MouseMoveEvent e)	
		{
			//e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseMoveTouchMoveAction(eventX, eventY);
			
			
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{
			//e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			//mouseUpTouchEndAction();

		}

	} //MouseHandler

	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				int eventX = touch.getPageX() - sliderCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - sliderCanvas.getAbsoluteTop();
				
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
				
			    boolean shiftPressed = false;
			    int eventX = touch.getPageX() - sliderCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - sliderCanvas.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			//mouseUpTouchEndAction();
		}

	}
	
	
/*	
	// inner class for mouse events
	class MLMML extends MouseAdapter
	            implements MouseMotionListener
    {   public void mousePressed(MouseEvent e)
        {   if (enabled)
            {   requestFocus();
                int xPos = e.getX();
                setValue(xPos);
            }
        }
        public void mouseDragged(MouseEvent e)
        {   if (enabled)
            {   int xPos = e.getX();
                int yPos = e.getY();
                // limit dragg events to slider
                if (contains(xPos, yPos))
                    setValue(xPos);
            }
        }
        public void mouseMoved(MouseEvent e) {}
    }
*/
	
}
