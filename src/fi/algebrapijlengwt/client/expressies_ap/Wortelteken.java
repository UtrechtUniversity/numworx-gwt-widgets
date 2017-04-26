package fi.algebrapijlengwt.client.expressies_ap;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


public class Wortelteken 
{	
	int breedte;
	int hoogte;
			
	public Wortelteken(int b, int h)
	{	breedte = b;
		hoogte = h;
	}
	
	public void paint(Context2d gIm, int x, int y)
  	{ 	
		gIm.setStrokeStyle(CssColor.make(0,0,0));
		
		gIm.beginPath();
		gIm.moveTo(x+2,y+hoogte/2);
		gIm.lineTo(x+hoogte/4,y+hoogte-1);
		gIm.stroke();
		
		
		gIm.beginPath();
		gIm.moveTo(x+hoogte/2,y+0);
		gIm.lineTo(x+hoogte/4,y+hoogte-1);
		gIm.stroke();
		
		
		gIm.beginPath();
		gIm.moveTo(x+breedte-1,y+0);
		gIm.lineTo(x+hoogte/2,y+0);
		gIm.stroke();
		
	}
}
