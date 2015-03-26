package fi.statistiekgwt.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import fi.statistiekgwt.client.StatInteractiePanel;
import fi.statistiekgwt.client.histogram.HistogramController;
import fi.statistiekgwt.client.text.Text_nl;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource.ClassName;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StatistiekGWT implements EntryPoint, InteractionStub
{
	private static final Logger logger = Logger.getLogger(ClassName.class.getName());
	
	public static Text_nl rb = new Text_nl();
	static final String holderId = "dockholder";
	static final String upgradeMessage = 
			"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	private Map<String, Object> launchState; 
	
//	LayoutPanel basisPanel;
	StatInteractiePanel basisPanel;

	public static NumberFormat nf = NumberFormat.getDecimalFormat(); // number format for the default locale

	public static String fontString = "12px sans-serif";
	public static String fontBoldString = "12px sans-serif bold";;
	public static int scrollSpeedUnit = 16;
	public static CssColor backgroundColor = CssColor.make(255, 255, 255);

	// Name all StatistiekViews here, and add them to the createView method
	public static String[] VIEWS;// = {"Table", "Histogram", "Dotplot",
								 // "Frequentietabel", "Frequentiepolygoon",
								 // "Boxplot", "Kruistabel", "Spreidingsdiagram",
								 // "Kengetallen"};
	public static String[] VIEWS_translated;// = {"Table", "Histogram",
											// "Dotplot", "Frequentietabel",
											// "Frequentiepolygoon", "Boxplot", "Crosstab",
											// "Scatterplot", "Descriptive statistics"};

	int breedte;
	int hoogte;
	boolean volledigeBreedte = false;
	private static int WIDTH_OFFSET = 5;
	private static int HEIGHT_OFFSET; 
	public static int BUTTON_HEIGHT = 40; 
	
	boolean nagekeken = false;

	private static String language;


	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
		+ "attempting to contact the server. Please check your network "
		+ "connection and try again.";

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		initViews();
		basisPanel = new StatInteractiePanel();
		
		// voeg statinteractiepanel toe
		//RootPanel.get(holderId).add(basisPanel);
		RootLayoutPanel.get().add(basisPanel);
	}
	
	/**
	 * Deze methode wordt aangeroepen na init()
	 */
	private void initialize()
	{
		StatistiekGWT.HEIGHT_OFFSET = (int) this.basisPanel.getBarHeight() + StatistiekGWT.BUTTON_HEIGHT;

		this.basisPanel.setWidth(breedte);
		this.basisPanel.setHeight(hoogte);
		this.basisPanel.setState(launchState); // in setState wordt bepaald of de component getoond kan worden in HTML5 
		this.basisPanel.setPixelSize(breedte, hoogte);
		
		if (!this.basisPanel.getStatModel().getStatTableModel().isHTML5Ready())
		{
			this.basisPanel.setHTML5Message();
		}
	}

	static void initViews()
	{
		VIEWS_translated = new String[9];
		VIEWS_translated[0] = StatistiekGWT.rb.getString("tableOption");
		VIEWS_translated[1] = StatistiekGWT.rb.getString("histogramOption");
		VIEWS_translated[2] = StatistiekGWT.rb.getString("dotplotOption");
		VIEWS_translated[3] = StatistiekGWT.rb.getString("frequencytableOption");
		VIEWS_translated[4] = StatistiekGWT.rb.getString("frequencypolygonOption");
		VIEWS_translated[5] = StatistiekGWT.rb.getString("boxplotOption");
		VIEWS_translated[6] = StatistiekGWT.rb.getString("crosstabOption");
		VIEWS_translated[7] = StatistiekGWT.rb.getString("scatterplotOption");
		VIEWS_translated[8] = StatistiekGWT.rb.getString("descriptivesOption");

		VIEWS = new String[9];
		VIEWS[0] = "Table";
		VIEWS[1] = "Histogram";
		VIEWS[2] = "Dotplot";
		VIEWS[3] = "Frequentietabel";
		VIEWS[4] = "Frequentiepolygoon";
		VIEWS[5] = "Boxplot";
		VIEWS[6] = "Kruistabel";
		VIEWS[7] = "Spreidingsdiagram";
		VIEWS[8] = "Kengetallen";
	}

	@Override
	public HashMap<String, Object> getState()
	{
//		HashMap<String,Object> h = new HashMap<String,Object>();
//		return h;
		
		// we hebben:
		// tableModel
		// selectionList
		// statistiekViewTypes
		// statistiekViewStates
		// selectedView

		return this.basisPanel.getState();
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		this.basisPanel.setState(h);
	}

	@Override
	public int getScore()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean isCorrect()
	{
		return true;
	}

	@Override
	public void kijkNa()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zetVolledigeBreedte(int breedte)
	{
		// TODO Auto-generated method stub
		
	}
	
	public void zetNagekeken(boolean b) 
	{
			nagekeken = b;
	}

	@Override
	public Widget asWidget()
	{
		return basisPanel;
	}

	@Override
	public int getAsHoogte()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight()
	{
		return hoogte;
	}

	@Override
	public int getWidth()
	{
		return breedte;
	}

	@Override
	public void setAsHoogte(int ashoogte)
	{
		// TODO Auto-generated method stub
		
	}

	/*
	 * Default zero argument constructor is required.
	 */
	public StatistiekGWT()
	{
		
	}
	
	public StatistiekGWT(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden, int volleBreedte)
	{	
		StatistiekGWT.language = "nl";
		initViews();
		basisPanel = new StatInteractiePanel();

		ObjectMap map = JSONUtilities.wrapMap(h);
	
		if(map != null)
		{
			if (map.containsKey("breedte"))
				breedte = map.getInt("breedte");
			if (map.containsKey("hoogte"))
				hoogte = map.getInt("hoogte");
			if (map.containsKey("volledigeBreedte"))
				volledigeBreedte = map.getBoolean("volledigeBreedte");
		}

		if (volledigeBreedte)
			breedte = volleBreedte;
	
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");

		//alle gegevens uit launchState halen: 
		init(breedte, hoogte, launchState, randomVarWaarden);
	}

	/**
	 * Initialize with the values in launch data.
	 */
	@Override
	public void init(int width, int height, Map<String, Object> launchDataMap,
		Map<String, Number> values)
	{
		breedte = width - 2 * WIDTH_OFFSET;
		hoogte = height;
		
		ObjectMap launchData = JSONUtilities.wrapMap(launchDataMap);
		launchState = launchDataMap;
		
		if (launchData != null)
		{
			// we hebben:
			// tableModel
			// selectionList
			// statistiekViewTypes
			// statistiekViewStates
			// selectedView
			// 
		}
		
		// in initialize() wordt de launchState in statInteractiePanel gezet
		this.initialize();
	}
	
	public static StatistiekView createView(String viewType, String viewName,
		StatTableModel model, int startVar, int startVar2,
		StatInteractiePanel statInteractiePanel)
	{
//		System.out.println("Statistiek.createView(viewType=" + viewType + ", viewName=" + viewName 
//			+ ", identityHashCode(statTableModel)=" + identityHashCode(model) + ")");

		StatistiekView view = null;
		int w = statInteractiePanel != null ? statInteractiePanel.getWidth() : 0;
		int h = statInteractiePanel != null ? Math.max(0, statInteractiePanel.getHeight() - HEIGHT_OFFSET) : 0;
		
		if (viewType.equals("Table"))
		{
			view = new StatTable(model, statInteractiePanel, viewName);
		}
		else if (viewType.equals("Histogram"))
		{
			view = new HistogramController(model, viewName, false, startVar, w, h);
		}
		else if (viewType.equals("Dotplot"))
		{
			//return new DotplotController(model, viewName, startVar);
		}
		else if (viewType.equals("Frequentietabel"))
		{
			//return new FrequencyTableController(model, viewName, startVar);
		}
		else if (viewType.equals("Frequentiepolygoon"))
		{
			return new HistogramController(model, viewName, true, startVar, w, h);
		}
		else if (viewType.equals("Boxplot"))
		{
			//return new BoxplotController(model, viewName, startVar);
		}
		else if (viewType.equals("Kruistabel"))
		{
//			System.out.println("Statistiek.createView(): viewName = " + viewName);
//			CrossTabulationTableController controller = new CrossTabulationTableController(model, viewName, startVar, startVar2);
//			// set the split variable (i.e., the column variable)
//			controller.setSplit(startVar2);
//			return controller;
		}
		else if (viewType.equals("Spreidingsdiagram"))
		{
			//System.out.println("Statistiek.createView(): viewName = " + viewName);
			//return new DotplotController(model, viewName, startVar, startVar2);
		}
		else if (viewType.equals("Kengetallen"))
		{
			//System.out.println("Statistiek.createView(): viewName = " + viewName);
			//return new DescriptivesController(model, viewName, startVar);
		}
		
		return view;
	}
	
	private void setHeightOffset()
	{
		StatistiekGWT.HEIGHT_OFFSET = (int) this.basisPanel.getBarHeight() + StatistiekGWT.BUTTON_HEIGHT;
	}

	/**
	 * get the top level ancestor of a JComponent This implementation differs
	 * from JComponent.getTopLevelAncestor because this doesn't stop at an
	 * applet
	 * 
	 * @param c
	 *            The component to get the top level ancestor of
	 * @return c's top level ancestor
	 */
	public static Widget getTopLevelAncestor(Widget widget)
	{
		Widget topParent = widget.getParent();
		while (topParent.getParent() != null)
		{
			topParent = topParent.getParent();
		}
		return topParent;
	}
	
	/**
	 * Determine appropriate bin boundaries from given min, max and number of
	 * bins.
	 * 
	 * @param min
	 *            The minimum value in the dataset
	 * @param max
	 *            The maximum value in the dataset
	 * @param noBins
	 *            The desired number of bins
	 * @return ArrayList containing appropriate bin boundaries
	 */
	public static ArrayList<Double> appropriateBoundaries(double min,
		double max, int noBins)
	{
		// calculate decimal bin boundaries smaller than 1 
		if (((Math.abs(min) < 1) && (Math.abs(max) < 1))
			|| (((max - min) < 1) && ((max - min) != 0)))
		{
			// determine the number of decimals of min and max
			String minString = String.valueOf(min);
			int decimalPlacesMin = StatistiekGWT.getNumberOfDecimals(minString);
			String maxString = String.valueOf(max);
			int decimalPlacesMax = StatistiekGWT.getNumberOfDecimals(maxString);
			int numberOfDecimals = Math.max(decimalPlacesMin, decimalPlacesMax);
			
			min = min * (Math.pow(10, numberOfDecimals)); 
			max = max * (Math.pow(10, numberOfDecimals));
			
			ArrayList<Double> binBoundaries = appropriateBoundaries(min, max, noBins);
			// divide each bin boundary by Math.pow(10, numberOfDecimals)
			for (int i = 0; i < binBoundaries.size(); i++)
			{
				// divide the bin boundary by Math.pow(10, numberOfDecimals)
				// and round to the correct number of decimals (because of possible rounding errors)
				double newValue = round(binBoundaries.get(i) / (Math.pow(10, numberOfDecimals)), numberOfDecimals);
				binBoundaries.set(i, newValue);
			}
			return binBoundaries; 
		}
		
		double b = (max - min) / (double) (Math.max(noBins - 1, 1));
		
		if ((Math.abs(min) > 1) || (Math.abs(max) > 1))
		{
			// neem integer waarde
			b = (int) b;
		}
		
		int e;
		if (noBins == 1)
		{
			e = (int) Math.ceil(Math.log10(b));
		}
		else
		{
			e = (int) Math.floor(Math.log10(b));
		}

		double step = Math.ceil(b * Math.pow(10, -e)) * Math.pow(10, e);
		
		if (step == 0)
			step++;

		// System.out.println("e = " + e);
		// System.out.println("step = " + step);

		double start;
		
		if (min == Math.round(min) || ((Math.abs(min) < 1) && (Math.abs(max) < 1)))
		{
			start = min;
		}
		else
		{
			start = (Math.ceil(min / step) - 1) * step;
		}

		// make sure step is not too large
		// use min value instead of start to be more constraining 
		if (step > 1)
		{
			while (min + step >= max)
			{
				step = Math.ceil(step / 2);
			}
		}
		
		// make sure the maximum value is covered by the bins
		while ((start + noBins * step) <= max)
		{
			step = increaseStep(step);
		}

		// build arraylist
		ArrayList<Double> boundaries = new ArrayList<Double>();
		// correct afronden op basis van decimalen in start en binWidth
		String startString = String.valueOf(start);
		int decimalPlacesStart = StatistiekGWT.getNumberOfDecimals(startString);
		String binWidthString = String.valueOf(step);
		int decimalPlacesBinWidth = StatistiekGWT.getNumberOfDecimals(binWidthString);
		int numberOfDecimals = Math.max(decimalPlacesStart, decimalPlacesBinWidth);
		for (int i = 0; i <= noBins; i++)
		{
			double d = start + (double) i * step;
			d = round(d, numberOfDecimals);
			boundaries.add(d);
		}
		return boundaries;
	}

	/**
	 * Determine appropriate bin boundaries from given min, max, bin width and 
	 * the minimum bin boundary, and determine the number of bins.
	 * If the bin boundaries cannot be calculated for the given parameters
	 * null is returned.
	 * 
	 * @param min
	 *            The minimum value in the dataset
	 * @param max
	 *            The maximum value in the dataset
	 * @param binWidth
	 *            The desired bin width
	 * @param minBoundary
	 *            The minimum bin boundary
	 * @return ArrayList containing appropriate bin boundaries. If the bin
	 * boundaries cannot be calculated from the given parameters, null is
	 * returned.
	 */
	public static ArrayList<Double> appropriateBoundariesFromBinSettings(
		double min, double max, double binWidth, double minBoundary)
	{
		// check if parameters are valid
		if ((binWidth <= 0) 
			|| ((binWidth > 1) && (binWidth > 2 * (max - min))) 
			|| (binWidth < (max - min)/50))
			return null;
		
		if ((minBoundary > min) 
			|| (minBoundary < (min - 1 - 0.5 * max))) // minus 1 to avoid problems in case of small and negative values of min and max
			return null;
		
		// calculate decimal bin boundaries smaller than 1 
		if (((Math.abs(min) < 1) && (Math.abs(max) < 1))
			|| (((max - min) < 1) && ((max - min) != 0)))
		{
			// determine the number of decimals of min and max
//			String minString = String.valueOf(min);
//			int decimalPlacesMin = StatistiekGWT.getNumberOfDecimals(minString);
//			String maxString = String.valueOf(max);
//			int decimalPlacesMax = StatistiekGWT.getNumberOfDecimals(maxString);
//			int numberOfDecimals = Math.max(decimalPlacesMin, decimalPlacesMax);
			
			// test syl: alternative... using number of decimals of start and binWidth
			double start;
			if (minBoundary <= min)
			{
				start = minBoundary;
			}
			else
			{
				start = min;
			}

			String startString = String.valueOf(start);
			int decimalPlacesStart = StatistiekGWT.getNumberOfDecimals(startString);
			String binWidthString = String.valueOf(binWidth);
			int decimalPlacesBinWidth = StatistiekGWT.getNumberOfDecimals(binWidthString);
			int numberOfDecimals = Math.max(decimalPlacesStart, decimalPlacesBinWidth);

			min = min * (Math.pow(10, numberOfDecimals)); 
			max = max * (Math.pow(10, numberOfDecimals));
			binWidth = binWidth * (Math.pow(10, numberOfDecimals));
			minBoundary = minBoundary * (Math.pow(10, numberOfDecimals));
			
			ArrayList<Double> binBoundaries = appropriateBoundariesFromBinSettings(min, max, binWidth, minBoundary);
			
			if (binBoundaries != null)
			{
				// divide each bin boundary by Math.pow(10, numberOfDecimals)
				for (int i = 0; i < binBoundaries.size(); i++)
				{
					// divide the bin boundary by Math.pow(10, numberOfDecimals)
					// and round to the correct number of decimals (because of possible rounding errors)
					double newValue = round(binBoundaries.get(i) / (Math.pow(10, numberOfDecimals)), numberOfDecimals);
					binBoundaries.set(i, newValue);
				}
			}
			return binBoundaries; 
		}
		
		double start;
		int noBins;
		
		if (minBoundary <= min)
		{
			start = minBoundary;
		}
		else
		{
			start = min;
		}
		
		if (binWidth == 0)
			binWidth++;
		
		// The maximum bin boundary should be larger than the maximum value
		// so (max + 1) to determine the number of bins
		noBins = (int) Math.ceil(((max + 1) - start)/binWidth);
		while (start + noBins * binWidth <= max)
		{
			noBins++;
		}

		// build arraylist
		ArrayList<Double> boundaries = new ArrayList<Double>();
		// correct afronden op basis van decimalen in start en binWidth
		String startString = String.valueOf(start);
		int decimalPlacesStart = StatistiekGWT.getNumberOfDecimals(startString);
		String binWidthString = String.valueOf(binWidth);
		int decimalPlacesBinWidth = StatistiekGWT.getNumberOfDecimals(binWidthString);
		int numberOfDecimals = Math.max(decimalPlacesStart, decimalPlacesBinWidth);
		
		for (int i = 0; i <= noBins; i++)
		{
			double d = start + (double) i * binWidth;
			d = round(d, numberOfDecimals);
			boundaries.add(d);
		}

//		System.out.println("Statistiek.appropriateBoundariesFromBinSettings(min = "
//			+ min + ", max = " + max + ", binWidth = " + binWidth + ", minBoundary = " 
//			+ minBoundary + "): " + boundaries);
		
		return boundaries;
	}

	/**
	 * Increase the step with a value that is reasonable taking
	 * into account the size of the step.
	 * 
	 * @param step
	 * @return
	 */
	private static double increaseStep(double step)
	{
		double newStep = step;
		boolean found = false;
		
		if (divisibleBy(step, 10)) // tiental
		{
			int count = 0;
			double result = step;
			
			while (divisibleBy(result, 10))
			{
				// deel door 10 tot niet meer mogelijk
				result = result / 10;
				count++;
			}
			
			if (result >= 3)
			{
				// bepaal de nieuwe step, bijv.
				// step = 30  -> newStep = 40
				// step = 300 -> newStep = 400
				newStep = step + Math.pow(10, count);
				found = true;
			}
		}
		
		if (!found)
		{
			if (divisibleBy(step, 5)) // vijftal (inclusief tiental < 30)
			{
				if (step / 5 > 1) // vijftal > 5
				{
					newStep = step + 5;
				}
				else
				{
					// step = 5 -> newStep = 6
					newStep = step + 1;
				}
			}
			else
			{
				newStep = step + 1;
			}
		}
		
		return newStep;
	}

	private static boolean divisibleBy(double step, int factor)
	{
		boolean divisible = false;
		
		if ((int) step % factor == 0)
			divisible = true;
		
		return divisible;
	}

	/**
	 * Rounds a number to a certain number of decimals
	 * 
	 * @param number
	 *            the number to round
	 * @param decimals
	 *            the number of decimals to round to. If negative, the number
	 *            will be rounded to zero decimals.
	 * @return the rounded number
	 */
	public static double round(double number, int decimals)
	{
//		number = number * (Math.pow(10, decimals));
//		
//		number = Math.round(number);
//		
//		number = number / (Math.pow(10, decimals));

	    if (decimals < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(number);
	    bd = bd.setScale(decimals, RoundingMode.HALF_UP);
	    number = bd.doubleValue(); 

		return number;
	}
	
	/**
	 * Get the string value of double. If the value is an integer value
	 * a string is returned without decimals.
	 * The decimal format related to the language is used,
	 * with the number of decimals of d.
	 *  
	 * @param d The double value
	 * @return The string value
	 */
	public static String getStringValue(double d)
	{
		String s;
		if ((d == Math.floor(d)) && !Double.isInfinite(d))
			s = String.valueOf((int) d);
		else
		{
			NumberFormat numberFormat = StatistiekGWT.getNumberFormat(d);
			s = numberFormat.format(d); 
			
			// gebruik van locale door numberformat wordt (nog) niet ondersteund
			// zie: http://stackoverflow.com/questions/9805941/how-to-specify-the-thousands-and-decimal-separator-used-by-gwts-numberformat
			if (language.equals("nl"))
			{
				s = s.replace('.', ',');
			}
		}
		
		return s;
	}
	
	/**
	 * Get the string value with the language dependent decimal separator.
	 *  
	 * @param s The string with the double value
	 * @return The string value
	 */
	public static String getStringValue(String s)
	{
		// gebruik van locale door numberformat wordt (nog) niet ondersteund
		// zie: http://stackoverflow.com/questions/9805941/how-to-specify-the-thousands-and-decimal-separator-used-by-gwts-numberformat
		if (language.equals("nl"))
		{
			s = s.replace('.', ',');
		}
		
		return s;
	}
	
//	/**
//	 * Get the string value of double. If the value is an integer value
//	 * a string is returned without decimals.
//	 * @param d The double value
//	 * @return The string value
//	 */
//	public static String getStringValue(double d)
//	{
//		String s;
//		if ((d == Math.floor(d)) && !Double.isInfinite(d))
//			s = String.valueOf((int) d);
//		else
//			s = String.valueOf(d);
//		
//		return s;
//	}


	
	/**
	 * Get the string value of double. If the value is an integer value
	 * a string is returned without decimals.
	 * The default decimal format related to language is used, with one decimal.
	 *  
	 * @param d The double value
	 * @return The string value
	 */
	public static String getStringValueWithOneDecimal(double d)
	{
		String s;
		if ((d == Math.floor(d)) && !Double.isInfinite(d))
		{
			s = String.valueOf((int) d);
		}
		else
		{
			String separator = StatistiekGWT.getDecimalSeparator();
			NumberFormat.getFormat("0" + separator + "#"); // if there are decimals, show one
			s = nf.format(d); // use the default decimal format for the correct decimal separator
		}
		
		return s;
	}
	
	/**
	 * Get the number format for the default locale.
	 */
	public static NumberFormat getDefaultNumberFormat()
	{
		return nf;
	}
	
	/**
	 * Get the number format with number of decimals of the given double.
	 * 
	 * @param d The double 
	 */
	public static NumberFormat getNumberFormat(Double d)
	{
		String value = String.valueOf(d);
		int numberOfDecimals = StatistiekGWT.getNumberOfDecimals(value);
//		String separator;
//		
//		if (StatistiekGWT.language.equals("nl"))
//			separator = ",";
//		else
//			separator = ".";

		String pattern = "0";
		
		if (numberOfDecimals > 0)
			pattern = pattern + ".";
		for (int i = 0; i < numberOfDecimals; i++)
		{
			pattern = pattern + "#"; 
		}
		NumberFormat numberFormat = NumberFormat.getFormat(pattern);

		return numberFormat;
	}
	
	/**
	 * Parse the double value in doubleString to double 
	 * using the locale language settings.
	 * 
	 * @param doubleString
	 * @return
	 */
	public static double parseDouble(String doubleString)
	{
		double d;
		
		d = nf.parse(doubleString);

		return d;
	}
	
	/**
	 * Get language of statistiek.
	 * 
	 * @return
	 */
	public static String getLanguage()
	{
		return StatistiekGWT.language;
	}
	
	/**
	 * Set language of statistiek.
	 * 
	 * @return
	 */
	public static void setLanguage(String l)
	{
		StatistiekGWT.language = l;
	}
	
	/**
	 * Get the number of decimals of the given double string.
	 * 
	 * @param d
	 * @return
	 */
	public static int getNumberOfDecimals(String doubleString)
	{
		int decimalPlaces = 0;
		boolean isScientificNotation = doubleString.indexOf("E") > -1;
		
		if (!isScientificNotation)
		{
			int integerPlaces = doubleString.indexOf('.');
			if (integerPlaces > -1)
			{
				decimalPlaces = doubleString.length() - integerPlaces - 1;
			}
		}
		else
		{
			// met regular expressions
			String patternString = "(?:\\.(\\d+))?(?:[eE]([+-]?\\d+))";
			
			RegExp regExp = RegExp.compile(patternString);
			MatchResult matcher = regExp.exec(doubleString);
			boolean matchFound = matcher != null; // equivalent to regExp.test(inputStr); 
			if (matchFound)
			{
				// doubleString heeft een goed formaat
				decimalPlaces = 
					// Number of digits right of decimal point.
				    (((matcher.getGroup(1) != null) && Integer.valueOf(matcher.getGroup(1).toString()) != 0) 
				    	? matcher.getGroup(1).length() : 0)
				    // Adjust for scientific notation.
				    - (matcher.getGroup(2) != null ? Integer.valueOf(matcher.getGroup(2).toString()) : 0);
			}
		}
		
		return decimalPlaces;
	}
	
	/**
	 * Get the binWidth based on the values in bins. The number of 
	 * decimals of d will be the maximum number of decimals
	 * among the values in bins.
	 * 
	 * @param bins An arraylist of bin boundaries
	 * @return The string value of the bin width
	 */
	public static String getFormattedBinWidth(ArrayList<Double> bins)
	{
		String formattedValueString;
		int maxNumberOfDecimals = 0;
		String binValueString;
		
		Double d = bins.get(1) - bins.get(0);
		
		for (int i = 0; i < bins.size(); i++)
		{
			binValueString = String.valueOf(bins.get(i));
			int numberOfDecimals = StatistiekGWT.getNumberOfDecimals(binValueString);
			
			if (numberOfDecimals > maxNumberOfDecimals)
				maxNumberOfDecimals = numberOfDecimals;
		}

		// get format with numberOfDecimals
		String separator = StatistiekGWT.getDecimalSeparator();
		String pattern = "0";
		
		if (maxNumberOfDecimals > 0)
		{
			// set the pattern according to the number of decimals
			pattern = pattern + ".";
			for (int i = 0; i < maxNumberOfDecimals; i++)
			{
				pattern = pattern + "#";
			}
		}
		NumberFormat nf = NumberFormat.getFormat(pattern);
		formattedValueString = nf.format(d.doubleValue());
		
		if (separator.equals(","))
			formattedValueString = formattedValueString.replace('.', ',');
		
		return formattedValueString;
	}
	
	private static String getDecimalSeparator()
	{
		String separator = ".";
		if (StatistiekGWT.language.equals("nl"))
		{
			separator = ",";
		}
		
		return separator;
	}
}
