package fi.tekenveelvlakgwt.client;

//import java.awt.*;
//import java.awt.event.*;
import java.util.*;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;

import com.google.gwt.event.dom.client.DoubleClickEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;


public class Viewer3d extends LayoutPanel
{
	//TekenVeelvlakInteractiePanel tvip;
	TekenVeelvlakGWT tvGWT;

	Canvas canvas;
	private MuisBeheerder mb;
	private int breedte,hoogte;
	private Punt3D beginpunt,eindpunt,startpunt;
  	public Lichaam3D[] l;
  	//private Image im ;
  	//public Graphics gIm ;
  	public Context2d gIm ;
	public Matrix3D mat;  
	private boolean pen, vul, leeg, schaduw;
	private int lnummer;
  	private CssColor penkleur,vulkleur,achtergrondkleur;
	public boolean bezigMetTekenen;
	public boolean muisAan;
	public boolean klikAan = false;
	private double afstand;
	int aantalVeelvlakken;
	Veelvlak[] vvRij;
	double k, xhoek,yhoek, beginx, beginy;
	double draaiX = 20, draaiY = -30;
	
	Polygon[] p;
	
	int viewerPosition = TekenVeelvlakGWT.MOVEABLE;
	
	boolean restrictRotation = true;
	
	double k50;
	double kMinFac = 60e-2d;
	double kMaxFac = 140e-2d;
	double zoomFac = 1.0;
	
	boolean voorkantPijlZichtbaar = false;
	int voorkantPijlIndex = -1;
	
	boolean vlakkenKleurenOptie = false;
    
    boolean docentModus = false;

    
	PushButton kijkNaButton;
	LayoutPanel kijkNaPanel;
	//JLabel vinkjeLabel;
	//JLabel kruisjeLabel;
	
    
	VaktekPanel vaktek = null;
	
	boolean border = false;
	
	public Viewer3d(int x, int y,int b, int h, TekenVeelvlakGWT tvGWT)
	{	this(new Veelvlak(), x, y, b, h, tvGWT);
	}
	
	public Viewer3d(Veelvlak v, int x, int y,int b, int h, TekenVeelvlakGWT tvGWT)
	{	
		canvas = Canvas.createIfSupported();
		canvas.setWidth(b + "px");
		canvas.setHeight(h + "px");
		canvas.setCoordinateSpaceWidth(b);
		canvas.setCoordinateSpaceHeight(h);
		breedte = b;
		hoogte = h;
		
		add(canvas);
		setWidgetLeftWidth(canvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		setWidgetTopHeight(canvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		
		this.tvGWT = tvGWT;
		
		setBounds(x,y,b,h);
		
		aantalVeelvlakken = 1;
		vvRij = new Veelvlak[5];
		vvRij[0] = v;
		p = new Polygon[v.aantalVlakken+4];
		mb = new MuisBeheerder(this);
		//addMouseListener(mb);
		//addMouseMotionListener(mb);
		canvas.addMouseDownHandler(mb);
		canvas.addMouseUpHandler(mb);
		canvas.addMouseMoveHandler(mb);
		canvas.addTouchStartHandler(mb);
		canvas.addTouchEndHandler(mb);
		canvas.addTouchMoveHandler(mb);

		achtergrondkleur = CssColor.make(255,255,255);
		leeg = false;
		schaduw = true;
		muisAan = true;
		l = new Lichaam3D[5];
		afstand = 1000;
		lnummer=0;
		for(int i=0 ; i<5 ; i++)
		{	l[i] = new Lichaam3D();
			l[i].zetAfstand(afstand);
		}
		mat = new Matrix3D();
		k = 180; //k=200;
		xhoek = 0;
		yhoek = 0;
		beginx = 20;
		beginy = -30;


		kijkNaButton = new PushButton("Kijk Na");
		kijkNaPanel = new LayoutPanel();
		//kijkNaButton = new JButton(TekenVeelvlakOpdr.rb.getString("kijkNaLabel"));
		//kijkNaButton.setBounds(0, 0, 100, 20);
		//kijkNaButton.addActionListener(new KijkNaAL());
		
		 
		
		add(kijkNaPanel);
		kijkNaPanel.add(kijkNaButton);
//if (owner.goedKrulImage != null)				
		kijkNaPanel.add(tvGWT.goedKrulImage);
//else
//System.out.println("gki = null");

//if (owner.foutKruisImage != null)
		kijkNaPanel.add(tvGWT.foutKruisImage);
//else
//System.out.println("fki = null");

		kijkNaButton.addClickHandler(new PushClickHandler());
	
		kijkNaButton.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());
		
		setWidgetLeftWidth(kijkNaPanel, (breedte - 120)/2, Style.Unit.PX, 120, Style.Unit.PX);
		setWidgetTopHeight(kijkNaPanel, hoogte - 25, Style.Unit.PX, 25, Style.Unit.PX);
	
		kijkNaPanel.setWidgetLeftWidth(kijkNaButton, 0, Style.Unit.PX, 80, Style.Unit.PX);
		kijkNaPanel.setWidgetTopHeight(kijkNaButton, 0, Style.Unit.PX, 25, Style.Unit.PX);
	
//if (owner.goedKrulImage != null)
//{	
		kijkNaPanel.setWidgetLeftWidth(tvGWT.goedKrulImage, 80, Style.Unit.PX, 30, Style.Unit.PX);
		kijkNaPanel.setWidgetTopHeight(tvGWT.goedKrulImage, 0, Style.Unit.PX, 25, Style.Unit.PX);
//}			
	
//if (owner.foutKruisImage != null)
//{	
		kijkNaPanel.setWidgetLeftWidth(tvGWT.foutKruisImage, 80, Style.Unit.PX, 30, Style.Unit.PX);
		kijkNaPanel.setWidgetTopHeight(tvGWT.foutKruisImage, 0, Style.Unit.PX, 25, Style.Unit.PX);
//}			
	
		kijkNaPanel.setWidgetVisible(tvGWT.goedKrulImage, false);
		kijkNaPanel.setWidgetVisible(tvGWT.foutKruisImage, false);

		setWidgetVisible(kijkNaPanel, false);
		
	}
	
	
	
	public void initContext2d()
	{
		gIm = canvas.getContext2d();
	}
	
	public void setBounds(int x, int y, int b, int h)
	{
		
//System.out.println("v3d b = " + b + " h = " + h);
		
        k50 = 180 - (350 - b) * 25e-1d / 18;
        double kMin = kMinFac * k50;
		double kMax = kMaxFac * k50;

//System.out.println("v3d kMin = " + UF.format(kMin, 1));
//System.out.println("v3d kMax = " + UF.format(kMax, 1));
		
		
        k = zoomFac * (kMax - kMin) + kMin;
        
		//super.setBounds(x, y, b, h);
		
if (k > 0)		
{	//System.out.println("v3d b = " + b + " h = " + h);		
	//System.out.println("v3d k = " + UF.format(k, 1));
}
		
		
		//im = null;
		//repaint();

		tekenOpnieuw();
	}

	public void zetDocentModus(boolean b)
	{
		docentModus = b;
		
		tekenOpnieuw();
	}
    public void zetVlakkenKleurenOptie(boolean b)
    {	vlakkenKleurenOptie = b;
    }
    
	
	public void zetZoomFac(double zFac)
	{
		zoomFac = zFac;
		//k50 = 180 - (350 - getSize().width) * 25e-1d / 18;
		k50 = 180 - (350 - breedte) * 25e-1d / 18;
        double kMin = kMinFac * k50;
		double kMax = kMaxFac * k50;
		k = zoomFac * (kMax - kMin) + kMin;
		
//System.out.println("v3d zetZoomFac " + UF.format(k, 1));		
	}
	
	public void zetViewerPosition(int vPos)
	{
		muisAan = false;
		viewerPosition = vPos;
		
		if (vPos == TekenVeelvlakGWT.MOVEABLE)
		{	muisAan = true;
			schaduw = true;
			zetBeginHoeken(draaiX,draaiY);
			zetAfstand(1000);
		}
		else if (vPos == TekenVeelvlakGWT.FRONTVIEW)
		{	zetBeginHoeken(0,0);
			zetAfstand(100000);
			schaduw = false;
		}
		else if (vPos == TekenVeelvlakGWT.BACKVIEW)
		{	zetBeginHoeken(0,180);
			zetAfstand(100000);
			schaduw = false;
		}
		else if (vPos == TekenVeelvlakGWT.TOPVIEW)
		{	zetBeginHoeken(90,0);
			zetAfstand(100000);
			schaduw = false;
		}
		else if (vPos == TekenVeelvlakGWT.BOTTOMVIEW)
		{	zetBeginHoeken(-90,0);
			zetAfstand(100000);
			schaduw = false;
		}
		else if (vPos == TekenVeelvlakGWT.LEFTVIEW)
		{	zetBeginHoeken(0,90);
			zetAfstand(100000);
			schaduw = false;
		}
		else if (vPos == TekenVeelvlakGWT.RIGHTVIEW)
		{	zetBeginHoeken(0,-90);
			zetAfstand(100000);
			schaduw = false;
		}
	
		//tekenOpnieuw();
	}

	public String[] getKleuren()
	{
		String[] kleuren = new String[vvRij[0].aantalVlakken];
		for (int vCnt = 0; vCnt < kleuren.length; vCnt++)
		{	kleuren[vCnt] = vvRij[0].vlakken[vCnt].vulkleur;
		}
		return kleuren;
	}
	
	public void zetKleuren(String[] kleuren)
	{
		for (int vCnt = 0; vCnt < kleuren.length; vCnt++)
		{	vvRij[0].vlakken[vCnt].vulkleur = kleuren[vCnt];
		}

		//tekenOpnieuw();
	}
	
	
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
	
	public boolean evalueer(double gevraagdX, double gevraagdY)	
	{	double drx = geefDraaiX();
		double dry =  geefDraaiY();
		double tol = 20;
	
		if ((Math.abs(gevraagdX - drx) < tol) && (Math.abs(gevraagdY - dry) < tol))
			return true;
		else
			return false;
	}

	public void setState(Map map)
	{	
		
System.out.println("viewer setState");
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
		{	lijnenAL = h.getIntegerList("lijnen");
			if (lijnenAL == null)
			{	lijnen = h.getIntArray("lijnen"); 
			}
			else 
			{	lijnen = new int[lijnenAL.size()];
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
		
		//tekenOpnieuw();
		//paint();
		
		
	}
	
	
	public void setKleuren(String[] kleuren)
	{
		for (int i = 0; i < vvRij[0].aantalVlakken; i++)
		{	vvRij[0].vlakken[i].vulkleur = kleuren[i];
		}
		tekenOpnieuw();
		
	}

	public void updateViewerKleuren()
	{
		String[] viewerKleuren = new String[vvRij[0].aantalVlakken];
		for (int i = 0; i < viewerKleuren.length; i++)
		{	viewerKleuren[i] = vvRij[0].vlakken[i].vulkleur;
		}

/*		
		if (tvip.editMode != null)
		{
			if (docentModus)
			{	tvip.docentKleuren = viewerKleuren;
			}
			else if (vaktek == null) 
			{	tvip.viewerKleuren = viewerKleuren;
			}
			else if (vaktek != null)
			{
				vaktek.updateViewerKleuren();
			}
		}
		else
		{
*/
		
			if (vaktek != null)
			{
				vaktek.synchronizeViewerKleuren(this);
			}
		
		//}
	}
	
/*
	public Hashtable getState()
	{	
		if (vvRij[0] == null)
		{	
//System.out.println("vvRij[0] == null");			
			return null;
		}
		
System.out.println("viewer getState");

		double[] hoekpunten = null;
		int[] vlakken = null;
		int[] lijnen = null;
		String[] kleuren = null;
		
		hoekpunten = vvRij[0].hpRij;
		vlakken = vvRij[0].vlRij;
		lijnen = vvRij[0].lnRij;
		kleuren = new String[vvRij[0].aantalVlakken];
		for (int vCnt = 0; vCnt < kleuren.length; vCnt++)
		{	kleuren[vCnt] = vvRij[0].vlakken[vCnt].vulkleur;
		}
		
		
		double draaiX = this.draaiX;
		double draaiY = this.draaiY;
		
		if (viewerPosition == TekenVeelvlakInteractiePanel.MOVEABLE)
		{	draaiX = geefDraaiX();
			draaiY = geefDraaiY();
		}	
		
//System.out.println("hp = " + hoekpunten.length + " vl = " + vlakken.length + " ln = " + lijnen.length);		
		
		Hashtable h = new Hashtable();
		
		h.put("hoekpunten", hoekpunten);
		h.put("vlakken", vlakken);
		h.put("lijnen", lijnen);
		h.put("kleuren", kleuren);
		
		h.put("zoomFac", new Double(zoomFac));
		
		h.put("draaiX", new Double(draaiX));
		h.put("draaiY", new Double(draaiY));
//System.out.println("draaiX = " + UF.format(draaiX,1) + " draaiY = " + UF.format(draaiY,1));
		h.put("muisAan", new Boolean(muisAan));
		
		h.put("viewerPosition", new Integer(viewerPosition));
		
System.out.println("vPos = " + viewerPosition);		
		
		return h;
		
	}
*/	
	
	public void setBackground(CssColor c)
	{	achtergrondkleur = c;
		//super.setBackground(c);
	}
	
	public void zetAfstand(double afst)
	{	afstand = afst;
		for(int i=0 ; i<5 ; i++)
			{	l[i].zetAfstand(afst);
			}
	}
	public void zetSchaduw(boolean s)
	{	schaduw = s;
	}
	public void zetBeginHoeken(double hx, double hy)
	{	
//System.out.println("hx = " + hx + " hy = " + hy);

		beginx = hx;
		beginy = hy;
		if (restrictRotation)
		{	if (beginx > 90) 
				beginx = 90;
			if (beginx < -90)
				beginx = -90;
		}
		xhoek = 0;
		yhoek = 0;
	}
	public double geefDraaiX()
	{	return beginx+xhoek;
	}
	public double geefDraaiY()
	{	return beginy+yhoek;
	}
	public void zetKlikAan(boolean b)
	{	klikAan = b;
	}
	public void zetMuisAan(boolean b)
	{	muisAan = b;
	}
	public void zetVeelvlak(Veelvlak v)
	{	vvRij[0] = v;
	
		tekenOpnieuw();


	}
	public void zetVeelvlak(Veelvlak v, int n)
	{	vvRij[n] = v;
		tekenOpnieuw();
	}
	public void voegVeelvlakToe(Veelvlak v)
	{	vvRij[aantalVeelvlakken] = v;
		aantalVeelvlakken++;
	}

	public void voegVooraanzichtPijlToe(Veelvlak p)
	{	vvRij[aantalVeelvlakken] = p;
		voorkantPijlZichtbaar = true;
		voorkantPijlIndex = aantalVeelvlakken;
		aantalVeelvlakken++;
		//tekenOpnieuw();
	}

	// neem aan pijl is de laatste
	public void verwijderVooraanzichtPijl()
	{	
		if (voorkantPijlZichtbaar)
		{	
			vvRij[aantalVeelvlakken] = null;
			voorkantPijlZichtbaar = false;
			voorkantPijlIndex = -1;
			aantalVeelvlakken--;
			tekenOpnieuw();
		}	
	}
	
	void tekenprogramma()
	{	
//System.out.println("tekenProgramma");		
		mat.initialiseer();
		mat.xdraai(beginx+xhoek);
		mat.ydraai(beginy+yhoek);
		for (int i = 0; i < aantalVeelvlakken; i++)
			tekenVeelvlak(0,vvRij[i]);
	}
	void tekenVeelvlak(int n,Veelvlak vv)
	{	
		if (vv == vvRij[0])
		{	p = new Polygon[vv.aantalVlakken];
		}
		for (int i = 0; i < vv.aantalVlakken; i++)
		{	tekenVlak(n,vv.vlakken[i]);
			if (vv == vvRij[0])
			{	p[i] = geefVlak();
			}
		}
		//for (int i = 0; i < vv.aantalLijnen; i++)
		//{	tekenLijn(vv.lijnen[i]);
		//}
	}
	void tekenLijn(Lijn l)
	{	penUit();
		stap(k*l.hpunt1.x, k*l.hpunt1.y, k*l.hpunt1.z);
		penAan(1,l.kleur);
		stap(k*l.hpunt2.x - k*l.hpunt1.x, k*l.hpunt2.y - k*l.hpunt1.y, k*l.hpunt2.z - k*l.hpunt1.z);
		penUit(1);
		stap(-k*l.hpunt2.x, -k*l.hpunt2.y, -k*l.hpunt2.z);
	}
	void tekenVlak(int n,Vlak v)
	{	
		
//System.out.println("v3d tekenVlak " + v.vulkleur);

		penUit();
		stap(k*v.punten[0].x, k*v.punten[0].y, k*v.punten[0].z);
		
//System.out.println("vvk = " + v.vulkleur);		
		
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
			else
				vulAan(n,"grijs");
		}
		
		if (v.vulkleur != "zwart")
		{
			for(int i=v.aantalHoekpunten-1 ; i>-1 ; i--)
			{	int a=i ; 
				int b=(i+1)%v.aantalHoekpunten;
				stap(k*(v.punten[a].x-v.punten[b].x), k*(v.punten[a].y-v.punten[b].y), k*(v.punten[a].z-v.punten[b].z));
			}
			if (n==2)
				vulUit();
			else if (n==1)
				vulUit(1);
			else
				vulUit(n);
		}
		
		
		
		if (!(v.lijnkleur == "transparant"))
			penAan(v.lijnkleur);
		vulAan(n,v.vulkleur);
		for (int i = 0; i < v.aantalHoekpunten; i++)
		{	int a=i ; 
			int b=(i+1)%v.aantalHoekpunten;
			stap(-k*(v.punten[a].x-v.punten[b].x), -k*(v.punten[a].y-v.punten[b].y), -k*(v.punten[a].z-v.punten[b].z));
		}
		vulUit(n);
		penUit();
		stap(-k*v.punten[0].x, -k*v.punten[0].y, -k*v.punten[0].z);
		
	}
	
	public void paint()
  	{ 	
		
//System.out.println("paint");

		paint(gIm);
  	}
	
	//public void paintComponent(Graphics g)
	public void paint(Context2d g)
  	{ 	bezigMetTekenen = true;
  
/*  	
		if (im == null)
		{	
			
			breedte = getSize().width;
			hoogte = getSize().height;	
			double startschaal = Math.min((double)breedte/500,(double)hoogte/500);
			mat.initialiseer(0,0,0,startschaal);	
			startpunt = new Punt3D(breedte/2,hoogte/2,0);
			for(int i=0 ; i<5 ; i++)
			{	l[i].maakNulpunt(breedte/2,hoogte/2,0);
			}
			im = createImage(breedte,hoogte);
  			gIm = im.getGraphics();
			tekenOpImage(true);
			
		}
    	g.drawImage(im, 0, 0, null);
*/    	
    	
//System.out.println("paint(gIm)");

		double startschaal = Math.min((double) breedte / 400, (double) hoogte / 500);
		mat.initialiseer(0, 0, 0, startschaal);
		startpunt = new Punt3D(breedte / 2, hoogte / 2, 0);
		for (int i = 0; i < 5; i++)
		{
			l[i].maakNulpunt(breedte / 2, hoogte / 2, 0);
		}
		tekenOpImage(true);
    	
		bezigMetTekenen = false;
  	}
	
  	public void tekenOpImage(boolean wis)
  	{ 	
  		
//System.out.println("tekenOpImage");

		if (gIm == null)
		{
//System.out.println("gIm = null");			
			return;
		}
  		
  		beginpunt = new Punt3D(startpunt);
    	eindpunt = new Punt3D(beginpunt);
//OK    	
		//mat.initialiseer();
	  	//gIm.setColor(achtergrondkleur);
    	if(wis)
    	{	//gIm.fillRect(0, 0, breedte, hoogte);
    		gIm.setFillStyle(achtergrondkleur);
			gIm.fillRect(0, 0, breedte, hoogte);
//System.out.println("wis");			
    	}
    	if (border)
    	{
    		gIm.setStrokeStyle(CssColor.make(0,0,0));
			gIm.strokeRect(0, 0, breedte, hoogte);
    	}
    	penAan(0,0,0);
		vul = false;
    	tekenprogramma();
    	
//System.out.println("tekenProgramma klaar");

		for(int i=0 ; i<5 ; i++)
		{	l[i].sorteer();
		}
//		for(int i = 0; i < 200; i++)
//		{	sorteerRij[i] = l[0].sorteerRij[i];
//		}	
		for(int j=0 ; j<5 ; j++)
		{
//System.out.println("v3d l = " + j + " ap = " + l[j].aantalPolygonen);

			for(int i=0 ; i<l[j].aantalPolygonen ; i++)
			{
				
				if (l[j].vlakken[i].normaal.z > 0)
				{	if (schaduw)
					{	double grijsfactor = 0.5*((-l[j].vlakken[i].normaal.x - l[j].vlakken[i].normaal.y + l[j].vlakken[i].normaal.z)/Math.sqrt(3)+1);
						if (grijsfactor < 0)
							grijsfactor = 0;
						if (grijsfactor > 1)
							grijsfactor = 1;
						
						//int roodwaarde = 50+(int)(l[j].vlakken[i].vulkleur.getRed()*grijsfactor*0.75);
						//int groenwaarde = 50+(int)(l[j].vlakken[i].vulkleur.getGreen()*grijsfactor*0.75);
						//int blauwwaarde = 50+(int)(l[j].vlakken[i].vulkleur.getBlue()*grijsfactor*0.75);
						//gIm.setColor(new Color(roodwaarde,groenwaarde,blauwwaarde));
						
					    String vString = l[j].vlakken[i].vulkleur.toString().substring(4, l[j].vlakken[i].vulkleur.toString().length() - 1);
						String[] kleurenStr = StringUtils.split(vString,",");

						int fBlue =  Integer.parseInt(kleurenStr[2]);
						int fGreen = Integer.parseInt(kleurenStr[1]);
						int fRed =   Integer.parseInt(kleurenStr[0]);

						
						int roodwaarde = 50+(int)(fRed*grijsfactor*0.75);
						int groenwaarde = 50+(int)(fGreen*grijsfactor*0.75);
						int blauwwaarde = 50+(int)(fBlue*grijsfactor*0.75);
						//gIm.setColor(new Color(roodwaarde,groenwaarde,blauwwaarde));
						gIm.setFillStyle(CssColor.make(roodwaarde,groenwaarde,blauwwaarde));
						
//System.out.println("v3d schaduw");						
					}
					else
					{	//gIm.setColor(l[j].vlakken[i].vulkleur);
						gIm.setFillStyle(l[j].vlakken[i].vulkleur);
					}
					if (!l[j].vlakken[i].isLeeg)
					{	//gIm.fillPolygon(l[j].vlakken[i].pol);
						gIm.moveTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						gIm.beginPath();
						for (int k = 1; k < l[j].vlakken[i].pol.aantalPunten; k++)
						{	gIm.lineTo(l[j].vlakken[i].pol.puntenX[k], l[j].vlakken[i].pol.puntenY[k]);
						}
						gIm.lineTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						gIm.closePath();
						gIm.fill();

//System.out.println("vlak niet leeg");					
					}
					//gIm.setColor(l[j].vlakken[i].lijnkleur);
					gIm.setStrokeStyle(l[j].vlakken[i].lijnkleur);
					if(!l[j].vlakken[i].isLijn && l[j].vlakken[i].isOmlijnd )
					{	//gIm.setColor(l[j].vlakken[i].lijnkleur);
						gIm.setStrokeStyle(l[j].vlakken[i].lijnkleur);
						//gIm.drawPolygon(l[j].vlakken[i].pol);
						gIm.moveTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						gIm.beginPath();
						for (int k = 1; k < l[j].vlakken[i].pol.aantalPunten; k++)
						{	gIm.lineTo(l[j].vlakken[i].pol.puntenX[k], l[j].vlakken[i].pol.puntenY[k]);
						}
						gIm.lineTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						gIm.closePath();
						gIm.stroke();
					}
					if (l[j].vlakken[i].isLijn)
					{	//int grw = (int)(125-0.7*l[j].vlakken[i].gemz);
						//gIm.setColor(new Color(grw,grw,grw));
						//gIm.setColor(l[j].vlakken[i].lijnkleur);
						gIm.setStrokeStyle(l[j].vlakken[i].lijnkleur);
						//gIm.drawPolygon(l[j].vlakken[i].pol);
						gIm.moveTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						gIm.beginPath();
						for (int k = 1; k < l[j].vlakken[i].pol.aantalPunten; k++)
						{	gIm.lineTo(l[j].vlakken[i].pol.puntenX[k], l[j].vlakken[i].pol.puntenY[k]);
						}
						gIm.lineTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						gIm.closePath();
						gIm.stroke();
						penkleur = CssColor.make(0,0,0);
					}
				}
			}
			l[j] = new Lichaam3D();	
			l[j].zetAfstand(afstand);
			l[j].maakNulpunt(breedte/2,hoogte/2,0);
		}
	}
	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door handlers van het leerlingprogramma
	//-------------------------------------------------------------------------------------------
  	
  	
  	
//beide methoden aleen paint()??  	
	void tekenOpnieuw()
	{	if (gIm == null)
			return;
	
//System.out.println("tekenOpnieuw");

		bezigMetTekenen = true;
		tekenOpImage(true);
		//Graphics g = getGraphics();
		//g.drawImage(im, 0, 0, null);
		
		//paintComponents(g);
		
		bezigMetTekenen = false;
	}
  
  	void tekenErbij()
	{	bezigMetTekenen = true;
		tekenOpImage(false);
		//Graphics g = getGraphics();
		//g.drawImage(im, 0, 0, null);
		
		//paintComponents(g);
		
		bezigMetTekenen = false;
	}

	void stap(double dx,double dy,double dz)
	{	naarVolgendPunt(dx,-dy,-dz);
	}
	void naarVolgendPunt(double dx,double dy, double dz)
	{	eindpunt = mat.geefVolgendPunt(beginpunt,dx,dy,dz);
		if(pen && !vul)
		{	l[lnummer].voegPuntToe(beginpunt);
			l[lnummer].voegPuntToe(eindpunt);
			l[lnummer].voegPolygonToe(penkleur,penkleur,true, false);
		}
		if(vul) 	 
		{	l[lnummer].voegPuntToe(beginpunt);
		}
		beginpunt.x = eindpunt.x;
		beginpunt.y = eindpunt.y;
		beginpunt.z = eindpunt.z;
	}
	
	void tekenPolygon()
	{	l[0].voegPolygonToe(vulkleur, penkleur, pen, leeg);
	}
	void tekenPolygon(int n)
	{	l[n].voegPolygonToe(vulkleur, penkleur, pen, leeg);
	}
	void penAan()
	{	pen = true;
	}
	void penAan(String kl)
	{	pen = true;
		penkleur = maakKleur(kl);
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
	}
	void penAan(int r, int g, int b)
	{	pen = true;
		penkleur = CssColor.make(r,g,b);
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
	}
	void penAan(int n)
	{	pen = true;
		lnummer=n;
	}
	void penAan(int n,String kl)
	{	pen = true;
		penkleur = maakKleur(kl);
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
		lnummer=n;
	}
	void penAan(int n,int r, int g, int b)
	{	pen = true;
		penkleur = CssColor.make(r,g,b);
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
		lnummer=n;
	}
	void penUit()
	{	pen = false;
	}
	void penUit(int n)
	{	pen = false;
		lnummer=0;
	}
	void vulAan()
	{	vul = true;
	}
	void vulAan(String kl)
	{	vul = true;
		if (kl.equals("transparant"))
			leeg = true;
		vulkleur = maakKleur(kl);
	}
	void vulAan(int r, int g, int b)
	{	vul = true;	
		vulkleur = CssColor.make(r,g,b);
	}
	void vulAan(int n)
	{	vul = true;
		lnummer=n;
	}
	void vulAan(int n,String kl)
	{	vul = true;
		lnummer=n;
		if (kl.equals("transparant"))
			leeg = true;
		vulkleur = maakKleur(kl);
	}
	void vulAan(int n,int r, int g, int b)
	{	vul = true;
		lnummer=n;
		vulkleur = CssColor.make(r,g,b);
	}
	void vulAan(CssColor kl)
	{	vul = true;	
		vulkleur = kl;
	}

	void vulUit()
	{	tekenPolygon();
		vul = false;
		lnummer=0;
		leeg = false;
	}
	void vulUit(int n)
	{	tekenPolygon(n);
		vul = false;
		lnummer=0;
		leeg = false;
	}
	void achtergrondkleur(String kl)
	{	achtergrondkleur = maakKleur(kl);
	}
	void achtergrondkleur(int r, int g, int b)
	{	achtergrondkleur = CssColor.make(r,g,b);
	}
	void zetAchtergrond(CssColor c)
	{	achtergrondkleur = c;
	}
	//void schrijf(String s)
	//{	gIm.drawString(s, (int)beginpunt.x, (int)beginpunt.y);
	//}
	//void schrijf(String s, Font f)
	//{	gIm.setFont(f);
	//	gIm.drawString(s, (int)beginpunt.x, (int)beginpunt.y);
	//}
	Punt geefPunt()								// geeft de laatst getekende Punt
	{	double pf = (afstand-beginpunt.z)/afstand;
		double begx = l[0].nulpunt.x + (beginpunt.x-l[0].nulpunt.x)/pf;
		double begy = l[0].nulpunt.y + (beginpunt.y-l[0].nulpunt.y)/pf;
		return new Punt(begx,begy);
	}
	Punt geefPunt(int n)								// geeft de laatst getekende Punt
	{	double pf = (afstand-beginpunt.z)/afstand;
		double begx = l[n].nulpunt.x + (beginpunt.x-l[n].nulpunt.x)/pf;
		double begy = l[n].nulpunt.y + (beginpunt.y-l[n].nulpunt.y)/pf;
		return new Punt(begx,begy);
	}

	Polygon geefVlak()
	{	if(l[0].vlakken[l[0].aantalPolygonen-1].normaal.z > 0)
		return l[0].vlakken[l[0].aantalPolygonen-1].pol;
		else return new Polygon();
	}
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
		
/*		
		if (kl.equals("rood")) 
			return Color.red;
		else if (kl.equals("oranje"))
			return Color.orange;
		else if (kl.equals("groen")) 
			return Color.green;
		else if (kl.equals("blauw")) 
			return Color.blue;
		else if (kl.equals("geel")) 
			return Color.yellow;
		else if (kl.equals("cyaan")) 
			return Color.cyan;
		else if (kl.equals("roze")) 
			return Color.pink;
		else if (kl.equals("zwart")) 
			return Color.black;
		else if (kl.equals("grijs")) 
			return Color.gray;
		else if (kl.equals("lichtgrijs")) 
			return Color.lightGray;
		else if (kl.equals("magenta")) 
			return Color.magenta;
		else if (kl.equals("wit")) 
			return Color.white;
		else return Color.black;
*/				
	}	
	public void animatie(){}

	public void resetColors()
	{
		for (int j = vvRij[0].aantalVlakken - 1; j > -1; j--)
		{	vvRij[0].vlakken[j].vulkleur = "oranje";
		}
		tekenOpnieuw();
	}
	
	public void resetLeerlingColors()
	{
	}
	
	
	public void muisKkActie()
	{	//int extra = 0 ;
		//if (vvRij[1] != null)
		//	extra = vvRij[1].aantalVlakken;						
		//for (int j = vvRij[0].aantalVlakken + extra - 1; j > -1; j--)

//System.out.println("v3d muisKkActie vkoptie = " + vlakkenKleurenOptie);		
		
		for (int j = vvRij[0].aantalVlakken - 1; j > -1; j--)
		{	//int i = sorteerRij[j];
			if (p[j].contains(mb.geefDrukx(),mb.geefDruky()))
			{	
				if (vlakkenKleurenOptie)
				{
					
//NB dit is vlakkenKleuren door de leerling
//de docent heeft alleen de eerste twee nodig 					
					if (docentModus)
					{
						// een oranje vlak dat rood gekleurd wordt
						if (vvRij[0].vlakken[j].vulkleur.equals("oranje"))
						{	vvRij[0].vlakken[j].vulkleur = "rood";
//System.out.println("muisKkActie oranje wordt rood");					
						}
						// een rood vlak dat weer oranje gekleurd wordt
						else if (vvRij[0].vlakken[j].vulkleur.equals("rood"))
						{	vvRij[0].vlakken[j].vulkleur = "oranje";
//System.out.println("muisKkActie ood wordt oranje");					
						}
						
					}
					else // leerling
					{
System.out.println("v3d muisKkActie");
//System.out.println("vk " + j + " " + vvRij[0].vlakken[j].vulkleur);

						// een oranje vlak dat rood gekleurd wordt
						if (vvRij[0].vlakken[j].vulkleur.equals("oranje"))
						{	vvRij[0].vlakken[j].vulkleur = "rood";
//System.out.println("muisKkActie oranje wordt rood");					
						}
						// een rood vlak dat weer oranje gekleurd wordt
						else if (vvRij[0].vlakken[j].vulkleur.equals("rood"))
						{	vvRij[0].vlakken[j].vulkleur = "oranje";
//System.out.println("muisKkActie rood wordt oranje");					
						}
						
						if (vaktek != null)
						{	
							//vaktek.kijkNaPanel.setWidgetVisible(tvGWT.goedKrulImage,false);
							vaktek.kijkNaPanel.setWidgetVisible(vaktek.kijkNaLabelGoed,false);
							//vaktek.kijkNaPanel.setWidgetVisible(tvGWT.foutKruisImage,false);
							vaktek.kijkNaPanel.setWidgetVisible(vaktek.kijkNaLabelFout,false);
						}
						else
						{
							kijkNaPanel.setWidgetVisible(tvGWT.goedKrulImage,false);
		    				kijkNaPanel.setWidgetVisible(tvGWT.foutKruisImage,false);						
							
						}
						//updateViewerKleuren();
					}// leerling
					updateViewerKleuren();
				}//vlakkenkleurenOptie
				tekenOpnieuw();
				return;
			}
		}
	}
	
	public void muisSleepActie()
	{	if (muisAan)
		{	xhoek -= 0.5*mb.geefSleepdy();
			if (restrictRotation)
			{	if (xhoek > 90 - beginx)
					xhoek = 90 - beginx;
				if (xhoek < -90 - beginx)
					xhoek = -90 - beginx;
				
//System.out.println("beginx = " + beginx);
//System.out.println("xhoek = " + xhoek);
			}	
			yhoek += 0.5*mb.geefSleepdx();
			tekenOpnieuw();
		}
	}
	public void muisDrukActie(){}
	public void muisKlikActie(){}
	
	public void muisLosActie()
	{	
		
//System.out.println("muisLos " + klikAan);

		if ((klikAan && (mb.geefDrukx()-mb.geefX())*(mb.geefDrukx()-mb.geefX()) + 
			            (mb.geefDruky()-mb.geefY())*(mb.geefDruky()-mb.geefY()) < 10))
		{	muisKkActie();
		}
		tekenOpnieuw();
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
    			tvGWT.kijkNa();
    			if (tvGWT.correct)
    			{
    				kijkNaPanel.setWidgetVisible(tvGWT.goedKrulImage,true);
    				kijkNaPanel.setWidgetVisible(tvGWT.foutKruisImage,false);
    			}
    			else
    			{
    				kijkNaPanel.setWidgetVisible(tvGWT.goedKrulImage,false);
    				kijkNaPanel.setWidgetVisible(tvGWT.foutKruisImage,true);
    				
    			}

    		}

    	}	
    }
}

	
	
	
    









	

