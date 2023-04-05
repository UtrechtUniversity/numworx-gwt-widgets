package fi.algebrapijlengwt.client;

import fi.algebrapijlengwt.client.expressies_ap.*;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 *  superclass voor alle elementen in een pijlenketting; <br>
 *  handelt dragging van AlgebraSchuifComponenten af, wanneer dit toegestaan is (geen demo of alleen invullen); ook
 *  wordt het verbinden van AlgebraSchuifComponenten met Pijlen en het losmaken van Pijlen in deze klasse geregeld;<br>
 *  NB: een AlgebraSchuifComponent op het werkveld kan verwijderd worden door deze een stukje over de rand te slepen,
 *  terwijl de cursor binnen de rand blijft.<br>
 *  als de AlgebraSchuifComponent deel uitmaakt van een bona fide pijlenketting en geen UitvoerSchuifComponent 
 *  "vooraan" een pijlenketting is, dan is er sprake van een inkomende Expressie (als de UitvoerSchuifComponent 
 *  "vooraan" de pijlenketting ingevuld is) of een inkomende verborgen Expressie (UVS "vooraan" niet ingevuld") en
 *  kan een resultaat (verborgen) Expressie bepaald worden afhankelijk van het soort AlgebraSchuifComponent; dit
 *  gebeurt door herdefinieren van geefUitvoer resp. geefVerborgenUitvoer in de subklassen.     
 */
public class AlgebraSchuifComponent extends SchuifComponent 
{	
	/**
	 * de inkomende Pijl
	 */
	Pijl pijlIn1;
	/**
	 * de uitgaande Pijlen (maximum tien)
	 */
	Pijl[] pijlUit;
	/**
	 * het actuele aantal uitgaande Pijlen
	 */
	int aantalPu;
	/**
	 * is deze AlgebraSchuifComponent een stapel?
	 */
	boolean isStapel;
	/**
	 * uitgaande pijl(en) naar links?
	 */
	boolean links = false;
	/**
	 * heeft deze AlgebraSchuifComponent een label?<br>
	 * Opm: dat kan alleen als het een UitvoerSchuifComponent is
	 */
	boolean label = false;
	/**
	 * het werkveld 
	 */
	AlgebraSchuifVeld asv;
	/**
	 * de Context2d waarmee getekend wordt 
	 */
	Context2d ascContext2d;
	/**
	 * is deze AlgebraSchuifComponent zichtbaar? 
	 */
	boolean visible = true;
	/**
	 * constructor
	 * @param asv het werkveld
	 * @param x de x-positie van deze AlgebraSchuifComponent 
	 * @param y de y-positie van deze AlgebraSchuifComponent
	 * @param b de breedte van deze AlgebraSchuifComponent
	 * @param h de hoogte van deze AlgebraSchuifComponent
	 */
	public AlgebraSchuifComponent(AlgebraSchuifVeld asv, int x, int y, int b, int h)
	 {	super(x, y, b, h);
		this.asv = asv;
		ascContext2d = asv.asvContext2d;
		isStapel = true;
		aantalPu = 0;
		pijlUit = new Pijl[10];
	}
	/**
	 * is de AlgebraSchuifComponent een stapel en
	 * wijst(wijzen) de uitgaande pijl(en) van de AlgebraSchuifComponent naar links? 
	 * @return een HashMap met twee Booleans
	 */
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
	/**
	 * moet de AlgebraSchuifComponent een stapel worden en moet(en) 
	 * de uitgaande pijl(en) van de AlgebraSchuifComponent naar links wijzen?
	 * @param map een Map met twee Booleans
	 */
    public void setState(Map<String,Object> map)
    {  	ObjectMap h = JSONUtilities.wrapMap(map);
    	boolean isStapel = h.getBoolean("isStapel");
		boolean links = h.getBoolean("links");
		this.isStapel = isStapel;
		this.links = links;
		zetLinks(links);
    }
    /**
     * de uitgaande Pijl wijst naar links (links == true) of naar
     * rechts (links == false) 
     * @param b true/false
     */
	public void zetLinks(boolean b)
	{	links = b;
		for (int i = 0; i <aantalPu; i++)
		{	pijlUit[i].zetLinks(b);
			if (!links)
			{	pijlUit[i].zetPlaats(xPos + breedte + 9 ,yPos + 10 );
			}
			else 
			{	pijlUit[i].zetPlaats(xPos - 10 ,yPos + 10 );
			}
		}
	}
	/**
	 * maak ruimte voor een label boven de ASC (b == true),
	 * verwijder de ruimte voor een label (b == false) door
	 * de y-positie van de ASC te verkleinen/vergroten; alleen 
	 * voor UitvoerSchuifComponenten (zie subclass)
	 * @param b true/false
	 */
	public void toonLabel(boolean b)
	{	label = b;
		if (b) 
		{	yPos -= 20;
		}
		else 
		{	yPos += 20;
		}
	}
	/**
	 * redefined in subclass UitvoerSchuifComponent
	 * @param color de vakkleur
	 */
	public void zetVakKleur(CssColor color)
	{	
	}
	/**
	 * toon (b == true) of verberg (b == false) deze ASC
	 * @param b true/false
	 */
	public void setVisible(boolean b)
	{	visible = b;
		// vermijdt onnodige paints by asv.setState
		if (!asv.owner.asvSetState)
			asv.tekenOpnieuw();
	}
	
	public void paint()
	{	paint(ascContext2d);
	}

	/**
	 * teken het balletje waaraan inkomende pijlen bevestigd worden; <br>
	 * dit staat links van de AlgebraSchuifComponent als de uitgaande pijl(en) naar rechts wijzen en
	 * rechts van de AlgebraSchuifComponenet als de uitgaande pijl(en) naar links wijzen; houdt rekening
	 * met de eventuela aanwezigheid van een label
	 * @param gIm de Context2d waarmee getekend wordt
	 */
	public void paint(Context2d gIm)
  	{
		if (!visible)
			return;
		// grijs
		gIm.setFillStyle(CssColor.make(125,125,125));
		if (!links)
		{	if (label)
			{	gIm.beginPath();
               	gIm.arc(xPos + 5, yPos + 31, 3, 0, 2 * Math.PI);
               	gIm.fill();
			}
			else 
			{	gIm.beginPath();
				gIm.arc(xPos + 5, yPos + 11, 3, 0, 2 * Math.PI);
				gIm.fill();
			}
		}
		else 
		{	if (label)
			{	gIm.beginPath();
				gIm.arc(xPos + breedte - 6, yPos + 31, 3, 0, 2 * Math.PI);
				gIm.fill();
			}
			else 
			{	gIm.beginPath();
				gIm.arc(xPos + breedte - 6, yPos + 11, 3, 0, 2 * Math.PI);
				gIm.fill();
			}
		}
	}
	
	/**
	 * check of deze AlgebraSchuifComponent het punt (x,y) bevat; als deze ASC een UitvoerSchuifComponent is, 
	 * dan is dit inclusief eventueel label en/of tabel (zie klasse UitvoerSchuifComponent);
	 * @param x x-coordinaat
	 * @param y y-coordinaat
	 * @return true/false
	 */
	public boolean contains(int x, int y)
	{	
		if (!links)
		{	return (new Rectangle(xPos+10,yPos,breedte-10,hoogte)).contains(x,y);
		}
		else 
		{	return (new Rectangle(xPos,yPos,breedte-10,hoogte)).contains(x,y);
		
		}
	}
	
	/**
	 * maak deze AlgebraSchuifComponent, de uitgaande Pijlen en de hele pijlenketting verbonden met de
	 * ingaande Pijl zichtbaar/onzichtbaar; vergeet een eventuele Tabel niet  
	 * @param b true/false
	 */
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
			if (b && uvsc.zoomInTabel)
			{	uvsc.zoomInKnop.visible = b;
				uvsc.zoomUitKnop.visible = b;
			}
			else
			{	uvsc.zoomInKnop.visible = b;
				uvsc.zoomUitKnop.visible = b;
			}
		}
		for (int i = 0; i < aantalPu ; i++)
		{	pijlUit[i].setVisible(b);
		}
		if (pijlIn1 != null)
			pijlIn1.zender.zetKettingZichtbaar(b);
		paint();
	}

	/**
	 * vindt de naam van de default variabele van de UitvoerSchuifComponenet aan het begin van de pijlenketting waarvan
	 * deze AlgebraSchuifComponent een deel is; 
	 * @return de naam van de default variabele of null (geen bona fide pijlenketting)
	 */
	public String geefBronDefaultVarnaam()
	{	String result = null; 
		if ((pijlIn1 == null) && (this instanceof UitvoerSchuifComponent))
			result = ((UitvoerSchuifComponent) this).defaultVarnaam;
		else if (pijlIn1 != null)
			result = pijlIn1.zender.geefBronDefaultVarnaam();
		return result;
	}
	
	
	/**
	 * voeg een uitgaande Pijl p toe aan pijlUIt[], zet begin en eind van p op de goede plaats, en zet de zender van 
	 * p op deze ASC 
	 * @param p de toe te voegen Pijl
	 */
	public void voegPijlToe(Pijl p)
	{	if (aantalPu < 10)
		{	if (!links)
			{	if (label)
				{	p.zetPlaats(xPos + breedte + 9, yPos + 30);
				}
				else 
				{	p.zetPlaats(xPos + breedte + 9, yPos + 10);	
				}
			}
			else 
			{	if (label) 
				{	p.zetPlaats(xPos - 10, yPos + 30);
				}
				else 
				{	p.zetPlaats(xPos - 10, yPos + 10);
				}
			}
			pijlUit[aantalPu] = p;
			p.zetZender(this);
			aantalPu++;
		}
	}
	
	/**
	 * een uitgaande Pijl is losgemaakt van zijn ontvanger; 
	 * verwijder deze uit het array pijlUit[] door de overige
	 * uitgaande Pijlen naar links te schuiven
	 */
	public void verwijderPijl()
	{	for(int i=aantalPu-1 ; i>-1 ; i--)
		{	if(!pijlUit[i].actief && !pijlUit[i].vast)
			{	pijlUit[i] = null;
				for(int j=i ; j<aantalPu-1 ; j++)
				{	pijlUit[j] = pijlUit[j+1];
				}
				aantalPu--;
				return;
			}
		}
	}

	/**
	 * Pijl p is versleept met eindpunt (x-10,y) (pijl naar rechts) resp. (x+10,y) (pijl naar links); 
	 * definieer een "ingang" (een voldoende grote Rechthoek) en kijk of (x,y) daarin valt; 
	 * als deze ASC nog geen inkomende Pijl heeft, dan wordt Pijl p de nieuwe PijlIn1;
	 * update de pijlenketting 
	 * @param p versleepte Pijl
	 * @param x x-coordinaat "eindpunt" Pijl p
	 * @param y y-coordinaat "eindpunt" Pijl p
	 * @return true als Pijl p de inkomende Pijl van deze ASC wordt, false als niet
	 */
	public boolean meldAan(Pijl p, int x, int y)
	{	Rectangle ingang1;
		if (!links)
		{	ingang1 = new Rectangle(xPos-10, yPos, breedte + 10, hoogte + 5);
		}
		else 
		{	ingang1 = new Rectangle(xPos, yPos, breedte + 10, hoogte + 5);
		}
		if (pijlIn1 == null && ingang1.contains(x, y))
		{	pijlIn1 = p;
			if (!links)
			{	if (label) 
				{	pijlIn1.zetEind(xPos, yPos + 30);
				}
				else 
				{	pijlIn1.zetEind(xPos, yPos + 10);
				}
			}
			else 
			{	if (label) 
				{	pijlIn1.zetEind(xPos + breedte, yPos + 30);
				}
				else 
				{	pijlIn1.zetEind(xPos + breedte, yPos + 10);
				}
			}
			zetVeranderd(20);
			asv.tekenOpnieuw();
			return true;
		}
		return false;
	}

	/**
	 * zet de plaats van deze ASC; verander ook de plaats van de laatste uitgaande Pijl;
	 * alleen gebruikt in AlgebraSchuifVeld setState voor plaatscorrectie stapels, dus laatste = enigste
	 * @param x gewenste x-coordinaat
	 * @param y gewenste y-coordinaat
	 */
	public void zetPlaats(int x, int y)
	{
		xPos = x;
		yPos = y;
		Pijl p = pijlUit[aantalPu-1];
		if (!links)
		{	if (label)
			{	p.zetPlaats(xPos + breedte + 9, yPos + 30);
			}
			else 
			{	p.zetPlaats(xPos + breedte + 9, yPos + 10);
			}
		}
		else 
		{	if (label) 
			{	p.zetPlaats(xPos - 10, yPos + 30);
			}
			else 
			{	p.zetPlaats(xPos - 10, yPos + 10);
			}
		}
	}

	/**
	 * zet de plaats van deze ASC; verander ook de plaats van Pijl p;
	 * eenmalig gebruikt in AlgebraSchuifVeld setState()
	 * @param x gewenste x-coordinaat
	 * @param y gewenste y-coordinaat
	 * @param p Pijl die mee verplaatst moet worden
	 */
	public void zetPlaats(int x, int y, Pijl p)
	{
		xPos = x;
		yPos = y;
		if (!links)
		{	if (label)
			{	p.zetPlaats(xPos + breedte + 9, yPos + 30);
			}
			else 
			{	p.zetPlaats(xPos + breedte + 9, yPos + 10);
			}
		}
		else 
		{	if (label) 
			{	p.zetPlaats(xPos - 10, yPos + 30);
			}
			else 
			{	p.zetPlaats(xPos - 10, yPos + 10);
			}
		}
	}
	/**
	 * maak Pijl p tot ingaande Pijl van deze AlgebraSchuifComponent en zet the punt van Pijl p
	 * op de goede plaats; gebruikt voor setState 
	 * @param p de Pijl die inkomende Pijl wordt
	 */
	public void verbind(Pijl p)
	{	pijlIn1 = p;
		if (!links)
		{	if (label)
			{	pijlIn1.zetEind(xPos, yPos + 30);
			}
			else 
			{	pijlIn1.zetEind(xPos, yPos + 10);
			}
		}
		else 
		{	if (label)
			{	pijlIn1.zetEind(xPos + breedte, yPos + 30);
			}
			else 
			{	pijlIn1.zetEind(xPos + breedte, yPos + 10);
			}
		}
	}

	/**
	 * als Pijl p de inkomende Pijl is, zet de inkomende Pijl weer gelijk aan null (dus geen); 
	 * Pijl p zelf moet apart teruggezet worden (d.w.z. Pijl p wordt een alleen punt); zie klasse Pijl  
	 * @param p de Pijl p
	 */
	public void maakLos(Pijl p)
	{	if (p == pijlIn1)
		{	pijlIn1 = null;
		}
	}
	
	/**
	 * zet de afmeting van deze ASC; uitgaande pijlen die verbonden zijn, krijgen een nieuw beginpunt,
	 * uitgaande pijlen die niet verbonden zijn krijgen een nieuw begin- en eindpunt; verander ook 
	 * het eindpunt van de inkomende pijl (if any)
	 * @param b de gewenste breedte
	 * @param h de gewenste hoogte
	 */
	public void setSize(int b, int h)
	{	
		breedte = b;
		hoogte = h;
		
		for(int i=0 ; i<aantalPu ; i++)
		{	if(pijlUit[i].vast || pijlUit[i].actief)
			{	if(!links)
				{	if(label)
					{	pijlUit[i].zetBegin(xPos + breedte+10 ,yPos + 30 );
					}
					else
					{	pijlUit[i].zetBegin(xPos + breedte+10 ,yPos + 10 );
					}
				}
				else 
				{	if(label)
					{	pijlUit[i].zetBegin(xPos - 10 ,yPos + 30 );
					}
					else
					{	pijlUit[i].zetBegin(xPos - 10 ,yPos + 10 );
					}
				}
			}
			else 
			{	if(!links)
				{	if(label)
					{	pijlUit[i].zetPlaats(xPos + breedte + 9 ,yPos + 30 );
					}
					else
					{	pijlUit[i].zetPlaats(xPos + breedte + 9 ,yPos + 10 );
					}
				}
				else 
				{	if(label)
					{	pijlUit[i].zetPlaats(xPos -10 ,yPos + 30 );
					}
					else
					{	pijlUit[i].zetPlaats(xPos -10 ,yPos + 10 );
					}
				}
			}
			if(pijlIn1!=null)
			{	if(!links)
				{	if(label)
					{	pijlIn1.zetEind(xPos, yPos+30);
					}
					else
					{	pijlIn1.zetEind(xPos, yPos+10);
					}
				}
				else 
				{	if(label)
					{	pijlIn1.zetEind(xPos + breedte, yPos+30);
					}
					else
					{	pijlIn1.zetEind(xPos + breedte, yPos+10);
					}
				}
			}
		}
	}
	
	/**
	 * redefine in subklassen
	 */
	public void zetMaat()
	{
	}
	/**
	 * redefine in subklassen
	 */
	public void zetInvulWaarde()
	{
	}
	/**
	 * redefine in subklassen
	 * @param max maximum aantal uit te voeren stappen in de ketting
	 * @return de uitvoer als Expressie
	 */
	public Expressie geefUitvoer(int max)
	{	return null;
	}
	/**
	 * redefine in subklassen
	 * @param max maximum aantal uit te voeren stappen in de ketting
	 * @return de verborgen uitvoer als Expressie
	 */
	public Expressie geefVerborgenUitvoer(int max)
	{	return null;
	}
	
	/**
	 * update de ASC(s) die ontvanger zijn van een van de uitgaande pijlen van deze ASC; 
	 * update maximaal max stappen in de pijlenketting  
	 * @param max maximum aantal te updaten stappen
	 */
	public void zetVeranderd(int max)
	{	for(int i=0 ; i<aantalPu ; i++)
		{	if(pijlUit[i].vast && max>0)
			{	pijlUit[i].ontvanger.zetVeranderd(max-1);
			}
		}
		asv.answerChanged();
	}
	
	/**
	 * indien geen demo of alleen invullen, voer mouseDownTouchStartAction in de superclass SchuifComponent uit 
	 * (dit initieert dragging) 
	 */
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		if (asv.isDemo)
			return;
		if (asv.alleenInvullen)	
			return;
		super.mouseDownTouchStartAction(eventX, eventY);
	}
	
	/**
	 * indien geen demo of alleen invullen, voer mouseMoveTouchMoveAction in de superclass SchuifComponent uit 
	 * (dit handelt dragging van deze BewerkingSchuifComponent af); de pijlen van deze BewerkingSchuifComponent moeten
	 * apart verplaatst worden, evenals (in het geval van een UitvoerSchuifComponent) de Tabel (als die er is) 
	 * en de zoom-knoppen op de Tabel 
	 */
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		if (asv.alleenInvullen)	
			return;
		if (asv.isDemo)
			return;		
		// deze BewerkingSchuifComponent was een stapel die een stukje
		// verplaatst is; deze is dus "van de stapel gehaald", en er
		// moet een nieuwe stapel gemaakt worden
		if (isStapel)
		{	isStapel = false;
			asv.zetStapel(this);
			asv.tekenOpnieuw();
		}
		super.mouseMoveTouchMoveAction(eventX, eventY);
		// verplaats uitgaande pijlen
		for (int i = 0; i < aantalPu; i++)
		{	if (pijlUit[i] != null)
				pijlUit[i].verplaatsBegin(dx, dy);
		}
		// verplaats de ingaande pijl
		if (pijlIn1 != null)
			pijlIn1.verplaatsEind(dx, dy);
		// UitvoerSchuifComponent
		if (this instanceof UitvoerSchuifComponent)
		{	UitvoerSchuifComponent uvsc = (UitvoerSchuifComponent) this;
			// als nodig, verplaats Tabel en zoomknoppen
			if (uvsc.tabel != null)
			{	uvsc.tabel.xPos += dx;
				uvsc.tabel.yPos += dy;
			}
			uvsc.zoomInKnop.xPos += dx;
			uvsc.zoomInKnop.yPos += dy;
			uvsc.zoomUitKnop.xPos += dx;
			uvsc.zoomUitKnop.yPos += dy;
		}
		asv.tekenOpnieuw();
	}

	/**
	 * indien geen demo of alleen invullen, voer mouseUpTouchEndAction in de superclass SchuifComponent uit 
	 * (dit be-eindigt dragging van deze BewerkingSchuifComponent af); deze AlgebraSchuifComponent wordt 
	 * verwijderd door hem een stukje over  de rand van het werkveld te slepen; check bij een UitvoerSchuifComponent
	 * of er een tabel gewenst is
	 */
	public void mouseUpTouchEndAction()
	{	
		if (asv.isDemo)
			return;		
		if (asv.alleenInvullen)	
			return;
		dragging = false;
		// moet deze AlgebraSchuifComponent verwijderd worden?
		// NB mouseUp wordt niet geregistreerd buiten asv
		if (!isStapel && !(this instanceof GrafiekComponent) && asv.toolkit && 
						 (xPos < 80 || xPos > (asv.breedte-breedte) ||
						  yPos < 0 || yPos > (asv.hoogte-hoogte)))
		{	asv.verwijder(this);
		}
		// UitvoerSchuifComponent met tabelCheckBox (als die er is!) aangevinkt  
		if (!isStapel && this instanceof UitvoerSchuifComponent && (asv.owner.tabelBox != null))
		{	boolean tabelNodig = asv.owner.tabelBox.getValue();
			if (tabelNodig && !((UitvoerSchuifComponent) this).tabelZichtbaar)
			{	((UitvoerSchuifComponent) this).zetTabelAan(true);
			}
		}
		asv.tekenOpnieuw();
	}
}


