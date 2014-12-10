package fi.algebraexprgwt.client;


import java.util.HashMap;

import fi.algebraexprgwt.client.expressies_ap.*;


//import javax.swing.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
//import com.google.gwt.dom.client.Style;

import com.google.gwt.user.client.Command;
//import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;


public class UitvoerSchuifComponent extends AlgebraSchuifComponent //implements ActionListener, FocusListener
{	
	//private JTextField tf;
	TekstPopup tf, label;
	LayoutPanel inputOwner; 
	String tfString = "";
	
	private Expressie expressie;
	private BasisExpressie beginw;
	private Expressie verborgenExpressie;	
	private String waardeString;
	private boolean toonWaarde;
	private boolean labelZichtbaar;

	String labelTekst = "";
	boolean labelPressed = false;
	
	//private InUitvoerLabel label;
	
	boolean tabelZichtbaar;


	boolean zoomInTabel;	
	
	private boolean grafiek;
	boolean muisrechts;
	
	//private GrafiekComponent grafiekComponent;
	TabelComponent tabel;
	
	private int tabelCorr;
	
	String fontString = "12px sans-serif";
	int fontSize = 12;
	
	PopupPanel menuPopup;
	MenuBar menuBar;
	MenuItem labelItem, tabelItem, kettingItem;

	//private PlusMinKnop plusMinKnop;
	
	// later terugzetten als scrollknoppen lukken	
	//boolean scrollable = true;
	boolean scrollable = false;

	int scrollCorr = 0;
	
	public boolean kettingZichtbaar = true;	
	
	ZoomKnop zoomInKnop;
	ZoomKnop zoomUitKnop;
	
	boolean zoomInPressed = false;
	boolean zoomUitPressed = false;

	private double schaalFactorX=1;
	private int factorRijNummerX=99;
	private int beginwaarde;
	private int selectnummer;
	private double beginx;
	private String defaultVarnaam = "qq" + 1000 * Math.random();
	
	private CssColor vakKleur, vakKleurSoft;	
	
	public UitvoerSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(1, asv, x, y, b, h);
		
	
		//toonWaarde = !((AlgebraSchuifVeld) schuifveld).ip.isExpr();
		if (asv.owner.waardeBox != null)
			toonWaarde = asv.owner.waardeBox.getValue();
		// anders default false
		
		labelZichtbaar = false;
		tabelZichtbaar = false;
		
		zoomInTabel = true;
		
		waardeString = "";		
		
		inputOwner = asv.owner.canvasPanel;
		
		menuBar = new MenuBar(true);
		labelItem = new MenuItem("toon label", new MenuCommand("label"));
		tabelItem = new MenuItem("toon tabel", new MenuCommand("tabel"));
		kettingItem = new MenuItem("verberg ketting", new MenuCommand("ketting"));
		menuBar.addItem(labelItem);
		menuBar.addItem(tabelItem);
		menuBar.addItem(kettingItem);

		//label = new InUitvoerLabel();
		
// niet hier		
		//tabel = new TabelComponent();
		//tabel.setDefaultVarnaam(defaultVarnaam);		
		
//veel werk, niet nodig?
/*		
		// popup met label(2x), tabel(2x), ketting(2x)
		popup = new JPopupMenu();

		JMenuItem mi = new JMenuItem(AlgebraExpressies.rb.getString("popup1Label1"));
		mi.addActionListener(this);
		popup.add(mi);
		mi = new JMenuItem(AlgebraExpressies.rb.getString("popup1Label2"));
		mi.addActionListener(this);
		popup.add(mi);
		
		popup.addSeparator();
		
		mi = new JMenuItem(AlgebraExpressies.rb.getString("popup1Label3"));
		mi.addActionListener(this);
		popup.add(mi);
		mi = new JMenuItem(AlgebraExpressies.rb.getString("popup1Label4"));
		mi.addActionListener(this);
		popup.add(mi);
		
		popup.addSeparator();
		
		mi = new JMenuItem(AlgebraExpressies.rb.getString("popup1Label5"));
		mi.addActionListener(this);
		popup.add(mi);
		mi = new JMenuItem(AlgebraExpressies.rb.getString("popup1Label6"));
		mi.addActionListener(this);
		popup.add(mi);
		
		add(popup);
*/
		
/*		
		// popup met label(2x), ketting(2x)
		popup2 = new JPopupMenu();
		
		mi = new JMenuItem(AlgebraExpressies.rb.getString("popup1Label1"));
		mi.addActionListener(this);
		popup2.add(mi);
		mi = new JMenuItem(AlgebraExpressies.rb.getString("popup1Label2"));
		mi.addActionListener(this);
		popup2.add(mi);
		
		popup2.addSeparator();
		
		mi = new JMenuItem(AlgebraExpressies.rb.getString("popup1Label5"));
		mi.addActionListener(this);
		popup2.add(mi);
		mi = new JMenuItem(AlgebraExpressies.rb.getString("popup1Label6"));
		mi.addActionListener(this);
		popup2.add(mi);
		
		add(popup2);
*/
		
/*		
		// popup met label(2x), ketting(2x)
		popup3 = new JPopupMenu();
		
		mi = new JMenuItem(AlgebraExpressies.rb.getString("popup1Label1"));
		mi.addActionListener(this);
		popup3.add(mi);
		mi = new JMenuItem(AlgebraExpressies.rb.getString("popup1Label2"));
		mi.addActionListener(this);
		popup3.add(mi);
		
		add(popup3);
*/		

		verborgenExpressie = new BasisExpressie(defaultVarnaam);
		
/*		
		plusMinKnop = new PlusMinKnop(b-12,11,10,h-11, PlusMinKnop.VERTIKAAL);
		plusMinKnop.addActionListener(this);
		plusMinKnop.setVisible(false);		
		add(plusMinKnop);
*/
	
		
		zoomInKnop	= new ZoomKnop("zoomintabel", xPos, yPos + 80, 10, 10, asv.asvContext2d);		
		
		//zoomInKnop	= new ZoomKnop("zoominxsmal");
		//zoomInKnop.setBounds(1,60,11,25);
		//zoomInKnop.addActionListener(this);
		//add(zoomInKnop);
		
		zoomUitKnop	= new ZoomKnop("zoomuittabel", xPos, yPos + 130, 10, 10, asv.asvContext2d);		
		//zoomUitKnop	= new ZoomKnop("zoomuitxsmal");
		//zoomUitKnop.setBounds(1,110,11,25);
		//zoomUitKnop.addActionListener(this);
		//add(zoomUitKnop);
		
	
	}

	public HashMap<String,Object> getState()
	{	String basisExp  = null;
		String defaultVarnaam = null;
		boolean tabelZichtbaar = false;
		boolean labelZichtbaar = false;
        boolean kettingZichtbaar = true;
		String labelTekst = null;
		
		if (beginw != null)
			basisExp = this.beginw.basisString;
		else 
			basisExp = "";
		defaultVarnaam = this.defaultVarnaam;
		tabelZichtbaar = this.tabelZichtbaar;
		labelZichtbaar = this.labelZichtbaar;
        kettingZichtbaar = this.kettingZichtbaar;
        
        labelTekst = this.labelTekst;
        
		//labelTekst = label.geefTekst();
		
//System.out.println("get " + kettingZichtbaar);		
		HashMap<String,Object> h = super.getState();

		h.put("basisExp", basisExp);
	    h.put("defaultVarnaam", defaultVarnaam);
	    h.put("tabelZichtbaar", new Boolean(tabelZichtbaar));
	    h.put("labelZichtbaar", new Boolean(labelZichtbaar));
        h.put("kettingZichtbaar", new Boolean(kettingZichtbaar));
        h.put("labelTekst", labelTekst);
        
        h.put("scrollable", new Boolean(scrollable));
        h.put("zoomInTabel", new Boolean(zoomInTabel));
        
	    return h;
	}

    public void setState(HashMap<String,Object> h)
    {	String basisExp  = "";
 		String defaultVarnaam = null;
        boolean tabelZichtbaar = false;
        boolean labelZichtbaar = false;
        boolean kettingZichtbaar = true;
        String labelTekst = "";
        
        boolean scrollable = true;
        boolean zoomInTabel = true;
        
    	if (h.containsKey("basisExp")) 
    		basisExp = (String) h.get("basisExp");
    	if (h.containsKey("defaultVarnaam")) 
    		defaultVarnaam = (String) h.get("defaultVarnaam");
        if (h.containsKey("tabelZichtbaar")) 
        	tabelZichtbaar = ((Boolean) h.get("tabelZichtbaar")).booleanValue();		
        if (h.containsKey("labelZichtbaar")) 
        	labelZichtbaar = ((Boolean) h.get("labelZichtbaar")).booleanValue();
        if (h.containsKey("kettingZichtbaar")) 
        	kettingZichtbaar = ((Boolean) h.get("kettingZichtbaar")).booleanValue();
        if (h.containsKey("labelTekst")) 
        	labelTekst = (String) h.get("labelTekst");
        
        if (h.containsKey("scrollable")) 
        	scrollable = ((Boolean) h.get("scrollable")).booleanValue();
        if (h.containsKey("zoomInTabel")) 
        	zoomInTabel = ((Boolean) h.get("zoomInTabel")).booleanValue();
        
		if (!basisExp.equals(""))
			beginw = new BasisExpressie(basisExp);
		if (defaultVarnaam != null) 
		{	this.defaultVarnaam = defaultVarnaam;
			verborgenExpressie = new BasisExpressie(defaultVarnaam);
		}
		
		toonTabel(tabelZichtbaar,true);
		toonLabel(labelZichtbaar);
		this.labelTekst = labelTekst;
		
//GWT		
		//label.zetLabelTekst(labelTekst);

		if (!kettingZichtbaar)
			zetBoomZichtbaarHier(kettingZichtbaar);

        //zetScroll(scrollable);
        zetZoomInTabel(zoomInTabel);
		
		super.setState(h);
		
		zetMaat();
		
    }

    public void zetZoomInTabel(boolean b)
    {
    	zoomInTabel = b;
    	
    	//zoomInKnop.setVisible(b);
    	//zoomUitKnop.setVisible(b);
    	
    	zoomInKnop.visible = b;
    	zoomUitKnop.visible = b;

    }

    public void zetScroll(boolean b)
	{	scrollable = b;
	
		zetVeranderd(20);
	
		zetMaat();
	}
    
	public void zetVakKleur(CssColor color)
	{	vakKleur = color;
	
		String cString = color.toString().substring(4, color.toString().length() - 1);
		String[] kleurenStr = StringUtils.split(cString,",");

		int cBlue =  Integer.parseInt(kleurenStr[2]);	
		int cGreen = Integer.parseInt(kleurenStr[1]);
		int cRed =   Integer.parseInt(kleurenStr[0]);
	
	
	 	//vakKleurSoft = new Color((color.getRed()+765)/4, (color.getGreen()+765)/4, (color.getBlue()+765)/4);
		vakKleurSoft = CssColor.make((cRed+765)/4, (cGreen+765)/4, (cBlue+765)/4);
		
		//if(color==Color.black) vakKleur = null;
		if(color.toString().equals(CssColor.make(0, 0, 0).toString())) 
			vakKleur = null;
	}
    
	//public void paint(Graphics g)
	public void paint(Context2d g)
  	{ 	
		
		if (!visible)
			return;
		
  		//Color achtergrondkleur = Color.white;
  		CssColor achtergrondkleur = CssColor.make(255, 255, 255);
  		if (pijlIn1 != null)
  		{	//achtergrondkleur = new Color(220, 220, 220);
  			achtergrondkleur = CssColor.make(220, 220, 220);
  		}
  		
		//g.setColor(Color.gray);
		g.setFillStyle(CssColor.make(125, 125, 125));
		//g.fillRect(0, 10, getSize().width - 1, getSize().height - 11);
		g.fillRect(xPos + 0, yPos + 10, breedte - 1, hoogte - 11);
		
		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		
		//g.drawRect(0, 10, getSize().width - 1, getSize().height - 11);
		g.strokeRect(xPos + 0, yPos + 10, breedte - 1, hoogte - 11);
		
		super.paint(g);
		
//g.setColor(Color.red);
//g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
		
		int labelCorr = 0;
		int tabelCorr = 0;
		if (labelZichtbaar)
			labelCorr = 20;
		if (tabelZichtbaar)
			tabelCorr = 152;
		
		
		if (vakKleur != null) 
		{	//g.setColor(vakKleurSoft);
			g.setFillStyle(vakKleurSoft);
		}
		else 
		{	//g.setColor(achtergrondkleur);
			g.setFillStyle(achtergrondkleur);
		
		}
		
		//g.fillRect(2, labelCorr + 12, getSize().width - 5  - scrollCorr, getSize().height - labelCorr - tabelCorr - 15);
		g.fillRect(xPos + 2, yPos+labelCorr + 12, breedte - 5  - scrollCorr, hoogte - labelCorr - tabelCorr - 15);
		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		//g.drawRect(2, labelCorr + 12, getSize().width - 5  - scrollCorr, getSize().height - labelCorr - tabelCorr - 15);
		g.strokeRect(xPos+2, yPos+labelCorr + 12, breedte - 5  - scrollCorr, hoogte - labelCorr - tabelCorr - 15);
		
		
		//g.setFont(f);
		g.setFont(fontString);
		g.setFillStyle(CssColor.make(0,0,0));
		if (expressie != null)
		{	//expressie.zetMaat(fm);
			expressie.zetMaat(fontSize, ascContext2d);
			
			TextMetrics tm = g.measureText(waardeString);
			int stringWidth = (int) Math.round(tm.getWidth());
			
			if (toonWaarde && expressie.geefVarNaam() == null)
			{	//g.drawString(waardeString, (getSize().width - scrollCorr - fm.stringWidth(waardeString)) / 2, 
						                    //getSize().height - tabelCorr - 5);
				g.fillText(waardeString, xPos+(breedte - scrollCorr - stringWidth) / 2, 
								         yPos+ hoogte - tabelCorr - 5);
			}
			else 
			{	//expressie.teken(g, (getSize().width - scrollCorr - expressie.breedte) / 2, 
						        //12 + (labelCorr + getSize().height - tabelCorr - 15 - expressie.hoogte) / 2);
				expressie.teken(g, xPos+ (breedte - scrollCorr - expressie.breedte) / 2, 
								yPos+12 + (labelCorr + hoogte - tabelCorr - 15 - expressie.hoogte) / 2 +10);
//verbeterd + 10				
			}
		}
		

  		if ((soort == 1) && 
  			//(pijlIn1 == null) && 
  			labelZichtbaar)
  		{	//g.setColor(Color.white);
  			//g.setFillStyle(achtergrondkleur);
  			//g.fillRect(0, 0, 12, 5);
  			
			//if (labelZichtbaar)
			//{
//System.out.println("paint labelTekst rechts " + labelTekst);				
				g.setFont(fontString);
				g.setFillStyle(CssColor.make(255,255,255));
				TextMetrics tm = g.measureText(labelTekst);
				int labelWidth = (int) Math.round(tm.getWidth());
				g.fillText(labelTekst, xPos + (breedte - scrollCorr - labelWidth) / 2, yPos + 25); 
			//}

  		}
  		
		
	}
	
	public void zetToonWaarde(boolean b)
	{	toonWaarde = b;
		zetMaat();
	}
	
	public void toonLabel(boolean b)
	{	labelZichtbaar = b;
		if (b)
		{	
//GWT			
			//add(label);
			zetMaat();
		}
		else
		{	
//GWT			
			//remove(label);
			zetMaat();
		}
		asv.tekenOpnieuw();
	}
	
	
	public void toonTabel(boolean b, boolean verander)
	{	tabelZichtbaar = b;
		if (b)
		{	
		
			//add(tabel);
			tabel = new TabelComponent(asv);
			tabel.setDefaultVarnaam(defaultVarnaam);
			if (verander)
				zetVeranderd(20);
			zetMaat();
		}
		else
		{	
			
			//remove(tabel);
			tabel = null;
			zetMaat();
		}
		asv.tekenOpnieuw();
		
		GrafiekComponent gc = vindGrafiekComponent();
		if (gc != null)
			gc.zetVeranderd(20);
			
		
	}
	
	public boolean isLabelZichtbaar()
	{	return labelZichtbaar;
	}
	

	public String geefLabelTekst()
	{	return labelTekst;
	}
	
	public void zetLabelTekst()
	{
		
//System.out.println("zetLabelTekst " + label.getText());		
		labelTekst = label.getText();
		label.setVisible(false);
		inputOwner.remove(label);
		
		zetMaat();
		
		asv.paint();
	}
	
	public void setSize(int b, int h)
	{	
		breedte = b;
		hoogte = h;
//GWT?
		//label.setSize(b, 20);
		super.setSize(b, h);
	}
	
	public void zetMaat()
	{	
//GWT		
		//plusMinKnop.setVisible(false);
		
		int b = 40 + scrollCorr;
		int h = 30;
		int corr = 0;
		if (expressie != null)
		{	

			if (expressie.geefVarNaam() == null)
			{	
//GWT				
				//plusMinKnop.setVisible(true);
			
			}
			
			b = expressie.breedte + scrollCorr;
			h = expressie.hoogte;
			if (toonWaarde && expressie.geefVarNaam() == null && waardeString != null)
			{	
//System.out.println("ttonw && varn==null && ws not null");				
				
				//b = fm.stringWidth(waardeString) + scrollCorr;
				TextMetrics tm = ascContext2d.measureText(waardeString);
				int stringWidth = (int) Math.round(tm.getWidth());
				b = stringWidth + scrollCorr;
				h = 0;
				//plusMinKnop.setVisible(true);
			}
			if (b > 26)
				b = b + 14 + scrollCorr;
			else 
				b = 40 + scrollCorr;
			if (h > 12)
				h = 20 + ((h + 5) / 10) * 10;
			else 
				h = 30;
		}
		
		if (labelZichtbaar)
		{	corr = 20;
			h = h + 20;

			TextMetrics tm = ascContext2d.measureText(labelTekst);
			int labelWidth = (int) Math.round(tm.getWidth());
//System.out.println("labelWidth = " + labelWidth);			
		
			b = Math.max(b,labelWidth+15);
			
			//b = Math.max(b, label.geefBreedte());
		}
		if (tabelZichtbaar)
		{	h = h + 152;
		
			b = Math.max(b, tabel.geefBreedte());
//System.out.println("tabelb = " + tabel.geefBreedte());			
		}
		setSize(b, h);
		
//GWT		
		//label.setSize(b, 20);
		
		if (tabel != null)
			tabel.setBounds(xPos+10, yPos+h - 152, b - 10, 152);
		
//GWT
/*		
	
		plusMinKnop.setLocation(b - 12, 11 + corr);
		zoomInKnop.setBounds(1, h- 120,12,25);
		zoomUitKnop.setBounds(1, h - 70,12,25);
*/

	}
	
	public Expressie geefUitvoer(int max)
	{	return expressie;
	}

	public Expressie geefVerborgenUitvoer(int max)
	{	return verborgenExpressie;
	}

	public GrafiekComponent vindGrafiekComponent()
	{	GrafiekComponent gc = null;
		for (int pCnt = 0; pCnt < pijlUit.length; pCnt++)
		{
			if ((pijlUit[pCnt] != null) && (pijlUit[pCnt].ontvanger instanceof GrafiekComponent))
				gc = (GrafiekComponent) pijlUit[pCnt].ontvanger;
		}
	
		return gc;
	}
	
	public void zetVeranderd(int max)
	{	if (pijlIn1 != null)
		{	
//GWT		
			//remove(plusMinKnop);
		
			scrollCorr = 0;
			expressie = pijlIn1.zender.geefUitvoer(20);
			verborgenExpressie = pijlIn1.zender.geefVerborgenUitvoer(20);
			
			zoomInKnop.visible = false;
			zoomUitKnop.visible = false;			
			//zoomInKnop.setVisible(false);
			//zoomUitKnop.setVisible(false);
		}
		else 
		{	
			if (scrollable  && expressie != null && !Double.isNaN(expressie.geefWaarde().doubleValue()))
			{	if (scrollCorr == 0)
				{	
//GWT				
					//add(plusMinKnop);
				}
				scrollCorr = 10;
			}
			else
			{	scrollCorr = 0;
//GWT			
				//remove(plusMinKnop);
			}			
			expressie = beginw;
			verborgenExpressie = new BasisExpressie(defaultVarnaam);

			zoomInKnop.visible = true;
			zoomUitKnop.visible = true;			
			//zoomInKnop.setVisible(true);
			//zoomUitKnop.setVisible(true);
		}
	
		if (!kettingZichtbaar)
		{	
			zoomInKnop.visible = true;
			zoomUitKnop.visible = true;			
			//zoomInKnop.setVisible(true);
			//zoomUitKnop.setVisible(true);
		}
	
		//tabel.zetExpressie(expressie);
		
		if (expressie != null && !Double.isNaN(expressie.geefWaarde().doubleValue()))
			toonTabel(false,false);
		else if (verborgenExpressie != null && !Double.isNaN(verborgenExpressie.geefWaarde().doubleValue()))
			toonTabel(false,false);
		else if (expressie == null && verborgenExpressie == null)
			toonTabel(false,false);
		else
			toonTabel(tabelZichtbaar,false);

		if (tabel != null)
		{	
			if (expressie != null && expressie.geefVarNaam() != null)
				tabel.zetExpressie(expressie);
			else if (verborgenExpressie != null && verborgenExpressie.geefVarNaam() != null)
				tabel.zetExpressie(verborgenExpressie);
		}
		
		
		if (expressie != null)
		{	//expressie.zetMaat(fm);
			expressie.zetMaat(fontSize, ascContext2d);
			Double waarde = expressie.geefWaarde();
//if (waarde == null)
//System.out.println("w = null");	
			if (!Double.isNaN(waarde.doubleValue()))
			{	//waardeString = Expressie.df.format(waarde);
				waardeString = UF.format0(waarde,3);
			}
			else 
				waardeString = "-";
		}

		zetMaat();

// zie hierboven		
/*		
		if (expressie != null && !Double.isNaN(expressie.geefWaarde().doubleValue()))
			toonTabel(false);
		else if (verborgenExpressie != null && !Double.isNaN(verborgenExpressie.geefWaarde().doubleValue()))
			toonTabel(false);
		else if (expressie == null && verborgenExpressie == null)
			toonTabel(false);
		else
			toonTabel(tabelZichtbaar);
*/			
		
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
			gc.zetVeranderd(20);
		
	}

// DIT MOET WEG
/*	
	public void zetGrafiek(boolean b, GrafiekComponent gc)
	{	grafiek = b;
		tabel.zetSelectMogelijk(b);
		if (b)
			grafiekComponent = gc;
		else 
			grafiekComponent = null;
	}
*/	
// DIT MOET WEG	
/*	
	public void zetTabel(int beginwaarde, int selectnummer, String varN, double schaalFactorX)
	{	if (grafiek)
		{	tabel.zetTabel(beginwaarde, selectnummer, varN, schaalFactorX);
			
		}
		zetMaat();
	}
*/	
	
	public void setZoomState(String varnaam, ZoomState zoomState)
	{	if (expressie != null && expressie.geefVarNaam() != null && expressie.geefVarNaam().equals(varnaam)
			|| 
			verborgenExpressie != null && verborgenExpressie.geefVarNaam() != null && verborgenExpressie.geefVarNaam().equals(varnaam))
		{	this.beginwaarde = zoomState.getBeginwaarde();
			this.selectnummer = zoomState.getSelectnummer();
			this.schaalFactorX = zoomState.getSchaalFactorX();
			this.factorRijNummerX = zoomState.getFactorRijNummerX();
			this.beginx = zoomState.getBeginx();
			
			if (tabel != null)
			{	
				tabel.zetTabel(beginwaarde, selectnummer, varnaam, schaalFactorX, beginx);
				
				
				//if(grafiekComponent!=null)grafiekComponent.zetTabel(beginwaarde, selectnummer, varnaam, schaalFactorX);
			}
			zetMaat();
		}
		
	}
	
	public void zetInvulWaarde()
	{	
		ZoomState zs = null;
		if (expressie != null && expressie.geefVarNaam() != null && asv != null)
		{	zs = asv.zoomStateHolder.getZoomState(expressie.geefVarNaam());
			//naam  = expressie.geefVarNaam();
		}
		else if(verborgenExpressie!=null && verborgenExpressie.geefVarNaam()!=null && asv != null)
		{	zs = asv.zoomStateHolder.getZoomState(verborgenExpressie.geefVarNaam());
			//naam  = verborgenExpressie.geefVarNaam();
		}
		
		boolean isGeldigeInvoer = true;
		{	try
			{	String s = tf.getText();
				s = s.replace(',','.');
				tf.setText(s);
				Double w = Double.valueOf(tf.getText());
			}
			catch(NumberFormatException ex)
			{	for (int i = 0; i < tf.getText().length(); i++)
				{	if (!Character.isLetter(tf.getText().charAt(i)))
					{	isGeldigeInvoer = false;
						break;
					}
				}
				if (tf.getText().equals(""))
					isGeldigeInvoer = false;
				if (!isGeldigeInvoer)
				{	tf.setText("");
				}
			}
		}
		if (isGeldigeInvoer)
		{	
			
			tfString = tf.getText(); 	
			beginw = new BasisExpressie(tf.getText());
			//beginw = new BasisExpressie("2");
		
			//beginw.zetMaat(fm);
			beginw.zetMaat(fontSize, ascContext2d);
		}
		else
		{	beginw = null;
		}
		expressie = beginw;
		

		if (tabel != null)
		{	
			if (expressie != null && expressie.geefVarNaam() != null)
				tabel.zetExpressie(expressie);
			else 
				tabel.zetExpressie(verborgenExpressie);
		}
	
		if (zs != null && expressie != null && expressie.geefVarNaam() != null)
			asv.zoomStateHolder.copyZoomState(expressie.geefVarNaam(), zs);
		else if(zs != null) 
			asv.zoomStateHolder.copyZoomState(defaultVarnaam, zs);
		
		
		
		tf.setVisible(false);
		inputOwner.remove(tf);
		
		zetMaat();
		zetVeranderd(20);
		
		asv.tekenOpnieuw();
	}
	
	   public void zoomUitTabelAction()
	    {
			if (asv.isDemo)
				return;
			if (asv.frozen)
				return;
			
			//if (!e.getActionCommand().equals("knop") || factorRijNummerX > 120)
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
	        
	//System.out.println("vn = " + varnaam);            
	        
	        asv.zoomStateHolder.setBeginwaarde(varnaam, beginwaarde);
	        asv.zoomStateHolder.setSelectnummer(varnaam, selectnummer);
	        asv.zoomStateHolder.setSchaalFactorX(varnaam, schaalFactorX);
	        asv.zoomStateHolder.setFactorRijNummerX(varnaam, factorRijNummerX);
	        asv.zoomStateHolder.setBeginx(varnaam, beginx);
	        asv.zoomStateHolder.setZoomStates(varnaam);
	        
	        asv.tekenOpnieuw();
	    	
	    }
	    
	    public void zoomInTabelAction()
	    {
			if (asv.isDemo)
				return;
			if (asv.frozen)
				return;
			
			//if (!e.getActionCommand().equals("knop") || factorRijNummerX < 87)
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
			//((AlgebraSchuifVeld)getParent()).zetTabellen(beginwaarde,selectnummer, "x", schaalFactorX);
	        String varnaam = null;
	        if (expressie != null) 
	        	varnaam = expressie.geefVarNaam();
	        if (varnaam == null && verborgenExpressie != null) 
	        	varnaam = verborgenExpressie.geefVarNaam();
	        asv.zoomStateHolder.setBeginwaarde(varnaam, beginwaarde);
	        asv.zoomStateHolder.setSelectnummer(varnaam, selectnummer);
	        asv.zoomStateHolder.setSchaalFactorX(varnaam, schaalFactorX);
	        asv.zoomStateHolder.setFactorRijNummerX(varnaam, factorRijNummerX);
	        asv.zoomStateHolder.setBeginx(varnaam, beginx);
	        asv.zoomStateHolder.setZoomStates(varnaam);
	        //System.out.println("test1"+varnaam);
	        asv.tekenOpnieuw();
	    	
	    }
	
/*	
	public void actionPerformed(ActionEvent e)
	{		
		else if (e.getSource() == plusMinKnop)
		{	
			if (((AlgebraSchuifVeld) getParent()).isDemo)
				return;
			if (((AlgebraSchuifVeld) getParent()).frozen)
				return;
			

			if (beginw != null && !Double.isNaN(beginw.geefWaarde().doubleValue()))
			{	double w = beginw.geefWaarde().doubleValue();
				if (e.getActionCommand().equals("min"))
					w -= 1;
				if (e.getActionCommand().equals("plus"))
					w += 1;
				waardeString = Expressie.df.format(w);
				beginw = new BasisExpressie(waardeString);
				tf.setText(waardeString);
				zetVeranderd(20);
			}
		}
	
		else if (e.getSource() == zoomUitKnop)
		{	
			
			if (((AlgebraSchuifVeld) getParent()).isDemo)
				return;
			if (((AlgebraSchuifVeld) getParent()).frozen)
				return;
			
			
			if (!e.getActionCommand().equals("knop") || factorRijNummerX > 120) 
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
        
        	asv.zoomStateHolder.setBeginwaarde(varnaam, beginwaarde);
        	asv.zoomStateHolder.setSelectnummer(varnaam, selectnummer);
        	asv.zoomStateHolder.setSchaalFactorX(varnaam, schaalFactorX);
        	asv.zoomStateHolder.setFactorRijNummerX(varnaam, factorRijNummerX);
        	asv.zoomStateHolder.setBeginx(varnaam, beginx);
        	asv.zoomStateHolder.setZoomStates(varnaam);
        
        	asv.tekenOpnieuw();
		}
		else if (e.getSource() == zoomInKnop)
		{	
			if (((AlgebraSchuifVeld) getParent()).isDemo)
				return;
			if (((AlgebraSchuifVeld) getParent()).frozen)
				return;

			
			if (!e.getActionCommand().equals("knop") || factorRijNummerX < 87) 
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
			//((AlgebraSchuifVeld)getParent()).zetTabellen(beginwaarde,selectnummer, "x", schaalFactorX);
        	String varnaam = null;
        	if (expressie != null) 
        		varnaam = expressie.geefVarNaam();
        	if (varnaam == null && verborgenExpressie != null) 
        		varnaam = verborgenExpressie.geefVarNaam();
        	((AlgebraSchuifVeld)getParent()).zoomStateHolder.setBeginwaarde(varnaam, beginwaarde);
        	((AlgebraSchuifVeld)getParent()).zoomStateHolder.setSelectnummer(varnaam, selectnummer);
        	((AlgebraSchuifVeld)getParent()).zoomStateHolder.setSchaalFactorX(varnaam, schaalFactorX);
        	((AlgebraSchuifVeld)getParent()).zoomStateHolder.setFactorRijNummerX(varnaam, factorRijNummerX);
        	((AlgebraSchuifVeld)getParent()).zoomStateHolder.setBeginx(varnaam, beginx);
        	((AlgebraSchuifVeld)getParent()).zoomStateHolder.setZoomStates(varnaam);
        	//System.out.println("test1"+varnaam);
        	schuifveld.tekenOpnieuw();
		}
	
		else if (((JMenuItem)e.getSource()).getText().equals(AlgebraExpressies.rb.getString("popup1Label1")))
		{	toonLabel(true);
		}
		else if (((JMenuItem)e.getSource()).getText().equals(AlgebraExpressies.rb.getString("popup1Label2")))
		{	toonLabel(false);
		}
		else if (((JMenuItem)e.getSource()).getText().equals(AlgebraExpressies.rb.getString("popup1Label3")))
		{	toonTabel(true);
		}
		else if (((JMenuItem)e.getSource()).getText().equals(AlgebraExpressies.rb.getString("popup1Label4")))
		{	toonTabel(false);
		}
		else if (((JMenuItem)e.getSource()).getText().equals(AlgebraExpressies.rb.getString("popup1Label5")))
		{
			zetBoomZichtbaarHier(true);
		}
		else if (((JMenuItem)e.getSource()).getText().equals(AlgebraExpressies.rb.getString("popup1Label6")))
		{	
			zetBoomZichtbaarHier(false);
		}
	}
*/	
	
	public void showLabelPopup()
	{
		int popupX = xPos + inputOwner.getAbsoluteLeft();
		
		int popupY = yPos - 40 + inputOwner.getAbsoluteTop();
		//if (tabel != null)
		//	popupY -= 152;
		
		if ((label != null) && label.isVisible())
		{
			zetLabelTekst();
		}

		label = new TekstPopup(this, true);
		label.setText(labelTekst);
		label.setWidth("35px");
		label.setHeight("20px");
		//label.setModal(true);
		label.setPopupPosition(popupX, popupY);
		label.show();
		label.textBox.setFocus(true);

	}
	
	public void showTekstPopup()
	{
		int popupX = xPos + inputOwner.getAbsoluteLeft();
		int popupY = yPos + hoogte + inputOwner.getAbsoluteTop();
		if (tabel != null)
			popupY -= 152;
		
		if ((tf != null) && tf.isVisible())
		{
			zetInvulWaarde();
		}

		tf = new TekstPopup(this, false);
		tf.setText(tfString);
		tf.setWidth("35px");
		tf.setHeight("20px");
		//tf.setModal(true);
		tf.setPopupPosition(popupX, popupY);
		tf.show();
		tf.textBox.setFocus(true);

	}
	
	public void showPopupMenu()
	{
		int popupX = xPos + breedte + inputOwner.getAbsoluteLeft();
		int popupY = yPos + inputOwner.getAbsoluteTop();
		menuPopup = new PopupPanel(true);
		menuPopup.setWidget(menuBar);
		menuPopup.setPopupPosition(popupX, popupY);
		menuPopup.show();
	}	
	
	
	public void zetBoomZichtbaarHier(boolean b)
	{
		if (pijlIn1 != null)
			pijlIn1.zender.zetBoomZichtbaar(b);
		if (pijlIn2 != null)
			pijlIn2.zender.zetBoomZichtbaar(b);

		for (int i = 0; i < aantalPu; i++)
		{	pijlUit[i].setVisible(b);
		}
		
        open = b;
        kettingZichtbaar = b;
        
        asv.tekenOpnieuw();
	}
	
	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;
		
		
		
		muisrechts = false;
		
		//super.mousePressed(e);
		press = true;
		
		// erboven staat een balletje
		if (labelZichtbaar && new Rectangle(xPos,yPos+10,breedte,20).contains(eventX, eventY))
			labelPressed = true;
		
		if (zoomInTabel && 
			new Rectangle(zoomInKnop.xPos,zoomInKnop.yPos - 10,zoomInKnop.breedte + 4,
					      zoomInKnop.hoogte + 15).contains(eventX, eventY))
			zoomInPressed = true;
			
		if (zoomInTabel && 
			new Rectangle(zoomUitKnop.xPos,zoomUitKnop.yPos - 10,zoomUitKnop.breedte + 4,
						  zoomUitKnop.hoogte + 15).contains(eventX, eventY))
			zoomUitPressed = true;
		
		
        taptime = System.currentTimeMillis();
        doubletap.add(taptime);
		super.mouseDownTouchStartAction(eventX, eventY);
	}
	
	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction()
	{	
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;
				
		//super.mouseReleased(e);
		if (isDoubleClick() && !zoomInPressed && !zoomUitPressed) 
		{
			
			if (labelPressed && !isStapel && !asv.alleenInvullen)
			{
				showLabelPopup();
			}
			
			else if (!isStapel && (pijlIn1 == null) && (pijlIn2 == null))
			{	
				if (tabel == null)
					showTekstPopup();
				else
				{
					boolean raak = new Rectangle(xPos, yPos, breedte, hoogte - 152).contains(startx, starty);
					if (raak)
						showTekstPopup();
				}
			
			}
            doubletap.clear();
        } 
		else if (isLongClick() && !zoomInPressed && !zoomUitPressed) 
		{
			if (!dragging && !asv.alleenInvullen)
			{
				if (tabel == null)
					showPopupMenu();
				else
				{
					boolean raak = new Rectangle(xPos, yPos, breedte, hoogte - 152).contains(startx, starty);
					if (raak)
						showPopupMenu();
				}
				doubletap.clear();
			}
        } 
		else 
		{
            if (doubletap.size() >= 2) 
            {	//doubletap.clear();
            	doubletap.remove(0);
            }
            if (zoomInPressed)
            	zoomInTabelAction();
            if (zoomUitPressed)
            	zoomUitTabelAction();
        }
		super.mouseUpTouchEndAction();
		labelPressed = false;
		zoomInPressed = false;
		zoomUitPressed = false;

	}
	
	public void menuAction(String s)
	{
		
		if (s.equals("label"))
		{	if (labelItem.getText().equals("toon label"))
			{
				labelItem.setText("verberg label");
				toonLabel(true);
			}
			else if (labelItem.getText().equals("verberg label"))
			{
				labelItem.setText("toon label");
				toonLabel(false);
			}
		}
		
		else if (s.equals("tabel"))
		{	
			
			if (tabelItem.getText().equals("toon tabel"))
			{
				tabelItem.setText("verberg tabel");
				toonTabel(true,true);
			}
			else if (tabelItem.getText().equals("verberg tabel"))
			{
				tabelItem.setText("toon tabel");
				toonTabel(false,false);
			}
			
		}
		else if (s.equals("ketting"))
		{
			if (kettingItem.getText().equals("toon ketting"))
			{
				kettingItem.setText("verberg ketting");
				zetBoomZichtbaarHier(true);
				//zoomInKnop.setVisible(false);
				//zoomUitKnop.setVisible(false);
			}
			else if (kettingItem.getText().equals("verberg ketting"))
			{
				kettingItem.setText("toon ketting");
				zetBoomZichtbaarHier(false);
				//zoomInKnop.setVisible(true);
				//zoomUitKnop.setVisible(true);
			}
		}
		menuPopup.setVisible(false);
	}
	
	class MenuCommand implements Command
	{
		String cmdString = "";
		
		public MenuCommand(String s)
		{
			cmdString = s;
		}
		public void execute()
		{
			menuAction(cmdString);
		}
	}
	
	
	class TextBoxKeyDownHandler implements KeyDownHandler
	{
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
//System.out.println("enter");
				zetInvulWaarde();

			}
		}
	}

}
