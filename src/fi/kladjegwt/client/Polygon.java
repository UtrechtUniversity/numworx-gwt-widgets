package fi.kladjegwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * klasse die een polygon in het vlak representeert (GWT heeft geen klasse Polygon) 
 * @author Peter Boon
 */

class Polygon
{
	/**
	 * aantal punten
	 */
	int aantalPunten;
	/**
	 * x-coordinaten van de punten als integers
	 */
	int[] puntenX;
	/**
	 * y-coordinaten van de punten als integers
	 */
	int[] puntenY;
	/**
	 * x-coordinaten van de punten als doubles
	 */
	double[] doubleX;
	/**
	 * y-coordinaten van de punten als doubles
	 */
	double[] doubleY;
	
	/**
	 * default constructor
	 */
	public Polygon()
	{
		
	}
	
	/**
	 * constructor, gehele coordinaten en aantal punten voorgeschreven
	 * @param puntenX x-coordinaten van de punten als integers
	 * @param puntenY y-coordinaten van de punten als integers
	 * @param aantalPunten aantal punten
	 */
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
	
	/**
	 * constructor, hard copy van Polygon p
	 * @param p Polygon dat gekopieerd moet worden 
	 */
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
	
	/**
	 * voeg het punt (x,y) met gehele coordinaten toe aan
	 * dit Polygon
	 * @param x x-coordinaat nieuw punt
	 * @param y y-coordinaat nieuw punt
	 */
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

	/**
	 * overloaded: voeg het punt (x,y) met double coordinaten toe aan
	 * dit Polygon
	 * @param x x-coordinaat nieuw punt
	 * @param y y-coordinaat nieuw punt
	 */
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

	/**
	 * teken dit Polygon (omlijning en binnengebied)
	 * @param gIm de Context2d om te tekenen 
	 * @param lijnkleur de kleur voor de omlijning 
	 * @param vulkleur de vulkleur
	 */
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
	
	/**
	 * getter voor de gehele x-waarde van punt nr
	 * @param nr index van punt 
	 * @return gehele x-waarde van punt nr
	 */
	public int geefPuntX(int nr)
	{ 	return puntenX[nr];
	}
	
	/**
	 * getter voor de gehele y-waarde van punt nr
	 * @param nr index van punt 
	 * @return gehele y-waarde van punt nr
	 */
	public int geefPuntY(int nr)
	{ 	return puntenY[nr];
	}
	
	/**
	 * getter voor de double x-waarde van punt nr
	 * @param nr index van punt 
	 * @return double x-waarde van punt nr
	 */
	public double geefPuntXD(int nr)
	{ 	return doubleX[nr];
	}

	/**
	 * getter voor de double y-waarde van punt nr
	 * @param nr index van punt 
	 * @return double y-waarde van punt nr
	 */
	public double geefPuntYD(int nr)
	{ 	return doubleY[nr];
	}

	/**
	 * getter voor het aantal punten
	 * @return aantalPunten
	 */
	public int geefAantalPunten()
	{	return aantalPunten;
	}
	
	/**
	 * verschuif (transleer) dit Polygon langs de vector (dx,dy) 
	 * @param dx translatie in x-richting
	 * @param dy translatie in y-richting
	 */
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

	/**
	 * roteer dit Polygon tegen de klok in over rotation radialen
	 * en gebruik het punt (cx,cy) als centrum van de rotatie
	 * @param rotation rotatie tegen de klok in in radialen
	 * @param cx x-coordinaat rotatie centrum
	 * @param cy y-coordinaat rotatie centrum
	 */
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
	
	/**
	 * schaal dit Polygon met een factor s vanuit het punt (cx,xy)
	 * @param s schaal factor
	 * @param cx x-coordinaat schaal-centrum
	 * @param cy y-coordinaat schaal-centrum
	 */
	public void scale(double s, double cx, double cy)
	{
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{	doubleX[pCnt] = s * (doubleX[pCnt] - cx) + cx;
			doubleY[pCnt] = s * (doubleY[pCnt] - cy) + cy;
			puntenX[pCnt] = (int) Math.round(doubleX[pCnt]);
			puntenY[pCnt] = (int) Math.round(doubleY[pCnt]);
		}
	}

	/**
	 * schaal dit Polygon met een factor sx in de x-richting en met een
	 * factor sy in de y-richting vanuit het punt (cx,xy)
	 * @param sx schaal factor in de x-richting
	 * @param sy schaal factor in de y-richting
	 * @param cx x-coordinaat schaal-centrum
	 * @param cy y-coordinaat schaal-centrum
	 */
	public void scale(double sx, double sy, double cx, double cy)
	{
		for (int pCnt = 0; pCnt < aantalPunten; pCnt++)
		{	doubleX[pCnt] = sx * (doubleX[pCnt] - cx) + cx;
			doubleY[pCnt] = sy * (doubleY[pCnt] - cy) + cy;
			puntenX[pCnt] = (int) Math.round(doubleX[pCnt]);
			puntenY[pCnt] = (int) Math.round(doubleY[pCnt]);
		}
	}
	
	
	/**
	 * translate this Polygon over (-cx,-cy), transform it by the linear transformation
	 * M and translate the result over (cx,cy)
	 * @param m00 M linksboven
	 * @param m01 M rechtboven
	 * @param m10 M linksonder
	 * @param m11 M rechtsonder
	 * @param cx x-coordinaat pseudo-centrum
	 * @param cy y-coordinaat pseudo-centrum
	 */
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

	/**
	 * transformeer dit Polygon m.b.v. de affiene transformatie at
	 * @param at affiene transformatie 
	 */
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

	/**
	 * check of dit Polygon het gehele punt (x,y) bevat
	 * @param x gehele x-coordinaat
	 * @param y gehele y-coordinaat
	 * @return true/false
	 */
	public boolean contains(int x, int y) 
	{
		return contains((double)x, (double)y);
	}

	/**
	 * check of dit Polygon het double punt (x,y) bevat
	 * @param x double x-coordinaat
	 * @param y double y-coordinaat
	 * @return true/false
	 */
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
