package fi.statistiekgwt.client.event;

import java.util.ArrayList;

import com.google.gwt.event.shared.GwtEvent;
import fi.statistiekgwt.client.types.AllowedTypes;

public class EditColumnEvent extends GwtEvent<EditColumnEventHandler>
{
	public static Type<EditColumnEventHandler> TYPE = new Type<EditColumnEventHandler>();

	private final int columnIndex;
	private final String name;
	private final AllowedTypes type;
	private final ArrayList<String> enumOptions;
	private final String uitleg;
	
	private final boolean hasChangedName;
	private final boolean hasChangedType;
	private final boolean hasChangedEnumOptions;
	private final boolean hasChangedUitleg;

    public EditColumnEvent(int columnIndex, String name, boolean hasChangedName, 
    	AllowedTypes type, boolean hasChangedType,
    	ArrayList<String> enumOptions, boolean hasChangedEnumOptions,
    	String uitleg, boolean hasChangedUitleg) 
    {
    	this.columnIndex = columnIndex;
        this.name = name;
        this.hasChangedName = hasChangedName;
        this.type = type;
        this.hasChangedType = hasChangedType;
        this.enumOptions = enumOptions;
        this.hasChangedEnumOptions = hasChangedEnumOptions;
        this.uitleg = uitleg;
        this.hasChangedUitleg = hasChangedUitleg;
    }

	@Override
	public Type<EditColumnEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(EditColumnEventHandler handler)
	{
		handler.onEditColumn(this);
	}
	
	public int getColumnIndex()
	{
		return this.columnIndex;
	}

	public String getName() 
	{
        return this.name;
    }

	public AllowedTypes getType() 
	{
        return this.type;
    }

	public ArrayList<String> getEnumOptions() 
	{
        return this.enumOptions;
    }

	public String getUitleg() 
	{
        return this.uitleg;
    }
	
	public boolean hasChangedName()
	{
		return this.hasChangedName;
	}
	
	public boolean hasChangedType()
	{
		return this.hasChangedType;
	}
	
	public boolean hasChangedEnumOptions()
	{
		return this.hasChangedEnumOptions;
	}
	
	public boolean hasChangedUitleg()
	{
		return this.hasChangedUitleg;
	}
}
