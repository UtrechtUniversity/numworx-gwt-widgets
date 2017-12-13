package fi.weblogo3dgwt.client;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;

import nl.uu.fi.dwo.interaction.client.InteractionView;
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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.resources.client.ImageResource;

import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

import java.util.logging.Level;
import java.util.logging.Logger;

import fi.weblogo3dgwt.client.text.Text;

/**
 * see also class WebLogoGWT 
 * @author huub
 */

public class WebLogo3dGWT implements EntryPoint, InteractionStub, InteractionView, CBookEventListener 
{
	
	public static Text rb;
	// logger
    static Logger logger = Logger.getLogger("WebLogo3dGWT");

    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	WebLogo3dGWTClientBundle webLogo3dGWTClientBundle;
	static WebLogo3dGWTCssResource webLogo3dGWTCssResource;

	// UI
	DockLayoutPanel dlp;
	LayoutPanel bottomPanel;
	LayoutPanel webLogoPanel;
	JavaLogoSchuifVeld jlsVeld;
	public TraceBeheerder trb;
	/**
	 * instance of the 3D drawing class
	 */
	public TekenApplet3D uitvoerblad;
	
	int buttonWidth = 45;
	int buttonHeight = 22;
	int buttonSize = 22;

	static int jlsBreedteKlein = 390;
	static int jlsBreedteGroot = 620;
	
	int offSet = 4;
	int leftOffset = 5;
	int topOffset = 5;
	
	boolean paul = false;
	int breedteGroot = 784; //784 is maximale breedte in popupFacade;
	int breedteKlein = 700;
	int breedtePaul = 950; //maximale breedte stand-alone 
	int breedte = 784;
	int hoogte = 575;
	int hoogtePaul = 575;
	int bottomHeight = 32;
	int jlsHoogte = hoogte - bottomHeight - offSet;
	int ubxKlein = jlsBreedteKlein + 2 * offSet; 
	int ubxGroot = jlsBreedteGroot + 2 * offSet;
	int uby = offSet;
	int ubbKlein = breedteKlein - jlsBreedteKlein - 3 * offSet; 
	int ubbGroot = breedteGroot - jlsBreedteGroot - 3 * offSet;
	int ubbPaul = breedtePaul - jlsBreedteGroot - 3 * offSet;
	int ubb = 0;
	int ubh = jlsHoogte; 
	
	public static String fontString = "12px sans-serif";
	public static String boldFontString = "bold 12px sans-serif";
	
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	// parametrisatie
	boolean uitvoerVeldZichtbaar = true;
	boolean transparantOptie = true;
	boolean draadFiguurOptie = true;
	boolean zoomOptie = true;
	boolean programmaVeldZichtbaar = true;
	boolean deeltakenZichtbaar = true;
	boolean whileLoopZichtbaar = true;
	boolean keuzeCommandZichtbaar = true;
	boolean printCommandsZichtbaar = true;
	boolean tekenCommandsZichtbaar = true;
	boolean traceZichtbaar = true;
	boolean codeIOZichtbaar = true;
	
	/**
	 * parametrisation 3D: transparent/solid
	 */
	boolean transparant = false;
	/**
	 * parametrisation 3D: wireframe/solid
	 */
	boolean draadFiguur = false;
	/**
	 * parametrisation 3D: zoomfactor; keep this attribute here, since it is changed by
	 * the zoom buttons and used by getState and setState     
	 */
	double zoomFactor = 1;
	
	HashMap state = null;

	boolean traceAan = false;
	
	boolean correct = false;
	private int mode;
	private OpdrNavIF comRoot;
		
	PushButton importButton, exportButton, runButton;
	PushButton traceAanKnop, traceUitKnop, beginKnop, stapKnop, terugKnop, skipKnop;
	CheckBox showVariables;
	public Label methodeLabel;
	/**
	 * 3D only: make figure solid, visible if figure is a wireframe 
	 */
	PushButton solidButton;
	/**
	 * 3D only: make figure a wireframe, visible if figure is solid
	 */
	PushButton draadButton;
	/**
	 * 3D only: make figure non-transparent, visible if figure is transparent
	 */
	PushButton opaqueButton;
	/**
	 * 3D only: make figure transparent, visible if figure is opaque
	 */
	PushButton transparantButton;
	/**
	 * 3D only: zooming in
	 */
	PushButton zoomInButton;
	/**
	 * 3D only: zooming out
	 */
	PushButton zoomUitButton;
	
	public VardisplayPanel vartracer = null;
	
	public int vartracerWidth, vartracerHeight;

	/**
	 * ImageResources for 3D only buttons
	 */
	ImageResource solidResource, draadResource, opaqueResource, transparantResource, zoomInResource, zoomUitResource;
	/**
	 * images for 3D only buttons
	 */
	Image solidImage, draadImage, opaqueImage, transparantImage, zoomInImage, zoomUitImage;
	
	public void getImages() 
	{
		rb = GWT.create(Text.class);
		
		webLogo3dGWTClientBundle = GWT.create(WebLogo3dGWTClientBundle.class);
		webLogo3dGWTCssResource = webLogo3dGWTClientBundle.getWebLogo3dGWTCssResource();
		webLogo3dGWTCssResource.ensureInjected();
		
		solidResource = webLogo3dGWTClientBundle.solidResource();
		solidImage = new Image(solidResource);
		solidImage.addStyleName(webLogo3dGWTCssResource.pushimage());		
		
		draadResource = webLogo3dGWTClientBundle.draadResource();
		draadImage = new Image(draadResource);
		draadImage.addStyleName(webLogo3dGWTCssResource.pushimage());		
		
		opaqueResource = webLogo3dGWTClientBundle.opaqueResource();
		opaqueImage = new Image(opaqueResource);
		opaqueImage.addStyleName(webLogo3dGWTCssResource.pushimage());		
		
		transparantResource = webLogo3dGWTClientBundle.transparantResource();
		transparantImage = new Image(transparantResource);
		transparantImage.addStyleName(webLogo3dGWTCssResource.pushimage());		
		
		zoomInResource = webLogo3dGWTClientBundle.zoomInResource();
		zoomInImage = new Image(zoomInResource);
		zoomInImage.addStyleName(webLogo3dGWTCssResource.pushimage());		

		zoomUitResource = webLogo3dGWTClientBundle.zoomUitResource();
		zoomUitImage = new Image(zoomUitResource);
		zoomUitImage.addStyleName(webLogo3dGWTCssResource.pushimage());		
		

	}	
	public void onModuleLoad() 
	{
		
logger.info("WebLogo3dGWT onModuleLoad");		
		
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(webLogo3dGWTCssResource.dock());
		// StubView
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		// standalone versie Paul
		//dlp.setSize("" + breedte + "px", "" + (Window.getClientHeight()-20) + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(webLogo3dGWTCssResource.root());
		
		Stub.publish(this);
		// standalone versie Paul
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
			
	}
	
	public WebLogo3dGWT()
	{
		this(null, null, null);
	}
	
	public WebLogo3dGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		
logger.info("WebLogo3dGWT constructor");

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
		dlp.addStyleName(webLogo3dGWTCssResource.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");


		init(breedte, hoogte, launchState, randomVarWaarden);


	}



	public void init(int width, int height, Map<String,Object> map, Map<String,Number> values) 
	{
		
logger.info("WebLogo3dGWT uncompiled init");

			this.breedte = width;
			this.hoogte = height;
			
			ObjectMap launchState = JSONUtilities.wrapMap(map);
			
			if (launchState != null && launchState.containsKey("uitvoerVeldZichtbaar")) 
				uitvoerVeldZichtbaar = launchState.getBoolean("uitvoerVeldZichtbaar");
			if (launchState != null && launchState.containsKey("transparantOptie")) 
				transparantOptie = launchState.getBoolean("transparantOptie");
			if (launchState != null && launchState.containsKey("draadFiguurOptie")) 
				draadFiguurOptie = launchState.getBoolean("draadFiguurOptie");
			if (launchState != null && launchState.containsKey("zoomOptie")) 
				zoomOptie = launchState.getBoolean("zoomOptie");

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
			if (launchState != null && launchState.containsKey("codeIOZichtbaar")) 
				codeIOZichtbaar = launchState.getBoolean("codeIOZichtbaar");			
			
			if (launchState != null && launchState.containsKey("state")) 
				state = (HashMap) launchState.getMap("state");
			
			if (uitvoerVeldZichtbaar && programmaVeldZichtbaar)
			{
				if (deeltakenZichtbaar)
				{
					//this.breedte = breedteGroot;
					ubb = ubbGroot;
				}
				else
				{
					//this.breedte = breedteKlein;
					ubb = ubbKlein;
				}
				jlsHoogte = hoogte - bottomHeight - offSet;
				ubh = jlsHoogte;
			}
			else if (!uitvoerVeldZichtbaar && programmaVeldZichtbaar)
			{
				if (deeltakenZichtbaar)
				{
					//this.breedte = jlsBreedteGroot + 2 * offSet;
				}
				else
				{
					//this.breedte = jlsBreedteKlein + 2 * offSet;
				}
				jlsHoogte = hoogte - bottomHeight - offSet;
				ubh = jlsHoogte;
			}
			else if (uitvoerVeldZichtbaar && !programmaVeldZichtbaar)
			{
				this.breedte = width;
				this.hoogte = height;
				ubb = this.breedte;
				ubh = this.hoogte;
				
			}
			
			// stand-alone
			if (launchState != null && !launchState.containsKey("state"))
			{
//System.out.println("paul");				
				paul = true;
				breedte = Window.getClientWidth(); 
				ubb = breedte - jlsBreedteGroot - 3 * offSet; 
				hoogte = Window.getClientHeight()-20; 
				jlsHoogte = hoogte - bottomHeight - offSet;
				ubh = jlsHoogte;
				dlp.setWidth("100%");
//System.out.println("hoogte = " + hoogte);				
			}
			else
				dlp.setSize("" + this.breedte + "px", "" + this.hoogte + "px");
			
			webLogoPanel = new LayoutPanel();
			webLogoPanel.setSize("" + this.breedte + "px", "" + this.hoogte + "px");
			webLogoPanel.addStyleName(webLogo3dGWTCssResource.bottom());
			
			if (uitvoerVeldZichtbaar && (transparantOptie || draadFiguurOptie || zoomOptie))
			{	
				ubh -= (buttonSize + 8);
				
			}
			
			uitvoerblad = new TekenApplet3D(this,ubb,ubh);
			Canvas tekenbladCanvas = uitvoerblad.getCanvas();
			if (tekenbladCanvas == null) 
			{
		      RootPanel.get().add(new Label(upgradeMessage));
		      return;
		    }
			
			uitvoerblad.initContext2d();
			
			int boxX = 0;
			
			webLogoPanel.add(uitvoerblad);
			if (!uitvoerVeldZichtbaar)
				webLogoPanel.setWidgetVisible(uitvoerblad,false);
			if (uitvoerVeldZichtbaar && programmaVeldZichtbaar && deeltakenZichtbaar)
			{	
				webLogoPanel.setWidgetLeftWidth(uitvoerblad, ubxGroot, Style.Unit.PX, ubb, Style.Unit.PX);
				webLogoPanel.setWidgetTopHeight(uitvoerblad, uby, Style.Unit.PX, ubh, Style.Unit.PX);
				boxX = ubxGroot + 5;
			}
			else if (uitvoerVeldZichtbaar && programmaVeldZichtbaar && !deeltakenZichtbaar)
			{
				webLogoPanel.setWidgetLeftWidth(uitvoerblad, ubxKlein, Style.Unit.PX, ubb, Style.Unit.PX);
				webLogoPanel.setWidgetTopHeight(uitvoerblad, uby, Style.Unit.PX, ubh, Style.Unit.PX);
				boxX = ubxKlein + 5;
			}
			else if (uitvoerVeldZichtbaar && !programmaVeldZichtbaar)
			{
				webLogoPanel.setWidgetLeftWidth(uitvoerblad, 0, Style.Unit.PX, breedte, Style.Unit.PX);
				webLogoPanel.setWidgetTopHeight(uitvoerblad, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
				boxX = 5;
			}
			
			if (uitvoerVeldZichtbaar)
			{
				int currentX = boxX;
			
				// zet hier de defaults
				// setState zet de actuele opties
				transparantButton = new PushButton(transparantImage);
				opaqueButton = new PushButton(opaqueImage);
				draadButton = new PushButton(draadImage);
				solidButton = new PushButton(solidImage);
				zoomInButton = new PushButton(zoomInImage);
				zoomUitButton = new PushButton(zoomUitImage);
				
				if (transparantOptie)
				{
					webLogoPanel.add(transparantButton);
					webLogoPanel.setWidgetLeftWidth(transparantButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
					webLogoPanel.setWidgetTopHeight(transparantButton, ubh+5, Style.Unit.PX, buttonSize, Style.Unit.PX);
					transparantButton.addClickHandler(new PushClickHandler());
					webLogoPanel.add(opaqueButton);
					webLogoPanel.setWidgetLeftWidth(opaqueButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
					webLogoPanel.setWidgetTopHeight(opaqueButton, ubh+5, Style.Unit.PX, buttonSize, Style.Unit.PX);
					opaqueButton.addClickHandler(new PushClickHandler());
					webLogoPanel.setWidgetVisible(opaqueButton,false);
					currentX += buttonSize + 5;
				}
				if (draadFiguurOptie)
				{	
					webLogoPanel.add(draadButton);
					webLogoPanel.setWidgetLeftWidth(draadButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
					webLogoPanel.setWidgetTopHeight(draadButton, ubh+5, Style.Unit.PX, buttonSize, Style.Unit.PX);
					draadButton.addClickHandler(new PushClickHandler());
					webLogoPanel.add(solidButton);
					webLogoPanel.setWidgetLeftWidth(solidButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
					webLogoPanel.setWidgetTopHeight(solidButton, ubh+5, Style.Unit.PX, buttonSize, Style.Unit.PX);
					solidButton.addClickHandler(new PushClickHandler());
					webLogoPanel.setWidgetVisible(solidButton,false);
					currentX += buttonSize + 5;
				}
				if (zoomOptie)
				{
					webLogoPanel.add(zoomInButton);
					webLogoPanel.setWidgetLeftWidth(zoomInButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
					webLogoPanel.setWidgetTopHeight(zoomInButton, ubh+5, Style.Unit.PX, buttonSize, Style.Unit.PX);
					zoomInButton.addClickHandler(new PushClickHandler());
					currentX += buttonSize + 5;
					webLogoPanel.add(zoomUitButton);
					webLogoPanel.setWidgetLeftWidth(zoomUitButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
					webLogoPanel.setWidgetTopHeight(zoomUitButton, ubh+5, Style.Unit.PX, buttonSize, Style.Unit.PX);
					zoomUitButton.addClickHandler(new PushClickHandler());
					
				}
			}
			
			
			if (deeltakenZichtbaar)
				jlsVeld = new JavaLogoSchuifVeld(0,0,jlsBreedteGroot,jlsHoogte,uitvoerblad);
			else
				jlsVeld = new JavaLogoSchuifVeld(0,0,jlsBreedteKlein,jlsHoogte,uitvoerblad);
			
			Canvas jlsvCanvas = jlsVeld.getCanvas();
			if (jlsvCanvas == null) 
			{
		      RootPanel.get().add(new Label(upgradeMessage));
		      return;
		    }
			
			jlsVeld.initContext2d();
			jlsVeld.initialize();

			if (programmaVeldZichtbaar)
			{	
				webLogoPanel.add(jlsVeld);
				if (deeltakenZichtbaar)
				{	webLogoPanel.setWidgetLeftWidth(jlsVeld, offSet, Style.Unit.PX, jlsBreedteGroot, Style.Unit.PX);
					webLogoPanel.setWidgetTopHeight(jlsVeld, offSet, Style.Unit.PX, jlsHoogte, Style.Unit.PX);
				}	
				else
				{
					webLogoPanel.setWidgetLeftWidth(jlsVeld, offSet, Style.Unit.PX, jlsBreedteKlein, Style.Unit.PX);
					webLogoPanel.setWidgetTopHeight(jlsVeld, offSet, Style.Unit.PX, jlsHoogte, Style.Unit.PX);
				}
			}
			jlsVeld.zetDeeltaken(deeltakenZichtbaar);
			jlsVeld.zetWhileLoopZichtbaar(whileLoopZichtbaar);
			jlsVeld.zetKeuzeCommandZichtbaar(keuzeCommandZichtbaar);
			jlsVeld.zetPrintCommandsZichtbaar(printCommandsZichtbaar);
			jlsVeld.zetTekenCommandsZichtbaar(tekenCommandsZichtbaar);
			
			trb = new TraceBeheerder(uitvoerblad, jlsVeld, this);
			
			bottomPanel = new LayoutPanel();
			bottomPanel.setSize("" + breedte + "px", "" + bottomHeight + "px");
			bottomPanel.addStyleName(webLogo3dGWTCssResource.bottom());
			
			makeBottom();
			
			if (programmaVeldZichtbaar)
			{	
				webLogoPanel.add(bottomPanel);
				webLogoPanel.setWidgetLeftWidth(bottomPanel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
				webLogoPanel.setWidgetTopHeight(bottomPanel, hoogte-bottomHeight, Style.Unit.PX, bottomHeight, Style.Unit.PX);
			}
			
			
			dlp.add(webLogoPanel);
			
			jlsVeld.paint();
			uitvoerblad.paintTekenblad();

			if (state != null)
			{	
logger.info("state != null");				
				setState(state);
			}
			
			dlp.forceLayout();
			webLogoPanel.forceLayout();
			bottomPanel.forceLayout();
			jlsVeld.forceLayout();
			uitvoerblad.forceLayout();
			
			jlsVeld.paint();			
			
			vartracerWidth = 2*JavaLogoSchuifVeld.ccsw+12;
			vartracerHeight = 515;
			vartracer = new VardisplayPanel(vartracerWidth, vartracerHeight);
	}
	
	public void	makeBottom()
	{
		int currentX = leftOffset;
		int currentY = topOffset;

		if (codeIOZichtbaar)
		{	
			importButton = new PushButton(rb.importTekst()); //"import");
			importButton.addStyleName(webLogo3dGWTCssResource.pushbutton());
			bottomPanel.add(importButton);
			bottomPanel.setWidgetLeftWidth(importButton, currentX, Style.Unit.PX, buttonWidth + 20, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(importButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			importButton.addClickHandler(new PushClickHandler());
		
			currentX += leftOffset + buttonWidth + 20;
		
			exportButton = new PushButton(rb.exportTekst()); //"export");
			exportButton.addStyleName(webLogo3dGWTCssResource.pushbutton());
			bottomPanel.add(exportButton);
			bottomPanel.setWidgetLeftWidth(exportButton, currentX, Style.Unit.PX, buttonWidth + 20, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(exportButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			exportButton.addClickHandler(new PushClickHandler());
		
			currentX += leftOffset + buttonWidth + 20;
		}

		runButton = new PushButton(rb.runTekst()); //"run");
		runButton.addStyleName(webLogo3dGWTCssResource.pushbutton());
		bottomPanel.add(runButton);
		bottomPanel.setWidgetLeftWidth(runButton, currentX, Style.Unit.PX, buttonWidth - 10, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(runButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		runButton.addClickHandler(new PushClickHandler());
		
		currentX += leftOffset + buttonWidth - 10;
		
		if (traceZichtbaar)
		{
			traceAanKnop = new PushButton(rb.traceAanTekst()); //"trace aan");
			traceAanKnop.addStyleName(webLogo3dGWTCssResource.pushbutton());
			bottomPanel.add(traceAanKnop);
			bottomPanel.setWidgetLeftWidth(traceAanKnop, currentX, Style.Unit.PX, buttonWidth + 20, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(traceAanKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			traceAanKnop.addClickHandler(new PushClickHandler());
			
			currentX = leftOffset;
		
			traceUitKnop = new PushButton(rb.traceUitTekst()); //"trace uit");
			traceUitKnop.addStyleName(webLogo3dGWTCssResource.pushbutton());
			bottomPanel.add(traceUitKnop);
			bottomPanel.setWidgetLeftWidth(traceUitKnop, currentX, Style.Unit.PX, buttonWidth + 20, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(traceUitKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			traceUitKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(traceUitKnop, false);
	
			currentX += leftOffset + buttonWidth + 20;
			
			beginKnop = new PushButton(rb.beginTekst()); //"begin");
			beginKnop.addStyleName(webLogo3dGWTCssResource.pushbutton());
			bottomPanel.add(beginKnop);
			bottomPanel.setWidgetLeftWidth(beginKnop, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(beginKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			beginKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(beginKnop, false);
		
			currentX += leftOffset + buttonWidth;
			
			stapKnop = new PushButton(rb.stapTekst()); //"stap");
			stapKnop.addStyleName(webLogo3dGWTCssResource.pushbutton());
			bottomPanel.add(stapKnop);
			bottomPanel.setWidgetLeftWidth(stapKnop, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(stapKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			stapKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(stapKnop, false);
		
			currentX += leftOffset + buttonWidth;

			terugKnop = new PushButton(rb.terugTekst()); //"terug");
			terugKnop.addStyleName(webLogo3dGWTCssResource.pushbutton());
			bottomPanel.add(terugKnop);
			bottomPanel.setWidgetLeftWidth(terugKnop, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(terugKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			terugKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(terugKnop, false);
		
			currentX += leftOffset + buttonWidth;

			skipKnop = new PushButton(rb.skipTekst()); //"skip");
			skipKnop.addStyleName(webLogo3dGWTCssResource.pushbutton());
			bottomPanel.add(skipKnop);
			bottomPanel.setWidgetLeftWidth(skipKnop, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(skipKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			skipKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(skipKnop, false);
		
			currentX += leftOffset + buttonWidth;
			
			showVariables = new CheckBox(rb.toonVarsTekst()); //"Toon vars");
			bottomPanel.add(showVariables);
			bottomPanel.setWidgetLeftWidth(showVariables, currentX, Style.Unit.PX, 2 * buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(showVariables, currentY+3, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			showVariables.addValueChangeHandler(new ShowVariablesVCH());
			bottomPanel.setWidgetVisible(showVariables, false);
		
			currentX += leftOffset + 2 * buttonWidth;
			
			// hier nog een label/noneditable TextBox
			methodeLabel = new Label("");
			methodeLabel.addStyleName(webLogo3dGWTCssResource.label());
			bottomPanel.add(methodeLabel);
			bottomPanel.setWidgetLeftWidth(methodeLabel, currentX, Style.Unit.PX, 8 * buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(methodeLabel, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			bottomPanel.setWidgetVisible(methodeLabel, false);
			
		}

	}

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
				
//System.out.println("runButton");

				if ((jlsVeld.paramEditor != null) && jlsVeld.paramEditor.isVisible())
					jlsVeld.paramEditor.owner.parameterEdited(jlsVeld.paramEditor.getText());	
				uitvoerblad.paintDrawing(false);
				jlsVeld.paint();
				
				// bij klik op run-knop cross widget event afvuren
				String code = jlsVeld.getCode();
				HashMap<String, Object> inputVars = jlsVeld.getInputVars();
				Map<String,Object> map1 = new HashMap<String,Object>();
				map1.put("program", code);
				map1.put("inputVars", inputVars);
				fireCBookEvent("text.program", map1);
				
			} 
			else if (e.getSource() == traceAanKnop)
			{
//System.out.println("click traceAan");

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
				
				trb.traceAanAction();
			} 
			else if (e.getSource() == traceUitKnop)
			{
//System.out.println("click traceUit");

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

			// toggle visibility of transparantButton and opaqueButton 
			else if (e.getSource() == transparantButton)
			{	transparant = true;
				uitvoerblad.zetTransparant(true);
				webLogoPanel.setWidgetVisible(transparantButton, false);
				webLogoPanel.setWidgetVisible(opaqueButton, true);
			}
			else if (e.getSource() == opaqueButton)
			{	transparant = false;
				uitvoerblad.zetTransparant(false);
				webLogoPanel.setWidgetVisible(transparantButton, true);
				webLogoPanel.setWidgetVisible(opaqueButton, false);
			}
			
			// toggle visibility of solidButton and draadButton
			else if (e.getSource() == solidButton)
			{	draadFiguur = false;
				uitvoerblad.zetDraadFiguur(false);
				webLogoPanel.setWidgetVisible(solidButton, false);
				webLogoPanel.setWidgetVisible(draadButton, true);
			}
			else if (e.getSource() == draadButton)
			{	draadFiguur = true;
				uitvoerblad.zetDraadFiguur(true);
				webLogoPanel.setWidgetVisible(solidButton, true);
				webLogoPanel.setWidgetVisible(draadButton, false);
			}

			// adapt zoom factor for getState/setState
			else if (e.getSource() == zoomInButton)
			{	uitvoerblad.zoomIn();
				zoomFactor *= 11e-1d;
			}
			else if (e.getSource() == zoomUitButton)
			{	uitvoerblad.zoomUit();
				zoomFactor *= 91e-2d;
			}
    		
    	}
    }
    
	class ShowVariablesVCH implements ValueChangeHandler<Boolean>
	{	
		public void onValueChange(ValueChangeEvent<Boolean> e)
		{
			if (e.getSource() == showVariables) 
			{	trb.setVartracing(showVariables.getValue());
			}
		}
	}

	public Widget asWidget()
	{
		return dlp;
	}
	
	@Override
	public HashMap<String, Object> getState()
	{
		
logger.info("getState");

		HashMap<String, Object> h = new HashMap<String, Object>();
		
		String code = "";
	
		code = jlsVeld.getCode();
		HashMap<String,Object> inputVars = jlsVeld.getInputVars();
		 
	    h.put("code", code);
	    h.put("inputVars", inputVars);

	    // save rotation
	    h.put("draaiX", new Double(uitvoerblad.geefDraaiX()));
	    h.put("draaiY", new Double(uitvoerblad.geefDraaiY()));
	    
	    // save 3D settings
	    h.put("transparant", new Boolean(transparant));
	    h.put("draadFiguur", new Boolean(draadFiguur));
	    h.put("zoomFactor", new Double(zoomFactor));
    	
		return h;

	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		if ((h == null) || h.isEmpty())
			return;
logger.info("setState");

		ObjectMap map = JSONUtilities.wrapMap(h);
		
		String code = "";
		HashMap<String, Object> inputVars = null;
		
		if (map.containsKey("code")) 
			code = map.getString("code");
		if (map.containsKey("inputVars")) 
			inputVars = (HashMap) map.getMap("inputVars");
		
		if (inputVars != null)
			jlsVeld.setInputVars(inputVars);
		jlsVeld.importeer(code);
		jlsVeld.paint();
		
		// get roration
		double draaiX = 0;
		double draaiY = 0;
		if(map.containsKey("draaiX")) 
			draaiX = map.getDouble("draaiX");
		if(map.containsKey("draaiY")) 
			draaiY = map.getDouble("draaiY");

		// get 3D settings
		if (map.containsKey("transparant"))
			transparant = map.getBoolean("transparant");
		if (map.containsKey("draadFiguur"))
			draadFiguur = map.getBoolean("draadFiguur");
		if (map.containsKey("zoomFactor"))
			zoomFactor = map.getDouble("zoomFactor");
		
		// set 3D settings
		uitvoerblad.zetTransparant(transparant);
		uitvoerblad.zetDraadFiguur(draadFiguur);
		uitvoerblad.zoom(zoomFactor);

		// set rotation
		uitvoerblad.zetBeginHoeken(draaiX,draaiY);		
		
		uitvoerblad.paintDrawing(false);
	}

	@Override
	public int getScore()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean isCorrect()
	{
		// TODO Auto-generated method stub
		return correct;
	}

	@Override
	public void kijkNa() 
	{		 

	}
	
	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
logger.info("WebLogo3dGWT setComRoot");		
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
	{	this.mode = mode;
	}
	
	@Override
	public void zetVolledigeBreedte(int breedte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getAsHoogte() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() 
	{
		return hoogte;
	}

	@Override
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

	public String[] getSendCmds() {
		String[] commands = {"text.program"};
		return commands;
	}

	//@Override
	public String[] getAcceptedCmds() {
		String[] commands = {"text.program", "double.input", "double.input1", "double.input2", "double.input3", "double.input4"};
		return commands;
	}

	@Override
	public void acceptCBookEvent(CBookEvent event)
	{
		String code = "";

		try
		{
			String command = event.getCommand();
			if (command.startsWith("text"))
			{
				logger.info("accCBookEv " + command);
				
				Map map = (Map) event.getParameters();
				if (map != null)
				{
					if ((String) map.get("program") != null)	
					{
						// Logo geeft command "text.program" en de code in "program" in map
						
						code = (String) map.get("program");
						HashMap<String, Object> inputVars = (HashMap<String, Object>) map.get("inputVars");
						jlsVeld.setInputVars(inputVars);
						jlsVeld.importeer(code);
						uitvoerblad.paintDrawing(false);
					}
					else if ((String) map.get("content") != null)
					{
						// check-tekstantwoordvak geeft command "text.program" en de code in "content" in map (en "logID" 0)
						// tekst-antwoordvak geeft command "text.program" en de code in "content" in map
						
						code = (String) map.get("content");
						HashMap<String, Object> inputVars = jlsVeld.getInputVars(); // moet dit?
						jlsVeld.setInputVars(inputVars);
						jlsVeld.importeer(code);
						uitvoerblad.paintDrawing(false);
					}
				}
			}
			if (command.startsWith("double"))
			{
				logger.info("accCBookEv " + command);
				
				Map map = (Map) event.getParameters();
				
				if (map != null && command.equals("double.input"))
				{
					String name = (String) map.get("name");
					double waarde;
					if (map.get("value") != null)
					{
						// Slider geeft command "double.input" met de waarde in "value" in map
						
						waarde = ((Double) map.get("value")).doubleValue();
					}
					else if (map.get("text") != null)
					{
						// FEWA geeft command "double.input" met de waarde in "text" in map
						
						waarde = Double.parseDouble((String) map.get("text"));
						if (name == null)
						{
							name = "text";
						}
					}
					else
					{
						waarde = 0; // er is iets mis gegaan...
					}
					jlsVeld.setInputVar(name, waarde);
					// er moet nog iets gebeuren om te zorgen dat veelvlak op Tekenblad punten heeft om te tekenen. Die ontbreken!
					uitvoerblad.paintDrawing(false);
					
					// als in init(); weet niet zeker of deze forceLayout(0)s echt nodig zijn...
					dlp.forceLayout();
					webLogoPanel.forceLayout();
					bottomPanel.forceLayout();
					jlsVeld.forceLayout();
					uitvoerblad.forceLayout();

					// ook deze voor het wegwerken van de rode tekst?
					jlsVeld.paint();
				}
				else if (map != null && command.startsWith("double.input"))
				{
					String name = command.substring(command.length() - 1);
					double waarde = ((Double) map.get("value")).doubleValue();
					jlsVeld.setInputVar(name, waarde);
					uitvoerblad.paintDrawing(false);
				}
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
			
			// geef de gedane wijzigingen door aan mogelijke andere cross widget links
			fireCBookEventCurrentProgram();
		}
		catch (Exception e)
		{
			// something went wrong,
			// probably the code was not valid
			// or NPE in uitvoerblad.paintDrawing(false), Tekenblad.tekenPolygon() waar veelvlak.aantalPunten 0 is
			logger.log(Level.INFO, "WebLogoGWT.acceptCBookEvent(): error! code = " + code + ", " , e);
			//e.printStackTrace();
		}
	}
	
	/**
	 * Fire cbook event with the current program
	 * code and current input variables.
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
		{
			CBookEvent event = new CBookEvent(this, command, map);
			comRoot.fireEvent(event);
		}
	}
	
	//@Override
	public String getLocalizedCmd(String cmd) {
		
		String localizedCmd = null; 
		if (localizedCmd == null)
			return cmd;
		return localizedCmd;
	}

}
