package fi.calculatorgwt.client;



import java.util.Locale;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

//import fi.calculatorgwt.ReplaceCaret;
import fi.calculatorgwt.client.text.Text_nl;


public class CalculatorGWT implements EntryPoint {
	
	public static Text_nl rb = new Text_nl();
	protected static Locale language;
	
	LayoutPanel basisPanel = new LayoutPanel();
	
	static final String holderId = "dockholder";
	static final String upgradeMessage = 
			"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	int cdipBreedte = 500;
	int cdipHoogte = 300;
	
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
	
	LayoutPanel knoppenPanel, bovensteKnoppen, linkerKnoppen, rechterKnoppen, ondersteKnoppen;
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
		
		RootPanel.get(holderId).add(basisPanel); // was basisPanel
		RootPanel.get(holderId).setStyleName("root");
		
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
		
		blauw = CssColor.make(130, 180, 255);
		oranje = CssColor.make(255, 170, 80);
		groen = CssColor.make(0, 150, 0);
		lichtblauw = CssColor.make(208, 228, 255);
		witblauw = CssColor.make(245, 250, 255);
		geel = CssColor.make(255, 255, 180);
		lichtgeel = CssColor.make(255, 255, 220);
		grijs = CssColor.make("gray");
		donkergrijs = CssColor.make(98, 98, 98);
		
		
		getalKnop = new Button[10];
		for(int i = 0; i<getalKnop.length; i++)
			getalKnop[i] = maakButton(""+i, donkergrijs, witblauw);
		
		plusKnop = maakButton("+", grijs, witblauw);
		minKnop = maakButton("\u2212", grijs, witblauw);
		keerKnop = maakButton("\u00D7", grijs, witblauw);
		deelKnop = maakButton("\u00F7", grijs, witblauw);
		machtKnop = maakButton("^", grijs, witblauw);
		kwadraatKnop = maakButton("x\u00B2", grijs, witblauw);
		wortelKnop = maakButton("\u221A", grijs, witblauw);
		eenGedeeldDoorKnop = maakButton("x\u207B\u00B9", grijs, witblauw);
		breukKnop = maakButton("a b/c", grijs, witblauw);
		expKnop = maakButton("<html>&times;10<sup><i>x</i></sup></html>", grijs, witblauw);
		
		haakLinksKnop = maakButton("(", grijs, witblauw);
		haakRechtsKnop = maakButton(")", grijs, witblauw);
		
		pijlLinksKnop = maakButton("\u25C4", blauw, witblauw);
		pijlRechtsKnop = maakButton("\u25BA", blauw, witblauw);
		insKnop = maakButton("INS", blauw, witblauw);
		delKnop = maakButton("DEL", blauw, witblauw);
		cKnop = maakButton("C", blauw, witblauw);
		
		if(CalculatorGWT.language.toString().equals("nl"))
			kommaKnop = maakButton(",", grijs, witblauw);
		else
			kommaKnop = maakButton(".", grijs, witblauw);
		negatiefKnop = maakButton("(-)", grijs, witblauw);
		ansKnop = maakButton("Ans", grijs, witblauw);
		isKnop = maakButton("=", groen, CssColor.make("white"));
		
		sinKnop = maakButton("sin", grijs, witblauw);
		cosKnop = maakButton("cos", grijs, witblauw);
		tanKnop = maakButton("tan", grijs, witblauw);
		invKnop = maakButton("INV", geel, donkergrijs);
		piKnop = maakButton("\u03C0", grijs, witblauw);
		
		logKnop = maakButton("log", grijs, witblauw);
		lnKnop = maakButton("ln", grijs, witblauw);
		eKnop = maakButton("e", grijs, witblauw);
		nWortelKnop = maakButton("\u207F\u221A", grijs, witblauw);
		
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
		
		knoppenPanel = new LayoutPanel();
		basisPanel.add(knoppenPanel);
		//TODO: widgetleftwidth etc zetten
		//add(knoppenPanel, BorderLayout.CENTER);
		
		linkerKnoppen = new LayoutPanel();
		//linkerKnoppen.setLayout(new GridLayout(6, 5, 3, 3));
		rechterKnoppen = new LayoutPanel();
		//rechterKnoppen.setLayout(new GridLayout (5, 5, 3, 3));
		bovensteKnoppen = new LayoutPanel();
		//bovensteKnoppen.setLayout(new GridLayout(1, 5, 3, 3));
		ondersteKnoppen = new LayoutPanel();
		
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
		
		//zetRmMode(rmMode, gradenInstelbaar);
		
		//replaceCaret = new ReplaceCaret();
		//defaultCaret = new DefaultCaret();
		
//		invoerVeld = new TextBox("");
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
		//uitvoerVeld.setHorizontalAlignment(JLabel.RIGHT);
		//uitvoerVeld.setFont(theLargeFont);
		
		uitvoerPanel = new FlowPanel();
		//uitvoerPanel.setLayout(new BorderLayout());
		//uitvoerPanel.setBackground(lichtblauw);
		//add(uitvoerPanel, BorderLayout.NORTH);
		
		//uitvoerPanel.add(invoerVeld, BorderLayout.NORTH);
		//uitvoerPanel.add(uitvoerVeld, BorderLayout.SOUTH);

	}
	
	//met strings in plaats van CssColors?
	public Button maakButton(String s, CssColor backGround, CssColor foreGround)
	{
		Button button = new Button(s);
		//button.setFont(theLargeFont);
		button.getElement().getStyle().setBackgroundColor(backGround.toString());
		button.getElement().getStyle().setColor(foreGround.toString());
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
}
