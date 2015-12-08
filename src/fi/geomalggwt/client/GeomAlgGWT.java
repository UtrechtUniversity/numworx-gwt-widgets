package fi.geomalggwt.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

//import nl.uu.fi.dwo.interaction.client.InteractionView;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
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

public class GeomAlgGWT implements EntryPoint, InteractionStub
{
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	static final Logger logger = Logger.getLogger("GeomAlgGWT");
	
	// UI
	DockLayoutPanel dlp;
	LayoutPanel canvasPanel;
	LayoutPanel bottomPanel;
	Canvas geomAlgGWTCanvas;
	Context2d geomAlgGWTContext2d;
	LineaalVer lineaalVer;
	LineaalHor lineaalHor;
	AlgebraVeld av;
	
	PushButton wisButton, terugButton, xButton, yButton, zButton;
	CheckBox directBox;
	
	int breedte = 500;
	int hoogte = 450;
	int bottomHeight = 42;
	int leftOffset = 15;
	int topOffset = 5;
	
	int buttonWidth1 = 40;
	int buttonWidth2 = 22;
	int buttonHeight = 22;
	int checkBoxWidth = 180;

	GeomAlgGWTClientBundle geomAlgGWTClientBundle;
	GeomAlgGWTCssResource geomAlgGWTCss;
	
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;

	boolean varWaardeZichtbaar = false;
	boolean oppWaardeZichtbaar = false;
	boolean formuleZichtbaar = true;
	boolean constructieTools = true;
	boolean alleenOppervlaktes = false;
	boolean werkblad = false;
	boolean oppervlaktesZichtbaar = true;
	boolean lengtesBreedtesZichtbaar = true;
	boolean negatieveWaarden = true;
	boolean puzzelen = false;

	ImageResource resetResource;
	Image resetImage;
	PushButton resetButton;
	
int testY = hoogte / 2; 

boolean touchStart = false;

	public void getImages() 
	{
		geomAlgGWTClientBundle = GWT.create(GeomAlgGWTClientBundle.class);
		geomAlgGWTCss = geomAlgGWTClientBundle.getGeomAlgGWTCSS();
		geomAlgGWTCss.ensureInjected();
		
		resetResource = geomAlgGWTClientBundle.resetResource();
		resetImage = new Image(resetResource);
		resetImage.addStyleName(geomAlgGWTCss.pushimage());

	}
	
	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(geomAlgGWTCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(geomAlgGWTCss.root());

/*		
		bottomPanel = new LayoutPanel();
		bottomPanel.addStyleName(geomAlgGWTCss.bottom());

		int canvasBreedte = breedte;
		int canvasHoogte = hoogte;
		
		if (constructieTools)
		{	dlp.addSouth(bottomPanel, bottomHeight);
			canvasHoogte -= bottomHeight;
		}
		
		canvasPanel = new LayoutPanel();
		dlp.add(canvasPanel);
		
		geomAlgGWTCanvas = Canvas.createIfSupported();
		geomAlgGWTCanvas.setWidth(canvasBreedte + "px");
		geomAlgGWTCanvas.setHeight(canvasHoogte + "px");
		geomAlgGWTCanvas.setCoordinateSpaceWidth(canvasBreedte);
		geomAlgGWTCanvas.setCoordinateSpaceHeight(canvasHoogte);
		geomAlgGWTCanvas.addStyleName("canvas");
		
		if (geomAlgGWTCanvas == null) 
		{   RootPanel.get(holderId).add(new Label(upgradeMessage));
	        return;
	    }
		
		//dlp.add(geomAlgGWTCanvas);
		
		canvasPanel.add(geomAlgGWTCanvas);
		canvasPanel.setWidgetLeftWidth(geomAlgGWTCanvas, 0, Style.Unit.PX, canvasBreedte, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(geomAlgGWTCanvas, 0, Style.Unit.PX, canvasHoogte, Style.Unit.PX);
		
		resetButton = new PushButton(resetImage);
		
		if (!constructieTools)
		{
			resetButton = new PushButton(resetImage);
			
			canvasPanel.add(resetButton);
			canvasPanel.setWidgetLeftWidth(resetButton, canvasBreedte - leftOffset - 32, Style.Unit.PX, 32, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(resetButton, canvasHoogte - topOffset - 32, Style.Unit.PX, 32, Style.Unit.PX);
		
			resetButton.addClickHandler(new PushClickHandler());
		}

	
		
		geomAlgGWTContext2d = geomAlgGWTCanvas.getContext2d();
		
		av = new AlgebraVeld(breedte, hoogte, geomAlgGWTContext2d, this);
		av.zetVarWaardeZichtbaar(varWaardeZichtbaar);
		av.zetOppWaardeZichtbaar(oppWaardeZichtbaar);
		av.zetFormuleZichtbaar(formuleZichtbaar);
		av.zetConstructieTools(constructieTools);
		av.zetAlleenOppervlaktes(alleenOppervlaktes);
		av.zetWerkBlad(werkblad);
		av.zetOppervlaktesZichtbaar(oppervlaktesZichtbaar);
		av.zetLengtesBreedtesZichtbaar(lengtesBreedtesZichtbaar);
		av.zetNegatieveWaarden(negatieveWaarden);
		av.zetPuzzelen(puzzelen);
		
		av.setSize(breedte, hoogte);
		
		Figuur.zetGeslotenVeld(!constructieTools || alleenOppervlaktes);
		Figuur.zetVeldSizes(breedte, hoogte);
		
		if (constructieTools)
		{
			lineaalVer = new LineaalVer(hoogte, geomAlgGWTContext2d);
			lineaalVer.zetNegatieveWaarden(negatieveWaarden);
			
			lineaalHor = new LineaalHor(breedte, hoogte, geomAlgGWTContext2d);
			lineaalHor.zetNegatieveWaarden(negatieveWaarden);
			
		}
		
		MouseHandler mouseHandler = new MouseHandler();
		geomAlgGWTCanvas.addMouseDownHandler(mouseHandler);
		geomAlgGWTCanvas.addMouseMoveHandler(mouseHandler);
		geomAlgGWTCanvas.addMouseUpHandler(mouseHandler);
	  
	  	TouchHandler touchHandler = new TouchHandler();
	  	geomAlgGWTCanvas.addTouchStartHandler(touchHandler);
	  	geomAlgGWTCanvas.addTouchMoveHandler(touchHandler);
	  	geomAlgGWTCanvas.addTouchEndHandler(touchHandler);
		
		makeBottom();
		
		paint();
*/
		//Stub.publish(this);
		init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());

			
	}
	
	public void makeBottom()
	{
		int currentX = leftOffset;
		int currentY = topOffset;
		
		wisButton = new PushButton("wis");
		wisButton.addStyleName(geomAlgGWTCss.pushbutton());
		bottomPanel.add(wisButton);
		bottomPanel.setWidgetLeftWidth(wisButton, currentX, Style.Unit.PX, buttonWidth1, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(wisButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

		//wisButton.addTouchStartHandler(new PushTouchStartHandler());
		
		//wisButton.addMouseDownHandler(new PushMouseDownHandler());
		wisButton.addClickHandler(new PushClickHandler());
		
		currentX += buttonWidth1 + leftOffset;
		
		terugButton = new PushButton("terug");
		terugButton.addStyleName(geomAlgGWTCss.pushbutton());
		bottomPanel.add(terugButton);
		bottomPanel.setWidgetLeftWidth(terugButton, currentX, Style.Unit.PX, buttonWidth1, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(terugButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

		//terugButton.addTouchStartHandler(new PushTouchStartHandler());
		
		//terugButton.addMouseDownHandler(new PushMouseDownHandler());
		terugButton.addClickHandler(new PushClickHandler());
		
		currentX += buttonWidth1 + leftOffset;
		
		directBox = new CheckBox();
		directBox.setText("direct samenvoegen");
		directBox.addStyleName(geomAlgGWTCss.pushbutton());
		bottomPanel.add(directBox);
				bottomPanel.setWidgetLeftWidth(directBox, currentX, Style.Unit.PX, checkBoxWidth, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(directBox, currentY + 3, Style.Unit.PX, buttonHeight, Style.Unit.PX);

		//directBox.addTouchStartHandler(new PushTouchStartHandler());
		
		//directBox.addMouseDownHandler(new PushMouseDownHandler());
		directBox.addClickHandler(new PushClickHandler());
		
		currentX += checkBoxWidth + leftOffset;
		
		xButton = new PushButton("x");
		xButton.addStyleName(geomAlgGWTCss.pushbutton());
		bottomPanel.add(xButton);
		bottomPanel.setWidgetLeftWidth(xButton, currentX, Style.Unit.PX, buttonWidth2, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(xButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

		//xButton.addTouchStartHandler(new PushTouchStartHandler());
		
		//xButton.addMouseDownHandler(new PushMouseDownHandler());
		xButton.addClickHandler(new PushClickHandler());
		
		currentX += buttonWidth2 + leftOffset;
		
		yButton = new PushButton("y");
		yButton.addStyleName(geomAlgGWTCss.pushbutton());
		bottomPanel.add(yButton);
		bottomPanel.setWidgetLeftWidth(yButton, currentX, Style.Unit.PX, buttonWidth2, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(yButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

		//yButton.addTouchStartHandler(new PushTouchStartHandler());
		
		//yButton.addMouseDownHandler(new PushMouseDownHandler());
		yButton.addClickHandler(new PushClickHandler());
		
		currentX += buttonWidth2 + leftOffset;
		
		zButton = new PushButton("z");
		zButton.addStyleName(geomAlgGWTCss.pushbutton());
		bottomPanel.add(zButton);
		bottomPanel.setWidgetLeftWidth(zButton, currentX, Style.Unit.PX, buttonWidth2, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(zButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

		//zButton.addTouchStartHandler(new PushTouchStartHandler());
		
		//zButton.addMouseDownHandler(new PushMouseDownHandler());
		zButton.addClickHandler(new PushClickHandler());
		
		currentX += buttonWidth2 + leftOffset;
		

	}
	
	public void paint()
	{
		av.paint();	
		if (lineaalVer != null)
			lineaalVer.paint();
		if (lineaalHor != null)
			lineaalHor.paint();
		
	}
	
	public void drawTestString(String testString)
	{
		geomAlgGWTContext2d.setFillStyle(CssColor.make(0, 0, 0));
		geomAlgGWTContext2d.fillText(testString, breedte / 3, testY);
		testY += 20;
	}
	
	
	public GeomAlgGWT()
	{
		//this(null, null, null);
	}

	
	public GeomAlgGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
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
		dlp.addStyleName(geomAlgGWTCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		//RootPanel.get(holderId).add(dlp);
		//RootPanel.get(holderId).addStyleName(geomAlgGWTCss.root());
		
		init(breedte, hoogte, launchState, randomVarWaarden);

/*
		if (launchState != null && launchState.get("varWaardeZichtbaar") != null)
			varWaardeZichtbaar = (Boolean) launchState.get("varWaardeZichtbaar");
		if (launchState != null && launchState.get("oppWaardeZichtbaar") != null)
			oppWaardeZichtbaar = (Boolean) launchState.get("oppWaardeZichtbaar");
		if (launchState != null && launchState.get("formuleZichtbaar") != null)
			formuleZichtbaar = (Boolean) launchState.get("formuleZichtbaar");
		if (launchState != null && launchState.get("constructieTools") != null)
			constructieTools = (Boolean) launchState.get("constructieTools");
		if (launchState != null && launchState.get("alleenOppervlaktes") != null)
			alleenOppervlaktes = (Boolean) launchState.get("alleenOppervlaktes");
		if (launchState != null && launchState.get("werkblad") != null)
			werkblad = (Boolean) launchState.get("werkblad");
		if (launchState != null && launchState.get("oppervlaktesZichtbaar") != null)
			oppervlaktesZichtbaar = (Boolean) launchState.get("oppervlaktesZichtbaar");
		if (launchState != null && launchState.get("lengtesBreedtesZichtbaar") != null)
			lengtesBreedtesZichtbaar = (Boolean) launchState.get("lengtesBreedtesZichtbaar");
		if (launchState != null && launchState.get("negatieveWaarden") != null)
			negatieveWaarden = (Boolean) launchState.get("negatieveWaarden");
		if (launchState != null && launchState.get("puzzelen") != null)
			puzzelen = (Boolean) launchState.get("puzzelen");

		HashMap<String,Object> stateHM = new HashMap<String,Object>();
		if (launchState != null && launchState.get("stateHM") != null)
			stateHM = (HashMap<String,Object>) launchState.get("stateHM");
		
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(geomAlgGWTCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		bottomPanel = new LayoutPanel();
		bottomPanel.addStyleName(geomAlgGWTCss.bottom());
		
		dlp.addSouth(bottomPanel, bottomHeight);
		
		int canvasBreedte = breedte;
		int canvasHoogte = hoogte;
		if (constructieTools)
		{	dlp.addSouth(bottomPanel, bottomHeight);
			canvasHoogte -= bottomHeight;
		}
		
		geomAlgGWTCanvas = Canvas.createIfSupported();
		geomAlgGWTCanvas.setWidth(canvasBreedte + "px");
		geomAlgGWTCanvas.setHeight(canvasHoogte + "px");
		geomAlgGWTCanvas.setCoordinateSpaceWidth(canvasBreedte);
		geomAlgGWTCanvas.setCoordinateSpaceHeight(canvasHoogte);
		geomAlgGWTCanvas.addStyleName("canvas");
		
		if (geomAlgGWTCanvas == null) 
		{   RootPanel.get(holderId).add(new Label(upgradeMessage));
	        return;
	    }
		
		dlp.add(geomAlgGWTCanvas);
		geomAlgGWTContext2d = geomAlgGWTCanvas.getContext2d();
		
		av = new AlgebraVeld(breedte, hoogte, geomAlgGWTContext2d, this);
		av.zetVarWaardeZichtbaar(varWaardeZichtbaar);
		av.zetOppWaardeZichtbaar(oppWaardeZichtbaar);
		av.zetFormuleZichtbaar(formuleZichtbaar);
		av.zetConstructieTools(constructieTools);
		av.zetAlleenOppervlaktes(alleenOppervlaktes);
		av.zetWerkBlad(werkblad);
		av.zetOppervlaktesZichtbaar(oppervlaktesZichtbaar);
		av.zetLengtesBreedtesZichtbaar(lengtesBreedtesZichtbaar);
		av.zetNegatieveWaarden(negatieveWaarden);
		av.zetPuzzelen(puzzelen);
		
		av.setSize(breedte, hoogte);
		
		Figuur.zetGeslotenVeld(!constructieTools || alleenOppervlaktes);
		Figuur.zetVeldSizes(breedte, hoogte);
		
		if (constructieTools)
		{
			lineaalVer = new LineaalVer(hoogte, geomAlgGWTContext2d);
			lineaalVer.zetNegatieveWaarden(negatieveWaarden);
			
			lineaalHor = new LineaalHor(breedte, hoogte, geomAlgGWTContext2d);
			lineaalHor.zetNegatieveWaarden(negatieveWaarden);
			
		}
		
		State state = NoSer.setStateState(stateHM);
		av.setState(state);		
		av.docentState = new State(av.aantalFg, av.fg, av.var);
		
		MouseHandler mouseHandler = new MouseHandler();
		geomAlgGWTCanvas.addMouseDownHandler(mouseHandler);
		geomAlgGWTCanvas.addMouseMoveHandler(mouseHandler);
		geomAlgGWTCanvas.addMouseUpHandler(mouseHandler);
	  
	  	TouchHandler touchHandler = new TouchHandler();
	  	geomAlgGWTCanvas.addTouchStartHandler(touchHandler);
	  	geomAlgGWTCanvas.addTouchMoveHandler(touchHandler);
	  	geomAlgGWTCanvas.addTouchEndHandler(touchHandler);
		
		makeBottom();
		
		paint();
*/		

	}
	
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		if (lineaalVer != null)
		{	boolean changeBasis = lineaalVer.mouseDownTouchStartAction(eventX, eventY);
			if (changeBasis)
			{	av.zetBasis(lineaalVer.huidigeWaarde);
			}
		}
		if (lineaalHor != null)
		{	boolean changeBasis = lineaalHor.mouseDownTouchStartAction(eventX, eventY);
			if (changeBasis)
			{	av.zetBasis(lineaalHor.huidigeWaarde);
			}
		}

		av.mouseDownTouchStartAction(eventX, eventY);
	
	}

	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		av.mouseMoveTouchMoveAction(eventX, eventY);
	}
	
	public void mouseUpTouchEndAction(int eventX, int eventY)
	{	
		if (eventX == -10000) //touch
		{	av.mouseUpTouchEndAction(av.laatstex, av.laatstey);
		}	
		else // mouse
		{	av.mouseUpTouchEndAction(eventX, eventY);
		}
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
			
			mouseDownTouchStartAction(eventX, eventY);
			
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

			mouseMoveTouchMoveAction(eventX, eventY);
			
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;

			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseUpTouchEndAction(eventX, eventY);

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
				
				int eventX = touch.getPageX() - geomAlgGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - geomAlgGWTCanvas.getAbsoluteTop();				
				
				mouseDownTouchStartAction(eventX, eventY);
				
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
				
			    int eventX = touch.getPageX() - geomAlgGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - geomAlgGWTCanvas.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			//de Touch die beeindigd werd zit niet in e.getTouches() 
			//if (e.getTouches().length() > 0)
			//{
				//Touch touch = e.getTouches().get(0);
				
			    int eventX = -10000;
				int eventY = 0;				
			    
				mouseUpTouchEndAction(eventX, eventY);
				
		    //}
		}

	}

	//class PushMouseDownHandler implements MouseDownHandler
	class PushClickHandler implements ClickHandler
	{
		//public void onMouseDown(MouseDownEvent e)
		public void onClick(ClickEvent e)
		{
			if (touchStart)
				return;
	
	  		// DIT NIET toevoegen!!
    		//e.preventDefault();
			e.stopPropagation();
    			
			if (e.getSource() == wisButton)
			{
				av.wis();
			}
			else if (e.getSource() == terugButton)
			{
				
//drawTestString("mouse down terug " + System.currentTimeMillis() + "\n");

				av.maakOngedaan();
			} 
			if (e.getSource() == directBox)
			{
				boolean checked = directBox.getValue();
				av.zetDirectOptellen(checked);
			}
			if (e.getSource() == xButton)
			{
				av.zetVarBasis(1);
			}
			if (e.getSource() == yButton)
			{
				av.zetVarBasis(2);
			}
			if (e.getSource() == zButton)
			{
				av.zetVarBasis(3);
			}
			if (e.getSource() == resetButton)
			{
				av.reset();
			}
			
		}
	}

/*	
	class PushTouchStartHandler implements TouchStartHandler
	{
	
		public void onTouchStart(TouchStartEvent e)
		{
			touchStart = true;
			
	  		// DIT NIET toevoegen!!
    		//e.preventDefault();
			e.stopPropagation();
    			
			
			if (e.getSource() == wisButton)
			{
				av.wis();
			}
			else if (e.getSource() == terugButton)
			{
				
//drawTestString("touch start terug " + System.currentTimeMillis() + "\n");

				av.maakOngedaan();
			} 
			if (e.getSource() == directBox)
			{
				boolean checked = directBox.getValue();
				av.zetDirectOptellen(checked);
			}
			if (e.getSource() == xButton)
			{
				av.zetVarBasis(1);
			}
			if (e.getSource() == yButton)
			{
				av.zetVarBasis(2);
			}
			if (e.getSource() == zButton)
			{
				av.zetVarBasis(3);
			}
			
		}
	}
*/
	public Widget asWidget()
	{
		return dlp;
	}
	
	public HashMap<String, Object> getState()
	{
		State state = av.getStateState();
		HashMap stateHM = (HashMap) NoSer.getStateState(state);
		return stateHM;
	}

	public void setState(HashMap<String, Object> h)
	{
		State state = NoSer.setStateState(h);
		av.setState(state);
		paint();
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
		return Boolean.TRUE;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		// TODO Auto-generated method stub

	}

	
	
	@Override
	public void init(int width, int height, Map<String, Object> map, //launchState,
					 Map<String, Number> values) 
	{
		
logger.info("GeomAlgGWT init");

		this.breedte = width;
		this.hoogte = height;
		
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		
		//this.launchState = launchState;
		ObjectMap launchState = JSONUtilities.wrapMap(map);
		
		if (launchState.containsKey("varWaardeZichtbaar"))
			varWaardeZichtbaar = launchState.getBoolean("varWaardeZichtbaar");
		if (launchState.containsKey("oppWaardeZichtbaar"))
			oppWaardeZichtbaar = launchState.getBoolean("oppWaardeZichtbaar");
		if (launchState.containsKey("formuleZichtbaar"))
			formuleZichtbaar = launchState.getBoolean("formuleZichtbaar");
		if (launchState.containsKey("constructieTools"))
			constructieTools = launchState.getBoolean("constructieTools");
		if (launchState.containsKey("alleenOppervlaktes"))
			alleenOppervlaktes = launchState.getBoolean("alleenOppervlaktes");
		if (launchState.containsKey("werkblad"))
			werkblad = launchState.getBoolean("werkblad");
		if (launchState.containsKey("oppervlaktesZichtbaar"))
			oppervlaktesZichtbaar = launchState.getBoolean("oppervlaktesZichtbaar");
		if (launchState.containsKey("lengtesBreedtesZichtbaar"))
			lengtesBreedtesZichtbaar = launchState.getBoolean("lengtesBreedtesZichtbaar");
		if (launchState.containsKey("negatieveWaarden"))
			negatieveWaarden = launchState.getBoolean("negatieveWaarden");
		if (launchState.containsKey("puzzelen"))
			puzzelen = launchState.getBoolean("puzzelen");

		Map<String,Object> stateHM = new HashMap<String,Object>();
		if (launchState.containsKey("stateHM"))
		{	stateHM = launchState.getMap("stateHM");
//logger.info("stateHM found " + stateHM.isEmpty());		
		}
		else
		{
//logger.info("stateHM !found");
		}
		
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");
		
		bottomPanel = new LayoutPanel();
		bottomPanel.addStyleName(geomAlgGWTCss.bottom());
		
		//dlp.addSouth(bottomPanel, bottomHeight);
		
		int canvasBreedte = breedte;
		int canvasHoogte = hoogte;
		if (constructieTools)
		{	dlp.addSouth(bottomPanel, bottomHeight);
			canvasHoogte -= bottomHeight;
		}
		
		canvasPanel = new LayoutPanel();
		dlp.add(canvasPanel);
		
		geomAlgGWTCanvas = Canvas.createIfSupported();
		geomAlgGWTCanvas.setWidth(canvasBreedte + "px");
		geomAlgGWTCanvas.setHeight(canvasHoogte + "px");
		geomAlgGWTCanvas.setCoordinateSpaceWidth(canvasBreedte);
		geomAlgGWTCanvas.setCoordinateSpaceHeight(canvasHoogte);
		geomAlgGWTCanvas.addStyleName("canvas");
		
		if (geomAlgGWTCanvas == null) 
		{   RootPanel.get(holderId).add(new Label(upgradeMessage));
	        return;
	    }
		
		canvasPanel.add(geomAlgGWTCanvas);
		canvasPanel.setWidgetLeftWidth(geomAlgGWTCanvas, 0, Style.Unit.PX, canvasBreedte, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(geomAlgGWTCanvas, 0, Style.Unit.PX, canvasHoogte, Style.Unit.PX);
		
		resetButton = new PushButton(resetImage);
		
		if (!constructieTools)
		{
			resetButton = new PushButton(resetImage);
			
			canvasPanel.add(resetButton);
			canvasPanel.setWidgetLeftWidth(resetButton, canvasBreedte - leftOffset - 32, Style.Unit.PX, 32, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(resetButton, canvasHoogte - topOffset - 32, Style.Unit.PX, 32, Style.Unit.PX);
		
			resetButton.addClickHandler(new PushClickHandler());
		}
		
//		dlp.add(geomAlgGWTCanvas);
		geomAlgGWTContext2d = geomAlgGWTCanvas.getContext2d();
		
		av = new AlgebraVeld(breedte, hoogte, geomAlgGWTContext2d, this);
		av.zetVarWaardeZichtbaar(varWaardeZichtbaar);
		av.zetOppWaardeZichtbaar(oppWaardeZichtbaar);
		av.zetFormuleZichtbaar(formuleZichtbaar);
		av.zetConstructieTools(constructieTools);
		av.zetAlleenOppervlaktes(alleenOppervlaktes);
		av.zetWerkBlad(werkblad);
		av.zetOppervlaktesZichtbaar(oppervlaktesZichtbaar);
		av.zetLengtesBreedtesZichtbaar(lengtesBreedtesZichtbaar);
		av.zetNegatieveWaarden(negatieveWaarden);
		av.zetPuzzelen(puzzelen);
		
		av.setSize(breedte, hoogte);

//av.testString = testString;
//av.testStrings = testStrings;
		
		Figuur.zetGeslotenVeld(!constructieTools || alleenOppervlaktes);
		Figuur.zetVeldSizes(breedte, hoogte);
		
		if (constructieTools)
		{
			lineaalVer = new LineaalVer(hoogte, geomAlgGWTContext2d);
			lineaalVer.zetNegatieveWaarden(negatieveWaarden);
			
			lineaalHor = new LineaalHor(breedte, hoogte, geomAlgGWTContext2d);
			lineaalHor.zetNegatieveWaarden(negatieveWaarden);
			
		}
		
		State state = NoSer.setStateState(stateHM);
//if (state == null)		
//	logger.info("state null");
//else
//	logger.info("state not null");

		av.setState(state);		
		av.docentState = new State(av.aantalFg, av.fg, av.var);
		
		MouseHandler mouseHandler = new MouseHandler();
		geomAlgGWTCanvas.addMouseDownHandler(mouseHandler);
		geomAlgGWTCanvas.addMouseMoveHandler(mouseHandler);
		geomAlgGWTCanvas.addMouseUpHandler(mouseHandler);
	  
	  	TouchHandler touchHandler = new TouchHandler();
	  	geomAlgGWTCanvas.addTouchStartHandler(touchHandler);
	  	geomAlgGWTCanvas.addTouchMoveHandler(touchHandler);
	  	geomAlgGWTCanvas.addTouchEndHandler(touchHandler);
		
		makeBottom();
		
		paint();

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

class Rectangle
{
	int x; int y; int width; int height;
	
	public Rectangle(int x, int y, int w, int h)
	{
		this.x = x; this.y = y;
		width = w; height = h;
	}
	
	public Rectangle(Rectangle r)
	{
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
	}
	
	public boolean contains(int px, int py)
	{
		return (px >= x) && (px <= (x + width)) &&
		       (py >= y) && (py <= (y + height));
	}
	
	public void translate(int dx, int dy)
	{
		x += dx;
		y += dy;
	}
}