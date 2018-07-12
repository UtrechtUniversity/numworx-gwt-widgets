package fi.normverdgwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * klasse die een slider representeert; merk op dat de actie die uitgevoerd wordt
 * na veranderen van de slider-positie in de klasse "owner" gedefinieerd is;
 * de slider kan gebruikt worden om verschillende parameters te veranderen
 * aangezien de slider de parameter-naam kent; veranderen van de slider-positie
 * gebeurt door slepen van de sliderknop; 
 * merk ook op dat de slider op een extern Canvas getekend wordt m.b.v. de
 * Context2d van dat Canvas; dit Canvas onderschept ook Mouse/Touch Events 
 * op de slider   
 */

public class Slider	
{	
	/**
	 * Context2d on de slider te tekenen
	 */
	Context2d sliderContext2d;
	/**
	 * x-positie van de slider (pixels)
	 */
	int xPos;
	/**
	 * y-positie van de slider (pixels)
	 */
	int yPos;
	/**
	 * breedte van de slider (pixels)
	 */
	int breedte;
	/**
	 * hoogte van de slider (pixels)
	 */
	int hoogte;
	/**
	 * rechthoek met de bounds van de slider; 
	 * niet gebruikt 
	 */
	Rectangle sliderRectangle;
	
	/**
	 * het aantal pixels waarover de sliderknop kan bewegen
	 */
	private int lengte;
	/**
	 * de stand (in pixels) van de sliderknop: tussen minimum en maximum
	 */
	private int stand;
	/**
	 * de minimum stand van de sliderknop
	 */
	private int minimum = -2;
	/**
	 * de maximum stand van de sliderknop
	 */
	private int maximum;
	/**
	 * x-positie muis bij MouseDown/TouchStart
	 */
	private int muisStartX; 

	/**
	 * true als Mouse/Touch actie binnen de "gevoeligheids"
	 * rectangle van de sliderknop, zie methoden MouseStartTouchDownAction
	 * en MouseMoveTouchMoveAction
	 */
	private boolean raak;

	/**
	 * true: teken de horizontale lijn waarover de sliderknop beweegt
	 */
	private boolean showLine = true;
	
	/**
	 * kleur van de sliderknop
	 */
	private CssColor knopColor = CssColor.make(255,0,0);
	
	/**
	 * is de slider enabled (d.w.z. luistert naar Mouse/Touch Events?
	 */
	private boolean enabled = true;
	
	/**
	 * owner van deze slider
	 */
	NormaalPanel owner;
	
	/**
	 * String met de naam van de parameter die deze slider kan veranderen
	 */
	String param = "";
	
	/**
	 * constructor
	 * @param o owner
	 * @param aantalPix lengte waarover de sliderknop kan bewegen (pixels)
	 * @param beginst beginstand van de sliderknop
	 * @param x x-positie slider
	 * @param y y-positie slider
	 * @param c2d Contetx2d on de slider te tekenen
	 * @param p String met naam van de parameter die door de slider veranderd kan worden
	 */
	public Slider(NormaalPanel o, int aantalPix, int beginst, int x, int y, Context2d c2d, String p)
	{	
		owner = o;
		param = p;
		
		lengte = aantalPix;
		maximum = lengte;
		stand = beginst;
		
		xPos = x;
		yPos = y;
		breedte = lengte + 10;
		hoogte = 20;
		if (param.equalsIgnoreCase("grens"))
			hoogte = 13;
		sliderRectangle = new Rectangle(xPos, yPos, breedte, hoogte);
		
		sliderContext2d = c2d;
	}
	
	/**
	 * zet de lengte (aantal pixels waarover de sliderknop bewogen kan worden)
	 * @param aantalPix nieuwe lengte
	 */
	public void zetLengte(int aantalPix)
	{	lengte = aantalPix;
		maximum = lengte;
		breedte = lengte + 10;
		sliderRectangle = new Rectangle(xPos, yPos, breedte, hoogte);
		paint();
	}

	/**
	 * zet de nieuwe positie van de slider
	 * @param x x-coordinaat slider
	 * @param y y-coordianat slider
	 */
	public void setLocation(int x, int y)
	{
		xPos = x;
		yPos = y;
		sliderRectangle = new Rectangle(xPos, yPos, breedte, hoogte);
	}
		
	/**
	 * true: teken de lijn waarlangs de sliderknop beweegt 
	 * @param b true/false
	 */
	public void zetShowLine(boolean b)
	{	showLine = b;
	}	
	
	/**
	 * zet de kleur van de sliderknop
	 * @param c nieuwe kleur
	 */
	public void zetKnopColor(CssColor c)
	{	knopColor = c;
	}
		
	/**
	 * enable/disable de slider
	 * @param b true/false
	 */
	public void zetEnabled(boolean b)
	{	enabled = b;
	}

	public void paint()
	{
		tekenSlider(sliderContext2d);
	}
	
	/**
	 * teken de slider
	 * @param g Context2d om te tekenen
	 */
	public void tekenSlider(Context2d g)
	{	

		if (enabled && !param.equals("grens"))
		{	
			g.setFillStyle(NormaalPanel.veryLightBlue);
			g.fillRect(xPos, yPos, breedte, hoogte);
		}
		
		int deltay = 5;

		if (!param.equalsIgnoreCase("grens"))
			deltay = hoogte / 2;
	
		if (showLine && enabled)
		{	
			g.setStrokeStyle(CssColor.make(0,0,0));
			g.beginPath();
			g.moveTo(xPos + 5, yPos + deltay);
			g.lineTo(xPos + lengte + 5, yPos + deltay);
			g.stroke();
		
		}
	
		if (enabled)
		{
			g.setFillStyle(knopColor);
			g.beginPath();
            g.arc(xPos + 5 + stand, yPos + deltay, 3, 0, 2 * Math.PI);
       	 	g.fill();
       	 	
			g.setStrokeStyle(CssColor.make(0,0,0));
			g.beginPath();
            g.arc(xPos + 5 + stand, yPos + deltay, 3, 0, 2 * Math.PI);
       	 	g.stroke();
       	 	
		}
	
	}

	/**
	 * geef de stand van de slider
	 * @return stand
	 */
	public int geefStand()
	{	return stand;
	}
	
	/**
	 * zet de stand van de slider (tussen
	 * minimum en maximum)
	 * @param std nieuwe waarde stand
	 */
	public void zetStand(int std)
	{	if (std > maximum)
			stand = maximum;
		else if (std < minimum)
			stand = minimum;
		else 
			stand = std;
		paint();
	}

	/**
	 * zet de maximum stand van de slider
	 * @param max nieuw maximum
	 */
	public void setMaximum(int max)
	{	maximum = max;
	}

	/**
	 * zet de minimum stand van de slider
	 * @param min nieuw minimum
	 */
	public void setMinimum(int min)
	{	minimum = min - 2;
	}

	/**
	 * get de maximum stand van de slider
	 * @return maximum
	 */
	public int getMaximum()
	{	return maximum;
	}
	
	/**
	 * get de minimum stand van de slider
	 * @return 0
	 */
	public int getMinimum()
	{	return 0;
	}
	
	/**
	 * actie bij MouseDown/TouchStart: kijk of de slider aangeklikt is en fixeer de 
	 * x-positie van de klik
	 * @param eventX x-coordinaat MouseStart/TouchDown Event
	 * @param eventY y-coordinaat MouseStart/TouchDown Event
	 */
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		raak = enabled && (new Rectangle(xPos + stand - 3, yPos, 16, hoogte)).contains(eventX, eventY);
		muisStartX = eventX;
	}
	
	/**
	 * actie bij MouseMove/TouchMove: verschuif de slider,
	 * verander de juiste parameter in klasse owner
	 * @param eventX x-coordinaat MouseMove/TouchMove Event
	 * @param eventY y-coordinaat MouseMove/TouchMove Event
	 */
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		if (!raak && enabled && 
			(new Rectangle(xPos + stand - 3, yPos, 16, hoogte).contains(eventX, eventY)))
		{	raak = true;
			muisStartX = eventX;
		}
		// verander stand
		if (raak)
		{	int x = eventX;
			int dx = x - muisStartX;
			stand = stand + dx;
			if (stand > maximum) 
			{	stand = maximum;
			}
			else if (stand < minimum) 
			{	stand = minimum;
			}
			if (x < (xPos + 5) || x > (xPos + lengte + 20))
			{	raak = false;
			}
			paint();
			
			if (param.equals("mu"))
				owner.processMuSlider();
			else if (param.equals("sigma"))
				owner.processSigmaSlider();
			else if (param.equals("grens"))
				owner.processGrensSlider();
			else if (param.equals("kans"))
				owner.processKansSlider();
			muisStartX = x;
		}
	}
	
	/**
	 * actie bij MouseUp/TouchEnd: verwittig de owner dat er iets veranderd is
	 */
	public void mouseUpTouchEndAction()
	{	
		owner.changed();
	}

}
