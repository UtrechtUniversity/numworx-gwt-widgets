package fi.verknippengwt.client;

import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface VerknippenGWTClientBundle extends ClientBundle 
{
	   @Source("fi/verknippengwt/client/images/reset1.gif")
	   public ImageResource resetResource();

	   @Source("fi/verknippengwt/client/images/teken_penknop_up.gif")
	   public ImageResource tekenUpResource();

	   @Source("fi/verknippengwt/client/images/teken_penknop_down.gif")
	   public ImageResource tekenDownResource();

	   @Source("fi/verknippengwt/client/images/teken_gumknop_up.gif")
	   public ImageResource gumResource();

	   @Source("fi/verknippengwt/client/images/goedkrul.gif")
	   public ImageResource goedKrulResource();

	   @Source("fi/verknippengwt/client/css/VerknippenGWT.css")
	   public VerknippenGWTCssResource getVerknippenGWTCssResource();

}
