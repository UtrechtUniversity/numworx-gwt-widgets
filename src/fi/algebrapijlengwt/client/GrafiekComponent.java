package fi.algebrapijlengwt.client;

import fi.algebrapijlengwt.client.expressies_ap.*;

import java.util.HashMap;
import java.util.Map;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

/**
 * Een AlgebraSchuifComponent die grafieken tekent; de GrafiekComponent kan verbonden worden met de uitgaande pijlen van
 * maximaal 10 verschillende UitvoerSchuifComponenten; de pijl van de UVS naar de GrafiekComponent wordt niet getekend, maar 
 * de achtergrond van de UVS en de inkomende pijlpunt krijgen dezelfde unieke kleur; de getekende grafiek bestaat uit een of meerdere
 * punten (UVS zonder/met tabel aan het einde van een ketting met aan het begin alleen een waarde), of een lijngrafiek resp. lijngrafiek
 * met punten (UVS zonder/met tabel aan het einde van een ketting met aan het begin een variabele); <br>
 * het is mogelijk in- of uit te zoomen in de hele grafiek (x- en y-as tegelijk) of separaat op de x-as of de y-as; er is een reset
 * knop voor de zoom-toestand; slepen op de grafiek verandert het zichtbare deel van het xy-vlak; <br>
 * de afmeting van de GrafiekComponent kan vernaderd worden via resize-gebiedje rechtsonder<br>
 * de inner class GrafiekVeld tekent de grafieken.        
 */

public class GrafiekComponent extends AlgebraSchuifComponent 
{	
	/**
	 * default eenheid	
	 */
  	private int eenheid = 16;
  	/**
  	 * inzoomen op de x-as
  	 */
	private ZoomKnop zoomInX;
  	/**
  	 * uitzoomen op de x-as
  	 */
	private ZoomKnop zoomUitX;
  	/**
  	 * inzoomen op de y-as
  	 */
	private ZoomKnop zoomInY;
  	/**
  	 * uitzoomen op de y-as
  	 */
	private ZoomKnop zoomUitY;
  	/**
  	 * inzoomen beide assen tegelijk
  	 */
	private ZoomKnop zoomIn;
  	/**
  	 * uitzoomen beide assen tegelijk
  	 */
	private ZoomKnop zoomUit;
	/**
	 * zoom reset
	 */
	private ZoomKnop zoomStandaard;
  	
	/**
	 * de Expressies voor de grafieken
	 */
  	private Expressie[] expressies;
	/**
	 * het maximale aantal Expressies
	 */
	private int maxAantalExpressies;
	/**
	 * de default variabele-naam
	 */
	private String varNaam = "qq";
	/**
	 * de formulenaam if any (dit is de tekst uit het label van de UVS)
	 */
	private String formuleNaam;
	/**
	 * x-coordinaat grafiekVeld binnen de GrafiekComponent 
	 */
	private int veldx;
	/**
	 * y-coordinaat grafiekVeld binnen de GrafiekComponent 
	 */
	private int veldy;
	/**
	 * breedte grafiekVeld 
	 */
	private int veldb;
	/**
	 * hoogte grafiekVeld 
	 */
	private int veldh;
	/**
	 * t.b.v. zoomen in de x-richting, zie methode zoom(,,)
	 */
	private int beginwaarde;
	/**
	 * t.b.v. zoomen in de x-richting, zie methode zoom(,,)
	 */
	private int selectnummer;
	/**
	 * t.b.v. zoomen in de x-richting, zie methode zoom(,,)
	 */
	private double beginx;
	/**
	 * t.b.v. zoomen in de y-richting, zie methode zoom(,,)
	 */
	private double beginy;
	/**
	 * t.b.v. zoomen in de x-richting, zie methode zoom(,,)
	 */
	private int eenheidx;
	/**
	 * t.b.v. zoomen in de y-richting, zie methode zoom(,,)
	 */
	private int eenheidy;
	/**
	 * t.b.v. zoomen in de x-richting, zie methode zoom(,,)
	 */
	private double eenheidxD;
	/**
	 * t.b.v. zoomen in de y-richting, zie methode zoom(,,)
	 */
	private double eenheidyD;
	/**
	 * t.b.v. zoomen in de y-richting, zie methode zoom(,,)
	 */
	private double schaalFactorY;
	/**
	 * t.b.v. zoomen in de y-richting, zie methode zoom(,,)
	 */
	private int factorRijNummerY;
	/**
	 * t.b.v. zoomen in de x-richting, zie methode zoom(,,)
	 */
	private double schaalFactorX;
	/**
	 * t.b.v. zoomen in de x-richting, zie methode zoom(,,)
	 */
	private int factorRijNummerX;
	/**
	 * t.b.v. mouse/touch Action in grafiekVeld	 
	 */
	int startxv = 0;
	/**
	 * t.b.v. mouse/touch Action in grafiekVeld	 
	 */
	int startyv = 0;
	/**
	 * t.b.v. mouse/touch Action in de resize-hoek rechtsonder	 
	 */
	int startxrs = 0;
	/**
	 * t.b.v. mouse/touch Action in de resize-hoek rechtsonder	 
	 */
	int startyrs = 0;
	/**
	 * de inner class GrafiekVled
	 */
	private GrafiekVeld gv;
	/** 
	 * de inkomnde pijlen
	 */
	Pijl[] pijlenIn;
	/**
	 * het aantal inkomende pijlen 
	 */
	int aantalPijlenIn;

	/**
	 * zijn de Expressies puntgrafieken?
	 */
	private boolean[] isPuntGrafiek;
	/**
	 * zijn de Expressies meerpuntengrafieken?
	 */
	private boolean[] isMeerPuntenGrafiek;
	/**
	 * zijn de Expressies lijngrafieken?
	 */
	private boolean[] isLijnGrafiek;
	/**
	 * de x-waarde van het punt indien de Expressie een puntgrafiek is
	 */
	private double[] puntXWaarde;
	/**
	 * font voor labels
	 */
	String fontString = "10px, sans-serif";
	/**
	 * muis/touch actie op de resize-hoek rechtsonder?
	 */
	private boolean resize;
	/**
	 * kleuren voor de inkomende pijlen
	 */
	private CssColor[] colors;


	/**
	 * constructor
	 * @param sv werkveld
	 * @param x x-coordinaat
	 * @param y y-coordinaat
	 * @param b breedte
	 * @param h hoogte
	 */
	public GrafiekComponent(AlgebraSchuifVeld sv,int x, int y, int b, int h)
	{	super(sv,x,y,b,h);
		links = false;
		isStapel = false;
		maxAantalExpressies = 10;
		expressies = new Expressie[maxAantalExpressies];
		pijlenIn = new Pijl[maxAantalExpressies];
		aantalPijlenIn = 0;
		isPuntGrafiek = new boolean[10];
		isMeerPuntenGrafiek = new boolean[10];
		puntXWaarde = new double[10];
		isLijnGrafiek = new boolean[10];
		veldx = 40;
		veldy = 45;
		veldb = b-60;
		veldh = h-75;
				
		beginwaarde = 0;
		selectnummer = 999;
		eenheidx = eenheid;
		eenheidy = eenheid;
		eenheidxD = eenheid;
		eenheidyD = eenheid;
		beginx = eenheidx;
		beginy = eenheidy;
		schaalFactorY = 1;
		factorRijNummerY = 99;
		schaalFactorX = 1;
		factorRijNummerX = 99;

		varNaam = "qq";
		formuleNaam = "";
		// grafiekVeld
		gv = new GrafiekVeld(xPos+veldx,yPos+veldy,veldb,veldh);
		// zoomknoppen
		zoomStandaard = new ZoomKnop("standaard",xPos+32,yPos+2,25,25,asv.asvContext2d);
		zoomIn	= new ZoomKnop("zoomin",xPos+57,yPos+2,25,25,asv.asvContext2d);
		zoomUit	= new ZoomKnop("zoomuit",xPos+82,yPos+2,25,25,asv.asvContext2d);
		zoomInX	= new ZoomKnop("zoominx",xPos+107,yPos+2,25,25,asv.asvContext2d);
		zoomUitX= new ZoomKnop("zoomuitx",xPos+132,yPos+2,25,25,asv.asvContext2d);
		zoomInY	= new ZoomKnop("zoominy",xPos+157,yPos+2,25,25,asv.asvContext2d);
		zoomUitY= new ZoomKnop("zoomuity",xPos+182,yPos+2,25,25,asv.asvContext2d);
		// kleuren
		colors = new CssColor[10];
		colors[0] = CssColor.make(0,0,255);
		colors[1] = CssColor.make(0,200,0);
		colors[2] = CssColor.make(255,50,50);
		colors[3] = CssColor.make(00,220,220);
		colors[4] = CssColor.make(220,0,220);
		colors[5] = CssColor.make(200,200,0);
		CssColor black = CssColor.make(0,0,0);
		colors[6] = black;
		colors[7] = black;
		colors[8] = black;
		colors[9] = black;
	}

	/**
	 * zet de afmeting van deze GrafiekComponent
	 */
	public void setSize(int b, int h)
	{	super.setSize(b,h);
		veldb = b-60;
		veldh = h-75;
		gv.setSize(veldb,veldh);
	}
	
	/**
	 * get de State van deze GrafiekComponent
	 */
	public HashMap getState()
	{	int sizeB = 0;
		int sizeH = 0;
		double beginy = 0;
		double schaalFactorY  = 1;
		int factorRijNummerY = 99;
		sizeB = breedte;
		sizeH = hoogte;
		beginy = this.beginy;
		schaalFactorY = this.schaalFactorY;
		factorRijNummerY = this.factorRijNummerY;
		HashMap h = super.getState();
	    h.put("sizeB", new Integer(sizeB));
	    h.put("sizeH", new Integer(sizeH));
	    h.put("beginy", new Double(beginy));
	    h.put("schaalFactorY", new Double(schaalFactorY));
	    h.put("factorRijNummerY", new Integer(factorRijNummerY));
	    return h;
	}

	/**
	 * zet de State van deze GrafiekComponent
	 */
	public void setState(Map<String,Object> map)
    {	ObjectMap h = JSONUtilities.wrapMap(map);
		int sizeB = 0;
		int sizeH = 0;
		double beginy = 0;
		double schaalFactorY  = 1;
		int factorRijNummerY = 99;
		
		if(h.containsKey("sizeB")) 
			sizeB = h.getInt("sizeB");
    	if(h.containsKey("sizeH")) 
    		sizeH = h.getInt("sizeH");
    	if(h.containsKey("beginy")) 
    		beginy = h.getDouble("beginy");
    	if(h.containsKey("schaalFactorY")) 
    		schaalFactorY = h.getDouble("schaalFactorY");
    	if(h.containsKey("factorRijNummerY")) 
    	{	factorRijNummerY = h.getInt("factorRijNummerY");
    	}
		setSize(sizeB,sizeH);
		this.beginy = beginy;
		this.schaalFactorY = schaalFactorY;
		this.factorRijNummerY = factorRijNummerY;
    }
	
	/**
	 * teken de GrafiekComponent m.b.v. Context2d g
	 */
	public void paint(Context2d g)
	{
		g.setFont(fontString);
		//NB links is ruimte voor inkomende Pijlen
		// grijze achtergrond
		g.setFillStyle(CssColor.make(210,210,210));
		g.fillRect(xPos+10,yPos+0,breedte-11,hoogte - 1);
		// zwart uitgelijnd
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.strokeRect(xPos+10,yPos+0,breedte-11,hoogte - 1);
		
		// resize corner rechts beneden
		// witte schuine streepjes
		g.setStrokeStyle(CssColor.make(255,255,255));
		g.beginPath();
		g.moveTo(xPos+breedte-10, yPos+hoogte-2);
		g.lineTo(xPos+breedte-2, yPos+hoogte-10);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+breedte-7, yPos+hoogte-2);
		g.lineTo(xPos+breedte-2, yPos+hoogte-7);
		g.stroke();
		// donkergrijze schuine streepjes
		g.setStrokeStyle(CssColor.make(155,155,155));
		g.beginPath();
		g.moveTo(xPos+breedte-9, yPos+hoogte-2);
		g.lineTo(xPos+breedte-2, yPos+hoogte-9);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+breedte-6, yPos+hoogte-2);
		g.lineTo(xPos+breedte-2, yPos+hoogte-6);
		g.stroke();
		
		// witte achtergrond grafiekveld;
		g.setFillStyle(CssColor.make(255,255,255));
		g.fillRect(xPos+veldx-1,yPos+veldy-1,veldb+1,veldh+1);
		// teken de grafieken in het grafiekveld
		gv.paint(g);
		
		// outline grafiekveld die diepte suggereert
		// grijs, 4 zijden
		g.setStrokeStyle(CssColor.make(155,155,155));
		g.beginPath();
		g.moveTo(xPos+veldx-1,yPos+veldy-1);
		g.lineTo(xPos+veldx+veldb,yPos+veldy-1);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+veldx-1,yPos+veldy-1);
		g.lineTo(xPos+veldx-1,yPos+veldy+veldh);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+veldx-1,yPos+veldy-2);
		g.lineTo(xPos+veldx+veldb,yPos+veldy-2);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+veldx-2,yPos+veldy-1);
		g.lineTo(xPos+veldx-2,yPos+veldy+veldh);
		g.stroke();
		// donkergrijs, links en boven
		g.setStrokeStyle(CssColor.make(180,180,180));
		g.beginPath();
		g.moveTo(xPos+veldx+veldb,yPos+veldy-1);
		g.lineTo(xPos+veldx+veldb,yPos+veldy+veldh);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+veldx-1,yPos+veldy+veldh);
		g.lineTo(xPos+veldx+veldb,yPos+veldy+veldh);
		g.stroke();
		// wit, rechts en onder 
		g.setStrokeStyle(CssColor.make(255,255,255));
		g.beginPath();
		g.moveTo(xPos+veldx+veldb+1,yPos+veldy-1);
		g.lineTo(xPos+veldx+veldb+1,yPos+veldy+veldh);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+veldx-1,yPos+veldy+veldh+1);
		g.lineTo(xPos+veldx+veldb,yPos+veldy+veldh+1);
		g.stroke();

		g.setFillStyle(CssColor.make(0,0,0));
		g.setFont(fontString);
		TextMetrics tm = g.measureText(varNaam);
		int woordbreedte = (int) Math.round(tm.getWidth());
		// geen verborgen variabele?
		boolean b = varNaam.equals("qq") || varNaam.length() > 2 && varNaam.substring(0,2).equals("qq");
		if (!b) 
		{	// variabelenaam rechts bij x-as 
			g.fillText(varNaam, xPos+veldx+veldb+5,yPos+veldy+veldh+5);
		}
		// formulenaam linksboven grafiek
		g.fillText(formuleNaam,xPos+veldx,yPos+veldy-8);

		int imin = -(int)Math.round(beginx/eenheidx); 
		int imax = 1+veldb/eenheidx-(int)Math.round(beginx/eenheidx);
		int jmin = -(int)Math.round(beginy/eenheidy); 
		int jmax = 1+veldh/eenheidy-(int)Math.round(beginy/eenheidy);
		int bx = (int)beginx;
		int by = (int)beginy;
		// opschriften x-as
		for(int i=imin+1 ; i<imax ; i++)
		{	String getal = UF.format0(schaalFactorX*(i),4);
			tm = g.measureText(getal);
			woordbreedte = (int) Math.round(tm.getWidth());
			if(schaalFactorX>0.5 && schaalFactorX<5 && woordbreedte<eenheidx)
			{	g.fillText(getal,xPos+(int)(veldx+beginx+i*eenheidxD-woordbreedte/2),yPos+veldy+veldh+15);
			}
			else if(i%2==0)
			{	g.fillText(getal,xPos+(int)(veldx+beginx+i*eenheidxD-woordbreedte/2),yPos+veldy+veldh+15);
			}
		}
		// opschriften y-as
		for(int j=jmin+1 ; j<jmax ; j++)
		{	String getal = UF.format0(schaalFactorY*(j),4);
			tm = g.measureText(getal);
			woordbreedte = (int) Math.round(tm.getWidth());
			g.fillText(getal,xPos+veldx-5-woordbreedte,yPos+(int)(veldy+veldh+5-(beginy+j*eenheidyD)));
		}
		super.paint(g);
		// teken de zoomknoppen
		zoomStandaard.paint();
		zoomIn.paint();
		zoomUit.paint();
		zoomInX.paint();
		zoomUitX.paint();
		zoomInY.paint();
		zoomUitY.paint();
	}	

	/**
	 * zet Expressie nummer nr, bepaal wat voor soort grafiek dit moet worden
	 * @param nr nummer van de Expressie
	 * @param e de Expressie
	 */
	public void zetExpressie(int nr,Expressie e)
	{	Expressie exp = null;
		// reguliere Expressie
		if(e!=null && e.geefVarNaam()!=null )
		{	exp = e;
			if(varNaam.equals("qq")|| aantalPijlenIn==1)
				varNaam = e.geefVarNaam();
			isPuntGrafiek[nr] = false;
		}
		// Expressie is een getal, bepaal dit
		else if(e!=null && !Double.isNaN(e.geefWaarde().doubleValue()) && pijlenIn[nr]!=null)
		{	AlgebraSchuifComponent asc = pijlenIn[nr].zender;
			int teller = 20;
			puntXWaarde[nr] = asc.geefUitvoer(teller).geefWaarde().doubleValue();
			while(asc.pijlIn1 !=null && teller > 0)
			{	teller--;
				asc = asc.pijlIn1.zender;
				if (!Double.isNaN(asc.geefUitvoer(teller).geefWaarde().doubleValue()))
					puntXWaarde[nr] = asc.geefUitvoer(teller).geefWaarde().doubleValue();
				isPuntGrafiek[nr] = true;
			}
			exp = e;
		}
		else 
		{	exp = null;
			isPuntGrafiek[nr] = false;
		}
		expressies[nr] = exp;
		gv.tekenOpnieuw();
	}
	
	/**
	 * zet de zoomState van de variabele van deze GrafiekComponenet
	 * @param varNaam de naam van de variable
	 * @param zoomState de nieuwe ZoomState
	 */
	public void setZoomState(String varNaam, ZoomState zoomState)
	{	if(varNaam.equals(this.varNaam) && zoomState!=null)
		{	this.factorRijNummerX = zoomState.getFactorRijNummerX();
			this.schaalFactorX = zoomState.getSchaalFactorX();
			this.beginwaarde = zoomState.getBeginwaarde();
			this.beginx = ((double)zoomState.getBeginx()*eenheid)/14+eenheid;
			this.selectnummer = zoomState.getSelectnummer();
			gv.tekenOpnieuw();
		}
	}

	/**
	 * update deze grafiekComponent
	 */
	public void zetVeranderd(int max)
	{	// loop de inkomende pijlen na
		for(int i=0 ; i<aantalPijlenIn ; i++)
		{	Expressie e = pijlenIn[i].zender.geefUitvoer(20);
			Expressie ev = pijlenIn[i].zender.geefVerborgenUitvoer(20);
			zetExpressie(i,e);
			formuleNaam = ((UitvoerSchuifComponent) pijlenIn[i].zender).geefLabelTekst();
			// alleen punten
			if ( (e == null || !Double.isNaN(e.geefWaarde().doubleValue())) && 
				 !(ev instanceof BasisExpressie) && ((UitvoerSchuifComponent) pijlenIn[i].zender).tabelZichtbaar)
			{	zetExpressie(i,ev);
				isMeerPuntenGrafiek[i] = true;
				isLijnGrafiek[i] = false;
				if (e != null && !Double.isNaN(e.geefWaarde().doubleValue())) 
				{	AlgebraSchuifComponent asc = pijlenIn[i].zender;
					int teller = 20;
					puntXWaarde[i] = asc.geefUitvoer(teller).geefWaarde().doubleValue();
					while (asc.pijlIn1 != null && teller > 0)
					{	teller--;
						asc = asc.pijlIn1.zender;
						Double d = asc.geefUitvoer(teller).geefWaarde();
						if(!Double.isNaN(d.doubleValue())) 
							puntXWaarde[i] = d.doubleValue();
						isPuntGrafiek[i] = true;
					}
				}
			}
			// punten op een grafieklijn  
			else if(((UitvoerSchuifComponent)pijlenIn[i].zender).tabelZichtbaar)
			{	isMeerPuntenGrafiek[i] = true;
				isLijnGrafiek[i] = true;
			}
			else // alleen een grafieklijn
			{	isMeerPuntenGrafiek[i] = false;
				isLijnGrafiek[i] = true;
			}
			
		}
		// update de zoomState
		setZoomState(varNaam,asv.zoomStateHolder.getZoomState(varNaam));
        super.zetVeranderd(max);
        asv.tekenOpnieuw();
	}
	
	/**
	 * verbindt Pijl p met de GrafiekComponent onder nummer nr;<br>
	 * t.b.v. setState in het werkveld
	 * @param p Pijl p
	 * @param nr het pijlnummer
	 */
	public void verbind(Pijl p, int nr)
	{	pijlenIn[nr] = p;
		p.zetEind(xPos, yPos+10+nr*15);
		aantalPijlenIn++;
		pijlenIn[nr].setColor(colors[nr]);
	}

	/**
	 * kijk of the punt van pijl p binnen de aansluitrechthoek van de GrafiekComponent
	 * valt; sluit deze aan als p uit een UVS komt, nog niet angesloten is en de pijlenketting
	 * van p dezelfde variabele heeft als de GrafiekComponent
	 */
	public boolean meldAan(Pijl p, int x, int y)
	{	// niet uit UVS
		if (!(p.zender instanceof UitvoerSchuifComponent))
			return false;
		// al aangesloten
		for (int i = 0; i < aantalPijlenIn; i++)
		{	if (pijlenIn[i].zender == p.zender) 
				return false;
		}
		// vind Expressie aan het begin van de pijlenketting
		// waarvan p deel uit maakt
		AlgebraSchuifComponent asc = p.zender;
		int teller = 20;
		Expressie e = asc.geefUitvoer(teller);
		while (asc.pijlIn1 != null && teller > 0)
		{	teller--;
			asc = asc.pijlIn1.zender;
			e = asc.geefUitvoer(teller);
		}
		// verkeerde varnaam in deze Expressie 
		if (e != null && e.geefVarNaam() != null && varNaam != "qq" && !e.geefVarNaam().equals(varNaam)) 
			return false;
		// aansluitrechthoek
		Rectangle ingang = new Rectangle(xPos-10, yPos+0, breedte + 10, hoogte);
		// in aansluitrechthoek: sluit aaan
		if (aantalPijlenIn < 10 && ingang.contains(x, y))
		{	p.zetEind(xPos , yPos + 10 + aantalPijlenIn * 15);
			pijlenIn[aantalPijlenIn] = p;
			aantalPijlenIn++;
			if (e != null && e.geefVarNaam() != null) 
				varNaam = e.geefVarNaam();
			zetVeranderd(20);
			if (p != null) 
				p.setColor(colors[aantalPijlenIn - 1]);
			asv.tekenOpnieuw();
			return true;
		}
		return false;
	}
	
	/**
	 * maak Pijl p los van de GrafiekComponent; verwijder de Expressie
	 * uit de pijlenketting van p
	 */
	public void maakLos(Pijl p)
	{	for(int i=0 ; i<aantalPijlenIn ; i++)
		{	p.setColor(CssColor.make(0,0,0));
			if(p==pijlenIn[i])
			{	CssColor colorRes = colors[i];
				for(int j=i ; j<aantalPijlenIn-1 ; j++)
				{	pijlenIn[j] = pijlenIn[j+1];
					colors[j] = colors[j+1];
					pijlenIn[j].zetEind(xPos , yPos +10+j*15);
					expressies[j] = expressies[j+1];
				}
				colors[aantalPijlenIn-1] = colorRes;
				pijlenIn[aantalPijlenIn-1]=null;
				aantalPijlenIn--;
				break;
			}
		}
		selectnummer = 999;
		// reset
		if(aantalPijlenIn==0)
		{	asv.zoomStateHolder.setBeginy(varNaam, eenheidy);
            varNaam = "qq";
			formuleNaam = "";
			beginy = eenheidy;
		}
	}
	
	/**
	 * verplaats de zoomknoppen
	 * @param dx verplaatsing in x-richting
	 * @param dy verplaatsing in y-richting
	 */
	public void verplaatsKnoppen(int dx, int dy)
	{	zoomInX.translate(dx,dy);
		zoomUitX.translate(dx,dy);
		zoomInY.translate(dx,dy);
		zoomUitY.translate(dx,dy);
		zoomIn.translate(dx,dy);
		zoomUit.translate(dx,dy);
		zoomStandaard.translate(dx,dy);	
	}
	
	/**
	 * mouseDown/touchStart op (eventX,eventY)
	 */
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	if (asv.isDemo)
			return;
		if (asv.alleenInvullen)
				return;
		// mouseDown/touchStart op grafiekveld
		if (new Rectangle(xPos + veldx,yPos + veldy,veldb,veldh).contains(eventX, eventY))	
		{	startxv = eventX;
			startyv = eventY;
		}
		// mouseDown/touchStart op resize-hoek
		else if (eventX > (xPos + breedte - 10) && eventY > (yPos + hoogte - 10))
		{	resize = true;
			startxrs = eventX;
			startyrs = eventY;
		}
		// mouseDown/touchStart op een van de zoomknoppen?
		else if (zoomAction(eventX, eventY))
		{ // actie volgt vanzelf als nodig
		}
		else // start slepen van deze GrafiekComponent 
		{	super.mouseDownTouchStartAction(eventX, eventY);
		}
	}	
	
	/**
	 * mouseMove/touchMove op (eventX,eventY)
	 */
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	if (asv.isDemo)
			return;
		// mouseMove/touchMove op grafiekveld
		// grafiek-clip wordt versleept
		if (new Rectangle(xPos+veldx,yPos+veldy,veldb,veldh).contains(eventX, eventY))
		{	int dx = eventX - startxv;
			int dy = eventY - startyv;
			beginx = beginx + dx;
			beginy = beginy - dy;
			int b = beginwaarde;
			if (beginx > 0) 
				beginwaarde = 1 - (int) Math.round((beginx - eenheidx / 2) / eenheidx);
			else 
				beginwaarde = 1 - (int) Math.round((beginx + eenheidx / 2) / eenheidx);
			selectnummer = selectnummer + b - beginwaarde;
			// synchronizeer 
			asv.zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
            asv.zoomStateHolder.setSelectnummer(varNaam, selectnummer);
            asv.zoomStateHolder.setBeginx(varNaam, ((beginx-eenheid)*14)/eenheid);
            asv.zoomStateHolder.setBeginy(varNaam, beginy);
			asv.zoomStateHolder.setZoomStates(varNaam);
			startxv = eventX; 
			startyv = eventY; 
		}
		// mouseMove/touchMove op resize-hoek
		else if(resize)
		{	int rsdx = eventX - startxrs;
			int rsdy = eventY - startyrs;
			setSize(breedte + rsdx, hoogte + rsdy);
			asv.tekenOpnieuw();
			startxrs = eventX;
			startyrs = eventY;
		}
		else // sleep de GrafiekComponent
		{	super.mouseMoveTouchMoveAction(eventX, eventY);
			for(int i=0 ; i<aantalPijlenIn ; i++)
			{	pijlenIn[i].verplaatsEind(dx,dy);
			}
			verplaatsKnoppen(dx,dy);
			gv.verplaats(dx,dy);
			asv.tekenOpnieuw();
		}
	}
	
	/**
	 * mouseUp/touchEnd op (eventX,eventY)
	 * @param eventX x-coordinaat event
	 * @param eventY y-coordinaar event
	 */
	public void mouseUpTouchEndAction(int eventX, int eventY)
	{	if (asv.isDemo)
			return;
		resize = false;
		// mouseEnd/touchEnd op grafiekveld
		// einde sleep grafiek-clip 
		if (new Rectangle(xPos+veldx,yPos+veldy,veldb,veldh).contains(eventX, eventY))	
		{	beginx = eenheidx*Math.round(beginx/eenheidx);
			beginy = eenheidy*Math.round(beginy/eenheidy);
			// synchronizeer
			if(aantalPijlenIn>0)
			{	asv.zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
				asv.zoomStateHolder.setSchaalFactorX(varNaam, schaalFactorX);
				asv.zoomStateHolder.setFactorRijNummerX(varNaam, factorRijNummerX);
				asv.zoomStateHolder.setSchaalFactorY(varNaam, schaalFactorY);
				asv.zoomStateHolder.setFactorRijNummerY(varNaam, factorRijNummerY);
				asv.zoomStateHolder.setSelectnummer(varNaam, selectnummer);
				asv.zoomStateHolder.setBeginx(varNaam, Math.round(beginx-eenheidx)*14/16);
				asv.zoomStateHolder.setBeginy(varNaam, Math.round(beginy));
				asv.zoomStateHolder.setZoomStates(varNaam);
			}
			gv.tekenOpnieuw();
			asv.tekenOpnieuw();
		}
		else // einde sleep grafiekComponent
		{	super.mouseUpTouchEndAction();
		}
	}
	

	/**
	 * reset de zoom in de grafiek tot de default
	 */
	public void zoomStandaard()
	{	beginx = eenheidx;
		beginy = eenheidy;
		factorRijNummerX = 99;
		factorRijNummerY = 99;
		schaalFactorX = 1;
		schaalFactorY = 1;
		beginwaarde = 0;
		selectnummer = 999;
		// grafiek is niet leeg
		if(aantalPijlenIn>0)
		{	asv.zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
			asv.zoomStateHolder.setSchaalFactorX(varNaam, schaalFactorX);
			asv.zoomStateHolder.setFactorRijNummerX(varNaam, factorRijNummerX);
			asv.zoomStateHolder.setSchaalFactorY(varNaam, schaalFactorY);
			asv.zoomStateHolder.setFactorRijNummerY(varNaam, factorRijNummerY);
			asv.zoomStateHolder.setSelectnummer(varNaam, selectnummer);
			asv.zoomStateHolder.setBeginx(varNaam, (beginx-eenheidx)*14/16);
			asv.zoomStateHolder.setBeginy(varNaam, Math.round(beginy));
			asv.zoomStateHolder.setZoomStates(varNaam);
		}
		asv.tekenOpnieuw();
	}
	
	/**
	 * zoom in deze GrafiekComponent 
	 * @param x zoom in de x-richting?
	 * @param y zoom in de y-richting?
	 * @param in zoom in of zoom uit?
	 */
	public void zoom (boolean x, boolean y, boolean in)
	{	if(x) 
			selectnummer = 999;
        eenheidxD = eenheid;
		eenheidyD = eenheid;
		eenheidx = eenheid;
		eenheidy = eenheid;
		double stapx, stapy;
		double factorx = 1;
		double factory = 1;
		double middenx = eenheidx;
		double middeny = eenheidy;
		if (in && x)
		{	if (factorRijNummerX % 3 == 2)
			{	factorx = 0.4;
			}
			else if(factorRijNummerX % 3 == 0)
			{	factorx = 0.5;
			}
			else 
			{	factorx = 0.5;
			}
		}
		else if (!in && x)
		{	if (factorRijNummerX%3 == 1)
			{	factorx = 2.5;
			}
			else if(factorRijNummerX % 3 == 2)
			{	factorx = 2;
			}
			else 
			{	factorx = 2;
			}
		}
		if(in && y)
		{	if(factorRijNummerY%3==2)
			{	factory =0.4;
			}
			else if(factorRijNummerY%3==0)
			{	factory=0.5;
			}
			else 
			{	factory=0.5;
			}
		}
		else if(!in && y)
		{	if(factorRijNummerY%3==1)
			{	factory =2.5;
			}
			else if(factorRijNummerY%3==2)
			{	factory=2;
			}
			else 
			{	factory=2;
			}
		}
		stapx = Math.pow(factorx,0.1);
		stapy = Math.pow(factory,0.1);
		for (int i = 0; i < 5; i++)
		{	eenheidxD = eenheidxD/stapx;
			eenheidyD = eenheidyD/stapy;
			eenheidx = (int) Math.round(eenheidxD);
			eenheidy = (int) Math.round(eenheidyD);
			beginx =  middenx -(middenx - beginx)/stapx;
			beginy =  middeny -(middeny - beginy)/stapy;
			beginwaarde = 1-(int)Math.round(beginx/eenheidx);
		}
		schaalFactorX *= factorx;
		if (in && x)
			factorRijNummerX--;
		else if (!in && x)
			factorRijNummerX++;
		schaalFactorY *= factory;
		if (in && y)
			factorRijNummerY--;
		if (!in && y)
			factorRijNummerY++;
		eenheidxD = eenheidxD * factorx;
		eenheidyD = eenheidyD * factory;
		for(int i = 0; i < 5; i++)
		{	eenheidxD = eenheidxD/stapx;
			eenheidyD = eenheidyD/stapy;
			eenheidx = (int) Math.round(eenheidxD);
			eenheidy = (int) Math.round(eenheidyD);
			beginx =  middenx -(middenx - beginx)/stapx;
			beginy =  middeny -(middeny - beginy)/stapy;
			beginwaarde = 1-(int)Math.round(beginx/eenheidx);
		}
		beginwaarde = 1-(int)Math.round(beginx/eenheidx);
		if(x)
			selectnummer = 999;
		beginx = eenheidx - eenheidx * beginwaarde;
		// synchronizeer
        if (aantalPijlenIn > 0)
		{	asv.zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
        	asv.zoomStateHolder.setSchaalFactorX(varNaam, schaalFactorX);
        	asv.zoomStateHolder.setFactorRijNummerX(varNaam, factorRijNummerX);
        	asv.zoomStateHolder.setSchaalFactorY(varNaam, schaalFactorY);
        	asv.zoomStateHolder.setFactorRijNummerY(varNaam, factorRijNummerY);
        	asv.zoomStateHolder.setSelectnummer(varNaam, selectnummer);
        	asv.zoomStateHolder.setBeginx(varNaam, (beginx-eenheid)*14/eenheid);
        	asv.zoomStateHolder.setBeginy(varNaam, Math.round(beginy));
        	asv.zoomStateHolder.setZoomStates(varNaam);
		}
		asv.tekenOpnieuw();
	}
		
	
	/**
	 * kijk of een van de zoomKnoppen het punt (eventX,eventY) bevat en voer de corresponderende actie uit  
	 * @param eventX x-coordinaat punt
	 * @param eventY y-coordinaat punt
	 * @return true als een zoomKnop (eventX,eventY), false als niet
	 */
	public boolean zoomAction(int eventX, int eventY)
	{	if (new Rectangle(zoomStandaard.xPos,zoomStandaard.yPos,zoomStandaard.breedte,zoomStandaard.hoogte).contains(eventX, eventY))
		{	zoomStandaard();
			return true;
		}
		else if (new Rectangle(zoomIn.xPos,zoomIn.yPos,zoomIn.breedte,zoomIn.hoogte).contains(eventX, eventY))
		{	// mag je verder inzoomen?
			if (factorRijNummerX>87 && factorRijNummerY>87)
			{	zoom(true,true,true);
			}
			return true;
		}
		else if (new Rectangle(zoomUit.xPos,zoomUit.yPos,zoomUit.breedte,zoomUit.hoogte).contains(eventX, eventY))
		{	// mag je verder uitzoomen?
			if (factorRijNummerX<120 && factorRijNummerY<120)
			{	zoom(true,true,false);
			}
			return true;
		}
		else if (new Rectangle(zoomInX.xPos,zoomInX.yPos,zoomInX.breedte,zoomInX.hoogte).contains(eventX, eventY))
		{	// mag je verder inzoomen op de x-as?
			if (factorRijNummerX>87)
			{	zoom(true,false,true);
			}
			return true;
		}
		else if (new Rectangle(zoomUitX.xPos,zoomUitX.yPos,zoomUitX.breedte,zoomUitX.hoogte).contains(eventX, eventY))
		{	// mag je verder uitzoomen op de x-as?
			if (factorRijNummerX<120)
			{	zoom(true,false,false);
			}
			return true;
		}
		else if (new Rectangle(zoomInY.xPos,zoomInY.yPos,zoomInY.breedte,zoomInY.hoogte).contains(eventX, eventY))
		{	// mag je verder inzoomen op deyx-as?
			if (factorRijNummerY>87)
			{	zoom(false,true,true);
			}
			return true;
		}
		else if (new Rectangle(zoomUitY.xPos,zoomUitY.yPos,zoomUitY.breedte,zoomUitY.hoogte).contains(eventX, eventY))
		{	// mag je verder uitzoomen op de y-as?
			if (factorRijNummerY < 120)
			{	zoom(false, true, false);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * de inner class die de grafieken tekent 
	 */
	class GrafiekVeld
	{	
		/**
		 * de werkveld-x-coordinaat van het GrafiekVeld	
		 */
		int gvX;
		/**
		 * de werkveld-y-coordinaat van het GrafiekVeld	
		 */
		int gvY;
		/** 
		 * de breedte van het GrafiekVled
		 */
		int gvBreedte;
		/** 
		 * de hoogte van het GrafiekVled
		 */
		int gvHoogte;
	
		/**
		 * constructor
		 * @param x globale x-coordinaat
		 * @param y globale y-coordinaat
		 * @param b breedte
		 * @param h hoogte
		 */
		public GrafiekVeld(int x, int y, int b, int h)
		{	gvX = x;
			gvY = y;
			gvBreedte = b;
			gvHoogte = h;
		}
		
		/**
		 * verplaats het GrafiekVeld over (dx,dy) 
		 * @param dx verplaatsing x-richting
		 * @param dy verplaatsing y-richting
		 */
		public void verplaats(int dx, int dy)
		{	gvX += dx;
			gvY += dy;
		}
		
		public void paint()
		{	paint(asv.asvContext2d);
		}
		public void paint(Context2d g)
		{	// achtergrond wit
			g.setFillStyle(CssColor.make(255,255,255));
			g.fillRect(gvX,gvY,gvBreedte,gvHoogte);
			tekenFunctie(g);
		}
		
		/**
		 * zet de afmeting van het GrafiekVled
		 * @param b breedte
		 * @param h hoogte
		 */
		public void setSize(int b, int h)
		{	gvBreedte = b;
			gvHoogte = h;
			tekenOpnieuw();
		}
		
		public void tekenOpnieuw()
		{	paint();
		}

		/**
		 * teken het grafiekrooster, x- and y-as (als nodig) en de grafieken
		 * @param g de Context2d voor tekenen
		 */
		public void	tekenFunctie(Context2d g)
		{	
			// horizontale roosterlijnen
			int imin = - (int) Math.round(beginx / eenheidx); 
			int imax = 1 + gvBreedte / eenheidx - (int) Math.round(beginx / eenheidx);
			int bx = (int) Math.round(beginx);
			for (int i = imin; i < imax; i++)
			{	g.setStrokeStyle(CssColor.make(192,192,192));
				g.beginPath();
				g.moveTo(gvX + bx + i * eenheidxD, gvY + 0);
				g.lineTo(gvX + bx + i * eenheidxD, gvY + gvHoogte);
				g.stroke();
			}
			// vertikale roosterlijnen
			int jmin = -(int)Math.round(beginy/eenheidy); 
			int jmax = 1+ gvHoogte / eenheidy-(int)Math.round(beginy/eenheidy);
			int by = (int)Math.round(beginy);
			for(int j=jmin ; j<jmax ; j++)
			{	g.setStrokeStyle(CssColor.make(192,192,192));
				g.beginPath();
				g.moveTo(gvX + 0,gvY + gvHoogte-(by+j*eenheidyD));
				g.lineTo(gvX + gvBreedte,gvY + gvHoogte-(by+j*eenheidyD));
				g.stroke();
			}	
			g.setStrokeStyle(CssColor.make(0,0,0));
			// y-as in beeld
			if(bx>1 && bx<gvBreedte)
			{	g.beginPath();
				g.moveTo(gvX + bx-1,gvY + 0);
				g.lineTo(gvX + bx-1,gvY + gvHoogte);
				g.stroke();
				g.beginPath();
				g.moveTo(gvX + bx,gvY + 0);
				g.lineTo(gvX + bx,gvY + gvHoogte);
				g.stroke();
			}
			// x-as in beeld
			if(by>0 && by<gvHoogte)
			{	g.beginPath();
				g.moveTo(gvX + 0,gvY + gvHoogte-(by+1));
				g.lineTo(gvX + gvBreedte,gvY + gvHoogte-(by+1));
				g.stroke();
				g.beginPath();
				g.moveTo(gvX + 0,gvY + gvHoogte-(by));
				g.lineTo(gvX + gvBreedte,gvY + gvHoogte-(by));
				g.stroke();
			}
			g.setFillStyle(CssColor.make(0,0,0));
			// oorsprong
			if(bx>1 && bx<gvBreedte && by>0 && by<gvHoogte)
				g.fillText("O",gvX + bx-10,gvY + gvHoogte-by+12);
			
			// teken de grafieken: loop de inkomende pijlen na
			for (int j = 0; j< aantalPijlenIn; j++)
			{	// lijnGrafiek bij een valide Expressie 
				if (isLijnGrafiek[j] && expressies[j] != null && expressies[j].geefVarNaam() != null && 
					varNaam.equals(expressies[j].geefVarNaam()) && !expressies[j].geefVarNaam().equals("qq"))
				{	g.setStrokeStyle(pijlenIn[j].getColor());
					for(int i=0 ; i < gvBreedte ; i++)
					{	double ii = i;
						double d0 = expressies[j].geefW(schaalFactorX*(-beginx)/eenheidxD + schaalFactorX*ii/eenheidxD);
						double d1 = expressies[j].geefW(schaalFactorX*(-beginx)/eenheidxD + schaalFactorX*(ii+1)/eenheidxD);
						if(!Double.isNaN(d0) && !Double.isNaN(d1))
						{	int x0 = i;
							int x1 = i+1;
							double dy0 = Math.round(gvHoogte -(beginy+eenheidyD*d0/schaalFactorY));
							double dy1 = Math.round(gvHoogte -(beginy+eenheidyD*d1/schaalFactorY));
							if(dy0 > 1000)
								dy0 = 1000;
							if(dy0 < -1000)
								dy0 = -1000;
							if(dy1 > 1000)
								dy1 = 1000;
							if(dy1 < -1000)
								dy1 = -1000;
							int y0 = (int)dy0;
							int y1 = (int)dy1;
							if ((y0 >= 0) && (y0 <= gvHoogte) && (y1 >= 0) && (y1 <= gvHoogte))
							{	g.beginPath();
								g.moveTo(gvX + x0,gvY + y0);
								g.lineTo(gvX + x1,gvY + y1);
								g.stroke();
							}
						}
					}
				}
				// puntgrafiek bij een valide Expressie
				else if(isPuntGrafiek[j] && expressies[j]!=null && expressies[j].geefVarNaam()==null && !Double.isNaN(puntXWaarde[j]))
				{	double d = bx+1.0*((puntXWaarde[j])*eenheidx/schaalFactorX);
					int x = (int)d;
					double d0 = expressies[j].geefW((puntXWaarde[j])*schaalFactorX);
					int y = (int)Math.round(gvHoogte -(beginy+eenheidy*d0/schaalFactorY));
					g.setFillStyle(pijlenIn[j].getColor());
					// teken punt
					if ((y >= 0) && (y <= gvHoogte))
					{	g.beginPath();
                    	g.arc(gvX + x,gvY + y,2,0,2 * Math.PI);
               	 		g.fill();
					}
					// teken waarde
               	 	g.setFont(fontString);
               	 	String xString = UF.format0(puntXWaarde[j], 4);
               	 	String yString = UF.format0(d0, 4);
					int woordBreedte = 40;
					TextMetrics tm = g.measureText(xString + yString);
					woordBreedte = (int) Math.round(tm.getWidth());
					if ((y >= 0) && (y <= gvHoogte))
					{	g.setFillStyle(CssColor.make(255,255,225));
						g.fillRect(gvX + x+6,gvY + y-7,woordBreedte+20,15);
						g.setFillStyle(CssColor.make(0,0,0));
						g.fillText("(" + xString + " , " + yString + ")", gvX + x+8,gvY + y+5);
					}
				}
				// meerPuntenGrafiek (8 punten)
				if(isMeerPuntenGrafiek[j] && expressies[j]!=null)
				{	g.setFillStyle(pijlenIn[j].getColor());
					for (int k = 0; k<8; k++) 
					{	double d = bx+1.0*((k+beginwaarde)*eenheidx);
						int x = (int)d;
						if(expressies[j].isWaarde((k+beginwaarde)*schaalFactorX) && k<8 )
						{	double d0 = expressies[j].geefW((k+beginwaarde)*schaalFactorX);
							int y = (int)Math.round(gvHoogte -(beginy+eenheidy*d0/schaalFactorY));
							if ((y >= 0) && (y <= gvHoogte))
							{	g.beginPath();
		                    	g.arc(gvX + x,gvY + y,2,0,2 * Math.PI);
		               	 		g.fill();
							}
						}
				    }
					//puntgrafiek
					if (isPuntGrafiek[j] && !Double.isNaN(puntXWaarde[j]))
					{	double d = bx+1.0*((selectnummer+beginwaarde)*eenheidx);
                        int x = (int)d;
                        d = bx+1.0*((puntXWaarde[j])*eenheidx/schaalFactorX);
						x = (int)d;
						double d0 = expressies[j].geefW((puntXWaarde[j]));
						int y = (int)Math.round(gvHoogte -(beginy+eenheidy*d0/schaalFactorY));
						g.setFillStyle(CssColor.make(0,0,0));
						if ((y >= 0) && (y <= gvHoogte))
						{	g.beginPath();
	                    	g.arc(gvX + x,gvY + y,2,0,2 * Math.PI);
	               	 		g.fill();
						}
	               	 	g.setFont(fontString);
                        String xString = UF.format0(puntXWaarde[j],4);
                        String yString = UF.format0(d0,4);
                        int woordBreedte = 40;
    					TextMetrics tm = g.measureText(xString + yString);
    					woordBreedte = (int) Math.round(tm.getWidth());
    					if ((y >= 0) && (y <= gvHoogte))
    					{   g.setFillStyle(CssColor.make(255,255,225));
                        	g.fillRect(gvX + x+6,gvY + y-7,woordBreedte+20,15);
                        	g.setFillStyle(CssColor.make(0,0,0));
                        	g.fillText("(" + xString + " , " + yString + ")", gvX + x+8,gvY + y+5);
    					}
					}
				}
			} // for
		}
	}
}
