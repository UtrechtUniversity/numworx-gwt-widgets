package fi.tekenveelvlakgwt.client;


import java.util.*;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.dom.client.Style;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Label;

/**
 * klasse die een tool repesenteert waarmee de gebruiker een Veelvlak kan ontwerpen;
 * de klasse bevat een regelpaneel met knoppen om het ontwerpen te realizeren; <br>
 * gebruik makend van een basisfiguur (in dit geval een transparante kubus) kan de 
 * gebruiker vlakken toevoegen (toggleknop "maak vlak" ingedrukt, dwz heeft opschrift
 * MAAK VLAK") door achtereenvolgens minimaal drie hoekpunten van de basisfiguur of
 * van reeds toegevoegde vlakken of lijnen aan te klikken en daarna het eerste punt
 * nogmaals aan te klikken; als er pas twee hoekpunten geselecteerd zijn, dan 
 * kunnen die gedeselecteerd worden door ze nogmaals aan te klikken; de volgorde
 * van aanklikken is relevant: de normaal van het vlak (buitenkant) volgt
 * de kurketrekkerregel <br>
 * de gebruiker kan ook lijnen toevoegen (toggleknop "maak lijn" ingedrukt, dwz heeft opschrift
 * MAAK LIJN") door achtereenvolgens twee hoekpunten van de basisfiguur of
 * van reeds toegevoegde vlakken of lijnen aan te klikken; na toevoegen van een lijn
 * worden de snijpunten (als deze er zijn) met bestaande lijnen berekend; <br>
 * de gebruiker kan dus door tactisch lijnen toevoegen nieuwe punten creeren om er vlakken 
 * door te tekenen.<br>
 * merk op dat lijnen toegevoegd worden aan het veelvlak, gebruik dus als de constructie van het
 * veelvlak klaar is, de knop "wis lijnen" <br>  
 * @author Peter Boon
 */


public class TekenVeelvlak extends TekenApplet3D implements ClickHandler
{	
	/**
	 * breedte en hoogte (inclusief regelpaneel)
	 */
	int breedte, hoogte; 
	
	/**
	 * zoomslider
	 */
	Slider zijdeSl;
	
	/**
	 * Matrix3D wordt gebruikt voor draaien van basisfiguur en veelvlak,
	 * omdat tb.mat gelijk is een deze Matrix3D, zie klasse TekenBlad3D 
	 */
	Matrix3D matres;
	/**
	 * gebruik deze Matrix3D om verdikte punten te tekenen:
	 * zorg ervoor dat tb.mat (zie klasse TekenBlad3D) tijdelijk gelijk is een deze Matrix3D
	 */
	Matrix3D mateenh;
	/**
	 * Matrix3D voor implementatie initiele draaing<br>
	 * zie methode beginDraai 
	 */
	Matrix3D matrot;

	/**
	 * een vergroot/verklein factor afhankelijk van de
	 * breedte van het Canvas
	 */
	double k;
	/**
	 * variabelen gebruikt bij het berekenen van k<br>
	 * zie methode zetZoomFactor
	 */
	double kMinFac = 60e-2d;
	double kMaxFac = 140e-2d;
	double k50 = 180;
	double kMin, kMax;
	
	/**
	 * zoomfactor
	 */
	double zoomFac = 5e-1d;
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
	 * de initiele draaiing van het 3d-veelvlak om de x-as resp. de y-as
	 */
	double beginx = 20, beginy = -30;

	/**
	 * wordt er geluisterd naar MouseDown/TouchStart? 
	 */
	boolean muisDrukAan = true;
	
	/**
	 * de basisfiguur, een transparante kubus
	 */
	Veelvlak v;
	/**
	 * het Veelvlak onder constructie
	 */
	Veelvlak tv;
	/**
	 * pijl (een Veelvlak) die naar de voorkant van de figuur wijst
	 */
	Veelvlak voorkantPijl = null;
	
	/**
	 * de punten die aangeklikt kunnen worden om lijnen of vlakken te
	 * tekenen, zie methode maakTrefpunten
	 */
	Punt[] trefpunten;
	/**
	 * een array met booleans dat aangeeft welke van de trefpunten 
	 * aangeklikt zijn/werden (waarde true)
	 */
	boolean[] trefpuntRaak;
	/**
	 * niet gebruikt
	 */
	int aantalGetekendeHoekpunten;
	/**
	 * het aantal nieuwe hoekpunten (zie hoekpuntenNieuw)
	 *  
	 */
	int aantalHpNieuw;
	/**
	 * punten die geen hoekpunt zijn van het Veelvlak dat ontworpen wordt: 
	 * de hoekpunten van de baisfiguur aangevuld met de snijpunten van 
	 * getekende lijnen 
	 */
	Hoekpunt[] hoekpuntenNieuw;
	/**
	 * de hoekpuntenNieuw die aangeklikt kunnen worden (alle dus)
	 */
	Punt[] trefpuntenNieuw;
	/**
	 * is dit de eerste keer dat de figuur getekend wordt?<br>
	 * zie methode tekenProgramma
	 */
	boolean begin;
	/**
	 * true: basisfiguur zichtbaar
	 */
	boolean basisZichtbaar;
	/**
	 * true: modus lijnen maken
	 */
	boolean maakLijn;
	/**
	 * true: modus vlakken maken
	 */
	boolean maakVlak; 
	/**
	 * knop "verberg/toon basisfiguur"
	 */
	PushButton basisKnop;
	/**
	 * knop "maak ongedaan"
	 */
	PushButton terugKnop;
	/**
	 * knop "wis lijnen"
	 */
	PushButton wisKnop;
	/**
	 * knop "wis vlakken"
	 */
	PushButton wisVKnop;
	/**
	 * toggle "maak lijnen" aan/uit
	 */
	ToggleButton lijnKnop; 
	/**
	 * toggle "maak vlakken" aan/uit
	 */
	ToggleButton vlakKnop;
	/**
	 * label voor zoomslider
	 */
	Label zoomLabel;
	
	/**
	 * de indices in trefpunten(Nieuw) van het eerste en
	 * tweede aangeklikte punt van een lijn die getekend wordt<br>
	 * zie methode muisDrukActie
	 */
	int puntnr1, puntnr2;
	/**
	 * het aantal aangeklikte punten in trefpunten(Nieuw) van een vlak dat getekend wordt<br>
	 * zie methode muisDrukActie
	 */
	int aantalPuntenRood;
	/**
	 * de indices van de aangeklikte punten in trefpunten(Nieuw) van een vlak dat getekend wordt<br>
	 * zie methode muisDrukActie
	 */
	int[] puntnr;	
	
	/**
	 * verticale offset regelpaneel (pixels)
	 */
	int bStarH = 20;

	/**
	 * constructor: roep de constructor van de superklasse aan;<br>
	 * NB dit initialiseert i.h.b. het LayoutPanel rg 
	 * @param b breedte
	 * @param h hoogte
	 */
	public TekenVeelvlak(int b, int h)
	{
		super(b,h);
		breedte = b;
		hoogte = h;
	}
	
	/**
	 * initialiseer de knoppen op het regelpaneel rg; initialiseer de attributen;
	 * initialiseet de basisfiguur (een kubus) een een leeg Veelvlak onder constructie 
	 */
	public void initialiseer()
	{	
		
		int currentY = bStarH;
	    
		zoomLabel = new Label(TekenVeelvlakGWT.rb.zijdeLabel());
		rg.add(zoomLabel);
		rg.setWidgetLeftWidth(zoomLabel, 15, Style.Unit.PX, 100, Style.Unit.PX);
		rg.setWidgetTopHeight(zoomLabel, currentY, Style.Unit.PX, 20, Style.Unit.PX);

		currentY += 30;
		
		zijdeSl = new Slider(100,50,this);
		zijdeSl.achtergrondKleur = CssColor.make(208,228,255);
		rg.add(zijdeSl.sliderCanvas);
		rg.setWidgetLeftWidth(zijdeSl.sliderCanvas, 10, Style.Unit.PX, 110, Style.Unit.PX);
		rg.setWidgetTopHeight(zijdeSl.sliderCanvas, currentY, Style.Unit.PX, 20, Style.Unit.PX);
				
		zijdeSl.paint();
		
		currentY += 30;
		
		lijnKnop = new ToggleButton(TekenVeelvlakGWT.rb.lijnKnopLabel(),TekenVeelvlakGWT.rb.lijnKnopCapLabel());
		rg.add(lijnKnop);
		rg.setWidgetLeftWidth(lijnKnop, 8, Style.Unit.PX, 114, Style.Unit.PX);
		rg.setWidgetTopHeight(lijnKnop, currentY, Style.Unit.PX, 25, Style.Unit.PX);
		
		lijnKnop.addClickHandler(this);
		lijnKnop.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());
		
		currentY += 35;
				
		vlakKnop = new ToggleButton(TekenVeelvlakGWT.rb.vlakKnopLabel(), TekenVeelvlakGWT.rb.vlakKnopCapLabel());
		rg.add(vlakKnop);
		rg.setWidgetLeftWidth(vlakKnop, 8, Style.Unit.PX, 114, Style.Unit.PX);
		rg.setWidgetTopHeight(vlakKnop, currentY, Style.Unit.PX, 25, Style.Unit.PX);
		
		vlakKnop.addClickHandler(this);
		vlakKnop.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());
		
		currentY += 35;
		
		basisKnop = new PushButton(TekenVeelvlakGWT.rb.verbergBasisKnopLabel());
		rg.add(basisKnop);
		rg.setWidgetLeftWidth(basisKnop, 8, Style.Unit.PX, 114, Style.Unit.PX);
		rg.setWidgetTopHeight(basisKnop, currentY, Style.Unit.PX, 25, Style.Unit.PX);
		
		basisKnop.addClickHandler(this);
		basisKnop.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());
		
		currentY += 35;
		
		terugKnop = new PushButton(TekenVeelvlakGWT.rb.terugKnopLabel());
		rg.add(terugKnop);
		rg.setWidgetLeftWidth(terugKnop, 8, Style.Unit.PX, 114, Style.Unit.PX);
		rg.setWidgetTopHeight(terugKnop, currentY, Style.Unit.PX, 25, Style.Unit.PX);
		
		terugKnop.addClickHandler(this);
		terugKnop.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());
		
		currentY += 35;

		wisKnop = new PushButton(TekenVeelvlakGWT.rb.wisLijnKnopLabel());
		rg.add(wisKnop);
		rg.setWidgetLeftWidth(wisKnop, 8, Style.Unit.PX, 114, Style.Unit.PX);
		rg.setWidgetTopHeight(wisKnop, currentY, Style.Unit.PX, 25, Style.Unit.PX);

		wisKnop.addClickHandler(this);
		wisKnop.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());
		
		currentY += 35;
		
		wisVKnop = new PushButton(TekenVeelvlakGWT.rb.wisVlakKnopLabel());
		rg.add(wisVKnop);
		rg.setWidgetLeftWidth(wisVKnop, 8, Style.Unit.PX, 114, Style.Unit.PX);
		rg.setWidgetTopHeight(wisVKnop, currentY, Style.Unit.PX, 25, Style.Unit.PX);

		wisVKnop.addClickHandler(this);
		wisVKnop.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());
		
		currentY += 35;
		
		matrot = new Matrix3D();
		matres = new Matrix3D();
		mateenh = new Matrix3D();
		tb.mat = matres;
				
		k = 180; 
		begin=true;
		basisZichtbaar=true;
		maakLijn=true;
		maakVlak=false;

		trefpunten = new Punt[500];
		trefpuntRaak = new boolean[500];
		wisTrefpunten();

		aantalHpNieuw = 0;
		hoekpuntenNieuw = new Hoekpunt[500];
		trefpuntenNieuw = new Punt[500];
		aantalPuntenRood = 0;
		puntnr = new int[20];
		
		// basisfiguur kubus 
		v = (new Kubus(1));
		
		// geen extra punten op de ribben
		int n = 0;
		int aantalHp = 8;
		int aantalRib = 12;
		aantalGetekendeHoekpunten = aantalHp + n*aantalRib;
		Hoekpunt[] hp = new Hoekpunt[v.aantalHoekpunten+n*aantalRib];
		for(int i=0 ; i<v.aantalHoekpunten ; i++)
		{	hp[i]=v.hoekpunten[i];
		}
		v.hoekpunten = hp;
		v.aantalHoekpunten+=n*aantalRib;
		
		int[] rib = {0,1,1,2,2,3,3,0,0,4,1,5,2,6,3,7,4,5,5,6,6,7,7,4};
		for(int i=0 ; i<aantalRib ; i++)
		{	for(int j=0 ; j<n ; j++)
			{	v.hoekpunten[aantalHp+n*i+j] = new Hoekpunt(((n-j)*v.hoekpunten[rib[2*i]].x + (j+1)*v.hoekpunten[rib[2*i+1]].x)/(n+1),  
														((n-j)*v.hoekpunten[rib[2*i]].y + (j+1)*v.hoekpunten[rib[2*i+1]].y)/(n+1),
														((n-j)*v.hoekpunten[rib[2*i]].z + (j+1)*v.hoekpunten[rib[2*i+1]].z)/(n+1));
			}
		}
										
		// alle vlakken transparant
		for (int i = 0; i < v.aantalVlakken; i++)
		{	v.vlakken[i].vulkleur = "transparant";
		}
		
		// het Veelvlak onder constructie
		tv = new Veelvlak();
		
		// de nieuwe hoekpunten zijn die van de basisfiguur		
		for(int i=0 ; i < v.aantalHoekpunten ; i++)
		{	hoekpuntenNieuw[aantalHpNieuw] = v.hoekpunten[i];
			aantalHpNieuw++;
		}
		
		// dit berekent k 
		setBounds(0,0,breedte,hoogte);
	}

	/**
	 * gebruik dit om k te berekenen
	 * @param x niet gebruikt
	 * @param y niet gebruikt
	 * @param b breedte
	 * @param h niet gebruikt
	 */
	public void setBounds(int x, int y, int b, int h)
	{	
		k50 = 180.0/500*Math.min(b-150, h);
		kMin = kMinFac * k50;
		kMax = kMaxFac * k50;
		k = zoomFac * (kMax - kMin) + kMin;
	}
	
	/**'
	 * zet een nieuwe zoomfacor, herbereken k
	 * @param zFac nieuwe zoomfactor
	 */
	public void zetZoomFac(double zFac)
	{
		zoomFac = zFac;
		k50 = 180.0/500*Math.min(breedte-150, hoogte);
		kMin = kMinFac * k50;
		kMax = kMaxFac * k50;
		k = zoomFac * (kMax - kMin) + kMin; 
	}
	
	/**
	 * creeer het Veelvlak onder constructie m.b.v. de status data uit een Map:<br>
	 * ook positie en zoomFactor maken deel uit van de status; merk op dat de
	 * (al gecreerde) basisfiguur ongewijzigd wordt gelaten.  
	 * @param map Map met status data
	 */
	public void setState(Map map)
	{	
		if(map == null || map.isEmpty())
			return;
		ObjectMap h = JSONUtilities.wrapMap(map);

		double[] hoekpunten = null;
		List<Double> hoekpuntenAL = new ArrayList<Double>();
		int[] vlakken = null;
		List<Integer> vlakkenAL = new ArrayList<Integer>();
		int[] lijnen = null;
		List<Integer> lijnenAL = new ArrayList<Integer>();
		
		boolean basisZichtbaar = true;
		
		double zoomFac = 5e-1d;
		double draaiX = 20;
		double draaiY = -30;
		
		if (h.containsKey("hoekpunten"))
		{	
			// backwards compatibility
			hoekpuntenAL = h.getDoubleList("hoekpunten");
			if (hoekpuntenAL == null)
			{	hoekpunten = h.getDoubleArray("hoekpunen"); 
			}
			else 
			{	hoekpunten = new double[hoekpuntenAL.size()];
				for (int hp = 0; hp < hoekpuntenAL.size(); hp++)
					hoekpunten[hp] = hoekpuntenAL.get(hp).doubleValue();
			}
		}
		if (h.containsKey("vlakken"))
		{	
			// backwards compatibility
			vlakkenAL = h.getIntegerList("vlakken");
			if (vlakkenAL == null)
			{	vlakken = h.getIntArray("vlakken"); 
			}
			else 
			{	vlakken = new int[vlakkenAL.size()];
				for (int v = 0; v < vlakkenAL.size(); v++)
					vlakken[v] = vlakkenAL.get(v).intValue();
			}
		}
		if (h.containsKey("lijnen"))
		{	
			// backwards compatibility
			lijnenAL = h.getIntegerList("lijnen");
			if (lijnenAL == null)
			{	lijnen = h.getIntArray("lijnen"); 
			}
			else 
			{	lijnen = new int[lijnenAL.size()];
				for (int l = 0; l < lijnenAL.size(); l++)
					lijnen[l] = lijnenAL.get(l).intValue();
			}
		}
		
		if (h.containsKey("basisZichtbaar"))
			basisZichtbaar = h.getBoolean("basisZichtbaar");
		if (h.containsKey("zoomFac"))
			zoomFac = h.getDouble("zoomFac");
		if (h.containsKey("draaiX"))
			draaiX = h.getDouble("draaiX");
		if (h.containsKey("draaiY"))
			draaiY = h.getDouble("draaiY");

		aantalPuntenRood = 0;
		wisTrefpunten();
		aantalHpNieuw = 0;
		
		this.basisZichtbaar = basisZichtbaar;
		
		if (!basisZichtbaar) 
			basisKnop.setText("Toon basis");
		
		this.zoomFac = zoomFac;
		
		int stand = (int) Math.round(zoomFac * zijdeSl.geefLengte());
		zijdeSl.zetStand(stand);
		zetZoomFac(zoomFac);

		zetBeginHoeken(draaiX, draaiY);

		tv = new Veelvlak(hoekpunten, vlakken, lijnen);

		for (int i = 0; i < v.aantalHoekpunten; i++)
		{	hoekpuntenNieuw[aantalHpNieuw] = v.hoekpunten[i];
			aantalHpNieuw++;
		}
	
		maakAlleSnijpunten();
		
		begin = true;
		tekenOpnieuw();
		
	}
	
	/**
	 * sla de status data van het Veelvlak onder constructie op in een HashMap<br>
	 * ook positie en zoomFactor maken deel uit van de status;
	 * @return HashMap met status data van het Veelvlak onder constructie
	 */
	public HashMap getState()
	{	
		ArrayList<Double> hoekpuntenAL = new ArrayList<Double>();
		ArrayList<Integer> vlakkenAL = new ArrayList<Integer>();
		ArrayList<Integer> lijnenAL = new ArrayList<Integer>();
				
		boolean basisZichtbaar = true;
		
		double zoomFac = 5e-1d;
		
		double draaiX = 20;
		double draaiY = -30;

		for (int h = 0; h < tv.hpRij.length; h++)
			hoekpuntenAL.add(new Double(tv.hpRij[h]));

		for (int v = 0; v < tv.vlRij.length; v++)
			vlakkenAL.add(new Integer(tv.vlRij[v]));

		for (int k = 0; k < tv.lnRij.length; k++)
			lijnenAL.add(new Integer(tv.lnRij[k]));

		basisZichtbaar = this.basisZichtbaar;
		
		zoomFac = this.zoomFac;
		draaiX = geefDraaiX();
		draaiY = geefDraaiY();
		HashMap h = new HashMap();
		
		h.put("hoekpunten", hoekpuntenAL);
		h.put("vlakken", vlakkenAL);
		h.put("lijnen", lijnenAL);
		
		h.put("basisZichtbaar", new Boolean(basisZichtbaar));
		
		h.put("zoomFac", new Double(zoomFac));
		
		h.put("draaiX", new Double(draaiX));
		h.put("draaiY", new Double(draaiY));

		return h;
	}

	/**
	 * zet de positie van het oog op de positieve z-as
	 * in het punt (0,0,afstand) 
	 */
	public void zetAfstand(double afst)
	{	
		tb.zetAfstand(afst);
	}
	/**
	 * vlakjes van het Veelvlak met schaduw-effect tekenen?
	 */
	public void zetSchaduw(boolean s)
	{	tb.zetSchaduw(s);
	}
	/**
	 * zet de initiele draaiing om x- resp. y-as
	 * @param hx draaiing om de x-as
	 * @param hy draaiing om de y-as
	 */
	public void zetBeginHoeken(double hx, double hy)
	{	beginx = hx;
		beginy = hy;
		xhoek = 0;
		yhoek = 0;
	}

	/**
	 * geef de draaiing van de figuur om de x-as
	 * @return beginx+xhoek
	 */
	public double geefDraaiX()
	{	
		return beginx+xhoek;
	}
	/**
	 * geef de draaiing van de figuur om de y-as
	 * @return beginy+yhoek
	 */
	public double geefDraaiY()
	{	
		return beginy+yhoek;
	}

	/**
	 * toon een pijl (Veelvlak) dat naar de voorkant van de figuur wijst
	 * @param vkPijl de voorkantpijl
	 */
	public void toonVoorkantPijl(Veelvlak vkPijl)
	{
		voorkantPijl = vkPijl;
		tekenOpnieuw();
	}

	/**
	 * teken basisfiguur (als gewenst) en het Veelvlak
	 * onder constructie
	 */
	public void tekenprogramma()
	{	
			if (begin)
				begindraai(beginx,beginy);
			if (basisZichtbaar)
				tekenVeelvlak(2,v);
			tekenVeelvlak(1,tv);
			maakTrefpunten(tv);
			
			if (voorkantPijl != null)
			{	tekenVeelvlak(1,voorkantPijl);
			}
			
//		}
	}
	
	/**
	 * zet beginx resp. beginy op xdr resp. ydr en
	 * draai basisfiguur en veelvlak   
	 * @param xdr draaing om de x-as
	 * @param ydr draaing om de y-as
	 */
	void begindraai(double xdr,double ydr)
	{	
		if (begin)
		{	beginx = xdr;
			beginy = ydr;
						
			tb.mat.initialiseer();
			matrot.initialiseer();
			matrot.ydraaiAbs(ydr);
			matrot.xdraaiAbs(xdr);
			tb.mat.mult(matrot);
			begin=false;
		}
	}
	
	/**
	 * maak de punten die aangeklikt kunnen worden: dit zijn de hoekpunten
	 * van vlakken van Veelvlak v en de eindpunten van lijnen van Veelvlak v,
	 * (stop die in trefpunten[]), gevolgd door de nieuwe hoekpunten (stop die
	 * in trefPuntenNieuw[]); als een trefpunt aangeklikt is, verdik het dan in groen  
	 * @param v Veelvlak v
	 */
	void maakTrefpunten(Veelvlak v)
	{	
		
		for(int i=0 ; i<v.aantalHoekpunten ; i++)
		{	penUit();
			stap(k*v.hoekpunten[i].x, k*v.hoekpunten[i].y, k*v.hoekpunten[i].z);
			trefpunten[i] = geefPunt(1);
			if (trefpuntRaak[i])
			{	tb.mat = mateenh;
				stap(5,0);
				vulAan(1,"groen");
				stap(-5,-5);stap(-5,5);stap(5,5);stap(5,-5);
				vulUit(1);
				stap(-5,0);
				tb.mat = matres;
			}
			stap(-k*v.hoekpunten[i].x, -k*v.hoekpunten[i].y, -k*v.hoekpunten[i].z);
			penAan();
		}
		for(int i=0 ; i<aantalHpNieuw ; i++)
		{	penUit();
			stap(k*hoekpuntenNieuw[i].x, k*hoekpuntenNieuw[i].y, k*hoekpuntenNieuw[i].z);
			trefpuntenNieuw[i] = geefPunt(1);
			if(basisZichtbaar)
			{	tb.mat = mateenh;
				stap(2,0);
				vulAan(1,"zwart");
				stap(-2,-2);
				stap(-2,2);
				stap(2,2);
				stap(2,-2);
				vulUit(1);
				stap(-2,0);
				tb.mat = matres;
			}
			stap(-k*hoekpuntenNieuw[i].x, -k*hoekpuntenNieuw[i].y, -k*hoekpuntenNieuw[i].z);
			penAan();
		}
	}
	
	/**
	 * zet alle waarden in het array trefpuntRaak op false
	 */
	public void wisTrefpunten()
	{	for(int i=0 ; i<500 ; i++)
		{	trefpuntRaak[i]=false;
		}
	}
	
	/**
	 * teken Veelvlak vv (vlakken en lijnen) 
	 * @param n zie methode tekenVlak
	 * @param vv te tekeken Veelvlak
	 */
	void tekenVeelvlak(int n,Veelvlak vv)
	{	
		for (int i = 0; i < vv.aantalVlakken; i++)
		{	tekenVlak(n, vv.vlakken[i]);
		}
		for (int i = 0; i < vv.aantalLijnen; i++)
		{	tekenLijn(vv.lijnen[i]);
		}
	}

	/**
	 * teken Lijn l
	 * @param l te tekenen Lijn
	 */
	void tekenLijn(Lijn l)
	{	penUit();
		stap(k*l.hpunt1.x, k*l.hpunt1.y, k*l.hpunt1.z);
		penAan(1,l.kleur);
		stap(k*l.hpunt2.x - k*l.hpunt1.x, k*l.hpunt2.y - k*l.hpunt1.y, k*l.hpunt2.z - k*l.hpunt1.z);
		penUit(1);
		stap(-k*l.hpunt2.x, -k*l.hpunt2.y, -k*l.hpunt2.z);
	}
	
	/**
	 * teken Vlak v 
	 * @param n hier n == 1 (gebruik Lichaam3D nummer 1) of n == 2 (gebruik Lichaam3D nummer 1)
	 * @param v het te tekenen Vlak
	 */
	void tekenVlak(int n,Vlak v)
	{	penUit();
		stap(k*v.punten[0].x, k*v.punten[0].y, k*v.punten[0].z);
		
		if (!(v.lijnkleur=="transparant"))
			penAan("lichtgrijs");
		
		if (v.vulkleur=="transparant")
		{	if (n==2)
				vulAan(v.vulkleur);
			else if (n==1)
				vulAan(1,v.vulkleur);
		}
		else if (v.vulkleur != "zwart") 
		{	if (n==2)
			{	vulAan("grijs");
			}
			else if (n==1)
			{	vulAan(1,"grijs");
			}
		}
		
		if (v.vulkleur != "zwart")
		{
			for(int i=v.aantalHoekpunten-1 ; i>-1 ; i--)
			{	int a=i ; int b=(i+1)%v.aantalHoekpunten;
				stap(k*(v.punten[a].x-v.punten[b].x), k*(v.punten[a].y-v.punten[b].y), k*(v.punten[a].z-v.punten[b].z));
			}
			if (n==2)
				vulUit();
			else if (n==1)
				vulUit(1);
		}
		
		if (!(v.lijnkleur=="transparant"))
			penAan(v.lijnkleur);
		vulAan(n,v.vulkleur);
		for(int i=0 ; i<v.aantalHoekpunten ; i++)
		{	int a=i ; int b=(i+1)%v.aantalHoekpunten;
			stap(-k*(v.punten[a].x-v.punten[b].x), -k*(v.punten[a].y-v.punten[b].y), -k*(v.punten[a].z-v.punten[b].z));
		}
		vulUit(n);
		penUit();
		stap(-k*v.punten[0].x, -k*v.punten[0].y, -k*v.punten[0].z);
		
	}
	
	/**
	 * vindt het snijpunt van Lijn l1 en Lijn l2 en als het snijpunt bestaat
	 * voeg het toe aan hoekpuntenNieuw  
	 * @param l1 eerste Lijn
	 * @param l2 tweede Lijn
	 */
	void maakSnijpunt(Lijn l1, Lijn l2)
	{	double ax = l1.hpunt1.x;double ay = l1.hpunt1.y;double az = l1.hpunt1.z;
		double bx = l1.hpunt2.x;double by = l1.hpunt2.y;double bz = l1.hpunt2.z;
		double cx = l2.hpunt1.x;double cy = l2.hpunt1.y;double cz = l2.hpunt1.z;
		double dx = l2.hpunt2.x;double dy = l2.hpunt2.y;double dz = l2.hpunt2.z;
		
		double a1 = bx-ax;double a2 = by-ay;double a3 = bz-az;
		double b1 = cx-dx;double b2 = cy-dy;double b3 = cz-dz;
		double c1 = cx-ax;double c2 = cy-ay;double c3 = cz-az;
		
		double d = a1*b2 - a2*b1;
		double k=0; 
		double m;
		double afwijking ;
		if(Math.abs(d)>0.0000001)
		{	k = (c1*b2-c2*b1)/d;
			m = (a1*c2-a2*c1)/d;
			afwijking = c3-(a3*k+b3*m);
		}
		else
		{	d = a2*b3 - a3*b2;
			if(Math.abs(d)>0.0000001)
			{	k = (c2*b3-c3*b2)/d;
				m = (a2*c3-a3*c2)/d;
				afwijking = c1-(a1*k+b1*m);
			}
			else
			{	d = a1*b3 - a3*b1;
				if(Math.abs(d)>0.0000001)
				{	k = (c1*b3-c3*b1)/d;
					m = (a1*c3-a3*c1)/d;
					afwijking = c2-(a2*k+b2*m);
				}
				else return;
				
			}
		}
		
		if(afwijking<0.00001 && afwijking>-0.00001 && k<1 && k>0 && m<1 && m>0)
		{	hoekpuntenNieuw[aantalHpNieuw] = new Hoekpunt(ax + k*(bx-ax) , ay + k*(by-ay) , az + k*(bz-az));
			aantalHpNieuw++;
		}
		
	}
	/**
	 * vindt de snijpunten (if any) van alle Lijnen in het Veelvlak onder
	 * constructie en voeg ze toe aan hoekpuntenNieuw
	 */
	void maakAlleSnijpunten()
	{	for(int i=0 ; i<tv.aantalLijnen ; i++)
		{	for(int j=0 ; j<i ; j++)
			{	maakSnijpunt(tv.lijnen[j],tv.lijnen[i]);
			}
		}
	}

	/**
	 * vindt de snijpunten (if any) van de laatst gecreeerde Lijn in het Veelvlak onder
	 * constructie met alle andere Lijnen in het Veelvlak en voeg ze toe aan hoekpuntenNieuw
	 */
	void zoekSnijpunten()
	{	for(int i=0 ; i<tv.aantalLijnen-1 ; i++)
		{	maakSnijpunt(tv.lijnen[tv.aantalLijnen-1],tv.lijnen[i]);
		}
		
	}
	
	/**
	 * er zijn drie punten geselecteerd, met indices in in het hoekpunten array
	 * van het Veelvlak gegeven door het array puntnr; chack of Hoekpunt hpt
	 * in het vlak door deze drie geselecteerde punten ligt 
	 * @param hpt te chacken Hoekpunt
	 * @return true/false
	 */
	boolean checkVlak(Hoekpunt hpt)
	{	double ux = tv.hoekpunten[puntnr[1]].x - tv.hoekpunten[puntnr[0]].x;
		double uy = tv.hoekpunten[puntnr[1]].y - tv.hoekpunten[puntnr[0]].y;
		double uz = tv.hoekpunten[puntnr[1]].z - tv.hoekpunten[puntnr[0]].z;
		double vx = tv.hoekpunten[puntnr[2]].x - tv.hoekpunten[puntnr[1]].x;
		double vy = tv.hoekpunten[puntnr[2]].y - tv.hoekpunten[puntnr[1]].y;
		double vz = tv.hoekpunten[puntnr[2]].z - tv.hoekpunten[puntnr[1]].z;
		double nx = uy*vz - uz*vy;	
		double ny = uz*vx - ux*vz;
		double nz = ux*vy - uy*vx;
		double d = tv.hoekpunten[puntnr[0]].x*nx + tv.hoekpunten[puntnr[0]].y*ny + tv.hoekpunten[puntnr[0]].z*nz;
		double dn = hpt.x*nx + hpt.y*ny + hpt.z*nz;
		if (d-dn < 0.0001 && d-dn > -0.0001)
			return true;
		else 
			return false; 
	}
	
	/**
	 * de stand van de zoomslider is veranderd, update k
	 */
	public void sliderAction()
	{
		int stand = zijdeSl.geefStand();
		int lengte = zijdeSl.geefLengte();
		double kMin = kMinFac * k50;
		double kMax = kMaxFac * k50;
		zoomFac = (double) stand / (double) (lengte);
		k = zoomFac * (kMax - kMin) + kMin;
		
		tekenOpnieuw();
	}
	
	/**
	 * afhandelen van Click Events op de knoppen 
	 */
	public void onClick(ClickEvent e)
	{		
	
			if (e.getSource() == basisKnop)
			{	
				if (basisKnop.getText().equals(TekenVeelvlakGWT.rb.verbergBasisKnopLabel()))
				{	
					basisZichtbaar = false;
					basisKnop.setText(TekenVeelvlakGWT.rb.toonBasisKnopLabel());
					
				}
				else
				{	basisZichtbaar = true;
					basisKnop.setText(TekenVeelvlakGWT.rb.verbergBasisKnopLabel());
					
				}
			}
			else if (e.getSource() == terugKnop)
			{	aantalPuntenRood = 0;
				wisTrefpunten();
				if (maakLijn)
				{	if (tv.aantalLijnen > 0)
						tv.wisVorigeLijn();
				}
				if (maakVlak)
				{	if (tv.aantalVlakken > 0)
						tv.wisVorigVlak();
				}
			}
			else if (e.getSource() == lijnKnop)
			{	
				if (lijnKnop.isDown())
				{
					maakLijn = true;
					maakVlak = false;
					aantalPuntenRood=0;
					wisTrefpunten();
				
					vlakKnop.setDown(false);
				}
				else
				{
					maakLijn = false;
				}
			}
			else if (e.getSource() == vlakKnop)
			{	
				if (vlakKnop.isDown())
				{
					maakLijn = false;
					maakVlak = true;
					aantalPuntenRood = 0;
					wisTrefpunten();
				
					lijnKnop.setDown(false);
				}
				else
				{
					maakVlak = false;
				}
			}
			else if (e.getSource() == wisKnop)
			{	aantalPuntenRood = 0;
				wisTrefpunten();
				tv.wisLijnen();
				aantalHpNieuw = 0;
				for (int i = 0; i < v.aantalHoekpunten; i++)
				{	hoekpuntenNieuw[aantalHpNieuw] = v.hoekpunten[i];
					aantalHpNieuw++;
				}
			}
			else if(e.getSource() == wisVKnop)
			{	aantalPuntenRood = 0;
				wisTrefpunten();
				tv.wisVlakken();
				
			}
			tekenOpnieuw();
		
	}

	
	/**
	 * neem het Veelvlak met code figNr als basisfiguur en voeg nog extra punten op de ribbe toe<br>
	 * niet gebruikt 
	 * @param figNr figuur code
	 * @param aantalRibPunten aantal extra punten per ribbe 
	 */
	public void zetBasis(int figNr, int aantalRibPunten)
	{	if (figNr==0)
		{	v = new Kubus(1);
			int n = aantalRibPunten;
			int aantalHp = 8;
			int aantalRib = 12;
			aantalGetekendeHoekpunten = aantalHp + n*aantalRib;
			Hoekpunt[] hp = new Hoekpunt[v.aantalHoekpunten+n*aantalRib];
			for(int i=0 ; i<v.aantalHoekpunten ; i++)
			{	hp[i]=v.hoekpunten[i];
			}
			v.hoekpunten = hp;
			v.aantalHoekpunten+=n*aantalRib;
			
			int[] rib = {0,1,1,2,2,3,3,0,0,4,1,5,2,6,3,7,4,5,5,6,6,7,7,4};
			for(int i=0 ; i<aantalRib ; i++)
			{	for(int j=0 ; j<n ; j++)
				{	v.hoekpunten[aantalHp+n*i+j] = new Hoekpunt(((n-j)*v.hoekpunten[rib[2*i]].x + (j+1)*v.hoekpunten[rib[2*i+1]].x)/(n+1),  
															((n-j)*v.hoekpunten[rib[2*i]].y + (j+1)*v.hoekpunten[rib[2*i+1]].y)/(n+1),
															((n-j)*v.hoekpunten[rib[2*i]].z + (j+1)*v.hoekpunten[rib[2*i+1]].z)/(n+1));
				}
			}
		}
		else if(figNr==1)
		{
			v = (new Kubus(Math.sqrt(3))).dualiseer();
			int n = aantalRibPunten;
			int aantalHp = 6;
			int aantalRib = 12;
			
			Hoekpunt[] hp = new Hoekpunt[v.aantalHoekpunten+n*aantalRib];
			for(int i=0 ; i<v.aantalHoekpunten ; i++)
			{	hp[i]=v.hoekpunten[i];
			}
			v.hoekpunten = hp;
			v.aantalHoekpunten+=n*aantalRib;
			
			int[] rib = {0,1,0,2,0,3,0,4,1,2,2,3,3,4,4,1,5,1,5,2,5,3,5,4};
			for(int i=0 ; i<aantalRib ; i++)
			{	for(int j=0 ; j<n ; j++)
				{	v.hoekpunten[aantalHp+n*i+j] = new Hoekpunt(((n-j)*v.hoekpunten[rib[2*i]].x + (j+1)*v.hoekpunten[rib[2*i+1]].x)/(n+1),  
															((n-j)*v.hoekpunten[rib[2*i]].y + (j+1)*v.hoekpunten[rib[2*i+1]].y)/(n+1),
															((n-j)*v.hoekpunten[rib[2*i]].z + (j+1)*v.hoekpunten[rib[2*i+1]].z)/(n+1));
				}
			}
		}
		else if(figNr==2)
		{	
			v = new Icosaeder(1);
			int n = aantalRibPunten;
			int aantalHp = 12;
			int aantalRib = 30;
			aantalGetekendeHoekpunten = aantalHp + n*aantalRib;
			Hoekpunt[] hp = new Hoekpunt[v.aantalHoekpunten+n*aantalRib];
			for(int i=0 ; i<v.aantalHoekpunten ; i++)
			{	hp[i]=v.hoekpunten[i];
			}
			v.hoekpunten = hp;
			v.aantalHoekpunten+=n*aantalRib;
			
			int[] rib = {0,1,0,2,0,3,0,4,0,5,
						 1,2,2,3,3,4,4,5,5,1,
						 1,10,1,6,2,6,2,7,3,7,
						 3,8,4,8,4,9,5,9,5,10,
						 
						 6,7,7,8,8,9,9,10,10,6,
						 6,11,7,11,8,11,9,11,10,11				 
			};
			for(int i=0 ; i<aantalRib ; i++)
			{	for(int j=0 ; j<n ; j++)
				{	v.hoekpunten[aantalHp+n*i+j] = new Hoekpunt(((n-j)*v.hoekpunten[rib[2*i]].x + (j+1)*v.hoekpunten[rib[2*i+1]].x)/(n+1),  
															((n-j)*v.hoekpunten[rib[2*i]].y + (j+1)*v.hoekpunten[rib[2*i+1]].y)/(n+1),
															((n-j)*v.hoekpunten[rib[2*i]].z + (j+1)*v.hoekpunten[rib[2*i+1]].z)/(n+1));
				}
			}
		}
		else if(figNr==3)
		{
			v = (new Icosaeder(1.3)).dualiseer();
			int n = aantalRibPunten;
			int aantalHp = 20;
			int aantalRib = 30;
			aantalGetekendeHoekpunten = aantalHp + n*aantalRib;
			Hoekpunt[] hp = new Hoekpunt[v.aantalHoekpunten+n*aantalRib];
			for(int i=0 ; i<v.aantalHoekpunten ; i++)
			{	hp[i]=v.hoekpunten[i];
			}
			v.hoekpunten = hp;
			v.aantalHoekpunten+=n*aantalRib;
			
			int[] rib = {0,1,1,2,2,3,3,4,4,0,
						 0,5,1,6,2,7,3,8,4,9,
						 5,10,6,11,7,12,8,13,9,14,
						 10,6,11,7,12,8,13,9,14,5,
						 10,15,11,16,12,17,13,18,14,19,
						 15,16,16,17,17,18,18,19,19,15				 
			};
			for(int i=0 ; i<aantalRib ; i++)
			{	for(int j=0 ; j<n ; j++)
				{	v.hoekpunten[aantalHp+n*i+j] = new Hoekpunt(((n-j)*v.hoekpunten[rib[2*i]].x + (j+1)*v.hoekpunten[rib[2*i+1]].x)/(n+1),  
															((n-j)*v.hoekpunten[rib[2*i]].y + (j+1)*v.hoekpunten[rib[2*i+1]].y)/(n+1),
															((n-j)*v.hoekpunten[rib[2*i]].z + (j+1)*v.hoekpunten[rib[2*i+1]].z)/(n+1));
				}
			}
		}
		else if(figNr==4)
		{
			v = new Tetraeder(1);
			int n = aantalRibPunten;
			int aantalHp = 4;
			int aantalRib = 6;
			
			Hoekpunt[] hp = new Hoekpunt[v.aantalHoekpunten+n*aantalRib];
			for(int i=0 ; i<v.aantalHoekpunten ; i++)
			{	hp[i]=v.hoekpunten[i];
			}
			v.hoekpunten = hp;
			v.aantalHoekpunten+=n*aantalRib;
			
			int[] rib = {0,1,0,2,0,3,1,2,1,3,2,3};
			for(int i=0 ; i<aantalRib ; i++)
			{	for(int j=0 ; j<n ; j++)
				{	v.hoekpunten[aantalHp+n*i+j] = new Hoekpunt(((n-j)*v.hoekpunten[rib[2*i]].x + (j+1)*v.hoekpunten[rib[2*i+1]].x)/(n+1),  
															((n-j)*v.hoekpunten[rib[2*i]].y + (j+1)*v.hoekpunten[rib[2*i+1]].y)/(n+1),
															((n-j)*v.hoekpunten[rib[2*i]].z + (j+1)*v.hoekpunten[rib[2*i+1]].z)/(n+1));
				}
			}
		}
		else if(figNr==5)
		{
			v = new Prisma(0.7,6,1);
			
		}
		else if(figNr==6)
		{
			v = new Kuboctaeder(1.8).dualiseerb();
			
		}
		
		for(int i=0 ; i<v.aantalVlakken ; i++)
		{	v.vlakken[i].vulkleur = "transparant";
		}
		
		tekenOpnieuw();
	}
	
	/**
	 * actie bij MouseDown/TouchStart: <br>
	 * maak lijn: kijk of een hoekpunt van het Veelvlak in wording
	 * aangeklikt is en onthoudt dit; als niet, kijk dan of een 
	 * nieuw hoekpunt aangeklikt is, voeg het toe aan het Veelvlak in wording en 
	 * onthoudt dit; zodra twee punten aangeklikt zijn, maak dan de lijn;<br>
	 * maak vlak: kijk of een hoekpunt van het Veelvlak in wording
	 * aangeklikt is en onthoudt dit; als niet, kijk dan of een 
	 * nieuw hoekpunt aangeklikt is, voeg het toe aan het Veelvlak in wording en 
	 * onthoudt dit; opnieuw aanklikken van de eerste twee punten is een reset;
	 * opnieuw aanklikken van het eerste punt na drie aangeklikte punten
	 * maakt het vlak; idem na meer dan drie aangeklikte punten mits deze
	 * in hetzelfde vlak liggen    
	 */
	public void muisDrukActie()
	{	
		if (!muisDrukAan)
			return;
			
		boolean raak = false;
		int max = tv.aantalHoekpunten;
		// loop langs de trefpunten, dat zijn tv's hoekpunten, 
		for (int i = 0; i < max; i++)
		{	double ax = trefpunten[i].x - geefDrukx();
			double ay = trefpunten[i].y - geefDruky();
			if ((ax < 4 && ax > -4) && (ay < 4 && ay > -4))
			{	raak = true;
				if (maakLijn)
				{	if (aantalPuntenRood == 0)
					{	puntnr1 = i;
						aantalPuntenRood++;
						trefpuntRaak[i] = true;
					}
					else 
					{	puntnr2 = i;
						if (puntnr1 != puntnr2)
							tv.maakLijn(puntnr1,puntnr2,"rood");
						aantalPuntenRood = 0;
						// dubbel
						wisTrefpunten();
						trefpuntRaak[puntnr1] = false;
						// nieuwe snijpunten?
						if (tv.aantalLijnen > 1)
							zoekSnijpunten();
					}
				}
				else if (maakVlak)
				{	if (aantalPuntenRood > 0 && i == puntnr[aantalPuntenRood-1])
					{	wisTrefpunten();
						aantalPuntenRood = 0;
					}
					else if (aantalPuntenRood > 1 && i == puntnr[aantalPuntenRood-2])
					{	wisTrefpunten();
						aantalPuntenRood = 0;
					}
					else if(i!=puntnr[0] || aantalPuntenRood==0)
					{	if(aantalPuntenRood<3)
						{	puntnr[aantalPuntenRood] = i;
							aantalPuntenRood++;
							trefpuntRaak[i]=true;
						}
						else if(checkVlak(tv.hoekpunten[i]))
						{   puntnr[aantalPuntenRood] = i;
							aantalPuntenRood++;
							trefpuntRaak[i]=true;
						}
						
					}
					else 
					{	puntnr[aantalPuntenRood] = i; 
						tv.voegVlakToe(aantalPuntenRood,puntnr);
						aantalPuntenRood=0;
						wisTrefpunten();
					}
				}
				break;
			} // trefpunt geraakt
		} // for loop trefpunten
		
		if (raak)
		{	tekenOpnieuw();
			return;
		}
		else 
		{	raak = false; 
			max = aantalHpNieuw;
			// loop langs de nieuwe hoekpunten
			for (int i = 0; i < max; i++)
			{	double ax = trefpuntenNieuw[i].x - geefDrukx();
				double ay = trefpuntenNieuw[i].y - geefDruky();
				if ((ax < 4 && ax > -4) && (ay < 4 && ay > -4))
				{	
					raak = true;
					tv.voegHoekpuntToe(hoekpuntenNieuw[i].x, hoekpuntenNieuw[i].y, hoekpuntenNieuw[i].z);
					tv.aantalHoekpunten--;
					trefpuntRaak[tv.aantalHoekpunten]= true;
					
					if(maakLijn)
					{	if(aantalPuntenRood==0)
						{	puntnr1 = tv.aantalHoekpunten;
							aantalPuntenRood++;
							trefpuntRaak[tv.aantalHoekpunten]=true;
						}
						else 
						{	puntnr2 = tv.aantalHoekpunten;
							if(puntnr1!=puntnr2)
								tv.maakLijn(puntnr1,puntnr2,"rood");
							aantalPuntenRood=0;
							wisTrefpunten();
							trefpuntRaak[puntnr1]=false;
							if(tv.aantalLijnen>1)
								zoekSnijpunten();
						}
					}
					else if(maakVlak)
					{	if(aantalPuntenRood>0 && tv.aantalHoekpunten==puntnr[aantalPuntenRood-1])
						{	wisTrefpunten();
							aantalPuntenRood=0;
						}
						else if(aantalPuntenRood>1 && tv.aantalHoekpunten==puntnr[aantalPuntenRood-2])
						{	wisTrefpunten();
							aantalPuntenRood=0;
						}
						else if(tv.aantalHoekpunten!=puntnr[0] || aantalPuntenRood==0)
						{	if(aantalPuntenRood<3)
							{	puntnr[aantalPuntenRood] = tv.aantalHoekpunten;
								aantalPuntenRood++;
								trefpuntRaak[tv.aantalHoekpunten]=true;
							}
							else if(checkVlak(tv.hoekpunten[tv.aantalHoekpunten]))
							{   puntnr[aantalPuntenRood] = tv.aantalHoekpunten;
								aantalPuntenRood++;
								trefpuntRaak[tv.aantalHoekpunten]=true;
							}
							else 
							{	trefpuntRaak[tv.aantalHoekpunten]= false;
								tv.aantalHoekpunten--;
								tv.hpRijAantal = tv.hpRijAantal - 3;
							}
						}
						else 
						{	puntnr[aantalPuntenRood] = tv.aantalHoekpunten; 
							tv.voegVlakToe(aantalPuntenRood,puntnr);
							aantalPuntenRood=0;
							wisTrefpunten();
						}
						
					}
					tv.aantalHoekpunten++;
					break;
				}
			}
			if (raak)
			{	tekenOpnieuw();
				return;
			}
		}
	}
	
	/**
	 * slepen resulteert in draaien van basisfiguur en veelvlak
	 */
	public void muisSleepActie()
	{		
		xhoek -= 0.5*geefSleepdy();
		yhoek += 0.5*geefSleepdx();
		matres.initialiseer();
		matres.xdraai(beginx+xhoek);
		matres.ydraai(beginy+yhoek);
		tekenOpnieuw();
	}
	
}