package fi.statistiekgwt.client.piechart;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.googlecode.gwt.charts.client.event.ReadyEvent;
import com.googlecode.gwt.charts.client.event.ReadyHandler;
import com.googlecode.gwt.charts.client.options.PieSliceText;
import fi.statistiekgwt.client.ColorUtils;
import fi.statistiekgwt.client.ColorUtils.RGBColor;
import fi.statistiekgwt.client.DialogButton;
import fi.statistiekgwt.client.SplitOptions;
import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;
import fi.statistiekgwt.client.StatistiekUtils;
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
 * MVC View for statistiekView PieChart
 * 
 * @author Sylvia van Borkulo
 * 
 */
public class PieChartView extends LayoutPanel implements TableChangeEventHandler, SelectionChangeEventHandler, 
	OutlierChangeEventHandler, HasHandlers
{
	private PieChartModel model;
	private PieChartController controller;
	private PieChartUserOptionsPanel userOptionsPanel;
	private DialogButton dialogButton;

	/**
	 * Op panel 'alles' staan mainpanel en dialogbuttonpanel.
	 */
	private FlowPanel alles;
	private DockLayoutPanel mainPanel;
	private ScrollPanel scrollPanel;
	private HorizontalPanel dialogButtonPanel;
	private PieChart chart;
	/**
	 * Data table for pie chart.
	 */
	private DataTable dataTable;
	/**
	 * Options for the pie chart.
	 */
	private PieChartOptions options;
	
	/**
	 * The frequencies of the enum variable
	 */
	private FrequencyTuple[][] frequencies_enum;

	/**
	 * pieColors contains cssColor strings
	 */
	private String[] pieColors;
	
	public static final RGBColor SELECTED_COLOR = ColorUtils.SELECTION_COLOR_RGB;

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
	public PieChartView(PieChartModel model,
		PieChartController controller)
	{
		super();

		this.alles = new FlowPanel();
		
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		this.model = model;
		this.controller = controller;

		this.dummyTouchHandler = StatistiekUtils.getDummyTouchHandler();
		this.eventBus = StatistiekUtils.EVENT_BUS;
		
		// bind PieChartview to stattablemodel: to handle table changes in stattablemodel
		this.tableChangeEventHandlerRegistration = this.model.getStatTableModel().addTableChangeEventHandler(this);

		// bind PieChartview to stattablemodel: to handle selection changes in stattablemodel
		this.selectionChangeEventHandlerRegistration = this.model.getStatTableModel().addSelectionChangeEventHandler(this);
		
		// bind PieChartview to stattablemodel: to handle outlier changes in stattablemodel
		this.outlierChangeEventHandlerRegistration = this.model.getStatTableModel().addOutlierChangeEventHandler(this);
		
		// create GUI
		this.mainPanel = new DockLayoutPanel(Unit.PX);
		
		this.scrollPanel = new ScrollPanel(this.mainPanel);
		this.scrollPanel.setAlwaysShowScrollBars(false);
		this.scrollPanel.addStyleName(statistiekCss.backgroundwhite());
		
		this.alles.add(this.scrollPanel);

		this.userOptionsPanel = new PieChartUserOptionsPanel(this, controller, model);
		// initial update for setting widgets in user options panel
		this.userOptionsPanel.update();

		this.initializeSize();

		dialogButton = userOptionsPanel.getDialogButton();

		this.dialogButtonPanel = new HorizontalPanel();
		this.dialogButtonPanel.setWidth("100%");
		this.dialogButtonPanel.setHeight(StatistiekGWT.BUTTON_HEIGHT + "px");
		this.dialogButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.dialogButtonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.dialogButtonPanel.addStyleName(statistiekCss.backgroundblue());
		this.dialogButton = userOptionsPanel.getDialogButton();
		this.dialogButtonPanel.add(this.dialogButton);

		this.alles.add(this.dialogButtonPanel);
		if (!this.model.getStatTableModel().isViewsEditable())
		{
			this.dialogButtonPanel.setHeight("0px");
		}

		this.alles.setPixelSize(this.getWidth(), this.getHeight());
		
		// add alles to PieChartview (layoutpanel)
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
		
		this.setMainPanelSize();
	}

	/**
	 * Get the views width.
	 */
	public int getWidth()
	{
		return this.width;
	}
	
	/**
	 * Get the views height.
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

	public int getColumnIndexBoxSelectedIndex()
	{
		return this.userOptionsPanel.getColumnIndexBoxSelectedIndex();
	}

	public void update()
	{
		this.setMainPanelSize();
		
		this.dialogButton.setVisible(this.model.getStatTableModel()
			.isViewsEditable());
	
		// update the components in the useroptionspanel
		userOptionsPanel.update();

		if (this.model.columnIndexValid())
		{
			frequencies_enum = this.model.enumClassFrequency();
			this.pieColors = new String[frequencies_enum.length];
			
			// set colors for background
			for (int i = 0; i < frequencies_enum.length; i++)
			{
				this.pieColors[i] = ColorUtils.getColor(i).toString();
			}
			
			makePieChart();
			
		} // columnIndexValid()
	}

	/**
	 * Calculates the percentage value of the ratio frequency/divisor. If divisor is 0, 0 is returned.
	 * If the percentage value is an integer value, no decimals are returned in waardeString.
	 * If percentage is a decimal value, one decimal is shown.
	 * 
	 * @param frequency
	 * @param divisor
	 * @return waardeString The percentage value of the ratio frequency/divisor
	 */
	private String getWaardeString(int frequency, int divisor)
	{
		double waarde;
		String waardeString;
		
		if (divisor != 0)
		{
			waarde = ((double) frequency/divisor)*100;
			waardeString = StatistiekGWT.getStringValueWithOneDecimal(waarde);
		}
		else
		{
			waardeString = "0";
		}
		
		return waardeString;
	}

	/**
	 * Make the pie chart on panel.
	 */
	private void makePieChart()
	{
//		Canvas pieCanvas = Canvas.createIfSupported();
//		Context2d context = pieCanvas.getContext2d();
//		
//		drawPieChart(context);

		ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
		chartLoader.loadApi(new Runnable() {

			@Override
			public void run() {
				StatistiekUtils.removeAllWidgetsFromPanel(mainPanel);

				// Create and attach the chart
				// test add own legend
				VerticalPanel legend = new VerticalPanel();
				legend.add(new Label("links 15 30%"));
				legend.add(new Label("rechts 35 70%"));
				VerticalPanel legend2 = new VerticalPanel();
				legend2.add(new Label("links 15 30%"));
				legend2.add(new Label("rechts 35 70%"));
//				mainPanel.addEast(legend, 100);
//				mainPanel.addSouth(legend2, 50);
				
				chart = new PieChart();
				mainPanel.add(chart);
				
				draw();
			}
		});
//		this.mainPanel.add(pieCanvas);
	}

	/**
	 * Draw example pie chart.
	 */
	private void draw()
	{
		// Prepare the data
		dataTable = DataTable.create();
		String columnName = this.model.getStatTableModel().getColumnName(model.getColumnIndex());
		dataTable.addColumn(com.googlecode.gwt.charts.client.ColumnType.STRING, columnName);
		dataTable.addColumn(com.googlecode.gwt.charts.client.ColumnType.NUMBER, "Number");
		
		FrequencyTuple[] ft = frequencies_enum[0]; // er is geen split, dus splitclass 0
		
		dataTable.addRows(ft.length);
		
		// for-loop over de categorieen
		for (int i = 0; i < ft.length; i++)
		{
			FrequencyTuple ft_category = ft[i];
			
			// set the category name
			dataTable.setValue(i, 0, ft_category.label);
			// set the frequency of the category
			dataTable.setValue(i, 1, ft_category.frequency);
		}

		// Set options
		options = PieChartOptions.create();
		options.setBackgroundColor("transparent");

		// options.setColors(colors);
		options.setFontName("Tahoma");
		options.setIs3D(false);
		options.setPieResidueSliceColor("#000000");
		options.setPieResidueSliceLabel("Others");
		options.setSliceVisibilityThreshold(0);
		options.setTitle(columnName);
		options.setPieSliceText(PieSliceText.PERCENTAGE);

		// Draw the chart
		chart.draw(dataTable, options);
		
		// how get click on pie?
		chart.addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				NativeEvent event = e.getNativeEvent();
				System.out.println("PieChartView.draw().onClick(): source = " + e.getSource().toString());
			}
		}, ClickEvent.getType());

		
		chart.addReadyHandler(new ReadyHandler()
		{
			@Override
			public void onReady(ReadyEvent event)
			{
				//chart.setSelection(Selection.create(1, null));
//				System.out.println("PieChartView.draw().onReady(): w = " + chart.getOffsetWidth() + ", h = " + chart.getOffsetHeight());
			}
		});
	}

	/**
	 * Teken het cirkeldiagram op het canvas.
	 * 
	 * @param context
	 */
	private void drawPieChart(Context2d context)
	{
		
		
	}

	private void setMainPanelSize()
	{
		this.scrollPanel.setPixelSize(this.getWidth(), this.getHeight() - this.model.getStatTableModel().getDialogButtonHeight());
		this.scrollPanel.setAlwaysShowScrollBars(false);
		this.alles.setPixelSize(this.getWidth(), this.getHeight());
		this.mainPanel.setPixelSize(this.getWidth(), this.getHeight() - this.model.getStatTableModel().getDialogButtonHeight());
	}
	
	// Override setBound
	public void setBounds(int x, int y, int w, int h)
	{
		this.setMainPanelSize();
	}

	public void setModel(PieChartModel model)
	{
		this.model = model;
		userOptionsPanel.setModel(model);
		this.update();
		this.userOptionsPanel.update();
	}

	/*
	 * PieChartClickhandler is used on the slices in the pie chart.
	 */
	private class PieChartClickHandler implements ClickHandler
	{

		@Override
		public void onClick(ClickEvent event)
		{
			for (int i = 0; i < frequencies_enum.length; i++)
			{
				if (event.getSource().equals("")); //get enum_description[i]
				{
					pieSliceClicked(i);
					break;
				}
			}
		}

		/**
		 * The user clicked on a pie slice in the pie chart.
		 * 
		 * @param enum category
		 */
		private void pieSliceClicked(int enumIndex)
		{
			if (!PieChartView.this.model.columnIndexValid())
			{
				return;
			}
			
			// update the selectionList

			ColumnType cType = PieChartView.this.model.getStatTableModel()
				.getColumnTypes()
				.get(PieChartView.this.model.getColumnIndex());

			String clicked;
			clicked = cType.getEnumOptions()[enumIndex];
			int wildcardIndex = Arrays.asList(cType.getEnumOptions())
				.indexOf(ColumnType.WILDCARD);
			if (wildcardIndex <= 0 && wildcardIndex < enumIndex)
			{
				clicked = cType.getEnumOptions()[enumIndex + 1];
			}

			ArrayList<Boolean> selectionList = new ArrayList<Boolean>(
				PieChartView.this.model.getStatTableModel().getRowCount());
			for (int i = 0; i < PieChartView.this.model
				.getStatTableModel().getRowCount(); i++)
			{
				Object o = PieChartView.this.model.getStatTableModel()
					.getValueAt(i,
						PieChartView.this.model.getColumnIndex());

				selectionList.add(
					!o.equals(ColumnType.WILDCARD)
					&& ((String) o).equals(clicked)
					&& PieChartView.this.model.getStatTableModel()
						.classifyObject(i,
							new SplitOptions()) == enumIndex);
			}
			PieChartView.this.model.getStatTableModel().setSelectionList(
				selectionList);
			
			PieChartView.this.update();
		}

	} // class PieChartClickHandler

	@Override
	public void onSelectionChange(SelectionChangeEvent event)
	{
		this.update();
	}

	@Override
	public void onTableChange(TableChangeEvent event)
	{
		GWT.log("PieChartView.onTableChange()");

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
				if (event.getInfo().equals(TableChangeEvent.REMOVE_COLUMN))
				{
					this.model.updateColumnIndex(event.getColumnIndex());
				}
				
				// update both view and user options panel
				this.update();
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
		this.outlierChangeEventHandlerRegistration.removeHandler();
	}

	@Override
	public void onOutlierChange(OutlierChangeEvent event)
	{
		this.update();
	}

	public PieChartUserOptionsPanel getUserOptionsPanel()
	{
		return userOptionsPanel;
	}

//	public void setEditable(boolean editable)
//	{
//		this.alles.setStyleDependentName("readonly", !editable);
//
//		// als niet-editable, dan mainPanel in scrollpanel niet laten reageren op pointer events
//		if (!editable)
//		{
//			mainPanel.getElement().getStyle().setProperty("pointerEvents", "none");
//		}
//		else
//		{
//			mainPanel.getElement().getStyle().clearProperty("pointerEvents");
//		}
//		
//		dialogButton.setEnabled(editable);		
//	}
}
