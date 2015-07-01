package fi.mozarchgwt.client;

//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.event.MouseMotionListener;

import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;


//class MuisBeheerder2 implements MouseListener, MouseMotionListener
class MuisBeheerder2 implements MouseDownHandler, MouseMoveHandler, MouseUpHandler, 
								TouchStartHandler, TouchMoveHandler, TouchEndHandler
{
	private int eerstex, laatstex, eerstey, laatstey, dx, dy;
	private TekenPanel eigenaar;
	private boolean actief;
	
	boolean mouseDown = false;
	
	Canvas mozarchGWTCanvas;
	
	public MuisBeheerder2(TekenPanel ap)
	{	eigenaar = ap;
		mozarchGWTCanvas = eigenaar.mozarchGWTCanvas;
		actief = true;
		eerstex = 0;
		eerstey = 0;
		laatstex = 0;
		laatstey = 0;
		dx = 0;
		dy = 0;
	}
	//-------------------------------------------------------------------------------------------
	//afhandeling van de muis gebeurtenissen 
	//-------------------------------------------------------------------------------------------
	//public void mousePressed(MouseEvent e)
	public void onMouseDown(MouseDownEvent e)
	{	
		e.stopPropagation();
		
		if (actief)
		{	
			mouseDown = true;	
			
			eerstex = e.getX();
			eerstey = e.getY();
			laatstex = e.getX();
			laatstey = e.getY();
			eigenaar.muisDrukActie();
		}
	}
	
	//public void mouseDragged(MouseEvent e)
	public void onMouseMove(MouseMoveEvent e)
	{	
		e.stopPropagation();
		
		if (!mouseDown)
			return;
		if (actief)
		{	int x = e.getX();
			int y = e.getY();
			dx = x - laatstex;
			dy = laatstey - y;
			eigenaar.muisSleepActie();
			laatstex = x;
			laatstey = y;	
		}
	}
	//public void mouseReleased(MouseEvent e)
	public void onMouseUp(MouseUpEvent e)
	{	
		e.stopPropagation();
		
		mouseDown = false;	
		eigenaar.muisLosActie();
	}
	//public void mouseExited(MouseEvent e){;}
	//public void mouseClicked(MouseEvent e){;}
	//public void mouseEntered(MouseEvent e){;}
	//public void mouseMoved(MouseEvent e){;}
	
	public void onTouchStart(TouchStartEvent e)
	{	
		e.preventDefault();
		e.stopPropagation();
		
		if (e.getTouches().length() > 0)
		{
			Touch touch = e.getTouches().get(0);
			
			eerstex = touch.getPageX() - mozarchGWTCanvas.getAbsoluteLeft();
			eerstey = touch.getPageY() - mozarchGWTCanvas.getAbsoluteTop();				
			laatstex = touch.getPageX() - mozarchGWTCanvas.getAbsoluteLeft();
			laatstey = touch.getPageY() - mozarchGWTCanvas.getAbsoluteTop();				
			
			eigenaar.muisDrukActie();
			
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
			
		    int x = touch.getPageX() - mozarchGWTCanvas.getAbsoluteLeft();
			int y = touch.getPageY() - mozarchGWTCanvas.getAbsoluteTop();				
		    
			dx = x - laatstex;
			dy = laatstey - y;
			eigenaar.muisSleepActie();
			laatstex = x;
			laatstey = y;	

			
	    }
		e.preventDefault();
		e.stopPropagation();
		
	}
	public void onTouchEnd(TouchEndEvent e)
	{
		e.preventDefault();
		e.stopPropagation();
		
		eigenaar.muisLosActie();

		e.preventDefault();
		e.stopPropagation();

	}

	//-------------------------------------------------------------------------------------------
	//deze methode wordt gebruikt door de TraceBeheerder
	//-------------------------------------------------------------------------------------------
	public void setEnableMuisActie(boolean b)
	{	actief = b;
	}
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door de muishandlers in het leerlingenprogramma
	//-------------------------------------------------------------------------------------------
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

}	

