package fi.nabouwenaanzichtengwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;


public interface NabouwenAanzichtenGWTClientBundle extends ClientBundle 
{

   @Source("fi/nabouwenaanzichtengwt/client/resources/footerbgimage.png")
   public ImageResource footerbgimage();
   
   @Source("fi/nabouwenaanzichtengwt/client/resources/vinkjegrijs.png")
   public ImageResource vinkjegrijs();
   
   @Source("fi/nabouwenaanzichtengwt/client/resources/vinkjegeel.png")
   public ImageResource vinkjegeel();
   
   @Source("fi/nabouwenaanzichtengwt/client/resources/vinkjerood.png")
   public ImageResource vinkjerood();
   
   @Source("fi/nabouwenaanzichtengwt/client/resources/vinkje.png")
   public ImageResource vinkje();

  
   
   @Source("fi/nabouwenaanzichtengwt/client/resources/NabouwenAanzichtenGWT.css")
   public  NabouwenAanzichtenGWTCssResource getNabouwenAanzichtenGWTCSS();

}
