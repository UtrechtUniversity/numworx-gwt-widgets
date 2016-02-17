package fi.tegelsleggengwt.client;

import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface TegelsLeggenGWTClientBundle extends ClientBundle 
{
	
	   @Source("fi/tegelsleggengwt/client/css/TegelsLeggenGWT.css")
	   public TegelsLeggenGWTCssResource getTegelsLeggenGWTCssResource();

	   @Source("fi/tegelsleggengwt/client/images/zwart.gif")
	   public ImageResource zwartResource();

	   @Source("fi/tegelsleggengwt/client/images/grijs.gif")
	   public ImageResource grijsResource();

	   @Source("fi/tegelsleggengwt/client/images/rood.gif")
	   public ImageResource roodResource();
	   
	   @Source("fi/tegelsleggengwt/client/images/oranje.gif")
	   public ImageResource oranjeResource();
	   
	   @Source("fi/tegelsleggengwt/client/images/groen.gif")
	   public ImageResource groenResource();
	   
	   @Source("fi/tegelsleggengwt/client/images/cyaan.gif")
	   public ImageResource cyaanResource();
	   
	   @Source("fi/tegelsleggengwt/client/images/blauw.gif")
	   public ImageResource blauwResource();
	   
	   @Source("fi/tegelsleggengwt/client/images/magenta.gif")
	   public ImageResource magentaResource();
	   
	   @Source("fi/tegelsleggengwt/client/images/geel.gif")
	   public ImageResource geelResource();

}
