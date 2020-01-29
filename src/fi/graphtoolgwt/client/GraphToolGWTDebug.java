package fi.graphtoolgwt.client;

import java.util.Collections;

import com.google.gwt.user.client.ui.RootPanel;

public class GraphToolGWTDebug extends GraphToolGWT {

	@Override
	public void onModuleLoad() {

		RootPanel.get().add(this);

	
		init1(300,600, Collections.emptyMap(), Collections.emptyMap());
	}

	
	
	
}
