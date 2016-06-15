package fi.statsimgwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;

public interface StatSimGWTClientBundle extends ClientBundle {

	   @Source("fi/statsimgwt/client/resources/gaussian.gif")
	   public ImageResource gaussian();
	   
	   @Source("fi/statsimgwt/client/resources/pixel.gif")
	   public ImageResource pixel();
	   
	   @Source("fi/statsimgwt/client/resources/scheveVerdeling.png")
	   public ImageResource scheveVerdeling();
	   
}
