package fi.tekenveelvlakgwt.client;

import com.google.gwt.canvas.dom.client.CssColor;

/**
 * een polygon in 3-space; merk op dat van zo'n polygon
 * alleen de projectie op het x-y-vlak (kijkrichting is
 * vanuit de positieve z-as), de normaalvector (kijken we naar 
 * de voorkant of achterkant van het 3d-polygon?) en de
 * gemiddelde z-waarde van de punten in het 3d-polygon 
 * (ter bepaling van de volgorde waarin meerdere 3d-polygons getekend
 * moeten worden) bekend hoeft te zijn. 
 */

public class Polygon3D 
{
	/**
	 * het 3d-polygon geprojecteerd op het x-y-vlak
	 */
	public Polygon pol;
	/**
	 * de normaalvector van het 3d-polygon
	 */
	public Punt3D normaal;
	/**
	 * de gemiddelde z-waarde van de punten in het 3d-polygon
	 */
	public double gemz;
	/**
	 * de vulkleur van het geprojecteerde 3d-polygon
	 */
	public CssColor vulkleur;
	/**
	 * de outline-kleur van het geprojecteerde 3d-polygon
	 */
	public CssColor lijnkleur;
	/**
	 * is het geprojecteerde 3d-polygon een lijnsegment?
	 */
	public boolean isLijn;
	/**
	 * moet de outline van het geprojecteerde 3d-polygon getekend worden? 
	 */
	public boolean isOmlijnd;
	/**
	 *  is het het geprojecteerde 3d-polygon leeg (transparant), het wordt dan niet getekend
	 */
	public boolean isLeeg;
}
