package nl.numworx.replgwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;


public class ReplGWTDebug extends ReplGWT {
	  public void onModuleLoad() {
		  modules = GWT.getModuleBaseURL();
		  install(this);
		  init(600,300,null, null);
	  }

}
