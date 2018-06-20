package fi.tekenveelvlakgwt.client;

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

/**
 * klasse die een Slider representeert: de klasse bezit een Canvas
 * waarop de slider getekend wordt en onderschept Mouse en Touch Events
 * op dit Canvas waarmee de slider gemanipuleerd wordt; <br>
 * de stand van de slider kan veranderd worden door:<br>
 * 1) klikken oo de schuifKnop gevolgd door slepen naar een nieuwe positie<br>
 * 2) direct klikken op een nieuwe positie<br>
 * De nieuwe sliderstand wordt vervolgens doorgegeven aan de eigenaar van de slider 
 * @author Peter Boon
 */

public class Slider	
{	
	/**
	 * eigenaar
	 */
	TekenVeelvlak eigenaar;
	
	/**
	 * Canvas om de slider op te tekenen
	 */
	Canvas sliderCanvas;
	/**
	 * Context2d om de slider mee te tekenen
	 */
	Context2d sliderContext2d;

	/**
	 * de lengte (in paxels) waarover de sliderknop bewogen kan worden 
	 */
	private int lengte;
	/**
	 * de huidige x-positie (in pixels) van de sliderknop  
	 */
	private int stand;
	
	/**
	 * coordinaten laatste MouseDown/TouchStart
	 */
	private int muisStartX, muisStartY;
	/**
	 * de sliderknop
	 */
	private Polygon schuifKnop;
	/**
	 * true na MouseDown/TouchStart
	 */
	private boolean raak;
	
	/**
	 * horzontale en vertikale afmetingne van de hele slider (in pixels)
	 */
	int horSize, vertSize;
	
	/**
	 * achtergroundkleur van de slider
	 */
	CssColor achtergrondKleur;
	
	/**
	 * constructor add Handlers
	 * @param aantalPix lengte (in paxels) waarover de sliderknop bewogen kan worden
	 * @param beginst de beginpositien van de sliderknop
	 * @param eigen eigenaar van de slider
	 */
	public Slider(int aantalPix, int beginst, TekenVeelvlak eigen)
	{	lengte = aantalPix;
		stand = beginst;
		eigenaar = eigen;
		horSize = lengte+10;
		vertSize = 20;
		
		sliderCanvas = Canvas.createIfSupported();
		sliderCanvas.setWidth(horSize + "px");
		sliderCanvas.setHeight(vertSize + "px");
		sliderCanvas.setCoordinateSpaceWidth(horSize);
		sliderCanvas.setCoordinateSpaceHeight(vertSize);

		sliderCanvas.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.canvas());
		
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
	
	public void paint()
	{
		paint(sliderContext2d);
	}
	
	/**
	 * teken achtergrond en roep telenSlider aan
	 * @param g Context2d om te tekenen
	 */
	public void paint(Context2d g)
	{	g.setFillStyle(achtergrondKleur);
		g.fillRect(0,0,horSize, vertSize);
		tekenSlider(g);
	}
	
	
	/**
	 * teken outline, maak en teken de schuifknop
	 * @param g Context2d om te tekenen
	 */
	public void tekenSlider(Context2d g)
	{	
		// outline
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.setFillStyle(CssColor.make(0,0,0));
		g.strokeRect(5,7,lengte,6);
		
		schuifKnop = new Polygon();
		schuifKnop.addPoint(5+stand,0);
		schuifKnop.addPoint(5+stand+3,5);
		schuifKnop.addPoint(5+stand+3,15);
		schuifKnop.addPoint(5+stand,20);
		schuifKnop.addPoint(5+stand-3,15);
		schuifKnop.addPoint(5+stand-3,5);
		
		// schuifKnop
    	g.moveTo(schuifKnop.puntenX[0], schuifKnop.puntenY[0]);
		g.beginPath();
		for (int k = 1; k < schuifKnop.aantalPunten; k++)
		{	g.lineTo(schuifKnop.puntenX[k], schuifKnop.puntenY[k]);
		}
		g.lineTo(schuifKnop.puntenX[0], schuifKnop.puntenY[0]);
		g.closePath();
		g.fill();
		
		// outline schuifKnop
    	g.moveTo(schuifKnop.puntenX[0], schuifKnop.puntenY[0]);
		g.beginPath();
		for (int k = 1; k < schuifKnop.aantalPunten; k++)
		{	g.lineTo(schuifKnop.puntenX[k], schuifKnop.puntenY[k]);
		}
		g.lineTo(schuifKnop.puntenX[0], schuifKnop.puntenY[0]);
		g.closePath();
		g.stroke();

	}
	
	/**
	 * getter voor de stand van de slider (pixels)
	 * @return stand
	 */
	public int geefStand()
	{	return stand;
	}

	/**
	 * getter voor de maximale schuifruimte van de slider (pixels)
	 * @return lengte
	 */
	public int geefLengte()
	{	return lengte;
	}
	
	/**
	 * zet de stand van de slider
	 * @param std nieuwe stand
	 */
	public void zetStand(int std)
	{	if (std > lengte)
			stand = lengte;
		else if(std < 0)
			stand = 0;
		else 
			stand = std;
		paint();
	}

	/**
	 * inner class om Mouse Events op het sliderCanvas af te handelen 
	 */
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		boolean mouseDown = false;
		
		public void onMouseDown(MouseDownEvent e)
		{
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDown = true;
			
			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		public void onMouseMove(MouseMoveEvent e)	
		{
			e.stopPropagation();
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseMoveTouchMoveAction(eventX, eventY);
			
			
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{
			e.stopPropagation();
			
			mouseDown = false;

		}

	} //MouseHandler

	/**
	 * inner class om Touch Events op het sliderCanvas af te handelen 
	 */
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
				
			    //boolean shiftPressed = false;
			    int eventX = touch.getPageX() - sliderCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - sliderCanvas.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
					}

	}

	/**
	 * afhandelen MouseDown/TouchStart Events
	 * @param eventX x-coordinaat MouseDown/TouchStart Event
	 * @param eventY y-coordinaat MouseDown/TouchStart Event
	 */
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		raak = true;  
		stand = eventX-5;
		if (stand > lengte) 
		{	stand = lengte;
		}
		else if(stand < 0) 
		{	stand = 0;
		}
		paint();
		eigenaar.sliderAction();
		muisStartX = eventX;
		muisStartY = eventY;
	}
	
	/**
	 * afhandelen MouseMove/TouchMove Events
	 * @param eventX x-coordinaat MouseMove/TouchMove Event
	 * @param eventY y-coordinaat MouseMove/TouchMove Event
	 */
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		// kan dit? 
		if (!raak && new Rectangle(stand+5,0,10,20).contains(eventX, eventY))
		{	raak = true;
			muisStartX = eventX;
		}
		if (raak)
		{	int x = eventX;
			int dx = x - muisStartX;
			stand = x-5;
			if(stand>lengte) 
			{	stand = lengte;
			}
			else if(stand<0) 
			{	stand = 0;
			}
			paint();
			eigenaar.sliderAction();
			muisStartX = x;
		}
	}
	
}
