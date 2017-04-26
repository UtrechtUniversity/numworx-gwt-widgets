package fi.algebrapijlengwt.client;

import java.util.ArrayList;
import java.util.List;

/**
 * super class voor alle elementen van een pijlenketting; <br>
 * de klasse simuleert een Component en implementeert dragging, 
 * double clicks en long clicks  
 */
public class SchuifComponent 
{	
	/**
	 * simulatie Component
	 */
	int xPos, yPos, breedte, hoogte;
	/**
	 * t.b.v. dragging, 
	 */
	int startx, starty;
	/**
	 * t.b.v. dragging, moet als attribuut beschikbaar zijn 
	 */
	int dx, dy;
	
	/**
	 * tijdstip laatste MouseDown/TouchStart Event<br>
	 * voor gebruik in subclasses
	 */
    protected long taptime;
    /**
     * tijdstippen alle MouseDown/TouchStart Events<br>
     * voor gebruik in subclasses
     */
    protected List<Long> doubletap = new ArrayList<Long>();
    /**
     * dragging of niet?
     */
    boolean dragging = false;
	
	public SchuifComponent()
	{	
	}
	
	public SchuifComponent(int x, int y, int b, int h)
	{	
		xPos = x;
		yPos = y;
		breedte = b;
		hoogte = h;
	}

	/**
	 * check of het laatste MouseDown/TouchStart Event was meer dan 300 milliseconden
	 * geleden
	 * @return true/false
	 */
    protected boolean isLongClick() 
    {
    	return System.currentTimeMillis() - taptime > 300;
	}

    /**
     * check of de tijd tussen de laatste twee MouseDown/TouchStart Events was minder dan 700 milliseconden  
     * @return true/false
     */
	protected boolean isDoubleClick() 
	{
	    return doubletap.size() >= 2 && doubletap.get(1) - doubletap.get(0) < 700;
	}
	
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		startx = eventX;
		starty = eventY;
	}
	
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		dx = eventX - startx;
		dy = eventY - starty;
		int x = xPos + dx;
		int y = yPos + dy;
		xPos = x;
		yPos = y;
		startx = eventX;
		starty = eventY;
		if ((dx != 0) || (dy != 0))
			dragging = true;
	}
}
