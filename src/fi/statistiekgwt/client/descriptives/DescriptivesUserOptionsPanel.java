package fi.statistiekgwt.client.descriptives;

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
 * User options panel for StatistiekView Descriptives
 * 
 * @author Sylvia van Borkulo
 * 
 */
public class DescriptivesUserOptionsPanel extends FlowPanel
{

	private DescriptivesView view;
	private DescriptivesController controller;
	private DescriptivesModel model;

	private DialogButton dialogButton;

	/**
	 * Panel 'basisPanel' wordt aan DialogButton meegegeven als content.
	 */
	private FlowPanel basisPanel;
	//private JPanel panel;
	
	private DescriptivesUOPClickHandler clickHandler;
	private DescriptivesUOPBlurHandler blurHandler;
	private DescriptivesUOPChangeHandler changeHandler;
	private DescriptivesUOPValueChangeHandler valueChangeHandler;
	private DescriptivesUOPKeyDownHandler keyDownHandler;

	// variable settings
	private Label varLabel;
	/**
	 * The box for choosing the variable in the descriptives table
	 */
	private ListBox columnIndexBox;
	
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
	private boolean splitOptionsVisible;
	private boolean splitBoundariesVisible;
	private boolean splitEnumClasses;

	private Button okButton;
	
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

	public DescriptivesUserOptionsPanel(DescriptivesView view,
		DescriptivesController controller, DescriptivesModel model)
	{
		this.view = view;
		this.controller = controller;
		this.model = model;

		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		this.clickHandler = new DescriptivesUOPClickHandler();
		this.blurHandler = new DescriptivesUOPBlurHandler();
		this.changeHandler = new DescriptivesUOPChangeHandler();
		this.valueChangeHandler = new DescriptivesUOPValueChangeHandler();
		this.keyDownHandler = new DescriptivesUOPKeyDownHandler();

		this.createGuiComponents();
		this.layoutGuiComponents();
		this.addHandlers();

		this.dialogButton = new DialogButton(
			StatistiekGWT.rb.getString("settingsButton"), this.basisPanel);

		this.eventBus = StatistiekUtils.EVENT_BUS;
	}

	private void addHandlers()
	{
		// click handlers
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
		this.columnIndexBox.setWidth("100px");//PixelSize(100, 25);

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
		this.splitVarBox.setWidth("100px");

		this.splitBinsLabel = new Label(
			StatistiekGWT.rb.getString("noClassesLabel"));
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
			StatistiekGWT.rb.getString("binsButton"));

		this.separatorSplitBoundaries = new HTML(this.hrString);
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
		FlowPanel variableSettingsPanel, splitSettingsPanel;

		// Variable settings
		variableSettingsPanel = new FlowPanel();
		variableSettingsPanel.setTitle(StatistiekGWT.rb.getString("variableLabel")); // tooltip boven panel
		variableSettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		variableSettingsPanel.add(this.varLabel);
		variableSettingsPanel.add(this.columnIndexBox);

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
		allSettingsPanel = new HorizontalPanel();//new LayoutPanel();
		allSettingsPanel.setBorderWidth(2);
		allSettingsPanel.addStyleName(this.statistiekCss.horizontalPanel());
		allSettingsPanel.add(variableSettingsPanel);
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
		return this.dialogButton;
	}

	public int getColumnIndexBoxSelectedIndex()
	{
		return this.columnIndexBox.getSelectedIndex();
	}

	public int getSplitVarBoxSelectedIndex()
	{
		return this.splitVarBox.getSelectedIndex();
	}

	public void setModel(DescriptivesModel model)
	{
		this.model = model;
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
	
	public void updateColumnIndexBox()
	{
		this.removeAllItemsFromListBox(this.columnIndexBox);

		for (String varName : this.model.getStatTableModel().getColumnNames())
		{
			this.columnIndexBox.addItem(varName);
		}
		
		//if (this.model.getTableModel().isColumnIndexValid(this.model.getColumnIndex()))
		if (this.model.columnIndexValid())
		{
			this.columnIndexBox.setSelectedIndex(this.model.getColumnIndex());
		}
		else
		{
			// set no item selected
			//System.out.println("DescriptivesUserOptionsPanel.update(): no column index selected!");
			this.columnIndexBox.setSelectedIndex(-1);
		}
	}
	
	public void update()
	{
		//System.out.println("DescriptivesUserOptionsPanel.update()");
		
		updateColumnIndexBox();
		updateSplitSettings();
	}
	
	public void init()
	{
		this.setVisibleSplitBoundaryOptions(false);
	}

	private void updateSplitSettings()
	{
		this.removeAllItemsFromListBox(this.splitVarBox);
		this.splitVarBox.addItem(StatistiekGWT.rb.getString("chooseItem"));
		for (int column = 0; column < 
				this.model.getStatTableModel().getColumnCount(); column++)
		{
			splitVarBox.addItem(this.model.getStatTableModel()
				.getColumnName(column));
		}
		
		// check of columnindex valid
		if (this.model.columnIndexValid())
		{
			//System.out.println("DescriptivesUserOptionsPanel.update(): COLUMN INDEX VALID!");
			this.splitVarBox.setSelectedIndex(this.model.getSplitOptions()
				.getColumnSplitIndex() + 1);
		}
		else
		{
			// set no item selected
			this.splitVarBox.setSelectedIndex(0);
		}
		
		this.setSelectedItemInListBox(
			this.splitBinsBox, 
			String.valueOf(this.model.getSplitOptions().getBinBoundaries().size() - 1));
		
		// check of column index valid
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
    				this.splitBinWidthField.setText(StatistiekGWT.getFormattedBinWidth(this.model.getSplitOptions().getBinBoundaries()));
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
    				String splitMinValue = StatistiekGWT.getStringValue(this.model.getStatTableModel().getColumnMin(
						this.model.getSplitOptions().getColumnSplitIndex()));
    				this.splitMinValueLabel.setText(StatistiekGWT.rb.getString("minLabel") + splitMinValue);
    				String splitMaxValue = StatistiekGWT.getStringValue(this.model.getStatTableModel().getColumnMax(
						this.model.getSplitOptions().getColumnSplitIndex()));
    				this.splitMaxValueLabel.setText(StatistiekGWT.rb.getString("maxLabel") + splitMaxValue);
    				setSplitEnumClasses(false);
    				this.splitBinsLabel.setVisible(true);
    				this.splitBinsBox.setVisible(true);
    			}
    			else if (splitType.equals(AllowedTypes.ENUM))
    			{
    				StringBuilder sb = new StringBuilder();
    				for (String s : cSplitType.getEnumOptions())
    				{
    					sb.append(s);
    					sb.append("\n");
    				}
    				String stringWithoutWildcard = sb.substring(0, sb.length() - 2);//-2 voor /n en wildcard
    				this.splitBoundariesArea.setText(stringWithoutWildcard);
    				this.splitBinsLabel.setVisible(false);
    				this.splitBinsBox.setVisible(false);
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
    		} // er is een split variabele ingesteld
		}
		
		boolean split = this.hasSplit();
//		this.setVisibleSplitOptions(split);
		this.setVisibleSplitOptions(this.splitOptionsVisible);
	}

	private boolean hasSplit()
	{
		boolean split = this.model.getSplitOptions().getColumnSplitIndex() > -1;

		return split;
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
		splitEnumClasses = b;
		
//		this.splitNoBinsBox.setVisible(!b);
//		this.splitNoBinsLabel.setVisible(!b);

		splitMinBoundaryLabel.setVisible(splitBoundariesVisible && !b);
		splitMinBoundaryField.setVisible(splitBoundariesVisible && !b);
		splitBinWidthLabel.setVisible(splitBoundariesVisible && !b);
		splitBinWidthField.setVisible(splitBoundariesVisible && !b);
		splitNoObjectsLabel.setVisible(splitBoundariesVisible && !b);
		splitMinValueLabel.setVisible(splitBoundariesVisible && !b);
		splitMaxValueLabel.setVisible(splitBoundariesVisible && !b);
	}

	void setVisibleSplitOptions(boolean b)
	{
		this.splitOptionsVisible = b;

		this.splitVarLabel.setVisible(b);
		this.splitVarBox.setVisible(b);

		if (!b)
		{
			this.splitBinsLabel.setVisible(b);
			this.splitBinsBox.setVisible(b);
			this.splitChooseBoundariesButton.setVisible(b);
			this.splitButton.setText(StatistiekGWT.rb.getString("splitoptionsButton"));
			this.setVisibleSplitBoundaryOptions(false);
		}
		else
		{
			this.splitButton.setText(StatistiekGWT.rb
				.getString("removeSplitoptionsButton"));
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
	}

	/**
	 * Set the visibility of all components under the button 'Klassen'.
	 * 
	 * @param b
	 */
	private void setVisibleSplitBoundaryOptions(boolean b)
	{
		this.splitBoundariesVisible = b;
		this.separatorSplitBoundaries.setVisible(b);
		this.splitMinBoundaryLabel.setVisible(b && !splitEnumClasses);
		this.splitMinBoundaryField.setVisible(b && !splitEnumClasses);
		this.splitBinWidthLabel.setVisible(b && !splitEnumClasses);
		this.splitBinWidthField.setVisible(b && !splitEnumClasses);
		this.splitBoundariesLabel.setVisible(b);
		this.splitBoundariesArea.setVisible(b);
		this.splitNoObjectsLabel.setVisible(b && !splitEnumClasses);
		this.splitMinValueLabel.setVisible(b && !splitEnumClasses);
		this.splitMaxValueLabel.setVisible(b && !splitEnumClasses);
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

	public double getSplitminBoundary()
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
	
	public int getSplitBinsBoxSelectedInt()
	{
		int selectedIndex = this.splitBinsBox.getSelectedIndex();
		String itemText = this.splitBinsBox.getItemText(selectedIndex);
		return Integer.parseInt(itemText);
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
	 * A clickhandler for DescriptivesUserOptionsPanel
	 */
	class DescriptivesUOPClickHandler implements ClickHandler
	{

		@Override
		public void onClick(ClickEvent e)
		{
			DescriptivesModel model = DescriptivesUserOptionsPanel.this.model;
			DescriptivesView view = DescriptivesUserOptionsPanel.this.view;
			DescriptivesController controller = DescriptivesUserOptionsPanel.this.controller;
			
			if (e.getSource() == splitChooseBoundariesButton)
			{
				if (splitBoundariesVisible)
				{
					setVisibleSplitBoundaryOptions(false);
				}
				else
				{
					setVisibleSplitBoundaryOptions(true);
				}

				updateSplitSettings();
			}
			else if (e.getSource() == splitButton)
			{
				if (splitOptionsVisible)
				{
					// verwijder splitsing...
					model.setColumnSplitIndex(-1);
					setVisibleSplitOptions(false);
					DescriptivesUserOptionsPanel.this.clearGUISplitComponents();
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
				init();
			}
		}
		
		/**
		 * Update the user options panel.
		 */
		private void updateUserOptionsPanel()
		{
			// update the user options panel
			DescriptivesUserOptionsPanel.this.update();
		}

		/**
		 * Update the view and the user options panel.
		 */
		private void update()
		{
			// update view and user options panel
			DescriptivesUserOptionsPanel.this.view.update();
		}
	} // class DescriptivesUOPClickHandler

	/**
	 * A blurhandler for DescriptivesUserOptionsPanel
	 */
	class DescriptivesUOPBlurHandler implements BlurHandler
	{
		@Override
		public void onBlur(BlurEvent e)
		{
			DescriptivesController controller = DescriptivesUserOptionsPanel.this.controller;

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
			DescriptivesUserOptionsPanel.this.view.update();
		}
	} // class DescriptivesUOPBlurHandler
	
	class DescriptivesUOPChangeHandler implements ChangeHandler
	{
		@Override
		public void onChange(ChangeEvent e)
		{
			if (e.getSource() == columnIndexBox)
			{
				model.setColumnIndex(DescriptivesUserOptionsPanel.this.getColumnIndexBoxSelectedIndex());
			}
			else if (e.getSource() == splitBinsBox)
			{
				controller.setSplitType(model.getStatTableModel().getColumnTypes()
					.get(model.getSplitOptions().getColumnSplitIndex())
					.getType());
			}
			else if (e.getSource() == splitVarBox)
			{
				model.setColumnSplitIndex(DescriptivesUserOptionsPanel.this.getSplitVarBoxSelectedIndex() - 1);
				ArrayList<ColumnType> list = model.getStatTableModel().getColumnTypes();
				controller.setSplitType(list.get(model.getSplitOptions().getColumnSplitIndex())
					.getType());			
			}

			// update view (and uop)
			view.update();
		}
	} // class DescriptivesUOPChangeHandler

	class DescriptivesUOPValueChangeHandler implements ValueChangeHandler<String>
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
			DescriptivesUserOptionsPanel.this.view.update();
		}
	} // class DescriptivesUOPValueChangeHandler

	class DescriptivesUOPKeyDownHandler implements KeyDownHandler
	{
		@Override
		public void onKeyDown(KeyDownEvent e)
		{
			if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER)
			{
				DescriptivesController controller = DescriptivesUserOptionsPanel.this.controller;

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
				DescriptivesUserOptionsPanel.this.view.update();
			}
		}
	} // class DescriptivesUOPKeyDownHandler

}
