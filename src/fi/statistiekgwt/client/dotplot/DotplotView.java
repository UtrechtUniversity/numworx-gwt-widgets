package fi.statistiekgwt.client.dotplot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import fi.statistiekgwt.client.ColorUtils;
import fi.statistiekgwt.client.ColorLegend;
import fi.statistiekgwt.client.ColorPreviewer;
import fi.statistiekgwt.client.DialogButton;
import fi.statistiekgwt.client.StatTableModel;
import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;
import fi.statistiekgwt.client.StatistiekUtils;
import fi.statistiekgwt.client.StatistiekUtils.DummyTouchHandler;
import fi.statistiekgwt.client.StatistiekUtils.CustomScrollPanel;
import fi.statistiekgwt.client.event.ColorChangeEvent;
import fi.statistiekgwt.client.event.ColorChangeEventHandler;
import fi.statistiekgwt.client.event.SelectionChangeEvent;
import fi.statistiekgwt.client.event.SelectionChangeEventHandler;
import fi.statistiekgwt.client.event.TableChangeEvent;
import fi.statistiekgwt.client.event.TableChangeEventHandler;
import fi.statistiekgwt.client.event.ViewSelectionChangeEvent;
import fi.statistiekgwt.client.event.ViewSelectionChangeEventHandler;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * MVC View for StatistiekView Dotplot
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class DotplotView extends LayoutPanel implements 
	TableChangeEventHandler, SelectionChangeEventHandler, HasHandlers, ColorChangeEventHandler
{
	private DotplotModel model;
	private DotplotController controller;
	private DotplotUserOptionsPanel userOptionsPanel;
	private DialogButton dialogButton;

	// Constants
	public static final int X_AS_OFFSET = 25;
	private static final int FONT_HEIGHT = 10;
	/**
	 * The part below the minimum value and above the maximum value respectively 
	 * that serves to create extra space. For example, 0.05 is 5% of the 
	 * space available for drawing the dots. 
	 */
	public static final double KEEP_CLEAR_PART = 0.05;
	public static final CssColor SELECTION_RECTANGLE_COLOR = 
		CssColor.make(153, 204, 255); // blue
	public static final CssColor SELECTION_RECTANGLE_COLOR_TRANSPARENT = 
		CssColor.make("rgba(153, 204, 255, 0.5)"); // blue
	public static final int COLOR_LEGEND_WIDTH = 125;
	
	// Table with critical values of the Pearson's product-moment 
	// correlation coefficient. 
	// See also, method getLevelOfSignificance(r, N).
	// Column 4: if r > SIGNIFICANCE_TABLE[n][3] then p < 0.001
	// Column 3: if r > SIGNIFICANCE_TABLE[n][2] then p < 0.01
	// Column 2: if r > SIGNIFICANCE_TABLE[n][1] then p < 0.05
	// Column 1: if r > SIGNIFICANCE_TABLE[n][0] then p < 0.1
	//			 if r < SIGNIFICANCE_TABLE[n][0] then p > 0.1
	// Source: http://faculty.fortlewis.edu/CHEW_B/Documents/Table%20of%20critical%20values%20for%20Pearson%20correlation.htm
	private static final double[][] SIGNIFICANCE_TABLE = {
		{0.988, 0.997, 0.9999, 0.99999}, // N = 3 (i = 0)
		{0.900, 0.950, 0.990, 0.999}, // N = 4 (i = 1)
		{0.805, 0.878, 0.959, 0.991}, // N = 5 (i = 2)
		{0.729, 0.811, 0.917, 0.974}, // N = 6 (i = 3)
		{0.669, 0.754, 0.875, 0.951}, // N = 7 (i = 4)
		{0.621, 0.707, 0.834, 0.925}, // N = 8 (i = 5)
		{0.582, 0.666, 0.798, 0.898}, // N = 9 (i = 6)
		{0.549, 0.632, 0.765, 0.872}, // N = 10 (i = 7)
		{0.521, 0.602, 0.735, 0.847}, // N = 11 (i = 8)
		{0.497, 0.576, 0.708, 0.823}, // N = 12 (i = 9)
		{0.476, 0.553, 0.684, 0.801}, // N = 13 (i = 10)
		{0.458, 0.532, 0.661, 0.780}, // N = 14 (i = 11)
		{0.441, 0.514, 0.641, 0.760}, // N = 15 (i = 12)
		{0.426, 0.497, 0.623, 0.742}, // N = 16 (i = 13)
		{0.412, 0.482, 0.606, 0.725}, // N = 17 (i = 14)
		{0.400, 0.468, 0.590, 0.708}, // N = 18 (i = 15)
		{0.389, 0.456, 0.575, 0.693}, // N = 19 (i = 16)
		{0.378, 0.444, 0.561, 0.679}, // N = 20 (i = 17)
		{0.369, 0.433, 0.549, 0.665}, // N = 21 (i = 18)
		{0.360, 0.423, 0.537, 0.652}, // N = 22 (i = 19)
		{0.352, 0.413, 0.526, 0.640}, // N = 23 (i = 20)
		{0.344, 0.404, 0.515, 0.629}, // N = 24 (i = 21)
		{0.337, 0.396, 0.505, 0.618}, // N = 25 (i = 22)
		{0.330, 0.388, 0.496, 0.607}, // N = 26 (i = 23)
		{0.323, 0.381, 0.487, 0.597}, // N = 27 (i = 24)
		{0.317, 0.374, 0.479, 0.588}, // N = 28 (i = 25)
		{0.311, 0.367, 0.471, 0.579}, // N = 29 (i = 26)
		{0.306, 0.361, 0.463, 0.570}, // N = 30 (i = 27)
		{0.283, 0.334, 0.430, 0.532}, // N = 35 (i = 28)
		{0.264, 0.312, 0.403, 0.501}, // N = 40 (i = 29)
		{0.248, 0.294, 0.380, 0.474}, // N = 45 (i = 30)
		{0.235, 0.279, 0.361, 0.451}, // N = 50 (i = 31)
		{0.214, 0.254, 0.330, 0.414}, // N = 60 (i = 32)
		{0.198, 0.235, 0.306, 0.385}, // N = 70 (i = 33)
		{0.185, 0.220, 0.286, 0.361}, // N = 80 (i = 34)
		{0.174, 0.207, 0.270, 0.341}, // N = 90 (i = 35)
		{0.165, 0.197, 0.256, 0.324}, // N = 100 (i = 36)
		{0.117, 0.139, 0.182, 0.231}, // N = 200 (i = 37)
		{0.095, 0.113, 0.149, 0.189}, // N = 300 (i = 38)
		{0.082, 0.098, 0.129, 0.164}, // N = 400 (i = 39)
		{0.074, 0.088, 0.115, 0.147}, // N = 500 (i = 40)
		{0.052, 0.062, 0.081, 0.104} // N = 1000 (i = 41)
	};

	public int yAxisOffset = 55;
	/**
	 * Correction used to determine the x coordinate of a number value.
	 */
	private int xCoordCorrection = 0;

	// variables for painting, set every time paint is called
	private double xMin;
	private double xMax;
	private double xFirstMinorStep; // field to be used in determineXCoordNumClass()
	private double yMin;
	private double yMax;
	private double zMin;
	private double zMax;
	private double splitMin;
	private double splitMax;
	/**
	 * Dot diameter is the diameter of the dot,
	 * i.e., twice the radius of the dot. 
	 */
	private int dotRadius;
	private AllowedTypes xType;
	private AllowedTypes yType;
	private AllowedTypes zType;
	private AllowedTypes splitType;

	private ArrayList<Point> objectLocations;
	private int splitClasses;
	
	/**
	 * Op panel 'alles' staan mainpanel, colorlegend and dialogbutton.
	 */
	private DockLayoutPanel alles;
	private DotPanel mainPanel;
	private CustomScrollPanel scrollPanel;
	private ColorLegend colorLegend;
	
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
	
	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;

	/**
	 * Constructor
	 * 
	 * @param model
	 *            MVC Model
	 * @param controller
	 *            MVC Controller
	 */
	public DotplotView(DotplotModel model, DotplotController controller)
	{
		super();
		
		this.alles = new DockLayoutPanel(Unit.PX);
		
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();
		
		this.model = model;
		this.controller = controller;

		this.dummyTouchHandler = StatistiekUtils.getDummyTouchHandler();
		this.eventBus = StatistiekUtils.EVENT_BUS;
		
		// bind dotplotview to stattablemodel: to handle table changes in stattablemodel
		this.tableChangeEventHandlerRegistration = this.model.getStatTableModel().addTableChangeEventHandler(this);//addObserver(this);

		// bind dotplotview to stattablemodel: to handle selection changes in stattablemodel
		this.selectionChangeEventHandlerRegistration = this.model.getStatTableModel().addSelectionChangeEventHandler(this);
		
		// create GUI
		this.mainPanel = new DotPanel();
		
		this.scrollPanel = new CustomScrollPanel(this.mainPanel.getCanvas());
		this.scrollPanel.setAlwaysHideHorizontalScrollBar(true);
		
		// initialize types
		this.setTypes();
		
		userOptionsPanel = new DotplotUserOptionsPanel(this, controller, model);
		// initial update for setting widgets in user options panel
		this.userOptionsPanel.update();

		this.initializeSize();
		
		this.colorLegend = new ColorLegend("", null, null, DotplotView.COLOR_LEGEND_WIDTH, this.getHeight());
		this.alles.addEast(this.colorLegend, DotplotView.COLOR_LEGEND_WIDTH);
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
		
		// add alles to dotplotview (layoutpanel)
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
	 * Subscribe for events
	 */
	public HandlerRegistration addViewSelectionChangeEventHandler(ViewSelectionChangeEventHandler handler)
	{
		return this.eventBus.addHandler(ViewSelectionChangeEvent.TYPE, handler);
	}

	/**
	 * Get the string options of column X.
	 * @return
	 */
	private ArrayList<String> getXStringOptions()
	{
		return this.model.getStatTableModel().getStringOptions(
			this.model.getColumnXIndex());
	}

	/**
	 * Get the string options of column Y.
	 * @return
	 */
	private ArrayList<String> getYStringOptions()
	{
		return this.model.getStatTableModel().getStringOptions(
			this.model.getColumnYIndex());
	}

	private ArrayList<String> getColorStringOptions()
	{
		return this.model.getStatTableModel().getStringOptions(
			this.model.getColumnColorIndex());
	}

	private ArrayList<String> getSplitStringOptions()
	{
		return this.model.getStatTableModel().getStringOptions(
			this.model.getColumnSplitIndex());
	}

	/**
	 * Determine the width of the area where the dots are painted
	 * 
	 * @return the width of the area where the dots are painted
	 */
	private int dotAreaWidth()
	{
		int w = this.getWidth() - 20
			- (this.colorLegend.isVisible() ? DotplotView.COLOR_LEGEND_WIDTH : 0);
		
		if (this.model.columnYIndexValid())
		{
			w -= this.yAxisOffset;
		}
		if (this.splitClasses > 1)
		{
			w -= 20;
		}
		
		return w;
	}

	/**
	 * Determine the height of the area where the dots are painted
	 * 
	 * @return the height of the area where the dots are painted
	 */
	private int dotAreaHeight()
	{
		int h = this.getHeight();

		if (this.model.columnXIndexValid())
		{
			h -= DotplotView.X_AS_OFFSET;
		}
		
		return h;
	}

	public void setModel(DotplotModel model)
	{
		this.model = model;
		userOptionsPanel.setModel(model);
		this.update();
		this.userOptionsPanel.update();
	}

	public void setBounds(int x, int y, int w, int h)
	{
		this.setMainPanelSize();
	}

	private void setMainPanelSize()
	{
		int splitClasses = this.model.getStatTableModel().splitVarClasses(
			this.model.getSplitOptions());
		int colorLegendWidth = this.getColorLegendWidth();
		
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
	
	/**
	 * Get the width of the color legend. If color legend is not visible, 
	 * 0 is returned.
	 * 
	 * @return
	 */
	private int getColorLegendWidth()
	{
		int width = this.colorLegend.isVisible() ? DotplotView.COLOR_LEGEND_WIDTH : 0;
		
		return width;
	}

//	private int determinePreferredHeight()
//	{
//		int splitClasses = this.model.getStatTableModel().splitVarClasses(
//			this.model.getSplitOptions());
//		int preferredHeight = 0;
//
//		if (this.model.splitInSingleView())
//		{
//			preferredHeight = DotplotView.this.getHeight() - 5;
//		}
//		else
//		{
//			preferredHeight = splitClasses * (DotplotView.this.getHeight() - 5) + 1;
//		}
//		return preferredHeight;
//	}

	private boolean updateColorLegend()
	{
		boolean visibilityHasChanged = false;

		int splitClasses = this.model.getStatTableModel().splitVarClasses(
			this.model.getSplitOptions());
		if (splitClasses > 1)
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
				splitColors.add(ColorUtils.getColor(i).value());
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

	public void update()
	{
		this.setTypes();
		this.setMinMax();

		if (this.model.columnSplitIndexValid())
		{
			this.setSplitType();
			splitClasses = this.numberOfSplitClasses();
		}
		else
		{
			splitClasses = 1;
		}

		this.dialogButton.setVisible(this.model.getStatTableModel()
			.isViewsEditable());

		// updateColorLegend() retourneert boolean, maar voert ook update uit
		this.updateColorLegend();
		this.setMainPanelSize();

		this.userOptionsPanel.update();
		
		updateOffsets();
		
		this.mainPanel.paint();
	}
	
	/**
	 * Update the correlation setting in the view.
	 * 
	 */
	public void updateCorrelation()
	{
		this.mainPanel.paintCorrelation();
	}

	/**
	 * Update y-axis offsets depending on the labels of the y variable.
	 */
	private void updateOffsets()
	{
		Canvas canvas = Canvas.createIfSupported();
		Context2d context = canvas.getContext2d();
		TextMetrics metrics;
		
		int max = 0;
		int yIndex = this.model.getColumnYIndex();
		
		// update offset y-axis
		if (yIndex > -1)
		{
			ColumnType columnType = this.model.getStatTableModel().getColumnTypes()
				.get(yIndex);
			
			if (yType.equals(AllowedTypes.ENUM))
			{
				// determine max width of the enum option labels
				for (int i = 0; i < columnType.getEnumOptions().length; i++)
				{
					String option = columnType.getEnumOptions()[i];
					metrics = context.measureText(option);
					
					if (metrics.getWidth() > max)
					{
						max = (int) (metrics.getWidth() + 45); // + 45 for label, tick-width and some extra space
					}
					
					// test syl: bovenstaande werkt, maar + 45 is willekeurig. Om een of andere reden is er voor
					// langere enum-klassen-labels meer ruimte nodig dan voor kortere...
				}
			}
			else if (yType.equals(AllowedTypes.STRING))
			{
				// determine max width of the string option labels
				int amountOfOptions = this.getYStringOptions().size();

				// draw ticks and help line
				for (int i = 0; i < amountOfOptions; i++)
				{
					String option = this.getYStringOptions().get(i);
					metrics = context.measureText(option);

					if (metrics.getWidth() > max)
					{
						max = (int) (metrics.getWidth() + 45); // + 45 for label, tick-width and some extra space
					}
					
					// test syl: bovenstaande werkt, maar + 45 is willekeurig. Om een of andere reden is er voor
					// langere enum-klassen-labels meer ruimte nodig dan voor kortere...
				}
			}
			else
			{
				max = 55;
			}
		} // there is a y-axis variable
		else
		{
			max = 55;
		}

		this.yAxisOffset = max;
	}

	public boolean isSplitSingleViewSelected()
	{
		return userOptionsPanel.isSplitSingleViewSelected();
	}

	public boolean getUseColorScaleBoxSelected()
	{
		return userOptionsPanel.isUseColorScaleBoxSelected();
	}

	public int getVarXBoxSelected()
	{
		return userOptionsPanel.getVarXBoxSelectedIndex();
	}

	public int getVarYBoxSelected()
	{
		return userOptionsPanel.getVarYBoxSelectedIndex();
	}

	public int getVarColorBoxSelected()
	{
		return userOptionsPanel.getVarColorBoxSelectedIndex();
	}

	public int getSplitVarBoxSelectedIndex()
	{
		return userOptionsPanel.getSplitVarBoxSelectedIndex();
	}

	public int getSplitBinsBoxSelectedInt()
	{
		return userOptionsPanel.getSplitBinsBoxSelectedInt();
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

	public double getSplitBinWidth()
	{
		return userOptionsPanel.getSplitBinWidth();
	}

	public void setSplitBinWidth(double d)
	{
		this.userOptionsPanel.setSplitBinWidth(d);
	}

	public void setSplitBinWidth()
	{
		this.userOptionsPanel.setSplitBinWidth();
	}

	public boolean getShowCorrelationBoxSelected()
	{
		return userOptionsPanel.isShowCorrelationBoxSelected();
	}

	/**
	 * Sets the current columnTypes to thix.xType, this.yType and this.zType
	 */
	private void setTypes()
	{
		if (this.model.columnXIndexValid())
		{
			this.xType = this.model.getStatTableModel().getColumnTypes()
				.get(this.model.getColumnXIndex()).getType();
		}
		if (this.model.columnYIndexValid())
		{
			this.yType = this.model.getStatTableModel().getColumnTypes()
				.get(this.model.getColumnYIndex()).getType();
		}

		if(this.model.isUseColorScale() && this.model.columnColorIndexValid())
		{
			this.zType = this.model.getStatTableModel().getColumnTypes()
				.get(this.model.getColumnColorIndex()).getType();
		}

		if (this.model.columnSplitIndexValid())
		{
			this.setSplitType();
		}
	}

	private void setSplitType()
	{
		this.splitType = this.model.getStatTableModel().getColumnTypes()
			.get(this.model.getColumnSplitIndex()).getType();
	}

	/**
	 * Find the current min and max value for every numeric column
	 */
	private void setMinMax()
	{
		if (this.model.columnXIndexValid() && this.xType.isNumber())
		{
			xMin = this.model.getStatTableModel().getColumnMin(
				this.model.getColumnXIndex());
			xMax = this.model.getStatTableModel().getColumnMax(
				this.model.getColumnXIndex());
		}
		if (this.model.columnYIndexValid() && this.yType.isNumber())
		{
			yMin = this.model.getStatTableModel().getColumnMin(
				this.model.getColumnYIndex());
			yMax = this.model.getStatTableModel().getColumnMax(
				this.model.getColumnYIndex());
		}
		if(this.model.isUseColorScale() && this.model.columnColorIndexValid() && this.zType.isNumber())
		{
			zMin = this.model.getStatTableModel().getColumnMin(
				this.model.getColumnColorIndex());
			zMax = this.model.getStatTableModel().getColumnMax(
				this.model.getColumnColorIndex());
		}
		if (this.model.columnSplitIndexValid() && this.splitType.isNumber())
		{
			splitMin = this.model.getStatTableModel().getColumnMin(
				this.model.getColumnSplitIndex());
			splitMax = this.model.getStatTableModel().getColumnMax(
				this.model.getColumnSplitIndex());
		}
	}

	/**
	 * Calculate how large the dots should be
	 */
	private void determineDotSize()
	{
		double d = (this.getWidth() * this.getHeight())
			/ (double) Math.max(10, this.model.getStatTableModel().getRowCount());
		this.dotRadius = Math.max(2, (int) (0.25 * Math.pow(d, 1.0 / 3.0)));
	}

	/**
	 * Determine what color the dot representing an object should be.
	 * 
	 * @param rowIndex
	 *            The index of the object
	 * @return The color in which the dot representing the object will be
	 *         painted
	 */
	private CssColor determineColor(int rowIndex)
	{
		if (this.model.columnSplitIndexValid())
		{
			String s = (String) this.model.getStatTableModel().getValueAt(
				rowIndex, this.model.getColumnSplitIndex());
			if (s.equals(ColumnType.WILDCARD))
			{
				return ColorUtils.getWildCardColor();
			}
			else
			{
				if (this.splitType.equals(AllowedTypes.DOUBLE)
					|| this.splitType.equals(AllowedTypes.INTEGER))
				{
					// determine color for numeric type objects
					return ColorUtils.getColor(getSplitClass(rowIndex));
				}
				else if (this.splitType.equals(AllowedTypes.ENUM))
				{
					// determine color for enum type objects
					ColumnType columnType = this.model.getStatTableModel()
						.getColumnTypes().get(this.model.getColumnSplitIndex());
					int index = columnType.indexOfStringInEnum(s);
					
					if (columnType.indexOfStringInEnum(ColumnType.WILDCARD) < index)
					{
						index--;
					}
					
					return ColorUtils.getColor(index);
				}
				else
				{
					// determine color for string type objects
					int index = this.getSplitStringOptions().indexOf(s);
					
					return ColorUtils.getColor(index);
				}
			}
		} // columnSplitIndexValid()

		else if (this.model.isUseColorScale() && this.model.columnColorIndexValid())//this.model.isUseColorScale()
		{
			String s = (String) this.model.getStatTableModel().getValueAt(
				rowIndex, this.model.getColumnColorIndex());
			if (s.equals(ColumnType.WILDCARD))
			{
				return ColorUtils.getWildCardColor();
			}
			else
			{
				if (this.zType.equals(AllowedTypes.DOUBLE)
					|| this.zType.equals(AllowedTypes.INTEGER))
				{
					// determine color for numeric type objects
					double value = Double.parseDouble(s);
					
					return ColorPreviewer.mixColors(
						ColorUtils.getRGBColor(this.model.getColorA()),
						ColorUtils.getRGBColor(this.model.getColorB()), 
						(value - this.zMin) / (this.zMax - this.zMin)).getCssColor();
				}
				else if (this.zType.equals(AllowedTypes.ENUM))
				{
					// determine color for enum type objects
					ColumnType columnType = this.model.getStatTableModel()
						.getColumnTypes().get(this.model.getColumnColorIndex());
					int index = columnType.indexOfStringInEnum(s);
					if (columnType.indexOfStringInEnum(ColumnType.WILDCARD) < index)
					{
						index--;
					}
					
					return ColorPreviewer.mixColors(
						ColorUtils.getRGBColor(this.model.getColorA()),
						ColorUtils.getRGBColor(this.model.getColorB()),
						(double) index / (double) (columnType.getEnumOptions().length - 2)).getCssColor();
				}
				else
				{
					// determine color for string type objects
					int index = this.getColorStringOptions().indexOf(s);
					return ColorPreviewer.mixColors(
						ColorUtils.getRGBColor(this.model.getColorA()),
						ColorUtils.getRGBColor(this.model.getColorB()),
						(double) index / (double) (this.getColorStringOptions().size() - 1)).getCssColor();
				}
			}
		}
		
		// if not using a colorscale, the color is the default dot color
		return ColorUtils.DEFAULT_VIEW_ELEMENT_COLOR;
	}

	/**
	 * Determine the x coordinate of a dot representing an object
	 * in the dotplot or scatterplot.
	 * 
	 * @param pointIndex
	 *            the index of the object represented by the dot
	 * @return The x-coordinate of the location where the dot has to be painted
	 */
	private int determineXCoord(int pointIndex)
	{
		String valueString = (String) this.model.getStatTableModel().getValueAt(
			pointIndex, this.model.getColumnXIndex());
		if (valueString.equals(ColumnType.WILDCARD))
		{
			return -1;
		}

		if (this.xType == null)
		{
			System.out.println("xType is null");
		}

		if (this.xType.equals(AllowedTypes.DOUBLE)
			|| this.xType.equals(AllowedTypes.INTEGER))
		{
			double d = Double.parseDouble(valueString);
			return this.determineXCoordNumClass(d);
		}
		else if (this.xType.equals(AllowedTypes.ENUM))
		{
			return this.determineXCoordEnumClass(valueString);
		}
		else
		{
			return this.determineXCoordStringClass(valueString);
		}
	}

	/**
	 * Determine the x coordinate of a numeric value in the dotplot or scatterplot.
	 * 
	 * @param d
	 *            a numeric value
	 * @return the x-coordinate of where the value would be painted
	 */
	private int determineXCoordNumClass(double d)
	{		
		int drawWidth = this.dotAreaWidth();

		// determine the minimum value on the scale
		double minValue;
		
		// if possible take the first minor step
		if (!Double.isNaN(this.xFirstMinorStep))
		{
			minValue = this.xFirstMinorStep;
		}
		else
		{
			minValue = this.xMin;
		}
		
		int x = (int) ((DotplotView.KEEP_CLEAR_PART + (1 - 2 * DotplotView.KEEP_CLEAR_PART)
			* ((d - minValue) / (xMax - minValue))) * drawWidth);
	
		if (this.model.columnYIndexValid())
		{
			if (x < 0)
			{
				// The x coordinate should be in the dot area (x >= 0), else make a correction to shift x.
				// This correction is applied in determining the x coordinate for all values
				this.xCoordCorrection = -x;
			}
			x = x + this.yAxisOffset + this.xCoordCorrection;
		}

		return x;
	}

	/**
	 * Determine where an enum value would be painted in the scatterplot
	 * 
	 * @param value
	 *            the enum value
	 * @return the x-coordinate of where the value would be painted
	 */
	private int determineXCoordEnumClass(String value)
	{
		ColumnType cType = this.model.getStatTableModel().getColumnTypes()
			.get(this.model.getColumnXIndex());

		if (cType.getEnumOptions().length == 2) // including '*' which is not shown, so 1 class
		{
			// the middle of the field
			if (this.model.columnYIndexValid())
			{
				return this.yAxisOffset
					+ (this.mainPanel.getCanvas().getCoordinateSpaceWidth() - this.yAxisOffset) / 2;
			}
			else
			{
				return this.mainPanel.getCanvas().getCoordinateSpaceWidth() / 2;
			}
		}
		
		int index = cType.indexOfStringInEnum(value);

		double d = (double) (index + 1) / (double) cType.getEnumOptions().length;

		int x = 0;

		if (this.model.columnYIndexValid())
		{
			x = (int) (d * (this.mainPanel.getCanvas().getCoordinateSpaceWidth() - this.yAxisOffset)) + this.yAxisOffset;
		}
		else
		{
			x = (int) (d * this.mainPanel.getCanvas().getCoordinateSpaceWidth());
		}

		return x;
	}

	/**
	 * Determine where a String value would be painted in the scatterplot.
	 * 
	 * @param d
	 *            a String value
	 * @return the x-coordinate of where the value would be painted
	 */
	private int determineXCoordStringClass(String value)
	{
		if (this.getXStringOptions().size() == 1)
		{
			if (this.model.columnYIndexValid())
			{
				return this.yAxisOffset
					+ (this.mainPanel.getCanvas().getCoordinateSpaceWidth() - this.yAxisOffset) / 2;
			}
			else
			{
				return this.mainPanel.getCanvas().getCoordinateSpaceWidth() / 2;
			}
		}
		
		double d = (double) (this.getXStringOptions().indexOf(value) + 1)
			/ (double) (this.getXStringOptions().size() + 1);
		
		int x = 0;

		if (this.model.columnYIndexValid())
		{
			x = (int) (d * (this.mainPanel.getCanvas().getCoordinateSpaceWidth() - this.yAxisOffset)) + this.yAxisOffset;
		}
		else
		{
			x = (int) (d * this.mainPanel.getCanvas().getCoordinateSpaceWidth());
		}
		
		return x;
	}

	/**
	 * Determine the y coordinate of a dot representing an object
	 * in the dotplot or scatterplot.
	 * 
	 * @param pointIndex
	 *            the index of the object represented by the dot
	 * @return The y-coordinate of the location where the dot has to be painted
	 */
	private int determineYCoord(int pointIndex)
	{
		String valueString = (String) this.model.getStatTableModel().getValueAt(
			pointIndex, this.model.getColumnYIndex());

		if (valueString.equals(ColumnType.WILDCARD))
		{
			return -1;
		}

		if (this.yType.equals(AllowedTypes.DOUBLE)
			|| this.yType.equals(AllowedTypes.INTEGER))
		{
			double d = Double.parseDouble(valueString);

			return this.determineYCoordNumClass(d);
		}
		else if (this.yType.equals(AllowedTypes.ENUM))
		{
			return this.determineYCoordEnumClass(valueString);

		}
		else
		{
			return this.determineYCoordStringClass(valueString);
		}
	}

	/**
	 * Determine where a numeric value would be painted in the scatterplot.
	 * 
	 * @param d
	 *            a numeric value
	 * @return the y-coordinate of where the value would be painted
	 */
	private int determineYCoordNumClass(double d)
	{
		int drawHeight = this.dotAreaHeight();
		
		return (int) (DotplotView.KEEP_CLEAR_PART * drawHeight + (1 - (d - yMin)
			/ (yMax - yMin))
			* (1 - 2 * DotplotView.KEEP_CLEAR_PART) * drawHeight);
	}

	/**
	 * Determine where an enum value would be painted in the scatterplot.
	 * 
	 * @param value
	 *            the enum value
	 * @return the y-coordinate of where the value would be painted
	 */
	private int determineYCoordEnumClass(String value)
	{
		ColumnType cType = this.model.getStatTableModel().getColumnTypes()
			.get(this.model.getColumnYIndex());
		int drawHeight = this.dotAreaHeight();

		if (cType.getEnumOptions().length == 2)
		{
			return drawHeight / 2;
		}

		int index = cType.indexOfStringInEnum(value);
		
		if (cType.indexOfStringInEnum(ColumnType.WILDCARD) < index)
		{
			index--;
		}
		
		double d = (double) index
			/ (double) (cType.getEnumOptions().length - 2);
		
		return (int) (DotplotView.KEEP_CLEAR_PART * drawHeight + (1 - d)
			* (1 - 2 * DotplotView.KEEP_CLEAR_PART) * drawHeight);
	}

	/**
	 * Determine where a String value would be painted in the scatterplot
	 * 
	 * @param value
	 *            the String value
	 * @return the y-coordinate of where the value would be painted
	 */
	private int determineYCoordStringClass(String value)
	{
		int drawHeight = this.dotAreaHeight();

		if (this.getYStringOptions().size() == 1)
		{
			return drawHeight / 2;
		}
		
		int index = this.getYStringOptions().indexOf(value);
		double d = (double) index
			/ (double) (this.getYStringOptions().size() - 1);
		
		return (int) (DotplotView.KEEP_CLEAR_PART * drawHeight + (1 - d)
			* (1 - 2 * DotplotView.KEEP_CLEAR_PART) * drawHeight);
	}

	/**
	 * Paint a single point
	 * 
	 * @param context
	 *            the context in which the point will be painted
	 * @param rowIndex
	 *            The index of the object that will be painted
	 */
	private void drawPoint(Context2d context, int rowIndex)
	{
		// Determine painting location
		int x = this.determineXCoord(rowIndex);
		int y = this.determineYCoord(rowIndex);
		int splitClass = this.getSplitClass(rowIndex);

		if (x < 0 || y < 0 || splitClass < 0)
		{
			return;
		}
		else
		{
			if (this.model.columnSplitIndexValid())
			{
				if (this.model.splitInSingleView())
				{
					y += 0;
				}
				else
				{
					y += (splitClass) * (this.getHeight() - 5);
				}
			}
			drawPointAtLocation(context, x, y, rowIndex);
		}
	}

	private void drawPoint(Context2d context, int rowIndex, CssColor c)
	{
		// Determine painting location
		int x = this.determineXCoord(rowIndex);
		int y = this.determineYCoord(rowIndex);
		int splitClass = this.getSplitClass(rowIndex);

		if (x < 0 || y < 0 || splitClass < 0)
		{
			return;
		}
		else
		{
			if (this.model.columnSplitIndexValid())
			{
				y += (splitClass) * (this.getHeight() - 5);
			}
			c = ColorUtils.getColor(splitClass);
			drawPointAtLocation(context, x, y, rowIndex, c);
		}
	}

	/**
	 * Draw a single point (dot) at a given location.
	 * 
	 * @param context
	 *            the graphics in which the point will be painted
	 * @param x
	 *            the x-coordinate of the painting location
	 * @param y
	 *            the y-coordinate of the painting location
	 * @param rowIndex
	 *            the index of the object that will be painted
	 */
	private void drawPointAtLocation(Context2d context, int x, int y, int rowIndex)
	{
		// determine the color of the dot depending on the split class
		CssColor c = this.determineColor(rowIndex);
		
		// draw the point with the given color
		this.drawPointAtLocation(context, x, y, rowIndex, c);
	}

	/**
	 * Draw a single non-selected point (dot) at a given location.
	 * 
	 * @param context
	 *            the graphics in which the point will be painted
	 * @param x
	 *            the x-coordinate of the painting location
	 * @param y
	 *            the y-coordinate of the painting location
	 * @param rowIndex
	 *            the index of the object that will be painted
	 */
	private void drawNonSelectedPointAtLocation(Context2d context, int x, int y, int rowIndex)
	{
		// determine the color of the dot depending on the split class
		CssColor c = this.determineColor(rowIndex);
		
		// draw the point with the given color
		this.drawNonSelectedPointAtLocation(context, x, y, rowIndex, c);
	}

	/**
	 * Draw a single selected point (dot) at a given location.
	 * 
	 * @param context
	 *            the graphics in which the point will be painted
	 * @param x
	 *            the x-coordinate of the painting location
	 * @param y
	 *            the y-coordinate of the painting location
	 * @param rowIndex
	 *            the index of the object that will be painted
	 */
	private void drawSelectedPointAtLocation(Context2d context, int x, int y, int rowIndex)
	{
		// determine the color of the dot depending on the split class
		CssColor c = this.determineColor(rowIndex);
		
		// draw the point with the given color
		this.drawSelectedPointAtLocation(context, x, y, rowIndex, c);
	}

	/**
	 * Draw a single point (dot) at a given location with the given color.
	 * 
	 * @param context
	 * @param x
	 * @param y
	 * @param rowIndex
	 * @param c
	 */
	private void drawPointAtLocation(Context2d context, int x, int y, int rowIndex,
		CssColor c)
	{
		double alpha;
		CssColor transparentColor;

		if (this.model.getStatTableModel().isRowSelected(rowIndex))
		{
			// set non transparent for selected dot
			alpha = 1;
			transparentColor = CssColor.make("rgba(" 
				+ ColorUtils.getRed(c) + ", " 
				+ ColorUtils.getGreen(c) + "," 
				+ ColorUtils.getBlue(c) + ", " 
				+ alpha + ")");
		}
		else
		{
			// set default transparency
			alpha = 0.5;
			transparentColor = CssColor.make("rgba(" 
				+ ColorUtils.getRed(c) + ", " 
				+ ColorUtils.getGreen(c) + "," 
				+ ColorUtils.getBlue(c) + ", " 
				+ alpha + ")");
		}

		context.setFillStyle(transparentColor);
		context.beginPath();
		context.arc(x, y, this.dotRadius, 0, Math.PI * 2.0, true);
		context.closePath();
		context.fill();
		if (this.model.getStatTableModel().isRowSelected(rowIndex))
		{
			context.setLineWidth(2);
			context.setStrokeStyle(ColorUtils.BLACK);
			context.stroke();
			context.setLineWidth(1);
		}

		this.objectLocations.set(rowIndex, new Point(x, y));
	}

	/**
	 * Draw a single non-selected point (dot) at a given location with the given color.
	 * 
	 * @param context
	 * @param x
	 * @param y
	 * @param rowIndex
	 * @param c
	 */
	private void drawNonSelectedPointAtLocation(Context2d context, int x, int y, int rowIndex,
		CssColor c)
	{
		context.beginPath();
		context.arc(x, y, this.dotRadius, 0, Math.PI * 2.0, true);
		context.closePath();
		context.fill();

		this.objectLocations.set(rowIndex, new Point(x, y));
	}

	/**
	 * Draw a single selected point (dot) at a given location with the given color.
	 * 
	 * @param context
	 * @param x
	 * @param y
	 * @param rowIndex
	 * @param c
	 */
	private void drawSelectedPointAtLocation(Context2d context, int x, int y, int rowIndex,
		CssColor c)
	{
		context.beginPath();
		context.arc(x, y, this.dotRadius, 0, Math.PI * 2.0, true);
		context.closePath();
		context.fill();
		context.stroke();

		this.objectLocations.set(rowIndex, new Point(x, y));
	}

	/**
	 * Paint the axis text labels and split class labels if applicable.
	 * 
	 * @param context
	 * @param yOffset
	 * @param splitClass
	 */
	private void paintAxisLabels(Context2d context, int yOffset, int splitClass)
	{
		TextMetrics metrics;

		context.setFillStyle(ColorUtils.BLACK);

		if (this.model.columnYIndexValid())
		{
			String columnNameY = this.model.getStatTableModel().getColumnName(
				this.model.getColumnYIndex());
			
			double theta = Math.PI * 1.5;

			// draw label y-axis rotated
			metrics = context.measureText(columnNameY);
			// set the painting position
			context.save();
			context.translate(DotplotView.FONT_HEIGHT - 2, 
				this.getHeight() / 2 + metrics.getWidth() / 2 + yOffset); // the desired position of the text
			context.rotate(theta);
			context.fillText(columnNameY, 0, 0);
			context.restore();
		}
		
		if (this.model.columnXIndexValid())
		{
			String columnNameX = this.model.getStatTableModel().getColumnName(
				this.model.getColumnXIndex());
			
			// draw column name label x-axis
			metrics = context.measureText(columnNameX);
			context.fillText(columnNameX,
				(this.dotAreaWidth() - this.yAxisOffset - metrics.getWidth()) / 2
					+ this.yAxisOffset, 
					this.getHeight() - 2 + yOffset);
		}

		// draw split variable class (e.g., "geslacht: m")
		if (this.model.getStatTableModel().splitVarClasses(
			this.model.getSplitOptions()) > 1
			&& !this.model.splitInSingleView())
		{
			String columnNameSplit = this.model.getStatTableModel().getColumnName(
				this.model.getSplitOptions().getColumnSplitIndex());
			String splitClassLabel = this.model.getSplitOptions().getSplitClassLabel(splitClass,
				this.model.getStatTableModel());
			String s = columnNameSplit
				+ ": " + splitClassLabel;
			context.fillText(s, 10, 
				this.getHeight() - 2 + yOffset);
		}

		// draw horizontal axis line
//		context.fillRect(0, this.scrollPanel.getOffsetHeight() + DotplotView.X_AS_OFFSET - 28
//			+ yOffset, this.scrollPanel.getOffsetWidth(), 1);
//		context.fill();
	}

	/**
	 * Paint the x-axis
	 * 
	 * @param context
	 *            The graphics in which the x-axis will be painted
	 */
	private void paintXAxis(Context2d context, int heightOffset)
	{
		TextMetrics metrics;

		context.setStrokeStyle(ColorUtils.BLACK);
		context.setFillStyle(ColorUtils.BLACK);
		
		// theta for rotation
		double theta = Math.PI * 1.95;

		ColumnType columnType = this.model.getStatTableModel().getColumnTypes()
			.get(this.model.getColumnXIndex());

		int y = this.getHeight() - DotplotView.X_AS_OFFSET;
		
		// draw x-axis
		context.beginPath();
		if (this.model.columnYIndexValid())
		{
			context.moveTo(this.yAxisOffset, y + heightOffset);
			context.lineTo(this.getWidth(), y + heightOffset);
		}
		else
		{
			context.moveTo(0, y + heightOffset);
			context.lineTo(this.getWidth(), y + heightOffset);
		}
		context.stroke();
		context.closePath();

		if (this.xType.equals(AllowedTypes.ENUM))
		{
			boolean normalFit = this.determineNormalFitForEnum(context);
			// draw ticks and help lines, and labels
			for (int i = 0; i < columnType.getEnumOptions().length; i++)
			{
				String option = columnType.getEnumOptions()[i];
				if (option.equals(ColumnType.WILDCARD))
				{
					continue;
				}
				int x = this.determineXCoordEnumClass(option);
				
				context.beginPath();
				context.moveTo(x, y + heightOffset);
				context.lineTo(x, y + heightOffset + 5);
				context.stroke();
				context.closePath();
				
				// draw help line
				drawHelpLine(context, x, heightOffset + 5, y - 5, 0);
			    
				metrics = context.measureText(option);
				if (!normalFit)
				{
					context.save();
					// set the painting position
					context.translate(x - (int) (0.5 * metrics.getWidth()),
						y + heightOffset + 5 + DotplotView.FONT_HEIGHT); // the desired position of the text
					context.rotate(theta);
					context.fillText(option, 0, 0);
					context.restore();
				}
				else
				{
					context.fillText(option, x - (int) (0.5 * metrics.getWidth()),
						y + heightOffset + 5 + DotplotView.FONT_HEIGHT);					
				}
			}

		} // enum
		else if (this.xType.equals(AllowedTypes.STRING))
		{
			boolean normalFit = this.determineNormalFitForString(context);

			int amountOfOptions = this.getXStringOptions().size();

			// draw ticks and help lines
			for (int i = 0; i < amountOfOptions; i++)
			{
				String option = this.getXStringOptions().get(i);
				int x = this.determineXCoordStringClass(option);

				context.beginPath();
				context.moveTo(x, y + heightOffset);
				context.lineTo(x, y + heightOffset + 5);
				context.stroke();
				context.closePath();
				
				// draw help line
				drawHelpLine(context, x, heightOffset + 5, y - 5, 0);
			    
				metrics = context.measureText(option);
				if (!normalFit)
				{
					context.save();
					// set the painting position
					context.translate(x - (int) (0.5 * metrics.getWidth()),
						y + 5 + DotplotView.FONT_HEIGHT + heightOffset); // the desired position of the text
					context.rotate(theta);
					context.fillText(option, 0, 0);
					context.restore();
				}
				else
				{
					context.fillText(option, x - (int) (0.5 * metrics.getWidth()),
						y + 5 + DotplotView.FONT_HEIGHT + heightOffset);
				}
			}
		} // string
		else
		{
			// xType is int or double

			int base = 1;
			int exp = (int) Math.log10(this.xMax - this.xMin) - 1;
			int step = (int) (base * Math.pow(10, exp));
			while ((this.xMax - this.xMin) / step > 8)
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

			double minorStep;
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
//					minorStep = (int) (2 * Math.pow(10, exp - 1));
					minorStep = 2 * Math.pow(10, exp - 1);
					minorStepsPerMajorStep = 5;
					break;
				default:
					minorStep = 1;
					minorStepsPerMajorStep = step;
			}
			
			if (minorStep == 0)
			{
				System.out.println("DotplotView.paintXAxis(): minorStep == 0!");
			}
			
			double min = this.xMin - DotplotView.KEEP_CLEAR_PART
				* (this.xMax - this.xMin);
			double max = this.xMax + DotplotView.KEEP_CLEAR_PART
				* (this.xMax - this.xMin);

			double p = determineFirstMinorStep(min, minorStep);
			
			// field to be used in determineXCoordNumClass()
			this.xFirstMinorStep = p;

			// Math.ceil can give -0.0, this step turns that into 0.0
			if (p == 0)
			{
				p = 0.0;
			}

			// draw minor ticks
			while (p < max)
			{
				int x = this.determineXCoordNumClass(p);
				context.beginPath();
				context.moveTo(x, y + heightOffset);
				context.lineTo(x, y + 2 + heightOffset);
				context.stroke();
				context.closePath();

				p += minorStep;
				
				// check for invalid value of minorStep
				if (minorStep == 0)
				{
					break;
				}
			}

			p = Math.ceil(min / step) * step;

			// Math.ceil can give -0.0, this step turns that into 0.0
			if (p == 0)
			{
				p = 0.0;
			}

			// draw major ticks and help lines
			while (p < max)
			{
				int x = this.determineXCoordNumClass(p);
				context.beginPath();
				context.moveTo(x, y + heightOffset);
				context.lineTo(x, y + heightOffset + 5);
				context.stroke();
				context.closePath();
				
				// draw help line
				drawHelpLine(context, x, heightOffset + 5, y - 5, 0);
			    
				// get the right string value for integer or double
				String pString = StatistiekGWT.getStringValue(p);
				metrics = context.measureText(pString);
				context.fillText(pString,
					x - (int) (0.5 * metrics.getWidth()), 
					y + heightOffset + 5 + DotplotView.FONT_HEIGHT);

				p += step;
			}
		} // numerical xType
	}
	
	private boolean determineNormalFitForString(Context2d context)
	{
		TextMetrics metrics;
		
		boolean normalFit = true;
		
		int amountOfOptions = this.getXStringOptions().size();

		int tickWidth = 0;
		// determine tick width and normal fit
		for (int i = 0; i < amountOfOptions; i++)
		{
			String option = this.getXStringOptions().get(i);
			if (option.equals(ColumnType.WILDCARD))
			{
				continue;
			}

			int x = this.determineXCoordStringClass(option);

			if (i == 0)
			{
				tickWidth = x;
			}
			
			if (i == 1)
			{
				tickWidth = x - tickWidth;
			}
			
			metrics = context.measureText(option);
			double width = metrics.getWidth();
			
			if ((i > 0) && (width > tickWidth))
			{
				normalFit = false;
				break;
			}
		}
		
		return normalFit;
	}

	private boolean determineNormalFitForEnum(Context2d context)
	{
		TextMetrics metrics;

		boolean normalFit = true;
		
		ColumnType columnType = this.model.getStatTableModel().getColumnTypes()
			.get(this.model.getColumnXIndex());
		
		int tickWidth = 0;
		
		// determine tick width and normal fit
		for (int i = 0; i < columnType.getEnumOptions().length; i++)
		{
			String option = columnType.getEnumOptions()[i];
			if (option.equals(ColumnType.WILDCARD))
			{
				continue;
			}
			
			int x = this.determineXCoordEnumClass(option);
			
			if (i == 0)
			{
				tickWidth = x;
			}
			
			if (i == 1)
			{
				tickWidth = x - tickWidth;
			}
			
			metrics = context.measureText(option);
			double width = metrics.getWidth();
			
			if ((i > 0) && (width > tickWidth))
			{
				normalFit = false;
				break;
			}
		}
		
		return normalFit;
	}

	/**
	 * Draws a help help line in grey, starting from (x,y) with given length.
	 * @param context
	 * @param x
	 * @param y
	 * @param length
	 * @param orientation
	 * 	0 is vertical, 1 is horizontal
	 */
	private void drawHelpLine(Context2d context, int x, int y, int length, int orientation)
	{
		context.setStrokeStyle(ColorUtils.getGreyLineColor());

		// draw the line
		context.beginPath();
		if (orientation == 0)
		{
			// vertical orientation
			context.moveTo(x, y);
			context.lineTo(x, y + length);
		}
		else if (orientation == 1)
		{
			// horizontal orientation
			context.moveTo(x, y);
			context.lineTo(x + length, y);
		}
		else
		{
			System.out.println("DotplotView.drawHelpLine(): wrong parameter! orientation = " + orientation);
		}
		context.stroke();
		context.closePath();
		
		// reset the graphics
		context.setStrokeStyle(ColorUtils.BLACK);
	}

	private double determineFirstMinorStep(double min, double minorStep)
	{
		double first = 0;
		
		first = min - (min % minorStep);
		
		return first;
	}

	/**
	 * Paint the y-axis and its value labels.
	 * 
	 * @param context
	 *            The graphics in which the x-axis will be painted
	 */
	private void paintYAxis(Context2d context, int heightOffset)
	{
		TextMetrics metrics;
		
		context.setStrokeStyle(ColorUtils.BLACK);

		ColumnType columnType = this.model.getStatTableModel().getColumnTypes()
			.get(this.model.getColumnYIndex());

		int x = this.yAxisOffset;

		int drawHeight = this.dotAreaHeight();
		
		// draw y-axis
		context.beginPath();
		context.moveTo(x, 5 + heightOffset);
		context.lineTo(x, drawHeight + heightOffset);
		context.stroke();
		context.closePath();

		if (yType.equals(AllowedTypes.ENUM))
		{
			// draw ticks and help lines
			for (int i = 0; i < columnType.getEnumOptions().length; i++)
			{
				String option = columnType.getEnumOptions()[i];
				if (option.equals(ColumnType.WILDCARD))
				{
					continue;
				}

				int y = this.determineYCoordEnumClass(option);
				context.beginPath();
				context.moveTo(x - 5, y + heightOffset);
				context.lineTo(x, y + heightOffset);
				context.stroke();
				context.closePath();

				// draw help line
				drawHelpLine(context, x, y + heightOffset, this.getWidth() - this.yAxisOffset, 1);

				// draw enum option label
				metrics = context.measureText(option);
				context.fillText(option, x - 7 - metrics.getWidth(), 
					(int) (y + 0.5 * DotplotView.FONT_HEIGHT - 3)
					+ heightOffset);
			}

		}
		else if (this.yType.equals(AllowedTypes.STRING))
		{
			int amountOfOptions = this.getYStringOptions().size();

			// draw ticks and help line
			for (int i = 0; i < amountOfOptions; i++)
			{
				String option = this.getYStringOptions().get(i);
				int y = this.determineYCoordStringClass(option);
				context.beginPath();
				context.moveTo(x - 5, y + heightOffset);
				context.lineTo(x, y + heightOffset);
				context.stroke();
				context.closePath();
				// draw help line
				drawHelpLine(context, x, y + heightOffset, this.getWidth() - this.yAxisOffset, 1);

				metrics = context.measureText(option);
				context.fillText(option, x - 7 - metrics.getWidth(), 
					(int) (y + 0.5 * DotplotView.FONT_HEIGHT - 3)
					+ heightOffset);
			}

		}
		else
		{
			// yType is int or double

			int base = 1;
			int exp = (int) Math.log10(this.yMax - this.yMin) - 1;
			int step = (int) (base * Math.pow(10, exp));
			while ((this.yMax - this.yMin) / step > 8)
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

			double min = this.yMin - DotplotView.KEEP_CLEAR_PART
				* (this.yMax - this.yMin);
			double max = this.yMax + DotplotView.KEEP_CLEAR_PART
				* (this.yMax - this.yMin);
			double p = Math.ceil(min / step) * step;

			// test syl: TODO draw minor ticks
			
			// Math.ceil can give -0.0, this step turns that into 0.0
			if (p == 0)
			{
				p = 0.0;
			}

			// draw major ticks and help lines
			while (p < max)
			{
				int y = this.determineYCoordNumClass(p);
				if ((y + (int) (0.5 * DotplotView.FONT_HEIGHT - 3)) < 0)
				{
					continue;
				}

				context.beginPath();
				context.moveTo(x - 5, y + heightOffset);
				context.lineTo(x, y + heightOffset);
				context.stroke();
				context.closePath();
				// draw help line
				drawHelpLine(context, x, y + heightOffset, this.getWidth() - this.yAxisOffset, 1);
				
				// get the right string value for integer or double
				String pString = StatistiekGWT.getStringValue(p);//getStringValueForYVar(p);
				metrics = context.measureText(pString);
				context.fillText(pString,
					x - 7 - metrics.getWidth(), y
						+ (int) (0.5 * DotplotView.FONT_HEIGHT - 3) + heightOffset);

				p += step;
			}
		}
	}


	/**
	 * Paint the correlation. Pearson's product-moment correlation coefficient
	 * is calculated, or Pearson's r.
	 * 
	 * @param context
	 *            The context in which the correlation will be painted
	 */
	private void paintCorrelation(Context2d context)
	{
		if (this.model.isShowCorrelation()
			&& !this.model.columnSplitIndexValid())
		{
			double correlation;
			double r;
			
			context.setStrokeStyle(ColorUtils.BLACK);

			int columnAIndex = this.model.getColumnXIndex();
			int columnBIndex = this.model.getColumnYIndex();
			String correlationInfoString;
			
			double[][] data = this.model.getStatTableModel().getDataColumnsForCorrelation(columnAIndex, columnBIndex);
			String pString;
			
			// voor algoritme zie http://onlinestatbook.com/2/describing_bivariate_data/calculation.html
			correlation = this.getCorrelation(data[0], data[1]);
			r = Math.round(correlation * 100) / 100.0;
			int n = data[0].length;
			// getting p value from t distribution without using apache.commons is not trivial..., so use significance table
			
			try
			{
				// determine p value with significance table
				double p = getLevelOfSignificance(correlation, n);
				
				if (p == 0)
				{
					// for the significance value explicit precision is shown (in case of precise calculation with Commons Math)
					pString = "0.000";
				}
				else
				{
					pString = String.valueOf(p);
				}
				
				if (p == 1)
				{
					correlationInfoString = "r=" + Double.toString(r) 
						+ ", p>0.1";
				}
				else if (p == -1)
				{
					correlationInfoString = "r=" + Double.toString(r) 
						+ ", " + StatistiekGWT.rb.getString("significanceNoShow");
				}
				else
				{
					correlationInfoString = 
						"r=" + Double.toString(r) 
						+ ", p<" + pString;
				}
				
			}
			catch (Exception e)
			{
				System.out.println("degrees of freedom is 0; er is te weinig data om significantie te berekenen");
				correlationInfoString = StatistiekGWT.rb.getString("correlationNoShow");
			}
			
			context.fillText(correlationInfoString,
				3, this.getHeight() - 2);
		}
	}

	/**
	 * Get Pearson's correlation coefficient r for column X and Y. 
	 * For algorithm, see: http://onlinestatbook.com/2/describing_bivariate_data/calculation.html
	 * 
	 * @param columnX An array with valid values of column X.
	 * @param columnY An array with valid values of column Y.
	 */
	private double getCorrelation(double[] columnX, double[] columnY)
	{
		// column A and B should be of the same length, providing valid value pairs
		int n = Math.min(columnX.length, columnY.length);
		
		double[] xy = new double[n];
		double[] xSquared = new double[n];
		double[] ySquared = new double[n];
		for (int i = 0; i < n; i++)
		{
		    xy[i] = columnX[i] * columnY[i];
		    xSquared[i] = Math.pow(columnX[i], 2);
		    ySquared[i] = Math.pow(columnY[i], 2);
		}
			    
		double sumX = this.sum(columnX);
		double sumY = this.sum(columnY);
		double sumXY = this.sum(xy);
		double sumXSquared = this.sum(xSquared);
		double sumYSquared = this.sum(ySquared);

		return (sumXY - ((sumX * sumY)/n)) / 
			(Math.sqrt(sumXSquared - (Math.pow(sumX, 2)/n)) 
				* Math.sqrt(sumYSquared - (Math.pow(sumY, 2)/n)));
	}

	/**
	 * Sum the elements in the doubles array.
	 * @param doubles
	 * @return
	 */
	private double sum(double[] doubles)
	{
		double sum = 0;
		
		for (double value:doubles)
		     sum += value;
		
		return sum;
	}

	/**
	 * Get the level of significance of Pearson's product-moment correlation 
	 * coefficient r and n cases. This means that correlation r has
	 * significance p < level of significance.
	 * 
	 * @param r Pearson's product-moment correlation coefficient
	 * @param n The number of cases
	 * 
	 * @return The level of significance, i.e., correlation r has significance 
	 * p < level of significance. If the level cannot be determined, -1 is returned.
	 */
	private double getLevelOfSignificance(double r, int n)
	{
		double level;
		// the index for reading SIGNIFICANCE_TABLE
		int i = -1; 
		
		// determine the index for reading SIGNIFICANCE_TABLE
		if (n < 3)
		{
			i = -1; // not valid
		}
		else if (n <= 30)
		{
			i = n - 3;
		}
		else if ((n > 30) && (n <= 35))
		{
			i = 28;
		}
		else if ((n > 35) && (n <= 40))
		{
			i = 29;
		}
		else if ((n > 40) && (n <= 45))
		{
			i = 30;
		}
		else if ((n > 45) && (n <= 50))
		{
			i = 31;
		}
		else if ((n > 50) && (n <= 60))
		{
			i = 32;
		}
		else if ((n > 60) && (n <= 70))
		{
			i = 33;
		}
		else if ((n > 70) && (n <= 80))
		{
			i = 34;
		}
		else if ((n > 80) && (n <= 90))
		{
			i = 35;
		}
		else if ((n > 90) && (n <= 100))
		{
			i = 36;
		}
		else if ((n > 100) && (n <= 200))
		{
			i = 37;
		}
		else if ((n > 200) && (n <= 300))
		{
			i = 38;
		}
		else if ((n > 300) && (n <= 400))
		{
			i = 39;
		}
		else if ((n > 400) && (n <= 500))
		{
			i = 40;
		}
		else if ((n > 500))//test syl && (n <= 1000))
		{
			i = 41;
		}
			
		// read the table for row i
		if (i > -1)
		{
			r = Math.abs(r);
			if (r > SIGNIFICANCE_TABLE[i][3])
			{
				level = 0.001;
			}
			else if (r > SIGNIFICANCE_TABLE[i][2])
			{
				level = 0.01;
			}
			else if (r > SIGNIFICANCE_TABLE[i][1])
			{
				level = 0.05;
			}
			else if (r > SIGNIFICANCE_TABLE[i][0])
			{
				level = 0.1;
			}
			else if (r < SIGNIFICANCE_TABLE[i][0])
			{
				level = 1; // p > 0.1
			}
			else
			{
				level = -1; // not valid
			}
		}
		else
		{
			level = -1;
		}
		
		return level;
	}

	/**
	 * Determine the coordinates for all objects Used only in single variable
	 * cases, with that variable on the x-axis
	 * 
	 * @return a matrix containing the x and y coordinate for all objects
	 */
	private int[][] determineCoordsXSingleVar()
	{
		int[][] coords = new int[this.model.getStatTableModel().getRowCount()][2];
		int[] splitClasses = new int[this.model.getStatTableModel().getRowCount()];
		int[][] sortedData = null;

		int drawHeight = this.dotAreaHeight();
		for (int i = 0; i < this.model.getStatTableModel().getRowCount(); i++)
		{
			if (this.model.getStatTableModel()
				.getValueAt(i, this.model.getColumnXIndex())
				.equals(ColumnType.WILDCARD))
			{
				// skip wildcard objects
				splitClasses[i] = -1;
				continue;
			}
			splitClasses[i] = this.getSplitClass(i);

			// voor verdeling binnen 1 veld:
			if (DotplotView.this.model.splitInSingleView())
			{
				splitClasses[i] = 0;
			}

			coords[i][0] = this.determineXCoord(i);
		}
		
		// sortedData = [x, y, split, original index]
		sortedData = new int[this.model.getStatTableModel().getRowCount()][4];
		for (int i = 0; i < this.model.getStatTableModel().getRowCount(); i++)
		{
			sortedData[i][0] = coords[i][0];
			sortedData[i][2] = splitClasses[i];
			sortedData[i][3] = i;
		}
		
		Arrays.sort(sortedData, new Comparator<int[]>() {
            @Override
            /**
             * Compare [x1, y1, split1] to [x2, y2, split2] on x-coordinate
             * and split. 
             * @param o1
             * @param o2
             * @return
             */
            public int compare(int[] o1, int[] o2) 
            {
            	// compare the x coordinates
            	if (o1[0] < o2[0])
            		return -1;
            	else if (o1[0] > o2[0])
            		return 1;
            	else // same x coordinates
            	{
            		if (o1[2] < o2[2])
            			return -1;
            		else if (o1[2] > o2[2])
            			return 1;
            		else // same split
            			return 0;
            	}
            }
        });
		
		// bepaal de y-correctie die zo nodig gedaan wordt per split: max y / max aantal dots per x
		int[] maxFrequencyXPerSplit = this.getMaxFrequencyXPerSplit(sortedData);
		// correction in double values to avoid rounding errors
		double[] correctionYPerSplit = new double[this.splitClasses]; 
			
		for (int split = 0; split < this.splitClasses; split++)
		{
			correctionYPerSplit[split] = Math.min(
				this.dotRadius * 2, 
				((1 - DotplotView.KEEP_CLEAR_PART) * drawHeight - 2 * this.dotRadius) / maxFrequencyXPerSplit[split]);
		}

		// set initial y
		double y_initial = (1 - DotplotView.KEEP_CLEAR_PART) * drawHeight;
		double y = y_initial;
		// use double values to get a precise calculation for large data sets
		double[] y_doubles = new double[this.model.getStatTableModel().getRowCount()];
		
		for (int i = 0; i < this.model.getStatTableModel().getRowCount(); i++)
		{
			if (i > 0)
			{
				// look at the previous x-coordinate...
				// if x-coord and split the same, then adjust y-coord
				if (sortedData[i][0] == sortedData[i-1][0]) // same x coordinate
				{
					if ((sortedData[i][2] > -1)//!= -1) // skip wildcards; sortedData[i][2] == -2 if row i contains a wildcard
						&& (sortedData[i][2] == sortedData[i-1][2])) // same split
					{
						// dot with the same x-coordinate in the same split, so calculate y based on 
						// the previous y
						y = y_doubles[i-1] - correctionYPerSplit[sortedData[i][2]];
					}
					else // same x coordinate, next split 
					{
						// reset y to initial value
						y = y_initial;
					}
				}
				else // different x coordinate
				{
					// reset y to initial value
					y = y_initial;
				}
			}
			
			y_doubles[i] = y;
		} // i-loop

		// round double values to int coordinates
		for (int i = 0; i < this.model.getStatTableModel().getRowCount(); i++)
		{
			sortedData[i][1] = (int) y_doubles[i];
		}
		
		// zet sortedData in coords in de originele volgorde
		for (int i = 0; i < sortedData.length; i++)
		{
			int index = sortedData[i][3]; // index within the original order
			coords[index][0] = sortedData[i][0];
			coords[index][1] = sortedData[i][1];
		}
		
		return coords;
	}

	/**
	 * Get the frequency of the most frequently occurring value in sortedData[0].
	 * 
	 * @param sortedData
	 * @return
	 * 		The frequency of the most frequently occurring value in sortedData[0].
	 */
	private int[] getMaxFrequencyXPerSplit(int[][] sortedData)
	{
	    int[] maxCount = new int [this.splitClasses];
		
		if ((sortedData != null) && (sortedData.length != 0))
		{
			for (int split = 0; split < this.splitClasses; split++)
			{
			    int previous = sortedData[0][0];
			    int count = 1;
		
				for (int i = 1; i < sortedData.length; i++)
				{
			        if (sortedData[i][0] == previous) // x coordinate same as previous one
			        {
			        	if (sortedData[i][2] == split) //  in split
			        		count++;
			        }
			        else // different x coordinate
			        {
			            if (count > maxCount[split]) 
			            {
			                maxCount[split] = count;
			            }
			            previous = sortedData[i][0];
			            count = 1;
			        }
			    }
	            if (count > maxCount[split]) 
	            {
	                maxCount[split] = count;
	            }
			}
		}
		
		return maxCount;
	}

	/**
	 * Determine the coordinates for all objects. Used only in single variable
	 * cases, with that variable on the y-axis
	 * 
	 * @return a matrix containing the x and y coordinate for all objects
	 */
	private int[][] determineCoordsYSingleVar()
	{
		int[][] coords = new int[this.model.getStatTableModel().getRowCount()][2];
		int[] splitClasses = new int[this.model.getStatTableModel().getRowCount()];

		int dotSizeSquared = (int) (Math.pow(2 * this.dotRadius + 1, 2));
		for (int i = 0; i < this.model.getStatTableModel().getRowCount(); i++)
		{
			if (this.model.getStatTableModel()
				.getValueAt(i, this.model.getColumnYIndex())
				.equals(ColumnType.WILDCARD))
			{
				// skip wildcard cases
				splitClasses[i] = -1;
				continue;
			}
			splitClasses[i] = this.getSplitClass(i);

			// voor verdeling binnen 1 veld:
			if (DotplotView.this.model.splitInSingleView())
				splitClasses[i] = 0;

			coords[i][1] = this.determineYCoord(i);

			// find the lowest x value for point i such that the distance to all
			// other points is
			// greater than DotplotView.KEEP_CLEAR_PART
			int x = (int) (this.yAxisOffset + (DotplotView.KEEP_CLEAR_PART * (this
				.getWidth() - this.yAxisOffset)));

			for (int j = 0; j < i; j++)
			{
				if (!this.model.getStatTableModel()
					.getValueAt(j, this.model.getColumnYIndex())
					.equals(ColumnType.WILDCARD)
					&& splitClasses[i] == splitClasses[j]
					&& (Math.pow(coords[j][0] - x, 2) + Math.pow(coords[j][1]
						- coords[i][1], 2)) < dotSizeSquared)
				{
					// some other object is too close, so start over with
					// smaller y
					x += 2;
					j = -1; // j = -1 will cause the for loop to start over with
							// j = 0
				}
			}
			coords[i][0] = x;
		}

		return coords;
	}

	/**
	 * Determine in how many classes the split variable splits the data
	 * 
	 * @return the amount of classes in which the split variable splits the data
	 */
	private int numberOfSplitClasses()
	{
		if (!this.model.columnSplitIndexValid())
		{
			return 1;
		}
		else
		{
			ColumnType cType = this.model.getStatTableModel().getColumnTypes()
				.get(this.model.getColumnSplitIndex());
			if (this.splitType.isNumber())
			{
				if (this.model.getSplitBinBoundaries() == null)
				{
					return 1;
				}
				else
				{
					return this.model.getSplitBinBoundaries().size() - 1;
				}
			}
			else if (this.splitType.equals(AllowedTypes.ENUM))
			{
				return cType.getEnumOptions().length - 1;
			}
			else
			{
				return this.model.getStatTableModel()
					.getStringOptions(this.model.getColumnSplitIndex()).size();
			}
		}
	}

	/**
	 * Determine in which split class the object at rowIndex is
	 * 
	 * @param rowIndex
	 *            the index of the object to classify
	 * @return the split class in which the object at rowIndex is
	 * 		Is -1 if the object cannot be classified.
	 * 		Is -2 if the object is a wildcard.
	 */
	private int getSplitClass(int rowIndex)
	{
		return this.model.getStatTableModel().classifyObject(rowIndex,
			this.model.getSplitOptions());
	}

	/**
	 * Get the type of the x variable.
	 * @return The allowed type
	 */
	public AllowedTypes getXType()
	{
		return this.xType;
	}

	/**
	 * Get the type of the y variable.
	 * @return The allowed type
	 */
	public AllowedTypes getYType()
	{
		return this.yType;
	}
	
	private class Rectangle
	{
		private double x;
		private double y;
		private double width;
		private double height;
		
		public Rectangle(double x, double y, double width, double height)
		{
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		/**
		 * Returns true if rectangle contains the given point, else false.
		 *  
		 * @param p
		 * @return
		 */
		public boolean contains(Point p)
		{
			boolean b;
			
			if ((p.getX() >= x) && (p.getX() <= x + width)
				&& (p.getY() >= y) && (p.getY() <= y + height))
			{
				b = true;
			}
			else
			{
				b = false;
			}
			
			return b;
		}
	}

	/**
	 * Mouse move and click handler that enables the user to select objects by clicking on one
	 * or by dragging the mouse.
	 * 
	 * @author Manu Drijvers, Sylvia van Borkulo
	 * 
	 */
	private class DotHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler,  
		MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		private Point startDrag;
		private Point currentDragLocation;
		private boolean inDrag = false;
		private boolean mouseDown = false;

		@Override
		public void onMouseMove(MouseMoveEvent event)
		{
			if (this.mouseDown)
			{
				this.currentDragLocation = new Point(event.getX(), event.getY());
				this.inDrag = true;
			}

			// repaint the view to show which points would be selected
			// if the mouse would be released
			if (this.inDrag)
			{
				DotplotView.this.mainPanel.paint();
			}
		}

		@Override
		public void onMouseUp(MouseUpEvent event)
		{
			Point currentPoint = new Point(event.getX(), event.getY());
			
			if (!this.inDrag 
				|| (this.distance(this.startDrag, currentPoint) < dotRadius) // geen echte sleep-actie
				|| DotplotView.this.objectLocations == null)
			{
				// end of a click
				
				// uit mouseClicked:
				if (DotplotView.this.objectLocations == null)
				{
					return;
				}

				ArrayList<Point> pointList = DotplotView.this.objectLocations;
				
				int dotIndex;
				// check if a dot was clicked
				for (dotIndex = 0; dotIndex < pointList.size(); dotIndex++)
				{
					Point p = pointList.get(dotIndex);
//					if (p != null && this.distance(p, new Point(event.getClientX(), event.getClientY())) <= dotRadius)
					if (p != null && this.distance(p, new Point(event.getX(), event.getY())) <= dotRadius)
					{
						break;
					}
				}

				if (dotIndex < pointList.size())
				{
					// if i < pointsList.size() then a dot was clicked, so
					// select the object
					// create a new selection list
					ArrayList<Boolean> selectionList;
					
					// detect control click
					boolean controlClicked = event.isControlKeyDown();
					
					if (controlClicked)
					{
						// get the current selection list
						selectionList = model.getStatTableModel().getSelectionList();
						
						for (int j = 0; j < pointList.size(); j++)
						{
							if (dotIndex == j)
							{
								// add selection to current selectionlist
								selectionList.set(j, true);
							}
						}
					}
					else
					{
						// new selection list
						selectionList = new ArrayList<Boolean>(
							pointList.size());
					
						for (int j = 0; j < pointList.size(); j++)
						{
							selectionList.add(dotIndex == j);
						}
					}
					
					// update the selection
					model.getStatTableModel().setSelectionList(selectionList);
				}
			} // end of a click
			else
			{
				// end of mousedrag
				ArrayList<Point> pointList = DotplotView.this.objectLocations;
	
				double x1 = this.startDrag.getX();
				int x2 = event.getX();
				double y1 = this.startDrag.getY();
				int y2 = event.getY();
	
				// create the rectangle between the drag start location and the drag
				// release location
				Rectangle r = new Rectangle(Math.min(x1, x2), Math.min(y1, y2),
					Math.abs(x1 - x2), Math.abs(y1 - y2));
	
				// determine which points were selected
				ArrayList<Boolean> selectionList;
				
				// detect control click
				boolean controlClicked = event.isControlKeyDown();
				
				if (controlClicked)
				{
					// get the current selection list
					selectionList = model.getStatTableModel().getSelectionList();
	
					for (int i = 0; i < pointList.size(); i++)
					{
						Point p = pointList.get(i);
						if (p != null && r.contains(p))
						{
							selectionList.set(i, true);
						}
					}
				}
				else
				{
					// new selection list
					selectionList = new ArrayList<Boolean>(
						pointList.size());
	
					for (int i = 0; i < pointList.size(); i++)
					{
						Point p = pointList.get(i);
						selectionList.add(p != null && r.contains(p));
					}
				}
	
				// update the selection
				DotplotView.this.model.getStatTableModel().setSelectionList(
					selectionList);
	
				this.inDrag = false;
				
				// opnieuwe tekenen om het drag-vierkant weg te halen, ook als er geen selectie is
				DotplotView.this.mainPanel.paint();
				
			} // end of a drag
			
			this.mouseDown = false;
			
		} // onMouseup

		@Override
		public void onMouseDown(MouseDownEvent event)
		{
			if (DotplotView.this.objectLocations == null)
			{
				return;
			}
			// store the location where the moues drag started
			this.startDrag = new Point(event.getX(), event.getY());
			this.currentDragLocation = this.startDrag;
			this.mouseDown = true;
		}

		@Override
		public void onTouchEnd(TouchEndEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTouchMove(TouchMoveEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTouchStart(TouchStartEvent event)
		{
			// TODO Auto-generated method stub
			
		}
		
		/**
		 * Calculates the distance between the two given points.
		 * 
		 * @param p1
		 * @param p2
		 * @return
		 */
		private double distance(Point p1, Point p2)
		{
			double d;
			
			d = Math.sqrt(Math.pow(Math.abs(p1.getX() - p2.getX()), 2) + Math.pow(Math.abs(p1.getY() - p2.getY()), 2));
			
			return d;
		}
	} // DotClickListener class

	
	private class DotPanel
	{
		private Canvas canvas;
		private Context2d bufferContext;
		private Context2d context;
		private DotHandler dotHandler;

		public DotPanel()
		{
			this.canvas = Canvas.createIfSupported();
			this.canvas.addStyleName(statistiekCss.canvas());
			
			this.dotHandler = new DotHandler(); 
			
			this.canvas.addMouseDownHandler(dotHandler);
			this.canvas.addMouseMoveHandler(dotHandler);
			this.canvas.addMouseUpHandler(dotHandler);
			this.canvas.addTouchStartHandler(dotHandler);
			this.canvas.addTouchMoveHandler(dotHandler);
			this.canvas.addTouchEndHandler(dotHandler);
			
			this.canvas.addDomHandler(dummyTouchHandler, TouchStartEvent.getType());
			this.canvas.addDomHandler(dummyTouchHandler, TouchEndEvent.getType());
			
			this.context = canvas.getContext2d();
		}
		
		public void paintCorrelation()
		{
			this.context = canvas.getContext2d();
			this.context.clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());

			Canvas correlationCanvas = Canvas.createIfSupported();
			correlationCanvas.setCoordinateSpaceWidth(canvas.getCoordinateSpaceWidth());
			correlationCanvas.setCoordinateSpaceHeight(canvas.getCoordinateSpaceHeight());
			Context2d correlationContext = correlationCanvas.getContext2d();
			correlationContext.clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());

			DotplotView.this.paintCorrelation(correlationContext);
			
			// draw the drag context and the saved buffer context
			context.drawImage(bufferContext.getCanvas(), 0, 0);
			context.drawImage(correlationContext.getCanvas(), 0, 0);
		}

		public Canvas getCanvas()
		{
			return this.canvas;
		}
		
		public DotHandler getMouseMoveHandler()
		{
			return this.dotHandler;
		}
		
		public void paint()
		{
			GWT.log("DotPanel.paint()");
			
			// clear panel
			this.context = canvas.getContext2d();
			this.context.clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
			
			// if the user is dragging the mouse, draw the rectangle that will
			// be selected if
			// the user would release the mouse
			if (this.dotHandler.inDrag)
			{
				double x1 = this.dotHandler.startDrag.getX();
				double y1 = this.dotHandler.startDrag.getY();
				double x2 = this.dotHandler.currentDragLocation.getX();
				double y2 = this.dotHandler.currentDragLocation.getY();
				
				// set the drag context
				Canvas dragCanvas = Canvas.createIfSupported();
				dragCanvas.setCoordinateSpaceWidth(canvas.getCoordinateSpaceWidth());
				dragCanvas.setCoordinateSpaceHeight(canvas.getCoordinateSpaceHeight());
				Context2d dragContext = dragCanvas.getContext2d();
				
				dragContext.setFillStyle(SELECTION_RECTANGLE_COLOR_TRANSPARENT);
				dragContext.fillRect(Math.min(x1, x2), Math.min(y1, y2),
					Math.abs(x1 - x2), Math.abs(y1 - y2));
				dragContext.fill();
				
				// draw the drag context and the saved buffer context
				context.drawImage(bufferContext.getCanvas(), 0, 0);
				context.drawImage(dragContext.getCanvas(), 0, 0);
			}
			else
			{
				Canvas tempCanvas = Canvas.createIfSupported();
				tempCanvas.setCoordinateSpaceWidth(canvas.getCoordinateSpaceWidth());
				tempCanvas.setCoordinateSpaceHeight(canvas.getCoordinateSpaceHeight());
				Context2d tempContext = tempCanvas.getContext2d();
				tempContext.clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
				
				tempContext.setFillStyle(ColorUtils.BLACK);
				
				// correlation heeft eigen context, niet in buffercontext
				Canvas tempCorrelationCanvas = Canvas.createIfSupported();
				tempCorrelationCanvas.setCoordinateSpaceWidth(canvas.getCoordinateSpaceWidth());
				tempCorrelationCanvas.setCoordinateSpaceHeight(canvas.getCoordinateSpaceHeight());
				Context2d tempCorrelationContext = tempCorrelationCanvas.getContext2d();

				// sorteer de rij-indices zodat selected rijen op het eind staan
				// en de bijbehorende dots als laatste worden getekend
				final StatTableModel tableModel = DotplotView.this.model.getStatTableModel();
				int nrRows = tableModel.getRowCount();
				Integer[] indexSortedOnSelected = new Integer[nrRows];
				for (int i = 0; i < nrRows; i++)
				{
					indexSortedOnSelected[i] = i;
				}
				
				Arrays.sort(indexSortedOnSelected, new Comparator<Integer>() {
		            @Override
		            /**
		             * Compare integers i1 and i2 on being selected.
		             * and split. 
		             * @param i1
		             * @param i2
		             * @return
		             */
		            public int compare(Integer i1, Integer i2) 
		            {
		            	// if both rows are selected, the order is based on color //doesn't matter
	            		if (tableModel.isRowSelected(i1) && tableModel.isRowSelected(i2))
	            		{
	            			//return determineColor(i1).value().compareTo(determineColor(i2).value());
	            			return 0;
	            		}
		            	// indices of selected rows are always larger 
	            		else if (tableModel.isRowSelected(i2))
	            		{
		            		return -1;
	            		}
		            	else if (tableModel.isRowSelected(i1))
		            	{
		            		return 1;
		            	}
		            	else // if none of the rows is selected, the order doesn't matter
		            	{
	            			return 0;
		            	}
		            }
		        });
				
				int startIndexSelected = this.getStartIndexSelected(indexSortedOnSelected);
				
				if (DotplotView.this.model.columnXIndexValid()
					&& DotplotView.this.model.columnYIndexValid())
				{
					// all variables are valid, draw a scatterplot
					DotplotView.this.determineDotSize();
					DotplotView.this.objectLocations = new ArrayList<Point>(
						DotplotView.this.model.getStatTableModel().getRowCount());
					for (int i = 0; i < DotplotView.this.model.getStatTableModel()
						.getRowCount(); i++)
					{
						DotplotView.this.objectLocations.add(null);
					}
	
					DotplotView.this.paintCorrelation(tempCorrelationContext);
	
					for (int i = 0; i < DotplotView.this.splitClasses; i++)
					{
						DotplotView.this.paintXAxis(tempContext, 
							i * (DotplotView.this.getHeight() - 5));
						DotplotView.this.paintYAxis(tempContext, 
							i * (DotplotView.this.getHeight() - 5));
					}
	
					for (int row = 0; row < DotplotView.this.model.getStatTableModel()
						.getRowCount(); row++)
					{
						// use the ordered indices so that selected dots will be drawn at last
						// test syl TODO: in elke drawPoint() wordt setFillStyle gedaan voor de geselecteerde; dit zorgt voor slechte performance
						DotplotView.this.drawPoint(tempContext, indexSortedOnSelected[row]);
					}
				} // SCATTERPLOT
				else if (DotplotView.this.model.columnXIndexValid())
				{
					// X variable is valid, so draw DOTPLOT
					for (int i = 0; i < DotplotView.this.splitClasses; i++)
					{
						DotplotView.this.paintXAxis(tempContext, 
							i * (DotplotView.this.getHeight() - 5));
					}
					
					// only the x-column variable is valid, draw a single variable
					// dot plot with the variable on the x-axis
					DotplotView.this.determineDotSize();
					DotplotView.this.dotRadius = DotplotView.this.dotRadius * 2; // ??
					DotplotView.this.objectLocations = new ArrayList<Point>(
						DotplotView.this.model.getStatTableModel().getRowCount());
					for (int i = 0; i < DotplotView.this.model.getStatTableModel()
						.getRowCount(); i++)
					{
						DotplotView.this.objectLocations.add(null);
					}
	
					CssColor previousColor = null;
					CssColor currentColor = null;
					
					// dit duurt even bij grote dataset...
					int[][] coords = DotplotView.this.determineCoordsXSingleVar();
					
					// DRAW THE DOTS
					for (int i = 0; i < coords.length; i++)
					{
						// use the ordered indices so that selected dots will be drawn at last
						int index = indexSortedOnSelected[i];
	
						if (!DotplotView.this.model
							.getStatTableModel()
							.getValueAt(index, DotplotView.this.model.getColumnXIndex())
							.equals(ColumnType.WILDCARD))
						{
							int splitClass = DotplotView.this.getSplitClass(index);
							if (DotplotView.this.model.splitInSingleView())
							{
								splitClass = 0;
							}
							
							if (splitClass >= 0)
							{
								int heightOffset;
								if (DotplotView.this.model.getColumnSplitIndex() > -1
									&& DotplotView.this.model.splitInSingleView())
								{
									heightOffset = 0;
								}
								else
								{
									heightOffset = (splitClass)
										* (DotplotView.this.getHeight() - 5);
								}
	
								// startIndexSelected
								if (i < startIndexSelected)//(index < startIndexSelected)
								{
									currentColor = determineColor(index);
									
									if ((previousColor == null) ||
										(!currentColor.equals(previousColor)))
									{
										// only set fill style if necessary to improve performance
										double alpha;
										CssColor transparentColor ;
	
										// set default transparency
										alpha = 0.5;
										transparentColor = CssColor.make("rgba(" 
											+ ColorUtils.getRed(currentColor) + ", " 
											+ ColorUtils.getGreen(currentColor) + "," 
											+ ColorUtils.getBlue(currentColor) + ", " 
											+ alpha + ")");
	
										tempContext.setFillStyle(transparentColor);
	
										tempContext.setLineWidth(1);
										tempContext.setStrokeStyle(ColorUtils.BLACK);
										
										previousColor = currentColor;
									}
									
									DotplotView.this.drawNonSelectedPointAtLocation(tempContext,
										coords[index][0], coords[index][1] + heightOffset,
										index, currentColor);
								}
								else
								{ // the selected part, index >= startIndexSelected
									if (i == startIndexSelected)//(index == startIndexSelected) // moet dit i zijn?!
									{
										// only once for performance reasons!
										tempContext.setLineWidth(2);
										tempContext.setStrokeStyle(ColorUtils.BLACK);
										
										// reset previousColor
										previousColor = null;
									}
									
									currentColor = determineColor(index);
									
									if ((previousColor == null) ||
										(!currentColor.equals(previousColor)))
									{
										// only set fill style if necessary to improve performance
										double alpha;
										CssColor transparentColor ;
	
										// set non transparent for selected dot
										alpha = 1;
										transparentColor = CssColor.make("rgba(" 
											+ ColorUtils.getRed(currentColor) + ", " 
											+ ColorUtils.getGreen(currentColor) + "," 
											+ ColorUtils.getBlue(currentColor) + ", " 
											+ alpha + ")");
	
										tempContext.setFillStyle(transparentColor);
																			
										previousColor = currentColor;
									}
	
									DotplotView.this.drawSelectedPointAtLocation(tempContext,
										coords[index][0], coords[index][1] + heightOffset,
										index, currentColor);
								}
							}
						}
					} // for-loop DRAW THE DOTS
				} // dotplot
				else if (DotplotView.this.model.columnYIndexValid())
				{
					// Y variable is valid, so draw dotplot with only y variable (not possible yet in current version)
	
					for (int i = 0; i < DotplotView.this.splitClasses; i++)
					{
						DotplotView.this.paintYAxis(tempContext, 
							i * (DotplotView.this.getHeight() - 5));
					}
					
					// only the y-column variable is valid, draw a single variable
					// dot plot with the variable on the y-axis
					DotplotView.this.determineDotSize();
					DotplotView.this.dotRadius = DotplotView.this.dotRadius * 2; // ??
					DotplotView.this.objectLocations = new ArrayList<Point>(
						DotplotView.this.model.getStatTableModel().getRowCount());
					for (int i = 0; i < DotplotView.this.model.getStatTableModel()
						.getRowCount(); i++)
					{
						DotplotView.this.objectLocations.add(null);
					}
	
					int[][] coords = DotplotView.this.determineCoordsYSingleVar();
					for (int i = 0; i < coords.length; i++)
					{
						// use the ordered indices so that selected dots will be drawn at last
						int index = indexSortedOnSelected[i];
	
						if (!DotplotView.this.model
							.getStatTableModel()
							.getValueAt(index, DotplotView.this.model.getColumnYIndex())
							.equals(ColumnType.WILDCARD))
						{
							int splitClass = DotplotView.this.getSplitClass(index);
							if (DotplotView.this.model.splitInSingleView())
								splitClass = 0;
							if (splitClass >= 0)
							{
								int heightOffset;
								if (DotplotView.this.model.getColumnSplitIndex() > -1
									&& DotplotView.this.model.splitInSingleView())
								{
									heightOffset = 0;
								}
								else
								{
									heightOffset = (splitClass)
										* (DotplotView.this.getHeight() - 5);
								}
	
								DotplotView.this.drawPointAtLocation(tempContext,
									coords[index][0], coords[index][1] + heightOffset,
									index);
							}
						}
					}
				}
	
				// draw the bottom line and labels
				for (int i = 0; i < (DotplotView.this.isSplitSingleViewSelected() ? 1
					: splitClasses); i++)
				{
					int ySplitOffset = i
						* (DotplotView.this.getHeight() - 5);
					DotplotView.this.paintAxisLabels(tempContext, ySplitOffset, i);
				}
				
				// finally draw the buffer to the canvas
				context.drawImage(tempContext.getCanvas(), 0, 0);
				context.drawImage(tempCorrelationContext.getCanvas(), 0, 0);
				this.bufferContext = tempContext;
			} // no inDrag
		}

		/**
		 * Get the the first index in the array with indices that indicates 
		 * a selected row.
		 * 
		 * @param indexSortedOnSelected
		 * @return
		 */
		private int getStartIndexSelected(Integer[] indexSortedOnSelected)
		{
			int startIndex;
			
			final StatTableModel tableModel = DotplotView.this.model.getStatTableModel();
			int nrRows = tableModel.getRowCount();
			startIndex = nrRows;
			
			for (int i = 0; i < nrRows; i++)
			{
				if (tableModel.isRowSelected(indexSortedOnSelected[i]))
				{
					startIndex = i;
					break;
				}
			}
			
			return startIndex;
		}
		
	} // class DotPanel

	private class CopyContext2d
	{
		private Context2d context;
		
		public CopyContext2d(Context2d context)
		{
			this.context = context;
		}
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event)
	{
		this.update();
	}

	@Override
	public void onTableChange(TableChangeEvent event)
	{
		GWT.log("DotplotView.onTableChange()");

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
		if (this.model.columnXIndexValid())
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

	/**
	 * Remove all of this view's handler occurrences.
	 */
	public void removeHandlers()
	{
		this.tableChangeEventHandlerRegistration.removeHandler();
		this.selectionChangeEventHandlerRegistration.removeHandler();
	}

	/**
	 * Colors has been changed. Call the view's update method. 
	 * This also updates the user options panel.
	 */
	@Override
	public void onColorChange(ColorChangeEvent event)
	{
		// set colors in model
		this.model.setColorA(CssColor.make(event.getColorA()));		
		this.model.setColorB(CssColor.make(event.getColorB()));		

		this.update();
	}

	/**
	 * @return the userOptionsPanel
	 */
	public DotplotUserOptionsPanel getUserOptionsPanel()
	{
		return userOptionsPanel;
	}
}
