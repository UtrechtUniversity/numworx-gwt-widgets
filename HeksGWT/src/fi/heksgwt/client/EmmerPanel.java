package fi.heksgwt.client;

//import java.awt.*;
//import java.applet.*;
//import java.awt.event.*;
import fi.heksgwt.client.scobjects.ScContainer;
//import fi.beans.appletutil.*;
import fi.heksgwt.client.vectortek.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

public class EmmerPanel extends ScContainer //Panel implements ActionListener// ,
																	// FocusListener,
																	// MouseListener
{
	private Tekening emmertekening;
	//private Applet eigenaar;
	//private AppletUtil au;
	//Object owner;
	HeksGWT owner;
	
	private GetalComponent etiket;
	//private ActionListener actionListener;
	private int inhoud;

	boolean visible = true;
	
	public EmmerPanel(int x, int y, int b, int h, HeksGWT owner) 
	{
		super(x, y, b, h);
		this.owner = owner;
		
		//setBackground(Color.white);
		//eigenaar = applet;
		//au = new AppletUtil(eigenaar);

		emmertekening = new Tekening(xPos+0, yPos+0, b, h, owner.emmerKleinMap);
		//zetBegin();
		add(emmertekening);

		// was b/6, b*2/3
		etiket = new GetalComponent(xPos + b/6 + 3, yPos + h * 3 / 7, b / 2, h / 3, owner);
		etiket.transparent = true;
		//etiket.zetInstelbaar(true);
		//etiket.zetBekend(false);
		//etiket.addActionListener(this);
		add(etiket);
		
		zetBegin();
		//zetInhoud(0);

	}

	public void zetP23Klein()
	{
		etiket.p23Klein = true;
	}

	public void zetNaam(String naam)
	{
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
		if (emmertekening.contains(x - lx, y - ly))
			return true;
		else
			return false;
	}

	public int geefInhoud() 
	{
		return inhoud;
	}

	public void zetBegin() 
	{
		//etiket.zetInstelbaar(true);
		zetInhoud(0);
		//etiket.zetBekend(false);
	}

	public void zetInstelbaar(boolean b) 
	{
		etiket.zetInstelbaar(b);
	}

	public void zetInhoud(int aantal) 
	{
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
		} 
		else if (Math.abs(aantal) == 2) 
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
		} 
		else 
		{
			for (int i = 6; i < 10; i++) 
			{
				emmertekening.to[i].zetVulkleur(vulkleur);
				emmertekening.to[i].zetLijnkleur(CssColor.make(0,0,0));
			}
		}
		//emmertekening.repaint();
		emmertekening.paint(owner.heksGWTContext2d);
		//paint(owner.heksGWTContext2d);

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
	}
*/	
}
