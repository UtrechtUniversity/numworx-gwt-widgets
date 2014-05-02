package fi.draaibankgwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;

public interface DraaibankGWTClientBundle extends ClientBundle 
{


   @Source("fi/draaibankgwt/client/images/zoominknop.gif")
   public ImageResource vergrootResource();

   @Source("fi/draaibankgwt/client/images/zoomuitknop.gif")
   public ImageResource verkleinResource();


}
