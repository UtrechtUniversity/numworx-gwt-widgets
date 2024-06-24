package nl.numworx.sqlitegwt.client;

import java.util.Collections;
import java.util.Map;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;


public class SQLiteGWTDebug extends SQLiteGWT {

	public void onModuleLoad() {
		  modules = GWT.getModuleBaseURL();
		  install(this);
		  Map<String, Object> launchData = Collections.singletonMap("url", "https://www.fi.uu.nl/dwo/resources/sqlite_danilo.db");
		  init(600,300,launchData, null);
		  Document.get().getElementById("content").getStyle().setTop(13, Style.Unit.EM);
	  }


}
