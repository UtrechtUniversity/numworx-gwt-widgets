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

public class VulKrommeTek extends TekenObjectTek 
{
	Polygon basisPolygon;
	public Polygon buigPolygon;
	CssColor vulkleur;
	CssColor lijnkleur;
	boolean isOmlijnd, isGevuld;
	double[] exactePuntenX, exactePuntenY;

/*	
	public VulKrommeTek(DataInputStream inv) 
	{
		isGevuld = false;
		isOmlijnd = false;
		basisPolygon = new Polygon();
		try {
			int gevuld = inv.readByte();
			if (gevuld != 0) {
				isGevuld = true;
				int r = inv.readByte() + 128;
				int g = inv.readByte() + 128;
				int b = inv.readByte() + 128;
				vulkleur = new Color(r, g, b);
			}
			int omlijnd = inv.readByte();
			if (omlijnd != 0) {
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
		} 
		catch (IOException io) 
		{
		}
		buigPolygon = buig(basisPolygon);
	}
*/
	public VulKrommeTek(Map<String,Object> map)
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
		
		buigPolygon = buig(basisPolygon);
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

		if (isGevuld && vulkleur != null) {
			//g.setColor(vulkleur);
			gr.setFillStyle(vulkleur);
			//g.fillPolygon(buigPolygon);
			gr.beginPath();		
			gr.moveTo(buigPolygon.puntenX[0], buigPolygon.puntenY[0]);
			for (int k = 1; k < buigPolygon.aantalPunten; k++) 
			{	gr.lineTo(buigPolygon.puntenX[k], buigPolygon.puntenY[k]);
			}
			gr.lineTo(buigPolygon.puntenX[0], buigPolygon.puntenY[0]);
			gr.closePath();
			gr.fill();
			
			
		}

		if (isOmlijnd && lijnkleur != null) {
			//g.setColor(lijnkleur);
			gr.setStrokeStyle(lijnkleur);
			//g.drawPolygon(buigPolygon);
			gr.beginPath();		
			gr.moveTo(buigPolygon.puntenX[0], buigPolygon.puntenY[0]);
			for (int k = 1; k < buigPolygon.aantalPunten; k++) 
			{	gr.lineTo(buigPolygon.puntenX[k], buigPolygon.puntenY[k]);
			}
			gr.lineTo(buigPolygon.puntenX[0], buigPolygon.puntenY[0]);
			gr.closePath();
			gr.stroke();

		}
	}

	public boolean contains(int x, int y) {
		return (buigPolygon.contains(x, y));
	}

	public void zetVulkleur(CssColor c) 
	{
		vulkleur = c;
		isGevuld = true;
	}

	public void zetLijnkleur(CssColor c) {
		lijnkleur = c;
		isOmlijnd = true;
	}

	public Polygon buig(Polygon pol) 
	{
		Polygon pNieuw = new Polygon();
		int aantalP = pol.aantalPunten;
		int aantalPW;
		boolean klaar = false;
		double[] puntenX = new double[aantalP];
		double[] puntenY = new double[aantalP];
		for (int i = 0; i < pol.aantalPunten; i++) 
		{
			puntenX[i] = pol.puntenX[i];
			puntenY[i] = pol.puntenY[i];
		}

		for (int f = 0; f < 10; f++) {
			while (!klaar) {
				aantalPW = 0;
				double[] puntenXW = new double[2 * aantalP];
				double[] puntenYW = new double[2 * aantalP];

				for (int i = 0; i < aantalP; i++) {
					double x0 = puntenX[i];
					double y0 = puntenY[i];
					double x1 = puntenX[(i + 1) % aantalP];
					double y1 = puntenY[(i + 1) % aantalP];
					double x2 = puntenX[(i + 2) % aantalP];
					double y2 = puntenY[(i + 2) % aantalP];
					double x3 = puntenX[(i + 3) % aantalP];
					double y3 = puntenY[(i + 3) % aantalP];

					double xv0 = x1 - x0;
					double yv0 = y1 - y0;
					double xv1 = x2 - x1;
					double yv1 = y2 - y1;
					double xv2 = x3 - x2;
					double yv2 = y3 - y2;

					double uitprodukt1 = xv0 * yv1 - xv1 * yv0;
					double orientatie1;
					if (uitprodukt1 == 0)
						orientatie1 = 0;
					else
						orientatie1 = uitprodukt1 / Math.abs(uitprodukt1);
					double improdukt1 = xv0 * xv1 + yv0 * yv1;
					double norm0 = Math.sqrt((xv0) * (xv0) + (yv0) * (yv0));
					double norm1 = Math.sqrt((xv1) * (xv1) + (yv1) * (yv1));
					double hoekA = 0;
					if (norm0 * norm1 > 0.000010 && Math.abs(improdukt1 / (norm0 * norm1)) < 1)
						hoekA = orientatie1 * Math.acos(improdukt1 / (norm0 * norm1));
					else if (norm0 * norm1 > 0.000010 && improdukt1 / (norm0 * norm1) > 1)
						hoekA = 0;
					else if (norm0 * norm1 > 0.000010 && improdukt1 / (norm0 * norm1) < -1)
						hoekA = Math.PI;
					double uitprodukt2 = xv1 * yv2 - xv2 * yv1;
					double orientatie2;
					if (uitprodukt2 == 0)
						orientatie2 = 0;
					else
						orientatie2 = uitprodukt2 / Math.abs(uitprodukt2);
					double improdukt2 = xv1 * xv2 + yv1 * yv2;
					double norm2 = Math.sqrt((xv2) * (xv2) + (yv2) * (yv2));
					double hoekB = 0;
					if (norm1 * norm2 > 0.000010 && Math.abs(improdukt2 / (norm1 * norm2)) <= 1)
						hoekB = orientatie2 * Math.acos(improdukt2 / (norm1 * norm2));
					else if (norm1 * norm2 > 0.000010 && improdukt2 / (norm1 * norm2) > 1)
						hoekB = 0;
					else if (norm1 * norm2 > 0.000010 && improdukt2 / (norm1 * norm2) < -1)
						hoekB = Math.PI;
					double d = 0.5 * Math.tan((hoekA + hoekB) / 8);
					double xn = (0.5 * (x1 + x2) + yv1 * d);
					double yn = (0.5 * (y1 + y2) - xv1 * d);

					puntenXW[aantalPW] = x1;
					puntenYW[aantalPW] = y1;
					aantalPW++;
					if (Math.sqrt((x1 - xn) * (x1 - xn) + (y1 - yn) * (y1 - yn)) > 2 && Math.sqrt((x2 - xn) * (x2 - xn) + (y2 - yn) * (y2 - yn)) > 2
							&& Math.abs(d * norm1) > 0.5) {
						puntenXW[aantalPW] = xn;
						puntenYW[aantalPW] = yn;
						aantalPW++;
					}
				}
				if (aantalPW > aantalP) {
					puntenX = new double[aantalPW];
					puntenY = new double[aantalPW];

					for (int i = 0; i < aantalPW; i++) {
						puntenX[i] = puntenXW[i];
						puntenY[i] = puntenYW[i];
					}
					aantalP = aantalPW;
				} else
					klaar = true;
			}
		}

		for (int i = 0; i < aantalP; i++) {
			pNieuw.addPoint((int) puntenX[i], (int) puntenY[i]);
		}
		return pNieuw;
	}

	public void zetPolygon() 
	{
		Polygon p = new Polygon();
		for (int j = 0; j < basisPolygon.aantalPunten; j++) 
		{
			p.addPoint(basisPolygon.puntenX[j], basisPolygon.puntenY[j]);
		}
		basisPolygon = p;
		buigPolygon = buig(basisPolygon);
	}

	public void verplaats(int dx, int dy) 
	{
		for (int j = 0; j < basisPolygon.aantalPunten; j++) 
		{
			//buigPolygon.puntenX[j] += dx;
			//buigPolygon.puntenY[j] += dy;
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

	public void schaal(double factor) {
		schaal(factor, factor);
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
		buigPolygon = buig(basisPolygon);
	}

	public void draai(double dh) {
		if (exactePuntenX == null) {
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
			basisPolygon.puntenX[j] = ((int) exactePuntenX[j]);
			basisPolygon.puntenY[j] = ((int) exactePuntenY[j]);
		}
		buigPolygon = buig(basisPolygon);

	}
}
