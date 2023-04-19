package fi.statistiekgwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class UpdateViewEvent extends GwtEvent<UpdateViewEventHandler>
{
	public static Type<UpdateViewEventHandler> TYPE = new Type<UpdateViewEventHandler>();

	private final String name;

    public UpdateViewEvent(String name) 
    {
        this.name = name;
    }

	@Override
	public Type<UpdateViewEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(UpdateViewEventHandler handler)
	{
		handler.onUpdateView(this);
	}

	public String getName()
	{
        return this.name;
    }
}
