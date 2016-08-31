package fi.statistiekgwt.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import fi.statistiekgwt.client.StatInteractiePanel;
import fi.statistiekgwt.client.boxplot.BoxplotController;
import fi.statistiekgwt.client.crosstabulationtable.CrossTabulationTableController;
import fi.statistiekgwt.client.descriptives.DescriptivesController;
import fi.statistiekgwt.client.dotplot.DotplotController;
import fi.statistiekgwt.client.frequencytable.FrequencyTableController;
import fi.statistiekgwt.client.histogram.HistogramController;
import fi.statistiekgwt.client.text.Text;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StatistiekGWT implements EntryPoint, InteractionStub
{
	private static final Logger logger = Logger.getLogger(StatistiekGWT.class.getName());

	OpdrNavIF comRoot;

	public static final String TEXT_CSV = "text.csv";
	public static final String NL = "nl";

	public static Text rb;
	static final String holderId = "dockholder";
	static final String upgradeMessage = 
			"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
	
	private Map<String, Object> launchState; 
	
	StatInteractiePanel basisPanel;
	SimplePanel simpel = new SimplePanel();

	public static NumberFormat nf = NumberFormat.getDecimalFormat(); // number format for the default locale

	public static String fontString = "12px sans-serif";
	public static String fontBoldString = "12px sans-serif bold";
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

	public static int DEFAULT_WIDTH = 1000;
	public static int DEFAULT_HEIGHT = 400;
	int breedte = StatistiekGWT.DEFAULT_WIDTH;
	int hoogte = StatistiekGWT.DEFAULT_HEIGHT;
	boolean volledigeBreedte = false;
	private static int WIDTH_OFFSET = 5;
	private static int heightOffset; 
	public static int BUTTON_HEIGHT = 30;//40;
	public static int TABLE_BUTTON_MARGIN = 1;
	public static int TABLE_BUTTON_PADDING = 3;
	public static double BIN_WIDTH_DEFAULT = 1;
	
	boolean nagekeken = false;

	private static String language;

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
		+ "attempting to contact the server. Please check your network "
		+ "connection and try again.";

	public static final int ASCENDING = 0;
	public static final int DESCENDING = 1;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		// prevent the browser's default context menu
		Element body = Document.get().getBody();
		//body.setAttribute("oncontextmenu", "return false;");
		
		GWT.setUncaughtExceptionHandler(
			new GWT.UncaughtExceptionHandler() {
		        public void onUncaughtException(Throwable e) 
		        {
		        	logger.log(Level.SEVERE, e.getMessage(), e);
		        	Window.alert("UncaughtException: message = " + e.getMessage() + ", stackTrace = " + e.getStackTrace().toString()
		        		+ ", cause = " + e.getCause());
		        }
		    });
		
		rb = GWT.create(Text.class);
		StatistiekGWT.language = rb.language();
		
		initViews();
		basisPanel = new StatInteractiePanel();
		basisPanel.statistiekGWT = this; // backlink
		StatistiekGWT.heightOffset = (int) this.basisPanel.getBarHeight();
		this.basisPanel.setWidth(breedte);
		this.basisPanel.setHeight(hoogte);
		basisPanel.setPixelSize(breedte, hoogte);
		
		// voeg statinteractiepanel toe
		//RootPanel.get(holderId).add(basisPanel);
		RootLayoutPanel.get().add(simpel);
		
		//simpel.setWidget(asWidget()); // deze regel aanzetten voor standalone test
		Stub.publish(this); // deze regel uitzetten voor standalone test
	}
	
	/**
	 * Deze methode wordt aangeroepen na init()
	 */
	private void initialize()
	{
		StatistiekGWT.heightOffset = (int) this.basisPanel.getBarHeight();

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
		VIEWS_translated[0] = StatistiekGWT.rb.tableOption();
		VIEWS_translated[1] = StatistiekGWT.rb.histogramOption();
		VIEWS_translated[2] = StatistiekGWT.rb.dotplotOption();
		VIEWS_translated[3] = StatistiekGWT.rb.frequencytableOption();
		VIEWS_translated[4] = StatistiekGWT.rb.frequencypolygonOption();
		VIEWS_translated[5] = StatistiekGWT.rb.boxplotOption();
		VIEWS_translated[6] = StatistiekGWT.rb.crosstabOption();
		VIEWS_translated[7] = StatistiekGWT.rb.scatterplotOption();
		VIEWS_translated[8] = StatistiekGWT.rb.descriptivesOption();

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
		
		// sort the arrays
//		Arrays.sort(VIEWS_translated);
//		Arrays.sort(VIEWS);
	}

	@Override
	public HashMap<String, Object> getState()
	{
		return this.basisPanel.getState();
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		if(h == null || h.isEmpty()) return;
		this.basisPanel.setState(h);
	}

	@Override
	public int getScore()
	{
		return 0;
	}
	
	public int[][] getScoreObjectives()
	{
		return null;
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
		this.comRoot = comRoot;
		
		comRoot.addCBookEventListener(TEXT_CSV, this.basisPanel);
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
		rb = GWT.create(Text.class);
		StatistiekGWT.language = rb.language();
		initViews();
	}
	
	public StatistiekGWT(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden, int volleBreedte)
	{	
		rb = GWT.create(Text.class);
		StatistiekGWT.language = rb.language();
		initViews();
		basisPanel = new StatInteractiePanel();
		basisPanel.statistiekGWT = this; // backlink

		ObjectMap map = JSONUtilities.wrapMap(h);
	
		if (map != null)
		{
			if (map.containsKey("breedte"))
			{
				breedte = map.getInt("breedte");
			}
			if (map.containsKey("hoogte"))
			{
				hoogte = map.getInt("hoogte");
			}
			if (map.containsKey("volledigeBreedte"))
			{
				volledigeBreedte = map.getBoolean("volledigeBreedte");
			}
		}

		if (volledigeBreedte)
		{
			breedte = volleBreedte;
		}
	
		if (h != null && h.get("interactiePanelLaunchState") != null)
		{
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		}

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
		
		launchState = launchDataMap;
		
		// in initialize() wordt de launchState in statInteractiePanel gezet
		this.initialize();
		
		simpel.setWidget(asWidget());
	}
	
	public static StatistiekView createView(String viewType, String viewName,
		StatTableModel model, int startVar, int startVar2,
		StatInteractiePanel statInteractiePanel)
	{
		StatistiekView view = null;
		int w = statInteractiePanel != null ? statInteractiePanel.getWidth() : 0;
		int h = statInteractiePanel != null ? Math.max(0, statInteractiePanel.getHeight() - StatistiekGWT.heightOffset) : 0;
		
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
			view = new DotplotController(model, viewName, startVar, w, h);
		}
		else if (viewType.equals("Frequentietabel"))
		{
			return new FrequencyTableController(model, viewName, startVar, w, h);
		}
		else if (viewType.equals("Frequentiepolygoon"))
		{
			view = new HistogramController(model, viewName, true, startVar, w, h);
		}
		else if (viewType.equals("Boxplot"))
		{
			view = new BoxplotController(model, viewName, startVar, w, h);
		}
		else if (viewType.equals("Kruistabel"))
		{
			CrossTabulationTableController controller = new CrossTabulationTableController(
				model, viewName, startVar, startVar2, w, h);
			// set the split variable (i.e., the column variable)
			controller.setSplit(startVar2);
			view = controller;
		}
		else if (viewType.equals("Spreidingsdiagram"))
		{
			view = new DotplotController(model, viewName, startVar, startVar2, w, h);
		}
		else if (viewType.equals("Kengetallen"))
		{
			view = new DescriptivesController(model, viewName, startVar, w, h);
		}
		
		return view;
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
		ArrayList<Double> boundaries;
		
		if ((min == 0) && (max == 0))
		{
			boundaries = new ArrayList<Double>();
			boundaries.add(0.0);
			boundaries.add(0.0);
		}
		else
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
			
			int eValue;
			if (noBins == 1)
			{
				eValue = (int) Math.ceil(Math.log10(b));
			}
			else
			{
				eValue = (int) Math.floor(Math.log10(b));
			}
	
			// test syl
	//		boolean isInfinite = Double.isInfinite(eValue);
	//		double firstTerm = Math.ceil(b * Math.pow((double) 10, -eValue));
	////		double pow = Math.pow((double) 10, (double) -e); // in gecompileerde versie geeft dit geen 0, maar infinity
	//		double pow = Math.pow(10.0, (double) -eValue); // in gecompileerde versie geeft dit geen 0, maar infinity
	//		double secondTerm = Math.pow((double) 10, (double) eValue);
	
	//		Window.alert("apprBoundaries(" + min + "," + max + "," + noBins
	//			+ "): b = " + b + ", e = " + eValue + ", pow = " + pow
	//			+ ", firstTerm = " + firstTerm
	//			+ ", secondTerm = " + secondTerm 
	//			+ ", isInfinite = " + isInfinite);
			
			double step = Math.ceil(b * Math.pow(10, -eValue)) * Math.pow(10, eValue);
			
			if (Double.isNaN(step))
			{
				step = 0;
			}
			
			if (step == 0)
			{
				step++;
			}
	
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
			boundaries = new ArrayList<Double>();
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
		ArrayList<Double> boundaries;

		if ((min == 0) && (max == 0))
		{
			boundaries = new ArrayList<Double>();
			boundaries.add(0.0);
			boundaries.add(0.0);
		}
		else
		{

			// check if parameters are valid
			if ((binWidth <= 0) 
				&& (max != min)) // if max = min binwidth is not restricted
			{
				return null;
			}

			// calculate decimal bin boundaries smaller than 1 
			if (((Math.abs(min) < 1) && (Math.abs(max) < 1))
				|| (((max - min) < 1) && ((max - min) != 0)))
			{
				// use number of decimals of start and binWidth
				double start;
//				if (minBoundary <= min)
//				{
					start = minBoundary;
//				}
//				else
//				{
//					start = min;
//				}
	
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
			
			start = minBoundary;
			
			if (binWidth == 0)
			{
				binWidth++;
			}
			
			// The maximum bin boundary should be larger than the maximum value
			// so (max + 1) to determine the number of bins
			noBins = (int) Math.ceil(((max) - start)/binWidth); // voor decimale getallen geeft dit een bin teveel
			while (start + noBins * binWidth <= max)
			{
				noBins++;
			}
	
			// build arraylist
			boundaries = new ArrayList<Double>();
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
		}
	
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
		{
			divisible = true;
		}
		
		return divisible;
	}
	
	/**
	 * Get the formatted value given the decimal count.
	 * 
	 * @param value
	 * @param decimalCount
	 * @return
	 */
	public static String getFormatted(double value, int decimalCount)
	{
		StringBuilder numberPattern = new StringBuilder(
			(decimalCount <= 0) ? "" : ".");
		for (int i = 0; i < decimalCount; i++)
		{
			numberPattern.append('0');
		}
		return NumberFormat.getFormat(numberPattern.toString()).format(value);
	}
	
	/**
	 * Get the formatted value given the decimal count.
	 * 
	 * @param value
	 * @param decimalCount
	 * @return
	 */
	public static String getFormatted(String value, int decimalCount)
	{
		StringBuilder numberPattern = new StringBuilder(
			(decimalCount <= 0) ? "" : "0.");
		for (int i = 0; i < decimalCount; i++)
		{
			numberPattern.append('0');
		}
		NumberFormat numberFormat = NumberFormat.getFormat(numberPattern.toString());
		if (language.equals(NL))
		{
			value = value.replace('.', ',');
		}
		double doubleValue = numberFormat.parse(value); // voor nl wordt 59.0 -> 590.0, in unittest: voor nl parse(51,857142857142854) = 5.1857142857142856E16 ipv 51.857142857142854
		
		return numberFormat.format(doubleValue);// in unittest wordt 5.1857142857142856E16 51857142857142856.00
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
	    if (decimals < 0)
	    {
	    	throw new IllegalArgumentException();
	    }
	    if (Double.isNaN(number) || Double.isInfinite(number))
	    {
	    	return 0;
	    }

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
		{
			s = String.valueOf((long)Math.floor(d));
		}
		else
		{
			NumberFormat numberFormat = StatistiekGWT.getNumberFormat(d);
			s = numberFormat.format(d); 
			
			// gebruik van locale door numberformat wordt (nog) niet ondersteund
			// zie: http://stackoverflow.com/questions/9805941/how-to-specify-the-thousands-and-decimal-separator-used-by-gwts-numberformat
			if (language.equals(NL))
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
		if (language.equals(NL))
		{
			s = s.replace('.', ',');
		}
		
		return s;
	}
	
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
			char separator = StatistiekGWT.getDecimalSeparatorChar();
			NumberFormat numberFormat = NumberFormat.getFormat("0.#");//("0" + separator + "#"); // if there are decimals, show one
			s = numberFormat.format(d).replace('.', separator); // use the default decimal format for the correct decimal separator
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
		String pattern = "0";
		
		if (numberOfDecimals > 0)
		{
			pattern = pattern + ".";
		}
		
		for (int i = 0; i < numberOfDecimals; i++)
		{
			pattern = pattern + "#"; 
		}
		NumberFormat numberFormat = NumberFormat.getFormat(pattern);

		return numberFormat;
	}
	
	/**
	 * Parse the double value in doubleString to double. 
	 * 
	 * @param doubleString
	 * @return
	 */
	public static double parseDouble(String doubleString)
	{
		double d;
		
		// let op: deze houdt geen rekening met de nl "," separator!
		int numberOfDecimals = getNumberOfDecimals(doubleString);
		
		//d = nf.parse(doubleString);
		String formattedString = getFormatted(doubleString, numberOfDecimals);
		if (language.equals(NL))
		{
			formattedString = formattedString.replace(',', '.');
		}
		
		d = Double.parseDouble(formattedString);
		
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
				    (((matcher.getGroup(1) != null) && getDefaultNumberFormat().parse(matcher.getGroup(1).toString()) != 0) 
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
			{
				maxNumberOfDecimals = numberOfDecimals;
			}
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
		{
			formattedValueString = formattedValueString.replace('.', ',');
		}
		
		return formattedValueString;
	}
	
	private static String getDecimalSeparator()
	{
		String separator = ".";
		if (StatistiekGWT.language.equals(NL))
		{
			separator = ",";
		}
		
		return separator;
	}
	
	private static char getDecimalSeparatorChar()
	{
		char separator = '.';
		if (StatistiekGWT.language.equals(NL))
		{
			separator = ',';
		}
		
		return separator;
	}

	/**
	 * Get the bin boundaries that are from minOnScale to maxOnScale. If minOnScale is
	 * smaller than the smallest bin in binBoundaries, then bin boundaries are added 
	 * such that the new smallest bin boundary is equal to or smaller than minOnScale.
	 * If maxOnScale is larger than the largest bin in binBoundaries, then bin boundaries
	 * are added such that the new largest bin boundary is equal to or larger than 
	 * maxOnScale.
	 * 
	 * @param binBoundaries
	 * @param minOnScale
	 * @param maxOnScale
	 * @return
	 */
	public static ArrayList<Double> getBinBoundariesFromScaleSettings(
		ArrayList<Double> binBoundaries, double minOnScale, double maxOnScale)
	{
		ArrayList<Double> bins = new ArrayList<Double>();

		if (binBoundaries != null && binBoundaries.size() > 1) // valid bin boundaries
		{
			Double smallest = determineNewSmallestBinBoundary(binBoundaries, minOnScale);
			Double largest = determineNewLargestBinBoundary(binBoundaries, maxOnScale);
			
			int bin0Decimals = StatistiekGWT.getNumberOfDecimals(binBoundaries.get(0).toString());
			int bin1Decimals = StatistiekGWT.getNumberOfDecimals(binBoundaries.get(1).toString());
			int maxNumberOfDecimals = Math.max(bin0Decimals, bin1Decimals);

			Double binWidth = StatistiekGWT.round(binBoundaries.get(1) - binBoundaries.get(0), maxNumberOfDecimals);

			if (binWidth == 0)
			{
				binWidth = maxOnScale - minOnScale;
			}
			
			if (smallest.compareTo(largest) == 0)
			{
				bins.add(smallest);
				bins.add(largest);
			}
			else
			{
				Double binValue = smallest;
				int binValueDecimals = StatistiekGWT.getNumberOfDecimals(binValue.toString());
				maxNumberOfDecimals = Math.max(maxNumberOfDecimals, binValueDecimals);

				bins.add(binValue); // add the first value

				while (binValue < largest)
				{
					binValue = StatistiekGWT.round(binValue + binWidth, maxNumberOfDecimals);
					bins.add(binValue);
				}
			}
		}
		else
		{
			bins = binBoundaries;
		}
		
		return bins;
	}

	/**
	 * Determine the 
	 * 
	 * @param binBoundaries
	 * @param minOnScale
	 * @return
	 */
	private static Double determineNewSmallestBinBoundary(
		ArrayList<Double> binBoundaries, double minOnScale)
	{
		Double newSmallestBin = null;
		
		if (binBoundaries != null && binBoundaries.size() > 1) // valid bin boundaries
		{
			newSmallestBin = binBoundaries.get(0);
			Double binWidth = binBoundaries.get(1) - binBoundaries.get(0); // first bin value
			
			if (binWidth == 0)
			{
				newSmallestBin = minOnScale;
			}
			else
			{				
				if (minOnScale <= newSmallestBin)
				{
					// loop until bin value is found smaller than minOnScale
					while (minOnScale < newSmallestBin)
					{
						newSmallestBin = newSmallestBin - binWidth;
					}
				}
				else
				{
					// loop until bin value is found larger than minOnScale
					while (minOnScale >= newSmallestBin)
					{
						newSmallestBin = newSmallestBin + binWidth;
					}
					
					newSmallestBin = newSmallestBin - binWidth;
				}
			}
		}
		
		return newSmallestBin;
	}
	
	/**
	 * Determine the 
	 * 
	 * @param binBoundaries
	 * @param maxOnScale
	 * @return
	 */
	private static Double determineNewLargestBinBoundary(
		ArrayList<Double> binBoundaries, double maxOnScale)
	{
		Double newLargestBin = null;
		
		if (binBoundaries != null && binBoundaries.size() > 1) // valid bin boundaries
		{
			newLargestBin = binBoundaries.get(binBoundaries.size() - 1); // last bin value
			Double binWidth = binBoundaries.get(1) - binBoundaries.get(0);
			
			if (binWidth == 0)
			{
				newLargestBin = maxOnScale;
			}
			else
			{
				if (maxOnScale >= binBoundaries.get(binBoundaries.size() - 1))
				{
					// loop until bin value is found larger than maxOnScale
					while (maxOnScale > newLargestBin)
					{
						newLargestBin = newLargestBin + binWidth;
					}
				}
				else
				{
					// loop until bin value is found smaller than maxOnScale
					while (maxOnScale < newLargestBin)
					{
						newLargestBin = newLargestBin - binWidth;
					}
				}
			}
		}

		return newLargestBin;
	}

	/**
	 * Get bin boundaries based on the given minimum, maximum and bin width.
	 * 
	 * @param minimum
	 * @param maximum
	 * @param binWidth
	 * @return
	 */
	public static ArrayList<Double> getBinBoundariesFromScaleSettings(
		double minimum, double maximum, double binWidth)
	{
		ArrayList<Double> bins = new ArrayList<Double>();

		if (binWidth == 0)
		{
			binWidth = maximum - minimum;
		}
		
		if (minimum == maximum)
		{
			bins.add(minimum);
			bins.add(maximum);
		}
		else
		{
			Double binValue = minimum;
			bins.add(binValue); // add the first value

			while (binValue < maximum)
			{
				binValue = binValue + binWidth;
				bins.add(binValue);
			}
		}
		
		return bins;
	}
	
	public void fire(String command, String key, Object data) {
		if(comRoot != null) {
			Map<String, Object> map = Collections.singletonMap(key, data);
			CBookEvent event = new CBookEvent(this, command, map);
			comRoot.fireEvent(event);
		}
	}

}
