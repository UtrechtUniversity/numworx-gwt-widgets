package fi.stippelpatronengwt.client;

import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface StippelPatronenGWTClientBundle extends ClientBundle 
{
	   @Source("fi/stippelpatronengwt/client/images/foutkruis.gif")
	   public ImageResource foutKruisResource();

	   @Source("fi/stippelpatronengwt/client/images/goedkrul.gif")
	   public ImageResource goedKrulResource();

	   @Source("fi/stippelpatronengwt/client/images/goedkrulhalf.gif")
	   public ImageResource goedKrulHalfResource();
	
	   @Source("fi/stippelpatronengwt/client/css/StippelPatronenGWT.css")
	   public StippelPatronenGWTCssResource getStippelPatronenGWTCssResource();

}
