package fi.weblogo3dgwt.client.logotekenap3d;

//import java.awt.*;
//import java.awt.event.*;
//import javax.swing.JPanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import fi.weblogo3dgwt.client.UF;


public class Tekenblad3D // extends JPanel //Canvas
{
	int breedte,hoogte;
	private Punt3D beginpunt,eindpunt,startpunt;
  	public Lichaam3D l;
  	//private Image im ;
  	//private Graphics gIm ;
  	private Context2d gIm ;
	public Matrix3D mat;  
	private TekenApplet3D eigenaar;
	private boolean pen, vul;
  	private CssColor penkleur,vulkleur,achtergrondkleur;
	public boolean bezigMetTekenen;
	
	Canvas tekenbladCanvas;
	
//	public static int consoleStartX = 10;
//	public static int consoleStartY = 16;
//	private int consoleX = consoleStartX;
//	private int consoleY = consoleStartY;
	
	CssColor achterkantKleur = CssColor.make(192,192,192);
	
	double hoekX, hoekY, beginx, beginy;
	
	boolean cursorAan = false;
	boolean transparant = false;
	//int transparantAlpha = 125;
	boolean draadFiguur = false;
	
	double zoomFactor = 1;
	
	public Tekenblad3D(TekenApplet3D ap, int w, int h)
	{	
		breedte = w;
		hoogte = h;
		
		tekenbladCanvas = Canvas.createIfSupported();
		tekenbladCanvas.setWidth(breedte + "px");
		tekenbladCanvas.setHeight(hoogte + "px");
		tekenbladCanvas.setCoordinateSpaceWidth(breedte);
		tekenbladCanvas.setCoordinateSpaceHeight(hoogte);

		//setLayout(null);
		achtergrondkleur = CssColor.make(255,255,255); //white;
		l = new Lichaam3D();
		eigenaar = ap;
		mat = new Matrix3D();
		
	}
	
	public void initContext2d()
	{
		gIm = tekenbladCanvas.getContext2d();
	}

	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door het Tekenblad: om de image te initialiseren en
	//op het scherm te zetten. "paint()" wordt alleen bij de eerste keer tekenen gebruikt, daarna 
	//zorgt "tekenOpnieuw()" of "tekenErbij()" hiervoor. "TekenOpImage()" zorgt voor het vullen 
	//van de image, metbehulp van het door de leerlingen geimplementeerde "tekenprogramma()",
	//en wordt zowel door "paint()" als door "tekenOpImage()" gebruikt
	//-------------------------------------------------------------------------------------------  	
	//public void paint(Graphics g)
	//public void paint(Context2d g)
	public void paintTekenblad()
  	{ 	
		
System.out.println("tb paint");

		//bezigMetTekenen = true;
		//if (im == null)
		//{	breedte = getSize().width;
		//	hoogte = getSize().height;	
			double startschaal = Math.min((double)breedte/500,(double)hoogte/500);
			mat.initialiseer(0,0,0,startschaal);	
			startpunt = new Punt3D(breedte/2,hoogte/2,0);
			l.maakNulpunt(breedte/2,hoogte/2,0);
			//im = createImage(breedte,hoogte);
  			//gIm = im.getGraphics();
  			//initializeDrawing(eigenaar.eigenaar.trb.isTraceAan());
			gIm.setGlobalAlpha(1);
			tekenOpImage(eigenaar.eigenaar.trb.isTraceAan());
			if (transparant)
			{
				gIm.setGlobalAlpha(5e-1d);
				tekenOpImage(eigenaar.eigenaar.trb.isTraceAan());
			}
		//}
    	//g.drawImage(im, 0, 0, null);
		//bezigMetTekenen = false;
  	}
	  
 	/**
 	 * Initializes a drawing. To be called before starting the execution of a 'tekenalgoritme'.
 	 */
 	public void initializeDrawing(boolean cursor)
  	{ 	
 		
System.out.println("tb initializeDrawing");

  		// wat is de reden van deze voorwaarde???
  		if (startpunt == null) 
  		{	
System.out.println("tb initializeDrawing startpunt == null");  			
  			return;
  		
  		}
  		
  		
  		beginpunt = new Punt3D(startpunt);
    	eindpunt = new Punt3D(beginpunt);
    	mat.initialiseer();
    	//mat.initialiseer();
    	achtergrondkleur = maakKleur("wit");
	  	//gIm.setColor(achtergrondkleur);
	  	gIm.setFillStyle(achtergrondkleur);
    	gIm.fillRect(0, 0, breedte, hoogte);
    	//gIm.setColor(Color.gray);
    	gIm.setStrokeStyle(maakKleur("grijs"));
    	//gIm.drawRect(0, 0, breedte-1, hoogte-1);
    	gIm.strokeRect(0, 0, breedte, hoogte);
//    	consoleX = consoleStartX;
//    	consoleY = consoleStartY;
    	//penAan(0,0,0);
    	//pen = true;
    	pen = false;
    	penkleur = maakKleur("zwart");
    	//gIm.setColor(penkleur);
    	//penAan();
		vul = false;
    	vulkleur = maakKleur("zwart");
    	if (cursor)
    	{	eigenaar.eigenaar.trb.traceProgram();
    		tekenCursor();
    	}
    	else
    		eigenaar.eigenaar.trb.executeProgram();
    	
    		
	}
  	
	void tekenCursor()
	{	

		Punt3D[] cursorPunten = new Punt3D[4];
		Punt3D p = mat.geefVolgendPunt(beginpunt,25,0,0);
		cursorPunten[0] = p;
		p = mat.geefVolgendPunt(beginpunt,0,10,0);
		cursorPunten[1] = p;
		p = mat.geefVolgendPunt(beginpunt,-10,0,0);
		cursorPunten[2] = p;
		p = mat.geefVolgendPunt(beginpunt,0,-15,0);
		cursorPunten[3] = p;
				
		l.voegCursorToe(cursorPunten, CssColor.make(0,0,0), CssColor.make(255,255,0));
	}

	public void paint()
	{
		paintDrawing(cursorAan);
	}

	
	/**
	 * Outputs a finished drawing to the display, with or without the cursor (tracing)
	 * 
	 * @param cursor true when tracing, draw cursor on top of drawing.
	 */
	public void paintDrawing(boolean cursor)
	{	
//System.out.println("tb paintDrawing " + cursor);

		cursorAan = cursor;
		//initializeDrawing(cursor);
		gIm.setGlobalAlpha(1);
		tekenOpImage(cursor);
		if (transparant)
		{	gIm.setGlobalAlpha(5e-1d);
			tekenOpImage(cursor);
		}
		
// transparant? zie VerknippenGWT
		
		//Graphics g = getGraphics();
		//if(g!=null)
			//g.drawImage(im, 0, 0, null);
	}

  	public void tekenOpImage(boolean cursor)
  	{ 	
  		
//System.out.println("tb tekenOpImage " + cursor);


  		beginpunt = new Punt3D(startpunt);
    	eindpunt = new Punt3D(beginpunt);
    	mat.initialiseer();
    	mat.schaal(zoomFactor);
	  	//gIm.setColor(achtergrondkleur);
	  	gIm.setFillStyle(achtergrondkleur);
		gIm.fillRect(0, 0, breedte, hoogte);
    	//gIm.setColor(Color.gray);
    	gIm.setStrokeStyle(maakKleur("grijs"));
    	//gIm.drawRect(0, 0, breedte-1, hoogte-1);
    	gIm.strokeRect(0, 0, breedte-1, hoogte-1);
    	//penAan(0,0,0);
    	pen = false;
		vul = false;
		mat.xdraai(beginx+hoekX); 
		mat.ydraai(beginy+hoekY);
    	if (cursor)
    	{	eigenaar.eigenaar.trb.traceProgram();
    		tekenCursor();
    	}
    	else
    		eigenaar.eigenaar.trb.executeProgram();
    	//eigenaar.tekenprogramma();
		l.sorteer();

//System.out.println("polyg = " + l.aantalPolygonen);

		for (int i = 0; i < l.aantalPolygonen; i++)
		{
			
			
			if (l.vlakken[i].pol.aantalPunten > 0 && l.vlakken[i].normaal.z > 0)
			{	
//System.out.println("nz > 0");				
				
				double grijsfactor = 0.5*((-l.vlakken[i].normaal.x - l.vlakken[i].normaal.y + l.vlakken[i].normaal.z)/Math.sqrt(3)+1);
				if (grijsfactor < 0) 
					grijsfactor = 0;
				if (grijsfactor > 1)
					grijsfactor = 1;
				if (l.vlakken[i].vulkleur == null)
				{	l.vlakken[i].vulkleur = maakKleur("magenta");
//System.out.println("polyg = " + i + " vk = null");				
				}
/*				
				int roodwaarde = 50 + (int) (l.vlakken[i].vulkleur.getRed() * grijsfactor * 0.75);
				int groenwaarde = 50 + (int) (l.vlakken[i].vulkleur.getGreen() * grijsfactor * 0.75);
				int blauwwaarde = 50 + (int) (l.vlakken[i].vulkleur.getBlue() * grijsfactor *0.75);
				int alpha = 255;
				if (transparant && !l.vlakken[i].naam.equals("cursor"))
					alpha = transparantAlpha;
*/				
			    String vString = l.vlakken[i].vulkleur.toString().substring(4, l.vlakken[i].vulkleur.toString().length() - 1);
				String[] kleurenStr = StringUtils.split(vString,",");

				int fBlue =  Integer.parseInt(kleurenStr[2]);
				int fGreen = Integer.parseInt(kleurenStr[1]);
				int fRed =   Integer.parseInt(kleurenStr[0]);
				
				int roodwaarde = 50+(int)(fRed*grijsfactor*0.75);
				int groenwaarde = 50+(int)(fGreen*grijsfactor*0.75);
				int blauwwaarde = 50+(int)(fBlue*grijsfactor*0.75);

				if (l.vlakken[i].naam.equals("cursor"))
				{	//gIm.setColor(l.vlakken[i].vulkleur);
					gIm.setFillStyle(l.vlakken[i].vulkleur);
				}
				else			
				{	//gIm.setColor(new Color(roodwaarde,groenwaarde,blauwwaarde,alpha));
					gIm.setFillStyle(CssColor.make(roodwaarde,groenwaarde,blauwwaarde));
				
				}
				
				
				if (!draadFiguur || l.vlakken[i].naam.equals("cursor"))
				{	//gIm.fillPolygon(l.vlakken[i].pol);
					gIm.moveTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.beginPath();
					for (int k = 1; k < l.vlakken[i].pol.aantalPunten; k++)
					{	gIm.lineTo(l.vlakken[i].pol.puntenX[k], l.vlakken[i].pol.puntenY[k]);
					}
					gIm.lineTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.closePath();
					gIm.fill();

				}
				//gIm.setColor(l.vlakken[i].lijnkleur);
				gIm.setStrokeStyle(l.vlakken[i].lijnkleur);
				if (!l.vlakken[i].isLijn && (l.vlakken[i].isOmlijnd || draadFiguur))
				{	//gIm.setColor(l.vlakken[i].lijnkleur);
					gIm.setStrokeStyle(l.vlakken[i].lijnkleur);
					//gIm.drawPolygon(l.vlakken[i].pol);
					gIm.setStrokeStyle(l.vlakken[i].lijnkleur);
					//gIm.drawPolygon(l.vlakken[i].pol);
					gIm.moveTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.beginPath();
					for (int k = 1; k < l.vlakken[i].pol.aantalPunten; k++)
					{	gIm.lineTo(l.vlakken[i].pol.puntenX[k], l.vlakken[i].pol.puntenY[k]);
					}
					gIm.lineTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.closePath();
					gIm.stroke();

				}
				if (l.vlakken[i].isLijn)
				{	//gIm.setColor(l.vlakken[i].lijnkleur);
					gIm.setStrokeStyle(l.vlakken[i].lijnkleur);
					//gIm.drawPolygon(l.vlakken[i].pol);
					gIm.setStrokeStyle(l.vlakken[i].lijnkleur);
					//gIm.drawPolygon(l.vlakken[i].pol);
					gIm.moveTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.beginPath();
					for (int k = 1; k < l.vlakken[i].pol.aantalPunten; k++)
					{	gIm.lineTo(l.vlakken[i].pol.puntenX[k], l.vlakken[i].pol.puntenY[k]);
					}
					gIm.lineTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.closePath();
					gIm.stroke();

					penkleur = maakKleur("zwart");
				}
			}
// 			// toegevoegd Huub, achterkant wel tekenen in achterkantKleur
			else if (l.vlakken[i].pol.aantalPunten > 0 && l.vlakken[i].normaal.z <= 0) 
			{
				
//System.out.println("nz < 0");

				double grijsfactor = 0.5*((-l.vlakken[i].normaal.x - l.vlakken[i].normaal.y + l.vlakken[i].normaal.z)/Math.sqrt(3)+1);
				if (grijsfactor < 0) 
					grijsfactor = 0;
				if (grijsfactor > 1)
					grijsfactor = 1;

			    String vString = achterkantKleur.toString().substring(4, achterkantKleur.toString().length() - 1);
				String[] kleurenStr = StringUtils.split(vString,",");

				int fBlue =  Integer.parseInt(kleurenStr[2]);
				int fGreen = Integer.parseInt(kleurenStr[1]);
				int fRed =   Integer.parseInt(kleurenStr[0]);
				
				int roodwaarde = 50+(int)(fRed*grijsfactor*0.75);
				int groenwaarde = 50+(int)(fGreen*grijsfactor*0.75);
				int blauwwaarde = 50+(int)(fBlue*grijsfactor*0.75);
				
				if (l.vlakken[i].naam.equals("cursor"))
				{	//gIm.setColor(l.vlakken[i].vulkleur);
					gIm.setFillStyle(l.vlakken[i].vulkleur);			
				}
				else			
				{	//gIm.setColor(new Color(roodwaarde,groenwaarde,blauwwaarde,alpha));
					gIm.setFillStyle(CssColor.make(roodwaarde,groenwaarde,blauwwaarde));
				}
				
				
				if (!draadFiguur || l.vlakken[i].naam.equals("cursor"))
				{	//gIm.fillPolygon(l.vlakken[i].pol);
					gIm.moveTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.beginPath();
					for (int k = 1; k < l.vlakken[i].pol.aantalPunten; k++)
					{	gIm.lineTo(l.vlakken[i].pol.puntenX[k], l.vlakken[i].pol.puntenY[k]);
					}
					gIm.lineTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.closePath();
					gIm.fill();
				}
				
				//gIm.setColor(l.vlakken[i].lijnkleur);
				gIm.setStrokeStyle(l.vlakken[i].lijnkleur);
				if (!l.vlakken[i].isLijn && (l.vlakken[i].isOmlijnd || draadFiguur))
				{	//gIm.setColor(l.vlakken[i].lijnkleur);
					gIm.setStrokeStyle(l.vlakken[i].lijnkleur);
					//gIm.drawPolygon(l.vlakken[i].pol);
					gIm.moveTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.beginPath();
					for (int k = 1; k < l.vlakken[i].pol.aantalPunten; k++)
					{	gIm.lineTo(l.vlakken[i].pol.puntenX[k], l.vlakken[i].pol.puntenY[k]);
					}
					gIm.lineTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.closePath();
					gIm.stroke();
				}
				
				if (l.vlakken[i].isLijn)
				{	//gIm.setColor(l.vlakken[i].lijnkleur);
					gIm.setStrokeStyle(l.vlakken[i].lijnkleur);
					//gIm.drawPolygon(l.vlakken[i].pol);
					gIm.setStrokeStyle(l.vlakken[i].lijnkleur);
					//gIm.drawPolygon(l.vlakken[i].pol);
					gIm.moveTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.beginPath();
					for (int k = 1; k < l.vlakken[i].pol.aantalPunten; k++)
					{	gIm.lineTo(l.vlakken[i].pol.puntenX[k], l.vlakken[i].pol.puntenY[k]);
					}
					gIm.lineTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.closePath();
					gIm.stroke();
					
					penkleur = maakKleur("zwart");
				}

				
			}
		}
		l = new Lichaam3D();			
		l.maakNulpunt(breedte/2,hoogte/2,0);
	}
	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door handlers van het leerlingprogramma
	//-------------------------------------------------------------------------------------------
	void tekenOpnieuw()
	{	
		paint();

	}
  
  	void tekenErbij()
	{
  		paint();
	}
  	
	public Punt3D geefBeginpunt()
	{	return new Punt3D(beginpunt);
	}

	public void zetCursorAan(boolean b)
	{	cursorAan = b;
		tekenOpnieuw();
	}

	public void zetTransparant(boolean b)
	{	transparant = b;
		tekenOpnieuw();
	}

	public void zetDraadFiguur(boolean b)
	{	draadFiguur = b;
		tekenOpnieuw();
	}

	public void zoomIn()
	{
//System.out.println("tb zoomIn");		
		mat.zetStartschaal(mat.geefStartschaal()*(11e-1d));
		//zoomFactor *= 11e-1d;
		tekenOpnieuw();
//System.out.println("tb zoomIn " + UF.format(mat.geefStartschaal(), 2));		
	}
	
	public void zoomUit()
	{
		mat.zetStartschaal(mat.geefStartschaal()*(91e-2d));
		//zoomFactor *= 91e-2d;
		tekenOpnieuw();
//System.out.println("tb zoomUit " + UF.format(mat.geefStartschaal(), 2));		
		
	}

	public void zoom(double fac)
	{
//System.out.println("tb zoom " + UF.format(fac, 2));
//System.out.println("tb zoom " + UF.format(mat.geefStartschaal(),2));
		mat.zetStartschaal(mat.geefStartschaal()*fac);
		//zoomFactor *= fac;
		tekenOpnieuw();
//System.out.println("tb zoom " + UF.format(mat.geefStartschaal(), 2));		
	}
	

	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door het Tekenblad om de lijnen en vlakken te tekenen
	//-------------------------------------------------------------------------------------------

	void naarVolgendPunt(double dx,double dy, double dz)
	{	eindpunt = mat.geefVolgendPunt(beginpunt, dx, dy, dz);
		
		if (pen && !vul)
		{	l.voegPuntToe(beginpunt);
			l.voegPuntToe(eindpunt);
			l.voegPolygonToe(penkleur,penkleur,true);
		}
		if (pen && vul)
		{
//System.out.println("voegLijnToe");
			l.voegLijnToe(beginpunt, eindpunt, penkleur, vulkleur);
		}
		if (vul)		 
		{	l.voegPuntToe(beginpunt);
		}
		beginpunt.x = eindpunt.x;
		beginpunt.y = eindpunt.y;
		beginpunt.z = eindpunt.z;
		
	}
	
	void tekenPolygon()
	{	l.voegPolygonToe(vulkleur, penkleur, pen);
	}
	
 	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt in "tekenprogramma()" 
	//-------------------------------------------------------------------------------------------
	void penAan()
	{	pen = true;
	}
	void penAan(String kl)
	{	pen = true;
		penkleur = maakKleur(kl);
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
	}
	void penAan(int r, int g, int b)
	{	pen = true;
		penkleur = CssColor.make(r,g,b);
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
	}
	void penUit()
	{	pen = false;
	}
	void vulAan()
	{	vul = true;
	}
	void vulAan(String kl)
	{	vul = true;	
		vulkleur = maakKleur(kl);
	}
	void vulAan(int r, int g, int b)
	{	vul = true;	
		vulkleur = CssColor.make(r,g,b);
	}
	void vulUit()
	{	tekenPolygon();
		vul = false;
	}
	void achtergrondkleur(String kl)
	{	achtergrondkleur = maakKleur(kl);
	}
	void achtergrondkleur(int r, int g, int b)
	{	achtergrondkleur = CssColor.make(r,g,b);
	}
	Polygon geefVlak()
	{	if (l.vlakken[l.aantalPolygonen-1].normaal.z > 0)
			return l.vlakken[l.aantalPolygonen-1].pol;
		else 
			return new Polygon();
	}
	
 	//-------------------------------------------------------------------------------------------
	//deze methode wordt gebruikt een kleur in de vorm van een string om te zetten in een Color
	//-------------------------------------------------------------------------------------------
// hoe alle bestaande kleuren tranparant te maken en terug?	
	private CssColor maakKleur(String kl)
	{			
		if (kl.equals("rood"))
			return CssColor.make(255, 0, 0);
		else if (kl.equals("groen"))
			return CssColor.make(0, 255, 0);
		else if (kl.equals("blauw"))
			return CssColor.make(0, 0, 255);
		else if (kl.equals("geel"))
			return CssColor.make(255, 255, 0);
		else if (kl.equals("cyaan"))
			return CssColor.make(0, 255, 255);
		else if (kl.equals("roze"))
			return CssColor.make(255,20,147);
		else if (kl.equals("zwart"))
			return CssColor.make(0, 0, 0);
		else if (kl.equals("grijs"))
			return CssColor.make(128, 128, 128);
		else if (kl.equals("lichtgrijs"))
			return CssColor.make(200, 200, 200);
		else if (kl.equals("magenta"))
			return CssColor.make(255, 0, 255);
		else if (kl.equals("wit"))
			return CssColor.make(255, 255, 255);
		else if (kl.equals("oranje"))
			return CssColor.make(255, 165, 0);
		else
			return CssColor.make(255, 128, 0);
	}	
	
	public double geefDraaiX()
	{
		return beginx+hoekX;
	}

	public void zetBeginHoeken(double hx, double hy)
	{
		beginx = hx;
		beginy = hy;
		hoekX = 0;
		hoekY = 0;
	}

	public double geefDraaiY()
	{
		return beginy+hoekY;
	}


	public void muisSleepActie()
	{	if (eigenaar.geefMuisBeheerder() != null)
		{	hoekX = hoekX - eigenaar.geefSleepdy();
			hoekY = hoekY + eigenaar.geefSleepdx();
		}	
		tekenOpnieuw();
	}
}
