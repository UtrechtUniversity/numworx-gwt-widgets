package fi.stroomdiagrammengwt.client;

//import java.awt.*;

//import javax.swing.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


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
    
    int xPos, yPos, breedte, hoogte;
    // constructor
    public LWArrowButton(int dir, CssColor bg)
    {   // wrong direction gives up arrow
        if ( (dir >= 0) && (dir <= 3) )
            direction = dir;
        else
            direction = 0;
        bgColor = bg;    
    }
    
    public void setBounds(int x, int y, int b, int h)
    {
    	xPos = x; yPos = y; breedte = b; hoogte = h;
    }
    
    public void setLocation(int x, int y)
    {
    	xPos = x; yPos = y;
    }
    
    // paint
    //public void paintComponent(Graphics g)
    public void paintComponent(Context2d g)
    {   //g.setColor(bgColor);
    	g.setFillStyle(bgColor);
        //g.fillRect(0, 0, getSize().width, getSize().height);
    	g.fillRect(xPos, yPos, breedte, hoogte);
        // construct arrow
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        int nPoints = 3;
        switch (direction)
        {   case 0: // up arrow
            {   xPoints[0] = xPos + breedte / 2; //getSize().width / 2;
                xPoints[1] = xPos + breedte / 4; //getSize().width / 4;
                xPoints[2] = xPos + (breedte / 4) * 3; //(getSize().width / 4) * 3;
                yPoints[0] = yPos + hoogte / 4; //getSize().height / 4;
                yPoints[1] = yPos + (hoogte / 4) * 3; //(getSize().height / 4) * 3;
                yPoints[2] = yPos + (hoogte / 4) * 3; //(getSize().height / 4) * 3;
            }
            break;
            case 1: // right arrow
            {   xPoints[0] = xPos + breedte / 4; //getSize().width / 4;
                xPoints[1] = xPos + (breedte / 4) * 3; //(getSize().width / 4) * 3;
                xPoints[2] = xPos + breedte / 4; //getSize().width / 4;
                yPoints[0] = yPos + hoogte / 4; //getSize().height / 4;
                yPoints[1] = yPos + hoogte / 2; //getSize().height / 2;
                yPoints[2] = yPos + (hoogte / 4) * 3; //(getSize().height / 4) * 3;
            }
            break;
            case 2: // down arrow
            {   xPoints[0] = xPos + breedte / 4; //getSize().width / 4;
                xPoints[1] = xPos + (breedte / 4) * 3; //(getSize().width / 4) * 3;
                xPoints[2] = xPos + breedte / 2; //getSize().width / 2;
                yPoints[0] = yPos + hoogte / 4; //getSize().height / 4;
                yPoints[1] = yPos + hoogte / 4; //getSize().height / 4;
                yPoints[2] = yPos + (hoogte / 4) * 3; //(getSize().height / 4) * 3;
            }
            break;
            case 3: // left arrow
            {   xPoints[0] = xPos + (breedte / 4) * 3; //(getSize().width / 4) * 3;
                xPoints[1] = xPos + breedte / 4; //getSize().width / 4;
                xPoints[2] = xPos + (breedte / 4) * 3; //(getSize().width / 4) * 3;
                yPoints[0] = yPos + hoogte / 4; //getSize().height / 4;
                yPoints[1] = yPos + hoogte / 2; //getSize().height / 2;
                yPoints[2] = yPos + (hoogte / 4) * 3; //(getSize().height / 4) * 3;
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
        {   //g.fillPolygon(p);
        	g.beginPath();
        	g.moveTo(p.doubleX[0], p.doubleY[0]);
        	for (int pCnt = 1; pCnt < p.aantalPunten; pCnt++)
        	{	g.lineTo(p.doubleX[pCnt], p.doubleY[pCnt]);
        	}
        	g.lineTo(p.doubleX[0], p.doubleY[0]);
        	g.closePath();
        	g.fill();
        	
        }
        else
        {   //g.drawPolygon(p);
        	g.beginPath();
        	g.moveTo(p.doubleX[0], p.doubleY[0]);
        	for (int pCnt = 1; pCnt < p.aantalPunten; pCnt++)
        	{	g.lineTo(p.doubleX[pCnt], p.doubleY[pCnt]);
        	}
        	g.lineTo(p.doubleX[0], p.doubleY[0]);
        	g.closePath();
        	g.stroke();
        
        }
        
        // paint button outline
        //g.setColor(Color.white);
        g.setStrokeStyle(CssColor.make(255,255,255));
        //g.drawLine(0, 0, getSize().width - 1, 0);
        g.beginPath();
        g.moveTo(xPos, yPos);
        g.lineTo(xPos + breedte - 1, yPos);
        g.stroke();

        //g.drawLine(1, 1, getSize().width - 2, 1);
        g.beginPath();
        g.moveTo(xPos + 1, yPos + 1);
        g.lineTo(xPos + breedte - 2, yPos + 1);
        g.stroke();

        //g.drawLine(0, 0, 0, getSize().height - 1);
        g.beginPath();
        g.moveTo(xPos, yPos);
        g.lineTo(xPos, yPos + hoogte - 1);
        g.stroke();
       
        //g.drawLine(1, 1, 1, getSize().height - 2);
        g.beginPath();
        g.moveTo(xPos + 1, yPos + 1);
        g.lineTo(xPos + 1, yPos + hoogte - 2);
        g.stroke();
        
        //g.setColor(Color.black);
        g.setStrokeStyle(CssColor.make(0,0,0));
        //g.drawLine(0, getSize().height - 1, getSize().width - 1, getSize().height - 1);
        g.beginPath();
        g.moveTo(xPos, yPos + hoogte - 1);
        g.lineTo(xPos + breedte - 1, yPos + hoogte - 1);
        g.stroke();

        //g.drawLine(1, getSize().height - 2, getSize().width - 2, getSize().height - 2);
        g.beginPath();
        g.moveTo(xPos + 1, yPos + hoogte - 2);
        g.lineTo(xPos + breedte - 2, yPos + hoogte - 2);
        g.stroke();
        
        //g.drawLine(getSize().width - 1, 0, getSize().width - 1, getSize().height - 1);
        g.beginPath();
        g.moveTo(xPos + breedte - 1, yPos);
        g.lineTo(xPos + breedte - 1, yPos + hoogte - 1);
        g.stroke();
        
        //g.drawLine(getSize().width - 2, 1, getSize().width - 2, getSize().height - 2);
        g.beginPath();
        g.moveTo(xPos + breedte - 2, yPos + 1);
        g.lineTo(xPos + breedte - 2, yPos + hoogte - 2);
        g.stroke();
                   
    } // paint

    // redefined method
    public void setEnabled(boolean b)
    {   enabled = b;
        //super.setEnabled(b);
//GWT    
        //repaint();
    }
    

} // LWArrowButton

