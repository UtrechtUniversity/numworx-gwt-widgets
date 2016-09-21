package fi.algebraexprgwt.client;

import java.awt.*;
import java.awt.event.*;
//import java.util.Hashtable;

import java.util.HashMap;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

//import fi.algebraexpressies.schuifobjects.*;
import fi.algebraexprgwt.client.expressies_ap.*;



public class AlgebraSchuifComponent extends SchuifComponent 
{	
	 int soort;
	 Pijl pijlIn1, pijlIn2;
	 Pijl[] pijlUit;
	 int aantalPu;
	 boolean isStapel;
	 boolean open;

	 AlgebraSchuifVeld asv;
	 
	 Context2d ascContext2d;
	 
	 boolean visible = true;
	 
	 boolean mouseDown;
	 
	 
	//public AlgebraSchuifComponent(int srt, SchuifVeld sv, int x, int y, int b, int h)
	public AlgebraSchuifComponent(int srt, AlgebraSchuifVeld asv, int x, int y, int b, int h) 
	{	super(x, y, b, h);
		this.asv = asv;
		ascContext2d = asv.asvContext2d;
		soort = srt;
		isStapel = true;
		open = true;
		aantalPu = 0;
		pijlUit = new Pijl[10];
	}

	public HashMap<String,Object> getState()
	{	boolean isStapel = false;
		
		isStapel = this.isStapel;
		
		HashMap<String,Object> h = new HashMap<String,Object>();
	    h.put("isStapel", new Boolean(isStapel));
	    
	    return h;
	}

    public void setState(HashMap<String,Object> h)
    {	boolean isStapel = ((Boolean) h.get("isStapel")).booleanValue();
		
		this.isStapel = isStapel;
		
    }

    // redefined in UitvoerSchuifComponent
	public void zetVakKleur(CssColor color)
	{	
	}
    
	public void setVisible(boolean b)
	{
		visible = b;
		
		if (!asv.owner.asvSetState)
			asv.tekenOpnieuw();
		
		
	}
	
	public void paint()
	{
		paint(ascContext2d);
	}
	
	public void paint(Context2d gIm)
  	{
		if (!visible)
			return;
			
//if (isStapel)		
//System.out.println("asc stapel " + xPos);

		if (soort == 1 && open)
		{	//gIm.setColor(Color.gray);
			gIm.setFillStyle(CssColor.make(125,125,125));
			//gIm.fillOval(18, 5, 3, 3);
			gIm.beginPath();
			gIm.arc(xPos + 20, yPos + 5, 3, 0, 2 * Math.PI);
			gIm.fill();
			
		}
		else if (soort == 2)
		{	//gIm.setColor(Color.gray);
			gIm.setFillStyle(CssColor.make(125,125,125));
			//gIm.fillOval(8, 5, 3, 3);
			gIm.beginPath();
			gIm.arc(xPos + 10, yPos + 5, 3, 0, 2 * Math.PI);
			gIm.fill();
			//gIm.fillOval(28, 5, 3, 3);
			gIm.beginPath();
			gIm.arc(xPos + 30, yPos + 5, 3, 0, 2 * Math.PI);
			gIm.fill();
		}
		//super.paint(gIm);
	}
	
	public boolean contains(int x, int y)
	{	//return (new Rectangle(0, 10, getSize().width, getSize().height - 10)).contains(x,y);
		return (new Rectangle(xPos+0, yPos+10, breedte, hoogte - 10)).contains(x,y);
	}
	
	public void zetPlaats(int x, int y)
	{
		xPos = x;
		yPos = y;
		Pijl p = pijlUit[aantalPu-1];
		p.zetPlaats(xPos + breedte - 20 ,yPos + hoogte + 10);
	}
	
	public void zetPlaats(int x, int y, Pijl p)
	{
		xPos = x;
		yPos = y;
		
		p.zetPlaats(xPos + breedte - 20 ,yPos + hoogte + 10);
	}

	public void zetBoomZichtbaar(boolean b)
	{	
		setVisible(b);
		if (this instanceof UitvoerSchuifComponent)
		{
			UitvoerSchuifComponent uvsc = (UitvoerSchuifComponent) this;
			if (uvsc.tabel != null)
			{	if (b && uvsc.tabelZichtbaar)
					uvsc.tabel.visible = b;
				else
					uvsc.tabel.visible = b;
			}
		}

		if (pijlIn1 != null)
			pijlIn1.zender.zetBoomZichtbaar(b);
		if (pijlIn2 != null)
			pijlIn2.zender.zetBoomZichtbaar(b);
		//setVisible(b);
		for (int i = 0; i < aantalPu; i++)
		{	pijlUit[i].setVisible(b);
		}
		paint();
	}
	
	public void voegPijlToe(Pijl p)
	{	if (aantalPu < 10)
		{	//p.zetPlaats(getLocation().x + getSize().width - 20 ,getLocation().y + getSize().height + 10);
			p.zetPlaats(xPos + breedte - 20 ,yPos + hoogte + 10);
			pijlUit[aantalPu] = p;
			//schuifveld.add(p,0);
			//schuifveld.add(p);
			p.zetZender(this);
			aantalPu++;
		}
	}
	
	public void verwijderPijl()
	{	for (int i = aantalPu - 1; i > -1; i--)
		{	if (!pijlUit[i].actief && !pijlUit[i].vast)
			{	//schuifveld.remove(pijlUit[i]);
				pijlUit[i] = null;
				for (int j = i; j < aantalPu - 1; j++)
				{	pijlUit[j] = pijlUit[j + 1];
				}
				aantalPu--;
				return;
			}
		}
	}
	
	public boolean meldAan(Pijl p, int x, int y)
	{	if (soort == 0)
		{	return false;
		}
		else if (soort == 1)
		{	//Rectangle ingang1 = new Rectangle(0, 0, getSize().width, getSize().height);
//gevoeligheid!!			
			Rectangle ingang1 = new Rectangle(xPos, yPos, breedte, hoogte);
			//if (pijlIn1 == null && ingang1.contains(x - getLocation().x, y - getLocation().y))
			if (pijlIn1 == null && ingang1.contains(x, y))
			{	pijlIn1 = p;
				//pijlIn1.zetEind(getLocation().x + 20, getLocation().y + 10);
				pijlIn1.zetEind(xPos + 20, yPos + 10);
				zetVeranderd(20);
				asv.tekenOpnieuw();
				return true;
			}
		}
		else if (soort == 2)
		{	//Rectangle ingang1 = new Rectangle(0, 0, getSize().width / 2, getSize().height);
//gevoeligheid!!			
			Rectangle ingang1 = new Rectangle(xPos, yPos, breedte / 2, hoogte);
			//Rectangle ingang2 = new Rectangle(getSize().width / 2, 0, getSize().width / 2, getSize().height);
//gevoeligheid!!			
			Rectangle ingang2 = new Rectangle(xPos + breedte / 2, yPos, breedte / 2, hoogte);
			//if (pijlIn1 == null && ingang1.contains(x - getLocation().x, y - getLocation().y))
			if (pijlIn1 == null && ingang1.contains(x, y))
			{	pijlIn1 = p;
				//pijlIn1.zetEind(getLocation().x + 10, getLocation().y + 10);
				pijlIn1.zetEind(xPos + 10, yPos + 10);
				zetVeranderd(20);
				asv.tekenOpnieuw();
				return true;
			}
			//else if (pijlIn2 == null && ingang2.contains(x - getLocation().x, y - getLocation().y))
			else if (pijlIn2 == null && ingang2.contains(x, y))
			{	pijlIn2 = p;
				//pijlIn2.zetEind(getLocation().x + 30, getLocation().y + 10);
				pijlIn2.zetEind(xPos + 30, yPos + 10);
				zetVeranderd(20);
				asv.tekenOpnieuw();
				return true;
			}
		}
		return false;
	}
	
	public void verbind(Pijl p, boolean links)
	{	
		if (soort == 1)
		{	pijlIn1 = p;
			//pijlIn1.zetEind(getLocation().x + 20, getLocation().y + 10);
			pijlIn1.zetEind(xPos + 20, yPos + 10);
		}
		else if (soort == 2)
		{	
			if (links)
			{	pijlIn1 = p;
				//pijlIn1.zetEind(getLocation().x + 10, getLocation().y + 10);
				pijlIn1.zetEind(xPos + 10, yPos + 10);
			}
			else 
			{	pijlIn2 = p;
				//pijlIn2.zetEind(getLocation().x + 30, getLocation().y + 10);
				pijlIn2.zetEind(xPos + 30, yPos + 10);
			}
		}
		
		
	}
	
	public void maakLos(Pijl p)
	{	if (p == pijlIn1)
		{	pijlIn1 = null;
		}
		else if (p == pijlIn2)
		{	pijlIn2 = null;
		}
	}
	
	public void setSize(int b, int h)
	{	//super.setSize(b, h);
		
		breedte = b;
		hoogte = h;
		
		for (int i = 0; i < aantalPu; i++)
		{	if (pijlUit[i].vast || pijlUit[i].actief)
			{	//pijlUit[i].zetBegin(getLocation().x + getSize().width - 20 ,getLocation().y + getSize().height + 10);
				pijlUit[i].zetBegin(xPos + breedte - 20 ,yPos + hoogte + 10);
			}
			else 
			{	//pijlUit[i].zetPlaats(getLocation().x + getSize().width - 20 ,getLocation().y + getSize().height + 10);
				pijlUit[i].zetPlaats(xPos + breedte - 20 ,yPos + hoogte + 10);
			}
		}
	}
	
	public void zetMaat()
	{
	}
	
	public Expressie geefUitvoer(int max)
	{	return null;
	}

	public Expressie geefVerborgenUitvoer(int max)
	{	return null;
	}
	
	public void zetVeranderd(int max)
	{	for (int i = 0; i < aantalPu; i++)
		{	if (pijlUit[i].vast && max > 0)
			{	pijlUit[i].ontvanger.zetVeranderd(max - 1);
			}
		}
	
		asv.changed = true;
	}
	
	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		if (asv.isDemo)
			return;		
		if (asv.frozen)
			return;
		
		
		//requestFocus();
		super.mouseDownTouchStartAction(eventX, eventY);
	}
	
	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		if (asv.alleenInvullen)
			return;
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;
		

		if ((this instanceof UitvoerSchuifComponent) &&
				(((UitvoerSchuifComponent) this).muisrechts)
			   )	
				return;
		
		if (isStapel)
		{	asv.zetStapel(this);
			isStapel = false;
			asv.tekenOpnieuw();
		}
		super.mouseMoveTouchMoveAction(eventX, eventY);
		
		//int dx = e.getX() - startx;
		//int dy =  e.getY() - starty;
		for (int i = 0; i < aantalPu; i++)
		{	if (pijlUit[i] != null)
				pijlUit[i].verplaatsBegin(dx, dy);
		}
		if (pijlIn1 != null)
			pijlIn1.verplaatsEind(dx, dy);
		if (pijlIn2 != null)
			pijlIn2.verplaatsEind(dx, dy);
		
		if (this instanceof UitvoerSchuifComponent)
		{
			UitvoerSchuifComponent uvsc = (UitvoerSchuifComponent) this;
			if (uvsc.tabel != null)
			{
				uvsc.tabel.xPos += dx;
				uvsc.tabel.yPos += dy;
			}	
			
			uvsc.zoomInKnop.xPos += dx;
			uvsc.zoomInKnop.yPos += dy;
			uvsc.zoomUitKnop.xPos += dx;
			uvsc.zoomUitKnop.yPos += dy;

			
		}
		
		asv.tekenOpnieuw();
	}
	
	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction()
	{	
		if (asv.isDemo)
			return;		
		if (asv.frozen)
			return;
		
		//super.mouseReleased(e);
		asv.changed = true;
		
		mouseDown = false;
		dragging = false;

		
		//if (!isStapel && (getLocation().x < 80 || getLocation().x > schuifveld.getSize().width
		//				 || getLocation().y < 0 || getLocation().y > schuifveld.getSize().height))
		//{	((AlgebraSchuifVeld) schuifveld).verwijder(this);
		//}
		if (!isStapel && !(this instanceof GrafiekComponent) && asv.toolkit &&
						 (xPos < 80 || xPos > (asv.breedte-breedte) || 
						  yPos < 0 || yPos > (asv.hoogte-hoogte)))
		{	asv.verwijder(this);
		}
		
		asv.tekenOpnieuw();
	}
}


