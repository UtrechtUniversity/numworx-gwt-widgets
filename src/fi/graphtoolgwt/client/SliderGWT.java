package fi.graphtoolgwt.client;

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.logging.Logger;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.touch.client.Point;

import fi.graphtoolgwt.client.GraphToolGWT.MouseHandler;

public class SliderGWT {
	
	private static Logger logger = Logger.getLogger("Slider");
	private final static int cDefault_x = 15;
	private final static int cDefault_y = 15;
	private final static int cDefault_marge = 3;
	private final static double cDefault_onderGrensWaarde = 0.0;
	private final static double cDefault_bovenGrensWaarde = 1.0;

	private final static int cKnobSize = 3;

	private int marge = cDefault_marge;
	private int x = cDefault_x; 
	private int y = cDefault_y;
	
	private String naam = "a";
	
	private int lengte;
	private int stand;
	
	private double onderGrensWaarde = cDefault_onderGrensWaarde;
	private double bovenGrensWaarde = cDefault_bovenGrensWaarde;
	
	static CssColor rood = CssColor.make(255, 0, 0);
	static CssColor zwart = CssColor.make(0, 0, 0);
	
	public SliderGWT(int aantalPix, int beginStand) {	

		lengte = aantalPix;
		stand = beginStand;
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void zetLengte(int aantalPix) {	
		lengte = aantalPix;
	}
	
	public void zetNaam(String naam) {
		this.naam = naam;
	}
	
	public boolean isRaak(int xPos, int yPos) {
		boolean xRaak = ((xPos >= x+stand-marge) && (xPos <= x+stand-marge));
		boolean yRaak = ((yPos >= y+-marge) && (yPos <= x-marge));
		return (xRaak && yRaak);
	}
	
	public void paint(Context2d g) { 
		
		g.setStrokeStyle(zwart);
		g.setFillStyle(rood);
		
		g.beginPath();
		g.moveTo(x,  y);
		g.lineTo(x + lengte, y);
		g.stroke();

		g.beginPath();
		g.arc(x + stand, y, cKnobSize, 0, 2 * Math.PI); 
		
		g.closePath();
		g.fill();
		g.stroke();
		
		if(naam.length() > 0)
		{	String fontString = "10px sans-serif";
			g.setFont(fontString);
			TextMetrics tm = g.measureText(naam);
			
			int naamBreedte = (int) Math.round(tm.getWidth());
			
			double waarde = (double) stand/ (double) lengte * (bovenGrensWaarde - onderGrensWaarde) + onderGrensWaarde;
			
			int aantalStappen = (int) ((bovenGrens - onderGrens)/stapGrootte);
			for(int i = 0; i < aantalStappen; i++)
			{	if(waarde - onderGrens < i * stapGrootte + stapGrootte/2)
				{	waarde = onderGrens + i * stapGrootte;
					break;
				}
			}
			if(waarde - onderGrens > (aantalStappen - 1) * stapGrootte + stapGrootte/2)
				waarde = bovenGrens;
			
			//g.setFont(font);
			g.setFillStyle(zwart);
			if(Math.round(stapGrootte) == stapGrootte)
			{
				int intWaarde = (int) Math.round(waarde);
				g.fillText(naam + "=" + intWaarde, stand + linksMarge - naamBreedte, 10);
			}
			
			else
			{	waarde = (double) Math.round(10*waarde)/10;
				g.fillText(naam + "=" + waarde, stand + linksMarge - naamBreedte, 10);
			}
		}		
	}
	
	public void update(Context2d gIm)
	{	paint(gIm);
	}
	
	public int geefStand()
	{	return stand;
	}
	
	public double geefWaarde() {
		return 0.0;
	}
	
	public void zetGrensWaarden (double onderGrensWaarde, double bovenGrensWaarde) {
		this.onderGrensWaarde = onderGrensWaarde;
		this.bovenGrensWaarde = bovenGrensWaarde;
	}
	
	public void zetStand(int xPos) {
		
		if (xPos <= x) {
			stand = 0;
			return;
		}
		
		if (xPos >= x + lengte -1) {
			stand = lengte - 1;
			return;
		}		
		
		// Stand bepalen afgerond naar stapGrootte en pixels
		double waarde =  ( ( double) (xPos - x) / lengte) * (bovenGrensWaarde-onderGrensWaarde) + onderGrensWaarde;  
		waarde = Math.Round((waarde - onderGrensWaarde) / stapGrootte) * stapGrootte + onderGrensWaarde;
		
		if(std>lengte)stand = lengte;
		else if(std<minimum)stand = minimum;
		else stand = std;
//		paint(gIm);
	}
	/*
	
	public void mousePressed(MouseEvent e)
	{	raak = (new Rectangle(stand,0,10,20)).contains(e.getX(), e.getY());
		muisStartX = e.getX();
		if (raak)// && actionListener != null)
		{	//actionListener.actionPerformed( new ActionEvent(this, 0, "start") );
		}
	}
	
	public void mouseDragged(MouseEvent e)
	{	if(!raak && new Rectangle(stand,0,10,20).contains(e.getX(), e.getY()))
		{	raak = true;
			muisStartX = e.getX();
			if (raak)// && actionListener != null)
			{	//actionListener.actionPerformed( new ActionEvent(this, 0, "start") );
			}
		}
		if(raak)
		{	int x = e.getX();
			int dx = x - muisStartX;
			stand = stand + dx;
			if(stand>lengte) 
			{	stand = lengte;
			}
			else if(stand<minimum) 
			{	stand = minimum;
			}
			if(x<5 || x>lengte+20)
			{	raak = false;
			}
			paint(gIm);
			//if (actionListener != null)
 			//{	actionListener.actionPerformed( new ActionEvent(this, 0, "verschoven") );
 			//}
			muisStartX = x;
		}
	}
	*/
}
