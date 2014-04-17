package fi.algebrapijlengwt.client;

import java.util.ArrayList;
import java.util.List;


public class SchuifComponent 
{	
	int startx, starty, dx, dy;
	int xPos, yPos, breedte, hoogte;
	
	protected boolean press;
    protected long taptime;
    protected List<Long> doubletap = new ArrayList<Long>();
    
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
	
    protected boolean isLongClick() 
    {
    	return System.currentTimeMillis() - taptime > 300;
	}

	protected boolean isDoubleClick() 
	{
	    return doubletap.size() >= 2 && doubletap.get(1) - doubletap.get(0) < 700;
		//return (doubletap.size() >= 2) && doubletap.get(doubletap.size() - 1) - doubletap.get(doubletap.size() - 2) < 700;
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
		
// GWT??
/*		
		if(schuifveld.isGesloten())
		{	int b = getSize().width;
			int h = getSize().height;
			int bp = schuifveld.getSize().width;
			int hp = schuifveld.getSize().height;
			if(x < 0)x = 0;
			if(x > bp-b)x = bp-b;
			if(y < 0)y = 0;
			if(y > hp-h)y = hp-h;
		}
*/
		
		//setLocation(x,y);
		
		xPos = x;
		yPos = y;
		startx = eventX;
		starty = eventY;
		
		if ((dx != 0) || (dy != 0))
			dragging = true;
		
	}
	

}
