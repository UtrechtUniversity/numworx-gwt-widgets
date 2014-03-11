package fi.doorziengwt.client;

import java.util.*;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
//import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.resources.client.ImageResource;


public class DoorzienGWT implements EntryPoint, InteractionStub //InteractionView 
{
	
	MenuBar menuBar;
	MenuBar figurenMenu, optiesMenu;
	
	MenuItem geenHulpPuntenItem, eenHulpPuntItem, tweeHulpPuntenItem, drieHulpPuntenItem, vierHulpPuntenItem, lettersItem;
	MenuItem centraleProjectieItem, parallelProjectieItem;
	MenuItem achtvlakItem, balkItem, cilinderItem, piramideHuisItem, schildHuisItem,
		     kegel1Item, kegel2Item, kegel3Item, kegel4Item, kubusItem, 
		     piramide3Item, piramide4Item, piramide5Item, piramide6Item, piramide7Item, piramide8Item,
		     prisma3Item, prisma4Item, prisma5Item, prisma6Item, twaalfvlakItem, twintigvlakItem, viervlakItem,
		     mijnFiguurItem;
	MenuBar huizenMenu, kegelsMenu,piramidesMenu, prismasMenu; 
	
	// constants for figures
	public static final int OCTAHEDRON = 0;
	public static final int BLOCK = 1;
	public static final int CYLINDER = 2;
	public static final int HOUSES = 3;
    	public static final int PIRHOUSE = 31;
	    public static final int EDGEHOUSE = 32;	
	public static final int CONES = 4;
	    public static final int CONE1 = 41;
	    public static final int CONE2 = 42;
	    public static final int CONE3 = 43;
	    public static final int CONE4 = 44;
	public static final int CUBE = 5;
	public static final int PIRAMIDS = 6;
	    public static final int PIRAMID3 = 61;
	    public static final int PIRAMID4 = 62;
	    public static final int PIRAMID5 = 63;
	    public static final int PIRAMID6 = 64;
	    public static final int PIRAMID7 = 65;
	    public static final int PIRAMID8 = 66;	    
	public static final int PRISMS = 7;
	    public static final int PRISM3 = 71;
	    public static final int PRISM4 = 72;
	    public static final int PRISM5 = 73;
	    public static final int PRISM6 = 74;
	public static final int DODECAHEDRON = 8;
	public static final int ICOSAHEDRON = 9;	
	public static final int TETRAHEDRON = 10;

	public static final int MYFIGURE = 100;
	
	// versions
    public static final int EPN = 0;
    public static final int FI = 1;
    public static int version = EPN;
	
    ObjectGroup3D startModel;
    
 // circle radius for rotate modes
    public static double RADFACTOR = 1d;
    boolean dragging = false;
    boolean inCircle = false;
    int xStart, yStart;
    
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	// UI
	DockLayoutPanel dlp;
	LayoutPanel bottomPanel;
	LayoutPanel canvasPanel;
	DrawingShell drawingShell;
	
	// Popup met inhoud
	PopupPanel doorzienGWTPopup;
	DockLayoutPanel doorzienGWTDock;
	
	int breedte = 500;
	int hoogte = 450;
	int bottomHeight = 32;
	int leftOffset = 5;
	int topOffset = 5;
	
	int popupBreedte = 600;
	int popupHoogte = 550;
	
	int topBarHeight;
	int menuHeight = 25;
	int topToolBarHeight = 55; // inclusief de helpbar
	int rightToolBarWidth = 50;
	LayoutPanel topBar;
	TopToolBar2 topToolBar;
	RightToolBar2 rightToolBar;
	DrawingPanel2 drawingPanel;
	Label helpBar;
	
	CssColor bottomBgColor = CssColor.make(220, 220, 220);	
	
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;

	DoorzienGWTClientBundle doorzienGWTClientBundle;
	static DoorzienGWTCssResource doorzienGWTCss;
	
	ImageResource zoomInResource, zoomOutResource, resetResource, toolsResource;
	Image zoomInImage, zoomOutImage, resetImage, toolsImage;
	
	ImageResource conDrawResource, figureResource, cutResource, glueResource, deleteLineResource, drawLineResource,
	              deletePlaneResource, drawPlaneResource, parPlaneResource, planesEmptyResource, planesFilledResource,
	              hideCutResource, showCutResource, lengLinesResource, shortLinesResource, solidResource, wireframeResource,
	              redoResource, undoResource;

	Image conDrawImage, figureImage, cutImage, glueImage, deleteLineImage, drawLineImage,
    	  deletePlaneImage, drawPlaneImage, parPlaneImage, planesEmptyImage, planesFilledImage,
    	  hideCutImage, showCutImage, lengLinesImage, shortLinesImage, solidImage, wireframeImage,
    	  redoImage, undoImage;
	
	PushButton toolsButton, resetButton;
	
	boolean figurenMenuOptie = true;
	boolean optiesMenuOptie = true;
	boolean helpBarOptie = true;
	
	boolean lijnTekenOptie = true;
	boolean lijnVerlengOptie = true;
	
	boolean vlakTekenOptie = true;
	boolean evenwijdigVlakOptie = true;
	boolean toonDoorsnedeOptie = true;
	boolean splitsFiguurOptie = true;
	
	boolean bouwplaatOptie = true;
	
	boolean previewOptie = false;
	
	boolean designOption = true;
	boolean resetOption = true;
	
	//boolean demo = false;
	
	boolean letters = false;
	boolean hulpPunten = false;
	boolean centraleProjectie = true;
	
	HashMap<String,Object> resetState = null;
	
	int numLines = 0;
	int numPlanes = 0;
	
	boolean popupVisible = false;
	
	boolean touchStart = false;
	
	public void getImages() 
	{
		doorzienGWTClientBundle = GWT.create(DoorzienGWTClientBundle.class);
		doorzienGWTCss = doorzienGWTClientBundle.getDoorzienGWTCSS();
		doorzienGWTCss.ensureInjected();
		
		zoomInResource = doorzienGWTClientBundle.zoomInResource();
		zoomInImage = new Image(zoomInResource);
		zoomInImage.addStyleName(doorzienGWTCss.pushimage());

		zoomOutResource = doorzienGWTClientBundle.zoomOutResource();
		zoomOutImage = new Image(zoomOutResource);
		zoomOutImage.addStyleName(doorzienGWTCss.pushimage());
		
		resetResource = doorzienGWTClientBundle.resetResource();
		resetImage = new Image(resetResource);
		resetImage.addStyleName(doorzienGWTCss.pushimage());
		
		toolsResource = doorzienGWTClientBundle.toolsResource();
		toolsImage = new Image(toolsResource);
		toolsImage.addStyleName(doorzienGWTCss.pushimage());

		conDrawResource = doorzienGWTClientBundle.conDrawResource();
		conDrawImage = new Image(conDrawResource);
		conDrawImage.addStyleName(doorzienGWTCss.pushimage());
		
		figureResource = doorzienGWTClientBundle.figureResource();
		figureImage = new Image(figureResource);
		figureImage.addStyleName(doorzienGWTCss.pushimage());
		
		cutResource = doorzienGWTClientBundle.cutResource();
		cutImage = new Image(cutResource);
		cutImage.addStyleName(doorzienGWTCss.pushimage());
		
		glueResource = doorzienGWTClientBundle.glueResource();
		glueImage = new Image(glueResource);
		glueImage.addStyleName(doorzienGWTCss.pushimage());
		
		deleteLineResource = doorzienGWTClientBundle.deleteLineResource();
		deleteLineImage = new Image(deleteLineResource);
		deleteLineImage.addStyleName(doorzienGWTCss.pushimage());
		
		drawLineResource = doorzienGWTClientBundle.drawLineResource();
		drawLineImage = new Image(drawLineResource);
		drawLineImage.addStyleName(doorzienGWTCss.pushimage());
		
		deletePlaneResource = doorzienGWTClientBundle.deletePlaneResource();
		deletePlaneImage = new Image(deletePlaneResource);
		deletePlaneImage.addStyleName(doorzienGWTCss.pushimage());

		drawPlaneResource = doorzienGWTClientBundle.drawPlaneResource();
		drawPlaneImage = new Image(drawPlaneResource);
		drawPlaneImage.addStyleName(doorzienGWTCss.pushimage());
		
		parPlaneResource = doorzienGWTClientBundle.parPlaneResource();
		parPlaneImage = new Image(parPlaneResource);
		parPlaneImage.addStyleName(doorzienGWTCss.pushimage());
		
		planesEmptyResource = doorzienGWTClientBundle.planesEmptyResource();
		planesEmptyImage = new Image(planesEmptyResource);
		planesEmptyImage.addStyleName(doorzienGWTCss.pushimage());

		planesFilledResource = doorzienGWTClientBundle.planesFilledResource();
		planesFilledImage = new Image(planesFilledResource);
		planesFilledImage.addStyleName(doorzienGWTCss.pushimage());
		
		hideCutResource = doorzienGWTClientBundle.hideCutResource();
		hideCutImage = new Image(hideCutResource);
		hideCutImage.addStyleName(doorzienGWTCss.pushimage());
		
		showCutResource = doorzienGWTClientBundle.showCutResource();
		showCutImage = new Image(showCutResource);
		showCutImage.addStyleName(doorzienGWTCss.pushimage());
		
		lengLinesResource = doorzienGWTClientBundle.lengLinesResource();
		lengLinesImage = new Image(lengLinesResource);
		lengLinesImage.addStyleName(doorzienGWTCss.pushimage());
		
		shortLinesResource = doorzienGWTClientBundle.shortLinesResource();
		shortLinesImage = new Image(shortLinesResource);
		shortLinesImage.addStyleName(doorzienGWTCss.pushimage());
		
		solidResource = doorzienGWTClientBundle.solidResource();
		solidImage = new Image(solidResource);
		solidImage.addStyleName(doorzienGWTCss.pushimage());
		
		wireframeResource = doorzienGWTClientBundle.wireframeResource();
		wireframeImage = new Image(wireframeResource);
		wireframeImage.addStyleName(doorzienGWTCss.pushimage());

		redoResource = doorzienGWTClientBundle.redoResource();
		redoImage = new Image(redoResource);
		redoImage.addStyleName(doorzienGWTCss.pushimage());

		undoResource = doorzienGWTClientBundle.undoResource();
		undoImage = new Image(undoResource);
		undoImage.addStyleName(doorzienGWTCss.pushimage());
		
	}
	
	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		//dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(doorzienGWTCss.root());
		
		
		//Stub.publish(this);
		init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
		
	}

	public void makePopup()
	{
		if (doorzienGWTPopup != null)
			return;
		
		if (figurenMenuOptie || optiesMenuOptie)
			topBarHeight = menuHeight + topToolBarHeight;
		else
			topBarHeight = topToolBarHeight;
		
		doorzienGWTDock = new DockLayoutPanel(Style.Unit.PX);
		doorzienGWTDock.addStyleName(doorzienGWTCss.dock());
		doorzienGWTDock.setSize("" + popupBreedte + "px", "" + popupHoogte + "px");
		
		topBar = new LayoutPanel();
		topBar.setSize("" + popupBreedte + "px", "" + topBarHeight + "px");
		
		if (figurenMenuOptie || optiesMenuOptie)
		{	makeMenus();
			topBar.add(menuBar);
			topBar.setWidgetLeftWidth(menuBar, 0, Style.Unit.PX, popupBreedte, Style.Unit.PX);
			topBar.setWidgetTopHeight(menuBar, 0, Style.Unit.PX, menuHeight, Style.Unit.PX);
		}
		
		topToolBar = new TopToolBar2(this, popupBreedte);
		topToolBar.addStyleName(doorzienGWTCss.toolbar());
		topBar.add(topToolBar);
		topBar.setWidgetLeftWidth(topToolBar, 0, Style.Unit.PX, popupBreedte, Style.Unit.PX);
		if (figurenMenuOptie || optiesMenuOptie)
			topBar.setWidgetTopHeight(topToolBar, menuHeight, Style.Unit.PX, topBarHeight, Style.Unit.PX);
		else
			topBar.setWidgetTopHeight(topToolBar, 0, Style.Unit.PX, topBarHeight, Style.Unit.PX);
		
		doorzienGWTDock.addNorth(topBar, topBarHeight);
		//doorzienGWTDock.addNorth(topToolBar, topToolBarHeight);
		
		rightToolBar = new RightToolBar2(this);
		rightToolBar.addStyleName(doorzienGWTCss.toolbar());
		doorzienGWTDock.addEast(rightToolBar, rightToolBarWidth);
		
		drawingPanel = new DrawingPanel2(this, popupBreedte - rightToolBarWidth,
										 popupHoogte - topBarHeight, CUBE);
		//doorzienGWTDock.add(drawingPanel.drawingPanelCanvas);
		doorzienGWTDock.add(drawingPanel);
		
		figureToPopup();
			
		int popupX = dlp.getAbsoluteLeft();
		int popupY = dlp.getAbsoluteTop();
		
		//if (doorzienGWTPopup == null)
		doorzienGWTPopup = new PopupPanel();
		doorzienGWTPopup.setWidget(doorzienGWTDock);
		doorzienGWTPopup.setPopupPosition(popupX, popupY);
		doorzienGWTPopup.show();

	}
	
	public void makeMenus()
	{
		menuBar = new MenuBar();
		
		figurenMenu = new MenuBar(true);
		figurenMenu.addItem("achtvlak", new MenuCommand("achtvlak"));
		figurenMenu.addItem("balk", new MenuCommand("balk"));
		figurenMenu.addItem("cilinder", new MenuCommand("cilinder"));
		huizenMenu = new MenuBar(true);
		huizenMenu.addItem("huis met piramidedak", new MenuCommand("piramidehuis"));
		huizenMenu.addItem("huis met schilddak", new MenuCommand("schildhuis"));
		figurenMenu.addItem("huizen", huizenMenu);
		kegelsMenu = new MenuBar(true);
		kegelsMenu.addItem("kegel 1", new MenuCommand("kegel1"));
		kegelsMenu.addItem("kegel 2", new MenuCommand("kegel2"));
		kegelsMenu.addItem("kegel 3", new MenuCommand("kegel3"));
		kegelsMenu.addItem("kegel 4", new MenuCommand("kegel4"));
		figurenMenu.addItem("kegels", kegelsMenu);
		figurenMenu.addItem("kubus", new MenuCommand("kubus"));
		piramidesMenu = new MenuBar(true);
		piramidesMenu.addItem("driezijdige piramide", new MenuCommand("piramide3"));
		piramidesMenu.addItem("vierzijdige piramide", new MenuCommand("piramide4"));
		piramidesMenu.addItem("vijfzijdige piramide", new MenuCommand("piramide5"));
		piramidesMenu.addItem("zeszijdige piramide", new MenuCommand("piramide6"));
		piramidesMenu.addItem("zevenzijdige piramide", new MenuCommand("piramide7"));
		piramidesMenu.addItem("achtzijdige piramide", new MenuCommand("piramide8"));
		figurenMenu.addItem("piramides", piramidesMenu);
		prismasMenu = new MenuBar(true);
		prismasMenu.addItem("driezijdig prisma", new MenuCommand("prisma3"));
		prismasMenu.addItem("vierzijdig prisma", new MenuCommand("prisma4"));
		prismasMenu.addItem("vijfzijdig prisma", new MenuCommand("prisma5"));
		prismasMenu.addItem("zeszijdig prisma", new MenuCommand("prisma6"));
		figurenMenu.addItem("prismas", prismasMenu);
		figurenMenu.addItem("twaalfvlak", new MenuCommand("twaalfvlak"));
		figurenMenu.addItem("twintigvlak", new MenuCommand("twintigvlak"));
		figurenMenu.addItem("viervlak", new MenuCommand("viervlak"));
		
		optiesMenu = new MenuBar(true);
		
		geenHulpPuntenItem = new MenuItem("geen hulppunten",new MenuCommand("hp0"));
		eenHulpPuntItem = new MenuItem("een hulppunt",new MenuCommand("hp1"));
		tweeHulpPuntenItem = new MenuItem("twee hulppunten",new MenuCommand("hp2"));
		drieHulpPuntenItem = new MenuItem("drie hulppunten",new MenuCommand("hp3"));
		vierHulpPuntenItem = new MenuItem("vier hulppunten",new MenuCommand("hp4"));
		
		if (hulpPunten)
		{	//geenHulpPuntenItem = new MenuItem("geen hulppunten",new MenuCommand("hulppunten"));
			eenHulpPuntItem.setStyleName(doorzienGWTCss.boldmenuitem());
		}
		else
		{	//hulpPuntenItem = new MenuItem("toon hulppunten",new MenuCommand("hulppunten"));
			geenHulpPuntenItem.setStyleName(doorzienGWTCss.boldmenuitem());
		}

		if (letters)
			lettersItem = new MenuItem("geen letters",new MenuCommand("letters"));
		else
			lettersItem = new MenuItem("toon letters",new MenuCommand("letters"));
		
		centraleProjectieItem = new MenuItem("centrale projectie",new MenuCommand("cprojectie"));
		parallelProjectieItem = new MenuItem("parallelprojectie",new MenuCommand("pprojectie"));
		if (centraleProjectie)
		{
//			centraleProjectieItem = new MenuItem("centrale projectie",new MenuCommand("cprojectie"));
//			parallelProjectieItem = new MenuItem("parallelprojectie",new MenuCommand("pprojectie"));
			centraleProjectieItem.setStyleName(doorzienGWTCss.boldmenuitem());
		}
		else
		{
//			centraleProjectieItem = new MenuItem("centrale projectie",new MenuCommand("cprojectie"));
//			parallelProjectieItem = new MenuItem("parallelprojectie",new MenuCommand("pprojectie"));
			parallelProjectieItem.setStyleName(doorzienGWTCss.boldmenuitem());
		}
		
		
		optiesMenu.addItem(geenHulpPuntenItem);
		optiesMenu.addItem(eenHulpPuntItem);
		optiesMenu.addItem(tweeHulpPuntenItem);
		optiesMenu.addItem(drieHulpPuntenItem);
		optiesMenu.addItem(vierHulpPuntenItem);
		optiesMenu.addSeparator();
		optiesMenu.addItem(lettersItem);
		optiesMenu.addSeparator();
		optiesMenu.addItem(centraleProjectieItem);
		optiesMenu.addItem(parallelProjectieItem);
		
		menuBar.addItem("figuren", figurenMenu);
		menuBar.addItem("opties", optiesMenu);
		
		
	}	
		
	public void resetLetters()
	{
		letters = false; 
		lettersItem.setText("toon letters");
	}
	
	public void resetProjection()
	{
		centraleProjectieItem.setStyleName(doorzienGWTCss.boldmenuitem());
		parallelProjectieItem.setStyleName(doorzienGWTCss.normalmenuitem());
		centraleProjectie = true;
		
	}
	
	public void resetHelpPoints()
	{
		setHulpToNormal(geenHulpPuntenItem);
		geenHulpPuntenItem.setStyleName(doorzienGWTCss.boldmenuitem());
		hulpPunten = false;
		
	}
	
	public void setHulpToNormal(MenuItem mi)
	{
		if (mi != geenHulpPuntenItem)
			geenHulpPuntenItem.setStyleName(doorzienGWTCss.normalmenuitem());
		if (mi != eenHulpPuntItem)
			eenHulpPuntItem.setStyleName(doorzienGWTCss.normalmenuitem());			
		if (mi != tweeHulpPuntenItem)
			tweeHulpPuntenItem.setStyleName(doorzienGWTCss.normalmenuitem());	
		if (mi != drieHulpPuntenItem)
			drieHulpPuntenItem.setStyleName(doorzienGWTCss.normalmenuitem());
		if (mi != vierHulpPuntenItem)
			vierHulpPuntenItem.setStyleName(doorzienGWTCss.normalmenuitem());
	}
	
	public void menuAction(String s)
	{
		if (s.equals("achtvlak"))
		{	drawingPanel.setNewModel(OCTAHEDRON);
		}
		else if (s.equals("balk"))
		{	drawingPanel.setNewModel(BLOCK);
		}
		else if (s.equals("cilinder"))
		{	drawingPanel.setNewModel(CYLINDER);
		}
		else if (s.equals("piramidehuis"))
		{	drawingPanel.setNewModel(PIRHOUSE);
		}
		else if (s.equals("schildhuis"))
		{	drawingPanel.setNewModel(EDGEHOUSE);
		}
		else if (s.equals("kegel1"))
		{	drawingPanel.setNewModel(CONE1);
		}
		else if (s.equals("kegel2"))
		{	drawingPanel.setNewModel(CONE2);
		}
		else if (s.equals("kegel3"))
		{	drawingPanel.setNewModel(CONE3);
		}
		else if (s.equals("kegel4"))
		{	drawingPanel.setNewModel(CONE4);
		}
		else if (s.equals("kubus"))
		{	drawingPanel.setNewModel(CUBE);
		}
		else if (s.equals("piramide3"))
		{	drawingPanel.setNewModel(PIRAMID3);
		}
		else if (s.equals("piramide4"))
		{	drawingPanel.setNewModel(PIRAMID4);
		}
		else if (s.equals("piramide5"))
		{	drawingPanel.setNewModel(PIRAMID5);
		}
		else if (s.equals("piramide6"))
		{	drawingPanel.setNewModel(PIRAMID6);
		}
		else if (s.equals("piramide7"))
		{	drawingPanel.setNewModel(PIRAMID7);
		}
		else if (s.equals("piramide8"))
		{	drawingPanel.setNewModel(PIRAMID8);
		}
		else if (s.equals("prisma3"))
		{	drawingPanel.setNewModel(PRISM3);
		}
		else if (s.equals("prisma4"))
		{	drawingPanel.setNewModel(PRISM4);
		}
		else if (s.equals("prisma5"))
		{	drawingPanel.setNewModel(PRISM5);
		}
		else if (s.equals("prisma6"))
		{	drawingPanel.setNewModel(PRISM6);
		}
		else if (s.equals("twaalfvlak"))
		{	drawingPanel.setNewModel(DODECAHEDRON);
		}
		else if (s.equals("twintigvlak"))
		{	drawingPanel.setNewModel(ICOSAHEDRON);
		}
		else if (s.equals("viervlak"))
		{	drawingPanel.setNewModel(TETRAHEDRON);
		}
		
/*		
		else if (s.equals("hulppunten"))
		{
			if (hulpPuntenItem.getText().equals("toon hulppunten"))
			{
				hulpPuntenItem.setText("geen hulppunten");
				drawingPanel.setHelpPointDrop(true);
			}
			else 
			{
				hulpPuntenItem.setText("toon hulppunten");
				drawingPanel.setHelpPointDrop(false);
				
			}
		}
*/		
		
		else if (s.equals("hp0"))
		{
			setHulpToNormal(geenHulpPuntenItem);
			geenHulpPuntenItem.setStyleName(doorzienGWTCss.boldmenuitem());
			drawingPanel.setHelpPoints(0);
			hulpPunten = false;
		}
		else if (s.equals("hp1"))
		{
			setHulpToNormal(eenHulpPuntItem);
			eenHulpPuntItem.setStyleName(doorzienGWTCss.boldmenuitem());
			drawingPanel.setHelpPoints(1);
			hulpPunten = true;
		}
		else if (s.equals("hp2"))
		{
			setHulpToNormal(tweeHulpPuntenItem);
			tweeHulpPuntenItem.setStyleName(doorzienGWTCss.boldmenuitem());
			drawingPanel.setHelpPoints(2);
			hulpPunten = true;
		}
		else if (s.equals("hp3"))
		{
			setHulpToNormal(drieHulpPuntenItem);
			drieHulpPuntenItem.setStyleName(doorzienGWTCss.boldmenuitem());
			drawingPanel.setHelpPoints(3);
			hulpPunten = true;
		}
		else if (s.equals("hp4"))
		{
			setHulpToNormal(vierHulpPuntenItem);
			vierHulpPuntenItem.setStyleName(doorzienGWTCss.boldmenuitem());
			drawingPanel.setHelpPoints(4);
			hulpPunten = true;
		}


			
		else if (s.equals("letters"))
		{
			if (lettersItem.getText().equals("toon letters"))
			{
				lettersItem.setText("geen letters");
				drawingPanel.setLetters(true);
				letters = true;
				
			}
			else 
			{
				lettersItem.setText("toon letters");
				drawingPanel.setLetters(false);
				letters = false;
				
			}
		}
		else if (s.equals("cprojectie"))
		{
			//if (centraleProjectieItem.getText().equals("- centrale projectie"))
			//{
				//centraleProjectieItem.setText("+ centrale projectie");
				//parallelProjectieItem.setText("- parallelprojectie");
				centraleProjectieItem.setStyleName(doorzienGWTCss.boldmenuitem());
				parallelProjectieItem.setStyleName(doorzienGWTCss.normalmenuitem());
				drawingPanel.setProjection(DrawingPanel2.CENTRALPROJ);
				centraleProjectie = true;
			//}
		}
		else if (s.equals("pprojectie"))
		{
			//if (parallelProjectieItem.getText().equals("- parallelprojectie"))
			//{
				//centraleProjectieItem.setText("- centrale projectie");
				//parallelProjectieItem.setText("+ parallelprojectie");
				centraleProjectieItem.setStyleName(doorzienGWTCss.normalmenuitem());
				parallelProjectieItem.setStyleName(doorzienGWTCss.boldmenuitem());
				drawingPanel.setProjection(DrawingPanel2.PARALLELPROJ);
				centraleProjectie = false;
			//}
		}
		
		
	}
	
	class MenuCommand implements Command
	{
		String cmdString = "";
		
		public MenuCommand(String s)
		{
			cmdString = s;
		}
		public void execute()
		{
			menuAction(cmdString);
		}
	}

	
	public DoorzienGWT()
	{
		//this(null, null, null);
	}
	
	
	
	public DoorzienGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		
//System.out.println("constructor");
		ObjectMap h = JSONUtilities.wrapMap(map);

		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		
		if (h.containsKey("breedte"))
			breedte = h.getInt("breedte");
		if (h.containsKey("hoogte"))
			hoogte = h.getInt("hoogte");
		if (h.containsKey("interactiePanelLaunchState"))
			//launchState = (Map<String, Object>) h.get("interactiePanelLaunchState");
			launchState = h.getMap("interactiePanelLaunchState");
		
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(doorzienGWTCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		//RootPanel.get(holderId).add(dlp);
		//RootPanel.get(holderId).addStyleName(doorzienGWTCss.root());
		
		
		//Stub.publish(this);
		init(breedte, hoogte, launchState, randomVarWaarden);

/*
		if (launchState != null && launchState.get("lijnTekenOptie") != null)
			lijnTekenOptie = (Boolean) launchState.get("lijnTekenOptie");
		if (launchState != null && launchState.get("lijnVerlengOptie") != null)
			lijnVerlengOptie = (Boolean) launchState.get("lijnVerlengOptie");

		if (launchState != null && launchState.get("vlakTekenOptie") != null)
			vlakTekenOptie = (Boolean) launchState.get("vlakTekenOptie");
		if (launchState != null && launchState.get("evenwijdigVlakOptie") != null)
			evenwijdigVlakOptie = (Boolean) launchState.get("evenwijdigVlakOptie");

		if (launchState != null && launchState.get("toonDoorsnedeOptie") != null)
			toonDoorsnedeOptie = (Boolean) launchState.get("toonDoorsnedeOptie");
		if (launchState != null && launchState.get("splitsFiguurOptie") != null)
			splitsFiguurOptie = (Boolean) launchState.get("splitsFiguurOptie");
		
		if (launchState != null && launchState.get("bouwplaatOptie") != null)
			bouwplaatOptie = (Boolean) launchState.get("bouwplaatOptie");
		
		if (launchState != null && launchState.get("previewOptie") != null)
			previewOptie = (Boolean) launchState.get("previewOptie");

		if (launchState != null && launchState.get("designOption") != null)
			designOption = (Boolean) launchState.get("designOption");

		if (launchState != null && launchState.get("resetOption") != null)
			resetOption = (Boolean) launchState.get("resetOption");

		if (launchState != null && launchState.get("letters") != null)	
			letters = ((Boolean) launchState.get("letters"));
		if (launchState != null && launchState.get("hulpPunten") != null)
			hulpPunten = ((Boolean) launchState.get("hulpPunten"));
		if (launchState != null && launchState.get("centraleProjectie") != null)
			centraleProjectie = ((Boolean) launchState.get("centraleProjectie"));
		
		// beginfiguur
		if (launchState != null && launchState.get("origObject") != null)
		{	resetState = launchState;
		}
		
		
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		//dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

//		bottomPanel = new LayoutPanel();
//		bottomPanel.addStyleName(doorzienGWTCss.bottom());
//		dlp.addSouth(bottomPanel, bottomHeight);
		
		canvasPanel = new LayoutPanel();
		dlp.add(canvasPanel);

		
		drawingShell = new DrawingShell(this, breedte, hoogte, CUBE);

		if (drawingShell.drawingPanelCanvas == null) 
		{
	      RootPanel.get(holderId).add(new Label(upgradeMessage));
	      return;
	    }
		
		drawingShell.drawingPanelCanvas.addStyleName(doorzienGWTCss.canvas());
//		dlp.add(drawingShell.drawingPanelCanvas);

		canvasPanel.add(drawingShell.drawingPanelCanvas);
		canvasPanel.setWidgetLeftWidth(drawingShell.drawingPanelCanvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(drawingShell.drawingPanelCanvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		
		drawingShell.slider = new Slider2(drawingShell, 0, 1);
		canvasPanel.add(drawingShell.slider.sliderCanvas);
		canvasPanel.setWidgetLeftWidth(drawingShell.slider.sliderCanvas, breedte - drawingShell.slider.horSize - 1, 
				                       Style.Unit.PX, drawingShell.slider.horSize, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(drawingShell.slider.sliderCanvas, 1, 
				                       Style.Unit.PX, drawingShell.slider.vertSize, Style.Unit.PX);
//		drawingShell.slider.setVisible(false);
		
		makeBottom();
		
		if (resetState != null)
			setState(launchState);
*/		
	}
	

	public void makeBottom()
	{
		if (designOption)
		{	
			toolsButton = new PushButton(toolsImage);
		
//		bottomPanel.add(toolsButton);
//		bottomPanel.setWidgetLeftWidth(toolsButton, leftOffset, Style.Unit.PX, 33, Style.Unit.PX);
//		bottomPanel.setWidgetTopHeight(toolsButton, 0, Style.Unit.PX, 32, Style.Unit.PX);

			canvasPanel.add(toolsButton);
			canvasPanel.setWidgetLeftWidth(toolsButton, leftOffset, Style.Unit.PX, 33, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(toolsButton, hoogte - topOffset - 32, Style.Unit.PX, 32, Style.Unit.PX);
		
			//toolsButton.addMouseDownHandler(new PushMouseDownHandler());
			toolsButton.addClickHandler(new PushClickHandler());
		//twmi.addTouchStartHandler(toolsButton, new PushTouchStartHandler());
			//toolsButton.addTouchStartHandler(new PushTouchStartHandler());
		
		}
		
		if (resetOption)
		{
			resetButton = new PushButton(resetImage);
			
			canvasPanel.add(resetButton);
			canvasPanel.setWidgetLeftWidth(resetButton, breedte - leftOffset - 32, Style.Unit.PX, 32, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(resetButton, hoogte - topOffset - 32, Style.Unit.PX, 32, Style.Unit.PX);
		
			//resetButton.addMouseDownHandler(new PushMouseDownHandler());
			resetButton.addClickHandler(new PushClickHandler());
		//twmi.addTouchStartHandler(resetButton, new PushTouchStartHandler());
			//resetButton.addTouchStartHandler(new PushTouchStartHandler());			
		}
	}
	
	
    public void figureToViewer()
    {
    	Map<String,Object> h = getPopupState();
    	setViewerState(h);
    	
    	if (doorzienGWTPopup != null)
    		doorzienGWTPopup.setVisible(false);
    	doorzienGWTPopup = null;
    	popupVisible = false;
    }
    
    public void figureToPopup()
    {
    	popupVisible = true;
    	
    	Map<String,Object> h = getViewerState();
    	setPopupState(h);
    	
    }

    
    public void setViewerState(Map<String,Object> map)
    {
    	
    	ObjectMap h = JSONUtilities.wrapMap(map);
    	
//		boolean letters = false;
//		boolean hulpPunten = false;
//		boolean centraleProjectie = true;

//		if (b.containsKey("letters"))
//			letters = ((Boolean) b.get("letters")).booleanValue();
//		if (b.containsKey("hulpPunten"))
//			hulpPunten = ((Boolean) b.get("hulpPunten")).booleanValue();
//		if (b.containsKey("centraleProjectie"))
//			centraleProjectie = ((Boolean) b.get("centraleProjectie")).booleanValue();

		// true voegt de helpPointDrop toe maar laat
		// die niet zien
//		if (hulpPunten)
//			drawingPanel.setHelpPointDrop(true);		    
//		else
//			drawingPanel.setHelpPointDrop(false);
//		helpPuntenItem.setSelected(hulpPunten);
		
//		drawingPanel.setLetters(letters);

//		lettersItem.setSelected(letters);

//		if (centraleProjectie)
//		{	drawingPanel.setProjection(DrawingPanel.CENTRALPROJ);
//			centraleProjectieItem.setSelected(true);
//		}
//		else
//		{	drawingPanel.setProjection(DrawingPanel.PARALLELPROJ);
//			parallelProjectieItem.setSelected(true);
//		}

		int figuurCode = CUBE;
		if (h.containsKey("figuurCode"))
			figuurCode = h.getInt("figuurCode");
		
//		selectItem(figuurCode);
		
		
		int numLines = 0;
		int numPlanes = 0;
		boolean filled = false;
		boolean planesFilled = false;

		if (h.containsKey("numLines"))
			numLines = h.getInt("numLines");
		if (h.containsKey("numPlanes"))
			numPlanes = h.getInt("numPlanes");
		if (h.containsKey("filled"))
			filled = h.getBoolean("filled");
		if (h.containsKey("planesFilled"))
			planesFilled = h.getBoolean("planesFilled");
		

		drawingShell.setNumLines(numLines);
		drawingShell.setNumPlanes(numPlanes);

//		if (filled)
//		{	rightToolBar.wireSolidButton.setDown(true);
//		}	
//		if (planesFilled)
//		{	topToolBar.planesFilledButton.setDown(true);
//		}
		
		double lengthFactor = 0;
		if (h.containsKey("lengthFactor"))
			lengthFactor = h.getDouble("lengthFactor");

		DrawConstants.llFactor = lengthFactor;
		
		// dit betekent dat er zeker lijnen zijn!
//		if (lengthFactor > 0)
//		{	topToolBar.shortLinesButton.setEnabled(true);
//            if (lengthFactor >= (drawingPanel.MAXLLFACTOR - drawingPanel.LLSTEP / 10))
//                topToolBar.lengLinesButton.setEnabled(false);    
//		}
		
		Matrix3D mat = new Matrix3D();
		//double[] coeff = new double[9];
		List<Double> coeff = new ArrayList<Double>(); 
		int paintType = Object3DContainer.PUREZ;
		double zoomFactor = 9e-1d;
		boolean showInside = true;
		
		if (h.containsKey("matrix3D"))
			coeff = h.getDoubleList("matrix3D");
		mat = NoSer.setMatrix3DState(coeff);		
		if (h.containsKey("paintType"))
			paintType = h.getInt("paintType");
		if (h.containsKey("zoomFactor"))
			zoomFactor = h.getDouble("zoomFactor");
		if (h.containsKey("showInside"))
			showInside = h.getBoolean("showInside");
		
		// moet dit VOOR of NA het creeeren van het Object3D?
		drawingShell.panel3D.mat = mat;
		drawingShell.panel3D.mat.setOrigin(
				drawingShell.panel3D.breedte / 2,
				drawingShell.panel3D.hoogte / 2,
				0);
		drawingShell.panel3D.paintType = paintType;					
		drawingShell.panel3D.showInside = showInside;
		
		drawingShell.panel3D.setZoomFactor(zoomFactor);					
		
		drawingShell.zoom = zoomFactor;
        
//		if (drawingPanel.zoom <= (drawingPanel.MINZOOM + drawingPanel.ZOOMSTEP / 10))
//        {	rightToolBar.zoomOutButton.setEnabled(false);    
//        }
//        if (drawingPanel.zoom >= (drawingPanel.MAXZOOM - drawingPanel.ZOOMSTEP / 10))
//        {   rightToolBar.zoomInButton.setEnabled(false);    
//		}
		
        int mode = drawingShell.INERT;
        if (h.containsKey("mode"))
        {	mode = h.getInt("mode");
//System.out.println("contains mode");        
        }
        
        drawingShell.mouseMode = mode;
        
//System.out.println("dp mode = " + mode);        
        
		Map<String,Object> origObject = new HashMap<String,Object>();
		//Vector conState = new Vector();
		Object conStateOb = null;
		List<Object> conStateList = new ArrayList<Object>();
		
		if (h.containsKey("origObject"))
		{	origObject = h.getMap("origObject");
		}
		if (h.containsKey("conState"))
		{	//conStateOb = h.get("conState");
			conStateList = h.getList("conState");
		}
/*		
		if (conStateOb instanceof Vector)
		{
			conState = (Vector) conStateOb;
		}
		else if (conStateOb instanceof ArrayList)
		{
			ArrayList conStateArr = (ArrayList) conStateOb;
			for (int c = 0; c < conStateArr.size(); c++)
			{
				Object o = conStateArr.get(c);
				conState.addElement(o);
			}
		}
*/
		
		Object3D originalObject = NoSer.setObject3DState(origObject);
		Vector construction = NoSer.setConstructionState(conStateList);
		
//drawingShell.panel3D.testString2 = "pv = " + popupVisible + " nvob = " + originalObject.numVertices;		
		
		drawingShell.currentObjectGroup = drawingShell.rebuild(originalObject, construction, null);
		drawingShell.originalObject = drawingShell.currentObjectGroup.leftMostLeaf();

		if (drawingShell.mouseMode == drawingShell.FOLDOUT)
		{
			// toestand originele object
			boolean oldFilled = false;
			Matrix3D oldPos = new Matrix3D();
			//double[] oldCoeff = new double[9];
			List<Double> oldCoeff = new ArrayList<Double>();
			if (h.containsKey("oldFilled"))
				oldFilled = h.getBoolean("oldFilled");
			if (h.containsKey("oldPos"))
				oldCoeff = h.getDoubleList("oldPos");
			oldPos = NoSer.setMatrix3DState(oldCoeff);
			
			// toestand fold out
			boolean flattened = false;
			double angle = 2e-1d;
			if (h.containsKey("flattened"))
				flattened = h.getBoolean("flattened");
			if (h.containsKey("angle"))
				angle = h.getDouble("angle");
			drawingShell.flattened = flattened;
			drawingShell.currentFoldOut = angle;
			
//System.out.println("set angle = " + angle);			
			
			Facet3D startFacet = null;
			//double[] vertices = new double[0];;
			List<Double> vertices = new ArrayList<Double>(); 
			if (h.containsKey("startFacet"))
			{	vertices = h.getDoubleList("startFacet");
//System.out.println("contains sf");			
			}
	
			startFacet = NoSer.setFacet3DVertexState(vertices);

			if (startFacet != null)
			{	
				drawingShell.startFacet = startFacet; 

//System.out.println("sf != null");						
				
				drawingShell.makeFoldOut(0, true);
				
				drawingShell.processSlider(angle);
				
				if (flattened)
					drawingShell.flattenAction();
				
			}
			else
			{
//System.out.println("sf == null");				
			}
			
		}
		else if (drawingShell.mouseMode == drawingShell.CUTOBJECT)
		{
			// toestand originele object
			boolean oldPlanesFilled = false;
			if (h.containsKey("oldPlanesFilled"))
				oldPlanesFilled = h.getBoolean("oldPlanesFilled");
			drawingShell.oldPlanesFilled = oldPlanesFilled;

			// toestand cut object
			String volumeString = "";
			if (h.containsKey("volumeString"))
				volumeString = h.getString("volumeString");
			drawingShell.panel3D.testString = volumeString;
			
			
			Plane3D planeChoosen = new Plane3D(1, 0, 0, 0);
			//double[] planeChoosenCoeff = new double[9];
			List<Double> planeChoosenCoeff = new ArrayList<Double>(); 
			if (h.containsKey("planeChoosen"))
				planeChoosenCoeff = h.getDoubleList("planeChoosen");
			planeChoosen = NoSer.setPlane3DState(planeChoosenCoeff);
			drawingShell.planeChoosen = planeChoosen; 
			
			drawingShell.figureCut = true;
			drawingShell.cutObject(1, true);
			
		}
		else // geen bouwplaat of versneden object
		{
/*			
			if (drawingPanel != null)
				drawingShell.panel3D.initializeModel(drawingPanel.currentObjectGroup, false); 
				// FIXME Als er geen popup is, dan NPE
			else // FIXME WAT MOET HIER STAAN HUUB?
				drawingShell.panel3D.initializeModel(drawingShell.makeNewModel(drawingShell.modelCode), true);
*/				 
			drawingShell.panel3D.initializeModel(drawingShell.currentObjectGroup, false);
			drawingShell.slider.setVisible(false);
			drawingShell.flatButton.setVisible(false);
		}

		// dit moet NA het creeeren van het Object3D
		drawingShell.setFilled(filled);
		drawingShell.fillPlanes(planesFilled);
		
		drawingShell.addToHistory();
		
//		if (drawingPanel.mouseMode != drawingPanel.INERT)
//		{
//			rightToolBar.undoButton.setEnabled(false);
//		}
		
			
		drawingShell.panel3D.repaint();


    } // setViewerState
    
    public Map<String,Object> getViewerState()
    {
    	Map<String,Object> h = new HashMap<String,Object>();
    	
		// de leerling KAN deze veranderd hebben
//		boolean letters = this.letters;
//		boolean hulpPunten = this.hulpPunten;
//		boolean centraleProjectie = this.centraleProjectie;
		
//		h.put("letters", new Boolean(letters));
//		h.put("hulpPunten", new Boolean(hulpPunten));
//		h.put("centraleProjectie", new Boolean(centraleProjectie));
		
		// status van de knoppen/het object
		int numLines = drawingShell.numLines;
		int numPlanes = drawingShell.numPlanes;
		boolean filled = drawingShell.filled;
		boolean planesFilled = drawingShell.planesFilled;
		
		h.put("numLines", new Integer(numLines));
		h.put("numPlanes", new Integer(numPlanes));
		h.put("filled", new Boolean(filled));
		h.put("planesFilled", new Boolean(planesFilled));
		
		int figuurCode = drawingShell.modelCode;
		if ((drawingShell.numLines > 0) || (drawingShell.numPlanes > 0))
			figuurCode = MYFIGURE;
		h.put("figuurCode", new Integer(figuurCode));
		
		double lengthFactor = DrawConstants.llFactor;
		h.put("lengthFactor", new Double(lengthFactor));
		
		// drawingPanel.panel3D items
		Matrix3D mat = drawingShell.panel3D.mat;
		int paintType = drawingShell.panel3D.paintType;
		double zoomFactor = drawingShell.panel3D.zoomFactor;
		boolean showInside = drawingShell.panel3D.showInside;
		
		//double[] coeff = NoSer.getMatrix3DState(mat);
		List<Double> coeff = NoSer.getMatrix3DState(mat);
		h.put("matrix3D", coeff);
		h.put("paintType", new Integer(paintType));
		h.put("zoomFactor", new Double(zoomFactor));
		h.put("showInside", new Boolean(showInside));
		
		HashMap<String,Object> origObject = NoSer.getObject3DState(drawingShell.originalObject);

		Vector construction = new Vector();
        if (drawingShell.currentObjectGroup instanceof ObjectWithLine)
            construction = ((ObjectWithLine) drawingShell.currentObjectGroup).getConstruction();
        else if (drawingShell.currentObjectGroup instanceof ObjectWithPlane)
            construction = ((ObjectWithPlane) drawingShell.currentObjectGroup).getConstruction();
        
        //Vector conState = NoSer.getConstructionState(construction);
        List<Object> conState = NoSer.getConstructionState(construction);
		
		h.put("origObject", origObject);
		h.put("conState", conState);
		
		int mode = drawingShell.INERT;
		
		if ((drawingShell.mouseMode == drawingShell.FOLDOUT) && 
			(drawingShell.startFacet != null))
		{
			mode = drawingShell.FOLDOUT;
			
			// toestand originele object
			boolean oldFilled = drawingShell.oldFilled;
			Matrix3D oldPos = drawingShell.oldPos;
			//double[] oldCoeff = NoSer.getMatrix3DState(oldPos);
			List<Double> oldCoeff = NoSer.getMatrix3DState(oldPos);
			h.put("oldFilled", new Boolean(oldFilled));
			h.put("oldPos", oldCoeff);
			
			// toestand fold out
			boolean flattened = drawingShell.flattened;
			double angle = drawingShell.currentFoldOut;
			h.put("flattened", new Boolean(flattened));
			h.put("angle", new Double(angle));
			
//System.out.println("get angle = " + angle);

			Facet3D theStartFacet = drawingShell.startFacet;
			//double[] startFacet = NoSer.getFacet3DVertexState(theStartFacet);
			List<Double> startFacet = NoSer.getFacet3DVertexState(theStartFacet);
			h.put("startFacet", startFacet);
			
			//scormedObject3D.theFoldOutGroup = dp.foldOutObjectGroup;			
			//scormedObject3D.theFoldOutTreeRoot = dp.foldOutTreeRoot;
		}
		if ((drawingShell.mouseMode == drawingShell.CUTOBJECT) && 
			(drawingShell.planeChoosen != null))
		{
			mode = drawingShell.CUTOBJECT;
			// toestand originele object
			boolean oldPlanesFilled = drawingShell.oldPlanesFilled;
			h.put("oldPlanesFilled", new Boolean("oldPlanesFilled"));
			// toestand cut object
			String volumeString = drawingShell.panel3D.testString;
			h.put("volumeString", volumeString);
			
			Plane3D thePlaneChoosen = drawingShell.planeChoosen;
			//double[] planeChoosen = NoSer.getPlane3DState(thePlaneChoosen);
			List<Double> planeChoosen = NoSer.getPlane3DState(thePlaneChoosen);
			h.put("planeChoosen", planeChoosen);
			
			//scormedObject3D.theCutObjectGroup = dp.cutObjectGroup;
		}
		
		h.put("mode", new Integer(mode));
    	
    	
    	return h;
    	
    	
    }

    public void setPopupState(Map<String,Object> map)
    {
//		boolean letters = false;
//		boolean hulpPunten = false;
//		boolean centraleProjectie = true;

//		if (b.containsKey("letters"))
//			letters = ((Boolean) b.get("letters")).booleanValue();
//		if (b.containsKey("hulpPunten"))
//			hulpPunten = ((Boolean) b.get("hulpPunten")).booleanValue();
//		if (b.containsKey("centraleProjectie"))
//			centraleProjectie = ((Boolean) b.get("centraleProjectie")).booleanValue();

		// true voegt de helpPointDrop toe maar laat
		// die niet zien
    	
    	ObjectMap h = JSONUtilities.wrapMap(map);
    	
		if (hulpPunten)
		{	drawingPanel.setHelpPointDrop(true);
			eenHulpPuntItem.setStyleName(doorzienGWTCss.boldmenuitem());
		}
		else
		{	drawingPanel.setHelpPointDrop(false);
			geenHulpPuntenItem.setStyleName(doorzienGWTCss.boldmenuitem());
    	}


    	if (letters)
		{
			lettersItem.setText("geen letters");
			drawingPanel.setLetters(true);
		}
		else 
		{
			lettersItem.setText("toon letters");
			drawingPanel.setLetters(false);
		}

		if (centraleProjectie)
		{	drawingPanel.setProjection(DrawingPanel2.CENTRALPROJ);
			//centraleProjectieItem.setSelected(true);
			centraleProjectieItem.setStyleName(doorzienGWTCss.boldmenuitem());
			parallelProjectieItem.setStyleName(doorzienGWTCss.normalmenuitem());
		}
		else
		{	drawingPanel.setProjection(DrawingPanel2.PARALLELPROJ);
			//parallelProjectieItem.setSelected(true);
			centraleProjectieItem.setStyleName(doorzienGWTCss.normalmenuitem());
			parallelProjectieItem.setStyleName(doorzienGWTCss.boldmenuitem());
		}

		int figuurCode = CUBE;
		if (h.containsKey("figuurCode"))
			figuurCode = h.getInt("figuurCode");
		
//		selectItem(figuurCode);
		
		
		int numLines = 0;
		int numPlanes = 0;
		boolean filled = false;
		boolean planesFilled = false;

		if (h.containsKey("numLines"))
			numLines = h.getInt("numLines");
		if (h.containsKey("numPlanes"))
			numPlanes = h.getInt("numPlanes");
		if (h.containsKey("filled"))
			filled = h.getBoolean("filled");
		if (h.containsKey("planesFilled"))
			planesFilled = h.getBoolean("planesFilled");
		
		// dit enabled/disabled de lijn knoppen
		drawingPanel.setNumLines(numLines);
		// dit enabled/disabled de vlak knoppen
		drawingPanel.setNumPlanes(numPlanes);
		if (filled)
		{	rightToolBar.wireSolidButton.setDown(true);
			//rightToolBar.wireSolidButton.setImage(getImage("wireframe.gif"));
		}	
		if (planesFilled)
		{	//topToolBar.planesFilledButton.setImage(getImage("planesempty.gif"));
			topToolBar.planesFilledButton.setDown(true);
		}
		
		double lengthFactor = 0;
		if (h.containsKey("lengthFactor"))
			lengthFactor = h.getDouble("lengthFactor");

		DrawConstants.llFactor = lengthFactor;
		// dit betekent dat er zeker lijnen zijn!
		if (lengthFactor > 0)
		{	//topToolBar.shortLinesButton.setImage(getImage("shortLines.gif"));		
			topToolBar.shortLinesButton.setEnabled(true);
            if (lengthFactor >= (drawingPanel.MAXLLFACTOR - drawingPanel.LLSTEP / 10))
                topToolBar.lengLinesButton.setEnabled(false);    
		}
		
		Matrix3D mat = new Matrix3D();
		//double[] coeff = new double[9];
		List<Double> coeff = new ArrayList<Double>(); 
		int paintType = Object3DContainer.PUREZ;
		double zoomFactor = 9e-1d;
		boolean showInside = true;
		
		if (h.containsKey("matrix3D"))
			coeff = h.getDoubleList("matrix3D");
		mat = NoSer.setMatrix3DState(coeff);		
		if (h.containsKey("paintType"))
			paintType = h.getInt("paintType");
		if (h.containsKey("zoomFactor"))
			zoomFactor = h.getDouble("zoomFactor");
		if (h.containsKey("showInside"))
			showInside = h.getBoolean("showInside");
		
		// moet dit VOOR of NA het creeeren van het Object3D?
		drawingPanel.panel3D.mat = mat;
		drawingPanel.panel3D.mat.setOrigin(
				drawingPanel.panel3D.breedte / 2,
				drawingPanel.panel3D.hoogte / 2,
				0);
		drawingPanel.panel3D.paintType = paintType;					
		drawingPanel.panel3D.showInside = showInside;
		
		drawingPanel.panel3D.setZoomFactor(zoomFactor);					
		
		drawingPanel.zoom = zoomFactor;
        if (drawingPanel.zoom <= (drawingPanel.MINZOOM + drawingPanel.ZOOMSTEP / 10))
        {	rightToolBar.zoomOutButton.setEnabled(false);    
        }
        if (drawingPanel.zoom >= (drawingPanel.MAXZOOM - drawingPanel.ZOOMSTEP / 10))
        {   rightToolBar.zoomInButton.setEnabled(false);    
		}
		
        int mode = drawingPanel.INERT;
        if (h.containsKey("mode"))
        {	mode = h.getInt("mode");
//System.out.println("contains mode");        
        }
        
        drawingPanel.mouseMode = mode;
        
//System.out.println("dp mode = " + mode);        
        
		Map<String,Object> origObject = new HashMap<String,Object>();
		//Vector conState = new Vector();
		Object conStateOb = null;
		List<Object> conStateList = new ArrayList<Object>(); 
		
		if (h.containsKey("origObject"))
		{	origObject = h.getMap("origObject");
		}
		if (h.containsKey("conState"))
		{	//conState = (Vector) h.get("conState");
			//conStateOb = h.getObject("conState");
			conStateList = h.getList("conState");
		}
/*		
		if (conStateOb instanceof Vector)
		{
			conState = (Vector) conStateOb;
		}
		else if (conStateOb instanceof ArrayList)
		{
			ArrayList conStateArr = (ArrayList) conStateOb;
			for (int c = 0; c < conStateArr.size(); c++)
			{
				Object o = conStateArr.get(c);
				conState.addElement(o);
			}
		}
*/		
		
		Object3D originalObject = NoSer.setObject3DState(origObject);
		Vector construction = NoSer.setConstructionState(conStateList);
		
		drawingPanel.currentObjectGroup = drawingPanel.rebuild(originalObject, construction, null);
		drawingPanel.originalObject = drawingPanel.currentObjectGroup.leftMostLeaf();

		if (drawingPanel.mouseMode == drawingPanel.FOLDOUT)
		{
			// toestand originele object
			boolean oldFilled = false;
			Matrix3D oldPos = new Matrix3D();
			//double[] oldCoeff = new double[9];
			List<Double> oldCoeff = new ArrayList<Double>();
			if (h.containsKey("oldFilled"))
				oldFilled = h.getBoolean("oldFilled");
			if (h.containsKey("oldPos"))
				oldCoeff = h.getDoubleList("oldPos");
			oldPos = NoSer.setMatrix3DState(oldCoeff);
			
			// toestand fold out
			boolean flattened = false;
			double angle = 2e-1d;
			if (h.containsKey("flattened"))
				flattened = h.getBoolean("flattened");
			if (h.containsKey("angle"))
				angle = h.getDouble("angle");
			drawingPanel.flattened = flattened;
			drawingPanel.currentFoldOut = angle;
			
//System.out.println("set angle = " + angle);			
			
			Facet3D startFacet = null;
			//double[] vertices = new double[0];;
			List<Double> vertices = new ArrayList<Double>(); 
			if (h.containsKey("startFacet"))
			{	vertices = h.getDoubleList("startFacet");
//System.out.println("contains sf");			
			}
	
			startFacet = NoSer.setFacet3DVertexState(vertices);

			if (startFacet != null)
			{	
				drawingPanel.startFacet = startFacet; 

//System.out.println("sf != null");						
				
				drawingPanel.makeFoldOut(0, true);
				
				drawingPanel.processSlider(angle);
				
				if (flattened)
					drawingPanel.flattenAction();
				
				rightToolBar.conDrawButton.setDown(true);
				
			}
			else
			{
//System.out.println("sf == null");				
			}
			
		}
		else if (drawingPanel.mouseMode == drawingPanel.CUTOBJECT)
		{
			// toestand originele object
			boolean oldPlanesFilled = false;
			if (h.containsKey("oldPlanesFilled"))
				oldPlanesFilled = h.getBoolean("oldPlanesFilled");
			drawingPanel.oldPlanesFilled = oldPlanesFilled;

			// toestand cut object
			String volumeString = "";
			if (h.containsKey("volumeString"))
				volumeString = h.getString("volumeString");
			drawingPanel.panel3D.testString = volumeString;
			
			
			Plane3D planeChoosen = new Plane3D(1, 0, 0, 0);
			//double[] planeChoosenCoeff = new double[9];
			List<Double> planeChoosenCoeff = new ArrayList<Double>(); 
			if (h.containsKey("planeChoosen"))
				planeChoosenCoeff = h.getDoubleList("planeChoosen");
			planeChoosen = NoSer.setPlane3DState(planeChoosenCoeff);
			drawingPanel.planeChoosen = planeChoosen; 
			
			drawingPanel.figureCut = true;
			drawingPanel.cutObject(1, true);
			
			topToolBar.cutButton.setDown(true);
			
		}
		else
		{	drawingPanel.panel3D.initializeModel(drawingPanel.currentObjectGroup, false);
		
		}

		// dit moet NA het creeeren van het Object3D
		drawingPanel.setFilled(filled);
		drawingPanel.fillPlanes(planesFilled);
		
		drawingPanel.addToHistory();
		
		if (drawingPanel.mouseMode != drawingPanel.INERT)
		{
			rightToolBar.undoButton.setEnabled(false);
		}
		
			
		drawingPanel.panel3D.repaint();

    } // setPopupState
    
    public Map<String,Object> getPopupState()
    {
    	Map<String,Object> h = new HashMap<String,Object>();
    	
		// de leerling KAN deze veranderd hebben
//		boolean letters = this.letters;
//		boolean hulpPunten = this.hulpPunten;
//		boolean centraleProjectie = this.centraleProjectie;
		
//		h.put("letters", new Boolean(letters));
//		h.put("hulpPunten", new Boolean(hulpPunten));
//		h.put("centraleProjectie", new Boolean(centraleProjectie));
		
		// status van de knoppen/het object
		int numLines = drawingPanel.numLines;
		int numPlanes = drawingPanel.numPlanes;
		boolean filled = drawingPanel.filled;
		boolean planesFilled = drawingPanel.planesFilled;
		
		h.put("numLines", new Integer(numLines));
		h.put("numPlanes", new Integer(numPlanes));
		h.put("filled", new Boolean(filled));
		h.put("planesFilled", new Boolean(planesFilled));
		
		int figuurCode = drawingPanel.modelCode;
		if ((drawingPanel.numLines > 0) || (drawingPanel.numPlanes > 0))
			figuurCode = MYFIGURE;
		h.put("figuurCode", new Integer(figuurCode));
		
		double lengthFactor = DrawConstants.llFactor;
		h.put("lengthFactor", new Double(lengthFactor));
		
		// drawingPanel.panel3D items
		Matrix3D mat = drawingPanel.panel3D.mat;
		int paintType = drawingPanel.panel3D.paintType;
		double zoomFactor = drawingPanel.panel3D.zoomFactor;
		boolean showInside = drawingPanel.panel3D.showInside;
		
		//double[] coeff = NoSer.getMatrix3DState(mat);
		List<Double> coeff = NoSer.getMatrix3DState(mat);
		h.put("matrix3D", coeff);
		h.put("paintType", new Integer(paintType));
		h.put("zoomFactor", new Double(zoomFactor));
		h.put("showInside", new Boolean(showInside));
		
		HashMap<String,Object> origObject = NoSer.getObject3DState(drawingPanel.originalObject);

		Vector construction = new Vector();
        if (drawingPanel.currentObjectGroup instanceof ObjectWithLine)
            construction = ((ObjectWithLine) drawingPanel.currentObjectGroup).getConstruction();
        else if (drawingPanel.currentObjectGroup instanceof ObjectWithPlane)
            construction = ((ObjectWithPlane) drawingPanel.currentObjectGroup).getConstruction();
        
        //Vector conState = NoSer.getConstructionState(construction);
        List<Object> conState = NoSer.getConstructionState(construction);
		
		h.put("origObject", origObject);
		h.put("conState", conState);
		
		int mode = drawingPanel.INERT;
		
		if ((drawingPanel.mouseMode == drawingPanel.FOLDOUT) && 
			(drawingPanel.startFacet != null))
		{
			mode = drawingPanel.FOLDOUT;
			
			// toestand originele object
			boolean oldFilled = drawingPanel.oldFilled;
			Matrix3D oldPos = drawingPanel.oldPos;
			//double[] oldCoeff = NoSer.getMatrix3DState(oldPos);
			List<Double> oldCoeff = NoSer.getMatrix3DState(oldPos);
			h.put("oldFilled", new Boolean(oldFilled));
			h.put("oldPos", oldCoeff);
			
			// toestand fold out
			boolean flattened = drawingPanel.flattened;
			double angle = drawingPanel.currentFoldOut;
			h.put("flattened", new Boolean(flattened));
			h.put("angle", new Double(angle));
			
//System.out.println("get angle = " + angle);

			Facet3D theStartFacet = drawingPanel.startFacet;
			//double[] startFacet = NoSer.getFacet3DVertexState(theStartFacet);
			List<Double> startFacet = NoSer.getFacet3DVertexState(theStartFacet);
			h.put("startFacet", startFacet);
			
			//scormedObject3D.theFoldOutGroup = dp.foldOutObjectGroup;			
			//scormedObject3D.theFoldOutTreeRoot = dp.foldOutTreeRoot;
		}
		if ((drawingPanel.mouseMode == drawingPanel.CUTOBJECT) && 
			(drawingPanel.planeChoosen != null))
		{
			mode = drawingPanel.CUTOBJECT;
			// toestand originele object
			boolean oldPlanesFilled = drawingPanel.oldPlanesFilled;
			h.put("oldPlanesFilled", new Boolean("oldPlanesFilled"));
			// toestand cut object
			String volumeString = drawingPanel.panel3D.testString;
			h.put("volumeString", volumeString);
			
			Plane3D thePlaneChoosen = drawingPanel.planeChoosen;
			//double[] planeChoosen = NoSer.getPlane3DState(thePlaneChoosen);
			List<Double> planeChoosen = NoSer.getPlane3DState(thePlaneChoosen);
			h.put("planeChoosen", planeChoosen);
			
			//scormedObject3D.theCutObjectGroup = dp.cutObjectGroup;
		}
		
		h.put("mode", new Integer(mode));
    	
    	
    	return h;
    }
    
     
    //class PushMouseDownHandler implements MouseDownHandler
    class PushClickHandler implements ClickHandler
	{
	   	//public void onMouseDown(MouseDownEvent e)
    	public void onClick(ClickEvent e)
		{   
	   		if (touchStart)
	   			return;
	   		
	   		//e.preventDefault();
	   		e.stopPropagation();
	    	
	   		if (e.getSource() == toolsButton)
	   		{
	   			makePopup();
	   		}
	   		else if (e.getSource() == resetButton)
	   		{
	   			if (resetState != null)
	   				setViewerState(resetState);
	   			else
	   				drawingShell.setNewModel(CUBE);
	   		}
	   		else if (e.getSource() == drawingShell.flatButton)
	   		{
	   			drawingShell.flattened = true;
	   			drawingShell.flattenAction();
	   		}
	    		
	    		
		}
	}	

/*    
    class PushTouchStartHandler implements TouchStartHandler
	{
	   	public void onTouchStart(TouchStartEvent e)
		{   	
	   		touchStart = true;
	   		
	   		//e.preventDefault();
	   		e.stopPropagation();
	    	
	   		if (e.getSource() == toolsButton)
	   		{
	   			makePopup();
	   		}
	    		
	   		else if (e.getSource() == resetButton)
	   		{
	   			if (resetState != null)
	   				setViewerState(resetState);
	   			else
	   				drawingShell.setNewModel(CUBE);
	   		}
	    		
		}
	}	
*/    
	public Widget asWidget()
	{
		return dlp;
	}
	
	@Override
	public HashMap<String, Object> getState()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("popupVisible", new Boolean(popupVisible));
		
		if (popupVisible)
		{	//h.put("state", getPopupState());
			figureToViewer();
		
		}
		//else
		//{	
			h.put("state", getViewerState());
		
		//}
		
		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		//boolean popupVisible = false;
		if (h.containsKey("popupVisible"))
			popupVisible = ((Boolean) h.get("popupVisible")).booleanValue();
		
		HashMap<String,Object> state = new HashMap<String,Object>();
		if (h.containsKey("state"))
			state = (HashMap<String,Object>) h.get("state");
		else
			state = h;
		
		
		//this.popupVisible = popupVisible;
		
		if (popupVisible)
		{	makePopup();
			setPopupState(state);
		}
		else
		{	setViewerState(state);
		}
		 
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
	
	@Override
	public void init(int width, int height, Map<String, Object> map, //launchState, 
			    Map<String, Number> values) 
	{
		this.breedte = width;
		this.hoogte = height;
		//this.launchState = launchState;
		ObjectMap launchState = JSONUtilities.wrapMap(map);
		
		if (launchState.containsKey("lijnTekenOptie"))
			lijnTekenOptie = launchState.getBoolean("lijnTekenOptie");
		if (launchState.containsKey("lijnVerlengOptie"))
			lijnVerlengOptie = launchState.getBoolean("lijnVerlengOptie");

		if (launchState.containsKey("vlakTekenOptie"))
			vlakTekenOptie = launchState.getBoolean("vlakTekenOptie");
		if (launchState.containsKey("evenwijdigVlakOptie"))
			evenwijdigVlakOptie = launchState.getBoolean("evenwijdigVlakOptie");

		if (launchState.containsKey("toonDoorsnedeOptie"))
			toonDoorsnedeOptie = launchState.getBoolean("toonDoorsnedeOptie");
		if (launchState.containsKey("splitsFiguurOptie"))
			splitsFiguurOptie = launchState.getBoolean("splitsFiguurOptie");
		
		if (launchState.containsKey("bouwplaatOptie"))
			bouwplaatOptie = launchState.getBoolean("bouwplaatOptie");
		
		if (launchState.containsKey("previewOptie"))
			previewOptie = launchState.getBoolean("previewOptie");

		if (launchState.containsKey("designOption"))
			designOption = launchState.getBoolean("designOption");

		if (launchState.containsKey("resetOption"))
			resetOption = launchState.getBoolean("resetOption");

		if (launchState.containsKey("demo"))
			popupVisible = !(launchState.getBoolean("demo"));

		
		if (launchState.containsKey("letters"))	
			letters = launchState.getBoolean("letters");
		if (launchState.containsKey("hulpPunten"))
			hulpPunten = launchState.getBoolean("hulpPunten");
		if (launchState.containsKey("centraleProjectie"))
			centraleProjectie = launchState.getBoolean("centraleProjectie");
		
		//Object origObject = launchState.get("origObject");
		
		// beginfiguur
		if (launchState != null && launchState.getMap("origObject") != null)
		{	resetState = mapToHashMap(map); //launchState;
			 
		}
		
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		
		canvasPanel = new LayoutPanel();
		dlp.add(canvasPanel);
		
		drawingShell = new DrawingShell(this, breedte, hoogte, CUBE);

		if (drawingShell.drawingPanelCanvas == null) 
		{
	      RootPanel.get(holderId).add(new Label(upgradeMessage));
	      return;
	    }
		
		drawingShell.drawingPanelCanvas.addStyleName(doorzienGWTCss.canvas());
		
		canvasPanel.add(drawingShell.drawingPanelCanvas);
		canvasPanel.setWidgetLeftWidth(drawingShell.drawingPanelCanvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(drawingShell.drawingPanelCanvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		
		      
		drawingShell.slider = new Slider2(drawingShell, 0, 1);
		canvasPanel.add(drawingShell.slider.sliderCanvas);
		canvasPanel.setWidgetLeftWidth(drawingShell.slider.sliderCanvas, breedte - Slider2.horSize - 1, 
				                       Style.Unit.PX, Slider2.horSize, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(drawingShell.slider.sliderCanvas, 1, 
				                       Style.Unit.PX, Slider2.vertSize, Style.Unit.PX);
		drawingShell.slider.setVisible(false);
		
		drawingShell.flatButton = new PushButton("plat");
		canvasPanel.add(drawingShell.flatButton);
		canvasPanel.setWidgetLeftWidth(drawingShell.flatButton, breedte - 40 - 1, Style.Unit.PX, 40, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(drawingShell.flatButton, 1 + Slider2.vertSize, Style.Unit.PX, 22, Style.Unit.PX);
		drawingShell.flatButton.setVisible(false);
		drawingShell.flatButton.addStyleName(DoorzienGWT.doorzienGWTCss.pushbutton());
		drawingShell.flatButton.addClickHandler(new PushClickHandler());
      
		makeBottom();
		
		if (resetState != null)
			setState(resetState);
		
//if (resetState == null)
//drawingShell.panel3D.testString2 = "rs null";	
		
		drawingShell.panel3D.repaint();


	} //init	
	
	private HashMap<String,Object> mapToHashMap(Map<String,Object> m)
	{	HashMap<String,Object> result = new HashMap<String,Object>();
		Set<String> keys = m.keySet();
		Iterator<String> iterator = (Iterator<String>)keys.iterator();
		while(iterator.hasNext()) 
		{
			String key = iterator.next();
			Object o = m.get(key);
			result.put(key, o);
		}
		return result;
	}
}

