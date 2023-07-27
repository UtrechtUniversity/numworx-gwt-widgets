package fi.weblogo3dgwt.client.logotekenap3d;

import com.google.gwt.canvas.dom.client.CssColor;

/**
 * a Polygon in 3-space: note that it is only necessary to remember the
 * projection of the polygon on the x-y-plane, its normal vector in
 * 3-space (for shading) and the average z-coordinate of its 3-space
 * points (for correctly drawing multiple 3d-polygons); see class Lichaam3D  
 */

class Polygon3D
{
	public Polygon pol;
	public Punt3D normaal;
	public double gemz;
	public CssColor vulkleur,lijnkleur;
	public boolean isLijn,isOmlijnd;
	public String naam = "";
}
