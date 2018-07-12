package fi.kladjegwt.client;

import java.util.HashMap;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class KladjeGWTDebug extends KladjeGWT {

	public void onModuleLoad() 
	{
		getImages();
		
		dlp = new DockLayoutPanel(Style.Unit.PX);
		dlp.addStyleName(kladjeCss.dock());
		dlp.setPixelSize(breedte , hoogte );

		RootPanel.get().add(dlp);
		RootPanel.get().addStyleName(kladjeCss.root());
		
		//Stub.publish(this); 
		init(breedte, hoogte, new HashMap<String, Object>(), new HashMap<String, Number>());

	}	


}
