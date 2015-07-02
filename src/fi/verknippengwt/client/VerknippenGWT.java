package fi.verknippengwt.client;

//import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;


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


public class VerknippenGWT implements EntryPoint, InteractionStub, InteractionView 
{
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	VerknippenGWTClientBundle verknippenGWTClientBundle;
	VerknippenGWTCssResource verknippenGWTCssResource;

	// UI
	DockLayoutPanel dlp;
	LayoutPanel bottomPanel;
	DrawingPanel2 dp2; 
	
	int buttonWidth = 40;
	int buttonHeight = 22;

	int breedte = 500;
	int hoogte = 450;
	int bottomHeight = 32;
	int leftOffset = 5;
	int topOffset = 5;
	
//	CssColor bottomBgColor = CssColor.make(220, 220, 220);	
	
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	// parametrisatie
	int taakNummer = 2;
	boolean groteBalletjes = true;
	Vector rodeFiguurCoordinaten = new Vector();
	
	ImageResource resetResource;
	Image resetImage;
	ImageResource tekenUpResource;
	Image tekenUpImage;
	ImageResource tekenDownResource;
	Image tekenDownImage;
	ImageResource gumResource;
	Image gumImage;
	ImageResource goedKrulResource;
	Image goedKrulImage;

	PushButton resetKnop;
	ToggleButton tekenKnop;
	PushButton gumKnop;
	
	boolean correct = false;
	private int mode;
	private OpdrNavIF comRoot;
	
	public void getImages() 
	{

		verknippenGWTClientBundle = GWT.create(VerknippenGWTClientBundle.class);
		verknippenGWTCssResource = verknippenGWTClientBundle.getVerknippenGWTCssResource();
		verknippenGWTCssResource.ensureInjected();
		
		resetResource = verknippenGWTClientBundle.resetResource();
		resetImage = new Image(resetResource);
		resetImage.setStyleName(verknippenGWTCssResource.pushimage());
		tekenUpResource = verknippenGWTClientBundle.tekenUpResource();
		tekenUpImage = new Image(tekenUpResource);
		tekenUpImage.setStyleName(verknippenGWTCssResource.upimage());
		tekenDownResource = verknippenGWTClientBundle.tekenDownResource();
		tekenDownImage = new Image(tekenDownResource);
		tekenDownImage.setStyleName(verknippenGWTCssResource.downimage());
		gumResource = verknippenGWTClientBundle.gumResource();
		gumImage = new Image(gumResource);
		gumImage.setStyleName(verknippenGWTCssResource.upimage());
		goedKrulResource = verknippenGWTClientBundle.goedKrulResource();
		goedKrulImage = new Image(goedKrulResource);
		goedKrulImage.setStyleName(verknippenGWTCssResource.upimage());

	}	
	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName("root");
		
//		bottomPanel = new LayoutPanel();
//		bottomPanel.addStyleName("bottom");
		//bottomPanel.getElement().getStyle().setBackgroundColor(bottomBgColor.toString());		

//		dlp.addSouth(bottomPanel, bottomHeight);
		
		
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
		
		Stub.publish(this);
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());


			
	}
	
	public VerknippenGWT()
	{
		this(null, null, null);
	}
	
	public VerknippenGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
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
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

//		bottomPanel = new LayoutPanel();
//		bottomPanel.addStyleName("bottom");
		//bottomPanel.getElement().getStyle().setBackgroundColor(bottomBgColor.toString());
		
		//dlp.addSouth(bottomPanel, bottomHeight);
		
//		kladjeGWTVeld = new KladjeGWTVeld(breedte, hoogte - bottomHeight, true); 

//		kladjeGWTCanvas = kladjeGWTVeld.getCanvas();
		
//		kladjeGWTCanvas.addStyleName("canvas");
//		kladjeGWTVeld.initContext2d();		
		
		//dlp.add(kladjeGWTCanvas);
//		dlp.add(kladjeGWTVeld.getAsPanel());

		init(breedte, hoogte, launchState, randomVarWaarden);
		//makeBottom();

	}

	public Vector processFiguurString(String fString)
	{	fString = removeAllBlanks(fString);
		Vector figCoordStrings = parseOnChar(fString, '|');
		if (figCoordStrings.size() == 0)
			return null;
		Vector figCoords = new Vector();
		boolean error = false;
		for (int cCnt = 0; cCnt < figCoordStrings.size(); cCnt++)
		{	String aCoordString = (String) figCoordStrings.elementAt(cCnt);
			int kommaIndex = aCoordString.indexOf(',');
			if ((kommaIndex >= 0) && (kommaIndex < (aCoordString.length() - 1)))
			{	String xCoordString = aCoordString.substring(0, kommaIndex);
				String yCoordString = aCoordString.substring(kommaIndex + 1);
				int xCoord = 0;
				int yCoord = 0;
				//boolean error = false;
				try
				{	xCoord = Integer.parseInt(xCoordString);
					yCoord = Integer.parseInt(yCoordString);	
				}
				catch (NumberFormatException nfe)
				{	error = true;
				}
				if (!error)
				{	Point figPoint = new Point(xCoord, yCoord);
					figCoords.addElement(figPoint);
				}
			}
			else
				error = true;
		}
		if (error)
			return null;
		else
			return figCoords;
			
	} 

	public Vector parseOnChar(String inString, char c)
	{	Vector result = new Vector();
		if ((inString == null) || (inString.length() == 0))
			return result;
		int index = inString.indexOf(c);
		while (index >= 0)
		{	String word = inString.substring(0,index);
			if (word.length() > 0)
			{	word = skipSpaces(word);
				result.addElement(word);
			}	
			if (inString.length() > (index + 1))					
				inString = inString.substring(index + 1);
			else 	
				inString = "";
			index = inString.indexOf(c);	
		}	
		if (inString.length() > 0)
		{	inString = skipSpaces(inString);
			result.addElement(inString);
		}	
		return result;	
	}

	public String skipSpaces(String inString)
	{	String result = inString;
		while ((result.length() > 0) && (result.charAt(0) == ' '))
			result = result.substring(1);
		return result;	
	}

    public String removeAllBlanks(String s)
    {   int index = s.indexOf(' ');
        while (index >= 0)
        {   s = s.substring(0, index) + s.substring(index + 1);
            index = s.indexOf(' ');
        }
        return s;
    }

	public void zetSchaduwZichtbaar(boolean b, String rodeFiguurString)
	{	boolean schaduwZichtbaar = b;
		if (schaduwZichtbaar)
		{	Vector rodeFiguurCoordinaten = processFiguurString(rodeFiguurString);
			if (rodeFiguurCoordinaten == null)
				return;
			
			if (taakNummer < 4)
			{	KnipPolygon2 sp = new KnipPolygon2(dp2, rodeFiguurCoordinaten, KnipPolygon2.CENTER);
				dp2.shadowPolygon = sp;
			}
			else // taakNummer== 4
			{	KnipPolygon2 sp = new KnipPolygon2(dp2, rodeFiguurCoordinaten, KnipPolygon2.RIGHTAL);
				dp2.shadowPolygon = sp;				 	
			}	
			dp2.paint();
		}
		else
		{	dp2.shadowPolygon = null;
			dp2.paint();
		}
		
	}

	public void zetGrijzeFiguur(String grijzeFiguurString)
	{	Vector grijzeFiguurCoordinaten = processFiguurString(grijzeFiguurString);
		if (grijzeFiguurCoordinaten == null)
			return;
		//this.grijzeFiguurString = grijzeFiguurString;
		//this.grijzeFiguurCoordinaten = grijzeFiguurCoordinaten;
		if (taakNummer == 4)
			dp2.grijsPolygon = new KnipPolygon2(dp2, grijzeFiguurCoordinaten, KnipPolygon2.LEFTAL);
		
	}

	public void opnieuwAction(Vector rodeFiguurCoordinaten)
	{
		dp2.removeAllKnipPolygons();
		
		dp2.rectangles.removeAllElements();
		
		KnipPolygon2 kp = new KnipPolygon2(dp2, rodeFiguurCoordinaten, KnipPolygon2.CENTER);
										 
		if (taakNummer == 4)
		{	kp = new KnipPolygon2(dp2, rodeFiguurCoordinaten, KnipPolygon2.RIGHTAL);
		}												 
		if ((taakNummer == 2) || (taakNummer == 3))
			kp.setLabelPoint();
										 
		dp2.addKnipPolygon(kp);        

		dp2.oval1Pos = null;
		dp2.oval2Pos = null;			        
		dp2.oval3Pos = null;
		dp2.paint();
		dp2.figureIsRectangle = false;

		//antwoordOK = false;
		//antwoord = 0;
		//antwoordenFout = 0;

		if (taakNummer == 1)
		{	
//GWT			
			//bottomPanel2.opdrachtLabel.setText(Verknippen.rb.getString("maakRechthoekTekst"));
		}
		else // taakNummer==2 of taakNummer==3 of taakNummer==4
		{	//antwoord = 0;
			//bottomPanel2.oppervlakteTextField.setText("");
		}	
		
		//bottomPanel2.showGoed = false;
		//bottomPanel2.showFout = false;

		
		//repaint();
		
	}

	public void init(int width, int height, Map<String, Object> map, //launchState,
			Map<String, Number> values) 
	{		this.breedte = width;
			this.hoogte = height;
			//this.launchState = launchState;
			ObjectMap launchState = JSONUtilities.wrapMap(map);
			
			boolean roosterZichtbaar = false;
			boolean schaduwZichtbaar = true;
			boolean afmetingenZichtbaar = true;
			boolean figuurTransparant = true;
			boolean resetButton = true;
			int gridSize = 20;
			String rodeFiguurString =  "2,0|10,0|8,8|0,8";
			String grijzeFiguurString =  "0,0|8,0|8,8|0,8";
			boolean tekenGumOptie = true;
			int maxScore = 10;
			
			if ((launchState != null) && launchState.containsKey("taakNummer"))
					taakNummer = launchState.getInt("taakNummer");
			if ((launchState != null) && launchState.containsKey("groteBalletjes"))
				groteBalletjes = launchState.getBoolean("groteBalletjes");
			if ((launchState != null) && launchState.containsKey("roosterZichtbaar"))
				roosterZichtbaar = launchState.getBoolean("roosterZichtbaar");
			if ((launchState != null) && launchState.containsKey("schaduwZichtbaar"))
				schaduwZichtbaar = launchState.getBoolean("schaduwZichtbaar");
			if ((launchState != null) && launchState.containsKey("figuurTransparant"))
				figuurTransparant = launchState.getBoolean("figuurTransparant");
			if ((launchState != null) && launchState.containsKey("resetButton"))
				resetButton = launchState.getBoolean("resetButton");
			if ((launchState != null) && launchState.containsKey("gridSize"))
				gridSize = launchState.getInt("gridSize");
			if ((launchState != null) && launchState.containsKey("rodeFiguurString"))
				rodeFiguurString = launchState.getString("rodeFiguurString");
			if ((launchState != null) && launchState.containsKey("grijzeFiguurString"))
				grijzeFiguurString = launchState.getString("grijzeFiguurString");
			if ((launchState != null) && launchState.containsKey("tekenGumOptie"))
				tekenGumOptie = launchState.getBoolean("tekenGumOptie");
			if ((launchState != null) && launchState.containsKey("maxScore"))
				maxScore = launchState.getInt("maxScore");
			
			dp2 = new DrawingPanel2(breedte, hoogte, this, groteBalletjes); 

			Canvas dp2Canvas = dp2.getCanvas();
			if (dp2Canvas == null) 
			{
		      RootPanel.get().add(new Label(upgradeMessage));
		      return;
		    }
			
			dp2Canvas.addStyleName(verknippenGWTCssResource.canvas());
			dp2.initContext2d();		
			
			dlp.add(dp2);
			
			dp2.showGrid = roosterZichtbaar;
			dp2.showSizes = afmetingenZichtbaar;			
			dp2.figuurTransparant = figuurTransparant;
			dp2.gridSize = gridSize;
			
			rodeFiguurCoordinaten = processFiguurString(rodeFiguurString);

			
			KnipPolygon2 kp = new KnipPolygon2(dp2, rodeFiguurCoordinaten, KnipPolygon2.CENTER);
			if (taakNummer == 4)
				kp = new KnipPolygon2(dp2, rodeFiguurCoordinaten, KnipPolygon2.RIGHTAL);
			dp2.addKnipPolygon(kp);
			dp2.oval3Pos = kp.getRotationPoint().toPoint();
			if ((taakNummer == 2) || (taakNummer == 3))		
				kp.setLabelPoint();

			
			zetSchaduwZichtbaar(schaduwZichtbaar, rodeFiguurString);
			
			if (taakNummer == 4)
				zetGrijzeFiguur(grijzeFiguurString);
			
			resetKnop = new PushButton(resetImage);
			if (resetButton)
			{
				dp2.add(resetKnop);
				dp2.setWidgetLeftWidth(resetKnop, breedte - 36, Style.Unit.PX, 33, Style.Unit.PX);
				dp2.setWidgetTopHeight(resetKnop, 3, Style.Unit.PX, 33, Style.Unit.PX);
				PushClickHandler resetHandler = new PushClickHandler();
				resetKnop.addClickHandler(resetHandler);

			}
			tekenKnop = new ToggleButton(tekenUpImage,tekenDownImage);
			gumKnop = new PushButton(gumImage);
			if (tekenGumOptie)
			{
				dp2.add(tekenKnop);
				dp2.setWidgetLeftWidth(tekenKnop, 5, Style.Unit.PX, 24, Style.Unit.PX);
				dp2.setWidgetTopHeight(tekenKnop, 5, Style.Unit.PX, 24, Style.Unit.PX);
				PushClickHandler tekenHandler = new PushClickHandler();
				tekenKnop.addClickHandler(tekenHandler);
				
				dp2.add(gumKnop);
				dp2.setWidgetLeftWidth(gumKnop, 32, Style.Unit.PX, 24, Style.Unit.PX);
				dp2.setWidgetTopHeight(gumKnop, 5, Style.Unit.PX, 24, Style.Unit.PX);
				PushClickHandler gumHandler = new PushClickHandler();
				gumKnop.addClickHandler(gumHandler);

				dp2.showGrid = true;
				dp2.tekenGumOptie = true;
			}
			
			if (taakNummer == 1)
			{
				dp2.add(goedKrulImage);
				dp2.setWidgetLeftWidth(goedKrulImage, (breedte - 30) / 2, Style.Unit.PX, 30, Style.Unit.PX);
				dp2.setWidgetTopHeight(goedKrulImage, hoogte - 35, Style.Unit.PX, 30, Style.Unit.PX);
				dp2.setWidgetVisible(goedKrulImage,false);
			}

			
			if ((map != null) && (map.containsKey("polygonMaps")))
				setState((HashMap) map);

			//makeBottom();
			
			dp2.paint();


	}
	
	public void	makeBottom()
	{
		
		
	}

    class PushClickHandler implements ClickHandler
    {
    	
    	public void onClick(ClickEvent e)
    	{
			//e.preventDefault();
			e.stopPropagation();
    		
			if (e.getSource() == resetKnop)
				opnieuwAction(rodeFiguurCoordinaten);
			else if (e.getSource() == tekenKnop)
			{	
				if (tekenKnop.isDown())
				{
					dp2.tekenen = true;
				}
				else
				{
					dp2.tekenen = false;
				}
			}
			else if (e.getSource() == gumKnop)
			{	
				tekenKnop.setDown(false);
				dp2.tekenen = false;
				dp2.rectangles.removeAllElements();
				dp2.paint();

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
		
System.out.println("getState");

		HashMap<String, Object> h = new HashMap<String, Object>(); 
		
    	ArrayList<Map<String,Object>> polygonMaps = new ArrayList<Map<String,Object>>(); 
    	
    	for (int pCnt = 0; pCnt < dp2.knipPolygons.size(); pCnt++)
    	{	
    		ArrayList<Double> polygonX = new ArrayList<Double>();
        	ArrayList<Double> polygonY = new ArrayList<Double>();
        	Map<String,Object> polygonMap = new HashMap<String,Object>();
    		
    		KnipPolygon2 kp = (KnipPolygon2) dp2.knipPolygons.elementAt(pCnt);

    		for (int qCnt = 0; qCnt < kp.realPoints.length; qCnt++)
    		{	polygonX.add(new Double(kp.realPoints[qCnt].x));
    			polygonY.add(new Double(kp.realPoints[qCnt].y));
    		}
    		polygonMap.put("polygonX", polygonX);
    		polygonMap.put("polygonY", polygonY);
    		polygonMap.put("oppervlakte", new Integer(kp.oppervlakte));
    		
    		polygonMaps.add(polygonMap);
    		
    	}
    	h.put("polygonMaps", polygonMaps);
    	
System.out.println("polyMaps " + polygonMaps.size());    	
    	
    	ArrayList<Integer> rectanglesX = new ArrayList<Integer>();
    	ArrayList<Integer> rectanglesY = new ArrayList<Integer>();
    	ArrayList<Integer> rectanglesW = new ArrayList<Integer>();
    	ArrayList<Integer> rectanglesH = new ArrayList<Integer>();
    	for (int rCnt = 0; rCnt < dp2.rectangles.size(); rCnt++)
    	{	Rectangle r = (Rectangle) dp2.rectangles.elementAt(rCnt);
    		rectanglesX.add(new Integer(r.x));
    		rectanglesY.add(new Integer(r.y));
    		rectanglesW.add(new Integer(r.width));
    		rectanglesH.add(new Integer(r.height));
    	}
    
    	h.put("rectanglesX", rectanglesX);
    	h.put("rectanglesY", rectanglesY);
    	h.put("rectanglesW", rectanglesW);
    	h.put("rectanglesH", rectanglesH);
    	
    	
    	
		return h;

	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		
System.out.println("setState");

		ObjectMap map = JSONUtilities.wrapMap(h);
		
		List<Map<String,Object>> polygonMaps = new ArrayList<Map<String,Object>>();
		
		if (map.containsKey("polygonMaps"))
		{	polygonMaps = map.getMapList("polygonMaps");
//System.out.println("contains polygonMaps " + polygonMaps.size());		
		}
//else		
//System.out.println("no polygonMaps " + polygonMaps.size());
		
		dp2.knipPolygons.removeAllElements();
		dp2.oval3Pos = null;
		
		for (int pCnt = 0; pCnt < polygonMaps.size(); pCnt++)
		{	
			Map<String,Object> polygonMapRaw = polygonMaps.get(pCnt);
			
			ObjectMap polygonMap = JSONUtilities.wrapMap(polygonMapRaw);
			
			List<Double> polygonX = polygonMap.getDoubleList("polygonX");
			List<Double> polygonY = polygonMap.getDoubleList("polygonY");
    		int oppervlakte = polygonMap.getInt("oppervlakte");
    		
    		Vector realPoints = new Vector();
    		for (int qCnt = 0; qCnt < polygonX.size(); qCnt++)
    		{
    			double x = ((Double) polygonX.get(qCnt)).doubleValue();
    			double y = ((Double) polygonY.get(qCnt)).doubleValue();
    			
    			realPoints.add(new RealPoint(x,y));
    		}
		
    		KnipPolygon2 kp = new KnipPolygon2(realPoints, dp2);
			kp.oppervlakte = oppervlakte;
			dp2.knipPolygons.addElement(kp);
		
			if ((taakNummer == 2) || (taakNummer == 3))
				kp.setLabelPoint();
		}
		
    	Vector rectangles = new Vector();
    	List<Integer> rectanglesX = new ArrayList<Integer>();
    	List<Integer> rectanglesY = new ArrayList<Integer>();
    	List<Integer> rectanglesW = new ArrayList<Integer>();
    	List<Integer> rectanglesH = new ArrayList<Integer>();

		if (map.containsKey("rectanglesX"))			
			rectanglesX = map.getIntegerList("rectanglesX");
		if (map.containsKey("rectanglesY"))			
			rectanglesY = map.getIntegerList("rectanglesY");
		if (map.containsKey("rectanglesW"))			
			rectanglesW = map.getIntegerList("rectanglesW");
		if (map.containsKey("rectanglesH"))			
			rectanglesH = map.getIntegerList("rectanglesH");
		
		for (int rCnt = 0; rCnt < rectanglesX.size(); rCnt++)
		{	int x = rectanglesX.get(rCnt);
			int y = rectanglesY.get(rCnt);
			int w = rectanglesW.get(rCnt);
			int he = rectanglesH.get(rCnt);
			
			Rectangle r = new Rectangle(x,y,w,he);
			
			rectangles.add(r);
		}
		
		dp2.rectangles = rectangles;

		dp2.paint();
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
	{	if (taakNummer == 1)
		{
			if (dp2 != null)
			{    
				correct = dp2.figureIsRectangle;
				comRoot.setChanged(isCorrect().booleanValue());
				if (correct)
					dp2.setWidgetVisible(goedKrulImage,true);
				else
					dp2.setWidgetVisible(goedKrulImage,false);
			}	
     
//System.out.println("kijkNa() - 2");         
		}	 

	}
	
	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());


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

}
