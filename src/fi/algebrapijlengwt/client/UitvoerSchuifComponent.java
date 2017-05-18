package fi.algebrapijlengwt.client;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import fi.algebrapijlengwt.client.expressies_ap.*;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * een AlgebraSchuifComponent voor de<br> 1) invoer van een getal of variabele, de laatste een letter of een woord, indien de UVS
 * zich aan het begin van een PijlenKetting bevindt (d.w.z. pijlIn == null) of <br> 2) de weergave van de uitvoer van de Pijlenketting
 * die verbonden is met pijlIn van de UVS; <br> in situatie 1) is de achtergrond van de UVS wit, en verschijnt na dubbelklik op
 * deze witte achtergrond een TekstPopup voor de invoer; in situatie 2) is de achtergrond van de UVS grijs; in beide van de gevallen
 * 1) en 2) geeft een long click op de UVS een popupmenu met de keuzes: <br> a) toon/verberg label om de UVS van een label te voorzien; 
 * labeltekst invoeren door dubbelklik op het label; <br> b) toon/verberg tabel om aan de UVS een tabel te hangen; <br> c) verberg/toon ketting
 * om het stuk pijlenketting verbonden met pijlIn te verbergen; in dit geval krijgt de UVS de vorm x pijl formule (een functie) en wordt
 * de tabel  * (als die er is) 2-koloms; <br> indien ingesteld door de docent, kan in de tabellen in- en uitgezoomed worden (synchroon
 * voor alle  * tabellen in dezelfde pijlenketting); een UVS kan een door de docent ingestelde BeginExpressie bevatten; in dat geval
 * kan de UVS  * geen pijlIn hebben; doel: de leerling moet van 3x-4 weer x maken <br>
 * merk op dat de afmetingen van de UVS (als SchuifComponent) inclusief het label (als dit er is) en inclusief de tabel (als die er is)
 * zijn.  
 */

public class UitvoerSchuifComponent extends AlgebraSchuifComponent //implements ActionListener, FocusListener
{	
	/**
	 * PopupMenu voor invoer van het getal/de variabele in deze UVSC, zie klasse TekstPopup
	 */
	TekstPopup tf;
	/**
	 * PopupMenu voor invoer van de tekst van het label, zie klasse TekstPopup
	 */
	TekstPopup label;
	/**
	 * het basis LayoutPanel van AlgebraPijlenGWT, nodig voor de correcte plaatsing van de PopupMenus
	 */
	LayoutPanel inputOwner; 
	/**
	 * het getal/de variabele in deze UVSC als String
	 */
	String tfString = "";
	/**
	 * de Expressie van deze UVS (if any)
	 */
	private Expressie expressie;
	/**
	 * de verborgen Expressie van deze UVS, geinitialiseerd als BasisExpressie(defaultVarnaam)
	 */
	private Expressie verborgenExpressie;
	/**
	 * de BasisExpressie van deze UVS in geval van invoer
	 */
	private BasisExpressie beginw;
	/**
	 * een String met de waarde van de UVS indien deze een getal bevat
	 */
	private String waardeString;
	/**
	 * dummy, geinitialiseerd als true
	 */
	private boolean toonWaarde;
	/**
	 * is het label van deze UVS zichtbaar
	 */
	private boolean labelZichtbaar;
	/**
	 * de teksts van het label van deze UVS
	 */
	String labelTekst = "";

	/**
	 * is de tabel zichtbaar?, zie toonTabel()
	 */
	boolean tabelZichtbaar;
	/**
	 * staat de tabel aan?, zie zetTabelAan() 
	 */
	boolean tabelAan;
	/**
	 * mag er in de tabel in- en uitgezoomd worden?
	 */
	boolean zoomInTabel;
	/**
	 * de TabelComponent van deze UVS
	 */
	TabelComponent tabel;
	/**
	 * correctie voor aanwezigheid tabel, zie zetMaat()
	 */
	private int tabelCorr;

	/**
	 * font om te tekenen
	 */
	String fontString = "12px sans-serif";
	/**
	 * font size
	 */
	int fontSize = 12;
	/**
	 * het PopupMenu, dit bevat als widget een MenuBar (niet zichtbaar) waaraan menuItems worden
	 * toegevoegd
	 */
	PopupPanel menuPopup;
	/**
	 * de MenuBar, zie PopupMenu
	 */
	MenuBar menuBar;
	/**
	 * MenuItem voor toon/verberg label, zie PopupMenu
	 */
	MenuItem labelItem;
	/**
	 * MenuItem voor toon/verberg tabel, zie PopupMenu
	 */
	MenuItem tabelItem;
	/**
	 * MenuItem voor erberg/toon ketting, zie PopupMenu
	 */
	MenuItem kettingItem;
	/**
	 * is de ketting (if any) verbonden met pijlIn zichtbaar? 
	 */
	public boolean kettingZichtbaar = true;
	/**
	 * knop voor zoom in in tabel (instelbaar)
	 */
	ZoomKnop zoomInKnop;
	/**
	 * knop voor zoom uit in tabel (instelbaar)
	 */
	ZoomKnop zoomUitKnop;
	/**
	 * t.b.v. muisDown/touchStart Events: werd het label aangeklikt? 
	 */
	boolean labelPressed = false;
	/**
	 * t.b.v. muisDown/touchStart Events: werd de zoomInKnop aangeklikt? 
	 */
	boolean zoomInPressed = false;
	/**
	 * t.b.v. muisDown/touchStart Events: werd de zoomUitKnop aangeklikt? 
	 */
	boolean zoomUitPressed = false;
	/**
	 * t.b.v. zoomen in de tabel, zie methode zoomInTabelAction()	
	 */
	private double schaalFactorX=1;
	/**
	 * t.b.v. zoomen in de tabel, zie methode zoomInTabelAction()	
	 */
	private int factorRijNummerX=99;
	/**
	 * t.b.v. zoomen in de tabel, zie methode zoomInTabelAction()	
	 */
	private int beginwaarde;
	/**
	 * t.b.v. zoomen in de tabel, zie methode zoomInTabelAction()	
	 */
	private double beginx;
	/**
	 * de geselcteerde waarde in de tabel (if any)
	 */
	private int selectnummer;
	/**
	 * de variabele naam als de UVS geen inputExpressie heeft
	 */
	String defaultVarnaam = "qq"+1000*Math.random();
	/**
	 * achtergrondkleur indiend de UVS verbonden is met de GrafiekComponent
	 */
	private CssColor vakKleur;
	/**
	 * lichtere versie van vakKleur
	 */
	private CssColor vakKleurSoft;
	/**
	 * bevat de UVS een beginExpressie?
	 */
	boolean isBeginExpressie = false;
	
	/**
	 * constructor
	 * @param asv werkveld
	 * @param x x-positie
	 * @param y y-positie
	 * @param b breedte
	 * @param h hoogte
	 */
	public UitvoerSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(asv,x,y,b,h);
		toonWaarde = true;
		labelZichtbaar = false;
		tabelZichtbaar = false;
		zoomInTabel = true;
		waardeString = "";
		inputOwner = asv.owner.canvasPanel;
		// popupmenu
		menuBar = new MenuBar(true);
		labelItem = new MenuItem(AlgebraPijlenGWT.rb.toonLabel(), new MenuCommand("label"));
		tabelItem = new MenuItem(AlgebraPijlenGWT.rb.toonTabel(), new MenuCommand("tabel"));
		kettingItem = new MenuItem(AlgebraPijlenGWT.rb.verbergKetting(), new MenuCommand("ketting"));
		menuBar.addItem(labelItem);
		menuBar.addItem(tabelItem);
		menuBar.addItem(kettingItem);
		verborgenExpressie = new BasisExpressie(defaultVarnaam);
		zoomInKnop	= new ZoomKnop("zoomintabel", xPos + 10, yPos + 50, 10, 10, asv.asvContext2d);
		zoomUitKnop	= new ZoomKnop("zoomuittabel", xPos + 10, yPos + 100, 10, 10, asv.asvContext2d);
	}

	/**
	 * toon het PopupMenu met de juiste menu-items, d.w.z. stop de menuBar in een popupPanel
	 */
	public void showPopupMenu()
	{	int popupX = xPos + breedte + inputOwner.getAbsoluteLeft();
		int popupY = yPos + inputOwner.getAbsoluteTop();
		menuPopup = new PopupPanel(true);
		if (labelZichtbaar)
		{	labelItem.setText(AlgebraPijlenGWT.rb.verbergLabel());
		}
		else
		{	labelItem.setText(AlgebraPijlenGWT.rb.toonLabel());
		}
		if (tabelZichtbaar)
		{	tabelItem.setText(AlgebraPijlenGWT.rb.verbergTabel());
		}
		else
		{	tabelItem.setText(AlgebraPijlenGWT.rb.toonTabel());
		}
		if (kettingZichtbaar)
		{	kettingItem.setText(AlgebraPijlenGWT.rb.verbergKetting());
		}
		else
		{	kettingItem.setText(AlgebraPijlenGWT.rb.toonKetting());
		}
		menuPopup.setWidget(menuBar);
		menuPopup.setPopupPosition(popupX, popupY);
		menuPopup.show();
	}	

	/**
	 * toon de Popup voor invoer, zie klasse TekstPopup 
	 */
	public void showTekstPopup()
	{	int popupX = xPos + inputOwner.getAbsoluteLeft();
		int popupY = yPos + hoogte + inputOwner.getAbsoluteTop();
		if (tabel != null)
			popupY -= 152;
		// als er nog een tekstPopup open is, handel die af
		if ((tf != null) && tf.isVisible())
		{	zetInvulWaarde();
		}
		// maak een nieuwe
		tf = new TekstPopup(this, false);
		
		if (!"".equals(tfString))
			tf.setText(tfString);
		else if (expressie != null) 
		{
			// format voor grote getallen met wetenschappelijke notatie zoals 1234567^2 = 1524155677489...
			String formatted = NumberFormat.getFormat("0.###").format(expressie.geefWaarde());
			formatted = formatted.replace(',', '.'); // dit moet, anders gaat BasisExpressie.geefWaarde() met Double.valueOf(basisString) mis
			tf.setText(formatted);
		}
		else
		{
			tf.setText("");
		}
		tf.resize();
		
		tf.setPopupPosition(popupX, popupY);
		tf.show();
		tf.setFocus(true);
		tf.setSelected();
	}

	/**
	 * toon de Popup voor label-invoer, zie klasse TekstPopup 
	 */
	public void showLabelPopup()
	{	int popupX = xPos + inputOwner.getAbsoluteLeft();
		int popupY = yPos - 40 + inputOwner.getAbsoluteTop();
		// als er nog een labelPopup open is, handel die af
		if ((label != null) && label.isVisible())
		{	zetLabelTekst();
		}
		// maak een nieuwe
		label = new TekstPopup(this, true);
		label.setText(labelTekst);
		label.setWidth("35px");
		label.setHeight("20px");
		label.resize();
		label.setPopupPosition(popupX, popupY);
		label.show();
		label.setFocus(true);
		label.setSelected();
	}

	/** 
	 * save de State van deze UVS in een HashMap
	 */
	public HashMap<String,Object> getState()
	{	
		if (tf != null)
			tf.hide();
		
		String basisExp  = null;
		String defaultVarnaam = null;
		boolean tabelAan = false;
		boolean labelZichtbaar = false;
        boolean kettingZichtbaar = true;
		String labelTekst = null;
		if (beginw != null)
		{
			if ((tf != null) && !tf.isForLabel)
			{
				// bij popup, neem de waarde uit popup
				basisExp = tf.getText();
			}
			else
			{
				basisExp = this.beginw.basisString;
			}
		}
		else 
			basisExp = "";
		defaultVarnaam = this.defaultVarnaam;
		tabelAan = this.tabelAan;
		labelZichtbaar = this.labelZichtbaar;
        kettingZichtbaar = this.kettingZichtbaar;
        labelTekst = this.labelTekst;
		HashMap<String,Object> h = super.getState();
	    h.put("basisExp", basisExp);
	    h.put("defaultVarnaam", defaultVarnaam);
	    h.put("tabelAan", new Boolean(tabelAan));
	    h.put("labelZichtbaar", new Boolean(labelZichtbaar));
        h.put("kettingZichtbaar", new Boolean(kettingZichtbaar));
        h.put("labelTekst", labelTekst);
        h.put("zoomInTabel", new Boolean(zoomInTabel));
        h.put("isBeginExpressie", new Boolean(isBeginExpressie));
        if (isBeginExpressie)
        	h.put("beginExpString", expressie.toString());
	    return h;
	}

	/**
	 * set de State van deze UVS m.b.v. een Map
	 */
    public void setState(Map<String,Object> map)
    {	ObjectMap h = JSONUtilities.wrapMap(map);
    	String basisExp  = null;
   		String defaultVarnaam = null;
        boolean tabelAan = false;
        boolean labelZichtbaar = false;
        boolean kettingZichtbaar = true;
        String labelTekst = null;
        boolean zoomInTabel = true;
        boolean isBeginExpressie = false;
        String beginExpString = "";
    	if (h.containsKey("basisExp")) 
    		basisExp = h.getString("basisExp");
    	if (h.containsKey("defaultVarnaam")) 
    		defaultVarnaam = h.getString("defaultVarnaam");
        if (h.containsKey("tabelAan")) 
        	tabelAan = h.getBoolean("tabelAan");		
        if (h.containsKey("labelZichtbaar")) 
        	labelZichtbaar = h.getBoolean("labelZichtbaar");
        if (h.containsKey("kettingZichtbaar")) 
        	kettingZichtbaar = h.getBoolean("kettingZichtbaar");
        if (h.containsKey("labelTekst")) 
        	labelTekst = h.getString("labelTekst");
        if (h.containsKey("zoomInTabel")) 
        	zoomInTabel = h.getBoolean("zoomInTabel");
		if (!basisExp.equals(""))
			beginw = new BasisExpressie(basisExp);
		if (defaultVarnaam != null) 
		{	this.defaultVarnaam = defaultVarnaam;
			verborgenExpressie = new BasisExpressie(defaultVarnaam);
		}
		zetTabelAan(tabelAan);
		zetLabel(labelZichtbaar);
		this.labelTekst = labelTekst;
        if (!kettingZichtbaar)
        	zetKettingZichtbaarHier(kettingZichtbaar);
        zetZoomInTabel(zoomInTabel);
        if (h.containsKey("isBeginExpressie")) 
        	isBeginExpressie = h.getBoolean("isBeginExpressie");
        this.isBeginExpressie = isBeginExpressie;
        if (h.containsKey("beginExpString"))
        	beginExpString = h.getString("beginExpString");
        if (isBeginExpressie && !beginExpString.equals(""))
        {  	beginExpString = "$f" + beginExpString + "@";
        	Expressie beginExp = FormuleParser_ap.geefExpressie(beginExpString);
        	if (beginExp == null) 
        	{
        		//Window.alert("null in beginExp " + beginExpString);
        	} 
        	else 
        		zetExpressie(beginExp);
        }
        super.setState(map);
		zetMaat();
    }
    
    /**
     * setter voor zoomInTabel
     * @param b true/false
     */
    public void zetZoomInTabel(boolean b)
    {  	zoomInTabel = b;
    	zoomInKnop.visible = b;
    	zoomUitKnop.visible = b;
    }

    /**
     * zet de uitgaande Pijlen naar links/rechts
     */
	public void zetLinks(boolean b)
	{	if (links)
		{	zoomInKnop.translate(-10,0);
			zoomUitKnop.translate(-10,0);
		}
		links = b;
		for(int i=0 ; i<aantalPu ; i++)
		{	pijlUit[i].zetLinks(b);
			if(!links)
			{	pijlUit[i].zetPlaats(xPos + breedte+9 ,yPos + 10 );
			}
			else 
			{	pijlUit[i].zetPlaats(xPos - 10 ,yPos + 10 );
			}
		}
	}
	
	/**
	 * zet de achtergrondkleur van de UVS
	 */
	public void zetVakKleur(CssColor color)
	{	vakKleur = color;
		String cString = color.toString().substring(4, color.toString().length() - 1);
		String[] kleurenStr = StringUtils.split(cString,",");
		int cBlue =  Integer.parseInt(kleurenStr[2]);	
		int cGreen = Integer.parseInt(kleurenStr[1]);
		int cRed =   Integer.parseInt(kleurenStr[0]);
		vakKleurSoft = CssColor.make((cRed+765)/4, (cGreen+765)/4, (cBlue+765)/4);
		if (color.toString().equals(CssColor.make(0, 0, 0).toString())) 
			vakKleur = null;
	}
	
	/**
	 * teken deze UVS
	 */
	public void paint(Context2d g)
  	{	if (!visible)
			return;
		CssColor achtergrondkleur = CssColor.make(255, 255, 255);
  		if (pijlIn1 != null)
  		{	achtergrondkleur = CssColor.make(220, 220, 220);
  		}
		if (!links)
		{	// rand
			g.setFillStyle(CssColor.make(125, 125, 125));
			g.fillRect(xPos + 10, yPos + 0, breedte - 11, hoogte - 1);
			g.setStrokeStyle(CssColor.make(0,0,0));
			g.strokeRect(xPos + 10, yPos + 0, breedte - 11, hoogte - 1);
			super.paint(g);
			int labelCorr = 0;
			tabelCorr = 0;
			if (labelZichtbaar)
				labelCorr = 20;
			if (tabelZichtbaar)
				tabelCorr = 152;
			// override
			if (vakKleur != null) 
			{	g.setFillStyle(vakKleurSoft);
			}
			else 
			{	g.setFillStyle(achtergrondkleur);
			}
			g.fillRect(xPos + 12, yPos + labelCorr + 2, breedte - 15, hoogte - labelCorr - tabelCorr - 5);
			g.setStrokeStyle(CssColor.make(0,0,0));
			g.strokeRect(xPos + 12, yPos + labelCorr + 2, breedte - 15, hoogte - labelCorr - tabelCorr - 5);
		
			g.setFont(fontString);
			g.setFillStyle(CssColor.make(0,0,0));
			if (expressie != null && kettingZichtbaar)
			{	// vindt afmeting expressie
				expressie.zetMaat(fontSize, ascContext2d);
				TextMetrics tm = g.measureText(waardeString);
				int stringWidth = (int) Math.round(tm.getWidth());
				// er moet een waarde getoond	
				if (toonWaarde && !Double.isNaN(expressie.geefWaarde().doubleValue()))
				{	g.fillText(waardeString, xPos + 5+(breedte-stringWidth)/2, yPos + hoogte - tabelCorr - 5);
				}
				else // teken de Expressie 
				{	expressie.teken(g, xPos + 5 + (breedte - expressie.breedte) / 2, 
								    yPos + 7 + (labelCorr + hoogte - tabelCorr - 15 - expressie.hoogte)/2 + 10);
				}
			}
			// ketting niet zichtnbaar, maak van de Expressie een functie en teken die
			else if (expressie != null && expressie.geefVarNaam() != null && 
					 !(expressie instanceof Functie) && !kettingZichtbaar)
			{	Expressie functie = new Functie(new BasisExpressie(expressie.geefVarNaam()),expressie);
				functie.zetMaat(fontSize, ascContext2d);
				functie.teken(g, xPos + 5+(breedte - functie.breedte)/2, 
							  yPos + 7 + (labelCorr + hoogte - tabelCorr - 15 - functie.hoogte)/2 + 10);
			}
			if (labelZichtbaar)
			{	g.setFont(fontString);
				g.setFillStyle(CssColor.make(255,255,255));
				TextMetrics tm = g.measureText(labelTekst);
				int labelWidth = (int) Math.round(tm.getWidth());
				g.fillText(labelTekst, xPos + 5 + (breedte - labelWidth) / 2, yPos + 15); 
			}
		}
		else // links
		{	g.setStrokeStyle(CssColor.make(125,125,125));
			g.fillRect(xPos + 0,yPos + 0,breedte-11,hoogte-1);
			g.setStrokeStyle(CssColor.make(0,0,0));
			g.strokeRect(xPos + 0,yPos + 0,breedte-11,hoogte-1);
			super.paint(g);
			int labelCorr = 0;
			tabelCorr = 0;
			if (labelZichtbaar)
				labelCorr = 20;
			if (tabelZichtbaar)
				tabelCorr = 152;
			// override
			if (vakKleur != null) 
			{	g.setFillStyle(vakKleur);
			}
			else 
			{	g.setFillStyle(achtergrondkleur);
			}
			g.fillRect(xPos + 2,yPos + labelCorr+2,breedte-15,hoogte-labelCorr-tabelCorr-5);
			g.setStrokeStyle(CssColor.make(0,0,0));
			g.strokeRect(xPos + 2,yPos + labelCorr+2,breedte-15,hoogte-labelCorr-tabelCorr-5);
		
			g.setFont(fontString);
			g.setFillStyle(CssColor.make(0,0,0));
			
			if (expressie != null)
			{	expressie.zetMaat(fontSize, ascContext2d);
				TextMetrics tm = g.measureText(waardeString);
				int stringWidth = (int) Math.round(tm.getWidth());
				// waarde
				if (toonWaarde && !Double.isNaN(expressie.geefWaarde().doubleValue()))
				{	g.fillText(waardeString, xPos -5 + (breedte - stringWidth) / 2, 
							   yPos + hoogte - tabelCorr - 5);
				}	
				else // expressie
				{	expressie.teken(g, xPos -5 + (breedte - expressie.breedte) / 2, 
									yPos + 7 + (labelCorr+hoogte - tabelCorr - 15 - expressie.hoogte)/2 + 10);
				}
			}	
			if (labelZichtbaar)
			{	g.setFont(fontString);
				g.setFillStyle(CssColor.make(255,255,255));
				TextMetrics tm = g.measureText(labelTekst);
				int labelWidth = (int) Math.round(tm.getWidth());
				g.fillText(labelTekst, xPos -5 + (breedte - labelWidth) / 2, yPos + 15); 
			}
		}	
	}
	/**
	 * zet het label zichtbaar, voor setState
	 * @param b true/false
	 */
	public void zetLabel(boolean b)
	{	labelZichtbaar = b;
		if(b)
		{	if (zoomInKnop.yPos == yPos+50)
				zoomInKnop.translate(0,20);
			if (zoomUitKnop.yPos == yPos+100)
				zoomUitKnop.translate(0,20);
			zetMaat();
		}
		else
		{	zetMaat();
		}
		if (!asv.owner.asvSetState)
			asv.tekenOpnieuw();
	}

	/**
	 * toon/verberg het Label
	 */
	public void toonLabel(boolean b)
	{	labelZichtbaar = b;
		super.toonLabel(b);
		if(b)
		{	if (zoomInKnop.yPos == yPos+50)
				zoomInKnop.translate(0,20);
			if (zoomUitKnop.yPos == yPos+100)
				zoomUitKnop.translate(0,20);
			zetMaat();
		}
		else
		{	if (zoomInKnop.yPos == yPos+70)
				zoomInKnop.translate(0,-20);
			if (zoomUitKnop.yPos == yPos+120)
				zoomUitKnop.translate(0,-20);
			zetMaat();
		}
		if (!asv.owner.asvSetState)
			asv.tekenOpnieuw();
	}

	/**
	 * toon/verberg de tabel
	 * @param b true/false
	 */
	public void toonTabel(boolean b)
	{	tabelZichtbaar = b;
		if (b)
		{	tabel = new TabelComponent(asv);
			tabel.setDefaultVarnaam(defaultVarnaam);
			zetMaat();
			zetVeranderd(20);
		}
		else 
		{	tabel = null;
			zetMaat();
		}
		if (!asv.owner.asvSetState)
			asv.tekenOpnieuw();
		GrafiekComponent gc = vindGrafiekComponent();
		if (gc != null)
			gc.zetVeranderd(20);
	}
	/**
	 * getter voor labelTekst
	 * @return labelTekst
	 */
	public String geefLabelTekst()
	{	
		return labelTekst;
	}
		
	/**
	 * bepaal de afmetingen van deze UVS, rekening houdend met de grootte van de Expressie, de tabel (if any) en het label (if any)
	 */
	public void zetMaat()
	{	// basismaten	
		int b = AlgebraSchuifVeld.basisB;
		int h = AlgebraSchuifVeld.basisH;
		// hoogtecorrectie voor label
		int corr = 0;
		Expressie expFunctie = expressie;
		// de expressie in de UVS moet worden weergegeven als een functie, bepaal de nieuwe afmetingen
		if (expressie != null && expressie.geefVarNaam() != null && !(expressie instanceof Functie) && !kettingZichtbaar) 
		{	expFunctie = new Functie(new BasisExpressie(expressie.geefVarNaam()),expressie);
			expFunctie.zetMaat(fontSize,ascContext2d);
		}
		if (expressie != null)
		{	b = expFunctie.breedte;
			h = expFunctie.hoogte;
			if (toonWaarde && !Double.isNaN(expressie.geefWaarde().doubleValue()))
			{	TextMetrics tm = ascContext2d.measureText(waardeString);
				int stringWidth = (int) Math.round(tm.getWidth());
				b = stringWidth;
				h = 0;
			}
			// correcties
			if (b > 26)
			{	b = b + 24;
			}
			else 
			{	b =  AlgebraSchuifVeld.basisB;
			}
			if (h > 12)
				h = 10+((h+5)/10)*10;
			else 
				h = AlgebraSchuifVeld.basisH;
		}
		// label? update h en b
		if (labelZichtbaar)
		{	corr = 20;
			h=h+20;
			// breedte labelTekst
			TextMetrics tm = ascContext2d.measureText(labelTekst);
			int labelWidth = (int) Math.round(tm.getWidth());
			b = Math.max(b,labelWidth+15);
		}
		// tabel? update h en b
		if (tabelZichtbaar)
		{	h=h+152;
			b = Math.max(b,tabel.geefBreedte()+10);
		}
		setSize(b,h);
		// positioneer tabel (if any)
		if(!links)
		{	if (tabel != null)
				tabel.setBounds(xPos+20,yPos+h-152,b-10,152);
		}
		else
		{	if (tabel != null)
				tabel.setBounds(xPos+10,yPos+h-152,b-10,152);
		}	
	}
	
	/**
	 * zet de Expressie van deze UVS, bepaal ook de maat
	 * @param e de Expressie 
	 */
	public void zetExpressie(Expressie e)
	{	expressie = e;
		if (e instanceof BasisExpressie) 
			beginw = (BasisExpressie) e;
		expressie.zetMaat(fontSize, ascContext2d);
		if (tabel != null)
			tabel.zetExpressie(expressie);
		zetMaat();
	}
		
	/**
	 * geef de uitvoer-Expressie van deze UVS
	 */
	public Expressie geefUitvoer(int max)
	{	return expressie;
	}

	/**
	 * geef de verborgen uitvoer-Expressie van deze UVS
	 */
	public Expressie geefVerborgenUitvoer(int max)
	{	return verborgenExpressie;
	}
	
	/**
	 * kijk of een van de uitgaande pijlen van deze UVS verbonden is met de GrafiekComponent
	 * @return de GrafiekComponent of null
	 */
	public GrafiekComponent vindGrafiekComponent()
	{	GrafiekComponent gc = null;
		for (int pCnt = 0; pCnt < pijlUit.length; pCnt++)
		{	if ((pijlUit[pCnt] != null) && (pijlUit[pCnt].ontvanger instanceof GrafiekComponent))
				gc = (GrafiekComponent) pijlUit[pCnt].ontvanger;
		}
		return gc;
	}
	
	/**
	 * update de pijlenketting, met maximum diepte max
	 */
	public void zetVeranderd(int max)
	{	
		if (pijlIn1 != null)
		{	expressie = pijlIn1.zender.geefUitvoer(20);
			verborgenExpressie = pijlIn1.zender.geefVerborgenUitvoer(20);
			// verwijder goedkrul/foutkruis (if any)
			for (int pCnt = 0; pCnt < pijlUit.length; pCnt++)
			{ 	if (pijlUit[pCnt] != null)
					pijlUit[pCnt].im = null;
			}
			if (asv.owner.kijkNaActief)
				asv.answerChanged();
		}
		else if (!isBeginExpressie)
		{	// verwijder goedkrul/foutkruis (if any)
			for (int pCnt = 0; pCnt < pijlUit.length; pCnt++)
			{ 	if (pijlUit[pCnt] != null)
					pijlUit[pCnt].im = null;
			}
			if (asv.owner.kijkNaActief)
				asv.answerChanged();
			expressie = beginw;
			verborgenExpressie = new BasisExpressie(defaultVarnaam);
		}
  		if (tabel != null)
  		{	if (expressie != null && expressie.geefVarNaam() != null)
				tabel.zetExpressie(expressie);
			else 
				tabel.zetExpressie(verborgenExpressie);
		}		
		if (expressie != null)
		{	expressie.zetMaat(fontSize, ascContext2d);
			Double waarde = expressie.geefWaarde();
			if (!Double.isNaN(waarde.doubleValue()))
			{	waardeString = UF.format0(waarde,3);
			}
			else 
				waardeString = "-";
		}
		zetMaat();
		// zoomdata
		ZoomState zs = null;
		String naam = null;
		if (expressie != null && expressie.geefVarNaam() != null && asv != null)
		{	zs = asv.zoomStateHolder.getZoomState(expressie.geefVarNaam());
			naam  = expressie.geefVarNaam();
		}
		else if (verborgenExpressie != null && verborgenExpressie.geefVarNaam() != null && asv != null)
		{	zs = asv.zoomStateHolder.getZoomState(verborgenExpressie.geefVarNaam());
			naam  = verborgenExpressie.geefVarNaam();
		}
		if (zs != null && naam != null)
			setZoomState(naam, zs);
		super.zetVeranderd(max);
		GrafiekComponent gc = vindGrafiekComponent();
		if (gc != null)
			gc.zetVeranderd(max);
	}
	
	/**
	 * update de zoomState voor variabele varnaam indien deze de variabele is van expressie of verborgenExpressie 
	 * @param varnaam nam van de variabele
	 * @param zoomState de zoomState voor variabele varnaam
	 */
	public void setZoomState(String varnaam, ZoomState zoomState)
	{	if (expressie != null && expressie.geefVarNaam() != null && expressie.geefVarNaam().equals(varnaam) ||
			verborgenExpressie != null && verborgenExpressie.geefVarNaam() != null && verborgenExpressie.geefVarNaam().equals(varnaam))
		{	this.beginwaarde = zoomState.getBeginwaarde();
			this.selectnummer = zoomState.getSelectnummer();
			this.schaalFactorX = zoomState.getSchaalFactorX();
			this.factorRijNummerX = zoomState.getFactorRijNummerX();
			this.beginx = zoomState.getBeginx();
			// update zoomState Tabel
			if (tabel != null)
			{	tabel.zetTabel(beginwaarde, selectnummer, varnaam, schaalFactorX, beginx);
			}
			zetMaat();
		}
	}

	/**
	 * zet de tabel aan of uit, zie methode toonTabel
	 * @param b true/false
	 */
	public void zetTabelAan(boolean b)
	{	tabelAan = b;
		if (!isStapel) 
			toonTabel(b);
	}

	/**
	 * haal de tekst voor het label uit de Popup,
	 * verwijder de Popup en update
	 */
	public void zetLabelTekst()
	{	labelTekst = label.getText();
		label.setVisible(false);
		inputOwner.remove(label);
		zetMaat();
		zetVeranderd(20);
		asv.paint();
	}

	/**
	 * kijk of de tekst in de invoerpopup valide invoer is
	 */
	public void zetInvulWaarde()
	{
		try
		{
			zetInvulWaarde0();
		}
		catch (RestartException r)
		{
			r.restart(new Runnable()
			{
				public void run()
				{
					try
					{
						zetInvulWaarde0();
					}
					catch (RestartException e)
					{
						e.restart(this);
					}
				}
			});
		}
	}
	
	public void zetInvulWaarde0() throws RestartException
	{
		ZoomState zs = null;
		
		if (expressie != null && expressie.geefVarNaam() != null && asv != null)
		{	
			zs = asv.zoomStateHolder.getZoomState(expressie.geefVarNaam());
		}
		else if (verborgenExpressie != null && verborgenExpressie.geefVarNaam() != null && asv != null )
		{	
			zs = asv.zoomStateHolder.getZoomState(verborgenExpressie.geefVarNaam());
		}
		// check invoer
		boolean isGeldigeInvoer = true;
		{	
			try
			{	
				String s = tf.getText();
				s = s.replace(',','.');
				
				// formules uit formuleeditor zoals 3$m2@ verwerken
				fi.wiskopdr.expressies.Expressie exp = FormuleParser.geefExpressie(addFormulaCodes(tf.getText()));
				
				if (s.equals(""))
				{
					tfString = "";
					isGeldigeInvoer = false;
				}
				else if (exp == null)
				{
					isGeldigeInvoer = false;
				}
				else
				{
					exp = fi.wiskopdr.expressies.Expressie.evalWithCAS(exp); // deze kan een restartexception geven
					Double d = null;
					if (exp != null && exp.isWaarde())
					{
						d = exp.geefWaarde();
						// afronden op 3 decimalen; format voor grote getallen met wetenschappelijke notatie zoals 1234567^2 = 1524155677489...
						String formatted = NumberFormat.getFormat("0.###").format(d);
						formatted = formatted.replace(',', '.'); // dit moet, anders gaat BasisExpressie.geefWaarde() met Double.valueOf(basisString) mis
						tf.setText(formatted);
					}
					else
					{
						// check of variabele letters/variabelenaam bevat
						for (int i = 0; i < tf.getText().length(); i++)
						{
							if (!Letter.isLetter(tf.getText().charAt(i)))
							{
								isGeldigeInvoer = false;
								break;
							}
						}
						if (!isGeldigeInvoer)
						{
							tf.setText("");
							tfString = "";
						}
					}
				}
			}	
			catch (NumberFormatException ex)
			{
				for (int i = 0; i < tf.getText().length(); i++)
				{
					if (!Letter.isLetter(tf.getText().charAt(i)))
					{
						isGeldigeInvoer = false;
						break;
					}
				}
				if (tf.getText().equals(""))
					isGeldigeInvoer = false;
				if (!isGeldigeInvoer)
				{	
					tf.setText("");
					tfString = "";
				}
			}			
		}
		
		if (isGeldigeInvoer)
		{	
			tfString = tf.getText();
			beginw = new BasisExpressie(tf.getText());
			beginw.zetMaat(fontSize, ascContext2d);
		}
		else // ongeldige invoer alles leeg
		{	
			tf.setText("");
			tfString = "";
			beginw = null;
		}

		expressie = beginw;
		
		if (tabel != null)
		{	if (expressie != null && expressie.geefVarNaam() != null)
				tabel.zetExpressie(expressie); 
			else 
				tabel.zetExpressie(verborgenExpressie);
		}
		// variabele kan veranderd zijn: nieuwe zoomState vor de variabele
		if (zs != null && expressie != null && expressie.geefVarNaam() != null)
			asv.zoomStateHolder.copyZoomState(expressie.geefVarNaam(), zs);
		else if (zs != null) 
			asv.zoomStateHolder.copyZoomState(defaultVarnaam, zs);
		// verwijder invoer popup
		tf.setVisible(false);
		inputOwner.remove(tf);
		// update
		zetMaat();
		zetVeranderd(20);
		asv.tekenOpnieuw();
	}
  
	/**
	 * Surround the given string with the formule codes "$f" and "@".
	 * Used for fomula editor.
	 * 
	 * @param string
	 * @return
	 */
	private String addFormulaCodes(String string)
	{
		String startCode = "$f";
		String endCode = "@";
		String s = startCode + string + endCode;
		return s;
	}

	/**
	 * verberg/toon de pijlenketting verbonden met pijlIn; maak de tabel (if any)
	 * dubbel/enkel koloms
	 * @param b false/true
	 */
    public void zetKettingZichtbaarHier(boolean b)
    {   if (isBeginExpressie)
    		return;
    	if (pijlIn1 != null)
    		pijlIn1.zender.zetKettingZichtbaar(b);
        kettingZichtbaar = b;
        if (tabel != null)
        	tabel.zetDubbel(!b);
        zetMaat();
        asv.tekenOpnieuw();
    }

    /**
     * zoom uit in de tabel
     */
    public void zoomUitTabelAction()
    {	if (asv.isDemo)
			return;
    	// kijk wat huidig factorRijNummerX is
		if (factorRijNummerX > 120)
			return;
		if (factorRijNummerX % 3 == 1)
		{	schaalFactorX *= 2.5;
			beginx = beginx / 2.5;
		}
		else 
		{	schaalFactorX *= 2;
			beginx = beginx / 2;
		}
		beginx = Math.round(beginx / 14) * 14;
		beginwaarde = -(int) Math.round(beginx / 14);
		selectnummer = 999;
		factorRijNummerX++;
        String varnaam = null;
        if (expressie != null) 
        	varnaam = expressie.geefVarNaam();
        if (varnaam == null && verborgenExpressie != null) 
        	varnaam = verborgenExpressie.geefVarNaam();
        // update de zoomState voor de variabele
        asv.zoomStateHolder.setBeginwaarde(varnaam, beginwaarde);
        asv.zoomStateHolder.setSelectnummer(varnaam, selectnummer);
        asv.zoomStateHolder.setSchaalFactorX(varnaam, schaalFactorX);
        asv.zoomStateHolder.setFactorRijNummerX(varnaam, factorRijNummerX);
        asv.zoomStateHolder.setBeginx(varnaam, beginx);
        asv.zoomStateHolder.setZoomStates(varnaam);
        asv.tekenOpnieuw();
    }

    /**
     * zoom in in de tabel 
     */
    public void zoomInTabelAction()
    {	if (asv.isDemo)
			return;
		// kijk wat huidig factorRijNummerX is
    	if (factorRijNummerX < 87)
			return;
		if (factorRijNummerX % 3 == 2)
		{	schaalFactorX /= 2.5;
			beginx = beginx * 2.5;
		}
		else 
		{	schaalFactorX /= 2;
			beginx = beginx * 2;
		}
		beginx = Math.round(beginx / 14) * 14;
		beginwaarde = -(int) Math.round(beginx / 14);
		selectnummer = 999;
		factorRijNummerX--;
        String varnaam = null;
        if (expressie != null) 
        	varnaam = expressie.geefVarNaam();
        if (varnaam == null && verborgenExpressie != null) 
        	varnaam = verborgenExpressie.geefVarNaam();
        // uddate de zoomState
        asv.zoomStateHolder.setBeginwaarde(varnaam, beginwaarde);
        asv.zoomStateHolder.setSelectnummer(varnaam, selectnummer);
        asv.zoomStateHolder.setSchaalFactorX(varnaam, schaalFactorX);
        asv.zoomStateHolder.setFactorRijNummerX(varnaam, factorRijNummerX);
        asv.zoomStateHolder.setBeginx(varnaam, beginx);
        asv.zoomStateHolder.setZoomStates(varnaam);
       	asv.tekenOpnieuw();
    }

	/**
	 * redefined, als de UVS een beginExpressie bevat, dan kan deze geen pijlIn hebben
	 */
	public boolean meldAan(Pijl p, int x, int y)
	{	if (isBeginExpressie)
			return false;
		else 
			return super.meldAan(p, x, y);
	}
	
	/**
	 * actie bij mouseDown/touchStart op (eventX,eventY)
	 */
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	if (asv.isDemo)
			return;
		zoomInPressed = false;
		zoomUitPressed = false;
		labelPressed = false;
		// op het label geklikt?
		if (labelZichtbaar && new Rectangle(xPos,yPos,breedte,20).contains(eventX, eventY))
			labelPressed = true;
		// zijn de zoomknoppen actief en is erop geklikt?
		if (zoomInTabel && 
			new Rectangle(zoomInKnop.xPos,zoomInKnop.yPos - 10,zoomInKnop.breedte + 4,
					      zoomInKnop.hoogte + 15).contains(eventX, eventY))
			zoomInPressed = true;
		if (zoomInTabel && 
			new Rectangle(zoomUitKnop.xPos,zoomUitKnop.yPos - 10,zoomUitKnop.breedte + 4,
						  zoomUitKnop.hoogte + 15).contains(eventX, eventY))
			zoomUitPressed = true;
		// fixeer hier
        taptime = System.currentTimeMillis();
        doubletap.add(taptime);
        // slepen
        if (!labelPressed && !zoomInPressed && !zoomUitPressed)
        	super.mouseDownTouchStartAction(eventX, eventY);
	}
	
	/**
	 * actie bij mouseMove/touchMove op (eventX,eventY)
	 */
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	if (asv.isDemo)
			return;
    	if (!labelPressed && !zoomInPressed && !zoomUitPressed)
    		super.mouseMoveTouchMoveAction(eventX, eventY);
	}
	
	/**
	 * actie bij mouseUp/touchEnd	
	 */
	public void mouseUpTouchEndAction()
	{	
		if (asv.isDemo)
			return;
		// doubleclick en niet zoomen in de tabel 
		if (isDoubleClick() && !zoomInPressed && !zoomUitPressed) 
		{	// doubleClick op het label
			if (labelPressed && !isStapel && !asv.alleenInvullen)
			{	showLabelPopup();
			}
			// doubleClick op het invoerveld
			else if (!isStapel && !isBeginExpressie && pijlIn1 == null)
			{	if (tabel == null)
					showTekstPopup();
				else // alleen popup als niet op de tabel geklikt
				{	boolean raak = new Rectangle(xPos, yPos, breedte, hoogte - 152).contains(startx, starty);
					if (raak)
						showTekstPopup();
				}
			}
            doubletap.clear();
        } 
		// longclick en niet zoomen in de tabel
		else if (isLongClick() && !zoomInPressed && !zoomUitPressed) 
		{	// longclick op het invoerveld
			if (!isStapel && !dragging && !isBeginExpressie && !asv.alleenInvullen)
			{
				if (tabel == null)
					showPopupMenu();
				else // alleen popupMenu als niet op de tabel geklikt
				{	boolean raak = new Rectangle(xPos, yPos, breedte, hoogte - 152).contains(startx, starty);
					if (raak)
						showPopupMenu();
				}
				doubletap.clear();
			}
        } 
		else // normale mouseUp/touchEnd
		{   if (doubletap.size() >= 2) 
            {	doubletap.remove(0);
            }
            if (zoomInPressed)
            	zoomInTabelAction();
            if (zoomUitPressed)
            	zoomUitTabelAction();
        }
		if (dragging)
			super.mouseUpTouchEndAction();
		labelPressed = false;
		zoomInPressed = false;
		zoomUitPressed = false;
	}

	/**
	 * voer de actie uit die correspondeert met Menu Command s
	 * @param s the Menu Command String
	 */
	public void menuAction(String s)
	{
		// menuCommand "label"
		if (s.equals("label"))
		{	if (labelItem.getText().equals("toon label") || 
				labelItem.getText().equals("show label") ||
				labelItem.getText().equals("montrer label") ||
				labelItem.getText().equals("zeige Label"))
			{	labelItem.setText(AlgebraPijlenGWT.rb.verbergLabel());
				toonLabel(true);
			}
			else if (labelItem.getText().equals("verberg label") ||
					 labelItem.getText().equals("hide label") ||
					 labelItem.getText().equals("cacher label")  ||
					 labelItem.getText().equals("verberge Label"))
			{	labelItem.setText(AlgebraPijlenGWT.rb.toonLabel());
				toonLabel(false);
			}
		}
		// menuCommand "tabel"
		else if (s.equals("tabel"))
		{	if (tabelItem.getText().equals("toon tabel") ||
				tabelItem.getText().equals("show table") ||
				tabelItem.getText().equals("montrer table") ||
				tabelItem.getText().equals("zeige Tabelle"))
			{	tabelItem.setText(AlgebraPijlenGWT.rb.verbergTabel());
				zetTabelAan(true);
			}
			else if (tabelItem.getText().equals("verberg tabel") ||
					 tabelItem.getText().equals("hide table") ||
					 tabelItem.getText().equals("cacher table") ||
					 tabelItem.getText().equals("verberge Tabelle"))
			{	tabelItem.setText(AlgebraPijlenGWT.rb.toonTabel());
				zetTabelAan(false);
			}
		}
		// menuCommand "ketting"
		else if (s.equals("ketting"))
		{
			if (kettingItem.getText().equals("toon ketting") ||
				kettingItem.getText().equals("show chain") ||
				kettingItem.getText().equals("montrer flèche") ||
				kettingItem.getText().equals("zeige Baum"))
			{	kettingItem.setText(AlgebraPijlenGWT.rb.verbergKetting());
				zetKettingZichtbaarHier(true);
			}
			else if (kettingItem.getText().equals("verberg ketting") ||
					 kettingItem.getText().equals("hide chain") ||
					 kettingItem.getText().equals("cacher flèche") ||
					 kettingItem.getText().equals("verberge Baum"))
			{	kettingItem.setText(AlgebraPijlenGWT.rb.toonKetting());
				zetKettingZichtbaarHier(false);
			}
		}
		menuPopup.setVisible(false);
	}
	
	/**
	 * Geef de expressie. Dit wordt gebruikt om de 'oude' invoer op te vragen
	 * bij druk op escape-toets.
	 */
	String geefExpressieString()
	{
		String text;
		
		text = expressie.toString();
		
		return text;
	}
	
	/**
	 * 	inner class die een Command String van een MenuItem 
	 * vertaald naar de corresponderende actie
	 */
	class MenuCommand implements Command
	{	String cmdString = "";
		public MenuCommand(String s)
		{	cmdString = s;
		}
		public void execute()
		{	menuAction(cmdString);
		}
	}
}
