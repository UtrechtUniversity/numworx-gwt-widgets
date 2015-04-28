package fi.kansbomengwt.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import fi.kansbomengwt.client.text.Text_nl;

public class KansbomenGWT implements EntryPoint, InteractionStub{

	public static Text_nl rb = new Text_nl();
	OpdrNavIF comRoot;
	
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap randomVarWaarden = null;
	FlowPanel basisPanel;
	
	static final String holderId = "dockholder";
	static final String upgradeMessage = 
			"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	int breedte = 800;
	int hoogte = 400;
	
	int linkerKolomBreedte = 160;
	int fontSize = 12;
	
	int kansboomBreedte;
	Kansboom kansboom;
	
	ListBox terugleggenBox, trekkingenBox, optiesBox;
	Label aantalTrekkingen, aantalOptiesLabel, legendaKop;
	Label[] aantalOptie, legendaOptie;
	LijntjeLabel[] legendaKleur;
	CssColor[] gekleurdeRij = new CssColor[6];
	TextBox[] aantalOptieVeld;
	
	boolean teruglegZichtbaar = true;
	boolean trekkingZichtbaar = true;
	boolean optiesZichtbaar = true;
	boolean ballenZichtbaar = true;
	boolean legendaZichtbaar = true;
	boolean aantallenZichtbaar = true;
	boolean bovenbalkZichtbaar = true;
	boolean kleur = true;
	int kansVolgordeKeuze = 0;
	boolean terugleggen = true;
	boolean letter = false;
	int terugleggenKeuze = 0;
	int labelsKeuze = 0;
	int trekkingen = 2;
	
	String[] naamOptieTekst;
	String[] letterString = new String[6]; //{"d","d","d","d","d","d","d"}
	String trekkingTekst = rb.getString("trekkingBalkTekst");
	String trekkingMvTekst = rb.getString("trekkingBalkTekstMv");
	
	int aantalOpties = 4;
	int[] aantalInt = new int[] {4,4,4,4,4,4};
	int breedteAantalVeld;

	int offset=5;
	
	private int score;
	int scoreMax = 10;
	
	PushButton kijkNaButton;
	int nakijkKnopBreedte = 95;
	int nakijkKnopHoogte = 24;
	LayoutPanel kijkNaPanel;
	//FlowPanel kijkNaPanel;
	KansbomenClientBundle kansbomenClientBundle; 
	ImageResource goedKrulResource, foutKruisResource;
	Image goedKrulImage, foutKruisImage;
	
	boolean kijkNaActief;
	private boolean checkExternal = false;
	private boolean volledigeBreedte = false;
	
	int[] nakijkModel = new int[] {10, 4, 4, 4, 4, 4, 4, 0, 3, 4};
	int[] leerlingAntwoorden = new int[] {0, 4, 4, 4, 4, 4, 4, 0, 3, 4};
	int[] beginStatus = new int[] {0, 4, 4, 4, 4, 4, 4, 0, 3, 4};

	private boolean ingevuld;
	private boolean nagekeken;
	private int mode;

	
	public KansbomenGWT()
	{
		basisPanel = new FlowPanel();
		init(breedte, hoogte, launchState, null);
	}
	
	
	public KansbomenGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
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
		if(map.containsKey("volledigeBreedte"))
			volledigeBreedte = h.getBoolean("volledigeBreedte");
		
		basisPanel = new FlowPanel();
		init(breedte, hoogte, launchState, randomVarWaarden);
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
	
	@Override
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width;
		hoogte = height;
		
		ObjectMap h = JSONUtilities.wrapMap(launchData);
		
		if(launchData != null)
		{
			if (h.containsKey("teruglegZichtbaar"))
				teruglegZichtbaar = h.getBoolean("teruglegZichtbaar");
			if (h.containsKey("trekkingZichtbaar"))
				trekkingZichtbaar = h.getBoolean("trekkingZichtbaar");
			if (h.containsKey("optiesZichtbaar"))
				optiesZichtbaar = h.getBoolean("optiesZichtbaar");
			if (h.containsKey("ballenZichtbaar"))
				ballenZichtbaar = h.getBoolean("ballenZichtbaar");
			if (h.containsKey("legendaZichtbaar"))
				legendaZichtbaar = h.getBoolean("legendaZichtbaar");
			if (h.containsKey("aantallenZichtbaar"))
				aantallenZichtbaar = h.getBoolean("aantallenZichtbaar");
			if (h.containsKey("bovenbalkZichtbaar"))
				bovenbalkZichtbaar = h.getBoolean("bovenbalkZichtbaar");
			if (h.containsKey("kijkNaActief"))
				kijkNaActief = h.getBoolean("kijkNaActief");
			if (h.containsKey("checkExternal"))
				checkExternal = h.getBoolean("checkExternal");
			if (h.containsKey("labelsKeuze"))
				labelsKeuze = h.getInt("labelsKeuze");
			if(h.containsKey("kleur"))
				kleur = h.getBoolean("kleur");
			if (h.containsKey("kansVolgordeKeuze"))
				kansVolgordeKeuze = h.getInt("kansVolgordeKeuze");
			if (h.containsKey("naamOptieTekst"))
			{	String[] naamOptieTekst = h.getStringArray("naamOptieTekst");
				if(naamOptieTekst.length == 6)
					this.naamOptieTekst = naamOptieTekst;
				else if(naamOptieTekst.length == 7)
				{
					this.naamOptieTekst = new String[6];
					for(int i = 0; i < this.naamOptieTekst.length; i++)
						this.naamOptieTekst[i] = naamOptieTekst[i+1];
				}
			}
			if (h.containsKey("letterString"))
			{	String[] letterString = h.getStringArray("letterString");
				if(letterString.length == 6)
					this.letterString = letterString;
				else if(letterString.length == 7)
				{
					this.letterString = new String[6];
					for(int i = 0; i < this.letterString.length; i++)
						this.letterString[i] = letterString[i+1];
				}
			}
			if(h.containsKey("terugleggenKeuze"))
				terugleggenKeuze = h.getInt("terugleggenKeuze");
			if(h.containsKey("trekkingen"))
				trekkingen = h.getInt("trekkingen");
			if(h.containsKey("aantalOpties"))
				aantalOpties = h.getInt("aantalOpties");
			if(h.containsKey("nakijkModel"))
				nakijkModel = h.getIntArray("nakijkModel"); 
			
			if (h.containsKey("aantalInt"))
			{	int[] aantalInt = h.getIntArray("aantalInt");
				if(aantalInt.length == 6)
					this.aantalInt = aantalInt;
				else if(aantalInt.length == 7)
				{
					this.aantalInt = new int[6];
					for(int i = 0; i < this.aantalInt.length; i++)
						this.aantalInt[i] = aantalInt[i+1];
					
					//kennelijk bevat de state nog oude launchdata. Ook aantalOpties, aantalTrekkingen en nakijkModel omzetten naar nieuwe versie.
					aantalOpties += 2;
					trekkingen += 1;
					nakijkModel[8] += 1;
					nakijkModel[9] += 2;
				}
			}
			if(h.containsKey("scoreMax"))
				scoreMax = h.getInt("scoreMax");
			if(!kijkNaActief)
				scoreMax = 0;
			if(h.containsKey("trekkingTekst"))
				trekkingTekst = h.getString("trekkingTekst");
			if(h.containsKey("trekkingMvTekst"))
				trekkingMvTekst = h.getString("trekkingMvTekst");
			//if(h.containsKey("kbipBreedte"))
			//	kbipBreedte = h.getInt("kbipBreedte");
			//if(h.containsKey("kbipHoogte"))
			//	kbipHoogte = h.getInt("kbipHoogte");
		}
		
//		kansboom.init (oid)
//		for(int i = 1; i < 7; i++)
//			zetNaamOptie(i, naamOptieTekst[i]);
			
		
		initialize();
		
	}
	
//	weglaten:
//	public void zetNaamOptie(int i, String s)
//	{
//		naamOptieTekst[i] = s;
//		aantalOptie[i].setText(rb.getString("aantalTekst")+s+":");
//		zetLegendaTekst(i);
//		letterString[i] = s.substring(0,1).toLowerCase();
//		kansboom.letter[i] = letterString[i];
//	}
	
	public void zetLetterOptie(int i, String s)
	{
		letterString[i] = s;
		kansboom.letter[i] = letterString[i];
	}
	
	public void bepaalKansboomBreedte()
	{
		if(teruglegZichtbaar || trekkingZichtbaar || optiesZichtbaar || ballenZichtbaar
				|| legendaZichtbaar)
		{	kansboomBreedte = breedte - linkerKolomBreedte - 2 * offset;
		}
		else
		{	kansboomBreedte = breedte - 2 * offset;
		}
	}
	
	public void initialize()
	{
	
		getImages();
		
		gekleurdeRij[0] = CssColor.make(0,0,255);
		gekleurdeRij[1] = CssColor.make(0,200,0);
		gekleurdeRij[2] = CssColor.make(255,50,50);
		gekleurdeRij[3] = CssColor.make(0,220,220);
		gekleurdeRij[4] = CssColor.make(255,180,0);
		gekleurdeRij[5] = CssColor.make(220,0,220);
		
		bepaalKansboomBreedte();
		FlowPanel instellingenKolom = new FlowPanel();
		instellingenKolom.getElement().getStyle().setTextAlign(TextAlign.LEFT);
		instellingenKolom.setPixelSize(linkerKolomBreedte, hoogte - nakijkKnopHoogte);
		
		terugleggenBox = new ListBox();
		terugleggenBox.addItem(rb.getString("metTerugleggenTekst"));
		terugleggenBox.addItem(rb.getString("zonderTerugleggenTekst"));
		terugleggenBox.setSelectedIndex(terugleggenKeuze);
		terugleggenBox.getElement().getStyle().setMarginBottom(5, Unit.PX);
		terugleggenBox.setWidth(linkerKolomBreedte + "px");
		instellingenKolom.add(terugleggenBox);
		terugleggenBox.setVisible(teruglegZichtbaar);
		terugleggenBox.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent event) {
				terugleggenKeuze = terugleggenBox.getSelectedIndex();
				if(terugleggenKeuze==0)
					terugleggen = true;
				else if(terugleggenKeuze==1)
					terugleggen = false;
				kansboom.zetTerugleggen(terugleggen);
			}
			
		});
		
		FlowPanel trekkingenPanel = new FlowPanel();
		aantalTrekkingen = new Label(rb.getString("aantalTekst") + trekkingMvTekst);
		zetLabelLayout(aantalTrekkingen);
		trekkingenPanel.add(aantalTrekkingen);
		
		trekkingenBox = new ListBox();
		for(int i = 1; i < 7; i++)
			trekkingenBox.addItem("" + i);
		trekkingenBox.setSelectedIndex(trekkingen - 1);
		Style style = trekkingenBox.getElement().getStyle();
		style.setProperty("float", "right");
		style.setFontSize(fontSize, Style.Unit.PX);
		trekkingenPanel.add(trekkingenBox);
		trekkingenBox.addChangeHandler(new ChangeHandler(){
			
			@Override
			public void onChange(ChangeEvent event)
			{
				trekkingen = trekkingenBox.getSelectedIndex() + 1;
				kansboom.zetTrekkingen(trekkingen);
			}
		});
		instellingenKolom.add(trekkingenPanel);
		trekkingenPanel.setVisible(trekkingZichtbaar);
		
		FlowPanel optiesPanel = new FlowPanel();
		aantalOptiesLabel = new Label(rb.getString("aantalOptiesTekst"));
		zetLabelLayout(aantalOptiesLabel);
		optiesPanel.add(aantalOptiesLabel);
		
		optiesBox = new ListBox();
		for(int i = 2; i < 7; i++)
			optiesBox.addItem("" + i);
		optiesBox.setSelectedIndex(aantalOpties - 2);
		style = optiesBox.getElement().getStyle();
		style.setProperty("float", "right");
		style.setFontSize(12, Style.Unit.PX);
		optiesPanel.add(optiesBox);
		optiesBox.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event)
			{
				aantalOpties = optiesBox.getSelectedIndex() + 2;
				zetOpties(aantalOpties, aantalInt);
			}
		});
		instellingenKolom.add(optiesPanel);
		optiesPanel.setVisible(optiesZichtbaar);
		
		breedteAantalVeld = 30;
				                       
		aantalOptieVeld = new TextBox[6];
		for(int i=0; i<6; i++)
		{	aantalOptieVeld[i] = new TextBox();
			aantalOptieVeld[i].setText(""+aantalInt[i]);
			aantalOptieVeld[i].setWidth(breedteAantalVeld + "px");
			style = aantalOptieVeld[i].getElement().getStyle();
			style.setPadding(3, Unit.PX);
			style.setProperty("float", "right");
			style.setFontSize(12, Style.Unit.PX);
			final int index = i;
			aantalOptieVeld[i].addChangeHandler(new ChangeHandler()
			{
				@Override
				public void onChange(ChangeEvent e)
				{
					actieAantalOptieVeld(index);
				}
			});
		}
			
		if(naamOptieTekst == null)
		{
			naamOptieTekst = new String[6];
			naamOptieTekst[0] = rb.getString("naam1StringTekst");
			naamOptieTekst[1] = rb.getString("naam2StringTekst");
			naamOptieTekst[2] = rb.getString("naam3StringTekst");
			naamOptieTekst[3] = rb.getString("naam4StringTekst");
			naamOptieTekst[4] = rb.getString("naam5StringTekst");
			naamOptieTekst[5] = rb.getString("naam6StringTekst");
		}
		
		if(letterString == null)
		{
			for(int i=0; i<6; i++)
				letterString[i] = naamOptieTekst[i].substring(0,1).toLowerCase();
		}
		
		                            
		aantalOptie = new Label[6];
		for (int i=0; i<6; i++)
		{	aantalOptie[i] = new Label(rb.getString("aantalTekst")+naamOptieTekst[i]+":");
			zetLabelLayout(aantalOptie[i]);
		}
		for(int i=0; i<6; i++)
		{	FlowPanel optiePanel = new FlowPanel();
			optiePanel.add(aantalOptie[i]);
			optiePanel.add(aantalOptieVeld[i]);
			if(i >= aantalOpties)
			{
				aantalOptie[i].setVisible(false);
				aantalOptieVeld[i].setVisible(false);
			}
			instellingenKolom.add(optiePanel);
			optiePanel.setVisible(ballenZichtbaar);
		}
		
		legendaKop = new Label(rb.getString("legendaTekst"));
		style = legendaKop.getElement().getStyle();
		style.setFontWeight(FontWeight.BOLD);
		style.setFontSize(fontSize, Style.Unit.PX);
		style.setMarginTop(10, Unit.PX);
		style.setMarginBottom(5, Unit.PX);
		instellingenKolom.add(legendaKop);
		legendaKop.setVisible(legendaZichtbaar);
		
		legendaOptie = new Label[6];
		
		legendaKleur = new LijntjeLabel[6];
		
		CssColor[] kleurRij = new CssColor[6];
		if(kleur) 
			kleurRij = gekleurdeRij;
		else
		{	for(int i = 0; i < kleurRij.length; i++)
				kleurRij[i] = CssColor.make("black");
		}
		
		for (int i = 0; i < 6; i++)
		{
			FlowPanel legendaRegel = new FlowPanel();
			legendaRegel.getElement().getStyle().setMarginBottom(3, Unit.PX);
			legendaKleur[i] = new LijntjeLabel(kleurRij[i], 20);
			legendaRegel.add(legendaKleur[i].getCanvas());
			legendaKleur[i].paint();
			
			legendaOptie[i] = new Label();
			zetLegendaTekst(i);
			style = legendaOptie[i].getElement().getStyle();
			style.setMarginLeft(3, Unit.PX);
			style.setFontSize(fontSize, Style.Unit.PX);
			legendaRegel.add(legendaOptie[i]);
			style.setDisplay(Display.INLINE_BLOCK);
			instellingenKolom.add(legendaRegel);
			if(i >= aantalOpties)
			{
				legendaKleur[i].getCanvas().setVisible(false);
				legendaOptie[i].setVisible(false);
			}
			legendaRegel.setVisible(legendaZichtbaar);
		}
		
		kijkNaButton = new PushButton(rb.getString("kijkNaTekst"));
		//kijkNaButton.setSize(nakijkKnopBreedte + "px", nakijkKnopHoogte + "px");
		style = kijkNaButton.getElement().getStyle();
		style.setFontSize(fontSize, Style.Unit.PX);
		style.setTextAlign(TextAlign.CENTER);
		kijkNaButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{	e.stopPropagation();
				kijkNa();
			}
		});
		
		kijkNaPanel = new LayoutPanel();
		style = kijkNaPanel.getElement().getStyle();
		style.setDisplay(Display.INLINE_BLOCK);
		style.setFloat(Float.RIGHT);
		
		kijkNaPanel.setPixelSize(nakijkKnopBreedte, nakijkKnopHoogte);
		kijkNaPanel.add(kijkNaButton);
		kijkNaPanel.setWidgetLeftWidth(kijkNaButton, 0, Style.Unit.PX, nakijkKnopBreedte, Style.Unit.PX);
		kijkNaPanel.setWidgetTopHeight(kijkNaButton, 0, Style.Unit.PX, nakijkKnopHoogte, Style.Unit.PX);
		
		kijkNaPanel.add(goedKrulImage);
		kijkNaPanel.add(foutKruisImage);
		kijkNaPanel.setWidgetRightWidth(goedKrulImage, 1, Style.Unit.PX, 15, Style.Unit.PX);
		kijkNaPanel.setWidgetTopHeight(goedKrulImage, 1, Style.Unit.PX, 20, Style.Unit.PX);
		kijkNaPanel.setWidgetRightWidth(foutKruisImage, 1, Style.Unit.PX, 15, Style.Unit.PX);
		kijkNaPanel.setWidgetTopHeight(foutKruisImage, 1, Style.Unit.PX, 20, Style.Unit.PX);
		goedKrulImage.setVisible(false);
		foutKruisImage.setVisible(false);		
		
		FlowPanel linkerKolom = new FlowPanel();
		linkerKolom.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
		
		linkerKolom.add(instellingenKolom);
		linkerKolom.add(kijkNaPanel);
		
		//instellingenKolom.add(kijkNaPanel);
		kijkNaPanel.setVisible(kijkNaActief);
		kijkNaButton.setVisible(!checkExternal);
		
		if(teruglegZichtbaar || trekkingZichtbaar || optiesZichtbaar || ballenZichtbaar
				|| legendaZichtbaar)
			basisPanel.add(linkerKolom);
		linkerKolom.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		
		kansboom = new Kansboom(kansboomBreedte, hoogte);
		//kansboom.setLocation(currentX, offset);
		kansboom.getCanvas().getElement().getStyle().setPaddingLeft(5, Unit.PX);
		basisPanel.add(kansboom.getCanvas());
		kansboom.getCanvas().getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		
		
		kansboom.zetBovenbalkZichtbaar(bovenbalkZichtbaar); //deze moet wel weer zichtbaar!
		kansboom.zetLabelsKeuze(labelsKeuze);
		for(int i = 0; i < 6; i++)
			zetLetterOptie(i, letterString[i]);
		kansboom.zetKleur(kleur);
		kansboom.zetKansVolgorde(kansVolgordeKeuze); //deze moet wel weer zichtbaar!
		zetTerugleggen(terugleggenKeuze);
		zetTrekkingen(trekkingen);
		
		zetOpties(aantalOpties,aantalInt); //nodig hier?
		kansboom.trekkingTekst = trekkingTekst;
		kansboom.paint();
		
		for(int i = 1; i < 7; i++)
			beginStatus[i] = aantalInt[i - 1];
		beginStatus[7] = terugleggenKeuze;
		beginStatus[8] = trekkingen;
		beginStatus[9] = aantalOpties;
	}
	
	public void zetLabelLayout(Label l)
	{
		Style style = l.getElement().getStyle();
		style.setDisplay(Display.INLINE_BLOCK);
		style.setMarginTop(5, Unit.PX);
		style.setMarginBottom(8, Unit.PX);
		style.setFontSize(fontSize, Style.Unit.PX);
	}
	
	public void getImages() 
	{
		kansbomenClientBundle = GWT.create(KansbomenClientBundle.class);
		
		goedKrulResource = kansbomenClientBundle.goedkrulResource();
		foutKruisResource = kansbomenClientBundle.foutkruisResource();

		goedKrulImage = new Image(goedKrulResource.getSafeUri());
		foutKruisImage = new Image(foutKruisResource.getSafeUri());
	}
	
	public void zetOpties(int k, int[] opties)
	{	aantalInt = opties;
		aantalOpties = k;
		kansboom.zetOpties(k, opties);
		for(int i = 1; i<aantalInt.length; i++)
		{	//zetLegendaTekst(i);
			aantalOptieVeld[i].setText(""+aantalInt[i]);
		}
		optiesBox.setSelectedIndex(k - 2);
		for(int p = 0; p < 6; p++)
		{	if(aantalOpties > p)
			{
				aantalOptie[p].setVisible(ballenZichtbaar);
				aantalOptieVeld[p].setVisible(ballenZichtbaar);
				aantalOptie[p].getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
				legendaKleur[p].getCanvas().setVisible(legendaZichtbaar);
				legendaOptie[p].setVisible(legendaZichtbaar);
				legendaOptie[p].getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			}
			else
			{
				aantalOptie[p].setVisible(false);
				aantalOptieVeld[p].setVisible(false);
				legendaKleur[p].getCanvas().setVisible(false);
				legendaOptie[p].setVisible(false);
			}
		}
	}
	
	public void zetTerugleggen(int i)
	{
		terugleggenKeuze = i;
		if(i==0)
			terugleggen = true;
		else if(i==1)
			terugleggen = false;
		kansboom.zetTerugleggen(terugleggen);
		terugleggenBox.setSelectedIndex(terugleggenKeuze);
	}

	public void zetTrekkingen(int i)
	{
		trekkingen = i;
		kansboom.zetTrekkingen(i);
		trekkingenBox.setSelectedIndex(trekkingen  - 1);
	}
	
	public void zetLegendaTekst(int i)
	{
		if(aantallenZichtbaar)
			legendaOptie[i].setText(naamOptieTekst[i]+" ("+aantalInt[i]+")");
		else
			legendaOptie[i].setText(naamOptieTekst[i]);
	}
	
	public void actieAantalOptieVeld(int i)
	{
		int[] aantalIntOud = new int[aantalInt.length];
		try
		{ 	aantalIntOud[i] = aantalInt[i];
			aantalInt[i] = Integer.parseInt( aantalOptieVeld[i].getText() );
			if(aantalInt[i] > 0)
			{	zetOpties(aantalOpties, aantalInt);
				zetLegendaTekst(i);
			}	
			else 
			{	aantalInt[i] = aantalIntOud[i];
				aantalOptieVeld[i].setText(""+aantalInt[i]);
			}	
		}
		catch (Exception p)
		{aantalOptieVeld[i].setText(""+aantalInt[i]);}
	}
	
	public void updateLeerlingAntwoorden()
	{
		leerlingAntwoorden[0] = beginStatus[0];
		for(int i = 1; i < 7; i++)
			leerlingAntwoorden[i] = aantalInt[i - 1];
		leerlingAntwoorden[7] = terugleggenKeuze;
		leerlingAntwoorden[8] = trekkingen;
		leerlingAntwoorden[9] = aantalOpties;	
	}

	@Override
	public HashMap<String, Object> getState() {
		int terugleggenKeuze = 0;
		int trekkingen = 2;
		int aantalOpties = 2;
		int[] aantalInt = {4,4,4,4,4,4};
		
		terugleggenKeuze = this.terugleggenKeuze;
		trekkingen = this.trekkingen;
		aantalOpties = this.aantalOpties;
		if(this.aantalInt.length == 7)
		{	aantalInt = new int[6];
			for(int i = 0; i < aantalInt.length; i++)
			{
				aantalInt[i] = this.aantalInt[i+1];
			}
			//kennelijk bevat de state nog oude launchdata. Ook aantalOpties en aantalTrekkingen omzetten naar nieuwe versie.
			aantalOpties += 2;
			trekkingen += 1;
		}
		else
			aantalInt = this.aantalInt;
		
		HashMap<String,Object> h = new HashMap<String,Object>();
		
		h.put("terugleggenKeuze", terugleggenKeuze);
		h.put("trekkingen", trekkingen);
		h.put("aantalOpties", aantalOpties);
		h.put("aantalInt", aantalInt);
		h.put("ingevuld", new Boolean(ingevuld));
	    h.put("nagekeken", new Boolean(nagekeken));
		
		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		
		ObjectMap map = JSONUtilities.wrapMap(h);
		if(map.containsKey("terugleggenKeuze"))
			terugleggenKeuze = map.getInt("terugleggenKeuze");
		zetTerugleggen(terugleggenKeuze); 
		
		if(map.containsKey("trekkingen"))
			trekkingen = map.getInt("trekkingen");
		
		if(map.containsKey("aantalOpties"))
			aantalOpties = map.getInt("aantalOpties");
		
		if (map.containsKey("aantalInt"))
		{	int[] aantalInt = map.getIntArray("aantalInt");
			if(aantalInt.length == 6)
				this.aantalInt = aantalInt;
			else if(aantalInt.length == 7)
			{
				this.aantalInt = new int[6];
				for(int i = 0; i < this.aantalInt.length; i++)
					this.aantalInt[i] = aantalInt[i+1];
				//kennelijk bevat de state nog oude launchdata. Ook aantalOpties en aantalTrekkingen omzetten naar nieuwe versie.
				aantalOpties += 2;
				trekkingen += 1;
			}
		}
		zetTrekkingen(trekkingen); 
		zetOpties(aantalOpties, aantalInt);
		for(int i = 0; i < 6; i++)
			zetLegendaTekst(i);
		
		if (map.containsKey("ingevuld")) 
			ingevuld = map.getBoolean("ingevuld");
	    if (map.containsKey("nagekeken")) 
	    	nagekeken = map.getBoolean("nagekeken");
		if (ingevuld && (mode == 0 || nagekeken)) 
	    	kijkNa();
	}

	@Override
	public int getScore() {
		return score;
	}
	

	@Override
	public Boolean isCorrect() {
		if (!kijkNaActief)
			return true;
		return 
			score == scoreMax;
	}

	@Override
	public void kijkNa() {
		// niet nakijken
    	if (!kijkNaActief)
    		return;
    	updateLeerlingAntwoorden();
    	if(Arrays.equals(leerlingAntwoorden,beginStatus))
    	{
    		ingevuld = false;
    		return;
    	}		                                   
    	
    	leerlingAntwoorden[0] = nakijkModel[0];
    	for(int i = nakijkModel[9]; i < 6; i++)
    		leerlingAntwoorden[i + 1] = nakijkModel[i + 1];
    	if(Arrays.equals(leerlingAntwoorden,nakijkModel))
    	{	score = scoreMax;
    		foutKruisImage.setVisible(false);
    		goedKrulImage.setVisible(true);
    	}
    	else
    	{	score = 0;
    		foutKruisImage.setVisible(true);
    		goedKrulImage.setVisible(false);
    	}
    	ingevuld = true;
    	comRoot.setChanged(foutKruisImage.isVisible());
	}

	@Override
	public void zetNagekeken(boolean b) {
		if (ingevuld) 
			nagekeken = b;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		//TODO: hier nog toetsenbord installeren?
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		if(volledigeBreedte)
		{
			this.breedte = breedte;
			bepaalKansboomBreedte();
			kansboom.setSize(kansboomBreedte, hoogte);
			kansboom.paint();
		}
	}

	@Override
	public Widget asWidget() {
		return basisPanel;
	}

	@Override
	public int getAsHoogte() {
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
		
	}

	
	
}
