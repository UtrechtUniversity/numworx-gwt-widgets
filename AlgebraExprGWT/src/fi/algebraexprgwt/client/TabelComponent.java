package fi.algebraexprgwt.client;

//import java.awt.*;
//import java.awt.event.*;
//import java.util.Vector;

import fi.algebraexprgwt.client.expressies_ap.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

import java.util.*;

//import javax.swing.*;

public class TabelComponent extends SchuifComponent 
						    //implements MouseListener, MouseMotionListener
{	
	 private Polygon pijlPlus, pijlMin;
	 Rectangle pijlPlusContain, pijlMinContain;
	 private Expressie exp;
	 private boolean selectMogelijk;
	 private int beginwaarde;
	 private int selectnummer;
	 private double schaalFactorX;
	 private int breedteInv;
	 private int breedteUitv;
	 private String varNaam;
//	 Font f;
//	 FontMetrics fm;

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
		breedteInv = 20;
		breedteUitv = 33;
		
		//setOpaque(false);
	}
	
	public void setDefaultVarnaam(String s)
	{	defaultVarnaam = s;
		exp = new BasisExpressie(defaultVarnaam);
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
	
	public void paint()
	{
		paint(asv.asvContext2d);
	}
	
	//public void paint(Graphics g)
	public void paint(Context2d g)
	{	
		//int breedte = getSize().width;
		//int hoogte = getSize().height;
		
		if (!visible)
			return;
		
		breedteUitv = breedte - 10 - breedteInv;
		
		//g.setFont(f);
			
		//g.setColor(Color.white);
		g.setFillStyle(CssColor.make(255,255,255));
		//g.fillRect(breedteInv + 5, 15, breedteUitv, hoogte - 31);
		g.fillRect(xPos+breedteInv + 5, yPos+15, breedteUitv, hoogte - 31);
		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		//g.drawRect(breedteInv + 5, 15, breedteUitv, hoogte - 31);
		g.strokeRect(xPos+breedteInv + 5, yPos+15, breedteUitv, hoogte - 31);
			
			
		//g.setColor(Color.white);
		g.setFillStyle(CssColor.make(255,255,255));
		//g.fillRect(3, 15, breedteInv, hoogte - 31);
		g.fillRect(xPos+3, yPos+15, breedteInv, hoogte - 31);
		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		//g.drawRect(3, 15, breedteInv, hoogte - 31);
		g.strokeRect(xPos+3, yPos+15, breedteInv, hoogte - 31);

			
		//g.setColor(new Color(220,220,220));
		g.setFillStyle(CssColor.make(220,220,220));
		//g.fillRect(3, 0, breedteInv, 15);
		g.fillRect(xPos+3, yPos+0, breedteInv, 15);
		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		//g.drawRect(3, 0, breedteInv, 15);
		g.strokeRect(xPos+3, yPos+0, breedteInv, 15);
			
		if (selectMogelijk && selectnummer > -1 && selectnummer < 8 && exp != null && !exp.geefVarNaam().equals(""))
		{	//g.setColor(new Color(255,200,200));
			//g.setColor(traceKleur);
			g.setFillStyle(traceKleur);
			if (selectnummer < 7 && beginx > 0 || selectnummer > 0 && beginx < 0 || beginx % eenheidx == 0)
			{	//g.fillRect(3, 15 + selectnummer * 15 + beginx % eenheidx, breedteInv, 16);
				g.fillRect(xPos+3, yPos+15 + selectnummer * 15 + beginx % eenheidx, breedteInv, 16);
				//g.fillRect(breedteInv + 5, 15 + selectnummer * 15 + beginx % eenheidx, breedteUitv, 16);
				g.fillRect(xPos+breedteInv + 5, yPos+15 + selectnummer * 15 + beginx % eenheidx, breedteUitv, 16);
			}	

			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			if (selectnummer < 7 && beginx > 0 || selectnummer > 0 && beginx < 0 || beginx % eenheidx == 0)
			{	//g.drawRect(3, 15 + selectnummer * 15 + beginx % eenheidx, breedteInv, 16);
				g.strokeRect(xPos+3, yPos+15 + selectnummer * 15 + beginx % eenheidx, breedteInv, 16);
				//g.drawRect(breedteInv + 5, 15 + selectnummer * 15 + beginx % eenheidx, breedteUitv, 16);
				g.strokeRect(xPos+breedteInv + 5, yPos+15 + selectnummer * 15 + beginx % eenheidx, breedteUitv, 16);
			}
			//g.drawRect(3,15+selectnummer*15,breedteInv,16);
		}
			
		
		pijlPlus = new Polygon();
		pijlPlus.addPoint(xPos+3 + breedteInv + 3 + breedteUitv / 2 - 5, yPos+12);
		pijlPlus.addPoint(xPos+3 + breedteInv + 3 + breedteUitv / 2 + 5, yPos+12);
		pijlPlus.addPoint(xPos+3 + breedteInv + 3 + breedteUitv / 2, yPos+4);
		pijlPlusContain = new Rectangle(xPos+3+breedteInv+3+ breedteUitv/2-25,yPos+2,50, 12);
		
		g.setFillStyle(CssColor.make(0,0,0));
		
		//g.fillPolygon(pijlPlus);
       	g.moveTo(pijlPlus.doubleX[0], pijlPlus.doubleY[0]);
		g.beginPath();
		for (int k = 1; k < pijlPlus.aantalPunten; k++)
		{	g.lineTo(pijlPlus.doubleX[k], pijlPlus.doubleY[k]);
		}
		g.lineTo(pijlPlus.doubleX[0], pijlPlus.doubleY[0]);
		g.closePath();
		g.fill();
		
		//g.drawPolygon(pijlPlus);
		
		pijlMin = new Polygon();
		pijlMin.addPoint(xPos+3 + breedteInv + 3 +breedteUitv/2-5,yPos+hoogte-13);
		pijlMin.addPoint(xPos+3 + breedteInv + 3 +breedteUitv/2+5,yPos+hoogte-13);
		pijlMin.addPoint(xPos+3 + breedteInv + 3 +breedteUitv/2,yPos+hoogte-5);
		pijlMinContain = new Rectangle(xPos+3+breedteInv+3+ breedteUitv/2-25,yPos+hoogte-13,50, 12);
		
//		g.fillPolygon(pijlMin);
       	g.moveTo(pijlMin.doubleX[0], pijlMin.doubleY[0]);
		g.beginPath();
		for (int k = 1; k < pijlMin.aantalPunten; k++)
		{	g.lineTo(pijlMin.doubleX[k], pijlMin.doubleY[k]);
		}
		g.lineTo(pijlMin.doubleX[0], pijlMin.doubleY[0]);
		g.closePath();
		g.fill();
		
		
		
		//g.drawPolygon(pijlMin);
		
		if (exp != null)
		{	//String s = exp.geefVarNaam();
			Vector varNames = Algebra.geefVarN(exp);
//System.out.println(exp.toString());
//System.out.println("vn = " + s);
			boolean bb = false;
			for (int cnt = 0; cnt < varNames.size(); cnt++)
			{	String s = (String) varNames.elementAt(cnt);	
				boolean b = s.equals("qq") || s.length() > 2 && s.substring(0,2).equals("qq");
				if (b)
					bb = true;
			}
			//if (s != null && !s.equals(""))
			if (varNames.size() > 0)
			{	if (!bb)
				{	//g.drawString(s, 5, 12);
					//g.drawString((String) varNames.elementAt(0), 5, 12);
					g.fillText((String) varNames.elementAt(0), xPos+5, yPos+12);
				}
				for (int i = 0; i < 8; i++)
				{	if (exp.isWaarde(schaalFactorX * (i + beginwaarde)))
					{	double d = exp.geefW(schaalFactorX * (i + beginwaarde));
						if (i < 7 && beginx > 0 || i > 0 && beginx < 0 || beginx % eenheidx == 0)
							//g.drawString(exp.df.format(d), breedteInv + 9, 28 + i * 15 + beginx % eenheidx);
							g.fillText(UF.format0(d,3), xPos+breedteInv + 9, yPos+28 + i * 15 + beginx % eenheidx);
					}
					else 
					{	//g.drawString("-", breedteInv + 9, 28 + i * 15 + beginx % eenheidx);
						g.fillText("-", xPos+breedteInv + 9, yPos+28 + i * 15 + beginx % eenheidx);
					
					}
				
					if (i < 7 && beginx > 0 || i > 0 && beginx < 0 || beginx % eenheidx == 0)
						//g.drawString(exp.df.format(schaalFactorX * (i + beginwaarde)), 6, 28 + i * 15 + beginx % eenheidx);
						g.fillText(UF.format0(schaalFactorX * (i + beginwaarde),3), xPos+6, yPos+28 + i * 15 + beginx % eenheidx);
				}
			}
		}
	}
/*	
	public void zetExpressie(Expressie e)
	{	if (e != null && e.geefWaarde() == null && e.geefVarNaam() != null)
			exp = e;
		else 
			exp = null;
		//varNaam = e.geefVarNaam();
		//int n = geefBreedte();
		//repaint();
	}
*/	
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
	
	public void zetTabel(int beginwaarde, int selectnummer, String varN, double schaalFactorX)
	{	//if(varNaam.equals(varN))
		{	this.beginwaarde = beginwaarde;
			this.selectnummer = selectnummer;
			this.schaalFactorX = schaalFactorX;
			paint();
		}
	}

	
	public void zetTabel(int beginwaarde, int selectnummer, String varN, double schaalFactorX, double beginx)
	{	varNaam = exp.geefVarNaam();
		//System.out.println("test3"+varNaam +varN);
	
		if(varNaam.equals(varN))
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
	{	breedteInv = 20;
		breedteUitv = 33;
		if (exp != null)
		{	varNaam = exp.geefVarNaam();
			if (varNaam != null && !varNaam.equals(""))
			{	boolean b = varNaam.equals("qq") || varNaam.length() > 2 && varNaam.substring(0,2).equals("qq");
				if(!b)
				{	//breedteInv = Math.max(breedteInv, fm.stringWidth(varNaam) + 8);
					TextMetrics tm = asv.asvContext2d.measureText(varNaam);
					int stringWidth = (int) Math.round(tm.getWidth());
					breedteInv = Math.max(breedteInv, stringWidth + 8);
				}
				
				for (int i = 0; i < 8; i++)
				{	if (exp.isWaarde(schaalFactorX * i + beginwaarde))
					{	double d = exp.geefW(schaalFactorX * (i + beginwaarde));
						//String sUitv = exp.df.format(d);
						String sUitv = UF.format0(d,3);
						TextMetrics tm = asv.asvContext2d.measureText(sUitv);
						int stringWidth = (int) Math.round(tm.getWidth());
						//breedteUitv = Math.max(breedteUitv, fm.stringWidth(sUitv) + 18);
						breedteUitv = Math.max(breedteUitv, stringWidth + 18);
					}
					//String sInv = exp.df.format(schaalFactorX * (i + beginwaarde));
					String sInv = UF.format0(schaalFactorX * (i + beginwaarde),3);
					TextMetrics tm = asv.asvContext2d.measureText(sInv);
					int stringWidth = (int) Math.round(tm.getWidth());
					//breedteInv = Math.max(breedteInv, fm.stringWidth(sInv) + 8);
					breedteInv = Math.max(breedteInv, stringWidth + 8);
				}
			}
		}
		int b = breedteInv + breedteUitv + 10;
		//int b = breedteUitv + 20;
		return b;	
	}
	
	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	//starty = e.getY();
		startY = eventY;
		//raak = (new Rectangle(5, 15, getSize().width - 10, getSize().height - 31)).contains(e.getX(),e.getY());
		raak = (new Rectangle(xPos+5, yPos+15, breedte - 10, hoogte - 31)).contains(eventX,eventY);
	
		//if (new Rectangle(0, 17, getSize().width - 5, getSize().height - 34).contains(e.getX(), e.getY()))
		//	setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
		
		
		if (pijlPlusContain.contains(eventX,eventY) && ! asv.isDemo)
		{	
			beginwaarde--;
			selectnummer++;
//			((UitvoerSchuifComponent)getParent()).zetGrafiekTabel(beginwaarde,selectnummer,varNaam,schaalFactorX);
			//repaint();
			asv.tekenOpnieuw();
		}
		else if (pijlMinContain.contains(eventX,eventY) &&	!asv.isDemo)
		{	beginwaarde++;
			selectnummer--;
//			((UitvoerSchuifComponent)getParent()).zetGrafiekTabel(beginwaarde,selectnummer,varNaam, schaalFactorX);
			//repaint();
			asv.tekenOpnieuw();
		}
		else
		{	
/*			
			for (int i = 0; i < 8; i++)
			{	if ((new Rectangle(4, 17 + i * 15, getSize().width - 9, 15)).contains(e.getX(),e.getY()))
				{	if (selectnummer == i) 
						selectnummer = 999;
					else 
						selectnummer = i;
					((UitvoerSchuifComponent)getParent()).zetGrafiekTabel(beginwaarde,selectnummer,varNaam, schaalFactorX);
				}
			}
*/			
		}
		beginx = -beginwaarde * eenheidx;
		//((AlgebraSchuifVeld)getParent().getParent()).zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
        //((AlgebraSchuifVeld)getParent().getParent()).zoomStateHolder.setZoomStates(varNaam);
        
//GWT?		
		//if (!raak)
		//	((SchuifComponent)getParent()).mousePressed(e);
	}	

	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	if (raak)
		{
			int ddy =  eventY - startY;
		
			beginx = beginx + ddy;
			int b = beginwaarde;
			beginwaarde = -(int) Math.round(beginx / eenheidx);
			selectnummer = selectnummer + b - beginwaarde;
			//repaint();
			startY = eventY;
			
			asv.zoomStateHolder.setSelectnummer(varNaam, selectnummer);
			asv.zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
			asv.zoomStateHolder.setBeginx(varNaam, beginx);
			asv.zoomStateHolder.setZoomStates(varNaam);
		
	        //repaint();
			asv.tekenOpnieuw();
		}
	}
	
	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction(int eventX, int eventY)
	{	//setCursor(new Cursor(Cursor.HAND_CURSOR));
		int beginxOud = beginx;
		int b = beginwaarde;
		if (beginx > 0)
			beginx = (beginx + eenheidx / 2) / eenheidx * eenheidx;
		else 
			beginx = (beginx - eenheidx / 2) / eenheidx * eenheidx;
		beginwaarde = -(int) Math.round(beginx / eenheidx);
		selectnummer = selectnummer + b - beginwaarde;
		beginx = -beginwaarde * eenheidx;
		
		//((SchuifComponent) getParent()).mouseReleased(e);
		
		if (beginx == beginxOud)
		{	for (int i = 0; i < 8; i++)
			{	if ((new Rectangle(xPos+5, yPos+15 + i * 15, breedte - 10, 15)).contains(eventX,eventY))
				{	if (selectnummer == i)
						selectnummer = 999;
					else 
						selectnummer = i;
					//((AlgebraSchuifVeld)getParent().getParent()).zetTabellen(beginwaarde,selectnummer,varNaam, schaalFactorX);
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
