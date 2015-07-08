package fi.statistiekgwt.client.dotplot;

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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

import fi.statistiekgwt.client.ColorUtils;
import fi.statistiekgwt.client.ColorPreviewer;
import fi.statistiekgwt.client.DialogButton;
import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;
import fi.statistiekgwt.client.StatistiekUtils;
import fi.statistiekgwt.client.colorpicker.ColorPickerDialog;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

public class DotplotUserOptionsPanel extends FlowPanel
{

	private DotplotView view;
	private DotplotController controller;
	private DotplotModel model;

	private DialogButton dialogButton;

	/**
	 * Panel 'basisPanel' wordt aan DialogButton meegegeven als content.
	 */
	private FlowPanel basisPanel;
	
	private DotplotUOPClickHandler clickHandler;
	private DotplotUOPBlurHandler blurHandler;
	private DotplotUOPChangeHandler changeHandler;
	private DotplotUOPValueChangeHandler valueChangeHandler;
	private DotplotUOPKeyDownHandler keyDownHandler;

	// variable settings
	private Label varLabel;
	private Label varXLabel;
	private ListBox varXBox;
	private Label varYLabel;
	private ListBox varYBox;

	// display settings
	private Label absRelLabel;
	private CheckBox useColorScaleBox;
	private CheckBox showCorrelationBox;
	private Label varColorLabel;
	private ListBox varColorBox;
	private ColorPreviewer colorPreviewPanel;
	private ColorPickerDialog colorPickerDialog;
	private HTML separatorColorScale_splitOptions;
	private HTML separatorSplitOptions_correlation;
	private RadioButton singleViewRadioItem;
	private RadioButton separateRadioItem;

	// split settings
	private Label splitSettingsLabel;
	private Button splitButton;
	private Label splitVarLabel;
	private ListBox splitVarBox;
	private Label splitBinsLabel;
	private ListBox splitBinsBox;
	private Button splitChooseBoundariesButton;
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

	private Button okButton;

	private boolean splitBoundariesVisible;
	private boolean splitOptionsVisible;
	private boolean splitEnumClasses;

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

	public DotplotUserOptionsPanel(DotplotView view,
		DotplotController controller, DotplotModel model)
	{
		this.view = view;
		this.controller = controller;
		this.model = model;

		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		this.clickHandler = new DotplotUOPClickHandler();
		this.blurHandler = new DotplotUOPBlurHandler();
		this.changeHandler = new DotplotUOPChangeHandler();
		this.valueChangeHandler = new DotplotUOPValueChangeHandler();
		this.keyDownHandler = new DotplotUOPKeyDownHandler();

		createGuiComponents();
		layoutGuiComponents();
		addHandlers();

		dialogButton = new DialogButton(
			StatistiekGWT.rb.getString("settingsButton"), this.basisPanel);

		this.eventBus = StatistiekUtils.EVENT_BUS;
	}

	/**
	 * Add the handlers to the user options panel GUI components.
	 */
	private void addHandlers()
	{
		// click handlers
		this.useColorScaleBox.addClickHandler(this.clickHandler);
		this.showCorrelationBox.addClickHandler(this.clickHandler);
		this.singleViewRadioItem.addClickHandler(this.clickHandler);
		this.separateRadioItem.addClickHandler(this.clickHandler);
		this.splitButton.addClickHandler(this.clickHandler);
		this.splitChooseBoundariesButton.addClickHandler(this.clickHandler);
		this.okButton.addClickHandler(this.clickHandler);
		
		// blur handlers
		this.splitMinBoundaryField.addBlurHandler(this.blurHandler);
		this.splitBinWidthField.addBlurHandler(this.blurHandler);
		
		// key down handlers
		this.splitMinBoundaryField.addKeyDownHandler(this.keyDownHandler);
		this.splitBinWidthField.addKeyDownHandler(this.keyDownHandler);
		
		// change handlers
		this.varXBox.addChangeHandler(this.changeHandler);
		this.varYBox.addChangeHandler(this.changeHandler);
		this.varColorBox.addChangeHandler(this.changeHandler);
		this.splitVarBox.addChangeHandler(this.changeHandler);
		this.splitBinsBox.addChangeHandler(this.changeHandler);
		
		// value change handlers
		this.splitMinBoundaryField.addValueChangeHandler(this.valueChangeHandler);
		this.splitBinWidthField.addValueChangeHandler(this.valueChangeHandler);
		
		// color change handler
		this.colorPickerDialog.addColorChangeEventHandler(this.view);
	}

	private void createGuiComponents()
	{
		this.basisPanel = new FlowPanel();

		// var settings
		this.varLabel = new Label(StatistiekGWT.rb.getString("variableLabel"));
		this.varLabel.addStyleName(statistiekCss.titlelabel());

		this.varXLabel = new Label(StatistiekGWT.rb.getString("variableXLabel"));

		this.varXBox = new ListBox();
		this.varXBox.setWidth("100px");
		
		this.varYLabel = new Label(StatistiekGWT.rb.getString("variableYLabel"));
		this.varYLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.varYBox = new ListBox();
		this.varYBox.setWidth("100px");

		// display settings
		this.absRelLabel = new Label(StatistiekGWT.rb.getString("absRelLabel"));
		this.absRelLabel.addStyleName(statistiekCss.titlelabel());

		this.useColorScaleBox = new CheckBox(
			StatistiekGWT.rb.getString("usecolorscaleCheckbox"), false);
		this.useColorScaleBox.addStyleName(statistiekCss.checkBox());

		this.varColorLabel = new Label(
			StatistiekGWT.rb.getString("variablecolorscaleLabel"));
		this.varColorLabel.addStyleName(statistiekCss.spaceTopLabel());

		this.varColorBox = new ListBox();
		this.varColorBox.setWidth("100px");
		this.varColorBox.addStyleName(statistiekCss.block());
		this.varColorBox.addStyleName(statistiekCss.margin());

		this.colorPickerDialog = new ColorPickerDialog();
		this.colorPickerDialog.setColorA(this.model.getColorA());
		this.colorPickerDialog.setColorB(this.model.getColorB());
		
		this.colorPreviewPanel = new ColorPreviewer(ColorUtils.getRGBColor(this.model.getColorA()),
			ColorUtils.getRGBColor(this.model.getColorB()));
		this.colorPreviewPanel.getCanvas().setCoordinateSpaceWidth(100);
		this.colorPreviewPanel.getCanvas().setCoordinateSpaceHeight(25);
		this.colorPreviewPanel.getCanvas().addStyleName(statistiekCss.block());
		this.colorPreviewPanel.getCanvas().addStyleName(statistiekCss.margin());
		this.colorPreviewPanel.getCanvas().addClickHandler(new ClickHandler()
		{
			int clientX;
			int clientY;
			
			@Override
			public void onClick(ClickEvent event)
			{
				clientX = event.getClientX();
				clientY = event.getClientY();
				
				colorPickerDialog.setPopupPositionAndShow(new PopupPanel.PositionCallback()
				{
					public void setPosition(int offsetWidth, int offsetHeight)
					{
						colorPickerDialog.setPopupPosition(clientX, clientY - offsetHeight);// - scrollXPosition
					}
				});
			}
		});
		
		this.separatorColorScale_splitOptions = new HTML(DotplotUserOptionsPanel.hrString);
		this.separatorColorScale_splitOptions.addStyleName(statistiekCss.horizontalrule());

		this.showCorrelationBox = new CheckBox(
			StatistiekGWT.rb.getString("showcorrelationCheckbox"), false);
		this.showCorrelationBox.addStyleName(statistiekCss.checkBox());

		this.singleViewRadioItem = new RadioButton("splitViewGroup",
			StatistiekGWT.rb.getString("splitsingleviewCheckBox"));
		this.singleViewRadioItem.addStyleName(statistiekCss.radioButton());

		this.separateRadioItem = new RadioButton("splitViewGroup",
			StatistiekGWT.rb.getString("separateFromEachOtherRadioItem"));
		this.separateRadioItem.addStyleName(statistiekCss.radioButton());

		this.separatorSplitOptions_correlation = new HTML(DotplotUserOptionsPanel.hrString);
		this.separatorSplitOptions_correlation.addStyleName(statistiekCss.horizontalrule());

		// split settings
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

		Integer[] options1 = new Integer[20];
		for (int i = 0; i < 20; i++)
		{
			options1[i] = i + 1;
		}
		this.splitBinsBox = new ListBox();
		for (int i = 0; i < options1.length; i++)
		{
			this.splitBinsBox.addItem(options1[i].toString());
		}

		this.splitChooseBoundariesButton = new Button(
			StatistiekGWT.rb.getString("binsButton"));

		this.separatorSplitBoundaries = new HTML(DotplotUserOptionsPanel.hrString);
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

		this.okButton = new Button(StatistiekGWT.rb.getString("OKButtonText"));
	}

	private void layoutGuiComponents()
	{
		HorizontalPanel allSettingsPanel;
		FlowPanel variableSettingsPanel, displaySettingsPanel, splitSettingsPanel;
		
		// Variable settings
		variableSettingsPanel = new FlowPanel();
		variableSettingsPanel.setTitle(StatistiekGWT.rb.getString("variableLabel")); // tooltip boven panel
		variableSettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		variableSettingsPanel.add(this.varLabel);
		variableSettingsPanel.add(this.varXLabel);
		variableSettingsPanel.add(this.varXBox);

		if (this.model.isScatterplotMode())
		{
			variableSettingsPanel.add(this.varYLabel);
			variableSettingsPanel.add(this.varYBox);
		}

		// Display settings
		displaySettingsPanel = new FlowPanel();
		displaySettingsPanel.setTitle(StatistiekGWT.rb.getString("absRelLabel")); // tooltip boven panel
		displaySettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		displaySettingsPanel.add(this.absRelLabel);
		displaySettingsPanel.add(this.useColorScaleBox);
		displaySettingsPanel.add(this.varColorLabel);
		displaySettingsPanel.add(this.varColorBox);
		displaySettingsPanel.add(this.colorPreviewPanel.getCanvas());
		displaySettingsPanel.add(this.separatorColorScale_splitOptions);
		displaySettingsPanel.add(this.separateRadioItem);
		displaySettingsPanel.add(this.singleViewRadioItem);
		displaySettingsPanel.add(this.separatorSplitOptions_correlation);
		displaySettingsPanel.add(this.showCorrelationBox);

		// splitOptions settings
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
		allSettingsPanel = new HorizontalPanel();
		allSettingsPanel.setBorderWidth(2);
		allSettingsPanel.addStyleName(this.statistiekCss.horizontalPanel());
		allSettingsPanel.add(variableSettingsPanel);
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

	public int getVarXBoxSelectedIndex()
	{
		return this.varXBox.getSelectedIndex();
	}

	public int getVarYBoxSelectedIndex()
	{
		return this.varYBox.getSelectedIndex();
	}

	public int getVarColorBoxSelectedIndex()
	{
		return this.varColorBox.getSelectedIndex();
	}

	public int getSplitVarBoxSelectedIndex()
	{
		return this.splitVarBox.getSelectedIndex();
	}

	public boolean isUseColorScaleBoxSelected()
	{
		return this.useColorScaleBox.getValue();
	}

	public boolean isShowCorrelationBoxSelected()
	{
		return this.showCorrelationBox.getValue();
	}

	public boolean isSplitSingleViewSelected()
	{
		return this.singleViewRadioItem.getValue();
	}

	public int getSplitBinsBoxSelectedInt()
	{
		int selectedIndex = this.splitBinsBox.getSelectedIndex();
		return Integer.parseInt(this.splitBinsBox.getItemText(selectedIndex));
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
		this.splitBinWidthField.setText(StatistiekGWT.getFormattedBinWidth(this.model.getSplitBinBoundaries()));
	}
	
	public void setModel(DotplotModel model)
	{
		this.model = model;
	}

	public void update()
	{
		StatistiekUtils.removeAllItemsFromListBox(this.varXBox);
		for (String varName : this.model.getStatTableModel().getColumnNames())
		{
			this.varXBox.addItem(varName);
		}
		if (this.model.columnXIndexValid())
		{
			this.varXBox.setSelectedIndex(this.model.getColumnXIndex());
		}
		else
		{
			// set no item selected
			this.varXBox.setSelectedIndex(-1);
		}

		if (this.model.isScatterplotMode())
		{
			StatistiekUtils.removeAllItemsFromListBox(this.varYBox);
			for (String varName : this.model.getStatTableModel().getColumnNames())
			{
				this.varYBox.addItem(varName);
			}
			if (this.model.columnYIndexValid())
			{
				this.varYBox.setSelectedIndex(this.model.getColumnYIndex());
			}
			else
			{
				// set no item selected
				this.varYBox.setSelectedIndex(-1);
			}
		} // scatterplot mode

		StatistiekUtils.removeAllItemsFromListBox(this.varColorBox);
		for (String varName : this.model.getStatTableModel().getColumnNames())
		{
			this.varColorBox.addItem(varName);
		}
		if (this.model.columnColorIndexValid())
		{
			this.varColorBox.setSelectedIndex(this.model.getColumnColorIndex());
		}
		else
		{
			// set no item selected
			this.varColorBox.setSelectedIndex(-1);
		}

		StatistiekUtils.removeAllItemsFromListBox(this.splitVarBox);
		this.splitVarBox.addItem(StatistiekGWT.rb.getString("chooseItem"));
		for (int column = 0; column < this.model.getStatTableModel()
			.getColumnCount(); column++)
		{
			splitVarBox.addItem(this.model.getStatTableModel()
				.getColumnName(column));
		}
		this.splitVarBox.setSelectedIndex(this.model.getSplitOptions()
			.getColumnSplitIndex() + 1);

		this.setSelectedItemInListBox(
			this.splitBinsBox, 
			String.valueOf(this.model.getSplitOptions().getBinBoundaries().size() - 1));

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
				this.splitBinWidthField.setText(StatistiekGWT.getFormattedBinWidth(this.model.getSplitBinBoundaries()));
				StringBuilder sb = new StringBuilder();
				int splitClasses = this.model.getStatTableModel().splitVarClasses(
					this.model.getSplitOptions());
				for (int i = 0; i < splitClasses; i++)
				{
					sb.append(this.model.getSplitOptions()
						.getSplitClassLabel(i, this.model.getStatTableModel()));
					sb.append("\n");
				}
				
				this.splitBoundariesArea.setText(sb.toString());
				this.splitNoObjectsLabel.setText(StatistiekGWT.rb.getString("numberLabel")
					+ this.model.getStatTableModel().getRowCount());
				String splitMinValue = StatistiekGWT.getStringValue(
					this.model.getStatTableModel().getColumnMin(this.model.getSplitOptions().getColumnSplitIndex()));
				this.splitMinValueLabel.setText(StatistiekGWT.rb.getString("minLabel")
					+ splitMinValue);
				String splitMaxValue = StatistiekGWT.getStringValue(
					this.model.getStatTableModel().getColumnMax(this.model.getSplitOptions().getColumnSplitIndex()));
				this.splitMaxValueLabel.setText(StatistiekGWT.rb.getString("maxLabel")
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
				String stringWithoutWildcard = sb.substring(0, sb.length() - 2);//- 1); -2 voor /n en wildcard
				this.splitBoundariesArea.setText(stringWithoutWildcard);
				this.separatorSplitBoundaries.setVisible(false);
				this.splitBinsBox.setVisible(false);
				this.splitBinsLabel.setVisible(false);
				setSplitEnumClasses(true);
			}
			else if (splitType.equals(AllowedTypes.STRING))
			{
				StringBuilder sb = new StringBuilder();
				int splitClasses = this.model.getStatTableModel().splitVarClasses(
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
		} // split

		boolean correlatieMogelijk = this.model.getColumnXIndex() > -1
			&& this.model.getColumnYIndex() > -1;
		this.showCorrelationBox.setVisible(correlatieMogelijk);
		this.separatorSplitOptions_correlation.setVisible(correlatieMogelijk);

		boolean colorScale;
		boolean split = this.hasSplit();
		if (split)
		{
			// voor split geen colorScale tonen
			colorScale = false;
		}
		else
		{
			colorScale = this.model.isUseColorScale();
		}
		
		setColorOptionsVisible(colorScale);
		
		this.setVisibleSplitOptions(this.splitOptionsVisible);

		this.separateRadioItem.setValue(!this.model.splitInSingleView());
		this.singleViewRadioItem.setValue(this.model.splitInSingleView());
		this.separateRadioItem.setVisible(split);
		this.separateRadioItem.setVisible(split);
		this.singleViewRadioItem.setVisible(split);
		this.singleViewRadioItem.setVisible(split);

		if (this.model.isScatterplotMode() 
			&& this.view.getXType().isNumber() && this.view.getYType().isNumber()
			&& !split)
		{
			this.enableCorrelationCheckBox(true);
			this.showCorrelationBox.setValue(this.model.isShowCorrelation());
		}
		else
		{
			// uncheck
			this.showCorrelationBox.setValue(false);
			// and disable
			this.enableCorrelationCheckBox(false);
		}
	}

	public void init()
	{
		// Check if a split is set and initialize properly
		if (hasSplit())
		{
			setVisibleSplitOptions(true);
		}
		else
		{
			setVisibleSplitOptions(false);
		}

		setVisibleSplitBoundaryOptions(false);
		
		boolean colorScale;
		if (this.hasSplit())
		{
			// voor split geen colorScale tonen
			colorScale = false;
		}
		else
		{
			colorScale = this.model.isUseColorScale();
		}
		
		setColorOptionsVisible(colorScale);
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
	
	private void enableCorrelationCheckBox(boolean b)
	{
		this.showCorrelationBox.setEnabled(b);
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
		this.singleViewRadioItem.setVisible(b);
		this.separateRadioItem.setVisible(b);
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
			setVisibleSplitBoundaryOptions(false);
		}
		else
		{
			this.splitButton.setText(StatistiekGWT.rb
				.getString("removeSplitoptionsButton"));
		}
	}

	private void setColorOptionsVisible(boolean b)
	{
		this.varColorLabel.setVisible(b);
		this.varColorBox.setVisible(b);
		this.colorPreviewPanel.getCanvas().setVisible(b);

		if (this.hasSplit()) // dotplot of scatterplot met split
		{
			// bij split geen kleurschaal
			this.useColorScaleBox.setVisible(false);
			this.separatorColorScale_splitOptions.setVisible(false);
		}
		else // dotplot of scatterplot zonder split
		{
			this.useColorScaleBox.setVisible(true);
			this.useColorScaleBox.setValue(b);
			this.colorPreviewPanel.setColorA(ColorUtils.getRGBColor(this.model.getColorA()));
			this.colorPreviewPanel.setColorB(ColorUtils.getRGBColor(this.model.getColorB()));
			this.colorPickerDialog.setColorA(this.model.getColorA());
			this.colorPickerDialog.setColorB(this.model.getColorB());
			this.separatorColorScale_splitOptions.setVisible(false);
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
		this.splitVarBox.setSelectedIndex(0);
		this.splitBinWidthField.setText("");
		this.splitMinBoundaryField.setText("");
		this.splitBoundariesArea.setText("");
		this.splitNoObjectsLabel.setText("");
		this.splitMinValueLabel.setText("");
		this.splitMaxValueLabel.setText("");
	}

	
	/**
	 * A clickhandler for DotplotUserOptionsPanel
	 */
	class DotplotUOPClickHandler implements ClickHandler
	{

		@Override
		public void onClick(ClickEvent e)
		{
			DotplotModel model = DotplotUserOptionsPanel.this.model;
			DotplotView view = DotplotUserOptionsPanel.this.view;
			
			if (e.getSource() == useColorScaleBox)
			{
				model.setUseColorScale(view.getUseColorScaleBoxSelected());

				setColorOptionsVisible(useColorScaleBox.getValue());
				varColorBox.setSelectedIndex(-1);
				this.update();
			}
			else if (e.getSource() == showCorrelationBox)
			{
				model.setShowCorrelation(view.getShowCorrelationBoxSelected());
				view.updateCorrelation();
			}
			else if (e.getSource() == singleViewRadioItem)
			{
				model.setSplitInSingleView(true);
				this.update();
			}
			else if (e.getSource() == separateRadioItem)
			{
				model.setSplitInSingleView(false);
				this.update();
			}
			else if (e.getSource() == splitButton)
			{
				if (splitOptionsVisible)
				{
					// verwijder splitsing
					model.setColumnSplitIndex(-1);
					setVisibleSplitOptions(false);
					clearGUISplitComponents();
					this.update();
				}
				else
				{
					setVisibleSplitOptions(true);
					if (model.isScatterplotMode())
					{
						// for a split no correlation should be shown 
						model.setShowCorrelation(false);
						enableCorrelationCheckBox(false);
					}
					this.updateUserOptionsPanel();
				}
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
			else if (e.getSource() == okButton)
			{
				if (splitVarBox.getSelectedIndex() == 0)
				{
					setVisibleSplitOptions(false);
				}
				if (splitVarBox.getSelectedIndex() == 0)
				{
					setVisibleSplitOptions(false);
				}
				if (varColorBox.getSelectedIndex() < 0)
				{
					setColorOptionsVisible(false);
					useColorScaleBox.setValue(false);
				}
				dialogButton.closeDialog();
			}
		}
		
		/**
		 * Update the user options panel.
		 */
		private void updateUserOptionsPanel()
		{
			// update the user options panel
			DotplotUserOptionsPanel.this.update();
		}

		/**
		 * Update the view and the user options panel.
		 */
		private void update()
		{
			// update view and user options panel
			DotplotUserOptionsPanel.this.view.update();
		}
	} // class DotplotUOPClickHandler

	/**
	 * A blurhandler for DotplotUserOptionsPanel
	 */
	class DotplotUOPBlurHandler implements BlurHandler
	{
		@Override
		public void onBlur(BlurEvent e)
		{
			DotplotController controller = DotplotUserOptionsPanel.this.controller;

			if (e.getSource() == splitMinBoundaryField)
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
			DotplotUserOptionsPanel.this.view.update();
		}
	} // class DotplotUOPBlurHandler
	
	class DotplotUOPChangeHandler implements ChangeHandler
	{
		@Override
		public void onChange(ChangeEvent e)
		{
			if (e.getSource() == varXBox)
			{
				model.setColumnXIndex(view.getVarXBoxSelected());
//				model.setColumnXIndex(DotplotUserOptionsPanel.this.getVarXBoxSelectedIndex());
				if (model.isScatterplotMode() 
					&& !(view.getXType().isNumber() 
						&& view.getYType().isNumber()))
				{
					// if not both variables are numerical, no correlation should be shown 
					model.setShowCorrelation(false);
				}
			}
			else if (e.getSource() == varYBox)
			{
				model.setColumnYIndex(view.getVarYBoxSelected());
				if (!(view.getXType().isNumber() && view.getYType().isNumber()))
				{
					// if not both variables are numerical, no correlation should be shown 
					model.setShowCorrelation(false);
				}
			}
			else if (e.getSource() == varColorBox)
			{
				model.setColumnColorIndex(view.getVarColorBoxSelected());
			}
			else if (e.getSource() == splitVarBox)
			{
				if (view.getSplitVarBoxSelectedIndex() - 1 != model
					.getSplitOptions().getColumnSplitIndex())
				{
					model.setColumnSplitIndex(view.getSplitVarBoxSelectedIndex() - 1);
					model.setSplitOptions(model.getSplitOptions());
					if (view.getSplitVarBoxSelectedIndex() > 0)
					{
						controller.setSplitType(model.getStatTableModel()
							.getColumnTypes()
							.get(model.getSplitOptions().getColumnSplitIndex())
							.getType());
					}
				}

			}
			else if (e.getSource() == splitBinsBox)
			{
				ArrayList<ColumnType> list = model.getStatTableModel().getColumnTypes();
				controller.setSplitType(list.get(model.getSplitOptions().getColumnSplitIndex())
					.getType());
			}

			// update view (and uop)
			DotplotUserOptionsPanel.this.view.update();
		}
	} // class DotplotUOPChangeHandler

	
	class DotplotUOPValueChangeHandler implements ValueChangeHandler<String>
	{
		@Override
		public void onValueChange(ValueChangeEvent<String> e)
		{
			if (e.getSource() == splitMinBoundaryField)
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
			DotplotUserOptionsPanel.this.view.update();
		}
	} // class DotplotUOPValueChangeHandler

	
	class DotplotUOPKeyDownHandler implements KeyDownHandler
	{
		@Override
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				DotplotController controller = DotplotUserOptionsPanel.this.controller;

				if (e.getSource() == splitMinBoundaryField)
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
				DotplotUserOptionsPanel.this.view.update();
			}
		}
	} // class DotplotUOPKeyDownHandler

}
