package fi.graphtoolgwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

// import java.util.logging.Logger;

public class SliderGWT {
	
//	private static Logger logger = Logger.getLogger("SliderGWT");
	
	private final static int cDefault_x = 15;
	private final static int cDefault_y = 15;
	private final static int cDefault_marge = 3;

	private final static int cTekstYOffset = 5;
	private final static int cKnobSize = 3;

	private int marge = cDefault_marge;
	private int x = cDefault_x; 
	private int y = cDefault_y;
	
	private String naam = "a";
	
	private int lengte;
	private int stand;
	private double waarde;
	
	private double onderGrensWaarde = SchuifParameterGWT.cDefault_onderGrensWaarde;
	private double bovenGrensWaarde = SchuifParameterGWT.cDefault_bovenGrensWaarde;
	private double stapGrootte = SchuifParameterGWT.cDefault_stapGrootte;
	
	static CssColor rood = CssColor.make(255, 0, 0); // Vulkleur slider-knop
	static CssColor zwart = CssColor.make(0, 0, 0);  // Tekenkleur
	static String fontString = "10px sans-serif";
	
	public SliderGWT(int aantalPix, int beginStand) {	

		lengte = aantalPix;
		stand = beginStand;
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void zetLengte(int aantalPix) {	
		lengte = aantalPix;
	}
	
	public void zetNaam(String naam) {
		this.naam = naam;
	}
	
	public boolean isRaak(int xPos, int yPos) {
		boolean xRaak = ((xPos >= x+stand-marge) && (xPos <= x+stand+marge));
		boolean yRaak = ((yPos >= y-marge) && (yPos <= y+marge));

		return (xRaak && yRaak);
	}
	
	public void paint(Context2d g) { 
		
		g.setStrokeStyle(zwart);
		g.setFillStyle(rood);
		
		g.beginPath();
		g.moveTo(x,  y);
		g.lineTo(x + lengte, y);
		g.stroke();

		g.beginPath();
		g.arc(x + stand, y, cKnobSize, 0, 2 * Math.PI); 
		
		g.closePath();
		g.fill();
		g.stroke();
		
		if(naam.length() > 0) {	
			g.setFont(fontString);
			TextMetrics tm = g.measureText(naam);
			
			int naamBreedte = (int) Math.round(tm.getWidth());
			
			g.setFillStyle(zwart);
			waarde = (double) Math.round(1000 * waarde) / 1000;
			g.fillText(naam + "=" + waarde, stand + x - naamBreedte, y-cTekstYOffset);
			
		}		
	}
	
	public void update(Context2d gIm) {	
		paint(gIm);
	}
	
	public int geefStand() {	
		return stand;
	}
	
	public String geefNaam() {	
		return naam;
	}
	
	public double geefWaarde() {
		return waarde;
	}
	
	public void zetStapGrootte(double stapGrootte)
	{
		this.stapGrootte = stapGrootte;
	}
	
	public void zetGrensWaarden (double onderGrensWaarde, double bovenGrensWaarde) {
		this.onderGrensWaarde = onderGrensWaarde;
		this.bovenGrensWaarde = bovenGrensWaarde;
	}
	
	public void zetStand(int xPos) {
		
		if (xPos <= x) {
			stand = 0;
			waarde = onderGrensWaarde;
			return;
		}
		
		if (xPos >= x + lengte -1) {
			stand = lengte - 1;
			waarde = bovenGrensWaarde;
			return;
		}		

		// Afronden op stapGrootte & pixels
		double deltaWaarde = (double) ( (double) (xPos-x) / lengte) * (bovenGrensWaarde-onderGrensWaarde);
		deltaWaarde = Math.round(deltaWaarde / stapGrootte) * stapGrootte;
		stand = (int) Math.round(deltaWaarde / (bovenGrensWaarde-onderGrensWaarde) * lengte -1 );
		waarde = deltaWaarde + onderGrensWaarde;
		
	}
	
	public void zetWaarde(double waarde) {
		if (waarde >= bovenGrensWaarde) {
			this.waarde = bovenGrensWaarde;
			stand = lengte - 1;
			return;
		}
		
		if (waarde <= onderGrensWaarde) {
			this.waarde = onderGrensWaarde;
			stand = 0;
			return;
		}

		this.waarde = waarde;
		stand = (int) Math.round( (double) (waarde - onderGrensWaarde) / (bovenGrensWaarde - onderGrensWaarde) * lengte - 1);
	}
	
	public int[] geefPositie() {
		int[] positie;
		positie = new int[2];
		positie[0] = x;
		positie[1] = y;
		return (positie);
	}
	
	public int geefLengte() {
		return lengte;
	}
}
