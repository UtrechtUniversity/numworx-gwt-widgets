package fi.statistiekgwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event is fired when the selection in a view has changed.
 * A view can also be the table view.
 * 
 * @author borku102
 *
 */
public class ViewSelectionChangeEvent extends GwtEvent<ViewSelectionChangeEventHandler>
{
	public static Type<ViewSelectionChangeEventHandler> TYPE = new Type<ViewSelectionChangeEventHandler>();

	private final String sender;

    public ViewSelectionChangeEvent(String sender) 
    {
        this.sender = sender;
    }

	@Override
	public Type<ViewSelectionChangeEventHandler> getAssociatedType()
	{
		return this.TYPE;
	}

	@Override
	protected void dispatch(ViewSelectionChangeEventHandler handler)
	{
		handler.onViewSelectionChange(this);
	}

	/**
	 * Get the sender name.
	 * @return
	 */
	public String getSenderName() 
	{
        return this.sender;
    }
}
