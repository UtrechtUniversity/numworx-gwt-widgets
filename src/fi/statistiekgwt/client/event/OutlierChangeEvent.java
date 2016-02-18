package fi.statistiekgwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class OutlierChangeEvent extends GwtEvent<OutlierChangeEventHandler>
{
	public static Type<OutlierChangeEventHandler> TYPE = new Type<OutlierChangeEventHandler>();
	
	private final String sender;

    public OutlierChangeEvent(String sender) 
    {
        this.sender = sender;
    }

	@Override
	public Type<OutlierChangeEventHandler> getAssociatedType()
	{
		return this.TYPE;
	}

	@Override
	protected void dispatch(OutlierChangeEventHandler handler)
	{
		handler.onOutlierChange(this);
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
