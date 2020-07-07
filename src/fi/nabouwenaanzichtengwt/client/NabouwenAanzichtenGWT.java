package fi.nabouwenaanzichtengwt.client;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import fi.nabouwenaanzichtengwt.client.text.Msgs;
import fi.nabouwenaanzichtengwt.client.text.Text;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * hoofdklasse voor NabouwenAanzichtenGWT, voor de instellingen zie ook de Java-versie; <br>
 * afhankelijk van deze instellingen verschijnt de instantie van NabouwenAanzichten als:<br>
 * A) een panel met 3, 2 of een onveranderbare aanzichten;<br>
 * B) een viewer3d met daarin een kubusbouwsel weergegeven als<br>
 * B1) als (onveranderbaar) bovenaanzicht al dan niet met hoogtes<br>
 * B2) als kubusbouwsel dat (instelbaar) gedraaid kan worden, waaraan (instelbaar)
 * kubusjes kunnen worden toegevoegd of waarvan kubusjes kunnen worden verwijderd,
 * dit toevoegen/verwijderen kan (instelbaar) via een aparte keuze bouwen/slopen, of 
 * toevoegen via mouse klik en verwijderen via long mouse klik; ook kan het kubusbouwsel
 * weergegeven woden als silhouet (instelbaar); verder wordt (instelbaar) het aantal kubusjes getoond
 * en is er (instelbaar) een knop die het bouwsel helemaal vol of helemaal leeg maakt.<br>     
 * voor de nakijkopties zie de Java-versie; <br>
 * CBook-communicatie:<br>
 * 1) de gebruiker bouwt/sloopt in een instantie van NabouwenAanzichtenGWT met een Viewer3d en
 * het resultaat wordt getoond in een instantie van NabouwenAanzichtenGWT met een Viewer3d of een
 * VaktekPanel met aanzichten; <br>
 * 2) de gebruiker bouwt/sloopt in een instantie van NabouwenAanzichtenGWT met een Viewer3d en
 * de gebruikte bouw- en sloopopdrachten worden als bouwprogramma getoond in een TekstVak<br>
 * 3) de gebruiker typt een bouwprogramma bestaande uit bouw- en sloopopdrachten in in een TekstVak
 * en het resultaat wordt getoond in een instantie van NabouwenAanzichtenGWT met een Viewer3d of een
 * VaktekPanel met aanzichten; <br>   
 * @author Peter Boon
 */

public class NabouwenAanzichtenGWT implements EntryPoint, InteractionStub, InteractionView, CBookEventListener
{
	private static final String LOG_OPTION = "logOption";
	/**
	 * CBook constantes
	 */
	public static final String TEXT_CSV = "text.csv";
	public static final String ACTION_CORRECT = "action.correct";
	public static final String ACTION_FALSE = "action.false";
	public static final String ACTION_FALSE2 = "action.false_2";

	/**
	 * voorgedefinieerde CBook Events
	 */
	private static final CBookEvent EVENT_CORRECT = new CBookEvent(ACTION_CORRECT); 
	private static final CBookEvent EVENT_FALSE = new CBookEvent(ACTION_FALSE); 
	private static final CBookEvent EVENT_FALSE2 = new CBookEvent(ACTION_FALSE2); 

    static Logger logger = Logger.getLogger("NabouwenaanzichtenGWT");
    /**
     * internationalisatie
     */
	static final Text rb = GWT.create(Text.class);

	static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	/**
	 * Canvas van de Viewer3d (als die er is)
	 */
	Canvas canvas;
	
	OpdrNavIF comRoot;
	int mode;

	/**
	 * breedte en hoogte
	 */
	private int breedte = 600;
	private int hoogte = 250;
	
	/**
	 * launch data
	 */
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	Map<String, ?> randomVarWaarden = null;
	/**
	 * logging 
	 */
	public boolean logOption = false;
	
	
	/**
	 * main panel
	 */
	LayoutPanel panel = new LayoutPanel();
	
	/**
	 * RadioButton groep bouwen/slopen
	 */
	String bouwenSlopenGroup = "bouwenSlopenGroup";
	/**
	 * bouwen RadioButton
	 */
	RadioButton bouwenButton;
	/**
	 * slopen RadioButton
	 */
	RadioButton slopenButton;

	/**
	 * PushButtons om het kubusrooster helemaal vol of helemaal leeg
	 * te maken; gebruik twee Pushbuttons, waarvan er steeds een zichtbaar is,
	 * i.p.v. het opschrift van een PushButton te veranderen (werkt niet goed) 
	 */
	PushButton volButton = new PushButton(rb.maakVol());
	PushButton leegButton = new PushButton(rb.maakLeeg());

	/**
	 * Label dat het aantal blokjes in het kubusbouwsel weergeeft
	 */
	Label blokjesLabel;
	
	/**
	 * wordt er extern nagekeken?
	 */
	private boolean checkExternal = false;

	/**
	 * nakijkknop  
	 */
	PushButton kijkNaButton = new PushButton(rb.kijkNa());
	/**
	 * panel voor nakijkknop
	 */
	LayoutPanel kijkNaPanel = new LayoutPanel();
	
	/**
	 * is het kubusbouwsel veranderd na de laatste keer dat er nagekeken werd?
	 */
	private boolean isVeranderdNaNakijken = false;
	
	/**
	 * de viewer met het kubusbouwsel
	 */
	private Viewer3d vWerk = null;
	/**
	 * het Panel met 1,2 of 3 aanzichten
	 */
	private VaktekPanel vaktekPanel = null;
	
	/**
	 * nakijk-klasse, zie aldaar
	 */
	private NabouwenAanzichtenChecker naChecker;

	/**
	 * nakijk paremeters
	 */
	private int goedHalfFout;
	private int score = 0;
    private int errorCount;
	Boolean correct = null;
	private String feedback = "";
    private boolean changed = false;
    private int foutStraf = 2;

    /**
     * het kubusbouwsel als silhouet tonen (d.w.z. helemaal zwart)?
     */
	boolean silhouet = false;
	
	/**
	 * is er een keer nagekeken?
	 */
	boolean nagekeken;
	/**
	 * is het initiele kubus bouwsel veranderd?
	 */
	boolean ingevuld;

	/**
	 * moet er nagekeken worden?
	 */
	boolean kijkNaActief = false;

	/**
	 * het kubusrooster, zie klasse KubusRooster
	 */
	private KubusRooster startKr;
	/**
	 * default waarde maximum zijde van het kubusrooster (dus maximaal 
	 * maxAantal x maxAantal x maxAantal kubusjes), wordt uitgelezen
	 * uit de launch data
	 */
	int maxAantal = 4;
	/**
	 * boolean rooster voor het KubusRooster: true op een
	 * positie betekent dat zich daar een kubusje bevindt 
	 */
	boolean[][][] b;

	NabouwenAanzichtenGWTCssResource nabouwenAanzichtenCss;
	
	/**
	 * String met bouwprogramma voor CBook
	 */
	String buildHistory = "";

	public NabouwenAanzichtenGWT()
	{

	}

	public NabouwenAanzichtenGWT(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		makeResources();

		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		if (h != null && h.get("breedte") != null)
			breedte = ((Number) h.get("breedte")).intValue();
		if (h != null && h.get("hoogte") != null)
			hoogte = ((Number) h.get("hoogte")).intValue();
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");

		init(breedte, hoogte, launchState, randomVarWaarden);
		
	}

	NabouwenAanzichtenGWTClientBundle clientBundle = GWT.create(NabouwenAanzichtenGWTClientBundle.class);
	private LessonMode lessonMode;

	public void makeResources()
	{
		nabouwenAanzichtenCss = clientBundle.getNabouwenAanzichtenGWTCSS();
		nabouwenAanzichtenCss.ensureInjected();
	}

	public void onModuleLoad()
	{
		makeResources();
		RootPanel.get().add(panel);
		
		Stub.publish(this);
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
	}


	/**
	 * inner class voor Click Events op de kijkNaButton, de
	 * volButton en de leegButton; van de vvolButton en de leegButton
	 * is er steeds maar een zichtbaar
	 */
    class PushClickHandler implements ClickHandler
    {
    	public void onClick(ClickEvent e)
    	{
			e.stopPropagation();
    		
    		if (e.getSource() == kijkNaButton)
    		{
    			kijkNa();
    		}
    		else if (e.getSource() == volButton)
    		{
    			panel.setWidgetVisible(volButton, false);
    			panel.setWidgetVisible(leegButton, true);
    			buildHistory = "";
    			vWerk.kr.maakVol();
    			vWerk.tekenOpnieuw();
    			zetVeranderd(false);
    			setChanged(true);
    			if (nagekeken)
    				zetIsVeranderdNaNakijken(true);
    		}
    		else if (e.getSource() == leegButton)
    		{
    			panel.setWidgetVisible(volButton, true);
    			panel.setWidgetVisible(leegButton, false);
    			vWerk.kr.maakLeeg();
    			buildHistory = "";
    			vWerk.tekenOpnieuw();
    			zetVeranderd(false);
    			
    			setChanged(true);
    			if (nagekeken)
    				zetIsVeranderdNaNakijken(true);
    		}
    	}
    }
    
	/**
	 * Retourneert true als nabouwen aanzichten in de nakijk-modus staat en
	 * moet nakijken. 
	 * @return true/false
	 */
	private boolean isNakijkModus()
	{
		return kijkNaActief || checkExternal;
	}
		
	private void fireEvent(CBookEvent event) 
	{
		comRoot.fireEvent(event);
	}

	public Panel getAsPanel()
	{
		return panel;
	}

	/**
	 * er is iets veranderd aan het bouwsel;
	 * update het blokjes-label; fire CBook Events;
	 * als state == false reset het kijknagebeuren
	 * (d.w.z.clear de feedback) 
	 * @param state true: aangeroepen tijdens setState
	 */
	void zetVeranderd(boolean state)
	{
		zetVeranderd(state, null);
	}
	void zetVeranderd(boolean state, String changeLog)
	{
		if ((blokjesLabel != null) && (vWerk != null))
		{
			Msgs msgs = GWT.create(Msgs.class);
			
			int aantal = vWerk.kr.geefAantalK();
			blokjesLabel.setText(msgs.blokjes(aantal));
		}

		if (vWerk != null)
		{	
			boolean[][][] booleanKR = vWerk.kr.geefBooleanRooster();
			int krSize = vWerk.kr.maxAantal;
			boolean[] booleanArray = boolKRtoBoolArray(booleanKR, krSize);
			
			Map<String,Object> map = new HashMap<String,Object>();
						
			map.put("krSize", krSize);
			map.put("booleanArr", booleanArray);
			
 			comRoot.fireEvent(new CBookEvent(this,"blockBuilding", map));
			
			String lastBuildCommand = vWerk.getLastBuildCommand();
			if (!"".equals(lastBuildCommand))
				buildHistory = buildHistory + lastBuildCommand + "\n";
			Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("content", buildHistory);
			comRoot.fireEvent(new CBookEvent(this,"text.buildingProgram", map1));
		}

		if (vWerk == null || !isNakijkModus())
			return;
		
		ingevuld = true;
		
		if (state) 
			return;
		
		clearFeedbackImages();
		
		correct = false;
		score = 0;
		if (!startKr.isGelijk(vWerk.kr))
			ingevuld = true;
		else
			ingevuld = false;
		if(changeLog != null)
		{
			if (mode == OpdrNavIF.EINDTOETS)
				kijkNa(false, false);
			setAttempt(changeLog);
		}
	}

	/**
	 * Haal vinkje/kruis weg.
	 */
	private void clearFeedbackImages()
	{
		kijkNaPanel.setStyleName("fout", false);
		kijkNaPanel.setStyleName("half", false);
		kijkNaPanel.setStyleName("goed", false);
	}

	/**
	 * is de bouwenButton aangevinkt?
	 * return true als er geen bouwenButton is (dan kunnen we altijd bouwen)
	 * @return true/false
	 */
	boolean isBouwen()
	{
		if (bouwenButton != null)
			return bouwenButton.getValue();
		else
			return true;
	}

	/**
	 * stop het huidige kubusbouwsel (als een
	 * boolean rooster) in een HashMap; bewaar ook
	 * de actuele nakijkparameters
	 * bij een VakTekPanel is er niets te doen, want daar zit het kubusbouwsel in
	 * de launchdate en kan niet door de leerling veranderd worden
	 */
	public HashMap<String, Object> getState()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		if (vWerk == null)
			return h;

		kijkNa(false, false);
		
		boolean[][][] stateNew = null;
		int errorCount = this.errorCount;
		stateNew = vWerk.kr.geefBooleanRooster();

		h.put("silhouet", silhouet);
		h.put("stateNew", stateNew);
		h.put("nagekeken", nagekeken);
		h.put("ingevuld", ingevuld);
		h.put("isVeranderdNaNakijken", new Boolean(isVeranderdNaNakijken));
        h.put("errorCount", new Integer(errorCount));

		return h;
	}

	/**
	 * roep setState0 aan, vang Exceptions af
	 */
	public void setState(HashMap<String,Object> h)
	{
		try
		{
			setState0(h);
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, "setState" , e);
		}
	}
	
	/**
	 * haal het boolean kubusrooster en de laatste nakijkparemeters uit de HashMap,
	 * maak hiervan een kubusbouwsen, stop dit in de viewer3d en kijk na als
	 * nodig;<br>
	 * bij een VakTekPanel is er niets te doen, want daar zit het kubusbouwsel in
	 * de launchdate en kan niet door de leerling veranderd worden
	 * @param h HashMap met boolean kubusrooster en nakijkparameters
	 */
	private void setState0(HashMap<String, Object> h)
	{
		if (vWerk == null || h == null)
			return;

		Object stateNew = null;

		if (h.containsKey("stateNew"))
			stateNew = h.get("stateNew");
		if (stateNew != null)
		{
			if (stateNew instanceof boolean[][][])
			{
				vWerk.zetKubusRooster(new KubusRooster((boolean[][][]) stateNew, 1));
			}
			else if (stateNew instanceof Object[])
			{
				vWerk.zetKubusRooster(new KubusRooster(KubusRooster.toBooleanArray((Object[]) stateNew), 1));
			}
			vWerk.draw();
			boolean silhouet = false;
			if (launchState.containsKey("silhouet"))
			{
				silhouet = ((Boolean) launchState.get("silhouet")).booleanValue();
			}
			this.silhouet = silhouet;
			if (silhouet)
			{	
				vWerk.zetKlikAan(false);
				vWerk.zetSchaduw(false);
				vWerk.kr.zetVulkleur("zwart");
				vWerk.draw();
			}
			
			// Bug: als "volleegoptie" false is, crashed panel.setWidgetVisible met een NPE			
			if (vWerk.kr.isVol() && volButton.getParent() == panel && leegButton.getParent() == panel)
			{
    			panel.setWidgetVisible(volButton, false); // assert volButton.getParent() == panel anders niet goed!!
    			panel.setWidgetVisible(leegButton, true);

			}
			if (vWerk.kr.isLeeg() && volButton.getParent() == panel && leegButton.getParent() == panel)
			{
    			panel.setWidgetVisible(volButton, true);
    			panel.setWidgetVisible(leegButton, false);
				
			}
		}

		ingevuld = false;
		if (h.containsKey("nagekeken"))
			nagekeken = (Boolean) h.get("nagekeken");
		if (h.containsKey("ingevuld"))
			ingevuld = (Boolean) h.get("ingevuld");
		if (h.containsKey("isVeranderdNaNakijken"))
			isVeranderdNaNakijken = ((Boolean) h.get("isVeranderdNaNakijken")).booleanValue();
	    if (h.get("errorCount") != null) 
	    	this.errorCount = ((Number)h.get("errorCount")).intValue();

		setChanged(false);
		if (nakijkenNodig())
			kijkNa(true);
		
		zetVeranderd(nagekeken);
	}

	void setChanged(boolean c)
	{
		changed = c;
	}
	
	/**
	 * Retourneert true als er nagekeken moet worden (en vinkje/kruis getoond).
	 * Retourneert false als er niet nagekeken moet worden.
	 * @return true/false
	 */
	boolean nakijkenNodig()
	{
		boolean nodig = false;
		
		if (ingevuld)
		{
			if (mode == OpdrNavIF.ZELFTOETS && nagekeken && !isVeranderdNaNakijken)
			{
				nodig = true;
			}
			else if ((checkExternal && mode != OpdrNavIF.ZELFTOETS) || mode == OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN)
			{
				nodig = true;
			}
		}
		
		return nodig;
	}

    public void verhoogErrorCount()
    {
    	if (isChanged())
    		errorCount++;
    	setChanged(false);
    }
    
	public boolean isChanged()
	{
		return changed;
	}

	public int getScore()
	{
		return score;
	}

	public Boolean isCorrect()
	{
		if (!isNakijkModus())
			return Boolean.TRUE;
		if (!nagekeken) 
			return null;
		return correct;
	}

	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		zetMode(comRoot.getMode(),comRoot.getLessonMode());
		CssColor background = comRoot.getBackground();
		panel.getElement().getStyle().setBackgroundColor(background.value());
		if (vWerk != null)
		{	vWerk.zetAchtergrond(background);
			vWerk.tekenOpnieuw();
		}

		comRoot.addCBookEventListener("text.buildingProgram", this);
		comRoot.addCBookEventListener("blockBuilding", this);
	}
	
	public void zetMode(int mode, LessonMode lessonMode)
	{
		this.mode = mode;
		this.lessonMode = lessonMode;
		if (isNakijkModus())
		{
			kijkNaActief = (mode == 0 || mode == 1);
		}
	}

	public Widget asWidget()
	{
		return getAsPanel();
	}

	/**
	 * lees het kubusbouwsel uit de launchdata (if any) en lees alle
	 * instellingen uit de lauchdata; bepaal of deze instantie van
	 * NabouwenAanzichten een VaktekPanel is of een Viewer3d; 
	 * voeg een bouwen/slopen panel toe (indien gewenst), een 
	 * kubusjesteller (indien gewenst) en een kijkna-panel
	 * (ndien gewenst)
	 */
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) 
	{
		breedte = width;
		hoogte  = height;

logger.info("NabouwenAanzichtenGWT init");
		
		launchState = launchData;
		ObjectMap launchMap = JSONUtilities.wrapMap(launchData);
		randomVarWaarden = values;
		randomVarNamen   = values.keySet().toArray(new String[values.size()]);

		logOption = launchMap.getBoolean(LOG_OPTION, logOption);
		
		panel.setSize(breedte + "px", hoogte + "px");

		Object stateNew = null;

		if (launchState.containsKey("stateNew"))
		{
			stateNew = launchState.get("stateNew");
		}
		else if (launchState.containsKey("state"))
		{
			stateNew = launchState.get("state");
			if (stateNew instanceof List) 
			{
				stateNew = ((List)stateNew).get(0);
				maxAantal = ((List)stateNew).size();
			}
			else if (stateNew instanceof Object[]) 
			{
				stateNew = ((Object[])stateNew)[0];
				maxAantal = ((Object[])stateNew).length;
			}
		}
		if (launchState.containsKey("maxAantal"))
			maxAantal = ((Number) launchState.get("maxAantal")).intValue();

		b = new boolean[maxAantal][maxAantal][maxAantal];
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					b[i][j][k] = false;
				}
			}
		}
		if (stateNew != null)
		{
			// Complicaties: stateNew is Object[] als afkomstig via stub
			// stateNew is List<?> als afkomstig via constructor
			
			if (stateNew instanceof List)
			{
				List<List<List<Boolean>>> stateLst = (List<List<List<Boolean>>>) stateNew;
				b = new boolean[maxAantal][maxAantal][maxAantal];
				for (int i = 0; i < stateLst.size(); i++)
				{
					for (int j = 0; j < stateLst.get(i).size(); j++)
					{
						for (int k = 0; k < stateLst.get(i).get(j).size(); k++)
						{
							b[i][j][k] = stateLst.get(i).get(j).get(k);
						}
					}
				}
			} 
			else if (stateNew instanceof Object[]) 
			{
				final boolean[][][] bb = KubusRooster.toBooleanArray((Object[]) stateNew);
				b = new boolean[maxAantal][maxAantal][maxAantal];
				for (int i = 0; i < bb.length; i++)
				{
					for (int j = 0; j < bb[i].length; j++)
					{
						for (int k = 0; k < bb[i][j].length; k++)
						{
							b[i][j][k] = bb[i][j][k];
						}
					}
				}
			}
		}

		
		boolean rotatieVast = false;		//impl 
		double beginHoekX = 30;				//impl
		double beginHoekY = -30;			//impl
		boolean nietBouwenSlopen = false;	//impl
		boolean keuzeBouwenSlopen = false;	//impl
		boolean perspectief = true;			//impl
		boolean volLeegOptie = false;		//impl
		boolean aantalBlokjes = false;		//impl
		
		boolean pijlAan = true;				//impl
		boolean balkAan = false;			//impl
		boolean bovenAanzichtMetHoogtes = false; //impl
		boolean maakAanzicht = false;		//impl
		
	    boolean blokkenBouwsel = true;		//impl
	    boolean silhouet = false;			//impl
	    boolean drieAanzichten = false;		//impl
	    boolean voorZijAanzicht = false;	//impl
	    boolean bovenAanzicht = false;
	    boolean voorAanzicht = false;
	    boolean rechtsAanzicht = false;

	    boolean kijkNaActief = false;		//impl
	    
		if (launchState.containsKey("volLeegOptie"))
			volLeegOptie = ((Boolean) launchState.get("volLeegOptie")).booleanValue();
		
		if (launchState.containsKey("keuzeBouwenSlopen"))
			keuzeBouwenSlopen = ((Boolean) launchState.get("keuzeBouwenSlopen")).booleanValue();
		
		if (launchState.containsKey("aantalBlokjes"))
			aantalBlokjes = ((Boolean) launchState.get("aantalBlokjes")).booleanValue();

		if (launchState.containsKey("kijkNaActief"))
			kijkNaActief = ((Boolean) launchState.get("kijkNaActief")).booleanValue();
		
		if (launchMap.containsKey("checkExternal"))
			checkExternal = launchMap.getBoolean("checkExternal");

		this.kijkNaActief = kijkNaActief; 

		if (launchState.containsKey("drieAanzichten"))
			drieAanzichten = ((Boolean) launchState.get("drieAanzichten")).booleanValue();

		if (launchState.containsKey("voorZijAanzicht"))
			voorZijAanzicht = ((Boolean) launchState.get("voorZijAanzicht")).booleanValue();
		
		if (launchState.containsKey("bovenAanzicht"))
			bovenAanzicht = ((Boolean) launchState.get("bovenAanzicht")).booleanValue();
		
		if (launchState.containsKey("voorAanzicht"))
			voorAanzicht = ((Boolean) launchState.get("voorAanzicht")).booleanValue();
				
		if (launchState.containsKey("rechtsAanzicht"))
			rechtsAanzicht = ((Boolean) launchState.get("rechtsAanzicht")).booleanValue();

		if (launchMap.containsKey("bovenAanzichtMetHoogtes"))
		{
			bovenAanzichtMetHoogtes = launchMap.getBoolean("bovenAanzichtMetHoogtes");
			if(bovenAanzichtMetHoogtes)
				rechtsAanzicht = false;
		}
		
		if (drieAanzichten)
		{
			startKr = new KubusRooster(b, 1.5);
			vaktekPanel = new VaktekPanel(startKr, breedte, hoogte, 3, this);
			FlowPanel vkPanel = vaktekPanel.getPanel();
			panel.add(vkPanel);
			panel.setWidgetLeftWidth(vkPanel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
			panel.setWidgetTopHeight(vkPanel, 0, Style.Unit.PX, hoogte, Style.Unit.PX);

		}
		else if (voorZijAanzicht)
		{
			startKr = new KubusRooster(b, 1.5);
			vaktekPanel = new VaktekPanel(startKr, breedte, hoogte, 2, this);
			FlowPanel vkPanel = vaktekPanel.getPanel();
			panel.add(vkPanel);
			panel.setWidgetLeftWidth(vkPanel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
			panel.setWidgetTopHeight(vkPanel, 0, Style.Unit.PX, hoogte, Style.Unit.PX);

		}
		else if (bovenAanzicht)
		{
			startKr = new KubusRooster(b, 1.5);
			vaktekPanel = new VaktekPanel(startKr, breedte, hoogte, 4, this);
			FlowPanel vkPanel = vaktekPanel.getPanel();
			panel.add(vkPanel);
			panel.setWidgetLeftWidth(vkPanel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
			panel.setWidgetTopHeight(vkPanel, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		}
		else if (voorAanzicht)
		{
			startKr = new KubusRooster(b, 1.5);
			vaktekPanel = new VaktekPanel(startKr, breedte, hoogte, 5, this);
			FlowPanel vkPanel = vaktekPanel.getPanel();
			panel.add(vkPanel);
			panel.setWidgetLeftWidth(vkPanel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
			panel.setWidgetTopHeight(vkPanel, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		}
		else if (rechtsAanzicht)
		{
			startKr = new KubusRooster(b, 1.5);
			vaktekPanel = new VaktekPanel(startKr, breedte, hoogte, 6, this);
			FlowPanel vkPanel = vaktekPanel.getPanel();
			panel.add(vkPanel);
			panel.setWidgetLeftWidth(vkPanel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
			panel.setWidgetTopHeight(vkPanel, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		}
		
		else // het moet een blokkenbouwsel zijn
		{
			startKr = new KubusRooster(b, 1);
			
			int vWerkBreedte = breedte;
			int vWerkHoogte = hoogte;
			if (showKijkNaKnop() || keuzeBouwenSlopen || volLeegOptie || aantalBlokjes)
				vWerkHoogte = hoogte - 25;

			vWerk = new Viewer3d(new KubusRooster(b, 1), 351, -30, vWerkBreedte, vWerkHoogte, this);
			vWerk.zetAfstand(1000);
			vWerk.zetSchaduw(true);
			vWerk.zetBeginHoeken(30, -30);
			vWerk.zetMuisAan(true);

			if (launchState.containsKey("rotatieVast"))
				rotatieVast = ((Boolean) launchState.get("rotatieVast")).booleanValue();
			vWerk.zetMuisAan(!rotatieVast);

			if (launchState.containsKey("beginHoekX"))
				beginHoekX = ((Double) launchState.get("beginHoekX")).doubleValue();
			if (launchState.containsKey("beginHoekY"))
				beginHoekY = ((Double) launchState.get("beginHoekY")).doubleValue();
			vWerk.zetBeginHoeken(beginHoekX, beginHoekY);

			if (launchState.containsKey("nietBouwenSlopen"))
				nietBouwenSlopen = ((Boolean) launchState.get("nietBouwenSlopen")).booleanValue();
			vWerk.zetKlikAan(!nietBouwenSlopen);

			if (launchState.containsKey("perspectief"))
				perspectief = ((Boolean) launchState.get("perspectief")).booleanValue();
			if (!perspectief)
				vWerk.zetAfstand(1000000000);

			if (launchState.containsKey("pijlAan"))
				pijlAan = ((Boolean) launchState.get("pijlAan")).booleanValue();
			if (launchState.containsKey("balkAan"))
				balkAan = ((Boolean) launchState.get("balkAan")).booleanValue();
			vWerk.zetPijlAan(pijlAan);
			vWerk.zetBalkAan(balkAan);
			
			if (launchState.containsKey("maakAanzicht"))
			{	maakAanzicht = ((Boolean) launchState.get("maakAanzicht")).booleanValue();
				if (maakAanzicht)
					vWerk.zetMaakAanzicht(maakAanzicht);
			}	
			if (launchState.containsKey("bovenAanzichtMetHoogtes"))
			{	bovenAanzichtMetHoogtes = ((Boolean) launchState.get("bovenAanzichtMetHoogtes")).booleanValue();
				if (bovenAanzichtMetHoogtes)
				{	vWerk.zetGetalRooster(bovenAanzichtMetHoogtes);
					vWerk.zetHoogtes();
				}
			}
			if (launchState.containsKey("silhouet"))
			{	silhouet = ((Boolean) launchState.get("silhouet")).booleanValue();
				this.silhouet = silhouet;
				if (silhouet)
				{	vWerk.zetKlikAan(false);
					vWerk.zetSchaduw(false);
					startKr.zetVulkleur("zwart");
				}
			}

			
			int slopenX = 70;
			int gap1 = 10;
			int volleegW = 90;
			int aantalW  = 70;
			int kijknaW = showKijkNaKnop() ? 90 : 0;
			int currentX = 0;
			if (keuzeBouwenSlopen)
			{
				currentX += 2*gap1+2*slopenX;
			}
			if (volLeegOptie)
			{
				currentX += gap1+volleegW;
			}
			if (aantalBlokjes)
			{
				currentX += gap1+aantalW;
			}
			if (showKijkNaKnop())
			{
				currentX += gap1+kijknaW;
			}
			int hSpace = (breedte - currentX)/2;
			if (hSpace >= 0)
				currentX = hSpace;
			else
				currentX = 0;
			
			if (keuzeBouwenSlopen)
			{
				bouwenButton = new RadioButton(bouwenSlopenGroup, rb.bouwen());
				slopenButton = new RadioButton(bouwenSlopenGroup, rb.slopen());
				bouwenButton.addStyleName(nabouwenAanzichtenCss.radiobutton());
				slopenButton.addStyleName(nabouwenAanzichtenCss.radiobutton());
				panel.add(bouwenButton);
				panel.add(slopenButton);
				panel.setWidgetLeftWidth(bouwenButton, currentX+gap1, Style.Unit.PX, slopenX, Style.Unit.PX);
				panel.setWidgetTopHeight(bouwenButton, hoogte - 25, Style.Unit.PX, 25, Style.Unit.PX);
				currentX += gap1+slopenX;
				panel.setWidgetLeftWidth(slopenButton, currentX, Style.Unit.PX, slopenX+gap1, Style.Unit.PX);
				panel.setWidgetTopHeight(slopenButton, hoogte - 25, Style.Unit.PX, 25, Style.Unit.PX);
				bouwenButton.setValue(true);
				currentX += gap1+slopenX;
			}
			
			if (volLeegOptie)
			{
				panel.add(volButton);
				volButton.addStyleName(nabouwenAanzichtenCss.pushbutton());
				panel.add(leegButton);
				leegButton.addStyleName(nabouwenAanzichtenCss.pushbutton());
				panel.setWidgetLeftWidth(volButton, currentX+gap1, Style.Unit.PX, volleegW, Style.Unit.PX);
				panel.setWidgetTopHeight(volButton, hoogte - 25, Style.Unit.PX, 25, Style.Unit.PX);
				panel.setWidgetLeftWidth(leegButton, currentX+gap1, Style.Unit.PX, volleegW, Style.Unit.PX);
				panel.setWidgetTopHeight(leegButton, hoogte - 25, Style.Unit.PX, 25, Style.Unit.PX);
				panel.setWidgetVisible(leegButton, false);
				volButton.addClickHandler(new PushClickHandler());
				leegButton.addClickHandler(new PushClickHandler());
				
				currentX += gap1+volleegW;
			}
			
			if (aantalBlokjes)
			{
				int aantal = vWerk.kr.geefAantalK();
				blokjesLabel = new Label();
				Msgs msgs = GWT.create(Msgs.class);
				blokjesLabel.setText(msgs.blokjes(vWerk.kr.geefAantalK()));
				panel.add(blokjesLabel);
				panel.setWidgetLeftWidth(blokjesLabel, currentX+gap1, Style.Unit.PX, aantalW, Style.Unit.PX);
				panel.setWidgetTopHeight(blokjesLabel, hoogte - 20, Style.Unit.PX, 25, Style.Unit.PX);
				currentX += gap1+aantalW;
			}	
			
			vWerk.initContext2d();

			vWerk.draw();

			canvas = vWerk.getCanvas();

			panel.add(canvas);
			panel.setWidgetLeftWidth(canvas, 0, Style.Unit.PX, vWerkBreedte, Style.Unit.PX);
			panel.setWidgetTopHeight(canvas, 0, Style.Unit.PX, vWerkHoogte, Style.Unit.PX);

			if (isNakijkModus())
			{
				naChecker = new NabouwenAanzichtenChecker(launchState, randomVarNamen, randomVarWaarden);

				kijkNaButton.addStyleName(nabouwenAanzichtenCss.pushbutton());
				
				panel.add(kijkNaPanel);
				kijkNaPanel.setStylePrimaryName("kijknapanel");

				if (!checkExternal)
					kijkNaPanel.add(kijkNaButton);

				kijkNaButton.addClickHandler(new PushClickHandler());
			
				panel.setWidgetLeftWidth(kijkNaPanel, currentX+gap1, Style.Unit.PX, 90, Style.Unit.PX);
				panel.setWidgetTopHeight(kijkNaPanel, hoogte - 25, Style.Unit.PX, 25, Style.Unit.PX);
			
				if (!checkExternal)
				{
					kijkNaPanel.setWidgetLeftWidth(kijkNaButton, 0, Style.Unit.PX, 60, Style.Unit.PX);
					kijkNaPanel.setWidgetTopHeight(kijkNaButton, 0, Style.Unit.PX, 25, Style.Unit.PX);
				}
			}
		
			ingevuld = false;
		}
		
		panel.forceLayout();
		kijkNaPanel.forceLayout();
	}

	/**
	 * Retourneert true als de nakijk-knop getoond moet worden,
	 * anders false.
	 * @return true/false
	 */
	boolean showKijkNaKnop()
	{
		return kijkNaActief && !checkExternal;
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
			
			boolean[][][] state = null;
			int errorCount = this.errorCount;
			state = vWerk.kr.geefBooleanRooster();
			parameters.put("response", Arrays.deepToString(state)); // de blokjes als string, 3d representatie [[[true,true],[true,false]]]
			if (feedback != null && !feedback.isEmpty())
				parameters.put("feedback", feedback);
			setAttempt(parameters);
		}
	}
	
	public void setAttempt(String changeLog) {
		if (logOption) {
// Build parameters voor logging: zie FormuleEditorWithAnswer.buildLoggingMap
// fixed keys: response, verb:
			Map<String,Object> parameters = new HashMap<>();
			parameters.put("response", changeLog);
			parameters.put("verb", "http://www.dwo.nl/verbs/addRemoveCommand");
			setAttempt(parameters);
		}
	}
	
	
	public void kijkNa() 
	{
		// reset isVeranderdNaNakijken
		zetIsVeranderdNaNakijken(false);
		kijkNa(false);
	}
	
	public void kijkNa(boolean setState)
	{
		kijkNa(true, setState);
	}

	/**
	 * kijk na, geef feed back via CBook als gewenst 
	 * @param show moet feed back getoond worden?
	 * @param setState komen we terug bij een opgave die al eerder
	 * nagekeken was en dus nagekeken moet worden?
	 */
	public void kijkNa(boolean show, boolean setState)
	{
		if (setState)
			setChanged(false);

		if (vWerk == null || !ingevuld || !isNakijkModus())
		{	
			return;
		}

		KubusRooster useranswer = vWerk.kr;
		HashMap<String, Object> checkResults = naChecker.checkAnswer(useranswer);

		this.correct = (Boolean) checkResults.get("correct");
		this.score = (Integer) checkResults.get("score");
		if (mode == OpdrNavIF.OEFENEN_STRAFPUNTEN)
			score = Math.max(0, score - errorCount * foutStraf);
		this.feedback = (String) checkResults.get("feedback");
		this.goedHalfFout = (Integer) checkResults.get("goedHalfFout");
// Bepaal wanneer er vinkjes getoond worden:		
boolean showMark = mode == 1 || mode == 2 || lessonMode == LessonMode.review || lessonMode == LessonMode.browse;
		if (goedHalfFout == NabouwenAanzichtenChecker.DOOR || goedHalfFout == NabouwenAanzichtenChecker.HALF)
		{
			kijkNaPanel.setStyleName("fout", false);
			kijkNaPanel.setStyleName("half", showMark);
			kijkNaPanel.setStyleName("goed", false);
		}

		else if (goedHalfFout == NabouwenAanzichtenChecker.GOED)
		{
			kijkNaPanel.setStyleName("fout", false);
			kijkNaPanel.setStyleName("half", false);
			kijkNaPanel.setStyleName("goed", showMark);
		}
		else if (goedHalfFout == NabouwenAanzichtenChecker.FOUT)
		{
			verhoogErrorCount();
			kijkNaPanel.setStyleName("fout", showMark);
			kijkNaPanel.setStyleName("half", false);
			kijkNaPanel.setStyleName("goed", false);
		}
		
		nagekeken = true;
		ingevuld = true;

		if (show) // alleen als feedback moet worden getoond
		{
			comRoot.setChanged(isCorrect().booleanValue());
			
			if (correct) 
				fireEvent(EVENT_CORRECT);
			else if (errorCount > 1) 
				fireEvent(EVENT_FALSE2);
			else
				fireEvent(EVENT_FALSE);
			setAttempt(); // hier? Ja, maar ook bij zetVeranderd
		}
		
	}

	void zetIsVeranderdNaNakijken(boolean b)
	{
		this.isVeranderdNaNakijken = b;
	}
	

	public void zetVolledigeBreedte(int breedte)
	{
		if (this.breedte != breedte)
		{
			this.breedte = breedte;
		}
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
		if (ingevuld) 
			nagekeken = b;
	}

	public int[][] getScoreObjectives() 
	{
		return null; 
	}

	/**
	 * get de send commands for CBook
	 * @return array met Strings
	 */
	public String[] getSendCmds() 
	{
		String[] commands = {"blockBuilding", "text.buildingProgram"};
		return commands;
	}

	/**
	 * maak van een boolean kubusrooster met roosterafmeting krSize een
	 * array met krSize*krSize*krSize booleans 
	 * @param boolKR boolean kubusrooster met roosterafmeting krSize
	 * @param krSize rooster afmeting
	 * @return array met krSize*krSize*krSize booleans
	 */
	public boolean[] boolKRtoBoolArray(boolean[][][] boolKR, int krSize)
	{
		boolean[] boolArray = new boolean[krSize*krSize*krSize];
		
		for (int xCnt = 0; xCnt < krSize; xCnt++)
			for (int yCnt = 0; yCnt < krSize; yCnt++)
				for (int zCnt = 0; zCnt < krSize; zCnt++)
				{
					int index = xCnt+yCnt*krSize+zCnt*krSize*krSize;
					if (index < boolArray.length)
						boolArray[index] = boolKR[xCnt][yCnt][zCnt];
				}
		
		return boolArray;
	}

	/**
	 * maak van een array met krSize*krSize*krSize booleans, een boolean
	 * kubusrooster met roosterafmeting krSize 
	 * @param boolArray array met krSize*krSize*krSize booleans
	 * @param krSize rooster afmeting
	 * @return een boolean kubusrooster met roosterafmeting krSize
	 */
	public boolean[][][] boolArraytoBoolKR(boolean[] boolArray, int krSize)
	{
		boolean[][][] boolKR = new boolean[krSize][krSize][krSize];
		
		for (int i = 0; i < boolArray.length; i++)
		{
			int x = i % krSize;
			int z = i / (krSize * krSize);
			int y = (i - z * krSize * krSize) / krSize;
			if ((x < krSize) && (y < krSize) && (z < krSize))
				boolKR[x][y][z] = boolArray[i];
		}	
		
		return boolKR;
	}

	/**
	 * accepteer een CBook Event; dit CBook Event komt
	 * in twee typen:<br>
	 * 1) het Event bevat een array met booleans dat omgezet
	 * kan worden in een kubusbouwsel<br>
	 * 2) het Event bevat een bouwprogramma (multiline String
	 * met bouwopdrachten) dat door een instantie van
	 * de Intepreter (zie die klasse) omgezet wordt
	 * in een kubusbouwsel
	 */
	public void acceptCBookEvent(CBookEvent event) 
	{
		String command = event.getCommand();
		
		if (command.startsWith("blockBuilding"))
		{
			Map map = (Map) event.getParameters();
			
			ObjectMap oMap = JSONUtilities.wrapMap(map);	

			if (oMap != null)
			{	
				boolean[] booleanArray = oMap.getBooleanArray("booleanArr");
				int krSize = oMap.getInt("krSize");
				boolean[][][] booleanKR = boolArraytoBoolKR(booleanArray, krSize);
				startKr = new KubusRooster(booleanKR, 1);
				if (vWerk != null)
				{
					vWerk.zetKubusRooster(startKr);
					vWerk.tekenOpnieuw(); //.draw();
				}
				if (vaktekPanel != null)
				{
					vaktekPanel.zetKubusRooster(startKr);
					vaktekPanel.tekenOpnieuw();
				}
				Msgs msgs = GWT.create(Msgs.class);
				if (blokjesLabel != null && vWerk != null)
					blokjesLabel.setText(msgs.blokjes(vWerk.kr.geefAantalK()));
			}
			
		}
		else if (command.startsWith("text.buildingProgram"))
		{
			Map map = (Map)event.getParameters();
			
			if (map!=null)
			{
				String programText = (String) map.get("content");
				vWerk.kr.maakLeeg();
				Interpreter interpreter = new Interpreter(vWerk.kr);
				interpreter.execute(programText);
				vWerk.tekenOpnieuw();
				Msgs msgs = GWT.create(Msgs.class);
				if (blokjesLabel != null)
					blokjesLabel.setText(msgs.blokjes(vWerk.kr.geefAantalK()));
			}
		}
	}
}
