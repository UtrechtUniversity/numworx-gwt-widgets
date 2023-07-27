package fi.heksgwt.client;

//import java.awt.*;
//import java.applet.*;
//import java.awt.event.*;
import com.google.gwt.canvas.dom.client.Context2d;

import fi.heksgwt.client.scobjects.*;
//import fi.beans.appletutil.*;
import fi.heksgwt.client.vectortek.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;


public class Emmer extends ScContainer //implements ActionListener// ,
																// FocusListener,
																// MouseListener
{
	private Tekening emmertekening;
	//private Applet eigenaar;
	//private AppletUtil au;
	//Object owner;
	HeksGWT owner;
	
	GetalComponent etiket;
	//private ActionListener actionListener;
	private int inhoud;
	
	String naam = "";
	
	boolean visible = true;
	
	public Emmer(int x, int y, int b, int h, HeksGWT owner) 
	{
		super(x, y, b, h);
		this.owner = owner;
		//eigenaar = applet;
		//au = new AppletUtil(eigenaar);
		
		refresh = false;

		emmertekening = new Tekening(xPos+0, yPos+0, b, h, owner.emmerMap);
		add(emmertekening);
		
		etiket = new GetalComponent(xPos + b / 6, yPos + h * 3 / 7, b * 2 / 3, h / 3, owner);
		etiket.transparent = true;
		etiket.zetInstelbaar(true);
//GWT?		
		//etiket.setEnabled(true);
		etiket.zetBekend(false);
		//etiket.addActionListener(this);
		add(etiket);

		zetBegin();

	}

	public void setLocation(int x, int y)
	{
		xPos = x; yPos = y;
		emmertekening.setLocation(x, y);
		etiket.setLocation(x + breedte / 6, y + hoogte * 3 / 7);
	}

	public void zetNaam(String naam)
	{
		this.naam = naam;
		emmertekening.naam = naam;
	}
	public void setVisible(boolean b)
	{
		visible = b;
	}

	public boolean raakt(int x, int y) 
	{
		int lx = 0; //getLocation().x;
		int ly = 0; //getLocation().y;
		boolean raak = emmertekening.contains(x - lx, y - ly);
//System.out.println("emmer raak " + raak);		
		if (raak)
			return true;
		else
			return false;
	}

	public boolean raaktEtiket(int x, int y)
	{
		return etiket.contains(x, y);
	}
	
	
	public int geefInhoud() 
	{
		return inhoud;
	}

	public void zetBegin() 
	{
		etiket.zetInstelbaar(true);
		zetInhoud(0);
		etiket.zetBekend(false);
	}

	public void zetInstelbaar(boolean b) 
	{
		etiket.zetInstelbaar(b);
	}

	public void zetInhoud(int aantal) 
	{
		
//System.out.println(naam + " zetInhoud " + aantal);

		inhoud = aantal;
		etiket.zetWaarde(aantal);
		CssColor vulkleur;
		if (aantal > 0)
			vulkleur = CssColor.make(255,0,0);
		else
			vulkleur = CssColor.make(0, 100, 255);
		if (aantal == 0 || aantal == -999) 
		{
			for (int i = 6; i < 10; i++) 
			{
//if (emmertekening == null)
//System.out.println("emmertek = null");	
				emmertekening.to[i].zetVulkleur(null);
				emmertekening.to[i].zetLijnkleur(null);
			}
		} 
		else if (Math.abs(aantal) == 1) 
		{
			for (int i = 6; i < 10; i++) 
			{
				emmertekening.to[i].zetVulkleur(null);
				emmertekening.to[i].zetLijnkleur(null);
			}
			emmertekening.to[6].zetVulkleur(vulkleur);
			emmertekening.to[6].zetLijnkleur(CssColor.make(0,0,0));
			emmertekening.to[7].zetVulkleur(vulkleur);
			emmertekening.to[7].zetLijnkleur(CssColor.make(0,0,0));
		} else if (Math.abs(aantal) == 2) 
		{
			for (int i = 6; i < 10; i++) 
			{
				emmertekening.to[i].zetVulkleur(null);
				emmertekening.to[i].zetLijnkleur(null);
			}
			emmertekening.to[7].zetVulkleur(vulkleur);
			emmertekening.to[7].zetLijnkleur(CssColor.make(0,0,0));
			emmertekening.to[6].zetVulkleur(vulkleur);
			emmertekening.to[6].zetLijnkleur(CssColor.make(0,0,0));
			emmertekening.to[8].zetVulkleur(vulkleur);
			emmertekening.to[8].zetLijnkleur(CssColor.make(0,0,0));
		} else 
		{
			for (int i = 6; i < 10; i++) 
			{
				emmertekening.to[i].zetVulkleur(vulkleur);
				emmertekening.to[i].zetLijnkleur(CssColor.make(0,0,0));
			}
		}
		//emmertekening.repaint();
		emmertekening.paint(owner.heksGWTContext2d);

	}
	
	public void paint(Context2d g)
	{	if (!visible)
			return;
	
		super.paint(g);
	}	

/*
	public void addActionListener(ActionListener listener) 
	{
		actionListener = AWTEventMulticaster.add(actionListener, listener);
	}
*/
/*	
	public void removeActionListener(ActionListener listener) 
	{
		actionListener = AWTEventMulticaster.remove(actionListener, listener);
	}
*/

//GWT
/*	
	public void actionPerformed(ActionEvent e) 
	{
		if (etiket.isBekend()) 
		{
			zetInstelbaar(false);
			zetInhoud(etiket.geefWaarde());
			if (actionListener != null) 
			{
				actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "" + this));
			}
		}
		if (e.getSource() == etiket)
		{
//System.out.println(naam + " etiket mousePressed");			
		}
		
	}
*/	
}
