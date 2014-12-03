package fi.statistiekgwt.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import fi.statistiekgwt.client.types.AllowedTypes;

public class AddColumnEvent extends GwtEvent<AddColumnEventHandler>
{
	public static Type<AddColumnEventHandler> TYPE = new Type<AddColumnEventHandler>();

	private final String name;
	private final AllowedTypes type;
	private final String uitleg;

    public AddColumnEvent(String name, AllowedTypes type, String uitleg) 
    {
        this.name = name;
        this.type = type;
        this.uitleg = uitleg;
    }

	@Override
	public Type<AddColumnEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(AddColumnEventHandler handler)
	{
		handler.onAddColumn(this);
	}

	public String getName() 
	{
        return this.name;
    }

	public AllowedTypes getType() 
	{
        return this.type;
    }

	public String getUitleg() 
	{
        return this.uitleg;
    }
}
