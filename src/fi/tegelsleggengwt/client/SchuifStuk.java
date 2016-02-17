package fi.tegelsleggengwt.client;

//import java.awt.*;
//import java.awt.Polygon;

//import java.io.Serializable;

//import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;


class SchuifStuk //implements Serializable 
{	
	int aantalPunten;
	Point[] punten;
	CssColor kleur;
	Point positie;
	Polygon pol;
	
	boolean transVersion = false;
	
	public SchuifStuk(boolean version, int n, Point[] ptn, Point pos, CssColor kl)
	{	
		transVersion = version;
		
		aantalPunten = n;
		punten = ptn;
		kleur = kl;
		positie = new Point(pos);
		maakPol();
	}
	public SchuifStuk(boolean version, SchuifStuk s, int x, int y)
	{	
		transVersion = version;		
		
		aantalPunten = s.aantalPunten;
		punten = new Point[aantalPunten];
		for (int i = 0; i < aantalPunten; i++)
		{	punten[i] = new Point(s.punten[i]);
		}
		positie = new Point(x,y);
		//kleur = new Color(s.kleur.getRGB());
		kleur = CssColor.make(s.kleur.value());
		maakPol();
	}
	public SchuifStuk(boolean version, int n, Point[] ptn, CssColor kl)
	{	
		transVersion = version;		
		
		aantalPunten = n;
		punten = ptn;
		kleur = kl;
		positie = new Point(0,0);
		maakPol();
	}
	
	public static boolean equalSS(SchuifStuk ss1, SchuifStuk ss2)
	{	
		boolean kleurEqual = ss1.kleur.value().equals(ss2.kleur.value());
		if (!kleurEqual)
			return false;
		boolean equalNumPoints = ss1.aantalPunten == ss2.aantalPunten;
		if (!equalNumPoints)
			return false;
//volgorde??
		boolean equalPoints = true;
		for (int pCnt = 0; pCnt < ss1.punten.length; pCnt++)
		{
			equalPoints = equalPoints && (ss1.punten[pCnt].x == ss2.punten[pCnt].x) &&
			(ss1.punten[pCnt].y == ss2.punten[pCnt].y);
		}
		
		return equalPoints;
	}
	
	
	public void maakPol()
	{	
		if (transVersion)
		{
			pol = new Polygon();
			for(int i = 0; i < aantalPunten; i++)
			{	pol.addPoint(positie.x + Trans.geefx(punten[i].x,punten[i].y), 
					         positie.y + Trans.geefy(punten[i].x,punten[i].y));
			}
		}
		else
		{	
			pol = new Polygon();
			for (int i=0 ; i < aantalPunten; i++)
			{	pol.addPoint(positie.x + punten[i].x, positie.y + punten[i].y);
			}
		}
		
//for (int i = 0; i < pol.aantalPunten; i++)
//{
//System.out.print("("+pol.geefPuntX(i)+","+pol.geefPuntY(i)+") ");
//}
//System.out.println("");
	} //maakPol
	public boolean bevat (int x, int y)
	{	if (pol.contains(x,y))
			return true;
		else 
			return false;
	}
	public void draaiVorm()
	{	
		if (transVersion)
		{
			for (int i = 0; i < aantalPunten; i++)
			{	int nx = -punten[i].y;
				int ny = punten[i].x + punten[i].y;
				punten[i].x = nx;
				punten[i].y = ny;
			}
			maakPol();
		}
		else
		{	
			for (int i = 0; i < aantalPunten; i++)
			{	int nx = -punten[i].y;
				int ny = punten[i].x;
				punten[i].x = nx;
				punten[i].y = ny;
			}
			maakPol();
		}
	}
	public void spiegel()
	{	
		if (transVersion)
		{
			for (int i = 0; i < aantalPunten; i++)
			{	punten[i].x = -(punten[i].x + punten[i].y);
			}
			maakPol();
		}
		else
		{	
			for (int i = 0; i < aantalPunten; i++)
			{	punten[i].x = -punten[i].x;
			}
			maakPol();
		}
	}
	public void zetKleur(CssColor c)
	{	kleur = c;
	}
	public void zetPositie(int x, int y)
	{	positie.x = x;
		positie.y = y;
		maakPol();
	}
	public void veranderPositie(int dx,int dy)
	{	positie.x = positie.x + dx;
		positie.y = positie.y + dy;
		maakPol();
	}
	public void plaatsOpGrid()
	{	
		if (transVersion)
		{
			int x = positie.x + 300;
			int y = positie.y + 300;
			int f = Trans.factor;
			int ex = x % (f * 4);
			int ey = y % (f * 7);
			if (ex < 2 * f)
				veranderPositie(-ex, 0);
			else 
				veranderPositie(f * 4 - ex, 0);
			if (ey < 7 * f / 2)
				veranderPositie(0, - ey);
			else 
				veranderPositie(0, f * 7 - ey);
			maakPol();
		}
		else
		{	
			int x = positie.x + 300;
			int y = positie.y + 300;
			int ex = x % 20;
			int ey = y % 20;
			if (ex < 10)
				veranderPositie(- ex, 0);
			else 
				veranderPositie(20 - ex,0);
			if (ey < 10)
				veranderPositie(0, - ey);
			else 
				veranderPositie(0, 20 - ey);
			maakPol();
		}
	}
}
