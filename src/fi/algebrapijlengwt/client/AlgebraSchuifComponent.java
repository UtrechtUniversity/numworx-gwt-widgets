package fi.algebrapijlengwt.client;

import fi.algebrapijlengwt.client.expressies_ap.*;

//import java.util.Hashtable;
import java.util.HashMap;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;


public class AlgebraSchuifComponent extends SchuifComponent 
{	
	 int soort;
	 Pijl pijlIn1;
	 Pijl pijlIn2;
	 Pijl[] pijlUit;
	 int aantalPu;
	 boolean isStapel;
	 boolean open;
	 boolean links = false;
	 boolean label = false;
	 
	 AlgebraSchuifVeld asv;
	 
	 Context2d ascContext2d;
	 
	 boolean visible = true;
	 
	 boolean mouseDown;
	 
	//public AlgebraSchuifComponent(int srt, SchuifVeld sv, int x, int y, int b, int h)
	 public AlgebraSchuifComponent(int srt, AlgebraSchuifVeld asv, int x, int y, int b, int h)
	 {	super(x, y, b, h);
		soort = srt;
		this.asv = asv;
		ascContext2d = asv.asvContext2d;
		isStapel = true;
		open = true;
		aantalPu = 0;
		pijlUit = new Pijl[10];
	}
	
	public HashMap<String,Object> getState()
	{	boolean isStapel = false;
		boolean links = false;	
		
		isStapel = this.isStapel;
		links = this.links;
		
		HashMap<String,Object> h = new HashMap<String,Object>();
	    h.put("isStapel", new Boolean(isStapel));
	    h.put("links", new Boolean(links));
	    
	    return h;
	}

    public void setState(HashMap<String,Object> h)
    {	boolean isStapel = ((Boolean) h.get("isStapel")).booleanValue();
		boolean links = ((Boolean) h.get("links")).booleanValue();
		
		this.isStapel = isStapel;
		this.links = links;
		
		zetLinks(links);
    }
    
	public void zetLinks(boolean b)
	{	links = b;
		for(int i=0 ; i<aantalPu ; i++)
		{	pijlUit[i].zetLinks(b);
			if (!links)
			{	//pijlUit[i].zetPlaats(getLocation().x + getSize().width+9 ,getLocation().y + 10 );
				pijlUit[i].zetPlaats(xPos + breedte + 9 ,yPos + 10 );
			}
			else 
			{	//pijlUit[i].zetPlaats(getLocation().x - 10 ,getLocation().y + 10 );
				pijlUit[i].zetPlaats(xPos - 10 ,yPos + 10 );
			}
		}
	}
	
	public void toonLabel(boolean b)
	{	label = b;
		if (b) 
		{	//setLocation(getLocation().x, getLocation().y - 20);
			yPos -= 20;
		}
		else 
		{	//setLocation(getLocation().x, getLocation().y + 20);
			yPos += 20;
		
		}
	}
	
	// redefined in UitvoerSchuifComponent
	public void zetVakKleur(CssColor color)
	{	
	}
	
	public void setVisible(boolean b)
	{
		visible = b;
		
		asv.tekenOpnieuw();
		
	}
	
	public void paint()
	{
		paint(ascContext2d);
	}
	//public void paint(Graphics gIm)
	public void paint(Context2d gIm)
  	{
		if (!visible)
			return;
		
		if (open)
		{	//gIm.setColor(Color.gray);
			gIm.setFillStyle(CssColor.make(125,125,125));
		
			if (!links)
			{	if (label)
				{	//gIm.fillOval(5,28,3,3);
                	gIm.beginPath();
                	//gIm.arc(xPos + 8, yPos + 31, 3, 0, 2 * Math.PI);
                	gIm.arc(xPos + 5, yPos + 31, 3, 0, 2 * Math.PI);
                	gIm.fill();
				
				}
				else 
				{	//gIm.fillOval(5,8,3,3);
					gIm.beginPath();
					//gIm.arc(xPos + 8, yPos + 11, 3, 0, 2 * Math.PI);
					gIm.arc(xPos + 5, yPos + 11, 3, 0, 2 * Math.PI);
					gIm.fill();
				}
				
			}
			else 
			{	if (label)
				{	//gIm.fillOval(5,28,3,3);
					gIm.beginPath();
					//gIm.arc(xPos + 8, yPos + 31, 3, 0, 2 * Math.PI);
					gIm.arc(xPos + 6, yPos + 31, 3, 0, 2 * Math.PI);
					gIm.fill();
				}
				else 
				{	//gIm.fillOval(getSize().width-7,8,3,3);
					gIm.beginPath();
					//gIm.arc(xPos + breedte - 4, yPos + 11, 3, 0, 2 * Math.PI);
					gIm.arc(xPos + breedte - 6, yPos + 11, 3, 0, 2 * Math.PI);
					gIm.fill();
				}
			}
		}
		//super.paint(gIm);
	}
	
	public boolean contains(int x, int y)
	{	//if (new Rectangle(0,getSize().height-120,10,25).contains(x,y))
		if (new Rectangle(xPos,yPos+hoogte-120,10,25).contains(x,y))
		{	return true;
		
		}
		//if (new Rectangle(0,getSize().height-70,10,25).contains(x,y))
		if (new Rectangle(xPos,yPos + hoogte-70,10,25).contains(x,y))
		{	return true;
		
		}
		if (!links)
		{	//return (new Rectangle(10,0,getSize().width-10,getSize().height)).contains(x,y);
			return (new Rectangle(xPos+10,yPos,breedte-10,hoogte)).contains(x,y);
		}
		else 
		{	//return (new Rectangle(0,0,getSize().width-10,getSize().height)).contains(x,y);
			return (new Rectangle(xPos,yPos,breedte-10,hoogte)).contains(x,y);
		
		}
	}
	
	public void zetKettingZichtbaar(boolean b)
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
		
		for (int i = 0; i < aantalPu ; i++)
		{	pijlUit[i].setVisible(b);
		}
		
		if (pijlIn1 != null)
			pijlIn1.zender.zetKettingZichtbaar(b);
//		setVisible(b);
//		for (int i = 0; i < aantalPu ; i++)
//		{	pijlUit[i].setVisible(b);
//		}
		paint();
	}
	
	public String geefBronDefaultVarnaam()
	{	String result = null; 
		if ((pijlIn1 == null) && (this instanceof UitvoerSchuifComponent))
			result = ((UitvoerSchuifComponent) this).defaultVarnaam;
		else if (pijlIn1 != null)
			result = pijlIn1.zender.geefBronDefaultVarnaam();
		
		return result;
		
	}
	public void voegPijlToe(Pijl p)
	{	if (aantalPu < 10)
		{	if (!links)
			{	if (label)
				{	//p.zetPlaats(getLocation().x + getSize().width + 9, getLocation().y + 30);
					p.zetPlaats(xPos + breedte + 9, yPos + 30);
				}
				else 
				{	//p.zetPlaats(getLocation().x + getSize().width + 9, getLocation().y + 10);
					p.zetPlaats(xPos + breedte + 9, yPos + 10);	
				
				}
			}
			else 
			{	if (label) 
				{	//p.zetPlaats(getLocation().x - 10, getLocation().y + 30);
					p.zetPlaats(xPos - 10, yPos + 30);
				
				}
				else 
				{	//p.zetPlaats(getLocation().x - 10, getLocation().y + 10);
					p.zetPlaats(xPos - 10, yPos + 10);
				
				}
			}
			pijlUit[aantalPu] = p;

			//schuifveld.add(p);

			p.zetZender(this);
			aantalPu++;
		}
	}
	
	public void verwijderPijl()
	{	for(int i=aantalPu-1 ; i>-1 ; i--)
		{	if(!pijlUit[i].actief && !pijlUit[i].vast)
			{	
				//schuifveld.remove(pijlUit[i]);
				
				pijlUit[i] = null;
				for(int j=i ; j<aantalPu-1 ; j++)
				{	pijlUit[j] = pijlUit[j+1];
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
		//else if(soort==1)
		{	Rectangle ingang1;
			if (!links)
			{	//ingang1 = new Rectangle(-10, 0, getSize().width + 10, getSize().height + 5);
				ingang1 = new Rectangle(xPos-10, yPos, breedte + 10, hoogte + 5);
			
			}
			else 
			{	//ingang1 = new Rectangle(0, 0, getSize().width + 10, getSize().height + 5);
				ingang1 = new Rectangle(xPos, yPos, breedte + 10, hoogte + 5);
			
			}
			//if (pijlIn1 == null && ingang1.contains(x - getLocation().x, y - getLocation().y))
			//if (pijlIn1 == null && ingang1.contains(x - xPos, y - yPos))
			if (pijlIn1 == null && ingang1.contains(x, y))
			{	pijlIn1 = p;
				if (!links)
				{	if (label) 
					{	//pijlIn1.zetEind(getLocation().x, getLocation().y + 30);
						pijlIn1.zetEind(xPos, yPos + 30);
					
					}
					else 
					{	//pijlIn1.zetEind(getLocation().x, getLocation().y + 10);
						pijlIn1.zetEind(xPos, yPos + 10);
					
					}
				}
				else 
				{	if (label) 
					{	//pijlIn1.zetEind(getLocation().x + getSize().width, getLocation().y + 30);
						pijlIn1.zetEind(xPos + breedte, yPos + 30);
					
					}
					else 
					{	//pijlIn1.zetEind(getLocation().x + getSize().width, getLocation().y + 10);
						pijlIn1.zetEind(xPos + breedte, yPos + 10);
					
					}
				}
				zetVeranderd(20);
				asv.tekenOpnieuw();
				return true;
			}
		}
		return false;
	}
	
	public void zetPlaats(int x, int y)
	{
		//setLocation(x,y);
		xPos = x;
		yPos = y;
		Pijl p = pijlUit[aantalPu-1];
		if (!links)
		{	if (label)
			{	//p.zetPlaats(getLocation().x + getSize().width + 9, getLocation().y + 30);
				p.zetPlaats(xPos + breedte + 9, yPos + 30);
			}
			else 
			{	//p.zetPlaats(getLocation().x + getSize().width + 9, getLocation().y + 10);
				p.zetPlaats(xPos + breedte + 9, yPos + 10);
			
			}
		}
		else 
		{	if (label) 
			{	//p.zetPlaats(getLocation().x - 10, getLocation().y + 30);
				p.zetPlaats(xPos - 10, yPos + 30);
			}
			else 
			{	//p.zetPlaats(getLocation().x - 10, getLocation().y + 10);
				p.zetPlaats(xPos - 10, yPos + 10);
			
			}
		}
	}
	
	

	public void zetPlaats(int x, int y, Pijl p)
	{
		//setLocation(x,y);
		xPos = x;
		yPos = y;
		//Pijl p = pijlUit[aantalPu-1];
		if (!links)
		{	if (label)
			{	//p.zetPlaats(getLocation().x + getSize().width + 9, getLocation().y + 30);
				p.zetPlaats(xPos + breedte + 9, yPos + 30);
			}
			else 
			{	//p.zetPlaats(getLocation().x + getSize().width + 9, getLocation().y + 10);
				p.zetPlaats(xPos + breedte + 9, yPos + 10);
			
			}
		}
		else 
		{	if (label) 
			{	//p.zetPlaats(getLocation().x - 10, getLocation().y + 30);
				p.zetPlaats(xPos - 10, yPos + 30);
			}
			else 
			{	//p.zetPlaats(getLocation().x - 10, getLocation().y + 10);
				p.zetPlaats(xPos - 10, yPos + 10);
			
			}
		}
	}
	
	
	public void verbind(Pijl p)
	{	pijlIn1 = p;
		if (!links)
		{	if (label)
			{	//pijlIn1.zetEind(getLocation().x, getLocation().y + 30);
				pijlIn1.zetEind(xPos, yPos + 30);
			
			}
			else 
			{	//pijlIn1.zetEind(getLocation().x, getLocation().y + 10);
				pijlIn1.zetEind(xPos, yPos + 10);
			}
		}
		else 
		{	if (label)
			{	//pijlIn1.zetEind(getLocation().x + getSize().width + 10, getLocation().y + 30);
				pijlIn1.zetEind(xPos + breedte, yPos + 30);
			}
			else 
			{	//pijlIn1.zetEind(getLocation().x + getSize().width + 10, getLocation().y + 10);
				pijlIn1.zetEind(xPos + breedte, yPos + 10);
			}
//System.out.println("verbind " + (getLocation().x + getSize().width));		
		}
	}

	public void maakLos(Pijl p)
	{	if(p==pijlIn1)
		{	pijlIn1 = null;
		}
	}
	
	public void setSize(int b, int h)
	{	//super.setSize(b,h);
		breedte = b;
		hoogte = h;
		
		for(int i=0 ; i<aantalPu ; i++)
		{	if(pijlUit[i].vast || pijlUit[i].actief)
			{	if(!links)
				{	
					if(label)
					{	//pijlUit[i].zetBegin(getLocation().x + getSize().width+10 ,getLocation().y + 30 );
						pijlUit[i].zetBegin(xPos + breedte+10 ,yPos + 30 );
					}
					else
					{	//pijlUit[i].zetBegin(getLocation().x + getSize().width+10 ,getLocation().y + 10 );
						pijlUit[i].zetBegin(xPos + breedte+10 ,yPos + 10 );
					}
				}
				else 
				{	if(label)
					{	//pijlUit[i].zetBegin(getLocation().x - 10 ,getLocation().y + 30 );
						pijlUit[i].zetBegin(xPos - 10 ,yPos + 30 );
					}
					else
					{	//pijlUit[i].zetBegin(getLocation().x - 10 ,getLocation().y + 10 );
						pijlUit[i].zetBegin(xPos - 10 ,yPos + 10 );
					}
				}
			}
			else 
			{	if(!links)
				{	if(label)
					{	//pijlUit[i].zetPlaats(getLocation().x + getSize().width+9 ,getLocation().y + 30 );
						pijlUit[i].zetPlaats(xPos + breedte + 9 ,yPos + 30 );
					}
					else
					{	//pijlUit[i].zetPlaats(getLocation().x + getSize().width+9 ,getLocation().y + 10 );
						pijlUit[i].zetPlaats(xPos + breedte + 9 ,yPos + 10 );
					}
				}
				else 
				{	if(label)
					{	//pijlUit[i].zetPlaats(getLocation().x -10 ,getLocation().y + 30 );
						pijlUit[i].zetPlaats(xPos -10 ,yPos + 30 );
					}
					else
					{	//pijlUit[i].zetPlaats(getLocation().x -10 ,getLocation().y + 10 );
						pijlUit[i].zetPlaats(xPos -10 ,yPos + 10 );
					}
				}
			}
			if(pijlIn1!=null)
			{	if(!links)
				{	if(label)
					{	//pijlIn1.zetEind(getLocation().x  , getLocation().y+30);
						pijlIn1.zetEind(xPos, yPos+30);
					}
					else
					{	//pijlIn1.zetEind(getLocation().x  , getLocation().y+10);
						pijlIn1.zetEind(xPos, yPos+10);
					}
				}
				else 
				{	if(label)
					{	//pijlIn1.zetEind(getLocation().x + getSize().width   , getLocation().y+30);
						pijlIn1.zetEind(xPos + breedte, yPos+30);
					}
					else
					{	//pijlIn1.zetEind(getLocation().x + getSize().width  , getLocation().y+10);
						pijlIn1.zetEind(xPos + breedte, yPos+10);
					}
				}
			}
		}
	}
	
	public void zetMaat()
	{
	}
	public void zetInvulWaarde()
	{
	}
	
	public Expressie geefUitvoer(int max)
	{	return null;
	}
	
	public Expressie geefVerborgenUitvoer(int max)
	{	return null;
	}
	
	public void zetVeranderd(int max)
	{	for(int i=0 ; i<aantalPu ; i++)
		{	if(pijlUit[i].vast && max>0)
			{	pijlUit[i].ontvanger.zetVeranderd(max-1);
			}
		}
	
		asv.changed = true;
	}
	
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		if (asv.fixed)
			return;
		if (asv.isDemo)
			return;		
		if (asv.frozen)
			return;		
		
		super.mouseDownTouchStartAction(eventX, eventY);
	}
	
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		if (asv.fixed)
			return;
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
		{	isStapel = false;
			asv.zetStapel(this);

			asv.tekenOpnieuw();
		}

		super.mouseMoveTouchMoveAction(eventX, eventY);

		for (int i = 0; i < aantalPu; i++)
		{	if (pijlUit[i] != null)
				pijlUit[i].verplaatsBegin(dx, dy);
		}
		if (pijlIn1 != null)
			pijlIn1.verplaatsEind(dx, dy);
		//if (pijlIn2 != null)
		//	pijlIn2.verplaatsEind(dx, dy);
		
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
	
	public void mouseUpTouchEndAction()
	{	if (asv.fixed)
			return;
		if (asv.isDemo)
			return;		
		if (asv.frozen)
			return;
		
		asv.changed = true;
		
		mouseDown = false;
		dragging = false;
		
		//if (!isStapel && (getLocation().x < 80 || getLocation().x > schuifveld.getSize().width
		//				|| getLocation().y < 0 || getLocation().y > schuifveld.getSize().height))
		if (!isStapel && !(this instanceof GrafiekComponent) && asv.toolkit && 
						 (xPos < 80 || xPos > (asv.breedte-breedte) ||
						  yPos < 0 || yPos > (asv.hoogte-hoogte)))
		{	asv.verwijder(this);
		}
		
// check dit!!: mouseUp wordt niet geregistreerd buiten asv
		
		if (!isStapel && this instanceof UitvoerSchuifComponent && (asv.owner.tabelBox != null))
		{	
			
		
//			boolean tabelNodig = ((AlgebraSchuifVeld) schuifveld).tabelCheckbox.isSelected();
			boolean tabelNodig = asv.owner.tabelBox.getValue();
			if (tabelNodig && !((UitvoerSchuifComponent) this).tabelZichtbaar)
			{	((UitvoerSchuifComponent) this).zetTabelAan(true);
			}
		}
		
		asv.tekenOpnieuw();
	}
}


