package fi.kladjegwt.client;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.HashMap;
//import java.awt.Point;
import java.util.ArrayList;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

//import org.vectomatic.dom.svg.OMSVGDocument;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

import fi.writemathgwt.client.engine.DoubleRectangle;
import fi.writemathgwt.client.engine.Point;
import fi.writemathgwt.client.engine.Stroke;
import fi.writemathgwt.client.engine.StrokeContainer;

import com.google.gwt.event.dom.client.TouchEndHandler;

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;

/**
 * klasse die het werkveld beheert: de klasse bevat een Canvas en handelt ook de
 * Mouse/Touch Events op dit Canvas af; de uitgevoerde actie bij een Mouse/Touch
 * Event hangt af van de mouse mode, die gekozen wordt m.b.v. de ToggleButtons
 * onder het werkveld (zie ook klasse KLadjeGWT):<br>
 * mouse mode = tekenen: zie klasse Streep <br>
 * mouse mode = lijnTekenen: zie klasse Lijn <br>
 * mouse mode = rechthoekTekenen: zie klasse Rechthoek <br>
 * mouse mode = cirkelTekenen: zie klasse Ellips <br>
 * mouse mode = tekstTekenen: zie klasse TekstElement <br>
 * mouse mode = selecteren: twee mogelijkheden: <br>
 * klik op een object, het object krijgt dan een handle box en kan 
 * geschaald (als dat mag), gedraaid (als dat mag), verplaatst of
 * gewist worden; ergens anders klikken verwijderd de handle box;<br>
 * klik ergens op het werkveld en sleep een rechthoek tevoorschijn 
 * (de selecteerRechthoek); bij mouseUp/touchEnd verschijnt een handle box
 * waarbij alle objecten die zich in de handle box bevinden geselecteerd
 * zijn; alle geselecteerde objecten kunnen dan geschaald (als dat mag), 
 * gedraaid (als dat mag, TekstElementen worden niet gedraaid), 
 * verplaatst of gewist worden; ergens anders klikken verwijderd
 * de selecteerRechthoek;<br>
 * de achterground van het Canvas wordt niet getekened, zodat KladjeGWT
 * b.v. over een foto heengelegd kan worden.<br>
 * er is een history die maximaal r voorafgaande tekensituaties onthoudt
 * en de bediend wordt via de "terug"-knop; let op dat de history niet
 * in de state van de opdracht wordt onthouden, d.w.z. ga je naar een andere
 * opdracht en weer terug, dan is je history een "diep", d.w.z. alles wat de
 * leerling getekend heeft; "terug" wist dit allemaal.     
 * @author huub
 */

public class KladjeGWTVeld 
{
	
	private static Logger logger = Logger.getLogger("KladjeGWTVeld");
	
	/**
	 * het Canvas om op te tekenen
	 */
	public Canvas kladjeHWTCanvas, backgroundCanvas;//, strokeContainerCanvas
	/**
	 * de Context2d om mee te tekenen
	 */
	public Context2d gIm, backgroundgIm, strokeContainergIm;
	
	/**
	 * een hele kleine positieve double
	 */
	static double NZERO = 1e-5d;
	
	/**
	 * instelling: true: er mag gedraaid worden (er verschijnen draaihandles)   
	 */
	static boolean roteren = true;
	/**
	 * instelling: true: er mag geschaald worden (er verschijnen schaalhandles)   
	 */
	static boolean schalen = true;

	/**
	 * true: een van de handles van de handle box van een individueel object werd aangeklikt
	 */
	boolean handleAction = false;
	/**
	 * true: een van de handles (als die er al zijn) van de selecteerRechthoek
	 * werd aangeklikt
	 */
	boolean groupHandleAction = false;
	/**
	 * true: de schaalhandle rechtsboven van de selecteerRechthoek werd aangeklikt
	 */
	boolean scalingTopRight = false;
	/**
	 * true: de schaalhandle linksboven van de selecteerRechthoek werd aangeklikt
	 */
	boolean scalingTopLeft = false;
	/**
	 * true: de schaalhandle rechtsbeneden van de selecteerRechthoek werd aangeklikt
	 */
	boolean scalingBottomRight = false;
	/**
	 * true: de schaalhandle linksbeneden van de selecteerRechthoek werd aangeklikt
	 */
	boolean scalingBottomLeft = false;
	/**
	 * true: de draaihandle rechte van de selecteerRechthoek werd aangeklikt
	 */
	boolean rotatingEast = false;
	/**
	 * true: de draaihandle links van de selecteerRechthoek werd aangeklikt
	 */
	boolean rotatingWest = false;
	/**
	 * instelling: true: het werkveld bevat horizontale lijnen
	 */
	boolean lijnen = false;
	/**
	 * instelling: true: het werkveld bevat ruitjes 
	 */
	boolean ruitjes = true;
	
	/**
	 * instelling: afstand in pixels tussen de horizontale lijnen 
	 */
	int lineDistance = 10;
	/**
	 * instelling: afmeting in pixels van de ruitses
	 */
	int gridSize = 10;
	
	/**
	 * lichtblauw
	 */
	static CssColor lightBlue = CssColor.make(148, 148, 255);
	/**
	 * de kleur van de lijnen (lichtblauw)
	 */
	CssColor lijnenKleur = CssColor.make(150, 150, 255);
	/**
	 * de kleur van de ruitjes (grijs)
	 */
	CssColor ruitjesKleur = CssColor.make(38, 115, 182);
	
	/**
	 * de kleur van de bounding boxes van objecten
	 */
	static CssColor bbColor = lightBlue; 
	/**
	 * de kleur van de handle boxes van objecten (blauw) 
	 */
	static CssColor hbColor = CssColor.make(0, 0, 255);
	
	/**
	 * de minimum breedte en hoogte van een handle box
	 */
	static int minHandleBoxSize = 50;

	/**
	 * de kleur van de selectierechthoek
	 */
	CssColor selectieColor = CssColor.make(0, 0, 255);
		
	/**
	 * voorgedefinieerde kleuren
	 */
	static CssColor zwart = CssColor.make(0, 0, 0);
	static CssColor grijs = CssColor.make(220, 220, 220);
	static CssColor rood = CssColor.make(255, 0, 0);
	static CssColor oranje = CssColor.make(255, 127, 0);
	static CssColor groen = CssColor.make(0, 255, 0);
	static CssColor cyaan = CssColor.make(0, 255, 255);
	static CssColor blauw = CssColor.make(0, 0, 255);
	static CssColor magenta = CssColor.make(255, 0, 255);
	static CssColor geel = CssColor.make(255, 255, 0);
	
	/**
	 * de actuele tekenkleur (default zwart)
	 */
	CssColor drawingColor = CssColor.make(0, 0, 0);
	
	/**
	 * mouse/touch modes
	 */
	final int inert = 0;
	final int tekenen = 1;
	final int formuleOptie = 2;
	final int lijnTekenen = 3;
	final int rechthoekTekenen = 4;
	final int cirkelTekenen = 5;
	final int tekstTekenen = 6;
	final int selecteren = 7;
	final int ivmOptie = 8;
	/**
	 * de actuele mouse mode
	 */
	int mouseMode = inert;
	
	/**
	 * true na MouseDown/TouchStart, moet true zijn voor MouseMove
	 * (dit is dan dragging) 
	 */
	boolean mouseDown;
	
	/**
	 * smoothing types voor getekende strepen
	 */
	final int GAUSSIAN = 0;
	final int AVERAGE = 1;
	final int AVERAGE2 = 2;
	/**
	 * actuele smoothing type
	 */
	int smoothType = AVERAGE2;

	/**
	 * tijdelijke lijst van getekende punten bij slepen in mouse mode = tekenen; deze punten 
	 * worden een Streep by MouseUp/TouchStart
	 */
	ArrayList<DoublePoint> draggDoublePoints = new ArrayList<DoublePoint>();
	ArrayList<fi.writemathgwt.client.engine.Point> formulaStrokePoints = new ArrayList<fi.writemathgwt.client.engine.Point>();
	Stroke lastStroke;

	/**
	 * tijdelijk startpunt bij slepen in alle mouse modes, gefixeerd door mouseDowm/TouchStart 
	 */
	Point figuurStart = null;
	/**
	 * tijdelijk eindpunt bij slepen in mouse mode = lijntekenen, bij mouseUp/TouchEnd wordt een
	 * Lijn gecreeerd  
	 */
	Point lijnEinde = null;
	
	/**
	 * tijdelijke rechthoek bij slepen in mouse mode = rechthoek of mouse mode = ellips;
	 * de linker bovenhoek figuurStart wordt gefixeerd door mouseDowm/TouchStart, in het geval van
	 * mouse mode = rechthoek, teken de rechthoek, in het geval van mouse mode = ellips,
	 * teken een ellips in de rechthoek; bij mouseUp/TouchEnd wordt een Rechthoek resp.
	 * een Ellips gecreeerd  
	 */
	Rectangle tekenRechthoek = null;

	/**
	 * de rechthoek om te selecteren
	 */
	Rectangle selecteerRechthoek = null;

	/**
	 * het actuele TekstElement dat ge-edit wordt (of null) 
	 */
	TekstElement tekstEdited = null;

	/**
	 * schaal handles voor de selecteerRechthoek
	 */
	Polygon topRightHandle, bottomRightHandle, topLeftHandle, bottomLeftHandle;
	/**
	 * klikken op de schaal handles voor de selecteerRechthoek
	 */
	Rectangle topRightRect, bottomRightRect, topLeftRect, bottomLeftRect;
	/**
	 * draai handles voor de selecteerRechthoek
	 */
	Rectangle rotateEastHandle, rotateWestHandle;
	/**
	 * aantal pixels offset voor de selecteerRechthoek
	 */
	int hbFactor = 4;

	/**
	 * afmetingen Canvas
	 */
	int breedte, hoogte;
	
	/**
	 * Strepen getekend door de leerling
	 */
	Vector<Streep> streepVector = new Vector<Streep>();
	/**
	 * Lijnen getekend door de leerling
	 */
	Vector<Lijn> lijnVector = new Vector<Lijn>();
	/**
	 * Rechthoeken getekend door de leerling
	 */
	Vector<Rechthoek> rechthoekVector = new Vector<Rechthoek>();
	/**
	 * Ellipsen getekend door de leerling
	 */
	Vector<Ellips> ellipsVector = new Vector<Ellips>();
	/**
	 * TekstElementen getekend door de leerling
	 */
	Vector<TekstElement> tekstElementVector = new Vector<TekstElement>();

	/**
	 * Strepen uit de launchdata, kunnen niet veranderd worden 
	 */
	Vector<Streep> docentStreepVector = new Vector<Streep>();
	/**
	 * Strepen uit de launchdata, kunnen niet veranderd worden 
	 */
	Vector<Lijn> docentLijnVector = new Vector<Lijn>();
	/**
	 * Rechthoeken uit de launchdata, kunnen niet veranderd worden 
	 */
	Vector<Rechthoek> docentRechthoekVector = new Vector<Rechthoek>();
	/**
	 * Ellipsen uit de launchdata, kunnen niet veranderd worden 
	 */
	Vector<Ellips> docentEllipsVector = new Vector<Ellips>();
	/**
	 * TekstElementen uit de launchdata, kunnen niet veranderd worden 
	 */
	Vector<TekstElement> docentTekstElementVector = new Vector<TekstElement>();

	/**
	 * maximum aantal snapshots voor "terug"
	 */
	int maxHistories = 5;
	/**
	 *  aantal beschikbare snapshots voor "terug"
	 */
	int numHistories = 0;
	/**
	 * de snapshots voor "terug"
	 */
	HashMap<String,Object>[] histories = new HashMap[maxHistories + 1];

	/**
	 * true: een of meer objecten kunnen gesleept worden 
	 */
	boolean sleepSelectie = false;
	/**
	 * true: een of meer objecten werden verplaatst
	 * maak een snapshot voor de history 
	 */
	boolean objectMoved = false;
	/**
	 * true: een of meer objecten werden geschaald of gedraaid
	 * maak een snapshot voor de history 
	 */
	boolean objectHandled = false;
	/**
	 * coordinaten voor MouseDown/TouchStart en MouseMove/TouchMove 
	 */
	int startX, startY;

	/**
	 * de geselecteerde StrokeContainer
	 */
	KStrokeContainer selectedStrokeContainer = null;
	/**
	 * de geselcteerde Streep
	 */
	Streep selectedStreep = null;
	/**
	 * de geselecteerde Lijn
	 */
	Lijn selectedLijn = null;
	/** 
	 * de geselecteerde Rechthoek
	 */
	Rechthoek selectedRechthoek = null;
	/**
	 * de geselecteerde Ellips
	 */
	Ellips selectedEllips = null;
	/**
	 * het geselecteerde TekstElement
	 */
	TekstElement selectedTekstElement = null;
	/**
	 * een groep geselecteerde objecten
	 */
	Vector<Object> objectsSelected = new Vector<Object>();

	/**
	 * de popup voor tekstinvoer (voor TekstElementen)
	 */
	TekstPopup tekstPopup;	
	
	/**
	 * tekst uitgelezen uit de actieve TeksPopup 
	 */
	String tekstString = "";
	/**
	 * x- en y-coordinaten van MouseDown/TouchStart in mouse mode = teksttekeken,
	 * hier komt het neiuwe TekstElement
	 */
	int tekstX = 0;
	int tekstY = 0;
	
	/**
	 * draaistap voor slepen aan de draaihandles;
	 * 10 degrees in radians
	 */
	double rotateStep = Math.PI / 18; 
	/**
	 * cumulative draaiing bij slepen aan de draaihandles
	 */
	double angleSum = 0;
	/**
	 * schaal stapjes voor slepen aan de schaalhandles;
	 */
	double scaleUpStep = 105e-2d;
	double scaleDownStep = 1 / 105e-2d;
	
	fi.kladjegwt.client.Point translation = new fi.kladjegwt.client.Point(30,20);
	double scale = 2.0;
	KladjeGWT eigenaar;
	
	private ArrayList<KStrokeContainer> kStrokeContainers = new ArrayList<KStrokeContainer>();
	private KStrokeContainer currentStrokeContainer, lastCurrentStrokeContainer;// = new KStrokeContainer();
	private Image binImage;
	private ImageElement binImageElement;
	protected double schrijfLeesFactor = 2.2;
	
	
	/**
	 * constructor, creeer het Canvas en voeg Mouse en Touch Handlers toe
	 * @param w breedte
	 * @param h hoogte
	 */
	public KladjeGWTVeld(int w, int h, KladjeGWT eigenaar)
	{	
		this.eigenaar = eigenaar;
		
		kladjeHWTCanvas = Canvas.createIfSupported();
		backgroundCanvas = Canvas.createIfSupported();
		//strokeContainerCanvas = Canvas.createIfSupported();

		setSize(w, h);
		
		MouseHandler mouseHandler = new MouseHandler();
		kladjeHWTCanvas.addMouseDownHandler(mouseHandler);
		kladjeHWTCanvas.addMouseMoveHandler(mouseHandler);
		kladjeHWTCanvas.addMouseUpHandler(mouseHandler);
		
		MGWTTouchHandler touchHandler = new MGWTTouchHandler();
		kladjeHWTCanvas.addTouchStartHandler(touchHandler);
		kladjeHWTCanvas.addTouchMoveHandler(touchHandler);
		kladjeHWTCanvas.addTouchEndHandler(touchHandler);
		
		ImageResource binResource = eigenaar.kladjeGWTClientBundle.binResource();
		binImage = new Image(binResource);
		binImageElement = ImageElement.as(binImage.getElement());
		
		//kStrokeContainers.add(currentStrokeContainer);
	}

	/**
	 * zet nieuwe afmetingen
	 * @param w nieuwe breedte
	 * @param h nieuwe hoogte
	 */
	void setSize(int w, int h) 
	{
		breedte = w;
		hoogte = h;
		kladjeHWTCanvas.setWidth(w + "px");
		kladjeHWTCanvas.setHeight(h + "px");
		kladjeHWTCanvas.setCoordinateSpaceWidth(w);
		kladjeHWTCanvas.setCoordinateSpaceHeight(h);
		backgroundCanvas.setWidth(w + "px");
		backgroundCanvas.setHeight(h + "px");
		backgroundCanvas.setCoordinateSpaceWidth(w);
		backgroundCanvas.setCoordinateSpaceHeight(h);
//		strokeContainerCanvas.setWidth(w + "px");
//		strokeContainerCanvas.setHeight(h + "px");
//		strokeContainerCanvas.setCoordinateSpaceWidth(w);
//		strokeContainerCanvas.setCoordinateSpaceHeight(h);
	}

	/**
	 * getter voor het tekenCanvas
	 * @return het kladjeHWTCanvas
	 */
	public Canvas getCanvas()
	{
		return kladjeHWTCanvas;
	}
	
	/**
	 * zet de Context2d van het tekenCanvas
	 */
	public void initContext2d() 
	{
		gIm = kladjeHWTCanvas.getContext2d();
		backgroundgIm = backgroundCanvas.getContext2d();
		//strokeContainergIm = strokeContainerCanvas.getContext2d();
	}
	
	public Rectangle getBoundingBox(StrokeContainer strokeContainer) 
	{
		int x = (int)strokeContainer.getBoundingBox().x;
		int y = (int)strokeContainer.getBoundingBox().y;
		int width = (int)strokeContainer.getBoundingBox().width;
		int height = (int)strokeContainer.getBoundingBox().height;
		
		return new Rectangle(x, y, width, height);
	}
	
	private KStrokeContainer findInactiveStrokeContainer(int x, int y)
	{
		for(int k=0 ; k<kStrokeContainers.size() ; k++) 
		{	
			if(kStrokeContainers.get(k).contains(x,y,5) && !kStrokeContainers.get(k).isActive())
				return kStrokeContainers.get(k);
		}
		return null;
	}
	
	private KStrokeContainer findStrokeContainer(int x, int y, int distance)
	{
		if(currentStrokeContainer!=null && currentStrokeContainer.contains(x,y, distance))
		{		return currentStrokeContainer;
		}
		for(int k=0 ; k<kStrokeContainers.size() ; k++) 
		{	
			if(kStrokeContainers.get(k).contains(x,y, distance))
			{		return kStrokeContainers.get(k);
			}
		}
		return null;
	}
	
	public String getFormula()
	{
		if(currentStrokeContainer!=null)
			return currentStrokeContainer.getFormulaString();
		return "";
	}
	
	/**
	 * er is een tekstPopup open en zichtbaar: verwerkt de inhoud
	 * (creeer of edit een TekstElement) en verberg de TekstPopup
	 * @param empty if true, maak de (verborgen) TekstPopup leeg
	 */
	public void hideTekstVeld(boolean empty)
	{
		if ((tekstPopup == null) || !tekstPopup.isVisible())
			return;
		
		tekstString = tekstPopup.getText();
		tekstX = tekstPopup.tekstX;
		tekstY = tekstPopup.tekstY;
		tekstPopup.setVisible(false);

		// tekstPopup hoort niet bij een TekstElement
		if (!tekstString.equals("") && (tekstEdited == null))
		{
			TekstElement tekstElement = 
				new TekstElement(drawingColor, tekstString, tekstX, tekstY);
			tekstElement.zetTekst(tekstString, gIm);
			tekstElementVector.addElement(tekstElement);
			addToHistory();
			paint();
		}
		// tekstPopup hoort bij een TekstElement
		else if (!tekstString.equals("") && (tekstEdited != null))
		{
			tekstEdited.zetTekst(tekstString, gIm);
			addToHistory();
			paint();
			tekstEdited = null;
		}
		// TekstElement verwijderen
		else if (tekstString.equals("") && (tekstEdited != null))
		{
			tekstElementVector.removeElement(tekstEdited);
			addToHistory();
			paint();
		}
	
		if (empty)
		{	tekstString = "";
			tekstPopup.setText("");
		}
	}
	

	/**
	 * voeg een snapshot (zie methode getState) toe aan 
	 * de history
	 */
	void addToHistory()
	{

		HashMap<String,Object> stateTable = getState(false);
		
		histories[numHistories] = stateTable;
		numHistories++;
		if (numHistories > maxHistories)
		{	for (int i = 0; i < numHistories - 1; i++)
			{	histories[i] = histories[i + 1];
			}
			numHistories--;
		}		
	}
	
	/**
	 * haal het laatst gemaakte snapshot uit de history
	 * @return een HashMap, zie ook methode setState 
	 */
	public HashMap<String,Object> getFromHistory()
	{	
		if (numHistories > 0)
			numHistories--;
		
		if (numHistories > 0)
		{	
			return histories[numHistories - 1];
		}
		else
		{	numHistories = 0;
			return null;
		}
	}

	/**
	 * stop de status van het werkveld in een HashMap
	 * @return HashMap met status werkveld
	 */
	
	public HashMap<String,Object> getState()
	{
		return getState(true);
	}
	
	public HashMap<String,Object> getState(boolean end)
	{
		HashMap<String,Object> h = new HashMap<String,Object>();
		
		if(end && mouseMode!=ivmOptie && currentStrokeContainer!=null) {
			closeCurrentContainer();
		}
		
		int lastCurrentIndex = kStrokeContainers.indexOf(lastCurrentStrokeContainer);
		h.put("lastCurrentIndex", new Integer(lastCurrentIndex));
		
		if(mouseMode==ivmOptie && currentStrokeContainer!=null)
		{
			Map<String,Object> ivmStrokeContainer = new HashMap <String,Object>();
			ivmStrokeContainer = currentStrokeContainer.getState();
			h.put("ivmStrokeContainer", ivmStrokeContainer);
		}
		
		List<Map<String,Object>> strokeContainerList = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < kStrokeContainers.size(); i++)
		{	KStrokeContainer sc = kStrokeContainers.get(i);
			if(sc != currentStrokeContainer)
				strokeContainerList.add(sc.getState());
		}
		h.put("strokeContainerList", strokeContainerList);
		
		// stop de status van de Strepen in een ArrayList
		List<Map<String,Object>> strepen = new ArrayList<Map<String,Object>>(); 
		for (int sCnt = 0; sCnt < streepVector.size(); sCnt++)
		{	Streep streep = (Streep) streepVector.elementAt(sCnt);
			strepen.add(streep.getState());
		}
		h.put("strepen", strepen);
		
		// stop de status van de Lijnen in een ArrayList
		List<Map<String,Object>> lijnenAL = new ArrayList<Map<String,Object>>();		
		for (int lCnt = 0; lCnt < lijnVector.size(); lCnt++)
		{	Lijn lijn = (Lijn) lijnVector.elementAt(lCnt);
			lijnenAL.add(lijn.getState());
		}
		h.put("lijnenhash", lijnenAL); // !!! lijnen is al in gebruik voor lijntjesop het werkveld
		
		// stop de status van de Rechthoeken in een ArrayList
		List<Map<String,Object>> rechthoeken = new ArrayList<Map<String,Object>>();		
		for (int rCnt = 0; rCnt < rechthoekVector.size(); rCnt++)
		{	Rechthoek rechthoek = (Rechthoek) rechthoekVector.elementAt(rCnt);
			rechthoeken.add(rechthoek.getState());
		}
		h.put("rechthoeken", rechthoeken);

		// stop de status van de Ellipsen in een ArrayList
		List<Map<String,Object>> ellipsen = new ArrayList<Map<String,Object>>();		
		for (int eCnt = 0; eCnt < ellipsVector.size(); eCnt++)
		{	Ellips ellips = (Ellips) ellipsVector.elementAt(eCnt);
			ellipsen.add(ellips.getState());
		}
		h.put("ellipsen", ellipsen);

		// stop de status van de TekstElementen in een ArrayList
		List<Map<String,Object>> tekstElementen = new ArrayList<Map<String,Object>>();		
		for (int tCnt = 0; tCnt < tekstElementVector.size(); tCnt++)
		{	TekstElement tekstElement = (TekstElement) tekstElementVector.elementAt(tCnt);
			tekstElementen.add(tekstElement.getState());
		}
		h.put("tekstElementen", tekstElementen);

		return h;
	}

	
	/**
	 * haal de status uit een HashMap en toon deze op het werkveld
	 * @param map de HashMap met se status
	 * @param init true: inlezen launchdata, d.w.z. docent-objecten
	 * false: dit zijn de door de leerling gecreeerde objecten 
	 */
	public void setState(Map<String, Object> map, boolean init)
	{
		if(map == null || map.isEmpty())
			return;
		ObjectMap launchState = JSONUtilities.wrapMap(map);
		
		kStrokeContainers.clear();
		
		if(mouseMode==ivmOptie)
		{
			Map<String,Object> ivmStrokeContainer = new HashMap<String,Object>();
			
			if (launchState.containsKey("ivmStrokeContainer"))
				ivmStrokeContainer = launchState.getMap("ivmStrokeContainer");
			currentStrokeContainer = new KStrokeContainer(this);
			try {
				currentStrokeContainer.setState(ivmStrokeContainer);
			}
			catch(Exception e) {
				
			}
		}
		
		if (init)
		{	
			docentStreepVector.removeAllElements();
		}
		else // alleen leerling-Strepen verwijderen
			streepVector.removeAllElements();

		List<Map<String,Object>> strokeContainerList = new ArrayList<Map<String,Object>>();
		
		if (launchState.containsKey("strokeContainerList"))
			strokeContainerList = launchState.getMapList("strokeContainerList");
		//logger.info(strokeContainerList.toString());
		for (int sCnt = 0; sCnt < strokeContainerList.size(); sCnt++)
		{	
			KStrokeContainer sc = new KStrokeContainer(this);
			sc.setState(strokeContainerList.get(sCnt));
			kStrokeContainers.add(sc);
		}
		if (launchState.containsKey("lastCurrentIndex")) {
			//int lastCurrentIndex = launchState.getInt("lastCurrentIndex");
			//lastCurrentStrokeContainer = kStrokeContainers.get(lastCurrentIndex);
		}
		
		// alle Strepen verwijderen
		
		if (init)
		{	streepVector.removeAllElements();
			docentStreepVector.removeAllElements();
		}
		else // alleen leerling-Strepen verwijderen
			streepVector.removeAllElements();
		
		List<Map<String,Object>> strepen = new ArrayList<Map<String,Object>>();
		
		if (launchState.containsKey("strepen"))
			strepen = launchState.getMapList("strepen");
		for (int sCnt = 0; sCnt < strepen.size(); sCnt++)
		{	Streep streep = Streep.setState(strepen.get(sCnt));
			if (init)
				docentStreepVector.addElement(streep);
			else
				streepVector.addElement(streep);
		}
		
		// alle Lijnen verwijderen
		if (init)
		{	lijnVector.removeAllElements();
			docentLijnVector.removeAllElements();
		}
		else // alleen leerling-Lijnen verwijderen
			lijnVector.removeAllElements();

		List<Map<String,Object>> lijnenAL = new ArrayList<Map<String,Object>>();

		if (launchState.containsKey("lijnenhash"))
			lijnenAL = launchState.getMapList("lijnenhash");
		for (int lCnt = 0; lCnt < lijnenAL.size(); lCnt++)
		{	Lijn lijn = Lijn.setState(lijnenAL.get(lCnt));

			if (init)
				docentLijnVector.addElement(lijn);
			else
				lijnVector.addElement(lijn);
		}
		
		// alle Rechthoeken verwijderen
		if (init)
		{	rechthoekVector.removeAllElements();
			docentRechthoekVector.removeAllElements();
		}
		else // alleen leerling-Rechthoeken verwijderen
			rechthoekVector.removeAllElements();

		List<Map<String,Object>> rechthoeken = new ArrayList<Map<String,Object>>();
		if (launchState.containsKey("rechthoeken"))
			rechthoeken = launchState.getMapList("rechthoeken");
		for (int rCnt = 0; rCnt < rechthoeken.size(); rCnt++)
		{	Rechthoek rechthoek = Rechthoek.setState(rechthoeken.get(rCnt));

			if (init)
				docentRechthoekVector.addElement(rechthoek);
			else
				rechthoekVector.addElement(rechthoek);
		}

		// alle Ellipsen verwijderen
		if (init)
		{	ellipsVector.removeAllElements();
			docentEllipsVector.removeAllElements();
		}
		else // alleen leerling-Ellipsen verwijderen
			ellipsVector.removeAllElements();

		List<Map<String,Object>> ellipsen = new ArrayList<Map<String,Object>>();
		if (launchState.containsKey("ellipsen"))
			ellipsen = launchState.getMapList("ellipsen");
		for (int eCnt = 0; eCnt < ellipsen.size(); eCnt++)
		{	Ellips ellips = Ellips.setState(ellipsen.get(eCnt));

			if (init)
				docentEllipsVector.addElement(ellips);
			else
				ellipsVector.addElement(ellips);
		}

		// alle TekstElementen verwijderen
		if (init)
		{	tekstElementVector.removeAllElements();
			docentTekstElementVector.removeAllElements();
		}
		else // alleen leerling-TekstElementen verwijderen
			tekstElementVector.removeAllElements();

		List<Map<String,Object>> tekstElementen = new ArrayList<Map<String,Object>>();
		if (launchState.containsKey("tekstElementen"))
			tekstElementen = launchState.getMapList("tekstElementen");
		for (int tCnt = 0; tCnt < tekstElementen.size(); tCnt++)
		{	TekstElement tekstElement = TekstElement.setState(tekstElementen.get(tCnt));

			if (init)
				docentTekstElementVector.addElement(tekstElement);
			else
				tekstElementVector.addElement(tekstElement);
		}

		paint();
	}

	/**
	 * toon het laatst gemaakte snapshot uit de history op het werkveld
	 */
	void undo()
	{
		wis(false);
		HashMap<String,Object> lastState = getFromHistory();
		if (lastState != null)
		{	setState(lastState, false);
		}

		paint();
	}
	/**
	 * wis een enkel geselecteerd object, meerdere geselecteerde
	 * objecten of alle objecten; in het laatste geval, wis ook
	 * de history als complete == true
	 * @param complete true: wis ook de history
	 */
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
	
	/**
	 * smooth een ArrayList of DoublePoints volgens de Gauss-methode
	 * @param doublePoints ArrayList of DoublePoints om the smoothen
	 * @return gesmoothe ArrayList of DoublePoints
	 */
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

	/**
	 * smooth een ArrayList of DoublePoints volgens de average-methode
	 * @param doublePoints ArrayList of DoublePoints om the smoothen
	 * @return gesmoothe ArrayList of DoublePoints
	 */
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

	/**
	 * smooth een ArrayList of DoublePoints
	 * @param doublePoints ArrayList of DoublePoints om the smoothen
	 * @param smoothType type smooth: Gauss, average of 2 keer average achter elkaar
	 * @return gesmoothe ArrayList of DoublePoints
	 */
	public ArrayList<DoublePoint> smooth(ArrayList<DoublePoint> doublePoints, int smoothType)
	{
		if (smoothType == GAUSSIAN)
			return gaussianSmooth(doublePoints);
		else if (smoothType == AVERAGE)
			return averageSmooth(doublePoints);
		else if (smoothType == AVERAGE2)
		{	//ArrayList<DoublePoint> oneSmooth = averageSmooth(doublePoints);
			return averageSmooth(doublePoints);			
		}
		else
			return doublePoints;
	}
	
	private void cleanFormulePoints(ArrayList<fi.writemathgwt.client.engine.Point> fp)
	{
		if(fp.size()<5)
			return;
		double[] angles = new double[fp.size()-1];
		for(int i=1 ; i<fp.size() ; i++) {
			double dx = fp.get(i).getX() - fp.get(i-1).getX();
			double dy = fp.get(i).getY() - fp.get(i-1).getY();
			angles[i-1] = (int)(180.0*(Math.atan2(-dy, dx)/Math.PI));
		}
		double[] dAngles = new double[angles.length-1];
		for(int i=1 ; i<angles.length-1 ; i++) {
			double angleStep1 = angles[i-1];
			double angleStep2 = angles[i];
			
			if(angleStep2-angleStep1>180)
				angleStep2 -= 360;
			if(angleStep2-angleStep1<-180)
				angleStep2 += 360;
			
			double dAngle = angleStep2-angleStep1;
			dAngles[i-1] = dAngle;
		}
		for(int i=3 ; i>-1 ; i--) {
			if(dAngles[i]>80 || dAngles[i]<-80)
				for(int j=0 ; j<i+1 ; j++) {
					fp.remove(0);
					return;
				}
		}
	}
	public void paintFormule(boolean refresh) {
		if(refresh) {
			gIm.clearRect(0, 0, breedte, hoogte);
			gIm.drawImage(backgroundCanvas.getCanvasElement(), 0.0, 0.0);
		}
		if(currentStrokeContainer!=null)
		{
			gIm.setStrokeStyle(CssColor.make(80, 80, 80));
			currentStrokeContainer.draw(gIm);
			
			if (formulaStrokePoints.size() == 1)
			{	fi.writemathgwt.client.engine.Point p =  formulaStrokePoints.get(0);
				gIm.strokeRect(p.x, p.y, 1, 1);
			}
			if (formulaStrokePoints.size() > 1)
			{	
				fi.writemathgwt.client.engine.Point p1 = formulaStrokePoints.get(0);
				gIm.beginPath();
				gIm.moveTo(p1.x, p1.y);
				for (int pCnt = 1; pCnt < formulaStrokePoints.size(); pCnt++)
				{	fi.writemathgwt.client.engine.Point p2 = formulaStrokePoints.get(pCnt);
					gIm.lineTo(p2.x, p2.y);
					p1 = p2;
				}
				gIm.stroke();
			}
		}
		
		if(proActiveStrokeContainer!=null)
		{
			proActiveStrokeContainer.draw(gIm);
		}
		
//		if(lastStroke!=null && formulaStrokePoints.size()==0)
//		{
//			
//			gIm.beginPath();
//			double x0 = (int)lastStroke.getParsePoints().get(0).x;
//			double y0 = (int)lastStroke.getParsePoints().get(0).y;
//			gIm.moveTo(x0, y0);
//			if(lastStroke.getParsePointsbox().width>3 ||  lastStroke.getParsePointsbox().height>3) {
//				for(int j = 1 ; j < lastStroke.getParsePoints().size() ; j++) {
//					double x = lastStroke.getParsePoints().get(j).x ;
//					double y = lastStroke.getParsePoints().get(j).y;
//					gIm.lineTo(x, y);
//				}
//				gIm.moveTo(x0, y0);
//				gIm.closePath();
//				gIm.stroke();
//			}
//			else {
//				gIm.arc(x0, y0, 1.5, 0, 1.5* Math.PI);
//				gIm.closePath();
//				gIm.stroke();
//			}
//			//lastStroke=null;
//		}
		
	}
	
	public void paint()
	{
		if(mouseMode==formuleOptie) {
			paint(backgroundgIm);
			gIm.clearRect(0, 0, breedte, hoogte);
			gIm.drawImage(backgroundCanvas.getCanvasElement(), 0.0, 0.0);
			
			
		}
		else
			paint(gIm);
	}
	
	/**
	 * teken de achtergrond (lijnen/ruitjes, als gewenst) van het werkveld;
	 * NB er is geen achergrondkleur, het werkveld is tramsparent;
	 * roep dan de methode tekenProgramma aan 
	 * @param g Context2d om the tekenen
	 */
	public void paint(Context2d g)
	{
		g.setLineWidth(0.1d);
		// alles weg
		g.clearRect(0, 0, breedte, hoogte);
		
		if(mouseMode==formuleOptie)
			g.drawImage(binImageElement, breedte-60, 10);
		
		if(mouseMode!=ivmOptie && currentStrokeContainer!=null && !currentStrokeContainer.isNotRelevant())
		{	g.setFillStyle( CssColor.make("rgba(200,200,200,0.5)"));
			g.fillRect(0, 0, breedte, hoogte);
		}
		// achtergrond horizontale lijnen 
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
			}
			
		}
		// achtergrond ruitjes
		if (ruitjes)
		{
			if(mouseMode == formuleOptie)
				lineDistance = 10;
			g.setStrokeStyle(ruitjesKleur);
			int vSteps = hoogte / lineDistance;
			for (int vCnt = 1; vCnt <= vSteps; vCnt++)
			{
				g.beginPath();
				g.moveTo(0, vCnt * lineDistance);
				g.lineTo(breedte - 1, vCnt * lineDistance);
				g.stroke();
			}
			int hSteps = breedte / lineDistance;
			for (int hCnt = 1; hCnt <= hSteps; hCnt++)
			{
				g.beginPath();
				g.moveTo(hCnt * lineDistance, 0);
				g.lineTo(hCnt * lineDistance, hoogte - 1);
				g.stroke();
			}
		}
		
		//g.setStrokeStyle(zwart);
		//g.strokeRect(0, 0, breedte, hoogte);
		
		
		
		g.setLineWidth(1.2d); 
		tekenProgramma(g);

	}

	/**
	 * teken de inhoud van het werkveld: <br>
	 * 1) de objecten die door de docent klaargezet zijn<br>
	 * 2) de objecten die de leerling al gecreeerd heeft<br>
	 * 3) objecten die de leerling nog aan het tekenen is, zoals
	 * de punten van een Streep die nog getrokken wordt, 
	 * een lijn waarvan het einde nog gesleept wordt,
	 * een rechthoek of een ellips die nog gesleept worden,
	 * alles wat nog geselecteerd wordt of al geslecteerd is  
	 * @param g Context2d om te tekenen
	 */
	void tekenProgramma(Context2d g)
	{
		
		g.scale(scale, scale);
		g.translate(translation.x,translation.y);
		
		g.setLineWidth(3.0d);
		if(mouseMode==ivmOptie)
			g.setLineWidth(2.0d);
		
		// elementen docent
		for (int sCnt = 0; sCnt < docentStreepVector.size(); sCnt++)
		{	Streep streep = (Streep) docentStreepVector.elementAt(sCnt);
			streep.teken(g);
		}
		for (int lCnt = 0; lCnt < docentLijnVector.size(); lCnt++)
		{	Lijn lijn = (Lijn) docentLijnVector.elementAt(lCnt);
			lijn.teken(g);
		}
		for (int rCnt = 0; rCnt < docentRechthoekVector.size(); rCnt++)
		{	Rechthoek rechthoek = (Rechthoek) docentRechthoekVector.elementAt(rCnt);
			rechthoek.teken(g);
		}
		for (int eCnt = 0; eCnt < docentEllipsVector.size(); eCnt++)
		{	Ellips ellips = (Ellips) docentEllipsVector.elementAt(eCnt);
			ellips.teken(g);
		}
		for (int tCnt = 0; tCnt < docentTekstElementVector.size(); tCnt++)
		{	TekstElement tekstElement = (TekstElement) docentTekstElementVector.elementAt(tCnt);
			tekstElement.teken(g);
		}
		// elementen leerling
		for (int sCnt = 0; sCnt < streepVector.size(); sCnt++)
		{	Streep streep = (Streep) streepVector.elementAt(sCnt);
			if(mouseMode==ivmOptie)
				g.setLineWidth(6.0d);
			streep.teken(g);
		}
		
		for(int k=0 ; k<kStrokeContainers.size() ; k++) 
		{	if(kStrokeContainers.get(k)!=currentStrokeContainer && kStrokeContainers.get(k)!=proActiveStrokeContainer)
			{
				if(currentStrokeContainer!=null && !currentStrokeContainer.isNotRelevant())
					g.setStrokeStyle(CssColor.make(150, 150, 150));
				else
					g.setStrokeStyle(CssColor.make(80, 80, 80));
				kStrokeContainers.get(k).draw(g);
			}
		}
//		if(currentStrokeContainer!=null)
//		{
//			g.setStrokeStyle(CssColor.make(80, 80, 80));
//			currentStrokeContainer.draw(g);
//		}
		
		
		
		if(mouseMode==ivmOptie) {
			if(currentStrokeContainer!=null)
				currentStrokeContainer.draw(g);
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

		// er wordt een streep getekend
		if (draggDoublePoints.size() == 1)
		{	DoublePoint p = (DoublePoint) draggDoublePoints.get(0);
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
		
		// er wordt een formule stroke getekend
//		if (formulaStrokePoints.size() == 1)
//		{	fi.writemathgwt.client.engine.Point p =  formulaStrokePoints.get(0);
//			g.strokeRect(p.x, p.y, 1, 1);
//		}
//		if (mouseMode!=formuleOptie && formulaStrokePoints.size() > 1)
//		{	
//			//ArrayList<DoublePoint> smoothedDraggDoublePoints = smooth(formulaStrokePoints, smoothType);
//			
//			fi.writemathgwt.client.engine.Point p1 = formulaStrokePoints.get(0);
//			g.beginPath();
//			g.moveTo(p1.x, p1.y);
//			for (int pCnt = 1; pCnt < formulaStrokePoints.size(); pCnt++)
//			{	fi.writemathgwt.client.engine.Point p2 = formulaStrokePoints.get(pCnt);
//				g.lineTo(p2.x, p2.y);
//				p1 = p2;
//			}
//			g.stroke();
//			
//		}
		
		
		g.setStrokeStyle(drawingColor);
		
		// er wordt een lijn getekend
		if ((mouseMode == lijnTekenen) && (figuurStart != null) && (lijnEinde != null))
		{	
			
			g.beginPath();
			g.moveTo(figuurStart.x, figuurStart.y);
			g.lineTo(lijnEinde.x, lijnEinde.y);
			g.stroke();
		}

		// er wordt een rechthoek getekend
		if ((mouseMode == rechthoekTekenen) && (tekenRechthoek != null))
		{
			g.beginPath();
			g.strokeRect(tekenRechthoek.x, tekenRechthoek.y, tekenRechthoek.width, tekenRechthoek.height);
		}
		
		// er wordt een ellips getekend
		if ((mouseMode == cirkelTekenen) && (tekenRechthoek != null))
		{
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
			
		}
		
		// niets te tekenen
		if (mouseMode == tekstTekenen)
		{
			
		}

		// de selecteerRechthoek wordt nog gesleept of is uitgesleept;
		// de handles (als die er al zijn) zijn alleen !null als de selecteerRechthoek uitgesleept is
		if ((mouseMode == selecteren) && (selecteerRechthoek != null))
		{
			g.setLineWidth(0.8d);
			g.setStrokeStyle(selectieColor);
			g.beginPath();
			g.strokeRect(selecteerRechthoek.x, selecteerRechthoek.y, 
    					selecteerRechthoek.width, selecteerRechthoek.height);
			g.setLineWidth(1.5d);
			if (schalen)
			{	
				if (topRightHandle != null)
				{	
					g.setStrokeStyle(hbColor);
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
					g.setStrokeStyle(hbColor);
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
					g.setStrokeStyle(hbColor);
						
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
					g.setStrokeStyle(hbColor);
						
					g.beginPath();		
					g.moveTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
					for (int k = 1; k < bottomLeftHandle.aantalPunten; k++) 
					{	g.lineTo(bottomLeftHandle.doubleX[k], bottomLeftHandle.doubleY[k]);
					}
					g.lineTo(bottomLeftHandle.doubleX[0], bottomLeftHandle.doubleY[0]);
					g.closePath();
					g.stroke();
					}
			} // if schalen
			if (roteren)
			{
				if (rotateEastHandle != null)
				{	
					g.setStrokeStyle(hbColor);
					g.beginPath();
		            g.arc(selecteerRechthoek.x + selecteerRechthoek.width + 2 * hbFactor, 
		            	  selecteerRechthoek.y + selecteerRechthoek.height/2, 2 * hbFactor, 0, 2 * Math.PI);
		       	 	g.stroke();
				}
				if (rotateWestHandle != null)
				{	
					g.setStrokeStyle(hbColor);
						
					g.beginPath();
		            g.arc(selecteerRechthoek.x - 2 * hbFactor, 
		            		selecteerRechthoek.y + selecteerRechthoek.height/2, 2 * hbFactor, 0, 2 * Math.PI);
		       	 	g.stroke();
				}

			}

		}
		// geselcteeerde elementen (individuaal of als groep
		if (mouseMode == selecteren)
		{
//			if (selectedStrokeContainer != null)
//			{	selectedStrokeContainer.tekenHandleBox(g);
//			}
			if (selectedStreep != null)
			{	selectedStreep.tekenHandleBox(g);
			}
			if (selectedLijn != null)
			{	selectedLijn.tekenHandleBox(g);
			}
			if (selectedRechthoek != null)
			{	selectedRechthoek.tekenHandleBox(g);
			}
			if (selectedEllips != null)
			{	selectedEllips.tekenHandleBox(g);
			}
			if (selectedTekstElement != null)
			{	selectedTekstElement.tekenBB(g);
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
		
		g.translate(-translation.x,-translation.y);
		g.scale(1/scale, 1/scale);
	}
	
	

	/**
	 * de objecten in de selecteerRechthoek zijn verplaatst, geschaald of
	 * gedraaid; pas de selecteerRechthoek aan zodat ze er weer allemaal inpassen;
	 * doe dit door te zorgen dat hun handle boxes in de selecteerRechthoek passen
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
	
	/**
	 * maak de vier handles om the schalen en de bijbehorende klik-rechthoeken van de selecteerRechthoek
	 */
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
	
	/**
	 * zet de vier handles om the schalen en de bijbehorende klikrechthoeken van de selecteerRechthoek op null
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
	 * maak de klik-rechtoeken voor de twee draai-handles van de selecteerRechthoek
	 */
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

	/**
	 * zet de klik-rechtoeken voor de twee draai-handles van de selecteerRechthoek op null
	 */
	public void killRotateHandles()
	{
		rotateEastHandle = null; 
		rotateWestHandle = null;
	}

	/**
	 * zet alle selected objects op null
	 */
	public void resetSelectedObject()
	{
		selectedStreep = null;
		selectedLijn = null;
		selectedRechthoek = null;
		selectedEllips = null;
		selectedTekstElement = null;
	}
	
	/**
	 * maak de Vector objectsSelected leeg
	 */
	public void resetSelectedObjects()
	{
		objectsSelected.removeAllElements();
	}
	
	/**
	 * check of een object geselecteerd is, d.w.z. 
	 * een van selectedStreep, selectedLijn, selectedRechthoek,
	 * selectedEllips of selectedTekstElement is niet null
	 * @return true/false
	 */
	public boolean objectSelected()
	{
		return (selectedStreep != null)|| 
		   (selectedLijn != null) || 
		   (selectedRechthoek != null) || 
		   (selectedEllips != null) ||
		   (selectedTekstElement != null);		
	}
	
	public void setSelecteerMode()
	{
		mouseMode = selecteren;
		closeCurrentContainer();
	}
	
	public void setCorrect(boolean correct) {
		if(currentStrokeContainer!=null)
			currentStrokeContainer.setCorrect(correct);
		else if(lastCurrentStrokeContainer!=null) {
			lastCurrentStrokeContainer.setCorrect(correct);
			currentStrokeContainer = lastCurrentStrokeContainer;
			currentStrokeContainer.scale(schrijfLeesFactor/1.0);
			currentStrokeContainer.setActive(true);
			lastCurrentStrokeContainer = null;
		}
		//paint();
	}
	
	public void setFalse(boolean isfalse) {
		if(currentStrokeContainer!=null)
			currentStrokeContainer.setFalse(isfalse);
		else if(lastCurrentStrokeContainer!=null) {
			lastCurrentStrokeContainer.setFalse(isfalse);
			currentStrokeContainer = lastCurrentStrokeContainer;
			currentStrokeContainer.scale(schrijfLeesFactor/1.0);
			currentStrokeContainer.setActive(true);
			lastCurrentStrokeContainer = null;
		}
		//paint();
	}
	
	/**
	 * kijk of de bounding box van een van de objecten 
	 * het punt met coordinaten (x,y) bevat en als ja, zet 
	 * het corresponderende selectedObject  
	 * NB er kan maar een object geselecteerd worden
	 * @param x x-coordinaat te checken punt
	 * @param y y-coordinaat te checken punt
	 * @return true als er een object het punt bevat 
	 */
	public boolean setSelectedObject(int x, int y)
	{	boolean found = false;
	
		for (int sCnt = 0; sCnt < kStrokeContainers.size(); sCnt++)
		{	KStrokeContainer sc = kStrokeContainers.get(sCnt);
			if (sc.contains(x, y))
			{	selectedStrokeContainer = sc;
				selectedStreep = null;
				selectedLijn = null;
				selectedRechthoek  = null;
				selectedEllips  = null;
				selectedTekstElement  = null;
				return true; 
			}
		}
		for (int sCnt = 0; sCnt < streepVector.size(); sCnt++)
		{	Streep streep = (Streep) streepVector.elementAt(sCnt);
			if (streep.bbContains(x, y))
			{	selectedStreep = streep;
				selectedStrokeContainer = null;
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
				selectedStrokeContainer = null;
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
				selectedStrokeContainer = null;
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
				selectedStrokeContainer = null;
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
				selectedStrokeContainer = null;
				selectedStreep = null;
				selectedLijn  = null;
				selectedRechthoek  = null;
				selectedEllips  = null;
				return true; 
			}
		}
	
		return found;
	}
	
	/**
	 * kijk of de bounding box van een TeksElement het punt met coordinaten
	 * (x,y) bevat
	 * @param x x-coordinaat te checken punt
	 * @param y y-coordinaat te checken punt
	 * @return het TekstElement of null 
	 */
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
	
	/**
	 * zoek alle objecten die binnen de rechthoek r liggen  
	 * en stop ze in objectsSelected (maak deze Vector eerst leeg!)
	 * @param r de zoek rechthoek
	 * @return true als er objecten gevonden zijn, false als niet
	 */
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
	
	
	/**
	 * wis het geselecteerde object (als dat er is en als wissen mag, 
	 * d.i. het object is niet deel van de launchdata);
	 * zet sleepSelectie op false 
	 * als er iets gewist is, update de history 
	 */
	public void wisObjectSelected()
	{ 
		boolean gewist = false;
		if (selectedStrokeContainer != null)// && selectedStrokeContainer.deletable)
		{	kStrokeContainers.remove(selectedStrokeContainer);
			selectedStrokeContainer = null;
			gewist = true;
		}
		if (selectedStreep != null && selectedStreep.deletable)
		{	streepVector.removeElement(selectedStreep);
			selectedStreep = null;
			gewist = true;
		}
		if (selectedLijn != null && selectedLijn.deletable)  
		{	lijnVector.removeElement(selectedLijn);
			selectedLijn = null;
			gewist = true;
		}
		if (selectedRechthoek != null && selectedRechthoek.deletable)  
		{	rechthoekVector.removeElement(selectedRechthoek);
			selectedRechthoek = null;
			gewist = true;
		}
		if (selectedEllips != null && selectedEllips.deletable) 
		{	ellipsVector.removeElement(selectedEllips);
			selectedEllips = null;
			gewist = true;
		}
		if (selectedTekstElement != null && selectedTekstElement.deletable)
		{	tekstElementVector.removeElement(selectedTekstElement);
			selectedTekstElement = null;
			gewist = true;
		}
		
		sleepSelectie = false;
		
		if (gewist)
			addToHistory();
		paint();
	}

	/**
	 * wis alle geselecteerde objecten (dus een groep van meer dan een) 
	 * als die er zijn en als wissen mag (d.i. het object is niet deel van de launchdata);
	 * zet sleepSelectie op false en verwijder se seelcteerRechthoek en handles 
	 * als er iets gewist is, update de history 
	 */
	public void wisObjectsSelected()
	{ 
		boolean gewist = false;
		for (int oCnt = 0; oCnt < objectsSelected.size(); oCnt++)
		{
			Object o = (Object) objectsSelected.elementAt(oCnt);
			if (o instanceof Streep && ((Streep) o).deletable)
			{	streepVector.removeElement((Streep) o);
				gewist = true;
			}
			else if (o instanceof Lijn && ((Lijn) o).deletable)
			{	lijnVector.removeElement((Lijn) o);
				gewist = true;
			}
			else if (o instanceof Rechthoek && ((Rechthoek) o).deletable)
			{	rechthoekVector.removeElement((Rechthoek) o);
				gewist = true;
			}
			else if (o instanceof Ellips && ((Ellips) o).deletable)
			{	ellipsVector.removeElement((Ellips) o);
				gewist = true;
			}
			else if (o instanceof TekstElement && ((TekstElement) o).deletable)
			{	tekstElementVector.removeElement((TekstElement) o);
				gewist = true;
			}
			
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

	/**
	 * verplaats het geselecteerde object over de vector (dx,dy)
	 * @param dx x-verplaatsing
	 * @param dy y-verplaatseing
	 */
	public void translateObjectSelected(int dx, int dy)
	{ 
		if (selectedStrokeContainer != null)
			selectedStrokeContainer.translate(dx, dy);
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

	/**
	 * verplaats alle geselecteerde objecten over de vector (dx,dy)
	 * @param dx x-verplaatsing
	 * @param dy y-verplaatseing
	 */
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

	/**
	 * kijk of de handle box van het geselecteerde object het punt
	 * (x,y) bevat 
	 * @param x x-coordinaat te checken punt
	 * @param y y-coordinaat te checken punt
	 * @return true/false
	 */
	public boolean objectSelectedContains(int x, int y)
	{ 
		return ((selectedStreep != null) && 
				 selectedStreep.handleBox.contains(x, y)) ||
			   ((selectedLijn != null) && 
			     selectedLijn.handleBox.contains(x, y)) ||	   
			   ((selectedRechthoek != null) && 
				 selectedRechthoek.handleBox.contains(x, y)) ||	   
			   ((selectedEllips != null) && 
				 selectedEllips.handleBox.contains(x, y)) ||	   
			   ((selectedTekstElement != null) && 
			     selectedTekstElement.handleBox.contains(x, y));	   
	}

	/**
	 * een van de handles (schaal of draai) van de selecteerRechthoek werd aangeklikt
	 * en is nu een stukje versleept over de vector (dx,dy); afhankelijk van het type
	 * handle, schaal of roteer alle objecten in de selecteerRechthoek en bereken daarna
	 * een nieuwe selecteerRechthoek en handles 
	 * @param dx x-translatie
	 * @param dy y-translatie
	 */
	public void processSelecteerRechthoekHandleAction(int dx, int dy)
	{
		// dit wordt het schaal- en draaicentrum
		int crx = selecteerRechthoek.x + selecteerRechthoek.width / 2;
		int cry = selecteerRechthoek.y + selecteerRechthoek.height / 2;
		
		if (scalingTopRight)
		{
			double aspectDirX = selecteerRechthoek.x + selecteerRechthoek.width - crx;
			double aspectDirY = selecteerRechthoek.y - cry;
			double dxDouble = (double) dx;
			double dyDouble = (double) dy;
			double aa = aspectDirX * aspectDirX + aspectDirY * aspectDirY;
			double s = (aspectDirX * dxDouble + aspectDirY * dyDouble) / aa;
			double asXDouble = s * aspectDirX;
			double oldWidth = (double) selecteerRechthoek.width / 2;
			double newWidth = oldWidth + asXDouble;
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
			double oldWidth = (double) selecteerRechthoek.width / 2;
			double newWidth = oldWidth + asXDouble;
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
				
			}
			
			updateSelecteerRechthoek();
		}

		
	}

	/**
	 * een van de handles (schaal of draai) van een geslecteerd object werd aangeklikt
	 * en is nu een stukje versleept over de vector (dx,dy); afhankelijk van het type
	 * handle, schaal of roteer dit object; de handle box wordt vanzelf aangepast
	 * @param dx x-translatie
	 * @param dy y-translatie
	 */
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
				double oldWidth = (double) selectedStreep.handleBox.width / 2;
				double newWidth = oldWidth + asXDouble;
				double sc = ((double) newWidth) / oldWidth;
				selectedStreep.scale(sc);
			}
			else if (scalingTopLeft)
			{
				
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
				double oldWidth = (double) selectedStreep.handleBox.width / 2;
				double newWidth = oldWidth + asXDouble;
				double sc = ((double) newWidth) / oldWidth;
				selectedStreep.scale(sc);
			}
			else if (rotatingEast)
			{
				// hier is alleen dy van belang; naar beneden = dy < 0 = met de klok mee
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
				double oldWidth = (double) selectedLijn.handleBox.width / 2;
				double newWidth = oldWidth + asXDouble;
				double sc = ((double) newWidth) / oldWidth;
				selectedLijn.scale(sc);
			}
			else if (scalingTopLeft)
			{
				
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
				double oldWidth = (double) selectedLijn.handleBox.width / 2;
				double newWidth = oldWidth + asXDouble;
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
				double oldWidth = (double) selectedRechthoek.handleBox.width / 2;
				double newWidth = oldWidth + asXDouble;
				double sc = ((double) newWidth) / oldWidth;
				selectedRechthoek.scale(sc);
			}
			else if (scalingTopLeft)
			{
				
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
				double oldWidth = (double) selectedRechthoek.handleBox.width / 2;
				double newWidth = oldWidth + asXDouble;
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
				double oldWidth = (double) selectedEllips.handleBox.width / 2;
				double newWidth = oldWidth + asXDouble;
				double sc = ((double) newWidth) / oldWidth;
				selectedEllips.scale(sc);
			}
			else if (scalingTopLeft)
			{
				
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
				double oldWidth = (double) selectedEllips.handleBox.width / 2;
				double newWidth = oldWidth + asXDouble;
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

			if (scalingBottomRight)
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
				double oldWidth = (double) selectedTekstElement.handleBox.width / 2;
				double newWidth = oldWidth + asXDouble;
				double sc = ((double) newWidth) / oldWidth;
				
				selectedTekstElement.scale(sc);
				
			}
		}
	}
	
	private void processIVM() {
		streepVector.clear();
		ArrayList<DoublePoint> pointsLeft = new ArrayList<DoublePoint>();
		ArrayList<DoublePoint> pointsRight = new ArrayList<DoublePoint>();
		ArrayList<DoublePoint> pointsBottom = new ArrayList<DoublePoint>();
		double vaasX = 500;
		double volumeUnit = 20*lastStroke.getParsePointsbox().height;
		
		//ArrayList<DoublePoint> points = smooth(lastStroke.getParsePoints(), smoothType);
		double vaasY = lastStroke.getParsePoints().get(0).y;
		for(int j = 1 ; j < lastStroke.getParsePoints().size() ; j++) {
			double dx = lastStroke.getParsePoints().get(j).x - lastStroke.getParsePoints().get(j-1).x ;
			double dy = lastStroke.getParsePoints().get(j).y - lastStroke.getParsePoints().get(j-1).y;
			
			double r = Math.sqrt(-volumeUnit*dx/dy);
			pointsLeft.add(new DoublePoint(vaasX-r, lastStroke.getParsePoints().get(j).y));
			pointsRight.add(new DoublePoint(vaasX+r, lastStroke.getParsePoints().get(j).y));
			pointsBottom.add(pointsLeft.get(0));
			pointsBottom.add(pointsRight.get(0));
		}
		ArrayList<DoublePoint> smoothedPointsLeft = pointsLeft;//smooth(pointsLeft, smoothType);
		ArrayList<DoublePoint> smoothedPointsRight = pointsRight;//smooth(pointsRight, smoothType);
		
		Streep streepLeft = new Streep(drawingColor, smoothedPointsLeft);
		Streep streepRight = new Streep(drawingColor, smoothedPointsRight);
		Streep streepBottom = new Streep(drawingColor, pointsBottom);
		
		streepVector.addElement(streepLeft);
		streepVector.addElement(streepRight);
		streepVector.addElement(streepBottom);
		
		paint();
	}

	/**
	 * check of een van de schaal- of draaihandles (als die er al zijn)
	 * van de selecteerRechthoek het punt met coordinaten (x,y) bevat
	 * en zet de booleans groupHandleAction en de action-booleans
	 * @param x x-coordinaat te checken punt
	 * @param y y-coordinaat te checken punt
	 * @return true/false
	 */
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

	/**
	 * check of een van de schaal- of draaihandles (als die er al zijn)
	 * van een geselecteerd object het punt met coordinaten (x,y) bevat
	 * en zet de booleans handleAction en de action-booleans
	 * @param x x-coordinaat te checken punt
	 * @param y y-coordinaat te checken punt
	 * @return true/false
	 */
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
			if ((selectedTekstElement.bottomRightRect != null) && selectedTekstElement.bottomRightRect.contains(x,y))
			{	handleAction = true;
				scalingBottomRight = true;
			}
		}
		
		return handleAction;
	}
	
	private void closeCurrentContainer() {
		currentStrokeContainer.setActive(false);
		currentStrokeContainer.scale(1.0/schrijfLeesFactor);
//		if(currentStrokeContainer.getBox().x < 0)
//			currentStrokeContainer.translate(-currentStrokeContainer.getBox().x+30, 0);
		currentStrokeContainer.translate(-activeTranslation.x, -activeTranslation.y);
		activeTranslation.x = 0;
		activeTranslation.y = 0;
		lastCurrentStrokeContainer = currentStrokeContainer;
		currentStrokeContainer=null;
	}
	
	private KStrokeContainer proActiveStrokeContainer;
	private int proActiveX;
	private int proActiveY;
	private boolean writing;
	private boolean moving;
	private Point activeTranslation = new Point(0,0);
	
	public void mouseDownTouch2StartAction(int eventX, int eventY)
	{
		mouseDownTouchStartAction(eventX, eventY);
		paint();
	}

	/**
	 * actie bij MouseDown/TouchStart met coordinaten (eventX,eventY): <br>
	 * tekenen: voeg (eventX,eventY) toe aan draggDoublePoints<br>
	 * lijn/rechthoek/cirkel tekenen: fixeer figuurStart op (eventX,eventY)<br>
	 * teksttekenen: maak en open een TekstPopup en bepaal of die dient voor een nieuw
	 * TekstElement of om een bestaand TekstElement te editen<br>
	 * selecteren: kijk of er een selecteerRechthoek is en of een van de handles daarvan 
	 * aangeklikt is of dat een punt binnen de selecteerRechthoek aangeklikt is (dit wordt een sleep);<br>
	 * kijk of een individeel object geselecteerd is en of een van de handles van dit object aangeklikt is
	 * of dat een punt binnen de handle box aangeklikt is (dit wordt een sleep);<br> "niets" aanklikken
	 * verwijderd selecteerRechthoek en andere handle boxes en is de start van een nieuwe selecteerRechthoek     
	 * @param eventX x-coordinaat MouseDown/TouchStart
	 * @param eventY y-coordinaat MouseDown/TouchStart
	 */
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		eventX = (int)(eventX/scale -translation.x);
		eventY = (int)(eventY/scale -translation.y);
		
		if (mouseMode == tekenen)
		{
			mouseDown = true;
			draggDoublePoints.add(new DoublePoint(eventX, eventY));
			paint();
		}
		else if (mouseMode == ivmOptie)
		{
			//if(currentStrokeContainer==null)
				currentStrokeContainer = new KStrokeContainer(this);
			
			formulaStrokePoints.clear();
			mouseDown = true;
			formulaStrokePoints.add(new fi.writemathgwt.client.engine.Point(eventX, eventY));
			paint();
			
		}
		else if (mouseMode == formuleOptie)
		{
			mouseDown = true;
			startX = eventX;
			startY = eventY;
			
			if(currentStrokeContainer!=null && currentStrokeContainer.getCloseButtonArea().contains(eventX, eventY)) {
				closeCurrentContainer();
				paint();
				return;
			}
			
			
			if(currentStrokeContainer!=null && currentStrokeContainer.getCheckButtonArea().contains(eventX, eventY)) {
				eigenaar.fireCheck();
				return;
			}
					
			if(currentStrokeContainer!=null && !currentStrokeContainer.writeBoxContains(eventX, eventY)) {
				closeCurrentContainer();
				eigenaar.setChanged();
			}
			
			KStrokeContainer ksc = findInactiveStrokeContainer(eventX, eventY);
			//if(ksc!=null)
				
			proActiveStrokeContainer = findInactiveStrokeContainer(eventX, eventY);
			if(currentStrokeContainer==null && proActiveStrokeContainer!=null) {
				proActiveX = proActiveStrokeContainer.getBox().x;
				proActiveY = proActiveStrokeContainer.getBox().y;
				paint();
				paintFormule(true);
				return;
			}
			
			if(currentStrokeContainer==null || !currentStrokeContainer.writeBoxContains(eventX, eventY) && proActiveStrokeContainer==null) {
				currentStrokeContainer = new KStrokeContainer(this);
				currentStrokeContainer.setActive(true);
				kStrokeContainers.add(currentStrokeContainer);
			}
			
			
			proActiveStrokeContainer = null;
			formulaStrokePoints.clear();
			//draggDoublePoints.clear();
			mouseDown = true;
			formulaStrokePoints.add(new fi.writemathgwt.client.engine.Point(eventX, eventY));
			//draggDoublePoints.add(new DoublePoint(eventX, eventY));
			paintFormule(true);
			//eigenaar.setChanged();
			

		}
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
			
			// positie tekstPopup bij nieuwe tekst
			startX = eventX + kladjeHWTCanvas.getAbsoluteLeft();
			startY = eventY + kladjeHWTCanvas.getAbsoluteTop();

			// verwerk openstaande en zichtbare TekstPopup
			if ((tekstPopup != null) && tekstPopup.isVisible())
			{
				hideTekstVeld(true);
			}
			// is er een TekstElement aangeklikt
			tekstEdited = getClickedTekstElement(eventX, eventY);
			// nieuwe TekstPopup
			tekstPopup = new TekstPopup(this, eventX, eventY);
			// tekst bewerken
			if (tekstEdited != null)
			{	tekstPopup.setText(tekstEdited.tekst);
				tekstPopup.setTextColor(tekstEdited.kleur.toString());
				tekstPopup.setPopupPosition(tekstEdited.bb2.geefPuntX(0) + kladjeHWTCanvas.getAbsoluteLeft(),
						                    tekstEdited.bb2.geefPuntY(0) + kladjeHWTCanvas.getAbsoluteTop());
			}
			else  // nieuwe tekst
			{	tekstPopup.setTextColor(drawingColor.toString());
				tekstPopup.setPopupPosition(startX - 8, startY - 8);
			}
			tekstPopup.show();
			tekstPopup.textBox.setFocus(true);
			paint();
			
		}
		else if (mouseMode == selecteren)
		{
			mouseDown = true;

			// geklikt op een van de handles van de selecteerRechthoek (start van 
			// schalen of draaien van een groep objecten) 
			if (selecteerRechthoekHandlesContain(eventX, eventY))
			{
				startX = eventX;
				startY = eventY;
				
				objectHandled = false;
			}
			// geklikt op een punt in de selecteerRechthoek (start van een groepssleep)
			else if ((selecteerRechthoek != null) && selecteerRechthoek.contains(eventX, eventY))
			{
				resetSelectedObject();
				sleepSelectie = true;
				startX = eventX;
				startY = eventY;
			}

			// geklikt op een van de handles van de handle box van een geselecteerd object (start van 
			// schalen of draaien van individueel objecten) 
			else if (objectSelectedHandlesContain(eventX, eventY))
			{
				startX = eventX;
				startY = eventY;
			
				objectHandled = false;
				
			}
			// individueel object aangeklikt, was mogelijk al geselecteerd
			// verwijder de selecteerRechthoek
			else if (setSelectedObject(eventX, eventY) || objectSelectedContains(eventX, eventY))
			{
				sleepSelectie = true;
				startX = eventX;
				startY = eventY;
				selecteerRechthoek = null;
				killScaleHandles();
				killRotateHandles();

				resetSelectedObjects();

				objectMoved = false;
			}
			else // niets relevants aangeklikt, unselect alles, verwijder de selecteerRechthoek
				 // fixeer (eventX,eventY) als mogelijk startpunt van een nieuwe selecteerRechthoek 
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
	
	public void mouseMoveTouch2MoveAction(int eventX, int eventY)
	{
		if(currentStrokeContainer != null) {
			formulaStrokePoints.clear();
			//draggDoublePoints.clear();
			int dx = eventX - startX;
			int dy = eventY - startY;
			currentStrokeContainer.translate(dx, dy);
			activeTranslation.x += dx;
			activeTranslation.y += dy;
			paintFormule(true);
			startX = eventX;
			startY = eventY;
		}
	}

	/**
	 * actie bij MouseMove/TouchMove met coordinaten (eventX,eventY): <br>
	 * tekenen: voeg (eventX,eventY) toe aan de streep die getekend wordt <br>
	 * lijntekenen; verbindt figuurStart met (eventX,eventY), maak er een horizontale/
	 * vertikale/diagonale lijn van als ShiftPressed == true (alleen op de PC)<br>
	 * rechthoek/cirkel tekenen: teken een rechthoek (of een ellips in die rechthoek) met
	 * tegenoverliggende hoekpunten figuurStart en (eventX,eventY); maak er een
	 * vierkant/cirkel van als ShiftPressed == true (alleen op de PC)<br>
	 * teksttekenen: niets te doen<br>
	 * selecteren: als groupHandleAction == true: process de handle actie voor de geselecteerde
	 * groep<br> als handleAction == true: process de handle actie voor het geselecteerde
	 * object<br> als sleepselectie == true: sleep de geslecteerdr groep of het geselecteerde 
	 * object<br> anders: vorm de selecteerRechthoek        
	 * @param eventX x-coordinaat MouseMove/TouchMove
	 * @param eventY y-coordinaat MouseMove/TouchMove
	 * @param shiftPressed is de Shift-key ingedrukt (alleen op de PC)
	 */
	public void mouseMoveTouchMoveAction(int eventX, int eventY, boolean shiftPressed)
	{
		eventX = (int)(eventX/scale -translation.x);
		eventY = (int)(eventY/scale -translation.y);
		
		if (!mouseDown)
			return;
		
		if (mouseMode == tekenen)
		{
			draggDoublePoints.add(new DoublePoint(eventX, eventY));
			paint();
		}
		else if (mouseMode == ivmOptie)
		{
			if(formulaStrokePoints.size()>0) {
				int dx = (int)(formulaStrokePoints.get(formulaStrokePoints.size()-1).x) - eventX;
				int dy = (int)(formulaStrokePoints.get(formulaStrokePoints.size()-1).y) - eventY;
				if(dx*dx+dy*dy>30)
					formulaStrokePoints.add(new fi.writemathgwt.client.engine.Point(eventX, eventY));
			}
			paint();
			paintFormule(false);
			
		}
		else if (mouseMode == formuleOptie)
		{
			if(proActiveStrokeContainer!=null )
			{
				int dx = eventX - startX;
				int dy = eventY - startY;
				proActiveStrokeContainer.translate(dx, dy);
				startX = eventX;
				startY = eventY;
			}
			if(formulaStrokePoints.size()>0) {
				formulaStrokePoints.add(new fi.writemathgwt.client.engine.Point(eventX, eventY));
				//draggDoublePoints.add(new DoublePoint(eventX, eventY));
			//	paintFormule();
			}
			//else 
				paintFormule(true);
		}
		else if (mouseMode == lijnTekenen)
		{

			// maak er een horizontale/vertikale/diagoale lijn van 
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
			else // vrije lijn
			{	
				lijnEinde = new Point(eventX, eventY);
			}
			
			paint();
			
		} // lijnTekenen

		else if ((mouseMode == rechthoekTekenen) || (mouseMode == cirkelTekenen))
		{
			
			if (figuurStart != null)
			{
				
				// maak er een vierkant/cirkel van
				if (shiftPressed)
				{
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
				else // vindt de juiste rechthoek
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
			else // kan eigenlijk niet
			{	tekenRechthoek = new Rectangle(10, 10, 10, 10);
				paint();
			}
			
		} //rechthoek && cirkel
		
		// niets te doen
		else if (mouseMode == tekstTekenen)
		{
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

			// verplaats de selecteerRechthoek met inhoud of het geselecteerde object
			else if (sleepSelectie) 
			{
				
				int dx = eventX - startX;
				int dy = eventY - startY;
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
				
				objectMoved = true;
				
				paint();
				
				
			}
			else // sleepSelectie, vorm de selecteerRechthoek
			{	
				
				if (figuurStart == null)
					return;
				
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

	/**
	 * actie bij MouseUp/TouchEnd<br>
	 * tekenen: smooth alle getekende punten en maak er een Streep van <br>
	 * lijntekenen; maak een Lijn van figuurStart naar lijnEinde <br>
	 * rechthoek tekeken: maak een Rechthoek van tekenRechthoek<br>
	 * cirkel tekenen: maak een Ellips die in tekenRechthoek past<br>
	 * teksttekenen: niets te doen<br>
	 * selecteren: als sleepSelectie == true: be-eindig de sleep;<br>
	 * kijk dan naar ObjectsSelected: als size == 0: sleepRechthoek weg;
	 * als size == 1: sleepRechthoek weg en toon handle box van dit geselecteerde
	 * object; als size groter dan 1: geef de selecteerRechthoek handles (als dat mag)         
	 */
	public void mouseUpTouchEndAction(int eventX, int eventY)
	{
		if (mouseMode == tekenen)
		{	
			ArrayList<DoublePoint> smoothedDraggDoublePoints = smooth(draggDoublePoints, smoothType);
			
			Streep streep = new Streep(drawingColor, smoothedDraggDoublePoints);
			streepVector.addElement(streep);
			if (draggDoublePoints.size() > 1)
				addToHistory();

			draggDoublePoints.clear();
			
			paint();
			eigenaar.setChanged();
		}
		else if (mouseMode == ivmOptie)
		{
			//ArrayList<DoublePoint> smoothedPoints = smooth(formulaStrokePoints, smoothType);
			currentStrokeContainer.clear();
			lastStroke = new Stroke(formulaStrokePoints);
			currentStrokeContainer.addStroke(lastStroke);
			formulaStrokePoints.clear();
			processIVM();
			
		}
		else if (mouseMode == formuleOptie)
		{	
			if(proActiveStrokeContainer!=null) 
			{
//				if(proActiveStrokeContainer.getBox().contains(breedte-60, 60) 
//						|| (new Rectangle(breedte-60,0,60,60)).contains(proActiveStrokeContainer.getBox().x, proActiveStrokeContainer.getBox().y)) {
//					kStrokeContainers.remove(proActiveStrokeContainer);
//					proActiveStrokeContainer = null;
//					paint();
//					return;
//				}
				if(eventX>breedte-60 && eventY<60 || proActiveStrokeContainer.getBox().x>breedte || proActiveStrokeContainer.getBox().y>hoogte) {
					kStrokeContainers.remove(proActiveStrokeContainer);
					proActiveStrokeContainer = null;
					paint();
					return;
				}
				int pX = proActiveStrokeContainer.getBox().x - proActiveX;
				int pY = proActiveStrokeContainer.getBox().y - proActiveY;
				
				
				
				boolean nietVerschoven = pX*pX+pY*pY<16;
				if(nietVerschoven)
				{
					currentStrokeContainer = proActiveStrokeContainer;
					currentStrokeContainer.scale(schrijfLeesFactor/1.0);
					currentStrokeContainer.setActive(true);
					
				}
				proActiveStrokeContainer=null;
				paint();
			}
			
			if(formulaStrokePoints.size()>0) 
			{
//				if(currentStrokeContainer==null) {
//					currentStrokeContainer = new KStrokeContainer(this);
//					currentStrokeContainer.setActive(true);
//				}
//				lastStroke = new Stroke(formulaStrokePoints);
//				DoubleRectangle r = lastStroke.getParsePointsbox();
//				lastStroke.translate(-r.x, -r.y);
//				lastStroke.scale(0, 0, 10);
				cleanFormulePoints(formulaStrokePoints);
				if(!currentStrokeContainer.addStroke(new Stroke(formulaStrokePoints)))
					currentStrokeContainer.addStroke(new Stroke(formulaStrokePoints,""));
				
				currentStrokeContainer.setCorrect(false);
				currentStrokeContainer.setFalse(false);
				if(currentStrokeContainer.getStrokeCount()==1)
					paint();
			}
			if(currentStrokeContainer!=null && currentStrokeContainer.isNotRelevant())
			{	kStrokeContainers.remove(currentStrokeContainer);
				currentStrokeContainer = null;
				formulaStrokePoints.clear();
				//draggDoublePoints.clear();
				paint();
				//setSelecteerMode();
				return;
			}
			formulaStrokePoints.clear();
			//draggDoublePoints.clear();
			paintFormule(true);
			if(currentStrokeContainer==null)
				eigenaar.sendDrawing();
			else
				eigenaar.sendEquation();
		}
		
		else if (mouseMode == lijnTekenen)
		{
			
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
				if (objectMoved)
					addToHistory();
				objectMoved = false;
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
			{	addToHistory();
			
			}
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
		
		} // selecteren
		
	}
	
	/**
	 * inner class voor afhandelen Mouse Events;
	 * roep de corresponderende actie aan 
	 * @author huub
	 */
	boolean mouseOnRight = false;
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		
		public void onMouseDown(MouseDownEvent e)
		{
			e.preventDefault();
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			if (e.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
				mouseOnRight = true;
				startX = eventX;
				startY = eventY;
				mouseDownTouch2StartAction(eventX, eventY);
				return;
			}
			mouseDownTouchStartAction(eventX, eventY);
		}
		
		public void onMouseMove(MouseMoveEvent e)	
		{
			e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();
			boolean shiftPressed = e.isShiftKeyDown();
			
			if(mouseOnRight)
				mouseMoveTouch2MoveAction(eventX, eventY);
			else
				mouseMoveTouchMoveAction(eventX, eventY, shiftPressed);
			
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{
			int eventX = e.getX();
			int eventY = e.getY();
			
			logger.info("mouse up");
			e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			mouseDown = false;
			mouseOnRight = false;
			if (e.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
				return;
			}
			mouseUpTouchEndAction(eventX, eventY);
		}

	}


	/**
	 * inner class voor afhandelen Touch Events
	 * roep de corresponderende actie aan 
	 * @author huub
	 */
	class MGWTTouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() == 0)
				return;
			
			Touch touch = e.getTouches().get(0);
			
			int eventX = touch.getPageX() - kladjeHWTCanvas.getAbsoluteLeft();
			int eventY = touch.getPageY() - kladjeHWTCanvas.getAbsoluteTop();
			
			
			if ( (e.getTouches().length() == 2) ) {
				moving = true;
				writing = false;
				startX = eventX;
				startY = eventY;
				mouseDownTouch2StartAction(eventX, eventY);
				return;
			}	
			if (e.getTouches().length() == 1 && !moving ) {
				writing = true;
				mouseDownTouchStartAction(eventX, eventY);
			}
			if ( (e.getTouches().length() > 2) ) {
				moving = false;
				writing = false;
				
			}			

			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() ==1)
			{
				Touch touch = e.getTouches().get(0);
				
			    boolean shiftPressed = false;
			    int eventX = touch.getPageX() - kladjeHWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - kladjeHWTCanvas.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(eventX, eventY, shiftPressed);
				
		    }
			if (moving && e.getTouches().length() ==2)
			{
				Touch touch = e.getTouches().get(0);
				
			    boolean shiftPressed = false;
			    int eventX = touch.getPageX() - kladjeHWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - kladjeHWTCanvas.getAbsoluteTop();				
			    
				mouseMoveTouch2MoveAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			Touch touch = e.getTouches().get(0);
			
		    int eventX = touch.getPageX() - kladjeHWTCanvas.getAbsoluteLeft();
			int eventY = touch.getPageY() - kladjeHWTCanvas.getAbsoluteTop();
			
			moving = false;
			mouseUpTouchEndAction(eventX, eventY);
		}

	}

}

