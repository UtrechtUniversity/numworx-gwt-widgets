package fi.stroomdiagrammengwt.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * class simulating an up/right/down/left arrow button 
 */
public class LWArrowButton 
{   
	/**
	 * direction of the arrow 0 = up, 1 = right, 2 = down, 3 = left
	 */
    int direction;

    /**
     * polygon for the arrow
     */
    Polygon p;

    /**
     * flagg for being enabled
     */
    boolean enabled = true;

    /**
     * background color of the arrow button
     */
    CssColor bgColor;

    /**
     * simulating a component: x-position
     */
    int xPos;
    /**
     * simulating a component: y-position
     */
    int yPos;
    /**
     * simulating a component: width
     */
    int breedte;
    /**
     * simulating a component: height
     */
    int hoogte;
    // constructor
    /**
     * constructor
     * @param dir direction of the arrow button
     * @param bg background color of the arrow button
     */
    public LWArrowButton(int dir, CssColor bg)
    {   // wrong direction gives up arrow
        if ( (dir >= 0) && (dir <= 3) )
            direction = dir;
        else
            direction = 0;
        bgColor = bg;    
    }
    
    /**
     * simulating a component: setBounds
     * @param x x-position
     * @param y y-position
     * @param b width
     * @param h height
     */
    public void setBounds(int x, int y, int b, int h)
    {
    	xPos = x; yPos = y; breedte = b; hoogte = h;
    }
    
    /**
     * simulating a component
     * @param x x-position
     * @param y y-position
     */
    public void setLocation(int x, int y)
    {
    	xPos = x; yPos = y;
    }
    
    /**
     * paint the arrow button using Context2d g
     * @param g the Context2d g
     */
    public void paintComponent(Context2d g)
    {   
    	g.setFillStyle(bgColor);
    	g.fillRect(xPos, yPos, breedte, hoogte);
        // construct arrow
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        int nPoints = 3;
        switch (direction)
        {   case 0: // up arrow
            {   xPoints[0] = xPos + breedte / 2; 
                xPoints[1] = xPos + breedte / 4; 
                xPoints[2] = xPos + (breedte / 4) * 3; 
                yPoints[0] = yPos + hoogte / 4; 
                yPoints[1] = yPos + (hoogte / 4) * 3; 
                yPoints[2] = yPos + (hoogte / 4) * 3; 
            }
            break;
            case 1: // right arrow
            {   xPoints[0] = xPos + breedte / 4; 
                xPoints[1] = xPos + (breedte / 4) * 3; 
                xPoints[2] = xPos + breedte / 4; 
                yPoints[0] = yPos + hoogte / 4; 
                yPoints[1] = yPos + hoogte / 2; 
                yPoints[2] = yPos + (hoogte / 4) * 3; 
            }
            break;
            case 2: // down arrow
            {   xPoints[0] = xPos + breedte / 4; 
                xPoints[1] = xPos + (breedte / 4) * 3; 
                xPoints[2] = xPos + breedte / 2; 
                yPoints[0] = yPos + hoogte / 4; 
                yPoints[1] = yPos + hoogte / 4; 
                yPoints[2] = yPos + (hoogte / 4) * 3; 
            }
            break;
            case 3: // left arrow
            {   xPoints[0] = xPos + (breedte / 4) * 3; 
                xPoints[1] = xPos + breedte / 4; 
                xPoints[2] = xPos + (breedte / 4) * 3; 
                yPoints[0] = yPos + hoogte / 4; 
                yPoints[1] = yPos + hoogte / 2; 
                yPoints[2] = yPos + (hoogte / 4) * 3; 
            }
            break;
            default: // nothing, see constructor
        } // switch
        p = new Polygon(xPoints, yPoints, nPoints);
        // paint arrow: solid if enabled, outline if not enabled
        g.setFillStyle(CssColor.make(0,0,0));
        g.setStrokeStyle(CssColor.make(0,0,0));
        if (enabled)
        {   
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
        {   
        	g.beginPath();
        	g.moveTo(p.doubleX[0], p.doubleY[0]);
        	for (int pCnt = 1; pCnt < p.aantalPunten; pCnt++)
        	{	g.lineTo(p.doubleX[pCnt], p.doubleY[pCnt]);
        	}
        	g.lineTo(p.doubleX[0], p.doubleY[0]);
        	g.closePath();
        	g.stroke();
        
        }
        
        // paint button outline: white lines
        g.setStrokeStyle(CssColor.make(255,255,255));
        g.beginPath();
        g.moveTo(xPos, yPos);
        g.lineTo(xPos + breedte - 1, yPos);
        g.stroke();

        g.beginPath();
        g.moveTo(xPos + 1, yPos + 1);
        g.lineTo(xPos + breedte - 2, yPos + 1);
        g.stroke();

        g.beginPath();
        g.moveTo(xPos, yPos);
        g.lineTo(xPos, yPos + hoogte - 1);
        g.stroke();
       
        g.beginPath();
        g.moveTo(xPos + 1, yPos + 1);
        g.lineTo(xPos + 1, yPos + hoogte - 2);
        g.stroke();
        
        // paint button outline: black lines
        g.setStrokeStyle(CssColor.make(0,0,0));
        g.beginPath();
        g.moveTo(xPos, yPos + hoogte - 1);
        g.lineTo(xPos + breedte - 1, yPos + hoogte - 1);
        g.stroke();

        g.beginPath();
        g.moveTo(xPos + 1, yPos + hoogte - 2);
        g.lineTo(xPos + breedte - 2, yPos + hoogte - 2);
        g.stroke();
        
        g.beginPath();
        g.moveTo(xPos + breedte - 1, yPos);
        g.lineTo(xPos + breedte - 1, yPos + hoogte - 1);
        g.stroke();
        
        g.beginPath();
        g.moveTo(xPos + breedte - 2, yPos + 1);
        g.lineTo(xPos + breedte - 2, yPos + hoogte - 2);
        g.stroke();
                   
    } // paint

    /**
     * enable/disable the button
     * @param b true/false
     */
    public void setEnabled(boolean b)
    {   enabled = b;
    }
} // LWArrowButton

