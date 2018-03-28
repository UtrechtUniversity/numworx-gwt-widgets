package fi.statistiekgwt.client.piechart;

import java.util.ArrayList;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import fi.statistiekgwt.client.DialogButton;
import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;
import fi.statistiekgwt.client.StatistiekUtils;

/**
 * User options panel for StatistiekView PieChart
 * 
 * @author Sylvia van Borkulo
 * 
 */
public class PieChartUserOptionsPanel extends FlowPanel
{
	private PieChartView view;
	private PieChartController controller;
	private PieChartModel model;

	private DialogButton dialogButton;

	/**
	 * Panel 'basisPanel' wordt aan DialogButton meegegeven als content.
	 */
	private FlowPanel basisPanel;
	
	private PieChartUOPClickHandler clickHandler;
	private PieChartUOPChangeHandler changeHandler;

	// variable settings
	private Label varLabel;
	private ListBox columnIndexBox;

	// display settings
	private Button okButton;

	/**
	 * The event bus to send change events to event handlers associated 
	 * with the views using StatTableModel.
	 */
	EventBus eventBus;

	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;

	public PieChartUserOptionsPanel(PieChartView view,
		PieChartController controller, PieChartModel model)
	{
		this.view = view;
		this.controller = controller;
		this.model = model;

		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		this.clickHandler = new PieChartUOPClickHandler();
		this.changeHandler = new PieChartUOPChangeHandler();

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
		this.okButton.addClickHandler(this.clickHandler);
		
		// change handlers
		this.columnIndexBox.addChangeHandler(this.changeHandler);
	}

	private void createGuiComponents()
	{
		this.basisPanel = new FlowPanel();

		// variable settings
		this.varLabel = new Label(StatistiekGWT.rb.variableLabel());
		this.varLabel.addStyleName(statistiekCss.titlelabel());

		this.columnIndexBox = new ListBox();

		// ok-cancel
		this.okButton = new Button(StatistiekGWT.rb.oKButtonText());
	}

	private void layoutGuiComponents()
	{
		HorizontalPanel allSettingsPanel;
		FlowPanel variableSettingsPanel;

		// Variable settings
		variableSettingsPanel = new FlowPanel();
		variableSettingsPanel.setTitle(StatistiekGWT.rb.variableLabel()); // tooltip boven panel
		variableSettingsPanel.addStyleName(this.statistiekCss.settingspanel());
		// add components
		variableSettingsPanel.add(varLabel);
		variableSettingsPanel.add(this.columnIndexBox);

		// Put settings panels together on allSettingsPanel
		allSettingsPanel = new HorizontalPanel();
		allSettingsPanel.setBorderWidth(2);
		allSettingsPanel.addStyleName(this.statistiekCss.horizontalPanel());
		allSettingsPanel.add(variableSettingsPanel);

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

	public int getColumnIndexBoxSelectedIndex()
	{
		return this.columnIndexBox.getSelectedIndex();
	}

	public void setModel(PieChartModel model)
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
		
		boolean isEnumOnlyView = true;
		boolean ignoreFirstItem = false;
		StatistiekUtils.setEnumOnlyColumnsEnabledVarListBox(columnIndexBox, isEnumOnlyView, ignoreFirstItem, model.getStatTableModel());
		
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
	 * A clickhandler for PieChartUserOptionsPanel
	 */
	class PieChartUOPClickHandler implements ClickHandler
	{

		@Override
		public void onClick(ClickEvent e)
		{
			if (e.getSource() == okButton)
			{
				dialogButton.closeDialog();
			}
			else
			{
				//System.out.println("PieChartUserOptionsPanel.PieChartUOPClickHandler.actionPerformed(): Unknown action source! " + e);
			}
		}
		
	} // class PieChartUOPClickHandler

	class PieChartUOPChangeHandler implements ChangeHandler
	{
		@Override
		public void onChange(ChangeEvent e)
		{
			if (e.getSource() == columnIndexBox)
			{
				model.setColumnIndex(view.getColumnIndexBoxSelectedIndex());
			}

			// update view (and uop)
			view.update();
		}
	} // class PieChartUOPChangeHandler

}
