package fi.stroomdiagrammengwt.client;

//import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

import com.google.gwt.resources.client.ImageResource;

import fi.stroomdiagrammengwt.client.text.Text;

import java.util.logging.Logger;

public class StroomDiagrammenGWT implements EntryPoint, InteractionStub 
{
	public static Text rb;
	
	// logger
    static Logger logger = Logger.getLogger("StroomDiagrammenGWT");
	
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	private int mode;
	private OpdrNavIF comRoot;
	boolean correct;

	// UI
	DockLayoutPanel dlp;
	LayoutPanel bottomPanel;
	LayoutPanel topPanel;
	DrawingPanel drawingPanel;

	PushButton opnieuwButton, terugButton;
	
	int buttonWidth1 = 50;
	int buttonWidth2 = 100;
	int buttonHeight = 25;

	int breedte = 500;
	int hoogte = 450;
	int bottomHeight = 35;
	int leftOffset = 5;
	int topOffset = 5;
	
	int menuHeight = 25;
	
//	CssColor bottomBgColor = CssColor.make(220, 220, 220);	
	
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	MenuBar menuBar;
	MenuBar berekeningenMenu, stroombreedteMenu, optiesMenu;
	
	MenuItem decimaalItem, breukenItem;
	MenuItem relatiefItem, absoluutItem;
	MenuItem labelsItem, addRootItem;
		
	   // parametrisatie
    boolean toonBerekeningenMenu = true;
    boolean berekenInBreuken = false;
    
    boolean toonStroombreedteMenu = true;
    boolean stroombreedteAbsoluut = false;
    
    boolean toonOptiesMenu = true;
    boolean toonLabels = false;
    int aantalBronnen = 1;
    
    boolean isDemo = false;
	
    // modes for showing flows
    public static int decMode = 0;
    public static int percMode = 1;
    public static int fracMode = 2;
    // decimals for capacities
    public static int capDecs = 2;
    // modes for showing edge thickness
    public static int relMode = 0;
    public static int absMode = 1;
    
    boolean breuken = false;
    boolean absoluut = false;
    int numRoots = 1;
    boolean labels = false;
    DiagramCopy diagramCopy = null;
    
    public static CssColor appletBackground = CssColor.make(222, 222, 222);
   	public static CssColor workBackground = CssColor.make(255,255,255);
   	public static CssColor edgeColor = CssColor.make(0,255,255); 
   	public static CssColor highEdgeColor = CssColor.make(198, 239, 140);
   	public static CssColor zeroEdgeColor = CssColor.make(220,220,220);
	public static CssColor bubbleColor = CssColor.make(0,155,155); 
	public static CssColor bubbleColor1 = CssColor.make(0,175,175);
	public static CssColor bubbleColor2 = CssColor.make(0,195,195);
   	public static CssColor highBubbleColor = CssColor.make(198, 239, 140);
   	public static CssColor highBubbleColor1 = CssColor.make(218, 255, 160);
   	public static CssColor highBubbleColor2 = CssColor.make(238, 2255, 180);
   	public static CssColor buttonColor = CssColor.make(255,255,0); //Color.lightGray;

   	StroomDiagrammenGWTClientBundle stroomDiagrammenGWTClientBundle;
   	StroomDiagrammenGWTCssResource stroomDiagrammenGWTCssResource;
   	
	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(stroomDiagrammenGWTCssResource.root());
		
		//Stub.publish(this);
		init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
		
		
	}
	
	public void getImages()
	{
		rb = GWT.create(Text.class);
		
		stroomDiagrammenGWTClientBundle = GWT.create(StroomDiagrammenGWTClientBundle.class);
		stroomDiagrammenGWTCssResource = stroomDiagrammenGWTClientBundle.getStroomDiagrammenGWTCssResource();
		stroomDiagrammenGWTCssResource.ensureInjected();

	}
	
	public StroomDiagrammenGWT()
	{
		//this(null, null, null);
	}
	
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

	public void makeMenus()
	{
		menuBar = new MenuBar();
		menuBar.addStyleName(stroomDiagrammenGWTCssResource.menubar());
		
		berekeningenMenu = new MenuBar(true);
		decimaalItem = new MenuItem("decimaal",new MenuCommand("decimaal"));
		breukenItem = new MenuItem("breuken",new MenuCommand("breuken"));
		
		if (berekenInBreuken)
		{	breukenItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());
		}
		else
		{	decimaalItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());
		}
		berekeningenMenu.addItem(decimaalItem);
		berekeningenMenu.addItem(breukenItem);

		stroombreedteMenu = new MenuBar(true);
		relatiefItem = new MenuItem("relatief",new MenuCommand("relatief"));
		absoluutItem = new MenuItem("absoluut",new MenuCommand("absoluut"));
		
		if (stroombreedteAbsoluut)
		{	absoluutItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());
		}
		else
		{	relatiefItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());
		}
		
		
		stroombreedteMenu.addItem(relatiefItem);
		stroombreedteMenu.addItem(absoluutItem);
		
		optiesMenu = new MenuBar(true);
		
		labelsItem = new MenuItem("knooppunten met labels",new MenuCommand("labels"));
		addRootItem = new MenuItem("voeg extra bron toe",new MenuCommand("root"));
		
		if (toonLabels)
		{	labelsItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());
		}

		optiesMenu.addItem(labelsItem);
		optiesMenu.addItem(addRootItem);
		
		if (toonBerekeningenMenu)
			menuBar.addItem("berekeningen", berekeningenMenu);
		if (toonStroombreedteMenu)
			menuBar.addItem("stroombreedte", stroombreedteMenu);
		if (toonOptiesMenu)
			menuBar.addItem("opties", optiesMenu);
		
		
	}	

	public void	makeBottom()
	{
		int currentX = leftOffset;
		int currentY = topOffset;

		opnieuwButton = new PushButton("nieuw diagram");
		opnieuwButton.addStyleName(stroomDiagrammenGWTCssResource.pushbutton());
		bottomPanel.add(opnieuwButton);
		bottomPanel.setWidgetLeftWidth(opnieuwButton, currentX, Style.Unit.PX, buttonWidth2, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(opnieuwButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
		opnieuwButton.addClickHandler(new PushClickHandler());
		
		currentX += buttonWidth2 + 2 * leftOffset;		

		terugButton = new PushButton("terug");
		terugButton.addStyleName(stroomDiagrammenGWTCssResource.pushbutton());
		bottomPanel.add(terugButton);
		bottomPanel.setWidgetLeftWidth(terugButton, currentX, Style.Unit.PX, buttonWidth1, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(terugButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
		terugButton.addClickHandler(new PushClickHandler());
		
		currentX += buttonWidth1 + 2 * leftOffset;		

		
	}

    class PushClickHandler implements ClickHandler
    {
    	
    	public void onClick(ClickEvent e)
    	{
			//e.preventDefault();
			e.stopPropagation();
    		
			if (e.getSource() == terugButton)
			{
				drawingPanel.previousDiagram();
        		if (drawingPanel.history.size() <= 1)
        			terugButton.setEnabled(false);
			}
			else if (e.getSource() == opnieuwButton)
			{
				drawingPanel.diagramManager.clearDiagram(true);
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
	public Boolean isCorrect()
	{
		// TODO Auto-generated method stub
		return correct;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		// TODO Auto-generated method stub

	}
	
	public void init(int width, int height, Map<String, Object> map, Map<String, Number> values) 
	{
logger.info("StroomDiagrammenGWT init");
System.out.println("StroomDiagrammenGWT init");

		getImages();
		
		this.breedte = width;
		this.hoogte = height;
		//this.launchState = launchState;
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		
		ObjectMap launchState = JSONUtilities.wrapMap(map);

	    // parametrisatie
	    boolean toonBerekeningenMenu = true;
	    boolean berekenInBreuken = false;
	    
	    boolean toonStroombreedteMenu = true;
	    boolean stroombreedteAbsoluut = false;
	    
	    boolean toonOptiesMenu = true;
	    boolean toonLabels = false;
	    int aantalBronnen = 1;
	    
	    boolean isDemo = false;

		int score = 0;
		int scoreMax = 0;

		if ((launchState != null) && launchState.containsKey("toonBerekeningenMenu"))
			toonBerekeningenMenu = launchState.getBoolean("toonBerekeningenMenu");
			
		
		
		
		
		
		bottomPanel = new LayoutPanel();
		bottomPanel.addStyleName(stroomDiagrammenGWTCssResource.bottom());

		dlp.addSouth(bottomPanel, bottomHeight);
		
		if (toonBerekeningenMenu || toonStroombreedteMenu || toonOptiesMenu)
		{	makeMenus();
			dlp.addNorth(menuBar, menuHeight);
		}
		
		drawingPanel = new DrawingPanel(this, breedte, hoogte - bottomHeight); 

		Canvas dpCanvas = drawingPanel.getCanvas();
		if (dpCanvas == null) 
		{
	      RootPanel.get().add(new Label(upgradeMessage));
	      return;
	    }
		
		dpCanvas.addStyleName(stroomDiagrammenGWTCssResource.canvas());
		
	
		dlp.add(dpCanvas);

		
		makeBottom();
		
		//drawingPanel.paint();

	}
	
	public void kijkNa() 
	{
		
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
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		// TODO Auto-generated method stub
		
	}
	
	//@Override
	public void zetNagekeken(boolean b) 
	{
	}

	
	//@Override
	public int[][] getScoreObjectives() 
	{
		return null;
	}

	public void menuAction(String s)
	{
		
		if (s.equals("decimaal"))
		{
			drawingPanel.diagramManager.setFlowMode(DrawingPanel.decMode);
			decimaalItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());
			breukenItem.setStyleName(stroomDiagrammenGWTCssResource.normalmenuitem());
		}
		else if (s.equals("breuken"))
		{
			drawingPanel.diagramManager.setFlowMode(DrawingPanel.fracMode);
			decimaalItem.setStyleName(stroomDiagrammenGWTCssResource.normalmenuitem());
			breukenItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());		
		}
		else if (s.equals("relatief"))
		{
			drawingPanel.diagramManager.setEdgeThicknessMode(DrawingPanel.relMode);
			relatiefItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());
			absoluutItem.setStyleName(stroomDiagrammenGWTCssResource.normalmenuitem());		
		}
		else if (s.equals("absoluut"))
		{
			drawingPanel.diagramManager.setEdgeThicknessMode(DrawingPanel.absMode);
			relatiefItem.setStyleName(stroomDiagrammenGWTCssResource.normalmenuitem());
			absoluutItem.setStyleName(stroomDiagrammenGWTCssResource.boldmenuitem());		
		}
		
		else if (s.equals("labels"))
		{
			if (labelsItem.getText().equals("knooppunten met labels"))
			{
				
System.out.println("met");				
				labelsItem.setText("knooppunten zonder labels");
				drawingPanel.diagramManager.setVertexLabels(true);
				
			}
			else 
			{
System.out.println("zonder");				
				labelsItem.setText("knooppunten met labels");
				drawingPanel.diagramManager.setVertexLabels(false);
				
			}
			
		}
		else if (s.equals("root"))
		{
			drawingPanel.addNewRoot(true);		
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


}
