package fi.statistiekgwt.client.frequencytable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

import fi.statistiekgwt.client.DialogButton;
import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;
import fi.statistiekgwt.client.StatistiekUtils;
import fi.statistiekgwt.client.histogram.HistogramModel.FrequencyTuple;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

public class FrequencyTableUserOptionsPanel extends FlowPanel
{

	private FrequencyTableView view;
	private FrequencyTableController controller;
	private FrequencyTableModel model;

	private DialogButton dialogButton;

	/**
	 * Panel 'basisPanel' wordt aan DialogButton meegegeven als content.
	 */
	private FlowPanel basisPanel;

	private FrequencyTableUOPClickHandler clickHandler;
	private FrequencyTableUOPBlurHandler blurHandler;
	private FrequencyTableUOPChangeHandler changeHandler;
	private FrequencyTableUOPValueChangeHandler valueChangeHandler;
	private FrequencyTableUOPKeyDownHandler keyDownHandler;

	// variable settings
	private Label varLabel;
	private ListBox columnIndexBox;
//	private Label noBinsLabel;
//	private TextBox noBinsField;
	private Button chooseBinsButton;
	
	// bin settings
	private Label binSettingsLabel;
//	private Label binsLabel;
//	private ListBox binsBox;
//	private Button chooseBoundariesButton;
	private Label minBoundaryLabel;
	private TextBox minBoundaryField;
	private Label binWidthLabel;
	private TextBox binWidthField;
	private Label boundariesLabel;
	private TextArea boundariesArea;
	private Label noObjectsLabel;
	private Label minValueLabel;
	private Label maxValueLabel;

	// display settings
	private Label absRelLabel;
	private CheckBox showPercBox;
	private CheckBox showCumulativeBox;

	// split settings
	private Label splitSettingsLabel;
	private Button splitButton;
	private Label splitVarLabel;
	private ListBox splitVarBox;
	private Label splitBinsLabel;
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

	private boolean splitBoundariesVisible;
	private boolean splitOptionsVisible;
	private boolean enumClasses;
	private boolean splitEnumClasses;
	private boolean boundariesVisible;

	private Button okButton;

	private ArrayList<Double> binBoundaries;

	/**
	 * The event bus to send change events to event handlers associated 
	 * with the views using StatTableModel.
	 */
	EventBus eventBus;

	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;

	/**
	 * hr element, used to create a separator.
	 */
	private static final String hrString = new String("<hr  style=\"width:100%;\" />");

	public FrequencyTableUserOptionsPanel(FrequencyTableView view,
		FrequencyTableController controller, FrequencyTableModel model)
	{
		this.view = view;
		this.controller = controller;
		this.model = model;

		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		this.clickHandler = new FrequencyTableUOPClickHandler();
		this.blurHandler = new FrequencyTableUOPBlurHandler();
		this.changeHandler = new FrequencyTableUOPChangeHandler();
		this.valueChangeHandler = new FrequencyTableUOPValueChangeHandler();
		this.keyDownHandler = new FrequencyTableUOPKeyDownHandler();

		this.createGuiComponents();
		this.layoutGuiComponents();
		this.addHandlers();

		this.dialogButton = new DialogButton(
			StatistiekGWT.rb.settingsButton(), this.basisPanel);

		this.eventBus = StatistiekUtils.EVENT_BUS;
	}

	private void addHandlers()
	{
		// click handlers
		this.showPercBox.addClickHandler(this.clickHandler);
		this.showCumulativeBox.addClickHandler(this.clickHandler);
		this.splitButton.addClickHandler(this.clickHandler);
		this.splitChooseBoundariesButton.addClickHandler(this.clickHandler);
		this.okButton.addClickHandler(this.clickHandler);
		
		// blur handlers
		this.minBoundaryField.addBlurHandler(this.blurHandler);
		this.binWidthField.addBlurHandler(this.blurHandler);
		//this.noBinsField.addBlurHandler(this.blurHandler);
		this.splitMinBoundaryField.addBlurHandler(this.blurHandler);
		this.splitBinWidthField.addBlurHandler(this.blurHandler);
		
		// key down handlers
		this.minBoundaryField.addKeyDownHandler(this.keyDownHandler);
		this.binWidthField.addKeyDownHandler(this.keyDownHandler);
		//this.noBinsField.addKeyDownHandler(this.keyDownHandler);
		this.splitMinBoundaryField.addKeyDownHandler(this.keyDownHandler);
		this.splitBinWidthField.addKeyDownHandler(this.keyDownHandler);
		
		// change handlers
		this.columnIndexBox.addChangeHandler(this.changeHandler);
		this.splitVarBox.addChangeHandler(this.changeHandler);
		this.splitBinsBox.addChangeHandler(this.changeHandler);
		
		// value change handlers
		this.minBoundaryField.addValueChangeHandler(this.valueChangeHandler);
		this.binWidthField.addValueChangeHandler(this.valueChangeHandler);
		//this.noBinsField.addValueChangeHandler(this.valueChangeHandler);
		this.splitMinBoundaryField.addValueChangeHandler(this.valueChangeHandler);
		this.splitBinWidthField.addValueChangeHandler(this.valueChangeHandler);
	}

	private void createGuiComponents()
	{
		this.basisPanel = new FlowPanel();

		// var settings
		this.varLabel = new Label(StatistiekGWT.rb.variableLabel());
		this.varLabel.addStyleName(statistiekCss.titlelabel());

		this.columnIndexBox = new ListBox();
		this.columnIndexBox.setWidth("100px");//PixelSize(100, 25);

		// bin settings
		this.binSettingsLabel = new Label(StatistiekGWT.rb.classDivisionLabel());
		this.binSettingsLabel.addStyleName(statistiekCss.titlelabel());
		
		this.minBoundaryLabel = new Label(
			StatistiekGWT.rb.startvalueLabel());

		this.minBoundaryField = new TextBox();

		this.binWidthLabel = new Label(
			StatistiekGWT.rb.classwidthLabel());
		this.binWidthLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.binWidthField = new TextBox();

		this.boundariesLabel = new Label(StatistiekGWT.rb.binsButton());

		this.boundariesArea = new TextArea();
		this.boundariesArea.setEnabled(false);
		this.boundariesArea.addStyleName(statistiekCss.boxsizingborder());

		this.noObjectsLabel = new Label("noObjectsLabel");
		this.noObjectsLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.minValueLabel = new Label("minValueLabel");

		this.maxValueLabel = new Label("maxValueLabel");

		// display settings
		this.absRelLabel = new Label(StatistiekGWT.rb.absRelLabel());
		this.absRelLabel.addStyleName(statistiekCss.titlelabel());

		this.showPercBox = new CheckBox(
			StatistiekGWT.rb.showpercentageCheckbox(), true);
		this.showPercBox.addStyleName(statistiekCss.checkBox());

		this.showCumulativeBox = new CheckBox(
			StatistiekGWT.rb.showcumulativefrequencyCheckbox(), true);

		// split settings
		this.splitSettingsLabel = new Label(StatistiekGWT.rb.splitsLabel());
		this.splitSettingsLabel.addStyleName(statistiekCss.titlelabel());

		this.splitButton = new Button(
			StatistiekGWT.rb.splitoptionsButton());
		this.splitButton.addStyleName(statistiekCss.button());

		this.splitVarLabel = new Label(
			StatistiekGWT.rb.splitvariableLabel());
		this.splitVarLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.splitVarBox = new ListBox();

		this.splitBinsLabel = new Label(
			StatistiekGWT.rb.noClassesLabel());
		this.splitBinsLabel.addStyleName(statistiekCss.spaceTopLabel());

		Integer[] options1 = new Integer[50];
		for (int i = 0; i < 50; i++)
		{
			options1[i] = i + 1;
		}

		this.splitBinsBox = new ListBox();
		for (int i = 0; i < options1.length; i++)
		{
			this.splitBinsBox.addItem(options1[i].toString());
		}

		this.splitChooseBoundariesButton = new Button(
			StatistiekGWT.rb.binsButton());

		this.separatorSplitBoundaries = new HTML(this.hrString);
		this.separatorSplitBoundaries.addStyleName(statistiekCss.horizontalrule());

		this.splitMinBoundaryLabel = new Label(
			StatistiekGWT.rb.startvalueLabel());
		this.splitMinBoundaryLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.splitMinBoundaryField = new TextBox();

		this.splitBinWidthLabel = new Label(
			StatistiekGWT.rb.classwidthLabel());
		this.splitBinWidthLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.splitBinWidthField = new TextBox();

		this.splitBoundariesLabel = new Label(
			StatistiekGWT.rb.binsButton());
		this.splitBoundariesLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.splitBoundariesArea = new TextArea();
		this.splitBoundariesArea.setEnabled(false);
		this.splitBoundariesArea.addStyleName(statistiekCss.boxsizingborder());

		this.splitNoObjectsLabel = new Label("");

		this.splitMinValueLabel = new Label("");

		this.splitMaxValueLabel = new Label("");

		// ok-cancel
		this.okButton = new Button(StatistiekGWT.rb.oKButtonText());
	}

	private void layoutGuiComponents()
	{
		HorizontalPanel allSettingsPanel;
		FlowPanel variableSettingsPanel, binsSettingsPanel, displaySettingsPanel, splitSettingsPanel;

		// Variable settings
		variableSettingsPanel = new FlowPanel();//new LayoutPanel();
		variableSettingsPanel.setTitle(StatistiekGWT.rb.variableLabel()); // tooltip boven panel
		variableSettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		variableSettingsPanel.add(this.varLabel);
		variableSettingsPanel.add(this.columnIndexBox);
		
		// Bins settings
		binsSettingsPanel = new FlowPanel();
		binsSettingsPanel.setTitle(StatistiekGWT.rb.classDivisionLabel()); // tooltip boven panel
		binsSettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		binsSettingsPanel.add(this.binSettingsLabel);
		binsSettingsPanel.add(this.minBoundaryLabel);
		binsSettingsPanel.add(this.minBoundaryField);

		binsSettingsPanel.add(this.binWidthLabel);
		binsSettingsPanel.add(this.binWidthField);
		
//		binsSettingsPanel.add(this.binsLabel);
//		binsSettingsPanel.add(this.binsBox);
		
		binsSettingsPanel.add(this.boundariesLabel);
		binsSettingsPanel.add(this.boundariesArea);
		
		binsSettingsPanel.add(this.noObjectsLabel);
		binsSettingsPanel.add(this.minValueLabel);
		binsSettingsPanel.add(this.maxValueLabel);
		
		// Display settings
		displaySettingsPanel = new FlowPanel();
		displaySettingsPanel.setTitle(StatistiekGWT.rb.absRelLabel()); // tooltip boven panel
		displaySettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		displaySettingsPanel.add(this.absRelLabel);
		displaySettingsPanel.add(this.showPercBox);
		displaySettingsPanel.add(this.showCumulativeBox);
		
		// splitOptions settings
		splitSettingsPanel = new FlowPanel();
		splitSettingsPanel.setTitle(StatistiekGWT.rb.splitsLabel()); // tooltip boven panel
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
		allSettingsPanel = new HorizontalPanel();
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

	public void init()
	{
		this.setVisibleBoundaryOptions();
		this.setVisibleSplitBoundaryOptions(false);
	}

	private void setVisibleBoundaryOptions()
	{
		this.minBoundaryLabel.setVisible(!this.enumClasses);
		this.minBoundaryField.setVisible(!this.enumClasses);
		this.binWidthLabel.setVisible(!this.enumClasses);
		this.binWidthField.setVisible(!this.enumClasses);
		this.noObjectsLabel.setVisible(!this.enumClasses);
		this.minValueLabel.setVisible(!this.enumClasses);
		this.maxValueLabel.setVisible(!this.enumClasses);
	}

	public DialogButton getDialogButton()
	{
		return dialogButton;
	}

	public int getVarXBoxSelectedIndex()
	{
		return this.columnIndexBox.getSelectedIndex();
	}

	public void setModel(FrequencyTableModel model)
	{
		this.model = model;
	}

	public void update()
	{
//		System.out.println("FrequencyTableUserOptionsPanel.update(): this.model.columnIndexValid() = "
//			+ this.model.columnIndexValid() + ", this.model.getSplitOptions().getColumnSplitIndex() = "
//			+ this.model.getSplitOptions().getColumnSplitIndex());
		
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
		
		if (this.model.columnIndexValid())
		{
			ColumnType cType = this.model.getStatTableModel().getColumnTypes()
				.get(this.model.getColumnIndex());
			AllowedTypes type = cType.getType();
			if (type.equals(AllowedTypes.DOUBLE)
				|| type.equals(AllowedTypes.INTEGER))
			{
				this.minBoundaryField.setText(
					StatistiekGWT.getStringValue(this.model.getBinBoundaries().get(0)));
				// set the bin width based on the bin boundaries
				this.binWidthField.setText(StatistiekGWT.getFormattedBinWidth(this.model.getBinBoundaries()));
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < this.model.getNoBins(); i++)
				{
					sb.append(StatistiekGWT.getStringValue(this.model.getBinBoundaries().get(i)));
					sb.append(" -< ");
					sb.append(StatistiekGWT.getStringValue(this.model.getBinBoundaries().get(i + 1)));
					sb.append("\n");
				}

				this.boundariesArea.setText(sb.toString());
				this.noObjectsLabel.setText(
					StatistiekGWT.rb.numberLabel()
					+ this.model.getStatTableModel().getRowCount());
				String minValue = StatistiekGWT.getStringValue(
					this.model.getStatTableModel().getColumnMin(this.model.getColumnIndex()));
				this.minValueLabel.setText(StatistiekGWT.rb.minLabel()
					+ minValue);
				String maxValue = StatistiekGWT.getStringValue(
					this.model.getStatTableModel().getColumnMax(this.model.getColumnIndex()));
				this.maxValueLabel.setText(StatistiekGWT.rb.maxLabel()
					+ maxValue);
				setEnumClasses(false);
			}
			else if (type.equals(AllowedTypes.ENUM))
			{
				StringBuilder sb = new StringBuilder();
				java.util.List<String> list = Arrays.asList(cType.getEnumOptions());
				
				for (String s : list)
				{
					if (!s.equals("*"))
					{
						sb.append(s);
						sb.append("\n");
					}
				}
				
				if (sb.length() > 0)
				{
					sb.substring(0, sb.length() - 2);// -2 voor /n en wildcard
				}
				this.boundariesArea.setText(sb.toString());
				setEnumClasses(true);
			}
			else if (type.equals(AllowedTypes.STRING))
			{
				StringBuilder sb = new StringBuilder();
				java.util.List<String> list = new ArrayList<String>();
					
				FrequencyTuple[] freqTuple = this.model.enumClassFrequency()[0];
				for (int i = 0; i < freqTuple.length; i++)
				{
					list.add(freqTuple[i].label);
				}

				// Use collator to sort for example '�' correctly
//				Collator collator = Collator.getInstance(Locale.getDefault());
				Collections.sort(list);
				
				for (String s : list)
				{
					sb.append(s);
					sb.append("\n");
				}

				// sb heeft soms lengte 0
				String s = "";
				if (sb.length() > 0)
				{
					s = sb.substring(0, sb.length() - 1);
				}
				this.boundariesArea.setText(s);
				this.setEnumClasses(true);
			}
		}
		
		this.showPercBox.setValue(this.model.isShowPercentage());
		this.showCumulativeBox.setValue(this.model.isShowCumulative());
		
		StatistiekUtils.removeAllItemsFromListBox(this.splitVarBox);
		this.splitVarBox.addItem(StatistiekGWT.rb.chooseItem());
		for (int column = 0; column < this.model.getStatTableModel()
			.getColumnCount(); column++)
		{
			splitVarBox.addItem(this.model.getStatTableModel()
				.getColumnName(column));
		}
		
		// check of columnindex valid
		if (this.model.columnIndexValid())
		{
			//System.out.println("FrequencyTableUserOptionsPanel.update(): COLUMN INDEX VALID!");
			this.splitVarBox.setSelectedIndex(this.model.getSplitOptions()
				.getColumnSplitIndex() + 1);
		}
		else
		{
			// set no split variable selected
			this.splitVarBox.setSelectedIndex(0);
		}
	
		this.setSelectedItemInListBox(
			this.splitBinsBox, 
			String.valueOf(this.model.getSplitOptions().getBinBoundaries().size() - 1));
		
		if (this.model.columnIndexValid())
		{
    		if (this.model.getSplitOptions().getColumnSplitIndex() > -1)
    		{
    			ColumnType cSplitType = this.model.getStatTableModel().getColumnTypes()
    				.get(this.model.getSplitOptions().getColumnSplitIndex());
    			
    			AllowedTypes splitType = cSplitType.getType();
    			if (splitType.equals(AllowedTypes.DOUBLE)
    				|| splitType.equals(AllowedTypes.INTEGER))
    			{
    				this.splitMinBoundaryField.setText(
    					StatistiekGWT.getStringValue(this.model.getSplitOptions().getBinBoundaries().get(0)));
    				// set the split bin width based on the split bin boundaries
    				this.splitBinWidthField.setText(StatistiekGWT.getFormattedBinWidth(this.model.getSplitOptions().getBinBoundaries()));
    				StringBuilder sb = new StringBuilder();
    				for (int i = 0; i < this.model.getSplitOptions()
    					.getBinBoundaries().size() - 1; i++)
    				{
    					sb.append(
    						StatistiekGWT.getStringValue(this.model.getSplitOptions().getBinBoundaries().get(i)));
    					sb.append(" -< ");
    					sb.append(
    						StatistiekGWT.getStringValue(this.model.getSplitOptions().getBinBoundaries().get(i + 1)));
    					sb.append("\n");
    				}
    				this.splitBoundariesArea.setText(sb.toString());
    				this.splitNoObjectsLabel.setText(StatistiekGWT.rb.numberLabel()
    					+ this.model.getStatTableModel().getRowCount());
    				String splitMinValue = StatistiekGWT.getStringValue(
    					this.model.getStatTableModel().getColumnMin(this.model.getSplitOptions().getColumnSplitIndex()));
    				this.splitMinValueLabel.setText(StatistiekGWT.rb.minLabel() + splitMinValue);
    				String splitMaxValue = StatistiekGWT.getStringValue(
    					this.model.getStatTableModel().getColumnMax(this.model.getSplitOptions().getColumnSplitIndex()));
    				this.splitMaxValueLabel.setText(StatistiekGWT.rb.maxLabel()
    					+ splitMaxValue);
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
		
		boolean split = this.model.getSplitOptions().getColumnSplitIndex() > -1;
		//this.setVisibleSplitOptions(split);
		this.setVisibleSplitOptions(this.splitOptionsVisible);
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

	public double getBinWidth()
	{
		String s = this.binWidthField.getText();
		s = s.replace(',', '.');
		return Double.parseDouble(s);
	}

	public void setBinWidth(double d)
	{
		this.binWidthField.setText(String.valueOf(d));
	}
	
	/**
	 *Set the bin width based on the model's bin boundaries. 
	 */
	public void setBinWidth()
	{
		this.binWidthField.setText(StatistiekGWT.getFormattedBinWidth(this.model.getBinBoundaries()));
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
	
	private void setEnumClasses(boolean b)
	{
		enumClasses = b;
		
		minBoundaryLabel.setVisible(!b);
		minBoundaryField.setVisible(!b);
		binWidthLabel.setVisible(!b);
		binWidthField.setVisible(!b);
		noObjectsLabel.setVisible(!b);
		minValueLabel.setVisible(!b);
		maxValueLabel.setVisible(!b);
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

	public boolean isShowPercBoxSelected()
	{
		return (this.showPercBox != null && this.showPercBox.getValue());
	}

	public boolean isShowCumulativeBoxSelected()
	{
		return (this.showCumulativeBox != null && this.showCumulativeBox.getValue());
	}
	
	void setVisibleSplitOptions(boolean b)
	{
		this.splitOptionsVisible = b;
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
			this.splitButton.setText(StatistiekGWT.rb.splitoptionsButton());
			this.setVisibleSplitBoundaryOptions(false);
		}
		else
		{
			this.splitButton.setText(StatistiekGWT.rb.removeSplitoptionsButton());
		}
	}
	
	private void setVisibleSplitBoundaryOptions(boolean b)
	{
		this.splitBoundariesVisible = b;
		this.separatorSplitBoundaries.setVisible(b);
		this.splitMinBoundaryLabel.setVisible(b && !this.splitEnumClasses);
		this.splitMinBoundaryField.setVisible(b && !this.splitEnumClasses);
		this.splitBinWidthLabel.setVisible(b && !this.splitEnumClasses);
		this.splitBinWidthField.setVisible(b && !this.splitEnumClasses);
		this.splitBoundariesLabel.setVisible(b);
		this.splitBoundariesArea.setVisible(b);
		this.splitNoObjectsLabel.setVisible(b && !this.splitEnumClasses);
		this.splitMinValueLabel.setVisible(b && !this.splitEnumClasses);
		this.splitMaxValueLabel.setVisible(b && !this.splitEnumClasses);
		if (!b)
		{
			this.splitChooseBoundariesButton.setText(
				StatistiekGWT.rb.binsButton());
		}
		else
		{
			this.splitChooseBoundariesButton.setText(
				StatistiekGWT.rb.hideButtonLabel());
		}
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

	public int getSplitVarBoxSelectedIndex()
	{
		return this.splitVarBox.getSelectedIndex();
	}

	public int getSplitBinsBoxSelectedInt()
	{
		int selectedIndex = this.splitBinsBox.getSelectedIndex();
		String itemText = this.splitBinsBox.getItemText(selectedIndex);
		return Integer.parseInt(itemText);
	}

	private boolean hasSplit()
	{
		boolean split = this.model.getSplitOptions().getColumnSplitIndex() > -1;

		return split;
	}

	

	/**
	 * A clickhandler for FrequencyTableUserOptionsPanel
	 */
	class FrequencyTableUOPClickHandler implements ClickHandler
	{

		@Override
		public void onClick(ClickEvent e)
		{
			FrequencyTableModel model = FrequencyTableUserOptionsPanel.this.model;
			FrequencyTableView view = FrequencyTableUserOptionsPanel.this.view;
			FrequencyTableController controller = FrequencyTableUserOptionsPanel.this.controller;
			
			if (e.getSource() == showCumulativeBox)
			{
				model.setShowCumulative(view.isShowCumulativeBoxSelected());
				this.update();
			}
			else if (e.getSource() == showPercBox)
			{
				model.setShowPercentage(view.isShowPercBoxSelected());
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
					clearGUISplitComponents();
					this.update();
				}
				else
				{
					setVisibleSplitOptions(true);
					this.updateUserOptionsPanel();
				}
			}
			else if (e.getSource() == okButton)
			{
				if (splitVarBox.getSelectedIndex() == 0)
				{
					setVisibleSplitOptions(false);
				}
				
				dialogButton.closeDialog();
			}
			else if (e.getSource() == dialogButton)
			{
				init(); // gebeurt dit ooit?
			}
		}
		
		/**
		 * Update the user options panel.
		 */
		private void updateUserOptionsPanel()
		{
			// update the user options panel
			FrequencyTableUserOptionsPanel.this.update();
		}

		/**
		 * Update the view and the user options panel.
		 */
		private void update()
		{
			// update view and user options panel
			FrequencyTableUserOptionsPanel.this.view.update();
		}
	} // class FrequencyTableUOPClickHandler

	/**
	 * A blurhandler for FrequencyTableUserOptionsPanel
	 */
	class FrequencyTableUOPBlurHandler implements BlurHandler
	{
		@Override
		public void onBlur(BlurEvent e)
		{
			FrequencyTableController controller = FrequencyTableUserOptionsPanel.this.controller;

			try
			{
				if (e.getSource() == minBoundaryField)
				{
					processMinBoundaryChanged();
				}
				else if (e.getSource() == binWidthField)
				{
					processBinWidthChanged();
				}
				else if (e.getSource() == splitMinBoundaryField)
				{
					processSplitMinBoundaryChanged();
				}
				else if (e.getSource() == splitBinWidthField)
				{
					processSplitBinWidthChanged();
				}
			}
			catch (NumberFormatException nfe)
			{
				// invalid value, do nothing
			}

			// update view
			FrequencyTableUserOptionsPanel.this.view.update();
		}
	} // class FrequencyTableUOPBlurHandler
	
	class FrequencyTableUOPChangeHandler implements ChangeHandler
	{
		@Override
		public void onChange(ChangeEvent e)
		{
			if (e.getSource() == columnIndexBox)
			{
				model.initNoBins(5);
				model.setColumnIndex(getVarXBoxSelectedIndex());
			}
			else if (e.getSource() == splitBinsBox)
			{
				controller.setSplitType(model.getStatTableModel().getColumnTypes()
					.get(model.getSplitOptions().getColumnSplitIndex())
					.getType());
			}
			else if (e.getSource() == splitVarBox)
			{
				if (view.getSplitVarBoxSelectedIndex() - 1 != 
					model.getSplitOptions().getColumnSplitIndex())
				{
					if (view.getSplitVarBoxSelectedIndex() != -1)
					{
						model.setColumnSplitIndex(view.getSplitVarBoxSelectedIndex() - 1);
					}
					model.setSplitOptions(model.getSplitOptions());
					ArrayList<ColumnType> list = model.getStatTableModel().getColumnTypes();
					if (view.getSplitVarBoxSelectedIndex() > 0)
					{
						controller.setSplitType(list.get(
							model.getSplitOptions().getColumnSplitIndex()).getType());
					}
				}
			}

			// update view (and uop)
			view.update();
		}
	} // class FrequencyTableUOPChangeHandler

	class FrequencyTableUOPValueChangeHandler implements ValueChangeHandler<String>
	{
		@Override
		public void onValueChange(ValueChangeEvent<String> e)
		{
			try
			{
				if (e.getSource() == minBoundaryField)
				{
					processMinBoundaryChanged();
				}
				else if (e.getSource() == binWidthField)
				{
					processBinWidthChanged();
				}
				else if (e.getSource() == splitMinBoundaryField)
				{
					processSplitMinBoundaryChanged();
				}
				else if (e.getSource() == splitBinWidthField)
				{
					processSplitBinWidthChanged();
				}
			}
			catch (NumberFormatException nfe)
			{
				// invalid value, do nothing
			}

			// update view
			FrequencyTableUserOptionsPanel.this.view.update();
		}
	} // class FrequencyTableUOPValueChangeHandler

	class FrequencyTableUOPKeyDownHandler implements KeyDownHandler
	{
		@Override
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				FrequencyTableController controller = FrequencyTableUserOptionsPanel.this.controller;

				try
				{
					if (e.getSource() == minBoundaryField)
					{
						processMinBoundaryChanged();
					}
					else if (e.getSource() == binWidthField)
					{
						processBinWidthChanged();
					}
					else if (e.getSource() == splitMinBoundaryField)
					{
						processSplitMinBoundaryChanged();
					}
					else if (e.getSource() == splitBinWidthField)
					{
						processSplitBinWidthChanged();
					}
				}
				catch (NumberFormatException nfe)
				{
					// invalid value, do nothing
				}
				
				// update view
				FrequencyTableUserOptionsPanel.this.view.update();
			}
		}
	} // class FrequencyTableUOPKeyDownHandler

	private void processMinBoundaryChanged()
	{
		double minBoundary = view.getUserOptionsPanel().getMinBoundary(); // the user entered value
		double minData = this.model.getStatTableModel().getColumnMin(this.model.getColumnIndex());
		
		if (minBoundary <= minData)
		{
			// update bin settings
			controller.updateBoundariesFromBinSettings();
		}
		else
		{
			// reset to latest value
			double resetMin;
			if (model.getBinBoundaries() != null && model.getBinBoundaries().size() > 0)
			{
				resetMin = model.getBinBoundaries().get(0);
			}
			else
			{
				resetMin = minData;
			}
			
			view.getUserOptionsPanel().setMinBoundary(resetMin);
		}
	}

	public void processBinWidthChanged()
	{
		// update bin settings
		controller.updateBoundariesFromBinSettings();
	}

	public void processSplitBinWidthChanged()
	{
		// update split index bin settings
		controller.updateSplitBoundariesFromBinSettings();
	}

	private void processSplitMinBoundaryChanged()
	{
		double splitMinBoundary = view.getUserOptionsPanel().getSplitMinBoundary(); // the user entered value
		double splitMinData = this.model.getStatTableModel().getColumnMin(this.model.getSplitOptions().getColumnSplitIndex());
		
		if (splitMinBoundary <= splitMinData)
		{
			// update split index bin settings
			controller.updateSplitBoundariesFromBinSettings();
		}
		else
		{
			// reset to latest value
			double resetSplitMin;
			if (model.getSplitOptions().getBinBoundaries() != null && model.getSplitOptions().getBinBoundaries().size() > 0)
			{
				resetSplitMin = model.getSplitOptions().getBinBoundaries().get(0);
			}
			else
			{
				resetSplitMin = splitMinData;
			}
			
			view.getUserOptionsPanel().setSplitMinBoundary(resetSplitMin);
		}
	}

}
