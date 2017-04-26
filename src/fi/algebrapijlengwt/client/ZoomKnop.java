package fi.algebrapijlengwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * klasse die een knopje tekent met zoom-symbolen bepaald door een String code; 
 * gebruikt in de GrafiekComponent en voor zoom in Tabellen (zie klasse UitvoerSchuifCompoment); 
 */

public class ZoomKnop 	
{	
	/**
	 * Context2d om te tekenen
	 */	Context2d gIm;	/**
	 * de code van het knopje
	 */	protected String code;
	/**
	 * achtergrondkleur
	 */
	protected CssColor bgColor = CssColor.make(210,210,210);
	/**
	 * achtergrondkleur donkerder
	 */
	protected CssColor bgColorDarker = CssColor.make(170,170,170);
	/**
	 * achtergrondkleur lichter
	 */
	protected CssColor bgColorBrighter = CssColor.make(250,250,250);
	/**
	 * x-coordinaat
	 */
	int xPos;
	/**
	 * y-coordinaat
	 */
	int yPos;
	/**
	 * breedte
	 */
	int breedte;
	/**
	 * hoogte
	 */
	int hoogte;
	/**
	 * is dit knopje zichtbaar?
	 */
	boolean visible = true;

	/**
	 * constructor
	 * @param s code van de knop
	 * @param x x-positie
	 * @param y y-positie
	 * @param b breedte
	 * @param h hoogte
	 * @param c2d Context2d voor tekenen
	 */	public ZoomKnop(String s, int x, int y, int b, int h, Context2d c2d)
	{	code = s;
		xPos = x; yPos = y; breedte = b; hoogte = h;
		gIm = c2d;
	}

	/**
	 * verplaats deze ZoomKnop over (dx,dy)
	 * @param dx x-translatie
	 * @param dy y-translatie
	 */
	public void translate(int dx, int dy)
	{
		xPos += dx;
		yPos += dy;
	}

	public void paint()
	{	paintBuffer(gIm);
	}
	/**
	 * teken de zoomknop
	 * @param g de Context2d om te tekenen 
	 */
	public void paintBuffer(Context2d g)
	{			if (!visible)
			return;
		int xPos = this.xPos;
		// correctie
		if (code.equals("zoomintabel") || code.equals("zoomuittabel"))
			xPos += 2;
		// achtergrond
		g.setFillStyle(bgColor);
		gIm.fillRect(xPos+0,yPos+0,breedte,hoogte);
		// outline boven+links		g.setStrokeStyle(bgColorBrighter);
		g.beginPath();
		g.moveTo(xPos+0,yPos+0);
		g.lineTo(xPos+breedte-1,yPos+0);
		g.stroke();		g.beginPath();
		g.moveTo(xPos+0,yPos+0);
		g.lineTo(xPos+0,yPos+hoogte-1);
		g.stroke();
		// outline rechts+onder
		g.setStrokeStyle(bgColorDarker);
		g.beginPath();
		g.moveTo(xPos+breedte-1,yPos+0);
		g.lineTo(xPos+breedte-1,yPos+hoogte-1);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+0,yPos+hoogte-1);
		g.lineTo(xPos+breedte-1,yPos+hoogte-1);
		g.stroke();
		// zoomsymbolen
		g.setStrokeStyle(CssColor.make(0,0,0));
		// cirkel met + erin en horizontale
		// tweepuntige pijl
		if (code.equals("zoominx"))		{	
			g.beginPath();
			g.arc(xPos+12, yPos+7, 4, 0, 2*Math.PI);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+12,yPos+5);
			g.lineTo(xPos+12,yPos+9);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+10,yPos+7);
			g.lineTo(xPos+14,yPos+7);
			g.stroke();
			
			g.beginPath();
			g.moveTo(xPos+8,yPos+18);
			g.lineTo(xPos+17,yPos+18);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+8,yPos+18);
			g.lineTo(xPos+10,yPos+16);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+8,yPos+18);
			g.lineTo(xPos+10,yPos+20);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+17,yPos+18);
			g.lineTo(xPos+15,yPos+16);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+17,yPos+18);
			g.lineTo(xPos+15,yPos+20);
			g.stroke();
		}
		// cirkel met - erin en horizontale
		// tweepuntige pijl
		else if(code.equals("zoomuitx"))		{			
			g.beginPath();
			g.arc(xPos+12, yPos+7, 4, 0, 2*Math.PI);
			g.stroke();
			
			g.beginPath();
			g.moveTo(xPos+10,yPos+7);
			g.lineTo(xPos+14,yPos+7);
			g.stroke();
			
			g.beginPath();
			g.moveTo(xPos+8,yPos+18);
			g.lineTo(xPos+17,yPos+18);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+8,yPos+18);
			g.lineTo(xPos+10,yPos+16);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+8,yPos+18);
			g.lineTo(xPos+10,yPos+20);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+17,yPos+18);
			g.lineTo(xPos+15,yPos+16);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+17,yPos+18);
			g.lineTo(xPos+15,yPos+20);
			g.stroke();
		}
		// cirkel met + erin en vertikale
		// tweepuntige pijl
		else if(code.equals("zoominy"))		{			
			g.beginPath();
			g.arc(xPos+12, yPos+7, 4, 0, 2*Math.PI);
			g.stroke();
			
			g.beginPath();
			g.moveTo(xPos+12,yPos+5);
			g.lineTo(xPos+12,yPos+9);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+10,yPos+7);
			g.lineTo(xPos+14,yPos+7);
			g.stroke();
			
			g.beginPath();
			g.moveTo(xPos+12,yPos+14);
			g.lineTo(xPos+12,yPos+22);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+12,yPos+14);
			g.lineTo(xPos+10,yPos+16);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+12,yPos+14);
			g.lineTo(xPos+14,yPos+16);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+12,yPos+22);
			g.lineTo(xPos+10,yPos+20);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+12,yPos+22);
			g.lineTo(xPos+14,yPos+20);
			g.stroke();
		}
		// cirkel met - erin en vertikale
		// tweepuntige pijl
		else if(code.equals("zoomuity"))		{			
			g.beginPath();
			g.arc(xPos+12, yPos+7, 4, 0, 2*Math.PI);
			g.stroke();
			
			g.beginPath();
			g.moveTo(xPos+10,yPos+7);
			g.lineTo(xPos+14,yPos+7);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+12,yPos+14);
			g.lineTo(xPos+12,yPos+22);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+12,yPos+14);
			g.lineTo(xPos+10,yPos+16);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+12,yPos+14);
			g.lineTo(xPos+14,yPos+16);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+12,yPos+22);
			g.lineTo(xPos+10,yPos+20);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+12,yPos+22);
			g.lineTo(xPos+14,yPos+20);
			g.stroke();
		}
		// cirkel met + erin en horizontale en vertikale
		// tweepuntige pijl		else if(code.equals("zoomin"))		{	
			g.beginPath();
			g.arc(xPos+9, yPos+9, 5, 0, 2*Math.PI);
			g.stroke();
			
			g.beginPath();
			g.moveTo(xPos+6,yPos+9);
			g.lineTo(xPos+12,yPos+9);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+9,yPos+6);
			g.lineTo(xPos+9,yPos+12);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+15,yPos+19);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+7,yPos+17);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+7,yPos+21);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+15,yPos+19);
			g.lineTo(xPos+13,yPos+17);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+15,yPos+19);
			g.lineTo(xPos+13,yPos+21);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+19,yPos+15);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+17,yPos+7);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+21,yPos+7);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+19,yPos+15);
			g.lineTo(xPos+17,yPos+13);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+19,yPos+15);
			g.lineTo(xPos+21,yPos+13);
			g.stroke();

		}
		// cirkel met - erin en horizontale en vertikale
		// tweepuntige pijl
		else if(code.equals("zoomuit"))		{	
			g.beginPath();
			g.arc(xPos+9, yPos+9, 5, 0, 2*Math.PI);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+6,yPos+9);
			g.lineTo(xPos+12,yPos+9);
			g.stroke();
			
			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+15,yPos+19);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+7,yPos+17);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+7,yPos+21);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+15,yPos+19);
			g.lineTo(xPos+13,yPos+17);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+15,yPos+19);
			g.lineTo(xPos+13,yPos+21);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+19,yPos+15);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+17,yPos+7);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+21,yPos+7);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+19,yPos+15);
			g.lineTo(xPos+17,yPos+13);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+19,yPos+15);
			g.lineTo(xPos+21,yPos+13);
			g.stroke();

		}
		// kruis		else if(code.equals("standaard"))		{	
			g.beginPath();
			g.moveTo(xPos+4,yPos+12);
			g.lineTo(xPos+20,yPos+12);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+12,yPos+4);
			g.lineTo(xPos+12,yPos+20);
			g.stroke();
	
		}
		// cirkel met + erin
		else if(code.equals("zoomintabel"))
		{	
			g.beginPath();
			g.arc(xPos+5, yPos+5, 5, 0, 2*Math.PI);
			g.stroke();
			
			g.beginPath();
			g.moveTo(xPos+2,yPos+5);
			g.lineTo(xPos+8,yPos+5);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+5,yPos+2);
			g.lineTo(xPos+5,yPos+8);
			g.stroke();
		}
		// cirkel met - erin
		else if(code.equals("zoomuittabel"))
		{	
			g.beginPath();
			g.arc(xPos+5, yPos+5, 5, 0, 2*Math.PI);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+2,yPos+5);
			g.lineTo(xPos+8,yPos+5);
			g.stroke();
		}
	}
}