package fi.statistiekgwt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface TableChangeEventHandler extends EventHandler
{
	void onTableChange(TableChangeEvent event);
}
