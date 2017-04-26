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
	 
	 private String defaultVarnaam;
	 
	 //private Color traceKleur = new Color(220,220,220);
	 private CssColor traceKleur = CssColor.make(220,220,220);

	 AlgebraSchuifVeld asv;
	 
	boolean visible = true;
	 
	public TabelComponent(AlgebraSchuifVeld asv)
	{	
		
		this.asv = asv;
		
		//addMouseListener(this);
		//addMouseMotionListener(this);
		//f = new Font("SansSerrif",Font.PLAIN,12);
		//fm = getFontMetrics(f);
		
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
		
		//setOpaque(false);
		
	}
	
	public void setBounds(int x, int y, int b, int h)
	{
		xPos = x;
		yPos = y;
		breedte = b;
		hoogte = h;
	}
	
	public boolean contains(int x, int y)
	{
		return (new Rectangle(xPos,yPos,breedte,hoogte).contains(x, y));
	}
	
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
	{	
		if (!visible)
			return;
		
		if (dubbel)
		{	breedteUitv = breedte-10-breedteInv;
			
			//g.setColor(Color.white);
			g.setFillStyle(CssColor.make(255,255,255));
			g.fillRect(xPos+breedteInv+6,yPos+15,breedteUitv-10,hoogte - 31);
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			//g.drawRect(breedteInv+6,15,breedteUitv-10,hoogte - 31);
			g.strokeRect(xPos+breedteInv+6,yPos+15,breedteUitv-10,hoogte - 31);

			//g.setColor(Color.white);
			g.setFillStyle(CssColor.make(255,255,255));
			g.fillRect(xPos+3,yPos+15,breedteInv,hoogte - 31);
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			//g.drawRect(3,15,breedteInv,hoogte - 31);
			g.strokeRect(xPos+3,yPos+15,breedteInv,hoogte - 31);
			
			//g.setColor(new Color(220,220,220));
			g.setFillStyle(CssColor.make(220,220,220));
			g.fillRect(xPos+3,yPos,breedteInv,15);
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			//g.drawRect(3,0,breedteInv,15);
			g.strokeRect(xPos+3,yPos,breedteInv,15);
			
			if(selectMogelijk && selectnummer>-1 && selectnummer<8 && exp!=null && !exp.geefVarNaam().equals(""))
			{	//g.setColor(traceKleur);
				g.setFillStyle(traceKleur);
				g.fillRect(xPos+breedteInv+6,yPos+15+selectnummer*15+beginx%eenheidx,breedteUitv-10,16);
				g.fillRect(xPos+3,yPos+15+selectnummer*15+beginx%eenheidx,breedteInv,16);
				//g.setColor(Color.black);
				g.setStrokeStyle(CssColor.make(0,0,0));
				//g.drawRect(breedteInv+6,15+selectnummer*15+beginx%eenheidx,breedteUitv-10,16);
				g.strokeRect(xPos+breedteInv+6,yPos+15+selectnummer*15+beginx%eenheidx,breedteUitv-10,16);
				//g.drawRect(3,15+selectnummer*15+beginx%eenheidx,breedteInv,16);
				g.strokeRect(xPos+3,yPos+15+selectnummer*15+beginx%eenheidx,breedteInv,16);
			}
			
			int knopPlusX = xPos+38;//15;
			int knopPlusY = yPos+4;
			int knopMinX = xPos+38;//15;
			int knopMinY = yPos+hoogte-5;
			
			//pijlPlusContain = new Polygon();
			//pijlPlusContain.addPoint(knopPlusX-10,knopPlusY+8);
			//pijlPlusContain.addPoint(knopPlusX+10,knopPlusY+8);
			//pijlPlusContain.addPoint(knopPlusX,knopPlusY-4);
			pijlPlusContain = new Rectangle(knopPlusX-10,knopPlusY-4,20,12);
				
			pijlPlus = new Polygon();
			pijlPlus.addPoint(knopPlusX-5,knopPlusY+8);
			pijlPlus.addPoint(knopPlusX+5,knopPlusY+8);
			pijlPlus.addPoint(knopPlusX,knopPlusY);
			
			g.setFillStyle(CssColor.make(0,0,0));
			
//			g.fillPolygon(pijlPlus);
	       	g.moveTo(pijlPlus.doubleX[0], pijlPlus.doubleY[0]);
			g.beginPath();
			for (int k = 1; k < pijlPlus.aantalPunten; k++)
			{	g.lineTo(pijlPlus.doubleX[k], pijlPlus.doubleY[k]);
			}
			g.lineTo(pijlPlus.doubleX[0], pijlPlus.doubleY[0]);
			g.closePath();
			g.fill();

//			g.drawPolygon(pijlPlus);
		
			//pijlMinContain = new Polygon();
			//pijlMinContain.addPoint(knopMinX-10,knopMinY-8);
			//pijlMinContain.addPoint(knopMinX+10,knopMinY-8);
			//pijlMinContain.addPoint(knopMinX,knopMinY+5);
			pijlMinContain = new Rectangle(knopMinX-10,knopMinY-8,20,12);
			
			pijlMin = new Polygon();
			pijlMin.addPoint(knopMinX-5,knopMinY-8);
			pijlMin.addPoint(knopMinX+5,knopMinY-8);
			pijlMin.addPoint(knopMinX,knopMinY);
			

			//	g.fillPolygon(pijlMin);
	       	g.moveTo(pijlMin.doubleX[0], pijlMin.doubleY[0]);
			g.beginPath();
			for (int k = 1; k < pijlMin.aantalPunten; k++)
			{	g.lineTo(pijlMin.doubleX[k], pijlMin.doubleY[k]);
			}
			g.lineTo(pijlMin.doubleX[0], pijlMin.doubleY[0]);
			g.closePath();
			g.fill();
			
			
//			g.drawPolygon(pijlMin);	
			
			if(exp!=null)
			{	String s = exp.geefVarNaam();
				boolean b = s.length() >= 2 && s.substring(0,2).equals("qq");
				if(s!=null && !s.equals(""))
				{	if (!b)					
					{	
						//g.drawString(s,7,12);
						g.fillText(s,xPos+7,yPos+12);
					
					}
					for(int i=0 ; i<8 ; i++)
					{	if(exp.isWaarde(schaalFactorX*(i+beginwaarde)))
						{	double d = exp.geefW(schaalFactorX*(i+beginwaarde));
							if(i<7 && beginx>0 || i>0 && beginx<0 ||beginx%eenheidx==0) 
							{	
								//g.drawString(exp.df.format(d),breedteInv+8,28+i*15+beginx%eenheidx);
								g.fillText(UF.format0(d, 3),xPos+breedteInv+8,yPos+28+i*15+beginx%eenheidx);
							}
						}
						else 
						{	//g.drawString("-",breedteInv+8,28+i*15+beginx%eenheidx);
							g.fillText("-",xPos+breedteInv+8,yPos+28+i*15+beginx%eenheidx);
						
						}

						if(i<7 && beginx>0 || i>0 && beginx<0 ||beginx%eenheidx==0)
						{	
							
							//g.drawString(exp.df.format(schaalFactorX*(i+beginwaarde)),5,28+i*15+beginx%eenheidx);
							g.fillText(UF.format0(schaalFactorX*(i+beginwaarde), 3),xPos+5,yPos+28+i*15+beginx%eenheidx);
						}	
					}
				}
			}
		}
		else // !dubbel, i.e. een(1) kolom
		{	
			breedteUitv = breedte - 10;
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
	
	
	public void zetDubbel(boolean b)
	{	dubbel = b;
	}
	
	public void zetExpressie(Expressie e)
	{	if (e != null  && e.geefVarNaam() != null)//&& e.geefWaarde()==null
		{	exp = e;
			varNaam = e.geefVarNaam();
		}
		else //exp = null;
		{	exp = new BasisExpressie(defaultVarnaam); 
			varNaam = defaultVarnaam;
		}
	}
	
	public void zetTabel(int beginwaarde, int selectnummer, String varN, double schaalFactorX, double beginx)
	{	varNaam = exp.geefVarNaam();
		
//System.out.println("test3"+varNaam +varN);
	
		if (varNaam.equals(varN))
		{	this.beginwaarde = beginwaarde;
			this.selectnummer = selectnummer;
			this.schaalFactorX = schaalFactorX;
			this.beginx = (int) Math.round(beginx);
			paint();
		}
	}
	
	public void zetSelectMogelijk(boolean b)
	{	selectMogelijk = b;
	}
	
	public int geefBreedte()
	{	if (dubbel)
		{	breedteInv = 20;
			breedteUitv = 23;
			if (exp != null)
			{	varNaam = exp.geefVarNaam();
				if (varNaam != null && !varNaam.equals(""))
				{	boolean b = varNaam.equals("qq") || varNaam.length() > 2 && varNaam.substring(0,2).equals("qq");
					if(!b)
					{	//breedteInv = Math.max(breedteInv, fm.stringWidth(varNaam) + 4);
						TextMetrics tm = asv.asvContext2d.measureText(varNaam);
						int stringWidth = (int) Math.round(tm.getWidth());
						breedteInv = Math.max(breedteInv, stringWidth + 4);
					}
					
					for (int i = 0; i < 8; i++)
					{	if (exp.isWaarde(schaalFactorX * (i + beginwaarde)))
						{	double d = exp.geefW(schaalFactorX * (i + beginwaarde));
						
							//String sUitv = exp.df.format(d);
							String sUitv = UF.format0(d,3);
							
							TextMetrics tm = asv.asvContext2d.measureText(sUitv);
							int stringWidth = (int) Math.round(tm.getWidth());
							//breedteUitv = Math.max(breedteUitv, fm.stringWidth(sUitv) + 4);
							breedteUitv = Math.max(breedteUitv, stringWidth + 4);
						}
					
						//String sInv = exp.df.format(schaalFactorX * (i + beginwaarde));
						String sInv = UF.format0(schaalFactorX * (i + beginwaarde),3);
						TextMetrics tm = asv.asvContext2d.measureText(sInv);
						int stringWidth = (int) Math.round(tm.getWidth());
						//breedteInv = Math.max(breedteInv, fm.stringWidth(sInv) + 4);
						breedteInv = Math.max(breedteInv, stringWidth + 4);
					}
				}
			}
			int b = breedteInv + breedteUitv + 20;
			return b;	
		}
		else
		{	breedteUitv = 30;
			if (exp != null)
			{	varNaam = exp.geefVarNaam();
				if (varNaam != null && !varNaam.equals(""))
				{				
					for (int i = 0; i < 8; i++)
					{	if (exp.isWaarde(schaalFactorX * (i + beginwaarde)))
						{	double d = exp.geefW(schaalFactorX * (i + beginwaarde));
//GWT						
//							String sUitv = exp.df.format(d);
//							breedteUitv = Math.max(breedteUitv, fm.stringWidth(sUitv) + 4);
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
