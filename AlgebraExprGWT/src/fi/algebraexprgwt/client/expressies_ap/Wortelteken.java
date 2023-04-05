package fi.algebraexprgwt.client.expressies_ap;

//import java.awt.*;

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
	
	//public void paint(Graphics gIm, int x, int y)
	public void paint(Context2d gIm, int x, int y)
  	{ 	//gIm.setColor(Color.black);
		gIm.setStrokeStyle(CssColor.make(0,0,0));
		
		gIm.beginPath();
		gIm.moveTo(x+2,y+hoogte/2);
		gIm.lineTo(x+hoogte/4,y+hoogte-1);
		gIm.stroke();
		//gIm.drawLine(x+2,y+hoogte/2,x+hoogte/4,y+hoogte-1);
		
		gIm.beginPath();
		gIm.moveTo(x+hoogte/2,y+0);
		gIm.lineTo(x+hoogte/4,y+hoogte-1);
		gIm.stroke();
		//gIm.drawLine(x+hoogte/2,y+0,x+hoogte/4,y+hoogte-1);
		
		gIm.beginPath();
		gIm.moveTo(x+breedte-1,y+0);
		gIm.lineTo(x+hoogte/2,y+0);
		gIm.stroke();
		//gIm.drawLine(x+breedte-1,y+0,x+hoogte/2,y+0);
	}
}
