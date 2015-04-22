package fi.tekenveelvlakgwt.client;

//import java.awt.*;
//import java.awt.event.*;

//import javax.swing.JPanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;


public class Slider	//extends JPanel implements MouseListener, MouseMotionListener
{	//private Image im;
	//private Graphics gIm;
	
	TekenVeelvlak eigenaar;
	
	Canvas sliderCanvas;
	Context2d sliderContext2d;

	//protected ActionListener actionListener = null;
	
	private int lengte;
	private int stand;
	private int muisStartX, muisStartY;
	private Polygon schuifKnop;
	private boolean raak;
	
	int horSize, vertSize;
	
	CssColor achtergrondKleur;
	
	public Slider(int aantalPix, int beginst, TekenVeelvlak eigen)
	{	lengte = aantalPix;
		stand = beginst;
		
		eigenaar = eigen;
		
		horSize = lengte+10;
		vertSize = 20;
		
		//addMouseListener(this);
		//addMouseMotionListener(this);
		//setSize(lengte+10,20);
		//setOpaque(true);
		
		sliderCanvas = Canvas.createIfSupported();
		sliderCanvas.setWidth(horSize + "px");
		sliderCanvas.setHeight(vertSize + "px");
		sliderCanvas.setCoordinateSpaceWidth(horSize);
		sliderCanvas.setCoordinateSpaceHeight(vertSize);

		sliderCanvas.addStyleName(TekenVeelvlakGWT.tekenVeelvlakGWTCssResource.canvas());
		
		sliderContext2d = sliderCanvas.getContext2d();
		
    	MouseHandler mouseHandler = new MouseHandler();
    	sliderCanvas.addMouseDownHandler(mouseHandler);
    	sliderCanvas.addMouseMoveHandler(mouseHandler);
    	sliderCanvas.addMouseUpHandler(mouseHandler);

      	TouchHandler touchHandler = new TouchHandler();
      	sliderCanvas.addTouchStartHandler(touchHandler);
      	sliderCanvas.addTouchMoveHandler(touchHandler);
      	sliderCanvas.addTouchEndHandler(touchHandler);

	}
	
	public void paint()
	{
		paint(sliderContext2d);
	}
	
	//public void paint(Graphics g)
	public void paint(Context2d g)
	{	{ 	//if(im==null)
			//{	im = createImage(getSize().width,getSize().height);
  			//	gIm = im.getGraphics();
			//}
			//gIm.setColor(getBackground());
			g.setFillStyle(achtergrondKleur);
			g.fillRect(0,0,horSize, vertSize);
			tekenSlider(g);
			//g.drawImage(im, 0, 0, null);
  		}
	}
	
//	public void update(Graphics g)
//	{	paint(g);
//	}
	
//	public void tekenSlider(Graphics g)
	public void tekenSlider(Context2d g)
	{	//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.setFillStyle(CssColor.make(0,0,0));
		//g.drawRect(5,7,lengte,6);
		g.strokeRect(5,7,lengte,6);
		schuifKnop = new Polygon();
		schuifKnop.addPoint(5+stand,0);
		schuifKnop.addPoint(5+stand+3,5);
		schuifKnop.addPoint(5+stand+3,15);
		schuifKnop.addPoint(5+stand,20);
		schuifKnop.addPoint(5+stand-3,15);
		schuifKnop.addPoint(5+stand-3,5);
		//g.fillPolygon(schuifKnop);
    	g.moveTo(schuifKnop.puntenX[0], schuifKnop.puntenY[0]);
		g.beginPath();
		for (int k = 1; k < schuifKnop.aantalPunten; k++)
		{	g.lineTo(schuifKnop.puntenX[k], schuifKnop.puntenY[k]);
		}
		g.lineTo(schuifKnop.puntenX[0], schuifKnop.puntenY[0]);
		g.closePath();
		g.fill();
		
		//g.drawPolygon(schuifKnop);
    	g.moveTo(schuifKnop.puntenX[0], schuifKnop.puntenY[0]);
		g.beginPath();
		for (int k = 1; k < schuifKnop.aantalPunten; k++)
		{	g.lineTo(schuifKnop.puntenX[k], schuifKnop.puntenY[k]);
		}
		g.lineTo(schuifKnop.puntenX[0], schuifKnop.puntenY[0]);
		g.closePath();
		g.stroke();

	}
	
//	public void addActionListener(ActionListener l) 
// 	{	actionListener = AWTEventMulticaster.add(actionListener,l);
// 	}
 	
// 	public void removeActionListener(ActionListener l)
// 	{	actionListener = AWTEventMulticaster.remove(actionListener, l);
// 	}
	
	public int geefStand()
	{	return stand;
	}

	public int geefLengte()
	{	return lengte;
	}
	
	public void zetStand(int std)
	{	if (std > lengte)stand = lengte;
		else if(std < 0)
			stand = 0;
		else 
			stand = std;
		paint();
	}
	
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		boolean mouseDown = false;
		
		public void onMouseDown(MouseDownEvent e)
		{
			//e.preventDefault();
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDown = true;
			
			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		public void onMouseMove(MouseMoveEvent e)	
		{
			//e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseMoveTouchMoveAction(eventX, eventY);
			
			
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{
			//e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			//mouseUpTouchEndAction();

		}

	} //MouseHandler

	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				int eventX = touch.getPageX() - sliderCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - sliderCanvas.getAbsoluteTop();
				
				mouseDownTouchStartAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
			    //boolean shiftPressed = false;
			    int eventX = touch.getPageX() - sliderCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - sliderCanvas.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			//mouseUpTouchEndAction();
		}

	}

	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	raak = true;  //(new Rectangle(stand-5,0,20,20)).contains(e.getX(), e.getY());
		stand = eventX-5;
		if (stand > lengte) 
		{	stand = lengte;
		}
		else if(stand < 0) 
		{	stand = 0;
		}
		paint();
		//if (actionListener != null)
 		//{	actionListener.actionPerformed( new ActionEvent(this, 0, "verschoven") );
 		//}
		eigenaar.sliderAction();
		muisStartX = eventX;
		muisStartY = eventY;
	}
	
	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	if (!raak && new Rectangle(stand+5,0,10,20).contains(eventX, eventY))
		{	raak = true;
			muisStartX = eventX;
		}
		if (raak)
		{	int x = eventX;
			int dx = x - muisStartX;
			stand = x-5;//stand + dx;
			if(stand>lengte) 
			{	stand = lengte;
			}
			else if(stand<0) 
			{	stand = 0;
			}
			if(x<10 || x>lengte+20)
			{	//raak = false;
			}
			paint();
			//if (actionListener != null)
 			//{	actionListener.actionPerformed( new ActionEvent(this, 0, "verschoven") );
 			//}
			eigenaar.sliderAction();
			muisStartX = x;
		}
	}
	
	//public void mouseReleased(MouseEvent e){;}
	//public void mouseClicked(MouseEvent e){;}
	//public void mouseExited(MouseEvent e){;}
	//public void mouseEntered(MouseEvent e){;}
	//public void mouseMoved(MouseEvent e){;}
}
