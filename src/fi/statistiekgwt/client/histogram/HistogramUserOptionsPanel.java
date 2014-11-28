package fi.statistiekgwt.client.histogram;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

import fi.statistiekgwt.client.DialogButton;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * @author Sylvia van Borkulo
 *
 */
public class HistogramUserOptionsPanel extends LayoutPanel 
	//implements ActionListener
{

	private HistogramView view;
	private HistogramController controller;
	private HistogramModel model;

	private DialogButton dialogButton;

	private LayoutPanel panel;
	private FlowPanel vp0;
	
	// test syl: waarschijnlijk een of meerdere van dit om de handlers te kunnen verwijderen
	// heb ik er 1 nodig voor iedere widget met handler?
	private HandlerRegistration[] handlerRegistration[];
	private HistogramUOPTouchHandler touchHandler;
	private HistogramUOPClickHandler clickHandler;
	private HistogramUOPBlurHandler blurHandler;

	// variable settings
	private Label varLabel;
	private ListBox varBox;
	private Label axisLabel;
	private ListBox axisBox;

	// bin settings
	private Label binsLabel;
	/**
	 * Box for choosing the number of bins.
	 */
	private ListBox binsBox;
	/**
	 * Separator between bin boundaries settings and number of bins setting 
	 */
	//private JSeparator separator1;
	private Label minBoundaryLabel;
	private TextBox minBoundaryField;
	private Label binWidthLabel;
	private TextBox binWidthField;
	private Label noObjectsLabel;
	private Label minValueLabel;
	private Label maxValueLabel;

	// display settings
	private Label absRelLabel;
	private RadioButton amountRadioItem;
	private RadioButton percentageRadioItem;
	private CheckBox cumulativeBox;
	/**
	 * Separator between amount/percentage settings and label positioning settings
	 */
	//private JSeparator separator4;
	private RadioButton labelUnderBinRadioItem; // labels midden onder staven
	private RadioButton labelBetweenBinsRadioItem; // labels tussen staven
	/**
	 * Separator between label positioning settings and split view setting
	 */
	//private JSeparator separator2;
	private RadioButton nextToEachOtherRadioItem;
	private RadioButton aboveEachOtherRadioItem;
	private RadioButton separateRadioItem;
	private RadioButton singleViewRadioItem;

	// split settings
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
	//private JSeparator separator3;
	private Label splitMinBoundaryLabel;
	private TextBox splitMinBoundaryField;
	private Label splitBinWidthLabel;
	private TextBox splitBinWidthField;
	private Label splitBoundariesLabel;
	private TextArea splitBoundariesArea;
	private ScrollPanel splitBoundariesAreaScrollPanel;
	private Label splitNoObjectsLabel;
	private Label splitMinValueLabel;
	private Label splitMaxValueLabel;

	private CheckBox stackModeBox;

	private Button okButton;

	private boolean splitBoundariesVisible;
	private boolean splitOptionsVisible;
	private boolean enumClasses;
	private boolean splitEnumClasses;

	public HistogramUserOptionsPanel(HistogramView view,
		HistogramController controller, HistogramModel model)
	{
		this.view = view;
		this.controller = controller;
		this.model = model;
		this.touchHandler = new HistogramUOPTouchHandler();
		this.clickHandler = new HistogramUOPClickHandler();
		this.blurHandler = new HistogramUOPBlurHandler();

		createGuiComponents();
		layoutGuiComponents();

		dialogButton = new DialogButton(
			StatistiekGWT.rb.getString("settingsButton"), this.panel);
		dialogButton.addClickHandler(clickHandler);//addActionListener(this);

		//panel.addComponentListener(dialogButton);
	}

	private void createGuiComponents()
	{
		this.panel = new LayoutPanel();//new JPanel(new FlowLayout());
		//this.panel.setBackground(CssColor.make(230, 230, 230));

		this.splitVarLabel = new Label(
			StatistiekGWT.rb.getString("splitvariableLabel"));

		// var settings
		this.varLabel = new Label(StatistiekGWT.rb.getString("variableLabel"));

		this.varBox = new ListBox();
		this.varBox.setPixelSize(100, 25); // was: setPreferredSize()
		//this.varBox.setActionCommand("varBox");
		this.varBox.addClickHandler(this.clickHandler);//addActionListener(this.controller);

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
		//this.axisBox.setActionCommand("axisBox");
		this.axisBox.addClickHandler(this.clickHandler);//addActionListener(this.controller);

		// bin settings
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
		//this.binsBox.setActionCommand("binsBox");
		this.binsBox.addClickHandler(this.clickHandler);//addActionListener(this.controller);
		
//		this.separator1 = new JSeparator();
//		this.separator1.setBorder(BorderFactory
//			.createEtchedBorder(EtchedBorder.LOWERED));
//		this.separator1.setMaximumSize(new Dimension(140, 3));

		this.minBoundaryLabel = new Label(
			StatistiekGWT.rb.getString("startvalueLabel"));

		this.minBoundaryField = new TextBox();
		//this.minBoundaryField.setActionCommand("minBoundary");
		this.minBoundaryField.addClickHandler(this.clickHandler);//addActionListener(controller);
		this.minBoundaryField.addBlurHandler(this.blurHandler);//addFocusListener(controller);

		this.binWidthLabel = new Label(
			StatistiekGWT.rb.getString("classwidthLabel"));

		this.binWidthField = new TextBox();
		//this.binWidthField.setActionCommand("binWidth");
		this.binWidthField.addClickHandler(this.clickHandler);//addActionListener(controller);
		this.binWidthField.addBlurHandler(this.blurHandler);//addFocusListener(controller);

		this.noObjectsLabel = new Label("");

		this.minValueLabel = new Label("");

		this.maxValueLabel = new Label("");

		// display settings
		this.absRelLabel = new Label(StatistiekGWT.rb.getString("absRelLabel"));

		this.amountRadioItem = new RadioButton("percAmountGroup",
			StatistiekGWT.rb.getString("amountLabel"));
		//this.amountRadioItem.setActionCommand("amountRadioItem");
		this.amountRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);

		this.percentageRadioItem = new RadioButton("percAmountGroup",
			StatistiekGWT.rb.getString("percentageRadio"));
		//this.percentageRadioItem.setActionCommand("percentageRadioItem");
		this.percentageRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);

		this.cumulativeBox = new CheckBox(
			StatistiekGWT.rb.getString("cumulativeCheckbox"), false);
		//this.cumulativeBox.setActionCommand("cumulativeBox");
		this.cumulativeBox.addClickHandler(this.clickHandler);//addActionListener(controller);

//		this.separator4 = new JSeparator();
//		this.separator4.setBorder(BorderFactory
//			.createEtchedBorder(EtchedBorder.LOWERED));
//		this.separator4.setMaximumSize(new Dimension(140, 3));

		// radiobuttons for position labels
		this.labelBetweenBinsRadioItem = new RadioButton("labelPositionGroup",
			StatistiekGWT.rb.getString("labelBetweenBinsRadio"));
		//this.labelBetweenBinsRadioItem.setActionCommand("labelsBetweenBinsRadioItem");
		this.labelBetweenBinsRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);

		this.labelUnderBinRadioItem = new RadioButton("labelPositionGroup",
			StatistiekGWT.rb.getString("labelUnderBinRadio"));
		//this.labelUnderBinRadioItem.setActionCommand("labelsUnderBinRadioItem");
		this.labelUnderBinRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);

//		this.separator2 = new JSeparator();
//		this.separator2.setBorder(BorderFactory
//			.createEtchedBorder(EtchedBorder.LOWERED));
//		this.separator2.setMaximumSize(new Dimension(140, 3));

		this.nextToEachOtherRadioItem = new RadioButton("splitViewGroup",
			StatistiekGWT.rb.getString("nextToEachOtherRadioItem"));
		//this.nextToEachOtherRadioItem.setActionCommand("nextToEachOther");
		this.nextToEachOtherRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);

		this.aboveEachOtherRadioItem = new RadioButton("splitViewGroup",
			StatistiekGWT.rb.getString("aboveEachOtherRadioItem"));
		//this.aboveEachOtherRadioItem.setActionCommand("aboveEachOther");
		this.aboveEachOtherRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);

		this.separateRadioItem = new RadioButton("splitViewGroup",
			StatistiekGWT.rb.getString("separateFromEachOtherRadioItem"));
		//this.separateRadioItem.setActionCommand("separateFromEachOther");
		this.separateRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);

		this.singleViewRadioItem = new RadioButton("splitViewGroup",
			StatistiekGWT.rb.getString("splitsingleviewCheckBox"));
		//this.singleViewRadioItem.setActionCommand("splitSingleView");
		this.singleViewRadioItem.addClickHandler(this.clickHandler);//addActionListener(this.controller);

		// split settings
		this.splitButton = new Button(
			StatistiekGWT.rb.getString("splitoptionsButton"));
		//this.splitButton.setActionCommand("splitButton");
		this.splitButton.addClickHandler(this.clickHandler);//addActionListener(this);

		this.splitVarLabel = new Label(
			StatistiekGWT.rb.getString("splitvariableLabel"));

		this.splitVarBox = new ListBox();
		//this.splitVarBox.setActionCommand("splitVarBox");
		// changes in split variable have GUI consequences,
		// so action is performed by the userOptionsPanel
		// test syl: dit geldt toch voor alle acties in uop?
		this.splitVarBox.addClickHandler(this.clickHandler);//addActionListener(this);

		this.splitBinsLabel = new Label(
			StatistiekGWT.rb.getString("noClassesLabel"));

		this.splitBinsBox = new ListBox();
		for (int i = 0; i < options1.length; i++)
		{
			this.splitBinsBox.addItem(options1[i].toString());
		}
		//this.splitBinsBox.setActionCommand("splitBinsBox");
		this.splitBinsBox.addClickHandler(this.clickHandler);//addActionListener(this.controller);

		this.splitChooseBoundariesButton = new Button(
			StatistiekGWT.rb.getString("binsButton"));
//		this.splitChooseBoundariesButton
//			.setActionCommand("splitChooseBinsButton");
		this.splitChooseBoundariesButton.addClickHandler(this.clickHandler);//addActionListener(this);

//		this.separator3 = new JSeparator();
//		this.separator3.setBorder(BorderFactory
//			.createEtchedBorder(EtchedBorder.LOWERED));
//		this.separator3.setMaximumSize(new Dimension(140, 3));

		this.splitMinBoundaryLabel = new Label(
			StatistiekGWT.rb.getString("startvalueLabel"));

		this.splitMinBoundaryField = new TextBox();
		//this.splitMinBoundaryField.setActionCommand("splitMinBoundary");
		this.splitMinBoundaryField.addClickHandler(this.clickHandler);//addActionListener(controller);
		this.splitMinBoundaryField.addBlurHandler(this.blurHandler);
//		this.splitMinBoundaryField.addFocusListener(new FocusListener()
//		{
//			@Override
//			public void focusLost(FocusEvent e)
//			{
//				HistogramUserOptionsPanel.this.controller.updateSplitBoundariesFromBinSettings();
//			}
//			
//			@Override
//			public void focusGained(FocusEvent e)
//			{
//			}
//		});

		this.splitBinWidthLabel = new Label(
			StatistiekGWT.rb.getString("classwidthLabel"));

		this.splitBinWidthField = new TextBox();
		//this.splitBinWidthField.setActionCommand("splitBinWidth");
		this.splitBinWidthField.addClickHandler(this.clickHandler);//addActionListener(controller);
		this.splitBinWidthField.addBlurHandler(this.blurHandler);
//		this.splitBinWidthField.addFocusListener(new FocusListener()
//		{
//			@Override
//			public void focusLost(FocusEvent e)
//			{
//				HistogramUserOptionsPanel.this.controller.updateSplitBoundariesFromBinSettings();
//			}
//			
//			@Override
//			public void focusGained(FocusEvent e)
//			{
//			}
//		});

		this.splitBoundariesLabel = new Label(
			StatistiekGWT.rb.getString("binsButton"));

		this.splitBoundariesArea = new TextArea();
		this.splitBoundariesArea.setEnabled(false);
		this.splitBoundariesAreaScrollPanel = new ScrollPanel(
			this.splitBoundariesArea);

		this.splitNoObjectsLabel = new Label("");

		this.splitMinValueLabel = new Label("");

		this.splitMaxValueLabel = new Label("");

		this.stackModeBox = new CheckBox(
			StatistiekGWT.rb.getString("stackfrequencypolygonsCheckbox"), true);
		this.stackModeBox.setVisible(this.model.isFrequencyPolygonMode());
		//this.stackModeBox.setActionCommand("stackMode");
		this.stackModeBox.addClickHandler(this.clickHandler);//addActionListener(this.controller);

		// ok-cancel
		this.okButton = new Button("OK");
		this.okButton.addClickHandler(this.clickHandler);//addActionListener(this);
	}

	private void layoutGuiComponents()
	{
		// Variable
		HorizontalPanel hp0, hp1, hp2, hp3, hp3a, hp4, hp5, hp6, hp7, hp8, hp8a, 
			hp9, hp10, hp11, hp12, hp13, hp14;
		// VerticalPanel geeft problemen met de gegenereerde HTML
		// zie: http://mechanitis.blogspot.nl/2011/01/gwt-why-verticalpanel-is-evil.html
		FlowPanel vp1, vp2, vp3, vp4, vp5, vp6, vp7;  

		hp2 = new HorizontalPanel();
		hp2.add(varBox);
		// create some extra space after varBox
//		hp2.add(Box.createRigidArea(new Dimension(50, 25)));

		hp3 = new HorizontalPanel();
		hp3.add(axisLabel);

		hp4 = new HorizontalPanel();
		hp4.add(axisBox);

		vp1 = new FlowPanel();
		vp1.setTitle(StatistiekGWT.rb.getString("variableLabel"));
		vp1.add(hp2);
		vp1.add(hp3);
		vp1.add(hp4);

		// Bins
		hp1 = new HorizontalPanel();
		hp1.add(this.minBoundaryLabel);
		hp1.add(this.minBoundaryField);

		hp2 = new HorizontalPanel();
		hp2.add(this.binWidthLabel);
		hp2.add(this.binWidthField);

//		hb3.add(separator1);

		hp4 = new HorizontalPanel();
		hp4.add(binsLabel);

		hp5 = new HorizontalPanel();
		hp5.add(binsBox);

		hp6 = new HorizontalPanel();
		hp6.add(this.noObjectsLabel);

		hp7 = new HorizontalPanel();
		hp7.add(this.minValueLabel);

		hp8 = new HorizontalPanel();
		hp8.add(this.maxValueLabel);

		vp2 = new FlowPanel();
		vp2.setTitle(StatistiekGWT.rb.getString("classDivisionLabel"));
		vp2.add(hp1);
		vp2.add(hp2);
		vp2.add(hp4);
		vp2.add(hp5);
		vp2.add(hp6);
		vp2.add(hp7);
		vp2.add(hp8);

		// Display
		hp1 = new HorizontalPanel();
		hp1.add(absRelLabel);

		hp2 = new HorizontalPanel();
		hp2.add(amountRadioItem);

		hp3 = new HorizontalPanel();
		hp3.add(percentageRadioItem);

		hp3a = new HorizontalPanel();
		hp3a.add(cumulativeBox);

		hp4 = new HorizontalPanel();
//		hp4.add(separator4);

		hp5 = new HorizontalPanel();
		hp5.add(labelBetweenBinsRadioItem);

		hp6 = new HorizontalPanel();
		hp6.add(labelUnderBinRadioItem);

//		hp7.add(separator2);

		hp8 = new HorizontalPanel();
		hp8.add(separateRadioItem);

		if (this.model.isFrequencyPolygonMode())
		{
			hp8a = new HorizontalPanel();
			hp8a.add(singleViewRadioItem);
		}
		else
		{
			hp8a = null;
		}

		if (!this.model.isFrequencyPolygonMode())
		{
			hp9 = new HorizontalPanel();
			hp9.add(aboveEachOtherRadioItem);
	
			hp10 = new HorizontalPanel();
			hp10.add(nextToEachOtherRadioItem);
		}
		else
		{
			hp9 = null;
			hp10 = null;
		}

		vp3 = new FlowPanel();
		vp3.setTitle(StatistiekGWT.rb.getString("absRelLabel"));
		vp3.add(hp2);
		vp3.add(hp3);
		if (this.model.isFrequencyPolygonMode())
			vp3.add(hp3a);
		vp3.add(hp4);
		vp3.add(hp5);
		vp3.add(hp6);
		vp3.add(hp7);
		vp3.add(hp8);
		if (this.model.isFrequencyPolygonMode())
			vp3.add(hp8a);
		else
		{
			vp3.add(hp9);
			vp3.add(hp10);
		}

		// splitOptions
		hp1 = new HorizontalPanel();
		hp1.add(splitButton);

		hp2 = new HorizontalPanel();
		hp2.add(splitVarLabel);

		hp3 = new HorizontalPanel();
		hp3.add(splitVarBox);

		hp4 = new HorizontalPanel();
		hp4.add(splitBinsLabel);

		hp5 = new HorizontalPanel();
		hp5.add(splitBinsBox);

//		hp6.add(separator3);

		hp7 = new HorizontalPanel();
		hp7.add(splitChooseBoundariesButton);

		hp8 = new HorizontalPanel();
		hp8.add(this.splitMinBoundaryLabel);
		hp8.add(this.splitMinBoundaryField);

		hp9 = new HorizontalPanel();
		hp9.add(this.splitBinWidthLabel);
		hp9.add(this.splitBinWidthField);

		hp10 = new HorizontalPanel();
		hp10.add(this.splitBoundariesLabel);

		hp11 = new HorizontalPanel();
		hp11.add(this.splitBoundariesAreaScrollPanel);

		hp12 = new HorizontalPanel();
		hp12.add(this.splitNoObjectsLabel);

		hp13 = new HorizontalPanel();
		hp13.add(this.splitMinValueLabel);

		hp14 = new HorizontalPanel();
		hp14.add(this.splitMaxValueLabel);

		vp4 = new FlowPanel();
		vp4.setTitle(StatistiekGWT.rb.getString("splitsLabel"));
		vp4.add(hp1);
		vp4.add(hp2);
		vp4.add(hp3);
		vp4.add(hp4);
		vp4.add(hp5);
		vp4.add(hp6);
		vp4.add(hp7);
		vp4.add(hp8);
		vp4.add(hp9);
		vp4.add(hp10);
		vp4.add(hp11);
		vp4.add(hp12);
		vp4.add(hp13);
		vp4.add(hp14);

		hp0 = new HorizontalPanel();
		hp0.add(vp1);
		hp0.add(vp2);
		hp0.add(vp3);
		hp0.add(vp4);

		vp0 = new FlowPanel();
		vp0.add(hp0);
		vp0.add(okButton);

		panel.add(vp0);
		init();
	}

	public void resize(Panel p)
	{
		//System.out.println("HistogramUserOptionsPanel.resize(): p = " + p.toString());
		int w = p.getOffsetWidth();
		int h = p.getOffsetHeight();
		this.panel.setPixelSize(w + 10, h);
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
		//this.binsBox.addActionListener(this.controller);
		/*
		 * boolean b = this.model.columnIndexValid(); if(b) { AllowedTypes type
		 * =
		 * this.model.getTableModel().getColumnTypes().get(this.model.getColumnIndex
		 * ()).getType(); b = !(type.equals(AllowedTypes.DOUBLE) ||
		 * type.equals(AllowedTypes.INTEGER)); this.binsBox.setVisible(!b);
		 * this.binsLabel.setVisible(!b); if(b) this.setEnumClasses(true); }
		 * else { this.binsBox.setVisible(false);
		 * this.binsLabel.setVisible(false);
		 * this.setVisibleBoundaryOptions(false); }
		 */
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
				this.minBoundaryField.setText(StatistiekGWT.df.format(this.model
					.getBinBoundaries().get(0)));
				Double d = this.model.getBinBoundaries().get(1)
					- this.model.getBinBoundaries().get(0);
				this.binWidthField.setText(StatistiekGWT.df.format(d));
				this.noObjectsLabel.setText(StatistiekGWT.rb
					.getString("numberLabel")
					+ this.model.getStatTableModel().getRowCount());
				this.minValueLabel.setText(StatistiekGWT.rb.getString("minLabel")
					+ this.model.getStatTableModel().getColumnMin(
						this.model.getColumnIndex()));
				this.maxValueLabel.setText(StatistiekGWT.rb.getString("maxLabel")
					+ this.model.getStatTableModel().getColumnMax(
						this.model.getColumnIndex()));
//				this.separator1.getParent().setVisible(true);
//				this.separator4.getParent().setVisible(true);
				this.labelBetweenBinsRadioItem.getParent().setVisible(true);
				this.labelUnderBinRadioItem.getParent().setVisible(true);
				this.binsBox.getParent().setVisible(true);
				this.binsLabel.getParent().setVisible(true);
				setEnumClasses(false);
			}
			else if (type.equals(AllowedTypes.ENUM) || type.equals(AllowedTypes.STRING))
			{
//				this.separator1.getParent().setVisible(false);
//				this.separator4.getParent().setVisible(false);
				this.labelBetweenBinsRadioItem.getParent().setVisible(false);
				this.labelUnderBinRadioItem.getParent().setVisible(false);
				this.binsBox.getParent().setVisible(false);
				this.binsLabel.getParent().setVisible(false);
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
    				this.splitMinBoundaryField.setText(StatistiekGWT.df
    					.format(this.model.getSplitOptions().getBinBoundaries()
    						.get(0)));
    				Double d = this.model.getSplitOptions().getBinBoundaries()
    					.get(1)
    					- this.model.getSplitOptions().getBinBoundaries().get(0);
    				this.splitBinWidthField.setText(StatistiekGWT.df.format(d));
    				
    				StringBuilder sb = new StringBuilder();
    				for (int i = 0; i < this.model.getSplitOptions()
    					.getBinBoundaries().size() - 1; i++)
    				{
    					sb.append(StatistiekGWT.df.format(this.model.getSplitOptions()
    						.getBinBoundaries().get(i)));
    					sb.append(" - ");
    					sb.append(StatistiekGWT.df.format(this.model.getSplitOptions()
    						.getBinBoundaries().get(i + 1)));
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
    				this.splitBinsBox.getParent().setVisible(true);
    				this.splitBinsLabel.getParent().setVisible(true);
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
    				this.splitBinsBox.getParent().setVisible(false);
    				this.splitBinsLabel.getParent().setVisible(false);
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

		boolean split = this.model.getSplitOptions().getColumnSplitIndex() > -1;
		this.setVisibleSplitOptions(split);
		
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
		setVisibleBoundaryOptions();
		setVisibleSplitBoundaryOptions(false);
		if (vp0 != null)
			resize(vp0);

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
		enumClasses = b;
		
		// vertical box containing bin settings not visible for enum variable 
		binWidthLabel.getParent().getParent().setVisible(!b);

		// set visibility of the components on the vertical box
		//separator4.getParent().setVisible(!b);
		minBoundaryLabel.getParent().setVisible(!b);
		minBoundaryField.getParent().setVisible(!b);
		binWidthLabel.getParent().setVisible(!b);
		binWidthField.getParent().setVisible(!b);
		noObjectsLabel.getParent().setVisible(!b);
		minValueLabel.getParent().setVisible(!b);
		maxValueLabel.getParent().setVisible(!b);
		resize(vp0);
	}

	private void setSplitEnumClasses(boolean b)
	{
		splitEnumClasses = b;
		splitMinBoundaryLabel.getParent().setVisible(
			splitBoundariesVisible && !b);
		splitMinBoundaryField.getParent().setVisible(
			splitBoundariesVisible && !b);
		splitBinWidthLabel.getParent().setVisible(splitBoundariesVisible && !b);
		splitBinWidthField.getParent().setVisible(splitBoundariesVisible && !b);
		splitNoObjectsLabel.getParent()
			.setVisible(splitBoundariesVisible && !b);
		splitMinValueLabel.getParent().setVisible(splitBoundariesVisible && !b);
		splitMaxValueLabel.getParent().setVisible(splitBoundariesVisible && !b);
		resize(vp0);
	}

	private void setVisibleBoundaryOptions()
	{
		//separator1.getParent().setVisible(!enumClasses);
		minBoundaryLabel.getParent().setVisible(!enumClasses);
		minBoundaryField.getParent().setVisible(!enumClasses);
		binWidthLabel.getParent().setVisible(!enumClasses);
		binWidthField.getParent().setVisible(!enumClasses);
		noObjectsLabel.getParent().setVisible(!enumClasses);
		minValueLabel.getParent().setVisible(!enumClasses);
		maxValueLabel.getParent().setVisible(!enumClasses);
	}

	private void setVisibleSplitBoundaryOptions(boolean b)
	{
		splitBoundariesVisible = b;
		//separator3.getParent().setVisible(b);
		splitMinBoundaryLabel.getParent().setVisible(b && !splitEnumClasses);
		splitMinBoundaryField.getParent().setVisible(b && !splitEnumClasses);
		splitBinWidthLabel.getParent().setVisible(b && !splitEnumClasses);
		splitBinWidthField.getParent().setVisible(b && !splitEnumClasses);
		splitBoundariesLabel.getParent().setVisible(b);
		splitBoundariesArea.getParent().setVisible(b);
		splitBoundariesAreaScrollPanel.getParent().setVisible(b);
		splitNoObjectsLabel.getParent().setVisible(b && !splitEnumClasses);
		splitMinValueLabel.getParent().setVisible(b && !splitEnumClasses);
		splitMaxValueLabel.getParent().setVisible(b && !splitEnumClasses);
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

	private void setVisibleSplitOptions(boolean b)
	{
		splitOptionsVisible = b;
		//separator2.getParent().setVisible(b);
		// splitSingleViewBox.getParent().setVisible(b);
		
		if (this.model.isFrequencyPolygonMode())
		{
			singleViewRadioItem.getParent().setVisible(b);
		}
		else
		{
			nextToEachOtherRadioItem.getParent().setVisible(b);
			aboveEachOtherRadioItem.getParent().setVisible(b);
		}
		separateRadioItem.getParent().setVisible(b);
		splitVarLabel.getParent().setVisible(b);
		splitVarBox.getParent().setVisible(b);
		if (!b)
			splitBinsLabel.getParent().setVisible(b);
		if (!b)
			splitBinsBox.getParent().setVisible(b);
		splitChooseBoundariesButton.getParent().setVisible(b);
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
			}
			else if ((e.getSource() == labelBetweenBinsRadioItem) ||
				e.getSource() == labelUnderBinRadioItem)
			{
				model.setLabelUnderBin(view.labelUnderBinItemSelected());
			}
			else if (e.getSource() == varBox)
			{
				model.setColumnIndex(view.getVarBoxSelectedIndex());
				//System.out.println("Var set to " + this.model.getColumnIndex());
			}
			else if (e.getSource() == binsBox)
			{
				model.setNoBins(view.getBinsBoxSelectedInt());
			}
			else if (e.getSource() == minBoundaryField)
			{
				controller.updateBoundariesFromBinSettings();
			}
			else if (e.getSource() == binWidthField)
			{
				controller.updateBoundariesFromBinSettings();
			}
			else if (e.getSource() == axisBox)
			{
				model.setVerticalBars(view.xAxisSelected());
			}
			else if (e.getSource() == cumulativeBox)
			{
				model.setFrequencyPolygonCumulativeMode(
					view.isCumulativeBoxSelected());
			}
			else if (e.getSource() == singleViewRadioItem)
			{
				model.setSplitInSingleView(view.isSplitSingleViewSelected());
			}
			else if ((e.getSource() == aboveEachOtherRadioItem)
				&& !model.isFrequencyPolygonMode())
			{
				model.setSplitInSingleView(view.isSplitSingleViewSelected());
//				this.model
//					.setNextToEachOther(this.view.isNextToEachOtherSelected());
				// code above is not working (anymore?), so straightforward
				model.setNextToEachOther(false);			
			}
			else if ((e.getSource() == nextToEachOtherRadioItem)
				&& !model.isFrequencyPolygonMode())
			{
				model.setSplitInSingleView(view.isSplitSingleViewSelected());
//				this.model
//					.setNextToEachOther(this.view.isNextToEachOtherSelected());
				// code above is not working (anymore?), so straightforward
				model.setNextToEachOther(true);
			}
			else if (e.getSource() == separateRadioItem)
			{
				model.setSplitInSingleView(false);
			}
			else if (e.getSource() == stackModeBox)
			{
				model.setFrequencyPolygonStackMode(view.isStackModeBoxSelected());
			}
			else if (e.getSource() == splitBinsBox)
			{
				ArrayList<ColumnType> list = model.getStatTableModel().getColumnTypes();
				controller.setSplitType(list.get(model.getSplitOptions().getColumnSplitIndex())
					.getType());
			}
			else if (e.getSource() == splitMinBoundaryField)
			{
				controller.updateSplitBoundariesFromBinSettings();
			}
			else if (e.getSource() == splitBinWidthField)
			{
				controller.updateSplitBoundariesFromBinSettings();
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

				resize(vp0);
			}
			else if (e.getSource() == splitButton)
			{
				if (splitOptionsVisible)
				{
					setVisibleSplitOptions(false);
					HistogramUserOptionsPanel.this.clearGUISplitComponents();
				}
				else
				{
					setVisibleSplitOptions(true);
				}
				resize(vp0);
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
	} // class HistogramUOPBlurHandler

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
		}
	} // class HistogramUOPBlurHandler
}
