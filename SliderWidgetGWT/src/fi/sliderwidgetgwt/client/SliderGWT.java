package fi.sliderwidgetgwt.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
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
import com.vaadin.pointerevents.client.PointerCancelEvent;
import com.vaadin.pointerevents.client.PointerCancelHandler;
import com.vaadin.pointerevents.client.PointerDownEvent;
import com.vaadin.pointerevents.client.PointerDownHandler;
import com.vaadin.pointerevents.client.PointerEvent;
import com.vaadin.pointerevents.client.PointerMoveEvent;
import com.vaadin.pointerevents.client.PointerMoveHandler;
import com.vaadin.pointerevents.client.PointerUpEvent;
import com.vaadin.pointerevents.client.PointerUpHandler;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.FlowPanel;

public class SliderGWT extends FlowPanel implements	
	MouseDownHandler, MouseMoveHandler, MouseUpHandler, 
	TouchStartHandler, TouchMoveHandler, TouchEndHandler,
	PointerDownHandler, PointerMoveHandler, PointerUpHandler
											//MouseListener,
											// MouseMotionListener
{
	private static final String DECIMAL = LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator();
	private static final char PERIOD = '.';

	private int lengte;
	private int stand;
	private int minimum = -2;
	//private int muisStartX;
	//private boolean raak;
	private boolean dragging = false;
	private String naam = "";
	private double onderGrens, bovenGrens, stapGrootte = 1;
	private int zijkantMarge = 20;
	private int bovenMarge = 40;

	private static final String FONT_STRING = "13px sans-serif";
	private static final int DOT_RADIUS = 5;
	
	public Canvas canvas;
	Context2d context;
	
	SliderWidgetGWT sliderWidgetGWT;
	private boolean hasPointerEventSupport = false;

	public SliderGWT(int aantalPix, int beginst)
	{
		super();

		canvas = Canvas.createIfSupported();
		canvas.getElement().getStyle().setProperty("touchAction", "none");
		context = canvas.getContext2d();

		this.add(canvas);
		
		addHandlers();
		
		lengte = aantalPix;
		stand = beginst;
		
		if (naam.length() > 0)
		{
			zijkantMarge = 20;
			bovenMarge = 20;
		}

		// initialize minimum size
		setPixelSize(lengte + 2 * zijkantMarge, bovenMarge + DOT_RADIUS);
	}

	public void zetGrenzen(double onderGrens, double bovenGrens)
	{
		this.onderGrens = onderGrens;
		this.bovenGrens = bovenGrens;
	}

	public void zetStapGrootte(double stapGrootte)
	{
		this.stapGrootte = stapGrootte;
	}

	public void zetMinimum(int min)
	{
		minimum = min;
	}

	public void zetLengte(int aantalPix)
	{
		int lengteOud = lengte;
		lengte = aantalPix;
		stand = stand * lengte / lengteOud;
		
		if (naam.length() > 0)
		{
			zijkantMarge = 20;
			bovenMarge = 20;
		}
		
		setPixelSize(lengte + 2 * zijkantMarge, bovenMarge + DOT_RADIUS);
//		repaint();
	}

	public void zetNaam(String naam)
	{
		this.naam = naam;
		
		if (naam.length() > 0)
		{
			zijkantMarge = 20;
			bovenMarge = 20;
		}

		setPixelSize(lengte + 2 * zijkantMarge, bovenMarge + DOT_RADIUS);
	}

//	public boolean isRaak()
//	{
//		return raak;
//	}

	public void paintComponent()
	{
		// clear all
		context.clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		//context.setFillStyle(CssColor.make(255, 255, 255)); // white
		//context.fillRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());

		//teken slider
				context.setFillStyle(CssColor.make(0, 0, 0));//black
				context.beginPath();
				context.moveTo(zijkantMarge, bovenMarge);
				context.lineTo(lengte + zijkantMarge, bovenMarge);
				context.stroke();
		
		// vul rondje
		context.setFillStyle(CssColor.make(255, 0, 0)); // red
		context.setStrokeStyle(CssColor.make(0, 0, 0)); // black
		context.beginPath();
		context.arc(zijkantMarge + stand, bovenMarge, DOT_RADIUS, 0, Math.PI * 2.0, true);
		context.closePath();
		context.fill();
		
		//teken randje
		context.setFillStyle(CssColor.make(0, 0, 0));//black
		context.arc(zijkantMarge + stand, bovenMarge, DOT_RADIUS, 0, Math.PI * 2.0, true);
		context.stroke();
		
		if (naam.length() > 0)
		{
			double doubleStand = stand;
			double doubleLengte = lengte;
			double waarde = doubleStand / doubleLengte * (bovenGrens - onderGrens) + onderGrens;
			int aantalStappen = (int) ((bovenGrens - onderGrens) / stapGrootte);
			
			for (int i = 0; i < aantalStappen; i++)
			{
				if (waarde - onderGrens < i * stapGrootte + stapGrootte / 2)
				{
					waarde = onderGrens + i * stapGrootte;
					break;
				}
			}
			
			if (waarde - onderGrens > (aantalStappen - 1) * stapGrootte + stapGrootte / 2)
			{
				waarde = bovenGrens;
			}

			context.setFont(FONT_STRING);
			TextMetrics metrics;
			metrics = context.measureText(naam);
			context.setFillStyle(CssColor.make(0, 0, 0)); // black
			
			if (Math.round(stapGrootte) == stapGrootte)
			{
				String teken = waarde<-0.0001 ? "\u2013" : ""; //een echt minteken is duidelijker
				int intWaarde = Math.abs((int) Math.round(waarde));
				String intString = replaceWithLocalDecimalSeparator(String.valueOf(intWaarde));
				context.fillText(naam + " = " + teken + intString, stand + zijkantMarge - metrics.getWidth(), bovenMarge - 9);
			}
			else
			{
				String teken = waarde<-0.0001 ? "\u2013" : ""; //een echt minteken is duidelijker
				waarde = Math.abs((double) Math.round(10 * waarde) / 10);
				String waardeString = replaceWithLocalDecimalSeparator(String.valueOf(waarde));
				context.fillText(naam + " = " + teken + waardeString, stand + zijkantMarge - metrics.getWidth(), bovenMarge - 9);
			}
		}
	}

	/**
	 * Replace the default '.' decimal separator with the local separator.
	 * 
	 * @param s
	 * @return
	 */
	private String replaceWithLocalDecimalSeparator(String s)
	{
		String replacedString = s.replace(PERIOD, DECIMAL.charAt(0));
		
		return replacedString;
	}

	public int geefStand()
	{
		return stand;
	}

	public String geefNaam()
	{
		return naam;
	}

	public void zetStand(int std)
	{
		if (std > lengte)
			stand = lengte;
		else if (std < minimum)
			stand = minimum;
		else
			stand = std;
		
		// fire cross widget event
		this.sliderWidgetGWT.fire("double.sliderValue");
	}

	private void addHandlers()
	{
		// add mouse handlers
		canvas.addMouseDownHandler(this);
		canvas.addMouseMoveHandler(this);
		canvas.addMouseUpHandler(this);

		// add touch handlers
		canvas.addTouchStartHandler(this);
		canvas.addTouchMoveHandler(this);
		canvas.addTouchEndHandler(this);
		
		canvas.addDomHandler((PointerMoveHandler)this, PointerMoveEvent.getType()); 
		canvas.addDomHandler((PointerUpHandler)this, PointerUpEvent.getType()); 
		canvas.addDomHandler((PointerDownHandler)this, PointerDownEvent.getType()); 
	}

	@Override
	public void onMouseUp(MouseUpEvent event)
	{
		event.preventDefault();
		event.stopPropagation();
		if(hasPointerEventSupport)
			return;
		mouseUpTouchEndAction();
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		event.preventDefault();
		event.stopPropagation();
		if(hasPointerEventSupport)
			return;
		mouseMoveTouchMoveAction(event.getX());
	}

	@Override
	public void onMouseDown(MouseDownEvent event)
	{
		event.preventDefault();
		event.stopPropagation();
		if(hasPointerEventSupport)
			return;
		mouseDownTouchStartAction(event.getX(), event.getY());
	}
	
	/**
	 * Set the size of the slider canvas and its parent.
	 * 
	 * @param w
	 * @param h
	 */
	public void setSize(int w, int h)
	{
		this.setPixelSize(w, h);
		canvas.setCoordinateSpaceWidth(w);
		canvas.setCoordinateSpaceHeight(h);
	}
	
	public void setSliderWidgetGWT(SliderWidgetGWT sliderWidgetGWT)
	{
		this.sliderWidgetGWT = sliderWidgetGWT;
	}

	/**
	 * Methode die de mouse down of touch start actie uitvoert.
	 * 
	 * @param x
	 * 		De x-coordinaat van de mouse of touch
	 * @param y
	 * 		De y-coordinaat van de mouse of touch
	 */
	public void mouseDownTouchStartAction(int x, int y)
	{
		if (x >= zijkantMarge + stand - DOT_RADIUS 
			&& x <= zijkantMarge + stand + DOT_RADIUS
			&& y >= bovenMarge - DOT_RADIUS 
			&& y <= bovenMarge + DOT_RADIUS)
		{
			dragging = true;
		}
	}

	/**
	 * Methode die de mouse move of touch move actie uitvoert.
	 * 
	 * @param x
	 * 		De x-coordinaat van de mouse of touch
	 */
	public void mouseMoveTouchMoveAction(int x)
	{
		if (dragging == true)
		{
			stand = x - 20;
			if (stand < 0)
				stand = 0;
			if (stand > lengte)
				stand = lengte;
			
			zetStand(stand);
			
			paintComponent();
		}
	}

	/**
	 * Methode die de mouse up of touch end actie uitvoert.
	 * 
	 */
	public void mouseUpTouchEndAction()
	{
		dragging = false;

		int aantalStappen = lengte;
		int intStapGrootte = 1;
		if (stapGrootte != 0)
		{
			intStapGrootte = (int) Math.round(stapGrootte * lengte / (bovenGrens - onderGrens));
			aantalStappen = (int) (lengte / intStapGrootte);
		}
		
		for (int i = 0; i < aantalStappen; i++)
		{
			if (stand < i * intStapGrootte + intStapGrootte / 2)
			{
				stand = (int) (i * intStapGrootte);
				break;
			}
		}
		if (stand > (aantalStappen - 1) * intStapGrootte + intStapGrootte / 2)
		{
			stand = lengte;
		}
		
		paintComponent();
		
		zetStand(stand);
	}

	@Override
	public void onTouchStart(TouchStartEvent event)
	{
		event.preventDefault();
		event.stopPropagation();
		if(hasPointerEventSupport)
			return;
		
		Touch touch = event.getTouches().get(0);
		int eventX = touch.getPageX() - canvas.getAbsoluteLeft();
		int eventY = touch.getPageY() - canvas.getAbsoluteTop();

		mouseDownTouchStartAction(eventX, eventY);
	}

	@Override
	public void onTouchEnd(TouchEndEvent event)
	{
		event.preventDefault();
		event.stopPropagation();
		if(hasPointerEventSupport)
			return;
		mouseUpTouchEndAction();
	}

	@Override
	public void onTouchMove(TouchMoveEvent event)
	{
		event.preventDefault();
		event.stopPropagation();
		if(hasPointerEventSupport)
			return;
		
		Touch touch = event.getTouches().get(0);
		int eventX = touch.getPageX() - canvas.getAbsoluteLeft();

		mouseMoveTouchMoveAction(eventX);
	}

	@Override
	public void onPointerUp(PointerUpEvent event) {
		mouseUpTouchEndAction();
		
	}

	@Override
	public void onPointerMove(PointerMoveEvent event) {
		event.preventDefault();
		event.stopPropagation();
		
		int eventX = event.getRelativeX(canvas.getElement());

		mouseMoveTouchMoveAction(eventX);
		
	}

	@Override
	public void onPointerDown(PointerDownEvent event) {
		event.preventDefault();
		event.stopPropagation();
		
		hasPointerEventSupport = true;
		
		int eventX = event.getRelativeX(canvas.getElement());
		int eventY= event.getRelativeY(canvas.getElement());

		mouseDownTouchStartAction(eventX, eventY);
	}

}
