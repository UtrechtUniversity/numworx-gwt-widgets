package fi.graphtoolgwt.client;



import java.util.HashMap;
import java.util.Map;
//import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
//import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
//import fi.graphtool.Slider;
import com.google.gwt.touch.client.Point;

import fi.wiskopdr.expressies.Expressie;
import gwt.awt.geom.Area;
import gwt.awt.Rectangle;
import gwt.awt.geom.GeneralPath;
import gwt.awt.geom.PathIterator;




public class GrafiekGWTVeld {
	
	private static Logger logger = Logger.getLogger("GrafiekGWTVeld");
	
	/* contstants */
	private final int cMaxPiLinesOnScreen = 8;

	public Canvas grafiekGWTCanvas;
	public Context2d gIm;
	//public g2d gIm;
	
	private int eenheid = 16;
	int breedte, hoogte;
	int offset = 5;
	//int veldb, veldh;
	
	//private ZoomDraad zoomDraad;
	
	//TekstPopup tf;
	
	boolean tracing = false;
	//SliderGWT slider;
	//slider:
	//private int lengte;
	int stand;
	private int minimum=0;
	//private int muisStartX;
	boolean sliderRaak;
	
	int tracex = 0;
	double tracexD = tracex;
	
	
	int beginwaarde;
	int selectnummer;
	//private String xAsNaam, yAsNaam;
	
	public int xAsNaamLinks, xAsNaamRechts, xAsNaamBoven, xAsNaamOnder, yAsNaamLinks, yAsNaamRechts, yAsNaamBoven, yAsNaamOnder;
	
	CssColor roosterKleur = CssColor.make(210, 210, 210);
	CssColor roosterKleurLicht = CssColor.make(240, 240, 240);
	static CssColor zwart = CssColor.make(0, 0, 0);
	static CssColor wit = CssColor.make(255, 255, 255);
	static CssColor rood = CssColor.make(255, 0, 0);
	static CssColor geel = CssColor.make(255,255,200);
	static CssColor grijs = CssColor.make(100,100,100);

	//boolean mouseDown;
	
	
	//private DecimalFormatSymbols dfs;
	//private Font font = new Font("SansSerif", Font.PLAIN, 10);
	//private FontMetrics fm = new FontMetrics(font);
	//private DecimalFormat df;
	
	private final GraphToolGWT interactiePanel;
	
	
	
	
	public GrafiekGWTVeld(GraphToolGWT interactiePanel, int w, int h)
	{	this.interactiePanel = interactiePanel;
		grafiekGWTCanvas = Canvas.createIfSupported();
		
		
		setSize(w, h);
		
		beginwaarde = 0;
		selectnummer = 999;
		
		stand = 3;
		setSize(w, h);
	}
	
	void setSize(int w, int h) {
		breedte = w;
		hoogte = h;
		grafiekGWTCanvas.setWidth(w + "px");
		grafiekGWTCanvas.setHeight(h + "px");
		grafiekGWTCanvas.setCoordinateSpaceWidth(w);
		grafiekGWTCanvas.setCoordinateSpaceHeight(h);
	}
	
	public Canvas getCanvas()
	{
		return grafiekGWTCanvas;
	}
	
	public void initContext2d() 
	{
		gIm = grafiekGWTCanvas.getContext2d();
		
	}
	
	public HashMap<String,Object> getState()
	{
		
		HashMap<String,Object> h = new HashMap<String,Object>();
		
		return h;
	}
	
	public void setState(Map<String, Object> launchState)
	{
		if(launchState != null)
		{
			ObjectMap h = JSONUtilities.wrapMap(launchState);
			if(h.containsKey("beginwaarde"))
				beginwaarde = h.getInt("beginwaarde");
			if(h.containsKey("selectnummer"))
				selectnummer = h.getInt("selectnummer");
			//nodig?
			if(h.containsKey("tracexD"))
			{	tracexD = h.getDouble("tracexD");
			}
		}
		
		tracex = (int) Math.round(tracexD);
		stand = tracex;
	}
	
	public double pixelsXtoValue(double pixelsX) { 
		/* This function also needs to perform for values in between pixels, therefore a double is used to represent pixelsX */
		double scalingMultiplier;		
		if (interactiePanel.manualScalingX) {
			scalingMultiplier = interactiePanel.eenheidxValue;
		}
		else {
			scalingMultiplier = interactiePanel.schaalFactorX;			
		}
		double valueX = (pixelsX-interactiePanel.beginx)/interactiePanel.eenheidxD*scalingMultiplier;
		if (interactiePanel.xAsLog) {
			valueX = Math.pow(10, valueX);
		} 
		return valueX;
	}

	public double valueXtoPixels(double valueX) {
		double pixelsX;
		double scalingDivider;
		if (interactiePanel.manualScalingX) {
			scalingDivider = interactiePanel.eenheidxValue;
		}
		else {
			scalingDivider = interactiePanel.schaalFactorX;			
		}
		
		double valX;
		if (interactiePanel.xAsLog) {
			valX = Math.log10(valueX);		
		} else {
			valX = valueX;
		}
        pixelsX = interactiePanel.beginx + valX*interactiePanel.eenheidxD/scalingDivider;
		return pixelsX;
	}
	
	public double valueYtoPixels(double valueY) {
		double pixelsY;
		double scalingDivider;
		if (interactiePanel.manualScalingY) {
			scalingDivider = interactiePanel.eenheidyValue;
		}
		else {
			scalingDivider = interactiePanel.schaalFactorY;			
		}
		double valY;
		if (interactiePanel.yAsLog) {
			valY = Math.log10( valueY);
		} else {
			valY = valueY;
		}
        pixelsY = hoogte - (interactiePanel.beginy + valY*interactiePanel.eenheidyD/scalingDivider);
		return pixelsY; 
	}
	
	
	public void paint()
	{
		gIm.clearRect(0, 0, breedte, hoogte);

		int bx = (int)Math.round(interactiePanel.beginx);			
		int by = (int)Math.round(interactiePanel.beginy);
		
		int maxWoordBreedteY = 0;
		int maxWoordHoogteX = 10;
		boolean witruimteX = by <= 12;
		boolean witruimteY = false;
		int maxHoogteLijn = hoogte-Math.max(witruimteX?maxWoordHoogteX:0,(interactiePanel.yPositief?by:0));
		
		String fontString = "10px sans-serif";
		gIm.setFont(fontString);
		
		gIm.setLineWidth(0.5d);
		
		int scalingMultiplyX = 1; // Standard remain 1, except i case of manual Scaling & zoom-in causes the raster to become to small (otherwise).
		int scalingMultiplyY = 1;
		
		while ( (interactiePanel.manualScalingX) && (interactiePanel.eenheidxD * scalingMultiplyX) < (0.2 * interactiePanel.eenheid) ) {
			scalingMultiplyX *= 2;
		}
		while ( (interactiePanel.manualScalingX) && (interactiePanel.eenheidyD * scalingMultiplyY) < (0.2 * interactiePanel.eenheid) ) {
			scalingMultiplyY *= 2;
		}

		if (interactiePanel.roosterZichtbaar || interactiePanel.schaalZichtbaar)
		{	int imin = -(int)Math.round(interactiePanel.beginx/(interactiePanel.eenheidx * scalingMultiplyX)); 
			int imax = 1+breedte/(interactiePanel.eenheidx * scalingMultiplyX)-(int)Math.round(interactiePanel.beginx/(interactiePanel.eenheidx * scalingMultiplyX));
			int jmin = -(int)Math.round(interactiePanel.beginy/(interactiePanel.eenheidy * scalingMultiplyY)); 
			int jmax = 1+hoogte/(interactiePanel.eenheidy* scalingMultiplyY)-(int)Math.round(interactiePanel.beginy/(interactiePanel.eenheidy*scalingMultiplyY));
			
			for(int j=jmin+1 ; j<jmax-1 ; j++) {	
//				String getal = Double.toString(interactiePanel.schaalFactorY*(j));
//				if(interactiePanel.yAsLog)
//					getal = 10 + toSuperScript(getal);
				String getal; 
				if (interactiePanel.manualScalingY) {
					getal = Double.toString(interactiePanel.eenheidyValue*(j) * scalingMultiplyY);
				} else { // standard scaling, possibly logarithmic
					getal = Double.toString(interactiePanel.schaalFactorY*(j));
					if(interactiePanel.yAsLog) {
						getal = 10 + toSuperScript(getal);
					}					
				}
				
				if(getal.contains("."))
				{	getal = getal.substring(0, Math.min(getal.indexOf(".") + 3, getal.length()));
					while(getal.endsWith("0"))
					{
						getal = getal.substring(0, getal.length() - 1);
					}
					if(getal.endsWith("."))
					{	getal = getal.substring(0, getal.length() - 1);
					
					}
				}
				TextMetrics tm = gIm.measureText(getal);
				int woordbreedte = (int) Math.round(tm.getWidth());
				if(j!=0 && j%2==0 && (!interactiePanel.yPositief || j>0))
				{	maxWoordBreedteY = Math.max(maxWoordBreedteY, woordbreedte);
				}
				
			}
			witruimteY = maxWoordBreedteY >= bx - 2;
			//log-roosterlijnen tekenen (iets lichter dan gewone roosterlijnen):
			gIm.setStrokeStyle(roosterKleurLicht);
			if(interactiePanel.roosterX)
			{	gIm.beginPath();
				for(int i = imin+1; i < imax; i++)
				{	if((!interactiePanel.xPositief || i > 0) && interactiePanel.xAsLog && !interactiePanel.roosterGrof)
					{	for(int k = 1; k < 10; k++)
						{	gIm.moveTo((int) (bx + i*interactiePanel.eenheidxD + Math.log10(k)*interactiePanel.eenheidxD), 0);
							gIm.lineTo((int) (bx + i*interactiePanel.eenheidxD + Math.log10(k)*interactiePanel.eenheidxD), maxHoogteLijn);
						}
					}
				}
				gIm.stroke();
			}
			if(interactiePanel.roosterY)
			{	gIm.beginPath();
				for(int j = jmin; j < jmax; j++)
				{	if((!interactiePanel.yPositief || j > 0) && interactiePanel.yAsLog && !interactiePanel.roosterGrof)
					{	for(int k = 1; k < 10; k++)
						{	gIm.moveTo(Math.max(witruimteY?maxWoordBreedteY:0, interactiePanel.xPositief?bx:0), (int) (hoogte - (by + j*interactiePanel.eenheidyD + Math.log10(k)*interactiePanel.eenheidyD)));
							gIm.lineTo(breedte, (int) (hoogte - (by + j*interactiePanel.eenheidyD + Math.log10(k)*interactiePanel.eenheidyD)));
						}
					}
				}
				gIm.stroke();
			}
			//gewone roosterlijnen tekenen:
			gIm.setStrokeStyle(roosterKleur);
			gIm.setFillStyle(zwart);
			for(int i=imin+1 ; i<imax ; i++)
			{	
//				String getal = Double.toString(interactiePanel.schaalFactorX*(i));
//				if(interactiePanel.xAsLog)
//					getal = 10 + toSuperScript(getal);
				String getal; 
				if (interactiePanel.manualScalingX) {
					getal = Double.toString(interactiePanel.eenheidxValue*(i)* scalingMultiplyX);
				} else { // standard scaling, possibly logarithmic
					getal = Double.toString(interactiePanel.schaalFactorX*(i));
					if(interactiePanel.xAsLog) {
						getal = 10 + toSuperScript(getal);
					}					
				}

				if(getal.contains("."))
				{	getal = getal.substring(0, Math.min(getal.indexOf(".") + 3, getal.length()));
					while(getal.endsWith("0"))
					{
						getal = getal.substring(0, getal.length() - 1);
					}
					if(getal.endsWith("."))
					{
						getal = getal.substring(0, getal.length() - 1);
					}
				}
				TextMetrics tm = gIm.measureText(getal);
				int woordbreedte = (int) Math.round(tm.getWidth());
				int xLabel = (int)(interactiePanel.beginx+i*interactiePanel.eenheidxD * scalingMultiplyX -woordbreedte/2);
//				int yLabel = Math.min(hoogte-1, hoogte-by+11);
				int yLabel = Math.max(11, Math.min(hoogte-4, hoogte-by+11));

				boolean schaalTekenen = (i%2 == 0 || interactiePanel.xAsLog) && interactiePanel.schaalZichtbaar && interactiePanel.schaalX;
				//witruimteX = yLabel==hoogte-1;
				if(interactiePanel.roosterZichtbaar && interactiePanel.roosterX && (!interactiePanel.xPositief || i > 0))
				{	if(schaalTekenen) 
					{	gIm.beginPath();
						gIm.moveTo((int)(bx+i*interactiePanel.eenheidxD * scalingMultiplyX ), 0);
						gIm.lineTo((int)(bx+i*interactiePanel.eenheidxD * scalingMultiplyX ), Math.min(yLabel - 9, maxHoogteLijn));
						if(maxHoogteLijn > yLabel + 2)
						{	gIm.moveTo((int)(bx+i*interactiePanel.eenheidxD * scalingMultiplyX ), yLabel + 2);
							gIm.lineTo((int)(bx+i*interactiePanel.eenheidxD * scalingMultiplyX ), maxHoogteLijn);
						}
						gIm.stroke();
					}
					else if(i%2 == 0 || !interactiePanel.roosterGrof || interactiePanel.xAsLog)
					{	gIm.beginPath();
						gIm.moveTo((int)(bx+i*interactiePanel.eenheidxD * scalingMultiplyX ),0);
						gIm.lineTo((int)(bx+i*interactiePanel.eenheidxD * scalingMultiplyX ), maxHoogteLijn);
						gIm.stroke();
					}
				}
				if((!interactiePanel.xPositief || i > 0) && schaalTekenen && i != 0)
				{
					gIm.fillText(getal,	xLabel,	yLabel);
					//gIm.setColor(lijnenKleur);
				}
			}
			for(int j=jmin ; j<jmax ; j++) {	
//				String getal = Double.toString(interactiePanel.schaalFactorY*(j)); 
//				if(interactiePanel.yAsLog)
//					getal = 10 + toSuperScript(getal);
				String getal; 
				if (interactiePanel.manualScalingY) {
					getal = Double.toString(interactiePanel.eenheidyValue*(j) * scalingMultiplyY);
				} else { // standard scaling, possibly logarithmic
					getal = Double.toString(interactiePanel.schaalFactorY*(j));
					if(interactiePanel.yAsLog) {
						getal = 10 + toSuperScript(getal);
					}					
				}

				if(getal.contains("."))
				{	getal = getal.substring(0, Math.min(getal.indexOf(".") + 3, getal.length()));
					while(getal.endsWith("0"))
					{	getal = getal.substring(0, getal.length() - 1);
					
					}
					if(getal.endsWith("."))
					{	getal = getal.substring(0, getal.length() - 1);
					
					}
				}
				TextMetrics tm = gIm.measureText(getal);
				int woordbreedte = (int) Math.round(tm.getWidth());
//				int xLabel = Math.max(maxWoordBreedteY-woordbreedte,bx-2-woordbreedte);
				int xLabel = Math.min(breedte-3-woordbreedte,Math.max(maxWoordBreedteY-woordbreedte+4,bx-2-woordbreedte));
				int yLabel = (int)(hoogte+5-(interactiePanel.beginy+j*interactiePanel.eenheidyD * scalingMultiplyY ));
				witruimteY = xLabel==maxWoordBreedteY-woordbreedte;
				int minimaalBegin = Math.max(witruimteY?maxWoordBreedteY:0, interactiePanel.xPositief?bx:0);
				boolean schaalTekenen = (j%2 == 0 || interactiePanel.yAsLog) && interactiePanel.schaalZichtbaar && interactiePanel.schaalY;
				if((!interactiePanel.yPositief || j>0) && hoogte-(by+j*interactiePanel.eenheidyD) <= maxHoogteLijn) 
				{
					if(schaalTekenen) 
					{	gIm.beginPath();
						if(xLabel - 1 > minimaalBegin)
						{	gIm.moveTo(minimaalBegin, (int)(hoogte-(by+j*interactiePanel.eenheidyD * scalingMultiplyY)));
							gIm.lineTo(xLabel - 1, (int)(hoogte-(by+j*interactiePanel.eenheidyD * scalingMultiplyY)));
						}
						gIm.moveTo(Math.max(minimaalBegin, xLabel + woordbreedte + 1), (int)(hoogte-(by+j*interactiePanel.eenheidyD * scalingMultiplyY)));
						gIm.lineTo(breedte, (int)(hoogte-(by+j*interactiePanel.eenheidyD * scalingMultiplyY)));
						gIm.stroke();
					}
					else if(j%2 == 0 || interactiePanel.yAsLog || !interactiePanel.roosterGrof)
					{	gIm.beginPath();
						gIm.moveTo(minimaalBegin,(int)(hoogte-(by+j*interactiePanel.eenheidyD * scalingMultiplyY)));
						gIm.lineTo(breedte,(int)(hoogte-(by+j*interactiePanel.eenheidyD * scalingMultiplyY)));
						gIm.stroke();
					}
							
				}
				if((!interactiePanel.yPositief || j>0) && schaalTekenen && j != 0)
				{
					gIm.fillText(getal, xLabel, yLabel);
					
				}
			}
		}
		
		double rangeX = pixelsXtoValue(breedte)-pixelsXtoValue(0);
		int piMultiplier = 1;
		while ( rangeX/(Math.PI*piMultiplier)>(double) cMaxPiLinesOnScreen) {
			piMultiplier++;
		}
		
		double piScalingDivider;
		if (interactiePanel.manualScalingX) {
			piScalingDivider = interactiePanel.eenheidxValue;
		} else {
			piScalingDivider = interactiePanel.schaalFactorX;
		}

		if (interactiePanel.piLijnenZichtbaar)
		{	
			int dashStep = 5;
			int dashes = hoogte / dashStep;
			
			gIm.setStrokeStyle(grijs);
			gIm.setFillStyle(zwart);
			// 0 is in beeld
			if ((bx > 0) && (bx < breedte)) {	
//				int maxLCnt = (int) Math.round(interactiePanel.beginx / Math.PI);
				int maxLCnt = (int) Math.round(interactiePanel.beginx / (piMultiplier * Math.PI * interactiePanel.eenheidxD / piScalingDivider));
				for (int lCnt = 1; lCnt <= maxLCnt; lCnt++)
				{	//int piX = (int) Math.round(beginx - lCnt * Math.PI * eenheidxD / schaalFactorX); 
//					int piX = (int) Math.round(interactiePanel.beginx - lCnt * Math.PI * interactiePanel.eenheidxD);
					int piX = (int) Math.round(interactiePanel.beginx - (lCnt * piMultiplier * Math.PI * interactiePanel.eenheidxD / piScalingDivider));

					if ((piX > bx || !interactiePanel.xPositief && piX > 0) && (piX < breedte))
					{	gIm.beginPath();
						for (int dCnt = 0; dCnt < dashes; dCnt++)
						{	if ((dCnt % 2) == 0)
								if(dCnt * dashStep + dashStep < hoogte - by || !interactiePanel.yPositief)
								{	gIm.moveTo(piX, dCnt * dashStep);
									gIm.lineTo(piX, dCnt * dashStep + dashStep);
								}
						}	
						gIm.stroke();
//						double aantalPi = lCnt * interactiePanel.schaalFactorX;
						double aantalPi = piMultiplier * lCnt;
						int aantalPiInt = (int) aantalPi;
						if(aantalPi == 0);
						else if(aantalPi == 1)
							gIm.fillText("-" + "\u03C0", piX-3, hoogte - by + 20);
						else
						{	if(aantalPiInt == aantalPi)
								gIm.fillText("-" + aantalPiInt + "\u03C0", piX - 3, hoogte - by + 20);
							else
								gIm.fillText("-" + Double.toString(aantalPi) + "\u03C0", piX - 3, hoogte - by + 20);
						}
						gIm.beginPath();
						gIm.moveTo(piX,hoogte-by-2);
						gIm.lineTo(piX,hoogte-by+2);
						gIm.stroke();
					}
				}
//				int maxRCnt = (int) Math.round((breedte - interactiePanel.beginx) / Math.PI);
				int maxRCnt = (int) Math.round((breedte - interactiePanel.beginx) / (piMultiplier * Math.PI * interactiePanel.eenheidxD / piScalingDivider));
				for (int rCnt = 1; rCnt <= maxRCnt; rCnt++) {
//					int piX = (int) Math.round(interactiePanel.beginx + rCnt * Math.PI * interactiePanel.eenheidxD);
					int piX = (int) Math.round(interactiePanel.beginx + rCnt * (piMultiplier * Math.PI * interactiePanel.eenheidxD / piScalingDivider) );

					if ((piX > bx || !interactiePanel.xPositief && piX > 0) && (piX < breedte))
					{	gIm.beginPath();
						for (int dCnt = 0; dCnt < dashes; dCnt++)
						{	if ((dCnt % 2) == 0)
								if(dCnt * dashStep + dashStep < hoogte - by || !interactiePanel.yPositief)
								{	gIm.moveTo(piX, dCnt * dashStep);
									gIm.lineTo(piX, dCnt * dashStep + dashStep);
								}
						}	
						gIm.stroke();
//						double aantalPi = rCnt * interactiePanel.schaalFactorX; 
						double aantalPi = piMultiplier *rCnt;
						int aantalPiInt = (int) aantalPi;
						if(aantalPi == 0);
						else if(aantalPi == 1)
							gIm.fillText("\u03C0", piX-3, hoogte - by + 20);
						else
						{	if(aantalPiInt == aantalPi)
								gIm.fillText(aantalPiInt + "\u03C0", piX - 3, hoogte - by + 20);
							else
								gIm.fillText(Double.toString(aantalPi) + "\u03C0", piX - 3, hoogte - by + 20);
						}
						gIm.beginPath();
						gIm.moveTo(piX,hoogte-by-2);
						gIm.lineTo(piX,hoogte-by+2);
						gIm.stroke();
					}
				}
				
			}	
			// 0 is links
			else if (bx <= 0) {	
//				int maxRCnt = (int) Math.round((breedte - interactiePanel.beginx) / Math.PI);
				int maxRCnt = (int) Math.round((breedte - interactiePanel.beginx) / (piMultiplier * Math.PI * interactiePanel.eenheidxD / piScalingDivider));

				for (int rCnt = 1; rCnt <= maxRCnt; rCnt++) {	
					//int piX = (int) Math.round(beginx + rCnt * Math.PI * eenheidxD / schaalFactorX); 
					int piX = (int) Math.round(interactiePanel.beginx + rCnt * (piMultiplier * Math.PI * interactiePanel.eenheidxD / piScalingDivider) );
					if ((piX > 0) && (piX < breedte))
					{	gIm.beginPath();
						for (int dCnt = 0; dCnt < dashes; dCnt++)
						{	if ((dCnt % 2) == 0)
								if(dCnt * dashStep + dashStep < hoogte - by || !interactiePanel.yPositief)
								{	gIm.moveTo(piX, dCnt * dashStep);
									gIm.lineTo(piX, dCnt * dashStep + dashStep);
								}
						}	
						gIm.stroke();
//						double aantalPi = rCnt * interactiePanel.schaalFactorX;
						double aantalPi = piMultiplier *rCnt;

						int aantalPiInt = (int) aantalPi;
						if(aantalPi == 0);
						else if(aantalPi == 1)
							gIm.fillText("\u03C0", piX-3, hoogte - by + 20);
						else
						{	if(aantalPiInt == aantalPi)
								gIm.fillText(aantalPiInt + "\u03C0", piX - 3, hoogte - by + 20);
							else
								gIm.fillText(Double.toString(aantalPi) + "\u03C0", piX - 3, hoogte - by + 20);
						}
						gIm.beginPath();
						gIm.moveTo(piX, hoogte - by - 2);
						gIm.lineTo(piX,hoogte-by+2);
						gIm.stroke();
					}
				}
			}		
			// 0 is rechts
			else if (bx >= breedte && !interactiePanel.xPositief) {	
//				int maxLCnt = (int) Math.round(interactiePanel.beginx / Math.PI);
				int maxLCnt = (int) Math.round(interactiePanel.beginx / (piMultiplier * Math.PI * interactiePanel.eenheidxD / piScalingDivider));
				for (int lCnt = 1; lCnt <= maxLCnt; lCnt++)
				{	//int piX = (int) Math.round(beginx - lCnt * Math.PI * eenheidxD / schaalFactorX); 
//					int piX = (int) Math.round(interactiePanel.beginx - lCnt * Math.PI * interactiePanel.eenheidxD);
					int piX = (int) Math.round(interactiePanel.beginx - lCnt * (piMultiplier * Math.PI * interactiePanel.eenheidxD / piScalingDivider));
					if ((piX > 0) && (piX < breedte))
					{	gIm.beginPath();
						for (int dCnt = 0; dCnt < dashes; dCnt++)
						{	if ((dCnt % 2) == 0)
								if(dCnt * dashStep + dashStep < hoogte - by || !interactiePanel.yPositief)
								{	gIm.moveTo(piX, dCnt * dashStep);
									gIm.lineTo(piX, dCnt * dashStep + dashStep);
								}
						}
						gIm.stroke();
//						double aantalPi = lCnt * interactiePanel.schaalFactorX;
						double aantalPi = piMultiplier * lCnt;

						int aantalPiInt = (int) aantalPi;
						if(aantalPi == 0);
						else if(aantalPi == 1)
							gIm.fillText("-" + "\u03C0", piX-3, hoogte - by + 20);
						else
						{	if(aantalPiInt == aantalPi)
								gIm.fillText("-" + aantalPiInt + "\u03C0", piX - 3, hoogte - by + 20);
							else
								gIm.fillText("-" + Double.toString(aantalPi) + "\u03C0", piX - 3, hoogte - by + 20);
						}
						gIm.beginPath();
						gIm.moveTo(piX,hoogte-by-2);
						gIm.lineTo(piX,hoogte-by+2);
						gIm.stroke();
					}
				}
			}		
		}
		
		if (interactiePanel.assenZichtbaar) 
		{
			gIm.setLineWidth(1.0d);
			gIm.setStrokeStyle(zwart);
			gIm.setFillStyle(zwart);
			
			/* Y-as */
//			if(bx>1 && bx<breedte) {	
//				gIm.beginPath(); 
//				gIm.moveTo(bx, 0);
//				gIm.lineTo(bx, hoogte-Math.max(witruimteX?maxWoordHoogteX:0,(interactiePanel.yPositief?by:0)));
//				gIm.stroke();
//			}
			
			if (bx<=1) {
				gIm.beginPath(); 
				gIm.moveTo(1, 0);
				gIm.lineTo(1, hoogte-Math.max(witruimteX?maxWoordHoogteX:0,(interactiePanel.yPositief?by:0)));
				gIm.stroke();
			} 
			else { // bx >1
				if (bx<breedte) { //(bx>1 && bx<breedte)
					gIm.beginPath(); 
					gIm.moveTo(bx, 0);
					gIm.lineTo(bx, hoogte-Math.max(witruimteX?maxWoordHoogteX:0,(interactiePanel.yPositief?by:0)));
					gIm.stroke();
				}
				else { //bx >=breedte
					gIm.beginPath(); 
					gIm.moveTo(breedte-1, 0);
					gIm.lineTo(breedte-1, hoogte-Math.max(witruimteX?maxWoordHoogteX:0,(interactiePanel.yPositief?by:0)));
					gIm.stroke();
				}
			}
			
			/* X-as */
//			if(by>0 && by<hoogte) {	
//				gIm.beginPath();
//				gIm.moveTo(Math.max(witruimteY?maxWoordBreedteY:0, interactiePanel.xPositief?bx:0), hoogte-by);
//				gIm.lineTo(breedte,hoogte-by);
//				gIm.stroke();
//			}
			
			if (by<=0) {// by<=0
				gIm.beginPath();
				gIm.moveTo(Math.max(witruimteY?maxWoordBreedteY:0, interactiePanel.xPositief?bx:0), hoogte-1);
				gIm.lineTo(breedte,hoogte-1);
				gIm.stroke();
			}
			else { // by>0
				if (by<hoogte) { // (by>0 && by<hoogte)
					gIm.beginPath();
					gIm.moveTo(Math.max(witruimteY?maxWoordBreedteY:0, interactiePanel.xPositief?bx:0), hoogte-by);
					gIm.lineTo(breedte,hoogte-by);
					gIm.stroke();
				} 
				else { // by>=hoogte
					gIm.beginPath();
					gIm.moveTo(Math.max(witruimteY?maxWoordBreedteY:0, interactiePanel.xPositief?bx:0), 1);
					gIm.lineTo(breedte, 1);
					gIm.stroke();
				}
			}

						
			fontString = "italic 10px sans-serif";
			gIm.setFont(fontString);
			gIm.fillText("O",bx-11,hoogte-by+10);
			
			String xAsNaam = interactiePanel.grafiekXAsNaam;
			String yAsNaam = interactiePanel.grafiekYAsNaam;
			TextMetrics tm = gIm.measureText(xAsNaam);
			int woordBreedte = (int) Math.round(tm.getWidth());
			tm = gIm.measureText(yAsNaam);
			int formuleWoordBreedte = (int) Math.round(tm.getWidth());
			
			gIm.fillText(xAsNaam,  breedte - woordBreedte - 5,  Math.min(hoogte - 17, hoogte - by - 5));
			gIm.fillText(yAsNaam, Math.max(18,  bx+6), 10);
			
			xAsNaamLinks = breedte - woordBreedte - 5;
			xAsNaamBoven = Math.min(hoogte - 17,  hoogte - by - 5);
			xAsNaamRechts = breedte - 5;
			xAsNaamOnder = Math.min(hoogte - 5, hoogte - by + 7);
			yAsNaamLinks = Math.max(18, bx + 6);
			yAsNaamRechts = Math.max(18 + formuleWoordBreedte, bx + 6 + formuleWoordBreedte);
			yAsNaamBoven = 10;
			yAsNaamOnder = 22;
			//gIm.setFont(new Font(font.getName(), Font.ITALIC, font.getSize()));
			//FontMetrics fm = g.getFontMetrics();
			//int woordbreedte = fm.stringWidth(xAsNaam);
			//int formuleWoordbreedte = fm.stringWidth(yAsNaam);
			//g.drawString(xAsNaam, breedte-woordbreedte-5,Math.min(hoogte-17, hoogte-(by)-5));
			
			//g.drawString(yAsNaam, Math.max(18,bx+6), 9);
			
			//xAsNaamActivator.setBounds(breedte-woordbreedte-5, Math.min(hoogte-17, hoogte-(by)-15), woordbreedte, 15);
			//yAsNaamActivator.setBounds(Math.max(18,bx+6), 0, formuleWoordbreedte, 15);
			
			//xAsNaamTF.setLocation(breedte-85,Math.min(hoogte-17, hoogte-(by)-15));
			//yAsNaamTF.setLocation(Math.max(18,bx+6),0);
			//*/
			
		}	
		
		if (interactiePanel.tekenDocentFuncties != null && (interactiePanel.typeOpdracht == GraphToolGWT.VINDFORMULEBIJGRAFIEK
				|| interactiePanel.typeOpdracht == GraphToolGWT.TEKENPUNTENBIJFORMULE && interactiePanel.score > 0 && (interactiePanel.mode == 0 || interactiePanel.mode == 1 || interactiePanel.nagekeken)))
		{	for(int j = 0; j < interactiePanel.tekenDocentFuncties.length; j++)
			{	if(interactiePanel.tekenDocentFuncties[j] != null)
				{	int xMin = Math.max(witruimteY?maxWoordBreedteY:0, interactiePanel.xPositief?bx:0);
					tekenFunctie(gIm, xMin, interactiePanel.tekenDocentFuncties[j], interactiePanel.docentDomeinen[j], 
							interactiePanel.docentColor);
				}
			}
		}
		
		if(!((interactiePanel.mode == 2 || interactiePanel.mode == 3) && !interactiePanel.nagekeken && 
				(interactiePanel.typeOpdracht == GraphToolGWT.VINDFORMULEBIJGRAFIEK || interactiePanel.typeOpdracht == GraphToolGWT.VINDFORMULEBIJPUNTEN)))
				//|| interactiePanel.mode == 0 || interactiePanel.mode == 1) 
			//gaat dit niet fout bij toetsen waarbij leerling vrij moet kunnen tekenen? Jawel en ook als docent al functies klaarzet en daarna ander type opdrachten zet. Daarom if-statement veranderd. 
			for(int j=0 ; j<interactiePanel.functies.length ; j++) {	
				
				if(interactiePanel.functies[j]!=null && interactiePanel.yAsNaam.equals(interactiePanel.grafiekYAsNaam)) {	
					
					int xMin = Math.max(witruimteY?maxWoordBreedteY:0, interactiePanel.xPositief?bx:0);
					tekenFunctie(gIm, xMin, interactiePanel.functies[j], interactiePanel.domeinen[j],
							(interactiePanel.typeOpdracht == GraphToolGWT.VINDFORMULEBIJGRAFIEK || interactiePanel.typeOpdracht == GraphToolGWT.VINDFORMULEBIJPUNTEN 
							|| interactiePanel.grafiekKleuren)?interactiePanel.colors[j]:interactiePanel.colors[0]);
				
					if(interactiePanel.traceOptie) {
						Expressie expressie;
						expressie = interactiePanel.functies[j];
						
						for (int i=0; i<interactiePanel.schuifParameters.length; i++) {
							expressie = expressie.substitueer(interactiePanel.schuifParameters[i].geefWaarde(), interactiePanel.schuifParameters[i].geefNaam());			
						}
						
						gIm.setStrokeStyle(grijs);
						gIm.setFillStyle(grijs);
						fontString = "10px sans-serif";
						gIm.setFont(fontString);
						
						//g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_NORMALIZE);
						double d = bx+1.0*((selectnummer+beginwaarde)*interactiePanel.eenheidx);
						int x = (int) Math.round(d);
//						double d0 = interactiePanel.functies[j].geefWaarde(interactiePanel.xAsLog?Math.pow(10, (selectnummer+beginwaarde)*interactiePanel.schaalFactorX):
//							(selectnummer+beginwaarde)*interactiePanel.schaalFactorX); 
						double d0;
						if (interactiePanel.manualScalingX) {
//							d0 = interactiePanel.functies[j].geefWaarde(interactiePanel.xAsLog?Math.pow(10, (selectnummer+beginwaarde)*interactiePanel.eenheidxValue):
//								 (selectnummer+beginwaarde)*interactiePanel.eenheidxValue); 
							d0 = expressie.geefWaarde(interactiePanel.xAsLog?Math.pow(10, (selectnummer+beginwaarde)*interactiePanel.eenheidxValue):
								 (selectnummer+beginwaarde)*interactiePanel.eenheidxValue); 
							
						} else {
//							d0 = interactiePanel.functies[j].geefWaarde(interactiePanel.xAsLog?Math.pow(10, (selectnummer+beginwaarde)*interactiePanel.schaalFactorX):
//								 (selectnummer+beginwaarde)*interactiePanel.schaalFactorX); 
							d0 = expressie.geefWaarde(interactiePanel.xAsLog?Math.pow(10, (selectnummer+beginwaarde)*interactiePanel.schaalFactorX):
								 (selectnummer+beginwaarde)*interactiePanel.schaalFactorX); 
						}
						
						if(!tracing && !Double.isNaN(d0) && selectnummer<8 && selectnummer>-1 &&
								x >= interactiePanel.domeinen[j][0] && x <= interactiePanel.domeinen[j][1]) {	
//							int y = (int)Math.round(interactiePanel.yAsLog?Math.log10(hoogte -(interactiePanel.beginy+interactiePanel.eenheidy*d0/interactiePanel.schaalFactorY)):
//								hoogte -(interactiePanel.beginy+interactiePanel.eenheidy*d0/interactiePanel.schaalFactorY)); 
							int y = (int) Math.round(valueYtoPixels(d0));
							
							gIm.beginPath();
							//gIm.arc(stand, hoogte - by, 3, 0, 2* Math.PI);
							gIm.arc(x, y, 2, 0, 2* Math.PI);
							gIm.closePath();
							gIm.fill();
							gIm.stroke();
							
							gIm.beginPath();
							gIm.moveTo(x, Math.min(hoogte - by, hoogte));
							gIm.lineTo(x, y);
							gIm.lineTo(Math.max(0,  bx), y);
							gIm.lineTo(x, y);
							gIm.closePath();
							gIm.stroke();
							
							/*
							g.fillOval(x-2,y-2,5,5);
							g.setStroke(new BasicStroke(1.0f));
							g.drawLine(x,y,x,Math.min(hoogte-by, hoogte));
							g.drawLine(x,y,Math.max(0,bx),y);
							g.setStroke(new BasicStroke(1.0f));
							*/
							tracexD = interactiePanel.xAsLog?Math.log10(d):d;
							tracex = x;
							stand = tracex;
							//interactiePanel.slider.zetStand(tracex);
							
							//double dTraceX = Math.round(100*(interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*tracexD/interactiePanel.eenheidxD))/100;
							//double dTraceY = Math.round(100*interactiePanel.functies[j].geefWaarde(dTraceX))/100;
//							double dTraceX = interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*tracexD/interactiePanel.eenheidxD; 
							double dTraceX;
							if (interactiePanel.manualScalingX) {
								dTraceX = interactiePanel.eenheidxValue*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.eenheidxValue*tracexD/interactiePanel.eenheidxD; 
							} else {
								dTraceX = interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*tracexD/interactiePanel.eenheidxD; 
							}
							// double dTraceY = interactiePanel.functies[j].geefWaarde(dTraceX);
							double dTraceY = expressie.geefWaarde(dTraceX);
							dTraceX = Math.round(100*dTraceX)/100.0;
							dTraceY = Math.round(100*dTraceY)/100.0;
//							int tracey = (int)Math.round(hoogte -(interactiePanel.beginy+interactiePanel.eenheidy*dTraceY/interactiePanel.schaalFactorY));
							int tracey;
							if (interactiePanel.manualScalingY) {
								tracey = (int)Math.round(hoogte -(interactiePanel.beginy+interactiePanel.eenheidy*dTraceY/interactiePanel.eenheidyValue));
							} else {
								tracey = (int)Math.round(hoogte -(interactiePanel.beginy+interactiePanel.eenheidy*dTraceY/interactiePanel.schaalFactorY));
							}

							//String xWaarde = interactiePanel.dfTrace.format(dTraceX);
							//String yWaarde = interactiePanel.dfTrace.format(dTraceY);
							String xWaarde = "" + dTraceX;
							String yWaarde = "" + dTraceY;
							//g.setFont(interactiePanel.font);
							//interactiePanel.fm = g.getFontMetrics();
							TextMetrics tm = gIm.measureText(xWaarde);
							int woordBreedteX = (int) Math.round(tm.getWidth());
							int woordHoogteX = 10;
							tm = gIm.measureText(yWaarde);
							int woordBreedteY = (int) Math.round(tm.getWidth());
							int woordHoogteY = 10;
							
							//int woordBreedteX = interactiePanel.fm.stringWidth(xWaarde);
							//int woordHoogteX = interactiePanel.fm.getAscent();
							//int woordBreedteY = interactiePanel.fm.stringWidth(yWaarde);
							//int woordHoogteY = interactiePanel.fm.getAscent();
							gIm.setFillStyle(geel);
							gIm.setStrokeStyle(zwart);
							//g.setColor(new Color(255,255,200));
							//g.fillRect(tracex-woordBreedteX/2-2, Math.min(hoogte-by, hoogte-woordHoogteX-2), woordBreedteX+4, woordHoogteX+2);
							//g.fillRect(Math.max(0,bx-woordBreedteY), tracey-woordHoogteY/2-2, woordBreedteY+4, woordHoogteY+4);
							//g.setColor(Color.black);
							gIm.beginPath();
							gIm.rect(tracex-woordBreedteX/2-2, Math.min(hoogte-by, hoogte-woordHoogteX-2), woordBreedteX+4, woordHoogteX+2);
							gIm.closePath();
							gIm.fill();
							gIm.stroke();
							gIm.beginPath();
							gIm.rect(Math.max(0,bx-woordHoogteY), tracey-woordHoogteY/2-2, woordBreedteY+4, woordHoogteY+4);
							gIm.closePath();
							gIm.fill();
							gIm.stroke();
							gIm.setFillStyle(zwart);
							gIm.fillText(xWaarde, tracex-woordBreedteX/2, Math.min(hoogte-by+woordHoogteX, hoogte-2));
							gIm.fillText(yWaarde, Math.max(2,bx-woordBreedteY+2), tracey+woordHoogteY/2);
						}
						else {	
//							double dTraceX = interactiePanel.xAsLog?Math.pow(10, interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD 
//								+ interactiePanel.schaalFactorX*tracexD/interactiePanel.eenheidxD):interactiePanel.schaalFactorX*(-interactiePanel.beginx)
//								/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*tracexD/interactiePanel.eenheidxD;
							double dTraceX = pixelsXtoValue(tracexD);

//							double dTraceY = interactiePanel.yAsLog?Math.log10((interactiePanel.functies[j].substitueer(dTraceX, interactiePanel.grafiekXAsNaam)).geefWaarde()):
//								(interactiePanel.functies[j].substitueer(dTraceX, interactiePanel.grafiekXAsNaam)).geefWaarde();
							double dTraceY = interactiePanel.yAsLog?Math.log10((expressie.substitueer(dTraceX, interactiePanel.grafiekXAsNaam)).geefWaarde()):
								(expressie.substitueer(dTraceX, interactiePanel.grafiekXAsNaam)).geefWaarde();

							if(!Double.isNaN(dTraceY) && tracex<interactiePanel.breedte && tracex>-1 && (!interactiePanel.xPositief || tracex>bx)
									&& dTraceX >= interactiePanel.domeinen[j][0] && dTraceX <= interactiePanel.domeinen[j][1])
							{	dTraceX = Math.round(100*dTraceX)/100.0;
								dTraceY = Math.round(100*dTraceY)/100.0;
							
//								int tracey = (int)Math.round(hoogte -(interactiePanel.beginy+interactiePanel.eenheidy*dTraceY/interactiePanel.schaalFactorY));
								int tracey;
								if (interactiePanel.manualScalingY) {
									tracey = (int)Math.round(hoogte -(interactiePanel.beginy+interactiePanel.eenheidy*dTraceY/interactiePanel.eenheidyValue));
								} else {
									tracey = (int)Math.round(hoogte -(interactiePanel.beginy+interactiePanel.eenheidy*dTraceY/interactiePanel.schaalFactorY));
								}
								gIm.beginPath();
								gIm.arc(tracex, tracey, 3, 0, 2* Math.PI);
								gIm.closePath();
								gIm.fill();
								gIm.stroke();
								
								gIm.beginPath();
								gIm.moveTo(tracex, Math.min(hoogte - by, hoogte));
								gIm.lineTo(tracex, tracey);
								gIm.lineTo(Math.max(bx, 0), tracey);
								gIm.lineTo(tracex, tracey);
								gIm.closePath();
								gIm.stroke();
								
								//String xWaarde = interactiePanel.dfTrace.format(dTraceX);
								//String yWaarde = interactiePanel.dfTrace.format(interactiePanel.yAsLog?Math.pow(10,dTraceY):dTraceY);
								String xWaarde = "" + dTraceX;
								String yWaarde = "" + (interactiePanel.yAsLog?Math.pow(10, dTraceY):dTraceY);
								
								//g.setFont(interactiePanel.font);
								//interactiePanel.fm = g.getFontMetrics();
								TextMetrics tm = gIm.measureText(xWaarde);
								int woordBreedteX = (int) Math.round(tm.getWidth());
								int woordHoogteX = 10;
								tm = gIm.measureText(yWaarde);
								int woordBreedteY = (int) Math.round(tm.getWidth());
								int woordHoogteY = 10;
								
								/*
								int woordBreedteX = interactiePanel.fm.stringWidth(xWaarde);
								int woordHoogteX = interactiePanel.fm.getAscent();
								int woordBreedteY = interactiePanel.fm.stringWidth(yWaarde);
								int woordHoogteY = interactiePanel.fm.getAscent();
								*/
								gIm.setFillStyle(geel);
								gIm.setStrokeStyle(zwart);
								//g.setColor(new Color(255,255,200));
								//g.fillRect(tracex-woordBreedteX/2-2, Math.min(hoogte-by, hoogte-woordHoogteX-2), woordBreedteX+4, woordHoogteX+2);
								//g.fillRect(Math.max(0,bx-woordBreedteY-5), tracey-woordHoogteY/2-2, woordBreedteY+4, woordHoogteY+4);
								//g.setColor(Color.black);
								gIm.beginPath();
								gIm.rect(tracex-woordBreedteX/2-2, Math.min(hoogte-by, hoogte-woordHoogteX-2), woordBreedteX+4, woordHoogteX+2);
								gIm.closePath();
								gIm.fill();
								gIm.stroke();
								gIm.beginPath();
								gIm.rect(Math.max(0,bx-woordBreedteY-5), tracey-woordHoogteY/2-2, woordBreedteY+4, woordHoogteY+4);
								gIm.closePath();
								gIm.fill();
								gIm.stroke();
								gIm.setFillStyle(zwart);
								gIm.fillText(xWaarde, tracex-woordBreedteX/2, Math.min(hoogte-by+woordHoogteX, hoogte-2));
								gIm.fillText(yWaarde, Math.max(2,bx-woordBreedteY-3), tracey+woordHoogteY/2);
							}
						}
						
						
						gIm.beginPath();
						gIm.moveTo(0, hoogte - by);
						gIm.lineTo(breedte, hoogte - by);
						gIm.stroke();
						
						
					}
					
				}
			}
		
				
		tekenOngelijkheden(gIm);
		tekenVerticaleLijnen(gIm);
		
		//Punten en grafieken uit tekenEditor;
		if(interactiePanel.docentGraphPoints != null && interactiePanel.typeOpdracht == GraphToolGWT.VINDFORMULEBIJPUNTEN)
			tekenGraphPoints(interactiePanel.getActiveIndex(), gIm, true, witruimteY, maxWoordBreedteY, bx, breedte, hoogte);
				
		if(interactiePanel.graphPoints != null)
		{	// eerst de punten en verbindingen van de niet actieve grafieken	
			for (int index = 1; index <= interactiePanel.getNumGraphs(); index++)
			{	if (index != interactiePanel.getActiveIndex())
				{	tekenGraphPoints(index, gIm, false, witruimteY, maxWoordBreedteY, bx, breedte, hoogte);
				}
			}
			// dan de punten en verbindingen van de actieve grafiek
			tekenGraphPoints(interactiePanel.getActiveIndex(), gIm, false, witruimteY, maxWoordBreedteY, bx, breedte, hoogte);
		}
		
		if(interactiePanel.traceOptie)
		{	gIm.setStrokeStyle(zwart);
			gIm.setFillStyle(rood);
		
			gIm.beginPath();
			gIm.moveTo(0, hoogte - by);
			gIm.lineTo(breedte, hoogte - by);
			gIm.stroke();
			
			gIm.beginPath();
			gIm.arc(stand, hoogte - by, 3, 0, 2* Math.PI);
			gIm.closePath();
			gIm.fill();
			gIm.stroke();
		}
		
		//schuifParameters tekenen.
		if(interactiePanel.schuifParameters != null) {	
			for(int i = 0; i < interactiePanel.schuifParameters.length; i++) {	
				interactiePanel.schuifParameters[i].paint(gIm);
			}
		}
		
		
	}

	public void tekenGraphPoints(int index, Context2d g, boolean docent, boolean witruimteY, int maxWoordBreedteY, int bx, int breedte, int hoogte)
	{	Vector indexPoints = interactiePanel.getPoints(index, docent);
		if(docent)
		{	g.setFillStyle(interactiePanel.docentColor);
			g.setStrokeStyle(interactiePanel.docentColor);
		}
		else if(interactiePanel.grafiekKleuren)
		{	g.setFillStyle(interactiePanel.colors[index - 1]);
			g.setStrokeStyle(interactiePanel.colors[index - 1]);
		}
		else
		{	g.setFillStyle(interactiePanel.colors[0]);
			g.setStrokeStyle(interactiePanel.colors[0]);
		}
		g.setLineWidth(1.0);
		for (int pCnt = 0; pCnt < indexPoints.size(); pCnt++)
		{	RealPoint rp = (RealPoint) indexPoints.elementAt(pCnt);
			Point pix = interactiePanel.realPointToPixels(rp);
			if(index == interactiePanel.getActiveIndex() && !docent && pix != null)// && tekenComponent.getCursorMode() != tekenComponent.NOCUR)
			{	g.beginPath();
				g.arc(pix.getX(), pix.getY(), interactiePanel.PRAD + 1, 0, 2* Math.PI);
				g.closePath();
				g.fill();
				//g.stroke();
			}
			else if (pix != null)
			{	g.beginPath();
				g.arc(pix.getX(), pix.getY(), interactiePanel.PRAD, 0, 2* Math.PI);
				g.closePath();
				g.fill();
				//g.stroke();
			}
		}
		// verbinden met lijnen
		//if ((tekenComponentAan && tekenComponent.getConnectMode() == tekenComponent.LINES) &&
		  //  (indexPoints.size() > 1))
		if (interactiePanel.tekenComponent != null && interactiePanel.tekenComponent.getConnectMode() == interactiePanel.tekenComponent.LINES 
				&& indexPoints.size() > 1)
		{	RealPoint rp0 = (RealPoint) indexPoints.elementAt(0);
			Point pix0 = interactiePanel.realPointToPixels(rp0);
			RealPoint rp1 = null;
			Point pix1 = null;
			for (int pCnt = 1; pCnt < indexPoints.size(); pCnt++)
			{	rp1 = (RealPoint) indexPoints.elementAt(pCnt);
				pix1 = interactiePanel.realPointToPixels(rp1);
				g.beginPath();
				g.moveTo(pix0.getX(), pix0.getY());
				g.lineTo(pix1.getX(), pix1.getY());
				g.stroke();
				rp0 = rp1;
				pix0 = pix1;
			}
		}
		
		//if ((tekenComponentAan && tekenComponent.getConnectMode() == tekenComponent.CURVE) &&
		//    (indexPoints.size() == 2))
		if (interactiePanel.tekenComponent != null && interactiePanel.tekenComponent.getConnectMode() == interactiePanel.tekenComponent.CURVE 
				&& indexPoints.size() == 2)
		{	RealPoint rp0 = (RealPoint) indexPoints.elementAt(0);
			RealPoint rp1 = (RealPoint) indexPoints.elementAt(1);
			Point pix0 = interactiePanel.realPointToPixels(rp0);
			Point pix1 = interactiePanel.realPointToPixels(rp1);
			g.beginPath();
			g.moveTo(pix0.getX(), pix0.getY());
			g.lineTo(pix1.getX(), pix1.getY());
			g.stroke();
			
		}    
		//if ((tekenComponentAan && tekenComponent.getConnectMode() == tekenComponent.CURVE) &&
		//	    (indexPoints.size() > 2))
			
		if (interactiePanel.tekenComponent != null && interactiePanel.tekenComponent.getConnectMode() == interactiePanel.tekenComponent.CURVE 
				&& indexPoints.size() > 2)
		{	//Graphics2D g2D = (Graphics2D) g;
			//g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			boolean puntenVerwijderd = false;
			if(!interactiePanel.xAsLog && !interactiePanel.yAsLog)
			{	indexPoints = sorteerNaarX(indexPoints);
				if(berekenLijn(indexPoints, interactiePanel.tekenGrafiekNauwkeurigheid))
				{	for(int i = indexPoints.size() - 2; i > 0; i--)
						indexPoints.removeElementAt(i);
					puntenVerwijderd = true;
				}
				else if(indexPoints.size() > 3 && berekenParabool(indexPoints, interactiePanel.tekenGrafiekNauwkeurigheid))
				{	int midden = indexPoints.size()/2;
					for(int i = indexPoints.size() - 2; i > 0; i--)
					{	if(i != midden)
						{	indexPoints.removeElementAt(i);
						}
					}
					puntenVerwijderd = true;
				}
			}
			
			if(puntenVerwijderd)
			{	g.beginPath();
				//GeneralPath curve = new GeneralPath();
				double[] weights = berekenGewichten(indexPoints);
				RealPoint beginPunt = (RealPoint) indexPoints.elementAt(0);
				Point beginPuntPix = interactiePanel.realPointToPixels(beginPunt);
				RealPoint eindPunt = (RealPoint) indexPoints.elementAt(indexPoints.size() - 1);
				Point eindPuntPix = interactiePanel.realPointToPixels(eindPunt);
				//for(int i=Math.max(witruimteY?maxWoordBreedteY:0, interactiePanel.xPositief?bx:0) ; i<breedte ; i++)
				for(int i= (int) Math.max(0, beginPuntPix.getX()); i < Math.min(breedte, eindPuntPix.getX()) ; i++)
				{	double ii = i;
//					double d0 = berekenLagrangeY(indexPoints, interactiePanel.xAsLog?Math.pow(10,interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii
//						/interactiePanel.eenheidxD):interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii/interactiePanel.eenheidxD, weights);
//					double d1 = berekenLagrangeY(indexPoints, interactiePanel.xAsLog?Math.pow(10,interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+1)
//							/interactiePanel.eenheidxD):interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+1)/interactiePanel.eenheidxD, weights);
					double d0 = berekenLagrangeY(indexPoints, pixelsXtoValue(ii), weights);
					double d1 = berekenLagrangeY(indexPoints, pixelsXtoValue(ii+1), weights);
					int x0 = i;
					int x1 = i+1;
//					double dy0 = hoogte -(interactiePanel.beginy+interactiePanel.eenheidyD*(interactiePanel.yAsLog?Math.log10(d0):d0)/interactiePanel.schaalFactorY);
//					double dy1 = hoogte -(interactiePanel.beginy+interactiePanel.eenheidyD*(interactiePanel.yAsLog?Math.log10(d1):d1)/interactiePanel.schaalFactorY);
					double dy0 = valueYtoPixels(d0);
					double dy1 = valueYtoPixels(d1);
					if(dy0>1000)dy0 = 1000;
					if(dy0<-1000)dy0 = -1000;
					if(dy1>1000)dy1 = 1000;
					if(dy1<-1000)dy1 = -1000;
					
					//if(curve.getCurrentPoint()==null){
					g.moveTo((float)x0, (float)dy0);
					//}
					if(!interactiePanel.yPositief || d1>0) 
					{	g.lineTo((float)x1, (float)dy1);						
					}
				}
				//g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);
				//g2D.setStroke(new BasicStroke(1.2f));
				g.stroke();
			}
			else
			{	
				// het punt voor het startpunt p0, if any
				RealPoint p00 = null;
				// startpunt p0
				RealPoint rp0 = (RealPoint) indexPoints.elementAt(0);
				RealPoint p0 = interactiePanel.realPointToRealPixels(rp0);
				// eindpunt p1
				RealPoint rp1 = null;
				RealPoint p1 = null;
				// het punt na het eindpunt p1, if any
				RealPoint rp11 = null;
				RealPoint p11 = null;
		
				double intervalFrac = 3;				
		
				for (int pCnt = 1; pCnt < indexPoints.size(); pCnt++)
				{	// vind eindpunt
					rp1 = (RealPoint) indexPoints.elementAt(pCnt);
					p1 = interactiePanel.realPointToRealPixels(rp1);
					// kijk of p0-p1 in pixels vertikaal is, teken lijn
					if (Math.abs(p0.getX() - p1.getX()) < RealPoint.NZERO)
					{	Point pix0 = interactiePanel.realPointToPixels(rp0);
						Point pix1 = interactiePanel.realPointToPixels(rp1);
						g.beginPath();
						g.moveTo(pix0.getX(), pix0.getY());
						g.lineTo(pix1.getX(), pix1.getY());
						g.stroke();
					}
					else
					{	// vind het punt na p1, if any
						if (pCnt < (indexPoints.size() - 1))
						{	rp11 = (RealPoint) indexPoints.elementAt(pCnt + 1);
							p11 = interactiePanel.realPointToRealPixels(rp11);
						}
						// vind nu de controle-punten tussen p0 en p1
						// controlepunt 0
						RealPoint c0 = null;
						// p0 is het eerste punt, p1 is het tweede punt
						if (p00 == null)
						{	// vector p0 -> p1
							RealPoint slp0p1 = new RealPoint(
								p1.getX() - p0.getX(), p1.getY() - p0.getY());
							RealPoint unitSlope0 =	slp0p1.standarize();
							double xLength = (p1.getX() - p0.getX()) / intervalFrac;
							double newLength = xLength / unitSlope0.getX();
							RealPoint dir0 = new RealPoint(unitSlope0.getX() * newLength,
								unitSlope0.getY() * newLength);
							c0 = new RealPoint(p0.getX() + dir0.getX(), p0.getY() + dir0.getY());		
						}
						else
						{	// vector p00 -> p0
							RealPoint slp00p0 = new RealPoint(p0.getX() - p00.getX(), p0.getY() - p00.getY());
							// vector p0 -> p1
							RealPoint slp0p1 = new RealPoint(p1.getX() - p0.getX(), p1.getY() - p0.getY());	
	//eerst middelen, dan standariseren of omgekeerd?																
							RealPoint meanSlope0 = new RealPoint((slp00p0.getX() + slp0p1.getX()) / 2,
								(slp00p0.getY() + slp0p1.getY()) / 2);	
							RealPoint unitSlope0 =	meanSlope0.standarize();	
							double xLength = (p1.getX() - p0.getX()) / intervalFrac;
							double newLength = xLength / unitSlope0.getX();
							RealPoint dir0 = new RealPoint(unitSlope0.getX() * newLength,
								unitSlope0.getY() * newLength);
							c0 = new RealPoint(p0.getX() + dir0.getX(), p0.getY() + dir0.getY());		
						}
	
						// controlepunt 1
						RealPoint c1 = null;
						// p1 is het laatste punt
						if (p11 == null)
						{	// vector p0 -> p1
							RealPoint slp0p1 = new RealPoint(p1.getX() - p0.getX(), p1.getY() - p0.getY());	
							RealPoint unitSlope1 =	slp0p1.standarize();	
							double xLength = (p1.getX() - p0.getX()) / intervalFrac;
							double newLength = xLength / unitSlope1.getX();
							RealPoint dir1 = new RealPoint(	- unitSlope1.getX() * newLength,
								- unitSlope1.getY() * newLength);
							c1 = new RealPoint(p1.getX() + dir1.getX(), p1.getY() + dir1.getY());		
						}
						else
						{	// vector p0 -> p1
							RealPoint slp0p1 = new RealPoint(p1.getX() - p0.getX(), p1.getY() - p0.getY());	
							// vector p1 -> p11	
							RealPoint slp1p11 = new RealPoint(p11.getX() - p1.getX(), p11.getY() - p1.getY());	
	//eerst middelen, dan standariseren of omgekeerd?																
							RealPoint meanSlope1 = new RealPoint((slp0p1.getX() + slp1p11.getX()) / 2,
								(slp0p1.getY() + slp1p11.getY()) / 2);
							RealPoint unitSlope1 =	meanSlope1.standarize();	
							double xLength = (p1.getX() - p0.getX()) / intervalFrac;
							double newLength = xLength / unitSlope1.getX();
							RealPoint dir1 = new RealPoint(- unitSlope1.getX() * newLength,
								- unitSlope1.getY() * newLength);
							c1 = new RealPoint(p1.getX() + dir1.getX(), p1.getY() + dir1.getY());		
						}
				
						//if(index == interactiePanel.getActiveIndex())
						//	g.setLineWidth(0.7f);
							//g2D.setStroke(new BasicStroke(0.7f));
							g.beginPath();
							g.moveTo(p0.getX(), p0.getY());
							g.bezierCurveTo(c0.getX(), c0.getY(), c1.getX(), c1.getY(), p1.getX(), p1.getY());
							g.stroke();
							/*
						CubicCurve2D bezier = new CubicCurve2D.Double();
						bezier.setCurve(p0.x, p0.y, c0.x, c0.y,
									    c1.x, c1.y, p1.x, p1.y);
						g2D.draw(bezier);	
						*/		    
					} // else niet vertikaal
					p00 = p0;
					p0 = p1;
				}
			}
		}
		
		if (interactiePanel.tekenComponent != null && interactiePanel.tekenComponent.getConnectMode() == interactiePanel.tekenComponent.CURVE_EXTRA 
				&& indexPoints.size() == 2)
		{	
			RealPoint rp0 = (RealPoint) indexPoints.elementAt(0);
			RealPoint rp1 = (RealPoint) indexPoints.elementAt(1);
			
			double helling = ((interactiePanel.yAsLog?Math.log10(rp1.getY()):rp1.getY()) - (interactiePanel.yAsLog?Math.log10(rp0.getY()):rp0.getY()))/
					((interactiePanel.xAsLog?Math.log10(rp1.getX()):rp1.getX()) - (interactiePanel.xAsLog?Math.log10(rp0.getX()):rp0.getX()));
//			double linkerGrens = interactiePanel.xPositief?0:(interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD);
			double linkerGrens;
			if (interactiePanel.manualScalingX) {
				linkerGrens = interactiePanel.xPositief?0:(interactiePanel.eenheidxValue*(-interactiePanel.beginx)/interactiePanel.eenheidxD);
			}	
			else {
				linkerGrens = interactiePanel.xPositief?0:(interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD);
			}

			double xLinks = interactiePanel.xAsLog?Math.pow(10, linkerGrens):linkerGrens;
			double yLinks = interactiePanel.yAsLog?Math.pow(10, helling * (linkerGrens - (interactiePanel.xAsLog?Math.log10(rp0.getX()):rp0.getX())) + (interactiePanel.yAsLog?Math.log10(rp0.getY()):rp0.getY())):
				(helling * (linkerGrens - (interactiePanel.xAsLog?Math.log10(rp0.getX()):rp0.getX())) + (interactiePanel.yAsLog?Math.log10(rp0.getY()):rp0.getY()));
			
			double ii2 = breedte;
//			double rechterGrens = interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii2/interactiePanel.eenheidxD;
			double rechterGrens;
			if (interactiePanel.manualScalingX) {
				rechterGrens = interactiePanel.eenheidxValue*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.eenheidxValue*ii2/interactiePanel.eenheidxD;
			} else {
				rechterGrens = interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii2/interactiePanel.eenheidxD;
			}
			double xRechts = interactiePanel.xAsLog?Math.pow(10,rechterGrens):rechterGrens;
			double yRechts = interactiePanel.yAsLog?Math.pow(10, helling * (rechterGrens - (interactiePanel.xAsLog?Math.log10(rp0.getX()):rp0.getX())) + (interactiePanel.yAsLog?Math.log10(rp0.getY()):rp0.getY())):
				(helling * (rechterGrens - (interactiePanel.xAsLog?Math.log10(rp0.getX()):rp0.getX())) + (interactiePanel.yAsLog?Math.log10(rp0.getY()):rp0.getY())); 
			
			RealPoint linkerPunt = new RealPoint(xLinks, yLinks);
			RealPoint rechterPunt = new RealPoint(xRechts, yRechts);
			
			Point pix0 = interactiePanel.realPointToPixels(linkerPunt);
			Point pix1 = interactiePanel.realPointToPixels(rechterPunt);
			g.beginPath();
			g.moveTo(pix0.getX(), pix0.getY());
			g.lineTo(pix1.getX(), pix1.getY());
			g.stroke();
			
			/*
			Graphics2D g2D = (Graphics2D) g;
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			
			
			
			GeneralPath curve = new GeneralPath();
			double[] weights = berekenGewichten(indexPoints);
			for(int i=Math.max(witruimteY?maxWoordBreedteY:0, interactiePanel.xPositief?bx:0) ; i<breedte ; i++)
			{	double ii = i;
				double d0 = berekenLagrangeY(indexPoints, interactiePanel.xAsLog?Math.pow(10,interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii
					/interactiePanel.eenheidxD):interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii/interactiePanel.eenheidxD, weights);
				double d1 = berekenLagrangeY(indexPoints, interactiePanel.xAsLog?Math.pow(10,interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+1)
						/interactiePanel.eenheidxD):interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+1)/interactiePanel.eenheidxD, weights);
				int x0 = i;
				int x1 = i+1;
				double dy0 = hoogte -(interactiePanel.beginy+interactiePanel.eenheidyD*(interactiePanel.yAsLog?Math.log10(d0):d0)/interactiePanel.schaalFactorY);
				double dy1 = hoogte -(interactiePanel.beginy+interactiePanel.eenheidyD*(interactiePanel.yAsLog?Math.log10(d1):d1)/interactiePanel.schaalFactorY);
				if(dy0>1000)dy0 = 1000;
				if(dy0<-1000)dy0 = -1000;
				if(dy1>1000)dy1 = 1000;
				if(dy1<-1000)dy1 = -1000;
				
				if(curve.getCurrentPoint()==null)
				{	curve.moveTo((float)x0, (float)dy0);
				}
				if(!interactiePanel.yPositief || d1>0) 
				{	curve.lineTo((float)x1, (float)dy1);						
				}
			}
			g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);
			g2D.setStroke(new BasicStroke(1.2f));
			g2D.draw(curve);
			*/
		}
		if (interactiePanel.tekenComponent != null && interactiePanel.tekenComponent.getConnectMode() == interactiePanel.tekenComponent.CURVE_EXTRA 
				&& indexPoints.size() > 2)
		{	//Graphics2D g2D = (Graphics2D) g;
			//g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			boolean puntenVerwijderd = false;
			if(!interactiePanel.xAsLog && !interactiePanel.yAsLog)
			{	indexPoints = sorteerNaarX(indexPoints);
				if(berekenLijn(indexPoints, interactiePanel.tekenGrafiekNauwkeurigheid))
				{	for(int i = indexPoints.size() - 2; i > 0; i--)
						indexPoints.removeElementAt(i);
					puntenVerwijderd = true;
				}
				else if(indexPoints.size() > 3 && berekenParabool(indexPoints, interactiePanel.tekenGrafiekNauwkeurigheid))
				{	int midden = indexPoints.size()/2;
					for(int i = indexPoints.size() - 2; i > 0; i--)
						if(i != midden)
						{	indexPoints.removeElementAt(i);
						}
					puntenVerwijderd = true;
				}
			}
			
			if(puntenVerwijderd)
			{	g.beginPath();
				double[] weights = berekenGewichten(indexPoints);
				for(int i=Math.max(witruimteY?maxWoordBreedteY:0, interactiePanel.xPositief?bx:0) ; i<breedte ; i++)
				{	double ii = i;
//					double d0 = berekenLagrangeY(indexPoints, interactiePanel.xAsLog?Math.pow(10,interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii
//						/interactiePanel.eenheidxD):interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii/interactiePanel.eenheidxD, weights);
//					double d1 = berekenLagrangeY(indexPoints, interactiePanel.xAsLog?Math.pow(10,interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+1)
//							/interactiePanel.eenheidxD):interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+1)/interactiePanel.eenheidxD, weights);
					double d0 = berekenLagrangeY(indexPoints, pixelsXtoValue(ii), weights);
					double d1 = berekenLagrangeY(indexPoints, pixelsXtoValue(ii+1), weights);

					int x0 = i;
					int x1 = i+1;
//					double dy0 = hoogte -(interactiePanel.beginy+interactiePanel.eenheidyD*(interactiePanel.yAsLog?Math.log10(d0):d0)/interactiePanel.schaalFactorY);
//					double dy1 = hoogte -(interactiePanel.beginy+interactiePanel.eenheidyD*(interactiePanel.yAsLog?Math.log10(d1):d1)/interactiePanel.schaalFactorY);
					double dy0 = valueYtoPixels(d0);
					double dy1 = valueYtoPixels(d1);
					if(dy0>1000)dy0 = 1000;
					if(dy0<-1000)dy0 = -1000;
					if(dy1>1000)dy1 = 1000;
					if(dy1<-1000)dy1 = -1000;
					
					//if(curve.getCurrentPoint()==null)
					//{	
					g.moveTo((float)x0, (float)dy0);
					//}
					if(!interactiePanel.yPositief || d1>0) 
					{	g.lineTo((float)x1, (float)dy1);						
					}
				}
				//g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);
				//g2D.setStroke(new BasicStroke(1.2f));
				//g2D.draw(curve);
				g.stroke();
			}
			else
			{	
				// het punt voor het startpunt p0, if any
				RealPoint p00 = null; 
				
				RealPoint hp0 = (RealPoint) indexPoints.elementAt(0);
				RealPoint hp1 = (RealPoint) indexPoints.elementAt(1);
				RealPoint hp2 = (RealPoint) indexPoints.elementAt(2);
				
				//double helling 01 = (hp1.y - hp0.y)/(hp1.x - hp0.x);
				//double helling12 = (hp2.y - hp1.y)/(hp2.x - hp1.x);
				double helling01 = ((interactiePanel.yAsLog?Math.log10(hp1.getY()):hp1.getY()) - (interactiePanel.yAsLog?Math.log10(hp0.getY()):hp0.getY()))/
						((interactiePanel.xAsLog?Math.log10(hp1.getX()):hp1.getX()) - (interactiePanel.xAsLog?Math.log10(hp0.getX()):hp0.getX()));
				double helling12 = ((interactiePanel.yAsLog?Math.log10(hp2.getY()):hp2.getY()) - (interactiePanel.yAsLog?Math.log10(hp1.getY()):hp1.getY()))/
						((interactiePanel.xAsLog?Math.log10(hp2.getX()):hp2.getX()) - (interactiePanel.xAsLog?Math.log10(hp1.getX()):hp1.getX()));
				
				//hier aanpassen voor andere "extrapolatieregel", en iets verderop.
				double helling0 = - helling12/3 + 4 * helling01/3;
				
				
				//double ii = Math.max(witruimteY?maxWoordBreedteY:0, interactiePanel.xPositief?bx:0);
				//double x0 = interactiePanel.xAsLog?Math.pow(10,interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii
				//		/interactiePanel.eenheidxD):interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii/interactiePanel.eenheidxD;
				double linkerGrens;
				if (interactiePanel.manualScalingX) {
					linkerGrens = interactiePanel.xPositief?0:(interactiePanel.eenheidxValue*(-interactiePanel.beginx)/interactiePanel.eenheidxD);
				} else {
					linkerGrens = interactiePanel.xPositief?0:(interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD);
				}
				double x0 = interactiePanel.xAsLog?Math.pow(10, linkerGrens):linkerGrens;
				double y0 = interactiePanel.yAsLog?Math.pow(10, helling0 * (linkerGrens - (interactiePanel.xAsLog?Math.log10(hp0.getX()):hp0.getX())) + (interactiePanel.yAsLog?Math.log10(hp0.getY()):hp0.getY())):
					(helling0 * (linkerGrens - (interactiePanel.xAsLog?Math.log10(hp0.getX()):hp0.getX())) + (interactiePanel.yAsLog?Math.log10(hp0.getY()):hp0.getY()));
				
				if(y0 < 0 && interactiePanel.yPositief)
				{
					y0 = 0;
					x0 = interactiePanel.xAsLog?Math.pow(10, (helling0 * (interactiePanel.xAsLog?Math.log10(hp0.getX()):hp0.getX()) - (interactiePanel.yAsLog?Math.log10(hp0.getY()):hp0.getY()))/helling0): 
						(helling0 * (interactiePanel.xAsLog?Math.log10(hp0.getX()):hp0.getX()) - (interactiePanel.yAsLog?Math.log10(hp0.getY()):hp0.getY()))/helling0;
				}
				// startpunt p0
				//RealPoint rp0 = new RealPoint(this.graphToolInteractiePanel.beginx, helling0*(this.graphToolInteractiePanel.beginx - hp0.x) + hp0.y);
				RealPoint rp0 = new RealPoint(x0, y0);
				//RealPoint rp0 = (RealPoint) indexPoints.elementAt(0);
				RealPoint p0 = interactiePanel.realPointToRealPixels(rp0);
				
				RealPoint hpLaatst0 = (RealPoint) indexPoints.elementAt(indexPoints.size() - 1);
				RealPoint hpLaatst1 = (RealPoint) indexPoints.elementAt(indexPoints.size() - 2);
				RealPoint hpLaatst2 = (RealPoint) indexPoints.elementAt(indexPoints.size() - 3);
				double helling10 = ((interactiePanel.yAsLog?Math.log10(hpLaatst0.getY()):hpLaatst0.getY()) - (interactiePanel.yAsLog?Math.log10(hpLaatst1.getY()):hpLaatst1.getY()))/
						((interactiePanel.xAsLog?Math.log10(hpLaatst0.getX()):hpLaatst0.getX()) - (interactiePanel.xAsLog?Math.log10(hpLaatst1.getX()):hpLaatst1.getX())); 
				double helling21 = ((interactiePanel.yAsLog?Math.log10(hpLaatst1.getY()):hpLaatst1.getY()) - (interactiePanel.yAsLog?Math.log10(hpLaatst2.getY()):hpLaatst2.getY()))/
						((interactiePanel.xAsLog?Math.log10(hpLaatst1.getX()):hpLaatst1.getX()) - (interactiePanel.xAsLog?Math.log10(hpLaatst2.getX()):hpLaatst2.getX()));
				
				//hier aanpassen voor andere "extrapolatieregel", en een stukje terug.
				double hellingLaatst = - helling21/3 + 4 * helling10/3;
				
				double ii2 = breedte;
//				double rechterGrens = interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii2/interactiePanel.eenheidxD;
				double rechterGrens;
				if (interactiePanel.manualScalingX) {
					rechterGrens = interactiePanel.eenheidxValue*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.eenheidxValue*ii2/interactiePanel.eenheidxD;
				} else {
					rechterGrens = interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii2/interactiePanel.eenheidxD;
				}
				double xLaatst = interactiePanel.xAsLog?Math.pow(10,rechterGrens):rechterGrens;
				double yLaatst = interactiePanel.yAsLog?Math.pow(10, hellingLaatst * (rechterGrens - (interactiePanel.xAsLog?Math.log10(hpLaatst0.getX()):hpLaatst0.getX())) + (interactiePanel.yAsLog?Math.log10(hpLaatst0.getY()):hpLaatst0.getY())):
					(hellingLaatst * (rechterGrens - (interactiePanel.xAsLog?Math.log10(hpLaatst0.getX()):hpLaatst0.getX())) + (interactiePanel.yAsLog?Math.log10(hpLaatst0.getY()):hpLaatst0.getY())); 
				RealPoint eindPunt = new RealPoint(xLaatst, yLaatst);
				//RealPoint eindPunt = interactiePanel.realPointToRealPixels(realEindPunt);
				// eindpunt p1
				RealPoint rp1 = null;
				RealPoint p1 = null;
				// het punt na het eindpunt p1, if any
				RealPoint rp11 = null;
				RealPoint p11 = null;
		
				double intervalFrac = 3;				
		
				for (int pCnt = 0; pCnt < indexPoints.size() + 1; pCnt++) //deze teller al bij 0 laten beginnen. En bij indexPoints.size() + 1 laten eindigen.
				{	// vind eindpunt
					if(pCnt < indexPoints.size())
						rp1 = (RealPoint) indexPoints.elementAt(pCnt);
					else
						rp1 = eindPunt;
					p1 = interactiePanel.realPointToRealPixels(rp1);
					// kijk of p0-p1 in pixels vertikaal is, teken lijn
					if (Math.abs(p0.getX() - p1.getX()) < RealPoint.NZERO)
					{	Point pix0 = interactiePanel.realPointToPixels(rp0);
						Point pix1 = interactiePanel.realPointToPixels(rp1);
						g.beginPath();
						g.moveTo(pix0.getX(), pix0.getY());
						g.lineTo(pix1.getX(), pix1.getY());
						g.stroke();
					}
					else
					{	// vind het punt na p1, if any
						if (pCnt < (indexPoints.size() - 1)) //TO DO: aanvullen met punt voor pCnt = indexPoints.size() - 1
						{	rp11 = (RealPoint) indexPoints.elementAt(pCnt + 1);
							p11 = interactiePanel.realPointToRealPixels(rp11);
						}
						else if (pCnt == indexPoints.size() - 1)
						{	rp11 = eindPunt;
							p11 = interactiePanel.realPointToRealPixels(rp11);
						}
						// vind nu de controle-punten tussen p0 en p1
						// controlepunt 0
						RealPoint c0 = null;
						// p0 is het eerste punt, p1 is het tweede punt
						if (p00 == null)
						{	// vector p0 -> p1
							RealPoint slp0p1 = new RealPoint(
								p1.getX() - p0.getX(), p1.getY() - p0.getY());
							RealPoint unitSlope0 =	slp0p1.standarize();
							double xLength = (p1.getX() - p0.getX()) / intervalFrac;
							double newLength = xLength / unitSlope0.getX();
							RealPoint dir0 = new RealPoint(unitSlope0.getX() * newLength,
								unitSlope0.getY() * newLength);
							c0 = new RealPoint(p0.getX() + dir0.getX(), p0.getY() + dir0.getY());		
						}
						else
						{	// vector p00 -> p0
							RealPoint slp00p0 = new RealPoint(p0.getX() - p00.getX(), p0.getY() - p00.getY());
							// vector p0 -> p1
							RealPoint slp0p1 = new RealPoint(p1.getX() - p0.getX(), p1.getY() - p0.getY());	
	//eerst middelen, dan standariseren of omgekeerd?																
							RealPoint meanSlope0 = new RealPoint((slp00p0.getX() + slp0p1.getX()) / 2,
								(slp00p0.getY() + slp0p1.getY()) / 2);	
							RealPoint unitSlope0 =	meanSlope0.standarize();	
							double xLength = (p1.getX() - p0.getX()) / intervalFrac;
							double newLength = xLength / unitSlope0.getX();
							RealPoint dir0 = new RealPoint(unitSlope0.getX() * newLength,
								unitSlope0.getY() * newLength);
							c0 = new RealPoint(p0.getX() + dir0.getX(), p0.getY() + dir0.getY());		
						}
	
						// controlepunt 1
						RealPoint c1 = null;
						// p1 is het laatste punt
						if (p11 == null)
						{	// vector p0 -> p1
							RealPoint slp0p1 = new RealPoint(p1.getX() - p0.getX(), p1.getY() - p0.getY());	
							RealPoint unitSlope1 =	slp0p1.standarize();	
							double xLength = (p1.getX() - p0.getX()) / intervalFrac;
							double newLength = xLength / unitSlope1.getX();
							RealPoint dir1 = new RealPoint(	- unitSlope1.getX() * newLength,
								- unitSlope1.getY() * newLength);
							c1 = new RealPoint(p1.getX() + dir1.getX(), p1.getY() + dir1.getY());		
						}
						else
						{	// vector p0 -> p1
							RealPoint slp0p1 = new RealPoint(p1.getX() - p0.getX(), p1.getY() - p0.getY());	
							// vector p1 -> p11	
							RealPoint slp1p11 = new RealPoint(p11.getX() - p1.getX(), p11.getY() - p1.getY());	
	//eerst middelen, dan standariseren of omgekeerd?																
							RealPoint meanSlope1 = new RealPoint((slp0p1.getX() + slp1p11.getX()) / 2,
								(slp0p1.getY() + slp1p11.getY()) / 2);
							RealPoint unitSlope1 =	meanSlope1.standarize();	
							double xLength = (p1.getX() - p0.getX()) / intervalFrac;
							double newLength = xLength / unitSlope1.getX();
							RealPoint dir1 = new RealPoint(- unitSlope1.getX() * newLength,
								- unitSlope1.getY() * newLength);
							c1 = new RealPoint(p1.getX() + dir1.getX(), p1.getY() + dir1.getY());		
						}
				
						if(index == interactiePanel.getActiveIndex())
							g.setLineWidth(0.7f);
							//g2D.setStroke(new BasicStroke(0.7f));
						g.beginPath();
						g.moveTo(p0.getX(), p0.getY());
						g.bezierCurveTo(c0.getX(), c0.getY(), c1.getX(), c1.getY(), p1.getX(), p1.getY());
						g.stroke();	
						
						/*
						CubicCurve2D bezier = new CubicCurve2D.Double();
						bezier.setCurve(p0.x, p0.y, c0.x, c0.y,
									    c1.x, c1.y, p1.x, p1.y);
						g2D.draw(bezier);
						*/			    
					} // else niet vertikaal
					p00 = p0;
					p0 = p1;
				} 
			}
		}
	}
	
	public double[] berekenGewichten(Vector points)
	{	double[] weights = new double[points.size()];
		for(int i = 0; i < points.size(); i++)
		{	weights[i] = 1;
			RealPoint rpi = (RealPoint) points.elementAt(i);
			for(int j = 0; j < points.size(); j++)
			{	if(i != j)
				{	RealPoint rpj = (RealPoint) points.elementAt(j);
					weights[i] *= 1/(rpi.getX() - rpj.getX());
				}
			}
		}		
		return weights;
	}
	
	public double berekenLagrangeY(Vector points, double x, double[] weights)
	{	double teller = 0;
		double noemer = 0;
		double waarde;
		double lagrangeY;
		int ingevuldPunt = -1;
		for(int i = 0; i < points.size(); i++)
		{	RealPoint rpi = (RealPoint) points.elementAt(i);
			waarde = weights[i]/(x - rpi.getX());
			if(Double.isNaN(waarde) || Double.isInfinite(waarde))
			{	ingevuldPunt = i;
				break;
			}
			teller += waarde*rpi.getY();
			noemer += waarde;
		}
		if(noemer == 0)
			noemer = 1;
		if(ingevuldPunt > -1)
		{	RealPoint rpi = (RealPoint) points.elementAt(ingevuldPunt);
			lagrangeY = rpi.getY();
		}
		else
			lagrangeY = teller/noemer;
		return lagrangeY;
	
	}
	
	public Vector sorteerNaarX(Vector points)
	{	Vector gesorteerd = new Vector();
		while(!points.isEmpty())
		{	RealPoint rp0 = (RealPoint) points.elementAt(0);
			double xMin = rp0.getX();
			double xMax = rp0.getX();
			int minIndex = 0;
			int maxIndex = 0;
			for(int i = 1; i < points.size(); i++)
			{	RealPoint rpi = (RealPoint) points.elementAt(i);
				if(rpi.getX() > xMax)
				{	xMax = rpi.getX();
					maxIndex = i;
				}
				if(rpi.getX() < xMin)
				{	xMin = rpi.getX();
					minIndex = i;
				}
			}
			int insertPlek = gesorteerd.size()/2;
			RealPoint rpMin = (RealPoint) points.elementAt(minIndex);
			if(minIndex != maxIndex)
			{	RealPoint rpMax = (RealPoint) points.elementAt(maxIndex);
				points.removeElementAt(maxIndex);
				gesorteerd.insertElementAt(rpMax, insertPlek);
			}
			if(maxIndex < minIndex)
				points.removeElementAt(minIndex - 1);
			else
				points.removeElementAt(minIndex);
			gesorteerd.insertElementAt(rpMin, insertPlek);
		}
		return gesorteerd;
	}
	
	public boolean berekenLijn(Vector points, int nauwkeurigheid)
	{	double nauwkeurigDoubleX = interactiePanel.schaalFactorX * nauwkeurigheid / interactiePanel.eenheidxD;
		double nauwkeurigDoubleY = interactiePanel.schaalFactorY * nauwkeurigheid / interactiePanel.eenheidyD;
		
		RealPoint rpMin = (RealPoint) points.elementAt(0);
		RealPoint rpMax = (RealPoint) points.elementAt(points.size() - 1);
		double a = ((interactiePanel.yAsLog?Math.log10(rpMax.getY()):rpMax.getY()) - (interactiePanel.yAsLog?Math.log10(rpMin.getY()):rpMin.getY()))
				/((interactiePanel.xAsLog?Math.log10(rpMax.getX()):rpMax.getX()) - (interactiePanel.xAsLog?Math.log10(rpMin.getX()):rpMin.getX()));
		double b = (interactiePanel.yAsLog?Math.log10(rpMax.getY()):rpMax.getY() - a*(interactiePanel.xAsLog?Math.log10(rpMax.getX()):rpMax.getX()));
		boolean lijn = true;
		
		for(int i = 1; i < points.size() - 1 && lijn; i++)
			{	RealPoint rpi = (RealPoint) points.elementAt(i);
				double xs = (a*(interactiePanel.yAsLog?Math.log10(rpi.getY()):rpi.getY()) + (interactiePanel.xAsLog?Math.log10(rpi.getX()):rpi.getX()) - a*b)/(a*a + 1);
				double ys = (a*a*(interactiePanel.yAsLog?Math.log10(rpi.getY()):rpi.getY()) + a*(interactiePanel.xAsLog?Math.log10(rpi.getX()):rpi.getX()) + b)/(a*a + 1);
//				double afstand = ((interactiePanel.xAsLog?Math.log10(rpi.getX()):rpi.getX()) - xs)*((interactiePanel.xAsLog?Math.log10(rpi.getX()):rpi.getX()) - xs) + 
//						((interactiePanel.yAsLog?Math.log10(rpi.getY()):rpi.getY()) - ys)*((interactiePanel.yAsLog?Math.log10(rpi.getY()):rpi.getY()) - ys);
//				afstand = Math.sqrt(afstand);
//				if(afstand > nauwkeurigDouble)
				boolean afstandxKleinGenoeg = Math.abs((interactiePanel.xAsLog?Math.log10(rpi.getX()):rpi.getX()) - xs) <= nauwkeurigDoubleX;
				boolean afstandyKleinGenoeg = Math.abs((interactiePanel.yAsLog?Math.log10(rpi.getY()):rpi.getY()) - ys) <= nauwkeurigDoubleY; 
				if(!(afstandxKleinGenoeg && afstandyKleinGenoeg))
				{	lijn = false;
				
				}
			}
		return lijn;
	}
	
	public boolean berekenParabool(Vector points, int nauwkeurigheid)
	{	double nauwkeurigDoubleX = interactiePanel.schaalFactorX * nauwkeurigheid / interactiePanel.eenheidxD;
		double nauwkeurigDoubleY = interactiePanel.schaalFactorY * nauwkeurigheid / interactiePanel.eenheidyD;
		
		RealPoint rpMin = (RealPoint) points.elementAt(0);
		RealPoint rpMax = (RealPoint) points.elementAt(points.size() - 1);
		RealPoint rpMiddle = (RealPoint) points.elementAt(points.size()/2);
		double c1 = rpMin.getY()/((rpMin.getX() - rpMax.getX()) * (rpMin.getX() - rpMiddle.getX()));
		double c2 = rpMax.getY()/((rpMax.getX() - rpMiddle.getX()) * (rpMax.getX() - rpMin.getX()));
		double c3 = rpMiddle.getY()/((rpMiddle.getX() - rpMin.getX()) * (rpMiddle.getX() - rpMax.getX()));
		double a = c1 + c2 + c3;
		double b = -c1*rpMax.getX() - c1*rpMiddle.getX() - c2*rpMin.getX() - c2*rpMiddle.getX() - c3*rpMin.getX() - c3*rpMax.getX();
		double c = c1*rpMax.getX()*rpMiddle.getX() + c2*rpMiddle.getX()*rpMin.getX() + c3*rpMin.getX() * rpMax.getX();
		boolean parabool = true;
		
		for(int i = 1; i < points.size() - 1 && parabool; i++)
		{	if(i != points.size()/2)
			{	RealPoint rpi = (RealPoint) points.elementAt(i);
				double deel1 = 2-b*b+4*a*(c-rpi.getY());
				double wortel = Math.pow(a, 6)*(27*(b+2*a*rpi.getX())*(b+2*a*rpi.getX())+Math.pow(deel1, 3));
				wortel = Math.sqrt(3) * Math.sqrt(wortel);
				if(Double.isNaN(wortel))
				{	parabool = false;
					break;
				}
				double deel2 = 9*a*a*a*b + 18*a*a*a*a*rpi.getX() + wortel;
				double nieuweDeel2 = Math.pow(deel2, 1.0/3.0);
				if(Double.isNaN(nieuweDeel2))
					nieuweDeel2 = - Math.pow(- deel2, 1.0/3.0);
				deel2 = nieuweDeel2;
				double xs = (-3*a*b+Math.pow(3, 1.0/3.0)*deel2 - (Math.pow(3, 2.0/3.0)*a*a*deel1)/deel2)/(6*a*a);
				double ys = a*xs*xs + b*xs + c;
//				double afstand = (rpi.getX() - xs)*(rpi.getX() - xs) + (rpi.getY() - ys)*(rpi.getY() - ys);
//				afstand = Math.sqrt(afstand);
//				if(afstand > nauwkeurigDouble)
				boolean afstandxKleinGenoeg = Math.abs((interactiePanel.xAsLog?Math.log10(rpi.getX()):rpi.getX()) - xs) <= nauwkeurigDoubleX;
				boolean afstandyKleinGenoeg = Math.abs((interactiePanel.yAsLog?Math.log10(rpi.getY()):rpi.getY()) - ys) <= nauwkeurigDoubleY; 
				if(!(afstandxKleinGenoeg && afstandyKleinGenoeg))
				{	parabool = false;
					break;
				}
			}
		}
		return parabool;
	}
	
	public void tekenFunctie(Context2d g, int xMin, Expressie expressie, double[] domein, CssColor kleur)
	{
		Expressie exp = expressie;
		for (int i=0; i<interactiePanel.schuifParameters.length; i++) {
			exp = exp.substitueer(interactiePanel.schuifParameters[i].geefWaarde(), interactiePanel.schuifParameters[i].geefNaam());			
		}
		//Logger.getLogger("grafiekGWTVeld").info("tekenFunctie: " + exp.toString());
		g.beginPath();
		//int xMin = Math.max(witruimteY?maxWoordBreedteY:0, interactiePanel.xPositief?bx:0);
		int xMax = breedte;
		if(interactiePanel.domeinen != null && domein != null)
		{	if(!Double.isInfinite(domein[0])) {	
//				int xMin2 = (int) Math.round(interactiePanel.eenheidxD*(interactiePanel.xAsLog?Math.log10(domein[0]):domein[0])
//					/interactiePanel.schaalFactorX + interactiePanel.beginx);	
				int xMin2 = (int) Math.round(valueXtoPixels(domein[0]));

				xMin = Math.max(xMin, xMin2);
			}
			if(!Double.isInfinite(domein[1])) {	
//				int xMax2 = (int) Math.round(interactiePanel.eenheidxD*(interactiePanel.xAsLog?Math.log10(domein[1]):domein[1])
//					/interactiePanel.schaalFactorX + interactiePanel.beginx);	
				int xMax2 = (int) Math.round(valueXtoPixels(domein[1]));
				xMax = Math.min(xMax, xMax2);
			}
		}
		for(int i=xMin; i<xMax ; i++)
		{	double ii = i;
//			double d0 = (exp.substitueer(interactiePanel.xAsLog?Math.pow(10,interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii
//					/interactiePanel.eenheidxD):interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii/interactiePanel.eenheidxD, interactiePanel.grafiekXAsNaam)).geefWaarde();//dd0.doubleValue();
//			double d1 = (exp.substitueer(interactiePanel.xAsLog?Math.pow(10,interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+1)
//					/interactiePanel.eenheidxD):interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+1)/interactiePanel.eenheidxD, interactiePanel.grafiekXAsNaam)).geefWaarde();//dd0.doubleValue();
			double d0 = exp.substitueer(pixelsXtoValue(ii),  interactiePanel.grafiekXAsNaam).geefWaarde();
			double d1 = exp.substitueer(pixelsXtoValue(ii+1),  interactiePanel.grafiekXAsNaam).geefWaarde();
			double d0waarde = d0;
			double d1waarde = d1;
			if(Double.isNaN(d1) && !Double.isNaN(d0))
			{	double newD1waarde = d0;
				for(int k = 1; k < 20; k++)
				{	double kd = k;
//					double dt0 = (exp.substitueer(interactiePanel.xAsLog?Math.pow(10,interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+kd/20)
//						/interactiePanel.eenheidxD):interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+kd/20)/interactiePanel.eenheidxD, interactiePanel.grafiekXAsNaam)).geefWaarde();
					double dt0 = exp.substitueer(pixelsXtoValue(ii+kd/20),  interactiePanel.grafiekXAsNaam).geefWaarde();

					if(Double.isNaN(dt0))
					{	break;
					}
					else
						newD1waarde = dt0;
				}
				d1waarde = newD1waarde;
			}
			if(Double.isNaN(d0) && !Double.isNaN(d1))
			{	double newD0waarde = d1;
				for(int k = 19; k > 0; k--)
				{	double kd = k;
//					double dt0 = (exp.substitueer(interactiePanel.xAsLog?Math.pow(10,interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+kd/20)
//						/interactiePanel.eenheidxD):interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+kd/20)/interactiePanel.eenheidxD, interactiePanel.grafiekXAsNaam)).geefWaarde();
					double dt0 = exp.substitueer(pixelsXtoValue(ii+kd/20),  interactiePanel.grafiekXAsNaam).geefWaarde();
					if(Double.isNaN(dt0))
					{	break;
					}
					else
						newD0waarde = dt0;
				}
				d0waarde = newD0waarde;
			}
			if(!(Double.isNaN(d0) && Double.isNaN(d1)) && (!interactiePanel.yPositief || d0 >= 0 || d1 >= 0))
			{	int x0 = i;
				int x1 = i+1;
//				double dy0 = hoogte -(interactiePanel.beginy+interactiePanel.eenheidyD*(interactiePanel.yAsLog?Math.log10(d0waarde):d0waarde)/interactiePanel.schaalFactorY);
//				double dy1 = hoogte -(interactiePanel.beginy+interactiePanel.eenheidyD*(interactiePanel.yAsLog?Math.log10(d1waarde):d1waarde)/interactiePanel.schaalFactorY);
				double dy0 = valueYtoPixels(d0waarde);
				double dy1 = valueYtoPixels(d1waarde);

				if(dy0>1000)dy0 = 1000;
				if(dy0<-1000)dy0 = -1000;
				if(dy1>1000)dy1 = 1000;
				if(dy1<-1000)dy1 = -1000;
				
				//if(curve.getCurrentPoint()==null && (!interactiePanel.yPositief || d0>=0))
				if(!interactiePanel.yPositief || d0>=0)
					g.moveTo((float)x0, (float)dy0);
				//else if(curve.getCurrentPoint() == null) 
				else
					g.moveTo((float)x0, hoogte - interactiePanel.beginy);
				if(!interactiePanel.yPositief || d1>=0) 
					g.lineTo((float)x1, (float)dy1);
				else
					g.lineTo((float)x1, hoogte - interactiePanel.beginy);
			}
			if(Double.isNaN(d1) || interactiePanel.yPositief && d1<0)
			{	g.setFillStyle(kleur);
				g.setStrokeStyle(kleur);
				g.stroke();
			}
		}
		g.setFillStyle(kleur);
		g.setStrokeStyle(kleur);
		g.stroke();
	}
	
	
	public void tekenOngelijkheden(Context2d g)
	{	//Graphics2D g = (Graphics2D) gr;
		CssColor[][] ongelijkheidKleuren;
		if(!interactiePanel.yAsNaam.equals(interactiePanel.grafiekYAsNaam))
			return;
		//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_NORMALIZE);
		
		Area[] areas = new Area[interactiePanel.ongelijkheden.length];
		//int breedte = getCanvas().getOffsetWidth(); //werkt dit?
		//int hoogte = getCanvas().getOffsetHeight();
		
		int bx = (int)Math.round(interactiePanel.beginx);			
		int maxWoordBreedteY = 0;
		boolean witruimteY = false;


		for(int j=0 ; j<interactiePanel.ongelijkheden.length ; j++) {	
			Expressie expressie = interactiePanel.ongelijkheden[j];
			if (expressie != null ) {
				for (int i=0; i<interactiePanel.schuifParameters.length; i++) {
					expressie = expressie.substitueer(interactiePanel.schuifParameters[i].geefWaarde(), interactiePanel.schuifParameters[i].geefNaam());			
				}
			}
			if(interactiePanel.ongelijkheden[j]!=null && interactiePanel.isY[j]) {	
				

				GeneralPath curve = new GeneralPath();
				int xMin = Math.max(witruimteY?maxWoordBreedteY:-1, interactiePanel.xPositief?bx:-1);
				int xMax = breedte +1;
				double horizontaleGrens = -1;
				if(!interactiePanel.isGroterGelijk[j])
					horizontaleGrens = interactiePanel.yPositief?(hoogte - interactiePanel.beginy):hoogte+1;
				
				for(int i=xMin; i<xMax ; i++)
				{	double ii = i;
//					double d0 = (interactiePanel.ongelijkheden[j].substitueer(interactiePanel.xAsLog?Math.pow(10, interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii/interactiePanel.eenheidxD):
//						interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii/interactiePanel.eenheidxD, interactiePanel.grafiekXAsNaam)).geefWaarde();//dd0.doubleValue();
//					double d1 = (interactiePanel.ongelijkheden[j].substitueer(interactiePanel.xAsLog?Math.pow(10, interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+1)/interactiePanel.eenheidxD):
//						interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+1)/interactiePanel.eenheidxD, interactiePanel.grafiekXAsNaam)).geefWaarde();//dd0.doubleValue();
					double d0 = expressie.substitueer(pixelsXtoValue(ii), interactiePanel.grafiekXAsNaam).geefWaarde();//dd0.doubleValue();
					double d1 = expressie.substitueer(pixelsXtoValue(ii+1), interactiePanel.grafiekXAsNaam).geefWaarde();//dd0.doubleValue();

					if(!Double.isNaN(d0) && !Double.isNaN(d1))
					{	int x0 = i;
						int x1 = i+1;
//						double dy0 = hoogte -(interactiePanel.beginy+interactiePanel.eenheidyD*(interactiePanel.yAsLog?Math.log10(d0):d0)/interactiePanel.schaalFactorY);
//						double dy1 = hoogte -(interactiePanel.beginy+interactiePanel.eenheidyD*(interactiePanel.yAsLog?Math.log10(d1):d1)/interactiePanel.schaalFactorY);
						double dy0 = valueYtoPixels(d0);
						double dy1 = valueYtoPixels(d1);
						if(dy0>1000)dy0 = 1000;
						if(dy0<-1000)dy0 = -1000;
						if(dy1>1000)dy1 = 1000;
						if(dy1<-1000)dy1 = -1000;
						
						if(curve.getCurrentPoint()==null)
						{	
							curve.moveTo((float)x0, horizontaleGrens);
							//if(!gtip.yPositief || dy0 >= 0)
								curve.lineTo((float)x0, (float)dy0);
						}
						if(!interactiePanel.yPositief || d1>0) 
							curve.lineTo((float)x1, (float)dy1); 
						else
							curve.lineTo((float)x1, (float)(hoogte - interactiePanel.beginy));
					}
					
					if(Double.isNaN(d1))// || gtip.yPositief && d1<0)
					{	if(curve.getCurrentPoint()!=null)
							curve.lineTo(curve.getCurrentPoint().getX(), horizontaleGrens);
					}
					else if(Double.isNaN(d0))// || gtip.yPositief && d0<0)
						if(curve.getCurrentPoint()!=null)
						{	int x1 = i + 1;
							curve.lineTo((float)x1, horizontaleGrens);
//							double dy1 = hoogte -(interactiePanel.beginy+interactiePanel.eenheidyD*(interactiePanel.yAsLog?Math.log10(d1):d1)/interactiePanel.schaalFactorY);
							double dy1 = valueYtoPixels(d1);
							if(dy1>1000)dy1 = 1000;
							if(dy1<-1000)dy1 = -1000;
							curve.lineTo((float)x1, (float)dy1);
						}
				}
				
				if(curve.getCurrentPoint() != null)
				{	curve.lineTo(curve.getCurrentPoint().getX(), horizontaleGrens);
					curve.lineTo(xMin, horizontaleGrens);
					
				}
				areas[j] = new Area(curve);
			}
			if(interactiePanel.ongelijkheden[j] != null && !interactiePanel.isY[j])
			{	double grens = expressie.geefWaarde();
//				int pixelGrens = (int)((interactiePanel.xAsLog?Math.log10(grens):grens)*interactiePanel.eenheidxD/interactiePanel.schaalFactorX + interactiePanel.beginx);
				int pixelGrens = (int) Math.round(valueXtoPixels(grens));
				
				if(interactiePanel.isGroterGelijk[j])
				{	Rectangle rechthoek = new Rectangle(pixelGrens, - 1, breedte - pixelGrens + 1, hoogte + 2);
					areas[j] = new Area(rechthoek);
				}
				else
				{	Rectangle rechthoek = new Rectangle(-1, -1, pixelGrens + 1, hoogte + 2);
					areas[j] = new Area(rechthoek);
				}
			}
		
		}
		
		int aantalAreaClusters = 0;
		boolean inCluster = false;
		
		for(int i = 0; i < areas.length; i++)
		{	if(!inCluster && areas[i] != null && !areas[i].isEmpty())
			{	inCluster = true;
				aantalAreaClusters++;
			}
			else if(inCluster && (areas[i] == null || areas[i].isEmpty()))
				inCluster = false;
		}
		if(aantalAreaClusters == 0)
			return;
		
		int[] clusterLengtes = new int[aantalAreaClusters];
		int clusterNr = -1;
		inCluster = false;
		for(int i = 0; i < areas.length; i++)
		{	if(!inCluster && areas[i] != null && !areas[i].isEmpty())
			{	inCluster = true;
				clusterNr++;
				clusterLengtes[clusterNr] = 1;
			}
			else if(inCluster && (areas[i] == null || areas[i].isEmpty()))
			{	inCluster = false;
			}
			else if(inCluster)
			{	clusterLengtes[clusterNr]++;
			}
		}
		
		
		boolean[] enOf = new boolean[areas.length];
		for(int i = 0; i < enOf.length; i++)
			enOf[i] = interactiePanel.isEn[i];
		
		boolean[][] verwerkEnOf = new boolean[aantalAreaClusters][];
		Area[][] areaClusters = new Area[aantalAreaClusters][];
		ongelijkheidKleuren = new CssColor[aantalAreaClusters][];
		for(int i = 0; i < aantalAreaClusters; i++)
		{	verwerkEnOf[i] = new boolean[clusterLengtes[i]];
			areaClusters[i] = new Area[clusterLengtes[i]];
			ongelijkheidKleuren[i] = new CssColor[clusterLengtes[i]];
		}
		
		
		int teller = 0;
		clusterNr = 0;
		for(int i = 0; i < areas.length; i++)
			if(areas[i] != null && !areas[i].isEmpty())
			{	areaClusters[clusterNr][teller] = areas[i];
				if(interactiePanel.grafiekKleuren)
					ongelijkheidKleuren[clusterNr][teller] = interactiePanel.colors[i];
				else
					ongelijkheidKleuren[clusterNr][teller] = interactiePanel.colors[0];
				teller++;
				if(teller >= areaClusters[clusterNr].length)
				{	clusterNr++;
					teller = 0;
				}
			}
			else
			{	enOf[i] = false;
				if(i>0)
					enOf[i-1] = false;
			}
		teller = 0;
		clusterNr = 0;
		for(int i = 0; i < enOf.length; i++)
			if(areas[i] != null && !areas[i].isEmpty())
			{	verwerkEnOf[clusterNr][teller] = enOf[i];
				teller++;
				if(teller >= areaClusters[clusterNr].length)
				{	clusterNr++;
					teller = 0;
				}
			}
		
		for(int j = 0; j < areaClusters.length; j++)
		{
			while(erIsNogEn(verwerkEnOf[j]))
			{	for(int i = 0; i < areaClusters[j].length; i++)
				{	if(verwerkEnOf[j][i])
					{	areaClusters[j][i].intersect(areaClusters[j][i+1]);
						Area[] areas2 = new Area[areaClusters[j].length - 1];
						boolean[] verwerkEnOf2 = new boolean[verwerkEnOf[j].length - 1];
						for(int k = 0; k < i; k++)
						{	areas2[k] = areaClusters[j][k];
							verwerkEnOf2[k] = verwerkEnOf[j][k];//dit moet wel false zijn.
						}
						areas2[i] = areaClusters[j][i];
						verwerkEnOf2[i] = verwerkEnOf[j][i+1];
						for(int k = i + 1; k < areas2.length; k++)
						{	areas2[k] = areaClusters[j][k+1];
							verwerkEnOf2[k] = verwerkEnOf[j][k+1];
						}
						areaClusters[j] = new Area[areas2.length];
						verwerkEnOf[j] = new boolean[verwerkEnOf2.length];
						for(int k = 0; k < areaClusters[j].length; k++)
							areaClusters[j][k] = areas2[k];
						for(int k = 0; k < verwerkEnOf2.length; k++)
							verwerkEnOf[j][k] = verwerkEnOf2[k];
						break;
					}
				}
			}
			
			//g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);
			//g.setStroke(new BasicStroke(1.2f));
			g.setGlobalAlpha(0.5); //Voor halve doorschijnendheid.
			
			for(int i = areaClusters[j].length - 1; i > 0; i--)
			{	Area doorsnede = (Area) areaClusters[j][i].clone();
				doorsnede.intersect(areaClusters[j][i-1]);
				if(!doorsnede.intersects(0, 0, breedte, hoogte)) 
				{	g.setFillStyle(ongelijkheidKleuren[j][i]);
					PathIterator pi = areaClusters[j][i].getPathIterator(null);
					g.beginPath();
					while(!pi.isDone())
					{
						double[] seg = new double[6];
						int segment = pi.currentSegment(seg);
						if(segment == PathIterator.SEG_MOVETO)
							g.moveTo(seg[0], seg[1]);
						else if(segment == PathIterator.SEG_LINETO)
							g.lineTo(seg[0], seg[1]);
						else if(segment == PathIterator.SEG_QUADTO)
							g.quadraticCurveTo(seg[0], seg[1], seg[2], seg[3]);
						else if(segment == PathIterator.SEG_CUBICTO)
							g.bezierCurveTo(seg[0], seg[1], seg[2], seg[3], seg[4], seg[5]);
						else if(segment == PathIterator.SEG_CLOSE)
						{
							g.closePath();
							g.fill();
							g.beginPath();
						}
						pi.next();
					}
					g.closePath();
					g.fill();
				//g.fill(areaClusters[j][i]);
				}
				else
				{	areaClusters[j][i-1].add(areaClusters[j][i]);
				}
				
			}
			g.setFillStyle(ongelijkheidKleuren[j][0]);
			PathIterator pi = areaClusters[j][0].getPathIterator(null);
			g.beginPath();
			while(!pi.isDone())
			{
				double[] seg = new double[6];
				int segment = pi.currentSegment(seg);
				if(segment == PathIterator.SEG_MOVETO)
					g.moveTo(seg[0], seg[1]);
				else if(segment == PathIterator.SEG_LINETO)
					g.lineTo(seg[0], seg[1]);
				else if(segment == PathIterator.SEG_QUADTO)
					g.quadraticCurveTo(seg[0], seg[1], seg[2], seg[3]);
				else if(segment == PathIterator.SEG_CUBICTO)
					g.bezierCurveTo(seg[0], seg[1], seg[2], seg[3], seg[4], seg[5]);
				else if(segment == PathIterator.SEG_CLOSE)
				{
					g.closePath();
					g.fill();
					g.beginPath();
				}
				pi.next();
			}
			g.closePath();
			g.fill();
		}
		g.setGlobalAlpha(1.0);
	}

//	//nog verder uitzoeken, hoe kan ik Area's vervangen door iets waar gwt wel mee overweg kan?
//	public void tekenOngelijkheden(Context2d g)
//	{	//Graphics2D g = (Graphics2D) gr;
//		CssColor[][] ongelijkheidKleuren;
//		if(!interactiePanel.yAsNaam.equals(interactiePanel.grafiekYAsNaam))
//			return;
//		//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		//g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_NORMALIZE);
//		g.setGlobalAlpha(0.5); //ofzo? Voor halve doorschijnendheid...
//		
//		Area[] areas = new Area[interactiePanel.ongelijkheden.length];
//		int breedte = getCanvas().getOffsetWidth();//werkt dit?
//		int hoogte = getCanvas().getOffsetHeight();
//		int bx = (int)Math.round(interactiePanel.beginx);			
//		int maxWoordBreedteY = 0;
//		boolean witruimteY = false;
//		
//		for(int j = 0; j < interactiePanel.ongelijkheden.length; j++)
//		{	
//			if(interactiePanel.ongelijkheden[j] != null && interactiePanel.isY[j])
//			{
//				int xMin = Math.max(witruimteY?maxWoordBreedteY:-1, interactiePanel.xPositief?bx:-1);
//				int xMax = breedte + 1;
//				areas[j] = new Area(j, xMin, xMax, hoogte, interactiePanel);
//			}
//			if(interactiePanel.ongelijkheden[j] != null && !interactiePanel.isY[j])
//			{
//				double grens = interactiePanel.ongelijkheden[j].geefWaarde();
//				int pixelGrens = (int)((interactiePanel.xAsLog?Math.log10(grens):grens)*interactiePanel.eenheidxD/interactiePanel.schaalFactorX + interactiePanel.beginx);
//				if(interactiePanel.isGroterGelijk[j])
//				{	ArrayList<Point> punten = new ArrayList<Point>();
//					punten.add(new Point(pixelGrens, - 1));
//					punten.add(new Point(breedte + 1, - 1));
//					punten.add(new Point(breedte + 1, hoogte + 1));
//					punten.add(new Point(pixelGrens, hoogte + 1));
//					ArrayList<ArrayList<Point>> componenten = new ArrayList<ArrayList<Point>>();
//					componenten.add(punten);
//					areas[j] = new Area(componenten, interactiePanel);
//				}
//				else
//				{	ArrayList<Point> punten = new ArrayList<Point>();
//					punten.add(new Point(-1, - 1));
//					punten.add(new Point(pixelGrens, - 1));
//					punten.add(new Point(pixelGrens, hoogte + 1));
//					punten.add(new Point(-1, hoogte + 1));
//					ArrayList<ArrayList<Point>> componenten = new ArrayList<ArrayList<Point>>();
//					componenten.add(punten);
//					areas[j] = new Area(componenten, interactiePanel);
//				}
//			}
//		}
//		
//		
//		int aantalAreaClusters = 0;
//		boolean inCluster = false;
//		
//		for(int i = 0; i < interactiePanel.ongelijkheden.length; i++)
//		{	if(!inCluster && interactiePanel.ongelijkheden[i] != null)
//			{	inCluster = true;
//				aantalAreaClusters++;
//			}
//			else if(inCluster && interactiePanel.ongelijkheden[i] == null)
//				inCluster = false;
//		}
//		if(aantalAreaClusters == 0)
//			return;
//		
//		int[] clusterLengtes = new int[aantalAreaClusters];
//		int clusterNr = -1;
//		inCluster = false;
//		for(int i = 0; i < interactiePanel.ongelijkheden.length; i++)
//		{	if(!inCluster && interactiePanel.ongelijkheden[i] != null)
//			{	inCluster = true;
//				clusterNr++;
//				clusterLengtes[clusterNr] = 1;
//			}
//			else if(inCluster && interactiePanel.ongelijkheden[i] == null)
//			{	inCluster = false;
//			}
//			else if(inCluster)
//			{	clusterLengtes[clusterNr]++;
//			}
//		}
//			
//		boolean[] enOf = new boolean[interactiePanel.ongelijkheden.length];
//		for(int i = 0; i < enOf.length; i++)
//			enOf[i] = interactiePanel.isEn[i];
//		
//		boolean[][] verwerkEnOf = new boolean[aantalAreaClusters][];
//		//Expressie[][] ongelijkheidClusters = new Expressie[aantalAreaClusters][];
//		Area[][] areaClusters = new Area[aantalAreaClusters][];
//		ongelijkheidKleuren = new CssColor[aantalAreaClusters][];
//		for(int i = 0; i < aantalAreaClusters; i++)
//		{	verwerkEnOf[i] = new boolean[clusterLengtes[i]];
//			//ongelijkheidClusters[i] = new Expressie[clusterLengtes[i]];
//			areaClusters[i] = new Area[clusterLengtes[i]];
//			ongelijkheidKleuren[i] = new CssColor[clusterLengtes[i]];
//		}
//		
//		
//		int teller = 0;
//		clusterNr = 0;
//		for(int i = 0; i < interactiePanel.ongelijkheden.length; i++)
//			if(interactiePanel.ongelijkheden[i] != null)
//			{	areaClusters[clusterNr][teller] = areas[i];
//				//ongelijkheidClusters[clusterNr][teller] = interactiePanel.ongelijkheden[i];
//				if(interactiePanel.grafiekKleuren)
//				{	ongelijkheidKleuren[clusterNr][teller] = interactiePanel.colors[i];
//				}
//				else
//					ongelijkheidKleuren[clusterNr][teller] = interactiePanel.colors[0];
//				teller++;
//				if(teller >= areaClusters[clusterNr].length)
//				{	clusterNr++;
//					teller = 0;
//				}
//			}
//			else
//			{	enOf[i] = false;
//				if(i>0)
//					enOf[i-1] = false;
//			}
//		teller = 0;
//		clusterNr = 0;
//		for(int i = 0; i < enOf.length; i++)
//			if(interactiePanel.ongelijkheden[i] != null)
//			{	verwerkEnOf[clusterNr][teller] = enOf[i];
//				teller++;
//				if(teller >= areaClusters[clusterNr].length)
//				{	clusterNr++;
//					teller = 0;
//				}
//			}
//		
//		for(int j = 0; j < areaClusters.length; j++)
//		{
//			while(erIsNogEn(verwerkEnOf[j]))
//			{	for(int i = 0; i < areaClusters[j].length; i++)
//				{	if(verwerkEnOf[j][i])
//					{	areaClusters[j][i].intersect(areaClusters[j][i+1]);
//						Area[] areas2 = new Area[areaClusters[j].length - 1];
//						boolean[] verwerkEnOf2 = new boolean[verwerkEnOf[j].length - 1];
//						for(int k = 0; k < i; k++)
//						{	areas2[k] = areaClusters[j][k];
//							verwerkEnOf2[k] = verwerkEnOf[j][k];//dit moet wel false zijn.
//						}
//						areas2[i] = areaClusters[j][i];
//						verwerkEnOf2[i] = verwerkEnOf[j][i+1];
//						for(int k = i + 1; k < areas2.length; k++)
//						{	areas2[k] = areaClusters[j][k+1];
//							verwerkEnOf2[k] = verwerkEnOf[j][k+1];
//						}
//						areaClusters[j] = new Area[areas2.length];
//						verwerkEnOf[j] = new boolean[verwerkEnOf2.length];
//						for(int k = 0; k < areaClusters[j].length; k++)
//							areaClusters[j][k] = areas2[k];
//						for(int k = 0; k < verwerkEnOf2.length; k++)
//							verwerkEnOf[j][k] = verwerkEnOf2[k];
//						break;
//					}
//				}
//			}
//			
//			//g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);
//			//g.setStroke(new BasicStroke(1.2f));
//			
//			
//			for(int i = areaClusters[j].length - 1; i > 0; i--)
//			{	Area doorsnede = (Area) areaClusters[j][i].clone();
//				doorsnede.intersect(areaClusters[j][i-1]);
//				ArrayList<Point> punten = new ArrayList<Point>();
//				punten.add(new Point(0, 0));
//				punten.add(new Point(0, hoogte));
//				punten.add(new Point(breedte, hoogte));
//				punten.add(new Point(breedte, 0));
//				ArrayList<ArrayList<Point>> componenten = new ArrayList<ArrayList<Point>>();
//				componenten.add(punten);
//				if(!doorsnede.intersects(new Area(componenten, interactiePanel))) 
//				{	//g.setColor(new Color(ongelijkheidKleuren[j][i].getRed(), ongelijkheidKleuren[j][i].getGreen(), ongelijkheidKleuren[j][i].getBlue(), 128));//door
//					g.setFillStyle(ongelijkheidKleuren[j][i]);//door
//					areaClusters[j][i].fill(g);
//					//g.fill(areaClusters[j][i]);
//				}
//				else
//				{	areaClusters[j][i-1].add(areaClusters[j][i]);
//				}
//				
//			}
//			g.setFillStyle(ongelijkheidKleuren[j][0]);
//			areaClusters[j][0].fill(g);
//		}
//		
//		/*
//		Area[] areas = new Area[interactiePanel.ongelijkheden.length];
//		int breedte = grafiekGWTCanvas.getOffsetWidth();
//		int hoogte = grafiekGWTCanvas.getOffsetHeight();
//		int bx = (int)Math.round(interactiePanel.beginx);			
//		int maxWoordBreedteY = 0;
//		boolean witruimteY = false;
//		
//		for(int j=0 ; j<interactiePanel.ongelijkheden.length ; j++)
//		{	if(interactiePanel.ongelijkheden[j]!=null && interactiePanel.isY[j])
//			{	g.beginPath();	
//			//GeneralPath curve = new GeneralPath();
//				int xMin = Math.max(witruimteY?maxWoordBreedteY:-1, interactiePanel.xPositief?bx:-1);
//				int xMax = breedte +1;
//				double horizontaleGrens = -1;
//				if(!interactiePanel.isGroterGelijk[j])
//					horizontaleGrens = interactiePanel.yPositief?(hoogte - interactiePanel.beginy):hoogte+1;
//				
//				for(int i=xMin; i<xMax ; i++)
//				{	double ii = i;
//					double d0 = (interactiePanel.ongelijkheden[j].substitueer(interactiePanel.xAsLog?Math.pow(10, interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii/interactiePanel.eenheidxD):
//						interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*ii/interactiePanel.eenheidxD, interactiePanel.grafiekXAsNaam)).geefWaarde();//dd0.doubleValue();
//					double d1 = (interactiePanel.ongelijkheden[j].substitueer(interactiePanel.xAsLog?Math.pow(10, interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+1)/interactiePanel.eenheidxD):
//						interactiePanel.schaalFactorX*(-interactiePanel.beginx)/interactiePanel.eenheidxD + interactiePanel.schaalFactorX*(ii+1)/interactiePanel.eenheidxD, interactiePanel.grafiekXAsNaam)).geefWaarde();//dd0.doubleValue();
//					if(!Double.isNaN(d0) && !Double.isNaN(d1))
//					{	int x0 = i;
//						int x1 = i+1;
//						double dy0 = hoogte -(interactiePanel.beginy+interactiePanel.eenheidyD*(interactiePanel.yAsLog?Math.log10(d0):d0)/interactiePanel.schaalFactorY);
//						double dy1 = hoogte -(interactiePanel.beginy+interactiePanel.eenheidyD*(interactiePanel.yAsLog?Math.log10(d1):d1)/interactiePanel.schaalFactorY);
//						if(dy0>1000)dy0 = 1000;
//						if(dy0<-1000)dy0 = -1000;
//						if(dy1>1000)dy1 = 1000;
//						if(dy1<-1000)dy1 = -1000;
//						
//						//if(curve.getCurrentPoint()==null)
//						//{	
//							g.moveTo((float)x0, horizontaleGrens); // gaat niet goed..
//							//if(!interactiePanel.yPositief || dy0 >= 0)
//								g.lineTo((float)x0, (float)dy0);
//						//}
//						if(!interactiePanel.yPositief || d1>0) 
//							g.lineTo((float)x1, (float)dy1); 
//						else
//							g.lineTo((float)x1, (float)(hoogte - interactiePanel.beginy));
//					}
//					
//					if(Double.isNaN(d1))// || gtip.yPositief && d1<0)
//					{	//if(g.getCurrentPoint()!=null)
//							g.lineTo((float) i+1, horizontaleGrens);
//					}
//					else if(Double.isNaN(d0))// || gtip.yPositief && d0<0)
//						//if(curve.getCurrentPoint()!=null)
//						{	
//							int x1 = i + 1;
//							g.lineTo((float)x1, horizontaleGrens);
//							double dy1 = hoogte -(interactiePanel.beginy+interactiePanel.eenheidyD*(interactiePanel.yAsLog?Math.log10(d1):d1)/interactiePanel.schaalFactorY);
//							if(dy1>1000)dy1 = 1000;
//							if(dy1<-1000)dy1 = -1000;
//							g.lineTo((float)x1, (float)dy1);
//						}
//				}
//				
//				//if(curve.getCurrentPoint() != null)
//				{	g.lineTo(xMax, horizontaleGrens);
//					g.lineTo(xMin, horizontaleGrens);
//					
//				}
//				g.closePath();
//				areas[j] = new Area(curve);
//			}
//			if(interactiePanel.ongelijkheden[j] != null && !interactiePanel.isY[j])
//			{	double grens = interactiePanel.ongelijkheden[j].geefWaarde();
//				int pixelGrens = (int)((interactiePanel.xAsLog?Math.log10(grens):grens)*interactiePanel.eenheidxD/interactiePanel.schaalFactorX + interactiePanel.beginx);
//				if(interactiePanel.isGroterGelijk[j])
//				{	Rectangle rechthoek = new Rectangle(pixelGrens, - 1, getWidth() - pixelGrens + 1, getHeight() + 2);
//					areas[j] = new Area(rechthoek);
//				}
//				else
//				{	Rectangle rechthoek = new Rectangle(-1, -1, pixelGrens + 1, getHeight() + 2);
//					areas[j] = new Area(rechthoek);
//				}
//			}
//		
//		}
//		
//		*/
//		
//		
//		
//	}
	
	public boolean erIsNogEn(boolean[] enOfReeks)
	{
		for(int i = 0; i < enOfReeks.length - 1; i++)
		{	if(enOfReeks[i])
				return true;
		}
		return false;
	}
	
	public void tekenVerticaleLijnen(Context2d g) {
		//Graphics2D g = (Graphics2D) gr;
		Expressie expressie;
        for(int j=0 ; j<interactiePanel.verticaleLijnen.length ; j++) {
        	if(interactiePanel.verticaleLijnen[j]!=null) { 
        		expressie = interactiePanel.verticaleLijnen[j];
    			if (expressie != null ) {
    				for (int i=0; i<interactiePanel.schuifParameters.length; i++) {
    					expressie = expressie.substitueer(interactiePanel.schuifParameters[i].geefWaarde(), interactiePanel.schuifParameters[i].geefNaam());			
    				}
    			}

        		double xWaarde = expressie.geefWaarde();
//              double xWaardePixels = interactiePanel.beginx + interactiePanel.eenheidxD*(interactiePanel.xAsLog?Math.log10(xWaarde):xWaarde)/interactiePanel.schaalFactorX;
				double xWaardePixels = valueXtoPixels(xWaarde);

                //GeneralPath curve = new GeneralPath();
                g.beginPath();
                g.moveTo(xWaardePixels,  0);
                g.lineTo(xWaardePixels, interactiePanel.yPositief?(hoogte - interactiePanel.beginy):hoogte);
                //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);
                //g.setStroke(new BasicStroke(1.2f));
                //g.setColor(interactiePanel.colors[j]);
                g.setStrokeStyle(interactiePanel.colors[j]);
                g.stroke();
            }
        }
    }


	public int geefSliderStand()
	{	return stand;
	}
	
	public void zetSliderStand(int std)
	{	if(std>breedte)stand = breedte;
		else if(std<minimum)stand = minimum;
		else stand = std;
		
		
	//	paint(); 
	}
	
	//om de schaalverdeling bij logschalen in orde te krijgen; 
	//deze methode werkt alleen goed bij strings die volledig uit getallen bestaan.
	public String toSuperScript(String s)
	{	if(s.contains("."))
		{	s = s.substring(0, Math.min(s.indexOf(".") + 3, s.length()));
			while(s.endsWith("0"))
			{
				s = s.substring(0, s.length() - 1);
			}
			if(s.endsWith("."))
			{
				s = s.substring(0, s.length() - 1);
			}
		}
		
		s = s.replaceAll("0", "\u2070");
		s = s.replaceAll("1", "\u00B9");
		s = s.replaceAll("2", "\u00B2");
		s = s.replaceAll("3", "\u00B3");
		s = s.replaceAll("4", "\u2074");
		s = s.replaceAll("5", "\u2075");
		s = s.replaceAll("6", "\u2076");
		s = s.replaceAll("7", "\u2077");
		s = s.replaceAll("8", "\u2078");
		s = s.replaceAll("9", "\u2079"); 
		s = s.replaceAll("-", "\u207B");
		
		
		return s;
	}
	
	/*
	public void mousePressed(MouseEvent e)
	{	raak = (new Rectangle(stand,0,10,20)).contains(e.getX(), e.getY());
		muisStartX = e.getX();
		if (raak)// && actionListener != null)
		{	//actionListener.actionPerformed( new ActionEvent(this, 0, "start") );
		}
	}
	
	public void mouseDragged(MouseEvent e)
	{	if(!raak && new Rectangle(stand,0,10,20).contains(e.getX(), e.getY()))
		{	raak = true;
			muisStartX = e.getX();
			if (raak)// && actionListener != null)
			{	//actionListener.actionPerformed( new ActionEvent(this, 0, "start") );
			}
		}
		if(raak)
		{	int x = e.getX();
			int dx = x - muisStartX;
			stand = stand + dx;
			if(stand>breedte) 
			{	stand = breedte;
			}
			else if(stand<minimum) 
			{	stand = minimum;
			}
			if(x<0 || x>breedte)
			{	raak = false;
			}
			paint(gIm);
			//if (actionListener != null)
 			//{	actionListener.actionPerformed( new ActionEvent(this, 0, "verschoven") );
 			//}
			muisStartX = x;
		}
	}
	*/

/*
	class ZoomDraad extends Thread 
	{	boolean dood = false;
		boolean x,y,in;
		
		ZoomDraad(boolean x, boolean y, boolean in)
		{	this.x = x;
			this.y = y;
			this.in = in;
		}
		
		public void run()
		{	if(x) selectnummer = 999;
	        eenheidxD = xAsLog?2*eenheid:eenheid;
			eenheidyD = yAsLog?2*eenheid:eenheid;
			eenheidx = xAsLog?2*eenheid:eenheid;
			eenheidy = yAsLog?2*eenheid:eenheid;
			double stapx, stapy;
			double factorx = 1;
			double factory = 1;
			
			//double middenx = veldb/2/eenheidx*eenheidx;
			//double middeny = veldh/2/eenheidy*eenheidy;
			
			double middenx = breedte/2/eenheidx*eenheidx;
			double middeny = hoogte/2/eenheidy*eenheidy;
			
			if(in && x)
			{	if(factorRijNummerX%3==2)
					factorx=0.4;
				else 
					factorx=0.5;
			}
			else if(!in && x)
			{	if(factorRijNummerX%3==1)
					factorx=2.5;
				else 
					factorx=2;
			}
			
			if(in && y)
			{	if(factorRijNummerY%3==2)
					factory =0.4;
				else 
					factory=0.5;
			}
			
			else if(!in && y)
			{	if(factorRijNummerY%3==1)
					factory =2.5;
				else 
					factory=2;
			}
			
			stapx= Math.pow(factorx,0.1);
			stapy= Math.pow(factory,0.1);
			
			for(int i=0 ; i<5 ; i++)
			{	int delay = 20;
				long t = System.currentTimeMillis();
				try
				{	t = t+delay;
					sleep(Math.max(1, t-System.currentTimeMillis()));
				}
				catch(InterruptedException e)    // geen ;
				{   };
				eenheidxD = eenheidxD/stapx;
				eenheidyD = eenheidyD/stapy;
				eenheidx = (int) Math.round(eenheidxD);
				eenheidy = (int) Math.round(eenheidyD);
				beginx =  middenx -(middenx - beginx)/stapx;
				beginy =  middeny -(middeny - beginy)/stapy;
				
				tracexD = middenx -(middenx - tracexD)/stapx;
				
				beginwaarde = 1-(int)Math.round(beginx/eenheidx);
				tracex = (int) Math.round(tracexD);
				zetSliderStand(tracex);
				
				paint();
			}
			
			schaalFactorX*=factorx;
			if(in && x)factorRijNummerX--;
			if(!in && x)factorRijNummerX++;
			schaalFactorY*=factory;
			if(in && y)factorRijNummerY--;
			if(!in && y)factorRijNummerY++;
			
			eenheidxD = eenheidxD*factorx;
			eenheidyD = eenheidyD*factory;
			
			for(int i=0 ; i<5 ; i++)
			{	int delay = 20;
				long t = System.currentTimeMillis();
				try
				{	t = t+delay;
					sleep(Math.max(1, t-System.currentTimeMillis()));
				}
				catch(InterruptedException e)    // geen ;
				{   };
				eenheidxD = eenheidxD/stapx;
				eenheidyD = eenheidyD/stapy;
				eenheidx = (int) Math.round(eenheidxD);
				eenheidy = (int) Math.round(eenheidyD);
				beginx =  middenx -(middenx - beginx)/stapx;
				beginy =  middeny -(middeny - beginy)/stapy;
				
				tracexD = middenx -(middenx - tracexD)/stapx;
				
				beginwaarde = 1-(int)Math.round(beginx/eenheidx);
				tracex = (int) Math.round(tracexD);
				zetSliderStand(tracex);
				
				paint();
			}
			beginwaarde = 1-(int)Math.round(beginx/eenheidx);
			double beginwaardeD = 1.0-(beginx/eenheidx);
			
			tracexD = tracexD + eenheid*(beginwaardeD - beginwaarde);
			tracex = (int) Math.round(tracexD);
			zetSliderStand(tracex);
			
			if(x)selectnummer = 999;
			
			paint();
			
		}
		public void maakDood()
		{	dood = true;
		}
	}
	*/
	
	/*
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	mouseDown = true;
		
	
	
	
		//slider:
		raak = (eventX >= stand - 5 && eventX <= stand + 5 
				&& eventY >= hoogte - interactiePanel.beginy - 5 && eventY <= hoogte - interactiePanel.beginy + 5);
		//muisStartX = eventX;
		if (raak)
		{	tracing = true;
			tracex = geefSliderStand();
			tracexD = tracex;
		}
		interactiePanel.startxv = eventX;
		interactiePanel.startyv = eventY;
		paint();
	}
	
	public void mouseMoveTouchMoveAction(int eventX, int eventY, boolean shiftPressed)
	{	if (!mouseDown)
		return;
		
		if(!raak && (eventX >= stand - 5 && eventX <= stand + 5 
			&& eventY >= hoogte - interactiePanel.beginy - 5 && eventY > hoogte - interactiePanel.beginy + 5))
			{	raak = true;
				//muisStartX = eventX;
			if (raak)
			{	tracing = true;
				tracex = geefSliderStand();
				tracexD = tracex;
			}
		}
		if(raak )
		{	int x = eventX;
			int dx = x - interactiePanel.startxv;
			//stand = stand + dx;
			tracexD = tracexD+dx;
			tracex = tracex+dx;
			zetSliderStand(tracex);
			/*
			if(stand>breedte) 
			{	stand = breedte;
			}
			else if(stand<minimum) 
			{	stand = minimum;
			}
			*/
	/*
			if(x<0 || x>breedte)
			{	raak = false;
			}
			
			//tracex = geefSliderStand();
			//tracexD = tracex;
			paint(gIm);
			
			interactiePanel.startxv = x;
			interactiePanel.startyv = eventY;
		}
		else
		{	int dx = eventX - interactiePanel.startxv;
			int dy =  eventY - interactiePanel.startyv;					
			interactiePanel.beginx = interactiePanel.beginx+dx;
			interactiePanel.beginy = interactiePanel.beginy-dy;
			
			int b = beginwaarde;
			beginwaarde = 1-(int)Math.round(interactiePanel.beginx/interactiePanel.eenheidx);
			selectnummer = selectnummer + b - beginwaarde;
			
			interactiePanel.startxv = eventX;
			interactiePanel.startyv = eventY;
			
			paint();
		}
		
	}
	
	public void mouseUpTouchEndAction()
	{	double beginxR = interactiePanel.beginx;
		interactiePanel.beginx = interactiePanel.eenheidx*Math.round(interactiePanel.beginx/interactiePanel.eenheidx);
		interactiePanel.beginy = interactiePanel.eenheidy*Math.round(interactiePanel.beginy/interactiePanel.eenheidy);
		
		if(interactiePanel.traceOptie && tracex!=-2) 
		{	tracexD += interactiePanel.beginx-beginxR;
			tracex += interactiePanel.beginx-beginxR;
			zetSliderStand(tracex);
		}
	
		paint();
		
	}
	*/
	
	/*
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		
		public void onMouseDown(MouseDownEvent e)
		{
			//e.preventDefault();
			
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		//public void mouseDragged(MouseEvent e)
		public void onMouseMove(MouseMoveEvent e)	
		{
			//e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
//System.out.println("mouse move veld");			
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();
			boolean shiftPressed = e.isShiftKeyDown();

//System.out.println("sp = " + shiftPressed);

			mouseMoveTouchMoveAction(eventX, eventY, shiftPressed);
			
			
			
		} // onMouseMove
		
		//public void mouseReleased(MouseEvent e)
		public void onMouseUp(MouseUpEvent e)	
		{
			//e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			mouseUpTouchEndAction();

		}

	} //MLMML


	// tablet, dwo 
	class MGWTTouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				
				int eventX = touch.getPageX() - grafiekGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - grafiekGWTCanvas.getAbsoluteTop();				
				
				mouseDownTouchStartAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				//boolean shiftPressed = e.isShiftKeyDown();

			    boolean shiftPressed = false;
			    int eventX = touch.getPageX() - grafiekGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - grafiekGWTCanvas.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(eventX, eventY, shiftPressed);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			mouseUpTouchEndAction();
		}

	}
	*/
}

