package fi.statistiekgwt.client.histogram;

import java.util.ArrayList;

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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

import fi.statistiekgwt.client.DialogButton;
import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;
import fi.statistiekgwt.client.StatistiekUtils;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * @author Sylvia van Borkulo
 *
 */
public class HistogramUserOptionsPanel extends FlowPanel
{

	private HistogramView view;
	private HistogramController controller;
	private HistogramModel model;

	private DialogButton dialogButton;

	/**
	 * Panel 'basisPanel' wordt aan DialogButton meegegeven als content.
	 */
	private FlowPanel basisPanel;
	
	private HistogramUOPClickHandler clickHandler;
	private HistogramUOPBlurHandler blurHandler;
	private HistogramUOPChangeHandler changeHandler;
	private HistogramUOPValueChangeHandler valueChangeHandler;
	private HistogramUOPKeyDownHandler keyDownHandler;

	// variable settings
	private Label varLabel;
	private ListBox columnIndexBox;
	private Label axisLabel;
	private ListBox axisBox;

	// bin settings
	private CheckBox optimizeScaleBox;
	private Label binSettingsLabel;
	private Label binsLabel;
	/**
	 * Box for choosing the number of bins.
	 */
	private ListBox binsBox;
	/**
	 * Separator between bin boundaries settings and number of bins settings. 
	 */
	private HTML separatorBinSettings;
	private Label minBoundaryLabel;
	private TextBox minBoundaryField;
	private Label binWidthLabel;
	private TextBox binWidthField;
	private Label maxOnScaleLabel;
	/**
	 * Max boundary input field. To be used when the
	 * scale is not automatically optimized.
	 */
	private TextBox maxOnScaleField;
	/**
	 * Dit label bevat "Aantal: " en het aantal
	 */
	private Label noObjectsLabel;
	/**
	 * Dit label bevat "Minimum: " en het minimum
	 */
	private Label minValueLabel;
	/**
	 * Dit label bevat "Maximum: " en het maximum
	 */
	private Label maxValueLabel;

	// display settings
	private Label absRelLabel;
	private RadioButton amountRadioItem;
	private RadioButton percentageRadioItem;
	private CheckBox cumulativeBox;
	/**
	 * Separator between amount/percentage settings and bin label positioning settings.
	 */
	private HTML amountLabelHR;
	private RadioButton labelUnderBinRadioItem; // labels midden onder staven
	private RadioButton labelBetweenBinsRadioItem; // labels tussen staven
	/**
	 * Separator between bin label positioning settings and split view setting
	 */
	private HTML labelSplitHR;
	private RadioButton nextToEachOtherRadioItem;
	private RadioButton aboveEachOtherRadioItem;
	private RadioButton separateRadioItem;
	private RadioButton singleViewRadioItem;
	/**
	 * Separator between split view setting and split percentage
	 */
	private HTML splitViewPercentageHR;
	/**
	 * Indicates that the percentage in the split is
	 * relative to the end total.
	 */
	private RadioButton percentage_endTotal;
	/**
	 * Indicates that the percentage in the split is
	 * relative to the split total.
	 */
	private RadioButton percentage_splitTotal;

	// split settings
	private Label splitSettingsLabel;
	private Button splitButton;
	private Label splitVarLabel;
	private ListBox splitVarBox;
	private Label splitBinsLabel;
	/**
	 * Box for choosing the number of split bins.
	 */
	private ListBox splitBinsBox;
	private Button splitChooseBoundariesButton;
	/**
	 * Separator between number of split bins settings and split bin boundaries settings
	 */
	private HTML separatorSplitBoundaries;
	private Label splitMinBoundaryLabel;
	private TextBox splitMinBoundaryField;
	private Label splitBinWidthLabel;
	private TextBox splitBinWidthField;
	private Label splitBoundariesLabel;
	private TextArea splitBoundariesArea;
	private Label splitNoObjectsLabel;
	private Label splitMinValueLabel;
	private Label splitMaxValueLabel;

	private CheckBox stackModeBox;

	private Button okButton;

	private boolean splitBoundariesVisible;
	private boolean splitOptionsVisible;
	private boolean enumClasses;
	private boolean splitEnumClasses;

	/**
	 * The event bus to send change events to event handlers associated 
	 * with the views using StatTableModel.
	 */
	EventBus eventBus;

	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;

	public static final int DEFAULT_WIDTH = 800;
	public static final int DEFAULT_HEIGHT = 600;

	/**
	 * hr element, used to create a separator.
	 */
	private static final String hrString = new String("<hr  style=\"width:100%;\" />");


	public HistogramUserOptionsPanel(HistogramView view,
		HistogramController controller, HistogramModel model)
	{
		this.view = view;
		this.controller = controller;
		this.model = model;

		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

//		this.touchHandler = new HistogramUOPTouchHandler();
		this.clickHandler = new HistogramUOPClickHandler();
		this.blurHandler = new HistogramUOPBlurHandler();
		this.changeHandler = new HistogramUOPChangeHandler();
		this.valueChangeHandler = new HistogramUOPValueChangeHandler();
		this.keyDownHandler = new HistogramUOPKeyDownHandler();

		createGuiComponents(); // including this.basisPanel
		layoutGuiComponents();
		addHandlers();

		dialogButton = new DialogButton(
			StatistiekGWT.rb.getString("settingsButton"), this.basisPanel);
		
		this.eventBus = StatistiekUtils.EVENT_BUS;//new SimpleEventBus();
	}

	/**
	 * Add the handlers to the user options panel GUI components.
	 */
	private void addHandlers()
	{
		// click handlers
		this.amountRadioItem.addClickHandler(this.clickHandler);
		this.percentageRadioItem.addClickHandler(this.clickHandler);
		this.percentage_splitTotal.addClickHandler(this.clickHandler);
		this.percentage_endTotal.addClickHandler(this.clickHandler);
		this.cumulativeBox.addClickHandler(this.clickHandler);
		this.labelBetweenBinsRadioItem.addClickHandler(this.clickHandler);
		this.labelUnderBinRadioItem.addClickHandler(this.clickHandler);
		this.nextToEachOtherRadioItem.addClickHandler(this.clickHandler);
		this.aboveEachOtherRadioItem.addClickHandler(this.clickHandler);
		this.separateRadioItem.addClickHandler(this.clickHandler);
		this.singleViewRadioItem.addClickHandler(this.clickHandler);
		this.splitButton.addClickHandler(this.clickHandler);
		this.splitChooseBoundariesButton.addClickHandler(this.clickHandler);
		this.stackModeBox.addClickHandler(this.clickHandler);
		this.optimizeScaleBox.addClickHandler(this.clickHandler);
		this.okButton.addClickHandler(this.clickHandler);
		
		// blur handlers
		this.minBoundaryField.addBlurHandler(this.blurHandler);
		this.binWidthField.addBlurHandler(this.blurHandler);
		this.maxOnScaleField.addBlurHandler(this.blurHandler);
		this.splitMinBoundaryField.addBlurHandler(this.blurHandler);
		this.splitBinWidthField.addBlurHandler(this.blurHandler);
		
		// key down handlers
		this.minBoundaryField.addKeyDownHandler(this.keyDownHandler);
		this.binWidthField.addKeyDownHandler(this.keyDownHandler);
		this.maxOnScaleField.addKeyDownHandler(this.keyDownHandler);
		this.splitMinBoundaryField.addKeyDownHandler(this.keyDownHandler);
		this.splitBinWidthField.addKeyDownHandler(this.keyDownHandler);
		
		// change handlers
		this.columnIndexBox.addChangeHandler(this.changeHandler);
		this.axisBox.addChangeHandler(this.changeHandler);
		this.binsBox.addChangeHandler(this.changeHandler);
		this.splitVarBox.addChangeHandler(this.changeHandler);
		this.splitBinsBox.addChangeHandler(this.changeHandler);
		
		// value change handlers
		this.minBoundaryField.addValueChangeHandler(this.valueChangeHandler);
		this.binWidthField.addValueChangeHandler(this.valueChangeHandler);
		this.maxOnScaleField.addValueChangeHandler(this.valueChangeHandler);
		this.splitMinBoundaryField.addValueChangeHandler(this.valueChangeHandler);
		this.splitBinWidthField.addValueChangeHandler(this.valueChangeHandler);
	}

	private void createGuiComponents()
	{
		this.basisPanel = new FlowPanel();

		// VARIABLE SETTINGS
		this.varLabel = new Label(StatistiekGWT.rb.getString("variableLabel"));
		this.varLabel.addStyleName(statistiekCss.titlelabel());

		this.columnIndexBox = new ListBox();
		this.columnIndexBox.setWidth("100px");

		this.axisLabel = new Label(StatistiekGWT.rb.getString("axisLabel"));
		this.axisLabel.addStyleName(statistiekCss.spaceTopLabel());

		String[] options2 = new String[2];
		options2[0] = "X";
		options2[1] = "Y";
		this.axisBox = new ListBox();
		for (int i = 0; i < options2.length; i++)
		{
			this.axisBox.addItem(options2[i]);
		}
		this.axisBox.setPixelSize(100, 25);

		// BIN SETTINGS
		this.optimizeScaleBox = new CheckBox(
			StatistiekGWT.rb.getString("optimizeScaleBox"), true);
		this.binSettingsLabel = new Label(StatistiekGWT.rb.getString("classDivisionLabel"));
		this.binSettingsLabel.addStyleName(statistiekCss.titlelabel());
		this.binsLabel = new Label(StatistiekGWT.rb.getString("noClassesLabel"));

		Integer[] options1 = new Integer[50];
		for (int i = 0; i < 50; i++)
		{
			options1[i] = i + 1;
		}
		this.binsBox = new ListBox();
		for (int i = 0; i < options1.length; i++)
		{
			this.binsBox.addItem(options1[i].toString());
		}
		
		separatorBinSettings = new HTML(HistogramUserOptionsPanel.hrString);
		separatorBinSettings.addStyleName(statistiekCss.horizontalrule());

		this.minBoundaryLabel = new Label(
			StatistiekGWT.rb.getString("startvalueLabel"));

		this.minBoundaryField = new TextBox();

		this.maxOnScaleLabel = new Label(
			StatistiekGWT.rb.getString("maxValueLabel"));
		this.maxOnScaleField = new TextBox();
		
		this.binWidthLabel = new Label(
			StatistiekGWT.rb.getString("classwidthLabel"));
		this.binWidthLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.binWidthField = new TextBox();

		this.noObjectsLabel = new Label("");
		this.noObjectsLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.minValueLabel = new Label("");

		this.maxValueLabel = new Label("");

		// DISPLAY SETTINGS
		this.absRelLabel = new Label(StatistiekGWT.rb.getString("absRelLabel"));
		this.absRelLabel.addStyleName(statistiekCss.titlelabel());

		this.amountRadioItem = new RadioButton("percAmountGroup",
			StatistiekGWT.rb.getString("amountLabel"));
		this.amountRadioItem.addStyleName(statistiekCss.radioButton());

		this.percentageRadioItem = new RadioButton("percAmountGroup",
			StatistiekGWT.rb.getString("percentageRadio"));
		this.percentageRadioItem.addStyleName(statistiekCss.radioButton());
		this.percentageRadioItem.addStyleName(statistiekCss.spaceBottomLabel());

		this.cumulativeBox = new CheckBox(
			StatistiekGWT.rb.getString("cumulativeCheckbox"), false);

		amountLabelHR = new HTML(HistogramUserOptionsPanel.hrString);
		amountLabelHR.addStyleName(statistiekCss.horizontalrule());

		// radiobuttons for position labels
		this.labelBetweenBinsRadioItem = new RadioButton("labelPositionGroup",
			StatistiekGWT.rb.getString("labelBetweenBinsRadio"));
		this.labelBetweenBinsRadioItem.addStyleName(statistiekCss.radioButton());

		this.labelUnderBinRadioItem = new RadioButton("labelPositionGroup",
			StatistiekGWT.rb.getString("labelUnderBinRadio"));
		this.labelUnderBinRadioItem.addStyleName(statistiekCss.radioButton());

		labelSplitHR = new HTML(HistogramUserOptionsPanel.hrString);
		labelSplitHR.addStyleName(statistiekCss.horizontalrule());

		this.nextToEachOtherRadioItem = new RadioButton("splitViewGroup",
			StatistiekGWT.rb.getString("nextToEachOtherRadioItem"));
		this.nextToEachOtherRadioItem.addStyleName(statistiekCss.radioButton());

		this.aboveEachOtherRadioItem = new RadioButton("splitViewGroup",
			StatistiekGWT.rb.getString("aboveEachOtherRadioItem"));
		this.aboveEachOtherRadioItem.addStyleName(statistiekCss.radioButton());

		this.separateRadioItem = new RadioButton("splitViewGroup",
			StatistiekGWT.rb.getString("separateFromEachOtherRadioItem"));
		this.separateRadioItem.addStyleName(statistiekCss.radioButton());

		this.singleViewRadioItem = new RadioButton("splitViewGroup",
			StatistiekGWT.rb.getString("splitsingleviewCheckBox"));
		this.singleViewRadioItem.addStyleName(statistiekCss.radioButton());
		
		splitViewPercentageHR = new HTML(HistogramUserOptionsPanel.hrString);
		splitViewPercentageHR.addStyleName(statistiekCss.horizontalrule());
		
		this.percentage_splitTotal = new RadioButton("percentageSplitTotalGroup",
			StatistiekGWT.rb.getString("percentage_splitTotal"));
		this.percentage_splitTotal.addStyleName(statistiekCss.radioButton());
		
		this.percentage_endTotal = new RadioButton("percentageSplitTotalGroup",
			StatistiekGWT.rb.getString("percentage_endTotal"));
		this.percentage_endTotal.addStyleName(statistiekCss.radioButton());

		// SPLIT SETTINGS
		this.splitSettingsLabel = new Label(StatistiekGWT.rb.getString("splitsLabel"));
		this.splitSettingsLabel.addStyleName(statistiekCss.titlelabel());

		this.splitButton = new Button(
			StatistiekGWT.rb.getString("splitoptionsButton"));
		this.splitButton.addStyleName(statistiekCss.button());

		this.splitVarLabel = new Label(
			StatistiekGWT.rb.getString("splitvariableLabel"));
		this.splitVarLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.splitVarBox = new ListBox();

		this.splitBinsLabel = new Label(
			StatistiekGWT.rb.getString("noClassesLabel"));
		this.splitBinsLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.splitBinsBox = new ListBox();
		for (int i = 0; i < options1.length; i++)
		{
			this.splitBinsBox.addItem(options1[i].toString());
		}

		this.splitChooseBoundariesButton = new Button(
			StatistiekGWT.rb.getString("binsButton"));

		this.separatorSplitBoundaries = new HTML(HistogramUserOptionsPanel.hrString);
		this.separatorSplitBoundaries.addStyleName(statistiekCss.horizontalrule());

		this.splitMinBoundaryLabel = new Label(
			StatistiekGWT.rb.getString("startvalueLabel"));
		this.splitMinBoundaryLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.splitMinBoundaryField = new TextBox();

		this.splitBinWidthLabel = new Label(
			StatistiekGWT.rb.getString("classwidthLabel"));
		this.splitBinWidthLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.splitBinWidthField = new TextBox();

		this.splitBoundariesLabel = new Label(
			StatistiekGWT.rb.getString("binsButton"));
		this.splitBoundariesLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.splitBoundariesArea = new TextArea();
		this.splitBoundariesArea.setEnabled(false);
		this.splitBoundariesArea.addStyleName(statistiekCss.boxsizingborder());
		
		this.splitNoObjectsLabel = new Label("");

		this.splitMinValueLabel = new Label("");

		this.splitMaxValueLabel = new Label("");

		this.stackModeBox = new CheckBox(
			StatistiekGWT.rb.getString("stackfrequencypolygonsCheckbox"), true);
		this.stackModeBox.setVisible(this.model.isFrequencyPolygonMode());

		this.okButton = new Button(StatistiekGWT.rb.getString("OKButtonText"));
	}

	private void layoutGuiComponents()
	{
		HorizontalPanel allSettingsPanel;
		// Hier geen VerticalPanel; geeft problemen met de gegenereerde HTML
		// zie: http://mechanitis.blogspot.nl/2011/01/gwt-why-verticalpanel-is-evil.html
		FlowPanel variableSettingsPanel, binsSettingsPanel, displaySettingsPanel, splitSettingsPanel;

		// VARIABLE SETTINGS
		variableSettingsPanel = new FlowPanel();//new LayoutPanel();
		variableSettingsPanel.setTitle(StatistiekGWT.rb.getString("variableLabel")); // tooltip boven panel
		variableSettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		variableSettingsPanel.add(this.varLabel);
		variableSettingsPanel.add(this.columnIndexBox);
		variableSettingsPanel.add(this.axisLabel);
		variableSettingsPanel.add(this.axisBox);

		// BIN SETTINGS
		binsSettingsPanel = new FlowPanel();
		binsSettingsPanel.setTitle(StatistiekGWT.rb.getString("classDivisionLabel")); // tooltip boven panel
		binsSettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		binsSettingsPanel.add(this.binSettingsLabel);
		binsSettingsPanel.add(this.optimizeScaleBox);
		binsSettingsPanel.add(this.minBoundaryLabel);
		binsSettingsPanel.add(this.minBoundaryField);
		binsSettingsPanel.add(this.maxOnScaleLabel);
		binsSettingsPanel.add(this.maxOnScaleField);

		binsSettingsPanel.add(this.binWidthLabel);
		binsSettingsPanel.add(this.binWidthField);
		
		binsSettingsPanel.add(this.separatorBinSettings);

		binsSettingsPanel.add(this.binsLabel);
		binsSettingsPanel.add(this.binsBox);
		binsSettingsPanel.add(this.noObjectsLabel);
		binsSettingsPanel.add(this.minValueLabel);
		binsSettingsPanel.add(this.maxValueLabel);

		// DISPLAY
		displaySettingsPanel = new FlowPanel();
		displaySettingsPanel.setTitle(StatistiekGWT.rb.getString("absRelLabel")); // tooltip boven panel
		displaySettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		displaySettingsPanel.add(this.absRelLabel);
		displaySettingsPanel.add(this.amountRadioItem);
		displaySettingsPanel.add(this.percentageRadioItem);
		if (this.model.isFrequencyPolygonMode())
		{
			displaySettingsPanel.add(this.cumulativeBox);
		}
		displaySettingsPanel.add(this.amountLabelHR);
		displaySettingsPanel.add(this.labelBetweenBinsRadioItem);
		displaySettingsPanel.add(this.labelUnderBinRadioItem);
		displaySettingsPanel.add(this.labelSplitHR);
		displaySettingsPanel.add(this.separateRadioItem);
		if (this.model.isFrequencyPolygonMode())
		{
			displaySettingsPanel.add(this.singleViewRadioItem);
		}
		else
		{
			displaySettingsPanel.add(this.aboveEachOtherRadioItem);
			displaySettingsPanel.add(this.nextToEachOtherRadioItem);
		}
		displaySettingsPanel.add(this.splitViewPercentageHR);
		displaySettingsPanel.add(this.percentage_endTotal);
		displaySettingsPanel.add(this.percentage_splitTotal);

		// SPLIT OPTIONS
		splitSettingsPanel = new FlowPanel();
		splitSettingsPanel.setTitle(StatistiekGWT.rb.getString("splitsLabel")); // tooltip boven panel
		splitSettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		splitSettingsPanel.add(this.splitSettingsLabel);
		splitSettingsPanel.add(this.splitButton);
		splitSettingsPanel.add(this.splitVarLabel);
		splitSettingsPanel.add(this.splitVarBox);
		splitSettingsPanel.add(this.splitBinsLabel);
		splitSettingsPanel.add(this.splitBinsBox);
		
		splitSettingsPanel.add(this.separatorSplitBoundaries);
		splitSettingsPanel.add(this.splitChooseBoundariesButton);

		splitSettingsPanel.add(this.splitMinBoundaryLabel);
		splitSettingsPanel.add(this.splitMinBoundaryField);

		splitSettingsPanel.add(this.splitBinWidthLabel);
		splitSettingsPanel.add(this.splitBinWidthField);

		splitSettingsPanel.add(this.splitBoundariesLabel);
		splitSettingsPanel.add(this.splitBoundariesArea);
		splitSettingsPanel.add(this.splitNoObjectsLabel);
		splitSettingsPanel.add(this.splitMinValueLabel);
		splitSettingsPanel.add(this.splitMaxValueLabel);

		// Put settings panels together on allSettingsPanel
		allSettingsPanel = new HorizontalPanel();//new LayoutPanel();
		allSettingsPanel.setBorderWidth(2);
		allSettingsPanel.addStyleName(this.statistiekCss.horizontalPanel());
		allSettingsPanel.add(variableSettingsPanel);
		allSettingsPanel.add(binsSettingsPanel);
		allSettingsPanel.add(displaySettingsPanel);
		allSettingsPanel.add(splitSettingsPanel);

		this.basisPanel.setHeight("100%");
		this.basisPanel.setWidth("100%");
		this.basisPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		this.basisPanel.add(allSettingsPanel);
		this.basisPanel.add(this.okButton);

		init();
	}

	public DialogButton getDialogButton()
	{
		return dialogButton;
	}

	public int getVarBoxSelectedIndex()
	{
		return this.columnIndexBox.getSelectedIndex();
	}

	public int getSplitVarBoxSelectedIndex()
	{
		return this.splitVarBox.getSelectedIndex();
	}

	public boolean isCumulativeBoxSelected()
	{
		return this.cumulativeBox != null && this.cumulativeBox.getValue();
	}

	public boolean isSplitSingleViewSelected()
	{
		return this.nextToEachOtherRadioItem.getValue()
			|| aboveEachOtherRadioItem.getValue()
			|| singleViewRadioItem.getValue();
	}

	public boolean isNextToEachOtherSelected()
	{
		// System.out.println("HistogramUserOptionsPanel.isNextToEachOtherSelected(): nextToEachOtherRadioItem.isSelected()="
		// + this.nextToEachOtherRadioItem.isSelected());
		return this.nextToEachOtherRadioItem.getValue();
	}

	public boolean isStackModeBoxSelected()
	{
		return this.stackModeBox.getValue();
	}

	public int getBinsBoxSelectedInt()
	{
		int selectedIndex = this.binsBox.getSelectedIndex();
		String itemText = this.binsBox.getItemText(selectedIndex);
		return Integer.parseInt(itemText);
	}

	public int getSplitBinsBoxSelectedInt()
	{
		int selectedIndex = this.splitBinsBox.getSelectedIndex();
		String itemText = this.splitBinsBox.getItemText(selectedIndex);
		return Integer.parseInt(itemText);
	}

	public double getMinBoundary()
	{
		String s = this.minBoundaryField.getText();
		s = s.replace(',', '.');
		return Double.parseDouble(s);
	}

	public TextBox getMinBoundaryTextField()
	{
		return this.minBoundaryField;
	}
	
	public void setMinBoundary(double d)
	{
		this.minBoundaryField.setText(String.valueOf(d));
	}

	public double getSplitMinBoundary()
	{
		String s = this.splitMinBoundaryField.getText();
		s = s.replace(',', '.');
		return Double.parseDouble(s);
	}

	public void setSplitMinBoundary(double d)
	{
		this.splitMinBoundaryField.setText(String.valueOf(d));
	}

	public double getMaxOnScale()
	{
		String s = this.maxOnScaleField.getText();
		s = s.replace(',', '.');
		return Double.parseDouble(s);
	}
	
	public TextBox getMaxOnScaleField()
	{
		return this.maxOnScaleField;
	}

	public void setMaxOnScale(double d)
	{
		this.maxOnScaleField.setText(String.valueOf(d));
	}

	public TextBox getBinWidthTextField()
	{
		return this.binWidthField;
	}
	
	public double getBinWidth()
	{
		double d = 1;
		String s = this.binWidthField.getText();
		s = s.replace(',', '.');
		try
		{
			d = Double.parseDouble(s); 
		}
		catch (NumberFormatException e)
		{
			System.out.println("Klassenbreedte heeft niet het goede formaat. Cannot parse bin width " + s);
			//e.printStackTrace();
		}
		return d;
	}
	
	/**
	 *Set the bin width based on the model's bin boundaries. 
	 */
	public void setBinWidth()
	{
		String w;
		if (this.model.getStatTableModel().isEmptyColumn(this.model.getColumnIndex()))
		{
			w = StatistiekGWT.getStringValue(this.model.getBinWidth());
		}
		else
		{
			this.binWidthField.setText(StatistiekGWT.getFormattedBinWidth(this.model.getBinBoundaries()));
		}
	}
	
	public void setBinWidth(double d)
	{
		this.binWidthField.setText(String.valueOf(d));
	}
	
	public double getSplitBinWidth()
	{
		String s = this.splitBinWidthField.getText();
		s = s.replace(',', '.');
		return Double.parseDouble(s);
	}

	public void setSplitBinWidth(double d)
	{
		this.splitBinWidthField.setText(String.valueOf(d));
	}
	
	/**
	 *Set the split bin width based on the model's split bin boundaries. 
	 */
	public void setSplitBinWidth()
	{
		this.splitBinWidthField.setText(StatistiekGWT.getFormattedBinWidth(this.model.getSplitOptions().getBinBoundaries()));
	}
	
	public boolean xAxisSelected()
	{
		return this.axisBox.getSelectedIndex() == 0;
	}

	public boolean percentageItemSelected()
	{
		return this.percentageRadioItem.getValue();
	}
	
	public boolean percentageSplitTotalSelected()
	{
		return this.percentage_splitTotal.getValue();
	}
	
	public boolean labelUnderBinSelected()
	{
		return this.labelUnderBinRadioItem.getValue();
	}

	public boolean labelBetweenBinsSelected()
	{
		return this.labelBetweenBinsRadioItem.getValue();
	}

	public void setModel(HistogramModel model)
	{
		this.model = model;
	}

	public void update()
	{
		this.optimizeScaleBox.setValue(this.model.isOptimizeScale());
		
		updateVarBox();

		updateSplitVarBox();

		this.setSelectedItemInListBox(
			this.binsBox, String.valueOf(this.model.getNoBins()));

		this.setSelectedItemInListBox(
			this.splitBinsBox, 
			String.valueOf(this.model.getSplitOptions().getBinBoundaries().size() - 1));

		updateBinSettings();

		updateSplitBinSettings();

		if (this.model.isFrequencyPolygonMode()
			&& this.model.isFrequencyPolygonCumulativeMode() != this.isCumulativeBoxSelected())
		{
			this.cumulativeBox.setValue(this.model
				.isFrequencyPolygonCumulativeMode());
		}
		
		if (this.model.getLabelUnderBin())
		{
			this.labelUnderBinRadioItem.setValue(true);
		}
		else
		{
			this.labelBetweenBinsRadioItem.setValue(true);
		}

		if (this.model.getPercentage())
		{
			this.percentageRadioItem.setValue(true);
		}
		else
		{
			this.amountRadioItem.setValue(true);
		}

		if (this.model.hasVerticalBars())
		{
			this.axisBox.setSelectedIndex(0);
		}
		else
		{
			this.axisBox.setSelectedIndex(1);
		}

		boolean split = this.hasSplit();
		this.setVisibleSplitOptions(this.splitOptionsVisible);
		this.setVisiblePercentageSettings(this.model.getPercentage() && split && !model.isSplitInSingleView());
		
		this.singleViewRadioItem.setValue(this.model.splitInSingleView()
			&& split && this.model.isFrequencyPolygonMode());
		this.separateRadioItem.setValue(!this.model.splitInSingleView()
			&& split);

		this.nextToEachOtherRadioItem.setValue(this.model
			.splitInSingleView() && this.model.isNextToEachOther() && split);

		this.aboveEachOtherRadioItem.setValue(this.model.splitInSingleView()
			&& !this.model.isNextToEachOther() && split && !this.model.isFrequencyPolygonMode());

		this.stackModeBox.setValue(this.model.isFrequencyPolygonStackMode());
		this.stackModeBox.setEnabled(this.model.isFrequencyPolygonMode()
			&& this.model.isFrequencyPolygonCumulativeMode());
	}

	private void updateSplitBinSettings()
	{
		// check of column index valid
		if (this.model.columnIndexValid())
		{
    		if (this.model.getSplitOptions().getColumnSplitIndex() > -1)
    		{
    			ArrayList<ColumnType> typeList = this.model.getStatTableModel().getColumnTypes();
    			ColumnType cSplitType = typeList.get(this.model.getSplitOptions().getColumnSplitIndex());
    			AllowedTypes splitType = cSplitType.getType();
    			if (splitType.equals(AllowedTypes.DOUBLE)
    				|| splitType.equals(AllowedTypes.INTEGER))
    			{
    				this.splitMinBoundaryField.setText(
    					StatistiekGWT.getStringValue(this.model.getSplitOptions().getBinBoundaries().get(0)));
    				this.splitBinWidthField.setText(
    					StatistiekGWT.getFormattedBinWidth(this.model.getSplitOptions().getBinBoundaries()));
    				
    				StringBuilder sb = new StringBuilder();
    				int splitClasses = this.model.getStatTableModel().numberOfSplitVarClasses(
    					this.model.getSplitOptions());
    				for (int i = 0; i < splitClasses; i++)
    				{
    					sb.append(this.model.getSplitOptions()
    						.getSplitClassLabel(i, this.model.getStatTableModel()));
    					sb.append("\n");
    				}

    				this.splitBoundariesArea.setText(sb.toString());
    				this.splitNoObjectsLabel.setText(StatistiekGWT.rb
    					.getString("numberLabel")
    					+ this.model.getStatTableModel().getRowCount());
    				String splitMinValue = StatistiekGWT.getStringValue(
    					this.model.getStatTableModel().getColumnMin(this.model.getSplitOptions().getColumnSplitIndex()));
    				this.splitMinValueLabel.setText(StatistiekGWT.rb.getString("minLabel")
    					+ splitMinValue);
    				String splitMaxValue = StatistiekGWT.getStringValue(
    					this.model.getStatTableModel().getColumnMax(this.model.getSplitOptions().getColumnSplitIndex()));
    				this.splitMaxValueLabel.setText(StatistiekGWT.rb.getString("maxLabel")
    					+ splitMaxValue);
    				this.splitBinsLabel.setVisible(true);
    				this.splitBinsBox.setVisible(true);
    				setSplitEnumClasses(false);
    			}
    			else if (splitType.equals(AllowedTypes.ENUM))
    			{
    				StringBuilder sb = new StringBuilder();
    				for (String s : cSplitType.getEnumOptions())
    				{
    					sb.append(s);
    					sb.append("\n");
    				}
    				String stringWithoutWildcard = sb.substring(0, sb.length() - 2);//- 1); -2 voor /n en wildcard
    				this.splitBoundariesArea.setText(stringWithoutWildcard);
    				this.splitBinsBox.setVisible(false);
    				this.splitBinsLabel.setVisible(false);
    				setSplitEnumClasses(true);
    			}
				else if (splitType.equals(AllowedTypes.STRING))
				{
					StringBuilder sb = new StringBuilder();
					int splitClasses = this.model.getStatTableModel().numberOfSplitVarClasses(
    					this.model.getSplitOptions());
					for (int i = 0; i < splitClasses; i++)
					{
    					sb.append(this.model.getSplitOptions()
    						.getSplitClassLabel(i, this.model.getStatTableModel()));
						sb.append("\n");
					}
					
    				this.splitBoundariesArea.setText(sb.toString());
    				this.separatorSplitBoundaries.setVisible(false);
					this.splitBinsBox.setVisible(false);
					this.splitBinsLabel.setVisible(false);
					setSplitEnumClasses(true);
				}
    		}
		}
	}

	private void updateBinSettings()
	{
		if (this.model.columnIndexValid())
		{
			ArrayList<ColumnType> typeList = this.model.getStatTableModel().getColumnTypes(); 
			ColumnType cType = typeList.get(this.model.getColumnIndex());
			AllowedTypes type = cType.getType();
			if (type.equals(AllowedTypes.DOUBLE)
				|| type.equals(AllowedTypes.INTEGER))
			{
				this.minBoundaryField.setText(StatistiekGWT.getStringValue(this.view.getBinsOnScale().get(0)));
				int last = this.view.getBinsOnScale().size() - 1;
				this.maxOnScaleField.setText(StatistiekGWT.getStringValue(this.view.getBinsOnScale().get(last)));
				String binWidth;
				if (this.model.isOptimizeScale())
				{
					binWidth = StatistiekGWT.getFormattedBinWidth(this.model.getBinBoundaries());
				}
				else
				{
					binWidth = StatistiekGWT.getStringValue(this.model.getBinWidth());
				}				
				this.binWidthField.setText(binWidth);
				this.noObjectsLabel.setText(StatistiekGWT.rb
					.getString("numberLabel")
					+ this.model.getStatTableModel().getNumberOfValidDataRows(this.model.getColumnIndex()));
				String minValue = StatistiekGWT.getStringValue(
					this.model.getStatTableModel().getColumnMin(this.model.getColumnIndex()));
				this.minValueLabel.setText(StatistiekGWT.rb.getString("minLabel")
					+ minValue);
				String maxValue = StatistiekGWT.getStringValue(
					this.model.getStatTableModel().getColumnMax(this.model.getColumnIndex()));
				this.maxValueLabel.setText(StatistiekGWT.rb.getString("maxLabel")
					+ maxValue);
				this.separatorBinSettings.setVisible(true);
				this.separatorSplitBoundaries.setVisible(true);
				this.labelBetweenBinsRadioItem.setVisible(true);
				this.labelUnderBinRadioItem.setVisible(true);
				this.binsBox.setVisible(true);
				this.binsLabel.setVisible(true);
				this.optimizeScaleBox.getParent().setVisible(true);
				this.optimizeScaleBox.setVisible(true);
				setEnumClasses(false);
			}
			else if (type.equals(AllowedTypes.ENUM) || type.equals(AllowedTypes.STRING))
			{
				this.separatorBinSettings.setVisible(false);
				this.separatorSplitBoundaries.setVisible(false);
				this.labelBetweenBinsRadioItem.setVisible(false);
				this.labelUnderBinRadioItem.setVisible(false);
				this.binsBox.setVisible(false);
				this.binsLabel.setVisible(false);
				this.optimizeScaleBox.getParent().setVisible(false); // voor enum/string geen indeling-settings, dus hele blok (parent) verbergen
				this.optimizeScaleBox.setVisible(false);
				setEnumClasses(true);
			}
		}
	}

	private void updateSplitVarBox()
	{
		StatistiekUtils.removeAllItemsFromListBox(this.splitVarBox);
		this.splitVarBox.addItem(StatistiekGWT.rb.getString("chooseItem"));
		for (int column = 0; column < this.model.getStatTableModel()
			.getColumnCount(); column++)
		{
			splitVarBox.addItem(this.model.getStatTableModel()
				.getColumnName(column));
		}
		
		// check of columnindex valid
		if (this.model.columnIndexValid())
		{
			this.splitVarBox.setSelectedIndex(this.model.getSplitOptions()
				.getColumnSplitIndex() + 1);
		}
		else
		{
			// set no split variable selected
			this.splitVarBox.setSelectedIndex(0);
		}
	}

	private void updateVarBox()
	{
		StatistiekUtils.removeAllItemsFromListBox(this.columnIndexBox);

		ArrayList<String> nameList = this.model.getStatTableModel().getColumnNames();
		for (String varName : nameList)
		{
			this.columnIndexBox.addItem(varName);
		}

		if (this.model.columnIndexValid())
		{
			this.columnIndexBox.setSelectedIndex(this.model.getColumnIndex());
		}
		else
		{
			// set no item selected
			this.columnIndexBox.setSelectedIndex(-1);
		}
	}

	/**
	 * Remove all items from listbox.
	 * @param listBox
	 */
	private void removeAllItemsFromListBox(ListBox listBox)
	{
		for (int i = listBox.getItemCount() - 1; i > -1; i--)
		{
			listBox.removeItem(i);
		}
	}

	public void init()
	{
		this.setVisibleBoundaryOptions();
		this.setVisibleSplitBoundaryOptions(false);
	}

	/**
	 * Set the given string item selected in typeBox.
	 * @param type
	 */
	private void setSelectedItemInListBox(ListBox listBox, String string)
	{
		// find the index of string
		int indexToFind = -1;
		for (int i=0; i < listBox.getItemCount(); i++) 
		{
		    if (listBox.getItemText(i).equals(string)) 
		    {
		        indexToFind = i;
		        break;
		    }
		}
		listBox.setSelectedIndex(indexToFind);
	}

	private void setEnumClasses(boolean b)
	{
		this.enumClasses = b;
		
		// vertical box containing bin settings not visible for enum variable 
		this.binWidthLabel.setVisible(!b);

		// set visibility of the components
		this.amountLabelHR.setVisible(!b);
		this.separatorSplitBoundaries.setVisible(!b);
		this.minBoundaryLabel.setVisible(!b);
		this.minBoundaryField.setVisible(!b);
		this.maxOnScaleLabel.setVisible(!b && !isOptimizeScale());
		this.maxOnScaleField.setVisible(!b && !isOptimizeScale());
		this.binWidthLabel.setVisible(!b);
		this.binWidthField.setVisible(!b);
		
		this.binsLabel.setVisible(!b && isOptimizeScale());
		this.binsBox.setVisible(!b && isOptimizeScale());
		
		this.noObjectsLabel.setVisible(!b);
		this.minValueLabel.setVisible(!b);
		this.maxValueLabel.setVisible(!b);
	}

	private void setSplitEnumClasses(boolean b)
	{
		this.splitEnumClasses = b;
		this.splitMinBoundaryLabel.setVisible(this.splitBoundariesVisible && !b);
		this.splitMinBoundaryField.setVisible(this.splitBoundariesVisible && !b);
		this.splitBinWidthLabel.setVisible(this.splitBoundariesVisible && !b);
		this.splitBinWidthField.setVisible(this.splitBoundariesVisible && !b);
		this.splitNoObjectsLabel.setVisible(this.splitBoundariesVisible && !b);
		this.splitMinValueLabel.setVisible(this.splitBoundariesVisible && !b);
		this.splitMaxValueLabel.setVisible(this.splitBoundariesVisible && !b);
	}

	private void setVisibleBoundaryOptions()
	{
		this.separatorBinSettings.setVisible(!this.enumClasses);
		this.optimizeScaleBox.setVisible(!this.enumClasses);
		this.minBoundaryLabel.setVisible(!this.enumClasses);
		this.minBoundaryField.setVisible(!this.enumClasses);
		this.maxOnScaleLabel.setVisible(!this.enumClasses && !isOptimizeScale());
		this.maxOnScaleField.setVisible(!this.enumClasses && !isOptimizeScale());
		this.binWidthLabel.setVisible(!this.enumClasses);
		this.binWidthField.setVisible(!this.enumClasses);
		this.noObjectsLabel.setVisible(!this.enumClasses);
		this.minValueLabel.setVisible(!this.enumClasses);
		this.maxValueLabel.setVisible(!this.enumClasses);
		this.amountLabelHR.setVisible(!this.enumClasses);
	}

	private void setVisibleSplitBoundaryOptions(boolean b)
	{
		this.splitBoundariesVisible = b;
		this.labelSplitHR.setVisible(b);
		this.splitMinBoundaryLabel.setVisible(b && !this.splitEnumClasses);
		this.splitMinBoundaryField.setVisible(b && !this.splitEnumClasses);
		this.splitBinWidthLabel.setVisible(b && !this.splitEnumClasses);
		this.splitBinWidthField.setVisible(b && !this.splitEnumClasses);
		this.splitBoundariesLabel.setVisible(b);
		this.splitBoundariesArea.setVisible(b);
		this.splitBoundariesArea.setVisible(b);
		this.splitNoObjectsLabel.setVisible(b && !this.splitEnumClasses);
		this.splitMinValueLabel.setVisible(b && !this.splitEnumClasses);
		this.splitMaxValueLabel.setVisible(b && !this.splitEnumClasses);
		if (!b)
		{
			this.splitChooseBoundariesButton.setText(StatistiekGWT.rb
				.getString("binsButton"));
		}
		else
		{
			this.splitChooseBoundariesButton.setText(StatistiekGWT.rb
				.getString("hideButtonLabel"));
		}
	}

	void setVisibleSplitOptions(boolean b)
	{
		this.splitOptionsVisible = b;
		
		if (this.model.isFrequencyPolygonMode())
		{
			this.singleViewRadioItem.setVisible(this.hasSplit());
		}
		else
		{
			this.nextToEachOtherRadioItem.setVisible(this.hasSplit());
			this.aboveEachOtherRadioItem.setVisible(this.hasSplit());
		}
		this.separateRadioItem.setVisible(this.hasSplit());
		this.labelSplitHR.setVisible(this.hasSplit());

		
		this.splitVarLabel.setVisible(b);
		this.splitVarBox.setVisible(b);
		if (!b)
		{
			this.splitBinsLabel.setVisible(b);
		}
		if (!b)
		{
			this.splitBinsBox.setVisible(b);
		}
		
		// alleen als splitvar is gekozen (splitVarBox selectedIndex > 0)
		if (this.hasSplit())
		{
			this.splitChooseBoundariesButton.setVisible(true);
			this.separatorSplitBoundaries.setVisible(true);
		}
		else
		{
			this.splitChooseBoundariesButton.setVisible(false);
			this.separatorSplitBoundaries.setVisible(false);
		}
		
		if (!b)
		{
			this.splitButton.setText(StatistiekGWT.rb.getString("splitoptionsButton"));
			this.setVisibleSplitBoundaryOptions(false);
		}
		else
		{
			this.splitButton.setText(StatistiekGWT.rb
				.getString("removeSplitoptionsButton"));
		}
	}

	/**
	 * Set the visibility of components related to percentage.
	 * 
	 * @param percentage
	 */
	private void setVisiblePercentageSettings(boolean b)
	{
		splitViewPercentageHR.setVisible(b);
		percentage_splitTotal.setVisible(b);
		percentage_endTotal.setVisible(b);
		
		percentage_splitTotal.setValue(this.model.getPercentageSplitTotal());
		percentage_endTotal.setValue(!this.model.getPercentageSplitTotal());
	}

	private boolean hasSplit()
	{
		boolean split = this.model.getSplitOptions().getColumnSplitIndex() > -1;

		return split;
	}

	/**
	 * Clear all split GUI components, i.e. 
	 * split variable, bin settings and labels with information 
	 * about number of objects and minimum and maximum values.
	 */
	private void clearGUISplitComponents()
	{
		this.splitVarBox.setSelectedIndex(0);
		this.splitBinWidthField.setText("");
		this.splitMinBoundaryField.setText("");
		this.splitBoundariesArea.setText("");
		this.splitNoObjectsLabel.setText("");
		this.splitMinValueLabel.setText("");
		this.splitMaxValueLabel.setText("");
	}
	
	public boolean isOptimizeScale()
	{
		return this.optimizeScaleBox.getValue();
	}
	
	public CheckBox getOptimizeScaleBox()
	{
		return this.optimizeScaleBox;
	}


//	@Override
//	public void fireEvent(GwtEvent<?> e)
//	{
//		this.eventBus.fireEvent(e);
//	}

//	/**
//	 * A touchhandler for HistogramUserOptionsPanel
//	 */
//	class HistogramUOPTouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
//	{
//
//		@Override
//		public void onTouchEnd(TouchEndEvent event)
//		{
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void onTouchMove(TouchMoveEvent event)
//		{
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void onTouchStart(TouchStartEvent event)
//		{
//			// TODO Auto-generated method stub
//			
//		}
//		
//	} // class HistogramUOPTouchHandler
	
	/**
	 * A clickhandler for HistogramUserOptionsPanel
	 */
	class HistogramUOPClickHandler implements ClickHandler
	{

		@Override
		public void onClick(ClickEvent e)
		{
			HistogramModel model = HistogramUserOptionsPanel.this.model;
			HistogramView view = HistogramUserOptionsPanel.this.view;
			HistogramController controller = HistogramUserOptionsPanel.this.controller;
			
			if ((e.getSource() == amountRadioItem) || (e.getSource() == percentageRadioItem))
			{
				model.setPercentage(view.percentageItemSelected());
				this.update();
			}
			else if ((e.getSource() == percentage_splitTotal) || (e.getSource() == percentage_endTotal))
			{
				model.setPercentageSplitTotal(view.percentageSplitTotalSelected());
				this.update();
			}
			else if ((e.getSource() == labelBetweenBinsRadioItem) ||
				e.getSource() == labelUnderBinRadioItem)
			{
				model.setLabelUnderBin(view.labelUnderBinItemSelected());
				this.update();
			}
			else if (e.getSource() == optimizeScaleBox)
			{
				model.setBinWidth(view.getBinWidth());
				model.setMinOnScale(view.getMinBoundary());
				model.setOptimizeScale(view.getUserOptionsPanel().isOptimizeScale());
				this.update();
			}
			else if (e.getSource() == cumulativeBox)
			{
				model.setFrequencyPolygonCumulativeMode(
					view.isCumulativeBoxSelected());
				this.update();
			}
			else if (e.getSource() == singleViewRadioItem)
			{
				model.setSplitInSingleView(view.isSplitSingleViewSelected());
				this.update();
			}
			else if ((e.getSource() == aboveEachOtherRadioItem)
				&& !model.isFrequencyPolygonMode())
			{
				model.setSplitInSingleView(view.isSplitSingleViewSelected());
//				this.model
//					.setNextToEachOther(this.view.isNextToEachOtherSelected());
				// code above is not working (anymore?), so straightforward
				model.setNextToEachOther(false);
				this.update();
			}
			else if ((e.getSource() == nextToEachOtherRadioItem)
				&& !model.isFrequencyPolygonMode())
			{
				model.setSplitInSingleView(view.isSplitSingleViewSelected());
//				this.model
//					.setNextToEachOther(this.view.isNextToEachOtherSelected());
				// code above is not working (anymore?), so straightforward
				model.setNextToEachOther(true);
				this.update();
			}
			else if (e.getSource() == separateRadioItem)
			{
				model.setSplitInSingleView(false);
				this.update();
			}
			else if (e.getSource() == stackModeBox)
			{
				model.setFrequencyPolygonStackMode(view.isStackModeBoxSelected());
				this.update();
			}
			else if (e.getSource() == optimizeScaleBox)
			{
				model.setOptimizeScale(view.getUserOptionsPanel().isOptimizeScale());
				this.update();
			}
			else if (e.getSource() == splitChooseBoundariesButton)
			{
				if (splitBoundariesVisible)
				{
					setVisibleSplitBoundaryOptions(false);
				}
				else
				{
					setVisibleSplitBoundaryOptions(true);
				}

				this.update();
			}
			else if (e.getSource() == splitButton)
			{
				if (splitOptionsVisible)
				{
					// verwijder splitsing...
					model.setColumnSplitIndex(-1);
					setVisibleSplitOptions(false);
					setVisiblePercentageSettings(false);
					clearGUISplitComponents();
					this.update();
				}
				else
				{
					setVisibleSplitOptions(true);
					setVisiblePercentageSettings(model.getPercentage() && !model.isSplitInSingleView());
					this.updateUserOptionsPanel();
				}
			}
			else if (e.getSource() == okButton)
			{
				setVisibleBoundaryOptions();
				if (splitVarBox.getSelectedIndex() == 0)
				{
					setVisibleSplitOptions(false);
				}
				dialogButton.closeDialog();
			}
			else
			{
				//System.out.println("HistogramUserOptionsPanel.HistogramUOPClickHandler.onClick(): Unknown action source! " + e);
			}
		}
		
		/**
		 * Update the user options panel.
		 */
		private void updateUserOptionsPanel()
		{
			// update the user options panel
			HistogramUserOptionsPanel.this.update();
		}

		/**
		 * Update the view and the user options panel.
		 */
		private void update()
		{
			// update view and user options panel
			HistogramUserOptionsPanel.this.view.update();
		}
	} // class HistogramUOPClickHandler

	/**
	 * A blurhandler for HistogramUserOptionsPanel
	 */
	class HistogramUOPBlurHandler implements BlurHandler
	{
		@Override
		public void onBlur(BlurEvent e)
		{
			HistogramController controller = HistogramUserOptionsPanel.this.controller;

			if (e.getSource() == minBoundaryField)
			{
				// update column index bin settings
				controller.updateBoundariesFromBinSettings();
				
				// zorg dat maximumwaarde overeenkomt met de hoogste bin waarde op de schaal
				double maxBinValue = view.getMaxBinOnScale();
				
				if ((view.getUserOptionsPanel().getMaxOnScale() < maxBinValue) 
					&& !model.getStatTableModel().isEmptyColumn(model.getColumnIndex())) // for empty table or column without any values every maximum is allowed
				{
					if (model.getMaxOnScale() < maxBinValue)
					{
						// the model's max is not correct, data may have been changed and the model's max on scale needs to be reset
						model.setMaxOnScale(maxBinValue);
					}
					else
					{
						// reset to latest value
						view.getUserOptionsPanel().setMaxOnScale(model.getMaxOnScale());
					}
				}
				else
				{
					model.setMaxOnScale(view.getUserOptionsPanel().getMaxOnScale());
				}
			}
			else if (e.getSource() == maxOnScaleField)
			{
				if (model.getStatTableModel().isEmptyColumn(model.getColumnIndex()))
				{
					// max < min is niet toegestaan
					if (view.getUserOptionsPanel().getMaxOnScale() < view.getUserOptionsPanel().getMinBoundary())
					{
						// reset to latest value
						view.getUserOptionsPanel().setMaxOnScale(model.getMaxOnScale());
					}
					else
					{
						model.setMaxOnScale(view.getUserOptionsPanel().getMaxOnScale());
					}
				}
				else
				{
					double maxBinValue = model.getMaxBinBoundaryValue();
					if (view.getUserOptionsPanel().getMaxOnScale() < maxBinValue)
					{
						if (model.getMaxOnScale() < maxBinValue)
						{
							// the model's max is not correct, data may have been changed and the model's max on scale needs to be reset
							model.setMaxOnScale(maxBinValue);
						}
						else
						{
							// reset to latest value
							view.getUserOptionsPanel().setMaxOnScale(model.getMaxOnScale());
						}
					}
					else
					{
						model.setMaxOnScale(view.getUserOptionsPanel().getMaxOnScale());
					}
				}
			}
			else if (e.getSource() == binWidthField)
			{
				model.setBinWidth(view.getBinWidth());
				
				// column index bin settings
				controller.updateBoundariesFromBinSettings();

				// zorg dat maximumwaarde overeenkomt met de hoogste bin waarde op de schaal
				double maxBinValue = view.getMaxBinOnScale();
				if (view.getUserOptionsPanel().getMaxOnScale() < maxBinValue)
				{
					view.getUserOptionsPanel().setMaxOnScale(maxBinValue);
				}
				else
				{
					model.setMaxOnScale(view.getUserOptionsPanel().getMaxOnScale());
				}
			}
			else if (e.getSource() == splitMinBoundaryField)
			{
				// update split index bin settings
				controller.updateSplitBoundariesFromBinSettings();
			}
			else if (e.getSource() == splitBinWidthField)
			{
				// update split index bin settings
				controller.updateSplitBoundariesFromBinSettings();
			}

			// update view
			HistogramUserOptionsPanel.this.view.update();
		}
	} // class HistogramUOPBlurHandler
	
	class HistogramUOPChangeHandler implements ChangeHandler
	{
		@Override
		public void onChange(ChangeEvent e)
		{
			if (e.getSource() == columnIndexBox)
			{
				model.setOptimizeScale(true); // default
				model.setColumnIndex(HistogramUserOptionsPanel.this.getVarBoxSelectedIndex());
			}
			else if (e.getSource() == binsBox)
			{
				model.setNoBins(view.getBinsBoxSelectedInt());
			}
			else if (e.getSource() == splitBinsBox)
			{
				controller.setSplitType(model.getStatTableModel().getColumnTypes()
					.get(model.getSplitOptions().getColumnSplitIndex())
					.getType());
			}
			else if (e.getSource() == axisBox)
			{
				model.setVerticalBars(view.xAxisSelected());
			}
			else if (e.getSource() == splitVarBox)
			{
				model.setColumnSplitIndex(HistogramUserOptionsPanel.this.getSplitVarBoxSelectedIndex() - 1);
				ArrayList<ColumnType> list = model.getStatTableModel().getColumnTypes();
				controller.setSplitType(list.get(model.getSplitOptions().getColumnSplitIndex())
					.getType());			
			}

			// update view (and uop)
			view.update();
		}
	} // class HistogramUOPChangeHandler

	class HistogramUOPValueChangeHandler implements ValueChangeHandler<String>
	{
		@Override
		public void onValueChange(ValueChangeEvent<String> e)
		{
			if (e.getSource() == minBoundaryField)
			{
				// update column index bin settings
				controller.updateBoundariesFromBinSettings();
				
				// zorg dat maximumwaarde overeenkomt met de hoogste bin waarde op de schaal
				double maxBinValue = view.getMaxBinOnScale();
				
				if ((view.getUserOptionsPanel().getMaxOnScale() < maxBinValue) 
					&& !model.getStatTableModel().isEmptyColumn(model.getColumnIndex())) // for empty table or column without any values every maximum is allowed
				{
					if (model.getMaxOnScale() < maxBinValue)
					{
						// the model's max is not correct, data may have been changed and the model's max on scale needs to be reset
						model.setMaxOnScale(maxBinValue);
					}
					else
					{
						// reset to latest value
						view.getUserOptionsPanel().setMaxOnScale(model.getMaxOnScale());
					}
				}
				else
				{
					model.setMaxOnScale(view.getUserOptionsPanel().getMaxOnScale());
				}
			}
			else if (e.getSource() == maxOnScaleField)
			{
				if (model.getStatTableModel().isEmptyColumn(model.getColumnIndex()))
				{
					// max < min is niet toegestaan
					if (view.getUserOptionsPanel().getMaxOnScale() < view.getUserOptionsPanel().getMinBoundary())
					{
						// reset to latest value
						view.getUserOptionsPanel().setMaxOnScale(model.getMaxOnScale());
					}
					else
					{
						model.setMaxOnScale(view.getUserOptionsPanel().getMaxOnScale());
					}
				}
				else
				{
					double maxBinValue = model.getMaxBinBoundaryValue();
					if (view.getUserOptionsPanel().getMaxOnScale() < maxBinValue)
					{
						if (model.getMaxOnScale() < maxBinValue)
						{
							// the model's max is not correct, data may have been changed and the model's max on scale needs to be reset
							model.setMaxOnScale(maxBinValue);
						}
						else
						{
							// reset to latest value
							view.getUserOptionsPanel().setMaxOnScale(model.getMaxOnScale());
						}
					}
					else
					{
						model.setMaxOnScale(view.getUserOptionsPanel().getMaxOnScale());
					}
				}
			}
			else if (e.getSource() == binWidthField)
			{
				model.setBinWidth(view.getBinWidth());
				
				// column index bin settings
				controller.updateBoundariesFromBinSettings();

				// zorg dat maximumwaarde overeenkomt met de hoogste bin waarde op de schaal
				double maxBinValue = view.getMaxBinOnScale();
				if (view.getUserOptionsPanel().getMaxOnScale() < maxBinValue)
				{
					view.getUserOptionsPanel().setMaxOnScale(maxBinValue);
				}
				else
				{
					model.setMaxOnScale(view.getUserOptionsPanel().getMaxOnScale());
				}
			}
			else if (e.getSource() == splitMinBoundaryField)
			{
				// update split index bin settings
				controller.updateSplitBoundariesFromBinSettings();
			}
			else if (e.getSource() == splitBinWidthField)
			{
				// update split index bin settings
				controller.updateSplitBoundariesFromBinSettings();
			}

			// update view
			HistogramUserOptionsPanel.this.view.update();
		}
	} // class HistogramUOPValueChangeHandler

	class HistogramUOPKeyDownHandler implements KeyDownHandler
	{
		@Override
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				HistogramController controller = HistogramUserOptionsPanel.this.controller;

				if (e.getSource() == minBoundaryField)
				{
					// update column index bin settings
					controller.updateBoundariesFromBinSettings();
					
					// zorg dat maximumwaarde overeenkomt met de hoogste bin waarde op de schaal
					double maxBinValue = view.getMaxBinOnScale();
					
					if ((view.getUserOptionsPanel().getMaxOnScale() < maxBinValue) 
						&& !model.getStatTableModel().isEmptyColumn(model.getColumnIndex())) // for empty table or column without any values every maximum is allowed
					{
						if (model.getMaxOnScale() < maxBinValue)
						{
							// the model's max is not correct, data may have been changed and the model's max on scale needs to be reset
							model.setMaxOnScale(maxBinValue);
						}
						else
						{
							// reset to latest value
							view.getUserOptionsPanel().setMaxOnScale(model.getMaxOnScale());
						}
					}
					else
					{
						model.setMaxOnScale(view.getUserOptionsPanel().getMaxOnScale());
					}
				}
				else if (e.getSource() == maxOnScaleField)
				{
					if (model.getStatTableModel().isEmptyColumn(model.getColumnIndex()))
					{
						// max < min is niet toegestaan
						if (view.getUserOptionsPanel().getMaxOnScale() < view.getUserOptionsPanel().getMinBoundary())
						{
							// reset to latest value
							view.getUserOptionsPanel().setMaxOnScale(model.getMaxOnScale());
						}
						else
						{
							model.setMaxOnScale(view.getUserOptionsPanel().getMaxOnScale());
						}
					}
					else
					{
						double maxBinValue = model.getMaxBinBoundaryValue();
						if (view.getUserOptionsPanel().getMaxOnScale() < maxBinValue)
						{
							if (model.getMaxOnScale() < maxBinValue)
							{
								// the model's max is not correct, data may have been changed and the model's max on scale needs to be reset
								model.setMaxOnScale(maxBinValue);
							}
							else
							{
								// reset to latest value
								view.getUserOptionsPanel().setMaxOnScale(model.getMaxOnScale());
							}
						}
						else
						{
							model.setMaxOnScale(view.getUserOptionsPanel().getMaxOnScale());
						}
					}
				}
				else if (e.getSource() == binWidthField)
				{
					model.setBinWidth(view.getBinWidth());
					
					// column index bin settings
					controller.updateBoundariesFromBinSettings();

					// zorg dat maximumwaarde overeenkomt met de hoogste bin waarde op de schaal
					double maxBinValue = view.getMaxBinOnScale();
					if (view.getUserOptionsPanel().getMaxOnScale() < maxBinValue)
					{
						view.getUserOptionsPanel().setMaxOnScale(maxBinValue);
					}
					else
					{
						model.setMaxOnScale(view.getUserOptionsPanel().getMaxOnScale());
					}
				}
				else if (e.getSource() == splitMinBoundaryField)
				{
					// update split index bin settings
					controller.updateSplitBoundariesFromBinSettings();
				}
				else if (e.getSource() == splitBinWidthField)
				{
					// update split index bin settings
					controller.updateSplitBoundariesFromBinSettings();
				}

				// update view
				HistogramUserOptionsPanel.this.view.update();
			}
		}
	} // class HistogramUOPKeyDownHandler
}
