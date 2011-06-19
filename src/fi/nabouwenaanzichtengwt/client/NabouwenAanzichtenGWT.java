package fi.nabouwenaanzichtengwt.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;


public class NabouwenAanzichtenGWT implements EntryPoint {
  static final String holderId = "canvasholder";

  static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

  Canvas canvas;
  Canvas backBuffer;
  
  // mouse positions relative to canvas
  int mouseX, mouseY;

  //timer refresh rate, in milliseconds
  static final int refreshRate = 25;
  
  // canvas size, in px
  static final int height = 400;
  static final int width = 400;
  
  final CssColor redrawColor = CssColor.make("rgba(255,255,255,0.6)");
  Context2d context;
  Context2d backBufferContext;
  
  public void onModuleLoad() {
	
	  int maxAantal = 6;
	boolean[][][] b = new boolean[maxAantal][maxAantal][maxAantal];
	for(int i=0 ; i<maxAantal ; i++)
	{	for(int j=0 ; j<maxAantal ; j++)
		{	for(int k=0 ; k<maxAantal ; k++)
			{	if(k==0 && j==0 && i==0) ;
				b[i][j][k]=false;
			}
		}
	}
	Viewer3d  vWerk = new Viewer3d(new KubusRooster(b,1), 351, -30, 450, 450, this);
		//vWerk.zetAchtergrond(bgcolor);
		vWerk.zetAfstand(1000);
		vWerk.zetSchaduw(true);
		vWerk.zetBeginHoeken(30,-30);
		vWerk.zetMuisAan(true);
		//vWerk.zetGetalRooster(true);
		
		canvas = vWerk.getCanvas();
		if (canvas == null) {
	      RootPanel.get(holderId).add(new Label(upgradeMessage));
	      return;
	    }
		
		RootPanel.get(holderId).add(canvas);
		
		vWerk.initContext2d();
		
		vWerk.draw();
    /*canvas = Canvas.createIfSupported();
    backBuffer = Canvas.createIfSupported();
    if (canvas == null) {
      RootPanel.get(holderId).add(new Label(upgradeMessage));
      return;
    }

    // init the canvases
    canvas.setWidth(width + "px");
    canvas.setHeight(height + "py");
    canvas.setCoordinateSpaceWidth(width);
    canvas.setCoordinateSpaceHeight(height);
    backBuffer.setCoordinateSpaceWidth(width);
    backBuffer.setCoordinateSpaceHeight(height);
    RootPanel.get(holderId).add(canvas);
    context = canvas.getContext2d();
    backBufferContext = backBuffer.getContext2d();
    
    // init the objects
    
    // init handlers
    initHandlers();
    
    // setup timer
    final Timer timer = new Timer() {
      @Override
      public void run() {
        doUpdate();
      }
    };
    timer.scheduleRepeating(refreshRate);
  }

  void doUpdate() {
    // update the back canvas
    backBufferContext.setFillStyle(redrawColor);
    backBufferContext.fillRect(0, 0, width, height);



    // update the front canvas
     
     */
    
  }
  
  void zetVeranderd() {
	  
  }
  
  boolean isBouwen() {
	  return true;
  }
  
  void initHandlers() {
    canvas.addMouseMoveHandler(new MouseMoveHandler() {
      public void onMouseMove(MouseMoveEvent event) {
        mouseX = event.getRelativeX(canvas.getElement());
        mouseY = event.getRelativeY(canvas.getElement());
      }
    });
    
    

    canvas.addMouseOutHandler(new MouseOutHandler() {
      public void onMouseOut(MouseOutEvent event) {
        mouseX = -200;
        mouseY = -200;
      }
    });

    canvas.addTouchMoveHandler(new TouchMoveHandler() {
      public void onTouchMove(TouchMoveEvent event) {
        event.preventDefault();
        if (event.getTouches().length() > 0) {
          Touch touch = event.getTouches().get(0);
          mouseX = touch.getRelativeX(canvas.getElement());
          mouseY = touch.getRelativeY(canvas.getElement());
        }
        event.preventDefault();
      }
    });

    canvas.addTouchEndHandler(new TouchEndHandler() {
      public void onTouchEnd(TouchEndEvent event) {
        event.preventDefault();
        mouseX = -200;
        mouseY = -200;
      }
    });

    canvas.addGestureStartHandler(new GestureStartHandler() {
      public void onGestureStart(GestureStartEvent event) {
        event.preventDefault();
      }
    });
  }
}
