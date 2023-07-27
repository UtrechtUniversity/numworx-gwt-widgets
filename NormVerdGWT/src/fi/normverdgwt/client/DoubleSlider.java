package fi.normverdgwt.client;


import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * een klasse die een slider met twee knoppen implementeert:
 * merk op dat de actie die uitgevoerd wordt na veranderen van 
 * de positie van een van de knoppen in de klasse "owner" gedefinieerd is;
 * de slider kan dus twee parameters veranderen; veranderen van de slider-positie
 * van de knoppen gebeurt door slepen, waarbij de linkerknop niet rechts van
 * de rechterknop gesleept kan worden en de rechterknop niet links van de linkerknop
 * gesleept kan worden; 
 * merk ook op dat de slider op een extern Canvas getekend wordt m.b.v. de
 * Context2d van dat Canvas; dit Canvas onderschept ook Mouse/Touch Events 
 * op de slider    
 * @author huub
 */

public class DoubleSlider //extends JComponent implements MouseListener, MouseMotionListener
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
	 * het aantal pixels waarover de sliderknoppen kunnen bewegen
	 */
	private int lengte;

	/**
	 * de stand (in pixels) van de linker sliderknop: tussen minimumLinks en maximumLinks
	 */
	private int standLinks;

	/**
	 * de stand (in pixels) van de rechter sliderknop: tussen minimumRechts en maximumRechts
	 */
	private int standRechts;
	
	/**
	 * de minimum stand van de linker sliderknop
	 */
	private int minimumLinks = -2;
	/**
	 * de maximum stand van de linker sliderknop
	 */
	private int maximumLinks;
	/**
	 * de minimum stand van de rechter sliderknop
	 */
	private int minimumRechts;
	/**
	 * de maximum stand van de rechter sliderknop
	 */
	private int maximumRechts; 
	/**
	 * x-positie muis bij MouseDown/TouchStart
	 */
	private int muisStartX;

	/**
	 * true als Mouse/Touch actie binnen de "gevoeligheids"
	 * rectangle van linker de sliderknop, zie methoden MouseStartTouchDownAction
	 * en MouseMoveTouchMoveAction
	 */
	private boolean raakLinks;
	/**
	 * true als Mouse/Touch actie binnen de "gevoeligheids"
	 * rectangle van de rechter sliderknop, zie methoden MouseStartTouchDownAction
	 * en MouseMoveTouchMoveAction
	 */
	private boolean raakRechts;

	/**
	 * is de linker sliderknop enabled (d.w.z. luistert naar Mouse/Touch Events?
	 */
	private boolean linksEnabled = true;
	/**
	 * is de rechter sliderknop enabled (d.w.z. luistert naar Mouse/Touch Events?
	 */
	private boolean rechtsEnabled = true;
	
	/**
	 * true: teken de horizontale lijn waarover de sliderknop beweegt
	 */
	private boolean showLine = true;
	
	/**
	 * kleur van de linker sliderknop
	 */
	private CssColor knopLinksColor = CssColor.make(255,153,0);
	/**
	 * kleur van de rechter sliderknop
	 */
	private CssColor knopRechtsColor = CssColor.make(255,0,255);

	/**
	 * de minimum afstand in pixels tussen linker- en recher sliderknop
	 */
	int pixDis = 10;
	
	/**
	 * owner van deze DoubleSlider
	 */
	NormaalPanel owner;
	
	/**
	 * constructor
	 * @param o owner
	 * @param aantalPix lengte waarover de sliderknoppen kunnen bewegen (pixels)
	 * @param beginLinks beginstand linker sliderknop
	 * @param beginRechts beginstand rechter sliderknop
	 * @param x x-positie slider
	 * @param y y-positie slider
	 * @param c2d Contetx2d on de slider te tekenen
	 */
	public DoubleSlider(NormaalPanel o, int aantalPix, int beginLinks, int beginRechts,
						int x, int y, Context2d c2d)
	{	
		owner = o;
		
		lengte = aantalPix;
		standLinks = beginLinks;
		standRechts = beginRechts;
		// minimumLinks = -2; blijft zo
		maximumLinks = standRechts - pixDis;		
		minimumRechts = standLinks + pixDis - 2;
		maximumRechts = lengte;

		xPos = x;
		yPos = y;
		breedte = lengte + 10;
		hoogte = 13;
		sliderRectangle = new Rectangle(xPos, yPos, breedte, hoogte);
		
		sliderContext2d = c2d;
	}

	/**
	 * zet de lengte (aantal pixels waarover de sliderknoppen bewogen kunnen worden)
	 * @param aantalPix nieuwe lengte
	 */
	public void zetLengte(int aantalPix)
	{	lengte = aantalPix;
		maximumRechts = lengte;
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
	 * get de lengte waarover de sliderknoppen kunnen bewegen (pixels)
	 * @return lengte
	 */
	public int getLengte()
	{	return lengte;
	}		
	
	/**
	 * true: teken de lijn waarlangs de sliderknoppen bewegen 
	 * @param b true/false
	 */
	public void zetShowLine(boolean b)
	{	showLine = b;
	}	

	/**
	 * enable/disable de linker sliderknop
	 * @param b true/false
	 */
	public void zetLinksEnabled(boolean b)
	{	linksEnabled = b;
	}
	/**
	 * enable/disable de rechter sliderknop
	 * @param b true/false
	 */
	public void zetRechtsEnabled(boolean b)
	{	rechtsEnabled = b;
	}

	/**
	 * zet de kleur van de linker sliderknop
	 * @param c nieuwe kleur
	 */
	public void zetKnopLinksColor(CssColor c)
	{	knopLinksColor = c;
	}

	/**
	 * zet de kleur van de rechter sliderknop
	 * @param c nieuwe kleur
	 */
	public void zetKnopRechtsColor(CssColor c)
	{	knopRechtsColor = c;
	}
		
	public void paint()
	{
		tekenSlider(sliderContext2d);
	}
	
	
	/**
	 * teken de slider, teken de knoppen alleen als deze enabled zijn
	 * @param g Context2d om te tekenen
	 */
	public void tekenSlider(Context2d g)
	{	
		g.setStrokeStyle(CssColor.make(0,0,0));
		
		if (showLine)
		{	
			g.beginPath();
			g.moveTo(xPos + 5, yPos + 5);
			g.lineTo(xPos + lengte + 5, yPos + 5);
			g.stroke();
	
		}
		
		if (linksEnabled)
		{
			g.setFillStyle(knopLinksColor);
			
			g.beginPath();
            g.arc(xPos + 5 + standLinks, yPos + 5, 3, 0, 2 * Math.PI);
       	 	g.fill();
			
			g.setStrokeStyle(CssColor.make(0,0,0));
			g.beginPath();
            g.arc(xPos + 5 + standLinks, yPos + 5, 3, 0, 2 * Math.PI);
       	 	g.stroke();
		}
		
		if (rechtsEnabled)
		{
			g.setFillStyle(knopRechtsColor);
			
			g.beginPath();
            g.arc(xPos + 5 + standRechts, yPos + 5, 3, 0, 2 * Math.PI);
       	 	g.fill();
			
			g.setStrokeStyle(CssColor.make(0,0,0));
			g.beginPath();
            g.arc(xPos + 5 + standRechts, yPos + 5, 3, 0, 2 * Math.PI);
       	 	g.stroke();
		}	
	
	}
	
	/**
	 * geef de stand van de linker sliderknop
	 * @return standLinks
	 */
	public int geefStandLinks()
	{	return standLinks;
	}

	/**
	 * geef de stand van de rechter sliderknop
	 * @return standRechts
	 */
	public int geefStandRechts()
	{	return standRechts;
	}

	/**
	 * zet de stand van de linker sliderknop (tussen
	 * minimumLinks and maximumLinks), pas minimumRechts aan
	 * @param std nieuwe stand
	 */
	public void zetStandLinks(int std)
	{	if (std > maximumLinks)
			standLinks = maximumLinks;
		else if (std < minimumLinks)
			standLinks = minimumLinks;
		else 
			standLinks = std;
			
		minimumRechts = standLinks + pixDis;	
		
		paint();
		
	}

	/**
	 * zet de stand van de rechter sliderknop (tussen
	 * minimumRechts and maximumRechts), pas maximumLinks aan
	 * @param std nieuwe stand
	 */
	public void zetStandRechts(int std)
	{	if (std > maximumRechts)
			standRechts = maximumRechts;
		else if (std < minimumRechts)
			standRechts = minimumRechts;
		else 
			standRechts = std;
			
		maximumLinks = standRechts - pixDis;					
		
		paint();
	}

	/**
	 * zet het maximum voor de linker sliderknop
	 * @param max nieuw maximum
	 */
	public void zetMaximumLinks(int max)
	{	maximumLinks = max;
	}

	/**
	 * zet het minimum voor de rechter sliderknop
	 * @param min nieuw minimum
	 */
	public void zetMinimumRechts(int min)
	{	minimumRechts = min - 2;
	}
	
	/**
	 * actie bij MouseDown/TouchStart: kijk of de linker- of rechterknop
	 * van de lider aangeklikt is en fixeer de x-positie van de klik
	 * @param eventX x-coordinaat MouseStart/TouchDown Event
	 * @param eventY y-coordinaat MouseStart/TouchDown Event
	 */
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		raakLinks = linksEnabled && 
					(new Rectangle(xPos + standLinks - 3, yPos, 16, 13)).contains(eventX, eventY);
		
		raakRechts = rechtsEnabled && 
					 (new Rectangle(xPos + standRechts - 3, yPos, 16, 13)).contains(eventX, eventY);
		
		muisStartX = eventX;
		
	}
	
	/**
	 * actie bij MouseMove/TouchMove: verschuif de linker- of rechter
	 * sliderknop, en verwittig de klasse owner
	 * @param eventX x-coordinaat MouseMove/TouchMove Event
	 * @param eventY y-coordinaat MouseMove/TouchMove Event
	 */
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		if (!raakLinks && linksEnabled && 
			(new Rectangle(xPos + standLinks - 3, yPos, 16, 13)).contains(eventX, eventY))
		{	raakLinks = true;
			muisStartX = eventX;
		}
		
		if (!raakRechts && rechtsEnabled && 
			(new Rectangle(xPos + standRechts - 3, yPos, 16, 13)).contains(eventX, eventY))
		{	raakRechts = true;
			muisStartX = eventX;
		}
		
		// verander stand linker knop en minimumRechts
		if (raakLinks)
		{	int x = eventX;
			int dx = x - muisStartX;
			standLinks = standLinks + dx;
			if (standLinks > maximumLinks) 
			{	standLinks = maximumLinks;
			}
			else if (standLinks < minimumLinks) 
			{	standLinks = minimumLinks;
			}
			minimumRechts = standLinks + pixDis;
			
			if (x < 5 || x > lengte + 20)
			{	raakLinks = false;
			}
			
			owner.processTweeGrenzenSlider(true);
			
			muisStartX = x;
		}

		// verander stand rechter knop maximumLinks
		if (raakRechts)
		{	int x = eventX;
			int dx = x - muisStartX;
			standRechts = standRechts + dx;
			if (standRechts > maximumRechts) 
			{	standRechts = maximumRechts;
			}
			else if (standRechts < minimumRechts) 
			{	standRechts = minimumRechts;
			}
			
			maximumLinks = standRechts - pixDis;		
			
			if (x < 5 || x > lengte + 20)
			{	raakRechts = false;
			}
			
			owner.processTweeGrenzenSlider(false);
			
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
