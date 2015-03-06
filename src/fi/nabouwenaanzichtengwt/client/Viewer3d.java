package fi.nabouwenaanzichtengwt.client;


import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.event.dom.client.DoubleClickEvent;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;

//import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
//import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;

public class Viewer3d
{
	Canvas canvas;
	private NabouwenAanzichtenGWT eigenaar;
	private GetalRooster gr;
	//private AnimatieBeheerder ab;
	private MuisBeheerder mb;
	private int breedte, hoogte;
	private Punt3D beginpunt, eindpunt, startpunt;
	public Lichaam3D[] l;
	//private Image im ;
	public Context2d gIm;
	public Matrix3D mat;
	private boolean pen, vul, leeg, schaduw, grZichtbaar;
	private int lnummer;
	private CssColor penkleur, vulkleur, achtergrondkleur;
	public boolean bezigMetTekenen, muisAan, klikAan, pijlAan, balkAan, maakAanzicht;
	private double afstand;
	int aantalVeelvlakken;
	Veelvlak[] vvRij;
	Polygon[][] p;
	Polygon[][][][] pp;
	Klikvlak[] kv;
	int aantalKv;
	KubusRooster kr;
	private double k, xhoek, yhoek, beginx, beginy;
	private int[] sorteerRij;
	//private CubeRemoveThread cubeRemoveThread;
	private boolean removed = false;
	private boolean removing = false;
	private boolean holdMouse = false;
	private long holdMouseStartTime = 0;
	private long holdMouseEndTime = 0;

	public Viewer3d(KubusRooster kr, int x, int y, int b, int h, NabouwenAanzichtenGWT hb)
	{
		canvas = Canvas.createIfSupported();
		canvas.setWidth(b + "px");
		canvas.setHeight(h + "px");
		canvas.setCoordinateSpaceWidth(b);
		canvas.setCoordinateSpaceHeight(h);
		breedte = b;
		hoogte = h;
		aantalVeelvlakken = 0;
		eigenaar = hb;

		this.kr = kr;
		int n = kr.maxAantal;
		p = new Polygon[n][n];
		pp = new Polygon[n][n][n][6];
		aantalKv = 0;
		kv = new Klikvlak[n * n * n * 7];
		for (int i = 0; i < kr.maxAantal; i++)
		{
			for (int j = 0; j < kr.maxAantal; j++)
			{
				for (int k = 0; k < kr.maxAantal; k++)
				{
					for (int m = 0; m < 6; m++)
					{
						pp[i][j][k][m] = new Polygon();
					}
				}
			}
		}

		mb = new MuisBeheerder(this);
		canvas.addMouseDownHandler(mb);
		canvas.addMouseUpHandler(mb);
		canvas.addMouseMoveHandler(mb);
		canvas.addTouchStartHandler(mb);
		canvas.addTouchEndHandler(mb);
		canvas.addTouchMoveHandler(mb);
		
		achtergrondkleur = CssColor.make("white");
		leeg = false;
		schaduw = true;
		muisAan = true;
		klikAan = true;
		pijlAan = true;
		balkAan = false;
		grZichtbaar = false;
		l = new Lichaam3D[5];
		afstand = 1000;
		lnummer = 0;
		for (int i = 0; i < 5; i++)
		{
			l[i] = new Lichaam3D();
			l[i].zetAfstand(afstand);
		}
		sorteerRij = new int[2000];
		mat = new Matrix3D();
		//if(mb!=null && ab!=null)				
		//{	mb.meldAnimatieBeheerder(ab);		
		//}
		k = 230;
		xhoek = 0;
		yhoek = 0;
		beginx = 30;
		beginy = -30;
	}

	public void zetMuisBeheerder(MuisBeheerder mb)
	{
		this.mb = mb;
	}

	public Canvas getCanvas()
	{
		return canvas;
	}

	public void initContext2d()
	{
		gIm = canvas.getContext2d();
		//gIm.setFillStyle(achtergrondkleur);
		//gIm.fillRect(0,0,200, 100);
	}

	public void zetAfstand(double afst)
	{
		afstand = afst;
		for (int i = 0; i < 5; i++)
		{
			l[i].zetAfstand(afst);
		}
	}

	public void zetSchaduw(boolean s)
	{
		schaduw = s;
	}

	public void zetBeginHoeken(double hx, double hy)
	{
		beginx = hx;
		beginy = hy;
		xhoek = 0;
		yhoek = 0;
	}

	public void zetMuisAan(boolean b)
	{
		muisAan = b;
	}

	public void zetKlikAan(boolean b)
	{
		klikAan = b;
	}

    public void zetPijlAan(boolean b)
    {   pijlAan = b;
        if (pijlAan)
        	balkAan = false;
    }

    public void zetBalkAan(boolean b)
    {   balkAan = b;
        if (balkAan)
            pijlAan = false;
    }
   
    public void zetMaakAanzicht(boolean b)
    {   maakAanzicht = b;
	    if (maakAanzicht)
	    {	zetBeginHoeken(90,0);
	    	zetAfstand(1000000000);
	    	zetMuisAan(false);
	    	zetSchaduw(false);
	    }
	    else
	    {	//zetBeginHoeken(30,-30);
	    	//zetAfstand(1000);
	    	//zetMuisAan(true);
	    	//zetSchaduw(true);
	    }
    }

    
	public void zetKubusRooster(KubusRooster kur)
	{
		kr = kur;
		if (grZichtbaar)
		{ //if(gr!=null)remove(gr);
			zetGetalRooster(true);
			for (int i = 0; i < kr.maxAantal; i++)
			{
				for (int j = 0; j < kr.maxAantal; j++)
				{
					for (int k = 0; k < kr.maxAantal; k++)
					{
						if (kr.kubussen[i][j][k] != null && gr.geefHoogte(i, j) < k + 1)
						{
							gr.zetHoogte(i, j, k);
						}
					}
				}
			}
		}
		int n = kr.maxAantal;
		p = new Polygon[n][n];
		pp = new Polygon[n][n][n][6];
		for (int i = 0; i < kr.maxAantal; i++)
		{
			for (int j = 0; j < kr.maxAantal; j++)
			{
				for (int k = 0; k < kr.maxAantal; k++)
				{
					for (int m = 0; m < 6; m++)
					{
						pp[i][j][k][m] = new Polygon();
					}
				}
			}
		}
		aantalKv = 0;
		kv = new Klikvlak[n * n * n * 7];
		//if(im!=null)tekenOpnieuw();
	}

	public void zetVeelvlak(Veelvlak v)
	{
		vvRij[0] = v;
		tekenOpnieuw();
	}

	public void zetVeelvlak(Veelvlak v, int n)
	{
		vvRij[n] = v;
		tekenOpnieuw();
	}

	public void zetGetalRooster(boolean bool)
	{
		grZichtbaar = bool;
		if (bool)
		{
			int n = kr.maxAantal;
			int x = breedte * 80 / 300 + 12;
			int y = breedte * 80 / 300 - 11;
			int b = breedte * 38 / 300;
			int h = hoogte * 57 / 300;
			gr = new GetalRooster(n, x, y, b, h, breedte, hoogte);
			//add(gr);
		}
		else
		{ //if(gr!=null)remove(gr);
		}
		zetBeginHoeken(90, 0);
		zetAfstand(1000000000);
		zetMuisAan(false);
		zetSchaduw(false);
	}

    public void zetGetalRooster2(boolean bool)
    {   grZichtbaar = bool;
        if (bool)
        {   
        	int n = kr.maxAantal;

            int x = breedte * 80 / 300;
            int y = hoogte * 80 / 300;
            int b = Math.min(hoogte,breedte) * 140 / 300;
            int h = b;

            if (hoogte < breedte)
            {   x += (breedte - hoogte) * 40 / 300;
            
            }
            else // hoogte > breedte
            {   y += (hoogte - breedte) * 40 / 300;
            }
            
            gr = new GetalRooster(n, x, y, b, h, breedte, hoogte);
            //add(gr);
            
System.out.println("breedte = " + breedte);
System.out.println("hoogte = " + hoogte);
System.out.println("x = " + x);
System.out.println("y = " + y);
System.out.println("b = " + b);
        }
        else
        {   if (gr != null)
            {   //remove(gr);
                gr = null;
            }
        }
        if (bool)
        {	zetBeginHoeken(90,0);
        	zetAfstand(1000000000);
        	zetMuisAan(false);
        	zetSchaduw(false);
        }
    }
	
    public void zetHoogtes()
    {   if (gr != null)
        {   for (int i = 0; i < kr.maxAantal; i++)
            {   for (int j = 0; j < kr.maxAantal; j++)
                {   for (int k = 0; k < kr.maxAantal; k++)
                    {   if (kr.kubussen[i][j][k] != null && gr.geefHoogte(i,j) < k + 1)
                        {   gr.zetHoogte(i, j, k);
                        }
                    }
                }
            }
        
        }
        
    }
     
	public void voegVeelvlakToe(Veelvlak v)
	{
		vvRij[aantalVeelvlakken] = v;
		aantalVeelvlakken++;
	}

	void tekenprogramma()
	{
		mat.initialiseer();
		mat.xdraai(beginx + xhoek);
		mat.ydraai(beginy + yhoek);
		tekenKubusRooster();
	}

	void tekenKubusRooster()
	{
		aantalKv = 0;
		if (!maakAanzicht)
			tekenVeelvlak(0, kr.grondvlak);
		//tekenVeelvlak(0, kr.grondvlak);
		if (pijlAan)
		{
			
//System.out.println("tkr pijlAan");

			for (int i = 0; i < kr.pijl.aantalVlakken; i++)
			{
				tekenVlak(1, kr.pijl.vlakken[i]);
				kv[aantalKv] = new Klikvlak(i, 0, 1, 6);
				aantalKv++;
			}
		}
		if (balkAan)
	    {
			
//System.out.println("tkr balkAan");

	        for (int i = 0; i < kr.balk.aantalVlakken; i++)
	        {   tekenVlak(1, kr.balk.vlakken[i]);
	            kv[aantalKv] = new Klikvlak(i, 0, 1, 6);
	            aantalKv++;
	        }
	    }

		
//System.out.println("tkr");		
		for (int i = 0; i < kr.maxAantal; i++)
		{
			for (int j = 0; j < kr.maxAantal; j++)
			{	
				if (maakAanzicht) 
        		{	kr.vierkanten[i][j].vlakken[0].vulkleur = "wit";
        			kr.vierkanten[i][j].vlakken[0].vorigeKleur = "wit";
        		}
				else
				{  	kr.vierkanten[i][j].vlakken[0].vorigeKleur = "lichtgrijs";
					kr.vierkanten[i][j].vlakken[0].vulkleur = "lichtgrijs";
				}
				tekenVlak(1, kr.vierkanten[i][j].vlakken[0]);
				p[i][j] = geefVlak(1);
				
				kv[aantalKv] = new Klikvlak(i, j, 0, 6);
				aantalKv++;
				for (int k = 0; k < kr.maxAantal; k++)
				{
					if (kr.kubussen[i][j][k] != null)
					{
						for (int m = 0; m < 6; m++)
						{
							if (kr.kubussen[i][j][k].isOnbedekt[m])
							{
								tekenVlak(1, kr.kubussen[i][j][k].vlakken[m]);
								pp[i][j][k][m] = geefVlak(1);
								kv[aantalKv] = new Klikvlak(i, j, k, m);
								aantalKv++;
							}
						}
					}
				}
			}
		}
	}

	void tekenVeelvlak(int n, Veelvlak vv)
	{
		for (int i = 0; i < vv.aantalVlakken; i++)
		{
			tekenVlak(n, vv.vlakken[i]);
		}
	}

	void tekenVlak(int n, Vlak v)
	{
		penUit();
		stap(k * v.punten[0].x, k * v.punten[0].y, k * v.punten[0].z);
		if (!(v.lijnkleur == "transparant"))
			penAan(v.lijnkleur);
		vulAan(n, v.vulkleur);
		for (int i = 0; i < v.aantalHoekpunten; i++)
		{
			int a = i;
			int b = (i + 1) % v.aantalHoekpunten;
			stap(-k * (v.punten[a].x - v.punten[b].x), -k * (v.punten[a].y - v.punten[b].y), -k * (v.punten[a].z - v.punten[b].z));
		}
		vulUit(n);
		penUit();
		stap(-k * v.punten[0].x, -k * v.punten[0].y, -k * v.punten[0].z);

	}

	public void draw()
	{
		draw(gIm);
	}

	public void draw(Context2d g)
	{
		bezigMetTekenen = true;
		/*if(im==null)
		{	breedte = getSize().width;
			hoogte = getSize().height;	
			// breedte/400 ipv breedte/500, dan past de langwerpige kr beter
			double startschaal = Math.min((double)breedte/400,(double)hoogte/500);
			mat.initialiseer(0,0,0,startschaal);	
			startpunt = new Punt3D(breedte/2,hoogte/2,0);
			for(int i=0 ; i<5 ; i++)
			{	l[i].maakNulpunt(breedte/2,hoogte/2,0);
			}
			im = createImage(breedte,hoogte);
			gIm = im.getGraphics();
			tekenOpImage(true);
		}
		g.drawImage(im, 0, 0, null);*/
		double startschaal = Math.min((double) breedte / 400, (double) hoogte / 500);
		mat.initialiseer(0, 0, 0, startschaal);
		startpunt = new Punt3D(breedte / 2, hoogte / 2, 0);
		for (int i = 0; i < 5; i++)
		{
			l[i].maakNulpunt(breedte / 2, hoogte / 2, 0);
		}
		tekenOpImage(true);

		//gIm.setFillStyle(achtergrondkleur);
		//gIm.fillRect(0, 0, breedte, hoogte);
		bezigMetTekenen = false;
	}

	public void tekenOpImage(boolean wis)
	{
		if (gIm == null)
			return;
		
//gIm.setStrokeStyle(CssColor.make(0,0,0));
//gIm.strokeRect(0, 0, breedte, hoogte);
		
		beginpunt = new Punt3D(startpunt);
		eindpunt = new Punt3D(beginpunt);
		mat.initialiseer();
		penAan(0, 0, 0);
		vul = false;
		tekenprogramma();
		for (int i = 0; i < 5; i++)
		{
			l[i].sorteer();
		}
		for (int i = 0; i < 2000; i++)
		{
			sorteerRij[i] = l[1].sorteerRij[i];
		}
		if (wis)
		{
			gIm.setFillStyle(achtergrondkleur);
			gIm.fillRect(0, 0, breedte, hoogte);
		}
		for (int j = 0; j < 5; j++)
		{
			l[j].draw(gIm, schaduw);
			l[j] = new Lichaam3D();
			l[j].zetAfstand(afstand);
			l[j].maakNulpunt(breedte / 2, hoogte / 2, 0);
		}
		//super.paint(gIm);
		if (gr != null)
		{    gr.paint(gIm, p);
//System.out.println("gr paint");		
		}
	}

	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door handlers van het leerlingprogramma
	//-------------------------------------------------------------------------------------------
	void tekenOpnieuw()
	{
		bezigMetTekenen = true;
		tekenOpImage(true);
		//Graphics g = getGraphics();
		//if(g!=null)g.drawImage(im, 0, 0, null); 
		bezigMetTekenen = false;
	}

	void tekenErbij()
	{
		bezigMetTekenen = true;
		tekenOpImage(false);
		//Graphics g = getGraphics();
		//g.drawImage(im, 0, 0, null);
		bezigMetTekenen = false;
	}

	void stap(double dx, double dy, double dz)
	{
		naarVolgendPunt(dx, -dy, -dz);
	}

	void naarVolgendPunt(double dx, double dy, double dz)
	{
		eindpunt = mat.geefVolgendPunt(beginpunt, dx, dy, dz);
		if (pen && !vul)
		{
			l[lnummer].voegPuntToe(beginpunt);
			l[lnummer].voegPuntToe(eindpunt);
			l[lnummer].voegPolygonToe(penkleur, penkleur, true, false);
		}
		if (vul)
		{
			l[lnummer].voegPuntToe(beginpunt);
		}
		beginpunt.x = eindpunt.x;
		beginpunt.y = eindpunt.y;
		beginpunt.z = eindpunt.z;
	}

	void tekenPolygon()
	{
		l[0].voegPolygonToe(vulkleur, penkleur, pen, leeg);
	}

	void tekenPolygon(int n)
	{
		l[n].voegPolygonToe(vulkleur, penkleur, pen, leeg);
	}

	void penAan()
	{
		pen = true;
	}

	void penAan(String kl)
	{
		pen = true;
		penkleur = maakKleur(kl);
		gIm.setStrokeStyle(penkleur);
	}

	void penAan(int r, int g, int b)
	{
		pen = true;
		penkleur = CssColor.make(r, g, b);
		gIm.setStrokeStyle(penkleur);
	}

	void penAan(int n)
	{
		pen = true;
		lnummer = n;
	}

	void penAan(int n, String kl)
	{
		pen = true;
		penkleur = maakKleur(kl);
		gIm.setStrokeStyle(penkleur);
		lnummer = n;
	}

	void penAan(int n, int r, int g, int b)
	{
		pen = true;
		penkleur = CssColor.make(r, g, b);
		gIm.setStrokeStyle(penkleur);
		lnummer = n;
	}

	void penUit()
	{
		pen = false;
	}

	void penUit(int n)
	{
		pen = false;
		lnummer = 0;
	}

	void vulAan()
	{
		vul = true;
	}

	void vulAan(String kl)
	{
		vul = true;
		if (kl.equals("transparant"))
			leeg = true;
		else
			vulkleur = maakKleur(kl);
	}

	void vulAan(int r, int g, int b)
	{
		vul = true;
		vulkleur = CssColor.make(r, g, b);
	}

	void vulAan(int n)
	{
		vul = true;
		lnummer = n;
	}

	void vulAan(int n, String kl)
	{
		vul = true;
		lnummer = n;
		if (kl.equals("transparant"))
			leeg = true;
		else
			vulkleur = maakKleur(kl);
	}

	void vulAan(int n, int r, int g, int b)
	{
		vul = true;
		lnummer = n;
		vulkleur = CssColor.make(r, g, b);
	}

	void vulAan(CssColor kl)
	{
		vul = true;
		vulkleur = kl;
	}

	void vulUit()
	{
		tekenPolygon();
		vul = false;
		lnummer = 0;
		leeg = false;
	}

	void vulUit(int n)
	{
		tekenPolygon(n);
		vul = false;
		lnummer = 0;
		leeg = false;
	}

	void achtergrondkleur(String kl)
	{
		achtergrondkleur = maakKleur(kl);
	}

	void achtergrondkleur(int r, int g, int b)
	{
		achtergrondkleur = CssColor.make(r, g, b);
	}

	void zetAchtergrond(CssColor c)
	{
		achtergrondkleur = c;
	}

	//	void schrijf(String s)
	//	{	gIm.drawString(s, (int)beginpunt.x, (int)beginpunt.y);
	//	}
	//	void schrijf(String s, Font f)
	//	{	gIm.setFont(f);
	//		gIm.drawString(s, (int)beginpunt.x, (int)beginpunt.y);
	//	}
	Punt geefPunt() // geeft de laatst getekende Punt
	{
		double pf = (afstand - beginpunt.z) / afstand;
		double begx = l[0].nulpunt.x + (beginpunt.x - l[0].nulpunt.x) / pf;
		double begy = l[0].nulpunt.y + (beginpunt.y - l[0].nulpunt.y) / pf;
		return new Punt(begx, begy);
	}

	Punt geefPunt(int n) // geeft de laatst getekende Punt
	{
		double pf = (afstand - beginpunt.z) / afstand;
		double begx = l[n].nulpunt.x + (beginpunt.x - l[n].nulpunt.x) / pf;
		double begy = l[n].nulpunt.y + (beginpunt.y - l[n].nulpunt.y) / pf;
		return new Punt(begx, begy);
	}

	Polygon geefVlak()
	{
		if (l[0].vlakken[l[0].aantalPolygonen - 1].normaal.z > 0)
			return l[0].vlakken[l[0].aantalPolygonen - 1].pol;
		else
			return new Polygon();
	}

	Polygon geefVlak(int n)
	{
		if (l[n].vlakken[l[n].aantalPolygonen - 1].normaal.z > 0)
			return l[n].vlakken[l[n].aantalPolygonen - 1].pol;
		else
			return new Polygon();
	}

	private CssColor maakKleur(String kl)
	{
		if (kl.equals("rood"))
			return CssColor.make(255, 0, 0);
		else if (kl.equals("groen"))
			return CssColor.make(0, 255, 0);
		else if (kl.equals("blauw"))
			return CssColor.make(0, 0, 255);
		else if (kl.equals("geel"))
			return CssColor.make(255, 255, 0);
		else if (kl.equals("cyaan"))
			return CssColor.make(0, 255, 255);
		else if (kl.equals("roze"))
			return CssColor.make("pink");
		else if (kl.equals("zwart"))
			return CssColor.make(0, 0, 0);
		else if (kl.equals("grijs"))
			return CssColor.make(128, 128, 128);
		else if (kl.equals("lichtgrijs"))
			return CssColor.make(200, 200, 200);
		else if (kl.equals("magenta"))
			return CssColor.make(255, 0, 255);
		else if (kl.equals("wit"))
			return CssColor.make(255, 255, 255);
		else if (kl.equals("oranje"))
			return CssColor.make(255, 128, 0);
		else
			return CssColor.make(0, 0, 0);
	}

	public void animatie()
	{
	}

	public void muisSleepActie()
	{
		if (removed)
			return;
		holdMouse = holdMouse && System.currentTimeMillis() - holdMouseStartTime < 300;
		if (!holdMouse && muisAan)
		{
			xhoek -= 0.5 * mb.geefSleepdy();
			if (xhoek > 90 - beginx)
				xhoek = 90 - beginx;
			if (xhoek < 0 - beginx)
				xhoek = 0 - beginx;
			yhoek += 0.5 * mb.geefSleepdx();
			tekenOpnieuw();
		}

	}

	public void muisKkActie(MouseUpEvent e, boolean remove)
	//public void muisKkActie(boolean remove)
	{
		for (int q = aantalKv - 1; q > -1; q--)
		{
			int n = sorteerRij[q];
			if (kv[n].m == 6 && kv[n].k == 0 && p[kv[n].i][kv[n].j].contains(mb.geefDrukx(), mb.geefDruky()))
			{
				if (eigenaar.isBouwen() && !(e.isControlKeyDown() || remove))
				{
					kr.voegKubusToe(kv[n].i, kv[n].j, 0);
					if (gr != null)
						gr.verhoog(kv[n].i, kv[n].j);
				}
				else
				{
					kr.verwijderKubus(kv[n].i, kv[n].j, 0);
					if (gr != null)
						gr.verlaag(kv[n].i, kv[n].j);
				}
				return;
			}
			else if (kv[n].m != 6 && pp[kv[n].i][kv[n].j][kv[n].k][kv[n].m].contains(mb.geefDrukx(), mb.geefDruky()))
			{
				if (eigenaar.isBouwen() && !(e.isControlKeyDown() || remove))
				{
					if (kv[n].m == 0)
					{
						kr.voegKubusToe(kv[n].i, kv[n].j, kv[n].k + 1);
						if (gr != null)
							gr.verhoog(kv[n].i, kv[n].j);
					}
					if (kv[n].m == 1)
					{
						kr.voegKubusToe(kv[n].i, kv[n].j - 1, kv[n].k);
					}
					if (kv[n].m == 2)
					{
						kr.voegKubusToe(kv[n].i + 1, kv[n].j, kv[n].k);
					}
					if (kv[n].m == 3)
					{
						kr.voegKubusToe(kv[n].i, kv[n].j + 1, kv[n].k);
					}
					if (kv[n].m == 4)
					{
						kr.voegKubusToe(kv[n].i - 1, kv[n].j, kv[n].k);
					}
					if (kv[n].m == 5)
					{
						kr.voegKubusToe(kv[n].i, kv[n].j, kv[n].k - 1);
					}
				}
				else
				{
					kr.verwijderKubus(kv[n].i, kv[n].j, kv[n].k);
					if (gr != null)
						gr.verlaag(kv[n].i, kv[n].j);
				}
				return;
			}
		}
	}

	public void muisDoubleKlikActie(DoubleClickEvent e)
	{
		for (int q = aantalKv - 1; q > -1; q--)
		{
			int n = sorteerRij[q];
			if (kv[n].m == 6 && kv[n].k == 0 && p[kv[n].i][kv[n].j].contains(mb.geefDrukx(), mb.geefDruky()))
			{
				kr.verwijderKubus(kv[n].i, kv[n].j, 0);
				if (gr != null)
					gr.verlaag(kv[n].i, kv[n].j);
				return;
			}
			else if (kv[n].m != 6 && pp[kv[n].i][kv[n].j][kv[n].k][kv[n].m].contains(mb.geefDrukx(), mb.geefDruky()))
			{
				kr.verwijderKubus(kv[n].i, kv[n].j, kv[n].k);
				if (gr != null)
					gr.verlaag(kv[n].i, kv[n].j);
				return;
			}
		}
	}

	public void muisKkActie(boolean remove)
	{
		for (int q = aantalKv - 1; q > -1; q--)
		{
			int n = sorteerRij[q];
			if (kv[n].m == 6 && kv[n].k == 0 && p[kv[n].i][kv[n].j].contains(mb.geefDrukx(), mb.geefDruky()))
			{
				boolean changed = false;
				if (eigenaar.isBouwen() && !(holdMouse || remove))
				{
					changed = kr.voegKubusToe(kv[n].i, kv[n].j, 0);
					if (gr != null)
						gr.verhoog(kv[n].i, kv[n].j);
				}
				else
				{
					changed = kr.verwijderKubus(kv[n].i, kv[n].j, 0);
					if (gr != null)
						gr.verlaag(kv[n].i, kv[n].j);
				}
				tekenOpnieuw();
				if (changed)
					eigenaar.zetVeranderd();
				return;

			}
			else if (kv[n].m != 6 && pp[kv[n].i][kv[n].j][kv[n].k][kv[n].m].contains(mb.geefDrukx(), mb.geefDruky()))
			{
				boolean changed = false;
				if (eigenaar.isBouwen() && !(holdMouse || remove) && !maakAanzicht)
				{
					if (kv[n].m == 0)
					{
						changed = kr.voegKubusToe(kv[n].i, kv[n].j, kv[n].k + 1);
						if (gr != null)
							gr.verhoog(kv[n].i, kv[n].j);
					}
					if (kv[n].m == 1)
					{
						changed = kr.voegKubusToe(kv[n].i, kv[n].j - 1, kv[n].k);
					}
					if (kv[n].m == 2)
					{
						changed = kr.voegKubusToe(kv[n].i + 1, kv[n].j, kv[n].k);
					}
					if (kv[n].m == 3)
					{
						changed = kr.voegKubusToe(kv[n].i, kv[n].j + 1, kv[n].k);
					}
					if (kv[n].m == 4)
					{
						changed = kr.voegKubusToe(kv[n].i - 1, kv[n].j, kv[n].k);
					}
					if (kv[n].m == 5)
					{
						changed = kr.voegKubusToe(kv[n].i, kv[n].j, kv[n].k - 1);
					}
					tekenOpnieuw();
					if (changed)
						eigenaar.zetVeranderd();
				}
				else
				{
					changed = kr.verwijderKubus(kv[n].i, kv[n].j, kv[n].k);
					if (gr != null)
						gr.verlaag(kv[n].i, kv[n].j);
					tekenOpnieuw();
					if (changed)
						eigenaar.zetVeranderd();
				}
				return;
			}
		}
	}

	public void muisDrukActie(MouseDownEvent e)
	{
		if (removing)
			return;
		removing = true;
		//if(cubeRemoveThread!=null)
		{ //cubeRemoveThread.maakDood();
			//cubeRemoveThread=null;
		}
		//cubeRemoveThread = new CubeRemoveThread(e);
		//cubeRemoveThread.start();

	}

	public void muisDrukActie()
	{
		if (removing)
			return;
		removing = true;
		//if(cubeRemoveThread!=null)
		{ //cubeRemoveThread.maakDood();
			//cubeRemoveThread=null;
		}
		//cubeRemoveThread = new CubeRemoveThread(e);
		//cubeRemoveThread.start();
		for (int q = aantalKv - 1; q > -1; q--)
		{
			int n = sorteerRij[q];
			if (kv[n].m != 6 && pp[kv[n].i][kv[n].j][kv[n].k][kv[n].m].contains(mb.geefDrukx(), mb.geefDruky()))
			{
				holdMouse = true;
			}
		}
		holdMouseStartTime = System.currentTimeMillis();
//System.out.println("hmstart " + holdMouseStartTime);
	}

	public void muisKlikActie()
	{
	}

	public void muisLosActie(MouseUpEvent e)
	//public void muisLosActie()
	{
		removing = false;
		if (!removed && (klikAan && (mb.geefDrukx() - mb.geefX()) * (mb.geefDrukx() - mb.geefX()) + (mb.geefDruky() - mb.geefY()) * (mb.geefDruky() - mb.geefY()) < 16))
		{
			muisKkActie(e, false);
			tekenOpnieuw();
			//eigenaar.zetVeranderd();
		}
		removed = false;
	}

	public void muisLosActie()
	{
		holdMouseEndTime = System.currentTimeMillis();
//System.out.println("hmend " + holdMouseEndTime);		
		long holdMouseTime = holdMouseEndTime - holdMouseStartTime; 
		removing = false;
		if (!removed && (klikAan && (mb.geefDrukx() - mb.geefX()) * (mb.geefDrukx() - mb.geefX()) + (mb.geefDruky() - mb.geefY()) * (mb.geefDruky() - mb.geefY()) < 16))
		{
		
			holdMouse = false;
			if (holdMouseTime > 300)
				holdMouse = true;

//System.out.println("holdMouse " + holdMouse);			
			
			muisKkActie(false);
			tekenOpnieuw();
			//eigenaar.zetVeranderd();
		}
		//else if (!removed && (klikAan && (mb.geefDrukx() - mb.geefX()) * (mb.geefDrukx() - mb.geefX()) + (mb.geefDruky() - mb.geefY()) * (mb.geefDruky() - mb.geefY()) > 16 && holdMouse))
		//{
		//	muisKkActie(false);
		//	tekenOpnieuw();
			//eigenaar.zetVeranderd();
		//}
		removed = false;
		holdMouse = false;
		
		eigenaar.ingevuld = true;
	}

	/*
	class CubeRemoveThread extends Thread 
	{	
		final MouseEvent ee;
		boolean dood = false;
		
		public CubeRemoveThread(MouseEvent e)
		{	ee = e;
		}
		
		public void run()
		{	try
			{   sleep(500);
			}
			catch(InterruptedException e)    
			{ }
			if((removing && !dood && klikAan && (mb.geefDrukx()-mb.geefX())*(mb.geefDrukx()-mb.geefX()) + (mb.geefDruky()-mb.geefY())*(mb.geefDruky()-mb.geefY()) < 16) )
			{	removed = true;
				muisKkActie(ee, true);
				eigenaar.zetVeranderd();	
			}
			removing = false;
		}
		public void maakDood()
		{	dood = true;
		}
	}
	*/
}
