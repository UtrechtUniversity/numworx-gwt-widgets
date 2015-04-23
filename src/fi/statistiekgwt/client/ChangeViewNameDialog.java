package fi.statistiekgwt.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.columndialog.ColumnDialogController;
import fi.statistiekgwt.client.event.AddColumnEvent;
import fi.statistiekgwt.client.event.EditColumnEvent;
import fi.statistiekgwt.client.types.AllowedTypes;

/**
 * Dialog used for changing the name of a view
 * 
 * @author Sylvia van Borkulo
 * 
 */
public class ChangeViewNameDialog extends DialogBox // implements ActionListener
{
	private static final int DEFAULT_WIDTH = 400;
	private static final int DEFAULT_HEIGHT = 130;
	
	private Label nameLabel;
	private TextBox nameField;
	private FlowPanel namePanel;
	private Button okButton;
	private Button cancelButton;
	private HorizontalPanel okCancelPanel;
	private FlowPanel alles;
	
	private StatModel model;
	private String viewName;
	private StatInteractiePanelView view;
	
	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;

	/**
	 * Constructor
	 * 
	 * @param owner
	 *            the dialog owner
	 * @param model
	 *            the model
	 * @param viewName
	 *            the index of the view
	 * @param view
	 *            the view component
	 * @param location
	 *            the initial location of the dialog
	 */
	public ChangeViewNameDialog(StatModel model, String viewName,
		StatInteractiePanelView view)
	{
		super(true, true);
		super.setText(StatistiekGWT.rb.getString("changeviewnameDialog"));
		this.viewName = viewName;
		this.model = model;
		this.view = view;
		
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		this.setUp();

		this.addClickHandlers(new ChangeViewNameClickHandler());
	}

	private void addClickHandlers(ClickHandler handler)
	{
		this.okButton.addClickHandler(handler);
		this.cancelButton.addClickHandler(handler);
	}

	/**
	 * Initialize GUI
	 */
	private void setUp()
	{
		this.nameLabel = new Label(StatistiekGWT.rb.getString("enternameLabel"));
		this.nameLabel.addStyleName(statistiekCss.spaceBottomLabel());
		this.nameField = new TextBox();
		this.nameField.setText(this.viewName);
		this.nameField.addStyleName(statistiekCss.textbox());
		this.nameField.addStyleName(statistiekCss.spaceBottomLabel());

		this.namePanel = new FlowPanel();
		this.namePanel.add(this.nameLabel);
		this.namePanel.add(this.nameField);
		
		this.okButton = new Button(StatistiekGWT.rb.getString("OKButtonText"));
		this.okButton.addStyleName(statistiekCss.margin());
		this.cancelButton = new Button(StatistiekGWT.rb.getString("cancelButtonText"));
		this.cancelButton.addStyleName(statistiekCss.margin());
		
		this.okCancelPanel = new HorizontalPanel();//LayoutPanel();
		this.okCancelPanel.addStyleName(statistiekCss.horizontalPanelWithoutBorder());
		this.okCancelPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		this.okCancelPanel.add(this.okButton);
		this.okCancelPanel.add(this.cancelButton);
		
		this.alles = new FlowPanel();//LayoutPanel();
		
		this.alles.add(this.namePanel);
		this.alles.add(this.okCancelPanel);

		this.add(this.alles);
	}

	private boolean hasChanged()
	{
		boolean hasChanged = false;
		
		if (!this.nameField.getText().equals(viewName))
		{
			hasChanged = true;
		}
		
		return hasChanged;
	}
	
	class ChangeViewNameClickHandler implements ClickHandler//TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		@Override
		public void onClick(ClickEvent e)
		{
			if (e.getSource() == okButton)
			{
				hide(); // genoeg? view.hidePopupmenu() doet ook setVisible(false)
				
				if (hasChanged() && isValid())
				{
					// set nieuwe naam
					int selectedTabIndex = view.getSelectedTabIndex();
					model.getViews().get(selectedTabIndex).setViewName(nameField.getText());

					// set the currently selected view
					// syl: tabPane is not showing correctly,
					// while the field selectedView is properly updated (e.g.,
					// change page to check this)
					view.processSelectedView(selectedTabIndex);

					view.update();
				}
			}
			else if (e.getSource() == cancelButton)
			{
				setVisible(false);
				hide();
			}
		}
		
	} // class ChangeViewNameClickHandler

	public boolean isValid()
	{
		boolean b = true;
		int selectedTabIndex = view.getSelectedTabIndex();
		for (int i = 0; b && i < this.model.getViews().size(); i++)
		{
			b = (i == selectedTabIndex)
				|| !this.model.getViews().get(i).getViewName()
					.equals(this.nameField.getText());
		}

		return b;
	}


}
