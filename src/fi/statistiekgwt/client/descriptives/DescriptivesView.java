package fi.statistiekgwt.client.descriptives;

import java.util.ArrayList;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import fi.statistiekgwt.client.ColorUtils;
import fi.statistiekgwt.client.DialogButton;
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
 * MVC View for statistiekView Descriptives
 * 
 * @author Sylvia van Borkulo
 * 
 */
public class DescriptivesView extends LayoutPanel implements TableChangeEventHandler, SelectionChangeEventHandler, 
	OutlierChangeEventHandler, HasHandlers
{
	private DescriptivesModel model;
	private DescriptivesController controller;
	private DescriptivesUserOptionsPanel userOptionsPanel;
	private DialogButton dialogButton;

	/**
	 * Op panel 'alles' staan mainpanel en dialogbuttonpanel.
	 */
	private FlowPanel alles;
	private FlowPanel mainPanel;
	private ScrollPanel scrollPanel;
	private HorizontalPanel dialogButtonPanel;

	public static final CssColor SELECTED_COLOR = ColorUtils.SELECTION_COLOR;
	public static final int GRID_TOPGAP = 5;
	public static final int GRID_BOTTOMGAP = 5;
	public static final int GRID_LEFTGAP = 5;
	public static final int GRID_RIGHTGAP = 5;
	/**
	 * The number of decriptives in the table.
	 */
	private static int NUMBER_OF_DESCRIPTIVES = 7;
	/**
	 * Number of rows in a single descriptives table, 
	 * including header row
	 */
	private static int NUMBER_OF_TABLE_ROWS = 8;
	/**
	 * The type of the selected variable
	 */
	private AllowedTypes typeColumnIndex;
	/**
	 * The type of the split variable
	 */
	private AllowedTypes typeSplitVar;
	private int minRowHeight = 30;
	
	/**
	 * The frequencies in case of an enum rows variable
	 */
	private FrequencyTuple[][] frequencies_enum;
	/**
	 * The frequencies in case of a number rows variable
	 */
	private int[][] frequencies_number;
	/**
	 * The total frequencies per column bin (i.e., the split bins) 
	 */
	int[] aantalPerColumnBin;

	/**
	 * The labels with the data[descriptive fields][0..1 if selection][splitClass].
	 * Contains descriptive fields:
	 *   - number of cases
	 *   - minimum
	 *   - maximum
	 *   - mean
	 *   - standard deviation
	 *   - median
	 *   - modus
	 */
	private Label[][][] dataLabels;
	
	/**
	 * The number of cases[0..1 if selection][splitClass].
	 */
	private int[][] numberOfCases;
	
	/**
	 * The minimum[0..1 if selection][splitClass].
	 */
	private int[][] minimum;
	
	/**
	 * The maximum[0..1 if selection][splitClass].
	 */
	private int[][] maximum;
	
	/**
	 * The mean[0..1 if selection][splitClass].
	 */
	private int[][] mean;
	
	/**
	 * The standard deviation[0..1 if selection][splitClass].
	 */
	private int[][] standardDeviation;
	
	/**
	 * The median[0..1 if selection][splitClass].
	 */
	private int[][] median;
	
	/**
	 * The mode[0..1 if selection][splitClass].
	 */
	private String[][] mode;
	
	/**
	 * Bin labels for the split variable. Split is not yet implemented.
	 */
	private Label[] binLabelsSplitVar;
	
	private int[] maxColumnWidth;

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
	public DescriptivesView(DescriptivesModel model,
		DescriptivesController controller)
	{
		super();

		this.alles = new FlowPanel();
		
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();
		
		this.model = model;
		this.controller = controller;
		
		this.dummyTouchHandler = StatistiekUtils.getDummyTouchHandler();
		this.eventBus = StatistiekUtils.EVENT_BUS;//new SimpleEventBus();
		
		// bind descriptivesview to stattablemodel: to handle table changes in stattablemodel
		this.tableChangeEventHandlerRegistration = this.model.getStatTableModel().addTableChangeEventHandler(this);

		// bind descriptivesview to stattablemodel: to handle selection changes in stattablemodel
		this.selectionChangeEventHandlerRegistration = this.model.getStatTableModel().addSelectionChangeEventHandler(this);

		// bind descriptivesview to stattablemodel: to handle selection changes in stattablemodel
		this.outlierChangeEventHandlerRegistration = this.model.getStatTableModel().addOutlierChangeEventHandler(this);

		// create GUI
		this.mainPanel = new FlowPanel();
		
		this.scrollPanel = new ScrollPanel(this.mainPanel);
		this.scrollPanel.setAlwaysShowScrollBars(false);
		this.scrollPanel.addStyleName(statistiekCss.backgroundwhite());
		
		this.alles.add(this.scrollPanel);
		
		this.userOptionsPanel = new DescriptivesUserOptionsPanel(this, controller, model);
		// initial update for setting widgets in user options panel
		this.userOptionsPanel.update();

		this.initializeSize();
		
		this.dialogButtonPanel = new HorizontalPanel();
		this.dialogButtonPanel.setWidth("100%");
		this.dialogButtonPanel.setHeight(StatistiekGWT.BUTTON_HEIGHT + "px");//"100%");
		this.dialogButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.dialogButtonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.dialogButtonPanel.addStyleName(statistiekCss.backgroundblue());
		this.dialogButton = userOptionsPanel.getDialogButton();
		this.dialogButtonPanel.add(this.dialogButton);

		this.alles.add(this.dialogButtonPanel);//, StatistiekGWT.BUTTON_HEIGHT);
		if (!this.model.getStatTableModel().isViewsEditable())
		{
			//this.alles.setWidgetSize(dialogButtonPanel, 0);
			this.dialogButtonPanel.setHeight("0px");
		}

		this.alles.setPixelSize(this.getWidth(), this.getHeight() + StatistiekGWT.BUTTON_HEIGHT);
		
		// add alles to descriptivesview (layoutpanel)
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

	private int tupleArraySum(FrequencyTuple[] array)
	{
		int sum = 0;
		for (FrequencyTuple ft : array)
		{
			sum += ft.frequency;
		}
		return sum;
	}

	public void update()
	{
//		System.out.println("DescriptivesView.update(): splitIndex = " + this.model.getSplitOptions().getColumnSplitIndex());
		
		this.dialogButton.setVisible(this.model.getStatTableModel()
			.isViewsEditable());
		
//		this.setMainPanelSize();
//
		StatistiekUtils.removeAllWidgetsFromPanel(this.mainPanel);
		
		if (this.model.columnIndexValid())
		{
			ColumnType cTypeColumnIndex = this.model.getStatTableModel().getColumnTypes()
				.get(this.model.getColumnIndex());
			this.typeColumnIndex = cTypeColumnIndex.getType();
			
			ColumnType cTypeSplit;
			if (this.model.getColumnSplitIndex() > -1)
			{
				cTypeSplit = this.model.getStatTableModel().getColumnTypes()
					.get(this.model.getColumnSplitIndex());
				typeSplitVar = cTypeSplit.getType();
			}
			
			if (this.typeColumnIndex.isNumber())
			{
				this.frequencies_number = this.model.numberClassFrequency();
			}
			else
			{ // enum or string
				this.frequencies_enum = this.model.enumClassFrequency();
			}

			setData();

			makeDescritivesTable();
			
		} // columnIndexValid()

		this.setMainPanelSize();

		// update the components in the useroptionspanel
		userOptionsPanel.update();
	}

	private void setMainPanelSize()
	{
		int splitClasses = this.model.getStatTableModel().numberOfSplitVarClasses(
			this.model.getSplitOptions());
		
		int scrollWidth = this.scrollPanel.getElement().getScrollWidth();
		int scrollHeight = this.scrollPanel.getElement().getScrollHeight();

		this.scrollPanel.setPixelSize(this.getWidth(), this.getHeight());
		this.scrollPanel.setAlwaysShowScrollBars(false);

		//this.mainPanel.setPixelSize(this.getWidth() - 8, splitClasses * this.getHeight());
	}

	/**
	 * Calculate the data and set the data labels used to create the descriptives table.
	 */
	private void setData()
	{
		int numberOfSplits = DescriptivesView.this.model.getStatTableModel()
			.numberOfSplitVarClasses(DescriptivesView.this.model.getSplitOptions());
		
		int columnIndex = this.model.getColumnIndex(); // this.varBoxSelectedIndex() is nog niet geupdate!

		if (this.hasSelection())
		{
			this.numberOfCases = new int[2][numberOfSplits];
			this.mode = new String[2][numberOfSplits];
		}
		else
		{
			this.numberOfCases = new int[1][numberOfSplits];
			this.mode = new String[1][numberOfSplits];
		}
		
		for (int i = 0; i < numberOfSplits; i++)
		{
			// number of cases, missing excluded (missing is wildcard '*')
			this.numberOfCases[0][i] = this.getNumberOfCases(0,i);

			if (this.hasSelection())
			{
				this.numberOfCases[1][i] = this.getNumberOfCases(1, i);
			}
		}
		
		
		for (int i = 0; i < numberOfSplits; i++)
		{
			String s;

			s = this.model.getColumnMode(columnIndex, i, false);
			if (this.isNumeric(s))
			{
				this.mode[0][i] = StatistiekGWT.getStringValue(Double.valueOf(s));
			}
			else
			{
				this.mode[0][i] = s;
			}
			
			if (this.hasSelection())
			{
//				s = this.model.getTableModel().getColumnModeOfSelection(columnIndex);
				s = this.model.getColumnMode(columnIndex, i, true);
				if (this.isNumeric(s))
				{
					this.mode[1][i] = StatistiekGWT.getStringValue(Double
						.valueOf(s));
				}
				else
				{
					this.mode[1][i] = s;
				}
			}
		}		
		
		// set data for both the main table and the selection table
		setDataLabels();
	}

	/**
	 * Get the number of cases for the given splitClass,
	 * excluding wildcards and outliers. 
	 * 
	 * @param selection
	 * 		If selection is 0, the number of cases for the splitClass is returned.
	 * 		If selection is 1, the number of selected cases for the splitClass is returned. 
	 * @param splitClass
	 * @return
	 */
	private int getNumberOfCases(int selection, int splitClass)
	{
		int numberOfCases = 0;
		
		if (this.typeColumnIndex.isNumber())
		{
			if (this.frequencies_number != null)
			{
				int[] frequencies = frequencies_number[splitClass];

				// Er is altijd maar 1 bin
				if (selection == 0)
				{
					numberOfCases = frequencies[0];
				}
				else
				{
					numberOfCases = frequencies[1];
				}
			}
		}
		else
		{ // enum or string
			if (frequencies_enum != null)
			{
				FrequencyTuple[] frequencies = frequencies_enum[splitClass];
				int sum = 0;
				
				for (int i = 0; i < frequencies.length; i++)
				{
					if (!frequencies[i].label.equals(ColumnType.WILDCARD))
					{
						if (selection == 0)
						{
							sum += frequencies[i].frequency;
						}
						else
						{
							sum += frequencies[i].selectionFrequency;
						}
					}
				}
				numberOfCases = sum;
			}
		}
		
		return numberOfCases;
	}

	/**
	 * Checks whether string s contains a numerical value.
	 * 
	 * @param s
	 * @return True if string s contains a numerical value, else false.
	 */
	private boolean isNumeric(String s)
	{
		try
		{
			double d = Double.parseDouble(s);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		return true;
	}

	/**
	 * Set the data labels to be used in the descriptives table.
	 */
	private void setDataLabels()
	{
		int numberOfSplits = DescriptivesView.this.model.getStatTableModel()
			.numberOfSplitVarClasses(DescriptivesView.this.model.getSplitOptions());

		if (this.hasSelection())
		{
			dataLabels = new Label[this.NUMBER_OF_DESCRIPTIVES][2][numberOfSplits];
		}
		else
		{
			dataLabels = new Label[this.NUMBER_OF_DESCRIPTIVES][1][numberOfSplits];
		}

		for (int i = 0; i < numberOfSplits; i++)
		{
			// the main data
			this.setDataLabels(0, i);
			
			if (this.hasSelection())
			{
				// the selection data
				this.setDataLabels(1, i);
			}
		}
	}

	public Label[][][] getDataLabels()
	{
		return dataLabels;
	}

	public int[][] getNumberOfCases()
	{
		return numberOfCases;
	}

	public String[][] getMode()
	{
		return mode;
	}

	/**
	 * Set the data labels for minimum, maximum, mean, standard deviation and median,
	 * for the given splitclass and for all cases or the selected cases.
	 * 
	 * @param selection 0 for no selection, all included, 1 for the selected cases.
	 * @param splitClass
	 */
	private void setDataLabels(int selection, int splitClass)
	{
		String minimumString;
		String maximumString;
		String meanString;
		String sdString;
		String medianString;

		if (this.typeColumnIndex.isNumber() && (this.numberOfCases[selection][splitClass] > 0))
		{
			int columnIndex = this.model.getColumnIndex(); // this.varBoxSelectedIndex() is nog niet geupdate!
			int numberOfDecimals = determineMaxNumberOfDecimals(columnIndex) + 2;
			
			if (selection == 0)
			{
				minimumString = this.getMinimumValue(columnIndex, splitClass, false);
				maximumString = this.getMaximumValue(columnIndex, splitClass, false);
				
				meanString = this.model.getColumnMean(columnIndex, splitClass, false); 
				
				if (!meanString.equals(StatistiekGWT.rb.notAvailable()))
				{
					meanString = StatistiekGWT.getFormatted(meanString, numberOfDecimals);
				}
				
				sdString = this.model.getColumnSD(columnIndex, splitClass, false);
				if (!sdString.equals(StatistiekGWT.rb.notAvailable()))
				{
					sdString = StatistiekGWT.getFormatted(sdString, numberOfDecimals);
				}
				medianString = getMedianValue(columnIndex, splitClass, false);
			}
			else
			{
				minimumString = this.getMinimumValue(columnIndex, splitClass, true);
				maximumString = this.getMaximumValue(columnIndex, splitClass, true);
				
				meanString = this.model.getColumnMean(columnIndex, splitClass, true);
				if (!meanString.equals(StatistiekGWT.rb.notAvailable()))
				{
					meanString = StatistiekGWT.getFormatted(meanString, numberOfDecimals);
				}
				
				sdString = this.model.getColumnSD(columnIndex, splitClass, true);
				if (!sdString.equals(StatistiekGWT.rb.notAvailable()))
				{
					sdString = StatistiekGWT.getFormatted(sdString, numberOfDecimals);
				}
				medianString = getMedianValue(columnIndex, splitClass, true);
			}
		} // type is number
		else
		{ // type is enum or string
			minimumString = StatistiekGWT.rb.notAvailable();
			maximumString = StatistiekGWT.rb.notAvailable();
			meanString = StatistiekGWT.rb.notAvailable();
			sdString = StatistiekGWT.rb.notAvailable();
			medianString = StatistiekGWT.rb.notAvailable();
		}

		// number of cases
		dataLabels[0][selection][splitClass] = new Label(String.valueOf(this.numberOfCases[selection][splitClass]));
		dataLabels[0][selection][splitClass].addStyleName(statistiekCss.noWrap());
		
		// minimum
		dataLabels[1][selection][splitClass] = new Label(minimumString);
		dataLabels[1][selection][splitClass].addStyleName(statistiekCss.noWrap());

		// maximum
		dataLabels[2][selection][splitClass] = new Label(maximumString);
		dataLabels[2][selection][splitClass].addStyleName(statistiekCss.noWrap());

		// mean
		dataLabels[3][selection][splitClass] = new Label(meanString);
		dataLabels[3][selection][splitClass].addStyleName(statistiekCss.noWrap());

		// standard deviation
		dataLabels[4][selection][splitClass] = new Label(sdString);
		dataLabels[4][selection][splitClass].addStyleName(statistiekCss.noWrap());

		// median
		dataLabels[5][selection][splitClass] = new Label(medianString);
		dataLabels[5][selection][splitClass].addStyleName(statistiekCss.noWrap());

		// modus
		dataLabels[6][selection][splitClass] = new Label(this.mode[selection][splitClass]);
		dataLabels[6][selection][splitClass].addStyleName(statistiekCss.noWrap());

		if (selection == 1)
		{
			this.setSelectionColors(selection, splitClass);
		}
	}

	private void setSelectionColors(int selection, int splitClass)
	{
		for (int i = 0; i < this.NUMBER_OF_DESCRIPTIVES; i++)
		{
//			dataLabels[i][selection][splitClass].getElement().getStyle().setBackgroundColor(this.SELECTED_COLOR.value());
//			dataLabels[i][selection][splitClass].getElement().getStyle().setOpacity(1.0);
			dataLabels[i][selection][splitClass].addStyleName(statistiekCss.backgroundgrey());
		}
	}

	private String getMedianValue(int columnIndex, int splitClass, boolean forSelection)
	{
		String medianValue;
		
		int numberOfDecimals = determineMaxNumberOfDecimals(columnIndex) + 2;

		double medianDouble = this.model.getColumnMedian(columnIndex, splitClass, forSelection);

		medianValue = StatistiekGWT.getStringValue(StatistiekGWT.round(medianDouble, numberOfDecimals));
		
		return medianValue;
	}

	private String getMaximumValue(int columnIndex, int splitClass, boolean forSelection)
	{
		String maximumValue = this.model.getColumnMax(columnIndex, splitClass, forSelection);
		
		return maximumValue;
	}

	private String getMinimumValue(int columnIndex, int splitClass, boolean forSelection)
	{
		String minimumValue = this.model.getColumnMin(columnIndex, splitClass, forSelection);
		
		return minimumValue;
	}

	/**
	 * Determine the number of decimals that is used in column columnIndex. 
	 * 
	 * @param columnIndex
	 * @return
	 */
	private int determineMaxNumberOfDecimals(int columnIndex)
	{
		String numberString;
		int max = 0;
		int integerPlaces, decimalPlaces;
		for (int i = 0; i < this.model.getStatTableModel().getRowCount(); i++)
		{
			if (!this.model.getStatTableModel().isOutlier(i, columnIndex))
			{
				numberString = String.valueOf(this.model.getStatTableModel().getValueAt(i, columnIndex));
				integerPlaces = numberString.indexOf('.');
				if (integerPlaces > -1)
				{
					decimalPlaces = numberString.length() - integerPlaces - 1;
					if (decimalPlaces > max)
					{
						max = decimalPlaces;
					}
				}
			}
		}
		return max;
	}

	/**
	 * Calculates the percentage value of the ratio frequency/divisor. If divisor is 0, 0 is returned.
	 * If the percentage value is an integer value, no decimals are returned in waardeString.
	 * 
	 * @param frequency
	 * @param divisor
	 * @return waardeString The percentage value of the ratio frequency/divisor
	 */
	private String getWaardeString(int frequency, int divisor)
	{
		double waarde;
		String waardeString;
		NumberFormat df = StatistiekGWT.getDefaultNumberFormat();
		
		if (divisor != 0)
		{
			waarde = ((double) frequency/divisor)*100;
			waardeString = df.format(waarde);
		}
		else
			waardeString = "0";
		
		return waardeString;
	}

	/**
	 * Make the descriptives table view on panel. When there is a selection, 
	 * a table for the selected data is shown next to the descriptives table
	 * for the complete data set.
	 */
	private void makeDescritivesTable()
	{
		int numberOfSplits = DescriptivesView.this.model.getStatTableModel()
			.numberOfSplitVarClasses(DescriptivesView.this.model.getSplitOptions());

		for (int i = 0; i < numberOfSplits; i++)
		{
			makeDescriptivesTable(i);
		}
	}

	/**
	 * Make the descriptives table.
	 * 
	 * @param selection
	 * 		If selection is 0, the main descriptives table is made.
	 * 		If selection is 1, the descriptive table for the selection is made.
	 * @param splitClass
	 */
	private void makeDescriptivesTable(int splitClass)
	{
		FlowPanel flowPanel = new FlowPanel();
		
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.addStyleName(statistiekCss.horizontalPanelDescriptives());
		
		FlexTable flexTable = new FlexTable();
		FlexCellFormatter cellFormatter = flexTable.getFlexCellFormatter();
	    flexTable.addStyleName(statistiekCss.flexTable());

		// add the selected variable name to the first column
		String label = this.model.getStatTableModel().getColumnName(this.model.getColumnIndex());
		Label columnIndexName = new Label(label);
		columnIndexName.addStyleName(statistiekCss.noWrap());

	    cellFormatter.setHorizontalAlignment(
	        0, 0, HasHorizontalAlignment.ALIGN_LEFT);
	    flexTable.setWidget(0, 0, columnIndexName);
	    cellFormatter.setColSpan(0, 0, 2);

		// add row 'Number'
		Label aantalLabel = new Label(StatistiekGWT.rb.amountLabel());
	    flexTable.setWidget(1, 0, aantalLabel);
	    flexTable.setWidget(1, 1, dataLabels[0][0][splitClass]);

		// add row 'Minimum'
		Label minLabel = new Label(StatistiekGWT.rb.minimum());
	    flexTable.setWidget(2, 0, minLabel);
	    flexTable.setWidget(2, 1, dataLabels[1][0][splitClass]);
		
		// add row 'Maximum'
		Label maxLabel = new Label(StatistiekGWT.rb.maximum());
	    flexTable.setWidget(3, 0, maxLabel);
	    flexTable.setWidget(3, 1, dataLabels[2][0][splitClass]);
		
		// add row 'Mean'
		Label meanLabel = new Label(StatistiekGWT.rb.mean());
	    flexTable.setWidget(4, 0, meanLabel);
	    flexTable.setWidget(4, 1, dataLabels[3][0][splitClass]);
		
		// add row 'Standard Deviation'
		Label sdLabel = new Label(StatistiekGWT.rb.standardDeviation());
	    flexTable.setWidget(5, 0, sdLabel);
	    flexTable.setWidget(5, 1, dataLabels[4][0][splitClass]);
		
		// add row 'Median'
		Label medianLabel = new Label(StatistiekGWT.rb.median());
	    flexTable.setWidget(6, 0, medianLabel);
	    flexTable.setWidget(6, 1, dataLabels[5][0][splitClass]);
		
		// add row 'Modus'
		Label modusLabel = new Label(StatistiekGWT.rb.mode());
	    flexTable.setWidget(7, 0, modusLabel);
	    flexTable.setWidget(7, 1, dataLabels[6][0][splitClass]);
		
		hPanel.add(flexTable);
		
		if (this.hasSelection())
		{
			FlexTable selectionFlexTable = new FlexTable();
		    selectionFlexTable.addStyleName(statistiekCss.flexTableSelection());
		    FlexCellFormatter cellFormatterSelection = selectionFlexTable.getFlexCellFormatter();

			// add the selected variable name to the first column
			label = StatistiekGWT.rb.selection();
			Label selectionLabel = new Label(label);

		    cellFormatterSelection.setHorizontalAlignment(
		        0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		    selectionFlexTable.setWidget(0, 0, selectionLabel);
		    cellFormatterSelection.setColSpan(0, 0, 2);

			// add row 'Number'
		    Label aantalLabelSelection = new Label(aantalLabel.getText());
		    selectionFlexTable.setWidget(1, 0, aantalLabelSelection);
		    selectionFlexTable.setWidget(1, 1, dataLabels[0][1][splitClass]);

			// add row 'Minimum'
		    Label minLabelSelection = new Label(minLabel.getText());
		    selectionFlexTable.setWidget(2, 0, minLabelSelection);
		    selectionFlexTable.setWidget(2, 1, dataLabels[1][1][splitClass]);
			
			// add row 'Maximum'
		    Label maxLabelSelection = new Label(maxLabel.getText());
		    selectionFlexTable.setWidget(3, 0, maxLabelSelection);
		    selectionFlexTable.setWidget(3, 1, dataLabels[2][1][splitClass]);
			
			// add row 'Mean'
		    Label meanLabelSelection = new Label(meanLabel.getText());
		    selectionFlexTable.setWidget(4, 0, meanLabelSelection);
		    selectionFlexTable.setWidget(4, 1, dataLabels[3][1][splitClass]);
			
			// add row 'Standard Deviation'
		    Label sdLabelSelection = new Label(sdLabel.getText());
		    selectionFlexTable.setWidget(5, 0, sdLabelSelection);
		    selectionFlexTable.setWidget(5, 1, dataLabels[4][1][splitClass]);
			
			// add row 'Median'
		    Label medianLabelSelection = new Label(medianLabel.getText());
		    selectionFlexTable.setWidget(6, 0, medianLabelSelection);
		    selectionFlexTable.setWidget(6, 1, dataLabels[5][1][splitClass]);
			
			// add row 'Modus'
		    Label modusLabelSelection = new Label(modusLabel.getText());
		    selectionFlexTable.setWidget(7, 0, modusLabelSelection);
		    selectionFlexTable.setWidget(7, 1, dataLabels[6][1][splitClass]);
		    
			// voeg selection flextable toe aan horizontalpanel
		    hPanel.add(selectionFlexTable);
		}

		flowPanel.add(hPanel);
		
		if (this.hasSplit())
		{
			String splitColumnName = this.model.getStatTableModel().getColumnName(this.model.getColumnSplitIndex());
			String splitClassString = splitColumnName + ": "
				+ this.model.getSplitOptions().getSplitClassLabel(splitClass, this.model.getStatTableModel()); // e.g., "geslacht: m"
			Label splitClassLabel = new Label(splitClassString);
			splitClassLabel.addStyleName(statistiekCss.descriptivesSplitClassLabel());
			
			flowPanel.add(splitClassLabel);
		}

		this.mainPanel.add(flowPanel);
	}

	private boolean lastRowInView(int splitClass)
	{
		int numberOfSplits = DescriptivesView.this.model.getStatTableModel()
			.numberOfSplitVarClasses(DescriptivesView.this.model.getSplitOptions());
		
		return (splitClass == numberOfSplits - 1);
	}

	/**
	 * Returns true if the view has a split, else false.
	 * @return
	 * 		 True if the view has a split, else false.
	 */
	private boolean hasSplit()
	{
		return (this.model.getColumnSplitIndex() > -1);
	}

	private boolean hasSelection()
	{
		boolean hasSelection = false;
		
		ArrayList<Boolean> list = this.model.getStatTableModel().getSelectionList();
		
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i).booleanValue())
			{
				hasSelection = true;
				break;
			}
		}
		
		return hasSelection;
	}
	
	// Override setBound
	public void setBounds(int x, int y, int w, int h)
	{
		this.setMainPanelSize();
	}

	public void setModel(DescriptivesModel model)
	{
		this.model = model;
		userOptionsPanel.setModel(model);
		this.update();
		this.userOptionsPanel.update();
	}

	public int varBoxSelectedIndex()
	{
		return userOptionsPanel.getColumnIndexBoxSelectedIndex();
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
		return userOptionsPanel.getSplitminBoundary();
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

	/**
	 *Set the split bin width based on the model's split bin boundaries. 
	 */
	public void setSplitBinWidth()
	{
		this.userOptionsPanel.setSplitBinWidth();
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event)
	{
		this.update();
	}

	@Override
	public void onTableChange(TableChangeEvent event)
	{
		GWT.log("DescriptivesView.onTableChange()");

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
				if (event.getInfo().equals(TableChangeEvent.REMOVE_ROW)
					|| event.getInfo().equals(TableChangeEvent.REMOVE_ROWS))
				{
					this.recalculateBinBoundaries();
					
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
						// bins opnieuw berekenen
						this.recalculateBinBoundaries();
					}
					else if (event.getColumnIndex() == this.model.getSplitOptions().getColumnSplitIndex())
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
	 * Recalculate the bin boundaries for descriptives's columnIndex
	 * if possible.
	 * This is necessary to calculate the frequencies, in the whole data set and
	 * in the selection. There is always one bin.
	 * 
	 * @param typeHasChanged
	 * 		The type has changed yes/no.
	 */
	public void recalculateBinBoundaries()
	{
			if (this.model.columnIndexValid())
			{
				ArrayList<ColumnType> list = this.model.getStatTableModel().getColumnTypes();
				if (list.get(this.model.getColumnIndex())
					.getType().isNumber())
				{
    				// binBoundaries worden hier standaard gezet
    				ArrayList<Double> binBoundaries = StatistiekGWT
    					.appropriateBoundaries(
    						this.model.getStatTableModel().getColumnMin(this.model.getColumnIndex()),
    						this.model.getStatTableModel().getColumnMax(this.model.getColumnIndex()),
    						1);
    				this.model.setBinBoundaries(binBoundaries);
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
	 * @return the userOptionsPanel
	 */
	public DescriptivesUserOptionsPanel getUserOptionsPanel()
	{
		return userOptionsPanel;
	}

	@Override
	public void onOutlierChange(OutlierChangeEvent event)
	{
		this.update();
	}
}
