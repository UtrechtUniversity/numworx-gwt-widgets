package fi.sliderwidgetgwt.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class SliderWidgetGWT implements EntryPoint, MouseDownHandler, MouseMoveHandler, MouseUpHandler {
	

	static final String upgradeMessage = 
			"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	public Canvas sliderGWTCanvas;
	public Context2d context;
	
	int sliderPosition;
	boolean dragging=false;
	
	public void onModuleLoad() {
		sliderGWTCanvas = Canvas.createIfSupported(); 

		sliderGWTCanvas.setWidth("560px");
		sliderGWTCanvas.setHeight("340px");
		sliderGWTCanvas.setCoordinateSpaceWidth(560);
		sliderGWTCanvas.setCoordinateSpaceHeight(340);

		if (sliderGWTCanvas == null) 
		{
	      RootPanel.get().add(new Label(upgradeMessage));
	      return;
	    }
		
		context = sliderGWTCanvas.getContext2d();		
		sliderGWTCanvas.addMouseMoveHandler(this);
		sliderGWTCanvas.addMouseDownHandler(this);
		sliderGWTCanvas.addMouseUpHandler(this);
		
		RootPanel.get().add(sliderGWTCanvas);
		
		
		sliderPosition=0;
		paint();
	}

	public void paint() {
		
		context.setFillStyle(CssColor.make(255,255,255));
		context.fillRect(0,0,560,340);
		
		context.beginPath();
		context.moveTo(20, 50);
		context.lineTo(220,50);
		context.stroke();
		
		context.setFillStyle(CssColor.make(255,0,0));
		context.setLineWidth(1);
		context.setStrokeStyle(CssColor.make(0,0,0));
		context.beginPath();
		context.arc(20+sliderPosition, 50, 8, 0, Math.PI * 2.0, true);
		context.closePath();
		context.fill();
	}
	@Override
	public void onMouseDown(MouseDownEvent event) {
		// TODO Auto-generated method stub
		
		if (event.getX()>=20+sliderPosition-4 && event.getX()<=20+sliderPosition+4 && event.getY()>=46 && event.getY()<=54)
			dragging=true;
		//Window.alert(Integer.toString(event.getX())+" "+Integer.toString(event.getY()));
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub
		if (dragging==true) {
			sliderPosition=event.getX()-20;
			if (sliderPosition<0)
				sliderPosition=0;
			if (sliderPosition>200)
				sliderPosition=200;
			paint();
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		// TODO Auto-generated method stub
		dragging=false;
	}
}
