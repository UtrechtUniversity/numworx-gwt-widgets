package fi.heksgwt.client;

//import java.awt.*;
import fi.heksgwt.client.scobjects.*;
//import fi.beans.appletutil.*;
import fi.heksgwt.client.vectortek.*;
//import java.applet.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.animation.client.Animation;

public class ZinkAnimatieEmmer extends ScContainer //implements Runnable 
{
	//private Applet eigenaar;
	//private AppletUtil au;
	HeksGWT owner; 
	ScContainer pagina;
	
	private Emmer emmer;
	private int blx, bly;
	private Animation zinkAnimatie;
//GWT	
	//private BellenDraad bellenAnimatie;
	private boolean klaar = true;
	private int[] belx, bely;
	private double[] beld;
	private int aantalBellen;
	private boolean bellenAan;
	
	private final int DURATION = 1500;
	double fraction = 65e-2d;

	public ZinkAnimatieEmmer(int x, int y, int b, int h, HeksGWT owner, ScContainer pagina) 
	{
		super(x, y, b, h);
		this.owner = owner;
		//eigenaar = hk;
		//au = new AppletUtil(eigenaar);
		this.pagina = pagina;

		emmer = new Emmer(0, 200, 110 * h / 200, 125 * h / 200, owner);
		emmer.zetInstelbaar(false);
		emmer.setVisible(false);
		emmer.etiket.p23Emmer = true;
		add(emmer);
emmer.zetNaam("anim");


		aantalBellen = 50;
		belx = new int[aantalBellen];
		bely = new int[aantalBellen];
		beld = new double[aantalBellen];
		for (int i = 0; i < aantalBellen; i++) {
			belx[i] = (int) (schaal * (10 + (relb - 20) * Math.random()));
			bely[i] = (int) (schaal * (20 + (relh - 20) * Math.random()));
			beld[i] = schaal * 2;
		}
		bellenAan = true;
		// bellenAnimatie = new BellenDraad();
		// bellenAnimatie.start();
	}

	//public void paint(Graphics g)
	public void paint(Context2d g)
	{
		//super.paint(g);
		//g.setColor(new Color(255, 0, 255));
		g.setStrokeStyle(CssColor.make(255, 0, 255));
		for (int i = 0; i < aantalBellen; i++) 
		{
//GWT			
			//g.drawOval(belx[i], bely[i], (int) beld[i], (int) beld[i]);
		}
	}

	public void start() 
	{
		bellenAan = true;
		//startBellen();
	}

	public void stop() 
	{
		bellenAan = false;
	}

	public void zetInhoud(int aantal) 
	{
		emmer.zetInhoud(aantal);
	}

	public void zetBellenAan(boolean b) 
	{
		bellenAan = b;
	}

/*	
	public void run() {
		int breedte = getSize().width;
		int hoogte = getSize().height;
		while (bly < hoogte) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) // geen ;
			{
			}
			;
			emmer.setLocation(blx, bly);
			bly += 2;
			repaint();
		}
		klaar = true;
	}
*/
	
/*	
	public void pauze(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException e) // geen ;
		{
		}
	}
*/
	public void start(int startx, int starty) 
	{
		
		if (startx < 0)
			blx = xPos+0;
		else if (startx > xPos + breedte - emmer.breedte) //getSize().width)
			blx = xPos + breedte - emmer.breedte; //getSize().width;
		else
			blx = xPos+startx;
		bly = starty;
		emmer.setLocation(blx, bly);
		if (klaar) 
		{
			klaar = false;
			zinkAnimatie = new ZinkDraad(starty);
			zinkAnimatie.run(DURATION);
		}
		
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
//			if (progCnt > 0)
//System.out.println("progress " + UF.format(progress,2));

			//int newbly = blyStart + (int) Math.round((blyEnd - blyStart) * (progress));
			int newbly = blyStart + (int) Math.round((blyEnd - blyStart) * (65e-2));
			
			emmer.setLocation(blx, newbly);
			
			if ((progress > progMin) && (progress < 1))
			{	emmer.setVisible(true);
				emmer.paint(owner.heksGWTContext2d);
			
			}

		}
		
		protected void onComplete()
		{
			klaar = true;
			emmer.setVisible(false);
			pagina.paint(owner.heksGWTContext2d);
		}

	}		
	public void startBellen() 
	{
//GWT		
		//bellenAnimatie = new BellenDraad();
		//bellenAnimatie.start();
	}
/*
	class BellenDraad extends Thread 
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
				try 
				{
					Thread.sleep(10);
				} catch (InterruptedException e) // geen ;
				{
				}
				;
				repaint();
			}
		}
	}
*/
}
