package nl.numworx.geodefinergwt.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.numworx.geodefiner.common.Snapper;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class GeoDefinerGWTDebug extends GeoDefinerGWT implements EntryPoint {

	@Override
	public void onModuleLoad() {
		root = uiBinder.createAndBindUi(this);
		
		RootPanel.get().add(root);

		Map<String, Object> launchDebug = new HashMap<String, Object>();
		List<Integer> toolbox = Arrays.asList(0,1,2,3, 4,5,6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22);
		launchDebug.put("toolbox", toolbox);
		Map<String,Object> checkDWO = new HashMap<String,Object>();
		checkDWO.put("formule", "$ftrue@");
		checkDWO.put("score", 10);
		checkDWO.put("check", Boolean.TRUE);
		launchDebug.put("checkDWO", checkDWO);
		Map<String, Number> values = new HashMap<String, Number>();
		init(getWidth(), getHeight(), launchDebug, values);
		viewer.adapt(Snapper.class).setGravity(true);
	}

	
}
