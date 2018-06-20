package fi.tekenveelvlakgwt.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * klasse die het tekenen implementeert
 * @author Peter Boon
 */

public class Tekenblad3D 
{
	/**
	 * Canvas om op te tekenen
	 */
	Canvas canvas;
	/**
	 * breddte en hoogte Canvs
	 */
	public int breedte,hoogte;
	/**
	 * huidige positie van de tekencursor
	 */
	private Punt3D beginpunt;
	/**
	 * nieuwe positie van de tekencursor na een translatie, zie methode naarVolgendePunt 
	 */
	private Punt3D eindpunt;
	/**
	 * beginpositie van de teknecursor
	 */
	private Punt3D startpunt;
	
	/**
	 * een aantal 3d-lichamen, zie klasse Lichaam3D
	 */
  	public Lichaam3D[] l;
  	/**
  	 * Context3d om mee te tekenen
  	 */
  	public Context2d gIm;
  	/**
  	 * Matrix3D die de tekenrichting onthoudt en herberekent, zie klasse Matrix3D
  	 */
  	public Matrix3D mat;
  	/**
  	 * eigenaar van dit TekenBlad3D
  	 */
	private TekenApplet3D eigenaar;

	/**
	 * is de pen actief?
	 */
	private boolean pen;
	/**
	 * is vullen actief? zie methode vulAan
	 */
	private boolean vul;

	/**
	 * moet het huidige 3d-polygon transparant worden?
	 */
	private boolean leeg;
	
	/**
	 * true: teken gekleurde vlakken met een schaduw-effect
	 */
	private boolean schaduw;

	/**
	 * nummer van het lichaam3D dat bewerkt wordt 
	 */
	private int lnummer;
	
	/**
	 * penkleur, vulkleur en achtergrondkleur
	 */
  	private CssColor penkleur,vulkleur,achtergrondkleur;
	/**
	 * afstand oog (op positieve z-as) tot x-y-vlak
	 */
	private double afstand;
	/**
	 * afhandelen Mouse/Touch Events op Canvas, zie klasse MuisBeheerder
	 */
	MuisBeheerder mb;
	
	/**
	 * constructor
	 * @param ap eigenaar van dit TekenBlad3D
	 * @param b breedte
	 * @param h hoogte
	 */
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
		eigenaar = ap;
		mat = new Matrix3D();
		
	}
	
	public void paint()
	{
		paintComponent(gIm);
	}
	

	public void paintComponent(Context2d g)
  	{ 	
		
		gIm = g;
		startpunt = new Punt3D(breedte/2,hoogte/2,0);
		for(int i=0 ; i<5 ; i++)
		{	l[i].maakNulpunt(breedte/2,hoogte/2,0);
		}
		tekenOpImage(true, g);
  	}
	
	/**
	 * zet de afstand van het oog (op positieve z-as) tot x-y-vlak
	 * @param afst nieuwe afstand 
	 */
	public void zetAfstand(double afst)
	{	afstand = afst;
		for(int i=0 ; i<5 ; i++)
		{	l[i].zetAfstand(afst);
		}
	}
	/**
	 * zet schaduweffect
	 * @param s true/false
	 */
	public void zetSchaduw(boolean s)
	{	schaduw = s;
	}

	/**
	 * teken de 3d-lichamen
	 * @param wis true: wis de huidige tekening
	 * @param g Context2d om te tekenen 
	 */
  	public void tekenOpImage(boolean wis, Context2d g)
  	{ 	startpunt = new Punt3D(breedte/2,hoogte/2,0);
  		
  		beginpunt = new Punt3D(startpunt);
    	eindpunt = new Punt3D(beginpunt);
    	
    	gIm.setFillStyle(achtergrondkleur);
    	if (wis)
    		gIm.fillRect(0, 0, breedte, hoogte);
    	
    	penAan(0,0,0);
		vul = false;
		// hier vult eigenaar 3d-lichamen met vlakken en lijnen
    	eigenaar.tekenprogramma();
		for(int i=0 ; i<5 ; i++)
		{	l[i].sorteer();
		}
		// loop door de 5 3d-lichamen
		for(int j=0 ; j<5 ; j++)
		{
			// loop door de vlakken
			for(int i=0 ; i<l[j].aantalPolygonen ; i++)
			{
				// teken alleen de buitenkanten van de vlakken
				if (l[j].vlakken[i].normaal.z > 0)
				{	
					if (schaduw)
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
						gIm.setFillStyle(CssColor.make(roodwaarde,groenwaarde,blauwwaarde));
						
					}
					else // geen schaduw
					{	
						gIm.setFillStyle(l[j].vlakken[i].vulkleur);
					
					}
					// vul het vlak als het niet transparant is
					if(!l[j].vlakken[i].isLeeg)
					{	
						g.moveTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						g.beginPath();
						for (int k = 1; k < l[j].vlakken[i].pol.aantalPunten; k++)
						{	g.lineTo(l[j].vlakken[i].pol.puntenX[k], l[j].vlakken[i].pol.puntenY[k]);
						}
						g.lineTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						g.closePath();
						g.fill();

					}	
					
					gIm.setStrokeStyle(l[j].vlakken[i].lijnkleur);
					
					// teken de omlijning als gewenst en als het vlak geen lijn is
					if (!l[j].vlakken[i].isLijn && l[j].vlakken[i].isOmlijnd )
					{	
						gIm.setStrokeStyle(l[j].vlakken[i].lijnkleur);
			        	g.moveTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						g.beginPath();
						for (int k = 1; k < l[j].vlakken[i].pol.aantalPunten; k++)
						{	g.lineTo(l[j].vlakken[i].pol.puntenX[k], l[j].vlakken[i].pol.puntenY[k]);
						}
						g.lineTo(l[j].vlakken[i].pol.puntenX[0], l[j].vlakken[i].pol.puntenY[0]);
						g.closePath();
						g.stroke();

					}
					// lijnen apart
					if (l[j].vlakken[i].isLijn)
					{	
						gIm.setStrokeStyle(l[j].vlakken[i].lijnkleur);
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

  	/**
  	 * repaint
  	 */
	void tekenOpnieuw()
	{	
		paint();
	}

	/**
	 * repaint
	 */
  	void tekenErbij()
	{	
  		paint();
	}

	/**
	 * ga naar een nieuwe positie die berekend wordt als nieuw = huidige + mat * (dx,dy,dz)T; 
 	 * als pen == true and vul == false, verbindt dan de oude en nieuwe posities met een lijn in penkleur, 
 	 * d.w.z. voeg twee punten toe aan het actuele lichaam3D en maak hiervan een Polygon3D  
 	 * als vul == true, voeg dan de oude positie als punt toe aan het actuele Lichaam3D
 	 * (al deze punten worden een Polygon3D na aanroepen van vulUit())  
	 * @param dx x-verandering
	 * @param dy y-verandering
	 * @param dz z-verandering
	 */
	void naarVolgendPunt(double dx,double dy, double dz)
	{	eindpunt = mat.geefVolgendPunt(beginpunt,dx,dy,dz);
		
		if(pen && !vul)
		{	l[lnummer].voegPuntToe(beginpunt);
			l[lnummer].voegPuntToe(eindpunt);
			l[lnummer].voegPolygonToe(penkleur,penkleur,true, false);
		}
		if(vul) 		 
		{	l[lnummer].voegPuntToe(beginpunt);
		}
		beginpunt.x = eindpunt.x;
		beginpunt.y = eindpunt.y;
		beginpunt.z = eindpunt.z;
	}

	/**
	 * maak van alle punten in lichaam3D 0 een polygon 
	 */
	void tekenPolygon()
	{	l[0].voegPolygonToe(vulkleur, penkleur, pen, leeg);
	}
	/**
	 * maak van alle punten in lichaam3D n een polygon
	 * @param n lichaam3D-nummer 
	 */
	void tekenPolygon(int n)
	{	l[n].voegPolygonToe(vulkleur, penkleur, pen, leeg);
	}
	
	/**
	 * activeer de pen
	 */
	void penAan()
	{	pen = true;
	}
	/**
	 * activeer de pen in kleur kl
	 * @param kl String met kleurnaam
	 */
	void penAan(String kl)
	{	pen = true;
		penkleur = maakKleur(kl);
		gIm.setStrokeStyle(penkleur);
	}
	/**
	 * activeer de pen in rgb-kleur (r,g,b)
	 * @param r roodfactor
	 * @param g groenfactor
	 * @param b blauwfactoe
	 */
	void penAan(int r, int g, int b)
	{	pen = true;
		penkleur = CssColor.make(r,g,b);
		gIm.setStrokeStyle(penkleur);
	}
	/**
	 * activeer de pen en maak lichaam3D n actueel
	 * @param n lichaam3D nummer
	 */
	void penAan(int n)
	{	pen = true;
		lnummer=n;
	}
	/**
	 * activeer de pen in kleur kl en maak lichaam3D n actueel
	 * @param n lichaam3D nummer
	 * @param kl String met kleurnaam
	 */
	void penAan(int n,String kl)
	{	pen = true;
		penkleur = maakKleur(kl);
		gIm.setStrokeStyle(penkleur);
		lnummer=n;
	}
	/**
	 * activeer de pen in rgb-kleur (r,g,b) en maak lichaam3D n actueel
	 * @param n lichaam3D nummer
	 * @param r roodfactor
	 * @param g groenfactor
	 * @param b blauwfactor
	 */
	void penAan(int n,int r, int g, int b)
	{	pen = true;
		penkleur = CssColor.make(r,g,b);
		gIm.setStrokeStyle(penkleur);
		lnummer=n;
	}
	/**
	 * de-activeer de pen
	 */
	void penUit()
	{	pen = false;
	}
	/**
	 * de-activeer de pen en maak lichaam3D 0 actueel
	 * @param n dummy
	 */
	void penUit(int n)
	{	pen = false;
		lnummer=0;
	}
	/**
	 * start vul, d.w.z. voeg alle nieuw getekende punten toe aan het actuele lichaam3D
	 * vulUit() maakt van deze punten een Polygon3D in het actuele lichaam3D 
	 */
	void vulAan()
	{	vul = true;
	}
	/**
	 * start vul, d.w.z. voeg alle nieuw getekende punten toe aan het actuele lichaam3D
	 * en zet vulkleur op kl
	 * vulUit() maakt van deze punten een Polygon3D in het actuele lichaam3D
	 * @param kl String met kleurnaam 
	 */
	void vulAan(String kl)
	{	vul = true;
		if (kl.equals("transparant"))
			leeg = true;
		vulkleur = maakKleur(kl);
	}
	/**
	 * start vul, d.w.z. voeg alle nieuw getekende punten toe aan het actuele lichaam3D
	 * en zet vulkleur op rgb-kleur (r,g,b)
	 * vulUit() maakt van deze punten een Polygon3D in het actuele lichaam3D
	 * @param r roodfactor
	 * @param g groenfactor 
	 * @param b blauwfactor
	 */
	void vulAan(int r, int g, int b)
	{	vul = true;	
		vulkleur = CssColor.make(r,g,b);
	}
	/**
	 * zet het actuele lichaam op lichaam3D nummer n en 
	 * start vul, d.w.z. voeg alle nieuw getekende punten toe aan het actuele lichaam3D
	 * vulUit() maakt van deze punten een Polygon3D in het actuele lichaam3D
	 * @param n licham3D nummer
	 */   
	void vulAan(int n)
	{	vul = true;
		lnummer=n;
	}
	/**
	 * zet het actuele lichaam op lichaam3D nummer n, vulkeur op kl en  
	 * start vul, d.w.z. voeg alle nieuw getekende punten toe aan het actuele lichaam3D
	 * vulUit() maakt van deze punten een Polygon3D in het actuele lichaam3D
	 * @param n lichaam3D nummer
	 * @param kl de vulkleur
	 */   
	void vulAan(int n,String kl)
	{	vul = true;
		lnummer=n;
		if (kl.equals("transparant"))
			leeg = true;
		vulkleur = maakKleur(kl);
	}
	/**
	 * zet het actuele lichaam op lichaam3D nummer n, vulkeur op rgb-kleur (r,g,b) en  
	 * start vul, d.w.z. voeg alle nieuw getekende punten toe aan het actuele lichaam3D
	 * vulUit() maakt van deze punten een Polygon3D in het actuele lichaam3D
	 * @param n licham3D nummer
	 * @param r roodfactor
	 * @param g groenfactor
	 * @param b blauwfactor
	 */
	void vulAan(int n,int r, int g, int b)
	{	vul = true;
		lnummer=n;
		vulkleur = CssColor.make(r,g,b);
	}
	/**
	 * zet vulkeur op CssColor kl en 
	 * start vul, d.w.z. voeg alle nieuw getekende punten toe aan het actuele lichaam3D
	 * vulUit() maakt van deze punten een Polygon3D in het actuele lichaam3D
	 * @param kl CssColor
	 */
	void vulAan(CssColor kl)
	{	vul = true;	
		vulkleur = kl;
	}
	/**
	 * de-activeer vul en mak van alle punten in lichaam3D 0 een polygon;
	 * zet het actuele lichaam3D op nummer 0 
	 */
	void vulUit()
	{	tekenPolygon();
		vul = false;
		lnummer=0;
		leeg = false;
	}
	/**
	 * de-activeer vul en mak van alle punten in lichaam3D n een polygon;
	 * zet het actuele lichaam3D op nummer 0 
	 * @param n lichaam3D nummer
	 */
	void vulUit(int n)
	{	tekenPolygon(n);
		vul = false;
		lnummer=0;
		leeg = false;
	}
	/**
	 * zet de achtergrondkleur
	 * @param kl kleurnaam
	 */
	void achtergrondkleur(String kl)
	{	achtergrondkleur = maakKleur(kl);
	}
	/**
	 * zet de achtergrondkleur
	 * @param r roodfactor
	 * @param g groenfactor
	 * @param b blauwfactor
	 */
	void achtergrondkleur(int r, int g, int b)
	{	achtergrondkleur = CssColor.make(r,g,b);
	}
	/**
	 * geef het laatst getekende punt van lichaam3D nummer 0
	 * @return laatst getekende punt van lichaam3D 0
	 */
	Punt geefPunt()							
	{	double pf = (afstand-beginpunt.z)/afstand;
		double begx = l[0].nulpunt.x + (beginpunt.x-l[0].nulpunt.x)/pf;
		double begy = l[0].nulpunt.y + (beginpunt.y-l[0].nulpunt.y)/pf;
		return new Punt(begx,begy);
	}
	/**
	 * geef het laatst getekende punt van lichaam3D nummer n
	 * @param n lichaam3D-nummer
	 * @return laatst getekende punt van lichaam3D n
	 */
	Punt geefPunt(int n)								
	{	double pf = (afstand-beginpunt.z)/afstand;
		double begx = l[n].nulpunt.x + (beginpunt.x-l[n].nulpunt.x)/pf;
		double begy = l[n].nulpunt.y + (beginpunt.y-l[n].nulpunt.y)/pf;
		return new Punt(begx,begy);
	}
	/**
	 * geef het laatst gemaakte vlak in lichaam3D nummer 0 wanneer de normaalvector
	 * van dit vlak naar de positieve z-as wijst (d.w.z. wannneer de buitenkant van het vlak
	 * zichtbaar is); als niet, gee een Polygon zonder punten terug   
	 * @return een Polygon (mogelijk zonder punten)
	 */
	Polygon geefVlak()
	{	if (l[0].vlakken[l[0].aantalPolygonen-1].normaal.z > 0)
			return l[0].vlakken[l[0].aantalPolygonen-1].pol;
		else 
			return new Polygon();
	}

	/**
	 * geef het laatst gemaakte vlak in lichaam3D nummer n wanneer de normaalvector
	 * van dit vlak naar de positieve z-as wijst (d.w.z. wannneer de buitenkant van het vlak
	 * zichtbaar is); als niet, gee een Polygon zonder punten terug
	 * @param n lichaam3D-nummer   
	 * @return een Polygon (mogelijk zonder punten)
	 */
	Polygon geefVlak(int n)
	{	if (l[n].vlakken[l[n].aantalPolygonen-1].normaal.z > 0)
			return l[n].vlakken[l[n].aantalPolygonen-1].pol;
		else 
			return new Polygon();
	}
	
	/**
	 * gegeven de kleurnaam, maar de corresponderende kleur
	 * @param kl kleurnaam
	 * @return corresponderende kleur
	 */
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

