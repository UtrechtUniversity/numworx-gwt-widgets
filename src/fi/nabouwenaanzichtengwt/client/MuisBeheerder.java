package fi.nabouwenaanzichtengwt.client;


import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseEvent;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

import com.google.gwt.user.client.ui.Widget;

/**
 * afhandelen Mouse/Touch Events;<br>
 * zie klasse Muisbeheerder in WebLogoGWT 
 */

class MuisBeheerder implements MouseDownHandler, MouseUpHandler, MouseMoveHandler,
							   TouchStartHandler, TouchMoveHandler, TouchEndHandler 
{
	private int eerstex, laatstex, eerstey, laatstey, dx, dy;
	private Viewer3d eigenaar;
	private boolean mouseDown;
	
	public MuisBeheerder(Viewer3d v3d)
	{	eigenaar = v3d;
		eerstex = 0;
		eerstey = 0;
		laatstex = 0;
		laatstey = 0;
		dx = 0;
		dy = 0;
		v3d.zetMuisBeheerder(this);
	}
	
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
	
	public void onMouseMove(MouseMoveEvent e)
	{	e.preventDefault();
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
	
	public void onMouseUp(MouseUpEvent e)
	{	
		e.preventDefault();
		e.stopPropagation();
	
		mouseDown = false;
		eigenaar.muisLosActie();
		
	}
	
	public int geefSleepdx()
	{	return dx;
	}
	public int geefSleepdy()
	{	return dy;
	}
	public int geefDrukx()
	{	return eerstex;
	}
	public int geefDruky()
	{	return eerstey;
	}
	public int geefX()
	{	return laatstex;
	}
	public int geefY()
	{	return laatstey;
	}

	public void onTouchMove(TouchMoveEvent event) 
	{
		event.preventDefault();
		event.stopPropagation();
		if (event.getTouches().length() > 0) 
		{
			Touch touch = event.getTouches().get(0);
			Widget sender = (Widget) event.getSource();
		    Element elem = sender.getElement();
			int x = touch.getPageX()- eigenaar.getCanvas().getAbsoluteLeft();//getRelativeX(elem);
			int y = touch.getPageY()- eigenaar.getCanvas().getAbsoluteTop();//getRelativeY(elem);
	        dx = x - laatstex;
			dy = laatstey -y;
			eigenaar.muisSleepActie();
			laatstex = x;
			laatstey = y;
	    }
	    event.preventDefault();
	    event.stopPropagation();
		
	}

	public void onTouchStart(TouchStartEvent event) 
	{
		event.preventDefault();
		event.stopPropagation();
		if (event.getTouches().length() > 0) 
		{
			Touch touch = event.getTouches().get(0);
			Widget sender = (Widget) event.getSource();
		    Element elem = sender.getElement();
			eerstex = touch.getPageX() - eigenaar.getCanvas().getAbsoluteLeft();//getRelativeX(elem);
			eerstey = touch.getPageY() - eigenaar.getCanvas().getAbsoluteTop();;//getRelativeY(elem);
			laatstex = touch.getPageX() - eigenaar.getCanvas().getAbsoluteLeft();;//getRelativeX(elem);
			laatstey = touch.getPageY() - eigenaar.getCanvas().getAbsoluteTop();;//getRelativeY(elem);
			eigenaar.muisDrukActie();
	    }
		event.preventDefault();
		event.stopPropagation();
		
	}

	public void onTouchEnd(TouchEndEvent event) 
	{
		eigenaar.muisLosActie();
	}

}	