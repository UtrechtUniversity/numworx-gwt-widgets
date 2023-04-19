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
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.resources.client.ImageResource;

import java.util.logging.Logger;

import fi.normverdgwt.client.text.Text;

/**
 * entry class voor de GWT-versie van het normale verdeling applet; 
 * voor de instellingen zie de Java-versie;<br>
 * het applet laat de kansverdeling van de normale verdeling zien;
 * deze kansverdeling is afhankelijk van 4 parameters:<br>
 * mu: de verwachtingswaarde; <br>
 * sigma: de wortel van de variantie; <br>
 * een grenswaarde G (twee grenswaarden L en R); <br>
 * de linker- of rechter kans d.w.z. de oppervlakte van de kansverdeling
 * links of rechts van de grens G (de oppervlakte tussen de grenzen L en G);<br>
 * de optie waarbij de gebruiker kan kiezen tussen 2 grenzen of linker- of rechter kans
 * is instelbaar; kan de gberuiker niet kiezen, dan is elk van deze
 * drie mogelijkheden instelbaar;<br>
 * als de waarden van drie van de vier parameters bekend zijn, dan kan de
 * waarde van de vierde parameter berekend worden;<br> 
 * rechtsboven kan d.m.v. radiobuttons gekozen worden welke van de 4 parameters 
 * berekend moet worden; deze keuze is weer instelbaar; <br>
 * linksboven kunnen de waarden van de parameters die niet berekend worden
 * aangepast worden d.m.v. een invulveld of (instelbaar) een slider; men kan
 * (instelbaar) mu en sigma een vaste waarde geven die niet veranderd kan worden.
 * de waarden van de parameters kunnen (instelbaar) verborgen worden
 * in de lijst linksboven of in de figuur<br>
 * NB: de waarden van de parameters kunnen ook gerandomiseerd worden.       
 */


public class NormVerdGWT implements EntryPoint, InteractionStub, InteractionView 
{
	public static Text rb;
	
	private static Logger logger = Logger.getLogger("NormVerdGWT");
	
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	/**
	 * basispanel, hangt aan de root
	 */
	DockLayoutPanel dlp;
	/**
	 * centre panel van dlp, zie klasse NormaalPanel
	 */
	NormaalPanel normaalPanel;

	/**
	 * default breedt en hoogte
	 */
	int breedte = 500;
	int hoogte = 450;

	/**
	 * launch data
	 */
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	Map<String, Number> randomVarWaarden = null;
	
	NormVerdGWTClientBundle normVerdGWTClientBundle;
	/**
	 * imageResources
	 */
	ImageResource foutKruisResource, goedKrulResource;
	/**
	 * images
	 */
	Image foutKruisImage, goedKrulImage;

	static NormVerdGWTCssResource normVerdGWTCss;
	
	private int mode;
	private OpdrNavIF comRoot;
	
	/**
	 * nakijken?
	 */
	boolean kijkOpdrachtNa = false;
	/**
	 * resultaat nakijken
	 */
	Boolean correct = true;
	/**
	 * is er een keer nagekeken?
	 */
	boolean nagekeken = false;
	/**
	 * heeft de gebruiker iets veranderd?
	 */
	boolean ingevuld = false; 
	
	/**
	 * fix ClientBundle, Css en images
	 */
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

		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		
		init(breedte, hoogte, launchState, randomVarWaarden);
		

	}

	public Widget asWidget()
	{
		return dlp;
	}
	
	/**
	 * sla de status van de opdracht in normaalPanel op in een HashMap;
	 * bewaar ook de nakijk-status
	 */
	public HashMap<String, Object> getState()
	{
		HashMap<String, Object> h = normaalPanel.getState();
		h.put("nagekeken", new Boolean(nagekeken));
		h.put("ingevuld", new Boolean(ingevuld));
		return h;
	}

	/**
	 * zet de status van de opdracht in normaalpanel; 
	 * zet ook de nakijk-status
	 */
	public void setState(HashMap<String, Object> h)
	{
		if(h == null || h.isEmpty()) return; 

		// let even op: als kanskeuze == TWEEGRENZEN actualMu/SigmaBerekenbaar = false;
		normaalPanel.setState(h);
		
		ingevuld = false;
		
		if (h.containsKey("nagekeken"))
		{	nagekeken = ((Boolean) h.get("nagekeken")).booleanValue();
		}
		if (h.containsKey("ingevuld"))
			ingevuld = ((Boolean) h.get("ingevuld")).booleanValue();

		if (ingevuld &&(mode == 0 || nagekeken))
			kijkNa();
		
	}

	public int getScore()
	{
		return normaalPanel.score;
	}

	public Boolean isCorrect()
	{
		if (normaalPanel.kijkOpdrachtNa)
		{
			return correct;
		}
		else
			return Boolean.TRUE;

	}

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

	/**
	 * er is iets veranderd, dus verwijder het 
	 * laatste nakijkresultaat
	 */
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

	public void kijkNa() 
	{
		if (!kijkOpdrachtNa)
			return;

		// dit verandert correct
		normaalPanel.kijkNa();
		nagekeken = true;
   		ingevuld = true;

		comRoot.setChanged(isCorrect());
		
		
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
	
	public int getMode() {
		return 0; //mode;
	}

	/**
	 * uit de launchdata lees: <br>
	 * de initiele waarden van de parameters, randomiseer als nodig; <br>
	 * de kansopties, de berekenbaaropties, de vaste waarde opties,
	 * de slider opties, de waarde zichtaar opties, de waarde zichtbaar
	 * in figuur opties en de nakijkopties; lees ook de antwoorden indien
	 * er nagekeken wordt;<br>
	 * creeer het normaalPanel, zet de opties in het normaalPanel,
	 * en initialiseer normaalPanel met de initiele parameterwaarden   
	 */
	public void init(int width, int height, Map<String, Object> map, 
					 Map<String, Number> values) 
	{
		getImages();
		
		randomVarWaarden = values;
		randomVarNamen = values.keySet().toArray(new String[values.size()]);
		
logger.info("NormVerdGWT init");		
		
		this.breedte = width;
		this.hoogte = height;
		
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		
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
		}
		boolean sigmaVastOptie = false;
		if (launchState.containsKey("sigmavastoptie"))
			sigmaVastOptie = launchState.getBoolean("sigmavastoptie");

		
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
		
		normaalPanel.setInitState();
		
		dlp.forceLayout();
		normaalPanel.forceLayout();
		
		normaalPanel.paint();
		
		ingevuld = false;


	}
	
	public static double substitueerRandom(double def, String s, String[] randomVarNamen, Map<String,Number> randomVarWaarden) 
	{	double d = Double.NaN;
		s = s.substring(1, s.length() - 1);
		String[] delen = StringUtils.split(s, "/");
		int decFactor = 1;
		
		for (int j = 0 ; j < randomVarNamen.length; j++)
		{	
			if (randomVarNamen[j].equals(delen[0])) 
				d = randomVarWaarden.get(randomVarNamen[j]).doubleValue();
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
