package fi.algebraexprgwt.client.expressies_ap;

//import java.awt.*;

import com.google.gwt.canvas.dom.client.Context2d;

public class HaakjeLinks 
{	
	int breedte;
	int hoogte;
		
	public HaakjeLinks(int h)
	{	hoogte = h-2;
	}
	
	//public void teken(Graphics g, int x, int y)
	public void teken(Context2d g, int x, int y)
  	{ 	//int h = g.getFontMetrics().getHeight();
		int h = hoogte;
		int hh = h/2;
		int b = h/4;
		int bb = b/2;
		breedte = b;
		//y++;
		y -= 9;
		g.beginPath();
		g.moveTo(x+b, y);
		g.lineTo(x+b-bb, y+bb);
		g.stroke();
		//g.drawLine(x+b, y, x+b-bb, y+bb);
		
		g.beginPath();
		g.moveTo(x+b-bb, y+bb);
		g.lineTo(x, y+hh-b);
		g.stroke();
		//g.drawLine(x+b-bb, y+bb, x, y+hh-b);
		
		g.beginPath();
		g.moveTo(x, y+hh-b);
		g.lineTo(x, y+hoogte-hh+b);
		g.stroke();
		//g.drawLine(x, y+hh-b, x, y+hoogte-hh+b);
		
		g.beginPath();
		g.moveTo(x+b-bb, y+hoogte-bb);
		g.lineTo(x, y+hoogte-hh+b);
		g.stroke();
		//g.drawLine(x+b-bb, y+hoogte-bb, x, y+hoogte-hh+b);
		
		g.beginPath();
		g.moveTo(x+b, y+hoogte);
		g.lineTo(x+b-bb, y+hoogte-bb);
		g.stroke();
		//g.drawLine(x+b, y+hoogte, x+b-bb, y+hoogte-bb);
	}
	
	//public void zetMaat(FontMetrics fm)
	public void zetMaat(int fs, Context2d c2d)
  	{	//int h =fm.getHeight();
		int h = fs;
		int b = h/4;
		breedte = b;
	}
	//public static int geefHBreedte(FontMetrics fm)
	public static int geefHBreedte(int fs)
	{	//int h =fm.getHeight();
		int h = fs;
		int b = h/4;
		return b;
	}

}
