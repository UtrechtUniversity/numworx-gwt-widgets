package fi.statistiekgwt.client.boxplot;

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

public class BoxplotUserOptionsPanel extends FlowPanel
{

	private BoxplotView view;
	private BoxplotController controller;
	private BoxplotModel model;

	private DialogButton dialogButton;

	/**
	 * Panel 'basisPanel' wordt aan DialogButton meegegeven als content.
	 */
	private FlowPanel basisPanel;
	
	private BoxplotUOPClickHandler clickHandler;
	private BoxplotUOPBlurHandler blurHandler;
	private BoxplotUOPChangeHandler changeHandler;
	private BoxplotUOPValueChangeHandler valueChangeHandler;
	private BoxplotUOPKeyDownHandler keyDownHandler;

	// variable settings
	private Label varLabel;
	private ListBox columnIndexBox;

	// display settings
	private Label absRelLabel;
	private RadioButton verticalBoxesRadioItem;
	private RadioButton horizontalBoxesRadioItem;

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

	public BoxplotUserOptionsPanel(BoxplotView view,
		BoxplotController controller, BoxplotModel model)
	{
		this.view = view;
		this.controller = controller;
		this.model = model;

		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		this.clickHandler = new BoxplotUOPClickHandler();
		this.blurHandler = new BoxplotUOPBlurHandler();
		this.changeHandler = new BoxplotUOPChangeHandler();
		this.valueChangeHandler = new BoxplotUOPValueChangeHandler();
		this.keyDownHandler = new BoxplotUOPKeyDownHandler();

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
		this.horizontalBoxesRadioItem.addClickHandler(this.clickHandler);
		this.verticalBoxesRadioItem.addClickHandler(this.clickHandler);
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
		this.columnIndexBox.addChangeHandler(this.changeHandler);
		this.splitVarBox.addChangeHandler(this.changeHandler);
		this.splitBinsBox.addChangeHandler(this.changeHandler);
		
		// value change handlers
		this.splitMinBoundaryField.addValueChangeHandler(this.valueChangeHandler);
		this.splitBinWidthField.addValueChangeHandler(this.valueChangeHandler);
	}

	private void createGuiComponents()
	{
		this.basisPanel = new FlowPanel();

		// var settings
		this.varLabel = new Label(StatistiekGWT.rb.getString("variableLabel"));
		this.varLabel.addStyleName(statistiekCss.titlelabel());

		this.columnIndexBox = new ListBox();
		this.columnIndexBox.setWidth("100px");

		// display settings
		this.absRelLabel = new Label(StatistiekGWT.rb.getString("absRelLabel"));
		this.absRelLabel.addStyleName(statistiekCss.titlelabel());

		this.verticalBoxesRadioItem = new RadioButton("verticalHorizontalGroup",
			StatistiekGWT.rb.getString("verticalboxplotsRadio"));
		this.verticalBoxesRadioItem.addStyleName(statistiekCss.radioButton());

		this.horizontalBoxesRadioItem = new RadioButton("verticalHorizontalGroup",
			StatistiekGWT.rb.getString("horizontalboxplotsRadio"));
		this.horizontalBoxesRadioItem.addStyleName(statistiekCss.radioButton());

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

		this.separatorSplitBoundaries = new HTML(BoxplotUserOptionsPanel.hrString);
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
		variableSettingsPanel = new FlowPanel();//new LayoutPanel();
		variableSettingsPanel.setTitle(StatistiekGWT.rb.getString("variableLabel")); // tooltip boven panel
		variableSettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		variableSettingsPanel.add(varLabel);
		variableSettingsPanel.add(this.columnIndexBox);

		// Display settings
		displaySettingsPanel = new FlowPanel();
		displaySettingsPanel.setTitle(StatistiekGWT.rb.getString("absRelLabel")); // tooltip boven panel
		displaySettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		displaySettingsPanel.add(this.absRelLabel);
		displaySettingsPanel.add(horizontalBoxesRadioItem);
		displaySettingsPanel.add(verticalBoxesRadioItem);

		// splitOptions
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

	public String getColumnBoxSelectedString()
	{
		int selectedIndex = this.columnIndexBox.getSelectedIndex();
		
		return this.columnIndexBox.getItemText(selectedIndex);
	}

	public boolean isVerticalBoxesButtonSelected()
	{
		return verticalBoxesRadioItem.getValue();
	}

	public int getColumnIndexBoxSelectedIndex()
	{
		return this.columnIndexBox.getSelectedIndex();
	}

	public int getSplitVarBoxSelectedIndex()
	{
		return this.splitVarBox.getSelectedIndex();
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
	
	public void setModel(BoxplotModel model)
	{
		this.model = model;
	}

	public void update()
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
					this.splitNoObjectsLabel.setText(StatistiekGWT.rb
						.getString("numberLabel")
						+ this.model.getStatTableModel().getRowCount());
					String splitMinValue = StatistiekGWT.getStringValue(
						this.model.getStatTableModel().getColumnMin(this.model.getSplitOptions().getColumnSplitIndex()));
					this.splitMinValueLabel.setText(StatistiekGWT.rb.getString("minLabel") + splitMinValue);
					String splitMaxValue = StatistiekGWT.getStringValue(
						this.model.getStatTableModel().getColumnMax(this.model.getSplitOptions().getColumnSplitIndex()));
					this.splitMaxValueLabel.setText(StatistiekGWT.rb.getString("maxLabel") + splitMaxValue);
					this.separatorSplitBoundaries.setVisible(true);
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
			}
		}

		if (this.model.isVerticalBoxplots())
		{
			verticalBoxesRadioItem.setValue(true);
		}
		else
		{
			horizontalBoxesRadioItem.setValue(true);
		}

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

	public void init()
	{
		this.setVisibleSplitBoundaryOptions(false);
	}

	private void setSplitEnumClasses(boolean b)
	{
		splitEnumClasses = b;
		splitMinBoundaryLabel.setVisible(splitBoundariesVisible && !b);
		splitMinBoundaryField.setVisible(splitBoundariesVisible && !b);
		splitBinWidthLabel.setVisible(splitBoundariesVisible && !b);
		splitBinWidthField.setVisible(splitBoundariesVisible && !b);
		splitNoObjectsLabel.setVisible(splitBoundariesVisible && !b);
		splitMinValueLabel.setVisible(splitBoundariesVisible && !b);
		splitMaxValueLabel.setVisible(splitBoundariesVisible && !b);
	}

	private void setVisibleSplitBoundaryOptions(boolean b)
	{
		splitBoundariesVisible = b;
		separatorSplitBoundaries.setVisible(b);
		splitMinBoundaryLabel.setVisible(b && !splitEnumClasses);
		splitMinBoundaryField.setVisible(b && !splitEnumClasses);
		splitBinWidthLabel.setVisible(b && !splitEnumClasses);
		splitBinWidthField.setVisible(b && !splitEnumClasses);
		splitBoundariesLabel.setVisible(b);
		splitBoundariesArea.setVisible(b);
		splitNoObjectsLabel.setVisible(b && !splitEnumClasses);
		splitMinValueLabel.setVisible(b && !splitEnumClasses);
		splitMaxValueLabel.setVisible(b && !splitEnumClasses);
		if (!b)
		{
			splitChooseBoundariesButton.setText(StatistiekGWT.rb
				.getString("binsButton"));
		}
		else
		{
			splitChooseBoundariesButton.setText(StatistiekGWT.rb
				.getString("hideButtonLabel"));
		}
	}

	void setVisibleSplitOptions(boolean b)
	{
		splitOptionsVisible = b;
		splitVarLabel.setVisible(b);
		splitVarBox.setVisible(b);
		if (!b)
		{
			splitBinsLabel.setVisible(b);
		}
		if (!b)
		{
			splitBinsBox.setVisible(b);
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
			splitButton.setText(StatistiekGWT.rb.getString("splitoptionsButton"));
			setVisibleSplitBoundaryOptions(false);
		}
		else
		{
			splitButton.setText(StatistiekGWT.rb
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
		this.splitVarBox.setSelectedIndex(0);
		this.splitBinWidthField.setText("");
		this.splitMinBoundaryField.setText("");
		this.splitBoundariesArea.setText("");
		this.splitNoObjectsLabel.setText("");
		this.splitMinValueLabel.setText("");
		this.splitMaxValueLabel.setText("");
	}

	/**
	 * A clickhandler for BoxplotUserOptionsPanel
	 */
	class BoxplotUOPClickHandler implements ClickHandler
	{

		@Override
		public void onClick(ClickEvent e)
		{
			BoxplotModel model = BoxplotUserOptionsPanel.this.model;
			BoxplotView view = BoxplotUserOptionsPanel.this.view;
			
			if ((e.getSource() == horizontalBoxesRadioItem) || (e.getSource() == verticalBoxesRadioItem))
			{
				model.setVerticalBoxplots(view.isVerticalBoxesButtonSelected());
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
				dialogButton.closeDialog();
			}
			else
			{
				//System.out.println("BoxplotUserOptionsPanel.BoxplotUOPClickHandler.onClick(): Unknown action source! " + e);
			}
		}
		
		/**
		 * Update the user options panel.
		 */
		private void updateUserOptionsPanel()
		{
			// update the user options panel
			BoxplotUserOptionsPanel.this.update();
		}

		/**
		 * Update the view and the user options panel.
		 */
		private void update()
		{
			// update view and user options panel
			BoxplotUserOptionsPanel.this.view.update();
		}
	} // class BoxplotUOPClickHandler

	/**
	 * A blurhandler for BoxplotUserOptionsPanel
	 */
	class BoxplotUOPBlurHandler implements BlurHandler
	{
		@Override
		public void onBlur(BlurEvent e)
		{
			BoxplotController controller = BoxplotUserOptionsPanel.this.controller;

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
			BoxplotUserOptionsPanel.this.view.update();
		}
	} // class BoxplotUOPBlurHandler
	
	class BoxplotUOPChangeHandler implements ChangeHandler
	{
		@Override
		public void onChange(ChangeEvent e)
		{
			if (e.getSource() == columnIndexBox)
			{
				model.setColumnIndex(BoxplotUserOptionsPanel.this.getColumnIndexBoxSelectedIndex());
				
				String selectedString = view.getColumnBoxSelectedString();
				if (selectedString != null)
				{
					model.setColumnIndex(model.getStatTableModel().getColumnIndexByName(selectedString));
				}

			}
			else if (e.getSource() == splitVarBox)
			{
				model.setColumnSplitIndex(view.getSplitVarBoxSelectedIndex() - 1);
				//model.setSplitOptions(model.getSplitOptions());
				if (view.getSplitVarBoxSelectedIndex() > 0)
				{
					ArrayList<ColumnType> list = model.getStatTableModel().getColumnTypes();
					controller.setSplitType(list.get(model.getSplitOptions().getColumnSplitIndex())
						.getType());
				}
				model.setPercentileValues();

			}
			else if (e.getSource() == splitBinsBox)
			{
				ArrayList<ColumnType> list = model.getStatTableModel().getColumnTypes();
				controller.setSplitType(list.get(model.getSplitOptions().getColumnSplitIndex())
					.getType());
			}

			// update view (and uop)
			BoxplotUserOptionsPanel.this.view.update();
		}
	} // class BoxplotUOPChangeHandler

	class BoxplotUOPValueChangeHandler implements ValueChangeHandler<String>
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
			BoxplotUserOptionsPanel.this.view.update();
		}
	} // class BoxplotUOPValueChangeHandler

	class BoxplotUOPKeyDownHandler implements KeyDownHandler
	{
		@Override
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				BoxplotController controller = BoxplotUserOptionsPanel.this.controller;

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
				BoxplotUserOptionsPanel.this.view.update();
			}
		}
	} // class BoxplotUOPKeyDownHandler

}
