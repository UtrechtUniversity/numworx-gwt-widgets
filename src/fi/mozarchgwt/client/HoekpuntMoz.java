package fi.mozarchgwt.client;


public class HoekpuntMoz
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