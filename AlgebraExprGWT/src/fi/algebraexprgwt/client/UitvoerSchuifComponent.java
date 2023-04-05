package fi.algebraexprgwt.client;

import java.util.HashMap;

import fi.algebraexprgwt.client.expressies_ap.*;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import nl.uu.fi.dwo.interaction.client.Letter;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.event.dom.client.KeyDownEvent;

public class UitvoerSchuifComponent extends AlgebraSchuifComponent
{
	TekstPopup tf, label;
	LayoutPanel inputOwner;
	String tfString = "";

	private Expressie expressie;
	private BasisExpressie beginw;
	private Expressie verborgenExpressie;
	private String waardeString;
	private boolean toonWaarde;
	private boolean labelZichtbaar;

	String labelTekst = "";
	boolean labelPressed = false;

	boolean tabelZichtbaar;

	boolean zoomInTabel;

	private boolean grafiek;
	boolean muisrechts;

	TabelComponent tabel;

//	private int tabelCorr;

	String fontString = "12px sans-serif";
	int fontSize = 12;

	PopupPanel menuPopup;
	MenuBar menuBar;
	MenuItem labelItem, tabelItem, kettingItem;

	// private PlusMinKnop plusMinKnop;

	// later terugzetten als scrollknoppen lukken
	// boolean scrollable = true;
	boolean scrollable = false;

	int scrollCorr = 0;

	public boolean kettingZichtbaar = true;

	ZoomKnop zoomInKnop;
	ZoomKnop zoomUitKnop;

	boolean zoomInPressed = false;
	boolean zoomUitPressed = false;

	private double schaalFactorX = 1;
	private int factorRijNummerX = 99;
	private int beginwaarde;
	private int selectnummer;
	private double beginx;
	private String defaultVarnaam = "qq" + 1000 * Math.random();

	private CssColor vakKleur, vakKleurSoft;

	public UitvoerSchuifComponent(AlgebraSchuifVeld asv, int x, int y, int b, int h)
	{
		super(1, asv, x, y, b, h);

		// toonWaarde = !((AlgebraSchuifVeld) schuifveld).ip.isExpr();
		if (asv.owner.waardeBox != null)
			toonWaarde = asv.owner.waardeBox.getValue();
		// anders default false

		labelZichtbaar = false;
		tabelZichtbaar = false;

		zoomInTabel = true;

		waardeString = "";

		inputOwner = asv.owner.canvasPanel;

		menuBar = new MenuBar(true);
		labelItem = new MenuItem("toon label", new MenuCommand("label"));
		tabelItem = new MenuItem("toon tabel", new MenuCommand("tabel"));
		kettingItem = new MenuItem("verberg ketting", new MenuCommand("ketting"));
		menuBar.addItem(labelItem);
		menuBar.addItem(tabelItem);
		menuBar.addItem(kettingItem);

		verborgenExpressie = new BasisExpressie(defaultVarnaam);

		zoomInKnop = new ZoomKnop("zoomintabel", xPos, yPos + 80, 10, 10, asv.asvContext2d);

		zoomUitKnop = new ZoomKnop("zoomuittabel", xPos, yPos + 130, 10, 10, asv.asvContext2d);
	}

	public HashMap<String, Object> getState()
	{
		String basisExp = null;
		String defaultVarnaam = null;
		boolean tabelZichtbaar = false;
		boolean labelZichtbaar = false;
		boolean kettingZichtbaar = true;
		String labelTekst = null;

		if (beginw != null)
			basisExp = this.beginw.basisString;
		else
			basisExp = "";
		defaultVarnaam = this.defaultVarnaam;
		tabelZichtbaar = this.tabelZichtbaar;
		labelZichtbaar = this.labelZichtbaar;
		kettingZichtbaar = this.kettingZichtbaar;

		labelTekst = this.labelTekst;

		HashMap<String, Object> h = super.getState();

		h.put("basisExp", basisExp);
		h.put("defaultVarnaam", defaultVarnaam);
		h.put("tabelZichtbaar", new Boolean(tabelZichtbaar));
		h.put("labelZichtbaar", new Boolean(labelZichtbaar));
		h.put("kettingZichtbaar", new Boolean(kettingZichtbaar));
		h.put("labelTekst", labelTekst);

		h.put("scrollable", new Boolean(scrollable));
		h.put("zoomInTabel", new Boolean(zoomInTabel));

		return h;
	}

	public void setState(HashMap<String, Object> h)
	{
		String basisExp = "";
		String defaultVarnaam = null;
		boolean tabelZichtbaar = false;
		boolean labelZichtbaar = false;
		boolean kettingZichtbaar = true;
		String labelTekst = "";

		boolean scrollable = true;
		boolean zoomInTabel = true;

		if (h.containsKey("basisExp"))
			basisExp = (String) h.get("basisExp");
		if (h.containsKey("defaultVarnaam"))
			defaultVarnaam = (String) h.get("defaultVarnaam");
		if (h.containsKey("tabelZichtbaar"))
			tabelZichtbaar = ((Boolean) h.get("tabelZichtbaar")).booleanValue();
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
		{
			this.defaultVarnaam = defaultVarnaam;
			verborgenExpressie = new BasisExpressie(defaultVarnaam);
		}

		toonTabel(tabelZichtbaar, true);
		toonLabel(labelZichtbaar);
		this.labelTekst = labelTekst;

		// GWT
		// label.zetLabelTekst(labelTekst);

		if (!kettingZichtbaar)
			zetBoomZichtbaarHier(kettingZichtbaar);

		// zetScroll(scrollable);
		zetZoomInTabel(zoomInTabel);

		super.setState(h);

		zetMaat();
	}

	public void zetZoomInTabel(boolean b)
	{
		zoomInTabel = b;

		// zoomInKnop.setVisible(b);
		// zoomUitKnop.setVisible(b);

		zoomInKnop.visible = b;
		zoomUitKnop.visible = b;

	}

	public void zetScroll(boolean b)
	{
		scrollable = b;

		zetVeranderd(20);

		zetMaat();
	}

	public void zetVakKleur(CssColor color)
	{
		vakKleur = color;

		String cString = color.toString().substring(4, color.toString().length() - 1);
		String[] kleurenStr = StringUtils.split(cString, ",");

		int cBlue = Integer.parseInt(kleurenStr[2]);
		int cGreen = Integer.parseInt(kleurenStr[1]);
		int cRed = Integer.parseInt(kleurenStr[0]);

		vakKleurSoft = CssColor.make((cRed + 765) / 4, (cGreen + 765) / 4, (cBlue + 765) / 4);

		if (color.toString().equals(CssColor.make(0, 0, 0).toString()))
			vakKleur = null;
	}

	// public void paint(Graphics g)
	public void paint(Context2d g)
	{

		if (!visible)
			return;

		CssColor achtergrondkleur = CssColor.make(255, 255, 255);
		if (pijlIn1 != null)
		{
			achtergrondkleur = CssColor.make(220, 220, 220);
		}

		g.setFillStyle(CssColor.make(125, 125, 125)); // grey
		g.fillRect(xPos + 0, yPos + 10, breedte - 1, hoogte - 11);

		g.setStrokeStyle(CssColor.make(0, 0, 0));

		g.strokeRect(xPos + 0, yPos + 10, breedte - 1, hoogte - 11);

		super.paint(g);

		int labelCorr = 0;
		int tabelCorr = 0;
		if (labelZichtbaar)
			labelCorr = 20;
		if (tabelZichtbaar)
			tabelCorr = 152;

		if (vakKleur != null)
		{
			g.setFillStyle(vakKleurSoft);
		}
		else
		{
			g.setFillStyle(achtergrondkleur);
		}

		g.fillRect(xPos + 2, yPos + labelCorr + 12, breedte - 5 - scrollCorr, hoogte - labelCorr - tabelCorr - 15);
		g.setStrokeStyle(CssColor.make(0, 0, 0)); // zwart
		g.strokeRect(xPos + 2, yPos + labelCorr + 12, breedte - 5 - scrollCorr, hoogte - labelCorr - tabelCorr - 15);

		g.setFont(fontString);
		g.setFillStyle(CssColor.make(0, 0, 0));
		if (expressie != null)
		{
			expressie.zetMaat(fontSize, ascContext2d);

			TextMetrics tm = g.measureText(waardeString);
			int stringWidth = (int) Math.round(tm.getWidth());

			if (toonWaarde && expressie.geefVarNaam() == null)
			{
				g.fillText(waardeString, xPos + (breedte - scrollCorr - stringWidth) / 2,
					yPos + hoogte - tabelCorr - 5);
			}
			else
			{
				expressie.teken(g, xPos + (breedte - scrollCorr - expressie.breedte) / 2,
					yPos + 12 + (labelCorr + hoogte - tabelCorr - 15 - expressie.hoogte) / 2 + 10);
			}
		}

		if ((soort == 1) &&
			labelZichtbaar)
		{
			g.setFont(fontString);
			g.setFillStyle(CssColor.make(255, 255, 255));
			TextMetrics tm = g.measureText(labelTekst);
			int labelWidth = (int) Math.round(tm.getWidth());
			g.fillText(labelTekst, xPos + (breedte - scrollCorr - labelWidth) / 2, yPos + 25);
		}

	}

	public void zetToonWaarde(boolean b)
	{
		toonWaarde = b;
		zetMaat();
	}

	public void toonLabel(boolean b)
	{
		labelZichtbaar = b;
		if (b)
		{
			if (zoomInKnop.yPos == yPos + 50)
				zoomInKnop.translate(0, 20);
			if (zoomUitKnop.yPos == yPos + 100)
				zoomUitKnop.translate(0, 20);

			zetMaat();
		}
		else
		{
			if (zoomInKnop.yPos == yPos + 70)
				zoomInKnop.translate(0, -20);
			if (zoomUitKnop.yPos == yPos + 120)
				zoomUitKnop.translate(0, -20);
			zetMaat();
		}
		if (!asv.owner.asvSetState)
			asv.tekenOpnieuw();
	}

	public void toonTabel(boolean b, boolean verander)
	{
		tabelZichtbaar = b;
		if (b)
		{

			tabel = new TabelComponent(asv);
			tabel.setDefaultVarnaam(defaultVarnaam);
			if (verander)
				zetVeranderd(20);
			zetMaat();
		}
		else
		{
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
	{
		return labelZichtbaar;
	}

	public String geefLabelTekst()
	{
		return labelTekst;
	}

	public void zetLabelTekst()
	{
		labelTekst = label.getText();
		label.setVisible(false);
		inputOwner.remove(label);

		zetMaat();

		asv.paint();
	}

	public void setSize(int b, int h)
	{
		breedte = b;
		hoogte = h;
		super.setSize(b, h);
	}

	public void zetMaat()
	{
		int b = 40 + scrollCorr;
		int h = 30;
		int corr = 0;
		if (expressie != null)
		{

			if (expressie.geefVarNaam() == null)
			{
				// GWT
				// plusMinKnop.setVisible(true);

			}

			b = expressie.breedte + scrollCorr;
			h = expressie.hoogte;
			if (toonWaarde && expressie.geefVarNaam() == null && waardeString != null)
			{
				TextMetrics tm = ascContext2d.measureText(waardeString);
				int stringWidth = (int) Math.round(tm.getWidth());
				b = stringWidth + scrollCorr;
				h = 0;
			}
			if (b > 26)
				b = b + 14 + scrollCorr;
			else
				b = 40 + scrollCorr;
			if (h > 12)
				h = 20 + ((h + 5) / 10) * 10;
			else
				h = 30;
		}

		if (labelZichtbaar)
		{
			corr = 20;
			h = h + 20;

			TextMetrics tm = ascContext2d.measureText(labelTekst);
			int labelWidth = (int) Math.round(tm.getWidth());

			b = Math.max(b, labelWidth + 15);
		}
		if (tabelZichtbaar)
		{
			h = h + 152;

			b = Math.max(b, tabel.geefBreedte());
		}
		setSize(b, h);

		if (tabel != null)
			tabel.setBounds(xPos + 10, yPos + h - 152, b - 10, 152);
	}

	public Expressie geefUitvoer(int max)
	{
		return expressie;
	}

	public Expressie geefVerborgenUitvoer(int max)
	{
		return verborgenExpressie;
	}

	/**
	 * Geef de expressie. Dit wordt gebruikt om de 'oude' invoer op te vragen
	 * bij druk op escape-toets.
	 */
	String geefExpressieString()
	{
		String text;

		text = expressie.toString();

		return text;
	}

	public GrafiekComponent vindGrafiekComponent()
	{
		GrafiekComponent gc = null;
		for (int pCnt = 0; pCnt < pijlUit.length; pCnt++)
		{
			if ((pijlUit[pCnt] != null) && (pijlUit[pCnt].ontvanger instanceof GrafiekComponent))
				gc = (GrafiekComponent) pijlUit[pCnt].ontvanger;
		}

		return gc;
	}

	public void zetVeranderd(int max)
	{
		if (pijlIn1 != null)
		{
			scrollCorr = 0;
			expressie = pijlIn1.zender.geefUitvoer(20);
			verborgenExpressie = pijlIn1.zender.geefVerborgenUitvoer(20);

			zoomInKnop.visible = false;
			zoomUitKnop.visible = false;

			for (int pCnt = 0; pCnt < pijlUit.length; pCnt++)
			{
				if (pijlUit[pCnt] != null)
					pijlUit[pCnt].im = null;
			}

			if (asv.owner.isNakijkModus())
				asv.answerChanged();
		}
		else
		{
			for (int pCnt = 0; pCnt < pijlUit.length; pCnt++)
			{
				if (pijlUit[pCnt] != null)
					pijlUit[pCnt].im = null;
			}
			if (asv.owner.kijkNaActief)
				asv.answerChanged();

			if (scrollable && expressie != null && !Double.isNaN(expressie.geefWaarde().doubleValue()))
			{
				if (scrollCorr == 0)
				{
					// GWT
					// add(plusMinKnop);
				}
				scrollCorr = 10;
			}
			else
			{
				scrollCorr = 0;
			}
			expressie = beginw;
			verborgenExpressie = new BasisExpressie(defaultVarnaam);

			zoomInKnop.visible = true;
			zoomUitKnop.visible = true;
		}

		if (!kettingZichtbaar)
		{
			zoomInKnop.visible = true;
			zoomUitKnop.visible = true;
		}

		if (expressie != null && !Double.isNaN(expressie.geefWaarde().doubleValue()))
			toonTabel(false, false);
		else if (verborgenExpressie != null && !Double.isNaN(verborgenExpressie.geefWaarde().doubleValue()))
			toonTabel(false, false);
		else if (expressie == null && verborgenExpressie == null)
			toonTabel(false, false);
		else
			toonTabel(tabelZichtbaar, false);

		if (tabel != null)
		{
			if (expressie != null && expressie.geefVarNaam() != null)
				tabel.zetExpressie(expressie);
			else if (verborgenExpressie != null && verborgenExpressie.geefVarNaam() != null)
				tabel.zetExpressie(verborgenExpressie);
		}

		if (expressie != null)
		{
			expressie.zetMaat(fontSize, ascContext2d);
			Double waarde = expressie.geefWaarde();
			if (!Double.isNaN(waarde.doubleValue()))
			{
				waardeString = UF.format0(waarde, 3);
			}
			else
				waardeString = "-";
		}

		zetMaat();

		ZoomState zs = null;
		String naam = null;
		if (expressie != null && expressie.geefVarNaam() != null && asv != null)
		{
			zs = asv.zoomStateHolder.getZoomState(expressie.geefVarNaam());
			naam = expressie.geefVarNaam();
		}
		else if (verborgenExpressie != null && verborgenExpressie.geefVarNaam() != null && asv != null)
		{
			zs = asv.zoomStateHolder.getZoomState(verborgenExpressie.geefVarNaam());
			naam = verborgenExpressie.geefVarNaam();
		}
		if (zs != null && naam != null)
			setZoomState(naam, zs);

		super.zetVeranderd(max);

		GrafiekComponent gc = vindGrafiekComponent();
		if (gc != null)
			gc.zetVeranderd(20);

	}

	public void setZoomState(String varnaam, ZoomState zoomState)
	{
		if (expressie != null && expressie.geefVarNaam() != null && expressie.geefVarNaam().equals(varnaam)
			|| verborgenExpressie != null && verborgenExpressie.geefVarNaam() != null
				&& verborgenExpressie.geefVarNaam().equals(varnaam))
		{
			this.beginwaarde = zoomState.getBeginwaarde();
			this.selectnummer = zoomState.getSelectnummer();
			this.schaalFactorX = zoomState.getSchaalFactorX();
			this.factorRijNummerX = zoomState.getFactorRijNummerX();
			this.beginx = zoomState.getBeginx();

			if (tabel != null)
			{
				tabel.zetTabel(beginwaarde, selectnummer, varnaam, schaalFactorX, beginx);
			}
			zetMaat();
		}
	}

	/**
	 * kijk of de tekst in de invoerpopup valide invoer is
	 */
	public void zetInvulWaarde()
	{
		try
		{
			zetInvulWaarde0();
		}
		catch (RestartException r)
		{
			r.restart(new Runnable()
			{
				public void run()
				{
					try
					{
						zetInvulWaarde0();
					}
					catch (RestartException e)
					{
						e.restart(this);
					}
				}
			});
		}
	}
	
	public void zetInvulWaarde0() throws RestartException
	{
		ZoomState zs = null;
		if (expressie != null && expressie.geefVarNaam() != null && asv != null)
		{
			zs = asv.zoomStateHolder.getZoomState(expressie.geefVarNaam());
		}
		else if (verborgenExpressie != null && verborgenExpressie.geefVarNaam() != null && asv != null)
		{
			zs = asv.zoomStateHolder.getZoomState(verborgenExpressie.geefVarNaam());
		}

		// check invoer
		boolean isGeldigeInvoer = true;
		{
			try
			{
				String s = tf.getText();
				s = s.replace(',', '.');

				// formules uit formuleeditor zoals 3$m2@ verwerken
				fi.wiskopdr.expressies.Expressie exp = FormuleParser.geefExpressie(addFormulaCodes(tf.getText()));

				if (s.equals(""))
				{
					tfString = "";
					isGeldigeInvoer = false;
				}
				else if (exp == null)
				{
					isGeldigeInvoer = false;
				}
				else
				{ 
					boolean casNodig = false;
					if (exp!=null) 
						casNodig = exp.toString().indexOf("$i")>-1 
							|| exp.toString().indexOf("$d")>-1 
							|| exp.toString().indexOf("$T")>-1  
							|| exp.toString().indexOf("$S")>-1  
							|| exp.toString().indexOf("$P")>-1;
					//logger.fine(antwoord + " needs " + casNodig);
					if (casNodig)
						exp = fi.wiskopdr.expressies.Expressie.evalWithCAS(exp); // deze kan een restartexception geven
					Double d = null;
					if (exp != null && exp.isWaarde())
					{
						d = exp.geefWaarde();
						// afronden op 3 decimalen; format voor grote getallen met wetenschappelijke notatie zoals 1234567^2 = 1524155677489...
						String formatted = NumberFormat.getFormat("0.###").format(d);
						formatted = formatted.replace(',', '.'); // dit moet, anders gaat BasisExpressie.geefWaarde() met Double.valueOf(basisString) mis
						tf.setText(formatted);
					}
					else
					{
						// check of variabele letters/variabelenaam bevat
						for (int i = 0; i < tf.getText().length(); i++)
						{
							if (!Letter.isLetter(tf.getText().charAt(i)))
							{
								isGeldigeInvoer = false;
								break;
							}
						}
						if (!isGeldigeInvoer)
						{
							tf.setText("");
							tfString = "";
						}
					}
				}
			}
			catch (NumberFormatException ex)
			{
				for (int i = 0; i < tf.getText().length(); i++)
				{
					if (!Character.isLetter(tf.getText().charAt(i)))
					{
						isGeldigeInvoer = false;
						break;
					}
				}
				if (tf.getText().equals(""))
					isGeldigeInvoer = false;
				if (!isGeldigeInvoer)
				{
					tf.setText("");
				}
			}
		}
		if (isGeldigeInvoer)
		{

			tfString = tf.getText();
			beginw = new BasisExpressie(tf.getText());
			beginw.zetMaat(fontSize, ascContext2d);
		}
		else
		{
			beginw = null;
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

	/**
	 * Surround the given string with the formule codes "$f" and "@".
	 * Used for fomula editor.
	 * 
	 * @param string
	 * @return
	 */
	private String addFormulaCodes(String string)
	{
		String startCode = "$f";
		String endCode = "@";
		String s = startCode + string + endCode;
		return s;
	}

	public void zoomUitTabelAction()
	{
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;

		if (factorRijNummerX > 120)
			return;
		if (factorRijNummerX % 3 == 1)
		{
			schaalFactorX *= 2.5;
			beginx = beginx / 2.5;
		}
		else
		{
			schaalFactorX *= 2;
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

		if (factorRijNummerX < 87)
			return;
		if (factorRijNummerX % 3 == 2)
		{
			schaalFactorX /= 2.5;
			beginx = beginx * 2.5;
		}
		else
		{
			schaalFactorX /= 2;
			beginx = beginx * 2;
		}
		beginx = Math.round(beginx / 14) * 14;
		beginwaarde = -(int) Math.round(beginx / 14);
		selectnummer = 999;

		factorRijNummerX--;
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
		asv.tekenOpnieuw();
	}

	public void showLabelPopup()
	{
		int popupX = xPos + inputOwner.getAbsoluteLeft();

		int popupY = yPos - 40 + inputOwner.getAbsoluteTop();

		if ((label != null) && label.isVisible())
		{
			zetLabelTekst();
		}

		label = new TekstPopup(this, true);
		label.setText(labelTekst);
		label.resizePopup();
		label.setPopupPosition(popupX, popupY);
		label.show();
		label.setFocus(true);
		label.setSelected();
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
		
		if (!"".equals(tfString))
			tf.setText(tfString);
		else if (expressie != null) 
		{
			// format voor grote getallen met wetenschappelijke notatie zoals 1234567^2 = 1524155677489...
			String formatted = NumberFormat.getFormat("0.###").format(expressie.geefWaarde());
			formatted = formatted.replace(',', '.'); // dit moet, anders gaat BasisExpressie.geefWaarde() met Double.valueOf(basisString) mis
			tf.setText(formatted);
		}
		else
		{
			tf.setText("");
		}

		tf.resizePopup();

		tf.setPopupPosition(popupX, popupY);
		tf.show();
		tf.setFocus(true);
		tf.setSelected();
	}

	public void showPopupMenu()
	{
		int popupX = xPos + breedte + inputOwner.getAbsoluteLeft();
		int popupY = yPos + inputOwner.getAbsoluteTop();
		menuPopup = new PopupPanel(true);

		if (labelZichtbaar)
		{
			labelItem.setText("verberg label");
			labelItem.setText(AlgebraExprGWT.rb.verbergLabel());
		}
		else
		{
			labelItem.setText("toon label");
			labelItem.setText(AlgebraExprGWT.rb.toonLabel());
		}
		if (tabelZichtbaar)
		{
			tabelItem.setText("verberg tabel");
			tabelItem.setText(AlgebraExprGWT.rb.verbergTabel());
		}
		else
		{
			tabelItem.setText("toon tabel");
			tabelItem.setText(AlgebraExprGWT.rb.toonTabel());
		}
		if (kettingZichtbaar)
		{
			kettingItem.setText(AlgebraExprGWT.rb.verbergKetting());
		}
		else
		{
			kettingItem.setText(AlgebraExprGWT.rb.toonKetting());
		}

		menuPopup.setWidget(menuBar);
		menuPopup.setPopupPosition(popupX, popupY);
		menuPopup.show();
	}

	public void zetBoomZichtbaarHier(boolean b)
	{
		if (pijlIn1 != null)
			pijlIn1.zender.zetBoomZichtbaar(b);
		if (pijlIn2 != null)
			pijlIn2.zender.zetBoomZichtbaar(b);

		for (int i = 0; i < aantalPu; i++)
		{
			pijlUit[i].setVisible(b);
		}

		open = b;
		kettingZichtbaar = b;

		asv.tekenOpnieuw();
	}

	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;

		muisrechts = false;

		press = true;

		// erboven staat een balletje
		if (labelZichtbaar && new Rectangle(xPos, yPos + 10, breedte, 20).contains(eventX, eventY))
			labelPressed = true;

		if (zoomInTabel
			&& new Rectangle(zoomInKnop.xPos, zoomInKnop.yPos - 10, zoomInKnop.breedte + 4, zoomInKnop.hoogte + 15)
				.contains(eventX, eventY))
			zoomInPressed = true;

		if (zoomInTabel
			&& new Rectangle(zoomUitKnop.xPos, zoomUitKnop.yPos - 10, zoomUitKnop.breedte + 4, zoomUitKnop.hoogte + 15)
				.contains(eventX, eventY))
			zoomUitPressed = true;

		taptime = System.currentTimeMillis();
		doubletap.add(taptime);
		super.mouseDownTouchStartAction(eventX, eventY);
	}

	public void mouseUpTouchEndAction()
	{
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;

		if (isDoubleClick() && !zoomInPressed && !zoomUitPressed)
		{
			if (labelPressed && !isStapel && !asv.alleenInvullen)
			{
				showLabelPopup();
			}

			else if (!isStapel && (pijlIn1 == null) && (pijlIn2 == null))
			{
				if (tabel == null)
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
			if (!dragging && !asv.alleenInvullen)
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
			{
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
		{
			if (labelItem.getText().equals("toon label") || labelItem.getText().equals("show label")
				|| labelItem.getText().equals("montrer label") || labelItem.getText().equals("zeige Label"))
			{
				labelItem.setText(AlgebraExprGWT.rb.verbergLabel());
				toonLabel(true);
			}
			else if (labelItem.getText().equals("verberg label") || labelItem.getText().equals("hide label")
				|| labelItem.getText().equals("cacher label") || labelItem.getText().equals("verberge Label"))
			{
				labelItem.setText(AlgebraExprGWT.rb.toonLabel());
				toonLabel(false);
			}
		}
		else if (s.equals("tabel"))
		{

			if (tabelItem.getText().equals("toon tabel") || tabelItem.getText().equals("show table")
				|| tabelItem.getText().equals("montrer table") || tabelItem.getText().equals("zeige Tabelle"))
			{
				tabelItem.setText(AlgebraExprGWT.rb.verbergTabel());
				toonTabel(true, true);
			}
			else if (tabelItem.getText().equals("verberg tabel") || tabelItem.getText().equals("hide table")
				|| tabelItem.getText().equals("cacher table") || tabelItem.getText().equals("verberge Tabelle"))
			{
				tabelItem.setText(AlgebraExprGWT.rb.toonTabel());
				toonTabel(false, false);
			}
		}
		else if (s.equals("ketting"))
		{
			if (kettingItem.getText().equals("toon ketting") || kettingItem.getText().equals("show chain")
				|| kettingItem.getText().equals("montrer flèche") || kettingItem.getText().equals("zeige Baum"))
			{
				labelItem.setText(AlgebraExprGWT.rb.verbergKetting());
				zetBoomZichtbaarHier(true);
			}
			else if (kettingItem.getText().equals("verberg ketting") || kettingItem.getText().equals("hide chain")
				|| kettingItem.getText().equals("cacher flèche") || kettingItem.getText().equals("verberge Baum"))
			{
				labelItem.setText(AlgebraExprGWT.rb.toonKetting());
				zetBoomZichtbaarHier(false);
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

	class TextBoxKeyDownHandler implements KeyDownHandler
	{
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				zetInvulWaarde();
			}
		}
	}

}
