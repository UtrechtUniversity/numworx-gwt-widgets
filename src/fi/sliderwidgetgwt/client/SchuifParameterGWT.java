package fi.sliderwidgetgwt.client;

public class SchuifParameterGWT
{
	private String naam;
	private double waarde = 0;
	private double onderGrensWaarde, bovenGrensWaarde, stapGrootte;
	private SliderGWT slider;
	private int lengte;
//	private int x, y;

	/**
	 * Class voor de parameters van de slider widget en de slider zelf.
	 * 
	 * @param aantalPix
	 * @param naam
	 */
	public SchuifParameterGWT(int aantalPix, String naam)
	{
		lengte = aantalPix;
		slider = new SliderGWT(lengte, 0);
		slider.zetMinimum(0);
		this.naam = naam;
		onderGrensWaarde = 0;
		bovenGrensWaarde = 5;
		stapGrootte = 0.1;
//		x = 5;
//		y = 5;
		slider.zetNaam(naam);
		slider.zetGrenzen(onderGrensWaarde, bovenGrensWaarde);
	}

	public void zetGrensWaarden(double onderGrens, double bovenGrens)
	{
		onderGrensWaarde = onderGrens;
		bovenGrensWaarde = bovenGrens;
		slider.zetGrenzen(onderGrensWaarde, bovenGrensWaarde);
	}

	public double geefOnderGrens()
	{
		return onderGrensWaarde;
	}

	public double geefBovenGrens()
	{
		return bovenGrensWaarde;
	}

	public double geefStapGrootte()
	{
		return stapGrootte;
	}

	public String geefNaam()
	{
		return naam;
	}

	public void zetNaam(String naam)
	{
		this.naam = naam;
		slider.zetNaam(naam);
	}

	public void zetLengte(int lengte)
	{
		this.lengte = lengte;
		slider.zetLengte(lengte);
	}

//	public void zetLocatie(int x, int y)
//	{
//		this.x = x;
//		this.y = y;
//	}
//
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
		this.stapGrootte = stapGrootte;
		slider.zetStapGrootte(stapGrootte);
	}

	/**
	 *  geefDoubleStand() leidt de waarde van de parameter af uit de stand in
	 *  pixels zoals die door de slider wordt teruggegeven.
	 * @return
	 */
	public double geefDoubleStand()
	{
		double pixStand = slider.geefStand();
		double doubleLengte = lengte;
		double stand = pixStand / doubleLengte * (bovenGrensWaarde - onderGrensWaarde) + onderGrensWaarde;
		
		// nu nog afronden met behulp van stapgrootte.
		int aantalStappen = (int) ((bovenGrensWaarde - onderGrensWaarde) / stapGrootte);
		for (int i = 0; i < aantalStappen; i++)
		{
			if (stand - onderGrensWaarde < i * stapGrootte + stapGrootte / 2)
			{
				stand = onderGrensWaarde + i * stapGrootte;
				break;
			}
		}
		
		if (stand - onderGrensWaarde > (aantalStappen - 1) * stapGrootte + stapGrootte / 2)
		{
			stand = bovenGrensWaarde;
		}
		
		return stand;
	}

	public int geefLengte()
	{
		return lengte;
	}

	public double geefWaarde()
	{
		return waarde;
	}

	public void zetWaarde(double waarde, boolean actie)
	{
		this.waarde = waarde;
		int pixStand = (int) (lengte * (waarde - onderGrensWaarde) / (bovenGrensWaarde - onderGrensWaarde));
		if (!actie)
		{
			slider.zetStand(pixStand);
		}
	}

	public SliderGWT geefSlider()
	{
		return slider;
	}

	public void paint()
	{
		slider.paintComponent();
	}

}
