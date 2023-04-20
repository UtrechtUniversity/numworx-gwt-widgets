package fi.tegelsleggengwt.client;

//import java.awt.*;

//import javax.swing.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;


public class TekenPanel2 //extends JPanel 
{
	TegelsPanel owner;
	
	public TekenPanel2(TegelsPanel o)
	{	owner = o;
		
	}
	
	
	//public void paintComponent(Graphics g)
	public void paintComponent(Context2d g)
	{
		tekenOpImage(g);
	}
	
  	//public void tekenOpImage(Graphics g)
	public void tekenOpImage(Context2d g)
  	{ 	
  		
  		//g.setColor(Color.white);
  		g.setFillStyle(CssColor.make(255,255,255));
  		//g.setColor(Color.yellow);
    	//g.fillRect(0, 0, owner.breedte, owner.hoogte - owner.controlHoogte - 2); oud in Java
  		g.fillRect(0, 0, owner.breedte, owner.hoogte);
		tekenprogramma(g);
		
		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		//g.drawRect(0, 0, owner.breedte - 1, owner.hoogte - 1);
		g.strokeRect(0, 0, owner.breedte - 1, owner.hoogte);

		// lijn boven control panel 
		//gIm.drawLine(181, owner.hoogte - 62, owner.breedte - 1, owner.hoogte - 62); oud in Java
		if (!owner.demoVersion)
		{	//g.drawLine(owner.hokBreedte + 1, owner.hoogte - owner.controlHoogte - 2, 
			//		   owner.breedte - 1, owner.hoogte - owner.controlHoogte - 2);
			g.beginPath();
			g.moveTo(owner.hokBreedte, owner.hoogte - owner.controlHoogte);
			g.lineTo(owner.breedte, owner.hoogte - owner.controlHoogte);
			g.stroke();
		}	
		
		//super.paint(g);
	}
	
 	void tekenOpnieuw()
	{	
/* 		
 		tekenOpImage();
		Graphics g = getGraphics();
		g.drawImage(im, 0, 0, null);
*/		
 		paint();
	}	
/*	
 	public void update(Graphics g)
 	{
 		paint(g);
 	}
 	
*/	
 	public void paint()
 	{
 		tekenOpImage(owner.tegelsContext2d);
 	}
 	
	//public void tekenprogramma(Graphics g)
 	public void tekenprogramma(Context2d g)
	{	tekenStukken(g);
	
		if (!owner.demoVersion)
		{	
			tekenHok(g);
			if (!owner.maakVorm && owner.basisv != null)
			{	tekenStapel(g);
//System.out.println("tekenStapel");			
			}
		
			if (owner.actiefSs != null)
			{	tekenSs(owner.actiefSs, g);
//System.out.println("tekenSs");			
			}
		
			if (owner.maakVorm)
			{	tekenRoosterHok(g);
				tekenPunten(g);
				tekenLijnen(g);
			}
		}
	}
	
	//void tekenStapel(Graphics g)
 	void tekenStapel(Context2d g)
	{	tekenSs(new SchuifStuk(owner.transVersion, owner.basisv, owner.basisv.positie.x - 3, owner.basisv.positie.y - 3), g);
		if (!owner.transVersion)
			tekenSs(owner.basisv, g);
	}
	
	//void tekenHok(Graphics gIm)
	void tekenHok(Context2d gIm)
	{	//gIm.setColor(Color.lightGray);
		gIm.setFillStyle(CssColor.make(192,192,192));
		gIm.fillRect(0, owner.hoogte - owner.hokBreedte, owner.hokBreedte, owner.hokBreedte);
		
		//gIm.setColor(Color.black);
		gIm.setStrokeStyle(CssColor.make(0,0,0));
		//gIm.drawRect(0, owner.hoogte - owner.hokBreedte - 1, owner.hokBreedte, owner.hokBreedte);
		gIm.strokeRect(0, owner.hoogte - owner.hokBreedte, owner.hokBreedte, owner.hokBreedte);
	}
	
	//void tekenRoosterHok(Graphics gIm)
	void tekenRoosterHok(Context2d gIm)
	{	
		if (owner.transVersion)
		{
			//gIm.setColor(Color.white);
			gIm.setFillStyle(CssColor.make(255,255,255));
			//gIm.fillPolygon(owner.zeshok);
			gIm.beginPath();		
			gIm.moveTo(owner.zeshok.doubleX[0], owner.zeshok.doubleY[0]);
			for (int k = 1; k < owner.zeshok.aantalPunten; k++) 
			{	gIm.lineTo(owner.zeshok.doubleX[k], owner.zeshok.doubleY[k]);
			}
			gIm.lineTo(owner.zeshok.doubleX[0], owner.zeshok.doubleY[0]);
			gIm.closePath();
			gIm.fill();

			//gIm.setColor(Color.black);
			gIm.setStrokeStyle(CssColor.make(0,0,0));
			//gIm.drawPolygon(owner.zeshok);
			gIm.beginPath();		
			gIm.moveTo(owner.zeshok.doubleX[0], owner.zeshok.doubleY[0]);
			for (int k = 1; k < owner.zeshok.aantalPunten; k++) 
			{	gIm.lineTo(owner.zeshok.doubleX[k], owner.zeshok.doubleY[k]);
			}
			gIm.lineTo(owner.zeshok.doubleX[0], owner.zeshok.doubleY[0]);
			gIm.closePath();
			gIm.stroke();
			
			if (owner.basisvOud != null)
			{	//gIm.setColor(owner.basisvOud.kleur);
				gIm.setFillStyle(owner.basisvOud.kleur);
				//gIm.fillPolygon(owner.basisvOud.pol);
				gIm.beginPath();		
				gIm.moveTo(owner.basisvOud.pol.doubleX[0], owner.basisvOud.pol.doubleY[0]);
				for (int k = 1; k < owner.basisvOud.pol.aantalPunten; k++) 
				{	gIm.lineTo(owner.basisvOud.pol.doubleX[k], owner.basisvOud.pol.doubleY[k]);
				}
				gIm.lineTo(owner.basisvOud.pol.doubleX[0], owner.basisvOud.pol.doubleY[0]);
				gIm.closePath();
				gIm.fill();
			}
			
			//gIm.setColor(Color.black);
			gIm.setStrokeStyle(CssColor.make(0,0,0));
			int n = 10 / Trans.factor;
			for (int i = -n; i < n+1; i++)
			{	for (int j = -n; j < n + 1; j++)
				{	int x = Trans.geefx(i, j);
					int y = Trans.geefy(i, j);
					if (Math.abs(i + j) <= n)
					{	//gIm.drawLine(85 + x, owner.hoogte - 85 - y, 85 + x, owner.hoogte - 85 - y);
						gIm.beginPath();
						gIm.moveTo(85 + x, owner.hoogte - 85 - y);
						gIm.lineTo(85 + x+1, owner.hoogte - 85 - y);
						gIm.stroke();
					}
				}
			}
						
		}
		else
		{	
			//gIm.setColor(Color.white);
			gIm.setFillStyle(CssColor.make(255,255,255));
			gIm.fillRect(15, owner.hoogte - 165, 160, 160);
		
			if (owner.basisvOud != null)
			{	//gIm.setColor(owner.basisvOud.kleur);
				gIm.setFillStyle(owner.basisvOud.kleur);
				//gIm.fillPolygon(owner.basisvOud.pol);
				gIm.beginPath();		
				gIm.moveTo(owner.basisvOud.pol.doubleX[0], owner.basisvOud.pol.doubleY[0]);
				for (int k = 1; k < owner.basisvOud.pol.aantalPunten; k++) 
				{	gIm.lineTo(owner.basisvOud.pol.doubleX[k], owner.basisvOud.pol.doubleY[k]);
				}
				gIm.lineTo(owner.basisvOud.pol.doubleX[0], owner.basisvOud.pol.doubleY[0]);
				gIm.closePath();
				gIm.fill();

			}
		
			//gIm.setColor(Color.black);
			gIm.setStrokeStyle(CssColor.make(0,0,0));
			for (int j = 0; j < 9; j++)	
			{	//gIm.drawLine(15, owner.hoogte - 5 - 20 * j, 175, owner.hoogte - 5 - 20 * j);
				gIm.beginPath();
				gIm.moveTo(15, owner.hoogte - 5 - 20 * j);
				gIm.lineTo(175, owner.hoogte - 5 - 20 * j);
				gIm.stroke();
			
			}
			for (int j = 0; j < 9; j++)	
			{	//gIm.drawLine(15 + 20 * j, owner.hoogte - 5, 15 + 20*j, owner.hoogte - 165);
				gIm.beginPath();
				gIm.moveTo(15 + 20 * j, owner.hoogte - 5);
				gIm.lineTo(15 + 20*j, owner.hoogte - 165);
				gIm.stroke();
			}
		
			gIm.setFillStyle(CssColor.make(0,0,0));
			for (int i = 0; i < 9; i++)	
			{	//gIm.drawString(owner.abc[i], 13 + 20 * i, owner.hoogte - 168);
				gIm.fillText(owner.abc[i], 13 + 20 * i, owner.hoogte - 168);
			}
			for (int i = 0; i < 9 ; i++)	
			{	//gIm.drawString(Integer.toString(i + 1), 5, owner.hoogte - 162 + 20 * i);
				gIm.fillText(Integer.toString(i + 1), 5, owner.hoogte - 162 + 20 * i);
			}
		}
	}
	
	//void tekenStukken(Graphics g)
	void tekenStukken(Context2d g)
	{	for(int i = owner.aantalSs - 1; i > -1; i--)
		{	tekenSs(owner.ss[i], g);
		}
	}	
	
	//void tekenSs(SchuifStuk s, Graphics gIm)
	void tekenSs(SchuifStuk s, Context2d gIm)
	{	//gIm.setColor(s.kleur);
		gIm.setFillStyle(s.kleur);
		//gIm.fillPolygon(s.pol);
		gIm.beginPath();	
		
		gIm.moveTo(s.pol.doubleX[0], s.pol.doubleY[0]);
		for (int k = 1; k < s.pol.aantalPunten; k++) 
		{	gIm.lineTo(s.pol.doubleX[k], s.pol.doubleY[k]);
		}
		gIm.lineTo(s.pol.doubleX[0], s.pol.doubleY[0]);
		gIm.closePath();
		gIm.fill();

		//gIm.setColor(Color.black);
		gIm.setStrokeStyle(CssColor.make(0,0,0));
		//gIm.drawPolygon(s.pol);
		gIm.beginPath();		
		gIm.moveTo(s.pol.doubleX[0], s.pol.doubleY[0]);
		for (int k = 1; k < s.pol.aantalPunten; k++) 
		{	gIm.lineTo(s.pol.doubleX[k], s.pol.doubleY[k]);
		}
		gIm.lineTo(s.pol.doubleX[0], s.pol.doubleY[0]);
		gIm.closePath();
		gIm.stroke();

	}
	
	//void tekenPunten(Graphics gIm)
	void tekenPunten(Context2d gIm)
	{	
		gIm.setFillStyle(CssColor.make(0,0,0));
		if (owner.transVersion)
		{	for (int i = 0; i < owner.aantalNieuwHp; i++)
			{	int x = Trans.geefx(owner.nieuwHp[i].x, owner.nieuwHp[i].y);
				int y = Trans.geefy(owner.nieuwHp[i].x, owner.nieuwHp[i].y);
			   
				//gIm.fillOval(owner.posBasis.x + x - 3, owner.posBasis.y + y - 3, 6, 6);
				gIm.beginPath();
	            gIm.arc(owner.posBasis.x + x, owner.posBasis.y + y, 3, 0, 2 * Math.PI);
	            gIm.fill();
			}
		}
		else
		{	
			for (int i = 0; i < owner.aantalNieuwHp; i++)
			{	//gIm.fillOval(owner.posBasis.x + owner.nieuwHp[i].x - 3, 
				//			 owner.posBasis.y + owner.nieuwHp[i].y - 3, 6, 6);
				gIm.beginPath();
				gIm.arc(owner.posBasis.x + owner.nieuwHp[i].x, 
						owner.posBasis.y + owner.nieuwHp[i].y,
						3, 0, 2 * Math.PI);
				gIm.fill();
			}
		}
	}
/*	
	void vermenigvuldigPunten(double factor)
	{	for (int i = 0; i < aantalNieuwHp; i++)
		{	nieuwHp[i].x *= factor;
			nieuwHp[i].y *= factor;
		}
		basisv = new SchuifStuk(aantalNieuwHp, nieuwHp, posBasis, Color.red);
	}
*/	
	//void tekenLijnen(Graphics gIm)
	void tekenLijnen(Context2d gIm)
	{	
		if (owner.transVersion)
		{	if (owner.aantalNieuwHp > 1)
			{	//gIm.setColor(Color.red);
				gIm.setStrokeStyle(CssColor.make(255,0,0));
				for (int i = 0; i < owner.aantalNieuwHp - 1; i++)
				{	int x = Trans.geefx(owner.nieuwHp[i].x, owner.nieuwHp[i].y);
					int y = Trans.geefy(owner.nieuwHp[i].x, owner.nieuwHp[i].y);
					int xn = Trans.geefx(owner.nieuwHp[i + 1].x, owner.nieuwHp[i + 1].y);
					int yn = Trans.geefy(owner.nieuwHp[i + 1].x, owner.nieuwHp[i + 1].y);
					//gIm.drawLine(owner.posBasis.x + xn , owner.posBasis.y + yn ,
					//		 owner.posBasis.x + x, owner.posBasis.y + y);
					gIm.beginPath();
					gIm.moveTo(owner.posBasis.x + xn , owner.posBasis.y + yn);
					gIm.lineTo(owner.posBasis.x + x, owner.posBasis.y + y);
					gIm.stroke();
				}
				//gIm.setColor(Color.black);
				gIm.setStrokeStyle(CssColor.make(0,0,0));
			}
			
		}
		else
		{
			if (owner.aantalNieuwHp > 1)
			{	//gIm.setColor(Color.red);
				gIm.setStrokeStyle(CssColor.make(255,0,0));
				for (int i = 0; i < owner.aantalNieuwHp - 1; i++)
				{	//gIm.drawLine(owner.posBasis.x + owner.nieuwHp[i + 1].x , 
					//			 owner.posBasis.y + owner.nieuwHp[i + 1].y ,
					//			 owner.posBasis.x + owner.nieuwHp[i].x, 
					//			 owner.posBasis.y + owner.nieuwHp[i].y);
					gIm.beginPath();
					gIm.moveTo(owner.posBasis.x + owner.nieuwHp[i + 1].x , 
							   owner.posBasis.y + owner.nieuwHp[i + 1].y);
					gIm.lineTo(owner.posBasis.x + owner.nieuwHp[i].x, 
							   owner.posBasis.y + owner.nieuwHp[i].y);
					gIm.stroke();
				}
				//gIm.setColor(Color.black);
				gIm.setStrokeStyle(CssColor.make(0,0,0));
			}
		}
	}
	
}
