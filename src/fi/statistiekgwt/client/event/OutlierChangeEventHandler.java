package fi.statistiekgwt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface OutlierChangeEventHandler extends EventHandler
{
	void onOutlierChange(OutlierChangeEvent event);
}
