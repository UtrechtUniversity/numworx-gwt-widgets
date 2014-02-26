package fi.geomalggwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;

public interface GeomAlgGWTClientBundle extends ClientBundle
{
	   @Source("fi/geomalggwt/client/images/reseticon.gif")
	   public ImageResource resetResource();
	
	   @Source("fi/geomalggwt/client/css/GeomAlgGWT.css")
	   public GeomAlgGWTCssResource getGeomAlgGWTCSS();

}
