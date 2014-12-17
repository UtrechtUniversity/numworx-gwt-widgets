package fi.statistiekgwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;

public class DialogButton extends Button implements HasClickHandlers// implements ActionListener,
											// ComponentListener
{
	private DialogBox dialog;
	private LayoutPanel content;
	private String title;
	int preferredWidth = 700;
	int preferredHeight = 450;
	private DialogButtonClickHandler clickHandler;

	public DialogButton(String string, LayoutPanel content)
	{
		super(string);
		this.title = string;
		this.content = content;
		this.preferredWidth = content.getOffsetWidth();
		this.preferredHeight = content.getOffsetHeight();
		this.clickHandler = new DialogButtonClickHandler();
		//this.addClickHandler(this.clickHandler);//addActionListener(this);
		//.addClickHandler(this.clickHandler);//addActionListener(this);
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
		//this.dialog.add(content);
		this.dialog.setWidget(content);
//		this.dialog.setPixelSize(this.preferredWidth, this.preferredHeight);
		this.dialog.setPixelSize(800, 600);
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

/*	public void componentResized(ComponentEvent e)
	{
		// System.out.println("DialogButton.componentResized(): e.getSource=" +
		// e.getSource());
		if (e.getSource() == content)
		{
			this.preferredWidth = content.getOffsetWidth();
			this.preferredHeight = content.getOffsetHeight();
			// test syl
			if (dialog != null)
			{
				// System.out.println("DialogButton.componentResized() dialog != null, e="
				// + e.toString());
				dialog.setPixelSize(preferredWidth, preferredHeight);
			}
			else
			{
				// System.out.println("DialogButton.componentResized() dialog is null, e="
				// + e.toString());
			}
		}
	}
*/

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
