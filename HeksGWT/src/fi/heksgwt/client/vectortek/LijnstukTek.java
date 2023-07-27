package fi.heksgwt.client.vectortek;

//import java.awt.*;
//import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

//import java.io.*;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class LijnstukTek extends TekenObjectTek 
{
	Point[] punten;
	CssColor lijnkleur;
	double[] exactePuntenX, exactePuntenY;

/*	
	public LijnstukTek(DataInputStream inv) 
	{
		punten = new Point[2];
		try 
		{
			int r = inv.readByte() + 128;
			int g = inv.readByte() + 128;
			int b = inv.readByte() + 128;
			lijnkleur = new Color(r, g, b);

			int x = inv.readByte() * 2 + 254;
			int y = inv.readByte() * 2 + 254;
			punten[0] = new Point(x, y);
			x = inv.readByte() * 2 + 254;
			y = inv.readByte() * 2 + 254;
			punten[1] = new Point(x, y);
		} 
		catch (IOException io) 
		{
		}

	}
	
*/	
	public LijnstukTek(Map<String,Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		String lijnkleurString = "rgb(0,0,0)";
		int punt1x = 0;
		int punt1y = 0;
		int punt2x = 0;
		int punt2y = 0;
		
		if (h.containsKey("lijnkleur"))
			lijnkleurString = h.getString("lijnkleur");
		if (h.containsKey("punt1x"))
			punt1x = h.getInt("punt1x");
		if (h.containsKey("punt1y"))
			punt1y = h.getInt("punt1y");
		if (h.containsKey("punt2x"))
			punt2x = h.getInt("punt2x");
		if (h.containsKey("punt2y"))
			punt2y = h.getInt("punt2y");

		lijnkleur = CssColor.make(lijnkleurString);
		
		punten = new Point[2];
		punten[0] = new Point(punt1x, punt1y);
		punten[1] = new Point(punt2x, punt2y);
	}

/*	
	public Hashtable<String,Object> getState()
	{
		Hashtable<String,Object> h = new Hashtable<String,Object>();
		
		h.put("soort", "Lijnstuk");
		h.put("lijnkleur", new String("rgb(" + lijnkleur.getRed()+ "," + lijnkleur.getGreen() + "," + lijnkleur.getBlue() + ")"));
		h.put("punt1x", new Integer(punten[0].x));
		h.put("punt1y", new Integer(punten[0].y));
		h.put("punt2x", new Integer(punten[1].x));
		h.put("punt2y", new Integer(punten[1].y));
		
		return h;
	}
*/	
	
	//public void paint(Graphics gr)
	public void paint(Context2d  gr)
	{
		//Graphics g = (Graphics2D) gr;
		//((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//g.setColor(lijnkleur);
		gr.setStrokeStyle(lijnkleur);
		//g.drawLine(punten[0].x, punten[0].y, punten[1].x, punten[1].y);
		gr.moveTo(punten[0].x, punten[0].y);
		gr.lineTo(punten[1].x, punten[1].y);
		gr.stroke();
		
	}

	public boolean contains(int x, int y) 
	{
		return false;
	}

	public void zetLijnkleur(CssColor c) 
	{
		lijnkleur = c;
	}

	public void verplaats(int dx, int dy) 
	{
		for (int j = 0; j < 2; j++) 
		{
			punten[j].x += dx;
			punten[j].y += dy;

			if (exactePuntenX != null) 
			{
				exactePuntenX[j] += dx;
				exactePuntenY[j] += dy;
			}
		}

	}

	public void schaal(double factorX, double factorY) 
	{
		if (exactePuntenX == null) 
		{
			exactePuntenX = new double[2];
			exactePuntenY = new double[2];
			for (int j = 0; j < 2; j++) {
				exactePuntenX[j] = punten[j].x;
				exactePuntenY[j] = punten[j].y;
			}
		}
		for (int j = 0; j < 2; j++) 
		{
			exactePuntenX[j] = factorX * exactePuntenX[j];
			exactePuntenY[j] = factorY * exactePuntenY[j];
			punten[j].x = ((int) exactePuntenX[j]);
			punten[j].y = ((int) exactePuntenY[j]);
		}
	}

	public void draai(double dh) 
	{
		if (exactePuntenX == null) 
		{
			exactePuntenX = new double[2];
			exactePuntenY = new double[2];
			for (int j = 0; j < 2; j++) {
				exactePuntenX[j] = punten[j].x;
				exactePuntenY[j] = punten[j].y;
			}
		}
		double cos = Math.cos(dh * Math.PI / 180);
		double sin = Math.sin(dh * Math.PI / 180);

		for (int j = 0; j < 2; j++) 
		{
			double x = exactePuntenX[j] - breedte / 2;
			double y = exactePuntenY[j] - hoogte / 2;
			exactePuntenX[j] = cos * x + sin * y + breedte / 2;
			exactePuntenY[j] = cos * y - sin * x + hoogte / 2;
			punten[j].x = ((int) exactePuntenX[j] + 1) / 2 * 2;
			punten[j].y = ((int) exactePuntenY[j] + 1) / 2 * 2;
		}
	}
}
