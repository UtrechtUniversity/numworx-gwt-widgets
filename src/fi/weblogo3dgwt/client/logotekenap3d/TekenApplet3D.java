package fi.weblogo3dgwt.client.logotekenap3d;					 

//import java.awt.*;
//import java.awt.event.*;
//import java.applet.Applet;
//import javax.swing.JPanel;

//import fi.javalogoweb3d.JavaLogoInteractiePanel;
import fi.weblogo3dgwt.client.WebLogo3dGWT;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.canvas.client.Canvas;

public class TekenApplet3D extends LayoutPanel 
{
//	private Regelaar rg;
	public Tekenblad3D tb;
	//private AnimatieBeheerder ab;
	private MuisBeheerder mb;
	
	//JavaLogoInteractiePanel eigenaar;
	WebLogo3dGWT eigenaar;
	int breedte, hoogte;
			  
	public TekenApplet3D(WebLogo3dGWT wl3g, int w, int h)
	{
		eigenaar = wl3g;
		breedte = w;
		hoogte = h;
		init();
		
	}
	//-----------------------------------------------------------------------------------------
	// initalisatie
	//-----------------------------------------------------------------------------------------
	public void init()
	{	tb = new Tekenblad3D(this, breedte, hoogte);
		if (tb.tekenbladCanvas != null)
		{
			add(tb.tekenbladCanvas);
			setWidgetLeftWidth(tb.tekenbladCanvas, 0, Style.Unit.PX, tb.breedte, Style.Unit.PX);
			setWidgetTopHeight(tb.tekenbladCanvas, 0, Style.Unit.PX, tb.hoogte, Style.Unit.PX);

		}
//		rg = new Regelaar(this);
		//this.setLayout(new BorderLayout(0,0));
//		setLayout(null);
		initialiseer();
		maakMuisActieMogelijk();
		//add(tb); //,"Center");
//		add(rg,"East");
		
		//if (mb != null && ab != null)				
		//{	mb.meldAnimatieBeheerder(ab);		
		//}										
	}	
	
	public void initContext2d()
	{
		tb.initContext2d();
	}
	
	public Canvas getCanvas()
	{
		return tb.tekenbladCanvas;
	}
	public void stop()
	{	//if(animatieLopend())onderbreekAnimatie();		
	}
	//-------------------------------------------------------------------------------------------
	//deze methoden kunnen alleen worden gebruikt in  "initialiseer()" van leerling-applet
	//-------------------------------------------------------------------------------------------
	public void maakAnimatieMogelijk()
	{	//ab = new AnimatieBeheerder(this);
		//add(ab,"North");
	}
	
	public void maakMuisActieMogelijk()
	{	mb = new MuisBeheerder(this);
		tb.tekenbladCanvas.addMouseDownHandler(mb);
		tb.tekenbladCanvas.addMouseUpHandler(mb);
		tb.tekenbladCanvas.addMouseMoveHandler(mb);
		tb.tekenbladCanvas.addTouchStartHandler(mb);
		tb.tekenbladCanvas.addTouchEndHandler(mb);
		tb.tekenbladCanvas.addTouchMoveHandler(mb);

		//tb.addMouseListener(mb);
		//tb.addMouseMotionListener(mb);
	}
/*	
	public void maakZichtbaar(Component c)
	{	rg.maakZichtbaar(c);
	} 
*/	
	
	public MuisBeheerder geefMuisBeheerder()
	{	return mb;
	}
	
	//public AnimatieBeheerder geefAnimatieBeheerder()
	//{	return ab;
	//}
	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt in de muishandler en  doorgegeven aan MuisBeheerder mb
	//-------------------------------------------------------------------------------------------
	public int geefSleepdx()
	{	return mb.geefSleepdx();
	}
	public int geefSleepdy()
	{	return mb.geefSleepdy();
	}
	public int geefDrukx()
	{	return mb.geefDrukx();
	}
	public int geefDruky()
	{	return mb.geefDruky();
	}
	public int geefX()
	{	return mb.geefX();
	}
	public int geefY()
	{	return mb.geefY();
	}


	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt in de animatiehandler en doorgegeven aan AnimatieBeheerder ab
	//-------------------------------------------------------------------------------------------
//GWT?	
	//public void pauze(int millisec){ab.pauze(millisec);}
	//public boolean animatieLopend(){if (ab!=null)return ab.animatieLopend();else return false;}
	//public void onderbreekAnimatie(){ab.onderbreekAnimatie();}
	//public void beginAnimatie(){ab.beginAnimatie();}
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt in de animatiehandler en muishandlers en doogegeven aan 
	//Tekenblad
	//-------------------------------------------------------------------------------------------
	public void tekenOpnieuw()
	{	tb.tekenOpnieuw();
	}
	public void tekenErbij()
	{	tb.tekenErbij();
	}
	public void paintDrawing(boolean cursor)
	{	tb.paintDrawing(cursor);
	}
	public void initializeDrawing(boolean cursor)
	{	tb.initializeDrawing(cursor);
	}
	
	public void zetCursorAan(boolean b)
	{	tb.zetCursorAan(b);
	}

	public void zetTransparant(boolean b)
	{	tb.zetTransparant(b);
	}

	public void zetDraadFiguur(boolean b)
	{	tb.zetDraadFiguur(b);
	}
	
	public void zoomIn()
	{
		tb.zoomIn();
	}
	public void zoomUit()
	{
		tb.zoomUit();
	}
	
	public void zoom(double fac)
	{
		tb.zoom(fac);
	}

	public double geefDraaiX()
	{
		return tb.geefDraaiX();
	}

	public double geefDraaiY()
	{
		return tb.geefDraaiY();
	}

	public void zetBeginHoeken(double hx, double hy)
	{	tb.zetBeginHoeken(hx,hy);
	}

	public void paintTekenblad()
	{
		tb.paintTekenblad();
	}

	
  	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt in "initialiseer" en doorgegeven aan Tekenblad tb (of 
	//Matrix2d) 
	//-------------------------------------------------------------------------------------------
	public void schaal(double s){tb.mat.schaal(s);}
	public void achtergrondkleur(String kl){tb.achtergrondkleur(kl);}
	public void achtergrondkleur(int r, int g, int b){tb.achtergrondkleur(r, g, b);}
	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt in "tekenprogramma()" en doorgegeven aan Tekenblad tb (of 
	//Matrix2d) 
	//-------------------------------------------------------------------------------------------
	public void xdraai(double dh){tb.mat.xdraai(dh);}
	public void ydraai(double dh){tb.mat.ydraai(dh);}
	public void zdraai(double dh){tb.mat.zdraai(dh);}
	public void rechts(double dh){tb.mat.zdraai(-dh);}
	public void links(double dh){tb.mat.zdraai(dh);}
	public void stapy(double dy){tb.naarVolgendPunt(0,-dy,0);}
	public void vooruit(double dy){tb.naarVolgendPunt(0,-dy,0);}
	public void stapx(double dx){tb.naarVolgendPunt(dx,0,0);}
	public void stapz(double dz){tb.naarVolgendPunt(0,0,-dz);}
	public void stap(double dx,double dy,double dz){tb.naarVolgendPunt(dx,-dy,-dz);}
	public void stap(double dx,double dy){tb.naarVolgendPunt(dx,-dy,0);}
	public void penAan(){tb.penAan();}
	public void penAan(String kl){tb.penAan(kl);}
	public void penAan(int r, int g, int b){tb.penAan(r, g, b);}
	public void penUit(){tb.penUit();}
	public void vulAan(){tb.vulAan();}
	public void vulAan(String kl){tb.vulAan(kl);}
	public void vulAan(int r, int g, int b){tb.vulAan(r, g, b);}
	public void vulUit(){tb.vulUit();}
	public Polygon geefVlak(){return tb.geefVlak();}
	public Punt3D geefBeginpunt()
	{	return tb.geefBeginpunt();
	}
	
// deze zijn niet geimplementeerd
	public void print(String s){}
	public void printl(String s){}
	public void vulBlad(int r, int g, int b){}
	
	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden geimplemeteerd in het leerlingenprogramma
	//-------------------------------------------------------------------------------------------
	public void tekenprogramma(){}
	public void initialiseer(){}
	public void animatie(){}
	public void muisSleepActie()
	{	tb.muisSleepActie();
	}
	public void muisDrukActie(){}
	public void muisLosActie(){}
	//public void invoerVarActie(InvoerVariabele iv){}
	//public void schuifInvoerVarActie(SchuifInvoerVariabele iv){}
}		
	
/*
class Regelaar extends Panel
{	
	private TekenApplet3D eigenaar;
	private GridBagLayout gridbag;
	private GridBagConstraints c;
	private int aantalComponenten;
	private int maxAantalComponenten;
	private Component[] componenten;
	
	public Regelaar(TekenApplet3D ap)
	{	setBackground(Color.lightGray);
		eigenaar = ap;
		maxAantalComponenten = 10;
		componenten = new Component[maxAantalComponenten];
		aantalComponenten = 0;
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		setLayout(gridbag);
		c.insets = new Insets(10, 10, 10, 10); 			
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weighty = 0.0;
		c.weightx = 0.0;
	}	
	//-----------------------------------------------------------------------------------------
	// nieuwe InvoerVariabelen worden hier op het panel geplaatst 
	//-----------------------------------------------------------------------------------------

	public void maakZichtbaar(Component com)
	{	if(com instanceof InvoerVar)
		{	((InvoerVar)com).zetBaas(eigenaar);
		}
		componenten[aantalComponenten] = com;
		aantalComponenten++;
		gridbag.setConstraints(com, c);
		add(com);
	}
	
}
*/

