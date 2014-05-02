package fi.draaibankgwt.client;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
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
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.resources.client.ImageResource;


public class DraaibankGWT implements EntryPoint, InteractionView 
{
    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	// UI
	DockLayoutPanel dlp;
	LayoutPanel canvasPanel;
	Canvas draaibankGWTCanvas;
	TekenPanel tekenPanel;
	
	int buttonWidth = 40;
	int buttonHeight = 22;
	int pushSize = 24;

	int breedte = 500;
	int hoogte = 450;
	int bottomHeight = 32;
	int leftOffset = 5;
	int topOffset = 5;
	
	PushButton terugButton, wisButton;
	PushButton vergrootButton, verkleinButton;
	
//	CssColor bottomBgColor = CssColor.make(220, 220, 220);	
	
	DraaibankGWTClientBundle draaibankGWTClientBundle;
	ImageResource vergrootResource, verkleinResource;
	Image vergrootImage, verkleinImage;
	
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	// parametrisatie
	boolean demoVersion = false;
	boolean zoomOption = true; //false;
	
	public void getImages()
	{
		draaibankGWTClientBundle = GWT.create(DraaibankGWTClientBundle.class);
		
		vergrootResource = draaibankGWTClientBundle.vergrootResource();
		vergrootImage = new Image(vergrootResource);
		vergrootImage.addStyleName("pushimage");
		
		verkleinResource = draaibankGWTClientBundle.verkleinResource();
		verkleinImage = new Image(verkleinResource);
		verkleinImage.addStyleName("pushimage");
	}
	
	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName("root");
		
		//Stub.publish(this);
		init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());
				
	}
	
	public DraaibankGWT()
	{

	}
	
	public DraaibankGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
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
		dlp.addStyleName("dock");
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		
		init(breedte, hoogte, launchState, randomVarWaarden);


	}

	public void	makeBottom()
	{
		if (demoVersion)
			return;
		
		int currentX = 185;
		int currentY = hoogte - topOffset - buttonHeight; 
		
		terugButton = new PushButton("terug");
		terugButton.addStyleName("pushbutton");
		canvasPanel.add(terugButton);
		canvasPanel.setWidgetLeftWidth(terugButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(terugButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
		terugButton.addClickHandler(new PushClickHandler());
		
		currentX += buttonWidth + 3 * leftOffset;		

		wisButton = new PushButton("wis");
		wisButton.addStyleName("pushbutton");
		canvasPanel.add(wisButton);
		canvasPanel.setWidgetLeftWidth(wisButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(wisButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
		wisButton.addClickHandler(new PushClickHandler());

		currentX += buttonWidth + 3 * leftOffset;		
		
		if (zoomOption)
		{
			currentY = hoogte - topOffset - pushSize;
			
			vergrootButton = new PushButton(vergrootImage);
			vergrootButton.addStyleName("pushbutton");
			canvasPanel.add(vergrootButton);
			canvasPanel.setWidgetLeftWidth(vergrootButton, currentX, Style.Unit.PX, pushSize, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(vergrootButton, currentY, Style.Unit.PX, pushSize, Style.Unit.PX);
		
			vergrootButton.addClickHandler(new PushClickHandler());
		
			currentX += pushSize + 3 * leftOffset;

			verkleinButton = new PushButton(verkleinImage);
			verkleinButton.addStyleName("pushbutton");
			canvasPanel.add(verkleinButton);
			canvasPanel.setWidgetLeftWidth(verkleinButton, currentX, Style.Unit.PX, pushSize, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(verkleinButton, currentY, Style.Unit.PX, pushSize, Style.Unit.PX);
		
			verkleinButton.addClickHandler(new PushClickHandler());
		
			currentX += pushSize + 3 * leftOffset;
		}
	}

    class PushClickHandler implements ClickHandler
    {
    	
    	public void onClick(ClickEvent e)
    	{
			//e.preventDefault();
			e.stopPropagation();
			
			if (e.getSource() == terugButton)
    		{
    			tekenPanel.tekenStapTerug();
    			    			
    		}
    		else if (e.getSource() == wisButton)
    		{
    			tekenPanel.wis();
    		}
    		else if (e.getSource() == vergrootButton)
    		{
    			tekenPanel.zoomIn();
    		}
    		else if (e.getSource() == verkleinButton)
    		{
    			tekenPanel.zoomUit();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		// TODO Auto-generated method stub

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
	
	public void init(int width, int height, Map<String, Object> map, //launchState,
			Map<String, Number> values) 
	{
		
// launchdata/state		
		
		this.breedte = width;
		this.hoogte = height;
		//this.launchState = launchState;
		ObjectMap launchState = JSONUtilities.wrapMap(map);
		
		if (launchState.containsKey("demoVersion"))
			demoVersion = launchState.getBoolean("demoVersion");
		if (launchState.containsKey("zoomOption"))
			zoomOption = launchState.getBoolean("zoomOption");
		
		dlp.setSize(breedte + "px", hoogte + "px");
		
		canvasPanel = new LayoutPanel();
		dlp.add(canvasPanel);

		tekenPanel = new TekenPanel(breedte, hoogte);
		
		if (tekenPanel.draaibankGWTCanvas == null) 
		{
	      RootPanel.get(holderId).add(new Label(upgradeMessage));
	      return;
	    }
		
		tekenPanel.demoVersion = demoVersion;
		
		tekenPanel.draaibankGWTCanvas.addStyleName("canvas");
		
		canvasPanel.add(tekenPanel.draaibankGWTCanvas);
		canvasPanel.setWidgetLeftWidth(tekenPanel.draaibankGWTCanvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(tekenPanel.draaibankGWTCanvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);

		
		makeBottom();
		tekenPanel.setState(map);

		tekenPanel.tekenOpnieuw();

	}	
}
