package fi.weblogo3dgwt.client.logotekenap3d;

//import java.awt.*;
//import java.awt.event.*;

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


public class MuisBeheerder implements MouseDownHandler, MouseUpHandler, MouseMoveHandler,
									  TouchStartHandler, TouchMoveHandler, TouchEndHandler
{
	private int eerstex, laatstex, eerstey, laatstey, dx, dy;
	private TekenApplet3D eigenaar;
	//private AnimatieBeheerder ab;
	private boolean animatieWasAan;
	boolean mouseDown;
	
	public MuisBeheerder(TekenApplet3D ap)
	{	eigenaar = ap;
		eerstex = 0;
		eerstey = 0;
		laatstex = 0;
		laatstey = 0;
		dx = 0;
		dy = 0;
	}
	
	//-------------------------------------------------------------------------------------------
	//de AnimatieBeheerder maakt zich met deze methode kenbaar aan de Muisbeheerder  
	//-------------------------------------------------------------------------------------------
	//public void meldAnimatieBeheerder(AnimatieBeheerder ab)
	//{	this.ab = ab;
	//}
	
	//-------------------------------------------------------------------------------------------
	//afhandeling van de muis gebeurtenissen 
	//-------------------------------------------------------------------------------------------
	public void onMouseDown(MouseDownEvent e)
	{	
//		if (ab != null && ab.animatieLopend())
//		{	animatieWasAan = true;
//			ab.onderbreekAnimatie();
//		}
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
	
	public void onMouseUp(MouseUpEvent e)
	{	
		e.preventDefault();
		e.stopPropagation();
		
		mouseDown = false;
	
		eigenaar.muisLosActie();
//		if (animatieWasAan)
//		{	animatieWasAan = false;
//			ab.beginAnimatie();
//		}
	}

	public void onTouchMove(TouchMoveEvent event) 
	{
		event.preventDefault();
		event.stopPropagation();
		if (event.getTouches().length() > 0) 
		{
			Touch touch = event.getTouches().get(0);
			//Widget sender = (Widget) event.getSource();
		    //Element elem = sender.getElement();
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

	@Override
	public void onTouchStart(TouchStartEvent event) 
	{
		event.preventDefault();
		event.stopPropagation();
		if (event.getTouches().length() > 0) 
		{
			Touch touch = event.getTouches().get(0);
			//Widget sender = (Widget) event.getSource();
		    //Element elem = sender.getElement();
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

	@Override
	public void onTouchEnd(TouchEndEvent event) 
	{
		event.preventDefault();
		event.stopPropagation();
		
		if (eigenaar != null)
			eigenaar.muisLosActie();
		event.preventDefault();
		event.stopPropagation();
		
	}
	

	//public void mouseExited(MouseEvent e){;}
	//public void mouseClicked(MouseEvent e){;}
	//public void mouseEntered(MouseEvent e){;}
	//public void mouseMoved(MouseEvent e){;}
	
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