package fi.binomverdgwt.client;

//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Font;
//import java.awt.FontMetrics;
//import java.awt.GridLayout;
//import java.awt.Rectangle;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.FocusEvent;
//import java.awt.event.FocusListener;
import java.util.ArrayList;
//import java.util.Hashtable;
import java.util.Iterator;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;


//import javax.swing.ButtonGroup;
//import javax.swing.ImageIcon;
//import javax.swing.JButton;
//import javax.swing.JCheckBox;
//import javax.swing.JComboBox;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JRadioButton;
//import javax.swing.JTextField;
//import javax.swing.SwingConstants;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;

/**
 * InteractiePanel van BinomVerdeling
 * Model, view en controller nu bij elkaar gevoegd.
 */
public class BinomVerdPanel extends LayoutPanel //JPanel implements InteractiePanel, ActionListener, FocusListener 
{
	double p; //succeskans
	int n; //aantal herhalingen (BV), grootte greep (Hypergeometrisch)
	
	int M; //voor hypergeometrisch, succesgevallen in populatie
	int populatie; //voor hypergeometrisch, grootte van populatie
	
	private GrenzenOptie grenzenOptie;
	private boolean tweeGrenzen; //true = 2 grenzen, false = 1 grens
	
	private boolean hypergeometrisch; //true: Hypergeometrisch, false: Binomiaal
	
	BVStaafjesPanel staafjesPanel;
	private TextBox nText;
	private Label nLabel;
	private TextBox pText;
	private Label pLabel;
	private TextBox MText;
	private Label MLabel;
	private TextBox populatieText;
	private Label populatieLabel;
	private RadioButton kansRadioLinks;
	private RadioButton kansRadioMidden;
	private RadioButton kansRadioRechts;
	private Label kansLabelLinks;
	private Label kansLabelMidden;
	private Label kansLabelRechts;
	
	
	
//GWT?
/*	
	private JPanel kansBalk;
	private JPanel zuidBalk;
	private JPanel noordBalk;
	private JPanel keuzeBalk;
*/	
	
	
	private Slider nSlider;
	private Slider pSlider;
	private Slider MSlider;
	private Slider populatieSlider;
	
	private CheckBox grenzenBox;
	private ListBox hyperComboBox;
	
	int grenzenBoxWidth;
	
	public static final int N_MIN = 0; //min en max van n voor de sliders
	public static final int N_MAX = 100;
	public static final int POPULATIE_MIN = 1;
	public static final int POPULATIE_MAX = 100;
	
	private boolean nVeranderbaar;
	private boolean pVeranderbaar;
	private boolean MVeranderbaar;
	private boolean populatieVeranderbaar;
	
	private boolean showNSlider;
	private boolean showPSlider;
	private boolean showMSlider;
	private boolean showPopulatieSlider;
	
	BVInvoer nInvoer;
	BVInvoer pInvoer;
	BVInvoer MInvoer;
	BVInvoer populatieInvoer;
	
	private boolean showNoordBalk;
	private boolean showTweeGrenzenKeuze;
	private boolean showKansBalk;
	private boolean showHyperKeuze;
	
	PushButton kijkNaButton;
	LayoutPanel kijkNaPanel;
	//private JLabel vinkjeLabel;
	//private JLabel kruisjeLabel;
	
	
	//private Font font;
	//private FontMetrics fontMetrics;
	String fontString = "12px sans-serif";
	TextMetrics tm;
		
	public final CssColor STAAFJE_TELT = CssColor.make(110,5,165);
	public final CssColor STAAFJE_TELT_NIET = CssColor.make(234,229,255);
	public final CssColor LABEL_BACKGROUND = CssColor.make(240,247,255);	
	public final CssColor LABEL_ACTIEF = CssColor.make(200,227,255);
	

	
	private LayoutPanel noordLinks;
	private LayoutPanel noordMidden;
	private LayoutPanel noordRechtsBV;
	private LayoutPanel noordRechtsHyp;
	
	public final int HOFFSET = 10;
	
	public final int NOORDBALKHEIGHT = 43;
	public final int NOORDBALKGAP = 4;
	public final int COMBOBOXHEIGHT = 22;
	
	public final int ZUIDBALKHEIGHT = 44;
	public final int ZUIDBALKVOFFSET = 4;
	public final int RADIOBUTTONHEIGHT = 20;
	public final int CHECKBOXHEIGHT = 20;
	
	public final int TEXTBOXWIDTH = 50;
	public final int TEXTBOXHEIGHT = 25;
	
	public final int SHORTLABELWIDTH = 20;
	public final int LONGLABELWIDTH = 70;
	
	private Rectangle lastBounds; //om bij te houden wanneer de maat bounds veranderen
	
//GWT	
	//private ArrayList<ActionListener> listeners;
	
	//nakijken:
	boolean kijkOpdrachtNa;
	int maxScore = 10;
	boolean kijkGrenzenNa;
	int antwoordGrensLinks = 5;
	int antwoordGrensRechts = 10;
	boolean kijkVerdelingNa;
	int antwoordVerdeling = 0; //0 = BV, 1 = Hyp
	boolean kijkNNa;
	int antwoordN = 30;
	boolean kijkPNa;
	BVInvoer antwoordP = new BVInvoer("0.5");
	boolean kijkMNa;
	int antwoordM = 50;
	boolean kijkPopulatieNa;
	int antwoordPopulatie = 100;
	
	int score;
	//private boolean nagekeken;
	//private int mode;
	//Boolean correct = null;
	//boolean fout = false;

	
	int breedte, hoogte;
	
	BinomVerdGWT owner;
	
	/**
	 * Constructor
	 */
	public BinomVerdPanel(BinomVerdGWT o, int b, int h) 
	{
		
		owner = o;
		//super();
		//super.setLayout(new BorderLayout());

		breedte = b;
		hoogte = h;
		//this.font = new Font("Dialog", Font.PLAIN, 12);
		//this.fontMetrics = getFontMetrics(this.font);
		
		this.n = 30;
		this.p = 0.5;
		this.M = 50;
		this.populatie = 100;
		
		this.nInvoer = new BVInvoer("30");
		this.pInvoer = new BVInvoer("0.5");
		this.MInvoer = new BVInvoer("50");
		this.populatieInvoer = new BVInvoer("100");
		
		this.hypergeometrisch = false;
		this.showHyperKeuze = true;
		
		this.kijkOpdrachtNa = false;
		
		this.grenzenOptie = GrenzenOptie.LINKS;
		
		this.nVeranderbaar = true;
		this.pVeranderbaar = true;
		this.populatieVeranderbaar = true;
		this.MVeranderbaar = true;
		
		this.showNSlider = true;
		this.showPSlider = true;
		this.showMSlider = true;
		this.showPopulatieSlider = true;
				
		this.showNoordBalk = true;
		this.showTweeGrenzenKeuze = true;
		this.showKansBalk = true;
		
		
// in plaatsComponenten()		
		//maak het paneel wat in de center van de BorderLayout komt
        //this.staafjesPanel = new BVStaafjesPanel(this, this.grenzenOptie);
        
                // dit in plaatsCompomenten
        //super.add(this.staafjesPanel, BorderLayout.CENTER);
		
        this.kansLabelLinks = new Label();
//GWT (4)        
        //this.kansLabelLinks.setBackground(this.LABEL_BACKGROUND);
        //this.kansLabelLinks.setOpaque(true);
        //this.kansLabelLinks.setFont(this.font);
        //this.kansLabelLinks.setHorizontalAlignment(SwingConstants.CENTER);
        
        this.kansLabelMidden = new Label();
        kansLabelMidden.addStyleName(BinomVerdGWT.binomVerdGWTCss.label());
//GWT (4)        
        //this.kansLabelMidden.setBackground(this.LABEL_ACTIEF);
        //this.kansLabelMidden.setOpaque(true);
        //this.kansLabelMidden.setFont(this.font);
        //this.kansLabelMidden.setHorizontalAlignment(SwingConstants.CENTER);
        
        this.kansLabelRechts = new Label();
//GWT (4)        
        //this.kansLabelRechts.setBackground(this.LABEL_BACKGROUND);
        //this.kansLabelRechts.setOpaque(true);
        //this.kansLabelRechts.setFont(this.font);
        //this.kansLabelRechts.setHorizontalAlignment(SwingConstants.CENTER);
        
        //this.zuidBalk.setLayout(new GridLayout(2,1));
        
        
		//this.kansBalk = new JPanel();
		//GridLayout gl = new GridLayout(1,3);
		//gl.setHgap(5);
		//this.kansBalk.setLayout(gl);
		//this.kansBalk.setBackground(Color.WHITE);
		
        String kansGroep = "kansGroep";
        
		this.kansRadioLinks = new RadioButton(kansGroep);
		kansRadioLinks.setStyleName(BinomVerdGWT.binomVerdGWTCss.radiobuttonon());		
		this.kansRadioMidden = new RadioButton(kansGroep);
		kansRadioMidden.setStyleName(BinomVerdGWT.binomVerdGWTCss.radiobuttonon());		
		this.kansRadioRechts = new RadioButton(kansGroep);
		kansRadioRechts.setStyleName(BinomVerdGWT.binomVerdGWTCss.radiobuttonon());

		kansRadioLinks.setValue(true, false);

//GWT (3)		
		//this.kansRadioLinks.setFont(this.font);
		//this.kansRadioMidden.setFont(this.font);
		//this.kansRadioRechts.setFont(this.font);
		
//GWT (3)		
		//this.kansRadioLinks.setHorizontalAlignment(SwingConstants.CENTER);
		//this.kansRadioMidden.setHorizontalAlignment(SwingConstants.CENTER);
		//this.kansRadioRechts.setHorizontalAlignment(SwingConstants.CENTER);
		
//in plaatsComponenten		
		//this.kansRadioLinks.addActionListener(this);
		//this.kansRadioMidden.addActionListener(this);
		//this.kansRadioRechts.addActionListener(this);
		
//GWT (3)		
		//this.kansRadioLinks.setBackground(this.LABEL_ACTIEF);
		//this.kansRadioMidden.setBackground(this.LABEL_BACKGROUND);
		//this.kansRadioRechts.setBackground(this.LABEL_BACKGROUND);
		
		//ButtonGroup buttonGroup = new ButtonGroup();
		
//in plaatsComponenten		
		//buttonGroup.add(this.kansRadioLinks);
		//buttonGroup.add(this.kansRadioMidden);
		//buttonGroup.add(this.kansRadioRechts);
		
		//this.kansBalk.add(this.kansRadioLinks);
		//this.kansBalk.add(this.kansRadioMidden);
		//this.kansBalk.add(this.kansRadioRechts);
		//this.zuidBalk.add(this.kansBalk);
		
		//tm = staafjesPanel.binomVerdGWTContext2d.measureText("Twee grenswaarden");
		grenzenBoxWidth = 170;
		this.grenzenBox = new CheckBox("Twee grenswaarden");
		//grenzenBox.addStyleName(BinomVerdGWT.binomVerdGWTCss.checkbox());		
//GWT (2)		
		//this.grenzenBox.setFont(this.font);
		//this.grenzenBox.setBackground(Color.WHITE);
		
//in plaatsComponenten		
		//this.grenzenBox.addActionListener(this);
		
		kijkNaButton = new PushButton("Kijk Na");
		kijkNaButton.addStyleName(BinomVerdGWT.binomVerdGWTCss.pushbutton());
		kijkNaPanel = new LayoutPanel();
//GWT later		
/*		
		this.kijkNaButton = new JButton("Kijk Na");
		this.kijkNaButton.setFont(this.font);
		this.kijkNaButton.addActionListener(this);
		
		java.net.URL imageURL = BinomVerdeling.class.getResource("resources/goedkrul_en_klein.gif");
		if (imageURL != null) {
		    this.vinkjeLabel = new JLabel(new ImageIcon(imageURL));
		}
		else {
			System.out.println("Error reading goedkrul_en_klein.gif.");
			this.vinkjeLabel = new JLabel();
		}
		imageURL = BinomVerdeling.class.getResource("resources/foutkruis_klein.gif");
		if (imageURL != null) {
		    this.kruisjeLabel = new JLabel(new ImageIcon(imageURL));
		}
		else {
			System.out.println("Error reading foutkruis_klein.gif.");
			this.kruisjeLabel = new JLabel();
		}
		this.vinkjeLabel.setVisible(false);
		this.kruisjeLabel.setVisible(false);
		this.kijkNaPanel = new JPanel(null);
		this.kijkNaPanel.setBackground(Color.WHITE);
		this.kijkNaPanel.add(this.kijkNaButton);
		this.kijkNaPanel.add(this.vinkjeLabel);
		this.kijkNaPanel.add(this.kruisjeLabel);
		this.kijkNaPanel.setVisible(this.kijkOpdrachtNa);
		this.plaatsComponentenKijkNaPanel(this.getWidth());
*/		
		//this.keuzeBalk = new JPanel(new GridLayout(1,2));
		//this.keuzeBalk.setBackground(Color.WHITE);
		//this.keuzeBalk.add(this.grenzenBox);
		//this.keuzeBalk.add(this.kijkNaPanel);
		//this.zuidBalk.add(this.keuzeBalk);
		
		//super.add(this.zuidBalk, BorderLayout.SOUTH);
		
        this.nText = new TextBox();
        this.nText.setText(Integer.toString(this.n));
        
        this.pText = new TextBox();
        this.pText.setText(Double.toString(this.p));
        
        this.MText = new TextBox();
        this.MText.setText(Integer.toString(this.M));
        
        this.populatieText = new TextBox();
        this.populatieText.setText(Integer.toString(this.populatie));
        
        this.nLabel = new Label("n = ");
        this.MLabel = new Label("M = ");
        this.populatieLabel = new Label("populatie = ");
        this.pLabel = new Label("p = ");
        
//in plaatsComponenten
        //this.nSlider = new Slider(100,50);
        //this.pSlider = new Slider(100,50);
        //this.MSlider = new Slider(100,50);
        //this.populatieSlider = new Slider(100,50);
        //this.nSlider.zetLengte(this.getWidth()/3);
        //this.pSlider.zetLengte(this.getWidth()/3);
        //this.MSlider.zetLengte(this.getWidth()/3);
        //this.populatieSlider.zetLengte(this.getWidth()/3);
        //this.nSlider.addActionListener(this);
        //this.pSlider.addActionListener(this);
        //this.MSlider.addActionListener(this);
        //this.populatieSlider.addActionListener(this);
                
        //maak het paneel wat in het NORTH gebied van de BorderLayout komt
        
        //String[] keuzes = {"Binomiaal", "Hypergeometrisch"};
        hyperComboBox = new ListBox();
        hyperComboBox.addItem("Binomiaal");
        hyperComboBox.addItem("Hypergeometrisch");
        hyperComboBox.setVisibleItemCount(1);
        if (hypergeometrisch)
        	hyperComboBox.setItemSelected(1, true);
        
        
//GWT (1)        
      //this.hyperComboBox.setBackground(this.LABEL_BACKGROUND);

//in plaatsCompomenten        
        //this.hyperComboBox.setBounds(0,0,this.getWidth(), this.COMBOBOXHEIGHT);
        //this.hyperComboBox.addActionListener(this);
  
/*        
        this.noordBalk = new JPanel();
        this.noordBalk.setLayout(null);
        this.noordBalk.setPreferredSize(new Dimension(this.getWidth(), this.NOORDBALKHEIGHT));
        this.noordBalk.setBackground(Color.WHITE);
*/        
  
        
        this.noordLinks = new LayoutPanel();
        //this.noordLinks.setBackground(this.LABEL_BACKGROUND);
        //this.noordLinks.setLayout(null);
        //this.noordLinks.setSize(this.getWidth()/3, this.NOORDBALKHEIGHT);
        //this.noordLinks.setLocation(0, this.COMBOBOXHEIGHT);
        //this.noordLinks.add(this.nSlider);
        //this.noordLinks.add(this.nLabel);
        //this.noordLinks.add(this.nText);
        
        
        
        this.noordMidden = new LayoutPanel();
        //this.noordMidden.setBackground(this.LABEL_BACKGROUND);
        //this.noordMidden.setLayout(null);
        //this.noordMidden.setSize(this.getWidth()/3, this.NOORDBALKHEIGHT);
        //this.noordMidden.setLocation(this.getWidth()/3, this.COMBOBOXHEIGHT);
        //this.noordMidden.add(this.MSlider);
        //this.noordMidden.add(this.MLabel);
        //this.noordMidden.add(this.MText);

        
        
        this.noordRechtsHyp = new LayoutPanel();
        //this.noordRechtsHyp.setBackground(this.LABEL_BACKGROUND);
        //this.noordRechtsHyp.setLayout(null);
        //this.noordRechtsHyp.setSize(this.getWidth()/3, this.NOORDBALKHEIGHT);
        //this.noordRechtsHyp.setLocation(this.getWidth()/3*2, this.COMBOBOXHEIGHT);
        //this.noordRechtsHyp.add(this.populatieSlider);
        //this.noordRechtsHyp.add(this.populatieLabel);
        //this.noordRechtsHyp.add(this.populatieText);

        
        this.noordRechtsBV = new LayoutPanel();
        //this.noordRechtsBV.setBackground(this.LABEL_BACKGROUND);
        //this.noordRechtsBV.setLayout(null);
        //this.noordRechtsBV.setSize(this.getWidth()/3, this.NOORDBALKHEIGHT);
        //this.noordRechtsBV.setLocation(this.getWidth()/3*2, this.COMBOBOXHEIGHT);
        //this.noordRechtsBV.add(this.pSlider);
        //this.noordRechtsBV.add(this.pLabel);
        //this.noordRechtsBV.add(this.pText);

        
        
        //this.noordBalk.add(this.hyperComboBox);
        //this.noordBalk.add(this.noordLinks);
        //this.noordBalk.add(this.noordRechtsBV);
        
        //this.noordRechtsHyp.setVisible(false);
        //this.noordMidden.setVisible(false);
        //this.noordBalk.add(this.noordRechtsHyp);
        //this.noordBalk.add(this.noordMidden);
                
        //this.plaatsComponentenNoordBalk(this.getWidth());
        //super.add(this.noordBalk, BorderLayout.NORTH);
        
//GWT        
        //this.listeners = new ArrayList<ActionListener>();
	}
	
	public Canvas getCanvas()
	{
		return staafjesPanel.binomVerdGWTCanvas;
	}
	
	public void paint()
	{
		
//System.out.println("BinomVerdPanel paint");

		staafjesPanel.paint();
		nSlider.paint();
		pSlider.paint();
		MSlider.paint();
		populatieSlider.paint();
	}
	
	public void plaatsComponenten(boolean init)
	{
		int noordHoogte = 0;
		int zuidHoogte = 0;
		
		if (showNoordBalk && showHyperKeuze)
		{	noordHoogte = COMBOBOXHEIGHT;
		}
		if (showNoordBalk)
		{
			noordHoogte += NOORDBALKHEIGHT;
		}
		
		boolean bottomSpace = showTweeGrenzenKeuze || kijkOpdrachtNa;
		
		// bepaal vast even de hoogtes
		//if (showTweeGrenzenKeuze && showKansBalk)
		if (bottomSpace && showKansBalk)	
		{	zuidHoogte = CHECKBOXHEIGHT + RADIOBUTTONHEIGHT + ZUIDBALKVOFFSET;
		}
		//else if (showTweeGrenzenKeuze && !showKansBalk)
		else if (bottomSpace && !showKansBalk)
		{	zuidHoogte = CHECKBOXHEIGHT;
		}
		//else if (!showTweeGrenzenKeuze && showKansBalk)
		else if (!bottomSpace && showKansBalk)
		{	zuidHoogte = RADIOBUTTONHEIGHT;
		}
		
		if (init)
		{	
			staafjesPanel = new BVStaafjesPanel(this, this.grenzenOptie, breedte, hoogte - zuidHoogte - noordHoogte);
			add(staafjesPanel.binomVerdGWTCanvas);
			setWidgetLeftWidth(staafjesPanel.binomVerdGWTCanvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
			setWidgetTopHeight(staafjesPanel.binomVerdGWTCanvas, noordHoogte, 
					           Style.Unit.PX, hoogte - zuidHoogte - noordHoogte, Style.Unit.PX);
			
		}	
		
		// bovenste deel
		int yPos = 0;
		if (showNoordBalk && showHyperKeuze)
		{	if (init)
			{	add(hyperComboBox);
				hyperComboBox.addChangeHandler(new HyperComboCH());
			}
			setWidgetLeftWidth(hyperComboBox, breedte / 3 + 20, Style.Unit.PX, breedte / 3, Style.Unit.PX);
			setWidgetTopHeight(hyperComboBox, 0, Style.Unit.PX, COMBOBOXHEIGHT, Style.Unit.PX);
		
			yPos += COMBOBOXHEIGHT;
		}
		if (showNoordBalk)
		{
			if (init)
			{
				add(noordLinks);
				noordLinks.addStyleName(BinomVerdGWT.binomVerdGWTCss.noordpanel());
				add(noordMidden);
				noordMidden.addStyleName(BinomVerdGWT.binomVerdGWTCss.noordpanel());
				add(noordRechtsBV);
				noordRechtsBV.addStyleName(BinomVerdGWT.binomVerdGWTCss.noordpanel());
				add(noordRechtsHyp);
				noordRechtsHyp.addStyleName(BinomVerdGWT.binomVerdGWTCss.noordpanel());
				
				noordLinks.add(nLabel);
				noordLinks.add(nText);
				int xnPos = (breedte / 3 - SHORTLABELWIDTH - TEXTBOXWIDTH) / 2;
				int ynPos = 0;
				noordLinks.setWidgetLeftWidth(nLabel, xnPos, Style.Unit.PX, SHORTLABELWIDTH, Style.Unit.PX);
				noordLinks.setWidgetTopHeight(nLabel, ynPos + 7, Style.Unit.PX, TEXTBOXHEIGHT, Style.Unit.PX);
				noordLinks.setWidgetLeftWidth(nText, xnPos + SHORTLABELWIDTH, Style.Unit.PX, TEXTBOXWIDTH, Style.Unit.PX);
				noordLinks.setWidgetTopHeight(nText, ynPos + 1, Style.Unit.PX, TEXTBOXHEIGHT, Style.Unit.PX);
				
				this.nText.setReadOnly(!nVeranderbaar);
				nText.addKeyDownHandler(new TextBoxKeyDownHandler(nText));
				nText.addBlurHandler(new TextBoxBlurHandler(nText));
				
				nSlider = new Slider(this, breedte / 3 - 10, 50, 0, 0, null, "n");
				ynPos += TEXTBOXHEIGHT + 4;
				noordLinks.add(nSlider.sliderCanvas);
				noordLinks.setWidgetLeftWidth(nSlider.sliderCanvas, 0, Style.Unit.PX, breedte / 3, Style.Unit.PX);
				noordLinks.setWidgetTopHeight(nSlider.sliderCanvas, ynPos, Style.Unit.PX, 13, Style.Unit.PX);
				
				setNSlider();
				nSlider.zetEnabled(showNSlider && nVeranderbaar);
				
				noordRechtsBV.add(pLabel);
				noordRechtsBV.add(pText);
				xnPos = (breedte / 3 - SHORTLABELWIDTH - TEXTBOXWIDTH) / 2;
				ynPos = 0;
				noordRechtsBV.setWidgetLeftWidth(pLabel, xnPos, Style.Unit.PX, SHORTLABELWIDTH, Style.Unit.PX);
				noordRechtsBV.setWidgetTopHeight(pLabel, ynPos + 7, Style.Unit.PX, TEXTBOXHEIGHT, Style.Unit.PX);
				noordRechtsBV.setWidgetLeftWidth(pText, xnPos + SHORTLABELWIDTH, Style.Unit.PX, TEXTBOXWIDTH, Style.Unit.PX);
				noordRechtsBV.setWidgetTopHeight(pText, ynPos + 1, Style.Unit.PX, TEXTBOXHEIGHT, Style.Unit.PX);
				
				this.pText.setReadOnly(!pVeranderbaar);
				pText.addKeyDownHandler(new TextBoxKeyDownHandler(pText));
				pText.addBlurHandler(new TextBoxBlurHandler(pText));
				
				pSlider = new Slider(this, breedte / 3 - 10, 50, 0, 0, null, "p");
				ynPos += TEXTBOXHEIGHT + 4;
				noordRechtsBV.add(pSlider.sliderCanvas);
				noordRechtsBV.setWidgetLeftWidth(pSlider.sliderCanvas, 0, Style.Unit.PX, breedte / 3, Style.Unit.PX);
				noordRechtsBV.setWidgetTopHeight(pSlider.sliderCanvas, ynPos, Style.Unit.PX, 13, Style.Unit.PX);
				
				setPSlider();
				pSlider.zetEnabled(showPSlider && pVeranderbaar);
				
				noordMidden.add(MLabel);
				noordMidden.add(MText);
				xnPos = (breedte / 3 - (SHORTLABELWIDTH + 10) - TEXTBOXWIDTH) / 2;
				ynPos = 0;
				noordMidden.setWidgetLeftWidth(MLabel, xnPos, Style.Unit.PX, SHORTLABELWIDTH + 10, Style.Unit.PX);
				noordMidden.setWidgetTopHeight(MLabel, ynPos + 7, Style.Unit.PX, TEXTBOXHEIGHT, Style.Unit.PX);
				noordMidden.setWidgetLeftWidth(MText, xnPos + SHORTLABELWIDTH + 10, Style.Unit.PX, TEXTBOXWIDTH, Style.Unit.PX);
				noordMidden.setWidgetTopHeight(MText, ynPos + 1, Style.Unit.PX, TEXTBOXHEIGHT, Style.Unit.PX);
				
				this.MText.setReadOnly(!MVeranderbaar);
				MText.addKeyDownHandler(new TextBoxKeyDownHandler(MText));
				MText.addBlurHandler(new TextBoxBlurHandler(MText));
				
				MSlider = new Slider(this, breedte / 3 - 10 - 2 * NOORDBALKGAP, 50, 0, 0, null, "M");
				ynPos += TEXTBOXHEIGHT + 4;
				noordMidden.add(MSlider.sliderCanvas);
				noordMidden.setWidgetLeftWidth(MSlider.sliderCanvas, 0, Style.Unit.PX, breedte / 3 - 2 * NOORDBALKGAP, Style.Unit.PX);
				noordMidden.setWidgetTopHeight(MSlider.sliderCanvas, ynPos, Style.Unit.PX, 13, Style.Unit.PX);
				
				setMSlider();
				MSlider.zetEnabled(showMSlider && MVeranderbaar);				
				
				noordRechtsHyp.add(populatieLabel);
				noordRechtsHyp.add(populatieText);
				xnPos = (breedte / 3 - LONGLABELWIDTH - TEXTBOXWIDTH) / 2;
				ynPos = 0;
				noordRechtsHyp.setWidgetLeftWidth(populatieLabel, xnPos, Style.Unit.PX, LONGLABELWIDTH, Style.Unit.PX);
				noordRechtsHyp.setWidgetTopHeight(populatieLabel, ynPos + 7, Style.Unit.PX, TEXTBOXHEIGHT, Style.Unit.PX);
				noordRechtsHyp.setWidgetLeftWidth(populatieText, xnPos + LONGLABELWIDTH, Style.Unit.PX, TEXTBOXWIDTH, Style.Unit.PX);
				noordRechtsHyp.setWidgetTopHeight(populatieText, ynPos + 1, Style.Unit.PX, TEXTBOXHEIGHT, Style.Unit.PX);
				
				this.populatieText.setReadOnly(!populatieVeranderbaar);
				populatieText.addKeyDownHandler(new TextBoxKeyDownHandler(populatieText));
				populatieText.addBlurHandler(new TextBoxBlurHandler(populatieText));
				
				populatieSlider = new Slider(this, breedte / 3 - 10, 50, 0, 0, null, "populatie");
				ynPos += TEXTBOXHEIGHT + 4;
				noordRechtsHyp.add(populatieSlider.sliderCanvas);
				noordRechtsHyp.setWidgetLeftWidth(populatieSlider.sliderCanvas, 0, Style.Unit.PX, breedte / 3, Style.Unit.PX);
				noordRechtsHyp.setWidgetTopHeight(populatieSlider.sliderCanvas, ynPos, Style.Unit.PX, 13, Style.Unit.PX);
				
				setPopulatieSlider();
				populatieSlider.zetEnabled(showPopulatieSlider && populatieVeranderbaar);
				
				noordLinks.setWidgetVisible(nSlider.sliderCanvas, showNSlider && nVeranderbaar);
				noordRechtsBV.setWidgetVisible(pSlider.sliderCanvas, showPSlider && pVeranderbaar);
				noordRechtsHyp.setWidgetVisible(populatieSlider.sliderCanvas, showPopulatieSlider && populatieVeranderbaar);
				noordMidden.setWidgetVisible(MSlider.sliderCanvas, showMSlider && MVeranderbaar);

				
				
			}
			setWidgetLeftWidth(noordLinks, 0, Style.Unit.PX, breedte / 3, Style.Unit.PX);
			setWidgetTopHeight(noordLinks, yPos, Style.Unit.PX, NOORDBALKHEIGHT, Style.Unit.PX);
			setWidgetLeftWidth(noordMidden, breedte / 3 + NOORDBALKGAP, Style.Unit.PX, breedte / 3 - 2 * NOORDBALKGAP, Style.Unit.PX);
			setWidgetTopHeight(noordMidden, yPos, Style.Unit.PX, NOORDBALKHEIGHT, Style.Unit.PX);
			setWidgetLeftWidth(noordRechtsBV, 2 * breedte / 3, Style.Unit.PX, breedte / 3, Style.Unit.PX);
			setWidgetTopHeight(noordRechtsBV, yPos, Style.Unit.PX, NOORDBALKHEIGHT, Style.Unit.PX);
			setWidgetLeftWidth(noordRechtsHyp, 2 * breedte / 3, Style.Unit.PX, breedte / 3, Style.Unit.PX);
			setWidgetTopHeight(noordRechtsHyp, yPos, Style.Unit.PX, NOORDBALKHEIGHT, Style.Unit.PX);
			
			if (hypergeometrisch)
			{	setWidgetVisible(noordMidden, true);
				setWidgetVisible(noordRechtsHyp, true);
				setWidgetVisible(noordRechtsBV, false);
			}
			else
			{	setWidgetVisible(noordMidden, false);
				setWidgetVisible(noordRechtsHyp, false);
				setWidgetVisible(noordRechtsBV, true);
			}
		}
		
		// onderste deel
		yPos = hoogte;
		
		if (showTweeGrenzenKeuze)
		{
			if (init)
			{	add(grenzenBox);
				grenzenBox.addValueChangeHandler(new GrensKeuzeVCH());
			}
			setWidgetLeftWidth(grenzenBox, HOFFSET, Style.Unit.PX, grenzenBoxWidth, Style.Unit.PX);
			setWidgetTopHeight(grenzenBox, hoogte - CHECKBOXHEIGHT, Style.Unit.PX, CHECKBOXHEIGHT, Style.Unit.PX);
			
			yPos = hoogte - CHECKBOXHEIGHT;
		}
		if (kijkOpdrachtNa && kijkNaPanel != null)
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
			
			setWidgetLeftWidth(kijkNaPanel, breedte - 120, Style.Unit.PX, 120, Style.Unit.PX);
			setWidgetTopHeight(kijkNaPanel, hoogte - CHECKBOXHEIGHT, Style.Unit.PX, CHECKBOXHEIGHT, Style.Unit.PX);
			
			kijkNaPanel.setWidgetLeftWidth(kijkNaButton, 0, Style.Unit.PX, 80, Style.Unit.PX);
			kijkNaPanel.setWidgetTopHeight(kijkNaButton, 0, Style.Unit.PX, CHECKBOXHEIGHT, Style.Unit.PX);
			
//if (owner.goedKrulImage != null)
//{	
			kijkNaPanel.setWidgetLeftWidth(owner.goedKrulImage, 80, Style.Unit.PX, 30, Style.Unit.PX);
			kijkNaPanel.setWidgetTopHeight(owner.goedKrulImage, 0, Style.Unit.PX, CHECKBOXHEIGHT, Style.Unit.PX);
//}			
			
//if (owner.foutKruisImage != null)
//{	
			kijkNaPanel.setWidgetLeftWidth(owner.foutKruisImage, 80, Style.Unit.PX, 30, Style.Unit.PX);
			kijkNaPanel.setWidgetTopHeight(owner.foutKruisImage, 0, Style.Unit.PX, CHECKBOXHEIGHT, Style.Unit.PX);
//}			
			
			kijkNaPanel.setWidgetVisible(owner.goedKrulImage, false);
			kijkNaPanel.setWidgetVisible(owner.foutKruisImage, false);

		}
		if (showKansBalk)
		{
			if (showTweeGrenzenKeuze)
			{	yPos -= ZUIDBALKVOFFSET;
			}
			if (init)
			{	add(kansRadioLinks);
				add(kansRadioMidden);
				add(kansRadioRechts);
				// listeners
				kansRadioLinks.addValueChangeHandler(new KansKeuzeVCH());
				kansRadioMidden.addValueChangeHandler(new KansKeuzeVCH());
				kansRadioRechts.addValueChangeHandler(new KansKeuzeVCH());
				add(kansLabelLinks);
				add(kansLabelMidden);
				add(kansLabelRechts);
			}
			kansRadioLinks.setText(kansLabelLinksTekst());
			kansRadioMidden.setText(kansLabelMiddenTekst());
			kansRadioRechts.setText(kansLabelRechtsTekst());
			kansLabelLinks.setText(kansLabelLinksTekst());
			kansLabelMidden.setText(kansLabelMiddenTekst());
			kansLabelRechts.setText(kansLabelRechtsTekst());
			setWidgetLeftWidth(kansRadioLinks, HOFFSET, Style.Unit.PX, breedte / 3 - HOFFSET, Style.Unit.PX);
			setWidgetTopHeight(kansRadioLinks, yPos - RADIOBUTTONHEIGHT, Style.Unit.PX, RADIOBUTTONHEIGHT, Style.Unit.PX);
			setWidgetLeftWidth(kansRadioMidden, breedte / 3 + HOFFSET, Style.Unit.PX, breedte / 3 - HOFFSET, Style.Unit.PX);
			setWidgetTopHeight(kansRadioMidden, yPos - RADIOBUTTONHEIGHT, Style.Unit.PX, RADIOBUTTONHEIGHT, Style.Unit.PX);
			setWidgetLeftWidth(kansRadioRechts, 2 * breedte / 3 + HOFFSET, Style.Unit.PX, breedte / 3 - HOFFSET, Style.Unit.PX);
			setWidgetTopHeight(kansRadioRechts, yPos - RADIOBUTTONHEIGHT, Style.Unit.PX, RADIOBUTTONHEIGHT, Style.Unit.PX);
			setWidgetLeftWidth(kansLabelLinks, HOFFSET, Style.Unit.PX, breedte / 3 - HOFFSET, Style.Unit.PX);
			setWidgetTopHeight(kansLabelLinks, yPos - RADIOBUTTONHEIGHT, Style.Unit.PX, RADIOBUTTONHEIGHT, Style.Unit.PX);
			setWidgetLeftWidth(kansLabelMidden, breedte / 3 + HOFFSET, Style.Unit.PX, breedte / 3 - HOFFSET, Style.Unit.PX);
			setWidgetTopHeight(kansLabelMidden, yPos - RADIOBUTTONHEIGHT, Style.Unit.PX, RADIOBUTTONHEIGHT, Style.Unit.PX);
			setWidgetLeftWidth(kansLabelRechts, 2 * breedte / 3 + HOFFSET, Style.Unit.PX, breedte / 3 - HOFFSET, Style.Unit.PX);
			setWidgetTopHeight(kansLabelRechts, yPos - RADIOBUTTONHEIGHT, Style.Unit.PX, RADIOBUTTONHEIGHT, Style.Unit.PX);
			
			setWidgetVisible(kansRadioLinks, !tweeGrenzen);
			setWidgetVisible(kansRadioMidden, !tweeGrenzen);
			setWidgetVisible(kansRadioRechts, !tweeGrenzen);
			setWidgetVisible(kansLabelLinks, tweeGrenzen);
			setWidgetVisible(kansLabelMidden, tweeGrenzen);
			setWidgetVisible(kansLabelRechts, tweeGrenzen);
			
		}
		
	}	
	
	/**
	 * Update de kansbalk, zet JLabels neer als tweeGrenzen waar is, anders de JRadioButtons, en zet de teksten goed
	 */
//GWT do dit in plaatsCompomenten	
	public void updateKansBalk() 
	{
/*		
		if(this.tweeGrenzen) 
		{	
			this.kansBalk.remove(this.kansRadioLinks);
			this.kansBalk.remove(this.kansRadioMidden);
			this.kansBalk.remove(this.kansRadioRechts);
			
			this.kansLabelLinks.setText(this.kansLabelLinksTekst());
			this.kansLabelMidden.setText(this.kansLabelMiddenTekst());
			this.kansLabelRechts.setText(this.kansLabelRechtsTekst());
			
			this.kansBalk.add(this.kansLabelLinks);
			this.kansBalk.add(this.kansLabelMidden);
			this.kansBalk.add(this.kansLabelRechts);
			
			this.kansLabelLinks.setBackground(this.LABEL_BACKGROUND);
			this.kansLabelMidden.setBackground(this.LABEL_ACTIEF);
			this.kansLabelRechts.setBackground(this.LABEL_BACKGROUND);
			
			this.kansBalk.validate();
		}
		else 
		{
			this.kansBalk.remove(this.kansLabelLinks);
			this.kansBalk.remove(this.kansLabelMidden);
			this.kansBalk.remove(this.kansLabelRechts);
			
			this.kansRadioLinks.setText(this.kansLabelLinksTekst());			
			this.kansRadioMidden.setText(this.kansLabelMiddenTekst());
			this.kansRadioRechts.setText(this.kansLabelRechtsTekst());
						
			this.kansBalk.add(this.kansRadioLinks);
			this.kansBalk.add(this.kansRadioMidden);
			this.kansBalk.add(this.kansRadioRechts);
			
			this.kansBalk.validate();
		}
*/		
	}
	
	/**
	 * @return De string die op het linker kanslabel moet komen te staan.
	 */
	private String kansLabelLinksTekst() 
	{
		if (this.tweeGrenzen) 
		{
			int grens = this.staafjesPanel.getGrensLinks();
			double kans;
			if (this.hypergeometrisch) 
			{
				kans = this.berekenHyperKansCumulatief(0, grens-1);
			}
			else 
			{
				kans = this.berekenKansCumulatief(0, grens-1);
			}
			int hulp = (int) Math.round(10000*kans);
			kans = (double)hulp/10000;
			
			return new String("P(X<" + grens + ") = " + kans);
		}
		else 
		{
			int grens = this.staafjesPanel.getGrensRechts();
			double kans;
			if (this.hypergeometrisch) 
			{
				kans = this.berekenHyperKansCumulatief(0, grens);
			}
			else 
			{
				kans = this.berekenKansCumulatief(0, grens);
			}
			int hulp = (int)Math.round(10000*kans);
			kans = (double)hulp/10000;
			
			return new String("P(X\u2264" + grens + ") = " + kans);
		}
	}
	
	/**
	 * @return De string die op het middelste kanslabel moet komen te staan.
	 */
	private String kansLabelMiddenTekst() 
	{
		if(this.tweeGrenzen) 
		{
			double kans;
			if(this.hypergeometrisch) 
			{
				kans = this.berekenHyperKansCumulatief(this.staafjesPanel.getGrensLinks(), this.staafjesPanel.getGrensRechts());
			}
			else 
			{
				kans = this.berekenKansCumulatief(this.staafjesPanel.getGrensLinks(), this.staafjesPanel.getGrensRechts());
			}
			kans = (double)Math.round(kans*10000)/10000.0;
			return "P(" + this.staafjesPanel.getGrensLinks() + "\u2264X\u2264" + this.staafjesPanel.getGrensRechts() + ") = " + kans;
		}
		else 
		{
			double kans;
			if(this.hypergeometrisch) 
			{
				kans = this.berekenHyperKansK(this.staafjesPanel.getGrensRechts());
			}
			else 
			{
				kans = this.berekenKansK(this.staafjesPanel.getGrensRechts());
			}
			kans = (double)Math.round(kans*10000)/10000.0;
			return "P(X=" + this.staafjesPanel.getGrensRechts() + ") = " + kans;
		}
	}
	
	/**
	 * @return De string die op het rechter kanslabel moet komen te staan.
	 */
	private String kansLabelRechtsTekst() 
	{
		if(this.tweeGrenzen) 
		{
			double kans;
			if(this.hypergeometrisch) 
			{
				kans = this.berekenHyperKansCumulatief(this.staafjesPanel.getGrensRechts()+1, this.n);
			}
			else 
			{
				kans = this.berekenKansCumulatief(this.staafjesPanel.getGrensRechts()+1, this.n);
			}
			kans = (double)Math.round(kans*10000)/10000.0;
			return "P(X>" + this.staafjesPanel.getGrensRechts() + ") = " + kans;
		}
		else 
		{
			double kans;
			if (this.hypergeometrisch) 
			{
				kans = this.berekenHyperKansCumulatief(this.staafjesPanel.getGrensRechts(), this.n);
			}
			else {
				kans = this.berekenKansCumulatief(this.staafjesPanel.getGrensRechts(), this.n);
			}
			kans = (double)Math.round(kans*10000)/10000.0;
			return "P(X\u2265" + this.staafjesPanel.getGrensRechts() + ") = " + kans;
		}
	}
	
	/**
	 * Procedure om alle componenten in het kijkNaPanel goed te plaatsen
	 * @param breedte De (eventueel nieuwe) breedte van het gehele BVInteractiePanel
	 */
	
//GWT later
/*	
	private void plaatsComponentenKijkNaPanel(int breedte) {
		this.kijkNaButton.setBounds((breedte/2)-140, 4, 100, 17);
		this.vinkjeLabel.setBounds((breedte/2)-40, 0, 20, 20);
		this.kruisjeLabel.setBounds((breedte/2)-40, 0, 20, 20);
	}
*/	
	/**
	 * Procedure om alle componenten in de noordBalk goed te plaatsen
	 * @param breedte De (eventueel nieuwe) breedte van het gehele BVInteractiePanel
	 */
//GWT	
	private void plaatsComponentenNoordBalk(int breedte) 
	{
		
/*		
		int comboHeight;
		if(this.showHyperKeuze) 
		{
			comboHeight = this.COMBOBOXHEIGHT;
		}
		else 
		{
			comboHeight = 0;
		}
		
		this.noordBalk.setPreferredSize(new Dimension(breedte, this.NOORDBALKHEIGHT+comboHeight));
		
		this.noordLinks.setBounds(0,comboHeight,breedte/3-this.NOORDBALKGAP,this.NOORDBALKHEIGHT);
		this.noordMidden.setBounds(breedte/3+(int)(0.5*this.NOORDBALKGAP),comboHeight,breedte/3-this.NOORDBALKGAP,this.NOORDBALKHEIGHT);
		this.noordRechtsBV.setBounds(2*breedte/3+this.NOORDBALKGAP,comboHeight,breedte/3-this.NOORDBALKGAP,this.NOORDBALKHEIGHT);
		this.noordRechtsHyp.setBounds(2*breedte/3+this.NOORDBALKGAP,comboHeight,breedte/3-this.NOORDBALKGAP,this.NOORDBALKHEIGHT);
		
		this.hyperComboBox.setBounds(0, 0, breedte, comboHeight);
		
		//noordBalkLinks:
		this.nSlider.setLocation(0, 28);
		this.nLabel.setBounds((int)(breedte/6.0)-this.nLabel.getPreferredSize().width -10, 10, this.nLabel.getPreferredSize().width, 15);
		this.nText.setBounds((int)(breedte/6.0) -10, 7, this.nText.getPreferredSize().width, 20);
		
		//noordBalkMidden:
		this.MSlider.setLocation(0,28);
		this.MLabel.setBounds((int)(breedte/6.0)-this.MLabel.getPreferredSize().width -10, 10, this.MLabel.getPreferredSize().width, 15);
		this.MText.setBounds((int)(breedte/6.0) -10, 7, this.MText.getPreferredSize().width, 20);
		
		//noordBalkRechtsBV
		this.pSlider.setLocation(0, 28);
		this.pLabel.setBounds((int)(breedte/6.0)-this.pLabel.getPreferredSize().width -10, 10, this.pLabel.getPreferredSize().width, 15);
		this.pText.setBounds((int)(breedte/6.0) -10, 7, this.pText.getPreferredSize().width, 20);
		
		//noordBalkRechtsHyp
		this.populatieSlider.setLocation(0, 28);
		this.populatieLabel.setBounds((int)(breedte/6.0)-this.populatieLabel.getPreferredSize().width -10, 10, this.populatieLabel.getPreferredSize().width, 15);
		this.populatieText.setBounds((int)(breedte/6.0) -10, 7, this.populatieText.getPreferredSize().width, 20);
*/		
	}
	
	/**
	 * Hulpfunctie om de stand van een slider te krijgen
	 * @param slider de Slider waarvan je de stand wil weten
	 * @return op welk deel de slider staat, dus in[0;1]
	 */
	

	
	private double getPercentageFromSlider(Slider slider) 
	{
		double d = (double)(slider.geefStand()-slider.getMinimum()) / (double)(slider.getMaximum()-slider.getMinimum());
		int i = (int)(d*10000);
		return i/10000.0;
	}
	
	/**
	 * Hulpfunctie om de slider te zetten
	 * @param slider De slider die je gaat zetten
	 * @param deel Het deel van de gehele lengte van de slider waarop je hem zet, moet element zijn van [0;1];
	 */

	
	private void setSlider(Slider slider, double deel) 
	{
		slider.zetStand((int)(deel * (slider.getMaximum()-slider.getMinimum()) + slider.getMinimum()));
	}

	
	/**
	 * Update de view
	 */
	public void vernieuw() 
	{		
		if (!(this.nInvoer.isBreuk() || this.nInvoer.isRandomInput())) 
		{
			this.nInvoer.setInput(Integer.toString(this.n));
		}
		if (!(this.pInvoer.isBreuk() || this.pInvoer.isRandomInput())) 
		{
			this.pInvoer.setInput(Double.toString(this.p));
		}
		if (!(this.populatieInvoer.isBreuk() || this.populatieInvoer.isRandomInput())) 
		{
			this.populatieInvoer.setInput(Integer.toString(this.populatie));
		}
		if (!(this.MInvoer.isBreuk() || this.MInvoer.isRandomInput())) 
		{
			this.MInvoer.setInput(Integer.toString(this.M));
		}
		
		this.showRightComponentsZuidBalk();
		
		
		this.nText.setText(this.nInvoer.getInput());
		//this.nText.setEditable(this.nVeranderbaar);
		this.nText.setReadOnly(!nVeranderbaar);
		
		this.pText.setText(this.pInvoer.getInput());
		//this.pText.setEditable(this.pVeranderbaar);
		this.pText.setReadOnly(!pVeranderbaar);
		
		this.populatieText.setText(this.populatieInvoer.getInput());
		//this.populatieText.setEditable(this.populatieVeranderbaar);
		this.populatieText.setReadOnly(!populatieVeranderbaar);
		
		this.MText.setText(this.MInvoer.getInput());
		//this.MText.setEditable(this.MVeranderbaar);
		this.MText.setReadOnly(!MVeranderbaar);
		
		
		noordLinks.setWidgetVisible(nSlider.sliderCanvas, showNSlider && nVeranderbaar);
		noordRechtsBV.setWidgetVisible(pSlider.sliderCanvas, showPSlider && pVeranderbaar);
		noordRechtsHyp.setWidgetVisible(populatieSlider.sliderCanvas, showPopulatieSlider && populatieVeranderbaar);
		noordMidden.setWidgetVisible(MSlider.sliderCanvas, showMSlider && MVeranderbaar);
		
		//this.nSlider.setEditable(this.nVeranderbaar);
		//this.pSlider.setEditable(this.pVeranderbaar);
		//this.populatieSlider.setEditable(this.populatieVeranderbaar);
		//this.MSlider.setEditable(this.MVeranderbaar);
		
		this.grenzenBox.setVisible(showTweeGrenzenKeuze);
//GWT		
		//this.kijkNaPanel.setVisible(this.kijkOpdrachtNa);
		
		this.updateKansBalk();
		
		
		paint();
	}
	
	/**
	 * Aantal mogelijkheden voor een k-greep uit n
	 * @return C(n,k)
	 */
	public static double binom(int n, int k)
	{
		if(n < 0 || k < 0) {
			System.out.println("Error! n < 0 || k < 0");
			return 0.0;
		}
		else if (k>n) {
			return 0.0;
		}
		if(n == k) {
			return 1.0;
		}
		else {
			double[] b = new double[n+1];
			b[0] = 1;
			for(int i=1 ; i<n+1 ; i++)
			{	b[i] = 1;
				for(int j=i-1 ; j>0 ; j--)
				{	b[j] += b[j-1];
				}
			}
			return b[k];
		}
	}
	
	/**
	 * komt neer op BinomPDF
	 * @param k het aantal te behalen successen
	 * @return P(X=k)
	 */
	public double berekenKansK(int k) 
	{
		if (k > this.n) {
			return 0.0;
		}
		return BinomVerdPanel.binom(this.n, k) * (double)Math.pow(this.p,k) * (double)Math.pow(1-this.p, this.n-k);
	}
	
	/**
	 * Komt neer op BinomCDF
	 * @return P(van <= X <= tot)
	 */
	public double berekenKansCumulatief(int van, int tot) 
	{
		if(van <= 0 && tot >= this.n) { //als alles meetelt is de som dus 1
			return 1.0;
		}
		else {
			double som = 0.0;
			for (int count = van; count <= Math.min(tot, this.n); count++) {
				som += this.berekenKansK(count);
			}
			return som;
		}
	}
	
	/**
	 * Bepaal de kans op k successen in een hypergeometrische verdeling
	 * @param k Het aantal te behalen successen
	 * @return P(X = k)
	 */
	public double berekenHyperKansK(int k) 
	{
		if(k > this.M) 
		{
			return 0.0;
		}
		else {
			return BinomVerdPanel.binom(this.M, k) * BinomVerdPanel.binom(this.populatie - this.M, this.n - k) / BinomVerdPanel.binom(this.populatie, this.n);
		}
	}
	
	/**
	 * Bepaal de kans op een aantal successen uit [van;tot] in een hypergeometrische verdeling
	 * @return P(van <= X <= tot)
	 */
	public double berekenHyperKansCumulatief(int van, int tot) 
	{
		double som = 0.0;
		for(int count = van; count <= tot; count++) 
		{
			som += this.berekenHyperKansK(count);
		}
		return som;
	}
	
	public BVStaafjesPanel getStaafjesPanel() 
	{
		return this.staafjesPanel;
	}
	
//GWT
/*	
	public void addNSliderListener(ActionListener al) {
		this.nSlider.addActionListener(al);
	}
*/	
	public double getP() 
	{
		return this.p;
	}
	public int getM() 
	{
		return this.M;
	}
	public int getPopulatie() 
	{
		return this.populatie;
	}
	
	public void setP(double p) 
	{
		if(p >= 0 && p <= 1) 
		{
			this.p = p;
		}
		this.vernieuw();
	}
	public int getN() 
	{
		return this.n;
	}
	public void setNVeranderbaar(boolean b) 
	{
		this.nVeranderbaar = b;
		this.nText.setReadOnly(!nVeranderbaar);
		//this.vernieuw();
	}
	public void setPVeranderbaar(boolean b) 
	{
		this.pVeranderbaar = b;
		this.pText.setReadOnly(!pVeranderbaar);
		//this.vernieuw();
	}
	public void setMVeranderbaar(boolean b) {
		this.MVeranderbaar = b;
		this.MText.setReadOnly(!MVeranderbaar);
		//this.vernieuw();
	}
	public void setPopulatieVeranderbaar(boolean b) 
	{
		this.populatieVeranderbaar = b;
		this.populatieText.setReadOnly(!populatieVeranderbaar);
		//this.vernieuw();
	}
	public void setShowXAs(boolean b) 
	{
		this.staafjesPanel.setShowXAs(b);
	}
	public void setShowYAs(boolean b) 
	{
		this.staafjesPanel.setShowYAs(b);
	}
	public void setGrenzenOptie(GrenzenOptie grenzenOptie) 
	{
		this.grenzenOptie = grenzenOptie;
		this.staafjesPanel.setGrenzenOptie(grenzenOptie);
		
		if (this.grenzenOptie == GrenzenOptie.LINKS) 
		{
			//this.kansRadioLinks.setSelected(true);
			//this.kansRadioLinks.setValue(true, false);
			kansRadioLinks.setStyleName("radiobuttonon");
			kansRadioMidden.setStyleName("radiobutton");
			kansRadioRechts.setStyleName("radiobutton");
//GWT (3)			
			//this.kansRadioLinks.setBackground(this.LABEL_ACTIEF);
			//this.kansRadioMidden.setBackground(this.LABEL_BACKGROUND);
			//this.kansRadioRechts.setBackground(this.LABEL_BACKGROUND);
		}
		if (this.grenzenOptie == GrenzenOptie.GELIJK) 
		{
			//this.kansRadioMidden.setSelected(true);
			//this.kansRadioMidden.setValue(true, false);
			kansRadioLinks.setStyleName("radiobutton");
			kansRadioMidden.setStyleName("radiobuttonon");
			kansRadioRechts.setStyleName("radiobutton");

//GWT (3)			
			//this.kansRadioLinks.setBackground(this.LABEL_BACKGROUND);
			//this.kansRadioMidden.setBackground(this.LABEL_ACTIEF);
			//this.kansRadioRechts.setBackground(this.LABEL_BACKGROUND);
		}
		if (this.grenzenOptie == GrenzenOptie.RECHTS) 
		{
			//this.kansRadioRechts.setSelected(true);
			//this.kansRadioRechts.setValue(true, false);
			kansRadioLinks.setStyleName("radiobutton");
			kansRadioMidden.setStyleName("radiobutton");
			kansRadioRechts.setStyleName("radiobuttonon");

//GWT (3)			
			//this.kansRadioLinks.setBackground(this.LABEL_BACKGROUND);
			//this.kansRadioMidden.setBackground(this.LABEL_BACKGROUND);
			//this.kansRadioRechts.setBackground(this.LABEL_ACTIEF);
		}
	}
	public void setShowMSlider(boolean b) 
	{
		this.showMSlider = b;
		this.vernieuw();
	}
	public void setShowPopulatieSlider(boolean b) 
	{
		this.showPopulatieSlider = b;
		this.vernieuw();
	}
	public void setShowNSlider(boolean b) 
	{
		this.showNSlider = b;
		this.vernieuw();
	}
	public void setShowPSlider(boolean b) 
	{
		this.showPSlider = b;
		this.vernieuw();
	}
	public void setShowGrensSlider(boolean b) 
	{
		this.staafjesPanel.setShowGrensSlider(b);
	}
	
	public void setTweeGrenzen(boolean tweeGrenzen) 
	{
		this.tweeGrenzen = tweeGrenzen;
		//this.grenzenBox.setSelected(tweeGrenzen);
		this.grenzenBox.setValue(tweeGrenzen, false);
		this.staafjesPanel.setTweeGrenzen(tweeGrenzen);
		this.updateKansBalk();
		//this.staafjesPanel.bepaalGrenzenMetSlider();
	}
	
	/**
	 * Kies tussen binomiale en hypergeometrische verdeling
	 * @param hypergeometrisch true voor hypergeometrisch, false voor binomiaal
	 */
	private void setHypergeometrisch(boolean hypergeometrisch) 
	{
		this.hypergeometrisch = hypergeometrisch;
		if (this.hypergeometrisch) 
		{
			this.hyperComboBox.setSelectedIndex(1);
		}
		else 
		{
			this.hyperComboBox.setSelectedIndex(0);
		}
		if (this.hypergeometrisch) 
		{
//GWT (3)			
			//this.noordRechtsBV.setVisible(false);
			//this.noordRechtsHyp.setVisible(true);
			//this.noordMidden.setVisible(true);
		}
		else 
		{
//GWT (3)			
			//this.noordMidden.setVisible(false);
			//this.noordRechtsHyp.setVisible(false);
			//this.noordRechtsBV.setVisible(true);
		}
		
		plaatsComponenten(false);
	}
	
	public boolean getHypergeometrisch() 
	{
		return this.hypergeometrisch;
	}

	public void setShowHyperKeuze(boolean b) 
	{
		this.showHyperKeuze = b;
		//this.hyperComboBox.setVisible(this.showHyperKeuze);
		//this.plaatsComponentenNoordBalk(this.getWidth());
	}


	
	public void setShowNoordBalk (boolean show) 
	{
		this.showNoordBalk = show;
		
		//if(show) 
		//{
		//	this.remove(this.noordBalk);
		//	this.add(this.noordBalk, BorderLayout.NORTH);
		//}
		//else 
		//{
		//	this.remove(this.noordBalk);
		//}
		//this.revalidate();
		//this.repaint();
	}
	
	
	private void setMSlider() 
	{
		setSlider(MSlider, 
				(double)(M-BinomVerdPanel.POPULATIE_MIN)/(double)(BinomVerdPanel.POPULATIE_MAX - BinomVerdPanel.POPULATIE_MIN));
	}
	
	
	private void setNSlider() 
	{
		setSlider(nSlider, 
				(double)(n-BinomVerdPanel.N_MIN)/(double)(BinomVerdPanel.N_MAX - BinomVerdPanel.N_MIN));
	}
	
	
	private void setPSlider() 
	{
		this.setSlider(this.pSlider, this.p);
	}
	

	private void setPopulatieSlider() 
	{
		this.setSlider(this.populatieSlider, (double)(this.populatie)/(double)(BinomVerdPanel.POPULATIE_MAX));
	}
	
	
	private void setM(int M) 
	{
		if (M >= 0) 
		{
			this.M = M;
			
			//kijk of M niet groter wordt dan de gehele populatie
			if(this.M > this.populatie) 
			{
				if (this.populatieVeranderbaar) 
				{
					this.setPopulatie(this.M);
					this.setPopulatieSlider();
				}
				else 
				{
					this.M = this.populatie;
					this.setMSlider();
				}
			}
			
			vernieuw();
		}
	}
	
	private void setPopulatie(int populatie) 
	{
		if (populatie > 0) 
		{
			this.populatie = populatie;
			
			//controleer of de populatie niet kleiner wordt dan een onveranderbaar succesdeel,
			//of kleiner wordt dan een onveranderbare n.
			if ((this.populatie < this.M && !this.MVeranderbaar) || (this.populatie < this.n && !this.nVeranderbaar)) 
			{
				this.populatie = Math.max(this.n, this.M);
				this.setPopulatieSlider();
			}
			else 
			{
				if (this.populatie < this.M) 
				{
					this.setM(this.populatie);
					this.setMSlider();
				}
				if(this.n > this.populatie) 
				{
					this.setN(this.populatie);
					this.setNSlider();
				}
			}
		
			vernieuw();
		}
	}

	public void setKijkOpdrachtNa(boolean b) 
	{
		this.kijkOpdrachtNa = b;
		this.vernieuw();
	}
	
/*	
	public void setN(int n) 
	{
		if(n >= 0) 
		{
			this.n = n;
			
			//kijk of n niet groter wordt dan de gehele populatie
			if(this.n > this.populatie) 
			{
				//kijk of je de populatie wel kunt aanpassen
				if(this.populatieVeranderbaar) 
				{
					this.setPopulatie(this.n);
					this.setPopulatieSlider();
				}
				else {
					this.n = this.populatie;
					this.setNSlider();
				}
			}
			
			this.staafjesPanel.berekenStaafBreedte();
			this.staafjesPanel.bepaalGrenzenMetSlider();
			this.vernieuw();
		}
		
	}
*/	
	public void setN(int n) 
	{
		if(n >= 0) 
		{
			this.n = n;
			
			//kijk of n niet groter wordt dan de gehele populatie
			if(this.n > this.populatie) 
			{
				//kijk of je de populatie wel kunt aanpassen
				if(this.populatieVeranderbaar) 
				{
					this.setPopulatie(this.n);
					this.setPopulatieSlider();
				}
				else {
					this.n = this.populatie;
					this.setNSlider();
				}
			}
			
			this.staafjesPanel.berekenStaafBreedte();
			this.staafjesPanel.bepaalGrenzenMetSlider();
			this.vernieuw();
		}
		
	}

	/**
	 * Procedure om de juiste componenten toe te voegen aan de zuidBalk;
	 */
//GWT opnieuw
	private void showRightComponentsZuidBalk() 
	{
/*		
		this.remove(this.keuzeBalk);
		this.remove(this.zuidBalk);
		this.remove(this.kansBalk);
		this.zuidBalk.removeAll();
		
		if(this.showKansBalk) 
		{
			if(this.showTweeGrenzenKeuze || this.kijkOpdrachtNa) 
			{
				this.zuidBalk.add(this.kansBalk);
				this.zuidBalk.add(this.keuzeBalk);
				this.add(this.zuidBalk, BorderLayout.SOUTH);
			}
			else 
			{
				this.add(this.kansBalk, BorderLayout.SOUTH);
			}
		}
		else 
		{
			if(this.showTweeGrenzenKeuze || this.kijkOpdrachtNa) 
			{
				this.add(this.keuzeBalk, BorderLayout.SOUTH);
			}
		}
		this.revalidate();
		this.repaint();
*/		
	}
	
	/**
	 * Verander of de kansenbalk zichtbaar is of niet
	 * @param show true als kansenbalk zichtbaar moet zijn, anders false
	 */
	public void setShowKansBalk(boolean show) 
	{
		this.showKansBalk = show;
		this.vernieuw();
	}
	
	/**
	 * Verander of de keuze tussen één en twee grenzen zichtbaar is of niet
	 * @param show true als keuze zichtbaar moet zijn, anders false
	 */
	public void setShowTweeGrenzenKeuze(boolean show) 
	{
		this.showTweeGrenzenKeuze = show;
		this.vernieuw();
	}
	
	/**
	 * Verwerk een verandering in het nTextField
	 */	
	private void nTextUpdate() 
	{
		BVInvoer invoer = new BVInvoer(this.nText.getText());
		if (invoer.isValidIntInput()) 
		{
			this.nInvoer.setInput(this.nText.getText());
			if (!this.nInvoer.isRandomInput()) 
			{
				try 
				{
					this.setN((int)Math.round(Double.parseDouble(this.nInvoer.getInput())));
				}
				catch (NumberFormatException e)
				{
					System.out.println("NumberFormatException in nTextUpdate! " + e.toString());
				}
			}
			this.staafjesPanel.bepaalGrenzenMetSlider();
			this.staafjesPanel.updateSuccessenSliderPosition();
			
		}
		else 
		{
			this.nText.setText(this.nInvoer.getInput());
		}
		this.setNSlider();
		
		paint();
		plaatsComponenten(false);
	}
	/**
	 * Verwerk een verandering in het pTextField
	 */
	private void pTextUpdate() 
	{
		BVInvoer invoer = new BVInvoer(this.pText.getText());
		double newP;
		if(invoer.isValidDoubleInput()) 
		{
			this.pInvoer.setInput(this.pText.getText());
			if (!this.pInvoer.isRandomInput()) 
			{
				if (!this.pInvoer.isBreuk()) 
				{
					try 
					{
						newP = Double.parseDouble(this.pInvoer.getInput());
						if(newP >= 0.0 && newP <= 1.0) 
						{
							this.p = newP;
						}
					}
					catch (NumberFormatException e){
						System.out.println("NumberFormatException in pTextUpdate! " + e.toString());
					}
				}
				else {
					try {
						System.out.println("P is " + Double.parseDouble(this.pInvoer.getTellerString()) + "/" + Double.parseDouble(this.pInvoer.getNoemerString()));
						newP = Double.parseDouble(this.pInvoer.getTellerString()) / Double.parseDouble(this.pInvoer.getNoemerString());
						if(newP >= 0.0 && newP <= 1.0) {
							this.p = newP;
						}
					}
					catch (NumberFormatException e) {
						System.out.println("NumberFormatException in pTextUpdate! " + e.toString());
					}
				}
			}
		}
		else {
			this.pText.setText(this.pInvoer.getInput());
		}
		this.setPSlider();
		
		paint();
		plaatsComponenten(false);
	}
	
	/**
	 * Verwerk een verandering in het populatieTextField
	 */
	private void populatieTextUpdate() {
		BVInvoer invoer = new BVInvoer(this.populatieText.getText());
		if(invoer.isValidIntInput()) {
			this.populatieInvoer.setInput(this.populatieText.getText());
			if(!this.populatieInvoer.isRandomInput()) {
				try {
					this.setPopulatie((int)Math.round(Double.parseDouble(this.populatieInvoer.getInput())));
				}
				catch (NumberFormatException e){
					System.out.println("NumberFormatException in populatieTextUpdate! " + e.toString());
				}
			}
			this.staafjesPanel.bepaalGrenzenMetSlider();
		}
		else {
			this.populatieText.setText(this.populatieInvoer.getInput());
		}
		this.setPopulatieSlider();
		
		paint();
		plaatsComponenten(false);
	}
	
	/**
	 * Verwerk verandering in MTextField
	 */
	private void MTextUpdate() {
		BVInvoer invoer = new BVInvoer(this.MText.getText());
		if(invoer.isValidIntInput()) {
			this.MInvoer.setInput(this.MText.getText());
			if(!this.MInvoer.isRandomInput()) {
				try {
					this.setM((int)Math.round(Double.parseDouble(this.MInvoer.getInput())));
				}
				catch (NumberFormatException e){
					System.out.println("NumberFormatException in MTextUpdate! " + e.toString());
				}
			}
			this.staafjesPanel.bepaalGrenzenMetSlider();
		}
		else 
		{
			this.MText.setText(this.MInvoer.getInput());
		}
		this.setMSlider();
		
		paint();
		plaatsComponenten(false);
	}
	
/*	
	public void focusGained(FocusEvent e) {

		//niet nodig, implementatie voor interface
	}
*/
/*	
	public void focusLost(FocusEvent e) {
		if(e.getSource() == this.nText) {
			this.nTextUpdate();
			this.vernieuw();
		}
		if(e.getSource() == this.pText) {
			this.pTextUpdate();
			this.vernieuw();
		}
		if(e.getSource() == this.MText) {
			this.MTextUpdate();
			this.vernieuw();
		}
		if(e.getSource() == this.populatieText) {
			this.populatieTextUpdate();
			this.vernieuw();
		}
	}
*/	
	/**
	 * Verwerk ActionEvents
	 */
	
	class HyperComboCH implements ChangeHandler
	{
		public void onChange(ChangeEvent e)
		{
			if (e.getSource() == hyperComboBox) 
			{
				setHypergeometrisch(hyperComboBox.getSelectedIndex() == 1);
				
				staafjesPanel.paint();
				
				plaatsComponenten(false);
			}

		}
	}

	class GrensKeuzeVCH implements ValueChangeHandler<Boolean>
	{	//public void actionPerformed(ActionEvent e)
		public void onValueChange(ValueChangeEvent<Boolean> e)
		{
			if (e.getSource() == grenzenBox) 
			{
				tweeGrenzen = grenzenBox.getValue();
				staafjesPanel.setTweeGrenzen(tweeGrenzen);
				staafjesPanel.paint();
				
				plaatsComponenten(false);
			}
		}
	}
	
	class KansKeuzeVCH implements ValueChangeHandler<Boolean>
	{	//public void actionPerformed(ActionEvent e)
		public void onValueChange(ValueChangeEvent<Boolean> e)
		{
			if (e.getSource() == kansRadioLinks) 
			{
				setGrenzenOptie(GrenzenOptie.LINKS);
			}
			if (e.getSource() == kansRadioMidden) 
			{
				setGrenzenOptie(GrenzenOptie.GELIJK);
			}
			if (e.getSource() == kansRadioRechts) 
			{
				setGrenzenOptie(GrenzenOptie.RECHTS);
			}
			
			changed();
		}
	}
	
	class TextBoxKeyDownHandler implements KeyDownHandler
	{	
		TextBox inputTextField;
		
		public TextBoxKeyDownHandler(TextBox input)
		{	inputTextField = input;
		}
		
		public void onKeyDown(KeyDownEvent e)
		{
			
//System.out.println("okd");

			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
//System.out.println("ke");
				//String text = inputTextField.getText();
			
				if (inputTextField == nText)
				{	nTextUpdate();
				}
				else if (inputTextField == pText)
				{	pTextUpdate();
				}
				else if (inputTextField == MText)
				{	MTextUpdate();
				}
				else if (inputTextField == populatieText)
				{	populatieTextUpdate();
				}
				
				changed();
			
			} //if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			
						
		} // onKeyDown 
		
	}

	class TextBoxBlurHandler implements BlurHandler
	{	
		TextBox inputTextField;
		
		public TextBoxBlurHandler(TextBox input)
		{	inputTextField = input;
		}
		
		public void onBlur(BlurEvent e)
		{
			
//System.out.println("okd");

			//if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			//{
//System.out.println("ke");
				//String text = inputTextField.getText();
			
				if (inputTextField == nText)
				{	nTextUpdate();
				}
				else if (inputTextField == pText)
				{	pTextUpdate();
				}
				else if (inputTextField == MText)
				{	MTextUpdate();
				}
				else if (inputTextField == populatieText)
				{	populatieTextUpdate();
				}
			
			//} //if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			
				changed();
		} // onBlur 
		
	}

	public void processNSlider(boolean finished)
	{
		setN((int)(getPercentageFromSlider(nSlider)*(BinomVerdPanel.N_MAX - BinomVerdPanel.N_MIN) + BinomVerdPanel.N_MIN));
		this.staafjesPanel.bepaalGrenzenMetSlider();
		paint();
		if (!this.nInvoer.isRandomInput())
		{
			this.nInvoer.setInput(Integer.toString(this.n));
		}
		if (finished) 
		{	staafjesPanel.updateSuccessenSliderPosition();
			paint();
		}
		plaatsComponenten(false);
	}
	
	public void processPSlider()
	{
		//this.p = this.getPercentageFromSlider(this.pSlider);
		this.setP(this.getPercentageFromSlider(this.pSlider));
		if (!this.pInvoer.isRandomInput()) 
		{
			this.pInvoer.setInput(Double.toString(this.p));
		}
		
		paint();
		plaatsComponenten(false);
	}
	
	
	public void processMSlider()
	{	this.setM((int)(this.getPercentageFromSlider(this.MSlider)*(BinomVerdPanel.POPULATIE_MAX)));
		if (!this.MInvoer.isRandomInput()) 
		{ this.MInvoer.setInput(Integer.toString(this.M));
		}
		
		paint();
		plaatsComponenten(false);
	}	
	
	public void processPopulatieSlider()
	{
		setPopulatie((int)(getPercentageFromSlider(populatieSlider)*(BinomVerdPanel.POPULATIE_MAX - BinomVerdPanel.POPULATIE_MIN) + BinomVerdPanel.POPULATIE_MIN));
		if (!this.populatieInvoer.isRandomInput()) 
		{
			this.populatieInvoer.setInput(Integer.toString(this.populatie));
		}
		
		paint();
		plaatsComponenten(false);
		
	}
	
	public void processGrensSlider(boolean finished)
	{
//System.out.println("BVP processGrensSlider");		
		staafjesPanel.processGrensSlider(finished);
		plaatsComponenten(false);
	}
	
	public void processTweeGrenzenSlider(boolean finished)
	{
		staafjesPanel.processTweeGrenzenSlider(finished);
		plaatsComponenten(false);
	}

//GWT
/*	
	public void actionPerformed(ActionEvent arg0) {
		
		if (arg0.getSource() == this.nSlider) 
		{
			this.setN((int)(this.getPercentageFromSlider(this.nSlider)*(BVInteractiePanel.N_MAX - BVInteractiePanel.N_MIN) + BVInteractiePanel.N_MIN));
			this.staafjesPanel.bepaalGrenzenMetSlider();
			if(!this.nInvoer.isRandomInput()) {
				this.nInvoer.setInput(Integer.toString(this.n));
			}
			if(arg0.getActionCommand().equals("stop")) {
				this.staafjesPanel.updateSuccessenSliderPosition();
			}
		}		
		if (arg0.getSource() == this.pSlider) {
			this.p = this.getPercentageFromSlider(this.pSlider);
			if(!this.pInvoer.isRandomInput()) {
				this.pInvoer.setInput(Double.toString(this.p));
			}
		}
		if (arg0.getSource() == this.MSlider) {
			this.setM((int)(this.getPercentageFromSlider(this.MSlider)*(BVInteractiePanel.POPULATIE_MAX)));
			if(!this.MInvoer.isRandomInput()) {
				this.MInvoer.setInput(Integer.toString(this.M));
			}
			
		}
		if(arg0.getSource() == this.populatieSlider) {
			this.setPopulatie((int)(this.getPercentageFromSlider(this.populatieSlider)*(BVInteractiePanel.POPULATIE_MAX - BVInteractiePanel.POPULATIE_MIN) + BVInteractiePanel.POPULATIE_MIN));
			if(!this.populatieInvoer.isRandomInput()) {
				this.populatieInvoer.setInput(Integer.toString(this.populatie));
			}
		}
		
		if(arg0.getSource() == this.kijkNaButton) {
			this.kijkNa();
		}
		this.vernieuw();
	}
*/	
	/**
	 * Gekopiëerd uit Normale Verdeling, kleine aanpassingen gemaakt
	 */
	public static double substitueerRandom(double defaultValue, String s, String[] randomVars, HashMap randomValues) {
		System.out.println("Randomvalues: " + randomValues);
		double d = Double.NaN;
		s = s.substring(1, s.length() - 1);
		//String[] delen = StringUtils.split(s, "/");
		String[] delen = s.split("/");
		int decFactor = 1;
		for (int j = 0 ; j < randomVars.length; j++) {
			if (randomVars[j].equals(delen[0])) {
				d = ((Integer) randomValues.get(randomVars[j])).intValue();
			}
		}
		if (delen.length > 1) {
			decFactor = Integer.parseInt(delen[1]);
			d = d / decFactor;
		}
		if (Double.isNaN(d)) {
			d = defaultValue;
		}
		return d;
	}
	
	//====================================================================================================
	//===========InteractiePanel Interface methoden=======================================================
	//====================================================================================================
	
	/**
	 * Geef anderen de mogelijkheid om naar dit BVInteractiePanel te luisteren
	 * -> vuurt allen event bij nakijken
	 */
//GWT??
/*	
	public void addActionListener(ActionListener al) 
	{
		this.listeners.add(al);
	}
*/
	public void destroy() {
		
	}

	public int geefAsHoogte() {
		return 0;
	}
		
	/**
	 * Methode om een EditPanel op te vragen
	 * @return een nieuw BVInteractieEditPanel
	 */
	//public InteractieEditPanel getEditPanel() {
	//	return new BVInteractieEditPanel();
	//}
	
	/**
	 * Wordt aangeroepen door getEditState van BVInteractieEditPanel, omdat een deel
	 * van de editgegevens in BVInteractiePanel staan.
	 */
	public HashMap getEditState() 
	{
		return this.getState();
	}
	
	/**
	 * Zet de oude Editgegevens voor BVInteractiePanel als je met BVInteractieEditPanel 
	 * nieuwe editgegevens gaat maken.
	 */
	public void setEditState(HashMap b) 
	{
		this.setState(b);
	}

	public int getIpId() {
		return 0;
	}
	
	/**
	 * @return De huidige score voor deze opgave
	 */
/*	
	public int getScore() {
		return this.score;
	}
*/	
	/**
	 * @return De te behalen score voor deze opgave
	 */
/*	
	public int getScoreMax() {
		return this.maxScore;
	}
*/
	/**
	 * Geeft de volledige toestand in de vorm van een Hashtable
	 */
	public HashMap getState() 
	{
		HashMap h = new HashMap();
		
		h.put("n", new Integer(this.n));
		h.put("p", new Double(this.p));
		h.put("M", new Integer(this.M));
		h.put("populatie", new Integer(this.populatie));
		
		//h.put("showXAs", new Boolean(this.staafjesPanel.getShowXAs()));
		//h.put("showYAs", new Boolean(this.staafjesPanel.getShowYAs()));
				
		//h.put("nVeranderbaar", new Boolean(this.nVeranderbaar));
		//h.put("pVeranderbaar", new Boolean(this.pVeranderbaar));
		//h.put("populatieVeranderbaar", new Boolean(this.populatieVeranderbaar));
		//h.put("MVeranderbaar", new Boolean(this.MVeranderbaar));
		
		h.put("nInvoer", this.nInvoer.getInput());
		h.put("pInvoer", this.pInvoer.getInput());
		h.put("MInvoer", this.MInvoer.getInput());
		h.put("populatieInvoer", this.populatieInvoer.getInput());
		
		//h.put("showNSlider", new Boolean(this.showNSlider));
		//h.put("showPSlider", new Boolean(this.showPSlider));
		//h.put("showMSlider", new Boolean(this.showMSlider));
		//h.put("showPopulatieSlider", new Boolean(this.showPopulatieSlider));
		
		h.put("grensLinks", new Integer(this.staafjesPanel.getGrensLinks()));
		h.put("grensRechts", new Integer(this.staafjesPanel.getGrensRechts()));
//System.out.println("put GL = " + this.staafjesPanel.getGrensLinks());
//System.out.println("put GR = " + this.staafjesPanel.getGrensRechts());
		
		if (this.grenzenOptie == GrenzenOptie.LINKS) 
		{
			h.put("grenzenOptie", new Integer(0));
		}
		if (this.grenzenOptie == GrenzenOptie.GELIJK) 
		{
			h.put("grenzenOptie", new Integer(1));
		}
		if (this.grenzenOptie == GrenzenOptie.RECHTS) 
		{
			h.put("grenzenOptie", new Integer(2));
		}
		h.put("tweeGrenzen", new Boolean(this.tweeGrenzen));
		
		//h.put("showGrensSlider", new Boolean(this.staafjesPanel.getShowGrensSlider()));
		//h.put("showNoordBalk", new Boolean(this.showNoordBalk));
		//h.put("showKansBalk", new Boolean(this.showKansBalk));
		//h.put("showTweeGrenzenKeuze", new Boolean(this.showTweeGrenzenKeuze));
		//h.put("showHyperKeuze", new Boolean(this.showHyperKeuze));
		
		h.put("hypergeometrisch", new Boolean(this.hypergeometrisch));
		
		return h;
	}

	/**
	 * Zet de toestand naar de inhoud van de hashtable b
	 */
	public void setState(Map map) 
	{
		ObjectMap b = JSONUtilities.wrapMap(map);
		
		if (b.containsKey("n")) 
		{	this.n = b.getInt("n");
		}
		if (b.containsKey("p")) 
		{	this.p = b.getDouble("p");
		}
		if (b.containsKey("M")) 
		{	this.M = b.getInt("M");
		}
		if (b.containsKey("populatie")) 
		{	this.populatie = b.getInt("populatie");
		}
		if (b.containsKey("showXAs")) {
			this.staafjesPanel.setShowXAs(b.getBoolean("showXAs"));
		}
		if (b.containsKey("showYAs")) {
			this.staafjesPanel.setShowYAs(b.getBoolean("showYAs"));
		}
		if (b.containsKey("nVeranderbaar")) {
			this.nVeranderbaar = b.getBoolean("nVeranderbaar");
		}
		if (b.containsKey("pVeranderbaar")) {
			this.pVeranderbaar = b.getBoolean("pVeranderbaar");
		}
		if (b.containsKey("MVeranderbaar")) {
			this.MVeranderbaar = b.getBoolean("MVeranderbaar");
		}
		if (b.containsKey("populatieVeranderbaar")) {
			this.populatieVeranderbaar = b.getBoolean("populatieVeranderbaar");
		}
		if (b.containsKey("nInvoer")) {
			this.nInvoer = new BVInvoer(b.getString("nInvoer"));
		}
		if (b.containsKey("pInvoer")) {
			this.pInvoer = new BVInvoer(b.getString("pInvoer"));
		}
		if (b.containsKey("MInvoer")) {
			this.MInvoer = new BVInvoer(b.getString("MInvoer"));
		}
		if (b.containsKey("populatieInvoer")) {
			this.populatieInvoer = new BVInvoer(b.getString("populatieInvoer"));
		}
		if (b.containsKey("showNSlider")) {
			this.showNSlider = b.getBoolean("showNSlider");
		}
		if (b.containsKey("showPSlider")) {
			this.showPSlider = b.getBoolean("showPSlider");
		}
		if (b.containsKey("showMSlider")) {
			this.showMSlider = b.getBoolean("showMSlider");
		}
		if (b.containsKey("showPopulatieSlider")) {
			this.showPopulatieSlider = b.getBoolean("showPopulatieSlider");
		}
		if (b.containsKey("showGrensSlider")) {
			this.staafjesPanel.setShowGrensSlider(b.getBoolean("showGrensSlider"));
		}
		
		if (b.containsKey("tweeGrenzen")) 
		{
			this.tweeGrenzen = b.getBoolean("tweeGrenzen");
			this.setTweeGrenzen(this.tweeGrenzen);
		}
		
		if (b.containsKey("grensLinks")) {
			this.staafjesPanel.setGrensLinks(b.getInt("grensLinks"));
//System.out.println("get GL = " + this.staafjesPanel.getGrensLinks());
			
		}
		if (b.containsKey("grensRechts")) {
			this.staafjesPanel.setGrensRechts(b.getInt("grensRechts"));
//System.out.println("get GR = " + this.staafjesPanel.getGrensRechts());			
		}
		
		if (b.containsKey("grenzenOptie")) {
			int optie = b.getInt("grenzenOptie");
			if (optie == 0) {
				this.setGrenzenOptie(GrenzenOptie.LINKS);
			}
			if (optie == 1) {
				this.setGrenzenOptie(GrenzenOptie.GELIJK);
			}
			if (optie == 2) {
				this.setGrenzenOptie(GrenzenOptie.RECHTS);
			}
		}
//		if (b.containsKey("tweeGrenzen")) 
//		{
//			this.tweeGrenzen = b.getBoolean("tweeGrenzen");
//			this.setTweeGrenzen(this.tweeGrenzen);
//		}
		
		if(b.containsKey("showKansBalk")) 
		{
			this.setShowKansBalk(b.getBoolean("showKansBalk"));
		}
		if(b.containsKey("showNoordBalk")) 
		{
			this.setShowNoordBalk(b.getBoolean("showNoordBalk"));
		}
		if(b.containsKey("showTweeGrenzenKeuze")) {
			this.setShowTweeGrenzenKeuze(b.getBoolean("showTweeGrenzenKeuze"));
		}
		if(b.containsKey("showHyperKeuze")) 
		{
			this.setShowHyperKeuze(b.getBoolean("showHyperKeuze"));
		}
		if(b.containsKey("hypergeometrisch")) {
			this.setHypergeometrisch(b.getBoolean("hypergeometrisch"));
		}

		plaatsComponenten(false);
		
		this.vernieuw();
		this.setMSlider();
		this.setNSlider();
		this.setPopulatieSlider();
		this.setPSlider();
		
		paint();
	}
	
	public boolean isCorrect() {
		return this.score == this.maxScore;
	}
	
	public boolean isFout() {
		return this.score != this.maxScore;
	}
	
	/**
	 * Vergelijk de ingevulde waarden met het opgegeven antwoordmodel, zet de score in this.score en vuur een actionEvent naar alle listeners.
	 */
	
    public void changed()
    {
	   	if (kijkOpdrachtNa) 
		{
	   		
//System.out.println("BVPanel changed");

    		score = 0;
	   		kijkNaPanel.setWidgetVisible(owner.goedKrulImage, false);
	   		kijkNaPanel.setWidgetVisible(owner.foutKruisImage, false);

	   		owner.changed();
		}
	}

	
	public void kijkNa() 
	{
		boolean correct = true;
		if (this.kijkOpdrachtNa) 
		{
			if (this.kijkGrenzenNa) 
			{
				int links, rechts;
				if (this.tweeGrenzen) 
				{
					links = this.staafjesPanel.getGrensLinks();
					rechts = this.staafjesPanel.getGrensRechts();
				}
				else 
				{
					if (this.grenzenOptie == GrenzenOptie.GELIJK) 
					{
						links = this.staafjesPanel.getGrensRechts();
						rechts = this.staafjesPanel.getGrensRechts();
					}
					else if(this.grenzenOptie == GrenzenOptie.LINKS) 
					{
						links = 0;
						rechts = this.staafjesPanel.getGrensRechts();
					}
					else 
					{
						links = this.staafjesPanel.getGrensRechts();
						rechts = this.getN();
					}
				}
				correct = correct && links == this.antwoordGrensLinks && rechts == this.antwoordGrensRechts;
			}
			
			if (this.kijkVerdelingNa) 
			{
				correct = correct && this.hypergeometrisch == (this.antwoordVerdeling == 1);
			}
			
			if (this.kijkNNa) 
			{
				correct = correct && this.n == this.antwoordN;
			}
			
			if (this.kijkMNa) 
			{
				correct = correct && this.M == this.antwoordM;
			}
			
			if (this.kijkPopulatieNa) 
			{
				correct = correct && this.populatie == this.antwoordPopulatie;
			}
			
			if (this.kijkPNa) 
			{
				//TODO terugzetten?
				//correct = correct && Math.round(1000.0 * this.p) == Math.round(1000.0*this.antwoordP);
				//correct = correct && this.p == this.antwoordP;
				correct = correct && this.checkP(this.antwoordP);
			}
		}
		if (correct) 
		{
			this.score = this.maxScore;
		}
		else 
		{
			this.score = 0;
		}
		
		//this.vinkjeLabel.setVisible(correct);
		//this.kruisjeLabel.setVisible(!correct);
		kijkNaPanel.setWidgetVisible(owner.goedKrulImage, correct);
		kijkNaPanel.setWidgetVisible(owner.foutKruisImage, !correct);
		
		
		owner.correct = correct;
		//fire actionEvent
		//ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "changed");
		//Iterator<ActionListener> iterator = this.listeners.iterator();
		//while(iterator.hasNext()) 
		//{
		//	iterator.next().actionPerformed(event);
		//}
	}
	
	private boolean checkP(BVInvoer antwoordP) 
	{
//System.out.println("CHeck P");
		if (this.pInvoer.isBreuk() && antwoordP.isBreuk()) 
		{
//System.out.println("checkP: beide breuk");
			Double d1 = new Double(Double.parseDouble(pInvoer.getTellerString())/Double.parseDouble(pInvoer.getNoemerString()));
			Double d2 = new Double(Double.parseDouble(antwoordP.getTellerString())/Double.parseDouble(antwoordP.getNoemerString()));
//System.out.println(d1 + " en " + d2);
			return d1.equals(d2);
		}
		else 
		{
			double d;
			if(antwoordP.isBreuk()) 
			{
				d = Double.parseDouble(antwoordP.getTellerString())/Double.parseDouble(antwoordP.getNoemerString());
			}
			else 
			{
				d = Double.parseDouble(antwoordP.getInput());
			}
//System.out.println(d + " en " + this.p);
			return d == this.p;
		}
	}
	
	public void kijkNa(int stapNr) 
	{
	}
	
	public void opnieuw() {
	}
	
	/**
	 * Override setBounds om bepaalde componenten goed te plaatsen en te resizen
	 */
/*	
	public void setBounds(int x, int y, int b, int h) {
		Rectangle r = new Rectangle(x,y,b,h);
		if(!r.equals(this.lastBounds)) {
			
			System.out.println("BVInteractiePanel.setBounds(" + x + "," + y + "," + b + "," + h);
			
			//resize de sliders
			this.nSlider.zetLengte(b/3 - 10 - this.NOORDBALKGAP);
			this.pSlider.zetLengte(b/3 - 10 - this.NOORDBALKGAP);
			this.MSlider.zetLengte(b/3 - 10 - this.NOORDBALKGAP);
			this.populatieSlider.zetLengte(b/3 - 10 - this.NOORDBALKGAP);
			
			//zet de sliders weer op de goede stand
			this.setMSlider();
			this.setNSlider();
			this.setPopulatieSlider();
			this.setPSlider();
			
			this.plaatsComponentenNoordBalk(b);
			this.plaatsComponentenKijkNaPanel(b);
			
			//zet bounds van panel
			super.setBounds(x, y, b, h);
		}
		this.lastBounds = r;
	}
*/	

	public void start() {
	}

	public void stop() {
	}

	public void wis() {
	}

	public void zetMaat() {
	}
/*
	public void zetMode(int mode) 
	{	this.mode = mode;
		if (kijkOpdrachtNa)    
			kijkOpdrachtNa = (mode == 0 || mode == 1);
	}
*/

	/**
	 * Initialiseer zoals in de BVInteractieEditPanel is ingesteld, met evt. randomvars.
	 * @param b De hashtable die uit getEditState() komt
	 * @param randomVars De namen van de random variabelen
	 * @param randomValues De waarden van alle random variabelen
	 */
	public void zetOpdracht(HashMap b, String[] randomVars, HashMap randomValues) {
		BVInvoer invoer = new BVInvoer("");
		
		//zet gegevens uit getEditState hashtable
		this.setState(b);
		
		//vul de randomwaarden in
		if(b.containsKey("initGrensLinks")) {
			invoer.setInput((String)b.get("initGrensLinks"));
			if(invoer.isRandomInput()) {
				this.staafjesPanel.setGrensLinks((int)BinomVerdPanel.substitueerRandom(0, invoer.getInput(), randomVars, randomValues));
			}
			else {
				this.staafjesPanel.setGrensLinks(Integer.parseInt(invoer.getInput()));
			}
		}
		if(b.containsKey("initGrensRechts")) {
			invoer.setInput((String)b.get("initGrensRechts"));
			if(invoer.isRandomInput()) {
				this.staafjesPanel.setGrensRechts((int)BinomVerdPanel.substitueerRandom(10, invoer.getInput(), randomVars, randomValues));
			}
			else {
				this.staafjesPanel.setGrensRechts(Integer.parseInt(invoer.getInput()));
			}
		}
		
		if(this.nInvoer.isRandomInput()) {
			this.n = (int) BinomVerdPanel.substitueerRandom((double)this.n, this.nInvoer.getInput(), randomVars, randomValues);
			this.nInvoer.setInput(Integer.toString(this.n));
		}
		
		if(this.pInvoer.isRandomInput()) {
			if(!this.pInvoer.isBreuk()) {
				this.p = BinomVerdPanel.substitueerRandom(this.p, this.pInvoer.getInput(), randomVars, randomValues);
				this.pInvoer.setInput(Double.toString(this.p));
			}
			else {
				double teller;
				double noemer;
				if(BVInvoer.isRandomVar(this.pInvoer.getTellerString())) {
					teller = BinomVerdPanel.substitueerRandom(this.p, this.pInvoer.getTellerString(), randomVars, randomValues);
				}
				else{
					teller = Double.parseDouble(this.pInvoer.getTellerString());
				}
				
				if(BVInvoer.isRandomVar(this.pInvoer.getNoemerString())) {
					noemer = BinomVerdPanel.substitueerRandom(1.0, this.pInvoer.getNoemerString(), randomVars, randomValues);
				}
				else {
					noemer = Double.parseDouble(this.pInvoer.getNoemerString());
				}
				
				this.p = teller/noemer;
				if(this.p > 1.0) { //kansen groter dan 1.0 zijn onzin
					this.p = 1.0;
				}
				this.pInvoer.setInput(Double.toString(teller) + "/" + Double.toString(noemer));
			}
		}
		System.out.println("PInvoer: " + this.pInvoer.getInput());
		this.pInvoer.haalPuntNulWeg();
		
		
		//zet nakijkopties
		if(b.containsKey("kijkNa")) {			
			this.kijkOpdrachtNa = ((Boolean)b.get("kijkNa")).booleanValue();
			
			if(b.containsKey("scoreMax")) {
				this.maxScore = ((Integer)b.get("scoreMax")).intValue();
			}
			
			if(this.kijkOpdrachtNa) {
				if(b.containsKey("antwoordN")) {
					this.kijkNNa = true;
					invoer.setInput((String)b.get("antwoordN"));
					if(invoer.isRandomInput()) {
						this.antwoordN = (int) BinomVerdPanel.substitueerRandom(30, invoer.getInput(), randomVars, randomValues);
					}
					else {
						this.antwoordN = Integer.parseInt(invoer.getInput());
					}
				}
				else {
					this.kijkNNa = false;
				}
				
				if(b.containsKey("antwoordGrenzenVan") && b.containsKey("antwoordGrenzenTot")) {
					this.kijkGrenzenNa = true;
					invoer.setInput((String)b.get("antwoordGrenzenVan"));
					if(invoer.isRandomInput()) {
						this.antwoordGrensLinks = (int) BinomVerdPanel.substitueerRandom(5, invoer.getInput(), randomVars, randomValues);
					}
					else {
						this.antwoordGrensLinks = Integer.parseInt((String)b.get("antwoordGrenzenVan"));
					}
					
					invoer.setInput((String)b.get("antwoordGrenzenTot"));
					if(invoer.isRandomInput()) {
						this.antwoordGrensRechts = (int) BinomVerdPanel.substitueerRandom(10, invoer.getInput(), randomVars, randomValues);
					}
					else {
						this.antwoordGrensRechts = Integer.parseInt((String)b.get("antwoordGrenzenTot"));
					}
					
				}
				else {
					this.kijkGrenzenNa = false;
				}
				
				if(b.containsKey("antwoordVerdeling")) {
					this.kijkVerdelingNa = true;
					this.antwoordVerdeling = (((Integer)b.get("antwoordVerdeling")).intValue());
					
					if(this.antwoordVerdeling == 0) {
						//er moet een binomiale verdeling worden nagekeken
						if(b.containsKey("antwoordP")) {
							this.kijkPNa = true;
							invoer.setInput((String)b.get("antwoordP"));
							System.out.println("Kijk P na invoer: " + invoer.getInput());
							if(invoer.isRandomInput()) {
								if(invoer.isBreuk()) {
									double teller;
									double noemer;
									if(BVInvoer.isRandomVar(invoer.getTellerString())) {
										teller = BinomVerdPanel.substitueerRandom(1, invoer.getTellerString(), randomVars, randomValues);
									}
									else {
										teller = Double.parseDouble(invoer.getTellerString());
									}
									if(BVInvoer.isRandomVar(invoer.getNoemerString())) {
										noemer = BinomVerdPanel.substitueerRandom(1, invoer.getNoemerString(), randomVars, randomValues);
									}
									else {
										noemer = Double.parseDouble(invoer.getNoemerString());
									}
									this.antwoordP = new BVInvoer(Double.toString(teller) + "/" + Double.toString(noemer));
								}
								else {
									this.antwoordP = new BVInvoer(Double.toString(BinomVerdPanel.substitueerRandom(0.5, invoer.getInput(), randomVars, randomValues)));
								}
							}
							else {
								/*
								if(invoer.isBreuk()) {
									this.antwoordP = Double.parseDouble(invoer.getTellerString()) / Double.parseDouble(invoer.getNoemerString());
								}
								else {
									this.antwoordP = Double.parseDouble(invoer.getInput());
								}*/
								this.antwoordP = new BVInvoer(invoer.getInput());
							}
						}
						else {
							this.kijkPNa = false;
						}
					}
					else {
						//er moet een hypergeometrische verdeling worden nagekeken
						if(b.containsKey("antwoordM")) {
							this.kijkMNa = true;
							invoer.setInput((String)b.get("antwoordM"));
							if(invoer.isRandomInput()) {
								this.antwoordM = (int) BinomVerdPanel.substitueerRandom(50, invoer.getInput(), randomVars, randomValues);
							}
							else {
								this.antwoordM = Integer.parseInt(invoer.getInput());
							}
						}
						else {
							this.kijkMNa = false;
						}
						
						if(b.containsKey("antwoordPopulatie")) {
							this.kijkPopulatieNa = true;
							invoer.setInput((String)b.get("antwoordPopulatie"));
							if(invoer.isRandomInput()) {
								this.antwoordPopulatie = (int) BinomVerdPanel.substitueerRandom(100, invoer.getInput(), randomVars, randomValues);
							}
							else {
								this.antwoordPopulatie = Integer.parseInt(invoer.getInput());
							}
						}
						else {
							this.kijkPopulatieNa = false;
						}
					}
				}
				else {
					this.kijkVerdelingNa = false;
				}
				
			}
			else {
				this.kijkGrenzenNa = false;
				this.kijkMNa = false;
				this.kijkNNa = false;
				this.kijkPNa = false;
				this.kijkPopulatieNa = false;
				this.kijkVerdelingNa = false;
			}
			
		}
		else {
			this.kijkOpdrachtNa = false;
			this.kijkGrenzenNa = false;
			this.kijkNNa = false;
			this.kijkMNa = false;
			this.kijkVerdelingNa = false;
			this.kijkPopulatieNa = false;
			this.kijkPNa = false;
		}
		
		
		//System.out.println("AntwoordP = " + this.antwoordP.getInput());
		//System.out.println("invoerP = " + this.pInvoer.getInput());
		
		//update
		this.vernieuw();
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
    			 owner.kijkNa();   			
    		}

    	}	
    }	

}