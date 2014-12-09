package fi.algebrapijlengwt.client;

import java.util.HashMap;
import java.util.Map;

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


public class AlgebraPijlenGWT implements EntryPoint, InteractionStub //InteractionView 
{
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
	
	boolean standAlone = false;

	public void getImages() 
	{
		algebraPijlenGWTClientBundle = GWT.create(AlgebraPijlenGWTClientBundle.class);
		algebraPijlenGWTCss = algebraPijlenGWTClientBundle.getAlgebraPijlenGWTCSS();
		algebraPijlenGWTCss.ensureInjected();
		
//System.out.println("getImages");		
	}
	
	
	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(algebraPijlenGWTCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(algebraPijlenGWTCss.root());
	
		standAlone = true;
		
		Stub.publish(this);
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
			
	}


	private void initCanvas() {
		canvasPanel = new LayoutPanel();
		dlp.add(canvasPanel);
		
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
		
		asv.paint();
		
		makeLeft();
	
	}
	
	public void makeLeft()
	{
		if (!toolkit || isDemo || alleenInvullen)
			return;
		
		int currentX = (asv.toolsWidth - buttonWidth) / 2; //leftOffset;
		int currentY = topOffset + 270;
		if (brugklas)
			currentY = topOffset + 195;
		
		linksRechtsButton = new ToggleButton("links", "rechts");
		linksRechtsButton.addStyleName(algebraPijlenGWTCss.togglebutton());
		if (terugHeen)
		{	
			canvasPanel.add(linksRechtsButton);
			canvasPanel.setWidgetLeftWidth(linksRechtsButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(linksRechtsButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
			linksRechtsButton.addClickHandler(new PushClickHandler());
		
			currentY += buttonHeight + topOffset;
		}
		
		currentX = (asv.toolsWidth - checkBoxWidth) / 2; //leftOffset;
		
		tabelBox = new CheckBox();
		tabelBox.setText("tabel");
		tabelBox.addStyleName(algebraPijlenGWTCss.checkbox());
		if (tabelOptie)
		{	
			canvasPanel.add(tabelBox);
			canvasPanel.setWidgetLeftWidth(tabelBox, currentX, Style.Unit.PX, checkBoxWidth - 10, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(tabelBox, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
			tabelBox.addClickHandler(new PushClickHandler());
		
			currentY += buttonHeight + topOffset;
		}
		
		currentX = (asv.toolsWidth - checkBoxWidth) / 2; //leftOffset;
		
		grafiekBox = new CheckBox();
		grafiekBox.setText("grafiek");
		grafiekBox.addStyleName(algebraPijlenGWTCss.checkbox());
		if (grafiekOptie)
		{	
			canvasPanel.add(grafiekBox);
			canvasPanel.setWidgetLeftWidth(grafiekBox, currentX, Style.Unit.PX, checkBoxWidth, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(grafiekBox, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
			grafiekBox.addClickHandler(new PushClickHandler());
		
			currentY += buttonHeight + topOffset;
		}
		
		currentX = (asv.toolsWidth - buttonWidth) / 2; //leftOffset;
		
		wisButton = new PushButton("wis");
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
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(algebraPijlenGWTCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		//RootPanel.get(holderId).add(dlp);
		//RootPanel.get(holderId).addStyleName(algebraPijlenHWTCss.root());
	
		init(breedte, hoogte, launchState, randomVarWaarden);


	}

	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{   
		boolean mouseDown = false;
		
		public void onMouseDown(MouseDownEvent e)
		{
			//e.preventDefault();
			
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDown = true;
			
			asv.mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		public void onMouseMove(MouseMoveEvent e)	
		{
			//e.preventDefault();
			
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
			//e.preventDefault();
			
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

		}	
	}
		

	public Widget asWidget()
	{
		return dlp;
	}
	
	@Override
	public HashMap<String, Object> getState()
	{
		return asv.getState();
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		asv.setState(h);

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

	
	@Override
	public void init(int width, int height, Map<String, Object> map, //launchState,
					 Map<String, Number> values) 
	{
		this.breedte = width;
		this.hoogte = height;
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
		
		
		canvasPanel = new LayoutPanel();
		dlp.add(canvasPanel);
		
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
		
		//asv.paint();
		
		makeLeft();
		
		if (!standAlone && (map != null))
		{	asv.setState(map);
		}
		
		asv.paint();
		

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

