package fi.statistiekgwt.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
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
	private LayoutPanel namePanel;
	private Button okButton;
	private Button cancelButton;
	private LayoutPanel okCancelPanel;
	private LayoutPanel alles;
	
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
		super.setPixelSize(ChangeViewNameDialog.DEFAULT_WIDTH, ChangeViewNameDialog.DEFAULT_HEIGHT);
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
		this.nameField = new TextBox();
		this.nameField.setText(this.viewName);
		this.nameField.addStyleName(statistiekCss.textbox());

		this.namePanel = new LayoutPanel();
		this.namePanel.add(this.nameLabel);
		this.namePanel.add(this.nameField);
		// set position
		this.namePanel.setWidgetLeftWidth(this.nameLabel, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.namePanel.setWidgetTopHeight(this.nameLabel, 0, Style.Unit.PCT, 50, Style.Unit.PCT);
		this.namePanel.setWidgetLeftWidth(this.nameField, 0, Style.Unit.PCT, 100, Style.Unit.PCT);
		this.namePanel.setWidgetTopHeight(this.nameField, 50, Style.Unit.PCT, 50, Style.Unit.PCT);
		
		this.okButton = new Button(StatistiekGWT.rb.getString("OKButtonText"));
		this.cancelButton = new Button(StatistiekGWT.rb.getString("cancelButtonText"));
		this.okCancelPanel = new LayoutPanel();
		this.okCancelPanel.add(this.okButton);
		this.okCancelPanel.add(this.cancelButton);
		// set position
		this.okCancelPanel.setWidgetLeftWidth(this.okButton, 25, Style.Unit.PCT, 25, Style.Unit.PCT);
		this.okCancelPanel.setWidgetTopHeight(this.okButton, 0, Style.Unit.PX, 30, Style.Unit.PX);
		this.okCancelPanel.setWidgetLeftWidth(this.cancelButton, 50, Style.Unit.PCT, 25, Style.Unit.PCT);
		this.okCancelPanel.setWidgetTopHeight(this.cancelButton, 0, Style.Unit.PX, 30, Style.Unit.PX);
		
		this.alles = new LayoutPanel();
		this.alles.setPixelSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		this.alles.add(this.namePanel);
		this.alles.add(this.okCancelPanel);
		this.alles.setPixelSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		// set position
		this.alles.setWidgetLeftWidth(this.namePanel, 0, Unit.PCT, 100, Unit.PCT);  // Top panel
		this.alles.setWidgetTopHeight(this.namePanel, 0, Style.Unit.PX, 70, Style.Unit.PX);
		this.alles.setWidgetLeftWidth(this.okCancelPanel, 0, Unit.PCT, 100, Unit.PCT); // Bottom panel
		this.alles.setWidgetTopHeight(this.okCancelPanel, 70, Style.Unit.PX, 30, Style.Unit.PX);
		this.add(this.alles);
	}

//	public void actionPerformed(ActionEvent arg0)
//	{
//		if (arg0.getSource() == this.textField)
//		{
//			boolean b = true;
//			for (int i = 0; b && i < this.model.getViews().size(); i++)
//			{
//				b = i == this.viewIndex
//					|| !this.model.getViews().get(i).getViewName()
//						.equals(this.textField.getText());
//			}
//			if (b)
//			{
//				this.model.getViews().get(this.viewIndex)
//					.setViewName(this.textField.getText());
//
//				this.view.update();
//				// set the currently selected view
//				// syl: tabPane is not showing correctly,
//				// while the field selectedView is properly updated (e.g.,
//				// change page to check this)
//				this.view.processSelectedView(this.viewIndex);
//
//				super.setVisible(false);
//			}
//			else
//			{
//				this.nameLabel.setText(StatistiekGWT.rb
//					.getString("namealreadyinuseLabel"));
//			}
//		}
//	}
	
	private boolean hasChanged()
	{
		boolean hasChanged = false;
		
		if (!this.nameField.getText().equals(viewName))
			hasChanged = true;
		
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
