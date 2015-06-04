package fi.statistiekgwt.client.colorpicker;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.statistiekgwt.client.StatistiekCssResource;
import fi.statistiekgwt.client.StatistiekGWT;
import fi.statistiekgwt.client.StatistiekGWTClientBundle;

public abstract class Dialog extends DialogBox
{
	private ClickHandler buttonClickHandler = new ClickHandler()
	{
		public void onClick(ClickEvent event)
		{
			buttonClicked((Widget) event.getSource());
		}
	};

	private Widget dialogArea;
	private Button okButton;
	private Button cancelButton;

	StatistiekGWTClientBundle statistiekGWTClientBundle;
	StatistiekCssResource statistiekCss;

	public Dialog()
	{
		this.statistiekGWTClientBundle = GWT.create(StatistiekGWTClientBundle.class);
		this.statistiekCss = this.statistiekGWTClientBundle.getStatistiekGWTCSS();
		this.statistiekCss.ensureInjected();

		VerticalPanel panel = new VerticalPanel();
		dialogArea = createDialogArea();
		panel.add(dialogArea);
		panel.add(createButtonBar());
		setWidget(panel);
	}

	public HandlerRegistration addDialogClosedHandler(
		IDialogClosedHandler handler)
	{
		return addHandler(handler, DialogClosedEvent.getType());
	}

	protected void close(boolean canceled)
	{
		hide();
		fireDialogClosed(canceled);
	}

	private void fireDialogClosed(boolean canceled)
	{
		fireEvent(new DialogClosedEvent(canceled));
	}

	protected Widget createButtonBar()
	{
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setWidth("100%");
		buttonsPanel.setStyleName("DialogButtons"); //$NON-NLS-1$
		buttonsPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		List<? extends Widget> buttons = createButtonsForButtonBar();
		for (Widget button : buttons)
		{
			buttonsPanel.add(button);
		}
		return buttonsPanel;
	}

	protected List<? extends Widget> createButtonsForButtonBar()
	{
		okButton = createButton(StatistiekGWT.rb.getString("OKButtonText"));
		okButton.addStyleName(statistiekCss.margin());
		cancelButton = createButton(StatistiekGWT.rb.getString("cancelButtonText"));
		cancelButton.addStyleName(statistiekCss.margin());
		
		return Arrays.asList(okButton, cancelButton);
	}

	protected Button createButton(String text)
	{
		return new Button(text, buttonClickHandler);
	}

	protected abstract Widget createDialogArea();

	protected Widget getDialogArea()
	{
		return dialogArea;
	}

	protected abstract void buttonClicked(Widget button);

	protected Button getOkButton()
	{
		return okButton;
	}

	protected Button getCancelButton()
	{
		return cancelButton;
	}
}