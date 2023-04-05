package fi.binomverdgwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;

public interface BinomVerdGWTClientBundle extends ClientBundle 
{
	   @Source("fi/binomverdgwt/client/images/foutkruis_klein.gif")
	   public ImageResource foutKruisResource();

	   @Source("fi/binomverdgwt/client/images/goedkrul_en_klein.gif")
	   public ImageResource goedKrulResource();
	   
	   @Source("fi/binomverdgwt/client/css/BinomVerdGWT.css")
	   public  BinomVerdGWTCssResource getBinomVerdGWTCSS();

}
