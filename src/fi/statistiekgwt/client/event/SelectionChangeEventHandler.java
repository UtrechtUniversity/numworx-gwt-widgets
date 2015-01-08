package fi.statistiekgwt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface SelectionChangeEventHandler extends EventHandler
{
	void onSelectionChange(SelectionChangeEvent event);
}
