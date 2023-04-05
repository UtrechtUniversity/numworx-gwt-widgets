package fi.algebraexprgwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;

public interface AlgebraExprGWTClientBundle extends ClientBundle
{
     @Source("fi/algebraexprgwt/client/images/foutkruis.gif")
	 public ImageResource foutKruisResource();

	 @Source("fi/algebraexprgwt/client/images/goedkrul_en.gif")
	 public ImageResource goedKrulEnResource();

	 @Source("fi/algebraexprgwt/client/images/goedkrul.gif")
	 public ImageResource goedKrulResource();
	 
	 @Source("fi/algebraexprgwt/client/images/goedkrulhalf.gif")
	 public ImageResource goedKruHalflResource();
	
	 @Source("fi/algebraexprgwt/client/css/AlgebraExprGWT.css")
	 public AlgebraExprGWTCssResource getAlgebraExprGWTCSS();

}
