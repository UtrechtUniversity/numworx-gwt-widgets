package nl.numworx.replgwt.client;

import java.util.Map;
import java.util.function.Consumer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;


public class ReplGWTDebug extends ReplGWT implements Consumer<String> {
	private InputReader w;
	protected boolean consuming;
	
	@Override
	public void init(int width, int height, Map<String, Object> launchData, Map<String, Number> values) {
		super.init(width, height, launchData, values);
		
		w = new InputReader();
		w.setConsumer(this);
//		startInput();
	}

	protected void startInput() {
		RootPanel.get("output").add(w);
		w.start();
		consuming = true;
	}

	public void onModuleLoad() {
		  modules = GWT.getModuleBaseURL();
		  installServiceWorker(this);
		  install(this);
		  init(600,300,null, null);
	  }

	@Override
	public void accept(String t) {
		w.removeFromParent();
		consuming = false;
		t += "\n";
		printx(t);
		service.postMessage((t).toString());
	}

	protected void setServiceWorker(ServiceWorker w) {
		service = w;
		  java.util.logging.Logger.getLogger("ReplGWTDebug").severe(String.valueOf(w));
	}

	@Override
	protected void requestInput() {
		if (consuming) {
			  java.util.logging.Logger.getLogger("ReplGWTDebug").severe("consuming");
			  return;
		}
		startInput();
	}

}
