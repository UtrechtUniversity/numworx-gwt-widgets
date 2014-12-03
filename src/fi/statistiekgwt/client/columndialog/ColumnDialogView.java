package fi.statistiekgwt.client.columndialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.columndialog.ColumnDialogController.ColumnDialogChangeHandler;
import fi.statistiekgwt.client.columndialog.ColumnDialogController.ColumnDialogValueChangeHandler;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;

/**
 * View for add column dialog
 * 
 * @author Manu Drijvers
 * 
 */
public class ColumnDialogView extends DialogBox// implements Observer
{
	private ColumnDialogModel model;

	private LayoutPanel alles;

	private Label kiesNaam;
	private TextBox nameField;

	private Label kiesType;
	private ListBox typeBox;
	private LayoutPanel setTypePanel;

	private DockLayoutPanel createEnumPanel;
	private LayoutPanel addEnumElementPanel;
	private Label addEnumElementLabel;
	private TextBox addEnumElementField;
	// private JTextArea enumElementsView;
	private ListBox enumElementsList; // was: OrderableJList
	private ArrayList<String> stringOptions;
	private AllowedTypes originalColumnType;
	private LayoutPanel enumScrollPanel; // was: JScrollPane
	/**
	 * Panel with 'remove selected' and 'remove all' buttons for enumeration.
	 */
	private LayoutPanel enumSouthPanel;
	/**
	 * Panel with move up/down and sort buttons for enumeration.
	 */
	private LayoutPanel enumEastPanel;
	private Button removeSelectedElement;
	private Button removeAllElements;
	private Button sortElements;
	private PushButton moveElementUp;
	private PushButton moveElementDown;

	private LayoutPanel typePanel;
	private LayoutPanel uitlegPanel;
	private Label uitlegLabel;
	private ScrollPanel uitlegScrollPane; // was: JScrollPane
	private TextArea uitlegArea;
	
	/**
	 * Panel with OK/Cancel buttons.
	 */
	private LayoutPanel okCancelPanel;
	private Button okButton;
	private Button cancelButton;


	private String font;

	public static final int DEFAULT_WIDTH = 600;
	public static final int DEFAULT_HEIGHT = 330;

	/**
	 * Constructor.
	 * 
	 * @param model
	 *            MVC Model
	 */
	public ColumnDialogView(ColumnDialogModel model)
	{
		super(true, true);
		super.setPixelSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		super.setText(StatistiekGWT.rb.getString("addacolumn"));
		this.getElement().getStyle().setBackgroundColor("GREY");

		this.model = model;
		//this.model.addObserver(this);

		this.initialize();
	}

	/**
	 * Constructor with Frame owner
	 * 
	 * @param owner
	 *            Dialog owner
	 * @param model
	 *            MVC Model
	 */
	public ColumnDialogView(Frame owner, ColumnDialogModel model)
	{
		//super(owner, "Add a column", true);
		super(true, true);
		super.setPixelSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		super.setText(StatistiekGWT.rb.getString("addacolumn"));

		this.model = model;
		//this.model.addObserver(this);

		this.initialize();
	}

	/**
	 * Constructor with Dialog owner
	 * 
	 * @param owner
	 *            Dialog owner
	 * @param model
	 *            MVC Model
	 */
	public ColumnDialogView(DialogBox owner, ColumnDialogModel model)
	{
		//super(owner, StatistiekGWT.rb.getString("addacolumn"), true);
		super(true, true);
		super.setPixelSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		super.setText(StatistiekGWT.rb.getString("addacolumn"));

		this.model = model;
		//this.model.addObserver(this);

		this.initialize();
	}

	/**
	 * Set up GUI
	 */
	private void initialize()
	{
		//super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.font = StatistiekGWT.fontString;
//		Border border = BorderFactory.createEmptyBorder(5, 0, 5, 5);

		this.kiesNaam = new Label(StatistiekGWT.rb.getString("columnname"));
		this.nameField = new TextBox();
//		this.nameField.setActionCommand("nameField");
		this.kiesType = new Label(StatistiekGWT.rb.getString("choosetype"));

		// Use strings from text file to create listbox
		String[] types = {
			StatistiekGWT.rb.getString("integer"),
			StatistiekGWT.rb.getString("double"),
			StatistiekGWT.rb.getString("string"),
			StatistiekGWT.rb.getString("enum")
		};
		this.typeBox = new ListBox();
		for (int i = 0; i < types.length; i++)
		{
			this.typeBox.addItem(types[i]);
		}
		setTypeBox();
//		this.typeBox.setActionCommand("typeBox");

		this.setTypePanel = new LayoutPanel();
		this.setTypePanel.add(this.kiesNaam);
		this.setTypePanel.add(this.nameField);
		this.setTypePanel.add(this.kiesType);
		this.setTypePanel.add(this.typeBox);
		// set position
		this.setTypePanel.setWidgetLeftWidth(this.kiesNaam, 0, Style.Unit.PCT, 50, Style.Unit.PCT);
		this.setTypePanel.setWidgetTopHeight(this.kiesNaam, 0, Style.Unit.PX, 30, Style.Unit.PX);
		this.setTypePanel.setWidgetLeftWidth(this.nameField, 50, Style.Unit.PCT, 50, Style.Unit.PCT);
		this.setTypePanel.setWidgetTopHeight(this.nameField, 0, Style.Unit.PX, 30, Style.Unit.PX);
		this.setTypePanel.setWidgetLeftWidth(this.kiesType, 0, Style.Unit.PCT, 50, Style.Unit.PCT);
		this.setTypePanel.setWidgetTopHeight(this.kiesType, 30, Style.Unit.PX, 30, Style.Unit.PX);
		this.setTypePanel.setWidgetLeftWidth(this.typeBox, 50, Style.Unit.PCT, 50, Style.Unit.PCT);
		this.setTypePanel.setWidgetTopHeight(this.typeBox, 30, Style.Unit.PX, 30, Style.Unit.PX);

		this.addEnumElementLabel = new Label(
			StatistiekGWT.rb.getString("addenumeration"));
		this.addEnumElementField = new TextBox();
//		this.addEnumElementField.setActionCommand("addEnumElementField");
		this.addEnumElementPanel = new LayoutPanel();
//		this.addEnumElementPanel.setLayout(new GridLayout(2, 1));
		this.addEnumElementPanel.add(this.addEnumElementLabel);
		this.addEnumElementPanel.add(this.addEnumElementField);
		// set position
		this.addEnumElementPanel.setWidgetLeftWidth(this.addEnumElementLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.addEnumElementPanel.setWidgetTopHeight(this.addEnumElementLabel, 0, Style.Unit.PX, 30, Style.Unit.PX);
		this.addEnumElementPanel.setWidgetLeftWidth(this.addEnumElementField, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.addEnumElementPanel.setWidgetTopHeight(this.addEnumElementField, 30, Style.Unit.PX, 30, Style.Unit.PX);

		/*
		 * this.enumElementsView = new JTextArea();
		 * this.enumElementsView.setFont(this.font);
		 * this.enumElementsView.setEditable(false);
		 */
//		this.enumElementsList = new OrderableJList(new OrderableJListModel(
//			this.model.getEnumOptions()));
		this.enumElementsList = new ListBox();
		ArrayList<String> list = this.model.getEnumOptions();
		for (int i = 0; i < list.size(); i++)
		{
			this.enumElementsList.addItem(list.get(i));
		}
		this.enumScrollPanel = new LayoutPanel();//new ScrollPanel(this.enumElementsList);
		this.enumScrollPanel.add(this.enumElementsList);
		// set position
		this.enumScrollPanel.setWidgetLeftWidth(this.enumElementsList, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.enumScrollPanel.setWidgetTopHeight(this.enumElementsList, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		
		this.removeSelectedElement = new Button(
			StatistiekGWT.rb.getString("removeselectedelement"));
//		this.removeSelectedElement.setActionCommand("removeSelectedElement");

		this.removeAllElements = new Button(
			StatistiekGWT.rb.getString("removeAllElements"));
//		this.removeAllElements.setActionCommand("removeAllElements");
		
		this.enumSouthPanel = new LayoutPanel();
		this.enumSouthPanel.add(this.removeSelectedElement);
		this.enumSouthPanel.add(this.removeAllElements);
		// set position
		this.enumSouthPanel.setWidgetLeftWidth(this.removeSelectedElement, 0, Style.Unit.PCT, 50, Style.Unit.PCT);
		this.enumSouthPanel.setWidgetTopHeight(this.removeSelectedElement, 0, Style.Unit.PX, 30, Style.Unit.PX);
		this.enumSouthPanel.setWidgetLeftWidth(this.removeAllElements, 50, Style.Unit.PCT, 50, Style.Unit.PCT);
		this.enumSouthPanel.setWidgetTopHeight(this.removeAllElements, 0, Style.Unit.PX, 30, Style.Unit.PX);
		
		this.sortElements = new Button(
			StatistiekGWT.rb.getString("sortElements"));
//		this.sortElements.setForeground(ColorGenerator.BUTTON_TEXT_GREY);
//		this.sortElements.setActionCommand("sortElements");
		this.sortElements.setTitle(StatistiekGWT.rb.getString("sortElementsTooltip"));// tooltip

//		ImageResource imageResource = //StatistiekGWT.class.getResource("resources/arrow-137-16_525252up.gif");
		this.moveElementUp = new PushButton(new Image("resources/arrow-137-16_525252up.gif"));
//		this.moveElementUp.setActionCommand("moveElementUp");
		this.moveElementUp.setTitle(StatistiekGWT.rb.getString("moveElementUpTooltip"));

		this.moveElementDown = new PushButton(new Image("resources/arrow-199-16_525252down.gif"));
//		this.moveElementDown.setActionCommand("moveElementDown");
		this.moveElementDown.setTitle(StatistiekGWT.rb.getString("moveElementDownTooltip"));

		this.enumEastPanel = new LayoutPanel();
//		this.enumEastPanel.setLayout(new GridLayout(3, 1, 0, 10));
		this.enumEastPanel.add(this.moveElementUp);
		this.enumEastPanel.add(this.moveElementDown);
		this.enumEastPanel.add(this.sortElements);
		// set position
		this.enumEastPanel.setWidgetLeftWidth(this.moveElementUp, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.enumEastPanel.setWidgetTopHeight(this.moveElementUp, 0, Style.Unit.PX, 30, Style.Unit.PX);
		this.enumEastPanel.setWidgetLeftWidth(this.moveElementDown, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.enumEastPanel.setWidgetTopHeight(this.moveElementDown, 30, Style.Unit.PX, 30, Style.Unit.PX);
		this.enumEastPanel.setWidgetLeftWidth(this.sortElements, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.enumEastPanel.setWidgetTopHeight(this.sortElements, 60, Style.Unit.PX, 30, Style.Unit.PX);

//		this.createEnumPanel = new LayoutPanel();
		this.createEnumPanel = new DockLayoutPanel(Unit.PX);
//		this.createEnumPanel.setLayout(new BorderLayout());
		this.createEnumPanel.addNorth(this.addEnumElementPanel, 60);//, BorderLayout.NORTH);
		this.createEnumPanel.addEast(this.enumEastPanel, 30);//, BorderLayout.EAST);
		this.createEnumPanel.addSouth(this.enumSouthPanel, 30);//, BorderLayout.SOUTH);
		this.createEnumPanel.add(this.enumScrollPanel);//, BorderLayout.CENTER);
		this.createEnumPanel.setPixelSize(150, 240);
		this.createEnumPanel.getElement().getStyle().setBackgroundColor("yellow");
		// set position for this.createEnumPanel = LayoutPanel
//		this.createEnumPanel.setWidgetLeftWidth(this.addEnumElementPanel, 0, Style.Unit.PCT, 80, Style.Unit.PCT);
//		this.createEnumPanel.setWidgetTopHeight(this.addEnumElementPanel, 0, Style.Unit.PX, 60, Style.Unit.PX);
//		this.createEnumPanel.setWidgetLeftWidth(this.enumScrollPanel, 0, Style.Unit.PCT, 80, Style.Unit.PCT);
//		this.createEnumPanel.setWidgetTopHeight(this.enumScrollPanel, 60, Style.Unit.PX, 150, Style.Unit.PX);
//		this.createEnumPanel.setWidgetLeftWidth(this.enumEastPanel, 80, Style.Unit.PCT, 20, Style.Unit.PCT);
//		this.createEnumPanel.setWidgetTopHeight(this.enumEastPanel, 60, Style.Unit.PX, 150, Style.Unit.PX);
//		this.createEnumPanel.setWidgetLeftWidth(this.enumSouthPanel, 0, Style.Unit.PCT, 80, Style.Unit.PCT);
//		this.createEnumPanel.setWidgetTopHeight(this.enumSouthPanel, 210, Style.Unit.PX, 30, Style.Unit.PX);
		
		this.typePanel = new LayoutPanel();
//		this.typePanel.setLayout(new BorderLayout());
		this.typePanel.add(this.setTypePanel);//, BorderLayout.NORTH);
		this.typePanel.add(this.createEnumPanel);//, BorderLayout.CENTER);
		// test syl: even niet het onzichtbaar blijven createEnumPanel, maar alleen het noord-deel
//		this.typePanel.add(this.enumScrollPanel);//, BorderLayout.CENTER);
		
		// set position
		this.typePanel.setWidgetLeftWidth(this.setTypePanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.typePanel.setWidgetTopHeight(this.setTypePanel, 0, Style.Unit.PX, 60, Style.Unit.PX);
		this.typePanel.setWidgetLeftWidth(this.createEnumPanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.typePanel.setWidgetTopHeight(this.createEnumPanel, 60, Style.Unit.PX, 240, Style.Unit.PX);
//		this.typePanel.setWidgetLeftWidth(this.enumScrollPanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
//		this.typePanel.setWidgetTopHeight(this.enumScrollPanel, 60, Style.Unit.PX, 240, Style.Unit.PX);
		
		this.uitlegLabel = new Label(StatistiekGWT.rb.getString("uitlegbijkolom"));
		this.uitlegArea = new TextArea();
		this.uitlegScrollPane = new ScrollPanel(this.uitlegArea);
		this.uitlegPanel = new LayoutPanel();
//		this.uitlegPanel.setLayout(new BorderLayout());
		this.uitlegPanel.add(this.uitlegLabel);//, BorderLayout.NORTH);
		this.uitlegPanel.add(this.uitlegArea);//, BorderLayout.CENTER);
		// set position
		this.uitlegPanel.setWidgetLeftWidth(this.uitlegLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.uitlegPanel.setWidgetTopHeight(this.uitlegLabel, 0, Style.Unit.PX, 30, Style.Unit.PX);
		this.uitlegPanel.setWidgetLeftWidth(this.uitlegArea, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.uitlegPanel.setWidgetTopHeight(this.uitlegArea, 30, Style.Unit.PX, 240, Style.Unit.PX);

		this.okCancelPanel = new LayoutPanel();
		this.okButton = new Button(StatistiekGWT.rb.getString("OKButtonText"));
//		this.doneButton.setActionCommand("doneButton");
		this.cancelButton = new Button(StatistiekGWT.rb.getString("cancelButtonText"));
		this.okCancelPanel.add(this.okButton);
		this.okCancelPanel.add(this.cancelButton);
		// set position
		this.okCancelPanel.setWidgetLeftWidth(this.okButton, 25, Style.Unit.PCT, 25, Style.Unit.PCT);
		this.okCancelPanel.setWidgetTopHeight(this.okButton, 0, Style.Unit.PX, 30, Style.Unit.PX);
		this.okCancelPanel.setWidgetLeftWidth(this.cancelButton, 50, Style.Unit.PCT, 25, Style.Unit.PCT);
		this.okCancelPanel.setWidgetTopHeight(this.cancelButton, 0, Style.Unit.PX, 30, Style.Unit.PX);


//		GridLayout gl = new GridLayout(1, 2);
//		gl.setHgap(5);
		this.alles = new LayoutPanel();
		this.alles.setPixelSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
//		this.alles.setLayout(gl);
		this.alles.add(this.typePanel);
		this.alles.add(this.uitlegPanel);
		this.alles.add(this.okCancelPanel);
		// set position
		this.alles.setWidgetLeftWidth(this.typePanel, 0, Unit.PCT, 50, Unit.PCT);  // Left panel
		this.alles.setWidgetTopHeight(this.typePanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.alles.setWidgetLeftWidth(this.uitlegPanel, 50, Unit.PCT, 50, Unit.PCT); // Right panel
		this.alles.setWidgetTopHeight(this.uitlegPanel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.alles.setWidgetLeftWidth(this.okCancelPanel, 0, Unit.PCT, 100, Unit.PCT); // Bottom panel
		this.alles.setWidgetTopHeight(this.okCancelPanel, 300, Style.Unit.PX, 30, Style.Unit.PX);

		this.add(this.alles);
		
		this.originalColumnType = this.model.getType();
		this.setStringOptions();

		this.update();
	}

	private void setStringOptions()
	{
		// Voor integerkolommen (dus ook nieuwe kolom) worden bij een switch naar enum 
		// de waarden niet vooringevuld in stringoptions
		if (this.wasInteger())
		{
			this.stringOptions = new ArrayList<String>();
		}
		else if (!this.wasEnum())
		{
			this.stringOptions = this.model.getTableModel().
				getStringOptions(this.model.getColumnIndex());
		}
	}

	/**
	 * Select the column's type in the type box. 
	 */
	private void setTypeBox()
	{
		AllowedTypes type = this.model.getType(); 
		
		this.setSelectedItemTypeBox(type);
	}

	/**
	 * @return The currently selected AllowedType
	 */
	public AllowedTypes getSelectedType()
	{
		int selectedIndex = this.typeBox.getSelectedIndex();
		String typeString = (String) this.typeBox.getItemText(selectedIndex);
		AllowedTypes type = null;
		
		if (typeString.equals(StatistiekGWT.rb.getString("integer")))
			type = AllowedTypes.INTEGER;
		else if (typeString.equals(StatistiekGWT.rb.getString("double")))
			type = AllowedTypes.DOUBLE;
		else if (typeString.equals(StatistiekGWT.rb.getString("string")))
			type = AllowedTypes.STRING;
		else if (typeString.equals(StatistiekGWT.rb.getString("enum")))
			type = AllowedTypes.ENUM;
			
		return type;
	}

	/**
	 * @return The text in this.uitlegArea
	 */
	public String getUitleg()
	{
		return this.uitlegArea.getText();
	}

	/**
	 * @return Get the text in the addEnumElementField textfield
	 */
	public String getEnumOption()
	{
		return this.addEnumElementField.getText();
	}

	/**
	 * Get the string options.
	 * 
	 * @return The string options
	 */
	public ArrayList getStringOptions()
	{
		return this.stringOptions;
	}

	/**
	 * @return The text in the nameField textfield
	 */
	public String getCurrentName()
	{
		return this.nameField.getText();
	}

	/**
	 * @return The index of the selected enum item in the enum elements list
	 */
	public int getSelectedOptionInListIndex()
	{
		return this.enumElementsList.getSelectedIndex();
	}
	
	/**
	 * Set selected index in enum elements list.
	 * @param i The index to be selected
	 */
	public void setSelectedOptionInListIndex(int i)
	{
		this.enumElementsList.setSelectedIndex(i);
	}

	/**
	 * Add a clickhandler to all buttons and fields
	 * 
	 * @param handler
	 *            the attached clickhandler
	 */
	public void addClickHandlers(ClickHandler handler) // was: addActionListeners()
	{
		this.removeSelectedElement.addClickHandler(handler);
		this.removeAllElements.addClickHandler(handler);
		this.sortElements.addClickHandler(handler);
		this.moveElementUp.addClickHandler(handler);
		this.moveElementDown.addClickHandler(handler);
		this.okButton.addClickHandler(handler);
		this.cancelButton.addClickHandler(handler);
	}

	/**
	 * Add a blurhandler to text field and text area.
	 * 
	 * @param handler
	 *            the subscribing FocusListener
	 */
	public void addBlurHandlers(BlurHandler handler)//addFocusListeners(FocusListener fl)
	{
		this.nameField.addBlurHandler(handler);
		this.uitlegArea.addBlurHandler(handler);
	}
	
	public void addChangeHandlers(ColumnDialogChangeHandler handler)
	{
		this.typeBox.addChangeHandler(handler);
	}

	public void addValueChangeHandlers(
		ColumnDialogValueChangeHandler handler)
	{
		this.nameField.addValueChangeHandler(handler);
		this.addEnumElementField.addValueChangeHandler(handler);
		this.uitlegArea.addValueChangeHandler(handler);
	}

	public TextArea getUitlegArea()
	{
		return this.uitlegArea;
	}

	public TextBox getNameField()
	{
		return this.nameField;
	}

	public void clearAddEnumElementField()
	{
		this.addEnumElementField.setText("");
	}

	/**
	 * Implementation of Observer
	 */
	public void update()//(Observable o, Object arg)
	{
		// update typeBox selected item
		this.setSelectedItemTypeBox(this.model.getType());

		// set the visibility
		this.createEnumPanel.setVisible(this.model.getType().equals(
			AllowedTypes.ENUM));
		this.enumElementsList.setVisible(this.model.getType().equals(
			AllowedTypes.ENUM));

		// Update the list with options of current enumeration
		// or with string options if there is no current enumeration
		if (this.wasEnum())
		{
			this.fillEnumElementsList(this.model.getEnumOptions());
			//this.enumElementsList.setModel(new OrderableJListModel(this.model.getEnumOptions()));
		}
		else
		{
			// vul met stringOptions
			this.fillEnumElementsList(this.stringOptions);
//			this.enumElementsList.setModel(new OrderableJListModel(
//				this.stringOptions));
		}

		this.nameField.setText(this.model.getName());

		this.uitlegArea.setText(this.model.getUitleg());

		//super.validate();
	}

	/**
	 * Fill enumElementsList with the values in the given list.
	 * @param list
	 */
	private void fillEnumElementsList(ArrayList<String> list)
	{
		for (int i = 0; i < list.size(); i++)
		{
			this.enumElementsList.addItem(list.get(i));
		}
	}

	/**
	 * Set the given type selected in typeBox.
	 * @param type
	 */
	private void setSelectedItemTypeBox(AllowedTypes type)
	{
		String typeString = "";
		if (type.equals(AllowedTypes.INTEGER))
			typeString = StatistiekGWT.rb.getString("integer");
		else if (type.equals(AllowedTypes.DOUBLE))
			typeString = StatistiekGWT.rb.getString("double");
		else if (type.equals(AllowedTypes.STRING))
			typeString = StatistiekGWT.rb.getString("string");
		else if (type.equals(AllowedTypes.ENUM))
			typeString = StatistiekGWT.rb.getString("enum");

		// find the index of type
		int indexToFind = -1;
		for (int i=0; i < this.typeBox.getItemCount(); i++) 
		{
		    if (this.typeBox.getItemText(i).equals(typeString)) {
		        indexToFind = i;
		        break;
		    }
		}
		this.typeBox.setSelectedIndex(indexToFind);
	}

	public AllowedTypes getOriginalColumnType()
	{
		return this.originalColumnType;
	}

	/**
	 * Return whether the column originally was of type enumeration.
	 * @return
	 */
	private boolean wasEnum()
	{
		return this.originalColumnType.equals(AllowedTypes.ENUM);
	}

	/**
	 * Return whether the column originally was of type integer.
	 * @return
	 */
	private boolean wasInteger()
	{
		return this.originalColumnType.equals(AllowedTypes.INTEGER);
	}

	public void removeStringOption(int index)
	{
		if (index > -1)
		{
			this.stringOptions.remove(index);
		}
	}

	public void addStringOption(String s)
	{
		this.stringOptions.add(s);
	}

	/**
	 * Add the options in stringOptions to the enum options.
	 */
	public void updateEnumOptions()
	{
		for (int i = 0; i < this.stringOptions.size(); i++)
		{
			String newElement = (String) this.stringOptions.get(i);
			this.model.addEnumOptionWithoutEvent(newElement);
		}
	}

	/**
	 * Remove all string options except '*' from the string options.
	 */
	public void removeAllStringOptions()
	{
		for (int i = this.stringOptions.size() - 1; i > -1 ; i--)
		{
			if (!this.stringOptions.get(i).equals(ColumnType.WILDCARD))
			{
				this.stringOptions.remove(i);
			}
		}
	}

	/**
	 * Sort string options alphabetically ascending.
	 */
	public void sortStringOptions()
	{
		String[] sortedStringOptions = new String[this.stringOptions.size()];
		sortedStringOptions = this.stringOptions.toArray(sortedStringOptions);
		
		Arrays.sort(sortedStringOptions, new Comparator<String>() {
            @Override
            /**
             * Compare strings alphabetically. 
             * A wildcard is larger than any other string.
             * @param s1
             * @param s2
             * @return
             */
            public int compare(String s1, String s2) 
            {
            	// check for wildcard among the strings
            	if (s1.equals(ColumnType.WILDCARD))
            		return 1;
            	else if (s2.equals(ColumnType.WILDCARD))
            		return -1;
            	else 
            	{
            		// apart from '*' sort the enum options alphabetically
            		return s1.compareTo(s2);
            	}
            }
        });
		
		this.stringOptions = new ArrayList(Arrays.asList(sortedStringOptions));
	}

	/**
	 * Swap string options with index1 and index2. A wildcard is not swapped
	 * @param index1
	 * @param index2
	 */
	public void swapStringOptions(int index1, int index2)
	{
		if (this.validStringOptionsIndex(index1) && this.validStringOptionsIndex(index2)
			&& !this.stringOptions.get(index1).equals(ColumnType.WILDCARD) // the wildcard should stay at the end
			&& !this.stringOptions.get(index2).equals(ColumnType.WILDCARD))
		{
			Collections.swap(this.stringOptions, index1, index2);
		}
	}

	/**
	 * Check whether index is a valid index in string options.
	 * @param index
	 * @return True if index is a valid index, else false.
	 */
	private boolean validStringOptionsIndex(int index)
	{
		boolean isValid = false;
		
//		if ((this.enumOptions == null) || this.enumOptions.size() == 0)
//			isValid = false;
//		else 
			if ((index > -1) && (index < this.stringOptions.size()))
			isValid = true;
		
		return isValid;
	}

	public TextBox getAddEnumElementField()
	{
		return this.addEnumElementField;
	}

	public Button getRemoveSelectedElement()
	{
		return this.removeSelectedElement;
	}

	public Button getRemoveAllElements()
	{
		return this.removeAllElements;
	}

	public Button getSortElements()
	{
		return this.sortElements;
	}

	public PushButton getMoveElementUp()
	{
		return this.moveElementUp;
	}

	public PushButton getMoveElementDown()
	{
		return this.moveElementDown;
	}

	public ListBox getTypeBox()
	{
		return this.typeBox;
	}

	public Button getOkButton()
	{
		return this.okButton;
	}

	public Button getCancelButton()
	{
		return this.cancelButton;
	}

}
