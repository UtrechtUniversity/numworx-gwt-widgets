package fi.doorziengwt.client;

import java.util.*;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
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
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.resources.client.ImageResource;

import fi.doorziengwt.client.text.Text;

/**
 * main class for DoorzienGWT; note that DoorzienGWT has two 
 * appearances: <br>
 * 1) just a viewer where the 3d-object can be rotated (and folded in or out if it is a foldout) <br>
 * 2) a toolpanel consisting of a viewer with a menubar (a menu for choosing figures and a menu for options)
 * on top and toolbars on top and on the right with (toggle)buttons to modify the figure; availability of the 
 * menus and the tools are determined by the launch data<br>
 * optionally the viewer contains a tools-button to open the toolpanel (with the same 3d-object),
 * and a reset button (resetting to the original launch data figure),
 * while the toolpanel contains a close-button to close the toolpanel and show the same 3d-object
 * in just the viewer; 
 * this class handles button action in viewer mode and menu choices in toolpanel mode;
 * versions: <br>
 * EPN: drawing lines and planes one at a time, that is, after finishing a line or plane, the drawing mode is ended;<br>
 * only points on the initial object can be used to draw lines or planes;<br>
 * FI: drawing multiple lines and planes, that is, after finishing a line or plane, we stay in drawing mode; <br>
 * all points can be used for drawing lines, planes etc.<br>
 * preview (FI-mode) is not implemented since there is no MouseMove on the tablet.  
 * @author huub
 */

public class DoorzienGWT implements EntryPoint, InteractionStub 
{
	public static Text rb;
	
	private static Logger logger = Logger.getLogger("DoorzienGWT");
	
	/**
	 * the menu bar
	 */
	MenuBar menuBar;
	/**
	 * the menus for choosing figures or options 
	 */
	MenuBar figurenMenu, optiesMenu;
	
	/**
	 * options menu: number of help points (tick marks), lettering vertices
	 */
	MenuItem geenHulpPuntenItem, eenHulpPuntItem, tweeHulpPuntenItem, drieHulpPuntenItem, vierHulpPuntenItem, lettersItem;
	/**
	 * options menu: central or parallel projection
	 */
	MenuItem centraleProjectieItem, parallelProjectieItem;
	
	/**
	 * figures menu: 
	 */
	MenuItem achtvlakItem, balkItem, cilinderItem, kubusItem, twaalfvlakItem, twintigvlakItem, viervlakItem;

	/**
	 * figure submenus
	 */
	MenuBar huizenMenu, kegelsMenu,piramidesMenu, prismasMenu;
	/**
	 * figure submenus items: piramideHuisItem, schildHuisItem, kegel1Item, kegel2Item, kegel3Item, kegel4Item,  
	 */
	MenuItem piramideHuisItem, schildHuisItem, kegel1Item, kegel2Item, kegel3Item, kegel4Item,
			 piramide3Item, piramide4Item, piramide5Item, piramide6Item, piramide7Item, piramide8Item,
			 prisma3Item, prisma4Item, prisma5Item, prisma6Item;
	
	/**
	 * constants for figures
	 */
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

	/**
	 * constant for user figure 
	 */
	public static final int MYFIGURE = 100;
	
	/**
	 * version constants
	 */
    public static final int EPN = 0;
    public static final int FI = 1;
    /**
     * actual version
     */
    public static int version = FI; //EPN; 
	
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	/**
	 * basic LayoutPanel: contains an instance canvasPanel of LayoutPanel in viewer mode
	 * and contains an instance doorzienGWTDock of DockLayoutPanel in toolpanel mode  
	 */
	LayoutPanel dlp;
	/**
	 * class managing the viewer (simplified version of DrawingPanel2)
	 */
	DrawingShell drawingShell;
	/**
	 * a LayoutPanel containing an instance of Canvas from drawingShell and (optionally) 
	 * a toolsButton and a resetButton 
	 */
	LayoutPanel canvasPanel;
	
	/**
	 * DockLayoutPanel for the toolPanel mode: this contains a menu bar (optional) and a tool bar on top,
	 * a tool bar on the right and an instance of DrawingPanel2 (a LayoutPanel) which in turn contains
	 * the drawing Canvas, see method amkeTool()  
	 */
	DockLayoutPanel doorzienGWTDock; 
	
	/**
	 * general layout constants
	 */
	int breedte = 500;
	int hoogte = 450;
	int leftOffset = 5;
	int topOffset = 5;
	
	/**
	 * toolpanel layout constants 
	 */
	int topBarHeight;
	int menuHeight = 25;
	int topToolBarHeight = 55; 
	int rightToolBarWidth = 50;
	/**
	 * top bar of toolpanel, contains (optionally) a menubar and a top tool bar
	 */
	LayoutPanel topBar;
	/**
	 * top tool bar of toolpanel, this included the help label
	 */
	TopToolBar2 topToolBar;
	/**
	 * right tool bar of toolpanel
	 */
	RightToolBar2 rightToolBar;
	/**
	 * instance of LatoutPanel containing a drawing Canvas and managing all design changes to the 3d-figure
	 */
	DrawingPanel2 drawingPanel;
	/**
	 * label displaying help, included in TopToolBar2 
	 */
	Label helpBar;
	
	/**
	 * launch data
	 */
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
	
	/**
	 * (optional) buttons for viewer mode
	 */
	PushButton toolsButton, resetButton;

	/**
	 * parametrisation: toolpanel mode
	 */
	boolean figurenMenuOptie = true;
	boolean optiesMenuOptie = true;
	
	boolean lijnTekenOptie = true;
	boolean lijnVerlengOptie = true;
	
	boolean vlakTekenOptie = true;
	boolean evenwijdigVlakOptie = true;
	boolean toonDoorsnedeOptie = true;
	boolean splitsFiguurOptie = true;
	
	boolean bouwplaatOptie = true;
	
	/**
	 * not implemented
	 */
	boolean previewOptie = false;
	
	/**
	 * parametrisation: viewer mode 
	 */
	boolean designOption = true;
	boolean resetOption = true;
	boolean borderOption = false;

	/**
	 * user options
	 */
	boolean letters = false;
	boolean hulpPunten = false;
	boolean centraleProjectie = true;
	
	/**
	 * initial 3d-figure from launchdata
	 */
	HashMap<String,Object> resetState = null;

	/**
	 * number of lines/planes in the initial 3d-figure from launchdata;
	 * necessary to correctly initialize the top tool bar in toolpanel mode
	 */
	int numLines = 0;
	int numPlanes = 0;
	
	/**
	 * is the toolpanel visible? if not, the viewer is visible (this used to be a popup)
	 */
	boolean popupVisible = false;
	
	/**
	 * insert Css, get ImageResources and tirn them into Images
	 */
	public void getImages() 
	{
		rb = GWT.create(Text.class);
		
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
		
		dlp = new LayoutPanel();
		dlp.addStyleName(doorzienGWTCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(doorzienGWTCss.root());
		
		
		Stub.publish(this);
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
		
	}

	/**
	 * create the toolPanel: on top a menubar (optional) and a toolbar (including help bar),
	 * on the right another toolbar  
	 */
	public void makeTool()
	{
		// any menu's?
		if (figurenMenuOptie || optiesMenuOptie)
			topBarHeight = menuHeight + topToolBarHeight;
		else
			topBarHeight = topToolBarHeight;
		
		doorzienGWTDock = new DockLayoutPanel(Style.Unit.PX);
		doorzienGWTDock.addStyleName(doorzienGWTCss.dock());
		doorzienGWTDock.setSize("" + breedte + "px", "" + hoogte + "px");
		
		topBar = new LayoutPanel();
		topBar.setSize("" + breedte + "px", "" + topBarHeight + "px");
		
		// create the menus
		if (figurenMenuOptie || optiesMenuOptie)
		{	makeMenus();
			topBar.add(menuBar);
			topBar.setWidgetLeftWidth(menuBar, 0, Style.Unit.PX, breedte, Style.Unit.PX);
			topBar.setWidgetTopHeight(menuBar, 0, Style.Unit.PX, menuHeight, Style.Unit.PX);
		}
		
		// top tool bar
		topToolBar = new TopToolBar2(this, breedte);
		topToolBar.addStyleName(doorzienGWTCss.toolbar());
		topBar.add(topToolBar);
		topBar.setWidgetLeftWidth(topToolBar, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		if (figurenMenuOptie || optiesMenuOptie)
			topBar.setWidgetTopHeight(topToolBar, menuHeight, Style.Unit.PX, topBarHeight, Style.Unit.PX);
		else
			topBar.setWidgetTopHeight(topToolBar, 0, Style.Unit.PX, topBarHeight, Style.Unit.PX);
		
		doorzienGWTDock.addNorth(topBar, topBarHeight);

		// right tool bar
		rightToolBar = new RightToolBar2(this);
		rightToolBar.addStyleName(doorzienGWTCss.toolbar());
		doorzienGWTDock.addEast(rightToolBar, rightToolBarWidth);
		
		drawingPanel = new DrawingPanel2(this, breedte - rightToolBarWidth,
										 hoogte - topBarHeight, CUBE);
		// drawing area
		doorzienGWTDock.add(drawingPanel);
		
	}

	/**
	 * create menus and submenus
	 */
	public void makeMenus()
	{
		// main menu bar
		menuBar = new MenuBar();
		
		// figure menu
		figurenMenu = new MenuBar(true);
		figurenMenu.addItem(rb.achtvlakTekst(), new MenuCommand("achtvlak"));
		figurenMenu.addItem(rb.balkTekst(), new MenuCommand("balk"));
		figurenMenu.addItem(rb.cylinderTekst(), new MenuCommand("cilinder"));
		figurenMenu.addItem(rb.kubusTekst(), new MenuCommand("kubus"));
		figurenMenu.addItem(rb.twaalfVlakTekst(), new MenuCommand("twaalfvlak"));
		figurenMenu.addItem(rb.twintigVlakTekst(), new MenuCommand("twintigvlak"));
		figurenMenu.addItem(rb.vierVlakTekst(), new MenuCommand("viervlak"));
		// submenu huizen
		huizenMenu = new MenuBar(true);
		huizenMenu.addItem(rb.piramideDakTekst(), new MenuCommand("piramidehuis"));
		huizenMenu.addItem(rb.schildDakTekst(), new MenuCommand("schildhuis"));
		figurenMenu.addItem(rb.huizenTekst(), huizenMenu);
		// submenu cones
		kegelsMenu = new MenuBar(true);
		kegelsMenu.addItem(rb.kegel1Tekst(), new MenuCommand("kegel1"));
		kegelsMenu.addItem(rb.kegel2Tekst(), new MenuCommand("kegel2"));
		kegelsMenu.addItem(rb.kegel3Tekst(), new MenuCommand("kegel3"));
		kegelsMenu.addItem(rb.kegel4Tekst(), new MenuCommand("kegel4"));
		figurenMenu.addItem(rb.kegelsTekst(), kegelsMenu);
		// submenu piramides
		piramidesMenu = new MenuBar(true);
		piramidesMenu.addItem(rb.piramide3Tekst(), new MenuCommand("piramide3"));
		piramidesMenu.addItem(rb.piramide4Tekst(), new MenuCommand("piramide4"));
		piramidesMenu.addItem(rb.piramide5Tekst(), new MenuCommand("piramide5"));
		piramidesMenu.addItem(rb.piramide6Tekst(), new MenuCommand("piramide6"));
		piramidesMenu.addItem(rb.piramide7Tekst(), new MenuCommand("piramide7"));
		piramidesMenu.addItem(rb.piramide8Tekst(), new MenuCommand("piramide8"));
		figurenMenu.addItem(rb.piramidesTekst(), piramidesMenu);
		// submenu prisms
		prismasMenu = new MenuBar(true);
		prismasMenu.addItem(rb.prisma3Tekst(), new MenuCommand("prisma3"));
		prismasMenu.addItem(rb.prisma4Tekst(), new MenuCommand("prisma4"));
		prismasMenu.addItem(rb.prisma5Tekst(), new MenuCommand("prisma5"));
		prismasMenu.addItem(rb.prisma6Tekst(), new MenuCommand("prisma6"));
		figurenMenu.addItem(rb.prismasTekst(), prismasMenu);

		// options menu
		optiesMenu = new MenuBar(true);
		geenHulpPuntenItem = new MenuItem(rb.geenHulppuntenTekst(),new MenuCommand("hp0"));
		eenHulpPuntItem = new MenuItem(rb.eenHulppuntTekst(),new MenuCommand("hp1"));
		tweeHulpPuntenItem = new MenuItem(rb.tweeHulppuntenTekst(),new MenuCommand("hp2"));
		drieHulpPuntenItem = new MenuItem(rb.drieHulppuntenTekst(),new MenuCommand("hp3"));
		vierHulpPuntenItem = new MenuItem(rb.vierHulppuntenTekst(),new MenuCommand("hp4"));
		
		if (hulpPunten)
		{	eenHulpPuntItem.setStyleName(doorzienGWTCss.boldmenuitem());
		}
		else
		{	geenHulpPuntenItem.setStyleName(doorzienGWTCss.boldmenuitem());
		}

		if (letters)
			lettersItem = new MenuItem(rb.geenLettersTekst(),new MenuCommand("letters"));
		else
			lettersItem = new MenuItem(rb.toonLettersTekst(),new MenuCommand("letters"));
		
		centraleProjectieItem = new MenuItem(rb.centraleProjTekst(),new MenuCommand("cprojectie"));
		parallelProjectieItem = new MenuItem(rb.parallelProjTekst(),new MenuCommand("pprojectie"));
		if (centraleProjectie)
		{
			centraleProjectieItem.setStyleName(doorzienGWTCss.boldmenuitem());
		}
		else
		{
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
		
		menuBar.addItem(rb.figurenTekst(), figurenMenu);
		menuBar.addItem(rb.optiesTekst(), optiesMenu);
		
		menuBar.addStyleName(doorzienGWTCss.menubar());
		
		
	}	
		
	/**
	 * set lettering to default none, adapt menu
	 */
	public void resetLetters()
	{
		letters = false; 
		lettersItem.setText(rb.toonLettersTekst());
	}
	
	/**
	 * set projection to default central projection, adapt menu
	 */
	public void resetProjection()
	{
		centraleProjectieItem.setStyleName(doorzienGWTCss.boldmenuitem());
		parallelProjectieItem.setStyleName(doorzienGWTCss.normalmenuitem());
		centraleProjectie = true;
		
	}
	
	/**
	 * set help points to default none, adapt menu
	 */
	public void resetHelpPoints()
	{
		setHulpToNormal(geenHulpPuntenItem);
		geenHulpPuntenItem.setStyleName(doorzienGWTCss.boldmenuitem());
		hulpPunten = false;
		
	}
	
	/**
	 * after selecting a menuItem for help points (which is boldened)
	 * "unbold" the other help points menuItems   
	 * @param mi menuItem selected
	 */
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

	/**
	 * menu actions
	 * @param s String from menuCommand
	 */
	public void menuAction(String s)
	{
		// figures
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
		
		// help points
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

		// lettering, change meniItem text
		else if (s.equals("letters"))
		{
			if (lettersItem.getText().equals(rb.toonLettersTekst()))
			{
				lettersItem.setText(rb.geenLettersTekst());
				drawingPanel.setLetters(true);
				letters = true;
				
			}
			else 
			{
				lettersItem.setText(rb.toonLettersTekst());
				drawingPanel.setLetters(false);
				letters = false;
				
			}
		}
		// central projection
		else if (s.equals("cprojectie"))
		{
			centraleProjectieItem.setStyleName(doorzienGWTCss.boldmenuitem());
			parallelProjectieItem.setStyleName(doorzienGWTCss.normalmenuitem());
			drawingPanel.setProjection(DrawingPanel2.CENTRALPROJ);
			centraleProjectie = true;
		}
		// parallel projection
		else if (s.equals("pprojectie"))
		{
			centraleProjectieItem.setStyleName(doorzienGWTCss.normalmenuitem());
			parallelProjectieItem.setStyleName(doorzienGWTCss.boldmenuitem());
			drawingPanel.setProjection(DrawingPanel2.PARALLELPROJ);
			centraleProjectie = false;
		}
		
		
	}

	/**
	 * inner class for handling menu choices 
	 */
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
	}
	
	public DoorzienGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		
		ObjectMap h = JSONUtilities.wrapMap(map);

		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		
		if (h.containsKey("breedte"))
			breedte = h.getInt("breedte");
		if (h.containsKey("hoogte"))
			hoogte = h.getInt("hoogte");
		if (h.containsKey("interactiePanelLaunchState"))
			launchState = h.getMap("interactiePanelLaunchState");
		
		getImages();
		
		dlp = new LayoutPanel();
		dlp.addStyleName(doorzienGWTCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		init(breedte, hoogte, launchState, randomVarWaarden);

	}
	

	/**
	 * add (optionally) toolsButton and resetButton in viewer mode
	 */
	public void makeBottom()
	{
		if (designOption)
		{	
			toolsButton = new PushButton(toolsImage);
		
			canvasPanel.add(toolsButton);
			canvasPanel.setWidgetLeftWidth(toolsButton, leftOffset, Style.Unit.PX, 33, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(toolsButton, hoogte - topOffset - 32, Style.Unit.PX, 32, Style.Unit.PX);
		
			toolsButton.addClickHandler(new PushClickHandler());
		}
		
		if (resetOption)
		{
			resetButton = new PushButton(resetImage);
			
			canvasPanel.add(resetButton);
			canvasPanel.setWidgetLeftWidth(resetButton, breedte - leftOffset - 32, Style.Unit.PX, 32, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(resetButton, hoogte - topOffset - 32, Style.Unit.PX, 32, Style.Unit.PX);
		
			resetButton.addClickHandler(new PushClickHandler());
		}
	}
	

	/**
	 * extract the current3d figure from the toolpanel and show it in the viewer
	 * (hiding the toolpanel and showing the viewer)
	 */
    public void figureToViewer()
    {
    	Map<String,Object> h = getToolState();
    	setViewerState(h);
    	
    	dlp.setWidgetVisible(doorzienGWTDock,false);
    	dlp.setWidgetVisible(canvasPanel,true);
    	popupVisible = false;
    	drawingShell.panel3D.repaint();
    }
    
	/**
	 * extract the current3d figure from the viewer and show it in the toolpanel
	 * (hiding the viewer and showing the toolpanel) 
	 */
    public void figureToPopup()
    {
    	popupVisible = true;
    	
    	Map<String,Object> h = getViewerState();
    	setToolState(h);
    	
    	dlp.setWidgetVisible(doorzienGWTDock,true);
    	dlp.setWidgetVisible(canvasPanel,false);
    	drawingPanel.paint();
    }

    /**
     * set the 3d-object in the viewer 
     * @param map Map containing the 3d-object, see class NoSer 
     */
    public void setViewerState(Map<String,Object> map)
    {
    	
    	ObjectMap h = JSONUtilities.wrapMap(map);
    	
    	// letters is taken care of in DrawConstants
		if (centraleProjectie)
		{	drawingShell.setProjection(DrawingShell.CENTRALPROJ);
		}
		else
		{	drawingShell.setProjection(DrawingShell.PARALLELPROJ);
		}

		int figuurCode = CUBE;
		if (h.containsKey("figuurCode"))
			figuurCode = h.getInt("figuurCode");
		
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

		// take care of filled and planesFilled after creating the Object3D
		
		double lengthFactor = 0;
		if (h.containsKey("lengthFactor"))
			lengthFactor = h.getDouble("lengthFactor");

		DrawConstants.llFactor = lengthFactor;

		// parameters for drawingShell.panel3D 
		Matrix3D mat = new Matrix3D();
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
		
		drawingShell.panel3D.mat = mat;
		drawingShell.panel3D.mat.setOrigin(
				drawingShell.panel3D.breedte / 2,
				drawingShell.panel3D.hoogte / 2,
				0);
		drawingShell.panel3D.paintType = paintType;					
		drawingShell.panel3D.showInside = showInside;
		
		drawingShell.panel3D.setZoomFactor(zoomFactor);					
		
		drawingShell.zoom = zoomFactor;
        
        int mode = drawingShell.INERT;
        if (h.containsKey("mode"))
        {	mode = h.getInt("mode");
        }
        
        drawingShell.mouseMode = mode;
        
        // this Map describes the original object
		Map<String,Object> origObject = new HashMap<String,Object>();
		// this List contains the construction list
		List<Object> conStateList = new ArrayList<Object>();
		
		if (h.containsKey("origObject"))
		{	origObject = h.getMap("origObject");
		}
		if (h.containsKey("conState"))
		{	conStateList = h.getList("conState");
		}
		
		Object3D originalObject = NoSer.setObject3DState(origObject);
		Vector construction = NoSer.setConstructionState(conStateList);

		// construct the 3d-object from originalObject and construction 
		drawingShell.currentObjectGroup = drawingShell.rebuild(originalObject, construction, null);
		drawingShell.originalObject = drawingShell.currentObjectGroup.leftMostLeaf();

		// foldout
		if (drawingShell.mouseMode == drawingShell.FOLDOUT)
		{
			// state of stand original object
			boolean oldFilled = false;
			Matrix3D oldPos = new Matrix3D();
			List<Double> oldCoeff = new ArrayList<Double>();
			if (h.containsKey("oldFilled"))
				oldFilled = h.getBoolean("oldFilled");
			if (h.containsKey("oldPos"))
				oldCoeff = h.getDoubleList("oldPos");
			oldPos = NoSer.setMatrix3DState(oldCoeff);
			
			// state of fold out
			boolean flattened = false;
			double angle = 2e-1d;
			if (h.containsKey("flattened"))
				flattened = h.getBoolean("flattened");
			if (h.containsKey("angle"))
				angle = h.getDouble("angle");
			drawingShell.flattened = flattened;
			drawingShell.currentFoldOut = angle;
			
			// reconstruct startFacet for fold out
			Facet3D startFacet = null;
			List<Double> vertices = new ArrayList<Double>(); 
			if (h.containsKey("startFacet"))
			{	vertices = h.getDoubleList("startFacet");
			}
	
			startFacet = NoSer.setFacet3DVertexState(vertices);

			if (startFacet != null)
			{	
				drawingShell.startFacet = startFacet; 
				drawingShell.makeFoldOut(0, true);
				drawingShell.processSlider(angle);
				
				if (flattened)
					drawingShell.flattenAction();
				
			}
			else
			{
			}
			
		}
		// object was cut into two pieces
		else if (drawingShell.mouseMode == drawingShell.CUTOBJECT)
		{
			// state of original object
			boolean oldPlanesFilled = false;
			if (h.containsKey("oldPlanesFilled"))
				oldPlanesFilled = h.getBoolean("oldPlanesFilled");
			drawingShell.oldPlanesFilled = oldPlanesFilled;

			// state of cut object
			String volumeString = "";
			if (h.containsKey("volumeString"))
				volumeString = h.getString("volumeString");
			drawingShell.panel3D.testString = volumeString;
			
			// cutting plane
			Plane3D planeChoosen = new Plane3D(1, 0, 0, 0);
			List<Double> planeChoosenCoeff = new ArrayList<Double>(); 
			if (h.containsKey("planeChoosen"))
				planeChoosenCoeff = h.getDoubleList("planeChoosen");
			planeChoosen = NoSer.setPlane3DState(planeChoosenCoeff);
			drawingShell.planeChoosen = planeChoosen; 
			
			drawingShell.figureCut = true;
			drawingShell.cutObject(1, true);
			
		}
		else // object is not a foldout or a cut object
		{
			drawingShell.panel3D.initializeModel(drawingShell.currentObjectGroup, false);
			drawingShell.slider.setVisible(false);
			drawingShell.flatButton.setVisible(false);
		}

		// this after creating the 3d-object
		drawingShell.setFilled(filled);
		drawingShell.fillPlanes(planesFilled);
			
		drawingShell.panel3D.repaint();

    } // setViewerState
    
    
    /**
     * get the 3d-object from the viewer
     * @return a HashMap containing the 3d-object, see class NoSer
     */
    public Map<String,Object> getViewerState()
    {
    	Map<String,Object> h = new HashMap<String,Object>();
    	
		// user cannot change lettering, projection in viewer mode
    	// there are no help points in viewer mode
		
		// status of the 3d-object for toolbuttons in toolpanel mode
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
		
		// drawingShell.panel3D items
		Matrix3D mat = drawingShell.panel3D.mat;
		int paintType = drawingShell.panel3D.paintType;
		double zoomFactor = drawingShell.panel3D.zoomFactor;
		boolean showInside = drawingShell.panel3D.showInside;

		// convert mat to a List
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
        
        List<Object> conState = NoSer.getConstructionState(construction);
		
		h.put("origObject", origObject);
		h.put("conState", conState);
		
		int mode = drawingShell.INERT;

		// fold out
		if ((drawingShell.mouseMode == drawingShell.FOLDOUT) && 
			(drawingShell.startFacet != null))
		{
			mode = drawingShell.FOLDOUT;
			
			// state original object
			boolean oldFilled = drawingShell.oldFilled;
			Matrix3D oldPos = drawingShell.oldPos;
			List<Double> oldCoeff = NoSer.getMatrix3DState(oldPos);
			h.put("oldFilled", new Boolean(oldFilled));
			h.put("oldPos", oldCoeff);
			
			// state fold out
			boolean flattened = drawingShell.flattened;
			double angle = drawingShell.currentFoldOut;
			h.put("flattened", new Boolean(flattened));
			h.put("angle", new Double(angle));
			
			Facet3D theStartFacet = drawingShell.startFacet;
			List<Double> startFacet = NoSer.getFacet3DVertexState(theStartFacet);
			h.put("startFacet", startFacet);
			
		}
		// object in two pieces
		if ((drawingShell.mouseMode == drawingShell.CUTOBJECT) && 
			(drawingShell.planeChoosen != null))
		{
			mode = drawingShell.CUTOBJECT;
			// state original object
			boolean oldPlanesFilled = drawingShell.oldPlanesFilled;
			h.put("oldPlanesFilled", new Boolean("oldPlanesFilled"));
			// state cut object
			String volumeString = drawingShell.panel3D.testString;
			h.put("volumeString", volumeString);
			
			Plane3D thePlaneChoosen = drawingShell.planeChoosen;
			List<Double> planeChoosen = NoSer.getPlane3DState(thePlaneChoosen);
			h.put("planeChoosen", planeChoosen);
			
		}
		
		h.put("mode", new Integer(mode));
    	
    	
    	return h;
    	
    	
    }

    /**
     * set the 3d-object in the toolpanel 
     * @param map Map containing the 3d-object, see class NoSer 
     */
    public void setToolState(Map<String,Object> map)
    {
    	
    	ObjectMap h = JSONUtilities.wrapMap(map);

    	// help points after creating the object
    	
    	if (letters)
		{
			lettersItem.setText(rb.geenLettersTekst());
			drawingPanel.setLetters(true);
		}
		else 
		{
			lettersItem.setText(rb.toonLettersTekst());
			drawingPanel.setLetters(false);
		}

		if (centraleProjectie)
		{	drawingPanel.setProjection(DrawingPanel2.CENTRALPROJ);
			centraleProjectieItem.setStyleName(doorzienGWTCss.boldmenuitem());
			parallelProjectieItem.setStyleName(doorzienGWTCss.normalmenuitem());
		}
		else
		{	drawingPanel.setProjection(DrawingPanel2.PARALLELPROJ);
			centraleProjectieItem.setStyleName(doorzienGWTCss.normalmenuitem());
			parallelProjectieItem.setStyleName(doorzienGWTCss.boldmenuitem());
		}

		int figuurCode = CUBE;
		if (h.containsKey("figuurCode"))
			figuurCode = h.getInt("figuurCode");
		
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
		
		// this enables/disables the line buttons
		drawingPanel.setNumLines(numLines);
		// this enables/disables the plane buttons
		drawingPanel.setNumPlanes(numPlanes);
		if (filled)
		{	rightToolBar.wireSolidButton.setDown(true);
		}	
		if (planesFilled)
		{	topToolBar.planesFilledButton.setDown(true);
		}
		
		double lengthFactor = 0;
		if (h.containsKey("lengthFactor"))
			lengthFactor = h.getDouble("lengthFactor");

		DrawConstants.llFactor = lengthFactor;
		// this means there are lines
		if (lengthFactor > 0)
		{	topToolBar.shortLinesButton.setEnabled(true);
            if (lengthFactor >= (DrawConstants.MAXLLFACTOR - DrawConstants.LLSTEP / 10))
                topToolBar.lengLinesButton.setEnabled(false);    
		}

		// drawingPanel.panel3D items
		Matrix3D mat = new Matrix3D();
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
        }
        
        drawingPanel.mouseMode = mode;

        // this Map describes the original object 
		Map<String,Object> origObject = new HashMap<String,Object>();
		// List containing the construction
		List<Object> conStateList = new ArrayList<Object>(); 
		
		if (h.containsKey("origObject"))
		{	origObject = h.getMap("origObject");
		}
		if (h.containsKey("conState"))
		{	conStateList = h.getList("conState");
		}
		
		Object3D originalObject = NoSer.setObject3DState(origObject);
		Vector construction = NoSer.setConstructionState(conStateList);

		// create the 3d-object
		drawingPanel.currentObjectGroup = drawingPanel.rebuild(originalObject, construction, null);
		drawingPanel.originalObject = drawingPanel.currentObjectGroup.leftMostLeaf();

		// fold out
		if (drawingPanel.mouseMode == drawingPanel.FOLDOUT)
		{
			// state originale object
			boolean oldFilled = false;
			Matrix3D oldPos = new Matrix3D();
			List<Double> oldCoeff = new ArrayList<Double>();
			if (h.containsKey("oldFilled"))
				oldFilled = h.getBoolean("oldFilled");
			if (h.containsKey("oldPos"))
				oldCoeff = h.getDoubleList("oldPos");
			oldPos = NoSer.setMatrix3DState(oldCoeff);
			
			// state fold out
			boolean flattened = false;
			double angle = 2e-1d;
			if (h.containsKey("flattened"))
				flattened = h.getBoolean("flattened");
			if (h.containsKey("angle"))
				angle = h.getDouble("angle");
			drawingPanel.flattened = flattened;
			drawingPanel.currentFoldOut = angle;

			// reconstruct startFacet fold out
			Facet3D startFacet = null;
			List<Double> vertices = new ArrayList<Double>(); 
			if (h.containsKey("startFacet"))
			{	vertices = h.getDoubleList("startFacet");
			}
	
			startFacet = NoSer.setFacet3DVertexState(vertices);

			if (startFacet != null)
			{	
				drawingPanel.startFacet = startFacet; 
				drawingPanel.makeFoldOut(0, true);
				
				drawingPanel.processSlider(angle);
				
				if (flattened)
					drawingPanel.flattenAction();
				
				rightToolBar.conDrawButton.setDown(true);
				
			}
			else
			{
			}
			
		}
		// object was cut into two pieces
		else if (drawingPanel.mouseMode == drawingPanel.CUTOBJECT)
		{
			// state original object
			boolean oldPlanesFilled = false;
			if (h.containsKey("oldPlanesFilled"))
				oldPlanesFilled = h.getBoolean("oldPlanesFilled");
			drawingPanel.oldPlanesFilled = oldPlanesFilled;

			// state cut object
			String volumeString = "";
			if (h.containsKey("volumeString"))
				volumeString = h.getString("volumeString");
			drawingPanel.panel3D.testString = volumeString;
			
			
			Plane3D planeChoosen = new Plane3D(1, 0, 0, 0);
			List<Double> planeChoosenCoeff = new ArrayList<Double>(); 
			if (h.containsKey("planeChoosen"))
				planeChoosenCoeff = h.getDoubleList("planeChoosen");
			planeChoosen = NoSer.setPlane3DState(planeChoosenCoeff);
			drawingPanel.planeChoosen = planeChoosen; 
			
			drawingPanel.figureCut = true;
			drawingPanel.cutObject(1, true);
			
			topToolBar.cutButton.setDown(true);
			
		}
		else // no foldout or cut object
		{	drawingPanel.panel3D.initializeModel(drawingPanel.currentObjectGroup, false);
		
		}

		// do this AFTER creating the Object3D
		drawingPanel.setFilled(filled);
		drawingPanel.fillPlanes(planesFilled);
		if (hulpPunten)
		{	drawingPanel.setHelpPoints(1);
			setHulpToNormal(eenHulpPuntItem);
			eenHulpPuntItem.setStyleName(doorzienGWTCss.boldmenuitem());
		}
		else
		{	drawingPanel.setHelpPoints(0);
			setHulpToNormal(geenHulpPuntenItem);
			geenHulpPuntenItem.setStyleName(doorzienGWTCss.boldmenuitem());
    	}
		
		drawingPanel.addToHistory();
		
		if (drawingPanel.mouseMode != drawingPanel.INERT)
		{
			rightToolBar.undoButton.setEnabled(false);
		}
		
			
		drawingPanel.panel3D.repaint();

    } // setToolState
    
    /**
     * get the 3d-object from the toolpanel
     * @return a HashMap containing the 3d-object, see class NoSer
     */
    public Map<String,Object> getToolState()
    {
    	Map<String,Object> h = new HashMap<String,Object>();
    	
		// letters is taken care of in DrawConstants
    	hulpPunten = DrawConstants.TICKNUM > 0;
    	centraleProjectie = (drawingPanel.projection == DrawingPanel2.CENTRALPROJ);
		
		// status of the 3d-object 
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
		
		// convert mat to a List
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
        
        List<Object> conState = NoSer.getConstructionState(construction);
		
		h.put("origObject", origObject);
		h.put("conState", conState);
		
		int mode = drawingPanel.INERT;
	
		// fold out
		if ((drawingPanel.mouseMode == drawingPanel.FOLDOUT) && 
			(drawingPanel.startFacet != null))
		{
			mode = drawingPanel.FOLDOUT;
			
			// state original object
			boolean oldFilled = drawingPanel.oldFilled;
			Matrix3D oldPos = drawingPanel.oldPos;
			List<Double> oldCoeff = NoSer.getMatrix3DState(oldPos);
			h.put("oldFilled", new Boolean(oldFilled));
			h.put("oldPos", oldCoeff);
			
			// state  fold out
			boolean flattened = drawingPanel.flattened;
			double angle = drawingPanel.currentFoldOut;
			h.put("flattened", new Boolean(flattened));
			h.put("angle", new Double(angle));

			Facet3D theStartFacet = drawingPanel.startFacet;
			List<Double> startFacet = NoSer.getFacet3DVertexState(theStartFacet);
			h.put("startFacet", startFacet);
			
		}
		// object cut into two pieces
		if ((drawingPanel.mouseMode == drawingPanel.CUTOBJECT) && 
			(drawingPanel.planeChoosen != null))
		{
			mode = drawingPanel.CUTOBJECT;
			// state original object
			boolean oldPlanesFilled = drawingPanel.oldPlanesFilled;
			h.put("oldPlanesFilled", new Boolean("oldPlanesFilled"));
			// state cut object
			String volumeString = drawingPanel.panel3D.testString;
			h.put("volumeString", volumeString);
			
			Plane3D thePlaneChoosen = drawingPanel.planeChoosen;
			List<Double> planeChoosen = NoSer.getPlane3DState(thePlaneChoosen);
			h.put("planeChoosen", planeChoosen);
			
		}
		
		h.put("mode", new Integer(mode));
    	
    	
    	return h;
    }
    
     
    /**
     * inner class for handling toolsButton/resetButton/flatButton in viewer mode 
     * @author huub
     *
     */
    class PushClickHandler implements ClickHandler
	{
    	public void onClick(ClickEvent e)
		{   
	   		//e.preventDefault();
	   		e.stopPropagation();
	    	
	   		if (e.getSource() == toolsButton)
	   		{
	   			figureToPopup();
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

	public Widget asWidget()
	{
		return dlp;
	}
	
	/**
	 * get the state of the current 3d-figure, that is:
	 * save the state of the toolpanel (visible or not),
	 * if the toolpanel is visible, extract its figure
	 * to the viewer and then save the figure from the viewer  
	 */
	public HashMap<String, Object> getState()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		
		h.put("popupVisible", new Boolean(popupVisible));
		
		if (popupVisible)
		{	figureToViewer();
		
		}
		h.put("state", getViewerState());
		return h;
	}

	/**
	 *  set the state of the current 3d-figure, that is:
	 *  find out if the toolpanel or the viewer should be 
	 *  visible, then extract the 3d-figure from the
	 *  HashMap and put in in the toolpanel or the viewer
	 */
	public void setState(HashMap<String, Object> h)
	{
		if(h == null || h.isEmpty()) return;
	
		if (h.containsKey("popupVisible"))
			popupVisible = ((Boolean) h.get("popupVisible")).booleanValue();
		
		HashMap<String,Object> state = new HashMap<String,Object>();
		if (h.containsKey("state"))
			state = (HashMap<String,Object>) h.get("state");
		else
			state = h;
		
		if (popupVisible)
		{	
			setToolState(state);
			dlp.setWidgetVisible(doorzienGWTDock,true);
			dlp.setWidgetVisible(canvasPanel,false);
			drawingPanel.paint();
			
			doorzienGWTDock.forceLayout();
		}
		else
		{	setViewerState(state);
			dlp.setWidgetVisible(doorzienGWTDock,false);
			dlp.setWidgetVisible(canvasPanel,true);
			drawingShell.panel3D.repaint();
			
			canvasPanel.forceLayout();
		}
		 
	}

	@Override
	public int getScore()
	{	// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean isCorrect()
	{	return Boolean.TRUE;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{	// TODO Auto-generated method stub

	}

	/**
	 * read launch data, create viewer and toolpanel, and show initial figure (if any)
	 * in viewer or toolpanel (as requested in the launch data) 
	 */
	public void init(int width, int height, Map<String, Object> map, Map<String, Number> values) 
	{
		
logger.info("DoorzienGWT init");

		this.breedte = width;
		this.hoogte = height;
		
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		
		ObjectMap launchState = JSONUtilities.wrapMap(map);

		// parametrisation for toolpanel mode
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

		// parametrisation for viewer mode
		if (launchState.containsKey("designOption"))
			designOption = launchState.getBoolean("designOption");

		if (launchState.containsKey("resetOption"))
			resetOption = launchState.getBoolean("resetOption");

		if (launchState.containsKey("borderOption"))
			borderOption = launchState.getBoolean("borderOption");

		// true: start with viewer panel, false: start with toolpanel 
		if (launchState.containsKey("demo"))
			popupVisible = !(launchState.getBoolean("demo"));

		if (launchState.containsKey("letters"))	
			letters = launchState.getBoolean("letters");
		if (launchState.containsKey("hulpPunten"))
			hulpPunten = launchState.getBoolean("hulpPunten");
		if (launchState.containsKey("centraleProjectie"))
			centraleProjectie = launchState.getBoolean("centraleProjectie");
		
		// starting figure from the launchdata (if any)
		if (launchState != null && launchState.getMap("origObject") != null)
		{	resetState = mapToHashMap(map); 
			 
		}
		
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		
		// viewer mode
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
		
		// slider for fold out (initially not visible)
		drawingShell.slider = new Slider2(drawingShell, 0, 1);
		canvasPanel.add(drawingShell.slider.sliderCanvas);
		canvasPanel.setWidgetLeftWidth(drawingShell.slider.sliderCanvas, breedte - Slider2.horSize - 1, 
				                       Style.Unit.PX, Slider2.horSize, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(drawingShell.slider.sliderCanvas, 1, 
				                       Style.Unit.PX, Slider2.vertSize, Style.Unit.PX);
		drawingShell.slider.setVisible(false);
		
		// flat button for foldout (initilally not visible)
		drawingShell.flatButton = new PushButton(rb.platTekst());
		drawingShell.flatButton.addStyleName(doorzienGWTCss.pushbutton());
		canvasPanel.add(drawingShell.flatButton);
		canvasPanel.setWidgetLeftWidth(drawingShell.flatButton, breedte - 40 - 1, Style.Unit.PX, 40, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(drawingShell.flatButton, 1 + Slider2.vertSize, Style.Unit.PX, 22, Style.Unit.PX);
		drawingShell.flatButton.setVisible(false);
		drawingShell.flatButton.addStyleName(DoorzienGWT.doorzienGWTCss.pushbutton());
		drawingShell.flatButton.addClickHandler(new PushClickHandler());
		// add optional tool- and resetButton 
		makeBottom();
		
		drawingShell.panel3D.setBordered(borderOption);
		
		// create the toolpanel
		makeTool();
		
		dlp.add(doorzienGWTDock);
		dlp.setWidgetLeftWidth(doorzienGWTDock, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		dlp.setWidgetTopHeight(doorzienGWTDock, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		dlp.setWidgetVisible(doorzienGWTDock, false);

		// this shows viewer or toolpanel (as determined by popupVisible)
		// with the initial launchdata figure 
		if (resetState != null)
			setState(resetState);
		
		drawingShell.panel3D.repaint();

		dlp.forceLayout();
		canvasPanel.forceLayout();
		doorzienGWTDock.forceLayout();

	} //init	

	/**
	 * convert a Map to a HashMap
	 * @param m Map to covert
	 * @return Map as HashMap
	 */
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

	@Override
	public void kijkNa() {
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
	
	//@Override
	public void zetNagekeken(boolean b) {
	}

	//@Override
	public int[][] getScoreObjectives() {
		return null;
	}

}

