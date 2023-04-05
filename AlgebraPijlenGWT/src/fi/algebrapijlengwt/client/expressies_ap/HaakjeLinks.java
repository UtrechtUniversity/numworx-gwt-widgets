package fi.algebrapijlengwt.client.expressies_ap;

import com.google.gwt.canvas.dom.client.Context2d;

public class HaakjeLinks 
{	
	int breedte;
	int hoogte;
		
	public HaakjeLinks(int h)
	{	hoogte = h-2;
	}
	
	public void teken(Context2d g, int x, int y)
  	{ 	
		int h = hoogte;
		int hh = h/2;
		int b = h/4;
		int bb = b/2;
		breedte = b;
		y -= 9;
		g.beginPath();
		g.moveTo(x+b, y);
		g.lineTo(x+b-bb, y+bb);
		g.stroke();
		
		g.beginPath();
		g.moveTo(x+b-bb, y+bb);
		g.lineTo(x, y+hh-b);
		g.stroke();
		
		g.beginPath();
		g.moveTo(x, y+hh-b);
		g.lineTo(x, y+hoogte-hh+b);
		g.stroke();
		
		g.beginPath();
		g.moveTo(x+b-bb, y+hoogte-bb);
		g.lineTo(x, y+hoogte-hh+b);
		g.stroke();
		
		g.beginPath();
		g.moveTo(x+b, y+hoogte);
		g.lineTo(x+b-bb, y+hoogte-bb);
		g.stroke();
	}
	
	public void zetMaat(int fs, Context2d c2d)
  	{	int h = fs;
		int b = h/4;
		breedte = b;
	}

	public static int geefHBreedte(int fs)
	{	int h = fs;
		int b = h/4;
		return b;
	}
}
