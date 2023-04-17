package fi.kladjegwt.client;

import java.util.*;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * klasse die een streep representeert: <br> een streep is de collectie punten die wordt getekend 
 * door te slepen tussen een mouseDown/touchStart en de eerstvolgende mouseUp/touchEnd;
 * de mouseUp/touchEnd fixeert de streep;<br>
 * streep verplaatsten, schalen, draaien of verwijderen: <br>
 * ga in modus selecteren (togglebuttons), klik op de streep, waarna een handlebox
 * (met vier schaal handles op de hoeken en twee rotatie handles op de zijden) verschijnt; 
 * klikken plus slepen in de handlebox verplaatst de streep, klikken plus slepen in de schaal handles
 * schaalt de streep, klikken plus slepen in de rotatie handles draait de streep, klikken op
 * de wisknop verwijderd de streep;
 * een streep wordt ook verplaatst, geschaald, gedraaid of verwijderd als deel van een geselecteerde
 * groep van figuren.<br>
 * een Streep wordt als volgt "onthouden": onthoudt initiele punten bij creatie,
 * en stop alle wijzigingen (verplaatsingen, schalingen, draaiingen)
 * cumulatief in een affiene transformatie en onthoudt deze affiene transformatie     
 * @author huub
 */

public class Streep 
{
	/**
	 * de kleur van deze Streep
	 */
	CssColor kleur;
	/**
	 * de initiele punten van deze streep
	 */
	double[] puntenXD, puntenYD;
	/**
	 * de actuele punten van deze Streep (dus na verplaatsingen, draaiingen, schaling) 
	 */
	double[] pXD, pYD;
	/**
	 * dikte van de bounding box van deze Streep
	 */
	int bbFactor = 4;
	/**
	 * de bounding box van deze Streep
	 */
	Polygon bb;
	/**
	 * het middelpunt (barycentrum) van deze streep 
	 */
	double cx = 0, cy = 0;
	/**
	 * gebruik een affiene transformatie om cumulatief alle
	 * veranderingen (verplaatsing, schaling, draaing) aan deze
	 * Streep op te slaan 
	 */
	AffineTransform at = new AffineTransform();
	
	/**
	 * de handle box van deze Rechthoek
	 */
	Rectangle handleBox;
	/**
	 * aantal pixels offset voor de handlebox
	 */
	int hbFactor = 4;

	/**
	 * vier handles op de hoeken van de handlebox om te scalen
	 */
	Polygon topRightHandle, bottomRightHandle, topLeftHandle, bottomLeftHandle;
	/**
	 * klikken en slepen op de vier handles op de hoeken van de handlebox om te scalen 
	 */
	Rectangle topRightRect, bottomRightRect, topLeftRect, bottomLeftRect;
	/**
	 * klikken en slepen op de twee draai-handles
	 */
	Rectangle rotateEastHandle, rotateWestHandle;

	/** 
	 * kan deze Streep verwijderd worden?
	 */
	boolean deletable = true;

	/**
	 * constructor, bereken meteen het middelpunt
	 * @param c de kleur van de Streep
	 * @param punten de punten (ArrayList met DoublePoints)
	 */
	public Streep(CssColor c, ArrayList<DoublePoint> punten)
	{	kleur = c;
		puntenXD = new double[punten.size()];
		puntenYD = new double[punten.size()];

		for (int pCnt = 0; pCnt < punten.size(); pCnt++)
		{	DoublePoint pt = punten.get(pCnt);
			puntenXD[pCnt] = pt.x;
			puntenYD[pCnt] = pt.y;
			
			cx += puntenXD[pCnt];
			cy += puntenYD[pCnt];
		}
		
		cx /= puntenXD.length;
		cy /= puntenYD.length;

		maakStreep();
	}
	
	/**
	 * constructor (overloaded), bereken meteen het middelpunt
	 * @param c de kleur van de Streep
	 * @param punten de punten (Vector met Points)
	 */
	public Streep(CssColor c, Vector<Point> punten)
	{	kleur = c;
		puntenXD = new double[punten.size()];
		puntenYD = new double[punten.size()];

		for (int pCnt = 0; pCnt < punten.size(); pCnt++)
		{	Point pt = (Point) punten.elementAt(pCnt);
			puntenXD[pCnt] = pt.x;
			puntenYD[pCnt] = pt.y;
		
			cx += puntenXD[pCnt];
			cy += puntenYD[pCnt];
		}
		
		cx /= puntenXD.length;
		cy /= puntenYD.length;

		maakStreep();
	}
	
	/**
	 * constructor (overloaded), bereken meteen het middelpunt
	 * @param c de kleur van de Streep
	 * @param ptXD x-coordinaten van de punten als doubles
	 * @param ptYD y-coordinaten van de punten als doubles
	 */
	public Streep(CssColor c, double[] ptXD, double[] ptYD)
	{	kleur = c;
	
		puntenXD = new double[ptXD.length];
		puntenYD = new double[ptYD.length];

		for (int cnt = 0; cnt < ptXD.length; cnt++) 
		{	puntenXD[cnt] = ptXD[cnt];
			puntenYD[cnt] = ptYD[cnt];
			
			cx += puntenXD[cnt];
			cy += puntenYD[cnt];
			
		}

		cx /= puntenXD.length;
		cy /= puntenYD.length;

		maakStreep();

	}

	/**
	 * constructor (overloaded), bereken meteen het middelpunt
	 * @param c de kleur van de Streep
	 * @param ptX x-coordinaten van de punten als integers
	 * @param ptY y-coordinaten van de punten als integers
	 */
	public Streep(CssColor c, int[] ptX, int[] ptY)
	{	kleur = c;
		puntenXD = new double[ptX.length];
		puntenYD = new double[ptY.length];

		for (int cnt = 0; cnt < ptX.length; cnt++) 
		{	puntenXD[cnt] = ptX[cnt];
			puntenYD[cnt] = ptY[cnt];
			
			cx += puntenXD[cnt];
			cy += puntenYD[cnt];

		}

		cx /= puntenXD.length;
		cy /= puntenYD.length;

		maakStreep();
		//maakBBs();
		//makeHandleBox();
	}
	
	/**
	 * kopieer initiele punten in actuele punten,
	 * bepaal de bounding en handle box
	 */
	public void maakStreep()
	{
		pXD = new double[puntenXD.length];
		pYD = new double[puntenYD.length];
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{
			pXD[pCnt] = puntenXD[pCnt];
			pYD[pCnt] = puntenYD[pCnt];
		}
		
		maakBBs();

		makeHandleBox();
	}
	
	/**
	 * maak de handle box voor deze Streep (minimum breedte en hoogte in KladjeVeldGWT)
	 * en voeg handles toe als er geschaald en/of geroteerd mag worden
	 */
	public void makeHandleBox()
	{
		int minX = 1000;
		int maxX = -100;
		int minY = 1000;
		int maxY = -100;
		for (int pCnt = 0; pCnt < bb.aantalPunten; pCnt++)
		{
			if (bb.puntenX[pCnt] < minX)
				minX = bb.puntenX[pCnt];
			if (bb.puntenX[pCnt] > maxX)
				maxX = bb.puntenX[pCnt];
			if (bb.puntenY[pCnt] < minY)
				minY = bb.puntenY[pCnt];
			if (bb.puntenY[pCnt] > maxY)
				maxY = bb.puntenY[pCnt];
		}
		
		int w = maxX - minX + 2 * hbFactor;
		int h = maxY - minY + 2 * hbFactor;
		int dw = w - KladjeGWTVeld.minHandleBoxSize;
		int dh = h - KladjeGWTVeld.minHandleBoxSize;
		if ((dw >= 0) && (dh >= 0))
			handleBox = new Rectangle(minX - hbFactor, minY - hbFactor,w, h);
		else if ((dw >= 0) && (dh < 0))
			handleBox = new Rectangle(minX - hbFactor, minY - hbFactor + dh/2, w, KladjeGWTVeld.minHandleBoxSize);
		else if ((dw < 0) && (dh >= 0))
			handleBox = new Rectangle(minX - hbFactor + dw/2, minY - hbFactor, KladjeGWTVeld.minHandleBoxSize, h);
		else if ((dw < 0) && (dh < 0))
			handleBox = new Rectangle(minX - hbFactor + dw/2, minY - hbFactor + dh/2, KladjeGWTVeld.minHandleBoxSize, KladjeGWTVeld.minHandleBoxSize);
		
		if (KladjeGWTVeld.schalen)
			makeScaleHandles();
		if (KladjeGWTVeld.roteren)
			makeRotateHandles();
		
	}
	
	/**
	 * maak de vier handles om the schalen en de bijbehorende klik-rechthoeken
	 */
	public void makeScaleHandles()
	{
		topRightHandle = new Polygon();
		topRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								handleBox.y - hbFactor);
		topRightHandle.addPoint(handleBox.x + handleBox.width - 3 * hbFactor,
								handleBox.y - hbFactor);
		topRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								handleBox.y + 3 * hbFactor);
		topRightRect = new Rectangle(handleBox.x + handleBox.width - 3 * hbFactor,
									 handleBox.y - hbFactor, 4 * hbFactor, 4 * hbFactor);
		
		topLeftHandle = new Polygon();
		topLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y - hbFactor);
		topLeftHandle.addPoint(handleBox.x + 3 * hbFactor, handleBox.y - hbFactor);
		topLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + 3 * hbFactor);
		topLeftRect = new Rectangle(handleBox.x - hbFactor, handleBox.y - hbFactor, 4 * hbFactor, 4 * hbFactor);
		
		bottomRightHandle = new Polygon();
		bottomRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								   handleBox.y + handleBox.height + hbFactor);
		bottomRightHandle.addPoint(handleBox.x + handleBox.width - 3 * hbFactor,
								   handleBox.y + handleBox.height + hbFactor);
		bottomRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								   handleBox.y + handleBox.height - 3 * hbFactor);
		bottomRightRect = new Rectangle(handleBox.x + handleBox.width - 3 * hbFactor,
				   						handleBox.y + handleBox.height - 3 * hbFactor, 4 * hbFactor, 4 * hbFactor);		
		
		bottomLeftHandle = new Polygon();
		bottomLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + handleBox.height + hbFactor);
		bottomLeftHandle.addPoint(handleBox.x + 3 * hbFactor, handleBox.y + handleBox.height + hbFactor);
		bottomLeftHandle.addPoint(handleBox.x - hbFactor, handleBox.y + handleBox.height - 3 * hbFactor);
		bottomLeftRect = new Rectangle(handleBox.x - hbFactor, handleBox.y + handleBox.height - 3 * hbFactor, 
									   4 * hbFactor, 4 * hbFactor);		
		
	}
	/**
	 * zet de vier handles om the schalen en de bijbehorende klikrechthoeken op null
	 */
	public void killScaleHandles()
	{
		topRightHandle = null; 
		bottomRightHandle = null; 
		topLeftHandle = null;
		bottomLeftHandle = null;
		topRightRect = null;
		bottomRightRect = null;
		topLeftRect = null;
		bottomLeftRect = null;
		
	}
	
	public void setColor(CssColor kleur) {
		this.kleur = kleur;
	}
	
	/**
	 * maak de klik-rechtoeken voor de twee draai-handles
	 */
	public void makeRotateHandles()
	{
		rotateEastHandle = new Rectangle(handleBox.x + handleBox.width,// - 2 * hbFactor,
										 handleBox.y + handleBox.height/2 - 2 * hbFactor,
										 4 * hbFactor, 4 * hbFactor);
		rotateWestHandle = new Rectangle(handleBox.x - 4 * hbFactor, 
										 handleBox.y + handleBox.height/2 - 2 * hbFactor,
										 4 * hbFactor, 4 * hbFactor);
	}

	/**
	 * zet de klik-rechtoeken voor de twee draai-handles op null
	 */
	public void killRotateHandles()
	{
		rotateEastHandle = null; 
		rotateWestHandle = null;
	}
	
	/**
	 * maak een bounding box voor de Streep als volgt:
	 * loop langs de Streep van het begin to het einde en bepaal de punten van de ene kant van 
	 * de bounding box; loop dan weer terug naar het begin en bepaal de punten van de andere kant van
	 * de bounding box
	 */
	public void maakBBs()
	{
		bb = new Polygon();
		
		if (puntenXD.length == 1)
		{	bb.addPoint((int) Math.round(puntenXD[0] - bbFactor), (int) Math.round(puntenYD[0] - bbFactor));
			bb.addPoint((int) Math.round(puntenXD[0] + bbFactor), (int) Math.round(puntenYD[0] - bbFactor));
			bb.addPoint((int) Math.round(puntenXD[0] + bbFactor), (int) Math.round(puntenYD[0] + bbFactor));
			bb.addPoint((int) Math.round(puntenXD[0] - bbFactor), (int) Math.round(puntenYD[0] + bbFactor));
		}
		if (puntenXD.length > 1)
		{  
			
			// for loop 1
			for (int pCnt = 1; pCnt < puntenXD.length; pCnt++)
			{	
				double fromX = puntenXD[pCnt - 1];
				double fromY = puntenYD[pCnt - 1];
				double toX = puntenXD[pCnt];
				double toY = puntenYD[pCnt];
				// richtingsvector
				double rX = toX - fromX;
				double rY = toY - fromY;
				// normaalvector
				double nx = 0;
				double ny = 0;
				boolean normal1 = true;
				if (Math.abs(rX) > Math.abs(rY))
				{	nx = rY;
					ny = -rX;
					normal1 = false;
				}
				else
				{	nx = -rY;
					ny = rX;
				}
				// eenheids normaalvector
				double nl = Math.sqrt(nx * nx + ny * ny);
				if (nl > 0)
				{	 
					if (normal1)
					{	
						// eerste punt
						double px = fromX + nx * bbFactor / nl;
						double py = fromY + ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py)); 
						// 	tweede punt
						px = toX + nx * bbFactor / nl;
						py = toY + ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py));
					}
					else
					{	
						//	vierde punt
						double px = fromX - nx * bbFactor / nl;
						double py = fromY - ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py));
						
						//derde punt
						px = toX - nx * bbFactor / nl;
						py = toY - ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py));

					}	
				}	
		
			}
		
			// for loop 2
			for (int pCnt = puntenXD.length - 1; pCnt > 0; pCnt--)
			{	
				double fromX = puntenXD[pCnt - 1];
				double fromY = puntenYD[pCnt - 1];
				double toX = puntenXD[pCnt];
				double toY = puntenYD[pCnt];
				// richtingsvector
				double rX = toX - fromX;
				double rY = toY - fromY;
				// normaalvector
				double nx = 0;
				double ny = 0;
				boolean normal1 = true;
				if (Math.abs(rX) > Math.abs(rY))
				{	nx = rY;
					ny = -rX;
					normal1 = false;
				}
				else
				{	nx = -rY;
					ny = rX;
				}
				// eenheids normaalvector
				double nl = Math.sqrt(nx * nx + ny * ny);
				if (nl > 0)
				{	
					if (!normal1)
					{	
						// 	tweede punt
						double px = toX + nx * bbFactor / nl;
						double py = toY + ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py));
					
						// eerste punt
						px = fromX + nx * bbFactor / nl;
						py = fromY + ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py));
					}
					else
					{	
						
						// derde punt
						double px = toX - nx * bbFactor / nl;
						double py = toY - ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py));
						// 	vierde punt
						px = fromX - nx * bbFactor / nl;
						py = fromY - ny * bbFactor / nl;
						bb.addPoint((int) Math.round(px), (int) Math.round(py));
					}
				}	
		
			}
		}
		
	}
	
	/**
	 * draai deze Streep over rotateStep radialen tegen de klok in met
	 * als draaicentrum het middelpunt van deze Streep; update de affiene transformatie,
	 * de bounding box en de handle box  
	 * @param rotateStep draaihoek (radialen)
	 */
	public void rotate(double rotateStep)
	{		
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			double pXDNew = Math.cos(rotateStep) * (pXD[pCnt] - cx) - Math.sin(rotateStep) * (pYD[pCnt] - cy);
			double pYDNew = Math.sin(rotateStep) * (pXD[pCnt] - cx) + Math.cos(rotateStep) * (pYD[pCnt] - cy);
			pXD[pCnt] = pXDNew + cx;
			pYD[pCnt] = pYDNew + cy;
		}
		
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
				  Math.sin(rotateStep), Math.cos(rotateStep), 
				  cx - Math.cos(rotateStep) * cx + Math.sin(rotateStep) * cy, 
				  cy - Math.sin(rotateStep) * cx - Math.cos(rotateStep) * cy);
		at = at.leftMultiplyBy(rot);

		bb.rotate(rotateStep, cx, cy);
	
		makeHandleBox();
		
	}
	/**
	 * draai deze Streep over rotateStep radialen tegen de klok in met
	 * als draaicentrum het punt (dx,dy); update het middelpunt, de affiene transformatie,
	 * de bounding rechthoek en de handle box  
	 * @param rotateStep draaihoek (radialen)
	 * @param dx x-coordinaat draaicentrum
	 * @param dy y-coordinaat draaicentrum
	 */
	public void rotate(double rotateStep, double dx, double dy)
	{	
	
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			double pXDNew = Math.cos(rotateStep) * (pXD[pCnt] - dx) - Math.sin(rotateStep) * (pYD[pCnt] - dy);
			double pYDNew = Math.sin(rotateStep) * (pXD[pCnt] - dx) + Math.cos(rotateStep) * (pYD[pCnt] - dy);
			pXD[pCnt] = pXDNew + dx;
			pYD[pCnt] = pYDNew + dy;
		}

		double cxNew = Math.cos(rotateStep) * (cx - dx) - Math.sin(rotateStep) * (cy - dy);
		double cyNew = Math.sin(rotateStep) * (cx - dx) + Math.cos(rotateStep) * (cy - dy);
		cx = cxNew + dx;
		cy = cyNew + dy;
		
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
												  Math.sin(rotateStep), Math.cos(rotateStep), 
												  dx - Math.cos(rotateStep) * dx + Math.sin(rotateStep) * dy, 
												  dy - Math.sin(rotateStep) * dx - Math.cos(rotateStep) * dy);
		at = at.leftMultiplyBy(rot);
		
		bb.rotate(rotateStep, dx, dy);
	
		makeHandleBox();
	}
	
	/**
	 * schaal deze Streep vanuit het middelpunt met een factor scaleStep; update de affiene transformatie,
	 * de bounding box en de handle box 
	 * @param scaleStep schaalfactor
	 */
	public void scale(double scaleStep)
	{
//		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
//		{	
//			puntenXD[pCnt] = scaleStep * puntenXD[pCnt] + (1 - scaleStep) * cx;
//			puntenYD[pCnt] = scaleStep * puntenYD[pCnt] + (1 - scaleStep) * cy;
//		}
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			pXD[pCnt] = scaleStep * pXD[pCnt] + (1 - scaleStep) * cx;
			pYD[pCnt] = scaleStep * pYD[pCnt] + (1 - scaleStep) * cy;
		}
		
		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * cx, (1 - scaleStep) * cy);
		at = at.leftMultiplyBy(sc);

		bb.scale(scaleStep, cx, cy);
		
		makeHandleBox();
		
	}
	
	/**
	 * schaal deze Streep vanuit het punt (dx,dy) met een factor scaleStep; update het middelpunt, 
	 * de affiene transformatie, de bounding box en de handle box 
	 * @param scaleStep schaalfactor
	 * @param dx x-coordinaat schaalcentrum 
	 * @param dy y-coordinaat schaalcentrum
	 */
	public void scale(double scaleStep, double dx, double dy)
	{
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			pXD[pCnt] = scaleStep * pXD[pCnt] + (1 - scaleStep) * dx;
			pYD[pCnt] = scaleStep * pYD[pCnt] + (1 - scaleStep) * dy;
		}
		
		cx = scaleStep * cx + (1 - scaleStep) * dx;
		cy = scaleStep * cy + (1 - scaleStep) * dy;
		
		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * dx, (1 - scaleStep) * dy);
		at = at.leftMultiplyBy(sc);
		
		bb.scale(scaleStep, dx, dy);

		makeHandleBox();
		
	}


	/**
	 * schaal deze Streep vanuit het middelpunt met een factor sx in de x-richting en een
	 * factor sy in de y-richting; update de affiene transformatie,
	 * de bounding box en de handle box 
	 * @param scaleStepX schaalfactor x-richting
	 * @param scaleStepY schaalfactor y-richting
	 */
	public void scale(double scaleStepX, double scaleStepY)
	{
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			pXD[pCnt] = scaleStepX * pXD[pCnt] + (1 - scaleStepX) * cx;
			pYD[pCnt] = scaleStepY * pYD[pCnt] + (1 - scaleStepY) * cy;
		}
		
		AffineTransform sc = new AffineTransform(scaleStepX, 0, 0, scaleStepY, (1 - scaleStepX) * cx, (1 - scaleStepY) * cy);
		at = at.leftMultiplyBy(sc);

		bb.scale(scaleStepX, scaleStepY, cx, cy);
		
		maakStreep();
		
		makeHandleBox();
		
	}
	
	/**
	 * schaal deze Streep vanuit het punt (dx,dy) met een factor sx in de x-richting en een
	 * factor sy in de y-richting; update het middelpunt, de affiene transformatie,
	 * de bounding box en de handle box 
	 * @param scaleStepX schaalfactor x-richting
	 * @param scaleStepY schaalfactor y-richting
	 * @param dx x-coordinaat schaalcentrum
	 * @param dy y-coordinaat schaalcentrum
	 */
	public void scale(double scaleStepX, double scaleStepY, double dx, double dy)
	{
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			pXD[pCnt] = scaleStepX * pXD[pCnt] + (1 - scaleStepX) * dx;
			pYD[pCnt] = scaleStepY * pYD[pCnt] + (1 - scaleStepY) * dy;
		}
		
		cx = scaleStepX * cx + (1 - scaleStepX) * dx;
		cy = scaleStepY * cy + (1 - scaleStepY) * dy;
		
		AffineTransform sc = new AffineTransform(scaleStepX, 0, 0, scaleStepY, (1 - scaleStepX) * dx, (1 - scaleStepY) * dy);
		at = at.leftMultiplyBy(sc);
		
		
		bb.scale(scaleStepX, scaleStepY, dx, dy);
		
		
		makeHandleBox();
		
	}

	/**
	 * sla de state van deze Streep op in een HashMap, dus x- en y coordinaten  
	 * initiele punten (maak van de arrays ArrayLists) en
	 * de affiene transformatie met (cumulatief) alle wijzigingen 
	 * @return state HashMap
	 */
	public HashMap<String, Object> getState()
	{	HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("kleurgwt", kleur.value());
		
		ArrayList<Double> puntenXDAL = new ArrayList<Double>();
		ArrayList<Double> puntenYDAL = new ArrayList<Double>();
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{
			puntenXDAL.add(new Double(puntenXD[pCnt]));
			puntenYDAL.add(new Double(puntenYD[pCnt]));
		}

		h.put("puntenXD", puntenXDAL);
		h.put("puntenYD", puntenYDAL);
		
		// affiene transformatie
		h.put("m00", new Double(at.m00));
		h.put("m10", new Double(at.m10));
		h.put("m01", new Double(at.m01));
		h.put("m11", new Double(at.m11));
		h.put("b0", new Double(at.b0));
		h.put("b1", new Double(at.b1));


	
		return h;
	}

	/**
	 * lees de state van een Streep uit een HashMap en creeer
	 * deze Streep; houdt rekening met oudere versies
	 * @param map de HashMap waarin de state van een Streep
	 * @return de Streep  
	 */
	public static Streep setState(Map<String, Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		CssColor kleur = KladjeGWTVeld.zwart;
		double[] puntenXD = new double[0];
		double[] puntenYD = new double[0];
		List<Double> puntenXDAL = new ArrayList<Double>();
		List<Double> puntenYDAL = new ArrayList<Double>();
		double rotation = 0;
		
		double m00 = 1;
		double m01 = 0;
		double m10 = 0;
		double m11 = 1;
		double b0 = 0;
		double b1 = 0;

		
		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make(h.getString("kleurgwt"));
		
		// launchdata or data from getState
		if (h.containsKey("puntenXD"))
			puntenXDAL = h.getDoubleList("puntenXD");
		if (h.containsKey("puntenYD"))
			puntenYDAL = h.getDoubleList("puntenYD");

		// compatibility oude versie
		if (h.containsKey("rotation"))
			rotation = h.getDouble("rotation");

		// affiene transformatie
		if (h.containsKey("m00"))
			m00 = h.getDouble("m00");
		if (h.containsKey("m10"))
			m10 = h.getDouble("m10");
		if (h.containsKey("m01"))
			m01 = h.getDouble("m01");
		if (h.containsKey("m11"))
			m11 = h.getDouble("m11");
		if (h.containsKey("b0"))
			b0 = h.getDouble("b0");
		if (h.containsKey("b1"))
			b1 = h.getDouble("b1");

		// maak van de ArayLists arrays
		puntenXD = new double[puntenXDAL.size()];
		puntenYD = new double[puntenYDAL.size()];
		for (int pCnt = 0; pCnt < puntenXDAL.size(); pCnt++)
		{
			puntenXD[pCnt] = ((Number) puntenXDAL.get(pCnt)).doubleValue();
			puntenYD[pCnt] = ((Number) puntenYDAL.get(pCnt)).doubleValue();
		}
		
		
		Streep streep = new Streep(kleur, puntenXD, puntenYD);
		streep.maakStreep();
		
		if (h.containsKey("rotation"))
		{	streep.rotate(rotation);
		}
		else if (h.containsKey("b0"))
		{	streep.transformBy(m00, m01, m10, m11, b0, b1);
		}
		else
		{	streep.transformBy(m00, m01, m10, m11);
		}
		
		return streep;
	}

	/**
	 * transformeer deze Streep m.b.v. een lineaire transformatie M;
	 * alleen gebruikt in setState na creatie van de initiele Streep; 
	 * bepaal de affiene transformatie, de bounding box en 
	 * de handle box
	 * @param m00 M linksboven
	 * @param m01 M rechtsboven
	 * @param m10 M linksonder
	 * @param m11 M rechtsonder
	 */
	public void transformBy(double m00, double m01, double m10, double m11)
	{
		
		at = new AffineTransform(m00, m01, m10, m11, - m00*cx - m01*cy + cx, - m10*cx - m11*cy + cy);
		
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			double pXDNew = at.m00 * pXD[pCnt] + at.m01 * pYD[pCnt] + at.b0;
			double pYDNew = at.m10 * pXD[pCnt] + at.m11 * pYD[pCnt] + at.b1;
			pXD[pCnt] = pXDNew;
			pYD[pCnt] = pYDNew;
			
		}
		
		bb.transformBy(at);
		
		makeHandleBox();
	}

	/**
	 * transformeer deze Streep m.b.v. een affiene transformatie Ax=Mx+b;
	 * alleen gebruikt in setState na creatie van de initiele Streep; 
	 * update het middelpunt, bepaal de affiene transformatie, de bounding box en 
	 * de handle box
	 * @param m00 M linksboven
	 * @param m01 M rechtsboven
	 * @param m10 M linksonder
	 * @param m11 M rechtsonder
	 * @param b0 x-coordinaat b
	 * @param b1 y-coordinaat b
	 */
	public void transformBy(double m00, double m01, double m10, double m11, double b0, double b1)
	{
		
		at = new AffineTransform(m00, m01, m10, m11, b0, b1);
		
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	
			double pXDNew = at.m00 * pXD[pCnt] + at.m01 * pYD[pCnt] + at.b0;
			double pYDNew = at.m10 * pXD[pCnt] + at.m11 * pYD[pCnt] + at.b1;
			pXD[pCnt] = pXDNew;
			pYD[pCnt] = pYDNew;
			
		}
		
		double cxNew = at.m00 * cx + at.m01 * cy + at.b0;
		double cyNew = at.m10 * cx + at.m11 * cy + at.b1;
		cx = cxNew;
		cy = cyNew;

		bb.transformBy(at);
		
		makeHandleBox();
	}
	
	/**
	 * teken deze Streep
	 * @param g Context2d om te tekenen
	 */
	public void teken(Context2d g)
	{
		
		g.setStrokeStyle(kleur);		
		
		if (puntenXD.length == 1)
		{	g.beginPath();
			g.strokeRect(pXD[0], pYD[0], 1, 1);
		}
		if (puntenXD.length > 1)
		{	
			g.beginPath();
			g.moveTo(pXD[0], pYD[0]);
			for (int pCnt = 1; pCnt < puntenXD.length; pCnt++)
			{	g.lineTo(pXD[pCnt], pYD[pCnt]);
			}
			g.stroke();
			
		}
		
	}

	/**
	 * teken de handle box van deze Streep
	 * @param g Context2d om te tekenen
	 */
	public void tekenHandleBox(Context2d g)
	{
		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.hbColor);
		
		g.strokeRect(handleBox.x, handleBox.y, handleBox.width, handleBox.height);
		
		g.setLineWidth(1.5d);
		
		tekenHandles(g);
	}
	
	/**
	 * teken de handles van de handle box van deze Streep,
	 * de handles zijn null als er niet geschaald/gedraaid mag worden
	 * @param g Context2d om te tekenen
	 */
	public void tekenHandles(Context2d g)
	{
		if (topRightHandle != null)
		{	
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			g.beginPath();
			g.moveTo(topRightHandle.doubleX[0], topRightHandle.doubleY[0]);
			for (int k = 1; k < topRightHandle.aantalPunten; k++) 
			{	g.lineTo(topRightHandle.doubleX[k], topRightHandle.doubleY[k]);
			}
			g.lineTo(topRightHandle.doubleX[0], topRightHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (topLeftHandle != null)
		{	
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			g.beginPath();
			g.moveTo(topLeftHandle.doubleX[0], topLeftHandle.doubleY[0]);
			for (int k = 1; k < topLeftHandle.aantalPunten; k++) 
			{	g.lineTo(topLeftHandle.doubleX[k], topLeftHandle.doubleY[k]);
			}
			g.lineTo(topLeftHandle.doubleX[0], topLeftHandle.doubleY[0]);
			g.closePath();
			g.stroke();

			
		}
		if (bottomRightHandle != null)
		{	
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			g.beginPath();
			g.moveTo(bottomRightHandle.doubleX[0], bottomRightHandle.doubleY[0]);
			for (int k = 1; k < bottomRightHandle.aantalPunten; k++) 
			{	g.lineTo(bottomRightHandle.doubleX[k], bottomRightHandle.doubleY[k]);
			}
			g.lineTo(bottomRightHandle.doubleX[0], bottomRightHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (bottomLeftHandle != null)
		{	
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			g.beginPath();
			g.moveTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
			for (int k = 1; k < bottomLeftHandle.aantalPunten; k++) 
			{	g.lineTo(bottomLeftHandle.doubleX[k], bottomLeftHandle.doubleY[k]);
			}
			g.lineTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
			g.closePath();
			g.stroke();
			
		}
		if (rotateEastHandle != null)
		{	
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			g.beginPath();
            g.arc(handleBox.x + handleBox.width + 2 * hbFactor, handleBox.y + handleBox.height/2, 2 * hbFactor, 0, 2 * Math.PI);
       	 	g.stroke();
		}
		if (rotateWestHandle != null)
		{	
			g.setStrokeStyle(KladjeGWTVeld.hbColor);
			g.beginPath();
            g.arc(handleBox.x - 2 * hbFactor, handleBox.y + handleBox.height/2, 2 * hbFactor, 0, 2 * Math.PI);
       	 	g.stroke();

		}

	}

	/**
	 * teken de bounding box van deze Streep
	 * @param g Context2d om te tekenen
	 */
	public void tekenBB(Context2d g)
	{
		
		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.bbColor);

		g.beginPath();
		g.moveTo(bb.doubleX[0], bb.doubleY[0]);
		for (int k = 1; k < bb.aantalPunten; k++) 
		{	g.lineTo(bb.doubleX[k], bb.doubleY[k]);
		}
		g.lineTo(bb.doubleX[0], bb.doubleY[0]);
		g.closePath();
		g.stroke();

			
		g.setLineWidth(1.5d);
	}


	/**
	 * check if the bounding box van deze Streep het punt (x,y) bevat
	 * @param x x-coordinaat te checken punt 
	 * @param y y-coordinaat te checken punt
	 * @return true/false
	 */
	public boolean bbContains(int x, int y)
	{

		return bb.contains(x, y);
	}
	
	/**
	 * verplaats deze Streep over de vector (dx,dy) 
	 * @param dx x-verplaatsing
	 * @param dy y-verplaatsing
	 */
	public void translate(int dx, int dy)
	{
		for (int pCnt = 0; pCnt < puntenXD.length; pCnt++)
		{	pXD[pCnt] += dx;
			pYD[pCnt] += dy; 
		}

		AffineTransform trans = new AffineTransform(1,0,0,1,dx,dy);
		at = at.leftMultiplyBy(trans);
		
		cx += dx;
		cy += dy;

		bb.translate(dx, dy);
		
		makeHandleBox();
	}
	
	/**
	 * check of deze Streep bevat is in een gegeven rechthoek;
	 * doe dit door te kijken of de bounding box in deze rechthoek past  
	 * @param r gegeven rechthoek
	 * @return true/false
	 */
	public boolean isContainedIn(Rectangle r)
	{
		if (r == null)
			return false;		
		
		boolean isContainedIn = true;
		
		for (int pCnt = 0; pCnt < bb.aantalPunten; pCnt++)
		{
			isContainedIn = isContainedIn && r.contains(bb.puntenX[pCnt], bb.puntenY[pCnt]);

		}
		
		return isContainedIn;
	}
}

