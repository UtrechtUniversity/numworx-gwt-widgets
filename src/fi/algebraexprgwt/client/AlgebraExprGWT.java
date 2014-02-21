package fi.algebraexprgwt.client;

import java.util.HashMap;
import java.util.Map;

//import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;


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
import com.google.gwt.user.client.ui.RootPanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.resources.client.ImageResource;


public class AlgebraExprGWT implements EntryPoint, InteractionStub 
{
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	// UI
	DockLayoutPanel dlp;
	LayoutPanel canvasPanel;
	
	Canvas algebraExprGWTCanvas;
	Context2d algebraExprGWTContext2d;
	AlgebraSchuifVeld asv;

	CheckBox expressieBox, waardeBox;
	PushButton wisButton;
	CheckBox tabelBox, grafiekBox;
	
	int toggleSize = 22;
	int buttonWidth = 40;
	int buttonHeight = 22;
	int checkBoxWidth = 80;

	int breedte = 500;
	int hoogte = 450;
	int bottomHeight = 32;
	int leftOffset = 5;
	int topOffset = 5;
	
	CssColor bottomBgColor = CssColor.make(220, 220, 220);	
	
	AlgebraExprGWTClientBundle algebraExprGWTClientBundle;
	AlgebraExprGWTCssResource algebraExprGWTCss;
	
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	static boolean touchStart = false;
	
	int lastStartX, lastStartY, lastMoveX, lastMoveY;

	// parametrisatie	
	boolean toolkit = true;
	boolean alleenInvullen = false;
	boolean isDemo = false;
	
	boolean brugklas = false;
	boolean tabelOptie = true;	
	boolean grafiekOptie = true;
	
	boolean scrollOptie = true;
	boolean zoomOptie = true; 

	boolean standAlone = false;
	
	public void getImages() 
	{
		algebraExprGWTClientBundle = GWT.create(AlgebraExprGWTClientBundle.class);
		algebraExprGWTCss = algebraExprGWTClientBundle.getAlgebraExprGWTCSS();
		algebraExprGWTCss.ensureInjected();
	}
	
	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(algebraExprGWTCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(algebraExprGWTCss.root());

		standAlone = true;
		
		//Stub.publish(this);
		init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
	
	}
	
	public AlgebraExprGWT()
	{
		//this(null, null, null);
	}
	
	public AlgebraExprGWT(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		if (h != null && h.get("breedte") != null)
			breedte = (Integer) h.get("breedte");
		if (h != null && h.get("hoogte") != null)
			hoogte = (Integer) h.get("hoogte");
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");

		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		init(breedte, hoogte, launchState, randomVarWaarden);

	}

	public void	makeLeft()
	{
		if (!toolkit || isDemo || alleenInvullen)
			return;
		
		int currentX = (asv.toolsWidth - checkBoxWidth) / 2;
		int currentY = topOffset + 280;
		if (brugklas)
			currentY = topOffset + 195;
		
		if (grafiekOptie)
		{	
			grafiekBox = new CheckBox();
			grafiekBox.setText("grafiek");
			canvasPanel.add(grafiekBox);
			canvasPanel.setWidgetLeftWidth(grafiekBox, currentX, Style.Unit.PX, checkBoxWidth - 12, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(grafiekBox, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
			grafiekBox.addClickHandler(new PushClickHandler());
		
			currentY += buttonHeight + topOffset;
		}
		
		currentX = (asv.toolsWidth - buttonWidth) / 2; //leftOffset;
		
		
		wisButton = new PushButton("wis");
		wisButton.addStyleName(algebraExprGWTCss.pushbutton());
		canvasPanel.add(wisButton);
		canvasPanel.setWidgetLeftWidth(wisButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(wisButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
		wisButton.addClickHandler(new PushClickHandler());
		
		currentY += buttonHeight + topOffset;
		
		currentX = (asv.toolsWidth - checkBoxWidth) / 2;
		
		expressieBox = new CheckBox();
		expressieBox.setText("expressie");
		canvasPanel.add(expressieBox);
		canvasPanel.setWidgetLeftWidth(expressieBox, currentX, Style.Unit.PX, checkBoxWidth, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(expressieBox, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		expressieBox.setValue(true);
		
		expressieBox.addClickHandler(new PushClickHandler());
		
		currentY += buttonHeight + topOffset;
		
		waardeBox = new CheckBox();
		waardeBox.setText("waarde");
		canvasPanel.add(waardeBox);
		canvasPanel.setWidgetLeftWidth(waardeBox, currentX, Style.Unit.PX, checkBoxWidth - 12, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(waardeBox, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
		waardeBox.addClickHandler(new PushClickHandler());
		
		currentY += buttonHeight + topOffset;

	}

    class PushClickHandler implements ClickHandler
    {
    	
    	public void onClick(ClickEvent e)
    	{
			//e.preventDefault();
			e.stopPropagation();
    		
			if (e.getSource() == wisButton)
			{
				asv.wisAction();
			}
			else if (e.getSource() == grafiekBox)
			{
				boolean checked = grafiekBox.getValue();
				asv.toonGrafiekComponent(checked);
				asv.zetVeranderd();

			}
			else if (e.getSource() == expressieBox)
			{
				boolean checked = expressieBox.getValue();
				waardeBox.setValue(!checked);
				asv.toonWaarde = !checked;
				asv.zetVeranderd();

			}
			else if (e.getSource() == waardeBox)
			{
				boolean checked = waardeBox.getValue();
				expressieBox.setValue(!checked);
				asv.toonWaarde = checked;
				asv.zetVeranderd();
			}
    		
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
			touchStart = true;
			
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				int eventX = touch.getPageX() - algebraExprGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - algebraExprGWTCanvas.getAbsoluteTop();			
				
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
				
			    int eventX = touch.getPageX() - algebraExprGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - algebraExprGWTCanvas.getAbsoluteTop();		
				
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
				//Touch touch = e.getTouches().get(0);
				
			    //int eventX = touch.getPageX() - algebraExprGWTCanvas.getAbsoluteLeft();
				//int eventY = touch.getPageY() - algebraExprGWTCanvas.getAbsoluteTop();				

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
	public boolean isCorrect()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		// TODO Auto-generated method stub

	}
	
	@Override
	public void init(int width, int height, Map<String, Object> launchState,
					 Map<String, Number> values) 
	{
		this.breedte = width;
		this.hoogte = height;
		this.launchState = launchState;
		
		if (launchState != null && launchState.get("toolkit") != null)
			toolkit = ((Boolean) launchState.get("toolkit")).booleanValue();
		
		if (launchState != null && launchState.get("alleenInvullen") != null)
			alleenInvullen = ((Boolean) launchState.get("alleenInvullen")).booleanValue();
		
		if (launchState != null && launchState.get("isDemo") != null)
			isDemo = ((Boolean) launchState.get("isDemo")).booleanValue();

		if (launchState != null && launchState.get("brugklas") != null)
			brugklas = ((Boolean) launchState.get("brugklas")).booleanValue();
		
		if (launchState != null && launchState.get("tabelOptie") != null)
			tabelOptie = ((Boolean) launchState.get("tabelOptie")).booleanValue();
		
		if (launchState != null && launchState.get("grafiekOptie") != null)
			grafiekOptie = ((Boolean) launchState.get("grafiekOptie")).booleanValue();

		canvasPanel = new LayoutPanel();
		dlp.add(canvasPanel);
		
		int canvasBreedte = breedte;
		int canvasHoogte = hoogte;
		algebraExprGWTCanvas = Canvas.createIfSupported();
		algebraExprGWTCanvas.setWidth(canvasBreedte + "px");
		algebraExprGWTCanvas.setHeight(canvasHoogte + "px");
		algebraExprGWTCanvas.setCoordinateSpaceWidth(canvasBreedte);
		algebraExprGWTCanvas.setCoordinateSpaceHeight(canvasHoogte);
		algebraExprGWTCanvas.addStyleName("canvas");
		
		if (algebraExprGWTCanvas == null) 
		{   RootPanel.get(holderId).add(new Label(upgradeMessage));
	        return;
	    }
		
		algebraExprGWTCanvas.addStyleName(algebraExprGWTCss.canvas());
		
		MouseHandler mouseHandler = new MouseHandler();
		algebraExprGWTCanvas.addMouseDownHandler(mouseHandler);
		algebraExprGWTCanvas.addMouseMoveHandler(mouseHandler);
		algebraExprGWTCanvas.addMouseUpHandler(mouseHandler);
	  
	  	TouchHandler touchHandler = new TouchHandler();
	  	algebraExprGWTCanvas.addTouchStartHandler(touchHandler);
	  	algebraExprGWTCanvas.addTouchMoveHandler(touchHandler);
	  	algebraExprGWTCanvas.addTouchEndHandler(touchHandler);
		
		canvasPanel.add(algebraExprGWTCanvas);
		canvasPanel.setWidgetLeftWidth(algebraExprGWTCanvas, 0, Style.Unit.PX, canvasBreedte, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(algebraExprGWTCanvas, 0, Style.Unit.PX, canvasHoogte, Style.Unit.PX);

		//makeLeft();
		
		algebraExprGWTContext2d = algebraExprGWTCanvas.getContext2d();

		asv = new AlgebraSchuifVeld(0, 0, breedte, hoogte, algebraExprGWTContext2d, this);

		makeLeft();
		
		if (!standAlone && (launchState != null))
			asv.setState(launchState);
		
		asv.paint();


		
	}	
}
