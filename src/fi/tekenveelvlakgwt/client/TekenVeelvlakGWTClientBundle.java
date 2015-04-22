package fi.tekenveelvlakgwt.client;

import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface TekenVeelvlakGWTClientBundle extends ClientBundle 
{
	   @Source("fi/tekenveelvlakgwt/client/images/foutkruis_klein.gif")
	   public ImageResource foutKruisResource();

	   @Source("fi/tekenveelvlakgwt/client/images/goedkrul_en_klein.gif")
	   public ImageResource goedKrulResource();

	
	   @Source("fi/tekenveelvlakgwt/client/css/TekenVeelvlakGWT.css")
	   public TekenVeelvlakGWTCssResource getTekenVeelvlakGWTCssResource();

}
