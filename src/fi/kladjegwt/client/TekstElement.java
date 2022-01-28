package fi.kladjegwt.client;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * klasse die een stukje tekst representeert; 
 * tekst wordt als volgt ingevoerd (zie ook klassen KladjeGWT en KladjeVeldGWT): <br>
 * ga in modus tekstekenen (togglebuttons), klik op het werkveld waar de tekst
 * moet komen, er verschijnt dan een popup met daarin een textbox; voer de gewenste
 * tekst en en druk op enter; verander de tekst door in modus teksttekenen op de
 * tekst te klikken zodat een popup opent met de tekst erin<br>
 * tekst verplaatsten, schalen en of verwijderen: <br>
 * ga in modus selecteren (togglebuttons), klik op de tekst, waarna een handlebox
 * (met een handle rechtsonder) verschijnt; klikken plus slepen in de handlebox 
 * verplaatst de tekst, klikken plus slepen in de handle schaalt de tekst;
 * klikken op de wisknop verwijdert de tekst.
 * tekst kan i.t.t. andere figuren niet gedraaid worden; als een TekstElement geselecteerd is
 * als deel van een geselecteerde groep van figuren, dan wordt het wel verplaatst als de
 * groep verplaatst wordt, maar niet geschaald of geroteerd als de groep geschaald of 
 * geroteerd wordt.<br>
 * een TekstElement wordt als volgt "onthouden": onthoudt de tekst, coordinaten linksboven van de bounding box, 
 * de breedte en hoogte en de schaalfactoren in x- en y-richting. 
 * @author huub
 */

public class TekstElement 
{
	/**
	 * de kleur van de tekst
	 */
	CssColor kleur;
	/**
	 * de actuele tekst
	 */
	String tekst;
	/**
	 * coordinaten linksboven van de bounding box van dit TekstElement
	 */
	int xPos, yPos;
	/**
	 * afmetingen van de bounding box van dit TekstElement
	 */
	int breedte, hoogte;
	/**
	 * de bounding box van dit TekstElement
	 */
	Polygon bb2;
	/**
	 * het middelpunt van de bounding box van dit TekstElement
	 */
	double cx, cy;
	
	/**
	 * schaalfactor x-richting
	 */
	double scaleX = 1;
	/**
	 * schaalfactor y-richting
	 */
	double scaleY = 1;
	
	/**
	 * de coordinaten waar de tekst getekend moet worden, 
	 * by schaling niet hetzelfde als xPos,yPos, zie
	 * methoden scale en teken
	 *  
	 */
	int tekstX, tekstY;
	
	/**
	 * de handle box van dit TekstElement
	 */
	Rectangle handleBox;
	
	/**
	 * aantal pixels offset voor de handlebox
	 */
	int hbFactor = 4;
	
	/**
	 * handvat rechts onder voor schalen
	 */
	Polygon bottomRightHandle;
	/**
	 * klikken en slepen op het handvat rechts onder
	 */
	Rectangle bottomRightRect;

	/**
	 * kan dit TekstElement gewist worden?
	 */
	boolean deletable = true;

	/**
	 * contructor
	 * @param c kleur van het TekstElement
	 * @param t tekst van het TekstElement
	 * @param x linksboven x-ccordinaat
	 * @param y linksboven y-coordinaat
	 */
	public TekstElement(CssColor c, String t, int x, int y)
	{
		kleur = c;
		tekst = new String(t);
		xPos = x;
		yPos = y;
		tekstX = x;
		tekstY = y;
				
	}

	/**
	 * zet de tekst van dit TekstElement
	 * @param t de nieuwe tekst
	 * @param g Context2d om tekstbreedte te kunnen bepalen
	 */
	public void zetTekst(String t, Context2d g)
	{
		tekst = new String(t);
		TextMetrics tm = g.measureText(tekst);
		breedte = (int) Math.round(tm.getWidth());
		hoogte = 16;
		breedte = (int) Math.round(scaleX * breedte);
		hoogte = (int) Math.round(scaleY * hoogte);
		// nieuw bounding box, dit crert nieuwe handle box
		makeBB();
		
	}

	/**
	 * bereken centrum en maak de bounding box van dit TekstElement 
	 */
	public void makeBB()
	{
		
		cx = xPos + ((double) breedte) / 2;
		cy = yPos + ((double) hoogte) / 2;
		bb2 = new Polygon();
		bb2.addPoint(xPos, yPos);
		bb2.addPoint(xPos + breedte, yPos);
		bb2.addPoint(xPos + breedte, yPos + hoogte);
		bb2.addPoint(xPos, yPos + hoogte);

		makeHandleBox();
	}

	/**
	 * maak de handle box voor dit TekstElement (minimum breedte en hoogte
	 * zijn gedefinieerd in KladjeVeldGWT) en maak de schaal handle
	 * rechtsonder als er geschaald mag worden
	 */
	public void makeHandleBox()
	{

		int minX = 1000;
		int maxX = -100;
		int minY = 1000;
		int maxY = -100;
		for (int pCnt = 0; pCnt < bb2.aantalPunten; pCnt++)
		{
			if (bb2.puntenX[pCnt] < minX)
				minX = bb2.puntenX[pCnt];
			if (bb2.puntenX[pCnt] > maxX)
				maxX = bb2.puntenX[pCnt];
			if (bb2.puntenY[pCnt] < minY)
				minY = bb2.puntenY[pCnt];
			if (bb2.puntenY[pCnt] > maxY)
				maxY = bb2.puntenY[pCnt];
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
	}

	/**
	 * om the schalen, gebruik alleen het handvat rechts onder
	 * maak het handvat rechts onder en de rechthoek rechts onder
	 */
	public void makeScaleHandles()
	{
		bottomRightHandle = new Polygon();
		bottomRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								   handleBox.y + handleBox.height + hbFactor);
		bottomRightHandle.addPoint(handleBox.x + handleBox.width - 3 * hbFactor,
								   handleBox.y + handleBox.height + hbFactor);
		bottomRightHandle.addPoint(handleBox.x + handleBox.width + hbFactor,
								   handleBox.y + handleBox.height - 3 * hbFactor);
		bottomRightRect = new Rectangle(handleBox.x + handleBox.width - 3 * hbFactor,
				   						handleBox.y + handleBox.height - 3 * hbFactor, 4 * hbFactor, 4 * hbFactor);		
	}

	/**
	 * zet het handvat rechts onder en de rechthoek rechts onder op null
	 */
	public void killScaleHandles()
	{
		bottomRightHandle = null; 
		bottomRightRect = null;
	}

	public void setColor(CssColor kleur) {
		this.kleur = kleur;
	}
	
	/**
	 * schaal dit TekstElement; NB: xPos en yPos onveranderd,
	 * tekstX en tekstY veranderen ! 
	 * @param scaleStep de schaalfactor
	 */
	public void scale(double scaleStep)
	{	

		scaleX *= scaleStep;
		scaleY *= scaleStep;
		
		breedte = (int) Math.round(scaleStep * breedte);
		hoogte = (int) Math.round(scaleStep * hoogte);

		tekstX = (int) Math.round((1/scaleStep) * tekstX);
		tekstY = (int) Math.round((1/scaleStep) * tekstY);

	}
	
	/**
	 * schaal dit TekstElement; NB: xPos en yPos onveranderd,
	 * tekstX en tekstY veranderen !
	 * @param sx de schaalfactor in de x-richting
	 * @param sy de schaalfactor in de y-richting
	 */
	public void scale(double sx, double sy)
	{	
		
		scaleX *= sx;
		scaleY *= sy;
		
		breedte = (int) Math.round(sx * breedte);
		hoogte = (int) Math.round(sy * hoogte);
	
		tekstX = (int) Math.round((1/sx) * tekstX);
		tekstY = (int) Math.round((1/sy) * tekstY);
		

	}

	/**
	 * stop de status van dit TekstElement in een HashMap;
	 * save breedte en hoogte als bGWT en hGWT om verschil te
	 * maken tussen een HashMap afkomstig uit
	 * de DWO en een HashMap afkomstig uit de DWOPlayer
	 * @return de status HashMap
	 */
	public HashMap<String, Object> getState()
	{	
		HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("kleurgwt", kleur.value());
		h.put("tekst", new String(tekst));
		h.put("xPos", new Integer(xPos));
		h.put("yPos", new Integer(yPos));
		
		h.put("scaleX", new Double(scaleX));
		h.put("scaleY", new Double(scaleY));
		
		h.put("bGWT", new Integer(breedte));
		h.put("hGWT", new Integer(hoogte));
				
		return h;
	}

	/**
	 * creeer een TekstElement uit status-data; voor de breedte,
	 * maak een verschil tussen een HashMap afkomstig uit
	 * de DWO en een HashMap afkomstig uit de DWOPlayer     
	 * @param map HashMap met status data
	 * @return het TekstElement
	 */
	public static TekstElement setState(Map<String, Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		CssColor kleur = KladjeGWTVeld.zwart;
		String tekst = new String("");
		int xPos = 0;
		int yPos = 0;
		double rotation = 0;
		double scaleX = 10;
		double scaleY = 10;
		
		double breedteGWT = 0;
		double hoogteGWT = 0;
		
		if (h.containsKey("kleurgwt"))
			kleur = CssColor.make(h.getString("kleurgwt"));
		if (h.containsKey("tekst"))
			tekst = h.getString("tekst");
		if (h.containsKey("xPos"))
			xPos = h.getInt("xPos");
		if (h.containsKey("yPos"))
			yPos = h.getInt("yPos");
		
		if (h.containsKey("scaleX"))
			scaleX = h.getDouble("scaleX");
		if (h.containsKey("scaleY"))
			scaleY = h.getDouble("scaleY");

		if (h.containsKey("bGWT"))
			breedteGWT = h.getInt("bGWT");
		if (h.containsKey("hGWT"))
			hoogteGWT = h.getInt("hGWT");
		
		TekstElement tekstElement = new TekstElement(kleur, tekst, xPos, yPos);

		// status was opgeslagen in GWT
		if (breedteGWT != 0)
		{
			tekstElement.breedte = (int) Math.round(breedteGWT / scaleX);
			tekstElement.hoogte = (int) Math.round(hoogteGWT / scaleY);
		}
		else // status komt uit launchdata
		{
			// dit bepaalt de breedte
			//tekstElement.zetTekst(tekst, KladjeGWTVeld.gIm);
			// ad hoc correctie
			tekstElement.breedte += 15;
		}
			
		tekstElement.cx = xPos + ((double) tekstElement.breedte) / 2;
		tekstElement.cy = yPos + ((double) tekstElement.hoogte) / 2;
			
		tekstElement.scale(scaleX, scaleY);
		
		return tekstElement;
	}

	/**
	 * bereken het middelpunt van dit TekstElement
	 */
	public void setCenter()
	{
		cx = (bb2.geefPuntX(0) + bb2.geefPuntX(2)) / 2;
		cy = (bb2.geefPuntY(0) + bb2.geefPuntY(2)) / 2;
	}

	/**
	 * teken dit TekstElement: 
	 * @param g Context2d om te tekenen
	 */
	public void teken(Context2d g)
	{
		String fontString = "bold 14px arial, sans-serif";
		g.setFont(fontString);
		
		makeBB();
		// redundant
		makeHandleBox();

		// schaal de Context2d
		g.scale(scaleX, scaleY);
		
		g.setTextBaseline(Context2d.TextBaseline.BOTTOM);
		g.setTextAlign(Context2d.TextAlign.START);
		
		g.setFillStyle(kleur);
		
		g.fillText(tekst, tekstX, tekstY + 15, breedte);

		// schaal de Context2d weer terug
		g.scale(1/scaleX, 1/scaleY);
		
	}

	/**
	 * teken de handle box van dit TekstElement
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
	 * teken de handvaten van de handle box van dit TekstElement
	 * er is alleen een handvat recht onder
	 * @param g Context2d om te tekenen
	 */
	public void tekenHandles(Context2d g)
	{
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
	}
	
	/**
	 * teken de bounding box van dit TekstElement
	 * @param g Context2d om te tekenen
	 */
	public void tekenBB(Context2d g)
	{
		g.setLineWidth(0.8d);
		g.setStrokeStyle(KladjeGWTVeld.bbColor);
		g.beginPath();
		g.moveTo(bb2.doubleX[0], bb2.doubleY[0]);
		for (int k = 1; k < bb2.aantalPunten; k++) 
		{	g.lineTo(bb2.doubleX[k], bb2.doubleY[k]);
		}
		g.lineTo(bb2.doubleX[0], bb2.doubleY[0]);
		g.closePath();
		g.stroke();
		g.setLineWidth(1.5d);

	}


	/**
	 * check of de bounding box van dit TekstElement het punt (x,y) bevat
	 * @param x x-coordinaat van het punt
	 * @param y y-coordinaat van het punt
	 * @return true/false
	 */
	public boolean bbContains(int x, int y)
	{
		return bb2.contains(x, y);
	}

	/**
	 * verschuif dit TekstElement over de vector (dx,dy) 
	 * @param dx x-translatie
	 * @param dy y-translatie
	 */
	public void translate(int dx, int dy)
	{
		xPos += dx;
		yPos += dy;
		
		tekstX = (int) Math.round(((double) xPos) / scaleX);
		tekstY = (int) Math.round(((double) yPos) / scaleY);
		
		cx += dx;
		cy += dy;
		
		bb2.translate(dx, dy);
		
		makeHandleBox();
	}	

	/**
	 * check of dit TekstElement bevat is in rechthoek r; doe
	 * dit door te kijken of de middens van de zijden van de
	 * bounding box in r liggen 
	 * @param r te checken rechthoek
	 * @return true/false
	 */
	public boolean isContainedIn(Rectangle r)
	{
		if (r == null)
			return false;
		
		boolean isContainedIn = true;
		
		// bounding box x-coordinaten hoeken
		int topLeftX = bb2.geefPuntX(0);
		int topRightX = bb2.geefPuntX(1);
		int bottomRightX = bb2.geefPuntX(2);
		int bottomLeftX = bb2.geefPuntX(3);

		// bounding box y-coordinaten hoeken
		int topLeftY = bb2.geefPuntY(0);
		int topRightY = bb2.geefPuntY(1);
		int bottomRightY = bb2.geefPuntY(2);
		int bottomLeftY = bb2.geefPuntY(3);
		
		// bounding box x- en y-coordinaten van de middens van de zijden
		int topMiddleX = (topLeftX + topRightX) / 2;
		int topMiddleY = (topLeftY + topRightY) / 2;
		int rightMiddleX = (topRightX + bottomRightX) / 2;
		int rightMiddleY = (topRightY + bottomRightY) / 2;
		int bottomMiddleX = (bottomLeftX + bottomRightX) / 2;
		int bottomMiddleY = (bottomLeftY + bottomRightY) / 2;
		int leftMiddleX = (topLeftX + bottomLeftX) / 2;
		int leftMiddleY = (topLeftY + bottomLeftY) / 2;

		// check of deze middens in r liggen
		isContainedIn = isContainedIn && r.contains(topMiddleX, topMiddleY);
		isContainedIn = isContainedIn && r.contains(rightMiddleX, rightMiddleY);
		isContainedIn = isContainedIn && r.contains(bottomMiddleX, bottomMiddleY);
		isContainedIn = isContainedIn && r.contains(leftMiddleX, leftMiddleY);
		
		return isContainedIn;
	}	
}
