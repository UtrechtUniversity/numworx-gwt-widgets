package fi.normverdgwt.client;

import java.util.*;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PushButton;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
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
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;



public class NormaalPanel extends LayoutPanel

{	
	protected static int editBreedte = 290;	

	// fonts
	//Font theFont;
	//FontMetrics theFM;
	String fontString = "12px sans-serif";
	int fontSize = 12;
	
	// layout
	int offSet = 10;
	int cHeight, cHeight1, cHeight2;
	int breedte, hoogte;

	// de kanskeuzes
	//ButtonGroup kansGroup;
	String kansGroup = "kansGroup";
	RadioButton linksButton;
	RadioButton rechtsButton;
	RadioButton tweeGrenzenButton;
	
	Label berekenLabel;
	
	// de berekenkeuzes
	//ButtonGroup berekenGroup;
	String berekenGroup = "berekenGroup";
	RadioButton muButton;
	RadioButton sigmaButton;
	RadioButton grensButton;
	RadioButton grensLinksButton;
	RadioButton grensRechtsButton;
	RadioButton kansButton;

	// de parameters
	Label muLabel;
	Slider muSlider;
	TextBox muTextField;
	Label muWaardeLabel;

	Label sigmaLabel;
	Slider sigmaSlider;
	TextBox sigmaTextField;
	Label sigmaWaardeLabel;

	Label grensLabel;
	Slider grensSlider;
	TextBox grensTextField;
	Label grensWaardeLabel;

	Label grensLinksLabel;
	TextBox grensLinksTextField;
	Label grensLinksWaardeLabel;

	Label grensRechtsLabel;
	TextBox grensRechtsTextField;
	Label grensRechtsWaardeLabel;
	
	DoubleSlider tweeGrenzenSlider;

	Label kansLabel;
	Slider kansSlider;	
	TextBox kansTextField;
	Label kansWaardeLabel;

	Label muMetWaardeLabel;

	// kansKeuzes	
	static final int KANSLINKS = 0;
	static final int KANSRECHTS = 1;
	static final int TWEEGRENZEN = 2;
	int kansKeuze = KANSLINKS;
	//int kansKeuze = KANSRECHTS;
	//int kansKeuze = TWEEGRENZEN;
	
	// berekenKeuzes
	static final int BEREKENMU = 0;
	static final int BEREKENSIGMA = 1;
	static final int BEREKENGRENS = 2;
	static final int BEREKENKANS = 3;
	static final int BEREKENGRENSLINKS = 4;
	static final int BEREKENGRENSRECHTS = 5;
	int berekenKeuze = BEREKENKANS;
	
	int oldBerekenKeuze;

	// grafiek-grenzen in pixels
	// zet in plaatsComponenten()
	int xMin, xMax, yMin, yMax;

	// grafiek-grenzen als reeele waarden
	// voor de standaardnormale verdeling
	// worden nooit veranderd
	double minX = -45e-1d;
	double maxX = 45e-1d;
	double minY = 0;
	double maxY = 8e-1d;
	
	// grafiekgrenzen voor niet-standaardnormaal
	// wordt bij tekenen gezet op minX+mu en maxX+mu
	double minMuX = minX;
	double maxMuX = maxX;
	
	// parameters
	double mu = 0;
	int muDecimals = 2;
	String muString = "";
	double muMin = -10000;
	double muMax = 10000;
	double muSliderMin = mu - 1;
	double muSliderMax = mu + 1;
	
	double sigma = 1;
	int sigmaDecimals = 1;
	double sigmaMin = 1e-2d;
	double sigmaMax = 500;
	double sigmaSliderMin = sigmaMin;
	double sigmaSliderMax = sigma + 5e-1d;
	
	double grens = mu + 1;//sigma;
	double grensLinks = mu - 1;//sigma;
	double grensRechts = mu + 1;//sigma;
	int grensDecimals = 2;
	
	double kans = 841e-3d;
	int kansDecimals = 3;

	static final double NZERO = 1e-5d;

	static CssColor lightBlue = CssColor.make(198, 239, 247);
	static CssColor veryLightBlue = CssColor.make(220, 239, 247);
	static CssColor veryLightPurperBlue = CssColor.make(234, 229, 255);
	static CssColor pinkRed = CssColor.make(203, 14, 113);	
    static CssColor lightRed = CssColor.make(255, 99, 66);
    static CssColor lightGray = CssColor.make(192, 192, 192);
    static CssColor gray = CssColor.make(149, 149, 149);
    
    CssColor areaColor = veryLightPurperBlue;
    CssColor kansColor = pinkRed;
    CssColor muLineColor = lightGray;
    CssColor sigmaLineColor = gray;    
    
    Context2d nvContext2d;
    Canvas nvCanvas;
    
	boolean lowerGrensLabels = false;
	boolean lowerGrensLinksLabels = false;
	boolean lowerGrensRechtsLabels = false;

	// edit state variabelen
	
	// kans opties
	boolean kansLinksOptie = true;
	boolean kansRechtsOptie = true;
	boolean tweeGrenzenOptie = true;
	
	// bereken opties
	boolean muBerekenbaarOptie = false;
	boolean actualMuBerekenbaarOptie = false;
	
	boolean sigmaBerekenbaarOptie = false;
	boolean actualSigmaBerekenbaarOptie = false;
	
	// vaste waarde opties
	boolean muVastOptie = false;
	boolean sigmaVastOptie = false;
	
	// slider opties
	boolean muSliderOptie = false;
	boolean sigmaSliderOptie = false;
	boolean grensSliderOptie = true;
	boolean kansSliderOptie = false;
	
	boolean muZichtbaarOptie = true;
	boolean sigmaZichtbaarOptie = true;
	boolean grensZichtbaarOptie = true;
	boolean kansZichtbaarOptie = true;
	
	boolean muZichtbaarFigOptie = true;
	boolean sigmaZichtbaarFigOptie = true;
	boolean grensZichtbaarFigOptie = true;
	boolean kansZichtbaarFigOptie = true;

	boolean berekenbaarZichtbaar = true;

//GWT?	
	//RoundedPanel bgPanel1, bgPanel2, bgPanel3, bgPanel4, bgPanel5, bgPanel6;
	
//GWT	
	//static DecimalFormatSymbols dfs;
	//public static DecimalFormat df,df1,df2,df3;

	//nakijken:
	boolean kijkOpdrachtNa = false;

	boolean kijkMuNa;
	double antwoordMu;
	boolean kijkSigmaNa;
	double antwoordSigma;
	boolean kijkGrensNa;
	double antwoordGrens;
	boolean kijkGrensLinksNa;
	double antwoordGrensLinks;
	boolean kijkGrensRechtsNa;
	double antwoordGrensRechts;
	boolean kijkKansNa;
	double antwoordKans;
	
	int maxScore;	
	int score;
	
//GWT (4)	
	PushButton kijkNaButton;
	LayoutPanel kijkNaPanel;
	//JLabel vinkjeLabel;
	//JLabel kruisjeLabel;
	
	Vector listeners = new Vector();
	
	NormVerdGWT owner;
	
	boolean mouseDown = false;
	
	int rechtsButtonWidth, linksButtonWidth, tweeGrenzenButtonWidth;
	int berekenLabelWidth, muButtonWidth, sigmaButtonWidth, grensButtonWidth, 
		grensLinksButtonWidth, grensRechtsButtonWidth, kansButtonWidth;
	int muLabelWidth, muTextFieldWidth, muWaardeLabelWidth, sigmaLabelWidth, sigmaTextFieldWidth, sigmaWaardeLabelWidth, 
		grensLabelWidth, grensTextFieldWidth, grensWaardeLabelWidth, 
		kansLabelWidth, kansTextFieldWidth, kansWaardeLabelWidth,
		grensLinksLabelWidth, grensLinksTextFieldWidth, grensLinksWaardeLabelWidth,
		grensRechtsLabelWidth, grensRechtsTextFieldWidth, grensRechtsWaardeLabelWidth;
	
	Rectangle berekenRect, muRect, sigmaRect, grensRect, kansRect, grensLinksRect, grensRechtsRect;
	int leftWidth = 200;
	
	public NormaalPanel(NormVerdGWT o, int w, int h)
	{	
		owner = o;
		
		//setBackground(Color.white);
		//setLayout(null);
		
		nvCanvas = Canvas.createIfSupported();
		
		breedte = w;
		hoogte = h;
		nvCanvas.setWidth(w + "px");
		nvCanvas.setHeight(h + "px");
		nvCanvas.setCoordinateSpaceWidth(w);
		nvCanvas.setCoordinateSpaceHeight(h);
		
		add(nvCanvas);
		setWidgetLeftWidth(nvCanvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		setWidgetTopHeight(nvCanvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		
		nvContext2d = nvCanvas.getContext2d();
		
		MouseHandler mouseHandler = new MouseHandler();
		nvCanvas.addMouseDownHandler(mouseHandler);
		nvCanvas.addMouseMoveHandler(mouseHandler);
		nvCanvas.addMouseUpHandler(mouseHandler);
		
		//TouchWidgetMobileImpl twmi = new TouchWidgetMobileImpl();		
	
		//MGWTTouchHandler touchHandler = new MGWTTouchHandler();
		//twmi.addTouchStartHandler(kladjeGWTCanvas, touchHandler);
		//twmi.addTouchMoveHandler(kladjeGWTCanvas, touchHandler);
		//twmi.addTouchEndHandler(kladjeGWTCanvas, touchHandler);
		
		TouchHandler touchHandler = new TouchHandler();
		nvCanvas.addTouchStartHandler(touchHandler);
		nvCanvas.addTouchMoveHandler(touchHandler);
		nvCanvas.addTouchEndHandler(touchHandler);

		
		//dfs = new DecimalFormatSymbols();
		//dfs.setDecimalSeparator('.');
		//df = new DecimalFormat("0", dfs);
		//df1 = new DecimalFormat("0.#", dfs);
		//df2 = new DecimalFormat("0.##", dfs);
		//df3 = new DecimalFormat("0.###", dfs);
	
		//theFont = new Font("Dialog", Font.PLAIN, 12);
		//theFM = getFontMetrics(theFont);

		//cHeight = 3 * theFM.getHeight() / 2;
//GWT		
		cHeight = 20;
		cHeight1 = cHeight + offSet;
		cHeight2 = cHeight + offSet / 2;
		
		int width = 0;
		TextMetrics tm;

		// kansGroup
				
		//rechtsButton = new JRadioButton(NormaleVerdeling.rb.getString("kansRechtsTekst"));
		rechtsButton = new RadioButton(kansGroup, "Kans rechts");
//GWT (2)		
		//rechtsButton.setFont(theFont);
		//rechtsButton.setOpaque(false);
		tm = nvContext2d.measureText("Kans rechts");
		width = (int) Math.round(tm.getWidth());
		rechtsButtonWidth = 2 * width;
		
		linksButton = new RadioButton(kansGroup, "Kans links");
//GWT(2)		
		//linksButton.setFont(theFont);
		//linksButton.setOpaque(false);
		tm = nvContext2d.measureText("Kans links");
		width = (int) Math.round(tm.getWidth());
		linksButtonWidth = 2 * width;					 
		
		tweeGrenzenButton = new RadioButton(kansGroup, "2 grenzen");
//GWT(2)		
		//tweeGrenzenButton.setFont(theFont);
		//tweeGrenzenButton.setOpaque(false);		
		tm = nvContext2d.measureText("2 grenzen");
		width = (int) Math.round(tm.getWidth());
		tweeGrenzenButtonWidth = 2 * width;					 
		
		//kansGroup = new ButtonGroup();
		//kansGroup.add(rechtsButton);
		//kansGroup.add(linksButton);
		//kansGroup.add(tweeGrenzenButton);
		
//test		
		linksButton.addStyleName("radiobutton");
		rechtsButton.addStyleName("radiobutton");
		tweeGrenzenButton.addStyleName("radiobutton");

// dit in plaatsComponenten		
		//add(linksButton);
		//add(rechtsButton);
		//add(tweeGrenzenButton);
		
// dit in plaatsComponenten		
		//linksButton.setValue(true);
		
		// berekenGroup

		//width = theFM.stringWidth(NormaleVerdeling.rb.getString("grensRechtsTekst")) + 30;
		tm = nvContext2d.measureText("Bereken");
		width = (int) Math.round(tm.getWidth());
		berekenLabelWidth = 2 * width;
		berekenLabel = new Label("Bereken");
//GWT		
		//berekenLabel.setFont(theFont);
		
		//berekenLabel.setSize(width, cHeight);
		//add(berekenLabel);		
		
		tm = nvContext2d.measureText("m");
		width = (int) Math.round(tm.getWidth());
		muButtonWidth = 2 * width + 20;
		//muButton = new RadioButton(berekenGroup, "mu"); // "\u03BC";
		muButton = new RadioButton(berekenGroup, "\u03BC"); // "\u03BC";
//GWT		
		//muButton.setFont(theFont);
		//muButton.setOpaque(false);
		
		//muButton.setSize(width, cHeight);					 
		tm = nvContext2d.measureText("s");
		width = (int) Math.round(tm.getWidth());
		sigmaButtonWidth = 2 * width + 20;
		//sigmaButton = new RadioButton(berekenGroup, "sigma"); // "\u03C3"
		sigmaButton = new RadioButton(berekenGroup, "\u03C3"); // "\u03C3"
//GWT		
		//sigmaButton.setFont(theFont);
		//sigmaButton.setOpaque(false);		
		
		//sigmaButton.setSize(width, cHeight);					 
		
		tm = nvContext2d.measureText("Grens");
		width = (int) Math.round(tm.getWidth());
		grensButtonWidth = 2 * width + 20;
		grensButton = new RadioButton(berekenGroup, "Grens");
//GWT
		//grensButton.setFont(theFont);
		//grensButton.setOpaque(false);
		
		//grensButton.setSize(width, cHeight);					 
		tm = nvContext2d.measureText("Kans");
		width = (int) Math.round(tm.getWidth());
		kansButtonWidth = 2 * width + 20;
		kansButton = new RadioButton(berekenGroup, "Kans");
//GWT
		//kansButton.setFont(theFont);
		//kansButton.setOpaque(false);
		
		//kansButton.setSize(width, cHeight);					 
		
		tm = nvContext2d.measureText("Linkergrens");
		width = (int) Math.round(tm.getWidth());
		grensLinksButtonWidth = 2 * width + 20;
		grensLinksButton = new RadioButton(berekenGroup, "Linkergrens");
//GWT		
		//grensLinksButton.setFont(theFont);
		//grensLinksButton.setOpaque(false);
		//grensLinksButton.setSize(width, cHeight);					 

		
		tm = nvContext2d.measureText("Rechtergrens");
		width = (int) Math.round(tm.getWidth());
		grensRechtsButtonWidth = 2 * width + 20;
		grensRechtsButton = new RadioButton(berekenGroup, "Rechtergrens");
//GWT		
		//grensRechtsButton.setFont(theFont);
		//grensRechtsButton.setOpaque(false);
		
		//grensRechtsButton.setSize(width, cHeight);					 
							   
		//berekenGroup = new ButtonGroup();
		//berekenGroup.add(muButton);
		//berekenGroup.add(sigmaButton);
		//berekenGroup.add(grensButton);
		//berekenGroup.add(kansButton);
		//berekenGroup.add(grensLinksButton);		
		//berekenGroup.add(grensRechtsButton);				

//test		
		berekenLabel.addStyleName("radiobutton");
		muButton.addStyleName("radiobutton");
		sigmaButton.addStyleName("radiobutton");
		grensButton.addStyleName("radiobutton");
		grensLinksButton.addStyleName("radiobutton");
		grensRechtsButton.addStyleName("radiobutton");
		kansButton.addStyleName("radiobutton");

// in plaatsComponenten		
/*		
		add(muButton);
		muButton.addClickHandler(new BerekenKeuzeCH());		
		add(sigmaButton);
		sigmaButton.addClickHandler(new BerekenKeuzeCH());		
		add(grensButton);
		grensButton.addClickHandler(new BerekenKeuzeCH());		
		add(kansButton);
		kansButton.addClickHandler(new BerekenKeuzeCH());		
		add(grensLinksButton);		
		grensLinksButton.addClickHandler(new BerekenKeuzeCH());		
		add(grensRechtsButton);
		grensRechtsButton.addClickHandler(new BerekenKeuzeCH());		
*/
		
//GWT gokje		
		int height = 3 * 20 / 2; //theFM.getHeight() / 2;

		tm = nvContext2d.measureText("m = ");
		width = (int) Math.round(tm.getWidth());
		muLabelWidth = width + 5;
		muLabel = new Label("\u03BC" + " = "); // "\u03BC"
		
//GWT (3)		
		//muLabel.setFont(theFont);
		//width = theFM.stringWidth(muLabel.getText()) + 3;
		//muLabel.setSize(width, cHeight);
		
//in PC		
		//add(muLabel);

		tm = nvContext2d.measureText("XXXXX");
		width = (int) Math.round(tm.getWidth());
		muTextFieldWidth = width + 15;
		muTextField = new TextBox();
		
//GWT (4)		
		//muTextField.setFont(theFont);
		//width = theFM.stringWidth("XXXXX") + 10;
		//muTextField.setSize(width, cHeight);

//in PC		
		//add(muTextField);
		
//in PC		
		// listeners
		//muTextField.addKeyListener(new InputKL(muTextField, true));
		//muTextField.addActionListener(new TextAL(muTextField));		
		//muTextField.addFocusListener(new TextFL(muTextField));		

		muWaardeLabel = new Label("");
//GWT (3)		
		//muWaardeLabel.setFont(theFont);
		//muWaardeLabel.setForeground(Color.blue);
		//muWaardeLabel.setSize(width, cHeight);

//in PC		
		//add(muWaardeLabel);	
		
		muSlider = new Slider(this, 150, 75, 0, 0, nvContext2d, "mu");
		muSlider.zetEnabled(false);
		//add(muSlider);	
		//muSlider.addActionListener(this);

		tm = nvContext2d.measureText("m = ");
		width = (int) Math.round(tm.getWidth());
		sigmaLabelWidth = width + 5;
		sigmaLabel = new Label("\u03C3" + " = "); // "\u03C3"
		
//GWT (3)		
		//sigmaLabel.setFont(theFont);
		//width = theFM.stringWidth(sigmaLabel.getText()) + 3;
		//sigmaLabel.setSize(width, cHeight);
		
		//add(sigmaLabel);
		tm = nvContext2d.measureText("XXXXX");
		width = (int) Math.round(tm.getWidth());
		sigmaTextFieldWidth = width + 25;
		sigmaTextField = new TextBox();
//GWT (4)		
		//sigmaTextField.setFont(theFont);
		//width = theFM.stringWidth("XXXXX") + 10;
		//sigmaTextField.setSize(width, cHeight);
		//add(sigmaTextField);	
		// listeners
//GWT(3)		
		//sigmaTextField.addKeyListener(new InputKL(sigmaTextField, false));
		//sigmaTextField.addActionListener(new TextAL(sigmaTextField));		
		//sigmaTextField.addFocusListener(new TextFL(sigmaTextField));		

		sigmaWaardeLabel = new Label("");
//GWT(4)		
		//sigmaWaardeLabel.setFont(theFont);
		//sigmaWaardeLabel.setForeground(Color.blue);
		//sigmaWaardeLabel.setSize(width, cHeight);
		//add(sigmaWaardeLabel);	

		sigmaSlider = new Slider(this, 150, 75, 0, 0, nvContext2d, "sigma");
		sigmaSlider.zetEnabled(false);
		//add(sigmaSlider);	
		//sigmaSlider.addActionListener(this);		

		tm = nvContext2d.measureText("Grens G = ");
		width = (int) Math.round(tm.getWidth());
		grensLabelWidth = width + 20;
		grensLabel = new Label("Grens G = ");
		
		//grensLabel.setFont(theFont);
		//width = theFM.stringWidth(grensLabel.getText()) + 3;
		//grensLabel.setSize(width, cHeight);
		//add(grensLabel);

		tm = nvContext2d.measureText("XXXXXX");
		width = (int) Math.round(tm.getWidth());
		grensTextFieldWidth = width + 25;
		grensTextField = new TextBox();
		
		//grensTextField.setFont(theFont);
		//width = theFM.stringWidth("XXXXXX") + 10;
		//grensTextField.setSize(width, cHeight);
		//add(grensTextField);	
		// listeners
		//grensTextField.addKeyListener(new InputKL(grensTextField, true));
		//grensTextField.addActionListener(new TextAL(grensTextField));		
		//grensTextField.addFocusListener(new TextFL(grensTextField));		

		grensWaardeLabel = new Label("");
		
		//grensWaardeLabel.setFont(theFont);
		//grensWaardeLabel.setForeground(Color.blue);
		//grensWaardeLabel.setSize(width, cHeight);
		//add(grensWaardeLabel);	

		// kies maar wat
		grensSlider = new Slider(this, 100, 50, 0, 0, nvContext2d, "grens");
		grensSlider.zetShowLine(false);
		grensSlider.zetEnabled(false);
		//add(grensSlider);
		//grensSlider.addActionListener(this);

		tm = nvContext2d.measureText("Linkergrens L = ");
		width = (int) Math.round(tm.getWidth());
		grensLinksLabelWidth = width + 30;
		grensLinksLabel = new Label("Linkergrens L = ");
		
		//grensLinksLabel.setFont(theFont);
		//width = theFM.stringWidth(grensLinksLabel.getText()) + 3;
		//grensLinksLabel.setSize(width, cHeight);
		//add(grensLinksLabel);

		tm = nvContext2d.measureText("XXXXXX");
		width = (int) Math.round(tm.getWidth());
		grensLinksTextFieldWidth = width + 25;
		grensLinksTextField = new TextBox();
		
		//grensLinksTextField.setFont(theFont);
		//width = theFM.stringWidth("XXXXXX") + 10;
		//grensLinksTextField.setSize(width, cHeight);
		//add(grensLinksTextField);	
		// listeners
		//grensLinksTextField.addKeyListener(new InputKL(grensLinksTextField, true));
		//grensLinksTextField.addActionListener(new TextAL(grensLinksTextField));		
		//grensLinksTextField.addFocusListener(new TextFL(grensLinksTextField));		

		grensLinksWaardeLabel = new Label("");
		
		//grensLinksWaardeLabel.setFont(theFont);
		//grensLinksWaardeLabel.setForeground(Color.blue);
		//grensLinksWaardeLabel.setSize(width, cHeight);
		//add(grensLinksWaardeLabel);	

		tm = nvContext2d.measureText("Rechtergrens R = ");
		width = (int) Math.round(tm.getWidth());
		grensRechtsLabelWidth = width + 30;
		grensRechtsLabel = new Label("Rechtergrens R = ");
		
		//grensRechtsLabel.setFont(theFont);
		//width = theFM.stringWidth(grensRechtsLabel.getText()) + 3;
		//grensRechtsLabel.setSize(width, cHeight);
		//add(grensRechtsLabel);

		tm = nvContext2d.measureText("XXXXXX");
		width = (int) Math.round(tm.getWidth());
		grensRechtsTextFieldWidth = width + 25;
		grensRechtsTextField = new TextBox();
//GWT(4)		
		//grensRechtsTextField.setFont(theFont);
		//width = theFM.stringWidth("XXXXXX") + 10;
		//grensRechtsTextField.setSize(width, cHeight);
		//add(grensRechtsTextField);	
		// listeners
//GWT(3)		
		//grensRechtsTextField.addKeyListener(new InputKL(grensRechtsTextField, true));
		//grensRechtsTextField.addActionListener(new TextAL(grensRechtsTextField));		
		//grensRechtsTextField.addFocusListener(new TextFL(grensRechtsTextField));		

		grensRechtsWaardeLabel = new Label("");
//GWT(4)		
		//grensRechtsWaardeLabel.setFont(theFont);
		//grensRechtsWaardeLabel.setForeground(Color.blue);
		//grensRechtsWaardeLabel.setSize(width, cHeight);
		//add(grensRechtsWaardeLabel);	

		
		tweeGrenzenSlider = new DoubleSlider(this, 100, 30, 70, 0, 0, nvContext2d);
		tweeGrenzenSlider.zetShowLine(false);
		tweeGrenzenSlider.zetLinksEnabled(false);
		tweeGrenzenSlider.zetRechtsEnabled(false);
		//add(tweeGrenzenSlider);
		//tweeGrenzenSlider.addActionListener(this);
		
		tm = nvContext2d.measureText("Kans = ");
		width = (int) Math.round(tm.getWidth());
		kansLabelWidth = width + 5;
		kansLabel = new Label("Kans = ");
//GWT(4)		
		//kansLabel.setFont(theFont);	
		//width = theFM.stringWidth(kansLabel.getText()) + 3;
		//kansLabel.setSize(width, cHeight);
		//add(kansLabel);	
		
		tm = nvContext2d.measureText("XXXXXX");
		width = (int) Math.round(tm.getWidth());
		kansTextFieldWidth = width + 25;
		kansTextField = new TextBox();
//GWT(4)		
		//kansTextField.setFont(theFont);
		//width = theFM.stringWidth("XXXXXX") + 10;
		//kansTextField.setSize(width, cHeight);
		//add(kansTextField);	
		// listeners
//GWT(3)		
		//kansTextField.addKeyListener(new InputKL(kansTextField, false));
		//kansTextField.addActionListener(new TextAL(kansTextField));		
		//kansTextField.addFocusListener(new TextFL(kansTextField));		

		kansWaardeLabel = new Label("");
//GWT(4)		
		//kansWaardeLabel.setFont(theFont);
		//kansWaardeLabel.setForeground(Color.blue);
		//kansWaardeLabel.setSize(width, cHeight);
		//add(kansWaardeLabel);	

		
		kansSlider = new Slider(this, 150, 75, 0,0, nvContext2d, "kans");
		kansSlider.zetEnabled(false);
		//add(kansSlider);	
		//kansSlider.addActionListener(this);
		
		muWaardeLabel.addStyleName("label");
		sigmaWaardeLabel.addStyleName("label");
		grensWaardeLabel.addStyleName("label");
		grensLinksWaardeLabel.addStyleName("label");
		grensRechtsWaardeLabel.addStyleName("label");
		kansWaardeLabel.addStyleName("label");
		
		
		
		//muMetWaardeLabel = new JLabel(NormaleVerdeling.rb.getString("muTekst") + 
		//						      " = XXXX", SwingConstants.CENTER);
//GWT center?? echte mu		
		muMetWaardeLabel = new Label("mu" + " = XXXX");
		
//GWT(3)		
		//muMetWaardeLabel.setFont(theFont);
		//width = theFM.stringWidth(muMetWaardeLabel.getText()) + 3;		
		//muMetWaardeLabel.setSize(width, cHeight);
		

//GWT??
/* 
		bgPanel1 = new RoundedPanel(10);
		add(bgPanel1);
		bgPanel2 = new RoundedPanel(10);
		add(bgPanel2);
		bgPanel3 = new RoundedPanel(10);
		add(bgPanel3);
		bgPanel4 = new RoundedPanel(10);
		add(bgPanel4);
		bgPanel5 = new RoundedPanel(10);
		add(bgPanel5);
		bgPanel6 = new RoundedPanel(10);
		add(bgPanel6);
		
		// HIER!!
		setSize(w, h);
		size = new Dimension(w, h);
*/		
		
//tijdelijk eruit
		
		zetMu(mu, false, false);
		
		zetSigma(sigma, false, false);

		zetGrens(grens, false);		
		zetGrensLinks(grensLinks, false);				
		zetGrensRechts(grensRechts, false);		
						
		zetKans(kans, false);
		
// dit gaat niet goed hier		
		//zetKansKeuze(true);
		
// dit gaat niet goed hier		
		//zetBerekenKeuze(true);
		
		//bereken();
	
	
//GWT kijkNa-gebeuren		
		
		kijkNaButton = new PushButton("Kijk Na");
		kijkNaPanel = new LayoutPanel();
		//kijkNaButton.setFont(theFont);
		//kijkNaButton.setBounds(0, 0, 100, 20);
		//kijkNaButton.addActionListener(new KijkNaAL());
/*		
		java.net.URL imageURL = NormaleVerdeling.class.getResource("resources/goedkrul_en_klein.gif");
		if (imageURL != null) {
		    vinkjeLabel = new JLabel(new ImageIcon(imageURL));
		}
		else {
			System.out.println("Error reading goedkrul_en_klein.gif.");
			vinkjeLabel = new JLabel();
		}
		vinkjeLabel.setBounds(100, 0, 20, 20);
		imageURL = NormaleVerdeling.class.getResource("resources/foutkruis_klein.gif");
		if (imageURL != null) {
		    kruisjeLabel = new JLabel(new ImageIcon(imageURL));
		}
		else {
			System.out.println("Error reading foutkruis_klein.gif.");
			kruisjeLabel = new JLabel();
		}
		kruisjeLabel.setBounds(100, 0, 20, 20);
		
		vinkjeLabel.setVisible(false);
		kruisjeLabel.setVisible(false);
		
		
		kijkNaPanel = new JPanel(null);
		kijkNaPanel.setBackground(Color.WHITE);
		kijkNaPanel.setSize(120, 20);
		kijkNaPanel.add(kijkNaButton);
		kijkNaPanel.add(vinkjeLabel);
		kijkNaPanel.add(kruisjeLabel);
		kijkNaPanel.setVisible(kijkOpdrachtNa);
		
		add(kijkNaPanel);
*/		
		
//GWT: dit apart extern aanroepen  		
		//plaatsComponenten();

	}
	
	public void init()
	{
		zetKansKeuze(true);
		
		zetBerekenKeuze(true);
				
		bereken();
		
	}
	
	public void addListeners()
	{
		//ClickHandler ch = new KansKeuzeCH();
		ValueChangeHandler<Boolean> vch = new KansKeuzeVCH();
		
		//linksButton.addClickHandler(ch);
		//rechtsButton.addClickHandler(ch);
		//tweeGrenzenButton.addClickHandler(ch);

		linksButton.addValueChangeHandler(vch);
		rechtsButton.addValueChangeHandler(vch);
		tweeGrenzenButton.addValueChangeHandler(vch);
		
	}
	
	public Canvas getCanvas()
	{
		return nvCanvas;
	}
	
	

/*	
	public void zetKijkOpdrachtNa(boolean b)
	{
		kijkOpdrachtNa = b;
		kijkNaPanel.setVisible(kijkOpdrachtNa);
		
		plaatsComponenten();
	}
*/	
	
	public void plaatsComponenten(boolean init)
	{
//		im = null;
		
		// kansGroup

		// 3 opties
		if (kansLinksOptie && kansRechtsOptie && tweeGrenzenOptie)
		{
			//linksButton.setValue(true);
			
			if (init)
			{	add(rechtsButton);
				rechtsButton.addValueChangeHandler(new KansKeuzeVCH()); 			
			}
			setWidgetLeftWidth(rechtsButton, breedte - rechtsButtonWidth, Style.Unit.PX, rechtsButtonWidth, Style.Unit.PX);
			setWidgetTopHeight(rechtsButton, hoogte - cHeight, Style.Unit.PX, cHeight, Style.Unit.PX);
			
			if (init)
			{	add(linksButton);
				linksButton.addValueChangeHandler(new KansKeuzeVCH());
			}
			setWidgetLeftWidth(linksButton, breedte - 2 * rechtsButtonWidth, Style.Unit.PX, linksButtonWidth, Style.Unit.PX);
			setWidgetTopHeight(linksButton, hoogte - cHeight, Style.Unit.PX, cHeight, Style.Unit.PX);
			
			if (init)
			{	add(tweeGrenzenButton);
				tweeGrenzenButton.addValueChangeHandler(new KansKeuzeVCH());
			}
			setWidgetLeftWidth(tweeGrenzenButton, offSet, Style.Unit.PX, tweeGrenzenButtonWidth, Style.Unit.PX);
			setWidgetTopHeight(tweeGrenzenButton, hoogte - cHeight, Style.Unit.PX, cHeight, Style.Unit.PX);
			
		}
		//2 opties
		else if (!kansLinksOptie && kansRechtsOptie && tweeGrenzenOptie)
		{
			if (init)
			{	add(rechtsButton);
				rechtsButton.addValueChangeHandler(new KansKeuzeVCH()); 			
			}
			setWidgetLeftWidth(rechtsButton, breedte - rechtsButtonWidth, Style.Unit.PX, rechtsButtonWidth, Style.Unit.PX);
			setWidgetTopHeight(rechtsButton, hoogte - cHeight, Style.Unit.PX, cHeight, Style.Unit.PX);

			if (init)
			{	add(tweeGrenzenButton);
				tweeGrenzenButton.addValueChangeHandler(new KansKeuzeVCH());
			}
			setWidgetLeftWidth(tweeGrenzenButton, offSet, Style.Unit.PX, tweeGrenzenButtonWidth, Style.Unit.PX);
			setWidgetTopHeight(tweeGrenzenButton, hoogte - cHeight, Style.Unit.PX, cHeight, Style.Unit.PX);
			
		}
		else if (kansLinksOptie && !kansRechtsOptie && tweeGrenzenOptie)
		{
			if (init)
			{	add(linksButton);
				linksButton.addValueChangeHandler(new KansKeuzeVCH());
			}
			setWidgetLeftWidth(linksButton, breedte - linksButtonWidth, Style.Unit.PX, linksButtonWidth, Style.Unit.PX);
			setWidgetTopHeight(linksButton, hoogte - cHeight, Style.Unit.PX, cHeight, Style.Unit.PX);
			
			if (init)
			{	add(tweeGrenzenButton);
				tweeGrenzenButton.addValueChangeHandler(new KansKeuzeVCH());
			}
			setWidgetLeftWidth(tweeGrenzenButton, offSet, Style.Unit.PX, tweeGrenzenButtonWidth, Style.Unit.PX);
			setWidgetTopHeight(tweeGrenzenButton, hoogte - cHeight, Style.Unit.PX, cHeight, Style.Unit.PX);
			
		}
		else if (kansLinksOptie && kansRechtsOptie && !tweeGrenzenOptie)
		{
			if (init)
			{	add(rechtsButton);
				rechtsButton.addValueChangeHandler(new KansKeuzeVCH()); 			
			}
			setWidgetLeftWidth(rechtsButton, breedte - rechtsButtonWidth, Style.Unit.PX, rechtsButtonWidth, Style.Unit.PX);
			setWidgetTopHeight(rechtsButton, hoogte - cHeight, Style.Unit.PX, cHeight, Style.Unit.PX);

			if (init)
			{	add(linksButton);
				linksButton.addValueChangeHandler(new KansKeuzeVCH());
			}
			setWidgetLeftWidth(linksButton, breedte - 2 * rechtsButtonWidth, Style.Unit.PX, linksButtonWidth, Style.Unit.PX);
			setWidgetTopHeight(linksButton, hoogte - cHeight, Style.Unit.PX, cHeight, Style.Unit.PX);
			
		}
		// 1 optie: nothing to do
			

/*		
		if (kansRechtsOptie)
		{
			rechtsButton.setLocation(getSize().width - rechtsButton.getSize().width,
								     getSize().height - cHeight);
			linksButton.setLocation(getSize().width - 2 * rechtsButton.getSize().width,
							  	getSize().height - cHeight);
		}
		else
		{
			rechtsButton.setLocation(getSize().width - rechtsButton.getSize().width,
								     getSize().height - cHeight);
			linksButton.setLocation(getSize().width - linksButton.getSize().width,
							  	getSize().height - cHeight);
		}

		tweeGrenzenButton.setLocation(offSet,
							      	  getSize().height - cHeight);

		bgPanel5.setBounds(0, tweeGrenzenButton.getY(),
						   getWidth(),
						   getHeight() - tweeGrenzenButton.getY() + 6);
		
		if (!rechtsButton.isVisible() && !tweeGrenzenButton.isVisible()) 
				bgPanel5.setVisible(false);
*/		
		int yPos = 3;

		// berekenGroup
		
		if (berekenbaarZichtbaar)
		{	
			
			
			//berekenLabel.setLocation(getSize().width - berekenLabel.getSize().width, yPos);
			if (init)
				add(berekenLabel);
			setWidgetLeftWidth(berekenLabel, breedte - grensRechtsButtonWidth + 25, Style.Unit.PX, berekenLabelWidth, Style.Unit.PX);
			setWidgetTopHeight(berekenLabel, yPos, Style.Unit.PX, cHeight, Style.Unit.PX);
		
			yPos += cHeight;						 	
			
			if (init)
			{	add(muButton);
				muButton.addValueChangeHandler(new BerekenKeuzeVCH());
			}
			if (actualMuBerekenbaarOptie)
			{	
				//muButton.setVisible(true);
				setWidgetVisible(muButton, true);
				
				//muButton.setLocation(getSize().width - muButton.getSize().width, yPos);
				setWidgetLeftWidth(muButton, breedte - grensRechtsButtonWidth + 25, Style.Unit.PX, muButtonWidth, Style.Unit.PX);
				setWidgetTopHeight(muButton, yPos, Style.Unit.PX, cHeight, Style.Unit.PX);
			
				yPos += cHeight;						 							     
			}
			else 
			{	//muButton.setVisible(false);
				setWidgetVisible(muButton, false);
			}
			
			
			if (init)
			{	add(sigmaButton);
				sigmaButton.addValueChangeHandler(new BerekenKeuzeVCH());
			}
			if (actualSigmaBerekenbaarOptie)
			{	
				//sigmaButton.setVisible(true);
				setWidgetVisible(sigmaButton, true);
				
				//sigmaButton.setLocation(getSize().width - sigmaButton.getSize().width,  yPos);
				setWidgetLeftWidth(sigmaButton, breedte - grensRechtsButtonWidth + 25, Style.Unit.PX, sigmaButtonWidth, Style.Unit.PX);
				setWidgetTopHeight(sigmaButton, yPos, Style.Unit.PX, cHeight, Style.Unit.PX);
			
				yPos += cHeight;
			}
			else
			{	//sigmaButton.setVisible(false);
				setWidgetVisible(sigmaButton, false);
			}
			
			
			
			if (init)
			{	add(grensButton);
				grensButton.addValueChangeHandler(new BerekenKeuzeVCH());
				add(grensLinksButton);
				grensLinksButton.addValueChangeHandler(new BerekenKeuzeVCH());
				add(grensRechtsButton);
				grensRechtsButton.addValueChangeHandler(new BerekenKeuzeVCH());
			}			
		
			if (kansKeuze == TWEEGRENZEN)
			{	
				setWidgetVisible(grensButton, false);
				setWidgetVisible(grensLinksButton, true);
				setWidgetVisible(grensRechtsButton, true);				
				
				//grensLinksButton.setLocation(getSize().width - grensLinksButton.getSize().width, yPos);
				setWidgetLeftWidth(grensLinksButton, breedte - grensRechtsButtonWidth + 25, Style.Unit.PX, grensLinksButtonWidth, Style.Unit.PX);
				setWidgetTopHeight(grensLinksButton, yPos, Style.Unit.PX, cHeight, Style.Unit.PX);
			
				yPos += cHeight;			    

				//grensRechtsButton.setLocation(getSize().width - grensRechtsButton.getSize().width, yPos);
				setWidgetLeftWidth(grensRechtsButton, breedte - grensRechtsButtonWidth + 25, Style.Unit.PX, grensRechtsButtonWidth, Style.Unit.PX);
				setWidgetTopHeight(grensRechtsButton, yPos, Style.Unit.PX, cHeight, Style.Unit.PX);
				
				yPos += cHeight;			                 						                  
			}	
			else
			{
				setWidgetVisible(grensButton, true);
				setWidgetVisible(grensLinksButton, false);
				setWidgetVisible(grensRechtsButton, false);				

				//grensButton.setLocation(getSize().width - grensButton.getSize().width, yPos);
				setWidgetLeftWidth(grensButton, breedte - grensRechtsButtonWidth + 25, Style.Unit.PX, grensButtonWidth, Style.Unit.PX);
				setWidgetTopHeight(grensButton, yPos, Style.Unit.PX, cHeight, Style.Unit.PX);
				
				
				yPos += cHeight;			                 						                  						            
			}
			
			if (init)
			{	add(kansButton);
				kansButton.addValueChangeHandler(new BerekenKeuzeVCH());
			}
		    //kansButton.setLocation(getSize().width - kansButton.getSize().width, yPos);
		    setWidgetLeftWidth(kansButton, breedte - grensRechtsButtonWidth + 25, Style.Unit.PX, kansButtonWidth, Style.Unit.PX);
			setWidgetTopHeight(kansButton, yPos, Style.Unit.PX, cHeight, Style.Unit.PX);
		    
		    
		    
		    yPos += cHeight;	
		    
		    berekenRect = new Rectangle(breedte - grensRechtsButtonWidth + 15, 0, grensRechtsButtonWidth - 15, yPos + 10); 

//GWT??
/*		    
		    bgPanel6.setBounds(getSize().width - berekenLabel.getSize().width - 10,
		    				   berekenLabel.getY() - 3,
		    				   berekenLabel.getSize().width + 10,
		    				   yPos - berekenLabel.getY() + 11);
*/		    				   

		}
// berekenzichtbaar is instelling docent		}
/*		
		else
		{
			berekenLabel.setVisible(false);
			grensLinksButton.setVisible(false);
			grensRechtsButton.setVisible(false);
			grensButton.setVisible(false);
			kansButton.setVisible(false);
			bgPanel6.setVisible(false);
		}
*/		
		
		if (kijkOpdrachtNa && (kijkNaPanel != null))
		{
			if (init)
			{
				add(kijkNaPanel);
				kijkNaPanel.add(kijkNaButton);
//if (owner.goedKrulImage != null)				
				kijkNaPanel.add(owner.goedKrulImage);
//else
//System.out.println("gki = null");

//if (owner.foutKruisImage != null)
				kijkNaPanel.add(owner.foutKruisImage);
//else
//	System.out.println("fki = null");

				kijkNaButton.addClickHandler(new PushClickHandler());
			}
			int y = 50;
			if (berekenbaarZichtbaar)
				y = 120;
			
			setWidgetLeftWidth(kijkNaPanel, breedte - 120, Style.Unit.PX, 120, Style.Unit.PX);
			setWidgetTopHeight(kijkNaPanel, y, Style.Unit.PX, cHeight2, Style.Unit.PX);
			
			kijkNaPanel.setWidgetLeftWidth(kijkNaButton, 0, Style.Unit.PX, 80, Style.Unit.PX);
			kijkNaPanel.setWidgetTopHeight(kijkNaButton, 0, Style.Unit.PX, cHeight2, Style.Unit.PX);
			
//if (owner.goedKrulImage != null)
//{	
			kijkNaPanel.setWidgetLeftWidth(owner.goedKrulImage, 80, Style.Unit.PX, 30, Style.Unit.PX);
			kijkNaPanel.setWidgetTopHeight(owner.goedKrulImage, 0, Style.Unit.PX, cHeight2, Style.Unit.PX);
//}			
			
//if (owner.foutKruisImage != null)
//{	
			kijkNaPanel.setWidgetLeftWidth(owner.foutKruisImage, 80, Style.Unit.PX, 30, Style.Unit.PX);
			kijkNaPanel.setWidgetTopHeight(owner.foutKruisImage, 0, Style.Unit.PX, cHeight2, Style.Unit.PX);
//}			
			
			kijkNaPanel.setWidgetVisible(owner.goedKrulImage, false);
			kijkNaPanel.setWidgetVisible(owner.foutKruisImage, false);
			
			//kijkNaPanel.setLocation(getSize().width - kijkNaPanel.getSize().width - 20, y);
		}
		else
		{
			//if (kijkNaPanel != null)
			//{	setWidgetVisible(kijkNaPanel, false);
			//}
		}
		
		// parameters
		yPos = 7; // was 3
		
		if (init)
		{	add(muLabel);
			add(muTextField);
			muTextField.addKeyDownHandler(new TextBoxKeyDownHandler(muTextField));
			add(muWaardeLabel);
		}
		if (muZichtbaarOptie)
		{
			
			
			//muLabel.setLocation(offSet, yPos);
		    setWidgetLeftWidth(muLabel, offSet, Style.Unit.PX, muLabelWidth, Style.Unit.PX);
			setWidgetTopHeight(muLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);
			
			//muTextField.setLocation(muLabel.getLocation().x + muLabel.getSize().width, yPos);
		    setWidgetLeftWidth(muTextField, offSet + muLabelWidth, Style.Unit.PX, muTextFieldWidth, Style.Unit.PX);
			setWidgetTopHeight(muTextField, yPos, Style.Unit.PX, cHeight2, Style.Unit.PX);
			
			//muWaardeLabel.setLocation(muLabel.getLocation().x + muLabel.getSize().width, yPos);
		    setWidgetLeftWidth(muWaardeLabel, offSet + muLabelWidth, Style.Unit.PX, muTextFieldWidth, Style.Unit.PX);
			setWidgetTopHeight(muWaardeLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);

			setWidgetVisible(muLabel, true);
			setWidgetVisible(muTextField, !muVastOptie && !(berekenKeuze == BEREKENMU));
			setWidgetVisible(muWaardeLabel, muVastOptie || (berekenKeuze == BEREKENMU));
			
			yPos += cHeight2;
			
			muRect = new Rectangle(0, yPos - cHeight2 - 4, leftWidth, cHeight2 + 8);
			
			
			if ((berekenKeuze != BEREKENMU) && !muVastOptie &&  muSliderOptie)
			{	//muSlider = new Slider(this, 150, 75, offSet, yPos + 2, nvContext2d, "mu");
				muSlider.setLocation(offSet, yPos + 3);
				muSlider.zetEnabled(true);
				//muSlider.setLocation(offSet, yPos);// + (cHeight - muSlider.getSize().height) / 2); 
				//yPos += muSlider.getSize().height-5;
				yPos += muSlider.hoogte - 5;
				
				muRect = new Rectangle(0, yPos - muSlider.hoogte + 5 - cHeight2 - 4, leftWidth, 
							           cHeight2 + 8 + muSlider.hoogte - 5);
			}	
			else
				muSlider.zetEnabled(false);
			
			yPos += 10;

//GWT?			
			//bgPanel1.setBounds(0,muLabel.getY()-3,180,yPos-muLabel.getY()-4);
		}
		else
		{
			setWidgetVisible(muLabel, false);
			setWidgetVisible(muTextField, false);
			setWidgetVisible(muWaardeLabel, false);
			
		}
		
// muZichtbaar is instelling docent
/*		
		else
		{	//muLabel.setVisible(false);
			//muTextField.setVisible(false);
			//muWaardeLabel.setVisible(false);
			//muSlider.setVisible(false);
			//bgPanel1.setVisible(false);
		}
*/		
		if (init)
		{	add(sigmaLabel);
			add(sigmaTextField);
			sigmaTextField.addKeyDownHandler(new TextBoxKeyDownHandler(sigmaTextField));
			add(sigmaWaardeLabel);
		}
		if (sigmaZichtbaarOptie)
		{	
			//sigmaLabel.setLocation(offSet, yPos);
		    setWidgetLeftWidth(sigmaLabel, offSet, Style.Unit.PX, sigmaLabelWidth, Style.Unit.PX);
			setWidgetTopHeight(sigmaLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);
			
			//sigmaTextField.setLocation(sigmaLabel.getLocation().x + sigmaLabel.getSize().width,	yPos);
		    setWidgetLeftWidth(sigmaTextField, offSet + sigmaLabelWidth, Style.Unit.PX, sigmaTextFieldWidth, Style.Unit.PX);
			setWidgetTopHeight(sigmaTextField, yPos, Style.Unit.PX, cHeight2, Style.Unit.PX);
			
			//sigmaWaardeLabel.setLocation(sigmaLabel.getLocation().x + sigmaLabel.getSize().width, yPos);
		    setWidgetLeftWidth(sigmaWaardeLabel, offSet + sigmaLabelWidth, Style.Unit.PX, sigmaTextFieldWidth, Style.Unit.PX);
			setWidgetTopHeight(sigmaWaardeLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);
			
			setWidgetVisible(sigmaLabel, true);
			setWidgetVisible(sigmaTextField, !sigmaVastOptie && !(berekenKeuze == BEREKENSIGMA));
			setWidgetVisible(sigmaWaardeLabel, sigmaVastOptie || (berekenKeuze == BEREKENSIGMA));
			
			yPos += cHeight2;
			
			sigmaRect = new Rectangle(0, yPos - cHeight2 - 4, leftWidth, cHeight2 + 8);
			

			if ((berekenKeuze != BEREKENSIGMA) && !sigmaVastOptie && sigmaSliderOptie)
			{	//sigmaSlider = new Slider(this, 150,75, offSet, yPos + 3, nvContext2d, "sigma");
				
				sigmaSlider.setLocation(offSet, yPos + 3);
				sigmaSlider.zetEnabled(true);
				//yPos += sigmaSlider.getSize().height-5;
				yPos += sigmaSlider.hoogte-5;
				
				sigmaRect = new Rectangle(0, yPos - sigmaSlider.hoogte + 5 - cHeight2 - 4, leftWidth, 
				                          cHeight2 + 8 + sigmaSlider.hoogte - 5);
			}
			else
				sigmaSlider.zetEnabled(false);
			
			
			yPos += 10;
			
//GWT??			
			//bgPanel2.setBounds(0,sigmaLabel.getY()-3,180,yPos-sigmaLabel.getY()-4);
		}
		else
		{
			setWidgetVisible(sigmaLabel, false);
			setWidgetVisible(sigmaTextField, false);
			setWidgetVisible(sigmaWaardeLabel, false);
			
		}
		
// sigma zichtbaar is instelling docent
/* 		
		else
		{	sigmaLabel.setVisible(false);
			sigmaTextField.setVisible(false);
			sigmaWaardeLabel.setVisible(false);
			sigmaSlider.setVisible(false);
			bgPanel2.setVisible(false);
		}
*/
		
		if (init)
		{	add(grensLabel);
			add(grensTextField);
			grensTextField.addKeyDownHandler(new TextBoxKeyDownHandler(grensTextField));
			add(grensWaardeLabel);
			add(grensLinksLabel);
			add(grensLinksTextField);
			grensLinksTextField.addKeyDownHandler(new TextBoxKeyDownHandler(grensLinksTextField));
			add(grensLinksWaardeLabel);
			add(grensRechtsLabel);
			add(grensRechtsTextField);
			grensRechtsTextField.addKeyDownHandler(new TextBoxKeyDownHandler(grensRechtsTextField));
			add(grensRechtsWaardeLabel);

		}
		
		if (grensZichtbaarOptie)
		{	if (kansKeuze == TWEEGRENZEN)
			{	
				//grensLinksLabel.setLocation(offSet, yPos);
			    setWidgetLeftWidth(grensLinksLabel, offSet, Style.Unit.PX, grensLinksLabelWidth, Style.Unit.PX);
				setWidgetTopHeight(grensLinksLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);
				
				//grensLinksTextField.setLocation(grensLinksLabel.getLocation().x + grensLinksLabel.getSize().width, yPos);
			    setWidgetLeftWidth(grensLinksTextField, offSet + grensLinksLabelWidth, Style.Unit.PX, grensLinksTextFieldWidth, Style.Unit.PX);
				setWidgetTopHeight(grensLinksTextField, yPos, Style.Unit.PX, cHeight2, Style.Unit.PX);
				
				//grensLinksWaardeLabel.setLocation(grensLinksLabel.getLocation().x + grensLinksLabel.getSize().width, yPos);
			    setWidgetLeftWidth(grensLinksWaardeLabel, offSet + grensLinksLabelWidth, Style.Unit.PX, grensLinksTextFieldWidth, Style.Unit.PX);
				setWidgetTopHeight(grensLinksWaardeLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);

				grensLinksRect = new Rectangle(0, yPos - 4, leftWidth, cHeight2 + 8);
				
				yPos += cHeight1 + offSet / 2;
				
				//grensLinksRect = new Rectangle(0, yPos - cHeight1, leftWidth, cHeight1);
				
				//grensRechtsLabel.setLocation(offSet, yPos);
			    setWidgetLeftWidth(grensRechtsLabel, offSet, Style.Unit.PX, grensRechtsLabelWidth, Style.Unit.PX);
				setWidgetTopHeight(grensRechtsLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);
				
				//grensRechtsTextField.setLocation(grensRechtsLabel.getLocation().x + grensRechtsLabel.getSize().width, yPos);
			    setWidgetLeftWidth(grensRechtsTextField, offSet + grensRechtsLabelWidth, Style.Unit.PX, grensRechtsTextFieldWidth, Style.Unit.PX);
				setWidgetTopHeight(grensRechtsTextField, yPos, Style.Unit.PX, cHeight2, Style.Unit.PX);
				
				//grensRechtsWaardeLabel.setLocation(	grensRechtsLabel.getLocation().x + grensRechtsLabel.getSize().width, yPos);
			    setWidgetLeftWidth(grensRechtsWaardeLabel, offSet + grensRechtsLabelWidth, Style.Unit.PX, grensRechtsTextFieldWidth, Style.Unit.PX);
				setWidgetTopHeight(grensRechtsWaardeLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);
				
				yPos += cHeight2;			
				grensRechtsRect = new Rectangle(0, yPos - cHeight2 - 4, leftWidth, cHeight2 + 8);
				
			}
			else // kansKeuze KANSLINKS OF KANSRECHTS
			{
				//grensLabel.setLocation(offSet, yPos);
			    setWidgetLeftWidth(grensLabel, offSet, Style.Unit.PX, grensLabelWidth, Style.Unit.PX);
				setWidgetTopHeight(grensLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);

				//grensTextField.setLocation(	grensLabel.getLocation().x + grensLabel.getSize().width, yPos);
			    setWidgetLeftWidth(grensTextField, offSet + grensLabelWidth, Style.Unit.PX, grensTextFieldWidth, Style.Unit.PX);
				setWidgetTopHeight(grensTextField, yPos, Style.Unit.PX, cHeight2, Style.Unit.PX);
				
				//grensWaardeLabel.setLocation(grensLabel.getLocation().x + grensLabel.getSize().width, yPos);
			    setWidgetLeftWidth(grensWaardeLabel, offSet + grensLabelWidth, Style.Unit.PX, grensTextFieldWidth, Style.Unit.PX);
				setWidgetTopHeight(grensWaardeLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);

				yPos += cHeight2;
				
				grensRect = new Rectangle(0, yPos - cHeight2 - 4, leftWidth, cHeight2 + 8);
			}
		
			if (kansKeuze == TWEEGRENZEN)
			{
				setWidgetVisible(grensLinksLabel, true);
				setWidgetVisible(grensLinksTextField, !(berekenKeuze == BEREKENGRENSLINKS));
				setWidgetVisible(grensLinksWaardeLabel, (berekenKeuze == BEREKENGRENSLINKS));
				setWidgetVisible(grensRechtsLabel, true);
				setWidgetVisible(grensRechtsTextField, !(berekenKeuze == BEREKENGRENSRECHTS));
				setWidgetVisible(grensRechtsWaardeLabel, (berekenKeuze == BEREKENGRENSRECHTS));

				setWidgetVisible(grensLabel, false);
				setWidgetVisible(grensTextField, false);
				setWidgetVisible(grensWaardeLabel, false);
				
				if (grensSliderOptie && (berekenKeuze != BEREKENGRENSLINKS))
					tweeGrenzenSlider.zetLinksEnabled(true);
				else 
					tweeGrenzenSlider.zetLinksEnabled(false);
				if (grensSliderOptie && (berekenKeuze != BEREKENGRENSRECHTS))
					tweeGrenzenSlider.zetRechtsEnabled(true);
				else
					tweeGrenzenSlider.zetRechtsEnabled(false);
				
				grensSlider.zetEnabled(false);
				
				
			}
			else
			{
				setWidgetVisible(grensLabel, true);
				setWidgetVisible(grensTextField, !(berekenKeuze == BEREKENGRENS));
				setWidgetVisible(grensWaardeLabel, (berekenKeuze == BEREKENGRENS));
				
				setWidgetVisible(grensLinksLabel, false);
				setWidgetVisible(grensLinksTextField, false);
				setWidgetVisible(grensLinksWaardeLabel, false);
				setWidgetVisible(grensRechtsLabel, false);
				setWidgetVisible(grensRechtsTextField, false);
				setWidgetVisible(grensRechtsWaardeLabel, false);

				if (grensSliderOptie && (berekenKeuze != BEREKENGRENS))
					grensSlider.zetEnabled(true);
				else 
					grensSlider.zetEnabled(false);
					
				tweeGrenzenSlider.zetLinksEnabled(false);
				tweeGrenzenSlider.zetRechtsEnabled(false);
				
			}
		
			yPos += 10;
			
//GWT?			
			//bgPanel3.setBounds(0,grensLabel.getY()-3,180,yPos-grensLabel.getY()-4);
			//if (kansKeuze == TWEEGRENZEN)
				//bgPanel3.setBounds(0,grensLinksLabel.getY()-3,180,yPos-grensLinksLabel.getY()-4);
		}
		else
		{
			setWidgetVisible(grensLabel, false);
			setWidgetVisible(grensTextField, false);
			setWidgetVisible(grensWaardeLabel, false);
			setWidgetVisible(grensLinksLabel, false);
			setWidgetVisible(grensLinksTextField, false);
			setWidgetVisible(grensLinksWaardeLabel, false);
			setWidgetVisible(grensRechtsLabel, false);
			setWidgetVisible(grensRechtsTextField, false);
			setWidgetVisible(grensRechtsWaardeLabel, false);
		}
		
// grenzen zichtbaar is instelling docent
/*  		
		else
		{	grensLabel.setVisible(false);
			grensTextField.setVisible(false);
			grensWaardeLabel.setVisible(false);
			grensLinksLabel.setVisible(false);
			grensLinksTextField.setVisible(false);
			grensLinksWaardeLabel.setVisible(false);
			grensRechtsLabel.setVisible(false);
			grensRechtsTextField.setVisible(false);
			grensRechtsWaardeLabel.setVisible(false);
			bgPanel3.setVisible(false);
		}
*/		

		if (init)
		{	add(kansLabel);
			add(kansTextField);
			kansTextField.addKeyDownHandler(new TextBoxKeyDownHandler(kansTextField));
			add(kansWaardeLabel);
		}
		
		if (kansZichtbaarOptie)
		{	
			// kansLabel kan meerdere labels hebben
			TextMetrics tm = nvContext2d.measureText(kansLabel.getText());
			int width = (int) Math.round(tm.getWidth());
			kansLabelWidth = width + 10;
			//int width = theFM.stringWidth(kansLabel.getText()) + 3;
			//kansLabel.setBounds(offSet, yPos, width, cHeight);
		    setWidgetLeftWidth(kansLabel, offSet, Style.Unit.PX, kansLabelWidth, Style.Unit.PX);
			setWidgetTopHeight(kansLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);

			//kansTextField.setLocation(kansLabel.getLocation().x + kansLabel.getSize().width,yPos);
		    setWidgetLeftWidth(kansTextField, offSet + kansLabelWidth, Style.Unit.PX, kansTextFieldWidth, Style.Unit.PX);
			setWidgetTopHeight(kansTextField, yPos, Style.Unit.PX, cHeight2, Style.Unit.PX);
			
			//kansWaardeLabel.setLocation(kansLabel.getLocation().x + kansLabel.getSize().width, yPos);
		    setWidgetLeftWidth(kansWaardeLabel, offSet + kansLabelWidth, Style.Unit.PX, kansTextFieldWidth, Style.Unit.PX);
			setWidgetTopHeight(kansWaardeLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);

			setWidgetVisible(kansLabel, true);
			if (berekenKeuze == BEREKENKANS)
			{	setWidgetVisible(kansTextField, false);
				setWidgetVisible(kansWaardeLabel, true);
			}
			else
			{	setWidgetVisible(kansTextField, true);
				setWidgetVisible(kansWaardeLabel, false);
			}
				

			
			yPos += cHeight2;
			
			kansRect = new Rectangle(0, yPos - cHeight2 - 4, leftWidth, cHeight2 + 8);
			
			if (berekenKeuze != BEREKENKANS && kansSliderOptie)
			{	
				//kansSlider = new Slider(this, 150, 75, offSet, yPos + 3, nvContext2d, "kans");
				kansSlider.setLocation(offSet,yPos + 3);// + (cHeight - kansSlider.getSize().height) / 2); 
				kansSlider.zetEnabled(true);
				//yPos += kansSlider.getSize().height-5;
				yPos += kansSlider.hoogte-5;
				
				kansRect = new Rectangle(0, yPos - kansSlider.hoogte + 5 - cHeight2 - 4, leftWidth, 
                                         cHeight2 + 8 + kansSlider.hoogte - 5);
			}	
			else 
				kansSlider.zetEnabled(false);
			
			yPos += 10;
			
//GWT??			
			//bgPanel4.setBounds(0,kansLabel.getY()-3,180,yPos-kansLabel.getY()-4);
		}
		else
		{	setWidgetVisible(kansLabel, false);
			setWidgetVisible(kansTextField, false);
			setWidgetVisible(kansWaardeLabel, false);
		}
		
// grenzen zichtbaar is instelling docent
/*		
		else
		{	kansLabel.setVisible(false);
			kansTextField.setVisible(false);
			kansWaardeLabel.setVisible(false);
			kansSlider.setVisible(false);
			bgPanel4.setVisible(false);
		}
*/			

//wat is dit?		
/*		
		
		int hSpace = (getSize().width - muMetWaardeLabel.getSize().width) / 2;

		muMetWaardeLabel.setLocation(hSpace, getSize().height - 2*cHeight+4);
*/
		// grafiekParameters

		xMin = offSet;
		xMax = breedte - offSet;
		yMin = hoogte - 5 * cHeight / 2;
		yMax = offSet;	   	

		// een grens
		
		grensSlider.zetLengte(xMax - xMin);
		grensSlider.setLocation(xMin - 5, yMin - grensSlider.hoogte / 2 + 1);
		zetGrensSlider();

		// twee grenzen
	
		tweeGrenzenSlider.zetLengte(xMax - xMin);
		tweeGrenzenSlider.setLocation(xMin - 5, yMin - tweeGrenzenSlider.hoogte / 2 + 1);
		
		zetGrensLinksSlider();
		zetGrensRechtsSlider();
		
		//paint();
	}

	public double round(double d, int decs)
	{	double factor = Math.pow(10, decs);
		return Math.round(d * factor) / factor;
	}	
	
	public void zetMu(double waarde, boolean bereken, boolean resetSlider)
	{	
		//mu = waarde;
		double muWaarde = waarde;

		if (muWaarde > muMax - NZERO)
			muWaarde = muMax;
		if (muWaarde < muMin + NZERO)
			muWaarde = muMin;
				
		double deltaMu = muWaarde - mu;
		
		mu = muWaarde;		
				

		// input via TextField
		if (resetSlider)
		{	
		
			minMuX = minX + mu;
			maxMuX = maxX + mu;

			// zet nieuwe grenzen voor de muSlider
			muSliderMin = minMuX;
			muSliderMax = maxMuX;

			zetMuSlider();
		}
		// input via slider, afrondingsfouten
		//if (!bereken && !resetSlider)
		else
		{		
		
			if (mu < (muSliderMin + NZERO))
			{	mu = muSliderMin;
			}
			else if (mu > (muSliderMax - NZERO))
			{	mu = muSliderMax;
			}
		
			zetMuSlider();
		}

		// muDecimals aanpassen
		
		//muString = df3.format(mu);
		muString = UF.format0(mu,3);
		
		if (Math.abs(mu) < 2) 
		{	//muString = df3.format(mu);
			muString = UF.format0(mu,3);		
			muDecimals = 3;
		}	
		else if (Math.abs(mu) < 20)
		{	//muString = df2.format(mu);
			muString = UF.format0(mu,2);
			muDecimals = 2;
		}
		else if (Math.abs(mu) < 200)
		{	//muString = df1.format(mu);
			muString = UF.format0(mu,1);
			muDecimals = 1;
		}	
		else 
		{	//muString = df.format(mu);
			muString = UF.format0(mu,0);
			muDecimals = 0;	
		}	
		
//System.out.println("zetMu " + muString);		
		
		mu = round(mu, muDecimals);	
		//String muString = UF.format(mu, muDecimals);
		//String muString = df.format(mu);
		
//GWT?		
		//if (NormaleVerdeling.langArg.equals("nl"))
			muString = muString.replace('.', ',');
		
		muTextField.setText(muString);
		muWaardeLabel.setText(muString);

		//muMetWaardeLabel.setText(NormaleVerdeling.rb.getString("muTekst") + 
		//						   " = " + muString);
		muMetWaardeLabel.setText("\u03BC" + " = " + muString); // "\u03BC"

		// grenzen blijven op hun plaats, maar raken mogelijk buiten beeld!!

		if (((kansKeuze == KANSLINKS) || (kansKeuze == KANSRECHTS)) &&
			(berekenKeuze != BEREKENGRENS)) 
		{	zetGrens(grens, false);
			//zetGrens(grens, !bereken);
		}
		if (kansKeuze == TWEEGRENZEN)
		{	// nieuwe waarde groter, grenzen schuiven naar links
			if (deltaMu > NZERO)
			{	zetGrensLinks(grensLinks, false);
				zetGrensRechts(grensRechts, true);

			}
			// nieuwe waarde kleiner, grenzen schuiven naar rechts
			else if (deltaMu < - NZERO)
			{	zetGrensRechts(grensRechts, false);
				zetGrensLinks(grensLinks, true);

			}			
			else
			{	zetGrensLinks(grensLinks, false);
				zetGrensRechts(grensRechts, true);
			}
		}

		if (bereken)
			bereken();

		//paint();
	
	}

	

	
	public void zetMuSlider()
	{	
		if (muSlider == null)
			return;
		
		int sliderPos = (int) Math.round(
						    (mu - muSliderMin) / (muSliderMax - muSliderMin) * 
						    (muSlider.getMaximum() - muSlider.getMinimum()));
						    
		muSlider.zetStand(sliderPos);				    
		
		//paint();
	}	


	public void zetSigma(double waarde, boolean bereken, boolean resetSlider)
	{	
		sigma = waarde;
				
		if (sigma > sigmaMax - NZERO)
		{	sigma = sigmaMax;
			if (berekenKeuze == BEREKENSIGMA)
			{	berekenKeuze = BEREKENGRENS;
				bereken();
				berekenKeuze = BEREKENSIGMA;
			}	
		}
		if (sigma < sigmaMin + NZERO)
		{	sigma = sigmaMin;
			if (berekenKeuze == BEREKENSIGMA)
			{	berekenKeuze = BEREKENGRENS;
				bereken();
				berekenKeuze = BEREKENSIGMA;
			}	
		}

		if (resetSlider)
		{	
		
			minX = -45e-1d * sigma;
			maxX = 45e-1d * sigma;
			minY = 0;
			maxY = 6e-1d / sigma;
		
			minMuX = minX + mu;
			maxMuX = maxX + mu;		

			// zet nieuwe grenzen voor de muSlider
			muSliderMin = minMuX;
			muSliderMax = maxMuX;

			
			zetMuSlider();		

			// zet de grenzen voor de sigmaslider
			sigmaSliderMin = sigma - sigma / 2;
			sigmaSliderMax = sigma + sigma / 2;
			if (sigmaSliderMin < (sigmaMin + NZERO))
			{	sigmaSliderMin = sigmaMin;
			}
			if (sigmaSliderMax > (sigmaMax - NZERO))
			{	sigmaSliderMax = sigmaMax;
			}

			
			zetSigmaSlider();
			
		}

		else
		{		
			if (sigma < (sigmaSliderMin + NZERO))
			{	sigma = sigmaSliderMin;
			}
			else if (sigma > (sigmaSliderMax - NZERO))
			{	sigma = sigmaSliderMax;
			}
			
			zetSigmaSlider();
		}
				
		// sigmaDecimals aanpassen
		if (sigma < 10 - NZERO)
			sigmaDecimals = 2;
		else if (sigma < 100 - NZERO)
			sigmaDecimals = 1;	
		else
			sigmaDecimals = 0;		
		

		sigma = round(sigma, sigmaDecimals);	

		String sigmaString = UF.format(sigma, sigmaDecimals);
		
//GWT		
		//if (NormaleVerdeling.langArg.equals("nl"))
			sigmaString = sigmaString.replace('.', ',');
			
		sigmaTextField.setText(sigmaString);
		sigmaWaardeLabel.setText(sigmaString);

		// grenzen blijven niet noodzakelijk op hun plaats, 
		// en raken mogelijk buiten beeld!!

		if ((kansKeuze == KANSLINKS) || (kansKeuze == KANSRECHTS))
		{	zetGrens(grens, false);
		}

		if (kansKeuze == TWEEGRENZEN)
		{		
			zetGrensLinks(grensLinks, false);
			zetGrensRechts(grensRechts, true);

		}

		if (bereken)
			bereken();
		//	bereken(resetSlider);

		//paint();	
	}

	
	
	public void zetSigmaSlider()
	{	
		if (sigmaSlider == null)
			return;
		
		int sliderPos = (int) Math.round(
						    (sigma - sigmaSliderMin) / 
						    (sigmaSliderMax - sigmaSliderMin) * 
						    (sigmaSlider.getMaximum() - sigmaSlider.getMinimum()));
						    
		sigmaSlider.zetStand(sliderPos);				    
		
		//paint();
	}	
	
	
//	public void zetGrens(double waarde, boolean bereken, boolean start)
	public void zetGrens(double waarde, boolean bereken)
	{	
		grens = waarde;
		double grensMax = maxMuX;
		double grensMin = minMuX;
		
// hier if (berekenKeuze == BEREKENMU)
// etc;		
		if (berekenKeuze == BEREKENMU)
		{	
		
			grensMax = muMax + minX;// + maxX;
			grensMin = muMin + maxX;// + minX;
		}


//System.out.println("mimu = " + minMuX);
//System.out.println("mamu = " + maxMuX);
		
		if (grens > grensMax - NZERO)
		{	grens = grensMax;
		
			if (berekenKeuze == BEREKENGRENS)
			{
				berekenKeuze = BEREKENKANS;
				bereken();
				berekenKeuze = BEREKENGRENS;
			}
		}
		if (grens < grensMin + NZERO)
		{	grens = grensMin;
		
			if (berekenKeuze == BEREKENGRENS)
			{
				berekenKeuze = BEREKENKANS;
				bereken();
				berekenKeuze = BEREKENGRENS;
			}
		
		}

		
grensDecimals = findGrensDecimals();		
		
		
		grens = round(grens, grensDecimals);
					
		String grensString = UF.format(grens, grensDecimals);

//GWT		
		//if (NormaleVerdeling.langArg.equals("nl"))
			grensString = grensString.replace('.', ',');
	
		grensTextField.setText(grensString);
		grensWaardeLabel.setText(grensString);

		
		zetGrensSlider();

		if (bereken)
			bereken();

		//paint();		
	}
	
	public int findGrensDecimals()
	{	int result = 2;
		
		// aantal eenheden x-as per pixel
		double xUnitsPerPixel = (maxX - minX) / (xMax - xMin);
		
//System.out.println("xup = " + xUnitsPerPixel);		
		
		if (xUnitsPerPixel < 1e-4d + NZERO)
			result = 5;
		else if (xUnitsPerPixel < 1e-3d + NZERO)
			result = 4;
		else if (xUnitsPerPixel < 1e-2d + NZERO)
			result = 3;
		else if (xUnitsPerPixel < 1e-1d + NZERO)
			result = 2;
		else if (xUnitsPerPixel < 1 + NZERO)
			result = 1;
		else 
			result = 0;
		
	
		return result;
	}
	

	
	public void zetGrensSlider()
	{	
		if (grensSlider == null)
			return;
		
		
		//minMuX = minX + mu;
		int sliderPos = (int) Math.round(
						    (grens - minMuX) / (maxX - minX) * (xMax - xMin));
		grensSlider.zetStand(sliderPos);				    
		
		//paint();
	}

	public void zetGrensLinks(double waarde, boolean bereken)
	{	
	
		// kontrole op waarden	
		grensLinks = waarde;


		double minDis = 
			((double) tweeGrenzenSlider.pixDis) / (xMax - xMin) * (maxX - minX);

//System.out.println("minDis = " + minDis);
		
		if (grensLinks > grensRechts - minDis - NZERO)
		{	grensLinks = grensRechts - minDis - NZERO;
			
//System.out.println("dicht links gevonden");
							
			if (berekenKeuze == BEREKENGRENSLINKS)
			{	int oldBerekenKeuze = berekenKeuze;
				berekenKeuze = BEREKENKANS;
				bereken();
				berekenKeuze = oldBerekenKeuze;
				
				zetKansSlider();
			}
		}


		// grenslinks loopt links vast
		// fixeer grens rechts (kans gegeven)		
		if (grensLinks < minMuX + NZERO)
		{	grensLinks = minMuX;

//System.out.println("vast links gevonden");														

			if (grensRechts < (maxMuX - NZERO))
			{
			
			
						if (berekenKeuze == BEREKENGRENSLINKS)
						{	oldBerekenKeuze = berekenKeuze;
							berekenKeuze = BEREKENGRENSRECHTS;
							bereken();
							berekenKeuze = oldBerekenKeuze;
			
						}
						
			}			
		}

grensDecimals = findGrensDecimals();

		grensLinks = round(grensLinks, grensDecimals);
		
		String grensLinksString = UF.format(grensLinks, grensDecimals);
		
//GWT		
		//if (NormaleVerdeling.langArg.equals("nl"))
			grensLinksString = grensLinksString.replace('.', ',');
	
		grensLinksTextField.setText(grensLinksString);
		grensLinksWaardeLabel.setText(grensLinksString);

		
		zetGrensLinksSlider();

		if (bereken)
			bereken();
		
		//paint();			
		
	}


	
	public void zetGrensLinksSlider()
	{	
		if (tweeGrenzenSlider == null)
			return;
		
		//minMuX = minX + mu;
		int sliderPos = (int) Math.round(
						    (grensLinks - minMuX) / (maxX - minX) * (xMax - xMin));
		tweeGrenzenSlider.zetStandLinks(sliderPos);				    
		
		//paint();
	}


	public void zetGrensRechts(double waarde, boolean bereken)
	{	
	
		// kontrole op waarden
		grensRechts = waarde;
		
		
		if (grensRechts > maxMuX - NZERO)
		{	grensRechts = maxMuX;

//System.out.println("vast rechts gevonden");														
if (grensLinks > minMuX + NZERO)
{

			if (berekenKeuze == BEREKENGRENSRECHTS)
			{	oldBerekenKeuze = berekenKeuze;
				berekenKeuze = BEREKENGRENSLINKS;
				bereken();
				berekenKeuze = oldBerekenKeuze;
			}
}			
		}
	
		double minDis = 
			((double) tweeGrenzenSlider.pixDis) / (xMax - xMin) * (maxX - minX);
			
		if (grensRechts < (grensLinks + minDis + NZERO))
		{	grensRechts = grensLinks + minDis + NZERO;
		
//System.out.println("dicht rechts gevonden");																			

			if (berekenKeuze == BEREKENGRENSRECHTS)
			{	
//System.out.println("dicht rechts bij berekengrensrechts");																	
				int oldBerekenKeuze = berekenKeuze;
				berekenKeuze = BEREKENKANS;
				bereken();
				berekenKeuze = oldBerekenKeuze;
				
				zetKansSlider();
			}

		}
		
grensDecimals = findGrensDecimals();
		
		grensRechts = round(grensRechts, grensDecimals);				
		
		String grensRechtsString = UF.format(grensRechts, grensDecimals);
		
//GWT		
		//if (NormaleVerdeling.langArg.equals("nl"))
			grensRechtsString = grensRechtsString.replace('.', ',');
	
		grensRechtsTextField.setText(grensRechtsString);
		grensRechtsWaardeLabel.setText(grensRechtsString);
		
	
		zetGrensRechtsSlider();
		
		if (bereken)
			bereken();
		
		//paint();		
	}


	
	public void zetGrensRechtsSlider()
	{
		if (tweeGrenzenSlider == null)
			return;
		
		int sliderPos = (int) Math.round(
						    (grensRechts - minMuX) / (maxX - minX) * (xMax - xMin));
		tweeGrenzenSlider.zetStandRechts(sliderPos);				    
		
		//paint();
	}

	public void zetKans(double waarde, boolean bereken)
	{	
	
		double kansWaarde = waarde;
		
		if (kansWaarde > 1 - NZERO)
			kansWaarde = 1;
		if (kansWaarde < NZERO)
			kansWaarde = 0;	

		// kans wordt veranderd, mu wordt berekend
		// en mu komt over maximum heen:
		// fixeer kans
		if ((berekenKeuze == BEREKENMU) &&
			(mu > (muMax - NZERO)))
		{	int oldBerekenKeuze = berekenKeuze;
			berekenKeuze = BEREKENKANS;
			bereken();
			berekenKeuze = oldBerekenKeuze;
			if (kansWaarde < kans)
			{	
				
				zetKansSlider();
			}
			else
			{	kans = kansWaarde;
			}		
		}	
		// kans wordt veranderd, mu wordt berekend
		// en mu komt beneden minimum:
		// fixeer kans
		else if ((berekenKeuze == BEREKENMU) &&
			(mu < (muMin + NZERO)))
		{	int oldBerekenKeuze = berekenKeuze;
			berekenKeuze = BEREKENKANS;
			bereken();
			berekenKeuze = oldBerekenKeuze;
			if (kansWaarde > kans)
			{	
				
				zetKansSlider();
			}
			else
			{	kans = kansWaarde;
			}		
		}	

		// kans wordt veranderd, grensLinks wordt berekend
		// en grensLinks komt links buiten beeld, i.e.
		// wordt kleiner dan minMuX
		// fixeer kans
		else if ((berekenKeuze == BEREKENGRENSLINKS) &&
			(grensLinks < (minMuX + NZERO)) 
		   )
		{	
//System.out.println("links vast bij kans");
//System.out.println("kansWaarde = " + kansWaarde);
				// herbereken		
		
				int oldBerekenKeuze = berekenKeuze;
				berekenKeuze = BEREKENKANS;
				bereken();
				berekenKeuze = oldBerekenKeuze;

				if (kansWaarde > kans)
				{	
				
					zetKansSlider();
				}
				else
				{	kans = kansWaarde;
				}
			//}
		}
		// kans wordt veranderd, grensRechts wordt berekend
		// en grensRechts komt rechts buiten beeld, i.e.
		// wordt groter dan maxMuX
		// fixeer kans
		else if ((berekenKeuze == BEREKENGRENSRECHTS) &&
			(grensRechts > (maxMuX - NZERO)) //&&
		   )
		{	
//System.out.println("rechts vast bij kans");
//System.out.println("kansWaarde = " + kansWaarde);

			// herbereken		
		
			int oldBerekenKeuze = berekenKeuze;
			berekenKeuze = BEREKENKANS;
			bereken();
			berekenKeuze = oldBerekenKeuze;

			if (kansWaarde > kans)
			{	
			
				zetKansSlider();
			}
			else
			{	kans = kansWaarde;
			}

		}
		else	
			kans = kansWaarde;

		kans = round(kans, kansDecimals);
			
		String kansString = UF.format(kans, kansDecimals);

//GWT		
		//if (NormaleVerdeling.langArg.equals("nl"))
			kansString = kansString.replace('.', ',');
	
		kansTextField.setText(kansString);
		kansWaardeLabel.setText(kansString);

		zetKansSlider();	

		if (bereken)
			bereken();

		//paint();		
	}



	public void zetKansSlider()
	{	
		if (kansSlider == null)
			return;

		
		int sliderPos = (int) Math.round(
						    (kans - 0) / (1 - 0) * 
						    (kansSlider.getMaximum() - kansSlider.getMinimum()));
						    
		kansSlider.zetStand(sliderPos);				    
		
		//paint();
	}	
	
	public void zetKansKeuze(boolean init)
	{	
		if (init)
		{
			if (kansKeuze == KANSLINKS)
			{	linksButton.setValue(true);
			}
			else if (kansKeuze == KANSRECHTS)
			{	rechtsButton.setValue(true);
			}
			else // kansKeuze == TWEEGRENZEN
			{	tweeGrenzenButton.setValue(true);
			}
		}	
	

		zetLijsten();

//al weg in orig		
//		bereken();
		
		//paint();
		
	}
	
	public void resetParameters()
	{	
//GWT - styledependent		
		//muLabel.setForeground(Color.black);
		
		//muLabel.setOpaque(false);
		
		//muLabel.setVisible(true);
		setWidgetVisible(muLabel, true);
		//muTextField.setVisible(true);
		setWidgetVisible(muTextField, true);
		//muWaardeLabel.setVisible(false);
		setWidgetVisible(muWaardeLabel, false);
		
		//muSlider.setVisible(muSliderOptie);
		muSlider.zetEnabled(muSliderOptie);
		
//GWT - styledependent		
		//muWaardeLabel.setForeground(Color.black);
		
		//muWaardeLabel.setOpaque(false);

//GWT - styledependent		
		//sigmaLabel.setForeground(Color.black);
		
		//sigmaLabel.setOpaque(false);
		
		//sigmaLabel.setVisible(true);
		setWidgetVisible(sigmaLabel, true);
		//sigmaTextField.setVisible(true);
		setWidgetVisible(sigmaTextField, true);
		//sigmaWaardeLabel.setVisible(false);
		setWidgetVisible(sigmaWaardeLabel, false);
		
		
		//sigmaSlider.setVisible(sigmaSliderOptie);
		sigmaSlider.zetEnabled(sigmaSliderOptie);
		
//GWT - styledependent		
		//sigmaWaardeLabel.setForeground(Color.black);
		
		//sigmaWaardeLabel.setOpaque(false);

//GWT - styledependent		
		//grensLabel.setForeground(Color.black);
		
		//grensLabel.setOpaque(false);

		if (kansKeuze != TWEEGRENZEN)
		{	//grensLabel.setVisible(true);
			setWidgetVisible(grensLabel, true);
			//grensTextField.setVisible(true);
			setWidgetVisible(grensTextField, true);
			
			
			grensSlider.zetEnabled(grensSliderOptie);
			//grensSlider.setVisible(grensSliderOptie);
		}
		//grensWaardeLabel.setVisible(false);
		setWidgetVisible(grensWaardeLabel, false);
		
		//grensWaardeLabel.setOpaque(false);

//GWT - styledependent		
		//kansLabel.setForeground(Color.black);
		
		//kansLabel.setOpaque(false);
		
		//kansLabel.setVisible(true);
		setWidgetVisible(kansLabel, true);
		//kansTextField.setVisible(true);
		setWidgetVisible(kansTextField, true);
		//kansWaardeLabel.setVisible(false);
		setWidgetVisible(kansWaardeLabel, false);
		
		//kansWaardeLabel.setOpaque(false);

	
		//kansSlider.setVisible(true && kansSliderOptie);
		kansSlider.zetEnabled(true && kansSliderOptie);

//GWT - styledependent		
		//grensLinksLabel.setForeground(Color.black);
		
		//grensLinksLabel.setOpaque(false);

		if (kansKeuze == TWEEGRENZEN)
		{	//grensLinksLabel.setVisible(true);
			setWidgetVisible(grensLinksLabel, true);
			//grensLinksTextField.setVisible(true);
			setWidgetVisible(grensLinksTextField, true);
		}
		//grensLinksWaardeLabel.setVisible(false);
		setWidgetVisible(grensLinksWaardeLabel, false);
		
		//grensLinksWaardeLabel.setOpaque(false);

//GWT - styledependent		
		//grensRechtsLabel.setForeground(Color.black);
		
		//grensRechtsLabel.setOpaque(false);
		
		if (kansKeuze == TWEEGRENZEN)
		{	//grensRechtsLabel.setVisible(true);		
			setWidgetVisible(grensRechtsLabel, true);
			//grensRechtsTextField.setVisible(true);
			setWidgetVisible(grensRechtsTextField, true);
			
			//tweeGrenzenSlider.setVisible(grensSliderOptie);
			tweeGrenzenSlider.zetLinksEnabled(grensSliderOptie);
			tweeGrenzenSlider.zetRechtsEnabled(grensSliderOptie);
			
			//grensLabel.setVisible(false);
			setWidgetVisible(grensLabel, false);
			//grensTextField.setVisible(false);
			setWidgetVisible(grensTextField, false);
		}
		//grensRechtsWaardeLabel.setVisible(false);
		setWidgetVisible(grensRechtsWaardeLabel, false);
		
		//grensRechtsWaardeLabel.setOpaque(false);

		grensSlider.zetEnabled(true);
		tweeGrenzenSlider.zetLinksEnabled(true);
		tweeGrenzenSlider.zetRechtsEnabled(true);
		
//GWT??
/*		
		bgPanel1.setBackground(new Color(240,247,255));
		bgPanel2.setBackground(new Color(240,247,255));
		bgPanel3.setBackground(new Color(240,247,255));
		bgPanel4.setBackground(new Color(240,247,255));
		bgPanel5.setBackground(new Color(240,247,255));
		bgPanel6.setBackground(new Color(240,247,255));
		
		bgPanel1.setVisible(true);
		bgPanel2.setVisible(true);
		bgPanel3.setVisible(true);
		bgPanel4.setVisible(true);
		bgPanel5.setVisible(true);
		bgPanel6.setVisible(true);
*/		
		//muMetWaardeLabel.setVisible(muZichtbaarFigOptie);
		//setWidgetVisible(muMetWaardeLabel, muZichtbaarFigOptie);

	}
	
	public void zetBerekenKeuze(boolean init)
	{	
		if (!init)
			resetParameters();
	
		if (berekenKeuze == BEREKENMU)
		{	if (init)
				muButton.setValue(true);

//GWT - styledependent		
			//muLabel.setForeground(Color.blue);
		
			//muLabel.setOpaque(true);

//GWT - styledependent		
			//muLabel.setBackground(new Color(200,227,255));

	
			//muSlider.setVisible(false);
			muSlider.zetEnabled(false);
		
			//muTextField.setVisible(false);
			setWidgetVisible(muTextField, false);
			//muWaardeLabel.setVisible(true);
			setWidgetVisible(muWaardeLabel, true);
			
//GWT - styledependent			
			//muWaardeLabel.setForeground(Color.blue);
			
			//muWaardeLabel.setOpaque(true);
			
//GWT - styledependent			
			//muWaardeLabel.setBackground(new Color(200,227,255));
			
//GWT?			
			//bgPanel1.setBackground(new Color(200,227,255));
			
			
			grensSlider.zetEnabled(false);
			tweeGrenzenSlider.zetLinksEnabled(false);
			tweeGrenzenSlider.zetRechtsEnabled(false);
		
		}
		else if (berekenKeuze == BEREKENSIGMA)
		{	
			if (init)
				sigmaButton.setValue(true);

//GWT - styledependent		
			//sigmaLabel.setForeground(Color.blue);
				
			//sigmaLabel.setOpaque(true);
		
//GWT - styledependent			
			//sigmaLabel.setBackground(new Color(200,227,255));
		
		
			//sigmaSlider.setVisible(false);
			sigmaSlider.zetEnabled(false);
		
			//sigmaTextField.setVisible(false);
			setWidgetVisible(sigmaTextField, false);
			//sigmaWaardeLabel.setVisible(true);
			setWidgetVisible(sigmaWaardeLabel, true);
			
//GWT - styledependent			
			//sigmaWaardeLabel.setForeground(Color.blue);
			
			//sigmaWaardeLabel.setOpaque(true);
			
//GWT - styledependent			
			//sigmaWaardeLabel.setBackground(new Color(200,227,255));
			
//GWT??			
			//bgPanel2.setBackground(new Color(200,227,255));
		
		}
		else if (berekenKeuze == BEREKENGRENS)
		{	
			if (init)			
				grensButton.setValue(true);
			
//GWT - styledependent		
			//grensLabel.setForeground(Color.blue);
		
			//grensLabel.setOpaque(true);
		
//GWT - styledependent		
			//grensLabel.setBackground(new Color(200,227,255));
		
			//grensTextField.setVisible(false);
			setWidgetVisible(grensTextField, false);
			//grensWaardeLabel.setVisible(true);
			setWidgetVisible(grensWaardeLabel, true);
			
//GWT - styledependent			
			//grensWaardeLabel.setForeground(Color.blue);
			
			//grensWaardeLabel.setOpaque(true);
			
//GWT - styledependent			
			//grensWaardeLabel.setBackground(new Color(200,227,255));
			
//GWT??			
			//bgPanel3.setBackground(new Color(200,227,255));
			
			
			grensSlider.zetEnabled(false);
			
			//muTextField.setVisible(!muVastOptie);
			setWidgetVisible(muTextField, !muVastOptie);
			
			//muSlider.setVisible(!muVastOptie && muSliderOptie);
			muSlider.zetEnabled(!muVastOptie && muSliderOptie);
			
			//muWaardeLabel.setVisible(muVastOptie);
			setWidgetVisible(muWaardeLabel, muVastOptie);
			
		
			//sigmaSlider.setVisible(!sigmaVastOptie && sigmaSliderOptie);
			sigmaSlider.zetEnabled(!sigmaVastOptie && sigmaSliderOptie);
			
			//sigmaTextField.setVisible(!sigmaVastOptie);
			setWidgetVisible(sigmaTextField, !sigmaVastOptie);
			//sigmaWaardeLabel.setVisible(sigmaVastOptie);
			setWidgetVisible(sigmaWaardeLabel, sigmaVastOptie);
		
		}
		else if (berekenKeuze == BEREKENKANS)
		{	if (init)
				kansButton.setValue(true);
		
//GWT - styledependent		
			//kansLabel.setForeground(Color.blue);
		
			//kansLabel.setOpaque(true);
		
//GWT - styledependent		
			//kansLabel.setBackground(new Color(200,227,255));
		
			//kansTextField.setVisible(false);
			setWidgetVisible(kansTextField, false);
			//kansWaardeLabel.setVisible(true);
			setWidgetVisible(kansWaardeLabel, true);
			
//GWT - styledependent			
			//kansWaardeLabel.setForeground(Color.blue);
			
			//kansWaardeLabel.setOpaque(true);
			
//GWT - styledependent			
			//kansWaardeLabel.setBackground(new Color(200,227,255));
			
//GWT??			
			//bgPanel4.setBackground(new Color(200,227,255));
		
			
			//kansSlider.setVisible(false);
			kansSlider.zetEnabled(false);
			
			//muTextField.setVisible(!muVastOptie);
			setWidgetVisible(muTextField, !muVastOptie);

		
			//muSlider.setVisible(!muVastOptie && muSliderOptie);
			muSlider.zetEnabled(!muVastOptie && muSliderOptie);
			
			//muWaardeLabel.setVisible(muVastOptie);
			setWidgetVisible(muWaardeLabel, muVastOptie);
			
			//sigmaSlider.setVisible(!sigmaVastOptie && sigmaSliderOptie);
			sigmaSlider.zetEnabled(!sigmaVastOptie && sigmaSliderOptie);
			
			//sigmaTextField.setVisible(!sigmaVastOptie);
			setWidgetVisible(sigmaTextField, !sigmaVastOptie);
			//sigmaWaardeLabel.setVisible(sigmaVastOptie);
			setWidgetVisible(sigmaWaardeLabel, sigmaVastOptie);
		
		}
		else if (berekenKeuze == BEREKENGRENSLINKS)
		{	
			if (init)			
				grensLinksButton.setValue(true);
		
//GWT - styledependent		
			//grensLinksLabel.setForeground(Color.blue);
		
			//grensLinksLabel.setOpaque(true);
		
//GWT - styledependent		
			//grensLinksLabel.setBackground(new Color(200,227,255));
			
			//grensLinksTextField.setVisible(false);
			setWidgetVisible(grensLinksTextField, false);
			//grensLinksWaardeLabel.setVisible(true);
			setWidgetVisible(grensLinksWaardeLabel, true);
			
			
//GWT - styledependent			
			//grensLinksWaardeLabel.setForeground(Color.blue);
			
			//grensLinksWaardeLabel.setOpaque(true);
			
//GWT - styledependent			
			//grensLinksWaardeLabel.setBackground(new Color(200,227,255));
			
			
			tweeGrenzenSlider.zetLinksEnabled(false);
			
			//muTextField.setVisible(!muVastOptie);
			setWidgetVisible(muTextField, !muVastOptie);
			
			//muSlider.setVisible(!muVastOptie && muSliderOptie);
			muSlider.zetEnabled(!muVastOptie && muSliderOptie);
			
			//muWaardeLabel.setVisible(muVastOptie);
			setWidgetVisible(muWaardeLabel, muVastOptie);
			
			
			//sigmaSlider.setVisible(!sigmaVastOptie && sigmaSliderOptie);
			sigmaSlider.zetEnabled(!sigmaVastOptie && sigmaSliderOptie);
			
			//sigmaTextField.setVisible(!sigmaVastOptie);
			setWidgetVisible(sigmaTextField, !sigmaVastOptie);
			//sigmaWaardeLabel.setVisible(sigmaVastOptie);
			setWidgetVisible(sigmaWaardeLabel, sigmaVastOptie);
		
		}
		else if (berekenKeuze == BEREKENGRENSRECHTS)
		{	
			if (init)			
				grensRechtsButton.setValue(true);
		
//GWT - styledependent		
			//grensRechtsLabel.setForeground(Color.blue);
		
			//grensRechtsLabel.setOpaque(true);
		
//GWT - styledependent		
			//grensRechtsLabel.setBackground(new Color(200,227,255));
			//grensRechtsTextField.setVisible(false);
			setWidgetVisible(grensRechtsTextField, false);
			//grensRechtsWaardeLabel.setVisible(true);
			setWidgetVisible(grensRechtsWaardeLabel, true);

//GWT - styledependent			
			//grensRechtsWaardeLabel.setForeground(Color.blue);
			
			//grensRechtsWaardeLabel.setOpaque(true);
			
//GWT - styledependent			
			//grensRechtsWaardeLabel.setBackground(new Color(200,227,255));

			tweeGrenzenSlider.zetRechtsEnabled(false);	
			
			//muTextField.setVisible(!muVastOptie);
			setWidgetVisible(muTextField, !muVastOptie);
		
			//muSlider.setVisible(!muVastOptie && muSliderOptie);
			muSlider.zetEnabled(!muVastOptie && muSliderOptie);
			
			//muWaardeLabel.setVisible(muVastOptie);
			setWidgetVisible(muWaardeLabel, muVastOptie);
			
			//sigmaSlider.setVisible(!sigmaVastOptie && sigmaSliderOptie);
			sigmaSlider.zetEnabled(!sigmaVastOptie && sigmaSliderOptie);
			
			//sigmaTextField.setVisible(!sigmaVastOptie);
			setWidgetVisible(sigmaTextField, !sigmaVastOptie);
			//sigmaWaardeLabel.setVisible(sigmaVastOptie);
			setWidgetVisible(sigmaWaardeLabel, sigmaVastOptie);
			
		}
		
		plaatsComponenten(false);	
		
	}
	
	public void zetLijsten()
	{	
		if (kansKeuze == TWEEGRENZEN)
		{	// berekenlijst
		
			//oldBerekenMuOptie = actualBerekenMuOptie;
			actualMuBerekenbaarOptie = false;
			//muButton.setVisible(false);
			setWidgetVisible(muButton, false);
			
			//oldBerekenSigmaOptie = berekenSigmaOptie;
			actualSigmaBerekenbaarOptie = false;
			//sigmaButton.setVisible(false);
			setWidgetVisible(sigmaButton, false);
		
			if ((berekenKeuze == BEREKENMU)	||
				(berekenKeuze == BEREKENSIGMA))
			{	berekenKeuze = BEREKENKANS;
				bereken();
			}	
		
			//grensButton.setVisible(false);
			setWidgetVisible(grensButton, false);
			//grensLinksButton.setVisible(true);
			setWidgetVisible(grensLinksButton, true);
			//grensRechtsButton.setVisible(true);
			setWidgetVisible(grensRechtsButton, true);	
			
			// parameterlijst
			//grensLabel.setVisible(false);	
			setWidgetVisible(grensLabel, false);
			//grensTextField.setVisible(false);	
			setWidgetVisible(grensTextField, false);
			//grensWaardeLabel.setVisible(false);
			setWidgetVisible(grensWaardeLabel, false);
			
			//grensSlider.setVisible(false);
			grensSlider.zetEnabled(false);
			
			kansLabel.setText("Kans " + " = ");
						
			plaatsComponenten(false);			
			
			//grensLinksLabel.setVisible(true);
			setWidgetVisible(grensLinksLabel, true);
			//grensLinksTextField.setVisible(true);
			setWidgetVisible(grensLinksTextField, true);
			
			//grensRechtsLabel.setVisible(true);
			setWidgetVisible(grensRechtsLabel, true);
			//grensRechtsTextField.setVisible(true);
			setWidgetVisible(grensRechtsTextField, true);

			//tweeGrenzenSlider.setVisible(true);
			if (berekenKeuze != BEREKENGRENSLINKS)
				tweeGrenzenSlider.zetLinksEnabled(true);
			if (berekenKeuze != BEREKENGRENSRECHTS)
				tweeGrenzenSlider.zetRechtsEnabled(true);

			
//			repaint();
			
			//kansLabel.setText(NormaleVerdeling.rb.getString("kansTekst") + " = ");

/*
			if (grens > (mu + 1 - NZERO))
			{	zetGrensRechts(grens, false);
				zetGrensLinks(- grens, true);	
			}
			else
			{	zetGrensRechts(grens + 2, false);
				zetGrensLinks(grens, true);	
			}
*/			
			
			// waarden geven/overdragen
			//if (grensButton.isSelected())
			//	grensLinksButton.setSelected(true);	
			if (berekenKeuze == BEREKENGRENS)
			{	berekenKeuze = BEREKENGRENSLINKS;
				//zetBerekenKeuze();
			}
			
			zetBerekenKeuze(true);			


			if (grens > (mu + 1 - NZERO))
			{	zetGrensRechts(grens, false);
				zetGrensLinks(- grens, true);	
			}
			else
			{	zetGrensRechts(grens + 2, false);
				zetGrensLinks(grens, true);	
			}
			

			
		} 
		else // KANSLINKS of KANSRECHTS
		{	// berekenlijst
			//grensButton.setVisible(true);
			setWidgetVisible(grensButton, true);
			//grensLinksButton.setVisible(false);
			setWidgetVisible(grensLinksButton, false);
			//grensRechtsButton.setVisible(false);
			setWidgetVisible(grensRechtsButton, false);
				
			// parameterlijst
			//grensLabel.setVisible(true);
			setWidgetVisible(grensLabel, true);
			//grensTextField.setVisible(true);
			setWidgetVisible(grensTextField, true);
			
			//grensSlider.setVisible(true);
			grensSlider.zetEnabled(true);

			boolean wasTweeGrenzen = grensLinksLabel.isVisible();
			
			//grensLinksLabel.setVisible(false);
			setWidgetVisible(grensLinksLabel, false);
			//grensLinksTextField.setVisible(false);
			setWidgetVisible(grensTextField, false);
			//grensLinksWaardeLabel.setVisible(false);
			setWidgetVisible(grensLinksWaardeLabel, false);

			//grensRechtsLabel.setVisible(false);
			setWidgetVisible(grensRechtsLabel, false);
			//grensRechtsTextField.setVisible(false);
			setWidgetVisible(grensRechtsTextField, false);
			//grensRechtsWaardeLabel.setVisible(false);
			setWidgetVisible(grensRechtsWaardeLabel, false);

			//tweeGrenzenSlider.setVisible(false);		
			if (berekenKeuze != BEREKENGRENSLINKS)
				tweeGrenzenSlider.zetLinksEnabled(true);
			if (berekenKeuze != BEREKENGRENSRECHTS)
				tweeGrenzenSlider.zetRechtsEnabled(true);
			
			//paint();
			
			if (kansKeuze == KANSLINKS)
			{	kansLabel.setText("Kans links" + " = ");
				if (wasTweeGrenzen)
					zetGrens(grensRechts, true);
			}
			else // kansKeuze == KANSRECHTS
			{	kansLabel.setText("Kans rechts" + " = ");
				if (wasTweeGrenzen)
					zetGrens(grensLinks, true);
			}
			
			// waarden geven/ overdragen
			if ((berekenKeuze == BEREKENGRENSLINKS) || 
				(berekenKeuze == BEREKENGRENSRECHTS)) 
			{	berekenKeuze = BEREKENGRENS;
			//	zetBerekenKeuze();
			}
			
			actualMuBerekenbaarOptie = muBerekenbaarOptie && !muVastOptie;
			actualSigmaBerekenbaarOptie = sigmaBerekenbaarOptie && !sigmaVastOptie;
			//muButton.setVisible(muBerekenbaarOptie && berekenbaarZichtbaar);
			setWidgetVisible(muButton, muBerekenbaarOptie && berekenbaarZichtbaar);
			//sigmaButton.setVisible(sigmaBerekenbaarOptie && berekenbaarZichtbaar);
			setWidgetVisible(sigmaButton, sigmaBerekenbaarOptie && berekenbaarZichtbaar);
						
			zetBerekenKeuze(true);			
			plaatsComponenten(false);			
				
		}
		
//		plaatsComponenten();
	}


	public void zetKansOpties()
	{	// booleans zijn al gezet

		boolean override = false;
			
		// override kansKeuze als nodig		
		if (!kansLinksOptie && (kansKeuze == KANSLINKS))
		{	if (kansRechtsOptie)
				kansKeuze = KANSRECHTS;
			else
				kansKeuze = TWEEGRENZEN;	
				
			override = true;	
		}
		if (!kansRechtsOptie && (kansKeuze == KANSRECHTS))
		{	if (kansLinksOptie)
				kansKeuze = KANSLINKS;
			else
				kansKeuze = TWEEGRENZEN;
				
			override = true;			
		}
		if (!tweeGrenzenOptie && (kansKeuze == TWEEGRENZEN))
		{	if (kansLinksOptie)
				kansKeuze = KANSLINKS;
			else
				kansKeuze = KANSRECHTS;	
				
			override = true;		
		}
		
		// als maar 1 optie geselecteerd geen box
		if (
			(kansLinksOptie && !kansRechtsOptie && !tweeGrenzenOptie) ||
			(!kansLinksOptie && kansRechtsOptie && !tweeGrenzenOptie) ||
			(!kansLinksOptie && !kansRechtsOptie && tweeGrenzenOptie)
		   )	
		{	//linksButton.setVisible(false);
			setWidgetVisible(linksButton, false);
			//rechtsButton.setVisible(false);
			setWidgetVisible(rechtsButton, false);
			//tweeGrenzenButton.setVisible(false);
			setWidgetVisible(tweeGrenzenButton, false);
		}		
		else
		{	//linksButton.setVisible(kansLinksOptie);
			setWidgetVisible(linksButton, kansLinksOptie);
			//rechtsButton.setVisible(kansRechtsOptie);
			setWidgetVisible(rechtsButton, kansRechtsOptie);
			//tweeGrenzenButton.setVisible(tweeGrenzenOptie);
			setWidgetVisible(tweeGrenzenButton, tweeGrenzenOptie);
		}
		
		if (override)
			zetKansKeuze(false);
		
		plaatsComponenten(false);
		
	}
	
	public void zetberekenbaarZichtbaar(boolean b)
	{	berekenbaarZichtbaar = b;
		//berekenLabel.setVisible(b);
		setWidgetVisible(berekenLabel, b);
		
		//muButton.setVisible(b && muBerekenbaarOptie && !muVastOptie);
		setWidgetVisible(muButton, b && muBerekenbaarOptie && !muVastOptie);
		//sigmaButton.setVisible(b && sigmaBerekenbaarOptie && !sigmaVastOptie);
		setWidgetVisible(sigmaButton, b && sigmaBerekenbaarOptie && !sigmaVastOptie);
		
		//grensLinksButton.setVisible(b && kansKeuze == TWEEGRENZEN);
		setWidgetVisible(grensLinksButton, b && kansKeuze == TWEEGRENZEN);
		//grensRechtsButton.setVisible(b && kansKeuze == TWEEGRENZEN);
		setWidgetVisible(grensRechtsButton, b && kansKeuze == TWEEGRENZEN);
		//grensButton.setVisible(b && kansKeuze != TWEEGRENZEN);
		setWidgetVisible(grensButton, b && kansKeuze != TWEEGRENZEN);
		//kansButton.setVisible(b);
		setWidgetVisible(kansButton, b);
		
//GWT??		
		//bgPanel6.setVisible(b);
		
		plaatsComponenten(false);
	}
	
	public void zetMuBerekenbaarOptie(boolean b)
	{	muBerekenbaarOptie = b;
		if (!b)
			actualMuBerekenbaarOptie = false;
		else		
			actualMuBerekenbaarOptie = (kansKeuze != TWEEGRENZEN);
			
		//if (actualMuBerekenbaarOptie)	
		//muButton.setVisible(actualMuBerekenbaarOptie);
		setWidgetVisible(muButton, actualMuBerekenbaarOptie);

		// uitschakelen met berekenKeuze==BEREKENMU
		if (!b && (berekenKeuze == BEREKENMU))
		{	berekenKeuze = BEREKENKANS;
			zetBerekenKeuze(true);
		}
		
		// inschakelen met muVastOptie==true 
		if (b && muVastOptie)
		{	zetMuVastOptie(!b);
		}
		
		//zetBerekenKeuze();
		plaatsComponenten(false);
	}

	public void zetSigmaBerekenbaarOptie(boolean b)
	{	sigmaBerekenbaarOptie = b;
		if (!b)
			actualSigmaBerekenbaarOptie = false;
		else
			actualSigmaBerekenbaarOptie = (kansKeuze != TWEEGRENZEN);
			
		//if (actualSigmaBerekenbaarOptie)	
		//sigmaButton.setVisible(actualSigmaBerekenbaarOptie);
		setWidgetVisible(sigmaButton, actualSigmaBerekenbaarOptie);
			
		// uitschakelen met berekenKeuze==BEREKENSIGMA
		if (!b && (berekenKeuze == BEREKENSIGMA))
		{	berekenKeuze = BEREKENKANS;
			zetBerekenKeuze(true);
			
		}

		// inschakelen met sigamVastOptie==true 
		if (b && sigmaVastOptie)
		{	zetSigmaVastOptie(!b);
		}
		
		//zetBerekenKeuze();
		plaatsComponenten(false);		
	}
	
	
	public void zetMuVastOptie(boolean b)
	{	muVastOptie = b;
//		muWaardeLabel.setVisible(b);
//		muTextField.setVisible(!b);
//		muSlider.setVisible(!b);

		// inschakelen met muBerekenbaarOptie==true
		if (b && muBerekenbaarOptie)
		{	zetMuBerekenbaarOptie(!b);
		}

//GWT - styledependent		
		//muWaardeLabel.setForeground(Color.black);
		
		//muWaardeLabel.setVisible(b);
		setWidgetVisible(muWaardeLabel, b);
		//muTextField.setVisible(!b);
		setWidgetVisible(muTextField, !b);
		
		//muSlider.setVisible(!b && muSliderOptie);
		muSlider.zetEnabled(!b && muSliderOptie);

		
		plaatsComponenten(false);
	}

	public void zetSigmaVastOptie(boolean b)
	{	sigmaVastOptie = b;
//		sigmaWaardeLabel.setVisible(b);
//		sigmaTextField.setVisible(!b);
//		sigmaSlider.setVisible(!b);
		
		// inschakelen met sigmaBerekenbaarOptie==true		
		if (b && sigmaBerekenbaarOptie)
		{	zetSigmaBerekenbaarOptie(!b);
		}
//GWT - styledependent
		//sigmaWaardeLabel.setForeground(Color.black);
		
		//sigmaWaardeLabel.setVisible(b);
		setWidgetVisible(sigmaWaardeLabel, b);
		//sigmaTextField.setVisible(!b);
		setWidgetVisible(sigmaTextField, !b);
		
		//sigmaSlider.setVisible(!b && sigmaSliderOptie);
		sigmaSlider.zetEnabled(!b && sigmaSliderOptie);

		plaatsComponenten(false);		
	}

//GWT
/*	
	public void zetMuSliderOptie(boolean b)
	{	muSliderOptie = b;
		muSlider.setVisible(b && !muVastOptie && (berekenKeuze != BEREKENMU));
		plaatsComponenten();
	}
*/
//GWT	
/*	
	public void zetSigmaSliderOptie(boolean b)
	{	sigmaSliderOptie = b;
		sigmaSlider.setVisible(b && !sigmaVastOptie && (berekenKeuze != BEREKENSIGMA));
		plaatsComponenten();
	}
*/
//GWT	
/*	
	public void zetGrensSliderOptie(boolean b)
	{	grensSliderOptie = b;
	
		grensSlider.setVisible(b && (berekenKeuze != BEREKENGRENS) &&
			(kansKeuze != TWEEGRENZEN));
		
		tweeGrenzenSlider.setVisible(b && (berekenKeuze != BEREKENGRENSLINKS) &&
			(berekenKeuze != BEREKENGRENSRECHTS) && (kansKeuze == TWEEGRENZEN));
		plaatsComponenten();
	}
*/	
//GWT
/*	
	public void zetKansSliderOptie(boolean b)
	{	kansSliderOptie = b;
		kansSlider.setVisible(b && (berekenKeuze != BEREKENKANS));
		plaatsComponenten();
	}
*/	
	public void zetMuZichtbaarOptie(boolean b)
	{	muZichtbaarOptie = b;
		zetBerekenKeuze(false);
		plaatsComponenten(false);
	}
	
	public void zetSigmaZichtbaarOptie(boolean b)
	{	sigmaZichtbaarOptie = b;
		zetBerekenKeuze(false);
		plaatsComponenten(false);
	}

	
	public void zetGrensZichtbaarOptie(boolean b)
	{	grensZichtbaarOptie = b;
		zetBerekenKeuze(false);
		plaatsComponenten(false);
	}
	
	public void zetKansZichtbaarOptie(boolean b)
	{	kansZichtbaarOptie = b;
		zetBerekenKeuze(false);
		plaatsComponenten(false);
	}
	
	public void zetMuZichtbaarFigOptie(boolean b)
	{	muZichtbaarFigOptie = b;
		//muMetWaardeLabel.setVisible(b);
		setWidgetVisible(muMetWaardeLabel, b);
		paint();
	}
	
	public void zetSigmaZichtbaarFigOptie(boolean b)
	{	sigmaZichtbaarFigOptie = b;
		paint();
	}
	
	public void zetGrensZichtbaarFigOptie(boolean b)
	{	grensZichtbaarFigOptie = b;
		paint();
	}
	
	public void zetKansZichtbaarFigOptie(boolean b)
	{	kansZichtbaarFigOptie = b;
		paint();
	}


	
	public void processMuSlider()
	{	
		int stand = muSlider.geefStand();
		double muWaarde = muSliderMin + 
			((double) stand) / 
				(muSlider.getMaximum() - muSlider.getMinimum()) * 
				(muSliderMax - muSliderMin);
		zetMu(muWaarde, true, false);	
	}


	public void processSigmaSlider()
	{	
	
		int stand = sigmaSlider.geefStand();
		double sigmaWaarde = sigmaSliderMin + 
			((double) stand) / 
				(sigmaSlider.getMaximum() - sigmaSlider.getMinimum()) * 
				(sigmaSliderMax - sigmaSliderMin);
		zetSigma(sigmaWaarde, true, false);	
		
	}

	
	public void processGrensSlider()
	{	//minMuX = minX + mu;
		int stand = grensSlider.geefStand();
		double grensWaarde = minMuX + 
			((double) stand) / (xMax - xMin) * (maxX - minX);
		zetGrens(grensWaarde, true);	
	}


	
	public void processTweeGrenzenSlider(boolean links)
	{	//minMuX = minX + mu;
		if (links)
		{	int standLinks = tweeGrenzenSlider.geefStandLinks();
			double grensLinksWaarde = minMuX + 
				((double) standLinks) / (xMax - xMin) * (maxX - minX);
			zetGrensLinks(grensLinksWaarde, true);	
		
		}
		else
		{	int standRechts = tweeGrenzenSlider.geefStandRechts();
			double grensRechtsWaarde = minMuX + 
				((double) standRechts) / (xMax - xMin) * (maxX - minX);
			zetGrensRechts(grensRechtsWaarde, true);	
		}
	}


	
	public void processKansSlider()
	{	
		int stand = kansSlider.geefStand();
		double kansWaarde = 0 + 
			((double) stand) / 
				(kansSlider.getMaximum() - kansSlider.getMinimum()) * (1 - 0);
		zetKans(kansWaarde, true);	
	}


/*	
	public void fastPaint()
	{	if (ng == null)
			repaint();
		else
			paint(ng);	
	}
	*/
	
	public void paint()
	{
		
//System.out.println("paint");

		paintComponent(nvContext2d);
	}
	
	
	
	//public void paintComponent(Graphics g)
	public void paintComponent(Context2d og)
	{			
		//Graphics2D og = (Graphics2D) g;
		//og.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//og.setColor(Color.white);
		og.setFillStyle(CssColor.make(255,255,255));
		//og.fillRect(0, 0, getSize().width, getSize().height);
		og.fillRect(0, 0, breedte, hoogte);

		paintArea(og);
		paintMuLine(og);
		
		paintSigmaLines(og);
		
		paintXAxis(og);
		paintDistribution(og);
		paintLabels(og);	

		if (berekenbaarZichtbaar && berekenRect != null)
		{
			og.setFillStyle(veryLightBlue);
			og.fillRect(berekenRect.x, berekenRect.y, berekenRect.width, berekenRect.height);
		}
		if (muZichtbaarOptie && muRect != null)
		{
			if (berekenKeuze == BEREKENMU)
				og.setFillStyle(lightBlue);
			else
				og.setFillStyle(veryLightBlue);
			og.fillRect(muRect.x, muRect.y, muRect.width, muRect.height);
		}
		if (sigmaZichtbaarOptie && sigmaRect != null)
		{
			if (berekenKeuze == BEREKENSIGMA)
				og.setFillStyle(lightBlue);
			else
				og.setFillStyle(veryLightBlue);
			og.fillRect(sigmaRect.x, sigmaRect.y, sigmaRect.width, sigmaRect.height);
		}

		if (grensZichtbaarOptie)
		{
			if (kansKeuze == TWEEGRENZEN && grensLinksRect != null && grensRechtsRect != null)
			{
				if (berekenKeuze == BEREKENGRENSLINKS)
					og.setFillStyle(lightBlue);
				else
					og.setFillStyle(veryLightBlue);
				og.fillRect(grensLinksRect.x, grensLinksRect.y, grensLinksRect.width, grensLinksRect.height);
				
				if (berekenKeuze == BEREKENGRENSRECHTS)
					og.setFillStyle(lightBlue);
				else
					og.setFillStyle(veryLightBlue);
				og.fillRect(grensRechtsRect.x, grensRechtsRect.y, grensRechtsRect.width, grensRechtsRect.height);
				
			}
			else if (kansKeuze != TWEEGRENZEN && grensRect != null)
			{
				if (berekenKeuze == BEREKENGRENS)
					og.setFillStyle(lightBlue);
				else
					og.setFillStyle(veryLightBlue);
				og.fillRect(grensRect.x, grensRect.y, grensRect.width, grensRect.height);
				
			}
			
		}
		if (kansZichtbaarOptie && kansRect != null)
		{
			if (berekenKeuze == BEREKENKANS)
				og.setFillStyle(lightBlue);
			else
				og.setFillStyle(veryLightBlue);
			og.fillRect(kansRect.x, kansRect.y, kansRect.width, kansRect.height);
		}

		if (muSlider != null)
			muSlider.paint();
		if (sigmaSlider != null)
			sigmaSlider.paint();
		if (grensSlider != null)
			grensSlider.paint();
		if (kansSlider != null)
			kansSlider.paint();
		if (tweeGrenzenSlider != null)
			tweeGrenzenSlider.paint();


	}
	
	
	//public void paintXAxis(Graphics g)
	public void paintXAxis(Context2d g)
	{	//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		//g.drawLine(xMin, yMin, xMax, yMin);
		g.beginPath();
		g.moveTo(xMin, yMin);
		g.lineTo(xMax, yMin);
		g.stroke();
		
	}
	
	//public void paintMuLine(Graphics g)
	public void paintMuLine(Context2d g)
	{	// centreren

		int xPos = xMin + (int) Math.round(
								(mu - minMuX) / (maxX - minX) * (xMax - xMin));

		
		double fx = normalDF(mu);		

		int y = yMin - (int) Math.round(
								(fx - minY) / (maxY - minY) * (yMin - yMax));
		
		//g.setColor(muLineColor);
		g.setStrokeStyle(muLineColor);
		//g.drawLine(xPos, y, xPos, yMin);
		g.beginPath();
		g.moveTo(xPos, y);
		g.lineTo(xPos, yMin);
		g.stroke();
		
		
		//String muWaarde = "mu" + " = " + muString; // "\u03BC"
		String muWaarde = "\u03BC" + " = " + muString;

		TextMetrics tm = g.measureText(muWaarde);
		
		//int width = theFM.stringWidth(muWaarde);
		int width = (int) Math.round(tm.getWidth());
		
		int bx = xPos - width / 2;
		if (bx < 2)
			bx = 2;
			
		if ((bx + width) > (breedte - 2))
			bx = breedte - width - 2;	
		
//GWT gokje (2)		
		int by = yMin + 2 * 15; // theFM.getHeight();
		int by2 = yMin + 15; //theFM.getHeight();
		
		boolean lower = lowerGrensLabels || lowerGrensLinksLabels ||
						lowerGrensRechtsLabels;
		
		//g.setColor(Color.black);
		g.setFillStyle(CssColor.make(0,0,0));
		if (muZichtbaarFigOptie && lower)
		{	//g.drawString(muWaarde, bx, by2);
			g.fillText(muWaarde, bx, by2);
		}
		else if (muZichtbaarFigOptie && !lower)	
		{	//g.drawString(muWaarde, bx, by);
			g.fillText(muWaarde, bx, by);
		}
		
	}

	//public void paintSigmaLines(Graphics g)
	public void paintSigmaLines(Context2d  g)
	{	// left
	
		int xStartLeft = xMin + (int) Math.round(
							(mu - sigma - minMuX) / (maxX - minX) * (xMax - xMin));
		int xEndLeft = xMin + (int) Math.round(
						(mu - minMuX) / (maxX - minX) * (xMax - xMin)) - 1;
						
		int xStartRight = xEndLeft + 2;
		
		int xEndRight = xMin + (int) Math.round(
							(mu + sigma - minMuX) / (maxX - minX) * (xMax - xMin));
						
						
		double fx = normalDF(mu - sigma);				
		
		int y = yMin - (int) Math.round(
								(fx - minY) / (maxY - minY) * (yMin - yMax));

		//String sigmaWaarde = "sigma" + " = " + UF.format(sigma, sigmaDecimals); // "\u03C3"
		String sigmaWaarde = "\u03C3" + " = " + UF.format(sigma, sigmaDecimals);
		
		TextMetrics tm = g.measureText(sigmaWaarde);
		//int width = theFM.stringWidth(sigmaWaarde);
		int width = (int) Math.round(tm.getWidth());
		
		int bxLeft = (xStartLeft + xEndLeft) / 2 - width / 2;
		int bxRight = (xStartRight + xEndRight) / 2 - width / 2;
		
//GWT gokje		
		int by = y + 20; //theFM.getHeight();

		if (sigmaZichtbaarFigOptie)
		{	
			//g.setColor(sigmaLineColor);
			g.setStrokeStyle(sigmaLineColor);
			//g.drawLine(xStartLeft, y, xEndLeft, y);
			g.beginPath();
			g.moveTo(xStartLeft, y);
			g.lineTo(xEndLeft, y);
			g.stroke();
						
			//g.drawLine(xStartLeft, y, xStartLeft + 5, y - 5);
			g.beginPath();
			g.moveTo(xStartLeft, y);
			g.lineTo(xStartLeft + 5, y - 5);
			g.stroke();
						
			//g.drawLine(xStartLeft, y, xStartLeft + 5, y + 5);
			g.beginPath();
			g.moveTo(xStartLeft, y);
			g.lineTo(xStartLeft + 5, y + 5);
			g.stroke();
			
			//g.drawLine(xEndLeft, y, xEndLeft - 5, y - 5);
			g.beginPath();
			g.moveTo(xEndLeft, y);
			g.lineTo(xEndLeft - 5, y - 5);
			g.stroke();
						
			//g.drawLine(xEndLeft, y, xEndLeft - 5, y + 5);
			g.beginPath();
			g.moveTo(xEndLeft, y);
			g.lineTo(xEndLeft - 5, y + 5);
			g.stroke();
						
			//g.drawLine(xStartRight, y, xEndRight, y);
			g.beginPath();
			g.moveTo(xStartRight, y);
			g.lineTo(xEndRight, y);
			g.stroke();

			//g.drawLine(xStartRight, y, xStartRight + 5, y - 5);
			g.beginPath();
			g.moveTo(xStartRight, y);
			g.lineTo(xStartRight + 5, y - 5);
			g.stroke();

			//g.drawLine(xStartRight, y, xStartRight + 5, y + 5);
			g.beginPath();
			g.moveTo(xStartRight, y);
			g.lineTo(xStartRight + 5, y + 5);
			g.stroke();

			//g.drawLine(xEndRight, y, xEndRight - 5, y - 5);
			g.beginPath();
			g.moveTo(xEndRight, y);
			g.lineTo(xEndRight - 5, y - 5);
			g.stroke();

			//g.drawLine(xEndRight, y, xEndRight - 5, y + 5);
			g.beginPath();
			g.moveTo(xEndRight, y);
			g.lineTo(xEndRight - 5, y + 5);
			g.stroke();


			//g.setColor(Color.black);	
			g.setFillStyle(CssColor.make(0,0,0));
			if ((bxRight + width) < breedte)		
			{	//g.drawString(sigmaWaarde, bxRight, by);
				g.fillText(sigmaWaarde, bxRight, by);
			}
			else
			{	//g.drawString(sigmaWaarde, bxLeft, by);
				g.fillText(sigmaWaarde, bxLeft, by);
			}
		}

		
	}

	//public void paintDistribution(Graphics g)
	public void paintDistribution(Context2d g)
	{	// centreren
	
// stroke zetten
	
		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		for (int xCnt = xMin; xCnt < xMax; xCnt++)
		{	// maak van xCnt en xCnt+1 de corresponderende double
			double x1 = minMuX + ((double) (xCnt - xMin)) / (xMax - xMin) * (maxX - minX);
			double x2 = minMuX + ((double) (xCnt + 1 - xMin)) / (xMax - xMin) * (maxX - minX);
			// bereken funktiewaarden
			double fx1 = normalDF(x1);
			double fx2 = normalDF(x2);
			// maak van fx1 en fx2 de correnponderende ints
			int y1 = yMin - (int) Math.round(
									(fx1 - minY) / (maxY - minY) * (yMin - yMax));
			int y2 = yMin - (int) Math.round(
									(fx2 - minY) / (maxY - minY) * (yMin - yMax));
			//g.drawLine(xCnt, y1, xCnt + 1, y2);
			g.beginPath();
			g.moveTo(xCnt, y1);
			g.lineTo(xCnt + 1, y2);
			g.stroke();
		}
	
	}

	//public void paintArea(Graphics g)
	public void paintArea(Context2d g)
	{	// centreren
		
		int xStart = 0;
		int xStop = 0;
		
		if (kansKeuze == KANSLINKS)
		{	xStart = xMin;
			xStop = xMin + (int) Math.round(
								  (grens - minMuX) / (maxX - minX) * (xMax - xMin));
		}
		else if (kansKeuze == KANSRECHTS)
		{	xStart = xMin + (int) Math.round(
								  (grens - minMuX) / (maxX - minX) * (xMax - xMin));
			xStop = xMax;					  
		}
		else // kansKeuze == TWEEGRENZEN
		{	xStart = xMin + (int) Math.round(
								  (grensLinks - minMuX) / (maxX - minX) * (xMax - xMin));
			xStop = xMin + (int) Math.round(
								  (grensRechts - minMuX) / (maxX - minX) * (xMax - xMin));					  
		}

		//g.setColor(lightBlue);
		for (int xCnt = xStart; xCnt <= xStop; xCnt++)
		{	// maak van xCnt een double
			double x = minMuX + ((double) (xCnt - xMin)) / (xMax - xMin) * (maxX - minX);
			// bereken funktiewaarde
			double fx = normalDF(x);
			// maak van fx de correnponderende int
			int y = yMin - (int) Math.round(
									(fx - minY) / (maxY - minY) * (yMin - yMax));
			
			if ((kansKeuze == KANSLINKS) && (xCnt == xStop))
			{	//g.setColor(Color.black);
				g.setStrokeStyle(CssColor.make(0,0,0));
			}
			else if ((kansKeuze == KANSRECHTS) && (xCnt == xStart))
			{	//g.setColor(Color.black);
				g.setStrokeStyle(CssColor.make(0,0,0));
			}
			else if ((kansKeuze == TWEEGRENZEN) && ((xCnt == xStart) || (xCnt == xStop)))
			{	//g.setColor(Color.black);
				g.setStrokeStyle(CssColor.make(0,0,0));
			}
			else
			{	//g.setColor(areaColor);
				g.setStrokeStyle(areaColor);
			}
			
			//g.drawLine(xCnt, y, xCnt, yMin);
			g.beginPath();
			g.moveTo(xCnt, y);
			g.lineTo(xCnt, yMin);
			g.stroke();
		}

		
	}

	//public void paintLabels(Graphics g)
	public void paintLabels(Context2d g)
	{	// centreren
	
		int hOffset = 5;		
	
		if (kansKeuze == KANSLINKS)
		{	String grensWaarde = UF.format(grens, grensDecimals);
		
			TextMetrics tm = g.measureText(grensWaarde);
		
			//int width = theFM.stringWidth(grensWaarde);
			int width = (int) Math.round(tm.getWidth());
			
			// in pixels
			int grensPos = xMin + (int) Math.round(
								  	  (grens - minMuX) / (maxX - minX) * (xMax - xMin));
								  	  
			int grensWaardePos = grensPos - width / 2;					  	  
			if (grensWaardePos < 0)
				grensWaardePos = 0;
			if (grensWaardePos + width > breedte)
				grensWaardePos -= grensWaardePos + width - breedte;	

			int vSpace = 0;
			if (lowerGrensLabels)
			{	
//GWT gokje				
				vSpace = 2 * 15; //theFM.getHeight();
			}
			else
			{	
//GWT gokje				
				vSpace = 15; //theFM.getHeight();
			
			}
				
			//g.setFont(theFont);
			g.setFont(fontString);
			//g.setColor(Color.black);
			g.setFillStyle(CssColor.make(0,0,0));
			if (grensZichtbaarFigOptie)
			{	//g.drawString(grensWaarde, grensWaardePos, yMin + vSpace);
				g.fillText(grensWaarde, grensWaardePos, yMin + vSpace);
			}
			
			String gString = "G";
			tm = g.measureText(gString);
			
			//width = theFM.stringWidth(gString);
			width = (int) Math.round(tm.getWidth());
			
			int gPos = grensPos + hOffset;
			if (gPos + width > breedte)
				gPos -= gPos + width - breedte;	
			if (grensZichtbaarFigOptie)
			{	//g.drawString(gString, gPos, yMin - theFM.getDescent());
//GWT gokje
				g.fillText(gString, gPos, yMin - 6);
			}
			
			String kansWaarde = UF.format(kans, kansDecimals);
			
			tm = g.measureText(kansWaarde);
			
			//width = theFM.stringWidth(kansWaarde);
			width = (int) Math.round(tm.getWidth());
			
			int kansPos = grensPos - width - 2 * hOffset;
			if (kansPos < 0)
				kansPos = 0;
			
			//g.setColor(kansColor);
			g.setFillStyle(kansColor);
			if (kansZichtbaarFigOptie)
			{	//g.drawString(kansWaarde, kansPos, yMin - theFM.getHeight());
//GWT gokje		
				g.fillText(kansWaarde, kansPos, yMin - 20);
			}
			
			
		}
		else if (kansKeuze == KANSRECHTS)
		{	String grensWaarde = UF.format(grens, grensDecimals);
		
			TextMetrics tm = g.measureText(grensWaarde);
			
			//int width = theFM.stringWidth(grensWaarde);
			int width = (int) Math.round(tm.getWidth());
			
			// in pixels
			int grensPos = xMin + (int) Math.round(
								  	  (grens - minMuX) / (maxX - minX) * (xMax - xMin));

			int grensWaardePos = grensPos - width / 2;					  	  
			if (grensWaardePos < 0)
				grensWaardePos = 0;
			if (grensWaardePos + width > breedte)
				grensWaardePos -= grensWaardePos + width - breedte;	

			int vSpace = 0;
			if (lowerGrensLabels)
			{	
//GWT gokje				
				vSpace = 2 * 15; //theFM.getHeight();
			}
			else
			{
//GWT gokje
				vSpace = 15; //theFM.getHeight();	
			}
			
			//g.setFont(theFont);
			g.setFont(fontString);
			//g.setColor(Color.black);
			g.setFillStyle(CssColor.make(0,0,0));
			
			if (grensZichtbaarFigOptie)
			{	//g.drawString(grensWaarde, grensWaardePos, yMin + vSpace);
				g.fillText(grensWaarde, grensWaardePos, yMin + vSpace);
			}
			
			String gString = "G";
			tm = g.measureText(gString);
			
			//width = theFM.stringWidth(gString);
			width = (int) Math.round(tm.getWidth());
			
			int gPos = grensPos - hOffset - width;
			if (gPos < 0)
				gPos = 0;
			if (grensZichtbaarFigOptie)
			{	//g.drawString(gString, gPos, yMin - theFM.getDescent());
//GWT gokje
				g.fillText(gString, gPos, yMin - 6);
			}
			
			String kansWaarde = UF.format(kans, kansDecimals);
			tm = g.measureText(kansWaarde);
			
			//width = theFM.stringWidth(kansWaarde);
			width = (int) Math.round(tm.getWidth());
			
			int kansPos = grensPos + 2 * hOffset;
			if (kansPos + width > breedte)
				kansPos -= kansPos + width - breedte;
			
			//g.setColor(kansColor);
			g.setFillStyle(kansColor);
			if (kansZichtbaarFigOptie)
			{	//g.drawString(kansWaarde, kansPos, yMin - theFM.getHeight());
//GWT gokje			
				g.fillText(kansWaarde, kansPos, yMin - 20);
			}
		}
		else // kansKeuze == TWEEGRENZEN
		{	String grensLinksWaarde = UF.format(grensLinks, grensDecimals);
			String grensRechtsWaarde = UF.format(grensRechts, grensDecimals);
			
			TextMetrics tmLinks = g.measureText(grensLinksWaarde);
			TextMetrics tmRechts = g.measureText(grensRechtsWaarde);
			
			//int widthLinks = theFM.stringWidth(grensLinksWaarde);
			int widthLinks = (int) Math.round(tmLinks.getWidth());
			//int widthRechts = theFM.stringWidth(grensRechtsWaarde);
			int widthRechts = (int) Math.round(tmRechts.getWidth());
			// in pixels
			int grensLinksPos = xMin + 
				(int) Math.round((grensLinks - minMuX) / (maxX - minX) * (xMax - xMin));
			int grensRechtsPos = xMin + 
				(int) Math.round((grensRechts - minMuX) / (maxX - minX) * (xMax - xMin));

			int grensLinksWaardePos = grensLinksPos - widthLinks / 2;					  	  
			if (grensLinksWaardePos < 0)
				grensLinksWaardePos = 0;
			if (grensLinksWaardePos + widthLinks > breedte)
				grensLinksWaardePos -= grensLinksWaardePos + widthLinks - breedte;	
			
			int grensRechtsWaardePos = grensRechtsPos - widthRechts / 2;					  	  
			if (grensRechtsWaardePos < 0)
				grensRechtsWaardePos = 0;
			if (grensRechtsWaardePos + widthRechts > breedte)
				grensRechtsWaardePos -= grensRechtsWaardePos + widthRechts - breedte;	
			
			if ((grensLinksWaardePos + widthLinks > grensRechtsWaardePos) &&
				!lowerGrensLinksLabels && !lowerGrensRechtsLabels)
			{	
				grensLinksWaardePos = grensLinksPos - widthLinks;	
				grensRechtsWaardePos = grensRechtsPos;
				
				if (grensLinksWaardePos < 0)
				{	
					grensLinksWaardePos = 0;
					grensRechtsWaardePos = grensLinksWaardePos + widthLinks;
										
				}
				
				if (grensRechtsWaardePos + widthRechts > breedte)
				{	
					grensRechtsWaardePos -= grensRechtsWaardePos + widthRechts - breedte;	 	
					grensLinksWaardePos = grensRechtsWaardePos - widthRechts;
					
				}
			}	
				

			int vLinksSpace = 0;
			if (lowerGrensLinksLabels)
			{
//GWT gokje				
				vLinksSpace = 2 * 15; //theFM.getHeight();
			}
			else
			{
//GWT gokje				
				vLinksSpace = 15; //theFM.getHeight();
			}
				
			int vRechtsSpace = 0;
			if (lowerGrensRechtsLabels)
			{
//GWT gokje				
				vRechtsSpace = 2 * 15; //theFM.getHeight();
			}
			else
			{	
//GWT gokje				
				vRechtsSpace = 15; //theFM.getHeight();
			
			}
				
			//g.setFont(theFont);
			g.setFont(fontString);
			//g.setColor(Color.black);
			g.setFillStyle(CssColor.make(0,0,0));
			
			if (grensZichtbaarFigOptie)
			{	//g.drawString(grensLinksWaarde, grensLinksWaardePos, yMin + vLinksSpace);
				g.fillText(grensLinksWaarde, grensLinksWaardePos, yMin + vLinksSpace);
			}
			if (grensZichtbaarFigOptie)
			{	//g.drawString(grensRechtsWaarde, grensRechtsWaardePos,  yMin + vRechtsSpace);
				g.fillText(grensRechtsWaarde, grensRechtsWaardePos,  yMin + vRechtsSpace);
			}
			
			String lString = "L";			 
			String rString = "R";			 
			
			tmLinks = g.measureText(lString);
			tmRechts = g.measureText(rString);
			
			//int lWidth = theFM.stringWidth(lString);
			//int rWidth = theFM.stringWidth(rString);
			int lWidth = (int) Math.round(tmLinks.getWidth());
			int rWidth = (int) Math.round(tmRechts.getWidth());
			
			
			int lPos = grensLinksPos - hOffset - lWidth;
			if (lPos < 0)
				lPos = 0;
				
			int rPos = grensRechtsPos + hOffset;	
			if (rPos + rWidth > breedte)
				rPos -= rPos + rWidth - breedte;
			
			if (grensZichtbaarFigOptie)
			{	//g.drawString(lString, lPos, yMin - theFM.getDescent());
//GWT gokje 			
				g.fillText(lString, lPos, yMin - 6);
			}
			if (grensZichtbaarFigOptie)
			{	//g.drawString(rString, rPos, yMin - theFM.getDescent());
//GWT gokje			
				g.fillText(rString, rPos, yMin - 6);
			}
			
			String kansWaarde = UF.format(kans, kansDecimals);
			
			TextMetrics tm = g.measureText(kansWaarde);
			//int width = theFM.stringWidth(kansWaarde);
			int width = (int) Math.round(tmLinks.getWidth());
			
			int kansPos = (grensLinksPos + grensRechtsPos) / 2 - width / 2;
			if (kansPos < 0)
				kansPos = 0;
			if (kansPos + width > breedte)
				kansPos -= kansPos + width - breedte;	
			
			//g.setColor(kansColor);
			g.setFillStyle(kansColor);
			if (kansZichtbaarFigOptie)
			{	//g.drawString(kansWaarde, kansPos, yMin - theFM.getHeight());
//GWT gokje		
				g.fillText(kansWaarde, kansPos, yMin - 20);
			}
		}
	}
	
	// rekenen
/*	
	public void bereken()
	{
		bereken(false);
	}
*/
//	public void bereken(boolean sigmaInput)
	public void bereken()
	{	
		if (berekenKeuze == BEREKENKANS)
		{	if (kansKeuze == KANSLINKS)
			{	zetKans(phi((grens - mu) / sigma), false);
			}
			else if (kansKeuze == KANSRECHTS)
			{	zetKans(1 - phi((grens - mu) / sigma), false);
			}
			else // kansKeuze == TWEEGRENZEN
			{	zetKans(phi((grensRechts - mu) / sigma) -
						phi((grensLinks - mu) / sigma), false);
			}
			
		}
		
		if (berekenKeuze == BEREKENGRENS)
		{	if (kansKeuze == KANSLINKS)
			{	// P[X<grens]=kans heeft als oplossing
				// grens=mu+sigma*phiInv(kans)
				zetGrens(mu + sigma * phiInv(kans), false);
			}
			else if (kansKeuze == KANSRECHTS)
			{	// P[X>grens]=kans heeft als oplossing
				// grens=mu+sigma*phiInv(1-kans)
				zetGrens(mu + sigma * phiInv(1 - kans), false);
			}
		}
		
		if (berekenKeuze == BEREKENGRENSLINKS)
		{	// kansKeuze == TWEEGRENZEN
			// P[grensLinks<X<grensRechts]=kans heeft als oplossing
			// voor grensLinks:
			// schrijf kans=P[grensLinks<X<grensRechts]=
			// 1-P[grensLinks<X]-P[X>grensRechts]	
			// dan is P[grensLinks<X]=1-P[X>grensRechts]-kans
			// met oplossing grensLinks=mu+sigma*phiInv(1-P[X>grensRechts]-kans)
			// waar P[X>grensRechts]=1-phi((grensRechts-mu)/sigma) 
			double kansWaarde = 1 - (1 - phi((grensRechts - mu) / sigma)) - kans; 
			if (kansWaarde < NZERO)
				kansWaarde = 0;
			
			zetGrensLinks(mu + sigma * phiInv(kansWaarde), false);
		}
		
		if (berekenKeuze == BEREKENGRENSRECHTS)
		{	// kansKeuze == TWEEGRENZEN
			// P[grensLinks<X<grensRechts]=kans heeft als oplossing
			// voor grensRechts:
			// schrijf kans=P[grensLinks<X<grensRechts]=
			// 1-P[grensLinks<X]-P[X>grensRechts]	
			// dan is P[grensRechst>X]=1-P[grensLinks<X]-kans
			// met oplossing grensRechts=mu+sigma*phiInv(1-(1-P[grensLinks<X]-kans))
			// waar P[grensLinks<X]=phi((grensLinks-mu)/sigma) 
			double kansWaarde = 1 - (1 - phi((grensLinks - mu) / sigma) - kans);
			if (kansWaarde > 1 - NZERO)
				kansWaarde = 1;
			
			zetGrensRechts(mu + sigma * phiInv(kansWaarde),	false);
		
		}
		
		if (berekenKeuze == BEREKENSIGMA)
		{	if (kansKeuze == KANSLINKS)
			{	// P[X<grens]=kans heeft als oplossing
				// grens=mu+sigma*phiInv(kans)
				// dus sigma=(grens-mu)/phiInv(kans)
				zetSigma((grens - mu) / phiInv(kans), false, true);
			}
			else if (kansKeuze == KANSRECHTS)
			{	// P[X>grens]=kans heeft als oplossing
				// grens=mu+sigma*phiInv(1-kans)
				// dus sigma=(grens-mu)/phiInv(1-kans)
				zetSigma((grens - mu) / phiInv(1-kans), false, true);
			}
			else // kansKeuze == TWEEGRENZEN
			{	// kans=P[grensLinks<X<grensRechts]=
				// phi((grensRechts - mu) / sigma) -
				// phi((grensLinks - mu) / sigma)

// dit werkt niet goed !!
				
				double sigmaStart = sigmaMin;
				double sigmaStep = 1e-1d;
				int steps = (int) Math.round((sigmaMax - sigmaMin) / sigmaStep);
				double kansWaarde = 
					phi((grensRechts - mu) / sigmaStart) -
					phi((grensLinks - mu) / sigmaStart);
				double sigmaSought = sigmaStart;	
					
				for (int sCnt = 1; sCnt <= steps; sCnt++)
				{	sigmaStart += sigmaStep;
					double waarde =
						phi((grensRechts - mu) / sigmaStart) -
						phi((grensLinks - mu) / sigmaStart);
					if (Math.abs(waarde - kans) < Math.abs(kansWaarde - kans))
					{	kansWaarde = waarde;
						sigmaSought = sigmaStart;
					}
				}	

				// hier nog een keer verfijnen	
				sigmaStart = sigmaSought - 2e-1d;
				sigmaStep = 2e-1d;
				double sigmaStop = sigmaSought + 2e-1d;
				steps = (int) Math.round((sigmaStop - sigmaStart) / sigmaStep);
				
				kansWaarde = 
					phi((grensRechts - mu) / sigmaStart) -
					phi((grensLinks - mu) / sigmaStart);
				sigmaSought = sigmaStart;	
				
				for (int sCnt = 1; sCnt <= steps; sCnt++)
				{	sigmaStart += sigmaStep;
					double waarde =
						phi((grensRechts - mu) / sigmaStart) -
						phi((grensLinks - mu) / sigmaStart);
					if (Math.abs(waarde - kans) < Math.abs(kansWaarde - kans))
					{	kansWaarde = waarde;
						sigmaSought = sigmaStart;
					}
				}	
				
				if (sigmaSought < sigmaMin + NZERO)
					sigmaSought = sigmaMin;
					
				if (sigmaSought > sigmaMax - NZERO)
					sigmaSought = sigmaMax;
								
//System.out.println("ss = " + sigmaSought);			
			
				zetSigma(sigmaSought, false, true);
			
			}
		}
		
		if (berekenKeuze == BEREKENMU)
		{	if (kansKeuze == KANSLINKS)
			{	// P[X<grens]=kans heeft als oplossing
				// grens=mu+sigma*phiInv(kans)
				// dus mu=grens-sigma*phiInv(kans)
				zetMu(grens - sigma * phiInv(kans), false, true);
			}
			else if (kansKeuze == KANSRECHTS)
			{	// P[X>grens]=kans heeft als oplossing
				// grens=mu+sigma*phiInv(1-kans)
				// dus mu=grens-sigma*phiInv(1-kans)
				zetMu(grens - sigma * phiInv(1-kans), false, true);
			}
			else // kansKeuze == TWEEGRENZEN
			{	// kans=P[grensLinks<X<grensRechts]=
				// phi((grensRechts - mu) / sigma) -
				// phi((grensLinks - mu) / sigma)
				
// dit werkt niet goed !!				
				
				double muStart = mu - 4 * sigma;
				double muEnd = mu + 4 * sigma;
				double muStep = 1e-1d;
				int steps = (int) Math.round((muEnd - muStart) / muStep);
				double kansWaarde = 
					phi((grensRechts - muStart) / sigma) -
					phi((grensLinks - muStart) / sigma);
				double muSought = muStart;	
				
				for (int sCnt = 1; sCnt <= steps; sCnt++)
				{	muStart += muStep;
					double waarde =
						phi((grensRechts - muStart) / sigma) -
						phi((grensLinks - muStart) / sigma);
					if (Math.abs(waarde - kans) < Math.abs(kansWaarde - kans))
					{	kansWaarde = waarde;
						muSought = muStart;
					}
				}	
				
				
				zetMu(muSought, false, true);				
			}
		}
		
		paint();
	}

	// density function normale verdeling
	public double normalDF(double x)
	{	double fx = Math.pow(Math.E, 
							 - (x - mu) * (x - mu) / (sigma * sigma * 2)) /
					(sigma * Math.sqrt(2 * Math.PI));		 
		return fx;			
	}

	// distribution function voor standaard normale verdeling
	public double phi(double z)
	{	if (Math.abs(z) < NZERO)
			return 5e-1d;
		else 
			return (1 + erf(z / Math.sqrt(2))) / 2;
	}

	// een benadering voor de error function
	public double erf(double x)
	{	
		double erfx = StatUtil.erf(x);

		return erfx;
	}
	
	// inverse distribution function voor standaard normale verdeling
	public double phiInv(double p)
	{	
		double phiInvp = StatUtil.getInvCDF(p, true);
	
		return phiInvp;
	
	}
/*	
	// een benadering voor de inverse van de error function
	public double erfInv(double x)
	{	double a = 8 * (Math.PI - 3) / (3 * Math.PI * (4 - Math.PI));
	
		double signx = 1;
		if (x < -NZERO)
			signx = -1;
		
		double temp = 2 / (Math.PI * a) + Math.log(1 - x * x) / 2;	
			
		double erfInvx = signx *
			Math.sqrt(
				Math.sqrt(temp * temp - Math.log(1 - x * x) / a) - temp);
		
		return erfInvx;			  
					
	}
*/	
	// inner classes
	
	class KansKeuzeVCH implements ValueChangeHandler<Boolean>
	{	//public void actionPerformed(ActionEvent e)
		public void onValueChange(ValueChangeEvent<Boolean> e)
		{	
			if (e.getSource() == linksButton)
			{	kansKeuze = KANSLINKS;
			}
			else if (e.getSource() == rechtsButton)
			{	kansKeuze = KANSRECHTS;
			}
			else if (e.getSource() == tweeGrenzenButton)	
			{	kansKeuze = TWEEGRENZEN;
			}

			zetKansKeuze(false);	
		
			bereken();	
			
			//paint();
		}
	}
	
// do not use with RadioButtons (Google)	
/*	
	//class KansKeuzeAL implements ActionListener
	class KansKeuzeCH implements ClickHandler
	{	//public void actionPerformed(ActionEvent e)
		public void onClick(ClickEvent e)
		{	
			//e.preventDefault();
			e.stopPropagation();
			if ((e.getSource() == linksButton))// && linksButton.getValue())
			{	kansKeuze = KANSLINKS;
			}
			else if ((e.getSource() == rechtsButton))// && rechtsButton.getValue())
			{	kansKeuze = KANSRECHTS;
			}
			else if ((e.getSource() == tweeGrenzenButton))// && tweeGrenzenButton.getValue())	
			{	kansKeuze = TWEEGRENZEN;
			}

			zetKansKeuze(false);	
		
			bereken();	
			
			paint();
		}
	}
*/
	
// do not use with RadioButtons (Google)	
/*	
	//class BerekenKeuzeAL implements ActionListener
	class BerekenKeuzeCH implements ClickHandler
	{	//public void actionPerformed(ActionEvent e)
		public void onClick(ClickEvent e)
		{	if (muButton.getValue())
				berekenKeuze = BEREKENMU;
			else if (sigmaButton.getValue())
				berekenKeuze = BEREKENSIGMA;
			else if (grensButton.getValue())
				berekenKeuze = BEREKENGRENS;
			else if (kansButton.getValue())
				berekenKeuze = BEREKENKANS;
			else if (grensLinksButton.getValue())
				berekenKeuze = BEREKENGRENSLINKS;
			else if (grensRechtsButton.getValue())
				berekenKeuze = BEREKENGRENSRECHTS;

			zetBerekenKeuze(false);	
			
		}
	}
*/
	
	class BerekenKeuzeVCH implements ValueChangeHandler<Boolean>
	{	//public void actionPerformed(ActionEvent e)
		public void onValueChange(ValueChangeEvent<Boolean> e)
		{	if (e.getSource() == muButton)
				berekenKeuze = BEREKENMU;
			else if (e.getSource() == sigmaButton)
				berekenKeuze = BEREKENSIGMA;
			else if (e.getSource() == grensButton)
				berekenKeuze = BEREKENGRENS;
			else if (e.getSource() == kansButton)
				berekenKeuze = BEREKENKANS;
			else if (e.getSource() == grensLinksButton)
				berekenKeuze = BEREKENGRENSLINKS;
			else if (e.getSource() == grensRechtsButton)
				berekenKeuze = BEREKENGRENSRECHTS;

			zetBerekenKeuze(false);
			paint();
			
		}
	}
	

/*	
	class KijkNaAL implements ActionListener
	{	public void actionPerformed(ActionEvent e)
		{
			kijkNa();
		}
		
	}
*/	
	
/*	
	class TextFL implements FocusListener
	{		
		JTextField inputTextField;
		
		public TextFL(JTextField input)
		{	inputTextField = input;
		}

	
		public void focusGained(FocusEvent e)
		{
		}
		public void focusLost(FocusEvent e)
		{	// invoer user
			String text = inputTextField.getText();
			// komma gebruikt
			if (text.indexOf(',') >= 0)
			{	String text1 = trimTrailingZeros(text, ',');
				boolean changed1 = (text.length() != text1.length());
				String text2 = addLeadingZero(text1, ',');
				boolean changed2 = (text1.length() != text2.length());
				if (changed1 || changed2)
				{	text = text2;
				}
			}
			// punt gebruikt
			if (text.indexOf('.') >= 0)
			{	String text1 = trimTrailingZeros(text, '.');
				boolean changed1 = (text.length() != text1.length());
				String text2 = addLeadingZero(text1, '.');
				boolean changed2 = (text1.length() != text2.length());
				if (changed1 || changed2)
				{	text = text2;
				}
			}	
			inputTextField.setText(text);				

			String format = new String(text);		
			format = format.replace(',', '.');		

			double userInput = 0;
			boolean error = false;
			try
			{	userInput = Double.parseDouble(format);
			}
			catch (NumberFormatException nfe)
			{	error = true;
			}
			// dit zou niet moeten gebeuren
			// Peter: nu wel bij de definitie van een random variabele ipv een double			
			if (error)
				return;

			if (inputTextField == muTextField)
			{	zetMu(userInput, true, true);
			}
			else if (inputTextField == sigmaTextField)
			{	zetSigma(userInput, true, true);
			}
			else if (inputTextField == grensTextField)
			{	zetGrens(userInput, true);
			}
			else if (inputTextField == grensLinksTextField)
			{	zetGrensLinks(userInput, true);
			}
			else if (inputTextField == grensRechtsTextField)
			{	zetGrensRechts(userInput, true);
			}
			else if (inputTextField == kansTextField)
			{	zetKans(userInput, true);
			}
		} // focusLost
	}
*/

	class TextBoxKeyDownHandler implements KeyDownHandler
	{	
		TextBox inputTextField;
		
		public TextBoxKeyDownHandler(TextBox input)
		{	inputTextField = input;
		}
		
		public void onKeyDown(KeyDownEvent e)
		{
			
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{

				String text = inputTextField.getText();
			
				// komma gebruikt
				if (text.indexOf(',') >= 0)
				{	String text1 = trimTrailingZeros(text, ',');
					boolean changed1 = (text.length() != text1.length());
					String text2 = addLeadingZero(text1, ',');
					boolean changed2 = (text1.length() != text2.length());
					if (changed1 || changed2)
					{	text = text2;
					}
				}
				// punt gebruikt
				if (text.indexOf('.') >= 0)
				{	String text1 = trimTrailingZeros(text, '.');
					boolean changed1 = (text.length() != text1.length());
					String text2 = addLeadingZero(text1, '.');
					boolean changed2 = (text1.length() != text2.length());
					if (changed1 || changed2)
					{	text = text2;
					}
				}	
				inputTextField.setText(text);				

				String format = new String(text);		
				format = format.replace(',', '.');		

				double userInput = 0;
				boolean error = false;
				try
				{	userInput = Double.parseDouble(format);
				}
				catch (NumberFormatException nfe)
				{	error = true;
				}
				// dit zou niet moeten gebeuren  
				// Peter: nu wel bij de definitie van een random variabele ipv een double
				if (error)
				{	return;
				}
			
				if (inputTextField == muTextField)
				{	zetMu(userInput, true, true);
				}
				else if (inputTextField == sigmaTextField)
				{	zetSigma(userInput, true, true);
				}
				else if (inputTextField == grensTextField)
				{	zetGrens(userInput, true);
				}
				else if (inputTextField == grensLinksTextField)
				{	zetGrensLinks(userInput, true);
				}
				else if (inputTextField == grensRechtsTextField)
				{	zetGrensRechts(userInput, true);
				}
				else if (inputTextField == kansTextField)
				{	zetKans(userInput, true);
				}
			
			} //if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			
		} // onKeyDown 
		
	}

	public String trimTrailingZeros(String s, char decSep)
	{	String txt = new String(s);
		if (txt.indexOf(decSep) < 0)
			return txt;
		char c = txt.charAt(txt.length() - 1);
		while (c == '0')
		{	txt = removeCharAt(txt, txt.length() - 1);
			c = txt.charAt(txt.length() - 1);
		}	
		c = txt.charAt(txt.length() - 1);
		if (c == decSep)
			txt = removeCharAt(txt, txt.length() - 1);
		return txt;		
	}				
		
	public String addLeadingZero(String s, char decSep)
	{	String txt = new String(s);
		// met minteken
		if ((txt.length() >= 2) && (txt.charAt(0) == '-') &&
			(txt.charAt(1) == decSep))
		{	txt = "-0" + txt.substring(1);
		}	
		// zonder minteken
		if ((txt.length() >= 1) && (txt.charAt(0) == decSep))
		{	txt = "0" + txt;
		}
		return txt;
	}


	public String removeCharAt(String s, int index)
	{	String txt = new String(s);
		// eerste
		if (index == 0)
			txt = txt.substring(1);
		// laatste	
		else if (index == (txt.length() - 1))
			txt = txt.substring(0, txt.length() - 1);
		// middenin	
		else
		{	String txt1 = txt.substring(0, index);
			String txt2 = txt.substring(index + 1);
			txt = txt1 + txt2;
		}
		return txt;
	}		
/*	
	class InputKL extends KeyAdapter
	{	
		JTextField inputTextField;
		boolean minusAllowed;
		
		public InputKL(JTextField input, boolean minAllowed)
		{	inputTextField = input;
			minusAllowed = minAllowed;
		}
		public void keyReleased(KeyEvent e)
		{	
			inputTextField.setForeground(Color.black);
		
			String txt = inputTextField.getText();
			
			//om randomvariabele in te kunnen vullen
			if (isLegal(txt))
				return;

//System.out.println(txt);
				
			boolean corrected = false;

			// kijk of txt illegale characters bevat
			// dit zou er maximaal 1 moeten zijn
			int index = -1;
			for (int cCnt = 0; cCnt < txt.length(); cCnt++)
			{	char c = txt.charAt(cCnt);
				if (!isLegal(c))
				{	index = cCnt;
//System.out.println("illegal " + index);				
				}
			}	
			// verwijder illegaal karakter
			if (index >= 0)
			{	txt = removeCharAt(txt, index);
				corrected = true;
//System.out.println("corr " + txt);							
			}
			
//System.out.println(txt);			
			
			// dubbele decimale komma
			// voldoende er twee te zoeken
			int pIndex1 = txt.indexOf(',');
			int pIndex2 = txt.lastIndexOf(',');
			if ((pIndex1 >= 0) && (pIndex2 >= 0) && (pIndex1 != pIndex2))
			{	// verwijderen
				txt = removeCharAt(txt, pIndex2);
				corrected = true;
			}

			// dubbele decimale punt
			// voldoende er twee te zoeken
			pIndex1 = txt.indexOf('.');
			pIndex2 = txt.lastIndexOf('.');
			if ((pIndex1 >= 0) && (pIndex2 >= 0) && (pIndex1 != pIndex2))
			{	// verwijderen
				txt = removeCharAt(txt, pIndex2);
				corrected = true;
			}
			
			// komma na decimale punt
			pIndex1 = txt.indexOf('.');
			pIndex2 = txt.lastIndexOf(',');
			if ((pIndex1 >= 0) && (pIndex2 >= 0) && (pIndex1 < pIndex2))
			{	// verwijderen
				txt = removeCharAt(txt, pIndex2);
				corrected = true;
			}
			
			// punt na decimale komma
			pIndex1 = txt.indexOf(',');
			pIndex2 = txt.lastIndexOf('.');
			if ((pIndex1 >= 0) && (pIndex2 >= 0) && (pIndex1 < pIndex2))
			{	// verwijderen
				txt = removeCharAt(txt, pIndex2);
				corrected = true;
			}
			
			// proberen een legaal karakter voor het
			// minteken (dit staat dan op plek 1) in te vullen
			if (txt.indexOf('-') == 1)
			{	txt = removeCharAt(txt, 0);
				corrected = true;
			}
			
			// minteken
			// alleen vooraan if any
			int minIndex = txt.lastIndexOf('-');
			if (minIndex > 0)
			{	txt = removeCharAt(txt, minIndex);
				corrected = true;
			}
			
			
			// leading zeros, leiden niet tot een NumberFormatException
			// geval met minteken
			if ((txt.indexOf('-') == 0) && (txt.length() >= 3) &&
				(txt.charAt(1) == '0') && Character.isDigit(txt.charAt(2)))
			{	txt = removeCharAt(txt, 1);
				corrected = true;
			}
			
			// leading zeros, leiden niet tot een NumberFormatException	
			// geen minteken
			if ((txt.indexOf('-') < 0) && (txt.length() >= 2) &&
				(txt.charAt(0) == '0') && Character.isDigit(txt.charAt(1)))
			{	txt = removeCharAt(txt, 0);
				corrected = true;
			}
			
			// trailing zeros na(!) decimale punt oplossen 
			// bij actionPerformed of focusLost			

			if (corrected)
			{	
//System.out.println("corr " + txt);							
				inputTextField.setText(txt);
			
			}
			
		}
		
		public boolean isLegal(String s)
		{	
			if (s != null && s.length() > 0)
				return s.charAt(0) == '#';
			else 
				return false;
		}
		
		public boolean isLegal(char c)
		{	
			if (minusAllowed)
				return Character.isDigit(c) || (c == ',') || (c == '.') || (c == '-');
			else	
				return Character.isDigit(c) || (c == ',') || (c == '.');
		}
	}	
*/
/*	
	// interface WiskOpdrApplet
	public InteractiePanel getInteractiePanel()
	{	return this;
	}
*/
/*	
	// interface InteractiePanel
	public void zetOpdracht(Hashtable b, String[] randomVars, Hashtable randomValues)
	{	
		
//System.out.println("raval " + randomVars.length);		
		double mu = 0;
		double sigma = 1;
		double grens = mu + 1;
		double grensLinks = mu - 1;
		double grensRechts = mu + 1;
		double kans = 6e-1d;
		int kansKeuze = KANSLINKS;
		int berekenKeuze = BEREKENKANS;
		
		if (b.containsKey("mu"))
			mu = ((Double) b.get("mu")).doubleValue();
		if (b.containsKey("sigma"))
			sigma = ((Double) b.get("sigma")).doubleValue();	
		if (b.containsKey("grens"))
			grens = ((Double) b.get("grens")).doubleValue();	
		if (b.containsKey("grenslinks"))
			grensLinks = ((Double) b.get("grenslinks")).doubleValue();	
		if (b.containsKey("grensrechts"))
			grensRechts = ((Double) b.get("grensrechts")).doubleValue();	
		if (b.containsKey("kans"))
			kans = ((Double) b.get("kans")).doubleValue();	
		
		if (b.containsKey("kanskeuze"))
			kansKeuze = ((Integer) b.get("kanskeuze")).intValue();	
		if (b.containsKey("berekenkeuze"))
			berekenKeuze = ((Integer) b.get("berekenkeuze")).intValue();	

		this.kansKeuze = kansKeuze;
		this.berekenKeuze = berekenKeuze;	

		boolean kansLinksOptie = true;
		boolean kansRechtsOptie = true;
		boolean tweeGrenzenOptie = true;
		
		boolean berekenbaarZichtbaar = true;
		boolean muBerekenbaarOptie = false;
		boolean sigmaBerekenbaarOptie = false;
		
		boolean muVastOptie = false;
		boolean sigmaVastOptie = false;
		
		boolean muSliderOptie = false;
		boolean sigmaSliderOptie = false;
		boolean grensSliderOptie = true;
		boolean kansSliderOptie = false;
		
		boolean muZichtbaarOptie = true;
		boolean sigmaZichtbaarOptie = true;
		boolean grensZichtbaarOptie = true;
		boolean kansZichtbaarOptie = true;
		
		boolean muZichtbaarFigOptie = true;
		boolean sigmaZichtbaarFigOptie = true;
		boolean grensZichtbaarFigOptie = true;
		boolean kansZichtbaarFigOptie = true;
		
		String muString = "";
		String sigmaString = "";
		String grensString = "";
		String grensLinksString = "";
		String grensRechtsString = "";
		String kansString = "";
		
		if (b.containsKey("kanslinksoptie"))
			kansLinksOptie = ((Boolean) b.get("kanslinksoptie")).booleanValue();
		if (b.containsKey("kansrechtsoptie"))
			kansRechtsOptie = ((Boolean) b.get("kansrechtsoptie")).booleanValue();
		if (b.containsKey("tweegrenzenoptie"))
			tweeGrenzenOptie = ((Boolean) b.get("tweegrenzenoptie")).booleanValue();
		
		if (b.containsKey("berekenbaarZichtbaar"))
			berekenbaarZichtbaar = ((Boolean) b.get("berekenbaarZichtbaar")).booleanValue();
		if (b.containsKey("muberekenbaaroptie"))
			muBerekenbaarOptie = ((Boolean) b.get("muberekenbaaroptie")).booleanValue();
		if (b.containsKey("sigmaberekenbaaroptie"))	
			sigmaBerekenbaarOptie = ((Boolean) b.get("sigmaberekenbaaroptie")).booleanValue();
		
		if (b.containsKey("muvastoptie"))
			muVastOptie = ((Boolean) b.get("muvastoptie")).booleanValue();
		if (b.containsKey("sigmavastoptie"))
			sigmaVastOptie = ((Boolean) b.get("sigmavastoptie")).booleanValue();
		
		if (b.containsKey("muSliderOptie")) 
			muSliderOptie = ((Boolean) b.get("muSliderOptie")).booleanValue();
		if (b.containsKey("sigmaSliderOptie")) 
			sigmaSliderOptie = ((Boolean) b.get("sigmaSliderOptie")).booleanValue();
		if (b.containsKey("kansSliderOptie")) 
			kansSliderOptie = ((Boolean) b.get("kansSliderOptie")).booleanValue();
		if (b.containsKey("grensSliderOptie")) 
			grensSliderOptie = ((Boolean) b.get("grensSliderOptie")).booleanValue();

		if (b.containsKey("muZichtbaarFigOptie")) 
			muZichtbaarFigOptie = ((Boolean) b.get("muZichtbaarFigOptie")).booleanValue();
		if (b.containsKey("sigmaZichtbaarFigOptie")) 
			sigmaZichtbaarFigOptie = ((Boolean) b.get("sigmaZichtbaarFigOptie")).booleanValue();
		if (b.containsKey("grensZichtbaarFigOptie")) 
			grensZichtbaarFigOptie = ((Boolean) b.get("grensZichtbaarFigOptie")).booleanValue();
		if (b.containsKey("kansZichtbaarFigOptie")) 
			kansZichtbaarFigOptie = ((Boolean) b.get("kansZichtbaarFigOptie")).booleanValue();

		if (b.containsKey("muZichtbaarOptie")) 
			muZichtbaarOptie = ((Boolean) b.get("muZichtbaarOptie")).booleanValue();
		if (b.containsKey("sigmaZichtbaarOptie")) 
			sigmaZichtbaarOptie = ((Boolean) b.get("sigmaZichtbaarOptie")).booleanValue();
		if (b.containsKey("grensZichtbaarOptie")) 
			grensZichtbaarOptie = ((Boolean) b.get("grensZichtbaarOptie")).booleanValue();
		if (b.containsKey("kansZichtbaarOptie")) 
			kansZichtbaarOptie = ((Boolean) b.get("kansZichtbaarOptie")).booleanValue();
		
		if (b.containsKey("muString"))
			muString = (String) b.get("muString");
		if (b.containsKey("sigmaString"))
			sigmaString = (String) b.get("sigmaString");
		if (b.containsKey("grensString"))
			grensString = (String) b.get("grensString");
		if (b.containsKey("grensLinksString"))
			grensLinksString = (String) b.get("grensLinksString");
		if (b.containsKey("grensRechtsString"))
			grensRechtsString = (String) b.get("grensRechtsString");
		if (b.containsKey("kansString"))
			kansString = (String) b.get("kansString");
		
		if (muString.length() > 0 && muString.charAt(0) == '#' && 
			muString.charAt(muString.length() - 1) == '#') 
			mu = substitueerRandom(mu, muString, randomVars, randomValues);
		if (sigmaString.length() > 0 && sigmaString.charAt(0) == '#' && 
			sigmaString.charAt(sigmaString.length() - 1) == '#') 
			sigma = substitueerRandom(sigma, sigmaString, randomVars, randomValues);
		if (grensString.length() > 0 && grensString.charAt(0) == '#' && 
			grensString.charAt(grensString.length() - 1) == '#') 
			grens = substitueerRandom(grens, grensString, randomVars, randomValues);
		if (grensLinksString.length() > 0 && grensLinksString.charAt(0) == '#' && 
			grensLinksString.charAt(grensLinksString.length() - 1) == '#') 
			grensLinks = substitueerRandom(grensLinks, grensLinksString, randomVars, randomValues);
		if (grensRechtsString.length() > 0 && grensRechtsString.charAt(0) == '#' && 
			grensRechtsString.charAt(grensRechtsString.length() - 1) == '#') 
			grensRechts = substitueerRandom(grensRechts, grensRechtsString, randomVars, randomValues);
		if (kansString.length() > 0 && kansString.charAt(0) == '#' && 
			kansString.charAt(kansString.length() - 1) == '#') 
			kans = substitueerRandom(kans,kansString, randomVars, randomValues);
		
		zetMu(mu, false, true);		
		zetSigma(sigma, false, true);
		zetGrens(grens, false);
		zetGrensLinks(grensLinks, false);				
		zetGrensRechts(grensRechts, false);						
		zetKans(kans, false);

		zetKansKeuze();
		
		if (this.kansKeuze == TWEEGRENZEN)
		{	zetGrensLinks(grensLinks, false);				
			zetGrensRechts(grensRechts, false);						
		}
		
		zetBerekenKeuze();
		
		this.kansLinksOptie = kansLinksOptie;
		this.kansRechtsOptie = kansRechtsOptie;		
		this.tweeGrenzenOptie = tweeGrenzenOptie;
		
		this.muBerekenbaarOptie = muBerekenbaarOptie;
		this.berekenbaarZichtbaar = berekenbaarZichtbaar;
		this.sigmaBerekenbaarOptie = sigmaBerekenbaarOptie;
		
		this.muVastOptie = muVastOptie;
		this.sigmaVastOptie = sigmaVastOptie;
		
		this.muSliderOptie = muSliderOptie;
		this.sigmaSliderOptie = sigmaSliderOptie;
		this.kansSliderOptie = kansSliderOptie;
		this.grensSliderOptie = grensSliderOptie;
		
		this.muZichtbaarOptie = muZichtbaarOptie;
		this.sigmaZichtbaarOptie = sigmaZichtbaarOptie;
		this.grensZichtbaarOptie = grensZichtbaarOptie;
		this.kansZichtbaarOptie = kansZichtbaarOptie;
		
		this.muZichtbaarFigOptie = muZichtbaarFigOptie;
		this.sigmaZichtbaarFigOptie = sigmaZichtbaarFigOptie;
		this.grensZichtbaarFigOptie = grensZichtbaarFigOptie;
		this.kansZichtbaarFigOptie = kansZichtbaarFigOptie;
		
		zetKansOpties();
		
		zetMuBerekenbaarOptie(this.muBerekenbaarOptie);
		
		zetSigmaBerekenbaarOptie(this.sigmaBerekenbaarOptie);						
		
		zetMuVastOptie(this.muVastOptie);

		zetSigmaVastOptie(this.sigmaVastOptie);						
		
		zetMuSliderOptie(this.muSliderOptie);
		zetSigmaSliderOptie(this.sigmaSliderOptie);
		zetGrensSliderOptie(this.grensSliderOptie);		
		zetKansSliderOptie(this.kansSliderOptie);

		zetMuZichtbaarOptie(this.muZichtbaarOptie);
		zetSigmaZichtbaarOptie(this.sigmaZichtbaarOptie);
		zetGrensZichtbaarOptie(this.grensZichtbaarOptie);
		zetKansZichtbaarOptie(this.kansZichtbaarOptie);
		
		zetMuZichtbaarFigOptie(this.muZichtbaarFigOptie);
		zetSigmaZichtbaarFigOptie(this.sigmaZichtbaarFigOptie);
		zetGrensZichtbaarFigOptie(this.grensZichtbaarFigOptie);
		zetKansZichtbaarFigOptie(this.kansZichtbaarFigOptie);
		

		zetBerekenKeuze();

		bereken();

		if (b.containsKey("kijkNa"))
			kijkOpdrachtNa = ((Boolean) b.get("kijkNa")).booleanValue();

		zetKijkOpdrachtNa(kijkOpdrachtNa);
		
		if (kijkOpdrachtNa)
		{
		
			antwoordMu = 0;
			if (b.containsKey("kijkMuNa"))
			{	kijkMuNa = ((Boolean) b.get("kijkMuNa")).booleanValue();
				if (b.containsKey("checkMu"))
				{	String checkMu = (String) b.get("checkMu");
					if (checkMu.length() > 0 && checkMu.charAt(0) == '#' && 
						checkMu.charAt(checkMu.length() - 1) == '#') 
						antwoordMu = substitueerRandom(antwoordMu, checkMu, randomVars, randomValues);
					else if (!checkMu.equals(""))
						antwoordMu = Double.parseDouble(checkMu);
					
//System.out.println("am = " + antwoordMu);					
				}
			}
			antwoordSigma = 1;
			if (b.containsKey("kijkSigmaNa"))
			{	kijkSigmaNa = ((Boolean) b.get("kijkSigmaNa")).booleanValue();
				if (b.containsKey("checkSigma"))
				{	String checkSigma = (String) b.get("checkSigma");
					if (checkSigma.length() > 0 && checkSigma.charAt(0) == '#' && 
						checkSigma.charAt(checkSigma.length() - 1) == '#') 
						antwoordSigma = substitueerRandom(antwoordSigma, checkSigma, randomVars, randomValues);
					else if (!checkSigma.equals(""))
						antwoordSigma = Double.parseDouble(checkSigma);
				}
			}
			antwoordGrens = antwoordMu - 1;
			if (b.containsKey("kijkGrensNa"))
			{	kijkGrensNa = ((Boolean) b.get("kijkGrensNa")).booleanValue();
				if (b.containsKey("checkGrens"))
				{	String checkGrens = (String) b.get("checkGrens");
					if (checkGrens.length() > 0 && checkGrens.charAt(0) == '#' && 
						checkGrens.charAt(checkGrens.length() - 1) == '#') 
						antwoordGrens = substitueerRandom(antwoordGrens, checkGrens, randomVars, randomValues);
					else if (!checkGrens.equals(""))
						antwoordGrens = Double.parseDouble(checkGrens);
				}
			}
			antwoordGrensLinks = antwoordMu - 1;
			if (b.containsKey("kijkGrensLinksNa"))
			{	kijkGrensLinksNa = ((Boolean) b.get("kijkGrensLinksNa")).booleanValue();
				if (b.containsKey("checkGrensLinks"))
				{	String checkGrensLinks = (String) b.get("checkGrensLinks");
					if (checkGrensLinks.length() > 0 && checkGrensLinks.charAt(0) == '#' && 
						checkGrensLinks.charAt(checkGrensLinks.length() - 1) == '#') 
						antwoordGrensLinks = substitueerRandom(antwoordGrensLinks, checkGrensLinks, randomVars, randomValues);
					else if (!checkGrensLinks.equals(""))
						antwoordGrensLinks = Double.parseDouble(checkGrensLinks);
				}
			}
			antwoordGrensRechts = antwoordMu + 1;
			if (b.containsKey("kijkGrensRechtsNa"))
			{	kijkGrensRechtsNa = ((Boolean) b.get("kijkGrensRechtsNa")).booleanValue();
				if (b.containsKey("checkGrensRechts"))
				{	String checkGrensRechts = (String) b.get("checkGrensRechts");
					if (checkGrensRechts.length() > 0 && checkGrensRechts.charAt(0) == '#' && 
						checkGrensRechts.charAt(checkGrensRechts.length() - 1) == '#') 
						antwoordGrensRechts = substitueerRandom(antwoordGrensRechts, checkGrensRechts, randomVars, randomValues);
					else if (!checkGrensRechts.equals(""))
						antwoordGrensRechts = Double.parseDouble(checkGrensRechts);
				}
			}
			antwoordKans = 25e-2d;
			if (b.containsKey("kijkKansNa"))
			{	kijkKansNa = ((Boolean) b.get("kijkKansNa")).booleanValue();
				if (b.containsKey("checkKans"))
				{	String checkKans = (String) b.get("checkKans");
					if (checkKans.length() > 0 && checkKans.charAt(0) == '#' && 
						checkKans.charAt(checkKans.length() - 1) == '#') 
						antwoordKans = substitueerRandom(antwoordKans, checkKans, randomVars, randomValues);
					else if (!checkKans.equals(""))
						antwoordKans = Double.parseDouble(checkKans);
				}
			}
			maxScore = 0;
			if (b.containsKey("maxScore"))
			{	String maxScoreStr = (String) b.get("maxScore");
				if (!maxScoreStr.equals(""))
					maxScore = Integer.parseInt(maxScoreStr);
			}
			
			
			
			
		}
		
	}
*/
	
/*	
	public static double substitueerRandom(double def, String s, String[] randomVars, Hashtable randomValues) 
	{	double d = Double.NaN;
		s = s.substring(1, s.length() - 1);
		String[] delen = StringUtils.split(s, "/");
		int decFactor = 1;
		
		for (int j = 0 ; j < randomVars.length; j++)
		{	
//System.out.println("rava " + j + " " + randomVars[j]);			
			if (randomVars[j].equals(delen[0])) 
				d = ((Integer) randomValues.get(randomVars[j])).intValue();
		}
		if (delen.length > 1)
		{	decFactor = Integer.parseInt(delen[1]);
			d = d / decFactor;
		}
		if (Double.isNaN(d)) 
			d = def;
		return d;
	}
*/
	public void setState(Map map)
	{	
		
//System.out.println("nvdp setState");
//System.out.println("muVastOptie " + muVastOptie);

		ObjectMap b = JSONUtilities.wrapMap(map);
		
		double mu = 0;
		double sigma = 1;
		double grens = mu + 1;
		double grensLinks = mu - 1;
		double grensRechts = mu + 1;
		double kans = 6e-1d;
		int kansKeuze = KANSLINKS;
		int berekenKeuze = BEREKENKANS;
		
		boolean kijkOpdrachtNa = false;
		
		if (b.containsKey("mu"))
			mu = b.getDouble("mu");
		if (b.containsKey("sigma"))
			sigma = b.getDouble("sigma");	
		if (b.containsKey("grens"))
			grens = b.getDouble("grens");	
		if (b.containsKey("grenslinks"))
			grensLinks = b.getDouble("grenslinks");	
		if (b.containsKey("grensrechts"))
			grensRechts = b.getDouble("grensrechts");	
		if (b.containsKey("kans"))
			kans = b.getDouble("kans");	
		
		if (b.containsKey("kanskeuze"))
			kansKeuze = b.getInt("kanskeuze");	
		if (b.containsKey("berekenkeuze"))
			berekenKeuze = b.getInt("berekenkeuze");	

		//if (b.containsKey("kijkOpdrachtNa"))
		//	kijkOpdrachtNa = b.getBoolean("kijkOpdrachtNa");	
		
		this.kansKeuze = kansKeuze;
		this.berekenKeuze = berekenKeuze;	
		
		//this.kijkOpdrachtNa = kijkOpdrachtNa;

		zetMu(mu, false, true);
		zetSigma(sigma, false, true);
		zetGrens(grens, false);
		
//		this.grensRechts = grensRechts;
		
		zetGrensLinks(grensLinks, false);				
		zetGrensRechts(grensRechts, false);						
		zetKans(kans, false);
		
//System.out.println("k = " + kans);		
		
		zetKansKeuze(true);
		
		if (this.kansKeuze == TWEEGRENZEN)
		{	zetGrensLinks(grensLinks, false);				
			zetGrensRechts(grensRechts, false);						
		}
		
		zetBerekenKeuze(false);
		
		bereken();
	}
	
	public void setInitState()
	{
		zetMu(mu, false, true);
		zetSigma(sigma, false, true);
		zetGrens(grens, false);
		
//		this.grensRechts = grensRechts;
		
		zetGrensLinks(grensLinks, false);				
		zetGrensRechts(grensRechts, false);						
		zetKans(kans, false);
		
//System.out.println("k = " + kans);		
		
		zetKansKeuze(true);
		
		if (this.kansKeuze == TWEEGRENZEN)
		{	zetGrensLinks(grensLinks, false);				
			zetGrensRechts(grensRechts, false);						
		}
		
		zetBerekenKeuze(false);
		
		bereken();
		
	}
	
/*	
	public void setEditState(Hashtable b)
	{	
		setState(b);
		
		boolean kansLinksOptie = true;
		boolean kansRechtsOptie = true;
		boolean tweeGrenzenOptie = true;
		
		boolean berekenbaarZichtbaar = true;
		boolean muBerekenbaarOptie = false;
		boolean sigmaBerekenbaarOptie = false;
		
		boolean muVastOptie = false;
		boolean sigmaVastOptie = false;
		
		boolean muSliderOptie = false;
		boolean sigmaSliderOptie = false;
		boolean grensSliderOptie = true;
		boolean kansSliderOptie = false;
		
		boolean muZichtbaarOptie = true;
		boolean sigmaZichtbaarOptie = true;
		boolean grensZichtbaarOptie = true;
		boolean kansZichtbaarOptie = true;
		
		boolean muZichtbaarFigOptie = true;
		boolean sigmaZichtbaarFigOptie = true;
		boolean grensZichtbaarFigOptie = true;
		boolean kansZichtbaarFigOptie = true;
		
		String muString = "";
		String sigmaString = "";
		String grensString = "";
		String grensLinksString = "";
		String grensRechtsString = "";
		String kansString = "";
		
		if (b.containsKey("kanslinksoptie"))
			kansLinksOptie = ((Boolean) b.get("kanslinksoptie")).booleanValue();
		if (b.containsKey("kansrechtsoptie"))
			kansRechtsOptie = ((Boolean) b.get("kansrechtsoptie")).booleanValue();
		if (b.containsKey("tweegrenzenoptie"))
			tweeGrenzenOptie = ((Boolean) b.get("tweegrenzenoptie")).booleanValue();
		
		if (b.containsKey("berekenbaarZichtbaar"))
			berekenbaarZichtbaar = ((Boolean) b.get("berekenbaarZichtbaar")).booleanValue();
		if (b.containsKey("muberekenbaaroptie"))
			muBerekenbaarOptie = ((Boolean) b.get("muberekenbaaroptie")).booleanValue();
		if (b.containsKey("sigmaberekenbaaroptie"))	
			sigmaBerekenbaarOptie = ((Boolean) b.get("sigmaberekenbaaroptie")).booleanValue();
		
		if (b.containsKey("muvastoptie"))
			muVastOptie = ((Boolean) b.get("muvastoptie")).booleanValue();
		if (b.containsKey("sigmavastoptie"))
			sigmaVastOptie = ((Boolean) b.get("sigmavastoptie")).booleanValue();
		
		if (b.containsKey("muSliderOptie")) 
			muSliderOptie = ((Boolean) b.get("muSliderOptie")).booleanValue();
		if (b.containsKey("sigmaSliderOptie")) 
			sigmaSliderOptie = ((Boolean) b.get("sigmaSliderOptie")).booleanValue();
		if (b.containsKey("grensSliderOptie")) 
			grensSliderOptie = ((Boolean) b.get("grensSliderOptie")).booleanValue();			
		if (b.containsKey("kansSliderOptie")) 
			kansSliderOptie = ((Boolean) b.get("kansSliderOptie")).booleanValue();

		if (b.containsKey("muZichtbaarFigOptie")) 
			muZichtbaarFigOptie = ((Boolean) b.get("muZichtbaarFigOptie")).booleanValue();
		if (b.containsKey("sigmaZichtbaarFigOptie")) 
			sigmaZichtbaarFigOptie = ((Boolean) b.get("sigmaZichtbaarFigOptie")).booleanValue();
		if (b.containsKey("grensZichtbaarFigOptie")) 
			grensZichtbaarFigOptie = ((Boolean) b.get("grensZichtbaarFigOptie")).booleanValue();
		if (b.containsKey("kansZichtbaarFigOptie")) 
			kansZichtbaarFigOptie = ((Boolean) b.get("kansZichtbaarFigOptie")).booleanValue();

		if (b.containsKey("muZichtbaarOptie")) 
			muZichtbaarOptie = ((Boolean) b.get("muZichtbaarOptie")).booleanValue();
		if (b.containsKey("sigmaZichtbaarOptie")) 
			sigmaZichtbaarOptie = ((Boolean) b.get("sigmaZichtbaarOptie")).booleanValue();
		if (b.containsKey("grensZichtbaarOptie")) 
			grensZichtbaarOptie = ((Boolean) b.get("grensZichtbaarOptie")).booleanValue();
		if (b.containsKey("kansZichtbaarOptie")) 
			kansZichtbaarOptie = ((Boolean) b.get("kansZichtbaarOptie")).booleanValue();
		
		if (b.containsKey("muString"))
			muString = (String) b.get("muString");
		if (b.containsKey("sigmaString"))
			sigmaString = (String) b.get("sigmaString");
		if (b.containsKey("grensString"))
			grensString = (String) b.get("grensString");
		if (b.containsKey("grensLinksString"))
			grensLinksString = (String) b.get("grensLinksString");
		if (b.containsKey("grensRechtsString"))
			grensRechtsString = (String) b.get("grensRechtsString");
		if (b.containsKey("kansString"))
			kansString = (String) b.get("kansString");
		
		if (!muString.equals(""))
			muTextField.setText(muString);
		if (!muString.equals(""))
			sigmaTextField.setText(sigmaString);
		if (!muString.equals(""))
			grensTextField.setText(grensString);
		if (!muString.equals(""))
			grensLinksTextField.setText(grensLinksString);
		if (!muString.equals(""))
			grensRechtsTextField.setText(grensRechtsString);
		if (!muString.equals(""))
			kansTextField.setText(kansString);
		
		this.kansLinksOptie = kansLinksOptie;
		this.kansRechtsOptie = kansRechtsOptie;		
		this.tweeGrenzenOptie = tweeGrenzenOptie;
		
		this.muBerekenbaarOptie = muBerekenbaarOptie;
		this.berekenbaarZichtbaar = berekenbaarZichtbaar;
		this.sigmaBerekenbaarOptie = sigmaBerekenbaarOptie;
		this.muVastOptie = muVastOptie;
		this.sigmaVastOptie = sigmaVastOptie;
		
		this.muSliderOptie = muSliderOptie;
		this.sigmaSliderOptie = sigmaSliderOptie;
		this.kansSliderOptie = kansSliderOptie;
		this.grensSliderOptie = grensSliderOptie;
		
		this.muZichtbaarOptie = muZichtbaarOptie;
		this.sigmaZichtbaarOptie = sigmaZichtbaarOptie;
		this.grensZichtbaarOptie = grensZichtbaarOptie;
		this.kansZichtbaarOptie = kansZichtbaarOptie;
		
		this.muZichtbaarFigOptie = muZichtbaarFigOptie;
		this.sigmaZichtbaarFigOptie = sigmaZichtbaarFigOptie;
		this.grensZichtbaarFigOptie = grensZichtbaarFigOptie;
		this.kansZichtbaarFigOptie = kansZichtbaarFigOptie;
		
				
		zetKansOpties();
		
		zetMuBerekenbaarOptie(this.muBerekenbaarOptie);
		
//System.out.println("muBerekenbaarOptie = " + this.muBerekenbaarOptie);		
//System.out.println("muVastOptie = " + this.muVastOptie);				
		
		zetSigmaBerekenbaarOptie(this.sigmaBerekenbaarOptie);						
		
		zetMuVastOptie(this.muVastOptie);

//System.out.println("muBerekenbaarOptie = " + this.muBerekenbaarOptie);				
//System.out.println("muVastOptie = " + this.muVastOptie);				


		zetSigmaVastOptie(this.sigmaVastOptie);						
	
//GWT (4)		
		//zetMuSliderOptie(this.muSliderOptie);
		//zetSigmaSliderOptie(this.sigmaSliderOptie);
		//zetGrensSliderOptie(this.grensSliderOptie);		
		//zetKansSliderOptie(this.kansSliderOptie);

		zetMuZichtbaarOptie(this.muZichtbaarOptie);
		zetSigmaZichtbaarOptie(this.sigmaZichtbaarOptie);
		zetGrensZichtbaarOptie(this.grensZichtbaarOptie);
		zetKansZichtbaarOptie(this.kansZichtbaarOptie);
		
		zetMuZichtbaarFigOptie(this.muZichtbaarFigOptie);
		zetSigmaZichtbaarFigOptie(this.sigmaZichtbaarFigOptie);
		zetGrensZichtbaarFigOptie(this.grensZichtbaarFigOptie);
		zetKansZichtbaarFigOptie(this.kansZichtbaarFigOptie);



		zetBerekenKeuze();


		bereken();
		
		
// is dit allemaal nodig??
// zie zetOpdracht		
		
//		if (b.containsKey("kijkNa"))
//			kijkOpdrachtNa = ((Boolean) b.get("kijkNa")).booleanValue();

	}
*/	

	public HashMap<String,Object> getState()
	{	
		HashMap<String,Object> h = new HashMap<String,Object>();
	    
	    h.put("mu", new Double(mu));
	    h.put("sigma", new Double(sigma));
	    h.put("grens", new Double(grens));
	    h.put("grenslinks", new Double(grensLinks));
	    h.put("grensrechts", new Double(grensRechts));
	    h.put("kans", new Double(kans));
	    
	    h.put("kanskeuze", new Integer(kansKeuze));
	    h.put("berekenkeuze", new Integer(berekenKeuze));
	    
	    //h.put("kijkOpdrachtNa", new Boolean(kijkOpdrachtNa));
	    
		return h;
	}

/*	
	public HashMap<String,Object> getEditState()
	{	
		String muString = "";
		String sigmaString = "";
		String grensString = "";
		String grensLinksString = "";
		String grensRechtsString = "";
		String kansString = "";
		
		muString = muTextField.getText();
		sigmaString = sigmaTextField.getText();
		grensString = grensTextField.getText();
		grensLinksString = grensLinksTextField.getText();
		grensRechtsString = grensRechtsTextField.getText();
		kansString = kansTextField.getText();
		
	    Hashtable h = getState();

	    h.put("kanslinksoptie", new Boolean(kansLinksOptie));
		h.put("kansrechtsoptie", new Boolean(kansRechtsOptie));	    	    
		h.put("tweegrenzenoptie", new Boolean(tweeGrenzenOptie));	    	    	    
		
		h.put("berekenbaarZichtbaar", new Boolean(berekenbaarZichtbaar));		
	    h.put("muberekenbaaroptie", new Boolean(muBerekenbaarOptie));		
	    h.put("sigmaberekenbaaroptie", new Boolean(sigmaBerekenbaarOptie));			    
	    
	    h.put("muvastoptie", new Boolean(muVastOptie));		
	    h.put("sigmavastoptie", new Boolean(sigmaVastOptie));
	    
	    h.put("muSliderOptie", new Boolean(muSliderOptie));
		h.put("sigmaSliderOptie", new Boolean(sigmaSliderOptie));
		h.put("grensSliderOptie", new Boolean(grensSliderOptie));	
		h.put("kansSliderOptie", new Boolean(kansSliderOptie));	
		
		h.put("muZichtbaarOptie", new Boolean(muZichtbaarOptie));
		h.put("sigmaZichtbaarOptie", new Boolean(sigmaZichtbaarOptie));
		h.put("grensZichtbaarOptie", new Boolean(grensZichtbaarOptie));	
		h.put("kansZichtbaarOptie", new Boolean(kansZichtbaarOptie));	
		
		h.put("muZichtbaarFigOptie", new Boolean(muZichtbaarFigOptie));
		h.put("sigmaZichtbaarFigOptie", new Boolean(sigmaZichtbaarFigOptie));
		h.put("grensZichtbaarFigOptie", new Boolean(grensZichtbaarFigOptie));	
		h.put("kansZichtbaarFigOptie", new Boolean(kansZichtbaarFigOptie));	

	    h.put("muString", muString);
	    h.put("sigmaString", sigmaString);
	    h.put("grensString", grensString);
	    h.put("grensLinksString", grensLinksString);
	    h.put("grensRechtsString", grensRechtsString);
	    h.put("kansString", kansString);
	    

	    return h;
	}
*/	
/*
	public InteractieEditPanel getEditPanel()
	{	
		return new NormaalEditPanel(getSize().width + editBreedte, getSize().height);
	}
*/
/*	
	// dit moet natuurlijk met een super !!		
	public void setBounds(int x, int y, int b, int h)
	{	super.setBounds(x, y, b, h);
	
		plaatsComponenten();
	}
*/
	public void wis()
	{}
	
	public void zetMaat()
	{}
	
	public int geefAsHoogte()
	{	return 0;
	}

	public int getScore()
	{	return score;
	}
	
	public int getScoreMax()
	{	return maxScore;
	}
	
	public boolean isCorrect()
	{	return score == maxScore;
	}
	
	public boolean isFout()
	{	return score != maxScore;
	}
	
	public void zetMode(int mode)
	{}
	
	public void zetNagekeken(boolean b)
	{}

	public void stop()
	{}
	
	public void start()
	{}
	
	public int getIpId()
	{	return 0;
	}
    
    public String getIpExpString()
    {	return null;
    }

    public void destroy()
    {}
    
    public void opnieuw()
    {}

//GWT    
    
    public void kijkNa()
    {
    	
System.out.println("kijkNa");

		boolean correct = true;
		if (kijkOpdrachtNa) 
		{
			if (kijkMuNa)
			{	double muIn = round(mu, muDecimals);
				double muAn = round(antwoordMu, muDecimals);
				correct = correct && (Math.abs(muIn - muAn) < NZERO);
			}
			
			if (kijkSigmaNa)
			{	double sigmaIn = round(sigma, sigmaDecimals);
				double sigmaAn = round(antwoordSigma, sigmaDecimals);
				correct = correct && (Math.abs(sigmaIn - sigmaAn) < NZERO);
			}

			if (kijkGrensNa)
			{	double grensIn = round(grens, grensDecimals);
				double grensAn = round(antwoordGrens, grensDecimals);
				correct = correct && (Math.abs(grensIn - grensAn) < NZERO);
			}

			if (kijkGrensLinksNa)
			{	double grensLinksIn = round(grensLinks, grensDecimals);
				double grensLinksAn = round(antwoordGrensLinks, grensDecimals);
				correct = correct && (Math.abs(grensLinksIn - grensLinksAn) < NZERO);
			}

			if (kijkGrensRechtsNa)
			{	double grensRechtsIn = round(grensRechts, grensDecimals);
				double grensRechtsAn = round(antwoordGrensRechts, grensDecimals);
				correct = correct && (Math.abs(grensRechtsIn - grensRechtsAn) < NZERO);
			}
			
			if (kijkKansNa)
			{	double kansIn = round(kans, kansDecimals);
				double kansAn = round(antwoordKans, kansDecimals);
				correct = correct && (Math.abs(kansIn - kansAn) < NZERO);
System.out.println("kansIn " + kansIn);
System.out.println("kansAn " + kansAn);
			}
			
			
		}
		
		if (correct) 
		{	score = maxScore;
		}
		else 
		{	score = 0;
		}
		
		//vinkjeLabel.setVisible(correct);
		//kruisjeLabel.setVisible(!correct);
		kijkNaPanel.setWidgetVisible(owner.goedKrulImage, correct);
		kijkNaPanel.setWidgetVisible(owner.foutKruisImage, !correct);

		
		
		//fire actionEvent
		//ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "changed");
		//for (int lCnt = 0; lCnt < listeners.size(); lCnt++)
		//{
		//	((ActionListener) listeners.elementAt(lCnt)).actionPerformed(event);
		//}
		
	}
    	
    
    public void kijkNa(int stapNr)
    {}

//GWT
/*    
    public void addActionListener(ActionListener al)
    {
    	listeners.addElement(al);
    }
*/
    
/*    
	public void actionPerformed(ActionEvent e)
	{	
		if ((e.getSource() == muSlider) &&
			e.getActionCommand().equals("verschoven"))
		{	processMuSlider();
		}		
	
		if ((e.getSource() == sigmaSlider) &&
			e.getActionCommand().equals("verschoven"))
		{	processSigmaSlider();
		}		
	
		if ((e.getSource() == grensSlider) &&
			e.getActionCommand().equals("verschoven"))
		{	processGrensSlider();
		}		
		
		if ((e.getSource() == grensSlider) &&
			e.getActionCommand().equals("start"))
		{	lowerGrensLabels = true;
			//muMetWaardeLabel.setVisible(false);
			
			fastPaint();
		}
		if ((e.getSource() == grensSlider) &&
			e.getActionCommand().equals("stop"))
		{	lowerGrensLabels = false;
			//muMetWaardeLabel.setVisible(muZichtbaarFigOptie);

			fastPaint();
		}
		
		if ((e.getSource() == tweeGrenzenSlider) &&
		    e.getActionCommand().equals("verschovenLinks"))
		{	processTweeGrenzenSlider(true);
		}    
		if ((e.getSource() == tweeGrenzenSlider) &&
		    e.getActionCommand().equals("startLinks"))
		{	lowerGrensLinksLabels = true;
			lowerGrensRechtsLabels = true;
			fastPaint();
		}    
		if ((e.getSource() == tweeGrenzenSlider) &&
		    e.getActionCommand().equals("verschovenRechts"))
		{	processTweeGrenzenSlider(false);
		}    
		if ((e.getSource() == tweeGrenzenSlider) &&
		    e.getActionCommand().equals("startRechts"))
		{	lowerGrensLinksLabels = true;
			lowerGrensRechtsLabels = true;
	
			fastPaint();
		}    
		if ((e.getSource() == tweeGrenzenSlider) &&
		    e.getActionCommand().equals("stop"))
		{	lowerGrensLinksLabels = false;
			lowerGrensRechtsLabels = false;
			
//System.out.println("l = " + tweeGrenzenSlider.geefStandLinks());
//System.out.println("r = " + tweeGrenzenSlider.geefStandRechts());			
			fastPaint();
		}    
		if ((e.getSource() == kansSlider) &&
			e.getActionCommand().equals("verschoven"))
		{	processKansSlider();
		}		
		
		
	}
*/
/*	
	public void zetBreedte(int b)
	{	setBounds(getLocation().x, getLocation().y, b, getSize().height);
	}
*/
/*	
	public void zetHoogte(int h)
	{	setBounds(getLocation().x, getLocation().y, getSize().width, h);
	}
*/	
    
    public void mouseDownTouchStartAction(int eventX, int eventY)
    {
    	
//System.out.println("np mdtsaction");

    	if ((muSlider != null) && muSlider.sliderRectangle.contains(eventX, eventY))
    		muSlider.mouseDownTouchStartAction(eventX, eventY);
    	if ((sigmaSlider != null) && sigmaSlider.sliderRectangle.contains(eventX, eventY))
    		sigmaSlider.mouseDownTouchStartAction(eventX, eventY);
    	if ((grensSlider != null) && grensSlider.sliderRectangle.contains(eventX, eventY))
    		grensSlider.mouseDownTouchStartAction(eventX, eventY);
    	if ((kansSlider != null) && kansSlider.sliderRectangle.contains(eventX, eventY))
    		kansSlider.mouseDownTouchStartAction(eventX, eventY);

    	if ((tweeGrenzenSlider != null) && tweeGrenzenSlider.sliderRectangle.contains(eventX, eventY))
    		tweeGrenzenSlider.mouseDownTouchStartAction(eventX, eventY);
    	
    }	
    
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{

//System.out.println("np mmtmaction");	

		if ((muSlider != null) && muSlider.sliderRectangle.contains(eventX, eventY))
			muSlider.mouseMoveTouchMoveAction(eventX, eventY);
    	if ((sigmaSlider != null) && sigmaSlider.sliderRectangle.contains(eventX, eventY))
    		sigmaSlider.mouseMoveTouchMoveAction(eventX, eventY);
    	if ((grensSlider != null) && grensSlider.sliderRectangle.contains(eventX, eventY))
    		grensSlider.mouseMoveTouchMoveAction(eventX, eventY);
    	if ((kansSlider != null) && kansSlider.sliderRectangle.contains(eventX, eventY))
    		kansSlider.mouseMoveTouchMoveAction(eventX, eventY);

    	if ((tweeGrenzenSlider != null) && tweeGrenzenSlider.sliderRectangle.contains(eventX, eventY))
    		tweeGrenzenSlider.mouseMoveTouchMoveAction(eventX, eventY);
    	
}
    
    public void mouseUpTouchEndAction()
    {
    	//if ((muSlider != null) && muSlider.sliderRectangle.contains(eventX, eventY))
    	//	muSlider.mouseUpTouchEndAction();
    }	
    	
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		
		//public void mousePressed(MouseEvent e)
		public void onMouseDown(MouseDownEvent e)
		{
			//e.preventDefault();
			
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDown = true;
			
			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		//public void mouseDragged(MouseEvent e)
		public void onMouseMove(MouseMoveEvent e)	
		{
			//e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
//System.out.println("mouse move veld");			
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();
			
//System.out.println("sp = " + shiftPressed);

			mouseMoveTouchMoveAction(eventX, eventY);
			
			
			
		} // onMouseMove
		
		//public void mouseReleased(MouseEvent e)
		public void onMouseUp(MouseUpEvent e)	
		{
			//e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			mouseUpTouchEndAction();

		}

	} //MLMML


	// tablet, dwo 
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				
				int eventX = touch.getPageX() - nvCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - nvCanvas.getAbsoluteTop();				
				
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
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				//boolean shiftPressed = e.isShiftKeyDown();

			    int eventX = touch.getPageX() - nvCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - nvCanvas.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			mouseUpTouchEndAction();
		}

	}

    class PushClickHandler implements ClickHandler
    {
    	//public void onMouseDown(MouseDownEvent e)
    	public void onClick(ClickEvent e)
    	{
    		
    		//if (touchStart)
    		//	return;
    		
			//e.preventDefault();
			e.stopPropagation();
    		
    		if (e.getSource() == kijkNaButton)
    		{
    			 kijkNa();   			
    		}

    	}	
    }	
}