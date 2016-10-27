package fi.grafiek3dgwt.client;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

//import javax.swing.JComboBox;

//import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

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

import fi.grafiek3dgwt.client.text.Text;

public class Grafiek3DGWT implements EntryPoint, InteractionStub //, InteractionView
{
	public static Text rb;
	
	static Logger logger = Logger.getLogger("WebLogoGWT");
	
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
	Label figuurKeuzeLabel;
	//ListBox grafiekKeuzeBox, oppervlakKeuzeBox, krommeKeuzeBox;
	
	ArrayList<GrafiekVoorbeeld> grafiekVoorbeelden;
	ArrayList<OppervlakVoorbeeld> oppervlakVoorbeelden;
	ArrayList<KrommeVoorbeeld> krommeVoorbeelden;
	
	boolean voorbeeldenEnabled = true;
	boolean functieTypeKeuze = true;
	
	int functieType = 0;
	
	String graphString = "$f@";
	String surfaceXString = "$f@";
	String surfaceYString = "$f@";
	String surfaceZString = "$f@";
	String uMinString = "$f@";
	String uMaxString = "$f@";
	String uPointsString = "$f@";
	String vMinString = "$f@";
	String vMaxString = "$f@";
	String vPointsString = "$f@";
	String curveXString = "$f@";
	String curveYString = "$f@";
	String curveZString = "$f@";
	String tMinString = "$f@";
	String tMaxString = "$f@";
	String tPointsString = "$f@";
	
	final double NZERO = 1e-5d;
	
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
	
	PushButton resetButton, zoomInButton, zoomUitButton, fijnerButton, groverButton;
	ToggleButton solidWireButton, projectieButton, assenButton;
	PushButton solidButton, wireButton;
	PushButton axesButton, noAxesButton;
	
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
	
	boolean voorbeeldenUsed = false;
	
	public void getImages() 
	{
		rb = GWT.create(Text.class);
		
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
		//solidImage.addStyleName(grafiek3DGWTCss.downimage());
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
		//assenImage.addStyleName(grafiek3DGWTCss.downimage());
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

/*
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
		
*/			
	}
	
	public void makeRight()
	{
		int currentX = leftOffset;
		int currentY = topOffset;

		resetButton = new PushButton(resetImage);
		
	   	if ((zoomOptie || translateOptie) && !figuurIsDemo)
    	{
	   		//resetButton = new PushButton(resetImage);
			rightPanel.add(resetButton);
			rightPanel.setWidgetLeftWidth(resetButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(resetButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			resetButton.addMouseDownHandler(new PushMouseDownHandler());
	   		
    		currentY += buttonSize + topOffset;
    	}

	   	zoomInButton = new PushButton(zoomInImage);
	   	zoomUitButton = new PushButton(zoomUitImage);
	   	
    	if (zoomOptie && !figuurIsDemo)
    	{
    		
	   		//zoomInButton = new PushButton(zoomInImage);
			rightPanel.add(zoomInButton);
			rightPanel.setWidgetLeftWidth(zoomInButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(zoomInButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			zoomInButton.addMouseDownHandler(new PushMouseDownHandler());
	   		
    		currentY += buttonSize + topOffset;
    		
	   		//zoomUitButton = new PushButton(zoomUitImage);
			rightPanel.add(zoomUitButton);
			rightPanel.setWidgetLeftWidth(zoomUitButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(zoomUitButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			zoomUitButton.addMouseDownHandler(new PushMouseDownHandler());
	   		
    		currentY += buttonSize + topOffset;
    		
    	}

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
    		
	   		//transPlusButton = new PushButton("+");
	   		//transPlusButton.addStyleName(grafiek3DGWTCss.pushbutton());
			rightPanel.add(transPlusButton);
			rightPanel.setWidgetLeftWidth(transPlusButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(transPlusButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			transPlusButton.addMouseDownHandler(new PushMouseDownHandler());
	   		
    		currentY += buttonSize + topOffset;
    		
    		//xAsButton = new ToggleButton("x", "X");
    		//xAsButton.addStyleName(grafiek3DGWTCss.togglebutton());
			rightPanel.add(xAsButton);
			rightPanel.setWidgetLeftWidth(xAsButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(xAsButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
			xAsButton.addMouseDownHandler(new ToggleMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
    		//yAsButton = new ToggleButton("y", "Y");
    		//yAsButton.addStyleName(grafiek3DGWTCss.togglebutton());
			rightPanel.add(yAsButton);
			rightPanel.setWidgetLeftWidth(yAsButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(yAsButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
			yAsButton.addMouseDownHandler(new ToggleMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
    		//zAsButton = new ToggleButton("z", "Z");
    		//zAsButton.addStyleName(grafiek3DGWTCss.togglebutton());
			rightPanel.add(zAsButton);
			rightPanel.setWidgetLeftWidth(zAsButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(zAsButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
			zAsButton.addMouseDownHandler(new ToggleMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
	   		//transMinButton = new PushButton("-");
	   		//transMinButton.addStyleName(grafiek3DGWTCss.pushbutton1());
			rightPanel.add(transMinButton);
			rightPanel.setWidgetLeftWidth(transMinButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(transMinButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			transMinButton.addMouseDownHandler(new PushMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
    	}

    	solidWireButton = new ToggleButton(wireFrameImage, solidImage);
    	solidButton = new PushButton(solidImage);
    	wireButton = new PushButton(wireFrameImage);
    	
    	if (solidDraadKeuzeOptie && !figuurIsDemo && functieType != CURVE)
    	{	
    		//solidWireButton = new ToggleButton(wireFrameImage, solidImage);
    		
			//rightPanel.add(solidWireButton);
			//rightPanel.setWidgetLeftWidth(solidWireButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			//rightPanel.setWidgetTopHeight(solidWireButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			rightPanel.add(solidButton);
			rightPanel.setWidgetLeftWidth(solidButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(solidButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetVisible(solidButton, false);
			
			rightPanel.add(wireButton);
			rightPanel.setWidgetLeftWidth(wireButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(wireButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
    		currentY += buttonSize + topOffset;
    		
    		//solidWireButton.addMouseDownHandler(new ToggleMouseDownHandler());
    		
    		solidButton.addMouseDownHandler(new PushMouseDownHandler());
    		wireButton.addMouseDownHandler(new PushMouseDownHandler());

    	}	

    	fijnerButton = new PushButton(fijnerImage);
    	groverButton = new PushButton(groverImage);
    	
    	if (finerKeuzeOptie  && !figuurIsDemo && functieType == FUNCTION)	
    	{	
	   		//fijnerButton = new PushButton(fijnerImage);
			rightPanel.add(fijnerButton);
			rightPanel.setWidgetLeftWidth(fijnerButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(fijnerButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);

			fijnerButton.addMouseDownHandler(new PushMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
	   		//groverButton = new PushButton(groverImage);
			rightPanel.add(groverButton);
			rightPanel.setWidgetLeftWidth(groverButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(groverButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
	   		
			groverButton.addMouseDownHandler(new PushMouseDownHandler());
			
    		currentY += buttonSize + topOffset;
    		
    		//if (grafiek3DComponent.xFinerStepsG == 2)
    		//	groverButton.setEnabled(false);
    	}
    	
    	assenButton = new ToggleButton(geenAssenImage, assenImage);
    	axesButton = new PushButton(assenImage);
    	noAxesButton = new PushButton(geenAssenImage);
    	
    	if (asKeuzeOptie  && !figuurIsDemo)
    	{
    		
    		//assenButton = new ToggleButton(geenAssenImage, assenImage);
    		
			//rightPanel.add(assenButton);
			//rightPanel.setWidgetLeftWidth(assenButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			//rightPanel.setWidgetTopHeight(assenButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
			rightPanel.add(axesButton);
			rightPanel.setWidgetLeftWidth(axesButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(axesButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetVisible(axesButton, false);
			
			rightPanel.add(noAxesButton);
			rightPanel.setWidgetLeftWidth(noAxesButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
			rightPanel.setWidgetTopHeight(noAxesButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
			
	   		
    		currentY += buttonSize + topOffset;
    		
    		assenButton.addMouseDownHandler(new ToggleMouseDownHandler());
    		axesButton.addMouseDownHandler(new PushMouseDownHandler());
    		noAxesButton.addMouseDownHandler(new PushMouseDownHandler());
    		
    	}

/*    	
    	if (labelKeuzeOptie)
    	{	
    		labelKeuzeButton.setBounds(5, currentY, 31, 21);
    		labelKeuzeButton.setVisible(true);
    		currentY += 31;
    	}
*/    	
    	
		projectieButton = new ToggleButton(parallelImage, centraalImage);
		
    	if (projectieKeuzeOptie && !figuurIsDemo)
    	{
    		
    		//projectieButton = new ToggleButton(parallelImage, centraalImage);
    		
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
		figuurKeuzeBox.setSelectedIndex(functieType);
		
		String figuurKeuzeTekst = "";
		if (functieType == FUNCTION)
			figuurKeuzeTekst = "grafiek";
		else if (functieType == SURFACE)
			figuurKeuzeTekst = "oppervlak";
		else if (functieType == CURVE)
			figuurKeuzeTekst = "kromme";
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
		{	String name = ((GrafiekVoorbeeld) grafiekVoorbeelden.get(gCnt)).nlNaam;
			voorbeeldKeuzeBox.addItem(name);
		}
//System.out.println("gv = " + grafiekVoorbeelden.size());
		
		voorbeeldKeuzeBox.setVisibleItemCount(1);

		if (voorbeeldenEnabled)
		{	
			toonVoorbeeldenBox(functieType);
			topPanel.add(voorbeeldKeuzeBox);
			topPanel.setWidgetLeftWidth(voorbeeldKeuzeBox, currentX, Style.Unit.PX, voorbeeldKeuzeWidth, Style.Unit.PX);
			topPanel.setWidgetTopHeight(voorbeeldKeuzeBox, currentY, Style.Unit.PX, figuurKeuzeHeight, Style.Unit.PX);
			voorbeeldKeuzeBox.addChangeHandler(new ListChangeHandler());
		}	
	
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

		dlp.setSize("" + breedte + "px", "" + hoogte + "px");


		ObjectMap launchState = JSONUtilities.wrapMap(map);

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
//System.out.println("ft = " + functieType);		
		}
		figuurKeuze = functieType;
		
		if (launchState.containsKey("graphString")) 
			graphString = launchState.getString("graphString");

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
		
		// de eerste krijgt de volle hoogte cq breedte
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
		//if (!figuurIsDemo)
		makeRight();
		
		//processInput(voorbeeldKeuze);
		
//logger.info("Grafiek3DGWT init before g3d.setState");		
		grafiek3DComponent.setState(map, false);
//logger.info("Grafiek3DGWT init after g3d.setState");
		
		grafiek3DComponent.resetState = (HashMap) map;
		
		voorbeeldenUsed = false;
		
		processLaunchInput();
logger.info("Grafiek3DGWT init after processLaunch");		
		
//System.out.println("g3DC graphColor = " + grafiek3DComponent.graphColor.toString());
		
		dlp.forceLayout();
		
		grafiek3DComponent.panel3D.repaint();

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
			voorbeeldKeuzeBox.addItem("voorbeelden");
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
			voorbeeldKeuzeBox.addItem("voorbeelden");
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
			voorbeeldKeuzeBox.addItem("voorbeelden");
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
		voorbeeldenUsed = true;
		
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
			//Expressie exp = gv;
			
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

	public void processLaunchInput()
	{
		
		if (functieType == FUNCTION)
		{	
			Expressie exp = FormuleParser.geefExpressie(graphString);
			
			grafiek3DComponent.objectType = FUNCTION;
			grafiek3DComponent.zetGrafiek3D(exp);
			
			//grafiek3DComponent.zetVulKleur(grafiek3DComponent.graphColor, grafiek3DComponent.objectType);
			
//System.out.println("g3DC zetVulKleur " + grafiek3DComponent.graphColor.toString());			
			
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
			
			//grafiek3DComponent.zetVulKleur(grafiek3DComponent.surfaceColor, grafiek3DComponent.objectType);
			
			
		}
		else if (functieType == CURVE)
		{	
			
logger.info("Grafiek3DGWT processLaunch curve");			
			Expressie expX = FormuleParser.geefExpressie(curveXString);
			Expressie expY = FormuleParser.geefExpressie(curveYString);
			Expressie expZ = FormuleParser.geefExpressie(curveZString);
logger.info("Grafiek3DGWT processLaunch expressies");			
			Expressie expTMin = FormuleParser.geefExpressie(tMinString);
			double tMin = expTMin.geefWaarde();
logger.info("Grafiek3DGWT processLaunch tMin");			
			Expressie expTMax = FormuleParser.geefExpressie(tMaxString);
			double tMax = expTMax.geefWaarde();
logger.info("Grafiek3DGWT processLaunch tMax");			
			Expressie expTPointsDouble = FormuleParser.geefExpressie(tPointsString);
			int tPoints = (int) Math.round(expTPointsDouble.geefWaarde());
logger.info("Grafiek3DGWT processLaunch tPoints");			
			
			grafiek3DComponent.objectType = CURVE;
			grafiek3DComponent.zetCurve3D(expX, expY, expZ, tMin, tMax, tPoints);
logger.info("Grafiek3DGWT processLaunch zetCurve");			
			
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
				
//System.out.println("swDown = " + solidWireButton.isDown());

				if (!solidWireButton.isDown())
				{
//System.out.println("swDown true " + solidWireButton.isDown());					
					grafiek3DComponent.zetDraadFiguur(true, figuurKeuze);
//System.out.println("swDown true = " + solidWireButton.isDown());		

				}
				else
				{
//System.out.println("swDown false = " + solidWireButton.isDown());					
					grafiek3DComponent.zetDraadFiguur(false, figuurKeuze);
//System.out.println("swDown false = " + solidWireButton.isDown());

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
					
					//processInput(voorbeeldKeuze);
					
				}
				else if ((index == 1) && (figuurKeuze != SURFACE))
				{
					figuurKeuze = SURFACE;

					toonVoorbeeldenBox(SURFACE);
					
					//processInput(voorbeeldKeuze);
					
				}
				else if ((index == 2) && (figuurKeuze != CURVE))
				{
					figuurKeuze = CURVE;

					toonVoorbeeldenBox(CURVE);					
					
					//processInput(voorbeeldKeuze);
					
				}
				
			}
			else if (e.getSource() == voorbeeldKeuzeBox)
			{
				int index = voorbeeldKeuzeBox.getSelectedIndex();
				
				//if ((index - 1) != voorbeeldKeuze)
				//{
					voorbeeldKeuze = index - 1;
				 
					processInput(voorbeeldKeuze);
				//}	
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
				figuurKeuzeBox.setSelectedIndex(functieType);
				figuurKeuze = functieType;
				toonVoorbeeldenBox(functieType);
				voorbeeldKeuzeBox.setSelectedIndex(0);
				
				solidWireButton.setDown(false);
				projectieButton.setDown(false);
				assenButton.setDown(false);

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
					
					//processInput(voorbeeldKeuze);
					
				
				}
				else if ((index == 1) && (figuurKeuze != SURFACE))
				{
					figuurKeuze = SURFACE;

					toonVoorbeeldenBox(SURFACE);
					
					//processInput(voorbeeldKeuze);
					
				}
				else if ((index == 2) && (figuurKeuze != CURVE))
				{
					figuurKeuze = CURVE;

					toonVoorbeeldenBox(CURVE);					
					
					//processInput(voorbeeldKeuze);
					
				}
			}	
			else if (e.getSource() == voorbeeldKeuzeBox)
			{
				int index = voorbeeldKeuzeBox.getSelectedIndex();
				
				if ((index - 1) != voorbeeldKeuze)
				{
					voorbeeldKeuze = index - 1;
				 
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
System.out.println("g3D getState");

		if (!voorbeeldenUsed)
			return grafiek3DComponent.getState();
		else
			return grafiek3DComponent.resetState;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		if(h == null || h.isEmpty()) 
			return;
System.out.println("g3D setState");		
		grafiek3DComponent.setState(h,true);

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
		return Boolean.TRUE;
	}
	
	@Override
	public void kijkNa() 
	{		 

	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		// TODO Auto-generated method stub

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
