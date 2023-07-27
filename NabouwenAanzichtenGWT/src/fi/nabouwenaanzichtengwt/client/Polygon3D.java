package fi.nabouwenaanzichtengwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
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
	 *  is het het geprojecteerde 3d-polygon leeg (transparant), de vulling wordt dan niet getekend
	 */
	public boolean isLeeg;
	
	/**
	 * teken dit Polygon3D: teken alleen de buitenkant van de vlakken, d.w.z.
	 * teken alleen die vlakken waarvan de normaalvector een positieve z-waarde
	 * heeft   
	 * @param gIm Context2d om te tekenen
	 * @param schaduw true: teken de vulkleuren van de vlakken met schaduwwwerking 
	 */
	public void draw(Context2d gIm, boolean schaduw)
  	{
		if (normaal.z > 0)
		{	
			if(schaduw)
			{	String vkString = vulkleur.toString().substring(4,vulkleur.toString().length()-1);
				String[] kleurenStr = StringUtils.split(vkString,",");

				int blue =  Integer.parseInt(kleurenStr[2]);
				int green = Integer.parseInt(kleurenStr[1]);
				int red =   Integer.parseInt(kleurenStr[0]);
				
				double grijsfactor = 0.5*((-normaal.x - normaal.y + normaal.z)/Math.sqrt(3)+1);
				if (grijsfactor<0)
					grijsfactor=0;
				if (grijsfactor>1)
					grijsfactor=1;
				int roodwaarde = 50+(int)(red*grijsfactor*0.75);
				int groenwaarde = 50+(int)(green*grijsfactor*0.75);
				int blauwwaarde = 50+(int)(blue*grijsfactor*0.75);
				
				vulkleur = CssColor.make(roodwaarde,groenwaarde,blauwwaarde);
				gIm.setFillStyle(vulkleur);
			}
			else
			{	gIm.setFillStyle(vulkleur);
			}
			
			CssColor gebruikteLijnkleur = lijnkleur;
			CssColor gebruikteVulkleur = vulkleur;
			
			if (isLeeg)
				gebruikteVulkleur = null;
			if (!isLijn && !isOmlijnd)
				gebruikteLijnkleur = null;
			
			pol.draw(gIm, gebruikteLijnkleur, gebruikteVulkleur);
			
		}
  	}
}
