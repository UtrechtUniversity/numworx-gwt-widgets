package fi.statistiekgwt.client.event;

import com.google.web.bindery.event.shared.Event;

public class ColorChangeEvent extends Event<ColorChangeEventHandler>
{
	public static Type<ColorChangeEventHandler> TYPE = new Type<ColorChangeEventHandler>();
	
	private final String colorA;
	private final String colorB;

    public ColorChangeEvent(String colorA, String colorB) 
    {
        this.colorA = colorA;
        this.colorB = colorB;
    }

	@Override
	public Type<ColorChangeEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(ColorChangeEventHandler handler)
	{
		handler.onColorChange(this);
	}

	/**
	 * Get color A.
	 * @return
	 */
	public String getColorA() 
	{
        return this.colorA;
    }

	/**
	 * Get color B.
	 * @return
	 */
	public String getColorB() 
	{
        return this.colorB;
    }
}
