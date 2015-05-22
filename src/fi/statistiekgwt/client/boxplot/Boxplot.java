package fi.statistiekgwt.client.boxplot;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

import fi.statistiekgwt.client.ColorGenerator;
import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;
import fi.statistiekgwt.client.histogram.HistogramView;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * Paints a boxplot, i.e., including the axes and split.
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class Boxplot
{
	private boolean drawable;
	private Canvas canvas;
	private BoxplotMouseMoveHandler mouseMoveHandler;
	
	private int width;
	private int height;

	private BoxplotView boxplotView;
	private String columnName;

	/**
	 * An array of the minimum values for the data of the boxplot
	 * for each split class.
	 */
	private ArrayList<Double> minValues;
	/**
	 * An array of the lower quartile values for the data of the boxplot
	 * for each split class.
	 */
	private ArrayList<Double> lowerQuartiles;
	/**
	 * An array of the median values for the data of the boxplot
	 * for each split class.
	 */
	private ArrayList<Double> medians;
	/**
	 * An array of the upper quartile values for the data of the boxplot
	 * for each split class.
	 */
	private ArrayList<Double> upperQuartiles;
	/**
	 * An array of the maximum values for the data of the boxplot
	 * for each split class.
	 */
	private ArrayList<Double> maxValues;

	/**
	 * The minimum value in the data set.
	 */
	private double dataMinValue;
	/**
	 * The maximum value in the data set.
	 */
	private double dataMaxValue;
	
	private double firstMarker;
	private int step;
	private double max;
	/**
	 * The width of a single boxplot, i.e., 
	 * in case of a split the width between the split class labels.
	 */
	private double singleBoxAreaWidth;
	private ColumnType cType;
	private AllowedTypes type;
	private boolean normalFit;

	private boolean verticalBoxplots;
	
	/**
	 * Array of booleans 'is highlighted y/n' for the minimum value of each split class.
	 */
	private ArrayList<Boolean> highlightMinValues;
	/**
	 * Array of booleans 'is highlighted y/n' for the lower quartile value of each split class.
	 */
	private ArrayList<Boolean> highlightLowerQuartiles;
	/**
	 * Array of booleans 'is highlighted y/n' for the median value of each split class.
	 */
	private ArrayList<Boolean> highlightMedians;
	/**
	 * Array of booleans 'is highlighted y/n' for the upper quartile value of each split class.
	 */
	private ArrayList<Boolean> highlightUpperQuartiles;
	/**
	 * Array of booleans 'is highlighted y/n' for the maximum value of each split class.
	 */
	private ArrayList<Boolean> highlightMaxValues;
	
	private int independentAxisWidth = 50;
	private int independentAxisHeight = 50;
	private int dependentAxisWidth = 50;
	private int dependentAxisHeight = 50;

	private static final double FONT_HEIGHT = 20;

	public static double WIDTH_FILL_FRACTION = 0.8;
	public static double FILL_FRACTION = 0.8;
	public static final CssColor BOX_COLOR = ColorGenerator.DEFAULT_VIEW_ELEMENT_COLOR;
	public static final int MAX_BOX_HEIGHT = 40;
	public static final int AXIS_OFFSET = 25;
	public static final int AXIS_LABEL_OFFSET = 10;
	public static final int SMALL_MARKER_LENGTH = 5;
	
	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;

	public Boxplot(BoxplotView boxplotView)
	{
		this.boxplotView = boxplotView;
		
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();
		
		this.canvas = Canvas.createIfSupported();
		this.canvas.addStyleName(statistiekCss.canvas());
		mouseMoveHandler = new BoxplotMouseMoveHandler(); 
		this.canvas.addMouseMoveHandler(mouseMoveHandler);
		
		this.initializeHighlightValues();
	}
	
	/**
	 * Initialize the highlight arraylists and the independent axis width. 
	 * Lists are created and a default false value is added. If there is
	 * no split, the independent axis width is set 0.
	 *  
	 */
	public void initializeHighlightValues()
	{
		int numberOfSplits = this.boxplotView.getModel().getSplitClasses();
		
		if (numberOfSplits == 1)
		{
			this.independentAxisWidth = 0;
		}
		
		this.highlightMinValues = new ArrayList<Boolean>();
		this.highlightLowerQuartiles = new ArrayList<Boolean>();
		this.highlightMedians = new ArrayList<Boolean>();
		this.highlightUpperQuartiles = new ArrayList<Boolean>();
		this.highlightMaxValues = new ArrayList<Boolean>();

		for (int i = 0; i < numberOfSplits; i++)
		{
			this.highlightMinValues.add(i, false);
			this.highlightLowerQuartiles.add(i, false);
			this.highlightMedians.add(i, false);
			this.highlightUpperQuartiles.add(i, false);
			this.highlightMaxValues.add(i, false);
		}
	}

	/**
	 * Set boxplot values without split.
	 * 
	 * @param columnName
	 * @param minValue
	 * @param lowerQuartile
	 * @param median
	 * @param upperQuartile
	 * @param maxValue
	 * @param dataMinValue
	 * @param dataMaxValue
	 * @param verticalBoxplots
	 * @param width
	 * @param height
	 */
	public void set(String columnName, Double minValue, Double lowerQuartile,
		Double median, Double upperQuartile, Double maxValue,
		Double dataMinValue, Double dataMaxValue, boolean verticalBoxplots,
		int width, int height)
	{
		this.columnName = columnName;
		
		this.width = width;
		this.height = height;

		if (minValue == null || lowerQuartile == null || median == null
			|| upperQuartile == null || maxValue == null
			|| dataMinValue == null || dataMaxValue == null)
		{
			drawable = false;
			return;
		}
		else
		{
			drawable = true;
		}

		this.minValues = new ArrayList<Double>();
		this.minValues.add(minValue);
		this.lowerQuartiles = new ArrayList<Double>();
		this.lowerQuartiles.add(lowerQuartile);
		this.medians = new ArrayList<Double>();
		this.medians.add(median);
		this.upperQuartiles = new ArrayList<Double>();
		this.upperQuartiles.add(upperQuartile);
		this.maxValues = new ArrayList<Double>();
		this.maxValues.add(maxValue);

		this.dataMinValue = dataMinValue;
		this.dataMaxValue = dataMaxValue;

		this.verticalBoxplots = verticalBoxplots;
		this.determineScale();
	}

	/**
	 * Set boxplot values with a split.
	 * 
	 * @param columnName
	 * @param minValues
	 * @param lowerQuartiles
	 * @param medians
	 * @param upperQuartiles
	 * @param maxValues
	 * @param dataMinValue
	 * @param dataMaxValue
	 * @param verticalBoxplots
	 * @param width
	 * @param height
	 */
	public void set(String columnName, ArrayList<Double> minValues, ArrayList<Double> lowerQuartiles,
		ArrayList<Double> medians, ArrayList<Double> upperQuartiles, ArrayList<Double> maxValues,
		Double dataMinValue, Double dataMaxValue, boolean verticalBoxplots,
		int width, int height)
	{
		this.columnName = columnName;
		
		this.width = width;
		this.height = height;

		if (minValues == null || lowerQuartiles == null || medians == null
			|| upperQuartiles == null || maxValues == null
			|| dataMinValue == null || dataMaxValue == null)
		{
			drawable = false;
			return;
		}
		else
		{
			drawable = true;
		}

		this.minValues = minValues;
		this.lowerQuartiles = lowerQuartiles;
		this.medians = medians;
		this.upperQuartiles = upperQuartiles;
		this.maxValues = maxValues;

		this.dataMinValue = dataMinValue;
		this.dataMaxValue = dataMaxValue;

		this.verticalBoxplots = verticalBoxplots;
		this.determineScale();
	}

	/**
	 * Determines the y coordinate (vertical boxplot) or the
	 * x coordinate (horizontal boxplot) on the screen in the boxplot of value d.
	 * 
	 * @return the y-coordinate on the screen in the boxplot of value d
	 */
	private int valueToScreenLocation(double d)
	{
		// bepaal de fractie van de waarde op de as
		// (bijv. minimum is 1 en maximum is 0, uitgaande van verticale boxplots) 
		double a = (this.dataMaxValue - d)
			/ (this.dataMaxValue - this.dataMinValue);
		
		// bepaal de locatie op basis van de berekende a en de beschikbare ruimte
		double y = (a * FILL_FRACTION)
			* (this.verticalBoxplots ? this.getHeight() - this.independentAxisHeight : this.getWidth() - this.independentAxisWidth);
		
		// + de helft van de ruimte naast de FILL_FRACTION? 
		y += (1.0 - FILL_FRACTION) * 0.5
			* (this.verticalBoxplots ? this.getHeight() - this.independentAxisHeight : this.getWidth() - this.independentAxisWidth);

		if (!this.verticalBoxplots)
		{
			y = this.getWidth() - y;
		}
		return (int) Math.round(y);
	}

	/**
	 * Determines for each split class the y coordinate (vertical boxplot) or the
	 * x coordinate (horizontal boxplot) on the screen in the boxplot of value d.
	 * 
	 * @return an array of coordinates on the screen in the boxplot of value d
	 * 		for each split class
	 */
	private int[] valueToScreenLocation(ArrayList<Double> values)
	{
		int[] coordinates = new int[values.size()];
		
		for (int splitClass = 0; splitClass < values.size(); splitClass++)
		{
			int coordinate;
			
			if (values.get(splitClass) != null)
			{
				coordinate = this.valueToScreenLocation(values.get(splitClass));
			}
			else
			{
				coordinate = -1;
			}

			coordinates[splitClass] = coordinate; 
		}
		
		return coordinates;
	}

	/**
	 * Draws a dotted vertical line
	 * 
	 * @param x
	 * @param y1
	 * @param y2
	 * @param context
	 */
	private void drawDottedVerticalLine(int x, int y1, int y2, Context2d context)
	{
		context.beginPath();

		for (int i = y1; i < y2; i += 4)
		{
			if ((i + 1) < y2)
			{
				context.moveTo(x, i);
				context.lineTo(x, i + 2);
			}
			else
			{
				context.moveTo(x, i);
				context.lineTo(x, i + 1);
			}
		}
		
		context.stroke();
		context.closePath();
	}

	/**
	 * Draws a dotted horizontal line
	 * 
	 * @param x1
	 * @param x2
	 * @param y
	 * @param g
	 */
	private void drawDottedHorizontalLine(int x1, int x2, int y, Context2d context)
	{
		context.beginPath();

		for (int i = x1; i < x2; i += 4)
		{
			if ((i + 1) < x2)
			{
				context.moveTo(i, y);
				context.lineTo(i + 2, y);
			}
			else
			{
				context.moveTo(i, y);
				context.lineTo(i + 1, y);
			}
		}

		context.stroke();
		context.closePath();
	}
	
	public Canvas getCanvas()
	{
		return this.canvas;
	}

	/**
	 * Paint single boxplot.
	 * Bij meerdere boxplots worden meerdere singleboxplotviews naast/onder elkaar getoond. 
	 */
	public void paint()
	{
		if (!this.drawable)
		{
			return;
		}

		// clear panel
		Context2d context = canvas.getContext2d();
		context.clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		
		if (boxplotView.hasSplit())
		{
			// PAINT INDEPENDENT AXIS
			this.paintIndependentAxis(context);
		}

		// PAINT DEPENDENT AXIS
		this.paintDependentAxis(context);
		
		// PAINT BOXPLOT AREA
		this.paintBoxplotArea(context);
	}
	
	/**
	 * Paint the boxplot area.
	 * 
	 * @param context
	 */
	private void paintBoxplotArea(Context2d context)
	{
		int numberOfSplitClasses = boxplotView.getModel().getSplitClasses();

		for (int splitClass = 0; splitClass < numberOfSplitClasses; splitClass++)
		{
			this.paintBoxplot(context, splitClass);
		}
	}
		
	/**
	 * Paint the boxplot for the given split class.
	 * 
	 * @param context
	 * @param splitClass
	 */
	private void paintBoxplot(Context2d context, int splitClass)
	{
		// PAINT 'HULPLIJNEN' ONLY ONCE
		if (splitClass == 0)
		{
			if (this.verticalBoxplots)
			{
					double p = this.firstMarker;
					while (p < this.max)
					{
						int x = this.valueToScreenLocation(p);
						// Zet hulplijnkleur
						context.setStrokeStyle(ColorGenerator.GREY);
						// Teken hulplijn
						context.beginPath();
						context.moveTo(this.dependentAxisWidth, x);
						context.lineTo(this.getWidth(), x);
						context.stroke();
						context.closePath();
		
						p += step;
					}
			} //  vertical boxplots
			else
			{
				double p = this.firstMarker;
				while (p < this.max)
				{
					int x = this.valueToScreenLocation(p);
					// Zet hulplijnkleur
					context.setStrokeStyle(ColorGenerator.GREY);
					// Teken hulplijn
					context.beginPath();
					context.moveTo(x, 0);
					context.lineTo(x, this.getHeight() - Boxplot.AXIS_OFFSET);
					context.stroke();
					context.closePath();
	
					p += step;
				}
			} // horizontal boxplots
		} // if (splitClass == 0) paint markers
		
		// Zet kleur weer terug op zwart
		context.setStrokeStyle(ColorGenerator.BLACK);
		context.setFillStyle(ColorGenerator.BLACK);

		
		// PAINT THE BOXPLOT FOR THE GIVEN SPLITCLASS
		int[] locationMinValues = valueToScreenLocation(minValues);
		int[] locationLowerQuartiles = valueToScreenLocation(lowerQuartiles);
		int[] locationMedians = valueToScreenLocation(medians);
		int[] locationUpperQuartiles = valueToScreenLocation(upperQuartiles);
		int[] locationMaxValues = valueToScreenLocation(maxValues);

		if (this.verticalBoxplots)
		{
			int width = Math.min(Boxplot.MAX_BOX_HEIGHT,
				(int) Math.round(WIDTH_FILL_FRACTION * this.getWidth()));

			int x = (int) Math.round((double) (splitClass + 0.5) * singleBoxAreaWidth)
				+ this.dependentAxisWidth - (int) (0.5 * width);

			// paint min value
			if (highlightMinValues.get(splitClass))
			{
		        context.setLineWidth(3);
				context.beginPath();
		        context.moveTo(x, locationMinValues[splitClass]);
		        context.lineTo(x + width, locationMinValues[splitClass]);
				context.stroke();
				context.closePath();
		        context.setLineWidth(1);
			}
			else
			{
				context.beginPath();
				context.moveTo(x, locationMinValues[splitClass]);
				context.lineTo(x + width, locationMinValues[splitClass]);
				context.stroke();
				context.closePath();
			}

			// paint dotted line from min value to lower quartile
			this.drawDottedVerticalLine(x + width / 2, locationLowerQuartiles[splitClass],
				locationMinValues[splitClass], context);

			// paint median and quartiles box around it
			// test syl: hoe krijg ik per split een andere kleur?
			context.setFillStyle(BOX_COLOR);
			context.fillRect(x, locationUpperQuartiles[splitClass], width, 
				locationLowerQuartiles[splitClass] - locationUpperQuartiles[splitClass]);
			context.setFillStyle(ColorGenerator.BLACK);
			
			// paint lower quartile
			if (highlightLowerQuartiles.get(splitClass))
			{
		        context.setLineWidth(3);
				context.beginPath();
		        context.moveTo(x, locationLowerQuartiles[splitClass]);
		        context.lineTo(x + width, locationLowerQuartiles[splitClass]);
				context.stroke();
				context.closePath();
		        context.setLineWidth(1);
			}
			else
			{
				context.beginPath();
				context.moveTo(x, locationLowerQuartiles[splitClass]);
				context.lineTo(x + width, locationLowerQuartiles[splitClass]);
				context.stroke();
				context.closePath();
			}

			// paint median
			if (highlightMedians.get(splitClass))
			{
		        context.setLineWidth(3);
				context.beginPath();
				context.moveTo(x, locationMedians[splitClass]);
				context.lineTo(x + width, locationMedians[splitClass]);
				context.stroke();
				context.closePath();
		        context.setLineWidth(1);
			}
			else
			{
				context.beginPath();
				context.moveTo(x, locationMedians[splitClass]);
				context.lineTo(x + width, locationMedians[splitClass]);
				context.stroke();
				context.closePath();
			}
			
			// paint upper quartile
			if (highlightUpperQuartiles.get(splitClass))
			{
		        context.setLineWidth(3);
				context.beginPath();
				context.moveTo(x, locationUpperQuartiles[splitClass]);
				context.lineTo(x + width, locationUpperQuartiles[splitClass]);
				context.stroke();
				context.closePath();
		        context.setLineWidth(1);
			}
			else
			{
				context.beginPath();
				context.moveTo(x, locationUpperQuartiles[splitClass]);
				context.lineTo(x + width, locationUpperQuartiles[splitClass]);
				context.stroke();
				context.closePath();
			}
			
			context.beginPath();
			context.moveTo(x, locationLowerQuartiles[splitClass]);
			context.lineTo(x, locationUpperQuartiles[splitClass]);
			context.moveTo(x + width, locationLowerQuartiles[splitClass]);
			context.lineTo(x + width, locationUpperQuartiles[splitClass]);
			context.stroke();
			context.closePath();
			// paint dotted line from upper quartile to max value
			this.drawDottedVerticalLine(x + width / 2, locationMaxValues[splitClass],
				locationUpperQuartiles[splitClass], context);

			// paint max value
			if (highlightMaxValues.get(splitClass))
			{
		        context.setLineWidth(3);
				context.beginPath();
				context.moveTo(x, locationMaxValues[splitClass]);
				context.lineTo(x + width, locationMaxValues[splitClass]);
				context.stroke();
				context.closePath();
		        context.setLineWidth(1);
			}
			else
			{
				context.beginPath();
				context.moveTo(x, locationMaxValues[splitClass]);
				context.lineTo(x + width, locationMaxValues[splitClass]);
				context.stroke();
				context.closePath();
			}
			
		} // vertical boxplots
		else 
		{
			// horizontal boxplots
			
			int height = Math.min(Boxplot.MAX_BOX_HEIGHT,
				(int) Math.round(WIDTH_FILL_FRACTION * this.getHeight()));

			int y = this.canvas.getCoordinateSpaceHeight()
				- (this.dependentAxisHeight + (int) Math.round((splitClass + 0.5) * this.singleBoxAreaWidth))
				+ (int) (0.5 * height);
			
			// paint min value
			if (highlightMinValues.get(splitClass))
			{
		        context.setLineWidth(3);
				context.beginPath();
				context.moveTo(locationMinValues[splitClass], y);
				context.lineTo(locationMinValues[splitClass], y - height);
				context.stroke();
				context.closePath();
		        context.setLineWidth(1);
			}
			else
			{
				context.beginPath();
				context.moveTo(locationMinValues[splitClass], y);
				context.lineTo(locationMinValues[splitClass], y - height);
				context.stroke();
				context.closePath();
			}

			// paint dotted line from min value to lower quartile
			this.drawDottedHorizontalLine(locationMinValues[splitClass],
				locationLowerQuartiles[splitClass], y - height / 2, context);

			// paint median and quartiles box around it
			context.setFillStyle(BOX_COLOR);
			context.fillRect(locationLowerQuartiles[splitClass], y - height, 
				locationUpperQuartiles[splitClass] - locationLowerQuartiles[splitClass], height);

			context.beginPath();
			context.moveTo(locationLowerQuartiles[splitClass], y);
			context.lineTo(locationUpperQuartiles[splitClass], y);
			context.moveTo(locationLowerQuartiles[splitClass], y - height);
			context.lineTo(locationUpperQuartiles[splitClass], y - height);
			context.stroke();
			context.closePath();
			
			// paint median
			if (highlightMedians.get(splitClass))
			{
		        context.setLineWidth(3);
				context.beginPath();
				context.moveTo(locationMedians[splitClass], y);
				context.lineTo(locationMedians[splitClass], y - height);
				context.stroke();
				context.closePath();
		        context.setLineWidth(1);
			}
			else
			{
				context.beginPath();
				context.moveTo(locationMedians[splitClass], y);
				context.lineTo(locationMedians[splitClass], y - height);
				context.stroke();
				context.closePath();
			}
			
			// paint lower quartile
			if (highlightLowerQuartiles.get(splitClass))
			{
		        context.setLineWidth(3);
				context.beginPath();
				context.moveTo(locationLowerQuartiles[splitClass], y);
				context.lineTo(locationLowerQuartiles[splitClass], y - height);
				context.stroke();
				context.closePath();
		        context.setLineWidth(1);
			}
			else
			{
				context.beginPath();
				context.moveTo(locationLowerQuartiles[splitClass], y);
				context.lineTo(locationLowerQuartiles[splitClass], y - height);
				context.stroke();
				context.closePath();
			}
			
			// paint upper quartile
			if (highlightUpperQuartiles.get(splitClass))
			{
		        context.setLineWidth(3);
				context.beginPath();
				context.moveTo(locationUpperQuartiles[splitClass], y);
				context.lineTo(locationUpperQuartiles[splitClass], y - height);
				context.stroke();
				context.closePath();
		        context.setLineWidth(1);
			}
			else
			{
				context.beginPath();
				context.moveTo(locationUpperQuartiles[splitClass], y);
				context.lineTo(locationUpperQuartiles[splitClass], y - height);
				context.stroke();
				context.closePath();
			}

			// paint dotted line from upper quartile to max value
			this.drawDottedHorizontalLine(locationUpperQuartiles[splitClass],
				locationMaxValues[splitClass], y - height / 2, context);

			// paint max value
			if (highlightMaxValues.get(splitClass))
			{
		        context.setLineWidth(3);
				context.beginPath();
				context.moveTo(locationMaxValues[splitClass], y);
				context.lineTo(locationMaxValues[splitClass], y - height);
				context.stroke();
				context.closePath();
		        context.setLineWidth(1);
			}
			else
			{
				context.beginPath();
				context.moveTo(locationMaxValues[splitClass], y);
				context.lineTo(locationMaxValues[splitClass], y - height);
				context.stroke();
				context.closePath();
			}
		}
	}
	
	/**
	 * Paint the boxplot's dependent axis.
	 */
	private void paintDependentAxis(Context2d context)
	{
		TextMetrics metrics;

		if (this.verticalBoxplots)
		{
			double p = this.firstMarker;
			while (p < max)
			{
				// paint small markers
				int y = this.valueToScreenLocation(p);//this.valueToScreenLocationDependentAxis(p);

				context.beginPath();
				context.moveTo(this.dependentAxisWidth - Boxplot.SMALL_MARKER_LENGTH, y);
				context.lineTo(this.dependentAxisWidth, y);
				context.closePath();
				context.stroke();
				
				String pString;
				String type;
				int columnIndex = this.boxplotView.getModel().getColumnIndex();
				type = this.boxplotView.getModel().getStatTableModel().getColumnTypes().get(columnIndex).toString();
				
				// check of integer
				if (type.equals(AllowedTypes.INTEGER.toString()))
				{
					pString = String.valueOf((int) p);
				}
				else
				{
					pString = StatistiekGWT.getStringValue(p);
				}
				
				metrics = context.measureText(pString);
				context.fillText(pString,
					this.dependentAxisWidth - 7 - metrics.getWidth(),
					y + (int) (0.5 * Boxplot.FONT_HEIGHT));

				p += step;
			}

			// paint the dependent axis line
			context.beginPath();
			context.moveTo(this.dependentAxisWidth, 0);
			context.lineTo(this.dependentAxisWidth, this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET);
			context.closePath();
			context.stroke();

			context.save();
			metrics = context.measureText(this.columnName);
			// set the painting position
			context.translate(Boxplot.FONT_HEIGHT/2 + 4, this.getHeight()
				/ 2 + metrics.getWidth() / 2); // the desired position of the text
			context.rotate(Math.PI * 1.5);
			context.fillText(this.columnName, 
					0, 
					0);
			context.restore();
			
		} // vertical boxplot
		else
		{
			// horizontal boxplot
			
			double p = this.firstMarker;
			while (p < this.max)
			{
				// paint small markers
				int x = this.valueToScreenLocation(p);
				context.beginPath();
				context.moveTo(x, this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET);
				context.lineTo(x, this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET + Boxplot.SMALL_MARKER_LENGTH);
				context.closePath();
				context.stroke();
				
				String pString;
				String type;
				int columnIndex = this.boxplotView.getModel().getColumnIndex();
				type = this.boxplotView.getModel().getStatTableModel().getColumnTypes().get(columnIndex).toString();
				
				// check of integer
				if (type.equals(AllowedTypes.INTEGER.toString()))
				{
					pString = String.valueOf((int) p);
				}
				else
				{
					pString = StatistiekGWT.getStringValue(p);
				}
				
				metrics = context.measureText(pString);
				context.setFillStyle(ColorGenerator.BLACK);
				context.fillText(pString,
					x - (int) (metrics.getWidth() / 2),
					this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET 
					+ Boxplot.SMALL_MARKER_LENGTH + 10);// + SingleBoxplotView.FONT_HEIGHT);
				
				p += step;
			}

			// paint the dependent axis line
			context.beginPath();
			context.moveTo(this.independentAxisWidth, this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET);
			context.lineTo(this.getWidth(), this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET);
			context.closePath();
			context.stroke();

			context.setFillStyle(ColorGenerator.BLACK);
			metrics = context.measureText(this.columnName);
			context.fillText(this.columnName,
				this.getWidth() / 2 - metrics.getWidth() / 2,
				this.canvas.getCoordinateSpaceHeight() - 2);
		}

	} // paintDependentAxis()

	/**
	 * Paint the boxplot's independent axis, if applicable.
	 * 
	 * @param context
	 */
	public void paintIndependentAxis(Context2d context)
	{
		TextMetrics metrics;
		BoxplotModel model = this.boxplotView.getModel();

		if (!model.getStatTableModel().isColumnIndexValid(
			model.getColumnSplitIndex()))
		{
			return;
		}

		this.determineNormalFitIndependentAxis(context);
		context.setFillStyle(ColorGenerator.BLACK);

		if (this.verticalBoxplots)
		{
			// paint the axis line
			context.beginPath();
			context.moveTo(this.dependentAxisWidth, this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET);
			context.lineTo(this.getWidth(), this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET);
			context.closePath();
			context.stroke();
			
			String s;
			if (type.isNumber())
			{
				for (int i = 0; i <= model.getSplitClasses(); i++)
				{
					int x = (int) Math.round((double) i * singleBoxAreaWidth)
						+ this.dependentAxisWidth;

					// paint small markers
					context.beginPath();
					context.moveTo(x, this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET);
					context.lineTo(x, this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET + Boxplot.SMALL_MARKER_LENGTH);
					context.closePath();
					context.stroke();

					// paint text
					s = StatistiekGWT.getStringValue(model.getSplitBinBoundaries().get(i));
					
					if (normalFit)
					{
						metrics = context.measureText(s);
						int x2 = x - (int) (0.5 * metrics.getWidth());
						context.fillText(s, x2, 
							this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET + Boxplot.SMALL_MARKER_LENGTH + 10);
					}
					else
					{
						context.save();
						int x2 = x - (int) (0.5 * Boxplot.FONT_HEIGHT);
						// set the painting position
						context.translate(x2, this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET + Boxplot.SMALL_MARKER_LENGTH + 10); // the desired position of the text
						context.rotate(Math.PI * 0.5);
						context.fillText(s, 
								0, 
								0);
						context.restore();
					}
				}
			} // number type 
			else
			{
				// text
				
				for (int splitClass = 0; splitClass < model.getSplitClasses(); splitClass++)
				{
					int x1 = (int) Math.round((splitClass + 0.5) * singleBoxAreaWidth)
						+ this.dependentAxisWidth;

					int x = (int) Math.round((double) (splitClass + 0.5) * this.singleBoxAreaWidth + this.dependentAxisWidth); 

					if (type.equals(AllowedTypes.ENUM))
					{
						s = cType.getEnumOptions()[splitClass];
					}
					else
					{
						s = model.getStatTableModel()
							.getStringOptions(model.getColumnSplitIndex())
							.get(splitClass);
					}

					if (normalFit)
					{
						metrics = context.measureText(s);
						context.fillText(s, x - metrics.getWidth()/2, this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET + Boxplot.SMALL_MARKER_LENGTH + 10);
					}
					else
					{
						context.save();
						metrics = context.measureText(s);
						int x2 = x1 - (int) (0.5 * Boxplot.FONT_HEIGHT);
						// set the painting position
						context.translate(x2, this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET + Boxplot.SMALL_MARKER_LENGTH); // the desired position of the text
						context.rotate(Math.PI * 0.5);
						context.fillText(s, 
								0, 
								0);
						context.restore();
					}
				}
			}

			// paint the split variable label
			int splitIndex = this.boxplotView.getModel().getColumnSplitIndex();
			String splitColumnName = this.boxplotView.getModel().getStatTableModel().getColumnName(splitIndex);
			metrics = context.measureText(splitColumnName);
			context.fillText(splitColumnName,
				this.canvas.getCoordinateSpaceWidth() / 2 - metrics.getWidth() / 2,
				this.canvas.getCoordinateSpaceHeight() - 2);

		} // vertical boxplots
		else
		{
			// horizontal boxplots
		
			// paint the axis line
			context.beginPath();
			context.moveTo(this.independentAxisWidth, 0);
			context.lineTo(this.independentAxisWidth, this.canvas.getCoordinateSpaceHeight() - Boxplot.AXIS_OFFSET);
			context.closePath();
			context.stroke();

			if (this.type.isNumber())
			{
				for (int i = 0; i <= model.getSplitClasses(); i++)
				{
					String s = StatistiekGWT.getStringValue(model.getSplitOptions()
						.getBinBoundaries().get(i));
					int y = this.canvas.getCoordinateSpaceHeight()
						- (this.dependentAxisHeight + (int) Math.round(i * this.singleBoxAreaWidth));
					
					// paint small markers
					context.beginPath();
					context.moveTo(this.independentAxisWidth - Boxplot.SMALL_MARKER_LENGTH, y);
					context.lineTo(this.independentAxisWidth, y);
					context.closePath();
					context.stroke();

					metrics = context.measureText(s);
					context.fillText(s, this.independentAxisWidth - metrics.getWidth() - 7, 
						y + (int) (0.5 * Boxplot.FONT_HEIGHT));
				}
			} // number
			else
			{ // non number
				String s;
				for (int i = 0; i < model.getSplitClasses(); i++)
				{
					if (type.equals(AllowedTypes.ENUM))
					{
						s = this.cType.getEnumOptions()[i];
					}
					else
					{
						s = model.getStatTableModel()
							.getStringOptions(model.getColumnSplitIndex()).get(i);
					}

					int y = this.canvas.getCoordinateSpaceHeight()
						- (this.dependentAxisHeight + (int) Math.round((i + 0.5) * this.singleBoxAreaWidth));
					metrics = context.measureText(s);
					context.fillText(s, this.independentAxisWidth - metrics.getWidth() - 4, y);
				}
			} // non number
			
			// paint the split variable label
			context.save();
			int splitIndex = this.boxplotView.getModel().getColumnSplitIndex();
			String splitColumnName = this.boxplotView.getModel().getStatTableModel().getColumnName(splitIndex);
			metrics = context.measureText(splitColumnName);
			// set the painting position
			context.translate(Boxplot.FONT_HEIGHT, 
				this.canvas.getCoordinateSpaceHeight() / 2 + metrics.getWidth() / 2); // the desired position of the text
			context.rotate(Math.PI * 1.5);
			context.fillText(splitColumnName, 
					0, 
					0);
			context.restore();
		}
	}

	/**
	 * Determine whether the labels wont overlap when painted horizontally and
	 * updates the preferred size
	 */
	private void determineNormalFitIndependentAxis(Context2d context)
	{
		TextMetrics metrics;

		BoxplotModel model = this.boxplotView.getModel();
		cType = model.getStatTableModel().getColumnTypes()
			.get(model.getColumnSplitIndex());
		type = cType.getType();


		double maxStringLength = 0;

		if (type.isNumber())
		{
			for (Double d : model.getSplitBinBoundaries())
			{
				metrics = context.measureText(StatistiekGWT.getStringValue(d));
				double width = metrics.getWidth();
				if (width > maxStringLength)
				{
					maxStringLength = width;
				}
			}
		}
		else if (type.equals(AllowedTypes.ENUM))
		{
			for (String s : cType.getEnumOptions())
			{
				metrics = context.measureText(s);
				double width = metrics.getWidth();

				if (width > maxStringLength)
				{
					maxStringLength = width;
				}
			}
		}
		else
		{
			for (String s : model.getStatTableModel().getStringOptions(
				model.getColumnSplitIndex()))
			{
				metrics = context.measureText(s);
				double width = metrics.getWidth();

				if (width > maxStringLength)
				{
					maxStringLength = width;
				}
			}
		}
		
		if (this.verticalBoxplots)
		{
			singleBoxAreaWidth = (double) (this.boxplotView.getWidth()
				- this.dependentAxisWidth - BoxplotView.KEEP_CLEAR_WIDTH)
				/ (double) model.getSplitClasses();
			normalFit = maxStringLength <= singleBoxAreaWidth;

			if (normalFit)
			{
				if (this.independentAxisHeight != 35)
				{
					this.independentAxisHeight = 35;
				}
			}
			else
			{
				if (this.independentAxisHeight != maxStringLength + 20)
				{
					this.independentAxisHeight = (int) (maxStringLength + 20);
				}
			}
		} // vertical boxplot
		else
		{
			// horizontal boxplot
			
			this.singleBoxAreaWidth = (double) (this.canvas.getCoordinateSpaceHeight() 
				- BoxplotView.KEEP_CLEAR_WIDTH 
				- this.dependentAxisHeight) 
				/ (double) model.getSplitClasses();

			if ((this.independentAxisWidth != maxStringLength + Boxplot.FONT_HEIGHT + 5))
			{
				this.independentAxisWidth = Math.max(50, (int) (maxStringLength + Boxplot.FONT_HEIGHT + 5));
			}

		}
	}
	
	/**
	 * Determine an appropriate scale,
	 * i.e. step, firstMarker and max values.
	 */
	private void determineScale()
	{
		int base = 1;
		int exp = (int) Math.log10(this.dataMaxValue - this.dataMinValue) - 1;
		this.step = (int) (base * Math.pow(10, exp));
		while ((this.dataMaxValue - this.dataMinValue) / this.step > 8)
		{
			switch (base)
			{
				case 1:
					base = 2;
					break;
				case 2:
					base = 5;
					break;
				case 5:
					base = 1;
					exp++;
					break;
			}
			this.step = (int) (base * Math.pow(10, exp));
		}

		double min = this.dataMinValue
			- (1 - Boxplot.FILL_FRACTION) * 0.5
			* (this.dataMaxValue - this.dataMinValue);
		this.max = this.dataMaxValue
			+ (1 - Boxplot.FILL_FRACTION) * 0.5
			* (this.dataMaxValue - this.dataMinValue);
		this.firstMarker = Math.ceil(min / this.step) * this.step;

		// Math.ceil can give -0.0, this step turns that into 0.0
		if (firstMarker == 0)
		{
			firstMarker = 0.0;
		}
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("minValue: \t" + this.minValues + "\n");
		sb.append("lowerQuartile: \t" + this.lowerQuartiles + "\n");
		sb.append("median: \t" + this.medians + "\n");
		sb.append("upperQuartile: \t" + this.upperQuartiles + "\n");
		sb.append("maxValue: \t" + this.maxValues + "\n");
		sb.append("Data min: \t" + this.dataMinValue + "\n");
		sb.append("Data max: \t" + this.dataMaxValue + "\n");
		return sb.toString();
	}

	private void setHighlightValues(boolean minValue,
		boolean lowerQuartile, boolean median, boolean upperQuartile, 
		boolean maxValue, int splitClass)
	{
		this.setArrayListValue(this.highlightMinValues, splitClass, minValue);		
		this.setArrayListValue(this.highlightLowerQuartiles, splitClass, lowerQuartile);
		this.setArrayListValue(this.highlightMedians, splitClass, median);
		this.setArrayListValue(this.highlightUpperQuartiles, splitClass, upperQuartile);
		this.setArrayListValue(this.highlightMaxValues, splitClass, maxValue);
	}

	/**
	 * Set value at the given index or add the value at the given index if the index
	 * exceeds the size of the list.
	 *  
	 * @param valueList
	 * @param index
	 * @param value
	 */
	private void setArrayListValue(ArrayList<Boolean> valueList,
		int index, boolean value)
	{
		if (valueList.size() < index + 1)
		{
			valueList.add(index, value);
		}
		else
		{
			valueList.set(index, value);
		}
	}

	/**
	 * Get the view's width.
	 */
	public int getWidth()
	{
		return this.width;
	}
	
	/**
	 * Get the view's height.
	 */
	public int getHeight()
	{
		return this.height;
	}
	
	/**
	 * Set the width of boxplot single view.
	 * 
	 * @param w
	 */
	public void setWidth(int w)
	{
		this.width = w;
	}

	/**
	 * Set the height of boxplot single view.
	 * 
	 * @param h
	 */
	public void setHeight(int h)
	{
		this.height = h;
	}

	
	private class BoxplotMouseMoveHandler implements MouseMoveHandler
	{
		private PopupPanel popup = new PopupPanel(true);
		/**
		 * x coordinate within statistics component
		 */
		private int x = 0;
		/**
		 * y coordinate within statistics component
		 */
		private int y = 0;
		/**
		 * x coordinate within browser, used to show popup
		 */
		private int clientX = 0;
		/**
		 * y coordinate within browser, used to show popup
		 */
		private int clientY = 0;
		
		@Override
		public void onMouseMove(MouseMoveEvent event)
		{
			x = event.getX();
			y = event.getY();

			clientX = event.getClientX();
			clientY = event.getClientY();

			PositionCallback positionCallBack = new PopupPanel.PositionCallback()
			{
				public void setPosition(int offsetWidth, int offsetHeight)
				{
					//int scrollXPosition = SingleBoxplotView.this.boxplotView.getScrollPanel().getHorizontalScrollPosition();
					//int scrollYPosition = SingleBoxplotView.this.boxplotView.getScrollPanel().getVerticalScrollPosition();

					popup.setPopupPosition(clientX, clientY - offsetHeight);// - scrollXPosition
				}
			};

			int[] locationMinValues = valueToScreenLocation(minValues);
			int[] locationLowerQuartiles = valueToScreenLocation(lowerQuartiles);
			int[] locationMedians = valueToScreenLocation(medians);
			int[] locationUpperQuartiles = valueToScreenLocation(upperQuartiles);
			int[] locationMaxValues = valueToScreenLocation(maxValues);
			
			// breedte van de boxplot
			int widthBoxplot = Math.min(Boxplot.MAX_BOX_HEIGHT,
				(int) Math.round(WIDTH_FILL_FRACTION * getHeight()));

			int numberOfSplitClasses = boxplotView.getModel().getSplitClasses();

			boolean showPopup = false;
			
			for (int splitClass = 0; splitClass < numberOfSplitClasses; splitClass++)
			{
				if (verticalBoxplots)
				{
					int width = Math.min(Boxplot.MAX_BOX_HEIGHT,
						(int) Math.round(WIDTH_FILL_FRACTION * getWidth()));

					int lower_x = (int) Math.round((double) splitClass * singleBoxAreaWidth)
						+ dependentAxisWidth + width;
					int upper_x = lower_x + widthBoxplot;
					
		    		if (x > lower_x && x < upper_x
		    			&& (y > (locationMedians[splitClass] - 5)) && y < (locationMedians[splitClass] + 5))
		    		{
						this.popup.setPopupPositionAndShow(positionCallBack);
						this.popup.clear();
						this.popup.add(new Label("mediaan = " + medians.get(splitClass)));
		    			setHighlightValues(false, false, true, false, false, splitClass);
		    			showPopup = true;
		    		}
		    		else if (x > lower_x && x < upper_x
		    			&& (y > (locationMinValues[splitClass] - 5)) && y < (locationMinValues[splitClass] + 5))
		    		{
						this.popup.setPopupPositionAndShow(positionCallBack);
						this.popup.clear();
						this.popup.add(new Label("minimum = " + minValues.get(splitClass)));
		    			setHighlightValues(true, false, false, false, false, splitClass);
		    			showPopup = true;
		    		}
		    		// TODO: when e.g. minValue and lowerQuartile are close together or the same
		    		// show multiple tooltips
		    		else if (x > lower_x && x < upper_x
		    			&& (y > (locationLowerQuartiles[splitClass] - 5)) && y < (locationLowerQuartiles[splitClass] + 5))
		    		{
						this.popup.setPopupPositionAndShow(positionCallBack);
						this.popup.clear();
						this.popup.add(new Label("1e kwartiel = " + lowerQuartiles.get(splitClass)));
		    			setHighlightValues(false, true, false, false, false, splitClass);
		    			showPopup = true;
		    		}
		    		else if (x > lower_x && x < upper_x
		    			&& (y > (locationUpperQuartiles[splitClass] - 5)) && y < (locationUpperQuartiles[splitClass] + 5))
		    		{
						this.popup.setPopupPositionAndShow(positionCallBack);
						this.popup.clear();
						this.popup.add(new Label("3e kwartiel = " + upperQuartiles.get(splitClass)));			
		    			setHighlightValues(false, false, false, true, false, splitClass);
		    			showPopup = true;
		    		}
		    		else if (x > lower_x && x < upper_x
		    			&& (y > (locationMaxValues[splitClass] - 5)) && y < (locationMaxValues[splitClass] + 5))
		    		{
						this.popup.setPopupPositionAndShow(positionCallBack);
						this.popup.clear();
						this.popup.add(new Label("maximum = " + maxValues.get(splitClass)));			
		    			setHighlightValues(false, false, false, false, true, splitClass);
		    			showPopup = true;
		    		}
		    		else
		    		{
		    			setHighlightValues(false, false, false, false, false, splitClass);
		    		}
		    		
		    		if (showPopup)
		    		{
		    			// the popup location has been identified; stop searching further
		    			break;
		    		}
		    		
				} // vertical boxplots
				else // horizontal boxplots
				{
					// lower y coordinate of the horizontal boxplot
					int height = Math.min(Boxplot.MAX_BOX_HEIGHT,
						(int) Math.round(WIDTH_FILL_FRACTION * getHeight()));

					int upper_y = canvas.getCoordinateSpaceHeight()
						- (dependentAxisHeight + (int) Math.round((splitClass + 0.5) * singleBoxAreaWidth))
						+ (int) (0.5 * height);
					int lower_y = upper_y - widthBoxplot;
					
		    		if (y > lower_y && y < upper_y
		    			&& (x > (locationMedians[splitClass] - 5)) && x < (locationMedians[splitClass] + 5))
		    		{
						this.popup.setPopupPositionAndShow(positionCallBack);
						this.popup.clear();
						this.popup.add(new Label("mediaan = " + medians.get(splitClass)));			
		    			setHighlightValues(false, false, true, false, false, splitClass);
		    			showPopup = true;
		    		}
		    		else if (y > lower_y && y < upper_y
		    			&& (x > (locationMinValues[splitClass] - 5)) && x < (locationMinValues[splitClass] + 5))
		    		{
						this.popup.setPopupPositionAndShow(positionCallBack);
						this.popup.clear();
						this.popup.add(new Label("minimum = " + minValues.get(splitClass)));
		    			setHighlightValues(true, false, false, false, false, splitClass);
		    			showPopup = true;
		    		}
		    		else if (y > lower_y && y < upper_y
		    			&& (x > (locationLowerQuartiles[splitClass] - 5)) && x < (locationLowerQuartiles[splitClass] + 5))
		    		{
						this.popup.setPopupPositionAndShow(positionCallBack);
						this.popup.clear();
						this.popup.add(new Label("1e kwartiel = " + lowerQuartiles.get(splitClass)));			
		    			setHighlightValues(false, true, false, false, false, splitClass);
		    			showPopup = true;
		    		}
		    		else if (y > lower_y && y < upper_y
		    			&& (x > (locationUpperQuartiles[splitClass] - 5)) && x < (locationUpperQuartiles[splitClass] + 5))
		    		{
						this.popup.setPopupPositionAndShow(positionCallBack);
						this.popup.clear();
						this.popup.add(new Label("3e kwartiel = " + upperQuartiles.get(splitClass)));			
		    			setHighlightValues(false, false, false, true, false, splitClass);
		    			showPopup = true;
		    		}
		    		else if (y > lower_y && y < upper_y
		    			&& (x > (locationMaxValues[splitClass] - 5)) && x < (locationMaxValues[splitClass] + 5))
		    		{
						this.popup.setPopupPositionAndShow(positionCallBack);
						this.popup.clear();
						this.popup.add(new Label("maximum = " + maxValues.get(splitClass)));			
		    			setHighlightValues(false, false, false, false, true, splitClass);
		    			showPopup = true;
		    		}
		    		else
		    		{
						setHighlightValues(false, false, false, false, false, splitClass);
		    		}
		    		
		    		if (showPopup)
		    		{
		    			// the popup location has been identified; stop searching further
		    			break;
		    		}

				} // horizontal boxplots
				
			} // for-loop over split classes
			
			if (!showPopup)
			{
				this.popup.hide();
			}
			
			paint();
		}
	} // class BoxplotMouseMoveHandler

}
