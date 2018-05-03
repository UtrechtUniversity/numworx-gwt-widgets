package fi.doorziengwt.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.PushButton;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * class representing the right toolbar; note that making a foldout 
 * (construction drawing) is an option available through the launch data
 * @author huub
 */

public class RightToolBar2 extends LayoutPanel
{   
	/**
	 * owner of this RightToolBar2
	 */
    DoorzienGWT owner;
    
    /**
     * toggle: show figure with transparent/filled facets
     */
    ToggleButton wireSolidButton;
    /**
     * toggle: show figure as is, or as a foldout
     */
    ToggleButton conDrawButton;
    /**
     * zoomin in, zooming out, undo, redo
     */
    PushButton zoomInButton, zoomOutButton, undoButton, redoButton;
    
    /**
     * layout constants
     */
    int buttonWidth = 41;
    int buttonHeight = 30;
	int leftOffset = 5;
	int topOffset = 5;

	/**
	 * constructor: create the buttons and add ClickHandlers
	 * @param o owner of this RightToolBar2 
	 */
    public RightToolBar2(DoorzienGWT o)
    {   owner = o;

    	ToggleClickHandler toggleClickHandler = new ToggleClickHandler();
    	PushClickHandler pushClickHandler = new PushClickHandler();
    	
    	// create and add components top to bottom
    	int currentX = leftOffset;
    	int currentY = topOffset;
    
    	wireSolidButton = new ToggleButton(owner.solidImage, owner.wireframeImage);
		add(wireSolidButton);
		setWidgetLeftWidth(wireSolidButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		setWidgetTopHeight(wireSolidButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
	
		wireSolidButton.addClickHandler(toggleClickHandler);
	
		currentY += buttonHeight + topOffset;
		
		zoomInButton = new PushButton(owner.zoomInImage);
		add(zoomInButton);
		setWidgetLeftWidth(zoomInButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		setWidgetTopHeight(zoomInButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
	
		zoomInButton.addClickHandler(pushClickHandler);
	
		currentY += buttonHeight + topOffset;
		
		zoomOutButton = new PushButton(owner.zoomOutImage);
		add(zoomOutButton);
		setWidgetLeftWidth(zoomOutButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		setWidgetTopHeight(zoomOutButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
	
		zoomOutButton.addClickHandler(pushClickHandler);
	
		currentY += buttonHeight + topOffset;
		
		conDrawButton = new ToggleButton(owner.conDrawImage, owner.figureImage);
		
        if (owner.bouwplaatOptie)
        {	
			add(conDrawButton);
			setWidgetLeftWidth(conDrawButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(conDrawButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
	
			conDrawButton.addClickHandler(toggleClickHandler);
	
			currentY += buttonHeight + topOffset;
        
        }
		undoButton = new PushButton(owner.undoImage);
		add(undoButton);
		setWidgetLeftWidth(undoButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		setWidgetTopHeight(undoButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
	
		undoButton.setEnabled(false);
		
		undoButton.addClickHandler(pushClickHandler);
	
		currentY += buttonHeight + topOffset;

		redoButton = new PushButton(owner.redoImage);
		add(redoButton);
		setWidgetLeftWidth(redoButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		setWidgetTopHeight(redoButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
	
		redoButton.setEnabled(false);
		
		redoButton.addClickHandler(pushClickHandler);
	
		currentY += buttonHeight + topOffset;

        
    }
    
   	public void onBrowserEvent(Event e)
	{
		e.preventDefault();
   		e.stopPropagation();
	}

   	/**
   	 * reset defaults for buttons: figure is not filled, zooming allowed,
   	 * figure is not a foldout, no undo and redo (since no history)   
   	 */
    public void resetDefaults()
    {
    	wireSolidButton.setDown(false);
        
        zoomInButton.setEnabled(true);
        zoomOutButton.setEnabled(true);

        conDrawButton.setDown(false);
        
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);

    }
    
    /**
     * inner class handling ClickEvents on ToggleButtons
     * @author huub
     */
    class ToggleClickHandler implements ClickHandler
    {
    	public void onClick(ClickEvent e)
		{
			//e.preventDefault();
			e.stopPropagation();
			
			if (e.getSource() == wireSolidButton)
			{	if (wireSolidButton.isDown())
				{	owner.drawingPanel.setFilled(true);
				}
				else
				{	owner.drawingPanel.setFilled(false);
				}
			}
			else if (e.getSource() == conDrawButton)
			{
				if (conDrawButton.isDown())
				{
					owner.drawingPanel.makeFoldOut(0, true);
				}
				else
				{
					owner.drawingPanel.makeFoldOut(0, false);
				}
			}
			
		}	
    }
    
    /**
     * inner class handling ClickEvents on PushButtons
     * @author huub
     */
    class PushClickHandler implements ClickHandler
    {
    	public void onClick(ClickEvent e)
		{   
    		//e.preventDefault();
    		e.stopPropagation();
    	
    		if (e.getSource() == zoomInButton)
    		{
    			owner.drawingPanel.zoomIn();
                zoomOutButton.setEnabled(true);
                // maximum zoomin reached
                if (owner.drawingPanel.zoom >= (owner.drawingPanel.MAXZOOM - owner.drawingPanel.ZOOMSTEP / 10))
                {    zoomInButton.setEnabled(false);
                }
    		}
    		else if (e.getSource() == zoomOutButton)
    		{
    			owner.drawingPanel.zoomOut();
                zoomInButton.setEnabled(true);
                // maximum zoomout reached
                if (owner.drawingPanel.zoom <= (owner.drawingPanel.MINZOOM + owner.drawingPanel.ZOOMSTEP / 10))
                {	zoomOutButton.setEnabled(false);    
                }
    		}
    		else if (e.getSource() == undoButton)
    		{
    			owner.drawingPanel.undo();
    		}
    		else if (e.getSource() == redoButton)
    		{
    			owner.drawingPanel.redo();
    		}

		}
    }
    

} // class RightToolBar2   

