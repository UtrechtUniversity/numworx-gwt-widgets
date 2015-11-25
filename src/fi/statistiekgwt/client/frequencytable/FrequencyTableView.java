package fi.statistiekgwt.client.frequencytable;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import fi.statistiekgwt.client.ColorPreviewer;
import fi.statistiekgwt.client.ColorUtils;
import fi.statistiekgwt.client.ColorUtils.RGBColor;
import fi.statistiekgwt.client.DialogButton;
import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;
import fi.statistiekgwt.client.StatistiekUtils;
import fi.statistiekgwt.client.StatistiekUtils.DummyTouchHandler;
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
 * MVC View for statistiekView FrequencyTable
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class FrequencyTableView extends LayoutPanel implements TableChangeEventHandler, SelectionChangeEventHandler, HasHandlers
{
	private FrequencyTableModel model;
	private FrequencyTableController controller;
	private FrequencyTableUserOptionsPanel userOptionsPanel;
	private DialogButton dialogButton;

	/**
	 * Op panel 'alles' staan mainpanel en dialogbuttonpanel.
	 */
	private FlowPanel alles;
	private FlowPanel mainPanel;
	private ScrollPanel scrollPanel;
	private HorizontalPanel dialogButtonPanel;
	private int numberOfSplitClasses = 1;

	/**
	 * The number of rows in the frequency table, including
	 * header and total row.
	 */
	private int frequencyTableRows;
	/**
	 * Row colors contains an array of colors in css color string format.
	 */
	private String[][] rowColors;
	/**
	 * Maximum number of rows for performance reasons.
	 */
	private int maxRows = 100;
	public static final int MAIN_GRID_HGAP = 8;
	public static final int GRID_TOPGAP = 5;
	public static final int GRID_BOTTOMGAP = 5;
	public static final int GRID_LEFTGAP = 5;
	public static final int GRID_RIGHTGAP = 5;
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
	public FrequencyTableView(FrequencyTableModel model,
		FrequencyTableController controller)
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
		
		// bind frequencytableview to stattablemodel: to handle table changes in stattablemodel
		this.tableChangeEventHandlerRegistration = this.model.getStatTableModel().addTableChangeEventHandler(this);

		// bind frequencytableview to stattablemodel: to handle selection changes in stattablemodel
		this.selectionChangeEventHandlerRegistration = this.model.getStatTableModel().addSelectionChangeEventHandler(this);
		
		this.mainPanel = new FlowPanel();
		
		this.scrollPanel = new ScrollPanel(this.mainPanel);
		this.scrollPanel.setAlwaysShowScrollBars(false);
		this.scrollPanel.addStyleName(statistiekCss.backgroundwhite());
		
		this.alles.add(this.scrollPanel);

		this.userOptionsPanel = new FrequencyTableUserOptionsPanel(this, controller, model);
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

		this.alles.add(this.dialogButtonPanel);//, StatistiekGWT.BUTTON_HEIGHT);
		if (!this.model.getStatTableModel().isViewsEditable())
		{
			//this.alles.setWidgetSize(dialogButtonPanel, 0);
			this.dialogButtonPanel.setHeight("0px");
		}

		this.alles.setPixelSize(this.getWidth(), this.getHeight() + StatistiekGWT.BUTTON_HEIGHT);
		
		// add alles to frequencytableview (layoutpanel)
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

//	/**
//	 * Class for a frequencytablepanel belonging to a split class,
//	 * that paints selected rows.
//	 * 
//	 * @author borku102
//	 *
//	 */
//	private class FrequencyTablePanel extends FlowPanel
//	{
//		private int splitClass;
//		
//		public FrequencyTablePanel(int splitClass)
//		{
//			super();
//			this.splitClass = splitClass;
//		}
//		
//		/**
//		 * Paint a white background and the selected rows in the frequency table panel.
//		 */
//		public void paintComponent(Graphics g)
//		{
//			if (!FrequencyTableView.this.model.columnIndexValid())
//			{
//				// variable is not valid, so there is nothing to paint
//				return;
//			}
//			else if (FrequencyTableView.this.frequencyTableRows > FrequencyTableView.this.maxRows)
//			{
//				return;
//			}
//
//			g.setColor(Color.BLACK);
//
//			g.setColor(Color.WHITE);
////			g.setColor(Color.PINK);
//			g.fillRect(0, 0, this.getWidth(), this.getHeight());
//			// test syl
////			g.fillRect(0, 0, this.getWidth(), this.getPreferredSize().height);
//
//			// draw selection color of rows
//			if (FrequencyTableView.this.rowColors != null)
//			{
//				for (int i = 0; i < FrequencyTableView.this.rowColors.length; i++)
//				{
//					g.setColor(FrequencyTableView.this.rowColors[i][this.splitClass]);
//					g.fillRect(
//						0,
//						(i + 1) * FrequencyTableView.this.rowHeight, 
//						FrequencyTableView.this.tableWidth, FrequencyTableView.this.rowHeight);
//				}
//			}
//		}
//	} // class FrequencyTablePanel
	
	public double getBinWidth()
	{
		return this.userOptionsPanel.getBinWidth();
	}

	public void setBinWidth(double d)
	{
		this.userOptionsPanel.setBinWidth(d);
	}

	public void setBinWidth()
	{
		this.userOptionsPanel.setBinWidth();
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

//	public JTextField getNoBinsField()
//	{
//		return this.noBinsField;
//	}
//
//	public String getNoBinsFieldText()
//	{
//		return this.noBinsField.getText();
//	}
//
	public boolean isShowPercBoxSelected()
	{
		return this.userOptionsPanel.isShowPercBoxSelected();
	}

	public boolean isShowCumulativeBoxSelected()
	{
		return this.userOptionsPanel.isShowCumulativeBoxSelected();
	}

	public int varBoxSelectedIndex()
	{
		return this.userOptionsPanel.getVarXBoxSelectedIndex();
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
		try
		{
			this.dialogButton.setVisible(this.model.getStatTableModel()
				.isViewsEditable());
			
			StatistiekUtils.removeAllWidgetsFromPanel(this.mainPanel);
			
			// initialize table height
//			this.tableHeight = 0;
//			this.isTableHeightSet = false;
			
			if (this.model.columnIndexValid())
			{
				ColumnType cType = this.model.getStatTableModel().getColumnTypes()
					.get(this.model.getColumnIndex());
				AllowedTypes type = cType.getType();
	
				FrequencyTuple[][] frequencyTuple = null;
				int[] frequencies = null;
				numberOfSplitClasses = this.model.getStatTableModel().numberOfSplitVarClasses(
					this.model.getSplitOptions());
				
				if (type.isNumber())
				{
					this.frequencyTableRows = this.model.getBinBoundaries().size() + 1;
					if (this.frequencyTableRows < 0)
					{
						this.frequencyTableRows = 0;
					}
					
					if (this.model.numberClassFrequency() != null)
					{
						this.rowColors = new String[this.model.numberClassFrequency()[0].length / 2][numberOfSplitClasses];
					}
				}
				else
				{
					frequencyTuple = this.model.enumClassFrequency();
					this.frequencyTableRows = 0;
					if (frequencyTuple != null)
					{
						this.frequencyTableRows = frequencyTuple[0].length + 2; // add 2 for header and total row
					}
					
					this.rowColors = new String[this.frequencyTableRows - 2][numberOfSplitClasses];
				}
				
				if (this.frequencyTableRows > this.maxRows)
				{
					String s = StatistiekGWT.rb.getString("messageNrRowsMoreThan") + this.maxRows
						+ ". " + StatistiekGWT.rb.getString("messageChooseOtherVar");
					Label message = new Label(s);
					this.mainPanel.add(message);
					userOptionsPanel.update();
					return;
				}
	
				for (int i = 0; i < numberOfSplitClasses; i++)
				{
//					splitClassPanels[i] = new FlowPanel();//FrequencyTablePanel(new GridBagLayout(), i);
					
					int sum = 0;
					if (type.isNumber())
					{
						if (this.model.numberClassFrequency() != null)
						{
							frequencies = this.model.numberClassFrequency()[i];
							sum = this.arrayEvenSum(frequencies);
						}
					}
					else
					{
						sum = this.tupleArraySum(frequencyTuple[i]);
	
					}
	
//	    			dimension = new Dimension(w, this.tableHeight);
//
//					splitClassPanels[i].setPreferredSize(dimension);
//					this.isTableHeightSet  = true;
					
					// set colors for background
					if (type.isNumber())
					{
						if (frequencies != null)
						{
							for (int j = 0; j < frequencies.length; j += 2)
							{
								double d = (frequencies[j] == 0 ? 0.0
									: (double) frequencies[j + 1] / (double) frequencies[j]);
								this.rowColors[j / 2][i] = ColorPreviewer.mixColorsToString(
									ColorUtils.WHITE_RGB, SELECTED_COLOR, d);
							}
						}
					}
					else
					{
						if (frequencyTuple != null)
						{
							for (int k = 0; k < frequencyTuple[i].length; k++)
							{
								FrequencyTuple ft = frequencyTuple[i][k];
								double d = (ft.frequency == 0 ? 0
									: (double) ft.selectionFrequency
										/ (double) ft.frequency);
								this.rowColors[k][i] = ColorPreviewer.mixColorsToString(
									ColorUtils.WHITE_RGB, SELECTED_COLOR, d);
							}
						}
					}
	
					if (frequencyTuple == null)
					{
						if (frequencies != null)
						{
							makeFrequencyTable(type, frequencies, null, sum, i);
						}
					}
					else
					{
						makeFrequencyTable(type, frequencies, frequencyTuple[i], sum, i);
					}
	
					//this.mainPanel.add(splitClassPanels[i]);
					
					if (numberOfSplitClasses > 1)
					{
						// add panel with splitclass label
						FlowPanel labelPanel = new FlowPanel();
						String splitVar = this.model.getStatTableModel()
							.getColumnName(this.model.getSplitOptions().getColumnSplitIndex());
						Label label = new Label(splitVar + ": " + this.model.getSplitOptions()
							.getSplitClassLabel(i, this.model.getStatTableModel()));
						label.addStyleName(statistiekCss.spaceBottomLabel());
						labelPanel.add(label);
						this.mainPanel.add(labelPanel);
					}
				}
			} //columnIndexValid()
			
			userOptionsPanel.update();
	
			// set mainPanel size op het eind zodat splitClassPanels bestaan
//			this.setMainPanelSize();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

//	/**
//	 * In order to determine the maximum width of column i, set the max width to the 
//	 * width of label if label width is larger than the value in maxColumnWidth.
//	 *  
//	 * @param i
//	 * @param label
//	 */
//	private void updateMaxColumnWidth(int i, Label label)
//	{
//		if (label.getOffsetWidth() > this.maxColumnWidth[i])
//		{
//			this.maxColumnWidth[i] = label.getOffsetWidth();
//		}
//	}

//	/**
//	 * Calculate the width of the frequency table as visible on the screen,
//	 * ignoring dummy labels.
//	 */
//	private void setTableWidth()
//	{
//		this.tableWidth = 0;
//		
//		if (this.maxColumnWidth != null)
//		{
//			for (int i = 0; i < this.maxColumnWidth.length; i++)
//			{
//				this.tableWidth += this.maxColumnWidth[i];
//			}
//		}
//
//		//System.out.println("FrequencyTableView.setTableWidth(): width = " + this.TABLE_WIDTH);
//	}

//	/**
//	 * Update the table height with the height of the given label.
//	 * @param label
//	 */
//	private void updateTableHeight(Label label)
//	{
//		if (!this.isTableHeightSet)
//		{
//			this.tableHeight += label.getOffsetHeight();
//		}
//	}

	/**
	 * Get the number of columns in the frequency table.
	 * @return
	 */
	private int getNumberOfColumns()
	{
		int n = 2;
		
		if (this.model.isShowPercentage())
		{
			n++;
		}
		if (this.model.isShowCumulative())
		{
			n++;
		}
		if (this.model.isShowPercentage() && this.model.isShowCumulative())
		{
			n = 5;
		}
		
		return n;
	}

	/**
	 * Make a flextable with the header, middle part and total row of the frequency table view 
	 * and add to mainpanel.
	 * 
	 * @param panel
	 * @param type
	 * @param frequencies
	 * @param frequencyTuple
	 * @param sum
	 * @param splitClass
	 */
	private void makeFrequencyTable(AllowedTypes type, int[] frequencies, 
		FrequencyTuple[] frequencyTuple, int sum, int splitClass)
	{
		// ********************* makeHeaderRow *********************
		// Make the header row
		FlexTable flexTable = new FlexTable();
		RowClickHandler rowClickHandler = new RowClickHandler(flexTable, splitClass);
		flexTable.addClickHandler(rowClickHandler);
		FlexCellFormatter cellFormatter = flexTable.getFlexCellFormatter();
	    HTMLTable.RowFormatter rowFormatter = flexTable.getRowFormatter();
	    flexTable.addStyleName(statistiekCss.flexTable());
	    
		Label variableName = new Label(this.model.getStatTableModel()
			.getColumnName(this.model.getColumnIndex()));
	    flexTable.setWidget(0, 0, variableName);

		Label freq = new Label("Freq.");
	    flexTable.setWidget(0, 1, freq);

		int percentage = 0;
		if (this.model.isShowPercentage())
		{
			Label freqPerc = new Label("Freq.%");
		    flexTable.setWidget(0, 2, freqPerc);
			percentage++;
		}
		if (this.model.isShowCumulative())
		{
			Label cumul = new Label("Cumul.");
		    flexTable.setWidget(0, 2 + percentage, cumul);
    	}
    	
    	if (this.model.isShowPercentage() && this.model.isShowCumulative())
    	{
			Label cumulPerc = new Label("Cumul.%");
		    flexTable.setWidget(0, 4, cumulPerc);
		}
		
		
		// ********************* makeMiddlePart ********************* 
		int cumulative = 0;
		int frequency;
		
		for (int bin = 0; bin < this.frequencyTableRows - 2; bin++)
		{
			// first column
			if (type.isNumber())
			{
				frequency = frequencies[bin * 2];
				Label label;
				
				ArrayList<Double> binBoundaries = this.model.getBinBoundaries();
				if (type.equals(AllowedTypes.INTEGER)
					&& (binBoundaries.get(1) - binBoundaries.get(0) == 1))
				{
					// bin width is 1
					label = new Label(binBoundaries.get(bin).toString());
				}
				else
				{
					label = new Label(
						StatistiekGWT.getStringValue(binBoundaries.get(bin))
						+ " -< "
						+ StatistiekGWT.getStringValue(binBoundaries.get(bin + 1)));
				}
				flexTable.setWidget(bin + 1, 0, label);
				
		    	// set selection color
		        rowFormatter.getElement(bin + 1).getStyle().setBackgroundColor(this.rowColors[bin][splitClass]);
			    
//				// administer the maximum column width
//				this.updateMaxColumnWidth(0, label);
//				// update table height with the height of the first label in row bin + 1
//				this.updateTableHeight(label);
			}
			else
			{
				frequency = frequencyTuple[bin].frequency;
				Label label = new Label(frequencyTuple[bin].label);
				flexTable.setWidget(bin + 1, 0, label);
				
		    	// set selection color
		        rowFormatter.getElement(bin + 1).getStyle().setBackgroundColor(this.rowColors[bin][splitClass]);
			    
//				// administer the maximum column width
//				this.updateMaxColumnWidth(0, label);
//				// update table height with the height of the first label in row bin + 1
//				this.updateTableHeight(label);
			}
			
			// second column
			Label freqLabel = new Label(Integer.toString(frequency));
			flexTable.setWidget(bin + 1, 1, freqLabel);
//			this.updateMaxColumnWidth(1, freqLabel);

			// set row height, to be used for row click calculations
//			this.rowHeight = freqLabel.getPreferredSize().height;
//			System.out.println("FrequencyTableView.makeMiddelPart(): freqLabel.getPreferredSize() = " 
//				+ freqLabel.getPreferredSize());

			percentage = 0;
			// third column
			if (this.model.isShowPercentage())
			{
				double d = frequency * 100 / (double) sum;
				d = Math.round(d * 100) / (double) 100;
				// round to one decimal
				int decimals = 1;
				d = StatistiekGWT.round(d, decimals);
				Label percLabel = new Label(StatistiekGWT.getStringValue(d) + "%");
				flexTable.setWidget(bin + 1, 2, percLabel);
//				this.updateMaxColumnWidth(2, percLabel);
				percentage++;
			}
			
			// fourth column
			if (this.model.isShowCumulative())
			{
				cumulative += frequency;
				Label cumulLabel = new Label(Integer.toString(cumulative));
				flexTable.setWidget(bin + 1, 2 + percentage, cumulLabel);
//				this.updateMaxColumnWidth(2 + percentage, cumulLabel);
	    	}
	    	
			// fifth column
	    	if (this.model.isShowPercentage() && this.model.isShowCumulative())
	    	{
				double d = (double) cumulative * 100 / (double) sum;
				d = Math.round(d * 100) / (double) 100;
				// round to one decimal
				int decimals = 1;
				d = StatistiekGWT.round(d, decimals);
				Label cumulPercLabel = new Label(StatistiekGWT.getStringValue(d) + "%");
				flexTable.setWidget(bin + 1, 4, cumulPercLabel);
//				this.updateMaxColumnWidth(4, cumulPercLabel);
			}
		}
		
		
		
		// ********************* makeTotalRow ********************* 
		Label totalLabel = new Label(StatistiekGWT.rb.getString("totalLabel"));
		flexTable.setWidget(this.frequencyTableRows - 1, 0, totalLabel);
//		// administer the maximum column width
//		this.updateMaxColumnWidth(0, totalLabel);
//		// update table height with the height of the first label in the last row
//		this.updateTableHeight(totalLabel);
		
		Label freqLabel = new Label(Integer.toString(sum));
		flexTable.setWidget(this.frequencyTableRows - 1, 1, freqLabel);
//		this.updateMaxColumnWidth(1, freqLabel);
    	
		percentage = 0;
    	if (this.model.isShowPercentage())
    	{
    		String percString;
    		if (sum == 0) // if there are no cases in the table, then total percentage is 0%
    		{
    			percString = "0%";
    		}
    		else
    		{
    			percString = "100%";
    		}
    		Label percLabel = new Label(percString);
    		flexTable.setWidget(this.frequencyTableRows - 1, 2, percLabel);
//    		this.updateMaxColumnWidth(2, percLabel);
    		percentage++;
    	}
    	
    	if (this.model.isShowCumulative())
    	{
    		Label cumulLabel = new Label(Integer.toString(sum));
    		flexTable.setWidget(this.frequencyTableRows - 1, 2 + percentage, cumulLabel);
//    		this.updateMaxColumnWidth(2 + percentage, cumulLabel);
    	}
    	
    	if (this.model.isShowPercentage() && this.model.isShowCumulative())
    	{
    		String percString;
    		if (sum == 0) // if there are no cases in the table, then total percentage is 0%
    		{
    			percString = "0%";
    		}
    		else
    		{
    			percString = "100%";
    		}
    		Label cumulPercLabel = new Label(percString);
    		flexTable.setWidget(this.frequencyTableRows - 1, 4, cumulPercLabel);
//    		this.updateMaxColumnWidth(4, cumulPercLabel);
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

	public void setModel(FrequencyTableModel model)
	{
		this.model = model;
		userOptionsPanel.setModel(model);
		this.update();
		this.userOptionsPanel.update();
	}

	public int getSplitVarBoxSelectedIndex()
	{
		return userOptionsPanel.getSplitVarBoxSelectedIndex();
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

	public int getSplitBinsBoxSelectedInt()
	{
		return userOptionsPanel.getSplitBinsBoxSelectedInt();
	}


	/*
	 * RowClickHandler is used on a flextable containing the frequency table of a split class.
	 */
	private class RowClickHandler implements ClickHandler
	{
		FlexTable flexTable;
		int splitClass;
		
		RowClickHandler(FlexTable flexTable, int splitClass)
		{
			super();
			this.flexTable = flexTable;
			this.splitClass = splitClass;
		}
		
        public void onClick(ClickEvent event) 
        {
            Cell src = this.flexTable.getCellForEvent(event);
            if (src != null)
            {
	            int rowIndex = src.getRowIndex() - 1;
	            
	            if ((rowIndex >= 0) && (rowIndex < FrequencyTableView.this.frequencyTableRows - 2))
	            {
		            this.rowClicked(rowIndex);
	            }
            }
        }
        
		/*
		 * The user clicked on row rowNumber. When there is a split, rowNumber has a maximum
		 * of numberOfBins * numberOfSplitClasses.
		 */
		private void rowClicked(int rowNumber)
		{
			if (!FrequencyTableView.this.model.columnIndexValid())
			{
				return;
			}

			int bins = FrequencyTableView.this.model.getStatTableModel()
				.numberOfSplitVarClasses(FrequencyTableView.this.model.getColumnIndex(),
					FrequencyTableView.this.model.getBinBoundaries());
			int row = rowNumber % bins;

			ColumnType cType = FrequencyTableView.this.model.getStatTableModel()
				.getColumnTypes()
				.get(FrequencyTableView.this.model.getColumnIndex());
			AllowedTypes type = cType.getType();
			if (type.isNumber())
			{
				ArrayList<Boolean> selectionList = new ArrayList<Boolean>(
					FrequencyTableView.this.model.getStatTableModel().getRowCount());
				for (int i = 0; i < FrequencyTableView.this.model
					.getStatTableModel().getRowCount(); i++)
				{
					Object o = FrequencyTableView.this.model.getStatTableModel()
						.getValueAt(i,
							FrequencyTableView.this.model.getColumnIndex());

					selectionList.add(!o.equals(ColumnType.WILDCARD)
						&& FrequencyTableView.this.model.binOfNumber(Double
							.parseDouble((String) o)) == row
						&& FrequencyTableView.this.model.getStatTableModel()
							.classifyObject(i,
								FrequencyTableView.this.model.getSplitOptions()) == splitClass);

				}

				FrequencyTableView.this.model.getStatTableModel().setSelectionList(
					selectionList);
			}
			else
			{
				String clicked;
				if (type.equals(AllowedTypes.ENUM))
				{
					clicked = cType.getEnumOptions()[row];
					int wildcardIndex = Arrays.asList(cType.getEnumOptions())
						.indexOf(ColumnType.WILDCARD);
					if (wildcardIndex <= 0 && wildcardIndex < row)
					{
						clicked = cType.getEnumOptions()[row + 1];
					}
				}
				else
				{
					// stringColumnOptions staan niet in alfabetische volgorde; neem enumClassFrequency
					FrequencyTuple[] freqTuple = FrequencyTableView.this.model.enumClassFrequency()[splitClass];
					clicked = freqTuple[row].label;
//					System.out.println("FrequencyTableView.RowClickListener.rowClicked("
//						+ rowNumber + ") in splitClass " + splitClass);
				}

				ArrayList<Boolean> selectionList = new ArrayList<Boolean>(
					FrequencyTableView.this.model.getStatTableModel().getRowCount());
				for (int i = 0; i < FrequencyTableView.this.model
					.getStatTableModel().getRowCount(); i++)
				{
					Object o = FrequencyTableView.this.model.getStatTableModel()
						.getValueAt(i,
							FrequencyTableView.this.model.getColumnIndex());

					selectionList
					.add(!o.equals(ColumnType.WILDCARD)
						&& ((String) o).equals(clicked)
						&& FrequencyTableView.this.model.getStatTableModel()
							.classifyObject(i,
								FrequencyTableView.this.model.getSplitOptions()) == splitClass);
				}
				FrequencyTableView.this.model.getStatTableModel().setSelectionList(
					selectionList);
			}
			
			update();
		}
	} // class RowClickHandler


	@Override
	public void onSelectionChange(SelectionChangeEvent event)
	{
		GWT.log("FrequencyTableView.onSelectionChange(): event.sender = " + event.getSenderName());
		if (!event.getSenderName().equals(this.controller.getViewName()))
		{
			this.update();
		}
	}
	
	@Override
	public void onTableChange(TableChangeEvent event)
	{
		GWT.log("FrequencyTableView.onTableChange()");

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
					// verwijderde waarde kan bins veranderen:
					// bins opnieuw berekenen
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
	 * Recalculate the bin boundaries for frequencytable's columnIndex
	 * if possible.
	 * 
	 * @param typeHasChanged
	 * 		The type has changed yes/no.
	 */
	public void recalculateBinBoundaries()
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

				this.model.setNoBins(binBoundaries.size() - 1);
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
	}
	
	/**
	 * @return the userOptionsPanel
	 */
	public FrequencyTableUserOptionsPanel getUserOptionsPanel()
	{
		return userOptionsPanel;
	}

	
//	/*
//	 * RowClickListener is used on mainPanel which may contain several splitClassPanels
//	 * in case of a split.
//	 */
//	private class RowClickListener implements MouseListener
//	{
//
//		public void mouseClicked(MouseEvent arg0)
//		{
//			if (FrequencyTableView.this.splitClassPanels[0] != null)
//			{
//				int x = arg0.getPoint().x;
//				int y = arg0.getPoint().y;
//				int y_transformed = y;
//				int heightSplitClassPanel = FrequencyTableView.this.splitClassPanels[0].getHeight();
//				int heightLabelPanel = 0;
//				if (FrequencyTableView.this.splitClassLabelPanel != null)
//					heightLabelPanel = FrequencyTableView.this.splitClassLabelPanel.getHeight();
//				// total height of a split class frequency table
//				int heightSplitFrequencyTable = heightSplitClassPanel + heightLabelPanel;
//				// number of data rows in a frequency table
//				int numberOfRows = FrequencyTableView.this.frequencyTableRows - 2;
//				
//				int count = 0;
//				// transform y
//				while (y_transformed > heightSplitFrequencyTable)
//				{
//					y_transformed = y_transformed - heightSplitFrequencyTable;
//					count++;
//	//				System.out.println("FrequencyTableView.RowClickListener.mouseClicked(): y_transformed = "
//	//					+ y_transformed + ", count = " + count);
//				}
//
//				int clicked = (y_transformed / FrequencyTableView.this.rowHeight) - 1;
//	
//	//			System.out.println("FrequencyTableView.RowClickListener.mouseClicked(): y = "
//	//				+ y + ", y_transformed = " + y_transformed + ", count = " + count
//	//				+ ", heightSplitClassPanel = " + heightSplitClassPanel
//	//				+ ", rowHeight = " + rowHeight
//	//				+ ", roundRowHeight = " + roundRowHeight
//	//				+ ", clicked = " + clicked);			
//	
//				if (clicked >= 0 && clicked < numberOfRows 
//					&& x <= FrequencyTableView.this.tableWidth)
//				{
//					int clicked_transformed = clicked + (count * numberOfRows);
//					this.rowClicked(clicked_transformed);
//				}
//			} // splitClassPanels != null
//		}
//
//
//		/*
//		 * The user clicked on row rowNumber. When there is a split, rowNumber has a maximum
//		 * of numberOfBins * numberOfSplitClasses.
//		 */
//		private void rowClicked(int rowNumber)
//		{
//			if (!FrequencyTableView.this.model.columnIndexValid())
//			{
//				return;
//			}
//
//			int bins = FrequencyTableView.this.model.getStatTableModel()
//				.splitVarClasses(FrequencyTableView.this.model.getColumnIndex(),
//					FrequencyTableView.this.model.getBinBoundaries());
//			int row = rowNumber % bins;
//			int splitClass = rowNumber / bins;
//
////			System.out.println("FrequencyTableView.RowClickListener.rowClicked(): rowNumber = " 
////				+ rowNumber + ", row " + row + " in splitClass " + splitClass);
//
//			ColumnType cType = FrequencyTableView.this.model.getStatTableModel()
//				.getColumnTypes()
//				.get(FrequencyTableView.this.model.getColumnIndex());
//			AllowedTypes type = cType.getType();
//			if (type.isNumber())
//			{
//				ArrayList<Boolean> selectionList = new ArrayList<Boolean>(
//					FrequencyTableView.this.model.getStatTableModel().getRowCount());
//				for (int i = 0; i < FrequencyTableView.this.model
//					.getStatTableModel().getRowCount(); i++)
//				{
//					Object o = FrequencyTableView.this.model.getStatTableModel()
//						.getValueAt(i,
//							FrequencyTableView.this.model.getColumnIndex());
//
//					selectionList.add(!o.equals(ColumnType.WILDCARD)
//						&& FrequencyTableView.this.model.binOfNumber(Double
//							.parseDouble((String) o)) == row
//						&& FrequencyTableView.this.model.getStatTableModel()
//							.classifyObject(i,
//								FrequencyTableView.this.model.getSplitOptions()) == splitClass);
//
//				}
//
//				FrequencyTableView.this.model.getStatTableModel().setSelectionList(
//					selectionList);
//			}
//			else
//			{
//				String clicked;
//				if (type.equals(AllowedTypes.ENUM))
//				{
//					clicked = cType.getEnumOptions()[row];
//					int wildcardIndex = Arrays.asList(cType.getEnumOptions())
//						.indexOf(ColumnType.WILDCARD);
//					if (wildcardIndex <= 0 && wildcardIndex < row)
//					{
//						clicked = cType.getEnumOptions()[row + 1];
//					}
//				}
//				else
//				{
//					// stringColumnOptions staan niet in alfabetische volgorde; neem enumClassFrequency
//					FrequencyTuple[] freqTuple = FrequencyTableView.this.model.enumClassFrequency()[splitClass];
//					clicked = freqTuple[row].label;
////					System.out.println("FrequencyTableView.RowClickListener.rowClicked("
////						+ rowNumber + ") in splitClass " + splitClass);
//				}
//
//				ArrayList<Boolean> selectionList = new ArrayList<Boolean>(
//					FrequencyTableView.this.model.getStatTableModel().getRowCount());
//				for (int i = 0; i < FrequencyTableView.this.model
//					.getStatTableModel().getRowCount(); i++)
//				{
//					Object o = FrequencyTableView.this.model.getStatTableModel()
//						.getValueAt(i,
//							FrequencyTableView.this.model.getColumnIndex());
//
//					selectionList
//					.add(!o.equals(ColumnType.WILDCARD)
//						&& ((String) o).equals(clicked)
//						&& FrequencyTableView.this.model.getStatTableModel()
//							.classifyObject(i,
//								FrequencyTableView.this.model.getSplitOptions()) == splitClass);
//				}
//				FrequencyTableView.this.model.getStatTableModel().setSelectionList(
//					selectionList);
//			}
//		}
//	} // class RowClickListener
}
