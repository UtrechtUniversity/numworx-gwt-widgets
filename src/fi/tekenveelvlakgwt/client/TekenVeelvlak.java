package fi.tekenveelvlakgwt.client;

//import java.awt.event.*;
//import java.awt.*;
//import java.io.*;
import java.util.*;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.dom.client.Style;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Label;


public class TekenVeelvlak extends TekenApplet3D implements  ClickHandler// ItemListener
{	
	//TekenVeelvlakInteractiePanel tvip;
	
	int breedte, hoogte; 
	
	Slider zijdeSl;
	
	Matrix3D matrot, matres,mateenh;
	double k;
	double kMinFac = 60e-2d;
	double kMaxFac = 140e-2d;
	double k50 = 180;
	double kMin, kMax;
	double zoomFac = 5e-1d;
	double xhoek,yhoek;
	double beginx = 20, beginy = -30;
	
	boolean muisDrukAan = true;
	
	Veelvlak v, tv;
	Veelvlak voorkantPijl = null;
	
	Polygon[] p;
	
	Punt[] trefpunten;
	boolean[] trefpuntRaak;
	int aantalHpNieuw, aantalGetekendeHoekpunten;
	Hoekpunt[] hoekpuntenNieuw;
	Punt[] trefpuntenNieuw;
	boolean begin, basisZichtbaar,maakLijn, maakVlak, vaktekening;
	
	PushButton basisKnop, terugKnop, wisKnop, wisVKnop;
	ToggleButton lijnKnop, vlakKnop;
	Label zoomLabel;
	
	int aantalPuntenRood, puntnr1, puntnr2;
	int[] puntnr;	
	
	int bStarH = 20;
	
	public TekenVeelvlak(int b, int h)
	{
		super(b,h);
		breedte = b;
		hoogte = h;
	}
	
	public void initialiseer()
	{	
		
System.out.println("initialiseer");

		int currentY = bStarH;
	    
		zoomLabel = new Label("zoom");
		rg.add(zoomLabel);
		rg.setWidgetLeftWidth(zoomLabel, 15, Style.Unit.PX, 100, Style.Unit.PX);
		rg.setWidgetTopHeight(zoomLabel, currentY, Style.Unit.PX, 20, Style.Unit.PX);

		currentY += 30;
		
		zijdeSl = new Slider(100,50,this);
		zijdeSl.achtergrondKleur = CssColor.make(208,228,255);
		rg.add(zijdeSl.sliderCanvas);
		rg.setWidgetLeftWidth(zijdeSl.sliderCanvas, 10, Style.Unit.PX, 110, Style.Unit.PX);
		rg.setWidgetTopHeight(zijdeSl.sliderCanvas, currentY, Style.Unit.PX, 20, Style.Unit.PX);
				
		zijdeSl.paint();
		
		currentY += 30;
		
		lijnKnop = new ToggleButton("Maak lijn","MAAK LIJN");
		rg.add(lijnKnop);
		rg.setWidgetLeftWidth(lijnKnop, 8, Style.Unit.PX, 114, Style.Unit.PX);
		rg.setWidgetTopHeight(lijnKnop, currentY, Style.Unit.PX, 25, Style.Unit.PX);
		
		lijnKnop.addClickHandler(this);
		
		lijnKnop.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());
		
		currentY += 35;
				
		vlakKnop = new ToggleButton("Maak vlak","MAAK VLAK");
		rg.add(vlakKnop);
		rg.setWidgetLeftWidth(vlakKnop, 8, Style.Unit.PX, 114, Style.Unit.PX);
		rg.setWidgetTopHeight(vlakKnop, currentY, Style.Unit.PX, 25, Style.Unit.PX);
		
		vlakKnop.addClickHandler(this);
		
		vlakKnop.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());
		
		currentY += 35;
		
		basisKnop = new PushButton("Verberg basis");
		rg.add(basisKnop);
		rg.setWidgetLeftWidth(basisKnop, 8, Style.Unit.PX, 114, Style.Unit.PX);
		rg.setWidgetTopHeight(basisKnop, currentY, Style.Unit.PX, 25, Style.Unit.PX);
		
		basisKnop.addClickHandler(this);
		
		basisKnop.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());
		
		currentY += 35;
		
		terugKnop = new PushButton("Maak ongedaan");
		rg.add(terugKnop);
		rg.setWidgetLeftWidth(terugKnop, 8, Style.Unit.PX, 114, Style.Unit.PX);
		rg.setWidgetTopHeight(terugKnop, currentY, Style.Unit.PX, 25, Style.Unit.PX);
		
		terugKnop.addClickHandler(this);
		
		terugKnop.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());
		
		currentY += 35;

		wisKnop = new PushButton("Wis lijnen");
		rg.add(wisKnop);
		rg.setWidgetLeftWidth(wisKnop, 8, Style.Unit.PX, 114, Style.Unit.PX);
		rg.setWidgetTopHeight(wisKnop, currentY, Style.Unit.PX, 25, Style.Unit.PX);

		wisKnop.addClickHandler(this);
		
		wisKnop.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());
		
		currentY += 35;
		
		wisVKnop = new PushButton("Wis vlakken");
		rg.add(wisVKnop);
		rg.setWidgetLeftWidth(wisVKnop, 8, Style.Unit.PX, 114, Style.Unit.PX);
		rg.setWidgetTopHeight(wisVKnop, currentY, Style.Unit.PX, 25, Style.Unit.PX);

		wisVKnop.addClickHandler(this);
		
		wisVKnop.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.pushbutton());
		
		currentY += 35;
		
		matrot = new Matrix3D();
		matres = new Matrix3D();
		mateenh = new Matrix3D();
		tb.mat = matres;
		k = 180; //k=180; // ik: 130
		begin=true;
		basisZichtbaar=true;
		maakLijn=true;
		maakVlak=false;
		vaktekening = false;
		trefpunten = new Punt[500];
		trefpuntRaak = new boolean[500];
		wisTrefpunten();
		aantalHpNieuw = 0;
		hoekpuntenNieuw = new Hoekpunt[500];
		trefpuntenNieuw = new Punt[500];
		aantalPuntenRood = 0;
		puntnr = new int[20];
		
		v = (new Kubus(1));
		
		int n = 0;
		int aantalHp = 8;
		int aantalRib = 12;
		aantalGetekendeHoekpunten = aantalHp + n*aantalRib;
		Hoekpunt[] hp = new Hoekpunt[v.aantalHoekpunten+n*aantalRib];
		for(int i=0 ; i<v.aantalHoekpunten ; i++)
		{	hp[i]=v.hoekpunten[i];
		}
		v.hoekpunten = hp;
		v.aantalHoekpunten+=n*aantalRib;
		
		int[] rib = {0,1,1,2,2,3,3,0,0,4,1,5,2,6,3,7,4,5,5,6,6,7,7,4};
		for(int i=0 ; i<aantalRib ; i++)
		{	for(int j=0 ; j<n ; j++)
			{	v.hoekpunten[aantalHp+n*i+j] = new Hoekpunt(((n-j)*v.hoekpunten[rib[2*i]].x + (j+1)*v.hoekpunten[rib[2*i+1]].x)/(n+1),  
														((n-j)*v.hoekpunten[rib[2*i]].y + (j+1)*v.hoekpunten[rib[2*i+1]].y)/(n+1),
														((n-j)*v.hoekpunten[rib[2*i]].z + (j+1)*v.hoekpunten[rib[2*i+1]].z)/(n+1));
			}
		}
											
		for (int i = 0; i < v.aantalVlakken; i++)
		{	v.vlakken[i].vulkleur = "transparant";
		}
		
		tv = new Veelvlak();
		
		for(int i=0 ; i<v.aantalHoekpunten ; i++)
		{	hoekpuntenNieuw[aantalHpNieuw] = v.hoekpunten[i];
			aantalHpNieuw++;
		}
		
		setBounds(0,0,breedte,hoogte);
	}

	public void setBounds(int x, int y, int b, int h)
	{	

//System.out.println("tv setBounds " + x + " " + y + " " + b + " " + h);
//System.out.println("k before " + k);
		
		//k=180.0/500*Math.min(b-90, h);
		k50 = 180.0/500*Math.min(b-150, h);
		kMin = kMinFac * k50;
		kMax = kMaxFac * k50;
		
//System.out.println("tvv kMin = " + UF.format(kMin, 1));
//System.out.println("tvv kMax = " + UF.format(kMax, 1));

		k = zoomFac * (kMax - kMin) + kMin;
		
		
		
		//k=180.0/500*Math.min(b-150, h);

if (k > 0)		
{	//System.out.println("tvvgwt b = " + b + " h = " + h);		
	//System.out.println("tvvgwt k = " + UF.format(k, 1));
}	

		//super.setBounds(x,y,b,h);
		
		//if (tb != null)
		//	tekenOpnieuw();
	}
	
	public void zetZoomFac(double zFac)
	{
		zoomFac = zFac;
		
		//k50 = 180.0/500*Math.min(getSize().width-150, getSize().height);
		k50 = 180.0/500*Math.min(breedte-150, hoogte);
		kMin = kMinFac * k50;
		kMax = kMaxFac * k50;

		k = zoomFac * (kMax - kMin) + kMin; 
		
//System.out.println("tvv setZoomFac k = " + UF.format(k, 1));		
		
	}
	
	public void setState(Map map)
	{	
		ObjectMap h = JSONUtilities.wrapMap(map);
//System.out.println("tv setState");

		double[] hoekpunten = null;
		List<Double> hoekpuntenAL = new ArrayList<Double>();
		int[] vlakken = null;
		List<Integer> vlakkenAL = new ArrayList<Integer>();
		int[] lijnen = null;
		List<Integer> lijnenAL = new ArrayList<Integer>();
		
		boolean basisZichtbaar = true;
		
		double zoomFac = 5e-1d;
		double draaiX = 20;
		double draaiY = -30;
		
		if (h.containsKey("hoekpunten"))
		{	
			hoekpuntenAL = h.getDoubleList("hoekpunten");
			if (hoekpuntenAL == null)
			{	hoekpunten = h.getDoubleArray("hoekpunen"); 
			}
			else 
			{	hoekpunten = new double[hoekpuntenAL.size()];
				for (int hp = 0; hp < hoekpuntenAL.size(); hp++)
					hoekpunten[hp] = hoekpuntenAL.get(hp).doubleValue();
			}
		}
		if (h.containsKey("vlakken"))
		{	
			vlakkenAL = h.getIntegerList("vlakken");
			if (vlakkenAL == null)
			{	vlakken = h.getIntArray("vlakken"); 
			}
			else 
			{	vlakken = new int[vlakkenAL.size()];
				for (int v = 0; v < vlakkenAL.size(); v++)
					vlakken[v] = vlakkenAL.get(v).intValue();
			}
		}
		if (h.containsKey("lijnen"))
		{	lijnenAL = h.getIntegerList("lijnen");
			if (lijnenAL == null)
			{	lijnen = h.getIntArray("lijnen"); 
			}
			else 
			{	lijnen = new int[lijnenAL.size()];
				for (int l = 0; l < lijnenAL.size(); l++)
					lijnen[l] = lijnenAL.get(l).intValue();
			}
		}
		
		if (h.containsKey("basisZichtbaar"))
			basisZichtbaar = h.getBoolean("basisZichtbaar");
		if (h.containsKey("zoomFac"))
			zoomFac = h.getDouble("zoomFac");
		if (h.containsKey("draaiX"))
			draaiX = h.getDouble("draaiX");
		if (h.containsKey("draaiY"))
			draaiY = h.getDouble("draaiY");

		aantalPuntenRood = 0;
		wisTrefpunten();
		//tv.wisLijnen();
		aantalHpNieuw = 0;
		
		this.basisZichtbaar = basisZichtbaar;
		
		if (!basisZichtbaar) 
			basisKnop.setText("Toon basis");
		
//System.out.println("zoomFac = " + UF.format(zoomFac,3));		
		
		this.zoomFac = zoomFac;
		
		int stand = (int) Math.round(zoomFac * zijdeSl.geefLengte());
		zijdeSl.zetStand(stand);
		zetZoomFac(zoomFac);

		zetBeginHoeken(draaiX, draaiY);
//System.out.println("draaiX = " + UF.format(draaiX,1) + " draaiY = " + UF.format(draaiY,1));		

		tv = new Veelvlak(hoekpunten, vlakken, lijnen);

		for (int i = 0; i < v.aantalHoekpunten; i++)
		{	hoekpuntenNieuw[aantalHpNieuw] = v.hoekpunten[i];
			aantalHpNieuw++;
		}
	
		maakAlleSnijpunten();
		
		begin = true;
		tekenOpnieuw();
		
	}
	
	public HashMap getState()
	{	
//System.out.println("tv getState");		
		
		//double[] hoekpunten = null;
		ArrayList<Double> hoekpuntenAL = new ArrayList<Double>();
		//int[] vlakken = null;
		ArrayList<Integer> vlakkenAL = new ArrayList<Integer>();
		//int[] lijnen = null;
		ArrayList<Integer> lijnenAL = new ArrayList<Integer>();
		//String[] kleuren = null;
		ArrayList<String> kleurenAL = new ArrayList<String>();
				
		boolean basisZichtbaar = true;
		
		double zoomFac = 5e-1d;
		
		double draaiX = 20;
		double draaiY = -30;

		//hoekpunten = tv.hpRij;
		for (int h = 0; h < tv.hpRij.length; h++)
			hoekpuntenAL.add(new Double(tv.hpRij[h]));
		//vlakken = tv.vlRij;
		for (int v = 0; v < tv.vlRij.length; v++)
			vlakkenAL.add(new Integer(tv.vlRij[v]));
		//lijnen = tv.lnRij;
		for (int k = 0; k < tv.lnRij.length; k++)
			lijnenAL.add(new Integer(tv.lnRij[k]));
		//kleuren = new String[tv.aantalVlakken];
		for (int vCnt = 0; vCnt < tv.aantalVlakken; vCnt++)
		{	kleurenAL.add(tv.vlakken[vCnt].vulkleur);
		}
		
		basisZichtbaar = this.basisZichtbaar;
		
		zoomFac = this.zoomFac;
		draaiX = geefDraaiX();
		draaiY = geefDraaiY();

//System.out.println("zoomFac = " + UF.format(zoomFac,3));
		
		HashMap h = new HashMap();
		
		h.put("hoekpunten", hoekpuntenAL);
		h.put("vlakken", vlakkenAL);
		h.put("lijnen", lijnenAL);
		h.put("kleuren", kleurenAL);
		
//		h.put("aantalVlakkenRood", new Integer(aantalVlakkenRood));
		
		h.put("basisZichtbaar", new Boolean(basisZichtbaar));
		
		h.put("zoomFac", new Double(zoomFac));
		
		h.put("draaiX", new Double(draaiX));
		h.put("draaiY", new Double(draaiY));
//System.out.println("draaiX = " + UF.format(draaiX,1) + " draaiY = " + UF.format(draaiY,1));		

		//h.put("viewerPosition", new Integer(viewerPosition));
		//h.put("muisAan", new Boolean(muisAan));
		
		return h;
	}
	
	public void zetAfstand(double afst)
	{	
		tb.zetAfstand(afst);
	}
	public void zetSchaduw(boolean s)
	{	tb.zetSchaduw(s);
	}
	public void zetBeginHoeken(double hx, double hy)
	{	beginx = hx;
		beginy = hy;
		xhoek = 0;
		yhoek = 0;
	}
	
/*	
	public void zetKlikAan(boolean b)
	{	klikAan = b;
		muisDrukAan = !b;
		//rg.setVisible(!b);
	}
	
*/	
	public double geefDraaiX()
	{	
//System.out.println("gdX " + beginx + " " + xhoek);		
		return beginx+xhoek;
	}
	public double geefDraaiY()
	{	
//System.out.println("gdY " + beginy + " " + yhoek);		
		return beginy+yhoek;
	}
	
/*	
	public int geefBasisFiguur()
	{	return kiesV.getSelectedIndex();
	}
*/
/*	
	public void zetKiesV(int basisFiguur)
	{
		kiesV.setSelectedIndex(basisFiguur);
	}
*/	
	public void toonVoorkantPijl(Veelvlak vkPijl)
	{
		voorkantPijl = vkPijl;
		
//GWT		
		//repaint();
		tekenOpnieuw();
	}
	
	public void tekenprogramma()
	{	
		if(vaktekening)
		{	
/*			
			begindraai(0,0);
			
			tb.gIm.drawRect(20,220,200,200);
			tb.gIm.drawRect(220,220,200,200);
			tb.gIm.drawRect(220,20,200,200);
			tb.gIm.drawRect(420,220,200,200);
			
			
			penUit(); stap(0,100,0); penAan();
			xdraai(90);
			if(basisZichtbaar)tekenVeelvlak(2,v);
			tekenVeelvlak(1,tv);
			xdraai(-90);
			penUit(); stap(0,-100,0); penAan();
			
			penUit(); stap(0,-100,0); penAan();
			xdraai(0);
			if(basisZichtbaar)tekenVeelvlak(2,v);
			tekenVeelvlak(1,tv);
			xdraai(0);
			penUit(); stap(0,100,0); penAan();
			
			penUit(); stap(-200,-100,0); penAan();
			ydraai(90);
			if(basisZichtbaar)tekenVeelvlak(2,v);
			tekenVeelvlak(1,tv);
			ydraai(-90);
			penUit(); stap(200,100,0); penAan();
			
			penUit(); stap(200,-100,0); penAan();
			ydraai(-90);
			if(basisZichtbaar)tekenVeelvlak(2,v);
			tekenVeelvlak(1,tv);
			ydraai(90);
			penUit(); stap(-200,100,0); penAan();
			
			k=92;
			penUit(); stap(220,130,0); penAan();
			xdraai(30);ydraai(-34);
			if(basisZichtbaar)tekenVeelvlak(2,v);
			tekenVeelvlak(1,tv);
			ydraai(34);xdraai(-30);
			penUit(); stap(-220,-130,0); penAan();
*/			
			
		}
		else
		{	
			
//System.out.println("tekenProg");
//System.out.println("mres = " + matres.toString());
//System.out.println("tbm = " + tb.mat.toString());


			if (begin)
				begindraai(beginx,beginy);
			if (basisZichtbaar)
				tekenVeelvlak(2,v);
			tekenVeelvlak(1,tv);
			maakTrefpunten(tv);
			
			if (voorkantPijl != null)
			{	tekenVeelvlak(1,voorkantPijl);
			}
			
		}
	}
	void begindraai(double xdr,double ydr)
	{	
		
//System.out.println("begindraai " + begin);

		if(begin)
		{	//lijnKnop.setSize(100,25);
			//basisKnop.setSize(100,25);
			//terugKnop.setSize(100,25);
			//wisKnop.setSize(100,25);
			//wisVKnop.setSize(100,25);
			//vlakKnop.setSize(100,25);
			beginx = xdr;
			beginy = ydr;
						
//System.out.println("beginx = " + beginx);
//System.out.println("beginy = " + beginy);

			tb.mat.initialiseer();
			matrot.initialiseer();
			matrot.ydraaiAbs(ydr);
			matrot.xdraaiAbs(xdr);
			tb.mat.mult(matrot);
			begin=false;
		}
	}
	void maakTrefpunten(Veelvlak v)
	{	for(int i=0 ; i<v.aantalHoekpunten ; i++)
		{	penUit();stap(k*v.hoekpunten[i].x, k*v.hoekpunten[i].y, k*v.hoekpunten[i].z);
			trefpunten[i] = geefPunt(1);
			if(trefpuntRaak[i])
			{	tb.mat = mateenh;
				stap(5,0);vulAan(1,"groen");stap(-5,-5);stap(-5,5);stap(5,5);stap(5,-5);vulUit(1);stap(-5,0);
				tb.mat = matres;
			}
			
			stap(-k*v.hoekpunten[i].x, -k*v.hoekpunten[i].y, -k*v.hoekpunten[i].z);penAan();
		}
		for(int i=0 ; i<aantalHpNieuw ; i++)
		{	penUit();stap(k*hoekpuntenNieuw[i].x, k*hoekpuntenNieuw[i].y, k*hoekpuntenNieuw[i].z);
			trefpuntenNieuw[i] = geefPunt(1);
			//if(v.trefpuntRaak[i])
			//{	tb.mat = mateenh;
			//	stap(3,0);vulAan(1,"rood");stap(-3,-3);stap(-3,3);stap(3,3);stap(3,-3);vulUit(1);stap(-3,0);
			//	tb.mat = matres;
			//}
			if(basisZichtbaar)// && i<aantalGetekendeHoekpunten)
			{	tb.mat = mateenh;
				stap(2,0);vulAan(1,"zwart");stap(-2,-2);stap(-2,2);stap(2,2);stap(2,-2);vulUit(1);stap(-2,0);
				tb.mat = matres;
			}
			stap(-k*hoekpuntenNieuw[i].x, -k*hoekpuntenNieuw[i].y, -k*hoekpuntenNieuw[i].z);penAan();
		}
	}
	public void wisTrefpunten()
	{	for(int i=0 ; i<500 ; i++)
		{	trefpuntRaak[i]=false;
		}
	}
	void tekenVeelvlak(int n,Veelvlak vv)
	{	
		if ((n == 1) && (vv == tv))
		{	p = new Polygon[vv.aantalVlakken];
		}
		for (int i = 0; i < vv.aantalVlakken; i++)
		{	tekenVlak(n, vv.vlakken[i]);
			if ((n == 1) && (vv == tv))
			{	p[i] = tb.geefVlak(1);
//System.out.println("p" + i + " " + polygonString(p[i]));			
			}
		}
		for (int i = 0; i < vv.aantalLijnen; i++)
		{	tekenLijn(vv.lijnen[i]);
		}
	}
	
/*	
	public String polygonString(Polygon p)
	{
		String result = "";
		for (int i = 0; i < p.npoints; i++)
		{
			result += "(" + p.xpoints[i] + "," + p.ypoints[i] + ") ";  
		}
		
		return result;
	}
*/
	void tekenLijn(Lijn l)
	{	penUit();
		stap(k*l.hpunt1.x, k*l.hpunt1.y, k*l.hpunt1.z);
		penAan(1,l.kleur);
		stap(k*l.hpunt2.x - k*l.hpunt1.x, k*l.hpunt2.y - k*l.hpunt1.y, k*l.hpunt2.z - k*l.hpunt1.z);
		penUit(1);
		stap(-k*l.hpunt2.x, -k*l.hpunt2.y, -k*l.hpunt2.z);
	}
	void tekenVlak(int n,Vlak v)
	{	penUit();
		stap(k*v.punten[0].x, k*v.punten[0].y, k*v.punten[0].z);
		
		
		if (!(v.lijnkleur=="transparant"))
			penAan("lichtgrijs");
		
		if (v.vulkleur=="transparant")
		{	if (n==2)
				vulAan(v.vulkleur);
			else if (n==1)
				vulAan(1,v.vulkleur);
		}
		else if (v.vulkleur != "zwart") 
		{	if (n==2)
			{	vulAan("grijs");
			}
			else if (n==1)
			{	vulAan(1,"grijs");
			}
		}
		
		if (v.vulkleur != "zwart")
		{
			for(int i=v.aantalHoekpunten-1 ; i>-1 ; i--)
			{	int a=i ; int b=(i+1)%v.aantalHoekpunten;
				stap(k*(v.punten[a].x-v.punten[b].x), k*(v.punten[a].y-v.punten[b].y), k*(v.punten[a].z-v.punten[b].z));
			}
			if (n==2)
				vulUit();
			else if (n==1)
				vulUit(1);
		}
		
		if (!(v.lijnkleur=="transparant"))
			penAan(v.lijnkleur);
		vulAan(n,v.vulkleur);
		for(int i=0 ; i<v.aantalHoekpunten ; i++)
		{	int a=i ; int b=(i+1)%v.aantalHoekpunten;
			stap(-k*(v.punten[a].x-v.punten[b].x), -k*(v.punten[a].y-v.punten[b].y), -k*(v.punten[a].z-v.punten[b].z));
		}
		vulUit(n);
		penUit();
		stap(-k*v.punten[0].x, -k*v.punten[0].y, -k*v.punten[0].z);
		
	}
	void maakSnijpunt(Lijn l1, Lijn l2)
	{	double ax = l1.hpunt1.x;double ay = l1.hpunt1.y;double az = l1.hpunt1.z;
		double bx = l1.hpunt2.x;double by = l1.hpunt2.y;double bz = l1.hpunt2.z;
		double cx = l2.hpunt1.x;double cy = l2.hpunt1.y;double cz = l2.hpunt1.z;
		double dx = l2.hpunt2.x;double dy = l2.hpunt2.y;double dz = l2.hpunt2.z;
		
		double a1 = bx-ax;double a2 = by-ay;double a3 = bz-az;
		double b1 = cx-dx;double b2 = cy-dy;double b3 = cz-dz;
		double c1 = cx-ax;double c2 = cy-ay;double c3 = cz-az;
		
		double d = a1*b2 - a2*b1;
		double k=0; 
		double m;
		double afwijking ;
		if(Math.abs(d)>0.0000001)
		{	k = (c1*b2-c2*b1)/d;
			m = (a1*c2-a2*c1)/d;
			afwijking = c3-(a3*k+b3*m);
		}
		else
		{	d = a2*b3 - a3*b2;
			if(Math.abs(d)>0.0000001)
			{	k = (c2*b3-c3*b2)/d;
				m = (a2*c3-a3*c2)/d;
				afwijking = c1-(a1*k+b1*m);
			}
			else
			{	d = a1*b3 - a3*b1;
				if(Math.abs(d)>0.0000001)
				{	k = (c1*b3-c3*b1)/d;
					m = (a1*c3-a3*c1)/d;
					afwijking = c2-(a2*k+b2*m);
				}
				else return;
				
			}
		}
		
		
		
		if(afwijking<0.00001 && afwijking>-0.00001 && k<1 && k>0 && m<1 && m>0)
		{	hoekpuntenNieuw[aantalHpNieuw] = new Hoekpunt(ax + k*(bx-ax) , ay + k*(by-ay) , az + k*(bz-az));
			aantalHpNieuw++;
			
			/*Hoekpunt[] hp = new Hoekpunt[tv.aantalHoekpunten+1];
			for(int i=0 ; i<tv.aantalHoekpunten ; i++)
			{	hp[i]=tv.hoekpunten[i];
			}
			hp[tv.aantalHoekpunten]= new Hoekpunt(ax + k*(bx-ax) , ay + k*(by-ay) , az + k*(bz-az));
			tv.hoekpunten = hp;
			tv.aantalHoekpunten++;*/
			
		}
		
	}
	void maakAlleSnijpunten()
	{	for(int i=0 ; i<tv.aantalLijnen ; i++)
		{	for(int j=0 ; j<i ; j++)
			{	maakSnijpunt(tv.lijnen[j],tv.lijnen[i]);
			}
		}
	}
	
	void zoekSnijpunten()
	{	for(int i=0 ; i<tv.aantalLijnen-1 ; i++)
		{	maakSnijpunt(tv.lijnen[tv.aantalLijnen-1],tv.lijnen[i]);
		}
		
	}
	boolean checkVlak(Hoekpunt hpt)
	{	double ux = tv.hoekpunten[puntnr[1]].x - tv.hoekpunten[puntnr[0]].x;
		double uy = tv.hoekpunten[puntnr[1]].y - tv.hoekpunten[puntnr[0]].y;
		double uz = tv.hoekpunten[puntnr[1]].z - tv.hoekpunten[puntnr[0]].z;
		double vx = tv.hoekpunten[puntnr[2]].x - tv.hoekpunten[puntnr[1]].x;
		double vy = tv.hoekpunten[puntnr[2]].y - tv.hoekpunten[puntnr[1]].y;
		double vz = tv.hoekpunten[puntnr[2]].z - tv.hoekpunten[puntnr[1]].z;
		double nx = uy*vz - uz*vy;	
		double ny = uz*vx - ux*vz;
		double nz = ux*vy - uy*vx;
		double d = tv.hoekpunten[puntnr[0]].x*nx + tv.hoekpunten[puntnr[0]].y*ny + tv.hoekpunten[puntnr[0]].z*nz;
		double dn = hpt.x*nx + hpt.y*ny + hpt.z*nz;
		if(d-dn < 0.0001 && d-dn > -0.0001)return true;
		else return false; 
	}
	
	
	public void sliderAction()
	{
		int stand = zijdeSl.geefStand();
		int lengte = zijdeSl.geefLengte();
		double kMin = kMinFac * k50;
		double kMax = kMaxFac * k50;
		zoomFac = (double) stand / (double) (lengte);
		k = zoomFac * (kMax - kMin) + kMin;
		
		tekenOpnieuw();
	}
	
	public void onClick(ClickEvent e)
	{		
	
			//if (e.getSource() == zijdeSl)
			//{	
			//	int stand = zijdeSl.geefStand();
			//	int lengte = zijdeSl.geefLengte();
			//	double kMin = kMinFac * k50;
			//	double kMax = kMaxFac * k50;
			//	zoomFac = (double) stand / (double) (lengte);
			//	k = zoomFac * (kMax - kMin) + kMin; 

			//}
			
			if (e.getSource() == basisKnop)
			{	

				if (basisKnop.getText().equals("Verberg basis"))
				{	
					basisZichtbaar = false;
					basisKnop.setText("Toon basis");
					
				}
				else
				{	basisZichtbaar = true;
					basisKnop.setText("Verberg basis");
				}
			}	
			else if (e.getSource() == terugKnop)
			{	aantalPuntenRood = 0;
				wisTrefpunten();
				if (maakLijn)
				{	if (tv.aantalLijnen > 0)
						tv.wisVorigeLijn();
				}
				if (maakVlak)
				{	if (tv.aantalVlakken > 0)
						tv.wisVorigVlak();
				}
			}
			else if (e.getSource() == lijnKnop)
			{	
				if (lijnKnop.isDown())
				{
				maakLijn = true;
				maakVlak = false;
				aantalPuntenRood=0;
				wisTrefpunten();
				
				vlakKnop.setDown(false);
				}
				else
				{
					maakLijn = false;
				}
			}
			else if (e.getSource() == vlakKnop)
			{	
				if (vlakKnop.isDown())
				{
				maakLijn = false;
				maakVlak = true;
				aantalPuntenRood = 0;
				wisTrefpunten();
				
				lijnKnop.setDown(false);
				}
				else
				{
					maakVlak = false;
				}
			}
			else if (e.getSource() == wisKnop)
			{	aantalPuntenRood = 0;
				wisTrefpunten();
				tv.wisLijnen();
				aantalHpNieuw = 0;
				for (int i = 0; i < v.aantalHoekpunten; i++)
				{	hoekpuntenNieuw[aantalHpNieuw] = v.hoekpunten[i];
					aantalHpNieuw++;
				}
			}
			else if(e.getSource() == wisVKnop)
			{	aantalPuntenRood = 0;
				wisTrefpunten();
				tv.wisVlakken();
				
			}
			tekenOpnieuw();
		
	}

	
/*	
	public void nieuw()
	{	String soortV = (String) kiesV.getSelectedItem();

		if (soortV == TekenVeelvlakOpdr.rb.getString("kubusLabel"))
			v = new Kubus(1);
		else if (soortV == TekenVeelvlakOpdr.rb.getString("octaederLabel"))
			v = (new Kubus(Math.sqrt(3))).dualiseer();
		else if (soortV == TekenVeelvlakOpdr.rb.getString("icosaederLabel"))
			v = new Icosaeder(1);
		else if (soortV == TekenVeelvlakOpdr.rb.getString("dodecaederLabel"))
			v = (new Icosaeder(1.3)).dualiseer();
		else if (soortV == TekenVeelvlakOpdr.rb.getString("tetraederLabel"))
			v = new Tetraeder(1);
		else if (soortV == TekenVeelvlakOpdr.rb.getString("prismaLabel"))
			v = new Prisma(0.7,6,1);
		else if (soortV == TekenVeelvlakOpdr.rb.getString("ruiten12Label"))
			v = new Kuboctaeder(1.8).dualiseerb();
		
		for (int i = 0; i < v.aantalVlakken; i++)
		{	v.vlakken[i].vulkleur = "transparant";
		}
		aantalPuntenRood = 0;
		wisTrefpunten();
		tv.wisLijnen();
		aantalHpNieuw = 0;
		tv = new Veelvlak();
		
		for(int i=0 ; i<v.aantalHoekpunten ; i++)
		{	hoekpuntenNieuw[aantalHpNieuw] = v.hoekpunten[i];
			aantalHpNieuw++;
		}
		tekenOpnieuw();
	}
*/	
	public void zetBasis(int figNr, int aantalRibPunten)
	{	if (figNr==0)
		{	v = new Kubus(1);
			int n = aantalRibPunten;
			int aantalHp = 8;
			int aantalRib = 12;
			aantalGetekendeHoekpunten = aantalHp + n*aantalRib;
			Hoekpunt[] hp = new Hoekpunt[v.aantalHoekpunten+n*aantalRib];
			for(int i=0 ; i<v.aantalHoekpunten ; i++)
			{	hp[i]=v.hoekpunten[i];
			}
			v.hoekpunten = hp;
			v.aantalHoekpunten+=n*aantalRib;
			
			int[] rib = {0,1,1,2,2,3,3,0,0,4,1,5,2,6,3,7,4,5,5,6,6,7,7,4};
			for(int i=0 ; i<aantalRib ; i++)
			{	for(int j=0 ; j<n ; j++)
				{	v.hoekpunten[aantalHp+n*i+j] = new Hoekpunt(((n-j)*v.hoekpunten[rib[2*i]].x + (j+1)*v.hoekpunten[rib[2*i+1]].x)/(n+1),  
															((n-j)*v.hoekpunten[rib[2*i]].y + (j+1)*v.hoekpunten[rib[2*i+1]].y)/(n+1),
															((n-j)*v.hoekpunten[rib[2*i]].z + (j+1)*v.hoekpunten[rib[2*i+1]].z)/(n+1));
				}
			}
		}
		else if(figNr==1)
		{
			v = (new Kubus(Math.sqrt(3))).dualiseer();
			int n = aantalRibPunten;
			int aantalHp = 6;
			int aantalRib = 12;
			
			Hoekpunt[] hp = new Hoekpunt[v.aantalHoekpunten+n*aantalRib];
			for(int i=0 ; i<v.aantalHoekpunten ; i++)
			{	hp[i]=v.hoekpunten[i];
			}
			v.hoekpunten = hp;
			v.aantalHoekpunten+=n*aantalRib;
			
			int[] rib = {0,1,0,2,0,3,0,4,1,2,2,3,3,4,4,1,5,1,5,2,5,3,5,4};
			for(int i=0 ; i<aantalRib ; i++)
			{	for(int j=0 ; j<n ; j++)
				{	v.hoekpunten[aantalHp+n*i+j] = new Hoekpunt(((n-j)*v.hoekpunten[rib[2*i]].x + (j+1)*v.hoekpunten[rib[2*i+1]].x)/(n+1),  
															((n-j)*v.hoekpunten[rib[2*i]].y + (j+1)*v.hoekpunten[rib[2*i+1]].y)/(n+1),
															((n-j)*v.hoekpunten[rib[2*i]].z + (j+1)*v.hoekpunten[rib[2*i+1]].z)/(n+1));
				}
			}
		}
		else if(figNr==2)
		{	
			v = new Icosaeder(1);
			int n = aantalRibPunten;
			int aantalHp = 12;
			int aantalRib = 30;
			aantalGetekendeHoekpunten = aantalHp + n*aantalRib;
			Hoekpunt[] hp = new Hoekpunt[v.aantalHoekpunten+n*aantalRib];
			for(int i=0 ; i<v.aantalHoekpunten ; i++)
			{	hp[i]=v.hoekpunten[i];
			}
			v.hoekpunten = hp;
			v.aantalHoekpunten+=n*aantalRib;
			
			int[] rib = {0,1,0,2,0,3,0,4,0,5,
						 1,2,2,3,3,4,4,5,5,1,
						 1,10,1,6,2,6,2,7,3,7,
						 3,8,4,8,4,9,5,9,5,10,
						 
						 6,7,7,8,8,9,9,10,10,6,
						 6,11,7,11,8,11,9,11,10,11				 
			};
			for(int i=0 ; i<aantalRib ; i++)
			{	for(int j=0 ; j<n ; j++)
				{	v.hoekpunten[aantalHp+n*i+j] = new Hoekpunt(((n-j)*v.hoekpunten[rib[2*i]].x + (j+1)*v.hoekpunten[rib[2*i+1]].x)/(n+1),  
															((n-j)*v.hoekpunten[rib[2*i]].y + (j+1)*v.hoekpunten[rib[2*i+1]].y)/(n+1),
															((n-j)*v.hoekpunten[rib[2*i]].z + (j+1)*v.hoekpunten[rib[2*i+1]].z)/(n+1));
				}
			}
		}
		else if(figNr==3)
		{
			v = (new Icosaeder(1.3)).dualiseer();
			int n = aantalRibPunten;
			int aantalHp = 20;
			int aantalRib = 30;
			aantalGetekendeHoekpunten = aantalHp + n*aantalRib;
			Hoekpunt[] hp = new Hoekpunt[v.aantalHoekpunten+n*aantalRib];
			for(int i=0 ; i<v.aantalHoekpunten ; i++)
			{	hp[i]=v.hoekpunten[i];
			}
			v.hoekpunten = hp;
			v.aantalHoekpunten+=n*aantalRib;
			
			int[] rib = {0,1,1,2,2,3,3,4,4,0,
						 0,5,1,6,2,7,3,8,4,9,
						 5,10,6,11,7,12,8,13,9,14,
						 10,6,11,7,12,8,13,9,14,5,
						 10,15,11,16,12,17,13,18,14,19,
						 15,16,16,17,17,18,18,19,19,15				 
			};
			for(int i=0 ; i<aantalRib ; i++)
			{	for(int j=0 ; j<n ; j++)
				{	v.hoekpunten[aantalHp+n*i+j] = new Hoekpunt(((n-j)*v.hoekpunten[rib[2*i]].x + (j+1)*v.hoekpunten[rib[2*i+1]].x)/(n+1),  
															((n-j)*v.hoekpunten[rib[2*i]].y + (j+1)*v.hoekpunten[rib[2*i+1]].y)/(n+1),
															((n-j)*v.hoekpunten[rib[2*i]].z + (j+1)*v.hoekpunten[rib[2*i+1]].z)/(n+1));
				}
			}
		}
		else if(figNr==4)
		{
			v = new Tetraeder(1);
			int n = aantalRibPunten;
			int aantalHp = 4;
			int aantalRib = 6;
			
			Hoekpunt[] hp = new Hoekpunt[v.aantalHoekpunten+n*aantalRib];
			for(int i=0 ; i<v.aantalHoekpunten ; i++)
			{	hp[i]=v.hoekpunten[i];
			}
			v.hoekpunten = hp;
			v.aantalHoekpunten+=n*aantalRib;
			
			int[] rib = {0,1,0,2,0,3,1,2,1,3,2,3};
			for(int i=0 ; i<aantalRib ; i++)
			{	for(int j=0 ; j<n ; j++)
				{	v.hoekpunten[aantalHp+n*i+j] = new Hoekpunt(((n-j)*v.hoekpunten[rib[2*i]].x + (j+1)*v.hoekpunten[rib[2*i+1]].x)/(n+1),  
															((n-j)*v.hoekpunten[rib[2*i]].y + (j+1)*v.hoekpunten[rib[2*i+1]].y)/(n+1),
															((n-j)*v.hoekpunten[rib[2*i]].z + (j+1)*v.hoekpunten[rib[2*i+1]].z)/(n+1));
				}
			}
		}
		else if(figNr==5)
		{
			v = new Prisma(0.7,6,1);
			
		}
		else if(figNr==6)
		{
			v = new Kuboctaeder(1.8).dualiseerb();
			
		}
		
		for(int i=0 ; i<v.aantalVlakken ; i++)
		{	v.vlakken[i].vulkleur = "transparant";
		}
		
//System.out.println("zetBasis fig = " + figNr + " hp = " + aantalRibPunten);		
		tekenOpnieuw();
	}
	
/*	
	public void itemStateChanged(ItemEvent e)
	{	boolean animatieWasAan = false;
		
		nieuw();
		
		if (animatieStatus())
		{	onderbreekAnimatie();
			animatieWasAan = true;
		}
		tekenOpnieuw();
		if (animatieWasAan)
			beginAnimatie();
	}
*/
	public void muisDrukActie()
	{	
		if (!muisDrukAan)
			return;
			
		boolean raak = false;
		int max = tv.aantalHoekpunten;
		// loop langs de trefpunten, dat zijn tv's hoekpunten, 
		for (int i = 0; i < max; i++)
		{	double ax = trefpunten[i].x - geefDrukx();
			double ay =  trefpunten[i].y - geefDruky();
			if ((ax < 4 && ax > -4) && (ay < 4 && ay > -4))
			{	raak = true;
				if (maakLijn)
				{	if (aantalPuntenRood == 0)
					{	puntnr1 = i;
						aantalPuntenRood++;
						trefpuntRaak[i] = true;
					}
					else 
					{	puntnr2 = i;
						if (puntnr1 != puntnr2)
							tv.maakLijn(puntnr1,puntnr2,"rood");
						aantalPuntenRood = 0;
						wisTrefpunten();
						trefpuntRaak[puntnr1] = false;
						if (tv.aantalLijnen > 1)
							zoekSnijpunten();
					}
				}
				else if (maakVlak)
				{	if (aantalPuntenRood > 0 && i == puntnr[aantalPuntenRood-1])
					{	wisTrefpunten();
						aantalPuntenRood = 0;
					}
					else if (aantalPuntenRood > 1 && i == puntnr[aantalPuntenRood-2])
					{	wisTrefpunten();
						aantalPuntenRood = 0;
					}
					else if(i!=puntnr[0] || aantalPuntenRood==0)
					{	if(aantalPuntenRood<3)
						{	puntnr[aantalPuntenRood] = i;
							aantalPuntenRood++;
							trefpuntRaak[i]=true;
						}
						else if(checkVlak(tv.hoekpunten[i]))
						{   puntnr[aantalPuntenRood] = i;
							aantalPuntenRood++;
							trefpuntRaak[i]=true;
						}
						
					}
					else 
					{	puntnr[aantalPuntenRood] = i; 
						//Vlak vl = new Vlak(aantalPuntenRood);
						//for(int j=0 ; j<aantalPuntenRood ; j++)
						//{	vl.punten[j] = tv.hoekpunten[puntnr[j]];
						//}
						tv.voegVlakToe(aantalPuntenRood,puntnr);
						//tv.vlakken[tv.aantalVlakken] = vl;
						//tv.aantalVlakken++;
						aantalPuntenRood=0;
						wisTrefpunten();
					}
				}
				break;
			} // trefpunt geraakt
		} // for loop trefpunten
		if (raak)
		{	tekenOpnieuw();
			return;
		}
		else 
		{	raak = false; 
			max = aantalHpNieuw;
			for (int i = 0; i < max; i++)
			{	double ax = trefpuntenNieuw[i].x - geefDrukx();
				double ay = trefpuntenNieuw[i].y - geefDruky();
				if ((ax < 4 && ax > -4) && (ay < 4 && ay > -4))
				{	//if(aantalPuntenRood>2 && !checkVlak(hoekpuntenNieuw[i]))
					//{	return;
					//}
					raak = true;
					//tv.hoekpunten[tv.aantalHoekpunten]=hoekpuntenNieuw[i];
					tv.voegHoekpuntToe(hoekpuntenNieuw[i].x, hoekpuntenNieuw[i].y, hoekpuntenNieuw[i].z);
					tv.aantalHoekpunten--;
					trefpuntRaak[tv.aantalHoekpunten]= true;
					
					if(maakLijn)
					{	if(aantalPuntenRood==0)
						{	puntnr1 = tv.aantalHoekpunten;
							aantalPuntenRood++;
							trefpuntRaak[tv.aantalHoekpunten]=true;
						}
						else 
						{	puntnr2 = tv.aantalHoekpunten;
							if(puntnr1!=puntnr2)tv.maakLijn(puntnr1,puntnr2,"rood");
							aantalPuntenRood=0;
							wisTrefpunten();
							trefpuntRaak[puntnr1]=false;
							if(tv.aantalLijnen>1)zoekSnijpunten();
						}
					}
					else if(maakVlak)
					{	if(aantalPuntenRood>0 && tv.aantalHoekpunten==puntnr[aantalPuntenRood-1])
						{	wisTrefpunten();
							aantalPuntenRood=0;
						}
						else if(aantalPuntenRood>1 && tv.aantalHoekpunten==puntnr[aantalPuntenRood-2])
						{	wisTrefpunten();
							aantalPuntenRood=0;
						}
						else if(tv.aantalHoekpunten!=puntnr[0] || aantalPuntenRood==0)
						{	if(aantalPuntenRood<3)
							{	puntnr[aantalPuntenRood] = tv.aantalHoekpunten;
								aantalPuntenRood++;
								trefpuntRaak[tv.aantalHoekpunten]=true;
							}
							else if(checkVlak(tv.hoekpunten[tv.aantalHoekpunten]))
							{   puntnr[aantalPuntenRood] = tv.aantalHoekpunten;
								aantalPuntenRood++;
								trefpuntRaak[tv.aantalHoekpunten]=true;
							}
							else 
							{	trefpuntRaak[tv.aantalHoekpunten]= false;
								tv.aantalHoekpunten--;
								tv.hpRijAantal = tv.hpRijAantal - 3;
							}
						}
						else 
						{	puntnr[aantalPuntenRood] = tv.aantalHoekpunten; 
							//Vlak vl = new Vlak(aantalPuntenRood);
							//for(int j=0 ; j<aantalPuntenRood ; j++)
							//{	vl.punten[j] = tv.hoekpunten[puntnr[j]];
							//}
							//tv.vlakken[tv.aantalVlakken] = vl;
							//tv.aantalVlakken++;
							tv.voegVlakToe(aantalPuntenRood,puntnr);
							aantalPuntenRood=0;
							wisTrefpunten();
						}
						
					}
					tv.aantalHoekpunten++;
					break;
				}
			}
			if (raak)
			{	tekenOpnieuw();
				return;
			}
		}
	}
	public void muisSleepActie()
	{	if (!vaktekening)
		{	
		
			xhoek -= 0.5*geefSleepdy();
			yhoek += 0.5*geefSleepdx();
			matres.initialiseer();
			matres.xdraai(beginx+xhoek);
			matres.ydraai(beginy+yhoek);
			



			tekenOpnieuw();
			
//System.out.println("xhoek = " + xhoek + " yhoek = " + yhoek);			
		}
	}
	
/*	
	public void muisLosActie()
	{	if ((klikAan && (geefDrukx()-geefX())*(geefDrukx()-geefX()) + 
			            (geefDruky()-geefY())*(geefDruky()-geefY()) < 10))
		{	muisKkActie();
		}
		tekenOpnieuw();
	}
*/	
/*
	public void muisKkActie()
	{	
		// p is vanzelf gesorteerd
		for (int j = tv.aantalVlakken - 1; j > -1; j--)
		{	
			if (p[j].contains(geefDrukx(),geefDruky()))
			{	
				if (tv.vlakken[j].vulkleur.equals("oranje"))
				{   tv.vlakken[j].vulkleur = "roodoranje";
					aantalVlakkenRood++;
				}
				else 
				{	tv.vlakken[j].vulkleur = "oranje";
					aantalVlakkenRood--;
				}
				
				tekenOpnieuw();
				return;
			}
		}
	}
*/
/*	
	public void animatie()
	{	while(animatieStatus() && !vaktekening)
		{	matrot.initialiseer();
			matrot.ydraaiAbs(1);
			matrot.xdraaiAbs(0);
			matres.mult(matrot);
			tekenOpnieuw();
		}
	}
*/
/*	
	public void numberChanged(String name,double val)
	{	boolean animatieWasAan = false;
		if(animatieStatus())
		{	animatieWasAan = true;
			onderbreekAnimatie();
		}
		
		if(name=="zijde")
		{	k = val;
		}
		if(!animatieStatus())tekenOpnieuw();
		if(animatieWasAan)beginAnimatie();
	}
*/

/*	
	public void addActionListener(ActionListener al) 
	{
		// TODO Auto-generated method stub
		
	}
*/
	

	public int geefAsHoogte() {
		// TODO Auto-generated method stub
		return 0;
	}


	//public InteractieEditPanel getEditPanel() {
	//	return this;
	//}


	public HashMap getEditState() 
	{
		return getState();
	}


	public int getIpId() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int getScore() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int getScoreMax() {
		// TODO Auto-generated method stub
		return 0;
	}


	public boolean isCorrect() {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean isFout() {
		// TODO Auto-generated method stub
		return false;
	}


	public void kijkNa() {
		// TODO Auto-generated method stub
		
	}


	public void kijkNa(int stapNr) {
		// TODO Auto-generated method stub
		
	}


	public void opnieuw() {
		// TODO Auto-generated method stub
		
	}


	public void setEditState(HashMap h) 
	{
		setState(h);
		//rg.add(kiesV);
		
	}


	public void wis() 
	{
		// TODO Auto-generated method stub
		
	}


	public void zetMaat() 
	{
		// TODO Auto-generated method stub
		
	}


	public void zetMode(int mode) 
	{
		// TODO Auto-generated method stub
		
	}


	public void zetNagekeken(boolean b) {
		// TODO Auto-generated method stub
		
	}


	public void zetOpdracht(HashMap h, String[] randomVars, HashMap randomValues) 
	{
		setState(h);
//		rg.remove(kiesV);
		
	}


	public void zetBreedte(int b) {
		// TODO Auto-generated method stub
		
	}


	public void zetHoogte(int h) {
		// TODO Auto-generated method stub
		
	}
}