package fi.mozarchgwt.client;

import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface MozarchGWTClientBundle extends ClientBundle 
{
	   //@Source("fi/verknippengwt/client/images/reset1.gif")
	   //public ImageResource resetResource();


	   @Source("fi/mozarchgwt/client/css/MozarchGWT.css")
	   public MozarchGWTCssResource getMozarchGWTCssResource();

}
