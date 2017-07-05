package fi.algebrapijlengwt.client;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;

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
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import fi.algebrapijlengwt.client.expressies_ap.*;

import fi.algebrapijlengwt.client.text.Text;

/**
 * Entry class voor AlgebraPijlenGWT<br>
 * M.b.v. AlgebraPijlenGWT construeert de gebruiker pijlenkettingen die
 * algebraische bewerkingen simuleren.<br>
 * Pijlenkettingen bestaan uit invoer/uitvoer-blokjes en bewerking-blokjes die
 * verbonden worden met een pijl; beide soorten blokjes hebben altijd maximaal
 * een(1) inkomende pijl, maar kunnen maximaal 10 uitgaande pijlen hebben.
 * Invoer is alleen mogelijk "vooraan" een pijlenketting en kan bestaan uit een
 * variabele of een getal;<br>
 * De bouwstenen voor de pijlenkettingen (deels instelbaar) liggen op stapels
 * (links) en worden vanaf de stapels naar het werkveld gesleept; aldaar worden
 * ze (door slepen) met pijlen verbonden; de uitgaande pijlen wijzen naar rechts
 * (en de inkomende pijl komt links binnen) of de uitgaande pijlen wijzen naar
 * links (en de inkomende pijl komt rechts binnen); via de linksRechtsKnop knop
 * is dit voor de stapels (dus de nieuwe blokjes) te kiezen; de aanwezigheid van
 * de linksRechtsKnop is een instelbare optie<br>
 * Relevante klassen: BewerkingSchuifComponent, UitvoerSchuifComponent, Pijl,
 * TabelComponent, GrafiekComponenet, AlgebraSchuifVeld;<br>
 * AlgebraPijlenGWT bestaat uit een Panel waarop een Canvas geplaatst wordt
 * waarop getekend wordt; bovenop het Canvas bevinden zich (instelbaar) nog de
 * linksRechtsKnop, de wisKnop, de tabelCheckBox, de GrafiekCheckBox de
 * kijkNaKnop (als er nagekeken wordt); ook de aanweziheid van de tabelCheckBox
 * en de GrafiekCheckBox zijn instelbare opties; <br>
 * de klasse AlgebraPijlenGWT luistert naar de knoppen/CheckBoxes en onderschept
 * Mouse/Touch Events op het Canvas;<br>
 * de klasse AlgebraSchuifVeld verwerkt de Mouse/Touch Events op het Canvas, de
 * acties op de knoppen/Checkboxes en de constructie van de pijlenkettingen.
 * 
 * @author Peter Boon
 */

public class AlgebraPijlenGWT implements EntryPoint, InteractionStub
{
	/**
	 * internationalisatie
	 */
	public static Text rb;

	static Logger logger = Logger.getLogger("APGWT");

	static final String holderId = "dockholder";
	static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	/**
	 * zie AftrekSchuifComponent.geefUitvoer(),
	 * DelingSchuifComponent.geefUitvoer()
	 */
	protected static boolean simplify = true;

	/**
	 * LayoutPanel waarop het Canvas en knoppen/CheckBoxes
	 */
	LayoutPanel canvasPanel;
	/**
	 * Canvas waarop pijlenkettingen getekenend worden
	 */
	SimplePanel simpel = new SimplePanel();

	Canvas algebraPijlenGWTCanvas;
	/**
	 * teken m.b.v. een Context2d
	 */
	Context2d algebraPijlenGWTContext2d;
	/**
	 * asv verwerkt Mouse/Touch Events, knop/CheckBox acties
	 */
	AlgebraSchuifVeld asv;

	/**
	 * toggle voor richting uitgaande pijlen van
	 * UitvoerSchuifComponenten/BewerkingSchuifComponenten: naar links of naar
	 * rechts?
	 */
	ToggleButton linksRechtsButton;
	/**
	 * wisknop voor het werkveld
	 */
	PushButton wisButton;
	/**
	 * checked: alle UitvoerSChuifComponenten op het werkveld hebben een tabel;
	 * unchecked: UitvoerSChuifComponenten op het werkveld hebben geen
	 * tabel;<br>
	 * individuele tabellen: zie klasse AlgebraSchuifVeld
	 */
	CheckBox tabelBox;
	/**
	 * checked: voeg een grafiekComponent toe aan het werkveld, unchecked:
	 * verwijder de grafiekComponent
	 */
	CheckBox grafiekBox;

	int breedte = 500;
	int hoogte = 450;
	/**
	 * layout: pixels
	 */
	int leftOffset = 5;
	/**
	 * layout: pixels
	 */
	int topOffset = 10;

	/**
	 * layout: pixels
	 */
	int buttonWidth = 60;
	/**
	 * layout: pixels
	 */
	int buttonHeight = 25;
	/**
	 * layout: pixels
	 */
	int checkBoxWidth = 60;

	AlgebraPijlenGWTClientBundle algebraPijlenGWTClientBundle;
	AlgebraPijlenGWTCssResource algebraPijlenGWTCss;

	/**
	 * launchdata
	 */
	private Map<String, Object> launchState;
	String[] randomVarNamen = null;
	HashMap<String, Object> randomVarWaarden = null;

	/**
	 * Touch Events: x position of the last touchDown
	 */
	int lastStartX;
	/**
	 * Touch Events: y position of the last touchDown
	 */
	int lastStartY;
	/**
	 * Touch Events: x position of the last touchMove<br>
	 * see inner class TouchHandler
	 */
	int lastMoveX;
	/**
	 * Touch Events: x position of the last touchMove
	 */
	int lastMoveY;

	/**
	 * toolkit = true: stapels en knoppen/CheckBoxes zoals ingesteld<br>
	 * toolkit = false: geen stapels en knoppen/checkboxes, dus alleen een
	 * gegeven pijlenketting die (als ketting) niet veranderd kan worden
	 */
	boolean toolkit = true;
	/**
	 * toolkit = false en in de gegeven pijlenketting(en) kunnen de
	 * invoerwaarden niet/wel veranderd worden
	 */
	boolean alleenInvullen = false;
	/**
	 * toolkit = false en alleenInvullen = false;
	 */
	boolean isDemo = false;

	/**
	 * parametrisatie: brugklas = true: geen omkeringen, wortels en machten<br>
	 * brugklas = false: alle bewerkingen mogelijk
	 */
	boolean brugklas = false;
	/**
	 * ' parametrisatie: kan de richting van de uitgaande pijlen veranderd
	 * worden?
	 */
	boolean terugHeen = true;
	/**
	 * parametrisatie: is de tabelCheckBox beschikbaar?
	 */
	boolean tabelOptie = true;
	/**
	 * parametrisatie: is de grafiek CheckBox beschikbaar?
	 */
	boolean grafiekOptie = true;
	/**
	 * parametrisatie: moet er nagekeken worden?
	 */
	boolean kijkNaActief = false;
	/**
	 * Of er extern moet worden nagekeken door een checkbutton. De nakijk-knop
	 * wordt dan verborgen.
	 */
	private boolean checkExternal = false;
	/**
	 * de kijkNaKnop
	 */
	PushButton kijkNaButton;
	/**
	 * Het panel waar de nakijkknop op komt en feedback-vinkjes/kruisje.
	 */
	LayoutPanel kijkNaPanel = new LayoutPanel();

	// deze images zijn nodig om te tekenen bij de afzonderlijke pijlenkettingen
	private Image vinkjeGroenImage;
	private Image vinkjeGeelImage;
	private Image kruisRoodImage;
	/**
	 * parametrisatie: de te maken Expressies als Strings
	 */
	List<String> docentExpressieStrings = new ArrayList<String>();
	/**
	 * de te maken Expressies, zie kijkNa()
	 */
	List<Expressie> docentExpressies = new ArrayList<Expressie>();
	/**
	 * parametrisatie
	 */
	int scoreMax = 10;
	int score = 0;
	/**
	 * flagg voor isCorrect()
	 */
	Boolean correct = null;
	/**
	 * is er iets veranderd op het werkveld
	 */
	boolean ingevuld = false;
	/**
	 * is er nagekeken?
	 */
	boolean nagekeken = false;

	private int mode;
	private OpdrNavIF comRoot;

	/**
	 * zet deze flagg aan voor setState en weer uit na setState om te voorkomen
	 * dat tijdens setState elke verandering een tekenOpnieuw veroorzaakt, zie
	 * klasse AlgebraSchuifVeld
	 */
	boolean asvSetState = false;

	/**
	 * t.b.v. het kopieren van een pijlenketting van de ene naar de andere
	 * opgave; <br>
	 * gedeactiveerd in klasse AlgebraSchuifVeld, want dit werkt niet in
	 * StubView:<br>
	 * het werkt nl. alleen als de instantie van AlgebraPijlenGWT "blijft leven"
	 * bij de overgang naar de andere opgave
	 */
	public static Map<String, Object> clipBoard = null;

	/**
	 * creeer internationalisatie en ClientBundle
	 */
	public void makeResources()
	{
		rb = GWT.create(Text.class);
		algebraPijlenGWTClientBundle = GWT.create(AlgebraPijlenGWTClientBundle.class);
		algebraPijlenGWTCss = algebraPijlenGWTClientBundle.getAlgebraPijlenGWTCSS();
		algebraPijlenGWTCss.ensureInjected();

		vinkjeGroenImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		vinkjeGeelImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
		kruisRoodImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
	}

	public void onModuleLoad()
	{
		makeResources();
		canvasPanel = new LayoutPanel();
		canvasPanel.setSize("" + breedte + "px", "" + hoogte + "px");
		RootPanel.get(holderId).add(canvasPanel); // deze regel uitzetten voor
													// standalone test
		RootPanel.get(holderId).addStyleName(algebraPijlenGWTCss.root());

		// RootLayoutPanel.get().add(simpel); // deze regel aanzetten voor
		// standalone test
		// simpel.setWidget(asWidget()); // deze regel aanzetten voor standalone
		// test

		Stub.publish(this); // deze regel uitzetten voor standalone test
		// init(breedte, hoogte, new HashMap<String, Object>(), new
		// HashMap<String, Number>()); // deze regel aanzetten voor standalone
		// test
	}

	/**
	 * maak de twee knoppen en de twee CheckBoxes links van het werkveld
	 * afhankelijk van de instellingen
	 */
	public void makeLeft()
	{
		if (!toolkit || isDemo || alleenInvullen)
			return;

		int currentX = (asv.toolsWidth - buttonWidth) / 2;
		int currentY = topOffset + 270;
		if (brugklas)
			currentY = topOffset + 195;
		linksRechtsButton = new ToggleButton(rb.linksLabel(), rb.rechtsLabel());
		linksRechtsButton.addStyleName(algebraPijlenGWTCss.togglebutton());
		if (terugHeen)
		{
			canvasPanel.add(linksRechtsButton);
			canvasPanel.setWidgetLeftWidth(linksRechtsButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(linksRechtsButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			linksRechtsButton.addClickHandler(new PushClickHandler());
			currentY += buttonHeight + topOffset;
		}

		currentX = 2 * leftOffset;
		tabelBox = new CheckBox();
		tabelBox.setText(rb.tabelLabel());
		tabelBox.addStyleName(algebraPijlenGWTCss.checkbox());
		if (tabelOptie)
		{
			canvasPanel.add(tabelBox);
			canvasPanel.setWidgetLeftWidth(tabelBox, currentX, Style.Unit.PX, checkBoxWidth + 10, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(tabelBox, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			tabelBox.addClickHandler(new PushClickHandler());
			currentY += buttonHeight + topOffset;
		}

		currentX = 2 * leftOffset;
		grafiekBox = new CheckBox();
		grafiekBox.setText(rb.grafiekLabel());
		grafiekBox.addStyleName(algebraPijlenGWTCss.checkbox());
		if (grafiekOptie)
		{
			canvasPanel.add(grafiekBox);
			canvasPanel.setWidgetLeftWidth(grafiekBox, currentX, Style.Unit.PX, checkBoxWidth + 20, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(grafiekBox, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
			grafiekBox.addClickHandler(new PushClickHandler());
			currentY += buttonHeight + topOffset;
		}

		currentX = (asv.toolsWidth - buttonWidth) / 2;
		wisButton = new PushButton(rb.wisKnopLabel());
		wisButton.addStyleName(algebraPijlenGWTCss.pushbutton());
		canvasPanel.add(wisButton);
		canvasPanel.setWidgetLeftWidth(wisButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(wisButton, currentY - 10, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		wisButton.addClickHandler(new PushClickHandler());
		currentY += buttonHeight + topOffset;
	}

	public AlgebraPijlenGWT()
	{
	}

	public AlgebraPijlenGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden)
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

		makeResources();
		canvasPanel = new LayoutPanel();
		canvasPanel.setSize("" + breedte + "px", "" + hoogte + "px");
		init(breedte, hoogte, launchState, randomVarWaarden);
	}

	/**
	 * inner class voor het afhandelen van Mouse Events op het
	 * algebraPijlenGWTCanvas
	 */
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		boolean mouseDown = false;

		public void onMouseDown(MouseDownEvent e)
		{
			e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			int eventX = e.getX();
			int eventY = e.getY();
			mouseDown = true;
			asv.mouseDownTouchStartAction(eventX, eventY);
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
			asv.mouseMoveTouchMoveAction(eventX, eventY);
		} // onMouseMove

		public void onMouseUp(MouseUpEvent e)
		{
			e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			mouseDown = false;
			int eventX = e.getX();
			int eventY = e.getY();
			asv.mouseUpTouchEndAction(eventX, eventY);
		}

	} // MouseHandler

	/**
	 * inner class voor het afhandelen van Touch Events op het
	 * algebraPijlenGWTCanvas;<br>
	 * onthoudt de het laatste TouchStart/TouchMove Event omdat het laatste
	 * TouchEnd Event niet de positie van het einde van de laatste Touch bevat
	 */

	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{

		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				int eventX = touch.getPageX() - algebraPijlenGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - algebraPijlenGWTCanvas.getAbsoluteTop();
				lastStartX = eventX;
				lastStartY = eventY;
				lastMoveX = -1000;
				lastMoveY = -1000;
				asv.mouseDownTouchStartAction(eventX, eventY);
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
				int eventX = touch.getPageX() - algebraPijlenGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - algebraPijlenGWTCanvas.getAbsoluteTop();
				lastMoveX = eventX;
				lastMoveY = eventY;
				asv.mouseMoveTouchMoveAction(eventX, eventY);
			}
			e.preventDefault();
			e.stopPropagation();
		}

		public void onTouchEnd(TouchEndEvent e)
		{
			int eventX = 0;
			int eventY = 0;
			// geen TouchMove
			if (lastMoveX <= -999)
			{
				eventX = lastStartX;
				eventY = lastStartY;
			}
			else // TouchMove
			{
				eventX = lastMoveX;
				eventY = lastMoveY;
			}
			asv.mouseUpTouchEndAction(eventX, eventY);
		}
	}

	/**
	 * inner class voor het afhandelen van Click Events op PushButtons en
	 * CheckBoxes
	 */
	class PushClickHandler implements ClickHandler
	{
		public void onClick(ClickEvent e)
		{
			// DIT NIET toevoegen!!
			// e.preventDefault();
			e.stopPropagation();
			if (e.getSource() == linksRechtsButton)
			{
				asv.linksRechtsAction();
			}
			else if (e.getSource() == wisButton)
			{
				asv.wisAction();
			}
			else if (e.getSource() == tabelBox)
			{
				boolean checked = tabelBox.getValue();
				asv.toonTabellen = checked;
				asv.zetVeranderd();
			}
			else if (e.getSource() == grafiekBox)
			{
				boolean checked = grafiekBox.getValue();
				asv.toonGrafiekComponent(checked);
				asv.zetVeranderd();
			}
			else if (e.getSource() == kijkNaButton)
			{
				kijkNa();
			}
		}
	}

	public Widget asWidget()
	{
		return canvasPanel;
	}

	/**
	 * Sluit geopende popup.
	 * 
	 * @return
	 */
	public void closeOpenedPopups()
	{
		asv.closeOpenedPopups();
	}

	/**
	 * Resize geopende popup.
	 * 
	 * @return
	 */
	public void resizePopup()
	{
		asv.resizePopup();
	}

	@Override
	public HashMap<String, Object> getState()
	{
		// sluit geopende popups
		closeOpenedPopups();

		HashMap<String, Object> h = asv.getState();
		h.put("nagekeken", new Boolean(nagekeken));
		h.put("ingevuld", new Boolean(ingevuld));

		return h;
	}

	@Override
	public void setState(HashMap<String, Object> map)
	{
		if (map == null || map.isEmpty())
			return;

		asvSetState = true;
		asv.setState(map);
		asvSetState = false;

		ObjectMap h = JSONUtilities.wrapMap(map);
		ingevuld = false;
		nagekeken = false;
		if (h.containsKey("nagekeken"))
		{
			nagekeken = h.getBoolean("nagekeken");
		}
		if (h.containsKey("ingevuld"))
		{
			ingevuld = h.getBoolean("ingevuld");
		}
		if (!ingevuld)
			asv.changed = false;
		
		if (ingevuld && (mode == 0 || nagekeken))
			kijkNa();
		
		asv.paint();
	}

	@Override
	public int getScore()
	{
		return score;
	}

	@Override
	public Boolean isCorrect()
	{
		if (isNakijkModus())
			return correct;
		else
			return new Boolean(true);
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());
		FormuleKeyboardIF kb = comRoot.getKeyboard();
		FormuleHolder.installKeyboard(kb);
	}

	public void zetMode(int mode)
	{
		this.mode = mode;
		if (kijkNaActief)
			kijkNaActief = (mode == 0 || mode == 1);
	}

	/**
	 * verander de docentExpressieStrings in docentExpressies
	 */
	public void maakDocentExpressies()
	{
		docentExpressies.clear();
		for (int i = 0; i < docentExpressieStrings.size(); i++)
		{
			String text = docentExpressieStrings.get(i);
			String formuleText = "$f" + text + "@";
			Expressie exp = FormuleParser_ap.geefExpressie(formuleText);
			docentExpressies.add(exp);
		}
	}

	/**
	 * als er nagekeken wordt en de gebruiker verandert de pijlenketting(en),
	 * zorg er dan voor dat het groene bolletje (als dat er al is) dat bij een
	 * nagekeken pijlenketting met resultaat "correct" hoort, weer verdwijnt
	 */
	public void answerChanged()
	{
		if ((comRoot != null) && isNakijkModus() && !asvSetState)
		{
			// reset alles
			correct = null;
			nagekeken = false;
			score = 0;
			ingevuld = true;
			asv.changed = true;
			setVisibleFeedbackImages(false, false, false);
			comRoot.setChanged(true);
		}
	}

	/**
	 * kijk na en bereken de score als volgt: als er een(1) pijlenketting
	 * gemaakt moet worden, kijk dan of die ergens op het werkveld staat; als er
	 * meerdere pijlenkettingen gemaakt moeten worden, vindt dan het percentage
	 * goede antwoorden
	 */
	public void kijkNa()
	{
		if (!isNakijkModus())
			return;

		ingevuld = true;
		maakDocentExpressies();

		// geen opdracht, alles goed
		if (docentExpressies.size() == 0)
		{
			score = scoreMax;
			return;
		}

		// vindt de UitvoerSchuifComponeneten aan het einde van alle valide
		// pijlenkettingen
		// op het werkveld (if any)
		Vector leerlingExpressieUVS = asv.vindExpressieUVS();

		// geen valide pijlenketting of niets ingevuld, fout
		if (leerlingExpressieUVS.size() == 0)
		{
			correct = new Boolean(false);
			comRoot.setChanged(isCorrect().booleanValue());
			return;
		}

		int hits = 0;

		// hier zijn er docent expressies
		// voor alle valide pijlenkettingen, vindt de uitvoer-Expressie en
		// vergelijk die met
		// de docent expressies
		for (int lCnt = 0; lCnt < leerlingExpressieUVS.size(); lCnt++)
		{
			UitvoerSchuifComponent uvs = (UitvoerSchuifComponent) leerlingExpressieUVS.elementAt(lCnt);
			// vindt de uitvoer Expressie
			Expressie llgExp = null;
			if (uvs.geefUitvoer(0) != null)
				llgExp = uvs.geefUitvoer(0);
			else if (uvs.geefVerborgenUitvoer(0) != null)
				llgExp = uvs.geefVerborgenUitvoer(0);
			// zorg dat de variabele x is
			String llgExpStr = llgExp.toString();
			String llgExpStrC = llgExpStr.replaceAll(uvs.geefBronDefaultVarnaam(), "x");
			llgExp = FormuleParser_ap.geefExpressie("$f" + llgExpStrC + "@");
			correct = new Boolean(false);
			if (llgExp != null)
			{
				for (int dCnt = 0; dCnt < docentExpressies.size(); dCnt++)
				{
					Expressie docExp = docentExpressies.get(dCnt);
					if (Algebra.isGelijkwaardig(docExp, llgExp))
					{
						hits++;
						correct = true;
					}
				}

				// zet een "V" rechts van de UitvoerSchuifComoponent
				if (correct.equals(Boolean.TRUE))
				{
					if (!uvs.pijlUit[0].isStapel && !uvs.pijlUit[0].vast && !uvs.pijlUit[0].actief)
					{
						uvs.pijlUit[0].im = "V";
						uvs.pijlUit[0].paint();
					}
				}
				else // zet een "X" rechts van de UitvoerSchuifComoponent
				{
					if (!uvs.pijlUit[0].isStapel && !uvs.pijlUit[0].vast && !uvs.pijlUit[0].actief) 
						// && (im != null))
					{
						uvs.pijlUit[0].im = "X";
						uvs.pijlUit[0].paint();
					}
				}
			}
		}

		// bereken score
		int scorePerExpressie = scoreMax / docentExpressies.size();
		if (hits == 0)
		{
			score = 0;
			setVisibleFeedbackImages(false, false, true);
		}
		else if (hits == docentExpressies.size())
		{
			score = scoreMax;
			setVisibleFeedbackImages(true, false, false);
		}
		else
		{
			score = hits * scorePerExpressie;
			setVisibleFeedbackImages(false, true, false);
		}

		asv.tekenOpnieuw();
		nagekeken = true;
		// bolletjes
		comRoot.setChanged(isCorrect().booleanValue());
	}

	@Override
	public void zetVolledigeBreedte(int breedte)
	{
	}

	@Override
	public int getAsHoogte()
	{
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
	public void setAsHoogte(int ashoogte)
	{
	}

	@Override
	public void init(int width, int height, Map<String, Object> map, Map<String, Number> values)
	{
		logger.info("AlgebraPijlenGWT init");

		this.breedte = width;
		this.hoogte = height;

		canvasPanel.setPixelSize(breedte, hoogte);

		ObjectMap launchState = JSONUtilities.wrapMap(map);

		makeResources();

		// parametrisatie
		if (launchState.containsKey("toolkit"))
			toolkit = launchState.getBoolean("toolkit");
		if (launchState.containsKey("alleenInvullen"))
			alleenInvullen = launchState.getBoolean("alleenInvullen");
		if (launchState.containsKey("isDemo"))
			isDemo = launchState.getBoolean("isDemo");
		if (launchState.containsKey("brugklas"))
			brugklas = launchState.getBoolean("brugklas");
		if (launchState.containsKey("terugHeen"))
			terugHeen = launchState.getBoolean("terugHeen");
		if (launchState.containsKey("tabelOptie"))
			tabelOptie = launchState.getBoolean("tabelOptie");
		if (launchState.containsKey("grafiekOptie"))
			grafiekOptie = launchState.getBoolean("grafiekOptie");
		// nakijken
		if (launchState.containsKey("docentExpressieStrings"))
		{
			docentExpressieStrings = launchState.getStringList("docentExpressieStrings");
		}
		if (launchState.containsKey("kijkNaActief"))
			kijkNaActief = launchState.getBoolean("kijkNaActief");
		if (launchState.containsKey("checkExternal"))
			checkExternal = launchState.getBoolean("checkExternal");
		if (launchState.containsKey("scoreMax"))
			scoreMax = launchState.getInt("scoreMax");

		int canvasBreedte = breedte;
		int canvasHoogte = hoogte;
		algebraPijlenGWTCanvas = Canvas.createIfSupported();
		algebraPijlenGWTCanvas.setWidth(canvasBreedte + "px");
		algebraPijlenGWTCanvas.setHeight(canvasHoogte + "px");
		algebraPijlenGWTCanvas.setCoordinateSpaceWidth(canvasBreedte);
		algebraPijlenGWTCanvas.setCoordinateSpaceHeight(canvasHoogte);
		algebraPijlenGWTCanvas.addStyleName("canvas");

		if (algebraPijlenGWTCanvas == null)
		{
			RootPanel.get(holderId).add(new Label(upgradeMessage));
			return;
		}

		algebraPijlenGWTCanvas.addStyleName(algebraPijlenGWTCss.canvas());

		MouseHandler mouseHandler = new MouseHandler();
		algebraPijlenGWTCanvas.addMouseDownHandler(mouseHandler);
		algebraPijlenGWTCanvas.addMouseMoveHandler(mouseHandler);
		algebraPijlenGWTCanvas.addMouseUpHandler(mouseHandler);

		TouchHandler touchHandler = new TouchHandler();
		algebraPijlenGWTCanvas.addTouchStartHandler(touchHandler);
		algebraPijlenGWTCanvas.addTouchMoveHandler(touchHandler);
		algebraPijlenGWTCanvas.addTouchEndHandler(touchHandler);

		canvasPanel.add(algebraPijlenGWTCanvas);
		canvasPanel.setWidgetLeftWidth(algebraPijlenGWTCanvas, 0, Style.Unit.PX, canvasBreedte, Style.Unit.PX);
		canvasPanel.setWidgetTopHeight(algebraPijlenGWTCanvas, 0, Style.Unit.PX, canvasHoogte, Style.Unit.PX);

		algebraPijlenGWTContext2d = algebraPijlenGWTCanvas.getContext2d();

		asv = new AlgebraSchuifVeld(0, 0, breedte, hoogte, algebraPijlenGWTContext2d, this);

		makeLeft(); // hier!!

		// map is altijd != null
		int aantalSc = 0;
		if (launchState.containsKey("aantalSc"))
			aantalSc = launchState.getInt("aantalSc");
		// dit wordt bijna altijd gedaan: in de launchdata zitten nl. ook de
		// stapels (bij toolkit = true)
		if (aantalSc > 0)
		{
			asvSetState = true;
			// hiermee kan de leerling de docentdata wissen
			// asv.setState(map);
			// hiermee kan de leerling de docentdadata niet wissen
			asv.setEditModeState(map);
			asvSetState = false;
		}

		if (isNakijkModus())
		{
			canvasPanel.add(kijkNaPanel);
			kijkNaPanel.setStylePrimaryName("kijknapanel");

			kijkNaButton = new PushButton(rb.kijkNa());
			kijkNaButton.addStyleName(algebraPijlenGWTCss.pushbutton());
			if (!checkExternal)
			{
				kijkNaPanel.add(kijkNaButton);
				kijkNaPanel.setWidgetLeftWidth(kijkNaButton, 0, Style.Unit.PX, 60, Style.Unit.PX);
				kijkNaPanel.setWidgetTopHeight(kijkNaButton, 0, Style.Unit.PX, 25, Style.Unit.PX);
			}
			kijkNaButton.addClickHandler(new PushClickHandler());

			canvasPanel.setWidgetLeftWidth(kijkNaPanel, (breedte - 70) / 2, Style.Unit.PX, 90, Style.Unit.PX);
			canvasPanel.setWidgetTopHeight(kijkNaPanel, hoogte - 40, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			setVisibleFeedbackImages(false, false, false);
		}

		canvasPanel.forceLayout();

		asv.paint();

		asv.changed = false;
		ingevuld = false;
	}

	/**
	 * Zet de feedback images zichtbaar adhv de meegegeven booleans.
	 * 
	 * @param vinkjeGroen
	 * @param vinkjeGeel
	 * @param kruisRood
	 */
	void setVisibleFeedbackImages(boolean vinkjeGroen, boolean vinkjeGeel, boolean kruisRood)
	{
		kijkNaPanel.setStyleName("goed", vinkjeGroen);
		kijkNaPanel.setStyleName("half", vinkjeGeel);
		kijkNaPanel.setStyleName("fout", kruisRood);
	}

	/**
	 * Retourneert true als nabouwen aanzichten in de nakijk-modus staat en moet
	 * nakijken.
	 * 
	 * @return
	 */
	private boolean isNakijkModus()
	{
		return kijkNaActief || checkExternal;
	}

	// @Override
	public void zetNagekeken(boolean b)
	{
	}

	// @Override
	public int[][] getScoreObjectives()
	{
		return null;
	}

	/**
	 * Het groene vinkje, om te tekenen in een context2d.
	 * 
	 * @return
	 */
	ImageElement getVinkjeGroen()
	{
		return ImageElement.as(vinkjeGroenImage.getElement());
	}

	/**
	 * Het gele vinkje, om te tekenen in een context2d.
	 * 
	 * @return
	 */
	ImageElement getVinkjeGeel()
	{
		return ImageElement.as(vinkjeGeelImage.getElement());
	}

	/**
	 * Het rode kruis, om te tekenen in een context2d.
	 * 
	 * @return
	 */
	ImageElement getKruisRood()
	{
		return ImageElement.as(kruisRoodImage.getElement());
	}
}