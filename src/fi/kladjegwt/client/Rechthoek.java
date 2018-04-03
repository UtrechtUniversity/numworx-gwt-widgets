package fi.kladjegwt.client;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * klasse die een rechthoek representeert; 
 * een rechthoek wordt als volgt getekend (zie ook klassen KladjeGWT en KladjeVeldGWT): <br>
 * ga in modus rechthoektekenen (togglebuttons), klik op het werkveld waar de linkerbovenhoek
 * van de rechthoek moet komen en sleep waarna een rechthoek verschijnt;
 * de rechthoek wordt gefixeerd bij het be-eindigen van de sleep;<br>  
 * NB: op de PC (niet op de tablet) levert slepen met de Shift-key ingedrukt een vierkant op;<br>
 * rechthoek verplaatsten, schalen, draaien of verwijderen: <br>
 * ga in modus selecteren (togglebuttons), klik op de rechthoek, waarna een handlebox
 * (met vier schaal handles op de hoeken en twee rotatie handles op de zijden) verschijnt; 
 * klikken plus slepen in de handlebox verplaatst de rechthoek, klikken plus slepen in de schaal handles
 * schaalt de rechthoek, klikken plus slepen in de rotatie handles draait de rechthoek, klikken op
 * de wisknop verwijderd de rechthoek;
 * een rechthoek wordt ook verplaatst, geschaald, gedraaid of verwijderd als deel van een geselecteerde
 * groep van figuren.<br>
 * een Rechthoek wordt als volgt "onthouden": onthoudt linkerbovenhoek van rechthoek bij creatie,
 * breedte en hoogte bij creatie, en stop alle wijzigingen (verplaatsingen, schalingen, draaiingen)
 * cumulatief in een affiene transformatie en onthoudt deze affiene transformatie     
 * @author huub
 */


public class Rechthoek 
{
	/**
	 * de kleur van deze Rechthoek
	 */
	CssColor kleur;
	
	/**
	 * de linkerbovenhoek van deze Rechthoek  
	 * toen deze Rechthoek gemaakt werd (dus voor veranderingen
	 * als draaing en verplaatsing)
	 */
	int topLeftX, topLeftY;
	/**
	 * breedte en hoogte van deze Rechthoek 
	 * toen deze Rechthoeks gemaakt werd (dus voor veranderingen
	 * als draaing en schaling) 
	 */
	int breedte, hoogte;
	
	/**
	 * afstand outerRechthoek en innerRechthoek
	 * tot deze Rechthoek
	 */
	int bbFactor = 4;
	/**
	 * deze Rechthoek
	 */
	Polygon rechthoek;

	/**
	 * een iets grotere en een iets kleinere Rechthoek
	 * om aan te geven dat deze Rechthoek deel uit maakt van
	 * een groepsselectie (dus een soort bounding rechthoek) 
	 */
	Polygon outerRechthoek, innerRechthoek;
	
	/**
	 * het middelpunt van deze Rechthoek
	 */
	double cx, cy;
	/**
	 * gebruik een affiene transformatie om cumulatief alle
	 * veranderingen (verplaatsing, schaling, draaing) aan deze
	 * Rechthoek op te slaan 
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
	 * kan deze Rechthoek verwijderd worden?
	 */
	boolean deletable = true;
	

	/**
	 * constructor, creeer meteen het middelpunt
	 * @param c kleur van de rechthoek
	 * @param x x-coordinaat linker bovenhoek
	 * @param y y-coordinaat linker bovenhoek
	 * @param w breedte
	 * @param h hoogte
	 */
	public Rechthoek(CssColor c, int x, int y, int w, int h)
	{
		kleur = c;
		topLeftX = x;
		topLeftY = y;
		breedte = w;
		hoogte = h;
		
		cx = topLeftX + ((double) breedte) / 2;
		cy = topLeftY + ((double) hoogte) / 2;
		
		maakRechthoek();
		
	}

	/**
	 * creeer het rechthoek-polygon, de bounding rechthoeken
	 * en de handle box
	 */
	public void maakRechthoek()
	{
		rechthoek = new Polygon();
		rechthoek.addPoint(topLeftX, topLeftY);
		rechthoek.addPoint(topLeftX + breedte, topLeftY);
		rechthoek.addPoint(topLeftX + breedte, topLeftY + hoogte);
		rechthoek.addPoint(topLeftX, topLeftY + hoogte);
		
		makeBB();
		
		makeHandleBox();
	}
	
	/**
	 * maak de handle box voor deze Rechthoek (minimum breedte en hoogte in KladjeVeldGWT)
	 * en voeg handles toe als er geschaald en/of geroteerd mag worden
	 */
	public void makeHandleBox()
	{
		int minX = 1000;
		int maxX = -100;
		int minY = 1000;
		int maxY = -100;
		for (int pCnt = 0; pCnt < outerRechthoek.aantalPunten; pCnt++)
		{
			if (outerRechthoek.puntenX[pCnt] < minX)
				minX = outerRechthoek.puntenX[pCnt];
			if (outerRechthoek.puntenX[pCnt] > maxX)
				maxX = outerRechthoek.puntenX[pCnt];
			if (outerRechthoek.puntenY[pCnt] < minY)
				minY = outerRechthoek.puntenY[pCnt];
			if (outerRechthoek.puntenY[pCnt] > maxY)
				maxY = outerRechthoek.puntenY[pCnt];
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
	 * maak de vier handels om the schalen en de bijbehorende klik-rechthoeken
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
		rotateEastHandle = new Rectangle(handleBox.x + handleBox.width, // - 2 * hbFactor,
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
	 * maak de twee rechthoek Polygonen voor de bounding rechthoek 
	 */
	public void makeBB()
	{
		outerRechthoek = new Polygon();
		outerRechthoek.addPoint(topLeftX - bbFactor, topLeftY - bbFactor);
		outerRechthoek.addPoint(topLeftX + breedte + bbFactor, topLeftY - bbFactor);
		outerRechthoek.addPoint(topLeftX + breedte + bbFactor, topLeftY + hoogte + bbFactor);
		outerRechthoek.addPoint(topLeftX - bbFactor, topLeftY + hoogte + bbFactor);
		
		innerRechthoek = new Polygon();
		innerRechthoek.addPoint(topLeftX + bbFactor, topLeftY + bbFactor);
		innerRechthoek.addPoint(topLeftX + breedte - bbFactor, topLeftY + bbFactor);
		innerRechthoek.addPoint(topLeftX + breedte - bbFactor, topLeftY + hoogte - bbFactor);
		innerRechthoek.addPoint(topLeftX + bbFactor, topLeftY + hoogte - bbFactor);		
		
	}

	/**
	 * draai deze Rechthoek over rotateStep radialen tegen de klok in met
	 * als draaicentrum het middelpunt van deze Rechthoek; update de affiene transformatie,
	 * de bounding rechthoek en de handle box  
	 * @param rotateStep draaihoek (radialen)
	 */
	public void rotate(double rotateStep)
	{	
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
				  Math.sin(rotateStep), Math.cos(rotateStep), 
				  cx - Math.cos(rotateStep) * cx + Math.sin(rotateStep) * cy, 
				  cy - Math.sin(rotateStep) * cx - Math.cos(rotateStep) * cy);
		at = at.leftMultiplyBy(rot);
		
		rechthoek.rotate(rotateStep, cx, cy);
		outerRechthoek.rotate(rotateStep, cx, cy);
		innerRechthoek.rotate(rotateStep, cx, cy);
		
		makeHandleBox();

	}

	/**
	 * draai deze Rechthoek over rotateStep radialen tegen de klok in met
	 * als draaicentrum het punt (dx,dy); update het middelpunt, de affiene transformatie,
	 * de bounding rechthoek en de handle box  
	 * @param rotateStep draaihoek (radialen)
	 * @param dx x-coordinaat draaicentrum
	 * @param dy y-coordinaat draaicentrum
	 */
	public void rotate(double rotateStep, double dx, double dy)
	{	

		double cxNew = Math.cos(rotateStep) * (cx - dx) - Math.sin(rotateStep) * (cy - dy);
		double cyNew = Math.sin(rotateStep) * (cx - dx) + Math.cos(rotateStep) * (cy - dy);
		cx = cxNew + dx;
		cy = cyNew + dy;
		
		AffineTransform rot = new AffineTransform(Math.cos(rotateStep),- Math.sin(rotateStep),
				  Math.sin(rotateStep), Math.cos(rotateStep), 
				  dx - Math.cos(rotateStep) * dx + Math.sin(rotateStep) * dy, 
				  dy - Math.sin(rotateStep) * dx - Math.cos(rotateStep) * dy);
		at = at.leftMultiplyBy(rot);

		rechthoek.rotate(rotateStep, dx, dy);
		outerRechthoek.rotate(rotateStep, dx, dy);
		innerRechthoek.rotate(rotateStep, dx, dy);
		
		makeHandleBox();

	}

	/**
	 * schaal deze Rechthoek vanuit het middelpunt met een factor scaleStep; update de affiene transformatie,
	 * de bounding rechthoek en de handle box 
	 * @param scaleStep schaalfactor
	 */
	public void scale(double scaleStep)
	{	

		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * cx, (1 - scaleStep) * cy);
		at = at.leftMultiplyBy(sc);
		
		rechthoek.scale(scaleStep, cx, cy);
		outerRechthoek.scale(scaleStep, cx, cy);
		innerRechthoek.scale(scaleStep, cx, cy);
		
		makeHandleBox();
		
	}
	
	/**
	 * schaal deze Rechthoek vanuit het punt (dx,dy) met een factor scaleStep; update het middelpunt, 
	 * de affiene transformatie, de bounding rechthoek en de handle box 
	 * @param scaleStep schaalfactor
	 * @param dx x-coordinaat schaalcentrum 
	 * @param dy y-coordinaat schaalcentrum
	 */
	public void scale(double scaleStep, double dx, double dy)
	{	
		cx = scaleStep * cx + (1 - scaleStep) * dx;
		cy = scaleStep * cy + (1 - scaleStep) * dy;
		
		AffineTransform sc = new AffineTransform(scaleStep, 0, 0, scaleStep, (1 - scaleStep) * dx, (1 - scaleStep) * dy);
		at = at.leftMultiplyBy(sc);

		rechthoek.scale(scaleStep, dx, dy);
		outerRechthoek.scale(scaleStep, dx, dy);
		innerRechthoek.scale(scaleStep, dx, dy);
		
		makeHandleBox();
		
	}

	/**
	 * schaal deze Rechthoek vanuit het middelpunt met een factor sx in de x-richting en een
	 * factor sy in de y-richting; update de affiene transformatie,
	 * de bounding rechthoek en de handle box 
	 * @param sx schaalfactor x-richting
	 * @param sy schaalfactor y-richting
	 */
	public void scale(double sx, double sy)
	{
		
		AffineTransform sc = new AffineTransform(sx, 0, 0, sy, (1 - sx) * cx, (1 - sy) * cy);
		at = at.leftMultiplyBy(sc);
		
		rechthoek.scale(sx, sy, cx, cy);
		outerRechthoek.scale(sx, sy, cx, cy);
		innerRechthoek.scale(sx,sy, cx, cy);
		
		makeHandleBox();
		
	}
	
	/**
	 * schaal deze Rechthoek vanuit het punt (dx,dy) met een factor sx in de x-richting en een
	 * factor sy in de y-richting; update het middelpunt, de affiene transformatie,
	 * de bounding rechthoek en de handle box 
	 * @param sx schaalfactor x-richting
	 * @param sy schaalfactor y-richting
	 * @param dx x-coordinaat schaalcentrum
	 * @param dy y-coordinaat schaalcentrum
	 */
	public void scale(double sx, double sy, double dx, double dy)
	{
		cx = sx * cx + (1 - sx) * dx;
		cy = sy * cy + (1 - sy) * dy;

		AffineTransform sc = new AffineTransform(sx, 0, 0, sy, (1 - sx) * dx, (1 - sy) * dy);
		at = at.leftMultiplyBy(sc);

		rechthoek.scale(sx, sy, dx, dy);
		outerRechthoek.scale(sx, sy, dx, dy);
		innerRechthoek.scale(sx,sy, dx, dy);
		
		makeHandleBox();
		
	}

	/**
	 * sla de state van deze Rechthoek op in een HashMap, dus 
	 * linkerbovenhoek van de rechthoek bij creatie, breedte en hoogte bij creatie, en
	 * de affiene transformatie met (cumulatief) alle wijzigingen 
	 * @return state HashMap
	 */
	public HashMap<String, Object> getState()
	{	HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("kleurgwt", kleur.value());
		h.put("topLeftX", new Integer(topLeftX));
		h.put("topLeftY", new Integer(topLeftY));
		h.put("breedte", new Integer(breedte));
		h.put("hoogte", new Integer(hoogte));

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
	 * lees de state van een Rechthoek uit een HashMap en creeer
	 * deze Rechthoek; houdt rekening met oudere versies
	 * @param map de HashMap waarin de state van een Rechthoek
	 * @return de Rechthoek  
	 */
	public static Rechthoek setState(Map<String, Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		CssColor kleur = KladjeGWTVeld.zwart;
		int topLeftX = 0; 
		int topLeftY = 0;
		int breedte = 0;
		int hoogte = 0;
		double rotation = 0;

		double m00 = 1;
		double m01 = 0;
		double m10 = 0;
		double m11 = 1;
		double b0 = 0;
		double b1 = 0;

		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make(h.getString("kleurgwt"));
		if (h.containsKey("topLeftX"))
			topLeftX = h.getInt("topLeftX");
		if (h.containsKey("topLeftY"))
			topLeftY = h.getInt("topLeftY");

		if (h.containsKey("breedte"))
			breedte = h.getInt("breedte");
		if (h.containsKey("hoogte"))
			hoogte = h.getInt("hoogte");
		
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


		
		Rechthoek rechthoek = new Rechthoek(kleur, topLeftX, topLeftY, breedte, hoogte);
		rechthoek.maakRechthoek();

		// oudste versie
		if (h.containsKey("rotation"))
		{	rechthoek.rotate(rotation);
		}
		// nieuwste versie
		else if (h.containsKey("b0"))
		{	rechthoek.transformBy(m00, m01, m10, m11, b0, b1);
		}
		else // tussenversie
		{	rechthoek.transformBy(m00, m01, m10, m11);
		}
		
		return rechthoek;
	}

	/**
	 * transformeer deze Rechthoek m.b.v. een lineaire transformatie M;
	 * alleen gebruikt in setState na creatie van de initiele Rechthoek; 
	 * bepaal de affiene transformatie, de bounding rechthoek en 
	 * de handle box
	 * @param m00 M linksboven
	 * @param m01 M rechtsboven
	 * @param m10 M linksonder
	 * @param m11 M rechtsonder
	 */
	public void transformBy(double m00, double m01, double m10, double m11)
	{
		
		at = new AffineTransform(m00, m01, m10, m11, - m00*cx - m01*cy + cx, - m10*cx - m11*cy + cy);
		
		rechthoek.transformBy(m00, m01, m10, m11, cx, cy);
		outerRechthoek.transformBy(m00, m01, m10, m11, cx, cy);
		innerRechthoek.transformBy(m00, m01, m10, m11, cx, cy);

		makeHandleBox();
		
	}
	
	/**
	 * transformeer deze Rechthoek m.b.v. een affiene transformatie Ax=Mx+b;
	 * alleen gebruikt in setState na creatie van de initiele Rechthoek; 
	 * update het middelpunt, bepaal de affiene transformatie, de bounding rechthoek en 
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
		
		double cxNew = at.m00 * cx + at.m01 * cy + at.b0;
		double cyNew = at.m10 * cx + at.m11 * cy + at.b1;
		cx = cxNew;
		cy = cyNew;

		rechthoek.transformBy(at);
		outerRechthoek.transformBy(at);
		innerRechthoek.transformBy(at);
		
		makeHandleBox();
		
	}
	
	/**
	 * teken deze Rechthoek
	 * @param g Context2d om te tekenen
	 */
	public void teken(Context2d g)
	{
		g.setStrokeStyle(kleur);		

		g.beginPath();		
		g.moveTo(rechthoek.doubleX[0], rechthoek.doubleY[0]);
		for (int k = 1; k < rechthoek.aantalPunten; k++) 
		{	g.lineTo(rechthoek.doubleX[k], rechthoek.doubleY[k]);
		}
		g.lineTo(rechthoek.doubleX[0], rechthoek.doubleY[0]);
		g.closePath();
		g.stroke();
		
	}

	/**
	 * teken de handle box en de handles van deze Rechthoek
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
	 * teken de handles van de handle box van deze Rechthoek,
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
	 * tekend e bounding rechthoek van deze Rechthoek (twee rechthoeken dus)
	 * @param g Context2d om te tekenen
	 */
	public void tekenBB(Context2d g)
	{
		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.bbColor);
		
		g.beginPath();
		g.moveTo(outerRechthoek.doubleX[0], outerRechthoek.doubleY[0]);
		for (int k = 1; k < outerRechthoek.aantalPunten; k++) 
		{	g.lineTo(outerRechthoek.doubleX[k], outerRechthoek.doubleY[k]);
		}
		g.lineTo(outerRechthoek.doubleX[0], outerRechthoek.doubleY[0]);
		g.closePath();
		g.stroke();

		g.beginPath();		
		g.moveTo(innerRechthoek.doubleX[0], innerRechthoek.doubleY[0]);
		for (int k = 1; k < innerRechthoek.aantalPunten; k++) 
		{	g.lineTo(innerRechthoek.doubleX[k], innerRechthoek.doubleY[k]);
		}
		g.lineTo(innerRechthoek.doubleX[0], innerRechthoek.doubleY[0]);
		g.closePath();
		g.stroke();
		
		g.setLineWidth(1.5d);
		
	}

	/**
	 * check of de bounding rechthoek het punt (x,y) bevat, d.w.z. 
	 * het punt light in de outerRechthoek en niet in de innerRechthoek 
	 * @param x x-coordinaat te checken punt
	 * @param y y-coordinaat te checken punt
	 * @return true/false
	 */
	public boolean bbContains(int x, int y)
	{
		
		return outerRechthoek.contains(x, y) && !innerRechthoek.contains(x, y);
	}

	/**
	 * verplaats deze Rechthoek over de vector (dx,dy); update het middelpunt,
	 * de affiene transformatie, de bounding rechthoek en de handle box
	 * @param dx verplaatsing in x-richting
	 * @param dy verplaatsing in y-richting
	 */
	public void translate(int dx, int dy)
	{
		cx += dx;
		cy += dy;
		
		AffineTransform trans = new AffineTransform(1,0,0,1,dx,dy);
		at = at.leftMultiplyBy(trans);

		rechthoek.translate(dx, dy);
		outerRechthoek.translate(dx, dy);
		innerRechthoek.translate(dx, dy);
		
		makeHandleBox();
		
	}	

	/**
	 * check of deze Rechthoek bevat is in een gegeven rechthoek
	 * @param r gegeven rechthoek
	 * @return true/false
	 */
	public boolean isContainedIn(Rectangle r)
	{
		if (r == null)
			return false;

		boolean isContainedIn = true;
		for (int cnt = 0; cnt < outerRechthoek.aantalPunten; cnt++)
		{
			isContainedIn = isContainedIn && 
							r.contains(outerRechthoek.puntenX[cnt], outerRechthoek.puntenY[cnt]);
		}
		
		return isContainedIn;
	}

}
