package fi.algebrapijlengwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * de klasse die een Pijl in een pijlenketting representeert; elke Pijl heeft
 * een zender (een AlgebraSchuifComponent die de Pijl als uitgaande Pijl heeft)
 * en een ontvanger (een AlgebraSchuifComponent waarvan de ingang door de
 * gebruiker met de punt van de Pijl verbonden wordt); begin en einde van een
 * Pijl vormen een driehoekige punt; als de Pijl geen ontvanger heeft, dan
 * vallen begin en einde samen; het einde van de Pijl kan versleept worden om de
 * Pijl vast/los te maken aan/van een AlgebraSchuifComponent; bij verslepen van
 * zender of ontvanger, wordt de Pijl meegesleept.
 */
public class Pijl
{
	private static final CssColor RED = CssColor.make(255, 0, 0);
	private static final CssColor GREEN = CssColor.make(41, 156, 57);
	/**
	 * een kleine double
	 */
	double nZero = 1e-3d;
	/**
	 * x-coordinaat van de botte kant van het begin van de Pijl
	 */
	int x0;
	/**
	 * y-coordinaat van de spitse kant van het begin van de Pijl
	 */
	int y0;
	/**
	 * x-coordinaat van de spitse kant van het eind van de Pijl
	 */
	int x1;
	/**
	 * y-coordinaat van de spitse kant van het einde van de Pijl
	 */
	int y1;
	/**
	 * referentie naar het werkveld
	 */
	AlgebraSchuifVeld asv;
	/**
	 * de ASC waarvan de Pijl een uitgaande Pijl is
	 */
	AlgebraSchuifComponent zender;
	/**
	 * de ASC (if any) waarvan de Pijl de inkomende Pijl is
	 */
	AlgebraSchuifComponent ontvanger;
	/**
	 * het begin van de Pijl, t.b.v. paint
	 */
	Polygon pijlpuntBegin;
	/**
	 * het einde van de Pijl, t.b.v. paint
	 */
	Polygon pijlpuntEind;
	/**
	 * een groter Polygon dan pijlpuntEind, t.b.v. muisactie
	 */
	Polygon pijlpuntKlik;
	/**
	 * t.b.v. slepen van de Pijl
	 */
	private int laatstex = 0;
	/**
	 * t.b.v. slepen van de Pijl
	 */
	private int laatstey = 0;
	/**
	 * wordt de Pijl gesleept?
	 */
	boolean actief;
	/**
	 * zit de Pijl vast aan en ontvanger?
	 */
	boolean vast;
	/**
	 * is de Pijl de uitgaan de Pijl van een ASC op een stapel?
	 */
	boolean isStapel;
	/**
	 * wijst de Pijl naar links?
	 */
	private boolean links = false;
	/**
	 * zwart
	 */
	CssColor black = CssColor.make(0, 0, 0);
	/**
	 * grijs
	 */
	CssColor gray = CssColor.make(128, 128, 128);
	/**
	 * rood
	 */
	CssColor red = RED;
	/**
	 * de kleur van het eindpunt van de Pijl als de Pijl vast zit; color is
	 * altijd zwart behalve wanneer de Pijl aan een GrafiekComponent vastzit;
	 * als de Pijl niet vastzit, dan worden begin == einde grijs getekend
	 */
	private CssColor color = CssColor.make(0, 0, 0);
	/**
	 * nakijken: im is null, een (groene) V of een (rood) X
	 */
	String im = null;
	/**
	 * t.b.v. paint
	 */
	Context2d pijlContext2d;
	/**
	 * t.b.v. muisactie
	 */
	boolean mouseDown = false;
	/**
	 * is deze Pijl zichtbaar?
	 */
	boolean visible = true;

	/**
	 * constructor
	 * 
	 * @param asv
	 *            het werkveld
	 */
	public Pijl(AlgebraSchuifVeld asv)
	{
		this.asv = asv;
		actief = false;
		vast = false;
		isStapel = false;
		pijlContext2d = asv.asvContext2d;
		pijlpuntEind = new Polygon();
		pijlpuntKlik = new Polygon();
	}

	/**
	 * laat de Pijl naar links wijzen
	 * 
	 * @param b
	 *            true/false
	 */
	public void zetLinks(boolean b)
	{
		links = b;
	}

	/**
	 * zet de kleur van het eindpunt van de Pijl (indien vast) en de vakkleur
	 * van de zender; alleen gebruikt als het eindpunt van de Pijl een de
	 * GrafiekComponent vastzit
	 * 
	 * @param color
	 *            de gewenste kleur
	 */
	public void setColor(CssColor color)
	{
		this.color = color;
		zender.zetVakKleur(color);
	}

	/**
	 * getter voor de kleur van het eindpunt van de Pijl (indien vast)
	 * 
	 * @return de einpuntkleur
	 */
	public CssColor getColor()
	{
		return color;
	}

	/**
	 * zet de Pijl zichtbaar; repaint het werkveld indien geen setState
	 * 
	 * @param b
	 *            true/false
	 */
	public void setVisible(boolean b)
	{
		visible = b;
		if (!asv.owner.asvSetState)
			asv.tekenOpnieuw();
	}

	public void paint()
	{
		paint(pijlContext2d);
	}

	/**
	 * teken de Pijl; de Pijl bestaat naast het begin- en het eindPolygon uit
	 * twee bogen of een rechte lijn
	 * 
	 * @param gIm
	 *            de Context2d
	 */
	public void paint(Context2d gIm)
	{
		if (!visible)
			return;
		
		// pijl naar rechts, alleen deze kan je aan de grafiek vastmaken
		if (!links)
		{
			gIm.setStrokeStyle(color);
			double dx = x1 - x0;
			double dy = y1 - y0;
			int teken = (int) ((dy / Math.abs(dy)));
			double s = Math.sqrt(dx * dx + dy * dy);
			double a;
			int r0, r1;
			double dr;
			int xc0, xc1, yc0, yc1, booghoek;
			if (Math.abs((double) dy / (double) dx) > 0.04 && dx >= 0)
			{
				a = Math.atan((double) dx / Math.abs(dy));
				r0 = (int) (s / (4 * Math.cos(a)));
				dr = s / (4 * Math.cos(a)) - r0;
				if (dr > 0.25)
					r1 = r0 + 1;
				else
					r1 = r0;
				xc0 = x0;
				yc0 = y0 + r0 * teken;
				xc1 = x1;
				yc1 = y1 - r1 * teken;
				booghoek = (int) ((2 * a - Math.PI) * 180 / Math.PI);
				// bogen niet tekenen als de Pijl aan een GrafiekComponent
				// vastzit (dan is hij gekleurd)
				if (color.toString().equals(black.toString()))
				{
					// Java angles: degrees and anticlockwise from positive
					// x-axis
					// GWT angles: radians and clockwise from positive x-axis
					// 90 Java = 270 GWT = 3pi/2, 270 Java = 90 GWT = pi/2
					double startAngle = teken * 3 * Math.PI / 2;
					double deltaAngle = -teken * (booghoek - 1) * Math.PI / 180;
					double endAngle = startAngle + deltaAngle;
					boolean antiClockWise = false;
					if (endAngle < startAngle)
					{
						antiClockWise = true;
					}
					gIm.beginPath();
					gIm.arc(xc0, yc0, r0, startAngle, endAngle, antiClockWise);
					gIm.stroke();
					startAngle = teken * Math.PI / 2;
					endAngle = startAngle + deltaAngle;
					antiClockWise = false;
					if (endAngle < startAngle)
					{
						antiClockWise = true;
					}
					gIm.beginPath();
					gIm.arc(xc1, yc1, r1, startAngle, endAngle, antiClockWise);
					gIm.stroke();
				}
			}
			else if (Math.abs(dy) > 1 && dx < 0)
			{
				r0 = (int) Math.abs(dy / 4);
				dr = Math.abs(dy / 4) - r0;
				if (dr > 0.25)
					r1 = r0 + 1;
				else
					r1 = r0;
				xc0 = x0;
				yc0 = y0 + r0 * teken;
				xc1 = x1;
				yc1 = y1 - r1 * teken;
				booghoek = -180;
				
				// bogen niet tekenen als de Pijl aan een GrafiekComponent
				// vastzit (dan is hij gekleurd)
				if (color.toString().equals(black.toString()))
				{
					double startAngle = teken * 3 * Math.PI / 2;
					double deltaAngle = -teken * (booghoek - 1) * Math.PI / 180;
					double endAngle = startAngle + deltaAngle;
					boolean antiClockWise = false;
					if (endAngle < startAngle)
					{
						antiClockWise = true;
					}
					gIm.beginPath();
					gIm.arc(xc0, yc0, r0, startAngle, endAngle, antiClockWise);
					gIm.stroke();
					startAngle = teken * Math.PI / 2;
					endAngle = startAngle + deltaAngle;
					antiClockWise = false;
					if (endAngle < startAngle)
					{
						antiClockWise = true;
					}
					gIm.beginPath();
					gIm.arc(xc1, yc1, r1, startAngle, endAngle, antiClockWise);
					gIm.stroke();
					gIm.beginPath();
					gIm.moveTo(x0, yc0 + teken * r0);
					gIm.lineTo(x1, yc1 - teken * r1);
					gIm.stroke();
				}
			}
			else // rechte lijn
			{
				// lijn niet tekenen als de Pijl aan een GrafiekComponent vastzit
				// (dan is hij gekleurd)
				if (color.toString().equals(black.toString()))
				{
					gIm.beginPath();
					gIm.moveTo(x0, y0);
					gIm.lineTo(x1, y1);
					gIm.stroke();
				}
			}
			pijlpuntBegin = new Polygon();
			pijlpuntBegin.addPoint(x0, y0);
			pijlpuntBegin.addPoint(x0 - 10, y0 - 7);
			pijlpuntBegin.addPoint(x0 - 10, y0 + 7);
			pijlpuntEind = new Polygon();
			pijlpuntEind.addPoint(x1 + 10, y1);
			pijlpuntEind.addPoint(x1, y1 - 7);
			pijlpuntEind.addPoint(x1, y1 + 7);
			pijlpuntKlik = new Polygon();
			pijlpuntKlik.addPoint(x1 + 20, y1);
			pijlpuntKlik.addPoint(x1 - 2, y1 - 15);
			pijlpuntKlik.addPoint(x1 - 2, y1 + 15);

			if (vast)
			{
				gIm.setStrokeStyle(color);
				gIm.setFillStyle(color);
			}
			else
			{
				gIm.setStrokeStyle(gray);
				gIm.setFillStyle(gray);
			}
			
			// vul pijlpuntBegin
			gIm.moveTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntBegin.aantalPunten; k++)
			{
				gIm.lineTo(pijlpuntBegin.doubleX[k], pijlpuntBegin.doubleY[k]);
			}
			gIm.lineTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.closePath();
			gIm.fill();
			
			// vul pijlpuntEind;
			gIm.moveTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntEind.aantalPunten; k++)
			{
				gIm.lineTo(pijlpuntEind.doubleX[k], pijlpuntEind.doubleY[k]);
			}
			gIm.lineTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.closePath();
			gIm.fill();

			gIm.setStrokeStyle(black);
			// outline pijlpuntBegin zwart
			gIm.moveTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntBegin.aantalPunten; k++)
			{
				gIm.lineTo(pijlpuntBegin.doubleX[k], pijlpuntBegin.doubleY[k]);
			}
			gIm.lineTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.stroke();
			// outline pijlpuntEind zwart
			gIm.moveTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntEind.aantalPunten; k++)
			{
				gIm.lineTo(pijlpuntEind.doubleX[k], pijlpuntEind.doubleY[k]);
			}
			gIm.lineTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.stroke();

			// kijk of deze Pijl de (onverbonden) uitgaande Pijl van een
			// UitvoerSchuifComponent if (deze USC is dan het einde van de
			// ketting)
			boolean isEinde = false;
			if ((zender != null) && (zender instanceof UitvoerSchuifComponent))
				isEinde = asv.isEindUVS((UitvoerSchuifComponent) zender);
			
			if (isEinde && (im != null))
			{
				if (im.equals("V"))
				{
					gIm.drawImage(asv.getOwner().getVinkjeGroen(), x0 + 5, y0 - 10);
				}
				else if (im.equals("X"))
				{
					gIm.drawImage(asv.getOwner().getKruisRood(), x0 + 5, y0 - 10);
				}
			}
		}
		else // pijl naar links (kan niet aan een GrafiekComponent)
		{
			gIm.setStrokeStyle(black);

			double dx = x0 - x1;
			double dy = y0 - y1;
			int teken = (int) ((dy / Math.abs(dy)));
			double s = Math.sqrt(dx * dx + dy * dy);
			double a;
			int r0, r1;
			double dr;
			int xc0, xc1, yc0, yc1, booghoek;
			if (Math.abs((double) dy / (double) dx) > 0.04 && dx >= 0)
			{
				a = Math.atan((double) dx / Math.abs(dy));
				r0 = (int) (s / (4 * Math.cos(a)));
				dr = s / (4 * Math.cos(a)) - r0;
				if (dr > 0.25)
					r1 = r0 + 1;
				else
					r1 = r0;
				xc0 = x1;
				yc0 = y1 + r0 * teken;
				xc1 = x0;
				yc1 = y0 - r1 * teken;
				booghoek = (int) ((2 * a - Math.PI) * 180 / Math.PI);
				double startAngle = teken * 3 * Math.PI / 2;
				double deltaAngle = -teken * (booghoek - 1) * Math.PI / 180;
				double endAngle = startAngle + deltaAngle;
				boolean antiClockWise = false;
				if (endAngle < startAngle)
				{
					antiClockWise = true;
				}
				gIm.beginPath();
				gIm.arc(xc0, yc0, r0, startAngle, endAngle, antiClockWise);
				gIm.stroke();
				startAngle = teken * Math.PI / 2;
				endAngle = startAngle + deltaAngle;
				antiClockWise = false;
				if (endAngle < startAngle)
				{
					antiClockWise = true;
				}
				gIm.beginPath();
				gIm.arc(xc1, yc1, r1, startAngle, endAngle, antiClockWise);
				gIm.stroke();
			}
			else if (Math.abs(dy) > 1 && dx < 0)
			{
				r0 = (int) Math.abs(dy / 4);
				dr = Math.abs(dy / 4) - r0;
				if (dr > 0.25)
					r1 = r0 + 1;
				else
					r1 = r0;
				xc0 = x1;
				yc0 = y1 + r0 * teken;
				xc1 = x0;
				yc1 = y0 - r1 * teken;
				booghoek = -180;
				double startAngle = teken * 3 * Math.PI / 2;
				double deltaAngle = -teken * (booghoek - 1) * Math.PI / 180;
				double endAngle = startAngle + deltaAngle;
				boolean antiClockWise = false;
				if (endAngle < startAngle)
				{
					antiClockWise = true;
				}
				gIm.beginPath();
				gIm.arc(xc0, yc0, r0, startAngle, endAngle, antiClockWise);
				gIm.stroke();
				startAngle = teken * Math.PI / 2;
				endAngle = startAngle + deltaAngle;
				antiClockWise = false;
				if (endAngle < startAngle)
				{
					antiClockWise = true;
				}
				gIm.beginPath();
				gIm.arc(xc1, yc1, r1, startAngle, endAngle, antiClockWise);
				gIm.stroke();
				gIm.beginPath();
				gIm.moveTo(x1, yc0 + teken * r0);
				gIm.lineTo(x0, yc1 - teken * r1);
				gIm.stroke();
			}
			else // rechte lijn
			{
				gIm.beginPath();
				gIm.moveTo(x0, y0);
				gIm.lineTo(x1, y1);
				gIm.stroke();

			}

			pijlpuntBegin = new Polygon();
			pijlpuntBegin.addPoint(x0, y0);
			pijlpuntBegin.addPoint(x0 + 10, y0 - 7);
			pijlpuntBegin.addPoint(x0 + 10, y0 + 7);
			pijlpuntEind = new Polygon();
			pijlpuntEind.addPoint(x1 - 10, y1);
			pijlpuntEind.addPoint(x1, y1 - 7);
			pijlpuntEind.addPoint(x1, y1 + 7);
			pijlpuntKlik = new Polygon();
			pijlpuntKlik.addPoint(x1 - 20, y1);
			pijlpuntKlik.addPoint(x1 + 2, y1 - 15);
			pijlpuntKlik.addPoint(x1 + 2, y1 + 15);

			if (vast)
			{
				gIm.setStrokeStyle(black);
				gIm.setFillStyle(black);
			}
			else
			{
				gIm.setStrokeStyle(gray);
				gIm.setFillStyle(gray);
			}
			
			// vul pijlpuntBegin;
			gIm.moveTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntBegin.aantalPunten; k++)
			{
				gIm.lineTo(pijlpuntBegin.doubleX[k], pijlpuntBegin.doubleY[k]);
			}
			gIm.lineTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.closePath();
			gIm.fill();
			
			// vul pijlpuntEind;
			gIm.moveTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntEind.aantalPunten; k++)
			{
				gIm.lineTo(pijlpuntEind.doubleX[k], pijlpuntEind.doubleY[k]);
			}
			gIm.lineTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.closePath();
			gIm.fill();

			gIm.setStrokeStyle(black);
			
			// outline pijlpuntBegin;
			gIm.moveTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntBegin.aantalPunten; k++)
			{
				gIm.lineTo(pijlpuntBegin.doubleX[k], pijlpuntBegin.doubleY[k]);
			}
			gIm.lineTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.stroke();
			
			// outline pijlpuntEind;
			gIm.moveTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntEind.aantalPunten; k++)
			{
				gIm.lineTo(pijlpuntEind.doubleX[k], pijlpuntEind.doubleY[k]);
			}
			gIm.lineTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.stroke();
			
			// nakijken
			if (!isStapel && !vast && !actief && (im != null))
			{
				if (im.equals("V"))
				{
					gIm.drawImage(asv.getOwner().getVinkjeGroen(), x0 - 25, y0 - 10);
				}
				else if (im.equals("X"))
				{
					gIm.drawImage(asv.getOwner().getKruisRood(), x0 - 25, y0 - 10);
				}
			}
		}
	}

	/**
	 * kijk of Polygon pijlpuntKlik het punt (x,y) bevat
	 * 
	 * @param x
	 *            x-coordinaat
	 * @param y
	 *            y-coordinaat
	 * @return true/false
	 */
	public boolean contains(int x, int y)
	{
		return pijlpuntKlik.contains(x, y);
	}

	/**
	 * maak van deze Pijl een stapelPijl (i.e. de uitgaande Pijl van een ASC die
	 * stapel is)
	 * 
	 * @param b
	 *            true/false
	 */
	public void zetStapel(boolean b)
	{
		isStapel = b;
	}

	/**
	 * zet de zender van deze Pijl op ASC r
	 * 
	 * @param r
	 *            de zender ASC
	 */
	public void zetZender(AlgebraSchuifComponent r)
	{
		zender = r;
	}

	/**
	 * zet de coordinaten van het beginpunt van de Pijl op (x,y), doe hetzelfde
	 * met de coordinaten van het eindpunt als deze Pijl geen ontvanger heeft
	 * 
	 * @param x
	 *            nieuwe x0
	 * @param y
	 *            nieuwe y0
	 */
	public void zetPlaats(int x, int y)
	{
		x0 = x;
		y0 = y;
		if (ontvanger == null)
		{
			if (!links)
				x1 = x - 10;
			else
				x1 = x + 10;
			y1 = y;
		}
	}

	/**
	 * zet de coordinaten van het eindpunt van de Pijl op (x,y)
	 * 
	 * @param x
	 *            nieuwe x1
	 * @param y
	 *            nieuwe y1
	 */
	public void zetEind(int x, int y)
	{
		x1 = x;
		y1 = y;
	}

	/**
	 * zet de coordinaten van het beginpunt van de Pijl op (x,y)
	 * 
	 * @param x
	 *            nieuwe x0
	 * @param y
	 *            nieuwe y0
	 */
	public void zetBegin(int x, int y)
	{
		x0 = x;
		y0 = y;
	}

	/**
	 * verplaats het beginpunt van de Pijl over (dx,dy) doe hetzelfde met de
	 * coordinaten van het eindpunt als deze Pijl niet vast zit an een ontvanger
	 * 
	 * @param dx
	 *            x-verplaatsing
	 * @param dy
	 *            y-verplaatsing
	 */
	public void verplaatsBegin(int dx, int dy)
	{
		x0 = x0 + dx;
		y0 = y0 + dy;
		if (!vast)
		{
			x1 = x1 + dx;
			y1 = y1 + dy;
		}
		paint();
	}

	/**
	 * verplaats het eindpunt van de Pijl over (dx,dy)
	 * 
	 * @param dx
	 *            x-verplaatsing
	 * @param dy
	 *            y-verplaatsing
	 */
	public void verplaatsEind(int dx, int dy)
	{
		x1 = x1 + dx;
		y1 = y1 + dy;
		paint();
	}

	/**
	 * plaats het beginpunt van de Pijl op het dichtstbijzijnde punt van een
	 * 10X10 grid
	 */
	public void plaatsOpGridBegin()
	{
		int x;
		int y;
		x = x0 + 300;
		y = y0 + 300;
		int ex = x % 10;
		int ey = y % 10;
		if (ex < 5)
			verplaatsBegin(-ex, 0);
		else
			verplaatsBegin(10 - ex, 0);
		if (ey < 5)
			verplaatsBegin(0, -ey);
		else
			verplaatsBegin(0, 10 - ey);
	}

	/**
	 * plaats het eindpunt van de Pijl op het dichtstbijzijnde punt van een
	 * 10x10 grid
	 */
	public void plaatsOpGridEind()
	{
		int x;
		int y;
		x = x1 + 300;
		y = y1 + 300;
		int ex = x % 10;
		int ey = y % 10;
		if (ex < 5)
			verplaatsEind(-ex, 0);
		else
			verplaatsEind(10 - ex, 0);
		if (ey < 5)
			verplaatsEind(0, -ey);
		else
			verplaatsEind(0, 10 - ey);
	}

	/**
	 * verplaats het beginpunt van de Pijl over (dx,dy) wanneer de pijl niet
	 * actief is (d.w.z. het eindpunt wordt niet gesleept), verplaats het
	 * eindpunt van de Pijl over (dx,dy) wanneer de pijl actief is (d.w.z. het
	 * eindpunt wordt gesleept),
	 * 
	 * @param dx
	 *            x-verplaatsing
	 * @param dy
	 *            y-verplaatsing
	 */
	public void verplaats(int dx, int dy)
	{
		if (!actief)
		{
			x0 = x0 + dx;
			y0 = y0 + dy;
		}
		else
		{
			x1 = x1 + dx;
			y1 = y1 + dy;
		}
		paint();
	}

	/**
	 * maak de Pijl los van de ontvanger en zet het einpunt van de Pijl bovenop
	 * het beginpunt van de Pijl
	 */
	public void pijlTerug()
	{
		vast = false;
		// verwijder goed-V, fout-X bij de ontvanger
		if (ontvanger != null && ontvanger.pijlUit != null && ontvanger.pijlUit[0] != null)
		{
			ontvanger.pijlUit[0].im = null;
		}
		ontvanger = null;
		if (!links)
			x1 = x0 - 10;
		else
			x1 = x0 + 10;
		y1 = y0;
		asv.tekenOpnieuw();
	}

	/**
	 * maak de Pijl vast aan ontvanger ASC; voeg een nieuwe onverbonden
	 * uitgaande Pijl toe aan de zender van deze Pijl
	 * 
	 * @param asc
	 *            nieuwe ontvanger
	 */
	public void zetVerbonden(AlgebraSchuifComponent asc)
	{
		vast = true;
		ontvanger = asc;
		Pijl p = new Pijl(asv);
		p.zetLinks(links);
		zender.voegPijlToe(p);
		if (!asv.owner.asvSetState)
			asv.tekenOpnieuw();
	}

	/**
	 * mouseDown/touchStart op het eindpaunt van de Pijl: maak de Pijl los en
	 * actief (= sleepbaar)
	 * 
	 * @param eventX
	 *            x-coordinaat MouseDown/TouchStart Event
	 * @param eventY
	 *            y-coordinaat MouseDown/TouchStart Event
	 */
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		if (asv.alleenInvullen)
			return;
		if (asv.isDemo)
			return;
		mouseDown = true;
		vast = false;
		actief = true;
		if (ontvanger != null)
		{
			ontvanger.maakLos(this);
			ontvanger.zetVeranderd(20);
		}
		zender.verwijderPijl();
		laatstex = eventX;
		laatstey = eventY;
		asv.tekenOpnieuw();
	}

	/**
	 * mouseMove/touchMove op het eindpaunt van de Pijl: sleep het eindpunt van
	 * de Pijl (behalve wanneer de Pijl behoort bij een ASC op een stapel)
	 * 
	 * @param eventX
	 *            x-coordinaat MouseMove/TouchMove Event
	 * @param eventY
	 *            y-coordinaat MouseMove/TouchMove Event
	 */
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{
		if (asv.alleenInvullen)
			return;
		if (asv.isDemo)
			return;
		if (!mouseDown)
			return;
		if (isStapel)
			return;
		if (actief)
		{
			int dx = eventX - laatstex;
			int dy = eventY - laatstey;
			x1 = x1 + dx;
			y1 = y1 + dy;
			paint();
			laatstex = eventX;
			laatstey = eventY;
		}
		asv.tekenOpnieuw();
	}

	/**
	 * mouseUp/touchEnd op het eindpunt van de Pijl: kijk of het eindpunt
	 * aangemeld kan worden bij een andere ASC op het werkveld; zo ja, maak de
	 * Pijl vast aan deze nieuwe ontvanger en voeg een nieuwe onverbonden
	 * uitgaande Pijl toe aan de zender van deze Pijl; zo nee, zet het eindpunt
	 * van de Pijl weer bovenop het beginpunt
	 */
	public void mouseUpTouchEndAction()
	{
		if (asv.alleenInvullen)
			return;
		if (asv.isDemo)
			return;
		mouseDown = false;
		asv.changed = true;
		plaatsOpGridEind();
		for (int i = 0; i < asv.aantalSc; i++)
		{
			boolean b = false;
			if (asv.schuifcomponenten[i].visible && !asv.schuifcomponenten[i].isStapel && !zender.isStapel
				&& asv.schuifcomponenten[i].links == links)
			{
				if (!links)
					b = asv.schuifcomponenten[i].meldAan(this, x1 + 10, y1);
				else
					b = asv.schuifcomponenten[i].meldAan(this, x1 - 10, y1);
			}
			if (b)
			{
				vast = true;
				ontvanger = asv.schuifcomponenten[i];
				Pijl p = new Pijl(asv);
				p.zetLinks(links);
				zender.voegPijlToe(p);
				actief = false;
				asv.tekenOpnieuw();
				return;
			}
		}
		if (actief)
			pijlTerug();
		actief = false;
		asv.tekenOpnieuw();
	}

}
