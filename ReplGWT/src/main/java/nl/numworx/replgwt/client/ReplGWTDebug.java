package nl.numworx.replgwt.client;

import java.util.Map;
import com.google.gwt.core.client.GWT;


public class ReplGWTDebug extends ReplGWT {
	@Override
	public void init(int width, int height, Map<String, Object> launchData, Map<String, Number> values) {
		super.init(width, height, launchData, values);
		
//		startInput();
	}

	public void onModuleLoad() {
		  modules = GWT.getModuleBaseURL();
		 // installServiceWorker(this);
		  install(this);
		  init(600,300,null, null);
	  }


}
