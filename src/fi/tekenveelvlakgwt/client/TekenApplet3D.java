package fi.tekenveelvlakgwt.client;					 

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;

import com.google.gwt.user.client.ui.LayoutPanel;

/**
 * superklasse die het tekenen afhandelt: deze bevat een instantie van klasse TekenBlad3D, die 
 * het Canvas bevat waarop getekend wordt, zodat alle tekenopdrachten doorgestuurd worden
 * naar tekenBlad3D; <br> 
 * de klasse bevat ook een instantie van de klasse MuisBeheerder, die als Mouse/Touch 
 * handler aan het Canvas in de instantie tekenBlad3 wordt gehangen; de Mouse/Touch Events
 * worden afgehandeld in de subklasse TekenVeelvlak. 
 */
public class TekenApplet3D extends LayoutPanel
{
	/**
	 * LayoutPanel voor slider en knoppen, zie subklasse TekenVeelVlak 
	 */
	Regelaar rg;
	/**
	 * bevat het Canvas en de tekenopdrachten, zie klasse TekenBlad3D
	 */
	Tekenblad3D tb;
	/**
	 * klasse die Mouse/Touch Events op het Canvas in tekenBlad3D afhandelt
	 */
	private MuisBeheerder mb;
	
	/**
	 * constructor
	 * @param b breedte
	 * @param h hoogte
	 */
	public TekenApplet3D(int b, int h)
	{	
		tb = new Tekenblad3D(this, b - 130, h);
		add(tb.canvas);
		setWidgetLeftWidth(tb.canvas, 0, Style.Unit.PX, b-130, Style.Unit.PX);
		setWidgetTopHeight(tb.canvas, 0, Style.Unit.PX, h, Style.Unit.PX);

		tb.canvas.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.canvas());
		
		rg = new Regelaar(this);
		add(rg);
		setWidgetLeftWidth(rg, b - 130, Style.Unit.PX, 130, Style.Unit.PX);
		setWidgetTopHeight(rg, 0, Style.Unit.PX, h, Style.Unit.PX);
		
		rg.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.regelaar());
		
		mb = new MuisBeheerder(this);
		tb.canvas.addMouseDownHandler(mb);
		tb.canvas.addMouseUpHandler(mb);
		tb.canvas.addMouseMoveHandler(mb);
		tb.canvas.addTouchStartHandler(mb);
		tb.canvas.addTouchEndHandler(mb);
		tb.canvas.addTouchMoveHandler(mb);
		
	}	

	/**
	 * getter voor de x-translatie van het laatste MouseMove/TouchMove Event
	 * @return dx uit mb
	 */
	public int geefSleepdx()
	{	return mb.geefSleepdx();
	}
	/**
	 * getter voor de y-translatie van het laatste MouseMove/TouchMove Event
	 * @return dy uit mb
	 */
	public int geefSleepdy()
	{	return mb.geefSleepdy();
	}
	/**
	 * getter voor de x-positie van het laatste MouseDown/TouchStart Event
	 * @return eerstex uit mb
	 */
	public int geefDrukx()
	{	return mb.geefDrukx();
	}
	/**
	 * getter voor de y-positie van het laatste MouseDown/TouchStart Event
	 * @return eerstey uit mb
	 */
	public int geefDruky(){	return mb.geefDruky();
	}
	/**
	 * getter voor de x-positie van het laatste MouseMove/TouchMove Event
	 * @return laatstex uit mb
	 */
	public int geefX()
	{	return mb.geefX();
	}
	/**
	 * getter voor de y-positie van het laatste MouseMove/TouchMove Event
	 * @return laatstey uit mb
	 */
	public int geefY()
	{	return mb.geefY();
	}
	/**
	 * repaint het tekenblad
	 */
	public void tekenOpnieuw()
	{	tb.tekenOpnieuw();
	}
	/**
	 * repaint het tekenblad
	 */
	public void tekenErbij()
	{	tb.tekenErbij();
	}

	/**
	 * schaal de tekening in het tekenblad met een factor
	 * @param s schaalfactor
	 */
	public void schaal(double s)
	{	tb.mat.schaal(s);
	}
	/**
	 * zet de achtegrondkleur van het tekenblad
	 * @param kl String met de naam van de kleur
	 */
	public void achtergrondkleur(String kl)
	{	tb.achtergrondkleur(kl);
	}
	/**
	 * zet de achtegrondkleur van het tekenblad
	 * @param r roodfactor (0-255)
	 * @param g groenfactor (0-255)
	 * @param b blauwfactor (0-255)
	 */
	public void achtergrondkleur(int r, int g, int b)
	{	tb.achtergrondkleur(r, g, b);
	}
	
	/**
	 * draai het x-y-z coordinaten systeem dh
	 * graden rond de positieve x-as
	 * @param dh draaihoek
	 */
	public void xdraai(double dh)
	{	tb.mat.xdraai(dh);
	}
	/**
	 * draai het x-y-z coordinaten systeem dh
	 * graden rond de positieve y-as
	 * @param dh draaihoek
	 */
	public void ydraai(double dh)
	{	tb.mat.ydraai(dh);
	}
	/**
	 * draai het x-y-z coordinaten systeem dh
	 * graden rond de positieve z-as
	 * @param dh draaihoek
	 */
	public void zdraai(double dh)
	{	tb.mat.zdraai(dh);
	}
	/**
	 * draai het x-y-z coordinaten systeem dh
	 * graden met de klok mee rond de positieve z-as
	 * @param dh draaihoek
	 */
	public void rechts(double dh)
	{	tb.mat.zdraai(-dh);
	}
	/**
	 * draai het x-y-z coordinaten systeem dh
	 * graden tegen de klok in rond de positieve z-as
	 * @param dh draaihoek
	 */
	public void links(double dh)
	{	tb.mat.zdraai(dh);
	}
	/**
	 * beweeg dy in de richting van de positieve y-as
	 * @param dy y-translatie
	 */
	public void stapy(double dy)
	{	tb.naarVolgendPunt(0,-dy,0);
	}
	/**
	 * beweeg dy in de richting van de positieve y-as
	 * @param dy y-translatie
	 */
	public void vooruit(double dy)
	{	tb.naarVolgendPunt(0,-dy,0);
	}
	/**
	 * beweeg dx in de richting van de positieve x-as
	 * @param dx x-translatie
	 */
	public void stapx(double dx)
	{	tb.naarVolgendPunt(dx,0,0);
	}
	/**
	 * beweeg dz in de richting van de positieve z-as
	 * @param dz z-translatie
	 */
	public void stapz(double dz)
	{	tb.naarVolgendPunt(0,0,-dz);
	}
	/**
	 * stapx(dx)+stapy(dy)+stapz(dz)
	 * @param dx x-translatie
	 * @param dy y-translatie
	 * @param dz z-translatie
	 */
	public void stap(double dx,double dy,double dz)
	{	tb.naarVolgendPunt(dx,-dy,-dz);
	}
	/**
	 * stapx(dx)+stapy(dy)
	 * @param dx x-translatie
	 * @param dy y-translatie
	 */
	public void stap(double dx,double dy)
	{	tb.naarVolgendPunt(dx,-dy,0);
	}
	
	/**
	 * zie klasse TekenBlad3D
	 */
	public void penAan()
	{	tb.penAan();
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param kl kleurnaam
	 */
	public void penAan(String kl)
	{	tb.penAan(kl);
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param r roodfactor
	 * @param g groenfactor
	 * @param b blauwfactor
	 */
	public void penAan(int r, int g, int b)
	{	tb.penAan(r, g, b);
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param n nieuwe actuele lichaam3D-nummer
	 */
	public void penAan(int n)
	{	tb.penAan(n);
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param n nieuwe actuele lichaam3D-nummer 
	 * @param kl kleurnaam
	 */
	public void penAan(int n,String kl)
	{	tb.penAan(n,kl);
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param n nieuwe actuele lichaam3D-nummer
	 * @param r roodfactor
	 * @param g groenfactor
	 * @param b blauwfactor
	 */
	public void penAan(int n,int r, int g, int b)
	{	tb.penAan(n,r, g, b);
	}
	/**
	 * zie klasse TekenBlad3D
	 */
	public void penUit()
	{	tb.penUit();
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param n dummy
	 */
	public void penUit(int n)
	{	tb.penUit(n);
	}
	/**
	 * zie klasse TekenBlad3D
	 */
	public void vulAan()
	{	tb.vulAan();
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param kl kleurnaam
	 */
	public void vulAan(String kl)
	{	tb.vulAan(kl);
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param r roodfactor
	 * @param g groenfactor
	 * @param b blauwfactor
	 */
	public void vulAan(int r, int g, int b)
	{	tb.vulAan(r, g, b);
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param n lichaam3D nummer
	 */
	public void vulAan(int n)
	{	tb.vulAan(n);
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param n lichaam3D nummer
	 * @param kl kleurnaam
	 */
	public void vulAan(int n,String kl)
	{	tb.vulAan(n,kl);
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param n lichaam3D nummer
	 * @param r roodfactor
	 * @param g groenfactor
	 * @param b blauwfactoe
	 */
	public void vulAan(int n,int r, int g, int b){tb.vulAan(n,r, g, b);}
	/**
	 * zie klasse TekenBlad3D
	 * @param kl CssColor
	 */
	public void vulAan(CssColor kl)
	{	tb.vulAan(kl);	
	}
	/**
	 * zie klasse TekenBlad3D
	 */
	public void vulUit()
	{	tb.vulUit();
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param n lichaam3D-nummer
	 */
	public void vulUit(int n)
	{	tb.vulUit(n);
	}
	/**
	 * zie klasse TekenBlad3D
	 * @return Polygon
	 */
	public Polygon geefVlak(){return tb.geefVlak();}
	/**
	 * zie klasse TekenBlad3D
	 * @return laatst getekende punt van lichaam3D 0
	 */
	public Punt geefPunt()
	{	return tb.geefPunt();
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param n lichaam3D-nummer
	 * @return laatst getekende punt van lichaam3D n
	 */
	public Punt geefPunt(int n)
	{	return tb.geefPunt(n);
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param afst afstand oog (op z-as) tot x-y-vlak 
	 */
	public void zetAfstand(double afst)
	{	tb.zetAfstand(afst);
	}
	/**
	 * zie klasse TekenBlad3D
	 * @param b wel/geen schaduweffect
	 */
	public void zetSchaduw(boolean b){tb.zetSchaduw(b);}

	/**'
	 * hergedefinierrd in de subklasse TekenVeelvlak
	 */
	public void tekenprogramma(){}
	/**'
	 * hergedefinierrd in de subklasse TekenVeelvlak
	 */
	public void initialiseer(){}
	/**'
	 * hergedefinierrd in de subklasse TekenVeelvlak
	 */
	public void muisSleepActie(){}
	/**'
	 * hergedefinierrd in de subklasse TekenVeelvlak
	 */
	public void muisDrukActie(){}
	/**
	 * niet gebruikt
	 */
	public void muisKlikActie(){}
	/**
	 * niet gebruikt
	 */
	public void muisLosActie(){}
	
}		
	
    





