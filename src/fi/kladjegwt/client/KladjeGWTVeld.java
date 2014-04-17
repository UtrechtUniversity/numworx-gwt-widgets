package fi.kladjegwt.client;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.HashMap;
import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;


public class KladjeGWTVeld 
{
	public Canvas kladjeHWTCanvas;
	public Context2d gIm;
	
	static double NZERO = 1e-5d;
	
	boolean lijnen = false;
	boolean ruitjes = true;
	
	int lineDistance = 20;
	int gridSize = 20;
	
	CssColor lijnenKleur = CssColor.make(190, 190, 190);
	CssColor ruitjesKleur = CssColor.make(190, 190, 190);
	static CssColor bbColor = CssColor.make(0, 0, 255);
	
	CssColor selectieColor = CssColor.make(0, 0, 255);
		
	static CssColor zwart = CssColor.make(0, 0, 0);
	static CssColor grijs = CssColor.make(220, 220, 220);
	static CssColor rood = CssColor.make(255, 0, 0);
	static CssColor oranje = CssColor.make(255, 127, 0);
	static CssColor groen = CssColor.make(0, 255, 0);
	static CssColor cyaan = CssColor.make(0, 255, 255);
	static CssColor blauw = CssColor.make(0, 0, 255);
	static CssColor magenta = CssColor.make(255, 0, 255);
	static CssColor geel = CssColor.make(255, 255, 0);
	
	CssColor drawingColor = CssColor.make(0, 0, 0);
	int bgRed = 255;
	int bgGreen = 255;
	int bgBlue = 255;
	CssColor backgroundColor = CssColor.make(bgRed, bgGreen, bgBlue);
	
//	Color[] kleuren = {Color.black, Color.lightGray, Color.red, Color.orange,
//	           Color.green, Color.cyan, Color.blue, Color.magenta};

	final int inert = 0;
	final int tekenen = 1;
	//final int gummen = 2;
	final int lijnTekenen = 3;
	final int rechthoekTekenen = 4;
	final int cirkelTekenen = 5;
	final int tekstTekenen = 6;
	final int selecteren = 7;
	int mouseMode = inert;
	
	boolean mouseDown;
//	Point start = null;
	
	Vector<Point> draggPoints = new Vector<Point>();
	//Vector gumPunten = new Vector();
	//int gumGrootte = 7; // oneven	

	Point figuurStart = null;
	Point lijnEinde = null;
	Rectangle tekenRechthoek = null;
//	KladjeRectangle tekstRechthoek = null;
	Rectangle selecteerRechthoek = null;
//	KladjeRectangle wisRechthoek = null;
	TekstElement tekstEdited = null;

	// backwards compatibility
	//ColorBytes[][] pixels = null;
	Vector<ColorBytes> pixels = new Vector<ColorBytes>();
	int breedte, hoogte;
	Vector<Streep> streepVector = new Vector<Streep>();
	Vector<Lijn> lijnVector = new Vector<Lijn>();
	Vector<Rechthoek> rechthoekVector = new Vector<Rechthoek>();
	Vector<Ellips> ellipsVector = new Vector<Ellips>();
	Vector<TekstElement> tekstElementVector = new Vector<TekstElement>();
	
	int maxHistories = 5;
	int numHistories = 0;
	HashMap<String,Object>[] histories = new HashMap[maxHistories + 1];

//Cursor selectCursor = null;
	boolean sleepSelectie = false;
	boolean objectMoved = false;
	int startX, startY;
	//Vector sleepPoints = new Vector();
	//ImageData sleepRectangleData = null;

	Streep selectedStreep = null;
	Lijn selectedLijn = null;
	Rechthoek selectedRechthoek = null;
	Ellips selectedEllips = null;
	TekstElement selectedTekstElement = null;
	Vector<Object> objectsSelected = new Vector<Object>();
	

//Cursor textCursor = null;

//Font tekstFont;
//Font tekenTekstFont;
//FontMetrics tekstFM;
	
	TekstPopup tekstPopup;	
	
	int tekstBreedte = 50;
	int tekstHoogte;
	//boolean sleepTekst;
	//int tekstRand = 5;
	String tekstString = "";
	int tekstX = 0;
	int tekstY = 0;
	
	double rotateStep = Math.PI / 12; // 15 degrees in radians
	double scaleUpStep = 11e-1d;
	double scaleDownStep = 1 / 11e-1d;
	
	boolean noUpdate = true; 
	
	public KladjeGWTVeld(int w, int h)
	{	
		
		kladjeHWTCanvas = Canvas.createIfSupported();

		setSize(w, h);
		
		MouseHandler mouseHandler = new MouseHandler();
		kladjeHWTCanvas.addMouseDownHandler(mouseHandler);
		kladjeHWTCanvas.addMouseMoveHandler(mouseHandler);
		kladjeHWTCanvas.addMouseUpHandler(mouseHandler);
		
		//TouchWidgetMobileImpl twmi = new TouchWidgetMobileImpl();		
	
		//MGWTTouchHandler touchHandler = new MGWTTouchHandler();
		//twmi.addTouchStartHandler(kladjeGWTCanvas, touchHandler);
		//twmi.addTouchMoveHandler(kladjeGWTCanvas, touchHandler);
		//twmi.addTouchEndHandler(kladjeGWTCanvas, touchHandler);
		
		MGWTTouchHandler touchHandler = new MGWTTouchHandler();
		kladjeHWTCanvas.addTouchStartHandler(touchHandler);
		kladjeHWTCanvas.addTouchMoveHandler(touchHandler);
		kladjeHWTCanvas.addTouchEndHandler(touchHandler);
		
		
		// tekstinvoer??
	}

	/**
	 * @param w
	 * @param h
	 */
	void setSize(int w, int h) {
		breedte = w;
		hoogte = h;
		kladjeHWTCanvas.setWidth(w + "px");
		kladjeHWTCanvas.setHeight(h + "px");
		kladjeHWTCanvas.setCoordinateSpaceWidth(w);
		kladjeHWTCanvas.setCoordinateSpaceHeight(h);
	}

	public Canvas getCanvas()
	{
		return kladjeHWTCanvas;
	}
	
	public void initContext2d() 
	{
		gIm = kladjeHWTCanvas.getContext2d();
		
	}
	
	public void hideTekstVeld(boolean empty)
	{
		if ((tekstPopup == null) || !tekstPopup.isVisible())
			return;
		
		tekstString = tekstPopup.getText();

		//tekstX = tekstVeld.getLocation().x + 2;
		//tekstY = tekstVeld.getLocation().y;
		tekstX = tekstPopup.tekstX;
		tekstY = tekstPopup.tekstY;
		
		
		tekstPopup.setVisible(false);

		if (!tekstString.equals("") && (tekstEdited == null))
		{
			TekstElement tekstElement = 
				new TekstElement(drawingColor, tekstString, tekstX, tekstY);
			tekstElementVector.addElement(tekstElement);
			addToHistory();
			paint();
		}
		else if (!tekstString.equals("") && (tekstEdited != null))
		{
			tekstEdited.zetTekst(tekstString, gIm);
			//tekstEdited.tekst = tekstString;
			addToHistory();
			paint();
			tekstEdited = null;
		}
		else if (tekstString.equals("") && (tekstEdited != null))
		{
			tekstElementVector.removeElement(tekstEdited);
			addToHistory();
			paint();
		}
		
		//drawTekstString();
	
		if (empty)
		{	
			tekstString = "";
			tekstPopup.setText("");
		
		}
	}
	
	
	void addToHistory()
	{

		HashMap<String,Object> stateTable = getState();
		
		histories[numHistories] = stateTable;
		numHistories++;
		if (numHistories > maxHistories)
		{	for (int i = 0; i < numHistories - 1; i++)
			{	histories[i] = histories[i + 1];
			}
			numHistories--;
		}		
	}
	
	public HashMap<String,Object> getFromHistory()
	{	
		if (numHistories > 0)
			numHistories--;
		
//System.out.println("gfh " + numHistories);

		if (numHistories > 0)
		{	
//System.out.println("returned " + numHistories);			
			return histories[numHistories - 1];
		
		}
		else
		{	numHistories = 0;
//System.out.println("returned null " + numHistories);		
		
			return null;
		}
	}


	public HashMap<String,Object> getState()
	{
		
		HashMap<String,Object> h = new HashMap<String,Object>();
		
		// backwards compatibility
		Vector<ArrayList<Short>> gwtStateVector = getGWTState();
		if (gwtStateVector.size() > 0)
		{	h.put("gwtpixels", gwtStateVector);
//System.out.println("put gwtpixels");		
		}

		ArrayList<HashMap<String,Object>> strepen = new ArrayList<HashMap<String,Object>>(); 
		//HashMap<String,Object>[] strepen = new HashMap[streepVector.size()];
		for (int sCnt = 0; sCnt < streepVector.size(); sCnt++)
		{	Streep streep = (Streep) streepVector.elementAt(sCnt);
			//strepen[sCnt] = streep.getState();
			strepen.add(streep.getState());
		}
		h.put("strepen", strepen);
		
		ArrayList<HashMap<String,Object>> lijnenAL = new ArrayList<HashMap<String,Object>>();		
		//HashMap<String,Object>[] lijnenHash = new HashMap[lijnVector.size()];
		for (int lCnt = 0; lCnt < lijnVector.size(); lCnt++)
		{	Lijn lijn = (Lijn) lijnVector.elementAt(lCnt);
			//lijnenHash[lCnt] = lijn.getState();
			lijnenAL.add(lijn.getState());
		}
		h.put("lijnenhash", lijnenAL); // !!!
		
		ArrayList<HashMap<String,Object>> rechthoeken = new ArrayList<HashMap<String,Object>>();		
		//HashMap<String,Object>[] rechthoeken = new HashMap[rechthoekVector.size()];
		for (int rCnt = 0; rCnt < rechthoekVector.size(); rCnt++)
		{	Rechthoek rechthoek = (Rechthoek) rechthoekVector.elementAt(rCnt);
			//rechthoeken[rCnt] = rechthoek.getState();
			rechthoeken.add(rechthoek.getState());
		}
		h.put("rechthoeken", rechthoeken);
		
		ArrayList<HashMap<String,Object>> ellipsen = new ArrayList<HashMap<String,Object>>();		
		//HashMap<String,Object>[] ellipsen = new HashMap[ellipsVector.size()];
		for (int eCnt = 0; eCnt < ellipsVector.size(); eCnt++)
		{	Ellips ellips = (Ellips) ellipsVector.elementAt(eCnt);
			//ellipsen[eCnt] = ellips.getState();
			ellipsen.add(ellips.getState());
		}
		h.put("ellipsen", ellipsen);
		
		ArrayList<HashMap<String,Object>> tekstElementen = new ArrayList<HashMap<String,Object>>();		
		//HashMap<String,Object>[] tekstElementen = new HashMap[tekstElementVector.size()];
		for (int tCnt = 0; tCnt < tekstElementVector.size(); tCnt++)
		{	TekstElement tekstElement = (TekstElement) tekstElementVector.elementAt(tCnt);
			//tekstElementen[tCnt] = tekstElement.getState();
			tekstElementen.add(tekstElement.getState());
		}
		h.put("tekstElementen", tekstElementen);
		
		
		return h;


		//return stateMap;
	}

	public Vector<ArrayList<Short>> getGWTState()
	{	
		//ArrayList<ArrayList<Short>> gwtStateAL = new ArrayList<ArrayList<Short>>(); 
		
		Vector<ArrayList<Short>> gwtStateVector = new Vector<ArrayList<Short>>();
		for (int pCnt = 0; pCnt < pixels.size(); pCnt++)
		{	ColorBytes cb = (ColorBytes) pixels.elementAt(pCnt);
			
			ArrayList<Short> gwtPixels = new ArrayList<Short>();
		
			gwtPixels.add(new Short((short) cb.x));
			gwtPixels.add(new Short((short) cb.y));
			gwtPixels.add(new Short(cb.red));
			gwtPixels.add(new Short(cb.green));
			gwtPixels.add(new Short(cb.blue));
			
			//short[] gwtPixels = new short[5];
			//gwtPixels[0] = (short) cb.x;
			//gwtPixels[1] = (short) cb.y;
			//gwtPixels[2] = cb.red;
			//gwtPixels[3] = cb.green;			
			//gwtPixels[4] = cb.blue;						
			gwtStateVector.addElement(gwtPixels);
				
		}
//System.out.println("kladjeVeld getGWTState " + gwtStateVector.size());
		return gwtStateVector;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object> toArrayList(Object object)
	{
		if (object instanceof List || object == null)
			return (List<Object>) object;
		if (object instanceof Object[])
		{
			Object[] objects = (Object[]) object;
			return Arrays.asList(objects);
		}
		return null;
	}

	
	public void setState(Map<String, Object> launchState)
	{
		
		//System.out.println("kv setState");

		
// accepteer alleen GWTPixels
		
/*		
		Vector<ArrayList<Short>> gwtStateVector = new Vector<ArrayList<Short>>();
		if (h.containsKey("gwtpixels"))
		{	gwtStateVector = (Vector<ArrayList<Short>>) h.get("gwtpixels");
			if (gwtStateVector.size() > 0)
				setOldGWTState(gwtStateVector);
		}
*/
		
		// hier de rest
		streepVector.removeAllElements();
		//HashMap<String,Object>[] strepen = new HashMap[0];
		List<?> strepen = new ArrayList<Object>();
		if (launchState.containsKey("strepen"))
			strepen = toArrayList( launchState.get("strepen") );
		for (int sCnt = 0; sCnt < strepen.size(); sCnt++)
		{	Streep streep = Streep.setState((HashMap<String, Object>) strepen.get(sCnt));
			streepVector.addElement(streep);
		}
		
		lijnVector.removeAllElements();
		//HashMap<String,Object>[] lijnenHash = new HashMap[0];
		List<?> lijnenAL = new ArrayList<HashMap<String,Object>>();
		// launchdata and setState
		if (launchState.containsKey("lijnenhash"))
			lijnenAL = toArrayList( launchState.get("lijnenhash") );
		for (int lCnt = 0; lCnt < lijnenAL.size(); lCnt++)
		{	Lijn lijn = Lijn.setState((HashMap<String, Object>) lijnenAL.get(lCnt));
			lijnVector.addElement(lijn);
		}
		
		rechthoekVector.removeAllElements();
		//HashMap<String,Object>[] rechthoeken = new HashMap[0];
		List<?> rechthoeken = new ArrayList<HashMap<String,Object>>();
		if (launchState.containsKey("rechthoeken"))
			rechthoeken = toArrayList( launchState.get("rechthoeken") );
		for (int rCnt = 0; rCnt < rechthoeken.size(); rCnt++)
		{	Rechthoek rechthoek = Rechthoek.setState((HashMap<String, Object>) rechthoeken.get(rCnt));
			rechthoekVector.addElement(rechthoek);
		}

		ellipsVector.removeAllElements();
		//HashMap<String,Object>[] ellipsen = new HashMap[0];
		List<?> ellipsen = new ArrayList<HashMap<String,Object>>();
		if (launchState.containsKey("ellipsen"))
			ellipsen = toArrayList( launchState.get("ellipsen") );
		for (int eCnt = 0; eCnt < ellipsen.size(); eCnt++)
		{	Ellips ellips = Ellips.setState((HashMap<String, Object>) ellipsen.get(eCnt));
			ellipsVector.addElement(ellips);
		}

		tekstElementVector.removeAllElements();
		//HashMap<String,Object>[] tekstElementen = new HashMap[0];
		List<?> tekstElementen = new ArrayList<HashMap<String,Object>>();
		if (launchState.containsKey("tekstElementen"))
			tekstElementen = toArrayList( launchState.get("tekstElementen") );
		for (int tCnt = 0; tCnt < tekstElementen.size(); tCnt++)
		{	TekstElement tekstElement = TekstElement.setState((HashMap<String, Object>) tekstElementen.get(tCnt));
			tekstElementVector.addElement(tekstElement);
		}
		

		paint();
	}
	
	//public void setOldGWTState(Vector<short[]> gwtStateVector)
	public void setOldGWTState(Vector<ArrayList<Short>> gwtStateVector)
	{
//System.out.println("kladjeVeld setGWTState " + gwtStateVector.size());

		//int cnt = 0;
		for (int pCnt = 0; pCnt < gwtStateVector.size(); pCnt++)
		{
			ArrayList<Short> gwtPixels = new ArrayList<Short>();
			gwtPixels = (ArrayList<Short>) gwtStateVector.elementAt(pCnt);
			pixels.removeAllElements();
			ColorBytes cb = new ColorBytes(((Short) gwtPixels.get(0)).shortValue(), 
										   ((Short) gwtPixels.get(1)).shortValue(), 
										   ((Short) gwtPixels.get(2)).shortValue(), 
										   ((Short) gwtPixels.get(3)).shortValue(), 
										   ((Short) gwtPixels.get(4)).shortValue());
			pixels.addElement(cb);
		}

	}
	
	
	void undo()
	{
		wis(false);
		HashMap<String,Object> lastState = getFromHistory();
		if (lastState != null)
		{	setState(lastState);
		}

		paint();
	}
	
	void wis(boolean complete)
	{	
		if ((mouseMode == selecteren) && objectSelected())
		{
			wisObjectSelected();
			
		}
		else if ((mouseMode == selecteren) && (selecteerRechthoek != null))
		{
			wisObjectsSelected();
		}
		else
		{
			pixels.removeAllElements();
			
			streepVector.removeAllElements();
			lijnVector.removeAllElements();
			rechthoekVector.removeAllElements();
			ellipsVector.removeAllElements();
			tekstElementVector.removeAllElements();
			
			if (complete)
				numHistories = 0;
		}			
		paint();
	}
	
	public void paint()
	{
		paint(gIm);
	}
	
/*	
	public void paint(boolean metDecoratie)
	{
		paint(gIm, metDecoratie);
	}
*/
	public void paint(Context2d g)
	{
		
		g.setLineWidth(1.0d);

		
		g.setFillStyle(backgroundColor);
		g.fillRect(0, 0, breedte, hoogte);
		
		
//		g.clearRect(0, 0, breedte, hoogte);
	
		
		
//		g.setStrokeStyle(zwart);
//		g.strokeRect(0, 0, breedte, hoogte);
		
		
		if (lijnen)
		{
			
			g.setStrokeStyle(lijnenKleur);
			int steps = hoogte / lineDistance;
			for (int lCnt = 1; lCnt <= steps; lCnt++)
			{
				g.beginPath();
				g.moveTo(0, lCnt * lineDistance);
				g.lineTo(breedte - 1, lCnt * lineDistance);
				g.stroke();
				
				//g.drawLine(0, lCnt * lineDistance, getSize().width - 1, lCnt * lineDistance);
			}
			
		}
		if (ruitjes)
		{
			g.setStrokeStyle(ruitjesKleur);
			int vSteps = hoogte / lineDistance;
			for (int vCnt = 1; vCnt <= vSteps; vCnt++)
			{
				g.beginPath();
				g.moveTo(0, vCnt * lineDistance);
				g.lineTo(breedte - 1, vCnt * lineDistance);
				g.stroke();
				//g.drawLine(0, vCnt * lineDistance, getSize().width - 1, vCnt * lineDistance);
			}
			int hSteps = breedte / lineDistance;
			for (int hCnt = 1; hCnt <= hSteps; hCnt++)
			{
				g.beginPath();
				g.moveTo(hCnt * lineDistance, 0);
				g.lineTo(hCnt * lineDistance, hoogte - 1);
				g.stroke();
				//g.drawLine(hCnt * lineDistance, 0, hCnt * lineDistance, getSize().height - 5);
			}

//System.out.println("ruitjes");
//System.out.println("lw = " + g.getLineWidth());
//System.out.println("ss = " + g.getStrokeStyle().toString());
		}
		

// tijdelijk		
		g.setStrokeStyle(zwart);
		g.strokeRect(0, 0, breedte, hoogte);
//System.out.println("outline");		
		
		g.setLineWidth(1.5d); 
		tekenProgramma(g);

	}
	
	void tekenProgramma(Context2d g)
	{

		// backwards compatibility
		if (pixels != null)
		{
			for (int cbCnt = 0; cbCnt < pixels.size(); cbCnt++)
			{
				ColorBytes cb = (ColorBytes) pixels.elementAt(cbCnt);
				g.setStrokeStyle(cb.makeColor());
				
				g.beginPath();
				g.strokeRect(cb.x, cb.y, 1, 1);
				
			}
			//g.putImageData(pixels, 0, 0);
//System.out.println("imageData");			
		}
/*		
		if (wisRechthoek != null)
		{
			g.setFillStyle(backgroundColor);
			g.fillRect(wisRechthoek.x, wisRechthoek.y, wisRechthoek.width, wisRechthoek.height);
			wisRechthoek = null;
			updatePixelArray();
			if (pixels != null)
			{	g.putImageData(pixels, 0, 0);
			}
		}
*/		
		
		// elementen
		for (int sCnt = 0; sCnt < streepVector.size(); sCnt++)
		{	Streep streep = (Streep) streepVector.elementAt(sCnt);
			streep.teken(g);
		}
		for (int lCnt = 0; lCnt < lijnVector.size(); lCnt++)
		{	Lijn lijn = (Lijn) lijnVector.elementAt(lCnt);
			lijn.teken(g);
		}
		for (int rCnt = 0; rCnt < rechthoekVector.size(); rCnt++)
		{	Rechthoek rechthoek = (Rechthoek) rechthoekVector.elementAt(rCnt);
			rechthoek.teken(g);
		}
		for (int eCnt = 0; eCnt < ellipsVector.size(); eCnt++)
		{	Ellips ellips = (Ellips) ellipsVector.elementAt(eCnt);
			ellips.teken(g);
		}
		for (int tCnt = 0; tCnt < tekstElementVector.size(); tCnt++)
		{	TekstElement tekstElement = (TekstElement) tekstElementVector.elementAt(tCnt);
			tekstElement.teken(g);
		}
		
		g.setStrokeStyle(drawingColor);		
		
		if (draggPoints.size() == 1)
		{	Point p = (Point) draggPoints.elementAt(0);
			//g.moveTo(p.x, p.y);
			//g.lineTo(p.x, p.y);
			g.strokeRect(p.x, p.y, 1, 1);
		}
		if (draggPoints.size() > 1)
		{	
			Point p1 = (Point) draggPoints.elementAt(0);
			g.beginPath();
			g.moveTo(p1.x, p1.y);
			for (int pCnt = 1; pCnt < draggPoints.size(); pCnt++)
			{	Point p2 = (Point) draggPoints.elementAt(pCnt);
				g.lineTo(p2.x, p2.y);
				p1 = p2;
			}
			g.stroke();
			
		}
/*
		for (int pCnt = 0; pCnt < gumPunten.size(); pCnt++)
		{
			Point p = (Point) gumPunten.elementAt(pCnt);
			g.setFillStyle(backgroundColor);
			g.fillRect(p.x - gumGrootte / 2, p.y - gumGrootte / 2, gumGrootte, gumGrootte);
			
		}
*/		
		g.setStrokeStyle(drawingColor);
		
		if ((mouseMode == lijnTekenen) && (figuurStart != null) && (lijnEinde != null))
		{	
			
			g.beginPath();
			g.moveTo(figuurStart.x, figuurStart.y);
			g.lineTo(lijnEinde.x, lijnEinde.y);
			g.stroke();
//System.out.println("lijn");		
//System.out.println("lw = " + g.getLineWidth());
//System.out.println("ss = " + g.getStrokeStyle().toString());

//			g.drawLine(figuurStart.x, figuurStart.y, lijnEinde.x, lijnEinde.y);
			
		}
		
		if ((mouseMode == rechthoekTekenen) && (tekenRechthoek != null))
		{
//System.out.println("mm = rh && trh not null");
			g.beginPath();
			g.strokeRect(tekenRechthoek.x, tekenRechthoek.y, tekenRechthoek.width, tekenRechthoek.height);
		}

//		else if ((mouseMode == rechthoekTekenen) && mouseDragged && (tekenRechthoek == null))
//		{
//			g.strokeRect(10, 10, 10, 10);
//		}
		
		
		if ((mouseMode == cirkelTekenen) && (tekenRechthoek != null))
		{
//System.out.println("mm = ci && trh not null");			
			double centerX = tekenRechthoek.x + tekenRechthoek.width / 2;
			double centerY = tekenRechthoek.y + tekenRechthoek.height / 2;
			int steps = 35;
			double angleStep = 2 * Math.PI / steps;
			
			g.moveTo(centerX + tekenRechthoek.width / 2, centerY + tekenRechthoek.height / 2);
			g.beginPath();
			for (int pCnt = 0; pCnt < steps; pCnt++)
			{
				g.lineTo(centerX + (tekenRechthoek.width / 2) * Math.cos(pCnt * angleStep),
						 centerY - (tekenRechthoek.height / 2) * Math.sin(pCnt * angleStep));
			}
			g.closePath();
			g.stroke();
			
//			g.beginPath();
//			g.arc(centerX, centerY, tekenRechthoek.height / 2, 0, 2 * Math.PI);
			//g.stroke();
			
//			g.drawOval(tekenRechthoek.x, tekenRechthoek.y, tekenRechthoek.width, tekenRechthoek.height);
		}
		
		// alleen op het scherm
		if (mouseMode == tekstTekenen)
		{
			
		}

		// alleen op het scherm
		if ((mouseMode == selecteren) && (selecteerRechthoek != null))// && !sleepSelectie)
		{
			//if (mouseDown)
			//{	
				g.setLineWidth(0.8d);
				g.setStrokeStyle(selectieColor);
				g.beginPath();
				g.strokeRect(selecteerRechthoek.x, selecteerRechthoek.y, 
							selecteerRechthoek.width, selecteerRechthoek.height);
				g.setLineWidth(1.5d);
			//}
			
		}
		
		if (mouseMode == selecteren)
		{
			if (selectedStreep != null)
				selectedStreep.tekenBB(g);
			if (selectedLijn != null)
				selectedLijn.tekenBB(g);
			if (selectedRechthoek != null)
				selectedRechthoek.tekenBB(g);
			if (selectedEllips != null)
				selectedEllips.tekenBB(g);
			if (selectedTekstElement != null)
				selectedTekstElement.tekenBB(g);
			
			for (int oCnt = 0; oCnt < objectsSelected.size(); oCnt++)
			{
				Object o = (Object) objectsSelected.elementAt(oCnt);
				if (o instanceof Streep)
					((Streep) o).tekenBB(g);
				else if (o instanceof Lijn)
					((Lijn) o).tekenBB(g);
				else if (o instanceof Rechthoek)
					((Rechthoek) o).tekenBB(g);
				else if (o instanceof Ellips)
					((Ellips) o).tekenBB(g);
				else if (o instanceof TekstElement)
					((TekstElement) o).tekenBB(g);
				
			}
		}	
		
/*		
		if ((mouseMode == selecteren) && (selecteerRechthoek != null) && sleepSelectie)
		{
		
			if (mouseDown)
			{	g.setLineWidth(0.8d);
				g.setStrokeStyle(selectieColor);
				g.beginPath();
				g.strokeRect(selecteerRechthoek.x, selecteerRechthoek.y, 
					     	 selecteerRechthoek.width, selecteerRechthoek.height);
			}	
			
		}
*/		
	}

/*	
	void updatePixelArray()
	{	
		pixels = gIm.getImageData(0, 0, breedte, hoogte);
		
	}	
*/
	
/*	
	void gumPunt(int x, int y, Context2d g)
	{
		
		g.setFillStyle(backgroundColor);
		g.fillRect(x - gumGrootte / 2, y - gumGrootte / 2, gumGrootte, gumGrootte);

	}
*/	
	
	public void resetSelectedObject()
	{
		selectedStreep = null;
		selectedLijn = null;
		selectedRechthoek = null;
		selectedEllips = null;
		selectedTekstElement = null;
	}
	
	public void resetSelectedObjects()
	{
		objectsSelected.removeAllElements();
	}
	
	
	public boolean objectSelected()
	{
		return (selectedStreep != null)|| 
		   (selectedLijn != null) || 
		   (selectedRechthoek != null) || 
		   (selectedEllips != null) ||
		   (selectedTekstElement != null);		
	}
	
	public boolean setSelectedObject(int x, int y)
	{	boolean found = false;
	
		for (int sCnt = 0; sCnt < streepVector.size(); sCnt++)
		{	Streep streep = (Streep) streepVector.elementAt(sCnt);
			if (streep.bbContains(x, y))
			{	selectedStreep = streep;
				selectedLijn = null;
				selectedRechthoek  = null;
				selectedEllips  = null;
				selectedTekstElement  = null;
				return true; 
			}
		}
		for (int lCnt = 0; lCnt < lijnVector.size(); lCnt++)
		{	Lijn lijn = (Lijn) lijnVector.elementAt(lCnt);
			if (lijn.bbContains(x, y))
			{	selectedLijn = lijn;
				selectedStreep = null;
				selectedRechthoek  = null;
				selectedEllips  = null;
				selectedTekstElement  = null;
				return true; 
			}
		}
		for (int rCnt = 0; rCnt < rechthoekVector.size(); rCnt++)
		{	Rechthoek rechthoek = (Rechthoek) rechthoekVector.elementAt(rCnt);
			if (rechthoek.bbContains(x, y))
			{	selectedRechthoek = rechthoek;
				selectedStreep = null;
				selectedLijn  = null;
				selectedEllips  = null;
				selectedTekstElement  = null;
			
				return true; 
			}
		}
		for (int eCnt = 0; eCnt < ellipsVector.size(); eCnt++)
		{	Ellips ellips = (Ellips) ellipsVector.elementAt(eCnt);
			if (ellips.bbContains(x, y))
			{	selectedEllips = ellips;
				selectedStreep = null;
				selectedLijn  = null;
				selectedRechthoek  = null;
				selectedTekstElement  = null;
			
				return true; 
			}
		}
		for (int tCnt = 0; tCnt < tekstElementVector.size(); tCnt++)
		{	TekstElement tekstElement = (TekstElement) tekstElementVector.elementAt(tCnt);
			if (tekstElement.bbContains(x, y))
			{	selectedTekstElement = tekstElement;
				selectedStreep = null;
				selectedLijn  = null;
				selectedRechthoek  = null;
				selectedEllips  = null;
				return true; 
			}
		}
	
		return found;
	}
	
	public TekstElement getClickedTekstElement(int x, int y)
	{
		TekstElement result = null;
		for (int tCnt = 0; tCnt < tekstElementVector.size(); tCnt++)
		{	TekstElement tekstElement = (TekstElement) tekstElementVector.elementAt(tCnt);
			if (tekstElement.bbContains(x, y))
			{	result = tekstElement;
			}
		}
		return result;
	}
	
	public boolean findObjectsSelected(Rectangle r)
	{	boolean found = false;
		objectsSelected.removeAllElements();
	
		for (int sCnt = 0; sCnt < streepVector.size(); sCnt++)
		{	Streep streep = (Streep) streepVector.elementAt(sCnt);
			if (streep.isContainedIn(r))
			{	objectsSelected.addElement(streep);
				found = true; 
			}
		}
		for (int lCnt = 0; lCnt < lijnVector.size(); lCnt++)
		{	Lijn lijn = (Lijn) lijnVector.elementAt(lCnt);
			if (lijn.isContainedIn(r))
			{	objectsSelected.addElement(lijn);
				found = true; 
			}
		}
		for (int rCnt = 0; rCnt < rechthoekVector.size(); rCnt++)
		{	Rechthoek rechthoek = (Rechthoek) rechthoekVector.elementAt(rCnt);
			if (rechthoek.isContainedIn(r))
			{	objectsSelected.addElement(rechthoek);			
				found = true; 
			}
		}
		for (int eCnt = 0; eCnt < ellipsVector.size(); eCnt++)
		{	Ellips ellips = (Ellips) ellipsVector.elementAt(eCnt);
			if (ellips.isContainedIn(r))
			{	objectsSelected.addElement(ellips);
				found = true; 
			}
		}
		for (int tCnt = 0; tCnt < tekstElementVector.size(); tCnt++)
		{	TekstElement tekstElement = (TekstElement) tekstElementVector.elementAt(tCnt);
			if (tekstElement.isContainedIn(r))
			{	objectsSelected.addElement(tekstElement);
				found = true; 
			}
		}
	
		return found;
	}
	
	
	public void wisObjectSelected()
	{ 
		boolean gewist = false;
		if (selectedStreep != null)
		{	streepVector.removeElement(selectedStreep);
			selectedStreep = null;
			gewist = true;
		}
		if (selectedLijn != null)  
		{	lijnVector.removeElement(selectedLijn);
			selectedLijn = null;
			gewist = true;
		}
		if (selectedRechthoek != null)  
		{	rechthoekVector.removeElement(selectedRechthoek);
			selectedRechthoek = null;
			gewist = true;
		}
		if (selectedEllips != null) 
		{	ellipsVector.removeElement(selectedEllips);
			selectedEllips = null;
			gewist = true;
		}
		if (selectedTekstElement != null)
		{	tekstElementVector.removeElement(selectedTekstElement);
			selectedTekstElement = null;
			gewist = true;
		}
		
		sleepSelectie = false;
		
		if (gewist)
			addToHistory();
		paint();
	}

	public void wisObjectsSelected()
	{ 
		boolean gewist = false;
		for (int oCnt = 0; oCnt < objectsSelected.size(); oCnt++)
		{
			Object o = (Object) objectsSelected.elementAt(oCnt);
			if (o instanceof Streep)
				streepVector.removeElement((Streep) o);
			else if (o instanceof Lijn)
				lijnVector.removeElement((Lijn) o);
			else if (o instanceof Rechthoek)
				rechthoekVector.removeElement((Rechthoek) o);
			else if (o instanceof Ellips)
				ellipsVector.removeElement((Ellips) o);
			else if (o instanceof TekstElement)
				tekstElementVector.removeElement((TekstElement) o);
			
			gewist = true;
		}
		
		sleepSelectie = false;
		objectsSelected.removeAllElements();
		selecteerRechthoek = null;
		
		if (gewist)
			addToHistory();
		paint();
	}
	
	public void rotateObjectSelected(double rotateStep)
	{ 
		if (selectedStreep != null)
			selectedStreep.rotate(rotateStep);
		if (selectedLijn != null)  
			selectedLijn.rotate(rotateStep);		   
		if (selectedRechthoek != null)  
			selectedRechthoek.rotate(rotateStep);
		if (selectedEllips != null) 
			selectedEllips.rotate(rotateStep);		
		//if  (selectedTekstElement != null)
		//	selectedTekstElement.rotate(rotateStep);
	
		paint();
	}

	public void scaleObjectSelected(double scaleStep)
	{ 
		if (selectedStreep != null)
			selectedStreep.scale(scaleStep);
		if (selectedLijn != null)  
			selectedLijn.scale(scaleStep);		   
		if (selectedRechthoek != null)  
			selectedRechthoek.scale(scaleStep);
		if (selectedEllips != null) 
			selectedEllips.scale(scaleStep);		
		//if  (selectedTekstElement != null)
		//	selectedTekstElement.scale(scaleStep);
		
		paint();
	}
	
	public void translateObjectSelected(int dx, int dy)
	{ 
		if (selectedStreep != null)
			selectedStreep.translate(dx, dy);
		if (selectedLijn != null)  
			selectedLijn.translate(dx, dy);		   
		if (selectedRechthoek != null)  
			selectedRechthoek.translate(dx, dy);
		if (selectedEllips != null) 
			selectedEllips.translate(dx, dy);		
		if  (selectedTekstElement != null)
			selectedTekstElement.translate(dx, dy);
		
	}

	public void translateObjectsSelected(int dx, int dy)
	{ 
		for (int oCnt = 0; oCnt < objectsSelected.size(); oCnt++)
		{
			Object o = (Object) objectsSelected.elementAt(oCnt);
			if (o instanceof Streep)
				((Streep) o).translate(dx, dy);
			else if (o instanceof Lijn)
				((Lijn) o).translate(dx, dy);
			else if (o instanceof Rechthoek)
				((Rechthoek) o).translate(dx, dy);
			else if (o instanceof Ellips)
				((Ellips) o).translate(dx, dy);
			else if (o instanceof TekstElement)
				((TekstElement) o).translate(dx, dy);
			
		}
		
	}
	
	public boolean objectSelectedContains(int x, int y)
	{ 
		return ((selectedStreep != null) && selectedStreep.bbContains(x, y)) || 
			   ((selectedLijn != null) && selectedLijn.bbContains(x, y)) || 
			   ((selectedRechthoek != null) && selectedRechthoek.bbContains(x, y)) || 
			   ((selectedEllips != null) && selectedEllips.bbContains(x, y)) ||
			   ((selectedTekstElement != null) && selectedTekstElement.bbContains(x, y));
	}
	

	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		if (mouseMode == tekenen)
		{
			mouseDown = true;
			draggPoints.addElement(new Point(eventX, eventY));
			paint();
		}
/*		
		else if (mouseMode == gummen)
		{
			mouseDown = true;
			//gumPunt(e.getX(), e.getY(), gIm);
			gumPunten.addElement(new Point(eventX, eventY));
			paint();
		}
*/		
		else if ((mouseMode == lijnTekenen) ||
				 (mouseMode == rechthoekTekenen) ||
				 (mouseMode == cirkelTekenen))
		{
			mouseDown = true;
			figuurStart = new Point(eventX, eventY);
			paint();
		}
		else if (mouseMode == tekstTekenen)
		{
			mouseDown = true;
			
			startX = eventX + kladjeHWTCanvas.getAbsoluteLeft();
			startY = eventY + kladjeHWTCanvas.getAbsoluteTop();
			
			if ((tekstPopup != null) && tekstPopup.isVisible())
			{
				hideTekstVeld(true);
			}

			tekstEdited = getClickedTekstElement(eventX, eventY);
			tekstPopup = new TekstPopup(this, eventX, eventY);
			if (tekstEdited != null)
			{	tekstPopup.setText(tekstEdited.tekst);
				tekstPopup.setTextColor(tekstEdited.kleur.toString());
			}
			else 
				tekstPopup.setTextColor(drawingColor.toString());
				
			//else	
			//	tekstVeld.setText("");
			//tekstPopup.setModal(true);
			
			tekstPopup.setPopupPosition(startX, startY);
			tekstPopup.show();
			tekstPopup.textBox.setFocus(true);

			
/*				
				if (tekstVeld.isVisible())
				{
					hideTekstVeld(true);
				}
				tekstVeld.setLocation(e.getX(), e.getY());
				tekstVeld.setText("");
				tekstVeld.setVisible(true);
				tekstVeld.requestFocus();

*/			
			paint();
			
		}
		else if (mouseMode == selecteren)
		{
			mouseDown = true;
			
			// individueel object aangeklikt, was mogelijk al geselecteerd
			//if (setSelectedObject(e.getX(), e.getY()) || objectSelectedContains(e.getX(), e.getY()))
			if (setSelectedObject(eventX, eventY) || objectSelectedContains(eventX, eventY))
			{
				sleepSelectie = true;
				startX = eventX;//e.getX();
				startY = eventY;//e.getY();
				selecteerRechthoek = null;
				resetSelectedObjects();

				objectMoved = false;
			}
			else if ((selecteerRechthoek != null) && selecteerRechthoek.contains(eventX, eventY))
			{
				resetSelectedObject();
				sleepSelectie = true;
				startX = eventX;//e.getX();
				startY = eventY;//e.getY();
			}
			else
			{
				sleepSelectie = false;
				resetSelectedObject();
				resetSelectedObjects();
				figuurStart = new Point(eventX, eventY);
				selecteerRechthoek = null;
			}
			paint();
		}
		
	}
	
	public void mouseMoveTouchMoveAction(int eventX, int eventY, boolean shiftPressed)
	{
		if (!mouseDown)
			return;
		
		if (mouseMode == tekenen)
		{
			draggPoints.addElement(new Point(eventX, eventY));
			paint();
		}
/*		
		else if (mouseMode == gummen)
		{
			//gumPunt(e.getX(), e.getY(), gIm);
			gumPunten.addElement(new Point(eventX, eventY));
			paint();
		}
*/		
		else if (mouseMode == lijnTekenen)
		{
			//if (figuurStart == null)
			//	figuurStart = new Point(eventX, eventY);
				
			if (shiftPressed)
			{
				if ((eventX > figuurStart.x) && (eventY > figuurStart.y))
				{	
					double xZijde = (double) eventX - figuurStart.x;
					double yZijde = (double) eventY - figuurStart.y;
					int min = Math.min(eventX - figuurStart.x, eventY - figuurStart.y);
					if (yZijde > xZijde - NZERO)
					{
						if (xZijde < yZijde / 2 + NZERO)
						{
							lijnEinde = new Point(figuurStart.x, eventY);
						}
						else
						{
							lijnEinde = new Point(figuurStart.x + min, figuurStart.y + min);
						}
					}
					else
					{
						if (yZijde > xZijde / 2 - NZERO)
						{
							lijnEinde = new Point(figuurStart.x + min, figuurStart.y + min);
						}
						else
						{
							lijnEinde = new Point(eventX, figuurStart.y);
						}
					}

				}	
				else if ((eventX > figuurStart.x) && (eventY < figuurStart.y))
				{	
					double xZijde = (double) eventX - figuurStart.x;
					double yZijde = (double) figuurStart.y - eventY;
					int min = Math.min(eventX - figuurStart.x, figuurStart.y - eventY);
					if (yZijde > xZijde + NZERO)
					{
						if (xZijde < yZijde / 2 + NZERO)
						{
							lijnEinde = new Point(figuurStart.x, eventY);
						}
						else
						{
							lijnEinde = new Point(figuurStart.x + min, figuurStart.y - min);
						}
					}
					else
					{
						if (yZijde > xZijde / 2 - NZERO)
						{
							lijnEinde = new Point(figuurStart.x + min, figuurStart.y - min);
						}
						else
						{
							lijnEinde = new Point(eventX, figuurStart.y);
						}
					}



				}	
				else if ((eventX < figuurStart.x) && (eventY > figuurStart.y))
				{	
					double xZijde = (double) figuurStart.x - eventX;
					double yZijde = (double) eventY - figuurStart.y;
					int min = Math.min(figuurStart.x - eventX, eventY - figuurStart.y);
					if (yZijde > xZijde + NZERO)
					{
						if (xZijde < yZijde / 2 + NZERO)
						{
							lijnEinde = new Point(figuurStart.x, eventY);
						}
						else
						{
							lijnEinde = new Point(figuurStart.x - min, figuurStart.y + min);
						}
					}
					else
					{
						if (yZijde > xZijde / 2 - NZERO)
						{
							lijnEinde = new Point(figuurStart.x - min, figuurStart.y + min);
						}
						else
						{
							lijnEinde = new Point(eventX, figuurStart.y);
						}
					}
					
					
				}	
				else if ((eventX < figuurStart.x) && (eventY < figuurStart.y))
				{	
					int xZijde = figuurStart.x - eventX;
					int yZijde = figuurStart.y - eventY;
					int min = Math.min(figuurStart.x - eventX, figuurStart.y - eventY);
					if (yZijde > xZijde + NZERO)
					{
						if (xZijde < yZijde / 2 + NZERO)
						{
							lijnEinde = new Point(figuurStart.x, eventY);
						}
						else
						{
							lijnEinde = new Point(figuurStart.x - min, figuurStart.y - min);
						}
					}
					else
					{
						if (yZijde > xZijde / 2 - NZERO)
						{
							lijnEinde = new Point(figuurStart.x - min, figuurStart.y - min);
						}
						else
						{
							lijnEinde = new Point(eventX, figuurStart.y);
						}
					}
					        
				}
				
			}
			else
			{	
				lijnEinde = new Point(eventX, eventY);
			}
			
			paint();
			
		} // lijnTekenen
		else if ((mouseMode == rechthoekTekenen) || (mouseMode == cirkelTekenen))
		{
//			mouseDragged = true;
			
			//if (figuurStart == null)
			//	figuurStart = new Point(eventX, eventY);
			
			if (figuurStart != null)
			{
				
				if (shiftPressed)
				{
//System.out.println("ShiftDown");
					if ((eventX > figuurStart.x) && (eventY > figuurStart.y))
					{	
						int zijde = Math.min(eventX - figuurStart.x, eventY - figuurStart.y);
						tekenRechthoek = new Rectangle(figuurStart.x, figuurStart.y, zijde, zijde); 

					}	
					else if ((eventX > figuurStart.x) && (eventY < figuurStart.y))
					{	
						int zijde = Math.min(eventX - figuurStart.x, figuurStart.y - eventY);
						tekenRechthoek = new Rectangle(figuurStart.x, eventY, zijde, zijde); 

					}	
					else if ((eventX < figuurStart.x) && (eventY > figuurStart.y))
					{	
						int zijde = Math.min(figuurStart.x - eventX, eventY - figuurStart.y);
						tekenRechthoek = new Rectangle(eventX, figuurStart.y, zijde, zijde);
						
					}	
					else if ((eventX < figuurStart.x) && (eventY < figuurStart.y))
					{	
						int zijde = Math.min(figuurStart.x - eventX, figuurStart.y - eventY);
						tekenRechthoek = new Rectangle(eventX, eventY, zijde, zijde); 
						        
					}
				}
				else
				{	
					if ((eventX > figuurStart.x) && (eventY > figuurStart.y))
					{	tekenRechthoek = new Rectangle(figuurStart.x, figuurStart.y, 
						                           	   eventX - figuurStart.x, eventY - figuurStart.y); 
					}	
					else if ((eventX > figuurStart.x) && (eventY < figuurStart.y))
					{	tekenRechthoek = new Rectangle(figuurStart.x, eventY, 
							                           eventX - figuurStart.x, figuurStart.y - eventY); 
					}	
					else if ((eventX < figuurStart.x) && (eventY > figuurStart.y))
					{	tekenRechthoek = new Rectangle(eventX, figuurStart.y, 
												       figuurStart.x - eventX, eventY - figuurStart.y); 
					}	
					else if ((eventX < figuurStart.x) && (eventY < figuurStart.y))
					{	tekenRechthoek = new Rectangle(eventX, eventY, 
												       figuurStart.x - eventX, figuurStart.y - eventY); 
					}
				}
				
				paint();
			}
			else
			{	tekenRechthoek = new Rectangle(10, 10, 10, 10);
				paint();
			}
			
		} //rechthoek && cirkel
		else if (mouseMode == tekstTekenen)
		{
/*				

			if (sleepTekst) // verplaats de tekstRechthoek en het tekstVeld!!
			{	
			
				int dx = e.getX() - startX;
				int dy = e.getY() - startY;
				tekstRechthoek.translate(dx, dy);
				tekstVeld.setLocation(tekstVeld.getLocation().x + dx, tekstVeld.getLocation().y + dy);
				
				startX = e.getX();
				startY = e.getY();
			
			}
*/				
			paint();
		}
		else if (mouseMode == selecteren)
		{
	
			if (sleepSelectie) // verplaats de selecteerRechthoek met inhoud!!
			{
				
				
				int dx = eventX - startX;
				int dy = eventY - startY;
				//int dx = eventX - start.x;
				//int dy = eventY - start.y;
				if (selecteerRechthoek != null)
					selecteerRechthoek.translate(dx, dy);
				
				translateObjectSelected(dx, dy);
				
				translateObjectsSelected(dx, dy);

				startX = eventX;
				startY = eventY;
				//start = new Point(eventX, eventY);
				
				objectMoved = true;
				
				paint();
				
				
			}
			else // sleepSelectie, vorm de selecteerRechthoek
			{	
				
				if (figuurStart == null)
					return;
					//	figuurStart = new Point(eventX, eventY);
				
				if ((eventX > figuurStart.x) && (eventY > figuurStart.y))
				{	selecteerRechthoek = new Rectangle(figuurStart.x, figuurStart.y, 
						eventX - figuurStart.x, eventY - figuurStart.y); 
				}	
				else if ((eventX > figuurStart.x) && (eventY < figuurStart.y))
				{	selecteerRechthoek = new Rectangle(figuurStart.x, eventY, 
						eventX - figuurStart.x, figuurStart.y - eventY); 
				}	
				else if ((eventX < figuurStart.x) && (eventY > figuurStart.y))
				{	selecteerRechthoek = new Rectangle(eventX, figuurStart.y, 
				       figuurStart.x - eventX, eventY - figuurStart.y); 
				}	
				else if ((eventX < figuurStart.x) && (eventY < figuurStart.y))
				{	selecteerRechthoek = new Rectangle(eventX, eventY, 
				       figuurStart.x - eventX, figuurStart.y - eventY); 
				}
				
				findObjectsSelected(selecteerRechthoek);
				
				paint();
			}
			
		}
		
		
	}
	
	public void mouseUpTouchEndAction()
	{
		if (mouseMode == tekenen)
		{	
			Streep streep = new Streep(drawingColor, draggPoints);
			streepVector.addElement(streep);
			if (draggPoints.size() > 1)
				addToHistory();
			draggPoints.removeAllElements();
			
			paint();
		}
/*		
		else if (mouseMode == gummen)
		{
			updatePixelArray();
			gumPunten.removeAllElements();
			addToHistory();

		}
*/		
		else if (mouseMode == lijnTekenen)
		{
			
//			mouseDragged = false;
			
			if (lijnEinde != null)
			{	
				Lijn lijn = new Lijn(drawingColor, figuurStart.x, figuurStart.y, lijnEinde.x, lijnEinde.y);
				lijnVector.addElement(lijn);
				
			}
			figuurStart = null;
			lijnEinde = null;
			tekenRechthoek = null;
			
			addToHistory();
			
			paint();
			
		}
		else if (mouseMode == rechthoekTekenen)
		{
			if (tekenRechthoek != null)
			{	
				Rechthoek rechthoek = new Rechthoek(drawingColor, 
												tekenRechthoek.x, tekenRechthoek.y,
												tekenRechthoek.width, tekenRechthoek.height);
				rechthoekVector.addElement(rechthoek);
			}
			
			figuurStart = null;
			lijnEinde = null;
			tekenRechthoek = null;
			
			addToHistory();
			paint();
		}
		else if (mouseMode == cirkelTekenen)
		{	if (tekenRechthoek != null)
			{
				Ellips ellips = new Ellips(drawingColor, 
							       tekenRechthoek.x, tekenRechthoek.y,
								   tekenRechthoek.width, tekenRechthoek.height);
				ellipsVector.addElement(ellips);				
			}
			figuurStart = null;
			lijnEinde = null;
			tekenRechthoek = null;
		
			addToHistory();
			paint();				

		}	
		
		else if (mouseMode == tekstTekenen)
		{

		}
		else if (mouseMode == selecteren)
		{
			mouseDown = false;

			if (sleepSelectie)
			{	

				sleepSelectie = false;
				//resetSelectedObject();
				if (objectMoved)
					addToHistory();
				objectMoved = false;
				//sleepSelectie = false;
				paint();
			}

			
		}
		
	}
	
	//class MLMML extends MouseAdapter implements MouseMotionListener
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		
		//public void mousePressed(MouseEvent e)
		public void onMouseDown(MouseDownEvent e)
		{
			//e.preventDefault();
			
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		//public void mouseDragged(MouseEvent e)
		public void onMouseMove(MouseMoveEvent e)	
		{
			//e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
//System.out.println("mouse move veld");			
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();
			boolean shiftPressed = e.isShiftKeyDown();

//System.out.println("sp = " + shiftPressed);

			mouseMoveTouchMoveAction(eventX, eventY, shiftPressed);
			
			
			
		} // onMouseMove
		
		//public void mouseReleased(MouseEvent e)
		public void onMouseUp(MouseUpEvent e)	
		{
			//e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			mouseUpTouchEndAction();

		}

	} //MLMML


	// tablet, dwo 
	class MGWTTouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				
				int eventX = touch.getPageX() - kladjeHWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - kladjeHWTCanvas.getAbsoluteTop();				
				
				mouseDownTouchStartAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				//boolean shiftPressed = e.isShiftKeyDown();

			    boolean shiftPressed = false;
			    int eventX = touch.getPageX() - kladjeHWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - kladjeHWTCanvas.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(eventX, eventY, shiftPressed);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			mouseUpTouchEndAction();
		}

	}

}

class ColorBytes implements Serializable
{	
	int x, y;
	byte red, green, blue;
	
	public ColorBytes(int x, int y, int r, int g, int b)
	{	
		this.x = x;
		this.y = y;
		
		red = new Integer(r).byteValue(); 
		green = new Integer(g).byteValue();
		blue = new Integer(b).byteValue();;
	}
	
	public ColorBytes(int x, int y, CssColor c)
	{
		
		this.x = x;
		this.y = y;
		
		String cString = c.toString().substring(4, c.toString().length() - 1);
		String[] kleurenStr = StringUtils.split(cString, ",");

		int intBlue =  Integer.parseInt(kleurenStr[2]);
		int intGreen = Integer.parseInt(kleurenStr[1]);
		int intRed =   Integer.parseInt(kleurenStr[0]);
		
		red = new Integer(intRed).byteValue(); 
		green = new Integer(intGreen).byteValue();
		blue = new Integer(intBlue).byteValue();;
	}

	public CssColor makeColor()
	{	int intRed = red;
		if (intRed < 0)
			intRed += 256;
		int intGreen = green;
		if (intGreen < 0)
			intGreen += 256;
		int intBlue = blue;
		if (intBlue < 0)
			intBlue += 256;
		
		return CssColor.make(intRed, intGreen, intBlue);
	}
	
	public void zetColor(CssColor c)
	{
		
		String cString = c.toString().substring(4, c.toString().length() - 1);
		String[] kleurenStr = StringUtils.split(cString, ",");

		int intBlue =  Integer.parseInt(kleurenStr[2]);
		int intGreen = Integer.parseInt(kleurenStr[1]);
		int intRed =   Integer.parseInt(kleurenStr[0]);
		
		red = new Integer(intRed).byteValue(); 
		green = new Integer(intGreen).byteValue();
		blue = new Integer(intBlue).byteValue();;
		
	}
}
class Point
{
	int x; int y;
	
	public Point(int x, int y)
	{
		this.x = x; this.y = y;
	}
}

class Rectangle
{
	int x; int y; int width; int height;
	
	public Rectangle(int x, int y, int w, int h)
	{
		this.x = x; this.y = y;
		width = w; height = h;
	}
	
	public Rectangle(Rectangle r)
	{
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
	}
	
	public boolean contains(int px, int py)
	{
		return (px >= x) && (px <= (x + width)) &&
		       (py >= y) && (py <= (y + height));
	}
	
	public void translate(int dx, int dy)
	{
		x += dx;
		y += dy;
	}
}