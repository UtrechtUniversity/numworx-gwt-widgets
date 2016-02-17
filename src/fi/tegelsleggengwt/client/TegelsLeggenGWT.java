package fi.tegelsleggengwt.client;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

//import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
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

import fi.tegelsleggengwt.client.text.Text;

import java.util.logging.Logger;

public class TegelsLeggenGWT implements EntryPoint, InteractionStub 
{
	public static Text rb;
	
	private static Logger logger = Logger.getLogger("TegelsLeggenGWT");
	
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	// UI
	DockLayoutPanel dlp;
	LayoutPanel canvasPanel;
	Canvas tegelsCanvas;
	TegelsPanel tegelsPanel;
	ControlPanel2 cp;
	
	int buttonWidth = 40;
	int buttonHeight = 22;
	int pushSize = 24;

	int breedte = 500;
	int hoogte = 450;
	int bottomHeight = 32;
	int leftOffset = 5;
	int topOffset = 5;
	
	
//	CssColor bottomBgColor = CssColor.make(220, 220, 220);	
	
	TegelsLeggenGWTClientBundle tegelsLeggenGWTClientBundle;
	static TegelsLeggenGWTCssResource tegelsLeggenGWTCssResource;
	
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	// parametrisatie
	boolean demoVersion = false;
	boolean transVersion = false;
	List<Map<String,Object>> schuifStukkenList = new ArrayList<Map<String,Object>>();
	List<Map<String,Object>> basisVormenList = new ArrayList<Map<String,Object>>();
	
	
	ImageResource zwartResource, grijsResource, roodResource, oranjeResource, groenResource, 
				  cyaanResource, blauwResource, magentaResource, geelResource;
	Image zwartImage, grijsImage, roodImage, oranjeImage, groenImage, 
		  cyaanImage, blauwImage, magentaImage, geelImage;
	
	
	public void getImages()
	{
		rb = GWT.create(Text.class);
		
		tegelsLeggenGWTClientBundle = GWT.create(TegelsLeggenGWTClientBundle.class);
		tegelsLeggenGWTCssResource = tegelsLeggenGWTClientBundle.getTegelsLeggenGWTCssResource();
		tegelsLeggenGWTCssResource.ensureInjected();

		zwartResource = tegelsLeggenGWTClientBundle.zwartResource();
		zwartImage = new Image(zwartResource);
		zwartImage.addStyleName(tegelsLeggenGWTCssResource.pushimage2());

		grijsResource = tegelsLeggenGWTClientBundle.grijsResource();
		grijsImage = new Image(grijsResource);
		grijsImage.addStyleName(tegelsLeggenGWTCssResource.pushimage2());
		
		roodResource = tegelsLeggenGWTClientBundle.roodResource();
		roodImage = new Image(roodResource);
		roodImage.addStyleName(tegelsLeggenGWTCssResource.pushimage2());
		
		oranjeResource = tegelsLeggenGWTClientBundle.oranjeResource();
		oranjeImage = new Image(oranjeResource);
		oranjeImage.addStyleName(tegelsLeggenGWTCssResource.pushimage2());
		
		groenResource = tegelsLeggenGWTClientBundle.groenResource();
		groenImage = new Image(groenResource);
		groenImage.addStyleName(tegelsLeggenGWTCssResource.pushimage2());
		
		cyaanResource = tegelsLeggenGWTClientBundle.cyaanResource();
		cyaanImage = new Image(cyaanResource);
		cyaanImage.addStyleName(tegelsLeggenGWTCssResource.pushimage2());
		
		blauwResource = tegelsLeggenGWTClientBundle.blauwResource();
		blauwImage = new Image(blauwResource);
		blauwImage.addStyleName(tegelsLeggenGWTCssResource.pushimage2());

		magentaResource = tegelsLeggenGWTClientBundle.magentaResource();
		magentaImage = new Image(magentaResource);
		magentaImage.addStyleName(tegelsLeggenGWTCssResource.pushimage2());
		
		geelResource = tegelsLeggenGWTClientBundle.geelResource();
		geelImage = new Image(geelResource);
		geelImage.addStyleName(tegelsLeggenGWTCssResource.pushimage2());
		

	}
	
	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(tegelsLeggenGWTCssResource.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(tegelsLeggenGWTCssResource.root());
		
		Stub.publish(this);
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
				
	}
	
	public TegelsLeggenGWT()
	{

	}
	
	public TegelsLeggenGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
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
		dlp.addStyleName(tegelsLeggenGWTCssResource.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		
		init(breedte, hoogte, launchState, randomVarWaarden);


	}

	public void	makeBottom()
	{
		
//		canvasPanel.add(tegelsPanel.cp.draaiknop);
//		canvasPanel.setWidgetLeftWidth(cp.draaiknop, tegelsPanel.hokBreedte + 1, Style.Unit.PX, 
//									   80, Style.Unit.PX);
//		canvasPanel.setWidgetTopHeight(cp.draaiknop, hoogte - tegelsPanel.controlHoogte - 1, Style.Unit.PX, 
//									   20, Style.Unit.PX);
		
/*		
		if (demoVersion)
			return;
		
		int currentX = 185;
		int currentY = hoogte - topOffset - buttonHeight; 
		
		terugButton = new PushButton("terug");
		terugButton.addStyleName(tegelsLeggenGWTCssResource.pushbutton());
		canvasPanel.add(terugButton);
		canvasPanel.setWidgetLeftWidth(terugButton, currentX, Style.Unit.PX, buttonWidth + 5, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(terugButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
		terugButton.addClickHandler(new PushClickHandler());
		
		currentX += buttonWidth + 3 * leftOffset;		

		wisButton = new PushButton("wis");
		wisButton.addStyleName(tegelsLeggenGWTCssResource.pushbutton());
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
			
//			if (e.getSource() == tegelsPanel.cp.draaiknop)
//			{	tegelsPanel.draaiBasisvorm();
//System.out.println("draaiknop");			
//			}

    		
    	}
    }
    
	
	public Widget asWidget()
	{
		return dlp;
	}
	
	public HashMap<String,Object> getSchuifStukState(SchuifStuk ss)
	{
		
//System.out.println("getSSState");

		HashMap<String,Object> h = new HashMap<String,Object>();
		
		h.put("aantalPunten", new Integer(ss.aantalPunten));
		
//System.out.println("aantalPunten = " + ss.aantalPunten);

		ArrayList<Integer> puntenX = new ArrayList<Integer>();
		for (int pCnt = 0; pCnt < ss.punten.length; pCnt++)
			puntenX.add(new Integer(ss.punten[pCnt].x));
		h.put("puntenX", puntenX);
		ArrayList<Integer> puntenY = new ArrayList<Integer>();
		for (int pCnt = 0; pCnt < ss.punten.length; pCnt++)
			puntenY.add(new Integer(ss.punten[pCnt].y));
		h.put("puntenY", puntenY);
		//h.put("kleur", new String("rgb(" + ss.kleur.getRed()+ "," + ss.kleur.getGreen() + "," + ss.kleur.getBlue() + ")"));
		h.put("kleur",ss.kleur.value());
		h.put("positieX", new Integer(ss.positie.x));
		h.put("positieY", new Integer(ss.positie.y));
		
		return h;
	}

	@Override
	public HashMap<String, Object> getState()
	{
		HashMap<String,Object> h = new HashMap<String,Object>();
		
		ArrayList<HashMap> schuifStukkenList = new ArrayList<HashMap>();
		for (int sCnt = 0; sCnt < tegelsPanel.aantalSs; sCnt++)
		{	schuifStukkenList.add(getSchuifStukState(tegelsPanel.ss[sCnt]));
		}
		h.put("schuifStukkenList", schuifStukkenList);
		
		ArrayList<HashMap> basisVormenList = new ArrayList<HashMap>();
		for (int bCnt = 0; bCnt < tegelsPanel.basisVormen.size(); bCnt++)
		{	basisVormenList.add(getSchuifStukState((SchuifStuk)tegelsPanel.basisVormen.elementAt(bCnt)));
		}
		h.put("basisVormenList", basisVormenList);

		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		ObjectMap state = JSONUtilities.wrapMap(h);
		
		if (state.containsKey("schuifStukkenList"))
			schuifStukkenList = state.getMapList("schuifStukkenList");
		if (state.containsKey("basisVormenList"))
			basisVormenList = state.getMapList("basisVormenList");
		
		tegelsPanel.aantalSs = schuifStukkenList.size();

		for (int sCnt = 0; sCnt < tegelsPanel.aantalSs; sCnt++)
		{	Map ssMap = schuifStukkenList.get(sCnt);
			ObjectMap ssOMap = JSONUtilities.wrapMap(ssMap);
		
			int aantalPunten = 0;
			List<Integer> puntenXList = new ArrayList<Integer>();
			int[] puntenX;
			List<Integer> puntenYList = new ArrayList<Integer>();
			int[] puntenY;
			Point[] punten;
			String kleurString = "";
			CssColor kleur;
			int positieX = 0;
			int positieY = 0;
			Point positie;
			if (ssOMap.containsKey("aantalPunten"))
			{	aantalPunten = ssOMap.getInt("aantalPunten");
			}
			puntenX = new int[aantalPunten];
			puntenY = new int[aantalPunten];
			if (ssOMap.containsKey("puntenX"))
			{	puntenXList = ssOMap.getIntegerList("puntenX");
				for (int xCnt = 0; xCnt < aantalPunten; xCnt++)
					puntenX[xCnt] = puntenXList.get(xCnt).intValue();
			}
			if (ssOMap.containsKey("puntenY"))
			{	puntenYList = ssOMap.getIntegerList("puntenY");
				for (int yCnt = 0; yCnt < aantalPunten; yCnt++)
					puntenY[yCnt] = puntenYList.get(yCnt).intValue(); 
			}
			punten = new Point[aantalPunten];
			for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
				punten[pCnt] = new Point(puntenX[pCnt], puntenY[pCnt]);
			
			if (ssOMap.containsKey("kleur"))
				kleurString = ssOMap.getString("kleur");
			
			kleur = CssColor.make(kleurString);
			
			if (ssOMap.containsKey("positieX"))
				positieX = ssOMap.getInt("positieX");
			if (ssOMap.containsKey("positieY"))
				positieY = ssOMap.getInt("positieY");
			positie = new Point(positieX,positieY);
			
			//tegelsPanel.ss[sCnt] = (SchuifStuk) schuifStukkenVector.elementAt(sCnt);
			tegelsPanel.ss[sCnt] = new SchuifStuk(transVersion, aantalPunten, punten, positie, kleur);
		}


		tegelsPanel.basisVormen = new Vector();
		
		int aantalBv = basisVormenList.size();

		for (int sCnt = 0; sCnt < aantalBv; sCnt++)
		{	Map bvMap = basisVormenList.get(sCnt);
			ObjectMap bvOMap = JSONUtilities.wrapMap(bvMap);
		
			int aantalPunten = 0;
			List<Integer> puntenXList = new ArrayList<Integer>();
			int[] puntenX;
			List<Integer> puntenYList = new ArrayList<Integer>();
			int[] puntenY;
			Point[] punten;
			String kleurString = "";
			CssColor kleur;
			int positieX = 0;
			int positieY = 0;
			Point positie;
			if (bvOMap.containsKey("aantalPunten"))
			{	aantalPunten = bvOMap.getInt("aantalPunten");
			}
			puntenX = new int[aantalPunten];
			puntenY = new int[aantalPunten];
			if (bvOMap.containsKey("puntenX"))
			{	puntenXList = bvOMap.getIntegerList("puntenX");
				for (int xCnt = 0; xCnt < aantalPunten; xCnt++)
					puntenX[xCnt] = puntenXList.get(xCnt).intValue();
			}
			if (bvOMap.containsKey("puntenY"))
			{	puntenYList = bvOMap.getIntegerList("puntenY");
				for (int yCnt = 0; yCnt < aantalPunten; yCnt++)
					puntenY[yCnt] = puntenYList.get(yCnt).intValue(); 
			}
			punten = new Point[aantalPunten];
			for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
				punten[pCnt] = new Point(puntenX[pCnt], puntenY[pCnt]);
			
			if (bvOMap.containsKey("kleur"))
				kleurString = bvOMap.getString("kleur");
			
			kleur = CssColor.make(kleurString);
			
			if (bvOMap.containsKey("positieX"))
				positieX = bvOMap.getInt("positieX");
			if (bvOMap.containsKey("positieY"))
				positieY = bvOMap.getInt("positieY");
			positie = new Point(positieX,positieY);
			
			SchuifStuk basisVorm = new SchuifStuk(transVersion, aantalPunten, punten, positie, kleur);
			tegelsPanel.basisVormen.addElement(basisVorm);
		}
		
		if (tegelsPanel.basisVormen.size() >= 1)
		{
			tegelsPanel.zetBasisVorm((SchuifStuk) tegelsPanel.basisVormen.elementAt(0));
			if (tegelsPanel.basisVormen.size() > 1)
				cp.downButton.setEnabled(true);
		}
		
		tegelsPanel.tekenOpnieuw();
		


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
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		// TODO Auto-generated method stub

	}
	
	public void init(int width, int height, Map<String, Object> map, //launchState,
			Map<String, Number> values) 
	{
		
// launchdata/state		
logger.info("TegelsLeggenGWT init");		
		
		this.breedte = width;
		this.hoogte = height;
		//this.launchState = launchState;
		
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		
		ObjectMap launchState = JSONUtilities.wrapMap(map);
		
		if (launchState.containsKey("demoVersion"))
			demoVersion = launchState.getBoolean("demoVersion");
		if (launchState.containsKey("transVersion"))
			transVersion = launchState.getBoolean("transVersion");
		if (launchState.containsKey("schuifStukkenList"))
			schuifStukkenList = launchState.getMapList("schuifStukkenList");
		if (launchState.containsKey("basisVormenList"))
			basisVormenList = launchState.getMapList("basisVormenList");
				
		dlp.setSize(breedte + "px", hoogte + "px");
		
		canvasPanel = new LayoutPanel();
		dlp.add(canvasPanel);

		tegelsPanel = new TegelsPanel(breedte, hoogte, this, transVersion, demoVersion);
		
		if (tegelsPanel.tegelsCanvas == null) 
		{
			RootPanel.get(holderId).add(new Label(upgradeMessage));
			return;
	    }
		
		tegelsPanel.tegelsCanvas.addStyleName(tegelsLeggenGWTCssResource.canvas());
		
		canvasPanel.add(tegelsPanel.tegelsCanvas);
		canvasPanel.setWidgetLeftWidth(tegelsPanel.tegelsCanvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(tegelsPanel.tegelsCanvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);

		cp = new ControlPanel2(this, tegelsPanel);
		cp.addStyleName(tegelsLeggenGWTCssResource.bottom());

		canvasPanel.add(cp);
		canvasPanel.setWidgetLeftWidth(cp, tegelsPanel.hokBreedte + 1, Style.Unit.PX, 
									   breedte - tegelsPanel.hokBreedte - 2, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(cp, hoogte - tegelsPanel.controlHoogte, Style.Unit.PX, 
									   tegelsPanel.controlHoogte - 1, Style.Unit.PX);

		if (!demoVersion)
		{
			cp.initialize();
		}
		else
		{
			canvasPanel.setWidgetVisible(cp,false);
		}

		
		tegelsPanel.aantalSs = schuifStukkenList.size();

		for (int sCnt = 0; sCnt < tegelsPanel.aantalSs; sCnt++)
		{	Map ssMap = schuifStukkenList.get(sCnt);
			ObjectMap ssOMap = JSONUtilities.wrapMap(ssMap);
		
			int aantalPunten = 0;
			List<Integer> puntenXList = new ArrayList<Integer>();
			int[] puntenX;
			List<Integer> puntenYList = new ArrayList<Integer>();
			int[] puntenY;
			Point[] punten;
			String kleurString = "";
			CssColor kleur;
			int positieX = 0;
			int positieY = 0;
			Point positie;
			if (ssOMap.containsKey("aantalPunten"))
			{	aantalPunten = ssOMap.getInt("aantalPunten");
			}
			puntenX = new int[aantalPunten];
			puntenY = new int[aantalPunten];
			if (ssOMap.containsKey("puntenX"))
			{	puntenXList = ssOMap.getIntegerList("puntenX");
				for (int xCnt = 0; xCnt < aantalPunten; xCnt++)
					puntenX[xCnt] = puntenXList.get(xCnt).intValue();
			}
			if (ssOMap.containsKey("puntenY"))
			{	puntenYList = ssOMap.getIntegerList("puntenY");
				for (int yCnt = 0; yCnt < aantalPunten; yCnt++)
					puntenY[yCnt] = puntenYList.get(yCnt).intValue(); 
			}
			punten = new Point[aantalPunten];
			for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
				punten[pCnt] = new Point(puntenX[pCnt], puntenY[pCnt]);
			
			if (ssOMap.containsKey("kleur"))
				kleurString = ssOMap.getString("kleur");
			
			kleur = CssColor.make(kleurString);
			
			if (ssOMap.containsKey("positieX"))
				positieX = ssOMap.getInt("positieX");
			if (ssOMap.containsKey("positieY"))
				positieY = ssOMap.getInt("positieY");
			positie = new Point(positieX,positieY);
			
			//tegelsPanel.ss[sCnt] = (SchuifStuk) schuifStukkenVector.elementAt(sCnt);
			tegelsPanel.ss[sCnt] = new SchuifStuk(transVersion, aantalPunten, punten, positie, kleur);
		}


		tegelsPanel.basisVormen = new Vector();
		
		int aantalBv = basisVormenList.size();

		for (int sCnt = 0; sCnt < aantalBv; sCnt++)
		{	Map bvMap = basisVormenList.get(sCnt);
			ObjectMap bvOMap = JSONUtilities.wrapMap(bvMap);
		
			int aantalPunten = 0;
			List<Integer> puntenXList = new ArrayList<Integer>();
			int[] puntenX;
			List<Integer> puntenYList = new ArrayList<Integer>();
			int[] puntenY;
			Point[] punten;
			String kleurString = "";
			CssColor kleur;
			int positieX = 0;
			int positieY = 0;
			Point positie;
			if (bvOMap.containsKey("aantalPunten"))
			{	aantalPunten = bvOMap.getInt("aantalPunten");
			}
			puntenX = new int[aantalPunten];
			puntenY = new int[aantalPunten];
			if (bvOMap.containsKey("puntenX"))
			{	puntenXList = bvOMap.getIntegerList("puntenX");
				for (int xCnt = 0; xCnt < aantalPunten; xCnt++)
					puntenX[xCnt] = puntenXList.get(xCnt).intValue();
			}
			if (bvOMap.containsKey("puntenY"))
			{	puntenYList = bvOMap.getIntegerList("puntenY");
				for (int yCnt = 0; yCnt < aantalPunten; yCnt++)
					puntenY[yCnt] = puntenYList.get(yCnt).intValue(); 
			}
			punten = new Point[aantalPunten];
			for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
				punten[pCnt] = new Point(puntenX[pCnt], puntenY[pCnt]);
			
			if (bvOMap.containsKey("kleur"))
				kleurString = bvOMap.getString("kleur");
			
			kleur = CssColor.make(kleurString);
			
			if (bvOMap.containsKey("positieX"))
				positieX = bvOMap.getInt("positieX");
			if (bvOMap.containsKey("positieY"))
				positieY = bvOMap.getInt("positieY");
			positie = new Point(positieX,positieY);
			
			SchuifStuk basisVorm = new SchuifStuk(transVersion, aantalPunten, punten, positie, kleur);
			tegelsPanel.basisVormen.addElement(basisVorm);
		}
		
		if (tegelsPanel.basisVormen.size() >= 1)
		{
			tegelsPanel.zetBasisVorm((SchuifStuk) tegelsPanel.basisVormen.elementAt(0));
			if (tegelsPanel.basisVormen.size() > 1)
				cp.downButton.setEnabled(true);
		}
		
		tegelsPanel.tekenOpnieuw();

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

}
