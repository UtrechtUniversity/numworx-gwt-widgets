package fi.binomverdgwt.client;

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

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;

import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Label;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.resources.client.ImageResource;


public class BinomVerdGWT implements EntryPoint, InteractionStub 
{
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	// UI
	DockLayoutPanel dlp;
	
	BinomVerdPanel binomVerdPanel;
	
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
	
	private int mode;
	private OpdrNavIF comRoot;
	Boolean correct = null;
	boolean nagekeken = false;
	boolean ingevuld = false;
	
	boolean bvSetState = false;

	// images
	BinomVerdGWTClientBundle binomVerdGWTClientBundle;
	ImageResource foutKruisResource, goedKrulResource;
	Image foutKruisImage, goedKrulImage;

	static BinomVerdGWTCssResource binomVerdGWTCss;
	
	public void getImages() 
	{
		binomVerdGWTClientBundle = GWT.create(BinomVerdGWTClientBundle.class);
		binomVerdGWTCss = binomVerdGWTClientBundle.getBinomVerdGWTCSS();
		binomVerdGWTCss.ensureInjected();

		
		foutKruisResource = binomVerdGWTClientBundle.foutKruisResource();
		goedKrulResource = binomVerdGWTClientBundle.goedKrulResource();
		foutKruisImage = new Image(foutKruisResource);
		goedKrulImage = new Image(goedKrulResource);
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
	
	public BinomVerdGWT()
	{
		
	}
	
	public BinomVerdGWT(HashMap<String,Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
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

		//getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		
		init(breedte, hoogte, launchState, randomVarWaarden);
		

	}

	public void	makeBottom()
	{
		
		
	}

    class PushMouseHandler implements MouseDownHandler
    {
    	
    	public void onMouseDown(MouseDownEvent e)
    	{
			//e.preventDefault();
			e.stopPropagation();
    		
    		
    	}
    }
    
    class PushTouchStartHandler implements TouchStartHandler
    {
    	public void onTouchStart(TouchStartEvent e)
    	{
			
    		// DIT NIET toevoegen!!
    		//e.preventDefault();
			e.stopPropagation();
    		
    		
    	}
    }
	
	public Widget asWidget()
	{
		return dlp;
	}
	
	@Override
	public HashMap<String, Object> getState()
	{
		HashMap<String, Object> h = binomVerdPanel.getState();
		h.put("nagekeken", new Boolean(nagekeken));
		h.put("ingevuld",new Boolean(ingevuld));
		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		bvSetState = true;
		binomVerdPanel.setState(h);
		bvSetState = false;
		ingevuld = false;
		
		if (h.containsKey("nagekeken"))
		{	nagekeken = ((Boolean) h.get("nagekeken")).booleanValue();
		}
		if (h.containsKey("ingevuld"))
			ingevuld = ((Boolean) h.get("ingevuld")).booleanValue();

		
		if (ingevuld && (mode == 0 || nagekeken))
		//if (nagekeken)
			kijkNa();
		

	}

	@Override
	public int getScore()
	{
		// TODO Auto-generated method stub
		return binomVerdPanel.score;
	}

	@Override
	public Boolean isCorrect()
	{
		if (binomVerdPanel.kijkOpdrachtNa)
			return correct;
		else
			return new Boolean(true);

	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());


	}
	
	public void zetNagekeken(boolean b) 
	{	nagekeken = true;
	}
	
	public void zetMode(int mode)
	{	this.mode = mode;
		if (binomVerdPanel.kijkOpdrachtNa)    
			binomVerdPanel.kijkOpdrachtNa = (mode == 0 || mode == 1);

	}

    public void changed()
	{
    	
	  	if (binomVerdPanel.kijkOpdrachtNa && !bvSetState) 
		{
//System.out.println("changed");	  		
	   		correct = null;
    		nagekeken = false;
    		ingevuld = true;
    		
    		comRoot.setChanged(true);
		   	
		}
	}

	@Override
	public void kijkNa() 
	{
		binomVerdPanel.kijkNa();
		nagekeken = true;
		ingevuld = true;
		comRoot.setChanged(isCorrect().booleanValue());
		
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

	public void init(int width, int height, Map<String, Object> map, //launchState,
			Map<String, Number> values) 
	{
		this.breedte = width;
		this.hoogte = height;
		//this.launchState = launchState;
		ObjectMap launchState = JSONUtilities.wrapMap(map);

		// edit state variabelen
		//boolean nVeranderbaar = false; //true;
		//boolean pVeranderbaar = true;
		//boolean MVeranderbaar = true;
		//boolean populatieVeranderbaar = true;

		//if (launchState.containsKey("nVeranderbaar"))
		//	nVeranderbaar = launchState.getBoolean("nVeranderbaar");
		//if (launchState.containsKey("pVeranderbaar"))
		//	pVeranderbaar = launchState.getBoolean("pVeranderbaar");
		//if (launchState.containsKey("MVeranderbaar"))
		//	MVeranderbaar = launchState.getBoolean("MVeranderbaar");
		//if (launchState.containsKey("populatieVeranderbaar"))
		//	populatieVeranderbaar = launchState.getBoolean("populatieVeranderbaar");
		
		//boolean showNSlider;
		//boolean showPSlider;
		//boolean showMSlider;
		//boolean showPopulatieSlider;

		//if (launchState.containsKey("showNSlider"))
		//	showNSlider = launchState.getBoolean("showNSlider");
		//if (launchState.containsKey("showPSlider"))
		//	showPSlider = launchState.getBoolean("showPSlider");
		//if (launchState.containsKey("showMSlider"))
		//	showMSlider = launchState.getBoolean("showMSlider");
		//if (launchState.containsKey("showPopulatieSlider"))
		//	showPopulatieSlider = launchState.getBoolean("showPopulatieSlider");
		
		//boolean tweeGrenzen;
		//boolean showGrensSlider;
		
		//if (launchState.containsKey("tweeGrenzen"))
		//	tweeGrenzen = launchState.getBoolean("tweeGrenzen");
		//if (launchState.containsKey("showGrensSlider"))
		//	showGrensSlider = launchState.getBoolean("showGrensSlider");
		
		
		//boolean showNoordBalk;
		//boolean showTweeGrenzenKeuze;
		//boolean showKansBalk;
		//boolean showHyperKeuze;

		//if (launchState.containsKey("showNoordBalk"))
		//	showNoordBalk = launchState.getBoolean("showNoordBalk");
		//if (launchState.containsKey("showTweeGrenzenKeuze"))
		//	showTweeGrenzenKeuze = launchState.getBoolean("showTweeGrenzenKeuze");
		//if (launchState.containsKey("showKansBalk"))
		//	showKansBalk = launchState.getBoolean("showKansBalk");
		//if (launchState.containsKey("showHyperKeuze"))
		//	showHyperKeuze = launchState.getBoolean("showHyperKeuze");
		
		dlp.setSize(breedte + "px", hoogte + "px");		
		
		getImages();
		
		binomVerdPanel = new BinomVerdPanel(this, breedte, hoogte);
		
		dlp.add(binomVerdPanel);
		
		if (launchState.containsKey("kijkNa")) 
		{			
			binomVerdPanel.kijkOpdrachtNa = launchState.getBoolean("kijkNa");
		}	
		
		binomVerdPanel.plaatsComponenten(true);
		
		Canvas binomVerdGWTCanvas = binomVerdPanel.getCanvas();
		
		if (binomVerdGWTCanvas == null) 
		{
	      RootPanel.get(holderId).add(new Label(upgradeMessage));
	      return;
	    }
		
		binomVerdPanel.setState(map);
		
/*		
		//nakijken:
		boolean kijkOpdrachtNa;
		int maxScore = 10;
		boolean kijkGrenzenNa;
		int antwoordGrensLinks = 5;
		int antwoordGrensRechts = 10;
		boolean kijkVerdelingNa;
		int antwoordVerdeling = 0; //0 = BV, 1 = Hyp
		boolean kijkNNa;
		int antwoordN = 30;
		boolean kijkPNa;
		BVInvoer antwoordP = new BVInvoer("0.5");
		boolean kijkMNa;
		int antwoordM = 50;
		boolean kijkPopulatieNa;
		int antwoordPopulatie = 100;
*/

		BVInvoer invoer = new BVInvoer("");
		
		//vul de randomwaarden in
		if (launchState.containsKey("initGrensLinks")) 
		{
			invoer.setInput(launchState.getString("initGrensLinks"));
			if (invoer.isRandomInput()) 
			{
				binomVerdPanel.staafjesPanel.setGrensLinks((int) substitueerRandom(0, invoer.getInput(), randomVarNamen, randomVarWaarden));
			}
			else 
			{
				binomVerdPanel.staafjesPanel.setGrensLinks(Integer.parseInt(invoer.getInput()));
			}
			
		}
		if (launchState.containsKey("initGrensRechts")) 
		{
			invoer.setInput(launchState.getString("initGrensRechts"));
			if (invoer.isRandomInput()) 
			{
				binomVerdPanel.staafjesPanel.setGrensRechts((int) substitueerRandom(10, invoer.getInput(), randomVarNamen, randomVarWaarden));
			}
			else 
			{
				binomVerdPanel.staafjesPanel.setGrensRechts(Integer.parseInt(invoer.getInput()));
			}
		}
		
		if (binomVerdPanel.nInvoer.isRandomInput()) 
		{
			binomVerdPanel.n = (int) substitueerRandom((double) binomVerdPanel.n, binomVerdPanel.nInvoer.getInput(), 
					                                    randomVarNamen, randomVarWaarden);
			binomVerdPanel.nInvoer.setInput(Integer.toString(binomVerdPanel.n));
			
			
		}
		
		if (binomVerdPanel.pInvoer.isRandomInput()) 
		{
			if (!binomVerdPanel.pInvoer.isBreuk()) 
			{
				binomVerdPanel.p = substitueerRandom(binomVerdPanel.p, binomVerdPanel.pInvoer.getInput(), 
						                             randomVarNamen, randomVarWaarden);
				binomVerdPanel.pInvoer.setInput(Double.toString(binomVerdPanel.p));
			}
			else 
			{
				double teller;
				double noemer;
				if (BVInvoer.isRandomVar(binomVerdPanel.pInvoer.getTellerString())) 
				{
					teller = substitueerRandom(binomVerdPanel.p, binomVerdPanel.pInvoer.getTellerString(), 
											   randomVarNamen, randomVarWaarden);
				}
				else
				{
					teller = Double.parseDouble(binomVerdPanel.pInvoer.getTellerString());
				}
				
				if (BVInvoer.isRandomVar(binomVerdPanel.pInvoer.getNoemerString())) 
				{
					noemer = substitueerRandom(1.0, binomVerdPanel.pInvoer.getNoemerString(), randomVarNamen, randomVarWaarden);
				}
				else 
				{
					noemer = Double.parseDouble(binomVerdPanel.pInvoer.getNoemerString());
				}
				
				binomVerdPanel.p = teller/noemer;
				if (binomVerdPanel.p > 1.0) 
				{ //kansen groter dan 1.0 zijn onzin
					binomVerdPanel.p = 1.0;
				}
				binomVerdPanel.pInvoer.setInput(Double.toString(teller) + "/" + Double.toString(noemer));
			}
		}
//		System.out.println("PInvoer: " + binomVerdPanel.pInvoer.getInput());
		binomVerdPanel.pInvoer.haalPuntNulWeg();
		
		
		//zet nakijkopties
		if (launchState.containsKey("kijkNa")) 
		{			
			binomVerdPanel.kijkOpdrachtNa = launchState.getBoolean("kijkNa");
			
			if (launchState.containsKey("scoreMax")) {
				binomVerdPanel.maxScore = launchState.getInt("scoreMax");
			}
			
			if (binomVerdPanel.kijkOpdrachtNa) 
			{
				if (launchState.containsKey("antwoordN")) 
				{
					binomVerdPanel.kijkNNa = true;
					invoer.setInput(launchState.getString("antwoordN"));
					if (invoer.isRandomInput()) 
					{
						binomVerdPanel.antwoordN = (int) substitueerRandom(30, invoer.getInput(), randomVarNamen, randomVarWaarden);
					}
					else 
					{
						binomVerdPanel.antwoordN = Integer.parseInt(invoer.getInput());
					}
				}
				else 
				{
					binomVerdPanel.kijkNNa = false;
				}
				
				if (launchState.containsKey("antwoordGrenzenVan") && launchState.containsKey("antwoordGrenzenTot")) {
					binomVerdPanel.kijkGrenzenNa = true;
					invoer.setInput(launchState.getString("antwoordGrenzenVan"));
					if (invoer.isRandomInput()) 
					{
						binomVerdPanel.antwoordGrensLinks = (int) substitueerRandom(5, invoer.getInput(), randomVarNamen, randomVarWaarden);
					}
					else 
					{
						binomVerdPanel.antwoordGrensLinks = Integer.parseInt(launchState.getString("antwoordGrenzenVan"));
					}
					
					invoer.setInput(launchState.getString("antwoordGrenzenTot"));
					if (invoer.isRandomInput()) 
					{
						binomVerdPanel.antwoordGrensRechts = (int) substitueerRandom(10, invoer.getInput(), randomVarNamen, randomVarWaarden);
					}
					else 
					{
						binomVerdPanel.antwoordGrensRechts = Integer.parseInt(launchState.getString("antwoordGrenzenTot"));
					}
					
				}
				else 
				{
					binomVerdPanel.kijkGrenzenNa = false;
				}
				
				if (launchState.containsKey("antwoordVerdeling")) 
				{
					binomVerdPanel.kijkVerdelingNa = true;
					binomVerdPanel.antwoordVerdeling = launchState.getInt("antwoordVerdeling");
					
					if (binomVerdPanel.antwoordVerdeling == 0) 
					{
						//er moet een binomiale verdeling worden nagekeken
						if (launchState.containsKey("antwoordP")) 
						{
							binomVerdPanel.kijkPNa = true;
							invoer.setInput(launchState.getString("antwoordP"));
//							System.out.println("Kijk P na invoer: " + invoer.getInput());
							if (invoer.isRandomInput()) 
							{
								if(invoer.isBreuk()) 
								{
									double teller;
									double noemer;
									if (BVInvoer.isRandomVar(invoer.getTellerString())) 
									{
										teller = substitueerRandom(1, invoer.getTellerString(), randomVarNamen, randomVarWaarden);
									}
									else 
									{
										teller = Double.parseDouble(invoer.getTellerString());
									}
									if (BVInvoer.isRandomVar(invoer.getNoemerString())) 
									{
										noemer = substitueerRandom(1, invoer.getNoemerString(), randomVarNamen, randomVarWaarden);
									}
									else 
									{
										noemer = Double.parseDouble(invoer.getNoemerString());
									}
									binomVerdPanel.antwoordP = new BVInvoer(Double.toString(teller) + "/" + Double.toString(noemer));
								}
								else 
								{
									binomVerdPanel.antwoordP = new BVInvoer(Double.toString(
											substitueerRandom(0.5, invoer.getInput(), randomVarNamen, randomVarWaarden)));
								}
							}
							else 
							{
								/*
								if(invoer.isBreuk()) {
									this.antwoordP = Double.parseDouble(invoer.getTellerString()) / Double.parseDouble(invoer.getNoemerString());
								}
								else {
									this.antwoordP = Double.parseDouble(invoer.getInput());
								}*/
								binomVerdPanel.antwoordP = new BVInvoer(invoer.getInput());
							}
						}
						else 
						{
							binomVerdPanel.kijkPNa = false;
						}
					}
					else 
					{
						//er moet een hypergeometrische verdeling worden nagekeken
						if (launchState.containsKey("antwoordM")) {
							binomVerdPanel.kijkMNa = true;
							invoer.setInput(launchState.getString("antwoordM"));
							if (invoer.isRandomInput()) 
							{
								binomVerdPanel.antwoordM = (int) substitueerRandom(50, invoer.getInput(), randomVarNamen, randomVarWaarden);
							}
							else 
							{
								binomVerdPanel.antwoordM = Integer.parseInt(invoer.getInput());
							}
						}
						else 
						{
							binomVerdPanel.kijkMNa = false;
						}
						
						if (launchState.containsKey("antwoordPopulatie")) 
						{
							binomVerdPanel.kijkPopulatieNa = true;
							invoer.setInput(launchState.getString("antwoordPopulatie"));
							if (invoer.isRandomInput()) 
							{
								binomVerdPanel.antwoordPopulatie = (int) substitueerRandom(100, invoer.getInput(), 
																			randomVarNamen, randomVarWaarden);
							}
							else 
							{
								binomVerdPanel.antwoordPopulatie = Integer.parseInt(invoer.getInput());
							}
						}
						else 
						{
							binomVerdPanel.kijkPopulatieNa = false;
						}
					}
				}
				else 
				{
					binomVerdPanel.kijkVerdelingNa = false;
				}
				
			}
			else 
			{
				binomVerdPanel.kijkOpdrachtNa = false;
				binomVerdPanel.kijkGrenzenNa = false;
				binomVerdPanel.kijkNNa = false;
				binomVerdPanel.kijkMNa = false;
				binomVerdPanel.kijkVerdelingNa = false;
				binomVerdPanel.kijkPopulatieNa = false;
				binomVerdPanel.kijkPNa = false;
			}
			
		}
		else 
		{
			binomVerdPanel.kijkOpdrachtNa = false;
			binomVerdPanel.kijkGrenzenNa = false;
			binomVerdPanel.kijkNNa = false;
			binomVerdPanel.kijkMNa = false;
			binomVerdPanel.kijkVerdelingNa = false;
			binomVerdPanel.kijkPopulatieNa = false;
			binomVerdPanel.kijkPNa = false;
		}
		
		
		//System.out.println("AntwoordP = " + this.antwoordP.getInput());
		//System.out.println("invoerP = " + this.pInvoer.getInput());
		
		//if (binomVerdPanel.kijkOpdrachtNa)
		//	binomVerdPanel.plaatsComponenten(false);
		//binomVerdPanel.staafjesPanel.berekenStaafBreedte();
		//binomVerdPanel.staafjesPanel.bepaalGrenzenMetSlider();
		

		binomVerdPanel.vernieuw();
		
		binomVerdPanel.staafjesPanel.updateSuccessenSliderPosition();

		binomVerdPanel.plaatsComponenten(false);
		
		binomVerdPanel.paint();
		
		ingevuld = false;


	}

	public static double substitueerRandom(double def, String s, String[] randomVarNamen, HashMap randomVarWaarden) 
	{	double d = Double.NaN;
		s = s.substring(1, s.length() - 1);
		String[] delen = StringUtils.split(s, "/");
		int decFactor = 1;
	
		for (int j = 0 ; j < randomVarNamen.length; j++)
		{	
//System.out.println("rava " + j + " " + randomVars[j]);			
			if (randomVarNamen[j].equals(delen[0])) 
				d = ((Integer) randomVarWaarden.get(randomVarNamen[j])).intValue();
		}
		if (delen.length > 1)
		{	decFactor = Integer.parseInt(delen[1]);
			d = d / decFactor;
		}
		if (Double.isNaN(d)) 
			d = def;
		return d;
	}

}
