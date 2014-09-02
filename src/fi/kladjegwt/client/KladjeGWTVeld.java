package fi.kladjegwt.client;

import java.awt.Color;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.HashMap;
import java.util.ArrayList;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

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
	public static Context2d gIm;
	
	static double NZERO = 1e-5d;
	
	static boolean roteren = true;
	static boolean schalen = true;
	
	boolean handleAction = false;
	boolean groupHandleAction = false;
	boolean scalingTopRight = false;
	boolean scalingTopLeft = false;
	boolean scalingBottomRight = false;
	boolean scalingBottomLeft = false;
	boolean rotatingEast = false;
	boolean rotatingWest = false;
	
	boolean lijnen = false;
	boolean ruitjes = true;
	
	int lineDistance = 20;
	int gridSize = 20;
	
	static CssColor lightBlue = CssColor.make(148, 148, 255);
	CssColor lijnenKleur = CssColor.make(150, 150, 255);
	CssColor ruitjesKleur = CssColor.make(210, 210, 210);
	static CssColor bbColor = lightBlue; 
	static CssColor hbColor = CssColor.make(0, 0, 255);
	
	static int minHandleBoxSize = 50;

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
	
	final int GAUSSIAN = 0;
	final int AVERAGE = 1;
	final int AVERAGE2 = 2;
	int smoothType = AVERAGE2;

	ArrayList<DoublePoint> draggDoublePoints = new ArrayList<DoublePoint>();	
//	Vector<Point> draggPoints = new Vector<Point>();
	//Vector gumPunten = new Vector();
	//int gumGrootte = 7; // oneven	

	Point figuurStart = null;
	Point lijnEinde = null;
	Rectangle tekenRechthoek = null;
//	KladjeRectangle tekstRechthoek = null;
	Rectangle selecteerRechthoek = null;
//	KladjeRectangle wisRechthoek = null;
	TekstElement tekstEdited = null;

	Polygon topRightHandle, bottomRightHandle, topLeftHandle, bottomLeftHandle;
	Rectangle topRightRect, bottomRightRect, topLeftRect, bottomLeftRect;
	Rectangle rotateEastHandle, rotateWestHandle;
	int hbFactor = 4;

	// backwards compatibility
	//ColorBytes[][] pixels = null;
	//Vector<ColorBytes> pixels = new Vector<ColorBytes>();
	
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
	boolean objectHandled = false;
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
	
	double rotateStep = Math.PI / 18; // 10 degrees in radians
	double angleSum = 0;
	double scaleUpStep = 105e-2d;
	double scaleDownStep = 1 / 105e-2d;
	
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
//		Vector<ArrayList<Short>> gwtStateVector = getGWTState();
//		if (gwtStateVector.size() > 0)
//		{	h.put("gwtpixels", gwtStateVector);
//		}

		List<Map<String,Object>> strepen = new ArrayList<Map<String,Object>>(); 
		//HashMap<String,Object>[] strepen = new HashMap[streepVector.size()];
		for (int sCnt = 0; sCnt < streepVector.size(); sCnt++)
		{	Streep streep = (Streep) streepVector.elementAt(sCnt);
			//strepen[sCnt] = streep.getState();
			strepen.add(streep.getState());
		}
		h.put("strepen", strepen);
		
		List<Map<String,Object>> lijnenAL = new ArrayList<Map<String,Object>>();		
		//HashMap<String,Object>[] lijnenHash = new HashMap[lijnVector.size()];
		for (int lCnt = 0; lCnt < lijnVector.size(); lCnt++)
		{	Lijn lijn = (Lijn) lijnVector.elementAt(lCnt);
			//lijnenHash[lCnt] = lijn.getState();
			lijnenAL.add(lijn.getState());
		}
		h.put("lijnenhash", lijnenAL); // !!!
		
		List<Map<String,Object>> rechthoeken = new ArrayList<Map<String,Object>>();		
		//HashMap<String,Object>[] rechthoeken = new HashMap[rechthoekVector.size()];
		for (int rCnt = 0; rCnt < rechthoekVector.size(); rCnt++)
		{	Rechthoek rechthoek = (Rechthoek) rechthoekVector.elementAt(rCnt);
			//rechthoeken[rCnt] = rechthoek.getState();
			rechthoeken.add(rechthoek.getState());
		}
		h.put("rechthoeken", rechthoeken);
		
		List<Map<String,Object>> ellipsen = new ArrayList<Map<String,Object>>();		
		//HashMap<String,Object>[] ellipsen = new HashMap[ellipsVector.size()];
		for (int eCnt = 0; eCnt < ellipsVector.size(); eCnt++)
		{	Ellips ellips = (Ellips) ellipsVector.elementAt(eCnt);
			//ellipsen[eCnt] = ellips.getState();
			ellipsen.add(ellips.getState());
		}
		h.put("ellipsen", ellipsen);
		
		List<Map<String,Object>> tekstElementen = new ArrayList<Map<String,Object>>();		
		//HashMap<String,Object>[] tekstElementen = new HashMap[tekstElementVector.size()];
		for (int tCnt = 0; tCnt < tekstElementVector.size(); tCnt++)
		{	TekstElement tekstElement = (TekstElement) tekstElementVector.elementAt(tCnt);
			//tekstElementen[tCnt] = tekstElement.getState();
			tekstElementen.add(tekstElement.getState());
		}
		h.put("tekstElementen", tekstElementen);

System.out.println("kgwtv get " + tekstElementen.size());		
		
		
		return h;


		//return stateMap;
	}

/*	
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
*/
	
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

	
	public void setState(Map<String, Object> map)
	{
		
		//System.out.println("kv setState");

		ObjectMap launchState = JSONUtilities.wrapMap(map);
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
		List<Map<String,Object>> strepen = new ArrayList<Map<String,Object>>();
		if (launchState.containsKey("strepen"))
			strepen = launchState.getMapList("strepen");
		for (int sCnt = 0; sCnt < strepen.size(); sCnt++)
		{	Streep streep = Streep.setState(strepen.get(sCnt));
			streepVector.addElement(streep);
		}
		
		lijnVector.removeAllElements();
		//HashMap<String,Object>[] lijnenHash = new HashMap[0];
		List<Map<String,Object>> lijnenAL = new ArrayList<Map<String,Object>>();
		// launchdata and setState
		if (launchState.containsKey("lijnenhash"))
			lijnenAL = launchState.getMapList("lijnenhash");
		for (int lCnt = 0; lCnt < lijnenAL.size(); lCnt++)
		{	Lijn lijn = Lijn.setState(lijnenAL.get(lCnt));
			lijnVector.addElement(lijn);
		}
		
		rechthoekVector.removeAllElements();
		//HashMap<String,Object>[] rechthoeken = new HashMap[0];
		List<Map<String,Object>> rechthoeken = new ArrayList<Map<String,Object>>();
		if (launchState.containsKey("rechthoeken"))
			rechthoeken = launchState.getMapList("rechthoeken");
		for (int rCnt = 0; rCnt < rechthoeken.size(); rCnt++)
		{	Rechthoek rechthoek = Rechthoek.setState(rechthoeken.get(rCnt));
			rechthoekVector.addElement(rechthoek);
		}

		ellipsVector.removeAllElements();
		//HashMap<String,Object>[] ellipsen = new HashMap[0];
		List<Map<String,Object>> ellipsen = new ArrayList<Map<String,Object>>();
		if (launchState.containsKey("ellipsen"))
			ellipsen = launchState.getMapList("ellipsen");
		for (int eCnt = 0; eCnt < ellipsen.size(); eCnt++)
		{	Ellips ellips = Ellips.setState(ellipsen.get(eCnt));
			ellipsVector.addElement(ellips);
		}

		tekstElementVector.removeAllElements();
		//HashMap<String,Object>[] tekstElementen = new HashMap[0];
		List<Map<String,Object>> tekstElementen = new ArrayList<Map<String,Object>>();
		if (launchState.containsKey("tekstElementen"))
			tekstElementen = launchState.getMapList("tekstElementen");
		for (int tCnt = 0; tCnt < tekstElementen.size(); tCnt++)
		{	TekstElement tekstElement = TekstElement.setState(tekstElementen.get(tCnt));
			tekstElementVector.addElement(tekstElement);
		}
System.out.println("kgwtv set " + tekstElementen.size());		

		paint();
	}

/*	
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
*/	
	
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
//			pixels.removeAllElements();
			
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
	
	public ArrayList<DoublePoint> gaussianSmooth(ArrayList<DoublePoint> doublePoints)
	{
		if (doublePoints.size() < 3) 
			return doublePoints;
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>();
		pointsNew.add(doublePoints.get(0));		
		for (int i = 1; i < doublePoints.size() - 1; i++)
		{
			DoublePoint pOld0 = doublePoints.get(i-1);
			DoublePoint pOld1 = doublePoints.get(i);
			DoublePoint pOld2 = doublePoints.get(i+1);
			DoublePoint smoothedPoint = new DoublePoint(pOld0.x / 4 + pOld1.x / 2 + pOld2.x / 4,
														pOld0.y / 4 + pOld1.y / 2 + pOld2.y / 4);
			pointsNew.add(smoothedPoint);
		}
		pointsNew.add(doublePoints.get(doublePoints.size() - 1));
		
		return pointsNew;
		
	}

	public ArrayList<DoublePoint> averageSmooth(ArrayList<DoublePoint> doublePoints)
	{
		if (doublePoints.size() < 5) 
			return doublePoints;
		ArrayList<DoublePoint> pointsNew = new ArrayList<DoublePoint>();
		pointsNew.add(doublePoints.get(0));		
		pointsNew.add(doublePoints.get(1));
		for (int i = 2; i < doublePoints.size() - 2; i++)
		{
			DoublePoint pOld0 = doublePoints.get(i-2);
			DoublePoint pOld1 = doublePoints.get(i-1);
			DoublePoint pOld2 = doublePoints.get(i);
			DoublePoint pOld3 = doublePoints.get(i+1);
			DoublePoint pOld4 = doublePoints.get(i+2);
			
			DoublePoint smoothedPoint = new DoublePoint(pOld0.x/5 + pOld1.x/5 + pOld2.x/5 + pOld3.x/5 + pOld4.x/5,
														pOld0.y/5 + pOld1.y/5 + pOld2.y/5 + pOld3.y/5 + pOld4.y/5);
			pointsNew.add(smoothedPoint);
		}
		pointsNew.add(doublePoints.get(doublePoints.size() - 1));
		
		return pointsNew;
		
	}

	public ArrayList<DoublePoint> smooth(ArrayList<DoublePoint> doublePoints, int smoothType)
	{
		if (smoothType == GAUSSIAN)
			return gaussianSmooth(doublePoints);
		else if (smoothType == AVERAGE)
			return averageSmooth(doublePoints);
		else if (smoothType == AVERAGE2)
		{	ArrayList<DoublePoint> oneSmooth = averageSmooth(doublePoints);
			return averageSmooth(oneSmooth);			
		}
		else
			return doublePoints;
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

/*		
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
*/		
		
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
		
		if (draggDoublePoints.size() == 1)
		{	DoublePoint p = (DoublePoint) draggDoublePoints.get(0);
			//g.moveTo(p.x, p.y);
			//g.lineTo(p.x, p.y);
			g.strokeRect(p.x, p.y, 1, 1);
		}
		if (draggDoublePoints.size() > 1)
		{	
			ArrayList<DoublePoint> smoothedDraggDoublePoints = smooth(draggDoublePoints, smoothType);
			
			DoublePoint p1 = (DoublePoint) smoothedDraggDoublePoints.get(0);
			g.beginPath();
			g.moveTo(p1.x, p1.y);
			for (int pCnt = 1; pCnt < smoothedDraggDoublePoints.size(); pCnt++)
			{	DoublePoint p2 = (DoublePoint) smoothedDraggDoublePoints.get(pCnt);
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
				if (schalen)
				{	
					if (topRightHandle != null)
					{	//g.setColor(hbColor);
						g.setStrokeStyle(hbColor);
						//g.drawPolygon(topRightHandle);
						
						g.beginPath();		
						g.moveTo(topRightHandle.doubleX[0], topRightHandle.doubleY[0]);
						for (int k = 1; k < topRightHandle.aantalPunten; k++) 
						{	g.lineTo(topRightHandle.doubleX[k], topRightHandle.doubleY[k]);
						}
						g.lineTo(topRightHandle.doubleX[0], topRightHandle.doubleY[0]);
						g.closePath();
						g.stroke();

		//g.setColor(Color.red);
		//g.drawRect(topRightRect.x,topRightRect.y,topRightRect.width,topRightRect.height);
					}
					if (topLeftHandle != null)
					{	//g.setColor(hbColor);
						g.setStrokeStyle(hbColor);
						//g.drawPolygon(topLeftHandle);
						
						g.beginPath();		
						g.moveTo(topLeftHandle.doubleX[0], topLeftHandle.doubleY[0]);
						for (int k = 1; k < topLeftHandle.aantalPunten; k++) 
						{	g.lineTo(topLeftHandle.doubleX[k], topLeftHandle.doubleY[k]);
						}
						g.lineTo(topLeftHandle.doubleX[0], topLeftHandle.doubleY[0]);
						g.closePath();
						g.stroke();
		//g.setColor(Color.red);
		//g.drawRect(topLeftRect.x,topLeftRect.y,topLeftRect.width,topLeftRect.height);
					
					}
					if (bottomRightHandle != null)
					{	//g.setColor(hbColor);
						g.setStrokeStyle(hbColor);
						//g.drawPolygon(bottomRightHandle);
						
						g.beginPath();		
						g.moveTo(bottomRightHandle.doubleX[0], bottomRightHandle.doubleY[0]);
						for (int k = 1; k < bottomRightHandle.aantalPunten; k++) 
						{	g.lineTo(bottomRightHandle.doubleX[k], bottomRightHandle.doubleY[k]);
						}
						g.lineTo(bottomRightHandle.doubleX[0], bottomRightHandle.doubleY[0]);
						g.closePath();
						g.stroke();
		//g.setColor(Color.red);
		//g.drawRect(bottomRightRect.x,bottomRightRect.y,bottomRightRect.width,bottomRightRect.height);
					
					}
					if (bottomLeftHandle != null)
					{	//g.setColor(hbColor);
						g.setStrokeStyle(hbColor);
						//g.drawPolygon(bottomLeftHandle);
						
						g.beginPath();		
						g.moveTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
						for (int k = 1; k < bottomLeftHandle.aantalPunten; k++) 
						{	g.lineTo(bottomLeftHandle.doubleX[k], bottomLeftHandle.doubleY[k]);
						}
						g.lineTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
						g.closePath();
						g.stroke();

		//g.setColor(Color.red);
		//g.drawRect(bottomLeftRect.x,bottomLeftRect.y,bottomLeftRect.width,bottomLeftRect.height);
					
					}
				} // if schalen
				if (roteren)
				{
					if (rotateEastHandle != null)
					{	//g.setColor(KladjeVeld.hbColor);
						g.setStrokeStyle(hbColor);
						//g.drawOval(selecteerRechthoek.x + selecteerRechthoek.width, // - 2 * hbFactor,
						//		   selecteerRechthoek.y + selecteerRechthoek.height/2 - 2 * hbFactor, 
						//		   4 * hbFactor, 4 * hbFactor);
						
						g.beginPath();
			            g.arc(selecteerRechthoek.x + selecteerRechthoek.width + 2 * hbFactor, 
			            	  selecteerRechthoek.y + selecteerRechthoek.height/2, 2 * hbFactor, 0, 2 * Math.PI);
			       	 	g.stroke();
						
			//g.setColor(Color.red);			
			//g.drawRect(rotateEastHandle.x, rotateEastHandle.y, rotateEastHandle.width, rotateEastHandle.height);
					}
					if (rotateWestHandle != null)
					{	//g.setColor(KladjeVeld.hbColor);
						g.setStrokeStyle(hbColor);
						//g.drawOval(selecteerRechthoek.x - 4 * hbFactor, 
						//		   selecteerRechthoek.y + selecteerRechthoek.height/2 - 2 * hbFactor, 
						//		   4 * hbFactor, 4 * hbFactor);
						
						g.beginPath();
			            g.arc(selecteerRechthoek.x - 2 * hbFactor, 
			            		selecteerRechthoek.y + selecteerRechthoek.height/2, 2 * hbFactor, 0, 2 * Math.PI);
			       	 	g.stroke();
			//g.setColor(Color.red);			
			//g.drawRect(rotateWestHandle.x, rotateWestHandle.y, rotateWestHandle.width, rotateWestHandle.height);
					}

				}

		}
		
		if (mouseMode == selecteren)
		{
			if (selectedStreep != null)
			{	//selectedStreep.tekenBB(g);
				selectedStreep.tekenHandleBox(g);
			}
			if (selectedLijn != null)
			{	//selectedLijn.tekenBB(g);
				selectedLijn.tekenHandleBox(g);
			}
			if (selectedRechthoek != null)
			{	//selectedRechthoek.tekenBB(g);
				selectedRechthoek.tekenHandleBox(g);
			}
			if (selectedEllips != null)
			{	//selectedEllips.tekenBB(g);
				selectedEllips.tekenHandleBox(g);
			}
			if (selectedTekstElement != null)
			{	//selectedTekstElement.tekenBB(g);
				selectedTekstElement.tekenHandleBox(g);
			}
			
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
	
	public void updateSelecteerRechthoek()
	{
		if (selecteerRechthoek == null)
			return;
		
		int minX = 1000;
		int maxX = -100;
		int minY = 1000;
		int maxY = -100;
		
		for (int cnt = 0; cnt < objectsSelected.size(); cnt++)
		{
			Object ob = objectsSelected.elementAt(cnt);
			if (ob instanceof Streep)
			{	Rectangle r = ((Streep) ob).handleBox;
				if (r.x < minX)
					minX = r.x;
				if ((r.x + r.width) > maxX)
					maxX = r.x + r.width;
				if (r.y < minY)
					minY = r.y;
				if ((r.y + r.height) > maxY)
					maxY = r.y + r.height;
			}
			else if (ob instanceof Lijn)
			{	Rectangle r = ((Lijn) ob).handleBox;
				if (r.x < minX)
					minX = r.x;
				if ((r.x + r.width) > maxX)
					maxX = r.x + r.width;
				if (r.y < minY)
					minY = r.y;
				if ((r.y + r.height) > maxY)
					maxY = r.y + r.height;
			}
			if (ob instanceof Rechthoek)
			{	Rectangle r = ((Rechthoek) ob).handleBox;
				if (r.x < minX)
					minX = r.x;
				if ((r.x + r.width) > maxX)
					maxX = r.x + r.width;
				if (r.y < minY)
					minY = r.y;
				if ((r.y + r.height) > maxY)
					maxY = r.y + r.height;
			}
			if (ob instanceof Ellips)
			{	Rectangle r = ((Ellips) ob).handleBox;
				if (r.x < minX)
					minX = r.x;
				if ((r.x + r.width) > maxX)
					maxX = r.x + r.width;
				if (r.y < minY)
					minY = r.y;
				if ((r.y + r.height) > maxY)
					maxY = r.y + r.height;
			}
			if (ob instanceof TekstElement)
			{	Rectangle r = ((TekstElement) ob).handleBox;
				if (r.x < minX)
					minX = r.x;
				if ((r.x + r.width) > maxX)
					maxX = r.x + r.width;
				if (r.y < minY)
					minY = r.y;
				if ((r.y + r.height) > maxY)
					maxY = r.y + r.height;
			}
		} // for
		int w = maxX - minX + 2 * hbFactor;
		int h = maxY - minY + 2 * hbFactor;
		selecteerRechthoek = new Rectangle(minX - hbFactor, minY - hbFactor, w, h);
		if (schalen)
			makeScaleHandles();
		if (roteren)
			makeRotateHandles();

		
	}
	
	public void makeScaleHandles()
	{
		if (selecteerRechthoek == null)
			return;
		
		topRightHandle = new Polygon();
		topRightHandle.addPoint(selecteerRechthoek.x + selecteerRechthoek.width + hbFactor,
								selecteerRechthoek.y - hbFactor);
		topRightHandle.addPoint(selecteerRechthoek.x + selecteerRechthoek.width - 3 * hbFactor,
								selecteerRechthoek.y - hbFactor);
		topRightHandle.addPoint(selecteerRechthoek.x + selecteerRechthoek.width + hbFactor,
								selecteerRechthoek.y + 3 * hbFactor);
		topRightRect = new Rectangle(selecteerRechthoek.x + selecteerRechthoek.width - 3 * hbFactor,
									 selecteerRechthoek.y - hbFactor, 4 * hbFactor, 4 * hbFactor);
		
		topLeftHandle = new Polygon();
		topLeftHandle.addPoint(selecteerRechthoek.x - hbFactor, selecteerRechthoek.y - hbFactor);
		topLeftHandle.addPoint(selecteerRechthoek.x + 3 * hbFactor, selecteerRechthoek.y - hbFactor);
		topLeftHandle.addPoint(selecteerRechthoek.x - hbFactor, selecteerRechthoek.y + 3 * hbFactor);
		topLeftRect = new Rectangle(selecteerRechthoek.x - hbFactor, selecteerRechthoek.y - hbFactor, 4 * hbFactor, 4 * hbFactor);
		
		bottomRightHandle = new Polygon();
		bottomRightHandle.addPoint(selecteerRechthoek.x + selecteerRechthoek.width + hbFactor,
								   selecteerRechthoek.y + selecteerRechthoek.height + hbFactor);
		bottomRightHandle.addPoint(selecteerRechthoek.x + selecteerRechthoek.width - 3 * hbFactor,
								   selecteerRechthoek.y + selecteerRechthoek.height + hbFactor);
		bottomRightHandle.addPoint(selecteerRechthoek.x + selecteerRechthoek.width + hbFactor,
								   selecteerRechthoek.y + selecteerRechthoek.height - 3 * hbFactor);
		bottomRightRect = new Rectangle(selecteerRechthoek.x + selecteerRechthoek.width - 3 * hbFactor,
				   						selecteerRechthoek.y + selecteerRechthoek.height - 3 * hbFactor, 4 * hbFactor, 4 * hbFactor);		
		
		bottomLeftHandle = new Polygon();
		bottomLeftHandle.addPoint(selecteerRechthoek.x - hbFactor, selecteerRechthoek.y + selecteerRechthoek.height + hbFactor);
		bottomLeftHandle.addPoint(selecteerRechthoek.x + 3 * hbFactor, selecteerRechthoek.y + selecteerRechthoek.height + hbFactor);
		bottomLeftHandle.addPoint(selecteerRechthoek.x - hbFactor, selecteerRechthoek.y + selecteerRechthoek.height - 3 * hbFactor);
		bottomLeftRect = new Rectangle(selecteerRechthoek.x - hbFactor, selecteerRechthoek.y + selecteerRechthoek.height - 3 * hbFactor, 
									   4 * hbFactor, 4 * hbFactor);		
		
	}
	
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
	
	public void makeRotateHandles()
	{
		
		if (selecteerRechthoek == null)
			return;
		
		rotateEastHandle = new Rectangle(selecteerRechthoek.x + selecteerRechthoek.width,// - 2 * hbFactor,
										 selecteerRechthoek.y + selecteerRechthoek.height/2 - 2 * hbFactor,
										 4 * hbFactor, 4 * hbFactor);
		rotateWestHandle = new Rectangle(selecteerRechthoek.x - 4 * hbFactor, 
										 selecteerRechthoek.y + selecteerRechthoek.height/2 - 2 * hbFactor,
										 4 * hbFactor, 4 * hbFactor);
	}

	public void killRotateHandles()
	{
		rotateEastHandle = null; 
		rotateWestHandle = null;
	}

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
		killScaleHandles();
		killRotateHandles();

		
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
		if  (selectedTekstElement != null)
			selectedTekstElement.rotate(rotateStep);
	
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
		if  (selectedTekstElement != null)
			selectedTekstElement.scale(scaleStep);
		
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
		return ((selectedStreep != null) && 
				 //selectedStreep.bbContains(x, y)) ||
				 selectedStreep.handleBox.contains(x, y)) ||
			   ((selectedLijn != null) && 
				 //selectedLijn.bbContains(x, y)) ||
			     selectedLijn.handleBox.contains(x, y)) ||	   
			   ((selectedRechthoek != null) && 
				 //selectedRechthoek.bbContains(x, y)) ||
				 selectedRechthoek.handleBox.contains(x, y)) ||	   
			   ((selectedEllips != null) && 
				 //selectedEllips.bbContains(x, y)) ||
				 selectedEllips.handleBox.contains(x, y)) ||	   
			   ((selectedTekstElement != null) && 
				 //selectedTekstElement.bbContains(x, y));
			     selectedTekstElement.handleBox.contains(x, y));	   
	}
	
	public void processSelecteerRechthoekHandleAction(int dx, int dy)
	{
		int crx = selecteerRechthoek.x + selecteerRechthoek.width / 2;
		int cry = selecteerRechthoek.y + selecteerRechthoek.height / 2;
		
		if (scalingTopRight)
		{
			double aspectDirX = selecteerRechthoek.x + selecteerRechthoek.width - 
								crx;
			double aspectDirY = selecteerRechthoek.y - cry;
			double dxDouble = (double) dx;
			double dyDouble = (double) dy;
			double aa = aspectDirX * aspectDirX + aspectDirY * aspectDirY;
			double s = (aspectDirX * dxDouble + aspectDirY * dyDouble) / aa;
			double asXDouble = s * aspectDirX;
			double asYDouble = s * aspectDirY;
			double oldWidth = (double) selecteerRechthoek.width / 2;
			double oldHeight = (double) selecteerRechthoek.height / 2;
			double newWidth = oldWidth + asXDouble;
			double newHeight = oldHeight - asYDouble;
			double sc = ((double) newWidth) / oldWidth;
			for (int cnt = 0; cnt < objectsSelected.size(); cnt++)
			{
				Object ob = objectsSelected.elementAt(cnt);
				if (ob instanceof Streep)
					((Streep) ob).scale(sc, crx, cry);
				else if (ob instanceof Lijn)
					((Lijn) ob).scale(sc, crx, cry);
				else if (ob instanceof Rechthoek)
					((Rechthoek) ob).scale(sc, crx, cry);
				else if (ob instanceof Ellips)
					((Ellips) ob).scale(sc, crx, cry);
				else if (ob instanceof TekstElement)
					((TekstElement) ob).scale(sc, crx, cry);
				
			}

			int tlx = selecteerRechthoek.x;
			int tly = selecteerRechthoek.y;
			int b = selecteerRechthoek.width;
			int h = selecteerRechthoek.height;
				
			int ntlx = (int) Math.round(sc * tlx + (1 - sc) * crx);
			int ntly = (int) Math.round(sc * tly + (1 - sc) * cry);
			int nb = (int) Math.round(sc * b);
			int nh = (int) Math.round(sc * h);
			selecteerRechthoek = new Rectangle(ntlx, ntly, nb, nh);
			
			topRightHandle.translate(ntlx + nb - tlx - b, ntly - tly);
			topRightRect.translate(ntlx + nb - tlx - b, ntly - tly);
			topLeftHandle.translate(ntlx - tlx, ntly - tly);
			topLeftRect.translate(ntlx - tlx, ntly - tly);
			bottomRightHandle.translate(ntlx + nb - tlx - b, ntly + nh - tly - h);
			bottomRightRect.translate(ntlx + nb - tlx - b, ntly + nh - tly - h);
			bottomLeftHandle.translate(ntlx - tlx, ntly + nh - tly - h);
			bottomLeftRect.translate(ntlx - tlx, ntly + nh- tly - h);

			if (roteren)
			{	
				rotateEastHandle.translate(ntlx + nb - tlx - b, ntly + nh/2 - tly - h/2);
				rotateWestHandle.translate(ntlx - tlx, ntly + nh/2 - tly - h/2);
			}	
			
		}
		else if (scalingTopLeft)
		{
			double dxDouble = (double) dx;
			double dyDouble = (double) dy;
			
			//double dxInvRot = selectedStreep.inverseRotX(dxDouble, dyDouble);
			//double dyInvRot = selectedStreep.inverseRotY(dxDouble, dyDouble);
			//double oldWidth = (double) selectedStreep.breedte / 2;
			//double oldHeight = (double) selectedStreep.hoogte / 2;
			
			double oldWidth = (double) selecteerRechthoek.width / 2;
			double oldHeight = (double) selecteerRechthoek.height / 2;

			double newWidth = oldWidth - dx;
			double newHeight = oldHeight - dy;
			double sx = newWidth / oldWidth;
			double sy = newHeight / oldHeight;

			for (int cnt = 0; cnt < objectsSelected.size(); cnt++)
			{
				Object ob = objectsSelected.elementAt(cnt);
				if (ob instanceof Streep)
					((Streep) ob).scale(sx, sy, crx, cry);
				else if (ob instanceof Lijn)
					((Lijn) ob).scale(sx, sy, crx, cry);
				else if (ob instanceof Rechthoek)
					((Rechthoek) ob).scale(sx, sy, crx, cry);
				else if (ob instanceof Ellips)
					((Ellips) ob).scale(sx, sy, crx, cry);
				else if (ob instanceof TekstElement)
					((TekstElement) ob).scale(sx, sy, crx, cry);
				
			}

			int tlx = selecteerRechthoek.x;
			int tly = selecteerRechthoek.y;
			int b = selecteerRechthoek.width;
			int h = selecteerRechthoek.height;
				
			int ntlx = (int) Math.round(sx * tlx + (1 - sx) * crx);
			int ntly = (int) Math.round(sy * tly + (1 - sy) * cry);
			int nb = (int) Math.round(sx * b);
			int nh = (int) Math.round(sy * h);
			selecteerRechthoek = new Rectangle(ntlx, ntly, nb, nh);
			
			topRightHandle.translate(ntlx + nb - tlx - b, ntly - tly);
			topRightRect.translate(ntlx + nb - tlx - b, ntly - tly);
			topLeftHandle.translate(ntlx - tlx, ntly - tly);
			topLeftRect.translate(ntlx - tlx, ntly - tly);
			bottomRightHandle.translate(ntlx + nb - tlx - b, ntly + nh - tly - h);
			bottomRightRect.translate(ntlx + nb - tlx - b, ntly + nh - tly - h);
			bottomLeftHandle.translate(ntlx - tlx, ntly + nh - tly - h);
			bottomLeftRect.translate(ntlx - tlx, ntly + nh- tly - h);

			if (roteren)
			{	
				rotateEastHandle.translate(ntlx + nb - tlx - b, ntly + nh/2 - tly - h/2);
				rotateWestHandle.translate(ntlx - tlx, ntly + nh/2 - tly - h/2);
			}	

		}
		else if (scalingBottomLeft)
		{
			double dxDouble = (double) dx;
			double dyDouble = (double) dy;
			
			//double dxInvRot = selectedStreep.inverseRotX(dxDouble, dyDouble);
			//double dyInvRot = selectedStreep.inverseRotY(dxDouble, dyDouble);
			//double oldWidth = (double) selectedStreep.breedte / 2;
			//double oldHeight = (double) selectedStreep.hoogte / 2;
			
			double oldWidth = (double) selecteerRechthoek.width / 2;
			double oldHeight = (double) selecteerRechthoek.height / 2;

			double newWidth = oldWidth - dx;
			double newHeight = oldHeight + dy;
			double sx = newWidth / oldWidth;
			double sy = newHeight / oldHeight;

			for (int cnt = 0; cnt < objectsSelected.size(); cnt++)
			{
				Object ob = objectsSelected.elementAt(cnt);
				if (ob instanceof Streep)
					((Streep) ob).scale(sx, sy, crx, cry);
				else if (ob instanceof Lijn)
					((Lijn) ob).scale(sx, sy, crx, cry);
				else if (ob instanceof Rechthoek)
					((Rechthoek) ob).scale(sx, sy, crx, cry);
				else if (ob instanceof Ellips)
					((Ellips) ob).scale(sx, sy, crx, cry);
				else if (ob instanceof TekstElement)
					((TekstElement) ob).scale(sx, sy, crx, cry);
				
			}
			
			int tlx = selecteerRechthoek.x;
			int tly = selecteerRechthoek.y;
			int b = selecteerRechthoek.width;
			int h = selecteerRechthoek.height;
				
			int ntlx = (int) Math.round(sx * tlx + (1 - sx) * crx);
			int ntly = (int) Math.round(sy * tly + (1 - sy) * cry);
			int nb = (int) Math.round(sx * b);
			int nh = (int) Math.round(sy * h);
			selecteerRechthoek = new Rectangle(ntlx, ntly, nb, nh);
			
			topRightHandle.translate(ntlx + nb - tlx - b, ntly - tly);
			topRightRect.translate(ntlx + nb - tlx - b, ntly - tly);
			topLeftHandle.translate(ntlx - tlx, ntly - tly);
			topLeftRect.translate(ntlx - tlx, ntly - tly);
			bottomRightHandle.translate(ntlx + nb - tlx - b, ntly + nh - tly - h);
			bottomRightRect.translate(ntlx + nb - tlx - b, ntly + nh - tly - h);
			bottomLeftHandle.translate(ntlx - tlx, ntly + nh - tly - h);
			bottomLeftRect.translate(ntlx - tlx, ntly + nh- tly - h);
			
			if (roteren)
			{	
				rotateEastHandle.translate(ntlx + nb - tlx - b, ntly + nh/2 - tly - h/2);
				rotateWestHandle.translate(ntlx - tlx, ntly + nh/2 - tly - h/2);
			}	

		}
		else if (scalingBottomRight)
		{
			double aspectDirX = selecteerRechthoek.x + selecteerRechthoek.width - crx; 
			double aspectDirY = selecteerRechthoek.y + selecteerRechthoek.height - cry;
			double dxDouble = (double) dx;
			double dyDouble = (double) dy;
			double aa = aspectDirX * aspectDirX + aspectDirY * aspectDirY;
			double s = (aspectDirX * dxDouble + aspectDirY * dyDouble) / aa;
			double asXDouble = s * aspectDirX;
			double asYDouble = s * aspectDirY;
			double oldWidth = (double) selecteerRechthoek.width / 2;
			double oldHeight = (double) selecteerRechthoek.height / 2;
			double newWidth = oldWidth + asXDouble;
			double newHeight = oldHeight + asYDouble;
			double sc = ((double) newWidth) / oldWidth;
			
			for (int cnt = 0; cnt < objectsSelected.size(); cnt++)
			{
				Object ob = objectsSelected.elementAt(cnt);
				if (ob instanceof Streep)
					((Streep) ob).scale(sc, crx, cry);
				else if (ob instanceof Lijn)
					((Lijn) ob).scale(sc, crx, cry);
				else if (ob instanceof Rechthoek)
					((Rechthoek) ob).scale(sc, crx, cry);
				else if (ob instanceof Ellips)
					((Ellips) ob).scale(sc, crx, cry);
				else if (ob instanceof TekstElement)
					((TekstElement) ob).scale(sc, crx, cry);
				
			}

			int tlx = selecteerRechthoek.x;
			int tly = selecteerRechthoek.y;
			int b = selecteerRechthoek.width;
			int h = selecteerRechthoek.height;
				
			int ntlx = (int) Math.round(sc * tlx + (1 - sc) * crx);
			int ntly = (int) Math.round(sc * tly + (1 - sc) * cry);
			int nb = (int) Math.round(sc * b);
			int nh = (int) Math.round(sc * h);
			selecteerRechthoek = new Rectangle(ntlx, ntly, nb, nh);
			
			topRightHandle.translate(ntlx + nb - tlx - b, ntly - tly);
			topRightRect.translate(ntlx + nb - tlx - b, ntly - tly);
			topLeftHandle.translate(ntlx - tlx, ntly - tly);
			topLeftRect.translate(ntlx - tlx, ntly - tly);
			bottomRightHandle.translate(ntlx + nb - tlx - b, ntly + nh - tly - h);
			bottomRightRect.translate(ntlx + nb - tlx - b, ntly + nh - tly - h);
			bottomLeftHandle.translate(ntlx - tlx, ntly + nh - tly - h);
			bottomLeftRect.translate(ntlx - tlx, ntly + nh- tly - h);

			if (roteren)
			{	
				rotateEastHandle.translate(ntlx + nb - tlx - b, ntly + nh/2 - tly - h/2);
				rotateWestHandle.translate(ntlx - tlx, ntly + nh/2 - tly - h/2);
			}	

		}
		
		else if (rotatingEast)
		{
			// hier is alleen dy van belang
			double angle = Math.atan(((double) dy) / (selecteerRechthoek.width/2));
			//selectedStreep.rotate(angle);
			for (int cnt = 0; cnt < objectsSelected.size(); cnt++)
			{
				Object ob = objectsSelected.elementAt(cnt);
				if (ob instanceof Streep)
					((Streep) ob).rotate(angle, crx, cry);
				else if (ob instanceof Lijn)
					((Lijn) ob).rotate(angle, crx, cry);
				else if (ob instanceof Rechthoek)
					((Rechthoek) ob).rotate(angle, crx, cry);
				else if (ob instanceof Ellips)
					((Ellips) ob).rotate(angle, crx, cry);
				else if (ob instanceof TekstElement)
					((TekstElement) ob).rotate(angle, crx, cry);
				
			}
			
			updateSelecteerRechthoek();

			
		}
		else if (rotatingWest)
		{
			// hier is alleen dy van belang
			double angle = - Math.atan(((double) dy) / (selecteerRechthoek.width/2));
			angleSum += angle; 
			int rotateSteps = (int) Math.round(angleSum / rotateStep);
			angleSum -= rotateSteps * rotateStep;
			//selectedStreep.rotate(rotateSteps * rotateStep);
			
			for (int cnt = 0; cnt < objectsSelected.size(); cnt++)
			{
				Object ob = objectsSelected.elementAt(cnt);
				if (ob instanceof Streep)
					((Streep) ob).rotate(rotateSteps * rotateStep, crx, cry);
				else if (ob instanceof Lijn)
					((Lijn) ob).rotate(rotateSteps * rotateStep, crx, cry);
				else if (ob instanceof Rechthoek)
					((Rechthoek) ob).rotate(rotateSteps * rotateStep, crx, cry);
				else if (ob instanceof Ellips)
					((Ellips) ob).rotate(rotateSteps * rotateStep, crx, cry);
				else if (ob instanceof TekstElement)
					((TekstElement) ob).rotate(rotateSteps * rotateStep, crx, cry);
				
			}
			
			updateSelecteerRechthoek();
		}

		
	}

	public void processHandleAction(int dx, int dy)
	{
		if (selectedStreep != null)
		{
			if (scalingTopRight)
			{
				double aspectDirX = selectedStreep.handleBox.x + selectedStreep.handleBox. width - 
									selectedStreep.cx;
				double aspectDirY = selectedStreep.handleBox.y - selectedStreep.cy;
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				double aa = aspectDirX * aspectDirX + aspectDirY * aspectDirY;
				double s = (aspectDirX * dxDouble + aspectDirY * dyDouble) / aa;
				double asXDouble = s * aspectDirX;
				double asYDouble = s * aspectDirY;
				double oldWidth = (double) selectedStreep.handleBox.width / 2;
				double oldHeight = (double) selectedStreep.handleBox.height / 2;
				double newWidth = oldWidth + asXDouble;
				double newHeight = oldHeight - asYDouble;
				double sc = ((double) newWidth) / oldWidth;
				selectedStreep.scale(sc);
			}
			else if (scalingTopLeft)
			{
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				
				//double dxInvRot = selectedStreep.inverseRotX(dxDouble, dyDouble);
				//double dyInvRot = selectedStreep.inverseRotY(dxDouble, dyDouble);
				//double oldWidth = (double) selectedStreep.breedte / 2;
				//double oldHeight = (double) selectedStreep.hoogte / 2;
				
				double oldWidth = (double) selectedStreep.handleBox.width / 2;
				double oldHeight = (double) selectedStreep.handleBox.height / 2;
				
				double newWidth = oldWidth - dx;
				double newHeight = oldHeight - dy;
				double sx = newWidth / oldWidth;
				double sy = newHeight / oldHeight;
				selectedStreep.scale(sx,sy);
			}
			else if (scalingBottomLeft)
			{
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;

				//double dxInvRot = selectedStreep.inverseRotX(dxDouble, dyDouble);
				//double dyInvRot = selectedStreep.inverseRotY(dxDouble, dyDouble);
				//double oldWidth = (double) selectedStreep.breedte / 2;
				//double oldHeight = (double) selectedStreep.hoogte / 2;
				
				double oldWidth = (double) selectedStreep.handleBox.width / 2;
				double oldHeight = (double) selectedStreep.handleBox.height / 2;
				
				double newWidth = oldWidth - dx;
				double newHeight = oldHeight + dy;
				double sx = newWidth / oldWidth;
				double sy = newHeight / oldHeight;
				selectedStreep.scale(sx,sy);
			}
			else if (scalingBottomRight)
			{
				double aspectDirX = selectedStreep.handleBox.x + selectedStreep.handleBox.width - 
								    selectedStreep.cx;
				double aspectDirY = selectedStreep.handleBox.y + selectedStreep.handleBox.height - selectedStreep.cy;
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				double aa = aspectDirX * aspectDirX + aspectDirY * aspectDirY;
				double s = (aspectDirX * dxDouble + aspectDirY * dyDouble) / aa;
				double asXDouble = s * aspectDirX;
				double asYDouble = s * aspectDirY;
				double oldWidth = (double) selectedStreep.handleBox.width / 2;
				double oldHeight = (double) selectedStreep.handleBox.height / 2;
				double newWidth = oldWidth + asXDouble;
				double newHeight = oldHeight + asYDouble;
				double sc = ((double) newWidth) / oldWidth;
				selectedStreep.scale(sc);
			}
			else if (rotatingEast)
			{
				// hier is alleen dy van belang
				double angle = Math.atan(((double) dy) / (selectedStreep.handleBox.width/2));
				selectedStreep.rotate(angle);
				
			}
			else if (rotatingWest)
			{
				// hier is alleen dy van belang
				double angle = - Math.atan(((double) dy) / (selectedStreep.handleBox.width/2));
				angleSum += angle; 
				int rotateSteps = (int) Math.round(angleSum / rotateStep);
				angleSum -= rotateSteps * rotateStep;
				selectedStreep.rotate(rotateSteps * rotateStep);
				
				
			}
			
			
			
		}
		else if (selectedLijn != null)
		{
			if (scalingTopRight)
			{
				double aspectDirX = selectedLijn.handleBox.x + selectedLijn.handleBox. width - 
									selectedLijn.cx;
				double aspectDirY = selectedLijn.handleBox.y - selectedLijn.cy;
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				double aa = aspectDirX * aspectDirX + aspectDirY * aspectDirY;
				double s = (aspectDirX * dxDouble + aspectDirY * dyDouble) / aa;
				double asXDouble = s * aspectDirX;
				double asYDouble = s * aspectDirY;
				double oldWidth = (double) selectedLijn.handleBox.width / 2;
				double oldHeight = (double) selectedLijn.handleBox.height / 2;
				double newWidth = oldWidth + asXDouble;
				double newHeight = oldHeight - asYDouble;
				double sc = ((double) newWidth) / oldWidth;
				selectedLijn.scale(sc);
			}
			else if (scalingTopLeft)
			{
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				
				//double dxInvRot = selectedLijn.inverseRotX(dxDouble, dyDouble);
				//double dyInvRot = selectedLijn.inverseRotY(dxDouble, dyDouble);
				//double breedte = Math.abs(selectedLijn.toX - selectedLijn.fromX);
				//double hoogte = Math.abs(selectedLijn.toY - selectedLijn.fromY);
				//double oldWidth = breedte / 2;
				//double oldHeight = hoogte / 2;
				
				double oldWidth = (double) selectedLijn.handleBox.width / 2;
				double oldHeight = (double) selectedLijn.handleBox.height / 2;
				
				double newWidth = oldWidth - dx;
				double newHeight = oldHeight - dy;
				double sx = newWidth / oldWidth;
				double sy = newHeight / oldHeight;
				
				selectedLijn.scale(sx,sy);
			}
			else if (scalingBottomLeft)
			{
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				
				//double dxInvRot = selectedLijn.inverseRotX(dxDouble, dyDouble);
				//double dyInvRot = selectedLijn.inverseRotY(dxDouble, dyDouble);
				//double breedte = Math.abs(selectedLijn.toX - selectedLijn.fromX);
				//double hoogte = Math.abs(selectedLijn.toY - selectedLijn.fromY);
				//double oldWidth = breedte / 2;
				//double oldHeight = hoogte / 2;
				
				double oldWidth = (double) selectedLijn.handleBox.width / 2;
				double oldHeight = (double) selectedLijn.handleBox.height / 2;
				
				double newWidth = oldWidth - dx;
				double newHeight = oldHeight + dy;
				double sx = newWidth / oldWidth;
				double sy = newHeight / oldHeight;
				selectedLijn.scale(sx,sy);
			}
			else if (scalingBottomRight)
			{
				double aspectDirX = selectedLijn.handleBox.x + selectedLijn.handleBox.width - 
								    selectedLijn.cx;
				double aspectDirY = selectedLijn.handleBox.y + selectedLijn.handleBox.height - 
									selectedLijn.cy;
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				double aa = aspectDirX * aspectDirX + aspectDirY * aspectDirY;
				double s = (aspectDirX * dxDouble + aspectDirY * dyDouble) / aa;
				double asXDouble = s * aspectDirX;
				double asYDouble = s * aspectDirY;
				double oldWidth = (double) selectedLijn.handleBox.width / 2;
				double oldHeight = (double) selectedLijn.handleBox.height / 2;
				double newWidth = oldWidth + asXDouble;
				double newHeight = oldHeight + asYDouble;
				double sc = ((double) newWidth) / oldWidth;
				selectedLijn.scale(sc);
			}
			else if (rotatingEast)
			{
				// hier is alleen dy van belang
				double angle = Math.atan(((double) dy) / (selectedLijn.handleBox.width/2));
				selectedLijn.rotate(angle);
				
			}
			else if (rotatingWest)
			{
				// hier is alleen dy van belang
				double angle = - Math.atan(((double) dy) / (selectedLijn.handleBox.width/2));
				angleSum += angle; 
				int rotateSteps = (int) Math.round(angleSum / rotateStep);
				angleSum -= rotateSteps * rotateStep;
				selectedLijn.rotate(rotateSteps * rotateStep);
				
				
			}

			
		}
		else if (selectedRechthoek != null)
		{
			if (scalingTopRight)
			{
				double aspectDirX = selectedRechthoek.handleBox.x + selectedRechthoek.handleBox. width - 
									selectedRechthoek.cx;
				double aspectDirY = selectedRechthoek.handleBox.y - selectedRechthoek.cy;
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				double aa = aspectDirX * aspectDirX + aspectDirY * aspectDirY;
				double s = (aspectDirX * dxDouble + aspectDirY * dyDouble) / aa;
				double asXDouble = s * aspectDirX;
				double asYDouble = s * aspectDirY;
				double oldWidth = (double) selectedRechthoek.handleBox.width / 2;
				double oldHeight = (double) selectedRechthoek.handleBox.height / 2;
				double newWidth = oldWidth + asXDouble;
				double newHeight = oldHeight - asYDouble;
				double sc = ((double) newWidth) / oldWidth;
				selectedRechthoek.scale(sc);
			}
			else if (scalingTopLeft)
			{
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				
				//double dxInvRot = selectedRechthoek.inverseRotX(dxDouble, dyDouble);
				//double dyInvRot = selectedRechthoek.inverseRotY(dxDouble, dyDouble);
				//double oldWidth = (double) selectedRechthoek.breedte / 2;
				//double oldHeight = (double) selectedRechthoek.hoogte / 2;
				
				double oldWidth = (double) selectedRechthoek.handleBox.width / 2;
				double oldHeight = (double) selectedRechthoek.handleBox.height / 2;

				double newWidth = oldWidth - dx;
				double newHeight = oldHeight - dy;
				double sx = newWidth / oldWidth;
				double sy = newHeight / oldHeight;
				
				selectedRechthoek.scale(sx,sy);
			}
			else if (scalingBottomLeft)
			{
				
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				
				//double dxInvRot = selectedRechthoek.inverseRotX(dxDouble, dyDouble);
				//double dyInvRot = selectedRechthoek.inverseRotY(dxDouble, dyDouble);
				//double oldWidth = (double) selectedRechthoek.breedte / 2;
				//double oldHeight = (double) selectedRechthoek.hoogte / 2;
				
				double oldWidth = (double) selectedRechthoek.handleBox.width / 2;
				double oldHeight = (double) selectedRechthoek.handleBox.height / 2;

				double newWidth = oldWidth - dx;
				double newHeight = oldHeight + dy;
				double sx = newWidth / oldWidth;
				double sy = newHeight / oldHeight;
				
				selectedRechthoek.scale(sx,sy);
			}
			else if (scalingBottomRight)
			{
				double aspectDirX = selectedRechthoek.handleBox.x + selectedRechthoek.handleBox.width - 
								    selectedRechthoek.cx;
				double aspectDirY = selectedRechthoek.handleBox.y + selectedRechthoek.handleBox.height - 
									selectedRechthoek.cy;
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				double aa = aspectDirX * aspectDirX + aspectDirY * aspectDirY;
				double s = (aspectDirX * dxDouble + aspectDirY * dyDouble) / aa;
				double asXDouble = s * aspectDirX;
				double asYDouble = s * aspectDirY;
				double oldWidth = (double) selectedRechthoek.handleBox.width / 2;
				double oldHeight = (double) selectedRechthoek.handleBox.height / 2;
				double newWidth = oldWidth + asXDouble;
				double newHeight = oldHeight + asYDouble;
				double sc = ((double) newWidth) / oldWidth;
				selectedRechthoek.scale(sc);
			}
			else if (rotatingEast)
			{
				// hier is alleen dy van belang
				double angle = Math.atan(((double) dy) / (selectedRechthoek.handleBox.width/2));
				selectedRechthoek.rotate(angle);
				
			}
			else if (rotatingWest)
			{
				// hier is alleen dy van belang
				double angle = - Math.atan(((double) dy) / (selectedRechthoek.handleBox.width/2));
				angleSum += angle; 
				int rotateSteps = (int) Math.round(angleSum / rotateStep);
				angleSum -= rotateSteps * rotateStep;
				selectedRechthoek.rotate(rotateSteps * rotateStep);
				
				
			}

			
		}
		else if (selectedEllips != null)
		{
			if (scalingTopRight)
			{
				double aspectDirX = selectedEllips.handleBox.x + selectedEllips.handleBox. width - 
									selectedEllips.cx;
				double aspectDirY = selectedEllips.handleBox.y - selectedEllips.cy;
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				double aa = aspectDirX * aspectDirX + aspectDirY * aspectDirY;
				double s = (aspectDirX * dxDouble + aspectDirY * dyDouble) / aa;
				double asXDouble = s * aspectDirX;
				double asYDouble = s * aspectDirY;
				double oldWidth = (double) selectedEllips.handleBox.width / 2;
				double oldHeight = (double) selectedEllips.handleBox.height / 2;
				double newWidth = oldWidth + asXDouble;
				double newHeight = oldHeight - asYDouble;
				double sc = ((double) newWidth) / oldWidth;
				selectedEllips.scale(sc);
			}
			else if (scalingTopLeft)
			{
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				
				//double dxInvRot = selectedEllips.inverseRotX(dxDouble, dyDouble);
				//double dyInvRot = selectedEllips.inverseRotY(dxDouble, dyDouble);
				//double oldWidth = (double) selectedEllips.breedte / 2;
				//double oldHeight = (double) selectedEllips.hoogte / 2;
				
				double oldWidth = (double) selectedEllips.handleBox.width / 2;
				double oldHeight = (double) selectedEllips.handleBox.height / 2;

				double newWidth = oldWidth - dx;
				double newHeight = oldHeight - dy;
				double sx = newWidth / oldWidth;
				double sy = newHeight / oldHeight;
				selectedEllips.scale(sx,sy);
			}
			else if (scalingBottomLeft)
			{
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				
				//double dxInvRot = selectedEllips.inverseRotX(dxDouble, dyDouble);
				//double dyInvRot = selectedEllips.inverseRotY(dxDouble, dyDouble);
				//double oldWidth = (double) selectedEllips.breedte / 2;
				//double oldHeight = (double) selectedEllips.hoogte / 2;
				
				double oldWidth = (double) selectedEllips.handleBox.width / 2;
				double oldHeight = (double) selectedEllips.handleBox.height / 2;

				double newWidth = oldWidth - dx;
				double newHeight = oldHeight + dy;
				double sx = newWidth / oldWidth;
				double sy = newHeight / oldHeight;
				
				selectedEllips.scale(sx,sy);
			}
			else if (scalingBottomRight)
			{
				double aspectDirX = selectedEllips.handleBox.x + selectedEllips.handleBox.width - 
								    selectedEllips.cx;
				double aspectDirY = selectedEllips.handleBox.y + selectedEllips.handleBox.height - 
									selectedEllips.cy;
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				double aa = aspectDirX * aspectDirX + aspectDirY * aspectDirY;
				double s = (aspectDirX * dxDouble + aspectDirY * dyDouble) / aa;
				double asXDouble = s * aspectDirX;
				double asYDouble = s * aspectDirY;
				double oldWidth = (double) selectedEllips.handleBox.width / 2;
				double oldHeight = (double) selectedEllips.handleBox.height / 2;
				double newWidth = oldWidth + asXDouble;
				double newHeight = oldHeight + asYDouble;
				double sc = ((double) newWidth) / oldWidth;
				selectedEllips.scale(sc);
			}
			else if (rotatingEast)
			{
				// hier is alleen dy van belang
				double angle = Math.atan(((double) dy) / (selectedEllips.handleBox.width/2));
				selectedEllips.rotate(angle);
				
			}
			else if (rotatingWest)
			{
				// hier is alleen dy van belang
				double angle = - Math.atan(((double) dy) / (selectedEllips.handleBox.width/2));
				angleSum += angle; 
				int rotateSteps = (int) Math.round(angleSum / rotateStep);
				angleSum -= rotateSteps * rotateStep;
				selectedEllips.rotate(rotateSteps * rotateStep);
				
				
			}
			
			
		}
		else if (selectedTekstElement != null)
		{

			if (scalingTopRight)
			{
				double aspectDirX = selectedTekstElement.handleBox.x + selectedTekstElement.handleBox. width - 
									selectedTekstElement.cx;
				double aspectDirY = selectedTekstElement.handleBox.y - selectedTekstElement.cy;
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				double aa = aspectDirX * aspectDirX + aspectDirY * aspectDirY;
				double s = (aspectDirX * dxDouble + aspectDirY * dyDouble) / aa;
				double asXDouble = s * aspectDirX;
				double asYDouble = s * aspectDirY;
				double oldWidth = (double) selectedTekstElement.handleBox.width / 2;
				double oldHeight = (double) selectedTekstElement.handleBox.height / 2;
				double newWidth = oldWidth + asXDouble;
				double newHeight = oldHeight - asYDouble;
				double sc = ((double) newWidth) / oldWidth;
				selectedTekstElement.scale(sc);
			}
			else if (scalingTopLeft)
			{
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				
				double oldWidth = (double) selectedTekstElement.handleBox.width / 2;
				double oldHeight = (double) selectedTekstElement.handleBox.height / 2;

				double newWidth = oldWidth - dx;
				double newHeight = oldHeight - dy;
				double sx = newWidth / oldWidth;
				double sy = newHeight / oldHeight;
				selectedTekstElement.scale(sx,sy);
			}
			else if (scalingBottomLeft)
			{
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;

				double oldWidth = (double) selectedTekstElement.handleBox.width / 2;
				double oldHeight = (double) selectedTekstElement.handleBox.height / 2;

				double newWidth = oldWidth - dx;
				double newHeight = oldHeight + dy;
				double sx = newWidth / oldWidth;
				double sy = newHeight / oldHeight;
				selectedTekstElement.scale(sx,sy);
			}
			
			else if (scalingBottomRight)
			{
				double aspectDirX = selectedTekstElement.handleBox.x + selectedTekstElement.handleBox.width - 
								    selectedTekstElement.cx;
				double aspectDirY = selectedTekstElement.handleBox.y + selectedTekstElement.handleBox.height - 
									selectedTekstElement.cy;
				
				double dxDouble = (double) dx;
				double dyDouble = (double) dy;
				double aa = aspectDirX * aspectDirX + aspectDirY * aspectDirY;
				double s = (aspectDirX * dxDouble + aspectDirY * dyDouble) / aa;
				double asXDouble = s * aspectDirX;
				double asYDouble = s * aspectDirY;
				double oldWidth = (double) selectedTekstElement.handleBox.width / 2;
				double oldHeight = (double) selectedTekstElement.handleBox.height / 2;
				double newWidth = oldWidth + asXDouble;
				double newHeight = oldHeight + asYDouble;
				double sc = ((double) newWidth) / oldWidth;
				
				selectedTekstElement.scale(sc);
				
			}
			else if (rotatingEast)
			{
				// hier is alleen dy van belang
				double angle = Math.atan(((double) dy) / (selectedTekstElement.handleBox.width/2));
				selectedTekstElement.rotate(angle);
				
			}
			else if (rotatingWest)
			{
				// hier is alleen dy van belang
				double angle = - Math.atan(((double) dy) / (selectedTekstElement.handleBox.width/2));
				angleSum += angle; 
				int rotateSteps = (int) Math.round(angleSum / rotateStep);
				angleSum -= rotateSteps * rotateStep;
				selectedTekstElement.rotate(rotateSteps * rotateStep);
				
				
			}
			
		}

			
	}

	public boolean selecteerRechthoekHandlesContain(int x, int y)
	{
				
		if ((topRightRect != null) && topRightRect.contains(x,y))
		{	groupHandleAction = true;
			scalingTopRight = true;
		}
		else if ((topLeftRect != null) && topLeftRect.contains(x,y))
		{	groupHandleAction = true;
			scalingTopLeft = true;
		}
		else if ((bottomRightRect != null) && bottomRightRect.contains(x,y))
		{	groupHandleAction = true;
			scalingBottomRight = true;
		}
		else if ((bottomLeftRect != null) && bottomLeftRect.contains(x,y))
		{	groupHandleAction = true;
			scalingBottomLeft = true;
		}
		else if ((rotateEastHandle != null) && rotateEastHandle.contains(x,y))
		{	groupHandleAction = true;
			rotatingEast = true;
		}
		else if ((rotateWestHandle != null) && rotateWestHandle.contains(x,y))
		{	groupHandleAction = true;
			rotatingWest = true;
		}
		
		return groupHandleAction;
	}

	
	public boolean objectSelectedHandlesContain(int x, int y)
	{
		if (selectedStreep != null)
		{
			if ((selectedStreep.topRightRect != null) && selectedStreep.topRightRect.contains(x,y))
			{	handleAction = true;
				scalingTopRight = true;
			}
			else if ((selectedStreep.topLeftRect != null) && selectedStreep.topLeftRect.contains(x,y))
			{	handleAction = true;
				scalingTopLeft = true;
			}
			else if ((selectedStreep.bottomRightRect != null) && selectedStreep.bottomRightRect.contains(x,y))
			{	handleAction = true;
				scalingBottomRight = true;
			}
			else if ((selectedStreep.bottomLeftRect != null) && selectedStreep.bottomLeftRect.contains(x,y))
			{	handleAction = true;
				scalingBottomLeft = true;
			}
			else if ((selectedStreep.rotateEastHandle != null) && selectedStreep.rotateEastHandle.contains(x,y))
			{	handleAction = true;
				rotatingEast = true;
			}
			else if ((selectedStreep.rotateWestHandle != null) && selectedStreep.rotateWestHandle.contains(x,y))
			{	handleAction = true;
				rotatingWest = true;
			}
						

		}
		else if (selectedLijn != null)
		{
			if ((selectedLijn.topRightRect != null) && selectedLijn.topRightRect.contains(x,y))
			{	handleAction = true;
				scalingTopRight = true;
			}
			else if ((selectedLijn.topLeftRect != null) && selectedLijn.topLeftRect.contains(x,y))
			{	handleAction = true;
				scalingTopLeft = true;
			}
			else if ((selectedLijn.bottomRightRect != null) && selectedLijn.bottomRightRect.contains(x,y))
			{	handleAction = true;
				scalingBottomRight = true;
			}
			else if ((selectedLijn.bottomLeftRect != null) && selectedLijn.bottomLeftRect.contains(x,y))
			{	handleAction = true;
				scalingBottomLeft = true;
			}
			else if ((selectedLijn.rotateEastHandle != null) && selectedLijn.rotateEastHandle.contains(x,y))
			{	handleAction = true;
				rotatingEast = true;
			}
			else if ((selectedLijn.rotateWestHandle != null) && selectedLijn.rotateWestHandle.contains(x,y))
			{	handleAction = true;
				rotatingWest = true;
			}
			

		}
		else if (selectedRechthoek != null)
		{
			if ((selectedRechthoek.topRightRect != null) && selectedRechthoek.topRightRect.contains(x,y))
			{	handleAction = true;
				scalingTopRight = true;
			}
			else if ((selectedRechthoek.topLeftRect != null) && selectedRechthoek.topLeftRect.contains(x,y))
			{	handleAction = true;
				scalingTopLeft = true;
			}
			else if ((selectedRechthoek.bottomRightRect != null) && selectedRechthoek.bottomRightRect.contains(x,y))
			{	handleAction = true;
				scalingBottomRight = true;
			}
			else if ((selectedRechthoek.bottomLeftRect != null) && selectedRechthoek.bottomLeftRect.contains(x,y))
			{	handleAction = true;
				scalingBottomLeft = true;
			}
			else if ((selectedRechthoek.rotateEastHandle != null) && selectedRechthoek.rotateEastHandle.contains(x,y))
			{	handleAction = true;
				rotatingEast = true;
			}
			else if ((selectedRechthoek.rotateWestHandle != null) && selectedRechthoek.rotateWestHandle.contains(x,y))
			{	handleAction = true;
				rotatingWest = true;
			}

		}
		else if (selectedEllips != null)
		{
			if ((selectedEllips.topRightRect != null) && selectedEllips.topRightRect.contains(x,y))
			{	handleAction = true;
				scalingTopRight = true;
			}
			else if ((selectedEllips.topLeftRect != null) && selectedEllips.topLeftRect.contains(x,y))
			{	handleAction = true;
				scalingTopLeft = true;
			}
			else if ((selectedEllips.bottomRightRect != null) && selectedEllips.bottomRightRect.contains(x,y))
			{	handleAction = true;
				scalingBottomRight = true;
			}
			else if ((selectedEllips.bottomLeftRect != null) && selectedEllips.bottomLeftRect.contains(x,y))
			{	handleAction = true;
				scalingBottomLeft = true;
			}
			else if ((selectedEllips.rotateEastHandle != null) && selectedEllips.rotateEastHandle.contains(x,y))
			{	handleAction = true;
				rotatingEast = true;
			}
			else if ((selectedEllips.rotateWestHandle != null) && selectedEllips.rotateWestHandle.contains(x,y))
			{	handleAction = true;
				rotatingWest = true;
			}


		}
		else if (selectedTekstElement != null)
		{
			if ((selectedTekstElement.topRightRect != null) && selectedTekstElement.topRightRect.contains(x,y))
			{	handleAction = true;
				scalingTopRight = true;
			}
			else if ((selectedTekstElement.topLeftRect != null) && selectedTekstElement.topLeftRect.contains(x,y))
			{	handleAction = true;
				scalingTopLeft = true;
			}
			else if ((selectedTekstElement.bottomRightRect != null) && selectedTekstElement.bottomRightRect.contains(x,y))
			{	handleAction = true;
				scalingBottomRight = true;
			}
			else if ((selectedTekstElement.bottomLeftRect != null) && selectedTekstElement.bottomLeftRect.contains(x,y))
			{	handleAction = true;
				scalingBottomLeft = true;
			}
			else if ((selectedTekstElement.rotateEastHandle != null) && selectedTekstElement.rotateEastHandle.contains(x,y))
			{	handleAction = true;
				rotatingEast = true;
			}
			else if ((selectedTekstElement.rotateWestHandle != null) && selectedTekstElement.rotateWestHandle.contains(x,y))
			{	handleAction = true;
				rotatingWest = true;
			}
			

		}
		
		return handleAction;
	}

	
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		if (mouseMode == tekenen)
		{
			mouseDown = true;
			draggDoublePoints.add(new DoublePoint(eventX, eventY));
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
				tekstPopup.setPopupPosition(tekstEdited.bb.x, tekstEdited.bb.y);
			}
			else 
			{	tekstPopup.setTextColor(drawingColor.toString());
				tekstPopup.setPopupPosition(startX - 10, startY - 10);
			}
				
			//else	
			//	tekstVeld.setText("");
			//tekstPopup.setModal(true);
			
			//tekstPopup.setPopupPosition(startX, startY);
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

			if ((selecteerRechthoek != null) && selecteerRechthoek.contains(eventX, eventY))
			{
				resetSelectedObject();
				sleepSelectie = true;
				startX = eventX;//e.getX();
				startY = eventY;//e.getY();
			}

			else if (objectSelectedHandlesContain(eventX, eventY))
			{
				startX = eventX;
				startY = eventY;
//System.out.println("mousedown oshc");			
				
				objectHandled = false;
			}
			else if (selecteerRechthoekHandlesContain(eventX, eventY))
			{
				startX = eventX;
				startY = eventY;
//System.out.println("mp oshc");			
				
				objectHandled = false;
			}

			// individueel object aangeklikt, was mogelijk al geselecteerd
			//if (setSelectedObject(e.getX(), e.getY()) || objectSelectedContains(e.getX(), e.getY()))
			else if (setSelectedObject(eventX, eventY) || objectSelectedContains(eventX, eventY))
			{
				sleepSelectie = true;
				startX = eventX;//e.getX();
				startY = eventY;//e.getY();
				selecteerRechthoek = null;
				killScaleHandles();
				killRotateHandles();

				resetSelectedObjects();

				objectMoved = false;
			}
			else
			{
				sleepSelectie = false;
				resetSelectedObject();
				resetSelectedObjects();
				figuurStart = new Point(eventX, eventY);
				selecteerRechthoek = null;
				killScaleHandles();
				killRotateHandles();

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
			//draggPoints.addElement(new Point(eventX, eventY));
			draggDoublePoints.add(new DoublePoint(eventX, eventY));
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
	
			if (handleAction)
			{
				int dx = eventX - startX;
				int dy = eventY - startY;

				processHandleAction(dx,dy);
				
				startX = eventX;
				startY = eventY;
				
				objectHandled = true;
				
				paint();
				
			}
			
			else if (groupHandleAction)
			{
				int dx = eventX - startX;
				int dy = eventY - startY;

				processSelecteerRechthoekHandleAction(dx,dy);
				
				startX = eventX;
				startY = eventY;
				
				objectHandled = true;
				
				paint();
				
			}


			else if (sleepSelectie) // verplaats de selecteerRechthoek met inhoud!!
			{
				
				
				int dx = eventX - startX;
				int dy = eventY - startY;
				//int dx = eventX - start.x;
				//int dy = eventY - start.y;
				if (selecteerRechthoek != null)
				{	selecteerRechthoek.translate(dx, dy);
					if (schalen)
					{
						topRightHandle.translate(dx, dy); 
						bottomRightHandle.translate(dx, dy); 
						topLeftHandle.translate(dx, dy);
						bottomLeftHandle.translate(dx, dy);
						topRightRect.translate(dx, dy);
						bottomRightRect.translate(dx, dy);
						topLeftRect.translate(dx, dy);
						bottomLeftRect.translate(dx, dy);
					}
					if (roteren)
					{
						rotateEastHandle.translate(dx, dy);
						rotateWestHandle.translate(dx, dy);
					}
				}
				
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
			ArrayList<DoublePoint> smoothedDraggDoublePoints = smooth(draggDoublePoints, smoothType);
			
			//Streep streep = new Streep(drawingColor, draggPoints);
			Streep streep = new Streep(drawingColor, smoothedDraggDoublePoints);
			streepVector.addElement(streep);
			if (draggDoublePoints.size() > 1)
				addToHistory();
			//draggPoints.removeAllElements();
			draggDoublePoints.clear();
			
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
			
			if (objectsSelected.size() == 0)
			{
				selecteerRechthoek = null;
				killScaleHandles();
				killRotateHandles();
				
				paint();
			}
			else if (objectsSelected.size() == 1)
			{	
				
//System.out.println("oss = 1");					
				Object objectSelected = objectsSelected.elementAt(0);
				if (objectSelected instanceof Streep)
					selectedStreep = (Streep) objectSelected;
				if (objectSelected instanceof Lijn)
					selectedLijn = (Lijn) objectSelected;
				if (objectSelected instanceof Rechthoek)
					selectedRechthoek = (Rechthoek) objectSelected;
				if (objectSelected instanceof Ellips)
					selectedEllips = (Ellips) objectSelected;
				if (objectSelected instanceof TekstElement)
					selectedTekstElement = (TekstElement) objectSelected;
				selecteerRechthoek = null;
				killScaleHandles();
				killRotateHandles();
				resetSelectedObjects();
				
				paint();
				
				
			}
			else // objectsSelected.size() >= 2
			{
				if (schalen)
					makeScaleHandles();
				if (roteren)
					makeRotateHandles();
				
				paint();
			}
			
			if (objectHandled)
				addToHistory();
			objectHandled = false;
			handleAction = false;
			groupHandleAction = false;
			scalingTopRight = false;
			scalingTopLeft = false;
			scalingBottomRight = false;
			scalingBottomLeft = false;
			rotatingEast = false;
			rotatingWest = false;
			angleSum = 0; 

			paint();
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

/*
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
*/
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