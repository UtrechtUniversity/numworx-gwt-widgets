package fi.grafiek3dgwt.client;

import java.util.HashMap;
import java.util.ArrayList;

import javax.swing.JComboBox;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.resources.client.ImageResource;

import fi.grafiek3dgwt.client.formuleobjects.*;
import fi.grafiek3dgwt.client.expressies.*;

public class Grafiek3DGWT implements EntryPoint, InteractionView 
{
	
	public static final String languageString = "nl";
	public static String deployVariant = "";
	
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	// UI
	DockLayoutPanel dlp;
	LayoutPanel topPanel;
	LayoutPanel rightPanel;
	Grafiek3DComponent grafiek3DComponent;
	
	ListBox figuurKeuzeBox;
	ListBox voorbeeldKeuzeBox;
	ListBox grafiekKeuzeBox, oppervlakKeuzeBox, krommeKeuzeBox;
	
	//ListBox grafiekVoorbeeldBox;
	//ListBox oppervlakVoorbeeldBox;
	//ListBox krommeVoorbeeldBox;
	
	ArrayList<GrafiekVoorbeeld> grafiekVoorbeelden;
	ArrayList<OppervlakVoorbeeld> oppervlakVoorbeelden;
	ArrayList<KrommeVoorbeeld> krommeVoorbeelden;
	
	//KladjeGWTVeld kladjeGWTVeld;
	//Canvas kladjeGWTCanvas;
	//ToggleButton tekenButton, gumButton, tekenLijnButton, tekenRechthoekButton, tekenCirkelButton,
    //			 tekenTekstButton, selecterenButton;

	
	int breedte = 500;
	int hoogte = 450;
	int topHeight = 32;
	int rightWidth = 32;
	int leftOffset = 5;
	int topOffset = 10;
	int figuurKeuzeWidth = 170;
	int figuurKeuzeHeight = 22;
	int voorbeeldKeuzeWidth = 200;
	
	int buttonSize = 22;
	
    public static final int FUNCTION = 0;
    public static final int SURFACE = 1;
    public static final int CURVE = 2;
	int figuurKeuze = FUNCTION;
	
	int voorbeeldKeuze = 0;
	
	//int toggleSize = 22;
	//int buttonWidth = 40;
	//int buttonHeight = 22;

	private HashMap<String, Object> launchState;
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
	
	PushButton resetButton, zoomInButton, zoomUitButton, fijnerButton, groverButton;
	ToggleButton solidWireButton, projectieButton, assenButton;
	
	PushButton transPlusButton, transMinButton;
	ToggleButton xAsButton, yAsButton, zAsButton;
	
	// edit state variables
	boolean zoomOptie = true;
	boolean translateOptie = true;
	boolean solidDraadKeuzeOptie = true;
	boolean finerKeuzeOptie = true;
	boolean asKeuzeOptie = true;
	boolean labelKeuzeOptie = true;
	boolean projectieKeuzeOptie = true;
	boolean kleurKeuzeOptie = true;
	
	boolean figuurIsDemo = false;
	
	public static int XTRANS = 0;
	public static int YTRANS = 1;
	public static int ZTRANS = 2;
	int transDirection = XTRANS;
	

	boolean touchStart = false;
	
	public void getImages() 
	{
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
		solidImage.addStyleName(grafiek3DGWTCss.downimage());
		
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
		assenImage.addStyleName(grafiek3DGWTCss.downimage());
		
		parallelResource = grafiek3DGWTClientBundle.parallelResource();
		parallelImage = new Image(parallelResource);
		parallelImage.addStyleName(grafiek3DGWTCss.upimage());
		
		centraalResource = grafiek3DGWTClientBundle.centraalResource();
		centraalImage = new Image(centraalResource);
		centraalImage.addStyleName(grafiek3DGWTCss.downimage());
		
		
	}
	
	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(grafiek3DGWTCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(grafiek3DGWTCss.root());

		grafiek3DComponent = new Grafiek3DComponent(this, breedte - rightWidth, hoogte - topHeight);

		if (grafiek3DComponent.grafiek3DCanvas == null) 
		{
	      RootPanel.get(holderId).add(new Label(upgradeMessage));
	      return;
	    }
		
		// de eerste krijgt de volle hoogte cq breedte
		rightPanel = new LayoutPanel();
		rightPanel.addStyleName(grafiek3DGWTCss.right());

		dlp.addEast(rightPanel, rightWidth);

		topPanel = new LayoutPanel();
		topPanel.addStyleName(grafiek3DGWTCss.bottom());

		dlp.addNorth(topPanel, topHeight);
		//dlp.addSouth(topPanel, topHeight);
		
		dlp.add(grafiek3DComponent.grafiek3DCanvas);		
		
		makeTop();
		makeRight();
		
		processInput(voorbeeldKeuze);
		
//		kladjeGWTVeld = new KladjeGWTVeld(breedte, hoogte - bottomHeight, true); 

//		kladjeGWTCanvas = kladjeGWTVeld.getCanvas();
//		if (kladjeGWTCanvas == null) {
//	      RootPanel.get(holderId).add(new Label(upgradeMessage));
//	      return;
//	    }
		
//		kladjeGWTCanvas.addStyleName("canvas");
//		kladjeGWTVeld.initContext2d();		
		
		//dlp.add(kladjeGWTCanvas);
//		dlp.add(kladjeGWTVeld.getAsPanel());

//		makeBottom();
		
//		kladjeGWTVeld.paint();

			
	}
	
	public void makeRight()
	{
		int currentX = leftOffset;
		int currentY = topOffset;

	   	if (zoomOptie || translateOptie)
    	{
	   		resetButton = new PushButton(resetImage);
			rightPanel.add(resetButton);
			rightPanel.setWidgetLeftWidth(resetButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(resetButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			resetButton.addMouseDownHandler(new PushMouseDownHandler());
	   		
    		currentY += buttonSize + topOffset;
    	}

    	if (zoomOptie)
    	{
    		
	   		zoomInButton = new PushButton(zoomInImage);
			rightPanel.add(zoomInButton);
			rightPanel.setWidgetLeftWidth(zoomInButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(zoomInButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			zoomInButton.addMouseDownHandler(new PushMouseDownHandler());
	   		
    		currentY += buttonSize + topOffset;
    		
	   		zoomUitButton = new PushButton(zoomUitImage);
			rightPanel.add(zoomUitButton);
			rightPanel.setWidgetLeftWidth(zoomUitButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(zoomUitButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			zoomUitButton.addMouseDownHandler(new PushMouseDownHandler());
	   		
    		currentY += buttonSize + topOffset;
    		
    	}

    	
    	if (translateOptie)
    	{	
    		
	   		transPlusButton = new PushButton("+");
	   		transPlusButton.addStyleName(grafiek3DGWTCss.pushbutton());
			rightPanel.add(transPlusButton);
			rightPanel.setWidgetLeftWidth(transPlusButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(transPlusButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			transPlusButton.addMouseDownHandler(new PushMouseDownHandler());
	   		
    		currentY += buttonSize + topOffset;
    		
    		xAsButton = new ToggleButton("x", "X");
    		xAsButton.addStyleName(grafiek3DGWTCss.togglebutton());
			rightPanel.add(xAsButton);
			rightPanel.setWidgetLeftWidth(xAsButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(xAsButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
			xAsButton.addMouseDownHandler(new ToggleMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
    		yAsButton = new ToggleButton("y", "Y");
    		yAsButton.addStyleName(grafiek3DGWTCss.togglebutton());
			rightPanel.add(yAsButton);
			rightPanel.setWidgetLeftWidth(yAsButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(yAsButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
			yAsButton.addMouseDownHandler(new ToggleMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
    		zAsButton = new ToggleButton("z", "Z");
    		zAsButton.addStyleName(grafiek3DGWTCss.togglebutton());
			rightPanel.add(zAsButton);
			rightPanel.setWidgetLeftWidth(zAsButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(zAsButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
			zAsButton.addMouseDownHandler(new ToggleMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
	   		transMinButton = new PushButton("-");
	   		transMinButton.addStyleName(grafiek3DGWTCss.pushbutton());
			rightPanel.add(transMinButton);
			rightPanel.setWidgetLeftWidth(transMinButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(transMinButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			transMinButton.addMouseDownHandler(new PushMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
    	}

     	
    	//if (solidDraadKeuzeOptie && (figuurKeuze != CURVE))
    	if (solidDraadKeuzeOptie)
    	{	
    		solidWireButton = new ToggleButton(wireFrameImage, solidImage);
    		
			rightPanel.add(solidWireButton);
			rightPanel.setWidgetLeftWidth(solidWireButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(solidWireButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
    		currentY += buttonSize + topOffset;
    		
    		solidWireButton.addMouseDownHandler(new ToggleMouseDownHandler());

    	}	
    	//if (finerKeuzeOptie && (objectType == FUNCTION))
    	if (finerKeuzeOptie)	
    	{	
	   		fijnerButton = new PushButton(fijnerImage);
			rightPanel.add(fijnerButton);
			rightPanel.setWidgetLeftWidth(fijnerButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(fijnerButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);

			fijnerButton.addMouseDownHandler(new PushMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
	   		groverButton = new PushButton(groverImage);
			rightPanel.add(groverButton);
			rightPanel.setWidgetLeftWidth(groverButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(groverButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
			groverButton.addMouseDownHandler(new PushMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
    		//if (grafiek3DComponent.xFinerStepsG == 2)
    		//	groverButton.setEnabled(false);
    	}
    	if (asKeuzeOptie)
    	{
    		
    		assenButton = new ToggleButton(geenAssenImage, assenImage);
    		
			rightPanel.add(assenButton);
			rightPanel.setWidgetLeftWidth(assenButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(assenButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
    		currentY += buttonSize + topOffset;
    		
    		assenButton.addMouseDownHandler(new ToggleMouseDownHandler());
    	}

/*    	
    	if (labelKeuzeOptie)
    	{	
    		labelKeuzeButton.setBounds(5, currentY, 31, 21);
    		labelKeuzeButton.setVisible(true);
    		currentY += 31;
    	}
*/    	
    	if (projectieKeuzeOptie)
    	{
    		
    		projectieButton = new ToggleButton(parallelImage, centraalImage);
    		
			rightPanel.add(projectieButton);
			rightPanel.setWidgetLeftWidth(projectieButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(projectieButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
    		currentY += buttonSize + topOffset;
    		
    		projectieButton.addMouseDownHandler(new ToggleMouseDownHandler());
    		
    	}

/*    	
    	if (kleurKeuzeOptie && (objectType != CURVE))
    	{	
    		kleurKeuzeButton.setBounds(5, currentY, 31, 21);
    		kleurKeuzeButton.setVisible(true);
    		currentY += 31;
    	}
    	
*/    	
 		
	}
	
	public void makeTop()
	{
		int currentX = leftOffset;
		int currentY = topOffset;
		
		figuurKeuzeBox = new ListBox();
		figuurKeuzeBox.addItem("grafiek");
		figuurKeuzeBox.addItem("oppervlak");
		figuurKeuzeBox.addItem("kromme");
		
		figuurKeuzeBox.setVisibleItemCount(1);
		
		topPanel.add(figuurKeuzeBox);
		topPanel.setWidgetLeftWidth(figuurKeuzeBox, currentX, Style.Unit.PX, figuurKeuzeWidth, Style.Unit.PX);
		topPanel.setWidgetTopHeight(figuurKeuzeBox, currentY, Style.Unit.PX, figuurKeuzeHeight, Style.Unit.PX);
		
		//figuurKeuzeBox.addTouchStartHandler(new ListTouchStartHandler());
		//figuurKeuzeBox.addMouseDownHandler(new ListMouseDownHandler());
		figuurKeuzeBox.addChangeHandler(new ListChangeHandler());
		
		currentX += figuurKeuzeWidth + leftOffset;
		
		maakGrafiekVoorbeelden();
		maakOppervlakVoorbeelden();
		maakKrommeVoorbeelden();

		voorbeeldKeuzeBox = new ListBox();
		for (int gCnt = 0; gCnt < grafiekVoorbeelden.size(); gCnt++)
		{	String name = ((GrafiekVoorbeeld) grafiekVoorbeelden.get(gCnt)).nlNaam;
			voorbeeldKeuzeBox.addItem(name);
		}
//System.out.println("gv = " + grafiekVoorbeelden.size());

		voorbeeldKeuzeBox.setVisibleItemCount(1);

		topPanel.add(voorbeeldKeuzeBox);
		topPanel.setWidgetLeftWidth(voorbeeldKeuzeBox, currentX, Style.Unit.PX, voorbeeldKeuzeWidth, Style.Unit.PX);
		topPanel.setWidgetTopHeight(voorbeeldKeuzeBox, currentY, Style.Unit.PX, figuurKeuzeHeight, Style.Unit.PX);

		//voorbeeldKeuzeBox.addTouchStartHandler(new ListTouchStartHandler());
		//voorbeeldKeuzeBox.addMouseDownHandler(new ListMouseDownHandler());
		voorbeeldKeuzeBox.addChangeHandler(new ListChangeHandler());
	
/*		
		grafiekKeuzeBox = new ListBox();
		for (int gCnt = 0; gCnt < grafiekVoorbeelden.size(); gCnt++)
		{	String name = ((GrafiekVoorbeeld) grafiekVoorbeelden.get(gCnt)).nlNaam;
			grafiekKeuzeBox.addItem(name);
		}
		
		topPanel.add(grafiekKeuzeBox);
		topPanel.setWidgetLeftWidth(grafiekKeuzeBox, currentX, Style.Unit.PX, voorbeeldKeuzeWidth, Style.Unit.PX);
		topPanel.setWidgetTopHeight(grafiekKeuzeBox, currentY, Style.Unit.PX, figuurKeuzeHeight, Style.Unit.PX);

		grafiekKeuzeBox.addTouchStartHandler(new ListTouchStartHandler());
		grafiekKeuzeBox.addMouseDownHandler(new ListMouseDownHandler());
		
		oppervlakKeuzeBox = new ListBox();
		for (int gCnt = 0; gCnt < oppervlakVoorbeelden.size(); gCnt++)
		{	String name = ((OppervlakVoorbeeld) oppervlakVoorbeelden.get(gCnt)).nlNaam;
			oppervlakKeuzeBox.addItem(name);
		}
		
		topPanel.add(oppervlakKeuzeBox);
		topPanel.setWidgetLeftWidth(oppervlakKeuzeBox, currentX, Style.Unit.PX, voorbeeldKeuzeWidth, Style.Unit.PX);
		topPanel.setWidgetTopHeight(oppervlakKeuzeBox, currentY, Style.Unit.PX, figuurKeuzeHeight, Style.Unit.PX);

		oppervlakKeuzeBox.addTouchStartHandler(new ListTouchStartHandler());
		oppervlakKeuzeBox.addMouseDownHandler(new ListMouseDownHandler());
		
		oppervlakKeuzeBox.setVisible(false);
		
		krommeKeuzeBox = new ListBox();
		for (int gCnt = 0; gCnt < krommeVoorbeelden.size(); gCnt++)
		{	String name = ((KrommeVoorbeeld) krommeVoorbeelden.get(gCnt)).nlNaam;
		krommeKeuzeBox.addItem(name);
		}
		
		topPanel.add(krommeKeuzeBox);
		topPanel.setWidgetLeftWidth(krommeKeuzeBox, currentX, Style.Unit.PX, voorbeeldKeuzeWidth, Style.Unit.PX);
		topPanel.setWidgetTopHeight(krommeKeuzeBox, currentY, Style.Unit.PX, figuurKeuzeHeight, Style.Unit.PX);

		krommeKeuzeBox.addTouchStartHandler(new ListTouchStartHandler());
		krommeKeuzeBox.addMouseDownHandler(new ListMouseDownHandler());
		
		krommeKeuzeBox.setVisible(false);
*/		
		
		currentX += voorbeeldKeuzeWidth + leftOffset;
	}
	
	
	public Grafiek3DGWT()
	{
		this(null, null, null);
	}
	
	public Grafiek3DGWT(HashMap<String, Object> h, String[] randomVarNamen, HashMap<String, Object> randomVarWaarden)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		if (h != null && h.get("breedte") != null)
			breedte = (Integer) h.get("breedte");
		if (h != null && h.get("hoogte") != null)
			hoogte = (Integer) h.get("hoogte");
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");

		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(grafiek3DGWTCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

//		bottomPanel = new LayoutPanel();
//		bottomPanel.addStyleName(grafiek3DGWTCss.bottom());
		
//		dlp.addSouth(bottomPanel, bottomHeight);
		
//		kladjeGWTVeld = new KladjeGWTVeld(breedte, hoogte - bottomHeight, true); 

//		kladjeGWTCanvas = kladjeGWTVeld.getCanvas();
		
//		kladjeGWTCanvas.addStyleName("canvas");
//		kladjeGWTVeld.initContext2d();		
		
		//dlp.add(kladjeGWTCanvas);
//		dlp.add(kladjeGWTVeld.getAsPanel());

//		makeBottom();
		

	}
	
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

	public void toonVoorbeeldenBox(int type)
	{
		voorbeeldKeuzeBox.clear();
		
		if (type == FUNCTION)
		{
			for (int gCnt = 0; gCnt < grafiekVoorbeelden.size(); gCnt++)
			{	String name = ((GrafiekVoorbeeld) grafiekVoorbeelden.get(gCnt)).nlNaam;
				voorbeeldKeuzeBox.addItem(name);
			}
			
			//grafiekKeuzeBox.setVisible(true);
			//oppervlakKeuzeBox.setVisible(false);
			//krommeKeuzeBox.setVisible(false);
			
		}
		else if (type == SURFACE)
		{
			for (int oCnt = 0; oCnt < oppervlakVoorbeelden.size(); oCnt++)
			{	String name = ((OppervlakVoorbeeld) oppervlakVoorbeelden.get(oCnt)).nlNaam;
				voorbeeldKeuzeBox.addItem(name);

			}
			
			//grafiekKeuzeBox.setVisible(false);
			//oppervlakKeuzeBox.setVisible(true);
			//krommeKeuzeBox.setVisible(false);
			
			
		}
		else if (type == CURVE)
		{
			for (int kCnt = 0; kCnt < krommeVoorbeelden.size(); kCnt++)
			{	String name = ((KrommeVoorbeeld) krommeVoorbeelden.get(kCnt)).nlNaam;
				voorbeeldKeuzeBox.addItem(name);
			}

			//grafiekKeuzeBox.setVisible(false);
			//oppervlakKeuzeBox.setVisible(false);
			//krommeKeuzeBox.setVisible(true);
			
		}
		
		voorbeeldKeuze = 0;
	}
	
	public void zetTestString(String ts)
	{
		grafiek3DComponent.panel3D.testString = ts;
	}
	
	public void processInput(int voorbeeldKeuze)
	{
		// knoppen terugzetten
		solidWireButton.setDown(false);
		projectieButton.setDown(false);
		assenButton.setDown(false);
		
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
			
			//grafiek3DComponent.panel3D.repaint();
				
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
			
			//grafiek3DComponent.panel3D.repaint();
			
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
			
			//grafiek3DComponent.panel3D.repaint();
			
		}
		
		
		
	}
	
   	void buttonsUp(ToggleButton tb)
   	{
   		if (!xAsButton.equals(tb))
   			xAsButton.setDown(false);
   		if (!yAsButton.equals(tb))
   			yAsButton.setDown(false);
 		if (!zAsButton.equals(tb))
   			zAsButton.setDown(false);
   	}
	
	class ToggleMouseDownHandler implements MouseDownHandler
	{
		public void onMouseDown(MouseDownEvent e)
		{
			// deze LIJKT niet nodig (mag wel)
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
			else if (e.getSource() == solidWireButton)
			{
				if (!solidWireButton.isDown())
				{
					grafiek3DComponent.zetDraadFiguur(true, figuurKeuze);
				}
				else
				{
					grafiek3DComponent.zetDraadFiguur(false, figuurKeuze);
				}
			}
			else if (e.getSource() == assenButton)
			{
				if (!assenButton.isDown())
				{
					grafiek3DComponent.zetGeenAssen(true, figuurKeuze);
				}
				else
				{
					grafiek3DComponent.zetxyzAs(true, figuurKeuze);
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

	class PushMouseDownHandler implements MouseDownHandler
	{
		public void onMouseDown(MouseDownEvent e)
		{
			if (touchStart)
				return;
			
			// deze LIJKT niet nodig (mag wel)
			//e.preventDefault();
			// deze zorgt dat je niet scollt in de DWOPlayer
			e.stopPropagation();

			if (e.getSource() == resetButton)
			{
				//grafiek3DComponent.zoomStandaard(true, figuurKeuze);
				processInput(voorbeeldKeuze);
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
		}
	}

	//class ListMouseDownHandler implements MouseDownHandler
	class ListChangeHandler implements ChangeHandler
	{
		//public void onMouseDown(MouseDownEvent e)
		public void onChange(ChangeEvent e)
		{
			// deze LIJKT niet nodig (mag wel)
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
					
					processInput(voorbeeldKeuze);
					
				}
				else if ((index == 1) && (figuurKeuze != SURFACE))
				{
					figuurKeuze = SURFACE;

					toonVoorbeeldenBox(SURFACE);
					
					processInput(voorbeeldKeuze);
					
				}
				else if ((index == 2) && (figuurKeuze != CURVE))
				{
					figuurKeuze = CURVE;

					toonVoorbeeldenBox(CURVE);					
					
					processInput(voorbeeldKeuze);
					
				}
				
			}
			else if (e.getSource() == voorbeeldKeuzeBox)
			{
				int index = voorbeeldKeuzeBox.getSelectedIndex();
				
				if (index != voorbeeldKeuze)
				{
					voorbeeldKeuze = index;
				 
					processInput(voorbeeldKeuze);
				}	
			}
		}
	}

	class ToggleTouchStartHandler implements TouchStartHandler
	{
		public void onTouchStart(TouchStartEvent e)
		{
			// deze LIJKT niet nodig (mag wel)
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
			else if (e.getSource() == solidWireButton)
			{
				if (!solidWireButton.isDown())
				{
					grafiek3DComponent.zetDraadFiguur(true, figuurKeuze);
				}
				else
				{
					grafiek3DComponent.zetDraadFiguur(false, figuurKeuze);
				}
			}
			else if (e.getSource() == assenButton)
			{
				if (!assenButton.isDown())
				{
					grafiek3DComponent.zetGeenAssen(true, figuurKeuze);
				}
				else
				{
					grafiek3DComponent.zetxyzAs(true, figuurKeuze);
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

	class PushTouchStartHandler implements TouchStartHandler
	{
		public void onTouchStart(TouchStartEvent e)
		{
			touchStart = true;
			
			// deze LIJKT niet nodig (mag wel)
			//e.preventDefault();
			// deze zorgt dat je niet scollt in de DWOPlayer
			e.stopPropagation();

			if (e.getSource() == resetButton)
			{
				//grafiek3DComponent.zoomStandaard(true, figuurKeuze);
				processInput(voorbeeldKeuze);
				
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
			
		}
	}
	
	class ListTouchStartHandler implements TouchStartHandler
	{
		public void onTouchStart(TouchStartEvent e)
		{
    		// DIT NIET toevoegen!!
    		//e.preventDefault();
			e.stopPropagation();

			if (e.getSource() == figuurKeuzeBox)
			{
				int index = figuurKeuzeBox.getSelectedIndex();
				
				if ((index == 0) && (figuurKeuze != FUNCTION))
				{
					figuurKeuze = FUNCTION;
					
					toonVoorbeeldenBox(FUNCTION);
					
					processInput(voorbeeldKeuze);
					
				
				}
				else if ((index == 1) && (figuurKeuze != SURFACE))
				{
					figuurKeuze = SURFACE;

					toonVoorbeeldenBox(SURFACE);
					
					processInput(voorbeeldKeuze);
					
				}
				else if ((index == 2) && (figuurKeuze != CURVE))
				{
					figuurKeuze = CURVE;

					toonVoorbeeldenBox(CURVE);					
					
					processInput(voorbeeldKeuze);
					
				}
			}	
			else if (e.getSource() == voorbeeldKeuzeBox)
			{
				int index = voorbeeldKeuzeBox.getSelectedIndex();
				
				if (index != voorbeeldKeuze)
				{
					voorbeeldKeuze = index;
				 
					processInput(voorbeeldKeuze);
				}
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getScore()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isCorrect()
	{
		// TODO Auto-generated method stub
		return false;
	}
	

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		// TODO Auto-generated method stub

	}
}

class Point
{
	int x; int y;
	
	public Point()
	{
		x = 0; y = 0;
	}
	
	public Point(int x, int y)
	{
		this.x = x; this.y = y;
	}
	
    // redefine for method contains in Vector     
    // equality of this Point and Point obj
    public boolean equals(Object obj)
    {    if (obj instanceof Point)
             return (x == ((Point) obj).x) && (y == ((Point) obj).y);
         return false;    
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
