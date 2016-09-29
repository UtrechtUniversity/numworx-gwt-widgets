package fi.sliderwidgetgwt.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;

public class SliderGWT extends FlowPanel implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
											//MouseListener,
											// MouseMotionListener
{
	private int lengte;
	private int stand;
	private int minimum = -2;
	//private int muisStartX;
	//private boolean raak;
	private boolean dragging = false;
	private String naam = "";
	private double onderGrens, bovenGrens, stapGrootte = 1;
	private int zijkantMarge = 20;
	private int bovenMarge = 20;

	private static final String FONT_STRING = "13px sans-serif";
	private static final int DOT_RADIUS = 5;
	
	public Canvas canvas;
	Context2d context;
	
	SliderWidgetGWT sliderWidgetGWT;

	public SliderGWT(int aantalPix, int beginst)
	{
		super();

		canvas = Canvas.createIfSupported();
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
		context.setFillStyle(CssColor.make(255, 255, 255)); // white
		context.fillRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());

		context.setFillStyle(CssColor.make(0, 0, 0));//black
		context.beginPath();
		context.moveTo(zijkantMarge, bovenMarge);
		context.lineTo(lengte + zijkantMarge, bovenMarge);
		context.stroke();
		
		// teken rondje
		context.setFillStyle(CssColor.make(255, 0, 0)); // red
		context.setStrokeStyle(CssColor.make(0, 0, 0)); // black
		//context.setLineWidth(1);
		context.beginPath();
		context.arc(zijkantMarge + stand, bovenMarge, DOT_RADIUS, 0, Math.PI * 2.0, true);
		context.closePath();
		context.fill();
		
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
				int intWaarde = (int) Math.round(waarde);
				context.fillText(naam + "=" + intWaarde, stand + zijkantMarge - metrics.getWidth(), 5);
			}
			else
			{
				waarde = (double) Math.round(10 * waarde) / 10;
				context.fillText(naam + "=" + waarde, stand + zijkantMarge - metrics.getWidth(), 10);
			}
		}
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
	}

	private void addHandlers()
	{
		canvas.addMouseDownHandler(this);
		canvas.addMouseMoveHandler(this);
		canvas.addMouseUpHandler(this);
	}

	@Override
	public void onMouseUp(MouseUpEvent event)
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
		
		// fire cross widget event
		this.sliderWidgetGWT.fire("double.sliderValue", geefNaam(), geefStand());
	}

	@Override
	public void onMouseMove(MouseMoveEvent event)
	{
		if (dragging == true)
		{
			stand = event.getX() - 20;
			if (stand < 0)
				stand = 0;
			if (stand > lengte)
				stand = lengte;
			paintComponent();
		}
	}

	@Override
	public void onMouseDown(MouseDownEvent event)
	{
		if (event.getX() >= zijkantMarge + stand - DOT_RADIUS 
				&& event.getX() <= zijkantMarge + stand + DOT_RADIUS 
				&& event.getY() >= bovenMarge - DOT_RADIUS
				&& event.getY() <= bovenMarge + DOT_RADIUS)
		{
			dragging = true;
		}
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
}
