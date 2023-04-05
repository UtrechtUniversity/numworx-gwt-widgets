package fi.algebrapijlengwt.client;

import fi.algebrapijlengwt.client.expressies_ap.*;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * Een klasse die een Tabel visualiseert; een Tabel "hangt" altijd aan een UitvoerSchuifComponent (zie aldaar)
 * en heeft een of twee kolommen (twee kolommen indien de UitvoerSchuifComponent de enigste zichtbare compoment
 * van de pijlenketting is, zie aldaar); via twee pijlknopjes kan in de Tabel naar boven of naar beneden
 * gescolld worden, waarbij dan hetzelfde gebeurt in de andere Tabellen in de pijlenketting; 
 * het is ook mogelijk te scollen door in de Tabel naar boven of beneden te slepen, waarbij dan ook hetzelfde gebeurt
 * in de andere Tabellen in de pijlenketting; <br>
 * er kan een waarde in de Tabel geselecteerd worden, waarna de corresponderende waarde(n) in de andere
 * Tabellen in de pijlenketting ook geselecteerd wordt(en); ook kan er optioneel in- of uitgezoomd worden in een
 * Tabel, waarbij dan hetzelfde gebeurt met de andere Tabellen in de pijlenketting;     
 * NB: de klasse Tabel tekent alleen de witte tabelkolom(men) met waarden, de eventueel geselecteerde waarde en
 * de scroll-pijlen (if any); de achtegrond onstaat door de UitvoerSchuifComponent groter te maken (zie aldaar).  
 */

public class TabelComponent extends SchuifComponent 
{
    /**
     * pijltekening voor scoll naar boven 	
     */
	private Polygon pijlPlus;
	/**
	 * pijltekening voor scoll naar beneden
	 */
	private Polygon pijlMin;
	/**
	 * klikken op de pijl omhoog 
	 */
	Rectangle pijlPlusContain;
	/**
	 * klikken op de pijl omlaag
	 */
	Rectangle pijlMinContain;
	/**
	 * de Expressie voor de waarden in de Tabel
	 */
	private Expressie exp;
	/**
	 * is selecteren mogelijk? 
	 */
	private boolean selectMogelijk;
	/**
	 *	de nummer van de geselecteerde tabelwaarde 
	 */
	private int selectnummer;
	/**
	 * de beginwaarde voor het berekenen van de tabelwaarden:
	 * deze zijn exp.geefWaarde(schaalFactorX*(i+beginwaarde)) voor i=0,...,7
	 */
	private int beginwaarde;
	/**
	 * de schaalfactor voor de Tabel
	 */
	private double schaalFactorX;
	/**
	 * de invoerbreedte van de tabel, zie geefBreedte()
	 */
	private int breedteInv;
	/** 
	 * de invoerbreedte van de tabel, zie geefBreedte()
	 */
	private int breedteUitv;
	/**
	 * de variabele uit de Tabel-Expressie
	 */
	private String varNaam;
	/**
	 * heeft deze Tabel twee kolommen
	 */
	private boolean dubbel;
	/**
	 * t.b.v. slepen in de Tabel, zie aldaar
	 */
	private int startY;
	/**
	 * t.b.v. slepen in de Tabel, zie aldaar
	 */
	private int beginx;
	/**
	 * t.b.v. slepen in de Tabel, zie aldaar
	 */
	private int eenheidx = 14;
	/**
	 * t.b.v. slepen in de Tabel, zie aldaar
	 */
	private boolean raak = false;
	/**
	 * de variablenaam als de Tabel-Expressie een BasisExpressie is  
	 */
	private String defaultVarnaam;
	/**
	 * de kleur voor de geselecteerde waarde 
	 */
	private CssColor traceKleur = CssColor.make(220,220,220);
	/**
	 * het werkveld 
	 */
	AlgebraSchuifVeld asv;
	/**
	 * is deze TabelComponent zichtbaar? 
	 */
	boolean visible = true;

	/**
	 * constructor
	 * @param asv het AlgebraSchuifVeld
	 */
	public TabelComponent(AlgebraSchuifVeld asv)
	{	this.asv = asv;
		beginwaarde = 0;
		selectnummer = 999;
		schaalFactorX = 1;
		selectMogelijk = true;
		dubbel = false;
		if (dubbel)
			breedteInv = 20;
		else 
			breedteInv = 30;
		breedteUitv = 33;
	}

	/**
	 * zet de afmetingen van deze TabelComponent
	 * @param x x-positie
	 * @param y y-positie
	 * @param b breedte
	 * @param h hoogte
	 */
	public void setBounds(int x, int y, int b, int h)
	{	xPos = x;
		yPos = y;
		breedte = b;
		hoogte = h;
	}
	
	/**
	 * bevat deze TabelCompoment het punt (x,y)
	 * @param x x-coordinaat punt
	 * @param y y-coordinaat punt
	 * @return true/false
	 */
	public boolean contains(int x, int y)
	{	return (new Rectangle(xPos,yPos,breedte,hoogte).contains(x, y));
	}
	
	/**
	 * zet de defaultVarnaam (en de Tabel-expressie)
	 * @param s defailt variable naam
	 */
	public void setDefaultVarnaam(String s)
	{	defaultVarnaam = s;
		exp = new BasisExpressie(defaultVarnaam);
	}
	
	public void paint()
	{	paint(asv.asvContext2d);
	}
	
	/**
	 * teken de tabel
	 * @param g de Context2d
	 */
	public void paint(Context2d g)
	{	if (!visible)
			return;
		// 2-kolommen
		if (dubbel)
		{	breedteUitv = breedte-10-breedteInv;
			// kolom 2, wit plus zwarte rand
			g.setFillStyle(CssColor.make(255,255,255));
			g.fillRect(xPos+breedteInv+6,yPos+15,breedteUitv-10,hoogte - 31);
			g.setStrokeStyle(CssColor.make(0,0,0));
			g.strokeRect(xPos+breedteInv+6,yPos+15,breedteUitv-10,hoogte - 31);
			// kolom 1, wit plus zwarte rand
			g.setFillStyle(CssColor.make(255,255,255));
			g.fillRect(xPos+3,yPos+15,breedteInv,hoogte - 31);
			g.setStrokeStyle(CssColor.make(0,0,0));
			g.strokeRect(xPos+3,yPos+15,breedteInv,hoogte - 31);
			// grijs doosje variabelenaam boven kolom 1
			g.setFillStyle(CssColor.make(220,220,220));
			g.fillRect(xPos+3,yPos,breedteInv,15);
			g.setStrokeStyle(CssColor.make(0,0,0));
			g.strokeRect(xPos+3,yPos,breedteInv,15);
			// achtergrond en rand geslecteerde waarde (if any)
			if(selectMogelijk && selectnummer>-1 && selectnummer<8 && exp!=null && !exp.geefVarNaam().equals(""))
			{	g.setFillStyle(traceKleur);
				g.fillRect(xPos+breedteInv+6,yPos+15+selectnummer*15+beginx%eenheidx,breedteUitv-10,16);
				g.fillRect(xPos+3,yPos+15+selectnummer*15+beginx%eenheidx,breedteInv,16);
				g.setStrokeStyle(CssColor.make(0,0,0));
				g.strokeRect(xPos+breedteInv+6,yPos+15+selectnummer*15+beginx%eenheidx,breedteUitv-10,16);
				g.strokeRect(xPos+3,yPos+15+selectnummer*15+beginx%eenheidx,breedteInv,16);
			}
			// scroll-knoppen
			int knopPlusX = xPos+38;
			int knopPlusY = yPos+4;
			int knopMinX = xPos+38;
			int knopMinY = yPos+hoogte-5;
			// klikrechthoek omhoog
			pijlPlusContain = new Rectangle(knopPlusX-10,knopPlusY-4,20,12);
			// polygon voor omhoog	
			pijlPlus = new Polygon();
			pijlPlus.addPoint(knopPlusX-5,knopPlusY+8);
			pijlPlus.addPoint(knopPlusX+5,knopPlusY+8);
			pijlPlus.addPoint(knopPlusX,knopPlusY);
			g.setFillStyle(CssColor.make(0,0,0));
	       	g.moveTo(pijlPlus.doubleX[0], pijlPlus.doubleY[0]);
			g.beginPath();
			for (int k = 1; k < pijlPlus.aantalPunten; k++)
			{	g.lineTo(pijlPlus.doubleX[k], pijlPlus.doubleY[k]);
			}
			g.lineTo(pijlPlus.doubleX[0], pijlPlus.doubleY[0]);
			g.closePath();
			g.fill();
			// klikrechthoek omlaag
			pijlMinContain = new Rectangle(knopMinX-10,knopMinY-8,20,12);
			// polygon omlaag
			pijlMin = new Polygon();
			pijlMin.addPoint(knopMinX-5,knopMinY-8);
			pijlMin.addPoint(knopMinX+5,knopMinY-8);
			pijlMin.addPoint(knopMinX,knopMinY);
	       	g.moveTo(pijlMin.doubleX[0], pijlMin.doubleY[0]);
			g.beginPath();
			for (int k = 1; k < pijlMin.aantalPunten; k++)
			{	g.lineTo(pijlMin.doubleX[k], pijlMin.doubleY[k]);
			}
			g.lineTo(pijlMin.doubleX[0], pijlMin.doubleY[0]);
			g.closePath();
			g.fill();
			// 
			if(exp!=null)
			{	String s = exp.geefVarNaam();
				// skip verborgen variabelen
				boolean b = s.length() >= 2 && s.substring(0,2).equals("qq");
				if(s!=null && !s.equals(""))
				{	if (!b)					
					{	g.fillText(s,xPos+7,yPos+12);
					}
					for(int i=0 ; i<8 ; i++)
					{	// valide waarde kolom 2
						if(exp.isWaarde(schaalFactorX*(i+beginwaarde)))
						{	double d = exp.geefW(schaalFactorX*(i+beginwaarde));
							if(i<7 && beginx>0 || i>0 && beginx<0 ||beginx%eenheidx==0) 
							{	g.fillText(UF.format0(d, 3),xPos+breedteInv+8,yPos+28+i*15+beginx%eenheidx);
							}
						}
						else // waarde niet voorhanden 
						{	g.fillText("-",xPos+breedteInv+8,yPos+28+i*15+beginx%eenheidx);
						
						}
						// kolom 1
						if(i<7 && beginx>0 || i>0 && beginx<0 ||beginx%eenheidx==0)
						{	g.fillText(UF.format0(schaalFactorX*(i+beginwaarde), 3),xPos+5,yPos+28+i*15+beginx%eenheidx);
						}	
					}
				}
			}
		}
		else // !dubbel, i.e. een(1) kolom
		{	breedteUitv = breedte - 10;
			// witte tabelrechthoek
			g.setFillStyle(CssColor.make(255,255,255));
			g.fillRect(xPos+3, yPos+15, breedte - 17, hoogte - 31);
			// outlined in zwart
			g.setStrokeStyle(CssColor.make(0,0,0));
			g.strokeRect(xPos+3, yPos+15, breedte - 17, hoogte - 31);
			// kijk of er een zichtbare waarde geselecteerd is (de laatste twee condities zijn overbodig, aangezien
			// setDefaultVarnaam wordt aangeroepen bij creatie van de Tabel) 
			if (selectMogelijk && selectnummer > -1 && selectnummer < 8 && exp != null && !exp.geefVarNaam().equals(""))
			{	// traceKleur rechthoek
				g.setFillStyle(traceKleur);
				if (selectnummer < 7 && beginx > 0 || selectnummer > 0 && beginx < 0 || beginx % eenheidx == 0)
					g.fillRect(xPos+3, yPos+15 + selectnummer * 15 + beginx % eenheidx, breedte - 17, 16);
				// outlined in zwart
				g.setStrokeStyle(CssColor.make(0,0,0));
				if (selectnummer < 7 && beginx > 0 || selectnummer > 0 && beginx < 0 || beginx % eenheidx == 0)
				{	g.strokeRect(xPos+3, yPos+15 + selectnummer * 15 + beginx % eenheidx, breedte - 17, 16);
				}
			}
			// klikrechthoek pijl omhoog
			pijlPlusContain = new Rectangle(xPos+breedteUitv/2-25,yPos+0,50, 12);
			// pijl omhoog	
			pijlPlus = new Polygon();
			pijlPlus.addPoint(xPos+breedteUitv/2-5,yPos+12);
			pijlPlus.addPoint(xPos+breedteUitv/2+5,yPos+12);
			pijlPlus.addPoint(xPos+breedteUitv/2,yPos+4);
			// teken pijl omhoog in zwart
			g.setFillStyle(CssColor.make(0,0,0));
	       	g.moveTo(pijlPlus.doubleX[0], pijlPlus.doubleY[0]);
			g.beginPath();
			for (int k = 1; k < pijlPlus.aantalPunten; k++)
			{	g.lineTo(pijlPlus.doubleX[k], pijlPlus.doubleY[k]);
			}
			g.lineTo(pijlPlus.doubleX[0], pijlPlus.doubleY[0]);
			g.closePath();
			g.fill();
			// klikrechthoek pijl omlaag
			pijlMinContain = new Rectangle(xPos+breedteUitv/2-25,yPos+hoogte-12,50, 12);
			// pijl omlaag
			pijlMin = new Polygon();
			pijlMin.addPoint(xPos+breedteUitv/2-5,yPos+hoogte-13);
			pijlMin.addPoint(xPos+breedteUitv/2+5,yPos+hoogte-13);
			pijlMin.addPoint(xPos+breedteUitv/2,yPos+hoogte-5);
			// teken pijl omlaag in zwart			
	       	g.moveTo(pijlMin.doubleX[0], pijlMin.doubleY[0]);
			g.beginPath();
			for (int k = 1; k < pijlMin.aantalPunten; k++)
			{	g.lineTo(pijlMin.doubleX[k], pijlMin.doubleY[k]);
			}
			g.lineTo(pijlMin.doubleX[0], pijlMin.doubleY[0]);
			g.closePath();
			g.fill();
			// vul de waarden in in de Tabel, condities exp != null, s != null en !s.equals("") zijn overbodig?   
			if (exp != null)
			{	String s = exp.geefVarNaam();
				if (s != null && !s.equals(""))
				{	for (int i = 0; i < 8; i++)
					{	// valide waarde
						if (exp.isWaarde(schaalFactorX * (i + beginwaarde)))
						{	double d = exp.geefW(schaalFactorX * (i + beginwaarde));
							if (i < 7 && beginx > 0 || i > 0 && beginx < 0 || beginx % eenheidx == 0)
							{	g.fillText(UF.format0(d,3), xPos+8, yPos+28 + i * 15 + beginx % eenheidx);
							}	
						}
						else // waarde niet voorhanden 
						{	g.fillText("-", xPos+8, yPos+28 + i * 15 + beginx % eenheidx);
						}
					}
				}
			}
		}
	}
	
	/**
	 * maak van de TabelCompont een 2-koloms tabel
	 * @param b 2-koloms/een kolom
	 */
	public void zetDubbel(boolean b)
	{	dubbel = b;
	}
	
	/**
	 * zet/reset de Expressie van deze TabelComponenet
	 * @param e de Expressie of null
	 */
	public void zetExpressie(Expressie e)
	{	if (e != null && e.geefVarNaam() != null)
		{	exp = e;
			varNaam = e.geefVarNaam();
		}
		else // reset;
		{	exp = new BasisExpressie(defaultVarnaam); 
			varNaam = defaultVarnaam;
		}
	}
	
	/**
	 * zet de attributen van eze TabelComponent indien de variabelenaam van de Tabel-Expressie gelijk is aan varN
	 * @param beginwaarde beginwaarde 
	 * @param selectnummer te selecteren entry (if any)
	 * @param varN de variabelenaam van de attributen
	 * @param schaalFactorX schaalfactor
	 * @param beginx beginwaarde voor slepen
	 */
	public void zetTabel(int beginwaarde, int selectnummer, String varN, double schaalFactorX, double beginx)
	{	varNaam = exp.geefVarNaam();
		if (varNaam.equals(varN))
		{	this.beginwaarde = beginwaarde;
			this.selectnummer = selectnummer;
			this.schaalFactorX = schaalFactorX;
			this.beginx = (int) Math.round(beginx);
			paint();
		}
	}

	/**
	 * kan in de TabelComponent een waarde geselecteerd worden?
	 * @param b ja/nee
	 */
	public void zetSelectMogelijk(boolean b)
	{	selectMogelijk = b;
	}

	/**
	 * bepaal de gewenste breedte van de TabelCompoment
	 * @return de breedte
	 */
	public int geefBreedte()
	{	// tabel met twee kolommen
		if (dubbel)
		{	breedteInv = 20;
			breedteUitv = 23;
			if (exp != null)
			{	varNaam = exp.geefVarNaam();
				if (varNaam != null && !varNaam.equals(""))
				{	// skip verborgen variabelen
					boolean b = varNaam.equals("qq") || varNaam.length() > 2 && varNaam.substring(0,2).equals("qq");
					// breedte naam kolom 1
					if(!b)
					{	TextMetrics tm = asv.asvContext2d.measureText(varNaam);
						int stringWidth = (int) Math.round(tm.getWidth());
						breedteInv = Math.max(breedteInv, stringWidth + 4);
					}
					// maximale breedte alle entires in kolom 1 en kolom 2
					for (int i = 0; i < 8; i++)
					{	if (exp.isWaarde(schaalFactorX * (i + beginwaarde)))
						{	double d = exp.geefW(schaalFactorX * (i + beginwaarde));
							String sUitv = UF.format0(d,3);
							TextMetrics tm = asv.asvContext2d.measureText(sUitv);
							int stringWidth = (int) Math.round(tm.getWidth());
							breedteUitv = Math.max(breedteUitv, stringWidth + 4);
						}
						String sInv = UF.format0(schaalFactorX * (i + beginwaarde),3);
						TextMetrics tm = asv.asvContext2d.measureText(sInv);
						int stringWidth = (int) Math.round(tm.getWidth());
						breedteInv = Math.max(breedteInv, stringWidth + 4);
					}
				}
			}
			int b = breedteInv + breedteUitv + 20;
			return b;	
		}
		else // enkelvoudige tabel
		{	breedteUitv = 30;
			if (exp != null)
			{	varNaam = exp.geefVarNaam();
				if (varNaam != null && !varNaam.equals(""))
				{	// bepaal de maximale breedte van alle zichtbare tabelgetallen				
					for (int i = 0; i < 8; i++)
					{	if (exp.isWaarde(schaalFactorX * (i + beginwaarde)))
						{	double d = exp.geefW(schaalFactorX * (i + beginwaarde));
							String sUitv = UF.format0(d,3);
							TextMetrics tm = asv.asvContext2d.measureText(sUitv);
							int stringWidth = (int) Math.round(tm.getWidth());
							breedteUitv = Math.max(breedteUitv, stringWidth + 4);
						}
					}
				}
			}
			int b = breedteUitv + 20;
			return b;	
		}
	}

	/**
	 * mouseDown/touchStart Event at (eventX,eventY) 
	 * @param eventX x-coordinaat
	 * @param eventY y-coordinaat
	 */
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		startY = eventY;
		// event in tabelrechthoek?
		raak = (new Rectangle(xPos + 4, yPos + 17, breedte - 9, 115)).contains(eventX,eventY);
		// event op pijl omhoog
		if (pijlPlusContain.contains(eventX,eventY) && !asv.isDemo)
		{	beginwaarde--;
			selectnummer++;
			asv.tekenOpnieuw();
		}
		// event op pijl omlaag
		else if (pijlMinContain.contains(eventX,eventY) && !asv.isDemo)// && !asv.frozen)
		{	beginwaarde++;
			selectnummer--;
			asv.tekenOpnieuw();
		}
		// initieer beginx
		beginx = -beginwaarde * eenheidx;
	}	
	
	/**
	 * mouseMove/touchMove Event at (eventX,eventY) 
	 * @param eventX x-coordinaat
	 * @param eventY y-coordinaat
	 */
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	if (raak)
		{	int ddy =  eventY - startY;
			beginx = beginx + ddy;
			int b = beginwaarde;
			beginwaarde = -(int) Math.round(beginx / eenheidx);
			selectnummer = selectnummer + b - beginwaarde;
			startY = eventY; 
			// save zoom parameters voor varNaam
			asv.zoomStateHolder.setSelectnummer(varNaam, selectnummer);
	        asv.zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
	        asv.zoomStateHolder.setBeginx(varNaam, beginx);
			// update alle UVS die varNaam bevatten
	        asv.zoomStateHolder.setZoomStates(varNaam);
	        asv.tekenOpnieuw();
		}
	}
	
	/**
	 * mouseUp/touchEnd Event at (eventX,eventY)
	 * @param eventX x-coordinaat
	 * @param eventY y-coordinaat
	 */
	public void mouseUpTouchEndAction(int eventX, int eventY)
	{	// zorg dat beginx weer een veelvoud van eenheidx wordt 
		int beginxOud = beginx;
		int b = beginwaarde;
		if (beginx > 0)
			beginx = (beginx + eenheidx / 2) / eenheidx * eenheidx;
		else 
			beginx = (beginx - eenheidx / 2) / eenheidx * eenheidx;
		beginwaarde = -(int) Math.round(beginx / eenheidx);
		selectnummer = selectnummer + b - beginwaarde;
		beginx = -beginwaarde * eenheidx;
		// niet gesleept, select/unselect
		if (beginx == beginxOud)
		{	for (int i = 0; i < 8; i++)
			{	if ((new Rectangle(xPos+4, yPos+17 + i * 15, breedte - 9, 15)).contains(eventX,eventY))
				{	if (selectnummer == i)
						selectnummer = 999;
					else 
						selectnummer = i;
				}
			}
		}
		// save zoom parameters voor varNaam
		asv.zoomStateHolder.setSelectnummer(varNaam, selectnummer);
		asv.zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
		asv.zoomStateHolder.setBeginx(varNaam, beginx);
		// update alle UVS die varNaam bevatten 
		asv.zoomStateHolder.setZoomStates(varNaam);
		asv.tekenOpnieuw();
	}
}
