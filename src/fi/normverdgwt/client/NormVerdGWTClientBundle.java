package fi.normverdgwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;

public interface NormVerdGWTClientBundle extends ClientBundle 
{

   @Source("fi/normverdgwt/client/images/foutkruis_klein.gif")
   public ImageResource foutKruisResource();

   @Source("fi/normverdgwt/client/images/goedkrul_en_klein.gif")
   public ImageResource goedKrulResource();

}
