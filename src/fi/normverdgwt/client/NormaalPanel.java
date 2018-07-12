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

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.BlurEvent;

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

/**
 * klasse die het normale verdeling applet implementeerd; de klasse
 * bestaat uit: <br>
 * een Canvas waarop de distributiefunctie van de normale verdeling
 * getekend wordt met de oppervlakte die correspondeert met de kans
 * (links van een grens, rechts van een grens of tussen twee grenzen) ingekleurd;<br>
 * NB: alle componenten hierbeneden worden toegevoegd aan het LayoutPanel
 * onder het Canvas en liggen dan op het Canvas;<br>
 * de normale verdeling heeft 4 parameters, wanneer er 3 gegeven zijn kan de
 * vierde berekend worden; <br>
 * rechtsboven een radioButton groep waarmee gekozen kan worden welke parameter
 * berekend moet worden (instelbaar) 
 * linksboven componenten die de 4 parameters weergeven en/of waarmee de waarden van
 * de paramaters die niet berekend worden veranderd kunnen worden (instelbaar);
 * parameters veranderen gaat vie een TextBox of met een slider (aanwezigheid slider
 * is instelbaar); merk op dat de slider voor de grens (de dubbele slider voor twee
 * grenzen) op de x-as in de figuur ligt<br>
 * onder de tekening van de distributiefunctie bevindt zich (instelbaar) een
 * radioButton groep waarmee gekozen wordt of de kans berekend wordt als de kans
 * links van een grens, de kans rechts van een grens of de kans tussen twee grenzen.  
 * @author huub
 */

public class NormaalPanel extends LayoutPanel

{	
	/**
	 * te gebruiken font
	 */
	String fontString = "12px sans-serif";
	
	/**
	 * layout constanten
	 */
	int offSet = 10;
	int cHeight, cHeight1, cHeight2;
	/**
	 * breedte en hoogte van dit NormaalPanel
	 */
	int breedte, hoogte;

	/**
	 * de groep met kanskeuzes (nodig voor de radioButtons)
	 */
	String kansGroup = "kansGroup";
	/**
	 * toon/bereken de kans links van een grens
	 */
	RadioButton linksButton;
	/**
	 * toon/bereken de kans rechts van een grens
	 */
	RadioButton rechtsButton;
	/**
	 * toon/bereken de kans tussen twee grenzen
	 */
	RadioButton tweeGrenzenButton;
	
	/**
	 * label boven het brekengedeelte rechtsboven
	 */
	Label berekenLabel;
	
	/**
	 * de groep met berekenkeuzes (nodig voor de radioButtons)
	 */
	String berekenGroup = "berekenGroup";
	/**
	 * bereken mu
	 */
	RadioButton muButton;
	/**
	 * bereken sigma
	 */
	RadioButton sigmaButton;
	/**
	 * bereken de grens
	 */
	RadioButton grensButton;
	/**
	 * bereken de linker grens
	 */
	RadioButton grensLinksButton;
	/**
	 * bereken de rechter grens
	 */
	RadioButton grensRechtsButton;
	/**
	 * bereken de kans
	 */
	RadioButton kansButton;

	/**
	 * label mu = 
	 */
	Label muLabel;
	/**
	 * slider voor mu
	 */
	Slider muSlider;
	/**
	 * TextBox om mu te veranderen
	 */
	TextBox muTextField;
	/**
	 * label voor de waarde van mu als mu berekend wordt
	 */
	Label muWaardeLabel;

	/**
	 * label sigma = 
	 */
	Label sigmaLabel;
	/**
	 * slider voor sigma
	 */
	Slider sigmaSlider;
	/**
	 * TextBox om sigma te veranderen
	 */
	TextBox sigmaTextField;
	/**
	 * label voor de waarde van sigma als sigma berekend wordt
	 */
	Label sigmaWaardeLabel;

	/**
	 * label grens = 
	 */
	Label grensLabel;
	/**
	 * slider voor de grens
	 */
	Slider grensSlider;
	/**
	 * TextBox om de grens te veranderen
	 */
	TextBox grensTextField;
	/**
	 * label voor de waarde van de grenss als de grens berekend wordt
	 */
	Label grensWaardeLabel;

	/**
	 * label grens links = 
	 */
	Label grensLinksLabel;
	/**
	 * TextBox om grens links te veranderen
	 */
	TextBox grensLinksTextField;
	/**
	 * label voor de waarde van de grens links als de grens links berekend wordt
	 */
	Label grensLinksWaardeLabel;

	/**
	 * label grens rechts = 
	 */
	Label grensRechtsLabel;
	/**
	 * TextBox om grens rechts te veranderen
	 */
	TextBox grensRechtsTextField;
	/**
	 * label voor de waarde van de grens rechts als de grens rechts berekend wordt
	 */
	Label grensRechtsWaardeLabel;
	
	/**
	 * dubbel-slider voor grens links en grens rechts
	 */
	DoubleSlider tweeGrenzenSlider;

	/**
	 * label kans = 
	 */
	Label kansLabel;
	/**
	 * slider voor de kans
	 */
	Slider kansSlider;	
	/**
	 * TextBox om de kans te veranderen
	 */
	TextBox kansTextField;
	/**
	 * label voor de waarde van de kans als de kans berekend wordt
	 */
	Label kansWaardeLabel;

	/**
	 * label voor mu met de waarde van mu voor onder de grafiek 
	 */
	Label muMetWaardeLabel;

	/**
	 * constanten voor de kanskeuzes
	 */
	static final int KANSLINKS = 0;
	static final int KANSRECHTS = 1;
	static final int TWEEGRENZEN = 2;
	/**
	 * de actuele kanskeuze
	 */
	int kansKeuze = KANSLINKS;
	
	/**
	 * constanten voor de berekenkeuzes
	 */
	static final int BEREKENMU = 0;
	static final int BEREKENSIGMA = 1;
	static final int BEREKENGRENS = 2;
	static final int BEREKENKANS = 3;
	static final int BEREKENGRENSLINKS = 4;
	static final int BEREKENGRENSRECHTS = 5;
	/**
	 * de actuele berekenkeuze
	 */
	int berekenKeuze = BEREKENKANS;
	/**
	 * de berekenkeuze tijdelijk onthouden
	 */
	int oldBerekenKeuze;

	/**
	 * grafiek-grenzen in pixels, zie methode plaatsComponenten
	 */
	int xMin, xMax, yMin, yMax;

	/**
	 * grafiek-grenzen als reele waarden voor de standaardnormale verdeling
	 * worden nooit veranderd 
	 */
	double minX = -45e-1d;
	double maxX = 45e-1d;
	double minY = 0;
	double maxY = 8e-1d;
	
	/**
	 * reele grafiekgrenzen voor niet-standaardnormaal
	 * worden bij tekenen gezet op minX+mu en maxX+mu
	 */
	double minMuX = minX;
	double maxMuX = maxX;
	
	/**
	 * (default) waarde mu
	 */
	double mu = 0;
	/**
	 * aantal decimalem weergave mu
	 */
	int muDecimals = 2;
	/**
	 * waarde mu als String
	 */
	String muString = "";
	/**
	 * minimum waarde mu
	 */
	double muMin = -10000;
	/**
	 * maximum waarde mu
	 */
	double muMax = 10000;
	/**
	 * minimum mu-slider
	 */
	double muSliderMin = mu - 1;
	/**
	 * maximum mu-slider
	 */
	double muSliderMax = mu + 1;

	/**
	 * (default) waarde sigma
	 */
	double sigma = 1;
	/**
	 * aantal decimalem weergave sigma
	 */
	int sigmaDecimals = 1;
	/**
	 * minimum waarde sigma
	 */
	double sigmaMin = 1e-2d;
	/**
	 * maximum waarde sigma
	 */
	double sigmaMax = 500;
	/**
	 * minimum sigma-slider
	 */
	double sigmaSliderMin = sigmaMin;
	/**
	 * maximum sigma-slider
	 */
	double sigmaSliderMax = sigma + 5e-1d;
	
	/**
	 * (default) waarde grens 
	 */
	double grens = mu + 1;
	/**
	 * (default) waarde grens links 
	 */
	double grensLinks = mu - 1;
	/**
	 * (default) waarde grens rechts 
	 */
	double grensRechts = mu + 1;
	/**
	 * aantal decimalem weergave grenzen
	 */
	int grensDecimals = 2;
	
	/**
	 * (default) waarde kans 
	 */
	double kans = 841e-3d;
	/**
	 * aantal decimalem weergave kans
	 */
	int kansDecimals = 3;

	/**
	 * een hele kleine double
	 */
	static final double NZERO = 1e-5d;

	/**
	 * voorgedefinieerde kleuren
	 */
	static CssColor lightBlue = CssColor.make(198, 239, 247);
	static CssColor veryLightBlue = CssColor.make(220, 239, 247);
	static CssColor veryLightPurperBlue = CssColor.make(234, 229, 255);
	static CssColor pinkRed = CssColor.make(203, 14, 113);	
    static CssColor lightRed = CssColor.make(255, 99, 66);
    static CssColor lightGray = CssColor.make(192, 192, 192);
    static CssColor gray = CssColor.make(149, 149, 149);
    
    /**
     * kleur voor oppervlak onder distributiefunctie corresponderend met de kans
     */
    CssColor areaColor = veryLightPurperBlue;
    /**
     * kleur voor het kans label
     */
    CssColor kansColor = pinkRed;
    /**
     * kleur voor de mu lijn
     */
    CssColor muLineColor = lightGray;
    /**
     * kleur voor de sigma lijn
     */
    CssColor sigmaLineColor = gray;    

    /**
     * Context2d van nvCanvas
     */
    Context2d nvContext2d;
    /**
     * Canvas om op te tekenen
     */
    Canvas nvCanvas;

    /**
     * moeten de grenslabels in de grafiek wat lager getekend worden?
     */
	boolean lowerGrensLabels = false;
    /**
     * moeten de labels van de linker grens in de grafiek wat lager getekend worden?
     */
	boolean lowerGrensLinksLabels = false;
    /**
     * moeten de labels van de rechter grens in de grafiek wat lager getekend worden?
     */
	boolean lowerGrensRechtsLabels = false;

	/**
	 * parametrisatie: kan kans links gekozen worden? 
	 */
	boolean kansLinksOptie = true;
	/**
	 * parametrisatie: kan kans rechts gekozen worden? 
	 */
	boolean kansRechtsOptie = true;
	/**
	 * parametrisatie: kan een kans tussen twee grenzen gekozen worden?
	 */
	boolean tweeGrenzenOptie = true;
	
	/**
	 * parametrisatie: is mu berekenbaar?
	 */
	boolean muBerekenbaarOptie = false;
	/**
	 * parametrisatie: mu is berekenbaar en mu heeft geen vaste waarde
	 */
	boolean actualMuBerekenbaarOptie = false;
	
	/**
	 * parametrisatie: is sigma berekenbaar?
	 */
	boolean sigmaBerekenbaarOptie = false;
	/**
	 * parametrisatie: sigma is berekenbaar en sigma heeft geen vaste waarde
	 */
	boolean actualSigmaBerekenbaarOptie = false;
	
	/**
	 * parametrisatie: heeft mu een vaste waarde?
	 */
	boolean muVastOptie = false;
	/**
	 * parametrisatie: heeft sigma een vaste waarde?
	 */
	boolean sigmaVastOptie = false;
	
	/**
	 * parametrisatie: heeft mu een slider?
	 */
	boolean muSliderOptie = false;
	/**
	 * parametrisatie: heeft sigma een slider?
	 */
	boolean sigmaSliderOptie = false;
	/**
	 * parametrisatie: heeft de grens (hebben de grenzen) een slider (een double slider)?
	 */
	boolean grensSliderOptie = true;
	/**
	 * parametrisatie: heeft de kans een slider?
	 */
	boolean kansSliderOptie = false;
	
	/**
	 * parametrisatie: is de waarde van mu linksboven zichtbaar?
	 */
	boolean muZichtbaarOptie = true;
	/**
	 * parametrisatie: is de waarde van sigma linksboven zichtbaar?
	 */
	boolean sigmaZichtbaarOptie = true;
	/**
	 * parametrisatie: is (zijn) de waarde(n) van de grens (grenzen) linksboven zichtbaar?
	 */
	boolean grensZichtbaarOptie = true;
	/**
	 * parametrisatie: is de waarde van de kand linksboven zichtbaar?
	 */
	boolean kansZichtbaarOptie = true;
	
	/**
	 * parametrisatie: is de waarde van mu zichtbaar in de figuur?
	 */
	boolean muZichtbaarFigOptie = true;
	/**
	 * parametrisatie: is de waarde van sigma zichtbaar in de figuur?
	 */
	boolean sigmaZichtbaarFigOptie = true;
	/**
	 * parametrisatie: is de waarde van de grens (grenzen) zichtbaar in de figuur?
	 */
	boolean grensZichtbaarFigOptie = true;
	/**
	 * parametrisatie: is de waarde van de kans zichtbaar in de figuur?
	 */
	boolean kansZichtbaarFigOptie = true;

	/**
	 * parametrisatie: is de bereken button groep rechtsboven zichtbaar?
	 */
	boolean berekenbaarZichtbaar = true;

	/**
	 * parametrisatie: nakijken?
	 */
	boolean kijkOpdrachtNa = false;

	/**
	 * parametrisatie: mu nakijken?
	 */
	boolean kijkMuNa;
	/**
	 * nakijken: antwoord voor mu
	 */
	double antwoordMu;
	/**
	 * parametrisatie: sigma nakijken?
	 */
	boolean kijkSigmaNa;
	/**
	 * nakijken: antwoord voor sigma
	 */
	double antwoordSigma;
	/**
	 * parametrisatie: grens nakijken?
	 */
	boolean kijkGrensNa;
	/**
	 * nakijken: antwoord voor de grens
	 */
	double antwoordGrens;
	/**
	 * parametrisatie: grens links nakijken?
	 */
	boolean kijkGrensLinksNa;
	/**
	 * nakijken: antwoord voor grens links
	 */
	double antwoordGrensLinks;
	/**
	 * parametrisatie: grens rechts nakijken?
	 */
	boolean kijkGrensRechtsNa;
	/**
	 * nakijken: antwoord voor grens rechts
	 */
	double antwoordGrensRechts;
	/**
	 * parametrisatie: kans nakijken?
	 */
	boolean kijkKansNa;
	/**
	 * nakijken: antwoord voor de kans
	 */
	double antwoordKans;
	
	int maxScore;	
	int score;
	
	/**
	 * nakijk knop
	 */
	PushButton kijkNaButton;
	/**
	 * panel voor nalijk knop
	 */
	LayoutPanel kijkNaPanel;
	
	/**
	 * eigenaar van dit normaalPanel
	 */
	NormVerdGWT owner;
	
	/**
	 * handling Mouse Events on the Canvas
	 */
	boolean mouseDown = false;
	
	/**
	 * breedtes voor kansopties radioButtons
	 */
	int rechtsButtonWidth, linksButtonWidth, tweeGrenzenButtonWidth;
	/**
	 * breedtes voor berekenopties radioButtons
	 */
	int berekenLabelWidth, muButtonWidth, sigmaButtonWidth, grensButtonWidth, 
		grensLinksButtonWidth, grensRechtsButtonWidth, kansButtonWidth;
	/**
	 * breedtes voor parameter Labels, parametrs TextBoxes, parameterwaarde Labels
	 */
	int muLabelWidth, muTextFieldWidth, muWaardeLabelWidth, sigmaLabelWidth, sigmaTextFieldWidth, sigmaWaardeLabelWidth, 
		grensLabelWidth, grensTextFieldWidth, grensWaardeLabelWidth, 
		kansLabelWidth, kansTextFieldWidth, kansWaardeLabelWidth,
		grensLinksLabelWidth, grensLinksTextFieldWidth, grensLinksWaardeLabelWidth,
		grensRechtsLabelWidth, grensRechtsTextFieldWidth, grensRechtsWaardeLabelWidth;
	
	/**
	 * rechthoek (voor paint) rondom het berekengedeelte rechtsboven
	 */
	Rectangle berekenRect;
	/**
	 * rechthoeken (voor paint) rondom de parameters linksboven
	 */
	Rectangle muRect, sigmaRect, grensRect, kansRect, grensLinksRect, grensRechtsRect;
	
	/**
	 * breedte parameter-deel linksboven
	 */
	int leftWidth = 200;
	
	/**
	 * constructor: creeer en voeg het Canvas toe, voeg
	 * Mouse en Touch Handlers toe aan het Canavs en creeer
	 * alle GUI-componenten; voeg die toe en zet die op hun plaats
	 * in methode plaatsComponenten 
	 * @param o eigenaar van dit NormaalPanel
	 * @param w breedte
	 * @param h hoogte
	 */
	public NormaalPanel(NormVerdGWT o, int w, int h)
	{	
		owner = o;

		// Canvas
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
		
		// Mouse en Touch Events
		MouseHandler mouseHandler = new MouseHandler();
		nvCanvas.addMouseDownHandler(mouseHandler);
		nvCanvas.addMouseMoveHandler(mouseHandler);
		nvCanvas.addMouseUpHandler(mouseHandler);
		
		TouchHandler touchHandler = new TouchHandler();
		nvCanvas.addTouchStartHandler(touchHandler);
		nvCanvas.addTouchMoveHandler(touchHandler);
		nvCanvas.addTouchEndHandler(touchHandler);

		
		cHeight = 20;
		cHeight1 = cHeight + offSet;
		cHeight2 = cHeight + offSet / 2;
		
		int width = 0;
		TextMetrics tm;

		// kanskeuze groep
		rechtsButton = new RadioButton(kansGroup, NormVerdGWT.rb.kansRechtsTekst());
		tm = nvContext2d.measureText(NormVerdGWT.rb.kansRechtsTekst());
		width = (int) Math.round(tm.getWidth());
		rechtsButtonWidth = 2 * width;
		
		linksButton = new RadioButton(kansGroup, NormVerdGWT.rb.kansLinksTekst());
		tm = nvContext2d.measureText(NormVerdGWT.rb.kansLinksTekst());
		width = (int) Math.round(tm.getWidth());
		linksButtonWidth = 2 * width;					 
		
		tweeGrenzenButton = new RadioButton(kansGroup, NormVerdGWT.rb.tweeGrenzenTekst());
		tm = nvContext2d.measureText(NormVerdGWT.rb.tweeGrenzenTekst());
		width = (int) Math.round(tm.getWidth());
		tweeGrenzenButtonWidth = 2 * width;					 
		
		linksButton.addStyleName(NormVerdGWT.normVerdGWTCss.radiobutton());
		rechtsButton.addStyleName(NormVerdGWT.normVerdGWTCss.radiobutton());
		tweeGrenzenButton.addStyleName(NormVerdGWT.normVerdGWTCss.radiobutton());

		// berekenkeuze groep

		tm = nvContext2d.measureText(NormVerdGWT.rb.berekenTekst());
		width = (int) Math.round(tm.getWidth());
		berekenLabelWidth = 2 * width;
		berekenLabel = new Label(NormVerdGWT.rb.berekenTekst());
		
		tm = nvContext2d.measureText("m");
		width = (int) Math.round(tm.getWidth());
		muButtonWidth = 2 * width + 20;
		muButton = new RadioButton(berekenGroup, "\u03BC"); // "\u03BC";
		
		tm = nvContext2d.measureText("s");
		width = (int) Math.round(tm.getWidth());
		sigmaButtonWidth = 2 * width + 20;
		sigmaButton = new RadioButton(berekenGroup, "\u03C3"); // "\u03C3"
		
		tm = nvContext2d.measureText(NormVerdGWT.rb.grensTekst());
		width = (int) Math.round(tm.getWidth());
		grensButtonWidth = 2 * width + 20;
		grensButton = new RadioButton(berekenGroup, NormVerdGWT.rb.grensTekst());

		tm = nvContext2d.measureText(NormVerdGWT.rb.kansTekst());
		width = (int) Math.round(tm.getWidth());
		kansButtonWidth = 2 * width + 20;
		kansButton = new RadioButton(berekenGroup, NormVerdGWT.rb.kansTekst());
		
		tm = nvContext2d.measureText(NormVerdGWT.rb.grensLinksTekst());
		width = (int) Math.round(tm.getWidth());
		grensLinksButtonWidth = 2 * width + 20;
		grensLinksButton = new RadioButton(berekenGroup, NormVerdGWT.rb.grensLinksTekst());
		
		tm = nvContext2d.measureText(NormVerdGWT.rb.grensRechtsTekst());
		width = (int) Math.round(tm.getWidth());
		grensRechtsButtonWidth = 2 * width + 20;
		grensRechtsButton = new RadioButton(berekenGroup, NormVerdGWT.rb.grensRechtsTekst());
		
		berekenLabel.addStyleName(NormVerdGWT.normVerdGWTCss.label());
		muButton.addStyleName(NormVerdGWT.normVerdGWTCss.radiobutton());
		sigmaButton.addStyleName(NormVerdGWT.normVerdGWTCss.radiobutton());
		grensButton.addStyleName(NormVerdGWT.normVerdGWTCss.radiobutton());
		grensLinksButton.addStyleName(NormVerdGWT.normVerdGWTCss.radiobutton());
		grensRechtsButton.addStyleName(NormVerdGWT.normVerdGWTCss.radiobutton());
		kansButton.addStyleName(NormVerdGWT.normVerdGWTCss.radiobutton());

		// ad hoc		
		int height = 3 * 20 / 2; 
		tm = nvContext2d.measureText("m = ");
		width = (int) Math.round(tm.getWidth());
		muLabelWidth = width + 5;
		muLabel = new Label("\u03BC" + " = "); // "\u03BC"
		
		tm = nvContext2d.measureText("XXXXX");
		width = (int) Math.round(tm.getWidth());
		muTextFieldWidth = width + 15;
		muTextField = new TextBox();
		
		muWaardeLabel = new Label("");
		
		muSlider = new Slider(this, 150, 75, 0, 0, nvContext2d, "mu");
		muSlider.zetEnabled(false);

		tm = nvContext2d.measureText("m = ");
		width = (int) Math.round(tm.getWidth());
		sigmaLabelWidth = width + 5;
		sigmaLabel = new Label("\u03C3" + " = "); // "\u03C3"
		
		tm = nvContext2d.measureText("XXXXX");
		width = (int) Math.round(tm.getWidth());
		sigmaTextFieldWidth = width + 25;
		sigmaTextField = new TextBox();

		sigmaWaardeLabel = new Label("");

		sigmaSlider = new Slider(this, 150, 75, 0, 0, nvContext2d, "sigma");
		sigmaSlider.zetEnabled(false);

		tm = nvContext2d.measureText(NormVerdGWT.rb.grensGTekst() + " = ");
		width = (int) Math.round(tm.getWidth());
		grensLabelWidth = width + 20;
		grensLabel = new Label(NormVerdGWT.rb.grensGTekst() + " = ");
		
		tm = nvContext2d.measureText("XXXXXX");
		width = (int) Math.round(tm.getWidth());
		grensTextFieldWidth = width + 25;
		grensTextField = new TextBox();
		
		grensWaardeLabel = new Label("");
		
		grensSlider = new Slider(this, 100, 50, 0, 0, nvContext2d, "grens");
		grensSlider.zetShowLine(false);
		grensSlider.zetEnabled(false);

		tm = nvContext2d.measureText(NormVerdGWT.rb.grensLinksLTekst() + " = ");
		width = (int) Math.round(tm.getWidth());
		grensLinksLabelWidth = width + 20;
		grensLinksLabel = new Label(NormVerdGWT.rb.grensLinksLTekst() + " = ");

		tm = nvContext2d.measureText("XXXXXX");
		width = (int) Math.round(tm.getWidth());
		grensLinksTextFieldWidth = width + 25;
		grensLinksTextField = new TextBox();
		
		grensLinksWaardeLabel = new Label("");
		
		tm = nvContext2d.measureText(NormVerdGWT.rb.grensRechtsRTekst() + " = ");
		width = (int) Math.round(tm.getWidth());
		grensRechtsLabelWidth = width + 20;
		grensRechtsLabel = new Label(NormVerdGWT.rb.grensRechtsRTekst() + " = ");

		tm = nvContext2d.measureText("XXXXXX");
		width = (int) Math.round(tm.getWidth());
		grensRechtsTextFieldWidth = width + 25;
		grensRechtsTextField = new TextBox();

		grensRechtsWaardeLabel = new Label("");
		
		tweeGrenzenSlider = new DoubleSlider(this, 100, 30, 70, 0, 0, nvContext2d);
		tweeGrenzenSlider.zetShowLine(false);
		tweeGrenzenSlider.zetLinksEnabled(false);
		tweeGrenzenSlider.zetRechtsEnabled(false);
		
		tm = nvContext2d.measureText(NormVerdGWT.rb.kansTekst() + " = ");
		width = (int) Math.round(tm.getWidth());
		kansLabelWidth = width + 5;
		kansLabel = new Label(NormVerdGWT.rb.kansTekst() + " = ");
		
		tm = nvContext2d.measureText("XXXXXX");
		width = (int) Math.round(tm.getWidth());
		kansTextFieldWidth = width + 25;
		kansTextField = new TextBox();

		kansWaardeLabel = new Label("");
		
		kansSlider = new Slider(this, 150, 75, 0,0, nvContext2d, "kans");
		kansSlider.zetEnabled(false);
		
		muWaardeLabel.addStyleName(NormVerdGWT.normVerdGWTCss.label());
		sigmaWaardeLabel.addStyleName(NormVerdGWT.normVerdGWTCss.label());
		grensWaardeLabel.addStyleName(NormVerdGWT.normVerdGWTCss.label());
		grensLinksWaardeLabel.addStyleName(NormVerdGWT.normVerdGWTCss.label());
		grensRechtsWaardeLabel.addStyleName(NormVerdGWT.normVerdGWTCss.label());
		kansWaardeLabel.addStyleName(NormVerdGWT.normVerdGWTCss.label());
		
		muMetWaardeLabel = new Label("mu" + " = XXXX");
		
		zetMu(mu, false, false);
		
		zetSigma(sigma, false, false);

		zetGrens(grens, false);		
		zetGrensLinks(grensLinks, false);				
		zetGrensRechts(grensRechts, false);		
						
		zetKans(kans, false);
		
// dit gaat niet goed hier, zie methode init		
		//zetKansKeuze(true);
		//zetBerekenKeuze(true);
		//bereken();
// einde dit gaat niet goed hier	
	
		//kijkNa-gebeuren		
		kijkNaButton = new PushButton(NormVerdGWT.rb.kijkNaTekst());
		kijkNaButton.addStyleName(NormVerdGWT.normVerdGWTCss.pushbutton());
		kijkNaPanel = new LayoutPanel();
		
//dit apart extern aanroepen  		
		//plaatsComponenten();

	}
	
	/**
	 * eerste berekeningen, roep aan na 
	 * methode plaatsComponenten
	 */
	public void init()
	{
		zetKansKeuze(true);
		zetBerekenKeuze(true);
		bereken();
	}

	/**
	 * creeer een VlalueChangeHandler en voeg die toe aan elk van de
	 * radioButtons in de kanskeuze groe[
	 */
	public void addListeners()
	{
		ValueChangeHandler<Boolean> vch = new KansKeuzeVCH();
		linksButton.addValueChangeHandler(vch);
		rechtsButton.addValueChangeHandler(vch);
		tweeGrenzenButton.addValueChangeHandler(vch);
	}
	
	/**
	 * getter voor het Canvas van dit NormaalPanel
	 * @return nvCanvas
	 */
	public Canvas getCanvas()
	{
		return nvCanvas;
	}
	
	
	/**
	 * voeg de GUI-componenten toe (alleen als init == true) en zet ze op hun
	 * plaats (altijd); houdt rekening met de instellingen
	 * @param init true/false
	 */
	public void plaatsComponenten(boolean init)
	{
		// kans groep
		// keuze uit 3 opties
		if (kansLinksOptie && kansRechtsOptie && tweeGrenzenOptie)
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
			if (init)
			{	add(tweeGrenzenButton);
				tweeGrenzenButton.addValueChangeHandler(new KansKeuzeVCH());
			}
			setWidgetLeftWidth(tweeGrenzenButton, offSet, Style.Unit.PX, tweeGrenzenButtonWidth, Style.Unit.PX);
			setWidgetTopHeight(tweeGrenzenButton, hoogte - cHeight, Style.Unit.PX, cHeight, Style.Unit.PX);
			
		}
		// keuze uit 2 opties
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
		// 1 kansoptie: nothing to do
			
		int yPos = 3;

		// berekenkeuze groep
		if (berekenbaarZichtbaar)
		{	
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
				setWidgetVisible(muButton, true);
				setWidgetLeftWidth(muButton, breedte - grensRechtsButtonWidth + 25, Style.Unit.PX, muButtonWidth, Style.Unit.PX);
				setWidgetTopHeight(muButton, yPos, Style.Unit.PX, cHeight, Style.Unit.PX);
			
				yPos += cHeight;						 							     
			}
			else 
			{	
				setWidgetVisible(muButton, false);
			}
			
			
			if (init)
			{	add(sigmaButton);
				sigmaButton.addValueChangeHandler(new BerekenKeuzeVCH());
			}
			if (actualSigmaBerekenbaarOptie)
			{	
				setWidgetVisible(sigmaButton, true);
				setWidgetLeftWidth(sigmaButton, breedte - grensRechtsButtonWidth + 25, Style.Unit.PX, sigmaButtonWidth, Style.Unit.PX);
				setWidgetTopHeight(sigmaButton, yPos, Style.Unit.PX, cHeight, Style.Unit.PX);
			
				yPos += cHeight;
			}
			else
			{	
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
				
				setWidgetLeftWidth(grensLinksButton, breedte - grensRechtsButtonWidth + 25, Style.Unit.PX, grensLinksButtonWidth, Style.Unit.PX);
				setWidgetTopHeight(grensLinksButton, yPos, Style.Unit.PX, cHeight, Style.Unit.PX);
			
				yPos += cHeight;			    

				setWidgetLeftWidth(grensRechtsButton, breedte - grensRechtsButtonWidth + 25, Style.Unit.PX, grensRechtsButtonWidth, Style.Unit.PX);
				setWidgetTopHeight(grensRechtsButton, yPos, Style.Unit.PX, cHeight, Style.Unit.PX);
				
				yPos += cHeight;			                 						                  
			}	
			else // een grens
			{
				setWidgetVisible(grensButton, true);
				setWidgetVisible(grensLinksButton, false);
				setWidgetVisible(grensRechtsButton, false);				

				setWidgetLeftWidth(grensButton, breedte - grensRechtsButtonWidth + 25, Style.Unit.PX, grensButtonWidth, Style.Unit.PX);
				setWidgetTopHeight(grensButton, yPos, Style.Unit.PX, cHeight, Style.Unit.PX);
				
				
				yPos += cHeight;			                 						                  						            
			}
			
			if (init)
			{	add(kansButton);
				kansButton.addValueChangeHandler(new BerekenKeuzeVCH());
			}
		    setWidgetLeftWidth(kansButton, breedte - grensRechtsButtonWidth + 25, Style.Unit.PX, kansButtonWidth, Style.Unit.PX);
			setWidgetTopHeight(kansButton, yPos, Style.Unit.PX, cHeight, Style.Unit.PX);
		    
		    yPos += cHeight;	
		    
		    berekenRect = new Rectangle(breedte - grensRechtsButtonWidth + 15, 0, grensRechtsButtonWidth - 15, yPos + 10); 


		}

		// nakijken
		if (kijkOpdrachtNa && (kijkNaPanel != null))
		{
			if (init)
			{
				add(kijkNaPanel);
				kijkNaPanel.add(kijkNaButton);
				kijkNaPanel.add(owner.goedKrulImage);
				kijkNaPanel.add(owner.foutKruisImage);
				kijkNaButton.addClickHandler(new PushClickHandler());
			}
			int y = 50;
			if (berekenbaarZichtbaar)
				y = 120;
			
			setWidgetLeftWidth(kijkNaPanel, breedte - 120, Style.Unit.PX, 120, Style.Unit.PX);
			setWidgetTopHeight(kijkNaPanel, y, Style.Unit.PX, cHeight2, Style.Unit.PX);
			
			kijkNaPanel.setWidgetLeftWidth(kijkNaButton, 0, Style.Unit.PX, 80, Style.Unit.PX);
			kijkNaPanel.setWidgetTopHeight(kijkNaButton, 0, Style.Unit.PX, cHeight2, Style.Unit.PX);

			kijkNaPanel.setWidgetLeftWidth(owner.goedKrulImage, 80, Style.Unit.PX, 30, Style.Unit.PX);
			kijkNaPanel.setWidgetTopHeight(owner.goedKrulImage, 0, Style.Unit.PX, cHeight2, Style.Unit.PX);

			kijkNaPanel.setWidgetLeftWidth(owner.foutKruisImage, 80, Style.Unit.PX, 30, Style.Unit.PX);
			kijkNaPanel.setWidgetTopHeight(owner.foutKruisImage, 0, Style.Unit.PX, cHeight2, Style.Unit.PX);
			
			kijkNaPanel.setWidgetVisible(owner.goedKrulImage, false);
			kijkNaPanel.setWidgetVisible(owner.foutKruisImage, false);
		}
		
		// parameters
		yPos = 7; 
		
		// mu
		if (init)
		{	add(muLabel);
			add(muTextField);
			muTextField.addKeyDownHandler(new TextBoxKeyDownHandler(muTextField));
			muTextField.addBlurHandler(new TextBoxBlurHandler(muTextField));
			add(muWaardeLabel);
		}
		if (muZichtbaarOptie)
		{
		    setWidgetLeftWidth(muLabel, offSet, Style.Unit.PX, muLabelWidth, Style.Unit.PX);
			setWidgetTopHeight(muLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);
			
		    setWidgetLeftWidth(muTextField, offSet + muLabelWidth, Style.Unit.PX, muTextFieldWidth, Style.Unit.PX);
			setWidgetTopHeight(muTextField, yPos, Style.Unit.PX, cHeight2, Style.Unit.PX);
			
		    setWidgetLeftWidth(muWaardeLabel, offSet + muLabelWidth, Style.Unit.PX, muTextFieldWidth, Style.Unit.PX);
			setWidgetTopHeight(muWaardeLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);

			setWidgetVisible(muLabel, true);
			setWidgetVisible(muTextField, !muVastOptie && !(berekenKeuze == BEREKENMU));
			setWidgetVisible(muWaardeLabel, muVastOptie || (berekenKeuze == BEREKENMU));
			
			yPos += cHeight2;
			
			muRect = new Rectangle(0, yPos - cHeight2 - 4, leftWidth, cHeight2 + 8);
			
			if ((berekenKeuze != BEREKENMU) && !muVastOptie && muSliderOptie)
			{	
				muSlider.setLocation(offSet, yPos + 3);
				muSlider.zetEnabled(true);
				yPos += muSlider.hoogte - 1;
				
				muRect = new Rectangle(0, yPos - muSlider.hoogte + 5 - cHeight2 - 4, leftWidth, 
							           cHeight2 + 8 + muSlider.hoogte - 5); // -5
			}	
			else
				muSlider.zetEnabled(false);
			
			yPos += 10;
		}
		else
		{
			setWidgetVisible(muLabel, false);
			setWidgetVisible(muTextField, false);
			setWidgetVisible(muWaardeLabel, false);
			
		}

		// sigma
		if (init)
		{	add(sigmaLabel);
			add(sigmaTextField);
			sigmaTextField.addKeyDownHandler(new TextBoxKeyDownHandler(sigmaTextField));
			sigmaTextField.addBlurHandler(new TextBoxBlurHandler(sigmaTextField));
			add(sigmaWaardeLabel);
		}
		if (sigmaZichtbaarOptie)
		{	
		    setWidgetLeftWidth(sigmaLabel, offSet, Style.Unit.PX, sigmaLabelWidth, Style.Unit.PX);
			setWidgetTopHeight(sigmaLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);
			
		    setWidgetLeftWidth(sigmaTextField, offSet + sigmaLabelWidth, Style.Unit.PX, sigmaTextFieldWidth, Style.Unit.PX);
			setWidgetTopHeight(sigmaTextField, yPos, Style.Unit.PX, cHeight2, Style.Unit.PX);
			
		    setWidgetLeftWidth(sigmaWaardeLabel, offSet + sigmaLabelWidth, Style.Unit.PX, sigmaTextFieldWidth, Style.Unit.PX);
			setWidgetTopHeight(sigmaWaardeLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);
			
			setWidgetVisible(sigmaLabel, true);
			setWidgetVisible(sigmaTextField, !sigmaVastOptie && !(berekenKeuze == BEREKENSIGMA));
			setWidgetVisible(sigmaWaardeLabel, sigmaVastOptie || (berekenKeuze == BEREKENSIGMA));
			
			yPos += cHeight2;
			
			sigmaRect = new Rectangle(0, yPos - cHeight2 - 4, leftWidth, cHeight2 + 8);
			
			if ((berekenKeuze != BEREKENSIGMA) && !sigmaVastOptie && sigmaSliderOptie)
			{	
				sigmaSlider.setLocation(offSet, yPos + 3);
				sigmaSlider.zetEnabled(true);
				yPos += sigmaSlider.hoogte-1;
				
				sigmaRect = new Rectangle(0, yPos - sigmaSlider.hoogte + 5 - cHeight2 - 4, leftWidth, 
				                          cHeight2 + 8 + sigmaSlider.hoogte - 5);
			}
			else
				sigmaSlider.zetEnabled(false);
			
			yPos += 10;
		}
		else
		{
			setWidgetVisible(sigmaLabel, false);
			setWidgetVisible(sigmaTextField, false);
			setWidgetVisible(sigmaWaardeLabel, false);
			
		}
		

		// grens (grenzen)
		if (init)
		{	add(grensLabel);
			add(grensTextField);
			grensTextField.addKeyDownHandler(new TextBoxKeyDownHandler(grensTextField));
			grensTextField.addBlurHandler(new TextBoxBlurHandler(grensTextField));
			add(grensWaardeLabel);
			add(grensLinksLabel);
			add(grensLinksTextField);
			grensLinksTextField.addKeyDownHandler(new TextBoxKeyDownHandler(grensLinksTextField));
			grensLinksTextField.addBlurHandler(new TextBoxBlurHandler(grensLinksTextField));
			add(grensLinksWaardeLabel);
			add(grensRechtsLabel);
			add(grensRechtsTextField);
			grensRechtsTextField.addKeyDownHandler(new TextBoxKeyDownHandler(grensRechtsTextField));
			grensRechtsTextField.addBlurHandler(new TextBoxBlurHandler(grensRechtsTextField));
			add(grensRechtsWaardeLabel);

		}
		
		if (grensZichtbaarOptie)
		{	
			
			if (kansKeuze == TWEEGRENZEN)
			{	
			    setWidgetLeftWidth(grensLinksLabel, offSet, Style.Unit.PX, grensLinksLabelWidth, Style.Unit.PX);
				setWidgetTopHeight(grensLinksLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);
				
			    setWidgetLeftWidth(grensLinksTextField, offSet + grensLinksLabelWidth, Style.Unit.PX, grensLinksTextFieldWidth, Style.Unit.PX);
				setWidgetTopHeight(grensLinksTextField, yPos, Style.Unit.PX, cHeight2, Style.Unit.PX);
				
			    setWidgetLeftWidth(grensLinksWaardeLabel, offSet + grensLinksLabelWidth, Style.Unit.PX, grensLinksTextFieldWidth, Style.Unit.PX);
				setWidgetTopHeight(grensLinksWaardeLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);

				grensLinksRect = new Rectangle(0, yPos - 4, leftWidth, cHeight2 + 8);
				
				yPos += cHeight1 + offSet / 2;
				
			    setWidgetLeftWidth(grensRechtsLabel, offSet, Style.Unit.PX, grensRechtsLabelWidth, Style.Unit.PX);
				setWidgetTopHeight(grensRechtsLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);
				
			    setWidgetLeftWidth(grensRechtsTextField, offSet + grensRechtsLabelWidth, Style.Unit.PX, grensRechtsTextFieldWidth, Style.Unit.PX);
				setWidgetTopHeight(grensRechtsTextField, yPos, Style.Unit.PX, cHeight2, Style.Unit.PX);
				
			    setWidgetLeftWidth(grensRechtsWaardeLabel, offSet + grensRechtsLabelWidth, Style.Unit.PX, grensRechtsTextFieldWidth, Style.Unit.PX);
				setWidgetTopHeight(grensRechtsWaardeLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);
				
				yPos += cHeight2;			
				grensRechtsRect = new Rectangle(0, yPos - cHeight2 - 4, leftWidth, cHeight2 + 8);
				
			}
			else // kansKeuze KANSLINKS OF KANSRECHTS
			{
			    setWidgetLeftWidth(grensLabel, offSet, Style.Unit.PX, grensLabelWidth, Style.Unit.PX);
				setWidgetTopHeight(grensLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);

			    setWidgetLeftWidth(grensTextField, offSet + grensLabelWidth, Style.Unit.PX, grensTextFieldWidth, Style.Unit.PX);
				setWidgetTopHeight(grensTextField, yPos, Style.Unit.PX, cHeight2, Style.Unit.PX);
				
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
			
			if (kansKeuze == TWEEGRENZEN)
			{	
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
				if (grensSliderOptie && (berekenKeuze != BEREKENGRENS))
					grensSlider.zetEnabled(true);
				else 
					grensSlider.zetEnabled(false);
					
				tweeGrenzenSlider.zetLinksEnabled(false);
				tweeGrenzenSlider.zetRechtsEnabled(false);
				
			}
		}
		
		// kans
		if (init)
		{	add(kansLabel);
			add(kansTextField);
			kansTextField.addKeyDownHandler(new TextBoxKeyDownHandler(kansTextField));
			kansTextField.addBlurHandler(new TextBoxBlurHandler(kansTextField));
			add(kansWaardeLabel);
		}
		
		if (kansZichtbaarOptie)
		{	
			// kansLabel kan meerdere opschriften hebben
			TextMetrics tm = nvContext2d.measureText(kansLabel.getText());
			int width = (int) Math.round(tm.getWidth());
			kansLabelWidth = width + 10;

		    setWidgetLeftWidth(kansLabel, offSet, Style.Unit.PX, kansLabelWidth, Style.Unit.PX);
			setWidgetTopHeight(kansLabel, yPos + offSet / 2, Style.Unit.PX, cHeight, Style.Unit.PX);

		    setWidgetLeftWidth(kansTextField, offSet + kansLabelWidth, Style.Unit.PX, kansTextFieldWidth, Style.Unit.PX);
			setWidgetTopHeight(kansTextField, yPos, Style.Unit.PX, cHeight2, Style.Unit.PX);
			
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
				kansSlider.setLocation(offSet,yPos + 3);// + (cHeight - kansSlider.getSize().height) / 2); 
				kansSlider.zetEnabled(true);
				yPos += kansSlider.hoogte-1;
				
				kansRect = new Rectangle(0, yPos - kansSlider.hoogte + 5 - cHeight2 - 4, leftWidth, 
                                         cHeight2 + 8 + kansSlider.hoogte - 5);
			}	
			else 
				kansSlider.zetEnabled(false);
			
			yPos += 10;
		}
		else
		{	setWidgetVisible(kansLabel, false);
			setWidgetVisible(kansTextField, false);
			setWidgetVisible(kansWaardeLabel, false);
		}
		
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
		
	}

	/**
	 * rond een double af op decs decimalen;
	 * @param d af te ronden double
	 * @param decs aantal decimalen
	 * @return afgeronde double
	 */
	public double round(double d, int decs)
	{	double factor = Math.pow(10, decs);
		return Math.round(d * factor) / factor;
	}	

	/**
	 * zet een nieuwe waarde voor mu, herbereken (als gewenst) en pas 
	 * minimum en maximum van de mu-slider aan (als gewenst);
	 * pas ook het aantal decimalen voor mu en de grenzen aan
	 * @param waarde nieuwe waarde voor mu
	 * @param bereken true: herbereken
	 * @param resetSlider true: pas minimum en maximum van de mu-slider aan 
	 */
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
		muString = UF.format0(mu,3);
		
		if (Math.abs(mu) < 2) 
		{	
			muString = UF.format0(mu,3);		
			muDecimals = 3;
		}	
		else if (Math.abs(mu) < 20)
		{	
			muString = UF.format0(mu,2);
			muDecimals = 2;
		}
		else if (Math.abs(mu) < 200)
		{	
			muString = UF.format0(mu,1);
			muDecimals = 1;
		}	
		else 
		{	
			muString = UF.format0(mu,0);
			muDecimals = 0;	
		}	
		
		mu = round(mu, muDecimals);	

		muString = muString.replace('.', ',');
		
		muTextField.setText(muString);
		muWaardeLabel.setText(muString);

		muMetWaardeLabel.setText("\u03BC" + " = " + muString); // "\u03BC"

		// grenzen blijven op hun plaats, maar raken mogelijk buiten beeld!!
		if (((kansKeuze == KANSLINKS) || (kansKeuze == KANSRECHTS)) &&
			(berekenKeuze != BEREKENGRENS)) 
		{	zetGrens(grens, false);
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
	
	}

	/**
	 * zet de knop van de mu-slider op de huidige waarde van mu 
	 */
	public void zetMuSlider()
	{	
		if (muSlider == null)
			return;
		
		int sliderPos = (int) Math.round(
						    (mu - muSliderMin) / (muSliderMax - muSliderMin) * 
						    (muSlider.getMaximum() - muSlider.getMinimum()));
						    
		muSlider.zetStand(sliderPos);				    
		
	}	

	/**
	 * zet een nieuwe waarde voor sigma, herbereken (als gewenst) en pas 
	 * minimum en maximum van de sigma-slider aan (als gewenst);
	 * pas ook het aantal decimalen voor sigma en de grenzen aan
	 * @param waarde nieuwe waarde voor sigma 
	 * @param bereken true: herbereken
	 * @param resetSlider true: pas minimum en maximum van de sigma-slider aan 
	 */
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
	}

	
	/**
	 * zet de knop van de sigma-slider op de huidige waarde van sigma 
	 */
	public void zetSigmaSlider()
	{	
		if (sigmaSlider == null)
			return;
		
		int sliderPos = (int) Math.round(
						    (sigma - sigmaSliderMin) / 
						    (sigmaSliderMax - sigmaSliderMin) * 
						    (sigmaSlider.getMaximum() - sigmaSlider.getMinimum()));
						    
		sigmaSlider.zetStand(sliderPos);				    
		
	}	
	
	
	/**
	 * zet een nieuwe waarde voor grens, herbereken (als gewenst); 
	 * pas ook het aantal decimalen voor grens aan
	 * @param waarde nieuwe waarde voor grens
	 * @param bereken true: herbereken
	 */
	public void zetGrens(double waarde, boolean bereken)
	{	
		grens = waarde;
		double grensMax = maxMuX;
		double grensMin = minMuX;
		
		if (berekenKeuze == BEREKENMU)
		{	
			grensMax = muMax + minX;
			grensMin = muMin + maxX;
		}

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

		grensString = grensString.replace('.', ',');
	
		grensTextField.setText(grensString);
		grensWaardeLabel.setText(grensString);
		
		zetGrensSlider();

		if (bereken)
			bereken();

	}
	
	/**'
	 * vindt het aantal decimalen voor grens
	 * @return aantal decimalen voor grens
	 */
	public int findGrensDecimals()
	{	int result = 2;
		
		// aantal eenheden x-as per pixel
		double xUnitsPerPixel = (maxX - minX) / (xMax - xMin);
		
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
	

	/**
	 * zet de knop van de grens-slider op de huidige waarde van grens 
	 */
	public void zetGrensSlider()
	{	
		if (grensSlider == null)
			return;
		
		int sliderPos = (int) Math.round(
						    (grens - minMuX) / (maxX - minX) * (xMax - xMin));
		grensSlider.zetStand(sliderPos);				    
		
	}

	/**
	 * zet een nieuwe waarde voor grenLinks, herbereken (als gewenst); 
	 * pas ook het aantal decimalen voor grensLinks aan
	 * @param waarde nieuwe waarde voor grensLinks
	 * @param bereken true: herbereken
	 */
	public void zetGrensLinks(double waarde, boolean bereken)
	{	
	
		// kontrole op waarden	
		grensLinks = waarde;


		double minDis = 
			((double) tweeGrenzenSlider.pixDis) / (xMax - xMin) * (maxX - minX);

		if (grensLinks > grensRechts - minDis - NZERO)
		{	grensLinks = grensRechts - minDis - NZERO;
			
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
		
		grensLinksString = grensLinksString.replace('.', ',');
	
		grensLinksTextField.setText(grensLinksString);
		grensLinksWaardeLabel.setText(grensLinksString);

		zetGrensLinksSlider();

		if (bereken)
			bereken();
		
	}

	/**
	 * zet de linkerknop van de twee grenzen double-slider op de huidige waarde van grensLinks 
	 */
	public void zetGrensLinksSlider()
	{	
		if (tweeGrenzenSlider == null)
			return;
		
		int sliderPos = (int) Math.round(
						    (grensLinks - minMuX) / (maxX - minX) * (xMax - xMin));
		tweeGrenzenSlider.zetStandLinks(sliderPos);				    
		
	}

	/**
	 * zet een nieuwe waarde voor grenRechts, herbereken (als gewenst); 
	 * pas ook het aantal decimalen voor grensRechts aan
	 * @param waarde nieuwe waarde voor grensRechts
	 * @param bereken true: herbereken
	 */
	public void zetGrensRechts(double waarde, boolean bereken)
	{	
		// kontrole op waarden
		grensRechts = waarde;
		
		if (grensRechts > maxMuX - NZERO)
		{	grensRechts = maxMuX;
														
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
		
			if (berekenKeuze == BEREKENGRENSRECHTS)
			{	
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
		
		grensRechtsString = grensRechtsString.replace('.', ',');
	
		grensRechtsTextField.setText(grensRechtsString);
		grensRechtsWaardeLabel.setText(grensRechtsString);
		
	
		zetGrensRechtsSlider();
		
		if (bereken)
			bereken();
	}


	/**
	 * zet de rechterknop van de twee grenzen double-slider op de huidige waarde van grensRechts 
	 */
	public void zetGrensRechtsSlider()
	{
		if (tweeGrenzenSlider == null)
			return;
		
		int sliderPos = (int) Math.round(
						    (grensRechts - minMuX) / (maxX - minX) * (xMax - xMin));
		tweeGrenzenSlider.zetStandRechts(sliderPos);				    
	}

	/**
	 * zet een nieuwe waarde voor kans, herbereken (als gewenst); 
	 * @param waarde nieuwe waarde voor kans
	 * @param bereken true: herbereken
	 */
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

		kansString = kansString.replace('.', ',');
	
		kansTextField.setText(kansString);
		kansWaardeLabel.setText(kansString);

		zetKansSlider();	

		if (bereken)
			bereken();

	}


	/**
	 * zet de knop van de kans-slider op de huidige waarde van kans 
	 */
	public void zetKansSlider()
	{	
		if (kansSlider == null)
			return;
	
		int sliderPos = (int) Math.round(
						    (kans - 0) / (1 - 0) * 
						    (kansSlider.getMaximum() - kansSlider.getMinimum()));
						    
		kansSlider.zetStand(sliderPos);				    
		
	}	
	
	/**
	 * kansKeuze is veranderd; 
	 * vink de nieuwe kansKeuze aan als init == true; 
	 * verander de berelen- en parameterlijst
	 * @param init true: vink de kansKeuze aan 
	 */
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
		
	}

	/**
	 * reset de parameters naar de situatie bij startup
	 */
	public void resetParameters()
	{	
		
		setWidgetVisible(muLabel, true);
		setWidgetVisible(muTextField, true);
		setWidgetVisible(muWaardeLabel, false);
		
		muSlider.zetEnabled(muSliderOptie);
		
		setWidgetVisible(sigmaLabel, true);
		setWidgetVisible(sigmaTextField, true);
		setWidgetVisible(sigmaWaardeLabel, false);
		
		
		sigmaSlider.zetEnabled(sigmaSliderOptie);

		if (kansKeuze != TWEEGRENZEN)
		{	
			setWidgetVisible(grensLabel, true);
			setWidgetVisible(grensTextField, true);
			
			grensSlider.zetEnabled(grensSliderOptie);
		}

		setWidgetVisible(grensWaardeLabel, false);
		
		setWidgetVisible(kansLabel, true);
		setWidgetVisible(kansTextField, true);
		setWidgetVisible(kansWaardeLabel, false);
		
		kansSlider.zetEnabled(true && kansSliderOptie);

		if (kansKeuze == TWEEGRENZEN)
		{	
			setWidgetVisible(grensLinksLabel, true);
			setWidgetVisible(grensLinksTextField, true);
		}
		setWidgetVisible(grensLinksWaardeLabel, false);
		
		if (kansKeuze == TWEEGRENZEN)
		{			
			setWidgetVisible(grensRechtsLabel, true);
			setWidgetVisible(grensRechtsTextField, true);
			
			tweeGrenzenSlider.zetLinksEnabled(grensSliderOptie);
			tweeGrenzenSlider.zetRechtsEnabled(grensSliderOptie);
			
			setWidgetVisible(grensLabel, false);
			setWidgetVisible(grensTextField, false);
		}
		setWidgetVisible(grensRechtsWaardeLabel, false);
		
		grensSlider.zetEnabled(true);
		tweeGrenzenSlider.zetLinksEnabled(true);
		tweeGrenzenSlider.zetRechtsEnabled(true);
	}
	
	/**
	 * pas de bereken- en parameterlijst aan
	 * nadat berekenKeuze veranderd is;
	 * vink de nieuwe berekenKeuze aan als init == true; 
	 * @param init true: vink de nieuwe berekenKeuze aan
	 */
	public void zetBerekenKeuze(boolean init)
	{	
		if (!init)
			resetParameters();
	
		if (berekenKeuze == BEREKENMU)
		{	if (init)
				muButton.setValue(true);
			muSlider.zetEnabled(false);
		
			setWidgetVisible(muTextField, false);
			setWidgetVisible(muWaardeLabel, true);
			grensSlider.zetEnabled(false);
			tweeGrenzenSlider.zetLinksEnabled(false);
			tweeGrenzenSlider.zetRechtsEnabled(false);
		
		}
		else if (berekenKeuze == BEREKENSIGMA)
		{	
			if (init)
				sigmaButton.setValue(true);
			sigmaSlider.zetEnabled(false);
		
			setWidgetVisible(sigmaTextField, false);
			setWidgetVisible(sigmaWaardeLabel, true);
		}
		else if (berekenKeuze == BEREKENGRENS)
		{	
			if (init)			
				grensButton.setValue(true);
			
			setWidgetVisible(grensTextField, false);
			setWidgetVisible(grensWaardeLabel, true);
			
			grensSlider.zetEnabled(false);
			
			setWidgetVisible(muTextField, !muVastOptie);
			
			muSlider.zetEnabled(!muVastOptie && muSliderOptie);
			
			setWidgetVisible(muWaardeLabel, muVastOptie);
			
			sigmaSlider.zetEnabled(!sigmaVastOptie && sigmaSliderOptie);
			
			setWidgetVisible(sigmaTextField, !sigmaVastOptie);
			setWidgetVisible(sigmaWaardeLabel, sigmaVastOptie);
		
		}
		else if (berekenKeuze == BEREKENKANS)
		{	if (init)
				kansButton.setValue(true);
		
			setWidgetVisible(kansTextField, false);
			setWidgetVisible(kansWaardeLabel, true);

			kansSlider.zetEnabled(false);
			
			setWidgetVisible(muTextField, !muVastOptie);
			
			muSlider.zetEnabled(!muVastOptie && muSliderOptie);
			
			setWidgetVisible(muWaardeLabel, muVastOptie);
			
			sigmaSlider.zetEnabled(!sigmaVastOptie && sigmaSliderOptie);
			
			setWidgetVisible(sigmaTextField, !sigmaVastOptie);
			setWidgetVisible(sigmaWaardeLabel, sigmaVastOptie);
		
		}
		else if (berekenKeuze == BEREKENGRENSLINKS)
		{	
			if (init)			
				grensLinksButton.setValue(true);
		
			setWidgetVisible(grensLinksTextField, false);
			setWidgetVisible(grensLinksWaardeLabel, true);
			
			tweeGrenzenSlider.zetLinksEnabled(false);
			
			setWidgetVisible(muTextField, !muVastOptie);
			
			muSlider.zetEnabled(!muVastOptie && muSliderOptie);
			
			setWidgetVisible(muWaardeLabel, muVastOptie);
			
			sigmaSlider.zetEnabled(!sigmaVastOptie && sigmaSliderOptie);
			
			setWidgetVisible(sigmaTextField, !sigmaVastOptie);
			setWidgetVisible(sigmaWaardeLabel, sigmaVastOptie);
		
		}
		else if (berekenKeuze == BEREKENGRENSRECHTS)
		{	
			if (init)			
				grensRechtsButton.setValue(true);
		
			setWidgetVisible(grensRechtsTextField, false);
			setWidgetVisible(grensRechtsWaardeLabel, true);

			tweeGrenzenSlider.zetRechtsEnabled(false);	
			
			setWidgetVisible(muTextField, !muVastOptie);
		
			muSlider.zetEnabled(!muVastOptie && muSliderOptie);
			
			setWidgetVisible(muWaardeLabel, muVastOptie);
			
			sigmaSlider.zetEnabled(!sigmaVastOptie && sigmaSliderOptie);
			
			setWidgetVisible(sigmaTextField, !sigmaVastOptie);
			setWidgetVisible(sigmaWaardeLabel, sigmaVastOptie);
			
		}
		
		plaatsComponenten(false);	
		
	}

	/**
	 * pas de berekenlijst en de parameterlijst aan: <br>
	 * dit is nodig indien de kanskeuze veranderd is van 
	 * een nar twee grenzen of vice-versa of indien een
	 * de parameter die berekend moet worden veranderd is   
	 */
	public void zetLijsten()
	{	
		if (kansKeuze == TWEEGRENZEN)
		{	
			// berekenlijst
			actualMuBerekenbaarOptie = false;
			if (berekenbaarZichtbaar)
				setWidgetVisible(muButton, false);
			
			actualSigmaBerekenbaarOptie = false;
			if (berekenbaarZichtbaar)
				setWidgetVisible(sigmaButton, false);
		
			if ((berekenKeuze == BEREKENMU)	||
				(berekenKeuze == BEREKENSIGMA))
			{	berekenKeuze = BEREKENKANS;
				bereken();
			}	
		
			if (berekenbaarZichtbaar)
			{	
				setWidgetVisible(grensButton, false);
				setWidgetVisible(grensLinksButton, true);
				setWidgetVisible(grensRechtsButton, true);	
			}
			
			// parameterlijst
			setWidgetVisible(grensLabel, false);
			setWidgetVisible(grensTextField, false);
			setWidgetVisible(grensWaardeLabel, false);
			
			grensSlider.zetEnabled(false);
			
			kansLabel.setText(NormVerdGWT.rb.kansTekst() + " = ");
						
			plaatsComponenten(false);			
			
			setWidgetVisible(grensLinksLabel, true);
			setWidgetVisible(grensLinksTextField, true);
			
			setWidgetVisible(grensRechtsLabel, true);
			setWidgetVisible(grensRechtsTextField, true);

			if (berekenKeuze != BEREKENGRENSLINKS)
				tweeGrenzenSlider.zetLinksEnabled(true);
			if (berekenKeuze != BEREKENGRENSRECHTS)
				tweeGrenzenSlider.zetRechtsEnabled(true);

			// waarden geven/overdragen
			if (berekenKeuze == BEREKENGRENS)
			{	berekenKeuze = BEREKENGRENSLINKS;
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
		{	
			// berekenlijst

			if (berekenbaarZichtbaar)
			{	
				setWidgetVisible(grensButton, true);
				setWidgetVisible(grensLinksButton, false);
				setWidgetVisible(grensRechtsButton, false);
			}	
			// parameterlijst
			setWidgetVisible(grensLabel, true);
			setWidgetVisible(grensTextField, true);
			
			grensSlider.zetEnabled(true);

			boolean wasTweeGrenzen = grensLinksLabel.isVisible();
			
			setWidgetVisible(grensLinksLabel, false);
			setWidgetVisible(grensTextField, false);
			setWidgetVisible(grensLinksWaardeLabel, false);

			setWidgetVisible(grensRechtsLabel, false);
			setWidgetVisible(grensRechtsTextField, false);
			setWidgetVisible(grensRechtsWaardeLabel, false);

			if (berekenKeuze != BEREKENGRENSLINKS)
				tweeGrenzenSlider.zetLinksEnabled(true);
			if (berekenKeuze != BEREKENGRENSRECHTS)
				tweeGrenzenSlider.zetRechtsEnabled(true);
			
			if (kansKeuze == KANSLINKS)
			{	
				kansLabel.setText(NormVerdGWT.rb.kansLinksTekst() + " = ");
				if (wasTweeGrenzen)
					zetGrens(grensRechts, true);
			}
			else // kansKeuze == KANSRECHTS
			{	
				kansLabel.setText(NormVerdGWT.rb.kansRechtsTekst() + " = ");
				if (wasTweeGrenzen)
					zetGrens(grensLinks, true);
			}
			
			// waarden geven/ overdragen
			if ((berekenKeuze == BEREKENGRENSLINKS) || 
				(berekenKeuze == BEREKENGRENSRECHTS)) 
			{	berekenKeuze = BEREKENGRENS;
			}
			
			actualMuBerekenbaarOptie = muBerekenbaarOptie && !muVastOptie;
			actualSigmaBerekenbaarOptie = sigmaBerekenbaarOptie && !sigmaVastOptie;
			if (berekenbaarZichtbaar)
			{
			setWidgetVisible(muButton, muBerekenbaarOptie && berekenbaarZichtbaar);
			setWidgetVisible(sigmaButton, sigmaBerekenbaarOptie && berekenbaarZichtbaar);
			}			
			zetBerekenKeuze(true);			
			plaatsComponenten(false);			
				
		}
		
	}


	/**
	 * lees de mu-slider af en pas mu aan
	 */
	public void processMuSlider()
	{	
		int stand = muSlider.geefStand();
		double muWaarde = muSliderMin + 
			((double) stand) / 
				(muSlider.getMaximum() - muSlider.getMinimum()) * 
				(muSliderMax - muSliderMin);
		zetMu(muWaarde, true, false);	
	}

	/**
	 * lees de sigma-slider af en pas sigma aan
	 */
	public void processSigmaSlider()
	{	
	
		int stand = sigmaSlider.geefStand();
		double sigmaWaarde = sigmaSliderMin + 
			((double) stand) / 
				(sigmaSlider.getMaximum() - sigmaSlider.getMinimum()) * 
				(sigmaSliderMax - sigmaSliderMin);
		zetSigma(sigmaWaarde, true, false);	
		
	}

	/**
	 * lees de grens-slider af en pas de grens aan
	 */
	public void processGrensSlider()
	{	
		int stand = grensSlider.geefStand();
		double grensWaarde = minMuX + 
			((double) stand) / (xMax - xMin) * (maxX - minX);
		zetGrens(grensWaarde, true);	
	}

	/**
	 * lees de tweegrenzen double slider af: stand linkerknop als links == true,
	 * stand rechetknop als links == false, en pas grensLinks resp. grensRechts aan 
	 * @param links true: stand linkerknop, false: stand rechterknop
	 */
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


	/**
	 * lees de kans-slider af en pas de kans aan
	 */
	public void processKansSlider()
	{	
		int stand = kansSlider.geefStand();
		double kansWaarde = 0 + 
			((double) stand) / 
				(kansSlider.getMaximum() - kansSlider.getMinimum()) * (1 - 0);
		zetKans(kansWaarde, true);	
	}

	public void paint()
	{
		paintComponent(nvContext2d);
	}
	

	/**
	 * teken:<br>
	 * de kansverdeling, d.w.z. de oppervlakte onder de distributiefunctie
	 * die correspondeert met de kans, een vertikale lijn voor
	 * mu, horizontale lijntjes voor sigma, de x-as, 
	 * de distributie-functie en de labels voor de parameters;<br>
	 * teken (licht)blauwe rechthoekjes om de parameterwaardem<br> 
	 * teken de sliders (als die er zijn) 
	 * @param og Context2d om te tekenen
	 */
	public void paintComponent(Context2d og)
	{			
		og.setFillStyle(CssColor.make(255,255,255));
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
	
	
	/**
	 * teken de x-as
	 * @param g Context2d om te tekenen
	 */
	public void paintXAxis(Context2d g)
	{	
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.beginPath();
		g.moveTo(xMin, yMin);
		g.lineTo(xMax, yMin);
		g.stroke();
		
	}
	
	/**
	 * teken een verticale lijn bij de mu-waarde en
	 * teken de mu-waarde
	 * @param g Context2d om te tekenen
	 */
	public void paintMuLine(Context2d g)
	{	// centreren
		int xPos = xMin + (int) Math.round(
								(mu - minMuX) / (maxX - minX) * (xMax - xMin));
		double fx = normalDF(mu);		

		int y = yMin - (int) Math.round(
								(fx - minY) / (maxY - minY) * (yMin - yMax));
		
		g.setStrokeStyle(muLineColor);
		g.beginPath();
		g.moveTo(xPos, y);
		g.lineTo(xPos, yMin);
		g.stroke();
		
		String muWaarde = "\u03BC" + " = " + muString;

		TextMetrics tm = g.measureText(muWaarde);
		
		int width = (int) Math.round(tm.getWidth());
		
		int bx = xPos - width / 2;
		if (bx < 2)
			bx = 2;
			
		if ((bx + width) > (breedte - 2))
			bx = breedte - width - 2;	
		
		// ad hoc		
		int by = yMin + 2 * 15; 
		int by2 = yMin + 15; 
		
		boolean lower = lowerGrensLabels || lowerGrensLinksLabels ||
						lowerGrensRechtsLabels;
		
		g.setFillStyle(CssColor.make(0,0,0));
		if (muZichtbaarFigOptie && lower)
		{	g.fillText(muWaarde, bx, by2);
		}
		else if (muZichtbaarFigOptie && !lower)	
		{	g.fillText(muWaarde, bx, by);
		}
		
	}

	/**
	 * teken twee horizontale lijntjes links en rechts van de lijn die
	 * de waarde van sigma aangeven en teken de sigma-waarde
	 * @param g Context2d om te tekenen
	 */
	public void paintSigmaLines(Context2d  g)
	{	
		// links
		int xStartLeft = xMin + (int) Math.round(
							(mu - sigma - minMuX) / (maxX - minX) * (xMax - xMin));
		int xEndLeft = xMin + (int) Math.round(
						(mu - minMuX) / (maxX - minX) * (xMax - xMin)) - 1;

		// rechts
		int xStartRight = xEndLeft + 2;
		
		int xEndRight = xMin + (int) Math.round(
							(mu + sigma - minMuX) / (maxX - minX) * (xMax - xMin));
						
						
		double fx = normalDF(mu - sigma);				
		
		int y = yMin - (int) Math.round(
								(fx - minY) / (maxY - minY) * (yMin - yMax));

		String sigmaWaarde = "\u03C3" + " = " + UF.format(sigma, sigmaDecimals);
		
		TextMetrics tm = g.measureText(sigmaWaarde);
		int width = (int) Math.round(tm.getWidth());
		
		int bxLeft = (xStartLeft + xEndLeft) / 2 - width / 2;
		int bxRight = (xStartRight + xEndRight) / 2 - width / 2;
		
		// ad hoc		
		int by = y + 20; 

		if (sigmaZichtbaarFigOptie)
		{	
			g.setStrokeStyle(sigmaLineColor);
			g.beginPath();
			g.moveTo(xStartLeft, y);
			g.lineTo(xEndLeft, y);
			g.stroke();
						
			g.beginPath();
			g.moveTo(xStartLeft, y);
			g.lineTo(xStartLeft + 5, y - 5);
			g.stroke();
						
			g.beginPath();
			g.moveTo(xStartLeft, y);
			g.lineTo(xStartLeft + 5, y + 5);
			g.stroke();
			
			g.beginPath();
			g.moveTo(xEndLeft, y);
			g.lineTo(xEndLeft - 5, y - 5);
			g.stroke();
						
			g.beginPath();
			g.moveTo(xEndLeft, y);
			g.lineTo(xEndLeft - 5, y + 5);
			g.stroke();
						
			g.beginPath();
			g.moveTo(xStartRight, y);
			g.lineTo(xEndRight, y);
			g.stroke();

			g.beginPath();
			g.moveTo(xStartRight, y);
			g.lineTo(xStartRight + 5, y - 5);
			g.stroke();

			g.beginPath();
			g.moveTo(xStartRight, y);
			g.lineTo(xStartRight + 5, y + 5);
			g.stroke();

			g.beginPath();
			g.moveTo(xEndRight, y);
			g.lineTo(xEndRight - 5, y - 5);
			g.stroke();

			g.beginPath();
			g.moveTo(xEndRight, y);
			g.lineTo(xEndRight - 5, y + 5);
			g.stroke();

			g.setFillStyle(CssColor.make(0,0,0));
			if ((bxRight + width) < breedte)		
			{	g.fillText(sigmaWaarde, bxRight, by);
			}
			else
			{	g.fillText(sigmaWaarde, bxLeft, by);
			}
		}

		
	}

	/**
	 * teken de distributiefunctie
	 * @param g Context2d om te tekenen
	 */
	public void paintDistribution(Context2d g)
	{	
		g.setStrokeStyle(CssColor.make(0,0,0));
		for (int xCnt = xMin; xCnt < xMax; xCnt++)
		{	// maak van xCnt en xCnt+1 de corresponderende double
			double x1 = minMuX + ((double) (xCnt - xMin)) / (xMax - xMin) * (maxX - minX);
			double x2 = minMuX + ((double) (xCnt + 1 - xMin)) / (xMax - xMin) * (maxX - minX);
			// bereken funktiewaarden
			double fx1 = normalDF(x1);
			double fx2 = normalDF(x2);
			// maak van fx1 en fx2 de corresponderende ints
			int y1 = yMin - (int) Math.round(
									(fx1 - minY) / (maxY - minY) * (yMin - yMax));
			int y2 = yMin - (int) Math.round(
									(fx2 - minY) / (maxY - minY) * (yMin - yMax));
			g.beginPath();
			g.moveTo(xCnt, y1);
			g.lineTo(xCnt + 1, y2);
			g.stroke();
		}
	
	}

	/**
	 * kleur de oppervlakte onder de distributiefunctie
	 * die correspondeert met de kans
	 * @param g Context2d om te tekenen
	 */
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

		for (int xCnt = xStart; xCnt <= xStop; xCnt++)
		{	// maak van xCnt een double
			double x = minMuX + ((double) (xCnt - xMin)) / (xMax - xMin) * (maxX - minX);
			// bereken funktiewaarde
			double fx = normalDF(x);
			// maak van fx de corresponderende int
			int y = yMin - (int) Math.round(
									(fx - minY) / (maxY - minY) * (yMin - yMax));
			
			if ((kansKeuze == KANSLINKS) && (xCnt == xStop))
			{	
				g.setStrokeStyle(CssColor.make(0,0,0));
			}
			else if ((kansKeuze == KANSRECHTS) && (xCnt == xStart))
			{	
				g.setStrokeStyle(CssColor.make(0,0,0));
			}
			else if ((kansKeuze == TWEEGRENZEN) && ((xCnt == xStart) || (xCnt == xStop)))
			{	
				g.setStrokeStyle(CssColor.make(0,0,0));
			}
			else
			{	
				g.setStrokeStyle(areaColor);
			}
			
			g.beginPath();
			g.moveTo(xCnt, y);
			g.lineTo(xCnt, yMin);
			g.stroke();
		}

		
	}

	/**
	 * voeg parameterlabels (als gewenst) toe aan in de tekening
	 * @param g Context2d om te tekenen
	 */
	public void paintLabels(Context2d g)
	{	
	
		int hOffset = 5;		
	
		if (kansKeuze == KANSLINKS)
		{	String grensWaarde = UF.format(grens, grensDecimals);
		
			TextMetrics tm = g.measureText(grensWaarde);
		
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
				// ad hoc				
				vSpace = 2 * 15; 
			}
			else
			{	
				// ad hoc				
				vSpace = 15; 
			
			}
				
			g.setFont(fontString);
			g.setFillStyle(CssColor.make(0,0,0));
			if (grensZichtbaarFigOptie)
			{	
				g.fillText(grensWaarde, grensWaardePos, yMin + vSpace);
			}
			
			String gString = NormVerdGWT.rb.gTekst();
			tm = g.measureText(gString);
			
			width = (int) Math.round(tm.getWidth());
			
			int gPos = grensPos + hOffset;
			if (gPos + width > breedte)
				gPos -= gPos + width - breedte;	
			if (grensZichtbaarFigOptie)
			{	
				// ad hoc
				g.fillText(gString, gPos, yMin - 6);
			}
			
			String kansWaarde = UF.format(kans, kansDecimals);
			
			tm = g.measureText(kansWaarde);
			
			width = (int) Math.round(tm.getWidth());
			
			int kansPos = grensPos - width - 2 * hOffset;
			if (kansPos < 0)
				kansPos = 0;
			
			g.setFillStyle(kansColor);
			if (kansZichtbaarFigOptie)
			{	
				// ad hoc		
				g.fillText(kansWaarde, kansPos, yMin - 20);
			}
			
			
		}
		else if (kansKeuze == KANSRECHTS)
		{	String grensWaarde = UF.format(grens, grensDecimals);
		
			TextMetrics tm = g.measureText(grensWaarde);
			
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
				// ad hoc				
				vSpace = 2 * 15; 
			}
			else
			{
				// ad hoc
				vSpace = 15; 	
			}
			
			g.setFont(fontString);
			g.setFillStyle(CssColor.make(0,0,0));
			
			if (grensZichtbaarFigOptie)
			{	
				g.fillText(grensWaarde, grensWaardePos, yMin + vSpace);
			}
			
			String gString = NormVerdGWT.rb.gTekst();
			tm = g.measureText(gString);
			
			width = (int) Math.round(tm.getWidth());
			
			int gPos = grensPos - hOffset - width;
			if (gPos < 0)
				gPos = 0;
			if (grensZichtbaarFigOptie)
			{	
				// ad hoc
				g.fillText(gString, gPos, yMin - 6);
			}
			
			String kansWaarde = UF.format(kans, kansDecimals);
			tm = g.measureText(kansWaarde);
			
			width = (int) Math.round(tm.getWidth());
			
			int kansPos = grensPos + 2 * hOffset;
			if (kansPos + width > breedte)
				kansPos -= kansPos + width - breedte;
			
			g.setFillStyle(kansColor);
			if (kansZichtbaarFigOptie)
			{	
				// ad hoc			
				g.fillText(kansWaarde, kansPos, yMin - 20);
			}
		}
		else // kansKeuze == TWEEGRENZEN
		{	String grensLinksWaarde = UF.format(grensLinks, grensDecimals);
			String grensRechtsWaarde = UF.format(grensRechts, grensDecimals);
			
			TextMetrics tmLinks = g.measureText(grensLinksWaarde);
			TextMetrics tmRechts = g.measureText(grensRechtsWaarde);
			

			int widthLinks = (int) Math.round(tmLinks.getWidth());
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
				// ad hoc				
				vLinksSpace = 2 * 15; 
			}
			else
			{
				// ad hoc				
				vLinksSpace = 15; 
			}
				
			int vRechtsSpace = 0;
			if (lowerGrensRechtsLabels)
			{
				// ad hoc				
				vRechtsSpace = 2 * 15; 
			}
			else
			{	
				// ad hoc				
				vRechtsSpace = 15; 
			
			}
				
			g.setFont(fontString);
			g.setFillStyle(CssColor.make(0,0,0));
			
			if (grensZichtbaarFigOptie)
			{	
				g.fillText(grensLinksWaarde, grensLinksWaardePos, yMin + vLinksSpace);
			}
			if (grensZichtbaarFigOptie)
			{	
				g.fillText(grensRechtsWaarde, grensRechtsWaardePos,  yMin + vRechtsSpace);
			}
			
			String lString = NormVerdGWT.rb.lTekst();
			String rString = NormVerdGWT.rb.rTekst();
			
			tmLinks = g.measureText(lString);
			tmRechts = g.measureText(rString);
			
			int lWidth = (int) Math.round(tmLinks.getWidth());
			int rWidth = (int) Math.round(tmRechts.getWidth());
			
			int lPos = grensLinksPos - hOffset - lWidth;
			if (lPos < 0)
				lPos = 0;
				
			int rPos = grensRechtsPos + hOffset;	
			if (rPos + rWidth > breedte)
				rPos -= rPos + rWidth - breedte;
			
			if (grensZichtbaarFigOptie)
			{	
				// ad hoc 			
				g.fillText(lString, lPos, yMin - 6);
			}
			if (grensZichtbaarFigOptie)
			{	
				// ad hoc			
				g.fillText(rString, rPos, yMin - 6);
			}
			
			String kansWaarde = UF.format(kans, kansDecimals);
			
			TextMetrics tm = g.measureText(kansWaarde);
			int width = (int) Math.round(tmLinks.getWidth());
			
			int kansPos = (grensLinksPos + grensRechtsPos) / 2 - width / 2;
			if (kansPos < 0)
				kansPos = 0;
			if (kansPos + width > breedte)
				kansPos -= kansPos + width - breedte;	
			
			g.setFillStyle(kansColor);
			if (kansZichtbaarFigOptie)
			{	
				// ad hoc		
				g.fillText(kansWaarde, kansPos, yMin - 20);
			}
		}
	}
	
	/**
	 * bereken de parameter gegeven door het attribuut berekenKeuze;<br>
	 * gebruik bij berekenKeuze == BEREKENSIGMA of BEREKENMU en kansKeuze == TWEEGRENZEN
	 * een iteratieve benadering
	 */
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

// dit werkt niet goed ??
				
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
				
// dit werkt niet goed ??				
				
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

	/**
	 * kansdichtheidsfunctie (kansverdeling) van de normale verdeling
	 * @param x x-waarde voor berekening kansdichtheidsfunctie   
	 * @return waarde kansdichtheidsfunctie in x
	 */
	public double normalDF(double x)
	{	double fx = Math.pow(Math.E, 
							 - (x - mu) * (x - mu) / (sigma * sigma * 2)) /
					(sigma * Math.sqrt(2 * Math.PI));		 
		return fx;			
	}

	/**
	 * cumulatieve kansverdeling voor de normale verdeling
	 * @param z z-waarde voor berekening cumulatieve kansverdeling  
	 * @return oppervlakte onder cumulatieve kansverdeling links van z
	 */
	public double phi(double z)
	{	if (Math.abs(z) < NZERO)
			return 5e-1d;
		else 
			return (1 + erf(z / Math.sqrt(2))) / 2;
	}

	/**
	 * een benadering voor de error function erf
	 * @param x x-waarde voor berekening erf(x)  
	 * @return erf(x)
	 */
	public double erf(double x)
	{	
		double erfx = StatUtil.erf(x);

		return erfx;
	}
	
	/**
	 * inverse cumulatieve kansverdeling voor de normale verdeling
	 * @param p gegeven kans
	 * @return z-waarde zodat de oppervlakte onder cumulatieve kansverdeling links van z
	 * gelijk is aan p
	 */
	public double phiInv(double p)
	{	
		double phiInvp = StatUtil.getInvCDF(p, true);
	
		return phiInvp;
	
	}

	/**
	 * inner class die ValueChangeEvents op de RadioButtons
	 * in de kansGroup afhandelt 
	 */
	class KansKeuzeVCH implements ValueChangeHandler<Boolean>
	{	
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
			
			changed();
		}
	}
	
	/**
	 * inner class die ValueChangeEvents op de RadioButtons
	 * in de berekenGroup afhandlet 
	 */
	class BerekenKeuzeVCH implements ValueChangeHandler<Boolean>
	{	
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
			
			changed();
		}
	}
	

	/**
	 * inner class die de input in een TextBox verwerkt wanneer 
	 * op de Enter key gebrukt wordt<br>
	 * de klasse werkt voor alle TextBoxes 
	 * @author huub
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
			
				changed();
			} //if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			
		} // onKeyDown 
		
	}

	/**
	 * inner class die de input in een TextBox verwerkt wanneer 
	 * de TextBox "geblurd" wordt, d.w.z. he focus verliest; <br>
	 * de klasse werkt voor alle TextBoxes 
	 * @author huub
	 */
	class TextBoxBlurHandler implements BlurHandler
	{	
		TextBox inputTextField;
		
		public TextBoxBlurHandler(TextBox input)
		{	inputTextField = input;
		}
		
		public void onBlur(BlurEvent e)
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
			
			changed();
			
		} // onBlur 
		
	}

	/**
	 * kijk of het decimale deel van String s eindigt op een of meer nullen
	 * en laat die weg; betaat het decimale deel helemaal uit nullen, laat dan
	 * ook de decimale punt of komma weg  
	 * @param s String die getrimd moet worden
	 * @param decSep character gebruikt voor decimale weergave
	 * @return getrimde String
	 */
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
		
	/**
	 * gegeven een String die begint met een decimale punt of een decimale
	 * komma, of die begint met een minteken en dan een decimale punt of een decimale komma,
	 * voeg vooraan een 0 toe (voeg een 0 toe na het minteken)
	 * @param s te veranderen String
	 * @param decSep character gebruikt voor decimale weergave
	 * @return String met extra 0
	 */
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

	/**
	 * verwijder het character op index s uit String s; <br>
	 * geen error handling bij illegale index
	 * @param s String waaruit weggelaten wordt
	 * @param index index van character dat weggeleten wordt
	 * @return String waaruit weggelaten is
	 */
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
	/**
	 * zet de status van de actuale opdracht zoals opgeslagen
	 * in de Map map 
	 * @param map Map met status opdracht
	 */
	public void setState(Map map)
	{	

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

		this.kansKeuze = kansKeuze;
		this.berekenKeuze = berekenKeuze;	

		zetMu(mu, false, true);
		zetSigma(sigma, false, true);
		zetGrens(grens, false);
		
		zetGrensLinks(grensLinks, false);				
		zetGrensRechts(grensRechts, false);						
		zetKans(kans, false);
		
		zetKansKeuze(true);
		
		if (this.kansKeuze == TWEEGRENZEN)
		{	zetGrensLinks(grensLinks, false);				
			zetGrensRechts(grensRechts, false);						
		}
		
		zetBerekenKeuze(false);
		
		bereken();
	}

	/**
	 * zet de initiele status van normaalPanel nadat de
	 * waarden van mu, sigma, grens, grenslinks, grensrechts,
	 * kans, kanskeuze en berekenkeuze geinitialiseerd zijn
	 */
	public void setInitState()
	{
		zetMu(mu, false, true);
		zetSigma(sigma, false, true);
		zetGrens(grens, false);
		
		zetGrensLinks(grensLinks, false);				
		zetGrensRechts(grensRechts, false);						
		zetKans(kans, false);
		
		zetKansKeuze(true);
		
		if (this.kansKeuze == TWEEGRENZEN)
		{	zetGrensLinks(grensLinks, false);				
			zetGrensRechts(grensRechts, false);						
		}
		
		zetBerekenKeuze(false);
		
		bereken();
		
	}
	
	/**
	 * stop de status van de actuele opdracht in een HashMap;
	 * bewaar alleen die dingen die de leerling veranderen kan
	 * @return HashMap met status
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
	    
		return h;
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
	
    /**
     * er werd iets veranderd, verwijder het laatste 
     * nakijkresultaat (d.w.z. goedkrul of foutkruis)
     */
    public void changed()
    {
    	if (kijkOpdrachtNa) 
		{
    		kijkNaPanel.setWidgetVisible(owner.goedKrulImage, false);
    		kijkNaPanel.setWidgetVisible(owner.foutKruisImage, false);
    		
    		owner.changed();
		}
    }

    /**
     * kijk na, zet goedkrul of foutkruis
     */
    public void kijkNa()
    {
    	
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
			}
			
			
		}
		
		if (correct) 
		{	score = maxScore;
		}
		else 
		{	score = 0;
		}
		
		kijkNaPanel.setWidgetVisible(owner.goedKrulImage, correct);
		kijkNaPanel.setWidgetVisible(owner.foutKruisImage, !correct);

		owner.correct = new Boolean(correct);
		
	}
    	
	/**
	 * actie bij MouseStart/TouchDown, alleen voor Events op de sliders
	 * @param eventX x-coordinaat MouseStart/TouchDown 
	 * @param eventY y-coordinaat MouseStart/TouchDown
	 */
    public void mouseDownTouchStartAction(int eventX, int eventY)
    {

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

	/**
	 * actie bij MouseMove/TouchMove, alleen voor Events op de sliders
	 * @param eventX x-coordinaat MouseMove/TouchMove 
	 * @param eventY y-coordinaat MouseMove/TouchMove
	 */
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{

		if ((muSlider != null) && muSlider.sliderRectangle.contains(eventX, eventY))
			muSlider.mouseMoveTouchMoveAction(eventX, eventY);
    	if ((sigmaSlider != null) && sigmaSlider.sliderRectangle.contains(eventX, eventY))
    		sigmaSlider.mouseMoveTouchMoveAction(eventX, eventY);
    	if ((grensSlider != null))
    		grensSlider.mouseMoveTouchMoveAction(eventX, eventY);
    	if ((kansSlider != null) && kansSlider.sliderRectangle.contains(eventX, eventY))
    		kansSlider.mouseMoveTouchMoveAction(eventX, eventY);

    	if ((tweeGrenzenSlider != null))
    		tweeGrenzenSlider.mouseMoveTouchMoveAction(eventX, eventY);
    	
	}
    
	/**
	 * actie bij MouseUp/TouchEnd, alleen voor Events op de sliders
	 * @param eventX x-coordinaat MouseUp/TouchEnd 
	 * @param eventY y-coordinaat MouseUp/TouchEnd
	 */
    public void mouseUpTouchEndAction(int eventX, int eventY)
    {
    	if ((muSlider != null) && muSlider.sliderRectangle.contains(eventX, eventY))
			muSlider.mouseUpTouchEndAction();
    	if ((sigmaSlider != null) && sigmaSlider.sliderRectangle.contains(eventX, eventY))
    		sigmaSlider.mouseUpTouchEndAction();
    	if ((grensSlider != null))
    		grensSlider.mouseUpTouchEndAction();
    	if ((kansSlider != null) && kansSlider.sliderRectangle.contains(eventX, eventY))
    		kansSlider.mouseUpTouchEndAction();

    	if ((tweeGrenzenSlider != null))
    		tweeGrenzenSlider.mouseUpTouchEndAction();

    }	
    	
	/**
	 * inner class die Mouse Events op nvCanvas afhandelt; zie methoden
	 * mouseDownTouchStartAction, mouseMoveTouchMoveAction en mouseUpTouchEndAction    
	 */
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{	// zie inner class TouchHandler
		int lastX, lastY;

		public void onMouseDown(MouseDownEvent e)
		{
			e.preventDefault();
			
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			lastX = e.getX();
			lastY = e.getY();
			
			mouseDown = true;
			
			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		public void onMouseMove(MouseMoveEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();

			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();
			
			lastX = e.getX();
			lastY = e.getY();
			
			mouseMoveTouchMoveAction(eventX, eventY);
			
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
			
			mouseUpTouchEndAction(lastX, lastY);

		}

	} 

	/**
	 * inner class die Touch Events op nvCanvas afhandelt; zie methoden
	 * mouseDownTouchStartAction, mouseMoveTouchMoveAction en mouseUpTouchEndAction    
	 */
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		// het TouchEnd Event wordt niet onthouden; onthoudt dus het laatste TouchMove Event  
		int lastX, lastY;
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				int eventX = touch.getPageX() - nvCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - nvCanvas.getAbsoluteTop();
				
				lastX = eventX;
				lastY = eventY;
				
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
				
			    int eventX = touch.getPageX() - nvCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - nvCanvas.getAbsoluteTop();				
				lastX = eventX;
				lastY = eventY;
			    
				mouseMoveTouchMoveAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			mouseUpTouchEndAction(lastX, lastY);
		}

	}

	/**
	 * inner class die Click Events op de
	 * kijkNaButton afhandelt
	 */
    class PushClickHandler implements ClickHandler
    {
    	public void onClick(ClickEvent e)
    	{
			e.stopPropagation();
    		
    		if (e.getSource() == kijkNaButton)
    		{
    			 owner.kijkNa();   			
    		}
    	}	
    }	

}