package fi.normverdgwt.client;

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


public class NormVerdGWT implements EntryPoint, InteractionStub, InteractionView 
{
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	// UI
	DockLayoutPanel dlp;
	//LayoutPanel bottomPanel;
	NormaalPanel normaalPanel;
	
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
	
	// images
	NormVerdGWTClientBundle normVerdGWTClientBundle;
	ImageResource foutKruisResource, goedKrulResource;
	Image foutKruisImage, goedKrulImage;

	private int mode;
	private OpdrNavIF comRoot;
	
	public void getImages() 
	{
		normVerdGWTClientBundle = GWT.create(NormVerdGWTClientBundle.class);
		
		foutKruisResource = normVerdGWTClientBundle.foutKruisResource();
		goedKrulResource = normVerdGWTClientBundle.goedKrulResource();
		foutKruisImage = new Image(foutKruisResource);
		goedKrulImage = new Image(goedKrulResource);
	}	
	
	public void onModuleLoad() 
	{
		getImages();
		
if (foutKruisImage == null)
System.out.println("fki is null");
else
System.out.println("fki not null");
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName("root");
	
		//Stub.publish(this);
		init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
		

			
	}
	
	public NormVerdGWT()
	{
		
	}
	
	public NormVerdGWT(HashMap<String,Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
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
//System.out.println("nvgwt getState");		
		return normaalPanel.getState();
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
//System.out.println("nvgwt setState");

		// let even op: als kanskeuze == TWEEGRENZEN actualMu/SigmaBerekenbaar = false;
		normaalPanel.setState(h);
	}

	@Override
	public int getScore()
	{
		// TODO Auto-generated method stub
		return normaalPanel.score;
	}

	@Override
	public Boolean isCorrect()
	{
		return normaalPanel.score == normaalPanel.maxScore; //Boolean.TRUE;
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
	public void kijkNa() 
	{
		normaalPanel.kijkNa();
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
	
	public int getMode() {
		return 0; //mode;
	}

	public void init(int width, int height, Map<String, Object> map, //launchState,
			Map<String, Number> values) 
	{
		getImages();
		
		this.breedte = width;
		this.hoogte = height;
		//this.launchState = launchState;
		ObjectMap launchState = JSONUtilities.wrapMap(map);

		// edit state variabelen
		// kans opties
		boolean kansLinksOptie = true;
		boolean kansRechtsOptie = true;
		boolean tweeGrenzenOptie = true;

		if (launchState.containsKey("kanslinksoptie"))
			kansLinksOptie = launchState.getBoolean("kanslinksoptie");
		if (launchState.containsKey("kansrechtsoptie"))
			kansRechtsOptie = launchState.getBoolean("kansrechtsoptie");
		if (launchState.containsKey("tweegrenzenoptie"))
			tweeGrenzenOptie = launchState.getBoolean("tweegrenzenoptie");
				
		// bereken opties
		boolean muBerekenbaarOptie = false;
		boolean actualMuBerekenbaarOptie = false;

		boolean sigmaBerekenbaarOptie = false;
		boolean actualSigmaBerekenbaarOptie = false;
		
		if (launchState.containsKey("muberekenbaaroptie"))
			muBerekenbaarOptie = launchState.getBoolean("muberekenbaaroptie");
		if (launchState.containsKey("sigmaberekenbaaroptie"))
			sigmaBerekenbaarOptie = launchState.getBoolean("sigmaberekenbaaroptie");

		
		// vaste waarde opties
		boolean muVastOptie = false;
		if (launchState.containsKey("muvastoptie"))
		{	muVastOptie = launchState.getBoolean("muvastoptie");
//System.out.println("contains muVastOptie");		
		}
		boolean sigmaVastOptie = false;
		if (launchState.containsKey("sigmavastoptie"))
			sigmaVastOptie = launchState.getBoolean("sigmavastoptie");

// dit moet naar setState in NormaalPanel		
		// correctie
		final int KANSLINKS = 0;
		final int KANSRECHTS = 1;
		final int TWEEGRENZEN = 2;
		int kansKeuze = KANSLINKS;
		if (launchState.containsKey("kansKeuze"))
			kansKeuze = launchState.getInt("kansKeuze");
		if (kansKeuze == TWEEGRENZEN)
		{	actualMuBerekenbaarOptie = false;
			actualSigmaBerekenbaarOptie = false;
		}
		else
		{	actualMuBerekenbaarOptie = muBerekenbaarOptie && !muVastOptie;
			actualSigmaBerekenbaarOptie = sigmaBerekenbaarOptie && !sigmaVastOptie;
		}
		
		// slider opties
		boolean muSliderOptie = true; //false;
		if (launchState.containsKey("muSliderOptie"))
			muSliderOptie = launchState.getBoolean("muSliderOptie");
		boolean sigmaSliderOptie = true; //false;
		if (launchState.containsKey("sigmaSliderOptie"))
			sigmaSliderOptie = launchState.getBoolean("sigmaSliderOptie");
		boolean grensSliderOptie = true;
		if (launchState.containsKey("grensSliderOptie"))
			grensSliderOptie = launchState.getBoolean("grensSliderOptie");
		boolean kansSliderOptie = true; //false;
		if (launchState.containsKey("kansSliderOptie"))
			kansSliderOptie = launchState.getBoolean("kansSliderOptie");
		
		// waarde zichtbaar opties
		boolean muZichtbaarOptie = true;
		if (launchState.containsKey("muZichtbaarOptie"))
			muZichtbaarOptie = launchState.getBoolean("muZichtbaarOptie");
		boolean sigmaZichtbaarOptie = true;
		if (launchState.containsKey("sigmaZichtbaarOptie"))
			sigmaZichtbaarOptie = launchState.getBoolean("sigmaZichtbaarOptie");
		boolean grensZichtbaarOptie = true;
		if (launchState.containsKey("grensZichtbaarOptie"))
			grensZichtbaarOptie = launchState.getBoolean("grensZichtbaarOptie");
		boolean kansZichtbaarOptie = true;
		if (launchState.containsKey("kansZichtbaarOptie"))
			kansZichtbaarOptie = launchState.getBoolean("kansZichtbaarOptie");
		
		// waarde zichtbaar in figuur opties
		boolean muZichtbaarFigOptie = true;
		if (launchState.containsKey("muZichtbaarFigOptie"))
			muZichtbaarFigOptie = launchState.getBoolean("muZichtbaarFigOptie");
		boolean sigmaZichtbaarFigOptie = true;
		if (launchState.containsKey("sigmaZichtbaarFigOptie"))
			sigmaZichtbaarFigOptie = launchState.getBoolean("sigmaZichtbaarFigOptie");
		boolean grensZichtbaarFigOptie = true;
		if (launchState.containsKey("grensZichtbaarFigOptie"))
			grensZichtbaarFigOptie = launchState.getBoolean("grensZichtbaarFigOptie");
		boolean kansZichtbaarFigOptie = true;
		if (launchState.containsKey("kansZichtbaarFigOptie"))
			kansZichtbaarFigOptie = launchState.getBoolean("kansZichtbaarFigOptie");

		// berekenkeuze zichtbaar
		boolean berekenbaarZichtbaar = true;
		if (launchState.containsKey("berekenbaarZichtbaar"))
			berekenbaarZichtbaar = launchState.getBoolean("berekenbaarZichtbaar");

		// nakijkopties
		boolean kijkOpdrachtNa = true; //false;
		boolean kijkMuNa = false;
		String checkMu = "0";
		double antwoordMu = 0;
		boolean kijkSigmaNa = false;
		String checkSigma = "1";
		double antwoordSigma = 1;
		boolean kijkGrensNa = false;
		String checkGrens = "";
		double antwoordGrens = 1;
		boolean kijkGrensLinksNa = false;
		String checkGrensLinks = "-1";
		double antwoordGrensLinks = -1;
		boolean kijkGrensRechtsNa = false;
		String checkGrensRechts = "1";
		double antwoordGrensRechts = 1;
		boolean kijkKansNa = false;
		String checkKans = "0.5";
		double antwoordKans = 5e-1d;
		int maxScore = 10;	
		int score = 0;
		
		if (launchState.containsKey("kijkNa"))
			kijkOpdrachtNa = launchState.getBoolean("kijkNa");
		if (launchState.containsKey("kijkMuNa"))
			kijkMuNa = launchState.getBoolean("kijkMuNa");
		if (launchState.containsKey("kijkSigmaNa"))
			kijkSigmaNa = launchState.getBoolean("kijkSigmaNa");
		if (launchState.containsKey("kijkGrensNa"))
			kijkGrensNa = launchState.getBoolean("kijkGrensNa");
		if (launchState.containsKey("kijkGrensLinksNa"))
			kijkGrensLinksNa = launchState.getBoolean("kijkGrensLinksNa");
		if (launchState.containsKey("kijkGrensRechtsNa"))
			kijkGrensRechtsNa = launchState.getBoolean("kijkGrensRechtsNa");
		if (launchState.containsKey("kijkKansNa"))
			kijkKansNa = launchState.getBoolean("kijkKansNa");
		if (launchState.containsKey("checkMu"))
			checkMu = launchState.getString("checkMu");
		if (launchState.containsKey("checkSigma"))
			checkSigma = launchState.getString("checkSigma");
		if (launchState.containsKey("checkGrens"))
			checkGrens = launchState.getString("checkGrens");
		if (launchState.containsKey("checkGrensLinks"))
			checkGrensLinks = launchState.getString("checkGrensLinks");
		if (launchState.containsKey("checkGrensRechts"))
			checkGrensRechts = launchState.getString("checkGrensRechts");
		if (launchState.containsKey("checkKans"))
			checkKans = launchState.getString("checkKans");
		if (launchState.containsKey("scoreMax"))
			maxScore = launchState.getInt("scoreMax");
		
		dlp.setSize(breedte + "px", hoogte + "px");		
		
		
		normaalPanel = new NormaalPanel(this, breedte, hoogte);
		
		Canvas normVerdGWTCanvas = normaalPanel.getCanvas();
		
		if (normVerdGWTCanvas == null) 
		{
	      RootPanel.get(holderId).add(new Label(upgradeMessage));
	      return;
	    }
		
		dlp.add(normaalPanel);
		
		normaalPanel.kansLinksOptie = kansLinksOptie;
		normaalPanel.kansRechtsOptie = kansRechtsOptie;
		normaalPanel.tweeGrenzenOptie = tweeGrenzenOptie;
		
		normaalPanel.kansKeuze = kansKeuze;
		
		normaalPanel.actualMuBerekenbaarOptie = actualMuBerekenbaarOptie;
		normaalPanel.actualSigmaBerekenbaarOptie = actualSigmaBerekenbaarOptie;
		normaalPanel.muBerekenbaarOptie = muBerekenbaarOptie;
		normaalPanel.sigmaBerekenbaarOptie = sigmaBerekenbaarOptie;
		
		normaalPanel.muVastOptie = muVastOptie;
		normaalPanel.sigmaVastOptie = sigmaVastOptie;
		
		normaalPanel.muSliderOptie = muSliderOptie;
		normaalPanel.sigmaSliderOptie = sigmaSliderOptie;
		normaalPanel.grensSliderOptie = grensSliderOptie;
		normaalPanel.kansSliderOptie = kansSliderOptie;

		normaalPanel.muZichtbaarOptie = muZichtbaarOptie;
		normaalPanel.sigmaZichtbaarOptie = sigmaZichtbaarOptie;
		normaalPanel.grensZichtbaarOptie = grensZichtbaarOptie;
		normaalPanel.kansZichtbaarOptie = kansZichtbaarOptie;
		
		normaalPanel.muZichtbaarFigOptie = muZichtbaarFigOptie;
		normaalPanel.sigmaZichtbaarFigOptie = sigmaZichtbaarFigOptie;
		normaalPanel.grensZichtbaarFigOptie = grensZichtbaarFigOptie;
		normaalPanel.kansZichtbaarFigOptie = kansZichtbaarFigOptie;
		
		normaalPanel.berekenbaarZichtbaar = berekenbaarZichtbaar;
		
		if (!checkMu.equals(""))
		{	checkMu = checkMu.replace(',', '.');	
			antwoordMu = Double.parseDouble(checkMu);
		}
		if (!checkSigma.equals(""))
		{	checkSigma = checkSigma.replace(',', '.');
			antwoordSigma = Double.parseDouble(checkSigma);
		}
		if (!checkGrens.equals(""))
		{	checkGrens = checkGrens.replace(',', '.');	
	 		antwoordGrens = Double.parseDouble(checkGrens);
		}
		if (!checkGrensLinks.equals(""))
	 	{	checkGrensLinks = checkGrensLinks.replace(',', '.');	
	 		antwoordGrensLinks = Double.parseDouble(checkGrensLinks);
	 	}
		if (!checkGrensRechts.equals(""))
		{	checkGrensRechts = checkGrensRechts.replace(',', '.');
			antwoordGrensRechts = Double.parseDouble(checkGrensRechts);
		}
		if (!checkKans.equals(""))
		{	checkKans  = checkKans.replace(',', '.');
			antwoordKans = Double.parseDouble(checkKans);
		}	
		
		normaalPanel.kijkOpdrachtNa = kijkOpdrachtNa;
		normaalPanel.kijkMuNa = kijkMuNa;
		normaalPanel.antwoordMu = antwoordMu;
		normaalPanel.kijkSigmaNa = kijkSigmaNa;
		normaalPanel.antwoordSigma = antwoordSigma;
		normaalPanel.kijkGrensNa = kijkGrensNa;
		normaalPanel.antwoordGrens = antwoordGrens;
		normaalPanel.kijkGrensLinksNa = kijkGrensLinksNa;
		normaalPanel.antwoordGrensLinks = antwoordGrensLinks;
		normaalPanel.kijkGrensRechtsNa = kijkGrensRechtsNa;
		normaalPanel.antwoordGrensRechts = antwoordGrensRechts;
		normaalPanel.kijkKansNa = kijkKansNa;
		normaalPanel.antwoordKans = antwoordKans;
		normaalPanel.maxScore = maxScore;	
		
		

		normaalPanel.plaatsComponenten(true);
		
		normaalPanel.init();
		
//System.out.println("init");		
		
		normaalPanel.setState(map);
		
		normaalPanel.paint();


	}
}
