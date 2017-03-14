package fi.stippelpatronengwt.client;

import java.util.HashMap;
import java.util.Map;



import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
//import nl.uu.fi.dwo.interaction.client.event.CBookEventHandler;



import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.resources.client.ImageResource;

import java.util.logging.Logger;

public class StippelPatronenGWT implements EntryPoint, InteractionStub, InteractionView, CBookEventListener 
{
	private static Logger logger = Logger.getLogger("StippelPatronenGWT");
	
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	// UI
	DockLayoutPanel dlp;
	LayoutPanel canvasPanel;
	Canvas stippelPatronenGWTCanvas;
	DrawingContainer drawCon;
	
	int buttonWidth = 40;
	int buttonHeight = 22;
	int pushSize = 24;

	int breedte = 500;
	int hoogte = 450;
	int bottomHeight = 32;
	int leftOffset = 5;
	int topOffset = 5;
	
	//PushButton terugButton, wisButton;
		
//	CssColor bottomBgColor = CssColor.make(220, 220, 220);	
	
	StippelPatronenGWTClientBundle stippelPatronenGWTClientBundle;
	StippelPatronenGWTCssResource stippelPatronenGWTCssResource;
	ImageResource foutKruisResource, goedKrulResource,goedKrulHalfResource;
	Image foutKruisImage, goedKrulImage,goedKrulHalfImage;

	
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	String[][] antwoorden = 
	{
		{"2n+1", "4n+1", "n^2",	   "4n",		 "4n+1",		"3n+1",	"4n+2",   "4n",		     "4n+3", "5n+2", "4n+1", "2n+2"},	
		{"4n-3", "2n+2", "n(n+1)", "n(n+1)/2",	 "n(n+1)/2",	"n^2",	"6n^2",   "n^2+4",       "2n^2", "8n+1", "n^2",  "3n+6"} ,
		{"4n^2", "6n+3", "8n+3",   "(3n^2-n)/2", "(n^2+n+2)/2",	"2^n",	"4n^2+2", "n^2+(n-1)^2", "n^3",  "2n^3", "3^n",  "(2n^3+3n^2+n)/6"}
	};
	
	// parametrisatie	
	int level = 1;
	int level1Keuze = 1;
	int level2Keuze = 1;
	int level3Keuze = 1;
	
	boolean correct = true;
	
    boolean ingevuld;
	private boolean nagekeken;
	private int mode;
	private OpdrNavIF comRoot;
	
	boolean kijkNaActief = true;
	int score = 0;
	int scoreMax = 10;

	
	public void getImages()
	{
		stippelPatronenGWTClientBundle = GWT.create(StippelPatronenGWTClientBundle.class);
		stippelPatronenGWTCssResource = stippelPatronenGWTClientBundle.getStippelPatronenGWTCssResource();
		stippelPatronenGWTCssResource.ensureInjected();

		foutKruisResource = stippelPatronenGWTClientBundle.foutKruisResource();
		goedKrulResource = stippelPatronenGWTClientBundle.goedKrulResource();
		goedKrulHalfResource = stippelPatronenGWTClientBundle.goedKrulHalfResource();
		foutKruisImage = new Image(foutKruisResource);
		goedKrulImage = new Image(goedKrulResource);
		goedKrulHalfImage = new Image(goedKrulHalfResource);
	}
	
	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(stippelPatronenGWTCssResource.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(stippelPatronenGWTCssResource.root());
		
		Stub.publish(this);
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
				
	}
	
	public StippelPatronenGWT()
	{

	}
	
	public StippelPatronenGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
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
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(stippelPatronenGWTCssResource.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		
		init(breedte, hoogte, launchState, randomVarWaarden);


	}

	public void	makeBottom()
	{
/*		
		int currentX = 185;
		int currentY = hoogte - topOffset - buttonHeight; 
		
		terugButton = new PushButton("terug");
		terugButton.addStyleName(stippelPatronenGWTCssResource.pushbutton());
		canvasPanel.add(terugButton);
		canvasPanel.setWidgetLeftWidth(terugButton, currentX, Style.Unit.PX, buttonWidth + 5, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(terugButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
		terugButton.addClickHandler(new PushClickHandler());
		
		currentX += buttonWidth + 3 * leftOffset;		

		wisButton = new PushButton("wis");
		wisButton.addStyleName(stippelPatronenGWTCssResource.pushbutton());
		canvasPanel.add(wisButton);
		canvasPanel.setWidgetLeftWidth(wisButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(wisButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
		wisButton.addClickHandler(new PushClickHandler());

		currentX += buttonWidth + 3 * leftOffset;		
*/		
	}

    class PushClickHandler implements ClickHandler
    {
    	
    	public void onClick(ClickEvent e)
    	{
			//e.preventDefault();
			e.stopPropagation();
/*			
			if (e.getSource() == terugButton)
    		{
    			//tekenPanel.tekenStapTerug();
    			    			
    		}
    		else if (e.getSource() == wisButton)
    		{
    			//tekenPanel.wis();
    		}
*/    		
    		
    	}
    }
    
	
	public Widget asWidget()
	{
		return dlp;
	}
	
	@Override
	public HashMap<String, Object> getState()
	{
		return null; //tekenPanel.getState();
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		//tekenPanel.setState(h);

	}

	@Override
	public int getScore()
	{	return score;
	}

	@Override
	public Boolean isCorrect()
	{
		if (kijkNaActief)
			return correct;
		else
			return new Boolean(true);
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		
System.out.println("setComRoot");

		this.comRoot = comRoot;
		zetMode(comRoot.getMode());
		comRoot.addCBookEventListener("action.showAllPatterns", this);
	}
	
	public void zetMode(int mode)
	{	this.mode = mode;
		if (kijkNaActief)    
			kijkNaActief = (mode == 0 || mode == 1);
	}

	public void zetLevel(int level)
	{	this.level = level;
		if (level == 1)
		{	zetLevel1Keuze(level1Keuze);
		}
		else if (level == 2)
		{	zetLevel2Keuze(level2Keuze);
			
		}
		else if (level == 3)
		{	zetLevel3Keuze(level3Keuze);
			
		}
			
	}
	
	public void zetLevel1Keuze(int l1Keuze)
	{	level1Keuze = l1Keuze;
		if (drawCon != null)
			drawCon.initDWOProblem((level - 1) * 100 + level1Keuze - 1);
		//if (antwoordVak != null)
		//	antwoordVak.zetJuisteAntwoord("$f" + Spot_Problems_dwo.rb.getString("aantalTekst") + "=" + 
		//							  	  antwoorden[level - 1][level1Keuze - 1] + "@");
		
	}
	
	public void zetLevel2Keuze(int l2Keuze)
	{	level2Keuze = l2Keuze;
		if (drawCon != null)
			drawCon.initDWOProblem((level - 1) * 100 + level2Keuze - 1);
		//if (antwoordVak != null)
		//	antwoordVak.zetJuisteAntwoord("$f" + Spot_Problems_dwo.rb.getString("aantalTekst") + "=" + 
		//								  antwoorden[level - 1][level2Keuze - 1] + "@");
		
	}
	
	public void zetLevel3Keuze(int l3Keuze)
	{	level3Keuze = l3Keuze;
		if (drawCon != null)
			drawCon.initDWOProblem((level - 1) * 100 + level3Keuze - 1);
		//if (antwoordVak != null)
		//	antwoordVak.zetJuisteAntwoord("$f" + Spot_Problems_dwo.rb.getString("aantalTekst") + "=" + 
		//		                          antwoorden[level - 1][level3Keuze - 1] + "@");
		
	}
	
	public void zetKijkNaActief(boolean b)
	{
		kijkNaActief = b;
		//if (antwoordVak != null)
		//	antwoordVak.zetKijkNaActief(kijkNaActief);
		
	}

	public void init(int width, int height, Map<String, Object> map, //launchState,
			Map<String, Number> values) 
	{
		
// launchdata/state		
logger.info("StippelPatronenGWT init");		
		
		this.breedte = width;
		this.hoogte = height;
		//this.launchState = launchState;
		
		dlp.setPixelSize(breedte, hoogte);
		
		ObjectMap launchState = JSONUtilities.wrapMap(map);

		int level = 3;
		int level1Keuze = 1;
		int level2Keuze = 4;
		int level3Keuze = 7;

		if (launchState != null && launchState.containsKey("level"))
			level = launchState.getInt("level");
		if (launchState != null && launchState.containsKey("level1Keuze"))
			level1Keuze = launchState.getInt("level1Keuze");
		if (launchState != null && launchState.containsKey("level2Keuze"))
			level2Keuze = launchState.getInt("level2Keuze");
		if (launchState != null && launchState.containsKey("level3Keuze"))
			level3Keuze = launchState.getInt("level3Keuze");
		

//if (level == 1)		
//System.out.println("antwoord = " + antwoorden[level - 1][level1Keuze - 1]);
//else if (level == 1)		
//System.out.println("antwoord = " + antwoorden[level - 1][level2Keuze - 1]);
//else if (level == 1)		
//System.out.println("antwoord = " + antwoorden[level - 1][level3Keuze - 1]);

		boolean kijkNaActief = true;
		if (launchState != null && launchState.containsKey("kijkNaActief"))
			kijkNaActief = launchState.getBoolean("kijkNaActief");
		zetKijkNaActief(kijkNaActief);
		
		
		canvasPanel = new LayoutPanel();
		dlp.add(canvasPanel);

		//drawCon = new DrawingContainer(this, breedte, hoogte - bottomHeight);
		drawCon = new DrawingContainer(this, breedte, hoogte);

		Canvas stippelCanvas = drawCon.getCanvas();
		if (stippelCanvas == null) 
		{
	      RootPanel.get().add(new Label(upgradeMessage));
	      return;
	    }
		
		stippelCanvas.addStyleName(stippelPatronenGWTCssResource.canvas());
		drawCon.initContext2d();		

		drawCon.initialize();

		zetLevel1Keuze(level1Keuze);
		zetLevel2Keuze(level2Keuze);
		zetLevel3Keuze(level3Keuze);
		// HIER
		zetLevel(level);

		canvasPanel.add(stippelCanvas);
		canvasPanel.setWidgetLeftWidth(stippelCanvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		//canvasPanel.setWidgetTopHeight(stippelCanvas, 0, Style.Unit.PX, hoogte - bottomHeight, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(stippelCanvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);

		
		makeBottom();
		//tekenPanel.setState(map);

		drawCon.paint();

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

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return hoogte;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
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
	
	public String[] getAcceptedCmds() 
	{
System.out.println("getAcceptedCmds");		
		String[] commands = {"action.showAllPatterns"};
		return commands;
	}

	public String getLocalizedCmd(String cmd) 
	{
System.out.println("getLocalizedCmd");
		String localizedCmd = null; //NabouwenAanzichten.rb.getString(CBA_PREFIX + cmd);
		if (localizedCmd == null)
			return cmd;
		return localizedCmd;
	}

	//@Override
	public void acceptCBookEvent(CBookEvent event) 
	{
		String command = event.getCommand();
		
System.out.println("command = " + command);

		if(command.startsWith("action.showAllPatterns"))
		{	drawCon.showAllPatterns();
		}
	}


}
