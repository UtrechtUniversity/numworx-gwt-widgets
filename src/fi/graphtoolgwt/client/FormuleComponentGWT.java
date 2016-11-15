package fi.graphtoolgwt.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Expressie;

public class FormuleComponentGWT extends LayoutPanel {//implements InteractionView{

	private FormuleEditorFactory factor = GWT.create(FormuleEditorFactory.class);
	{
		factor.setFc(this);
	}
	
	
	
	
	static class TriforkFormuleEditor extends GraphtFormuleEditor {

		TriforkFormuleEditor(int i, FormuleComponentGWT fc) {
			super(i, fc);
			// TODO Auto-generated constructor stub
		}

		public void resize()
		{
			fc.resize();
			//if(true || parsable(regelnummer)) // ALTIJD opslaan.
/* naar een 'factory' die bij TriFork gepatched is. */
  			if (fc.fromuser)
			{   fc.parseFormule(regelnummer, false);
				fc.interactiePanel.setChanged(false);
			}
		
			
		}

	}


	private static Logger logger = Logger.getLogger("FormuleComponentGWT");

	private IsWidget wrap (IsWidget widget) {
		//FocusOnTouch.installKeyboard(interactiePanel.kb);
		FocusPanel focus = FocusOnTouch.wrap (widget.asWidget(), false);
		//focus.addKeyDownHandler(interactiePanel.keyHandler);
		//focus.addKeyPressHandler(interactiePanel.keyHandler);
		//focus.add(widget);
		//focus.addMouseUpHandler(new FocusOnTouch(focus));
		return focus;
	}
	
	
	/*
	 * class voor de editors die de formules gaan verwerken; vooralsnog komt er in elke regel één.
	 */
	static class GraphtFormuleEditor extends FormuleEditor {

		int regelnummer;
		FormuleComponentGWT fc;
		GraphtFormuleEditor(int i, FormuleComponentGWT fc)
		{
			super();
			regelnummer = i;
			this.fc = fc;
			this.setFormuleToolBijFocus(true);
		}
		
		@Override
		public void enter() {
            
			fc.parseFormule(regelnummer, false);
			if(fc.alsOpdracht)
				fc.interactiePanel.kijkNa();
			else
				fc.interactiePanel.setChanged(false);
			fc.grafiekGWTVeld.paint();
		}
		

		@Override
		public void setFont(FormuleFont fm) {
			super.setFont(fm);
		}

		@Override
		public void diff_partial() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void stelsel() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public FormuleFont getDefaultFont() {
			// TODO Auto-generated method stub
			return super.getDefaultFont();
		}

		public void resize()
		{
			fc.resize();
			//if(true || parsable(regelnummer)) // ALTIJD opslaan.
/* TODO naar een 'factory' die bij TriFork gepatched is.
 * 			if (fromuser)
			{   parseFormule(regelnummer, false);
				interactiePanel.setChanged(false);
			}
*/			
			
		}
		
	}
	

	private final GraphToolGWT interactiePanel;
	private GrafiekGWTVeld grafiekGWTVeld;
	boolean alsOpdracht = false;
	
	private double[][] domeinen;
	private String[][] domeinStrings;
	private static double[] DEFAULTDOMEIN;
	
	private int maxAantalFormules=9;
	private int aantalRegels=1;
	
	private String xAsNaam = "x";
	private String yAsNaam = "y";
	String[] namen = {"f","g","h","i","j","k","l","m","n"};
	private boolean functieBeginZichtbaar = true;
	private boolean formeleFuncties = true;
	private boolean domeinInstelbaar = false;
	
	boolean grafiekKleurInstelbaar = true;
	boolean functieBeginAanpasbaar = true;
	
	private boolean functieToegestaan = true;
	private boolean ongelijkheidToegestaan = true;
	private boolean implicieteFunctieToegestaan = false;
	private boolean verticaleLijnToegestaan = true;
	private boolean parametrisatieToegestaan = false;
	
	private int formuleComponentHoogte; 
	
	private String[] functieBegin = new String[maxAantalFormules];
	//private boolean hasPrefix = true;
	private int breedte = 300;
	private int hoogte = 120;
	private HashMap<String, Object> launchState;
	//private HashMap<String, Object> instellingen;
	GraphtFormuleEditor[] editors = new GraphtFormuleEditor[maxAantalFormules];
	private TouchPanel[] editorPanels = new TouchPanel[maxAantalFormules];
	private Widget[] functieBeginViewers = new Widget[maxAantalFormules];
	//private FormuleViewer latest_answer_viewer; //nodig??
	//private FormuleKeyboardIF kb = null;
	private PushButton verwijderRegelKnop = null;
	private PushButton nieuweRegelKnop = null;
	
	
	private ScrollPanel sp;
	private LayoutPanel regelsPanel = null;
	private CheckBox[] checkboxen = new CheckBox[maxAantalFormules];
	private boolean[] geselecteerd = new boolean[maxAantalFormules];
	private PushButton[] enOfKnoppen = new PushButton[maxAantalFormules];
	private DomeinButtonGWT[] domeinButtons = new DomeinButtonGWT[maxAantalFormules];
	private boolean[] isEn;
	private boolean[] isOngelijkheid;
	
	//private FlowPanel feedbackPanel = null;
	//private FlowPanel mainPanel = null;
	
	//private HashMap<String, Object> h = null;
	//private String[] randomVarNamen = null;
	//private HashMap randomVarWaarden = null;
	//private ArrayList<FlowPanel> stepPanels = new ArrayList<FlowPanel>();
	//private FlowPanel[] regelPanels = new FlowPanel[maxAantalFormules];
	private LayoutPanel[] regelPanels = new LayoutPanel[maxAantalFormules];
	private FormuleFont font = FormuleFont.createFromFontSize(16);
	
	private static FormuleFont defaultfont;// = FormuleFont.createFromFontSize(18);
	//private boolean answeredCorrectly = false;
	private CssColor hlColor = CssColor.make(255, 255, 255);
	private CssColor bgColor = CssColor.make(240, 240, 240);
	
	GraphToolGWTClientBundle graphToolGWTClientBundle; 
	static GraphToolCssResource graphToolCss;
	ImageResource regelMinderButtonResource, regelMeerButtonResource;
	Image regelMinderButtonImage, regelMeerButtonImage;
	boolean fromuser;
	
	
public FormuleComponentGWT(GraphToolGWT interactiePanel, Map<String, Object> launchData, int breedte, int hoogte) {
		defaultfont = FormuleHolder.getDefaultActiviteitFont().createCopy();
		
		graphToolGWTClientBundle = GWT.create(GraphToolGWTClientBundle.class);
		graphToolCss = graphToolGWTClientBundle.getGraphToolGWTCSS();
		graphToolCss.ensureInjected();
		
		this.interactiePanel = interactiePanel;
		getImages();
		
		this.breedte = breedte;
		this.hoogte = hoogte;
		if(launchData != null) {
			//if(launchData.containsKey("grafiekKleuren"))
			//	grafiekKleuren = ((Boolean)launchData.get("grafiekKleuren")).booleanValue();
			//if(launchData.containsKey("kleurInstelbaar"))
			//	kleurInstelbaar = ((Boolean)launchData.get("kleurInstelbaar")).booleanValue();
			if(launchData.containsKey("functieBeginZichtbaar"))
				functieBeginZichtbaar = ((Boolean)launchData.get("functieBeginZichtbaar")).booleanValue();
			if(launchData.containsKey("functieBeginAanpasbaar"))
				functieBeginAanpasbaar = ((Boolean)launchData.get("functieBeginAanpasbaar")).booleanValue();
			if(launchData.containsKey("formeleFuncties"))
				formeleFuncties = ((Boolean)launchData.get("formeleFuncties")).booleanValue();
			if(launchData.containsKey("domeinInstelbaar"))
				domeinInstelbaar = ((Boolean)launchData.get("domeinInstelbaar")).booleanValue();
			if(launchData.containsKey("formuleComponentHoogte"))
				formuleComponentHoogte = ((Number)launchData.get("formuleComponentHoogte")).intValue();
			
			if(launchData.containsKey("functieToegestaan"))
				functieToegestaan = ((Boolean)launchData.get("functieToegestaan")).booleanValue();
			if(launchData.containsKey("ongelijkheidToegestaan"))
				ongelijkheidToegestaan = ((Boolean)launchData.get("ongelijkheidToegestaan")).booleanValue();
			if(launchData.containsKey("implicieteFunctieToegestaan"))
				implicieteFunctieToegestaan = ((Boolean)launchData.get("implicieteFunctieToegestaan")).booleanValue();
			if(launchData.containsKey("verticaleLijnToegestaan"))
				verticaleLijnToegestaan = ((Boolean)launchData.get("verticaleLijnToegestaan")).booleanValue();
			if(launchData.containsKey("parametrisatieToegestaan"))
				parametrisatieToegestaan = ((Boolean)launchData.get("parametrisatieToegestaan")).booleanValue();
			
			if(launchData.containsKey("xAsNaam"))
				xAsNaam = (String)launchData.get("xAsNaam");
			if(launchData.containsKey("yAsNaam"))
				yAsNaam = (String)launchData.get("yAsNaam");			
		}
		
		
		for(int i = 0; i < functieBegin.length; i++)
		{	if(formeleFuncties)
				functieBegin[i] = namen[i] + "(" + xAsNaam + ")=";
			else
				functieBegin[i] = yAsNaam + "=";
		}
		//prefix = "$ff(x)=@";
		//hasPrefix = true;
		/*
		if(h == null)
			return;
		
		this.h = h;
		if (h.get("breedte") != null)
			breedte = (Integer) h.get("breedte");
		if (h.get("hoogte") != null)
			hoogte = (Integer) h.get("hoogte");
		*/
		
		//Image regelMinderImg = new Image("images/resources/pijlterug.gif");
		//regelMinderImg.getElement().getStyle().setMargin(2, Unit.PX);
		verwijderRegelKnop = new PushButton(regelMinderButtonImage);
		verwijderRegelKnop.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		//verwijderRegelKnop.add();
		//verwijderRegelKnop.getElement().getStyle().setFloat(Style.Float.RIGHT);
		//regelMinderButton.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		//addButtonHandler(verwijderRegelKnop);
		verwijderRegelKnop.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				verwijderRegel();
			}
			
		});
		
		//Image regelMeerImg = new Image("images/resources/pijlterug.gif");
		//regelMeerImg.getElement().getStyle().setMargin(2, Unit.PX);
		nieuweRegelKnop = new PushButton(regelMeerButtonImage);
		nieuweRegelKnop.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		nieuweRegelKnop.addClickHandler(new ClickHandler(){
			
			@Override
			public void onClick(ClickEvent event) {
				voegRegelToe();
			}
		});
		//nieuweRegelKnop.getElement().getStyle().setFloat(Style.Float.RIGHT);
		//regelMeerButton.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		//addButtonHandler(nieuweRegelKnop);
		
		for(int i = 0; i < functieBeginViewers.length; i++) {	
			FormuleViewer f = new FormuleViewer(functieBegin[i]); // font is instellingen.font
			f.setColor(interactiePanel.getFormuleColor(i));
			f.setFont(defaultfont); // dit is het standaard font.
			f.setDefaultFont(defaultfont); // dit ook!
			f.setDefaultFont(defaultfont);
			functieBeginViewers[i] = f.getAsPanel();
			functieBeginViewers[i].getElement().getStyle().setProperty("display", "inline-block");
			functieBeginViewers[i].getElement().getStyle().setProperty("clear", "both");
			//functieBeginViewers[i].getElement().getStyle().setColor(interactiePanel.getFormuleColor(i).toString());
			//breedte functieBeginViewers instellen aan de hand van de lengte van de string functieBegin[i]. 
			//De breedte wordt nu mbv setWidgetLeftWidth op 50 gezet. Als er geen formule-functie-notatie is, dan is dat wat breed.
			//functieBeginViewers[i].setWidth((formeleFuncties?50:30) + "px");
			
			//functieBeginViewers[i].getElement().getStyle().setMarginLeft(5, Unit.PX);
		}
		
		for(int i = 0; i < checkboxen.length; i++)
		{	checkboxen[i] = new CheckBox();
			checkboxen[i].addClickHandler(new CheckBoxClickHandler(i));
			if(i==0)
				geselecteerd[i] = true;
			else
				geselecteerd[i] = false;
			checkboxen[i].setValue(geselecteerd[i]);
		}
		
		for(int i = 0 ; i < enOfKnoppen.length; i++)
		{	enOfKnoppen[i] = new PushButton(GraphToolGWT.rb.getString("enOfButton_En"));
			//enOfKnoppen[i].setMargin(new Insets(0,0,0,0));
			enOfKnoppen[i].setSize("24px", "19px");
			enOfKnoppen[i].getElement().getStyle().setPadding(1, Unit.PX);
			//enOfKnoppen[i].setOpaque(false);
			enOfKnoppen[i].addClickHandler(new EnOfKnopClickHandler(i));
		}
		
		for(int i = 0; i < domeinButtons.length; i++)
		{
			domeinButtons[i] = new DomeinButtonGWT();
		}
		
	
		//sp = new ScrollPanel();
		
		//sp.getElement().getStyle().setWidth(breedte - 5, Unit.PX);
		//sp.getElement().getStyle().setHeight(hoogte - 5, Unit.PX);
		//sp.getElement().getStyle().setOverflow(Overflow.AUTO);
		//sp.getElement().getStyle().setFloat(Style.Float.LEFT);
		
		LayoutPanel mainPanel = new LayoutPanel();
		final IsWidget wrap = wrap(mainPanel);
		this.add(wrap);
		this.setWidgetLeftWidth(wrap, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		this.setWidgetTopHeight(wrap, 0, Style.Unit.PX, hoogte, Style.Unit.PX); 
		FlowPanel rechthoekPanel = new FlowPanel();
		rechthoekPanel.getElement().getStyle().setBorderColor(CssColor.make(211, 211, 211).toString());
		rechthoekPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		rechthoekPanel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		mainPanel.add(rechthoekPanel);
		mainPanel.setWidgetLeftWidth(rechthoekPanel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		mainPanel.setWidgetTopHeight(rechthoekPanel, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		
		
		mainPanel.add(verwijderRegelKnop);
		mainPanel.add(nieuweRegelKnop);
		mainPanel.setWidgetLeftWidth(verwijderRegelKnop, breedte - 90, Style.Unit.PX, 40, Style.Unit.PX);
		mainPanel.setWidgetTopHeight(verwijderRegelKnop, 5, Style.Unit.PX, 30, Style.Unit.PX);
		mainPanel.setWidgetLeftWidth(nieuweRegelKnop, breedte - 45, Style.Unit.PX, 40, Style.Unit.PX);
		mainPanel.setWidgetTopHeight(nieuweRegelKnop, 5, Style.Unit.PX, 30, Style.Unit.PX);
		
		
		regelsPanel = new LayoutPanel();
		//contentPanel.addStyleName(graphToolCss.backgroundred());
		//contentPanel.getElement().getStyle().setPadding(5, Unit.PX);
		//contentPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		//contentPanel.getElement().getStyle().setProperty("display", "block");
		sp = new ScrollPanel(regelsPanel);
		sp.setWidget(regelsPanel);
		mainPanel.add(sp);
		mainPanel.setWidgetLeftWidth(sp, 1, Style.Unit.PX, breedte - 1, Style.Unit.PX);
		mainPanel.setWidgetTopHeight(sp, 30, Style.Unit.PX, hoogte - 31, Style.Unit.PX);
		
		//final IsWidget wrap = wrap(contentPanel);
		//this.add(wrap);
		//this.setWidgetLeftWidth(wrap, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		//this.setWidgetTopHeight(wrap, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		
		
		for(int i = 0; i < regelPanels.length; i++) {	
			/*regelPanels[i] = new FlowPanel();
			layoutRegelPanel(regelPanels[i]);
			highLight(regelPanels[i], true);
			regelPanels[i].add(checkboxen[i]);
			if (!functieBeginAanpasbaar)
				regelPanels[i].add(functieBeginViewers[i]);
		
			editors[i] = addNewEditor(regelPanels[i], i);//hoeft niet voor elke regel?
			if(functieBeginAanpasbaar && functieBeginZichtbaar)
				editors[i].insert(functieBegin[i]);
			if(domeinInstelbaar)
				regelPanels[i].add(domeinButtons[i]);
				*/
			regelPanels[i] = new LayoutPanel();
			layoutRegelPanel(regelPanels[i]);
			highLight(regelPanels[i], true);
			regelPanels[i].add(checkboxen[i]);
			//hier
			regelPanels[i].setWidgetLeftWidth(checkboxen[i], 5, Style.Unit.PX, 16, Style.Unit.PX);
			regelPanels[i].setWidgetTopHeight(checkboxen[i], 5, Style.Unit.PX, 15, Style.Unit.PX);
			
			if(!functieBeginAanpasbaar)
			{	regelPanels[i].add(functieBeginViewers[i]);
				regelPanels[i].setWidgetLeftWidth(functieBeginViewers[i], 20, Style.Unit.PX, formeleFuncties?43:30, Style.Unit.PX);
				regelPanels[i].setWidgetTopHeight(functieBeginViewers[i], 0, Style.Unit.PX, 30, Style.Unit.PX);
			}
			editors[i] = factor.build(i);
			if (functieBeginAanpasbaar)
				editors[i].getAsPanel().getElement().getStyle().setMarginLeft(13, Unit.PX);
			editors[i].getAsPanel().getElement().getStyle().setMarginTop(5, Unit.PX);
			editors[i].setFont(defaultfont);
			editors[i].setDefaultFont(defaultfont);

			editors[i].setColor(interactiePanel.getFormuleColor(i));
			editorPanels[i] = (TouchPanel) editors[i].getAsPanel();
			editorPanels[i].getElement().getStyle().setProperty("display", "inline-block");
			editors[i].setCurrent(0, 0);
			//kb = interactiePanel.kb; // THE ONE AND ONLY TODO betere interface naar interactiePanel.kb
			//editor.installKeyboard(kb);
			//editors[i].requestFocus();
			//if (!functieBeginAanpasbaar)
			//	regelPanels[i].add(functieBeginViewers[i]);
			regelPanels[i].add(editorPanels[i]);
			addFormulePanelListeners(editorPanels[i], editors[i]);
			
			regelPanels[i].setWidgetLeftRight(editorPanels[i], functieBeginAanpasbaar?20:(formeleFuncties?63:50), Style.Unit.PX, 
					domeinInstelbaar?20:0, Style.Unit.PX);
			regelPanels[i].setWidgetTopHeight(editorPanels[i], 0, Style.Unit.PX, 30, Style.Unit.PX);
			if(functieBeginAanpasbaar && functieBeginZichtbaar)
				editors[i].insert(functieBegin[i]);
			if(domeinInstelbaar)
			{	regelPanels[i].add(domeinButtons[i]);
				regelPanels[i].setWidgetRightWidth(domeinButtons[i], 0, Style.Unit.PX, 20, Style.Unit.PX);
				regelPanels[i].setWidgetTopHeight(domeinButtons[i], 0, Style.Unit.PX, 20, Style.Unit.PX);
			}
			editors[i].setCurrentElementRepaint();
		}
		
		regelsPanel.add(regelPanels[0]);
		regelsPanel.setWidgetLeftWidth(regelPanels[0], 0, Style.Unit.PX, breedte - 5, Style.Unit.PX);
		regelsPanel.setWidgetTopHeight(regelPanels[0], 0, Style.Unit.PX, Math.max(editors[0].getHeight(), 30), Style.Unit.PX);
		resize();
		//checkboxen[0].setValue(true);
		
		domeinStrings = new String[maxAantalFormules][2];
		for(int i = 0; i < maxAantalFormules; i++)
		{	domeinStrings[i][0] = "$f" + Double.NEGATIVE_INFINITY + "@";
			domeinStrings[i][1] = "$f" + Double.POSITIVE_INFINITY + "@";
		}
		
		DEFAULTDOMEIN = new double[2];
		DEFAULTDOMEIN[0] = Double.NEGATIVE_INFINITY;
		DEFAULTDOMEIN[1] = Double.POSITIVE_INFINITY;
		
		domeinen = new double[maxAantalFormules][2];
		for(int i = 0; i < maxAantalFormules; i++)
		{	domeinen[i][0] = DEFAULTDOMEIN[0];
			domeinen[i][1] = DEFAULTDOMEIN[1];
		}
		
		isOngelijkheid = new boolean[maxAantalFormules];
		for(int i = 0; i < maxAantalFormules; i++)
		{	isOngelijkheid[i] = false;
		
		}
		
		isEn = new boolean[maxAantalFormules];
		for(int i = 0; i<isEn.length; i++)
		{	isEn[i] = true;
		
		}
		fromuser = true;
		//contentPanel.getElement().addClassName("insert_formule_steps");
	}
	
	public boolean parsable(int regelnummer) {
		if(!fromuser) return false;
		String s = editors[regelnummer].toString();
        if(!functieBeginAanpasbaar)
        {     s = functieBegin[regelnummer] + s;
        }
        Object v = FormuleParser.parseVergelijking("$f" + s + "@");
        return v != null;
	}

	public void getImages() 
	{
		regelMinderButtonResource = graphToolGWTClientBundle.regelMinderButtonResource();
		regelMinderButtonImage = new Image(regelMinderButtonResource.getSafeUri());
		regelMinderButtonImage.addStyleName(graphToolCss.pushimage());
		
		regelMeerButtonResource = graphToolGWTClientBundle.regelMeerButtonResource();
		regelMeerButtonImage = new Image(regelMeerButtonResource.getSafeUri());
		regelMeerButtonImage.addStyleName(graphToolCss.pushimage());
	}
	
	/*
	public FormuleEditor addNewEditor(Panel p, int i)
	{
		FormuleEditor editor = new GraphtFormuleEditor(i);
		if (functieBeginAanpasbaar)
			editor.getAsPanel().getElement().getStyle().setMarginLeft(13, Unit.PX);
		editor.getAsPanel().getElement().getStyle().setMarginTop(5, Unit.PX);
		editor.setFont(defaultfont);
		editor.setColor(interactiePanel.getFormuleColor(i));
		//System.out.println("setColor: " + interactiePanel.getFormuleColor(i).toString());
		logger.info("setColor " + i + ":  " + interactiePanel.getFormuleColor(i).toString());
		TouchPanel tp = (TouchPanel) editor.getAsPanel();
		tp.getElement().getStyle().setProperty("display", "inline-block");
		editor.setCurrent(0, 0);
		//kb = interactiePanel.kb; // THE ONE AND ONLY TODO betere interface naar interactiePanel.kb
		//editor.installKeyboard(kb);
		editor.requestFocus();
		if (!functieBeginAanpasbaar)
			p.add(functieBeginViewers[i]);
		p.add(tp);
		
		addFormulePanelListeners(tp, editor);
		return editor;
	}*/
	
	private void addFormulePanelListeners(final TouchPanel tp, final FormuleEditor editor)
	{
		tp.addTouchHandler(new FormuleEditorTouchHandler(editor));
	}
	
	public void layoutRegelPanel(Widget w)
	{
		w.getElement().getStyle().setWidth(breedte - 5, Unit.PX);
		w.getElement().getStyle().setFloat(Float.LEFT);
		w.getElement().getStyle().setProperty("clear", "both");
		w.getElement().getStyle().setProperty("display", "block");
		w.getElement().getStyle().setBackgroundColor(hlColor.toString());
	}
	
	public void highLight(Widget w, boolean b)
	{
		w.getElement().getStyle().setBackgroundColor(b ? hlColor.toString() : bgColor.toString());
	}
	
	/*
	//weet nog niet of nuttig.
	public void zetInstellingen(HashMap<String, Object> instellingen)
	{
		this.instellingen = instellingen;
	}
	*/
	
	/*
	public void zetVoorvoegsel(int regelnummer)
	{	String huidigeTekst = editors[regelnummer].toString();
		boolean vervangen = huidigeTekst.equals("$f@") || huidigeTekst.endsWith("=@");
		if(functieBeginZichtbaar && functieBeginAanpasbaar && vervangen)
		{	editors[regelnummer].clearAll();
			if (formeleFuncties) 
				editors[regelnummer].insert("$f"+namen[regelnummer]+"(" + xAsNaam + ")=@");
			else if (regelnummer > 1)
				editors[regelnummer].insert("$f"+yAsNaam+"$s"+(regelnummer+1)+"@=@");
			else 
				editors[regelnummer].insert("$f"+yAsNaam+"=@");
		}
		else if(functieBeginZichtbaar)
		{	if (formeleFuncties) 
				functieBegin[regelnummer] = "$f"+namen[regelnummer]+"(" + xAsNaam + ")=@";
			else if (aantalRegels > 1)
				functieBegin[regelnummer] = "$f"+yAsNaam+"$s"+(regelnummer+1)+"@=@";
			else 
				functieBegin[regelnummer] = "$f"+yAsNaam+"=@";
		}	
	}*/
	
	public void zetGrafiekComponent(GrafiekGWTVeld gc)
	{	grafiekGWTVeld = gc;
		//grKeuze.setForeground(grafiekComponent.getColor(0));
	}
	
	public void zetGrafiekKleuren()
	{	
		if(editors != null && interactiePanel != null)
		{	for(int i=0 ; i<editors.length ; i++)
			{	editors[i].setColor(interactiePanel.getFormuleColor(i));
			
			}
		}
		//if(functieBeginViewers != null && interactiePanel != null)
		//	for(int i = 0; i < functieBeginViewers.length; i++)
		//		functieBeginViewers[i].getElement().getStyle().setColor(interactiePanel.getFormuleColor(i).toString());
		
		
	}
	
	public int getMaxAantalFuncties()
	{
		return maxAantalFormules;
	}
	
	/*
	public void terugNaarEenRegel(boolean setState)
	{	for (int rCnt = aantalRegels; rCnt > 1; rCnt--)
		{	
			interactiePanel.zetFunctie(aantalRegels - 1, null, "$f@", null, DEFAULTDOMEIN, true, setState, false);
			parseFormule("", aantalRegels - 1, false);
			regelsPanel.remove(regelPanels[aantalRegels - 1]);
			regelsPanel.remove(enOfKnoppen[aantalRegels-2]);
			isEn[aantalRegels - 2] = true; //gaat dit?
			aantalRegels--;
		}
	
	/*
		if(functieBeginZichtbaar)
		{	for(int i=0 ; i<maxAantalFormules ; i++)
			{	//zetVoorvoegsel(i);
			}
		}
		else
			for(int i = 0; i < maxAantalFormules; i++)
				formuleVakken[i].formuleVak.vulVak("$f@");
		
		parseFormule("$f@", 0, setState);
		*/
	//}
	
	
	public void zetAantalRegels(int aantalRegels, boolean setState)
	{
		if(aantalRegels >= this.aantalRegels)
			return;
		for (int rCnt = this.aantalRegels; rCnt > aantalRegels; rCnt--)
		{	
			interactiePanel.zetFunctie(this.aantalRegels - 1, null, "$f@", null, DEFAULTDOMEIN, true, setState, false);
			parseFormule("", this.aantalRegels - 1, false);
			regelsPanel.remove(regelPanels[this.aantalRegels - 1]);
			regelsPanel.remove(enOfKnoppen[this.aantalRegels-2]);
			isEn[this.aantalRegels - 2] = true; //gaat dit?
			this.aantalRegels--;
		}
	}
	
	public void zetMaxAantalFormules(int num, boolean setState)
	{	maxAantalFormules = num;
		boolean knoppenNodig = maxAantalFormules > 1;
		boolean checkboxenNodig = maxAantalFormules > 1 && (interactiePanel == null || interactiePanel.typeOpdracht == GraphToolGWT.GEENOPDRACHT); 
		nieuweRegelKnop.setVisible(knoppenNodig);
		verwijderRegelKnop.setVisible(knoppenNodig);
		checkboxen[0].setVisible(checkboxenNodig);
		//formuleX = knoppenNodig ? 30 : 10;
		if(aantalRegels > maxAantalFormules)
			aantalRegels = maxAantalFormules;
				
		//zetFormuleRegels(maxAantalFormules, setState);
		if(maxAantalFormules > domeinen.length)
		{	double[][] oudDomeinen = new double[domeinen.length][2];
			for(int i = 0; i < oudDomeinen.length; i++)
			{	oudDomeinen[i][0] = domeinen[i][0];
				oudDomeinen[i][1] = domeinen[i][1];
			}
			domeinen = new double[maxAantalFormules][2];
			for(int i = 0; i < oudDomeinen.length; i++)
			{	domeinen[i][0] = oudDomeinen[i][0];
				domeinen[i][1] = oudDomeinen[i][1];
			}
			for(int i = oudDomeinen.length; i < maxAantalFormules; i++)
			{	domeinen[i][0] = DEFAULTDOMEIN[0];
				domeinen[i][1] = DEFAULTDOMEIN[1];
			}
			String[][] oudDomeinStrings = new String[domeinStrings.length][2];
			for(int i = 0; i < oudDomeinen.length; i++)
			{	oudDomeinStrings[i][0] = domeinStrings[i][0];
				oudDomeinStrings[i][1] = domeinStrings[i][1];
			}
			domeinStrings = new String[maxAantalFormules][2];
			for(int i = 0; i < oudDomeinStrings.length; i++)
			{	domeinStrings[i][0] = oudDomeinStrings[i][0];
				domeinStrings[i][1] = oudDomeinStrings[i][1];
			}
			for(int i = oudDomeinStrings.length; i < maxAantalFormules; i++)
			{	domeinStrings[i][0] = "$f" + Double.NEGATIVE_INFINITY + "@";
				domeinStrings[i][1] = "$f" + Double.POSITIVE_INFINITY + "@";
			}
		}
		if(maxAantalFormules > isEn.length)
		{	boolean[] oudIsEn = new boolean[isEn.length];
			for(int i = 0; i < oudIsEn.length; i++)
			{	oudIsEn[i] = isEn[i];
			
			}
			isEn = new boolean[maxAantalFormules];
			for(int i = 0; i < oudIsEn.length; i++)
			{	isEn[i] = oudIsEn[i];
			
			}
			for(int i = oudIsEn.length; i < maxAantalFormules; i++)
			{	isEn[i] = true;
			
			}
			
		}
	}
	
	
	/*
	public void zetXAsNaam(String s, boolean setState)
	{	String oudeXAsNaam = xAsNaam;
		xAsNaam = s;
		for(int i=0 ; i<maxAantalFormules ; i++)
		{	String vervangString = editors[i].toString();
		
			//String vervangSubString = vervangString.substring(2, vervangString.length() - 1);
			//vervangString = "$f" + vervangSubString.replaceAll(oudeXAsNaam, xAsNaam) + "@";
			vervangString = vervangString.replaceAll(oudeXAsNaam, xAsNaam);
			editors[i].clearAll();
			editors[i].insert(vervangString);
			
			if(functieBeginZichtbaar)
			{	//vervangString = functieBeginViewers[i].toString();
				//vervangSubString = vervangString.substring(2, vervangString.length() - 1);
				//vervangString = "$f" + vervangSubString.replaceAll(oudeXAsNaam, xAsNaam) + "@";
				//vervangString = vervangString.replaceAll(oudeXAsNaam, xAsNaam);
				functieBegin[i] = functieBegin[i].replaceAll(oudeXAsNaam, xAsNaam);
				
				
				//((FormuleViewer) functieBeginViewers[i]).getCurrentRegel().insert(vervangString);
			}
			parseFormule(i, setState);
		}
	}
	
	public void zetYAsNaam(String s, boolean setState)
	{	String oudeYAsNaam = yAsNaam;
		yAsNaam = s;
		
		for(int i=0 ; i<maxAantalFormules ; i++)
		{	String vervangString = editors[i].toString();
			//String vervangSubString = vervangString.substring(2, vervangString.length() - 1);
			//vervangString = "$f" + vervangSubString.replaceAll(oudeYAsNaam, yAsNaam) + "@";
			vervangString = vervangString.replaceAll(oudeYAsNaam, yAsNaam);
			editors[i].clearAll();
			editors[i].insert(vervangString);
			
			if(functieBeginZichtbaar)
			{
				//vervangString = functieBegin[i];
				//System.out.println("vervangString y (kijk of substring nemen nodig is): " + vervangString);
				//vervangSubString = vervangString.substring(2, vervangString.length() - 1);
				//vervangString = "$f" + vervangSubString.replaceAll(oudeYAsNaam, yAsNaam) + "@";
				//vervangString = vervangString.replaceAll(oudeYAsNaam, yAsNaam);
				functieBegin[i] = functieBegin[i].replaceAll(oudeYAsNaam, yAsNaam);
				System.out.println("functieBegin["+ i + "]: " + functieBegin[i]);
			}
			
			
			parseFormule(i, setState);
			
		}
	}
	*/
	
	public void parseFormule(int regelnummer, boolean setState)
	{	//if(regelnummer >= viewers.size())
		//	return;
	
		String s = editors[regelnummer].toString();
        if(!functieBeginAanpasbaar)
        {     s = functieBegin[regelnummer] + s;
        }
        //System.out.println("parseFormule: " + s);
        parseFormule(s, regelnummer, setState);

	}
	
	public void updateFormulas() {
		for (int i=0; i<aantalRegels; i++ ) {
			parseFormule(i, false);
		}
	}
	
	public void parseFormule(String s, int regelnummer, boolean setState)
    {   
		//In alle lijstjes met expressies het huidige regelnummer verwijderen. 
          //Zo voorkom je dat expressies blijven staan als het type expressie verandert.
        if(interactiePanel != null && interactiePanel.typeOpdracht != 1 && regelnummer < domeinButtons.length)
        	domeinButtons[regelnummer].setVisible(false);
          isOngelijkheid[regelnummer] = false;
          if(interactiePanel != null)
          {     interactiePanel.zetOngelijkheid(regelnummer, null, true, true, false);
                interactiePanel.zetFunctie(regelnummer, null, "$f@", null, DEFAULTDOMEIN, true, setState, false);
                interactiePanel.zetVerticaleLijn(regelnummer, null);
          }
          
          try
          {
          //s = s.substring(2,s.length()-1);
          
	          if(s.length()==0)
	          {   //grafiekGWTVeld.paint();  
	        	  return;
	          }
	          String[] vergTekens = {"=", ">", "<", "\u2264", "\u2265"};
	          int tekenGetal = 0;
	          String[] expressieStrings = null;
	          Expressie e1 = null; //nu nog niet gebruikt, maar dat komt nog wel bij parametrisaties.
	          Expressie e2 = null;
	                
	          boolean split = false;
	          for(int j=0 ; j<vergTekens.length && !split; j++)
	          {	
	        	  expressieStrings  = split(s,vergTekens[j]);
	        	  //logger.info("vergTeken = " + vergTekens[j] + ", en aantal expressieStrings is: " + expressieStrings.length);
	              
	        	  if(expressieStrings.length==2)
	        	  {
	        		  e1 = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + expressieStrings[0] + "@")));
//	        		  logger.info("in splitten: expressieStrings[1] =  " + expressieStrings[1] + "; gaat nu parsen"); 
//	        		  String formuleString = FormuleParser.formuleString("$f" + expressieStrings[1] + "@");
//	        		  logger.info("FormuleParser.formuleString(blabla) = " + formuleString);
//	        		  String schoon = FormuleParser.schoon(formuleString);
//	        		  logger.info("FormuleParser.schoon(blabla) = " + schoon);
	        		  
	        		  
	        		  e2 = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + expressieStrings[1] + "@")));
	                      
	                  if(expressieStrings[0] == null || expressieStrings[1] == null) 
	                  {     split = false;
	                  }
	                  else 
	                  {     split = true;
	                        tekenGetal = j;
	                  }
	                  break;
	        	  }
	          }
	          if(!functieBeginAanpasbaar && expressieStrings.length == 2)
	        	  vulFunctieRegel(expressieStrings[0], expressieStrings[1], regelnummer);
	          
	  		    
	  		  if(!split)
	  		  {		//grafiekGWTVeld.paint();
	  			  return;
	  		  }
	  		  while(expressieStrings[0].endsWith(" "))
	  		  {    expressieStrings[0] = expressieStrings[0].substring(0, expressieStrings[0].length() - 1);
	  		  }
	          if(expressieStrings[0] == null || expressieStrings[1] == null)
	          {
	        	  //grafiekGWTVeld.paint();
	    		  return;
	          }
	          if(tekenGetal > 0 && !ongelijkheidToegestaan) // geval ongelijkheid
	          {	
	        	  editors[regelnummer].clearAll();
	        	  //formuleVakken[regelnummer].formuleVak.vulVak("$f@");
	        	  //grafiekGWTVeld.paint();
	        	  return;
	          }
		      else if(tekenGetal > 0)  
		      { if(expressieStrings[0].equals(xAsNaam))
		        {     boolean isGroterGelijk = true;
		              if(tekenGetal == 2 || tekenGetal == 3)
		                  isGroterGelijk = false;
		              if(checkboxen[regelnummer].getValue())
		                  interactiePanel.zetOngelijkheid(regelnummer, e2, false, isGroterGelijk, isEn[regelnummer]); 
		              isOngelijkheid[regelnummer] = true;
		        }
		        else if(expressieStrings[0].equals(yAsNaam))
		        {     boolean isGroterGelijk = true;
		              if(tekenGetal == 2 || tekenGetal == 3)
		                   isGroterGelijk = false;
		              if(checkboxen[regelnummer].getValue())
		                   interactiePanel.zetOngelijkheid(regelnummer, e2, true, isGroterGelijk, isEn[regelnummer]); 
		              isOngelijkheid[regelnummer] = true;
		        }
		      }//let op: neemt nu ook uitdrukkingen als sin(x) mee.
		      else //tekenGetal = 0
		      {    if(expressieStrings[0].equals(yAsNaam) || expressieStrings[0].endsWith("(" + xAsNaam + ")"))
			      {	if(!functieToegestaan)
			        {     //formuleVakken[regelnummer].formuleVak.vulVak("$f@");
			        //regel leegmaken
			    	  	//grafiekGWTVeld.paint();
			    	  	return;
			        }
				    else
				    { 
				        if(geselecteerd[regelnummer])
				        {   interactiePanel.zetFunctie(regelnummer, e2, "$f" + expressieStrings[1] +"@", expressieStrings[0], domeinen[regelnummer], true, setState, false);
				            domeinButtons[regelnummer].setVisible(domeinInstelbaar);
				        }
				        
				    } 
			      }
		          else if(expressieStrings[0].equals(xAsNaam))
		          { if(!verticaleLijnToegestaan)
		            {     //regel leegmaken
		                  //formuleVakken[regelnummer].formuleVak.vulVak("$f@");
		              //grafiekGWTVeld.paint();   
		        	  return;
		            }
		            else
		            {   if(geselecteerd[regelnummer])
		                        interactiePanel.zetVerticaleLijn(regelnummer, e2);
		            }
		          }
		          else
		            //regel leegmaken
		            //formuleVakken[regelnummer].formuleVak.vulVak("$f@");
		            ;
		      }
	      }
	      catch(Exception e){}
	      zetEnOfKnoppen();
	      //resize();
	      if(interactiePanel.typeOpdracht != GraphToolGWT.GEENOPDRACHT 
	    		  && interactiePanel.mode != OpdrNavIF.ZELFTOETS 
	    		  && interactiePanel.mode != OpdrNavIF.EINDTOETS)
	    	  interactiePanel.kijkNa();
	      else
	    	  interactiePanel.setChanged(false);
	      //grafiekGWTVeld.paint();
    }
	
	public void zetEnOfKnoppen()
	{	for(int i = 0; i < maxAantalFormules - 1; i++)
		{	if(i < enOfKnoppen.length)
			{
				if(isOngelijkheid[i] && isOngelijkheid[i+1])
					enOfKnoppen[i].setVisible(true);
				else
					enOfKnoppen[i].setVisible(false);
			}
		}
		
	}
	
	public void vulFunctieRegel(String deel1, String deel2, int regelnummer)
    {     
          //tekst prefix-viewer op deel1 zetten.
          //tekst formuleviewer/editor op deel2 zetten.
    
          //formuleVakken[regelnummer].functieBeginVak.vulVak("$f" + deel1 + "=@");
          //formuleVakken[regelnummer].formuleVak.vulVak("$f" + deel2 + "@");
    }


	
	 public static int count(String inputString, String patternString) 
	 {
	        int index = 0;
	        int count = 0;

	        /* -- */

	        while (true) {
	            index = inputString.indexOf(patternString, index);

	            if (index == -1) {
	                break;
	            } else {
	                index += patternString.length();
	                count++;
	            }
	        }

	        return count;
	}
	 
	public static String[] split(String inputString, String splitString) {
	        int index;
	        int count = count(inputString, splitString);
	        int upperBound = inputString.length();
	        String results[] = new String[count + 1];

	        /* -- */

	        index = 0;
	        count = 0;

	        while (index < upperBound) {
	            int nextIndex = inputString.indexOf(splitString, index);

	            if (nextIndex == -1) {
	                results[count++] = inputString.substring(index);
	                return results;
	            } else {
	                results[count++] = inputString.substring(index, nextIndex);
	            }

	            index = nextIndex + splitString.length();
	        }

	        // we should never get here

	        return results;
	}
	
	public void setState(Map<String, Object> launchState2, String[] randomVars, HashMap randomValues)
    {	//logger.fine("begin setState formuleComponent");
        //logger.info("maxAantalFormules: " + maxAantalFormules);
        //logger.info(String.valueOf(launchState2));
		fromuser = false;
		String[] expressieStrings = null;
    	boolean[] geselecteerd = null;
    	String[][] domeinStrings = null;
		boolean[] isEn = null;
    	
    	/*
    	if(h.containsKey("expressieStrings")) 
    		expressieStrings = (String[])h.get("expressieStrings");
    	if(h.containsKey("geselecteerd")) 
    		geselecteerd = (boolean[])h.get("geselecteerd");
    	if(h.containsKey("domeinStrings"))
    		domeinStrings = (String[][])h.get("domeinStrings");
    	if(h.containsKey("isEn")) 
        	isEn = (boolean[])h.get("isEn");
        */
    	if(launchState2 != null)
    	{
	    	if (launchState2.get("expressieStrings") != null) 
			{	//ArrayList<String> expressieStringsList = (ArrayList<String>) h.get("expressieStrings");
	    		expressieStrings = JSONUtilities.toStringArray(launchState2.get("expressieStrings"));
	    		//logger.info("expressieStrings = " + expressieStrings);
				/*
	    		expressieStrings = new String[expressieStringsList.size()];
				for(int i = 0; i < expressieStringsList.size(); i++)
					expressieStrings[i] = expressieStringsList.get(i);
					*/
			} else
				logger.severe("no strings");
	    	
	    	if (launchState2.get("geselecteerd") != null) 
			{	//ArrayList<Boolean> geselecteerdList = (ArrayList<Boolean>) h.get("geselecteerd");
	    		List<Object> geselecteerdList = JSONUtilities.toArrayList(launchState2.get("geselecteerd"));
				geselecteerd = new boolean[geselecteerdList.size()];
				for(int i = 0; i < geselecteerdList.size(); i++)
				{	geselecteerd[i] = (Boolean) geselecteerdList.get(i);
				
				}
			}
	    	if (launchState2.get("domeinStrings") != null)
			{	//ArrayList<ArrayList<String>> domeinStringsList = (ArrayList<ArrayList<String>>) h.get("domeinStrings");
	    		List<Object> domeinStringsList = JSONUtilities.toArrayList(launchState2.get("domeinStrings"));
	    		domeinStrings = new String[domeinStringsList.size()][2];
				//System.out.println("size is " + domeinStrings.length);
				for(int i = 0; i < domeinStringsList.size(); i++)
				{	domeinStrings[i] = JSONUtilities.toStringArray(domeinStringsList.get(i));
					//List<Object> lijstje = (List<Object>) domeinStringsList.get(i);
					//domeinStrings[i][0] = (String) lijstje.get(0);
					//domeinStrings[i][1] = (String) lijstje.get(1);
				}
			}
	    	if (launchState2.get("isEn") != null) 
			{	List<Object> isEnList = JSONUtilities.toArrayList(launchState2.get("isEn")); 
				isEn = new boolean[isEnList.size()];
				for(int i = 0; i < isEnList.size(); i++)
				{	isEn[i] = (Boolean) isEnList.get(i);
				
				}
				
				/*
	    		ArrayList<Boolean> isEnList = (ArrayList<Boolean>) h.get("isEn");
				isEn = new boolean[isEnList.size()];
				for(int i = 0; i < isEnList.size(); i++)
					isEn[i] = isEnList.get(i);
					*/
			}
	    	this.geselecteerd = geselecteerd;
	    	//System.out.println("geselecteerd.length: " + geselecteerd.length);
	    	this.isEn = isEn;
	    	this.domeinStrings = domeinStrings;
    	}
    	
	    	
    	if(domeinStrings != null)
    		domeinen = new double[domeinStrings.length][2];
     	
    	if(domeinStrings != null)
	    	for(int i=0 ; i<domeinStrings.length; i++)
			{	
	     		if(domeinStrings != null && i < domeinStrings.length && i < domeinButtons.length)
	     		{	if(!domeinStrings[i].equals("$f@"))
	    			{	if(randomValues != null)
	    				{	try
							{	domeinStrings[i][0] = FormuleParser.randomizeString(domeinStrings[i][0],randomVars,randomValues);
							}
							catch(Exception e)
							{	domeinStrings[i][0] = "$f???@";
								//this.zetRandomFout(true);
							}
							try
							{	domeinStrings[i][1] = FormuleParser.randomizeString(domeinStrings[i][1],randomVars,randomValues);
							}
							catch(Exception e)
							{	domeinStrings[i][1] = "$f???@";
								//this.zetRandomFout(true);
							}
	    				}
	    				zetDomein(domeinStrings[i], i);
	    			}
	     		domeinButtons[i].zetDomeinString(domeinStrings[i]);
	     		}
			}
    	
    	if(expressieStrings==null) 
    	{	return;
    	}
//    	logger.info("expressieStrings:");
//    	for(int i = 0; i < expressieStrings.length; i++)
//    		logger.info("expressieStrings[" + i + "]: " + expressieStrings[i]);
		for(int i = 0; i < expressieStrings.length; i++)	
     	{	if(expressieStrings[i].equals("$f@"))
     			expressieStrings[i] = "";
     		else if(expressieStrings[i].startsWith("$f") && expressieStrings[i].endsWith("@"))
			{	//expressieStrings[i] = expressieStrings[i].substring(2, expressieStrings[i].length() - 1);
				
     			if(randomVars != null)
				{	//logger.info("i = " + i + ": randomVars invullen. ExpressieString voor: " + expressieStrings[i]);
     				try			
	    			{	expressieStrings[i] = FormuleParser.randomizeString(expressieStrings[i],randomVars,randomValues);
	    				//logger.info("randomiseren gelukt, resultaat: " + expressieStrings[i]);
	    				expressieStrings[i] = expressieStrings[i].substring(2, expressieStrings[i].length()-1);
	    			}
	    			catch(Exception e)
	    			{	expressieStrings[i] = "???";
	    				//logger.log(Level.SEVERE, e.toString(), e);
	    				//this.zetRandomFout(true);
	    			}
     				//logger.info("expressieString na: " + expressieStrings[i]);
				}
				else
					expressieStrings[i] = expressieStrings[i].substring(2, expressieStrings[i].length() - 1);
    			//if(functieBeginAanpasbaar)
				//	formuleVakken[i].formuleVak.vulVak(expressieStrings[i]);
     			if(functieBeginAanpasbaar)
     			{	editors[i].clearAll();
     				//if(expressieStrings[i].startsWith("$f") && expressieStrings[i].endsWith("@"))
     					
     				editors[i].insert(expressieStrings[i]);
     				editors[i].setCurrentElementRepaint();
     				
     			}
     			else
     			{	editors[i].clearAll();
     				//System.out.println("functieString bij " + i + " voor knippen: " + expressieStrings[i]);
     				String functieString = expressieStrings[i].substring(2);
     				//System.out.println("functieString bij " + i + ": "  + functieString);
     				if(formeleFuncties)
     					functieString = functieString.substring(3);
     				editors[i].insert(functieString);
     				editors[i].setCurrentElementRepaint();
     			}
     			//logger.info("voor parseFormule: " + i);
     			parseFormule(expressieStrings[i], i, true);
     			//logger.info("na parseFormule: " + i);
				if(i>0 && expressieStrings[i].length() > 0 && !expressieStrings[i].endsWith("="))
					//add(formuleVakken[i],0);
				{	regelsPanel.remove(regelPanels[i]);
					regelsPanel.add(regelPanels[i]);
					
					regelsPanel.setWidgetLeftWidth(regelPanels[i], 0, Style.Unit.PX, breedte - 5, Style.Unit.PX);
					regelsPanel.setWidgetTopHeight(regelPanels[i], berekenRegelHoogte(i), Style.Unit.PX, Math.max(30, editors[i].getHeight()), Style.Unit.PX);
					if(interactiePanel != null && interactiePanel.typeOpdracht != GraphToolGWT.GEENOPDRACHT)
					{	checkboxen[aantalRegels].setVisible(false);
						//checkboxen[aantalRegels].setValue(true);
					}
					aantalRegels = i+1;
				}
				
     			//formuleVakken[i].setVisible(true);
				if(geselecteerd!=null)
					checkboxen[i].setValue(geselecteerd[i]);
				//add(checkboxen[i],0);
				//if(maxAantalFormules > 1)
				//	checkboxen[i].setVisible(true);
				//add(domeinButtons[i], 0);
				//domeinButtons[i].setVisible(false);
				//this.isEn[i] = isEn[i]; Niet nodig? Net al gelijk gezet?
				if(geselecteerd[i]) 
     				parseFormule(i, true);
				/*
				if(i>0)
				{	add(enOfKnoppen[i-1],0);
					if(isEn[i-1])
						enOfKnoppen[i-1].setText(GraphToolGWT.rb.getString("enOfButton_En"));
					else
						enOfKnoppen[i-1].setText(GraphToolGWT.rb.getString("enOfButton_Of"));
				}
				*/
     			
			}
			
		}
		resize();
     	//layoutVakken(true);
     	//grafiekComponent.updateTabelNames(geefExpNamen(), true);
		fromuser = true;
    }
	
	public int berekenRegelHoogte(int aantalRegels)
	{
		int y = 0;
		for(int i = 0; i < aantalRegels; i++)
		{
			y += Math.max(30, editors[i].getHeight()) + 5;
		}
		return y;
	}
	
	public void zetDomein(String[] domeinStrings, int i)
	{	
		if(domeinen.length > i)
		{	if(domeinStrings == null)
			{	domeinen[i][0] = DEFAULTDOMEIN[0];
				domeinen[i][1] = DEFAULTDOMEIN[1];
				return;
			}
			if(domeinStrings[0].equals("$f" + Double.NEGATIVE_INFINITY + "@"))
			{	domeinen[i][0] = Double.NEGATIVE_INFINITY;
			}
			else if(FormuleParser.geefExpressie(domeinStrings[0]) == null)
			{	domeinen[i][0] = Double.NEGATIVE_INFINITY;
			}
			else
			{	domeinen[i][0] = FormuleParser.geefExpressie(domeinStrings[0]).geefWaarde();
			}
			if(domeinStrings[1].equals("$f" + Double.POSITIVE_INFINITY + "@"))
				domeinen[i][1] = Double.POSITIVE_INFINITY;
			else if(FormuleParser.geefExpressie(domeinStrings[1]) == null)
				domeinen[i][1] = Double.POSITIVE_INFINITY;
			else
				domeinen[i][1] = FormuleParser.geefExpressie(domeinStrings[1]).geefWaarde();
		}
	}
	
	public HashMap<String,Object> getState()
	{
		/*
		ArrayList<String> expressieStrings = new ArrayList<String>();
		ArrayList<Boolean> geselecteerd = new ArrayList<Boolean>();
		ArrayList<ArrayList<String>> domeinStrings = new ArrayList<ArrayList<String>>();
		ArrayList<Boolean> isEn = new ArrayList<Boolean>();
		
		for(int i = 0; i < maxAantalFormules; i++)
		{	String s = editors[i].toString();
			if(!functieBeginAanpasbaar)
				s = functieBegin[i] + s;
			if(s.endsWith("="))
				s = "$f@";
			else
				s = "$f" + s + "@";
			expressieStrings.add(s);
			geselecteerd.add(checkboxen[i].getValue());
			isEn.add(this.isEn[i]);
		}
		*/
			
		Object[] expressieStrings = null;
		Object[] geselecteerd = null;
		Object[] domeinStrings = null;
		Object[] isEn = null;
		
		expressieStrings = new String[maxAantalFormules];
		geselecteerd = new Object[maxAantalFormules];
		isEn = new Object[maxAantalFormules];
		domeinStrings = this.domeinStrings;
		//int teller = 0;
		for(int i=0 ; i<maxAantalFormules ; i++)
		{	String hulp; expressieStrings[i] = hulp = editors[i].toString();
			if(!functieBeginAanpasbaar)
				expressieStrings[i] = functieBegin[i] + expressieStrings[i];
			if(hulp.endsWith("="))
				expressieStrings[i] = "$f@";
			else
				expressieStrings[i] = "$f" + expressieStrings[i] + "@";
			geselecteerd[i] = checkboxen[i].getValue();
			isEn[i] = this.isEn[i];
		}		
		
		HashMap<String,Object> h = new HashMap<String,Object>();
		h.put("expressieStrings", expressieStrings);
		h.put("geselecteerd", geselecteerd);
		h.put("domeinStrings", domeinStrings);
		h.put("isEn", isEn);
		return h;
	}
	
	public void verwijderRegel()
	{
		if(aantalRegels == 1)
			return;
		parseFormule("", aantalRegels - 1, false);
		if(functieBeginZichtbaar)
		{	//zetVoorvoegsel(aantalRegels - 1);
			//misschien nodig ivm setState en parsen
		}
		//else
		//	formuleVakken[aantalRegels - 1].formuleVak.vulVak("$f@");
		regelsPanel.remove(regelPanels[aantalRegels - 1]);
		if(interactiePanel != null && interactiePanel.typeOpdracht != GraphToolGWT.GEENOPDRACHT)
		{	checkboxen[aantalRegels-1].setValue(false);
			geselecteerd[aantalRegels - 1] = false;
		}
		
		//remove(formuleVakken[aantalRegels-1]);
		//remove(checkboxen[aantalRegels-1]);
		//remove(domeinButtons[aantalRegels-1]);
		//deze komt waarschijnlijk nog terug: 
		regelsPanel.remove(enOfKnoppen[aantalRegels-2]);
		isEn[aantalRegels - 2] = true;
		enOfKnoppen[aantalRegels-2].setText(GraphToolGWT.rb.getString("enOfButton_En"));
		//layoutVakken(false);
		aantalRegels--;
		
		grafiekGWTVeld.paint();
	}
	
	public void resize()
	{
		for(int i = 0; i < aantalRegels; i++)
		{
			int ashoogte = editors[i].getMainRegel().getAsHoogte();
			
			if(!functieBeginAanpasbaar)
			{	FormuleViewer f = new FormuleViewer(functieBegin[i]);
				if(regelPanels[i].getWidgetIndex(functieBeginViewers[i]) > -1)
					regelPanels[i].setWidgetTopHeight(functieBeginViewers[i], Math.max(0, ashoogte - f.getMainRegel().getAsHoogte()), Style.Unit.PX, f.getHeight(), Style.Unit.PX);
			}
			if(regelPanels[i].getWidgetIndex(editorPanels[i]) > -1)
				regelPanels[i].setWidgetTopHeight(editorPanels[i], 0, Style.Unit.PX, editors[i].getHeight(), Style.Unit.PX);
			if(regelPanels[i].getWidgetIndex(checkboxen[i]) > -1)
				regelPanels[i].setWidgetTopHeight(checkboxen[i], Math.max(ashoogte - 10, 5) , Style.Unit.PX, 15, Style.Unit.PX);
			if(regelsPanel.getWidgetIndex(regelPanels[i]) > -1)
				regelsPanel.setWidgetTopHeight(regelPanels[i], berekenRegelHoogte(i), Style.Unit.PX, Math.max(30, editors[i].getHeight()), Style.Unit.PX);
		}
	}
	
	public void voegRegelToe()
	{	if(aantalRegels >= maxAantalFormules)
		{	
			return;
		}
		parseFormule(aantalRegels - 1, false);
		regelsPanel.add(regelPanels[aantalRegels]);
		regelsPanel.setWidgetLeftWidth(regelPanels[aantalRegels], 0, Style.Unit.PX, breedte - 5, Style.Unit.PX);
		regelsPanel.setWidgetTopHeight(regelPanels[aantalRegels], berekenRegelHoogte(aantalRegels), Style.Unit.PX, Math.max(editors[aantalRegels].getHeight(), 30), Style.Unit.PX);
		if(interactiePanel != null && interactiePanel.typeOpdracht != GraphToolGWT.GEENOPDRACHT)
		{	checkboxen[aantalRegels].setValue(true);
			geselecteerd[aantalRegels] = true;
			checkboxen[aantalRegels].setVisible(false);
		}
		//else
		//	checkboxen[aantalRegels].setSelected(true);
		
		regelsPanel.add(enOfKnoppen[aantalRegels - 1]);
		regelsPanel.setWidgetLeftWidth(enOfKnoppen[aantalRegels - 1], breedte - 30, Style.Unit.PX, 25, Style.Unit.PX);
		regelsPanel.setWidgetTopHeight(enOfKnoppen[aantalRegels - 1], berekenRegelHoogte(aantalRegels) - 12, Style.Unit.PX, 20, Style.Unit.PX);
		//add(formuleVakken[aantalRegels],0);
		//zetVoorvoegsel(aantalRegels);	
		//add(checkboxen[aantalRegels],0);
		//add(domeinButtons[aantalRegels],0);
		//domeinButtons[aantalRegels].setVisible(false);
		//add(enOfKnoppen[aantalRegels - 1], 0);
		//layoutVakken(false);
		//formuleVakken[aantalRegels].formuleVak.requestFocus();
		aantalRegels++;
		grafiekGWTVeld.paint();
		editors[aantalRegels - 1].requestFocus();
	}
	
	public void setKeyboard(FormuleKeyboardIF kb)
	{
		//this.kb = kb;
	}
		
	
	
//	private void addButtonHandler(final TouchButton tb)
//	{
//		tb.addTouchHandler(new TouchHandler()
//		{
//			@Override
//			public void onTouchStart(TouchStartEvent event)
//			{
//				if(event.getSource().equals(verwijderRegelKnop))
//					verwijderRegel();
//				else if(event.getSource().equals(nieuweRegelKnop))
//					voegRegelToe();
//			}
//
//			@Override
//			public void onTouchMove(TouchMoveEvent event)
//			{
//			}
//
//			@Override
//			public void onTouchEnd(TouchEndEvent event)
//			{
//			}
//
//			@Override
//			public void onTouchCanceled(TouchCancelEvent event)
//			{
//			}
//		});
//	}
	
	class CheckBoxClickHandler implements ClickHandler
	{
		int regelnummer;
		
		public CheckBoxClickHandler(int i)
		{	super();
			regelnummer = i;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			geselecteerd[regelnummer] = !geselecteerd[regelnummer];
			parseFormule(regelnummer, false);
			grafiekGWTVeld.paint();
		}
	}
	
	class EnOfKnopClickHandler implements ClickHandler
	{
		int regelnummer;
		
		public EnOfKnopClickHandler(int i)
		{	super();
			regelnummer = i;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			isEn[regelnummer] = !isEn[regelnummer];
			if(isEn[regelnummer])
				enOfKnoppen[regelnummer].setText(GraphToolGWT.rb.getString("enOfButton_En"));
			else			
				enOfKnoppen[regelnummer].setText(GraphToolGWT.rb.getString("enOfButton_Of"));
			parseFormule(regelnummer, false);
			grafiekGWTVeld.paint();
			
			
			
			
		}
	}
}
