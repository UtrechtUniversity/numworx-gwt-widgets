package fi.kladjegwt.client;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * klasse die een lijn representeert; 
 * een lijn wordt als volgt getekend (zie ook klassen KladjeGWT en KladjeVeldGWT): <br>
 * ga in modus lijntekenen (togglebuttons), klik op het werkveld waar het beginpunt van de 
 * lijn moet komen en sleep waarna een lijn verschijnt;
 * de lijn wordt gefixeerd bij het be-eindigen van de sleep;<br>  
 * NB: op de PC (niet op de tablet) levert slepen met de Shift-key ingedrukt horizontale, vertikale
 * of diagonale lijnen op;<br>
 * lijn verplaatsten, schalen, draaien of verwijderen: <br>
 * ga in modus selecteren (togglebuttons), klik op de lijn, waarna een handlebox
 * (met vier schaal handles op de hoeken en twee rotatie handles op de zijden) verschijnt; 
 * klikken plus slepen in de handlebox verplaatst de lijn, klikken plus slepen in de schaal handles
 * schaalt de lijn, klikken plus slepen in de rotatie handles draait de lijn, klikken op
 * de wisknop verwijderd de lijn;
 * een lijn wordt ook verplaatst, geschaald, gedraaid of verwijderd als deel van een geselecteerde
 * groep van figuren.<br>
 * een Lijn wordt als volgt "onthouden": onthoudt de coordinaten van begin- en einpunt bij creatie,
 * en stop alle wijzigingen (verplaatsingen, schalingen, draaiingen)
 * cumulatief in een affiene transformatie en onthoudt deze affiene transformatie     
 * @author huub
 */
public class Lijn 
{
	/**
	 * de kleur van deze Lijn
	 */
	CssColor kleur;
	/**
	 * initiele coordinaten begin- en eindpunt
	 */
	int fromX, fromY, toX, toY;
	/**
	 * actuele coordinaten begin- en eindpunt (dus na verplaatsing, draaiing en schaling)
	 */
	double fX, fY, tX, tY;
	/**
	 * dikte van de bounding box van deze Lijn
	 */
	int bbFactor = 4;
	/**
	 * de bounding box van deze Lijn
	 */
	Polygon bb;

	/**
	 * het middlepunt van deze Lijn
	 */
	double cx, cy;
	
	/**
	 * gebruik een affiene transformatie om cumulatief alle
	 * veranderingen (verplaatsing, schaling, draaing) aan deze
	 * Streep op te slaan 
	 */
	AffineTransform at = new AffineTransform();
	
	/** 
	 * de handle box van deze Lijn
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
	 * kan deze Lijn verwijderd worden?
	 */
	boolean deletable = true;
	
	/**
	 * constructor, vindt meteen het middelpunt
	 * @param c de kleur van de Lijn
	 * @param fromX x-coordinaat beginpunt
	 * @param fromY y-coordinaat beginpunt
	 * @param toX x-coordinaat eindpunt
	 * @param toY y-coordinaat eindpunt
	 */
	public Lijn(CssColor c, int fromX, int fromY, int toX, int toY)
	{
		kleur = c;
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;

		cx = ((double) fromX + (double) toX) / 2;
		cy = ((double) fromY + (double) toY) / 2;
		
		maakLijn();
	}

	/**
	 * kopier initiele begin- en eindcoordinaten in actuele
	 * begin- en eindcoordinaten, maak de bouding box en de handle box
	 */
	public void maakLijn()
	{
		fX = fromX;
		fY = fromY;
		tX = toX;
		tY = toY;
				
		makeBB();
		
		makeHandleBox();
	}
	
	/**
	 * maak de handle box voor deze Lijn (minimum breedte en hoogte in KladjeVeldGWT)
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
	 * maak een bounding box voor deze Lijn
	 */
	public void makeBB()
	{
		bb = new Polygon();
		
		// richtingsvector
		double rX = toX - fromX;
		double rY = toY - fromY;
		// normaalvector
		double nx = 0;
		double ny = 0;
		if (Math.abs(rX) > Math.abs(rY))
		{	nx = rY;
			ny = -rX;
		}
		else
		{	nx = -rY;
			ny = rX;
		}
		// eenheids normaalvector
		double nl = Math.sqrt(nx * nx + ny * ny);
		if (nl > 0)
		{	// eerste punt
			double px = fromX + nx * bbFactor / nl;
			double py = fromY + ny * bbFactor / nl;
			bb.addPoint(px, py); 
			// tweede punt
			px = toX + nx * bbFactor / nl;
			py = toY + ny * bbFactor / nl;
			bb.addPoint(px, py);
			// derde punt
			px = toX - nx * bbFactor / nl;
			py = toY - ny * bbFactor / nl;
			bb.addPoint(px, py);
			// vierde punt
			px = fromX - nx * bbFactor / nl;
			py = fromY - ny * bbFactor / nl;
			bb.addPoint(px, py);
		
		
		}
		else // doosje
		{
			bb.addPoint(fromX - bbFactor, fromY - bbFactor);
			bb.addPoint(fromX + bbFactor, fromY - bbFactor);
			bb.addPoint(fromX + bbFactor, fromY + bbFactor);
			bb.addPoint(fromX - bbFactor, fromY + bbFactor);
		}
	}

	/**
	 * draai deze Lijn over rotateStep radialen tegen de klok in met
	 * als draaicentrum het middelpunt van deze Lijn; update de affiene transformatie,
	 * de bounding box en de handle box  
	 * @param rotateStep draaihoek (radialen)
	 */
	public void rotate(double rotateStep)
	{		
		double fXNew = Math.cos(rotateStep) * (fX - cx) - Math.sin(rotateStep) * (fY - cy);
		double fYNew = Math.sin(rotateStep) * (fX - cx) + Math.cos(rotateStep) * (fY - cy);
		double tXNew = Math.cos(rotateStep) * (tX - cx) - Math.sin(rotateStep) * (tY - cy);
		double tYNew = Math.sin(rotateStep) * (tX - cx) + Math.cos(rotateStep) * (tY - cy);
		fX = fXNew + cx;
		fY = fYNew + cy;
		tX = tXNew + cx;
		tY = tYNew + cy;
	
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
				  Math.sin(rotateStep), Math.cos(rotateStep), 
				  cx - Math.cos(rotateStep) * cx + Math.sin(rotateStep) * cy, 
				  cy - Math.sin(rotateStep) * cx - Math.cos(rotateStep) * cy);
		at = at.leftMultiplyBy(rot);

		bb.rotate(rotateStep, cx, cy);
		
		makeHandleBox();
	
	}

	/**
	 * draai deze Lijn over rotateStep radialen tegen de klok in met
	 * als draaicentrum het punt (dx,dy); update het middelpunt, de affiene transformatie,
	 * de bounding rechthoek en de handle box  
	 * @param rotateStep draaihoek (radialen)
	 * @param dx x-coordinaat draaicentrum
	 * @param dy y-coordinaat draaicentrum
	 */
	public void rotate(double rotateStep, double dx, double dy)
	{		
		double fXNew = Math.cos(rotateStep) * (fX - dx) - Math.sin(rotateStep) * (fY - dy);
		double fYNew = Math.sin(rotateStep) * (fX - dx) + Math.cos(rotateStep) * (fY - dy);
		double tXNew = Math.cos(rotateStep) * (tX - dx) - Math.sin(rotateStep) * (tY - dy);
		double tYNew = Math.sin(rotateStep) * (tX - dx) + Math.cos(rotateStep) * (tY - dy);
		fX = fXNew + dx;
		fY = fYNew + dy;
		tX = tXNew + dx;
		tY = tYNew + dy;
		
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
	 * schaal deze Lijn vanuit het middelpunt met een factor scaleStep; update de affiene transformatie,
	 * de bounding box en de handle box 
	 * @param scaleStep schaalfactor
	 */
	public void scale(double scaleStep)
	{	
		
		fX = scaleStep * fX + (1 - scaleStep) * cx;
		fY = scaleStep * fY + (1 - scaleStep) * cy;
		tX = scaleStep * tX + (1 - scaleStep) * cx;
		tY = scaleStep * tY + (1 - scaleStep) * cy;
		
		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * cx, (1 - scaleStep) * cy);
		at = at.leftMultiplyBy(sc);

		bb.scale(scaleStep, scaleStep, cx, cy);
		
		makeHandleBox();
		
	}

	/**
	 * schaal deze Lijn vanuit het punt (dx,dy) met een factor scaleStep; update het middelpunt, 
	 * de affiene transformatie, de bounding box en de handle box 
	 * @param scaleStep schaalfactor
	 * @param dx x-coordinaat schaalcentrum 
	 * @param dy y-coordinaat schaalcentrum
	 */
	public void scale(double scaleStep, double dx, double dy)
	{	
		fX = scaleStep * fX + (1 - scaleStep) * dx;
		fY = scaleStep * fY + (1 - scaleStep) * dy;
		tX = scaleStep * tX + (1 - scaleStep) * dx;
		tY = scaleStep * tY + (1 - scaleStep) * dy;
		
		cx = scaleStep * cx + (1 - scaleStep) * dx;
		cy = scaleStep * cy + (1 - scaleStep) * dy;
		
		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * dx, (1 - scaleStep) * dy);
		at = at.leftMultiplyBy(sc);
		
		bb.scale(scaleStep, scaleStep, dx, dy);

		makeHandleBox();
		
	}

	/**
	 * schaal deze Lijn vanuit het middelpunt met een factor sx in de x-richting en een
	 * factor sy in de y-richting; update de affiene transformatie,
	 * de bounding box en de handle box 
	 * @param sx schaalfactor x-richting
	 * @param sy schaalfactor y-richting
	 */
	public void scale(double sx, double sy)
	{	
		fX = sx * fX + (1 - sx) * cx;
		fY = sy * fY + (1 - sy) * cy;
		tX = sx * tX + (1 - sx) * cx;
		tY = sy * tY + (1 - sy) * cy;

		AffineTransform sc = new AffineTransform(sx, 0, 0, sy, (1 - sx) * cx, (1 - sy) * cy);
		at = at.leftMultiplyBy(sc);
		
		bb.scale(sx, sy, cx, cy);
		
		makeHandleBox();
		
	}
	
	/**
	 * schaal deze Lijn vanuit het punt (dx,dy) met een factor sx in de x-richting en een
	 * factor sy in de y-richting; update het middelpunt, de affiene transformatie,
	 * de bounding box en de handle box 
	 * @param sx schaalfactor x-richting
	 * @param sy schaalfactor y-richting
	 * @param dx x-coordinaat schaalcentrum
	 * @param dy y-coordinaat schaalcentrum
	 */
	public void scale(double sx, double sy, double dx, double dy)
	{	
		fX = sx * fX + (1 - sx) * dx;
		fY = sy * fY + (1 - sy) * dy;
		tX = sx * tX + (1 - sx) * dx;
		tY = sy * tY + (1 - sy) * dy;
		
		cx = sx * cx + (1 - sx) * dx;
		cy = sy * cy + (1 - sy) * dy;
		
		AffineTransform sc = new AffineTransform(sx, 0, 0, sy, (1 - sx) * dx, (1 - sy) * dy);
		at = at.leftMultiplyBy(sc);

		bb.scale(sx, sy, dx, dy);
		
		makeHandleBox();
		
	}

	/**
	 * sla de state van deze Lijn op in een HashMap, dus x- en y coordinaten  
	 * initieel begin- en eindpunt en
	 * de affiene transformatie met (cumulatief) alle wijzigingen 
	 * @return state HashMap
	 */
	public HashMap<String, Object> getState()
	{	HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("kleurgwt", kleur.value());
		h.put("fromX", new Integer(fromX));
		h.put("fromY", new Integer(fromY));
		h.put("toX", new Integer(toX));
		h.put("toY", new Integer(toY));

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
	 * lees de state van een Lijn uit een HashMap en creeer
	 * deze Streep; houdt rekening met oudere versies
	 * @param map de HashMap waarin de state van een Lijn
	 * @return de Lijn  
	 */
	public static Lijn setState(Map<String, Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		CssColor kleur = KladjeGWTVeld.zwart;
		int fromX = 0; 
		int fromY = 0;
		int toX = 0;
		int toY = 0;
		double rotation = 0;

		double m00 = 1;
		double m01 = 0;
		double m10 = 0;
		double m11 = 1;
		double b0 = 0;
		double b1 = 0;

		
		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make(h.getString("kleurgwt"));
		if (h.containsKey("fromX"))
			fromX = h.getInt("fromX");
		if (h.containsKey("fromY"))
			fromY = h.getInt("fromY");

		if (h.containsKey("toX"))
			toX = h.getInt("toX");
		if (h.containsKey("toY"))
			toY = h.getInt("toY");

		if (h.containsKey("rotation"))
			rotation = h.getDouble("rotation");

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

		
		Lijn lijn = new Lijn(kleur, fromX, fromY, toX, toY);
		lijn.maakLijn();

		// oudste versie
		if (h.containsKey("rotation"))
		{	lijn.rotate(rotation);
		}
		// nieuwste versie
		else if (h.containsKey("b00"))
		{	lijn.transformBy(m00, m01, m10, m11, b0, b1);
		}
		else // tussenversie
		{	lijn.transformBy(m00, m01, m10, m11);
		}
		
		return lijn;
	}

	/**
	 * transformeer deze Lijn m.b.v. een lineaire transformatie M;
	 * alleen gebruikt in setState na creatie van de initiele Lijn; 
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

		double fXNew = at.m00 * fX + at.m01 * fY + at.b0;
		double fYNew = at.m10 * fX + at.m11 * fY + at.b1;
		fX = fXNew;
		fY = fYNew;
		
		double tXNew = at.m00 * tX + at.m01 * tY + at.b0;
		double tYNew = at.m10 * tX + at.m11 * tY + at.b1;
		tX = tXNew;
		tY = tYNew;

		bb.transformBy(at);
		
		makeHandleBox();
		
		
	}
	
	/**
	 * transformeer deze Lijn m.b.v. een affiene transformatie Ax=Mx+b;
	 * alleen gebruikt in setState na creatie van de initiele Lijn; 
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

		double fXNew = at.m00 * fX + at.m01 * fY + at.b0;
		double fYNew = at.m10 * fX + at.m11 * fY + at.b1;
		fX = fXNew;
		fY = fYNew;
		
		double tXNew = at.m00 * tX + at.m01 * tY + at.b0;
		double tYNew = at.m10 * tX + at.m11 * tY + at.b1;
		tX = tXNew;
		tY = tYNew;

		bb.transformBy(at);

		double cxNew = at.m00 * cx + at.m01 * cy + at.b0;
		double cyNew = at.m10 * cx + at.m11 * cy + at.b1;
		cx = cxNew;
		cy = cyNew;

		makeHandleBox();
		
	}

	/**
	 * teken deze Lijn
	 * @param g Context2d om te tekenen
	 */
	public void teken(Context2d g)
	{
		g.setStrokeStyle(kleur);
		g.beginPath();
		g.moveTo(fX, fY);
		g.lineTo(tX, tY);
		g.stroke();

	}

	/**
	 * teken de handle box van deze Lijn
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
	 * teken de handles van de handle box van deze Lijn,
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
	 * teken de bounding box van deze Lijn
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
	 * check if the bounding box van deze Lijn het punt (x,y) bevat
	 * @param x x-coordinaat te checken punt 
	 * @param y y-coordinaat te checken punt
	 * @return true/false
	 */
	public boolean bbContains(int x, int y)
	{
		return bb.contains(x, y);
	}

	/**
	 * verplaats deze Lijn over de vector (dx,dy) 
	 * @param dx x-verplaatsing
	 * @param dy y-verplaatsing
	 */
	public void translate(int dx, int dy)
	{
		cx += dx;
		cy += dy;
		
		fX += dx;
		fY += dy;
		tX += dx; 
		tY += dy;

		AffineTransform trans = new AffineTransform(1,0,0,1,dx,dy);
		at = at.leftMultiplyBy(trans);

		bb.translate(dx, dy);

		makeHandleBox();

		
	}	

	/**
	 * check of deze Lijn bevat is in een gegeven rechthoek;
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
			isContainedIn = isContainedIn && r.contains(bb.puntenX[pCnt], bb.puntenY[pCnt]);		}
		
		return isContainedIn;
	}	

}
