package fi.statistiekgwt.client.histogram;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import fi.statistiekgwt.client.ColorUtils;
import fi.statistiekgwt.client.ColorUtils.RGBColor;
import fi.statistiekgwt.client.ColorLegend;
import fi.statistiekgwt.client.ColorPreviewer;
import fi.statistiekgwt.client.DialogButton;
import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;
import fi.statistiekgwt.client.StatistiekUtils;
import fi.statistiekgwt.client.StatistiekUtils.CustomScrollPanel;
import fi.statistiekgwt.client.StatistiekUtils.DummyTouchHandler;
import fi.statistiekgwt.client.event.OutlierChangeEvent;
import fi.statistiekgwt.client.event.OutlierChangeEventHandler;
import fi.statistiekgwt.client.event.SelectionChangeEvent;
import fi.statistiekgwt.client.event.SelectionChangeEventHandler;
import fi.statistiekgwt.client.event.TableChangeEvent;
import fi.statistiekgwt.client.event.TableChangeEventHandler;
import fi.statistiekgwt.client.event.ViewSelectionChangeEvent;
import fi.statistiekgwt.client.event.ViewSelectionChangeEventHandler;
import fi.statistiekgwt.client.histogram.HistogramModel.FrequencyTuple;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * MVC View for StatistiekView Histogram
 * 
 * @author ManuDrijvers, Sylvia van Borkulo
 * 
 */
/**
 * @author borku102
 *
 */
public class HistogramView extends LayoutPanel implements TableChangeEventHandler, SelectionChangeEventHandler, 
	OutlierChangeEventHandler, HasHandlers
{
	private HistogramModel model;
	private HistogramController controller;
	private HistogramUserOptionsPanel userOptionsPanel;
	private DialogButton dialogButton;

	public static final int KEUZEBALK_HOOGTE = 50;
	public static final int X_AS_OFFSET = 50;
	public static final int Y_AS_OFFSET = 50;
	public static final double MAX_SCREEN_FRACTION_FOR_BARS = 0.8;
	public static final int COLOR_LEGEND_WIDTH = 125;

	private double verticalBarWidth;
	private double horizontalBarWidth;

	/**
	 * List of the bar rectangles. For frequency polygons the rectangles correspond to the dots.
	 */
	private ArrayList<Rectangle> barRectangles;
	
	/**
	 * Op panel 'alles' staan mainpanel, colorlegend and dialogbutton.
	 */
	private DockLayoutPanel alles;
	private HistogramBarPanel mainPanel;
	private CustomScrollPanel scrollPanel;
	private ColorLegend colorLegend;

	public static final CssColor BAR_COLOR = ColorUtils.DEFAULT_VIEW_ELEMENT_COLOR;
	public static final CssColor SELECTED_BAR_COLOR = ColorUtils.SELECTION_COLOR;
	
	private int xAxisOffset;
	private int yAxisOffset;
	private Point lastPolygonPoint;
	
	private int width;
	private int height;
	
	/**
	 * Dummy touch handler that stops propagation. Used to avoid that an external view 
	 * in dragontouch dialogbox prevents default touch events, i.e., click events. 
	 */
	DummyTouchHandler dummyTouchHandler;
	/**
	 * The event bus to send events to event handlers associated 
	 * with the views using StatTableModel.
	 */
	EventBus eventBus;
	/**
	 * The handler registration used to remove the view's 
	 * table change event handler occurrence.
	 */
	HandlerRegistration tableChangeEventHandlerRegistration;
	/**
	 * The handler registration used to remove the view's
	 * selection change event handler occurrence.
	 */
	HandlerRegistration selectionChangeEventHandlerRegistration;
	/**
	 * The handler registration used to remove the view's
	 * outlier change event handler occurrence.
	 */
	HandlerRegistration outlierChangeEventHandlerRegistration;
	
	/**
	 * The number of the highlighted bar, or for frequency polygons the
	 * number of the highlighted dot. 
	 */
	private int highlightedBar = -1;
	private int highlightInSplit = -1;
	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;

	/**
	 * Constructor
	 * 
	 * @param model
	 *            MVC model
	 * @param controller
	 *            MVC controller
	 */
	public HistogramView(HistogramModel model, HistogramController controller)
	{
		super();
		
		this.alles = new DockLayoutPanel(Unit.PX);
		
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();
		
		this.model = model;
		this.controller = controller;
		
		this.dummyTouchHandler = StatistiekUtils.getDummyTouchHandler();
		this.eventBus = StatistiekUtils.EVENT_BUS;//new SimpleEventBus();
		
		// bind histogramview to stattablemodel: to handle table changes in stattablemodel
		this.tableChangeEventHandlerRegistration = this.model.getStatTableModel().addTableChangeEventHandler(this);//addObserver(this);

		// bind histogramview to stattablemodel: to handle selection changes in stattablemodel
		this.selectionChangeEventHandlerRegistration = this.model.getStatTableModel().addSelectionChangeEventHandler(this);
		
		// bind histogramview to stattablemodel: to handle outlier changes in stattablemodel
		this.outlierChangeEventHandlerRegistration = this.model.getStatTableModel().addOutlierChangeEventHandler(this);
		
		// create GUI
		this.mainPanel = new HistogramBarPanel(); // histogrambarpanel heeft een canvas met mousemovehandler
		
		this.scrollPanel = new CustomScrollPanel(this.mainPanel.getCanvas());
		this.scrollPanel.setAlwaysHideHorizontalScrollBar(true);
		
		this.userOptionsPanel = new HistogramUserOptionsPanel(this, controller, model);
		// initial update for setting widgets in user options panel
		this.userOptionsPanel.update();

		this.initializeSize();
		
		this.colorLegend = new ColorLegend("", null, null, 
			HistogramView.COLOR_LEGEND_WIDTH, this.getHeight());
		this.alles.addEast(this.colorLegend, HistogramView.COLOR_LEGEND_WIDTH);
		this.colorLegend.setVisible(false);
		this.alles.setWidgetHidden(this.colorLegend, true);

		HorizontalPanel dialogButtonPanel = new HorizontalPanel();
		dialogButtonPanel.setWidth("100%");
		dialogButtonPanel.setHeight("100%");
		dialogButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dialogButtonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		dialogButtonPanel.addStyleName(statistiekCss.backgroundblue());
		this.dialogButton = this.userOptionsPanel.getDialogButton();
		dialogButtonPanel.add(this.dialogButton);
		
		this.alles.addSouth(dialogButtonPanel, StatistiekGWT.BUTTON_HEIGHT);
		if (!this.model.getStatTableModel().isViewsEditable())
		{
			this.alles.setWidgetSize(dialogButtonPanel, 0);
		}

		this.alles.add(this.scrollPanel);// center
		
		this.alles.setPixelSize(this.getWidth(), this.getHeight() + StatistiekGWT.BUTTON_HEIGHT);
		
		// add alles to histogramview (layoutpanel)
		this.add(this.alles);
		
		this.addHandlers();
	}
	
	/**
	 * Add handlers, i.e., click and touch handlers to the buttons.
	 * Also add dummy handlers to stop propagation when view 
	 * is shown in its own window in a touch environment.
	 */
	private void addHandlers()
	{
		// let the view stay scrollable and clickable when shown in own window on a touch screen
	    this.alles.addDomHandler(this.dummyTouchHandler, TouchStartEvent.getType());
	    this.alles.addDomHandler(this.dummyTouchHandler, TouchMoveEvent.getType());
	    this.alles.addDomHandler(this.dummyTouchHandler, TouchEndEvent.getType());

		this.dialogButton.addClickHandler(this.dialogButton.getClickHandler());
		this.dialogButton.addDomHandler(this.dummyTouchHandler, TouchStartEvent.getType());
		this.dialogButton.addDomHandler(this.dummyTouchHandler, TouchEndEvent.getType());
	}

	/**
	 * Initialize the view's size.
	 */
	private void initializeSize()
	{
		this.setWidth(this.controller.getWidth());
		
		if (this.model.getStatTableModel().isViewsEditable())
		{
			this.setHeight(this.controller.getHeight());
		}
		else
		{
			// take up the space reserved for the user options button
			this.setHeight(this.controller.getHeight() + StatistiekGWT.BUTTON_HEIGHT);
		}
	}

	/**
	 * Get color for displaying multiple split groups in a single view
	 * 
	 * @param number
	 *            the number of the split group
	 * @return the color in which this split group will be displayed
	 */
	private CssColor getColor(int number)
	{
		return ColorUtils.getColor(number);
	}

	public void setModel(HistogramModel model)
	{
		this.model = model;
		userOptionsPanel.setModel(model);
		this.update();
		this.userOptionsPanel.update();
	}

	/**
	 * Gets the currently selected item of the combobox allowing you to choose
	 * the column that this StatistiekView will display
	 * 
	 * @return index of currently selected item
	 */
	public int getVarBoxSelectedIndex()
	{
		return userOptionsPanel.getVarBoxSelectedIndex();
	}

	public int getSplitVarBoxSelectedIndex()
	{
		return userOptionsPanel.getSplitVarBoxSelectedIndex();
	}

	public boolean isCumulativeBoxSelected()
	{
		return userOptionsPanel.isCumulativeBoxSelected();
	}

	public boolean isSplitSingleViewSelected()
	{
		return userOptionsPanel.isSplitSingleViewSelected();
	}

	public boolean isNextToEachOtherSelected()
	{
		if (this.model.isFrequencyPolygonMode())
		{
			return true; // is natuurljk een beetje gek
		}
		return userOptionsPanel.isNextToEachOtherSelected();
	}

	public boolean isStackModeBoxSelected()
	{
		return userOptionsPanel.isStackModeBoxSelected();
	}

	public boolean isOptimizeScale()
	{
		return userOptionsPanel.isOptimizeScale();
	}

	/**
	 * Gets currently selected item of the combobox allowing you to choose the
	 * number of bins
	 * 
	 * @return currently selected number of bins
	 */
	public int getBinsBoxSelectedInt()
	{
		return userOptionsPanel.getBinsBoxSelectedInt();
	}

	public int getSplitBinsBoxSelectedInt()
	{
		return userOptionsPanel.getSplitBinsBoxSelectedInt();
	}

	public double getMinBoundary()
	{
		return this.userOptionsPanel.getMinBoundary();
	}
	
	/**
	 * Set min boundary with value d
	 * @param d
	 */
	public void setMinBoundary(double d)
	{
		this.userOptionsPanel.setMinBoundary(d);
	}

	public double getSplitMinBoundary()
	{
		return userOptionsPanel.getSplitMinBoundary();
	}

	/**
	 * Set min boundary with value min
	 * @param min
	 */
	public void setSplitMinBoundary(double min)
	{
		this.userOptionsPanel.setSplitMinBoundary(min);
	}

	/**
	 * Returns bin width as calculated from the values in this.model.getBinBoundaries().
	 * -> test syl: dit geeft problemen bij gewijzigde waarde in uop
	 * 
	 * @return The bin width. Returns -1 if bin width can not be calculated.
	 */
	public double getBinWidth()
	{
		return this.userOptionsPanel.getBinWidth();
	}

	public void setBinWidth()
	{
		this.userOptionsPanel.setBinWidth();
	}

	public void setBinWidth(double d)
	{
		this.userOptionsPanel.setBinWidth(d);
	}

	public double getSplitBinWidth()
	{
		return userOptionsPanel.getSplitBinWidth();
	}

	public void setSplitBinWidth(double d)
	{
		this.userOptionsPanel.setSplitBinWidth(d);
	}

	/**
	 *Set the split bin width based on the model's split bin boundaries. 
	 */
	public void setSplitBinWidth()
	{
		this.userOptionsPanel.setSplitBinWidth();
	}

	/**
	 * Get the chosen axis from this.axisBox
	 * 
	 * @return true if x-axis selected
	 */
	public boolean xAxisSelected()
	{
		return userOptionsPanel.xAxisSelected();
	}

	/**
	 * Gets which item the user selected from the radiogroup
	 * choosing between percentage or amount.
	 * 
	 * @return true if percentage is chosen
	 */
	public boolean percentageItemSelected()
	{
		return userOptionsPanel.percentageItemSelected();
	}

	/**
	 * Gets which item the user selected from the radiogroup
	 * choosing between percentage in split category relative
	 * to split total or end total.
	 * 
	 * @return true if percentage in split is relative to split total
	 */
	public boolean percentageSplitTotalSelected()
	{
		return userOptionsPanel.percentageSplitTotalSelected();
	}

	/**
	 * Gets which item the user selected from the radiogroup
	 * choosing between labels under bin or labels between bins.
	 * 
	 * @return true if label under bin is chosen
	 */
	public boolean labelUnderBinItemSelected()
	{
		return userOptionsPanel.labelUnderBinSelected();
	}

	/**
	 * Calculate the width of each bar
	 * 
	 * @param numberOfBars
	 *            The amount of bars
	 */
	private void setBarWidth(int numberOfBars)
	{
		this.verticalBarWidth = ((double) (this.barAreaWidth() - numberOfBars - 1) / ((double) numberOfBars + 0.5));
		this.horizontalBarWidth = ((double) (this.barAreaHeight()
			- numberOfBars - 1) / ((double) numberOfBars + 0.5));
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
	 * Set the width of histogram view.
	 * 
	 * @param w
	 */
	public void setWidth(int w)
	{
		this.width = w;
	}

	/**
	 * Set the height of histogram view.
	 * 
	 * @param h
	 */
	public void setHeight(int h)
	{
		this.height = h;
	}

	/**
	 * Get the location of the dot for given dot number and height
	 * 
	 * @param dotHeight
	 *            The height of the dot
	 * @param dotNumber
	 *            The number of the dot
	 * @return the point where given dot would be painted
	 */
	private Point dotLocation(int dotHeight, int dotNumber)
	{
		if (this.model.hasVerticalBars())
		{
			int x1 = this.yAxisOffset + dotNumber + 1
				+ (int) (dotNumber * this.verticalBarWidth);
			int x2 = this.yAxisOffset + dotNumber + 1
				+ (int) ((dotNumber + 1) * this.verticalBarWidth);
			int y = this.barAreaHeight() - dotHeight;
			int xPoint;
			if (this.model.isFrequencyPolygonCumulativeMode())
			{
				xPoint = x2;
			}
			else
			{
				xPoint = (x1 + x2) / 2;
			}

			return new Point(xPoint, y);
		}
		else
		{
			int y1 = dotNumber + 1
				+ (int) ((dotNumber + 0.5) * this.horizontalBarWidth);
			int y2 = dotNumber + 1
				+ (int) ((dotNumber + 1.5) * this.horizontalBarWidth);
			int xPoint = this.yAxisOffset + dotHeight;
			int yPoint;
			if (this.model.isFrequencyPolygonCumulativeMode())
			{
				yPoint = y2;
			}
			else
			{
				yPoint = (y1 + y2) / 2;
			}
			return new Point(xPoint, yPoint);
		}
	}

	/**
	 * Paint a single bar. In case of frequencypolygon the dot and connecting
	 * line is painted. The bar color will be the standard bar color.
	 * 
	 * @param context
	 *            The graphics in which this will be painted
	 * @param barLength
	 *            The length of the bar
	 * @param selectedLength
	 *            The length of the selected part of the bar
	 * @param barNumber
	 *            The number of the bar
	 * @param ySplitOffset
	 * @param xSplitOffset
	 * @param splitClass
	 * 		The number of the split class
	 */
	private void paintBar(Context2d context, int barLength, int selectedLength,
		int barNumber, int ySplitOffset, int xSplitOffset, int splitClass)
	{
		this.paintBar(context, barLength, selectedLength, barNumber, ySplitOffset,
			xSplitOffset, HistogramView.BAR_COLOR, 0, 0, false, splitClass);
	}

	/**
	 * Paint a single bar. In case of frequency polygon, the dot and connecting
	 * line is painted.
	 * 
	 * @param context
	 *            The graphics in which this will be painted
	 * @param barLength
	 *            The length of the bar
	 * @param selectedLength
	 *            The length of the selected part of the bar
	 * @param barNumber
	 *            The number of the bar
	 * @param ySplitOffset
	 * @param xSplitOffset
	 * @param c
	 * 		The color of the bar
	 * @param numberOfBars
	 * 		Must be 'numberOfBarInBin'? If not split in single view, then 
	 * 		numberOfBars is 0. If split in single view, then numberOfBars 
	 * 		indicates the splitClass.
	 * @param totalBars
	 * @param drawNextToEachOther
	 * @param splitClass
	 * 		The number of the split class
	 */
	private void paintBar(Context2d context, int barLength, int selectedLength,
		int barNumber, int ySplitOffset, int xSplitOffset, CssColor c,
		int numberOfBars, int totalBars, boolean drawNextToEachOther, int splitClass)
	{
		if (this.model.isFrequencyPolygonMode())
		{
			// set color
			context.setStrokeStyle(c);
			context.setFillStyle(c);
			
			Point p = this.dotLocation(barLength, barNumber);
			int size = 4;
			
			// dit wordt normaal gesproken niet meer gepaint. onMouseMove toont de popup, maar doet geen paint()
			if ((highlightedBar == barNumber) && (highlightInSplit == splitClass))
			{
		        context.setLineWidth(3);
				context.arc(p.getX(), p.getY(), size, 0, 2 * Math.PI);
		        context.setLineWidth(1);
			}

			context.beginPath();
			context.arc(p.getX(), p.getY() + ySplitOffset, size, 0, 2 * Math.PI);
			context.fill();
			context.closePath();
			
			// Add 'dot' to barRectangles 
			this.barRectangles.add(new Rectangle(
				(int) p.getX() - size, (int) p.getY() - size + ySplitOffset, 2 * size,
				2 * size));

			if (this.model.isFrequencyPolygonCumulativeMode()
				&& this.lastPolygonPoint == null)
			{
				this.lastPolygonPoint = this.dotLocation(0, -1);
			}
			if (this.lastPolygonPoint != null)
			{
				// draw the connecting line
				context.beginPath();
				context.moveTo(this.lastPolygonPoint.getX(), 
					this.lastPolygonPoint.getY() + ySplitOffset);
				context.lineTo(p.getX(), p.getY() + ySplitOffset);
				context.stroke();
				context.closePath();
			}

			this.lastPolygonPoint = p;

		} // frequency polygon
		else
		{
			int columnIndex = this.model.getColumnIndex();
			ArrayList<ColumnType> list = this.model.getStatTableModel().getColumnTypes();
			AllowedTypes type = list.get(columnIndex).getType();
			
			int spacing = 0;
					
			if (type.equals(AllowedTypes.ENUM))
			{
				spacing = 4;
			}
			
			double colorMixSymm = 0.5; // t.b.v. shading
			double colorMix = 0.7;

			double darkerFactor = 0.25;
			RGBColor cRGB = ColorUtils.getRGBColor(c);
			int r = cRGB.getRed();
			int g = cRGB.getGreen();
			int b = cRGB.getBlue();
			
			RGBColor cRGBDarker =  new RGBColor((int) (r * darkerFactor), 
				(int) (g * darkerFactor), (int) (b * darkerFactor));

			if (this.model.hasVerticalBars())
			{
				int x1 = this.yAxisOffset + barNumber + 1
					+ (int) (barNumber * this.verticalBarWidth);
				int x2 = this.yAxisOffset + barNumber + 1
					+ (int) ((barNumber + 1) * this.verticalBarWidth);
				int y = this.barAreaHeight() - barLength;

				context.setStrokeStyle(c);
				int barWidth = x2 - x1;
				int barOffset = 0;
				if (drawNextToEachOther)
				{
					barWidth = (barWidth - 6) / totalBars;
					barOffset = numberOfBars * barWidth + 3;
					barWidth = barWidth - 1;
				}
				

				if (type.equals(AllowedTypes.ENUM) 
					|| type.equals(AllowedTypes.STRING)
					|| (type.equals(AllowedTypes.INTEGER)) && getBinWidth() == 1 && labelUnderBinItemSelected())
				{
					// Symmetrical shading
    				RGBColor shadingColor = ColorPreviewer.mixColors(cRGB, ColorUtils.WHITE_RGB,
						colorMixSymm);
    				
    				int x_coordinate = x1 + barOffset + spacing;
    				int y_coordinate = y + ySplitOffset;
    				int width = barWidth - spacing;
    				int height = barLength - selectedLength;
    				
    				this.fillRectWithSymmShade(x_coordinate, y_coordinate, 
    					width, height, context, c, 
    					shadingColor.getCssColor(), true);
    				
    				// draw selected bar (part)
    				if (selectedLength > 0)
    				{
	    				y_coordinate = y + barLength - selectedLength + ySplitOffset;
	    				height = selectedLength;
	
	    				// draw the selected bar darker in its original color c
	    				shadingColor = ColorPreviewer.mixColors(cRGBDarker, ColorUtils.WHITE_RGB,
							colorMixSymm);
	    				fillRectWithSymmShade(x_coordinate, y_coordinate, 
	    					width, height, context, 
	    					cRGBDarker.getCssColor(), shadingColor.getCssColor(), true);
    				}
				} // symmetrical shading
				else
				{
					// shading to the right 
					// in order to support visually that the upper bin boundary is not included
    				RGBColor shadingColor = ColorPreviewer.mixColors(cRGB, ColorUtils.WHITE_RGB,
						colorMix);

    				// paint bar
    				int x_coordinate = x1 + barOffset + spacing;
    				int y_coordinate = y + ySplitOffset;
    				int width = barWidth - spacing;
    				int height = barLength - selectedLength;
    				fillRectWithShadeToUpperBinSide(x_coordinate, y_coordinate, 
    					width, height, context, c, shadingColor.getCssColor(), true);

    				// paint selected bar
    				y_coordinate = y + barLength - selectedLength + ySplitOffset;
    				height = selectedLength;

    				// draw the selected bar darker in its original color c, but darker
    				shadingColor = ColorPreviewer.mixColors(cRGBDarker, ColorUtils.WHITE_RGB, colorMix);
    				fillRectWithShadeToUpperBinSide(x_coordinate, y_coordinate, 
    					width, height, context, cRGBDarker.getCssColor(), shadingColor.getCssColor(),
    					true);
				} // right shading
				
				context.setStrokeStyle(ColorUtils.BLACK);
				
				// test syl: dit wordt normaal gesproken niet meer gepaint. onMouseMove toont de popup, maar doet geen paint()
				if ((highlightedBar == barNumber) && (highlightInSplit == splitClass))
				{
					context.beginPath();
    		        context.setLineWidth(3);
    				context.moveTo(x1 - 1 + barOffset + spacing, y + ySplitOffset);
    				context.lineTo(x1 + barOffset + barWidth, y + ySplitOffset);
    		        context.stroke();
    		        context.closePath();
				}

				context.beginPath();
		        context.setLineWidth(1);
   				context.strokeRect(x1 - 1 + barOffset + spacing, y + ySplitOffset,
   					barWidth + 1 - spacing, barLength);
   				context.closePath();

				this.barRectangles.add(new Rectangle(x1 - 1 + barOffset + spacing, y
					+ ySplitOffset, barWidth + 1 - spacing, barLength));
			} // vertical bars
			else // horizontal bars
			{
				int x1 = this.yAxisOffset;
				int y1 = barNumber + 1
					+ (int) ((barNumber + 0.5) * this.horizontalBarWidth);
				int y2 = barNumber + 1
					+ (int) ((barNumber + 1.5) * this.horizontalBarWidth);

				int barWidth = y2 - y1;
				int barOffset = 0;
				if (drawNextToEachOther)
				{
					barWidth = barWidth / totalBars;
					barOffset = numberOfBars * barWidth;
					barWidth = barWidth - 1;
				}

				if (type.equals(AllowedTypes.ENUM) 
					|| (getBinWidth() == 1 && type.equals(AllowedTypes.INTEGER) && labelUnderBinItemSelected()))
				{
 					RGBColor shadingColor = ColorPreviewer.mixColors(cRGB, ColorUtils.WHITE_RGB,
						colorMixSymm);
    				
    				int x_coordinate = x1 + xSplitOffset + selectedLength;
    				int y_coordinate = y1 + ySplitOffset + barOffset + spacing;
    				int width = barLength - selectedLength;
    				int height = barWidth - spacing;
    				
    				fillRectWithSymmShade(x_coordinate, y_coordinate, 
    					width, height, context, c, shadingColor.getCssColor(), false);
    				
    				// draw selected bar
    				x_coordinate = x1 + xSplitOffset;
    				width = selectedLength;
    				
    				// draw the selected bar darker in its original color c, but darker
    				shadingColor = ColorPreviewer.mixColors(cRGBDarker, ColorUtils.WHITE_RGB, colorMixSymm);
    				fillRectWithSymmShade(x_coordinate, y_coordinate, 
    					width, height, context, cRGBDarker.getCssColor(), shadingColor.getCssColor(), false);

				}	
				else
				{
					// shading to the down side 
					// in order to support visually that the upper bin boundary is not included
    				RGBColor shadingColor = ColorPreviewer.mixColors(cRGB, ColorUtils.WHITE_RGB,
						colorMix);
    				
    				// paint bar
    				int x_coordinate = x1 + xSplitOffset + selectedLength;
    				int y_coordinate = y1 + ySplitOffset + barOffset + spacing;
    				int width = barLength - selectedLength;
    				int height = barWidth - spacing;
    				fillRectWithShadeToUpperBinSide(x_coordinate, y_coordinate, 
    					width, height, context, cRGB.getCssColor(), shadingColor.getCssColor(), false);

					// paint selected bar
					shadingColor = ColorPreviewer.mixColors(cRGBDarker, ColorUtils.WHITE_RGB,
						colorMix);
					x_coordinate = x1 + xSplitOffset;
					width = selectedLength;
					fillRectWithShadeToUpperBinSide(x_coordinate, y_coordinate, 
						width, height, context, cRGBDarker.getCssColor(), shadingColor.getCssColor(), false);
				}

				context.setStrokeStyle(ColorUtils.BLACK);

				if ((highlightedBar == barNumber) && (highlightInSplit == splitClass))
				{
					context.beginPath();
    		        context.setLineWidth(3);
    				context.moveTo(x1 + xSplitOffset - 1 + barLength, 
    					y1 - 1 + ySplitOffset + barOffset + spacing);
    				context.lineTo(x1 + xSplitOffset - 1 + barLength,
    					y1 + ySplitOffset + barOffset + barWidth);
    		        context.stroke();
					context.closePath();
				}

				context.beginPath();
		        context.setLineWidth(1);
				context.strokeRect(x1 + xSplitOffset - 1, y1 - 1 + ySplitOffset
					+ barOffset + spacing, barLength, barWidth + 1 - spacing);
				context.closePath();

				this.barRectangles.add(new Rectangle(x1 + xSplitOffset - 1, y1
					- 1 + ySplitOffset + barOffset + spacing, barLength,
					barWidth + 1 - spacing));
			}
		} // no frequency polygon
		//this.repaint();
	}

	/**
	 * Fills the rectangle starting at (x, y) with given width and height,
	 * and adds shade to both the right and left side, using color c for the body
	 * and shadingColor for the shading parts.
	 *  
	 * @param x
	 * 	x coordinate of the rectangle starting point
	 * @param y
	 * 	y coordinate of the rectangle starting point
	 * @param width
	 * 	width of the rectangle
	 * @param height
	 * 	height of the rectangle
	 * @param context
	 * 	the graphics to be used
	 * @param c
	 * 	the color of the body
	 * @param shadingColor
	 * 	the color of the end of the shade
	 * @param isVerticalBar
	 * 	true if the rectangle is a vertical bar,
	 * 	false if the rectangle is a horizontal bar
	 */
	private void fillRectWithSymmShade(int x, int y,
		int width, int height, Context2d context, CssColor c, CssColor shadingColor,
		boolean isVerticalBar)
	{
		double symmShadingFraction = (double) 1/3; // number indicating the part of the outside of the bar that is shaded

		CanvasGradient gradient;
		
		if (isVerticalBar)
		{
    		gradient = context.createLinearGradient(x, y, x + width, y);
		}
		else // horizontal bar
		{
			gradient = context.createLinearGradient(x, y, x, y + height);
		}
		
		gradient.addColorStop(0, shadingColor.toString());
		gradient.addColorStop(symmShadingFraction, c.toString()); // middle part is color c
		gradient.addColorStop(1 - symmShadingFraction, c.toString()); // middle part is color c
		gradient.addColorStop(1, shadingColor.toString());
		
		context.setFillStyle(gradient);

		// teken de rect nu in 1x
		if (isVerticalBar)
		{
			context.fillRect(x, y, width, height);
		}
		else
		{
			context.fillRect(x, y, width, height);
		}
		
	}
	
	/**
	 * Fills the rectangle starting at (x, y) with given width and height,
	 * and adds shade to the upper bin boundary side, using color c for the body
	 * and shadingColor for the shading part.
	 *  
	 * @param x
	 * 	x coordinate of the rectangle starting point
	 * @param y
	 * 	y coordinate of the rectangle starting point
	 * @param width
	 * 	width of the rectangle
	 * @param height
	 * 	height of the rectangle
	 * @param context
	 * 	the graphics to be used
	 * @param c
	 * 	the color of the body
	 * @param shadingColor
	 * 	the color of the end of the shade
	 * @param isVerticalBar
	 * 	true if the rectangle is a vertical bar,
	 * 	false if the rectangle is a horizontal bar
	 */
	private void fillRectWithShadeToUpperBinSide(int x, int y,
		int width, int height, Context2d context, CssColor c, CssColor shadingColor, 
		boolean isVerticalBar)
	{
		CanvasGradient gradient;
		
		if (isVerticalBar)
		{
    		gradient = context.createLinearGradient(x, y, x + width, y);
		}
		else // horizontal bar
		{
			gradient = context.createLinearGradient(x, y, x, y + height);
		}

		gradient.addColorStop(0, c.toString());
		gradient.addColorStop(1, shadingColor.toString());
		
		context.setFillStyle(gradient);

		// paint bar
		context.fillRect(x, y, width, height);
	}

	private void fillCumulativeFreqPolygonSegment(Context2d context, int dotHeight,
		int stackHeight, int prevDotHeight, int prevStackHeight, int dotNumber,
		CssColor color)
	{
		Point p1 = this.dotLocation(prevStackHeight, dotNumber - 1);
		Point p2 = this.dotLocation(prevDotHeight, dotNumber - 1);
		Point p3 = this.dotLocation(dotHeight, dotNumber);
		Point p4 = this.dotLocation(stackHeight, dotNumber);
		double[] xPoints =
			{ p1.getX(), p2.getX(), p3.getX(), p4.getX() };
		double[] yPoints =
			{ p1.getY(), p2.getY(), p3.getY(), p4.getY() };

		context.beginPath();
		context.setFillStyle(color);
		context.moveTo(xPoints[0], yPoints[0]);
		context.lineTo(xPoints[1], yPoints[1]);
		context.lineTo(xPoints[2], yPoints[2]);
		context.lineTo(xPoints[3], yPoints[3]);
		context.fill();
		context.closePath();
		this.lastPolygonPoint = p3;

		if (!this.model.isFrequencyPolygonStackMode())
		{
			// paint area above this segment white to get the correct color
			// mixing when using alpha values
			if (this.model.hasVerticalBars())
			{
				yPoints[0] = 0;
				yPoints[3] = 0;
			}
			else
			{
				xPoints[0] = this.barAreaWidth();
				xPoints[3] = this.barAreaWidth();
			}
			
			CssColor white = CssColor.make(255, 255, 255);
			context.beginPath();
			context.setFillStyle(white);
			context.moveTo(xPoints[0], yPoints[0]);
			context.lineTo(xPoints[1], yPoints[1]);
			context.lineTo(xPoints[2], yPoints[2]);
			context.lineTo(xPoints[3], yPoints[3]);
			context.fill();
			context.closePath();
			//context.fillPolygon(xPoints, yPoints, 4);
		}
	}

	/**
	 * Get the maximum value in the array.
	 * 
	 * @param array
	 * @return
	 */
	private int arrayMax(int[] array)
	{
		if (array.length == 0)
		{
			return -1;
		}
		int max = array[0];
		for (int i = 1; i < array.length; i++)
		{
			if (array[i] > max)
			{
				max = array[i];
			}
		}
		return max;
	}

	/**
	 * Get the maximum frequency value in the array.
	 * 
	 * @param array
	 * @return
	 */
	private int arrayMax(FrequencyTuple[] array)
	{
		if (array.length == 0)
		{
			return -1;
		}
		int max = array[0].frequency;
		for (int i = 1; i < array.length; i++)
		{
			if (array[i].frequency > max)
			{
				max = array[i].frequency;
			}
		}
		return max;
	}

	private int tupleArrayMax(FrequencyTuple[] array)
	{
		if (array.length == 0)
		{
			return -1;
		}
		int max = array[0].frequency;
		for (int i = 1; i < array.length; i++)
		{
			if (array[i].frequency > max)
			{
				max = array[i].frequency;
			}
		}
		return max;
	}

	private int tupleArraySum(FrequencyTuple[] array)
	{
		int sum = 0;
		for (FrequencyTuple ft : array)
		{
			sum += ft.frequency;
		}
		return sum;
	}

	/**
	 * @return The sum of all elements on even indices
	 */
	private int arrayEvenSum(int[] array)
	{
		int sum = 0;
		for (int i = 0; i < array.length; i += 2)
		{
			sum += array[i];
		}
		return sum;
	}

	private int barAreaWidth()
	{
		int w = this.getWidth() - this.yAxisOffset
			- (this.colorLegend.isVisible() ? HistogramView.COLOR_LEGEND_WIDTH : 0);//this.colorLegend.getOffsetWidth() : 0); // offsetWidth 1e keer 0?
		
		return w;
	}

	private int barAreaHeight()
	{
		int h = this.getHeight() - this.xAxisOffset;

		return h;
	}

	/**
	 * Paint the axis text labels (not the values).
	 * 
	 * @param context
	 *            The graphics in which the labels will be painted
	 */
	private void paintAxisLabels(Context2d context, int yOffset, int splitClass)
	{
		TextMetrics metrics;

		String s1 = this.model.hasVerticalBars() ? 
			(this.model.getPercentage() ? StatistiekGWT.rb.percentageLabel() : 
				StatistiekGWT.rb.frequentieLabel())
			: this.model.getStatTableModel().getColumnName(
				this.model.getColumnIndex());
		String s2 = !this.model.hasVerticalBars() ? 
			(this.model.getPercentage() ? StatistiekGWT.rb.percentageLabel() : 
				StatistiekGWT.rb.frequentieLabel())
			: this.model.getStatTableModel().getColumnName(
				this.model.getColumnIndex());
		
		// metrics van s1
		metrics = context.measureText(s1);
		context.setFillStyle(ColorUtils.BLACK);
		context.save();
		// set the drawing position 
		context.translate(
			10, //metrics.getWidth(), // hier moet de hoogte van de text
			this.barAreaHeight() / 2 + metrics.getWidth() / 2 + yOffset); // the desired position of the text
		context.rotate(Math.PI * 1.5);
		context.fillText(s1,
			0, 0);
		
		// restore context
		context.restore();
		//context.setFont(font);
		// metrics van s1
		metrics = context.measureText(s2);
		context.fillText(s2,
			(this.barAreaWidth() - this.yAxisOffset - metrics.getWidth()) / 2
				+ this.yAxisOffset, 
			this.barAreaHeight() + this.xAxisOffset - 10 + yOffset);

		if (this.model.getStatTableModel().numberOfSplitVarClasses(
			this.model.getSplitOptions()) > 1
			&& !this.model.isSplitInSingleView())
		{
			String name = this.model.getStatTableModel().getColumnName(
				this.model.getSplitOptions().getColumnSplitIndex());
			String s = name
				+ ": "
				+ this.model.getSplitOptions().getSplitClassLabel(splitClass,
					this.model.getStatTableModel());
			context.fillText(s, 10, this.barAreaHeight() + this.xAxisOffset - 10
				+ yOffset);
		}
	}

	/**
	 * Get the max frequency in the given array.
	 * 
	 * @param frequencies
	 * @return
	 */
	private int maxFrequency(int[] frequencies)
	{
		int max;
		if (this.model.isFrequencyPolygonMode()
			&& this.model.isFrequencyPolygonCumulativeMode())
		{
			// max frequency is total frequency, because we're in cumulative
			// mode
			max = this.arrayEvenSum(frequencies);
		}
		else
		{
			max = this.arrayMax(frequencies);
		}
		return max;
	}
	
	private int maxFrequencyOverAllSplits(int[][] frequenciesArray)
	{
		int max = 0;
		
		for (int[] splitFreq : frequenciesArray)
		{
			max = Math.max(max, this.maxFrequency(splitFreq));
		}
		
		return max;
	}
	
	private int maxFrequencyOverAllSplits(FrequencyTuple[][] frequenciesArray)
	{
		int max = 0;
		
		for (FrequencyTuple[] splitFreqTuple : frequenciesArray)
		{
			max = Math.max(max, this.maxFrequency(splitFreqTuple));
		}
		
		return max;
	}

	private int maxFrequency(FrequencyTuple[] frequencies)
	{
		int max;
		if (this.model.isFrequencyPolygonMode()
			&& this.model.isFrequencyPolygonCumulativeMode())
		{
			// max frequency is total frequency, because we're in cumulative
			// mode
			max = this.tupleArraySum(frequencies);
		}
		else
		{
			max = this.tupleArrayMax(frequencies);
		}
		return max;
	}

	/**
	 * Bepaalt de max frequency voor de gegeven split class.
	 * 
	 * @param frequencies
	 * @param splitClass
	 * @return
	 */
	private int maxFrequency(int[][] frequencies, int splitClass)
	{
		int max = 0;
		if (this.model.isSplitInSingleView())
		{
			if (!isNextToEachOtherSelected())
			{ // gestapeld
				for (int[] splitFreq : frequencies)
				{
					max += this.maxFrequency(splitFreq);
				}
				return max;
			}
			else
			{ // staafjes naast elkaar
				return this.maxFrequency(frequencies[splitClass]);
			}
		} // split in single view
		else
		{ // split in multiple views
			return this.maxFrequency(frequencies[splitClass]);
		}
	}

	private int maxFrequency(FrequencyTuple[][] frequencies, int splitClass)
	{
		int max = 0;
		if (this.model.isSplitInSingleView())
		{
			// if(this.model.isFrequencyPolygonMode() &&
			// this.model.isFrequencyPolygonCumulativeMode() &&
			// this.model.isFrequencyPolygonStackMode()) {
			if (!isNextToEachOtherSelected())
			{
				for (FrequencyTuple[] splitFreq : frequencies)
				{
					max += this.maxFrequency(splitFreq);
				}
				return max;
			}
			else
			{
				for (FrequencyTuple[] splitFreq : frequencies)
				{
					max = Math.max(max, this.maxFrequency(splitFreq));
				}
				return max;
			}
		}
		else
		{
			return this.maxFrequency(frequencies[splitClass]);
		}
	}

	/**
	 * Get the max percentage in the given array.
	 * 
	 * @param frequencies
	 * @return
	 */
	private int maxPercentage(int[] frequencies)
	{
		double max;
		if (this.model.isFrequencyPolygonMode()
			&& this.model.isFrequencyPolygonCumulativeMode())
		{
			// max percentage is 100%, because we're in cumulative
			// mode
			max = 100;
		}
		else
		{
			double freq = this.arrayMax(frequencies);
			double totalFrequency = this.arrayEvenSum(frequencies);
			max = 100 * freq / totalFrequency;
		}
		return (int) max;
	}
	
	/**
	 * Get the max percentage in the given array.
	 * 
	 * @param frequencies
	 * @return
	 */
	private int maxPercentage(FrequencyTuple[] frequencies)
	{
		double max;
		if (this.model.isFrequencyPolygonMode()
			&& this.model.isFrequencyPolygonCumulativeMode())
		{
			// max percentage is 100%, because we're in cumulative
			// mode
			max = 100;
		}
		else
		{
			double freq = this.arrayMax(frequencies);
			double totalFrequency = this.tupleArraySum(frequencies);
			max = 100 * freq / totalFrequency;
		}
		return (int) max;
	}
	
	/**
	 * Get the maximum percentage relative to the split (split total = 100%) over all splits.
	 * 
	 * @param frequenciesArray
	 * @return
	 */
	private int maxPercentageOverAllSplits(int[][] frequenciesArray)
	{
		int max = 0;
		
		for (int[] splitFreq : frequenciesArray)
		{
			max = Math.max(max, this.maxPercentage(splitFreq));
		}
		
		return max;
	}
	
	/**
	 * Get the maximum percentage relative to the split (split total = 100%) over all splits.
	 * 
	 * @param frequenciesArray
	 * @return
	 */
	private int maxPercentageOverAllSplits(FrequencyTuple[][] frequenciesArray)
	{
		int max = 0;
		
		for (int i = 0; i < frequenciesArray.length; i++)
		{
			max = Math.max(max, this.maxPercentage(frequenciesArray[i]));
		}
		
		return max;
	}
	
	/**
	 * Get the sum of the frequencies in the given split class.
	 * 
	 * @param frequencies
	 * @param splitClass
	 * @return the sum of the frequencies in the given split class
	 */
	private int getFrequenciesSum(int[][] frequencies, int splitClass)
	{
		int sum = 0;
		if (this.model.isSplitInSingleView()
			&& this.model.isFrequencyPolygonMode()
			&& this.model.isFrequencyPolygonCumulativeMode()
			&& this.model.isFrequencyPolygonStackMode())
		{
			for (int[] splitFreq : frequencies)
			{
				sum += this.arrayEvenSum(splitFreq);
			}
			return sum;
		}
		else
		{
			return this.arrayEvenSum(frequencies[splitClass]);
		}
	}
	
	/**
	 * Get the sum of the frequencies over all split classes.
	 * 
	 * @param frequencies
	 * @return the sum of the frequencies over all split classes
	 */
	private int getFrequenciesSum(int[][] frequencies)
	{
		int sum = 0;
		for (int[] splitFreq : frequencies)
		{
			sum += this.arrayEvenSum(splitFreq);
		}
		return sum;
	}
	
	/**
	 * Get the maximum of the summed frequencies of all split classes.
	 * 
	 * @param frequencies
	 * @param splitClass
	 * @return the maximum of the summed frequencies of all split classes
	 */
	private int getMaxFrequenciesSumOverSplitClasses(int[][] frequencies)
	{
		int sum = 0;

		// for split in single view, the return value is the max of summed frequencies per splitClass
		for (int[] splitFreq : frequencies)
		{
			sum = Math.max(sum, this.arrayEvenSum(splitFreq));
		}
		return sum;
	}
	
	/**
	 * 
	 * @param frequencies
	 * @param splitClass
	 * @return
	 */
	private int getFrequenciesSum(FrequencyTuple[][] frequencies, int splitClass)
	{
		int sum = 0;
		if (this.model.isSplitInSingleView()
			&& this.model.isFrequencyPolygonMode()
			&& this.model.isFrequencyPolygonCumulativeMode()
			&& this.model.isFrequencyPolygonStackMode())
		{
			for (FrequencyTuple[] splitFreq : frequencies)
			{
				sum += this.tupleArraySum(splitFreq);
			}
			return sum;
		}
		else
		{
			return this.tupleArraySum(frequencies[splitClass]);
		}
	}

	/**
	 * Returns the sum of the frequencies over all split classes.
	 * 
	 * @param frequencies
	 * @return
	 */
	private int getFrequenciesSum(FrequencyTuple[][] frequencies)
	{
		int sum = 0;
		for (FrequencyTuple[] splitFreq : frequencies)
		{
			sum += this.tupleArraySum(splitFreq);
		}
		return sum;
	}

	/**
	 * Paint the bars for numerical data and the axis values and markers.
	 * 
	 * @param context
	 *	The graphics in which the bars will be painted
	 * @param allFrequencies
	 *	Array of frequencies, one for each splitClass, containing for each bin
	 *	the frequency for the bin and the number of selected items within the bin. 
	 * @param splitClass
	 * 	The number of the splitClass, starting with 0. Is 0 when there is no split.
	 */
	private void paintNumberClass(Context2d context, int[][] allFrequencies,
		int splitClass)
	{
		boolean normalFit;
		double availableSpace;
		double maxValueOnAxis;
		double scale;
		double amountScale;
		
		int[] frequencies = allFrequencies[splitClass];
		TextMetrics metrics;
		double theta = Math.PI * 1.75;// 315 graden met de klok mee; 45 graden tegen de klok in
		int ySplitOffset = splitClass * (this.getHeight() - 5);

		availableSpace = HistogramView.MAX_SCREEN_FRACTION_FOR_BARS *
        	(this.model.hasVerticalBars() ? 
        		this.barAreaHeight() : 
        		this.barAreaWidth()); // if vertical then availableSpace represents height, if horizontal then width
		
		// determine max value on axis
		double max = this.maxFrequencyOverAllSplits(allFrequencies);
		if (this.model.getPercentage()
			&& this.model.getPercentageSplitTotal()) // split total is 100%
		{
			maxValueOnAxis = this.maxPercentageOverAllSplits(allFrequencies);
		}
		else if (this.model.getPercentage()) // end total is 100%
		{
			maxValueOnAxis = 100.0 * max / (this.getFrequenciesSum(allFrequencies)); 
		}
		else
		{
			maxValueOnAxis = max;
		}
			
		// determine scale for the axis with percentages or amounts; should be the same for splits in multiple views
		if (maxValueOnAxis == 0)
		{
			scale = 0;
		}
		else
		{
			scale =  availableSpace/maxValueOnAxis;
		}
		
		if (this.model.hasVerticalBars())
		{
			this.yAxisOffset = this.determineDependentAxisWidth(context, scale) + 20;
		}
		else
		{
			this.xAxisOffset = this.determineDependentAxisWidth(context, scale);
		}

		// set bar width
		int numberOfBars = frequencies.length / 2;
		if (isOptimizeScale())
		{
			this.setBarWidth(numberOfBars);
		}
		else
		{
			int numberOfBinsOnScale = this.getNumberOfBinsfromBinsSettings(); 
			this.setBarWidth(numberOfBinsOnScale);
		}

		// bepaal de bins
		ArrayList<Double> binsOnScale = this.getBinsOnScale();

		if (this.model.hasVerticalBars())
		{
			// check if the bin boundary strings will fit and determine the longest
			normalFit = true;
			double longest = 0;
			double width;
			
			// bepaal normalFit (of labels bij as onder staven passen of gekanteld moeten worden)
			if (this.model.getLabelUnderBin())
			{
				int marge = 5;
				int columnIndex = this.model.getColumnIndex();
				ArrayList<ColumnType> list = this.model.getStatTableModel().getColumnTypes();
				AllowedTypes type = list.get(columnIndex).getType();
				
				for (int i = 0; i < binsOnScale.size(); i++)
				{
					String s = StatistiekGWT.getStringValue(binsOnScale.get(i));
					if (i < binsOnScale.size() - 1)
					{
						String s_labelUnderBin;
						if (type.equals(AllowedTypes.INTEGER) && ((int) model.getBinWidth()) == 1)
						{
							// Voor gehele getallen met 1 waarde per klasse, 1 getal tonen onder de staaf
							s_labelUnderBin = s;
						}
						else
						{
    						s_labelUnderBin = s + "-<" +
								StatistiekGWT.getStringValue(binsOnScale.get(i + 1));
						}
						
						metrics = context.measureText(s_labelUnderBin);
						width = metrics.getWidth() + marge;
						if (width > this.verticalBarWidth)
						{
							normalFit = false;
						}
						if (width > longest)
						{
							longest = width;
						}
					}
				}
			} // label under bin
			else // label between bins
			{
				for (Double d : binsOnScale)
				{
					metrics = context.measureText(d.toString());
					width = metrics.getWidth();
					if (width > this.verticalBarWidth)
					{
						normalFit = false;
					}
					if (width > longest)
					{
						longest = width;
					}
				}
			}

			if (normalFit)
			{
				this.xAxisOffset = 40;
			}
			else
			{
				this.xAxisOffset = (int) (longest + 15);
			}
		} // vertical bars
		else // horizontal bars
		{
			double longest = 0;
			double width;

			// bepaal normalFit (of labels bij as onder staven passen of gekanteld moeten worden)
			if (this.model.getLabelUnderBin())
			{
				int columnIndex = this.model.getColumnIndex();
				ArrayList<ColumnType> list = this.model.getStatTableModel().getColumnTypes();
				AllowedTypes type = list.get(columnIndex).getType();
				
				for (int i = 0; i < binsOnScale.size(); i++)
				{
					String s = StatistiekGWT.getStringValue(binsOnScale.get(i));
					if (i < binsOnScale.size() - 1)
					{
						String s_labelUnderBin;
						if (type.equals(AllowedTypes.INTEGER) && ((int) model.getBinWidth()) == 1)
						{
							// Voor gehele getallen met 1 waarde per klasse, 1 getal tonen bij de staaf
							s_labelUnderBin = s;
						}
						else
						{
    						s_labelUnderBin = s + "-<" +
								StatistiekGWT.getStringValue(binsOnScale.get(i + 1));
						}
						
						metrics = context.measureText(s_labelUnderBin);
						width = metrics.getWidth();
						if (width > longest)
						{
							longest = width;
						}
					}
				}
			}
			else // labels between bins
			{
				// find longest binboundary label
				for (Double d : binsOnScale)
				{
					metrics = context.measureText(d.toString());
					width = metrics.getWidth();
					if (width > longest)
					{
						longest = width;
					}
				}
			}

			this.yAxisOffset = (int) (longest + 15);
		} // horizontal bars

		// correct scales
		// bepaal de amountscale om de bar-lengtes per split te kunnen berekenen
		if (this.model.getPercentage())
		{
			if (this.model.getStatTableModel().numberOfSplitVarClasses(
				this.model.getSplitOptions()) > 1)
			{
				// er is een split

				if (this.model.isSplitInSingleView())
				{
					if (this.model.isFrequencyPolygonMode()
						&& this.model.isFrequencyPolygonCumulativeMode())
					{
						maxValueOnAxis = 100.0;
					}
					else
					{
						int maxInSplit = 0;
						double maxFraction = 0;
						for (int split = 0; split < allFrequencies.length; split++)
						{
							maxInSplit = this.maxFrequency(allFrequencies, split);
							max = Math.max(max, maxInSplit);
							maxFraction = Math.max((double)maxInSplit/this.getFrequenciesSum(allFrequencies), maxFraction);
						}
						
						maxValueOnAxis = 100.0 * maxFraction;
					}
			        
					scale = availableSpace/maxValueOnAxis;
				} // split in single view
				else
				{ // split in multiple views
					if (this.model.getPercentageSplitTotal())
					{
						// amountScale = de beschikbare ruimte (die toont maxValueOnScale) gedeeld door de frequentie (in de huidige splitClass) die deze maxValueOnScale-waarde geeft.
						// Let op: voor split total = 100% is max dus de frequentie (in de huidige splitClass) die deze maxValueOnScale-waarde geeft
						// maar dat is niet perse een frequentie die voorkomt in de huidige splitClass. Het is daarom een decimale waarde.
						max = maxValueOnAxis * this.getFrequenciesSum(allFrequencies, splitClass) / 100;
					}
				}
			}
		} // percentage
		else
		{ // aantallen
			if (this.model.getStatTableModel().numberOfSplitVarClasses(
				this.model.getSplitOptions()) > 1)
			{
				// er is een split

				if (this.model.isSplitInSingleView())
				{
					int maxInSplit = 0;
					for (int split = 0; split < allFrequencies.length; split++)
					{
						maxInSplit = Math.max(maxInSplit, this.maxFrequency(allFrequencies, split));
						max = Math.max(max, maxInSplit);
					}
					
					maxValueOnAxis = maxInSplit;
					max = maxInSplit;
			        

					if (maxValueOnAxis == 0)
					{
						scale = 0;
					}
					else
					{
						scale = availableSpace / maxValueOnAxis;
					}
				} // split in single view
			}
		}
		
		// PAINT SCALE
		this.paintScale(context, scale, ySplitOffset);

		// PAINT BARS
		// use amountScale to paint the correct bar length for each split
		if (max == 0)
		{
			amountScale = availableSpace;
		}
		else
		{
			amountScale = availableSpace/max;
		}

		ArrayList<String> splitColors = new ArrayList<String>();
		ArrayList<String> splitLabels = new ArrayList<String>();

		if (this.model.isSplitInSingleView()
			&& this.model.getStatTableModel().numberOfSplitVarClasses(
				this.model.getSplitOptions()) > 1)
		{
			if (this.model.isFrequencyPolygonMode()
				&& this.model.isFrequencyPolygonCumulativeMode()
				&& this.model.isFrequencyPolygonStackMode())
			{
				int[] totalFrequencies = new int[frequencies.length / 2];
				int[] frequencySum = new int[frequencies.length / 2];
				for (int split = 0; split < allFrequencies.length; split++)
				{
					int splitFreq[] = allFrequencies[split];
					this.lastPolygonPoint = null;
					CssColor c = this.getColor(split);
					splitColors.add(c.value());
					splitLabels.add(this.model.getSplitOptions()
						.getSplitClassLabel(splitClass,
							this.model.getStatTableModel()));

					for (int i = 0; i < splitFreq.length / 2; i++)
					{
						if (i > 0)
						{
							frequencySum[i] = frequencySum[i - 1]
								+ splitFreq[2 * i];
						}
						else
						{
							frequencySum[i] = splitFreq[2 * i];
						}

						this.fillCumulativeFreqPolygonSegment(
							context,
							(int) Math.round(amountScale
								* (totalFrequencies[i] + frequencySum[i])),
							(int) Math.round(amountScale * totalFrequencies[i]),
							(int) Math.round(amountScale
								* (i > 0 ? frequencySum[i - 1]
									+ totalFrequencies[i - 1] : 0)),
							(int) Math.round(amountScale
								* (i > 0 ? totalFrequencies[i - 1] : 0)), i, c);
					}
					for (int i = 0; i < frequencySum.length; i++)
					{
						totalFrequencies[i] += frequencySum[i];
					}
					//System.out.println(Arrays.toString(totalFrequencies));
				}
			} // frequentiepolygoon, cumulatief, stapelen
			else
			{
				// frequentiepolygoon, cumulatief, split in single view
				if (this.model.isFrequencyPolygonMode()
					&& this.model.isFrequencyPolygonCumulativeMode())
				{
					// Paint the frequency polygon as dots and lines 
					for (int split = 0; split < allFrequencies.length; split++)
					{
						if (this.model.getPercentage())
						{
							amountScale = availableSpace/this.maxFrequency(allFrequencies[split]);
						}
						else
						{ // aantal
							// bij aantal moet hij de max frequency over alle splits nemen
							amountScale = availableSpace/this.maxFrequencyOverAllSplits(allFrequencies);
						}
						
						int[] splitFreq = allFrequencies[split];
						CssColor c = this.getColor(split);
						splitColors.add(c.value());
						splitLabels.add(this.model.getSplitOptions()
							.getSplitClassLabel(splitClass,
								this.model.getStatTableModel()));

						int frequencySum = 0;
						int frequencySelectedSum = 0;
						
						for (int i = 0; i < frequencies.length / 2; i++)
						{
							frequencySum += splitFreq[2 * i];
							frequencySelectedSum = splitFreq[2 * i + 1];
							this.paintBar(
								context,
								(int) (frequencySum * amountScale),
								(int) (frequencySelectedSum * amountScale),
								i, 0, 0, c, split,
								allFrequencies.length, true, split);
						}
						this.lastPolygonPoint = null;
					}

					//context.setComposite(this.makeComposite(1));

				} // cumulatief frequentiepolygoon
				else
				{
					// niet-cumulatief frequentiepolygoon
					// en histogram met split in single view
					
					// Hier worden de samengestelde staafjes getekend.
					int[] cumHeight = new int[frequencies.length / 2];
					for (int split = 0; split < allFrequencies.length; split++)
					{
						int[] splitFreq = allFrequencies[split];
						CssColor c = this.getColor(split);
						splitColors.add(c.value());
						splitLabels.add(this.model.getSplitOptions()
							.getSplitClassLabel(splitClass,
								this.model.getStatTableModel()));

						for (int i = 0; i < frequencies.length / 2; i++)
						{
							if (this.model.hasVerticalBars())
							{
								if (this.isNextToEachOtherSelected())
								{
									this.paintBar(
										context,
										(int) (splitFreq[2 * i] * amountScale),
										(int) (splitFreq[2 * i + 1] * amountScale),
										i, 0, 0, c, split,
										allFrequencies.length, true, split);
								}
								else
								{
									this.paintBar(
										context,
										(int) (splitFreq[2 * i] * amountScale),
										(int) (splitFreq[2 * i + 1] * amountScale),
										i, -cumHeight[i], 0, c, 0, 0, false, split);
								}
							}
							else
							{
								if (this.isNextToEachOtherSelected())
								{
									this.paintBar(
										context,
										(int) (splitFreq[2 * i] * amountScale),
										(int) (splitFreq[2 * i + 1] * amountScale),
										i, 0, 0, c, split,
										allFrequencies.length, true, split);
								}
								else
								{
									this.paintBar(
										context,
										(int) (splitFreq[2 * i] * amountScale),
										(int) (splitFreq[2 * i + 1] * amountScale),
										i, 0, cumHeight[i], c, 0, 0, false, split);
								}
							}
							cumHeight[i] += (int) (splitFreq[2 * i] * amountScale);
						}
						this.lastPolygonPoint = null;
					}
					//context.setComposite(this.makeComposite(1));
				}
			}
		} // split van meer dan 1 klasse
		else
		{ // no split or split in multiple views

			if (this.model.isFrequencyPolygonMode()
				&& this.model.isFrequencyPolygonCumulativeMode())
			{
				if (this.model.getStatTableModel().numberOfSplitVarClasses(
				this.model.getSplitOptions()) > 1)
				{
					// split in multiple views
					CssColor c = this.getColor(splitClass);
					splitColors.add(c.value());
					splitLabels.add(this.model.getSplitOptions()
						.getSplitClassLabel(splitClass,
							this.model.getStatTableModel()));

					int frequencySum = 0;
					int frequencySelectedSum = 0;
					for (int i = 0; i < frequencies.length / 2; i++)
					{
						frequencySum += frequencies[2 * i];
						frequencySelectedSum = frequencies[2 * i + 1];
						this.paintBar(
							context,
							(int) (frequencySum * amountScale),
							(int) (frequencySelectedSum * amountScale),
							i, ySplitOffset, 0, c, splitClass,
							allFrequencies.length, true, splitClass);
					}
				}
				else // no split
				{
					int frequencySum = 0;
					int frequencySelectedSum = 0;
					for (int i = 0; i < frequencies.length / 2; i++)
					{
						frequencySum += frequencies[2 * i];
						frequencySelectedSum = frequencies[2 * i + 1];
						this.paintBar(
							context, 
							(int) (frequencySum * amountScale),
							(int) (frequencySelectedSum * amountScale), 
							i, ySplitOffset, 0, splitClass);
					}
				}
			} // frequentiepolygoon cumulatief
			else
			{ // frequentiepolygoon niet-cumulatief of split in multiple views
				for (int i = 0; i < frequencies.length / 2; i++)
				{
					if (allFrequencies.length > 1)
					{
						this.paintBar(
							context,
							(int) (frequencies[2 * i] * amountScale),
							(int) (frequencies[2 * i + 1] * amountScale), 
							i, ySplitOffset, 0, this.getColor(splitClass), 0, 0,
							false, splitClass);
					}
					else
					{
						this.paintBar(
							context,
							(int) (frequencies[2 * i] * amountScale),
							(int) (frequencies[2 * i + 1] * amountScale), 
							i, ySplitOffset, 0, splitClass);
					}
				}
			}
		}

		context.beginPath(); // required to push a new state
		context.setFillStyle(ColorUtils.BLACK);
		context.setStrokeStyle(ColorUtils.BLACK);

		int columnIndex = this.model.getColumnIndex();
		ArrayList<ColumnType> list = this.model.getStatTableModel().getColumnTypes();
		AllowedTypes type = list.get(columnIndex).getType();

		// PAINT BIN BOUNDARY LABELS
		if (this.model.hasVerticalBars())
		{
			// check if the bin boundary strings will fit
			normalFit = determineNormalFitForVerticalBars(context, type);
			
			paintVerticalBinBoundaryLabels(context, normalFit, theta,
				ySplitOffset, type, binsOnScale);
		} // vertical bars
		else
		{ // horizontal bars
			paintHorizontalBinBoundaryLabels(context, frequencies,
				ySplitOffset, type, binsOnScale);
		} // horizontal bars
	} // paintNumberClass()

	/**
	 * Paint the bin boundary labels on the scale for a vertical histogram.
	 * 
	 * @param g
	 * @param fm
	 * @param ySplitOffset
	 * @param type
	 * @param binsOnScale
	 */
	private void paintHorizontalBinBoundaryLabels(Context2d context,
		int[] frequencies, int ySplitOffset, AllowedTypes type, ArrayList<Double> binsOnScale)
	{
		TextMetrics metrics;
		// paint bin boundaries
		for (int i = 0; i < binsOnScale.size(); i++)
		{
			int y = (int) (i + (i + 0.5) * this.horizontalBarWidth);
			int x = this.yAxisOffset;
			// Get the string value (integer or double)
			String s = StatistiekGWT.getStringValue(binsOnScale.get(i));
			
			if (this.model.getLabelUnderBin())
			{
				// put label under bin
				if (i < binsOnScale.size() - 1)
				{
					String s_labelUnderBin;
					if (type.equals(AllowedTypes.INTEGER) && ((int) model.getBinWidth()) == 1)
					{
						if (!HistogramView.this.model.isFrequencyPolygonMode())
						{
							s_labelUnderBin = s;
						}
						else
						{
							s_labelUnderBin = s;
							// draw marker
							context.beginPath();
							context.moveTo(x - 7, y + ySplitOffset);
							context.lineTo(x - 2, y + ySplitOffset);
							context.closePath();
							context.stroke();
						}
					}
					else
					{
						s_labelUnderBin = s + "-<" +
							StatistiekGWT.getStringValue(binsOnScale.get(i + 1));
						// draw marker
						context.beginPath();
						context.moveTo(x - 7, y + ySplitOffset);
						context.lineTo(x - 2, y + ySplitOffset);
						context.closePath();
						context.stroke();
					}
					
					metrics = context.measureText(s_labelUnderBin);
					int offset_labelUnderBin = (int) metrics.getWidth();
					context.fillText(s_labelUnderBin, x - offset_labelUnderBin - 7, y
						- 2 + ySplitOffset + (int) this.horizontalBarWidth/2);
				}
				else
				{
					// draw last marker
					context.beginPath();
					context.moveTo(x - 7, y + ySplitOffset);
					context.lineTo(x - 2, y + ySplitOffset);
					context.closePath();
					context.stroke();
				}
			}
			else
			{
				// draw marker
				context.beginPath();
				context.moveTo(x - 7, y + ySplitOffset);
				context.lineTo(x - 2, y + ySplitOffset);
				context.closePath();
				context.stroke();
				// put label between bins
				metrics = context.measureText(s);
				int offset = (int) metrics.getWidth();
				context.fillText(s, x - offset - 7, y - 2 + ySplitOffset);
			}
		}
	}

	/**
	 * Paint the bin boundary labels on the scale for a vertical histogram.
	 * 
	 * @param g
	 * @param normalFit
	 * @param fm
	 * @param theta
	 * @param rotateFont
	 * @param ySplitOffset
	 * @param type
	 * @param binsOnScale
	 */
	private void paintVerticalBinBoundaryLabels(Context2d context,
		boolean normalFit, double theta, int ySplitOffset, AllowedTypes type, ArrayList<Double> binsOnScale)
	{
		TextMetrics metrics;
		// paint bin boundaries
		if (normalFit)
		{
			int y = this.barAreaHeight();

			for (int i = 0; i < binsOnScale.size(); i++)
			{
				int x = (int) (this.yAxisOffset + i + i * this.verticalBarWidth);
				
				// Get the string value (integer or double)
				String s = StatistiekGWT.getStringValue(binsOnScale.get(i));

				metrics = context.measureText(s);
				int offset = (int) (metrics.getWidth() / 2);
				if (this.model.getLabelUnderBin())
				{
					// put label under bin
					if (i < binsOnScale.size() - 1)
					{
						context.beginPath();

						String s_labelUnderBin;
						if (type.equals(AllowedTypes.INTEGER) && ((int) model.getBinWidth()) == 1)
						{
							s_labelUnderBin = s;
						}
						else
						{
							s_labelUnderBin = s + "-<" +
								StatistiekGWT.getStringValue(binsOnScale.get(i + 1));
						}
						
						// draw marker
						context.moveTo(x, y + ySplitOffset);
						context.lineTo(x, y + 5 + ySplitOffset);
						context.closePath();
						context.stroke();

						int x2 = (int) (this.yAxisOffset + (i + 1) + (i + 1)
							* this.verticalBarWidth);
						metrics = context.measureText(s_labelUnderBin);
						String font = context.getFont();
						int offset_labelUnderBin = (int) (metrics.getWidth() / 2);
						context.fillText(s_labelUnderBin, ((x + x2)/2) - offset_labelUnderBin, y + 15 + ySplitOffset);
					}
					else
					{
						if (!type.equals(AllowedTypes.INTEGER) || ((int) model.getBinWidth()) != 1)
						{
							// draw last marker
							// test syl: kan dit weg? Markers worden altijd al getekend...?
							context.beginPath();
							context.moveTo(x, y + ySplitOffset);
							context.lineTo(x, y + 5 + ySplitOffset);
							context.closePath();
							context.stroke();
						}
					}
				}
				else
				{
					// draw marker
					context.beginPath();
					context.moveTo(x, y + ySplitOffset);
					context.lineTo(x, y + 5 + ySplitOffset);
					context.closePath();
					context.stroke();
					// put label between bins
					context.fillText(s, x - offset, y + 15 + ySplitOffset);
				}
			} // for loop over bins
		} // normal fit
		else
		{
			// the boundary labels won't fit the normal way, so rotate
			//context.setFont(rotateFont);
			
			int y = this.barAreaHeight();
			for (int i = 0; i < binsOnScale.size(); i++)
			{
				int x = (int) (this.yAxisOffset + i + i
					* this.verticalBarWidth);
				context.moveTo(x, y + ySplitOffset);
				context.lineTo(x, y + 5 + ySplitOffset);
				
				// Get the string value (integer or double)
				String s = StatistiekGWT.getStringValue(binsOnScale.get(i));
				metrics = context.measureText(s);
				int offset = (int) metrics.getWidth();
				
				if (this.model.getLabelUnderBin())
				{
					// put label under bin
					if (i < binsOnScale.size() - 1)
					{
						String s_labelUnderBin;
						if (type.equals(AllowedTypes.INTEGER) && ((int) model.getBinWidth()) == 1)
						{
							// Voor gehele getallen met 1 waarde per klasse, 1 getal tonen onder de staaf
							s_labelUnderBin = s;
						}
						else
						{
							s_labelUnderBin = s + "-<" +
								StatistiekGWT.getStringValue(binsOnScale.get(i + 1));
						}
						
						metrics = context.measureText(s_labelUnderBin);
						double offset_labelUnderBin = metrics.getWidth();
						int widthRotatedLabel = (int) (offset_labelUnderBin * Math.cos(theta));
						int heightRotatedLabel = (int) (offset_labelUnderBin * -Math.sin(theta));
						
						context.save();
						// set the drawing position 
						context.translate(
							(int) (x + 5 + (this.verticalBarWidth/2) - widthRotatedLabel), 
							y + 7 + heightRotatedLabel + 5 + ySplitOffset); // the desired position of the text
						context.rotate(theta);
						context.fillText(s_labelUnderBin, 
								0, 
								0);
						context.restore();
					}
				}
				else
				{
					// put label between bins
					metrics = context.measureText(s);
					double offset_labelBetweenBins = metrics.getWidth();
					int widthRotatedLabel = (int) (offset_labelBetweenBins * Math.cos(theta));
					context.save();
					// set the drawing position 
					context.translate(x + 5 - widthRotatedLabel, y + 7 + offset + ySplitOffset);
					context.rotate(theta);
					context.fillText(s, 0, 0);
					context.restore();
				}
			} // for loop over bins
		} // no normal fit
	}

	/**
	 * Get the upper value of the upper bin on the scale.
	 * @return
	 */
	public double getMaxBinOnScale()
	{
		double max;
		
		ArrayList<Double> binsOnScale = getBinsOnScale();
		max = binsOnScale.get(binsOnScale.size() - 1);
		
		return max;
	}

	/**
	 * Get the bin boundaries on the histogram's scale.
	 * 
	 * @return
	 */
	ArrayList<Double> getBinsOnScale()
	{
		ArrayList<Double> bins;
		
		if (model.isOptimizeScale())
		{
			bins = new ArrayList<Double>(model.getBinBoundaries());
		}
		else
		{
			bins = this.getBinsfromBinsSettings();
		}
		
		return bins;
	}

	/**
	 * Get the bin boundaries based on the settings in the user options panel.
	 * 
	 * @return
	 */
	private ArrayList<Double> getBinsfromBinsSettings()
	{
		ArrayList<Double> bins = new ArrayList<Double>();
		double maxOnScale = model.getMaxOnScale();
		double binWidth = model.getBinWidth();
		
		if (this.model.getStatTableModel().isEmptyColumn(this.model.getColumnIndex()))
		{
			// use the settings without check for valid values
			double minOnScale = this.getMinBoundary();//model.getMinOnScale(); // voor lege kolom is minOnScale mogelijk op 0 gezet
			double binValue = minOnScale;
			for (int i = 0; binValue < maxOnScale; i++)
			{
				binValue = minOnScale + i * binWidth;
				bins.add(binValue);
			}
			
			if (minOnScale == maxOnScale)
			{
				bins.add(minOnScale);
				bins.add(maxOnScale);
			}
			else if (bins.isEmpty())
			{
				bins.add(0.0);
				bins.add(0.0);
			}
		}
		else
		{
			// use the settings, check for valid values
			double binValue;
			double startValue = model.getMinOnScale();
			double min = this.model.getStatTableModel().getColumnMin(this.model.getColumnIndex());
			double max = this.model.getStatTableModel().getColumnMax(this.model.getColumnIndex());
			
			if (startValue > min)
			{
				startValue = min;
			}
			binValue = startValue;
			
			if (maxOnScale <= max)
			{
				maxOnScale = max + 1; // bins do not include the upper boundary, so max + 1
			}
			
			for (int i = 0; binValue < maxOnScale; i++)
			{
				binValue = startValue + i * binWidth;
				bins.add(binValue);
			}
		}

		return bins;
	}

	private boolean determineNormalFitForVerticalBars(Context2d context, AllowedTypes type)
	{
		boolean normalFit = true;
		TextMetrics metrics;

		ArrayList<Double> binsOnScale = this.getBinsOnScale();

		if (this.model.getLabelUnderBin())
		{
			int marge = 5;
			for (int i = 0; i < binsOnScale.size(); i++)
			{
				String s = StatistiekGWT.getStringValue(binsOnScale.get(i));
				if (i < binsOnScale.size() - 1)
				{
					String s_labelUnderBin;
					
					if (type.equals(AllowedTypes.INTEGER) && ((int) model.getBinWidth()) == 1)
					{
						s_labelUnderBin = s;
					}
					else
					{
						s_labelUnderBin = s + "-<" +
							StatistiekGWT.getStringValue(binsOnScale.get(i + 1));
					}
					
					metrics = context.measureText(s_labelUnderBin);
					if (metrics.getWidth() + marge > this.verticalBarWidth)
					{
						normalFit = false;
						break;
					}
				}
			}
		}
		else // label between bins
		{
			for (Double d : binsOnScale)
			{
				metrics = context.measureText(d.toString());
				if (metrics.getWidth() > this.verticalBarWidth)
				{
					normalFit = false;
					break;
				}
			}
		}
		
		return normalFit;
	}

	private int determineDependentAxisWidth(Context2d context, double scale)
	{
		int width;
		
		if (this.model.hasVerticalBars())
		{
			if (scale == 0)
			{
				width = 7;//14; 14 voor breedte van 2-cijferige y-aswaarden
			}
			else
			{
				width = 5;
	
				int panelHeight = (int) ((this.model.hasVerticalBars() ? this
					.barAreaHeight() : this.barAreaWidth()));
				int base = 1;
				int exp = 0;
				int step = (int) (base * Math.pow(10, exp));
				while (step * 6 * scale < panelHeight)
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
					step = (int) (base * Math.pow(10, exp));
				}
	
				// hoe kom ik aan de font-height?
				int h = this.getCurrentFontHeight(context);
				int majorSteps = (int) Math.floor(
					(panelHeight - 0.5 * h) / (step * scale));
	
				// determine the width of the axis labels
	
				TextMetrics metrics;
				for (int i = 0; i < majorSteps + 1; i++)
				{
					String s = new Integer(i * step).toString();
					if (this.model.getPercentage())
					{
						s = s + "%";
					}
					
					metrics = context.measureText(s);
					int stringWidth = (int) metrics.getWidth();
					if (stringWidth > width)
					{
						width = stringWidth;
					}
				}
			}
		}
		else
		{
			width = 40;
		}
		
		return width;
	}

	/**
	 * Get the height of the current font. The coordinates
	 * specify a rect that is save to draw in.  
	 * @param context
	 */
	private int getCurrentFontHeight(Context2d context)
	{
		int result = 0;
		int height = 0;
		int ascent = 0;
		int descent = 0;
		String font = context.getFont();
		
		HTML text = new HTML("<span>Hg</span>");
		//DOM.setElementProperty(text.getElement(), font, font);
		//$('<span>Hg</span>').css({ fontFamily: font });
		
		HTML block = new HTML("<div style='display: inline-block; width: 1px; height: 0px;'><span>Hg</span></div>");
		DOM.setElementProperty(block.getElement(), font, font);

		//HTML div = new HTML("<div></div>");
		//div.append(text, block);

		try
		{
			//var result = {};

			//block.css({ verticalAlign: 'baseline' });
			DOM.setStyleAttribute(block.getElement(), "verticalAlign", "baseline");

			ascent = DOM.getElementPropertyInt(block.getElement(), "offsetTop")
				- DOM.getElementPropertyInt(text.getElement(), "offsetTop");
		    //result.ascent = block.offset().top - text.offset().top;

		    //block.css({ verticalAlign: 'bottom' });
			DOM.setStyleAttribute(block.getElement(), "verticalAlign", "bottom");
			
			height = DOM.getElementPropertyInt(block.getElement(), "offsetTop")
				- DOM.getElementPropertyInt(text.getElement(), "offsetTop");
		    //result.height = block.offset().top - text.offset().top;

		    //result.descent = result.height - result.ascent;
			descent = height - ascent;

		} 
		finally
		{
		    while (DOM.getChildCount(block.getElement()) > 0) 
		    {
		    	DOM.removeChild(block.getElement(), DOM.getChild(block.getElement(), 0));
		    }
//		    div.remove();
		}

		  result = height;//ascent + descent;
//		  return result;
		  return 10;
	}

	/**
	 * Paint scale on the axis with the amounts or percentages,
	 * and the markers and grey help lines.
	 * 
	 * @param context
	 *            Context in which it will be painted
	 * @param scale
	 *            The multiplier used to make sure the bars fill the view
	 */
	private void paintScale(Context2d context, double scale,
		int ySplitOffset)
	{
		if (scale == 0)
		{
			return;
		}
		
		int height = this.getCurrentFontHeight(context);
		context.setFillStyle(ColorUtils.BLACK);
		TextMetrics metrics;

		// Determine the interval for markers on the axis
		int panelHeight = (int) ((this.model.hasVerticalBars() ? this
			.barAreaHeight() : this.barAreaWidth()));
		int base = 1;
		int exp = 0;
		int step = (int) (base * Math.pow(10, exp));
		while (step * 6 * scale < panelHeight) // hoezo 6?
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
			step = (int) (base * Math.pow(10, exp));
		}

		int minorStep;
		int minorStepsPerMajorStep;
		switch (base)
		{
    		case 5:
    			minorStep = (int) Math.pow(10, exp);
    			minorStepsPerMajorStep = 5;
    			break;
    		case 2:
    			minorStep = (int) (5 * Math.pow(10, exp - 1));
    			minorStepsPerMajorStep = 4;
    			break;
    		case 1:
    			minorStep = (int) (2 * Math.pow(10, exp - 1));
    			minorStepsPerMajorStep = 5;
    			break;
    		default:
    			minorStep = 1;
    			minorStepsPerMajorStep = step;
		}

		double majorSteps = (panelHeight - 0.5 * height)
			/ (step * scale);
		int majorStepsFloor = (int) Math.floor(majorSteps);

		if (this.model.hasVerticalBars())
		{
			// Paint the small markers
			for (int i = 0; i < majorSteps * minorStepsPerMajorStep; i++)
			{
				int y = this.barAreaHeight()
					- (int) (i * minorStep * scale);
				// paint light grey help line
				context.setStrokeStyle(ColorUtils.LIGHT_GREY);
				context.beginPath();
				context.moveTo(this.yAxisOffset, y + ySplitOffset);
				context.lineTo(this.barAreaWidth() + this.yAxisOffset, y + ySplitOffset);//this.getOffsetWidth(), y + ySplitOffset);
				context.closePath();
				context.stroke();

				// paint small marker
				context.setStrokeStyle(ColorUtils.BLACK);
				context.beginPath();
				context.moveTo(this.yAxisOffset - 2, y + ySplitOffset);
				context.lineTo(this.yAxisOffset, y + ySplitOffset);
				context.closePath();
				context.stroke();
			}

			// paint the large markers
			for (int i = 0; i < majorStepsFloor + 1; i++)
			{
				int y = this.barAreaHeight() - (int) (i * step * scale);
				// paint grey help line
				context.setStrokeStyle(ColorUtils.GREY);
				context.beginPath();
				context.moveTo(this.yAxisOffset, y + ySplitOffset);
				context.lineTo(this.barAreaWidth() + this.yAxisOffset, y + ySplitOffset);//this.getOffsetWidth(), y + ySplitOffset);
				context.closePath();
				context.stroke();

				// paint small marker
				context.setStrokeStyle(ColorUtils.BLACK);
				context.beginPath();
				context.moveTo(this.yAxisOffset - 5, y + ySplitOffset);
				context.lineTo(this.yAxisOffset, y + ySplitOffset);
				context.closePath();
				context.stroke();
			}

			// paint the markers' values
			for (int i = 0; i < majorStepsFloor + 1; i++)
			{
				int y = this.barAreaHeight() - (int) (i * step * scale);
				String s = new Integer(i * step).toString();
				if (this.model.getPercentage())
				{
					s = s + "%";
				}
				
				metrics = context.measureText(s);
				context.fillText(s, this.yAxisOffset - 7 - metrics.getWidth(), y
					+ (int) (height / 2.0) - 2 + ySplitOffset);
			}
		} // vertical bars
		else
		{ // horizontal bars
			int y = this.barAreaHeight();

			context.beginPath();
			// paint the small markers
			for (int i = 0; i < majorStepsFloor * minorStepsPerMajorStep; i++)
			{
				int x = this.yAxisOffset + (int) (i * minorStep * scale) - 1;
				// paint light grey help line
				context.setStrokeStyle(ColorUtils.LIGHT_GREY);
				context.beginPath();
				context.moveTo(x, ySplitOffset);
				context.lineTo(x, ySplitOffset + y);
				context.closePath();
				context.stroke();

				// paint small marker
				context.setStrokeStyle(ColorUtils.BLACK);
				context.beginPath();
				context.moveTo(x, y + ySplitOffset);
				context.lineTo(x, y + 2 + ySplitOffset);
				context.closePath();
				context.stroke();
			}

			// paint the large markers with their value
			for (int i = 0; i < majorStepsFloor + 1; i++)
			{
				int x = this.yAxisOffset + (int) (i * step * scale) - 1;
				// paint grey help line
				context.setStrokeStyle(ColorUtils.GREY);
				context.beginPath();
				context.moveTo(x, ySplitOffset);
				context.lineTo(x, ySplitOffset + y);
				context.closePath();
				context.stroke();

				// paint small marker
				context.setStrokeStyle(ColorUtils.BLACK);
				context.beginPath();
				context.moveTo(x, y + ySplitOffset);
				context.lineTo(x, y + 5 + ySplitOffset);
				context.closePath();
				context.stroke();
				
				String s = new Integer(i * step).toString();
				if (this.model.getPercentage())
				{
					s = s + "%";
				}
				
				metrics = context.measureText(s);
				context.fillText(s, x - (int) (metrics.getWidth() / 2.0),
					y + 7 + height + ySplitOffset);
			}
		} // horizontal bars
	}

	/**
	 * Paint the bars for enum or string data and the axis values and markers.
	 * 
	 * @param context
	 *            The graphics in which the bars will be painted
	 */
	private void paintEnumClass(Context2d context,
		FrequencyTuple[][] allFrequencies, int splitClass)
	{
		double availableSpace;
		double maxValueOnAxis;
		double scale;
		double amountScale;

		context.setStrokeStyle(ColorUtils.BLACK);
		TextMetrics metrics;
		int height = this.getCurrentFontHeight(context);
		double width;
		
		double theta = Math.PI * 1.75;

		// get frequencies
		FrequencyTuple[] frequencies = allFrequencies[splitClass];
		int ySplitOffset = splitClass * (this.getHeight() - 5);

		availableSpace = HistogramView.MAX_SCREEN_FRACTION_FOR_BARS *
        	(this.model.hasVerticalBars() ? 
        		this.barAreaHeight() : 
        		this.barAreaWidth()); // if vertical then availableSpace represents height, if horizontal then width
		
		// determine max on scale
		double max = this.maxFrequencyOverAllSplits(allFrequencies);
		if (this.model.getPercentage()
			&& this.model.getPercentageSplitTotal()) // split total is 100%
		{
			maxValueOnAxis = this.maxPercentageOverAllSplits(allFrequencies);
		}
		else if (this.model.getPercentage()) // end total is 100%
		{
			maxValueOnAxis = 100.0 * max / (this.getFrequenciesSum(allFrequencies)); 
		}
		else
		{
			maxValueOnAxis = max;
		}
		
		// determine scale for the axis with percentages or amounts; should be the same for splits in multiple views
		if (maxValueOnAxis == 0)
		{
			scale = 0;
		}
		else
		{
			scale =  availableSpace/maxValueOnAxis;
		}

		// set bar width
		this.setBarWidth(frequencies.length);

		if (this.model.hasVerticalBars())
		{
			// check if the bin boundary strings will fit
			boolean normalFit = true;
			double longest = 0;
			int highest = 0;
			for (FrequencyTuple ft : frequencies)
			{
				metrics = context.measureText(ft.label);
				width = metrics.getWidth();

				if (width > this.verticalBarWidth)
				{
					normalFit = false;
				}
				if (width > longest)
				{
					longest = width;
				}
			}

			if (normalFit)
			{
				this.xAxisOffset = 40;
			}
			else
			{
				this.xAxisOffset = (int) (longest + 5 + height);
			}

			this.yAxisOffset = this.determineDependentAxisWidth(context, scale) + 20;
		}
		else
		{ // horizontal bars
			this.xAxisOffset = this.determineDependentAxisWidth(context, scale);

			// find longest binboundary
			double longest = 0;
			for (FrequencyTuple ft : frequencies)
			{
				metrics = context.measureText(ft.label);
				width = metrics.getWidth();
				if (width > longest)
				{
					longest = width;
				}
			}

			this.yAxisOffset = (int) (longest + 5 + height);
		}

		// ---- correct scales ---- 
		// bepaal de amountscale om de bar-lengtes per split te kunnen berekenen
		if (this.model.getPercentage())
		{
			if (this.model.getStatTableModel().numberOfSplitVarClasses(
				this.model.getSplitOptions()) > 1)
			{
				// er is een split
				if (this.model.isSplitInSingleView())
				{
					if (this.model.isFrequencyPolygonMode()
						&& this.model.isFrequencyPolygonCumulativeMode())
					{
						maxValueOnAxis = 100.0;
					}
					else
					{
						int maxInSplit = 0;
						double maxFraction = 0;
						for (int i = 0; i < allFrequencies.length; i++)
						{
							maxInSplit = this.maxFrequency(allFrequencies, i);
							max = Math.max(max, maxInSplit);
							maxFraction = Math.max((double)maxInSplit/this.getFrequenciesSum(allFrequencies), maxFraction);
						}
						
						maxValueOnAxis = 100.0 * maxFraction;
					}

					scale = availableSpace / maxValueOnAxis;
				} // split in single view
				else
				{ // split in multiple views of geen split
					if (this.model.getPercentageSplitTotal())
					{
						// amountScale = de beschikbare ruimte (die toont maxValueOnScale) gedeeld door de frequentie (in de huidige splitClass) die deze maxValueOnScale-waarde geeft.
						// Let op: voor split total = 100% is max dus de frequentie (in de huidige splitClass) die deze maxValueOnScale-waarde geeft
						// maar dat is niet perse een frequentie die voorkomt in de huidige splitClass. Het is daarom een decimale waarde.
						max = maxValueOnAxis * this.getFrequenciesSum(allFrequencies, splitClass) / 100;
					}
				}
			}
		} // percentage
		else
		{ // aantallen
			if (this.model.getStatTableModel().numberOfSplitVarClasses(
				this.model.getSplitOptions()) > 1)
			{
				// er is een split
				if (this.model.isSplitInSingleView())
				{
					int maxInSplit = 0;
					for (int split = 0; split < allFrequencies.length; split++)
					{
						maxInSplit = Math.max(maxInSplit,
							this.maxFrequency(allFrequencies, split));
						max = Math.max(max, maxInSplit);
					}

					maxValueOnAxis = maxInSplit;
					max = maxInSplit;

					if (maxValueOnAxis == 0)
					{
						scale = 0;
					}
					else
					{
						scale = availableSpace / maxValueOnAxis;
					}
				} // split in single view
			}
		}

		// PAINT AMOUNT SCALE
		this.paintScale(context, scale, ySplitOffset);

		// PAINT BARS
		// use amountScale to paint the correct bar length
		amountScale = availableSpace/max;

		ArrayList<CssColor> splitColors = new ArrayList<CssColor>();
		ArrayList<String> splitLabels = new ArrayList<String>();

		if (this.model.isSplitInSingleView()
			&& this.model.getStatTableModel().numberOfSplitVarClasses(
				this.model.getSplitOptions()) > 1)
		{
			if (this.model.isFrequencyPolygonMode()
				&& this.model.isFrequencyPolygonCumulativeMode()
				&& this.model.isFrequencyPolygonStackMode())
			{
				int[] totalFrequencies = new int[frequencies.length];
				int[] frequencySum = new int[frequencies.length];
				for (int split = 0; split < allFrequencies.length; split++)
				{
					FrequencyTuple[] splitFreq = allFrequencies[split];
					this.lastPolygonPoint = null;
					CssColor c = this.getColor(split);
					for (int i = 0; i < splitFreq.length; i++)
					{
						if (i > 0)
						{
							frequencySum[i] = frequencySum[i - 1]
								+ splitFreq[i].frequency;
						}
						else
						{
							frequencySum[i] = splitFreq[i].frequency;
						}

						this.fillCumulativeFreqPolygonSegment(
							context,
							(int) Math.round(amountScale
								* (totalFrequencies[i] + frequencySum[i])),
							(int) Math.round(amountScale * totalFrequencies[i]),
							(int) Math.round(amountScale
								* (i > 0 ? frequencySum[i - 1]
									+ totalFrequencies[i - 1] : 0)),
							(int) Math.round(amountScale
								* (i > 0 ? totalFrequencies[i - 1] : 0)), i, c);
					}
					for (int i = 0; i < frequencySum.length; i++)
					{
						totalFrequencies[i] += frequencySum[i];
					}
				}
			} // frequentiepolygoon, cumulatief, stapelen
			else
			{
				// frequentiepolygoon, cumulatief, split in single view
				if (this.model.isFrequencyPolygonMode()
					&& this.model.isFrequencyPolygonCumulativeMode())
				{
					// Paint the frequency polygon as dots and lines 
					for (int split = 0; split < allFrequencies.length; split++)
					{
						if (this.model.getPercentage())
						{
							amountScale = availableSpace/this.maxFrequency(allFrequencies[split]);
						}
						else
						{ // aantal
							// bij aantal moet hij de max frequency over alle splits nemen
							amountScale = availableSpace/this.maxFrequencyOverAllSplits(allFrequencies);
						}
						
						FrequencyTuple[] splitFreq = allFrequencies[split];
						int frequencySum = 0;
						int frequencySelectedSum = 0;
						CssColor c = this.getColor(split);

						for (int i = 0; i < splitFreq.length; i++)
						{
							frequencySum += splitFreq[i].frequency;
							frequencySelectedSum = splitFreq[i].selectionFrequency;
							this.paintBar(
								context,
								(int) (frequencySum * amountScale),
								(int) (frequencySelectedSum * amountScale),
								i, 0, 0, c, split,
								allFrequencies.length, true, split);
						}
						this.lastPolygonPoint = null;
					}
				}
				else
				{
					int[] cumHeight = new int[frequencies.length];
					for (int split = 0; split < allFrequencies.length; split++)
					{
						FrequencyTuple[] splitFreq = allFrequencies[split];
						CssColor c = this.getColor(split);
						for (int i = 0; i < frequencies.length; i++)
						{
							if (this.model.hasVerticalBars())
							{
								if (this.isNextToEachOtherSelected())
								{
									this.paintBar(
										context,
										(int) (splitFreq[i].frequency * amountScale),
										(int) (splitFreq[i].selectionFrequency * amountScale),
										i, 0, 0, c, split,
										allFrequencies.length, true, split); // splitClass is altijd 0
								}
								else
								{
									this.paintBar(
										context,
										(int) (splitFreq[i].frequency * amountScale),
										(int) (splitFreq[i].selectionFrequency * amountScale),
										i, -cumHeight[i], 0, c, 0, 0, false, split); // splitClass is altijd 0
								}
							}
							else
							{
								if (this.isNextToEachOtherSelected())
								{
									this.paintBar(
										context,
										(int) (splitFreq[i].frequency * amountScale),
										(int) (splitFreq[i].selectionFrequency * amountScale),
										i, 0, 0, c, split,
										allFrequencies.length, true, split);
								}
								else
								{
									this.paintBar(
										context,
										(int) (splitFreq[i].frequency * amountScale),
										(int) (splitFreq[i].selectionFrequency * amountScale),
										i, 0, cumHeight[i], c, 0, 0, false, split);
								}
							}
							cumHeight[i] += (int) (splitFreq[i].frequency * amountScale);

						}
						this.lastPolygonPoint = null;
					}
				}
			}
		} // split in single view
		else
		{ // no split or split in multiple views
			if (this.model.isFrequencyPolygonMode()
				&& this.model.isFrequencyPolygonCumulativeMode())
			{
				if (this.model.getStatTableModel().numberOfSplitVarClasses(
					this.model.getSplitOptions()) > 1)
					{
						// split in multiple views
						CssColor c = this.getColor(splitClass);
						splitColors.add(c);
						splitLabels.add(this.model.getSplitOptions()
							.getSplitClassLabel(splitClass,
								this.model.getStatTableModel()));

						int frequencySum = 0;
						int frequencySelectedSum = 0;
						for (int i = 0; i < frequencies.length; i++)
						{
							frequencySum += frequencies[i].frequency;
							frequencySelectedSum = frequencies[i].selectionFrequency;
							this.paintBar(
								context,
								(int) (frequencySum * amountScale),
								(int) (frequencySelectedSum * amountScale),
								i, ySplitOffset, 0, c, splitClass,
								allFrequencies.length, true, splitClass);
						}
					}
					else // no split
					{
						int frequencySum = 0;
						int frequencySelectedSum = 0;
						for (int i = 0; i < frequencies.length; i++)
						{
							frequencySum += frequencies[i].frequency;
							frequencySelectedSum = frequencies[i].selectionFrequency;
							this.paintBar(
								context, 
								(int) (frequencySum * amountScale),
								(int) (frequencySelectedSum * amountScale), 
								i, ySplitOffset, 0, splitClass);
						}
					}

			}
			else
			{
				for (int i = 0; i < frequencies.length; i++)
				{
					if (allFrequencies.length > 1)
					{
						this.paintBar(
							context,
							(int) (frequencies[i].frequency * amountScale),
							(int) (frequencies[i].selectionFrequency * amountScale),
							i, ySplitOffset, 0, this.getColor(splitClass), 0,
							0, false, splitClass);
					}
					else
					{
						this.paintBar(
							context,
							(int) (frequencies[i].frequency * amountScale),
							(int) (frequencies[i].selectionFrequency * amountScale),
							i, ySplitOffset, 0, splitClass);
					}
				}
			}
		}

		// paint bar labels
		context.beginPath(); // required to push a new state
		context.setStrokeStyle(ColorUtils.BLACK);
		context.setFillStyle(ColorUtils.BLACK);
		if (this.model.hasVerticalBars())
		{
			int y = this.barAreaHeight() - 2;
			boolean normalFit = true;
			for (FrequencyTuple ft : frequencies)
			{
				metrics = context.measureText(ft.label);
				width = metrics.getWidth();
				if (width > this.verticalBarWidth)
				{
					normalFit = false;
					break;
				}
			}

			if (normalFit)
			{
				for (int i = 0; i < frequencies.length; i++)
				{
					int x = this.yAxisOffset
						+ (int) (((double) i + 0.5) * this.verticalBarWidth)
						+ i + 1;

					String s = frequencies[i].label;
					String font = context.getFont();
					metrics = context.measureText(s);
					context.fillText(s, x - (int) (metrics.getWidth() / 2.0), y + 5
						+ height + ySplitOffset);
				}
			} // normal fit
			else
			{
				// too wide, so rotate labels
				for (int i = 0; i < frequencies.length; i++)
				{
					int x = this.yAxisOffset
						+ (int) (((double) i + 0.5) * this.verticalBarWidth)
						+ i + 1;
					String s = frequencies[i].label;
					metrics = context.measureText(s);
					double offset_labelUnderBin = metrics.getWidth();
					int widthRotatedLabel = (int) (offset_labelUnderBin * Math.cos(theta));
					int heightRotatedLabel = (int) (offset_labelUnderBin * -Math.sin(theta));
					context.save();
					// set the drawing position 
					context.translate(
						x - widthRotatedLabel, 
						this.barAreaHeight() + 15 + heightRotatedLabel + ySplitOffset);
					context.rotate(theta);
					context.fillText(s, 0, 0); 
					context.restore();
				}
			}
		} // vertical bars
		else
		{ // horizontal bars
			for (int i = 0; i < frequencies.length; i++)
			{
				int y = i + (int) ((i + 1) * this.horizontalBarWidth);

				// check if the label will fit on the screen
				String s = frequencies[i].label;
				metrics = context.measureText(s);
				width = metrics.getWidth();
				
				if (width > this.yAxisOffset - 10)
				{
					// cut off the label to make it fit
					s = s + "...";
					while (width > this.yAxisOffset - 5 - height
						&& s.length() > 4)
					{
						s = s.substring(0, s.length() - 4) + "...";
					}
				}

				metrics = context.measureText(s);
				context.fillText(s, this.yAxisOffset - metrics.getWidth() - 3,
					(int) (y + (0.5 * height)) + ySplitOffset);
			}
		}
	}
	
	private int getTotalSumOfFrequencies(FrequencyTuple[][] allFrequencies)
	{
		int sum = 0;
		
		for (FrequencyTuple[] splitFreq : allFrequencies)
		{
			sum = sum + this.tupleArraySum(splitFreq);
		}

		return sum;
	}

	private int getTotalSumOfFrequencies(int[][] allFrequencies)
	{
		int sum = 0;
		
		for (int i = 0; i < allFrequencies.length; i++)
		{
			sum = sum + arrayEvenSum(allFrequencies[i]);
		}

		return sum;
	}

	private int getMaxFrequenciesofBins(FrequencyTuple[][] allFrequencies)
	{
		int max = 0;
		
		for (int i = 0; i < allFrequencies.length; i++)
		{
			max = Math.max(max, this.maxFrequency(allFrequencies, i));
		}
		
		return max;
	}
	
	private int getMaxFrequencyofBins(int[][] allFrequencies)
	{
		int max = 0;
		
		for (int i = 0; i < allFrequencies.length; i++)
		{
			max = Math.max(max, this.maxFrequency(allFrequencies, i));
		}

		return max;
	}

	private void setMainPanelSize()
	{
		int splitClasses = this.model.getStatTableModel().numberOfSplitVarClasses(
			this.model.getSplitOptions());
		int colorLegendWidth = this.colorLegend.isVisible() ? HistogramView.COLOR_LEGEND_WIDTH : 0;

		this.scrollPanel.setPixelSize(this.getWidth() - colorLegendWidth, this.getHeight());

		if (this.model.isSplitInSingleView())
		{
			this.mainPanel.getCanvas().setCoordinateSpaceWidth(this.getWidth() - colorLegendWidth);
			this.mainPanel.getCanvas().setCoordinateSpaceHeight(this.getHeight());
			
			this.scrollPanel.setAlwaysHideVerticalScrollBar(true);
		}
		else
		{
			this.mainPanel.getCanvas().setCoordinateSpaceWidth(this.getWidth());
			this.mainPanel.getCanvas().setCoordinateSpaceHeight(splitClasses * this.getHeight());
			
			if (splitClasses > 1)
			{
				this.scrollPanel.setAlwaysHideVerticalScrollBar(false);
			}
			else
			{
				this.scrollPanel.setAlwaysHideVerticalScrollBar(true);
			}
		}
	}

	// Override setBound
	public void setBounds(int x, int y, int w, int h)
	{
		this.setMainPanelSize();
	}

	/**
	 * Update and paint the view.
	 */
	public void update()
	{
		this.dialogButton.setVisible(this.model.getStatTableModel()
			.isViewsEditable());

		this.updateColorLegend();

		this.setMainPanelSize();

		this.userOptionsPanel.update();

		this.mainPanel.paint();
	}

	/**
	 * Updates the color legend
	 * 
	 * @return true if the visibility of the color legend changed
	 */
	private boolean updateColorLegend()
	{
		boolean visibilityHasChanged = false;
		
		int splitClasses = this.model.getStatTableModel().numberOfSplitVarClasses(
			this.model.getSplitOptions());
		
		if (splitClasses > 1 && this.model.isSplitInSingleView())
		{
			this.colorLegend.setColumnString(this.model.getStatTableModel()
				.getColumnName(
					this.model.getSplitOptions().getColumnSplitIndex()));
			ArrayList<String> splitStrings = new ArrayList<String>(splitClasses);
			ArrayList<String> splitColors = new ArrayList<String>(splitClasses);

			for (int i = 0; i < splitClasses; i++)
			{
				splitStrings.add(this.model.getSplitOptions()
					.getSplitClassLabel(i, this.model.getStatTableModel()));

				splitColors.add(this.getColor(i).value());
			}
			
			this.colorLegend.setColors(splitStrings, splitColors);

			if (!this.colorLegend.isVisible())
			{
				this.colorLegend.setVisible(true);
				this.alles.setWidgetHidden(this.colorLegend, false);
				visibilityHasChanged = true;
			}
			else
			{
				visibilityHasChanged = false;
			}
		}
		else
		{
			// bij geen split of split in 1 view geen colorlegend tonen
			if (this.colorLegend.isVisible())
			{
				this.colorLegend.setVisible(false);
				this.alles.setWidgetHidden(this.colorLegend, true);
				visibilityHasChanged = true;
			}
			else
			{
				visibilityHasChanged = false;
			}
		}
		
		this.forceLayout();
		
		return visibilityHasChanged;
	}

	private static int identityHashCode(Object o)
	{
		return System.identityHashCode(o);
	}
	
	
	/**
	 * Class HistogramBarPanel 
	 * @author Sylvia van Borkulo
	 *
	 */
	private class HistogramBarPanel //implements MouseMotionListener
	{
		private Canvas canvas;
		private Context2d context;
		private HistogramBarMouseMoveHandler mouseMoveHandler;
		
		public HistogramBarPanel()
		{
			this.canvas = Canvas.createIfSupported();
			this.canvas.addStyleName(statistiekCss.canvas());
			mouseMoveHandler = new HistogramBarMouseMoveHandler(); 
			this.canvas.addMouseMoveHandler(mouseMoveHandler);
			this.canvas.addClickHandler(new BarClickHandler());
			this.canvas.addDomHandler(dummyTouchHandler, TouchStartEvent.getType());
			this.canvas.addDomHandler(dummyTouchHandler, TouchEndEvent.getType());
			this.context = canvas.getContext2d();
		}
		
		public Canvas getCanvas()
		{
			return this.canvas;
		}
		
		public HistogramBarMouseMoveHandler getMouseMoveHandler()
		{
			return this.mouseMoveHandler;
		}
		
		public void paint()
		{
			// clear panel
			this.context = canvas.getContext2d();
			context.clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
			
			HistogramView.this.lastPolygonPoint = null;

			// clear locations of bars
			HistogramView.this.barRectangles = new ArrayList<Rectangle>(
				HistogramView.this.model.getNoBins()
					* HistogramView.this.model.getStatTableModel().numberOfSplitVarClasses(
						HistogramView.this.model.getSplitOptions()));

			if (!HistogramView.this.model.columnIndexValid())
			{
				return;
			}

			// get the data type
			ArrayList<ColumnType> list = HistogramView.this.model.getStatTableModel().getColumnTypes();
			AllowedTypes type = list.get(HistogramView.this.model.getColumnIndex()).getType();

			int splitClasses = HistogramView.this.model.getStatTableModel()
				.numberOfSplitVarClasses(HistogramView.this.model.getSplitOptions());

			if (HistogramView.this.model.isSplitInSingleView())
			{
				if (type.isNumber())
				{
					int[][] frequencies = HistogramView.this.model
						.numberClassFrequency();
					if (frequencies != null)
					{
						HistogramView.this.paintNumberClass(context, frequencies, 0);
					}
				}
				else
				{
					FrequencyTuple[][] frequencies = HistogramView.this.model
						.enumClassFrequency();
					HistogramView.this.paintEnumClass(context, frequencies, 0);
				}
			}
			else
			{
				// call the right paint method
				if (type.equals(AllowedTypes.ENUM)
					|| type.equals(AllowedTypes.STRING))
				{
					FrequencyTuple[][] frequencies = HistogramView.this.model
						.enumClassFrequency();
					for (int splitClass = 0; splitClass < splitClasses; splitClass++)
					{
						HistogramView.this.lastPolygonPoint = null;
						HistogramView.this.paintEnumClass(context, frequencies,
							splitClass);
					}
				}
				else
				{
					int[][] frequencies = HistogramView.this.model
						.numberClassFrequency();
					for (int splitClass = 0; splitClass < splitClasses; splitClass++)
					{
						HistogramView.this.lastPolygonPoint = null;
						HistogramView.this.paintNumberClass(context, frequencies,
							splitClass);
					}
				}
			}

			context.setStrokeStyle(ColorUtils.BLACK);
			// draw the axis for each split with the text labels (for vertical bars the x axis, for horizontal bars the y axis)
			for (int i = 0; i < (HistogramView.this.isSplitSingleViewSelected() ? 1
				: splitClasses); i++)
			{
				int ySplitOffset = i
					* (HistogramView.this.getHeight() - 5);

				if (HistogramView.this.model.hasVerticalBars())
				{
					if (canvas.getCoordinateSpaceWidth() > 0)
					{
						// x axis
						context.moveTo(HistogramView.this.yAxisOffset,
							HistogramView.this.barAreaHeight() + ySplitOffset);
						context.lineTo(HistogramView.this.barAreaWidth() + HistogramView.this.yAxisOffset,
							HistogramView.this.barAreaHeight() + ySplitOffset);
						context.stroke();
					}
				}
				else
				{
					// y axis
					context.moveTo(HistogramView.this.yAxisOffset - 1,
						ySplitOffset - 1);
					context.lineTo(HistogramView.this.yAxisOffset - 1,
						HistogramView.this.barAreaHeight() + ySplitOffset);
					context.stroke();
				}
				HistogramView.this.paintAxisLabels(context, ySplitOffset, i);
			}
			
		} // paint()
		
	} // class HistogramBarPanel

	private class HistogramBarMouseMoveHandler implements MouseMoveHandler
	{
		private PopupPanel popup = new PopupPanel(true);
		private int x = 0;
		private int y = 0;
		private int clientX = 0;
		private int clientY = 0;
		
		@Override
		public void onMouseMove(MouseMoveEvent e)
		{
			// Method mouseMoved() implements showing tooltip & highlight
			
			x = e.getX();
			y = e.getY();
			clientX = e.getClientX();
			clientY = e.getClientY();
			
			FrequencyTuple[][] frequencies_enum = HistogramView.this.model
				.enumClassFrequency();
			int[][] frequencies_number = HistogramView.this.model
				.numberClassFrequency();
			boolean isPercentage = HistogramView.this.model.getPercentage();
			int[] aantalPerSplit = null;
			int[] aantalPerBin = null;
			int aantal_totaal = 0;
			boolean found = false;

			int noBins = 0;
			int numberOfSplits = HistogramView.this.model.getStatTableModel()
				.numberOfSplitVarClasses(HistogramView.this.model.getSplitOptions());

			if (frequencies_number != null)
			{
				noBins = HistogramView.this.model.getNoBins();
			}
			else if (frequencies_enum != null)
			{
				noBins = frequencies_enum[0].length;
			}

			aantalPerSplit = new int[numberOfSplits];
			aantalPerBin = new int[noBins];

			if (isPercentage)
			{
				if (frequencies_number != null) // number variable
				{
					aantal_totaal = HistogramView.this.getFrequenciesSum(frequencies_number);
				}
				else if (frequencies_enum != null) // enum variable
				{
					aantal_totaal = HistogramView.this.getFrequenciesSum(frequencies_enum);
				}
				
				// bepaal aantal per bin
				for (int j = 0; j < noBins; j++)
				{
					// tel de aantallen op voor bin j
					
					aantalPerBin[j] = 0;
					if (frequencies_number != null) // number variable
					{
    					for (int i = 0; i < numberOfSplits; i++)
    					{
    						aantalPerBin[j] = aantalPerBin[j]
    							+ frequencies_number[i][j * 2];
    					}
					}
					else if (frequencies_enum != null) // enum variable
					{
						for (int i = 0; i < numberOfSplits; i++)
						{
    						aantalPerBin[j] = aantalPerBin[j]
								+ frequencies_enum[i][j].frequency;
						}
					}
				}
				
				// bepaal aantal per split
				for (int i = 0; i < numberOfSplits; i++)
				{
					if (frequencies_number != null) // number variable
					{
    					for (int j = 0; j < noBins; j++)
    					{
    						aantalPerSplit[i] = aantalPerSplit[i]
    							+ frequencies_number[i][j * 2];
    					}
					}
					else if (frequencies_enum != null) // enum variable
					{
						for (int j = 0; j < noBins; j++)
						{
    						aantalPerSplit[i] = aantalPerSplit[i]
								+ frequencies_enum[i][j].frequency;
						}
					}    					
				}
			} // percentage
			
			Rectangle rect;
			double value = 0;
			double[] cumulativeValuePerSplit = new double[numberOfSplits];
			for (int i = 0; i < numberOfSplits && !found; i++)
			{
				for (int j = 0; j < noBins && !found; j++) // j <
														   // HistogramView.this.barRectangles.size()
				{
					rect = HistogramView.this.barRectangles.get(j + i * noBins);
					
					if (HistogramView.this.model.isFrequencyPolygonCumulativeMode())
					{
						// calculate value and add to cumulativeValue
						if (isPercentage)
						{
							if (frequencies_number != null)
							{
								if (HistogramView.this.hasSplit())
								{
									if (HistogramView.this.model.getPercentageSplitTotal())
									{
										value = ((double) frequencies_number[i][j * 2] / aantalPerSplit[i]) * 100;
									}
									else
									{
										value = ((double) frequencies_number[i][j * 2] / aantal_totaal) * 100;
									}

								}
								else // geen split
								{
    								// percentage t.o.v. totaal
        							value = ((double) frequencies_number[i][j * 2] / aantal_totaal) * 100; 
								}
							}
							else if (frequencies_enum != null)
							{
								if (HistogramView.this.hasSplit())
								{
            						value = ((double) frequencies_enum[i][j].frequency / aantalPerSplit[i]) * 100;
								}
								else // geen split
								{
    								// percentage berekenen t.o.v. totaal
        							value = ((double) frequencies_enum[i][j].frequency / aantal_totaal) * 100;
								}
							}
							
							if (Double.isNaN(value) || Double.isInfinite(value))
							{
								value = 0;
							}
						} // isPercentage
						else
						{
							if (frequencies_number != null)
							{
								value = frequencies_number[i][j * 2];
							}
							else if (frequencies_enum != null)
							{
								value = frequencies_enum[i][j].frequency;
							}
						}
						
						cumulativeValuePerSplit[i] += value;
						
					} // isFrequencyPolygonCumulativeMode()

					if (isOverToolTipArea(x, y, rect))
					{
						if (isPercentage)
						{
							value = 0;
							
							if (frequencies_number != null)
							{
								if (HistogramView.this.hasSplit())
								{
									if (HistogramView.this.model.isSplitInSingleView())
        							{
										if (HistogramView.this.isNextToEachOtherSelected())
    									{
    										// als naast elkaar, dan percentage relatief aan totaalaantal
    										value = ((double) frequencies_number[i][j*2] / aantal_totaal) * 100;
    									}
    									else
    									{
            								// als gestapeld in 1 view, dan percentage relatief aan totaal per bin 
                							//value = ((double) frequencies_number[i][j * 2] / aantalPerBin[j]) * 100;
    										// percentage relatief aan totaalaantal
    										//value = ((double) frequencies_number[i][j*2] / aantal_totaal) * 100;
    										
    										// bij gestapeld moet de waarde relatief aan totaal 
    										// en tooltip de waarde aan de as
    										for (int k = 0; k <= i; k++)
    										{
    											value = value + ((double) frequencies_number[k][j*2] / aantal_totaal) * 100;
    										}
    									}
        							}
        							else if (HistogramView.this.model.getPercentageSplitTotal())
        							{ 
        								// split in meerdere views en percentage per split relatief aan split
            							value = ((double) frequencies_number[i][j * 2] / aantalPerSplit[i]) * 100;
        							}
        							else
        							{
        								// split met meerdere views en percentage per split relatief aan totaal
        								value = ((double) frequencies_number[i][j*2] / aantal_totaal) * 100;
        							}
								}
								else // geen split
								{
    								// percentage t.o.v. totaal
        							value = ((double) frequencies_number[i][j * 2] / aantal_totaal) * 100; 
								}
							}
							else if (frequencies_enum != null)
							{
								if (HistogramView.this.hasSplit())
								{
									if (HistogramView.this.model.isSplitInSingleView())
    								{
    									if (HistogramView.this.isNextToEachOtherSelected())
    									{
    										// als naast elkaar, dan percentage relatief aan totaalaantal
    										value = ((double) frequencies_enum[i][j].frequency / aantal_totaal) * 100;
    									}
    									else
    									{
            								// als gestapeld in 1 view, dan percentage relatief aan totaal per bin 
                							//value = ((double) frequencies_enum[i][j].frequency / aantalPerBin[j]) * 100;
    										
    										// bij gestapeld moet de waarde relatief aan totaal 
    										// en tooltip de waarde aan de as
    										for (int k = 0; k <= i; k++)
    										{
    											value = value + ((double) frequencies_enum[k][j].frequency / aantal_totaal) * 100;
    										}
    									}
    								}
        							else if (HistogramView.this.model.getPercentageSplitTotal())
        							{
        								// split in meerdere views en percentage per split relatief aan split
            							value = ((double) frequencies_enum[i][j].frequency / aantalPerSplit[i]) * 100;
        							}
    								else
    								{
        								// split met meerdere views en percentage per split relatief aan totaal
            							value = ((double) frequencies_enum[i][j].frequency / aantal_totaal) * 100;
    								}
								}
								else // geen split
								{
    								// percentage berekenen t.o.v. totaal
        							value = ((double) frequencies_enum[i][j].frequency / aantal_totaal) * 100;
								}
							} // enum
						} // isPercentage
						else
						{
							value = 0;
							
							if (frequencies_number != null)
							{
								if (HistogramView.this.hasSplit()
										&& HistogramView.this.model.isSplitInSingleView()
										&& !HistogramView.this.isNextToEachOtherSelected())
								{
									// voor gestapeld toon tooltip van as-waarde
									for (int k = 0; k <= i; k++)
									{
										value = value + frequencies_number[k][j*2];
									}
								}
								else
								{
									value = frequencies_number[i][j * 2];
								}
							}
							else if (frequencies_enum != null)
							{
								if (HistogramView.this.hasSplit()
										&& HistogramView.this.model.isSplitInSingleView()
										&& !HistogramView.this.isNextToEachOtherSelected())
								{
									// voor gestapeld toon tooltip van as-waarde
									for (int k = 0; k <= i; k++)
									{
										value = value + frequencies_enum[k][j].frequency;
									}
								}
								else
								{
									value = frequencies_enum[i][j].frequency;
								}
							}
						}

						if (HistogramView.this.model.isFrequencyPolygonCumulativeMode())
						{
							value = cumulativeValuePerSplit[i]; 
						}
						
						// round to one decimal
						if (!Double.isNaN(value) && !Double.isInfinite(value))
						{
							value = StatistiekGWT.round(value, 1);
						}
						else
						{
							value = 0;
						}
						
						// Get valueString for showing tooltip text
						String valueString = "0";
						valueString = StatistiekGWT.getStringValue(value);
						
						if (!valueString.equals("0") || HistogramView.this.model.isFrequencyPolygonMode())
						{
							// For frequency polygon show tooltip and highlight for every dot
							
							highlightedBar = j;
							highlightInSplit = i;

							// show popup
							if (isPercentage)
							{
								valueString = valueString + "%";
								this.popup.setPopupPositionAndShow(
									new PopupPanel.PositionCallback()
									{
										public void setPosition(
											int offsetWidth, int offsetHeight)
										{
//											int scrollXPosition = HistogramView.this.scrollPanel.getHorizontalScrollPosition();
//											int scrollYPosition = HistogramView.this.scrollPanel.getVerticalScrollPosition();
											popup.setPopupPosition(clientX - offsetWidth, clientY - offsetHeight);
										}
									});
							}
							else
							{
								valueString = StatistiekGWT.rb.amountIs() + valueString;
								this.popup.setPopupPositionAndShow(
									new PopupPanel.PositionCallback()
									{
										public void setPosition(
											int offsetWidth, int offsetHeight)
										{
//											int scrollXPosition = HistogramView.this.scrollPanel.getHorizontalScrollPosition();
//											int scrollYPosition = HistogramView.this.scrollPanel.getVerticalScrollPosition();
											popup.setPopupPosition(clientX - offsetWidth, clientY - offsetHeight);// no correction needed?
										}
									});
							}
						}
						else
						{
							highlightedBar = -1;
							highlightInSplit = -1;
						}
						found = true; // gevonden
						this.popup.clear();
						this.popup.add(new Label(valueString));
					} // isOverToolTipArea()
					else
					{
						highlightedBar = -1;
						highlightInSplit = -1;
						this.popup.hide();
					}
				}
			} // for-loop
		} // onMouseMove()

		private boolean isOverToolTipArea(int x, int y, Rectangle rect)
		{
			boolean isOverToolTipArea = false;
			int marge = 5;
			
			if (HistogramView.this.model.isFrequencyPolygonMode())
			{
				if ((x > rect.x) && (x < (rect.x + rect.w))
					&& (y > rect.y) && (y < (rect.y + rect.h)))
				{
					isOverToolTipArea = true;
				}
			}
			else
			{
				if (HistogramView.this.model.hasVerticalBars())
				{
	    			if ((x > rect.x) && (x < (rect.x + rect.w))
	    			&& (y > rect.y - marge) && (y < rect.y + marge))
	    			{
	    				isOverToolTipArea = true;
	    			}
				}
				else // horizontal bars
				{
	    			if ((x > (rect.x + rect.w - marge)) 
	    				&& (x < (rect.x + rect.w + marge))
	    				&& (y > rect.y) && (y < (rect.y + rect.h)))
	    			{
	    				isOverToolTipArea = true;
	    			}				
				}
			}
			
			return isOverToolTipArea;
		} // isOverToolTipArea()

	} // class HistogramBarMouseMoveHandler

	private class BarClickHandler implements ClickHandler
	{

		@Override
		public void onClick(ClickEvent e)
		{
			if (HistogramView.this.model.isFrequencyPolygonMode())
			{
				return;
			}
			
			int i;
			
			double x = e.getX();//getClientX();
			double y = e.getY();//getClientY();
			Point p = new Point(x, y);

			for (i = 0; i < HistogramView.this.barRectangles.size(); i++)
			{
				Rectangle rect = HistogramView.this.barRectangles.get(i);
				
				if ((rect != null) && rect.contains(p))
				{
					this.barClicked(i);
					break;
				}
			}
			
			if (i == HistogramView.this.barRectangles.size())
			{
				// no bin was clicked, deselect all
				ArrayList<Boolean> selectionList = new ArrayList<Boolean>();
				for (int row = 0; row < HistogramView.this.model
					.getStatTableModel().getRowCount(); row++)
				{
					selectionList.add(false);
				}
				HistogramView.this.model.getStatTableModel().setSelectionList(
					selectionList);
			}
		}

		private void barClicked(int bar)
		{
			if (!HistogramView.this.model.columnIndexValid())
			{
				return;
			}

			int bins = HistogramView.this.model.getStatTableModel()
				.numberOfSplitVarClasses(HistogramView.this.model.getColumnIndex(),
					HistogramView.this.model.getBinBoundaries());
			int bin = bar % bins;
			int splitClass = bar / bins;

			ArrayList<ColumnType> list = HistogramView.this.model.getStatTableModel().getColumnTypes();
			ColumnType cType = list.get(HistogramView.this.model.getColumnIndex());
			AllowedTypes type = cType.getType();
			if (type.isNumber())
			{
				ArrayList<Boolean> selectionList = new ArrayList<Boolean>(
					HistogramView.this.model.getStatTableModel().getRowCount());
				for (int i = 0; i < HistogramView.this.model.getStatTableModel()
					.getRowCount(); i++)
				{
					Object o = HistogramView.this.model.getStatTableModel()
						.getValueAt(i,
							HistogramView.this.model.getColumnIndex());

					selectionList
						.add(!o.equals(ColumnType.WILDCARD)
							&& HistogramView.this.model.binOfNumber(Double
								.parseDouble((String) o)) == bin
							&& HistogramView.this.model.getStatTableModel()
								.classifyObject(i,
									HistogramView.this.model.getSplitOptions()) == splitClass);
				}

				HistogramView.this.model.getStatTableModel().setSelectionList(
					selectionList);
			} // number
			else
			{
				String clicked;
				if (type.equals(AllowedTypes.ENUM))
				{
					clicked = cType.getEnumOptions()[bin];
					int wildcardIndex = Arrays.asList(cType.getEnumOptions())
						.indexOf(ColumnType.WILDCARD);
					if (wildcardIndex <= 0 && wildcardIndex < bin)
					{
						clicked = cType.getEnumOptions()[bin + 1];
					}
				}
				else
				{
					// stringColumnOptions staan niet in alfabetische volgorde; neem enumClassFrequency
					FrequencyTuple[] freqTuple = HistogramView.this.model.enumClassFrequency()[splitClass];
					clicked = freqTuple[bin].label;
				}

				ArrayList<Boolean> selectionList = new ArrayList<Boolean>(
					HistogramView.this.model.getStatTableModel().getRowCount());
				for (int i = 0; i < HistogramView.this.model.getStatTableModel()
					.getRowCount(); i++)
				{
					Object o = HistogramView.this.model.getStatTableModel()
						.getValueAt(i,
							HistogramView.this.model.getColumnIndex());
					selectionList
						.add(!o.equals(ColumnType.WILDCARD)
							&& ((String) o).equals(clicked)
							&& HistogramView.this.model.getStatTableModel()
								.classifyObject(i,
									HistogramView.this.model.getSplitOptions()) == splitClass);
				}
				HistogramView.this.model.getStatTableModel().setSelectionList(
					selectionList);
			} // enum or string
			
			// view statTable moet updaten en de selectie laten zien -> gebeurt al door setSelectionList, die triggert een SelectionChangeEvent
		}
		
	} // private class BarClickListener
	
	private class Rectangle
	{
		private int x, y, w, h;
		
		Rectangle(int x, int y, int w, int h)
		{
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
		
		public boolean contains(Point p)
		{
			boolean inRectangle = false;
			
			if ((p.getX() >= this.x) 
				&& (p.getX() <= (this.x + this.w))
				&& (p.getY() >= this.y) 
				&& (p.getY() <= (this.y + this.h)))
			{
				inRectangle = true;
			}
			
			return inRectangle;
		}
	} // class Rectangle

	@Override
	public void onTableChange(TableChangeEvent event)
	{
		GWT.log("HistogramView.onTableChange()");

		if (!event.getInfo().equals(TableChangeEvent.ADD_ROW) // if add row do nothing
			&& !event.getInfo().equals(TableChangeEvent.SORT_COLUMN)) // if sort column do nothing
		{
			if (event.getInfo().equals(TableChangeEvent.ADD_COLUMN))
			{
				// only update user options panel
				this.userOptionsPanel.update();
			}
			else
			{
				boolean typeHasChanged = false;
				if (event.getInfo().equals(TableChangeEvent.EDIT_COLUMN))
				{
					typeHasChanged = true;
				}
				
				if (event.getInfo().equals(TableChangeEvent.REMOVE_ROW)
					|| event.getInfo().equals(TableChangeEvent.REMOVE_ROWS))
				{
					// verwijderde waarde kan bins veranderen:
					// bins opnieuw berekenen
					if (this.model.columnIndexValid() 
						&& !this.model.getStatTableModel().isEmptyColumn(this.model.getColumnIndex()))
					{
						this.recalculateBinBoundaries(typeHasChanged);
					}

					if (this.model.getSplitOptions().getColumnSplitIndex() > -1)
					{
						// er is een split
						
						// split bins opnieuw berekenen
						this.recalculateSplitBinBoundaries();
					}
				}
				else if (event.getInfo().equals(TableChangeEvent.SET_VALUE_AT)
					|| event.getInfo().equals(TableChangeEvent.EDIT_COLUMN))
				{
					if (event.getColumnIndex() == this.model.getColumnIndex())
					{
						if (this.model.columnIndexValid() 
							&& !this.model.getStatTableModel().isEmptyColumn(this.model.getColumnIndex()))
						{
							// bins opnieuw berekenen
							this.recalculateBinBoundaries(typeHasChanged);
						}
					}
					if (event.getColumnIndex() == this.model.getSplitOptions().getColumnSplitIndex())
					{
						// split bins opnieuw berekenen
						this.recalculateSplitBinBoundaries();
					}
				}
				else if (event.getInfo().equals(TableChangeEvent.REMOVE_COLUMN))
				{
					this.model.updateColumnIndex(event.getColumnIndex());
				}
				
				// update both view and user options panel
				this.update();
			}
		}
	}
	
	/**
	 * Recalculate the bin boundaries for histogram's columnIndex
	 * if possible.
	 * 
	 * @param typeHasChanged
	 * 		The type has changed yes/no.
	 */
	public void recalculateBinBoundaries(boolean typeHasChanged)
	{
			if (this.model.columnIndexValid())
			{
				ArrayList<ColumnType> list = this.model.getStatTableModel().getColumnTypes();
				ArrayList<Double> binBoundaries = null;
				if (list.get(this.model.getColumnIndex())
					.getType().isNumber())
				{
    				binBoundaries = StatistiekGWT.appropriateBoundariesFromBinSettings(
    					this.model.getStatTableModel().getColumnMin(this.model.getColumnIndex()),
    					this.model.getStatTableModel().getColumnMax(this.model.getColumnIndex()), 
    					this.getBinWidth(), this.getMinBoundary()); // met invoervelden
    				
    				if (binBoundaries == null) // ongeldige waarden in invoervelden
    				{
        				binBoundaries = StatistiekGWT.appropriateBoundaries(
    						this.model.getStatTableModel().getColumnMin(this.model.getColumnIndex()),
    						this.model.getStatTableModel().getColumnMax(this.model.getColumnIndex()),
    						this.model.getNoBins());
        				
        				int bin0Decimals = StatistiekGWT.getNumberOfDecimals(binBoundaries.get(0).toString());
        				int bin1Decimals = StatistiekGWT.getNumberOfDecimals(binBoundaries.get(1).toString());
        				int maxNumberOfDecimals = Math.max(bin0Decimals, bin1Decimals);

        				binBoundaries = StatistiekGWT.appropriateBoundariesFromBinSettings(
        					this.model.getStatTableModel().getColumnMin(this.model.getColumnIndex()),
        					this.model.getStatTableModel().getColumnMax(this.model.getColumnIndex()), 
        					StatistiekGWT.round(binBoundaries.get(1) - binBoundaries.get(0), maxNumberOfDecimals), binBoundaries.get(0));
    				}
    				
    				if (binBoundaries == null)
    				{
    					System.out.println("HistogramView.recalculateBinBoundaries(typeHasChanged = " + typeHasChanged + "): binBoundaries is null");
    				}
    				else
    				{
    					this.model.setBinBoundaries(binBoundaries);
    					this.model.setNoBinsWithoutUpdatingBinBoundaries(binBoundaries.size() - 1);
    					
    					// update minOnScale and maxOnScale if necessary
    					if (this.model.getMinOnScale() > binBoundaries.get(0))
    					{
    						this.model.setMinOnScale(binBoundaries.get(0));
    					}
    					int last = binBoundaries.size() - 1;
    					if (this.model.getMaxOnScale() <= binBoundaries.get(last))
    					{
    						this.model.setMaxOnScale(binBoundaries.get(last));
    					}
    				}
				}

				if (typeHasChanged)
				{
					// set bin label positioning
					AllowedTypes type = list.get(this.model.getColumnIndex()).getType(); 
					if (type.equals(AllowedTypes.INTEGER))
					{
						this.model.setLabelUnderBin(true);
					}
					else if (type.equals(AllowedTypes.DOUBLE))
					{
						this.model.setLabelUnderBin(false);
					}
				}
			}
	}
	
	/**
	 * Recalculate the split bin boundaries for column with columnSplitIndex
	 * if possible.
	 * 
	 * @param columnIndex
	 * 		The index of the column for which the bin
	 *      boundaries will be calculated.
	 * @param typeHasChanged
	 * 		The type has changed yes/no.
	 */
	public void recalculateSplitBinBoundaries()
	{
		if (this.model.columnIndexValid())
		{
			int splitIndex = this.model.getSplitOptions().getColumnSplitIndex();
			AllowedTypes splitType = this.model.getStatTableModel().getColumnTypes().get(splitIndex).getType();
			if (splitType.isNumber())
			{
				ArrayList<Double> boundaries = new ArrayList<Double>();
				boundaries = StatistiekGWT.appropriateBoundaries(
					this.model.getStatTableModel().getColumnMin(
						this.model.getSplitOptions().getColumnSplitIndex()),
					this.model.getStatTableModel().getColumnMax(
						this.model.getSplitOptions().getColumnSplitIndex()),
					this.getSplitBinsBoxSelectedInt());
	
				this.model.setSplitBoundaries(boundaries);
				this.model.setSplitOptions(this.model.getSplitOptions());
				this.setModel(this.model);
			}
		}
	}

	@Override
	public void fireEvent(GwtEvent<?> e)
	{
		eventBus.fireEvent(e);
	}

	/**
	 * Return true if the view has a split, else false.
	 * 
	 * @return whether the view has a split
	 */
	public boolean hasSplit()
	{
		boolean hasSplit = false;
		
		if (this.model.getSplitOptions().getColumnSplitIndex() > -1)
		{
			hasSplit = true;
		}
		
		return hasSplit;
	}
	
	/**
	 * Get the scroll panel in which histogram mainpanel is shown.
	 * 
	 * @return
	 */
	public ScrollPanel getScrollPanel()
	{
		return this.scrollPanel;
	}

	/**
	 * @return the userOptionsPanel
	 */
	public HistogramUserOptionsPanel getUserOptionsPanel()
	{
		return userOptionsPanel;
	}

	/**
	 * @param userOptionsPanel the userOptionsPanel to set
	 */
	public void setUserOptionsPanel(HistogramUserOptionsPanel userOptionsPanel)
	{
		this.userOptionsPanel = userOptionsPanel;
	}

	/**
	 * Subscribe for events
	 */
	public HandlerRegistration addViewSelectionChangeEventHandler(ViewSelectionChangeEventHandler handler)
	{
		return this.eventBus.addHandler(ViewSelectionChangeEvent.TYPE, handler);
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event)
	{
		GWT.log("HistogramView.onSelectionChange(): event.sender = " + event.getSenderName());
		if (!event.getSenderName().equals(this.controller.getViewName()))
		{
			this.update();
		}
	}
	
	/**
	 * Remove all of this view's handler occurrences.
	 */
	public void removeHandlers()
	{
		this.tableChangeEventHandlerRegistration.removeHandler();
		this.selectionChangeEventHandlerRegistration.removeHandler();
		this.outlierChangeEventHandlerRegistration.removeHandler();
	}
	
	/**
	 * Get the number of bin boundaries based on the settings in the user options panel.
	 * 
	 * @return
	 */
	private int getNumberOfBinsfromBinsSettings()
	{
		ArrayList<Double> binBoundaries = getBinsfromBinsSettings();
		int number = binBoundaries.size() - 1;

		return number;
	}

	@Override
	public void onOutlierChange(OutlierChangeEvent event)
	{
		this.update();
	}
}
