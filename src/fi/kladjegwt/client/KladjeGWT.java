package fi.kladjegwt.client;



//import java.awt.Point;
import java.util.HashMap;
//import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

import fi.kladjegwt.client.text.Text;
import fi.writemathgwt.client.engine.ReferenceSamples;
import fi.writemathgwt.client.engine.Stroke;
import fi.writemathgwt.client.engine.StrokeContainer;


/**
 * hoofdklasse voor KladjeGWT; deze klasse creeert en beheert de teken- en selectie knoppen 
 * (de aanwezigheid van tekenknoppen is instelbaar), de terugknop. de wisknop en de
 * kleurkeuzeknop (instelbaar);<br>.  
 * de klasse leest ook docent-data (als die er zijn) in, d.w.z. tekeningen die door de
 * docent zijn klaargezet.  
 * @author huub
 */

public class KladjeGWT implements EntryPoint, InteractionStub, InteractionView, CBookEventListener
{
	private static Logger logger = Logger.getLogger("KladjeGWT");
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	static final Text rb = GWT.create(Text.class);

	OpdrNavIF comRoot;
	
	/**
	 * GUI componenten: dlp hangt aan de root, bottomPanel
	 * zit in dlp-south, het Canvas van KladjeGWTVeld zit
	 * in dlp-centrum   
	 */
	DockLayoutPanel dlp;
	/**
	 * LayoutPanel voor knoppen
	 */
	LayoutPanel bottomPanel;
	
	LayoutPanel topPanel;
	
	/**
	 * klasse die het tekengebeuren afhandelt
	 */
	KladjeGWTVeld kladjeGWTVeld;
	/**
	 * het Canvas om op te tekenen (dit gebeurt in klasse kladjeGWTVeld)
	 */
	Canvas kladjeGWTCanvas;
	
	/**
	 * toggle knoppen: tekenen en selecteren is altijd mogelijk, de andere acties zijn instelbaar 
	 */
	ToggleButton formuleButton, tekenButton, tekenLijnButton, tekenRechthoekButton, tekenCirkelButton,
    			 tekenTekstButton, selecterenButton;
	
	/**
	 * knoppen voor undo, wissen en (instelbaar) het oproepen van de colorPopup 
	 */
	PushButton terugButton, wisButton, kleurkeuzeButton;
	/**
	 * de ColorPopup (zie klasse ColorPopup)
	 */
	ColorPopup colorPopup;
	
	/**
	 * layout constantes in pixels
	 */
	int breedte = 700;
	int hoogte = 550;
	int bottomHeight = 32;
	int topHeight = 52;
	int leftOffset = 5;
	int topOffset = 5;
	int toggleSize = 22;
	int pushSize = 24;
	int buttonWidth = 40;
	int buttonHeight = 22;

	/**
	 * launchdata
	 */
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	/**
	 * bevat verwijzingen naar de plaatjes op de knoppen en de css 
	 */
	KladjeGWTClientBundle kladjeGWTClientBundle;
	/**
	 * cascading style sheet
	 */
	static KladjeCssResource kladjeCss;
	/**
	 * resources voor de plaatjes: alle ToggleButtons hebben twee resources: een voor de normale toestand
	 * en een voor de ingedrukte toestand
	 */
	ImageResource tekenFormuleUpResource, tekenFormuleDownResource, tekenKnopUpResource, tekenKnopDownResource, tekenLijnUpResource, tekenLijnDownResource, 
				  tekenRechthoekUpResource, tekenRechthoekDownResource, tekenCirkelUpResource, tekenCirkelDownResource, 
				  tekenTekstUpResource, tekenTekstDownResource, selecterenUpResource, selecterenDownResource, 
	              regenboogResource, 
	              zwartResource, grijsResource, roodResource, oranjeResource, groenResource, cyaanResource,  
	              blauwResource, magentaResource; 
	              
	/**
	 * de actuele plaatjes: alle ToggleButtons hebben twee plaatjes: een voor de normale toestand
	 * en een voor de ingedrukte toestand
	 */
	Image tekenFormuleUpImage, tekenFormuleDownImage, tekenKnopUpImage, tekenKnopDownImage, tekenLijnUpImage, tekenLijnDownImage, 
		  tekenRechthoekUpImage, tekenRechthoekDownImage, tekenCirkelUpImage, tekenCirkelDownImage, 
		  tekenTekstUpImage, tekenTekstDownImage, selecterenUpImage, selecterenDownImage,
		  regenboogImage, 
		  zwartImage, grijsImage, roodImage, oranjeImage, groenImage, cyaanImage, 
		  blauwImage, magentaImage; 
		  
	
	/**
	 * instelbaarheid: kleuren kiezen?
	 */
	boolean kleurkeuze = true;
	
	/**
	 * instelbaarheid: lijnen als achtergrond tekenveld?  
	 */
	boolean lijnen = false;
	/**
	 * instelbaarheid: ruitjes als achtergrond tekenveld?  
	 */
	boolean ruitjes = true;
	/**
	 * instelbaarheid: afmeting van de ruitjes als achtergrond (in pixels)
	 */
	int ruitjesSize = 20;
	/**
	 * instelbaarheid: formule tekenenlijnen tekenen?
	 */
	boolean formuleOptie = true;
	/**
	 * instelbaarheid: formule tekenen?
	 */
	boolean lijnTekenen = true;
	/**
	 * instelbaarheid: rechthoeken tekenen?
	 */
	boolean rechthoekTekenen = true;
	/**
	 * instelbaarheid: ellipsen tekenen?
	 */
	boolean cirkelTekenen = true;
	/**
	 * instelbaarheid: tekst toevoegen?
	 */
	boolean tekstTekenen = true;

	/**
	 * instelbaarheid: objecten draaien? 
	 */
	boolean roteren = true;
	/**
	 * instelbaarheid: objecten schalen
	 */
	boolean schalen = true;
	
	private Point translation = new Point(0,0);
	
	private double scale = 1.0;
	
	private int asHoogte;
	
	private StrokeContainer strokeContainer = new StrokeContainer();
	
	private FormuleViewer formuleViewer;
	
	/**
	 * maak de css in ore en haal via de resources alle plaatjes op 
	 */
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
		
		tekenFormuleUpResource = kladjeGWTClientBundle.tekenFormuleUpResource();
		tekenFormuleDownResource = kladjeGWTClientBundle.tekenFormuleDownResource();
		tekenFormuleUpImage = new Image(tekenFormuleUpResource);
		tekenFormuleDownImage = new Image(tekenFormuleDownResource);
		tekenFormuleUpImage.addStyleName(kladjeCss.upimage());
		tekenFormuleDownImage.addStyleName(kladjeCss.downimage());
		
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

	} // getImages	
		

	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(kladjeCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		RootPanel.get().add(dlp);
		RootPanel.get().addStyleName(kladjeCss.root());
		
		Stub.publish(this); 
		//init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());

	}	

	/**
	 * creeer de ToggleButtons en PushButtons (indien ingesteld) op het bottomPanel en voeg
	 * Click Handlers toe 
	 */
	public void makeBottom()
	{
		int currentX = leftOffset;
		int currentY = topOffset;
		ToggleClickHandler toggleClickHandler = new ToggleClickHandler();
		
		if (formuleOptie)
		{
			formuleButton = new ToggleButton(tekenFormuleUpImage, tekenFormuleDownImage);
			formuleButton.addStyleName("togglebutton");
			bottomPanel.add(formuleButton);
			bottomPanel.setWidgetLeftWidth(formuleButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(formuleButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		
			formuleButton.addClickHandler(toggleClickHandler);
		
			currentX += toggleSize + leftOffset;
		}
		
		tekenButton = new ToggleButton(tekenKnopUpImage, tekenKnopDownImage);
		bottomPanel.add(tekenButton);
		bottomPanel.setWidgetLeftWidth(tekenButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(tekenButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);

		
		tekenButton.addClickHandler(toggleClickHandler);

		currentX += toggleSize + leftOffset;

		if (lijnTekenen)
		{
			tekenLijnButton = new ToggleButton(tekenLijnUpImage, tekenLijnDownImage);
			tekenLijnButton.addStyleName("togglebutton");
			bottomPanel.add(tekenLijnButton);
			bottomPanel.setWidgetLeftWidth(tekenLijnButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(tekenLijnButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		
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

			tekenTekstButton.addClickHandler(toggleClickHandler);

			currentX += toggleSize + leftOffset;
		}
		
		selecterenButton = new ToggleButton(selecterenUpImage, selecterenDownImage);		
		selecterenButton.addStyleName("togglebutton");
		bottomPanel.add(selecterenButton);
		bottomPanel.setWidgetLeftWidth(selecterenButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(selecterenButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);

		selecterenButton.addClickHandler(toggleClickHandler);
				
		currentX += toggleSize + 2 * leftOffset;
	
		
		terugButton = new PushButton(rb.terugTekst());
		terugButton.addStyleName("pushbutton");
		bottomPanel.add(terugButton);
		bottomPanel.setWidgetLeftWidth(terugButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(terugButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
		terugButton.addClickHandler(new PushClickHandler());
		
		currentX += buttonWidth + 2 * leftOffset;		


		wisButton = new PushButton(rb.wisTekst());
		wisButton.addStyleName("pushbutton");
		bottomPanel.add(wisButton);
		bottomPanel.setWidgetLeftWidth(wisButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(wisButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
		wisButton.addClickHandler(new PushClickHandler());
		
		currentX += buttonWidth + 2 * leftOffset;
		currentY -= 2;

		if (kleurkeuze)
		{
			kleurkeuzeButton = new PushButton(regenboogImage);
			kleurkeuzeButton.addStyleName("pushbutton");
			bottomPanel.add(kleurkeuzeButton);
			bottomPanel.setWidgetLeftWidth(kleurkeuzeButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(kleurkeuzeButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		
			kleurkeuzeButton.addClickHandler(new PushClickHandler());
		
			currentX += toggleSize + leftOffset;
		}
		kladjeGWTVeld.paint();
		
	} // makeBottom()
	
	

	public KladjeGWT()
	{
		
	}
	
	
	
	public KladjeGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
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
		dlp.addStyleName(kladjeCss.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		init(breedte, hoogte, launchState, randomVarWaarden);

	}
	
	
	
	public Widget asWidget()
	{
		return dlp; 
	}
	
	/**
	 * ToggleButton tb werd ingedrukt, dus zorg dat alle andere
	 * ToggelButtons niet ingedrukt zijn (GWT heeft niet zoiets als een RadioButtonGroup)   
	 * @param tb de ToggleButton die werd ingedrukt
	 */
   	void buttonsUp(ToggleButton tb)
   	{
   		if (tekenButton != null && !tekenButton.equals(tb))
   			tekenButton.setDown(false);
   		if (formuleButton != null && !formuleButton.equals(tb))
   			formuleButton.setDown(false);
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
	

   	/**
   	 * inner class voor het afhandelen van Click Events op Toggle Buttons;
   	 * gebruik methode buttonsUp zodat de ToggleButtons zich als een RadioButtonGroup gedragen; <br>
   	 * @author huub
   	 */
   	class ToggleClickHandler implements ClickHandler
	{

   		public void onClick(ClickEvent e)
		{
			//e.preventDefault();
			
			// deze zorgt dat je niet scollt in de DWOPlayer
			e.stopPropagation();

    		if (e.getSource() == tekenButton)
    		{
    			// ToggleButton werd ingedrukt
    			if (tekenButton.isDown())
    			{
    				buttonsUp(tekenButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.tekenen;
    				kladjeGWTVeld.hideTekstVeld(true);    				
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.paint();
    			}
    			else // ToggleButton werd uitgedrukt
    			{
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.inert;
    				kladjeGWTVeld.hideTekstVeld(true);
    			}
    			
    		}
    		else if (e.getSource() == formuleButton)
    		{
    			// ToggleButton werd ingedrukt
    			if (formuleButton.isDown())
    			{
    				buttonsUp(formuleButton);
    				kladjeGWTVeld.mouseMode = kladjeGWTVeld.formuleOptie;
    				kladjeGWTVeld.hideTekstVeld(true);    				
    				kladjeGWTVeld.selecteerRechthoek = null;
    				kladjeGWTVeld.paint();
    			}
    			else // ToggleButton werd uitgedrukt
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
    				kladjeGWTVeld.setSelecteerMode();
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

    
   	/**
   	 * inner class for handling Click Events on PushButtons
   	 * @author huub
   	 */
    class PushClickHandler implements ClickHandler
    {
    	public void onClick(ClickEvent e)
    	{
			//e.preventDefault();
			e.stopPropagation();
    		
    		if (e.getSource() == terugButton)
    		{
    			kladjeGWTVeld.hideTekstVeld(true);
    			kladjeGWTVeld.undo();
    			setChanged();
    			    			
    		}
    		else if (e.getSource() == wisButton)
    		{
    			kladjeGWTVeld.wis(true);
    			setChanged();
    		}
    		else if (e.getSource() == kleurkeuzeButton)
    		{
    			kladjeGWTVeld.hideTekstVeld(true);
    			
    			// maak een nieuwe colorPopup
    			if (colorPopup == null)
    			{
    				colorPopup = new ColorPopup(KladjeGWT.this);
    				int showX = kleurkeuzeButton.getAbsoluteLeft() + toggleSize/2 - colorPopup.breedte/2;

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
  
    /**
     * get de status van het werkveld, zie methode getState in klasse kladjeGWTVeld
     */
	public HashMap<String, Object> getState()
	{
		return kladjeGWTVeld.getState();
	}

	/**
	 * zet de status van het werkveld, zie methode setState in klasse kladjeGWTVeld 
	 */
	public void setState(HashMap<String, Object> h)
	{
		kladjeGWTVeld.setState(h, false);

	}

	public int getScore()
	{
		return 0;
	}

	public Boolean isCorrect()
	{
		return Boolean.TRUE;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		comRoot.addCBookEventListener("drawing", this);
		comRoot.addCBookEventListener("action.setCorrect", this);
		comRoot.addCBookEventListener("double.translationX", this);
		comRoot.addCBookEventListener("double.translationY", this);
	}

	
	public void init(int width, int height, Map<String, Object> map, //launchState,
			Map<String, Number> values) 
	{
		
		this.breedte = width;
		this.hoogte = height;
		
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");

		ObjectMap launchState = JSONUtilities.wrapMap(map);

		// instellingen achtergrondvulling werkveld 
		if (launchState.containsKey("lijnen"))
			lijnen = launchState.getBoolean("lijnen");
		if (launchState.containsKey("ruitjes"))
			ruitjes = launchState.getBoolean("ruitjes");
		if (launchState.containsKey("ruitjessize"))
			ruitjesSize = launchState.getInt("ruitjessize");

		// instellingen tekenopties
		if (launchState.containsKey("lijnTekenen"))
			lijnTekenen = launchState.getBoolean("lijnTekenen");
		if (launchState.containsKey("rechthoekTekenen"))
			rechthoekTekenen = launchState.getBoolean("rechthoekTekenen");
		if (launchState.containsKey("cirkelTekenen"))
			cirkelTekenen = launchState.getBoolean("cirkelTekenen");
		if (launchState.containsKey("tekstTekenen"))
			tekstTekenen = launchState.getBoolean("tekstTekenen");
		if (launchState.containsKey("formuleOptie"))
			formuleOptie = launchState.getBoolean("formuleOptie");
		
		// instellingen schaal- en roteeroptie
		if (launchState.containsKey("roteren"))
			roteren = launchState.getBoolean("roteren");
		if (launchState.containsKey("schalen"))
			schalen = launchState.getBoolean("schalen");
		
		int translationx = translation.x;
		if(launchState.containsKey("translationX"))
			translationx = launchState.getInt("translationX");
		int translationy = translation.y;
		if(launchState.containsKey("translationY"))
			translationy = launchState.getInt("translationY");
		translation = new Point(translationx,translationy);
		
		if(launchState.containsKey("scale"))
			scale = launchState.getDouble("scale");
		
		
		
		bottomPanel = new LayoutPanel();
		bottomPanel.addStyleName(kladjeCss.bottom());
		if(!formuleOptie)
			dlp.addSouth(bottomPanel, bottomHeight);
		
		if(formuleOptie) {
			topPanel = new LayoutPanel();
			topPanel.addStyleName(kladjeCss.top());
			dlp.addNorth(topPanel, topHeight);
		}
		
		kladjeGWTVeld = new KladjeGWTVeld(breedte, hoogte - bottomHeight, this); 

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
		kladjeGWTVeld.lineDistance = ruitjesSize;
		kladjeGWTVeld.roteren = roteren;
		kladjeGWTVeld.schalen = schalen;
		kladjeGWTVeld.translation = translation;
		kladjeGWTVeld.scale = scale;
		if(formuleOptie)
			kladjeGWTVeld.mouseMode = kladjeGWTVeld.formuleOptie;
	
		// docent tekeningen
		kladjeGWTVeld.setState(map, true);

		makeBottom();
		
		dlp.forceLayout();
		bottomPanel.forceLayout();
		
		kladjeGWTVeld.paint();
	}

	@Override
	public void kijkNa() {
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		this.breedte = breedte;
	}

	@Override
	public int getAsHoogte() {
		return asHoogte;
	}

	public int getHeight() 
	{
		return hoogte;
	}

	public int getWidth() 
	{
		return breedte;
	}

	public void setAsHoogte(int ashoogte) {
		this.asHoogte = ashoogte;
	}

	//@Override
	public void zetNagekeken(boolean b) {
	}

	//@Override
	public int[][] getScoreObjectives() {
		return null;
	}
	
	public void setChanged() {
		if(formuleViewer!=null)
			topPanel.remove(formuleViewer.getAsPanel());
		formuleViewer = new FormuleViewer(kladjeGWTVeld.getFormula());
		formuleViewer.setFont(FormuleFont.createFromFontSize(16));
		topPanel.add(formuleViewer.getAsPanel());
		
		Map<String,Object> map = kladjeGWTVeld.getState();
		comRoot.fireEvent(new CBookEvent(this,"drawing",map));
		comRoot.fireEvent(new CBookEvent(this,"equation",kladjeGWTVeld.getFormula()));
//		logger.info("in setChanged");
	}


	@Override
	public void acceptCBookEvent(CBookEvent event) {
		String command = event.getCommand();
		if (command.startsWith("drawing"))
		{
			Map map = (Map)event.getParameters();
			if (map!=null)
			{	kladjeGWTVeld.setState(map, false);
				kladjeGWTVeld.paint();
			}
		}
		
		if (command.startsWith("action.setCorrect"))
		{
			Map map = (Map)event.getParameters();
			if (map!=null)
			{	kladjeGWTVeld.setState(map, false);
				kladjeGWTVeld.paint();
			}
		}
		
		if (command.startsWith("double.translationX"))
		{
			logger.info("command ontvangen");
			Map map = (Map)event.getParameters();
			if (map!=null)
			{	
				logger.info("map!=null");
				logger.info(map.toString());
				int valueX = (int)((Double)map.get("value")).doubleValue();
				translation = new Point(-valueX, translation.y);
				kladjeGWTVeld.translation = translation;
				kladjeGWTVeld.paint();
			}
		}
		if (command.startsWith("double.translationY"))
		{
			Map map = (Map)event.getParameters();
			if (map!=null)
			{	int valueY = (int)((Double)map.get("value")).doubleValue();
				translation = new Point(translation.x, -valueY);
				kladjeGWTVeld.translation = translation;
				kladjeGWTVeld.paint();
			}
		}
	}
}
