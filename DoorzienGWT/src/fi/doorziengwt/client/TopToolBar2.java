package fi.doorziengwt.client;


import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.PushButton;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * class representing the top toolbar; note that the availability of 
 * the different tools is read from the launch data, see class DoorzienGWT  
 * @author huub
 */

public class TopToolBar2 extends LayoutPanel
{   
	/**
	 * owner of this TopToolBar2
	 */
    DoorzienGWT owner;

    /**
     * drawing/deleting lines 
     */
    ToggleButton drawLineButton, deleteLineButton;
    /**
     * lengthening, shortening existing lines
     */
    PushButton lengLinesButton, shortLinesButton;
    
    /**
     * drawing planes, drawing parallel planes, deleting planes
     */
    ToggleButton drawPlaneButton, parPlaneButton, deletePlaneButton;
    /**
     * show planes solid or transparant
     */
    ToggleButton planesFilledButton;
    
    /**
     * show the intersection of the 3d-object with a plane as a separate flat 2d-object
     */
    ToggleButton showCutButton;
    
    /**
     * cut the 3d-object along a plane
     */
    ToggleButton  cutButton;
    
    /**
     * layout constants
     */
    int buttonWidth = 41;
    int buttonHeight = 30;
	int leftOffset = 4;
	int topOffset = 4;
	int helpBarHeight = 20;

	/**
	 * width
	 */
	int breedte;
	
	/**
	 * closing the Tools-panel, see class DoorzienGWT
	 */
	PushButton closeButton;

	/**
	 * constructor, makes (toggle)buttons for available tools and
	 * add ClickHandlers 
	 * @param o owner of this TopToolBar2 
	 * @param b width 
	 */
    public TopToolBar2(DoorzienGWT o, int b)
    {   owner = o;
    	breedte = b;
        
    	ToggleClickHandler toggleClickHandler = new ToggleClickHandler();
    	PushClickHandler pushClickHandler = new PushClickHandler();
    	int currentX = leftOffset;
    	int currentY = topOffset;
    	
    	drawLineButton = new ToggleButton(owner.drawLineImage);
    	deleteLineButton = new ToggleButton(owner.deleteLineImage);
    	
    	if (owner.lijnTekenOptie)
    	{
			add(drawLineButton);
			setWidgetLeftWidth(drawLineButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(drawLineButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
			drawLineButton.addClickHandler(toggleClickHandler);
		
			currentX += buttonWidth + leftOffset;
			
			add(deleteLineButton);
			setWidgetLeftWidth(deleteLineButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(deleteLineButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			if (owner.numLines == 0)
				deleteLineButton.setEnabled(false);
			
			deleteLineButton.addClickHandler(toggleClickHandler);
		
			currentX += buttonWidth + leftOffset;
    	}
    	
    	lengLinesButton = new PushButton(owner.lengLinesImage);
    	shortLinesButton = new PushButton(owner.shortLinesImage);
    	
        if (owner.lijnTekenOptie && owner.lijnVerlengOptie)
        {
			add(lengLinesButton);
			setWidgetLeftWidth(lengLinesButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(lengLinesButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			if (owner.numLines == 0)
				lengLinesButton.setEnabled(false);
			
			lengLinesButton.addClickHandler(pushClickHandler);
		
			currentX += buttonWidth + leftOffset;
			
			add(shortLinesButton);
			setWidgetLeftWidth(shortLinesButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(shortLinesButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			shortLinesButton.setEnabled(false);
			
			shortLinesButton.addClickHandler(pushClickHandler);
		
			currentX += buttonWidth + leftOffset;
			
        }
    
        drawPlaneButton = new ToggleButton(owner.drawPlaneImage);
        parPlaneButton = new ToggleButton(owner.parPlaneImage);
        deletePlaneButton = new ToggleButton(owner.deletePlaneImage);
        planesFilledButton = new ToggleButton(owner.planesFilledImage, owner.planesEmptyImage);
        
        if (owner.vlakTekenOptie)
        {	
			add(drawPlaneButton);
			setWidgetLeftWidth(drawPlaneButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(drawPlaneButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			drawPlaneButton.addClickHandler(toggleClickHandler);
	
			currentX += buttonWidth + leftOffset;
  	
        }
        
        if (owner.vlakTekenOptie && owner.evenwijdigVlakOptie)
        {  	
			add(parPlaneButton);
			setWidgetLeftWidth(parPlaneButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(parPlaneButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			parPlaneButton.addClickHandler(toggleClickHandler);
	
			currentX += buttonWidth + leftOffset;

        }
        
        if (owner.vlakTekenOptie)
        {   
			add(deletePlaneButton);
			setWidgetLeftWidth(deletePlaneButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(deletePlaneButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			if (owner.numPlanes == 0)
				deletePlaneButton.setEnabled(false);
			
			deletePlaneButton.addClickHandler(toggleClickHandler);
		
			currentX += buttonWidth + leftOffset;
        	
			add(planesFilledButton);
			setWidgetLeftWidth(planesFilledButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(planesFilledButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			if (owner.numPlanes == 0)
				planesFilledButton.setEnabled(false);
			
			planesFilledButton.addClickHandler(toggleClickHandler);
		
			currentX += buttonWidth + leftOffset;
			
        }

        showCutButton = new ToggleButton(owner.showCutImage, owner.hideCutImage);
        
        if (owner.vlakTekenOptie && owner.toonDoorsnedeOptie)
        {	
        	
			add(showCutButton);
			setWidgetLeftWidth(showCutButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(showCutButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			if (owner.numPlanes == 0)
				showCutButton.setEnabled(false);
		
			showCutButton.addClickHandler(toggleClickHandler);
	
			currentX += buttonWidth + leftOffset;

        }
        
        cutButton = new ToggleButton(owner.cutImage, owner.glueImage);
        
        if (owner.vlakTekenOptie && owner.splitsFiguurOptie)
        {	
			add(cutButton);
			setWidgetLeftWidth(cutButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(cutButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			if (owner.numPlanes == 0)
				cutButton.setEnabled(false);
	
			cutButton.addClickHandler(toggleClickHandler);

			currentX += buttonWidth + leftOffset;
   
        }
       	currentX = leftOffset;
       	currentY = topOffset + buttonHeight + 2;
       	
       	owner.helpBar = new Label("help bar");
       	owner.helpBar.addStyleName(DoorzienGWT.doorzienGWTCss.helpbar());
       	add(owner.helpBar);
       	setWidgetLeftWidth(owner.helpBar, currentX, Style.Unit.PX, breedte - 2 * leftOffset, Style.Unit.PX);
       	setWidgetTopHeight(owner.helpBar, currentY, Style.Unit.PX, helpBarHeight, Style.Unit.PX);
        
        closeButton = new PushButton(DoorzienGWT.rb.sluitKnopLabel());
        closeButton.addStyleName(DoorzienGWT.doorzienGWTCss.pushbutton());
		add(closeButton);
		setWidgetLeftWidth(closeButton, breedte - buttonWidth - leftOffset, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		setWidgetTopHeight(closeButton, topOffset, Style.Unit.PX, 22, Style.Unit.PX);

		closeButton.addStyleName(DoorzienGWT.doorzienGWTCss.pushbutton());
		closeButton.addClickHandler(pushClickHandler);

        	
    }
    
   	public void onBrowserEvent(Event e)
	{
		e.preventDefault();
   		e.stopPropagation();
	}
    
   	/**
   	 * reset buttons to a situation with no lines and no planes
   	 */
    public void resetDefaults()
    {   
        drawLineButton.setEnabled(true);
        deleteLineButton.setEnabled(false);
        lengLinesButton.setEnabled(false);
        shortLinesButton.setEnabled(false);

        drawPlaneButton.setEnabled(true);
        parPlaneButton.setEnabled(false);
        deletePlaneButton.setEnabled(false);        
        planesFilledButton.setEnabled(false);        
        showCutButton.setEnabled(false);
        cutButton.setEnabled(false);                                       
    }

    /**
     * after drawing the first line, enable the other line buttons,
     * after deleting the last line, disable the other line buttons, 
     * @param b true/false
     */
    public void activateLineButtons(boolean b)
    {   // just in case
        drawLineButton.setEnabled(true);
        deleteLineButton.setEnabled(b);
        lengLinesButton.setEnabled(b);  
        if (DrawConstants.llFactor < Vector3D.NZero)
            shortLinesButton.setEnabled(false);
        else
            shortLinesButton.setEnabled(true);

        
    }
    /**
     * disable all line-related buttons
     */
    public void disableLineButtons()
    {   // just in case
        drawLineButton.setEnabled(false);
        deleteLineButton.setEnabled(false);
        lengLinesButton.setEnabled(false);                                                                                         
        shortLinesButton.setEnabled(false);
    }
    
    /**
     * after drawing the first plane, enable the other plane buttons,
     * after deleting the last plane, disable the other plane buttons, 
     * @param b true/false
     */
    public void activatePlaneButtons(boolean b)
    {   drawPlaneButton.setEnabled(true);
        parPlaneButton.setEnabled(b);
        deletePlaneButton.setEnabled(b);        
        planesFilledButton.setEnabled(b);
         if (owner.drawingPanel.planesFilled && b)
         {  planesFilledButton.setDown(true);
         }
         else
        	 planesFilledButton.setDown(false);

        showCutButton.setEnabled(b);
      
        if (owner.drawingPanel.showCut)
        {   showCutButton.setDown(true);
        }
        else
        {
        	showCutButton.setDown(false);
        }
           
        cutButton.setEnabled(b);                                       
    }

    /**
     * disable all plane-related buttons 
     */
    public void disablePlaneButtons()
    {   drawPlaneButton.setEnabled(false);
        parPlaneButton.setEnabled(false);
        deletePlaneButton.setEnabled(false);        
        planesFilledButton.setEnabled(false);
        showCutButton.setEnabled(false);
        cutButton.setEnabled(false);                                       
    }

    /**
     * disable all plane-related buttons, except the
     * cutButton  
     */
    public void disablePlaneButtons2()
    {   drawPlaneButton.setEnabled(false);
        parPlaneButton.setEnabled(false);
        deletePlaneButton.setEnabled(false);        
        planesFilledButton.setEnabled(false);
        showCutButton.setEnabled(false);
                                     
    }

    /**
     * inner class for handling Events on ToggleButtons;
     * use ClickHandlers, not MouseDown or TouchStart Handlers
     * @author huub
    */
    class ToggleClickHandler implements ClickHandler
    {
    	public void onClick(ClickEvent e)
		{
			// no scrolling in the DWOPlayer
			e.stopPropagation();
			
			if (e.getSource() == drawLineButton)
			{	if (drawLineButton.isDown())
				{	owner.drawingPanel.drawLine(0, true);
				}
				else
				{	owner.drawingPanel.drawLine(0, false);
				}
			}
			else if (e.getSource() == deleteLineButton)
			{	if (deleteLineButton.isDown())
				{	owner.drawingPanel.deleteLine(0, true);
				}
				else
				{	owner.drawingPanel.deleteLine(0, false);
				}
			}
			else if (e.getSource() == drawPlaneButton)
			{	if (drawPlaneButton.isDown())
				{	owner.drawingPanel.drawPlane(0, true);
				}
				else
				{	owner.drawingPanel.drawPlane(0, false);
				}
			}
			else if (e.getSource() == parPlaneButton)
			{	if (parPlaneButton.isDown())
				{	owner.drawingPanel.drawParPlane(0, true);
				}
				else
				{	owner.drawingPanel.drawParPlane(0, false);
				}
			}
			else if (e.getSource() == deletePlaneButton)
			{	if (deletePlaneButton.isDown())
				{	owner.drawingPanel.deletePlane(0, true);
				}
				else
				{	owner.drawingPanel.deletePlane(0, false);
				}
			}
			else if (e.getSource() == planesFilledButton)
			{	if (planesFilledButton.isDown())
				{	owner.drawingPanel.fillPlanes(true);
				}
				else
				{	owner.drawingPanel.fillPlanes(false);
				}
			}
			else if (e.getSource() == showCutButton)
			{
				if (showCutButton.isDown())
				{	owner.drawingPanel.showCut(0, true);
				}
				else
				{	owner.drawingPanel.showCut(0, false);
				}
			}
			else if (e.getSource() == cutButton)
			{
				if (cutButton.isDown())
				{	owner.drawingPanel.cutObject(0, true);
				}
				else
				{	owner.drawingPanel.cutObject(0, false);
				}
			}
			
		}	
    }
    /**
     * inner class for handling Events on PushButtons;
     * use ClickHandlers, not MouseDown or TouchStart Handlers
     * @author huub
    */
    class PushClickHandler implements ClickHandler
    {
    	public void onClick(ClickEvent e)
		{   	
    		//e.preventDefault();
    		e.stopPropagation();
    	
    		if (e.getSource() == lengLinesButton)
    		{
    			owner.drawingPanel.lengthenLines();
                shortLinesButton.setEnabled(true);
                if (DrawConstants.llFactor >= (DrawConstants.MAXLLFACTOR - DrawConstants.LLSTEP / 10))
                    lengLinesButton.setEnabled(false);
    		
    			
    		}
    		else if (e.getSource() == shortLinesButton)
    		{
    			owner.drawingPanel.shortenLines();
                lengLinesButton.setEnabled(true);
                shortLinesButton.setEnabled(false);
    			
    		}
    		else if (e.getSource() == closeButton)
    		{
    			owner.figureToViewer();
    		}
    		
    		
		}
    }

    
    
} // class TopToolBar2

