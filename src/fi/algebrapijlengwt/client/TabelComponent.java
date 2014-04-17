package fi.algebrapijlengwt.client;

//import java.awt.*;
//import java.awt.event.*;
//import javax.swing.*;
import fi.algebrapijlengwt.client.expressies_ap.*;
//import fi.algebrapijlenopdr.schuifobjects.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


public class TabelComponent extends SchuifComponent 
							//JPanel 
						    //implements ActionListener, MouseListener, MouseMotionListener
{	
	 private Polygon pijlPlus, pijlMin; //, pijlPlusContain, pijlMinContain;
	 Rectangle pijlPlusContain, pijlMinContain;
	 private Expressie exp;
	 private boolean selectMogelijk;
	 private int beginwaarde;
	 private int selectnummer;
	 private double schaalFactorX;
	 private int breedteInv;
	 private int breedteUitv;
	 private String varNaam;
	 //Font f;
	 //FontMetrics fm;
	 private boolean dubbel;

	 private int startY;
	 private int beginx;
	 private int eenheidx = 14;
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
	{
		paint(asv.asvContext2d);
	}
	
	//public void paint(Graphics g)
	public void paint(Context2d g)
	{	
		
		if (!visible)
			return;
		//int breedte = getSize().width;
		//int hoogte = getSize().height;
		
		//g.setFont(f);
		
		if(dubbel)
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
		else // !dubbel
		{	
			breedteUitv = breedte - 10;
			//g.setColor(Color.white);
			g.setFillStyle(CssColor.make(255,255,255));
			g.fillRect(xPos+3, yPos+15, breedte - 17, hoogte - 31);
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			//g.drawRect(3, 15, breedte - 17, hoogte - 31);
			g.strokeRect(xPos+3, yPos+15, breedte - 17, hoogte - 31);
			
			if (selectMogelijk && selectnummer > -1 && selectnummer < 8 && exp != null && !exp.geefVarNaam().equals(""))
			{	//g.setColor(traceKleur);
				g.setFillStyle(traceKleur);
				if (selectnummer < 7 && beginx > 0 || selectnummer > 0 && beginx < 0 || beginx % eenheidx == 0)
					g.fillRect(xPos+3, yPos+15 + selectnummer * 15 + beginx % eenheidx, breedte - 17, 16);
				//g.setColor(Color.black);
				g.setStrokeStyle(CssColor.make(0,0,0));
				if (selectnummer < 7 && beginx > 0 || selectnummer > 0 && beginx < 0 || beginx % eenheidx == 0)
				{	//g.drawRect(3, 15 + selectnummer * 15 + beginx % eenheidx, breedte - 17, 16);
					g.strokeRect(xPos+3, yPos+15 + selectnummer * 15 + beginx % eenheidx, breedte - 17, 16);
				}
			}
			
			//pijlPlusContain = new Polygon();
			//pijlPlusContain.addPoint(xPos+breedteUitv/2-25,yPos+12);
			//pijlPlusContain.addPoint(xPos+breedteUitv/2+25,yPos+12);
			//pijlPlusContain.addPoint(xPos+breedteUitv/2,yPos+0);
			pijlPlusContain = new Rectangle(xPos+breedteUitv/2-25,yPos+0,50, 12);
				
			pijlPlus = new Polygon();
			pijlPlus.addPoint(xPos+breedteUitv/2-5,yPos+12);
			pijlPlus.addPoint(xPos+breedteUitv/2+5,yPos+12);
			pijlPlus.addPoint(xPos+breedteUitv/2,yPos+4);
			
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
			//pijlMinContain.addPoint(xPos+breedteUitv/2-25,yPos+hoogte-13);
			//pijlMinContain.addPoint(xPos+breedteUitv/2+25,yPos+hoogte-13);
			//pijlMinContain.addPoint(xPos+breedteUitv/2,yPos+hoogte);
			pijlMinContain = new Rectangle(xPos+breedteUitv/2-25,yPos+hoogte-12,50, 12);
			
			pijlMin = new Polygon();
			pijlMin.addPoint(xPos+breedteUitv/2-5,yPos+hoogte-13);
			pijlMin.addPoint(xPos+breedteUitv/2+5,yPos+hoogte-13);
			pijlMin.addPoint(xPos+breedteUitv/2,yPos+hoogte-5);
			
//			g.fillPolygon(pijlMin);
	       	g.moveTo(pijlMin.doubleX[0], pijlMin.doubleY[0]);
			g.beginPath();
			for (int k = 1; k < pijlMin.aantalPunten; k++)
			{	g.lineTo(pijlMin.doubleX[k], pijlMin.doubleY[k]);
			}
			g.lineTo(pijlMin.doubleX[0], pijlMin.doubleY[0]);
			g.closePath();
			g.fill();

//			g.drawPolygon(pijlMin);
			
			if (exp != null)
			{	String s = exp.geefVarNaam();
				if (s != null && !s.equals(""))
				{	for (int i = 0; i < 8; i++)
					{	if (exp.isWaarde(schaalFactorX * (i + beginwaarde)))
						{	double d = exp.geefW(schaalFactorX * (i + beginwaarde));
							if (i < 7 && beginx > 0 || i > 0 && beginx < 0 || beginx % eenheidx == 0)
							{
								
								//g.drawString(exp.df.format(d), 8, 28 + i * 15 + beginx % eenheidx);
								g.fillText(UF.format0(d,3), xPos+8, yPos+28 + i * 15 + beginx % eenheidx);
							}	
						}
						else 
						{	//g.drawString("-", 8, 28 + i * 15 + beginx % eenheidx);
							g.fillText("-", xPos+8, yPos+28 + i * 15 + beginx % eenheidx);
						}
					}
				}
			}
		}
		//super.paint(g);
	}
	
	
	public void zetDubbel(boolean b)
	{	dubbel = b;
	}
	
	public void zetExpressie(Expressie e)
	{	if(e!=null  && e.geefVarNaam()!=null)//&& e.geefWaarde()==null
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
	
		if(varNaam.equals(varN))
		{	this.beginwaarde = beginwaarde;
			this.selectnummer = selectnummer;
			this.schaalFactorX = schaalFactorX;
			this.beginx = (int)Math.round(beginx);
			
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
					{	if (exp.isWaarde(schaalFactorX * i + beginwaarde))
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
/*	
	public void actionPerformed(ActionEvent e)
	{
		
	}
*/	
	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	//starty = e.getY();
		startY = eventY;
		//raak = (new Rectangle(4, 17, getSize().width - 9, 115)).contains(e.getX(),e.getY());
		raak = (new Rectangle(xPos + 4, yPos + 17, breedte - 9, 115)).contains(eventX,eventY);
		
//System.out.println("raak mdtsa");		
		
//GWT?		
		//if (new Rectangle(0, 17, getSize().width - 5, getSize().height - 34).contains(e.getX(), e.getY()))
		//	setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
		
		if (pijlPlusContain.contains(eventX,eventY) && !asv.isDemo && !asv.frozen)
		{	
			beginwaarde--;
			selectnummer++;
			//paint();
			asv.tekenOpnieuw();
		}
		else if (pijlMinContain.contains(eventX,eventY) && !asv.isDemo && !asv.frozen)
		{	beginwaarde++;
			selectnummer--;
			//paint();
			asv.tekenOpnieuw();
		}
		else
		{	
		}
		beginx = -beginwaarde * eenheidx;
        
		if (!raak)
		{
//GWT, niet nodig?			
//			((SchuifComponent)getParent()).mousePressed(e);
		}	
	}	
	
	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		if (raak)
		{
			
			//int dy =  e.getY() - starty;
			int ddy =  eventY - startY;
		
//System.out.println("raak mmtma " + ddy);			
			
			beginx = beginx + ddy;
			int b = beginwaarde;
			beginwaarde = -(int) Math.round(beginx / eenheidx);
			selectnummer = selectnummer + b - beginwaarde;
			//repaint();
			startY = eventY; //e.getY();
			
			asv.zoomStateHolder.setSelectnummer(varNaam, selectnummer);
	        asv.zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
	        asv.zoomStateHolder.setBeginx(varNaam, beginx);
	        asv.zoomStateHolder.setZoomStates(varNaam);
	        
	        asv.tekenOpnieuw();
			
		}
		// else slepen tabel + uvs, de uve is onbekend 

	}
	
	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction(int eventX, int eventY)
	{	
		//setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		int beginxOud = beginx;
		int b = beginwaarde;
		if (beginx > 0)
			beginx = (beginx + eenheidx / 2) / eenheidx * eenheidx;
		else 
			beginx = (beginx - eenheidx / 2) / eenheidx * eenheidx;
		beginwaarde = -(int) Math.round(beginx / eenheidx);
		selectnummer = selectnummer + b - beginwaarde;
		beginx = -beginwaarde * eenheidx;
		
//bestaat niet		
//		((SchuifComponent) getParent()).mouseReleased(e);
		
		
		if (beginx == beginxOud)
		{	for (int i = 0; i < 8; i++)
			{	//if ((new Rectangle(4, 17 + i * 15, getSize().width - 9, 15)).contains(e.getX(),e.getY()))
				if ((new Rectangle(xPos+4, yPos+17 + i * 15, breedte - 9, 15)).contains(eventX,eventY))
				{	if (selectnummer == i)
						selectnummer = 999;
					else 
						selectnummer = i;
				}
			}
		}
		
		asv.zoomStateHolder.setSelectnummer(varNaam, selectnummer);
		asv.zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
		asv.zoomStateHolder.setBeginx(varNaam, beginx);
		asv.zoomStateHolder.setZoomStates(varNaam);
        
		asv.tekenOpnieuw();
	}
	
	//public void mouseMoved(MouseEvent e){;}
	//public void mouseExited(MouseEvent e)
	//{	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	//}
	//public void mouseClicked(MouseEvent e){;}
	//public void mouseEntered(MouseEvent e)
	//{	setCursor(new Cursor(Cursor.HAND_CURSOR));
	//}	
}
