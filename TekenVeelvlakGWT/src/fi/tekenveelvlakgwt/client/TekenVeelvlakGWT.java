package fi.tekenveelvlakgwt.client;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.tekenveelvlakgwt.client.text.Text;

/**
 * entry class voor TekenVeelvlakGWT, zie ook de Java-versie waar alle instellingne gemaakt worden;
 * TekenVeelvlakGWT heeft drie instelbare verschijningsvormen:<br>
 * de tool: hiermee creeert de leerling 3d-figuren;<br>
 * de viewer: deze toont een 3d-figuur die (instelbaar) gedraaid kan worden; de leerling kan gevraagd worden 
 * om de 3d-figuur in een bepaalde stand te zetten (wordt nagekeken); een andere optie is dat de leerling gevraagd wordt
 * aan de hand van een of meer aanzichten een aantal vlakken in de 3d-figuur te kleuren door ze aan te klikken (wordt nagekeken); <br>
 * het vaktekpanel: dit toont een aantal aanzichten van een 3d-figuur; de leerling kan gevraagd worden
 * aan de hand van een voorbeeld een aantal vlakken in de aanzichten te kleuren, door ze aan te klikken (wordt nagekeken);<br>  
 * @author Peter Boon
 */

public class TekenVeelvlakGWT implements EntryPoint, InteractionStub, InteractionView 
{
	static final String holderId = "dockholder";
	private static final String LOG_OPTION = "logOption";

	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	/**
	 * hang dit aan de Root en stop hier alles in
	 */
	DockLayoutPanel dlp;
	/**
	 * internationalisatie
	 */
	public static Text rb;

	/**
	 * breedte en hoogte
	 */
	int breedte = 500;
	int hoogte = 450;

	/**
	 * launch data
	 */
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	TekenVeelvlakGWTClientBundle tekenVeelvlakGWTClientBundle;
	static TekenVeelvlakGWTCssResource tekenVeelvlakGWTCssResource;
	
	private int mode;
	private OpdrNavIF comRoot;
	private LessonMode lessonMode;

	/**
	 * instelbaarheid: toon viewer?
	 */
	boolean viewerOnly = false;
	/**
	 * instelbaarheid: toon aanzichten-panel?
	 */
	boolean profilesOnly = false;
	/**
	 * instelbaarheid: kan/moet de leerling vlakken kleuren?
	 */
	boolean vlakkenKleurenOptie;	
	/**
	 * het Veelvlak-tool
	 */
	TekenVeelvlak tvv;
	/**
	 * de viewer
	 */
	Viewer3d v3d;
	/**
	 * het aanzichten-panel
	 */
	VaktekPanel vaktek;
	
	/**
	 * instelbaarheid: nakijk-opties
	 */
	boolean kijkVlakkenNa = false;
	boolean kijkDraaihoekNa = false;
	boolean checkExternalVlakken = false;
	boolean checkExternalDraaihoek = false;
	/**
	 * kijkNaActief geeft aan of er iets nagekeken kan/moet worden
	 */
	boolean kijkNaActief = false;
	Boolean correct = null;
	/**
	 * is er al eens nagekeken?
	 */
	boolean nagekeken = false;
	/**
	 * is er iets door de leerling veranderd?
	 */
	boolean ingevuld = false;
	
	/**
	 * array met kleurennamen van de door de docent gekleurde vlakken (zelfde volgorde als de vlakken)
	 */
	String[] docentKleuren = null;
	/**
	 * door de docent ingestelde draaihoek-x
	 */
	double docentDraaihoekX = 1e5d;
	/**
	 * door de docent ingestelde draaihoek-y
	 */
	double docentDraaihoekY = 1e5d;
	int score;
	int scoreMax = 10;
	/**
	 * logging 
	 */
	public boolean logOption = false;
	
	/**
	 * viewer constanten, zie klasse Viewer3d
	 */
    static int MOVEABLE = 0;
    static int FRONTVIEW = 1;
    static int BACKVIEW = 2;
    static int TOPVIEW = 3;
    static int BOTTOMVIEW = 4;
    static int LEFTVIEW = 5;
    static int RIGHTVIEW = 6;

    static Logger logger = Logger.getLogger("TekenVeelvlakGWT");

    /**
     * initialiseer Css 
     */
	public void getImages() 
	{
		rb = GWT.create(Text.class);
		
		tekenVeelvlakGWTClientBundle = GWT.create(TekenVeelvlakGWTClientBundle.class);
		tekenVeelvlakGWTCssResource = tekenVeelvlakGWTClientBundle.getTekenVeelvlakGWTCssResource();
		tekenVeelvlakGWTCssResource.ensureInjected();
	} 	

	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(tekenVeelvlakGWTCssResource.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(tekenVeelvlakGWTCssResource.root());
		
		Stub.publish(this);
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
	
	}	
		
	public TekenVeelvlakGWT()
	{
	}
	
	public TekenVeelvlakGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
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
		dlp.addStyleName(tekenVeelvlakGWTCssResource.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		init(breedte, hoogte, launchState, randomVarWaarden);

	}
	
	public Widget asWidget()
	{
		return dlp; 
	}
	
	/**
	 * stop de huidige status in een HashMap: bij een viewer kan de leerling
	 * de figuur alleen draaien of kleuren (als dat mag), bij een aanzichten-panel
	 * kan de leerling de aanzichten alleen kleuren (als dat mag); er hoeft dus geen
	 * 3d-figuur onthouden te worden; dit moet natuurlijk wel bij de tool<br>
	 * onthoudt ook of de leerling iets veranderd heeft en/of nagekeken heeft
	 */
	public HashMap<String, Object> getState()
	{
		// hier onderscheid maken tussen TVV, Viewer, Profiles 		

		HashMap<String, Object> h = new HashMap<String, Object>();

		if (viewerOnly)
		{
			double viewerDraaiX = v3d.geefDraaiX();
			double viewerDraaiY = v3d.geefDraaiY();
    	
			h.put("viewerDraaiX", new Double(viewerDraaiX));
			h.put("viewerDraaiY", new Double(viewerDraaiY));
		}
		else if (profilesOnly)
		{
			
		}
		else if (tvv != null)
		{
			Map tvState = tvv.getState();
			h.put("tvState", tvState);
			
			double tekenVeelvlakDraaiX = tvv.geefDraaiX();
			double tekenVeelvlakDraaiY = tvv.geefDraaiY();
    	
			h.put("tekenVeelvlakDraaiX", new Double(tekenVeelvlakDraaiX));
			h.put("tekenVeelvlakDraaiY", new Double(tekenVeelvlakDraaiY));
			
		}
		
    	String[] leerlingKleuren = null;
    	
    	if (vlakkenKleurenOptie && viewerOnly)
    	{	leerlingKleuren = v3d.getKleuren();
    		ArrayList<String> leerlingKleurenAL = new ArrayList<String>();
    		for (int lk = 0; lk < leerlingKleuren.length; lk++)
    			leerlingKleurenAL.add(leerlingKleuren[lk]);
    		h.put("leerlingKleuren", leerlingKleurenAL);
    	}
    	
    	if (vlakkenKleurenOptie && profilesOnly)
    	{	leerlingKleuren = vaktek.getKleuren();
    		ArrayList<String> leerlingKleurenAL = new ArrayList<String>();
    		for (int lk = 0; lk < leerlingKleuren.length; lk++)
    			leerlingKleurenAL.add(leerlingKleuren[lk]);
    		h.put("leerlingKleuren", leerlingKleurenAL);
    	}
    	h.put("nagekeken", new Boolean(nagekeken));
    	h.put("ingevuld", new Boolean(ingevuld));

    	return h;

	}

	/**
	 * reconstrueer de laatste status uit een HashMap: zie methode getState; <br>
	 * als nodig, kijk na
	 */
	public void setState(HashMap<String, Object> map)
	{
		// hier onderscheid maken tussen TVV, Viewer, Profiles		
		
		if ((map == null) || map.isEmpty())
			return;
		
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		if (viewerOnly)
		{
	    	double viewerDraaiX = 20;
	    	double viewerDraaiY = -30;
	    	if (h.containsKey("viewerDraaiX"))
	    		viewerDraaiX = h.getDouble("viewerDraaiX"); 
	    	if (h.containsKey("viewerDraaiY"))
	    		viewerDraaiY = h.getDouble("viewerDraaiY");

	    	if (v3d.viewerPosition == MOVEABLE)
	    	{	v3d.zetBeginHoeken(viewerDraaiX, viewerDraaiY);
	    		v3d.tekenOpnieuw();
	    	}

		}
		else if (profilesOnly)
		{
			
		}
		else if (tvv != null)
		{
			Map tvState = null;
			if (h.containsKey("tvState"))
				tvState = h.getMap("tvState");
			tvv.setState(tvState);
			
	    	double tekenVeelvlakDraaiX = 20;
	    	double tekenVeelvlakDraaiY = -30;

	    	if (h.containsKey("tekenVeelvlakDraaiX"))
	    		tekenVeelvlakDraaiX = h.getDouble("tekenVeelvlakDraaiX"); 
	    	if (h.containsKey("tekenVeelvlakDraaiY"))
	    		tekenVeelvlakDraaiY = h.getDouble("tekenVeelvlakDraaiY");

	    	tvv.zetBeginHoeken(tekenVeelvlakDraaiX, tekenVeelvlakDraaiY);
		}
		
    	String[] leerlingKleuren = null; 
    	if (h.containsKey("leerlingKleuren"))
    		leerlingKleuren = h.getStringArray("leerlingKleuren");
    	
    	if (leerlingKleuren != null)
    	{	
    		if (vlakkenKleurenOptie && viewerOnly)
    		{	v3d.zetKleuren(leerlingKleuren);
    		    v3d.tekenOpnieuw();
    		}
    		if (vlakkenKleurenOptie && profilesOnly)	
    		{	vaktek.zetVaktekKleuren(leerlingKleuren);
    			vaktek.paint();
    		}
    	}	
    	
    	ingevuld = false;
    	
    	if (h.containsKey("nagekeken"))
    		nagekeken = h.getBoolean("nagekeken");
    	if (h.containsKey("ingevuld"))
    		ingevuld = h.getBoolean("ingevuld");
    	
    	if (ingevuld && (nagekeken || mode == 0))
    		kijkNa();
    	
	}

	public int getScore()
	{
		return score;
	}

	public Boolean isCorrect()
	{
		if (kijkNaActief)
			return correct;
		else
			return Boolean.TRUE;
	}

	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		zetMode(comRoot.getMode(),comRoot.getLessonMode());

	}
	
	public void zetMode(int mode, LessonMode lm)
	{
		this.mode = mode;
		this.lessonMode = lm;
		if (kijkNaActief)    
		{
			//kijkNaActief = (mode == 0 || mode == 1);
		}
	}
	
	/**
	 * maak een pijl die naar de voorkant van de 3d-figuur wijst
	 * @return de pijl als Veelvlak
	 */
	public Veelvlak maakPijl()
	{	
		
		double[] hp = {0, -0.6, -0.85,	0.01, -0.6, -0.85,	0.01, -0.6, -0.65, 0, -0.6, -0.65,
					   -0.05, -0.6, -0.75,	 0.05, -0.6, -0.75};
		int[] vl = {4,
					4,	0,1,2,3,
					4,	0,3,2,1,
					3,	3,4,5,
					3,	3,5,4};
		Veelvlak v = new Veelvlak(hp,vl);
		for (int i = 0; i < v.aantalVlakken; i++)
		{	v.vlakken[i].vulkleur="zwart";
		}
		return v;
	}

	/**
	 * lees alle opties (inclusief de nakijkopties) uit de launchdata;
	 * creeer een viewer, een vaktekpanel of een tool met daarin de 
	 * docent-figuur (if any); lees ook de nakijkopties en de voor
	 * het nakijken benodigde antwoorden
	 */
	public void init(int width, int height, Map<String, Object> map, 
			Map<String, Number> values) 
	{
		logger.info("TekenVeelvlakGWT init");
		
		this.breedte = width;
		this.hoogte = height;
		dlp.setPixelSize(width, height);

		ObjectMap launchState = JSONUtilities.wrapMap(map);
		
		// Wim: scoreMax uit launchstate halen.
		if(launchState.containsKey("scoreMax"))
			scoreMax = launchState.getInt("scoreMax");
		logOption = launchState.getBoolean(LOG_OPTION, logOption);
		
		boolean viewerOnly = false;
		boolean profilesOnly = false;
		Map tvState = null;
		
		if (launchState.containsKey("viewerOnly"))
			viewerOnly = launchState.getBoolean("viewerOnly");
		if (launchState.containsKey("profilesOnly"))
			profilesOnly = launchState.getBoolean("profilesOnly");
		
		this.viewerOnly = viewerOnly;
		this.profilesOnly = profilesOnly;
		
		if (launchState.containsKey("tvState"))
			tvState = launchState.getMap("tvState");

		// viewer
        int viewerPosition = 0;
        // tool
        int basisFiguur = 0;
        int aantalHulppunten = 0;

        if (launchState.containsKey("viewerPosition"))
        	viewerPosition = launchState.getInt("viewerPosition");
        if (launchState.containsKey("basisFiguur"))
        	basisFiguur = launchState.getInt("basisFiguur");
        if (launchState.containsKey("aantalHulppunten"))
        	aantalHulppunten = launchState.getInt("aantalHulppunten");
		
		boolean toonVooraanzichtPijl = false;
        if (launchState.containsKey("toonVooraanzichtPijl"))
          	 toonVooraanzichtPijl = launchState.getBoolean("toonVooraanzichtPijl");
        
        // vlakken kleuren in viewer of vaktekpanel
        boolean vlakkenKleurenOptie = false;
        if (launchState.containsKey("vlakkenKleurenOptie"))
          	 vlakkenKleurenOptie = launchState.getBoolean("vlakkenKleurenOptie");

		this.vlakkenKleurenOptie = vlakkenKleurenOptie;
		
        // vlakken al gekleurd in viewer of vaktekpanel
        List<String> viewerKleurenAL = null;
        String[] viewerKleuren = null;
        if (launchState.containsKey("viewerKleuren"))
         	 viewerKleurenAL = launchState.getStringList("viewerKleuren");
    	if (viewerKleurenAL != null)
    	{
    		viewerKleuren = new String[viewerKleurenAL.size()];
    		for (int vk = 0; vk < viewerKleurenAL.size(); vk++)
        		viewerKleuren[vk] = viewerKleurenAL.get(vk);
    	}

    	// vlakken gekleurd door docent in viewer of vaktekpanel, dit is een antwoord voor nakijken
        List<String> docentKleurenAL = new ArrayList<String>();
        if (launchState.containsKey("docentKleuren"))
        {
        	docentKleurenAL = launchState.getStringList("docentKleuren");
        }
    	if (docentKleurenAL != null)
    	{
    		docentKleuren = new String[docentKleurenAL.size()];
    		for (int dk = 0; dk < docentKleurenAL.size(); dk++)
        		docentKleuren[dk] = docentKleurenAL.get(dk);
    	}
        
        if (launchState.containsKey("kijkDraaihoekNa"))
        	kijkDraaihoekNa = launchState.getBoolean("kijkDraaihoekNa");
        if (launchState.containsKey("kijkVlakkenNa"))
        	kijkVlakkenNa = launchState.getBoolean("kijkVlakkenNa");
        if (launchState.containsKey("checkExternalDraaihoek"))
        	checkExternalDraaihoek = launchState.getBoolean("checkExternalDraaihoek");
        if (launchState.containsKey("checkExternalVlakken"))
        	checkExternalVlakken = launchState.getBoolean("checkExternalVlakken");
                
        if (launchState.containsKey("docentDraaihoekX"))
        	docentDraaihoekX = launchState.getDouble("docentDraaihoekX");
        if (launchState.containsKey("docentDraaihoekY"))
        	docentDraaihoekY = launchState.getDouble("docentDraaihoekY");

        this.kijkNaActief = kijkDraaihoekNa || kijkVlakkenNa || checkExternalDraaihoek || checkExternalVlakken;
    	
		dlp.setSize(breedte + "px", hoogte + "px");
		
		if (viewerOnly)
		{
			v3d = new Viewer3d(0,0,breedte, hoogte,this);
			v3d.initContext2d();
			
			if (kijkNaActief)
			{
				// kijkna panel toevoegen aan south van docklayoutpanel dlp
				dlp.addSouth(v3d.getKijkNaPanel(), 25);
			}

			dlp.add(v3d);
			
			if (tvState != null)
				v3d.setState(tvState);
			
			v3d.zetViewerPosition(viewerPosition);
			
			if (viewerKleuren != null)
				v3d.zetKleuren(viewerKleuren);
			
			if (toonVooraanzichtPijl)
	    	{	Veelvlak v3dPijl = maakPijl();
	    		v3d.voegVooraanzichtPijlToe(v3dPijl);
	    	}
			
			if (vlakkenKleurenOptie)
			{	v3d.zetKlikAan(true);
				v3d.zetVlakkenKleurenOptie(true);
			}
			
			dlp.forceLayout();
			v3d.forceLayout();
			v3d.kijkNaPanel.forceLayout();
			v3d.alles.forceLayout();
			v3d.paint();
			
		}
		else if (profilesOnly)
		{
			vaktek = new VaktekPanel(0,0,breedte, hoogte,this);
			vaktek.addStyleName(tekenVeelvlakGWTCssResource.canvas());
			
			vaktek.initContext2d();
			
			dlp.add(vaktek);

			if (tvState != null)
				vaktek.setState(tvState);

			if (viewerKleuren != null)
				vaktek.zetVaktekKleuren(viewerKleuren);
			
			if (vlakkenKleurenOptie)
			{	vaktek.zetKlikAan(true);
				vaktek.zetVlakkenKleurenOptie(true);
			}
			
			if (kijkNaActief)
			{
				vaktek.setWidgetVisible(vaktek.kijkNaPanel, true);
			}

			dlp.forceLayout();
			vaktek.forceLayout();
			vaktek.kijkNaPanel.forceLayout();
			
			vaktek.paint();
		}
		else // tool
		{
			tvv = new TekenVeelvlak(breedte,hoogte);
			tvv.initialiseer();
		
			dlp.add(tvv);
		
			tvv.zetBasis(basisFiguur,aantalHulppunten);
			
			if (tvState != null)
				tvv.setState(tvState);
			
			if (toonVooraanzichtPijl)
	    	{	Veelvlak tvPijl = maakPijl();
	    		tvv.toonVoorkantPijl(tvPijl);
	    	}

			dlp.forceLayout();
			tvv.forceLayout();
			tvv.rg.forceLayout();

			tvv.tekenOpnieuw();
		}	
		
		ingevuld = false;
	}
	
	/**
	 * antwoord veranderd, reset
	 */
    public void answerChanged()
	{
	  	if (kijkNaActief)
	   	{	
	   		correct = null;
	   		score = 0;
	   		nagekeken = false;
	   		ingevuld = true;
	   		if (mode == OpdrNavIF.EINDTOETS) kijkNa();
	   		else comRoot.setChanged(true);
	   	}	
	}

	public void setAttempt(Map<String, ?> parameters) {
		if (logOption && comRoot != null) {
			comRoot.fireEvent(new CBookEvent(this, LOG_OPTION, parameters));
			logger.info(parameters.toString());
		}
	}
	public void setAttempt() {
		if (logOption) {
// Build parameters voor logging: zie FormuleEditorWithAnswer.buildLoggingMap
			Map<String,Object> parameters = new HashMap<>();
			parameters.put("verb", "http://adlnet.gov/expapi/verbs/attempted"); // standaard voor "poging"
			if (isCorrect()!= null) parameters.put("success", isCorrect());
			parameters.put("score", Collections.singletonMap("raw", getScore()));
			
			parameters.put("response", "???"); 
			setAttempt(parameters);
		}
	}
	
	public void setAttempt(String changeLog) {
		if (logOption) {
// Build parameters voor logging: zie FormuleEditorWithAnswer.buildLoggingMap
// fixed keys: response, verb:
			Map<String,Object> parameters = new HashMap<>();
			parameters.put("response", changeLog);
			if (isCorrect()!= null) parameters.put("success", isCorrect());
			parameters.put("score", Collections.singletonMap("raw", getScore()));
			parameters.put("verb", "http://adlnet.gov/expapi/verbs/attempted"); // standaard voor "poging"
			setAttempt(parameters);
		}
	}

	/**
	 * kijk na, onderscheidt mogelijkheden   
	 */
	public void kijkNa() 
	{
    	if (!kijkNaActief)
    		return;
    	boolean showMark = mode == 1 || mode == 2 || lessonMode == LessonMode.review || lessonMode == LessonMode.browse;
    	String changeLog = "";
    	if (isVlakkenNakijkModus() && profilesOnly)
    	{
    		correct = vaktek.evalueer(docentKleuren);
    		changeLog = Arrays.toString(vaktek.getKleuren());
    		nagekeken = true;
    		vaktek.kijkNaPanel.setStyleName("fout", !correct);
    		
    	}
    	else if (isVlakkenNakijkModus() && viewerOnly)
    	{
    		correct = v3d.evalueer(docentKleuren);
    		changeLog = Arrays.toString(v3d.getKleuren());
    		nagekeken = true;
    		v3d.kijkNaPanel.setStyleName("goed", correct);
    		v3d.kijkNaPanel.setStyleName("fout", !correct);

    	}
    	else if (isDraaihoekNakijkModus())
    	{
    		correct = v3d.evalueer(docentDraaihoekX, docentDraaihoekY);
    		changeLog = "(" + v3d.geefDraaiX() + "," + v3d.geefDraaiY() + ")";
    		nagekeken = true;
    	}

		if (correct)
			score = scoreMax;
		
		if (correct && viewerOnly)
		{
    		v3d.kijkNaPanel.setStyleName("goed", showMark);
    		v3d.kijkNaPanel.setStyleName("fout", false);
		}
		else if (!correct && viewerOnly)
		{
			v3d.kijkNaPanel.setStyleName("goed", false);
    		v3d.kijkNaPanel.setStyleName("fout", showMark);
		}
		else if (correct && profilesOnly)
		{
			vaktek.kijkNaPanel.setStyleName("goed", showMark);
			vaktek.kijkNaPanel.setStyleName("fout", false);
		}
		else if (!correct && profilesOnly)
		{
			vaktek.kijkNaPanel.setStyleName("goed", false);
			vaktek.kijkNaPanel.setStyleName("fout", showMark);
		}
		
		ingevuld = true;
		setAttempt(changeLog);
    	comRoot.setChanged(isCorrect().booleanValue());
	}

	/**
	 * wordt de draaihoek nagekeken (mogelijk extern)?
	 * @return true/false
	 */
    private boolean isDraaihoekNakijkModus()
    {
    	boolean b = false;
    	
    	b = kijkDraaihoekNa || checkExternalDraaihoek;
    	
    	return b;
    }
    
    /**
     * worden vlakken nagekeken (mogelijk extern)
     * @return true/false
     */
    private boolean isVlakkenNakijkModus()
    {
    	boolean b = false;
    	
    	b = kijkVlakkenNa || checkExternalVlakken;
    	
    	return b;
    }
    
    /**
     * wordt er extern nagekeken (draaihoek of vlakken)? 
     * @return true/false
     */
    boolean isCheckExternalModus()
    {
    	boolean b = false;
    	
    	b = checkExternalDraaihoek || checkExternalVlakken;
    	
    	return b;
    }
    
	public void zetVolledigeBreedte(int breedte) 
	{
	}

	public int getAsHoogte() 
	{
		return 0;
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
	}

	public void zetNagekeken(boolean b) 
	{
	}

	public int[][] getScoreObjectives() 
	{
		return null;
	}

	/**
	 * getter for CssResource 
	 * @return tekenVeelvlakGWTCssResource
	 */
	public TekenVeelvlakGWTCssResource getTekenVeelvlakCss()
	{
		return tekenVeelvlakGWTCssResource;
	}

	/**
	 * wordt er nagekeken?
	 * @return true/false
	 */
	public boolean isNakijkModus()
	{
    	boolean b = false;
    	
    	b = kijkNaActief;
    	
    	return b;
	}
}
