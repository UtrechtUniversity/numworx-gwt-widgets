package fi.tekenveelvlakgwt.client;					 

//import java.awt.*;
//import java.awt.event.*;
//import java.applet.Applet;

//import javax.swing.JPanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;


public class TekenApplet3D extends LayoutPanel
{
	Regelaar rg;
	Tekenblad3D tb;
	//AnimatieBeheerder ab;
	private MuisBeheerder mb;
	//KleurKiezer kk;
		  
	//-----------------------------------------------------------------------------------------
	// initalisatie
	//-----------------------------------------------------------------------------------------
	
	//public void init()
	public TekenApplet3D(int b, int h)
	{	
		//this.setLayout(null);
		
		tb = new Tekenblad3D(this, b - 130, h);
		add(tb.canvas);
		setWidgetLeftWidth(tb.canvas, 0, Style.Unit.PX, b-130, Style.Unit.PX);
		setWidgetTopHeight(tb.canvas, 0, Style.Unit.PX, h, Style.Unit.PX);

		tb.canvas.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.canvas());
		
		rg = new Regelaar(this);
		add(rg);
		setWidgetLeftWidth(rg, b - 130, Style.Unit.PX, 130, Style.Unit.PX);
		setWidgetTopHeight(rg, 0, Style.Unit.PX, h, Style.Unit.PX);
		
		rg.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.regelaar());
		
		// canvas gebruiken
		//tb.setBounds(0,0,getSize().width-150,getSize().height);
		//add(tb,0);
		//rg.setBounds(getSize().width-140,0,130,getSize().height);
		//add(rg,0);

		mb = new MuisBeheerder(this);
		tb.canvas.addMouseDownHandler(mb);
		tb.canvas.addMouseUpHandler(mb);
		tb.canvas.addMouseMoveHandler(mb);
		tb.canvas.addTouchStartHandler(mb);
		tb.canvas.addTouchEndHandler(mb);
		tb.canvas.addTouchMoveHandler(mb);
		
		//tbVb = new Tekenblad3D(this);
		//tbVb.setBounds(5,10,180,180);
		//add(tbVb);
		
		
		
		//this.setLayout(new BorderLayout(0,0));
		//initialiseer();
		//add(tb,"Center");
		//add(rg,"East");
		//add(kk,"South");
		
		
		//if(mb!=null && ab!=null)				
		//{	mb.meldAnimatieBeheerder(ab);		
		//}	
		
	}	

/*	
	public void setBounds(int x, int y, int b, int h)
	{
		super.setBounds(x,y,b,h);
		if (tb!=null)
		{	tb.setBounds(0,0,getSize().width-150,getSize().height);
		
		}
		
		//if (rg!=null)
		//{	rg.setBounds(getSize().width-140,0,130,getSize().height);
		//
		//}
	}
*/		
//	public void stop()
//	{	if(animatieStatus())onderbreekAnimatie();		
//	}
	
	//-------------------------------------------------------------------------------------------
	//deze methoden kunnen alleen worden gebruikt in  "initialiseer()" van leerling-applet
	//-------------------------------------------------------------------------------------------
	public void maakAnimatieMogelijk()
	{	//ab = new AnimatieBeheerder(this);
		//add(ab,"North");
	}
	
	public void maakMuisActieMogelijk()
	{	mb = new MuisBeheerder(this);
		//tb.addMouseListener(mb);
		//tb.addMouseMotionListener(mb);
	}
	//public InvoerVariabele nieuweInvoerVariabele(String n, double mn, double mx, double val)
	//{	return rg.nieuweInvoerVariabele( n,  mn,  mx,  val);
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
	public int geefDruky(){	return mb.geefDruky();
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
	//public void pauze(int millisec){ab.pauze(millisec);}
	//public boolean animatieStatus(){if (ab!=null)return ab.animatieStatus();else return false;}
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
	public void penAan(int n){tb.penAan(n);}
	public void penAan(int n,String kl){tb.penAan(n,kl);}
	public void penAan(int n,int r, int g, int b){tb.penAan(n,r, g, b);}
	public void penUit(){tb.penUit();}
	public void penUit(int n){tb.penUit(n);}
	public void vulAan(){tb.vulAan();}
	public void vulAan(String kl){tb.vulAan(kl);}
	public void vulAan(int r, int g, int b){tb.vulAan(r, g, b);}
	public void vulAan(int n){tb.vulAan(n);}
	public void vulAan(int n,String kl){tb.vulAan(n,kl);}
	public void vulAan(int n,int r, int g, int b){tb.vulAan(n,r, g, b);}
	public void vulAan(CssColor kl){tb.vulAan(kl);	}
	public void vulUit(){tb.vulUit();}
	public void vulUit(int n){tb.vulUit(n);}
	//public void schrijf(String s){tb.schrijf(s);}
	//public void schrijf(String s, Font f){tb.schrijf(s,f);}
	public Polygon geefVlak(){return tb.geefVlak();}
	public Punt geefPunt(){return tb.geefPunt();}
	public Punt geefPunt(int n){return tb.geefPunt(n);}
	public void zetAfstand(double afst){tb.zetAfstand(afst);}
	public void zetSchaduw(boolean b){tb.zetSchaduw(b);}

	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden geimplemeteerd in het leerlingenprogramma
	//-------------------------------------------------------------------------------------------
	public void tekenprogramma(){}
	public void initialiseer(){}
	public void animatie(){}
	public void muisSleepActie(){}
	public void muisDrukActie(){}
	public void muisKlikActie(){}
	public void muisLosActie(){}
	//public void invoerVarActie(InvoerVariabele iv){}
}		
	
    
class Punt3D

{	double x, y, z;
		
	Punt3D(double x, double y,double z)
	{	this.x = x;
		this.y = y;
		this.z = z;
	}
		
	Punt3D(Punt3D p)
	{	this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}
}




class Regelaar extends LayoutPanel
{	
	private TekenApplet3D eigenaar;
	//public GridBagLayout gridbag;
	//public GridBagConstraints c;
	
	public Regelaar(TekenApplet3D ap)
	{	eigenaar = ap;
		//setLayout(null);
	}	
	//public void paint(Graphics g)
	//{	super.paint(g);
	//}	
}

class Tekenblad3D //extends JPanel
{
	Canvas canvas;
	public int breedte,hoogte;
	private Punt3D beginpunt,eindpunt,startpunt;
  	public Lichaam3D[] l;
  	//private Image im ;
  	//public Graphics gIm ;
  	public Context2d gIm;
  	public Matrix3D mat;  
	private TekenApplet3D eigenaar;
	private boolean pen, vul,leeg, schaduw;
	private int lnummer;
  	private CssColor penkleur,vulkleur,achtergrondkleur;
	public boolean bezigMetTekenen;
	private double afstand;
	
	int[] sorteerRij;
	
	MuisBeheerder mb;
	
	public Tekenblad3D(TekenApplet3D ap, int b, int h)
	{	
		breedte = b;
		hoogte = h;
		
		// maak hier een Canvas met listeners
		canvas = Canvas.createIfSupported();
		canvas.setWidth(b + "px");
		canvas.setHeight(h + "px");
		canvas.setCoordinateSpaceWidth(b);
		canvas.setCoordinateSpaceHeight(h);
		
		gIm = canvas.getContext2d();
		
		achtergrondkleur = CssColor.make(255,255,255);
		leeg = false;
		schaduw = true;
		l = new Lichaam3D[5];
		afstand = 1000;
		lnummer=0;
		for(int i=0 ; i<5 ; i++)
		{	l[i] = new Lichaam3D();
			l[i].zetAfstand(afstand);
		}
		sorteerRij = new int[200];
		eigenaar = ap;
		mat = new Matrix3D();
		
	}
	
	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door het Tekenblad: om de image te initialiseren en
	//op het scherm te zetten. "paint()" wordt alleen bij de eerste keer tekenen gebruikt, daarna 
	//zorgt "tekenOpnieuw()" of "tekenErbij()" hiervoor. "TekenOpImage()" zorgt voor het vullen 
	//van de image, metbehulp van het door de leerlingen geimplementeerde "tekenprogramma()",
	//en wordt zowel door "paint()" als door "tekenOpImage()" gebruikt
	//-------------------------------------------------------------------------------------------
	
	public void paint()
	{
		paintComponent(gIm);
	}
	
	//public void paintComponent(Graphics g)
	public void paintComponent(Context2d g)
  	{ 	
		
//System.out.println("tb paint");

		gIm = g;
		
		bezigMetTekenen = true;
//		if(im==null)
//		{	
			//breedte = getSize().width;
			//hoogte = getSize().height;
			
//System.out.println("b = " + breedte);
//System.out.println("h = " + hoogte);
			
			//double startschaal = 1;
			//double startschaal = Math.min((double)breedte/500,(double)hoogte/500);
			//mat.initialiseer(0,0,0,startschaal);	
			startpunt = new Punt3D(breedte/2,hoogte/2,0);
			for(int i=0 ; i<5 ; i++)
			{	l[i].maakNulpunt(breedte/2,hoogte/2,0);
			}
			//im = createImage(breedte,hoogte);
  			//gIm = im.getGraphics();
			//tekenOpImage(true, g);
//		}
    	//g.drawImage(im, 0, 0, null);
		
		tekenOpImage(true, g);
		
		bezigMetTekenen = false;
  	}
	public void zetAfstand(double afst)
	{	afstand = afst;
		for(int i=0 ; i<5 ; i++)
		{	l[i].zetAfstand(afst);
		}
	}
	public void zetSchaduw(boolean s)
	{	schaduw = s;
	}

  	public void tekenOpImage(boolean wis, Context2d g)
  	{ 	startpunt = new Punt3D(breedte/2,hoogte/2,0);
  		
  		beginpunt = new Punt3D(startpunt);
    	eindpunt = new Punt3D(beginpunt);
		//mat.initialiseer();
	  	//gIm.setColor(achtergrondkleur);
    	gIm.setFillStyle(achtergrondkleur);
    	if (wis)
    		gIm.fillRect(0, 0, breedte, hoogte);
		//gIm.setColor(Color.black);
		//gIm.drawRect(0,0,breedte-1, hoogte-1);
    	penAan(0,0,0);
		vul = false;
    	eigenaar.tekenprogramma();
		for(int i=0 ; i<5 ; i++)
		{	l[i].sorteer();
		}
		for(int i=0 ; i<200 ; i++)
		{	sorteerRij[i] = l[1].sorteerRij[i];
		}			
		for(int j=0 ; j<5 ; j++)
		{
//System.out.println("tv lich = " + j + " ap = " + l[j].aantalPolygonen);

			for(int i=0 ; i<l[j].aantalPolygonen ; i++)
			{
				if (l[j].vlakken[i].normaal.z > 0)
				{	if (schaduw)
					{	double grijsfactor = 0.5*((-l[j].vlakken[i].normaal.x - l[j].vlakken[i].normaal.y + l[j].vlakken[i].normaal.z)/Math.sqrt(3)+1);
						if (grijsfactor < 0)
							grijsfactor = 0;
						if (grijsfactor > 1)
							grijsfactor = 1;
						
					    String vString = l[j].vlakken[i].vulkleur.toString().substring(4, l[j].vlakken[i].vulkleur.toString().length() - 1);
						String[] kleurenStr = StringUtils.split(vString,",");

						int fBlue =  Integer.parseInt(kleurenStr[2]);
						int fGreen = Integer.parseInt(kleurenStr[1]);
						int fRed =   Integer.parseInt(kleurenStr[0]);

						
						int roodwaarde = 50+(int)(fRed*grijsfactor*0.75);
						int groenwaarde = 50+(int)(fGreen*grijsfactor*0.75);
						int blauwwaarde = 50+(int)(fBlue*grijsfactor*0.75);
						//gIm.setColor(new Color(roodwaarde,groenwaarde,blauwwaarde));
						gIm.setFillStyle(CssColor.make(roodwaarde,groenwaarde,blauwwaarde));
						
//System.out.println("tv schaduw");						
					}
					else
					{	//gIm.setColor(l[j].vlakken[i].vulkleur);
						gIm.setFillStyle(l[j].vlakken[i].vulkleur);
					
					}
					if(!l[j].vlakken[i].isLeeg)
					{	
						//gIm.fillPolygon(l[j].vlakken[i].pol);
			        	g.moveTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						g.beginPath();
						for (int k = 1; k < l[j].vlakken[i].pol.aantalPunten; k++)
						{	g.lineTo(l[j].vlakken[i].pol.puntenX[k], l[j].vlakken[i].pol.puntenY[k]);
						}
						g.lineTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						g.closePath();
						g.fill();

					}	
					//gIm.setColor(l[j].vlakken[i].lijnkleur);
					gIm.setStrokeStyle(l[j].vlakken[i].lijnkleur);
					if (!l[j].vlakken[i].isLijn && l[j].vlakken[i].isOmlijnd )
					{	//gIm.setColor(l[j].vlakken[i].lijnkleur);
						gIm.setStrokeStyle(l[j].vlakken[i].lijnkleur);
						//gIm.drawPolygon(l[j].vlakken[i].pol);
			        	g.moveTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						g.beginPath();
						for (int k = 1; k < l[j].vlakken[i].pol.aantalPunten; k++)
						{	g.lineTo(l[j].vlakken[i].pol.puntenX[k], l[j].vlakken[i].pol.puntenY[k]);
						}
						g.lineTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						g.closePath();
						g.stroke();

					}
					if (l[j].vlakken[i].isLijn)
					{	//int grw = (int)(125-0.7*l[j].vlakken[i].gemz);
						//gIm.setColor(new Color(grw,grw,grw));
						//gIm.setColor(l[j].vlakken[i].lijnkleur);
						gIm.setStrokeStyle(l[j].vlakken[i].lijnkleur);
						//gIm.drawPolygon(l[j].vlakken[i].pol);
			        	g.moveTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						g.beginPath();
						for (int k = 1; k < l[j].vlakken[i].pol.aantalPunten; k++)
						{	g.lineTo(l[j].vlakken[i].pol.puntenX[k], l[j].vlakken[i].pol.puntenY[k]);
						}
						g.lineTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						g.closePath();
						g.stroke();

						penkleur = CssColor.make(0,0,0);
					}
				}
			}
			l[j] = new Lichaam3D();	
			l[j].zetAfstand(afstand);
			l[j].maakNulpunt(breedte/2,hoogte/2,0);
		}
	}
	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door handlers van het leerlingprogramma
	//-------------------------------------------------------------------------------------------
	void tekenOpnieuw()
	{	
		
		paint();
		
		
		////if(im==null)return;
		//bezigMetTekenen = true;
		//tekenOpImage(true, gIm);
		//Graphics g = getGraphics();
		//g.drawImage(im, 0, 0, null); 
		//bezigMetTekenen = false;
		
	}
  
  	void tekenErbij()
	{	
  		paint();
  		
  		
  		//bezigMetTekenen = true;
		//tekenOpImage(false,gIm);
		//Graphics g = getGraphics();
		//g.drawImage(im, 0, 0, null);
		//bezigMetTekenen = false;
		
	}

	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door het Tekenblad om de lijnen en vlakken te tekenen
	//-------------------------------------------------------------------------------------------

	void naarVolgendPunt(double dx,double dy, double dz)
	{	eindpunt = mat.geefVolgendPunt(beginpunt,dx,dy,dz);
		//double grw = (beginpunt.z + eindpunt.z)/2;
		//penkleur((int)(125-0.7*grw),(int)(125-0.7*grw),(int)(125-0.7*grw));
		//if(tg.pen && !tg.vul)gIm.drawLine((int)beginpunt.x,(int)beginpunt.y,(int)eindpunt.x,(int)eindpunt.y);
		
		if(pen && !vul)
		{	l[lnummer].voegPuntToe(beginpunt);
			l[lnummer].voegPuntToe(eindpunt);
			l[lnummer].voegPolygonToe(penkleur,penkleur,true, false);
		}
		//if(vul && lnummer==0)		 
		//{	l[0].voegPuntToe(beginpunt);
		//}
		if(vul)  //&&lnummer!=0		 
		{	l[lnummer].voegPuntToe(beginpunt);
		}
		beginpunt.x = eindpunt.x;
		beginpunt.y = eindpunt.y;
		beginpunt.z = eindpunt.z;
	}
	
	void tekenPolygon()
	{	l[0].voegPolygonToe(vulkleur, penkleur, pen, leeg);
	}
	void tekenPolygon(int n)
	{	l[n].voegPolygonToe(vulkleur, penkleur, pen, leeg);
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
	void penAan(int r, int g, int b)
	{	pen = true;
		penkleur = CssColor.make(r,g,b);
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
	}
	void penAan(int n)
	{	pen = true;
		lnummer=n;
	}
	void penAan(int n,String kl)
	{	pen = true;
		penkleur = maakKleur(kl);
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
		lnummer=n;
	}
	void penAan(int n,int r, int g, int b)
	{	pen = true;
		penkleur = CssColor.make(r,g,b);
		//gIm.setColor(penkleur);
		gIm.setStrokeStyle(penkleur);
		lnummer=n;
	}
	void penUit()
	{	pen = false;
	}
	void penUit(int n)
	{	pen = false;
		lnummer=0;
	}
	void vulAan()
	{	vul = true;
	}
	void vulAan(String kl)
	{	vul = true;
		if (kl.equals("transparant"))
			leeg = true;
		vulkleur = maakKleur(kl);
	}
	void vulAan(int r, int g, int b)
	{	vul = true;	
		vulkleur = CssColor.make(r,g,b);
	}
	void vulAan(int n)
	{	vul = true;
		lnummer=n;
	}
	void vulAan(int n,String kl)
	{	vul = true;
		lnummer=n;
		if (kl.equals("transparant"))
			leeg = true;
		vulkleur = maakKleur(kl);
	}
	void vulAan(int n,int r, int g, int b)
	{	vul = true;
		lnummer=n;
		vulkleur = CssColor.make(r,g,b);
	}
	void vulAan(CssColor kl)
	{	vul = true;	
		vulkleur = kl;
	}

	void vulUit()
	{	tekenPolygon();
		vul = false;
		lnummer=0;
		leeg = false;
	}
	void vulUit(int n)
	{	tekenPolygon(n);
		vul = false;
		lnummer=0;
		leeg = false;
	}
	void achtergrondkleur(String kl)
	{	achtergrondkleur = maakKleur(kl);
	}
	void achtergrondkleur(int r, int g, int b)
	{	achtergrondkleur = CssColor.make(r,g,b);
	}
//	void schrijf(String s)
//	{	gIm.drawString(s, (int)beginpunt.x, (int)beginpunt.y);
//	}
//	void schrijf(String s, Font f)
//	{	gIm.setFont(f);
//		gIm.drawString(s, (int)beginpunt.x, (int)beginpunt.y);
//	}
	Punt geefPunt()								// geeft de laatst getekende Punt
	{	double pf = (afstand-beginpunt.z)/afstand;
		double begx = l[0].nulpunt.x + (beginpunt.x-l[0].nulpunt.x)/pf;
		double begy = l[0].nulpunt.y + (beginpunt.y-l[0].nulpunt.y)/pf;
		return new Punt(begx,begy);
	}
	Punt geefPunt(int n)								// geeft de laatst getekende Punt
	{	double pf = (afstand-beginpunt.z)/afstand;
		double begx = l[n].nulpunt.x + (beginpunt.x-l[n].nulpunt.x)/pf;
		double begy = l[n].nulpunt.y + (beginpunt.y-l[n].nulpunt.y)/pf;
		return new Punt(begx,begy);
	}

	Polygon geefVlak()
	{	if (l[0].vlakken[l[0].aantalPolygonen-1].normaal.z > 0)
			return l[0].vlakken[l[0].aantalPolygonen-1].pol;
		else 
			return new Polygon();
	}
	
	Polygon geefVlak(int n)
	{	if (l[n].vlakken[l[n].aantalPolygonen-1].normaal.z > 0)
			return l[n].vlakken[l[n].aantalPolygonen-1].pol;
		else 
			return new Polygon();
	}
	
 	//-------------------------------------------------------------------------------------------
	//deze methode wordt gebruikt een kleur in de vorm van een string om te zetten in een Color
	//-------------------------------------------------------------------------------------------
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
			return CssColor.make(255, 165, 0);
		else
			return CssColor.make(255, 128, 0);
	}
}

/*
class AnimatieBeheerder extends Panel implements ActionListener, Runnable
{
  	public Button animatieknop;
  	private Thread animatie;
  	boolean animatieAan;
	TekenApplet3D eigenaar;	  

	public AnimatieBeheerder(TekenApplet3D ap)
	{	eigenaar = ap;
		animatieAan = false;
		animatieknop = new Button("animatie");
		animatieknop.addActionListener(this);
		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(animatieknop);
	}
	
 	//-------------------------------------------------------------------------------------------
	//afhandeling van de animatieknop actie, en het starten van de animatiedraad 
	//-------------------------------------------------------------------------------------------
 	public void actionPerformed(ActionEvent e)
	{	if(animatieknop.getLabel()=="animatie")
		{	beginAnimatie();
		}
		else
		{	onderbreekAnimatie();
			animatieknop.setLabel("animatie");
		}
	}
	
	public void run()
	{	while(eigenaar.tb.bezigMetTekenen)pauze(1);
		eigenaar.animatie();
		animatieknop.setLabel("animatie");
	}
	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden behalve door de Animatiebeheerder zelf, ook gebruikt door de 
	// MuisBeheerder 
	//-------------------------------------------------------------------------------------------
	public void onderbreekAnimatie()
	{	animatieAan=false;
		try
		{	animatie.join();
		}
		catch(InterruptedException e) {}
	}
	
	public void beginAnimatie()
	{	animatieAan=true;
		animatie = new Thread(this);
		animatie.start();
		animatieknop.setLabel("stoppen");
	}	
	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt in de animatie- en muishandlers van het leerlingenprogramma.
	//"animatieStatus()" wordt ook gebruikt door de TraceBeheerder en MuisBeheerder.
	//-------------------------------------------------------------------------------------------
	public boolean animatieStatus()
	{	return animatieAan;
	}
	
	public void pauze(int millisec)
	{  	try
    		{   Thread.sleep(millisec);
       		}
    		catch(InterruptedException e)    // geen ;
      		{   }
	}
}
*/
class MuisBeheerder implements MouseDownHandler, MouseUpHandler, MouseMoveHandler,
							   TouchStartHandler, TouchMoveHandler, TouchEndHandler
{
	private int eerstex, laatstex, eerstey, laatstey, dx, dy;
	private TekenApplet3D eigenaar;
	private Viewer3d eigenaar1;
	
	boolean mouseDown;
	
	public MuisBeheerder(TekenApplet3D ap)
	{	eigenaar = ap;
		eerstex = 0;
		eerstey = 0;
		laatstex = 0;
		laatstey = 0;
		dx = 0;
		dy = 0;
	}
	
	public MuisBeheerder(Viewer3d ap)
	{	eigenaar1 = ap;
		eerstex = 0;
		eerstey = 0;
		laatstex = 0;
		laatstey = 0;
		dx = 0;
		dy = 0;
	}
	
	public void onMouseDown(MouseDownEvent e)
	{	
		e.preventDefault();
		e.stopPropagation();
		mouseDown = true;
		eerstex = e.getX();
		eerstey = e.getY();
		laatstex = e.getX();
		laatstey = e.getY();
		if (eigenaar != null)
			eigenaar.muisDrukActie();
		
		if (eigenaar1 != null)
			eigenaar1.muisDrukActie();
		
	}
	
	public void onMouseMove(MouseMoveEvent e)
	{	
		e.preventDefault();
		e.stopPropagation();
		if (!mouseDown)
			return;
		int x = e.getX();
		int y = e.getY();
		dx = x - laatstex;
		dy = laatstey - y;
		if (eigenaar != null)
			eigenaar.muisSleepActie();
		
		if (eigenaar1 != null)
			eigenaar1.muisSleepActie();
		laatstex = x;
		laatstey = y;
		
	}
	
	public void onMouseUp(MouseUpEvent e)
	{	
		e.preventDefault();
		e.stopPropagation();
		
		mouseDown = false;
		if (eigenaar != null)
			eigenaar.muisLosActie();
		
		if (eigenaar1 != null)
			eigenaar1.muisLosActie();
		
		
	}
	
	@Override
	public void onTouchMove(TouchMoveEvent event) 
	{
		event.preventDefault();
		event.stopPropagation();
		if (event.getTouches().length() > 0) 
		{
			Touch touch = event.getTouches().get(0);
			//Widget sender = (Widget) event.getSource();
		    //Element elem = sender.getElement();
			int x = touch.getPageX()- eigenaar.tb.canvas.getAbsoluteLeft();//getRelativeX(elem);
			int y = touch.getPageY()- eigenaar.tb.canvas.getAbsoluteTop();//getRelativeY(elem);
	        dx = x - laatstex;
			dy = laatstey -y;
			if (eigenaar != null)
				eigenaar.muisSleepActie();
		
			if (eigenaar1 != null)
				eigenaar1.muisSleepActie();
			
			laatstex = x;
			laatstey = y;
	    }
	    event.preventDefault();
	    event.stopPropagation();
		
	}

	@Override
	public void onTouchStart(TouchStartEvent event) 
	{
		event.preventDefault();
		event.stopPropagation();
		if (event.getTouches().length() > 0) 
		{
			Touch touch = event.getTouches().get(0);
			//Widget sender = (Widget) event.getSource();
		    //Element elem = sender.getElement();
			eerstex = touch.getPageX() - eigenaar.tb.canvas.getAbsoluteLeft();//getRelativeX(elem);
			eerstey = touch.getPageY() - eigenaar.tb.canvas.getAbsoluteTop();;//getRelativeY(elem);
			laatstex = touch.getPageX() - eigenaar.tb.canvas.getAbsoluteLeft();;//getRelativeX(elem);
			laatstey = touch.getPageY() - eigenaar.tb.canvas.getAbsoluteTop();;//getRelativeY(elem);
			if (eigenaar != null)
				eigenaar.muisDrukActie();
		
			if (eigenaar1 != null)
				eigenaar1.muisDrukActie();
			
	    }
		event.preventDefault();
		event.stopPropagation();
		
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) 
	{
		event.preventDefault();
		event.stopPropagation();
		
		if (eigenaar != null)
			eigenaar.muisLosActie();
		
		if (eigenaar1 != null)
			eigenaar1.muisLosActie();
		
		event.preventDefault();
		event.stopPropagation();
		
	}
	
/*	
	public void mouseClicked(MouseEvent e)
	{	
		eerstex = e.getX();
		eerstey = e.getY();
		laatstex = e.getX();
		laatstey = e.getY();
		if(eigenaar!=null)eigenaar.muisKlikActie();
		//if(eigenaar1!=null)eigenaar1.muisKlikActie();
	
	}
*/	

	
	//-------------------------------------------------------------------------------------------
	//deze methoden worden gebruikt door de muishandlers in het leerlingenprogramma
	//-------------------------------------------------------------------------------------------
	public int geefSleepdx()
	{	return dx;
	}
	public int geefSleepdy()
	{	return dy;
	}
	public int geefDrukx()
	{	return eerstex;
	}
	public int geefDruky()
	{	return eerstey;
	}
	public int geefX()
	{	return laatstex;
	}
	public int geefY()
	{	return laatstey;
	}

}	

class Rotatie3D
{
	public int as;
	public double rotatieHoek;

	public Rotatie3D(int as, double rotatieHoek)
	{	this.as = as;
		this.rotatieHoek = rotatieHoek;
	}
}

class Matrix3D
{	
	//-------------------------------------------------------------------------------------------
	//deze klasse onthoudt, en berekent steeds opnieuw de tekenrichting, en berekent voor het 
	//Tekenblad aan de hand van een dx,dy en dz het volgende eindpunt van de tekenlijn.
	//-------------------------------------------------------------------------------------------

	private Rotatie3D[] rotatieRij;
	private int aantalRotaties;
	private double starthoekx,starthoeky,starthoekz,startschaal;
	private double xx, xy, xz ;
	private double yx, yy, yz;
	private double zx, zy, zz;
 	private static double pi = Math.PI;

	public Matrix3D()
	{	rotatieRij = new Rotatie3D[150];
		aantalRotaties = 0;
		starthoekx = 0;
		starthoeky = 0;
		starthoekz = 0;
		startschaal = 1;
		xx = 1.0;
		yy = 1.0;
		zz = 1.0;
	}
	void initialiseer()
	{	rotatieRij = new Rotatie3D[150];
		aantalRotaties = 0;
		xx=1;xy=0;xz=0;
		yx=0;yy=1;yz=0;
		zx=0;zy=0;zz=1;
		schaal(startschaal);
		ydraaiAbs(starthoeky);
		xdraaiAbs(starthoekx);
		zdraaiAbs(starthoekz);
	}
	
	void initialiseer(double hx, double hy, double hz, double schl)
	{	starthoekx = hx;
		starthoeky = hy;
		starthoekz = hz;
		startschaal = schl;
		initialiseer();
	}	
			
	void schaal(double f) 
	{
		xx *= f;
		xy *= f;
		xz *= f;
		yx *= f;
		yy *= f;
		yz *= f;
		zx *= f;
		zy *= f;
		zz *= f;
   }

	void xdraai(double theta) 
	{	voegRotatieToe(1,theta);
	}
	void ydraai(double theta) 
	{	voegRotatieToe(2,theta);
	}
	void zdraai(double theta) 
	{	voegRotatieToe(3,theta);
	}
	public void voegRotatieToe(int as, double rotatieHoek)
	{	Rotatie3D r = new Rotatie3D(as,rotatieHoek);
		for(int i = 0; i<aantalRotaties ; i++)
		{	Rotatie3D rt = rotatieRij[i];
			if(rt.as == 1)xdraaiAbs(-rt.rotatieHoek);
			else if(rt.as == 2)ydraaiAbs(-rt.rotatieHoek);
			else if(rt.as == 3)zdraaiAbs(-rt.rotatieHoek);
		}
		if(r.as == 1)xdraaiAbs(rotatieHoek);
		else if(r.as == 2)ydraaiAbs(rotatieHoek);
		else if(r.as == 3)zdraaiAbs(rotatieHoek);
		for(int i = aantalRotaties ; i>0 ; i--)
		{	Rotatie3D rt = rotatieRij[i-1];
			if(rt.as == 1)xdraaiAbs(rt.rotatieHoek);
			else if(rt.as == 2)ydraaiAbs(rt.rotatieHoek);
			else if(rt.as == 3)zdraaiAbs(rt.rotatieHoek);
		}
		
		if(aantalRotaties>0 && (rotatieRij[aantalRotaties-1].as == as))
		{	rotatieRij[aantalRotaties-1].rotatieHoek += rotatieHoek;
			if(rotatieRij[aantalRotaties-1].rotatieHoek%360 == 0) aantalRotaties--;
		}
		else 
		{	rotatieRij[aantalRotaties] = r;
			aantalRotaties++;
		}
	}

	void ydraaiAbs(double theta) 
	{
		theta *= (pi / 180);
		double ct = Math.cos(theta);
		double st = Math.sin(theta);

		double Nxx =  (xx * ct + zx * st);
		double Nxy =  (xy * ct + zy * st);
		double Nxz =  (xz * ct + zz * st);

		double Nzx =  (zx * ct - xx * st);
		double Nzy =  (zy * ct - xy * st);
		double Nzz =  (zz * ct - xz * st);

		xx = Nxx;
		xy = Nxy;
		xz = Nxz;
		zx = Nzx;
		zy = Nzy;
		zz = Nzz;
    }

    void xdraaiAbs(double theta) 
    {
		theta *= (pi / 180);
		double ct = Math.cos(theta);
		double st = Math.sin(theta);

		double Nyx = (yx * ct + zx * st);
		double Nyy = (yy * ct + zy * st);
		double Nyz = (yz * ct + zz * st);

		double Nzx = (zx * ct - yx * st);
		double Nzy = (zy * ct - yy * st);
		double Nzz = (zz * ct - yz * st);

		yx = Nyx;
		yy = Nyy;
		yz = Nyz;
		zx = Nzx;
		zy = Nzy;
		zz = Nzz;
	}

	void zdraaiAbs(double theta) 
	{
		theta *= -(pi / 180);
		double ct = Math.cos(theta);
		double st = Math.sin(theta);

		double Nyx = (yx * ct + xx * st);
		double Nyy = (yy * ct + xy * st);
		double Nyz = (yz * ct + xz * st);

		double Nxx = (xx * ct - yx * st);
		double Nxy = (xy * ct - yy * st);
		double Nxz = (xz * ct - yz * st);

		yx = Nyx;
		yy = Nyy;
		yz = Nyz;
		xx = Nxx;
		xy = Nxy;
		xz = Nxz;
	}
	
	void mult(Matrix3D rhs) 	{			double lxx = xx * rhs.xx + yx * rhs.xy + zx * rhs.xz;
		double lxy = xy * rhs.xx + yy * rhs.xy + zy * rhs.xz;
		double lxz = xz * rhs.xx + yz * rhs.xy + zz * rhs.xz;

		double lyx = xx * rhs.yx + yx * rhs.yy + zx * rhs.yz;
		double lyy = xy * rhs.yx + yy * rhs.yy + zy * rhs.yz;
		double lyz = xz * rhs.yx + yz * rhs.yy + zz * rhs.yz;

		double lzx = xx * rhs.zx + yx * rhs.zy + zx * rhs.zz;
		double lzy = xy * rhs.zx + yy * rhs.zy + zy * rhs.zz;
		double lzz = xz * rhs.zx + yz * rhs.zy + zz * rhs.zz;

		xx = lxx;
		xy = lxy;
		xz = lxz;

		yx = lyx;
		yy = lyy;
		yz = lyz;

		zx = lzx;
		zy = lzy;
		zz = lzz;
    }

	Punt3D geefVolgendPunt(Punt3D bp, double dx, double dy, double dz)
	{
		Punt3D ep = new Punt3D(0,0,0);
		ep.x = bp.x + dx*xx + dy*xy + dz*xz;
		ep.y = bp.y + dx*yx + dy*yy + dz*yz;
		ep.z = bp.z + dx*zx + dy*zy + dz*zz;
		return ep;
	}
	
	public String toString()
	{
		return "xx="+UF.format(xx,2)+" xy="+UF.format(xy,2)+" xz="+UF.format(xz,2);  
	}
}
	
class Polygon3D
{
	public Polygon pol;
	public Punt3D normaal;
	public double gemz;
	public CssColor vulkleur,lijnkleur;
	public boolean isLijn,isOmlijnd,isLeeg;
}

class Lichaam3D
{
	public int[] xcoor;
	public int[] ycoor;
	public int[] zcoor;
	public double[] xcoord;
	public double[] ycoord;
	public double[] zcoord;
	
	public int[] sorteerRij;

	public Polygon3D[] vlakken, vlakkenSort;
	public int aantalPunten, aantalPolygonen;
	private Polygon3D huidigePolygon;
	private double pf;
	public double afstand;
	Punt3D nulpunt;

	public Lichaam3D()
	{	xcoor = new int[2000];
		ycoor = new int[2000];
		zcoor = new int[2000];
		xcoord = new double[2000];
		ycoord = new double[2000];
		zcoord = new double[2000];

		vlakken = new Polygon3D[2000];
		sorteerRij = new int[200];
		aantalPunten = 0;
		aantalPolygonen = 0;
		nulpunt = new Punt3D(0,0,0);
		afstand = 1000;
	}
	public void zetAfstand(double afst)
	{	afstand = afst;
	}
	
	public void maakNulpunt(double x,double y,double z)
	{	nulpunt.x = x;
		nulpunt.y = y;
		nulpunt.z = z;
	}
	
	public void voegPuntToe(Punt3D p)
	{	pf = (afstand-p.z)/afstand;
		xcoord[aantalPunten] = nulpunt.x + (p.x-nulpunt.x)/pf;
		ycoord[aantalPunten] = nulpunt.y + (p.y-nulpunt.y)/pf;
		zcoord[aantalPunten] = p.z;
		xcoor[aantalPunten] = (int)xcoord[aantalPunten];
		ycoor[aantalPunten] = (int)ycoord[aantalPunten];
		zcoor[aantalPunten] = (int)p.z;

		aantalPunten++;
	}
	
	public void voegPolygonToe(CssColor vulkl, CssColor lijnkl, boolean isOmlnd, boolean isLg )
	{	huidigePolygon = new Polygon3D();
		huidigePolygon.pol = new Polygon(xcoor,ycoor,aantalPunten);
		if(aantalPunten<3)
		{
			huidigePolygon.normaal = new Punt3D(0,0,1);
			huidigePolygon.isLijn = true;
		}
		else
		{	double ux = xcoord[1] - xcoord[0];
			double uy = ycoord[1] - ycoord[0];
			double uz = zcoord[1] - zcoord[0];
			double vx = xcoord[2] - xcoord[1];
			double vy = ycoord[2] - ycoord[1];
			double vz = zcoord[2] - zcoord[1];
			double nx = uy*vz - uz*vy;	
			double ny = uz*vx - ux*vz;
			double nz = ux*vy - uy*vx;
			double ln = Math.sqrt(nx*nx + ny*ny + nz*nz);
			double nex = nx/ln;
			double ney = ny/ln;
			double nez = nz/ln;
			huidigePolygon.normaal = new Punt3D(nex,ney,nez);
		}
		double gz = 0;
		for(int i=0 ; i<aantalPunten ; i++)
		{	gz = gz + zcoor[i];
		}
		huidigePolygon.gemz = gz/aantalPunten;
		huidigePolygon.vulkleur = vulkl;
		huidigePolygon.lijnkleur = lijnkl;
		huidigePolygon.isOmlijnd = isOmlnd;
		huidigePolygon.isLeeg = isLg;
		aantalPunten = 0;
		vlakken[aantalPolygonen] = huidigePolygon;
		aantalPolygonen++;
		
	}

	public void sorteer()
	{	
		for (int i = 0; i < 200 ; i++)
		{	sorteerRij[i] = i;
		}
		for (int j = 0; j < aantalPolygonen; j++)
		{
			for(int i = j+1; i < aantalPolygonen; i++)
			{
				if (vlakken[j].gemz > vlakken[i].gemz)
				{	int res = sorteerRij[j];
					sorteerRij[j] = sorteerRij[i];
					sorteerRij[i] = res;
					huidigePolygon = vlakken[j];
					vlakken[j] = vlakken[i];
					vlakken[i] = huidigePolygon;
				}
			}
		}
	}
}