package fi.stroomdiagrammengwt.client;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import fi.stroomdiagrammengwt.client.text.Text;

import java.util.logging.Logger;

/**
 * main class for StroomDiagrammenGWT<br>
 * the flow diagram consist of Vertices (class Vertex) and Edges (class edge);<br>
 * the flow diagram is drawn by class DrawingPanel, which contains a Canvas, while 
 * the layout is handled by class DiagramManager;<br>
 * Mouse/Touch Events are handled by the class DrawingPanel;<br>
 * at the top of the Canvas there is a menuBar (optional) with three menus (each optional);
 * one menu each for choosing the format of displaying calculations (decimal number or fraction)
 * and the width of the edges (see classes Vertex and Edge); via the third menu labels can be 
 * added/removed from the vertices, and an additional source (root) can be added to the flow diagram;<br>
 * at the bottom of the Canvas is an (optional) panel containing two buttons to refresh (new diagram)
 * or undo changes in the flow diagram;<br> 
 * this class reads the launchdata and takes care of getState/setState (see classes NoSer,
 * Vertexcopy, EdgeCopy, DiagramCopy and DiagramCopier)    
 */

public class StroomDiagrammenGWT implements EntryPoint, InteractionStub 
{
	/**
	 * internationalization
	 */
	public static Text rb;
	
	/**
	 * logger
	 */
    static Logger logger = Logger.getLogger("StroomDiagrammenGWT");

    /**
     * holderId for html
     */
    static final String holderId = "dockholder";

    /**
     * error message if Canvas not supported 
     */
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	private int mode;
	
	/**
	 * the communications root
	 */
	private OpdrNavIF comRoot;
	
	Boolean correct = new Boolean(true);

	/**
	 * DockLayoutPanel containing menuBar (north), bottomPanel(south) and Canvas (center)
	 */
	DockLayoutPanel dlp;
	/**
	 * bottomPanel 
	 */
	LayoutPanel bottomPanel;
	/**
	 * drawingPanel containing the Canvas
	 */
	DrawingPanel drawingPanel;

	/**
	 * PushButton to return to the diagram in the launchdata (which can consist of a root only)  
	 */
	PushButton opnieuwButton;
	/**
	 * Undo button; note: the initial diagram from the launchdata cannot be undone
	 */
	PushButton terugButton;
	
	/**
	 * constant: width for a small button (pixels)
	 */
	int buttonWidth1 = 50;
	/**
	 * constant: width for a large button (pixels)
	 */
	int buttonWidth2 = 100;
	/**
	 * constant: height for a button (pixels)
	 */
	int buttonHeight = 25;

	/**
	 * default width (pixels)
	 */
	int breedte = 500;
	/**
	 * default height (pixels)
	 */
	int hoogte = 450;
	/**
	 * constant: height of bottomPanel (pixels)
	 */
	int bottomHeight = 35;
	/**
	 * horizontal offset for layout (pixels)
	 */
	int leftOffset = 5;
	/**
	 * vertical offset for layout (pixels)
	 */
	int topOffset = 5;
	
	/**
	 * constant: height of the menuBar (pixels)
	 */
	int menuHeight = 25;
	
	private int asHoogte;

	/**
	 * map containing the launchdata
	 */
	private Map<String, Object> launchState;
	
	String[] randomVarNamen = null;
	
	HashMap<String, Object> randomVarWaarden = null;
	
	/**
	 * the menuBar
	 */
	MenuBar menuBar;
	/**
	 * the menus of the menu bar
	 */
	MenuBar berekeningenMenu, stroombreedteMenu, optiesMenu;

	/**
	 * berekeningenMenu: show flow and capacities as decimal numbers
	 */
	MenuItem decimaalItem;
	/**
	 * berekeningenMenu: show flow and capacities as fractions
	 */
	MenuItem breukenItem;
	/**
	 * stroombreedteMenu: show flow width relative (see class Edge)
	 */
	MenuItem relatiefItem;
	/**
	 * stroombreedteMenu: show flow width absolute (see class Edge)
	 */
	MenuItem absoluutItem;
	/**
	 * optiesMenu: vertices with/without labels
	 */
	MenuItem labelsItem;
	/**
	 * optiesMenu: add an additional root to the flow diagram
	 */
	MenuItem addRootItem;
		
	/**
	 * show/hide the berekeningenMenu 
	 */
    boolean toonBerekeningenMenu = true;
    /**
     * calculate in fractions/decimal numbers
     */
    boolean berekenInBreuken = false;

	/**
	 * show/hide the stroombreedteMenu 
	 */
    boolean toonStroombreedteMenu = true;
    /**
     * flow width absolute/relative
     */
    boolean stroombreedteAbsoluut = false;
    
	/**
	 * show/hide the optiesMenu 
	 */
    boolean toonOptiesMenu = true;
    /**
     * vertices with/without labels
     */
    boolean toonLabels = false;
    /** 
     * number of roots in the diagram
     */
    int aantalBronnen = 1;
    
    /**
     * the flow diagram is for demo purpose (no changing flows, capacities, no mouse/touch actions)
     */
    boolean isDemo = false;
    
    /**
     * the flow diagram from the launch data
     */
    HashMap<String,Object> stategwt = null;
	
    /**
     * constant: background color of Canvas
     */
    public static CssColor workBackground = CssColor.make(255,255,255);
    /**
     * constant: color of and Edge with positive capacity
     */
   	public static CssColor edgeColor = CssColor.make(0,255,255);
   	/**
   	 * constant: color of a highlighted Edge
   	 */
   	public static CssColor highEdgeColor = CssColor.make(198, 239, 140);
   	/**
   	 * constant: color of an Edge with zero capacity	
   	 */
   	public static CssColor zeroEdgeColor = CssColor.make(220,220,220);
   	/**
   	 * constant: one of 3 colors for suggesting bubbles in an Edge
   	 */
	public static CssColor bubbleColor = CssColor.make(0,155,155); 
	public static CssColor bubbleColor1 = CssColor.make(0,175,175);
	public static CssColor bubbleColor2 = CssColor.make(0,195,195);
	/**
	 * constant: one of 3 colors to suggest bubbles in a highlighted Edge
	 */
   	public static CssColor highBubbleColor = CssColor.make(198, 239, 140);
   	public static CssColor highBubbleColor1 = CssColor.make(218, 255, 160);
   	public static CssColor highBubbleColor2 = CssColor.make(238, 2255, 180);

   	/**
   	 * the Client Bundle
   	 */
   	StroomDiagrammenGWTClientBundle stroomDiagrammenGWTClientBundle;
   	/**
   	 * the Css Resource
   	 */
   	StroomDiagrammenGWTCssResource stroomDiagrammenGWTCssResource;
   	
	public void onModuleLoad() 
	{
		getImages();

		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(stroomDiagrammenGWTCssResource.root());
		
		Stub.publish(this);
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
		
		
	}
	
	/**
	 * initializes internationalization, creates ClientBundle and CssResource
	 */
	public void getImages()
	{
		rb = GWT.create(Text.class);
		stroomDiagrammenGWTClientBundle = GWT.create(StroomDiagrammenGWTClientBundle.class);
		stroomDiagrammenGWTCssResource = stroomDiagrammenGWTClientBundle.getStroomDiagrammenGWTCssResource();
		stroomDiagrammenGWTCssResource.ensureInjected();
	}

	public StroomDiagrammenGWT()
	{
	}
	
	/**
	 * constructor for PopupFacade-modus
	 * @param map the launchdata
	 * @param randomVarNamen random variable names
	 * @param randomVarWaarden the values if the random variable names
	 */
	public StroomDiagrammenGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
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
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		init(breedte, hoogte, launchState, randomVarWaarden);
	}

	/**
	 * make the menus, add them to the menubar if required; <br>
	 * see inner class MenuCommand and method MenuAction
	 */
	public void makeMenus()
	{
		menuBar = new MenuBar();
		menuBar.addStyleName(stroomDiagrammenGWTCssResource.menubar());
		
		berekeningenMenu = new MenuBar(true);
		decimaalItem = new MenuItem(rb.decimaalLabel(),new MenuCommand("decimaal"));
		breukenItem = new MenuItem(rb.breukenLabel(),new MenuCommand("breuken"));
		// bolden the required item
		if (berekenInBreuken)
		{	breukenItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());
		}
		else
		{	decimaalItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());
		}
		berekeningenMenu.addItem(decimaalItem);
		berekeningenMenu.addItem(breukenItem);

		stroombreedteMenu = new MenuBar(true);
		relatiefItem = new MenuItem(rb.relatiefLabel(),new MenuCommand("relatief"));
		absoluutItem = new MenuItem(rb.absoluutLabel(),new MenuCommand("absoluut"));
		// bolden the required item
		if (stroombreedteAbsoluut)
		{	absoluutItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());
		}
		else
		{	relatiefItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());
		}
		stroombreedteMenu.addItem(relatiefItem);
		stroombreedteMenu.addItem(absoluutItem);
		
		optiesMenu = new MenuBar(true);
		labelsItem = new MenuItem(rb.knooppuntenMetLabelsLabel(),new MenuCommand("labels"));
		addRootItem = new MenuItem(rb.voegNieuweBronToeLabel(),new MenuCommand("root"));
		// bolden the required item		
		if (toonLabels)
		{	labelsItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());
		}
		optiesMenu.addItem(labelsItem);
		optiesMenu.addItem(addRootItem);
		
		if (toonBerekeningenMenu)
			menuBar.addItem(rb.berekeningenLabel(), berekeningenMenu);
		if (toonStroombreedteMenu)
			menuBar.addItem(rb.stroombreedteLabel(), stroombreedteMenu);
		if (toonOptiesMenu)
			menuBar.addItem(rb.optiesLabel(), optiesMenu);
	}	

	/**
	 * create and add the two buttons to the bottomPanel
	 */
	public void	makeBottom()
	{
		int currentX = leftOffset;
		int currentY = topOffset;
		opnieuwButton = new PushButton(rb.nieuwDiagramKnopLabel());
		opnieuwButton.addStyleName(stroomDiagrammenGWTCssResource.pushbutton());
		bottomPanel.add(opnieuwButton);
		bottomPanel.setWidgetLeftWidth(opnieuwButton, currentX, Style.Unit.PX, buttonWidth2, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(opnieuwButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		opnieuwButton.addClickHandler(new PushClickHandler());
		currentX += buttonWidth2 + 2 * leftOffset;		

		terugButton = new PushButton(rb.terugKnopLabel());
		terugButton.addStyleName(stroomDiagrammenGWTCssResource.pushbutton());
		bottomPanel.add(terugButton);
		bottomPanel.setWidgetLeftWidth(terugButton, currentX, Style.Unit.PX, buttonWidth1, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(terugButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		terugButton.addClickHandler(new PushClickHandler());
		currentX += buttonWidth1 + 2 * leftOffset;		
	}

	/**
	 * ClickHandler for opnieuwButton and terugButton 
	 */
    class PushClickHandler implements ClickHandler
    {
    	public void onClick(ClickEvent e)
    	{
			e.stopPropagation();
			if (e.getSource() == terugButton)
			{
				drawingPanel.previousDiagram();
        		if (DrawingPanel.history.size() < 1)
        			terugButton.setEnabled(false);
			}
			else if (e.getSource() == opnieuwButton)
			{
				DrawingPanel.diagramManager.clearDiagram(true);
				// reset to start flow diagram
				if (stategwt != null)
				{
					DrawingPanel.history.removeAllElements();
					DrawingPanel.diagramManager.recreateDiagram(stategwt);
					drawingPanel.paint();
				}
			}
    	}
    }
    
	
	public Widget asWidget()
	{
		return dlp;
	}
	
	/**
	 * get the state of the flow diagram
	 */
	public HashMap<String, Object> getState()
	{
		return DrawingPanel.diagramManager.copyDiagramToHashMap();
	}

	/**
	 * set the state of the flow diagram
	 */
	public void setState(HashMap<String, Object> h)
	{
		if(h == null || h.isEmpty())
			return;
		
		DrawingPanel.history.removeAllElements();
		DrawingPanel.diagramManager.recreateDiagram(h);
		drawingPanel.zetIsDemo(isDemo);
		drawingPanel.paint();

	}

	public int getScore()
	{
		return 0;
	}

	public Boolean isCorrect()
	{
		return correct;
	}

	/**
	 * sets communication root
	 */
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());
	}
	
	public void zetMode(int mode)
	{	
		this.mode = mode;
	}
	
	/**
	 * init method, reads the launch data, creates the GUI 
	 */
	public void init(int width, int height, Map<String, Object> map, Map<String, Number> values) 
	{
		logger.info("StroomDiagrammenGWT init");
		getImages();
		this.breedte = width;
		this.hoogte = height;
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		
		ObjectMap launchState = JSONUtilities.wrapMap(map);
		if ((launchState != null) && launchState.containsKey("toonBerekeningenMenu"))
			toonBerekeningenMenu = launchState.getBoolean("toonBerekeningenMenu");
		if ((launchState != null) && launchState.containsKey("berekenInBreuken"))
			berekenInBreuken = launchState.getBoolean("berekenInBreuken");
		if ((launchState != null) && launchState.containsKey("toonStroombreedteMenu"))
			toonStroombreedteMenu = launchState.getBoolean("toonStroombreedteMenu");
		if ((launchState != null) && launchState.containsKey("stroombreedteAbsoluut"))
			stroombreedteAbsoluut = launchState.getBoolean("stroombreedteAbsoluut");
		if ((launchState != null) && launchState.containsKey("toonOptiesMenu"))
			toonOptiesMenu = launchState.getBoolean("toonOptiesMenu");
		if ((launchState != null) && launchState.containsKey("toonLabels"))
			toonLabels = launchState.getBoolean("toonLabels");
		if ((launchState != null) && launchState.containsKey("aantalBronnen"))
			aantalBronnen = launchState.getInt("aantalBronnen");
		if ((launchState != null) && launchState.containsKey("isDemo"))
			isDemo = launchState.getBoolean("isDemo");
		// flow diagram in launch data
		if ((launchState != null) && launchState.containsKey("stategwt"))
			stategwt = (HashMap<String,Object>) launchState.getMap("stategwt");

		// add bottom panel if flow diagram is not a demo
		bottomPanel = new LayoutPanel();
		bottomPanel.addStyleName(stroomDiagrammenGWTCssResource.bottom());
		if (!isDemo)
			dlp.addSouth(bottomPanel, bottomHeight);
		else 
			bottomHeight = 0;
		// make menus and menubar and add the menubar if at least one menu is required
		if (toonBerekeningenMenu || toonStroombreedteMenu || toonOptiesMenu)
		{	makeMenus();
			dlp.addNorth(menuBar, menuHeight);
		}
		// create drawingPanel
		drawingPanel = new DrawingPanel(this, breedte, hoogte - bottomHeight); 
		// check if Canvas was created
		Canvas dpCanvas = drawingPanel.getCanvas();
		if (dpCanvas == null) 
		{
	      RootPanel.get().add(new Label(upgradeMessage));
	      return;
	    }
		dpCanvas.addStyleName(stroomDiagrammenGWTCssResource.canvas());
		// add the Canvas as the last item(!)
		dlp.add(dpCanvas);
		// put the buttons on the bottom panel if flow diagram is not a demo
		if (!isDemo)
			makeBottom();
		// force the layout to avoid the mouse-over syndrome in Stubview
		dlp.forceLayout();
		// set the flow diagram from the launch data
		if (stategwt != null)
		{	
			setState(stategwt);
		}
		// set flow mode to fractions (decimal numbers is default)
		if (berekenInBreuken)
			DrawingPanel.diagramManager.setFlowMode(DrawingPanel.fracMode);
		// set edge width to absolute (relative is default) 
		if (stroombreedteAbsoluut)
			DrawingPanel.diagramManager.setEdgeThicknessMode(DrawingPanel.absMode);
		// set flow diagram to demo or not
		drawingPanel.zetIsDemo(isDemo);
		drawingPanel.paint();
	}
	
	public void kijkNa() 
	{
	}

	public void zetVolledigeBreedte(int breedte) 
	{
		this.breedte = breedte;	
	}

	public int getAsHoogte() 
	{
		return asHoogte;
	}

	public int getHeight() 
	{
		return hoogte;
	}

	public int getWidth() 
	{
		return breedte;
	}

	public void setAsHoogte(int ashoogte) 
	{
		this.asHoogte = ashoogte;
	}

	public void zetNagekeken(boolean b) 
	{
	}

	public int[][] getScoreObjectives() 
	{
		return null;
	}

	/**
	 * perform the action corresponding to the (menu) String s
	 * @param s the menu String
	 */
	public void menuAction(String s)
	{
		// in case of a toggle, display the new menu choice in bold
		// decimal format for flow (see class Vertex)
		if (s.equals("decimaal"))
		{
			DrawingPanel.diagramManager.setFlowMode(DrawingPanel.decMode);
			decimaalItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());
			breukenItem.setStyleName(stroomDiagrammenGWTCssResource.normalmenuitem());
		}
		// fractions format for flow (see class Vertex) 
		else if (s.equals("breuken"))
		{
			DrawingPanel.diagramManager.setFlowMode(DrawingPanel.fracMode);
			decimaalItem.setStyleName(stroomDiagrammenGWTCssResource.normalmenuitem());
			breukenItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());		
		}
		// relative flow width (see class Edge)
		else if (s.equals("relatief"))
		{
			DrawingPanel.diagramManager.setEdgeThicknessMode(DrawingPanel.relMode);
			relatiefItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());
			absoluutItem.setStyleName(stroomDiagrammenGWTCssResource.normalmenuitem());		
		}
		// absolute flow width (see class Edge)
		else if (s.equals("absoluut"))
		{
			DrawingPanel.diagramManager.setEdgeThicknessMode(DrawingPanel.absMode);
			relatiefItem.setStyleName(stroomDiagrammenGWTCssResource.normalmenuitem());
			absoluutItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());		
		}
		// labels/no labels on the vertices
		else if (s.equals("labels"))
		{   // there are labels, remove then 
			if (labelsItem.getText().equals(rb.knooppuntenMetLabelsLabel()))
			{
				labelsItem.setText(rb.knooppuntenZonderLabelsLabel());
				DrawingPanel.diagramManager.setVertexLabels(true);
			}
			else // there are no labels, add them
			{
				labelsItem.setText(rb.knooppuntenMetLabelsLabel());
				DrawingPanel.diagramManager.setVertexLabels(false);
			}
			
		}
		// new root wanted
		else if (s.equals("root"))
		{
			drawingPanel.addNewRoot(true);		
		}
	}

	/**
	 * class translating a command String from a MenuItem 
	 * into the corresponding menu action
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
}
