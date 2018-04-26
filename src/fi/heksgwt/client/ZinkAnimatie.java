package fi.heksgwt.client;

//import java.awt.*;
import fi.heksgwt.client.scobjects.ScContainer;
import fi.heksgwt.client.vectortek.Tekening;
//import java.applet.*;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.user.client.Timer;
import com.google.gwt.animation.client.Animation;

public class ZinkAnimatie extends ScContainer// implements Runnable 
{
	HeksGWT owner;
	ScContainer pagina;
		
	private Tekening plusBlokje, minBlokje, blokje;
	private int blx, bly;
	//private Thread zinkAnimatie;
	//Timer zinkAnimatie;
	Animation zinkAnimatie;
	
	//private BellenDraad bellenAnimatie;
	Timer bellenAnimatie;
	private boolean klaar = true;
	private int[] belx, bely;
	private double[] beld;
	private int aantalBellen;
	private boolean bellenAan;
	
	boolean plusZink = false;
	
	private final int DURATION = 1500;
	
	double fraction = 85e-2d;

	public ZinkAnimatie(int x, int y, int b, int h, HeksGWT owner, ScContainer pagina) 
	{
		super(x, y, b, h);
		this.owner = owner;
		this.pagina = pagina;
		
		if (pagina instanceof Pagina24Panel)
			plusBlokje = new Tekening(xPos+0, yPos+200, 60 * h / 200, 60 * h / 200, owner.blokjePlus24Map);
		else
			plusBlokje = new Tekening(xPos+0, yPos+200, 60 * h / 200, 60 * h / 200, owner.blokjePlusMap);
		add(plusBlokje);
		plusBlokje.setVisible(false);
		
		if (pagina instanceof Pagina24Panel)
			minBlokje = new Tekening(xPos+0, yPos+200, 60 * h / 200, 60 * h / 200, owner.blokjeMin24Map);
		else
			minBlokje = new Tekening(xPos+0, yPos+200, 60 * h / 200, 60 * h / 200, owner.blokjeMinMap);
		add(minBlokje);
		minBlokje.setVisible(false);
		
		aantalBellen = 50;
		belx = new int[aantalBellen];
		bely = new int[aantalBellen];
		beld = new double[aantalBellen];
		for (int i = 0; i < aantalBellen; i++) 
		{
			belx[i] = (int) (schaal * (10 + (relb - 20) * Math.random()));
			bely[i] = (int) (schaal * (20 + (relh - 20) * Math.random()));
			beld[i] = schaal * 2;
		}
		bellenAan = true;
	}

	public void start() 
	{
		bellenAan = true;
		startBellen();
	}

	public void stop() 
	{
		bellenAan = false;
		if (bellenAnimatie != null)
			bellenAnimatie.cancel();
			
	}

	public void stopZinkAnimatie()
	{
		if (zinkAnimatie != null)
			zinkAnimatie.cancel();
	}
	//public void paint(Graphics g)
	public void paint(Context2d g)
	{
		
//System.out.println("za paint");		
		
		//super.paint(g);
		
		//g.setColor(new Color(255, 0, 255));
//g.setStrokeStyle(CssColor.make(255, 255, 0));
//g.strokeRect(xPos, yPos, breedte, hoogte);
		
		g.setStrokeStyle(CssColor.make(255, 0, 255));
		
		for (int i = 0; i < aantalBellen; i++) 
		{
//GWT			
			//g.drawOval(belx[i], bely[i], (int) beld[i], (int) beld[i]);
		}
		
	}

	public void zetBellenAan(boolean b) 
	{
		bellenAan = b;
	}

	public void pauze(int millisec) 
	{
	}

	public void start(boolean plus, int startx, int starty) 
	{
		if (startx < 0)
			blx = xPos+0;
		else if (startx > xPos + breedte - plusBlokje.getSize().x)
			blx = xPos + breedte - plusBlokje.getSize().x;
		else
			blx = xPos+startx;
		bly = starty;
		plusZink = plus;
		if (plus) 
		{
			//blokje = plusBlokje;
			plusBlokje.setVisible(true);
			minBlokje.setVisible(false);
			//minBlokje.setLocation(xPos+0, yPos+200);
			plusBlokje.setLocation(blx, bly);
		} 
		else 
		{
			//blokje = minBlokje;
			minBlokje.setVisible(true);
			plusBlokje.setVisible(false);
			//plusBlokje.setLocation(xPos+0, yPos+200);
			minBlokje.setLocation(blx, bly);
			
		}
		//blokje.setLocation(blx, bly);
		
		if (klaar) 
		{
			klaar = false;

			zinkAnimatie = new ZinkDraad(starty);
			//zinkAnimatie.scheduleRepeating(1000);
			zinkAnimatie.run(DURATION);
 		}
	}

	public void startBellen() 
	{
		//bellenAnimatie = new BellenDraad();
		//bellenAnimatie.start();
		//bellenAnimatie.scheduleRepeating(10);
		
	}
	
	class ZinkDraad extends Animation
	{
		public int blyStart, blyEnd;
		public int progCnt = 0;
		public double progMin = 0;
		
		public ZinkDraad(int starty)
		{
			blyStart = Math.max(starty,yPos);
			blyEnd = yPos+hoogte;
			
		}
		
		protected void onUpdate(double progress)
		{
			if (progCnt == 0)
			{   progCnt++;
				return;
			}
			progCnt++;
			if (progCnt == 2)
				progMin = progress; 
			//if (progCnt > 0)
//System.out.println("progress " + UF.format(progress,2));

			//int newbly = blyStart + (int) Math.round((blyEnd - blyStart) * (progress));
			//int newbly = blyStart + (int) Math.round((blyEnd - blyStart) * (85e-2));
			int newbly = blyStart + (int) Math.round((blyEnd - blyStart) * fraction);
			
			if (plusZink)
			{	plusBlokje.setLocation(blx, newbly);
				if ((progress > progMin) && (progress < 1))
					plusBlokje.paint(owner.heksGWTContext2d);
			}
			else
			{	minBlokje.setLocation(blx, newbly);
				if ((progress > progMin) && (progress < 1))
					minBlokje.paint(owner.heksGWTContext2d);
			}
			//if ((progress > 74e-2) && (progress < 76e-2))
			//{	pagina.paint(owner.heksGWTContext2d);
//System.out.println("paint page");			
			//}

		}
		
		protected void onComplete()
		{
			klaar = true;
			pagina.paint(owner.heksGWTContext2d);
		}
		
/*		
		public void run() 
		{
			//int breedte = getSize().width;
			//int hoogte = getSize().height;
	
			while (bly < yPos+hoogte) 
			{
				
System.out.println("zd run while " + bly);				
				//try 
				//{
				//	Thread.sleep(10);
				//} catch (InterruptedException e) // geen ;
				//{
				//}
				//;
				
				//blokje.setLocation(blx, bly);

				bly += 20;
				if (plusZink)
				{	plusBlokje.verplaats(0, 20);
					//plusBlokje.paint(owner.heksGWTContext2d);
				}
				else
				{	minBlokje.verplaats(0, 20);
					//minBlokje.paint(owner.heksGWTContext2d);
				}
			
				pagina.paint(owner.heksGWTContext2d);
				//blokje.paint(owner.heksGWTContext2d);
			}
			//pagina.paint(owner.heksGWTContext2d);
			klaar = true;
			stopZinkAnimatie();
	
		}
*/		
	}
/*
	class BellenDraad extends Timer 
	{
		public void run() 
		{
			while (bellenAan) 
			{
				for (int i = 0; i < aantalBellen; i++) 
				{
					bely[i] -= 2;
					beld[i] += 0.1;
					if (bely[i] < 0) {
						belx[i] = (int) (schaal * relb * Math.random());
						bely[i] = (int) (schaal * relh * Math.random());
						beld[i] = 2;
					}
				}
				
				paint(owner.heksGWTContext2d);
			}
		}
	}
*/	

}
