package fi.calculatorgwt.client;


import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.OsDetection;

import fi.calculatorgwt.client.text.Text;

public class CalculatorGWT implements EntryPoint, InteractionStub {
	
	public static Text rb = GWT.create(Text.class);
	private boolean isNederlands = true;
	private String DECIMAL = LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator();
	//protected static Locale language;
	
	private Map<String, Object> launchState;
	
	
	FlowPanel basisPanel;
	
	static final String holderId = "dockholder";
	static final String upgradeMessage = 
			"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	int breedte = 500;
	int hoogte = 300;
	
	//Font theFont, theLargeFont, theSmallFont;
	//FontMetrics theFM, theLargeFM, theSmallFM;
	
	Button[] getalKnop;
	Button plusKnop, minKnop, keerKnop, deelKnop, haakLinksKnop, haakRechtsKnop,
		machtKnop, kwadraatKnop, wortelKnop, eenGedeeldDoorKnop, breukKnop;
	Button pijlLinksKnop, pijlRechtsKnop, insKnop, delKnop, cKnop, kommaKnop, negatiefKnop, 
		ansKnop, isKnop;
	Button sinKnop, cosKnop, tanKnop, invKnop, piKnop;
	Button lnKnop, logKnop, eKnop, nWortelKnop, expKnop;
	
	Label[] leegLabel;
	Label sinInvLabel, cosInvLabel, tanInvLabel, decLabel;
	
	TextBox invoerVeld;
	Label uitvoerVeld;
	
	Grid linkerKnoppen, rechterKnoppen, ondersteKnoppen, bovensteKnoppen;
	//LayoutPanel linkerKnoppen, 
//	LayoutPanel rechterKnoppen, ondersteKnoppen;
	FlowPanel knoppenPanel;//, bovensteKnoppen;
	FlowPanel uitvoerPanel, instellingenPanel;
	
	String s;
	StringBuffer sb = new StringBuffer();
	StringBuffer sb2 = new StringBuffer();
	double rekenGetal;
	int lengteRekenGetal, lengte1, lengte2, lengteBreuk, lengteBreukB;
	double teller, noemer, tellerB, noemerB;
	double uitkomst, eindUitkomst;
	int lengteHaakjesUitdrukking;
	int linksTeller, rechtsTeller;
	
	RadioButton gradenButton, radialenButton;
	
	boolean syntaxError;//, mathError;
	String subString;
	String bewaardeAns;
	int cp = 0;
	boolean nieuweInvoer = false;

	CssColor blauw, oranje, groen, geel, lichtgeel, grijs, donkergrijs, lichtblauw, witblauw;
	
	boolean breuk = false;
	
	int rmMode = 1;
	boolean gradenInstelbaar = true;
	boolean gonioKnoppen = true;
	boolean logaritmeKnoppen = true;
	
	boolean graden = false;
	boolean invers = false;
	boolean insert = true;
	Label invLabel;
	
	//ReplaceCaret replaceCaret;
	//DefaultCaret defaultCaret;
	
	int kc;
	boolean isDesktop;

	public CalculatorGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		
//System.out.println("constructor");
		ObjectMap h = JSONUtilities.wrapMap(map);

		//this.randomVarNamen = randomVarNamen;
		//this.randomVarWaarden = randomVarWaarden;
		
		if (h.containsKey("breedte"))
			breedte = h.getInt("breedte");
		if (h.containsKey("hoogte"))
			hoogte = h.getInt("hoogte");
		if (h.containsKey("interactiePanelLaunchState"))
			launchState = h.getMap("interactiePanelLaunchState");
		basisPanel = new FlowPanel();
		
		

		//RootPanel.get(holderId).add(dlp);
		//RootPanel.get(holderId).addStyleName(doorzienGWTCss.root());
		
		
		//Stub.publish(this);
		init(breedte, hoogte, launchState, randomVarWaarden);
	}
	
	public CalculatorGWT() {
	}

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	
	
	public void onModuleLoad() {
		basisPanel = new FlowPanel();
		
		RootPanel.get(holderId).add(basisPanel); 
		RootPanel.get(holderId).setStyleName("root");
		
		//init(breedte, hoogte, null, null);
		Stub.publish(this);
		
		}
	
	public void initialize()
	{
		
		
//		theFont = new Font("Sansserif", Font.BOLD, 16);
//		theFM = getFontMetrics(theFont);
//		theLargeFont = new Font("Sansserif", Font.BOLD, 14);
//		theLargeFM = getFontMetrics(theLargeFont);
//		theSmallFont = new Font("Sansserif", Font.BOLD, 6);
//		theSmallFM = getFontMetrics(theSmallFont);
		
		basisPanel.setPixelSize(breedte, hoogte);
		basisPanel.getElement().getStyle().setBackgroundColor(CssColor.make(230, 230, 230).toString());
		
		blauw = CssColor.make(130, 180, 255);
		oranje = CssColor.make(255, 170, 80);
		groen = CssColor.make(0, 150, 0);
		lichtblauw = CssColor.make(208, 228, 255);
		witblauw = CssColor.make(245, 250, 255);
		geel = CssColor.make(255, 255, 180);
		lichtgeel = CssColor.make(255, 255, 220);
		grijs = CssColor.make("gray");
		donkergrijs = CssColor.make(98, 98, 98);
		
		int knopBreedte = 0;
		int knopHoogteBasis = 0;
		int knopHoogteNavigatie = 0;
		int knopHoogteExtra = 0;
		if(rmMode < 2)
		{
			int hoogteBovendeel = 75;
			int hoogteGraden = 30;
			int aantalKnoppenRij = 8;
			if(rmMode == 1 && gonioKnoppen && logaritmeKnoppen && gradenInstelbaar)
			{
				//knopBreedte = breedte/8 - 3;
				knopHoogteBasis = (hoogte - hoogteBovendeel - hoogteGraden)/5 - 3;
			}
			else if(rmMode == 1 && gonioKnoppen && gradenInstelbaar)
			{
				//knopBreedte = breedte / 8 - 3;
				knopHoogteBasis = (hoogte - hoogteBovendeel - hoogteGraden)/4 - 3;
			}
			else if(rmMode == 1 && gonioKnoppen && logaritmeKnoppen)
			{
				//knopBreedte = breedte / 8 - 3;
				knopHoogteBasis = (hoogte - hoogteBovendeel)/5 - 3;
			}	
			else if(rmMode == 1)
			{
				//knopBreedte = breedte / 8 - 3;
				knopHoogteBasis = (hoogte - hoogteBovendeel)/4 - 3;
			}
			else
			{
				aantalKnoppenRij = 6;
				//knopBreedte = breedte / 6 - 3;
				knopHoogteBasis = (hoogte - hoogteBovendeel) / 4 - 3;
			}
			knopBreedte = (breedte - aantalKnoppenRij * 2)/aantalKnoppenRij;
			knopHoogteNavigatie = 25;
			knopHoogteExtra = knopHoogteBasis;
		}
		else
		{
			knopBreedte = breedte/10 - 3;//(breedte - 30)/10 - 1;
			knopHoogteBasis = (hoogte - 56)/5;
			knopHoogteNavigatie = knopHoogteBasis;
			knopHoogteExtra = (hoogte - 50) / 6;
		}
		
		getalKnop = new Button[10];
		for(int i = 0; i<getalKnop.length; i++)
			getalKnop[i] = maakButton(""+i, donkergrijs, witblauw, knopBreedte, knopHoogteBasis);
		
		plusKnop = maakButton("+", grijs, witblauw, knopBreedte, knopHoogteBasis);
		minKnop = maakButton("\u2212", grijs, witblauw, knopBreedte, knopHoogteBasis);
		keerKnop = maakButton("\u00D7", grijs, witblauw, knopBreedte, knopHoogteBasis);
		deelKnop = maakButton("\u00F7", grijs, witblauw, knopBreedte, knopHoogteBasis);
		machtKnop = maakButton("^", grijs, witblauw, knopBreedte, knopHoogteExtra);
		kwadraatKnop = maakButton("x\u00B2", grijs, witblauw, knopBreedte, knopHoogteExtra);
		wortelKnop = maakButton("\u221A", grijs, witblauw, knopBreedte, knopHoogteExtra);
		eenGedeeldDoorKnop = maakButton("x\u207B\u00B9", grijs, witblauw, knopBreedte, knopHoogteExtra);
		breukKnop = maakButton("a b/c", grijs, witblauw, knopBreedte, knopHoogteExtra);
		expKnop = maakButton("<html>&times;10<sup><i>x</i></sup></html>", grijs, witblauw, knopBreedte, knopHoogteExtra);
		
		haakLinksKnop = maakButton("(", grijs, witblauw, knopBreedte, knopHoogteBasis);
		haakRechtsKnop = maakButton(")", grijs, witblauw, knopBreedte, knopHoogteBasis);
		
		int kolomBreedte = - 2;
		if(rmMode < 2)
			kolomBreedte = (breedte - 5*2) / 5;
		else
			kolomBreedte = (breedte - 3 * 10) / 10; 
		
		pijlLinksKnop = maakButton("\u25C4", blauw, witblauw, kolomBreedte, knopHoogteNavigatie);
		pijlRechtsKnop = maakButton("\u25BA", blauw, witblauw, kolomBreedte, knopHoogteNavigatie);
		insKnop = maakButton("INS", blauw, witblauw, kolomBreedte, knopHoogteNavigatie);
		delKnop = maakButton("DEL", blauw, witblauw, kolomBreedte, knopHoogteNavigatie);
		cKnop = maakButton("C", blauw, witblauw, kolomBreedte, knopHoogteNavigatie);
		
		if(isNederlands)
			kommaKnop = maakButton(DECIMAL, grijs, witblauw, knopBreedte, knopHoogteBasis);
		else
			kommaKnop = maakButton(".", grijs, witblauw, knopBreedte, knopHoogteBasis);
		negatiefKnop = maakButton("(-)", grijs, witblauw, knopBreedte, knopHoogteBasis);
		ansKnop = maakButton("Ans", grijs, witblauw, knopBreedte, knopHoogteBasis);
		isKnop = maakButton("=", groen, CssColor.make("white"), knopBreedte, knopHoogteBasis);
		
		sinKnop = maakButton("sin", grijs, witblauw, knopBreedte, knopHoogteExtra);
		cosKnop = maakButton("cos", grijs, witblauw, knopBreedte, knopHoogteExtra);
		tanKnop = maakButton("tan", grijs, witblauw, knopBreedte, knopHoogteExtra);
		invKnop = maakButton("INV", geel, donkergrijs, knopBreedte, knopHoogteExtra);
		piKnop = maakButton("\u03C0", grijs, witblauw, knopBreedte, knopHoogteExtra);
		
		logKnop = maakButton("log", grijs, witblauw, knopBreedte, knopHoogteExtra);
		lnKnop = maakButton("ln", grijs, witblauw, knopBreedte, knopHoogteExtra);
		eKnop = maakButton("e", grijs, witblauw, knopBreedte, knopHoogteExtra);
		nWortelKnop = maakButton("\u207F\u221A", grijs, witblauw, knopBreedte, knopHoogteExtra);
		

		leegLabel = new Label[15];
		for(int i = 0; i<15; i++)
			leegLabel[i] = new Label("");
		sinInvLabel = maakLabel("sin\u207B\u00B9", CssColor.make("orange"), knopBreedte);
		cosInvLabel = maakLabel("cos\u207B\u00B9", CssColor.make("orange"), knopBreedte);
		tanInvLabel = maakLabel("tan\u207B\u00B9", CssColor.make("orange"), knopBreedte);
		decLabel = maakLabel("\u2192dec", CssColor.make("orange"), knopBreedte);
		
		knoppenPanel = new FlowPanel();
		
		instellingenPanel = new FlowPanel();
		instellingenPanel.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
		
		String groep =  "groep";
		gradenButton = new RadioButton(groep, rb.gradenButton());
		gradenButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				graden = true;
			}
		});
		gradenButton.getElement().getStyle().setPadding(5, Style.Unit.PX);
		
		
		radialenButton = new RadioButton(groep, rb.radialenButton());
		radialenButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				graden = false;
			}
		});
		radialenButton.setValue(true);
		radialenButton.getElement().getStyle().setPadding(5, Style.Unit.PX);
		
		zetRmMode(rmMode, gradenInstelbaar);
		
		//replaceCaret = new ReplaceCaret();
		//defaultCaret = new DefaultCaret();
		LayoutPanel invoerPanel = new LayoutPanel();
		invoerPanel.setPixelSize(breedte, 25);
		
		
		invoerVeld = new TextBox();
		invoerVeld.setWidth((breedte - 16) + "px");
		invoerVeld.getElement().getStyle().setBackgroundColor(witblauw.toString());
		invoerVeld.getElement().getStyle().setPaddingLeft(10, Style.Unit.PX);
		invoerVeld.getElement().getStyle().setFontSize(14, Style.Unit.PX);
		invoerVeld.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		invoerVeld.getElement().setAttribute("spellCheck", "false");
		invoerVeld.addKeyDownHandler(new RmKeyDownHandler());
		invoerVeld.addKeyPressHandler(new RmKeyPressHandler());
		invoerVeld.addMouseUpHandler(new RmMouseUpHandler());
		
		OsDetection detection = MGWT.getOsDetection();
		isDesktop = detection.isDesktop();
		
		
		
//		//invoerVeld.setCaret(defaultCaret);
//		invoerVeld.getCaret().setBlinkRate(500);
//		invoerVeld.getCaret().setVisible(true);

		invLabel = new Label("I");
		invLabel.getElement().getStyle().setFontSize(6, Style.Unit.PX);
		invLabel.getElement().getStyle().setBackgroundColor("black");
		invLabel.getElement().getStyle().setColor(witblauw.toString());
		invLabel.setVisible(invers);
		invoerPanel.add(invoerVeld);
		invoerPanel.setWidgetLeftRight(invoerVeld, 0, Style.Unit.PX, 0, Style.Unit.PX);
		invoerPanel.setWidgetTopHeight(invoerVeld, 0, Style.Unit.PX, 25, Style.Unit.PX);
		invoerPanel.add(invLabel);
		invoerPanel.setWidgetLeftWidth(invLabel, 2, Style.Unit.PX, 5, Style.Unit.PX);
		invoerPanel.setWidgetTopHeight(invLabel, 2, Style.Unit.PX, 7, Style.Unit.PX);
		
		uitvoerVeld = new Label("0");
		uitvoerVeld.setWidth(breedte - 6 + "px");
		//kan ook naar CSS.
		uitvoerVeld.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		uitvoerVeld.getElement().getStyle().setBackgroundColor(lichtblauw.toString());
		uitvoerVeld.getElement().getStyle().setFontSize(14, Style.Unit.PX);
		uitvoerVeld.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		uitvoerVeld.getElement().getStyle().setPadding(3, Style.Unit.PX);
		
		basisPanel.add(invoerPanel);
		basisPanel.add(uitvoerVeld);

		basisPanel.add(knoppenPanel);
	}
	
	//met strings in plaats van CssColors?
	public Button maakButton(String s, CssColor backGround, CssColor foreGround, int width, int height)
	{
		Button button = new Button(s);
		button.removeStyleName("gwt-Button");
		button.getElement().getStyle().setBackgroundColor(backGround.toString());
		button.getElement().getStyle().setColor(foreGround.toString());
		//kunnen ook naar CSS.
		button.getElement().getStyle().setFontSize(13, Style.Unit.PX);
		button.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		button.setPixelSize(width, height);
		button.addClickHandler(new RmClickHandler());
		return button;
	}
	
	public Label maakLabel(String s, CssColor c, int width)
	{
		Label label = new Label(s);
		label.getElement().getStyle().setColor(c.toString());
		label.getElement().getStyle().setFontSize(14, Style.Unit.PX);
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		label.setWidth(width + "px");
		return label;
	}
	
	public void zetRmMode(int i, boolean b)
	{
		//rmMode = 0: Eenvoudig
		//rmMode = 1: Wetenschappelijk
		//rmMode = 2: Cito
		
		rmMode = i;
		gradenInstelbaar = (b && rmMode == 1);
		if(rmMode == 2)
			invKnop.getElement().getStyle().setBackgroundColor("orange");
		else
			invKnop.getElement().getStyle().setBackgroundColor(geel.toString());
		if(rmMode == 2)
			graden = true;
		else
			graden = false;
		
		if(rmMode < 2)
		{
			bovensteKnoppen = new Grid(1, 5);
			if(rmMode == 1 && gonioKnoppen && logaritmeKnoppen)
			{
				ondersteKnoppen = new Grid(5,8);
			}
			else if(rmMode == 1)
				ondersteKnoppen = new Grid(4, 8);
			else
				ondersteKnoppen = new Grid(4,6);
				
			bovensteKnoppen.setWidget(0, 0, pijlLinksKnop);
			bovensteKnoppen.setWidget(0, 1, pijlRechtsKnop);
			bovensteKnoppen.setWidget(0, 2, insKnop);
			bovensteKnoppen.setWidget(0, 3, delKnop);
			bovensteKnoppen.setWidget(0, 4, cKnop);
			
			knoppenPanel.add(bovensteKnoppen);
			knoppenPanel.add(ondersteKnoppen);
			
			bovensteKnoppen.setCellPadding(1);
			ondersteKnoppen.setCellPadding(1);
		
			if(gradenInstelbaar)
			{	instellingenPanel.add(gradenButton);
				instellingenPanel.add(radialenButton);
				knoppenPanel.add(instellingenPanel);
				instellingenPanel.getElement().getStyle().setTextAlign(TextAlign.LEFT);
				
				
			}
			ondersteKnoppen.setWidget(0, 0, getalKnop[7]);
			ondersteKnoppen.setWidget(0, 1, getalKnop[8]);
			ondersteKnoppen.setWidget(0, 2, getalKnop[9]);
			ondersteKnoppen.setWidget(0, 3, keerKnop);
			ondersteKnoppen.setWidget(0, 4, deelKnop);
			ondersteKnoppen.setWidget(0, 5, wortelKnop);
			if(rmMode == 1)
			{	ondersteKnoppen.setWidget(0, 6, nWortelKnop);
				if(gonioKnoppen)
					ondersteKnoppen.setWidget(0, 7, sinKnop);
				else if(logaritmeKnoppen)
					ondersteKnoppen.setWidget(0, 7, eenGedeeldDoorKnop);
			}
			
			ondersteKnoppen.setWidget(1, 0, getalKnop[4]);
			ondersteKnoppen.setWidget(1, 1, getalKnop[5]);
			ondersteKnoppen.setWidget(1, 2, getalKnop[6]);
			ondersteKnoppen.setWidget(1, 3, plusKnop);
			ondersteKnoppen.setWidget(1, 4, minKnop);
			ondersteKnoppen.setWidget(1, 5, kwadraatKnop);
			if(rmMode == 1)
			{	if(logaritmeKnoppen)
					ondersteKnoppen.setWidget(1, 6, logKnop);
				else
					ondersteKnoppen.setWidget(1, 6, eenGedeeldDoorKnop);
				if(gonioKnoppen)
					ondersteKnoppen.setWidget(1, 7, cosKnop);
				else if(logaritmeKnoppen)
					ondersteKnoppen.setWidget(1, 7, breukKnop);
			}
			
			ondersteKnoppen.setWidget(2, 0, getalKnop[1]);
			ondersteKnoppen.setWidget(2, 1, getalKnop[2]);
			ondersteKnoppen.setWidget(2, 2, getalKnop[3]);
			ondersteKnoppen.setWidget(2, 3, haakLinksKnop);
			ondersteKnoppen.setWidget(2, 4, haakRechtsKnop);
			ondersteKnoppen.setWidget(2, 5, machtKnop);
			if(rmMode == 1)
			{	if(logaritmeKnoppen)
					ondersteKnoppen.setWidget(2, 6, lnKnop);
				else
					ondersteKnoppen.setWidget(2, 6, breukKnop);
				if(gonioKnoppen)
					ondersteKnoppen.setWidget(2, 7, tanKnop);
				else if(logaritmeKnoppen)
					ondersteKnoppen.setWidget(2, 7, expKnop);
				else
					ondersteKnoppen.setWidget(2, 7, invKnop);
			}
			
			ondersteKnoppen.setWidget(3, 0, getalKnop[0]);
			ondersteKnoppen.setWidget(3, 1, kommaKnop);
			ondersteKnoppen.setWidget(3, 2, negatiefKnop);
			ondersteKnoppen.setWidget(3, 3, piKnop);
			int k = 4;
			if(rmMode == 1 && logaritmeKnoppen)
			{	ondersteKnoppen.setWidget(3, k, eKnop);
				k++;
			}
			ondersteKnoppen.setWidget(3, k, ansKnop);
			k++;
			if(rmMode == 1 && !logaritmeKnoppen)
			{	ondersteKnoppen.setWidget(3, k, expKnop);
				k++;
			}
			ondersteKnoppen.setWidget(3, k, isKnop);
			if(rmMode == 1 && (gonioKnoppen || logaritmeKnoppen))
				ondersteKnoppen.setWidget(3, 7, invKnop);
			if(rmMode == 1)
			{	if(gonioKnoppen && logaritmeKnoppen)
				{
					ondersteKnoppen.setWidget(4, 0, eenGedeeldDoorKnop);
					ondersteKnoppen.setWidget(4, 1, breukKnop);
					ondersteKnoppen.setWidget(4, 2, expKnop);
				}
			}
			
		}
		else
		{	
			linkerKnoppen = new Grid(4, 5);
			rechterKnoppen = new Grid(5, 5);
			linkerKnoppen.setCellPadding(0);
			rechterKnoppen.setCellPadding(0);
			linkerKnoppen.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			rechterKnoppen.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			linkerKnoppen.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
			rechterKnoppen.getElement().getStyle().setPaddingLeft(5, Style.Unit.PX);
			knoppenPanel.add(linkerKnoppen);
			knoppenPanel.add(rechterKnoppen);
			
			linkerKnoppen.setWidget(0, 0, invKnop);
			int tussenRuimte = 20;
			sinInvLabel.getElement().getStyle().setPaddingTop(tussenRuimte, Style.Unit.PX);
			cosInvLabel.getElement().getStyle().setPaddingTop(tussenRuimte, Style.Unit.PX);
			tanInvLabel.getElement().getStyle().setPaddingTop(tussenRuimte, Style.Unit.PX);
			decLabel.getElement().getStyle().setPaddingTop(tussenRuimte, Style.Unit.PX);
			
			linkerKnoppen.setWidget(1, 0, sinInvLabel);
			linkerKnoppen.setWidget(1, 1, cosInvLabel);
			linkerKnoppen.setWidget(1, 2, tanInvLabel);
			linkerKnoppen.setWidget(1, 3, decLabel);
			
			linkerKnoppen.setWidget(2, 0, sinKnop);
			linkerKnoppen.setWidget(2, 1, cosKnop);
			linkerKnoppen.setWidget(2, 2, tanKnop);
			linkerKnoppen.setWidget(2, 3, breukKnop);
			linkerKnoppen.setWidget(2, 4, expKnop);
			
			linkerKnoppen.setWidget(3, 0, wortelKnop);
			linkerKnoppen.setWidget(3, 1, kwadraatKnop);
			linkerKnoppen.setWidget(3, 2, machtKnop);
			linkerKnoppen.setWidget(3, 3, eenGedeeldDoorKnop);
			linkerKnoppen.setWidget(3, 4, piKnop);
		
			rechterKnoppen.setWidget(0, 0, pijlLinksKnop);
			rechterKnoppen.setWidget(0, 1, pijlRechtsKnop);
			rechterKnoppen.setWidget(0, 2, insKnop);
			rechterKnoppen.setWidget(0, 3, delKnop);
			rechterKnoppen.setWidget(0, 4, cKnop);
			
			rechterKnoppen.setWidget(1, 0, getalKnop[7]);
			rechterKnoppen.setWidget(1, 1, getalKnop[8]);
			rechterKnoppen.setWidget(1, 2, getalKnop[9]);
			rechterKnoppen.setWidget(1, 3, keerKnop);
			rechterKnoppen.setWidget(1, 4, deelKnop);
			
			rechterKnoppen.setWidget(2, 0, getalKnop[4]);
			rechterKnoppen.setWidget(2, 1, getalKnop[5]);
			rechterKnoppen.setWidget(2, 2, getalKnop[6]);
			rechterKnoppen.setWidget(2, 3, plusKnop);
			rechterKnoppen.setWidget(2, 4, minKnop);
			
			rechterKnoppen.setWidget(3, 0, getalKnop[1]);
			rechterKnoppen.setWidget(3, 1, getalKnop[2]);
			rechterKnoppen.setWidget(3, 2, getalKnop[3]);
			rechterKnoppen.setWidget(3, 3, haakLinksKnop);
			rechterKnoppen.setWidget(3, 4, haakRechtsKnop);
			
			rechterKnoppen.setWidget(4, 0, getalKnop[0]);
			rechterKnoppen.setWidget(4, 1, kommaKnop);
			rechterKnoppen.setWidget(4, 2, negatiefKnop);
			rechterKnoppen.setWidget(4, 3, ansKnop);
			rechterKnoppen.setWidget(4, 4, isKnop);
		}
	}

	@Override
	public HashMap<String, Object> getState() {
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		
	}

	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public Boolean isCorrect() {
		return Boolean.TRUE;
	}

	@Override
	public void kijkNa() {
		
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
	}

	@Override
	public Widget asWidget() {
		return basisPanel;
	}

	@Override
	public int getAsHoogte() {
		return 17; //standaardwaarde, moet eigenlijk gebaseerd zijn op fontsize van omvattende TekstVakPanel.
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
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width;
		hoogte = height;
		
		ObjectMap h = JSONUtilities.wrapMap(launchData);
		
		if (launchData != null)
		{	
			if (h.containsKey("rmMode")) 
				rmMode = h.getInt("rmMode");
			if (h.containsKey("gradenInstelbaar"))
				gradenInstelbaar = h.getBoolean("gradenInstelbaar");
			if (h.containsKey("gonioKnoppen"))
				gonioKnoppen = h.getBoolean("gonioKnoppen");
			if (h.containsKey("logaritmeKnoppen"))
				logaritmeKnoppen = h.getBoolean("logaritmeKnoppen");
		}
		//TODO: boolean isNederlands juiste waarde geven afhankelijk van taal profiel.
		initialize();
		
	}
	
	public void maakBerekenbaar(String s)
	{
		sb.delete(0, sb.length());
		sb.append(s);
		
		//Alle kwadraten veranderen in ^2
		for(int i = 0; i < sb.length(); i++)
			if(sb.charAt(i) == '\u00B2')
				sb.replace(i, i+1, "^2");
		
		//Alle ^(-1) goed schrijven
		for(int i = 0; i < sb.length()-1; i++)
			if(sb.charAt(i) == '\u207B')
				sb.replace(i, i+2, "^-1");
		
		//Alle *10^x goed schrijven
		for(int i = 0; i < sb.length()-1; i++)
			if(sb.charAt(i) == '\u2081')
			{	if(sb.length() < i + 3)
					syntaxError = true;
				else if(sb.charAt(i+2) == '-' || sb.charAt(i+2) == '\u2212')
					sb.replace(i, i+3, "G");
				else
					sb.replace(i, i+2, "E");
			}
		
		//Zorgen dat voor en na elke komma getallen staan
		for(int i = 0; i < sb.length(); i++)
			if(sb.charAt(i) == ',')
			{	if(i == 0)
					sb.insert(0, '0');
				else if(!Character.isDigit(sb.charAt(i-1)))
					sb.insert(i, '0');
				if(i == sb.length()-1)
					sb.append('0');
				else if(!Character.isDigit(sb.charAt(i+1)))
					sb.insert(i+1, '0');
			}
		
		//Alle komma's veranderen in punten
		for(int i = 0; i< sb.length(); i++)
			if(sb.charAt(i) == ',')
				sb.setCharAt(i, '.');
		
		//Aantal linker- en rechterhaakjes kloppend maken
		berekenTellers(sb);
		if(rechtsTeller > linksTeller)
			syntaxError = true;
		else if(linksTeller > rechtsTeller)
			for(int i = 0; i < linksTeller - rechtsTeller; i++)
			{	sb.append(')');
				setText(invoerVeld.getText()+")");
			}
		
		//Maaltekens invoegen waar nodig
		for(int i = 1; i < sb.length(); i++)
			if(sb.charAt(i) == '\u03C0' || sb.charAt(i) == 'e' || sb.charAt(i) == '(' || sb.charAt(i)=='\u221A'||sb.charAt(i) == 'A')
			{	if(sb.charAt(i-1)==')' || sb.charAt(i-1) == '\u03C0' || sb.charAt(i-1) == 'e' || Character.isDigit(sb.charAt(i-1)))
					sb.insert(i, 'x');
				if(sb.charAt(i-1) == 's' && sb.charAt(i-2) == 'n')
					sb.insert(i, 'x');
			}	
		
		//SyntaxErrors voor getal na pi, Ans en haakje sluiten
		for(int i = 0; i < sb.length() - 1; i++)
			if(sb.charAt(i) == '\u03C0' || sb.charAt(i) == 'e' || sb.charAt(i) == ')' || sb.charAt(i) == 's')
				if(Character.isDigit(sb.charAt(i+1)))
					syntaxError = true;
				
		if(syntaxError)
		{	return;
		}				
				
		//Ans invullen
		for(int i = 0; i < sb.length(); i++)
			if(sb.charAt(i) == 'A')
				sb.replace(i, i+3, bewaardeAns);
	}
	
	/*
	 * De berekenmethode; berekent wat er in de stringbuffer staat. Regelt
	 * gonioformules, logaritmes en haakjes zelf, besteedt de rest uit.
	 */
	
	public void bereken(StringBuffer sb)
	{
		//alle minnen hetzelfde maken, en alle keertekens en gedeeld-doortekens snel leesbaar maken
		replace(sb, "\u2212", "-");
		replace(sb, "\u00F7", "/");
		replace(sb, "\u00D7", "x");
		
		//++ veranderen in +, etc
		replace(sb, "++", "+");
		replace(sb, "+-", "-");
		replace(sb, "--", "+");
		replace(sb, "-+", "-");
		replace(sb, "x+", "x");
		replace(sb, "/+", "/");
		
		//checken of er breuktekentjes of B's in staan
		if(sb.indexOf("\u22A5") > -1 || sb.indexOf("B") > -1)
			breuk = true;	
				
		//goniofuncties uitrekenen
		for(int i = 0; i < sb.length() - 1; i++)
			if(sb.charAt(i) == 's' && sb.charAt(i+1)== 'i')
			{	if(sb.charAt(i+3) == '(')
				{	vindHaakjesUitdrukking(sb, i + 3);
					if(breuk)
					{	if(graden)
						{	teller = teller * Math.PI;
							noemer = noemer * 180;
						}	
						sb.replace(i, i + lengteHaakjesUitdrukking + 3, 
							Double.toString(Math.sin(teller/noemer)));
					}
					else	
					{	if(graden)
							uitkomst = uitkomst * Math.PI / 180;
						sb.replace(i, i + lengteHaakjesUitdrukking + 3,
							Double.toString(Math.sin(uitkomst)));
					}
				}
				else //arcsin
				{	vindHaakjesUitdrukking(sb, i + 7);
					if(breuk)
					{	if(graden)
						{	teller = teller * Math.PI;
							noemer = noemer * 180;
						}
						sb.replace(i, i + lengteHaakjesUitdrukking + 7,
							Double.toString(Math.asin(teller/noemer)));
					}
					else
					{	uitkomst = Math.asin(uitkomst);
						if(graden)
							uitkomst = uitkomst * 180 / Math.PI;
						sb.replace(i, i + lengteHaakjesUitdrukking + 7,
							Double.toString(uitkomst));
					}
				}
			}
		for(int i = 0; i < sb.length()-1; i++)
			if(sb.charAt(i) == 'c' && sb.charAt(i+1) == 'o')
			{	if(sb.charAt(i+3) == '(')
				{	vindHaakjesUitdrukking(sb, i + 3);
					if(breuk)
					{	if(graden)
						{	teller = teller * Math.PI;
							noemer = noemer * 180;
						}
						sb.replace(i, i + lengteHaakjesUitdrukking + 3,
							Double.toString(Math.cos(teller/noemer)));
					}
					else
					{	if(graden)
							uitkomst = uitkomst * Math.PI / 180;
						sb.replace(i, i + lengteHaakjesUitdrukking + 3,
							Double.toString(Math.cos(uitkomst)));
					}
				}
				else //arccos
				{	
					vindHaakjesUitdrukking(sb, i + 7);
					if(breuk)
					{	if(graden)
						{	teller = teller * Math.PI;
							noemer = noemer * 180;
						}
						sb.replace(i, i + lengteHaakjesUitdrukking + 7,
							Double.toString(Math.acos(teller/noemer)));
					}
					else
					{	uitkomst = Math.acos(uitkomst);
						if(graden)
							uitkomst = uitkomst * 180 / Math.PI;
						sb.replace(i, i + lengteHaakjesUitdrukking + 7,
							Double.toString(uitkomst));
					}
				}
			}
		for(int i = 0; i < sb.length()-1; i++)
			if(sb.charAt(i) == 't' && sb.charAt(i+1) == 'a')
			{	if(sb.charAt(i+3) == '(')
				{	vindHaakjesUitdrukking(sb, i + 3);
					if(breuk)
					{	if(graden)
						{	teller = teller * Math.PI;
							noemer = noemer * 180;
						}
						sb.replace(i, i + lengteHaakjesUitdrukking + 3,
							Double.toString(Math.tan(teller/noemer)));
					}
					else
					{	if(graden)
							uitkomst = uitkomst * Math.PI / 180;
						sb.replace(i, i + lengteHaakjesUitdrukking + 3,
								Double.toString(Math.tan(uitkomst)));
					}
					
				}
				else //arctan
				{	vindHaakjesUitdrukking(sb, i + 7);
					if(breuk)
					{	if(graden)
						{	teller = teller * Math.PI;
							noemer = noemer * 180;
						}
						sb.replace(i, i + lengteHaakjesUitdrukking + 7,
							Double.toString(Math.atan(teller/noemer)));
					}
					else	
					{	uitkomst = Math.atan(uitkomst);
						if(graden)
							uitkomst = uitkomst * 180 / Math.PI;
						sb.replace(i, i + lengteHaakjesUitdrukking + 7,
							Double.toString(uitkomst));
					}
				}
			}
		
		//logfuncties uitrekenen
		for(int i = 0; i < sb.length() - 1; i++)
			if(sb.charAt(i) == 'l' && sb.charAt(i+1)== 'o')//log
			{	vindHaakjesUitdrukking(sb, i + 3);
				if(breuk)
					sb.replace(i, i + lengteHaakjesUitdrukking + 3,
							Double.toString(Math.log10(teller/noemer)));
				else
					sb.replace(i, i + lengteHaakjesUitdrukking + 3,
							Double.toString(Math.log10(uitkomst)));
			}	
		for(int i = 0; i < sb.length() - 1; i++)
			if(sb.charAt(i) == 'l' && sb.charAt(i+1)== 'n')//ln
			{	vindHaakjesUitdrukking(sb, i + 2);
				if(breuk)
					sb.replace(i, i + lengteHaakjesUitdrukking + 2,
							Double.toString(Math.log(teller/noemer)));
				else
					sb.replace(i, i + lengteHaakjesUitdrukking + 2,
							Double.toString(Math.log(uitkomst)));
			}	
		
		//op zoek naar machten
		sb = vindMachten(sb);
			
		
		//haakjes wegwerken (met een while statement, zolang er nog ) zijn.
		berekenTellers(sb);
		String substring1;
		while(rechtsTeller > 0)
		{
			try
			{	int eindpunt = sb.indexOf(")");		
				int beginpunt = sb.substring(0,eindpunt).lastIndexOf("(");
				substring1 = sb.substring(beginpunt+1,eindpunt);				
				berekenWaarde(substring1);
				sb.replace(beginpunt, eindpunt+1, sb2.toString());
			}
			catch(Exception e){
				syntaxError = true;
			}
			rechtsTeller--;
		}
		
		try{
			berekenWaarde(sb.toString());
			sb.replace(0, sb.length(), sb2.toString());
		}
		catch(Exception e)
		{ syntaxError = true;
		}
		
	}
	
	public StringBuffer vindMachten(StringBuffer sb)
	{ 
		if(breuk)
			while(sb.indexOf("^") != -1 && breuk)
			{	vindUitkomstMachtBreuk("^", sb);		
				sb.replace(vindIndex("^", sb) - lengte1, vindIndex("^", sb) + lengte2 + 1, teller + "B" + noemer);
				if(syntaxError)
				{	return sb;
				}
			}
		else
			while(sb.indexOf("^") != -1)
			{	double rekenKind1, rekenKind2;
				if(sb.indexOf("^") > 0 && sb.charAt(sb.indexOf("^") - 1) == ')')
				{	int index = sb.indexOf("^") - 1;
					int haakjesTeller = 1;
					while(index > 0 && haakjesTeller > 0)
					{	index--;
						if(sb.charAt(index) == '(')
							haakjesTeller--;
						else if(sb.charAt(index) == ')')
							haakjesTeller++;
					}
					vindHaakjesUitdrukking(sb, index);
					rekenKind1 = uitkomst;
					lengte1 = lengteHaakjesUitdrukking; //hier stond +1
				}
				else
				{	vindGetalVoorBewerking(vindIndex("^", sb), sb, false);
					rekenKind1 = rekenGetal;
					lengte1 = lengteRekenGetal;
				}
				if(syntaxError)
				{	return sb;
				}
				if(sb.indexOf("^") < sb.length() - 1 && sb.charAt(sb.indexOf("^") + 1) == '(')
				{	int lengte1 = this.lengte1;
					vindHaakjesUitdrukking(sb, sb.indexOf("^") + 1);
					this.lengte1 = lengte1;
					rekenKind2 = uitkomst;
					lengte2 = lengteHaakjesUitdrukking;
				}
				else
				{	vindGetalNaBewerking(vindIndex("^", sb), sb);
					rekenKind2 = rekenGetal;
					lengte2 = lengteRekenGetal;
				}
				if(syntaxError)
				{	return sb;
				}
				uitkomst = Math.pow(rekenKind1, rekenKind2);
				if(Double.isNaN(uitkomst))
				{	uitkomst = -Math.pow(-rekenKind1, rekenKind2);
					double test = Math.pow(uitkomst, 1/rekenKind2);
					if(Math.abs(test - rekenKind1) > 0.000000001)
						uitkomst = Double.NaN;
				}
				sb.replace(vindIndex("^", sb)-lengte1, vindIndex("^", sb)+lengte2+1, Double.toString(uitkomst));
				//vervangUitkomst("^", sb);
				if(syntaxError)
				{	return sb;
				}
			}
		return sb;
	}
	
	/*
	 * berekenWaarde berekent de waarde van een expressie waarin geen haakjes, ans, logaritmes en
	 * gonio-formules voorkomen.
	 */
	public void berekenWaarde(String str) 
	{		
		sb2.delete(0, sb2.length());
		sb2.append(str);
	
		//op zoek naar wortels
		if(breuk)
		{	while(sb2.indexOf("\u221A") != -1)
			{	vindBBreukVanaf(sb2.indexOf("\u221A"), sb2);
				if(syntaxError)
				{	return;
				}
				if(sb2.indexOf("\u221A") == 0 || sb2.charAt(sb2.indexOf("\u221A")-1) != '\u207F')
				{	
					//testen of de teller en de noemer geheel zijn.
					//in dat geval breuk vereenvoudigen en dan pas wortels nemen, om te kunnen zien of ze geheel zijn.
					if(tellerB - (int) tellerB == 0 && noemerB - (int) noemerB == 0)
					{	teller = Math.sqrt(simplify((int) tellerB, (int) noemerB)[0]);
						noemer = Math.sqrt(simplify((int) tellerB, (int) noemerB)[1]);
					}
					else
					{	teller = Math.sqrt(tellerB);
						noemer = Math.sqrt(noemerB);
					}
					lengteRekenGetal = lengteBreukB;
					if(noemer == 1)
						sb2.replace(sb2.indexOf("\u221A"), sb2.indexOf("\u221A") + lengteBreukB + 1, "" + teller);
					else
						sb2.replace(sb2.indexOf("\u221A"), sb2.indexOf("\u221A") + lengteBreukB + 1, teller + "B" + noemer);
				}
				else
				{	teller = tellerB;
					noemer = noemerB;
					int lengte1 = lengteBreukB;
					vindBBreukTot(sb2.indexOf("\u221A") - 1, sb2, true);
					if(syntaxError)
					{	return;
					}
					sb2.replace(sb2.indexOf("\u221A") - 1 - lengteBreukB, sb2.indexOf("\u221A") + lengte1 + 1, teller + "B" + noemer + "^"+noemerB + "B"+tellerB);
				}
			}
		}
		else
		{	while(sb2.indexOf("\u221A") != -1)
			{	vindGetalNaBewerking(sb2.indexOf("\u221A"), sb2);
				if(syntaxError)
				{	return;
				}
				if(sb2.indexOf("\u221A") == 0 || sb2.charAt(sb2.indexOf("\u221A")-1) != '\u207F')
				{	rekenGetal = Math.sqrt(rekenGetal);
					sb2.replace(sb2.indexOf("\u221A"), sb2.indexOf("\u221A") + lengteRekenGetal + 1, 
						Double.toString(rekenGetal));
				}
				else
				{	double rekenKind1 = rekenGetal;
					int lengte1 = lengteRekenGetal;
					vindGetalVoorBewerking(sb2.indexOf("\u207F"), sb2, true);
					if(syntaxError)
					{	return;
					}
					rekenGetal = 1/rekenGetal;
					sb2.replace(sb2.indexOf("\u221A") - 1 - lengteRekenGetal, sb2.indexOf("\u221A") + lengte1 + 1, rekenKind1 + "^" + rekenGetal);
				}
			}
		}
		
		vindMachten(sb2);

		//op zoek naar producten en delingen
		if(breuk)
		{	while(sb2.indexOf("x") != -1 || sb2.indexOf("/") != -1)
			{
				if(sb2.indexOf("/") == -1) 
					vervangUitkomstBreuk("x", sb2);
				else if(sb2.indexOf("x") == -1)
					vervangUitkomstBreuk("/", sb2);
				else if(sb2.indexOf("x") < sb2.indexOf("/"))
					vervangUitkomstBreuk("x", sb2);
				else 
					vervangUitkomstBreuk("/", sb2);
				
				if(syntaxError)
				{	return;
				}
			}
		}
		else
		{	while(sb2.indexOf("x") != -1 || sb2.indexOf("/") != -1)
			{
				if(sb2.indexOf("/") == -1) 
					vervangUitkomst("x", sb2);
				else if(sb2.indexOf("x") == -1)
					vervangUitkomst("/", sb2);
				else if(sb2.indexOf("x") < sb2.indexOf("/"))
					vervangUitkomst("x", sb2);
				else 
					vervangUitkomst("/", sb2);
			
				if(syntaxError)
				{	return;
				}
			}
		}
		
		//E- veranderen in G om problemen met mintekens te voorkomen
		replace(sb2, "E-", "G");
		
		//op zoek naar optellen en aftrekken
		if(breuk)
			while(vindIndex("+", sb2) != -1 || vindIndex("-", sb2) != -1)
			{	if(vindIndex("-", sb2) == -1) 				
					vervangUitkomstBreuk("+", sb2);
				else if(vindIndex("+", sb2) == -1)
					vervangUitkomstBreuk("-", sb2);
				else if(vindIndex("+", sb2) < vindIndex("-", sb2))
					vervangUitkomstBreuk("+", sb2);
				else 
					vervangUitkomstBreuk("-", sb2);			
				if(syntaxError)
				{	return;
				}
			}
		else
			while(vindIndex("+", sb2) != -1 || vindIndex("-", sb2) != -1)
			{	if(vindIndex("-", sb2) == -1) 
					vervangUitkomst("+", sb2);
				else if(vindIndex("+", sb2) == -1)
					vervangUitkomst("-", sb2);
				else if(vindIndex("+", sb2) < vindIndex("-", sb2))
					vervangUitkomst("+", sb2);
				else 
					vervangUitkomst("-", sb2);			
				if(syntaxError)
				{	return;
				}
			}
		
		if(breuk)
		{	try
			{	vindBreukVanaf(-1, sb2);
				sb2.delete(0, sb2.length());
				sb2.append(teller + "B" + noemer);
			}
			catch(Exception e)
			{	syntaxError = true;
			}
		}
		else
			try{	
				uitkomst = Double.parseDouble(sb2.toString());
			}
			catch(Exception e)
			{	
				if(sb2.toString().equals("\u03C0"))
				{	uitkomst = Math.PI;
					sb2.delete(0, sb2.length());
					sb2.append(uitkomst);
				}
				else if(sb2.toString().equals("e"))
				{	uitkomst = Math.E;
					sb2.delete(0, sb2.length());
					sb2.append(uitkomst);
				}
				else if(sb2.indexOf("E") > -1)//een zeer groot getal
					try{
						//vindUitkomst("E", sb2);
						vervangUitkomst("E", sb2);
					}
					catch(Exception ex){
					syntaxError = true;
					}
				else if(sb2.indexOf("G") > -1)//een zeer klein getal
					try{
						//vindUitkomst("G", sb2);
						vervangUitkomst("G", sb2);
					}
					catch(Exception ex){
					syntaxError = true;
					}	
				else
				{	syntaxError = true;
				}
			}
	}
	
	/*
	 * Haakjestellers berekenen; kijken of er evenveel haakjes links als rechts zijn.
	 */
	public void berekenTellers(StringBuffer sb)
	{
		linksTeller = 0;
		rechtsTeller = 0;
		for(int i = 0; i < sb.length(); i++)
			if(sb.charAt(i) == '(')
				linksTeller++;
		for(int i = 0; i < sb.length(); i++)
			if(sb.charAt(i) == ')')
				rechtsTeller++;
	}
	
	/*
	 * Voor het vervangen van symbolen, om de string beter te kunnen verwerken.
	 */
	public void replace(StringBuffer sb, String s1, String s2)
	{
		while(sb.indexOf(s1) != -1)
		{	sb.replace(sb.indexOf(s1), sb.indexOf(s1)+s1.length(), s2);
		}
	}
	
	/*
	 * Uitkomst van 'simpele' bewerkingen berekenen:
	 * +, -, *, /
	 */
	public void vindUitkomst(String s, StringBuffer sb)
	{
		vindGetalVoorBewerking(vindIndex(s, sb), sb, true);
		if(syntaxError)
		{	return;
		}
		double rekenKind1 = rekenGetal;
		
		lengte1 = lengteRekenGetal;
		vindGetalNaBewerking(vindIndex(s, sb), sb);
		if(syntaxError)
		{	return;
		}
		double rekenKind2 = rekenGetal;
		lengte2 = lengteRekenGetal;
		if(s.equals("+"))
			uitkomst = rekenKind1 + rekenKind2;
		else if(s.equals("-"))
			uitkomst = rekenKind1 - rekenKind2;
		else if(s.equals("x"))
			uitkomst = rekenKind1 * rekenKind2;
		else if(s.equals("/"))
			uitkomst = rekenKind1/rekenKind2;
		else if(s.equals("^"))
			uitkomst = Math.pow(rekenKind1,rekenKind2);
		else if(s.equals("E"))
			uitkomst = rekenKind1 * Math.pow(10, rekenKind2);
		else if(s.equals("G"))
			uitkomst = rekenKind1 * Math.pow(10, -rekenKind2);
	}
	
	public void vervangUitkomst(String s, StringBuffer sb)
	{
		vindUitkomst(s, sb);		
		sb.replace(vindIndex(s, sb)-lengte1, vindIndex(s, sb)+lengte2+1, Double.toString(uitkomst));
	}
	
	public void vindGetalVoorBewerking(int pos, StringBuffer sb, boolean minteken)
	{	
		int beginPos = pos-1;
		try{
			if(sb.charAt(pos-1) == '.')
			{	sb.deleteCharAt(pos-1);
				pos--;
			}
			
			if(Character.isDigit(sb.charAt(pos-1)))
			{	while(beginPos >= 0 && Character.isDigit(sb.charAt(beginPos)))
					beginPos --;
				//doet het ��n keer te vaak:
				beginPos++;
				
				if(beginPos != 0 && sb.charAt(beginPos-1)=='.')
				{	beginPos = beginPos-2;
					while(beginPos >= 0 && Character.isDigit(sb.charAt(beginPos)))
						beginPos--;
					beginPos++;
				}	
				
				subString = sb.substring(beginPos, pos);
				rekenGetal = Double.parseDouble(subString);
				lengteRekenGetal = subString.length();				
				if(beginPos != 0 && sb.charAt(beginPos-1) == '-' && minteken)
					if(beginPos == 1 || sb.charAt(beginPos - 2) == '^'
							|| sb.charAt(beginPos - 2) == 'x' || sb.charAt(beginPos - 2) == '/' 
								|| sb.charAt(beginPos - 2) == '(' || sb.charAt(beginPos - 2) == 'E')
					{	rekenGetal = -rekenGetal;
						lengteRekenGetal++;
						beginPos--;
					}	
			}
			else if(sb.charAt(pos - 1) == '\u03C0')//dit is pi
			{	rekenGetal = Math.PI;
				lengteRekenGetal = 1;
				if(pos > 0 && sb.charAt(pos - 1) == '-')
					if(pos == 1 || sb.charAt(pos - 2) == '^'
						|| sb.charAt(pos - 2) == 'x' || sb.charAt(pos - 2) == '/' 
							|| sb.charAt(pos - 2) == '(' || sb.charAt(beginPos - 2) == 'E')
					{	rekenGetal = -rekenGetal;
						lengteRekenGetal++;
						beginPos--;
					}		
			}
			else if(sb.charAt(pos - 1) == 'e') //dan moet er wel e staan
			{	rekenGetal = Math.E;
				lengteRekenGetal = 1;
				if(pos > 0 && sb.charAt(pos - 1) == '-')
					if(pos == 1 || sb.charAt(pos - 2) == '^'
						|| sb.charAt(pos - 2) == 'x' || sb.charAt(pos - 2) == '/' 
							|| sb.charAt(pos - 2) == '(' || sb.charAt(beginPos - 2) == 'E')
					{	rekenGetal = -rekenGetal;
						lengteRekenGetal++;
						beginPos--;
					}	
			}				
			else
			{	syntaxError = true;
			}
			
		}
		catch(Exception e){
			syntaxError = true;
		}
		if(beginPos != 0 && sb.charAt(beginPos-1) == 'E') 
		{
			int beginPos2 = beginPos - 2;
			beginPos = beginPos2;
			while(beginPos >= 0 && Character.isDigit(sb.charAt(beginPos)))
				beginPos--;
			beginPos++;
			
			if(beginPos != 0 && sb.charAt(beginPos-1)=='.')
			{	beginPos = beginPos-2;
				while(beginPos >= 0 && Character.isDigit(sb.charAt(beginPos)))
					beginPos--;
				beginPos++;
			}	
			
			subString = sb.substring(beginPos, beginPos2+1);
			double rekenGetal2 = Double.parseDouble(subString);
			rekenGetal = rekenGetal2*Math.pow(10, rekenGetal);
			lengteRekenGetal = lengteRekenGetal + subString.length() + 1;
		}
		if(beginPos != 0 && sb.charAt(beginPos-1) == 'G') 
		{
			int beginPos2 = beginPos - 2;
			beginPos = beginPos2;
			while(beginPos >= 0 && Character.isDigit(sb.charAt(beginPos)))
				beginPos--;
			beginPos++;
			
			if(beginPos != 0 && sb.charAt(beginPos-1)=='.')
			{	beginPos = beginPos-2;
				while(beginPos >= 0 && Character.isDigit(sb.charAt(beginPos)))
					beginPos--;
				beginPos++;
			}	
			
			subString = sb.substring(beginPos, beginPos2+1);
			double rekenGetal2 = Double.parseDouble(subString);
			
			rekenGetal = rekenGetal2*Math.pow(10, -rekenGetal);
			lengteRekenGetal = lengteRekenGetal + subString.length() + 1;
		}
	}
	
	public void vindGetalNaBewerking(int pos, StringBuffer sb)
	{
		boolean negatief = false;
		try
		{	if(sb.charAt(pos+1) == '.')
			{	sb.insert(pos+1,'0');
			}
		
			if(sb.charAt(pos+1) == '-')
			{
				pos++;
				negatief = true;
			}
			int eindPos = pos + 1;	
			if(Character.isDigit(sb.charAt(pos+1)))//geval dat er een getal na de bewerking staat
			{	
				
				//int eindPos = pos+1;
				while(eindPos <= sb.length()-1 && Character.isDigit(sb.charAt(eindPos)))
					eindPos ++;
				//doet het ��n keer te vaak:
				eindPos--;
								
				if(eindPos < sb.length()-1 && sb.charAt(eindPos+1)=='.')
				{	eindPos = eindPos+2;
					while(eindPos <= sb.length() - 1 && Character.isDigit(sb.charAt(eindPos)))
						eindPos ++;
					eindPos--;
				}
				subString = sb.substring(pos + 1, eindPos + 1);
				rekenGetal = Double.parseDouble(subString);
				lengteRekenGetal = subString.length();
					
			}
			else if(sb.charAt(pos + 1) == '\u03C0')//dit is pi
			{	eindPos = pos + 1;//klopt dit??
				rekenGetal = Math.PI;
				lengteRekenGetal = 1;
			}
			else if(sb.charAt(pos + 1) == 'e') //nu moet er wel e staan
			{	eindPos = pos + 1;
				rekenGetal = Math.E;
				lengteRekenGetal = 1;
			}	
			else
			{	syntaxError = true;
				return;
			}
		
			if(negatief)
			{
				rekenGetal = - rekenGetal;
				lengteRekenGetal++;
			}
			
			if(eindPos < sb.length() - 1 && sb.charAt(eindPos + 1) == 'E')
			{
				negatief = false;
				int eindPos2 = eindPos + 2;
				eindPos = eindPos2;
				if(sb.charAt(eindPos)=='-')
				{	negatief = true;
					eindPos++;
				}
				
				while(eindPos <= sb.length() - 1 && Character.isDigit(sb.charAt(eindPos)))
					eindPos ++;
				eindPos--;
				subString = sb.substring(eindPos2, eindPos + 1);
				double rekenGetal2 = Double.parseDouble(subString);
				if(negatief)
					rekenGetal2 = - rekenGetal2;
				rekenGetal = rekenGetal*Math.pow(10, rekenGetal2);
				lengteRekenGetal = lengteRekenGetal + subString.length() + 1;
				if(negatief)
					lengteRekenGetal++;			
			}
			if(eindPos < sb.length() - 1 && sb.charAt(eindPos + 1) == 'G')
			{
				int eindPos2 = eindPos + 2;
				eindPos = eindPos2;
				
				while(eindPos <= sb.length() - 1 && Character.isDigit(sb.charAt(eindPos)))
					eindPos ++;
				eindPos--;
				subString = sb.substring(eindPos2, eindPos + 1);
				double rekenGetal2 = Double.parseDouble(subString);
				rekenGetal = rekenGetal*Math.pow(10, - rekenGetal2);
				lengteRekenGetal = lengteRekenGetal + subString.length() + 1;			
			}
		}
		catch(Exception e){
			syntaxError = true;
			return;
		}
	}
	
	/*
	 * Uitkomst van 'simpele' bewerkingen berekenen:
	 * +, -, *, /, ^
	 */
	public void vindUitkomstBreuk(String s, StringBuffer sb)
	{
		double teller1, noemer1, teller2, noemer2;
		
		vindBreukTot(vindIndex(s, sb), sb);
		if(syntaxError)
		{	return;
		}
		teller1 = teller;
		noemer1 = noemer;
		lengte1 = lengteBreuk;
		
		vindBreukVanaf(vindIndex(s, sb), sb);
		if(syntaxError)
		{	return;
		}
		teller2 = teller;
		noemer2 = noemer;
		lengte2 = lengteBreuk;
		
		if(s.equals("+"))
		{	teller = teller1 * noemer2 + noemer1 * teller2;
			noemer = noemer1 * noemer2;
		}
		else if(s.equals("-"))
		{	teller = teller1 * noemer2 - noemer1 * teller2;
			noemer = noemer1 * noemer2;
		}
		else if(s.equals("x"))
		{	teller = teller1 * teller2;
			noemer = noemer1 * noemer2;
		}
		else if(s.equals("/"))
		{	teller = teller1 * noemer2;
			noemer = noemer1 * teller2;
		}
		//else if(s.equals("^"))
		//{	vindUitkomstMachtBreuk(s, sb);
		//}
		else if(s.equals("E"))
		{	teller = teller1 * Math.pow(10, teller2/noemer2);
			noemer = noemer1;		
		}
		else if(s.equals("G"))
		{	teller = teller1 * Math.pow(10, -teller2/noemer2);
			noemer = noemer1;		
		}
	}
	
	public void vindUitkomstMachtBreuk(String s, StringBuffer sb)
	{
		double teller1, noemer1, teller2, noemer2;
		
		if(sb.indexOf("^") > 0 && sb.charAt(sb.indexOf("^") - 1) == ')')
		{	int index = sb.indexOf("^") - 1;
			int haakjesTeller = 1;
			while(index > 0 && haakjesTeller > 0)
			{	index--;
				if(sb.charAt(index) == '(')
					haakjesTeller--;
				else if(sb.charAt(index) == ')')
					haakjesTeller++;
			}
			vindHaakjesUitdrukking(sb, index);
			teller1 = teller;
			noemer1 = noemer;
			lengte1 = lengteHaakjesUitdrukking;
		}
		else
		{	vindBBreukTot(vindIndex(s, sb), sb, false);
			teller1 = tellerB;
			noemer1 = noemerB;
			lengte1 = lengteBreukB;
		}
		if(syntaxError)
		{	return;
		}
		
		if(sb.indexOf("^") < sb.length() - 1 && sb.charAt(sb.indexOf("^") + 1) == '(')
		{	int lengte1 = this.lengte1;
			vindHaakjesUitdrukking(sb, sb.indexOf("^") + 1);
			this.lengte1 = lengte1;
			teller2 = teller;
			noemer2 = noemer;
			lengte2 = lengteHaakjesUitdrukking;
		}
		else
		{	vindBBreukVanaf(vindIndex(s, sb), sb);
			teller2 = tellerB;
			noemer2 = noemerB;
			lengte2 = lengteBreukB;
		}
		if(syntaxError)
		{	return;
		}
		
		teller = Math.pow(teller1, teller2/noemer2);
		if(Double.isNaN(teller))
		{	teller = -Math.pow(-teller1, teller2/noemer2);
			double test = Math.pow(teller, noemer2/teller2);
			if(Math.abs(test - teller1) > 0.000000001)
				teller = Double.NaN;
		}
		noemer = Math.pow(noemer1, teller2/noemer2);
		if(Double.isNaN(noemer))
		{	noemer = -Math.pow(-noemer1, teller2/noemer2);
			double test = Math.pow(noemer, noemer2/teller2);
			if(Math.abs(test - noemer1) > 0.000000001)
				noemer = Double.NaN;
		}
	}
	
	public void vervangUitkomstBreuk(String s, StringBuffer sb)
	{
		vindUitkomstBreuk(s, sb);		
		sb.replace(vindIndex(s, sb) - lengte1, vindIndex(s, sb) + lengte2 + 1, teller + "B" + noemer);
	}
	
	public void vindBreukTot(int pos, StringBuffer sb)
	{
		vindBBreukTot(pos, sb, false);
		lengteBreuk = lengteBreukB;
		if(pos - lengteBreuk - 1 < 0 || sb.charAt(pos - lengteBreuk - 1) != '\u22A5' )
		{	teller = tellerB;
			noemer = noemerB;
		}
		else 
		{	teller = noemerB;
			noemer = tellerB;
			try{
				pos = pos - lengteBreukB - 1;
				vindBBreukTot(pos, sb, false);
				teller = teller * tellerB;
				noemer = noemer * noemerB; 
				lengteBreuk = lengteBreuk + lengteBreukB + 1;
			}
			catch(Exception e)
			{	syntaxError = true;
			}
			if(pos - lengteBreukB - 1 >= 0 && sb.charAt(pos - lengteBreukB - 1) == '\u22A5')
			{	try{
					pos = pos - lengteBreukB - 1;
					vindBBreukTot(pos, sb, false);
					teller = teller * noemerB + noemer * tellerB;
					noemer = noemer * noemerB;
					lengteBreuk = lengteBreuk + lengteBreukB + 1;
				}
				catch(Exception e)
				{	syntaxError = true;
				}
			}
		}
	}
	
	public void vindBreukVanaf(int pos, StringBuffer sb)
	{
		vindBBreukVanaf(pos, sb);
		teller = tellerB;
		noemer = noemerB;
		lengteBreuk = lengteBreukB;
		if(pos + lengteBreuk + 1 <= sb.length() - 1 && sb.charAt(pos + lengteBreukB + 1) == '\u22A5' )
		{	try{
				pos = pos + lengteBreukB + 1;
				vindBBreukVanaf(pos, sb);
				lengteBreuk = lengteBreuk + lengteBreukB + 1;
			}
			catch(Exception e)
			{	syntaxError = true;
				return;
			}
			if(pos + lengteBreukB + 1 >= sb.length() - 1 || sb.charAt(pos + lengteBreukB + 1) != '\u22A5')
			{	noemer = noemer * tellerB;
				teller = teller * noemerB;
			}
			else
			{
				double teller1 = teller * noemerB;
				double teller2 = noemer * tellerB;
				noemer = noemerB * noemer;
				try{
					pos = pos + lengteBreukB + 1;
					vindBBreukVanaf(pos, sb);
					teller = teller1 * tellerB + teller2 * noemerB;
					noemer = noemer * tellerB;
					lengteBreuk = lengteBreuk + lengteBreukB + 1;
				}
				catch(Exception e)
				{	syntaxError = true;
				}
			}
		}
	}
	
	public void vindBBreukTot(int pos, StringBuffer sb, boolean minteken)
	{
		vindGetalVoorBewerking(pos, sb, true);
		lengteBreukB = lengteRekenGetal;
		if(pos - lengteRekenGetal - 1 < 0 || sb.charAt(pos - lengteRekenGetal - 1) != 'B')
		{	tellerB = rekenGetal;
			noemerB = 1;
		}
		else
		{	noemerB = rekenGetal;
			vindGetalVoorBewerking(pos - lengteBreukB - 1, sb, minteken);
			tellerB = rekenGetal;
			lengteBreukB += lengteRekenGetal + 1;
		}
	}
	
	public void vindBBreukVanaf(int pos, StringBuffer sb)
	{
		vindGetalNaBewerking(pos, sb);
		tellerB = rekenGetal;
		lengteBreukB = lengteRekenGetal;
		if(pos + lengteRekenGetal + 1 > sb.length() -1 || sb.charAt(pos + lengteRekenGetal + 1) != 'B')
			noemerB = 1;
		else
		{	vindGetalNaBewerking(pos + lengteBreukB + 1, sb);
			noemerB = rekenGetal;
			lengteBreukB += lengteRekenGetal + 1;
		}
	}
	
	public int[] simplify(int nom, int denom)
    {   if (denom < 0)
        {   nom = - nom;
            denom = - denom;
        }
        if (nom == 0)
            denom = 1;
        else
        {   int g = gcd(nom, denom);
            nom = nom / g;
            denom = denom / g;
        }
        int[] breuk = {nom, denom}; 
        return breuk;
    }
  
	public int gcd(int a, int b)
	{   int m = Math.abs(a);
		int n = Math.abs(b);
		int temp = 0;
		while ( n != 0 )
		{   temp = m % n;
		    m = n;
		    n = temp;
		}
		return m;
	}
  
	public int vindIndex(String s, StringBuffer sb)
	{	int index;
		if(s.equals("-"))
		{	if(sb.substring(1).indexOf(s) > -1)
				index = sb.substring(1).indexOf(s) + 1;
			else
				index = sb.substring(1).indexOf(s);
		}
		else
			index = sb.indexOf(s);
		return index;
	  
	}
		
	/*
	 * Uitdrukking tussen haakjes vinden; haakje links staat op positie n.
	 * Wordt onder andere gebruikt voor gonioformules. 
	 */
	public void vindHaakjesUitdrukking(StringBuffer sb, int n)
	{	int teller = 1;
		int j = n;
		while(teller > 0)
		{	j++;
			if(sb.charAt(j) == '(')
				teller++;
			else if(sb.charAt(j) == ')')
				teller --;
		}
		StringBuffer sb3 = new StringBuffer();
		sb3.append(sb.substring(n + 1, j));
		bereken(sb3);
		lengteHaakjesUitdrukking = j - n + 1; //dit is nu de lengte inclusief haakjes
	}
	
	public void setText(String s)
	{	if(!isDesktop)
			invoerVeld.setReadOnly(true);
		
		invoerVeld.setText(s);
		if(!isDesktop)
			invoerVeld.setReadOnly(false);
	}
	
	public void setCursorPos(int pos)
	{
		if(!isDesktop)
			invoerVeld.setReadOnly(true);
		invoerVeld.setCursorPos(pos);
		
		if(!isDesktop)
			invoerVeld.setReadOnly(false);
	}
	
	public void setFocus(boolean b)
	{
		if(!isDesktop)
			invoerVeld.setReadOnly(true);
		invoerVeld.setFocus(b);
		if(!isDesktop)
			invoerVeld.setReadOnly(false);
	}
	/*
	 * de boolean is om aan te geven of Ans moet worden toegevoegd als 
	 * een nieuwe berekening wordt gestart.
	 */
	public void voegTekstIn(String s, boolean ans)
	{
		if(nieuweInvoer && ans)
		{	setText(rb.ans());
			nieuweInvoer = false;
		}
		if(nieuweInvoer && !ans)
		{	setText("");
			nieuweInvoer = false;
		}
		String str2 = invoerVeld.getText();
		if(invoerVeld.getCursorPos() == 0)
		{	setText(s + str2);
			setCursorPos(s.length());
		}
		else if(invoerVeld.getCursorPos() == str2.length())
			setText(str2 + s);
		else
		{	cp = invoerVeld.getCursorPos();
			setText(str2.substring(0,invoerVeld.getCursorPos())+ s + str2.substring(invoerVeld.getCursorPos(), str2.length()));
			setCursorPos(cp+s.length());
		}
	}
	
	public void vervangTekst(String s)
	{
		String str2 = invoerVeld.getText();
		cp = invoerVeld.getCursorPos();
		
		if(cp == str2.length())
		{	setText(str2 + s);
			setCursorPos(cp + s.length());
			return;
		}
		
		char testChar = str2.charAt(cp);
		// eerste stuk vast terugzetten:
		setText(str2.substring(0,invoerVeld.getCursorPos()) + s);
		
		//uitrekenen wat er verder nog terugmoet (meestal alles behalve het eerstevolgende karakter)
		if(testChar=='s' || testChar == 'c' || testChar == 't' || testChar == 'A')
		{	char testChar2 = str2.charAt(cp + 3);
			if(testChar2 == '(')
			{	setText(invoerVeld.getText() + str2.substring(cp + 4));
			}
			else
				setText(invoerVeld.getText() + str2.substring(cp + 6));
		}
		else
			setText(invoerVeld.getText() + str2.substring(cp + 1));
		
		setCursorPos(cp + s.length());
	}
	
	public void voegInOfVervang(String s, boolean ans)
	{
		if(insert)
			voegTekstIn(s, ans);
		else
			vervangTekst(s);
		
	}
	
	public void maakStapNaarRechts()
	{	String str = invoerVeld.getText();
		int cp = invoerVeld.getCursorPos();
		
		if(nieuweInvoer)
		{	nieuweInvoer = false;
			setCursorPos(str.length());
		}
		if(cp == str.length())
			return;
		else if(str.charAt(cp)=='A')
			cp += 3;
		else if(str.charAt(cp) == 's' || str.charAt(cp) == 'c' || str.charAt(cp) == 't')
		{	if(str.charAt(cp + 3) == '(' )
				cp += 4;
			else
				cp += 6;
		}
		else if(str.charAt(cp) == 'l')
			if(str.charAt(cp + 1) == 'n')
				cp += 3;
			else
				cp += 4;
		else if(str.charAt(cp) == '\u207F' || str.charAt(cp) == '\u2081' || str.charAt(cp) == '\u207B')
			cp += 2;
		else 
			cp += 1;
		
		setCursorPos(cp);
		
	}
	
	public void maakStapNaarLinks()
	{	String str = invoerVeld.getText();
		int cp = invoerVeld.getCursorPos();
		
		if(nieuweInvoer)
		{	nieuweInvoer = false;
			setCursorPos(str.length());
		}
		if(cp == 0)
			return;
		else if(str.charAt(cp - 1)=='s')
			cp -= 3;
		else if(str.charAt(cp - 1) == '(')
		{	if(cp < 3)
				cp -= 1;
			else if(str.charAt(cp - 2) == '\u00B9')
				cp -= 6;
			else if(str.charAt(cp - 2) == 'n' && str.charAt(cp - 3) == 'l')
				cp -= 3;
			else if(str.charAt(cp - 2) == 'n' || str.charAt(cp - 3) == 'o')
				cp -= 4;
			else 
				cp -= 1;
		}
		else if(cp >= 2 && (str.charAt(cp - 2) == '\u207F' || str.charAt(cp - 1) == '\u2080'
			|| str.charAt(cp - 1) == '\u00B9'))
			cp -= 2;
		else if(str.charAt(cp - 1) == '\u2070')
			cp -= 2;
		else 
			cp -= 1;
		
		setCursorPos(cp);
	}
	
	
	public void doeActieBackSpace()
	{	String str = invoerVeld.getText();
		int cp = invoerVeld.getCursorPos();
		
		if(nieuweInvoer)
		{	nieuweInvoer = false; 
			setCursorPos(str.length());
		}
		if(cp == 0)
			return;
		else if(invoerVeld.getSelectionLength() > 0)
		{
			if(str.indexOf(invoerVeld.getSelectedText()) == cp)
			{	setText(str.substring(0, cp) + str.substring(cp + invoerVeld.getSelectionLength()));
				setCursorPos(cp);
			}
			else
			{	setText(str.substring(0, cp - invoerVeld.getSelectionLength()) + str.substring(cp));
				setCursorPos(cp - invoerVeld.getSelectionLength());
			}
		}
		else if(str.charAt(cp - 1) == 's')
		{	setText(str.substring(0, cp - 3) + str.substring(cp));
			setCursorPos(cp - 3);
		}
		else if(str.charAt(cp - 1) == '(' )
		{	if(cp < 3)
			{	setText(str.substring(0, cp - 1) + str.substring(cp));
				setCursorPos(cp - 1);
			}
			else if(str.charAt(cp - 2) == '\u00B9')
			{	setText(str.substring(0, cp - 6) + str.substring(cp));
				setCursorPos(cp - 6);
			}
			else if(str.charAt(cp - 2) == 'n' && str.charAt(cp - 3) == 'l')
			{	setText(str.substring(0, cp - 3) + str.substring(cp));
				setCursorPos(cp - 3);
			}
			else if(str.charAt(cp - 2) == 'n' || str.charAt(cp - 3) == 'o')
			{	setText(str.substring(0, cp - 4) + str.substring(cp));
				setCursorPos(cp - 4);
			}
			else
			{	setText(str.substring(0, cp - 1) + str.substring(cp));
				setCursorPos(cp - 1);
			}
		}
		else if(cp >= 2 && str.charAt(cp - 2) == '\u207F')
		{	setText(str.substring(0, cp - 2) + str.substring(cp));
			setCursorPos(cp - 2);
		}
		else if(str.charAt(cp - 1) == '\u2080' || str.charAt(cp - 1) == '\u00B9')
		{	setText(str.substring(0, cp - 2) + str.substring(cp));
			setCursorPos(cp - 2);
		}
		else
		{ 	setText(str.substring(0, cp - 1) + str.substring(cp));
			setCursorPos(cp - 1);
		}
		if(invoerVeld.getText().equals(""))
		{	breuk = false;
			invers = false;
			invLabel.setVisible(false);
		}
	}
	
	public void doeActieDelete()
	{	String str = invoerVeld.getText();
		int cp = invoerVeld.getCursorPos();
		
		if(nieuweInvoer)
		{	nieuweInvoer = false;
			setCursorPos(str.length());
		}
		if(cp == str.length())
			return;
		else if(invoerVeld.getSelectionLength() > 0)
		{	if(str.indexOf(invoerVeld.getSelectedText()) == cp)
			{	setText(str.substring(0, cp) + str.substring(cp + invoerVeld.getSelectionLength()));
				
			}
			else
			{	setText(str.substring(0, cp - invoerVeld.getSelectionLength()) + str.substring(cp));
				cp = cp - invoerVeld.getSelectionLength();
			}
		}
		else if(str.charAt(cp)=='A')
			setText(str.substring(0, cp) + str.substring(cp + 3));
		else if(str.charAt(cp) == 's' || str.charAt(cp) == 'c' || str.charAt(cp) == 't')
		{	if(str.charAt(cp + 3) == '(' )
				setText(str.substring(0, cp) + str.substring(cp + 4));	
			else
				setText(str.substring(0, cp) + str.substring(cp + 6));
		}
		else if(str.charAt(cp) == 'l')
			if(str.charAt(cp + 1) == 'n')
				setText(str.substring(0, cp) + str.substring(cp + 3));
			else
				setText(str.substring(0, cp) + str.substring(cp + 4));
		else if(str.charAt(cp) == '\u207F' || str.charAt(cp) == '\u2081' || str.charAt(cp) == '\u207B')
			setText(str.substring(0, cp) + str.substring(cp + 2));
		else 
		setText(str.substring(0, cp) + str.substring(cp + 1));
		
		setCursorPos(cp);
		if(invoerVeld.getText().equals(""))
		{	breuk = false;
			invers = false;
			invLabel.setVisible(false);
		}
	}
	
	public void vindAntwoord(boolean dec)
	{	syntaxError = false;
		maakBerekenbaar(invoerVeld.getText());
		bereken(sb);
		if(!syntaxError)
		{	String uitvoerTekst;
			if(breuk)
			{	if(teller % 1 == 0  && noemer %1 == 0 && !dec)
				{	int tellerInt = (int) teller;	
					int noemerInt = (int) noemer;
					int[] breuk  = simplify(tellerInt, noemerInt);
				
					int gehelenInt = breuk[0]/breuk[1];
					if(gehelenInt > 0)
					{	breuk[0] = breuk[0] - gehelenInt * breuk[1];
						if(breuk[0] == 0)
							uitvoerTekst = "" + gehelenInt;
						else
							uitvoerTekst = gehelenInt + "\u22A5" + breuk[0] + "\u22A5" + breuk[1];
					}
					else
					{	if(breuk[0] == 0)
							uitvoerTekst = "" + 0;
						else
							uitvoerTekst = breuk[0] + "\u22a5" + breuk[1];
					}
					if(!syntaxError)
						bewaardeAns = uitvoerTekst;
				}
				else
				{	eindUitkomst = teller/noemer;
					if(!syntaxError)
						bewaardeAns = Double.toString(eindUitkomst);
					if(eindUitkomst < Math.pow(10, 9))
						eindUitkomst = (double) Math.round(1000000000 * eindUitkomst)/1000000000;
					uitvoerTekst = Double.toString(eindUitkomst);
				}
			}
			else
			{	try{
				eindUitkomst = Double.parseDouble(sb.toString());}
				catch(Exception ex) 
				{	if(sb2.indexOf("E") > -1)
						try
						{	vindUitkomst("E", sb2);
							eindUitkomst = uitkomst;
						}
						catch(Exception exc)
						{	syntaxError = true;
						}
					else if(sb2.indexOf("G") > -1)
						try
						{	vindUitkomst("G", sb2);
							eindUitkomst = uitkomst;
						}
						catch(Exception exc)
						{	syntaxError = true;
						}
					else if(sb2.indexOf("\u22a5") > -1 || sb2.indexOf("B") > -1)
					{
						eindUitkomst = teller/noemer;
					}
				}
				if(!syntaxError)
					bewaardeAns = Double.toString(eindUitkomst);
				if(eindUitkomst < Math.pow(10, 9))
					eindUitkomst = (double) Math.round(1000000000 * eindUitkomst)/1000000000;
				uitvoerTekst = Double.toString(eindUitkomst);
			}
			if(uitvoerTekst.length() > 1 && uitvoerTekst.endsWith(".0"))
				uitvoerTekst = uitvoerTekst.substring(0, uitvoerTekst.length()-2);
		
			int indexE;
			String tienMachtString;
			if(uitvoerTekst.contains("E"))
			{	indexE = uitvoerTekst.indexOf('E');
				tienMachtString = uitvoerTekst.substring(indexE + 1);
				tienMachtString = tienMachtString.replaceAll("0", "\u2070");
				tienMachtString = tienMachtString.replaceAll("1", "\u00B9");
				tienMachtString = tienMachtString.replaceAll("2", "\u00B2");
				tienMachtString = tienMachtString.replaceAll("3", "\u00B3");
				tienMachtString = tienMachtString.replaceAll("4", "\u2074");
				tienMachtString = tienMachtString.replaceAll("5", "\u2075");
				tienMachtString = tienMachtString.replaceAll("6", "\u2076");
				tienMachtString = tienMachtString.replaceAll("7", "\u2077");
				tienMachtString = tienMachtString.replaceAll("8", "\u2078");
				tienMachtString = tienMachtString.replaceAll("9", "\u2079");     
				tienMachtString = tienMachtString.replaceAll("-", "\u207B");
				
				if(uitvoerTekst.substring(0, indexE).length() > 1 && uitvoerTekst.substring(0, indexE).endsWith(".0"))
					uitvoerTekst = uitvoerTekst.substring(0, indexE - 2) + "\u00D710" + tienMachtString;
				else
					uitvoerTekst = uitvoerTekst.substring(0, indexE)+ "\u00D710"+ tienMachtString;
				
			}
			if(isNederlands)
				uitvoerTekst = uitvoerTekst.replace(".", DECIMAL);
			uitvoerVeld.setText(uitvoerTekst);
		}
		if(sb2.toString().contains("NaN") || sb.toString().contains("NaN") || sb2.toString().contains("Infinity")
				|| sb.toString().contains("Infinity"))
			uitvoerVeld.setText("Math ERROR");
		else if(syntaxError)
			uitvoerVeld.setText("Syntax ERROR");
		nieuweInvoer = true;
		breuk = false;
		invers = false;
		invLabel.setVisible(false);
		if(!insert)
		{	insert = true;
			//invoerVeld.getCaret().setVisible(false);
			//invoerVeld.setCaret(defaultCaret);
		}
		//invoerVeld.getCaret().setBlinkRate(500);
		//invoerVeld.getCaret().setVisible(false);
	}
	
	class RmClickHandler implements ClickHandler
	{
		public void onClick(ClickEvent e)
		{
			//invoerVeld.getCaret().setVisible(true);
			String str = new String("");
			for(int i = 0; i < 10; i++)
				if(e.getSource() == getalKnop[i])
					voegInOfVervang(""+i, false);
			
			if(e.getSource() == piKnop)
				voegInOfVervang("\u03C0", false);
			else if(e.getSource() == plusKnop)
				voegInOfVervang("+", true);
			else if(e.getSource() == minKnop)
				voegInOfVervang("\u2212", true);
			else if(e.getSource() == keerKnop)
				voegInOfVervang("\u00D7", true);
			else if(e.getSource() == deelKnop)
				voegInOfVervang("\u00F7", true);
			else if(e.getSource() == wortelKnop)
				voegInOfVervang("\u221A", false);
			else if(e.getSource() == kwadraatKnop)
				voegInOfVervang("\u00B2", true);
			else if(e.getSource() == machtKnop)
				voegInOfVervang("^", true);
			else if(e.getSource() == eenGedeeldDoorKnop)
				voegInOfVervang("\u207B\u00B9", true);
			else if(e.getSource() == expKnop)
				voegInOfVervang("\u2081\u2080", true);
			else if(e.getSource() == haakLinksKnop)
				voegInOfVervang("(", false);
			else if(e.getSource() == haakRechtsKnop)
				voegInOfVervang(")", false);
			else if(e.getSource() == kommaKnop)
			{	if(isNederlands)
					voegInOfVervang(DECIMAL, false);
				else
					voegInOfVervang(".", false);
			}
			else if(e.getSource() == negatiefKnop)
				voegInOfVervang("-", false);
			else if(e.getSource() == ansKnop)
				voegInOfVervang("Ans", false);
			else if(e.getSource() == breukKnop)
			{	if(!invers)
					voegInOfVervang("\u22A5", false);
				else
					vindAntwoord(true);
			}
			else if(e.getSource() == sinKnop)
			{	if(!invers)
					voegInOfVervang("sin(", false);
				else
				{	voegInOfVervang("sin\u207B\u00B9(", false);
					invers = false;
					invLabel.setVisible(false);
				}
			}
			else if(e.getSource() == cosKnop)
			{	if(!invers)
					voegInOfVervang("cos(", false);
				else
				{	voegInOfVervang("cos\u207B\u00B9(", false);
					invers = false;
					invLabel.setVisible(false);
				}
			}
			else if(e.getSource() == tanKnop)
			{	if(!invers)
					voegInOfVervang("tan(", false);
				else
				{	voegInOfVervang("tan\u207B\u00B9(", false);
					invers = false;
					invLabel.setVisible(false);
				}
			}
			else if(e.getSource() == logKnop)
				voegInOfVervang("log(", false);
			else if(e.getSource() == lnKnop)
				voegInOfVervang("ln(", false);
			else if(e.getSource() == eKnop)
				voegInOfVervang("e", false);
			else if(e.getSource() == nWortelKnop)
				voegInOfVervang("\u207F\u221A", false);
			else if(e.getSource() == invKnop)
			{	invers = !invers;
				invLabel.setVisible(invers);
			}
			else if(e.getSource() == cKnop)
			{	nieuweInvoer = false;
				breuk = false;
				invers = false;
				invLabel.setVisible(false);
				setText("");
				uitvoerVeld.setText("0");
			}
			else if(e.getSource() == delKnop)
				doeActieBackSpace();
			else if(e.getSource() == pijlLinksKnop)
				maakStapNaarLinks();
			else if(e.getSource() == pijlRechtsKnop)
				maakStapNaarRechts();
			else if(e.getSource() == insKnop)
			{	str = invoerVeld.getText();
				//invoerVeld.getCaret().setVisible(false);
				insert = !insert;
				if(nieuweInvoer)
				{	nieuweInvoer = false;
					setCursorPos(str.length());
				}
				cp = invoerVeld.getCursorPos();
				//if(insert)
				//	invoerVeld.setCaret(defaultCaret);
				//else
				//	invoerVeld.setCaret(replaceCaret);
				setCursorPos(cp);
				//invoerVeld.getCaret().setBlinkRate(500);
				//invoerVeld.getCaret().setVisible(true);
			}
			else if(e.getSource() == isKnop)
			{	vindAntwoord(false);
				
			}
			if(e.getSource() != isKnop && (e.getSource() != breukKnop || !invers))
				setFocus(true);
		}
	}
	
	class RmKeyDownHandler implements KeyDownHandler
	{

		@Override
		public void onKeyDown(KeyDownEvent event) {
			kc =event.getNativeKeyCode();
			
			if(kc == KeyCodes.KEY_LEFT)
				maakStapNaarLinks();
			else if(kc == KeyCodes.KEY_RIGHT)
				maakStapNaarRechts();
			else if(kc == KeyCodes.KEY_DELETE)
				doeActieDelete();
			else if(kc == KeyCodes.KEY_BACKSPACE)
				doeActieBackSpace();
			else if(kc == KeyCodes.KEY_ENTER)
				vindAntwoord(false);
			else
				return;
			event.stopPropagation();
			event.preventDefault();
			
		}
	}
	
	class RmKeyPressHandler implements KeyPressHandler
	{

		@Override
		public void onKeyPress(KeyPressEvent event) {
			char kch = event.getCharCode();
			if(kch == '=')
				vindAntwoord(false);
			else if(kch == '*')
				voegInOfVervang("\u00D7", true);
			else if(kch == '/' || kch == ':')
				voegInOfVervang("\u00F7", true);
			else if(kch == ',' || kch == '.' || kch == DECIMAL.charAt(0))
			{	if(isNederlands)
					voegInOfVervang(DECIMAL, false);
				else
					voegInOfVervang(".", false);
			}
			else if(kch == '-')
				voegInOfVervang("\u2212", true);
			else if(kch == '-')
				voegInOfVervang("\u2212", true);
			else if(kch == '+')
				voegInOfVervang("+", true);
			else if(kch =='(' || kch == ')' || Character.isDigit(kch))	
				voegInOfVervang("" + (char)kch, false);
			
			else if(event.isShiftKeyDown() && kch == '6')
				voegInOfVervang("^", true);
			
			
			event.stopPropagation();
			event.preventDefault();
		}
		
	}
	
	class RmMouseUpHandler implements MouseUpHandler
	{

		@Override
		public void onMouseUp(MouseUpEvent event) {
			String str = invoerVeld.getText();
			int cp = invoerVeld.getCursorPos();
			
			if(cp == 0 || cp == str.length() || invoerVeld.getSelectionLength() > 0)
				return;
			else if(str.charAt(cp) == 'i' || str.charAt(cp) == 'o' || str.charAt(cp - 1) == 'l' 
				|| str.charAt(cp - 1) == 't' || (str.charAt(cp) == 'n' && str.charAt(cp + 1) == 's')
				|| str.charAt(cp - 1) == '\u2081' || str.charAt(cp - 1) == '\u207F'
				|| (str.charAt(cp - 1) == '\u207B' && cp > 1 && str.charAt(cp-2) != 'n' && (str.charAt(cp - 2) != 's' || (cp > 2 && str.charAt(cp - 3) == 'n'))))
				cp--;
			else if(cp < str.length() - 1 && str.charAt(cp + 1) == '\u207B' && (str.charAt(cp) == 'n' || (str.charAt(cp) == 's' && str.charAt(cp - 1) != 'n')))
				cp -= 2;
			else if(str.charAt(cp - 1) == 'n' && (str.charAt(cp) == 's' || str.charAt(cp) == '(')
					|| (cp > 2 && str.charAt(cp - 2) == '\u207B' && (str.charAt(cp - 3) == 'n' || (str.charAt(cp - 3) == 's' && cp > 3 && str.charAt(cp - 4) != 'n')))
					|| (str.charAt(cp) == '(' && (str.charAt(cp - 1) == 's' || str.charAt(cp - 1) == 'g')))
				cp++;
			else if((cp < str.length() - 1 && str.charAt(cp + 1) == '(' && (str.charAt(cp - 1) == 'o' || str.charAt(cp) == 'n')) 
				|| (cp > 1 && str.charAt(cp - 1) == '\u207B' && (str.charAt(cp - 2) == 'n' || (str.charAt(cp - 2) == 's' && cp > 2 && str.charAt(cp - 3) != 'n'))))
				cp += 2;
			else if(str.charAt(cp) == '\u207B' && (str.charAt(cp - 1) == 'n' || (str.charAt(cp - 1) == 's' && cp > 1 && str.charAt(cp - 2) != 'n')))
				cp += 3;
					
			setCursorPos(cp);
			
		}
		
	}

	@Override
	public void zetNagekeken(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[][] getScoreObjectives() {
		return null;
	}
	
	

	
}
