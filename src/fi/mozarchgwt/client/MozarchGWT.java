package fi.mozarchgwt.client;

//import java.awt.Rectangle;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
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


public class MozarchGWT implements EntryPoint, InteractionStub, InteractionView 
{
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	MozarchGWTClientBundle mozarchGWTClientBundle;
	MozarchGWTCssResource mozarchGWTCssResource;

	// UI
	DockLayoutPanel dlp;
	//LayoutPanel bottomPanel;
	LayoutPanel canvasPanel;
	Canvas draaibankGWTCanvas;
	TekenPanel tekenPanel;
	
	PushButton wisKnop; 
	
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
	
	boolean correct = false;
	private int mode;
	private OpdrNavIF comRoot;
	
	public void getImages() 
	{

		mozarchGWTClientBundle = GWT.create(MozarchGWTClientBundle.class);
		mozarchGWTCssResource = mozarchGWTClientBundle.getMozarchGWTCssResource();
		mozarchGWTCssResource.ensureInjected();
		

	}	
	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName("root");
		
		
		Stub.publish(this);
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());


			
	}
	
	public MozarchGWT()
	{
		this(null, null, null);
	}
	
	public MozarchGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
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


		init(breedte, hoogte, launchState, randomVarWaarden);


	}



	public void init(int width, int height, Map<String, Object> map, //launchState,
			Map<String, Number> values) 
	{		this.breedte = width;
			this.hoogte = height;
			//this.launchState = launchState;
			ObjectMap launchState = JSONUtilities.wrapMap(map);
			
			// parametrisatie			
			boolean fractielen = false;
			boolean startFiguur = false;
			int fractielType = 1;
			int aantalHoekpunten = 3;
			int aantalPerZijde = 1;

			boolean triangles = false; //true;
			boolean squares = false; //true;
			boolean pentagons = true; //false;
			boolean hexagons = false; //true;
			boolean octagons = false; //true;
			boolean dekagons = true; //false;
			boolean dodekagons = true;
			
			if (launchState != null && launchState.containsKey("fractielen"))
				fractielen = launchState.getBoolean("fractielen");
			if (launchState != null && launchState.containsKey("startFiguur"))
				startFiguur = launchState.getBoolean("startFiguur");
			if (launchState != null && launchState.containsKey("fractielType"))
				fractielType = launchState.getInt("fractielType");
			if (launchState != null && launchState.containsKey("aantalHoekpunten"))
				aantalHoekpunten = launchState.getInt("aantalHoekpunten");
			if (launchState != null && launchState.containsKey("aantalPerZijde"))
				aantalPerZijde = launchState.getInt("aantalPerZijde");

			if (launchState != null && launchState.containsKey("triangles"))
				triangles = launchState.getBoolean("triangles");
			if (launchState != null && launchState.containsKey("squares"))
				squares = launchState.getBoolean("squares");
			if (launchState != null && launchState.containsKey("pentagons"))
				pentagons = launchState.getBoolean("pentagons");
			if (launchState != null && launchState.containsKey("hexagons"))
				hexagons = launchState.getBoolean("hexagons");
			if (launchState != null && launchState.containsKey("octagons"))
				octagons = launchState.getBoolean("octagons");
			if (launchState != null && launchState.containsKey("dekagons"))
				dekagons = launchState.getBoolean("dekagons");
			if (launchState != null && launchState.containsKey("dodekagons"))
				dodekagons = launchState.getBoolean("dodekagons");

			
			canvasPanel = new LayoutPanel();
			dlp.add(canvasPanel);

			tekenPanel = new TekenPanel(breedte, hoogte);
			
			if (tekenPanel.mozarchGWTCanvas == null) 
			{
		      RootPanel.get(holderId).add(new Label(upgradeMessage));
		      return;
		    }

			tekenPanel.mozarchGWTCanvas.addStyleName(mozarchGWTCssResource.canvas());
			
			canvasPanel.add(tekenPanel.mozarchGWTCanvas);
			canvasPanel.setWidgetLeftWidth(tekenPanel.mozarchGWTCanvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(tekenPanel.mozarchGWTCanvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);

			tekenPanel.fractielen = fractielen;
			tekenPanel.beginFig = startFiguur;
			tekenPanel.beginFractielType = fractielType;
			tekenPanel.beginFigAantalHp = aantalHoekpunten;
			tekenPanel.beginFigAantalPz = aantalPerZijde;
			
			tekenPanel.triangles = triangles;
			tekenPanel.squares = squares;
			tekenPanel.pentagons = pentagons;
			tekenPanel.hexagons = hexagons;
			tekenPanel.octagons = octagons;
			tekenPanel.dekagons = dekagons;
			tekenPanel.dodekagons = dodekagons;
			
			tekenPanel.initialiseer();
			
			wisKnop = new PushButton("wis");
			wisKnop.addStyleName(mozarchGWTCssResource.pushbutton());
			canvasPanel.add(wisKnop);
			canvasPanel.setWidgetLeftWidth(wisKnop, 10, Style.Unit.PX, 50, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(wisKnop, hoogte - 35, Style.Unit.PX, 25, Style.Unit.PX);
			wisKnop.addClickHandler(new PushClickHandler());
			
			//makeBottom();
			if (map != null)
				setState((HashMap) map);


			tekenPanel.tekenOpnieuw();

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
    		
			if (e.getSource() == wisKnop)
			{
				tekenPanel.wis();
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
		
//System.out.println("getState");

		HashMap<String, Object> h = new HashMap<String, Object>();
		
		//Vector vlakdelenVector = new Vector();
		ArrayList<Map<String,Object>> vlakdelenList = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < tekenPanel.aantalVlakdelen; i++)
		{	HashMap<String,Object> hv = new HashMap<String,Object>();
			hv.put("fractielType", new Integer(tekenPanel.vlakdelen[i].fractielType));
			hv.put("aantalHoekpunten", new Integer(tekenPanel.vlakdelen[i].aantalHoekpunten));
			hv.put("aantalPuntenPerZijde", new Integer(tekenPanel.vlakdelen[i].aantalPuntenPerZijde));
			hv.put("positiex", new Double(tekenPanel.vlakdelen[i].draaipunt.x));
			hv.put("positiey", new Double(tekenPanel.vlakdelen[i].draaipunt.y));
			hv.put("kleurgwt", tekenPanel.vlakdelen[i].kleur.value());
			hv.put("orientatie", new Double(tekenPanel.vlakdelen[i].orientatie));
			hv.put("nieuw", new Boolean(tekenPanel.vlakdelen[i].nieuw));
			hv.put("beginnummer", new Integer(tekenPanel.vlakdelen[i].beginnummer));
//			hv.put("aantalHoekpuntenVast", new Integer(tekenPanel.vlakdelen[i].aantalHoekpuntenVast));
//			hv.put("isHeap", new Boolean(tekenPanel.vlakdelen[i].isHeap));
			
			hv.put("volgorde", new Integer(tekenPanel.volgorde[i]));
			
			//Vector hoekpuntenVector = new Vector();
			ArrayList<Double> hoekpuntenXList = new ArrayList<Double>();
			ArrayList<Double> hoekpuntenYList = new ArrayList<Double>();
			for (int j = 0; j < tekenPanel.vlakdelen[i].aantalPunten + 1; j++)
			{	//hoekpuntenVector.addElement(
					// zonder plakken
				//	new Punt(tekenPanel.vlakdelen[i].hoekpunten[j].x, tekenPanel.vlakdelen[i].hoekpunten[j].y));
					// met plakken
					//tekenPanel.vlakdelen[i].hoekpunten[j]);
			
				hoekpuntenXList.add(new Double(tekenPanel.vlakdelen[i].hoekpunten[j].x));
				hoekpuntenYList.add(new Double(tekenPanel.vlakdelen[i].hoekpunten[j].y));
			}
			hv.put("hoekpuntenXList", hoekpuntenXList);
			hv.put("hoekpuntenYList", hoekpuntenYList);

//System.out.println("hv = " + hoekpuntenXList.size());			
			
			vlakdelenList.add(hv);
		}
//System.out.println("vdlv = " + vlakdelenList.size());

		h.put("vlakdelenList", vlakdelenList);
		
		
		
		return h;

	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		
//System.out.println("setState");

		ObjectMap map = JSONUtilities.wrapMap(h);
		
		int tempAantalVlakdelen = 0;
		List<Map<String,Object>> vlakdelenList = new ArrayList<Map<String,Object>>();
		
		if (map.containsKey("vlakdelenList"))
		{	vlakdelenList = map.getMapList("vlakdelenList");
//System.out.println("contains vlakdelenList " + vlakdelenList.size());
			tempAantalVlakdelen = vlakdelenList.size();
		}
//else		
//System.out.println("no vlakdelenList " + vlakdelenList.size());
		if (tempAantalVlakdelen == 0)
			return;
		
		int[] tempVolgorde = new int[tempAantalVlakdelen];
		Vlakdeel2[] tempVlakdelen = new Vlakdeel2[tempAantalVlakdelen];
		
		for (int vdCnt = 0; vdCnt < vlakdelenList.size(); vdCnt++)
		{	
			Map<String,Object> vlakdeelMapRaw = vlakdelenList.get(vdCnt);
			
			ObjectMap vlakdeelMap = JSONUtilities.wrapMap(vlakdeelMapRaw);

			int fractielT = 3;
			if (vlakdeelMap.containsKey("fractielType"))
				fractielT = vlakdeelMap.getInt("fractielType");
			int aantalHoekpt = 4;
			if (vlakdeelMap.containsKey("aantalHoekpunten"))
				aantalHoekpt = vlakdeelMap.getInt("aantalHoekpunten");
				
	//System.out.println("set: ap - i = " + aantalHoekpunten);

			int aantalPuntenPZ = 1;
			if (vlakdeelMap.containsKey("aantalPuntenPerZijde"))
				aantalPuntenPZ = vlakdeelMap.getInt("aantalPuntenPerZijde");

			double positiex = tekenPanel.startX;
			if (vlakdeelMap.containsKey("positiex"))
				positiex = vlakdeelMap.getDouble("positiex");
			double positiey = tekenPanel.startY;
			if (vlakdeelMap.containsKey("positiey"))
				positiey = vlakdeelMap.getDouble("positiey");
				
			CssColor kleur = CssColor.make(192,192,192);
			if (vlakdeelMap.containsKey("kleurgwt"))
				kleur = CssColor.make(vlakdeelMap.getString("kleurgwt"));
				
			tekenPanel.aantalVlakdelen = 0;
			if (fractielT == 0)
			{	tekenPanel.maakVeelhoek(aantalPuntenPZ, aantalHoekpt, positiex, positiey, kleur);
					
			}
			else
			{	tekenPanel.maakFractiel(fractielT, aantalPuntenPZ, positiex, positiey, kleur);
			}
			tempVlakdelen[vdCnt] = tekenPanel.vlakdelen[0];
			tekenPanel.aantalVlakdelen = 0;

	/*			
				boolean isHeap = false;
				if (hv.containsKey("isHeap"))
					isHeap = ((Boolean) hv.get("isHeap")).booleanValue();
				tempVlakdelen[i].isHeap = isHeap;
	*/			
			double orientatie = 0;
			if (vlakdeelMap.containsKey("orientatie"))
				orientatie = vlakdeelMap.getDouble("orientatie");
			tempVlakdelen[vdCnt].orientatie = orientatie;

			boolean nieuw = true;
			if (vlakdeelMap.containsKey("nieuw"))
				nieuw = vlakdeelMap.getBoolean("nieuw");
			tempVlakdelen[vdCnt].nieuw = nieuw;
				
			int beginnummer = 0;
			if (vlakdeelMap.containsKey("beginnummer"))
				beginnummer = vlakdeelMap.getInt("beginnummer");
			tempVlakdelen[vdCnt].beginnummer = beginnummer;

	/*			
				int aantalHoekpuntenVast = 0;
				if (hv.containsKey("aantalHoekpuntenVast"))
					aantalHoekpuntenVast = ((Integer) hv.get("aantalHoekpuntenVast")).intValue();
				tempVlakdelen[i].aantalHoekpuntenVast = aantalHoekpuntenVast;
	*/			
			int tVolgorde = 0; 
			if (vlakdeelMap.containsKey("volgorde"))
				tVolgorde = vlakdeelMap.getInt("volgorde");
			tempVolgorde[vdCnt] = tVolgorde;
				
			//Vector hoekpuntenVector = new Vector();
			List<Double> hoekpuntenXList = new ArrayList<Double>();
			List<Double> hoekpuntenYList = new ArrayList<Double>();
			if (vlakdeelMap.containsKey("hoekpuntenXList"))
				hoekpuntenXList = vlakdeelMap.getDoubleList("hoekpuntenXList");
			if (vlakdeelMap.containsKey("hoekpuntenYList"))
				hoekpuntenYList = vlakdeelMap.getDoubleList("hoekpuntenYList");
			for (int j = 0; j < hoekpuntenXList.size(); j++)
			{
					double x = hoekpuntenXList.get(j);
					double y = hoekpuntenYList.get(j);
					Punt punt = new Punt(x,y);
					// zonder5 plakken
					//Punt punt = (Punt) hoekpuntenVector.elementAt(j);
					tempVlakdelen[vdCnt].hoekpunten[j] = new HoekpuntMoz(punt.x, punt.y);
					
					// met plakken
					//tempVlakdelen[i].hoekpunten[j] = (HoekpuntMoz) hoekpuntenVector.elementAt(j);
			}		
				
	//System.out.println("set: hv = " + hoekpuntenVector.size());

		} // for

	/*		
			for (int vCnt = 0; vCnt < tempAantalVlakdelen; vCnt++)
			{	volgorde[vCnt] = vCnt;
			}
	*/		

			
		for (int tCnt = 0; tCnt < tempAantalVlakdelen; tCnt++)
		{
			tekenPanel.vlakdelen[tCnt] = tempVlakdelen[tCnt];
			tekenPanel.aantalVlakdelen++;

			//aantalVlakdelen++;
			//actiefVlakdeel = vlakdelen[tCnt];
				
			tekenPanel.volgorde[tCnt] = tempVolgorde[tCnt];
		}

		tekenPanel.tekenOpnieuw();
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
	{		 

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
