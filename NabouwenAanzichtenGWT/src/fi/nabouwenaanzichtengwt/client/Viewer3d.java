package fi.nabouwenaanzichtengwt.client;


import java.util.logging.Logger;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.vaadin.pointerevents.client.PointerDownEvent;
import com.vaadin.pointerevents.client.PointerDownHandler;
import com.vaadin.pointerevents.client.PointerMoveEvent;
import com.vaadin.pointerevents.client.PointerMoveHandler;
import com.vaadin.pointerevents.client.PointerUpEvent;
import com.vaadin.pointerevents.client.PointerUpHandler;

/**
 * klasse die het kubusbouwsel op een Canvas tekent; <br>
 * de klasse handelt ook (instelbaar) Mouse en Touch Events op het Canvas af, te
 * weten het draaien van het kubusbouwsel (MouseMove/TouchMove) en  
 * het toevoegen/verwijderen van een kubusje (MouseUp/TouchEnd)
 * @author Peter Boon
 */

public class Viewer3d
{
	private static final double deg15 = Math.PI / 180 * 15;
	private static final double deg5  = Math.PI / 180 * 5;
	
	private static final double ANGLE_TRESHOLD = deg15;
	/**
	 * teken Canvas
	 */
	Canvas canvas;
	/**
	 * eigenaar van deze Viewer3d
	 */
	private NabouwenAanzichtenGWT eigenaar;
	/**
	 * getal rooster om over een bovenaanzicht te leggen,
	 * zie klasse GetalRooster
	 */
	private GetalRooster gr;

	/**
	 * luisteren naar Mouse en Touch Events, zie klasse Muisbeheerder
	 */
	private MuisBeheerder mb;
	/**
	 * breedte en hoogte
	 */
	private int breedte, hoogte;
	/**
	 * actuele positie van de tekencursor
	 */
	private Punt3D beginpunt;
	/**
	 * nieuwe positie van de tekencursor na beweging
	 */
	private Punt3D eindpunt;
	/**
	 * initiele positie van de tekencursor
	 */
	private Punt3D startpunt;
	/**
	 * 3d-objecten, zie klasse Lichaam3D
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
	 * is de pen aan (punten worden verbonden)?
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
	 * moeten vlakjes van de kubusjesk gekleurd worden met een schaduw-effect?
	 */
	private boolean schaduw;
	/**
	 * is het getalrooster zichtbaar?
	 */
	private boolean grZichtbaar;
	/**
	 * nummer van het actuele Lichaam3D
	 */
	private int lnummer;
	/**
	 * kleur van de pen
	 */
	private CssColor penkleur;

	/**
	 * kleur van de vlakjes van de kubusjes
	 */
	private CssColor vulkleur;
	/**
	 * achtergrondkleur
	 */
	private CssColor achtergrondkleur;
	/**
	 * wordt er naar de muis geluisterd?
	 */
	public boolean muisAan;
	/**
	 * wordt er naar een muisklik geluisterd (bouwen/slopen)
	 * of kan er alleen gesleept worden (draaien)
	 */
	public boolean klikAan;
	/**
	 * is er een pijl die anar de voorkant van het kubusbouwsel wijst?
	 */
	public boolean pijlAan;
	/**
	 * is er een balk aan de voorkant van het grondvlak?
	 */
	public boolean balkAan;
	/**
	 * is het kubusbousel een bovenaanzicht?
	 */
	public boolean maakAanzicht;
	/**
	 * afstand oog op de positieve z-as tot x-y-vlak
	 */
	private double afstand;
	/**
	 * de geprojecteerde vlakjes van de vierkantjes in het grondvlak
	 */
	Polygon[][] p;
	/**
	 * de geprojecteerde vlakjes van alle kubusjes in het KubusRooster:<br>
	 * eerste 3 indices zijn de positie (elke coordinaat tussen 
	 * 0 en kr.maxAantal-1), laatste index varieert van 0 tot 5
	 * (6 vlakjes per kubusjes)
	 */
	Polygon[][][][] pp;
	/**
	 * alle klikbare vlakjes van kubusjes in het bouwsel, zie klasse KlikVlak
	 */
	Klikvlak[] kv;
	/**
	 * het aantal klikbare vlakjes van kubusjes in het bouwsel
	 */
	int aantalKv;
	/**
	 * het KubusRooster dat weergegeven wordt, zie klasse KubusRooster
	 */
	KubusRooster kr;
	/**
	 * een vergroot/verklein factor afhankelijk van de
	 * breedte van het Canvas
	 */
	private double k;
	/**
	 * de initiele draaiing van het kubusbouwsel om de x-as resp. de y-as
	 */
	double beginx, beginy;
	
	/**
	 * actuele draaiing van het kubusbouwsel om de x-as is beginx+xhoek;<br> 
	 * xhoek wordt veranderd bij slepen, zie methode muisSleepActie 
	 */
	double xhoek;
	
	/**
	 * actuele draaiing van het kubusbouwsel om de y-as is beginy+yhoek;<br> 
	 * yhoek wordt veranderd bij slepen, zie methode muisSleepActie 
	 */
	double yhoek;
	
	/**
	 * de gesorteerde vlakken uit een de verschillende Lichaam3D 
	 */
	private int[] sorteerRij;

	/**
	 * werd een kubusje verwijderd d.m.v. een long klik?
	 */
	private boolean removed = false;
	/**
	 * wordt er gesloopt?
	 */
	private boolean removing = false;
	/**
	 * was er een lange MouseDown/TouchStart?
	 */
	private boolean holdMouse = false;
	/**
	 * tijd laatste MouseDown/TouchStart
	 */
	private long holdMouseStartTime = 0;
	/**
	 * tijd laatste MouseUp/TouchEnd
	 */
	private long holdMouseEndTime = 0;
	/**
	 * het laatste bouw/sloop commando
	 */
	private String lastBuildCommand = "";

	Viewer3d() { }
	/**
	 * constructor: initialiseer de attributen, het Canvas en de
	 * Mouse/Touch handlers voor het Canvas  
	 * @param kr het kubusrooster
	 * @param x x-positie
	 * @param y y-positie
	 * @param b breedte
	 * @param h hoogte
	 * @param hb de eigenaar van deze Viewer3D
	 */
	public Viewer3d(KubusRooster kr, int x, int y, int b, int h, NabouwenAanzichtenGWT hb)
	{
		canvas = Canvas.createIfSupported();
		canvas.setWidth(b + "px");
		canvas.setHeight(h + "px");
		canvas.setCoordinateSpaceWidth(b);
		canvas.setCoordinateSpaceHeight(h);
		canvas.setStyleName(hb.nabouwenAanzichtenCss.canvas());
		breedte = b;
		hoogte = h;
		eigenaar = hb;

		this.kr = kr;
		int n = kr.maxAantal;
		p = new Polygon[n][n];
		pp = new Polygon[n][n][n][6];
		aantalKv = 0;
		kv = new Klikvlak[n * n * n * 7];
		for (int i = 0; i < kr.maxAantal; i++)
		{
			for (int j = 0; j < kr.maxAantal; j++)
			{
				for (int k = 0; k < kr.maxAantal; k++)
				{
					for (int m = 0; m < 6; m++)
					{
						pp[i][j][k][m] = new Polygon();
					}
				}
			}
		}

		mb = new MuisBeheerder(this);
		canvas.addMouseDownHandler(mb);
		canvas.addMouseUpHandler(mb);
		canvas.addMouseMoveHandler(mb);
		//canvas.addMouseOverHandler(mb);
		canvas.addTouchStartHandler(mb);
		canvas.addTouchEndHandler(mb);
		canvas.addTouchMoveHandler(mb);
		
		(canvas.asWidget()).addDomHandler((PointerMoveHandler)mb, PointerMoveEvent.getType()); 
		(canvas.asWidget()).addDomHandler((PointerUpHandler)mb, PointerUpEvent.getType()); 
		(canvas.asWidget()).addDomHandler((PointerDownHandler)mb, PointerDownEvent.getType()); 
		
		
		achtergrondkleur = CssColor.make("white");
		leeg = false;
		schaduw = true;
		muisAan = true;
		klikAan = true;
		pijlAan = true;
		balkAan = false;
		grZichtbaar = false;
		l = new Lichaam3D[5];
		afstand = 1000;
		lnummer = 0;
		for (int i = 0; i < 5; i++)
		{
			l[i] = new Lichaam3D();
			l[i].zetAfstand(afstand);
		}
		sorteerRij = new int[2000];
		mat = new Matrix3D();
		k = 230;
		xhoek = 0;
		yhoek = 0;
		beginx = 30;
		beginy = -30;
	}

	/**
	 * getter voor de laatste bouw/sloop opdracht 
	 * @return lastBuildCommand
	 */
	public String getLastBuildCommand()
	{	return lastBuildCommand;
	}
	    
	/**
	 * setter voor de laatste bouw/sloop opdracht
	 * @param s nieuwe waarde lastBuildCommand
	 */
	public void setLastBuildCommand(String s)
	{	lastBuildCommand = s;
	}
	 
	/**
	 * zet de MuisBeheerder
	 * @param mb instantie van MuisBeheerder 
	 */
	public void zetMuisBeheerder(MuisBeheerder mb)
	{
		this.mb = mb;
	}

	/**
	 * getter voor het teken canvas
	 * @return canvas
	 */
	public Canvas getCanvas()
	{
		return canvas;
	}

	/**
	 * zet de Context2d van het teken canvas
	 */
	public void initContext2d()
	{
		gIm = canvas.getContext2d();
	}

	/**
	 * zet de afstand oog (op de positive z-as) to het 
	 * x-y-vlak voor alle instanties van Lichaam3D
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
	 * setter voor tekeken met schaduw-effect
	 * @param s true/false
	 */
	public void zetSchaduw(boolean s)
	{
		schaduw = s;
	}

	/**
	 * setter voor de draaing om x- en y-as;
	 * zet beginx (beginy) op de nieuwe waarde en xhoek (yhoek) op 0 
	 * @param hx draaing om x-as
	 * @param hy draaing om y-as
	 */
	public void zetBeginHoeken(double hx, double hy)
	{
		beginx = hx;
		beginy = hy;
		xhoek = 0;
		yhoek = 0;
	}

	/**
	 * wordt er naar de muis geluisterd?
	 * @param b true/false
	 */
	public void zetMuisAan(boolean b)
	{
		muisAan = b;
	}

	/**
	 * wordt er naar een muisklik geluisterd (bouwen/slopen)
	 * of kan er alleen gesleept worden (draaien)
	 * @param b true/false
	 */
	public void zetKlikAan(boolean b)
	{
		klikAan = b;
	}

    /**
     * toon/verberg de balk aan de voorkant van het kubusbouwsel
     * @param b true/false
     */
    public void zetPijlAan(boolean b)
    {   pijlAan = b;
        if (pijlAan)
        	balkAan = false;
    }

    /**
     * toon/verberg de balk aan de voorkant van het kubusbouwsel
     * @param b true/false
     */
    public void zetBalkAan(boolean b)
    {   balkAan = b;
        if (balkAan)
            pijlAan = false;
    }
   
    /**
     * zet deze Viewer3d op bovenaanzicht
     * @param b alleen gebruikt met true
     */
    public void zetMaakAanzicht(boolean b)
    {   maakAanzicht = b;
	    if (maakAanzicht)
	    {	zetBeginHoeken(90,0);
	    	zetAfstand(1000000000);
	    	zetMuisAan(false);
	    	zetSchaduw(false);
	    }
    }

    /**
     * zet een nieuw kubusrooster in deze Viewer3d
     * @param kur nieuw KubusRooster
     */
	public void zetKubusRooster(KubusRooster kur)
	{
		kr = kur;
		if (grZichtbaar)
		{ 	zetGetalRooster(true);
			for (int i = 0; i < kr.maxAantal; i++)
			{
				for (int j = 0; j < kr.maxAantal; j++)
				{
					for (int k = 0; k < kr.maxAantal; k++)
					{
						if (kr.kubussen[i][j][k] != null && gr.geefHoogte(i, j) < k + 1)
						{
							gr.zetHoogte(i, j, k);
						}
					}
				}
			}
		}
		int n = kr.maxAantal;
		p = new Polygon[n][n];
		pp = new Polygon[n][n][n][6];
		for (int i = 0; i < kr.maxAantal; i++)
		{
			for (int j = 0; j < kr.maxAantal; j++)
			{
				for (int k = 0; k < kr.maxAantal; k++)
				{
					for (int m = 0; m < 6; m++)
					{
						pp[i][j][k][m] = new Polygon();
					}
				}
			}
		}
		aantalKv = 0;
		kv = new Klikvlak[n * n * n * 7];
	}

	/**
	 * toon/verberg het GetalRooster, zet deze Viewer3d op bovenaanzicht
	 * @param bool true/false
	 */
	public void zetGetalRooster(boolean bool)
	{
		grZichtbaar = bool;
		if (bool)
		{
			int n = kr.maxAantal;
			int x = breedte * 80 / 300 + 12;
			int y = breedte * 80 / 300 - 11;
			int b = breedte * 38 / 300;
			int h = hoogte * 57 / 300;
			gr = new GetalRooster(n, x, y, b, h, breedte, hoogte);
		}
		zetBeginHoeken(90, 0);
		zetAfstand(1000000000);
		zetMuisAan(false);
		zetSchaduw(false);
	}

	/**
	 * vindt de hoogte van het kubusbouwsel op elke
	 * (x,y)-positie en update dit in het GetalRooster
	 */
    public void zetHoogtes()
    {   if (gr != null)
        {   for (int i = 0; i < kr.maxAantal; i++)
            {   for (int j = 0; j < kr.maxAantal; j++)
                {   for (int k = 0; k < kr.maxAantal; k++)
                    {   if (kr.kubussen[i][j][k] != null && gr.geefHoogte(i,j) < k + 1)
                        {   gr.zetHoogte(i, j, k);
                        }
                    }
                }
            }
        
        }
        
    }

	/**
	 * initialiseer, draai en teken het kubusrooster 
	 */
	void tekenprogramma()
	{
		mat.initialiseer();
		mat.xdraai(beginx + xhoek);
		mat.ydraai(beginy + yhoek);
		tekenKubusRooster();
	}
	
	Punt3D normalVlak(Matrix3D hoekMatrix) {
		Punt3D p = new Punt3D(0,0,0);
		Matrix3D m = hoekMatrix;
		p =  m.geefVolgendPunt(p, 0, 1, 0);
		//logger.info("normalVlak = " + p.x + "," + p.y + "," + p.z);
		return p;
	}

	protected Matrix3D hoekMatrix() {
		Matrix3D m = new Matrix3D();
		m.initialiseer();
		m.xdraai(beginx + xhoek);
		m.ydraai(beginy + yhoek);
		return m;
	}

	Punt3D pijl(Matrix3D hoekMatrix) {
		Punt3D p = new Punt3D(0,0,0);
		Matrix3D m = hoekMatrix;
		p =  m.geefVolgendPunt(p, 0, 0, 1);
		//logger.info("pijl = " + p.x + "," + p.y + "," + p.z);
		return p;
	}
	
	

	/**
	 * teken het kubusrooster: grondvlak (als nodig), pijl of
	 * balk aan de voorkant (als nodig); actualiseer de 
	 * geprojecteerde vlakjes en de KlikVlakken 
	 */
	void tekenKubusRooster()
	{
		aantalKv = 0;
		if (!maakAanzicht)
			tekenVeelvlak(0, kr.grondvlak);
		if (pijlAan)
		{
			for (int i = 0; i < kr.pijl.aantalVlakken; i++)
			{
				tekenVlak(1, kr.pijl.vlakken[i]);
				kv[aantalKv] = new Klikvlak(i, 0, 1, 6);
				aantalKv++;
			}
		}
		if (balkAan)
	    {
	        for (int i = 0; i < kr.balk.aantalVlakken; i++)
	        {   tekenVlak(1, kr.balk.vlakken[i]);
	            kv[aantalKv] = new Klikvlak(i, 0, 1, 6);
	            aantalKv++;
	        }
	    }
		for (int i = 0; i < kr.maxAantal; i++)
		{
			for (int j = 0; j < kr.maxAantal; j++)
			{	
				if (maakAanzicht) 
        		{	kr.vierkanten[i][j].vlakken[0].vulkleur = "wit";
        			kr.vierkanten[i][j].vlakken[0].vorigeKleur = "wit";
        		}
				else
				{  	kr.vierkanten[i][j].vlakken[0].vorigeKleur = "lichtgrijs";
					kr.vierkanten[i][j].vlakken[0].vulkleur = "lichtgrijs";
				}
				tekenVlak(1, kr.vierkanten[i][j].vlakken[0]);
				p[i][j] = geefVlak(1);
				
				kv[aantalKv] = new Klikvlak(i, j, 0, 6);
				aantalKv++;
				for (int k = 0; k < kr.maxAantal; k++)
				{
					if (kr.kubussen[i][j][k] != null)
					{
						for (int m = 0; m < 6; m++)
						{
							if (kr.kubussen[i][j][k].isOnbedekt[m])
							{
								tekenVlak(1, kr.kubussen[i][j][k].vlakken[m]);
								pp[i][j][k][m] = geefVlak(1);
								kv[aantalKv] = new Klikvlak(i, j, k, m);
								aantalKv++;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * teken Veelvlak vv (vlaksgewijs);  
	 * @param n nummer van het Lichaam3D dat gebruikt wordt om de vlakken
	 * te tekenen
	 * @param vv het te tekenen veelvlak
	 */
	void tekenVeelvlak(int n, Veelvlak vv)
	{
		for (int i = 0; i < vv.aantalVlakken; i++)
		{
			tekenVlak(n, vv.vlakken[i]);
		}
	}

	/**
	 * teken Vlak v (alleen de voorkant)
	 * @param n nummer van te gebruiken Lichaam3D 
	 * @param v het te tekenen Vlak
	 */
	void tekenVlak(int n, Vlak v)
	{
		penUit();
		stap(k * v.punten[0].x, k * v.punten[0].y, k * v.punten[0].z);
		if (!(v.lijnkleur == "transparant"))
			penAan(v.lijnkleur);
		vulAan(n, v.vulkleur);
		for (int i = 0; i < v.aantalHoekpunten; i++)
		{
			int a = i;
			int b = (i + 1) % v.aantalHoekpunten;
			stap(-k * (v.punten[a].x - v.punten[b].x), -k * (v.punten[a].y - v.punten[b].y), -k * (v.punten[a].z - v.punten[b].z));
		}
		vulUit(n);
		penUit();
		stap(-k * v.punten[0].x, -k * v.punten[0].y, -k * v.punten[0].z);

	}

	public void draw()
	{
		draw(gIm);
	}

	/**
	 * initialiseer en roep tekenOpImage aan 
	 * @param g Context2d om te tekenen, redundant
	 */
	public void draw(Context2d g)
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
	 * teken de veelvlakken in de 3d-lichamen
	 * @param wis wis het Canvas voor het tekenen, hier altijd true
	 */
	public void tekenOpImage(boolean wis)
	{
		if (gIm == null)
			return;
		beginpunt = new Punt3D(startpunt);
		eindpunt = new Punt3D(beginpunt);
		mat.initialiseer();
		penAan(0, 0, 0);
		vul = false;
		tekenprogramma();
		for (int i = 0; i < 5; i++)
		{
			l[i].sorteer();
		}
		for (int i = 0; i < 2000; i++)
		{
			sorteerRij[i] = l[1].sorteerRij[i];
		}
		if (wis)
		{
			gIm.setFillStyle(achtergrondkleur);
			gIm.fillRect(0, 0, breedte, hoogte);
		}
		for (int j = 0; j < 5; j++)
		{
			l[j].draw(gIm, schaduw);
			l[j] = new Lichaam3D();
			l[j].zetAfstand(afstand);
			l[j].maakNulpunt(breedte / 2, hoogte / 2, 0);
		}
		if (gr != null)
		{    gr.paint(gIm, p);
		}
	}

	/**
	 * volledige repaint 
	 */
	void tekenOpnieuw()
	{
		tekenOpImage(true);
	}

	/**
	 * paint zonder wissen
	 */
	void tekenErbij()
	{
		tekenOpImage(false);
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
		else
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
		else
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
	 * zet de achtergrondkleur op kl 
	 * @param kl kleurnaam
	 */
	void achtergrondkleur(String kl)
	{
		achtergrondkleur = maakKleur(kl);
	}

	/**
	 * zet de achetgrondkleur op RGB-kleur (r,g,b)
	 * @param r rood
	 * @param g groen
	 * @param b blauw
	 */
	void achtergrondkleur(int r, int g, int b)
	{
		achtergrondkleur = CssColor.make(r, g, b);
	}

	/**
	 * zet de achetrgrondkleur op CssColor c
	 * @param c CssColor
	 */
	void zetAchtergrond(CssColor c)
	{
		achtergrondkleur = c;
	}

	/**
	 * geef het laatst getekende Punt uit Lichaam3D nummer 0
	 * @return laatstgetekende Punt
	 */
	Punt geefPunt() 
	{
		double pf = (afstand - beginpunt.z) / afstand;
		double begx = l[0].nulpunt.x + (beginpunt.x - l[0].nulpunt.x) / pf;
		double begy = l[0].nulpunt.y + (beginpunt.y - l[0].nulpunt.y) / pf;
		return new Punt(begx, begy);
	}

	/**
	 * geef het laatst getekende Punt uit Lichaam3D nummer n
	 * @param n nummer van het Lichaam3D
	 * @return laatstgetekende Punt
	 */
	Punt geefPunt(int n)
	{
		double pf = (afstand - beginpunt.z) / afstand;
		double begx = l[n].nulpunt.x + (beginpunt.x - l[n].nulpunt.x) / pf;
		double begy = l[n].nulpunt.y + (beginpunt.y - l[n].nulpunt.y) / pf;
		return new Punt(begx, begy);
	}

	/**
	 * geef het laatst getekende Polygon uit Lichaam3D nummer 0 indien
	 * de normaal naar het oog wijst, anders een leeg Polygon
	 * @return Polygon als beschreven
	 */
	Polygon geefVlak()
	{
		if (l[0].vlakken[l[0].aantalPolygonen - 1].normaal.z > 0)
			return l[0].vlakken[l[0].aantalPolygonen - 1].pol;
		else
			return new Polygon();
	}

	/**
	 * geef het laatst getekende Polygon uit Lichaam3D nummer n indien
	 * de normaal naar het oog wijst, anders een leeg Polygon
	 * @param n nummer van het Lichaam3D
	 * @return Polygon als beschreven
	 */
	Polygon geefVlak(int n)
	{
		if (l[n].vlakken[l[n].aantalPolygonen - 1].normaal.z > 0)
			return l[n].vlakken[l[n].aantalPolygonen - 1].pol;
		else
			return new Polygon();
	}

	/**
	 * gegeven de naam van een kleur, maak de corresponderende CssColor
	 * @param kl naam van de kleur 
	 * @return corresponderende CssColor
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
			return CssColor.make(255, 128, 0);
		else
			return CssColor.make(0, 0, 0);
	}


	/**
	 * update de String lastBuildCommand; merk op dat de kubusjes
	 * in het rooster coordinaten tussen 0 en maxAantal-1 hebben, 
	 * in de build commands tussen 1 en maxAantal
	 * @param changed is er iets veranderd?
	 * @param bouw bouwen of slopen?
	 * @param x x-positie veranderd kubusje
	 * @param y y-positie veranderd kubusje
	 * @param z z-positie veranderd kubusje
	 */
    private void updateLastBuildCommand(boolean changed, boolean bouw, int x, int y, int z)
    {	if(changed && bouw)
    	{	lastBuildCommand = eigenaar.rb.bouwOpdracht() + " " + (x+1) + "," + (y+1) + "," + (z+1);
    	}
    	else if(changed && !bouw)
    	{	lastBuildCommand = eigenaar.rb.sloopOpdracht() + " " + (x+1) + "," + (y+1) + "," + (z+1);
    	}
    	else
    		lastBuildCommand = "";
    }

    Punt3D lastNormal = new Punt3D(0,1,0);
    static final double cos15 = Math.cos(deg15);
    static final double cos30 = Math.cos(Math.PI * 30 / 180);
    
    static final double SIDE_TRESHOLD = cos15;
    
    static Logger logger = Logger.getLogger("Viewer3d");
    double rotatie;
    enum Side { LEFT, FRONT, RIGHT, BACK, TOP};
    Side side;
    
	/**
	 * actie bij MouseMove/TouchMove Event: draai
	 */
	public void muisSleepActie()
	{
		if (removed)
			return;
		holdMouse = holdMouse && System.currentTimeMillis() - holdMouseStartTime < 300;
		if (!holdMouse && muisAan)
		{

			logger.info("voor xh " + xhoek + " yh " + yhoek);
			Matrix3D old = hoekMatrix();
			old.transpose();
			
			xhoek -= 0.5 * mb.geefSleepdy();
			if (xhoek > 90 - beginx)
				xhoek = 90 - beginx;
			if (xhoek < 0 - beginx)
				xhoek = 0 - beginx;
			yhoek += 0.5 * mb.geefSleepdx();
			logger.info("na   xh " + xhoek + " yh " + yhoek);
			tekenOpnieuw();

			if (eigenaar.logOption && eigenaar.isNakijkModus())
			{	
			Matrix3D hoek = hoekMatrix();
			Punt3D normal = normalVlak(hoek);
			Punt3D pijl = pijl(hoek);
			old.mult(hoek); // old = hoek * old; premultiply
			double trace = old.trace();
			trace = Math.max(-1, trace); // fouten in berekening.
			trace = Math.min(3,  trace);
			double angle = Math.acos((trace - 1.0)*0.5);
			rotatie += angle;
			logger.info("hoek = " + deg(angle) + ", totaal " + deg(rotatie));
			if (Math.abs(normal.z) > SIDE_TRESHOLD) {
				if (side != Side.TOP)
				{	side = Side.TOP; rotatie = deg15;
					logger.info("mostly normal z: kijk van boven");
					eigenaar.viewed("entering " + side.name());
				}
			} else if (Math.abs(normal.y) > SIDE_TRESHOLD) {
				if ((pijl.x) > SIDE_TRESHOLD) {
					if (side != Side.LEFT)
					{
						side = Side.LEFT; rotatie = ANGLE_TRESHOLD;
						eigenaar.viewed("entering " + side.name());
						logger.info("mostly pijl x linkerkant");
					}
				}
				else if ((pijl.x) < -SIDE_TRESHOLD) {
			    	if (side != Side.RIGHT)
			    	{
				    	side = Side.RIGHT; rotatie = ANGLE_TRESHOLD;
						eigenaar.viewed("entering " + side.name());
			    		logger.info("mostly pijl x rechterkant");
			    	}
			    }
				else if ((pijl.z) > SIDE_TRESHOLD)  {
			    	if (side != Side.FRONT)
			    	{
				    	side = Side.FRONT; rotatie = ANGLE_TRESHOLD;
						eigenaar.viewed("entering " + side.name());				    	
			    		logger.info("mostly pijl z voor");
			    	}
			    }
				else if ((pijl.z) < -SIDE_TRESHOLD) {
			    	if (side != Side.BACK)
			    	{
				    	side = Side.BACK; rotatie = ANGLE_TRESHOLD;
						eigenaar.viewed("entering " + side.name());				    	
				    	logger.info("mostly pijl z achter");
			    	}
			    } else {
			    	if (side != null) {
				    	eigenaar.viewed("leaving " + side );
			    		logger.info("leaving " + side);
				    	side = null; rotatie = ANGLE_TRESHOLD;
			    	}
			    }
			} else {
				if (side != null)
				{
					eigenaar.viewed("leaving " + side);
					side = null; rotatie = ANGLE_TRESHOLD;
					logger.info("grijs gebied");
				}
			}
			if (rotatie >= ANGLE_TRESHOLD) {
				logger.info( deg(rotatie) + " rotatie >= " + deg(ANGLE_TRESHOLD) +  " graden ");
				eigenaar.rotated("("+ (xhoek+beginx) + "," + (yhoek+beginy) + ")");
				rotatie = 0.0;
			}

		}}

	}

	
	long deg(double rotatie2) {
		return Math.round(rotatie2/Math.PI * 180);
	}

	/**
	 * actie bij MouseUp/TouchEnd Event: bepaal of een vlakje van het grondvlak
	 * of een vlakje van een kubusje was aangeklikt; voeg een kubusje toe of
	 * verwijder een kubusje  
	 * @param remove dummy, is altijd false
	 */
	public void muisKkActie(boolean remove)
	{
		if (eigenaar.nagekeken)
			eigenaar.zetIsVeranderdNaNakijken(true);
		
		eigenaar.setChanged(true);
		
		String changeLog = "";
		
		for (int q = aantalKv - 1; q > -1; q--)
		{
			int n = sorteerRij[q];
			
			if (kv[n].m == 6 && kv[n].k == 0 && p[kv[n].i][kv[n].j].contains(mb.geefDrukx(), mb.geefDruky()))
			{
				boolean changed = false;
				if (eigenaar.isBouwen() && !(holdMouse || remove))
				{
					changed = kr.voegKubusToe(kv[n].i, kv[n].j, 0);
					changeLog += "addCube(" + kv[n].i + "," + kv[n].j + "," + 0 + ")";
					updateLastBuildCommand(changed, true, kv[n].i, kv[n].j, 0);
					if (gr != null)
						gr.verhoog(kv[n].i, kv[n].j);
				}
				else
				{
					changed = kr.verwijderKubus(kv[n].i, kv[n].j, 0);
					changeLog += "removeCube(" + kv[n].i + "," + kv[n].j + "," + 0 + ")";
					updateLastBuildCommand(changed, false, kv[n].i, kv[n].j, 0);
					if (gr != null)
						gr.verlaag(kv[n].i, kv[n].j);
				}
				tekenOpnieuw();
				if (changed) {
					eigenaar.zetVeranderd(false, changeLog);
				}
				return;

			}
			else if (kv[n].m != 6 && pp[kv[n].i][kv[n].j][kv[n].k][kv[n].m].contains(mb.geefDrukx(), mb.geefDruky()))
			{
				boolean changed = false;
				if (eigenaar.isBouwen() && !(holdMouse || remove) && !maakAanzicht)
				{
					if (kv[n].m == 0)
					{
						changed = kr.voegKubusToe(kv[n].i, kv[n].j, kv[n].k + 1);
						changeLog += "addCube(" + kv[n].i + "," + kv[n].j + "," + (kv[n].k+1) + ")";
						updateLastBuildCommand(changed, true, kv[n].i,kv[n].j,kv[n].k+1);
						if (gr != null)
							gr.verhoog(kv[n].i, kv[n].j);
					}
					if (kv[n].m == 1)
					{
						changed = kr.voegKubusToe(kv[n].i, kv[n].j - 1, kv[n].k);
						changeLog += "addCube(" + kv[n].i + "," + (kv[n].j-1) + "," + kv[n].k + ")";
						updateLastBuildCommand(changed, true, kv[n].i,kv[n].j-1,kv[n].k);
					}
					if (kv[n].m == 2)
					{
						changed = kr.voegKubusToe(kv[n].i + 1, kv[n].j, kv[n].k);
						changeLog += "addCube(" + (kv[n].i+1) + "," + kv[n].j + "," + kv[n].k + ")";
						updateLastBuildCommand(changed, true, kv[n].i+1,kv[n].j,kv[n].k);
					}
					if (kv[n].m == 3)
					{
						changed = kr.voegKubusToe(kv[n].i, kv[n].j + 1, kv[n].k);
						changeLog += "addCube(" + kv[n].i + "," + (kv[n].j+1) + "," + kv[n].k  + ")";
						updateLastBuildCommand(changed, true, kv[n].i,kv[n].j+1,kv[n].k);
					}
					if (kv[n].m == 4)
					{
						changed = kr.voegKubusToe(kv[n].i - 1, kv[n].j, kv[n].k);
						changeLog += "addCube(" + (kv[n].i-1) + "," + kv[n].j + "," + kv[n].k + ")";
						updateLastBuildCommand(changed, true, kv[n].i-1,kv[n].j,kv[n].k);
					}
					if (kv[n].m == 5)
					{
						changed = kr.voegKubusToe(kv[n].i, kv[n].j, kv[n].k - 1);
						changeLog += "addCube(" + kv[n].i + "," + kv[n].j + "," + (kv[n].k-1) + ")";
						updateLastBuildCommand(changed, true, kv[n].i,kv[n].j,kv[n].k-1);
					}
					tekenOpnieuw();
					if (changed)
						eigenaar.zetVeranderd(false, changeLog);
				}
				else
				{
					changed = kr.verwijderKubus(kv[n].i, kv[n].j, kv[n].k);
					changeLog += "removeCube(" + kv[n].i + "," + kv[n].j + "," + kv[n].k + ")";
					updateLastBuildCommand(changed, false, kv[n].i,kv[n].j,kv[n].k);
					if (gr != null)
						gr.verlaag(kv[n].i, kv[n].j);
					tekenOpnieuw();
					if (changed)
						eigenaar.zetVeranderd(false, changeLog);
				}
				return;
			}
		}
	}

	/**
	 * actie bij MouseDown/TouchStart Event:
	 * kijk of een vlakje van een kubusje aangeklikt werd, zet holdMouse to true
	 * neem de tijd op
	 */
	public void muisDrukActie()
	{
		if (removing)
			return;
		removing = true;
		for (int q = aantalKv - 1; q > -1; q--)
		{
			int n = sorteerRij[q];
			if (kv[n].m != 6 && pp[kv[n].i][kv[n].j][kv[n].k][kv[n].m].contains(mb.geefDrukx(), mb.geefDruky()))
			{
				holdMouse = true;
			}
		}
		holdMouseStartTime = System.currentTimeMillis();

	}

	/**
	 * actie bij MouseUp/TouchEnd Event: neem de tijd op, bepaal of
	 * er sprake is van een lange muisDruk en roep methode muisKkActie aan  
	 */
	public void muisLosActie()
	{
		holdMouseEndTime = System.currentTimeMillis();
		long holdMouseTime = holdMouseEndTime - holdMouseStartTime; 
		removing = false;
		if (!removed && (klikAan && (mb.geefDrukx() - mb.geefX()) * (mb.geefDrukx() - mb.geefX()) + 
				                    (mb.geefDruky() - mb.geefY()) * (mb.geefDruky() - mb.geefY()) < 16))
		{
			holdMouse = false;
			if (holdMouseTime > 300)
				holdMouse = true;
			muisKkActie(false);
			tekenOpnieuw();
		}
		removed = false;
		holdMouse = false;
		
		eigenaar.ingevuld = true;
	}

}
