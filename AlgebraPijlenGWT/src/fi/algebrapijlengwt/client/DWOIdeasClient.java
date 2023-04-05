package fi.algebrapijlengwt.client;

import com.google.gwt.user.client.Window;

import nl.uu.fi.dwo.ideas.client.IdeasClient;

public class DWOIdeasClient extends IdeasClient {

	static native String casServer() /*-{
		return $wnd.casServer
	}-*/;
	
	
	private static int ENDPOINT = IdeasClient.DEFAULT;
	private static String BASE = Window.Location.getProtocol() + "//" + Window.Location.getHost();

	static {
		try {
			String casServer = casServer();
			if(casServer != null && !casServer.isEmpty())
			{ ENDPOINT = IdeasClient.NONE;
			  BASE = casServer;
			}
		} catch (Exception _) { }
	}
	
	public DWOIdeasClient() {
		super(BASE, ENDPOINT);
	}
	
}
