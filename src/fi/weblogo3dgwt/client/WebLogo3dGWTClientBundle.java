package fi.weblogo3dgwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ImageResource;

public interface WebLogo3dGWTClientBundle extends ClientBundle 
{
	   @Source("fi/weblogo3dgwt/client/css/WebLogo3dGWT.css")
	   public  WebLogo3dGWTCssResource getWebLogo3dGWTCssResource();

	   @Source("fi/weblogo3dgwt/client/images/rood.gif")
	   public ImageResource opaqueResource();
	   
	   @Source("fi/weblogo3dgwt/client/images/oranje.gif")
	   public ImageResource transparantResource();
	   
	   @Source("fi/weblogo3dgwt/client/images/zoominknop.gif")
	   public ImageResource zoomInResource();

	   @Source("fi/weblogo3dgwt/client/images/zoomuitknop.gif")
	   public ImageResource zoomUitResource();
	   
	   @Source("fi/weblogo3dgwt/client/images/grover.gif")
	   public ImageResource draadResource();
	   
	   @Source("fi/weblogo3dgwt/client/images/solid.gif")
	   public ImageResource solidResource();

}
