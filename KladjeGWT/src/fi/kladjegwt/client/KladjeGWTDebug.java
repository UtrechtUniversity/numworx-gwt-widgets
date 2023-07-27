package fi.kladjegwt.client;

import java.util.HashMap;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class KladjeGWTDebug extends KladjeGWT {

	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(kladjeCss.dock());
		dlp.setPixelSize(breedte , hoogte );

		RootLayoutPanel.get().add(dlp);
		RootLayoutPanel.get().addStyleName(kladjeCss.root());
		
		//Stub.publish(this); 
		HashMap<String, Object> launchdata = new HashMap<String, Object>();
		launchdata.put("formuleOptie", Boolean.TRUE);
		init(breedte, hoogte, launchdata, new HashMap<String, Number>());
		//setCommunicationRoot(new MockOpdrNav());
	}	


}
