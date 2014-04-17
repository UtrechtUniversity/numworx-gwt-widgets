package fi.kladjegwt.client;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;


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

public class KladjeGWT implements EntryPoint, InteractionStub 
{
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	// UI
	DockLayoutPanel dlp;
	LayoutPanel bottomPanel;
	KladjeGWTVeld kladjeGWTVeld;
	Canvas kladjeGWTCanvas;
	ToggleButton tekenButton, gumButton, tekenLijnButton, tekenRechthoekButton, tekenCirkelButton,
    			 tekenTekstButton, selecterenButton;
	PushButton terugButton, wisButton;
	PushButton roteerLinksomButton, roteerRechtsomButton, vergrootButton, verkleinButton;
	PushButton kleurkeuzeButton;
	ColorPopup colorPopup;
	
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
	
	// images
	KladjeGWTClientBundle kladjeGWTClientBundle;
	static KladjeCssResource kladjeCss;
	ImageResource tekenKnopUpResource, tekenKnopDownResource, gumKnopUpResource, gumKnopDownResource,
	  			  tekenLijnUpResource, tekenLijnDownResource, tekenRechthoekUpResource, tekenRechthoekDownResource, 
	              tekenCirkelUpResource, tekenCirkelDownResource, tekenTekstUpResource, tekenTekstDownResource, 
	              selecterenUpResource, selecterenDownResource, 
	              roteerLinksomResource, roteerRechtsomResource, vergrootResource, verkleinResource,
	              regenboogResource, 
	              zwartResource, zwart2Resource, grijsResource, grijs2Resource, roodResource, rood2Resource, 
	              oranjeResource, oranje2Resource, groenResource, groen2Resource, cyaanResource, cyaan2Resource, 
	              blauwResource, blauw2Resource, magentaResource, magenta2Resource, 
	              geelResource;
	Image tekenKnopUpImage, tekenKnopDownImage, gumKnopUpImage, gumKnopDownImage, tekenLijnUpImage, tekenLijnDownImage, 
		  tekenRechthoekUpImage, tekenRechthoekDownImage, tekenCirkelUpImage, tekenCirkelDownImage, 
		  tekenTekstUpImage, tekenTekstDownImage, selecterenUpImage, selecterenDownImage,
		  roteerLinksomImage, roteerRechtsomImage, vergrootImage, verkleinImage,
		  regenboogImage, 
		  zwartImage, zwart2Image, grijsImage, grijs2Image, roodImage, rood2Image, oranjeImage, oranje2Image, 
		  groenImage, groen2Image, cyaanImage, cyaan2Image, blauwImage, blauw2Image, magentaImage, magenta2Image, 
		  geelImage;
	
	// instelbaarheid
	boolean kleurkeuze = true;
	
	boolean lijnen = false;
	boolean ruitjes = false;
	boolean lijnTekenen = true;
	boolean rechthoekTekenen = true;
	boolean cirkelTekenen = true;
	boolean tekstTekenen = true;

	boolean roteren = true;
	boolean schalen = true;
	
	boolean touchStart = false;

	public void getImages() 
	{
		kladjeGWTClientBundle = GWT.create(KladjeGWTClientBundle.class);
		kladjeCss = kladjeGWTClientBundle.getKladjeGWTCSS();
		kladjeCss.ensureInjected();
		
		tekenKnopUpResource = kladjeGWTClientBundle.tekenKnopUpResource();
		tekenKnopDownResource = kladjeGWTClientBundle.tekenKnopDownResource();
		tekenKnopUpImage = new Image(tekenKnopUpResource);
		tekenKnopDownImage = new Image(tekenKnopDownResource);
		tekenKnopUpImage.addStyleName(kladjeCss.upimage());
		tekenKnopDownImage.addStyleName(kladjeCss.downimage());
		
		gumKnopUpResource = kladjeGWTClientBundle.gumKnopUpResource();
		gumKnopDownResource = kladjeGWTClientBundle.gumKnopDownResource();
		gumKnopUpImage = new Image(gumKnopUpResource);
		gumKnopDownImage = new Image(gumKnopDownResource);
		gumKnopUpImage.addStyleName(kladjeCss.upimage());
		gumKnopDownImage.addStyleName(kladjeCss.downimage());
		
		tekenLijnUpResource = kladjeGWTClientBundle.tekenLijnUpResource();
		tekenLijnDownResource = kladjeGWTClientBundle.tekenLijnDownResource();
		tekenLijnUpImage = new Image(tekenLijnUpResource);
		tekenLijnDownImage = new Image(tekenLijnDownResource);
		tekenLijnUpImage.addStyleName(kladjeCss.upimage());
		tekenLijnDownImage.addStyleName(kladjeCss.downimage());

		tekenRechthoekUpResource = kladjeGWTClientBundle.tekenRechthoekUpResource();
		tekenRechthoekDownResource = kladjeGWTClientBundle.tekenRechthoekDownResource();
		tekenRechthoekUpImage = new Image(tekenRechthoekUpResource);
		tekenRechthoekDownImage = new Image(tekenRechthoekDownResource);
		tekenRechthoekUpImage.addStyleName(kladjeCss.upimage());
		tekenRechthoekDownImage.addStyleName(kladjeCss.downimage());

		tekenCirkelUpResource = kladjeGWTClientBundle.tekenCirkelUpResource();
		tekenCirkelDownResource = kladjeGWTClientBundle.tekenCirkelDownResource();
		tekenCirkelUpImage = new Image(tekenCirkelUpResource);
		tekenCirkelDownImage = new Image(tekenCirkelDownResource);
		tekenCirkelUpImage.addStyleName(kladjeCss.upimage());
		tekenCirkelDownImage.addStyleName(kladjeCss.downimage());
		
		tekenTekstUpResource = kladjeGWTClientBundle.tekenTekstUpResource();
		tekenTekstDownResource = kladjeGWTClientBundle.tekenTekstDownResource();
		tekenTekstUpImage = new Image(tekenTekstUpResource);
		tekenTekstDownImage = new Image(tekenTekstDownResource);
		tekenTekstUpImage.addStyleName(kladjeCss.upimage());
		tekenTekstDownImage.addStyleName(kladjeCss.downimage());

		selecterenUpResource = kladjeGWTClientBundle.selecterenUpResource();
		selecterenDownResource = kladjeGWTClientBundle.selecterenDownResource();
		selecterenUpImage = new Image(selecterenUpResource);
		selecterenDownImage = new Image(selecterenDownResource);
		selecterenUpImage.addStyleName(kladjeCss.upimage());
		selecterenDownImage.addStyleName(kladjeCss.downimage());
		
		roteerLinksomResource = kladjeGWTClientBundle.roteerLinksomResource();
		roteerLinksomImage = new Image(roteerLinksomResource);
		roteerLinksomImage.addStyleName(kladjeCss.upimage());
		
		roteerRechtsomResource = kladjeGWTClientBundle.roteerRechtsomResource();
		roteerRechtsomImage = new Image(roteerRechtsomResource);
		roteerRechtsomImage.addStyleName(kladjeCss.upimage());
		
		vergrootResource = kladjeGWTClientBundle.vergrootResource();
		vergrootImage = new Image(vergrootResource);
		vergrootImage.addStyleName(kladjeCss.pushimage());
		
		verkleinResource = kladjeGWTClientBundle.verkleinResource();
		verkleinImage = new Image(verkleinResource);
		verkleinImage.addStyleName(kladjeCss.pushimage());
		
		regenboogResource = kladjeGWTClientBundle.regenboogResource();
		regenboogImage = new Image(regenboogResource);
		regenboogImage.addStyleName(kladjeCss.pushimage2());

		zwartResource = kladjeGWTClientBundle.zwartResource();
		zwartImage = new Image(zwartResource);
		zwartImage.addStyleName(kladjeCss.pushimage2());

		grijsResource = kladjeGWTClientBundle.grijsResource();
		grijsImage = new Image(grijsResource);
		grijsImage.addStyleName(kladjeCss.pushimage2());
		
		roodResource = kladjeGWTClientBundle.roodResource();
		roodImage = new Image(roodResource);
		roodImage.addStyleName(kladjeCss.pushimage2());
		
		oranjeResource = kladjeGWTClientBundle.oranjeResource();
		oranjeImage = new Image(oranjeResource);
		oranjeImage.addStyleName(kladjeCss.pushimage2());
		
		groenResource = kladjeGWTClientBundle.groenResource();
		groenImage = new Image(groenResource);
		groenImage.addStyleName(kladjeCss.upimage());
		
		cyaanResource = kladjeGWTClientBundle.cyaanResource();
		cyaanImage = new Image(cyaanResource);
		cyaanImage.addStyleName(kladjeCss.pushimage2());
		
		blauwResource = kladjeGWTClientBundle.blauwResource();
		blauwImage = new Image(blauwResource);
		blauwImage.addStyleName(kladjeCss.pushimage2());

		magentaResource = kladjeGWTClientBundle.magentaResource();
		magentaImage = new Image(magentaResource);
		magentaImage.addStyleName(kladjeCss.pushimage2());
/*		
		geelResource = kladjeGWTClientBundle.geelResource();
		geelImage = new Image(geelResource);
		geelImage.addStyleName(kladjeCss.pushimage2());
*/		
		

	} // getImages	
		
	// stand-alone versie
	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(kladjeCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get().add(dlp);
		RootPanel.get().addStyleName(kladjeCss.root());
		
		//Stub.publish(this);
		init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());

		
	}	
		
	public void makeBottom()
	{
		int currentX = leftOffset;
		int currentY = topOffset;
		
		tekenButton = new ToggleButton(tekenKnopUpImage, tekenKnopDownImage);
		bottomPanel.add(tekenButton);
		bottomPanel.setWidgetLeftWidth(tekenButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(tekenButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);

		//tekenButton.addTouchStartHandler(new ToggleTouchStartHandler());
		
		//ToggleMouseHandler toggleMouseHandler = new ToggleMouseHandler();
		//tekenButton.addMouseDownHandler(toggleMouseHandler);
		
		ToggleClickHandler toggleClickHandler = new ToggleClickHandler();
		tekenButton.addClickHandler(toggleClickHandler);


/*		
		tekenButton.addClickHandler(new ClickHandler() { // Zowel touch als mouse, wordt aangeroepen als de toggle al geweest is.

			@Override
			public void onClick(ClickEvent event) {
    			if (tekenButton.isDown())
    			{
    				buttonsUp(tekenButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.tekenen;
    				kladjeGWTVeld.hideTekstVeld(true);    				
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.paint();
    			}
    			else
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    			}
 				
			}});
*/		
		
		currentX += toggleSize + leftOffset;


		if (lijnTekenen)
		{
			tekenLijnButton = new ToggleButton(tekenLijnUpImage, tekenLijnDownImage);
			tekenLijnButton.addStyleName("togglebutton");
			bottomPanel.add(tekenLijnButton);
			bottomPanel.setWidgetLeftWidth(tekenLijnButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(tekenLijnButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		
			//tekenLijnButton.addTouchStartHandler(new ToggleTouchStartHandler());

			//tekenLijnButton.addMouseDownHandler(toggleMouseHandler);
			tekenLijnButton.addClickHandler(toggleClickHandler);
		
			currentX += toggleSize + leftOffset;
		}
		
		if (rechthoekTekenen)
		{	
			tekenRechthoekButton = new ToggleButton(tekenRechthoekUpImage, tekenRechthoekDownImage);
			tekenRechthoekButton.addStyleName("togglebutton");
			bottomPanel.add(tekenRechthoekButton);
			bottomPanel.setWidgetLeftWidth(tekenRechthoekButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(tekenRechthoekButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		
			//tekenRechthoekButton.addTouchStartHandler(new ToggleTouchStartHandler());
		
			//tekenRechthoekButton.addMouseDownHandler(toggleMouseHandler);
			tekenRechthoekButton.addClickHandler(toggleClickHandler);
		
			currentX += toggleSize + leftOffset;
		}
		
		if (cirkelTekenen)
		{	
			tekenCirkelButton = new ToggleButton(tekenCirkelUpImage, tekenCirkelDownImage);		
			tekenCirkelButton.addStyleName("togglebutton");
			bottomPanel.add(tekenCirkelButton);
			bottomPanel.setWidgetLeftWidth(tekenCirkelButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(tekenCirkelButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);

			//tekenCirkelButton.addTouchStartHandler(new ToggleTouchStartHandler());
		
			//tekenCirkelButton.addMouseDownHandler(toggleMouseHandler);
			tekenCirkelButton.addClickHandler(toggleClickHandler);
				
			currentX += toggleSize + leftOffset;
		}	

		
  		if (tekstTekenen)
  		{
			tekenTekstButton = new ToggleButton(tekenTekstUpImage, tekenTekstDownImage); 
			tekenTekstButton.addStyleName("togglebutton");
			bottomPanel.add(tekenTekstButton);
			bottomPanel.setWidgetLeftWidth(tekenTekstButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(tekenTekstButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);

			//tekenTekstButton.addTouchStartHandler(new ToggleTouchStartHandler());
		
			//tekenTekstButton.addMouseDownHandler(toggleMouseHandler);
			tekenTekstButton.addClickHandler(toggleClickHandler);

			currentX += toggleSize + leftOffset;
		}
		
		

		selecterenButton = new ToggleButton(selecterenUpImage, selecterenDownImage);		
		selecterenButton.addStyleName("togglebutton");
		bottomPanel.add(selecterenButton);
		bottomPanel.setWidgetLeftWidth(selecterenButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(selecterenButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);

		//selecterenButton.addTouchStartHandler(new ToggleTouchStartHandler());
		
		//selecterenButton.addMouseDownHandler(toggleMouseHandler);
		selecterenButton.addClickHandler(toggleClickHandler);
				
		currentX += toggleSize + 2 * leftOffset;
	
		
		terugButton = new PushButton("terug");
		terugButton.addStyleName("pushbutton");
		bottomPanel.add(terugButton);
		bottomPanel.setWidgetLeftWidth(terugButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(terugButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
		//terugButton.addTouchEndHandler(new PushTouchEndHandler());
		
		//terugButton.addMouseDownHandler(new PushMouseHandler());
		terugButton.addClickHandler(new PushClickHandler());
		
		currentX += buttonWidth + 2 * leftOffset;		


		wisButton = new PushButton("wis");
		wisButton.addStyleName("pushbutton");
		bottomPanel.add(wisButton);
		bottomPanel.setWidgetLeftWidth(wisButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(wisButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
		//wisButton.addTouchEndHandler(new PushTouchEndHandler());
		
		//wisButton.addMouseDownHandler(new PushMouseHandler());
		wisButton.addClickHandler(new PushClickHandler());
		
		currentX += buttonWidth + 2 * leftOffset;
		currentY -= 2;
		
		if (roteren)
		{	
			roteerLinksomButton = new PushButton(roteerLinksomImage);
			roteerLinksomButton.addStyleName("pushbutton");
			
			bottomPanel.add(roteerLinksomButton);
			bottomPanel.setWidgetLeftWidth(roteerLinksomButton, currentX, Style.Unit.PX, pushSize, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(roteerLinksomButton, currentY, Style.Unit.PX, pushSize, Style.Unit.PX);
		
			//roteerLinksomButton.addTouchEndHandler(new PushTouchEndHandler());
		
			//roteerLinksomButton.addMouseDownHandler(new PushMouseHandler());
			roteerLinksomButton.addClickHandler(new PushClickHandler());
		
			currentX += pushSize + leftOffset;
		
			roteerRechtsomButton = new PushButton(roteerRechtsomImage);
			roteerRechtsomButton.addStyleName("pushbutton");
			bottomPanel.add(roteerRechtsomButton);
			bottomPanel.setWidgetLeftWidth(roteerRechtsomButton, currentX, Style.Unit.PX, pushSize, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(roteerRechtsomButton, currentY, Style.Unit.PX, pushSize, Style.Unit.PX);
		
			//roteerRechtsomButton.addTouchEndHandler(new PushTouchEndHandler());
			
			//roteerRechtsomButton.addMouseDownHandler(new PushMouseHandler());
			roteerRechtsomButton.addClickHandler(new PushClickHandler());
			
			currentX += pushSize + leftOffset;
		}
		
		if (schalen)
		{
			vergrootButton = new PushButton(vergrootImage);
			vergrootButton.addStyleName("pushbutton");
			bottomPanel.add(vergrootButton);
			bottomPanel.setWidgetLeftWidth(vergrootButton, currentX, Style.Unit.PX, pushSize, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(vergrootButton, currentY, Style.Unit.PX, pushSize, Style.Unit.PX);
		
			//vergrootButton.addTouchEndHandler(new PushTouchEndHandler());
			
			//vergrootButton.addMouseDownHandler(new PushMouseHandler());
			vergrootButton.addClickHandler(new PushClickHandler());
		
			currentX += pushSize + leftOffset;

			verkleinButton = new PushButton(verkleinImage);
			verkleinButton.addStyleName("pushbutton");
			bottomPanel.add(verkleinButton);
			bottomPanel.setWidgetLeftWidth(verkleinButton, currentX, Style.Unit.PX, pushSize, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(verkleinButton, currentY, Style.Unit.PX, pushSize, Style.Unit.PX);
		
			//verkleinButton.addTouchEndHandler(new PushTouchEndHandler());
			
			//verkleinButton.addMouseDownHandler(new PushMouseHandler());
			verkleinButton.addClickHandler(new PushClickHandler());
		
			currentX += pushSize + leftOffset;
		}
		
		if (kleurkeuze)
		{
			kleurkeuzeButton = new PushButton(regenboogImage);
			kleurkeuzeButton.addStyleName("pushbutton");
			bottomPanel.add(kleurkeuzeButton);
			bottomPanel.setWidgetLeftWidth(kleurkeuzeButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(kleurkeuzeButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		
			//kleurkeuzeButton.addTouchEndHandler(new PushTouchEndHandler());
			
			//kleurkeuzeButton.addMouseDownHandler(new PushMouseHandler());
			kleurkeuzeButton.addClickHandler(new PushClickHandler());
		
			currentX += toggleSize + leftOffset;
		}
		kladjeGWTVeld.paint();

		
		
	} // makeBottom()
	
	

	public KladjeGWT()
	{
		
	}
	
	
	
	public KladjeGWT(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		if (h != null && h.get("breedte") != null)
			breedte = (Integer) h.get("breedte");
		if (h != null && h.get("hoogte") != null)
			hoogte = (Integer) h.get("hoogte");
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		
		
// constructie en initialisatie uit elkaar trekken.
// constructie
		
		getImages(); 
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(kladjeCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		//RootPanel.get().add(dlp);
		//RootPanel.get().addStyleName(kladjeCss.root());
		
		init(breedte, hoogte, launchState, randomVarWaarden);

	}
	
	public Widget asWidget()
	{
		return dlp; 
	}
	
   	void buttonsUp(ToggleButton tb)
   	{
   		if (tekenButton != null && !tekenButton.equals(tb))
   			tekenButton.setDown(false);
   		if (tekenLijnButton != null && !tekenLijnButton.equals(tb))
   			tekenLijnButton.setDown(false);
 		if (tekenRechthoekButton != null && !tekenRechthoekButton.equals(tb))
   			tekenRechthoekButton.setDown(false);
   		if (tekenCirkelButton != null && !tekenCirkelButton.equals(tb))
   			tekenCirkelButton.setDown(false);
 		if (tekenTekstButton != null && !tekenTekstButton.equals(tb))
 			tekenTekstButton.setDown(false);
   		if (selecterenButton != null && !selecterenButton.equals(tb))
   			selecterenButton.setDown(false);
   	}
	
   	
   	class ToggleClickHandler implements ClickHandler
	//class ToggleMouseHandler implements MouseDownHandler//, MouseMoveHandler, MouseUpHandler
	{
		//public void onMouseDown(MouseDownEvent e)
   		public void onClick(ClickEvent e)
		{
//System.out.println("mouse down");
			
			// deze LIJKT niet nodig (mag wel)
			//e.preventDefault();
			
			// deze zorgt dat je niet scollt in de DWOPlayer
			e.stopPropagation();
			
			
			
			
    		if (e.getSource() == tekenButton)
    		{
    			if (tekenButton.isDown())
    			{
    				buttonsUp(tekenButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.tekenen;
    				kladjeGWTVeld.hideTekstVeld(true);    				
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.paint();
    			}
    			else
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    				kladjeGWTVeld.hideTekstVeld(true);
    			}
    			
    		}
    		else if (e.getSource() == tekenLijnButton)
    		{
    			if (tekenLijnButton.isDown())
    			{
    				buttonsUp(tekenLijnButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.lijnTekenen;
    				kladjeGWTVeld.hideTekstVeld(true);
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.paint();
    				
    			}
    			else
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    				kladjeGWTVeld.hideTekstVeld(true);
    			}
    			
    		}
    		else if (e.getSource() == tekenRechthoekButton)
    		{
    			if (tekenRechthoekButton.isDown())
    			{
    				buttonsUp(tekenRechthoekButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.rechthoekTekenen;
    				kladjeGWTVeld.hideTekstVeld(true);
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.paint();
    				
    			}
    			else
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    				kladjeGWTVeld.hideTekstVeld(true);
    			}
    			
    		}
    		else if (e.getSource() == tekenCirkelButton)
    		{
    			if (tekenCirkelButton.isDown())
    			{
    				buttonsUp(tekenCirkelButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.cirkelTekenen;
    				kladjeGWTVeld.hideTekstVeld(true);
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.paint();
    				
    			}
    			else
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    				kladjeGWTVeld.hideTekstVeld(true);
    			}
    			
    		}
    		
    		else if (e.getSource() == tekenTekstButton)
    		{
    			if (tekenTekstButton.isDown())
    			{
    				buttonsUp(tekenTekstButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.tekstTekenen;
    				kladjeGWTVeld.hideTekstVeld(true);
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.paint();
    				
    			}
    			else
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    				kladjeGWTVeld.hideTekstVeld(true);
    			}
    			
    		}
    		
    		else if (e.getSource() == selecterenButton)
    		{
    			if (selecterenButton.isDown())
    			{
    				buttonsUp(selecterenButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.selecteren;
    				kladjeGWTVeld.hideTekstVeld(true);
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.resetSelectedObject();
    				kladjeGWTVeld.resetSelectedObjects();
    				kladjeGWTVeld.paint();
    				
    			}
    			else
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    				kladjeGWTVeld.hideTekstVeld(true);
    			}
    			
    		}

		}
		
	}

	
    class ToggleTouchStartHandler implements TouchStartHandler
	{
    	public void onTouchStart(TouchStartEvent e)
    	{
    		// DIT NIET toevoegen!!
			//e.preventDefault();
			e.stopPropagation();
    		
    		if (e.getSource() == tekenButton)
    		{
    			if (tekenButton.isDown())
    			{
    				buttonsUp(tekenButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.tekenen;
    				kladjeGWTVeld.hideTekstVeld(true);
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.paint();
    				
    			}
    			else
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    			}
    			
    		}
/*    		
    		else if (e.getSource() == gumButton)
    		{
    			if (gumButton.isDown())
    			{
    				buttonsUp(gumButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.gummen;
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.paint();
    				
    			}
    			else
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    			}
    			
    		}
*/    		
    		else if (e.getSource() == tekenLijnButton)
    		{
    			if (tekenLijnButton.isDown())
    			{
    				buttonsUp(tekenLijnButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.lijnTekenen;
    				kladjeGWTVeld.hideTekstVeld(true);
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.paint();
    				
    			}
    			else
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    			}
    			
    		}
    		else if (e.getSource() == tekenRechthoekButton)
    		{
    			if (tekenRechthoekButton.isDown())
    			{
    				buttonsUp(tekenRechthoekButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.rechthoekTekenen;
    				kladjeGWTVeld.hideTekstVeld(true);
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.paint();
    				
    			}
    			else
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    			}
    			
    		}
    		else if (e.getSource() == tekenCirkelButton)
    		{
    			if (tekenCirkelButton.isDown())
    			{
    				buttonsUp(tekenCirkelButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.cirkelTekenen;
    				kladjeGWTVeld.hideTekstVeld(true);
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.paint();
    				
    			}
    			else
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    			}
    			
    		}
    		
    		else if (e.getSource() == tekenTekstButton)
    		{
    			if (tekenTekstButton.isDown())
    			{
    				buttonsUp(tekenTekstButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.tekstTekenen;
    				kladjeGWTVeld.hideTekstVeld(true);
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.paint();
    				
    			}
    			else
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    			}
    			
    		}
    		
    		else if (e.getSource() == selecterenButton)
    		{
    			if (selecterenButton.isDown())
    			{
    				buttonsUp(selecterenButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.selecteren;
    				kladjeGWTVeld.hideTekstVeld(true);
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.resetSelectedObject();
    				kladjeGWTVeld.resetSelectedObjects();
    				kladjeGWTVeld.paint();

    			}
    			else
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    			}
    			
    		}
    		
    		// DIT NIET!!
			//e.preventDefault();
			//e.stopPropagation();
    		
	    		
    	}
    	
    } // ToggleTouchStartHandler
    
    
    //class PushMouseHandler implements MouseDownHandler
    class PushClickHandler implements ClickHandler
    {
    	//public void onMouseDown(MouseDownEvent e)
    	public void onClick(ClickEvent e)
    	{
    		
    		if (touchStart)
    			return;
    		
			//e.preventDefault();
			e.stopPropagation();
    		
    		if (e.getSource() == terugButton)
    		{
    			kladjeGWTVeld.hideTekstVeld(true);
    			kladjeGWTVeld.undo();
    			    			
    		}
    		else if (e.getSource() == wisButton)
    		{
    			kladjeGWTVeld.wis(true);
    		}
    		else if (e.getSource() == roteerLinksomButton)
    		{
    			kladjeGWTVeld.hideTekstVeld(true);
    			
    				if (kladjeGWTVeld.mouseMode == kladjeGWTVeld.selecteren)
    					kladjeGWTVeld.rotateObjectSelected(- kladjeGWTVeld.rotateStep);
    		}
    		else if (e.getSource() == roteerRechtsomButton)
    		{
    			kladjeGWTVeld.hideTekstVeld(true);
    			
    			if (kladjeGWTVeld.mouseMode == kladjeGWTVeld.selecteren)
    				kladjeGWTVeld.rotateObjectSelected(kladjeGWTVeld.rotateStep);
    		}
    		else if (e.getSource() == vergrootButton)
    		{
    			kladjeGWTVeld.hideTekstVeld(true);
    			
    			if (kladjeGWTVeld.mouseMode == kladjeGWTVeld.selecteren)
    				kladjeGWTVeld.scaleObjectSelected(kladjeGWTVeld.scaleUpStep);
    		}
    		else if (e.getSource() == verkleinButton)
    		{
    			kladjeGWTVeld.hideTekstVeld(true);
    			
    			if (kladjeGWTVeld.mouseMode == kladjeGWTVeld.selecteren)
    				kladjeGWTVeld.scaleObjectSelected(kladjeGWTVeld.scaleDownStep);
    		}
    		else if (e.getSource() == kleurkeuzeButton)
    		{
    			kladjeGWTVeld.hideTekstVeld(true);
    			
    			if (colorPopup == null)
    			{
    				colorPopup = new ColorPopup(KladjeGWT.this);
    				//int showX = kleurkeuzeButton.getAbsoluteLeft() + toggleSize/2 - colorPopup.breedte/2;
    				int showX = kladjeGWTCanvas.getAbsoluteLeft() + breedte - colorPopup.breedte - 20;
    				int showY = hoogte - bottomHeight - colorPopup.hoogte - topOffset;
    				colorPopup.setPopupPosition(showX, showY);
    				colorPopup.show();
    			}
    			else
    			{
    				colorPopup.show();
    			}
    		}
    		
    	}
    	
    }
    
    class PushTouchStartHandler implements TouchStartHandler
    {
    	public void onTouchStart(TouchStartEvent e)
    	{
			
    		touchStart = true;
    		
    		// DIT NIET toevoegen!!
    		//e.preventDefault();
			e.stopPropagation();
    		
    		
    		if (e.getSource() == terugButton)
    		{
    			kladjeGWTVeld.undo();
    			
    		}
    		else if (e.getSource() == wisButton)
    		{
    			kladjeGWTVeld.wis(true);
    		}
    		else if (e.getSource() == roteerLinksomButton)
    		{
   				if (kladjeGWTVeld.mouseMode == kladjeGWTVeld.selecteren)
    					kladjeGWTVeld.rotateObjectSelected(- kladjeGWTVeld.rotateStep);
    		}
    		else if (e.getSource() == roteerRechtsomButton)
    		{
    			if (kladjeGWTVeld.mouseMode == kladjeGWTVeld.selecteren)
    				kladjeGWTVeld.rotateObjectSelected(kladjeGWTVeld.rotateStep);
    		}
    		else if (e.getSource() == vergrootButton)
    		{
    			if (kladjeGWTVeld.mouseMode == kladjeGWTVeld.selecteren)
    				kladjeGWTVeld.scaleObjectSelected(kladjeGWTVeld.scaleUpStep);
    		}
    		else if (e.getSource() == verkleinButton)
    		{
    			if (kladjeGWTVeld.mouseMode == kladjeGWTVeld.selecteren)
    				kladjeGWTVeld.scaleObjectSelected(kladjeGWTVeld.scaleDownStep);
    		}
    		
    		
    	}
    }

    class PushTouchEndHandler implements TouchEndHandler
    {
    	public void onTouchEnd(TouchEndEvent e)
    	{
			
       		touchStart = true;    		
    		
    		// DIT NIET toevoegen!!
    		//e.preventDefault();
			e.stopPropagation();

/*			
    		long touchEventAt = stp.getTime();
			if (lastTouchEventAt > 0)
			{	long deltaTime = touchEventAt - lastTouchEventAt;
				lastTouchEventAt = touchEventAt;
				if (deltaTime < touchPause)
					return;
			}
*/			
    		
    		if (e.getSource() == terugButton)
    		{
    			kladjeGWTVeld.undo();
    			
    		}
    		else if (e.getSource() == wisButton)
    		{
    			kladjeGWTVeld.wis(true);
    		}
    		else if (e.getSource() == roteerLinksomButton)
    		{
//    			if (!rotatedLeft)
//    			{	
    				if (kladjeGWTVeld.mouseMode == kladjeGWTVeld.selecteren)
    					kladjeGWTVeld.rotateObjectSelected(- kladjeGWTVeld.rotateStep);
//    				rotatedLeft = true;
//    			}
//    			else
//    			{
//    				rotatedLeft = false;
//    			}
    			
    		}
    		else if (e.getSource() == roteerRechtsomButton)
    		{
    			if (kladjeGWTVeld.mouseMode == kladjeGWTVeld.selecteren)
    				kladjeGWTVeld.rotateObjectSelected(kladjeGWTVeld.rotateStep);
    		}
    		else if (e.getSource() == vergrootButton)
    		{
    			if (kladjeGWTVeld.mouseMode == kladjeGWTVeld.selecteren)
    				kladjeGWTVeld.scaleObjectSelected(kladjeGWTVeld.scaleUpStep);
    		}
    		else if (e.getSource() == verkleinButton)
    		{
    			if (kladjeGWTVeld.mouseMode == kladjeGWTVeld.selecteren)
    				kladjeGWTVeld.scaleObjectSelected(kladjeGWTVeld.scaleDownStep);
    		}
    		
    		
    	}
    }
    
	@Override
	public HashMap<String, Object> getState()
	{
		return kladjeGWTVeld.getState();
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		kladjeGWTVeld.setState(h);

	}

	@Override
	public int getScore()
	{
		return 0;
	}

	@Override
	public boolean isCorrect()
	{
		return false;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		// TODO Auto-generated method stub

	}

	
	public void init(int width, int height, Map<String, Object> launchState,
			Map<String, Number> values) 
	{
		this.breedte = width;
		this.hoogte = height;
		this.launchState = launchState;
		
		if (launchState != null && launchState.get("lijnen") != null)
			lijnen = (Boolean) launchState.get("lijnen");
		if (launchState != null && launchState.get("ruitjes") != null)
			ruitjes = (Boolean) launchState.get("ruitjes");
		
		if (launchState != null && launchState.get("lijnTekenen") != null)
			lijnTekenen = (Boolean) launchState.get("lijnTekenen");
		if (launchState != null && launchState.get("rechthoekTekenen") != null)
			rechthoekTekenen = (Boolean) launchState.get("rechthoekTekenen");
		if (launchState != null && launchState.get("cirkelTekenen") != null)
			cirkelTekenen = (Boolean) launchState.get("cirkelTekenen");
		if (launchState != null && launchState.get("tekstTekenen") != null)
			tekstTekenen = (Boolean) launchState.get("tekstTekenen");
		
		if (launchState != null && launchState.get("roteren") != null)
			roteren = (Boolean) launchState.get("roteren");
		if (launchState != null && launchState.get("schalen") != null)
			schalen = (Boolean) launchState.get("schalen");

		dlp.setSize(breedte + "px", hoogte + "px");
		
		bottomPanel = new LayoutPanel();
		bottomPanel.addStyleName(kladjeCss.bottom());

		dlp.addSouth(bottomPanel, bottomHeight);

		kladjeGWTVeld = new KladjeGWTVeld(breedte, hoogte - bottomHeight); 

		kladjeGWTCanvas = kladjeGWTVeld.getCanvas();
		if (kladjeGWTCanvas == null) 
		{
	      RootPanel.get().add(new Label(upgradeMessage));
	      return;
	    }
		
		kladjeGWTCanvas.addStyleName(kladjeCss.canvas());
		kladjeGWTVeld.initContext2d();		
		
		dlp.add(kladjeGWTCanvas);

		kladjeGWTVeld.lijnen = lijnen;
		kladjeGWTVeld.ruitjes = ruitjes;
		
		kladjeGWTVeld.setState(launchState);

		makeBottom();
		
		kladjeGWTVeld.paint();
	}
	
    

}
