package fi.calculatorgwt.client;


import java.util.HashMap;
import java.util.Map;

//import nl.uu.fi.dwo.interaction.client.InteractionStub;
//import nl.uu.fi.dwo.interaction.client.JSONUtilities;
//import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
//import nl.uu.fi.dwo.interaction.client.Stub;
//import nl.uu.fi.dwo.interaction.client.json.ObjectMap;





import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;








//import fi.calculatorgwt.ReplaceCaret;
import fi.calculatorgwt.client.text.Text_nl;


public class CalculatorGWT implements EntryPoint {//, InteractionStub {
	
	public static Text_nl rb = new Text_nl();
	//protected static Locale language;
	
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
	//ButtonGroup groep;
	
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
		
		init(breedte, hoogte, null, null);
		//Stub.publish(this);
		
		}
	
	public void initialize()
	{
		//setLayout(new BorderLayout(5, 5));
		
//		theFont = new Font("Sansserif", Font.BOLD, 16);
//		theFM = getFontMetrics(theFont);
//		theLargeFont = new Font("Sansserif", Font.BOLD, 14);
//		theLargeFM = getFontMetrics(theLargeFont);
//		theSmallFont = new Font("Sansserif", Font.BOLD, 6);
//		theSmallFM = getFontMetrics(theSmallFont);
		
		basisPanel.setPixelSize(breedte, hoogte);
		
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
			int hoogteBovendeel = 100;
			if(rmMode == 1 && gonioKnoppen && logaritmeKnoppen)
			{
				knopBreedte = breedte/8 - 4;
				knopHoogteBasis = (hoogte - hoogteBovendeel)/5 - 3;
			}
			else if(rmMode == 1)
			{
				knopBreedte = breedte / 8 - 4;
				knopHoogteBasis = (hoogte - hoogteBovendeel)/4 - 3;
			}
			else
			{
				knopBreedte = breedte / 6 - 4;
				knopHoogteBasis = (hoogte - hoogteBovendeel) / 4 - 3;
			}
			knopHoogteNavigatie = 25;
			knopHoogteExtra = knopHoogteBasis;
		}
		else
		{
			knopBreedte = (breedte - 30)/10;
			knopHoogteBasis = (hoogte - 50)/5;
			knopHoogteNavigatie = knopHoogteBasis;
			knopHoogteExtra = 30;
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
		
		int kolomBreedte = 0;
		if(rmMode < 2)
			kolomBreedte = breedte / 5;
		else
			kolomBreedte = breedte / 10; //TODO: hier juiste breedte bepalen.
		
		pijlLinksKnop = maakButton("\u25C4", blauw, witblauw, kolomBreedte - 5, knopHoogteNavigatie);
		pijlRechtsKnop = maakButton("\u25BA", blauw, witblauw, kolomBreedte - 5, knopHoogteNavigatie);
		insKnop = maakButton("INS", blauw, witblauw, kolomBreedte - 4, knopHoogteNavigatie);
		delKnop = maakButton("DEL", blauw, witblauw, kolomBreedte - 4, knopHoogteNavigatie);
		cKnop = maakButton("C", blauw, witblauw, kolomBreedte - 5, knopHoogteNavigatie);
		
		//if(CalculatorGWT.language.toString().equals("nl"))
			kommaKnop = maakButton(",", grijs, witblauw, knopBreedte, knopHoogteBasis);
		//else
		//	kommaKnop = maakButton(".", grijs, witblauw);
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
		
//		for(int i = 0; i < 10; i++)
//			getalKnop[i].addActionListener(this);
//		plusKnop.addActionListener(this);
//		minKnop.addActionListener(this);
//		keerKnop.addActionListener(this);
//		deelKnop.addActionListener(this);
//		machtKnop.addActionListener(this);
//		kwadraatKnop.addActionListener(this);
//		wortelKnop.addActionListener(this);
//		eenGedeeldDoorKnop.addActionListener(this);
//		breukKnop.addActionListener(this);
//		expKnop.addActionListener(this);
//		haakLinksKnop.addActionListener(this);
//		haakRechtsKnop.addActionListener(this);
//		pijlLinksKnop.addActionListener(this);
//		pijlRechtsKnop.addActionListener(this);
//		insKnop.addActionListener(this);
//		delKnop.addActionListener(this);
//		cKnop.addActionListener(this);
//		kommaKnop.addActionListener(this);
//		negatiefKnop.addActionListener(this);
//		ansKnop.addActionListener(this);
//		isKnop.addActionListener(this);
//		sinKnop.addActionListener(this);
//		cosKnop.addActionListener(this);
//		tanKnop.addActionListener(this);
//		invKnop.addActionListener(this);
//		piKnop.addActionListener(this);
//		logKnop.addActionListener(this);
//		lnKnop.addActionListener(this);
//		eKnop.addActionListener(this);
//		nWortelKnop.addActionListener(this);
		leegLabel = new Label[15];
		for(int i = 0; i<15; i++)
			leegLabel[i] = new Label("");
		sinInvLabel = maakLabel("sin\u207B\u00B9", CssColor.make("orange"));
		cosInvLabel = maakLabel("cos\u207B\u00B9", CssColor.make("orange"));
		tanInvLabel = maakLabel("tan\u207B\u00B9", CssColor.make("orange"));
		decLabel = maakLabel("\u2192dec", CssColor.make("orange"));
		
		knoppenPanel = new FlowPanel();
		
		
		//TODO: widgetleftwidth etc zetten
		//add(knoppenPanel, BorderLayout.CENTER);
		
//		linkerKnoppen = new ();
//		//linkerKnoppen.setLayout(new GridLayout(6, 5, 3, 3));
//		rechterKnoppen = new LayoutPanel();
//		//rechterKnoppen.setLayout(new GridLayout (5, 5, 3, 3));
//		bovensteKnoppen = new FlowPanel();
//		//bovensteKnoppen.setLayout(new GridLayout(1, 5, 3, 3));
//		ondersteKnoppen = new LayoutPanel();
//		
		instellingenPanel = new FlowPanel();
		//instellingenPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		gradenButton = new RadioButton(rb.getString("gradenButton"));
		//gradenButton.addActionListener(this);
		
		radialenButton = new RadioButton(rb.getString("radialenButton"));
		//radialenButton.setSelected(true);
		//radialenButton.addActionListener(this);
		
//		groep = new ButtonGroup();
//		groep.add(gradenButton);
//		groep.add(radialenButton);
		
		zetRmMode(rmMode, gradenInstelbaar);
		
		//replaceCaret = new ReplaceCaret();
		//defaultCaret = new DefaultCaret();
		
		invoerVeld = new TextBox();
		invoerVeld.setWidth((breedte - 10) + "px");
		invoerVeld.getElement().getStyle().setBackgroundColor(witblauw.toString());
//		//invoerVeld.setCaret(defaultCaret);
//		invoerVeld.getCaret().setBlinkRate(500);
//		invoerVeld.getCaret().setVisible(true);
//		invoerVeld.setBackground(witblauw);
//		invoerVeld.setFont(theFont);
//		invoerVeld.setMargin(new Insets(8,10,3,3));
//		invoerVeld.addKeyListener(this);
//		invoerVeld.addMouseListener(this);

		invLabel = new Label("I");
		//invLabel.setFont(theSmallFont);
		//invLabel.setOpaque(true);
		//invLabel.setBackground(Color.BLACK);
		//invLabel.setForeground(witblauw);
		//invLabel.setHorizontalAlignment(SwingConstants.CENTER);
		//invLabel.setBounds(2,2,5,6);
		//invLabel.setVisible(invers);
		//invoerVeld.add(invLabel, 0);
		
		uitvoerVeld = new Label("0");
		uitvoerVeld.setWidth(breedte + "px");
		//kan ook naar CSS.
		uitvoerVeld.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		uitvoerVeld.getElement().getStyle().setBackgroundColor(lichtblauw.toString());
		uitvoerVeld.getElement().getStyle().setFontSize(14, Style.Unit.PX);
		uitvoerVeld.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		//uitvoerVeld.setHorizontalAlignment(JLabel.RIGHT);
		//uitvoerVeld.setFont(theLargeFont);
		
		//uitvoerPanel = new FlowPanel();
		//uitvoerPanel.setLayout(new BorderLayout());
		//uitvoerPanel.setBackground(lichtblauw);
		
		basisPanel.add(invoerVeld);
		basisPanel.add(uitvoerVeld);

		basisPanel.add(knoppenPanel);
	}
	
	//met strings in plaats van CssColors?
	public Button maakButton(String s, CssColor backGround, CssColor foreGround, int width, int height)
	{
		Button button = new Button(s);
		button.removeStyleName("gwt-Button");
		//button.setFont(theLargeFont);
		button.getElement().getStyle().setBackgroundColor(backGround.toString());
		button.getElement().getStyle().setColor(foreGround.toString());
		//kunnen ook naar CSS.
		button.getElement().getStyle().setFontSize(14, Style.Unit.PX);
		button.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		button.setPixelSize(width, height);
		//button.setMargin(new Insets(0, 0, 0, 0));
		return button;
	}
	
	public Label maakLabel(String s, CssColor c)
	{
		Label label = new Label(s);
		//label.setFont(theLargeFont);
		//label.setForeground(c);
		//label.setVerticalAlignment(SwingConstants.BOTTOM);
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
		//knoppenPanel.clear();
		
		if(rmMode < 2)
		{
			//knoppenPanel.setLayout(new BorderLayout(5, 5));
			bovensteKnoppen = new Grid(1, 5);
			if(rmMode == 1 && gonioKnoppen && logaritmeKnoppen)
			{
				ondersteKnoppen = new Grid(5,8);
			}
			else if(rmMode == 1)
				ondersteKnoppen = new Grid(4, 8);
			else
				ondersteKnoppen = new Grid(4,6);
				
			//ondersteKnoppen.getCellFormatter().
			
			bovensteKnoppen.setWidget(0, 0, pijlLinksKnop);
			bovensteKnoppen.setWidget(0, 1, pijlRechtsKnop);
			bovensteKnoppen.setWidget(0, 2, insKnop);
			bovensteKnoppen.setWidget(0, 3, delKnop);
			bovensteKnoppen.setWidget(0, 4, cKnop);
			
//			bovensteKnoppen.add(pijlRechtsKnop);
//			bovensteKnoppen.add(insKnop);
//			bovensteKnoppen.add(delKnop);
//			bovensteKnoppen.add(cKnop);
//			
			knoppenPanel.add(bovensteKnoppen);
			knoppenPanel.add(ondersteKnoppen);
		
			if(gradenInstelbaar)
			{	instellingenPanel.add(gradenButton);
				instellingenPanel.add(radialenButton);
				knoppenPanel.add(instellingenPanel);
				
				
			}
			
			//ondersteKnoppen.clear();
//			if(rmMode == 1 && gonioKnoppen && logaritmeKnoppen)
//				ondersteKnoppen.setLayout(new GridLayout(5, 8, 3, 3));
//			else if(rmMode == 1)
//				ondersteKnoppen.setLayout(new GridLayout(4, 8, 3, 3));
//			else
//				ondersteKnoppen.setLayout(new GridLayout(4, 6, 3, 3));
				
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
				//else
				//	ondersteKnoppen.add(leegLabel[1]);
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
				//else
				//	ondersteKnoppen.setWidget(leegLabel[2]);
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
			if(gonioKnoppen && logaritmeKnoppen)
				ondersteKnoppen.setWidget(3, 7, invKnop);
			if(rmMode == 1)
			{	k = 0;
				if(gonioKnoppen ^ logaritmeKnoppen)
				{	ondersteKnoppen.setWidget(4, 0, invKnop);
					k++;
				}
				if(gonioKnoppen && logaritmeKnoppen)
				{
					ondersteKnoppen.setWidget(4, k, eenGedeeldDoorKnop);
					ondersteKnoppen.setWidget(4, k+1, breukKnop);
					ondersteKnoppen.setWidget(4, k+2, expKnop);
					//ondersteKnoppen.setWidget(leegLabel[0]);
				}
			}
			
		}
		else
		{	//knoppenPanel.setLayout(new GridLayout(1, 2, 10, 5));
			
			knoppenPanel.add(linkerKnoppen);
			knoppenPanel.add(rechterKnoppen);
			
			linkerKnoppen.add(invKnop);
			linkerKnoppen.add(leegLabel[0]);
			linkerKnoppen.add(leegLabel[1]);
			linkerKnoppen.add(leegLabel[2]);
			linkerKnoppen.add(leegLabel[3]);
			
			linkerKnoppen.add(sinInvLabel);
			linkerKnoppen.add(cosInvLabel);
			linkerKnoppen.add(tanInvLabel);
			linkerKnoppen.add(decLabel);
			linkerKnoppen.add(leegLabel[4]);
			
			linkerKnoppen.add(sinKnop);
			linkerKnoppen.add(cosKnop);
			linkerKnoppen.add(tanKnop);
			linkerKnoppen.add(breukKnop);
			linkerKnoppen.add(expKnop);
			
			linkerKnoppen.add(wortelKnop);
			linkerKnoppen.add(kwadraatKnop);
			linkerKnoppen.add(machtKnop);
			linkerKnoppen.add(eenGedeeldDoorKnop);
			linkerKnoppen.add(piKnop);
		
			for(int j = 5; j < 15; j++)
				linkerKnoppen.add(leegLabel[j]);
			
			rechterKnoppen.add(pijlLinksKnop);
			rechterKnoppen.add(pijlRechtsKnop);
			rechterKnoppen.add(insKnop);
			rechterKnoppen.add(delKnop);
			rechterKnoppen.add(cKnop);
			
			rechterKnoppen.add(getalKnop[7]);
			rechterKnoppen.add(getalKnop[8]);
			rechterKnoppen.add(getalKnop[9]);
			rechterKnoppen.add(keerKnop);
			rechterKnoppen.add(deelKnop);
			
			rechterKnoppen.add(getalKnop[4]);
			rechterKnoppen.add(getalKnop[5]);
			rechterKnoppen.add(getalKnop[6]);
			rechterKnoppen.add(plusKnop);
			rechterKnoppen.add(minKnop);
			
			rechterKnoppen.add(getalKnop[1]);
			rechterKnoppen.add(getalKnop[2]);
			rechterKnoppen.add(getalKnop[3]);
			rechterKnoppen.add(haakLinksKnop);
			rechterKnoppen.add(haakRechtsKnop);
			
			rechterKnoppen.add(getalKnop[0]);
			rechterKnoppen.add(kommaKnop);
			rechterKnoppen.add(negatiefKnop);
			rechterKnoppen.add(ansKnop);
			rechterKnoppen.add(isKnop);
		}
			
		//revalidate();
		//repaint();
	}

	/*
	@Override
	public HashMap<String, Object> getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean isCorrect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void kijkNa() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Widget asWidget() {
		return basisPanel;
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
	*/

	//@Override
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width;
		hoogte = height;
		
		/*
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
		*/
		
		initialize();
		
	}
}
