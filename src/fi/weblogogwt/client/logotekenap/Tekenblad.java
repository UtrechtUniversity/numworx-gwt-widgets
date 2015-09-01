package fi.weblogogwt.client.logotekenap;

//import javax.swing.JPanel;

//import java.awt.Color;
//import java.awt.Font;
//import java.awt.FontMetrics;
//import java.awt.Graphics;
//import java.awt.Image;
//import java.awt.Polygon;

import fi.weblogogwt.client.Polygon;
import fi.weblogogwt.client.WebLogoGWT;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.dom.client.Style;

public class Tekenblad extends Uitvoerblad
{
	private int breedte,hoogte;
	private Punt beginpunt,eindpunt,startpunt;
  	private Polygon veelvlak;
//GWT?  	
  	//private Image im ;
  	//private Graphics gIm ;
  	private Context2d gIm ;
	public Matrix2D mat;  
	//private JavaLogoInteractiePanel eigenaar;
	private WebLogoGWT eigenaar;
	private boolean pen, vul;
  	private CssColor penkleur, vulkleur, achtergrondkleur;
	public static int consoleStartX = 10;
	public static int consoleStartY = 16;
	private int consoleX = consoleStartX;
	private int consoleY = consoleStartY;
	private boolean veranderd = true;
	
	Canvas tekenbladCanvas;
		
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

		//achtergrondkleur = Color.white;
		achtergrondkleur = CssColor.make(255,255,255);
		veelvlak = new Polygon();
		eigenaar = ap;
		mat = new Matrix2D();					// zorgt voor de tekenrichting
		
		double startschaal = Math.min((double)breedte/500,(double)hoogte/500);
		mat.initialiseer(0,startschaal);	
		startpunt = new Punt(breedte/2,hoogte/2);
		
	}
	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door het Tekenblad: om de image te initialiseren en
	//op het scherm te zetten. "paint()" wordt alleen bij de eerste keer tekenen gebruikt, daarna 
	//zorgt "tekenOpnieuw()" of "tekenErbij()" hiervoor. "TekenOpImage()" zorgt voor het vullen 
	//van de image, metbehulp van het door de leerlingen geimplementeerde "tekenprogramma()",
	//en wordt zowel door "paint()" als door "tekenOpImage()" gebruikt
	//-------------------------------------------------------------------------------------------
  	//public void paintComponent(Graphics g)
	public void paintComponent(Context2d g)
  	{ 	
		//if ( im==null || veranderd )
		//{	
			//breedte = getSize().width;
			//hoogte = getSize().height;	
			double startschaal = Math.min((double)breedte/500,(double)hoogte/500);
			mat.initialiseer(0,startschaal);	
			startpunt = new Punt(breedte/2,hoogte/2);
 			//im = createImage(breedte,hoogte);
  			//gIm = im.getGraphics();
  			initializeDrawing(eigenaar.trb.isTraceAan());
			//veranderd = false;
		//}
    	//g.drawImage(im, 0, 0, null);
  	}
  	
	public void initContext2d()
	{
		gIm = tekenbladCanvas.getContext2d();
	}
	
	public Canvas getCanvas()
	{
		return tekenbladCanvas;
	}
	
 	public void repaint()
	{	//veranderd = true;
		//super.repaint();
 		paintComponent(gIm);
	}
 	
	//-------------------------------------------------------------------------------------------
	// PBgv, 2015: deze methoden worden gebruikt door UI-componenten om de tekening te beginnen
 	// en na executie van het tekenalgoritme op het beeldscherm te zetten
	//-------------------------------------------------------------------------------------------

 	/**
 	 * Initializes a drawing. To be called before starting the execution of a 'tekenalgoritme'.
 	 */
	@Override
 	public void initializeDrawing(boolean cursor)
  	{ 	
  		// wat is de reden van deze voorwaarde???
  		if ( startpunt==null) return;
  		beginpunt = new Punt(startpunt);
    	eindpunt = new Punt(beginpunt);
    	mat.initialiseer();
    	//achtergrondkleur = Color.WHITE;
    	achtergrondkleur = CssColor.make(255,255,255);
	  	//gIm.setColor(achtergrondkleur);
    	gIm.setFillStyle(achtergrondkleur);
    	gIm.fillRect(0, 0, breedte, hoogte);
    	//gIm.setColor(Color.gray);
    	gIm.setStrokeStyle(CssColor.make(192, 192, 192));
    	//gIm.drawRect(0, 0, breedte-1, hoogte-1);
    	gIm.strokeRect(0, 0, breedte-1, hoogte-1);
    	consoleX = consoleStartX;
    	consoleY = consoleStartY;
    	//penAan(0,0,0);
    	pen = true;
    	//penkleur = Color.black;
    	penkleur = CssColor.make(0,0,0);
    	//gIm.setColor(penkleur);
    	gIm.setStrokeStyle(penkleur);
    	penAan();
		vul = false;
    	//vulkleur = Color.black;
    	vulkleur = CssColor.make(0,0,0);
    	if (cursor)
    	{	eigenaar.trb.traceProgram();
    		tekenCursor();
    	}
    	else
    		eigenaar.trb.executeProgram();
    	
    		
	}
  	
	void tekenCursor()
	{	Polygon cursor = new Polygon();
		Punt p;
		p = mat.geefVolgendPunt(beginpunt,10,0);
		cursor.addPoint((int)p.x,(int)p.y);
		p = mat.geefVolgendPunt(p,-10,-10);
		cursor.addPoint((int)p.x,(int)p.y);
		p = mat.geefVolgendPunt(p,-10,10);
		cursor.addPoint((int)p.x,(int)p.y);
		//gIm.setColor(new Color(255,255,0));
		gIm.setFillStyle(CssColor.make(255,255,0));
		//gIm.fillPolygon(cursor);
		
		gIm.beginPath();
		gIm.moveTo(cursor.geefPuntXD(0), cursor.geefPuntYD(0));
		for (int pCnt = 1; pCnt < cursor.geefAantalPunten(); pCnt++)
		{
			gIm.lineTo(cursor.geefPuntXD(pCnt), cursor.geefPuntYD(pCnt));
		}
		gIm.lineTo(cursor.geefPuntXD(0), cursor.geefPuntYD(0));
		gIm.fill();
		
		//gIm.setColor(new Color(0,0,255));
		gIm.setStrokeStyle(CssColor.make(0,0,255));
		//gIm.drawPolygon(cursor);
		
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
	 * 
	 * @param cursor	true when tracing, draw cursor on top of drawing.
	 */
	@Override
	public void paintDrawing(boolean cursor)
	{	
		initializeDrawing(cursor);
//GWT?		
		//Graphics g = getGraphics();
		//if(g!=null)
		//	g.drawImage(im, 0, 0, null);
	}
	
 	/**
 	 * TODO: Deze methode zorgde eerst voor execute and paint. Is nu obsolete. Er staat nog een 
 	 * dummy om compile errors tijdens conversie te voorkomen. 
 	 * Peter: de 'most likely candidate' om deze methode te vervangen is Tracebeheerder.executeProgram(),
 	 * die runt het programma zonder trace. Maar ik ken de widget communictions niet...
 	 */
 	public void tekenOpnieuw()
	{	
	}
 	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door het Tekenblad om de lijnen en vlakken te tekenen
	//-------------------------------------------------------------------------------------------
 	
	public void naarVolgendPunt(double dx,double dy)
	{	eindpunt = mat.geefVolgendPunt(beginpunt,dx,dy);
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
		if(pen)
		{	//gIm.drawLine((int)Math.rint(beginpunt.x),(int)Math.rint(beginpunt.y),
			//	         (int)Math.rint(eindpunt.x),(int)Math.rint(eindpunt.y));
			gIm.beginPath();
			gIm.moveTo((int)Math.rint(beginpunt.x),(int)Math.rint(beginpunt.y));
			gIm.lineTo((int)Math.rint(eindpunt.x),(int)Math.rint(eindpunt.y));
			gIm.stroke();
			
		
		}
		if(vul) 
			veelvlak.addPoint((int)Math.rint(beginpunt.x),(int)Math.rint(beginpunt.y));
		beginpunt.x = eindpunt.x;
		beginpunt.y = eindpunt.y;
	}
	
	void tekenPolygon()
	{	//gIm.setColor(vulkleur);
		gIm.setFillStyle(vulkleur);
		//gIm.fillPolygon(veelvlak);
		
		gIm.beginPath();
		gIm.moveTo(veelvlak.geefPuntXD(0), veelvlak.geefPuntYD(0));
		for (int pCnt = 1; pCnt < veelvlak.geefAantalPunten(); pCnt++)
		{
			gIm.lineTo(veelvlak.geefPuntXD(pCnt), veelvlak.geefPuntYD(pCnt));
		}
		gIm.lineTo(veelvlak.geefPuntXD(0), veelvlak.geefPuntYD(0));
		gIm.fill();
		
		if ( pen ) 
		{
			//gIm.setColor(penkleur);
			gIm.setStrokeStyle(penkleur);
			//gIm.drawPolygon(veelvlak);

			gIm.beginPath();
			gIm.moveTo(veelvlak.geefPuntXD(0), veelvlak.geefPuntYD(0));
			for (int pCnt = 1; pCnt < veelvlak.geefAantalPunten(); pCnt++)
			{
				gIm.lineTo(veelvlak.geefPuntXD(pCnt), veelvlak.geefPuntYD(pCnt));
			}
			gIm.lineTo(veelvlak.geefPuntXD(0), veelvlak.geefPuntYD(0));
			gIm.stroke();

			
		}
	}
  	void vulBlad(CssColor c)
  	{
  		//gIm.setColor(c);
  		gIm.setFillStyle(c);
  		gIm.fillRect(1, 1, breedte-2, hoogte-2);
  	}
	
	@Override
	public void links(double dHoek)
	{	
		mat.draai(dHoek);
	}
	
	@Override
  	public void rechts(double dHoek)
	{	
  		mat.draai(-dHoek);		
	}
  	
	@Override
	public void vooruit(double dy)
	{	
		naarVolgendPunt(0,-dy);	
	}
	
	public void stapy(double dy)
	{	
		naarVolgendPunt(0,-dy);		
	}
	
	public void stapx(double dx)
	{	
		naarVolgendPunt(dx,0);		
	}
	
	@Override
	public void stap(double dx,double dy)
	{	
		naarVolgendPunt(dx,-dy);
	}
	
	public void penAan()
	{	
		pen = true;							
	}
	
	public void penAan(String kl)
	{	
		pen = true;
		penkleur = maakKleur(kl);
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
	}
	
	@Override
	public void penAan(int r, int g, int b)
	{	
		pen = true;
		penkleur = CssColor.make(r,g,b); //new Color(r,g,b);
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
	}
	
	@Override
	public void penUit()
	{	
		pen = false;							
	}
	
	public void vulAan()
	{	
		vul = true;
		veelvlak = new Polygon();							
	}
	
	public void vulAan(String kl)
	{	
		vul = true;	
		vulkleur = maakKleur(kl);
		veelvlak = new Polygon();				
	}
	
	@Override
	public void vulAan(int r, int g, int b)
	{	
		vul = true;	
		vulkleur = CssColor.make(r,g,b); //new Color(r,g,b);
		veelvlak = new Polygon();	
	}
	
	@Override
	public void vulUit()
	{	
		tekenPolygon();
		vul = false;							
	}
	
	@Override
	public void vulBlad(int r, int g, int b)
	{
		//vulBlad(new Color(r,g,b));
		vulBlad(CssColor.make(r,g,b));
	}

	void achtergrondkleur(String kl)
	{	
		achtergrondkleur = maakKleur(kl);
	}
	
	void achtergrondkleur(int r, int g, int b)
	{	
		achtergrondkleur = CssColor.make(r,g,b); //new Color(r,g,b);
	}
	
	public void printConsole(String s)
	{	
		//gIm.setFont(JavaLogoWeb.defaultfont);
		gIm.setFont(WebLogoGWT.fontString);
		gIm.setFillStyle(CssColor.make(0,0,0));
		//gIm.drawString(s, consoleX, consoleY);
		gIm.fillText(s, consoleX, consoleY);
	}
	
	@Override
	public void printl(String s)
	{
		printConsole(s);
		consoleX = consoleStartX;
		consoleY = consoleY+16;
	}

	@Override
	public void print(String s)
	{
		printConsole(s);
		//FontMetrics fm = getFontMetrics(JavaLogoWeb.defaultfont);
		TextMetrics tm = gIm.measureText(s);
		int width = (int) Math.round(tm.getWidth());
		consoleX = consoleX + width; //fm.stringWidth(s);
	}

	void schrijf(String s, String fString)
	{	
		gIm.setFont(fString);
		gIm.setFillStyle(CssColor.make(0,0,0));
		//gIm.drawString(s, (int)beginpunt.x, (int)beginpunt.y);
		gIm.fillText(s, (int)beginpunt.x, (int)beginpunt.y);
	}
	
	Polygon geefVlak()							// geeft de laatst getekende Polygon
	{	
		return veelvlak;
	}
	
 	//-------------------------------------------------------------------------------------------
	//deze methode wordt gebruikt een kleur in de vorm van een string om te zetten in een Color
	//-------------------------------------------------------------------------------------------
	private CssColor maakKleur(String s)
	{	if (s.equals("rood")) return CssColor.make(255,0,0);
		else if ( s.equals("groen")) return CssColor.make(0,255,0);
		else if ( s.equals("blauw")) return CssColor.make(0,0,255);
		else if ( s.equals("geel")) return CssColor.make(255, 255, 0);
		else if ( s.equals("cyaan")) return CssColor.make(0, 255, 255);
		else if ( s.equals("roze")) return CssColor.make(255,20,147);
		else if ( s.equals("zwart")) return CssColor.make(0,0,0);
		else if ( s.equals("grijs")) return CssColor.make(192, 192, 192);
		else if ( s.equals("lichtgrijs")) return CssColor.make(220, 220, 220);
		else if ( s.equals("magenta")) return CssColor.make(255, 0, 255);
		else if ( s.equals("wit")) return CssColor.make(255,255,255);
		else if ( s.equals("oranje")) return CssColor.make(255, 127, 0);
	
		else if ( s.equals("red")) return CssColor.make(255,0,0);
		else if ( s.equals("green")) return CssColor.make(0,255,0);
		else if ( s.equals("blue")) return CssColor.make(0,0,255);
		else if ( s.equals("yellow")) return CssColor.make(255, 255, 0);
		else if ( s.equals("cyan")) return CssColor.make(0, 255, 255);
		else if ( s.equals("pink")) return CssColor.make(255,20,147);
		else if ( s.equals("black")) return CssColor.make(0,0,0);
		else if ( s.equals("gray")) return CssColor.make(192, 192, 192);
		else if ( s.equals("lightGray")) return CssColor.make(220, 220, 220);
		else if ( s.equals("magenta")) return CssColor.make(255, 0, 255);
		else if ( s.equals("white")) return CssColor.make(255,255,255);
		else if ( s.equals("orange")) return CssColor.make(255, 127, 0);
	
		else return CssColor.make(0,0,0);
	}

}
