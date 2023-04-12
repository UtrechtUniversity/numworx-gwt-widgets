package fi.grafiek3dgwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;

public interface Grafiek3DGWTClientBundle extends ClientBundle 
{
	 @Source("fi/grafiek3dgwt/client/images/reset.gif")
	 public ImageResource resetResource();
	
	 @Source("fi/grafiek3dgwt/client/images/zoominknop.gif")
	 public ImageResource zoomInResource();

	 @Source("fi/grafiek3dgwt/client/images/zoomuitknop.gif")
	 public ImageResource zoomUitResource();
		
	 @Source("fi/grafiek3dgwt/client/css/Grafiek3DGWT.css")
	 public Grafiek3DGWTCssResource getGrafiek3DGWTCSS();
	 
	 @Source("fi/grafiek3dgwt/client/images/solid.gif")
	 public ImageResource solidResource();
	 
	 @Source("fi/grafiek3dgwt/client/images/wireframe.gif")
	 public ImageResource wireFrameResource();
	 
	 @Source("fi/grafiek3dgwt/client/images/fijner.gif")
	 public ImageResource fijnerResource();
	 
	 @Source("fi/grafiek3dgwt/client/images/grover.gif")
	 public ImageResource groverResource();
	 
	 @Source("fi/grafiek3dgwt/client/images/centraal.gif")
	 public ImageResource centraalResource();
	 
	 @Source("fi/grafiek3dgwt/client/images/parallel.gif")
	 public ImageResource parallelResource();
	 
	 @Source("fi/grafiek3dgwt/client/images/assenstelsel.gif")
	 public ImageResource assenResource();
	 
	 @Source("fi/grafiek3dgwt/client/images/geen_assen.gif")
	 public ImageResource geenAssenResource();

}
