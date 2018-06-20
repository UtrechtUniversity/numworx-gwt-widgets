package fi.tekenveelvlakgwt.client;

import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;

/**
 * klasse die d.m.v. 4 instanties van Viewer3d het bovenaanzicht, vooraanzicht, linkerzijaanzicht
 * en rechterzijaanzicht van een 3d-veelvlak toont  
 * @author Peter Boon
 */
class VaktekPanel extends LayoutPanel
{
	/**
	 * eigenaar van dit VaktekPanel
	 */
	TekenVeelvlakGWT tvGWT;

	/**
	 * breedte en hoogte van dit VaktekPanel
	 */
	int breedte, hoogte;
	/**
	 * breedte en hoogte van een instantie van Viewer3d  
	 */
	int vakBreedte;
	/**
	 * viewer voor vooraanzicht 
	 */
	Viewer3d va;
	/**
	 * viewer voor bovenaanzicht.
	 */
	Viewer3d ba;
	/**
	 * viewer voor rechterzijaanzicht.
	 */
	Viewer3d ra;
	/**
	 * viewer voor linkerzijaanzicht.
	 */
	Viewer3d la;

	/**
	 * kunnen vlakken in de aanzichten gekleurd worden door erop te klikken?
	 */
	boolean vlakkenKleurenOptie = false;

	/**
	 * knop om na te kijken (instelbaar)
	 */
	PushButton kijkNaButton;
	/**
	 * Panel voor kijkNaButton
	 */
	LayoutPanel kijkNaPanel;

	/**
	 * constructor: maak een vaktekening met 4 lege viewers ingesteld voor de 4 aanzichten en met de gegeven maat.
	 * @param x x-coordinaat 	
	 * @param y y-coordinaat
	 * @param b breedte
	 * @param h hoogte
	 * @param tvGWT eigenaar van dit VaktekPanel
	 */
	public VaktekPanel(int x, int y, int b, int h, TekenVeelvlakGWT tvGWT)
	{
		this(new Veelvlak(), new Veelvlak(), new Veelvlak(), new Veelvlak(), x, y, b, h, tvGWT);
	}
	/**
	 * constructor: maak een vaktekening met de gegeven Veelvlakken als aanzichten met de gegeven maat.<br>
	 * gebruikt met lege Veelvlakken om de 4 viewers zo in te stellen dat ze de 4 aanzichten tonen 
	 * @param vva Veelvlak voor vooraanzicht
	 * @param vra Veelvlak voor rechterzijaanzicht
	 * @param vla Veelvlak voor linkerzijaanzicht
	 * @param vba Veelvlak voor bovenaanzicht
	 * @param x x-coordinaat 	
	 * @param y y-coordinaat
	 * @param b breedte
	 * @param h hoogte
	 * @param tvGWT eigenaar van dit VaktekPanel
	 */
	public VaktekPanel(Veelvlak vva, Veelvlak vra, Veelvlak vla, Veelvlak vba, int x, int y, int b, int h,
		TekenVeelvlakGWT tvGWT)
	{
		this.tvGWT = tvGWT;

		breedte = b;
		hoogte = h;
		vakBreedte = Math.min((breedte - 6) / 3, (hoogte - 6) / 2);

		// linkerzijaanzicht (op rij 2)
		la = new Viewer3d(vla, breedte / 2 - 3 * vakBreedte / 2 + 1, hoogte / 2 + 1, vakBreedte - 1, vakBreedte - 1,
			tvGWT);
		la.vaktek = this;
		la.k = 230;
		la.zetAfstand(10000000);
		la.zetSchaduw(false);
		la.zetBeginHoeken(0, 90); // linkerzijaanzicht
		la.zetMuisAan(false);
		la.border = true;
		add(la.canvas);
		setWidgetLeftWidth(la.canvas, breedte / 2 - 3 * vakBreedte / 2 + 1, Style.Unit.PX, vakBreedte - 1,
			Style.Unit.PX);
		setWidgetTopHeight(la.canvas, vakBreedte + 1, Style.Unit.PX, vakBreedte - 1, Style.Unit.PX);

		// bovenaanzicht (op rij 1)
		ba = new Viewer3d(vba, breedte / 2 - vakBreedte / 2 + 1, hoogte / 2 - vakBreedte + 1, vakBreedte - 1,
			vakBreedte - 1, tvGWT);
		ba.vaktek = this;
		ba.k = 230;
		ba.zetAfstand(10000000);
		ba.zetSchaduw(false);
		ba.zetBeginHoeken(90, 0); // bovenaanzicht
		ba.zetMuisAan(false);
		ba.border = true;
		add(ba.canvas);
		setWidgetLeftWidth(ba.canvas, breedte / 2 - vakBreedte / 2 + 1, Style.Unit.PX, vakBreedte - 1, Style.Unit.PX);
		setWidgetTopHeight(ba.canvas, 1, Style.Unit.PX, vakBreedte - 1, Style.Unit.PX);

		// vooraanzicht (op rij 2)
		va = new Viewer3d(vva, breedte / 2 - vakBreedte / 2 + 1, hoogte / 2 + 1, vakBreedte - 1, vakBreedte - 1, tvGWT);
		va.vaktek = this;
		va.k = 230;
		va.zetAfstand(10000000);
		va.zetSchaduw(false);
		va.zetBeginHoeken(0, 0); // vooraanzicht
		va.zetMuisAan(false);
		va.border = true;
		add(va.canvas);
		setWidgetLeftWidth(va.canvas, breedte / 2 - vakBreedte / 2 + 1, Style.Unit.PX, vakBreedte - 1, Style.Unit.PX);
		setWidgetTopHeight(va.canvas, vakBreedte + 1, Style.Unit.PX, vakBreedte - 1, Style.Unit.PX);

		// rechterzijaanzicht (op rij 2)
		ra = new Viewer3d(vra, breedte / 2 - vakBreedte / 2 + vakBreedte + 1, hoogte / 2 + 1, vakBreedte - 1,
			vakBreedte - 1, tvGWT);
		ra.vaktek = this;
		ra.k = 230;
		ra.zetAfstand(10000000);
		ra.zetSchaduw(false);
		ra.zetBeginHoeken(0, -90); // rechterzijaanzicht
		ra.zetMuisAan(false);
		ra.border = true;
		add(ra.canvas);
		setWidgetLeftWidth(ra.canvas, breedte / 2 - vakBreedte / 2 + vakBreedte + 1, Style.Unit.PX, vakBreedte - 1,
			Style.Unit.PX);
		setWidgetTopHeight(ra.canvas, vakBreedte + 1, Style.Unit.PX, vakBreedte - 1, Style.Unit.PX);

		kijkNaButton = new PushButton(TekenVeelvlakGWT.rb.kijkNaLabel());
		kijkNaPanel = new LayoutPanel();
		kijkNaPanel.setStylePrimaryName("kijknapanel");

		add(kijkNaPanel);
		setWidgetLeftWidth(kijkNaPanel, (breedte - 120) / 2, Style.Unit.PX, 100, Style.Unit.PX);
		setWidgetTopHeight(kijkNaPanel, hoogte - 25, Style.Unit.PX, 25, Style.Unit.PX);

		if (!tvGWT.isCheckExternalModus())
		{
			kijkNaPanel.add(kijkNaButton);
			kijkNaPanel.setWidgetLeftWidth(kijkNaButton, 0, Style.Unit.PX, 80, Style.Unit.PX);
			kijkNaPanel.setWidgetTopHeight(kijkNaButton, 0, Style.Unit.PX, 25, Style.Unit.PX);
		}

		kijkNaButton.addClickHandler(new PushClickHandler());
		kijkNaButton.addStyleName(tvGWT.getTekenVeelvlakCss().pushbutton());

		setWidgetVisible(kijkNaPanel, false);
	}

	/**
	 * init de Context2d voor de 4 viewers 
	 */
	public void initContext2d()
	{
		va.initContext2d();
		ra.initContext2d();
		la.initContext2d();
		ba.initContext2d();
	}

	/**
	 * toon het 3d-veelvlak gecodeerd in map in elk
	 * van de 4 viewers
	 * @param map staus van het 3d-veelvlak
	 */
	public void setState(Map map)
	{
		va.setState(map);
		ra.setState(map);
		la.setState(map);
		ba.setState(map);
	}

	/**
	 * teken de 4 viewers
	 */
	public void paint()
	{
		va.paint();
		ra.paint();
		la.paint();
		ba.paint();

	}

	/**
	 * zet de vlakkenKleurenOptie in de 4 viewers
	 * @param b true/false
	 */
	public void zetVlakkenKleurenOptie(boolean b)
	{
		vlakkenKleurenOptie = b;
		va.zetVlakkenKleurenOptie(b);
		ra.zetVlakkenKleurenOptie(b);
		la.zetVlakkenKleurenOptie(b);
		ba.zetVlakkenKleurenOptie(b);
	}

	/**
	 * kleur alle vlakken in alle viewers weer oranje
	 */
	public void resetColors()
	{
		va.resetColors();
		ra.resetColors();
		la.resetColors();
		ba.resetColors();
	}

	/**
	 * get de huidige kleurnamen voor de vlakken in de 3d-figuur<br>
	 * als een vlak in een van de aanzichten roodgekleurd is,
	 * zorg dan dat het ook rood is in het array met kleurnamen 
	 * @return array met kleurnamen
	 */
	public String[] getKleuren()
	{
		String[] vaKleuren = getVaKleuren();
		String[] raKleuren = getRaKleuren();
		String[] laKleuren = getLaKleuren();
		String[] baKleuren = getVaKleuren();
		String[] result = new String[vaKleuren.length];
		for (int cCnt = 0; cCnt < vaKleuren.length; cCnt++)
		{
			result[cCnt] = vaKleuren[cCnt];
			if (raKleuren[cCnt].equals("rood"))
				result[cCnt] = "rood";
			if (laKleuren[cCnt].equals("rood"))
				result[cCnt] = "rood";
			if (baKleuren[cCnt].equals("rood"))
				result[cCnt] = "rood";

		}

		return result;
	}

	/**
	 * get de huidige kleurnamen voor de vlakken in het vooraanzicht
	 * @return array met kleurnamen
	 */
	public String[] getVaKleuren()
	{
		return va.getKleuren();
	}

	/**
	 * get de huidige kleurnamen voor de vlakken in het rechterzijaanzicht
	 * @return array met kleurnamen
	 */
	public String[] getRaKleuren()
	{
		return ra.getKleuren();
	}

	/**
	 * get de huidige kleurnamen voor de vlakken in het linkerzijaanzicht
	 * @return array met kleurnamen
	 */
	public String[] getLaKleuren()
	{
		return la.getKleuren();
	}

	/**
	 * get de huidige kleurnamen voor de vlakken in het bovenaanzicht
	 * @return array met kleurnamen
	 */
	public String[] getBaKleuren()
	{
		return ba.getKleuren();
	}

	/**
	 * kleur de vlakken in de 4 aanzichten met de gegeven kleurnamen<br>
	 * zie methode zetKleuren in Viewer3d 
	 * @param kleuren array met kleurnamen
	 */
	public void zetVaktekKleuren(String[] kleuren)
	{
		va.zetKleuren(kleuren);
		ra.zetKleuren(kleuren);
		la.zetKleuren(kleuren);
		ba.zetKleuren(kleuren);
	}

	/**
	 * in Viewer3d v3d is de kleur van een vlak van het veelvlak veranderd,
	 * verander de kleur van dit vlak ook in de andere aanzichten
	 * @param v3d viewer waarin de kleur van een vlak van het veelvlak veranderd is
	 */
	public void synchronizeViewerKleuren(Viewer3d v3d)
	{

System.out.println("synchronize");

		String[] result = null;
		if (v3d == va)
		{
			result = getVaKleuren();
		}
		else if (v3d == ra)
		{
			result = getRaKleuren();
		}
		else if (v3d == la)
		{
			result = getLaKleuren();
		}
		else if (v3d == ba)
		{
			result = getBaKleuren();
		}

		// niet nodig? 
		String[] vaKleuren = getVaKleuren();
		String[] raKleuren = getRaKleuren();
		String[] laKleuren = getLaKleuren();
		String[] baKleuren = getBaKleuren();

		for (int cCnt = 0; cCnt < result.length; cCnt++)
		{
			vaKleuren[cCnt] = result[cCnt];
			raKleuren[cCnt] = result[cCnt];
			laKleuren[cCnt] = result[cCnt];
			baKleuren[cCnt] = result[cCnt];
		}
		// einde niet nodig

		va.zetKleuren(result);
		ra.zetKleuren(result);
		la.zetKleuren(result);
		ba.zetKleuren(result);

		va.tekenOpnieuw();
		ra.tekenOpnieuw();
		la.tekenOpnieuw();
		ba.tekenOpnieuw();
	}

	/**
	 * kijk of de huidige kleuren van het veelvlak waarvan de aanzichten zichtbaar zijn
	 * overeenkomen met de kleuren in het array nakijkKleuren
	 * @param nakijkKleuren door docent voorgeschreven kleuren 
	 * @return true/false
	 */
	public boolean evalueer(String[] nakijkKleuren)
	{
		String[] viewerKleuren = getKleuren();
		boolean result = true;
		for (int i = 0; i < viewerKleuren.length; i++)
		{
			result = result && viewerKleuren[i].equals(nakijkKleuren[i]);
		}

		return result;

	}

	/**
	 * zet klikken met de muis aan of uit in de 4 aanzichtviewers<br>
	 * zie klasse Viewer3D
	 * @param b true/false
	 */
	public void zetKlikAan(boolean b)
	{
		va.zetKlikAan(b);
		ra.zetKlikAan(b);
		la.zetKlikAan(b);
		ba.zetKlikAan(b);
	}

	/**
	 * zet het veelvlak in de 4 aanzicht-viewers<br>
	 * zie klasse Viewer3D 
	 * @param vva veelvlak voor vooraanzicht
	 * @param vra veelvlak voor rechterzijaanzicht
	 * @param vla veelvlak voor linkerzijaanzicht
	 * @param vba veelvlak voor bovenaanzicht
	 */
	public void zetVeelvlak(Veelvlak vva, Veelvlak vra, Veelvlak vla, Veelvlak vba)
	{
		la.zetVeelvlak(vla);
		ba.zetVeelvlak(vba);
		va.zetVeelvlak(vva);
		ra.zetVeelvlak(vra);
	}

	/**
	 * inner class voor het afhandelen van Click Events op de kijkNaButton: <br>
	 */
	class PushClickHandler implements ClickHandler
	{
		public void onClick(ClickEvent e)
		{
			// e.preventDefault();
			e.stopPropagation();

			if (e.getSource() == kijkNaButton)
			{
				tvGWT.kijkNa();
				if (tvGWT.correct)
				{
					kijkNaPanel.setStyleName("goed", true);
					kijkNaPanel.setStyleName("fout", false);
				}

				else
				{
					kijkNaPanel.setStyleName("goed", false);
					kijkNaPanel.setStyleName("fout", true);
				}
			}
		}
	}
}
