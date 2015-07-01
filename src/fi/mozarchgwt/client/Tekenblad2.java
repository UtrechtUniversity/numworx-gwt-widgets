package fi.mozarchgwt.client;

//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.Polygon;

//import javax.swing.JPanel;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;


class Tekenblad2 //extends JPanel
{
	private int breedte,hoogte;
	private Punt beginpunt,eindpunt,startpunt;
  	private Polygon veelvlak;
  	//private Graphics gIm ;
  	public Context2d gIm ;
	public Matrix2D mat;  
	private TekenPanel eigenaar;
	private boolean pen, vul;
  	private CssColor penkleur, vulkleur, achtergrondkleur;
	  
	public Tekenblad2(TekenPanel ap, int b, int h)
	{	
		//setLayout(null);
		
		breedte = b;
		hoogte = h;
		
		//achtergrondkleur = Color.white;
		achtergrondkleur = CssColor.make(255,255,255);
		veelvlak = new Polygon();
		eigenaar = ap;
		mat = new Matrix2D();					// zorgt voor de tekenrichting
		
		gIm = eigenaar.gIm;
	}
	
	public void teken()
	{
		paintComponent(gIm);
	}
	//public void paintComponent(Graphics g)
	public void paintComponent(Context2d g)
	{	//breedte = getSize().width;
		//hoogte = getSize().height;
		startpunt = new Punt(breedte / 2, hoogte / 2);

		gIm = g;
		tekenOpImage(g);
	}
  	
	//public void tekenOpImage(Graphics g)
	public void tekenOpImage(Context2d g)
  	{ 	beginpunt = new Punt(startpunt);
    	eindpunt = new Punt(beginpunt);
    	mat.initialiseer();
	  	
    	if (achtergrondkleur != null)
    	{	//g.setColor(achtergrondkleur);
    		g.setFillStyle(achtergrondkleur);
    	}
    	g.fillRect(0, 0, breedte, hoogte);

	  	penAan(0, 0, 0);
		vul = false;
    	vulkleur = CssColor.make(0,0,0);
    	eigenaar.tekenprogramma();
    	
	}
	public void zetStart()
	{	beginpunt = new Punt(startpunt);
    	eindpunt = new Punt(beginpunt);
	}
	public void zetStartPunt(int px, int py)
	{	startpunt = new Punt(px, py);
	}

	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door handlers van het leerlingprogramma
	//-------------------------------------------------------------------------------------------
	
 	void tekenOpnieuw()
	{	
 		//repaint();
 		eigenaar.tekenprogramma();
	}

 	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door het Tekenblad om de lijnen en vlakken te tekenen
	//-------------------------------------------------------------------------------------------
	void naarVolgendPunt(double dx, double dy)
	{	eindpunt = mat.geefVolgendPunt(beginpunt, dx, dy);
		if (pen)
		{	//gIm.drawLine((int) beginpunt.x,(int) beginpunt.y,(int) eindpunt.x,(int) eindpunt.y);
			gIm.beginPath();
			gIm.moveTo(beginpunt.x,beginpunt.y);
			gIm.lineTo(eindpunt.x,eindpunt.y);
			gIm.stroke();
		}
		if (vul)
			veelvlak.addPoint((int) beginpunt.x,(int) beginpunt.y);
		beginpunt.x = eindpunt.x;
		beginpunt.y = eindpunt.y;
	}
	
	void tekenPolygon()
	{	//gIm.setColor(vulkleur);
		gIm.setFillStyle(vulkleur);
		//gIm.fillPolygon(veelvlak);
		gIm.beginPath();		
		gIm.moveTo(veelvlak.doubleX[0], veelvlak.doubleY[0]);
		for (int k = 1; k < veelvlak.aantalPunten; k++) 
		{	gIm.lineTo(veelvlak.doubleX[k], veelvlak.doubleY[k]);
		}
		gIm.lineTo(veelvlak.doubleX[0], veelvlak.doubleY[0]);
		gIm.closePath();
		gIm.fill();
		
		
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
		if (pen)
		{	//gIm.drawPolygon(veelvlak);
			gIm.beginPath();		
			gIm.moveTo(veelvlak.doubleX[0], veelvlak.doubleY[0]);
			for (int k = 1; k < veelvlak.aantalPunten; k++) 
			{	gIm.lineTo(veelvlak.doubleX[k], veelvlak.doubleY[k]);
			}
			gIm.lineTo(veelvlak.doubleX[0], veelvlak.doubleY[0]);
			gIm.closePath();
			gIm.stroke();

		}
	}
	
 	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt in "tekenprogramma()" 
	//-------------------------------------------------------------------------------------------
	void penAan()
	{	pen = true;
	}
	void penAan(String kl)
	{	pen = true;
		penkleur = maakKleur(kl);
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
	}
	void penAan(CssColor kl)
	{	pen = true;
		penkleur = kl;
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
	}
	void penAan(int r, int g, int b)
	{	pen = true;
		penkleur = CssColor.make(r, g, b);
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
	}
	void penUit()
	{	pen = false;
	}
	
	void zetVul(boolean b)
	{	vul = b;
	}
	void vulAan()
	{	vul = true;
		veelvlak = new Polygon();
	}
	void vulAan(String kl)
	{	vul = true;	
		vulkleur = maakKleur(kl);
		veelvlak = new Polygon();
	}
	void vulAan(CssColor kl)
	{	vul = true;	
		vulkleur = kl;
		veelvlak = new Polygon();
	}
	void vulAan(int r, int g, int b)
	{	vul = true;	
		//vulkleur = new Color(r, g, b);
		vulkleur = CssColor.make(r, g, b);
		veelvlak = new Polygon();
	}
	void vulUit()
	{	tekenPolygon();
		vul = false;
	}
	void achtergrondkleur(String kl)
	{	achtergrondkleur = maakKleur(kl);
	}
	void achtergrondkleur(int r, int g, int b)
	{	//achtergrondkleur = new Color(r, g, b);
		achtergrondkleur = CssColor.make(r, g, b);
	}
	Polygon geefVlak()							// geeft de laatst getekende Polygon
	{	return veelvlak;
	}
	Punt geefPunt()								// geeft de laatst getekende Punt
	{	return beginpunt;
	}
	//void schrijf(String s)
	//{	gIm.drawString(s, (int) beginpunt.x, (int) beginpunt.y);
	//}
 	//-------------------------------------------------------------------------------------------
	//deze methode wordt gebruikt een kleur in de vorm van een string om te zetten in een Color
	//-------------------------------------------------------------------------------------------
	private CssColor maakKleur(String kl)
	{	if(kl.equals("rood")) 
			return CssColor.make(255,0,0);
		else if(kl.equals("groen")) 
			return CssColor.make(0,255,0);
		else if(kl.equals("blauw")) 
			return CssColor.make(0,0,255);
		else if(kl.equals("geel")) 
			return CssColor.make(255, 255, 0);
		else if(kl.equals("cyaan")) 
			return CssColor.make(0, 255, 255);
		else if(kl.equals("roze")) 
			return CssColor.make(255,20,147);
		else if(kl.equals("zwart")) 
			return CssColor.make(0,0,0);
		else if(kl.equals("grijs")) 
			return CssColor.make(192, 192, 192);
		else if(kl.equals("lichtgrijs")) 
			return CssColor.make(220, 220, 220);
		else if(kl.equals("magenta")) 
			return CssColor.make(255, 0, 255);
		else if(kl.equals("wit")) 
			return CssColor.make(255,255,255);
		else if(kl.equals("oranje")) 
			return CssColor.make(255, 127, 0);
		else 
			return CssColor.make(0,0,0);		
	}	
}
