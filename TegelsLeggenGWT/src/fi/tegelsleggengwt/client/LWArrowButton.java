package fi.tegelsleggengwt.client;

//import java.awt.*;

//import javax.swing.*;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;


// a Light Weight Button with an arrow on it
public class LWArrowButton //extends JButton //LWContainer
{   // the direction of the arrow 0 = up, 1 = right, 2 = down, 3 = left
    int direction;
    // polygon for the arrow
    Polygon p;
    // flagg for being enabled
    boolean enabled = true;
    //
    CssColor bgColor;
    int breedte, hoogte;
    
    Canvas buttonCanvas;
    Context2d buttonContext2d;
    
    // constructor
    public LWArrowButton(int b, int h, int dir, CssColor bg)
    {   
    	breedte = b;
    	hoogte = h;
    	
    	buttonCanvas = Canvas.createIfSupported();
		if (buttonCanvas != null)
		{	
			buttonCanvas.setWidth(b + "px");
			buttonCanvas.setHeight(h + "px");
			buttonCanvas.setCoordinateSpaceWidth(b);
			buttonCanvas.setCoordinateSpaceHeight(h);
		}	
		
		
//		MouseHandler mouseHandler = new MouseHandler();
//		buttonCanvas.addMouseDownHandler(mouseHandler);
//		buttonCanvas.addMouseMoveHandler(mouseHandler);
//		buttonCanvas.addMouseUpHandler(mouseHandler);
		
//		TouchHandler touchHandler = new TouchHandler();
//		buttonCanvas.addTouchStartHandler(touchHandler);
//		buttonCanvas.addTouchMoveHandler(touchHandler);
//		buttonCanvas.addTouchEndHandler(touchHandler);

		buttonContext2d = buttonCanvas.getContext2d();

    	
    	// wrong direction gives up arrow
        if ( (dir >= 0) && (dir <= 3) )
            direction = dir;
        else
            direction = 0;
        bgColor = bg;    
    }
/*    
    public LWContainer getCopy()
    {   return null;
    }
*/        
    
    public void paint()
    {
    	paintComponent(buttonContext2d);
    }
    
    // paint
    public void paintComponent(Context2d g)
    {   //g.setColor(bgColor);
    	g.setFillStyle(bgColor);
        g.fillRect(0, 0, breedte, hoogte);
        // construct arrow
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        int nPoints = 3;
        switch (direction)
        {   case 0: // up arrow
            {   xPoints[0] = breedte / 2;
                xPoints[1] = breedte / 4;
                xPoints[2] = (breedte / 4) * 3;
                yPoints[0] = hoogte / 4;
                yPoints[1] = (hoogte / 4) * 3;
                yPoints[2] = (hoogte / 4) * 3;
            }
            break;
            case 1: // right arrow
            {   xPoints[0] = breedte / 4;
                xPoints[1] = (breedte / 4) * 3;
                xPoints[2] = breedte / 4;
                yPoints[0] = hoogte / 4;
                yPoints[1] = hoogte / 2;
                yPoints[2] = (hoogte / 4) * 3;
            }
            break;
            case 2: // down arrow
            {   xPoints[0] = breedte / 4;
                xPoints[1] = (breedte / 4) * 3;
                xPoints[2] = breedte / 2;
                yPoints[0] = hoogte / 4;
                yPoints[1] = hoogte / 4;
                yPoints[2] = (hoogte / 4) * 3;
            }
            break;
            case 3: // left arrow
            {   xPoints[0] = (breedte / 4) * 3;
                xPoints[1] = breedte / 4;
                xPoints[2] = (breedte / 4) * 3;
                yPoints[0] = hoogte / 4;
                yPoints[1] = hoogte / 2;
                yPoints[2] = (hoogte / 4) * 3;
            }
            break;
            default: // nothing, see constructor
        } // switch
        p = new Polygon(xPoints, yPoints, nPoints);
        // paint arrow
        //g.setColor(Color.black);
        g.setFillStyle(CssColor.make(0,0,0));
        g.setStrokeStyle(CssColor.make(0,0,0));
        if (enabled)
        {    //g.fillPolygon(p);
        	 g.beginPath();		
        	 g.moveTo(p.doubleX[0], p.doubleY[0]);
        	 for (int k = 1; k < p.aantalPunten; k++) 
        	 {	g.lineTo(p.doubleX[k], p.doubleY[k]);
        	 }
        	 g.lineTo(p.doubleX[0], p.doubleY[0]);
        	 g.closePath();
        	 g.fill();

        }
        else
        {    //g.drawPolygon(p);
        
			 g.beginPath();		
			 g.moveTo(p.doubleX[0], p.doubleY[0]);
			 for (int k = 1; k < p.aantalPunten; k++) 
			 {	g.lineTo(p.doubleX[k], p.doubleY[k]);
			 }
			 g.lineTo(p.doubleX[0], p.doubleY[0]);
			 g.closePath();
			 g.stroke();
        }
/*        
        // paint button outline
        g.setColor(Color.white);
        g.drawLine(0, 0, getSize().width - 1, 0);
        g.drawLine(1, 1, getSize().width - 2, 1);
        g.drawLine(0, 0, 0, getSize().height - 1);
        g.drawLine(1, 1, 1, getSize().height - 2);
        g.setColor(Color.black);
        g.drawLine(0, getSize().height - 1,
                   getSize().width - 1, getSize().height - 1);
        g.drawLine(1, getSize().height - 2,
                   getSize().width - 2, getSize().height - 2);
        g.drawLine(getSize().width - 1, 0,
                   getSize().width - 1, getSize().height - 1);
        g.drawLine(getSize().width - 2, 1,
                   getSize().width - 2, getSize().height - 2);
*/                   
    } // paint

    // redefined method
    public void setEnabled(boolean b)
    {   enabled = b;
        //super.setEnabled(b);
        paintComponent(buttonContext2d);
    }
    


} // LWArrowButton

