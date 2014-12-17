package fi.statistiekgwt.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class TableChangeEvent extends GwtEvent<TableChangeEventHandler>
{
	public static Type<TableChangeEventHandler> TYPE = new Type<TableChangeEventHandler>();

	private final String info;

    public TableChangeEvent(String info) 
    {
        this.info = info;
    }

	@Override
	public Type<TableChangeEventHandler> getAssociatedType()
	{
		return this.TYPE;
	}

	@Override
	protected void dispatch(TableChangeEventHandler handler)
	{
		handler.onTableChange(this);
	}

	public String getInfo() 
	{
        return this.info;
    }
}
