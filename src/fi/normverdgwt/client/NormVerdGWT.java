package fi.normverdgwt.client;

import java.util.HashMap;
import java.util.Hashtable;
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

import java.util.logging.Logger;

import fi.normverdgwt.client.text.Text;

public class NormVerdGWT implements EntryPoint, InteractionStub, InteractionView 
{
	
	public static Text rb;
	
	private static Logger logger = Logger.getLogger("NormVerdGWT");
	
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

	static NormVerdGWTCssResource normVerdGWTCss;
	
	private int mode;
	private OpdrNavIF comRoot;
	
	boolean kijkOpdrachtNa = false;
	Boolean correct = true;
	boolean nagekeken = false;
	boolean ingevuld = false; 
	
	public void getImages() 
	{
		rb = GWT.create(Text.class);
		
		normVerdGWTClientBundle = GWT.create(NormVerdGWTClientBundle.class);
		normVerdGWTCss = normVerdGWTClientBundle.getNormVerdGWTCSS();
		normVerdGWTCss.ensureInjected();

		
		foutKruisResource = normVerdGWTClientBundle.foutKruisResource();
		goedKrulResource = normVerdGWTClientBundle.goedKrulResource();
		foutKruisImage = new Image(foutKruisResource);
		goedKrulImage = new Image(goedKrulResource);
	}	
	
	public void onModuleLoad() 
	{
		getImages();
		
//System.out.println("normverd onModuleLoad");

//if (foutKruisImage == null)
//System.out.println("fki is null");
//else
//System.out.println("fki not null");
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName("root");
	
		Stub.publish(this);
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
		

			
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
			e.preventDefault();
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
		HashMap<String, Object> h = normaalPanel.getState();
		h.put("nagekeken", new Boolean(nagekeken));
		h.put("ingevuld", new Boolean(ingevuld));
//System.out.println("nvgwt getState");		
		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
//System.out.println("nvgwt setState");

		// let even op: als kanskeuze == TWEEGRENZEN actualMu/SigmaBerekenbaar = false;
		normaalPanel.setState(h);
		
		ingevuld = false;
		
		if (h.containsKey("nagekeken"))
		{	nagekeken = ((Boolean) h.get("nagekeken")).booleanValue();
		}
		if (h.containsKey("ingevuld"))
			ingevuld = ((Boolean) h.get("ingevuld")).booleanValue();

		
		if (ingevuld &&(mode == 0 || nagekeken))
		//if (nagekeken)
			kijkNa();
		
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
		
		
		if (normaalPanel.kijkOpdrachtNa)
		{
//logger.info("isCorrect " + correct.toString());			
			return correct;
		}
		else
			return Boolean.TRUE;

	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
logger.info("setCommRoot");
				
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());

	}
	
	public void zetMode(int mode)
	{	this.mode = mode;
		if (normaalPanel.kijkOpdrachtNa)    
			normaalPanel.kijkOpdrachtNa = (mode == 0 || mode == 1);
	}
	
	public void changed()
	{
	  	if (normaalPanel.kijkOpdrachtNa) 
		{
	   		correct = null;
	   		nagekeken = false;
	    		
	   		ingevuld = true;
	   		comRoot.setChanged(true);
			   	
		}
	}

	@Override
	public void kijkNa() 
	{
//System.out.println("NV KijkNa");
		
		if (!kijkOpdrachtNa)
			return;

		// dit verandert correct
		normaalPanel.kijkNa();
		nagekeken = true;
   		ingevuld = true;
//System.out.println("cor " + isCorrect().booleanValue());		

logger.info("pre setChanged " + correct.toString());
if (comRoot == null)
logger.info("comRoot == null");	
		//comRoot.setChanged(isCorrect().booleanValue());
		comRoot.setChanged(isCorrect());
logger.info("pre setChanged " + correct.toString());		
		
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
	public int getHeight() 
	{	return hoogte;
	}

	@Override
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
	
	public int getMode() {
		return 0; //mode;
	}

	public void init(int width, int height, Map<String, Object> map, //launchState,
			Map<String, Number> values) 
	{
		getImages();
		
//System.out.println("normverd init");		
logger.info("NormVerdGWT init");		
		
		this.breedte = width;
		this.hoogte = height;
		
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		
		//this.launchState = launchState;
		ObjectMap launchState = JSONUtilities.wrapMap(map);

		double mu = 0;
		double sigma = 1;
		double grens = mu + 1;
		double grensLinks = mu - 1;
		double grensRechts = mu + 1;
		double kans = 6e-1d;
		int kansKeuze = NormaalPanel.KANSLINKS;
		int berekenKeuze = NormaalPanel.BEREKENKANS;
		
		if (launchState.containsKey("mu"))
			mu = launchState.getDouble("mu");
		if (launchState.containsKey("sigma"))
			sigma = launchState.getDouble("sigma");	
		if (launchState.containsKey("grens"))
			grens = launchState.getDouble("grens");	
		if (launchState.containsKey("grenslinks"))
			grensLinks = launchState.getDouble("grenslinks");	
		if (launchState.containsKey("grensrechts"))
			grensRechts = launchState.getDouble("grensrechts");	
		if (launchState.containsKey("kans"))
			kans = launchState.getDouble("kans");	
		
		if (launchState.containsKey("kanskeuze"))
			kansKeuze = launchState.getInt("kanskeuze");	
		if (launchState.containsKey("berekenkeuze"))
			berekenKeuze = launchState.getInt("berekenkeuze");	

		// randomizatie
		String muString = "";
		String sigmaString = "";
		String grensString = "";
		String grensLinksString = "";
		String grensRechtsString = "";
		String kansString = "";
		
		if (launchState.containsKey("muString"))
			muString = launchState.getString("muString");
		if (launchState.containsKey("sigmaString"))
			sigmaString = launchState.getString("sigmaString");
		if (launchState.containsKey("grensString"))
			grensString = launchState.getString("grensString");
		if (launchState.containsKey("grensLinksString"))
			grensLinksString = launchState.getString("grensLinksString");
		if (launchState.containsKey("grensRechtsString"))
			grensRechtsString = launchState.getString("grensRechtsString");
		if (launchState.containsKey("kansString"))
			kansString = launchState.getString("kansString");
		
		if (muString.length() > 0 && muString.charAt(0) == '#' && 
			muString.charAt(muString.length() - 1) == '#') 
				mu = substitueerRandom(mu, muString, randomVarNamen, randomVarWaarden);
		if (sigmaString.length() > 0 && sigmaString.charAt(0) == '#' && 
			sigmaString.charAt(sigmaString.length() - 1) == '#') 
				sigma = substitueerRandom(sigma, sigmaString, randomVarNamen, randomVarWaarden);
		if (grensString.length() > 0 && grensString.charAt(0) == '#' && 
			grensString.charAt(grensString.length() - 1) == '#') 
				grens = substitueerRandom(grens, grensString, randomVarNamen, randomVarWaarden);
		if (grensLinksString.length() > 0 && grensLinksString.charAt(0) == '#' && 
			grensLinksString.charAt(grensLinksString.length() - 1) == '#') 
				grensLinks = substitueerRandom(grensLinks, grensLinksString, randomVarNamen, randomVarWaarden);
		if (grensRechtsString.length() > 0 && grensRechtsString.charAt(0) == '#' && 
			grensRechtsString.charAt(grensRechtsString.length() - 1) == '#') 
				grensRechts = substitueerRandom(grensRechts, grensRechtsString, randomVarNamen, randomVarWaarden);
		if (kansString.length() > 0 && kansString.charAt(0) == '#' && 
			kansString.charAt(kansString.length() - 1) == '#') 
				kans = substitueerRandom(kans,kansString, randomVarNamen, randomVarWaarden);

		
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

		// blijft		
		// correctie
/*		
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
*/		
		
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
		//boolean kijkOpdrachtNa = false;
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
		
		normaalPanel.mu = mu;
		normaalPanel.sigma = sigma;
		normaalPanel.grens = grens;
		normaalPanel.grensLinks = grensLinks;
		normaalPanel.grensRechts = grensRechts;
		normaalPanel.kans = kans;
		normaalPanel.kansKeuze = kansKeuze;
		normaalPanel.berekenKeuze = berekenKeuze; 
		
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
		{	if (checkMu.charAt(0) == '#' && checkMu.charAt(checkMu.length() - 1) == '#') 
				antwoordMu = substitueerRandom(antwoordMu, checkMu, randomVarNamen, randomVarWaarden);
			else
			{	checkMu = checkMu.replace(',', '.');	
				antwoordMu = Double.parseDouble(checkMu);
			}	
		}
		if (!checkSigma.equals(""))
		{	if (checkSigma.charAt(0) == '#' && checkSigma.charAt(checkSigma.length() - 1) == '#') 
				antwoordSigma = substitueerRandom(antwoordSigma, checkSigma, randomVarNamen, randomVarWaarden);
			else 
			{	checkSigma = checkSigma.replace(',', '.');
				antwoordSigma = Double.parseDouble(checkSigma);
			}
		}
		if (!checkGrens.equals(""))
		{	if (checkGrens.charAt(0) == '#' && checkGrens.charAt(checkGrens.length() - 1) == '#') 
				antwoordGrens = substitueerRandom(antwoordGrens, checkGrens, randomVarNamen, randomVarWaarden);
			else
			{	checkGrens = checkGrens.replace(',', '.');	
	 			antwoordGrens = Double.parseDouble(checkGrens);
			}	
		}
		if (!checkGrensLinks.equals(""))
	 	{	if (checkGrensLinks.charAt(0) == '#' && checkGrensLinks.charAt(checkGrensLinks.length() - 1) == '#') 
	 			antwoordGrensLinks = substitueerRandom(antwoordGrensLinks, checkGrensLinks, randomVarNamen, randomVarWaarden);
	 		else
			{	checkGrensLinks = checkGrensLinks.replace(',', '.');	
	 			antwoordGrensLinks = Double.parseDouble(checkGrensLinks);
	 		}
	 	}
		if (!checkGrensRechts.equals(""))
		{	if (checkGrensRechts.charAt(0) == '#' && checkGrensRechts.charAt(checkGrensRechts.length() - 1) == '#') 
				antwoordGrensRechts = substitueerRandom(antwoordGrensRechts, checkGrensRechts, randomVarNamen, randomVarWaarden);
			else
			{	checkGrensRechts = checkGrensRechts.replace(',', '.');
				antwoordGrensRechts = Double.parseDouble(checkGrensRechts);
			}	
		}
		if (!checkKans.equals(""))
		{	if (checkKans.charAt(0) == '#' && checkKans.charAt(checkKans.length() - 1) == '#') 
				antwoordKans = substitueerRandom(antwoordKans, checkKans, randomVarNamen, randomVarWaarden);
			else
			{	checkKans  = checkKans.replace(',', '.');
				antwoordKans = Double.parseDouble(checkKans);
			}	
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
		
		normaalPanel.setInitState();
		
		normaalPanel.paint();
		
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
