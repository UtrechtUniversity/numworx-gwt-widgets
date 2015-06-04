package fi.statistiekgwt.client.colorpicker;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.event.shared.GwtEvent;

public class ColorChangedEvent extends GwtEvent<IColorChangedHandler>
{
	private static GwtEvent.Type<IColorChangedHandler> TYPE;

	private String color;
	private CssColor cssColor;

	ColorChangedEvent(String color)
	{
		this.color = color;
	}

	ColorChangedEvent(CssColor color)
	{
		this.cssColor = color;
	}

	public static GwtEvent.Type<IColorChangedHandler> getType()
	{
		if (TYPE == null)
		{
			TYPE = new Type<IColorChangedHandler>();
		}
		return TYPE;
	}

	@Override
	public GwtEvent.Type<IColorChangedHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(IColorChangedHandler handler)
	{
		handler.colorChanged(this);
	}

	public String getColor()
	{
		return this.color;
	}

	public CssColor getCssColor()
	{
		return this.cssColor;
	}
}