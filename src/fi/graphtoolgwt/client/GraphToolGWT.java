package fi.graphtoolgwt.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleTeken;
import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.FacetHelper;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.HandlerRegistrations;
import com.vaadin.pointerevents.client.PointerCancelEvent;
import com.vaadin.pointerevents.client.PointerCancelHandler;
import com.vaadin.pointerevents.client.PointerDownEvent;
import com.vaadin.pointerevents.client.PointerDownHandler;
import com.vaadin.pointerevents.client.PointerMoveEvent;
import com.vaadin.pointerevents.client.PointerMoveHandler;
import com.vaadin.pointerevents.client.PointerUpEvent;
import com.vaadin.pointerevents.client.PointerUpHandler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.Letter;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.expressies.repr.ContentMathML;
import fi.graphtoolgwt.client.FormuleComponentGWT.GraphtFormuleEditor;
import fi.graphtoolgwt.client.text.Text;
import fi.graphtoolgwt.client.ui.ResizePanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GraphToolGWT implements EntryPoint, InteractionStub, FacetAware, CBookEventListener {
	
	private static Logger logger = Logger.getLogger("GraphToolGWT");
	
	public static final String ACTION_CORRECT = "action.correct";
	public static final String ACTION_FALSE = "action.false";
	public static final String ACTION_FALSE2 = "action.false_2";
	private static final String LOG_OPTION = "logOption";

	private static final CBookEvent EVENT_CORRECT = new CBookEvent(ACTION_CORRECT); 
	private static final CBookEvent EVENT_FALSE = new CBookEvent(ACTION_FALSE); 
	private static final CBookEvent EVENT_FALSE2 = new CBookEvent(ACTION_FALSE2); 
	
    private int errorCount;
    private boolean changed = false;
    private int foutStraf = 2;
	private boolean logOption, attempt;

    boolean moveActionActivated = false; // used to detect when the system is in move_mode
	final static int cSelectMarge = 5;
	final static int cPointRadius = 3;
	final static CssColor cColorOrange = CssColor.make(255, 193, 0);
	final static CssColor cColorRed = CssColor.make(255, 0, 0);
	final static CssColor cColorGreen = CssColor.make(0, 200, 0); 
	
	final static boolean cDefault_tekenComponentAan = false;

	public static Text rb = GWT.create(Text.class);

	static final String holderId = "dockholder";
	static final String upgradeMessage = 
			"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	OpdrNavIF comRoot;
	
	final double asDefaultXMin=-8, asDefaultXMax=10, asDefaultXStap=2;
	final double asDefaultYMin=-7, asDefaultYMax=2, asDefaultYStap=2;
	
	//UI
	LayoutPanel basisPanel = new LayoutPanel();
	//protected FormuleKeyboardIF kb = null;
	//DockLayoutPanel dlp; //misschien niet nodig?
	ResizePanel grafiekVeldPanel;
	GrafiekGWTVeld grafiekGWTVeld;
	Canvas grafiekGWTCanvas;
	
	TekenComponentGWT tekenComponent;
	TabelComponentGWT tabelComponent;
	FormuleComponentGWT formuleComponent;
	VeldComponentGWT veldComponent;
	
	LayoutPanel zoomPanel;
	int zoomPanelHoogte = 23;
	private PushButton zoomInXButton, zoomUitXButton, zoomInYButton, zoomUitYButton, zoomInButton, zoomUitButton, zoomStandaardButton;
	int buttonSize = 20;
	int buttonOffset = 5;
	
	private ImageResource zoomInXResource, zoomUitXResource, zoomInYResource, zoomUitYResource, zoomInResource, zoomUitResource, zoomStandaardResource;
	private Image zoomInXImage, zoomUitXImage, zoomInYImage, zoomUitYImage, zoomInImage, zoomUitImage, zoomStandaardImage;
	
	int breedte = 300;
	int hoogte = 350;
	boolean volledigeBreedte = false;
	int tekenComponentHoogte = 24;
	int tabelComponentHoogte = 60;
	int offset = 5; //5;
	int grafiekVeldHoogte = hoogte - 2 * offset; //hoogte - 2 - 2 * offset;
	final int eenheid = 16;
	
	private static double[] DEFAULTDOMEIN = new double[] {Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY};
	
	private int maxAantalExpressies = 9;
	private int aantalFuncties = 0;
	
	Expressie[] functies = new Expressie[maxAantalExpressies];
	Expressie[][] veldFuncties = new Expressie[VeldComponentGWT.cMaxAantalStelsels][VeldComponentGWT.cAantalFormulesPerStelsel];

	Expressie[] ongelijkheden = new Expressie[maxAantalExpressies];
	Expressie[] verticaleLijnen = new Expressie[maxAantalExpressies];
	
	boolean[] isY = new boolean[maxAantalExpressies];
	boolean[] isGroterGelijk = new boolean[maxAantalExpressies];
	boolean[] isEn = new boolean[maxAantalExpressies];
	boolean[] inclusiefGelijkheid = new boolean[maxAantalExpressies];
	double[][] domeinen = new double[][]
			{{DEFAULTDOMEIN[0], DEFAULTDOMEIN[1]}, 
			{DEFAULTDOMEIN[0], DEFAULTDOMEIN[1]}, 
			{DEFAULTDOMEIN[0], DEFAULTDOMEIN[1]},
			{DEFAULTDOMEIN[0], DEFAULTDOMEIN[1]}, 
			{DEFAULTDOMEIN[0], DEFAULTDOMEIN[1]},
			{DEFAULTDOMEIN[0], DEFAULTDOMEIN[1]}, 
			{DEFAULTDOMEIN[0], DEFAULTDOMEIN[1]},
			{DEFAULTDOMEIN[0], DEFAULTDOMEIN[1]},
			{DEFAULTDOMEIN[0], DEFAULTDOMEIN[1]}};
	
	//int buttonSize = 20;
	//int buttonOffset = 2;
	
	int startxv = 0;
	int startyv = 0;
	RealPoint dragPoint = null;
	RealPoint otherPoint = null;
	
	private int beginwaarde = 0;
	private int selectnummer = 999; 
	
	int eenheidx = eenheid;
	int eenheidy = eenheid;
	double eenheidxD = eenheid;
	double eenheidyD = eenheid;
	int veldx = 0; // was: offset;
	int veldy = 30;
	//int veldb = breedte - 2 * offset;
	//int veldh = hoogte - veldy - 2 * offset;
	double docentSchaalFactorX = 1;
	double docentSchaalFactorY = 1;
	double schaalFactorX = 1;   
	double schaalFactorY = 1;
	int factorRijNummerX = 99;
	int factorRijNummerY = 99;
	//private ZoomDraad zoomDraad;
	
	String xAsNaam = "x";
	String yAsNaam = "y";
	String grafiekXAsNaam = "x";
	String grafiekYAsNaam = "y";
	TekstPopup tf;
	
	boolean assenZichtbaar = true;
	boolean roosterZichtbaar = true;
	boolean roosterGrof = false; 
	boolean roosterX = true;
	boolean roosterY = true;
	boolean schaalZichtbaar = true; 
	boolean schaalX = true;
	boolean schaalY = true;
	boolean piLijnenZichtbaar = false;
	boolean xPositief = false;
	boolean yPositief = false;
	boolean xAsLog = false;
	boolean yAsLog = false;
	boolean xVarEditable = false;
	boolean yVarEditable = false;
	boolean snapToGridPoints = false;
	boolean rechteVerbindingen = true;
	boolean krommeZonderExtrapolatie = true;
	boolean krommeMetExtrapolatie = true;
	boolean zoomInTabel = true;
	
	// manual Scaling variables
	boolean manualScalingX;
	boolean manualScalingY;
	double eenheidxValue;
	double eenheidyValue;
	double asDefXMin; 
	double asDefXMax;
	double asDefXStap;
	double asDefYMin;
	double asDefYMax;
	double asDefYStap;
	
	boolean zoomOptie = true; 
	boolean traceOptie = true; 
	boolean dragOptie = true;
	
	boolean grafiekKleuren = true;
	boolean puntenNagekeken = false;
	boolean kleurInstelbaar = true;
	boolean functieBeginZichtbaar = true;
	boolean functieBeginAanpasbaar = true;
	boolean formeleFuncties = true;
	boolean domeinInstelbaar = false;
	int formuleComponentHoogte = 120;
	int veldComponentHoogte = VeldComponentGWT.cDefault_hoogte;
	VeldComponentGWT.FieldGraphType veldGrafiekType = VeldComponentGWT.cDefault_grafiekType;
	VeldComponentGWT.FieldGraphArrowSizeMode veldPijlGrootteModus = VeldComponentGWT.cDefault_pijlGrootteModus;
	int veldPijlGroottePixels = VeldComponentGWT.cDefault_pijlGroottePixels;
	double veldPijlSchaalfactor = VeldComponentGWT.cDefault_pijlSchaalFactor;
	boolean veldLargerGridStartPoints = VeldComponentGWT.cDefault_largerGridStartPoints;

	
	boolean functieToegestaan = true;
	boolean ongelijkheidToegestaan = true;
	boolean implicieteFunctieToegestaan = false;
	boolean verticaleLijnToegestaan = true;
	boolean parametrisatieToegestaan = false;
	
	boolean formuleComponentAan = true;
	boolean veldComponentAan = false;
	boolean tekenComponentAan = cDefault_tekenComponentAan;
	
	//voor testen tabelcomponent: 
	//boolean tabelComponentAan = true;
	//standaard:
	boolean tabelComponentAan = false;
	//standaard:
	boolean tabelAlsTekenTool = false;
	//voor testen tekenComponent:
	//boolean tabelAlsTekenTool = true;
	
	double beginxDocent = breedte/2/eenheidx*eenheidx;
	double beginyDocent = grafiekVeldHoogte/2/eenheidy*eenheidy;
	double beginx = beginxDocent;
	double beginy = beginyDocent;
	
	String[] colors, opdrachtKleuren, gewoneKleuren;
	
	int tekenGrafiekNauwkeurigheid = 5;
	
	//private Map<String, Object> launchData;
	private Map<String, Object> launchState; //launchData?
	String[] randomVarNamen = null;
	//Map<String, Number> randomVarWaarden = null;
	HashMap randomVarWaarden = null;
	
	public static int maxGraphs = 3;
	private int numGraphs = maxGraphs;
	private int activeIndex = 1;
	Vector<RealPoint> graphPoints = new Vector<RealPoint>();
	boolean SeparateGraphPointColors = true;
	Vector<String> graphPointColors = new Vector<String>();
	SchuifParameterGWT[] schuifParameters = new SchuifParameterGWT[0];
	
	Vector<RealPoint> docentGraphPoints = new Vector<RealPoint>();
	//boolean docent = false;
	CssColor docentColor = CssColor.make(0, 0, 0);
	
	protected final static int GEENOPDRACHT = 0;
	protected final static int VINDFORMULEBIJGRAFIEK = 1;
	protected final static int VINDFORMULEBIJPUNTEN = 2;
	protected final static int TEKENPUNTENBIJFORMULE = 3;
	protected final static int TEKENTABELPUNTEN = 4;
	
	int typeOpdracht;
	private int scoreMax;
	private int[] maxScores;
	private int[] nauwkeurigheid;
	private int[] minimumPunten;
	private int kleinsteMinimum;
	private boolean leerlingZietTabel;
	private boolean domeinControleren;
	private Expressie[] docentFuncties;
	Expressie[] tekenDocentFuncties;
	double[][] docentDomeinen;
	String[][] docentDomeinStrings;
	private String[] docentFunctieStrings;
	
	int mode = OpdrNavIF.OEFENEN;		
	private boolean correct = false;
	private boolean fout = false;
	int score;
	boolean nagekeken = false;
	private boolean ingevuld = false;

	private boolean checkExternal = false;
	
	Button kijkNaButton; //PushButton?
	//Label groenVinkjeLabel, oranjeVinkjeLabel, kruisjeLabel; //nodig?
	LayoutPanel kijkNaPanel;
	int kijkNaPanelHoogte = 32;
	LayoutPanel feedbackPanel;
	Button feedbackCloseButton;
	Label feedbackTekst;
	//TekstArea feedbackTekst;
	//private Image feedbackBallonImage;
	//private JPanel mwFeedbackPanel;
	//private JButton feedbackCloseButton;
	
	GraphToolGWTClientBundle graphToolGWTClientBundle; 
	static GraphToolCssResource graphToolCss;
	ImageResource goedkrulResource, goedkrulHalfResource, foutkruisResource;//, feedbackBallonResource;
	Image goedkrulImage, goedkrulHalfImage, foutkruisImage;//, feedbackBallonImage;
	
	
	boolean kijkNaButtonZichtbaar = false;
	//URL imageURL;
	
	public static int READY = 0;
    public static int ONE_FINGER = 1;
    public static int TWO_FINGERS = 2;
    boolean zooming = false;
	
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int DIAGONAL = 2;
    
	//boolean mouseDown;
    //voor in timer:
   	double factorx = 1;
  	double factory = 1;
	
	private FacetHelper facet;
	private boolean fromuser;

	private int init_width;

	private int init_height;

	private Map<String, Object> init_map;

	private Map<String, Number> init_rondomValues;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		Widget kbp = null;

		//initialize();
		
// insert mock keyboard
// Hier uitsluitend als er nog geen Keyboard interface klaar is.
//		//FormuleKeyboard mockkb = new FormuleKeyboard();
//		FakeFormuleKeyboard mockkb = new FakeFormuleKeyboard();
//		FocusOnTouch.installKeyboard(mockkb);
//		FormuleHolder.installKeyboard(mockkb);
//		kbp = mockkb.asWidget();
//		if(kbp != null)
//			basisPanel.add(kbp);
// einde		
		
//		RootLayoutPanel root = RootLayoutPanel.get();
//        root.add(basisPanel); // was basisPanel
//        root.setWidgetTopBottom(basisPanel, 0, Unit.PX, 0, Unit.PX);
//        root.setWidgetLeftRight(basisPanel, 0, Unit.PX, 0, Unit.PX);

		//RootLayoutPanel.get().add(this);
		basisPanel = RootLayoutPanel.get();
//		if (grafiekGWTCanvas == null) {
//		      RootPanel.get(holderId).add(new Label(upgradeMessage));
//		      return;
//		}

		//plaatsComponenten();
		Stub.publish(this);
		// initialize(); (NPE!)

		/* Om testen mogelijk te maken mischien
		  if( publish(this) ) initialize_myself() 
		  m.a.v. publish returned true als die faalt
		 */
		
	}
/**
 * Roep deze methode aan na init(), niet ervoor.
 */
	private void initialize( )
	{
		grafiekVeldHoogte = hoogte  + 1 -  offset; //hoogte - 2 - 2 * offset;
		if(zoomOptie)
			grafiekVeldHoogte -= zoomPanelHoogte + offset;
		if(tekenComponentAan)
			grafiekVeldHoogte -= tekenComponentHoogte + offset;
		if(tabelComponentAan)
			grafiekVeldHoogte -= tabelComponentHoogte + offset;
		if(formuleComponentAan)
			grafiekVeldHoogte -= formuleComponentHoogte + offset;
		if(veldComponentAan)
			grafiekVeldHoogte -= veldComponentHoogte + offset;
		
		if((typeOpdracht == 3 || typeOpdracht == 4) && mode != OpdrNavIF.ZELFTOETS && mode != OpdrNavIF.EINDTOETS && !checkExternal) 
		{	grafiekVeldHoogte -= kijkNaPanelHoogte + offset;
		}

		if (grafiekVeldHoogte < 1) grafiekVeldHoogte = 1; // Minimum 1 pixel!
		
		getImages();
		
		for(int i = 0; i < maxAantalExpressies; i++)
		{	isY[i] = true;
			isGroterGelijk[i] = true;
			if(i < maxAantalExpressies - 1)
				isEn[i] = true;
		}
		
		if(colors == null)//als init niet is aangeroepen, voor standalone-versie
			maakStandaardKleuren();
		else
		{	for(int i = 0; i < colors.length; i++)
			{	colors[i] = gewoneKleuren[i];
			
			}
		}
		
		zoomPanel = new LayoutPanel();
		zoomPanel.setPixelSize(breedte, zoomPanelHoogte);
		zoomPanel.getElement().getStyle().setBackgroundColor(CssColor.make(239,240,241).toString());
		zoomPanel.getElement().getStyle().setBorderColor(CssColor.make(211, 211, 211).toString());
		zoomPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		zoomPanel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);

		int basisX = 0;
		if(zoomOptie)
		{	basisPanel.add(zoomPanel);
		    basisPanel.setWidgetTopHeight(zoomPanel, basisX, Unit.PX, zoomPanelHoogte, Unit.PX);
		    basisX += zoomPanelHoogte + offset;		
		}
		int currentX = offset;
		
		zoomStandaardButton = new PushButton(zoomStandaardImage);
		zoomStandaardButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		zoomPanel.add(zoomStandaardButton);
		zoomPanel.setWidgetLeftWidth(zoomStandaardButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		zoomPanel.setWidgetTopHeight(zoomStandaardButton, 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
		zoomStandaardButton.addClickHandler(new PushClickHandler());
		currentX += buttonSize + 2*buttonOffset;
		
		zoomInButton = new PushButton(zoomInImage);
		zoomInButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		zoomPanel.add(zoomInButton);
		zoomPanel.setWidgetLeftWidth(zoomInButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		zoomPanel.setWidgetTopHeight(zoomInButton, 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
		zoomInButton.addClickHandler(new PushClickHandler());
		currentX += buttonSize + 2*buttonOffset;
		
		zoomUitButton = new PushButton(zoomUitImage);
		zoomUitButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		zoomPanel.add(zoomUitButton);
		zoomPanel.setWidgetLeftWidth(zoomUitButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		zoomPanel.setWidgetTopHeight(zoomUitButton, 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
		zoomUitButton.addClickHandler(new PushClickHandler());
		currentX += buttonSize + 2*buttonOffset;
		
		zoomInXButton = new PushButton(zoomInXImage);
		zoomInXButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		zoomPanel.add(zoomInXButton);
		zoomPanel.setWidgetLeftWidth(zoomInXButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		zoomPanel.setWidgetTopHeight(zoomInXButton, 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
		zoomInXButton.addClickHandler(new PushClickHandler());
		currentX += buttonSize + 2*buttonOffset;
		
		zoomUitXButton = new PushButton(zoomUitXImage);
		zoomUitXButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		zoomPanel.add(zoomUitXButton);
		zoomPanel.setWidgetLeftWidth(zoomUitXButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		zoomPanel.setWidgetTopHeight(zoomUitXButton, 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
		zoomUitXButton.addClickHandler(new PushClickHandler());
		currentX += buttonSize + 2*buttonOffset;
		
		zoomInYButton = new PushButton(zoomInYImage);
		zoomInYButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		zoomPanel.add(zoomInYButton);
		zoomPanel.setWidgetLeftWidth(zoomInYButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		zoomPanel.setWidgetTopHeight(zoomInYButton, 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
		zoomInYButton.addClickHandler(new PushClickHandler());
		currentX += buttonSize + 2*buttonOffset;
		
		zoomUitYButton = new PushButton(zoomUitYImage);
		zoomUitYButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		zoomPanel.add(zoomUitYButton);
		zoomPanel.setWidgetLeftWidth(zoomUitYButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		zoomPanel.setWidgetTopHeight(zoomUitYButton, 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
		zoomUitYButton.addClickHandler(new PushClickHandler());
		currentX += buttonSize + buttonOffset;
		zoomPanel.forceLayout();
		
		grafiekVeldPanel = new ResizePanel();
		grafiekVeldPanel.setPixelSize(breedte , grafiekVeldHoogte);
		
		grafiekGWTVeld = new GrafiekGWTVeld(this, breedte, grafiekVeldHoogte);
		grafiekGWTCanvas = grafiekGWTVeld.getCanvas();
		MouseHandler mouseHandler = new MouseHandler();
		TouchHandler touchHandler = new TouchHandler();
		others = HandlerRegistrations.compose(
		grafiekGWTCanvas.addMouseDownHandler(mouseHandler),
		grafiekGWTCanvas.addMouseMoveHandler(mouseHandler),
		grafiekGWTCanvas.addMouseUpHandler(mouseHandler),
		
		grafiekGWTCanvas.addTouchStartHandler(touchHandler),
		grafiekGWTCanvas.addTouchMoveHandler(touchHandler),
		grafiekGWTCanvas.addTouchEndHandler(touchHandler));
		
		PointerHandler pointerHandler = new PointerHandler();
		grafiekGWTCanvas.addDomHandler(pointerHandler, PointerDownEvent.getType());
		grafiekGWTCanvas.addDomHandler(pointerHandler, PointerUpEvent.getType());
		grafiekGWTCanvas.addDomHandler(pointerHandler, PointerMoveEvent.getType());
// met de mouse out of canvas, cancel drags
		mouseHandler.pointer = pointerHandler;
		grafiekGWTCanvas.addMouseOutHandler(mouseHandler);
		grafiekGWTCanvas.addDomHandler(pointerHandler, PointerCancelEvent.getType());
			
		grafiekGWTVeld.initContext2d();		
		
		grafiekVeldPanel.add(grafiekGWTVeld);
		grafiekVeldPanel.setWidgetLeftWidth(grafiekGWTVeld, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		grafiekVeldPanel.setWidgetTopHeight(grafiekGWTVeld, 0, Style.Unit.PX, grafiekVeldHoogte, Style.Unit.PX);
		grafiekVeldPanel.forceLayout();
		
		basisPanel.add(grafiekVeldPanel);
		basisPanel.setWidgetTopHeight(grafiekVeldPanel, basisX, Unit.PX, grafiekVeldHoogte, Unit.PX);
		basisX += grafiekVeldHoogte;

		tekenComponent = new TekenComponentGWT(this, breedte);
		tekenComponent.setPixelSize(breedte, tekenComponentHoogte);
		
		tekenComponent.zetGrafiekComponent(grafiekGWTVeld);
		if(tekenComponentAan)
		{
//		    FlowPanel panel = new FlowPanel();
//			panel.setPixelSize(breedte,offset);
//			basisPanel.add(panel);
		    basisX += offset;
			tekenComponent.forceLayout();
			basisPanel.add(tekenComponent);
			basisPanel.setWidgetTopHeight(tekenComponent, basisX, Unit.PX, tekenComponentHoogte, Unit.PX);
			basisX += tekenComponentHoogte;
		}
		tekenComponent.zetLijnenKnoppen(rechteVerbindingen, krommeZonderExtrapolatie, krommeMetExtrapolatie);
		
		tabelComponent = new TabelComponentGWT(this, breedte);
		tabelComponent.setPixelSize(breedte, tabelComponentHoogte);
		tabelComponent.zetGrafiekComponent(grafiekGWTVeld);
		if(tabelComponentAan)
		{
//		    FlowPanel panel = new FlowPanel();
//			panel.setPixelSize(breedte , offset);
//			basisPanel.add(panel);
		    basisX += offset;
			tabelComponent.forceLayout();
			basisPanel.add(tabelComponent);
			basisPanel.setWidgetTopHeight(tabelComponent, basisX, Unit.PX, tabelComponentHoogte, Unit.PX);
			basisX += tabelComponentHoogte;
		}
		tabelComponent.zetZooming(zoomInTabel);
		tabelComponent.zetXAsNaam(xAsNaam);
		tabelComponent.zetYAsNaam(yAsNaam, true);
		
		formuleComponent = new FormuleComponentGWT(this, launchState, breedte, formuleComponentHoogte);
		formuleComponent.setPixelSize(breedte, (formuleComponentHoogte - 2 - offset)); // waarom minder hoog dan in constructor?
		
		veldComponent = new VeldComponentGWT(this, launchState, breedte, veldComponentHoogte);
		
		formuleComponent.zetGrafiekComponent(grafiekGWTVeld);
		if(typeOpdracht == VINDFORMULEBIJGRAFIEK || typeOpdracht == VINDFORMULEBIJPUNTEN)
			formuleComponent.alsOpdracht = true;
		if(formuleComponentAan) {	
//			FlowPanel panel = new FlowPanel();
//			panel.setPixelSize(breedte, offset);
//			basisPanel.add(panel);
		    basisX += offset;
            formuleComponent.forceLayout();
			basisPanel.add(formuleComponent);
			basisPanel.setWidgetTopHeight(formuleComponent, basisX, Unit.PX, formuleComponentHoogte, Unit.PX);
			basisX += formuleComponentHoogte;
		}
		
		if (veldComponentAan) {
//			FlowPanel panel = new FlowPanel();
//			panel.setPixelSize(breedte, offset);
//			basisPanel.add(panel);
		    basisX += offset;
			veldComponent.forceLayout();
			basisPanel.add(veldComponent);
			basisPanel.setWidgetTopHeight(veldComponent, basisX, Unit.PX, veldComponentHoogte, Unit.PX);
			basisX += veldComponentHoogte;
			//RPJ
			zetVectorVeld(0, 0, FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$fax+y@"))));
			zetVectorVeld(0, 1, FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$fay-x@"))));		
		}
		
		grafiekGWTVeld.initContext2d();
		
		grafiekGWTVeld.setState(launchState); //lijkt me niet nodig.
		tekenComponent.setState(launchState);
		formuleComponent.setState(launchState, randomVarNamen, randomVarWaarden);
		veldComponent.setState(launchState);
		
		// Initialiseer the schaal parameters in geval van manual scaling 
    	if (manualScalingX || manualScalingY) {
    		zetAssenDefinitie(asDefXMin, asDefXMax, asDefXStap, asDefYMin, asDefYMax, asDefYStap);
    	}
		
		tabelComponent.setState(launchState);
		if(tabelAlsTekenTool)
		{	tabelComponent.zetAlsTekenTool(true, tekenComponentAan);
			tabelComponent.zetTabelPunten(getPoints(activeIndex, false), true);
		}
		
		int nakijkButtonWidth = 75;
		kijkNaButton = new Button(GraphToolGWT.rb.kijkNaButton());
		kijkNaButton.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		kijkNaButton.getElement().getStyle().setFontSize(12, Style.Unit.PX);
		kijkNaButton.setSize(nakijkButtonWidth + "px", 24 + "px");
		kijkNaButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{	kijkNa();
				setAttempt();
				grafiekGWTVeld.paint();
			}
		});
		kijkNaButton.setEnabled(false);
		
		
		
		kijkNaPanel = new LayoutPanel();
		kijkNaPanel.setSize(breedte + "px", kijkNaPanelHoogte + "px");
		kijkNaPanel.add(kijkNaButton);
		kijkNaPanel.setWidgetLeftWidth(kijkNaButton, 0, Style.Unit.PX, nakijkButtonWidth, Style.Unit.PX);
		kijkNaPanel.setWidgetTopHeight(kijkNaButton, 0, Style.Unit.PX, 24, Style.Unit.PX);
		
		goedkrulImage.setVisible(false);
		goedkrulHalfImage.setVisible(false);
		foutkruisImage.setVisible(false);
		
		feedbackPanel = new LayoutPanel();
		feedbackPanel.getElement().getStyle().setBackgroundColor(CssColor.make(255, 255, 200).toString());
		feedbackPanel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		feedbackPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		feedbackPanel.getElement().getStyle().setBorderColor(CssColor.make(0, 0, 0).toString());
		
		feedbackTekst = new Label();
		feedbackPanel.add(feedbackTekst);
		feedbackPanel.setWidgetLeftRight(feedbackTekst, 5, Style.Unit.PX, 15, Style.Unit.PX);
		
		feedbackCloseButton = new Button("x");
		feedbackCloseButton.getElement().getStyle().setBackgroundColor(CssColor.make(255, 255, 200).toString());
		feedbackCloseButton.getElement().getStyle().setPadding(0, Style.Unit.PX);
		feedbackCloseButton.getElement().getStyle().setPaddingTop(-5, Style.Unit.PX);
		feedbackCloseButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		feedbackCloseButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{	feedbackPanel.removeFromParent();;
			
			}
		});
		feedbackPanel.add(feedbackCloseButton);
		feedbackPanel.setWidgetRightWidth(feedbackCloseButton, 1, Style.Unit.PX, 15, Style.Unit.PX);
		feedbackPanel.setWidgetTopHeight(feedbackCloseButton, 0, Style.Unit.PX, 15, Style.Unit.PX);
		
		if((typeOpdracht == 3 || typeOpdracht == 4))
		{	if(checkExternal || mode == OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS)
			{
				grafiekVeldPanel.add(goedkrulImage);
				grafiekVeldPanel.add(goedkrulHalfImage);
				grafiekVeldPanel.add(foutkruisImage);
				
				grafiekVeldPanel.setWidgetRightWidth(goedkrulImage, 5, Style.Unit.PX, buttonSize, Style.Unit.PX);
				grafiekVeldPanel.setWidgetBottomHeight(goedkrulImage, 5, Style.Unit.PX, buttonSize + 4, Style.Unit.PX);
				grafiekVeldPanel.setWidgetRightWidth(goedkrulHalfImage, 5, Style.Unit.PX, buttonSize, Style.Unit.PX);
				grafiekVeldPanel.setWidgetBottomHeight(goedkrulHalfImage, 5, Style.Unit.PX, buttonSize + 4, Style.Unit.PX);
				grafiekVeldPanel.setWidgetRightWidth(foutkruisImage, 5, Style.Unit.PX, buttonSize, Style.Unit.PX);
				grafiekVeldPanel.setWidgetBottomHeight(foutkruisImage, 5, Style.Unit.PX, buttonSize + 4, Style.Unit.PX);
			
			}
			else
			{	kijkNaPanel.add(goedkrulImage);
				kijkNaPanel.add(goedkrulHalfImage);
				kijkNaPanel.add(foutkruisImage);
				kijkNaPanel.setWidgetLeftWidth(goedkrulImage, nakijkButtonWidth, Style.Unit.PX, buttonSize, Style.Unit.PX);
				kijkNaPanel.setWidgetTopHeight(goedkrulImage, 0, Style.Unit.PX, buttonSize + 4, Style.Unit.PX);
				kijkNaPanel.setWidgetLeftWidth(goedkrulHalfImage, nakijkButtonWidth, Style.Unit.PX, buttonSize, Style.Unit.PX);
				kijkNaPanel.setWidgetTopHeight(goedkrulHalfImage, 0, Style.Unit.PX, buttonSize + 4, Style.Unit.PX);
				kijkNaPanel.setWidgetLeftWidth(foutkruisImage, nakijkButtonWidth, Style.Unit.PX, buttonSize, Style.Unit.PX);
				kijkNaPanel.setWidgetTopHeight(foutkruisImage, 0, Style.Unit.PX, buttonSize + 4, Style.Unit.PX);
				
//				FlowPanel panel = new FlowPanel();
//				panel.setSize(breedte + "px", offset + "px");
//				basisPanel.add(panel);
				basisX += offset;
				basisPanel.add(kijkNaPanel);
				basisPanel.setWidgetTopHeight(kijkNaPanel, basisX, Unit.PX, kijkNaPanelHoogte, Unit.PX);
				basisX +=  kijkNaPanelHoogte;
			}
		}
		
		
		//Opdrachtinstellingen
		if(typeOpdracht == VINDFORMULEBIJGRAFIEK)
		{	tekenDocentFuncties = docentFuncties;
			formuleComponent.zetAantalRegels(Math.max(1, aantalFuncties), true);
			formuleComponent.zetMaxAantalFormules(Math.max(1, aantalFuncties), true);
		}
		else if(typeOpdracht == VINDFORMULEBIJPUNTEN)
		{
			tekenDocentFuncties = docentFuncties;
			formuleComponent.zetAantalRegels(1, true);
			formuleComponent.zetMaxAantalFormules(1, true);
		}
		else if(typeOpdracht == TEKENPUNTENBIJFORMULE)
		{
			tekenComponent.zetAantalGrafieken(aantalFuncties);
			tekenComponent.setConnectMode(tekenComponent.NONE);
			tekenComponent.zetLijnenKnoppen(rechteVerbindingen, krommeZonderExtrapolatie, krommeMetExtrapolatie); 
		}
		else if(typeOpdracht == TEKENTABELPUNTEN)
		{
			//zetTabelComponent(leerlingZietTabel);
// dit stond er (23-1-2014)
//			tekenComponent.zetAantalGrafieken(1);
//			tekenComponent.zetLijnenKnoppen(false, false, false);

// dit staat in GraphToolInteractiePanel op deze plaats			
			tekenComponent.zetAantalGrafieken(1);
			tekenComponent.setConnectMode(TekenComponentGWT.NONE);
			tekenComponent.zetLijnenKnoppen(rechteVerbindingen, krommeZonderExtrapolatie, krommeMetExtrapolatie);

			
			tabelComponent.zetEenTabel(true);
			tabelComponent.zetTabelPunten(docentGraphPoints, true);
			tabelComponent.zetReset(false);
		}
		
		zetOpdrachtKleuren(typeOpdracht != GEENOPDRACHT);
		//weet nog niet of nodig, voor de zekerheid:
		
		setActiveIndex(activeIndex, true);
        grafiekGWTVeld.paint();
        basisPanel.forceLayout();

// XXX Hogere school magie: voor Safari force refresh met 'bottom' toggle        
        
        Scheduler.get().scheduleDeferred(() ->  {
		  Style style = grafiekGWTCanvas.getElement().getStyle();
		  style.clearBottom();
		  Scheduler.get().scheduleDeferred(() -> style.setBottom(0, Unit.PX));
//		  basisPanel.setWidgetLeftRight(grafiekVeldPanel, 0, Unit.EM, 0, Unit.EM);
//		  basisPanel.animate(10);
		})
		;
		
		
		
	}
	
	private void getImages() 
	{
		graphToolGWTClientBundle = GWT.create(GraphToolGWTClientBundle.class);
		graphToolCss = graphToolGWTClientBundle.getGraphToolGWTCSS();
		graphToolCss.ensureInjected();
		
		goedkrulResource = graphToolGWTClientBundle.goedkrulResource();
		goedkrulHalfResource = graphToolGWTClientBundle.goedkrulHalfResource();
		foutkruisResource = graphToolGWTClientBundle.foutkruisResource();

		goedkrulImage = new Image(goedkrulResource.getSafeUri());
		goedkrulHalfImage = new Image(goedkrulHalfResource.getSafeUri());
		foutkruisImage = new Image(foutkruisResource.getSafeUri());
		
		zoomInResource = graphToolGWTClientBundle.zoomInButtonResource();
		zoomUitResource = graphToolGWTClientBundle.zoomUitButtonResource();
		zoomInXResource =  graphToolGWTClientBundle.zoomInXResource();
		zoomUitXResource = graphToolGWTClientBundle.zoomUitXResource();
		zoomInYResource = graphToolGWTClientBundle.zoomInYResource();
		zoomUitYResource = graphToolGWTClientBundle.zoomUitYResource();
		zoomStandaardResource = graphToolGWTClientBundle.zoomStandaardResource();
		
		zoomInImage = new Image(zoomInResource.getSafeUri());
		zoomUitImage=  new Image(zoomUitResource.getSafeUri());
		zoomInXImage = new Image(zoomInXResource.getSafeUri());
		zoomUitXImage=  new Image(zoomUitXResource.getSafeUri());
		zoomInYImage = new Image(zoomInYResource.getSafeUri());
		zoomUitYImage=  new Image(zoomUitYResource.getSafeUri());
		zoomStandaardImage = new Image(zoomStandaardResource.getSafeUri());
		
		//toevoegen stylenames, om marges goed te zetten.
		zoomInImage.addStyleName(graphToolCss.pushimage());
		zoomUitImage.addStyleName(graphToolCss.pushimage());
		zoomInXImage.addStyleName(graphToolCss.pushimage());
		zoomUitXImage.addStyleName(graphToolCss.pushimage());
		zoomInYImage.addStyleName(graphToolCss.pushimage());
		zoomUitYImage.addStyleName(graphToolCss.pushimage());
		zoomStandaardImage.addStyleName(graphToolCss.pushimage());
		
	}
	
	public GraphToolGWT()
	{
		
	}
	
	public void updateXAsNaam(String text)
	{	String[] forbiddenStrings = {"sin","cos","tan","ln","log"};
		if(text.length()>0 && !Letter.isLetter(text.charAt(0)))
		{	//JOptionPane.showMessageDialog(WiskOpdr.applet, WiskOpdr.rb.getString("xVarMessage1"));
			DialogBox box = new DialogBox();
			box.setText(rb.xVarMessage1());
			box.show();
			updateX(text.substring(1),true);
		}
		else if(text.length()>1 && !FormuleParser.isWoordFormule())
		{	//JOptionPane.showMessageDialog(WiskOpdr.applet, WiskOpdr.rb.getString("xVarMessage2"));
			DialogBox box = new DialogBox();
			box.setText(rb.xVarMessage2());
			box.show();
			updateX(text.substring(0,1),true);
		}
		else if(text.length()==1 && text.charAt(0)=='e')
		{	//JOptionPane.showMessageDialog(WiskOpdr.applet, WiskOpdr.rb.getString("xVarMessage3"));
			DialogBox box = new DialogBox();
			box.setText(rb.xVarMessage3());
			box.show();
			
			if(FormuleParser.isWoordFormule())
				updateX(grafiekXAsNaam, false);
			else 
				updateX(text.substring(1),true);
		}
		else if(text.length()!=0)
		{	boolean forbidden = false;
			for(int i=0 ; i<forbiddenStrings.length ; i++)
			{	if(text.indexOf(forbiddenStrings[i])>-1)
				{	DialogBox box = new DialogBox();
					box.setText(rb.xVarMessage4a() + forbiddenStrings[i] + rb.xVarMessage4b());
					box.show();
					//DialogBox.showMessageDialog(WiskOpdr.applet, WiskOpdr.rb.getString("xVarMessage4a") + forbiddenStrings[i] + WiskOpdr.rb.getString("xVarMessage4b"));
					if(text.length()>0)
						updateX(text.substring(0,text.indexOf(forbiddenStrings[i])),true);
					forbidden = true;
				}
			}
			if(!forbidden)
			{	updateX(text,true);
			}
		}
		if(text.length()==0) 
			updateX(grafiekXAsNaam, false);
	}
	
	public void updateX(String s, boolean updateTF)
	{	String oldXAsNaam = grafiekXAsNaam;
		grafiekXAsNaam = s;
		if(updateTF) 
			//xAsNaamTF.setText(grafiekXAsNaam);
			tf.setText(grafiekXAsNaam);//nodig?
		if(grafiekXAsNaam.equals(""))
			grafiekXAsNaam = oldXAsNaam;
		//grafiekGWTVeld.setXAsNaam(grafiekXAsNaam);
		grafiekGWTVeld.paint();		
		//xAsNaamTF.requestFocus();
		veldComponent.updateAxisName(oldXAsNaam, grafiekXAsNaam);
	}
	
	public void updateYAsNaam(String text)
	{	String oldYAsNaam = grafiekYAsNaam;	
		grafiekYAsNaam = text;
		tf.setText(grafiekYAsNaam);//nodig?
		//yAsNaamTF.setText(grafiekYAsNaam);
		if(grafiekYAsNaam.equals(""))grafiekYAsNaam = oldYAsNaam;
		//grafiekGWTVeld.setYAsNaam(grafiekYAsNaam);
		grafiekGWTVeld.paint();
		veldComponent.updateAxisName(oldYAsNaam, grafiekYAsNaam);
		//repaint();
		
		//yAsNaamTF.requestFocus();
	}
	
	public void showTekstPopup(boolean isX)
	{
		//int popupX = xPos + 10 + inputOwner.getAbsoluteLeft();
		//int popupY = yPos + hoogte + inputOwner.getAbsoluteTop();
		
		
		/*if ((tf != null) && tf.isVisible())
		{
			zetInvulWaarde();
		}
		*/

		tf = new TekstPopup(isX);
		tf.zetInteractiePanel(this);
		tf.setText(isX?grafiekXAsNaam:grafiekYAsNaam);
		tf.setWidth("25px");
		tf.setHeight("20px");
		//tf.setModal(true);
		if(isX)
		{	int linkerGrens = grafiekGWTVeld.getCanvas().getAbsoluteLeft() + grafiekGWTVeld.getCanvas().getOffsetWidth() - 25;
			if(grafiekGWTVeld.xAsNaamLinks < grafiekGWTVeld.getCanvas().getOffsetWidth() - 25)
				linkerGrens = grafiekGWTVeld.getCanvas().getAbsoluteLeft() + grafiekGWTVeld.xAsNaamLinks;
			tf.setPopupPosition(linkerGrens, grafiekGWTVeld.getCanvas().getAbsoluteTop() + grafiekGWTVeld.xAsNaamOnder);
		}
		else
		{	int linkerGrens = grafiekGWTVeld.getCanvas().getAbsoluteLeft() + grafiekGWTVeld.getCanvas().getOffsetWidth() - 25;
			if(grafiekGWTVeld.yAsNaamLinks < grafiekGWTVeld.getCanvas().getOffsetWidth() - 25)
				linkerGrens = grafiekGWTVeld.getCanvas().getAbsoluteLeft() + grafiekGWTVeld.yAsNaamLinks;
			tf.setPopupPosition(linkerGrens, grafiekGWTVeld.getCanvas().getAbsoluteTop() + grafiekGWTVeld.yAsNaamOnder);
		}
		tf.show();
		tf.textBox.setFocus(true);

	}
	
	/*
	public void zetXAsNaam(String s, boolean setState)
	{	xAsNaam = s;
		grafiekXAsNaam = s;
		//xAsNaamTF.setText(s);
		formuleComponent.zetXAsNaam(s, setState);
		tabelComponent.zetXAsNaam(s);
		grafiekGWTVeld.paint();
	}
	
	public void zetYAsNaam(String s, boolean setState)
	{	yAsNaam = s;
		grafiekYAsNaam = s;
		//yAsNaamTF.setText(s);
		formuleComponent.zetYAsNaam(s, setState);
		tabelComponent.zetYAsNaam(s, true);
		grafiekGWTVeld.paint();
	}
	*/

	
	public Vector<RealPoint> getPoints(int index, boolean docent) {	
		Vector<RealPoint> points = new Vector<RealPoint>();
		Vector<RealPoint> vector = docent?docentGraphPoints:graphPoints;
		for (int pCnt = 0; pCnt < vector.size(); pCnt++)
		{	RealPoint rp = vector.elementAt(pCnt);
			if (rp.getIndex() == index)
				points.addElement(rp);
		}
		return points;
	}
	
	public Vector<String> getColors(int index) {
		Vector<String> colors = new Vector<String>();
		
		for (int pCnt = 0; pCnt < graphPoints.size(); pCnt++) {	
			RealPoint rp = graphPoints.elementAt(pCnt);
			if (rp.getIndex() == index) {
				colors.addElement(graphPointColors.elementAt(pCnt));
			}
		}
		return colors;
	}
	
	public int addInsert(RealPoint newRP, boolean checkVisibleBounds)  { //, boolean docent) 	
		if ( checkVisibleBounds && (!grafiekGWTVeld.valuePointWithinBounds(newRP.getX(), newRP.getY()))) {
			return (-1);
		}

		int pIndex = -1;
		boolean firstFound = false;
		
		for(int pCnt = 0; pCnt < graphPoints.size(); pCnt++)
		{	RealPoint rp = graphPoints.elementAt(pCnt);
			if(!firstFound && rp.hasLargerXThen(newRP))
			{	pIndex = pCnt;
				firstFound = true;
			}
		}
		if(pIndex == -1) {
			graphPoints.addElement(newRP);
			graphPointColors.addElement(opdrachtKleuren[activeIndex - 1]);
		}
		else {
			graphPoints.insertElementAt(newRP, pIndex);
			graphPointColors.insertElementAt(opdrachtKleuren[activeIndex - 1], pIndex);
		}
		
		/*
		for (int pCnt = 0; pCnt < (docent?docentGraphPoints:graphPoints).size(); pCnt++)
		{	RealPoint rp = (RealPoint) (docent?docentGraphPoints:graphPoints).elementAt(pCnt);
			if (!firstFound && rp.hasLargerXThen(newRP))
			{	pIndex = pCnt;
				firstFound = true;
			}
		}
		if (pIndex == -1)
			(docent?docentGraphPoints:graphPoints).addElement(newRP);
		else
			(docent?docentGraphPoints:graphPoints).insertElementAt(newRP, pIndex);
			*/
		if(tabelAlsTekenTool)
			tabelComponent.zetTabelPunt(newRP);
		//repaint();
		//produceAction("points changed");
		pointsChangedAction();
		return pIndex;
	}
	
	public void removePoints(int index)//, boolean docent)
	{	for (int pCnt = graphPoints.size() - 1; pCnt > - 1; pCnt--)
		{	RealPoint rp = graphPoints.elementAt(pCnt);
			if (rp.getIndex() == index) {
				graphPoints.removeElementAt(pCnt);
				graphPointColors.removeElementAt(pCnt);
			}
		}

		/*
		for (int pCnt = (docent?docentGraphPoints:graphPoints).size() - 1; pCnt > - 1; pCnt--)
		{	RealPoint rp = (RealPoint) (docent?docentGraphPoints:graphPoints).elementAt(pCnt);
			if (rp.getIndex() == index)
				(docent?docentGraphPoints:graphPoints).removeElementAt(pCnt);
		}
		*/
		
		tabelComponent.reset();
		pointsChangedAction();
		//produceAction("points changed");
	}
	
	public void pointsChangedAction()
	{
		if(typeOpdracht == TEKENTABELPUNTEN)
		{
			setColor(0, CssColor.make(opdrachtKleuren[0]), false);	
			tabelComponent.zetTabelPunten(docentGraphPoints, false);
			puntenNagekeken = false;
			
			if(graphPoints.size() >= docentGraphPoints.size()) {	
				kijkNaButton.setEnabled(true);
			}
			else
			{	kijkNaButton.setEnabled(false);
				goedkrulImage.setVisible(false);
				goedkrulHalfImage.setVisible(false);
				foutkruisImage.setVisible(false);
				
				//repaint();
				score = 0;
				correct = false;
    			//produceAction("changed");
			}
			setComRootChanged(false);
		}
		else if (typeOpdracht == TEKENPUNTENBIJFORMULE) {	
			int kleinsteMinimum = minimumPunten[0];
	    	for(int i = 1; i < aantalFuncties; i++)
	    	{	if(minimumPunten[i] < kleinsteMinimum)
	    		{	kleinsteMinimum = minimumPunten[i];
	    		}
	    	}
			if(graphPoints.size() >= kleinsteMinimum)
			{	kijkNaButton.setEnabled(true);
			}
			else
			{	kijkNaButton.setEnabled(false);
				goedkrulImage.setVisible(false);
				goedkrulHalfImage.setVisible(false);
				foutkruisImage.setVisible(false);
				
				//repaint();
				score = 0;
				correct = false;
    			//produceAction("changed");
			}
			setComRootChanged(false);
		}
	}
	
	public void removePoint(int tabelindex, int index)//, boolean docent)
	{	for (int pCnt = graphPoints.size() - 1; pCnt > - 1; pCnt--)
		{	RealPoint rp = (RealPoint) graphPoints.elementAt(pCnt);
			if (rp.getIndex() == index && rp.getTabelIndex() == tabelindex){	
				graphPoints.removeElementAt(pCnt);
				graphPointColors.removeElementAt(pCnt);
				//produceAction("points changed");
				pointsChangedAction();
				break;
			}
		}
	
	/*
		for (int pCnt = (docent?docentGraphPoints:graphPoints).size() - 1; pCnt > - 1; pCnt--)
		{	RealPoint rp = (RealPoint) (docent?docentGraphPoints:graphPoints).elementAt(pCnt);
			if (rp.getIndex() == index && rp.getTabelIndex() == tabelindex)
			{	(docent?docentGraphPoints:graphPoints).removeElementAt(pCnt);
				//produceAction("points changed");
				break;
			}
		}
		*/
	}
		
	public boolean hasPointWithSameXAs(RealPoint aRp)//, boolean docent)
	{	boolean found = false;
		Vector rPoints = getPoints(aRp.getIndex(), false);//, docent);
		for (int rCnt = 0; rCnt < rPoints.size(); rCnt++)
		{	RealPoint rp = (RealPoint) rPoints.elementAt(rCnt);
			if (aRp.hasSameXAs(rp))
				found = true;
		}
		return found;
	}
	
	public void zetOpdrachtKleuren(boolean opdrachten)
	{
		if(opdrachten)
		{	for(int i = 0; i < colors.length; i++)
			{	colors[i] = opdrachtKleuren[i];
			
			}
		}
		else
		{	for(int i = 0; i < colors.length; i++)
			{	colors[i] = gewoneKleuren[i];
			}
		}
		formuleComponent.zetGrafiekKleuren();
	}
	
	public CssColor getFormuleColor(int index)
	{
		if(grafiekKleuren && typeOpdracht != GEENOPDRACHT)
			return CssColor.make(opdrachtKleuren[index]);
		else if(grafiekKleuren)
			return CssColor.make(gewoneKleuren[index]);
		else
			return CssColor.make(gewoneKleuren[0]);
	}
	
	public void setColor(int nr, CssColor c, boolean nakijken) {
		//31-8-2015: colors[nr] moet altijd deze kleur worden; bij nakijken wordt rood/groene feedback anders niet getoond.

		colors[nr] = c.value();
		if(typeOpdracht == GEENOPDRACHT)
		{	gewoneKleuren[nr] = c.value();
		}
		else if (!nakijken)
		{	opdrachtKleuren[nr] = c.value();
		}
			
	}
	
	public int getNumGraphs()
	{	return numGraphs;
	}
	
	public int getActiveIndex()
	{	return activeIndex;
	}
	
	public void setActiveIndex(int index, boolean setState)
	{	
		if ((index < 1) || (index > maxGraphs))
			activeIndex = 1;
		else
			activeIndex = index;
		
		tekenComponent.zetSelectedIndexGrKeuze(activeIndex - 1);
		if(tabelAlsTekenTool)
			tabelComponent.setActiveIndex(activeIndex, setState);
				
	}
	
	public Point realPointToPixels(RealPoint rp) {  
	if (Double.isNaN(rp.getX()) || Double.isNaN(rp.getY()))
			return null;
//pix.x = (int) Math.round(beginx + eenheidxD * (xAsLog?Math.log10(rp.x):rp.x) / schaalFactorX);
//pix.y = (int) Math.round(grafiekGWTVeld.hoogte -
//					(beginy + eenheidyD * (yAsLog?Math.log10(rp.y):rp.y) / schaalFactorY));
//	Point pix = new Point(Math.round(beginx + eenheidxD * (xAsLog?Math.log10(rp.getX()):rp.getX()) / schaalFactorX), Math.round(grafiekGWTVeld.hoogte -
//	(beginy + eenheidyD * (yAsLog?Math.log10(rp.getY()):rp.getY()) / schaalFactorY)));

		double pixX;
		double sourceX = rp.getX();
		if (manualScalingX) {
			pixX = Math.round(beginx + eenheidxD * (sourceX) / eenheidxValue);
		} else {
			if (xAsLog) {
				sourceX = Math.log10(sourceX);
			}
			pixX = Math.round(beginx + eenheidxD * (sourceX) / schaalFactorX);
		}
		
		double pixY;
		double sourceY = rp.getY();
		if (manualScalingY) {
			pixY = Math.round(grafiekGWTVeld.hoogte - (beginy + eenheidyD * (sourceY) / eenheidyValue) );
		} else {
			if (yAsLog) {
				sourceY = Math.log10(sourceY);
			}
			pixY = Math.round(grafiekGWTVeld.hoogte - (beginy + eenheidyD * (sourceY) / schaalFactorY) );
		}
		Point pix = new Point(pixX, pixY);
		return pix;
	}
	
	public RealPoint realPointToRealPixels(RealPoint rp) {
//		RealPoint realPix = new RealPoint(
//			beginx + eenheidxD * (xAsLog?Math.log10(rp.getX()):rp.getX()) / schaalFactorX,
//			grafiekGWTVeld.hoogte - (beginy + eenheidyD * (yAsLog?Math.log10(rp.getY()):rp.getY()) / schaalFactorY));

		double pixX;
		double sourceX = rp.getX();
		if (manualScalingX) {
			pixX = (beginx + eenheidxD * (sourceX) / eenheidxValue);
		} else {
			if (xAsLog) {
				sourceX = Math.log10(sourceX);
			}
			pixX = (beginx + eenheidxD * (sourceX) / schaalFactorX);
		}
		
		double pixY;
		double sourceY = rp.getY();
		if (manualScalingY) {
			pixY = (grafiekGWTVeld.hoogte - (beginy + eenheidyD * (sourceY) / eenheidyValue) );
		} else {
			if (yAsLog) {
				sourceY = Math.log10(sourceY);
			}
			pixY = (grafiekGWTVeld.hoogte - (beginy + eenheidyD * (sourceY) / schaalFactorY) );
		}
		
		RealPoint realPix = new RealPoint(pixX, pixY); 
		return realPix;
	}
	
	public int getScore() {
		return score;
	}

	public int[][] getScoreObjectives() {
		return null;
	}

	public int getScoreMax() {
		return scoreMax;
		
	}

	public Boolean isCorrect()
	{
		if (typeOpdracht == GEENOPDRACHT)
			return Boolean.TRUE;
		
		if (!correct && !fout) 
			return null;
		
		return correct;
	}

	public boolean isFout() {
		if(typeOpdracht == GEENOPDRACHT)
			return false;
		return fout;
	}

	public void zetMode(int mode) {
		this.mode = mode;
		//zetKijkNaButton(kijkNaButtonZichtbaar); Lijkt me niet nodig; verandert niet in de tijd dat de leerling met de activiteit bezig is.
		if((typeOpdracht == 3 || typeOpdracht == 4) && (mode == OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS) && !checkExternal)
		{
			kijkNaButton.setVisible(false);
			grafiekVeldHoogte += kijkNaPanelHoogte + offset;
			grafiekVeldPanel.setPixelSize(breedte , grafiekVeldHoogte);
			
			grafiekVeldPanel.add(goedkrulImage);
			grafiekVeldPanel.add(goedkrulHalfImage);
			grafiekVeldPanel.add(foutkruisImage);
			
			grafiekVeldPanel.setWidgetRightWidth(goedkrulImage, 5, Style.Unit.PX, buttonSize, Style.Unit.PX);
			grafiekVeldPanel.setWidgetBottomHeight(goedkrulImage, 5, Style.Unit.PX, buttonSize + 4, Style.Unit.PX);
			grafiekVeldPanel.setWidgetRightWidth(goedkrulHalfImage, 5, Style.Unit.PX, buttonSize, Style.Unit.PX);
			grafiekVeldPanel.setWidgetBottomHeight(goedkrulHalfImage, 5, Style.Unit.PX, buttonSize + 4, Style.Unit.PX);
			grafiekVeldPanel.setWidgetRightWidth(foutkruisImage, 5, Style.Unit.PX, buttonSize, Style.Unit.PX);
			grafiekVeldPanel.setWidgetBottomHeight(foutkruisImage, 5, Style.Unit.PX, buttonSize + 4, Style.Unit.PX);
			
			grafiekGWTVeld.setSize(breedte, grafiekVeldHoogte);
			grafiekVeldPanel.forceLayout();
			grafiekGWTVeld.paint();
		}
		
	}

	public void zetNagekeken(boolean b) {
		if (ingevuld) 
		{	boolean changed = !nagekeken;
			nagekeken = b;
			if (mode == OpdrNavIF.ZELFTOETS && b && changed) {
				kijkNa(); // feedback
			}
		}
	}

	public void stop() {
		if(mode != OpdrNavIF.OEFENEN_STRAFPUNTEN) {
			kijkNa(); 
		}
	}

	public void start() {
		
	}

	public void destroy() {
		
	}

	public void opnieuw() {
		
	}
	
	// voor FACET
	private String toString(GraphtFormuleEditor editor, Expressie expr) {
		StringBuffer sb = new StringBuffer("<math xmlns='http://www.w3.org/1998/Math/MathML'><semantics>");
		sb.append(editor.getMainRegel().toMathML());
		if(expr != null)
		{
			sb.append("<annotation-xml encoding='MathML-Content'>");
			sb.append(expr.visit(ContentMathML.INSTANCE));
			sb.append("</annotation-xml>");
		}
		sb.append("</semantics></math>");
		return sb.toString();
	}

	private String toString(RealPoint rp) {
		if(rp == null) return "";
		return "(x:"+rp.getxString() + ")(y:" + rp.getyString() +")";
	}
	
	private List<String> getResponse() {
		Vector<String> response = new Vector<String>();
		int aantal = aantalFuncties;
		int size = facet.getResponseTypes().size();
		boolean coordinate = size > 0 && Type.coordinate == facet.getResponseTypes().get(0);
		switch(typeOpdracht) {
		case VINDFORMULEBIJPUNTEN:
			aantal = 1;
		case VINDFORMULEBIJGRAFIEK:
			for (int i = 0; i < aantal; i++) {
				response.add(toString( formuleComponent.editors[i] , functies[i]));
			}
			break;
		case TEKENPUNTENBIJFORMULE:
			List<RealPoint> points = graphPoints;
			for(RealPoint point: points) {
				if(coordinate) response.add(toString(point));
				else {
					response.add(point.getxString());
					response.add(point.getyString());
				}
			}
			break;
		case TEKENTABELPUNTEN:
			points = getPoints(1, false);
			for(RealPoint point: points) {
				if(coordinate) response.add(toString(point));
				else {
					response.add(point.getxString());
					response.add(point.getyString());
				}
			}
			break;
		}
		while(response.size()<size) response.add("");
		response.setSize(size);
		return response;
	}
	
	private CssColor verwerkAsNaamBijNakijken(boolean show, CssColor color, boolean correctLogica) {
		CssColor returnColor = color;
		if(!grafiekXAsNaam.equals(xAsNaam) || !grafiekYAsNaam.equals(yAsNaam)) {
			if ( (score > 0) && (correctLogica) )
				returnColor = CssColor.make(255, 193, 0);
			score = Math.max(score - 2, 0);
			if (correctLogica) {	
				correct = false; 
				goedkrulImage.setVisible(false);
				goedkrulHalfImage.setVisible(show);
				if(show) {
					setFeedback(GraphToolGWT.rb.feedbackTekstLabelsAssen(),true);
				}
			}
		}
		return (returnColor);
		
//		if(!grafiekXAsNaam.equals(xAsNaam) || !grafiekYAsNaam.equals(yAsNaam))
//		{	if(score > 0)
//			color = CssColor.make(255, 193, 0);
//			score = Math.max(score - 2, 0);
//			if(correct || goedkrulHalfImage.isVisible() && puntenCorrect)
//			{	correct = false; 
//				goedkrulImage.setVisible(false);
//				goedkrulHalfImage.setVisible(show);
//				if(show)
//					setFeedback(GraphToolGWT.rb.getString("feedbackTekstLabelsAssen"),true);
//			}
//		}

	}
	
	private CssColor verwerkTekenModusBijNakijken(boolean show, CssColor color, boolean correctLogica) {
		CssColor returnColor = color;
		//	if((rechteVerbindingen || krommeMetExtrapolatie || krommeZonderExtrapolatie) && 
		//	tekenComponent.getConnectMode() != tekenComponent.CURVE_EXTRA && 
		//	tekenComponent.getConnectMode() != tekenComponent.CURVE &&
		//	tekenComponent.getConnectMode() != tekenComponent.LINES) {	
		if ( !( ( rechteVerbindingen && tekenComponent.getConnectMode() == tekenComponent.LINES) || 
				( krommeZonderExtrapolatie && tekenComponent.getConnectMode() == tekenComponent.CURVE) ||
				( krommeMetExtrapolatie && tekenComponent.getConnectMode() == tekenComponent.CURVE_EXTRA)
			  ) && (rechteVerbindingen || krommeZonderExtrapolatie || krommeMetExtrapolatie) // er moet wel een grafieklijn gevraagd worden anders zeggen we niets
		   ) {
			if ( (score > 0) && (correctLogica) )
				returnColor = CssColor.make(255, 193, 0);
			score = Math.max(score - 2, 0);
			if (correctLogica) {	
				correct = false;
				goedkrulImage.setVisible(false);
				goedkrulHalfImage.setVisible(show);
				if(show) {
					setFeedback(GraphToolGWT.rb.feedbackTekstTekenGrafiek(),true);
				}
			} 
		}
		return (returnColor);
		
//		if((rechteVerbindingen || krommeMetExtrapolatie || krommeZonderExtrapolatie) &&
//				tekenComponent.getConnectMode() != tekenComponent.CURVE_EXTRA && 
//				tekenComponent.getConnectMode() != tekenComponent.CURVE &&
//				tekenComponent.getConnectMode() != tekenComponent.LINES)
//		{	if(score > 0)
//				color = CssColor.make(255, 193, 0);
//			score = Math.max(score - 2, 0);
//			if(correct || goedkrulHalfImage.isVisible() && puntenCorrect)
//			{	correct = false;
//				goedkrulImage.setVisible(false);
//				goedkrulHalfImage.setVisible(show);
//				if(show)
//					setFeedback(GraphToolGWT.rb.getString("feedbackTekstTekenGrafiek"),true);
//			} 
//		}	
		
	}
	
	public void kijkNa(boolean show, boolean setState)
	{
		ingevuld = false;
		if (feedbackPanel != null)
			feedbackPanel.removeFromParent();
		if (typeOpdracht == VINDFORMULEBIJGRAFIEK)
		{
			if (functies != null)
			{
				boolean[] functieCorrect = new boolean[aantalFuncties];
				Expressie[] vgldocentFuncties = new Expressie[aantalFuncties];
				for (int i = 0; i < aantalFuncties; i++)
				{
					vgldocentFuncties[i] = docentFuncties[i];
				}
				CssColor color = CssColor.make(255, 0, 0);
				score = 0;
				correct = false;
				fout = false;
				for (int i = 0; i < functies.length; i++)
				{
					color = CssColor.make(255, 0, 0);
					if (functies[i] != null)
					{
						ingevuld = true;
	    				for (int j = 0; j < vgldocentFuncties.length; j++)
	    				{
	    					if (vgldocentFuncties[j] != null && Algebra.isGelijkwaardig(functies[i], vgldocentFuncties[j]))
							{
	    						if (domeinControleren && (domeinen[i][0] != docentDomeinen[i][0] || domeinen[i][1] != docentDomeinen[i][1]))
								{
	    							color = CssColor.make(255, 193, 0);
									score += maxScores[j]/2;
								}
								else
								{
									color = CssColor.make(0, 200, 0);
									score += maxScores[j];
									functieCorrect[j] = true;
									domeinen[i][0] = docentDomeinen[j][0];
									domeinen[i][1] = docentDomeinen[j][1];
								}	
								vgldocentFuncties[j] = null;
								break;
							}
	    				}
					}
					if (show)
						setColor(i, color, true);
					else
					;	//nog iets leegmaken, zodat niet wordt getekend?
						
				}
				for (int i = 0; i < functieCorrect.length; i++)
				{
					if (!functieCorrect[i])
					{
						fout = true;
						break;
					}
				}
				if (!fout)
					correct = true;
			}
		}
		else if (typeOpdracht == VINDFORMULEBIJPUNTEN)
		{	
			Expressie leerlingExp = functies[0];
			if (leerlingExp != null)
			{
				ingevuld = true;
				CssColor color = CssColor.make(255, 0, 0);
				score = 0;
				correct = false;
				fout = false;
				if (Algebra.isGelijkwaardig(leerlingExp, docentFuncties[0]))
				{
					color = CssColor.make(0, 200, 0);
					score = scoreMax;
					correct = true;
				}
				else
				{
					fout = true;
				}
				if (show)
					setColor(0, color, true);
				else
					;	//iets leegmaken, zodat niet getekend?
			}
		}
		else if (typeOpdracht == TEKENPUNTENBIJFORMULE)
		{
			int kleinsteMinimum = minimumPunten[0];
			for (int i = 1; i < aantalFuncties; i++)
			{
				if(minimumPunten[i] < kleinsteMinimum)
					kleinsteMinimum = minimumPunten[i];
			}
			if (docentFuncties != null && graphPoints.size() < kleinsteMinimum)
			{
				score = 0;
				correct = false;
				if (setState && comRoot != null) // als setState false absoluut GEEN setChanged()!
					comRoot.setChanged(false);
				if (graphPoints.size() > 0)	
				{
					ingevuld = true;
					if ((checkExternal || mode == OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS) && show)
					{	
						setFeedback(GraphToolGWT.rb.feedbackTekstTeWeinigPunten(),true);
					}
				}
				return;
			}
			if (docentFuncties != null)
			{
//				puntenNagekeken = true; // RPJ MISSCHIEN LATER AAN Zorgt voor verschillende kleuren per punt
				boolean[] functieCorrect = new boolean[aantalFuncties];
				boolean puntenCorrect = true;
				for (int i = 0; i < aantalFuncties; i++)
				{
					functieCorrect[i] = false;
				}
				Expressie[] vgldocentFuncties = new Expressie[aantalFuncties];
				for (int i = 0; i < aantalFuncties; i++)
				{
					vgldocentFuncties[i] = docentFuncties[i];
				}
				ingevuld = true;
				CssColor color = CssColor.make(255, 0, 0);
				score = 0; 
				correct = false;
				fout = false;
				int[][] hits = new int[aantalFuncties][aantalFuncties];
				
				Vector[] checkPoints = new Vector[aantalFuncties];
				for (int i = 0; i < aantalFuncties; i++)
				{
					checkPoints[i] = getPoints(i + 1, false);
					for (int j = 0; j < hits.length; j++)
					{
						hits[i][j] = 0;
					}
					if (checkPoints[i].size() > 0)
					{
						for (int pCnt = 0; pCnt < checkPoints[i].size(); pCnt++)
						{
							RealPoint lPoint = (RealPoint) checkPoints[i].elementAt(pCnt);
							Point lPixel = realPointToPixels(lPoint);
							for (int j = 0; j < aantalFuncties; j++)
							{	//Vergelijk getekende punt (lPoint) met het punt met dezelfde x-coördinaat en 
								//als y-coördinaat de functiewaarde van de door auteur opgegeven functie.
																
								double dWaarde = docentFuncties[j].geefWaarde(lPoint.getX());
								RealPoint dPoint = new RealPoint(lPoint.getX(), dWaarde);
								Point dPixel = realPointToPixels(dPoint);
								double dis = 1000;
								try
								{
									dis = Math.sqrt((lPixel.getX() - dPixel.getX()) * (lPixel.getX() - dPixel.getX()) +
											(lPixel.getY() - dPixel.getY()) * (lPixel.getY() - dPixel.getY())); 
								}
								catch(Exception e)
								{}
								if (dis < nauwkeurigheid[j])
									hits[i][j]++;
								else
								{
									double yVerschil = 100;
									boolean positiefVerschil = false;
									for (int k = 1; k < nauwkeurigheid[j]; k++)
									{
										double xWaarde = lPixel.getX() - k;
										if (manualScalingX)
										{
											dPoint.setX(eenheidxValue * (-beginx)/eenheidxD + eenheidxValue * xWaarde / eenheidxD); 
										}
										else
										{
											dPoint.setX(schaalFactorX * (-beginx)/eenheidxD + schaalFactorX * xWaarde / eenheidxD); 
										}
										if (xAsLog)
											dPoint.setX(Math.pow(10, dPoint.getX()));
										
										dWaarde = docentFuncties[j].geefWaarde(dPoint.getX());
										dPoint.setY(dWaarde);
										dPixel = realPointToPixels(dPoint);
										dis = 1000;
										try
										{
											dis =  Math.sqrt((lPixel.getX() - dPixel.getX()) * (lPixel.getX() - dPixel.getX()) +
										     	   (lPixel.getY() - dPixel.getY()) * (lPixel.getY() - dPixel.getY()));
										}
										catch (Exception e){}
										
										if (dis < nauwkeurigheid[j])
										{
											hits[i][j]++;
											break;
										}
										yVerschil = lPoint.getY() - dWaarde;
										if (k == 1)
											positiefVerschil = yVerschil > 0;
										else if(positiefVerschil != yVerschil > 0)
										{
											hits[i][j]++;
											break;
										}
										xWaarde = lPixel.getX() + k;
										if (manualScalingX)
										{
											dPoint.setX(eenheidxValue * (-beginx)/eenheidxD + eenheidxValue * xWaarde / eenheidxD); 
										}
										else
										{
											dPoint.setX(schaalFactorX * (-beginx)/eenheidxD + schaalFactorX * xWaarde / eenheidxD); 
										}
										if (xAsLog)
											dPoint.setX(Math.pow(10, dPoint.getX()));
										
										dWaarde = docentFuncties[j].geefWaarde(dPoint.getX());
										dPoint.setY(dWaarde);
										dPixel = realPointToPixels(dPoint);
										dis = 1000;
										try
										{
											dis =  Math.sqrt((lPixel.getX() - dPixel.getX()) * (lPixel.getX() - dPixel.getX()) +
											     	   (lPixel.getY() - dPixel.getY()) * (lPixel.getY() - dPixel.getY()));
										}
										catch (Exception e){}
										if (dis < nauwkeurigheid[j])
										{
											hits[i][j]++;
											break;
										}
										yVerschil = lPoint.getY() - dWaarde;
										if (positiefVerschil != yVerschil > 0)
										{
											hits[i][j]++;
											break;
										}
									}
								}
							}
						}
					}
				}
				int[][] permutatie = new int[aantalFuncties][2];
				for (int i = 0; i < permutatie.length; i++)
				{
					permutatie[i][0] = i;
					permutatie[i][1] = -1;
				}
				permutatie[0][1] = 0;
				int totaalHits = 0;
				int permutatieHits;
				int[] koppeling = new int[aantalFuncties];
				while (permutatie != null)
				{
					permutatieHits = 0;
					for (int i = 0; i < permutatie.length; i++)
					{
						permutatieHits += hits[i][permutatie[i][0]];
					}
					if (permutatieHits > totaalHits)
					{
						totaalHits = permutatieHits;
						for (int i = 0; i < permutatie.length; i++)
						{
							koppeling[i] = permutatie[i][0];
						}
					}
					permutatie = vindVolgendePermutatie(permutatie);
				}
				int somMinimum = 0;
				for (int i = 0; i < aantalFuncties; i++)
				{
					somMinimum += minimumPunten[i];
				}
				int[] scorePerPunt = new int[aantalFuncties];
				for (int i = 0; i < aantalFuncties; i++)
				{
					scorePerPunt[koppeling[i]] = maxScores[i] / Math.max(checkPoints[i].size(), minimumPunten[koppeling[i]]);
				}
				boolean alleFunctiesCorrect = true;
				for (int i = 0; i < aantalFuncties; i++)
				{
					if (hits[i][koppeling[i]] == Math.max(checkPoints[i].size(), minimumPunten[koppeling[i]]))
						functieCorrect[koppeling[i]] = true;
					else
						alleFunctiesCorrect = false;
				}
				if (totaalHits == 0)
				{
					score = 0; 
					fout = true;
					goedkrulImage.setVisible(false);
					goedkrulHalfImage.setVisible(false);
					foutkruisImage.setVisible(show);
				}
				else if (totaalHits == Math.max(graphPoints.size(), somMinimum) && alleFunctiesCorrect)
				{
					for (int i = 0; i < aantalFuncties; i++)
					{
						color = CssColor.make(0, 200, 0);
					}
//					for(int i = 0; i < aantalFuncties; i++)
//					{	functieCorrect[i] = true;
//					
//					}
					score = scoreMax; 
					correct = true;
					goedkrulImage.setVisible(show);
					goedkrulHalfImage.setVisible(false);
					foutkruisImage.setVisible(false);
				}
				else 
				{
					score = 0;
					for (int i = 0; i < aantalFuncties; i++)
					{
						if (functieCorrect[koppeling[i]])
						{	//functieCorrect[koppeling[i]] = true;
							color = CssColor.make(0, 200, 0);
							score += maxScores[koppeling[i]];
						}
						else
							score += hits[i][koppeling[i]] * scorePerPunt[koppeling[i]];
					}
					fout = true;
					goedkrulImage.setVisible(false);
					goedkrulHalfImage.setVisible(show);
					foutkruisImage.setVisible(false);
					boolean minstensEenFunctieCorrect = false;
					for (int i = 0; i < functieCorrect.length; i++)
					{
						if (functieCorrect[i])
						{
							minstensEenFunctieCorrect = true;
							break;
						}
					}
					if (show && minstensEenFunctieCorrect)
					{
						setFeedback(GraphToolGWT.rb.feedbackTekstGrafiekenDeels(),true);
					}
					else if (show)
					{
						setFeedback(GraphToolGWT.rb.feedbackTekstPuntenDeels(),true);
						puntenCorrect = false;
					}
					//if(show)setFeedback(GraphToolGWT.rb.getString("feedbackTekstPuntenDeels"),true);
				}
				
				boolean correctLogica = (correct || goedkrulHalfImage.isVisible() && puntenCorrect);
				color = verwerkAsNaamBijNakijken(show, color, correctLogica);
				color = verwerkTekenModusBijNakijken(show, color, correctLogica);

				if (show)
				{	
					//leerlingcolor op juiste kleur zetten.
					for (int i = 0; i < aantalFuncties; i++)
					{	
						if (functieCorrect[koppeling[i]])
						{
							setColor(i, cColorGreen, true);
							//colors[i] = color;
						}
						else
						{
							setColor(i, cColorRed, true);
							//	colors[i] = CssColor.make(255, 0, 0);
						}
					}
					tekenDocentFuncties = new Expressie[aantalFuncties];
					if (!rechteVerbindingen && !krommeMetExtrapolatie && !krommeZonderExtrapolatie)
					{
						for (int i = 0; i < aantalFuncties; i++)
						{
							if (functieCorrect[i])
							{
								tekenDocentFuncties[i] = docentFuncties[i];
							}
						}
						docentColor = color;
					}
					//grafiekGWTVeld.paint(); //nodig?
				}
			}
		}
		else if (typeOpdracht == TEKENTABELPUNTEN)
		{	
			if (graphPoints.size() < docentGraphPoints.size())
			{
				score = 0;
				correct = false;
				//produceAction("changed");
				if (graphPoints.size() > 0)	
				{
					ingevuld = true;
					if ((checkExternal || mode == OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS) && show)
					{	
						setFeedback(GraphToolGWT.rb.feedbackTekstTeWeinigPunten(),true);
					}
				}
				return;
			}
			if (graphPoints.size() > 0)
			{	
				puntenNagekeken = true;
				ingevuld = true;
				CssColor color = CssColor.make(255, 0, 0);
				score = 0;
				correct = false;
				fout = false;
				// graphPoints aanvullen indien nodig
				while (graphPointColors.size() <  graphPoints.size() )
				{
					graphPointColors.add(cColorRed.value());
				}
				
				Vector llgPtsCopy = new Vector();
				for (int pCnt = 0; pCnt < graphPoints.size(); pCnt++)
				{
					RealPoint pt = (RealPoint) graphPoints.elementAt(pCnt);
					//alleen naar punten van actieve tabel (= grafiek met index 0) kijken; 
					//in andere tabellen kan de docent punten hebben getekend, die moeten niet worden meegenomen.
					if (pt.getIndex() == 1)
					{
						llgPtsCopy.addElement(graphPoints.elementAt(pCnt));
						graphPointColors.setElementAt(cColorRed.value(), pCnt); // default is false
					}
				}
				//RealPoint[] llgPtsArray = new RealPoint[graphPoints.size()];
				RealPoint[] llgPtsArray = new RealPoint[llgPtsCopy.size()];
				if (llgPtsArray.length == 0)
				{
					ingevuld = false;
					return;
				}
				for (int dCnt = 0; dCnt < docentGraphPoints.size(); dCnt++)
				{
					RealPoint dPt = (RealPoint) docentGraphPoints.elementAt(dCnt);
					RealPoint lPt = (RealPoint) llgPtsCopy.elementAt(0);
					int index = 0;
					double distance = Math.sqrt((dPt.getX() - lPt.getX())*(dPt.getX() - lPt.getX()) + (dPt.getY() - lPt.getY())*(dPt.getY() - lPt.getY()));
					for (int lCnt = 1; lCnt < llgPtsCopy.size(); lCnt++)
					{
						RealPoint aLlgPt = (RealPoint) llgPtsCopy.elementAt(lCnt);
						double aDis = Math.sqrt((dPt.getX() - aLlgPt.getX()) * (dPt.getX() - aLlgPt.getX()) +
												(dPt.getY() - aLlgPt.getY()) * (dPt.getY() - aLlgPt.getY()));
						if (aDis < distance)
						{
							distance = aDis;
							index = lCnt;
						}
					}
					
					llgPtsArray[dCnt] = (RealPoint) llgPtsCopy.elementAt(index);
					llgPtsCopy.removeElementAt(index);
				}
			
				int hits = 0;
				for (int dCnt = 0; dCnt < docentGraphPoints.size(); dCnt++)
				{	
					RealPoint dPoint = (RealPoint) docentGraphPoints.elementAt(dCnt);
					RealPoint lPoint = llgPtsArray[dCnt];
					
					Point lPixel = realPointToPixels(lPoint);
					Point dPixel = realPointToPixels(dPoint);
	
					double dis = Math.sqrt((lPixel.getX() - dPixel.getX()) * (lPixel.getX() - dPixel.getX()) +
								     	   (lPixel.getY() - dPixel.getY()) * (lPixel.getY() - dPixel.getY())); 
					
//					if (dis < nauwkeurigheid[0]) {
//						hits++;
//						graphPointColors.setElementAt(cColorGreen, dCnt);
//					} 
					if (dis < nauwkeurigheid[0])
					{
						hits++;
						if (show)
						{ // teken het juiste punt groen
							// findIndex of hitPoint

							double verglNauwkeurigheid = 0.000001;
							boolean found = false;
							for (int i = 0; (i < graphPoints.size() && !found); i++)
							{
								RealPoint pt = (RealPoint) graphPoints.elementAt(i); 
								
								if ( (Math.abs(lPoint.getX() - pt.getX()) < verglNauwkeurigheid) && 
										(Math.abs(lPoint.getY() - pt.getY()) < verglNauwkeurigheid) )
								{
									found = true;
									graphPointColors.setElementAt(cColorGreen.value(), i);
								}
							}
						}
					}
				}
				
				int scorePerPunt = scoreMax / Math.max(docentGraphPoints.size(), llgPtsArray.length);
				if (hits == 0)
				{
					score = 0;
					fout = true;
				}
				else if (hits == llgPtsArray.length)
				{
					color = CssColor.make(0, 200, 0);
					score = scoreMax;
					correct = true;
					goedkrulImage.setVisible(show);
					goedkrulHalfImage.setVisible(false);
					foutkruisImage.setVisible(false);
				}
				else
				{	
					score = hits * scorePerPunt;    			
					fout = true;
/* oud					
					goedkrulImage.setVisible(false);
					goedkrulHalfImage.setVisible(false);
					foutkruisImage.setVisible(show);
*/					
					goedkrulImage.setVisible(false);
					goedkrulHalfImage.setVisible(show);
					foutkruisImage.setVisible(false);
					setFeedback(GraphToolGWT.rb.feedbackTekstPuntenDeels(),true);
					color = cColorOrange;
				}
				
				color = verwerkTekenModusBijNakijken(show, color, correct);
				color = verwerkAsNaamBijNakijken(show, color, correct);

				if (show)
				{
					setColor(0, color, true);
				}
				
				//grafiekGWTVeld.paint(); // nodig?
			}
		}
		
		if ( (show) && (!setState))
		{
			if (ingevuld)
			{
				if (comRoot != null)
					comRoot.setChanged(fout);
			}
		}
		
		if (fout)
			verhoogErrorCount();
		
		if (mode == OpdrNavIF.OEFENEN_STRAFPUNTEN)
			score = Math.max(0, score - errorCount * foutStraf);

		//if (ingevuld)
			//produceAction("changed");
		grafiekGWTVeld.paint();
		
		if (show && ingevuld) // alleen als feedback moet worden getoond
		{
			comRoot.setChanged(isCorrect() == null ? false : isCorrect().booleanValue()); // bij halfgoed is correct null...
			
			if (correct) 
				fireEvent(EVENT_CORRECT);
			else if (errorCount > 1) 
				fireEvent(EVENT_FALSE2);
			else
				fireEvent(EVENT_FALSE);
		}
	}
	

	public void kijkNa()
    {
		kijkNa(true, false /* geen setState */);
	}
	
	
	public void setFeedback(String tekst, boolean closeable)
	{	
		feedbackTekst.setText("");
		//feedbackTekst.setCloseable(closeable);
		//feedbackPanel.setSize((breedte - kijkNaButton.getOffsetWidth() - buttonSize - 5) + "px", "29px");
		//feedbackTekst.setSize(195,20);
		if(checkExternal || mode == OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS)
		{
			if(!feedbackPanel.isAttached())
				grafiekVeldPanel.add(feedbackPanel);
			grafiekVeldPanel.setWidgetLeftRight(feedbackPanel, 5, Style.Unit.PX, buttonSize + 10, Style.Unit.PX);
			grafiekVeldPanel.setWidgetBottomHeight(feedbackPanel, 3, Style.Unit.PX, 35, Style.Unit.PX);
		}
		else
		{
			if(!feedbackPanel.isAttached())
			{	
				kijkNaPanel.add(feedbackPanel);
			}
			kijkNaPanel.setWidgetRightWidth(feedbackPanel, 3, Style.Unit.PX, (breedte - kijkNaButton.getOffsetWidth() - buttonSize - 6), Style.Unit.PX);
			kijkNaPanel.setWidgetTopBottom(feedbackPanel, 0, Style.Unit.PX, 1, Style.Unit.PX);//zou niet nodig moeten zijn.
		}
		
		feedbackTekst.setText(tekst);
		
	}
    
    	
    public int[][] vindVolgendePermutatie(int[][] permutatie)
    {	int verplaatsIndex = -1;
    	int verplaatsRichting = 0;
    	int verplaatsGetal = -1;
    	int[][] nieuwePerm = new int[permutatie.length][2];
    	
    	for(int i = 0; i < permutatie.length; i++)
    	{	if(permutatie[i][1] != 0 && permutatie[i][0] > verplaatsGetal)
    		{	verplaatsIndex = i;
    			verplaatsRichting = permutatie[i][1];
    			verplaatsGetal = permutatie[i][0];
    		}
    	}
    	if(verplaatsRichting == 1)
    	{	for(int i = 0; i < verplaatsIndex; i++)
    			nieuwePerm[i] = permutatie[i];
    		nieuwePerm[verplaatsIndex] = permutatie[verplaatsIndex + 1];
    		nieuwePerm[verplaatsIndex + 1] = permutatie[verplaatsIndex];
    		if(permutatie.length > verplaatsIndex + 2)
    		{	for(int i = verplaatsIndex + 2; i < permutatie.length; i++)
    				nieuwePerm[i] = permutatie[i];
    		}
    		if(verplaatsIndex == permutatie.length - 2 || nieuwePerm[verplaatsIndex + 2][0] > nieuwePerm[verplaatsIndex + 1][0])
    			nieuwePerm[verplaatsIndex + 1][1] = 0;
    		for(int i = 0; i < verplaatsIndex + 1; i++)
    		{	if(nieuwePerm[i][0] > nieuwePerm[verplaatsIndex + 1][0])
    				nieuwePerm[i][1] = 1;
    		}
    		if(permutatie.length > verplaatsIndex + 2)
    		{	for(int i = verplaatsIndex + 2; i < permutatie.length; i++)
    			{	if(nieuwePerm[i][0] > nieuwePerm[verplaatsIndex + 1][0])
    					nieuwePerm[i][1] = -1;
    			}
    		}
    	}
    	else if(verplaatsRichting == -1)
    	{	for(int i = 0; i < verplaatsIndex - 1; i++)
				nieuwePerm[i] = permutatie[i];
    		nieuwePerm[verplaatsIndex - 1] = permutatie[verplaatsIndex];
    		nieuwePerm[verplaatsIndex] = permutatie[verplaatsIndex - 1];
    		if(permutatie.length > verplaatsIndex + 1)
    		{	for(int i = verplaatsIndex + 1; i < permutatie.length; i++)
    				nieuwePerm[i] = permutatie[i];
    		}
    		if(verplaatsIndex == 1 || nieuwePerm[verplaatsIndex - 1][0] < nieuwePerm[verplaatsIndex - 2][0])
    			nieuwePerm[verplaatsIndex - 1][1] = 0;
    		for(int i = 0; i < verplaatsIndex - 1; i++)
    		{	if(nieuwePerm[i][0] > nieuwePerm[verplaatsIndex - 1][0])
    				nieuwePerm[i][1] = 1;
    		}
    		if(permutatie.length > verplaatsIndex + 1)
    		{	for(int i = verplaatsIndex; i < permutatie.length; i++)
    			{	if(nieuwePerm[i][0] > nieuwePerm[verplaatsIndex - 1][0])
    					nieuwePerm[i][1] = -1;
    			}
    		}
    	}
    	else
    		nieuwePerm = null;
    			
    	return nieuwePerm;
    }

    public void kijkNa(int stapNr)
    {
		kijkNa(true, false /* geen setState */);
	}
	
	public void kijkNa(int stapNr, boolean show)
	{
		kijkNa(show, false /* geen setState */);
	}
	
	
	public void updateTabelNames(String[] expNaam, boolean setState)
	{
		if(!tabelAlsTekenTool)
			tabelComponent.updateTabelNames(expNaam, formuleComponent.getMaxAantalFuncties(), setState);
	}
	
	public void zetOngelijkheid(int nr, Expressie e, boolean isYOngelijkheid, boolean isGroterGelijkOngelijkheid, boolean isEnOngelijkheid)//, boolean inclusiefGelijkheid)
	{
		ongelijkheden[nr] = e;
		isY[nr] = isYOngelijkheid;
		isGroterGelijk[nr] = isGroterGelijkOngelijkheid;
		isEn[nr] = isEnOngelijkheid;
		
		//if(paint)
		//	grafiekGWTVeld.paint();
	}
	
	public void zetFunctie(int nr, Expressie e, String expString, String expNaam, double[] domein, boolean update,
		boolean setState, boolean docent)
	{
		functies[nr] = e;
		domeinen[nr][0] = domein[0];
		domeinen[nr][1] = domein[1];
		if (update)
		{
			grafiekGWTVeld.paint();
		}

		if (!tabelAlsTekenTool)
		{
			tabelComponent.zetFunctie(nr, e, expNaam, update, setState);
		}
	}
	
	public void zetVectorVeld(int stelselNr, int functieNr, Expressie expressie) {
		veldFuncties[stelselNr][functieNr] = expressie;
//		logger.info("veldFuncties["+stelselNr+"]["+functieNr+"]=" +veldFuncties[stelselNr][functieNr] );
	}

	
	public void zetVerticaleLijn(int nr, Expressie e)
	{
		verticaleLijnen[nr] = e;
		//if(paint)
		//	grafiekGWTVeld.paint();
	}
	
	public GraphToolGWT(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden, int volleBreedte)
	{	ObjectMap map = JSONUtilities.wrapMap(h);
	
		if(map != null)
		{
			if(map.containsKey("breedte"))
				breedte = map.getInt("breedte");
			if(map.containsKey("hoogte"))
				hoogte = map.getInt("hoogte");
			if(map.containsKey("volledigeBreedte"))
				volledigeBreedte = map.getBoolean("volledigeBreedte");
			facet = new FacetHelper(map);
		}
		
		if(volledigeBreedte)
			breedte = volleBreedte;
		//if (h != null && h.get("breedte") != null)
		//	breedte =  h.get("breedte")).intValue();
		//if (h != null && h.get("hoogte") != null)
		//	hoogte =  h.get("hoogte")).intValue();
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		
//		this.randomVarNamen = randomVarNamen; should be set in  "init"
//		this.randomVarWaarden = randomVarWaarden;
        basisPanel.setPixelSize(breedte, hoogte );//setSize("" + (breedte + 10) + "px", "" + (hoogte + 10) + "px");
		
		//alle gegevens uit launchData halen: 
		init(breedte, hoogte, launchState, randomVarWaarden);
		
		//hoogte van grafiekveld uitrekenen, componenten maken en de benodigde ook plaatsen, instellingen van de verschillende componenten:
		//initialize();
		asHoogte = new FormuleHolder().getDefaultFont().getAscent(); 
	}

	@Override
	public HashMap<String, Object> getState() {
		
		if ( (!moveActionActivated) && 
				( (mode == OpdrNavIF.EINDTOETS) || (mode == OpdrNavIF.ZELFTOETS) ) ) {
			formuleComponent.updateFormulas();
			kijkNa(false /* no show */, false /* geen setState */);
		}
			if (mode == OpdrNavIF.EINDTOETS) {
				if (!nagekeken) {
					setAttempt();
				}
				zetNagekeken(true);
			}
		
		double beginx = 1;
		double beginy = 1;
		double schaalFactorX  = 1;
		double schaalFactorY  = 1;
		
		double[] graphPointsX = new double[graphPoints.size()];
		double[] graphPointsY = new double[graphPoints.size()];
		//ArrayList<Double> graphPointsX = new ArrayList<Double>();
		//ArrayList<Double> graphPointsY = new ArrayList<Double>();
		int[] graphPointsIndex = new int[graphPoints.size()];
		int[] graphPointsTabelIndex = new int[graphPoints.size()];
		//ArrayList<Integer> graphPointsIndex = new ArrayList<Integer>();
		//ArrayList<Integer> graphPointsTabelIndex = new ArrayList<Integer>();
		String[] graphPointsXString = new String[graphPoints.size()];
		String[] graphPointsYString = new String[graphPoints.size()];
		
		for(int i = 0; i < graphPoints.size(); i++)
		{	graphPointsX[i] = ((RealPoint) graphPoints.elementAt(i)).getX();
			graphPointsY[i] = ((RealPoint) graphPoints.elementAt(i)).getY();
			//graphPointsX.add(i, ((RealPoint) graphPoints.elementAt(i)).getX());
			//graphPointsY.add(i, ((RealPoint) graphPoints.elementAt(i)).getY());
			graphPointsIndex[i] = ((RealPoint) graphPoints.elementAt(i)).getIndex();
			graphPointsTabelIndex[i] = ((RealPoint) graphPoints.elementAt(i)).getTabelIndex();
			//graphPointsIndex.add(i, ((RealPoint) graphPoints.elementAt(i)).getIndex());
			//graphPointsTabelIndex.add(i, ((RealPoint) graphPoints.elementAt(i)).getTabelIndex());
			graphPointsXString[i] = ((RealPoint) graphPoints.elementAt(i)).getxString();
			graphPointsYString[i] = ((RealPoint) graphPoints.elementAt(i)).getyString();
		}
		//ArrayList<ArrayList<Integer>> colorRGBs = new ArrayList<ArrayList<Integer>>();
		
		double[] paramWaarden = null;
		if(schuifParameters != null) {	
			paramWaarden = new double[schuifParameters.length];
			for(int i = 0; i < schuifParameters.length; i++)
				paramWaarden[i] = schuifParameters[i].geefWaarde();
		}

		int[][] colorRGBsGraphPoints = new int[graphPointColors.size()][3];
		for(int i = 0; i < graphPointColors.size(); i++)
		{	int[] kleurLijstje = new int[3];
			String waarde = graphPointColors.elementAt(i);
			if(waarde.startsWith("rgb"))
			{	waarde = waarde.substring(4, waarde.length() - 1);
				try
				{	String[] waardeLijstje = FormuleComponentGWT.split(waarde, ",");
					for(int j = 0; j < 3; j++)
					{	try{kleurLijstje[j] = Integer.parseInt(waardeLijstje[j]);}
						catch(Exception e){/*kleurLijstje[j] = 0;*/}
					}
				}
				catch(Exception e)
				{	/*for(int j = 0; j < 3; j++)
						kleurLijstje[j] = 0;*/
				}
			}
			else
			{	for(int j = 0; j < 3; j++)
					kleurLijstje[j] = 0;
			}
			colorRGBsGraphPoints[i] = kleurLijstje;
		}
		
		int[][] colorRGBsOpdrachten = new int[opdrachtKleuren.length][3];
		for(int i = 0; i < opdrachtKleuren.length; i++)
		{	int[] kleurLijstje = new int[3];
			String waarde = opdrachtKleuren[i];
			if(waarde.startsWith("rgb"))
			{	waarde = waarde.substring(4, waarde.length() - 1);
				try
				{	String[] waardeLijstje = FormuleComponentGWT.split(waarde, ",");
					for(int j = 0; j < 3; j++)
					{	try{kleurLijstje[j] = Integer.parseInt(waardeLijstje[j]);}
						catch(Exception e){/*kleurLijstje[j] = 0;*/}
					}
				}
				catch(Exception e)
				{	/*for(int j = 0; j < 3; j++)
						kleurLijstje[j] = 0;*/
				}
			}
			else
			{	for(int j = 0; j < 3; j++)
					kleurLijstje[j] = 0;
			}
			colorRGBsOpdrachten[i] = kleurLijstje;
		}
		
		int[][] colorRGBsGewoon = new int[gewoneKleuren.length][3];
		for(int i = 0; i < gewoneKleuren.length; i++)
		{	int[] kleurLijstje = new int[3];
			String waarde = gewoneKleuren[i];
			if(waarde.startsWith("rgb"))
			{	waarde = waarde.substring(4, waarde.length() - 1);
				try
				{	String[] waardeLijstje = FormuleComponentGWT.split(waarde, ",");
					for(int j = 0; j < 3; j++)
					{	try{kleurLijstje[j] = Integer.parseInt(waardeLijstje[j]);}
						catch(Exception e){/*kleurLijstje[j] = 0;*/}
					}
				}
				catch(Exception e)
				{	/*for(int j = 0; j < 3; j++)
						kleurLijstje[j] = 0;*/
				}
			}
			else
			{	for(int j = 0; j < 3; j++)
					kleurLijstje[j] = 0;
			}
			colorRGBsGewoon[i] = kleurLijstje;
		}
		
		int activeIndex = 1;
		String grafiekXAsNaam = "x";
		String grafiekYAsNaam = "y";
		
		int selectnummer = 999;
		int beginwaarde = 0;
		
		boolean ingevuld = true;
		boolean nagekeken = false;
		boolean puntenNagekeken = false;
		int errorCount = this.errorCount;
		
		beginx = this.beginx;
		beginy =  this.beginy;
		schaalFactorX = this.schaalFactorX;
		schaalFactorY = this.schaalFactorY;
		
		activeIndex = this.activeIndex;
		grafiekXAsNaam = this.grafiekXAsNaam;
		grafiekYAsNaam = this.grafiekYAsNaam;
		
		selectnummer = this.selectnummer;
		beginwaarde = this.beginwaarde;
		
		ingevuld = this.ingevuld;
		nagekeken = this.nagekeken;
		puntenNagekeken = this.puntenNagekeken;
		
		HashMap<String, Object> h = new HashMap<String,Object>();
		h = tekenComponent.getState();
		HashMap<String, Object> h1 = formuleComponent.getState();
		h.putAll(h1);
		HashMap<String, Object> h2 = veldComponent.getState();
		h.putAll(h2);
		HashMap<String, Object> h3 = tabelComponent.getState();
		h.putAll(h3);
		
		h.put("beginx", new Double(beginx));
		h.put("beginy", new Double(beginy));
		h.put("schaalFactorX", new Double(schaalFactorX));
		h.put("schaalFactorY", new Double(schaalFactorY));
		h.put("graphPointsX", graphPointsX);
		h.put("graphPointsY", graphPointsY);
		h.put("graphPointsIndex", graphPointsIndex);
		h.put("graphPointsTabelIndex", graphPointsTabelIndex);
		h.put("graphPointsXString", graphPointsXString);
		h.put("graphPointsYString", graphPointsYString);
		h.put("paramWaarden", paramWaarden);
		//h.put("colorRGBs", colorRGBs);
		h.put("colorRGBsGewoon", colorRGBsGewoon);
		h.put("colorRGBsOpdrachten", colorRGBsOpdrachten);
		h.put("colorRGBsGraphPoints", colorRGBsGraphPoints);
		h.put("activeIndex", new Integer(activeIndex));
		h.put("grafiekXAsNaam", new String(grafiekXAsNaam));
		h.put("grafiekYAsNaam", new String(grafiekYAsNaam));
		h.put("selectnummer", new Integer(selectnummer));
		h.put("beginwaarde", new Integer(beginwaarde));
		h.put("ingevuld", new Boolean(ingevuld));
		h.put("nagekeken", new Boolean(nagekeken));
		h.put("puntenNagekeken", new Boolean(puntenNagekeken));
		h.put("errorCount", new Integer(errorCount));
		
		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		if (h == null || h.isEmpty())
			return;
		
		fromuser = false;
		//hier alleen dingen in die de leerling veranderd kan hebben.
		/*
		List<?> graphPointsX = null;
		List<?> graphPointsY = null;
		List<?> graphPointsIndex = null;
		List<?> graphPointsTabelIndex = null;
		List<String> graphPointsXString = null;
		List<String> graphPointsYString = null;
		
		graphPointsX = toList (h.get("graphPointsX"));
		graphPointsY = toList (h.get("graphPointsY"));
		graphPointsIndex = toList (h.get("graphPointsIndex"));
		graphPointsTabelIndex = toList (h.get("graphPointsTabelIndex"));
		graphPointsXString = toList (h.get("graphPointsXString"));
    	graphPointsYString = toList (h.get("graphPointsYString"));
    	*/
		
    	double[] graphPointsX = null;
		double[] graphPointsY = null;
		int[] graphPointsIndex = null;
		int[] graphPointsTabelIndex = null;
		String[] graphPointsXString = null;
		String[] graphPointsYString = null;
		
    	ObjectMap map = JSONUtilities.wrapMap(h);
		if(map.containsKey("graphPointsX"))
			graphPointsX = map.getDoubleArray("graphPointsX");
		if(map.containsKey("graphPointsY"))
			graphPointsY = map.getDoubleArray("graphPointsY");
		if(map.containsKey("graphPointsIndex"))
			graphPointsIndex = map.getIntArray("graphPointsIndex");
		if(map.containsKey("graphPointsTabelIndex"))
			graphPointsTabelIndex = map.getIntArray("graphPointsTabelIndex");
		if(map.containsKey("graphPointsXString"))
			graphPointsXString = map.getStringArray("graphPointsXString");
		if(map.containsKey("graphPointsYString"))
			graphPointsYString = map.getStringArray("graphPointsYString");
		
    	this.graphPoints = new Vector();
		
    	if(graphPointsX != null) 
		{	for(int i = 0; i < graphPointsX.length; i++)
			{	RealPoint rp = new RealPoint(graphPointsX[i], graphPointsY[i]);
				rp.setIndex(graphPointsIndex[i]);
				rp.setTabelIndex(graphPointsTabelIndex[i]);
				rp.setxString(graphPointsXString[i]);
				rp.setyString(graphPointsYString[i]);
				graphPoints.add(rp);
			}
			
			/*
			for(int i = 0; i < graphPointsX.size(); i++)
			{	RealPoint rp = new RealPoint(((Number) graphPointsX.get(i)).doubleValue(), ((Number) graphPointsY.get(i)).doubleValue());
				rp.setIndex(((Number) graphPointsIndex.get(i)).intValue());
				rp.setTabelIndex(((Number)graphPointsTabelIndex.get(i)).intValue());
				rp.setxString(graphPointsXString.get(i));
				rp.setyString(graphPointsYString.get(i));
				graphPoints.add(rp);
			}
			*/
		}
		
    	double[] paramWaarden = null;
    	if(map.containsKey("paramWaarden"))
    		paramWaarden = map.getDoubleArray("paramWaarden");
    	
    	if(schuifParameters != null) {	
    		for (int i = 0; i < schuifParameters.length; i++) {
    			schuifParameters[i].zetWaarde(paramWaarden[i]);
    		}
    	}
    	
		int[][] colorRGBsGewoon = null;
		if (map.containsKey("colorRGBsGewoon"))
		{	ObjectList list = map.getObjectList("colorRGBsGewoon");
			//List<Object> colorRGBsGewoonList = JSONUtilities.toArrayList(h.get("colorRGBsGewoon"));
			//ArrayList<ArrayList<Integer>> colorRGBsList = (ArrayList<ArrayList<Integer>>) h.get("colorRGBs");
			colorRGBsGewoon = new int[list.size()][];
			for (int i = 0; i < colorRGBsGewoon.length; i++) {
				colorRGBsGewoon[i] = list.getIntArray(i);
				gewoneKleuren[i] = CssColor.make(colorRGBsGewoon[i][0], colorRGBsGewoon[i][1], colorRGBsGewoon[i][2]).value();
			}
		}
    	
		int[][] colorRGBsGraphPoints = null;
		if (map.containsKey("colorRGBsGraphPoints")) {	
			graphPointColors.clear();
			ObjectList list = map.getObjectList("colorRGBsGraphPoints");
			//List<Object> colorRGBsGewoonList = JSONUtilities.toArrayList(h.get("colorRGBsGewoon"));
			//ArrayList<ArrayList<Integer>> colorRGBsList = (ArrayList<ArrayList<Integer>>) h.get("colorRGBs");
			colorRGBsGraphPoints = new int[list.size()][];
			for (int i = 0; i < colorRGBsGraphPoints.length; i++) {
				colorRGBsGraphPoints[i] = list.getIntArray(i);
				graphPointColors.add(CssColor.make(colorRGBsGraphPoints[i][0], colorRGBsGraphPoints[i][1], colorRGBsGraphPoints[i][2]).value());
			}
		}
		
		int[][] colorRGBsOpdrachten = null;
		if (map.containsKey("colorRGBsOpdrachten"))
		{	ObjectList list = map.getObjectList("colorRGBsOpdrachten");
			//List<Object> colorRGBsGewoonList = JSONUtilities.toArrayList(h.get("colorRGBsGewoon"));
			//ArrayList<ArrayList<Integer>> colorRGBsList = (ArrayList<ArrayList<Integer>>) h.get("colorRGBs");
			colorRGBsOpdrachten = new int[list.size()][];
			for (int i = 0; i < colorRGBsOpdrachten.length; i++) {
				colorRGBsOpdrachten[i] = list.getIntArray(i);
				opdrachtKleuren[i] = CssColor.make(colorRGBsOpdrachten[i][0], colorRGBsOpdrachten[i][1], colorRGBsOpdrachten[i][2]).value();
			}
		}
		
		zetOpdrachtKleuren(typeOpdracht != GEENOPDRACHT);
		
		double beginx = 1;
		double beginy = 1;
		double schaalFactorX = 1;
		double schaalFactorY = 1;

		int activeIndex = 1;
		String grafiekXAsNaam = "x";
		String grafiekYAsNaam = "y";
		int selectnummer = 999;
		int beginwaarde = 0;
		
		boolean ingevuld = false;
		boolean nagekeken = false;
		boolean puntenNagekeken = false;
		
		if(map.containsKey("beginx")) 
    		beginx = map.getDouble("beginx");
    	if(map.containsKey("beginy")) 
    		beginy = map.getDouble("beginy");
    	if(map.containsKey("schaalFactorX")) 
    		schaalFactorX = map.getDouble("schaalFactorX");
    	if(map.containsKey("schaalFactorY")) 
    		schaalFactorY = map.getDouble("schaalFactorY");
    	
    	if(map.containsKey("activeIndex"))
    		activeIndex = map.getInt("activeIndex");
    	if(map.containsKey("grafiekXAsNaam"))
    		grafiekXAsNaam = map.getString("grafiekXAsNaam");
    	if(map.containsKey("grafiekYAsNaam"))
    		grafiekYAsNaam = map.getString("grafiekYAsNaam");
    	if(map.containsKey("selectnummer"))
    		selectnummer = map.getInt("selectnummer");
    	if(map.containsKey("beginwaarde"))
    		beginwaarde = map.getInt("beginwaarde");
    	if(map.containsKey("ingevuld"))
    		ingevuld = map.getBoolean("ingevuld");
    	if(map.containsKey("nagekeken"))
    		nagekeken = map.getBoolean("nagekeken");
    	if(map.containsKey("puntenNagekeken"))
    		puntenNagekeken = map.getBoolean("puntenNagekeken");
	    if (map.containsKey("errorCount")) 
	    	this.errorCount = map.getInt("errorCount");
    	
    	this.beginx = beginx;
    	this.beginy = beginy;
    	this.schaalFactorX = schaalFactorX;
    	this.schaalFactorY = schaalFactorY;
    	
    	this.activeIndex = activeIndex;
    	this.grafiekXAsNaam = grafiekXAsNaam;
    	this.grafiekYAsNaam = grafiekYAsNaam;
    	this.selectnummer = selectnummer;
    	this.beginwaarde = beginwaarde;
    	this.ingevuld = ingevuld;
    	this.nagekeken = nagekeken;
    	
    	//xAsNaamTF.setText(grafiekXAsNaam);
    	//yAsNaamTF.setText(grafiekYAsNaam);
    	    	
    /*	
    	if((typeOpdracht == TEKENTABELPUNTEN && graphPoints.size() >= docentGraphPoints.size())
				|| (typeOpdracht == TEKENPUNTENBIJFORMULE && graphPoints.size() >= kleinsteMinimum))
			kijkNaButton.setEnabled(true);
    	*/
    	
    	//int b = beginwaarde;
		//beginwaarde = 1-(int)Math.round(beginx/eenheidx);
		//selectnummer = selectnummer + b - beginwaarde;
    	
    	//zetXAsNaam(xAsNaam, true);
    	//zetYAsNaam(yAsNaam, true);
		tabelComponent.setState(h);

		tekenComponent.setState(h);
		
		formuleComponent.setState(h, null, null);
		veldComponent.setState(h);
		setActiveIndex(activeIndex, true);
		pointsChangedAction();
		
		setChanged(false);

		if ((mode != OpdrNavIF.ZELFTOETS && mode != OpdrNavIF.EINDTOETS) || nagekeken)
		{ 
			kijkNa(true /* show */, true /* wel setSate */);
		}
		else
		{
			if ((mode == OpdrNavIF.EINDTOETS))
			{
				kijkNa(false /* no show */, true /* wel setState */);
			}
		}
		
		if (!puntenNagekeken)
		{ 
			// Moet persé na kijkna worden gezet om de juiste visuele situatie te initialiseren.
  		    // Want bij kijkNa wordt deze boolean gereset voor normaal gebruik.
			pointsChangedAction();
		}
		
		grafiekGWTVeld.setState(h);
		grafiekGWTVeld.paint();
		
		fromuser = true;
		
		herlokeerSchuifParameters();  // Indien nodig :: Herpositioneer de schuifParamters
	}

    public void verhoogErrorCount()
    {
    	if (isChanged())
    		errorCount++;
    	setChanged(false);
    }
    
	public boolean isChanged()
	{
		return changed;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		// Dit komt na init()! --> init() is dummy, de echte init1() volgt na het inlezen van de globale instellingen
		
		FormuleKeyboardIF kb = comRoot.getKeyboard();
		FormuleClipboardIF clip = comRoot.getFormuleClipboard();
		FocusOnTouch.installKeyboard(kb, clip);
		FormuleHolder.installKeyboard(kb);
		
		// globale instellingen zetten
		try
		{
			ObjectMap wrap = comRoot.getConfiguration();
			
			// extract from DWOplayer XMLView.java
			if (wrap.containsKey("maalTeken"))
			{
				boolean maalTeken =  wrap.getBoolean("maalTeken");
				FormuleTeken.zetMaalTeken(maalTeken);
			}

			boolean diffOperatoren = false;
			if (wrap.containsKey("diffOperatoren"))
			{
				diffOperatoren = wrap.getBoolean("diffOperatoren");
				FormuleTeken.zetDiffOperatoren(diffOperatoren);
				FormuleParser.zetDiffOperatoren(diffOperatoren);
			}
			if(wrap.containsKey("hoekGraden"))
			{
				boolean hoekGraden = wrap.getBoolean("hoekGraden");
				Expressie.zetHoekGraden(hoekGraden);
			}
			if (wrap.containsKey("woordFormule"))
				FormuleParser.zetWoordFormule(wrap.getBoolean("woordFormule"));
			if (wrap.containsKey("tweeHoofdletterVar"))
				FormuleParser.zetTweeHoofdletterVariabele(wrap.getBoolean("tweeHoofdletterVar"));
			if (wrap.containsKey("significantie"))
				FormuleParser.zetSignificantie(wrap.getBoolean("significantie"));
		}
		catch (Exception e)
		{
			// er gaat iets mis bij het ophalen van globale settings
			logger.log(Level.WARNING, "Zet globale instellingen" , e);
		}

		// nu init uitvoeren incl. de globale instellingen die van invloed zijn
		init1(this.init_width, this.init_height, this.init_map, this.init_rondomValues);
		
		this.comRoot = comRoot;
		comRoot.addCBookEventListener("expression.1", this);
		comRoot.addCBookEventListener("expression.2", this);
		comRoot.addCBookEventListener("expression.3", this);
		comRoot.addCBookEventListener("expression.4", this);
		comRoot.addCBookEventListener("expression.5", this);

		comRoot.addCBookEventListener("equation.twoGraphs", this);
		comRoot.addCBookEventListener("equation.graph", this);
		comRoot.addCBookEventListener("double.parameter", this);
		comRoot.addCBookEventListener("double.trace", this);
		comRoot.addCBookEventListener("draw_functions", this);
		
		zetMode(comRoot.getMode());		
	}

	/**
	 * Calculate expressies met random variabelen.
	 */
	private void calculateExpressiesMetRandomVariabelen()
	{
		if(randomVarNamen != null)
		{	for (int pCnt = 0; pCnt < graphPoints.size(); pCnt++)
			{	RealPoint rp = (RealPoint) graphPoints.elementAt(pCnt);
				String xString = rp.getxString();
				String yString = rp.getyString();
										
				//vervangen door:
				try 
				{	xString = FormuleParser.randomizeString("$f" + xString + "@", randomVarNamen, randomVarWaarden);
				}
				catch(Exception e)
				{	xString = "";
				}
				Expressie ex = FormuleParser.geefExpressie(xString);
				if (ex != null)
					rp.setX(ex.geefWaarde());
				try 
				{	yString = FormuleParser.randomizeString("$f" + yString + "@", randomVarNamen, randomVarWaarden);
				}
				catch(Exception e)
				{	yString = "";
				}
				Expressie ey = FormuleParser.geefExpressie(yString);
				if (ey != null)
					rp.setY(ey.geefWaarde());
				
				// just in case
				if (Double.isNaN(rp.getX()))
					rp.setX(0);
				if (Double.isNaN(rp.getY()))
					rp.setY(0);
				
				rp.setIndex(rp.getIndex() % 100);
			}
		}
				
		if(randomVarNamen != null)
    	{	for (int pCnt = 0; pCnt < docentGraphPoints.size(); pCnt++)
			{	RealPoint rp = (RealPoint) docentGraphPoints.elementAt(pCnt);
				String xString = rp.getxString();
				String yString = rp.getyString();
								
				//vervangen door:
				try 
				{	xString = FormuleParser.randomizeString("$f" + xString + "@", randomVarNamen, randomVarWaarden);
				
				}
				catch(Exception e)
				{	xString = "";
				}
				Expressie ex = FormuleParser.geefExpressie(xString);
				if (ex != null)
					rp.setX(ex.geefWaarde());
				try 
				{	yString = FormuleParser.randomizeString("$f" + yString + "@", randomVarNamen, randomVarWaarden);
				}
				catch(Exception e)
				{	yString = "";
				}
				Expressie ey = FormuleParser.geefExpressie(yString);
				if (ey != null)
					rp.setY(ey.geefWaarde());
				
				// just in case
				if (Double.isNaN(rp.getX()))
					rp.setX(0);
				if (Double.isNaN(rp.getY()))
					rp.setY(0);
				
				rp.setIndex(rp.getIndex() % 100);
			}
    	}
	}

	/**
	 * Calculate docentdomeinen.
	 */
	private void calculateDocentDomeinen()
	{
		try
		{
			if (docentDomeinStrings != null)
			{
				for(int i = 0; i < docentDomeinStrings.length; i++)
				{	try
					{	docentDomeinStrings[i][0] = FormuleParser.randomizeString(docentDomeinStrings[i][0], randomVarNamen, randomVarWaarden);
					}
					catch(Exception e)
					{	docentDomeinStrings[i][0] = "$f" + Double.toString(DEFAULTDOMEIN[0]) + "@";
					}
					try
					{	docentDomeinStrings[i][1] = FormuleParser.randomizeString(docentDomeinStrings[i][1], randomVarNamen, randomVarWaarden);
					}
					catch(Exception e)
					{	docentDomeinStrings[i][1] = "$f" + Double.toString(DEFAULTDOMEIN[1]) + "@";
					}
					if(docentDomeinStrings[i][0].equals("$f" + Double.toString(DEFAULTDOMEIN[0]) + "@"))
					{	docentDomeinen[i][0] = DEFAULTDOMEIN[0];
						
					}
					else
						docentDomeinen[i][0] = FormuleParser.geefExpressie(docentDomeinStrings[i][0]).geefWaarde();
					if(docentDomeinStrings[i][1].equals("$f" + Double.toString(DEFAULTDOMEIN[1]) + "@"))
						docentDomeinen[i][1] = DEFAULTDOMEIN[1];
					else
						docentDomeinen[i][1] = FormuleParser.geefExpressie(docentDomeinStrings[i][1]).geefWaarde();	
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculate docentfuncties.
	 */
	private void calculateDocentFuncties()
	{
		if (docentFunctieStrings != null)
		{
			for(int i = 0; i < docentFunctieStrings.length; i++)
			{
				try         
	        	{
					docentFunctieStrings[i] = FormuleParser.randomizeString(docentFunctieStrings[i],randomVarNamen,randomVarWaarden);
	        	}
	        	catch(Exception e)
	        	{
	        		docentFunctieStrings[i] = "$f???@";
	        	}

				if (!docentFunctieStrings[i].equals("$f@"))
				{
					docentFuncties[i] = FormuleParser.geefExpressie(docentFunctieStrings[i]);
					aantalFuncties++;
				}
			}
		}
	}
	@Override
	public Widget asWidget() {
		return basisPanel;
	}
	
	public void maakStandaardKleuren()
	{
		opdrachtKleuren = new String[10];
		gewoneKleuren = new String[10];
		colors = new String[10];
	    
		String zwart = CssColor.make(0, 0, 0).value();
		opdrachtKleuren[0] = CssColor.make(0,0,255).value();
		opdrachtKleuren[1] = CssColor.make(00,220,220).value();
		opdrachtKleuren[2] = CssColor.make(220,0,220).value();
		opdrachtKleuren[3] = CssColor.make(200,200,0).value();
		opdrachtKleuren[4] = zwart;
		opdrachtKleuren[5] = zwart;
		opdrachtKleuren[6] = zwart;
		opdrachtKleuren[7] = zwart;
		opdrachtKleuren[8] = zwart;
		opdrachtKleuren[9] = zwart;
		
		gewoneKleuren[0] = CssColor.make(0,0,255).value();
		gewoneKleuren[1] = CssColor.make(0,200,0).value();
		gewoneKleuren[2] = CssColor.make(255,50,50).value();
		gewoneKleuren[3] = CssColor.make(00,220,220).value();
		gewoneKleuren[4] = CssColor.make(220,0,220).value();
		gewoneKleuren[5] = CssColor.make(200,200,0).value();
		gewoneKleuren[6] = zwart;
		gewoneKleuren[7] = zwart;
		gewoneKleuren[8] = zwart;
		gewoneKleuren[9] = zwart;
		
		for(int i = 0; i < colors.length; i++)
			colors[i] = gewoneKleuren[i];
	}

	@Override
	public void init(int width, int height, Map<String, Object> map, Map<String, Number> randomValues)
	{
		this.init_width = width; 
		this.init_height = height;
		this.init_map = map;
		this.init_rondomValues = randomValues;
	}
	
	public void init1(int width, int height, Map<String, Object> map, 
			Map<String, Number> randomValues)
	{
		if (randomValues instanceof HashMap)
			this.randomVarWaarden = (HashMap) randomValues;
		else
		{
			this.randomVarWaarden = new HashMap(randomValues);
		}
		this.randomVarNamen = new String[randomVarWaarden.size()];
		this.randomVarNamen = (String[]) randomVarWaarden.keySet().toArray(this.randomVarNamen);

		breedte = width - 2 * offset;
		hoogte = height;
		// logger.finest("launchData: " + map);
		maakStandaardKleuren();
		// veldb = breedte - 2 * offset;
		ObjectMap launchData = JSONUtilities.wrapMap(map);
		launchState = map;
		if (launchData != null)
		{
			if (launchData.containsKey("beginxDocent"))
				beginxDocent = launchData.getDouble("beginxDocent");
			if (launchData.containsKey("beginyDocent"))
				beginyDocent = launchData.getDouble("beginyDocent");
			if (launchData.containsKey("beginx"))
				beginx = launchData.getDouble("beginx");
			if (launchData.containsKey("beginy"))
				beginy = launchData.getDouble("beginy");
			if (launchData.containsKey("beginwaarde"))
				beginwaarde = launchData.getInt("beginwaarde");
			if (launchData.containsKey("selectnummer"))
				selectnummer = launchData.getInt("selectnummer");
			// tracexD nog toevoegen?
			if (launchData.containsKey("eenheidx"))
				eenheidx = launchData.getInt("eenheidx");
			if (launchData.containsKey("eenheidy"))
				eenheidy = launchData.getInt("eenheidy");
			if (launchData.containsKey("eenheidxD"))
				eenheidxD = launchData.getDouble("eenheidxD");
			if (launchData.containsKey("eenheidyD"))
				eenheidyD = launchData.getDouble("eenheidyD");
			if (launchData.containsKey("veldx"))
				veldx = launchData.getInt("veldx");
			if (launchData.containsKey("veldy"))
				veldy = launchData.getInt("veldy");
			if (launchData.containsKey("docentSchaalFactorX"))
				docentSchaalFactorX = launchData.getDouble("docentSchaalFactorX");
			if (launchData.containsKey("docentSchaalFactorY"))
				docentSchaalFactorY = launchData.getDouble("docentSchaalFactorY");
			if (launchData.containsKey("schaalFactorX"))
				schaalFactorX = launchData.getDouble("schaalFactorX");
			if (launchData.containsKey("schaalFactorY"))
				schaalFactorY = launchData.getDouble("schaalFactorY");
			if (launchData.containsKey("factorRijNummerX"))
				factorRijNummerX = launchData.getInt("factorRijNummerX");
			if (launchData.containsKey("factorRijNummerY"))
				factorRijNummerY = launchData.getInt("factorRijNummerY");
			if (launchData.containsKey("xAsNaam"))
				xAsNaam = launchData.getString("xAsNaam");
			if (launchData.containsKey("yAsNaam"))
				yAsNaam = launchData.getString("yAsNaam");
			if (launchData.containsKey("grafiekXAsNaam"))
				grafiekXAsNaam = launchData.getString("grafiekXAsNaam");
			if (launchData.containsKey("grafiekYAsNaam"))
				grafiekYAsNaam = launchData.getString("grafiekYAsNaam");

			if (launchData.containsKey("assenZichtbaar"))
				assenZichtbaar = launchData.getBoolean("assenZichtbaar");
			if (launchData.containsKey("roosterZichtbaar"))
				roosterZichtbaar = launchData.getBoolean("roosterZichtbaar");
			if (launchData.containsKey("roosterGrof"))
				roosterGrof = launchData.getBoolean("roosterGrof");
			if (launchData.containsKey("roosterX"))
				roosterX = launchData.getBoolean("roosterX");
			if (launchData.containsKey("roosterY"))
				roosterY = launchData.getBoolean("roosterY");
			if (launchData.containsKey("schaalZichtbaar"))
				schaalZichtbaar = launchData.getBoolean("schaalZichtbaar");
			if (launchData.containsKey("schaalX"))
				schaalX = launchData.getBoolean("schaalX");
			if (launchData.containsKey("schaalY"))
				schaalY = launchData.getBoolean("schaalY");
			if (launchData.containsKey("piLijnenZichtbaar"))
				piLijnenZichtbaar = launchData.getBoolean("piLijnenZichtbaar");
			if (launchData.containsKey("xPositief"))
				xPositief = launchData.getBoolean("xPositief");
			if (launchData.containsKey("yPositief"))
				yPositief = launchData.getBoolean("yPositief");
			if (launchData.containsKey("xAsLog"))
				xAsLog = launchData.getBoolean("xAsLog");
			if (launchData.containsKey("yAsLog"))
				yAsLog = launchData.getBoolean("yAsLog");

			if (xAsLog)
			{
				eenheidxD = eenheidxD * 2;
				eenheidx = (int) Math.round(eenheidxD);
			}
			if (yAsLog)
			{
				eenheidyD = eenheidyD * 2;
				eenheidy = (int) Math.round(eenheidyD);
			}

			if (launchData.containsKey("asDefXMin"))
				asDefXMin = launchData.getDouble("asDefXMin");
			if (launchData.containsKey("asDefXMax"))
				asDefXMax = launchData.getDouble("asDefXMax");
			if (launchData.containsKey("asDefXStap"))
				asDefXStap = launchData.getDouble("asDefXStap");
			if (launchData.containsKey("asDefYMin"))
				asDefYMin = launchData.getDouble("asDefYMin");
			if (launchData.containsKey("asDefYMax"))
				asDefYMax = launchData.getDouble("asDefYMax");
			if (launchData.containsKey("asDefYStap"))
				asDefYStap = launchData.getDouble("asDefYStap");

			if (launchData.containsKey("manualScalingX"))
				manualScalingX = launchData.getBoolean("manualScalingX");
			if (launchData.containsKey("manualScalingY"))
				manualScalingY = launchData.getBoolean("manualScalingY");

			if (launchData.containsKey("xVarEditable"))
				xVarEditable = launchData.getBoolean("xVarEditable");
			if (launchData.containsKey("yVarEditable"))
				yVarEditable = launchData.getBoolean("yVarEditable");
			if (launchData.containsKey("snapToGridPoints"))
				snapToGridPoints = launchData.getBoolean("snapToGridPoints");
			if (launchData.containsKey("rechteVerbindingen"))
				rechteVerbindingen = launchData.getBoolean("rechteVerbindingen");
			if (launchData.containsKey("krommeZonderExtrapolatie"))
				krommeZonderExtrapolatie = launchData.getBoolean("krommeZonderExtrapolatie");
			if (launchData.containsKey("krommeMetExtrapolatie"))
				krommeMetExtrapolatie = launchData.getBoolean("krommeMetExtrapolatie");
			if (launchData.containsKey("zoomInTabel"))
				zoomInTabel = launchData.getBoolean("zoomInTabel");
			if (launchData.containsKey("zoomOptie"))
				zoomOptie = launchData.getBoolean("zoomOptie");
			if (launchData.containsKey("traceOptie"))
				traceOptie = launchData.getBoolean("traceOptie");
			if (launchData.containsKey("dragOptie"))
				dragOptie = launchData.getBoolean("dragOptie");
			if (launchData.containsKey("grafiekKleuren"))
				grafiekKleuren = launchData.getBoolean("grafiekKleuren");
			if (launchData.containsKey("kleurInstelbaar"))
				kleurInstelbaar = launchData.getBoolean("kleurInstelbaar");
			if (launchData.containsKey("functieBeginZichtbaar"))
				functieBeginZichtbaar = launchData.getBoolean("functieBeginZichtbaar");
			if (launchData.containsKey("functieBeginAanpasbaar"))
				functieBeginAanpasbaar = launchData.getBoolean("functieBeginAanpasbaar");
			if (launchData.containsKey("formeleFuncties"))
				formeleFuncties = launchData.getBoolean("formeleFuncties");
			if (launchData.containsKey("domeinInstelbaar"))
				domeinInstelbaar = launchData.getBoolean("domeinInstelbaar");
			if (launchData.containsKey("formuleComponentHoogte"))
				formuleComponentHoogte = launchData.getInt("formuleComponentHoogte");

			if (launchData.containsKey("functieToegestaan"))
				functieToegestaan = launchData.getBoolean("functieToegestaan");
			if (launchData.containsKey("ongelijkheidToegestaan"))
				ongelijkheidToegestaan = launchData.getBoolean("ongelijkheidToegestaan");
			if (launchData.containsKey("implicieteFunctieToegestaan"))
				implicieteFunctieToegestaan = launchData.getBoolean("implicieteFunctieToegestaan");
			if (launchData.containsKey("verticaleLijnToegestaan"))
				verticaleLijnToegestaan = launchData.getBoolean("verticaleLijnToegestaan");
			if (launchData.containsKey("parametrisatieToegestaan"))
				parametrisatieToegestaan = launchData.getBoolean("parametrisatieToegestaan");

			if (launchData.containsKey("formuleComponentAan"))
				formuleComponentAan = launchData.getBoolean("formuleComponentAan");
			if (launchData.containsKey("veldComponentAan"))
				veldComponentAan = launchData.getBoolean("veldComponentAan");

			/* veldComponent Parameters */
			if (launchData.containsKey("veldGrafiekType"))
				veldGrafiekType = VeldComponentGWT.FieldGraphType
					.values()[((Integer) launchData.getInt("veldGrafiekType"))];
			if (launchData.containsKey("veldPijlGrootteModus"))
				veldPijlGrootteModus = VeldComponentGWT.FieldGraphArrowSizeMode
					.values()[((Integer) launchData.getInt("veldPijlGrootteModus"))];
			if (launchData.containsKey("veldPijlGroottePixels"))
				veldPijlGroottePixels = ((Integer) launchData.getInt("veldPijlGroottePixels"));
			if (launchData.containsKey("veldPijlSchaalfactor"))
				veldPijlSchaalfactor = ((Double) launchData.getDouble("veldPijlSchaalfactor"));
			if (launchData.containsKey("veldLargerGridStartPoints"))
				veldLargerGridStartPoints = ((Boolean) launchData.getBoolean("veldLargerGridStartPoints"));

			if (launchData.containsKey("veldComponentHoogte"))
				veldComponentHoogte = launchData.getInt("veldComponentHoogte");
			/* veldComponent Parameters */

			if (launchData.containsKey("tekenComponentAan"))
				tekenComponentAan = launchData.getBoolean("tekenComponentAan");
			if (launchData.containsKey("tabelComponentAan"))
				tabelComponentAan = launchData.getBoolean("tabelComponentAan");
			if (launchData.containsKey("tabelAlsTekenTool"))
				tabelAlsTekenTool = launchData.getBoolean("tabelAlsTekenTool");
			if (launchData.containsKey("activeIndex"))
				activeIndex = launchData.getInt("activeIndex");
			if (launchData.containsKey("tekenGrafiekNauwkeurigheid"))
				tekenGrafiekNauwkeurigheid = launchData.getInt("tekenGrafiekNauwkeurigheid");

			double[] graphPointsX = null;
			double[] graphPointsY = null;
			int[] graphPointsIndex = null;
			int[] graphPointsTabelIndex = null;
			String[] graphPointsXString = null;
			String[] graphPointsYString = null;

			if (launchData.containsKey("graphPointsX"))
				graphPointsX = launchData.getDoubleArray("graphPointsX");
			if (launchData.containsKey("graphPointsY"))
				graphPointsY = launchData.getDoubleArray("graphPointsY");
			if (launchData.containsKey("graphPointsIndex"))
				graphPointsIndex = launchData.getIntArray("graphPointsIndex");
			if (launchData.containsKey("graphPointsTabelIndex"))
				graphPointsTabelIndex = launchData.getIntArray("graphPointsTabelIndex");
			if (launchData.containsKey("graphPointsXString"))
				graphPointsXString = launchData.getStringArray("graphPointsXString");
			if (launchData.containsKey("graphPointsYString"))
				graphPointsYString = launchData.getStringArray("graphPointsYString");

			this.graphPoints = new Vector();
			if (graphPointsX != null)
			{
				for (int i = 0; i < graphPointsX.length; i++)
				{
					RealPoint rp = new RealPoint(graphPointsX[i], graphPointsY[i]);
					rp.setIndex(graphPointsIndex[i]);
					rp.setTabelIndex(graphPointsTabelIndex[i]);
					rp.setxString(graphPointsXString[i]);
					rp.setyString(graphPointsYString[i]);
					graphPoints.add(rp);
				}
			}

			String[] paramNamen = null;
			double[] paramWaarden = null;
			double[] paramOnderGrensWaarden = null;
			double[] paramBovenGrensWaarden = null;
			double[] paramStapGroottes = null;
			int[] paramLengtes = null;
			boolean[] paramHideSlider = null;
			int[] paramX = null;
			int[] paramY = null;

			if (launchData.containsKey("paramNamen"))
				paramNamen = launchData.getStringArray("paramNamen");
			if (launchData.containsKey("paramWaarden"))
				paramWaarden = launchData.getDoubleArray("paramWaarden");
			if (launchData.containsKey("paramOnderGrensWaarden"))
				paramOnderGrensWaarden = launchData.getDoubleArray("paramOnderGrensWaarden");
			if (launchData.containsKey("paramBovenGrensWaarden"))
				paramBovenGrensWaarden = launchData.getDoubleArray("paramBovenGrensWaarden");
			if (launchData.containsKey("paramStapGroottes"))
				paramStapGroottes = launchData.getDoubleArray("paramStapGroottes");
			if (launchData.containsKey("paramLengtes"))
				paramLengtes = launchData.getIntArray("paramLengtes");
			if (launchData.containsKey("paramHideSlider"))
				paramHideSlider = launchData.getBooleanArray("paramHideSlider");
			if (launchData.containsKey("paramX"))
				paramX = launchData.getIntArray("paramX");
			if (launchData.containsKey("paramY"))
				paramY = launchData.getIntArray("paramY");

			if (paramNamen != null)
			{
				int maxX = breedte;
				int maxY = hoogte;

				this.schuifParameters = new SchuifParameterGWT[paramNamen.length];
				for (int i = 0; i < schuifParameters.length; i++)
				{
					schuifParameters[i] = new SchuifParameterGWT(paramLengtes[i], paramNamen[i]);
					schuifParameters[i].zetGrensWaarden(paramOnderGrensWaarden[i], paramBovenGrensWaarden[i]);
					schuifParameters[i].zetStapGrootte(paramStapGroottes[i]);
					schuifParameters[i].zetWaarde(paramWaarden[i]);
					schuifParameters[i].zetLocatie(paramX[i], paramY[i]);
					if(paramHideSlider!=null)	
						schuifParameters[i].zetHideSlider(paramHideSlider[i]);
				}
			}

			if (launchData.containsKey("colorRGBsGewoon"))
			{
				ObjectList list = launchData.getObjectList("colorRGBsGewoon");
				int[][] colorRGBsGewoon = new int[list.size()][];
				for (int i = 0; i < colorRGBsGewoon.length; i++)
				{
					colorRGBsGewoon[i] = list.getIntArray(i);
					gewoneKleuren[i] = CssColor
						.make(colorRGBsGewoon[i][0], colorRGBsGewoon[i][1], colorRGBsGewoon[i][2]).value();
				}
			}

			if (launchData.containsKey("colorRGBsOpdrachten"))
			{
				ObjectList list = launchData.getObjectList("colorRGBsOpdrachten");
				int[][] colorRGBsOpdrachten = new int[list.size()][];
				for (int i = 0; i < colorRGBsOpdrachten.length; i++)
				{
					colorRGBsOpdrachten[i] = list.getIntArray(i);
					opdrachtKleuren[i] = CssColor
						.make(colorRGBsOpdrachten[i][0], colorRGBsOpdrachten[i][1], colorRGBsOpdrachten[i][2]).value();
				}
			}

			if (launchData.containsKey("typeOpdracht"))
				typeOpdracht = launchData.getInt("typeOpdracht");
			if (launchData.containsKey("maxScores"))
			{
				maxScores = launchData.getIntArray("maxScores");
			}
			if (launchData.containsKey("docentFunctieStrings"))
			{ // docentFunctieStrings =
				// JSONUtilities.toStringArray(launchData.get("docentFunctieStrings"));
				docentFunctieStrings = launchData.getStringArray("docentFunctieStrings");
				if (docentFunctieStrings != null)
					docentFuncties = new Expressie[docentFunctieStrings.length];
				
				calculateDocentFuncties();
			}
			if (launchData.containsKey("docentDomeinStrings"))
			{
				ObjectList docentDomeinStringList = (launchData.getObjectList("docentDomeinStrings"));
				docentDomeinStrings = new String[docentDomeinStringList.size()][2];
				docentDomeinen = new double[docentDomeinStringList.size()][2];
				for (int i = 0; i < docentDomeinStringList.size(); i++)
				{
					docentDomeinStrings[i] = (docentDomeinStringList.getStringArray(i));
				}
				calculateDocentDomeinen();
			}
			if (launchData.containsKey("nauwkeurigheid"))
			{
				nauwkeurigheid = launchData.getIntArray("nauwkeurigheid");
			}
			if (launchData.containsKey("minimumPunten"))
			{
				minimumPunten = launchData.getIntArray("minimumPunten");
			}
			if (minimumPunten != null)
			{
				kleinsteMinimum = minimumPunten[0];
				for (int i = 1; i < aantalFuncties; i++)
				{
					if (minimumPunten[i] < kleinsteMinimum)
						kleinsteMinimum = minimumPunten[i];
				}
			}

			if (launchData.containsKey("scoreMax"))
				scoreMax = launchData.getInt("scoreMax");
			if (launchData.containsKey("domeinControleren"))
				domeinControleren = launchData.getBoolean("domeinControleren");
			if (launchData.containsKey("leerlingZietTabel"))
				leerlingZietTabel = launchData.getBoolean("leerlingZietTabel");
			if (launchData.containsKey("ingevuld"))
				ingevuld = launchData.getBoolean("ingevuld");
			if (launchData.containsKey("nagekeken"))
				nagekeken = launchData.getBoolean("nagekeken");
			if (launchData.containsKey("checkExternal"))
				checkExternal = launchData.getBoolean("checkExternal");

			double[] docentGraphPointsX = null;
			double[] docentGraphPointsY = null;
			int[] docentGraphPointsIndex = null;
			int[] docentGraphPointsTabelIndex = null;
			String[] docentGraphPointsXString = null;
			String[] docentGraphPointsYString = null;

			if (launchData.containsKey("docentGraphPointsX"))
				docentGraphPointsX = launchData.getDoubleArray("docentGraphPointsX");
			if (launchData.containsKey("docentGraphPointsY"))
				docentGraphPointsY = launchData.getDoubleArray("docentGraphPointsY");
			if (launchData.containsKey("docentGraphPointsIndex"))
				docentGraphPointsIndex = launchData.getIntArray("docentGraphPointsIndex");
			if (launchData.containsKey("docentGraphPointsTabelIndex"))
				docentGraphPointsTabelIndex = launchData.getIntArray("docentGraphPointsTabelIndex");
			if (launchData.containsKey("docentGraphPointsXString"))
				docentGraphPointsXString = launchData.getStringArray("docentGraphPointsXString");
			if (launchData.containsKey("docentGraphPointsYString"))
				docentGraphPointsYString = launchData.getStringArray("docentGraphPointsYString");

			this.docentGraphPoints = new Vector();
			if (docentGraphPointsX != null)
			{
				for (int i = 0; i < docentGraphPointsX.length; i++)
				{
					RealPoint rp = new RealPoint(docentGraphPointsX[i], docentGraphPointsY[i]);
					rp.setIndex(docentGraphPointsIndex[i]);
					rp.setTabelIndex(docentGraphPointsTabelIndex[i]);
					rp.setxString(docentGraphPointsXString[i]);
					rp.setyString(docentGraphPointsYString[i]);
					docentGraphPoints.add(rp);
				}
			}

			calculateExpressiesMetRandomVariabelen();

			logOption = Boolean.TRUE.equals(launchData.get("logOption"));
			attempt = logOption || launchData.containsKey("smObjectives");	
		}

		// initialize GUI nadat de launchdata is verwerkt
		initialize();
		fromuser = true;
	}
	
	private SchuifParameterGWT geefSchuifParameter(String name) {
		for (int i=0; i<schuifParameters.length; i++) {
			if(name.equals(schuifParameters[i].geefNaam()))
				return schuifParameters[i];
		}
		return null;
	}
	
	private void herlokeerSchuifParameters() {
		// Deze procedure controleert de positie van de schuifparameters tegen de grootte van het grafiekveld
		// Wanneer de schuifparameter niet past wordt hij naar een standaard-positie verplaatst
		
		if (schuifParameters != null) { // er zijn schuifParameters
			
			int marge = 0;//SliderGWT.cDefault_x; // contstante (minimale afstant tot een rand)
			int initY = SliderGWT.cDefault_y;
			int afstand = SliderGWT.cDefault_distance; // constante (minimale afstand tussen schuifparameters)
			int standaardPos = 0; // eerste standaard-positie
			int maxX = grafiekGWTVeld.breedte;
			int maxY = grafiekGWTVeld.hoogte;
			
			for (int i=0; i<schuifParameters.length; i++) {
				int[] positie = schuifParameters[i].geefPositie();
				int lengte = schuifParameters[i].geefLengte();
				
				if ( (positie == null) ||
					 (positie[0] - marge < 0) ||   // positie[0] = X
					 (positie[0] + marge + lengte > maxX) ||
					 (positie[1] - marge < 0) ||   // positie[1] = Y
					 (positie[1] + marge > maxY)
				   ) { // Wijzig positie
					int newPosX = marge;
					int newPosY = initY + standaardPos * afstand;
					schuifParameters[i].zetLocatie(newPosX, newPosY);
					standaardPos++;
				}
			}
			if (standaardPos > 0) {
				grafiekGWTVeld.paint();
			}
		}
		
//		this.schuifParameters = new SchuifParameterGWT[paramNamen.length];
//		for(int i = 0; i < schuifParameters.length; i++) {	
//			schuifParameters[i] = new SchuifParameterGWT(paramLengtes[i], paramNamen[i]);
//			schuifParameters[i].zetGrensWaarden(paramOnderGrensWaarden[i], paramBovenGrensWaarden[i]);
//			schuifParameters[i].zetStapGrootte(paramStapGroottes[i]);
//			schuifParameters[i].zetWaarde(paramWaarden[i]);
//			schuifParameters[i].zetLocatie(paramX[i], paramY[i]);
//		}
	}
	
	
	
	private static List toList(Object object) {
		if(object instanceof List )
		{	return (List) object;
		}
		else if(object instanceof Object[])
		{
			Object[] objects = (Object[]) object;
			return Arrays.asList(objects);
		}
		return null;
	}

	public RealPoint pixelsToRealPoint(Point pix) {	
		RealPoint rp = new RealPoint(0, 0);
		if (manualScalingX) {
			rp.setX(eenheidxValue * (-beginx)/eenheidxD + eenheidxValue * pix.getX() / eenheidxD);			
		} else {
			rp.setX(schaalFactorX * (-beginx)/eenheidxD + schaalFactorX * pix.getX() / eenheidxD);
			if(xAsLog)
				rp.setX(Math.pow(10, rp.getX()));
		}
		if (manualScalingY) {
			rp.setY((eenheidyValue * (-beginy) / eenheidyD +
					eenheidyValue * (grafiekGWTVeld.hoogte - pix.getY()) / eenheidyD));
		} else {
			rp.setY((schaalFactorY * (-beginy) / eenheidyD +
				    schaalFactorY * (grafiekGWTVeld.hoogte - pix.getY()) / eenheidyD));
			if(yAsLog)
				rp.setY(Math.pow(10, rp.getY()));
		}
		rp.setxString(Double.toString(rp.getX()));
		rp.setyString(Double.toString(rp.getY()));
		return rp;
	}
	
	public int geefEersteVrijeVak(boolean docent)
	{	int vakIndex = 0;
		int laagsteVakIndex = 0;
		int hoogsteVakIndex = 1;
		Vector points = getPoints(activeIndex, docent);
		if(points.size() == 0)
			return vakIndex;
		for (int pCnt = 0; pCnt < points.size(); pCnt++)
		{	RealPoint rp = (RealPoint) points.elementAt(pCnt);
			if(rp.getTabelIndex() < laagsteVakIndex)
				laagsteVakIndex = rp.getTabelIndex();
			if(rp.getTabelIndex() > hoogsteVakIndex)
				hoogsteVakIndex = rp.getTabelIndex();
		}
		boolean[] kandidaat = new boolean[hoogsteVakIndex - laagsteVakIndex + 2];
		for(int i = 0; i < kandidaat.length; i++)
			kandidaat[i] = true;
		for (int pCnt = 0; pCnt < points.size(); pCnt++)
		{	RealPoint rp = (RealPoint) points.elementAt(pCnt);
			kandidaat[rp.getTabelIndex() - laagsteVakIndex] = false;
		}
		for(int i = 0; i < kandidaat.length; i++)
		{	if(kandidaat[i])
			{	vakIndex = laagsteVakIndex + i;
				break;
			}
		}
		return vakIndex;	
	}
	
	public int closestFreePixX(double pressedX)
	{	
		Vector<RealPoint> points = getPoints(getActiveIndex(), false);

		// check pressedX en zoek naar links	
		boolean found = false;	
		int firstFreeXLeft = - 1;				
		for (int lCnt = (int)pressedX; lCnt >= 0; lCnt--)
		{	// nog geen gevonden
			if (!found)
			{	found = true;
				// ga door de punten heen	
				for (int pCnt = 0; pCnt < points.size(); pCnt++)
				{	RealPoint rp = (RealPoint) points.elementAt(pCnt);
					Point rpPix = realPointToPixels(rp);
					found = found && (rpPix.getX() != lCnt);
				}
				if (found)
					firstFreeXLeft = lCnt;
			}	
		}
		// klaar!
		if (firstFreeXLeft == pressedX)
			return firstFreeXLeft;
			
		// zoek nu rechts
		found = false;	
		int firstFreeXRight = - 1;				
		for (int rCnt = (int)pressedX + 1; rCnt <= breedte; rCnt++)
		{	// nog geen gevonden
			if (!found)
			{	found = true;
				// ga door de punten heen	
				for (int pCnt = 0; pCnt < points.size(); pCnt++)
				{	RealPoint rp = (RealPoint) points.elementAt(pCnt);
					Point rpPix = realPointToPixels(rp);
					found = found && (rpPix.getX() != rCnt);
				}
				if (found)
					firstFreeXRight = rCnt;
			}	
		}
			
		if ((firstFreeXLeft == -1) && (firstFreeXRight == -1))
			return -1;
		else if ((firstFreeXLeft == -1) && (firstFreeXRight >= 0))		
			return firstFreeXRight;
		else if ((firstFreeXLeft >= 0) && (firstFreeXRight == -1))			
			return firstFreeXLeft;
		else if ((firstFreeXLeft >= 0) && (firstFreeXRight >= 0))
		{	if ((pressedX - firstFreeXLeft) < (firstFreeXRight - pressedX))
				return firstFreeXLeft;
			else
				return firstFreeXRight;	
		}				
		else 
			return -1;
	}
	
	public double closestGridPix(double pressedX, boolean xAs)
	{	//int bx = (int)Math.round(beginx);			
		//int by = (int)Math.round(beginy);
		
		
		
		int imin = -(int)Math.round(beginx/eenheidx); 
		int imax = 1+grafiekGWTVeld.breedte/eenheidx-(int)Math.round(beginx/eenheidx);
		int jmin = -(int)Math.round(beginy/eenheidy); 
		int jmax = 1+grafiekGWTVeld.hoogte/eenheidy-(int)Math.round(beginy/eenheidy);
		
		if(xAs)
		{	for(int i=imin+1 ; i<imax ; i++)
			{	if((!xPositief || i>0) && i%((roosterGrof && !xAsLog)?2:1)==0)  
				{	if(Math.abs(pressedX - (beginx+i*eenheidxD)) <= eenheidxD/2)
					{	pressedX = beginx+i*eenheidxD;
						break;
					}
				}
			}
		}
		else
		{	for(int j=jmin ; j<jmax ; j++)
			{	if((!yPositief || j>0) && j%((roosterGrof && !yAsLog)?2:1)==0) 
				{	if(Math.abs(pressedX - (grafiekGWTVeld.hoogte-(beginy+j*eenheidyD))) <= eenheidyD/2)
					{	pressedX = grafiekGWTVeld.hoogte-(beginy+j*eenheidyD);
						break;
					}
				}
			}
		}
		
		return pressedX;
	}
	
	public void mouseDownTouchStartAction(Object source, int eventX, int eventY, int pinchState)
	{	
		//mouseDown = true;
		//requestFocus(); //nodig?
		if (pinchState == TWO_FINGERS)
			return;

		boolean schuifParameterTouched = false;
		// Check schuifParameters
		if (schuifParameters != null)
		{	
			for (int i = 0; i < schuifParameters.length; i++)
			{	
				if(!schuifParameters[i].geefHideSlider())
					schuifParameterTouched = schuifParameterTouched || schuifParameters[i].mouseTouchPressed(eventX, eventY);;
			}
		}
		
		if (schuifParameterTouched)
		{
			// SchuifParameter is aangeraakt, verder hoeft er niets te gebeuren
			// grafiekGWTVeld.paint();
			return;
		}
	
		if ( eventX >= grafiekGWTVeld.xAsNaamLinks - cSelectMarge && eventX <= grafiekGWTVeld.xAsNaamRechts + cSelectMarge && 
			 eventY >= grafiekGWTVeld.xAsNaamBoven - cSelectMarge && eventY <= grafiekGWTVeld.xAsNaamOnder + cSelectMarge && xVarEditable)
		{
			showTekstPopup(true);
			return;
		}
		else if (eventX >= grafiekGWTVeld.yAsNaamLinks - cSelectMarge && eventX <= grafiekGWTVeld.yAsNaamRechts+cSelectMarge && 
				eventY >= grafiekGWTVeld.yAsNaamBoven - cSelectMarge && eventY <= grafiekGWTVeld.yAsNaamOnder +cSelectMarge && yVarEditable)
		{
			showTekstPopup(false);
			return;
		}
		if (!tekenComponentAan && source == grafiekGWTCanvas) 
		{
			startxv = eventX;
			startyv = eventY;
		}
		else if (tekenComponentAan && source == grafiekGWTCanvas)
		{	
			setChanged(true); // klopt dit altijd?
			
			if (typeOpdracht > GEENOPDRACHT)
			{
				setColor(activeIndex - 1, CssColor.make(opdrachtKleuren[activeIndex - 1]), false);//checken of dit nog +/- 1 moet.
			}
			double pressedX = eventX;
			double pressedY = eventY;
			
			if (tekenComponent.getCursorMode() == tekenComponent.NOCUR)
			{
				startxv = eventX;
				startyv = eventY;
			}
			else if (tekenComponent.getCursorMode() == tekenComponent.DRAW)
			{	

				// geklikt met rechter muisknop, dit is gummen
				/*
				if ((e.getModifiers() & e.BUTTON1_MASK) == 0)
	        	{	// kijk of er op een punt van de actuele grafiek is geklikt
					RealPoint drp = null;
					Vector points = getPoints(getActiveIndex(), false);
					{	for (int pCnt = 0; pCnt < points.size(); pCnt++)
						{	RealPoint rp = (RealPoint) points.elementAt(pCnt);
							Point rpPix = realPointToPixels(rp);
							int dis = (int) Math.round(
								Math.sqrt((rpPix.getX() - pressedX) * (rpPix.getX() - pressedX) +
										  (rpPix.getY() - pressedY) * (rpPix.getY() - pressedY)));
							if (dis <= PRAD + 2)
							{	drp = rp;
							}
						}
					}
					if (drp != null)
					{	removePoint(drp.tabelIndex, drp.index, false);
					
					grafiekGWTVeld.paint();
					}
	
	        	}
	        	*/
	        	// geklikt met een andere muisknop
	        	//else	
	        	//{	// kijk of er op een point van de actuele grafiek is geklikt
					// dat gaan we dan slepen
					dragPoint = null;
					Vector points = getPoints(getActiveIndex(), false);
					{	for (int pCnt = 0; pCnt < points.size(); pCnt++)
						{	RealPoint rp = (RealPoint) points.elementAt(pCnt);
							Point rpPix = realPointToPixels(rp);
							int dis = (int) Math.round(
								Math.sqrt((rpPix.getX() - pressedX) * (rpPix.getX() - pressedX) +
										  (rpPix.getY() - pressedY) * (rpPix.getY() - pressedY)));
							if (dis <= cPointRadius + 1)
							{	dragPoint = rp;
							}
						}
					}

					// dragPoint slepen
					if (dragPoint != null) {	
						startxv = eventX;
						startyv = eventY;
					}	
	       			else { // tekenen 

	       				if(snapToGridPoints)
	       				{	pressedX = closestGridPix(pressedX, true);
	       					pressedY = closestGridPix(pressedY, false);
	       				}
	       				int freePixX = closestFreePixX(pressedX);						

						RealPoint newPoint = pixelsToRealPoint(
							new Point(freePixX, pressedY));
						newPoint.setIndex(getActiveIndex());
						newPoint.setTabelIndex(geefEersteVrijeVak(false));
						addInsert(newPoint, true);//, false);
						if(tabelAlsTekenTool)
							tabelComponent.vernieuwFirstIndexVisible(newPoint.getTabelIndex(), getActiveIndex());
						
						//repaint();
	       			} // tekenen
				//} // niet rechts geklikt
			} // DRAW
			else if (tekenComponent.getCursorMode() == tekenComponent.DELETE)
			{	
				// kijk of er op een punt van de actuele grafiek is geklikt
				RealPoint drp = null;
				Vector points = getPoints(getActiveIndex(), false);
				{	for (int pCnt = 0; pCnt < points.size(); pCnt++)
					{	RealPoint rp = (RealPoint) points.elementAt(pCnt);
						Point rpPix = realPointToPixels(rp);
						int dis = (int) Math.round(
							Math.sqrt((rpPix.getX() - pressedX) * (rpPix.getX() - pressedX) +
									  (rpPix.getY() - pressedY) * (rpPix.getY() - pressedY)));
						if (dis <= cPointRadius + 1)
						{	drp = rp;
						}
						
					}
				}
				if (drp != null)
				{	removePoint(drp.getTabelIndex(), drp.getIndex());//, false);
				
				}
			}
			else if (tekenComponent.getCursorMode() == tekenComponent.DRAG)
			{	// kijk of er op een punt van de actuele grafiek is geklikt
				dragPoint = null;
				otherPoint = null;
				Vector points = getPoints(getActiveIndex(), false);
				{	for (int pCnt = 0; pCnt < points.size(); pCnt++)
					{	RealPoint rp = (RealPoint) points.elementAt(pCnt);
						Point rpPix = realPointToPixels(rp);
						int dis = (int) Math.round(
							Math.sqrt((rpPix.getX() - pressedX) * (rpPix.getX() - pressedX) +
									  (rpPix.getY() - pressedY) * (rpPix.getY() - pressedY)));
						if (dis <= cPointRadius + 1)
						{	dragPoint = rp;
						}
						
					}
				}
				// als niet, kijk of er op een punt van een andere grafiek geklikt is
				if (dragPoint == null)
				{	for (int index = 1; index <= getNumGraphs(); index++)
					{	if (index != getActiveIndex())
						{	Vector indexPoints = getPoints(index, false);
							for (int pCnt = 0; pCnt < indexPoints.size(); pCnt++)
							{	RealPoint rp = (RealPoint) indexPoints.elementAt(pCnt);
								Point rpPix = realPointToPixels(rp);
								int dis = (int) Math.round(
									Math.sqrt((rpPix.getX() - pressedX) * (rpPix.getX() - pressedX) +
											  (rpPix.getY() - pressedY) * (rpPix.getY() - pressedY)));
								if (dis <= cPointRadius + 1)
								{	otherPoint = rp;
								}
						
							}
						}
					}
				}
				
				startxv = eventX;
				startyv = eventY;
			}
		}
	
		//slider:
		//misschien aanpassen nu ook schuifparameters erbij komen: raak toch weer in slider zelf laten bijhouden 
		//zoals ook in de gewone graphtool gebeurt.
		double  traceBase = Math.min(grafiekGWTVeld.drawYmax, Math.max( (grafiekVeldHoogte - beginy), grafiekGWTVeld.drawYmin));
		grafiekGWTVeld.sliderRaak = (eventX >= grafiekGWTVeld.stand - SliderGWT.cSelectMarge && eventX <= grafiekGWTVeld.stand + SliderGWT.cSelectMarge 
				&& eventY >= traceBase - SliderGWT.cSelectMarge && eventY <= traceBase + SliderGWT.cSelectMarge);
		if (grafiekGWTVeld.sliderRaak) {	
			grafiekGWTVeld.tracing = true;
			grafiekGWTVeld.tracex = grafiekGWTVeld.geefSliderStand();
			grafiekGWTVeld.tracexD = grafiekGWTVeld.tracex;
		}
		startxv = eventX;
		startyv = eventY;

		grafiekGWTVeld.paint();

	}
	
	public void mouseMoveTouchMoveAction(Object source, int eventX, int eventY, int pinchState, int startDistance, int newDistance, int direction)
	{	//if (!mouseDown)
		//return;
		moveActionActivated = true;

		boolean schuifParameterTouched = false;
		// Check schuifParameters
		if (schuifParameters != null) {	
			for(int i = 0; i < schuifParameters.length; i++) {	
				schuifParameterTouched = schuifParameterTouched || schuifParameters[i].mouseTouchMoved(eventX, eventY);;
			}
		}
		
		if (schuifParameterTouched) {
			// SchuifParameter is aangeraakt, verder hoeft er niets te gebeuren
			grafiekGWTVeld.paint();
			return;
		}
		
		if(zooming)
			return;
	
		if (pinchState == TWO_FINGERS && !zooming && Math.abs(newDistance - startDistance) > 5 && zoomOptie)
		{	zooming = true;
			if (newDistance > startDistance)//inzoomen
			{
				if(direction == HORIZONTAL)
					runZoom(true, false, true);
				else if(direction == VERTICAL)
					runZoom(false, true, true);
				else
					runZoom(true, true, true);
			}
			else if (newDistance < startDistance)
			{
				if(direction == HORIZONTAL)
					runZoom(true, false, false);
				else if(direction == VERTICAL)
					runZoom(false, true, false);
				else
					runZoom(true, true, false);
			}
			return;
		}
		
		/*
		if (tekenComponentAan && source != grafiekGWTCanvas || tekenComponent.getCursorMode() == tekenComponent.NOCUR)
		{	
			if (!dragOptie)
				return;
					
			int dx = eventX - startxv;
			int dy = eventY - startyv;
			beginx = beginx+dx;
			beginy = beginy-dy;
			if(traceOptie && tracex!=-2) 
			{	tracexD = tracexD+dx;
				tracex = tracex+dx;
				slider.zetStand(tracex);
			}
			
			int b = beginwaarde;
			beginwaarde = 1-(int)Math.round(beginx/eenheidx);
			selectnummer = selectnummer + b - beginwaarde;
			
			repaint();
			startxv = e.getX();
			startyv = e.getY();
		
		}
		*/
		
//		if(!grafiekGWTVeld.sliderRaak && (eventX >= grafiekGWTVeld.stand - 5 && eventX <= grafiekGWTVeld.stand + 5 
//			&& eventY >= grafiekVeldHoogte - beginy - 5 && eventY <= grafiekVeldHoogte - beginy + 5))
//			{	grafiekGWTVeld.sliderRaak = true;
//				//muisStartX = eventX;
//			if (grafiekGWTVeld.sliderRaak)
//			{	grafiekGWTVeld.tracing = true;
//				grafiekGWTVeld.tracex = grafiekGWTVeld.geefSliderStand();
//				grafiekGWTVeld.tracexD = grafiekGWTVeld.tracex;
//			}
//		}
//		logger.info("sliderRaak 2 = " + grafiekGWTVeld.sliderRaak);
		if (grafiekGWTVeld.sliderRaak )  { 
			if (grafiekGWTVeld.pixelsPointWithinBounds(eventX, eventY)) {	
				int x = eventX;
				int dx = x - startxv;
				
				grafiekGWTVeld.tracexD = grafiekGWTVeld.tracexD+dx;
				grafiekGWTVeld.tracex = grafiekGWTVeld.tracex+dx;
				grafiekGWTVeld.zetSliderStand(grafiekGWTVeld.tracex);
			
				startxv = x;
				startyv = eventY;
			}
		}
		else
		{	
			
			if ((dragPoint == null) && (otherPoint == null) && (dragOptie))
			{ // Move Graph if no point selected
				grafiekGWTVeld.setGrafiekSchuivenActief(true);
				int dx = eventX - startxv;
				int dy =  eventY - startyv;					
				beginx = beginx+dx;
				beginy = beginy-dy;
			
				int b = beginwaarde;
				beginwaarde = 1-(int)Math.round(beginx/eenheidx);
				selectnummer = selectnummer + b - beginwaarde;
			
				startxv = eventX;
				startyv = eventY;
			
				if(traceOptie && grafiekGWTVeld.tracex > 0) 
				{	grafiekGWTVeld.tracexD = grafiekGWTVeld.tracexD+dx;
					grafiekGWTVeld.tracex = grafiekGWTVeld.tracex+dx;
					grafiekGWTVeld.zetSliderStand(grafiekGWTVeld.tracex);
				}
			}
			
		}
		
		/*
		else if (tekenComponent.getCursorMode() == tekenComponent.NOCUR)
				{	
					if (!dragOptie)
						return;
						
					int dx = e.getX() - startxv;
					int dy = e.getY() - startyv;
					beginx = beginx+dx;
					beginy = beginy-dy;
					if(traceOptie && tracex!=-2) 
					{	tracexD = tracexD+dx;
						tracex = tracex+dx;
						slider.zetStand(tracex);
					}
					
					int b = beginwaarde;
					beginwaarde = 1-(int)Math.round(beginx/eenheidx);
					selectnummer = selectnummer + b - beginwaarde;
					
					repaint();
					startxv = e.getX();
					startyv = e.getY();
				}
				*/
		if (tekenComponentAan && tekenComponent.getCursorMode() == tekenComponent.DRAW)
		{	// punt slepen
			
			if (dragPoint != null)
			{	// schermpositie voor drag-event

				Point pix = realPointToPixels(dragPoint);
				int dx = eventX - startxv;
				int dy = eventY - startyv;	
				// schermpositie na drag-event
				Point dPix = new Point(pix.getX() + dx, pix.getY() + dy);
				RealPoint temp = pixelsToRealPoint(dPix);
				temp.setIndex(dragPoint.getIndex());
				if (hasPointWithSameXAs(temp))//, false))
				{	dragPoint.setX(temp.getX() + 2 * RealPoint.NZERO);					
				}
				else
				{	dragPoint.setX(temp.getX());										
				}	
				dragPoint.setY(temp.getY());
				dragPoint.setxString(Double.toString(dragPoint.getX()));
				dragPoint.setyString(Double.toString(dragPoint.getY()));
				removePoint(dragPoint.getTabelIndex(), dragPoint.getIndex());//, false);
				addInsert(dragPoint, true);//, false);
				
				
				startxv = eventX;
				startyv = eventY;
			}	
		}
		else if (tekenComponentAan && tekenComponent.getCursorMode() == tekenComponent.DRAG)
		{
			// punt slepen
			if (dragPoint != null)
			{	// schermpositie voor drag-event

				Point pix = realPointToPixels(dragPoint);
				int dx = eventX - startxv;
				int dy = eventY - startyv;	
				// schermpositie na drag-event
				Point dPix = new Point(pix.getX() + dx, pix.getY() + dy);
				RealPoint temp = pixelsToRealPoint(dPix);
				temp.setIndex(dragPoint.getIndex());
				if (hasPointWithSameXAs(temp))//, false))
				{	dragPoint.setX(temp.getX() + 2 * RealPoint.NZERO);															
				}
				else
				{	dragPoint.setX(temp.getX());										
				}	
				dragPoint.setY(temp.getY());
				dragPoint.setxString(Double.toString(dragPoint.getX()));
				dragPoint.setyString(Double.toString(dragPoint.getY()));
				removePoint(dragPoint.getTabelIndex(), dragPoint.getIndex());//, false);
				addInsert(dragPoint, true);//, false);					
				
				//repaint();
				
				startxv = eventX;
				startyv = eventY;
			}
			// grafiek slepen als drag==true
			else if ((dragPoint == null) && (otherPoint == null) && dragOptie) {
				grafiekGWTVeld.setGrafiekSchuivenActief(true);

				int dx = eventX - startxv;
				int dy = eventY - startyv;					
				beginx = beginx+dx;
				beginy = beginy-dy;
				
				if(traceOptie && grafiekGWTVeld.tracex!=-2) 
				{	grafiekGWTVeld.tracexD = grafiekGWTVeld.tracexD+dx;
					grafiekGWTVeld.tracex = grafiekGWTVeld.tracex+dx;
					grafiekGWTVeld.zetSliderStand(grafiekGWTVeld.tracex);
				}
				
				int b = beginwaarde;
				beginwaarde = 1-(int)Math.round(beginx/eenheidx);
				selectnummer = selectnummer + b - beginwaarde;
				
				//repaint();
				startxv = eventX;
				startyv = eventY;
			}
		}
		
		grafiekGWTVeld.paint();
	}
			
	/*
	if(!dragOptie)
				return;
			else if (e.getSource() == grafiekGWTVeld)//en niet de slider!
			{	int dx = eventX - startxv;
				int dy =  eventY - startyv;					
				beginx = beginx+dx;
				beginy = beginy-dy;
				
				if(traceOptie && tracex!=-2) 
				{	tracexD = tracexD+dx;
					tracex = tracex+dx;
					slider.zetStand(tracex);
				}
				
				int b = beginwaarde;
				beginwaarde = 1-(int)Math.round(beginx/eenheidx);
				selectnummer = selectnummer + b - beginwaarde;
				
				repaint();
				startxv = eventX;
				startyv = eventY;
			}
			
		repaint();
		}
		*/
	
	public void mouseUpTouchEndAction(Object source, int eventX, int eventY) {	
		moveActionActivated = false;
		grafiekGWTVeld.setGrafiekSchuivenActief(false);

		boolean schuifParameterTouched = false;
		// Check schuifParameters
		if (schuifParameters != null) {	
			for(int i = 0; i < schuifParameters.length; i++) {	
				schuifParameterTouched = schuifParameterTouched || schuifParameters[i].mouseTouchUp(eventX, eventY);;
			}
		}
		
		if (schuifParameterTouched) {
			// SchuifParameter is aangeraakt, verder hoeft er niets te gebeuren
			grafiekGWTVeld.paint();
			return;
		}
		
		if(zooming)
		{	zooming = false;
			return;
		}
		zooming = false;
		if(!tekenComponentAan && source == grafiekGWTCanvas) {	
			double beginxR = beginx;
			if (!manualScalingX && !manualScalingY) {
				beginx = eenheidx*Math.round(beginx/eenheidx);
				beginy = eenheidy*Math.round(beginy/eenheidy);
			}
			
			if(traceOptie && grafiekGWTVeld.tracex!=-2) 
			{	grafiekGWTVeld.tracexD += beginx-beginxR;
				grafiekGWTVeld.tracex += beginx-beginxR;
				grafiekGWTVeld.zetSliderStand(grafiekGWTVeld.tracex);
			}
		}
		else if (source == grafiekGWTCanvas) {	
			if (tekenComponent.getCursorMode() == tekenComponent.NOCUR)
			{	double beginxR = beginx;
				if (!manualScalingX && !manualScalingY) {
					beginx = eenheidx*Math.round(beginx/eenheidx);
					beginy = eenheidy*Math.round(beginy/eenheidy);
				}
				
				if(traceOptie && grafiekGWTVeld.tracex!=-2) 
				{	grafiekGWTVeld.tracexD += beginx-beginxR;
					grafiekGWTVeld.tracex += beginx-beginxR;
					grafiekGWTVeld.zetSliderStand(grafiekGWTVeld.tracex);
				}
			}
			else if (tekenComponent.getCursorMode() == tekenComponent.DRAW)
			{	if (dragPoint != null)
				{	// corrigeer dragPoint				
					removePoint(dragPoint.getTabelIndex(), dragPoint.getIndex());//, false);
					Point dragPix = realPointToPixels(dragPoint);
					if(snapToGridPoints)
					{	dragPix = new Point(closestGridPix(dragPix.getX(), true), closestGridPix(dragPix.getY(), false));
						//dragPix.x = closestGridPix(dragPix.x, true);
   						//dragPix.y = closestGridPix(dragPix.y, false);
					}
					int freePixX = closestFreePixX(dragPix.getX());
					RealPoint temp = pixelsToRealPoint(new Point(freePixX, dragPix.getY()));
					dragPoint.setX(temp.getX());
					dragPoint.setY(temp.getY());
					dragPoint.setxString(Double.toString(dragPoint.getX()));
					dragPoint.setyString(Double.toString(dragPoint.getY()));
					addInsert(dragPoint, true);//, false);
				
					dragPoint = null;
					
				}
			}
			else if (tekenComponent.getCursorMode() == tekenComponent.DRAG)
			{	if (dragPoint != null)
				{	// corrigeer dragPoint								
					
					removePoint(dragPoint.getTabelIndex(), dragPoint.getIndex());//, false);
					Point dragPix = realPointToPixels(dragPoint);
					if(snapToGridPoints)
					{	dragPix = new Point(closestGridPix(dragPix.getX(), true), closestGridPix(dragPix.getY(), false));
						//dragPix.x = closestGridPix(dragPix.x, true);
   						//dragPix.y = closestGridPix(dragPix.y, false);
					}
					int freePixX = closestFreePixX(dragPix.getX());
					RealPoint temp = pixelsToRealPoint(new Point(freePixX, dragPix.getY()));
					dragPoint.setX(temp.getX());
					dragPoint.setY(temp.getY());	
					dragPoint.setxString(Double.toString(dragPoint.getX()));
					dragPoint.setyString(Double.toString(dragPoint.getY()));
					addInsert(dragPoint, true);//, false);							
					
					dragPoint = null;
					
				}
				// grafiek slepen
				else if ((dragPoint == null) && (otherPoint == null) && dragOptie) {	
					double beginxR = beginx;
				
					if (!manualScalingX && !manualScalingY) {
						beginx = eenheidx*Math.round(beginx/eenheidx);
						beginy = eenheidy*Math.round(beginy/eenheidy);
					}

					if(traceOptie && grafiekGWTVeld.tracex!=-2) 
					{	grafiekGWTVeld.tracexD += beginx-beginxR;
						grafiekGWTVeld.tracex += beginx-beginxR;
						grafiekGWTVeld.zetSliderStand(grafiekGWTVeld.tracex);
					}
				}
			}
		}
	
		if (typeOpdracht == TEKENTABELPUNTEN)
		{	setColor(0, CssColor.make(0,0,255), false);	
			tabelComponent.zetTabelPunten(docentGraphPoints, false);
				
			if(graphPoints.size() >= docentGraphPoints.size())
			{	kijkNaButton.setEnabled(true);
			}
			else
			{	kijkNaButton.setEnabled(false);
				goedkrulImage.setVisible(false);
				goedkrulHalfImage.setVisible(false);
				foutkruisImage.setVisible(false);
					
				//repaint();
				score = 0;
				correct = false;
	    	}
		}
		else if (typeOpdracht == TEKENPUNTENBIJFORMULE)
		{	int kleinsteMinimum = minimumPunten[0];
	    	for(int i = 1; i < aantalFuncties; i++)
	    	{	if(minimumPunten[i] < kleinsteMinimum)
	    			{
	    				kleinsteMinimum = minimumPunten[i];
	    			}
	    	}
    		
			if(graphPoints.size() >= kleinsteMinimum)
			{	kijkNaButton.setEnabled(true);
			}
			else
			{	kijkNaButton.setEnabled(false);
				goedkrulImage.setVisible(false);
				goedkrulHalfImage.setVisible(false);
				foutkruisImage.setVisible(false);
					
				//repaint();
				score = 0;
				correct = false;
	    	}
		}
		
		grafiekGWTVeld.paint();
	//	xAsNaamTF.setVisible(xVarEditable && grafiekGWTVeld.activateXAsNaam(e.getX(), e.getY()));
	//	if(xAsNaamTF.isVisible())xAsNaamTF.requestFocus();
	//	yAsNaamTF.setVisible(yVarEditable && grafiekGWTVeld.activateYAsNaam(e.getX(), e.getY()));
	//	if(yAsNaamTF.isVisible())yAsNaamTF.requestFocus();
	}
	
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler
	{
		boolean mouseDown = false;
		PointerHandler pointer;
		
		public void onMouseDown(MouseDownEvent e) {
			DOM.setCapture(grafiekGWTCanvas.getElement());

			// e.preventDefault();
			
			// prevent scrolling 
		//	e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDown = true; // TODO
			
			mouseDownTouchStartAction(e.getSource(), eventX, eventY, ONE_FINGER);
			
		}
		
		//public void mouseDragged(MouseEvent e)
		public void onMouseMove(MouseMoveEvent e)	{
			
			//e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			RealPoint drp = null;
			Vector points = getPoints(getActiveIndex(), false);
			{	for (int pCnt = 0; pCnt < points.size(); pCnt++)
				{	RealPoint rp = (RealPoint) points.elementAt(pCnt);
					Point rpPix = realPointToPixels(rp);
					if(rpPix == null) continue;
					int dis = (int) Math.round(
						Math.sqrt((rpPix.getX() - e.getX()) * (rpPix.getX() - e.getX()) +
								  (rpPix.getY() - e.getY()) * (rpPix.getY() - e.getY())));
					if (dis <= cPointRadius + 1)
					{	drp = rp;
					}
					
				}
			}

			if ( mouseDown && (tekenComponent.getCursorMode() != tekenComponent.DELETE) ) {
				// only move action needs to be taken during drag, draw or default mode
				int eventX = e.getX();
				int eventY = e.getY();
				
				mouseMoveTouchMoveAction(e.getSource(), eventX, eventY, ONE_FINGER, 0, 0, 0);
			}
			
			
		} // onMouseMove
		
		//public void mouseReleased(MouseEvent e)
		public void onMouseUp(MouseUpEvent e) {
			DOM.releaseCapture(grafiekGWTCanvas.getElement());

			//e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			
			mouseUpTouchEndAction(e.getSource(), e.getX(), e.getY());

		}

		@Override
		public void onMouseOut(MouseOutEvent event) {
			logger.info("Mouse out");
			mouseDown = false;
			pointer.state = READY;
			
		}
	}
	
	public int dist(int x1, int y1, int x2, int y2)
	{
		return (int) Math.round(Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)));
	}
	
	public int direction(int x1, int y1, int x2, int y2)
	{	
		if(Math.abs(x1 - x2) < 20)
		{	return VERTICAL;
		
		}
		else if(Math.abs(y1 - y2) < 20)
		{	return HORIZONTAL;
		
		}
		else
		{	return DIAGONAL;
		
		}
	}
		
	
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		int touchCnt = 0;
		int pinchState = READY;
		int pinchStartDistance = 0;
		int pinchMoveDistance = 0;
		
	    
		
		
		int pinchMoveDirection = DIAGONAL;
		
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			touchCnt++;
			
			if (pinchState == READY)
			{
				Touch touch = e.getTouches().get(0);
			
				int eventX = touch.getPageX() - grafiekGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - grafiekGWTCanvas.getAbsoluteTop();
				eventX = touch.getRelativeX(grafiekGWTCanvas.getCanvasElement());
				eventY = touch.getRelativeY(grafiekGWTCanvas.getCanvasElement());
				pinchState = ONE_FINGER;
				
				mouseDownTouchStartAction(e.getSource(), eventX, eventY, pinchState);
				
			}
			else if (pinchState == ONE_FINGER)
			{
				Touch touch1 = e.getTouches().get(0);
				Touch touch2 = e.getTouches().get(1);
			
				int event1X = touch1.getPageX() - grafiekGWTCanvas.getAbsoluteLeft();
				int event1Y = touch1.getPageY() - grafiekGWTCanvas.getAbsoluteTop();				
				int event2X = touch2.getPageX() - grafiekGWTCanvas.getAbsoluteLeft();
				int event2Y = touch2.getPageY() - grafiekGWTCanvas.getAbsoluteTop();

				event1X = touch1.getRelativeX(grafiekGWTCanvas.getCanvasElement());
				event1Y = touch1.getRelativeY(grafiekGWTCanvas.getCanvasElement());
				event2X = touch2.getRelativeX(grafiekGWTCanvas.getCanvasElement());
				event2Y = touch2.getRelativeY(grafiekGWTCanvas.getCanvasElement());

				
				
				pinchState = TWO_FINGERS;
				
				pinchStartDistance = dist(event1X, event1Y, event2X, event2Y);
				
				mouseDownTouchStartAction(e.getSource(), event1X, event1Y, pinchState);
				
			}
			
			/*
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				
				int eventX = touch.getPageX() - grafiekGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - grafiekGWTCanvas.getAbsoluteTop();				
				
				mouseDownTouchStartAction(e.getSource(), eventX, eventY);
				
		    }
		    */
			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				if (pinchState == ONE_FINGER)
				{	
					Touch touch = e.getTouches().get(0);

					int eventX = touch.getPageX() - grafiekGWTCanvas.getAbsoluteLeft();
					int eventY = touch.getPageY() - grafiekGWTCanvas.getAbsoluteTop();				

					eventX = touch.getRelativeX(grafiekGWTCanvas.getCanvasElement());
					eventY = touch.getRelativeY(grafiekGWTCanvas.getCanvasElement());

					mouseMoveTouchMoveAction(e.getSource(), eventX, eventY, pinchState, 0, 0, 0);
					
					
					
				}
				else if (pinchState == TWO_FINGERS)
				{
					Touch touch1 = e.getTouches().get(0);
					Touch touch2 = e.getTouches().get(1);
				
					int event1X = touch1.getPageX() - grafiekGWTCanvas.getAbsoluteLeft();
					int event1Y = touch1.getPageY() - grafiekGWTCanvas.getAbsoluteTop();				
					int event2X = touch2.getPageX() - grafiekGWTCanvas.getAbsoluteLeft();
					int event2Y = touch2.getPageY() - grafiekGWTCanvas.getAbsoluteTop();				

					event1X = touch1.getRelativeX(grafiekGWTCanvas.getCanvasElement());
					event1Y = touch1.getRelativeY(grafiekGWTCanvas.getCanvasElement());
					event2X = touch2.getRelativeX(grafiekGWTCanvas.getCanvasElement());
					event2Y = touch2.getRelativeY(grafiekGWTCanvas.getCanvasElement());

					pinchMoveDistance = dist(event1X, event1Y, event2X, event2Y);
					pinchMoveDirection = direction(event1X, event1Y, event2X, event2Y);
					
															
					mouseMoveTouchMoveAction(e.getSource(), event1X, event1Y, pinchState, pinchStartDistance, pinchMoveDistance, pinchMoveDirection);
					
					pinchStartDistance = pinchMoveDistance;					
				
				}
				
		    }
			
			/*
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				//boolean shiftPressed = e.isShiftKeyDown();

			    boolean shiftPressed = false;
			    int eventX = touch.getPageX() - grafiekGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - grafiekGWTCanvas.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(e.getSource(), eventX, eventY, shiftPressed);
				
		    }
		    */
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			touchCnt--;
			if (touchCnt <= 0)
			{	touchCnt = 0; 
				pinchState = READY;
			}
			else
			{
				if (pinchState == TWO_FINGERS)
				{
					pinchState = ONE_FINGER;
					touchCnt = 1;
				}
				else if (pinchState == ONE_FINGER)
				{
					pinchState = READY;
					touchCnt = 0;
				}
				else
				{
					if (touchCnt == 2)
					{	pinchState = TWO_FINGERS;
					}
				}
			}
			int eventX = -1;
			int eventY = -1;
			if (e.getTouches().length() > 0) {
				Touch touch = e.getTouches().get(0);
			    eventX = touch.getPageX() - grafiekGWTCanvas.getAbsoluteLeft();
				eventY = touch.getPageY() - grafiekGWTCanvas.getAbsoluteTop();				
		    }

			mouseUpTouchEndAction(e.getSource(), eventX, eventY);
		}

	}
	
	HandlerRegistration others;
	
	class PointerHandler implements PointerDownHandler, PointerMoveHandler, PointerUpHandler, PointerCancelHandler {

		private int state = READY;
		private int id1, x1, y1, id2, x2, y2;

		int pinchStartDistance = 0;
		int pinchMoveDistance = 0;
		int pinchMoveDirection = DIAGONAL;

		@Override
		public void onPointerUp(PointerUpEvent e) {
			e.preventDefault();
			GWT.log("pointer-up " + e.getX() + " " + e.getY()  + " " + e.getPointerId());
			if (state == ONE_FINGER && e.getPointerId() == id1) 
			{
				state = READY;
				mouseUpTouchEndAction(e.getSource(), e.getX(), e.getY());
			} else if (state == TWO_FINGERS) {
				if (e.getPointerId() == id2) {
					state = ONE_FINGER;
				} else if (e.getPointerId() == id1) {
					id1 = id2;
					x1 = e.getX();
					y1 = e.getY(); 
					state = ONE_FINGER;
				}
				if (state == ONE_FINGER) {
					mouseUpTouchEndAction(e.getSource(), e.getX(), e.getY());				
				}
				
			}
		}

		@Override
		public void onPointerMove(PointerMoveEvent e) {
			if (others != null) {
				others.removeHandler();
				others = null;
			}
			e.preventDefault();
			
			if ( state == ONE_FINGER && (tekenComponent.getCursorMode() != TekenComponentGWT.DELETE) && e.getPointerId() == id1) {
				// only move action needs to be taken during drag, draw or default mode
				int eventX = x1 = e.getX();
				int eventY = y1 = e.getY();				
				mouseMoveTouchMoveAction(e.getSource(), eventX, eventY, ONE_FINGER, 0, 0, 0);
			} else if (state == TWO_FINGERS && e.getPointerId() == id1 ) {
				x1 = e.getX();
				y1 = e.getY();
				pinch(e);
				
			} else if (state == TWO_FINGERS && e.getPointerId() == id2) {
				x2 = e.getX();
				y2 = e.getY();
				pinch(e);
			}
		}

		private void pinch(PointerMoveEvent e) {
			pinchMoveDistance = dist(x1, y1, x2, y2);
			pinchMoveDirection = direction(x1, y1, x2, y2);
			mouseMoveTouchMoveAction(e.getSource(), x1, y2, state, pinchStartDistance, pinchMoveDistance, pinchMoveDirection);			
			pinchStartDistance = pinchMoveDistance;					
		}

		@Override
		public void onPointerDown(PointerDownEvent e) {
			if (others != null) {
				others.removeHandler();
				others = null;
			}
			e.preventDefault();

			int eventX = e.getX();
			int eventY = e.getY();
			GWT.log("pointer-down " + eventX + " " + eventY + " " + e.getPointerId());
			
			if (state == READY) {
				state = ONE_FINGER;
				id1 = e.getPointerId();
				x1 = eventX;
				y1 = eventY;
				mouseDownTouchStartAction(e.getSource(), eventX, eventY, ONE_FINGER);
			} else if (state == ONE_FINGER ) {
				state = TWO_FINGERS;
				id2 = e.getPointerId();
				x2 = eventX;
				y2 = eventY;
				pinchStartDistance = dist(x1, y1, x2, y2);
				mouseDownTouchStartAction(e.getSource(), x1, y1, TWO_FINGERS);
			}	
		}

		@Override
		public void onPointerCancel(PointerCancelEvent event) {
			logger.info("Pointer cancel");
			state = READY;			
		}
		
	}
	
	
	private void runZoom(boolean isX, boolean isY, boolean isIn) 
	{
		{	
			final boolean x = isX;
			final boolean y = isY;
			final boolean in = isIn;
			
			if(x) 
			{
				selectnummer = 999;
			}
			if (!manualScalingX) {
		        eenheidxD = xAsLog?2*eenheid:eenheid;
				eenheidy = yAsLog?2*eenheid:eenheid;
			}
			
			if (!manualScalingY) {
				eenheidyD = yAsLog?2*eenheid:eenheid;
				eenheidx = xAsLog?2*eenheid:eenheid;
			}
			
			factorx = 1;
			factory = 1;
			
			final double middenx = breedte/2/eenheidx*eenheidx;
			final double middeny = grafiekVeldHoogte/2/eenheidy*eenheidy;
			
			if(in && x)
			{	if(factorRijNummerX%3==2)
				{
					factorx=0.4;
				}
				else 
				{	
					factorx=0.5;
				}
			}
			else if(!in && x)
			{	if(factorRijNummerX%3==1)
				{
					factorx=2.5;
				}
				else 
				{
					factorx=2;
				}
			}
			
			if(in && y)
			{	if(factorRijNummerY%3==2)
			{
				factory =0.4;
			}
				else 
				{
					factory=0.5;
				}
			}
			
			else if(!in && y)
			{	if(factorRijNummerY%3==1)
			{
				factory =2.5;
			}
				else 
				{
					factory=2;
				}
			}
			
			//* Animation Part */
			final double stapx = Math.pow(factorx,0.1);
			final double stapy = Math.pow(factory,0.1);
			
			long t = System.currentTimeMillis();
				
			for(int i = 0; i < 5; i++) {
				int delay = 20*(i+1);
				long t2 = t + delay;
				Timer timer = new Timer() {
					public void run() { 
						eenheidxD = eenheidxD/stapx;
						eenheidyD = eenheidyD/stapy;
						eenheidx = (int) Math.round(eenheidxD);
						eenheidy = (int) Math.round(eenheidyD);
						beginx =  middenx -(middenx - beginx)/stapx;
						beginy =  middeny -(middeny - beginy)/stapy;
						
						grafiekGWTVeld.tracexD = middenx -(middenx - grafiekGWTVeld.tracexD)/stapx;
					
						beginwaarde = 1-(int)Math.round(beginx/eenheidx);
						grafiekGWTVeld.tracex = (int) Math.round(grafiekGWTVeld.tracexD);
						
						grafiekGWTVeld.zetSliderStand(grafiekGWTVeld.tracex);
						grafiekGWTVeld.paint();  
					} 
				};
				
				
				timer.schedule((int) Math.max(1, t2 - System.currentTimeMillis()));
				/*
				eenheidxD = eenheidxD/stapx;
				eenheidyD = eenheidyD/stapy;
				eenheidx = (int) Math.round(eenheidxD);
				eenheidy = (int) Math.round(eenheidyD);
				beginx =  middenx -(middenx - beginx)/stapx;
				beginy =  middeny -(middeny - beginy)/stapy;
				
				grafiekGWTVeld.tracexD = middenx -(middenx - grafiekGWTVeld.tracexD)/stapx;
				
				beginwaarde = 1-(int)Math.round(beginx/eenheidx);
				grafiekGWTVeld.tracex = (int) Math.round(grafiekGWTVeld.tracexD);
				
				
				timer.schedule(20*(i+1));
				*/
				
			}
			
			long t3 = t + 100;
			Timer timer2 = new Timer() { 
				public void run() {
					schaalFactorX*=factorx;
					if(in && x)factorRijNummerX--;
					if(!in && x)factorRijNummerX++;
					schaalFactorY*=factory;
					if(in && y)factorRijNummerY--;
					if(!in && y)factorRijNummerY++;
					
					if (!manualScalingX) {
						eenheidxD = eenheidxD*factorx;
					}
					if (!manualScalingY) {
						eenheidyD = eenheidyD*factory;
					}
				}
			};
			timer2.schedule((int) Math.max(1, t3 - System.currentTimeMillis()));
			
			for(int i=0 ; i<5 ; i++) {
				int delay = 20*(i+6);
				long t2 = t + delay;
				//t = t + delay;
				Timer timer = new Timer() {
					public void run() { 
						eenheidxD = eenheidxD/stapx;
						eenheidyD = eenheidyD/stapy;
						eenheidx = (int) Math.round(eenheidxD);
						eenheidy = (int) Math.round(eenheidyD);
						beginx =  middenx -(middenx - beginx)/stapx;
						beginy =  middeny -(middeny - beginy)/stapy;
					
						grafiekGWTVeld.tracexD = middenx -(middenx - grafiekGWTVeld.tracexD)/stapx;
					
						beginwaarde = 1-(int)Math.round(beginx/eenheidx);
						grafiekGWTVeld.tracex = (int) Math.round(grafiekGWTVeld.tracexD);
						
						grafiekGWTVeld.zetSliderStand(grafiekGWTVeld.tracex);
						grafiekGWTVeld.paint();  
			    	} 
				};
				timer.schedule((int) Math.max(1, t2 - System.currentTimeMillis()));
			}
			/* End of Animation part */
			
			beginwaarde = 1-(int)Math.round(beginx/eenheidx);
			double beginwaardeD = 1.0-(beginx/eenheidx);
			
			grafiekGWTVeld.tracexD = grafiekGWTVeld.tracexD + eenheid*(beginwaardeD - beginwaarde);
			grafiekGWTVeld.tracex = (int) Math.round(grafiekGWTVeld.tracexD);
			grafiekGWTVeld.zetSliderStand(grafiekGWTVeld.tracex);
			
			if(x)
				{
				selectnummer = 999;
				}
			
			grafiekGWTVeld.paint();
			//dood = true;
			
		}
	
	}
	
	
	class PushClickHandler implements ClickHandler
    {
    	public void onClick(ClickEvent e)
    	{
    		if( (e.getSource()==zoomUitYButton && factorRijNummerY<120) &&
    				!(manualScalingY && eenheidy<2) )
				runZoom(false,true,false);
			else if (e.getSource()==zoomInYButton  && factorRijNummerY>87) 
				runZoom(false,true,true);
			else if( (e.getSource()==zoomUitXButton && factorRijNummerX<120) &&
				    		!(manualScalingX && eenheidx<2) )
				runZoom(true,false,false);
			else if(e.getSource()==zoomInXButton  && factorRijNummerX>87) 
				runZoom(true,false,true);
			else if( (e.getSource()==zoomUitButton && factorRijNummerX<120 && factorRijNummerY<120) &&
							( !(manualScalingX && eenheidx<2) && !(manualScalingY && eenheidy<2) ) )
				runZoom(true,true,false);
			else if(e.getSource()==zoomInButton && factorRijNummerX>87 && factorRijNummerY>87)
				runZoom(true,true,true);
			else if(e.getSource()==zoomStandaardButton)
			{	beginx = beginxDocent;
				beginy = beginyDocent;
				double beginxVorig = beginx;
				grafiekGWTVeld.tracexD = beginx -(beginxVorig - grafiekGWTVeld.tracexD)*schaalFactorX;
				factorRijNummerX = 99;
				factorRijNummerY = 99;
				schaalFactorX = docentSchaalFactorX;
				schaalFactorY = docentSchaalFactorY; 
				if (manualScalingX || manualScalingY) {
					zetAssenDefinitie( asDefXMin,  asDefXMax,  asDefXStap,  asDefYMin,  asDefYMax,  asDefYStap);
				}
				
				beginwaarde = 0;
				selectnummer = 999;
				
				grafiekGWTVeld.tracex = (int) Math.round(grafiekGWTVeld.tracexD);
				grafiekGWTVeld.zetSliderStand(grafiekGWTVeld.tracex);
				
				grafiekGWTVeld.paint();
    		}
    	}
    }

	private int asHoogte;

	public int getAsHoogte() {
		return asHoogte;
	}
	public int getHeight() {
		return hoogte;
	}
	public int getWidth() {
		return breedte+2 ; //+ 2 * offset
	}
	
	public void zetVolledigeBreedte(int breedte)
	{
		
			
	}
	
	public void setAsHoogte(int ashoogte)
	{
		this.asHoogte = ashoogte;
	}

	@Override
	public void getResponses(List<String> responses)
	{
		responses.addAll(getResponse());
	}
	
	void setComRootChanged(boolean b)
	{
		if(comRoot != null && fromuser && (mode != OpdrNavIF.ZELFTOETS))
			comRoot.setChanged(b);
	}
	
	void setChanged(boolean c)
	{
		changed = c;
	}

	private void fireEvent(CBookEvent event) 
	{
		comRoot.fireEvent(event);
	}
	public void setAttempt(Map<String, ?> parameters) {
		if (attempt && comRoot != null) {
			comRoot.fireEvent(new CBookEvent(this, LOG_OPTION, parameters));
			logger.info(parameters.toString());
		}
	}

	public void setAttempt() {
		if (attempt && ingevuld) {
// Build parameters voor logging: zie FormuleEditorWithAnswer.buildLoggingMap
			Map<String,Object> parameters = new HashMap<>();
			parameters.put("verb", "http://adlnet.gov/expapi/verbs/attempted"); // standaard voor "poging"
			if (isCorrect()!= null) parameters.put("success", isCorrect());
			parameters.put("score", Collections.singletonMap("raw", getScore()));
			
			//parameters.put("response", "???"); 
			setAttempt(parameters);
		}
	}
	
	public void zetAssenDefinitie(double asDefXMin, double asDefXMax, double asDefXStap, double asDefYMin, double asDefYMax, double asDefYStap)
	{
		if (manualScalingX)
		{
			// Calculate graph-parameters X
			double rangeX = asDefXMax-asDefXMin;
			eenheidxD = breedte/(rangeX/asDefXStap)/2; // gedeeld door 2 tbv een kleinere schaling dan de grove 
			eenheidx = Math.max(1, (int) Math.round(eenheidxD));
			eenheidxValue = Math.abs(asDefXStap/2);  // we don't allow negative step-sizes (yet) 
			beginx = -Math.round(asDefXMin/eenheidxValue * eenheidxD);

			beginxDocent = beginx;
		}

		if (manualScalingY) {
			
			// Calculate graph-parameters Y
			double rangeY = asDefYMax-asDefYMin;
			eenheidyD = grafiekVeldHoogte/(rangeY/asDefYStap)/2; // always positive
			eenheidy = Math.max((int) Math.round(eenheidyD), 1); // idem.
			eenheidyValue = Math.abs(asDefYStap/2);  // gedeeld door 2 tbv een kleinere schaling dan de grove
			beginy = -Math.round(asDefYMin/eenheidyValue * eenheidyD);			
	
			beginyDocent = beginy;			
		}
		
	}
	
	/**
	 * Surround the given string with the formule codes "$f" and "@".
	 * @param string
	 * @return
	 */
	private String addFormulaCodes(String string)
	{
		String startCode = "$f";
		String endCode = "@";
		String s = startCode + string + endCode;
		return s;
	}
    
	public void acceptCBookEvent(CBookEvent event)
	{
		String command = event.getCommand();
		
		if(command.equals("input"))
		{
	 		String formuleString = (String) event.getMessage();
		}

		if (command.startsWith("expression"))
		{
			String indexString = command.substring(11);
			int index = Integer.parseInt(indexString) - 1;

			String formuleString = (String) event.getMessage();
			if ((formuleString.length() < 3) || (!formuleString.substring(0, 2).equals("$f")))
			{
				formuleString = addFormulaCodes(formuleString);
			}
			
	 		Expressie expr = FormuleParser.geefExpressie(formuleString);
	 		zetFunctie(index /* nr */, expr /* Expressie */, formuleString /* expString */, null /*expNaam */, 
	 				DEFAULTDOMEIN /* domein */, true /* update */ , false /* setState */, false /* docent */);
		}
		
		if (command.startsWith("equation.graph"))
		{
			String vergelijkingString = (String) event.getMessage();
			if (vergelijkingString.length() < 3 || !vergelijkingString.substring(0, 2).equals("$f"))
				vergelijkingString = "$f" + vergelijkingString + "@";
			
			VergelijkingMeerv v = FormuleParser.parseVergelijking(vergelijkingString);
			if(v==null) { //misschien is het een expressie
				vergelijkingString = "$fy=" + vergelijkingString.substring(2);
			}
			
			//String indexString = command.substring(11);
			//int index = Integer.parseInt(indexString) - 1;

			String functieString = null;
			
			v = FormuleParser.parseVergelijking(vergelijkingString);
			Expressie expr = v.geefVergelijking(0).geefExpRechts();
	 		functieString = "$f"+expr.toString()+"@";
			
	 		zetFunctie(0 /* nr */, expr /* Expressie */, functieString /* expString */, null /*expNaam */, 
	 				DEFAULTDOMEIN /* domein */, true /* update */ , false /* setState */, false /* docent */);
		}
				
		if (command.equals("equation.twoGraphs"))
		{
			String vergelijkingString = (String) event.getMessage();
			if (!vergelijkingString.substring(0, 2).equals("$f"))
				vergelijkingString = "$f" + vergelijkingString + "@";
		}
		if (event.getCommand().equals("draw_functions"))
		{
			Map map = (Map)event.getParameters();
			if (map!=null)
			{	
				
//				 BEGIN OLD JAVA ANNOTATION :: could be hint for new implmentation :: see next RPJ statement
//				String numberString = (String)map.get("number");
//				int number = 0;
//				try	{	
//					number = Integer.parseInt(numberString);
//				}
//				catch (NumberFormatException nfe) {
//					System.out.println(nfe.toString());
//				}
//				String clear = (String)map.get("clear");
//				String abscissa_name = (String)map.get("abscissa_name");
//				String abscissa_min = (String)map.get("abscissa_min");
//				String abscissa_max = (String)map.get("abscissa_max");
//				String ordinate_name = (String)map.get("ordinate_name");
//				String ordinate_min = (String)map.get("ordinate_min");
//				String ordinate_max = (String)map.get("ordinate_max");
//				
//				Expressie[] functions = new Expressie[number];
//				Color[] colors = null;
//				double[] thicknesses = null;
//				
//				for(int i=0 ; i<number ; i++)
//				{
//					String functionString = (String)map.get("function_"+i);
//					functions[i] = popcornParse(functionString);
//					String colorString = (String)map.get("color_"+i);
//					colors[i] = colorParse(colorString);
//					String thicknessString = (String)map.get("thickness_"+i);
//					try	{	
//						thicknesses[i] = Double.parseDouble(thicknessString);
//					}
//					catch (NumberFormatException nfe) {
//						System.out.println(nfe.toString());
//					}
//					
//				}
//				END OLD JAVA ANNOTATION 
//				getFormuleComponent().zetFuncties(map); RPJ == from active java version
			}
		}
		
		if (event.getCommand().equals("double.trace"))
		{
			Map map = (Map)event.getParameters();
			if (map!=null)
			{
				String name = (String)map.get("name");
				double waarde = ((Double)map.get("value")).doubleValue();
				if (grafiekXAsNaam.equals(name))
				{
					// onderstaande werkt goed, maar niet bij gedefinieerde schalen
					
//					int tracex =(int)(eenheidxD*(waarde)/schaalFactorX+beginx);
//					grafiekGWTVeld.tracexD = tracex;
//					grafiekGWTVeld.zetSliderStand(tracex);
//					grafiekGWTVeld.paint();
					
// RPJ START == from active java version					
//					tracing = true;
//					double xWaarde = ((Double)map.get("value")).doubleValue();
//					tracex =(int)(eenheidxD*(xWaarde)/schaalFactorX+beginx);
//					tracexD = tracex;
//					slider.zetStand(tracex);
//					
//					repaint();
//RPJ END 
				}
			}
		}
		if(event.getCommand().equals("double.parameter"))
		{
			Map map = (Map)event.getParameters();
			if (map!=null)
			{	
				String name = (String)map.get("name");
				double waarde = ((Double)map.get("value")).doubleValue();
				if(schuifParameters!=null) {
					SchuifParameterGWT schuifParameter = geefSchuifParameter(name);
					if(schuifParameter!=null) {
						schuifParameter.zetWaarde(waarde);
						grafiekGWTVeld.paint();
					}
				}
// RPJ START == from active java version
//				SchuifParameter schuifParameter = geefSchuifParameter(name);
//				if(schuifParameter==null) {	
//					schuifParameter = new SchuifParameter(200,name);
//					voegSchuifParameterToe(schuifParameter,false);
//				}
//				schuifParameter.zetWaarde(waarde, false);
//				gv.repaint();
//RPJ END 			
			}
		}	
	}
	
	/**
	 * Zet de domeinen.
	 * 
	 * @param domeinen
	 */
	public void zetDomeinen(double[][] domeinen)
	{
		this.domeinen = domeinen;
	}
	
	/**
	 * Zet het domein met de gegeven index in domeinen.
	 * 
	 * @param domein
	 * @param index
	 */
	public void zetDomein(double[] domein, int index)
	{
		if (index <= domeinen.length)
			this.domeinen[index] = domein;
	}
}
