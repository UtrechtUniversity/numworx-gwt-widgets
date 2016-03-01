package fi.statistiekgwt.client.crosstabulationtable;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

import fi.statistiekgwt.client.ColorUtils;
import fi.statistiekgwt.client.ColorUtils.RGBColor;
import fi.statistiekgwt.client.ColorPreviewer;
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
 * MVC View for statistiekView CrossTabulationTable
 * 
 * @author Sylvia van Borkulo
 * 
 */
public class CrossTabulationTableView extends LayoutPanel implements TableChangeEventHandler, SelectionChangeEventHandler, 
	OutlierChangeEventHandler, HasHandlers
{
	private CrossTabulationTableModel model;
	private CrossTabulationTableController controller;
	private CrossTabulationTableUserOptionsPanel userOptionsPanel;
	private DialogButton dialogButton;

	/**
	 * Op panel 'alles' staan mainpanel en dialogbuttonpanel.
	 */
	private FlowPanel alles;
	private FlowPanel mainPanel;
	private ScrollPanel scrollPanel;
	private HorizontalPanel dialogButtonPanel;
	private int numberOfColumnBins = 0;
	private int numberOfRowBins = 0;

	/**
	 * Number of rows in the crosstab table, 
	 * including header and bottom rows
	 */
	private int crosstabRows;
	/**
	 * The type of the rows
	 */
	private AllowedTypes typeRows;
	/**
	 * The type of the columns
	 */
	private AllowedTypes typeColumns;
//	/**
//	 * Number of columns in the crosstab table,
//	 * including variable name, bins and total columns
//	 */
//	private int crosstabColumns;
//	private int minRowHeight = 30;
	
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
	 * The total frequencies per row bin 
	 */
	private int[] aantalPerRowBin;
	private int aantalTotaal;

	private Label[][] data;
	private Label[] binLabelsRows;
	private Label[] binLabelsColumns;
	/**
	 * cellColors contains cssColor strings
	 */
	private String[][] cellColors;
	
	public static final RGBColor SELECTED_COLOR = ColorUtils.SELECTION_COLOR_RGB;
	public static final int GRID_TOPGAP = 5;
	public static final int GRID_BOTTOMGAP = 5;
	public static final int GRID_LEFTGAP = 5;
	public static final int GRID_RIGHTGAP = 5;
//	private int ROW_HEIGHT;
//	private int TABLE_WIDTH;
//	private int[] maxColumnWidth;

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
	public CrossTabulationTableView(CrossTabulationTableModel model,
		CrossTabulationTableController controller)
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
		
		// bind crosstabulationtableview to stattablemodel: to handle table changes in stattablemodel
		this.tableChangeEventHandlerRegistration = this.model.getStatTableModel().addTableChangeEventHandler(this);

		// bind crosstabulationtableview to stattablemodel: to handle selection changes in stattablemodel
		this.selectionChangeEventHandlerRegistration = this.model.getStatTableModel().addSelectionChangeEventHandler(this);
		
		// bind crosstabulationtableview to stattablemodel: to handle outlier changes in stattablemodel
		this.outlierChangeEventHandlerRegistration = this.model.getStatTableModel().addOutlierChangeEventHandler(this);
		
		// create GUI
		this.mainPanel = new FlowPanel();
		
		this.scrollPanel = new ScrollPanel(this.mainPanel);
		this.scrollPanel.setAlwaysShowScrollBars(false);
		this.scrollPanel.addStyleName(statistiekCss.backgroundwhite());
		
		this.alles.add(this.scrollPanel);

		this.userOptionsPanel = new CrossTabulationTableUserOptionsPanel(this, controller, model);
		// initial update for setting widgets in user options panel
		this.userOptionsPanel.update();

		this.initializeSize();

		dialogButton = userOptionsPanel.getDialogButton();

		this.dialogButtonPanel = new HorizontalPanel();
		this.dialogButtonPanel.setWidth("100%");
		this.dialogButtonPanel.setHeight(StatistiekGWT.BUTTON_HEIGHT + "px");//"100%");
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

		this.alles.setPixelSize(this.getWidth(), this.getHeight() + StatistiekGWT.BUTTON_HEIGHT);
		
		// add alles to crosstabulationtableview (layoutpanel)
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

	public TextBox getBinWidthRowsField()
	{
		return this.userOptionsPanel.getBinWidthRowsField();
	}

	public TextBox getMinBoundaryRowsField()
	{
		return this.userOptionsPanel.getMinBoundaryRowsField();
	}

	public TextBox getBinWidthColumnsField()
	{
		return this.userOptionsPanel.getBinWidthColumnsField();
	}

	public TextBox getMinBoundaryColumnsField()
	{
		return this.userOptionsPanel.getMinBoundaryColumnsField();
	}

	public double getBinWidthRows()
	{
		return this.userOptionsPanel.getBinWidthRows();
	}

	public void setBinWidthRows(double d)
	{
		this.userOptionsPanel.setBinWidthRows(d);
	}

	public void setBinWidthRows()
	{
		this.userOptionsPanel.setBinWidthRows();
	}

	public double getMinBoundaryRows()
	{
		return this.userOptionsPanel.getMinBoundaryRows();
	}
	
	/**
	 * Set min boundary field for the row variable with value min
	 * @param min
	 */
	public void setMinBoundaryRows(double min)
	{
		this.userOptionsPanel.setMinBoundaryRows(min);
	}

	public double getBinWidthColumns()
	{
		return this.userOptionsPanel.getBinWidthColumns();
	}

	public void setBinWidthColumns(double d)
	{
		this.userOptionsPanel.setBinWidthColumns(d);
	}

	public void setBinWidthColumns()
	{
		this.userOptionsPanel.setBinWidthColumns();
	}

	public double getMinBoundaryColumns()
	{
		return this.userOptionsPanel.getMinBoundaryColumns();
	}
	
	/**
	 * Set min boundary field for the column variable with value min
	 * @param min
	 */
	public void setMinBoundaryColumns(double min)
	{
		this.userOptionsPanel.setMinBoundaryColumns(min);
	}

	public int varRowsBoxSelectedIndex()
	{
		return this.userOptionsPanel.getVarRowsBoxSelectedIndex();
	}

	public int varColumnsBoxSelectedIndex()
	{
		return this.userOptionsPanel.getVarColumnsBoxSelectedIndex();
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
	 * Checks whether percentage_endTotal radio button is selected.
	 * 
	 * @return true if percentage_endTotal is chosen
	 */
	public boolean percentage_endTotalSelected()
	{
		return userOptionsPanel.percentage_endTotalSelected();
	}

	/**
	 * Checks whether percentage_rowTotal radio button is selected.
	 * 
	 * @return true if percentage_rowTotal is chosen
	 */
	public boolean percentage_rowTotalSelected()
	{
		return userOptionsPanel.percentage_rowTotalSelected();
	}

	/**
	 * Checks whether percentage_columnTotal radio button is selected.
	 * 
	 * @return true if percentage_columnTotal is chosen
	 */
	public boolean percentage_columnTotalSelected()
	{
		return userOptionsPanel.percentage_columnTotalSelected();
	}

	public void update()
	{
		this.dialogButton.setVisible(this.model.getStatTableModel()
			.isViewsEditable());
	
		// update the components in the useroptionspanel
		userOptionsPanel.update();

		StatistiekUtils.removeAllWidgetsFromPanel(this.mainPanel);

		if (this.model.columnIndexValid())
		{
			ColumnType cTypeRows = this.model.getStatTableModel().getColumnTypes()
				.get(this.model.getColumnIndex());
			typeRows = cTypeRows.getType();
			
			ColumnType cTypeColumns;
			if (this.model.getColumnSplitIndex() > -1)
			{
				cTypeColumns = this.model.getStatTableModel().getColumnTypes()
					.get(this.model.getColumnSplitIndex());
				typeColumns = cTypeColumns.getType();
			}
			else
			{
				// er is geen split
				return;
			}

			if (typeRows.isNumber())
			{
				numberOfRowBins = this.model.getBinBoundaries().size() - 1;
			}
			else 
			{ // ENUM or STRING
				FrequencyTuple[][] freq = this.model.enumClassFrequency();
				if (freq != null)
				{
					numberOfRowBins = freq[0].length;
				}
				else
				{
					numberOfRowBins = 0;
				}
			}
			
			// test syl
			if (numberOfRowBins > 100)
			{
				System.out.println("CrossTabulationTableView.update(): number of rows is more than 100");
				return;
			}
			
						
			numberOfColumnBins = this.model.getStatTableModel().numberOfSplitVarClasses(
				this.model.getSplitOptions());
			
			if (typeRows.isNumber())
			{
				this.crosstabRows = 3 + numberOfRowBins;
				if (this.crosstabRows < 0)
				{
					this.crosstabRows = 3;
				}
				
				// objects cannot be classified in the bins and frequencies_number will be null 
				// when the binBoundaries are not updated yet 
				frequencies_number = this.model.numberClassFrequency();
			}
			else
			{
				frequencies_enum = this.model.enumClassFrequency();
				this.crosstabRows = 3 + numberOfRowBins;
			}
			this.cellColors = new String[numberOfColumnBins][numberOfRowBins];
			
			calculateData();

			// set colors for background
			if (typeRows.isNumber())
			{
				if (frequencies_number != null)
				{
					for (int i = 0; i < numberOfColumnBins; i++)
					{
						for (int j = 0; j < numberOfRowBins; j++)
						{
							double d = (frequencies_number[i][j * 2] == 0 ? 0.0
								: (double) frequencies_number[i][j * 2 + 1] / (double) frequencies_number[i][j * 2]);

							this.cellColors[i][j] = ColorPreviewer.mixColorsToString(
								ColorUtils.WHITE_RGB, SELECTED_COLOR, d);
						}
					}
				}
			}
			else
			{
				if (frequencies_enum != null)
				{
					for (int i = 0; i < numberOfColumnBins; i++)
					{
						for (int j = 0; j < numberOfRowBins; j++)
						{
							FrequencyTuple[] ft = frequencies_enum[i];
							double d = (ft[j].frequency == 0 ? 0
								: (double) ft[j].selectionFrequency
									/ (double) ft[j].frequency);

							this.cellColors[i][j] = ColorPreviewer.mixColorsToString(
								ColorUtils.WHITE_RGB, SELECTED_COLOR, d);
						}
					}
				}
			}
			
			makeTable();
			
		} // columnIndexValid()
	}

	/**
	 * Calculates the data fields used to create the crosstab table.
	 */
	private void calculateData()
	{
		aantalPerColumnBin = null;
		aantalPerRowBin = null;
		aantalTotaal = 0;
		
		int numberOfSplits = CrossTabulationTableView.this.model.getStatTableModel()
			.numberOfSplitVarClasses(CrossTabulationTableView.this.model.getSplitOptions());

		aantalPerColumnBin = new int[numberOfSplits];
		aantalPerRowBin = new int[numberOfRowBins];
		
		// bereken aantalPerSplit en aantalPerBin
		for (int i = 0; i < numberOfSplits; i++)
		{
			// tel de aantallen op voor split i
			aantalPerColumnBin[i] = 0;
			if (typeRows.isNumber() && frequencies_number != null) // number variable
			{
				for (int j = 0; j < numberOfRowBins; j++)
				{
					aantalPerColumnBin[i] = aantalPerColumnBin[i]
						+ frequencies_number[i][j * 2];
				}
			}
			else if (frequencies_enum != null) // enum variable
			{
				if ((frequencies_enum[0].length == numberOfRowBins) && (frequencies_enum.length == numberOfSplits))
				{
					for (int j = 0; j < numberOfRowBins; j++)
					{
						aantalPerColumnBin[i] = aantalPerColumnBin[i]
							+ frequencies_enum[i][j].frequency;
					}
				}
			}
			aantalTotaal = aantalTotaal + aantalPerColumnBin[i];
		}
		
		for (int j = 0; j < numberOfRowBins; j++)
		{
			// tel de aantallen op voor bin j
			
			aantalPerRowBin[j] = 0;
			if (typeRows.isNumber() && frequencies_number != null) // number variable
			{
				for (int i = 0; i < numberOfSplits; i++)
				{
					aantalPerRowBin[j] = aantalPerRowBin[j]
						+ frequencies_number[i][j * 2];
				}
			}
			else if (frequencies_enum != null) // enum variable
			{
				if ((frequencies_enum[0].length == numberOfRowBins) && (frequencies_enum.length == numberOfSplits))
				{
					for (int i = 0; i < numberOfSplits; i++)
					{
						aantalPerRowBin[j] = aantalPerRowBin[j]
							+ frequencies_enum[i][j].frequency;
					}
				}
			}
		}

		setDataLabels();
		setBinLabels();
	}

	/**
	 * Set the bin labels to be used in the crosstab table.
	 */
	private void setBinLabels()
	{
		// set the labels for the row variable
		binLabelsRows = new Label[numberOfRowBins];

		if (typeRows.isNumber())
		{
			for (int i = 0; i < binLabelsRows.length; i++)
			{
				String text;
				
				if (typeRows.equals(AllowedTypes.INTEGER) 
					&& (this.model.getBinBoundaries().get(1) - this.model.getBinBoundaries().get(0) == 1))
				{
					text = StatistiekGWT.getStringValue(this.model.getBinBoundaries().get(i));
				}
				else
				{
					text = StatistiekGWT.getStringValue(this.model.getBinBoundaries().get(i)) 
						+ "-<" 
						+ StatistiekGWT.getStringValue(this.model.getBinBoundaries().get(i + 1));
				}
				binLabelsRows[i] = new Label(text);
			}
		}
		else // enum or string
		{
			if (frequencies_enum != null)
			{
				for (int i = 0; i < binLabelsRows.length; i++)
				{
					String text = frequencies_enum[0][i].label;
					binLabelsRows[i] = new Label(text);
				}
			}
		}
		
		// set the labels for the column variable
		binLabelsColumns = new Label[numberOfColumnBins];

		if (typeColumns != null)
		{
			if (typeColumns.isNumber())
			{
				for (int i = 0; i < binLabelsColumns.length; i++)
				{
					String text = this.model.getSplitOptions().getSplitClassLabel(i, this.model.getStatTableModel());

					binLabelsColumns[i] = new Label(text);
				}
			}
			else // enum or string
			{
				for (int splitClass = 0; splitClass < numberOfColumnBins; splitClass++)
				{
					String text = this.model.getSplitOptions().getSplitClassLabel(splitClass,
						this.model.getStatTableModel());
					binLabelsColumns[splitClass] = new Label(text);
				}
			}
		}
	}

	/**
	 * Set the data labels to be used in the crosstab table.
	 */
	private void setDataLabels()
	{
		if ((numberOfRowBins > 0) && (numberOfColumnBins > 0))
		{
			int x = numberOfColumnBins + 1; // + 1 for totals column
			int y = numberOfRowBins + 1; // + 1 for totals row
			data = new Label[x][y];
			
			if (percentageItemSelected())
			{ // percentages
				
				// calculate the divisors in order to convert the data to the percentage value
				int[][] divisors = new int[numberOfColumnBins + 1][numberOfRowBins + 1];
				
				if (percentage_endTotalSelected())
				{
					for (int i = 0; i < x; i++)
					{
						for (int j = 0; j < y; j++)
						{
							if (aantalTotaal != 0)
							{
								divisors[i][j] = aantalTotaal;
							}
							else
							{
								divisors[i][j] = 0;
							}
						}
					}
				}
				else if (percentage_rowTotalSelected())
				{
					for (int i = 0; i < x; i++)
					{
						for (int j = 0; j < y - 1; j++)
						{
							if (aantalPerRowBin[j] != 0)
							{
								divisors[i][j] = aantalPerRowBin[j];
							}
							else
							{
								divisors[i][j] = 0;
							}
						}
						
						// factor voor de laatste totaal-rij
						if (aantalTotaal != 0)
						{
							divisors[i][y-1] = aantalTotaal;
						}
						else
						{
							divisors[i][y-1] = 0;
						}
					}
				}
				else if (percentage_columnTotalSelected())
				{
					for (int i = 0; i < x - 1; i++)
					{
						for (int j = 0; j < y; j++)
						{
							if (aantalPerColumnBin[i] != 0)
							{
								divisors[i][j] = aantalPerColumnBin[i];
							}
							else
							{
								divisors[i][j] = 0;
							}
						}
					}
					
					// factor voor de laatste totaal-kolom
					for (int j = 0; j < y; j++)
					{
						if (aantalTotaal != 0)
						{
							divisors[x-1][j] = aantalTotaal;
						}
						else
						{
							divisors[x-1][j] = 0;
						}
					}
				}
				
				// set the percentage labels
				double waarde;
				String waardeString;
				if (typeRows.isNumber() && frequencies_number != null)
				{
					// for number variables use frequencies_number
					
					// loop over x het aantal splitbins
					for (int i_x = 0; i_x < x - 1; i_x++)
					{
						// loop over het aantal bins van de row variabele
						for (int i_y = 0; i_y < y - 1; i_y++)
						{
							waardeString = this.getWaardeString(frequencies_number[i_x][i_y * 2], divisors[i_x][i_y]);
							
							data[i_x][i_y] = new Label(waardeString + "%");
						}
						
						// set total for the end of the columns
						waardeString = this.getWaardeString(aantalPerColumnBin[i_x], divisors[i_x][y-1]);
						data[i_x][y-1] = new Label(waardeString + "%");
					}
					
					// set totals for the end of the rows
					for (int i_y = 0; i_y < y - 1; i_y++)
					{
						waardeString = this.getWaardeString(aantalPerRowBin[i_y], divisors[x-1][i_y]);
						data[x-1][i_y] = new Label(waardeString + "%");
					}		
					
					waardeString = this.getWaardeString(aantalTotaal, divisors[x-1][y-1]);
					data[x-1][y-1] = new Label(waardeString + "%");
				} // type number
				else if (!typeRows.isNumber() && frequencies_enum != null) // type enum or string
				{
					// for enum or string variables use frequencies_enum
					// loop over x het aantal splitbins
					for (int i_x = 0; i_x < x - 1; i_x++)
					{
						// check for the correct frequencies_enum; frequencies_enum kent de split in de eerste getriggerde update nog niet...
						if (frequencies_enum.length == numberOfColumnBins)
						{
							// loop over het aantal bins van de row variabele
							for (int i_y = 0; i_y < y - 1; i_y++)
							{
								waardeString = this.getWaardeString(frequencies_enum[i_x][i_y].frequency, divisors[i_x][i_y]);
								data[i_x][i_y] = new Label(waardeString + "%");
							}
							
							// set total for the end of the columns
							waardeString = this.getWaardeString(aantalPerColumnBin[i_x], divisors[i_x][y - 1]);
							data[i_x][y-1] = new Label(waardeString + "%");
						}
					}
					
					// set totals for the end of the rows
					for (int i_y = 0; i_y < y - 1; i_y++)
					{
						waardeString = this.getWaardeString(aantalPerRowBin[i_y], divisors[x-1][i_y]);
						data[x-1][i_y] = new Label(waardeString + "%");
					}		
					
					waardeString = this.getWaardeString(aantalTotaal, divisors[x-1][y-1]);
					data[x-1][y-1] = new Label(waardeString + "%");
				}
			} // percentage
			else
			{ // amounts
				if (typeRows.isNumber() && frequencies_number != null)
				{
					// for number variables use frequencies_number
					
					// loop over x het aantal splitbins
					for (int i_x = 0; i_x < x - 1; i_x++)
					{
						// loop over het aantal bins van de row variabele
						for (int i_y = 0; i_y < y - 1; i_y++)
						{
							data[i_x][i_y] = new Label("" + frequencies_number[i_x][i_y * 2]);
						}
						
						// set total for the end of the columns
						data[i_x][y-1] = new Label("" + aantalPerColumnBin[i_x]);
					}
					
					// set totals for the end of the rows
					for (int i_y = 0; i_y < y - 1; i_y++)
					{
						data[x-1][i_y] = new Label("" + aantalPerRowBin[i_y]);
					}		
					
					data[x-1][y-1] = new Label("" + aantalTotaal);
				} // type number
				else if (!typeRows.isNumber() && frequencies_enum != null) // type enum or string
				{
					// for enum or string variables use frequencies_enum
					// loop over x het aantal splitbins
					for (int i_x = 0; i_x < x - 1; i_x++)
					{
						// check of frequencies_enum de goede lengte heeft 
						// (frequencies_enum kent de split nog niet in de eerste getriggerde update)
						if (frequencies_enum.length == numberOfColumnBins)
						{
							// loop over het aantal bins van de row variabele
							for (int i_y = 0; i_y < y - 1; i_y++)
							{
								data[i_x][i_y] = new Label("" + frequencies_enum[i_x][i_y].frequency);
							}
							
							// set total for the end of the columns
							data[i_x][y-1] = new Label("" + aantalPerColumnBin[i_x]);
						}
					}
					
					// set totals for the end of the rows
					for (int i_y = 0; i_y < y - 1; i_y++)
					{
						data[x-1][i_y] = new Label("" + aantalPerRowBin[i_y]);
					}		
					
					data[x-1][y-1] = new Label("" + aantalTotaal);
				}
			} // amounts
		
			// add the listeners
			CellClickHandler clickHandler = new CellClickHandler();
			for (int i = 0; i < numberOfColumnBins; i++)
			{
				for (int j = 0; j < numberOfRowBins; j++)
				{
					if (data[i][j] != null)
					{
						data[i][j].addClickHandler(clickHandler);
					}
				}
			}

		} // there is data
		// else no data to set...
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
	 * Make the data part of the crosstab table view on panel.
	 */
	private void makeTable()
	{
		// The two header rows
		FlexTable flexTable = new FlexTable();
		flexTable.addStyleName(statistiekCss.flexTable());
		FlexCellFormatter cellFormatter = flexTable.getFlexCellFormatter();
		
		String columnsNameString;
		
		int columnsVarIndex = this.model.getSplitOptions().getColumnSplitIndex();
		if (columnsVarIndex < 0)
		{
			columnsNameString = "onbekende variabele";
		}
		else
		{
			columnsNameString = this.model.getStatTableModel()
				.getColumnName(columnsVarIndex);
		}
		
		// FIRST ROW with dummy labels, column variable name and "total" 
		Label variableColumnsName = new Label(columnsNameString);
		variableColumnsName.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		flexTable.setWidget(0, 2, variableColumnsName);
		cellFormatter.setColSpan(0, 2, numberOfColumnBins);
		
		Label totaalLabel = new Label(StatistiekGWT.rb.getString("totalLabel"));
		flexTable.setWidget(0, 3, totaalLabel);//numberOfColumnBins + 2, totaalLabel); // mind the colspan...
		
		// SECOND ROW with column bin labels
		for (int i = 0; i < binLabelsColumns.length; i++)
		{
			if (binLabelsColumns[i] != null)
			{
				binLabelsColumns[i].addStyleName(statistiekCss.noWrap());
				binLabelsColumns[i].getElement().getStyle().setTextAlign(TextAlign.CENTER);
				flexTable.setWidget(1, i + 2, binLabelsColumns[i]);
			}
		}
		
		// add empty label in last column
		Label empty = new Label("");
		flexTable.setWidget(1, binLabelsColumns.length + 2, empty);
		
		// the rest of the table
		// add the row variable name to the first column
		Label variableRowsName = new Label(this.model.getStatTableModel()
			.getColumnName(this.model.getColumnIndex()));
		flexTable.setWidget(2, 0, variableRowsName);
		cellFormatter.setRowSpan(2, 0, numberOfRowBins);
		
		// add the row bin labels
		for (int i = 0; i < binLabelsRows.length; i++)
		{
			if (binLabelsRows[i] != null)
			{
				binLabelsRows[i].addStyleName(statistiekCss.noWrap());
				if (i == 0)
				{
					flexTable.setWidget(i + 2, 1, binLabelsRows[i]);
				}
				else
				{
					// bug because of rowspan; subtract 1 of column index 
					flexTable.setWidget(i + 2, 0, binLabelsRows[i]);
				}
			}
		}

		// add the total label
		Label totalLabel = new Label(StatistiekGWT.rb.getString("totalLabel"));
		if (percentageItemSelected() && percentage_endTotalSelected())
		{
			totalLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		}
		flexTable.setWidget(crosstabRows - 1, 0, totalLabel);
		
		// add the DATA LABELS
		if (data != null)
		{
			for (int j = 0; j < numberOfRowBins; j++)
			{
				for (int i = 0; i < numberOfColumnBins; i++)
				{
					if (data[i][j] != null)
					{
						data[i][j].getElement().getStyle().setTextAlign(TextAlign.RIGHT);
						if (j == 0)
						{
							flexTable.setWidget(j + 2, i + 2, data[i][j]);
							cellFormatter.getElement(j + 2, i + 2).getStyle().setBackgroundColor(cellColors[i][j]);
						}
						else
						{
							// bug because of rowspan; subtract 1 of column index 
							flexTable.setWidget(j + 2, i + 1, data[i][j]);
							cellFormatter.getElement(j + 2, i + 1).getStyle().setBackgroundColor(cellColors[i][j]);
						}
					}
				}
				
				// last column with totals
				if (data[numberOfColumnBins][j] != null)
				{
					if (data[numberOfColumnBins][j] != null)
					{
						if (percentageItemSelected() && percentage_rowTotalSelected()
							&& data[numberOfColumnBins][j].getText().equals("100%"))
						{
							data[numberOfColumnBins][j].getElement().getStyle().setFontWeight(FontWeight.BOLD);
						}
						
						data[numberOfColumnBins][j].getElement().getStyle().setTextAlign(TextAlign.RIGHT);
						
						if (j == 0)
						{
							flexTable.setWidget(j + 2, numberOfColumnBins + 2, data[numberOfColumnBins][j]);
						}
						else
						{
							// bug because of rowspan; subtract 1 of column index 
							flexTable.setWidget(j + 2, numberOfColumnBins + 1, data[numberOfColumnBins][j]);
						}
					}
				}
			}
			
			// Last row with totals
			for (int i = 0; i < numberOfColumnBins; i++)
			{
				if (data[i][numberOfRowBins] != null)
				{
					if (percentageItemSelected() && percentage_columnTotalSelected()
						&& data[i][numberOfRowBins].getText().equals("100%"))
					{
						data[i][numberOfRowBins].getElement().getStyle().setFontWeight(FontWeight.BOLD);
					}
					
					data[i][numberOfRowBins].getElement().getStyle().setTextAlign(TextAlign.RIGHT);
					flexTable.setWidget(numberOfRowBins + 2, i + 2, data[i][numberOfRowBins]);
				}
			}
			
			// endtotal
			if (data[numberOfColumnBins][numberOfRowBins] != null)
			{
				if (percentageItemSelected() && percentage_endTotalSelected())
				{
					data[numberOfColumnBins][numberOfRowBins].getElement().getStyle().setFontWeight(FontWeight.BOLD);
				}
				
				data[numberOfColumnBins][numberOfRowBins].getElement().getStyle().setTextAlign(TextAlign.RIGHT);
				flexTable.setWidget(numberOfRowBins + 2, numberOfColumnBins + 2, data[numberOfColumnBins][numberOfRowBins]);
			}
		}
		
		this.mainPanel.add(flexTable);
	}

	private void setMainPanelSize()
	{
		this.scrollPanel.setPixelSize(this.getWidth(), this.getHeight());
		this.scrollPanel.setAlwaysShowScrollBars(false);
	}
	
	// Override setBound
	public void setBounds(int x, int y, int w, int h)
	{
		this.setMainPanelSize();
	}

	public void setModel(CrossTabulationTableModel model)
	{
		this.model = model;
		userOptionsPanel.setModel(model);
		this.update();
		this.userOptionsPanel.update();
	}

	public int getSplitBinsBoxSelectedInt()
	{
		return userOptionsPanel.getVarColumnsBoxSelectedIndex();
	}

	/*
	 * CellClickhandler is used on the labels in the crosstabulation table.
	 */
	private class CellClickHandler implements ClickHandler
	{

		@Override
		public void onClick(ClickEvent event)
		{
			for (int i = 0; i < numberOfColumnBins; i++)
			{
				for (int j = 0; j < numberOfRowBins; j++)
				{
					if (event.getSource().equals(data[i][j]))
					{
						cellClicked(i, j);
						break;
					}
				}
			}
		}

		/**
		 * The user clicked on cell (columnNumber, rowNumber) in the crosstabulation
		 * table.
		 * 
		 * @param columnNumber
		 * @param rowNumber
		 */
		private void cellClicked(int columnNumber, int rowNumber)
		{
			if (!CrossTabulationTableView.this.model.columnIndexValid())
			{
				return;
			}
			
			// update the selectionList

			ColumnType cType = CrossTabulationTableView.this.model.getStatTableModel()
				.getColumnTypes()
				.get(CrossTabulationTableView.this.model.getColumnIndex());

			if (typeRows.isNumber())
			{
				ArrayList<Boolean> selectionList = new ArrayList<Boolean>(
					CrossTabulationTableView.this.model.getStatTableModel().getRowCount());
				for (int i = 0; i < CrossTabulationTableView.this.model
					.getStatTableModel().getRowCount(); i++)
				{
					Object o = CrossTabulationTableView.this.model.getStatTableModel()
						.getValueAt(i,
							CrossTabulationTableView.this.model.getColumnIndex());

					selectionList.add(!o.equals(ColumnType.WILDCARD)
						&& CrossTabulationTableView.this.model.binOfNumber(Double
							.parseDouble((String) o)) == rowNumber
						&& CrossTabulationTableView.this.model.getStatTableModel()
							.classifyObject(i,
								CrossTabulationTableView.this.model.getSplitOptions()) == columnNumber);

				}

				CrossTabulationTableView.this.model.getStatTableModel().setSelectionList(
					selectionList);
			}
			else
			{
				String clicked;
				if (typeRows.equals(AllowedTypes.ENUM))
				{
					clicked = cType.getEnumOptions()[rowNumber];
					int wildcardIndex = Arrays.asList(cType.getEnumOptions())
						.indexOf(ColumnType.WILDCARD);
					if (wildcardIndex <= 0 && wildcardIndex < rowNumber)
					{
						clicked = cType.getEnumOptions()[rowNumber + 1];
					}
				}
				else
				{
					ArrayList<String> options = CrossTabulationTableView.this.model
						.getStatTableModel().stringColumnOptions(
							CrossTabulationTableView.this.model.getColumnIndex());

					clicked = options.get(rowNumber);
				}

				ArrayList<Boolean> selectionList = new ArrayList<Boolean>(
					CrossTabulationTableView.this.model.getStatTableModel().getRowCount());
				for (int i = 0; i < CrossTabulationTableView.this.model
					.getStatTableModel().getRowCount(); i++)
				{
					Object o = CrossTabulationTableView.this.model.getStatTableModel()
						.getValueAt(i,
							CrossTabulationTableView.this.model.getColumnIndex());

					selectionList
					.add(!o.equals(ColumnType.WILDCARD)
						&& ((String) o).equals(clicked)
						&& CrossTabulationTableView.this.model.getStatTableModel()
							.classifyObject(i,
								CrossTabulationTableView.this.model.getSplitOptions()) == columnNumber);
				}
				CrossTabulationTableView.this.model.getStatTableModel().setSelectionList(
					selectionList);
			}
			
			CrossTabulationTableView.this.update();
		}

	} // class CellClickHandler

	@Override
	public void onSelectionChange(SelectionChangeEvent event)
	{
		this.update();
	}

	@Override
	public void onTableChange(TableChangeEvent event)
	{
		GWT.log("CrossTabulationTableView.onTableChange()");

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
					this.recalculateRowsBinBoundaries();
					
					if (this.model.getSplitOptions().getColumnSplitIndex() > -1)
					{
						// er is een split
						
						// split bins opnieuw berekenen
						this.recalculateColumnsBinBoundaries();
					}
				}
				else if (event.getInfo().equals(TableChangeEvent.SET_VALUE_AT)
					|| event.getInfo().equals(TableChangeEvent.EDIT_COLUMN))
				{
					if (event.getColumnIndex() == this.model.getColumnIndex())
					{
						// bins opnieuw berekenen
						this.recalculateRowsBinBoundaries();
					}
					else if (event.getColumnIndex() == this.model.getSplitOptions().getColumnSplitIndex())
					{
						// split bins opnieuw berekenen
						this.recalculateColumnsBinBoundaries();
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
	 * Remove all of this view's handler occurrences.
	 */
	public void removeHandlers()
	{
		this.tableChangeEventHandlerRegistration.removeHandler();
		this.selectionChangeEventHandlerRegistration.removeHandler();
		this.outlierChangeEventHandlerRegistration.removeHandler();
	}

	/**
	 * Recalculate the bin boundaries for crosstab's columnIndex
	 * if possible.
	 * This is necessary to calculate the frequencies, in the whole data set and
	 * in the selection. There is always one bin.
	 * 
	 * @param typeHasChanged
	 * 		The type has changed yes/no.
	 */
	public void recalculateRowsBinBoundaries()
	{
		if (this.model.columnIndexValid())
		{
			ArrayList<ColumnType> list = this.model.getStatTableModel()
				.getColumnTypes();
			if (list.get(this.model.getColumnIndex()).getType().isNumber())
			{
				// binBoundaries worden hier standaard gezet
				ArrayList<Double> binBoundaries = StatistiekGWT
					.appropriateBoundaries(
						this.model.getStatTableModel().getColumnMin(
							this.model.getColumnIndex()),
						this.model.getStatTableModel().getColumnMax(
							this.model.getColumnIndex()), this.model
							.getNoBins());
				this.model.setBinBoundaries(binBoundaries);

				// opnieuw berekenen met de berekende binboundaries, omdat er
				// mogelijk minder bins nodig zijn
				binBoundaries = StatistiekGWT
					.appropriateBoundariesFromBinSettings(
						this.model.getStatTableModel().getColumnMin(
							this.model.getColumnIndex()),
						this.model.getStatTableModel().getColumnMax(
							this.model.getColumnIndex()), binBoundaries.get(1)
							- binBoundaries.get(0), binBoundaries.get(0));
				this.model.setBinBoundaries(binBoundaries);
			}
		}
	}
	
	/**
	 * Recalculate the column's bin boundaries for the column with columnSplitIndex
	 * if possible. In crosstab the column's variable is the split variable.
	 * 
	 * @param columnIndex
	 * 		The index of the column for which the bin
	 *      boundaries will be calculated.
	 * @param typeHasChanged
	 * 		The type has changed yes/no.
	 */
	public void recalculateColumnsBinBoundaries()
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
	public void onOutlierChange(OutlierChangeEvent event)
	{
		this.update();
	}

}
