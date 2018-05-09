package fi.heksgwt.client.vectortek;

//import java.awt.*;
//import java.awt.event.*;
//import java.io.*;
import fi.heksgwt.client.scobjects.ScContainer;
//import fi.beans.appletutil.*;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.dom.client.Context2d;

public class Tekening extends ScContainer
{
	public int breedte;
	public int hoogte;

	public int aantalTekenObj;
	public TekenObjectTek[] to;

	boolean log = false;
	String srtStr = "";

	boolean visible = true;

	public String naam = "";

	public Tekening(int x, int y, int b, int h, Map<String, Object> tekMap)
	{
		super();
		relx = x;
		rely = y;
		relb = b;
		relh = h;

		aantalTekenObj = 0;
		to = new TekenObjectTek[250];

		ObjectMap map = JSONUtilities.wrapMap(tekMap);

		if (map.containsKey("breedte"))
			breedte = map.getInt("breedte");
		if (map.containsKey("hoogte"))
			hoogte = map.getInt("hoogte");
		if (map.containsKey("aantaltekenobj"))
			aantalTekenObj = map.getInt("aantaltekenobj");
		List<Map<String, Object>> tekenObj = new ArrayList<Map<String, Object>>();
		if (map.containsKey("tekenobj"))
			tekenObj = map.getMapList("tekenobj");
		for (int tCnt = 0; tCnt < tekenObj.size(); tCnt++)
		{
			Map<String, Object> tekenObjState = tekenObj.get(tCnt);

			if (tekenObjState.containsKey("soort"))
			{
				String soort = (String) tekenObjState.get("soort");
				if (soort.equals("Lijnstuk"))
				{
					to[tCnt] = new LijnstukTek(tekenObjState);
				}
				else if (soort.equals("Veelhoek"))
				{
					to[tCnt] = new VeelhoekTek(tekenObjState);
				}
				if (soort.equals("Kromme"))
				{
					to[tCnt] = new KrommeTek(tekenObjState);
				}
				if (soort.equals("VulKromme"))
				{
					to[tCnt] = new VulKrommeTek(tekenObjState);
				}
				to[tCnt].setSize(breedte, hoogte);
				to[tCnt].verplaats(x, y);
				add(to[tCnt], 0);

			}
		}

		setBounds(x, y, b, h);
	}

	/*
	 * private void leesFile(AppletUtil au, String naam) { DataInputStream
	 * invoer; try { invoer = new DataInputStream(au.getStream("resources/" +
	 * naam)); breedte = (invoer.readByte() + 128) * 2; hoogte =
	 * (invoer.readByte() + 128) * 2; setSize(breedte, hoogte); aantalTekenObj =
	 * invoer.readByte(); int lCnt = 0; int vCnt = 0; int kCnt = 0; int vkCnt =
	 * 0; for (int i = 0; i < aantalTekenObj; i++) { int srt =
	 * invoer.readByte(); if (srt == 0) { to[i] = new LijnstukTek(invoer);
	 * lCnt++; } else if (srt == 3) { to[i] = new VeelhoekTek(invoer); vCnt++; }
	 * else if (srt == 4) { to[i] = new KrommeTek(invoer); kCnt++; } else if
	 * (srt == 5) { to[i] = new VulKrommeTek(invoer); vkCnt++; }
	 * to[i].setSize(breedte, hoogte); add(to[i], 0); } srtStr = "" + lCnt + ","
	 * + vCnt + "," + kCnt + "," + vkCnt; if (log) System.out.println("" + naam
	 * + " aantalTekenObj " + aantalTekenObj + " " + srtStr);
	 * 
	 * } catch (IOException io) {} }
	 */
	/*
	 * public Hashtable<String,Object> getState() { Hashtable<String,Object> h =
	 * new Hashtable<String,Object>(); h.put("breedte", breedte);
	 * h.put("hoogte", hoogte); h.put("aantaltekenobject", aantalTekenObj);
	 * ArrayList<Hashtable<String,Object>> tekenobj = new
	 * ArrayList<Hashtable<String,Object>>(); for (int tCnt = 0; tCnt <
	 * to.length; tCnt++) { TekenObjectTek tot = to[tCnt]; if (to[tCnt]
	 * instanceof LijnstukTek) {
	 * tekenobj.add(((LijnstukTek)to[tCnt]).getState()); } else if (to[tCnt]
	 * instanceof VeelhoekTek) {
	 * tekenobj.add(((VeelhoekTek)to[tCnt]).getState()); } else if (to[tCnt]
	 * instanceof KrommeTek) { tekenobj.add(((KrommeTek) to[tCnt]).getState());
	 * } else if (to[tCnt] instanceof VulKrommeTek) {
	 * tekenobj.add(((VulKrommeTek)to[tCnt]).getState()); }
	 * 
	 * } h.put("tekenobj", tekenobj);
	 * 
	 * return h; }
	 */
	public void setVisible(boolean b)
	{
		visible = b;
	}

	public void paint(Context2d gr)
	{

		if (!visible)
			return;

		for (int i = 0; i < aantalTekenObj; i++)
		{
			if (naam.equals("") || (i != 11))
				to[i].paint(gr);
		}
	}

	public boolean contains(int x, int y)
	{
		for (int i = 0; i < aantalTekenObj; i++)
		{
			if (to[i].contains(x, y))
				return true;
		}
		return false;
	}

	public void setSize(int b, int h)
	{
		double bd = b;
		double hd = h;
		double fx = bd / breedte;
		double fy = hd / hoogte;
		breedte = b;
		hoogte = h;
		for (int i = 0; i < aantalTekenObj; i++)
		{
			to[i].schaal(fx, fy);
			to[i].setSize(breedte, hoogte);
		}
		super.setSize(b, h);
	}

	public void verplaats(int dx, int dy)
	{

		// System.out.println("tek verplaats " + dx + "," + dy);

		for (int i = 0; i < aantalTekenObj; i++)
		{
			to[i].verplaats(dx, dy);
		}

		super.setLocation(getLocation().x + dx, getLocation().y + dy);
	}

	public void setLocation(int x, int y)
	{
		int dx = x - getLocation().x;
		int dy = y - getLocation().y;

		for (int i = 0; i < aantalTekenObj; i++)
		{
			to[i].verplaats(dx, dy);
		}

		super.setLocation(x, y);
	}

	public void setBounds(int x, int y, int b, int h)
	{
		double bd = b;
		double hd = h;
		double fx = bd / breedte;
		double fy = hd / hoogte;

		breedte = b;
		hoogte = h;
		for (int i = 0; i < aantalTekenObj; i++)
		{
			to[i].schaal(fx, fy);
			to[i].setSize(breedte, hoogte);
		}
		super.setBounds(x, y, b, h);

		// if (!naam.equals(""))
		// System.out.println(naam + " setBounds " + fx + "," + fy);

	}

	public void schaal(double factorX, double factorY)
	{
		breedte = (int) (factorX * relb);
		hoogte = (int) (factorY * relh);
		for (int i = 0; i < aantalTekenObj; i++)
		{
			to[i].schaal(factorX, factorY);
			to[i].setSize(breedte, hoogte);
		}
		super.setSize(breedte, hoogte);
	}

	public void schaal(double factor)
	{
		schaal = factor;
		int x = (int) (schaal * relx);
		int y = (int) (schaal * rely);
		int b = (int) (schaal * relb);
		int h = (int) (schaal * relh);
		setBounds(x, y, b, h);

		// if (!naam.equals(""))
		// System.out.println(naam + " loc schaal = " + getLocation().x + "," +
		// getLocation().y);

	}

	public void draai(double h)
	{
		for (int i = 0; i < aantalTekenObj; i++)
		{
			to[i].draai(h);
		}
	}
}
