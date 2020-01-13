package fi.tekenveelvlakgwt.client;

import java.util.*;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.vaadin.pointerevents.client.PointerDownEvent;
import com.vaadin.pointerevents.client.PointerDownHandler;
import com.vaadin.pointerevents.client.PointerMoveEvent;
import com.vaadin.pointerevents.client.PointerMoveHandler;
import com.vaadin.pointerevents.client.PointerUpEvent;
import com.vaadin.pointerevents.client.PointerUpHandler;

/**
 * klasse die een (of meer) 3d-veelvlakken op een Canvas tekent; <br>
 * de klasse handelt ook Mouse en Touch Events op het Canvas af, te
 * weten het draaien van het 3d-veelvlak (instelbaar) en het kleuren
 * (ontkleuren) van een vlakje van het veelvlak door erop te klikken
 * (instelbaar)   
 * @author Peter Boon
 */
public class Viewer3d extends LayoutPanel
{
	/**
	 * eigenaar van deze Viewer3d
	 */
	TekenVeelvlakGWT tvGWT;

	/**
	 * Panel voor Canvas en nakijkPanel
	 */
	LayoutPanel alles;
	/**
	 * teken Canvas
	 */
	Canvas canvas;
	/**
	 * afhandelen mouse/Touch Events, zie klasse MuisBeheerder
	 */
	private MuisBeheerder mb;
	/**
	 * breeste en hoogte Canvas
	 */
	private int breedte, hoogte;
	
	/**
	 * initiele positie van de tekencursor
	 */
	private Punt3D startpunt;
	/**
	 * huidige positie van de tekencursor
	 */
	private Punt3D beginpunt;
	/**
	 * nieuwe positie van de tekencursor na beweging
	 */
	private Punt3D eindpunt;
	/**
	 * 3d-objecten, zie klasse Lichaam3D, alleen l[0] wordt gebruikt
	 */
	public Lichaam3D[] l;
	/**
	 * Context2d om te tekenen
	 */
	public Context2d gIm;
	/**
	 * matrix die de bewegingsrichting van de tekencursor onthoudt en update, 
	 * zie klasse Matrix3D 
	 */
	public Matrix3D mat;
	/**
	 * is de pen aan (punten worden verbonden)
	 */
	private boolean pen;
	/**
	 * is vul aan (punten worden opgespaard voor een polygon
	 * waarvan de vulling getekend wordt bij aanroep van vulUit() 
	 */
	private boolean vul;
	/**
	 * is het huidige polygon leeg, d.w.z. het heeft wel punten, maar
	 * wordt niet getekend
	 */
	private boolean leeg;
	/**
	 * moeten vlakjes van een 3d-veelvlak gekleurd worden met een schaduw-effect?
	 */
	private boolean schaduw;
	/**
	 * nummer van het huidige 3d-object
	 */
	private int lnummer;
	/**
	 * de kleur van de pen
	 */
	private CssColor penkleur;
	/**
	 * de kleur om polygons op te vullen
	 */
	private CssColor vulkleur;
	/**
	 * luisteren naar Mouse/Touch Events?
	 */
	public boolean muisAan;
	/**
	 * muisAan == true en klikAan == false: alleen slepen (roteren)<br>
	 * muisAan == true en klikAan == true: vlakkenkleuren, zie muisLosActie
	 */
	public boolean klikAan = false;
	/**
	 * zichtafstand: het oog bevindt zich in (0,0,afstand)
	 */
	private double afstand;
	/**
	 * het aantal Veelvlakken zichtbaar in deze Viewer3d
	 */
	int aantalVeelvlakken;
	/**
	 * de Veelvlakken in deze Viewer3d (vvRij[0] voor het veelvlak, vvRij[1]
	 * voor de voorkantpijl (indien gewenst)
	 */
	Veelvlak[] vvRij;
	/**
	 * een vergroot/verklein factor afhankelijk van de
	 * breedte van het Canvas
	 */
	double k; 
	
	/**
	 * de initiele draaiing van het 3d-veelvlak om de x-as resp. de y-as
	 */
	double beginx, beginy;
	/**
	 * actuele draaiing van het 3d-veelvlak om de x-as is beginx+xhoek;<br> 
	 * xhoek wordt veranderd bij slepen, zie methode muisSleepActie 
	 */
	double xhoek;
	/**
	 * actuele draaiing van het 3d-veelvlak om de y-as is beginy+yhoek;<br> 
	 * yhoek wordt veranderd bij slepen, zie methode muisSleepActie 
	 */
	double yhoek;

	/**
	 * initiele draaiingen voor setState (zie aldaar) 
	 */
	double draaiX = 20, draaiY = -30;

	/**
	 * array met vlakjes (geprojecteerd) van vvRij[0], zie methode muisKkActie()
	 */
	Polygon[] p;

	/**
	 * de positie van het veelvlak in de viewer, zie klasse TekenVeelvlakGWT<br>
	 * de positie MOVEABLE (muisAan == true) of een aanzicht (muisAan == false), zie
	 * methode zetViewerPosition 
	 */
	int viewerPosition = TekenVeelvlakGWT.MOVEABLE;
	
	/**
	 * true: vrij draaien, false: het 3d-veelvlak kan niet helemaal om de x-as gedraaid worden
	 */
	boolean restrictRotation = true;

	/**
	 * parameters gebruikt om k te berekenen, zie methodes zetZoomFac
	 * en setBounds  
	 */
	double k50;
	double kMinFac = 60e-2d;
	double kMaxFac = 140e-2d;
	double zoomFac = 1.0;

	/**
	 * is een pijl die naar de voorkant van de figuur wijst zichtbaar?
	 */
	boolean voorkantPijlZichtbaar = false;
	/**
	 * index van de pijl (een veelvlak) die naar de voorkant van de figuur wijst in vvRij (-1 als geen pijl)
	 */
	int voorkantPijlIndex = -1;

	/**
	 * kunnen vlakken van de 3d-figuur gekleurd worden door aanklikken?
	 */
	boolean vlakkenKleurenOptie = false;

	/**
	 * kijkNaKnop
	 */
	PushButton kijkNaButton;
	/**
	 * Panel met kijkNaKnop
	 */
	LayoutPanel kijkNaPanel;

	/**
	 * niet null indien deze Viewer3d onderdeel is van VaktekPanel vaktek
	 * (zie klasse VaktekPanel)
	 */
	VaktekPanel vaktek = null;

	/**
	 * rand om de viewer tekeken?
	 */
	boolean border = false;

	/**
	 * constructor, creert een Viewer3d met een leeg Veelvlak:<br>
	 * creeer het Canvas met mouse/TouchHandlers, initieer attributen
	 * en het nakijkgebeuren (indien gewenst) 
	 * @param x x-coordinaat
	 * @param y y-coordinaat
	 * @param b breedte
	 * @param h hoogte
	 * @param tvGWT eigenaar
	 */
	public Viewer3d(int x, int y, int b, int h, TekenVeelvlakGWT tvGWT)
	{
		this(new Veelvlak(), x, y, b, h, tvGWT);
	}

	/**
	 * constructor, creert een Viewer3d met Veelvlak v
	 * creeer het Canvas met mouse/TouchHandlers, initieer attributen
	 * en het nakijkgebeuren (indien gewenst) 
	 * @param v Veelvlak 
	 * @param x x-coordinaat
	 * @param y y-coordinaat
	 * @param b breedte
	 * @param h hoogte
	 * @param tvGWT eigenaar
	 */
	public Viewer3d(Veelvlak v, int x, int y, int b, int h, TekenVeelvlakGWT tvGWT)
	{
		this.tvGWT = tvGWT;

		// op alles zetten we canvas en nakijkpanel
		alles = new LayoutPanel(); // docklayout proberen?
		
		int hoogteAlles;
		if (tvGWT.isNakijkModus())
			hoogteAlles = h + 30; // ruimte voor nakijkpanel
		else
			hoogteAlles = h;
		
		// maak het canvas waar de 3d-viewer op getekend wordt
		canvas = Canvas.createIfSupported();
		
		canvas.setWidth(b + "px");
		canvas.setHeight(h + "px");
		canvas.setCoordinateSpaceWidth(b);
		canvas.setCoordinateSpaceHeight(h);
		canvas.addStyleName(tvGWT.tekenVeelvlakGWTCssResource.canvas());
		breedte = b;
		hoogte = h;

		alles.add(canvas);
		alles.setWidgetLeftWidth(canvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		alles.setWidgetTopHeight(canvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);

		setBounds(x, y, b, h); // dit zet niet de maat van this...?

		aantalVeelvlakken = 1;
		vvRij = new Veelvlak[5];
		vvRij[0] = v;
		p = new Polygon[v.aantalVlakken + 4];
		mb = new MuisBeheerder(this);
		canvas.addMouseDownHandler(mb);
		canvas.addMouseUpHandler(mb);
		canvas.addMouseMoveHandler(mb);
		canvas.addTouchStartHandler(mb);
		canvas.addTouchEndHandler(mb);
		canvas.addTouchMoveHandler(mb);
		(canvas.asWidget()).addDomHandler((PointerMoveHandler)mb, PointerMoveEvent.getType()); 
		(canvas.asWidget()).addDomHandler((PointerUpHandler)mb, PointerUpEvent.getType()); 
		(canvas.asWidget()).addDomHandler((PointerDownHandler)mb, PointerDownEvent.getType()); 
		

		leeg = false;
		schaduw = true;
		muisAan = true;
		l = new Lichaam3D[5];
		afstand = 1000;
		lnummer = 0;
		for (int i = 0; i < 5; i++)
		{
			l[i] = new Lichaam3D();
			l[i].zetAfstand(afstand);
		}
		mat = new Matrix3D();
		k = 180; // k=200;
		xhoek = 0;
		yhoek = 0;
		beginx = 20;
		beginy = -30;

		kijkNaButton = new PushButton(TekenVeelvlakGWT.rb.kijkNaLabel());
		kijkNaButton.addClickHandler(new PushClickHandler());
		kijkNaButton.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());

		kijkNaPanel = new LayoutPanel();
		kijkNaPanel.setStylePrimaryName("kijknapanel");
		kijkNaPanel.setPixelSize(100, 25);

		if (!tvGWT.isCheckExternalModus())
		{
			kijkNaPanel.add(kijkNaButton);
			kijkNaPanel.setWidgetLeftWidth(kijkNaButton, 0, Style.Unit.PX, 80, Style.Unit.PX);
			kijkNaPanel.setWidgetTopHeight(kijkNaButton, 0, Style.Unit.PX, 25, Style.Unit.PX);
		}

		add(alles);
		setWidgetLeftWidth(alles, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		setWidgetTopHeight(alles, 0, Style.Unit.PX, hoogteAlles, Style.Unit.PX);
		
		setWidgetVisible(alles, true);
		alles.setWidgetVisible(canvas, true);
	}

	/**
	 * initialiseer de Context2d van het Canvas
	 */
	public void initContext2d()
	{
		gIm = canvas.getContext2d();
	}

	/**
	 * een nieuwe breedte levert een nieuw waarde voor k  
	 * @param x x-coordinaat niet gebruikt
	 * @param y y-coordinaat niet gebruikt
	 * @param b breedte nieuwe breedte
	 * @param h hoogte niet gebruikt
	 */
	public void setBounds(int x, int y, int b, int h)
	{
		k50 = 180 - (350 - b) * 25e-1d / 18;
		double kMin = kMinFac * k50;
		double kMax = kMaxFac * k50;
		k = zoomFac * (kMax - kMin) + kMin;
		tekenOpnieuw();
	}
	
	/**
	 * zet de optie vlakkenKleuren
	 * @param b true/false
	 */
	public void zetVlakkenKleurenOptie(boolean b)
	{
		vlakkenKleurenOptie = b;
	}

	/**
	 * zet een nieuwe zoom factor, herbereken k 
	 * @param zFac nieuwe zoom factor
	 */
	public void zetZoomFac(double zFac)
	{
		zoomFac = zFac;
		k50 = 180 - (350 - breedte) * 25e-1d / 18;
		double kMin = kMinFac * k50;
		double kMax = kMaxFac * k50;
		k = zoomFac * (kMax - kMin) + kMin;
	}

	/**
	 * zet de positie van het 3d-veelvlak in de viewer: <br>
	 * de positie is MOVEABLE (muisAan == true) of een aanzicht (muisAan == false) 
	 * @param vPos zie klasse tekenVeelvlakGWT
	 */
	public void zetViewerPosition(int vPos)
	{
		muisAan = false;
		viewerPosition = vPos;

		if (vPos == TekenVeelvlakGWT.MOVEABLE)
		{
			muisAan = true;
			schaduw = true;
			zetBeginHoeken(draaiX, draaiY);
			zetAfstand(1000);
		}
		else if (vPos == TekenVeelvlakGWT.FRONTVIEW)
		{
			zetBeginHoeken(0, 0);
			zetAfstand(100000);
			schaduw = false;
		}
		else if (vPos == TekenVeelvlakGWT.BACKVIEW)
		{
			zetBeginHoeken(0, 180);
			zetAfstand(100000);
			schaduw = false;
		}
		else if (vPos == TekenVeelvlakGWT.TOPVIEW)
		{
			zetBeginHoeken(90, 0);
			zetAfstand(100000);
			schaduw = false;
		}
		else if (vPos == TekenVeelvlakGWT.BOTTOMVIEW)
		{
			zetBeginHoeken(-90, 0);
			zetAfstand(100000);
			schaduw = false;
		}
		else if (vPos == TekenVeelvlakGWT.LEFTVIEW)
		{
			zetBeginHoeken(0, 90);
			zetAfstand(100000);
			schaduw = false;
		}
		else if (vPos == TekenVeelvlakGWT.RIGHTVIEW)
		{
			zetBeginHoeken(0, -90);
			zetAfstand(100000);
			schaduw = false;
		}
	}

	/**
	 * stop de kleurnamen van de vlakken van het veelvlak vvRij[0] in een array
	 * (in de zelfde volgorde als de vlakken in vvRij[0]) 
	 * @return array met kleurnamen
	 */
	public String[] getKleuren()
	{
		String[] kleuren = new String[vvRij[0].aantalVlakken];
		for (int vCnt = 0; vCnt < kleuren.length; vCnt++)
		{
			kleuren[vCnt] = vvRij[0].vlakken[vCnt].vulkleur;
		}
		return kleuren;
	}

	/**
	 * gegeven een array met kleurnamen, geef de vlakken van het veelvlak
	 * vvRij[0] die kleuren (in dezelfde volgorde);<br>
	 * aanname: aantal kleuren == aantal vlakken, geen error check   
	 * @param kleuren array met kleurnamen
	 */
	public void zetKleuren(String[] kleuren)
	{
		for (int vCnt = 0; vCnt < kleuren.length; vCnt++)
		{
			vvRij[0].vlakken[vCnt].vulkleur = kleuren[vCnt];
		}

	}

	/** 
	 * kijk of de huidige kleuren van het veelvlak in deze viewer overeenkomen met
	 * de kleur(namen) in het array nakijkKleuren
	 * @param nakijkKleuren door docent voorgeschreven kleuren
	 * @return true/false
	 */
	public boolean evalueer(String[] nakijkKleuren)
	{
		String[] viewerKleuren = getKleuren();
		boolean result = true;
		for (int i = 0; i < viewerKleuren.length; i++)
		{
			result = result && viewerKleuren[i].equals(nakijkKleuren[i]);
		}

		return result;
	}

	/**
	 * kijk of de huidige positie van het veelvlak in deze viewer
	 * overen komt de door de docent opgegeven positie
	 * @param gevraagdX docent draaing om de x-as 
	 * @param gevraagdY docent draaing om de y-as
	 * @return true/false
	 */
	public boolean evalueer(double gevraagdX, double gevraagdY)
	{
		double drx = geefDraaiX();
		double dry = geefDraaiY();
		double tol = 20;

		gevraagdX = putInRange(gevraagdX);
		gevraagdY = putInRange(gevraagdY);
		
		drx = putInRange(drx);
		dry = putInRange(dry);

		double verschilX = Math.min(Math.abs(gevraagdX - drx), gevraagdX - drx > 0 ? Math.abs(gevraagdX - drx - 360) : Math.abs(gevraagdX - drx + 360));
		double verschilY = Math.min(Math.abs(gevraagdY - dry), gevraagdY - dry > 0 ? Math.abs(gevraagdY - dry - 360) : Math.abs(gevraagdY - dry + 360));
		
		if ((Math.abs(verschilX) < tol) && (Math.abs(verschilY) < tol))
			return true;
		else
			return false;
	}

	/**
	 * maak van een gegeven hoek (in graden) een
	 * equivalente hoek tussen 0 en 360 graden
	 * @param hoek gegegen hoek
	 * @return hoek tussen 0 en 360
	 */
	public double putInRange(double hoek)
	{
		double inRange = hoek;
		if (hoek < 0)
			inRange = hoek + 360;
		else if (hoek > 360)
			inRange = hoek - 360;
		return inRange;
	}

	/**
	 * zet het veelvlak in deze viewer waarvan de status is opgeslagen in
	 * de Map map; als deze viewer geen deel uitmaakt van een VaktekPanel,
	 * zet dan ook de zoomFactor en de positie van het veelvlak 
	 * @param map Map met de status van een veelvlak
	 */
	public void setState(Map map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);

		double[] hoekpunten = null;
		List<Double> hoekpuntenAL = null;
		int[] vlakken = null;
		List<Integer> vlakkenAL = null;
		int[] lijnen = null;
		List<Integer> lijnenAL = null;

		List<String> kleurenAL = new ArrayList<String>();

		if (h.containsKey("hoekpunten"))
		{
			hoekpuntenAL = h.getDoubleList("hoekpunten");
			if (hoekpuntenAL == null)
			{
				hoekpunten = h.getDoubleArray("hoekpunen");
			}
			else
			{
				hoekpunten = new double[hoekpuntenAL.size()];
				for (int hp = 0; hp < hoekpuntenAL.size(); hp++)
					hoekpunten[hp] = hoekpuntenAL.get(hp).doubleValue();
			}
		}
		if (h.containsKey("vlakken"))
		{
			vlakkenAL = h.getIntegerList("vlakken");
			if (vlakkenAL == null)
			{
				vlakken = h.getIntArray("vlakken");
			}
			else
			{
				vlakken = new int[vlakkenAL.size()];
				for (int v = 0; v < vlakkenAL.size(); v++)
					vlakken[v] = vlakkenAL.get(v).intValue();
			}
		}
		if (h.containsKey("lijnen"))
		{
			lijnenAL = h.getIntegerList("lijnen");
			if (lijnenAL == null)
			{
				lijnen = h.getIntArray("lijnen");
			}
			else
			{
				lijnen = new int[lijnenAL.size()];
				for (int l = 0; l < lijnenAL.size(); l++)
					lijnen[l] = lijnenAL.get(l).intValue();
			}
		}

		if (vaktek != null)
		{
			Veelvlak v = new Veelvlak(hoekpunten, vlakken, lijnen);
			vvRij[0] = v;

			return;
		}

		if (h.containsKey("kleuren"))
			kleurenAL = h.getStringList("kleuren");

		double zoomFac = 1.0;
		double draaiX = 20;
		double draaiY = -30;

		boolean muisAan = true;

		int viewerPosition = TekenVeelvlakGWT.MOVEABLE;

		if (h.containsKey("zoomFac"))
			zoomFac = h.getDouble("zoomFac");
		if (h.containsKey("draaiX"))
			draaiX = h.getDouble("draaiX");
		if (h.containsKey("draaiY"))
			draaiY = h.getDouble("draaiY");

		if (h.containsKey("muisAan"))
			muisAan = h.getBoolean("muisAan");

		if (h.containsKey("viewerPosition"))
			viewerPosition = h.getInt("viewerPosition");
		
		// niet gebruikt
		String[] kleuren = new String[kleurenAL.size()];
		for (int s = 0; s < kleurenAL.size(); s++)
			kleuren[s] = kleurenAL.get(s);

		zetZoomFac(zoomFac);

		this.draaiX = draaiX;
		this.draaiY = draaiY;
		zetViewerPosition(viewerPosition);

		this.muisAan = muisAan;

		Veelvlak v = new Veelvlak(hoekpunten, vlakken, lijnen);
		vvRij[0] = v;

	}

	/**
	 * deze viewer maakt deel uit van een VaktekPanel;
	 * zorg dat de kleuren van de verschillende aanzichten in dit VaktekPanel
	 * met elkaar overeenkomen; <br>
	 * zie methode synchronizeViewerKleuren in klasse VaktekPanel  
	 */
	public void updateViewerKleuren()
	{
		if (vaktek != null)
		{
			vaktek.synchronizeViewerKleuren(this);
		}
	}

	/**
	 * zet de afstand van het oog (op de positive z-as)
	 * tot het x-y vlak
	 * @param afst nieuwe afstand
	 */
	public void zetAfstand(double afst)
	{
		afstand = afst;
		for (int i = 0; i < 5; i++)
		{
			l[i].zetAfstand(afst);
		}
	}

	/**
	 * zet schaduw-effect bij het tekeken van de vlakjes van het vleelvlak 
	 * @param s true/false
	 */
	public void zetSchaduw(boolean s)
	{
		schaduw = s;
	}

	/**
	 * zet de beginhoeken van het veelvlak
	 * @param hx draaing om de x-as
	 * @param hy draaing om de y-as
	 */
	public void zetBeginHoeken(double hx, double hy)
	{
		beginx = hx;
		beginy = hy;
		if (restrictRotation)
		{
			if (beginx > 90)
				beginx = 90;
			if (beginx < -90)
				beginx = -90;
		}
		xhoek = 0;
		yhoek = 0;
	}

	/**
	 * geef de huidige draaing van het veelvlak om de x-as
	 * @return beginx+xhoek
	 */
	public double geefDraaiX()
	{
		return beginx + xhoek;
	}

	/**
	 * geef de huidige draaing van het veelvlak om de y-as
	 * @return beginy+yhoek
	 */
	public double geefDraaiY()
	{
		return beginy + yhoek;
	}

	/**
	 * kunnen vlakjes van het veelvlak aangeklikt worden?
	 * @param b true/false
	 */
	public void zetKlikAan(boolean b)
	{
		klikAan = b;
	}

	/**
	 * luistert het Canvas naar Mouse/Touch Events?
	 * @param b true/false
	 */
	public void zetMuisAan(boolean b)
	{
		muisAan = b;
	}

	/**
	 * stop Veelvlak v in vvRij[0]; <br>
	 * NB. de viewer start altijd met een (leeg) Veelvlak in vvRij[0]  
	 * @param v nieuw Veelvlak
	 */
	public void zetVeelvlak(Veelvlak v)
	{
		vvRij[0] = v;

		tekenOpnieuw();
	}

	/**
	 * voeg een Veelvlak toe aan vvRij 
	 * @param v niew Veelvlak
	 */
	public void voegVeelvlakToe(Veelvlak v)
	{
		vvRij[aantalVeelvlakken] = v;
		aantalVeelvlakken++;
	}

	/**
	 * voeg de pijl die wijst naar de voorkant van het Veelvlak toe
	 * @param p pijl die wijst naar de voorkant (als Veelvlak)
	 */
	public void voegVooraanzichtPijlToe(Veelvlak p)
	{
		vvRij[aantalVeelvlakken] = p;
		voorkantPijlZichtbaar = true;
		voorkantPijlIndex = aantalVeelvlakken;
		aantalVeelvlakken++;

	}

	/**
	 * verwijder de pijl die wijst naar de voorkant van het Veelvlak
	 */
	public void verwijderVooraanzichtPijl()
	{
		if (voorkantPijlZichtbaar)
		{	// de pil is het laatste Veelvlak
			vvRij[aantalVeelvlakken] = null;
			voorkantPijlZichtbaar = false;
			voorkantPijlIndex = -1;
			aantalVeelvlakken--;
			tekenOpnieuw();
		}
	}

	/**
	 * teken vvRij[0] en (indien aanwezig) de pijl die wijst naar de voorkant van vvRij[0] 
	 */
	void tekenprogramma()
	{
		mat.initialiseer();
		mat.xdraai(beginx + xhoek);
		mat.ydraai(beginy + yhoek);
		for (int i = 0; i < aantalVeelvlakken; i++)
			tekenVeelvlak(0, vvRij[i]);
	}

	/**
	 * teken Veelvlak vv (vlaksgewijs); als vv == vvRij[0], bewaar alle 
	 * (geprojecteerde) Vlakken van vv in array p 
	 * @param n hier altijd 0
	 * @param vv het te tekenen veelvlak
	 */
	void tekenVeelvlak(int n, Veelvlak vv)
	{
		if (vv == vvRij[0])
		{
			p = new Polygon[vv.aantalVlakken];
		}
		for (int i = 0; i < vv.aantalVlakken; i++)
		{
			tekenVlak(n, vv.vlakken[i]);
			if (vv == vvRij[0])
			{
				p[i] = geefVlak();
			}
		}
	}

	/**
	 * teken de Lijn l en beweeg de tekencursor terug naar (0,0,0)
	 * @param l de te tekenen Lijn 
	 */
	void tekenLijn(Lijn l)
	{
		penUit();
		stap(k * l.hpunt1.x, k * l.hpunt1.y, k * l.hpunt1.z);
		penAan(1, l.kleur);
		stap(k * l.hpunt2.x - k * l.hpunt1.x, k * l.hpunt2.y - k * l.hpunt1.y, k * l.hpunt2.z - k * l.hpunt1.z);
		penUit(1);
		stap(-k * l.hpunt2.x, -k * l.hpunt2.y, -k * l.hpunt2.z);
	}

	/**
	 * teken Vlak v (voorkant en achterkant)
	 * @param n hier altijd 0
	 * @param v het te tekenen Vlak
	 */
	void tekenVlak(int n, Vlak v)
	{
		penUit();
		stap(k * v.punten[0].x, k * v.punten[0].y, k * v.punten[0].z);

		if (v.vulkleur == "transparant")
		{
			if (n == 2)
				vulAan(v.vulkleur);
			else if (n == 1)
				vulAan(1, v.vulkleur);
		}
		else if (v.vulkleur != "zwart")
		{
			if (n == 2)
			{
				vulAan("grijs");
			}
			else if (n == 1)
			{
				vulAan(1, "grijs");
			}
			else
				vulAan(n, "grijs");
		}

		if (v.vulkleur != "zwart")
		{
			for (int i = v.aantalHoekpunten - 1; i > -1; i--)
			{
				int a = i;
				int b = (i + 1) % v.aantalHoekpunten;
				stap(k * (v.punten[a].x - v.punten[b].x), k * (v.punten[a].y - v.punten[b].y),
					k * (v.punten[a].z - v.punten[b].z));
			}
			if (n == 2)
				vulUit();
			else if (n == 1)
				vulUit(1);
			else
				vulUit(n);
		}

		if (!(v.lijnkleur == "transparant"))
			penAan(v.lijnkleur);
		vulAan(n, v.vulkleur);
		for (int i = 0; i < v.aantalHoekpunten; i++)
		{
			int a = i;
			int b = (i + 1) % v.aantalHoekpunten;
			stap(-k * (v.punten[a].x - v.punten[b].x), -k * (v.punten[a].y - v.punten[b].y),
				-k * (v.punten[a].z - v.punten[b].z));
		}
		vulUit(n);
		penUit();
		stap(-k * v.punten[0].x, -k * v.punten[0].y, -k * v.punten[0].z);
	}

	public void paint()
	{
		paint(gIm);
	}

	/**
	 * initialiseer en roep tekenOpImage aan 
	 * @param g Context2d om te tekenen, redundant
	 */
	public void paint(Context2d g)
	{
		double startschaal = Math.min((double) breedte / 400, (double) hoogte / 500);
		mat.initialiseer(0, 0, 0, startschaal);
		startpunt = new Punt3D(breedte / 2, hoogte / 2, 0);
		for (int i = 0; i < 5; i++)
		{
			l[i].maakNulpunt(breedte / 2, hoogte / 2, 0);
		}
		tekenOpImage(true);

	}

	/**
	 * teken de veelvlakken in de 3d-lichamen (er zijn er maximaal 2)
	 * @param wis wis het Canvas voor het tekenen, hier altijd true
	 */
	public void tekenOpImage(boolean wis)
	{
		if (gIm == null)
		{
			return;
		}
		
		gIm.clearRect(0, 0, breedte, hoogte);

		beginpunt = new Punt3D(startpunt);
		eindpunt = new Punt3D(beginpunt);

		if (wis)
		{
			gIm.clearRect(0, 0, breedte, hoogte);
		}
		if (border)
		{
			gIm.setStrokeStyle(CssColor.make(0, 0, 0));
			gIm.strokeRect(0, 0, breedte, hoogte);
		}
		penAan(0, 0, 0);
		vul = false;
		tekenprogramma();

		for (int i = 0; i < 5; i++)
		{
			l[i].sorteer();
		}

		for (int j = 0; j < 5; j++)
		{
			for (int i = 0; i < l[j].aantalPolygonen; i++)
			{

				if (l[j].vlakken[i].normaal.z > 0)
				{
					if (schaduw)
					{
						double grijsfactor = 0.5
							* ((-l[j].vlakken[i].normaal.x - l[j].vlakken[i].normaal.y + l[j].vlakken[i].normaal.z)
								/ Math.sqrt(3) + 1);
						if (grijsfactor < 0)
							grijsfactor = 0;
						if (grijsfactor > 1)
							grijsfactor = 1;

						String vString = l[j].vlakken[i].vulkleur.toString().substring(4,
							l[j].vlakken[i].vulkleur.toString().length() - 1);
						String[] kleurenStr = StringUtils.split(vString, ",");

						int fBlue = Integer.parseInt(kleurenStr[2]);
						int fGreen = Integer.parseInt(kleurenStr[1]);
						int fRed = Integer.parseInt(kleurenStr[0]);

						int roodwaarde = 50 + (int) (fRed * grijsfactor * 0.75);
						int groenwaarde = 50 + (int) (fGreen * grijsfactor * 0.75);
						int blauwwaarde = 50 + (int) (fBlue * grijsfactor * 0.75);
						gIm.setFillStyle(CssColor.make(roodwaarde, groenwaarde, blauwwaarde));
					}
					else
					{
						gIm.setFillStyle(l[j].vlakken[i].vulkleur);
					}
					if (!l[j].vlakken[i].isLeeg)
					{
						gIm.moveTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						gIm.beginPath();
						for (int k = 1; k < l[j].vlakken[i].pol.aantalPunten; k++)
						{
							gIm.lineTo(l[j].vlakken[i].pol.puntenX[k], l[j].vlakken[i].pol.puntenY[k]);
						}
						gIm.lineTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						gIm.closePath();
						gIm.fill();
					}
					gIm.setStrokeStyle(l[j].vlakken[i].lijnkleur);
					if (!l[j].vlakken[i].isLijn && l[j].vlakken[i].isOmlijnd)
					{
						gIm.setStrokeStyle(l[j].vlakken[i].lijnkleur);
						gIm.moveTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						gIm.beginPath();
						for (int k = 1; k < l[j].vlakken[i].pol.aantalPunten; k++)
						{
							gIm.lineTo(l[j].vlakken[i].pol.puntenX[k], l[j].vlakken[i].pol.puntenY[k]);
						}
						gIm.lineTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						gIm.closePath();
						gIm.stroke();
					}
					if (l[j].vlakken[i].isLijn)
					{
						gIm.setStrokeStyle(l[j].vlakken[i].lijnkleur);
						gIm.moveTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						gIm.beginPath();
						for (int k = 1; k < l[j].vlakken[i].pol.aantalPunten; k++)
						{
							gIm.lineTo(l[j].vlakken[i].pol.puntenX[k], l[j].vlakken[i].pol.puntenY[k]);
						}
						gIm.lineTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						gIm.closePath();
						gIm.stroke();
						penkleur = CssColor.make(0, 0, 0);
					}
				}
			}
			l[j] = new Lichaam3D();
			l[j].zetAfstand(afstand);
			l[j].maakNulpunt(breedte / 2, hoogte / 2, 0);
		}
	}

	/**
	 * repaint
	 */
	void tekenOpnieuw()
	{
		if (gIm == null)
			return;

		tekenOpImage(true);
	}

	/**
	 * stap (dx,dy,dz) in de huidige richting, zie methode
	 * naarVolgendPunt
	 * @param dx x-verandering
	 * @param dy y-verandering
	 * @param dz z-verandering
	 */
	void stap(double dx, double dy, double dz)
	{
		naarVolgendPunt(dx, -dy, -dz);
	}

	/**
	 * beweeg naar een nieuwe positie, berekend als nieuw = huidig + mat * (dx,dy,dz)T; 
 	 * als pen == true en vul == false, verbindt de oude en nieuwe positie met een lijn in penkleur, d.w.z., 
 	 * voeg twee punten toe aan Lichaam3D lnummer en maak daar een Polygon3D van (een 3d-segment dus); <br>
 	 * als vul == true, voeg de oude positie als punt toe aan Lichaam3D lnummer (al deze punten 
 	 * worden een Polygon3D bij vulUit())
	 * @param dx x-verandering
	 * @param dy y-verandering
	 * @param dz z-verandering
	 */
	void naarVolgendPunt(double dx, double dy, double dz)
	{
		eindpunt = mat.geefVolgendPunt(beginpunt, dx, dy, dz);
		if (pen && !vul)
		{
			l[lnummer].voegPuntToe(beginpunt);
			l[lnummer].voegPuntToe(eindpunt);
			l[lnummer].voegPolygonToe(penkleur, penkleur, true, false);
		}
		if (vul)
		{
			l[lnummer].voegPuntToe(beginpunt);
		}
		beginpunt.x = eindpunt.x;
		beginpunt.y = eindpunt.y;
		beginpunt.z = eindpunt.z;
	}

	/**
	 * maak van de punten in Lichaam3D 0 een Polygon
	 */
	void tekenPolygon()
	{
		l[0].voegPolygonToe(vulkleur, penkleur, pen, leeg);
	}

	/**
	 * maak van de punten in Lichaam3D n een Polygon 
	 * @param n Lichaam3D nummer
	 */
	void tekenPolygon(int n)
	{
		l[n].voegPolygonToe(vulkleur, penkleur, pen, leeg);
	}

	/**
	 * zet de pen aan 
	 */
	void penAan()
	{
		pen = true;
	}

	/**
	 * zet de pen aan in kleur kl
	 * @param kl kleurnaam
	 */
	void penAan(String kl)
	{
		pen = true;
		penkleur = maakKleur(kl);
		gIm.setStrokeStyle(penkleur);
	}

	/**
	 * zet de pen aan in RGB-kleur (r,g,b)
	 * @param r rood
	 * @param g groen
	 * @param b blauw
	 */
	void penAan(int r, int g, int b)
	{
		pen = true;
		penkleur = CssColor.make(r, g, b);
		gIm.setStrokeStyle(penkleur);
	}

	/**
	 * zet de pen aan en het Lichaam3D-nummer op lnummer
	 * @param n nieuw lnummer
	 */
	void penAan(int n)
	{
		pen = true;
		lnummer = n;
	}

	/**
	 * zet de pen aan in kleur kl en het Lichaam3D-nummer op lnummer
	 * @param n nieuw lnummer
	 * @param kl kleurnaam
	 */
	void penAan(int n, String kl)
	{
		pen = true;
		penkleur = maakKleur(kl);
		gIm.setStrokeStyle(penkleur);
		lnummer = n;
	}

	/**
	 * zet de pen aan in RGB-kleur (r,g,b) en het Lichaam3D-nummer op lnummer
	 * @param n nieuw lnummer
	 * @param r rood
	 * @param g groen
	 * @param b blauw
	 */
	void penAan(int n, int r, int g, int b)
	{
		pen = true;
		penkleur = CssColor.make(r, g, b);
		gIm.setStrokeStyle(penkleur);
		lnummer = n;
	}

	/**
	 * zet de pen uit
	 */
	void penUit()
	{
		pen = false;
	}

	/**
	 * zet de pen uit en lnummer op 0
	 * @param n dummy
	 */
	void penUit(int n)
	{
		pen = false;
		lnummer = 0;
	}

	/**
	 * zet vul aan
	 */
	void vulAan()
	{
		vul = true;
	}

	/**
	 * zet vul aan in kleur kl
	 * @param kl kleurnaam
	 */
	void vulAan(String kl)
	{
		vul = true;
		if (kl.equals("transparant"))
			leeg = true;
		vulkleur = maakKleur(kl);
	}

	/**
	 * zet vul aan in RGB-kleur (r,g,b)
	 * @param r rood
	 * @param g groen
	 * @param b blauw
	 */
	void vulAan(int r, int g, int b)
	{
		vul = true;
		vulkleur = CssColor.make(r, g, b);
	}

	/**
	 * zet vul aan en het Lichaam3D-nummer op lnummer
	 * @param n nieuw lnummer
	 */
	void vulAan(int n)
	{
		vul = true;
		lnummer = n;
	}

	/**
	 * zet vul aan in kleur kl en het Lichaam3D-nummer op lnummer
	 * @param n nieuw lnummer
	 * @param kl kleurnaam
	 */
	void vulAan(int n, String kl)
	{
		vul = true;
		lnummer = n;
		if (kl.equals("transparant"))
			leeg = true;
		vulkleur = maakKleur(kl);
	}

	/**
	 * zet vul aan in RGB-kleur (r,g,b) en het Lichaam3D-nummer op lnummer
	 * @param n nieuw lnummer
	 * @param r rood
	 * @param g groen
	 * @param b blauw
	 */
	void vulAan(int n, int r, int g, int b)
	{
		vul = true;
		lnummer = n;
		vulkleur = CssColor.make(r, g, b);
	}

	/**
	 * zet vul aan in CssColor kl
	 * @param kl nieuwe CssColor
	 */
	void vulAan(CssColor kl)
	{
		vul = true;
		vulkleur = kl;
	}

	/**
	 * maak van alle punten in Lichaam3D n een Polgon en teken dit;
	 * zet vul uit en lnummer terug naar 0
	 */
	void vulUit()
	{
		tekenPolygon();
		vul = false;
		lnummer = 0;
		leeg = false;
	}

	/**
	 * maak van alle punten in Lichaam3D n een Polgon en teken dit;
	 * zet vul uit en lnummer terug naar 0
	 * @param n Lichaam3D nummer, geen error check
	 */
	void vulUit(int n)
	{
		tekenPolygon(n);
		vul = false;
		lnummer = 0;
		leeg = false;
	}

	/**
	 * Geeft het laatst getekende Punt van Lichaam 0
	 * @return het laatst getekende Punt
	 */
	Punt geefPunt()
	{
		double pf = (afstand - beginpunt.z) / afstand;
		double begx = l[0].nulpunt.x + (beginpunt.x - l[0].nulpunt.x) / pf;
		double begy = l[0].nulpunt.y + (beginpunt.y - l[0].nulpunt.y) / pf;
		return new Punt(begx, begy);
	}

	/**
	 * Geeft het laatst getekende Punt van Lichaam n
	 * @param n index van het Lichaam
	 * @return het laatst getekende Punt
	 */
	Punt geefPunt(int n)
	{
		double pf = (afstand - beginpunt.z) / afstand;
		double begx = l[n].nulpunt.x + (beginpunt.x - l[n].nulpunt.x) / pf;
		double begy = l[n].nulpunt.y + (beginpunt.y - l[n].nulpunt.y) / pf;
		return new Punt(begx, begy);
	}

	/**
	 * geef het laatst getekende vlak van lichaam 0 indien dit zichtbaar is
	 * @return een Polygon (mogelijk zonder punten)
	 */
	Polygon geefVlak()
	{
		if (l[0].vlakken[l[0].aantalPolygonen - 1].normaal.z > 0)
			return l[0].vlakken[l[0].aantalPolygonen - 1].pol;
		else
			return new Polygon();
	}

	/**
	 * gegeven de naam van een kleur, maak die kleur
	 * @param kl kleurnaam
	 * @return corresponderende kleur
	 */
	private CssColor maakKleur(String kl)
	{
		if (kl.equals("rood"))
			return CssColor.make(255, 0, 0);
		else if (kl.equals("groen"))
			return CssColor.make(0, 255, 0);
		else if (kl.equals("blauw"))
			return CssColor.make(0, 0, 255);
		else if (kl.equals("geel"))
			return CssColor.make(255, 255, 0);
		else if (kl.equals("cyaan"))
			return CssColor.make(0, 255, 255);
		else if (kl.equals("roze"))
			return CssColor.make("pink");
		else if (kl.equals("zwart"))
			return CssColor.make(0, 0, 0);
		else if (kl.equals("grijs"))
			return CssColor.make(128, 128, 128);
		else if (kl.equals("lichtgrijs"))
			return CssColor.make(200, 200, 200);
		else if (kl.equals("magenta"))
			return CssColor.make(255, 0, 255);
		else if (kl.equals("wit"))
			return CssColor.make(255, 255, 255);
		else if (kl.equals("oranje"))
			return CssColor.make(255, 165, 0);
		else
			return CssColor.make(255, 128, 0);
	}

	/**
	 * kleur alle vlakken weer oranje 
	 */
	public void resetColors()
	{
		for (int j = vvRij[0].aantalVlakken - 1; j > -1; j--)
		{
			vvRij[0].vlakken[j].vulkleur = "oranje";
		}
		tekenOpnieuw();
	}
	
	/**
	 * actie bij MouseUp/TouchEnd, klikAan == true en vlakkenKlerenOptie == true:<br>
	 * kijk of een vlakje in array p aangeklikt is, 
	 * kleur/ontkleur het aangeklikte vlakje, verwijder het nakijkresultaat 
	 * van vlakkenkleuren in vaktek of viewer 
	 */
	public void muisKkActie()
	{
		for (int j = vvRij[0].aantalVlakken - 1; j > -1; j--)
		{
			if (p[j].contains(mb.geefDrukx(), mb.geefDruky()))
			{
				if (vlakkenKleurenOptie)
				{
					// een oranje vlak dat rood gekleurd wordt
					if (vvRij[0].vlakken[j].vulkleur.equals("oranje"))
					{
						vvRij[0].vlakken[j].vulkleur = "rood";
					}
					// een rood vlak dat weer oranje gekleurd wordt
					else if (vvRij[0].vlakken[j].vulkleur.equals("rood"))
					{
						vvRij[0].vlakken[j].vulkleur = "oranje";
					}

					if (vaktek != null)
					{
						vaktek.kijkNaPanel.setStyleName("goed", false);
						vaktek.kijkNaPanel.setStyleName("fout", false);
					}
					else
					{
						kijkNaPanel.setStyleName("goed", false);
						kijkNaPanel.setStyleName("fout", false);
					}
					tvGWT.answerChanged();

					updateViewerKleuren();
				} // vlakkenkleurenOptie

				tekenOpnieuw();
				return;
			}
		}
	}

	/**
	 * actie bij MouseMove/TouchMove: draai het 3d-object;
	 * verwijder het nakijkresultaat wanneer de positie wordt nagekeken 
	 */
	public void muisSleepActie()
	{
		if (muisAan)
		{
			xhoek -= 0.5 * mb.geefSleepdy();
			if (restrictRotation)
			{
				if (xhoek > 90 - beginx)
					xhoek = 90 - beginx;
				if (xhoek < -90 - beginx)
					xhoek = -90 - beginx;
			}
			yhoek += 0.5 * mb.geefSleepdx();
			tekenOpnieuw();

			if (!klikAan)
			{
				kijkNaPanel.setStyleName("goed", false);
				kijkNaPanel.setStyleName("fout", false);

				tvGWT.answerChanged();
			}
		}
	}

	/**
	 * geen actie bij MouseDown/TouchStart
	 */
	public void muisDrukActie()
	{}


	/**
	 * initieer actie bij MouseUp/TouchEnd indien klikAan == true en er niet gesleept wordt
	 */
	public void muisLosActie()
	{
		if ((klikAan && (mb.geefDrukx() - mb.geefX()) * (mb.geefDrukx() - mb.geefX())
			+ (mb.geefDruky() - mb.geefY()) * (mb.geefDruky() - mb.geefY()) < 10))
		{
			muisKkActie();
		}
		tekenOpnieuw();
	}

	/**
	 * inner class om klikken op de kijkNaButton af te handelen
	 */
	class PushClickHandler implements ClickHandler
	{
		public void onClick(ClickEvent e)
		{
			e.stopPropagation();

			if (e.getSource() == kijkNaButton)
			{
				tvGWT.kijkNa();
				if (tvGWT.correct)
				{
					kijkNaPanel.setStyleName("goed", true);
					kijkNaPanel.setStyleName("fout", false);
				}
				else
				{
					kijkNaPanel.setStyleName("goed", false);
					kijkNaPanel.setStyleName("fout", true);
				}
			}
		}
	}

	/**
	 * Geef het kijknapanel van de 3dviewer.
	 * @return het kijknapanel van de 3dviewer.
	 */
	public LayoutPanel getKijkNaPanel()
	{
		return this.kijkNaPanel;
	}
}