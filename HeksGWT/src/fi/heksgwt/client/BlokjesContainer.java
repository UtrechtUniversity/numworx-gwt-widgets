package fi.heksgwt.client;

//import java.awt.*;

import com.google.gwt.canvas.dom.client.CssColor;

import fi.heksgwt.client.scobjects.*;
import fi.heksgwt.client.vectortek.*;

public class BlokjesContainer extends ScContainer 
{
	private boolean veranderd;
	private boolean alsGetal;

	HeksGWT owner;
	
	private int aantalBlokjes;
	private Tekening[] blokjes;
	private boolean[] soorten;
	Tekening plusblokje, minblokje;
	GetalComponent getalPlus, getalMin;

	private int maxRijen = 4;
	
	public BlokjesContainer(int x, int y, int b, int h, HeksGWT owner) 
	{
		super(x, y, b, h);
		this.owner = owner;
		
		aantalBlokjes = 0;
		blokjes = new Tekening[20];
		soorten = new boolean[500];

		plusblokje = new Tekening(0, 0, 40, 40, owner.blokjePlusMap);
		plusblokje.setVisible(false);
		plusblokje.verplaats(xPos,yPos);
		add(plusblokje);

		minblokje = new Tekening(80, 0, 40, 40, owner.blokjeMinMap);
		minblokje.setVisible(false);
		minblokje.verplaats(xPos,yPos);
		add(minblokje);

		getalPlus = new GetalComponent(xPos+5, yPos+35, 30, 25, owner);
		getalPlus.setVisible(false);
		getalPlus.zetWaarde(0);
		add(getalPlus);

		getalMin = new GetalComponent(xPos+55, yPos+35, 30, 25, owner);
		getalMin.setVisible(false);
		getalMin.zetWaarde(0);
		add(getalMin);

		veranderd = true;
		alsGetal = false;
	}

	public void zetMaxRijen(int maxR) 
	{
		maxRijen = maxR;
	}

	public void toonAlsGetal(boolean bool) 
	{
		alsGetal = bool;
	}

	public void voegBlokjeToe(boolean bool) 
	{
		int x = (int) ((aantalBlokjes % 5) * (relb / 23e-1d));
		int y = (int) (aantalBlokjes / 5 * (relb / 23e-1d));
		int b = (int) (20.0 / 110 * relb);
		int h = (int) (20.0 / 110 * relb);

		if (bool) 
		{
			getalPlus.verhoog();
//System.out.println("test bool " + getalPlus.geefWaarde());
			if (aantalBlokjes < 5 * maxRijen && !alsGetal) 
			{
				blokjes[aantalBlokjes] = new Tekening(x, y, b, h, owner.blokjePlusMap);
				blokjes[aantalBlokjes].schaal(schaal);
				blokjes[aantalBlokjes].verplaats(xPos, yPos);
				add(blokjes[aantalBlokjes]);
			} 
			else if (aantalBlokjes == 5 * maxRijen) 
			{
				plusblokje.setVisible(true);
				getalPlus.setVisible(true);
				minblokje.setVisible(true);
				getalMin.setVisible(true);
				for (int i = 0; i < 5 * maxRijen; i++) 
				{
					blokjes[i].setVisible(false);
				}
			}
			if (alsGetal && getalPlus.geefWaarde() == 1) 
			{
				plusblokje.setVisible(true);
				getalPlus.setVisible(true);
			}
		} 
		else 
		{
			getalMin.verhoog();
//System.out.println("test !bool");			
			if (aantalBlokjes < 5 * maxRijen && !alsGetal) 
			{
				blokjes[aantalBlokjes] = new Tekening(x, y, b, h, owner.blokjeMinMap);
				blokjes[aantalBlokjes].schaal(schaal);
				blokjes[aantalBlokjes].verplaats(xPos, yPos);
				add(blokjes[aantalBlokjes]);
			} 
			else if (aantalBlokjes == 5 * maxRijen) 
			{
				plusblokje.setVisible(true);
				getalPlus.setVisible(true);
				minblokje.setVisible(true);
				getalMin.setVisible(true);
				for (int i = 0; i < 5 * maxRijen; i++) {
					blokjes[i].setVisible(false);
				}
			}
			if (alsGetal && getalMin.geefWaarde() == 1) {
				minblokje.setVisible(true);
				getalMin.setVisible(true);
			}
		}
		soorten[aantalBlokjes] = bool;
		if (aantalBlokjes < 500)
			aantalBlokjes++;
		veranderd = true;
		paint();
	}

	public void paint()
	{
		owner.paint();
		
	}
	
	public void verwijderBlokje() 
	{
		aantalBlokjes--;
		if (soorten[aantalBlokjes])
			getalPlus.verlaag();
		else
			getalMin.verlaag();
		if (aantalBlokjes == 5 * maxRijen && !alsGetal) 
		{
			plusblokje.setVisible(false);
			getalPlus.setVisible(false);
			minblokje.setVisible(false);
			getalMin.setVisible(false);
			for (int i = 0; i < 5 * maxRijen; i++) 
			{
				blokjes[i].setVisible(true);
			}
		} 
		else if (aantalBlokjes < 5 * maxRijen) 
		{
			if (blokjes[aantalBlokjes] != null)
				remove(blokjes[aantalBlokjes]);
		}
		if (alsGetal && getalMin.geefWaarde() == 0) 
		{
			minblokje.setVisible(false);
			getalMin.setVisible(false);
		}
		if (alsGetal && getalPlus.geefWaarde() == 0) 
		{
			plusblokje.setVisible(false);
			getalPlus.setVisible(false);
		}
		veranderd = true;
		paint();
	}

	public void removeAll() 
	{
		int n = aantalBlokjes;
		for (int i = 0; i < n; i++) 
		{
			verwijderBlokje();
		}
		veranderd = true;
	}

}
