package fi.weblogo3dgwt.client.logotekenap3d;


import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * class responsible for the actual drawing of 3d objects; 
 * also implements rotation of the 3d objects 
 */

public class Tekenblad3D 
{
	/**
	 * width
	 */
	int breedte;
	/**
	 * height
	 */
	int hoogte;
	/**
	 * current 3d-turtle position
	 */
	private Punt3D beginpunt;
	/**
	 * new 3d-turtle position after moving
	 */
	private Punt3D eindpunt;
	/**
	 * initial 3d-turtle position
	 */
	private Punt3D startpunt;
	/**
	 * the 3d object, see class Lichaam3D
	 */
  	public Lichaam3D l;
  	/**
  	 * Context2d for drawing
  	 */
  	private Context2d gIm;
  	/**
  	 * matrix keeping track of the direction of the 3d-turtle (the drawing direction);
  	 * see class Matrix3D
  	 */
	public Matrix3D mat; 
	/**
	 * owner of this Tekenblad3D
	 */
	private TekenApplet3D eigenaar;
	private boolean pen, vul;
  	private CssColor penkleur,vulkleur,achtergrondkleur;

  	/**
  	 * Canvas for drawing
  	 */
	Canvas tekenbladCanvas;
	
	/**
	 * 3d polygons are drawn twice: the front (positive z for normal vector)
	 * in a shaded version of vulkleur, the back (negative z for normal vector)
	 * in a shaded version of achterkantkleur
	 */
	CssColor achterkantKleur = CssColor.make(192,192,192);
	
	/**
	 * current x-angle
	 */
	double hoekX;
	/**
	 * current y-angle
	 */
	double hoekY;
	/**
	 * initial x-angle
	 */
	double beginx;
	/**
	 * initial y-angle
	 */
	double beginy;
	
	/** 
	 * flagg for showing the cursor
	 */
	boolean cursorAan = false;
	/**
	 * flagg for transparant/solid drawing
	 */
	boolean transparant = false;
	/**
	 * flagg for wireframe/solod drawing
	 */
	boolean draadFiguur = false;
	
	
	/**
	 * constructor, create the drawing Canvas
	 * @param ap owner of this Tekenblad3D
	 * @param w width
	 * @param h height
	 */
	public Tekenblad3D(TekenApplet3D ap, int w, int h)
	{	
		breedte = w;
		hoogte = h;
		
		tekenbladCanvas = Canvas.createIfSupported();
		tekenbladCanvas.setWidth(breedte + "px");
		tekenbladCanvas.setHeight(hoogte + "px");
		tekenbladCanvas.setCoordinateSpaceWidth(breedte);
		tekenbladCanvas.setCoordinateSpaceHeight(hoogte);

		achtergrondkleur = CssColor.make(255,255,255); 
		l = new Lichaam3D();
		eigenaar = ap;
		mat = new Matrix3D();
		
	}

	/**
	 * determine the Context2d for drawing
	 */
	public void initContext2d()
	{
		gIm = tekenbladCanvas.getContext2d();
	}

	/**
	 * initialize and paint the drawing, used once
	 */
	public void paintTekenblad()
  	{ 	
//System.out.println("tb paint");
		double startschaal = Math.min((double)breedte/500,(double)hoogte/500);
		mat.initialiseer(0,0,0,startschaal);	
		startpunt = new Punt3D(breedte/2,hoogte/2,0);
		l.maakNulpunt(breedte/2,hoogte/2,0);
		gIm.setGlobalAlpha(1);
		tekenOpImage(eigenaar.eigenaar.trb.isTraceAan());
		if (transparant)
		{
			gIm.setGlobalAlpha(5e-1d);
			tekenOpImage(eigenaar.eigenaar.trb.isTraceAan());
		}
  	}

	/**
	 * draw the cursor (when tracing); note that he cursor is a separate Polygon3D in Lichaam3D
	 * in order not to mix up its points with those of the current Polygon3D being drawn
	 * when vul == true; note that the cursor is at the origin of the x-y-plane and has the form of 
	 * an asymmetric rhomboid: the longer axis coincides with the x-axis, with the longest "half" 
	 * of this axis longer coinciding with the positive x-axis; the shorter axis coincides with the y-axis,
	 * with the longest "half" of this shorter axis coinciding with the positive y-axis;       
	 * note that the direction if the cursor is the positive y-direction
	 */
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
	 * call method tekenOpImage(cursor): this method executes
	 * the total program (cursor == false) or the program up to
	 * and including the current statement being traced
	 * (cursor == true); it then paints the 3D-object; <br>
	 * to obtain a transparent drawing, calculate and draw twice
	 * (via method tekenOpImage(cursor)): once with globalAlpha 1
	 * (solid) and after that again with globalAlpha 0.5  
	 * @param cursor true/false
	 */
	public void paintDrawing(boolean cursor)
	{	
		cursorAan = cursor;
		gIm.setGlobalAlpha(1);
		tekenOpImage(cursor);
		if (transparant)
		{	gIm.setGlobalAlpha(5e-1d);
			tekenOpImage(cursor);
		}
	}

	/**
	 * execute the total program (cursor == false) or execute the
	 * program up to and including the current statement being traced
	 * (cursor == true); then paint the 3D-object; <br>
	 * @param cursor true/false
	 */
  	public void tekenOpImage(boolean cursor)
  	{ 	
  		beginpunt = new Punt3D(startpunt);
    	eindpunt = new Punt3D(beginpunt);
    	mat.initialiseer();
	  	// background
	  	gIm.setFillStyle(achtergrondkleur);
		gIm.fillRect(0, 0, breedte, hoogte);
    	// border
    	gIm.setStrokeStyle(maakKleur("grijs"));
    	gIm.strokeRect(0, 0, breedte-1, hoogte-1);
    	pen = false;
		vul = false;
		// initial rotation
		mat.xdraai(beginx+hoekX); 
		mat.ydraai(beginy+hoekY);
    	if (cursor)
    	{	eigenaar.eigenaar.trb.traceProgram();
    		tekenCursor();
    	}
    	else
    		eigenaar.eigenaar.trb.executeProgram();
		l.sorteer();

		for (int i = 0; i < l.aantalPolygonen; i++)
		{
			// these are the projected 3D Polygons with the front visible  
			if (l.vlakken[i].pol.aantalPunten > 0 && l.vlakken[i].normaal.z > 0)
			{
				// find the shading factor and RGB-values for shaded version of l.vlakken[i].vulkleur
				double grijsfactor = 0.5*((-l.vlakken[i].normaal.x - l.vlakken[i].normaal.y + l.vlakken[i].normaal.z)/Math.sqrt(3)+1);
				if (grijsfactor < 0) 
					grijsfactor = 0;
				if (grijsfactor > 1)
					grijsfactor = 1;
				if (l.vlakken[i].vulkleur == null)
				{	l.vlakken[i].vulkleur = maakKleur("magenta");
				}
			    String vString = l.vlakken[i].vulkleur.toString().substring(4, l.vlakken[i].vulkleur.toString().length() - 1);
				String[] kleurenStr = StringUtils.split(vString,",");

				int fBlue =  Integer.parseInt(kleurenStr[2]);
				int fGreen = Integer.parseInt(kleurenStr[1]);
				int fRed =   Integer.parseInt(kleurenStr[0]);
				
				int roodwaarde = 50+(int)(fRed*grijsfactor*0.75);
				int groenwaarde = 50+(int)(fGreen*grijsfactor*0.75);
				int blauwwaarde = 50+(int)(fBlue*grijsfactor*0.75);

				// front of cursor has its own color and is not shaded
				if (l.vlakken[i].naam.equals("cursor"))
				{	gIm.setFillStyle(l.vlakken[i].vulkleur);
				}
				else // shaded version of l.vlakken[i].vulkleur  			
				{	gIm.setFillStyle(CssColor.make(roodwaarde,groenwaarde,blauwwaarde));
				
				}
				
				// fill the projected 3D Polygon or the cursor  
				if (!draadFiguur || l.vlakken[i].naam.equals("cursor"))
				{	
					gIm.moveTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.beginPath();
					for (int k = 1; k < l.vlakken[i].pol.aantalPunten; k++)
					{	gIm.lineTo(l.vlakken[i].pol.puntenX[k], l.vlakken[i].pol.puntenY[k]);
					}
					gIm.lineTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.closePath();
					gIm.fill();

				}
				// outline the projected 3D Polygon
				gIm.setStrokeStyle(l.vlakken[i].lijnkleur);
				if (!l.vlakken[i].isLijn && (l.vlakken[i].isOmlijnd || draadFiguur))
				{	
					gIm.setStrokeStyle(l.vlakken[i].lijnkleur);
					gIm.moveTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.beginPath();
					for (int k = 1; k < l.vlakken[i].pol.aantalPunten; k++)
					{	gIm.lineTo(l.vlakken[i].pol.puntenX[k], l.vlakken[i].pol.puntenY[k]);
					}
					gIm.lineTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.closePath();
					gIm.stroke();

				}
				// draw projected 3D Polygons that are lines
				if (l.vlakken[i].isLijn)
				{	
					gIm.setStrokeStyle(l.vlakken[i].lijnkleur);
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
 			// added Huub: also draw 3D Polygons with the backside visible
			else if (l.vlakken[i].pol.aantalPunten > 0 && l.vlakken[i].normaal.z <= 0) 
			{
				// find the shading factor and RGB-values for shaded version of achterkantKleur
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
				
				// backside of cursor has same color as front and is not shaded
				if (l.vlakken[i].naam.equals("cursor"))
				{	gIm.setFillStyle(l.vlakken[i].vulkleur);			
				}
				else // shaded version of achterkantKleuer			
				{	gIm.setFillStyle(CssColor.make(roodwaarde,groenwaarde,blauwwaarde));
				}
				
				// fill the projected 3D Polygons backside
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
				
				// outline the projected 3D Polygons backside
				if (!l.vlakken[i].isLijn && (l.vlakken[i].isOmlijnd || draadFiguur))
				{	
					gIm.setStrokeStyle(l.vlakken[i].lijnkleur);
					gIm.moveTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.beginPath();
					for (int k = 1; k < l.vlakken[i].pol.aantalPunten; k++)
					{	gIm.lineTo(l.vlakken[i].pol.puntenX[k], l.vlakken[i].pol.puntenY[k]);
					}
					gIm.lineTo(l.vlakken[i].pol.puntenX[0], l.vlakken[i].pol.puntenY[0]);
					gIm.closePath();
					gIm.stroke();
				}
				
				// draw projected 3D Polygons that are lines (redundant?)
				if (l.vlakken[i].isLijn)
				{	gIm.setStrokeStyle(l.vlakken[i].lijnkleur);
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
		// reset
		l = new Lichaam3D();			
		l.maakNulpunt(breedte/2,hoogte/2,0);
	}
	
  	/**
  	 * drawing trasparant/solid (button action, see class WebLogo3dGWT))
  	 * @param b true/false
  	 */
	public void zetTransparant(boolean b)
	{	transparant = b;
		paint();
	}

	/**
	 * drawing wireframe or solid (button action, see class WebLogo3dGWT))
	 * @param b true/false
	 */
	public void zetDraadFiguur(boolean b)
	{	draadFiguur = b;
		paint();
	}

	/**
	 * zoom in: multiply the zoom factor with 1.1 (button action, see class WebLogo3dGWT)
	 */
	public void zoomIn()
	{
		mat.zetStartschaal(mat.geefStartschaal()*(11e-1d));
		paint();
	}

	/**
	 * zoom out: multiply the zoom factor with 0.91 (button action, see class WebLogo3dGWT)
	 */
	public void zoomUit()
	{
		mat.zetStartschaal(mat.geefStartschaal()*(91e-2d));
		paint();
	}

	/**
	 * zoom in or zoom out by a factor fac: fac larger than 1 is zoom in, fac smaller than 1 is zoom out (setState)
	 * @param fac zoom factor
	 */
	public void zoom(double fac)
	{
		mat.zetStartschaal(mat.geefStartschaal()*fac);
		paint();
	}
	

	/**
	 * move to a new position calculated as new = current + mat * (dx,dy,dz)T; 
 	 * note how this works: the 3d-turtle (cursor) exists in a plane containing an x-and y-axis, while 
 	 * the z-axis is perpendicular to this plane; in this coordinate system change the position by (dx,dy,dz); 
 	 * see method tekenCursor() and class TekenAppler3D; <br>
 	 * if pen == true and vul == false, connect old and new position by a line in penkleur, that is, add 
 	 * two points to Lichaam3D l and turn these into a Polygon3D; <br>
 	 * if pen == true and vul == true, connect old and new position by a line in penkleur, that is, add 
 	 * a separate line (Polygon3D) to Lichaam3D l; <br>
 	 * if vul == true, add the old position as a point to Lichaam3D (all these points are part of a Polygon3D being
 	 * formed, see class Lichaam3D), which will be added to Lichaam3D at tekenPolygon()<br>  
	 * @param dx x-change
	 * @param dy y-change
	 * @param dz z-change
	 */
	void naarVolgendPunt(double dx,double dy, double dz)
	{	eindpunt = mat.geefVolgendPunt(beginpunt, dx, dy, dz);
		
		if (pen && !vul)
		{	l.voegPuntToe(beginpunt);
			l.voegPuntToe(eindpunt);
			l.voegPolygonToe(penkleur,penkleur,true);
		}
		if (pen && vul)
		{
			l.voegLijnToe(beginpunt, eindpunt, penkleur, vulkleur);
		}
		if (vul)		 
		{	l.voegPuntToe(beginpunt);
		}
		beginpunt.x = eindpunt.x;
		beginpunt.y = eindpunt.y;
		beginpunt.z = eindpunt.z;
		
	}
	
	/**
	 * turn all points added to Lichaam3D l into a Polygon3D, and reset the points; 
	 * 
	 */
	void tekenPolygon()
	{	l.voegPolygonToe(vulkleur, penkleur, pen);
	}

	/**
	 * activate the pen
	 */
	void penAan()
	{	pen = true;
	}
	/**
	 * activate the pen with color kl 
	 * @param kl name of the color
	 */
	void penAan(String kl)
	{	pen = true;
		penkleur = maakKleur(kl);
		gIm.setStrokeStyle(penkleur);
	}
	/**
	 * activate the pen with rgb-color (r,g,b)
	 * @param r red value
	 * @param g green value
	 * @param b blue value
	 */
	void penAan(int r, int g, int b)
	{	pen = true;
		penkleur = CssColor.make(r,g,b);
		gIm.setStrokeStyle(penkleur);
	}
	/**
	 * de-activate the pen 
	 */
	void penUit()
	{	pen = false;
	}
	/**
	 * start vul, that is add all points through which the cursor passes
	 * to Lichaam3D l; 
	 * vulUit() turns these points into a Polygon3D and fills that  with vulkleur 
	 */
	void vulAan()
	{	vul = true;
	}
	/**
	 * start vul, that is add all points through which the cursor passes
	 * to Lichaam3D l; 
	 * set vulkleur to the color given by the name kl;
	 * vulUit() turns these points into a Polygon3D and fills that  with vulkleur
	 * @param kl name of color 
	 */
	void vulAan(String kl)
	{	vul = true;	
		vulkleur = maakKleur(kl);
	}
	/**
	 * start vul, that is add all points through which the cursor passes
	 * to Lichaam3D l; 
	 * set vulkleur to the RGB-color given by the integers (r,g,b)l;
	 * vulUit() turns these points into a Polygon3D and fills that  with vulkleur 
	 * @param r red value of color
	 * @param g green value of color
	 * @param b blue value of color
	 */
	void vulAan(int r, int g, int b)
	{	vul = true;	
		vulkleur = CssColor.make(r,g,b);
	}
	/**
	 * de-activate vul and turn all points added to Lichaam3D since the last vulAan()
	 * into a Polygon3D to be filled with vulkeur
	 */
	void vulUit()
	{	tekenPolygon();
		vul = false;
	}

	/**
	 * given the name (in Dutch or English) of a color, create that color
	 * @param kl color name (in Dutch or English)
	 * @return corresponding color
	 */
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
			return CssColor.make(192, 192, 192);
		else if (kl.equals("lichtgrijs"))
			return CssColor.make(220, 220, 220);
		else if (kl.equals("magenta"))
			return CssColor.make(255, 0, 255);
		else if (kl.equals("wit"))
			return CssColor.make(255, 255, 255);
		else if (kl.equals("oranje"))
			return CssColor.make(255, 165, 0);
		
		else if ( kl.equals("red")) 
			return CssColor.make(255,0,0);
		else if ( kl.equals("green")) 
			return CssColor.make(0,255,0);
		else if ( kl.equals("blue")) 
			return CssColor.make(0,0,255);
		else if ( kl.equals("yellow")) 
			return CssColor.make(255, 255, 0);
		else if ( kl.equals("cyan")) 
			return CssColor.make(0, 255, 255);
		else if ( kl.equals("pink")) 
			return CssColor.make(255,20,147);
		else if ( kl.equals("black")) 
			return CssColor.make(0,0,0);
		else if ( kl.equals("gray")) 
			return CssColor.make(192, 192, 192);
		else if ( kl.equals("lightGray")) 
			return CssColor.make(220, 220, 220);
		else if ( kl.equals("magenta")) 
			return CssColor.make(255, 0, 255);
		else if ( kl.equals("white")) 
			return CssColor.make(255,255,255);
		else if ( kl.equals("orange")) 
			return CssColor.make(255, 165, 0);

		else
			return CssColor.make(255, 0, 0);
	}	

	/**
	 * get the x-rotation (getState)
	 * @return x-rotaion
	 */
	public double geefDraaiX()
	{
		return beginx+hoekX;
	}

	/**
	 * get the y-rotation (getState)
	 * @return y-rotation
	 */
	public double geefDraaiY()
	{
		return beginy+hoekY;
	}

	/**
	 * set initial angles (setState)
	 * @param hx initial x-rotation
	 * @param hy initial y-rotaion 
	 */
	public void zetBeginHoeken(double hx, double hy)
	{
		beginx = hx;
		beginy = hy;
		hoekX = 0;
		hoekY = 0;
	}

	/**
	 * translate dragg dx and dy into a rotation of the 3D object
	 */
	public void muisSleepActie()
	{	
//System.out.println("hX = " + hoekX);
//System.out.println("hY = " + hoekY);
		if (eigenaar.geefMuisBeheerder() != null)
		{	hoekX = hoekX - 0.5 * eigenaar.geefSleepdy();
			if (hoekX < 0)
				hoekX += 360;
			if (hoekX >= 360)
				hoekX -= 360;
			// make sure x-dragging on the outside rotates
			// in the right direction
			if ((hoekX <= 90) || (hoekX >= 270))
				hoekY = hoekY + 0.5 * eigenaar.geefSleepdx();
			else 
				hoekY = hoekY - 0.5 * eigenaar.geefSleepdx();
			if (hoekY < 0)
				hoekY += 360;
			if (hoekY >= 360)
				hoekY -= 360;
		}	
		paint();
	}
}
