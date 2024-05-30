package nl.numworx.sqlitegwt.client;

import java.util.Map;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;


public class SQLiteGWTDebug extends SQLiteGWT {
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
		  Document.get().getElementById("content").getStyle().setTop(13, Style.Unit.EM);
	  }


}
