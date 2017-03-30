package fi.weblogo3dgwt.client.logotekenap3d;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

public class Polygon
{
	public int aantalPunten;
	int[] puntenX;
	int[] puntenY;
	public double[] doubleX;
	public double[] doubleY;
	
	public Polygon()
	{
		
	}
	public Polygon(int[] puntenX, int[]puntenY, int aantalPunten)
	{
		this.aantalPunten = aantalPunten;
		this.puntenX = new int[aantalPunten];
		this.puntenY = new int[aantalPunten];
		doubleX = new double[aantalPunten];
		doubleY = new double[aantalPunten];
		for (int k = 0; k < geefAantalPunten(); k++) 
		{
			this.puntenX[k] = puntenX[k];
			this.puntenY[k] = puntenY[k];
			this.doubleX[k] = puntenX[k];
			this.doubleY[k] = puntenY[k];
		}
	}
	
	public Polygon (Polygon p)
	{	
		aantalPunten = p.aantalPunten;
		puntenX = new int[aantalPunten];
		puntenY = new int[aantalPunten];
		doubleX = new double[aantalPunten];
		doubleY = new double[aantalPunten];
		for (int k = 0; k < geefAantalPunten(); k++) 
		{
			puntenX[k] = p.puntenX[k];
			puntenY[k] = p.puntenY[k];
			doubleX[k] = p.doubleX[k];
			doubleY[k] = p.doubleY[k];
		}
	}
	
	public void addPoint(int x, int y)
	{
		int[] oudePuntenX = puntenX;
		int[] oudePuntenY = puntenY;
		double[] oudeDoubleX = doubleX;
		double[] oudeDoubleY = doubleY;
		
		aantalPunten++;
		puntenX = new int[aantalPunten];
		puntenY = new int[aantalPunten];
		doubleX = new double[aantalPunten];
		doubleY = new double[aantalPunten];
		
		for (int pCnt = 0; pCnt < (aantalPunten - 1); pCnt++)
		{
			puntenX[pCnt] = oudePuntenX[pCnt];
			puntenY[pCnt] = oudePuntenY[pCnt];
			doubleX[pCnt] = oudeDoubleX[pCnt];
			doubleY[pCnt] = oudeDoubleY[pCnt];
		}
		puntenX[aantalPunten - 1] = x;
		puntenY[aantalPunten - 1] = y;
		doubleX[aantalPunten - 1] = x;
		doubleY[aantalPunten - 1] = y;
	}
	
	public void addPoint(double x, double y)
	{
		int[] oudePuntenX = puntenX;
		int[] oudePuntenY = puntenY;
		double[] oudeDoubleX = doubleX;
		double[] oudeDoubleY = doubleY;
		
		aantalPunten++;
		puntenX = new int[aantalPunten];
		puntenY = new int[aantalPunten];
		doubleX = new double[aantalPunten];
		doubleY = new double[aantalPunten];
		
		for (int pCnt = 0; pCnt < (aantalPunten - 1); pCnt++)
		{
			puntenX[pCnt] = oudePuntenX[pCnt];
			puntenY[pCnt] = oudePuntenY[pCnt];
			doubleX[pCnt] = oudeDoubleX[pCnt];
			doubleY[pCnt] = oudeDoubleY[pCnt];
		}
		puntenX[aantalPunten - 1] = (int) Math.round(x);
		puntenY[aantalPunten - 1] = (int) Math.round(y);;
		doubleX[aantalPunten - 1] = x;
		doubleY[aantalPunten - 1] = y;
	}
		
	public void draw(Context2d gIm, CssColor lijnkleur, CssColor vulkleur)
  	{
		if (vulkleur != null)
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
		if (lijnkleur != null)
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
	
	public double geefPuntXD(int nr)
	{ 	return doubleX[nr];
	}
	
	public double geefPuntYD(int nr)
	{ 	return doubleY[nr];
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
			doubleX[pCnt] += dx;
			doubleY[pCnt] += dy;
			
		}
	}

	public void rotate(double rotation, double cx, double cy)
	{	
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{
			double rotX = Math.cos(rotation) * (doubleX[pCnt] - cx) - Math.sin(rotation) * (doubleY[pCnt] - cy);
			double rotY = Math.sin(rotation) * (doubleX[pCnt] - cx) + Math.cos(rotation) * (doubleY[pCnt] - cy);
						
			puntenX[pCnt] = (int) Math.round(cx + rotX);
			puntenY[pCnt] = (int) Math.round(cy + rotY);
			doubleX[pCnt] = cx + rotX;
			doubleY[pCnt] = cy + rotY;
			
		}
		
	}
	
	public void scale(double s, double cx, double cy)
	{
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{	doubleX[pCnt] = s * (doubleX[pCnt] - cx) + cx;
			doubleY[pCnt] = s * (doubleY[pCnt] - cy) + cy;
			puntenX[pCnt] = (int) Math.round(doubleX[pCnt]);
			puntenY[pCnt] = (int) Math.round(doubleY[pCnt]);
		}
	}

	public void scale(double sx, double sy, double cx, double cy)
	{
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{	doubleX[pCnt] = sx * (doubleX[pCnt] - cx) + cx;
			doubleY[pCnt] = sy * (doubleY[pCnt] - cy) + cy;
			puntenX[pCnt] = (int) Math.round(doubleX[pCnt]);
			puntenY[pCnt] = (int) Math.round(doubleY[pCnt]);
		}
	}
	
	public void transformBy(double m00, double m01, double m10, double m11, double cx, double cy)
	{
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{
			double doubleXpCnt = m00 * (doubleX[pCnt] - cx) + m01 * (doubleY[pCnt] - cy);
			double doubleYpCnt = m10 * (doubleX[pCnt] - cx) + m11 * (doubleY[pCnt] - cy);
			doubleX[pCnt] = doubleXpCnt + cx;
			doubleY[pCnt] = doubleYpCnt + cy;
			puntenX[pCnt] = (int) Math.round(doubleX[pCnt]);
			puntenY[pCnt] = (int) Math.round(doubleY[pCnt]);
			
		}
	}
	
/*	
	public void transformBy(AffineTransform at)
	{
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{
			double doubleXpCnt = at.m00 * doubleX[pCnt] + at.m01 * doubleY[pCnt] + at.b0;
			double doubleYpCnt = at.m10 * doubleX[pCnt] + at.m11 * doubleY[pCnt] + at.b1;
			doubleX[pCnt] = doubleXpCnt;
			doubleY[pCnt] = doubleYpCnt;
			puntenX[pCnt] = (int) Math.round(doubleX[pCnt]);
			puntenY[pCnt] = (int) Math.round(doubleY[pCnt]);
			
		}
	}
*/
	
	public Rectangle getBounds()
	{
		double minXD = 1000;
		double maxXD = -100;
		double minYD = 1000;
		double maxYD = -100;
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{
			if (doubleX[pCnt] < minXD)
				minXD = doubleX[pCnt];
			if (doubleX[pCnt] > maxXD)
				maxXD = doubleX[pCnt];
			if (doubleY[pCnt] < minYD)
				minYD = doubleY[pCnt];
			if (doubleY[pCnt] > maxYD)
				maxYD = doubleY[pCnt];
		}
		
		int minX = (int) Math.round(minXD);
		int maxX = (int) Math.round(maxXD);
		int minY = (int) Math.round(minYD);
		int maxY = (int) Math.round(maxYD);

		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
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
		         } 
		         else 
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
		     }
		 
		     return ((hits & 1) != 0);
		 }
		 
	
		     
	
}
