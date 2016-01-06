package fi.algebrapijlengwt.client;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
//import java.util.Vector;

//import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.dom.client.Style.Unit;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.resources.client.ImageResource;

//import fi.algebrapijlengwt.client.opdr.UitvoerSchuifComponent;
import fi.algebrapijlengwt.client.expressies_ap.*;

import fi.algebrapijlengwt.client.text.Text;

public class AlgebraPijlenGWT implements EntryPoint, InteractionStub //InteractionView 
{
	public static Text rb;
	
	static Logger logger = Logger.getLogger("APGWT");
	
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	protected static boolean simplify = true;
	
	// UI
	DockLayoutPanel dlp;
	LayoutPanel canvasPanel;
	
	Canvas algebraPijlenGWTCanvas;
	Context2d algebraPijlenGWTContext2d;
	AlgebraSchuifVeld asv;
	
	ToggleButton linksRechtsButton;
	PushButton wisButton;
	CheckBox tabelBox, grafiekBox;
	
	int breedte = 500;
	int hoogte = 450;
	int bottomHeight = 32;
	int leftOffset = 5;
	int topOffset = 10;
	
	int buttonWidth = 50;
	int buttonHeight = 22;
	int checkBoxWidth = 60;

	CssColor bottomBgColor = CssColor.make(220, 220, 220);	
	
	AlgebraPijlenGWTClientBundle algebraPijlenGWTClientBundle;
	AlgebraPijlenGWTCssResource algebraPijlenGWTCss;
	//ImageResource foutKruisResource, goedKrulResource;
	//Image foutKruisImage, goedKrulImage;
	
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	int lastStartX, lastStartY, lastMoveX, lastMoveY;
	
	// parametrisatie
	boolean toolkit = true;
	boolean alleenInvullen = false;
	boolean isDemo = false;
	
	boolean brugklas = false;
	boolean terugHeen = true;
	boolean tabelOptie = true;
	boolean grafiekOptie =  true;
	
	//boolean standAlone = false;
	
	boolean kijkNaActief = false;
	PushButton kijkNaButton;
	List<String> docentExpressieStrings = new ArrayList<String>();
	List<Expressie> docentExpressies = new ArrayList<Expressie>();
	int scoreMax = 10;
	int score = 0;
	Boolean correct = null;
	boolean ingevuld = false;
	boolean nagekeken = false;
	
	private int mode;
	private OpdrNavIF comRoot;
	
	boolean asvSetState = false;
	
	public static HashMap<String,Object> clipBoard = null;

	public void getImages() 
	{
		rb = GWT.create(Text.class);
		
		algebraPijlenGWTClientBundle = GWT.create(AlgebraPijlenGWTClientBundle.class);
		algebraPijlenGWTCss = algebraPijlenGWTClientBundle.getAlgebraPijlenGWTCSS();
		algebraPijlenGWTCss.ensureInjected();
		
//		foutKruisResource = algebraPijlenGWTClientBundle.foutKruisResource();
//		goedKrulResource = algebraPijlenGWTClientBundle.goedKrulResource();
//		foutKruisImage = new Image(foutKruisResource);
//		goedKrulImage = new Image(goedKrulResource);
		
//System.out.println("getImages");		
	}
	
	
	public void onModuleLoad() 
	{
		getImages();
		
		//dlp = new DockLayoutPanel(Style.Unit.PX);
		//dlp.addStyleName(algebraPijlenGWTCss.dock());
		//dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		
		canvasPanel = new LayoutPanel(); 
		canvasPanel.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(canvasPanel);
		//RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(algebraPijlenGWTCss.root());
	
		//standAlone = true; // Wim een debuggertje was is blijven hangen?
		
		Stub.publish(this);
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
			
	}

/*
	private void initCanvas() {
		//canvasPanel = new LayoutPanel();
		//dlp.add(canvasPanel);
		
		int canvasBreedte = breedte;
		int canvasHoogte = hoogte;
		algebraPijlenGWTCanvas = Canvas.createIfSupported();
		if (algebraPijlenGWTCanvas == null) 
		{   RootPanel.get(holderId).add(new Label(upgradeMessage));
	        return;
	    }
		algebraPijlenGWTCanvas.setWidth(canvasBreedte + "px");
		algebraPijlenGWTCanvas.setHeight(canvasHoogte + "px");
		algebraPijlenGWTCanvas.setCoordinateSpaceWidth(canvasBreedte);
		algebraPijlenGWTCanvas.setCoordinateSpaceHeight(canvasHoogte);
		algebraPijlenGWTCanvas.addStyleName("canvas");
		algebraPijlenGWTCanvas.addStyleName(algebraPijlenGWTCss.canvas());
		
		MouseHandler mouseHandler = new MouseHandler();
		algebraPijlenGWTCanvas.addMouseDownHandler(mouseHandler);
		algebraPijlenGWTCanvas.addMouseMoveHandler(mouseHandler);
		algebraPijlenGWTCanvas.addMouseUpHandler(mouseHandler);
	  
	  	TouchHandler touchHandler = new TouchHandler();
	  	algebraPijlenGWTCanvas.addTouchStartHandler(touchHandler);
	  	algebraPijlenGWTCanvas.addTouchMoveHandler(touchHandler);
	  	algebraPijlenGWTCanvas.addTouchEndHandler(touchHandler);
		
		canvasPanel.add(algebraPijlenGWTCanvas);
		canvasPanel.setWidgetLeftWidth(algebraPijlenGWTCanvas, 0, Style.Unit.PX, canvasBreedte, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(algebraPijlenGWTCanvas, 0, Style.Unit.PX, canvasHoogte, Style.Unit.PX);

		//makeLeft();
		
		algebraPijlenGWTContext2d = algebraPijlenGWTCanvas.getContext2d();

		asv = new AlgebraSchuifVeld(0, 0, breedte, hoogte, algebraPijlenGWTContext2d, this);
		
		asv.paint();
		
		makeLeft();
	
	}
*/	
	
	public void makeLeft()
	{
		if (!toolkit || isDemo || alleenInvullen)
			return;
		
		int currentX = (asv.toolsWidth - buttonWidth) / 2; //leftOffset;
		int currentY = topOffset + 270;
		if (brugklas)
			currentY = topOffset + 195;
		
		//linksRechtsButton = new ToggleButton("links", "rechts");
		linksRechtsButton = new ToggleButton(rb.linksLabel(), rb.rechtsLabel());
		linksRechtsButton.addStyleName(algebraPijlenGWTCss.togglebutton());
		if (terugHeen)
		{	
			canvasPanel.add(linksRechtsButton);
			canvasPanel.setWidgetLeftWidth(linksRechtsButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(linksRechtsButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
			linksRechtsButton.addClickHandler(new PushClickHandler());
		
			currentY += buttonHeight + topOffset;
		}
		
		//currentX = (asv.toolsWidth - checkBoxWidth) / 2; //leftOffset;
		currentX = 2 * leftOffset;
		
		tabelBox = new CheckBox();
		//tabelBox.setText("tabel");
		tabelBox.setText(rb.tabelLabel());
		tabelBox.addStyleName(algebraPijlenGWTCss.checkbox());
		if (tabelOptie)
		{	
			canvasPanel.add(tabelBox);
			canvasPanel.setWidgetLeftWidth(tabelBox, currentX, Style.Unit.PX, checkBoxWidth - 10, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(tabelBox, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
			tabelBox.addClickHandler(new PushClickHandler());
		
			currentY += buttonHeight + topOffset;
		}
		
		//currentX = (asv.toolsWidth - checkBoxWidth) / 2; //leftOffset;
		currentX = 2 * leftOffset;
		
		grafiekBox = new CheckBox();
		//grafiekBox.setText("grafiek");
		grafiekBox.setText(rb.grafiekLabel());
		grafiekBox.addStyleName(algebraPijlenGWTCss.checkbox());
		if (grafiekOptie)
		{	
			canvasPanel.add(grafiekBox);
			canvasPanel.setWidgetLeftWidth(grafiekBox, currentX, Style.Unit.PX, checkBoxWidth + 20, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(grafiekBox, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
			grafiekBox.addClickHandler(new PushClickHandler());
		
			currentY += buttonHeight + topOffset;
		}
		
		currentX = (asv.toolsWidth - buttonWidth) / 2; //leftOffset;
		
		//wisButton = new PushButton("wis");
		wisButton = new PushButton(rb.wisKnopLabel());
		wisButton.addStyleName(algebraPijlenGWTCss.pushbutton());
		canvasPanel.add(wisButton);
		canvasPanel.setWidgetLeftWidth(wisButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(wisButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
		wisButton.addClickHandler(new PushClickHandler());
		
		currentY += buttonHeight + topOffset;
		

	}
	
	public AlgebraPijlenGWT()
	{
		//this(null, null, null); XXX Wim: Hier begrijp ik niets van
	}

	
	public AlgebraPijlenGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
/*		
		if (h != null && h.get("breedte") != null)
			breedte = (Integer) h.get("breedte");
		if (h != null && h.get("hoogte") != null)
			hoogte = (Integer) h.get("hoogte");
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
*/			
		if (h.containsKey("breedte"))
			breedte = h.getInt("breedte");
		if (h.containsKey("hoogte"))
			hoogte = h.getInt("hoogte");
		if (h.containsKey("interactiePanelLaunchState"))
			launchState = h.getMap("interactiePanelLaunchState");

		getImages();
		
		//dlp = new DockLayoutPanel(Style.Unit.PX);
		//dlp.addStyleName(algebraPijlenGWTCss.dock());
		//dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		canvasPanel = new LayoutPanel(); 
		canvasPanel.setSize("" + breedte + "px", "" + hoogte + "px");

		//RootPanel.get(holderId).add(dlp);
		//RootPanel.get(holderId).addStyleName(algebraPijlenHWTCss.root());
	
		init(breedte, hoogte, launchState, randomVarWaarden);


	}

	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{   
		boolean mouseDown = false;
		
		public void onMouseDown(MouseDownEvent e)
		{
			e.preventDefault();
			
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDown = true;
			
			asv.mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		public void onMouseMove(MouseMoveEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();

			asv.mouseMoveTouchMoveAction(eventX, eventY);
			
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;

			int eventX = e.getX();
			int eventY = e.getY();
			
			asv.mouseUpTouchEndAction(eventX,eventY);

		}

	} //MouseHandler
	
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				int eventX = touch.getPageX() - algebraPijlenGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - algebraPijlenGWTCanvas.getAbsoluteTop();				
				
				lastStartX = eventX; 
				lastStartY = eventY;
				lastMoveX = -1000;
				lastMoveY = -1000;
				
				asv.mouseDownTouchStartAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
			    int eventX = touch.getPageX() - algebraPijlenGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - algebraPijlenGWTCanvas.getAbsoluteTop();				
			    
				lastMoveX = eventX; 
				lastMoveY = eventY;
				
				asv.mouseMoveTouchMoveAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			//de Touch die beeindigd werd zit niet in e.getTouches()?? 
			//if (e.getTouches().length() > 0)
			//{
			//	Touch touch = e.getTouches().get(0);
				
			//    int eventX = touch.getPageX() - algebraPijlenHWTCanvas.getAbsoluteLeft();
			//	int eventY = touch.getPageY() - algebraPijlenHWTCanvas.getAbsoluteTop();
				
				int eventX = 0;
				int eventY = 0;
			
				if (lastMoveX <= -999)
				{
					eventX = lastStartX;
					eventY = lastStartY;
				}
				else
				{
					eventX = lastMoveX;
					eventY = lastMoveY;
					
				}
			    
				asv.mouseUpTouchEndAction(eventX,eventY);
				
		    //}
		}

	}

	//class PushMouseDownHandler implements MouseDownHandler
	class PushClickHandler implements ClickHandler
	{
		//public void onMouseDown(MouseDownEvent e)
		public void onClick(ClickEvent e)
		{
	  		// DIT NIET toevoegen!!
    		//e.preventDefault();
			e.stopPropagation();
    			
			if (e.getSource() == linksRechtsButton)
			{
				asv.linksRechtsAction();
			}
			else if (e.getSource() == wisButton)
			{
				asv.wisAction();
			}
			else if (e.getSource() == tabelBox)
			{
				boolean checked = tabelBox.getValue();
				asv.toonTabellen = checked;
				asv.zetVeranderd();

			}
			else if (e.getSource() == grafiekBox)
			{
				boolean checked = grafiekBox.getValue();
				asv.toonGrafiekComponent(checked);
				asv.zetVeranderd();
			}
			else if (e.getSource() == kijkNaButton)
			{
//System.out.println("kijkNaButton");

				kijkNa();
			}

		}	
	}
		

	public Widget asWidget()
	{
		return canvasPanel; //dlp;
	}
	
	@Override
	public HashMap<String, Object> getState()
	{
		HashMap<String, Object> h = asv.getState();
		h.put("nagekeken", new Boolean(nagekeken));
		h.put("ingevuld", new Boolean(ingevuld));
		if (clipBoard != null)
			h.put("clipBoard", clipBoard);
		
		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		
logger.info("AP setState");

		asvSetState = true;
		asv.setState(h);
		asvSetState = false;
		
		ingevuld = false;
		
//System.out.println("after asv setState");		

		if (h.containsKey("nagekeken"))
		{	nagekeken = ((Boolean) h.get("nagekeken")).booleanValue();
		}
		if (h.containsKey("ingevuld"))
		{	ingevuld = ((Boolean) h.get("ingevuld")).booleanValue();
		}

		if (!ingevuld)
			asv.changed = false;
		
		if (ingevuld && (mode == 0 || nagekeken))
		//if (nagekeken)
			kijkNa();
					
		
		//asv.setState(h);
		asv.paint();

	}

	@Override
	public int getScore()
	{

		return score;
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

    public void maakDocentExpressies()
    {	docentExpressies.clear();
    	for (int i = 0; i < docentExpressieStrings.size(); i++)
    	{	String text = docentExpressieStrings.get(i);
    		String formuleText = "$f" + text + "@";
    		Expressie exp = FormuleParser_ap.geefExpressie(formuleText);
    		docentExpressies.add(exp);
    		
//System.out.println("exp = " + exp.toString());    		
    	}
    	
    }
    
    public void answerChanged()
    {

    	if ((comRoot != null) && kijkNaActief && !asvSetState)
		{	
//System.out.println("changed");    		
    		correct = null;
    		nagekeken = false;
    		score = 0;
    		ingevuld = true;
    		asv.changed = true;
			//comRoot.setChanged(isCorrect());
    		comRoot.setChanged(true);
			
//System.out.println("corr " + isCorrect());			
		}	
    	
    }

	
	@Override
	public void kijkNa() 
	{
		if (!kijkNaActief)
    		return;
  
//System.out.println("kijkNa");

    	//ingevuld = !asv.veldIsLeeg();
		ingevuld = true;//asv.changed;
    	
//    	if (!ingevuld)
//    		return;
    	
//System.out.println("ingevuld");

    	maakDocentExpressies();
    	
    	// geen opdracht, alles goed
    	if (docentExpressies.size() == 0)
    	{	score = scoreMax;
    		//fireChangeEvent();
    		return;
    	}

//System.out.println("docentExpressies");

    	Vector leerlingExpressieUVS = asv.vindExpressieUVS();
//System.out.println("llgUVS = " + leerlingExpressieUVS.size());    	
//System.out.println("docS = " + docentExpressieStrings.size());
//System.out.println("docE = " + docentExpressies.size());
    	
		// geen valide expressie
		if (leerlingExpressieUVS.size() == 0)
		{
			correct = new Boolean(false);
			comRoot.setChanged(isCorrect().booleanValue());
			return;
		}
    	

		int hits = 0;
		// hier zijn er docent expressies
		for (int lCnt = 0; lCnt < leerlingExpressieUVS.size(); lCnt++)
		{	UitvoerSchuifComponent uvs = (UitvoerSchuifComponent) leerlingExpressieUVS.elementAt(lCnt);
			Expressie llgExp = null;
			if (uvs.geefUitvoer(0) != null)
				llgExp = uvs.geefUitvoer(0);
			else if (uvs.geefVerborgenUitvoer(0) != null)
				llgExp = uvs.geefVerborgenUitvoer(0);

			String llgExpStr = llgExp.toString();
//System.out.println(llgExpStr);
//System.out.println(uvs.geefBronDefaultVarnaam());
			String llgExpStrC = llgExpStr.replaceAll(uvs.geefBronDefaultVarnaam(), "x");
//System.out.println(llgExpStrC);
			llgExp = FormuleParser_ap.geefExpressie("$f" + llgExpStrC + "@");

			correct = new Boolean(false);
			if (llgExp != null)
			{	
				
				for (int dCnt = 0; dCnt < docentExpressies.size(); dCnt++)
				{	Expressie docExp = docentExpressies.get(dCnt);
//System.out.println("docExp = " + docExp.toString());
//System.out.println("llgExp = " + llgExp.toString());
					if (Algebra.isGelijkwaardig(docExp, llgExp))
					{	hits++;
						correct = true;
					}
				}
				
				if (correct.equals(Boolean.TRUE))
				{	if (!uvs.pijlUit[0].isStapel && !uvs.pijlUit[0].vast && !uvs.pijlUit[0].actief) // && (im != null))
					{	
//System.out.println("correct");					
						uvs.pijlUit[0].im = "V";
						
						uvs.pijlUit[0].paint();
					}
				}
				else
				{	if (!uvs.pijlUit[0].isStapel && !uvs.pijlUit[0].vast && !uvs.pijlUit[0].actief) // && (im != null))
					{	
//System.out.println("!correct");					
						uvs.pijlUit[0].im = "X";
						
						uvs.pijlUit[0].paint();
					}
				}
			}
		}	
//System.out.println("hits = " + hits);
		
		int scorePerExpressie = scoreMax / docentExpressies.size();
		if (hits == 0)
			score = 0;
		else if (hits == docentExpressies.size())
			score = scoreMax;
		else
			score = hits * scorePerExpressie;
		
/*		
		// leerlingExpressieUVS.size() - hits is aantal foute expressies
		if (leerlingExpressieUVS.size() >= docentExpressies.size())
			score = Math.max(0, scoreMax - (leerlingExpressieUVS.size() - hits));
		else
			score = Math.max(0, scoreMax - (docentExpressies.size() - leerlingExpressieUVS.size() - hits));
*/			

		asv.tekenOpnieuw();
		nagekeken = true;

		
		//fireChangeEvent();

/*
    	//fire actionEvent
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "changed");
		for (int lCnt = 0; lCnt < listeners.size(); lCnt++)
		{
			((ActionListener) listeners.elementAt(lCnt)).actionPerformed(event);
		}
*/		
		comRoot.setChanged(isCorrect().booleanValue());
		
//System.out.println("comRoot " + isCorrect().booleanValue());		
		
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
	}

	@Override
	public int getAsHoogte() {
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
	}

	
	@Override
	public void init(int width, int height, Map<String, Object> map, //launchState,
					 Map<String, Number> values) 
	{
logger.info("AlgebraPijlenGWT init");		
		
		this.breedte = width;
		this.hoogte = height;
		//dlp.setPixelSize(breedte , hoogte ); // Wim: nu zijn pas de maten bekend. 
		 
		canvasPanel.setPixelSize(breedte, hoogte);
	
		//this.launchState = launchState;
		ObjectMap launchState = JSONUtilities.wrapMap(map);
		
		getImages();
		
		if (launchState.containsKey("toolkit"))
			toolkit = launchState.getBoolean("toolkit");
		
		if (launchState.containsKey("alleenInvullen"))
			alleenInvullen = launchState.getBoolean("alleenInvullen");
		
		if (launchState.containsKey("isDemo"))
			isDemo = launchState.getBoolean("isDemo");

		if (launchState.containsKey("brugklas"))
			brugklas = launchState.getBoolean("brugklas");
		
		if (launchState.containsKey("terugHeen"))
			terugHeen = launchState.getBoolean("terugHeen");
		
		if (launchState.containsKey("tabelOptie"))
			tabelOptie = launchState.getBoolean("tabelOptie");
		
		if (launchState.containsKey("grafiekOptie"))
			grafiekOptie = launchState.getBoolean("grafiekOptie");
		
		if (launchState.containsKey("docentExpressieStrings"))
		{	docentExpressieStrings = launchState.getStringList("docentExpressieStrings");
//System.out.println("contains dES " + docentExpressieStrings.size());		
		}
		
		if (launchState.containsKey("kijkNaActief"))
			kijkNaActief = launchState.getBoolean("kijkNaActief");
//GWT		
		//zetKijkNaActief(kijkNaActief);

		if (launchState.containsKey("scoreMax"))
			scoreMax = launchState.getInt("scoreMax");

		
		//canvasPanel = new LayoutPanel();
		//dlp.add(canvasPanel);
		
		int canvasBreedte = breedte;
		int canvasHoogte = hoogte;
		algebraPijlenGWTCanvas = Canvas.createIfSupported();
		algebraPijlenGWTCanvas.setWidth(canvasBreedte + "px");
		algebraPijlenGWTCanvas.setHeight(canvasHoogte + "px");
		algebraPijlenGWTCanvas.setCoordinateSpaceWidth(canvasBreedte);
		algebraPijlenGWTCanvas.setCoordinateSpaceHeight(canvasHoogte);
		algebraPijlenGWTCanvas.addStyleName("canvas");
		
		if (algebraPijlenGWTCanvas == null) 
		{   RootPanel.get(holderId).add(new Label(upgradeMessage));
	        return;
	    }
		
		algebraPijlenGWTCanvas.addStyleName(algebraPijlenGWTCss.canvas());
		
		MouseHandler mouseHandler = new MouseHandler();
		algebraPijlenGWTCanvas.addMouseDownHandler(mouseHandler);
		algebraPijlenGWTCanvas.addMouseMoveHandler(mouseHandler);
		algebraPijlenGWTCanvas.addMouseUpHandler(mouseHandler);
	  
	  	TouchHandler touchHandler = new TouchHandler();
	  	algebraPijlenGWTCanvas.addTouchStartHandler(touchHandler);
	  	algebraPijlenGWTCanvas.addTouchMoveHandler(touchHandler);
	  	algebraPijlenGWTCanvas.addTouchEndHandler(touchHandler);
		
		canvasPanel.add(algebraPijlenGWTCanvas);
		canvasPanel.setWidgetLeftWidth(algebraPijlenGWTCanvas, 0, Style.Unit.PX, canvasBreedte, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(algebraPijlenGWTCanvas, 0, Style.Unit.PX, canvasHoogte, Style.Unit.PX);

		//makeLeft();
		
		algebraPijlenGWTContext2d = algebraPijlenGWTCanvas.getContext2d();

		asv = new AlgebraSchuifVeld(0, 0, breedte, hoogte, algebraPijlenGWTContext2d, this);
		
		//makeLeft();
		
		// map is altijd != null
		int aantalSc = 0;
		if (launchState.containsKey("aantalSc"))
			aantalSc = launchState.getInt("aantalSc");
 
		if (aantalSc > 0)	
		{	asvSetState = true;
			asv.setState(map);
			asvSetState = false;
		}

		makeLeft();
		
		if (kijkNaActief)
		{	
			//kijkNaButton = new PushButton("kijk na");
			kijkNaButton = new PushButton(rb.kijkNa());
			kijkNaButton.addStyleName(algebraPijlenGWTCss.pushbutton());
			canvasPanel.add(kijkNaButton);
			canvasPanel.setWidgetLeftWidth(kijkNaButton, (breedte - 60)/2, Style.Unit.PX, 60, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(kijkNaButton, hoogte - 40, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			kijkNaButton.addClickHandler(new PushClickHandler());

		}
			
			
		asv.paint();
		
		asv.changed = false;
		ingevuld = false;
	}


	//@Override
	public void zetNagekeken(boolean b) {
	}

	

	//@Override
	public int[][] getScoreObjectives() {
		return null;
	}

}

class Point
{
	int x; int y;
	
	public Point(int x, int y)
	{
		this.x = x; this.y = y;
	}
}

