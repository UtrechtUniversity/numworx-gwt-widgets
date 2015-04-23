package fi.statistiekgwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;

public class DialogButton extends Button implements HasClickHandlers
{
	private DialogBox dialog;
	private FlowPanel content;
	private String title;
	int preferredWidth = 700;
	int preferredHeight = 450;
	private DialogButtonClickHandler clickHandler;

	public DialogButton(String string, FlowPanel content)
	{
		super(string);
		this.title = string;
		this.content = content;
		this.preferredWidth = content.getOffsetWidth();
		this.preferredHeight = content.getOffsetHeight();
		this.clickHandler = new DialogButtonClickHandler();
	}

	public void setDialogSize(int w, int h)
	{
		this.preferredWidth = w;
		this.preferredHeight = h;
	}

	public void makeDialog()
	{
		// System.out.println("DialogButton.makeDialog()");
		if (this.dialog == null)
		{
			this.dialog = new DialogBox(false, true); //JDialog((Frame) null, title, true);
			this.dialog.setText(this.title);
		}
		
		this.dialog.setWidget(content);
		this.getElement().getStyle().setBackgroundColor("GREY");
		int clientHeight = Window.getClientHeight();
		int clientWidth = Window.getClientWidth();
		int x = this.getAbsoluteLeft()
			+ Math.min(0, clientWidth
				- (this.getAbsoluteLeft() + this.getOffsetWidth()));
		int y = this.getAbsoluteTop()
			+ Math.min(0, clientHeight
				- (this.getAbsoluteTop() + this.getOffsetHeight()));
		dialog.setPopupPosition(x, y);

		dialog.center();
		dialog.show();
	}

	public void closeDialog()
	{
		// System.out.println("DialogButton.closeDialog()");
		dialog.hide();
	}

	public HandlerRegistration addClickHandler(ClickHandler handler)
    {
//        return addDomHandler(handler, ClickEvent.getType());
        return super.addClickHandler(handler);
    }
	
	public DialogButtonClickHandler getClickHandler()
	{
		return this.clickHandler;
	}

	class DialogButtonClickHandler implements ClickHandler//TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		@Override
		public void onClick(ClickEvent event)
		{
			makeDialog();
		}
		
	} // class DialogButtonTouchHandler
}
