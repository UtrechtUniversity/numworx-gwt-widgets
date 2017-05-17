package fi.algebrapijlengwt.client;

import fi.algebrapijlengwt.client.expressies_ap.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.LayoutPanel;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

/**
 * superclass voor alle bewerkingen die veranderbaar zijn, dus optellen (plus een getal),
 * aftrekken (min een getal), vermeningvuldigen (keer een getal), delen (delen door een getal),
 * en machtsverheffen (tot de macht een getal);<br>
 * een doubleClick op de bewerkingSchuifComponent doet een TeksPopup verschijnen (zie klasse TekstPopup),
 * d.w.z. een PopupMenu met een TextBox erin waarin de nieuwe waarde ingevoerd kan worden (dit moet
 * een getal zijn).     
 */
public class BewerkingSchuifComponent extends AlgebraSchuifComponent
{	
	/**
	 * het getal in de bewerking als Expressie
	 */
	BasisExpressie beginw;
	/**
	 * de TekstPopup voor editing (zie klasse TekstPopup)
	 */
	TekstPopup tf;
	/**
	 * het basisPanel in AlgebraPijlenGWT, nodig om de TekstPopup goed te positioneren
	 */
	LayoutPanel inputOwner;
	/**
	 * de text uit de TekstPopup
	 */
	String tfString = "";
	/**
	 * de fontSize voor de BasisExpressie, zie methode zetMaat() in klasse BasisExpressie  
	 */
	int fontSize = 14;
	/**
	 * constructor
	 * @param asv het werkveld
	 * @param x de x-positie van deze BewerkingSchuifComponent
	 * @param y de y-positie van deze BewerkingSchuifComponent
	 * @param b de breedte van deze BewerkingSchuifComponent
	 * @param h de hoogte van deze BewerkingSchuifComponent
	 */
	public BewerkingSchuifComponent(AlgebraSchuifVeld asv,int x, int y, int b, int h)
	{	super(asv, x, y, b, h);
		beginw = new BasisExpressie("3");
		inputOwner = asv.owner.canvasPanel;
	}
	/**
	 * toon de TekstPopup ingevuld met de huidige waarde van de bewerking
	 */
	public void showTekstPopup()
	{	// onder de BewerkingSchuifComponent
		int popupX = xPos + 10 + inputOwner.getAbsoluteLeft();
		int popupY = yPos + hoogte + inputOwner.getAbsoluteTop();
		// als de TekstPopup nog open is, verwerk de inhoud
		if ((tf != null) && tf.isVisible())
		{	zetInvulWaarde();
		}
		// maak een nieuwe TekstPopup
		tf = new TekstPopup(this,false);
		
		if (!"".equals(tfString))
			tf.setText(tfString);
		else if (beginw != null) 
		{
			// format voor grote getallen met wetenschappelijke notatie zoals 1234567^2 = 1524155677489...
			String formatted = NumberFormat.getFormat("0.###").format(beginw.geefWaarde());
			formatted = formatted.replace(',', '.'); // dit moet, anders gaat BasisExpressie.geefWaarde() met Double.valueOf(basisString) mis
			tf.setText(formatted);
		}
		else
		{
			tf.setText("");
		}
		tf.resize();
		
		tf.setWidth("25px");
		tf.setHeight("20px");
		tf.setPopupPosition(popupX, popupY);
		tf.show();
		tf.setFocus(true);
		tf.setSelected();
	}
	/**
	 * stop de basisString van de BasisExpressie beginw in een HashMap
	 */
	public HashMap<String,Object> getState()
	{	
		String basisExp  = null;
		basisExp = this.beginw.basisString;
		HashMap<String,Object> h = super.getState();
	    h.put("basisExp", basisExp);
	    return h;
	}
	/**
	 * bepaal de BasisExpressie beginw m.b.v. de String basisExp uit de Map;
	 * bepaal de afmeting van deze BewerkingSchuifComponent    
	 */
    public void setState(Map<String,Object> map)
    {	
    	ObjectMap h = JSONUtilities.wrapMap(map);
    	String basisExp = h.getString("basisExp");
		beginw = new BasisExpressie(basisExp);
		beginw.zetMaat(fontSize, ascContext2d);
		super.setState(map);
		zetMaat();
    }
    
    /**
     * zet de uitgaande pijl(en) van deze BewerkingSchuifComponent naar links (b == true)
     * of naar rechts (b == false), zie klasse Pijl
     */
	public void zetLinks(boolean b)
	{	links = b;
		for (int i = 0; i < aantalPu ; i++)
		{	pijlUit[i].zetLinks(b);
			if (!links)
			{	pijlUit[i].zetPlaats(xPos + breedte + 9,yPos + 10);
			}
			else 
			{	pijlUit[i].zetPlaats(xPos - 10, yPos + 10);
			
			}
		}
	}
	
	/**
	 * teken achtergrond and rand van deze BewerkingSchuifComponent
	 */
	public void paint(Context2d g)
  	{	
		if (!visible)
			return; 
		if (!links)
		{	//oranje achtergrond
			g.setFillStyle(CssColor.make(255, 200, 0));
			g.fillRect(xPos + 10, yPos + 0, breedte - 11, hoogte - 1);
			//zwarte rand
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			g.strokeRect(xPos + 10,yPos + 0,breedte-11,hoogte-1);
		}
		else
		{	//oranje achtergrond;
			g.setFillStyle(CssColor.make(255, 200, 0));
			g.fillRect(xPos + 0, yPos + 0, breedte - 11, hoogte - 1);
			//zwarte rand
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			g.strokeRect(xPos + 0,yPos + 0,breedte-11,hoogte-1);
		}	
		super.paint(g); // dit is het balletje voor de ingaande pijl
	}
	
	/**
	 * bepaal de maat van deze BSC
	 */
	public void zetMaat()
	{	// basisafmeting
		int b = AlgebraSchuifVeld.basisB; 
		int h = AlgebraSchuifVeld.basisH;
		// corrigeer m.b.v. beginw.breedte (bepaald in zetInvulWaarde)
		if (beginw != null)
		{	b = beginw.breedte;
			if (b > 10)
				b = b+40;
			else 
				b = AlgebraSchuifVeld.basisB; 
		}
		// dit wel: past de pijlen aan
		setSize(b,h);
		breedte = b;
		hoogte = h;
	}

	/**
	 * kijk of de String in de TekstPopup een bona fide
	 * invoerwaarde is en verwerk die; update de pijlenketting 
	 */
	public void zetInvulWaarde()
	{	boolean isGeldigeInvoer = true;
		{	try
			{	String s = tf.getText();
				s = s.replace(',','.');
				tf.setText(s);
				Double w = Double.valueOf(tf.getText());
			}
			catch(NumberFormatException ex)
			{	isGeldigeInvoer = false;
				tf.setText(UF.format0(beginw.geefWaarde(),3));
			}
		}
		if (isGeldigeInvoer)
		{ 	tfString = tf.getText();
			beginw = new BasisExpressie(tf.getText());
			beginw.zetMaat(fontSize, ascContext2d);
		}
		else
		{	beginw = new BasisExpressie("3");
			beginw.zetMaat(fontSize, ascContext2d);
		}
		zetMaat();
		zetVeranderd(20);
		tf.setVisible(false);
		inputOwner.remove(tf);
		asv.tekenOpnieuw();
	}
	
	/**
	 * extra actie t.o.v. superklasse: initiliseer double en long click 
	 */
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	if (asv.isDemo)
			return;
        taptime = System.currentTimeMillis();
        doubletap.add(taptime);
		super.mouseDownTouchStartAction(eventX, eventY);
	}	
	
	/**
	 * extra actie t.o.v. superklasse: TekstPopup bij double click
	 */
	public void mouseUpTouchEndAction()
	{	
		if (asv.isDemo)
			return; 
		if (isDoubleClick()) 
		{	if (!isStapel)
			{	showTekstPopup();
			}
            doubletap.clear();
        } 
		else if (isLongClick()) 
		{    doubletap.clear();
        } 
		else 
		{   if (doubletap.size() >= 2) 
            {  	doubletap.remove(0);
            }
        }
		super.mouseUpTouchEndAction();
	}
	
	/**
	 * Geef de expressie. Dit wordt gebruikt om de 'oude' waarde op te vragen
	 * na druk op escape-toets.
	 * 
	 */
	String geefExpressieString()
	{
		return beginw.toString();
	}
}
