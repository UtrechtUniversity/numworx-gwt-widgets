package fi.tekenveelvlakgwt.client;

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
 * klasse om Mouse en Touch events af te handelen; een instantie van deze klasse wordt
 * als MouseDown/MouseMove/MouseUp/TouchStart/TouchMove/TouchEnd handler toegevoegd 
 * aan het Canvas waarop Events plaatsvinden; de Event details worden doorgestuurd 
 * naar de klasse die de instantie van MuisBeheerder bezit  
 */

public class MuisBeheerder implements MouseDownHandler, MouseUpHandler, MouseMoveHandler,
									  TouchStartHandler, TouchMoveHandler, TouchEndHandler
{
	/**
	 * x-coordinaat van het laatste MouseDown/TouchStart Event 
	 */
	private int eerstex;
	/**
	 * y-coordinaat van het laatste MouseDown/TouchStart Event
	 */
	private int eerstey;
	/**
	 * x-coordinaat van het laatste MouseMove/TouchMove Event
	 */
	private int laatstex;
	/**
	 * y-coordinaat van het laatste MouseMove/TouchMove Event
	 */
	private int laatstey;
	/**
	 * laatste MouseMove/TouchMove x-translatie
	 */
	private int dx;
	/**
	 * laatste MouseMove/TouchMove y-translatie
	 */
	private int dy;
	/**
	 * eigenaar
	 */
	private TekenApplet3D eigenaar;
	/**
	 * eigenaar1
	 */
	private Viewer3d eigenaar1;

	/**
	 * mouseDown Event?
	 */
	boolean mouseDown;

	/**
	 * constructor voor MuisBeheerder in TekenApplet3D 
	 * @param ap eigenaar
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
	 * constructor voor MuisBeheerder in Viewer3d 
	 * @param ap eigenaar1
	 */
	public MuisBeheerder(Viewer3d ap)
	{	eigenaar1 = ap;
		eerstex = 0;
		eerstey = 0;
		laatstex = 0;
		laatstey = 0;
		dx = 0;
		dy = 0;
	}

	/**
	 * afhandelen MouseDown Events 
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
		if (eigenaar != null)
			eigenaar.muisDrukActie();

		if (eigenaar1 != null)
			eigenaar1.muisDrukActie();

	}	
	/**
	 * afhandelen MouseMove Events 
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
		dy = laatstey - y;
		if (eigenaar != null)
			eigenaar.muisSleepActie();

		if (eigenaar1 != null)
			eigenaar1.muisSleepActie();
		laatstex = x;
		laatstey = y;
		
	}

	/**
	 * afhandelen MouseUp Events 
	 */
	public void onMouseUp(MouseUpEvent e)
	{	
		e.preventDefault();
		e.stopPropagation();

		mouseDown = false;
		if (eigenaar != null)
			eigenaar.muisLosActie();

		if (eigenaar1 != null)
			eigenaar1.muisLosActie();


	}

	/**
	 * afhandelen TouchMove Events 
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
				x = touch.getPageX()- eigenaar.tb.canvas.getAbsoluteLeft();
				y = touch.getPageY()- eigenaar.tb.canvas.getAbsoluteTop();
			}
			else if (eigenaar1 != null)
			{
				x = touch.getPageX()- eigenaar1.canvas.getAbsoluteLeft();
				y = touch.getPageY()- eigenaar1.canvas.getAbsoluteTop();

			}
			dx = x - laatstex;
			dy = laatstey -y;
			if (eigenaar != null)
				eigenaar.muisSleepActie();

			if (eigenaar1 != null)
				eigenaar1.muisSleepActie();

			laatstex = x;
			laatstey = y;
		}
		event.preventDefault();
		event.stopPropagation();

	}

	/**
	 * afhandelen TouchStart Events 
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
				eerstex = touch.getPageX() - eigenaar.tb.canvas.getAbsoluteLeft();
				eerstey = touch.getPageY() - eigenaar.tb.canvas.getAbsoluteTop();
				laatstex = touch.getPageX() - eigenaar.tb.canvas.getAbsoluteLeft();
				laatstey = touch.getPageY() - eigenaar.tb.canvas.getAbsoluteTop();
			}
			else if (eigenaar1 != null)
			{	
				eerstex = touch.getPageX() - eigenaar1.canvas.getAbsoluteLeft();
				eerstey = touch.getPageY() - eigenaar1.canvas.getAbsoluteTop();
				laatstex = touch.getPageX() - eigenaar1.canvas.getAbsoluteLeft();
				laatstey = touch.getPageY() - eigenaar1.canvas.getAbsoluteTop();
			} 
			if (eigenaar != null)
				eigenaar.muisDrukActie();

			if (eigenaar1 != null)
				eigenaar1.muisDrukActie();

		}
		event.preventDefault();
		event.stopPropagation();

	}

	/**
	 * afhandelen TouchEnd Events 
	 */
	public void onTouchEnd(TouchEndEvent event) 
	{
		event.preventDefault();
		event.stopPropagation();

		if (eigenaar != null)
			eigenaar.muisLosActie();

		if (eigenaar1 != null)
			eigenaar1.muisLosActie();

		event.preventDefault();
		event.stopPropagation();

	}

	/**
	 * getter voor dx
	 * @return dx
	 */
	public int geefSleepdx()
	{	return dx;
	}
	/**
	 * getter voor dy
	 * @return dy
	 */
	public int geefSleepdy()
	{	return dy;
	}	
	/**
	 * getter voor eerstex 
	 * @return eerstex
	 */
	public int geefDrukx()
	{	return eerstex;
	}
	/**
	 * getter voor eerstey 
	 * @return eerstey
	 */
	public int geefDruky()
	{	return eerstey;
	}
	/**
	 * getter voor laatstex 
	 * @return laatstex
	 */
	public int geefX()
	{	return laatstex;
	}
	/**
	 * getter voor laatstey 
	 * @return laatstey
	 */
	public int geefY()
	{	return laatstey;
	}

}	


