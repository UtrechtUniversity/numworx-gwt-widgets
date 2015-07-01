package fi.mozarchgwt.client;

//import java.awt.Color;
//import java.awt.FontMetrics;
//import java.awt.Polygon;
//import java.awt.event.*;

//import javax.swing.*;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.LayoutPanel;

public class TekenPanel extends LayoutPanel //JPanel implements ActionListener
{
	public Canvas mozarchGWTCanvas;
	public Context2d gIm;

	//private Color bgcolor = Color.white;
	private CssColor bgcolor = CssColor.make(255,255,255);
	
	int breedte;
	int hoogte;
	
	Tekenblad2 tb;
	private MuisBeheerder2 mb;
	private boolean initializing;
	
	Vlakdeel2[] vlakdelen;
	private Vlakdeel2 actiefVlakdeel; //,vorigActiefVlakdeel;

// aantal wordt niet gebruikt
	private int aantal;
	int aantalVlakdelen, actiefVlakdeelNummer;
	double trek, trekx = 0,treky = 0; 
	double zijde;
	int[] volgorde;


	int beginFigAantalHp = 3;
	int beginFigAantalPz = 1;
	int beginFractielType = 1;
    boolean beginFig = false;

    boolean fractielen = false;
    
	// schaalbaarheid
	// breedte stapelstrook
	private int rightWidth = 180;
	private int mozarchRightWidth = 180;
    private int fractielRightWidth = 105;
    // centrum applet
    private int cX, cY;
    private int stapelX;
  
    // in normale(!) coordinaten
    private int veldXMin = 10, veldYMin = 10;
    private int veldXMax, veldYMax;
    
    double dummyX, startX, startY;
    
	// parametrisatie
	boolean triangles = true;
	boolean squares = true;
	boolean pentagons = false;
	boolean hexagons = true;
	boolean octagons = true;
	boolean dekagons = false;
	boolean dodekagons = true;

	int triangleHeight = 40;
	int squareHeight = 40;
	int pentagonHeight = 66;
	int hexagonHeight = 80;
	int octagonHeight = 98;
	int dekagonHeight = 130;
	int dodekagonHeight = 150;
	
	int triangleVOffset = 15;
	int squareVOffset = 15;
	int pentagonVOffset = 15;
	int hexagonVOffset = 15;
	int octagonVOffset = 15;
	int dekagonVOffset = 15;
	int dodekagonVOffset = 15;
	
	
	
	int stapelsBoven;

	public TekenPanel(int w, int h)
	{
		mozarchGWTCanvas = Canvas.createIfSupported();
		if (mozarchGWTCanvas != null)
		{	breedte = w;
			hoogte = h;
			mozarchGWTCanvas.setWidth(w + "px");
			mozarchGWTCanvas.setHeight(h + "px");
			mozarchGWTCanvas.setCoordinateSpaceWidth(w);
			mozarchGWTCanvas.setCoordinateSpaceHeight(h);

			gIm = mozarchGWTCanvas.getContext2d();
			tb = new Tekenblad2(this,breedte,hoogte);

// apart aanroepen			
//			initialiseer();
			
		}

	}

	public Canvas getCanvas()
	{
		return mozarchGWTCanvas;
	}
	
	public void initContext2d() 
	{
		gIm = mozarchGWTCanvas.getContext2d();
		
	}
    
	//-----------------------------------------------------------------------------------------
	// initalisatie
	//-----------------------------------------------------------------------------------------
/*	
	public void init()
	{	
		tb = new Tekenblad2(this);
	
		setLayout(null);
		
		initializing = true;
		initialiseer();							// wordt geimplementeerd in leerlingprogramma
		initializing = false;
		
		
		tb.setBounds(0, 0, getSize().width, getSize().height);
		
		add(tb);
		
	}
*/													
	//-------------------------------------------------------------------------------------------
	//deze methoden kunnen alleen worden gebruikt in  "initialiseer()" van leerling-applet
	//-------------------------------------------------------------------------------------------
	public void maakMuisActieMogelijk()
	{	//if (initializing) 
		//{	
		
			mb = new MuisBeheerder2(this);
			//tb.addMouseListener(mb);
			//tb.addMouseMotionListener(mb);
		
			mozarchGWTCanvas.addMouseDownHandler(mb);
			mozarchGWTCanvas.addMouseMoveHandler(mb);
			mozarchGWTCanvas.addMouseUpHandler(mb);
	
			mozarchGWTCanvas.addTouchStartHandler(mb);
			mozarchGWTCanvas.addTouchMoveHandler(mb);
			mozarchGWTCanvas.addTouchEndHandler(mb);

		//}
	}
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt in de muishandler en  doorgegeven aan MuisBeheerder mb
	//-------------------------------------------------------------------------------------------
	public int geefSleepdx()
	{	return mb.geefSleepdx();
	}
	public int geefSleepdy()
	{	return mb.geefSleepdy();
	}
	public int geefDrukx()
	{	return mb.geefDrukx();
	}
	public int geefDruky()
	{	return mb.geefDruky();
	}

	// het hele applet!!
	public void tekenOpnieuw()
	{	//repaint();
		tb.teken();
	}

	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt in "initialiseer" en doorgegeven aan Tekenblad tb (of 
	//Matrix2d) 
	//-------------------------------------------------------------------------------------------
	public void schaal(double s)
	{	tb.mat.schaal(s);
	}
	public void achtergrondkleur(String kl)
	{	tb.achtergrondkleur(kl);
	}
	public void achtergrondkleur(int r, int g, int b)
	{	if (tb != null)
			tb.achtergrondkleur(r, g, b);
	}
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt in "tekenprogramma()" en doorgegeven aan Tekenblad tb (of 
	//Matrix2d) 
	//-------------------------------------------------------------------------------------------
 	public void links(double dHoek)
	{	tb.mat.draai(dHoek);
	}
  	public void rechts(double dHoek)
	{	tb.mat.draai(-dHoek);		
	}
	public void vooruit(double dy)
	{	tb.naarVolgendPunt(0, -dy);	
	}
	public void stapy(double dy)
	{	tb.naarVolgendPunt(0, -dy);		
		//if (trb != null && trb.geefTraceStatus())
			//trb.volgendeMethode("stapy(" + Integer.toString((int)Math.rint(dy)) + ")");
	}
	public void stapx(double dx)
	{	tb.naarVolgendPunt(dx,0);		
		//if(trb!=null && trb.geefTraceStatus())
		//trb.volgendeMethode("stapx("+Integer.toString((int)Math.rint(dx))+")");
	}
	public void stap(double dx, double dy)
	{	tb.naarVolgendPunt(dx, -dy);
		//if (trb != null && trb.geefTraceStatus())
			//trb.volgendeMethode("stap(" + Integer.toString((int)Math.rint(dx)) + "," + Integer.toString((int)Math.rint(dy)) + ")");
	}
	public void penAan()
	{	tb.penAan();							
		//if(trb!=null && trb.geefTraceStatus())
		//trb.volgendeMethode("penAan()");
	}
	public void penAan(String kl)
	{	tb.penAan(kl);				
		//if(trb!=null && trb.geefTraceStatus())
		//trb.volgendeMethode("penAan("+kl+")");
	}
	public void penAan(CssColor kl)
	{	tb.penAan(kl);				
		//if(trb!=null && trb.geefTraceStatus())
		//trb.volgendeMethode("penAan()");
	}
	public void penAan(int r, int g, int b)
	{	tb.penAan(r, g, b);	
		//if(trb!=null && trb.geefTraceStatus())
		//trb.volgendeMethode("penAan("+Integer.toString(r)+Integer.toString(g)+Integer.toString(b)+")");
	}
	public void penUit()
	{	tb.penUit();							
		//if(trb!=null && trb.geefTraceStatus())
		//trb.volgendeMethode("penUit()");
	}
	public void vulAan()
	{	tb.vulAan();							
		//if(trb!=null && trb.geefTraceStatus())
		//trb.volgendeMethode("vulAan()");
	}
	public void vulAan(String kl)
	{	tb.vulAan(kl);				
		//if(trb!=null && trb.geefTraceStatus())
		//trb.volgendeMethode("vulAan("+kl+")");
	}
	public void vulAan(CssColor kl)
	{	tb.vulAan(kl);				
		//if(trb!=null && trb.geefTraceStatus())
		//trb.volgendeMethode("vulAan()");
	}
	public void vulAan(int r, int g, int b)
	{	tb.vulAan(r, g, b);	
		//if (trb != null && trb.geefTraceStatus())
		//trb.volgendeMethode("vulAan(" + Integer.toString(r) + Integer.toString(g) + Integer.toString(b) + ")");
	}
	public void vulUit()
	{	tb.vulUit();							
		//if (trb != null && trb.geefTraceStatus())
		//trb.volgendeMethode("vulUit()");
	}
	public Polygon geefVlak()
	{	return tb.geefVlak();
	}
	public Punt geefPunt()
	{	return tb.geefPunt();
	}
	
	
	int maakVeelhoek(int aantalPerZijde, int n, double x, double y, CssColor k)
	{	
		double r = zijde * aantalPerZijde / (2 * Math.sin(Math.PI / n));
		
		vlakdelen[aantalVlakdelen] = new Vlakdeel2(this, aantalPerZijde * n + 1, x, y, k);
		vlakdelen[aantalVlakdelen].aantalHoekpunten = n;
		vlakdelen[aantalVlakdelen].aantalPuntenPerZijde = aantalPerZijde;
		
//		if (((int) Math.round(x)) == stapelX)
//			vlakdelen[aantalVlakdelen].isHeap = true;
		
		volgorde[aantalVlakdelen] = aantalVlakdelen;
		vlakdelen[aantalVlakdelen].hoekpunten[0] = new HoekpuntMoz(0,0);
	
		for (int i = 0; i < n; i++)
		{	
			double h = Math.PI /(n) - 2 * i * Math.PI / n;
			double a = r * Math.cos(h);
			double b = r * Math.sin(h);
			double hv = Math.PI /(n) - 2 * (i + 1) * Math.PI / n;
			double av = r * Math.cos(hv);
			double bv = r * Math.sin(hv);
			for (int j = 0; j < aantalPerZijde; j++)
			{	double an = a + j * (av - a) / aantalPerZijde;
				double bn = b + j * (bv - b) / aantalPerZijde;
				vlakdelen[aantalVlakdelen].hoekpunten[1 + aantalPerZijde * i + j] = new HoekpuntMoz(an,  bn);
			}
		}
		
		vlakdelen[aantalVlakdelen].hoekpunten[aantalPerZijde * n + 1] = vlakdelen[aantalVlakdelen].hoekpunten[1];
		aantalVlakdelen++;
		
		return (aantalVlakdelen - 1);
	}
	
	void maakFractiel(int type, int aantalPerZijde, double x, double y, CssColor k)
	{
		vlakdelen[aantalVlakdelen] = new Vlakdeel2(this, aantalPerZijde * 4 + 1, x, y, k);
		vlakdelen[aantalVlakdelen].aantalHoekpunten = 4;
		vlakdelen[aantalVlakdelen].aantalPuntenPerZijde = aantalPerZijde;
		vlakdelen[aantalVlakdelen].fractielType = type;
		
//		if (((int) Math.round(x)) == stapelX)
//			vlakdelen[aantalVlakdelen].isHeap = true;
		
		volgorde[aantalVlakdelen] = aantalVlakdelen;
		vlakdelen[aantalVlakdelen].hoekpunten[0] = new HoekpuntMoz(0,0);
		
		double hoek = Math.PI / 4;
				
		if (type == 1)
		{	hoek = 6 * Math.PI / 14;
		}
		if (type == 2)
		{	hoek = 5 * Math.PI / 14;
		}
		if (type == 3)
		{	hoek = 4 * Math.PI / 14;
		}
		
		//int fZijde =
		
		double rx = zijde * Math.cos(hoek);
		double ry = zijde * Math.sin(hoek);
		
//System.out.println("rx = " + rx);		
//System.out.println("ry = " + ry);		
		
		for (int i = 0; i < 4; i++)
		{	
			double h = - 2 * i * Math.PI / 4;
			double a = rx * Math.cos(h);
			double b = ry * Math.sin(h);
			double hv = - 2 * (i + 1) * Math.PI / 4;
			double av = rx * Math.cos(hv);
			double bv = ry * Math.sin(hv);
			for (int j = 0; j < aantalPerZijde; j++)
			{	double an = a + j * (av - a) / aantalPerZijde;
				double bn = b + j * (bv - b) / aantalPerZijde;
				vlakdelen[aantalVlakdelen].hoekpunten[1 + aantalPerZijde * i + j] = new HoekpuntMoz(an,  bn);
			}
		}
		
		vlakdelen[aantalVlakdelen].hoekpunten[aantalPerZijde * 4 + 1] = vlakdelen[aantalVlakdelen].hoekpunten[1];
		aantalVlakdelen++;
	}
	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden geimplemeteerd in het leerlingenprogramma
	//-------------------------------------------------------------------------------------------
	public void tekenprogramma()
	{	
		
//System.out.println("tekenprogramma");		
		for (int i = aantalVlakdelen - 1; i > -1; i--)
		{	if (volgorde[i] != -1 && !vlakdelen[volgorde[i]].nieuw)
				tekenVlakdeel(vlakdelen[volgorde[i]]);
		}
		tekenKader();
		for (int i = 0; i < aantalVlakdelen; i++)
		{	if (volgorde[i] != -1 && vlakdelen[volgorde[i]].nieuw)
				tekenVlakdeel(vlakdelen[volgorde[i]]);
		}
		if (actiefVlakdeel != null)
			tekenVlakdeel(actiefVlakdeel);
		
		
	}
	
	void tekenKader()
	{			
		
		
		penUit();

		// pen staat in (390,250)
		// stap(-390, 250);
		
		// pen staat in (cX,cY)
		stap(-cX, cY);
		// pen nu in (0,0)

		penAan();
		vulAan(220, 220, 160);
		
		//stap(779, 0);
		// pen nu in (779,0)
		//stap(0, -499);
		// pen nu in (779,499)
		//stap(-779, 0);
		// pen nu in (0,499)
		//stap(0, 499);
		// pen nu in (0,0)
		//stap(getSize().width - 1, 0);
		stap(breedte - 1, 0);
		
		// pen nu in (width-1,0)
		//stap(0, -(getSize().height - 1));
		stap(0, -(hoogte - 1));
		
		
		// pen nu in (width-1,height-1)
		//stap(-(getSize().width - 1), 0);
		stap(-(breedte - 1), 0);
		
		
		// pen nu in (0,height-1)
		//stap(0, getSize().height - 1);
		stap(0, hoogte - 1);
		
		// pen nu in (0,0)

		//stap(10, -10);
		// pen nu in (10,10)
		//stap(0, -460);
		// pen nu in (10,470)
		//stap(590, 0);
		// pen nu in (600,470)
		//stap(0, 460);
		// pen nu in (600,10)
		//stap(-590, 0);
		// pen nu in (10, 10)
		//stap(-10, 10);
		// pen nu in (0,0)

		stap(veldXMin, -veldYMin);
		// pen nu in (veldXMin,veldYMin)
		stap(0, -(veldYMax - veldYMin));
		// pen nu in (veldXMin,veldYMax)
		stap((veldXMax - veldXMin), 0);
		// pen nu in (veldXMax,veldYMax)
		stap(0, (veldYMax - veldYMin));
		// pen nu in (veldXMax,veldYMin)
		stap(-(veldXMax - veldXMin), 0);
		// pen nu in (veldXMin, veldYMin)
		stap(-veldXMin, veldYMin);
		// pen nu in (0,0)

		vulUit();
		penUit();
		
		stap(1, -1);
		
		// correctie
		vulAan(220, 220, 160);
		stap(9, 0);
		stap(0, -9);
		stap(-9, 0);
		stap(0, 9);
		vulUit();
		
		stap(-1, 1);
		// terug naar (390,250)
		//stap(390, -250);
		
		//terug naar (cX,cY)
		stap(cX, -cY);
		
		penAan();
		
	}
	
	void tekenVlakdeel(Vlakdeel2 vd)
	{	
		int len = vd.hoekpunten.length;
		int num = vd.beginnummer;
		
		penUit(); 
		stap(vd.draaipunt.x, vd.draaipunt.y); 
		links(vd.orientatie);
		vulAan(vd.kleur);
		
		for (int i = 0; i < len; i++)
		{	
			if ((i + 1 + num) % len == 0 || (i + num) % len == 0)
				tb.zetVul(false);
			stap(vd.hoekpunten[(i + 1 + num) % len].x - vd.hoekpunten[(i + num) % len].x , 
				 vd.hoekpunten[(i + 1 + num) % len].y - vd.hoekpunten[(i + num) % len].y);
			vd.hoekpunten[(i + 1 + num) % len].tekenpunt = new Punt(geefPunt());
			tb.zetVul(true);
		}
		vulUit();
		vd.tekenvlak = geefVlak();
		penAan();
		for (int i = 0 ; i < len; i++)
		{	if ((i + 1 + num) % len == 0 || (i + num) % len == 0)
				penUit();
			stap(vd.hoekpunten[(i + 1 + num) % len].x - vd.hoekpunten[(i + num) % len].x , 
				 vd.hoekpunten[(i + 1 + num) % len].y - vd.hoekpunten[(i + num) % len].y);
			penAan();
		}
		rechts(vd.orientatie);
		penUit();
		stap(-vd.draaipunt.x, -vd.draaipunt.y);
		tb.zetStart();
	}	
	
	public void maakStapels()
	{
		int currentBoven = stapelsBoven;
		//maakVeelhoek(1,  3, stapelX,  cY - 30, new Color(255, 0, 0));
		if (triangles)
		{	maakVeelhoek(1,  3, stapelX,  currentBoven - triangleHeight / 2, CssColor.make(255, 0, 0));
			currentBoven -= triangleHeight + triangleVOffset;
		}	
		//maakVeelhoek(1,  4, stapelX,  cY - 90, new Color(255, 255, 0));
		if (squares)
		{	maakVeelhoek(1,  4, stapelX,  currentBoven - squareHeight / 2, CssColor.make(255, 255, 0));
			currentBoven -= squareHeight + squareVOffset;;
		}	
		if (pentagons)
		{	int index = maakVeelhoek(1,  5, stapelX,  currentBoven - pentagonHeight / 2, CssColor.make(255, 0, 255));
			currentBoven -= pentagonHeight + pentagonVOffset;;
//System.out.println("h5 = " + vlakdelen[index].getHeight());
		}
		//maakVeelhoek(1,  6, stapelX,  cY - 170, new Color(0, 255, 0));
		if (hexagons)
		{	maakVeelhoek(1,  6, stapelX,  currentBoven - hexagonHeight / 2, CssColor.make(0, 255, 0));
			currentBoven -= hexagonHeight + hexagonVOffset;;
		}	
		//maakVeelhoek(1,  8, stapelX,  cY - 270, new Color(0, 255, 255));
		if (octagons)
		{	maakVeelhoek(1,  8, stapelX,  currentBoven - octagonHeight / 2, CssColor.make(0, 255, 255));
			currentBoven -= octagonHeight + octagonVOffset;
		}	
		if (dekagons)
		{	int index = maakVeelhoek(1,  10, stapelX,  currentBoven - dekagonHeight / 2, CssColor.make(255, 127, 0));
			currentBoven -= dekagonHeight + dekagonVOffset;;
//System.out.println("h10 = " + vlakdelen[index].getHeight());
		}
		//maakVeelhoek(1, 12, stapelX,  cY - 410, new Color(0, 0, 255));
		if (dodekagons)
		{	maakVeelhoek(1, 12, stapelX,  currentBoven - dodekagonHeight / 2, CssColor.make(0, 0, 255));
		}
		
	}

	public void maakStapel()
	{
		
//System.out.println("maakStapel");		

		if (actiefVlakdeel == null)
		{	
//System.out.println("avd = null");			
			return;
		}

		stapelsBoven = cY - 10; //30;
		
		int pts = actiefVlakdeel.aantalPunten;
//System.out.println("avdpts = " + pts);		
		
		int currentBoven = stapelsBoven;
		//maakVeelhoek(1,  3, stapelX,  cY - 30, new Color(255, 0, 0));
		if (triangles && (pts == 4))
		{	maakVeelhoek(1,  3, stapelX,  currentBoven - triangleHeight / 2, CssColor.make(255, 0, 0));
		}
		if (triangles)
			currentBoven -= triangleHeight + triangleVOffset;
			
		//maakVeelhoek(1,  4, stapelX,  cY - 90, new Color(255, 255, 0));
		if (squares && (pts == 5))
		{	maakVeelhoek(1,  4, stapelX,  currentBoven - squareHeight / 2, CssColor.make(255, 255, 0));
		}
		if (squares)
			currentBoven -= squareHeight + squareVOffset;;
			
		if (pentagons && (pts == 6))
		{	int index = maakVeelhoek(1,  5, stapelX,  currentBoven - pentagonHeight / 2, CssColor.make(255, 0, 255));
		}
		if (pentagons)
			currentBoven -= pentagonHeight + pentagonVOffset;;
//System.out.println("h5 = " + vlakdelen[index].getHeight());
		
		//maakVeelhoek(1,  6, stapelX,  cY - 170, new Color(0, 255, 0));
		if (hexagons && (pts == 7))
		{	maakVeelhoek(1,  6, stapelX,  currentBoven - hexagonHeight / 2, CssColor.make(0, 255, 0));
		}
		if (hexagons)
			currentBoven -= hexagonHeight + hexagonVOffset;;
			
		//maakVeelhoek(1,  8, stapelX,  cY - 270, new Color(0, 255, 255));
		if (octagons && (pts == 9))
		{	maakVeelhoek(1,  8, stapelX,  currentBoven - octagonHeight / 2, CssColor.make(0, 255, 255));
		}
		if (octagons)
			currentBoven -= octagonHeight + octagonVOffset;
			
		if (dekagons && (pts == 11))
		{	int index = maakVeelhoek(1,  10, stapelX,  currentBoven - dekagonHeight / 2, CssColor.make(255, 127, 0));
		}
		if (dekagons)
			currentBoven -= dekagonHeight + dekagonVOffset;;
//System.out.println("h10 = " + vlakdelen[index].getHeight());
		
		//maakVeelhoek(1, 12, stapelX,  cY - 410, new Color(0, 0, 255));
		if (dodekagons && (pts == 13))
		{	maakVeelhoek(1, 12, stapelX,  currentBoven - dodekagonHeight / 2, CssColor.make(0, 0, 255));
		}
		
	}
	
	public void initialiseer()
	{	
//System.out.println("initialiseer");

		//bgcolor = Color.white;
		bgcolor = CssColor.make(255,255,255);
		
		if (fractielen)
			rightWidth = fractielRightWidth;
		else 
			rightWidth = mozarchRightWidth;

		//veldXMax = getSize().width - rightWidth;
		veldXMax = breedte - rightWidth;
		
		veldYMax = hoogte - 10;
		
		String bgString = bgcolor.toString().substring(4, bgcolor.toString().length() - 1);
		String[] kleurenStr = StringUtils.split(bgString,",");

		int bgBlue =  Integer.parseInt(kleurenStr[2]);
		int bgGreen = Integer.parseInt(kleurenStr[1]);
		int bgRed =   Integer.parseInt(kleurenStr[0]);

		achtergrondkleur(bgRed, bgGreen, bgBlue);
		//achtergrondkleur(bgcolor.getRed(), bgcolor.getGreen(), bgcolor.getBlue());
		
		
		maakMuisActieMogelijk();
		
		aantalVlakdelen = 0;
		// aantal wordt nergens gebruikt
		aantal = 10;
		zijde = 40;
		vlakdelen = new Vlakdeel2[200];
		volgorde = new int[200];

		//cX = getSize().width / 2;
		//cY = getSize().height / 2;
		cX = breedte / 2;
		cY = hoogte / 2;

//System.out.println("cX = " + cX);
//System.out.println("cY = " + cY);
		
		//tb.zetStartPunt(cX, cY);
		
		stapelX = breedte - rightWidth / 2 - cX;
		
//System.out.println("stapelX = " + stapelX);

		dummyX = - 2 * cX; 
		startX = - cX + (veldXMax + veldXMin) / 2; 	
		startY = cY - (veldYMax - veldYMin) / 2;
		
//System.out.println("startX = " + startX);
//System.out.println("startY = " + startY);
		
		stapelsBoven = cY - 10; //30;
		

		// originele versie
		if (!fractielen)
		{	
			if (!beginFig)
				maakVeelhoek(beginFigAantalPz, beginFigAantalHp, dummyX, startY, CssColor.make(230, 230, 230));
			else 
				maakVeelhoek(beginFigAantalPz, beginFigAantalHp, startX, startY, CssColor.make(230, 230, 230));


			maakStapels();
/*			
			int currentBoven = stapelsBoven;
			//maakVeelhoek(1,  3, stapelX,  cY - 30, new Color(255, 0, 0));
			int index = maakVeelhoek(1,  3, stapelX,  currentBoven - triangleHeight / 2, new Color(255, 0, 0));
			currentBoven += - triangleHeight - triangleVOffset;
System.out.println("h3 = " + vlakdelen[index].getHeight());			

//maakVeelhoek(1,  4, stapelX,  cY - 90, new Color(255, 255, 0));
			index = maakVeelhoek(1,  4, stapelX,  currentBoven - squareHeight / 2, new Color(255, 255, 0));
			currentBoven += - squareHeight - squareVOffset;
System.out.println("h4 = " + vlakdelen[index].getHeight());

			//maakVeelhoek(1,  6, stapelX,  cY - 170, new Color(0, 255, 0));
			index = maakVeelhoek(1,  6, stapelX,  currentBoven - hexagonHeight / 2, new Color(0, 255, 0));
			currentBoven += - hexagonHeight - hexagonVOffset;;
System.out.println("h6 = " + vlakdelen[index].getHeight());			
			
			//maakVeelhoek(1,  8, stapelX,  cY - 270, new Color(0, 255, 255));
			index = maakVeelhoek(1,  8, stapelX,  currentBoven - octagonHeight / 2, new Color(0, 255, 255));
			currentBoven += - octagonHeight - octagonVOffset;;
System.out.println("h8 = " + vlakdelen[index].getHeight());			
			
			//maakVeelhoek(1, 12, stapelX,  cY - 410, new Color(0, 0, 255));
			index = maakVeelhoek(1, 12, stapelX,  currentBoven - dodekagonHeight / 2, new Color(0, 0, 255));
System.out.println("h12 = " + vlakdelen[index].getHeight());			
*/			
			
			if (beginFig)
				vlakdelen[0].nieuw = false;
		
		}
		else // versie met fractielen
		{	
			// aanpassen
			zijde = 60;

			if (!beginFig)
			{	maakFractiel(beginFractielType, 1, dummyX, startY, CssColor.make(230, 230, 230));
			}
			else 
			{	maakFractiel(beginFractielType, 1, startX, startY, CssColor.make(230, 230, 230));
			}
			
			// stukje 1 heeft ry=60, dus centrum op 10+60=70
			// stukje 2 heeft ry=55, dus centrum op 70+60+10+55=195
			// stukje 3 heeft ry=50, dus centrum op 195+55+10+50=310
			
			maakFractiel(1, 1, stapelX, cY - 70, CssColor.make(255, 0, 0));
			maakFractiel(2, 1, stapelX, cY - 195, CssColor.make(255, 255, 0));
			maakFractiel(3, 1, stapelX, cY - 310, CssColor.make(0, 0, 255));
		
			if (beginFig)
				vlakdelen[0].nieuw = false;

		}
 	} // initialiseer
	
	public void initialiseer2()
	{	
//System.out.println("initialiseer2");		
		
		if (fractielen)
			rightWidth = fractielRightWidth;
		else
			rightWidth = mozarchRightWidth;

		veldXMax = breedte - rightWidth;
		

		veldYMax = hoogte - 20;

		
		
		actiefVlakdeel = null;
		aantalVlakdelen = 0;
		// aantal wordt nergens gebruikt
		aantal = 10;
		zijde = 40;
		
		//vlakdelen = new Vlakdeel2[200];
		//volgorde = new int[200];

		cX = breedte / 2;
		cY = hoogte / 2;
		
		//tb.zetStartPunt(cX, cY);
		
		stapelX = breedte - rightWidth / 2 - cX;
		
		dummyX = - 2 * cX; 
		startX = - cX + (veldXMax + veldXMin) / 2; 	
		startY = cY - (veldYMax - veldYMin) / 2;

//System.out.println("cX2 = " + cX);
//System.out.println("cY2 = " + cY);
//System.out.println("stapelX2 = " + stapelX);
//System.out.println("startX2 = " + startX);
//System.out.println("startY2 = " + startY);
		
		stapelsBoven = cY - 10; //30;
		
		// originele versie
		if (!fractielen)
		{	
			if (!beginFig)
				maakVeelhoek(beginFigAantalPz, beginFigAantalHp, dummyX, startY, CssColor.make(230, 230, 230));
			else 
				maakVeelhoek(beginFigAantalPz, beginFigAantalHp, startX, startY, CssColor.make(230, 230, 230));
			

			maakStapels();
/*			
			int currentBoven = stapelsBoven;
			//maakVeelhoek(1,  3, stapelX,  cY - 30, new Color(255, 0, 0));
			if (triangles)
			{	maakVeelhoek(1,  3, stapelX,  currentBoven - triangleHeight / 2, new Color(255, 0, 0));
				currentBoven -= triangleHeight + triangleVOffset;
			}	
			//maakVeelhoek(1,  4, stapelX,  cY - 90, new Color(255, 255, 0));
			if (squares)
			{	maakVeelhoek(1,  4, stapelX,  currentBoven - squareHeight / 2, new Color(255, 255, 0));
				currentBoven -= squareHeight + squareVOffset;;
			}	
			//maakVeelhoek(1,  6, stapelX,  cY - 170, new Color(0, 255, 0));
			if (hexagons)
			{	maakVeelhoek(1,  6, stapelX,  currentBoven - hexagonHeight / 2, new Color(0, 255, 0));
				currentBoven -= hexagonHeight + hexagonVOffset;;
			}	
			//maakVeelhoek(1,  8, stapelX,  cY - 270, new Color(0, 255, 255));
			if (octagons)
			{	maakVeelhoek(1,  8, stapelX,  currentBoven - octagonHeight / 2, new Color(0, 255, 255));
				currentBoven -= octagonHeight + octagonVOffset;
			}	
			//maakVeelhoek(1, 12, stapelX,  cY - 410, new Color(0, 0, 255));
			if (dodekagons)
			{	maakVeelhoek(1, 12, stapelX,  currentBoven - dodekagonHeight / 2, new Color(0, 0, 255));
			}
*/			
			if (beginFig)
				vlakdelen[0].nieuw = false;
		
		}
		else // versie met fractielen
		{	
			// aanpassen
			zijde = 60;

			if (!beginFig)
			{	maakFractiel(beginFractielType, 1, dummyX, startY, CssColor.make(230, 230, 230));
			}
			else 
			{	maakFractiel(beginFractielType, 1, startX, startY, CssColor.make(230, 230, 230));
			}
			
			// stukje 1 heeft ry=60, dus centrum op 10+60=70
			// stukje 2 heeft ry=55, dus centrum op 70+60+10+55=195
			// stukje 3 heeft ry=50, dus centrum op 195+55+10+50=310
			
			maakFractiel(1, 1, stapelX, cY - 70, CssColor.make(255, 0, 0));
			maakFractiel(2, 1, stapelX, cY - 195, CssColor.make(255, 255, 0));
			maakFractiel(3, 1, stapelX, cY - 310, CssColor.make(0, 0, 255));
		
			if (beginFig)
				vlakdelen[0].nieuw = false;

		}
 	} // initialiseer2
	
	public void muisDrukActie()
	{	
		
//System.out.println("muisDrukActie");

		actiefVlakdeel = null;
		for (int i = 0; i < aantalVlakdelen; i++)
		{	int ii = volgorde[i];
			if (ii > 0 && vlakdelen[ii].tekenvlak.contains(geefDrukx(), geefDruky()))
			{	actiefVlakdeel = vlakdelen[ii];
				for (int j = i; j > 0 ; j--)
				{	volgorde[j] = volgorde[j - 1];
				}
				volgorde[0] = ii;
				actiefVlakdeelNummer = ii;
				vlakdelen[ii].sleeppunt.x = geefDrukx();  
				vlakdelen[ii].sleeppunt.y = geefDruky();
				break;
			}
		}
		
	}
	
	public void muisSleepActie()
	{	
//System.out.println("muisSleepActie");		
		if (actiefVlakdeel == null)
		{	
//System.out.println("aVd = null");			
			return;
		
		}
		
		int dx = geefSleepdx();  
		int dy = -geefSleepdy();
		double d = Math.sqrt(dx * dx + dy * dy);
		if (d < 0.0001)
			return;
		double mpx = actiefVlakdeel.sleeppunt.x - actiefVlakdeel.draaipunt.x - 0.5 * breedte; //getSize().width ; 
		double mpy = actiefVlakdeel.sleeppunt.y + actiefVlakdeel.draaipunt.y - 0.5 * hoogte; //getSize().height;
		double mp = Math.sqrt(mpx * mpx + mpy * mpy);
		
		double tx;
		double ty;
		double dhoek;
		if (mp < 8)
		{	tx = dx;
			ty = dy;
			dhoek = 0;
		}
		else
		{	double cosa = (mpx * dx + mpy * dy) / (mp * d);
			double mppx = actiefVlakdeel.sleeppunt.x + dx - actiefVlakdeel.draaipunt.x - 0.5 * breedte; //getSize().width; 
			double mppy = actiefVlakdeel.sleeppunt.y + dy + actiefVlakdeel.draaipunt.y - 0.5 * hoogte; //getSize().height;
			double mpp = Math.sqrt(mppx * mppx + mppy * mppy);
			if (mpp < 0.0001)
				mpp = 0.0001;
			double t = mpp - mp;
			tx = (t / mpp) * mppx; 
			ty = (t / mpp) * mppy;
			double dh = (Math.acos((mp + d * cosa) / mpp)) * 180 / Math.PI;
			if (mpx * dy - mpy * dx > 0) 
				dhoek = -dh;
			else 
				dhoek = dh;
		}
		
		// stapels aanvullen
		if (actiefVlakdeel.nieuw)
		{	
			
			// originele versie	
			if (!fractielen)
			{
				maakStapel();
/*				
				if (actiefVlakdeel.aantalPunten == 4)
					maakVeelhoek(1, 3, stapelX, cY - 30, new Color(255, 0, 0));
				else if (actiefVlakdeel.aantalPunten == 5)
					maakVeelhoek(1, 4, stapelX, cY - 90, new Color(255, 255, 0));
				else if (actiefVlakdeel.aantalPunten == 7)
					maakVeelhoek(1, 6, stapelX, cY - 170, new Color(0, 255, 0));
				else if (actiefVlakdeel.aantalPunten == 9)
					maakVeelhoek(1, 8, stapelX, cY -270, new Color(0, 255, 255));
				else if (actiefVlakdeel.aantalPunten == 13)
					maakVeelhoek(1, 12, stapelX, cY - 410, new Color(0, 0, 255));
*/					
			}
			else // fractielen versie
			{	
				if (actiefVlakdeel.fractielType == 1)
					maakFractiel(1, 1, stapelX, cY - 70, CssColor.make(255, 0, 0));
				else if (actiefVlakdeel.fractielType == 2)
					maakFractiel(2, 1, stapelX,  cY - 195, CssColor.make(255, 255, 0));
				else if (actiefVlakdeel.fractielType == 3)
					maakFractiel(3, 1, stapelX, cY - 310, CssColor.make(0, 0, 255));
				
			}
			actiefVlakdeel.nieuw = false;
		}
		
		
		actiefVlakdeel.sleeppunt.x = actiefVlakdeel.sleeppunt.x + dx; 
		actiefVlakdeel.sleeppunt.y = actiefVlakdeel.sleeppunt.y + dy;
		
		if (actiefVlakdeel.aantalHoekpuntenVast < 1)
		{	actiefVlakdeel.draaipunt.x = actiefVlakdeel.draaipunt.x + tx;
			actiefVlakdeel.draaipunt.y = actiefVlakdeel.draaipunt.y - ty;
			actiefVlakdeel.orientatie = actiefVlakdeel.orientatie + dhoek;
		}
		
		if (actiefVlakdeel.aantalHoekpuntenVast > 0)
		{	if (actiefVlakdeel.aantalHoekpuntenVast == 1)
			actiefVlakdeel.orientatie = actiefVlakdeel.orientatie + dhoek;
			trekx += tx;
			treky += ty;
			trek = Math.sqrt(trekx * trekx + treky * treky);
			if (actiefVlakdeel.aantalHoekpuntenVast > 1 && trek > 0 || actiefVlakdeel.aantalHoekpuntenVast == 1 && trek > 20)
			{	actiefVlakdeel.draaipunt.x = actiefVlakdeel.draaipunt.x + trekx;
				actiefVlakdeel.draaipunt.y = actiefVlakdeel.draaipunt.y - treky;
				for (int i = 1; i < actiefVlakdeel.aantalPunten; i++)
				{	for (int m = 0; m < actiefVlakdeel.hoekpunten[i].aantalVastgeklikt; m++)
					{	vlakdelen[actiefVlakdeel.hoekpunten[i].vlakdeelnummers[m]].klikLos(
							actiefVlakdeel.hoekpunten[i].hoeknummersVlakdeel[m],actiefVlakdeelNummer,i);
						
					}
				}
				actiefVlakdeel.klikAllesLos();
				trekx = 0 ; 
				treky = 0;
			}
		}
		tekenOpnieuw();
	}
	
	public void muisLosActie()
	{	trekx = 0; 
		treky = 0;
		// geen actief vlakdeel
		if (actiefVlakdeel == null)
			return;
		// centrum van actief vlakdeel is buiten speelveld
		// verwijder dit vlakdeel
		
		// coordinaten zijn t.o.v. (cX,cY)
		int relXMin = cX - veldXMin;
		int relXMax = veldXMax - cX;
		int relYMin = cY - veldYMin;
		int relYMax = veldYMax - cY;
		
System.out.println("relXMin = " + relXMin);
System.out.println("relXMax = " + relXMax);
System.out.println("relYMin = " + relYMin);
System.out.println("relYMax = " + relYMax);

		
		//if (!actiefVlakdeel.nieuw && (actiefVlakdeel.draaipunt.x < -400 || actiefVlakdeel.draaipunt.x > 200 ||
		//                              actiefVlakdeel.draaipunt.y < -250 || actiefVlakdeel.draaipunt.y > 250))
		//if (!actiefVlakdeel.nieuw && (actiefVlakdeel.draaipunt.x < -relXMin || actiefVlakdeel.draaipunt.x > relXMax ||
		//                              actiefVlakdeel.draaipunt.y < -relYMax || actiefVlakdeel.draaipunt.y > relYMin))
		if (!actiefVlakdeel.nieuw && (actiefVlakdeel.sleeppunt.x < -relXMin || actiefVlakdeel.sleeppunt.x > relXMax ||
                    actiefVlakdeel.sleeppunt.y < -relYMax || actiefVlakdeel.sleeppunt.y > relYMin))	
		{	for (int j = actiefVlakdeelNummer; j < aantalVlakdelen - 1; j++)
			{	vlakdelen[j] = vlakdelen[j + 1];
			}
			aantalVlakdelen--;
			for (int j = 0 ; j < aantalVlakdelen; j++)
			{	volgorde[j] = volgorde[j + 1];
				if (volgorde[j] > actiefVlakdeelNummer)
					volgorde[j]--;
			}
			
			//volgorde[0] = -1;
			actiefVlakdeel = null;
			tekenOpnieuw();
			return;
		}
		
		for (int m = 0 ; m < 2; m++)	
		{
			double bx = 0;
			double by = 0;
			double dhoek = 0;
			boolean maakAf = false;
			boolean raak = false;
		
			for (int i = 1; i < actiefVlakdeel.aantalPunten; i++)
			{	for (int j = aantalVlakdelen - 1; j > -1; j--)
				{	int jj = volgorde[j];
					for (int k = 1; jj != -1 && k < vlakdelen[jj].aantalPunten; k++)
					{	
						double ax = vlakdelen[jj].hoekpunten[k].tekenpunt.x - actiefVlakdeel.hoekpunten[i].tekenpunt.x;
						double ay = vlakdelen[jj].hoekpunten[k].tekenpunt.y - actiefVlakdeel.hoekpunten[i].tekenpunt.y;
						
						if ((ax < 15 && ax > -15) && (ay < 15 && ay > -15)&& (actiefVlakdeelNummer != jj))
						{	
							if ((actiefVlakdeel.aantalHoekpuntenVast < 1 || maakAf) && !vlakdelen[jj].nieuw)
							{	maakAf = true;
								actiefVlakdeel.hoekpunten[i].tekenpunt.x += ax;
								actiefVlakdeel.hoekpunten[i].tekenpunt.y += ay;
								actiefVlakdeel.klikVast(i, jj, k);
								vlakdelen[jj].klikVast(k, actiefVlakdeelNummer, i);
								raak = true;
							}
							else
							{	if (!actiefVlakdeel.hoekpunten[i].zitVast(jj, k))
								{	double dax = actiefVlakdeel.hoekpunten[i].tekenpunt.x - 
												 actiefVlakdeel.draaipunt.x - 0.5 * breedte; //getSize().width ;
									double day = -actiefVlakdeel.hoekpunten[i].tekenpunt.y - 
												  actiefVlakdeel.draaipunt.y + 0.5 * hoogte; //getSize().height;
									double dpx = vlakdelen[jj].hoekpunten[k].tekenpunt.x - 
												 actiefVlakdeel.draaipunt.x - 0.5 * breedte; //getSize().width ;
									double dpy = -vlakdelen[jj].hoekpunten[k].tekenpunt.y - 
												 actiefVlakdeel.draaipunt.y + 0.5 * hoogte; //getSize().height;
									double da = Math.sqrt(dax * dax + day * day);
									double dp = Math.sqrt(dpx * dpx + dpy * dpy);
									if ((da-dp < 0.01) && (da-dp > -0.01))
									{	if (Math.abs(dax - dpx) < 0.0001 && Math.abs(day - dpy) < 0.0001)
										{	dhoek = 0;
										}
										else
										{	double dh = (Math.acos((dax * dpx + day * dpy) / (da * dp)) * 180 / Math.PI);
											if (dax * dpy - day * dpx > 0) 
												dhoek = dh;
											else 
												dhoek = -dh;
										}
										actiefVlakdeel.klikVastDraai(i, jj, k);
										vlakdelen[jj].klikVastDraai(k, actiefVlakdeelNummer, i);
									}
								}
							}/**/
						} // voldoende dichtbij 
					} // for
				}
				if(raak)
				{	raak = false;
					break;
				}
			}
			actiefVlakdeel.orientatie += dhoek ;
			if (m == 0)
				tekenOpnieuw();
		}
		actiefVlakdeel = null;
		tekenOpnieuw();
	}
	//public void invoerVarActie(InvoerVariabele iv){}
	
	public void wis()
	{	aantalVlakdelen = 0;
		actiefVlakdeel = null;
        
		if (!fractielen)
		{
		
			if (!beginFig)
				maakVeelhoek(beginFigAantalPz, beginFigAantalHp, dummyX, startY, CssColor.make(230, 230, 230));
			else 
				maakVeelhoek(beginFigAantalPz, beginFigAantalHp, startX, startY, CssColor.make(230, 230, 230));
  

			maakStapels();
/*			
			maakVeelhoek(1,  3, stapelX,  cY - 30, new Color(255, 0, 0));
			maakVeelhoek(1,  4, stapelX,  cY - 90, new Color(255, 255, 0));
			maakVeelhoek(1,  6, stapelX,  cY - 170, new Color(0, 255, 0));
			maakVeelhoek(1,  8, stapelX,  cY - 270, new Color(0, 255, 255));
			maakVeelhoek(1, 12, stapelX,  cY - 410, new Color(0, 0, 255));
*/			
			tekenOpnieuw();
        
			if (beginFig)
				vlakdelen[0].nieuw = false;
		}
		else
		{

			if (!beginFig)
			{	maakFractiel(beginFractielType, beginFigAantalPz, dummyX, startY, CssColor.make(230, 230, 230));
			}
			else 
			{	maakFractiel(beginFractielType, beginFigAantalPz, startX, startY, CssColor.make(230, 230, 230));
			}
			
			maakFractiel(1, 1, stapelX, cY - 70, CssColor.make(255, 0, 0));
			maakFractiel(2, 1, stapelX, cY - 195, CssColor.make(255, 255, 0));
			maakFractiel(3, 1, stapelX, cY - 310, CssColor.make(0, 0, 255));
			tekenOpnieuw();
		
			if (beginFig)
				vlakdelen[0].nieuw = false;
			
			
		}
	}
	
	
//GWT
/*	
	public void actionPerformed(ActionEvent e)
	{	if (e.getSource() == wisKnop)
		{	wis();
		}
	}
*/	
}