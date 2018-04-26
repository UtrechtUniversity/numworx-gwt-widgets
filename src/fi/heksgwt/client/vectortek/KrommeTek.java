package fi.heksgwt.client.vectortek;

//import java.awt.*;
//import java.awt.event.*;

import java.util.HashMap;
import java.util.Map;

//import java.io.*;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;

import java.util.*;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;


public class KrommeTek extends TekenObjectTek 
{
	int aantalBPunten;
	int aantalTPunten;
	Point[] basispunten;
	Point[] tekenpunten;
	CssColor lijnkleur;
	double[] exactePuntenX, exactePuntenY;

/*	
	public KrommeTek(DataInputStream inv) 
	{
		try 
		{
			int r = inv.readByte() + 128;
			int g = inv.readByte() + 128;
			int b = inv.readByte() + 128;
			lijnkleur = new Color(r, g, b);

			aantalBPunten = inv.readByte();
			basispunten = new Point[aantalBPunten];
			for (int j = 0; j < aantalBPunten; j++) {
				int x = inv.readByte() * 2 + 254;
				int y = inv.readByte() * 2 + 254;
				basispunten[j] = new Point(x, y);
			}
		} 
		catch (IOException io) 
		{
		}
		tekenpunten = buig(basispunten);
		aantalTPunten = tekenpunten.length;

	}
*/
	
	public KrommeTek(Map<String,Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		String lijnkleurString = "rgb(0,0,0)";
		int[] puntenx = new int[0];
		int[] punteny = new int[0];

		if (h.containsKey("lijnkleur"))
			lijnkleurString = h.getString("lijnkleur");
		if (h.containsKey("puntenx"))
			puntenx = h.getIntArray("puntenx");
		if (h.containsKey("punteny"))
			punteny = h.getIntArray("punteny");
	
		lijnkleur = CssColor.make(lijnkleurString);
		
		basispunten = new Point[puntenx.length];
		for (int pCnt = 0; pCnt < puntenx.length; pCnt++)
			basispunten[pCnt] = new Point(puntenx[pCnt],punteny[pCnt]);

		tekenpunten = buig(basispunten);
		aantalTPunten = tekenpunten.length;

	}
	
/*
	public HashMap<String,Object> getState()
	{
		HashMap<String,Object> h = new HashMap<String,Object>(); 
		h.put("soort", "Kromme");
		h.put("lijnkleur", new String("rgb(" + lijnkleur.getRed()+ "," + lijnkleur.getGreen() + "," + lijnkleur.getBlue() + ")"));
		int[] puntenX = new int[aantalBPunten];
		int[] puntenY = new int[aantalBPunten];
		for (int i = 0; i < aantalBPunten; i++)
		{	puntenX[i] = basispunten[i].x;
			puntenY[i] = basispunten[i].y;
		}
		h.put("puntenx", puntenX);
		h.put("punteny", puntenY);
		return h;
	}
*/
	
	//public void paint(Graphics gr)
	public void paint(Context2d gr)
	{
		//Graphics g = (Graphics2D) gr;
		//((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//g.setColor(lijnkleur);
		gr.setStrokeStyle(lijnkleur);
		gr.beginPath();
		gr.moveTo(tekenpunten[0].x, tekenpunten[0].y);
		for (int i = 1; i < aantalTPunten; i++) 
		{
			//g.drawLine(tekenpunten[i].x, tekenpunten[i].y, tekenpunten[i + 1].x, tekenpunten[i + 1].y);
			gr.lineTo(tekenpunten[i].x, tekenpunten[i].y);
		}
		gr.stroke();
	}

	public boolean opLijn(int x, int y, int x0, int y0, int x1, int y1) {
		boolean bool = false;
		int a = y1 - y0;
		int b = x1 - x0;
		int c = x0 * y1 - x1 * y0;
		int d = 6 * Math.max(Math.abs(a), Math.abs(b));
		int xmax = Math.max(x0, x1);
		int xmin = Math.min(x0, x1);
		int ymax = Math.max(y0, y1);
		int ymin = Math.min(y0, y1);
		if (Math.abs(a * x - b * y - c) < d && x > xmin && y > ymin && x < xmax && y < ymax)
			bool = true;
		return bool;
	}

	public boolean contains(int x, int y) {
		for (int i = 0; i < aantalTPunten - 1; i++) {
			if (opLijn(x, y, tekenpunten[i].x, tekenpunten[i].y, tekenpunten[i + 1].x, tekenpunten[i + 1].y))
				return true;
		}
		return false;
	}

	public void zetLijnkleur(CssColor c) {
		lijnkleur = c;

	}

	public Point[] buig(Point[] pnt) 
	{

		int aantalP = pnt.length;
		int aantalPW;
		boolean klaar = false;
		double[] puntenX = new double[aantalP];
		double[] puntenY = new double[aantalP];
		for (int i = 0; i < aantalP; i++) {
			puntenX[i] = pnt[i].x;
			puntenY[i] = pnt[i].y;
		}

		for (int f = 0; f < 10; f++) {
			while (!klaar) {
				aantalPW = 0;
				double[] puntenXW = new double[2 * aantalP + 3];
				double[] puntenYW = new double[2 * aantalP + 3];

				double x0;
				double y0;
				double x1;
				double y1;
				double x2 = 0;
				double y2 = 0;
				double x3;
				double y3;

				for (int i = -1; i < aantalP - 2; i++) {
					if (i == -1) {
						x0 = puntenX[i + 1];
						y0 = puntenY[i + 1];
						x1 = puntenX[i + 1];
						y1 = puntenY[i + 1];
						x2 = puntenX[i + 2];
						y2 = puntenY[i + 2];
						x3 = puntenX[i + 3];
						y3 = puntenY[i + 3];
					} else if (i < aantalP - 3) {
						x0 = puntenX[i];
						y0 = puntenY[i];
						x1 = puntenX[i + 1];
						y1 = puntenY[i + 1];
						x2 = puntenX[i + 2];
						y2 = puntenY[i + 2];
						x3 = puntenX[i + 3];
						y3 = puntenY[i + 3];
					} else {
						x0 = puntenX[i];
						y0 = puntenY[i];
						x1 = puntenX[i + 1];
						y1 = puntenY[i + 1];
						x2 = puntenX[i + 2];
						y2 = puntenY[i + 2];
						x3 = puntenX[i + 2];
						y3 = puntenY[i + 2];
					}

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
				puntenXW[aantalPW] = x2;
				puntenYW[aantalPW] = y2;
				aantalPW++;

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

		Point[] pNieuw = new Point[aantalP];
		for (int i = 0; i < aantalP; i++) {
			pNieuw[i] = new Point((int) puntenX[i], (int) puntenY[i]);
		}
		return pNieuw;
	}

	public void verplaats(int dx, int dy) 
	{
		for (int j = 0; j < aantalBPunten; j++) {
			basispunten[j].x += dx;
			basispunten[j].y += dy;
			if (exactePuntenX != null) {
				exactePuntenX[j] += dx;
				exactePuntenY[j] += dy;
			}
		}
		tekenpunten = buig(basispunten);
		aantalTPunten = tekenpunten.length;

	}

	public void draai(double dh) 
	{
		if (exactePuntenX == null) {
			exactePuntenX = new double[aantalBPunten];
			exactePuntenY = new double[aantalBPunten];
			for (int j = 0; j < aantalBPunten; j++) {
				exactePuntenX[j] = basispunten[j].x;
				exactePuntenY[j] = basispunten[j].y;
			}
		}
		double cos = Math.cos(dh * Math.PI / 180);
		double sin = Math.sin(dh * Math.PI / 180);

		for (int j = 0; j < aantalBPunten; j++) {
			double x = exactePuntenX[j] - breedte / 2;
			double y = exactePuntenY[j] - hoogte / 2;
			exactePuntenX[j] = cos * x + sin * y + breedte / 2;
			exactePuntenY[j] = cos * y - sin * x + hoogte / 2;
			basispunten[j].x = ((int) exactePuntenX[j] + 1) / 2 * 2;
			basispunten[j].y = ((int) exactePuntenY[j] + 1) / 2 * 2;
		}
		tekenpunten = buig(basispunten);
		aantalTPunten = tekenpunten.length;

	}

	public void schaal(double factor) {
		schaal(factor, factor);
	}

	public void schaal(double factorX, double factorY) {
		if (exactePuntenX == null) {
			exactePuntenX = new double[aantalBPunten];
			exactePuntenY = new double[aantalBPunten];
			for (int j = 0; j < aantalBPunten; j++) {
				exactePuntenX[j] = basispunten[j].x;
				exactePuntenY[j] = basispunten[j].y;
			}
		}
		for (int j = 0; j < aantalBPunten; j++) {
			exactePuntenX[j] = factorX * exactePuntenX[j];
			exactePuntenY[j] = factorY * exactePuntenY[j];
			basispunten[j].x = ((int) exactePuntenX[j]);
			basispunten[j].y = ((int) exactePuntenY[j]);
		}
		tekenpunten = buig(basispunten);
		aantalTPunten = tekenpunten.length;
	}

}
