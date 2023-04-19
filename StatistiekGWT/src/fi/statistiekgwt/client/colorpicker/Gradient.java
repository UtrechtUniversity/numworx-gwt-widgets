package fi.statistiekgwt.client.colorpicker;

import com.google.gwt.core.client.JavaScriptObject;

public class Gradient extends JavaScriptObject
{
	protected Gradient()
	{
	}

	public final native void addColorStop(int offset, String color) /*-{
		this.addColorStop(offset, color);
	}-*/;
}