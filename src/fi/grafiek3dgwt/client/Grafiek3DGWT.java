package fi.grafiek3dgwt.client;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;

import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.resources.client.ImageResource;

import fi.grafiek3dgwt.client.formuleobjects.*;
import fi.grafiek3dgwt.client.expressies.*;

import fi.grafiek3dgwt.client.text.Text;

/**
 * main class for Grafiek3DGWT; note that the Object3D which is shown 
 * (a 3d-graph, 3d-surface or 3d-curve) is defined in the Java-version
 * Grafiek3D inside the DWO; the user can also (optionally) look at  
 * built-in examples of the 3 types of 3d-objects;
 * the launch data also include possible options to modify the
 * 3-d figure, such as zooming in or -out, translating the figure
 * along of of the axes, showing the figure as a solid or a wireframe,
 * refining the approximation to the figure, showing axes, 
 * showing labels on the axes, choosing the type of projection.
 * @author huub
 */
		

public class Grafiek3DGWT implements EntryPoint, InteractionStub //, InteractionView
{
	/**
	 * internationalization
	 */
	public static Text rb;
	
	static Logger logger = Logger.getLogger("Grafiek3DGWT");
	
	public static String languageString = "nl";
	
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	/**
	 * GUI: drawing in dlp-center, panels north and east
	 */
	DockLayoutPanel dlp;
	LayoutPanel topPanel;
	LayoutPanel rightPanel;
	Grafiek3DComponent grafiek3DComponent;
	
	/**
	 * GUI-components for topPanel: choosing the type of Object3D and the corresponding example list
	 */
	ListBox figuurKeuzeBox;
	ListBox voorbeeldKeuzeBox;
	/**
	 * GUI-components for topPanel: label displaying the type of Object3D shown
	 */
	Label figuurKeuzeLabel;

	/**
	 * arrayLists with predefined examples
	 */
	ArrayList<GrafiekVoorbeeld> grafiekVoorbeelden;
	ArrayList<OppervlakVoorbeeld> oppervlakVoorbeelden;
	ArrayList<KrommeVoorbeeld> krommeVoorbeelden;
	
	/** 
	 * predefined graph
	 */
	String graphString = "$f@";
	/**
	 * predefined surface
	 */
	String surfaceXString = "$f@";
	String surfaceYString = "$f@";
	String surfaceZString = "$f@";
	String uMinString = "$f@";
	String uMaxString = "$f@";
	String uPointsString = "$f@";
	String vMinString = "$f@";
	String vMaxString = "$f@";
	String vPointsString = "$f@";
	/**
	 * predefined curve
	 */
	String curveXString = "$f@";
	String curveYString = "$f@";
	String curveZString = "$f@";
	String tMinString = "$f@";
	String tMaxString = "$f@";
	String tPointsString = "$f@";
	
	/**
	 * very small double
	 */
	final double NZERO = 1e-5d;
	
	/**
	 * width and height
	 */
	int breedte = 500;
	int hoogte = 450;
	/**
	 * layout constants
	 */
	int topHeight = 32;
	int rightWidth = 32;
	int leftOffset = 5;
	int topOffset = 10;
	int figuurKeuzeWidth = 170;
	int figuurKeuzeHeight = 22;
	int voorbeeldKeuzeWidth = 200;
	int buttonSize = 22;

	/**
	 * constants for type of Object3D 
	 */
    public static final int FUNCTION = 0;
    public static final int SURFACE = 1;
    public static final int CURVE = 2;
    /**
     * actual type of Object3D (redundant)
     */
	int figuurKeuze = FUNCTION;
	int functieType = 0;
	/**
	 * actual type of Object3D-example
	 */
	int voorbeeldKeuze = 0;
	
	/**
	 * launch data
	 */
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;

	Grafiek3DGWTClientBundle grafiek3DGWTClientBundle;
	Grafiek3DGWTCssResource grafiek3DGWTCss;
	
	ImageResource resetResource, zoomInResource, zoomUitResource, solidResource, wireFrameResource,
			      fijnerResource, groverResource, centraalResource, parallelResource,
			      assenResource, geenAssenResource;
	
	Image resetImage, zoomInImage, zoomUitImage, solidImage, wireFrameImage,
    	  fijnerImage, groverImage, centraalImage, parallelImage,
    	  assenImage, geenAssenImage;
	
	/**
	 * reset, zoom in, zoom out, finer, coarser
	 */
	PushButton resetButton, zoomInButton, zoomUitButton, fijnerButton, groverButton;
	/**
	 * central/parallel projection
	 */
	ToggleButton projectieButton;
	/**
	 * solid/wireframe
	 */
	PushButton solidButton, wireButton;
	/**
	 * axes/no axes
	 */
	PushButton axesButton, noAxesButton;
	
	/**
	 * translate along the positive, negative part of an axis
	 */
	PushButton transPlusButton, transMinButton;
	/**
	 * choice of axis to translate along (one only)
	 */
	ToggleButton xAsButton, yAsButton, zAsButton;
	
	/**
	 * launch state variables
	 */
	boolean zoomOptie = true;
	boolean translateOptie = true;
	boolean solidDraadKeuzeOptie = true;
	boolean finerKeuzeOptie = true;
	boolean asKeuzeOptie = true;
	boolean labelKeuzeOptie = true;
	boolean projectieKeuzeOptie = true;
	boolean kleurKeuzeOptie = true;
	boolean figuurIsDemo = false;
	boolean voorbeeldenEnabled = true;
	boolean functieTypeKeuze = true;
	
	/**
	 * constants for translation of axes
	 */
	public static int XTRANS = 0;
	public static int YTRANS = 1;
	public static int ZTRANS = 2;
	/**
	 * actual translation axis
	 */
	int transDirection = XTRANS;

	/** 
	 * flagg for being on a tablet: used for preventing Mouse Events, since
	 * Touch Events seem to generate Mouse Events, so everything is carried out twice 
	 */
	boolean touchStart = false;

	/**
	 * flagg for examples being shown (necessary for correct reset
	 */
	boolean voorbeeldenUsed = false;
	
	public void getImages() 
	{
		rb = GWT.create(Text.class);

		// trick
		if (!rb.grafiekLabel().equals("grafiek"))
			languageString = "en";
		
		grafiek3DGWTClientBundle = GWT.create(Grafiek3DGWTClientBundle.class);
		grafiek3DGWTCss = grafiek3DGWTClientBundle.getGrafiek3DGWTCSS();
		grafiek3DGWTCss.ensureInjected();

		resetResource = grafiek3DGWTClientBundle.resetResource();
		resetImage = new Image(resetResource);
		resetImage.addStyleName(grafiek3DGWTCss.pushimage());
		
		zoomInResource = grafiek3DGWTClientBundle.zoomInResource();
		zoomInImage = new Image(zoomInResource);
		zoomInImage.addStyleName(grafiek3DGWTCss.pushimage());		
		
		zoomUitResource = grafiek3DGWTClientBundle.zoomUitResource();
		zoomUitImage = new Image(zoomUitResource);
		zoomUitImage.addStyleName(grafiek3DGWTCss.pushimage());

		wireFrameResource = grafiek3DGWTClientBundle.wireFrameResource();
		wireFrameImage = new Image(wireFrameResource);
		wireFrameImage.addStyleName(grafiek3DGWTCss.upimage());
		
		solidResource = grafiek3DGWTClientBundle.solidResource();
		solidImage = new Image(solidResource);
		solidImage.addStyleName(grafiek3DGWTCss.pushimage());
		
		fijnerResource = grafiek3DGWTClientBundle.fijnerResource();
		fijnerImage = new Image(fijnerResource);
		fijnerImage.addStyleName(grafiek3DGWTCss.pushimage());
		
		groverResource = grafiek3DGWTClientBundle.groverResource();
		groverImage = new Image(groverResource);
		groverImage.addStyleName(grafiek3DGWTCss.pushimage());
		
		geenAssenResource = grafiek3DGWTClientBundle.geenAssenResource();
		geenAssenImage = new Image(geenAssenResource);
		geenAssenImage.addStyleName(grafiek3DGWTCss.upimage());
		
		assenResource = grafiek3DGWTClientBundle.assenResource();
		assenImage = new Image(assenResource);
		assenImage.addStyleName(grafiek3DGWTCss.pushimage());
		
		parallelResource = grafiek3DGWTClientBundle.parallelResource();
		parallelImage = new Image(parallelResource);
		parallelImage.addStyleName(grafiek3DGWTCss.upimage());
		
		centraalResource = grafiek3DGWTClientBundle.centraalResource();
		centraalImage = new Image(centraalResource);
		centraalImage.addStyleName(grafiek3DGWTCss.downimage());
		
		
	}
	
	public void onModuleLoad() 
	{
		
logger.info("grafiek3dgwt onModuleLoad");

		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(grafiek3DGWTCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(grafiek3DGWTCss.root());
		
		Stub.publish(this);
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());

	}

	/**
	 * create content of the right Panel with figure modifying options
	 */
	public void makeRight()
	{
		int currentX = leftOffset;
		int currentY = topOffset;

		// resetting
		resetButton = new PushButton(resetImage);

	   	if ((zoomOptie || translateOptie) && !figuurIsDemo)
    	{
			rightPanel.add(resetButton);
			rightPanel.setWidgetLeftWidth(resetButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(resetButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			resetButton.addMouseDownHandler(new PushMouseDownHandler());
	   		
    		currentY += buttonSize + topOffset;
    	}

	   	// zooming in and out
	   	zoomInButton = new PushButton(zoomInImage);
	   	zoomUitButton = new PushButton(zoomUitImage);
	   	
    	if (zoomOptie && !figuurIsDemo)
    	{
			rightPanel.add(zoomInButton);
			rightPanel.setWidgetLeftWidth(zoomInButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(zoomInButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			zoomInButton.addMouseDownHandler(new PushMouseDownHandler());
	   		
    		currentY += buttonSize + topOffset;
    		
			rightPanel.add(zoomUitButton);
			rightPanel.setWidgetLeftWidth(zoomUitButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(zoomUitButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			zoomUitButton.addMouseDownHandler(new PushMouseDownHandler());
	   		
    		currentY += buttonSize + topOffset;
    		
    	}

    	// translating the 3d-object along the axes
    	transPlusButton = new PushButton("+");
    	transPlusButton.addStyleName(grafiek3DGWTCss.pushbutton());
		xAsButton = new ToggleButton("x", "X");
		xAsButton.addStyleName(grafiek3DGWTCss.togglebutton());
		yAsButton = new ToggleButton("y", "Y");
		yAsButton.addStyleName(grafiek3DGWTCss.togglebutton());
		zAsButton = new ToggleButton("z", "Z");
		zAsButton.addStyleName(grafiek3DGWTCss.togglebutton());
   		transMinButton = new PushButton("-");
   		transMinButton.addStyleName(grafiek3DGWTCss.pushbutton1());
    	
    	if (translateOptie && !figuurIsDemo)
    	{	
			rightPanel.add(transPlusButton);
			rightPanel.setWidgetLeftWidth(transPlusButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(transPlusButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			transPlusButton.addMouseDownHandler(new PushMouseDownHandler());
	   		
    		currentY += buttonSize + topOffset;
    		
			rightPanel.add(xAsButton);
			rightPanel.setWidgetLeftWidth(xAsButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(xAsButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
			xAsButton.addMouseDownHandler(new ToggleMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
			rightPanel.add(yAsButton);
			rightPanel.setWidgetLeftWidth(yAsButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(yAsButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
			yAsButton.addMouseDownHandler(new ToggleMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
			rightPanel.add(zAsButton);
			rightPanel.setWidgetLeftWidth(zAsButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(zAsButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
			zAsButton.addMouseDownHandler(new ToggleMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
			rightPanel.add(transMinButton);
			rightPanel.setWidgetLeftWidth(transMinButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(transMinButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			transMinButton.addMouseDownHandler(new PushMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
    	}

    	// showing the 3d-object as a solid or a wireframe
    	solidButton = new PushButton(solidImage);
    	wireButton = new PushButton(wireFrameImage);
    	
    	if (solidDraadKeuzeOptie && !figuurIsDemo && functieType != CURVE)
    	{	
			rightPanel.add(solidButton);
			rightPanel.setWidgetLeftWidth(solidButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(solidButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetVisible(solidButton, false);
			
			rightPanel.add(wireButton);
			rightPanel.setWidgetLeftWidth(wireButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(wireButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
    		currentY += buttonSize + topOffset;
    		
    		solidButton.addMouseDownHandler(new PushMouseDownHandler());
    		wireButton.addMouseDownHandler(new PushMouseDownHandler());

    	}	

    	/**
    	 * refining the calciulating grid, only for 3d-graphs
    	 */
    	fijnerButton = new PushButton(fijnerImage);
    	groverButton = new PushButton(groverImage);
    	
    	if (finerKeuzeOptie  && !figuurIsDemo && functieType == FUNCTION)	
    	{	
			rightPanel.add(fijnerButton);
			rightPanel.setWidgetLeftWidth(fijnerButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(fijnerButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);

			fijnerButton.addMouseDownHandler(new PushMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
			rightPanel.add(groverButton);
			rightPanel.setWidgetLeftWidth(groverButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(groverButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
			groverButton.addMouseDownHandler(new PushMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
    	}
    	
    	// adding/omitting axes
    	axesButton = new PushButton(assenImage);
    	noAxesButton = new PushButton(geenAssenImage);
    	
    	if (asKeuzeOptie  && !figuurIsDemo)
    	{
			rightPanel.add(axesButton);
			rightPanel.setWidgetLeftWidth(axesButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(axesButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetVisible(axesButton, false);
			
			rightPanel.add(noAxesButton);
			rightPanel.setWidgetLeftWidth(noAxesButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(noAxesButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
	   		
    		currentY += buttonSize + topOffset;
    		
    		axesButton.addMouseDownHandler(new PushMouseDownHandler());
    		noAxesButton.addMouseDownHandler(new PushMouseDownHandler());
    		
    	}

    	// choosing the projection (central/parallel)
		projectieButton = new ToggleButton(parallelImage, centraalImage);
		
    	if (projectieKeuzeOptie && !figuurIsDemo)
    	{
			rightPanel.add(projectieButton);
			rightPanel.setWidgetLeftWidth(projectieButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(projectieButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
    		currentY += buttonSize + topOffset;
    		
    		projectieButton.addMouseDownHandler(new ToggleMouseDownHandler());
    		
    	}
 		
	}
	
	/**
	 * create the content of the top Panel: in case of examples, checkboxes for
	 * choice of 3d-object (graph, surface, curve) and corresponding choice of 
	 * example, or just a label indicating the kind of 3d-object (graph, surface, curve)
	 * shown 
	 */
	public void makeTop()
	{
		int currentX = leftOffset;
		int currentY = topOffset;
		
		figuurKeuzeBox = new ListBox();
		figuurKeuzeBox.addItem(rb.grafiekLabel()); //"grafiek");
		figuurKeuzeBox.addItem(rb.oppervlakLabel()); //"oppervlak");
		figuurKeuzeBox.addItem(rb.krommeLabel()); //"kromme");
		
		figuurKeuzeBox.setVisibleItemCount(1);
		figuurKeuzeBox.setSelectedIndex(functieType);
		
		String figuurKeuzeTekst = "";
		if (functieType == FUNCTION)
			figuurKeuzeTekst = rb.grafiekLabel(); //"grafiek";
		else if (functieType == SURFACE)
			figuurKeuzeTekst = rb.oppervlakLabel(); //"oppervlak";
		else if (functieType == CURVE)
			figuurKeuzeTekst = rb.krommeLabel(); //"kromme";
		figuurKeuzeLabel = new Label(figuurKeuzeTekst);
		figuurKeuzeLabel.addStyleName(grafiek3DGWTCss.label());
		
		if (functieTypeKeuze && voorbeeldenEnabled)
		{	
			topPanel.add(figuurKeuzeBox);
			topPanel.setWidgetLeftWidth(figuurKeuzeBox, currentX, Style.Unit.PX, figuurKeuzeWidth, Style.Unit.PX);
			topPanel.setWidgetTopHeight(figuurKeuzeBox, currentY, Style.Unit.PX, figuurKeuzeHeight, Style.Unit.PX);
			figuurKeuzeBox.addChangeHandler(new ListChangeHandler());
		}

		if (!functieTypeKeuze || !voorbeeldenEnabled)
		{	
			topPanel.add(figuurKeuzeLabel);
			topPanel.setWidgetLeftWidth(figuurKeuzeLabel, currentX, Style.Unit.PX, figuurKeuzeWidth, Style.Unit.PX);
			topPanel.setWidgetTopHeight(figuurKeuzeLabel, currentY, Style.Unit.PX, figuurKeuzeHeight, Style.Unit.PX);

		}

		currentX += figuurKeuzeWidth + leftOffset;
		
		maakGrafiekVoorbeelden();
		maakOppervlakVoorbeelden();
		maakKrommeVoorbeelden();

		voorbeeldKeuzeBox = new ListBox();
		voorbeeldKeuzeBox.addItem("voorbeelden");
		for (int gCnt = 0; gCnt < grafiekVoorbeelden.size(); gCnt++)
		{	String name = "";
			if (languageString.equals("nl"))
				name = ((GrafiekVoorbeeld) grafiekVoorbeelden.get(gCnt)).nlNaam;
			else 
				name = ((GrafiekVoorbeeld) grafiekVoorbeelden.get(gCnt)).enNaam;
			voorbeeldKeuzeBox.addItem(name);
		}
		
		voorbeeldKeuzeBox.setVisibleItemCount(1);

		if (voorbeeldenEnabled)
		{	
			toonVoorbeeldenBox(functieType);
			topPanel.add(voorbeeldKeuzeBox);
			topPanel.setWidgetLeftWidth(voorbeeldKeuzeBox, currentX, Style.Unit.PX, voorbeeldKeuzeWidth, Style.Unit.PX);
			topPanel.setWidgetTopHeight(voorbeeldKeuzeBox, currentY, Style.Unit.PX, figuurKeuzeHeight, Style.Unit.PX);
			voorbeeldKeuzeBox.addChangeHandler(new ListChangeHandler());
		}	
		
		currentX += voorbeeldKeuzeWidth + leftOffset;
	}
	
	
	public Grafiek3DGWT()
	{
		//this(null, null, null);
	}
	
	public Grafiek3DGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
	{
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
		dlp.addStyleName(grafiek3DGWTCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		init(breedte, hoogte, launchState, randomVarWaarden);

	}
	
	public void init(int width, int height, Map<String,Object> map, Map<String,Number> values) 
	{
		
logger.info("Grafiek3DGWT init");
		this.breedte = width;
		this.hoogte = height;
		this.launchState = map;
				
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");


		ObjectMap launchState = JSONUtilities.wrapMap(map);

		// parametriation
		if (launchState.containsKey("zoomOptie"))
			zoomOptie = launchState.getBoolean("zoomOptie");
		if (launchState.containsKey("translateOptie"))
			translateOptie = launchState.getBoolean("translateOptie");
		if (launchState.containsKey("solidDraadKeuzeOptie"))
			solidDraadKeuzeOptie = launchState.getBoolean("solidDraadKeuzeOptie");
		if (launchState.containsKey("finerKeuzeOptie"))
			finerKeuzeOptie = launchState.getBoolean("finerKeuzeOptie");
		if (launchState.containsKey("asKeuzeOptie"))
			asKeuzeOptie = launchState.getBoolean("asKeuzeOptie");
		if (launchState.containsKey("labelKeuzeOptie"))
			labelKeuzeOptie = launchState.getBoolean("labelKeuzeOptie");
		if (launchState.containsKey("projectieKeuzeOptie"))
			projectieKeuzeOptie = launchState.getBoolean("projectieKeuzeOptie");
		if (launchState.containsKey("kleurKeuzeOptie"))
			kleurKeuzeOptie = launchState.getBoolean("kleurKeuzeOptie");

		if (launchState.containsKey("voorbeeldenEnabled"))
			voorbeeldenEnabled = launchState.getBoolean("voorbeeldenEnabled");
		if (launchState.containsKey("functieTypeKeuze"))
			functieTypeKeuze = launchState.getBoolean("functieTypeKeuze");
		
		if (launchState.containsKey("figuurIsDemo"))
			figuurIsDemo = launchState.getBoolean("figuurIsDemo");
			
		if (launchState.containsKey("functieType")) 
		{	functieType = launchState.getInt("functieType");
		}
		figuurKeuze = functieType;

		// predefined graph
		if (launchState.containsKey("graphString")) 
			graphString = launchState.getString("graphString");

		// predefined surface
		if (launchState.containsKey("surfaceXString")) 
			surfaceXString = launchState.getString("surfaceXString");
		if (launchState.containsKey("surfaceYString")) 
			surfaceYString = launchState.getString("surfaceYString");
		if (launchState.containsKey("surfaceZString")) 
			surfaceZString = launchState.getString("surfaceZString");
		if (launchState.containsKey("uMinString")) 
			uMinString = launchState.getString("uMinString");
		if (launchState.containsKey("uMaxString")) 
			uMaxString = launchState.getString("uMaxString");
		if (launchState.containsKey("uPointsString")) 
			uPointsString = launchState.getString("uPointsString");
		if (launchState.containsKey("vMinString")) 
			vMinString = launchState.getString("vMinString");
		if (launchState.containsKey("vMaxString")) 
			vMaxString = launchState.getString("vMaxString");
		if (launchState.containsKey("vPointsString")) 
			vPointsString = launchState.getString("vPointsString");
		
		if (launchState.containsKey("curveXString")) 
			curveXString = launchState.getString("curveXString");
		if (launchState.containsKey("curveYString")) 
			curveYString = launchState.getString("curveYString");
		if (launchState.containsKey("curveZString")) 
			curveZString = launchState.getString("curveZString");
		if (launchState.containsKey("tMinString")) 
			tMinString = launchState.getString("tMinString");
		if (launchState.containsKey("tMaxString")) 
			tMaxString = launchState.getString("tMaxString");
		if (launchState.containsKey("tPointsString")) 
			tPointsString = launchState.getString("tPointsString");

		if (figuurIsDemo)
		{	rightWidth = 0;
			topHeight = 0;
		}
		
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		

		grafiek3DComponent = new Grafiek3DComponent(this, breedte - rightWidth, hoogte - topHeight);

		if (grafiek3DComponent.grafiek3DCanvas == null) 
		{
	      RootPanel.get(holderId).add(new Label(upgradeMessage));
	      return;
	    }
		
		rightPanel = new LayoutPanel();
		rightPanel.addStyleName(grafiek3DGWTCss.right());

		if (!figuurIsDemo)
			dlp.addEast(rightPanel, rightWidth);

		topPanel = new LayoutPanel();
		topPanel.addStyleName(grafiek3DGWTCss.bottom());

		if (!figuurIsDemo)
			dlp.addNorth(topPanel, topHeight);
		
		dlp.add(grafiek3DComponent.grafiek3DCanvas);		

		if (!figuurIsDemo)
			makeTop();

		makeRight();
		
		grafiek3DComponent.setState(map, false);
		
		grafiek3DComponent.resetState = (HashMap) map;
		
		voorbeeldenUsed = false;
		
		processLaunchInput();
logger.info("Grafiek3DGWT init after processLaunch");		
		
		
		dlp.forceLayout();
		
		grafiek3DComponent.panel3D.repaint();

	}
	
	/**
	 * create the 3d-graph examples
	 */
	public void maakGrafiekVoorbeelden()
	{
		grafiekVoorbeelden = new ArrayList<GrafiekVoorbeeld>();		
		
		GrafiekVoorbeeld grafiekVoorbeeld1 = new Paraboloide();
		grafiekVoorbeelden.add(grafiekVoorbeeld1);
		GrafiekVoorbeeld grafiekVoorbeeld2 = new Zadel();
		grafiekVoorbeelden.add(grafiekVoorbeeld2);
		GrafiekVoorbeeld grafiekVoorbeeld3 = new ReciprokeTrumpet();
		grafiekVoorbeelden.add(grafiekVoorbeeld3);
		GrafiekVoorbeeld grafiekVoorbeeld4 = new LnTrumpet();
		grafiekVoorbeelden.add(grafiekVoorbeeld4);
		GrafiekVoorbeeld grafiekVoorbeeld5 = new SineHat();
		grafiekVoorbeelden.add(grafiekVoorbeeld5);
		GrafiekVoorbeeld grafiekVoorbeeld6 = new TangensChaos();
		grafiekVoorbeelden.add(grafiekVoorbeeld6);
		
		
	}

	/**
	 * create the 3d-surface examples
	 */
	public void maakOppervlakVoorbeelden()
	{
		oppervlakVoorbeelden = new ArrayList<OppervlakVoorbeeld>();
		
		OppervlakVoorbeeld oppervlakVoorbeeld1 = new Cylinder();
		oppervlakVoorbeelden.add(oppervlakVoorbeeld1);
		OppervlakVoorbeeld oppervlakVoorbeeld2 = new Cones();
		oppervlakVoorbeelden.add(oppervlakVoorbeeld2);
		OppervlakVoorbeeld oppervlakVoorbeeld3 = new Helicoide();
		oppervlakVoorbeelden.add(oppervlakVoorbeeld3);
		OppervlakVoorbeeld oppervlakVoorbeeld4 = new Sphere();
		oppervlakVoorbeelden.add(oppervlakVoorbeeld4);
		OppervlakVoorbeeld oppervlakVoorbeeld5 = new Ellipsoid();
		oppervlakVoorbeelden.add(oppervlakVoorbeeld5);
		OppervlakVoorbeeld oppervlakVoorbeeld6 = new Torus();
		oppervlakVoorbeelden.add(oppervlakVoorbeeld6);
		OppervlakVoorbeeld oppervlakVoorbeeld7 = new Trumpet();
		oppervlakVoorbeelden.add(oppervlakVoorbeeld7);
		OppervlakVoorbeeld oppervlakVoorbeeld8 = new EightSurface();
		oppervlakVoorbeelden.add(oppervlakVoorbeeld8);
		OppervlakVoorbeeld oppervlakVoorbeeld9 = new Shell();
		oppervlakVoorbeelden.add(oppervlakVoorbeeld9);
		OppervlakVoorbeeld oppervlakVoorbeeld10 = new KleinBagel();
		oppervlakVoorbeelden.add(oppervlakVoorbeeld10);
		
	}

	/**
	 * create the 3d-curve examples
	 */
	public void maakKrommeVoorbeelden()
	{
		krommeVoorbeelden = new ArrayList<KrommeVoorbeeld>();		
		
		KrommeVoorbeeld krommeVoorbeeld1 = new Helix();
		krommeVoorbeelden.add(krommeVoorbeeld1);
		KrommeVoorbeeld krommeVoorbeeld2 = new ConeHelix();
		krommeVoorbeelden.add(krommeVoorbeeld2);
		KrommeVoorbeeld krommeVoorbeeld3 = new TorusHelix();
		krommeVoorbeelden.add(krommeVoorbeeld3);
		KrommeVoorbeeld krommeVoorbeeld4 = new FlowerLeaves();
		krommeVoorbeelden.add(krommeVoorbeeld4);
		
	}

	/**
	 * show the example list corresponding to the type of 3d-object;
	 * take care of internationalisation
	 * @param type 3d-graph/3d-surface/3d-curve
	 */
	public void toonVoorbeeldenBox(int type)
	{
		voorbeeldKeuzeBox.clear();
		
		if (type == FUNCTION)
		{
			voorbeeldKeuzeBox.addItem("voorbeelden");
			for (int gCnt = 0; gCnt < grafiekVoorbeelden.size(); gCnt++)
			{	String name = "";
				if (languageString.equals("nl"))
					name = ((GrafiekVoorbeeld) grafiekVoorbeelden.get(gCnt)).nlNaam;
				else
					name = ((GrafiekVoorbeeld) grafiekVoorbeelden.get(gCnt)).enNaam;
				voorbeeldKeuzeBox.addItem(name);
			}
			
		}
		else if (type == SURFACE)
		{
			voorbeeldKeuzeBox.addItem("voorbeelden");
			for (int oCnt = 0; oCnt < oppervlakVoorbeelden.size(); oCnt++)
			{	String name = "";
				if (languageString.equals("nl"))
					name = ((OppervlakVoorbeeld) oppervlakVoorbeelden.get(oCnt)).nlNaam;
				else
					name = ((OppervlakVoorbeeld) oppervlakVoorbeelden.get(oCnt)).enNaam;
				voorbeeldKeuzeBox.addItem(name);

			}
			
		}
		else if (type == CURVE)
		{
			voorbeeldKeuzeBox.addItem("voorbeelden");
			for (int kCnt = 0; kCnt < krommeVoorbeelden.size(); kCnt++)
			{	String name = "";
				if (languageString.equals("nl"))
					name = ((KrommeVoorbeeld) krommeVoorbeelden.get(kCnt)).nlNaam;
				else
					name = ((KrommeVoorbeeld) krommeVoorbeelden.get(kCnt)).enNaam;
				voorbeeldKeuzeBox.addItem(name);
			}

		}
		
		voorbeeldKeuze = 0;
	}

	/**
	 * create and show the example of index i belonging to the current value
	 * of figuurKeuze
	 * @param voorbeeldKeuze the index of the example
	 */
	public void processInput(int voorbeeldKeuze)
	{
		voorbeeldenUsed = true;
		
		// knoppen terugzetten
		if (solidDraadKeuzeOptie)	
		{	if (solidButton.getParent() == rightPanel)
				rightPanel.setWidgetVisible(solidButton,false);
			if (wireButton.getParent() == rightPanel)
				rightPanel.setWidgetVisible(wireButton,true);
		}	
		
		projectieButton.setDown(false);
		
		if (asKeuzeOptie)
		{	if (axesButton.getParent() == rightPanel)
				rightPanel.setWidgetVisible(axesButton,false);
			if (noAxesButton.getParent() == rightPanel)
				rightPanel.setWidgetVisible(noAxesButton,true);
		}	
		
		// zet alles op null
		grafiek3DComponent.zetGrafiek3D(null);
		grafiek3DComponent.zetSurface3D(null, null, null, 0, 0, 0, 0, 0, 0);
		grafiek3DComponent.zetCurve3D(null, null, null, 0, 0, 0);
		
		grafiek3DComponent.panel3D.model = null;
		grafiek3DComponent.panel3D.repaint();
		
		if (figuurKeuze == FUNCTION)
		{	
			GrafiekVoorbeeld gv = (GrafiekVoorbeeld) grafiekVoorbeelden.get(voorbeeldKeuze);
			Expressie exp = FormuleParser.geefExpressie(gv.graphString);
			
			grafiek3DComponent.objectType = FUNCTION;
			grafiek3DComponent.zetGrafiekVoorbeeld(gv);
			grafiek3DComponent.zetGrafiek3D(exp);
				
		}
		else if (figuurKeuze == SURFACE)
		{
			OppervlakVoorbeeld ov = (OppervlakVoorbeeld) oppervlakVoorbeelden.get(voorbeeldKeuze);
			
			Expressie expX = FormuleParser.geefExpressie(ov.surfaceXString);
			Expressie expY = FormuleParser.geefExpressie(ov.surfaceYString);
			Expressie expZ = FormuleParser.geefExpressie(ov.surfaceZString);

			Expressie expUMin = FormuleParser.geefExpressie(ov.uMinString);
			double uMin = expUMin.geefWaarde();
			Expressie expUMax = FormuleParser.geefExpressie(ov.uMaxString);
			double uMax = expUMax.geefWaarde();
			Expressie expUPointsDouble = FormuleParser.geefExpressie(ov.uPointsString);
			int uPoints = (int) Math.round(expUPointsDouble.geefWaarde());
			
			Expressie expVMin = FormuleParser.geefExpressie(ov.vMinString);
			double vMin = expVMin.geefWaarde();
			Expressie expVMax = FormuleParser.geefExpressie(ov.vMaxString);
			double vMax = expVMax.geefWaarde();
			Expressie expVPointsDouble = FormuleParser.geefExpressie(ov.vPointsString);
			int vPoints = (int) Math.round(expVPointsDouble.geefWaarde());
			
			grafiek3DComponent.objectType = SURFACE;
			grafiek3DComponent.zetOppervlakVoorbeeld(ov);
			grafiek3DComponent.zetSurface3D(expX, expY, expZ, uMin, uMax, uPoints, vMin, vMax, vPoints);
			
		}
		else if (figuurKeuze == CURVE)
		{	
			KrommeVoorbeeld kv = (KrommeVoorbeeld) krommeVoorbeelden.get(voorbeeldKeuze);
			Expressie expX = FormuleParser.geefExpressie(kv.curveXString);
			Expressie expY = FormuleParser.geefExpressie(kv.curveYString);
			Expressie expZ = FormuleParser.geefExpressie(kv.curveZString);
			
			Expressie expTMin = FormuleParser.geefExpressie(kv.tMinString);
			double tMin = expTMin.geefWaarde();
			Expressie expTMax = FormuleParser.geefExpressie(kv.tMaxString);
			double tMax = expTMax.geefWaarde();
			Expressie expTPointsDouble = FormuleParser.geefExpressie(kv.tPointsString);
			int tPoints = (int) Math.round(expTPointsDouble.geefWaarde());
			
			grafiek3DComponent.objectType = CURVE;
			grafiek3DComponent.zetKrommeVoorbeeld(kv);
			grafiek3DComponent.zetCurve3D(expX, expY, expZ, tMin, tMax, tPoints);
			
		}
		
	}

	/**
	 * create and show the 3d-object contained in the launch data
	 */
	public void processLaunchInput()
	{
		
		if (functieType == FUNCTION)
		{	
			Expressie exp = FormuleParser.geefExpressie(graphString);
			
			grafiek3DComponent.objectType = FUNCTION;
			grafiek3DComponent.zetGrafiek3D(exp);
			
			
		}
		else if (functieType == SURFACE)
		{
			Expressie expX = FormuleParser.geefExpressie(surfaceXString);
			Expressie expY = FormuleParser.geefExpressie(surfaceYString);
			Expressie expZ = FormuleParser.geefExpressie(surfaceZString);

			Expressie expUMin = FormuleParser.geefExpressie(uMinString);
			double uMin = expUMin.geefWaarde();
			Expressie expUMax = FormuleParser.geefExpressie(uMaxString);
			double uMax = expUMax.geefWaarde();
			Expressie expUPointsDouble = FormuleParser.geefExpressie(uPointsString);
			int uPoints = (int) Math.round(expUPointsDouble.geefWaarde());
			
			Expressie expVMin = FormuleParser.geefExpressie(vMinString);
			double vMin = expVMin.geefWaarde();
			Expressie expVMax = FormuleParser.geefExpressie(vMaxString);
			double vMax = expVMax.geefWaarde();
			Expressie expVPointsDouble = FormuleParser.geefExpressie(vPointsString);
			int vPoints = (int) Math.round(expVPointsDouble.geefWaarde());
			
			grafiek3DComponent.objectType = SURFACE;
			grafiek3DComponent.zetSurface3D(expX, expY, expZ, uMin, uMax, uPoints, vMin, vMax, vPoints);
			
			
		}
		else if (functieType == CURVE)
		{	
			
			Expressie expX = FormuleParser.geefExpressie(curveXString);
			Expressie expY = FormuleParser.geefExpressie(curveYString);
			Expressie expZ = FormuleParser.geefExpressie(curveZString);
			
			Expressie expTMin = FormuleParser.geefExpressie(tMinString);
			double tMin = expTMin.geefWaarde();
			Expressie expTMax = FormuleParser.geefExpressie(tMaxString);
			double tMax = expTMax.geefWaarde();
			Expressie expTPointsDouble = FormuleParser.geefExpressie(tPointsString);
			int tPoints = (int) Math.round(expTPointsDouble.geefWaarde());
			
			grafiek3DComponent.objectType = CURVE;
			grafiek3DComponent.zetCurve3D(expX, expY, expZ, tMin, tMax, tPoints);
			
		}
		
	}

	/**
	 * only one of xAsButton, yAsButton, zAsButton can be down
	 * @param tb Togglebutton set to down
	 */
   	void buttonsUp(ToggleButton tb)
   	{
   		if (!xAsButton.equals(tb))
   			xAsButton.setDown(false);
   		if (!yAsButton.equals(tb))
   			yAsButton.setDown(false);
 		if (!zAsButton.equals(tb))
   			zAsButton.setDown(false);
   	}
	
	/**
	 * inner class handling Mouse Down Events on the modify ToggleButtons in the right Panel
	 * @author huub
	 */
	class ToggleMouseDownHandler implements MouseDownHandler
	{
		public void onMouseDown(MouseDownEvent e)
		{
			//e.preventDefault();
			// deze zorgt dat je niet scollt in de DWOPlayer
			e.stopPropagation();
			
			if (e.getSource() == xAsButton)
			{
				if (!xAsButton.isDown())
				{	buttonsUp(xAsButton);			
					transDirection = XTRANS;
				}
			}
			else if (e.getSource() == yAsButton)
			{
				if (!yAsButton.isDown())
				{	buttonsUp(yAsButton);			
					transDirection = YTRANS;
				}
			}
			else if (e.getSource() == zAsButton)
			{
				if (!zAsButton.isDown())
				{	buttonsUp(zAsButton);			
					transDirection = ZTRANS;
				}
			}
			else if (e.getSource() == projectieButton)
			{
				if (!projectieButton.isDown())
				{
					grafiek3DComponent.zetCentraleProjectie(false, figuurKeuze);
				}
				else
				{
					grafiek3DComponent.zetCentraleProjectie(true, figuurKeuze);
				}
				
				grafiek3DComponent.panel3D.repaint();
			}

		}
	}

	/**
	 * inner class handling Mouse Down Events on the modify PushButtons in the right Panel
	 * @author huub
	 */
	class PushMouseDownHandler implements MouseDownHandler
	{
		public void onMouseDown(MouseDownEvent e)
		{
			if (touchStart)
				return;
			
			// deze zorgt dat je niet scollt in de DWOPlayer
			e.stopPropagation();

			if (e.getSource() == resetButton)
			{
				
				figuurKeuzeBox.setSelectedIndex(functieType);
				figuurKeuze = functieType;
				toonVoorbeeldenBox(functieType);
				voorbeeldKeuzeBox.setSelectedIndex(0);
				
				if (solidDraadKeuzeOptie)	
				{	if (solidButton.getParent() == rightPanel)
						rightPanel.setWidgetVisible(solidButton,false);
					if (wireButton.getParent() == rightPanel)
						rightPanel.setWidgetVisible(wireButton,true);
				}	
				
				projectieButton.setDown(false);
				
				if (asKeuzeOptie)
				{	if (axesButton.getParent() == rightPanel)
						rightPanel.setWidgetVisible(axesButton,false);
					if (noAxesButton.getParent() == rightPanel)
						rightPanel.setWidgetVisible(noAxesButton,true);
				}	


				grafiek3DComponent.zetGrafiek3D(null);
				grafiek3DComponent.zetSurface3D(null, null, null, 0, 0, 0, 0, 0, 0);
				grafiek3DComponent.zetCurve3D(null, null, null, 0, 0, 0);
				
				grafiek3DComponent.panel3D.model = null;

				grafiek3DComponent.setState(launchState, true);
				processLaunchInput();
				
			}
			else if (e.getSource() == zoomInButton)
			{
				grafiek3DComponent.zoomIn(true, figuurKeuze);
			}
			else if (e.getSource() == zoomUitButton)
			{
				grafiek3DComponent.zoomUit(true, figuurKeuze);
			}
			else if (e.getSource() == transPlusButton)
			{
				if (transDirection == XTRANS)
				{
					grafiek3DComponent.transPlusX(true, figuurKeuze);
				}
				else if (transDirection == YTRANS)
				{
					grafiek3DComponent.transPlusY(true, figuurKeuze);
				}
				else if (transDirection == ZTRANS)
				{
					grafiek3DComponent.transPlusZ(true, figuurKeuze);
				}

			}
			else if (e.getSource() == transMinButton)
			{
				if (transDirection == XTRANS)
				{
					grafiek3DComponent.transMinX(true, figuurKeuze);
				}
				else if (transDirection == YTRANS)
				{
					grafiek3DComponent.transMinY(true, figuurKeuze);
				}
				else if (transDirection == ZTRANS)
				{
					grafiek3DComponent.transMinZ(true, figuurKeuze);
				}
				
			}
			else if (e.getSource() == fijnerButton)
			{
				grafiek3DComponent.zetFijner(true, figuurKeuze);
			}
			else if (e.getSource() == groverButton)
			{
				grafiek3DComponent.zetGrover(true, figuurKeuze);
			}
			
			else if (e.getSource() == solidButton)
			{
				grafiek3DComponent.zetDraadFiguur(false, figuurKeuze);
				rightPanel.setWidgetVisible(solidButton,false);
				rightPanel.setWidgetVisible(wireButton,true);
			}
			else if (e.getSource() == wireButton)
			{
				grafiek3DComponent.zetDraadFiguur(true, figuurKeuze);
				rightPanel.setWidgetVisible(solidButton,true);
				rightPanel.setWidgetVisible(wireButton,false);
			}
			
			else if (e.getSource() == axesButton)
			{
				grafiek3DComponent.zetxyzAs(true, figuurKeuze);
				rightPanel.setWidgetVisible(axesButton,false);
				rightPanel.setWidgetVisible(noAxesButton,true);
			}
			else if (e.getSource() == noAxesButton)
			{
				grafiek3DComponent.zetGeenAssen(true, figuurKeuze);
				rightPanel.setWidgetVisible(axesButton,true);
				rightPanel.setWidgetVisible(noAxesButton,false);
			}
			
		}
	}

	/**
	 * inner class handling Change Events on the figuurKeuzeBox or
	 * the voorbeeldKeuzeBox 
	 * @author huub
	 */
	class ListChangeHandler implements ChangeHandler
	{
		public void onChange(ChangeEvent e)
		{
			//e.preventDefault();
			
			// deze zorgt dat je niet scollt in de DWOPlayer
			e.stopPropagation();
			
			if (e.getSource() == figuurKeuzeBox)
			{
				int index = figuurKeuzeBox.getSelectedIndex();
				
				if ((index == 0) && (figuurKeuze != FUNCTION))
				{
					figuurKeuze = FUNCTION;
					
					toonVoorbeeldenBox(FUNCTION);
					
				}
				else if ((index == 1) && (figuurKeuze != SURFACE))
				{
					figuurKeuze = SURFACE;

					toonVoorbeeldenBox(SURFACE);
					
				}
				else if ((index == 2) && (figuurKeuze != CURVE))
				{
					figuurKeuze = CURVE;

					toonVoorbeeldenBox(CURVE);					
					
				}
				
			}
			else if (e.getSource() == voorbeeldKeuzeBox)
			{
				int index = voorbeeldKeuzeBox.getSelectedIndex();
				
				voorbeeldKeuze = index - 1;
				 
				processInput(voorbeeldKeuze);
			}
		}
	}

	/**
	 * inner class handling Touch Start Events on the modify ToggleButtons in the right Panel
	 * @author huub
	 */
	class ToggleTouchStartHandler implements TouchStartHandler
	{
		public void onTouchStart(TouchStartEvent e)
		{

			//e.preventDefault();
			// deze zorgt dat je niet scollt in de DWOPlayer
			e.stopPropagation();

			if (e.getSource() == xAsButton)
			{
				if (!xAsButton.isDown())
				{	buttonsUp(xAsButton);			
					transDirection = XTRANS;
				}
			}
			else if (e.getSource() == yAsButton)
			{
				if (!yAsButton.isDown())
				{	buttonsUp(yAsButton);			
					transDirection = YTRANS;
				}
			}
			else if (e.getSource() == zAsButton)
			{
				if (!zAsButton.isDown())
				{	buttonsUp(zAsButton);			
					transDirection = ZTRANS;
				}
			}
			else if (e.getSource() == projectieButton)
			{
				if (!projectieButton.isDown())
				{
					grafiek3DComponent.zetCentraleProjectie(false, figuurKeuze);
				}
				else
				{
					grafiek3DComponent.zetCentraleProjectie(true, figuurKeuze);
				}
				
				grafiek3DComponent.panel3D.repaint();
			}
			
		}
	}

	/**
	 * inner class handling Touch Start Events on the modify PushButtons in the right Panel
	 * @author huub
	 */
	class PushTouchStartHandler implements TouchStartHandler
	{
		public void onTouchStart(TouchStartEvent e)
		{
			touchStart = true;
			
			// deze zorgt dat je niet scrollt in de DWOPlayer
			e.stopPropagation();

			if (e.getSource() == resetButton)
			{
				figuurKeuzeBox.setSelectedIndex(functieType);
				figuurKeuze = functieType;
				toonVoorbeeldenBox(functieType);
				voorbeeldKeuzeBox.setSelectedIndex(0);

				if (solidDraadKeuzeOptie)	
				{	if (solidButton.getParent() == rightPanel)
						rightPanel.setWidgetVisible(solidButton,false);
					if (wireButton.getParent() == rightPanel)
						rightPanel.setWidgetVisible(wireButton,true);
				}	

				projectieButton.setDown(false);

				if (asKeuzeOptie)
				{	if (axesButton.getParent() == rightPanel)
						rightPanel.setWidgetVisible(axesButton,false);
					if (noAxesButton.getParent() == rightPanel)
						rightPanel.setWidgetVisible(noAxesButton,true);
				}	

				grafiek3DComponent.zetGrafiek3D(null);
				grafiek3DComponent.zetSurface3D(null, null, null, 0, 0, 0, 0, 0, 0);
				grafiek3DComponent.zetCurve3D(null, null, null, 0, 0, 0);
				
				grafiek3DComponent.panel3D.model = null;

				grafiek3DComponent.setState(launchState, true);
				processLaunchInput();
				
			}
			else if (e.getSource() == zoomInButton)
			{
				grafiek3DComponent.zoomIn(true, figuurKeuze);
			}
			else if (e.getSource() == zoomUitButton)
			{
				grafiek3DComponent.zoomUit(true, figuurKeuze);
			}
			else if (e.getSource() == transPlusButton)
			{
				if (transDirection == XTRANS)
				{
					grafiek3DComponent.transPlusX(true, figuurKeuze);
				}
				else if (transDirection == YTRANS)
				{
					grafiek3DComponent.transPlusY(true, figuurKeuze);
				}
				else if (transDirection == ZTRANS)
				{
					grafiek3DComponent.transPlusZ(true, figuurKeuze);
				}

			}
			else if (e.getSource() == transMinButton)
			{
				if (transDirection == XTRANS)
				{
					grafiek3DComponent.transMinX(true, figuurKeuze);
				}
				else if (transDirection == YTRANS)
				{
					grafiek3DComponent.transMinY(true, figuurKeuze);
				}
				else if (transDirection == ZTRANS)
				{
					grafiek3DComponent.transMinZ(true, figuurKeuze);
				}
				
			}
			else if (e.getSource() == fijnerButton)
			{
				grafiek3DComponent.zetFijner(true, figuurKeuze);
			}
			else if (e.getSource() == groverButton)
			{
				grafiek3DComponent.zetGrover(true, figuurKeuze);
			}
			
			else if (e.getSource() == solidButton)
			{
				grafiek3DComponent.zetDraadFiguur(false, figuurKeuze);
				rightPanel.setWidgetVisible(solidButton,false);
				rightPanel.setWidgetVisible(wireButton,true);
			}
			else if (e.getSource() == wireButton)
			{
				grafiek3DComponent.zetDraadFiguur(true, figuurKeuze);
				rightPanel.setWidgetVisible(solidButton,true);
				rightPanel.setWidgetVisible(wireButton,false);
			}
			
			else if (e.getSource() == axesButton)
			{
				grafiek3DComponent.zetxyzAs(true, figuurKeuze);
				rightPanel.setWidgetVisible(axesButton,false);
				rightPanel.setWidgetVisible(noAxesButton,true);
			}
			else if (e.getSource() == noAxesButton)
			{
				grafiek3DComponent.zetGeenAssen(true, figuurKeuze);
				rightPanel.setWidgetVisible(axesButton,true);
				rightPanel.setWidgetVisible(noAxesButton,false);
			}

			
		}
	}
	
	public Widget asWidget()
	{	return dlp;
	}
	
	/**
	 * get the viewing options for the 3d-object from the launch data; 
	 * if the user plays with examples, this playing is not saved     
	 */
	public HashMap<String, Object> getState()
	{
		if (!voorbeeldenUsed)
			return grafiek3DComponent.getState();
		else
			return grafiek3DComponent.resetState;
	}

	/**
	 * set the viewing options for the 3d-object from the launch data; see method getState()
	 */
	public void setState(HashMap<String, Object> h)
	{
		if(h == null || h.isEmpty()) 
			return;
		grafiek3DComponent.setState(h,true);

	}

	@Override
	public int getScore()
	{	// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean isCorrect()
	{	// TODO Auto-generated method stub
		return Boolean.TRUE;
	}
	
	@Override
	public void kijkNa() 
	{		 
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{	// TODO Auto-generated method stub

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

	public int getHeight() 
	{	return hoogte;
	}

	public int getWidth() 
	{	return breedte;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void zetNagekeken(boolean b) {
	}


	@Override
	public int[][] getScoreObjectives() {
		return null;
	}


}

