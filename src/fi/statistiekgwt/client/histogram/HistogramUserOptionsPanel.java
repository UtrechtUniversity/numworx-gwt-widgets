package fi.statistiekgwt.client.histogram;

import java.util.ArrayList;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

import fi.statistiekgwt.client.DialogButton;
import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;
import fi.statistiekgwt.client.StatistiekUtils;
import fi.statistiekgwt.client.columndialog.ColumnDialogController;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * @author Sylvia van Borkulo
 *
 */
public class HistogramUserOptionsPanel extends LayoutPanel // implements HasHandlers
	//implements ActionListener
{

	private HistogramView view;
	private HistogramController controller;
	private HistogramModel model;

	private DialogButton dialogButton;

	private LayoutPanel basisPanel;
	/**
	 * Panel 'alles' wordt aan DialogButton meegegeven als content.
	 */
	private LayoutPanel alles;
	
	// test syl: waarschijnlijk een of meerdere van dit om de handlers te kunnen verwijderen
	// heb ik er 1 nodig voor iedere widget met handler?
//	private HandlerRegistration[] handlerRegistration[];
//	private HistogramUOPTouchHandler touchHandler;
	private HistogramUOPClickHandler clickHandler;
	private HistogramUOPBlurHandler blurHandler;
	private HistogramUOPChangeHandler changeHandler;
	private HistogramUOPValueChangeHandler valueChangeHandler;
	private HistogramUOPKeyDownHandler keyDownHandler;

	// variable settings
	private Label varLabel;
	private ListBox varBox;
	private Label axisLabel;
	private ListBox axisBox;

	// bin settings
	private Label binSettingsLabel;
	private Label binsLabel;
	/**
	 * Box for choosing the number of bins.
	 */
	private ListBox binsBox;
	/**
	 * Separator between bin boundaries settings and number of bins settings. 
	 */
	private HTML binSettingsHR;
	private Label minBoundaryLabel;
	private TextBox minBoundaryField;
	private Label binWidthLabel;
	private TextBox binWidthField;
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
	private HTML splitBoundariesHR;
	private Label splitMinBoundaryLabel;
	private TextBox splitMinBoundaryField;
	private Label splitBinWidthLabel;
	private TextBox splitBinWidthField;
	private Label splitBoundariesLabel;
	private TextArea splitBoundariesArea;
//	private ScrollPanel splitBoundariesAreaScrollPanel;
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
			StatistiekGWT.rb.getString("settingsButton"), this.alles);
		
		this.eventBus = StatistiekUtils.EVENT_BUS;//new SimpleEventBus();
	}

	private void addHandlers()
	{
		// click handlers
		this.amountRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);
		this.percentageRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);
		this.cumulativeBox.addClickHandler(this.clickHandler);//addActionListener(controller);
		this.labelBetweenBinsRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);
		this.labelUnderBinRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);
		this.nextToEachOtherRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);
		this.aboveEachOtherRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);
		this.separateRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);
		this.singleViewRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);
		this.splitButton.addClickHandler(this.clickHandler);//addActionListener(this);
		this.splitChooseBoundariesButton.addClickHandler(this.clickHandler);//addActionListener(this);
		this.stackModeBox.addClickHandler(this.clickHandler);//addActionListener(this.controller);
		this.okButton.addClickHandler(this.clickHandler);//addActionListener(this);
		
		// blur handlers
		this.minBoundaryField.addBlurHandler(this.blurHandler);//addFocusListener(controller);
		this.binWidthField.addBlurHandler(this.blurHandler);//addFocusListener(controller);
		this.splitMinBoundaryField.addBlurHandler(this.blurHandler);
		this.splitBinWidthField.addBlurHandler(this.blurHandler);
		
		// key down handlers
		this.minBoundaryField.addKeyDownHandler(this.keyDownHandler);
		this.binWidthField.addKeyDownHandler(this.keyDownHandler);
		this.splitMinBoundaryField.addKeyDownHandler(this.keyDownHandler);
		this.splitBinWidthField.addKeyDownHandler(this.keyDownHandler);
		
		// change handlers
		this.varBox.addChangeHandler(this.changeHandler);
		this.axisBox.addChangeHandler(this.changeHandler);//addActionListener(this.controller);
		this.binsBox.addChangeHandler(this.changeHandler);//addActionListener(this.controller);
		this.splitVarBox.addChangeHandler(this.changeHandler);//addActionListener(this);
		this.splitBinsBox.addChangeHandler(this.changeHandler);//addActionListener(this.controller);
		
		// value change handlers
		this.minBoundaryField.addValueChangeHandler(this.valueChangeHandler);
		this.binWidthField.addValueChangeHandler(this.valueChangeHandler);
		this.splitMinBoundaryField.addValueChangeHandler(this.valueChangeHandler);
		this.splitBinWidthField.addValueChangeHandler(this.valueChangeHandler);
	}

	private void createGuiComponents()
	{
		this.alles = new LayoutPanel();
		this.basisPanel = new LayoutPanel();//new JPanel(new FlowLayout());
		//this.basisPanel.setPixelSize(this.DEFAULT_WIDTH, this.DEFAULT_HEIGHT);
		//this.panel.setBackground(CssColor.make(230, 230, 230));
		//this.panel.addStyleName(statistiekCss.useroptionspanel()");

		this.splitVarLabel = new Label(
			StatistiekGWT.rb.getString("splitvariableLabel"));

		// var settings
		this.varLabel = new Label(StatistiekGWT.rb.getString("variableLabel"));
		this.varLabel.addStyleName(statistiekCss.titlelabel());

		this.varBox = new ListBox();
		this.varBox.setPixelSize(100, 25); // was: setPreferredSize()

		this.axisLabel = new Label(StatistiekGWT.rb.getString("axisLabel"));

		String[] options2 = new String[2];
		options2[0] = "X";
		options2[1] = "Y";
		this.axisBox = new ListBox();
		for (int i = 0; i < options2.length; i++)
		{
			this.axisBox.addItem(options2[i]);
		}
		this.axisBox.setPixelSize(100, 25);

		// bin settings
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
		
		binSettingsHR = new HTML(this.hrString);
		binSettingsHR.addStyleName(statistiekCss.horizontalrule());

		this.minBoundaryLabel = new Label(
			StatistiekGWT.rb.getString("startvalueLabel"));

		this.minBoundaryField = new TextBox();

		this.binWidthLabel = new Label(
			StatistiekGWT.rb.getString("classwidthLabel"));

		this.binWidthField = new TextBox();

		this.noObjectsLabel = new Label("");

		this.minValueLabel = new Label("");

		this.maxValueLabel = new Label("");

		// display settings
		this.absRelLabel = new Label(StatistiekGWT.rb.getString("absRelLabel"));
		this.absRelLabel.addStyleName(statistiekCss.titlelabel());

		this.amountRadioItem = new RadioButton("percAmountGroup",
			StatistiekGWT.rb.getString("amountLabel"));

		this.percentageRadioItem = new RadioButton("percAmountGroup",
			StatistiekGWT.rb.getString("percentageRadio"));

		this.cumulativeBox = new CheckBox(
			StatistiekGWT.rb.getString("cumulativeCheckbox"), false);

		amountLabelHR = new HTML(this.hrString);
		amountLabelHR.addStyleName(statistiekCss.horizontalrule());

		// radiobuttons for position labels
		this.labelBetweenBinsRadioItem = new RadioButton("labelPositionGroup",
			StatistiekGWT.rb.getString("labelBetweenBinsRadio"));

		this.labelUnderBinRadioItem = new RadioButton("labelPositionGroup",
			StatistiekGWT.rb.getString("labelUnderBinRadio"));

		labelSplitHR = new HTML(this.hrString);
		labelSplitHR.addStyleName(statistiekCss.horizontalrule());

		this.nextToEachOtherRadioItem = new RadioButton("splitViewGroup",
			StatistiekGWT.rb.getString("nextToEachOtherRadioItem"));

		this.aboveEachOtherRadioItem = new RadioButton("splitViewGroup",
			StatistiekGWT.rb.getString("aboveEachOtherRadioItem"));

		this.separateRadioItem = new RadioButton("splitViewGroup",
			StatistiekGWT.rb.getString("separateFromEachOtherRadioItem"));

		this.singleViewRadioItem = new RadioButton("splitViewGroup",
			StatistiekGWT.rb.getString("splitsingleviewCheckBox"));

		// split settings
		this.splitSettingsLabel = new Label(StatistiekGWT.rb.getString("splitsLabel"));
		this.splitSettingsLabel.addStyleName(statistiekCss.titlelabel());
		this.splitButton = new Button(
			StatistiekGWT.rb.getString("splitoptionsButton"));

		this.splitVarLabel = new Label(
			StatistiekGWT.rb.getString("splitvariableLabel"));

		this.splitVarBox = new ListBox();

		this.splitBinsLabel = new Label(
			StatistiekGWT.rb.getString("noClassesLabel"));

		this.splitBinsBox = new ListBox();
		for (int i = 0; i < options1.length; i++)
		{
			this.splitBinsBox.addItem(options1[i].toString());
		}

		this.splitChooseBoundariesButton = new Button(
			StatistiekGWT.rb.getString("binsButton"));

		splitBoundariesHR = new HTML(this.hrString);
		splitBoundariesHR.addStyleName(statistiekCss.horizontalrule());

		this.splitMinBoundaryLabel = new Label(
			StatistiekGWT.rb.getString("startvalueLabel"));

		this.splitMinBoundaryField = new TextBox();

		this.splitBinWidthLabel = new Label(
			StatistiekGWT.rb.getString("classwidthLabel"));

		this.splitBinWidthField = new TextBox();

		this.splitBoundariesLabel = new Label(
			StatistiekGWT.rb.getString("binsButton"));

		this.splitBoundariesArea = new TextArea();
//		this.splitBoundariesArea.setSize("100%", "100%");
		//this.splitBoundariesArea.setSize("150px", "150px%");
		this.splitBoundariesArea.setPixelSize(150, 150);
		this.splitBoundariesArea.setEnabled(false);
		this.splitBoundariesArea.addStyleName(statistiekCss.boxsizingborder());
		
		// test syl: even zonder scrollpanel, alleen met textarea
//		this.splitBoundariesAreaScrollPanel = new ScrollPanel(
//			this.splitBoundariesArea);
//		this.splitBoundariesAreaScrollPanel.setSize("150px", "150px");

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
		LayoutPanel allSettingsPanel;
//		HorizontalPanel hp1, hp2, hp3, hp3a, hp4, hp5, hp6, hp7, hp8, hp8a, 
//			hp9, hp10, hp11, hp12, hp13, hp14;
		// Hier geen VerticalPanel; geeft problemen met de gegenereerde HTML
		// zie: http://mechanitis.blogspot.nl/2011/01/gwt-why-verticalpanel-is-evil.html
		LayoutPanel variableSettingsPanel, binsSettingsPanel, displaySettingsPanel, splitSettingsPanel;  

		// Variable settings
		variableSettingsPanel = new LayoutPanel();
		variableSettingsPanel.setTitle(StatistiekGWT.rb.getString("variableLabel")); // tooltip boven panel
		variableSettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		variableSettingsPanel.add(this.varLabel);
		variableSettingsPanel.add(this.varBox);
		variableSettingsPanel.add(this.axisLabel);
		variableSettingsPanel.add(this.axisBox);
		// set position
		variableSettingsPanel.setWidgetLeftWidth(this.varLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		variableSettingsPanel.setWidgetTopHeight(this.varLabel, 0, Style.Unit.PX, 30, Style.Unit.PX);
		variableSettingsPanel.setWidgetLeftWidth(this.varBox, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		variableSettingsPanel.setWidgetTopHeight(this.varBox, 30, Style.Unit.PX, 30, Style.Unit.PX);
		variableSettingsPanel.setWidgetLeftWidth(this.axisLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		variableSettingsPanel.setWidgetTopHeight(this.axisLabel, 60, Style.Unit.PX, 30, Style.Unit.PX);
		variableSettingsPanel.setWidgetLeftWidth(this.axisBox, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		variableSettingsPanel.setWidgetTopHeight(this.axisBox, 90, Style.Unit.PX, 30, Style.Unit.PX);

		// Bins settings
		binsSettingsPanel = new LayoutPanel();
		binsSettingsPanel.setTitle(StatistiekGWT.rb.getString("classDivisionLabel")); // tooltip boven panel
		binsSettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		binsSettingsPanel.add(this.binSettingsLabel);
		binsSettingsPanel.add(this.minBoundaryLabel);
		binsSettingsPanel.add(this.minBoundaryField);

		binsSettingsPanel.add(this.binWidthLabel);
		binsSettingsPanel.add(this.binWidthField);
		
		binsSettingsPanel.add(this.binSettingsHR);

		binsSettingsPanel.add(this.binsLabel);
		binsSettingsPanel.add(this.binsBox);
		binsSettingsPanel.add(this.noObjectsLabel);
		binsSettingsPanel.add(this.minValueLabel);
		binsSettingsPanel.add(this.maxValueLabel);
		// set position
		binsSettingsPanel.setWidgetLeftWidth(this.binSettingsLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		binsSettingsPanel.setWidgetTopHeight(this.binSettingsLabel, 0, Style.Unit.PX, 30, Style.Unit.PX);
		binsSettingsPanel.setWidgetLeftWidth(this.minBoundaryLabel, 0, Style.Unit.PCT, 75, Style.Unit.PCT);
		binsSettingsPanel.setWidgetTopHeight(this.minBoundaryLabel, 30, Style.Unit.PX, 30, Style.Unit.PX);
		binsSettingsPanel.setWidgetLeftWidth(this.minBoundaryField, 75, Style.Unit.PCT, 25, Style.Unit.PCT);
		binsSettingsPanel.setWidgetTopHeight(this.minBoundaryField, 30, Style.Unit.PX, 30, Style.Unit.PX);
		binsSettingsPanel.setWidgetLeftWidth(this.binWidthLabel, 0, Style.Unit.PCT, 75, Style.Unit.PCT);
		binsSettingsPanel.setWidgetTopHeight(this.binWidthLabel, 60, Style.Unit.PX, 30, Style.Unit.PX);
		binsSettingsPanel.setWidgetLeftWidth(this.binWidthField, 75, Style.Unit.PCT, 25, Style.Unit.PCT);
		binsSettingsPanel.setWidgetTopHeight(this.binWidthField, 60, Style.Unit.PX, 30, Style.Unit.PX);
		binsSettingsPanel.setWidgetLeftWidth(this.binSettingsHR, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		binsSettingsPanel.setWidgetTopHeight(this.binSettingsHR, 95, Style.Unit.PX, 5, Style.Unit.PX);
		binsSettingsPanel.setWidgetLeftWidth(this.binsLabel, 0, Style.Unit.PCT, 75, Style.Unit.PCT);
		binsSettingsPanel.setWidgetTopHeight(this.binsLabel, 100, Style.Unit.PX, 30, Style.Unit.PX);
		binsSettingsPanel.setWidgetLeftWidth(this.binsBox, 75, Style.Unit.PCT, 25, Style.Unit.PCT);
		binsSettingsPanel.setWidgetTopHeight(this.binsBox, 100, Style.Unit.PX, 30, Style.Unit.PX);
		binsSettingsPanel.setWidgetLeftWidth(this.noObjectsLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT); // in dit label wordt het aantal later achteraan geplakt
		binsSettingsPanel.setWidgetTopHeight(this.noObjectsLabel, 130, Style.Unit.PX, 30, Style.Unit.PX);
		binsSettingsPanel.setWidgetLeftWidth(this.minValueLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT); // in dit label wordt het aantal later achteraan geplakt
		binsSettingsPanel.setWidgetTopHeight(this.minValueLabel, 160, Style.Unit.PX, 30, Style.Unit.PX);
		binsSettingsPanel.setWidgetLeftWidth(this.maxValueLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT); // in dit label wordt het aantal later achteraan geplakt
		binsSettingsPanel.setWidgetTopHeight(this.maxValueLabel, 190, Style.Unit.PX, 30, Style.Unit.PX);

		// Display settings
		displaySettingsPanel = new LayoutPanel();
		displaySettingsPanel.setTitle(StatistiekGWT.rb.getString("absRelLabel")); // tooltip boven panel
		displaySettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		displaySettingsPanel.add(this.absRelLabel);
		displaySettingsPanel.add(this.amountRadioItem);
		displaySettingsPanel.add(this.percentageRadioItem);
		if (this.model.isFrequencyPolygonMode())
			displaySettingsPanel.add(this.cumulativeBox);
		displaySettingsPanel.add(this.amountLabelHR);
		displaySettingsPanel.add(this.labelBetweenBinsRadioItem);
		displaySettingsPanel.add(this.labelUnderBinRadioItem);
		displaySettingsPanel.add(this.separateRadioItem);
		displaySettingsPanel.add(this.labelSplitHR);
		if (this.model.isFrequencyPolygonMode())
			displaySettingsPanel.add(this.singleViewRadioItem);
		else
		{
			displaySettingsPanel.add(this.aboveEachOtherRadioItem);
			displaySettingsPanel.add(this.nextToEachOtherRadioItem);
		}
		// set position
		int freqPolCorrection = 0;
		displaySettingsPanel.setWidgetLeftWidth(this.absRelLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		displaySettingsPanel.setWidgetTopHeight(this.absRelLabel, 0, Style.Unit.PX, 30, Style.Unit.PX);
		displaySettingsPanel.setWidgetLeftWidth(this.amountRadioItem, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		displaySettingsPanel.setWidgetTopHeight(this.amountRadioItem, 30, Style.Unit.PX, 30, Style.Unit.PX);
		displaySettingsPanel.setWidgetLeftWidth(this.percentageRadioItem, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		displaySettingsPanel.setWidgetTopHeight(this.percentageRadioItem, 60, Style.Unit.PX, 30, Style.Unit.PX);
		if (this.model.isFrequencyPolygonMode())
		{
			displaySettingsPanel.setWidgetLeftWidth(this.cumulativeBox, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
			displaySettingsPanel.setWidgetTopHeight(this.cumulativeBox, 90, Style.Unit.PX, 30, Style.Unit.PX);
			freqPolCorrection = 30;
		}
		displaySettingsPanel.setWidgetLeftWidth(this.amountLabelHR, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		displaySettingsPanel.setWidgetTopHeight(this.amountLabelHR, 95 + freqPolCorrection, Style.Unit.PX, 5, Style.Unit.PX);

		displaySettingsPanel.setWidgetLeftWidth(this.labelBetweenBinsRadioItem, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		displaySettingsPanel.setWidgetTopHeight(this.labelBetweenBinsRadioItem, 100 + freqPolCorrection, Style.Unit.PX, 30, Style.Unit.PX);
		displaySettingsPanel.setWidgetLeftWidth(this.labelUnderBinRadioItem, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		displaySettingsPanel.setWidgetTopHeight(this.labelUnderBinRadioItem, 130 + freqPolCorrection, Style.Unit.PX, 30, Style.Unit.PX);
		displaySettingsPanel.setWidgetLeftWidth(this.labelSplitHR, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		displaySettingsPanel.setWidgetTopHeight(this.labelSplitHR, 165 + freqPolCorrection, Style.Unit.PX, 5, Style.Unit.PX);

		displaySettingsPanel.setWidgetLeftWidth(this.separateRadioItem, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		displaySettingsPanel.setWidgetTopHeight(this.separateRadioItem, 170 + freqPolCorrection, Style.Unit.PX, 30, Style.Unit.PX);
		
		if (this.model.isFrequencyPolygonMode())
		{
			displaySettingsPanel.setWidgetLeftWidth(this.singleViewRadioItem, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
			displaySettingsPanel.setWidgetTopHeight(this.singleViewRadioItem, 200 + freqPolCorrection, Style.Unit.PX, 30, Style.Unit.PX);
		}
		else
		{
			displaySettingsPanel.setWidgetLeftWidth(this.aboveEachOtherRadioItem, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
			displaySettingsPanel.setWidgetTopHeight(this.aboveEachOtherRadioItem, 200 + freqPolCorrection, Style.Unit.PX, 30, Style.Unit.PX);
			displaySettingsPanel.setWidgetLeftWidth(this.nextToEachOtherRadioItem, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
			displaySettingsPanel.setWidgetTopHeight(this.nextToEachOtherRadioItem, 230 + freqPolCorrection, Style.Unit.PX, 30, Style.Unit.PX);
		}

		// splitOptions settings
		splitSettingsPanel = new LayoutPanel();
		splitSettingsPanel.setTitle(StatistiekGWT.rb.getString("splitsLabel")); // tooltip boven panel
		splitSettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		splitSettingsPanel.add(this.splitSettingsLabel);
		splitSettingsPanel.add(this.splitButton);
		splitSettingsPanel.add(this.splitVarLabel);
		splitSettingsPanel.add(this.splitVarBox);
		splitSettingsPanel.add(this.splitBinsLabel);
		splitSettingsPanel.add(this.splitBinsBox);
		
		splitSettingsPanel.add(this.splitBoundariesHR);
		splitSettingsPanel.add(this.splitChooseBoundariesButton);

		splitSettingsPanel.add(this.splitMinBoundaryLabel);
		splitSettingsPanel.add(this.splitMinBoundaryField);

		splitSettingsPanel.add(this.splitBinWidthLabel);
		splitSettingsPanel.add(this.splitBinWidthField);

		splitSettingsPanel.add(this.splitBoundariesLabel);
//		splitSettingsPanel.add(this.splitBoundariesAreaScrollPanel);
		splitSettingsPanel.add(this.splitBoundariesArea);
		splitSettingsPanel.add(this.splitNoObjectsLabel);
		splitSettingsPanel.add(this.splitMinValueLabel);
		splitSettingsPanel.add(this.splitMaxValueLabel);
		// set position
		splitSettingsPanel.setWidgetLeftWidth(this.splitSettingsLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		splitSettingsPanel.setWidgetTopHeight(this.splitSettingsLabel, 0, Style.Unit.PX, 30, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitButton, 20, Style.Unit.PCT, 60, Style.Unit.PCT);
		splitSettingsPanel.setWidgetTopHeight(this.splitButton, 30, Style.Unit.PX, 30, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitVarLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		splitSettingsPanel.setWidgetTopHeight(this.splitVarLabel, 60, Style.Unit.PX, 30, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitVarBox, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		splitSettingsPanel.setWidgetTopHeight(this.splitVarBox, 90, Style.Unit.PX, 30, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitBinsLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		splitSettingsPanel.setWidgetTopHeight(this.splitBinsLabel, 120, Style.Unit.PX, 30, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitBinsBox, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		splitSettingsPanel.setWidgetTopHeight(this.splitBinsBox, 150, Style.Unit.PX, 30, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitBoundariesHR, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		splitSettingsPanel.setWidgetTopHeight(this.splitBoundariesHR, 155, Style.Unit.PX, 5, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitChooseBoundariesButton, 20, Style.Unit.PCT, 60, Style.Unit.PCT);
		splitSettingsPanel.setWidgetTopHeight(this.splitChooseBoundariesButton, 190, Style.Unit.PX, 30, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitMinBoundaryLabel, 0, Style.Unit.PCT, 75, Style.Unit.PCT);
		splitSettingsPanel.setWidgetTopHeight(this.splitMinBoundaryLabel, 220, Style.Unit.PX, 30, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitMinBoundaryField, 75, Style.Unit.PCT, 25, Style.Unit.PCT);
		splitSettingsPanel.setWidgetTopHeight(this.splitMinBoundaryField, 220, Style.Unit.PX, 30, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitBinWidthLabel, 0, Style.Unit.PCT, 75, Style.Unit.PCT);
		splitSettingsPanel.setWidgetTopHeight(this.splitBinWidthLabel, 250, Style.Unit.PX, 30, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitBinWidthField, 75, Style.Unit.PCT, 25, Style.Unit.PCT);
		splitSettingsPanel.setWidgetTopHeight(this.splitBinWidthField, 250, Style.Unit.PX, 30, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitBoundariesLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		splitSettingsPanel.setWidgetTopHeight(this.splitBoundariesLabel, 280, Style.Unit.PX, 30, Style.Unit.PX);
//		splitSettingsPanel.setWidgetLeftWidth(this.splitBoundariesAreaScrollPanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
//		splitSettingsPanel.setWidgetLeftWidth(this.splitBoundariesAreaScrollPanel, 0, Style.Unit.PX, 150, Style.Unit.PX);
//		splitSettingsPanel.setWidgetTopHeight(this.splitBoundariesAreaScrollPanel, 310, Style.Unit.PX, 150, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitBoundariesArea, 0, Style.Unit.PX, 150, Style.Unit.PX);
		splitSettingsPanel.setWidgetTopHeight(this.splitBoundariesArea, 310, Style.Unit.PX, 150, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitNoObjectsLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT); // in dit label wordt het aantal later achteraan geplakt
		splitSettingsPanel.setWidgetTopHeight(this.splitNoObjectsLabel, 460, Style.Unit.PX, 30, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitMinValueLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT); // in dit label wordt het aantal later achteraan geplakt
		splitSettingsPanel.setWidgetTopHeight(this.splitMinValueLabel, 490, Style.Unit.PX, 30, Style.Unit.PX);
		splitSettingsPanel.setWidgetLeftWidth(this.splitMaxValueLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT); // in dit label wordt het aantal later achteraan geplakt
		splitSettingsPanel.setWidgetTopHeight(this.splitMaxValueLabel, 520, Style.Unit.PX, 30, Style.Unit.PX);

		
		// Put settings panels together on allSettingsPanel
		allSettingsPanel = new LayoutPanel();
		allSettingsPanel.add(variableSettingsPanel);
		allSettingsPanel.add(binsSettingsPanel);
		allSettingsPanel.add(displaySettingsPanel);
		allSettingsPanel.add(splitSettingsPanel);
		// set position
		allSettingsPanel.setWidgetLeftWidth(variableSettingsPanel, 0, Style.Unit.PCT, 25, Style.Unit.PCT);
		allSettingsPanel.setWidgetTopHeight(variableSettingsPanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		allSettingsPanel.setWidgetLeftWidth(binsSettingsPanel, 25, Style.Unit.PCT, 25, Style.Unit.PCT);
		allSettingsPanel.setWidgetTopHeight(binsSettingsPanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		allSettingsPanel.setWidgetLeftWidth(displaySettingsPanel, 50, Style.Unit.PCT, 25, Style.Unit.PCT);
		allSettingsPanel.setWidgetTopHeight(displaySettingsPanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		allSettingsPanel.setWidgetLeftWidth(splitSettingsPanel, 75, Style.Unit.PCT, 25, Style.Unit.PCT);
		allSettingsPanel.setWidgetTopHeight(splitSettingsPanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);

		this.basisPanel.setHeight("100%");
		this.basisPanel.setWidth("100%");
		this.basisPanel.add(allSettingsPanel);
		this.basisPanel.add(this.okButton);
		// set position
		this.basisPanel.setWidgetLeftWidth(allSettingsPanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.basisPanel.setWidgetTopHeight(allSettingsPanel, 0, Style.Unit.PX, 570, Style.Unit.PX);
		this.basisPanel.setWidgetLeftWidth(this.okButton, 35, Style.Unit.PCT, 30, Style.Unit.PCT);
		this.basisPanel.setWidgetTopHeight(this.okButton, 570, Style.Unit.PX, 30, Style.Unit.PX);

		this.alles.setPixelSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.alles.add(basisPanel);
		// set position
		this.alles.setWidgetLeftWidth(basisPanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.alles.setWidgetTopHeight(basisPanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);

		init();
	}

	public void resize(Panel p)
	{
		//System.out.println("HistogramUserOptionsPanel.resize(): p = " + p.toString());
		
		// dit gaat niet goed...
//		int w = p.getOffsetWidth();
//		int h = p.getOffsetHeight();
//		this.basisPanel.setPixelSize(w + 10, h);
	}

	public DialogButton getDialogButton()
	{
		return dialogButton;
	}

	public int getVarBoxSelectedIndex()
	{
		return this.varBox.getSelectedIndex();
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
		//System.out.println("HistogramUserOptionsPanel.update()");

		//this.varBox.removeActionListener(this.controller);
		this.removeAllItemsFromListBox(this.varBox);
		ArrayList<String> nameList = this.model.getStatTableModel().getColumnNames();
		for (String varName : nameList)
		{
			this.varBox.addItem(varName);
		}
		if (this.model.columnIndexValid())
		{
			this.varBox.setSelectedIndex(this.model.getColumnIndex());
		}
		else
		{
			// set no item selected
			this.varBox.setSelectedIndex(-1);
		}
		//this.varBox.addActionListener(this.controller);

		//this.splitVarBox.removeActionListener(this);
		this.removeAllItemsFromListBox(this.splitVarBox);
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
			//System.out.println("HistogramUserOptionsPanel.update(): COLUMN INDEX VALID!");
			this.splitVarBox.setSelectedIndex(this.model.getSplitOptions()
				.getColumnSplitIndex() + 1);
		}
		else
		{
			// set no split variable selected
			this.splitVarBox.setSelectedIndex(0);
		}
		//this.splitVarBox.addActionListener(this);

		//this.binsBox.removeActionListener(this.controller);
		this.setSelectedItemInListBox(
			this.binsBox, String.valueOf(this.model.getNoBins()));
		//this.splitBinsBox.removeActionListener(this.controller);
		this.setSelectedItemInListBox(
			this.splitBinsBox, 
			String.valueOf(this.model.getSplitOptions().getBinBoundaries().size() - 1));
		//this.splitBinsBox.addActionListener(this.controller);

		if (this.model.columnIndexValid())
		{
			ArrayList<ColumnType> typeList = this.model.getStatTableModel().getColumnTypes(); 
			ColumnType cType = typeList.get(this.model.getColumnIndex());
			AllowedTypes type = cType.getType();
			if (type.equals(AllowedTypes.DOUBLE)
				|| type.equals(AllowedTypes.INTEGER))
			{
				this.minBoundaryField.setText(this.model.getBinBoundaries().get(0).toString());
				this.binWidthField.setText(StatistiekGWT.getFormattedBinWidth(this.model.getBinBoundaries()));
				this.noObjectsLabel.setText(StatistiekGWT.rb
					.getString("numberLabel")
					+ this.model.getStatTableModel().getRowCount());
				this.minValueLabel.setText(StatistiekGWT.rb.getString("minLabel")
					+ this.model.getStatTableModel().getColumnMin(
						this.model.getColumnIndex()));
				this.maxValueLabel.setText(StatistiekGWT.rb.getString("maxLabel")
					+ this.model.getStatTableModel().getColumnMax(
						this.model.getColumnIndex()));
				this.binSettingsHR.setVisible(true);
				this.splitBoundariesHR.setVisible(true);
				this.labelBetweenBinsRadioItem.setVisible(true);
				this.labelUnderBinRadioItem.setVisible(true);
				this.binsBox.setVisible(true);
				this.binsLabel.setVisible(true);
				setEnumClasses(false);
			}
			else if (type.equals(AllowedTypes.ENUM) || type.equals(AllowedTypes.STRING))
			{
				this.binSettingsHR.setVisible(false);
				this.splitBoundariesHR.setVisible(false);
				this.labelBetweenBinsRadioItem.setVisible(false);
				this.labelUnderBinRadioItem.setVisible(false);
				this.binsBox.setVisible(false);
				this.binsLabel.setVisible(false);
				setEnumClasses(true);
			}
		}

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
    				this.splitMinBoundaryField.setText(this.model.getSplitOptions().getBinBoundaries()
    						.get(0).toString());
    				this.splitBinWidthField.setText(
    					StatistiekGWT.getFormattedBinWidth(this.model.getSplitOptions().getBinBoundaries()));
    				
    				StringBuilder sb = new StringBuilder();
    				for (int i = 0; i < this.model.getSplitOptions()
    					.getBinBoundaries().size() - 1; i++)
    				{
    					sb.append(this.model.getSplitOptions()
    						.getBinBoundaries().get(i).toString());
    					sb.append(" - ");
    					sb.append(this.model.getSplitOptions()
    						.getBinBoundaries().get(i + 1).toString());
    					sb.append("\n");
    				}
    				this.splitBoundariesArea.setText(sb.toString());
    				this.splitNoObjectsLabel.setText(StatistiekGWT.rb
    					.getString("numberLabel")
    					+ this.model.getStatTableModel().getRowCount());
    				this.splitMinValueLabel.setText(StatistiekGWT.rb
    					.getString("minLabel")
    					+ this.model.getStatTableModel().getColumnMin(
    						this.model.getSplitOptions().getColumnSplitIndex()));
    				this.splitMaxValueLabel.setText(StatistiekGWT.rb
    					.getString("maxLabel")
    					+ this.model.getStatTableModel().getColumnMax(
    						this.model.getSplitOptions().getColumnSplitIndex()));
    				this.splitBinsBox.setVisible(true);
    				this.splitBinsLabel.setVisible(true);
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
    				sb.substring(0, sb.length() - 1);
    				this.splitBoundariesArea.setText(sb.toString());
    				this.splitBinsBox.setVisible(false);
    				this.splitBinsLabel.setVisible(false);
    				setSplitEnumClasses(true);
    			}
    		}
		}

		if (this.model.isFrequencyPolygonMode()
			&& this.model.isFrequencyPolygonCumulativeMode() != this
				.isCumulativeBoxSelected())
		{
			this.cumulativeBox.setValue(this.model
				.isFrequencyPolygonCumulativeMode());
		}
		
//		System.out.println("HistogramUserOptionsPanel.update(): this.model.getLabelUnderBin() = "
//			+ this.model.getLabelUnderBin());
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
//		this.setVisibleSplitOptions(split);
		// test syl: na klik op 'Maak splitsing' is splitOptionsVisible = true, terwijl er geen split is
		this.setVisibleSplitOptions(this.splitOptionsVisible);
		
		this.singleViewRadioItem.setValue(this.model.splitInSingleView()
			&& split && this.model.isFrequencyPolygonMode());
		this.separateRadioItem.setValue(!this.model.splitInSingleView()
			&& split);
		// System.out.println("HistogramUserOptionsPanel.update(): this.separateRadioItem.setSelected("
		// + (!this.model.splitInSingleView() && split) +
		// "); !this.model.splitInSingleView()="
		// + !this.model.splitInSingleView() + ", split=" + split);

		this.nextToEachOtherRadioItem.setValue(this.model
			.splitInSingleView() && this.model.isNextToEachOther() && split);
		// System.out.println("HistogramUserOptionsPanel.update(): nextToEachOtherRadioItem.setSelected(splitInSingleView="
		// + this.model.splitInSingleView() + " && isNextToEachOther=" +
		// this.model.isNextToEachOther()
		// + " && split=" + split
		// + ")");

		this.aboveEachOtherRadioItem.setValue(this.model.splitInSingleView()
			&& !this.model.isNextToEachOther() && split && !this.model.isFrequencyPolygonMode());

		this.stackModeBox.setValue(this.model.isFrequencyPolygonStackMode());
		this.stackModeBox.setEnabled(this.model.isFrequencyPolygonMode()
			&& this.model.isFrequencyPolygonCumulativeMode());
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
		if (this.basisPanel != null)
			this.resize(this.basisPanel);

	}

	/**
	 * Set the given type selected in typeBox.
	 * @param type
	 */
	private void setSelectedItemInListBox(ListBox listBox, String string)
	{
		// find the index of string
		int indexToFind = -1;
		for (int i=0; i < listBox.getItemCount(); i++) 
		{
		    if (listBox.getItemText(i).equals(string)) {
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
		this.splitBoundariesHR.setVisible(!b);
		this.minBoundaryLabel.setVisible(!b);
		this.minBoundaryField.setVisible(!b);
		this.binWidthLabel.setVisible(!b);
		this.binWidthField.setVisible(!b);
		this.noObjectsLabel.setVisible(!b);
		this.minValueLabel.setVisible(!b);
		this.maxValueLabel.setVisible(!b);
		this.resize(this.basisPanel);
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
		this.resize(this.basisPanel);
	}

	private void setVisibleBoundaryOptions()
	{
		this.binSettingsHR.setVisible(!this.enumClasses);
		this.minBoundaryLabel.setVisible(!this.enumClasses);
		this.minBoundaryField.setVisible(!this.enumClasses);
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
		//this.splitBoundariesHR.setVisible(b);
		this.splitMinBoundaryLabel.setVisible(b && !this.splitEnumClasses);
		this.splitMinBoundaryField.setVisible(b && !this.splitEnumClasses);
		this.splitBinWidthLabel.setVisible(b && !this.splitEnumClasses);
		this.splitBinWidthField.setVisible(b && !this.splitEnumClasses);
		this.splitBoundariesLabel.setVisible(b);
		this.splitBoundariesArea.setVisible(b);
//		this.splitBoundariesAreaScrollPanel.setVisible(b);
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

	private void setVisibleSplitOptions(boolean b)
	{
		this.splitOptionsVisible = b;
		
		if (this.model.isFrequencyPolygonMode())
		{
			this.singleViewRadioItem.setVisible(b);
		}
		else
		{
			this.nextToEachOtherRadioItem.setVisible(b);
			this.aboveEachOtherRadioItem.setVisible(b);
		}
		this.separateRadioItem.setVisible(b);
		this.splitVarLabel.setVisible(b);
		this.splitVarBox.setVisible(b);
		if (!b)
			this.splitBinsLabel.setVisible(b);
		if (!b)
			this.splitBinsBox.setVisible(b);
		
		// test syl: alleen als splitvar is gekozen (splitVarBox selectedIndex > 0)
		if (this.hasSplit())
		{
			this.splitChooseBoundariesButton.setVisible(true);
			this.splitBoundariesHR.setVisible(true);
		}
		else
		{
			this.splitChooseBoundariesButton.setVisible(false);
			this.splitBoundariesHR.setVisible(false);
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
		//System.out.println("HistogramUserOptionsPanel.clearGUISplitComponents()");
		this.splitVarBox.setSelectedIndex(0);
		this.splitBinWidthField.setText("");
		this.splitMinBoundaryField.setText("");
		this.splitBoundariesArea.setText("");
		this.splitNoObjectsLabel.setText("");
		this.splitMinValueLabel.setText("");
		this.splitMaxValueLabel.setText("");
	}

	/**
	 * Subscribe for events
	 */
//	public HandlerRegistration addAddColumnEventHandler(AddColumnEventHandler handler)
//	{
//		return this.eventBus.addHandler(AddColumnEvent.TYPE, handler);
//	}
	
	@Override
	public void fireEvent(GwtEvent<?> e)
	{
		this.eventBus.fireEvent(e);
	}

	/**
	 * A touchhandler for HistogramUserOptionsPanel
	 */
	class HistogramUOPTouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{

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
		
	} // class HistogramUOPTouchHandler
	
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
			else if ((e.getSource() == labelBetweenBinsRadioItem) ||
				e.getSource() == labelUnderBinRadioItem)
			{
				model.setLabelUnderBin(view.labelUnderBinItemSelected());
				this.update();
			}
			else if (e.getSource() == minBoundaryField)
			{
				controller.updateBoundariesFromBinSettings();
				this.update();
			}
			else if (e.getSource() == binWidthField)
			{
				controller.updateBoundariesFromBinSettings();
				this.update();
			}
			else if (e.getSource() == axisBox)
			{
				model.setVerticalBars(view.xAxisSelected());
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
			else if (e.getSource() == splitBinsBox)
			{
				ArrayList<ColumnType> list = model.getStatTableModel().getColumnTypes();
				controller.setSplitType(list.get(model.getSplitOptions().getColumnSplitIndex())
					.getType());
				this.update();
			}
			else if (e.getSource() == splitMinBoundaryField)
			{
				controller.updateSplitBoundariesFromBinSettings();
				this.update();
			}
			else if (e.getSource() == splitBinWidthField)
			{
				controller.updateSplitBoundariesFromBinSettings();
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

				resize(basisPanel);
				this.update();
			}
			else if (e.getSource() == splitButton)
			{
				if (splitOptionsVisible)
				{
					// verwijder splitsing...
					model.setColumnSplitIndex(-1);
					setVisibleSplitOptions(false);
					HistogramUserOptionsPanel.this.clearGUISplitComponents();
					this.update();
				}
				else
				{
					setVisibleSplitOptions(true);
					this.updateUserOptionsPanel();
				}
				
				resize(basisPanel);
			}
			else if (e.getSource() == okButton)
			{
				setVisibleBoundaryOptions();
				setVisibleSplitBoundaryOptions(false);
				if (splitVarBox.getSelectedIndex() == 0)
					setVisibleSplitOptions(false);
				dialogButton.closeDialog();
			}
			else if (e.getSource() == splitVarBox)
			{
				//System.out.println("HistogramUserOptionsPanel.HistogramUOPClickHandler.onClick(): splitVarBox, SplitColumnUpdate!");
				if (view.getSplitVarBoxSelectedIndex() - 1 
						!= model.getSplitOptions().getColumnSplitIndex())
				{
					if (view.getSplitVarBoxSelectedIndex() != -1)
					{
						model.setColumnSplitIndex(
							view.getSplitVarBoxSelectedIndex() - 1);
					}
					
					model.setSplitOptions(model.getSplitOptions());
					if (view.getSplitVarBoxSelectedIndex() > 0)
					{
						ArrayList<ColumnType> list = model.getStatTableModel().getColumnTypes();
						controller.setSplitType(
							list.get(model.getSplitOptions().getColumnSplitIndex())
							.getType());
					}
				}

				this.update();
			}
			else if (e.getSource() == dialogButton)
			{
				init();
			}
			else
			{
				//System.out.println("HistogramUserOptionsPanel.HistogramUOPClickHandler.actionPerformed(): Unknown action source! " + e);
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
		 * Update the view.
		 */
		private void updateView()
		{
			// update view
			HistogramUserOptionsPanel.this.view.update();
		}

		/**
		 * Update the view and the user options panel.
		 */
		private void update()
		{
			// update view
			this.updateView();
			// update the user options panel
			this.updateUserOptionsPanel();
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
			}
			else if (e.getSource() == binWidthField)
			{
				// update column index bin settings
				controller.updateBoundariesFromBinSettings();
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
			// update the rest of the user options panel
			HistogramUserOptionsPanel.this.update();
		}
	} // class HistogramUOPBlurHandler
	
	class HistogramUOPChangeHandler implements ChangeHandler
	{
		@Override
		public void onChange(ChangeEvent e)
		{
			if (e.getSource() == varBox)
			{
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

			// update view
			HistogramUserOptionsPanel.this.view.update();
			// update the rest of the user options panel
			HistogramUserOptionsPanel.this.update();
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
			}
			else if (e.getSource() == binWidthField)
			{
				// update column index bin settings
				controller.updateBoundariesFromBinSettings();
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
			// update the rest of the user options panel
			HistogramUserOptionsPanel.this.update();
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
				}
				else if (e.getSource() == binWidthField)
				{
					// update column index bin settings
					controller.updateBoundariesFromBinSettings();
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
				// update the rest of the user options panel
				HistogramUserOptionsPanel.this.update();
			}
		}
	} // class HistogramUOPKeyDownHandler
}
