package fi.graphtoolgwt.client;

import com.google.gwt.canvas.dom.client.Context2d;


public class SchuifParameterGWT {

	final static double cDefault_onderGrensWaarde = 0.0;
	final static double cDefault_bovenGrensWaarde = 5.0;
	final static double cDefault_stapGrootte = 0.1;
	
	final static int cDefault_xLocation = 15;
	final static int cDefault_yLocation = 15;
	final static double cDefault_beginstandPercentage = 10;

	private SliderGWT slider;
	private boolean schuifModus;
	
	public SchuifParameterGWT(int aantalPix, String naam)
	{
//		lengte = aantalPix;
		slider = new SliderGWT(aantalPix, (int) Math.round(aantalPix /100 * cDefault_beginstandPercentage) );
//		slider.zetMinimum(0);
//		this.naam = naam;
//		onderGrensWaarde = cDefault_onderGrensWaarde;
//		bovenGrensWaarde = cDefault_bovenGrensWaarde;
//		stapGrootte = cDefault_stapGrootte;
//		x = 5;
//		y = 5;
		slider.setLocation(cDefault_xLocation, cDefault_yLocation);
		slider.zetNaam(naam);
//		slider.zetGrensWaarden(onderGrensWaarde, bovenGrensWaarde);
		slider.zetLengte(aantalPix);
		schuifModus = false;
	}
	
	public void paint(Context2d g) { 
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
	
	public void zetGrensWaarden(double onderGrensWaarde, double bovenGrensWaarde)
	{
		slider.zetGrensWaarden(onderGrensWaarde, bovenGrensWaarde);
	}
	
//	public double geefOnderGrens()
//	{
//		return slider.geefOnderGrens();
//	}
//	
//	public double geefBovenGrens()
//	{
//		return slider.geefOnderGrens();
//	}
	
//	public double geefStapGrootte()
//	{
//		return stapGrootte;
//	}
	
	public String geefNaam()
	{
		return slider.geefNaam();
	}
	
	public void zetLocatie(int x, int y)
	{
//		this.x = x;
//		this.y = y;
		slider.setLocation(x, y);
	}
	
//	public int getX()
//	{
//		return x;
//	}
//
//	public int getY()
//	{
//		return y;
//	}
	
	public void zetStapGrootte(double stapGrootte)
	{
		slider.zetStapGrootte(stapGrootte);
	}
	
	//geefDoubleStand() leidt de waarde van de parameter af uit de stand in pixels zoals die door de slider wordt teruggegeven.
//	public double geefDoubleStand()
//	{
//		double pixStand = slider.geefStand();
//		double doubleLengte = lengte;
//		double stand = pixStand/doubleLengte * (bovenGrensWaarde - onderGrensWaarde) + onderGrensWaarde;
//		//nu nog afronden met behulp van stapgrootte.
//		int aantalStappen = (int) ((bovenGrensWaarde - onderGrensWaarde)/stapGrootte);
//		for(int i = 0; i < aantalStappen; i++)
//		{	if(stand - onderGrensWaarde < i * stapGrootte + stapGrootte/2)
//			{	stand = onderGrensWaarde + i * stapGrootte;
//				break;
//			}
//		}
//		if(stand - onderGrensWaarde > (aantalStappen - 1) * stapGrootte + stapGrootte/2)
//			stand = bovenGrensWaarde;
//		return stand;
//	}
	
//	public int geefLengte()
//	{
//		return lengte;
//	}
	
	public double geefWaarde() {
		return slider.geefWaarde();
	}
	
	public void zetWaarde(double waarde) {
		slider.zetWaarde(waarde);
//		this.waarde = waarde;
//		int pixStand = (int) (lengte * (waarde - onderGrensWaarde)/(bovenGrensWaarde - onderGrensWaarde));
//		if(!actie)
//			slider.zetStand(pixStand);
	}
	
	
	/*
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == slider)
		{ 	waarde = geefDoubleStand();
			
		}
	}
	*/
	
}
