package fi.statistiekgwt.client.columndialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;
import fi.statistiekgwt.client.StatistiekUtils;
import fi.statistiekgwt.client.columndialog.ColumnDialogController.ColumnDialogBlurHandler;
import fi.statistiekgwt.client.columndialog.ColumnDialogController.ColumnDialogChangeHandler;
import fi.statistiekgwt.client.columndialog.ColumnDialogController.ColumnDialogKeyDownHandler;
import fi.statistiekgwt.client.columndialog.ColumnDialogController.ColumnDialogValueChangeHandler;
import fi.statistiekgwt.client.types.AllowedTypes;
import fi.statistiekgwt.client.types.ColumnType;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;

/**
 * View for add column dialog
 * 
 * @author Manu Drijvers, Sylvia van Borkulo
 * 
 */
public class ColumnDialogView extends DialogBox
{
	private static final int MAXIMUM_VISIBLE_IN_ENUM_LIST = 7;

	private ColumnDialogModel model;

	private FlowPanel alles;

	private Label kiesNaam;
	private TextBox nameField;

	private Label kiesType;
	private ListBox typeBox;
	private HTML separatorComputeVariable;
	/**
	 * hr element, used to create an invisible separator.
	 */
	private static final String hrString = new String("<hr  style=\"width:100%; visibility: hidden\" />");

	private FlowPanel setTypePanel;

	/**
	 *  Compute variable button.
	 */
	private Button computeVariableButton;
	/**
	 * The panel for computing the variable.
	 */
	private FlowPanel computeVariablePanel;
	/**
	 * Listbox met de kolomnamen, t.b.v. bereken veriabele.
	 */
	private ListBox columnsListBox;
	/**
	 * De editor voor het invoeren van de berekening
	 * voor 'bereken variabele'.
	 */
	private FormuleEditor computeVariableEditor;
	/**
	 * Het dwo-toetsenbord.
	 */
	FormuleKeyboardIF kb;
	/**
	 * Touch panel om een FormuleEditorTouchHandler aan te koppelen opdat je
	 * binnen de formule-editor de focus kunt zetten voor de verschillende 
	 * invulvakken van een formule.
	 */
	TouchPanel touchPanel = null;
	/**
	 * Boolean indicating 'Computer variable' button
	 * has been clicked yes or no.
	 */
	private boolean hasClickedComputeVariable = false;

	private FlowPanel createEnumPanel;
	private FlowPanel addEnumElementPanel;
	private Label addEnumElementLabel;
	private TextBox addEnumElementField;
	private ListBox enumElementsList; // was: OrderableJList
	private ArrayList<String> stringOptions;
	private AllowedTypes originalColumnType;
	/**
	 * The panel with the list with enumeration elements.
	 */
	private FlowPanel enumListPanel;
	/**
	 * Panel with 'remove selected' and 'remove all' buttons for enumeration.
	 */
	private HorizontalPanel enumSouthPanel;
	/**
	 * Panel with move up/down and sort buttons for enumeration.
	 */
	private FlowPanel enumEastPanel;
	private Button removeSelectedElement;
	private Button removeAllElements;
	private Button sortElements;
	private PushButton moveElementUp;
	private PushButton moveElementDown;

	/**
	 * Panel met naam, type en enum instellingen.
	 */
	private FlowPanel typePanel;
	/**
	 * Panel met uitleg invoerveld.
	 */
	private FlowPanel uitlegPanel;
	private Label uitlegLabel;
	private TextArea uitlegArea;
	
	/**
	 * Panel with OK/Cancel buttons.
	 */
	private FlowPanel okCancelPanel;
	private Button okButton;
	private Button cancelButton;

	boolean isViewOnly = false;

	private String font;

	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;

	/**
	 * Constructor.
	 * 
	 * @param model
	 *            MVC Model
	 */
	public ColumnDialogView(ColumnDialogModel model, String text)
	{
		// voor DWO-toetsenbord modal false
		super(false, false);
		super.setText(text);
		
		if (text.equals(StatistiekGWT.rb.columninfo()))
		{
			this.isViewOnly = true;
		}

		this.model = model;
		
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		this.initialize();
	}

	/**
	 * Set up GUI
	 */
	private void initialize()
	{
		this.font = StatistiekGWT.fontString;

		this.kiesNaam = new Label(StatistiekGWT.rb.columnname());
		this.nameField = new TextBox();
		this.nameField.addStyleName(statistiekCss.textbox());
		this.kiesType = new Label(StatistiekGWT.rb.choosetype());
		this.kiesType.addStyleName(statistiekCss.spaceTopLabel());

		// Use strings from text file to create listbox
		String[] types = {
			StatistiekGWT.rb.integer(),
			StatistiekGWT.rb.decimalNumber(),
			StatistiekGWT.rb.string(),
			StatistiekGWT.rb.enumValue()
		};
		this.typeBox = new ListBox();
		for (int i = 0; i < types.length; i++)
		{
			this.typeBox.addItem(types[i]);
		}
		setTypeBox();
		
		this.separatorComputeVariable = new HTML(ColumnDialogView.hrString);
		this.separatorComputeVariable.addStyleName(statistiekCss.horizontalrule());
		this.computeVariableButton = new Button(StatistiekGWT.rb.computeVariable());
		this.columnsListBox = new ListBox();
		fillColumnsListBox();
		StatistiekUtils.setNumericColumnsEnabledListBox(columnsListBox, true, true, model.getTableModel());
		this.computeVariableEditor = new FormuleEditor();
		this.kb = computeVariableEditor.getKeyboard();
		kb.setEnterType(EnterType.APPLY);
		touchPanel = new TouchPanel();
		touchPanel.add(computeVariableEditor.getAsPanel());
		
		// compute variable panel
		this.computeVariablePanel = new FlowPanel();
		this.computeVariablePanel.add(new Label(StatistiekGWT.rb.computeVariableLabel()));
		this.computeVariablePanel.add(columnsListBox);
		this.computeVariablePanel.add(touchPanel);
		
		this.computeVariableEditor.setFormuleToolBijFocus(true);

		touchPanel.addTouchHandler(new FormuleEditorTouchHandler(computeVariableEditor) {

			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				super.onTouchStart(event);
			}
			
		});

		this.setTypePanel = new FlowPanel();
		this.setTypePanel.add(this.kiesNaam);
		this.setTypePanel.add(this.nameField);
		this.setTypePanel.add(this.kiesType);
		this.setTypePanel.add(this.typeBox);
		this.setTypePanel.add(this.separatorComputeVariable);
		this.setTypePanel.add(this.computeVariableButton);

		this.addEnumElementLabel = new Label(
			StatistiekGWT.rb.addenumeration());
		this.addEnumElementLabel.addStyleName(statistiekCss.spaceTopLabel());
		this.addEnumElementField = new TextBox();
		this.addEnumElementField.addStyleName(statistiekCss.textbox());
		this.addEnumElementPanel = new FlowPanel();
		this.addEnumElementPanel.add(this.addEnumElementLabel);
		this.addEnumElementPanel.add(this.addEnumElementField);

		this.enumElementsList = new ListBox();
		this.enumElementsList.addStyleName(statistiekCss.margin());
		ArrayList<String> list = this.model.getEnumOptions();
		for (int i = 0; i < list.size(); i++)
		{
			this.enumElementsList.addItem(list.get(i));
		}
		
		this.enumElementsList.setVisibleItemCount(list.size());
		this.enumListPanel = new FlowPanel();
		this.enumListPanel.add(this.enumElementsList);
		
		this.removeSelectedElement = new Button(
			StatistiekGWT.rb.removeselectedelement());
		this.removeSelectedElement.addStyleName(statistiekCss.margin());

		this.removeAllElements = new Button(
			StatistiekGWT.rb.removeAllElements());
		this.removeAllElements.addStyleName(statistiekCss.margin());
		
		this.enumSouthPanel = new HorizontalPanel();
		this.enumSouthPanel.add(this.removeSelectedElement);
		this.enumSouthPanel.add(this.removeAllElements);
		
		this.moveElementUp = new PushButton(new Image(statistiekGWTClientBundle.arrowUpResource().getSafeUri()));
		this.moveElementUp.setTitle(StatistiekGWT.rb.moveElementUpTooltip());
		this.moveElementUp.addStyleName(statistiekCss.margin());

		this.moveElementDown = new PushButton(new Image(statistiekGWTClientBundle.arrowDownResource().getSafeUri()));
		this.moveElementDown.setTitle(StatistiekGWT.rb.moveElementDownTooltip());
		this.moveElementDown.addStyleName(statistiekCss.margin());

		this.sortElements = new Button(
			StatistiekGWT.rb.sortElements());
		this.sortElements.setTitle(StatistiekGWT.rb.sortElementsTooltip());// tooltip
		this.sortElements.addStyleName(statistiekCss.margin());

		this.enumEastPanel = new FlowPanel();
		this.enumEastPanel.setWidth("47px"); // make the buttons fit
		this.enumEastPanel.add(this.moveElementUp);
		this.enumEastPanel.add(this.moveElementDown);
		this.enumEastPanel.add(this.sortElements);

		HorizontalPanel enumScrollAndButtonsPanel = new HorizontalPanel();
		enumScrollAndButtonsPanel.setBorderWidth(1);
		enumScrollAndButtonsPanel.addStyleName(statistiekCss.horizontalPanel());
		enumScrollAndButtonsPanel.add(this.enumListPanel);
		enumScrollAndButtonsPanel.add(this.enumEastPanel);
		this.createEnumPanel = new FlowPanel();
		this.createEnumPanel.add(this.addEnumElementPanel);
		this.createEnumPanel.add(enumScrollAndButtonsPanel);
		this.createEnumPanel.add(this.enumSouthPanel);
		
		this.typePanel = new FlowPanel();
		this.typePanel.addStyleName(statistiekCss.margin());
		this.typePanel.add(this.setTypePanel);
		this.typePanel.add(this.computeVariablePanel);
		this.typePanel.add(this.createEnumPanel);
		
		this.uitlegLabel = new Label(StatistiekGWT.rb.uitlegbijkolom());
		this.uitlegArea = new TextArea();
		this.uitlegArea.addStyleName(statistiekCss.textarea());
		this.uitlegArea.addStyleName(statistiekCss.boxsizingborder());
		this.uitlegPanel = new FlowPanel();
		this.uitlegPanel.addStyleName(statistiekCss.margin());
		this.uitlegPanel.add(this.uitlegLabel);
		this.uitlegPanel.add(this.uitlegArea);

		this.okCancelPanel = new FlowPanel();
		this.okButton = new Button(StatistiekGWT.rb.oKButtonText());
		this.okButton.addStyleName(statistiekCss.margin());
		this.cancelButton = new Button(StatistiekGWT.rb.cancelButtonText());
		this.cancelButton.addStyleName(statistiekCss.margin());
		this.okCancelPanel.add(this.okButton);
		this.okCancelPanel.add(this.cancelButton);

		this.alles = new FlowPanel();
		this.alles.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setBorderWidth(1);
		hPanel.addStyleName(this.statistiekCss.horizontalPanel());

		hPanel.add(this.typePanel);
		hPanel.add(this.uitlegPanel);
		this.alles.add(hPanel);
		this.alles.add(this.okCancelPanel);

		this.add(this.alles);
		
		this.originalColumnType = this.model.getType();
		this.setStringOptions();
		
		if (this.isViewOnly)
		{
			this.setColumnInfoMode(false);
		}

		this.update();
	}

	/**
	 * Vul columnslistbox met de kolomnamen.
	 */
	private void fillColumnsListBox()
	{
		columnsListBox.addItem(StatistiekGWT.rb.chooseItem());
		
		for (int i = 0; i < model.getTableModel().getColumnCount(); i++)
		{
			String name = model.getTableModel().getColumnName(i);
			columnsListBox.addItem(name);
		}
	}

	/**
	 * Set column info mode yes/no, i.e. in column info mode
	 * show info fields disabled 
	 * and hide the irrelevant components.
	 * 
	 * @param b column info mode yes/no
	 */
	private void setColumnInfoMode(boolean b)
	{
		// set enabled
		this.nameField.setEnabled(b);
		this.typeBox.setEnabled(b);
		this.enumElementsList.setEnabled(b);
		this.uitlegArea.setEnabled(b);

		// hide or show add enum element components
		this.addEnumElementLabel.setVisible(b);
		this.addEnumElementField.setVisible(b);
		this.removeSelectedElement.setVisible(b);
		this.removeAllElements.setVisible(b);
		this.sortElements.setVisible(b);
		this.moveElementUp.setVisible(b);
		this.moveElementDown.setVisible(b);
		// hide or show cancel button
		this.cancelButton.setVisible(b);
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
			// new ArrayList, else the model's stringoptions may be altered
			this.stringOptions = new ArrayList<String>(this.model.getTableModel().
				getStringOptions(this.model.getColumnIndex()));
		}
	}
	
	/**
	 * Reset to the model's original stringoptions when the column dialog was opened.
	 * Used in case of altering stringoptions and cancelling the action.
	 */
	public void resetOriginalStringOptions()
	{
		this.stringOptions = this.model.getTableModel().
			getStringOptions(this.model.getColumnIndex());
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
		
		if (typeString.equals(StatistiekGWT.rb.integer()))
		{
			type = AllowedTypes.INTEGER;
		}
		else if (typeString.equals(StatistiekGWT.rb.decimalNumber()))
		{
			type = AllowedTypes.DOUBLE;
		}
		else if (typeString.equals(StatistiekGWT.rb.string()))
		{
			type = AllowedTypes.STRING;
		}
		else if (typeString.equals(StatistiekGWT.rb.enumValue()))
		{
			type = AllowedTypes.ENUM;
		}
			
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
	 * @return De formulestring om de variabele te berekenen.
	 */
	public String getComputeVariableFormula()
	{
		return computeVariableEditor.toString();
	}

	/**
	 * @return Get the text in the addEnumElementField textfield
	 */
	public String getEnumOption()
	{
		return this.addEnumElementField.getText();
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
	 * Add a clickhandler to all buttons and fields.
	 * 
	 * @param handler
	 *            the attached clickhandler
	 */
	public void addClickHandlers(ClickHandler handler)
	{
		this.computeVariableButton.addClickHandler(handler);
		this.removeSelectedElement.addClickHandler(handler);
		this.removeAllElements.addClickHandler(handler);
		this.sortElements.addClickHandler(handler);
		this.moveElementUp.addClickHandler(handler);
		this.moveElementDown.addClickHandler(handler);
		this.okButton.addClickHandler(handler);
		this.cancelButton.addClickHandler(handler);
	}

	public void addChangeHandlers(ColumnDialogChangeHandler handler)
	{
		this.typeBox.addChangeHandler(handler);
		this.columnsListBox.addChangeHandler(handler);
	}

	public void addValueChangeHandlers(
		ColumnDialogValueChangeHandler handler)
	{
		this.nameField.addValueChangeHandler(handler);
		this.addEnumElementField.addValueChangeHandler(handler);
		this.uitlegArea.addValueChangeHandler(handler);
	}

	public void addKeyDownHandlers(ColumnDialogKeyDownHandler handler)
	{
		this.nameField.addKeyDownHandler(handler);
		this.addEnumElementField.addKeyDownHandler(handler);
	}
	
	public void addBlurHandlers(ColumnDialogBlurHandler handler)
	{
		this.nameField.addBlurHandler(handler);
		this.addEnumElementField.addBlurHandler(handler);
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
	 * Update the column dialog box.
	 */
	public void update()
	{
		// update typeBox selected item
		this.setSelectedItemTypeBox(this.model.getType());

		// set the visibility
		this.createEnumPanel.setVisible(this.model.getType().equals(
			AllowedTypes.ENUM));
		this.enumElementsList.setVisible(this.model.getType().equals(
			AllowedTypes.ENUM));
		this.computeVariableButton.setVisible(this.model.getType().isNumber());

		// Update the list with options of current enumeration
		// or with string options if there is no current enumeration
		if (this.wasEnum())
		{
			this.fillEnumElementsList(this.model.getEnumOptions());
		}
		else
		{
			// vul met stringOptions
			this.fillEnumElementsList(this.stringOptions);
		}

		this.nameField.setText(this.model.getName());

		// zet de tekst van de knop
		if (hasClickedComputeVariable())
			setTextComputeVariableButton(StatistiekGWT.rb.cancelComputeVariable());
		else
			setTextComputeVariableButton(StatistiekGWT.rb.computeVariable());

		// toon compute variable panel als op de knop is gedrukt
		this.computeVariablePanel.setVisible(hasClickedComputeVariable() && this.model.getType().isNumber());
		if (hasClickedComputeVariable() && this.model.getType().isNumber())
		{
			setFocus(true);
			touchPanel.setVisible(true);
		}
		else
		{
			setFocus(false);
		}

		this.uitlegArea.setText(this.model.getUitleg());
	}

	public void setFocus(boolean b)
	{
		if (b)
		{
			computeVariableEditor.requestFocus();
			kb.focus();

			//om te zorgen dat cursor ook getekend wordt:
			if (computeVariableEditor.getCurrentElement() == null)
			{	
				computeVariableEditor.setCurrentElementRepaint(computeVariableEditor.getMainRegel());
			}
		}
		else
		{
			kb.blur();
		}
	}

	/**
	 * Fill enumElementsList with the values in the given list.
	 * @param list
	 */
	private void fillEnumElementsList(ArrayList<String> list)
	{
		// empty enumElementsList
		for (int i = this.enumElementsList.getItemCount() - 1; i >= 0; i--)
		{
			this.enumElementsList.removeItem(i);
		}
		
		for (int i = 0; i < list.size(); i++)
		{
			this.enumElementsList.addItem(list.get(i));
		}
		
		this.enumElementsList.setVisibleItemCount(Math.min(list.size(), ColumnDialogView.MAXIMUM_VISIBLE_IN_ENUM_LIST));
	}

	/**
	 * Set the given type selected in typeBox.
	 * @param type
	 */
	private void setSelectedItemTypeBox(AllowedTypes type)
	{
		String typeString = "";
		if (type.equals(AllowedTypes.INTEGER))
		{
			typeString = StatistiekGWT.rb.integer();
		}
		else if (type.equals(AllowedTypes.DOUBLE))
		{
			typeString = StatistiekGWT.rb.decimalNumber();
		}
		else if (type.equals(AllowedTypes.STRING))
		{
			typeString = StatistiekGWT.rb.string();
		}
		else if (type.equals(AllowedTypes.ENUM))
		{
			typeString = StatistiekGWT.rb.enumValue();
		}

		// find the index of type
		int indexToFind = -1;
		for (int i=0; i < this.typeBox.getItemCount(); i++) 
		{
		    if (this.typeBox.getItemText(i).equals(typeString)) 
		    {
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

	boolean hasClickedComputeVariable()
	{
		return hasClickedComputeVariable;
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
            	{
            		return 1;
            	}
            	else if (s2.equals(ColumnType.WILDCARD))
            	{
            		return -1;
            	}
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
		
		if ((index > -1) && (index < this.stringOptions.size()))
		{
			isValid = true;
		}
		
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

	public ListBox getColumnsListBox()
	{
		return this.columnsListBox;
	}

	public Button getOkButton()
	{
		return this.okButton;
	}

	public Button getCancelButton()
	{
		return this.cancelButton;
	}

	public Button getComputeVariableButton()
	{
		return this.computeVariableButton;
	}
	
	public void setHasClickedComputeVariable(boolean b)
	{
		hasClickedComputeVariable = b;
	}
	
	/**
	 * Zet de gegeven tekst op de computeVariableButton.
	 * 
	 * @param text
	 */
	void setTextComputeVariableButton(String text)
	{
		computeVariableButton.setText(text);
	}

	/**
	 * Voeg de huidige geselecteerde kolomnaam in columnsListBox 
	 * zonder spaties toe aan computeVariableEditor.
	 *  
	 */
	public void addToEditor()
	{
		String strippedColumnName = columnsListBox.getSelectedItemText().replaceAll("\\s", "");
		computeVariableEditor.insert(strippedColumnName);
		setFocus(true);
	}
}
