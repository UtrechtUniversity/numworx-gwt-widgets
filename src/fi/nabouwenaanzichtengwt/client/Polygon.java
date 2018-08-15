package fi.nabouwenaanzichtengwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * GWT heeft geen klasse Polygon; zie klasse Polygon in KladjeGWT 
 * @author Peter Boon
 */

public class Polygon
{
	int aantalPunten;
	int[] puntenX;
	int[] puntenY;
	
	
	public Polygon()
	{
		
	}
	public Polygon(int[] puntenX, int[]puntenY, int aantalPunten)
	{
		this.aantalPunten = aantalPunten;
		this.puntenX = new int[aantalPunten];
		this.puntenY = new int[aantalPunten];
		for (int k = 0; k < geefAantalPunten(); k++) 
		{
			this.puntenX[k] = puntenX[k];
			this.puntenY[k] = puntenY[k];
		}
	}
	
	public void draw(Context2d gIm, CssColor lijnkleur, CssColor vulkleur)
  	{
		if(vulkleur!=null)
		{	gIm.moveTo(geefPuntX(0), geefPuntY(0));
			gIm.setFillStyle(vulkleur);
			gIm.beginPath();
			for (int k = 1; k < geefAantalPunten(); k++) {
				gIm.lineTo(geefPuntX(k), geefPuntY(k));
			}
			gIm.lineTo(geefPuntX(0), geefPuntY(0));
			gIm.closePath();
			gIm.fill();
		}
		if(lijnkleur!=null)
		{	gIm.moveTo(geefPuntX(0), geefPuntY(0));
			gIm.setStrokeStyle(lijnkleur);
			gIm.beginPath();
			for (int k = 1; k < geefAantalPunten(); k++) {
				gIm.lineTo(geefPuntX(k), geefPuntY(k));
			}
			gIm.lineTo(geefPuntX(0), geefPuntY(0));
			gIm.closePath();
			gIm.stroke();
		}
		
  	}
	
	public int geefPuntX(int nr)
	{ 	return puntenX[nr];
	}
	
	public int geefPuntY(int nr)
	{ 	return puntenY[nr];
	}
	
	public int geefAantalPunten()
	{	return aantalPunten;
	}
	
	public void translate(int dx, int dy)
	{
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{
			puntenX[pCnt] += dx;
			puntenY[pCnt] += dy;
		}
	}
	
	public boolean contains(int x, int y) 
	{
		return contains((double)x, (double)y);
	}
	
	public boolean contains(double x, double y) 
	{
		if (aantalPunten <= 2) 
		{
			return false;
		}
		int hits = 0;
		 
		int lastx = puntenX[aantalPunten - 1];
		int lasty = puntenY[aantalPunten - 1];
		int curx, cury;
		 
		// Walk the edges of the polygon
		for (int i = 0; i < aantalPunten; lastx = curx, lasty = cury, i++) 
		{
		    curx = puntenX[i];
		    cury = puntenY[i];
		 
            if (cury == lasty) 
	        {
	            continue;
		    }
		 
		    int leftx;
		    if (curx < lastx) 
		    {
		        if (x >= lastx) 
		        {
		            continue;
		        }
		        leftx = curx;
		     } else 
		     {
		         if (x >= curx) 
		         {
		             continue;
		         }
		         leftx = lastx;
	         }
		 
	         double test1, test2;
	         if (cury < lasty) 
	         {
		         if (y < cury || y >= lasty) 
		         {
		             continue;
		         }
		         if (x < leftx) 
		         {
		             hits++;
		             continue;
		         }
		         test1 = x - curx;
		         test2 = y - cury;
		     }
	         else 
	         {
		         if (y < lasty || y >= cury) 
		         {
		             continue;
		         }
		         if (x < leftx) 
		         {
		             hits++;
		             continue;
		         }
		         test1 = x - lastx;
		         test2 = y - lasty;
	         }

	         if (test1 < (test2 / (lasty - cury) * (lastx - curx))) 
	         {
		         hits++;
		     }
	     } // for
		 
	     return ((hits & 1) != 0);
	 }
	
}
