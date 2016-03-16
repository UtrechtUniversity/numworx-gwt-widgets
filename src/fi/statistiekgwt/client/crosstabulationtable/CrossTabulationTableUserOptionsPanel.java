package fi.statistiekgwt.client.crosstabulationtable;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;

import fi.statistiekgwt.client.DialogButton;
import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;
import fi.statistiekgwt.client.StatistiekUtils;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * User options panel for StatistiekView CrossTabulationTable
 * 
 * @author Sylvia van Borkulo
 * 
 */
public class CrossTabulationTableUserOptionsPanel extends FlowPanel
{

	private CrossTabulationTableView view;
	private CrossTabulationTableController controller;
	private CrossTabulationTableModel model;

	private DialogButton dialogButton;

	/**
	 * Panel 'basisPanel' wordt aan DialogButton meegegeven als content.
	 */
	private FlowPanel basisPanel;
	
	private CrossTabulationTableUOPClickHandler clickHandler;
	private CrossTabulationTableUOPBlurHandler blurHandler;
	private CrossTabulationTableUOPChangeHandler changeHandler;
	private CrossTabulationTableUOPValueChangeHandler valueChangeHandler;
	private CrossTabulationTableUOPKeyDownHandler keyDownHandler;

	// ROWS variable settings
	Label varRowsLabel;
	/**
	 * The box for choosing the variable for the rows in the crosstab
	 */
	private Label rowsLabel;
	private ListBox rowIndexBox;
	
	// rows bin settings
	private Label minBoundaryRowsLabel;
	private TextBox minBoundaryRowsField;
	private Label binWidthRowsLabel;
	private TextBox binWidthRowsField;
	private Label noObjectsRowsLabel;
	private Label minValueRowsLabel;
	private Label maxValueRowsLabel;
	
	// SWAP elements
	private Label swapLabel;
	/**
	 * Button to swap row and column variable
	 */
	private PushButton swapButton;

	// COLUMNS (split) variable settings
	Label varColumnsLabel;
	/**
	 * The box for choosing the variable for the columns in the crosstab
	 */
	private Label columnsLabel;
	private ListBox columnIndexBox;

	// columns bin settings
	private Label minBoundaryColumnsLabel;
	private TextBox minBoundaryColumnsField;
	private Label binWidthColumnsLabel;
	private TextBox binWidthColumnsField;
	private Label noObjectsColumnsLabel;
	private Label minValueColumnsLabel;
	private Label maxValueColumnsLabel;

	// display settings
	private Label absRelLabel;
	/**
	 * Indicates that amounts are shown in the crosstab table
	 */
	private RadioButton amountRadioItem;
	/**
	 * Indicates that percentages are shown in the crosstab table
	 */
	private RadioButton percentageRadioItem;
	/**
	 * Separates the percentage radio button and the 
	 * percentage settings.
	 */
	private HTML separatorPercentageSettings;
	/**
	 * Indicates that the end total adds up to 100%
	 */
	private RadioButton percentage_endTotal;
	/**
	 * Indicates that the row total adds up to 100%
	 */
	private RadioButton percentage_rowTotal;
	/**
	 * Indicates that the column total adds up to 100%
	 */
	private RadioButton percentage_columnTotal;

	private static final String SWAP_ICON_PATH = "../resources/reseticon.gif";

	private Button okButton;

	/**
	 * hr element, used to create a separator.
	 */
	private static final String hrString = new String("<hr  style=\"width:100%;\" />");

	/**
	 * The event bus to send change events to event handlers associated 
	 * with the views using StatTableModel.
	 */
	EventBus eventBus;

	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;

	public CrossTabulationTableUserOptionsPanel(CrossTabulationTableView view,
		CrossTabulationTableController controller, CrossTabulationTableModel model)
	{
		this.view = view;
		this.controller = controller;
		this.model = model;

		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		this.clickHandler = new CrossTabulationTableUOPClickHandler();
		this.blurHandler = new CrossTabulationTableUOPBlurHandler();
		this.changeHandler = new CrossTabulationTableUOPChangeHandler();
		this.valueChangeHandler = new CrossTabulationTableUOPValueChangeHandler();
		this.keyDownHandler = new CrossTabulationTableUOPKeyDownHandler();

		createGuiComponents();
		layoutGuiComponents();
		addHandlers();

		dialogButton = new DialogButton(
			StatistiekGWT.rb.settingsButton(), this.basisPanel);
		
		this.eventBus = StatistiekUtils.EVENT_BUS;
	}

	private void addHandlers()
	{
		// click handlers
		this.swapButton.addClickHandler(this.clickHandler);
		this.amountRadioItem.addClickHandler(this.clickHandler);
		this.percentageRadioItem.addClickHandler(this.clickHandler);
		this.percentage_endTotal.addClickHandler(this.clickHandler);
		this.percentage_rowTotal.addClickHandler(this.clickHandler);
		this.percentage_columnTotal.addClickHandler(this.clickHandler);
		this.okButton.addClickHandler(this.clickHandler);
		
		// blur handlers
		this.minBoundaryRowsField.addBlurHandler(this.blurHandler);
		this.binWidthRowsField.addBlurHandler(this.blurHandler);
		this.minBoundaryColumnsField.addBlurHandler(this.blurHandler);
		this.binWidthColumnsField.addBlurHandler(this.blurHandler);
		
		// key down handlers
		this.minBoundaryRowsField.addKeyDownHandler(this.keyDownHandler);
		this.binWidthRowsField.addKeyDownHandler(this.keyDownHandler);
		this.minBoundaryColumnsField.addKeyDownHandler(this.keyDownHandler);
		this.binWidthColumnsField.addKeyDownHandler(this.keyDownHandler);
		
		// change handlers
		this.rowIndexBox.addChangeHandler(this.changeHandler);
		this.columnIndexBox.addChangeHandler(this.changeHandler);
		
		// value change handlers
		this.minBoundaryRowsField.addValueChangeHandler(this.valueChangeHandler);
		this.binWidthRowsField.addValueChangeHandler(this.valueChangeHandler);
		this.minBoundaryColumnsField.addValueChangeHandler(this.valueChangeHandler);
		this.binWidthColumnsField.addValueChangeHandler(this.valueChangeHandler);
	}

	private void createGuiComponents()
	{
		this.basisPanel = new FlowPanel();

		// ROWS variable settings
		this.varRowsLabel = new Label(StatistiekGWT.rb.variableLabel());
		this.varRowsLabel.addStyleName(statistiekCss.titlelabel());
		this.rowsLabel = new Label(StatistiekGWT.rb.rowsLabel());
		this.rowIndexBox = new ListBox();
		this.rowIndexBox.setWidth("100px");

		// rows bin settings
		this.minBoundaryRowsLabel = new Label(StatistiekGWT.rb.startvalueLabel());
		this.minBoundaryRowsLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.minBoundaryRowsField = new TextBox();

		this.binWidthRowsLabel = new Label(StatistiekGWT.rb.classwidthLabel());
		this.binWidthRowsLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.binWidthRowsField = new TextBox();

		this.noObjectsRowsLabel = new Label("noObjectsLabel");
		this.noObjectsRowsLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.minValueRowsLabel = new Label("minValueLabel");

		this.maxValueRowsLabel = new Label("maxValueLabel");
		
		// SWAP elements
		this.swapLabel = new Label(StatistiekGWT.rb.swapLabel());
		this.swapLabel.addStyleName(statistiekCss.titlelabel());
		// button to swap row and column variable
		this.swapButton = new PushButton(new Image(statistiekGWTClientBundle.swapResource().getSafeUri()));
		this.swapButton.addStyleName(statistiekCss.pushbutton());

		// COLUMNS variable settings
		this.varColumnsLabel = new Label(StatistiekGWT.rb.variableLabel());
		this.varColumnsLabel.addStyleName(statistiekCss.titlelabel());
		this.columnsLabel = new Label(StatistiekGWT.rb.columnsLabel());
		this.columnIndexBox = new ListBox();

		// columns bin settings
		this.minBoundaryColumnsLabel = new Label(StatistiekGWT.rb.startvalueLabel());
		this.minBoundaryColumnsLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.minBoundaryColumnsField = new TextBox();

		this.binWidthColumnsLabel = new Label(StatistiekGWT.rb.classwidthLabel());
		this.binWidthColumnsLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.binWidthColumnsField = new TextBox();

		this.noObjectsColumnsLabel = new Label("noObjectsLabel");
		this.noObjectsColumnsLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.minValueColumnsLabel = new Label("minValueLabel");

		this.maxValueColumnsLabel = new Label("maxValueLabel");

		// display settings
		this.absRelLabel = new Label(StatistiekGWT.rb.absRelLabel());
		this.absRelLabel.addStyleName(statistiekCss.titlelabel());
		this.amountRadioItem = new RadioButton("percAmountGroup", 
			StatistiekGWT.rb.amountLabel());
		this.amountRadioItem.addStyleName(statistiekCss.radioButton());

		this.percentageRadioItem = new RadioButton("percAmountGroup",
			StatistiekGWT.rb.percentageRadio());
		this.percentageRadioItem.addStyleName(statistiekCss.radioButton());

		this.separatorPercentageSettings = new HTML(this.hrString);
		this.separatorPercentageSettings.addStyleName(statistiekCss.horizontalrule());

		this.percentage_endTotal = new RadioButton("percentageTotal",
			StatistiekGWT.rb.percentage_endTotal());
		this.percentage_endTotal.setValue(true);// by default true
		this.percentage_endTotal.addStyleName(statistiekCss.radioButton());

		this.percentage_rowTotal = new RadioButton("percentageTotal",
			StatistiekGWT.rb.percentage_rowTotal());
		this.percentage_rowTotal.addStyleName(statistiekCss.radioButton());

		this.percentage_columnTotal = new RadioButton("percentageTotal",
			StatistiekGWT.rb.percentage_columnTotal());
		this.percentage_columnTotal.addStyleName(statistiekCss.radioButton());

		// set tooltip
		this.swapButton.setTitle(StatistiekGWT.rb.swapTooltip());

		// ok-cancel
		this.okButton = new Button(StatistiekGWT.rb.oKButtonText());
	}

	private void layoutGuiComponents()
	{
		HorizontalPanel allSettingsPanel;
		FlowPanel variableRowsSettingsPanel, swapPanel, variableColumnsSettingsPanel, displaySettingsPanel;

		// ROWS variable settings
		variableRowsSettingsPanel = new FlowPanel();
		variableRowsSettingsPanel.setTitle(StatistiekGWT.rb.variableLabel()); // tooltip boven panel
		variableRowsSettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		variableRowsSettingsPanel.add(this.varRowsLabel); // the title
		variableRowsSettingsPanel.add(this.rowsLabel);
		variableRowsSettingsPanel.add(this.rowIndexBox);

		// Rows bins settings
		variableRowsSettingsPanel.add(this.minBoundaryRowsLabel);
		variableRowsSettingsPanel.add(this.minBoundaryRowsField);
		variableRowsSettingsPanel.add(this.binWidthRowsLabel);
		variableRowsSettingsPanel.add(this.binWidthRowsField);
		variableRowsSettingsPanel.add(this.noObjectsRowsLabel);
		variableRowsSettingsPanel.add(this.minValueRowsLabel);
		variableRowsSettingsPanel.add(this.maxValueRowsLabel);

		// Swap button
		swapPanel = new FlowPanel();
		swapPanel.setTitle(StatistiekGWT.rb.swapLabel());
		swapPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		swapPanel.add(this.swapLabel); // the title
		swapPanel.add(swapButton);

		// COLUMNS variable settings
		variableColumnsSettingsPanel = new FlowPanel();
		variableColumnsSettingsPanel.setTitle(StatistiekGWT.rb.variableLabel()); // tooltip boven panel
		variableColumnsSettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		variableColumnsSettingsPanel.add(this.varColumnsLabel); // the title
		variableColumnsSettingsPanel.add(columnsLabel);
		variableColumnsSettingsPanel.add(columnIndexBox);
		// Columns bins settings
		variableColumnsSettingsPanel.add(this.minBoundaryColumnsLabel);
		variableColumnsSettingsPanel.add(this.minBoundaryColumnsField);
		variableColumnsSettingsPanel.add(this.binWidthColumnsLabel);
		variableColumnsSettingsPanel.add(this.binWidthColumnsField);
		variableColumnsSettingsPanel.add(this.noObjectsColumnsLabel);
		variableColumnsSettingsPanel.add(this.minValueColumnsLabel);
		variableColumnsSettingsPanel.add(this.maxValueColumnsLabel);

		// Display
		displaySettingsPanel = new FlowPanel();
		displaySettingsPanel.setTitle(StatistiekGWT.rb.absRelLabel()); // tooltip boven panel
		displaySettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		displaySettingsPanel.add(this.absRelLabel); // the title
		displaySettingsPanel.add(amountRadioItem);
		displaySettingsPanel.add(percentageRadioItem);
		displaySettingsPanel.add(separatorPercentageSettings);
		displaySettingsPanel.add(percentage_endTotal);
		displaySettingsPanel.add(percentage_rowTotal);
		displaySettingsPanel.add(percentage_columnTotal);

		// Put settings panels together on allSettingsPanel
		allSettingsPanel = new HorizontalPanel();
		allSettingsPanel.setBorderWidth(2);
		allSettingsPanel.addStyleName(this.statistiekCss.horizontalPanel());
		allSettingsPanel.add(variableRowsSettingsPanel);
		allSettingsPanel.add(swapPanel);
		allSettingsPanel.add(variableColumnsSettingsPanel);
		allSettingsPanel.add(displaySettingsPanel);

		this.basisPanel.setHeight("100%");
		this.basisPanel.setWidth("100%");
		this.basisPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		this.basisPanel.add(allSettingsPanel);
		this.basisPanel.add(this.okButton);
	}

	public DialogButton getDialogButton()
	{
		return dialogButton;
	}

	public int getVarRowsBoxSelectedIndex()
	{
		return this.rowIndexBox.getSelectedIndex();
	}

	public int getVarColumnsBoxSelectedIndex()
	{
		return this.columnIndexBox.getSelectedIndex();
	}

	public void setModel(CrossTabulationTableModel model)
	{
		this.model = model;
	}
	
	public void updateRowIndexBox()
	{
		StatistiekUtils.removeAllItemsFromListBox(this.rowIndexBox);

		for (String varName : this.model.getStatTableModel().getColumnNames())
		{
			this.rowIndexBox.addItem(varName);
		}

		if (this.model.columnIndexValid())
		{
			this.rowIndexBox.setSelectedIndex(this.model.getColumnIndex());
		}
		else
		{
			// set no item selected
			this.rowIndexBox.setSelectedIndex(-1);
		}
	}
	
	public void updateColumnIndexBox()
	{
		StatistiekUtils.removeAllItemsFromListBox(this.columnIndexBox);

		for (String varName : this.model.getStatTableModel().getColumnNames())
		{
			this.columnIndexBox.addItem(varName);
		}
		
		if (this.model.getStatTableModel().isColumnIndexValid(this.model.getColumnSplitIndex()))
		{
			this.columnIndexBox.setSelectedIndex(this.model.getColumnSplitIndex());
		}
		else
		{
			// set no item selected
			this.columnIndexBox.setSelectedIndex(-1);
		}
	}
	
	public void updateRowBinSettings()
	{
		if (this.model.columnIndexValid())
		{
			ColumnType cType = this.model.getStatTableModel().getColumnTypes()
				.get(this.model.getColumnIndex());
			AllowedTypes type = cType.getType();
			if (type.equals(AllowedTypes.DOUBLE)
				|| type.equals(AllowedTypes.INTEGER))
			{
				this.minBoundaryRowsField.setText(
					StatistiekGWT.getStringValue(this.model.getBinBoundaries().get(0)));
				// set the row bin width based on the row's bin boundaries
				this.binWidthRowsField.setText(StatistiekGWT.getFormattedBinWidth(this.model.getBinBoundaries()));
				this.noObjectsRowsLabel.setText(StatistiekGWT.rb.numberLabel()
					+ this.model.getStatTableModel().getRowCount());
				String minValueRows = StatistiekGWT.getStringValue(
					this.model.getStatTableModel().getColumnMin(this.model.getColumnIndex()));
				this.minValueRowsLabel.setText(StatistiekGWT.rb.minLabel()
					+ minValueRows);
				String maxValueRows = StatistiekGWT.getStringValue(
					this.model.getStatTableModel().getColumnMax(this.model.getColumnIndex()));
				this.maxValueRowsLabel.setText(StatistiekGWT.rb.maxLabel()
					+ maxValueRows);
				setEnumClassesRows(false);
			}
			else if (type.equals(AllowedTypes.ENUM))
			{
				setEnumClassesRows(true);
			}
			else if (type.equals(AllowedTypes.STRING))
			{
				setEnumClassesRows(true);
			}
		}
	}
	
	public void updateColumnBinSettings()
	{
		if (this.model.getSplitOptions().getColumnSplitIndex() > -1)
		{
			AllowedTypes type = this.model.getStatTableModel().getColumnTypes().get(this.model.getSplitOptions().getColumnSplitIndex()).getType();
			
			if (type.equals(AllowedTypes.DOUBLE)
				|| type.equals(AllowedTypes.INTEGER))
			{
				this.minBoundaryColumnsField.setText(
					StatistiekGWT.getStringValue(this.model.getSplitOptions().getBinBoundaries().get(0)));
				// set the column bin width based on the column's bin boundaries
				this.binWidthColumnsField.setText(StatistiekGWT.getFormattedBinWidth(this.model.getSplitOptions().getBinBoundaries()));
				this.noObjectsColumnsLabel.setText(StatistiekGWT.rb.numberLabel()
					+ this.model.getStatTableModel().getRowCount());
				String minValueColumns = StatistiekGWT.getStringValue(
					this.model.getStatTableModel().getColumnMin(this.model.getSplitOptions().getColumnSplitIndex()));
				this.minValueColumnsLabel.setText(StatistiekGWT.rb.minLabel()
					+ minValueColumns);
				String maxValueColumns = StatistiekGWT.getStringValue(
					this.model.getStatTableModel().getColumnMax(this.model.getSplitOptions().getColumnSplitIndex()));
				this.maxValueColumnsLabel.setText(StatistiekGWT.rb.maxLabel()
					+ maxValueColumns);
				setEnumClassesColumns(false);
			}
			else if (type.equals(AllowedTypes.ENUM))
			{
				setEnumClassesColumns(true);
			}
			else if (type.equals(AllowedTypes.STRING))
			{
				setEnumClassesColumns(true);
			}
		}
	}

	public void update()
	{
		updateRowIndexBox();
		
		updateRowBinSettings();
		
		updateColumnIndexBox();

		updateColumnBinSettings();
		
		if (this.model.isShowPercentage())
		{
			this.percentageRadioItem.setValue(true);
			
			this.setPercentageOptionsVisible(true);
			
			this.percentage_endTotal.setValue(this.model.isShowPercentage_endTotal());
			this.percentage_rowTotal.setValue(this.model.isShowPercentage_rowTotal());
			this.percentage_columnTotal.setValue(this.model.isShowPercentage_columnTotal());
		}
		else
		{
			this.amountRadioItem.setValue(true);
			this.setPercentageOptionsVisible(false);
		}
	}

	public double getBinWidthRows()
	{
		String s = this.binWidthRowsField.getText();
		s = s.replace(',', '.');
		return Double.parseDouble(s);
	}

	public void setBinWidthRows(double d)
	{
		this.binWidthRowsField.setText(String.valueOf(d));
	}
	
	/**
	 *Set the bin width based on the model's bin boundaries. 
	 */
	public void setBinWidthRows()
	{
		this.binWidthRowsField.setText(StatistiekGWT.getFormattedBinWidth(this.model.getBinBoundaries()));
	}
	
	public double getMinBoundaryRows()
	{
		String s = this.minBoundaryRowsField.getText();
		s = s.replace(',', '.');
		return Double.parseDouble(s);
	}
	
	public void setMinBoundaryRows(double d)
	{
		this.minBoundaryRowsField.setText(String.valueOf(d));
	}

	public TextBox getBinWidthRowsField()
	{
		return binWidthRowsField;
	}

	public TextBox getMinBoundaryRowsField()
	{
		return minBoundaryRowsField;
	}

	public TextBox getBinWidthColumnsField()
	{
		return binWidthColumnsField;
	}

	public TextBox getMinBoundaryColumnsField()
	{
		return minBoundaryColumnsField;
	}

	public double getBinWidthColumns()
	{
		String s = this.binWidthColumnsField.getText();
		s = s.replace(',', '.');
		return Double.parseDouble(s);
	}

	public void setBinWidthColumns(double d)
	{
		this.binWidthColumnsField.setText(String.valueOf(d));
	}
	
	/**
	 *Set the column (split) bin width based on the model's column (split) bin boundaries. 
	 */
	public void setBinWidthColumns()
	{
		this.binWidthColumnsField.setText(
			StatistiekGWT.getFormattedBinWidth(this.model.getSplitOptions().getBinBoundaries()));
	}
	
	public double getMinBoundaryColumns()
	{
		String s = this.minBoundaryColumnsField.getText();
		s = s.replace(',', '.');
		return Double.parseDouble(s);
	}

	public void setMinBoundaryColumns(double d)
	{
		this.minBoundaryColumnsField.setText(String.valueOf(d));
	}

	private void setEnumClassesRows(boolean b)
	{
		minBoundaryRowsLabel.setVisible(!b);
		minBoundaryRowsField.setVisible(!b);
		binWidthRowsLabel.setVisible(!b);
		binWidthRowsField.setVisible(!b);
		noObjectsRowsLabel.setVisible(!b);
		minValueRowsLabel.setVisible(!b);
		maxValueRowsLabel.setVisible(!b);
	}

	private void setEnumClassesColumns(boolean b)
	{
		minBoundaryColumnsLabel.setVisible(!b);
		minBoundaryColumnsField.setVisible(!b);
		binWidthColumnsLabel.setVisible(!b);
		binWidthColumnsField.setVisible(!b);
		noObjectsColumnsLabel.setVisible(!b);
		minValueColumnsLabel.setVisible(!b);
		maxValueColumnsLabel.setVisible(!b);
	}

	private void setPercentageOptionsVisible(boolean b)
	{
		this.separatorPercentageSettings.setVisible(b);
		this.percentage_endTotal.setVisible(b);
		this.percentage_rowTotal.setVisible(b);
		this.percentage_columnTotal.setVisible(b);
	}

	public boolean percentageItemSelected()
	{
		return this.percentageRadioItem.getValue();
	}

	public boolean percentage_endTotalSelected()
	{
		return this.percentage_endTotal.getValue();
	}
	
	public boolean percentage_rowTotalSelected()
	{
		return this.percentage_rowTotal.getValue();
	}
	
	public boolean percentage_columnTotalSelected()
	{
		return this.percentage_columnTotal.getValue();
	}

	
	/**
	 * A clickhandler for CrossTabulationTableUserOptionsPanel
	 */
	class CrossTabulationTableUOPClickHandler implements ClickHandler
	{

		@Override
		public void onClick(ClickEvent e)
		{
			CrossTabulationTableModel model = CrossTabulationTableUserOptionsPanel.this.model;
			CrossTabulationTableView view = CrossTabulationTableUserOptionsPanel.this.view;
			CrossTabulationTableController controller = CrossTabulationTableUserOptionsPanel.this.controller;
			
			if (e.getSource() == rowIndexBox) // moet dit? Zit al in ChangeHandler
			{
				model.setColumnIndex(view.varRowsBoxSelectedIndex());
			}
			else if (e.getSource() == minBoundaryRowsField) // moet dit? Zit al in valueChangeHandler
			{
				controller.updateBoundariesFromRowsBinSettings();
			}
			else if (e.getSource() == binWidthRowsField) // moet dit? Zit al in valueChangeHandler
			{
				controller.updateBoundariesFromRowsBinSettings();
			}
			else if (e.getSource() == swapButton)
			{
				model.swapVariables();
			}
			else if (e.getSource() == columnIndexBox) // moet dit? Zit al in ChangeHandler
			{
				controller.setSplit(view.varColumnsBoxSelectedIndex());
			}
			else if (e.getSource() == minBoundaryColumnsField) // moet dit? Zit al in valueChangeHandler
			{
				controller.updateBoundariesFromColumnsBinSettings();
			}
			else if (e.getSource() == binWidthColumnsField) // moet dit? Zit al in valueChangeHandler
			{
				controller.updateBoundariesFromColumnsBinSettings();
			}
			else if ((e.getSource() == amountRadioItem) || (e.getSource() == percentageRadioItem))
			{
				setPercentageOptionsVisible(view.percentageItemSelected());
				if (!view.percentage_endTotalSelected()
					&& !view.percentage_rowTotalSelected()
					&& !view.percentage_columnTotalSelected())
				{
					// set the default end totals add up to 100%
					model.setShowPercentage_endTotal(true);
					percentage_endTotal.setValue(true);
				}			
				model.setShowPercentage(view.percentageItemSelected());
			}
			else if (e.getSource() == percentage_endTotal)
			{
				boolean b = percentage_endTotal.getValue();
				
				// Only update if there is a real change
				if (b != model.isShowPercentage_endTotal())
				{
					if (b)
					{
						// zet de andere 2 opties op false
						model.setShowPercentage_rowTotal(false);
						model.setShowPercentage_columnTotal(false);
					}

					model.setShowPercentage_endTotal(b);
				}
			}
			else if (e.getSource() == percentage_rowTotal)
			{
				boolean b = percentage_rowTotal.getValue();
				
				// Only update if there is a real change
				if (b != model.isShowPercentage_rowTotal())
				{
					if (b)
					{
						// zet de andere 2 opties op false
						model.setShowPercentage_endTotal(false);
						model.setShowPercentage_columnTotal(false);
					}

					model.setShowPercentage_rowTotal(b);
				}
			}
			else if (e.getSource() == percentage_columnTotal)
			{
				boolean b = percentage_columnTotal.getValue();
				
				// Only update if there is a real change
				if (b != model.isShowPercentage_columnTotal())
				{
					if (b)
					{
						// zet de andere 2 opties op false
						model.setShowPercentage_endTotal(false);
						model.setShowPercentage_rowTotal(false);
					}

					model.setShowPercentage_columnTotal(b);
				}
			}
			else if (e.getSource() == okButton)
			{
				dialogButton.closeDialog();
			}
			else
			{
				//System.out.println("CrossTabulationTableUserOptionsPanel.CrossTabulationTableUOPClickHandler.actionPerformed(): Unknown action source! " + e);
			}

			this.update();
		}
		
		/**
		 * Update the user options panel.
		 */
		private void updateUserOptionsPanel()
		{
			// update the user options panel
			CrossTabulationTableUserOptionsPanel.this.update();
		}

		/**
		 * Update the view and the user options panel.
		 */
		private void update()
		{
			// update view and user options panel
			CrossTabulationTableUserOptionsPanel.this.view.update();
		}
	} // class CrossTabulationTableUOPClickHandler

	/**
	 * A blurhandler for CrossTabulationTableUserOptionsPanel
	 */
	class CrossTabulationTableUOPBlurHandler implements BlurHandler
	{
		@Override
		public void onBlur(BlurEvent e)
		{
			CrossTabulationTableController controller = CrossTabulationTableUserOptionsPanel.this.controller;

			if (e.getSource() == minBoundaryRowsField)
			{
				controller.updateBoundariesFromRowsBinSettings();
			}
			else if (e.getSource() == binWidthRowsField)
			{
				controller.updateBoundariesFromRowsBinSettings();
			}
			else if (e.getSource() == minBoundaryColumnsField)
			{
				controller.updateBoundariesFromColumnsBinSettings();
			}
			else if (e.getSource() == binWidthColumnsField)
			{
				controller.updateBoundariesFromColumnsBinSettings();
			}

			// update view
			CrossTabulationTableUserOptionsPanel.this.view.update();
		}
	} // class CrossTabulationTableUOPBlurHandler
	
	class CrossTabulationTableUOPChangeHandler implements ChangeHandler
	{
		@Override
		public void onChange(ChangeEvent e)
		{
			if (e.getSource() == rowIndexBox)
			{
				model.setColumnIndex(view.varRowsBoxSelectedIndex());
			}
			else if (e.getSource() == columnIndexBox)
			{
				controller.setSplit(view.varColumnsBoxSelectedIndex());
			}

			// update view (and uop)
			view.update();
		}
	} // class CrossTabulationTableUOPChangeHandler

	class CrossTabulationTableUOPValueChangeHandler implements ValueChangeHandler<String>
	{
		@Override
		public void onValueChange(ValueChangeEvent<String> e)
		{
			if (e.getSource() == minBoundaryRowsField)
			{
				controller.updateBoundariesFromRowsBinSettings();
			}
			else if (e.getSource() == binWidthRowsField)
			{
				controller.updateBoundariesFromRowsBinSettings();
			}
			else if (e.getSource() == minBoundaryColumnsField)
			{
				controller.updateBoundariesFromColumnsBinSettings();
			}
			else if (e.getSource() == binWidthColumnsField)
			{
				controller.updateBoundariesFromColumnsBinSettings();
			}

			// update view
			CrossTabulationTableUserOptionsPanel.this.view.update();
		}
	} // class CrossTabulationTableUOPValueChangeHandler

	class CrossTabulationTableUOPKeyDownHandler implements KeyDownHandler
	{
		@Override
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				CrossTabulationTableController controller = CrossTabulationTableUserOptionsPanel.this.controller;

				if (e.getSource() == minBoundaryRowsField)
				{
					controller.updateBoundariesFromRowsBinSettings();
				}
				else if (e.getSource() == binWidthRowsField)
				{
					controller.updateBoundariesFromRowsBinSettings();
				}
				else if (e.getSource() == minBoundaryColumnsField)
				{
					controller.updateBoundariesFromColumnsBinSettings();
				}
				else if (e.getSource() == binWidthColumnsField)
				{
					controller.updateBoundariesFromColumnsBinSettings();
				}

				// update view
				CrossTabulationTableUserOptionsPanel.this.view.update();
			}
		}
	} // class CrossTabulationTableUOPKeyDownHandler

}
