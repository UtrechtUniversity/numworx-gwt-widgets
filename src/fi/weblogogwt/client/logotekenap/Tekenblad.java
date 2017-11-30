package fi.weblogogwt.client.logotekenap;

import fi.weblogogwt.client.Polygon;
import fi.weblogogwt.client.WebLogoGWT;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.dom.client.Style;

/**
 * class implementing all drawing; note that the superclass Uitvoerblad is actually not needed (see there); <br>
 * all drawing is so-called turtle graphics, that is, there is a virtual turtle (the cursor when tracing),
 * which at any time is at a certain position and points its node in a certain direction;
 * the turtle holds a pen, which can be put down on the "paper" or taken off the "paper"; the move-commands voorruit(pixels)
 * and stap(x-pixels,y-pixels) are relative to the to the turtle (cursor) position; in the same way the turn-commands
 * links(degrees) and rechts(degrees) are relative to the to the turtle (cursor) direction;        
 *
 */
public class Tekenblad extends Uitvoerblad
{
	/**
	 * width and height of drawing area
	 */
	private int breedte,hoogte;
	
	/**
	 * current position of turtle (cursor)
	 */
	private Punt beginpunt;
	/**
	 * new position of turtle (cursor) after move commands
	 */
	private Punt eindpunt;
	/**
	 * center of drawing area at initialization
	 */
	private Punt startpunt;
	
	/**
	 * Polygon use by methods vulAan() and vulUit()
	 */
  	private Polygon veelvlak;
  	/**
  	 * instance of Context2d for drawing
  	 */
  	private Context2d gIm;
  	/**
  	 * matrix for turtle graphics, see class Matrix2D
  	 */
	public Matrix2D mat;  

	private WebLogoGWT eigenaar;
	/**
	 * flagg for drawing/not drawing
	 */
	private boolean pen;
	/**
	 * flagg for saving points into a Polygon, to be filled in a later stage<br>
	 * see methods vulAan() and vulUit()
	 */
	private boolean vul;
	/**'
	 * the pen color
	 */
  	private CssColor penkleur;
  	/**
  	 * the fill color
  	 */
  	private CssColor vulkleur;
  	/**
  	 * the background color
  	 */
  	private CssColor achtergrondkleur;
  	/**
  	 * initial print x-position
  	 */
	public static int consoleStartX = 10;
  	/**
  	 * initial print y-position
  	 */
	public static int consoleStartY = 16;
	/**
	 * current print x-position
	 */
	private int consoleX = consoleStartX;
	/**
	 * current print y-position
	 */
	private int consoleY = consoleStartY;

	/**
	 * Canvas for drawing upon
	 */
	Canvas tekenbladCanvas;
		
	/**
	 * constructor
	 * @param ap instance of WebLogoGWT 
	 * @param b width
	 * @param h height
	 */
	public Tekenblad(WebLogoGWT ap, int b, int h)
	{	
		breedte = b;
		hoogte = h;
		
		tekenbladCanvas = Canvas.createIfSupported();
		tekenbladCanvas.setWidth(b + "px");
		tekenbladCanvas.setHeight(h + "px");
		tekenbladCanvas.setCoordinateSpaceWidth(b);
		tekenbladCanvas.setCoordinateSpaceHeight(h);
		add(tekenbladCanvas);
		setWidgetLeftWidth(tekenbladCanvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		setWidgetTopHeight(tekenbladCanvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);

		// white;
		achtergrondkleur = CssColor.make(255,255,255);
		veelvlak = new Polygon();
		eigenaar = ap;
		mat = new Matrix2D();					
		double startschaal = Math.min((double)breedte/500,(double)hoogte/500);
		mat.initialiseer(0,startschaal);	
		startpunt = new Punt(breedte/2,hoogte/2);
		
	}
	
	/**
	 * set the Context2d of the drawing Canvas
	 */
	public void initContext2d()
	{
		gIm = tekenbladCanvas.getContext2d();
	}
	
	/**
	 * getter for the drawing Canvas
	 */
	public Canvas getCanvas()
	{
		return tekenbladCanvas;
	}
	
 	/**
 	 * initialize the turtle, draw background, and output a finished drawing 
 	 * (no tracing) or a partial drawing (tracing)
	 * @param cursor true when tracing, draw cursor on top of drawing.
 	 */
 	public void initializeDrawing(boolean cursor)
  	{ 	
  		// possible?
  		if (startpunt == null) 
  		{	return;
  		}
  		beginpunt = new Punt(startpunt);
    	eindpunt = new Punt(beginpunt);
    	mat.initialiseer();
    	// background white
    	achtergrondkleur = CssColor.make(255,255,255);
    	gIm.setFillStyle(achtergrondkleur);
    	gIm.fillRect(0, 0, breedte, hoogte);
    	consoleX = consoleStartX;
    	consoleY = consoleStartY;
    	pen = true;
    	penkleur = CssColor.make(0,0,0);
    	gIm.setStrokeStyle(penkleur);
    	penAan();
		vul = false;
    	vulkleur = CssColor.make(0,0,0);
    	if (cursor)
    	{	eigenaar.trb.traceProgram();
    		tekenCursor();
    	}
    	else
    		eigenaar.trb.executeProgram();
	}
  	
	/**
	 * draw a cursor at the current turtle position pointing in the
	 * turtle direction
	 */
	void tekenCursor()
	{	Polygon cursor = new Polygon();
		Punt p;
		p = mat.geefVolgendPunt(beginpunt,10,0);
		cursor.addPoint((int)p.x,(int)p.y);
		p = mat.geefVolgendPunt(p,-10,-10);
		cursor.addPoint((int)p.x,(int)p.y);
		p = mat.geefVolgendPunt(p,-10,10);
		cursor.addPoint((int)p.x,(int)p.y);
		// yellow filling
		gIm.setFillStyle(CssColor.make(255,255,0));
		
		gIm.beginPath();
		gIm.moveTo(cursor.geefPuntXD(0), cursor.geefPuntYD(0));
		for (int pCnt = 1; pCnt < cursor.geefAantalPunten(); pCnt++)
		{
			gIm.lineTo(cursor.geefPuntXD(pCnt), cursor.geefPuntYD(pCnt));
		}
		gIm.lineTo(cursor.geefPuntXD(0), cursor.geefPuntYD(0));
		gIm.fill();
		
		// outline blue
		gIm.setStrokeStyle(CssColor.make(0,0,255));
		
		gIm.beginPath();
		gIm.moveTo(cursor.geefPuntXD(0), cursor.geefPuntYD(0));
		for (int pCnt = 1; pCnt < cursor.geefAantalPunten(); pCnt++)
		{
			gIm.lineTo(cursor.geefPuntXD(pCnt), cursor.geefPuntYD(pCnt));
		}
		gIm.lineTo(cursor.geefPuntXD(0), cursor.geefPuntYD(0));
		gIm.stroke();
	}

	/**
	 * Outputs a finished drawing to the display, with or without the cursor (tracing)
	 * redundant, this is just initializeDrawing 
	 * @param cursor	true when tracing, draw cursor on top of drawing.
	 */
	public void paintDrawing(boolean cursor)
	{	
		initializeDrawing(cursor);
	}
	
 	/**
 	 * move to a new position calculated as new = current + mat * (dx,dy)T; 
 	 * note how this works: the direction of the cursor defines the positive y-axis
 	 * of a coordinate system; in this coordinater system change the position by (dx,dy) 
 	 * if pen == true, connect old and new position by a line in penkleur;
 	 * if vul == true, add the old position to the Polygon veelvlak  
 	 * @param dx x-change
 	 * @param dy y-change
 	 */
	public void naarVolgendPunt(double dx,double dy)
	{	eindpunt = mat.geefVolgendPunt(beginpunt,dx,dy);
		gIm.setStrokeStyle(penkleur);
		if (pen)
		{	gIm.beginPath();
			gIm.moveTo((int)Math.rint(beginpunt.x),(int)Math.rint(beginpunt.y));
			gIm.lineTo((int)Math.rint(eindpunt.x),(int)Math.rint(eindpunt.y));
			gIm.stroke();
		}
		if (vul) 
			veelvlak.addPoint((int)Math.rint(beginpunt.x),(int)Math.rint(beginpunt.y));
		beginpunt.x = eindpunt.x;
		beginpunt.y = eindpunt.y;
	}
	
	/**
	 * draw the Polygon veelvlak; <br>
	 * NOTE: buggfix Huub: if the vulAan(--) statement is incorrect, it is skipped at
	 * execution; in this case, at the call to vulUit() veelvlak == null or 
	 * contains zero points
	 */
	void tekenPolygon()
	{	if (veelvlak == null)
		{	return;
		}
		if (veelvlak.geefAantalPunten() == 0)
		{	return;
		}
		// fill veelvlak 
		gIm.setFillStyle(vulkleur);
		gIm.beginPath();
		gIm.moveTo(veelvlak.geefPuntXD(0), veelvlak.geefPuntYD(0));
		for (int pCnt = 1; pCnt < veelvlak.geefAantalPunten(); pCnt++)
		{	gIm.lineTo(veelvlak.geefPuntXD(pCnt), veelvlak.geefPuntYD(pCnt));
		}
		gIm.lineTo(veelvlak.geefPuntXD(0), veelvlak.geefPuntYD(0));
		gIm.fill();
		// outline veelvlak in penkleur if pen == true
		if ( pen ) 
		{	gIm.setStrokeStyle(penkleur);
			gIm.beginPath();
			gIm.moveTo(veelvlak.geefPuntXD(0), veelvlak.geefPuntYD(0));
			for (int pCnt = 1; pCnt < veelvlak.geefAantalPunten(); pCnt++)
			{	gIm.lineTo(veelvlak.geefPuntXD(pCnt), veelvlak.geefPuntYD(pCnt));
			}
			gIm.lineTo(veelvlak.geefPuntXD(0), veelvlak.geefPuntYD(0));
			gIm.stroke();
		}
	}

	/**
	 * fill the drawing area with color c
	 * @param c the fill color
	 */
  	void vulBlad(CssColor c)
  	{	gIm.setFillStyle(c);
  		gIm.fillRect(1, 1, breedte-2, hoogte-2);
  	}

  	/**
	 * fill the drawing area with the RGB-color given by the integers (r,g,b) 
  	 */
	public void vulBlad(int r, int g, int b)
	{	vulBlad(CssColor.make(r,g,b));
	}

  	/**
  	 * turn the cursor left over angle dHoek (degrees)
  	 */
	public void links(double dHoek)
	{	mat.draai(dHoek);
	}
	
	/**
	 * turn the cursor rigth over angle dhoek (degrees)
	 */
  	public void rechts(double dHoek)
	{	mat.draai(-dHoek);		
	}
  	
  	/**
  	 * move forward dy in the direction of the cursor; 
  	 * see method naarVolgendPunt for pen and vul action
  	 */
	public void vooruit(double dy)
	{	naarVolgendPunt(0,-dy);	
	}
	
	/**
	 * move (dx,dy) relative to the direction of the cursor
 	 * note how this works: the direction of the cursor defines the positive y-axis
 	 * of a coordinate system; in this coordinater system change the position by (dx,dy) 
	 */
	public void stap(double dx,double dy)
	{	naarVolgendPunt(dx,-dy);
	}
	
	/**
	 * activate the pen
	 */
	public void penAan()
	{	pen = true;							
	}
	
	/**
	 * activate the pen with the color name given by a String
	 * @param kl name of the color
	 */
	public void penAan(String kl)
	{	pen = true;
		penkleur = maakKleur(kl);
		gIm.setStrokeStyle(penkleur);
	}

	/**
	 * activate the pern with RGB-color given by the integers (r,g,b)
	 */
	public void penAan(int r, int g, int b)
	{	pen = true;
		penkleur = CssColor.make(r,g,b); //new Color(r,g,b);
		gIm.setStrokeStyle(penkleur);
	}
	
	/**
	 * de-activate the pen
	 */
	public void penUit()
	{	pen = false;							
	}
	
	/**
	 * start vul, that is refresh the Polygon veelvlak and
	 * save all points passed in veelvlak; vulUit() fills
	 * the Polygon veelvlak with vulkleur 
	 */
	public void vulAan()
	{	vul = true;
		veelvlak = new Polygon();							
	}

	/**
	 * start vul, that is refresh the Polygon veelvlak and
	 * save all points through which the cursor passes in veelvlak; 
	 * set vulkleur to the color given by the name kl;
	 * vulUit() fills the Polygon veelvlak with vulkleur 
	 * @param kl name of vulkleur
	 */
	public void vulAan(String kl)
	{	vul = true;	
		vulkleur = maakKleur(kl);
		veelvlak = new Polygon();				
	}
	
	/**
	 * start vul, that is refresh the Polygon veelvlak and
	 * save all points through which the cursor passes in veelvlak; 
	 * set vulkleur to the RGB-color given by the integers (r,g,b)l;
	 * vulUit() fills the Polygon veelvlak with vulkleur 
	 */
	public void vulAan(int r, int g, int b)
	{	vul = true;	
		vulkleur = CssColor.make(r,g,b); //new Color(r,g,b);
		veelvlak = new Polygon();	
	}
	
	/**
	 * de-activate vul and fill the Polygon veelvlak 
	 * with vulkeur
	 */
	public void vulUit()
	{	tekenPolygon();
		vul = false;							
	}

	/**
	 * set the background color to kl
	 * @param kl String with name of color
	 */
	void achtergrondkleur(String kl)
	{	
		achtergrondkleur = maakKleur(kl);
	}
	
	/**
	 * set the background color giving RGB-values
	 * @param r red value
	 * @param g green value
	 * @param b blue value
	 */
	void achtergrondkleur(int r, int g, int b)
	{	
		achtergrondkleur = CssColor.make(r,g,b); 
	}
	
	/**
	 * print String s at the current console position
	 * do not update the console position
	 * @param s String to be printed
	 */
	public void printConsole(String s)
	{	
		gIm.setFont(WebLogoGWT.fontString);
		gIm.setFillStyle(penkleur);
		gIm.fillText(s, consoleX, consoleY);
	}

	/**
	 * print String s at the current console position
	 * and start a new line
	 */
	public void printl(String s)
	{
		printConsole(s);
		consoleX = consoleStartX;
		consoleY = consoleY+16;
	}

	/**
	 * print String s at the current console position
	 * and update the console position
	 */
	public void print(String s)
	{
		printConsole(s);
		TextMetrics tm = gIm.measureText(s);
		int width = (int) Math.round(tm.getWidth());
		consoleX = consoleX + width; 
	}

	/**
	 * get the last filled veelvlak
	 * @return veelvlak
	 */
	Polygon geefVlak()				
	{	
		return veelvlak;
	}

	/**
	 * given the name (in Dutch or English) of a color, create that color
	 * @param s color name (in Dutch or English)
	 * @return corresponding color
	 */
	private CssColor maakKleur(String s)
	{
		if (s.equals("rood"))
			return CssColor.make(255, 0, 0);
		else if (s.equals("groen"))
			return CssColor.make(0, 255, 0);
		else if (s.equals("blauw"))
			return CssColor.make(0, 0, 255);
		else if (s.equals("geel"))
			return CssColor.make(255, 255, 0);
		else if (s.equals("cyaan"))
			return CssColor.make(0, 255, 255);
		else if (s.equals("roze"))
			return CssColor.make(255, 20, 147);
		else if (s.equals("zwart"))
			return CssColor.make(0, 0, 0);
		else if (s.equals("grijs"))
			return CssColor.make(192, 192, 192);
		else if (s.equals("lichtgrijs"))
			return CssColor.make(220, 220, 220);
		else if (s.equals("magenta"))
			return CssColor.make(255, 0, 255);
		else if (s.equals("wit"))
			return CssColor.make(255, 255, 255);
		else if (s.equals("oranje"))
			return CssColor.make(255, 127, 0);

		else if (s.equals("red"))
			return CssColor.make(255, 0, 0);
		else if (s.equals("green"))
			return CssColor.make(0, 255, 0);
		else if (s.equals("blue"))
			return CssColor.make(0, 0, 255);
		else if (s.equals("yellow"))
			return CssColor.make(255, 255, 0);
		else if (s.equals("cyan"))
			return CssColor.make(0, 255, 255);
		else if (s.equals("pink"))
			return CssColor.make(255, 20, 147);
		else if (s.equals("black"))
			return CssColor.make(0, 0, 0);
		else if (s.equals("gray"))
			return CssColor.make(192, 192, 192);
		else if (s.equals("lightGray"))
			return CssColor.make(220, 220, 220);
		else if (s.equals("magenta"))
			return CssColor.make(255, 0, 255);
		else if (s.equals("white"))
			return CssColor.make(255, 255, 255);
		else if (s.equals("orange"))
			return CssColor.make(255, 127, 0);

		else
			return CssColor.make(0, 0, 0);
	}

}
