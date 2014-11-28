package fi.statistiekgwt.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import fi.statistiekgwt.client.StatInteractiePanel;
import fi.statistiekgwt.client.histogram.HistogramController;
import fi.statistiekgwt.client.text.Text_nl;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource.ClassName;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
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
	
	private HashMap<String, Object> launchState; 
	
	LayoutPanel basisPanel;

	public static NumberFormat df = NumberFormat.getDecimalFormat();
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

	
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
		+ "attempting to contact the server. Please check your network "
		+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
		.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		init();
		
		// voeg statinteractiepanel toe
//		RootPanel.get(holderId).add(basisPanel);
		RootLayoutPanel.get().add(basisPanel);
	}
	
	private void init()
	{
		initViews();
		basisPanel = new StatInteractiePanel();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		// TODO Auto-generated method stub
		
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

	@Override
	public Widget asWidget()
	{
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWidth()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAsHoogte(int ashoogte)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(int width, int height, Map<String, Object> launchData,
		Map<String, Number> values)
	{
		// TODO Auto-generated method stub
		
	}
	
	public static StatistiekView createView(String viewType, String viewName,
		StatTableModel model, int startVar, int startVar2,
		StatInteractiePanel statInteractiePanel)
	{
//		System.out.println("Statistiek.createView(viewType=" + viewType + ", viewName=" + viewName 
//			+ ", identityHashCode(statTableModel)=" + identityHashCode(model) + ")");

		if (viewType.equals("Table"))
		{
			return new StatTable(model, statInteractiePanel, viewName);
		}
		else if (viewType.equals("Histogram"))
		{
			return new HistogramController(model, viewName, false, startVar);
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
			//return new HistogramController(model, viewName, true, startVar);
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
		else
		{
			//return null;
		}
		return null;
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
	 * Get the string value of double. If the value is an integer value
	 * a string is returned without decimals.
	 * @param d The double value
	 * @return The string value
	 */
	public static String getStringValue(double d)
	{
		String s;
		if ((d == Math.floor(d)) && !Double.isInfinite(d))
			s = String.valueOf((int) d);
		else
			s = String.valueOf(d);
		
		return s;
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
		if ((Math.abs(min) < 1) && (Math.abs(max) < 1)
			|| ((max - min) < 1))
		{
			// determine the number of decimals of min and max
			String minString = String.valueOf(min);
			int decimalPlacesMin = minString.length() - minString.indexOf('.') - 1;
			String maxString = String.valueOf(max);
			int decimalPlacesMax = maxString.length() - maxString.indexOf('.') - 1;
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
		// test syl: voor lengte min 156, max 171 en noBins 2 wordt step 20 i.p.v. 10
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
		while (min + step >= max)
		{
			step = Math.ceil(step / 2);
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
		int decimalPlacesStart = startString.length() - startString.indexOf('.') - 1;
		String binWidthString = String.valueOf(step);
		int decimalPlacesBinWidth = binWidthString.length() - binWidthString.indexOf('.') - 1;
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
		if ((binWidth <= 0) || (binWidth > 2 * (max - min)) 
			|| (binWidth < (max - min)/50))
			return null;
		
		if ((minBoundary > min) || (minBoundary < (min - 0.5 * max)))
			return null;
		
		// calculate decimal bin boundaries smaller than 1 
		if ((Math.abs(min) < 1) && (Math.abs(max) < 1))
		{
			// determine the number of decimals of min and max
			String minString = String.valueOf(min);
			int decimalPlacesMin = minString.length() - minString.indexOf('.') - 1;
			String maxString = String.valueOf(max);
			int decimalPlacesMax = maxString.length() - maxString.indexOf('.') - 1;
			int numberOfDecimals = Math.max(decimalPlacesMin, decimalPlacesMax);
			
			min = min * (Math.pow(10, numberOfDecimals)); 
			max = max * (Math.pow(10, numberOfDecimals));
			binWidth = binWidth * (Math.pow(10, numberOfDecimals));
			minBoundary = minBoundary * (Math.pow(10, numberOfDecimals));
			
			ArrayList<Double> binBoundaries = appropriateBoundariesFromBinSettings(min, max, binWidth, minBoundary);
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
		int decimalPlacesStart = startString.length() - startString.indexOf('.') - 1;
		String binWidthString = String.valueOf(binWidth);
		int decimalPlacesBinWidth = binWidthString.length() - binWidthString.indexOf('.') - 1;
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
}
