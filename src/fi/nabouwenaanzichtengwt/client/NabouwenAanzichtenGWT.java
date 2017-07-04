package fi.nabouwenaanzichtengwt.client;

import java.awt.Cursor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
//import nl.uu.fi.dwo.interaction.client.event.CBookEventHandler;


import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

//import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
//import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;
//import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;



import fi.nabouwenaanzichtengwt.client.text.Msgs;
import fi.nabouwenaanzichtengwt.client.text.Text;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NabouwenAanzichtenGWT implements EntryPoint, InteractionStub, InteractionView, CBookEventListener
{
	public static final String TEXT_CSV = "text.csv";
	
    // logger
    static Logger logger = Logger.getLogger("NabouwenaanzichtenGWT");
	static final Text rb = GWT.create(Text.class);

	static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	Canvas canvas;
	int mouseX, mouseY;
	OpdrNavIF comRoot;
	int mode;

	static final int refreshRate = 25;

	final CssColor redrawColor = CssColor.make("rgba(255,255,255,0.6)");

	private int breedte = 600;
	private int hoogte = 250;
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	Map<String, ?> randomVarWaarden = null;

	LayoutPanel panel = new LayoutPanel();
	
	//TouchPanel touchPanel = new TouchPanel();
	
	//TouchButton nakijkKnop = new TouchButton();
	
	String bouwenSlopenGroup = "bouwenSlopenGroup";
	RadioButton bouwenButton;
	RadioButton slopenButton;

	PushButton volButton = new PushButton(rb.maakVol());
	PushButton leegButton = new PushButton(rb.maakLeeg());
	
	Label blokjesLabel;
	
	private boolean checkExternal = false;

	PushButton kijkNaButton = new PushButton(rb.kijkNa()); 
	LayoutPanel kijkNaPanel = new LayoutPanel();
	
	private boolean isVeranderdNaNakijken = false;
	
	private Viewer3d vWerk = null;
	private VaktekPanel vaktekPanel = null;
	private NabouwenAanzichtenChecker naChecker;

	private int goedHalfFout;
	private int score = 0;
	Boolean correct = null;
	private String feedback = "";

	boolean silhouet = false;
	
	boolean nagekeken;
	boolean ingevuld;

	boolean kijkNaActief = false;

	private KubusRooster startKr;
	int maxAantal = 4;
	boolean[][][] b;

	NabouwenAanzichtenGWTCssResource nabouwenAanzichtenCss;
	
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

	public void makeResources()
	{
		nabouwenAanzichtenCss = clientBundle.getNabouwenAanzichtenGWTCSS();
		nabouwenAanzichtenCss.ensureInjected();
	}

	public void onModuleLoad()
	{
		makeResources();
		//initOnLoad();
		RootPanel.get().add(panel);
		
		Stub.publish(this);
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
	}

	// 2 aanzichten
	private void initOnLoad()
	{
		hoogte = 200;
		breedte = 200;
		int maxAantal = 4;
		boolean[][][] b = new boolean[maxAantal][maxAantal][maxAantal];
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					if (k == 0 && j == 0 && i == 0)
						;
					b[i][j][k] = Math.random()>0.8;
				}
			}
		}
		startKr = new KubusRooster(b, 1.5);
		vaktekPanel = new VaktekPanel(startKr, breedte, hoogte, 2, this);
		panel.add(vaktekPanel.getPanel());

	}
	
	// 3 aanzichten
	private void initOnLoad_1()
	{
		hoogte = 200;
		breedte = 200;
		int maxAantal = 4;
		boolean[][][] b = new boolean[maxAantal][maxAantal][maxAantal];
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					if (k == 0 && j == 0 && i == 0)
						;
					b[i][j][k] = Math.random()>0.8;
				}
			}
		}
		startKr = new KubusRooster(b, 1.5);
		vaktekPanel = new VaktekPanel(startKr, breedte, hoogte, 3, this);
		panel.add(vaktekPanel.getPanel());

	}

	private void initOnLoad_0() {
		int maxAantal = 6;
		boolean[][][] b = new boolean[maxAantal][maxAantal][maxAantal];
		for (int i = 0; i < maxAantal; i++)
		{
			for (int j = 0; j < maxAantal; j++)
			{
				for (int k = 0; k < maxAantal; k++)
				{
					if (k == 0 && j == 0 && i == 0)
						;
					b[i][j][k] = false;
				}
			}
		}
		Viewer3d vWerk = new Viewer3d(new KubusRooster(b, 1), 351, -30, 450, 450, this);
		//vWerk.zetAchtergrond(bgcolor);
		vWerk.zetAfstand(1000);
		vWerk.zetSchaduw(true);
		vWerk.zetBeginHoeken(30, -30);
		vWerk.zetMuisAan(true);
		//vWerk.zetGetalRooster(true);

		canvas = vWerk.getCanvas();
		vWerk.initContext2d();

		vWerk.draw();

		//touchPanel.getElement().getStyle().setWidth(breedte, Unit.PX);
		//touchPanel.getElement().getStyle().setHeight(hoogte - (kijkNaActief ? 32 : 0), Unit.PX);
		//touchPanel.add(canvas);
		
		//panel.add(touchPanel);
		panel.add(canvas);
		
		//MuisBeheerder mb = new MuisBeheerder(vWerk);

		//touchPanel.addTouchHandler(mb);
	}

/*	
	private void addCheckButtonHandler(final TouchButton tb)
	{
		tb.addTouchStartHandler(new TouchStartHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				check();
				if (vWerk == null || !ingevuld || !kijkNaActief)
					comRoot.setChanged(goedHalfFout == NabouwenAanzichtenChecker.FOUT);
			}

		});
	}
*/
	
    class PushClickHandler implements ClickHandler
    {
    	//public void onMouseDown(MouseDownEvent e)
    	public void onClick(ClickEvent e)
    	{
    		
    		//if (touchStart)
    		//	return;
    		
			//e.preventDefault();
			e.stopPropagation();
    		
    		if (e.getSource() == kijkNaButton)
    		{
//System.out.println("click kijkNaB");    			
    			check();
				boolean b = vWerk == null || !ingevuld || !kijkNaActief;
				if (!b)
					comRoot.setChanged(goedHalfFout == NabouwenAanzichtenChecker.FOUT);
    		}
    		else if (e.getSource() == volButton)
    		{
    			panel.setWidgetVisible(volButton, false);
    			panel.setWidgetVisible(leegButton, true);
    			buildHistory = "";
    			//startKr.maakVol();
    			vWerk.kr.maakVol();
    			vWerk.tekenOpnieuw();
    			zetVeranderd(false);
    			
    			if (nagekeken)
    				zetIsVeranderdNaNakijken(true);
    		}
    		else if (e.getSource() == leegButton)
    		{
    			panel.setWidgetVisible(volButton, true);
    			panel.setWidgetVisible(leegButton, false);
    			//startKr.maakLeeg();
    			vWerk.kr.maakLeeg();
    			buildHistory = "";
    			vWerk.tekenOpnieuw();
    			zetVeranderd(false);
    			
    			if (nagekeken)
    				zetIsVeranderdNaNakijken(true);
    		}
    		
    		
    	}
    	
    }
    
	/**
	 * Retourneert true als nabouwen aanzichten in de nakijk-modus staat en
	 * moet nakijken. 
	 * 
	 * @return
	 */
	private boolean isNakijkModus()
	{
		return kijkNaActief || checkExternal;
	}
		
	private void check()
	{
		// reset isVeranderdNaNakijken
		zetIsVeranderdNaNakijken(false);

		if (vWerk == null || !ingevuld || !isNakijkModus())
		{	
//			System.out.println("ingevuld " + ingevuld);
//			System.out.println("kijkNaActief " + kijkNaActief);

			return;
		}
		
		//System.out.println("check");

		KubusRooster useranswer = vWerk.kr;
		HashMap<String, Object> checkResults = naChecker.checkAnswer(useranswer);

		this.correct = (Boolean) checkResults.get("correct");
		this.score = (Integer) checkResults.get("score");
		this.feedback = (String) checkResults.get("feedback");
		this.goedHalfFout = (Integer) checkResults.get("goedHalfFout");

		//System.out.println("userAnswer: "+useranswer);
		//System.out.println("correct: "+ correct);
		//System.out.println("score: "+score);
		//System.out.println("goedHalfFout: " + goedHalfFout);
		//System.out.println(" feedback: "+ feedback);

		if (goedHalfFout == NabouwenAanzichtenChecker.DOOR || goedHalfFout == NabouwenAanzichtenChecker.HALF)
		{
			kijkNaPanel.setStyleName(nabouwenAanzichtenCss.fout(), false);
			kijkNaPanel.setStyleName(nabouwenAanzichtenCss.half(), true);
			kijkNaPanel.setStyleName(nabouwenAanzichtenCss.goed(), false);
		}

		else if (goedHalfFout == NabouwenAanzichtenChecker.GOED)
		{
			kijkNaPanel.setStyleName(nabouwenAanzichtenCss.fout(), false);
			kijkNaPanel.setStyleName(nabouwenAanzichtenCss.half(), false);
			kijkNaPanel.setStyleName(nabouwenAanzichtenCss.goed(), true);
		}
		else if (goedHalfFout == NabouwenAanzichtenChecker.FOUT)
		{
			kijkNaPanel.setStyleName(nabouwenAanzichtenCss.fout(), true);
			kijkNaPanel.setStyleName(nabouwenAanzichtenCss.half(), false);
			kijkNaPanel.setStyleName(nabouwenAanzichtenCss.goed(), false);
		}
		
		nagekeken = true;
		ingevuld = true;

		comRoot.setChanged(isCorrect().booleanValue());
	}

	public Panel getAsPanel()
	{
		return panel;
	}

	/**
	 * Wat doet dit?
	 * 
	 * @param state
	 */
	void zetVeranderd(boolean state)
	{
		//if (vWerk == null || !kijkNaActief)
		//	return;
		//System.out.println("zetVeranderd");

		//nakijkKnop.clear();
		//nakijkKnop.add(vinkjeGrijsImage);
		if ((blokjesLabel != null) && (vWerk != null))
		{
			Msgs msgs = GWT.create(Msgs.class);
			
			int aantal = vWerk.kr.geefAantalK();
			blokjesLabel.setText(msgs.blokjes(aantal));

			//System.out.println("aantal " + aantal);			
		}

		if (vWerk != null)
		{	
			boolean[][][] booleanKR = vWerk.kr.geefBooleanRooster();
			int krSize = vWerk.kr.maxAantal;
			boolean[] booleanArray = boolKRtoBoolArray(booleanKR, krSize);
			
			Map<String,Object> map = new HashMap<String,Object>();
						
			map.put("krSize", krSize);
			map.put("booleanArr", booleanArray);
			
 			comRoot.fireEvent(new CBookEvent(this,"blockBuilding",map));
 			//System.out.println("fire blockBuilding");
			
			String lastBuildCommand = vWerk.getLastBuildCommand();
			//System.out.println("lastBuildCommand " + lastBuildCommand);			
			if(!"".equals(lastBuildCommand))
				buildHistory = buildHistory + lastBuildCommand + "\n";
			Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("content", buildHistory);
			comRoot.fireEvent(new CBookEvent(this,"text.buildingProgram",map1));
			//System.out.println("fire text.buildingProgram " + buildHistory);			
		}

		if (vWerk == null || !isNakijkModus())
			return;
		
		ingevuld = true;
		
		if (state) return;
		
		clearFeedbackImages();
		
		correct = false;
		score = 0;
		if (!startKr.isGelijk(vWerk.kr))
			ingevuld = true;
		else
			ingevuld = false;
		comRoot.setChanged(false);

/*		
		if (vWerk != null)
		{	
			boolean[][][]  booleanKR = vWerk.kr.geefBooleanRooster();
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("booleanKR", booleanKR);
			comRoot.fireEvent(new CBookEvent(this,"blockBuilding",map));
			
			String lastBuildCommand = vWerk.getLastBuildCommand();
			if(!"".equals(lastBuildCommand))
				buildHistory = buildHistory + lastBuildCommand + "\n";
			Map<String,Object> map1 = new HashMap<String,Object>();
			map1.put("content", buildHistory);
			comRoot.fireEvent(new CBookEvent(this,"text.buildingProgram",map1));
		}
*/
	}

	/**
	 * Haal vinkje/kruis weg.
	 */
	private void clearFeedbackImages()
	{
		kijkNaPanel.setStyleName(nabouwenAanzichtenCss.fout(), false);
		kijkNaPanel.setStyleName(nabouwenAanzichtenCss.half(), false);
		kijkNaPanel.setStyleName(nabouwenAanzichtenCss.goed(), false);
	}

	boolean isBouwen()
	{
		if (bouwenButton != null)
			return bouwenButton.getValue();
		else
			return true;
	}

	@Override
	public HashMap<String, Object> getState()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		if (vWerk == null)
			return h;

		// check(); FIXME waarom staat die hier? Leidt tot oneindige recursie! 
		boolean[][][] stateNew = null;
		stateNew = vWerk.kr.geefBooleanRooster();

		h.put("silhouet", silhouet);
		h.put("stateNew", stateNew);
		h.put("nagekeken", nagekeken);
		h.put("ingevuld", ingevuld);
		h.put("isVeranderdNaNakijken", new Boolean(isVeranderdNaNakijken));

		return h;
	}

	
	@Override
	public void setState(HashMap<String,Object> h) {
		try {
			setState_(h);
		} catch(Exception e) {
			logger.log(Level.SEVERE, "setState" , e);
		}
	}
	private void setState_(HashMap<String, Object> h)
	{
		if (vWerk == null || h == null)
			return;

		Object stateNew = null;

		if (h.containsKey("stateNew"))
			stateNew = h.get("stateNew");
		if (stateNew != null)
		{
			if (stateNew instanceof boolean[][][])
			{	vWerk.zetKubusRooster(new KubusRooster((boolean[][][]) stateNew, 1));
			}
			else if (stateNew instanceof Object[])
			{	vWerk.zetKubusRooster(new KubusRooster(KubusRooster.toBooleanArray((Object[]) stateNew), 1));
			}
			vWerk.draw();
			boolean silhouet = false;
			if (launchState.containsKey("silhouet"))
			{	silhouet = ((Boolean) launchState.get("silhouet")).booleanValue();
//System.out.println("contains silhouet " + silhouet);			
			}
			this.silhouet = silhouet;
			if (silhouet)
			{	
//System.out.println("if silhouet");				
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


		if (nakijkenNodig())
			check();
		
		zetVeranderd(nagekeken);
	}

	/**
	 * Retourneert true als er nagekeken moet worden (en vinkje/kruis getoond).
	 * Retourneert false als er niet nagekeken moet worden.
	 * 
	 * @return
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

	@Override
	public int getScore()
	{
		return score;
	}

	@Override
	public Boolean isCorrect()
	{
		if (!isNakijkModus())
			return Boolean.TRUE;
		if (!nagekeken) 
			return null;
		
		return correct;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());
		CssColor background = comRoot.getBackground();
		panel.getElement().getStyle().setBackgroundColor(background.value());
		if (vWerk != null)
		{	vWerk.zetAchtergrond(background);
			vWerk.tekenOpnieuw();
		}	
System.out.println("setComRoot");		
		comRoot.addCBookEventListener("text.buildingProgram", this);
		comRoot.addCBookEventListener("blockBuilding", this);
	}
	
	public void zetMode(int mode)
	{
		this.mode = mode;
		if (isNakijkModus())
		{
			kijkNaActief = (mode == 0 || mode == 1);
		}
	}


	@Override
	public Widget asWidget()
	{
		return getAsPanel();
	}

	@Override
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) 
	{
		breedte = width;
		hoogte  = height;

		logger.info("NabouwenAanzichtenGWT init");
		//System.out.println("breedte = " + breedte);
		//System.out.println("hoogte = " + hoogte);
		
		launchState = launchData;
		ObjectMap launchMap = JSONUtilities.wrapMap(launchData);
		randomVarWaarden = values;
		randomVarNamen   = values.keySet().toArray(new String[values.size()]);
		
		panel.setSize(breedte + "px", hoogte + "px");
		//panel.getElement().getStyle().setWidth(breedte, Unit.PX);
		//panel.getElement().getStyle().setHeight(hoogte, Unit.PX);
		//panel.getElement().getStyle().setProperty("textAlign", "right");

		//int maxAantal = 4;
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
	    
	    boolean checkBlokkenBouwsel = true;
	    boolean checkDrieAanzichten = false;
	    boolean checkVoorZijAanzicht = false;
	    boolean checkBovenVoorAanzicht = false;
	    boolean checkBovenZijAanzicht = false;
	    boolean checkBovenAanzicht = false;
	    boolean checkVoorAanzicht = false;
	    boolean checkRechtsAanzicht = false;
	    
	    boolean checkAantalKubus = false;

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

		//System.out.println("init kijkNaActief = " + kijkNaActief);
		
		this.kijkNaActief = kijkNaActief; 

		//boolean drieAanzichten = false;
		if (launchState.containsKey("drieAanzichten"))
			drieAanzichten = ((Boolean) launchState.get("drieAanzichten")).booleanValue();
		//boolean voorZijAanzicht = false;
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

			vWerk = new Viewer3d(startKr, 351, -30, vWerkBreedte, vWerkHoogte, this);
			vWerk.zetAfstand(1000);
			vWerk.zetSchaduw(true);
			vWerk.zetBeginHoeken(30, -30);
			vWerk.zetMuisAan(true);

			//boolean rotatieVast = false;
			if (launchState.containsKey("rotatieVast"))
				rotatieVast = ((Boolean) launchState.get("rotatieVast")).booleanValue();
			vWerk.zetMuisAan(!rotatieVast);

			//double beginHoekX = 30;
			//double beginHoekY = -30;
			if (launchState.containsKey("beginHoekX"))
				beginHoekX = ((Double) launchState.get("beginHoekX")).doubleValue();
			if (launchState.containsKey("beginHoekY"))
				beginHoekY = ((Double) launchState.get("beginHoekY")).doubleValue();
			vWerk.zetBeginHoeken(beginHoekX, beginHoekY);

			//boolean nietBouwenSlopen = false;
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
					//vWerk.zetAfstand(1000000000);
					//vWerk.zetSchaduw(false);
					//vWerk.zetMuisAan(false);
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

			
// dit moet je dus niet zo doen. Wim			
	// layout button panel:
	// [BOUWEN][SLOPEN] gap [vol/leeg] gap [aantal] gap [kijkna]
	//  70       70      10    90      30    70      ?    90     |
			int slopenX = 70;
			int gap1 = 10;
//			int span = 0;
//			if(!keuzeBouwenSlopen) 
//			{	slopenX = 0;
//				gap1 = 150;
//			} 
//			else
//				span = slopenX * 2;;
//			int volleegX = 150;
			int volleegW = 90;
//			int gap2 = 30;
//			if (!volLeegOptie) 
//			{
//				volleegW = 0;
//				gap2 = 120;
//			} 
//			else 
//				span = volleegX + volleegW;
//			int aantalX  = 270;
			int aantalW  = 70;
//			if(!aantalBlokjes) 
//			{
//				aantalW = 0;
//			} else
//				span = aantalX + aantalW;
//			int kijknaW = kijkNaActief ? 90 : 0;
			int kijknaW = showKijkNaKnop() ? 90 : 0;
//			if (breedte - kijknaW < span) 
//			{
	// we have a problem, Huub!			
//				int space = span - (breedte-kijknaW);
//				if (space <= gap1 + gap2) 
//				{					
					// reduce gap, done
//					int p1 = space * gap1 / (gap1 + gap2);
//					gap1 -= p1;
//					gap2 -= (space-p1);
//					space = 0;
//				} else 
//				{
					// take gaps, 
//					space -= gap1 + gap2;
//					gap1 = gap2 = 0;
//				}
//				if ( space > 0) 
//				{
					// take space from buttons/labels
//				}
// recalculate positions.				
//				volleegX = slopenX * 2 + gap1;
//				aantalX  = volleegX + volleegW + gap2;
//			}
			
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
			//System.out.println("hSpace " + hSpace);			
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

			//touchPanel.getElement().getStyle().setWidth(breedte, Unit.PX);
			//touchPanel.getElement().getStyle().setHeight(hoogte - (kijkNaActief ? 32 : 0), Unit.PX);
			//touchPanel.add(canvas);
			//panel.add(touchPanel);
			
			panel.add(canvas);
			panel.setWidgetLeftWidth(canvas, 0, Style.Unit.PX, vWerkBreedte, Style.Unit.PX);
			panel.setWidgetTopHeight(canvas, 0, Style.Unit.PX, vWerkHoogte, Style.Unit.PX);

			if (isNakijkModus())
			{
				naChecker = new NabouwenAanzichtenChecker(launchState, randomVarNamen, randomVarWaarden);

				kijkNaButton.addStyleName(nabouwenAanzichtenCss.pushbutton());
				
				panel.add(kijkNaPanel);
				kijkNaPanel.setStylePrimaryName(nabouwenAanzichtenCss.kijknapanel());

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
	}

	/**
	 * Retourneert true als de nakijk-knop getoond moet worden,
	 * anders false.
	 * 
	 * @return
	 */
	boolean showKijkNaKnop()
	{
		return kijkNaActief && !checkExternal;
	}
	
	@Override
	public void kijkNa() 
	{
		check();
	}
	
	void zetIsVeranderdNaNakijken(boolean b)
	{
		this.isVeranderdNaNakijken = b;
	}
	

	@Override
	public void zetVolledigeBreedte(int breedte) {
		if(this.breedte != breedte) {
			this.breedte = breedte;
			// relayout!
		}
		
	}

	@Override
	public int getAsHoogte() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		return hoogte;
	}

	@Override
	public int getWidth() {
		return breedte;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		// TODO Auto-generated method stub
		
	}
	
	//@Override
	public void zetNagekeken(boolean b) {
		if (ingevuld) 
			nagekeken = b;
	}

	
	//@Override
	public int[][] getScoreObjectives() 
	{
		return null; 
	}

	public String[] getSendCmds() 
	{
		
//System.out.println("getSendCmds");

		String[] commands = {"blockBuilding", "text.buildingProgram"};
		return commands;
	}

// restore method
    
	public String[] getAcceptedCmds() 
	{
//System.out.println("getAcceptedCmds");		
		String[] commands = {"blockBuilding", "text.buildingProgram"};
		return commands;
	}

	public String getLocalizedCmd(String cmd) 
	{
		
//System.out.println("getLocalizedCmd");

		String localizedCmd = null; //NabouwenAanzichten.rb.getString(CBA_PREFIX + cmd);
		if (localizedCmd == null)
			return cmd;
		return localizedCmd;
	}

	
	public boolean[] boolKRtoBoolArray(boolean[][][] boolKR, int krSize)
	{
		boolean[] boolArray = new boolean[krSize*krSize*krSize];
		
		for (int xCnt = 0; xCnt < krSize; xCnt++)
			for (int yCnt = 0; yCnt < krSize; yCnt++)
				for (int zCnt = 0; zCnt < krSize; zCnt++)
				{	int index = xCnt+yCnt*krSize+zCnt*krSize*krSize;
					if (index < boolArray.length)
						boolArray[index] = boolKR[xCnt][yCnt][zCnt];
					else
System.out.println("KRtoAR IOBE");						
				}	
		
		return boolArray;
	}

	public boolean[][][] boolArraytoBoolKR(boolean[] boolArray, int krSize)
	{
		boolean[][][] boolKR = new boolean[krSize][krSize][krSize];
		
		for (int i = 0; i < boolArray.length; i++)
		{	int x = i % krSize;
			int z = i / (krSize * krSize);
			int y = (i - z * krSize * krSize) / krSize;
			if ((x < krSize) && (y < krSize) && (z < krSize))
				boolKR[x][y][z] = boolArray[i];
			else
System.out.println("ArtoKR IOBE " + x + "," + y + "," + z);				
		}	
		
		return boolKR;
	}

	@Override
	public void acceptCBookEvent(CBookEvent event) 
	{
		String command = event.getCommand();
		
		
		if(command.startsWith("blockBuilding"))
		{
System.out.println("acceptCBookEvent");
System.out.println("command = " + command);

			Map map = (Map) event.getParameters();
			
ObjectMap oMap = JSONUtilities.wrapMap(map);	

			if (oMap != null)
			{	//boolean[][][] booleanKR = (boolean[][][]) oMap.get("booleanKR");
				boolean[] booleanArray = oMap.getBooleanArray("booleanArr");
				int krSize = oMap.getInt("krSize");
				
				boolean[][][] booleanKR = boolArraytoBoolKR(booleanArray, krSize);
				startKr = new KubusRooster(booleanKR, 1);
				if (vWerk != null)
				{	vWerk.zetKubusRooster(startKr);
					vWerk.tekenOpnieuw(); //.draw();
				}
				if (vaktekPanel != null)
				{	vaktekPanel.zetKubusRooster(startKr);
					vaktekPanel.tekenOpnieuw();
				}
				Msgs msgs = GWT.create(Msgs.class);
				if (blokjesLabel != null && vWerk != null)
					blokjesLabel.setText(msgs.blokjes(vWerk.kr.geefAantalK()));
			}
			
		}
		
		if(command.startsWith("text.buildingProgram"))
		{
			
System.out.println("acceptCBookEvent");
System.out.println("command = " + command);
			
			Map map = (Map)event.getParameters();
			if(map!=null)
			{
//System.out.println("map != null");				
				String programText = (String) map.get("content");
//System.out.println("" + programText);				
				//setCursor(new Cursor(Cursor.WAIT_CURSOR));
				vWerk.kr.maakLeeg();
				Interpreter interpreter = new Interpreter(vWerk.kr);
				interpreter.execute(programText);
				//setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				//zetVeranderd();
//System.out.println("" + vWerk.kr.geefAantalK());
				vWerk.tekenOpnieuw();
				Msgs msgs = GWT.create(Msgs.class);
				if (blokjesLabel != null)
					blokjesLabel.setText(msgs.blokjes(vWerk.kr.geefAantalK()));
			}
		}
	}

}
