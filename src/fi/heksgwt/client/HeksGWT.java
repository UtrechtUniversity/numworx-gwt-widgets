package fi.heksgwt.client;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.DataResource;

import com.google.gwt.typedarrays.shared.ArrayBuffer;

import java.util.logging.Logger;

import fi.heksgwt.client.vectortek.Tekening;
import fi.heksgwt.client.scobjects.ScLabel;
import fi.heksgwt.client.scobjects.ScContainer;
import fi.heksgwt.client.text.Text;

public class HeksGWT implements EntryPoint, InteractionStub 
{
	public static Text rb;
	
	private static Logger logger = Logger.getLogger("HeksGWT");
	
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	private OpdrNavIF comRoot;

	// UI
	LayoutPanel dlp;
	//LayoutPanel canvasPanel;
	
	int buttonWidth = 40;
	int buttonHeight = 22;
	int pushSize = 24;

	int breedte = 500;
	int hoogte = 450;
	int bottomHeight = 32;
	int leftOffset = 5;
	int topOffset = 5;
	
	HeksGWTClientBundle heksGWTClientBundle;
	HeksGWTCssResource heksGWTCssResource;
	
	//DataResource blokjeMinResource;
	
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	ScContainer pagina21Panel;
	ScContainer pagina22Panel;
	ScContainer pagina23Panel;
	ScContainer pagina24Panel;
	
	// parametrisatie
	int paginaNummer;
	boolean alleenErin;
	boolean alleenEruit;

	// figuren
	Map<String,Object> blokjePlusMap;
	Map<String,Object> blokjePlus24Map;
	Map<String,Object> blokjeMinMap;
	Map<String,Object> blokjeMin24Map;
	Map<String,Object> beginEindPotMap;
	Map<String,Object> potErinMap;
	Map<String,Object> potEruitMap;
	Map<String,Object> vloerMap;
	Map<String,Object> vloer23Map;
	Map<String,Object> potinhoudMap;
	Map<String,Object> potinhoud23Map;
	Map<String,Object> potinhoud24Map;
	Map<String,Object> potMap;
	Map<String,Object> pot23Map;
	Map<String,Object> pot24Map;
	Map<String,Object> emmerMap;
	Map<String,Object> emmerKleinMap;
	
	Context2d heksGWTContext2d;
	Canvas heksGWTCanvas;
	
	public void getImages()
	{
		rb = GWT.create(Text.class);
		
		heksGWTClientBundle = GWT.create(HeksGWTClientBundle.class);
		heksGWTCssResource = heksGWTClientBundle.getHeksGWTCssResource();
		heksGWTCssResource.ensureInjected();
		
	
	}
	
	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new LayoutPanel();
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

//		RootPanel.get(holderId).add(dlp);
//		RootPanel.get(holderId).addStyleName("root");
		RootLayoutPanel.get().add(dlp);
		RootLayoutPanel.get().addStyleName("root");
		
		Stub.publish(this);
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
				
	}
	
	public HeksGWT()
	{

	}
	
	public HeksGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
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
		dlp = new LayoutPanel();
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		
		init(breedte, hoogte, launchState, randomVarWaarden);


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
			
    		
    		
    	}
    }
    
	
	public Widget asWidget()
	{
		return dlp;
	}
	
	@Override
	public HashMap<String, Object> getState()
	{
System.out.println("HeksGWT getState");		
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		if (paginaNummer == 1)
		{
			result = ((Pagina21Panel) pagina21Panel).getState();
		}
		else if (paginaNummer == 2)
		{
			result = ((Pagina22Panel) pagina22Panel).getState();
		}
		else if (paginaNummer == 3)
		{
			result = ((Pagina23Panel) pagina23Panel).getState();
		}
		
		return result;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		if(h == null||h.isEmpty()) return;
System.out.println("HeksGWT setState");

		if (paginaNummer == 1)
		{
			((Pagina21Panel) pagina21Panel).setState(h);
		}
		else if (paginaNummer == 2)
		{
			((Pagina22Panel) pagina22Panel).setState(h);
		}
		else if (paginaNummer == 3)
		{
			((Pagina23Panel) pagina23Panel).setState(h);
		}

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
		return Boolean.TRUE;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		FormuleKeyboardIF kb = comRoot.getKeyboard();
		FormuleHolder.installKeyboard(kb);
	}
	
	public void init(int width, int height, Map<String, Object> map, //launchState,
			Map<String, Number> values) 
	{
		// launchdata/state		
		this.breedte = width;
		this.hoogte = height;
		
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		
		ObjectMap launchState = JSONUtilities.wrapMap(map);
		
		if (launchState.containsKey("blokjeplushash"))
		{	blokjePlusMap = launchState.getMap("blokjeplushash");
		}
		if (launchState.containsKey("blokjeplus24hash"))
		{	blokjePlus24Map = launchState.getMap("blokjeplus24hash");
		}
		if (launchState.containsKey("blokjeminhash"))
		{	blokjeMinMap = launchState.getMap("blokjeminhash");
		}
		if (launchState.containsKey("blokjemin24hash"))
		{	blokjeMin24Map = launchState.getMap("blokjemin24hash");
		}
		if (launchState.containsKey("begineindpothash"))
		{	beginEindPotMap = launchState.getMap("begineindpothash");
		}
		if (launchState.containsKey("poterinhash"))
		{	potErinMap = launchState.getMap("poterinhash");
		}
		if (launchState.containsKey("poteruithash"))
		{	potEruitMap = launchState.getMap("poteruithash");
		}
		if (launchState.containsKey("emmerhash"))
		{	emmerMap = launchState.getMap("emmerhash");
		}
		if (launchState.containsKey("emmerkleinhash"))
		{	emmerKleinMap = launchState.getMap("emmerkleinhash");
		}
		if (launchState.containsKey("vloerhash"))
		{	vloerMap = launchState.getMap("vloerhash");
		}
		if (launchState.containsKey("vloer23hash"))
		{	vloer23Map = launchState.getMap("vloer23hash");
		}
		if (launchState.containsKey("potinhoudhash"))
		{	potinhoudMap = launchState.getMap("potinhoudhash");
		}
		if (launchState.containsKey("potinhoud23hash"))
		{	potinhoud23Map = launchState.getMap("potinhoud23hash");
		}
		if (launchState.containsKey("potinhoud24hash"))
		{	potinhoud24Map = launchState.getMap("potinhoud24hash");
		}
		if (launchState.containsKey("pothash"))
		{	potMap = launchState.getMap("pothash");
		}
		if (launchState.containsKey("pot23hash"))
		{	pot23Map = launchState.getMap("pot23hash");
		}
		if (launchState.containsKey("pot24hash"))
		{	pot24Map = launchState.getMap("pot24hash");
		}
		
		if (launchState.containsKey("paginanummer"))
		{	paginaNummer = launchState.getInt("paginanummer");
		}
		if (launchState.containsKey("alleenerin"))
		{	alleenErin = launchState.getBoolean("alleenerin");
		}
		if (launchState.containsKey("alleeneruit"))
		{	alleenEruit = launchState.getBoolean("alleeneruit");
		}
		
		heksGWTCanvas = Canvas.createIfSupported();
		if (heksGWTCanvas == null) 
		{   RootPanel.get().add(new Label(upgradeMessage));
	      	return;
	    }
		
		heksGWTCanvas.addStyleName(heksGWTCssResource.canvas());

		//int paginaBreedte = breedte;
		//int paginaHoogte = hoogte;
		
		heksGWTCanvas.setWidth(breedte + "px");
		heksGWTCanvas.setHeight(hoogte + "px");
		heksGWTCanvas.setCoordinateSpaceWidth(breedte);
		heksGWTCanvas.setCoordinateSpaceHeight(hoogte);
		
		dlp.add(heksGWTCanvas);
		dlp.setWidgetLeftWidth(heksGWTCanvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		dlp.setWidgetTopHeight(heksGWTCanvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);

		
		heksGWTContext2d = heksGWTCanvas.getContext2d();
		
		if (paginaNummer == 1)
		{	
			//pagina21Panel = new Pagina21Panel(0, 0, 785, 660, this);
			pagina21Panel = new Pagina21Panel(0, 0, 825, 660, this);
		
			double sx = ((1.0 * breedte) / pagina21Panel.breedte);
			double sy = ((1.0 * hoogte) / pagina21Panel.hoogte);
			double schaal = Math.min(sx, sy); // * 0.95;

			pagina21Panel.schaal(schaal);

			((Pagina21Panel) pagina21Panel).initHandlers();
			((Pagina21Panel) pagina21Panel).initOpnieuwKnop();
			
			((Pagina21Panel) pagina21Panel).setState(map);
			
			pagina21Panel.paint(heksGWTContext2d);
		}
		else if (paginaNummer == 2)
		{	
			//pagina22Panel = new Pagina22Panel(0, 0, 785, 660, this);
			pagina22Panel = new Pagina22Panel(0, 0, 825, 660, this);
			
			double sx = ((1.0 * breedte) / pagina22Panel.breedte);
			double sy = ((1.0 * hoogte) / pagina22Panel.hoogte);
			double schaal = Math.min(sx, sy);//* 0.95;

			pagina22Panel.schaal(schaal);
			
			((Pagina22Panel) pagina22Panel).initHandlers();
			((Pagina22Panel) pagina22Panel).initOpnieuwKnop();
			
			((Pagina22Panel) pagina22Panel).setState(map);
			
			pagina22Panel.paint(heksGWTContext2d);
		}
		else if (paginaNummer == 3)
		{	
			//pagina23Panel = new Pagina23Panel(0, 0, 785, 660, this);
			pagina23Panel = new Pagina23Panel(0, 0, 815, 660, this);
			
			double sx = ((1.0 * breedte) / pagina23Panel.breedte);
			double sy = ((1.0 * hoogte) / pagina23Panel.hoogte);
			double schaal = Math.min(sx, sy);//* 0.95;

			pagina23Panel.schaal(schaal);
			
			((Pagina23Panel) pagina23Panel).initHandlers();
			((Pagina23Panel) pagina23Panel).initOpnieuwKnop();
			
			if (alleenErin)
				((Pagina23Panel) pagina23Panel).zetAlleenErin();
			if (alleenEruit)
				((Pagina23Panel) pagina23Panel).zetAlleenEruit();
			
			((Pagina23Panel) pagina23Panel).setState(map);
			
			pagina23Panel.paint(heksGWTContext2d);
				
		}
		else if (paginaNummer == 4)
		{	
			//b = 394, h = 221
			//pagina24Panel = new Pagina24Panel(0, 0, 3*266/2 -5, 3*151/2 -5, this);
			pagina24Panel = new Pagina24Panel(0, 0, 394, 261, this);
			
			double sx = ((1.0 * breedte) / pagina24Panel.breedte);
			double sy = ((1.0 * hoogte) / pagina24Panel.hoogte);
			double schaal = Math.min(sx, sy);//* 0.95;

			pagina24Panel.schaal(schaal);
			
			((Pagina24Panel) pagina24Panel).initHandlers();

			// dit is er niet
			//((Pagina24Panel) pagina24Panel).initOpnieuwKnop();
			//((Pagina24Panel) pagina24Panel).setState(map);
			
			pagina24Panel.paint(heksGWTContext2d);
		}

		dlp.forceLayout();
	}
	
	public void paint()
	{
		if ((paginaNummer == 1) && (pagina21Panel != null))
			pagina21Panel.paint(heksGWTContext2d);
		else if ((paginaNummer == 2) && (pagina22Panel != null))
			pagina22Panel.paint(heksGWTContext2d);
		else if ((paginaNummer == 3) && (pagina23Panel != null))
			pagina23Panel.paint(heksGWTContext2d);
		else if ((paginaNummer == 4) && (pagina24Panel != null))
			pagina24Panel.paint(heksGWTContext2d);
	}
	
	@Override
	public void kijkNa() {
		// TODO Auto-generated method stub
		
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
		return hoogte;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return breedte;
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

}
