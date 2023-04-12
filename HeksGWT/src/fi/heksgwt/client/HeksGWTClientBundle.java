package fi.heksgwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ClientBundle.Source;

public interface HeksGWTClientBundle extends ClientBundle 
{

   @Source("fi/heksgwt/client/css/HeksGWT.css")
   public HeksGWTCssResource getHeksGWTCssResource();
   
   
}
