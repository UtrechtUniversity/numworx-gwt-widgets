package fi.weblogogwt.client;

//import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;

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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.resources.client.ImageResource;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.logotekenap.Tekenblad;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

import java.util.logging.Logger;

public class WebLogoGWT implements EntryPoint, InteractionStub, InteractionView 
{
	// logger
    static Logger logger = Logger.getLogger("WebLogoGWT");

    static final String holderId = "dockholder";
	static final String upgradeMessage = 
		"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	WebLogoGWTClientBundle webLogoGWTClientBundle;
	static WebLogoGWTCssResource webLogoGWTCssResource;

	// UI
	DockLayoutPanel dlp;
	LayoutPanel bottomPanel;
	LayoutPanel webLogoPanel;
	JavaLogoSchuifVeld jlsVeld;
	public TraceBeheerder trb;
	public Uitvoerblad uitvoerblad;
	
	int buttonWidth = 45;
	int buttonHeight = 22;

	static int jlsBreedteKlein = 390;
	static int jlsBreedteGroot = 620;
	
	int offSet = 4;
	int leftOffset = 5;
	int topOffset = 5;
	
	int breedteGroot = 784; //784 is maximale breedte in popupFacade;
	int breedteKlein = 700;
	int breedtePaul = 950; //maximale breedte stand-alone 
	int breedte = 784;
	int hoogte = 575;
	int bottomHeight = 32;
	int jlsHoogte = hoogte - bottomHeight - offSet;
	int ubxKlein = jlsBreedteKlein + 2 * offSet; //programmaVeldZichtbaar ? scheidingX+5 : 5;
	int ubxGroot = jlsBreedteGroot + 2 * offSet;
	int uby = offSet;
	int ubbKlein = breedteKlein - jlsBreedteKlein - 3 * offSet; //getWidth()-(programmaVeldZichtbaar ? scheidingX+10 : 10);
	int ubbGroot = breedteGroot - jlsBreedteGroot - 3 * offSet;
	int ubbPaul = breedtePaul - jlsBreedteGroot - 3 * offSet;
	int ubb = 0;
	int ubh = jlsHoogte; //programmaVeldZichtbaar ? getHeight()-77 : getHeight()-10;
	
	public static String fontString = "12px sans-serif";
	public static String boldFontString = "bold 12px sans-serif";
	
//	CssColor bottomBgColor = CssColor.make(220, 220, 220);	
	
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;
	
	// parametrisatie
	boolean uitvoerVeldZichtbaar = true;
	boolean programmaVeldZichtbaar = true;
	boolean deeltakenZichtbaar = true;
	boolean whileLoopZichtbaar = true;
	boolean keuzeCommandZichtbaar = true;
	boolean printCommandsZichtbaar = true;
	boolean tekenCommandsZichtbaar = true;
	boolean traceZichtbaar = true;
	boolean codeIOZichtbaar = true;
	
	HashMap state = null;

	boolean traceAan = false;
	
	boolean correct = false;
	private int mode;
	private OpdrNavIF comRoot;
	
	
	PushButton importButton, exportButton, runButton;
	PushButton traceAanKnop, traceUitKnop, beginKnop, stapKnop, terugKnop, skipKnop;
	CheckBox showVariables;
	public Label methodeLabel;
	
	public VardisplayPanel vartracer = null;
	
	public int vartracerWidth, vartracerHeight;
	
	public void getImages() 
	{

		webLogoGWTClientBundle = GWT.create(WebLogoGWTClientBundle.class);
		webLogoGWTCssResource = webLogoGWTClientBundle.getWebLogoGWTCssResource();
		webLogoGWTCssResource.ensureInjected();
		

	}	
	public void onModuleLoad() 
	{
		
logger.info("WebLogoGWT onModuleLoad");		
		
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(webLogoGWTCssResource.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");


		RootPanel.get(holderId).add(dlp);
		RootPanel.get(holderId).addStyleName(webLogoGWTCssResource.root());
		
		
		//Stub.publish(this);
		init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());


			
	}
	
	public WebLogoGWT()
	{
		this(null, null, null);
	}
	
	public WebLogoGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		
logger.info("WebLogoGWT constructor");

		ObjectMap h = JSONUtilities.wrapMap(map);
		
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		if (h != null)
			breedte = h.getInt("breedte");
		if (h != null)
			hoogte = h.getInt("hoogte");
		if (h != null)
			launchState = h.getMap("interactiePanelLaunchState");

		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(webLogoGWTCssResource.dock());
		dlp.setSize("" + breedte + "px", "" + hoogte + "px");


		init(breedte, hoogte, launchState, randomVarWaarden);


	}



	public void init(int width, int height, Map<String,Object> map, Map<String,Number> values) 
	{
		
logger.info("WebLogoGWT uncompiled init");

			//this.breedte = width;
			//this.hoogte = height;
			
			//dlp.setSize("" + breedte + "px", "" + hoogte + "px");
			
			//this.launchState = launchState;
			ObjectMap launchState = JSONUtilities.wrapMap(map);
			
			if (launchState != null && launchState.containsKey("uitvoerVeldZichtbaar")) 
				uitvoerVeldZichtbaar = launchState.getBoolean("uitvoerVeldZichtbaar");
			if (launchState != null && launchState.containsKey("programmaVeldZichtbaar")) 
				programmaVeldZichtbaar = launchState.getBoolean("programmaVeldZichtbaar");
			if (launchState != null && launchState.containsKey("deeltakenZichtbaar"))	
				deeltakenZichtbaar = launchState.getBoolean("deeltakenZichtbaar");
			if (launchState != null && launchState.containsKey("whileLoopZichtbaar")) 
				whileLoopZichtbaar = launchState.getBoolean("whileLoopZichtbaar");
			if (launchState != null && launchState.containsKey("keuzeCommandZichtbaar")) 
				keuzeCommandZichtbaar = launchState.getBoolean("keuzeCommandZichtbaar");
			if (launchState != null && launchState.containsKey("printCommandsZichtbaar")) 
				printCommandsZichtbaar = launchState.getBoolean("printCommandsZichtbaar");
			if (launchState != null && launchState.containsKey("tekenCommandsZichtbaar")) 
				tekenCommandsZichtbaar = launchState.getBoolean("tekenCommandsZichtbaar");
			if (launchState != null && launchState.containsKey("traceZichtbaar")) 
				traceZichtbaar = launchState.getBoolean("traceZichtbaar");
			if (launchState != null && launchState.containsKey("codeIOZichtbaar")) 
				codeIOZichtbaar = launchState.getBoolean("codeIOZichtbaar");			
			
			if (launchState != null && launchState.containsKey("state")) 
				state = (HashMap) launchState.getMap("state");
			
			if (uitvoerVeldZichtbaar && programmaVeldZichtbaar)
			{
				if (deeltakenZichtbaar)
				{
					this.breedte = breedteGroot;
					ubb = ubbGroot;
logger.info("bg = " + breedte);
logger.info("ubb = " + ubb);
				}
				else
				{
					this.breedte = breedteKlein;
					ubb = ubbKlein;
				}
				// hoogte is al gezet
			}
			else if (!uitvoerVeldZichtbaar && programmaVeldZichtbaar)
			{
				if (deeltakenZichtbaar)
				{
					this.breedte = jlsBreedteGroot + 2 * offSet;
				}
				else
				{
					this.breedte = jlsBreedteKlein + 2 * offSet;
				}
				
				// hoogte is al gezet
			}
			else if (uitvoerVeldZichtbaar && !programmaVeldZichtbaar)
			{
				this.breedte = width;
				this.hoogte = height;
				ubb = this.breedte;
				ubh = this.hoogte;
				
			}
			
			// stand-alone
			if (launchState != null && !launchState.containsKey("state"))
			{
				breedte = breedtePaul;
				// hoogte is al gezet
				ubb = ubbPaul;
System.out.println("paul");				
			}
			
			dlp.setSize("" + this.breedte + "px", "" + this.hoogte + "px");
			
			webLogoPanel = new LayoutPanel();
			webLogoPanel.setSize("" + this.breedte + "px", "" + this.hoogte + "px");
			webLogoPanel.addStyleName(webLogoGWTCssResource.bottom());
			
			uitvoerblad = new Tekenblad(this,ubb,ubh);
			Canvas tekenbladCanvas = uitvoerblad.getCanvas();
			if (tekenbladCanvas == null) 
			{
		      RootPanel.get().add(new Label(upgradeMessage));
		      return;
		    }
			
			uitvoerblad.initContext2d();
			
			
			webLogoPanel.add(uitvoerblad);
			if (!uitvoerVeldZichtbaar)
				webLogoPanel.setWidgetVisible(uitvoerblad,false);
			if (uitvoerVeldZichtbaar && programmaVeldZichtbaar && deeltakenZichtbaar)
			{	
				webLogoPanel.setWidgetLeftWidth(uitvoerblad, ubxGroot, Style.Unit.PX, ubb, Style.Unit.PX);
				webLogoPanel.setWidgetTopHeight(uitvoerblad, uby, Style.Unit.PX, ubh, Style.Unit.PX);
			}
			else if (uitvoerVeldZichtbaar && programmaVeldZichtbaar && !deeltakenZichtbaar)
			{
				webLogoPanel.setWidgetLeftWidth(uitvoerblad, ubxKlein, Style.Unit.PX, ubb, Style.Unit.PX);
				webLogoPanel.setWidgetTopHeight(uitvoerblad, uby, Style.Unit.PX, ubh, Style.Unit.PX);
			}
			else if (uitvoerVeldZichtbaar && !programmaVeldZichtbaar)
			{
				webLogoPanel.setWidgetLeftWidth(uitvoerblad, 0, Style.Unit.PX, breedte, Style.Unit.PX);
				webLogoPanel.setWidgetTopHeight(uitvoerblad, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
			}
			
			
			if (deeltakenZichtbaar)
				jlsVeld = new JavaLogoSchuifVeld(0,0,jlsBreedteGroot,jlsHoogte,uitvoerblad);
			else
				jlsVeld = new JavaLogoSchuifVeld(0,0,jlsBreedteKlein,jlsHoogte,uitvoerblad);
			
			Canvas jlsvCanvas = jlsVeld.getCanvas();
			if (jlsvCanvas == null) 
			{
		      RootPanel.get().add(new Label(upgradeMessage));
		      return;
		    }
			
			jlsVeld.initContext2d();
			jlsVeld.initialize();

			if (programmaVeldZichtbaar)
			{	
				webLogoPanel.add(jlsVeld);
				if (deeltakenZichtbaar)
				{	webLogoPanel.setWidgetLeftWidth(jlsVeld, offSet, Style.Unit.PX, jlsBreedteGroot, Style.Unit.PX);
					webLogoPanel.setWidgetTopHeight(jlsVeld, offSet, Style.Unit.PX, jlsHoogte, Style.Unit.PX);
				}	
				else
				{
					webLogoPanel.setWidgetLeftWidth(jlsVeld, offSet, Style.Unit.PX, jlsBreedteKlein, Style.Unit.PX);
					webLogoPanel.setWidgetTopHeight(jlsVeld, offSet, Style.Unit.PX, jlsHoogte, Style.Unit.PX);
				}
			}
			jlsVeld.zetDeeltaken(deeltakenZichtbaar);
			jlsVeld.zetWhileLoopZichtbaar(whileLoopZichtbaar);
			jlsVeld.zetKeuzeCommandZichtbaar(keuzeCommandZichtbaar);
			jlsVeld.zetPrintCommandsZichtbaar(printCommandsZichtbaar);
			jlsVeld.zetTekenCommandsZichtbaar(tekenCommandsZichtbaar);
			
			trb = new TraceBeheerder(uitvoerblad, jlsVeld, this);
			
			bottomPanel = new LayoutPanel();
			bottomPanel.setSize("" + breedte + "px", "" + bottomHeight + "px");
			bottomPanel.addStyleName(webLogoGWTCssResource.bottom());
			
			makeBottom();
			
			if (programmaVeldZichtbaar)
			{	
				webLogoPanel.add(bottomPanel);
				webLogoPanel.setWidgetLeftWidth(bottomPanel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
				webLogoPanel.setWidgetTopHeight(bottomPanel, hoogte-bottomHeight, Style.Unit.PX, bottomHeight, Style.Unit.PX);
			}
			
			
			dlp.add(webLogoPanel);
			
			jlsVeld.paint();
			uitvoerblad.initializeDrawing(false);

			if (state != null)
			{	setState(state);
logger.info("state != null");			
			}
			
			dlp.forceLayout();
			webLogoPanel.forceLayout();
			bottomPanel.forceLayout();
			jlsVeld.forceLayout();
			uitvoerblad.forceLayout();
			
			jlsVeld.paint();			
			
			vartracerWidth = 2*JavaLogoSchuifVeld.ccsw+12;
			vartracerHeight = 515;
			vartracer = new VardisplayPanel(vartracerWidth, vartracerHeight);
			//vartracer.setBounds(JavaLogoSchuifVeld.ccx, JavaLogoSchuifVeld.ccy, 2*JavaLogoSchuifVeld.ccsw+10, 515);
	}
	
	public void	makeBottom()
	{
		int currentX = leftOffset;
		int currentY = topOffset;

		if (codeIOZichtbaar)
		{	
			importButton = new PushButton("import");
			importButton.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(importButton);
			bottomPanel.setWidgetLeftWidth(importButton, currentX, Style.Unit.PX, buttonWidth + 20, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(importButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			importButton.addClickHandler(new PushClickHandler());
		
			currentX += leftOffset + buttonWidth + 20;
		
			exportButton = new PushButton("export");
			exportButton.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(exportButton);
			bottomPanel.setWidgetLeftWidth(exportButton, currentX, Style.Unit.PX, buttonWidth + 20, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(exportButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			exportButton.addClickHandler(new PushClickHandler());
		
			currentX += leftOffset + buttonWidth + 20;
		}

		runButton = new PushButton("run");
		runButton.addStyleName(webLogoGWTCssResource.pushbutton());
		bottomPanel.add(runButton);
		bottomPanel.setWidgetLeftWidth(runButton, currentX, Style.Unit.PX, buttonWidth - 10, Style.Unit.PX);
		bottomPanel.setWidgetTopHeight(runButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		runButton.addClickHandler(new PushClickHandler());
		
		currentX += leftOffset + buttonWidth - 10;
		
		if (traceZichtbaar)
		{
			traceAanKnop = new PushButton("trace aan");
			traceAanKnop.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(traceAanKnop);
			bottomPanel.setWidgetLeftWidth(traceAanKnop, currentX, Style.Unit.PX, buttonWidth + 20, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(traceAanKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			traceAanKnop.addClickHandler(new PushClickHandler());
			
			currentX = leftOffset;
		
			traceUitKnop = new PushButton("trace uit");
			traceUitKnop.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(traceUitKnop);
			bottomPanel.setWidgetLeftWidth(traceUitKnop, currentX, Style.Unit.PX, buttonWidth + 20, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(traceUitKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			traceUitKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(traceUitKnop, false);
	
			currentX += leftOffset + buttonWidth + 20;
			
			beginKnop = new PushButton("begin");
			beginKnop.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(beginKnop);
			bottomPanel.setWidgetLeftWidth(beginKnop, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(beginKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			beginKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(beginKnop, false);
		
			currentX += leftOffset + buttonWidth;
			
			stapKnop = new PushButton("stap");
			stapKnop.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(stapKnop);
			bottomPanel.setWidgetLeftWidth(stapKnop, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(stapKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			stapKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(stapKnop, false);
		
			currentX += leftOffset + buttonWidth;

			terugKnop = new PushButton("terug");
			terugKnop.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(terugKnop);
			bottomPanel.setWidgetLeftWidth(terugKnop, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(terugKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			terugKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(terugKnop, false);
		
			currentX += leftOffset + buttonWidth;

			skipKnop = new PushButton("skip");
			skipKnop.addStyleName(webLogoGWTCssResource.pushbutton());
			bottomPanel.add(skipKnop);
			bottomPanel.setWidgetLeftWidth(skipKnop, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(skipKnop, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			skipKnop.addClickHandler(new PushClickHandler());
			bottomPanel.setWidgetVisible(skipKnop, false);
		
			currentX += leftOffset + buttonWidth;
			
			showVariables = new CheckBox("Toon vars");
			bottomPanel.add(showVariables);
			bottomPanel.setWidgetLeftWidth(showVariables, currentX, Style.Unit.PX, 2 * buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(showVariables, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			showVariables.addValueChangeHandler(new ShowVariablesVCH());
			bottomPanel.setWidgetVisible(showVariables, false);
		
			currentX += leftOffset + 2 * buttonWidth;
			
			// hier nog een label/noneditable TextBox
			methodeLabel = new Label("");
			methodeLabel.addStyleName(webLogoGWTCssResource.label());
			bottomPanel.add(methodeLabel);
			bottomPanel.setWidgetLeftWidth(methodeLabel, currentX, Style.Unit.PX, 4 * buttonWidth, Style.Unit.PX);
			bottomPanel.setWidgetTopHeight(methodeLabel, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			bottomPanel.setWidgetVisible(methodeLabel, false);
			
		}
		


	}

    class PushClickHandler implements ClickHandler
    {
    	
    	public void onClick(ClickEvent e)
    	{
			//e.preventDefault();
			e.stopPropagation();
			
			if (e.getSource() == importButton)
			{
				jlsVeld.importFrame();
			}
			else if (e.getSource() == exportButton)
			{
				jlsVeld.exportFrame(jlsVeld.getCode());
			} 
			else if (e.getSource() == runButton)
			{
				if ((jlsVeld.paramEditor != null) && jlsVeld.paramEditor.isVisible())
					jlsVeld.paramEditor.owner.parameterEdited(jlsVeld.paramEditor.getText());	
				uitvoerblad.paintDrawing(false);
				jlsVeld.paint();
			} 
			else if (e.getSource() == traceAanKnop)
			{
//System.out.println("click traceAan");

				if (codeIOZichtbaar)
				{	bottomPanel.setWidgetVisible(importButton, false);
					bottomPanel.setWidgetVisible(exportButton, false);
					//if ((jlsVeld.exportPopup != null) && jlsVeld.exportPopup.isVisible()) 
				}
				
				bottomPanel.setWidgetVisible(runButton, false);
				
				bottomPanel.setWidgetVisible(traceAanKnop, false);
				bottomPanel.setWidgetVisible(traceUitKnop, true);
				traceAan = true;
				
				bottomPanel.setWidgetVisible(beginKnop, true);
				bottomPanel.setWidgetVisible(stapKnop, true);
				bottomPanel.setWidgetVisible(terugKnop, true);
				bottomPanel.setWidgetVisible(skipKnop, true);
				bottomPanel.setWidgetVisible(showVariables, true);
				bottomPanel.setWidgetVisible(methodeLabel, true);
				methodeLabel.setText("");
				
				trb.traceAanAction();
			} 
			else if (e.getSource() == traceUitKnop)
			{
//System.out.println("click traceUit");

				if (codeIOZichtbaar)
				{	bottomPanel.setWidgetVisible(importButton, true);
					bottomPanel.setWidgetVisible(exportButton, true);
				}
				
				bottomPanel.setWidgetVisible(runButton, true);
				
				bottomPanel.setWidgetVisible(traceAanKnop, true);
				bottomPanel.setWidgetVisible(traceUitKnop, false);
				traceAan = false;
				
				bottomPanel.setWidgetVisible(beginKnop, false);
				bottomPanel.setWidgetVisible(stapKnop, false);
				bottomPanel.setWidgetVisible(terugKnop, false);
				bottomPanel.setWidgetVisible(skipKnop, false);
				bottomPanel.setWidgetVisible(showVariables, false);
				bottomPanel.setWidgetVisible(methodeLabel, false);
				
				showVariables.setValue(false);
				trb.setVartracing(false);
				
				trb.traceUitAction();
				
			}
			else if (e.getSource() == beginKnop)
			{
				methodeLabel.setText("");
				trb.beginAction();
			}
			else if (e.getSource() == stapKnop)
			{
				trb.stapAction();
			}
			else if (e.getSource() == terugKnop)
			{
				trb.terugAction();
			}
			else if (e.getSource() == skipKnop)
			{
				trb.skipAction();
			}
    		
			
    		
    	}
    }
    
	class ShowVariablesVCH implements ValueChangeHandler<Boolean>
	{	//public void actionPerformed(ActionEvent e)
		public void onValueChange(ValueChangeEvent<Boolean> e)
		{
			if (e.getSource() == showVariables) 
			{	trb.setVartracing(showVariables.getValue());
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
		
logger.info("getState");

		HashMap<String, Object> h = new HashMap<String, Object>();
		
		String code = "";
		//int scheidingX = 615;
	
		code = jlsVeld.getCode();
		
//logger.info("code = " + code);		
		//scheidingX = this.scheidingX;
		HashMap<String,Object> inputVars = jlsVeld.getInputVars();
		 
	    h.put("code", code);
	    //h.put("scheidingX", new Integer(scheidingX));
	    h.put("inputVars", inputVars);
    	
		return h;

	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		
logger.info("setState");

		ObjectMap map = JSONUtilities.wrapMap(h);
		
		String code = "";
		//int scheidingX = 615;
		HashMap<String, Object> inputVars = null;
		
		if (map.containsKey("code")) 
			code = map.getString("code");
		
//logger.info("code = " + code);

		//if(h.containsKey("scheidingX")) scheidingX = ((Integer)h.get("scheidingX")).intValue();
		if (map.containsKey("inputVars")) 
			inputVars = (HashMap) map.getMap("inputVars");
		
		if (inputVars != null)
			jlsVeld.setInputVars(inputVars);
		jlsVeld.importeer(code);
		//this.scheidingX = scheidingX;
		//setBounds(getBounds());
		jlsVeld.paint();
		uitvoerblad.paintDrawing(false);
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
		return correct;
	}

	@Override
	public void kijkNa() 
	{		 

	}
	
	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
logger.info("WebLogoGWT setComRoot");		
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());


	}
	
	public void zetMode(int mode)
	{	this.mode = mode;
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
	public int getHeight() 
	{
		return hoogte;
	}

	@Override
	public int getWidth() 
	{
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
