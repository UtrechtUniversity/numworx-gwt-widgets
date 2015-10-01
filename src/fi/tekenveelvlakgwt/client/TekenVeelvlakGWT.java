package fi.tekenveelvlakgwt.client;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
//import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;

import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import com.google.gwt.canvas.client.Canvas;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.resources.client.ImageResource;


public class TekenVeelvlakGWT implements EntryPoint, InteractionStub, InteractionView 
{
    static final String holderId = "dockholder";

	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	// UI
	DockLayoutPanel dlp;
	LayoutPanel bottomPanel;
	
	
	int breedte = 500;
	int hoogte = 450;
	int bottomHeight = 32;
	int leftOffset = 5;
	int topOffset = 5;
	int toggleSize = 22;
	int pushSize = 24;
	int buttonWidth = 40;
	int buttonHeight = 22;

	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	// images/css
	TekenVeelvlakGWTClientBundle tekenVeelvlakGWTClientBundle;
	static TekenVeelvlakGWTCssResource tekenVeelvlakGWTCssResource;
	ImageResource foutKruisResource, goedKrulResource;
	Image foutKruisImage, goedKrulImage;

	
	private int mode;
	private OpdrNavIF comRoot;
	
	// instelbaarheid
	boolean viewerOnly = false;
	boolean profilesOnly = false;
	boolean vlakkenKleurenOptie;	
	TekenVeelvlak tvv;
	Viewer3d v3d;
	VaktekPanel vaktek;
	
	boolean kijkVlakkenNa = false;
	boolean kijkDraaihoekNa = false;
	boolean kijkNaActief = false;
	Boolean correct = null;
	boolean nagekeken = false;
	boolean ingevuld = false;
	
	String[] docentKleuren = null;
	double docentDraaihoekX = 1e5d;
	double docentDraaihoekY = 1e5d;
	int score;
	int scoreMax = 10;
	
    static int MOVEABLE = 0;
    static int FRONTVIEW = 1;
    static int BACKVIEW = 2;
    static int TOPVIEW = 3;
    static int BOTTOMVIEW = 4;
    static int LEFTVIEW = 5;
    static int RIGHTVIEW = 6;
    static int TEACHER = 7;


	public void getImages() 
	{
		tekenVeelvlakGWTClientBundle = GWT.create(TekenVeelvlakGWTClientBundle.class);
		tekenVeelvlakGWTCssResource = tekenVeelvlakGWTClientBundle.getTekenVeelvlakGWTCssResource();
		tekenVeelvlakGWTCssResource.ensureInjected();
		
		foutKruisResource = tekenVeelvlakGWTClientBundle.foutKruisResource();
		goedKrulResource = tekenVeelvlakGWTClientBundle.goedKrulResource();
		foutKruisImage = new Image(foutKruisResource);
		goedKrulImage = new Image(goedKrulResource);

		
	} // getImages	
		
	// stand-alone versie
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
		
		
// constructie en initialisatie uit elkaar trekken.
// constructie
		
		getImages(); 
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(tekenVeelvlakGWTCssResource.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		//RootPanel.get().add(dlp);
		//RootPanel.get().addStyleName(kladjeCss.root());
		
		init(breedte, hoogte, launchState, randomVarWaarden);

	}
	
	public Widget asWidget()
	{
		return dlp; 
	}
	
	@Override
	public HashMap<String, Object> getState()
	{
// hier onderscheid maken tussen TVV, Viewer, Profiles 		
System.out.println("tvGWT getState");

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

	@Override
	public void setState(HashMap<String, Object> map)
	{
// hier onderscheid maken tussen TVV, Viewer, Profiles?		
System.out.println("tvGWT setState");    	
		
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
		
    	ArrayList<String> leerlingKleurenAL = null; 
    	if (h.containsKey("leerlingKleuren"))
    		leerlingKleurenAL = (ArrayList<String>) h.get("leerlingKleuren");
    	
    	if (leerlingKleurenAL != null)
    	{	
    		String[] leerlingKleuren = new String[leerlingKleurenAL.size()];
    		for (int lk = 0; lk < leerlingKleurenAL.size(); lk++)
    			leerlingKleuren[lk] = leerlingKleurenAL.get(lk);

    		if (vlakkenKleurenOptie && viewerOnly)
    		{	v3d.setKleuren(leerlingKleuren);
    		}
    		if (vlakkenKleurenOptie && profilesOnly)	
    		{	vaktek.setVaktekKleuren(leerlingKleuren);
    		}
    	}	
    	
    	ingevuld = false;
    	
    	if (h.containsKey("nagekeken"))
    		nagekeken = h.getBoolean("nagekeken");
    	if (h.containsKey("ingevuld"))
    		ingevuld = h.getBoolean("ingevuld");
    	
    	if (ingevuld && (nagekeken || mode == 0))
    		kijkNa();
    	
/*    	
    	if (kijkNaActief && nagekeken)
    	{
    		if (correct && viewerOnly)
    		{	//viewer.vinkjeLabel.setVisible(true);
    			v3d.kijkNaPanel.setWidgetVisible(goedKrulImage,true);
    			//vaktek.vinkjeLabel.setVisible(true);
    		}
    		else if (!correct && viewerOnly)
    		{
    			v3d.kijkNaPanel.setWidgetVisible(foutKruisImage,true);
    		}
    		else if (correct && profilesOnly)
    		{
    			//viewer.kruisjeLabel.setVisible(true);
    			//v3d.kijkNaPanel.setWidgetVisible(tvGWT.goedKrulImage,false);
    			//vaktek.kruisjeLabel.setVisible(true);
    			vaktek.kijkNaPanel.setWidgetVisible(vaktek.kijkNaLabelGoed,true);
    		}
    		else if (!correct && profilesOnly)
    		{
    			vaktek.kijkNaPanel.setWidgetVisible(vaktek.kijkNaLabelFout,true);
    		}
    	}
		
*/
	}

	@Override
	public int getScore()
	{
		return 0;
	}

	@Override
	public Boolean isCorrect()
	{
		if (kijkNaActief)
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
	
	public void zetMode(int mode)
	{	this.mode = mode;
		if (kijkNaActief)    
			kijkNaActief = (mode == 0 || mode == 1);
	}
	
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

	public void init(int width, int height, Map<String, Object> map, //launchState,
			Map<String, Number> values) 
	{
		
		this.breedte = width;
		this.hoogte = height;
		//this.launchState = launchState;
		ObjectMap launchState = JSONUtilities.wrapMap(map);
		
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

        int viewerPosition = 0;
        int basisFiguur = 1;
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
           
        boolean vlakkenKleurenOptie = false;
        if (launchState.containsKey("vlakkenKleurenOptie"))
          	 vlakkenKleurenOptie = launchState.getBoolean("vlakkenKleurenOptie");

		this.vlakkenKleurenOptie = vlakkenKleurenOptie;
		
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

        List<String> docentKleurenAL = new ArrayList<String>();
//        String[] docentKleuren = null;
        if (launchState.containsKey("docentKleuren"))
        { 	 docentKleurenAL = launchState.getStringList("docentKleuren");
System.out.println("contains docentKleuren");        
        }
    	if (docentKleurenAL != null)
    	{	docentKleuren = new String[docentKleurenAL.size()];
    		for (int dk = 0; dk < docentKleurenAL.size(); dk++)
        		docentKleuren[dk] = docentKleurenAL.get(dk);
    	}
        
        if (launchState.containsKey("kijkDraaihoekNa"))
        	kijkDraaihoekNa = launchState.getBoolean("kijkDraaihoekNa");
        if (launchState.containsKey("kijkVlakkenNa"))
        	kijkVlakkenNa = launchState.getBoolean("kijkVlakkenNa");
                
        if (launchState.containsKey("docentDraaihoekX"))
        	docentDraaihoekX = launchState.getDouble("docentDraaihoekX");
        if (launchState.containsKey("docentDraaihoekY"))
        	docentDraaihoekY = launchState.getDouble("docentDraaihoekY");

        this.kijkDraaihoekNa = kijkDraaihoekNa;
        this.kijkVlakkenNa = kijkVlakkenNa;
        this.kijkNaActief = kijkDraaihoekNa || kijkVlakkenNa;
        this.docentDraaihoekX = docentDraaihoekX;
        this.docentDraaihoekY = docentDraaihoekY;
	
    	
		dlp.setSize(breedte + "px", hoogte + "px");
		
		if (viewerOnly)
		{	v3d = new Viewer3d(0,0,breedte, hoogte,this);
			v3d.initContext2d();
			
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
			
			if (kijkNaActief)
			{
				v3d.setWidgetVisible(v3d.kijkNaPanel, true);
			}
			
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
			
			vaktek.paint();
		}
		else
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
		
			tvv.tekenOpnieuw();
		}	
		
		ingevuld = false;
		
	}
	
	   public void answerChanged()
	    {
	    	if (kijkNaActief)
	    	{	
	    		
	System.out.println("answerChanged");

	    		correct = null;
	    		score = 0;
	    		nagekeken = false;
	    		ingevuld = true;
	    		comRoot.setChanged(true);
	    	}	
	    }


	@Override
	public void kijkNa() 
	{
    	if (!kijkNaActief)
    		return;
    	
    	//if (vlakkenKleurenOptie && profielenKleurenOptie && profilesOnly)
    	if (kijkVlakkenNa && profilesOnly)
    	{
System.out.println("kijkNa profiles");    		
    		
    		correct = vaktek.evalueer(docentKleuren);
    		nagekeken = true;
    		//vaktek.kijkNaPanel.setWidgetVisible(goedKrulImage, correct);
    		//vaktek.kijkNaPanel.setWidgetVisible(foutKruisImage, !correct);
    		
    		vaktek.kijkNaPanel.setWidgetVisible(vaktek.kijkNaLabelGoed, correct);
    		vaktek.kijkNaPanel.setWidgetVisible(vaktek.kijkNaLabelFout, !correct);
    		
    		
    	}
    	else if (kijkVlakkenNa && viewerOnly)
    	{
    		
System.out.println("kijkNa viewer");    		
    		correct = v3d.evalueer(docentKleuren);
    		nagekeken = true;
    		v3d.kijkNaPanel.setWidgetVisible(goedKrulImage, correct);
    		v3d.kijkNaPanel.setWidgetVisible(foutKruisImage, !correct);
    	}
    	else if (kijkDraaihoekNa)
    	{
    		
//System.out.println("kijkNa draaihoek");    		
    		correct = v3d.evalueer(docentDraaihoekX, docentDraaihoekY);
//System.out.println("correct = " + correct);    		
    		nagekeken = true;
    	}

		if (correct)
			score = scoreMax;
		
		if (correct && viewerOnly)
		{	//viewer.vinkjeLabel.setVisible(true);
			v3d.kijkNaPanel.setWidgetVisible(goedKrulImage,true);
			//vaktek.vinkjeLabel.setVisible(true);
		}
		else if (!correct && viewerOnly)
		{
			v3d.kijkNaPanel.setWidgetVisible(foutKruisImage,true);
		}
		else if (correct && profilesOnly)
		{
			//viewer.kruisjeLabel.setVisible(true);
			//v3d.kijkNaPanel.setWidgetVisible(tvGWT.goedKrulImage,false);
			//vaktek.kruisjeLabel.setVisible(true);
			vaktek.kijkNaPanel.setWidgetVisible(vaktek.kijkNaLabelGoed,true);
		}
		else if (!correct && profilesOnly)
		{
			vaktek.kijkNaPanel.setWidgetVisible(vaktek.kijkNaLabelFout,true);
		}
		
		ingevuld = true;

    	comRoot.setChanged(isCorrect().booleanValue());

		
	}

	public void zetVolledigeBreedte(int breedte) {
		// TODO Auto-generated method stub	
	}

	public int getAsHoogte() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getHeight() {
		return hoogte;
	}

	public int getWidth() {
		return breedte;
	}

	public void setAsHoogte(int ashoogte) {
		// TODO Auto-generated method stub	
	}

	public void zetNagekeken(boolean b) {
		// TODO Auto-generated method stub	
	}
	
    

}
