package fi.algebrapijlengwt.client;

import java.util.HashMap;

import fi.algebrapijlengwt.client.expressies_ap.*;

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
	TekstPopup tf, label;
	LayoutPanel inputOwner; 
	String tfString = "";
	
	private Expressie expressie;
	private Expressie verborgenExpressie;
	private BasisExpressie beginw;
	private String waardeString;
	private boolean toonWaarde;
	private boolean labelZichtbaar;

	String labelTekst = "";
	boolean labelPressed = false;
		
	//private InUitvoerLabel label;
		
	boolean tabelZichtbaar;
	private boolean tabelAan;
	
	boolean zoomInTabel;
	
	private boolean grafiek;
	boolean muisrechts;

	
	TabelComponent tabel;
	
	private int tabelCorr;

	String fontString = "12px sans-serif";
	int fontSize = 12;
	
	PopupPanel menuPopup;
	MenuBar menuBar;
	MenuItem labelItem, tabelItem, kettingItem;
	
	
//GWT	
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
	String defaultVarnaam = "qq"+1000*Math.random();
	
	private CssColor vakKleur, vakKleurSoft;
	
	boolean isBeginExpressie = false;
	
	public UitvoerSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(1,asv,x,y,b,h);
		
//GWT niet bij AlgebraPijlen alleen by AlgebraExpressies	
		//toonWaarde = !((AlgebraSchuifVeld) schuifveld).ip.isExpr();

		toonWaarde = true;
		
		labelZichtbaar = false;
		tabelZichtbaar = false;
		
		zoomInTabel = true;
		
		waardeString = "";
		
//GWT		
		//label = new InUitvoerLabel();
		
//niet hier		
		//tabel = new TabelComponent();
		//tabel.setDefaultVarnaam(defaultVarnaam);
		
		inputOwner = asv.owner.canvasPanel;
		
		menuBar = new MenuBar(true);
		labelItem = new MenuItem("toon label", new MenuCommand("label"));
		tabelItem = new MenuItem("toon tabel", new MenuCommand("tabel"));
		kettingItem = new MenuItem("verberg ketting", new MenuCommand("ketting"));
		menuBar.addItem(labelItem);
		menuBar.addItem(tabelItem);
		menuBar.addItem(kettingItem);
		
		verborgenExpressie = new BasisExpressie(defaultVarnaam);
	
//GWT
/*		
		if (!links)
		{	plusMinKnop = new PlusMinKnop(b-12,1,10,h-2, PlusMinKnop.VERTIKAAL);
		}
		else
		{	plusMinKnop = new PlusMinKnop(b-22,1,10,h-2, PlusMinKnop.VERTIKAAL);
		}
		plusMinKnop.addActionListener(this);
		add(plusMinKnop);
*/		
	

		
		zoomInKnop	= new ZoomKnop("zoomintabel", xPos + 10, yPos + 50, 10, 10, asv.asvContext2d);
		//zoomInKnop.setBounds(10,50,10,25);
		//zoomInKnop.addActionListener(this);
		//add(zoomInKnop);
		
		zoomUitKnop	= new ZoomKnop("zoomuittabel", xPos + 10, yPos + 100, 10, 10, asv.asvContext2d);
		//zoomUitKnop.setBounds(10,100,10,25);
		//zoomUitKnop.addActionListener(this);
		//add(zoomUitKnop);
		
	}
	
	public void showPopupMenu()
	{
		int popupX = xPos + breedte + inputOwner.getAbsoluteLeft();
		int popupY = yPos + inputOwner.getAbsoluteTop();
		menuPopup = new PopupPanel(true);
		if (labelZichtbaar)
			labelItem.setText("verberg label");
		else
			labelItem.setText("toon label");
		if (tabelZichtbaar)
			tabelItem.setText("verberg tabel");
		else
			tabelItem.setText("toon tabel");
		if (kettingZichtbaar)
			kettingItem.setText("verberg ketting");
		else
			kettingItem.setText("toon ketting");
	
		
		menuPopup.setWidget(menuBar);
		menuPopup.setPopupPosition(popupX, popupY);
		menuPopup.show();
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
	
	public HashMap<String,Object> getState()
	{	String basisExp  = null;
		String defaultVarnaam = null;
		boolean tabelAan = false;
		boolean labelZichtbaar = false;
        boolean kettingZichtbaar = true;
		String labelTekst = null;
		
		if (beginw != null)
			basisExp = this.beginw.basisString;
		else 
			basisExp = "";
		defaultVarnaam = this.defaultVarnaam;
		tabelAan = this.tabelAan;
		labelZichtbaar = this.labelZichtbaar;
        kettingZichtbaar = this.kettingZichtbaar;
        labelTekst = this.labelTekst;
        
//GWT        
		//labelTekst = label.geefTekst();
		
		HashMap<String,Object> h = super.getState();
		
	    h.put("basisExp", basisExp);
	    h.put("defaultVarnaam", defaultVarnaam);
	    h.put("tabelAan", new Boolean(tabelAan));
	    h.put("labelZichtbaar", new Boolean(labelZichtbaar));
        h.put("kettingZichtbaar", new Boolean(kettingZichtbaar));
        h.put("labelTekst", labelTekst);
        
        h.put("scrollable", new Boolean(scrollable));
        h.put("zoomInTabel", new Boolean(zoomInTabel));
        
        h.put("isBeginExpressie", new Boolean(isBeginExpressie));
        if (isBeginExpressie)
        	h.put("beginExpString", expressie.toString());
        
	    return h;
	}

    public void setState(HashMap<String,Object> h)
    {	String basisExp  = null;
   		String defaultVarnaam = null;
        boolean tabelAan = false;
        boolean labelZichtbaar = false;
        boolean kettingZichtbaar = true;
        String labelTekst = null;
 
        boolean scrollable = true;
        boolean zoomInTabel = true;
        
        boolean isBeginExpressie = false;
        String beginExpString = "";
        
    	if (h.containsKey("basisExp")) 
    		basisExp = (String) h.get("basisExp");
    	if (h.containsKey("defaultVarnaam")) 
    		defaultVarnaam = (String) h.get("defaultVarnaam");
        if (h.containsKey("tabelAan")) 
        	tabelAan = ((Boolean) h.get("tabelAan")).booleanValue();		
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
		zetTabelAan(tabelAan);
		zetLabel(labelZichtbaar);
		this.labelTekst = labelTekst;
		
        if (!kettingZichtbaar)
        	zetKettingZichtbaarHier(kettingZichtbaar);
        
        //zetScroll(scrollable);
        zetZoomInTabel(zoomInTabel);
        
        
        if (h.containsKey("isBeginExpressie")) 
        	isBeginExpressie = ((Boolean) h.get("isBeginExpressie")).booleanValue();
        this.isBeginExpressie = isBeginExpressie;
        if (h.containsKey("beginExpString"))
        	beginExpString = (String) h.get("beginExpString");
        if (isBeginExpressie && !beginExpString.equals(""))
        {
        	beginExpString = "$f" + beginExpString + "@";
        	Expressie beginExp = FormuleParser_ap.geefExpressie(beginExpString);
        	zetExpressie(beginExp);
        }

        super.setState(h);
		
		zetMaat();
		
    }
    
    
    public void zetScroll(boolean b)
	{	scrollable = b;
//System.out.println("zetScroll " + b);	
		zetVeranderd(20);
	
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
    
	public void zetLinks(boolean b)
	{	links = b;
//GWT	
		//label.zetLinks(b);
		
//GWT	
		//if(!links)tf.setBounds(12,0,35,20);
		//else tf.setBounds(2,0,35,20);
		
		
		for(int i=0 ; i<aantalPu ; i++)
		{	pijlUit[i].zetLinks(b);
			if(!links)
			{	//pijlUit[i].zetPlaats(getLocation().x + getSize().width+9 ,getLocation().y + 10 );
				pijlUit[i].zetPlaats(xPos + breedte+9 ,yPos + 10 );
			}
			else 
			{	//pijlUit[i].zetPlaats(getLocation().x - 10 ,getLocation().y + 10 );
				pijlUit[i].zetPlaats(xPos - 10 ,yPos + 10 );
			}
		}
		
//GWT
/*		
		if(!links)
		{	plusMinKnop.setLocation(getSize().width-12,1);
		}
		else
		{	plusMinKnop.setLocation(getSize().width-22,1);
		}
*/		
		
		//asv.tekenOpnieuw();

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
	 	
		if(color.toString().equals(CssColor.make(0, 0, 0).toString())) 
			vakKleur = null;
	}
	
	//public void paint(Graphics g)
	public void paint(Context2d g)
  	{ 	
		
		if (!visible)
			return;
		
//System.out.println("paint usc " + breedte + "," + hoogte);

		//Color achtergrondkleur = Color.white;
		CssColor achtergrondkleur = CssColor.make(255, 255, 255);
		
  		if (pijlIn1 != null)
  		{	//achtergrondkleur = new Color(220, 220, 220);
  			achtergrondkleur = CssColor.make(220, 220, 220);
  		}
		
		if (!links)
		{	//g.setColor(Color.gray);
			g.setFillStyle(CssColor.make(125, 125, 125));
			
			//g.fillRect(10, 0, getSize().width - 11, getSize().height - 1);
			g.fillRect(xPos + 10, yPos + 0, breedte - 11, hoogte - 1);
			
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			
			//g.drawRect(10, 0, getSize().width - 11, getSize().height - 1);
			g.strokeRect(xPos + 10, yPos + 0, breedte - 11, hoogte - 1);
						
			super.paint(g);

			int labelCorr = 0;
			tabelCorr = 0;
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
			
			//g.fillRect(12, labelCorr + 2, getSize().width - 15 - scrollCorr, getSize().height - labelCorr - tabelCorr - 5);
			g.fillRect(xPos + 12, yPos + labelCorr + 2, breedte - 15 - scrollCorr, hoogte - labelCorr - tabelCorr - 5);
			
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			//g.drawRect(12, labelCorr + 2, getSize().width - 15 - scrollCorr, getSize().height - labelCorr - tabelCorr - 5);
			g.strokeRect(xPos + 12, yPos + labelCorr + 2, breedte - 15 - scrollCorr, hoogte - labelCorr - tabelCorr - 5);
		
			//g.setFont(f);
			g.setFont(fontString);
			g.setFillStyle(CssColor.make(0,0,0));
			if (expressie != null && kettingZichtbaar)
			{	//expressie.zetMaat(fm);
				expressie.zetMaat(fontSize, ascContext2d);

				TextMetrics tm = g.measureText(waardeString);
				int stringWidth = (int) Math.round(tm.getWidth());
				
				if (toonWaarde && !Double.isNaN(expressie.geefWaarde().doubleValue()))
				{	//g.drawString(waardeString, 5+(getSize().width-scrollCorr-fm.stringWidth(waardeString))/2, 
					//			 getSize().height-tabelCorr-5);
					g.fillText(waardeString, xPos + 5+(breedte-scrollCorr-stringWidth)/2, 
							//yPos + hoogte - tabelCorr - 5);
							yPos + hoogte - tabelCorr - 5);
//System.out.println("tw");					
				}
				else 
				{	//expressie.teken(g, 5+(getSize().width-scrollCorr-expressie.breedte)/2, 
					//		           7 + (labelCorr+getSize().height-tabelCorr-15 - expressie.hoogte)/2);
					expressie.teken(g, xPos + 5 + (breedte - scrollCorr - expressie.breedte) / 2, 
							//yPos + 7 + (labelCorr + hoogte - tabelCorr - 15 - expressie.hoogte)/2);
							yPos + 7 + (labelCorr + hoogte - tabelCorr - 15 - expressie.hoogte)/2 + 10);
//verbeterd + 10					
//System.out.println("else tw " + scrollCorr);					
				}
			}
			else if (expressie != null && expressie.geefVarNaam() != null && 
					 !(expressie instanceof Functie) && !kettingZichtbaar)
			{	Expressie functie = new Functie(new BasisExpressie(expressie.geefVarNaam()),expressie);
				//functie.zetMaat(fm);
				functie.zetMaat(fontSize, ascContext2d);
//				functie.teken(g, 5+(getSize().width-scrollCorr-functie.breedte)/2, 
//						         7 + (labelCorr+getSize().height-tabelCorr-15 - functie.hoogte)/2);
				functie.teken(g, xPos + 5+(breedte - scrollCorr - functie.breedte)/2, 
						yPos + 7 + (labelCorr + hoogte - tabelCorr - 15 - functie.hoogte)/2 + 10);
//System.out.println("!f && !kz");				
			}
			
			if (labelZichtbaar)
			{
//System.out.println("paint labelTekst rechts " + labelTekst);				
				g.setFont(fontString);
				g.setFillStyle(CssColor.make(255,255,255));
				TextMetrics tm = g.measureText(labelTekst);
				int labelWidth = (int) Math.round(tm.getWidth());
				g.fillText(labelTekst, xPos + 5 + (breedte - scrollCorr - labelWidth) / 2, yPos + 15); 
			}

				
		}
		else // links
		{	//g.setColor(Color.gray);
			g.setStrokeStyle(CssColor.make(125,125,125));
		
			//g.fillRect(0,0,getSize().width-11,getSize().height-1);
			g.fillRect(xPos + 0,yPos + 0,breedte-11,hoogte-1);
			
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			//g.drawRect(0,0,getSize().width-11,getSize().height-1);
			g.strokeRect(xPos + 0,yPos + 0,breedte-11,hoogte-1);
			
			super.paint(g);
		
			int labelCorr = 0;
			tabelCorr = 0;
			if (labelZichtbaar)
				labelCorr = 20;
			if (tabelZichtbaar)
				tabelCorr = 152;
			
			if (vakKleur != null) 
			{	//g.setColor(vakKleur);
				g.setFillStyle(vakKleur);
			}
			else 
			{	//g.setColor(achtergrondkleur);
				g.setFillStyle(achtergrondkleur);
			}
			//g.fillRect(2,labelCorr+2,getSize().width-15-scrollCorr,getSize().height-labelCorr-tabelCorr-5);
			g.fillRect(xPos + 2,yPos + labelCorr+2,breedte-15-scrollCorr,hoogte-labelCorr-tabelCorr-5);
			
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			
			//g.drawRect(2,labelCorr+2,getSize().width-15-scrollCorr,getSize().height-labelCorr-tabelCorr-5);
			g.strokeRect(xPos + 2,yPos + labelCorr+2,breedte-15-scrollCorr,hoogte-labelCorr-tabelCorr-5);
		
			//g.setFont(f);
			g.setFont(fontString);
			g.setFillStyle(CssColor.make(0,0,0));
			
			if (expressie != null)
			{	//expressie.zetMaat(fm);
				expressie.zetMaat(fontSize, ascContext2d);
			
				TextMetrics tm = g.measureText(waardeString);
				int stringWidth = (int) Math.round(tm.getWidth());
				
				if (toonWaarde && !Double.isNaN(expressie.geefWaarde().doubleValue()))
				{	//g.drawString(waardeString, -5 + (getSize().width - scrollCorr - fm.stringWidth(waardeString)) / 2, 
					//		                   getSize().height - tabelCorr - 5);
					g.fillText(waardeString, xPos -5 + (breedte - scrollCorr - stringWidth) / 2, 
							yPos + hoogte - tabelCorr - 5);
				}	
				else 
				{	//expressie.teken(g, -5 + (getSize().width - scrollCorr - expressie.breedte) / 2, 
					//		           7 + (labelCorr+getSize().height - tabelCorr - 15 - expressie.hoogte)/2);
					expressie.teken(g, xPos -5 + (breedte - scrollCorr - expressie.breedte) / 2, 
							//yPos + 7 + (labelCorr+hoogte - tabelCorr - 15 - expressie.hoogte)/2);
							yPos + 7 + (labelCorr+hoogte - tabelCorr - 15 - expressie.hoogte)/2 + 10);
				}
			}	
			
			if (labelZichtbaar)
			{
				g.setFont(fontString);
				g.setFillStyle(CssColor.make(255,255,255));
				TextMetrics tm = g.measureText(labelTekst);
				int labelWidth = (int) Math.round(tm.getWidth());
				g.fillText(labelTekst, xPos -5 + (breedte - scrollCorr - labelWidth) / 2, yPos + 15); 
			}
		}	
		
		
	}
	
	public void zetToonWaarde(boolean b)
	{	toonWaarde = b;
		zetMaat();
	}
	
	public void zetLabel(boolean b)
	{	labelZichtbaar = b;
		if(b)
		{	
//GWT		
			
//asv.owner.logger.info("zetLabel");
			if (zoomInKnop.yPos == yPos+50)
				zoomInKnop.translate(0,20);
			if (zoomUitKnop.yPos == yPos+100)
				zoomUitKnop.translate(0,20);
			//add(label);
			zetMaat();
		}
		else
		{	
//GWT			
			//zoomInKnop.translate(0,-20);
			//zoomUitKnop.translate(0,-20);
			//remove(label);
			zetMaat();
		}
		if (!asv.owner.asvSetState)
			asv.tekenOpnieuw();
	}
	
	public void toonLabel(boolean b)
	{	labelZichtbaar = b;
		super.toonLabel(b);
		if(b)
		{	
//GWT		
			if (zoomInKnop.yPos == yPos+50)
				zoomInKnop.translate(0,20);
			if (zoomUitKnop.yPos == yPos+100)
				zoomUitKnop.translate(0,20);
			//add(label);
			zetMaat();
		}
		else
		{	
//GWT		
			if (zoomInKnop.yPos == yPos+70)
				zoomInKnop.translate(0,-20);
			if (zoomUitKnop.yPos == yPos+120)
				zoomUitKnop.translate(0,-20);
			//remove(label);
			zetMaat();
		}
		if (!asv.owner.asvSetState)
			asv.tekenOpnieuw();
	}
	
	public void toonTabel(boolean b)
	{	tabelZichtbaar = b;
		if(b)
		{	
			
			//add(tabel);
			tabel = new TabelComponent(asv);
			tabel.setDefaultVarnaam(defaultVarnaam);
			zetMaat();
			zetVeranderd(20);
		}
		else 
		{	
			
			//remove(tabel);
			tabel = null;
			zetMaat();
		}
		if (!asv.owner.asvSetState)
			asv.tekenOpnieuw();

	
		GrafiekComponent gc = vindGrafiekComponent();
		if (gc != null)
			gc.zetVeranderd(20);
	
		
	}
	
	public boolean isLabelZichtbaar()
	{	return labelZichtbaar;
	}
	

	public String geefLabelTekst()
	{	
		//return label.geefTekst();
//GWT
		return "";
	}
		
	
	public void setSize(int b, int h)
	{	
		breedte = b;
		hoogte = h;
//GWT?		
		//label.setSize(b-10,20);
		super.setSize(b,h);
	}
	
	public void zetMaat()
	{	
//System.out.println("zetMaat " + scrollCorr);		
		
		int b = AlgebraSchuifVeld.basisB + scrollCorr;
		int h = AlgebraSchuifVeld.basisH; //20;
		int corr = 0;
		Expressie expFunctie = expressie;
		if (expressie != null && expressie.geefVarNaam() != null && !(expressie instanceof Functie) && !kettingZichtbaar) 
		{	expFunctie = new Functie(new BasisExpressie(expressie.geefVarNaam()),expressie);
			//expFunctie.zetMaat(fm);
			expFunctie.zetMaat(fontSize,ascContext2d);
		}

		if (expressie != null)
		{	b = expFunctie.breedte + scrollCorr;
			h = expFunctie.hoogte;
			if (toonWaarde && !Double.isNaN(expressie.geefWaarde().doubleValue()))
			{	//b = fm.stringWidth(waardeString) + scrollCorr;
				TextMetrics tm = ascContext2d.measureText(waardeString);
				int stringWidth = (int) Math.round(tm.getWidth());
				b = stringWidth + scrollCorr;
				h = 0;
			}
//System.out.println("b = " + b);			
			if (b > 26)
			{	b = b + 24 + scrollCorr;
				//b = b + 44 + scrollCorr;
			}
			else 
			{	b =  AlgebraSchuifVeld.basisB + scrollCorr;
			}
			if (h > 12)
				h = 10+((h+5)/10)*10;
			else 
				h = AlgebraSchuifVeld.basisH;//20;
		}
		if (labelZichtbaar)
		{	corr = 20;
			h=h+20;
			
			TextMetrics tm = ascContext2d.measureText(labelTekst);
			int labelWidth = (int) Math.round(tm.getWidth());
//System.out.println("labelWidth = " + labelWidth);			
		
			b = Math.max(b,labelWidth+15);
			
		}
		else
		{
			
		}
		
		if (tabelZichtbaar)
		{	h=h+152;
	
			b = Math.max(b,tabel.geefBreedte()+10);
		}
		setSize(b,h);
		
//GWT		
		//label.setSize(b-10,20);
		
		if(!links)
		{	

			if (tabel != null)
				tabel.setBounds(xPos+20,yPos+h-152,b-10,152);
//GWT
/*			
			tf.setBounds(12, corr, b - 15 - scrollCorr, 20);
			plusMinKnop.setLocation(b-12,1+corr);
			zoomInKnop.setBounds(11,h-120,12,25);
			zoomUitKnop.setBounds(11,h-70,12,25);
*/			
			
		}
		else
		{	
			if (tabel != null)
				tabel.setBounds(xPos+10,yPos+h-152,b-10,152);
/*			
			tf.setBounds(2, corr, b - 15 - scrollCorr, 20);
			plusMinKnop.setLocation(b-22,1+corr);
			zoomInKnop.setBounds(b-1,h-120,12,25);
			zoomUitKnop.setBounds(b-1,h-70,12,25);
*/			
			
		}	

	}
	
	public void zetExpressie(Expressie e)
	{	expressie = e;
		if (e instanceof BasisExpressie) 
			beginw = (BasisExpressie) e;
		//expressie.zetMaat(fm);
		expressie.zetMaat(fontSize, ascContext2d);

		if (tabel != null)
			tabel.zetExpressie(expressie);
		
		zetMaat();
				
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
			
//GWT			
			//zoomInKnop.setVisible(false);
			//zoomUitKnop.setVisible(false);
			//asv.owner.kijkNa();
			
			for (int pCnt = 0; pCnt < pijlUit.length; pCnt++)
			{ 	if (pijlUit[pCnt] != null)
					pijlUit[pCnt].im = null;
			
			}
			if (asv.owner.kijkNaActief)
				asv.answerChanged();

		}
		else if (!isBeginExpressie)
		{	
			
			for (int pCnt = 0; pCnt < pijlUit.length; pCnt++)
			{ 	if (pijlUit[pCnt] != null)
					pijlUit[pCnt].im = null;
			
			}
			if (asv.owner.kijkNaActief)
				asv.answerChanged();
			
			if (scrollable && expressie != null && !Double.isNaN(expressie.geefWaarde().doubleValue()))
			{	if (scrollCorr == 0)
				{	
//GWT				
					//add(plusMinKnop);
				
				}
//System.out.println("scrll");

				scrollCorr = 10;
			}
			else
			{	scrollCorr = 0;
//GWT				
				//remove(plusMinKnop);
			}
			expressie = beginw;
			verborgenExpressie = new BasisExpressie(defaultVarnaam);
//GWT			
			//zoomInKnop.setVisible(true);
			//zoomUitKnop.setVisible(true);
		}
		
		if (!kettingZichtbaar)
		{	
//GWT			
			//zoomInKnop.setVisible(true);
			//zoomUitKnop.setVisible(true);
		}

//is dit nodig?		
		//zetTabelAan(tabelAan);
		
  		if (tabel != null)
  		{
			if (expressie != null && expressie.geefVarNaam() != null)
				tabel.zetExpressie(expressie);
			else 
				tabel.zetExpressie(verborgenExpressie);
		}		
		
		if (expressie != null)
		{	//expressie.zetMaat(fm);
			expressie.zetMaat(fontSize, ascContext2d);
			Double waarde = expressie.geefWaarde();
			if (!Double.isNaN(waarde.doubleValue()))
			{	//waardeString = Expressie.df.format(waarde);
				waardeString = UF.format0(waarde,3);
			}
			else 
				waardeString = "-";
		}
		zetMaat();
		
// is dit nodig?		
		//zetTabelAan(tabelAan);
		
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
	

	
	
	
	public void setZoomState(String varnaam, ZoomState zoomState)
	{	if(expressie!=null && expressie.geefVarNaam()!=null && expressie.geefVarNaam().equals(varnaam)
			|| verborgenExpressie!=null && verborgenExpressie.geefVarNaam()!=null && verborgenExpressie.geefVarNaam().equals(varnaam))
		{	this.beginwaarde = zoomState.getBeginwaarde();
			this.selectnummer = zoomState.getSelectnummer();
			this.schaalFactorX = zoomState.getSchaalFactorX();
			this.factorRijNummerX = zoomState.getFactorRijNummerX();
			this.beginx = zoomState.getBeginx();
			
			if (tabel != null)
			{	tabel.zetTabel(beginwaarde, selectnummer, varnaam, schaalFactorX, beginx);

			}
			zetMaat();
		}
		
	}
	
	public void zetTabelAan(boolean b)
	{	tabelAan = b;
		if (!isStapel) 
			toonTabel(b);
	}
	
	public void zetLabelTekst()
	{
		
//System.out.println("zetLabelTekst " + label.getText());		
		labelTekst = label.getText();
		label.setVisible(false);
		inputOwner.remove(label);
		
		zetMaat();
		
		paint();
	}
	
	public void zetInvulWaarde()
	{
		
		ZoomState zs = null;
		if (expressie != null && expressie.geefVarNaam() != null && asv != null)
		{	zs = asv.zoomStateHolder.getZoomState(expressie.geefVarNaam());

		}
		else if (verborgenExpressie != null && verborgenExpressie.geefVarNaam() != null && asv != null )
		{	zs = asv.zoomStateHolder.getZoomState(verborgenExpressie.geefVarNaam());

		}
				
		boolean isGeldigeInvoer = true;
		{	
			
			try
			{	
			
				String s = tf.getText();
				//String s = "2";
				
				s = s.replace(',','.');
				
				tf.setText(s);
				Double w = Double.valueOf(tf.getText());

				toonTabel(false);
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
		else if (zs != null) 
			asv.zoomStateHolder.copyZoomState(defaultVarnaam, zs);

		tf.setVisible(false);
		inputOwner.remove(tf);
		
		zetMaat();
		zetVeranderd(20);
		
		asv.tekenOpnieuw();
	}
  
    public void zetKettingZichtbaarHier(boolean b)
    {   
    	if (isBeginExpressie)
    		return;
    	
    	if (pijlIn1 != null)
    		pijlIn1.zender.zetKettingZichtbaar(b);
        open = b;
        kettingZichtbaar = b;
        
        if (tabel != null)
        	tabel.zetDubbel(!b);
        
        zetMaat();
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
        //if (!asv.owner.asvSetState)
        	asv.tekenOpnieuw();
    	
    }
	
/*
    public void actionPerformed(ActionEvent e)
	{	if (e.getActionCommand().equals("focus")) 
			schuifveld.tekenOpnieuw();
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
            
//System.out.println("vn = " + varnaam);            
            
            ((AlgebraSchuifVeld) getParent()).zoomStateHolder.setBeginwaarde(varnaam, beginwaarde);
            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setSelectnummer(varnaam, selectnummer);
            ((AlgebraSchuifVeld) getParent()).zoomStateHolder.setSchaalFactorX(varnaam, schaalFactorX);
            ((AlgebraSchuifVeld) getParent()).zoomStateHolder.setFactorRijNummerX(varnaam, factorRijNummerX);
            ((AlgebraSchuifVeld) getParent()).zoomStateHolder.setBeginx(varnaam, beginx);
            ((AlgebraSchuifVeld) getParent()).zoomStateHolder.setZoomStates(varnaam);
            
            schuifveld.tekenOpnieuw();
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
		else if(((JMenuItem)e.getSource()).getText().equals(AlgebraPijlenOpdr.rb.getString("popup1Label1")))
		{	toonLabel(true);
		}
		else if(((JMenuItem)e.getSource()).getText().equals(AlgebraPijlenOpdr.rb.getString("popup1Label2")))
		{	toonLabel(false);
		}
		else if(((JMenuItem)e.getSource()).getText().equals(AlgebraPijlenOpdr.rb.getString("popup1Label3")))
		{	zetTabelAan(true);
		}
		else if(((JMenuItem)e.getSource()).getText().equals(AlgebraPijlenOpdr.rb.getString("popup1Label4")))
		{	zetTabelAan(false);
		}
		else if(((JMenuItem)e.getSource()).getText().equals(AlgebraPijlenOpdr.rb.getString("popup1Label5")))
		{	zetKettingZichtbaarHier(true);
			zoomInKnop.setVisible(false);
			zoomUitKnop.setVisible(false);
		}
		else if(((JMenuItem)e.getSource()).getText().equals(AlgebraPijlenOpdr.rb.getString("popup1Label6")))
		{	zetKettingZichtbaarHier(false);
			zoomInKnop.setVisible(true);
			zoomUitKnop.setVisible(true);
		}
	}
*/
	
	public boolean meldAan(Pijl p, int x, int y)
	{	if (isBeginExpressie)
			return false;
		else 
			return super.meldAan(p, x, y);
	}
	
	
	
	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	if (asv.fixed)
			return;
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;		
		
		//requestFocus();
		muisrechts = false;

		press = true;
		
		if (labelZichtbaar && new Rectangle(xPos,yPos,breedte,20).contains(eventX, eventY))
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
        
        if (!zoomInPressed || zoomUitPressed)
        	super.mouseDownTouchStartAction(eventX, eventY);
	}
	
	
	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction()
	{	if (asv.fixed)
			return;
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
			
			else if (!isStapel && !isBeginExpressie && pijlIn1 == null)
			{	if (tabel == null)
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
			if (!dragging && !isBeginExpressie && !asv.alleenInvullen)
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
		{	if (tabelItem.getText().equals("toon tabel"))
			{
				tabelItem.setText("verberg tabel");
				zetTabelAan(true);
			}
			else if (tabelItem.getText().equals("verberg tabel"))
			{
				tabelItem.setText("toon tabel");
				zetTabelAan(false);
			}
		}
		else if (s.equals("ketting"))
		{
			if (kettingItem.getText().equals("toon ketting"))
			{
				kettingItem.setText("verberg ketting");
				zetKettingZichtbaarHier(true);
				//zoomInKnop.setVisible(false);
				//zoomUitKnop.setVisible(false);
			}
			else if (kettingItem.getText().equals("verberg ketting"))
			{
				kettingItem.setText("toon ketting");
				zetKettingZichtbaarHier(false);
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
	
/*	
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
*/	
		
}
