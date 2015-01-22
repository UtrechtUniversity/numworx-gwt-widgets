package fi.statistiekgwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SelectionChangeEvent extends GwtEvent<SelectionChangeEventHandler>
{
	public static Type<SelectionChangeEventHandler> TYPE = new Type<SelectionChangeEventHandler>();

	private final String sender;

    public SelectionChangeEvent(String sender) 
    {
        this.sender = sender;
    }

	@Override
	public Type<SelectionChangeEventHandler> getAssociatedType()
	{
		return this.TYPE;
	}

	@Override
	protected void dispatch(SelectionChangeEventHandler handler)
	{
		handler.onSelectionChange(this);
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
