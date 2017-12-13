package fi.weblogo3dgwt.client.logotekenap3d;					 

import fi.weblogo3dgwt.client.WebLogo3dGWT;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.canvas.client.Canvas;

/**
 * class handling all drawing: this class contains an instance of class TekenBlad3D, which 
 * in turn contains a Canvas to be drawn upon and the actual 3D-drawing methods;
 * note that the drawing commands from the program are forwarded to tekenblad3D; <br>
 * this class also contains an instance of class MuisBeheerder, for processing mouse and
 * touch events on the drawing Canvas (currently only mouseMove and TouchMove Events to
 * rotate the 3D-object); <br>
 * the class also forwards zoom requests and requests to change the drawing mode 
 * (solid/transparent/wireframe) to tekenBlad3d; <br>
 * all drawing is so-called 3d-turtle-graphics: the 3d-turtle (cursor) is actually a flat
 * turtle (cursor) and the plane determined by the 3d-turtle (cursor) is the current x-y-plane 
 * with the 3d-turtle always at the origin and pointing in the direction of the positive y-axis; 
 * the z-axis is of course perpendicular to this x-y-plane,  * with the positive z-axis obtained
 * by "screwing" the positive y-axis to the positive x-axis over the smallest angle; when using
 * the cursor (tracing) one obtains the directions of positive x- and y-axis from the shape of
 * the cursor (see method tekenCursor in class Tekenblad3D); <br>
 * there are two types of commands to move/rotate the 3d-turtle (cursor):<br>
 * commands that do not change the plane determined by the 3d-turtle (cursor):<br>
 * vooruit(dis): see class VooruitCComponent; stap(dx,dy): see class StapCComponent;
 * links(ang): see class LinksCComponent; rechts(ang): see class RechtsCComponent;<br>      
 * not available as command block: stapx(dx): see class StapXCommandComponent;  
 * stapy(dy): see class StapYCommandComponent;<br>
 * commands that move or rotate the plane determined by the 3d-turtle (cursor):<br>
 * stapz(dz): see class StapZCommandComponent; xdraai(ang): see class XDraaiCComponent;
 * ydraai(ang): see class YDraaiCComponent;<br>
 * not available as command block: zdraai(ang): see class ZDraaiCComponent; <br> 
 * note that links(ang),rechts(ang) and stap(dx,dy) are equivalent to
 * stapx(dx),stapy(dy) and zdraai(ang) and that vooruit(dis) is redundant. 
 */

public class TekenApplet3D extends LayoutPanel 
{
	/**
	 * class handling all drawing, the class also contains a Canvas to be drawn upon
	 */
	public Tekenblad3D tb;
	/**
	 * class processing all mouse and touch events on the Canvas of the tekenblad3D
	 */
	private MuisBeheerder mb;
	/**
	 * owner of this instance of TekenAppelt3D
	 */
	WebLogo3dGWT eigenaar;
	/**
	 * width and height
	 */
	int breedte, hoogte;
			  
	/**
	 * constructor
	 * @param wl3g owner
	 * @param w width
	 * @param h height
	 */
	public TekenApplet3D(WebLogo3dGWT wl3g, int w, int h)
	{
		eigenaar = wl3g;
		breedte = w;
		hoogte = h;
		init();
		
	}
	
	/**
	 * create and add instance of TekenBlad3D and Muisbeheerder
	 */
	public void init()
	{	tb = new Tekenblad3D(this, breedte, hoogte);
		if (tb.tekenbladCanvas != null)
		{
			add(tb.tekenbladCanvas);
			setWidgetLeftWidth(tb.tekenbladCanvas, 0, Style.Unit.PX, tb.breedte, Style.Unit.PX);
			setWidgetTopHeight(tb.tekenbladCanvas, 0, Style.Unit.PX, tb.hoogte, Style.Unit.PX);

		}
		maakMuisActieMogelijk();
	}	

	/**
	 * initialize the Context2d of the Canvas in tekenBlad3D
	 */
	public void initContext2d()
	{
		tb.initContext2d();
	}
	
	/**
	 * getter for the Canvas in tekenBlad3D
	 * @return drawing Canvas
	 */
	public Canvas getCanvas()
	{
		return tb.tekenbladCanvas;
	}
	
	/**
	 * create a Muisbeheerder and use it to listen to Mouse and Touch Events 
	 * on the Canvas in tekenBlad3D
	 */
	public void maakMuisActieMogelijk()
	{	mb = new MuisBeheerder(this);
		tb.tekenbladCanvas.addMouseDownHandler(mb);
		tb.tekenbladCanvas.addMouseUpHandler(mb);
		tb.tekenbladCanvas.addMouseMoveHandler(mb);
		tb.tekenbladCanvas.addTouchStartHandler(mb);
		tb.tekenbladCanvas.addTouchEndHandler(mb);
		tb.tekenbladCanvas.addTouchMoveHandler(mb);
	}

	/**
	 * getter for Muisbeheerder instance
	 * @return mb
	 */
	public MuisBeheerder geefMuisBeheerder()
	{	return mb;
	}
	
	/**
	 * get relative x-translation of last MouseMove/TouchMove Event
	 * @return dx
	 */
	public int geefSleepdx()
	{	return mb.geefSleepdx();
	}
	/**
	 * get relative y-translation of last MouseMove/TouchMove Event
	 * @return dy
	 */
	public int geefSleepdy()
	{	return mb.geefSleepdy();
	}
	/**
	 * get x-position of last MouseDown/TouchStart Event
	 * @return last x
	 */
	public int geefDrukx()
	{	return mb.geefDrukx();
	}
	/**
	 * get y-position of last MouseDown/TouchStart Event
	 * @return last y
	 */
	public int geefDruky()
	{	return mb.geefDruky();
	}
	/**
	 * get x-position of last MouseMove/TouchMove Event 
	 * @return last x
	 */
	public int geefX()
	{	return mb.geefX();
	}
	/**
	 * get y-position of last MouseMove/TouchMove Event 
	 * @return last y
	 */
	public int geefY()
	{	return mb.geefY();
	}

	/**
	 * execute the total program (cursor == false) or execute the
	 * program up to and including the current statement being traced
	 * (cursor == true); then paint the 3D-object 
	 * @param cursor true/false
	 */
	public void paintDrawing(boolean cursor)
	{	tb.paintDrawing(cursor);
	}

	/**
	 * draw transparant or solid 3D objects
	 * @param b true/false
	 */
	public void zetTransparant(boolean b)
	{	tb.zetTransparant(b);
	}

	/**
	 * draw wireframe or solid 3D objects
	 * @param b true/false
	 */
	public void zetDraadFiguur(boolean b)
	{	tb.zetDraadFiguur(b);
	}
	
	/**
	 * draw 3D object larger (button)
	 */
	public void zoomIn()
	{
		tb.zoomIn();
	}
	
	/**
	 * draw 3D object smaller (button)
	 */
	public void zoomUit()
	{
		tb.zoomUit();
	}
	
	/**
	 * set the zoom factor (setState)
	 * @param fac zoom factor
	 */
	public void zoom(double fac)
	{
		tb.zoom(fac);
	}

	/**
	 * get current x-rotation of 3D object (getState) 
	 * @return x-rotation
	 */
	public double geefDraaiX()
	{
		return tb.geefDraaiX();
	}

	/**
	 * get current y-rotation of 3D object (getState) 
	 * @return y-rotation
	 */
	public double geefDraaiY()
	{
		return tb.geefDraaiY();
	}

	/**
	 * set rotation of 3D object (setState)
	 * @param hx x-rotation
	 * @param hy y rotation
	 */
	public void zetBeginHoeken(double hx, double hy)
	{	tb.zetBeginHoeken(hx,hy);
	}

	/**
	 * initializes tekenBlad3D
	 */
	public void paintTekenblad()
	{
		tb.paintTekenblad();
	}

	/**
	 * rotate the x-y-z coordinate system angle
	 * degrees around the positive x-axis
	 * @param dh angle
	 */
	public void xdraai(double dh)
	{	tb.mat.xdraai(dh);
	}
	/**
	 * rotate the x-y-z coordinate system angle
	 * degrees around the positive y-axis
	 * @param dh angle
	 */
	public void ydraai(double dh)
	{	tb.mat.ydraai(dh);
	}
	/**
	 * rotate the x-y-z coordinate system angle
	 * degrees around the positive z-axis
	 * @param dh angle
	 */
	public void zdraai(double dh)
	{	tb.mat.zdraai(dh);
	}
	/**
	 * rotate the x-y-z coordinate system angle
	 * degrees clockwise around the positive z-axis 
	 * @param dh angle
	 */
	public void rechts(double dh)
	{	tb.mat.zdraai(-dh);
	}
	/**
	 * rotate the x-y-z coordinate system angle
	 * degrees anti-clockwise around the positive z-axis 
	 * @param dh angle
	 */
	public void links(double dh)
	{	tb.mat.zdraai(dh);
	}
	/**
	 * move dy in the direction of the positive y-axis
	 * @param dy y-translate
	 */
	public void stapy(double dy)
	{	tb.naarVolgendPunt(0,-dy,0);
	}
	/**
	 * move dy in the direction of the positive y-axis
	 * @param dy y-translate
	 */
	public void vooruit(double dy)
	{	tb.naarVolgendPunt(0,-dy,0);	
	}
	/**
	 * move dx in the direction of the positive x-axis
	 * @param dx x-translate
	 */
	public void stapx(double dx)
	{	tb.naarVolgendPunt(dx,0,0);
	}
	/**
	 * move dz in the direction of the positive z-axis
	 * @param dz z-translate
	 */
	public void stapz(double dz)
	{	tb.naarVolgendPunt(0,0,-dz);
	}
	public void stap(double dx,double dy,double dz)
	{	tb.naarVolgendPunt(dx,-dy,-dz);
	}
	/**
	 * move (dx,dy) in the x-y-plane
	 * @param dx x-translate
	 * @param dy y-translate
	 */
	public void stap(double dx,double dy)
	{	tb.naarVolgendPunt(dx,-dy,0);}
	
	/**
	 * activate the pen
	 */
	public void penAan()
	{	tb.penAan();
	}
	/**
	 * activate the pen in color kl
	 * @param kl color name
	 */
	public void penAan(String kl)
	{	tb.penAan(kl);
	}
	/**
	 * activate the pen in color (r,g,b)
	 * @param r red value
	 * @param g green value
	 * @param b blue value
	 */
	public void penAan(int r, int g, int b)
	{	tb.penAan(r, g, b);
	}
	/**
	 * de-activate the pen
	 */
	public void penUit()
	{	tb.penUit();
	}
	/**
	 * start polygon to be filled
	 */
	public void vulAan()
	{	tb.vulAan();
	}
	/**
	 * start polygon to be filled with color kl
	 * @param kl color name
	 */
	public void vulAan(String kl)
	{	tb.vulAan(kl);
	}
	/**
	 * start polygon to be filled with color (r,g,b)
	 * @param r red value
	 * @param g green value
	 * @param b blue value
	 */
	public void vulAan(int r, int g, int b)
	{	tb.vulAan(r, g, b);
	}
	/**
	 * de-activate filling and draw the polygon
	 */
	public void vulUit()
	{	tb.vulUit();
	}
	
	/**
	 * not implemented for lack of space
	 * @param s String to print
	 */
	public void print(String s){}
	/**
	 * not implemented for lack of space
	 * @param s String to println
	 */
	public void printl(String s){}
	/**
	 * not implemented for lack of space
	 * @param r background red
	 * @param g background green
	 * @param b background blue
	 */
	public void vulBlad(int r, int g, int b){}
	
	/**
	 * dragg action on drawing Canvas: this rotates the 3D-object
	 */
	public void muisSleepActie()
	{	tb.muisSleepActie();
	}
	/**
	 * not used
	 */
	public void muisDrukActie(){}
	/**
	 * not used
	 */
	public void muisLosActie(){}
}		
	

