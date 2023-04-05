package fi.algebrapijlengwt.client.expressies_ap;

import com.google.gwt.canvas.dom.client.Context2d;

public class HaakjeRechts 
{	
	int breedte;
	int hoogte;
		
	public HaakjeRechts(int h)
	{	
		hoogte = h-2;
	}
	
	public void teken(Context2d g, int x, int y)
  	{ 	
		int h = hoogte;
		int hh = h/2;
		int b = h/4;
		int bb = b/2;
		breedte = b;
		y -= 9;
		x--;
		g.beginPath();
		g.moveTo(x, y);
		g.lineTo(x+bb, y+bb);
		g.stroke();

		g.beginPath();
		g.moveTo(x+bb, y+bb);
		g.lineTo(x+b, y+hh-b);
		g.stroke();

		g.beginPath();
		g.moveTo(x+b, y+hh-b);
		g.lineTo(x+b, y+hoogte-hh+b);
		g.stroke();
		
		g.beginPath();
		g.moveTo(x+bb, y+hoogte-bb);
		g.lineTo(x+b, y+hoogte-hh+b);
		g.stroke();

		g.beginPath();
		g.moveTo(x, y+hoogte);
		g.lineTo(x+bb, y+hoogte-bb);
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
