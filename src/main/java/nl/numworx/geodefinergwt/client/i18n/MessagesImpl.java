package nl.numworx.geodefinergwt.client.i18n;

import com.google.gwt.core.client.GWT;

import fi.euclides.util.Messages;

public class MessagesImpl extends Messages {
	messages m = GWT.create(messages.class);

	@Override
	protected String getStringImpl(String string) {
		try {
			return m.getString(string);
		} catch(Exception e) {
			GWT.log("missing key " + string, e);
		}
		return super.getStringImpl(string);
	}
	
}
