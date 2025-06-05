package fi.weblogogwt.client;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.logotekenap.Tekenblad;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

import java.util.logging.Level;
import java.util.logging.Logger;

import fi.weblogogwt.client.text.Text;

/**
 * start-up class for WebLogoGWT; the widget contains a program part
 * (see class JavaLogoSchuifVeld), a drawing part (see class
 * Uitvoerblad/Tekenblad) and a bottomPanel (see method makeBottom(); 
 * when used in the DWOPlayer these parts can be added/omitted through  
 * using JavaLogoWebPaul in the DWO; in this case, CBook-communication
 * is also possible (e.g. using a large drawing area communicating with 
 * a separate program part under a button); <br>
 * in the stand-alone version all available space in the browser is used
 * (first choose the browser size, then close the browser, then open
 * the widget in the browser; if the widget is opened, it cannot adapt 
 * its size). 
 */

public class WebLogoGWT implements EntryPoint, InteractionStub, CBookEventListener 
{
	/**
	 * internationalisation
	 */
	public static Text rb;
    static Logger logger = Logger.getLogger("WebLogoGWT");

    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	WebLogoGWTClientBundle webLogoGWTClientBundle;
	static WebLogoGWTCssResource webLogoGWTCssResource;

	/**
	 * DockLayoutPanel added to the Root
	 */
	DockLayoutPanel dlp;
	/** 
	 * Panel at the bottom of dlp (if programmaVeldZichtbaar)
	 */
	LayoutPanel bottomPanel;
	/**
	 * Panel in the centre of  dlp
	 */
	LayoutPanel webLogoPanel;
	/**
	 * Java logo schuifveld.
	 * Panel for program part.
	 */
	JavaLogoSchuifVeld jlsVeld;
	/**
	 * Panel for drawing part
	 */
	public Uitvoerblad uitvoerblad;
	
	/**
	 * class implementing program tracing  
	 */
	public TraceBeheerder trb;
	
	/**
	 * predefined button sizes
	 */
	int buttonWidth = 45;
	int buttonHeight = 22;

	/**
	 * width program part without subroutines
	 */
	static int jlsBreedteKlein = 390;
	/**
	 * width program part with subroutines
	 */
	static int jlsBreedteGroot = 620;
	
	/**
	 * offsets
	 */
	int offSet = 4;
	int leftOffset = 5;
	int topOffset = 5;

	/**
	 * Flag for stand-alone version.
	 */
	boolean paul = false;
	/**
	 * width with subroutines and with drawing part, 784 is max width in popupFacade;
	 */
	int breedteGroot = 784; 
	/**
	 * width without subroutines and with drawing part 
	 */
	int breedteKlein = 700;
	/**
	 * final width
	 */
	int breedte = 784;
	/**
	 * final height
	 */
	int hoogte = 575;
	/**
	 * height of bottom panel (if programmaVeldZichtbaar)
	 */
	int bottomHeight = 32;
	/**
	 * height program part
	 */
	int jlsHoogte = hoogte - bottomHeight - offSet;
	/**
	 * x-position drawing part, no subroutines in program part
	 */
	int ubxKlein = jlsBreedteKlein + 2 * offSet; 
	/**
	 * x-position drawing part, subroutines in program part
	 */
	int ubxGroot = jlsBreedteGroot + 2 * offSet;
	/**
	 * y-position drawing part
	 */
	int uby = offSet;
	/**
	 * Uitvoerbladbreedte klein.
	 * Width drawing part, no subroutines in program part.
	 */
	int ubbKlein = breedteKlein - jlsBreedteKlein - 3 * offSet; 
	/**
	 * Uitvoerbladbreedte groot.
	 * Width drawing part, depending on subroutines in program part
	 */
	int ubbGroot = breedteGroot - jlsBreedteGroot - 3 * offSet;
	int ubb = 0;
	/**
	 * height drawing part
	 */
	int ubh = jlsHoogte; 
	/**
	 * fonts
	 */
	public static String fontString = "12px sans-serif";
	public static String boldFontString = "bold 12px sans-serif";
	
	/**
	 * launchdata
	 */
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	/**
	 * initial code (if any) in launchdata
	 */
	HashMap<String, Object> state = null;
	
	/**
	 * parametrisation: drawing part available
	 */
	public boolean uitvoerVeldZichtbaar = true;
	/**
	 * parametrisation: program part available
	 */
	boolean programmaVeldZichtbaar = true;
	/**
	 * parametrisation: subroutines available
	 */
	boolean deeltakenZichtbaar = true;
	/**
	 * parametrisation: while command available
	 */
	boolean whileLoopZichtbaar = true;
	/**
	 * parametrisation: if - else command available  
	 */
	boolean keuzeCommandZichtbaar = true;
	/**
	 * parametrisation: print commands available
	 */
	boolean printCommandsZichtbaar = true;
	/**
	 * parametrisation: drawing commands available
	 */
	boolean tekenCommandsZichtbaar = true;
	/**
	 * parametrisation: tracing available
	 */
	boolean traceZichtbaar = true;
	/**
	 * parametrisation: import/export code available
	 */
	boolean codeIOZichtbaar = true;
	
	boolean runZichtbaar = true;
	
	/**
	 * flagg for tracing
	 */
	boolean traceAan = false;
	
	private int mode;
	private OpdrNavIF comRoot;

	/**
	 * PushButtons for importing code, exporting code, running code
	 */
	PushButton importButton, exportButton, runButton;
	/**
	 * PushButtons for tracing
	 */
	PushButton traceAanKnop, traceUitKnop, beginKnop, stapKnop, terugKnop, skipKnop;
	/**
	 * CheckBox for showing a variable window
	 */
	CheckBox showVariables;
	/**
	 * label for showing current code line when tracing
	 */
	public Label methodeLabel;
	
	/**
	 * Panel showing the variables during tracing
	 */
	public VardisplayPanel vartracer = null;
	
	/**
	 * sizes of VardisplayPanel
	 */
	public int vartracerWidth, vartracerHeight;
	
	public void getImages() 
	{
		rb = GWT.create(Text.class);
		webLogoGWTClientBundle = GWT.create(WebLogoGWTClientBundle.class);
		webLogoGWTCssResource = webLogoGWTClientBundle.getWebLogoGWTCssResource();
		webLogoGWTCssResource.ensureInjected();
	}
	
	/**
	 * note the separate setSize statement for the stand-alone version
	 */
	public void onModuleLoad() 
	{
		
logger.info("WebLogoGWT onModuleLoad");		
		
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(webLogoGWTCssResource.dock());
		
		// StubView
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		
		// standalone version Paul
		//dlp.setSize("" + breedte + "px", "" + Window.getClientHeight() + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(webLogoGWTCssResource.root());
		
		Stub.publish(this);
		// standalone version Paul
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());

	}
	
	public WebLogoGWT()
	{
		this(null, null, null);
	}
	
	public WebLogoGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		
logger.info("WebLogoGWT constructor");

		ObjectMap h = JSONUtilities.wrapMap(map);
		
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		if (h != null)
			breedte = h.getInt("breedte");
		if (h != null)
			hoogte = h.getInt("hoogte");
		if (h != null)
			launchState = h.getMap("interactiePanelLaunchState");

		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(webLogoGWTCssResource.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		init(breedte, hoogte, launchState, randomVarWaarden);
	}

	/**
	 *  init
	 */
	public void init(int width, int height, Map<String,Object> map, Map<String,Number> values) 
	{
		logger.info("WebLogoGWT uncompiled init");

		// StubView shows a vertical scoll bar?
		this.hoogte = height;
		// take user width instead of fixed width
		this.breedte = width;
		
		ObjectMap launchState = JSONUtilities.wrapMap(map);

		// parametrisation (DWOPlayer version)
		if (launchState != null && launchState.containsKey("uitvoerVeldZichtbaar"))
			uitvoerVeldZichtbaar = launchState.getBoolean("uitvoerVeldZichtbaar");
		if (launchState != null && launchState.containsKey("programmaVeldZichtbaar"))
			programmaVeldZichtbaar = launchState.getBoolean("programmaVeldZichtbaar");
		if (launchState != null && launchState.containsKey("deeltakenZichtbaar"))
			deeltakenZichtbaar = launchState.getBoolean("deeltakenZichtbaar");
		if (launchState != null && launchState.containsKey("whileLoopZichtbaar"))
			whileLoopZichtbaar = launchState.getBoolean("whileLoopZichtbaar");
		if (launchState != null && launchState.containsKey("keuzeCommandZichtbaar"))
			keuzeCommandZichtbaar = launchState.getBoolean("keuzeCommandZichtbaar");
		if (launchState != null && launchState.containsKey("printCommandsZichtbaar"))
			printCommandsZichtbaar = launchState.getBoolean("printCommandsZichtbaar");
		if (launchState != null && launchState.containsKey("tekenCommandsZichtbaar"))
			tekenCommandsZichtbaar = launchState.getBoolean("tekenCommandsZichtbaar");
		if (launchState != null && launchState.containsKey("traceZichtbaar"))
			traceZichtbaar = launchState.getBoolean("traceZichtbaar");
		if (launchState != null && launchState.containsKey("runZichtbaar"))
			runZichtbaar = launchState.getBoolean("runZichtbaar");
		if (launchState != null && launchState.containsKey("codeIOZichtbaar"))
			codeIOZichtbaar = launchState.getBoolean("codeIOZichtbaar");
		// a non-null launchstate from the DWO always contains state
		// in the stand-alone version the launchstate is empty (but non-null)
		if (launchState != null && launchState.containsKey("state"))
			state = (HashMap) launchState.getMap("state");

		// DWOPlayer: program and drawing part
		// override width and height from DWO
		if (uitvoerVeldZichtbaar && programmaVeldZichtbaar)
		{
			// with subroutines
			if (deeltakenZichtbaar)
			{
				// this.breedte = breedteGroot;
				ubb = breedte - jlsBreedteGroot - 3 * offSet;
			}
			else // without subroutines
			{
				// this.breedte = breedteKlein;
				ubb = breedte - jlsBreedteKlein - 3 * offSet;
			}
			jlsHoogte = hoogte - bottomHeight - offSet;
			ubh = jlsHoogte;
		}
		// program part only
		// override width and height from DWO
		else if (!uitvoerVeldZichtbaar && programmaVeldZichtbaar)
		{ // with subroutines
			if (deeltakenZichtbaar)
			{
				// this.breedte = jlsBreedteGroot + 2 * offSet;
			}
			else // without subroutines
			{
				// this.breedte = jlsBreedteKlein + 2 * offSet;
			}
			jlsHoogte = hoogte - bottomHeight - offSet;
			ubh = jlsHoogte;
		}
		// drawing part only
		// use width and height from DWO
		else if (uitvoerVeldZichtbaar && !programmaVeldZichtbaar)
		{
			this.breedte = width;
			this.hoogte = height;
			ubb = this.breedte;
			ubh = this.hoogte;
		}

		// override previous settings in case of stand-alone version:
		// this has empty launchstate (thus non-null) without state
		if (launchState != null && !launchState.containsKey("state"))
		{
			paul = true;
			// use full browser width, fix the program width at jlsBreedteGroot
			// (thus including subroutines)
			// and use the remaining widt for the drawing area
			breedte = Window.getClientWidth();
			ubb = breedte - jlsBreedteGroot - 3 * offSet; // ubbPaul;
			hoogte = Window.getClientHeight(); // hoogtePaul;
			jlsHoogte = hoogte - bottomHeight - offSet;
			ubh = jlsHoogte;
			// use full browser width
			dlp.setWidth("100%");
			// it seems (?) only one 100% statement can be used, so set
			// dlp-height
			// to maximum in onModuleLoad()
			// dlp.setSize("100%","100%");
			// dlp.setHeight("100%");
		}
		else
			dlp.setPixelSize(this.breedte, this.hoogte);

		webLogoPanel = new LayoutPanel();
		webLogoPanel.setPixelSize(this.breedte, this.hoogte);
		webLogoPanel.addStyleName(webLogoGWTCssResource.bottom());

		// System.out.println("this b " + this.breedte);
		// System.out.println("this h " + this.hoogte);

		uitvoerblad = new Tekenblad(this, ubb, ubh);
		Canvas tekenbladCanvas = uitvoerblad.getCanvas();
		if (tekenbladCanvas == null)
		{
			RootPanel.get().add(new Label(upgradeMessage));
			return;
		}

		uitvoerblad.initContext2d();
		webLogoPanel.add(uitvoerblad);

		// position drawing part on webLogoPanel
		if (!uitvoerVeldZichtbaar)
			webLogoPanel.setWidgetVisible(uitvoerblad, false);
		if (uitvoerVeldZichtbaar && programmaVeldZichtbaar && deeltakenZichtbaar)
		{
			webLogoPanel.setWidgetLeftWidth(uitvoerblad, ubxGroot, Style.Unit.PX, ubb, Style.Unit.PX);
			webLogoPanel.setWidgetTopHeight(uitvoerblad, uby, Style.Unit.PX, ubh, Style.Unit.PX);
		}
		else if (uitvoerVeldZichtbaar && programmaVeldZichtbaar && !deeltakenZichtbaar)
		{
			webLogoPanel.setWidgetLeftWidth(uitvoerblad, ubxKlein, Style.Unit.PX, ubb, Style.Unit.PX);
			webLogoPanel.setWidgetTopHeight(uitvoerblad, uby, Style.Unit.PX, ubh, Style.Unit.PX);
		}
		else if (uitvoerVeldZichtbaar && !programmaVeldZichtbaar)
		{
			webLogoPanel.setWidgetLeftWidth(uitvoerblad, 0, Style.Unit.PX, breedte, Style.Unit.PX);
			webLogoPanel.setWidgetTopHeight(uitvoerblad, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		}

		if (deeltakenZichtbaar)
			jlsVeld = new JavaLogoSchuifVeld(0, 0, jlsBreedteGroot, jlsHoogte);
		else
			jlsVeld = new JavaLogoSchuifVeld(0, 0, jlsBreedteKlein, jlsHoogte);

		Canvas jlsvCanvas = jlsVeld.getCanvas();
		if (jlsvCanvas == null)
		{
			RootPanel.get().add(new Label(upgradeMessage));
			return;
		}

		jlsVeld.initContext2d();
		jlsVeld.initialize();

		// position program part on webLogoPanel
		if (programmaVeldZichtbaar)
		{
			webLogoPanel.add(jlsVeld);
			if (deeltakenZichtbaar)
			{
				webLogoPanel.setWidgetLeftWidth(jlsVeld, offSet, Style.Unit.PX, jlsBreedteGroot, Style.Unit.PX);
				webLogoPanel.setWidgetTopHeight(jlsVeld, offSet, Style.Unit.PX, jlsHoogte, Style.Unit.PX);
			}
			else
			{
				webLogoPanel.setWidgetLeftWidth(jlsVeld, offSet, Style.Unit.PX, jlsBreedteKlein, Style.Unit.PX);
				webLogoPanel.setWidgetTopHeight(jlsVeld, offSet, Style.Unit.PX, jlsHoogte, Style.Unit.PX);
			}
		}
		// set parametrization
		jlsVeld.zetDeeltaken(deeltakenZichtbaar);
		jlsVeld.zetWhileLoopZichtbaar(whileLoopZichtbaar);
		jlsVeld.zetKeuzeCommandZichtbaar(keuzeCommandZichtbaar);
		jlsVeld.zetPrintCommandsZichtbaar(printCommandsZichtbaar);
		jlsVeld.zetTekenCommandsZichtbaar(tekenCommandsZichtbaar);

		trb = new TraceBeheerder(uitvoerblad, jlsVeld, this);

		bottomPanel = new LayoutPanel();
		bottomPanel.setPixelSize(breedte, bottomHeight);
		bottomPanel.addStyleName(webLogoGWTCssResource.bottom());

		makeBottom();

		// bottom Panel only if program part is available
		if (programmaVeldZichtbaar)
		{
			webLogoPanel.add(bottomPanel);
			webLogoPanel.setWidgetLeftWidth(bottomPanel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
			webLogoPanel.setWidgetTopHeight(bottomPanel, hoogte - bottomHeight, Style.Unit.PX, bottomHeight,
				Style.Unit.PX);
		}

		dlp.add(webLogoPanel);

		jlsVeld.paint();
		uitvoerblad.initializeDrawing(false);

		if (state != null)
		{
			setState(state);
			logger.info("state != null");
		}

		dlp.forceLayout();
		webLogoPanel.forceLayout();
		bottomPanel.forceLayout();
		jlsVeld.forceLayout();
		uitvoerblad.forceLayout();

		jlsVeld.paint();

		// variable window
		vartracerWidth = 2 * JavaLogoSchuifVeld.ccsw + 12;
		vartracerHeight = 515;
		vartracer = new VardisplayPanel(vartracerWidth, vartracerHeight);
	}
	
	/**
	 * create the buttons and other GUI elements in the bottomPanel depending on the parametrization;
	 * clicking the traceAanKnop makes the other tracing related buttons and other GUI elements visible
	 * hides the traceAanKnop and sets the traceUitKnop visible; <br>
	 * clicking the traceuitKnop hides the other tracing related buttons and other GUI elements 
	 * hides the traceUitKnop and sets the traceAanKnop visible; <br>
	 * note: changing the text on the traceAanKnop does not work without errors   
	 */
	public void	makeBottom()
	{
		int currentX = leftOffset;
		int currentY = topOffset;

		if (codeIOZichtbaar)
		{	
			importButton = new PushButton(rb.importTekst()); 
			importButton.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(importButton);
			bottomPanel.setWidgetLeftWidth(importButton, currentX, Style.Unit.PX, buttonWidth + 20, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(importButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			importButton.addClickHandler(new PushClickHandler());
		
			currentX += leftOffset + buttonWidth + 20;
		
			exportButton = new PushButton(rb.exportTekst()); 
			exportButton.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(exportButton);
			bottomPanel.setWidgetLeftWidth(exportButton, currentX, Style.Unit.PX, buttonWidth + 20, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(exportButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			exportButton.addClickHandler(new PushClickHandler());
		
			currentX += leftOffset + buttonWidth + 20;
		}

		runButton = new PushButton(rb.runTekst()); 
		runButton.addStyleName(webLogoGWTCssResource.pushbutton());
		bottomPanel.add(runButton);
		bottomPanel.setWidgetLeftWidth(runButton, currentX, Style.Unit.PX, buttonWidth - 10, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(runButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		bottomPanel.setWidgetVisible(runButton, runZichtbaar);		
		runButton.addClickHandler(new PushClickHandler());
		
		currentX += leftOffset + buttonWidth - 10;
		
		if (traceZichtbaar)
		{
			traceAanKnop = new PushButton(rb.traceAanTekst()); 
			traceAanKnop.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(traceAanKnop);
			bottomPanel.setWidgetLeftWidth(traceAanKnop, currentX, Style.Unit.PX, buttonWidth + 20, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(traceAanKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			traceAanKnop.addClickHandler(new PushClickHandler());
			
			currentX = leftOffset;
		
			traceUitKnop = new PushButton(rb.traceUitTekst()); //"trace uit");
			traceUitKnop.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(traceUitKnop);
			bottomPanel.setWidgetLeftWidth(traceUitKnop, currentX, Style.Unit.PX, buttonWidth + 20, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(traceUitKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			traceUitKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(traceUitKnop, false);
	
			currentX += leftOffset + buttonWidth + 20;
			
			beginKnop = new PushButton(rb.beginTekst()); //"begin");
			beginKnop.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(beginKnop);
			bottomPanel.setWidgetLeftWidth(beginKnop, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(beginKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			beginKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(beginKnop, false);
		
			currentX += leftOffset + buttonWidth;
			
			stapKnop = new PushButton(rb.stapTekst()); //"stap");
			stapKnop.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(stapKnop);
			bottomPanel.setWidgetLeftWidth(stapKnop, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(stapKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			stapKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(stapKnop, false);
		
			currentX += leftOffset + buttonWidth;

			terugKnop = new PushButton(rb.terugTekst()); //"terug");
			terugKnop.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(terugKnop);
			bottomPanel.setWidgetLeftWidth(terugKnop, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(terugKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			terugKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(terugKnop, false);
		
			currentX += leftOffset + buttonWidth;

			skipKnop = new PushButton(rb.skipTekst()); //"skip");
			skipKnop.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(skipKnop);
			bottomPanel.setWidgetLeftWidth(skipKnop, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(skipKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			skipKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(skipKnop, false);
		
			currentX += leftOffset + buttonWidth;
			
			showVariables = new CheckBox(rb.toonVarsTekst()); //"Toon vars");
			bottomPanel.add(showVariables);
			bottomPanel.setWidgetLeftWidth(showVariables, currentX, Style.Unit.PX, 2 * buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(showVariables, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			showVariables.addValueChangeHandler(new ShowVariablesVCH());
			bottomPanel.setWidgetVisible(showVariables, false);
		
			currentX += leftOffset + 2 * buttonWidth;
			
			methodeLabel = new Label("");
			methodeLabel.addStyleName(webLogoGWTCssResource.label());
			bottomPanel.add(methodeLabel);
			bottomPanel.setWidgetLeftWidth(methodeLabel, currentX, Style.Unit.PX, 8 * buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(methodeLabel, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			bottomPanel.setWidgetVisible(methodeLabel, false);
			
		}
	}

	/**
	 * inner class for handling clicks on buttons  
	 */
    class PushClickHandler implements ClickHandler
    {
    	
    	public void onClick(ClickEvent e)
    	{
			//e.preventDefault();
			e.stopPropagation();
			
			if (e.getSource() == importButton)
			{
				jlsVeld.importFrame();
			}
			else if (e.getSource() == exportButton)
			{
				jlsVeld.exportFrame(jlsVeld.getCode());
			} 
			else if (e.getSource() == runButton)
			{
				if ((jlsVeld.paramEditor != null) && jlsVeld.paramEditor.isVisible())
					jlsVeld.paramEditor.owner.parameterEdited(jlsVeld.paramEditor.getText());
				try
				{
					uitvoerblad.paintDrawing(false);
				}
				catch (Exception exc)
				{
//					logger.log(Level.INFO, "WebLogoGWT.onClick() run button: something went wrong in uitvoerblad.paintDrawing(false), ", e);
				}
				jlsVeld.paint();
				
				// at click on runButton fire cross widget event
				String code = jlsVeld.getCode();
				HashMap<String, Object> inputVars = jlsVeld.getInputVars();
				Map<String,Object> map1 = new HashMap<String,Object>();
				map1.put("program", code);
				map1.put("inputVars", inputVars);
				fireCBookEvent("text.program", map1);
			} 
			else if (e.getSource() == traceAanKnop)
			{
				// hide/show relevant buttons and other Gui elements
				if (codeIOZichtbaar)
				{	bottomPanel.setWidgetVisible(importButton, false);
					bottomPanel.setWidgetVisible(exportButton, false);
				}
				
				bottomPanel.setWidgetVisible(runButton, false);
				
				bottomPanel.setWidgetVisible(traceAanKnop, false);
				bottomPanel.setWidgetVisible(traceUitKnop, true);
				traceAan = true;
				
				bottomPanel.setWidgetVisible(beginKnop, true);
				bottomPanel.setWidgetVisible(stapKnop, true);
				bottomPanel.setWidgetVisible(terugKnop, true);
				bottomPanel.setWidgetVisible(skipKnop, true);
				bottomPanel.setWidgetVisible(showVariables, true);
				bottomPanel.setWidgetVisible(methodeLabel, true);
				methodeLabel.setText("");
				
				// initialize tracing
				trb.traceAanAction();
			} 
			else if (e.getSource() == traceUitKnop)
			{
				// show/hide relevant buttons and other Gui elements
				if (codeIOZichtbaar)
				{	bottomPanel.setWidgetVisible(importButton, true);
					bottomPanel.setWidgetVisible(exportButton, true);
				}
				
				bottomPanel.setWidgetVisible(runButton, true);
				
				bottomPanel.setWidgetVisible(traceAanKnop, true);
				bottomPanel.setWidgetVisible(traceUitKnop, false);
				traceAan = false;
				
				bottomPanel.setWidgetVisible(beginKnop, false);
				bottomPanel.setWidgetVisible(stapKnop, false);
				bottomPanel.setWidgetVisible(terugKnop, false);
				bottomPanel.setWidgetVisible(skipKnop, false);
				bottomPanel.setWidgetVisible(showVariables, false);
				bottomPanel.setWidgetVisible(methodeLabel, false);
				
				showVariables.setValue(false);
				trb.setVartracing(false);
				
				// close tracing
				trb.traceUitAction();
				
			}
			else if (e.getSource() == beginKnop)
			{
				methodeLabel.setText("");
				trb.beginAction();
			}
			else if (e.getSource() == stapKnop)
			{
				trb.stapAction();
			}
			else if (e.getSource() == terugKnop)
			{
				trb.terugAction();
			}
			else if (e.getSource() == skipKnop)
			{
				trb.skipAction();
			}
    	}
    }

    /**
     * inner class to handle changes in showVariables CheckBox 
     */
	class ShowVariablesVCH implements ValueChangeHandler<Boolean>
	{	//public void actionPerformed(ActionEvent e)
		public void onValueChange(ValueChangeEvent<Boolean> e)
		{
			if (e.getSource() == showVariables) 
			{	trb.setVartracing(showVariables.getValue());
			}
		}
	}

	public Widget asWidget()
	{	return dlp;
	}
	
	/**
	 * state is a HashMap containing a String with all code of the program and a 
	 * HashMap containing the (names of  the) variables of the program and their values, see class JavaLogoSchuifVeld
	 */
	public HashMap<String, Object> getState()
	{
		//logger.info("getState");
		HashMap<String, Object> h = new HashMap<String, Object>();
		String code = "";
		code = jlsVeld.getCode();
		HashMap<String,Object> inputVars = jlsVeld.getInputVars();
	    h.put("code", code);
	    h.put("inputVars", inputVars);
		return h;
	}

	/**
	 * set the state using a HashMap containing a String with all code of the program and a 
	 * HashMap containing the (names of  the) variables of the program and their values, see class JavaLogoSchuifVeld 
	 */
	public void setState(HashMap<String, Object> h)
	{
		if ((h == null) || h.isEmpty())
			return;
		//logger.info("setState");
		ObjectMap map = JSONUtilities.wrapMap(h);
		String code = "";
		HashMap<String, Object> inputVars = null;
		if (map.containsKey("code")) 
			code = map.getString("code");
		//logger.info("code = " + code);
		if (map.containsKey("inputVars")) 
			inputVars = (HashMap) map.getMap("inputVars");
		if (inputVars != null)
			jlsVeld.setInputVars(inputVars);
		jlsVeld.importeer(code);
		jlsVeld.paint();
		try
		{
			uitvoerblad.paintDrawing(false);
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, "WebLogoGWT.setState(): error in uitvoerblad.paintDrawing(false)! ", e);
		}
	}

	public int getScore()
	{	return 0;
	}

	public Boolean isCorrect()
	{	return Boolean.TRUE;
	}

	public void kijkNa() 
	{		 
	}
	
	/**
	 * set the comRoot and make sure it can listen to the available CBookEvents 
	 */
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		//logger.info("WebLogoGWT setComRoot");		
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());
		comRoot.addCBookEventListener("text.program", this);
		comRoot.addCBookEventListener("double.input", this);
		comRoot.addCBookEventListener("double.input1", this);
		comRoot.addCBookEventListener("double.input2", this);
		comRoot.addCBookEventListener("double.input3", this);
		comRoot.addCBookEventListener("double.input4", this);
	}
	
	public void zetMode(int mode)
	{
		this.mode = mode;
	}
	
	@Override
	public void zetVolledigeBreedte(int breedte)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public int getAsHoogte()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getHeight() 
	{
		return hoogte;
	}

	public int getWidth() 
	{
		return breedte;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		// TODO Auto-generated method stub
		
	}
	
	//@Override
	public void zetNagekeken(boolean b) {
	}

	//@Override
	public int[][] getScoreObjectives() {
		return null;
	}

	//@Override
	public String[] getSendCmds() {
		String[] commands = {"text.program"};
		return commands;
	}

	//@Override
	public String[] getAcceptedCmds() {
		String[] commands = {"text.program", "double.input", "double.input1", "double.input2", "double.input3", "double.input4"};
		return commands;
	}

	/**
	 * 
	 */
	public void acceptCBookEvent(CBookEvent event)
	{
		String code = "";

		try
		{
			String command = event.getCommand();
			
			logger.info("WebLogoGWT.acceptCBookaccCBookEvent(): " + command);
			
			if (command.startsWith("text"))
			{
				Map map = (Map) event.getParameters();
				if (map != null)
				{
					if ((String) map.get("program") != null)	
					{
						// Logo gives command "text.program" so get program code and program variables 
						// from map and import program
						code = (String) map.get("program");
						HashMap<String, Object> inputVars = (HashMap<String, Object>) map.get("inputVars");
						jlsVeld.setInputVars(inputVars);
						jlsVeld.importeer(code);
						uitvoerblad.paintDrawing(false);
					}
					else if ((String) map.get("content") != null)
					{
						// check-tekstantwoordvak gives command "text.program" and code is in String "content" in map 
						// tekst-antwoordvak gives command "text.program" and code is in String "content" in map
						code = (String) map.get("content");
						HashMap<String, Object> inputVars = jlsVeld.getInputVars(); 
						jlsVeld.setInputVars(inputVars);
						jlsVeld.importeer(code);
						uitvoerblad.paintDrawing(false);
					}
				}
			}
			if (command.startsWith("double"))
			{
				// logger.info("accCBookEv " + command);
				Map map = (Map) event.getParameters();
				
				if (map != null && command.equals("double.input"))
				{
					String name = (String) map.get("name");
					double waarde;
					if (map.get("value") != null)
					{
						// Slider generates the command "double.input" with the name and value of the variable
						// in "name"and "value" in map
						waarde = ((Double) map.get("value")).doubleValue();
					}
					else if (map.get("text") != null)
					{
						// FEWA generates command "double.input" with the the name and value of the variable
						// in "name"and "text" in map
						waarde = Double.parseDouble((String) map.get("text"));
						if (name == null)
						{	name = "text";
						}
					}
					else
					{
						waarde = 0; // some error
					}
					jlsVeld.setInputVar(name, waarde);
					uitvoerblad.paintDrawing(false);
					
					// als in init(); weet niet zeker of deze forceLayout(0)s echt nodig zijn...
					// probably not necessary
					dlp.forceLayout();
					webLogoPanel.forceLayout();
					bottomPanel.forceLayout();
					jlsVeld.forceLayout();
					uitvoerblad.forceLayout();
					jlsVeld.paint();
				}
				// name of variable in command, value in map
				else if (map != null && command.startsWith("double.input"))
				{
					String name = command.substring(command.length() - 1);
					double waarde = ((Double) map.get("value")).doubleValue();
					jlsVeld.setInputVar(name, waarde);
					uitvoerblad.paintDrawing(false);
				}
				// name of variable in command, value in event Message
				else if (map == null && command.startsWith("double.input"))
				{
					String message = event.getMessage();
					double waarde = Double.parseDouble(message);
					String name = command.substring(command.length() - 1);
					jlsVeld.setInputVar(name, waarde);
					uitvoerblad.paintDrawing(false);
				}
				// kan map leeg zijn met andere commands "input..."?
			}
			
			// make sure changes are sent to other cross widget links
			fireCBookEventCurrentProgram();
		}
		catch (Exception e)
		{
			// something went wrong,
			// probably the code was not valid
			// or NPE in uitvoerblad.paintDrawing(false), Tekenblad.tekenPolygon() with veelvlak.aantalPunten 0
			// this error was corrected
			logger.log(Level.INFO, "WebLogoGWT.acceptCBookEvent(): error! code = " + code + ", " , e);
		}
	}
	
	/**
	 * Fire CBook event with the current program
	 * code and current variable names and their values.
	 */
	private void fireCBookEventCurrentProgram()
	{
		String code = jlsVeld.getCode();
		HashMap<String, Object> inputVars = jlsVeld.getInputVars();
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("program", code);
		map1.put("inputVars", inputVars);
		fireCBookEvent("text.program", map1);
	}

	/**
	 * Fire cross widget event with the given command and map.
	 * @param command the command
	 * @param map the Map
	 */
	public void fireCBookEvent(String command, Map<String, Object> map)
	{
		if (comRoot != null)
		{	CBookEvent event = new CBookEvent(this, command, map);
			comRoot.fireEvent(event);
		}
	}
	
	//@Override
	public String getLocalizedCmd(String cmd) 
	{
		String localizedCmd = null; 
		if (localizedCmd == null)
			return cmd;
		return localizedCmd;
	}
}
