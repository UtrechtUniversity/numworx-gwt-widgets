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

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

public class SliderGWT {

	public Canvas grafiekGWTCanvas;
	public Context2d gIm;
	
	private int x, y;
	
	private int lengte;
	private int stand;
	private int minimum=-2;
	private int muisStartX;
	private boolean raak;
	private String naam = "";
	private double onderGrens, bovenGrens, stapGrootte;
	private int linksMarge = 5;
	private int bovenMarge = 5;
	
	static CssColor rood = CssColor.make(255, 0, 0);
	static CssColor zwart = CssColor.make(0, 0, 0);
	
	
	public SliderGWT(int aantalPix, int beginst)
	{	grafiekGWTCanvas = Canvas.createIfSupported();
		gIm = grafiekGWTCanvas.getContext2d();
		lengte = aantalPix;
		stand = beginst;
		//addMouseListener(this);
		//addMouseMotionListener(this);
		if(naam.length() > 0)
		{	linksMarge = 15;
			bovenMarge = 15;
		}
		setSize(lengte + 2 * linksMarge, bovenMarge + 8);
	}
	
	public void zetGrenzen(double onderGrens, double bovenGrens)
	{
		this.onderGrens = onderGrens;
		this.bovenGrens = bovenGrens;
	}
	
	public void zetStapGrootte(double stapGrootte)
	{
		this.stapGrootte = stapGrootte;
	}
	
	public void zetMinimum(int min)
	{
		minimum = min;
	}
	
	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	void setSize(int w, int h) {
		grafiekGWTCanvas.setWidth(w + "px");
		grafiekGWTCanvas.setHeight(h + "px");
		grafiekGWTCanvas.setCoordinateSpaceWidth(w);
		grafiekGWTCanvas.setCoordinateSpaceHeight(h);
	}
	
	public void zetLengte(int aantalPix)
	{	
		lengte = aantalPix;
		if(naam.length() > 0)
		{	linksMarge = 15;
			bovenMarge = 15;
		}
		setSize(lengte + 2 * linksMarge, bovenMarge + 8);
		paint(gIm);
	}
	
	public void zetNaam(String naam)
	{
		this.naam = naam;
		if(naam.length() > 0)
		{	linksMarge = 15;
			bovenMarge = 15;
		}
		setSize(lengte + 2 * linksMarge, bovenMarge + 8);
		paint(gIm);
	}
	
	public boolean isRaak()
	{
		return raak;
	}
	
	public void paint()
	{
		paint(gIm);
	}
	
	public void paint(Context2d g)
	{	//zolang de schuifparameters nog niet werken ook nog even niet tekenen.
		return;
		
		/*
		g.setStrokeStyle(zwart);
		g.setFillStyle(rood);
		
		g.beginPath();
		g.moveTo(linksMarge,  bovenMarge);
		g.lineTo(lengte + linksMarge, bovenMarge);
		g.stroke();
		
		g.beginPath();
		g.arc(linksMarge + stand - 5, bovenMarge, 3, 0, 2 * Math.PI); // even kijken of helemaal goed zo.
		g.closePath();
		g.fill();
		g.stroke();
		
		if(naam.length() > 0)
		{	String fontString = "10px sans-serif";
			g.setFont(fontString);
			TextMetrics tm = g.measureText(naam);
			int naamBreedte = (int) Math.round(tm.getWidth());
			//Font font = new Font("SansSerif", Font.PLAIN, 10);
			//FontMetrics fm = getFontMetrics(font);
			double doubleStand = stand;
			double doubleLengte = lengte;
			double waarde = doubleStand/doubleLengte * (bovenGrens - onderGrens) + onderGrens;
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
		*/
	}
	
	public void update(Context2d gIm)
	{	paint(gIm);
	}
	
	public int geefStand()
	{	return stand;
	}
	
	public void zetStand(int std)
	{	if(std>lengte)stand = lengte;
		else if(std<minimum)stand = minimum;
		else stand = std;
		paint(gIm);
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
