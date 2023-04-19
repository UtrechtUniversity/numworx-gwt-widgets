package fi.statistiekgwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AddViewEvent extends GwtEvent<AddViewEventHandler>
{
	public static Type<AddViewEventHandler> TYPE = new Type<AddViewEventHandler>();

	private final String name;

    public AddViewEvent(String name) 
    {
        this.name = name;
    }

	@Override
	public Type<AddViewEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(AddViewEventHandler handler)
	{
		handler.onAddView(this);
	}

	public String getName()
	{
        return this.name;
    }
}
