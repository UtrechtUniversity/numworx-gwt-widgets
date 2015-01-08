package fi.statistiekgwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SelectionChangeEvent extends GwtEvent<SelectionChangeEventHandler>
{
	public static Type<SelectionChangeEventHandler> TYPE = new Type<SelectionChangeEventHandler>();

	private final String info;

    public SelectionChangeEvent(String info) 
    {
        this.info = info;
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

	public String getInfo() 
	{
        return this.info;
    }
}
