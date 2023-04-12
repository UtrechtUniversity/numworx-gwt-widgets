package fi.graphtoolgwt.client;

import com.google.gwt.canvas.dom.client.Context2d;

//import java.util.logging.Logger;

public class SchuifParameterGWT {
	
//	private static Logger logger = Logger.getLogger("SchuifParameterGWT");

	final static double cDefault_onderGrensWaarde = 0.0;
	final static double cDefault_bovenGrensWaarde = 5.0;
	final static double cDefault_stapGrootte = 0.1;
	
	final static int cDefault_xLocation = 15;
	final static int cDefault_yLocation = 15;
	final static double cDefault_beginstandPercentage = 10;

	private SliderGWT slider;
	private boolean schuifModus;
	
	public SchuifParameterGWT(int aantalPix, String naam) {
		slider = new SliderGWT(aantalPix, (int) Math.round(aantalPix /100 * cDefault_beginstandPercentage) );
		slider.setLocation(cDefault_xLocation, cDefault_yLocation);
		slider.zetNaam(naam);
		slider.zetLengte(aantalPix);
		schuifModus = false;
	}
	
	public void paint(Context2d g) { 
		if(!slider.geefHideSlider())
			slider.paint(g);
	}
	
	public boolean mouseTouchPressed( int x, int y) {
		schuifModus = slider.isRaak(x,  y);
		return schuifModus;
	}
	
	public boolean mouseTouchMoved (int x, int y) {
		if (schuifModus && x>0) {
			slider.zetStand(x);
		}
		return schuifModus;
	}
	
	public boolean mouseTouchUp(int x, int y) {
		boolean oldSchuifModus = schuifModus;
		if (schuifModus) {
			schuifModus = false;
			if (x>0) {
				slider.zetStand(x);
			}
		}
		return schuifModus;
	}
	
	public void zetGrensWaarden(double onderGrensWaarde, double bovenGrensWaarde) {
		slider.zetGrensWaarden(onderGrensWaarde, bovenGrensWaarde);
	}
	
	public String geefNaam() {
		return slider.geefNaam();
	}
	
	public void zetLocatie(int x, int y) {
		slider.setLocation(x, y);
	}
	
	public void zetStapGrootte(double stapGrootte) {
		slider.zetStapGrootte(stapGrootte);
	}
	
	public double geefWaarde() {
		return slider.geefWaarde();
	}
	
	public void zetWaarde(double waarde) {
		slider.zetWaarde(waarde);
	}
	
	public int[] geefPositie() {
		return slider.geefPositie();
	}
	
	public int geefLengte() {
		return slider.geefLengte();
	}
	
	public boolean geefHideSlider() {
		return slider.geefHideSlider();
	}
	
	public void zetHideSlider(boolean b) {
		slider.zetHideSlider(b);
	}
	
}
