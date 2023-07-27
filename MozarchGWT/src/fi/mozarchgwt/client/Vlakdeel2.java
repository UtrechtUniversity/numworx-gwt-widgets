package fi.mozarchgwt.client;

import com.google.gwt.canvas.dom.client.CssColor;
//import java.awt.Dimension;
//import java.awt.Polygon;
//import java.awt.Color;
//import java.applet.Applet;

public class Vlakdeel2
{	
	public int aantalPunten;
	public int aantalHoekpunten;
	public int aantalPuntenPerZijde;
	public HoekpuntMoz[] hoekpunten;
	public Punt sleeppunt, draaipunt;
	public double orientatie;
	public Polygon tekenvlak;
	//public Color kleur;
	public CssColor kleur;
	public int beginnummer;
	public int aantalHoekpuntenVast;
	public TekenPanel eigenaar;
	public boolean nieuw;
	
	public double positiex, positiey;
	//public boolean tekenbaar;
	
	//boolean isHeap = false;
	
	public int fractielType = 0;
	
	public Vlakdeel2(TekenPanel ap, int aantal, double positiex, double positiey, CssColor kl)
	{	
		this.positiex = positiex;
		this.positiey = positiey;
		
		nieuw = true;
		aantalPunten = aantal;
		hoekpunten = new HoekpuntMoz[aantal + 1];
		draaipunt = new Punt(positiex, positiey);
		sleeppunt = new Punt(0, 0);
		tekenvlak = new Polygon();
		kleur = kl;
		eigenaar = ap;
		beginnummer = 0;
		aantalHoekpuntenVast = 0;
		orientatie = 0;
		//tekenbaar = false;
		
//System.out.println("hoekpunten = " + hoekpunten.length);		
	}
	
	public int getHeight()
	{
		//double xMin = 1000;
		//double xMax = 0;
		double yMin = 1000;
		double yMax = 0;
		for (int hCnt = 0; hCnt < hoekpunten.length; hCnt++)
		{
			//if (hoekpunten[hCnt].x < xMin)
			//	xMin = hoekpunten[hCnt].x;
			//if (hoekpunten[hCnt].x > xMax)
			//	xMax = hoekpunten[hCnt].x;
			if (hoekpunten[hCnt].y < yMin)
				yMin = hoekpunten[hCnt].y;
			if (hoekpunten[hCnt].y > yMax)
				yMax = hoekpunten[hCnt].y;
		}

		return (int) Math.round(yMax - yMin);
		
		
	}
	
	
	public void klikVast(int hoekn, int vlakdeeln, int hoeknVlakdeel)
	{	beginnummer = hoekn;
		if (hoekpunten[hoekn].aantalVastgeklikt == 0)
			aantalHoekpuntenVast++;
		hoekpunten[hoekn].maakVast(vlakdeeln, hoeknVlakdeel);
		draaipunt = new Punt(hoekpunten[hoekn].tekenpunt.x - 0.5 * eigenaar.breedte, //getSize().width,
				            -hoekpunten[hoekn].tekenpunt.y + 0.5 * eigenaar.hoogte); //getSize().height);
  	}
	
	public void klikLos(int hoekn, int vlakdeeln, int hoeknVlakdeel)
	{	hoekpunten[hoekn].maakLos(vlakdeeln, hoeknVlakdeel);
		if (!hoekpunten[hoekn].vast)
		{	aantalHoekpuntenVast-- ;
		}
		if (aantalHoekpuntenVast == 0)
		{	beginnummer = 0;
			draaipunt = new Punt(hoekpunten[0].tekenpunt.x - 0.5 * eigenaar.breedte, //getSize().width,
					            -hoekpunten[0].tekenpunt.y + 0.5 * eigenaar.hoogte); //getSize().height);
		}
	}
	
	public void klikVastDraai(int hoekn, int vlakdeeln, int hoeknVlakdeel)
	{	if (hoekpunten[hoekn].aantalVastgeklikt == 0)
			aantalHoekpuntenVast++;
		hoekpunten[hoekn].maakVast(vlakdeeln, hoeknVlakdeel);
	}
	
	public void klikAllesLos()
	{	beginnummer = 0;
		aantalHoekpuntenVast = 0;
		draaipunt = new Punt(hoekpunten[0].tekenpunt.x - 0.5 * eigenaar.breedte, //getSize().width,
				            -hoekpunten[0].tekenpunt.y + 0.5 * eigenaar.hoogte); //getSize().height);
		for (int i = 1; i < hoekpunten.length - 1; i++)
		{	hoekpunten[i].aantalVastgeklikt = 0;
		}
	}
}

/*
class HoekpuntMoz implements Serializable
{
	public Punt tekenpunt;
	public double x, y;
	public int aantalVastgeklikt;
	public int[] vlakdeelnummers;
	public int[] hoeknummersVlakdeel;
	public boolean vast;
	
	public HoekpuntMoz(double x, double y)
	{	this.x = x;
		this.y = y;
		aantalVastgeklikt = 0;
		vlakdeelnummers = new int[30];
		hoeknummersVlakdeel = new int[30];
		vast = false;
	}
	
	void maakVast(int vlakdeeln, int hoeknVlakdeel)
	{	if (!zitVast(vlakdeeln, hoeknVlakdeel))
		{	vlakdeelnummers[aantalVastgeklikt] = vlakdeeln;
			hoeknummersVlakdeel[aantalVastgeklikt] = hoeknVlakdeel;
			aantalVastgeklikt++;
			vast = true;
		}
	}
	
	void maakLos(int vlakdeeln, int hoeknVlakdeel)
	{	for (int i = 0; i < aantalVastgeklikt; i++)
		{	if ((vlakdeelnummers[i] == vlakdeeln) && (hoeknummersVlakdeel[i] == hoeknVlakdeel))
			{	vlakdeelnummers[i] = vlakdeelnummers[aantalVastgeklikt - 1];
				hoeknummersVlakdeel[i] = hoeknummersVlakdeel[aantalVastgeklikt - 1];
				aantalVastgeklikt--;
			}
		}
		if (aantalVastgeklikt < 1)
			vast = false;
	}
	
	boolean zitVast(int vlakdeeln, int hoeknVlakdeel)
	{	boolean b = false;
		for (int i = 0; i < aantalVastgeklikt; i++)
		{	if (vlakdeelnummers[i] == vlakdeeln && hoeknummersVlakdeel[i] == hoeknVlakdeel)
			b = true;
		}
		return b;
	}
}
*/