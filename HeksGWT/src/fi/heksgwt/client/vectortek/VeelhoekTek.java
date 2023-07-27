package fi.heksgwt.client.vectortek;

//import java.awt.*;
//import java.awt.event.*;

//import java.io.*;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class VeelhoekTek extends TekenObjectTek 
{
	public Polygon basisPolygon;
	CssColor vulkleur;
	CssColor lijnkleur;
	boolean isOmlijnd, isGevuld;
	double[] exactePuntenX, exactePuntenY;

/*	
	public VeelhoekTek(DataInputStream inv) 
	{
		isGevuld = false;
		isOmlijnd = false;
		basisPolygon = new Polygon();
		try 
		{
			int gevuld = inv.readByte();
			if (gevuld != 0) 
			{
				isGevuld = true;
				int r = inv.readByte() + 128;
				int g = inv.readByte() + 128;
				int b = inv.readByte() + 128;
				vulkleur = new Color(r, g, b);
			}
			int omlijnd = inv.readByte();
			if (omlijnd != 0) 
			{
				isOmlijnd = true;
				int r = inv.readByte() + 128;
				int g = inv.readByte() + 128;
				int b = inv.readByte() + 128;
				lijnkleur = new Color(r, g, b);
			}
			int n = inv.readByte();
			for (int j = 0; j < n; j++) {
				int x = inv.readByte() * 2 + 254;
				int y = inv.readByte() * 2 + 254;
				basisPolygon.addPoint(x, y);
			}
		} catch (IOException io) 
		{}
	}
*/
	
	
	public VeelhoekTek(Map<String,Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		String lijnkleurString = "rgb(0,0,0)";
		String vulkleurString = "rgb(255,0,0)";
		 
		int[] puntenx = new int[0];
		int[] punteny = new int[0];
		
		if (h.containsKey("lijnkleur"))
			lijnkleurString = h.getString("lijnkleur");
		if (h.containsKey("vulkleur"))
			vulkleurString = h.getString("vulkleur");

		if (h.containsKey("omlijnd"))
			isOmlijnd = h.getBoolean("omlijnd");
		if (h.containsKey("gevuld"))
			isGevuld = h.getBoolean("gevuld");
				
		if (h.containsKey("puntenx"))
			puntenx = h.getIntArray("puntenx");
		if (h.containsKey("punteny"))
			punteny = h.getIntArray("punteny");

		lijnkleur = CssColor.make(lijnkleurString);
		vulkleur = CssColor.make(vulkleurString);
		
		basisPolygon = new Polygon();
		for (int pCnt = 0; pCnt < puntenx.length; pCnt++)
		{	if (puntenx[pCnt] != 0 && punteny[pCnt] != 0)
				basisPolygon.addPoint(puntenx[pCnt],punteny[pCnt]);
		
		}

//System.out.println("bp numP = " + basisPolygon.geefAantalPunten());
//if (basisPolygon.aantalPunten == 16)
//{
//for (int i = 0; i < basisPolygon.aantalPunten; i++)
//System.out.print("("+basisPolygon.geefPuntX(i)+","+basisPolygon.geefPuntY(i)+")");	
//}
//System.out.println("");
	}
	
/*	
	public Hashtable<String,Object> getState()
	{
		Hashtable<String,Object> h = new Hashtable<String,Object>();
		h.put("soort", "Veelhoek");
		h.put("lijnkleur", new String("rgb(" + lijnkleur.getRed()+ "," + lijnkleur.getGreen() + "," + lijnkleur.getBlue() + ")"));
		h.put("vulkleur", new String("rgb(" + vulkleur.getRed()+ "," + vulkleur.getGreen() + "," + vulkleur.getBlue() + ")"));
		h.put("omlijnd", new Boolean(isOmlijnd));
		h.put("gevuld", new Boolean(isGevuld));
		h.put("puntenx", basisPolygon.xpoints);
		h.put("punteny", basisPolygon.ypoints);
		
		return h;
	}
*/	
	
	//public void paint(Graphics gr)
	public void paint(Context2d gr)
	{
		//Graphics g = (Graphics2D) gr;
		//((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (isGevuld && vulkleur != null) 
		{
			//g.setColor(vulkleur);
			gr.setFillStyle(vulkleur);
			//g.fillPolygon(basisPolygon);
			gr.beginPath();		
			gr.moveTo(exactePuntenX[0], exactePuntenY[0]);
			for (int k = 1; k < basisPolygon.aantalPunten; k++) 
			{	gr.lineTo(exactePuntenX[k], exactePuntenY[k]);
			}
			gr.lineTo(exactePuntenX[0], exactePuntenY[0]);
			gr.closePath();
			gr.fill();

		}

		if (isOmlijnd && lijnkleur != null) 
		{
			//g.setColor(lijnkleur);
			gr.setStrokeStyle(lijnkleur);
			//g.drawPolygon(basisPolygon);
			gr.beginPath();		
			gr.moveTo(exactePuntenX[0], exactePuntenY[0]);
			for (int k = 1; k < basisPolygon.aantalPunten; k++) 
			{	gr.lineTo(exactePuntenX[k], exactePuntenY[k]);
			}
			gr.lineTo(exactePuntenX[0], exactePuntenY[0]);
			gr.closePath();
			gr.stroke();

		}
	}

	public boolean contains(int x, int y) 
	{
		return (basisPolygon.contains(x, y));
	}

	public void zetVulkleur(CssColor c) 
	{
		vulkleur = c;
		isGevuld = true;
	}

	public void zetLijnkleur(CssColor c) 
	{
		lijnkleur = c;
		isOmlijnd = true;
	}

	public void zetPolygon() 
	{
		Polygon p = new Polygon();
		for (int j = 0; j < basisPolygon.aantalPunten; j++) 
		{
			p.addPoint(basisPolygon.puntenX[j], basisPolygon.puntenY[j]);
		}
		basisPolygon = p;
	}

	public void verplaats(int dx, int dy) 
	{
		
		
		for (int j = 0; j < basisPolygon.aantalPunten; j++) 
		{
			basisPolygon.puntenX[j] += dx;
			basisPolygon.puntenY[j] += dy;

			if (exactePuntenX != null) 
			{
				exactePuntenX[j] += dx;
				exactePuntenY[j] += dy;
			}
		}
		zetPolygon();
	}

	public void schaal(double factorX, double factorY) 
	{
		if (exactePuntenX == null) 
		{
			exactePuntenX = new double[basisPolygon.aantalPunten];
			exactePuntenY = new double[basisPolygon.aantalPunten];
			for (int j = 0; j < basisPolygon.aantalPunten; j++) 
			{
				exactePuntenX[j] = basisPolygon.puntenX[j];
				exactePuntenY[j] = basisPolygon.puntenY[j];
			}
		}
		for (int j = 0; j < basisPolygon.aantalPunten; j++) 
		{
			exactePuntenX[j] = factorX * exactePuntenX[j];
			exactePuntenY[j] = factorY * exactePuntenY[j];
			basisPolygon.puntenX[j] = ((int) exactePuntenX[j]);
			basisPolygon.puntenY[j] = ((int) exactePuntenY[j]);
		}
		zetPolygon();
	}

	public void draai(double dh) 
	{
		if (exactePuntenX == null) 
		{
			exactePuntenX = new double[basisPolygon.aantalPunten];
			exactePuntenY = new double[basisPolygon.aantalPunten];
			for (int j = 0; j < basisPolygon.aantalPunten; j++) 
			{
				exactePuntenX[j] = basisPolygon.puntenX[j];
				exactePuntenY[j] = basisPolygon.puntenY[j];
			}
		}
		double cos = Math.cos(dh * Math.PI / 180);
		double sin = Math.sin(dh * Math.PI / 180);

		for (int j = 0; j < basisPolygon.aantalPunten; j++) 
		{
			double x = exactePuntenX[j] - breedte / 2;
			double y = exactePuntenY[j] - hoogte / 2;
			exactePuntenX[j] = cos * x + sin * y + breedte / 2;
			exactePuntenY[j] = cos * y - sin * x + hoogte / 2;
			basisPolygon.puntenX[j] = ((int) exactePuntenX[j] + 1) / 2 * 2;
			basisPolygon.puntenY[j] = ((int) exactePuntenY[j] + 1) / 2 * 2;
		}
		zetPolygon();
	}
}
