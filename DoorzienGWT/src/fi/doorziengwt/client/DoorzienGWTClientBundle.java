package fi.doorziengwt.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;


public interface DoorzienGWTClientBundle extends ClientBundle
{

   @Source("fi/doorziengwt/client/css/DoorzienGWT.css")
   public  DoorzienGWTCssResource getDoorzienGWTCSS();
   
   @Source("fi/doorziengwt/client/images/zoomin.gif")
   public ImageResource zoomInResource();

   @Source("fi/doorziengwt/client/images/zoomout.gif")
   public ImageResource zoomOutResource();
   
   @Source("fi/doorziengwt/client/images/reseticon.gif")
   public ImageResource resetResource();
   
   @Source("fi/doorziengwt/client/images/tools.gif")
   public ImageResource toolsResource();

   @Source("fi/doorziengwt/client/images/condraw.gif")
   public ImageResource conDrawResource();
   
   @Source("fi/doorziengwt/client/images/figure.gif")
   public ImageResource figureResource();
   
   @Source("fi/doorziengwt/client/images/cut.gif")
   public ImageResource cutResource();
   
   @Source("fi/doorziengwt/client/images/glue.gif")
   public ImageResource glueResource();
   
   @Source("fi/doorziengwt/client/images/drawline.gif")
   public ImageResource drawLineResource();
   
   @Source("fi/doorziengwt/client/images/deleteline.gif")
   public ImageResource deleteLineResource();

   @Source("fi/doorziengwt/client/images/drawplane.gif")
   public ImageResource drawPlaneResource();

   @Source("fi/doorziengwt/client/images/deleteplane.gif")
   public ImageResource deletePlaneResource();
   
   @Source("fi/doorziengwt/client/images/parplane.gif")
   public ImageResource parPlaneResource();
   
   @Source("fi/doorziengwt/client/images/planesfilled.gif")
   public ImageResource planesFilledResource();
   
   @Source("fi/doorziengwt/client/images/planesempty.gif")
   public ImageResource planesEmptyResource();
   
   @Source("fi/doorziengwt/client/images/hidecut.gif")
   public ImageResource hideCutResource();
   
   @Source("fi/doorziengwt/client/images/showcut.gif")
   public ImageResource showCutResource();
   
   @Source("fi/doorziengwt/client/images/lenglines.gif")
   public ImageResource lengLinesResource();
   
   @Source("fi/doorziengwt/client/images/shortlines.gif")
   public ImageResource shortLinesResource();
   
   @Source("fi/doorziengwt/client/images/solid.gif")
   public ImageResource solidResource();
   
   @Source("fi/doorziengwt/client/images/wireframe.gif")
   public ImageResource wireframeResource();

   @Source("fi/doorziengwt/client/images/redo.gif")
   public ImageResource redoResource();

   @Source("fi/doorziengwt/client/images/undo.gif")
   public ImageResource undoResource();

   
   
   
   
   
	
}
