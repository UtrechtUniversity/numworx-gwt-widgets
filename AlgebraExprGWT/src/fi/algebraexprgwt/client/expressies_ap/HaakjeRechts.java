package fi.algebraexprgwt.client.expressies_ap;

//import java.awt.*;

import com.google.gwt.canvas.dom.client.Context2d;

public class HaakjeRechts 
{	
	int breedte;
	int hoogte;
		
	public HaakjeRechts(int h)
	{	
		hoogte = h-2;
		
	}
	
	//public void teken(Graphics g, int x, int y)
	public void teken(Context2d g, int x, int y)
  	{ 	//int h =g.getFontMetrics().getHeight();
		int h = hoogte;
		int hh = h/2;
		int b = h/4;
		int bb = b/2;
		breedte = b;
		//y++;
		y -= 9;
		x--;
		g.beginPath();
		g.moveTo(x, y);
		g.lineTo(x+bb, y+bb);
		g.stroke();
		//g.drawLine(x, y, x+bb, y+bb);

		g.beginPath();
		g.moveTo(x+bb, y+bb);
		g.lineTo(x+b, y+hh-b);
		g.stroke();
		//g.drawLine(x+bb, y+bb, x+b, y+hh-b);

		g.beginPath();
		g.moveTo(x+b, y+hh-b);
		g.lineTo(x+b, y+hoogte-hh+b);
		g.stroke();
		//g.drawLine(x+b, y+hh-b, x+b, y+hoogte-hh+b);
		
		g.beginPath();
		g.moveTo(x+bb, y+hoogte-bb);
		g.lineTo(x+b, y+hoogte-hh+b);
		g.stroke();
		//g.drawLine(x+bb, y+hoogte-bb, x+b, y+hoogte-hh+b);

		g.beginPath();
		g.moveTo(x, y+hoogte);
		g.lineTo(x+bb, y+hoogte-bb);
		g.stroke();
		//g.drawLine(x, y+hoogte, x+bb, y+hoogte-bb);
		
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
