package fi.weblogo3dgwt.client.logotekenap3d;


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

/**
 * class handling mouse and touch events; an instance of this class should be added 
 * as MouseDown/MouseMove/MouseUp/TouchStart/TouchMove/TouchEnd handler to the 
 * Canvas on which events take place; the events details are redirected to the class
 * owning the instance of MuisBeheerder  
 */

public class MuisBeheerder implements MouseDownHandler, MouseUpHandler, MouseMoveHandler,
									  TouchStartHandler, TouchMoveHandler, TouchEndHandler
{
	/**
	 * x-coordinate of last mouseDown/touchStart event
	 */
	private int eerstex;
	/**
	 * y-coordinate of last mouseDown/touchStart event
	 */
	private int eerstey;
	/**
	 * x-coordinate of last mouseDown/touchStart/mouseMove/touchMove event
	 */
	private int laatstex;
	/**
	 * y-coordinate of last mouseDown/touchStart/mouseMove/touchMove event
	 */
	private int laatstey;
	/**
	 * last mouseMove/touchMove x-translation
	 */
	private int dx;
	/**
	 * last mouseMove/touchMove y-translation
	 */
	private int dy;
	/**
	 * class owning this Muisbeheerder (where events are processed)
	 */
	private TekenApplet3D eigenaar;
	/** 
	 * keeping track of mouseDown events
	 */
	boolean mouseDown;

	/**
	 * constructor
	 * @param ap owner of this MuisBeheerder
	 */
	public MuisBeheerder(TekenApplet3D ap)
	{	eigenaar = ap;
		eerstex = 0;
		eerstey = 0;
		laatstex = 0;
		laatstey = 0;
		dx = 0;
		dy = 0;
	}
	
	/**
	 * handling mouseDown Events
	 */
	public void onMouseDown(MouseDownEvent e)
	{	
		e.preventDefault();
		e.stopPropagation();
		mouseDown = true;
		eerstex = e.getX();
		eerstey = e.getY();
		laatstex = e.getX();
		laatstey = e.getY();
		eigenaar.muisDrukActie();
	}
	
	/**
	 * handling MouseMove Events
	 */
	public void onMouseMove(MouseMoveEvent e)
	{	
		e.preventDefault();
		e.stopPropagation();
		if (!mouseDown)
			return;
		int x = e.getX();
		int y = e.getY();
		dx = x - laatstex;
		dy = laatstey -y;
		eigenaar.muisSleepActie();
		laatstex = x;
		laatstey = y;	
	}

	/**
	 * handling MouseUp Events
	 */
	public void onMouseUp(MouseUpEvent e)
	{	
		e.preventDefault();
		e.stopPropagation();
		
		mouseDown = false;
	
		eigenaar.muisLosActie();
	}

	/**
	 * handling TouchMove Events
	 */
	public void onTouchMove(TouchMoveEvent event) 
	{
		event.preventDefault();
		event.stopPropagation();
		if (event.getTouches().length() > 0) 
		{
			Touch touch = event.getTouches().get(0);
			int x = 0;
			int y = 0;
			if (eigenaar != null)
			{	
				x = touch.getPageX()- eigenaar.tb.tekenbladCanvas.getAbsoluteLeft();//getRelativeX(elem);
				y = touch.getPageY()- eigenaar.tb.tekenbladCanvas.getAbsoluteTop();//getRelativeY(elem);
			}
	        dx = x - laatstex;
			dy = laatstey -y;
			if (eigenaar != null)
				eigenaar.muisSleepActie();
			laatstex = x;
			laatstey = y;
	    }
	    event.preventDefault();
	    event.stopPropagation();
		
	}

	/**
	 * handling TouchStart Events
	 */
	public void onTouchStart(TouchStartEvent event) 
	{
		event.preventDefault();
		event.stopPropagation();
		if (event.getTouches().length() > 0) 
		{
			Touch touch = event.getTouches().get(0);
			if (eigenaar != null)
			{	
				eerstex = touch.getPageX() - eigenaar.tb.tekenbladCanvas.getAbsoluteLeft();//getRelativeX(elem);
				eerstey = touch.getPageY() - eigenaar.tb.tekenbladCanvas.getAbsoluteTop();;//getRelativeY(elem);
				laatstex = touch.getPageX() - eigenaar.tb.tekenbladCanvas.getAbsoluteLeft();;//getRelativeX(elem);
				laatstey = touch.getPageY() - eigenaar.tb.tekenbladCanvas.getAbsoluteTop();;//getRelativeY(elem);
			}
			if (eigenaar != null)
				eigenaar.muisDrukActie();
	    }
		event.preventDefault();
		event.stopPropagation();
		
	}

	/**
	 * handling TouchEnd Events
	 */
	public void onTouchEnd(TouchEndEvent event) 
	{
		event.preventDefault();
		event.stopPropagation();
		
		if (eigenaar != null)
			eigenaar.muisLosActie();
		event.preventDefault();
		event.stopPropagation();
		
	}

	/**
	 * getter for last mouseMove/touchMove x-translation
	 * @return dx
	 */
	public int geefSleepdx()
	{	return dx;
	}
	/**
	 * getter for last mouseMove/touchMove y-translation
	 * @return dy
	 */
	public int geefSleepdy()
	{	return dy;
	}
	/**
	 * getter for last mouseDown/touchStart x
	 * @return eerste x 
	 */
	public int geefDrukx()
	{	return eerstex;
	}
	/**
	 * getter for last mouseDown/touchStart y
	 * @return eerste y 
	 */
	public int geefDruky()
	{	return eerstey;
	}
	/**
	 * getter for last mouseDown/touchStart/mouseMove/touchMove event x
	 * @return laatste x
	 */
	public int geefX()
	{	return laatstex;
	}
	/**
	 * getter for last mouseDown/touchStart/mouseMove/touchMove event y
	 * @return laatste y
	 */
	public int geefY()
	{	return laatstey;
	}

}