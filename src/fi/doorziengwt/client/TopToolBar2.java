package fi.doorziengwt.client;


import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.PushButton;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;

import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;


public class TopToolBar2 extends LayoutPanel
{   // owner
    DoorzienGWT owner;
    // left to right
    ToggleButton drawLineButton, deleteLineButton;
    PushButton lengLinesButton, shortLinesButton;
    ToggleButton drawPlaneButton, parPlaneButton, deletePlaneButton, planesFilledButton, showCutButton, cutButton;
    
    // button width
    int buttonWidth = 41;
    int buttonHeight = 30;
	int leftOffset = 4;
	int topOffset = 4;
	int helpBarHeight = 20;
	
	int breedte;
	
	PushButton closeButton;

	boolean touchStart = false;
	
	// constructor
    public TopToolBar2(DoorzienGWT o, int b)
    {   owner = o;
    	breedte = b;
        
    	//ToggleMouseDownHandler toggleMouseDownHandler = new ToggleMouseDownHandler();
    	//PushMouseDownHandler pushMouseDownHandler = new PushMouseDownHandler();
    	
    	ToggleClickHandler toggleClickHandler = new ToggleClickHandler();
    	PushClickHandler pushClickHandler = new PushClickHandler();
    	
    	//ToggleTouchStartHandler toggleTouchStartHandler = new ToggleTouchStartHandler();
    	//PushTouchStartHandler pushTouchStartHandler = new PushTouchStartHandler();
    	
    	int currentX = leftOffset;
    	int currentY = topOffset;
    	
    	// prevent NPE
    	drawLineButton = new ToggleButton(owner.drawLineImage);
    	deleteLineButton = new ToggleButton(owner.deleteLineImage);
    	
    	if (owner.lijnTekenOptie)
    	{
    		//drawLineButton = new ToggleButton(owner.drawLineImage);
    		
    		//drawLineButton.addStyleName(owner.doorzienGWTCss.togglebutton());
			add(drawLineButton);
			setWidgetLeftWidth(drawLineButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(drawLineButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
		
			//twmi.addTouchStartHandler(drawLineButton, new ToggleTouchStartHandler());
			//drawLineButton.addTouchStartHandler(toggleTouchStartHandler);
			//drawLineButton.addMouseDownHandler(toggleMouseDownHandler);
			drawLineButton.addClickHandler(toggleClickHandler);
		
			currentX += buttonWidth + leftOffset;
			
    		//deleteLineButton = new ToggleButton(owner.deleteLineImage);
			
    		//deleteLineButton.addStyleName(owner.doorzienGWTCss.togglebutton());
			add(deleteLineButton);
			setWidgetLeftWidth(deleteLineButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(deleteLineButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			if (owner.numLines == 0)
				deleteLineButton.setEnabled(false);
			
			//twmi.addTouchStartHandler(deleteLineButton, new ToggleTouchStartHandler());
			//deleteLineButton.addTouchStartHandler(toggleTouchStartHandler);
			//deleteLineButton.addMouseDownHandler(toggleMouseDownHandler);
			deleteLineButton.addClickHandler(toggleClickHandler);
		
			currentX += buttonWidth + leftOffset;
			

    	}
    	
    	lengLinesButton = new PushButton(owner.lengLinesImage);
    	shortLinesButton = new PushButton(owner.shortLinesImage);
    	
        if (owner.lijnTekenOptie && owner.lijnVerlengOptie)
        {
    		//lengLinesButton = new PushButton(owner.lengLinesImage);
        	
    		//lengLinesButton.addStyleName(owner.doorzienGWTCss.togglebutton());
			add(lengLinesButton);
			setWidgetLeftWidth(lengLinesButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(lengLinesButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			if (owner.numLines == 0)
				lengLinesButton.setEnabled(false);
			
			//twmi.addTouchStartHandler(lengLinesButton, new ToggleTouchStartHandler());
			//lengLinesButton.addTouchStartHandler(pushTouchStartHandler);
			//lengLinesButton.addMouseDownHandler(pushMouseDownHandler);
			lengLinesButton.addClickHandler(pushClickHandler);
		
			currentX += buttonWidth + leftOffset;
			
			//shortLinesButton = new PushButton(owner.shortLinesImage);
			
    		//shortLinesButton.addStyleName(owner.doorzienGWTCss.togglebutton());
			add(shortLinesButton);
			setWidgetLeftWidth(shortLinesButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(shortLinesButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			//if (owner.numLines == 0)
				shortLinesButton.setEnabled(false);
			
			//twmi.addTouchStartHandler(shortLinesButton, new ToggleTouchStartHandler());
			//shortLinesButton.addTouchStartHandler(pushTouchStartHandler);
			//shortLinesButton.addMouseDownHandler(pushMouseDownHandler);
			shortLinesButton.addClickHandler(pushClickHandler);
		
			currentX += buttonWidth + leftOffset;
			
        	
        }
    
        drawPlaneButton = new ToggleButton(owner.drawPlaneImage);
        parPlaneButton = new ToggleButton(owner.parPlaneImage);
        deletePlaneButton = new ToggleButton(owner.deletePlaneImage);
        planesFilledButton = new ToggleButton(owner.planesFilledImage, owner.planesEmptyImage);
        
        if (owner.vlakTekenOptie)
        {	
        	//drawPlaneButton = new ToggleButton(owner.drawPlaneImage);
        	
			//drawPlaneButton.addStyleName(owner.doorzienGWTCss.togglebutton());
			add(drawPlaneButton);
			setWidgetLeftWidth(drawPlaneButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(drawPlaneButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			//twmi.addTouchStartHandler(drawPlaneButton, new ToggleTouchStartHandler());
			//drawPlaneButton.addTouchStartHandler(toggleTouchStartHandler);
			//drawPlaneButton.addMouseDownHandler(toggleMouseDownHandler);
			drawPlaneButton.addClickHandler(toggleClickHandler);
	
			currentX += buttonWidth + leftOffset;
  	
        }
        
        if (owner.vlakTekenOptie && owner.evenwijdigVlakOptie)
        {  	
        	//parPlaneButton = new ToggleButton(owner.parPlaneImage);
        	
			//parPlaneButton.addStyleName(owner.doorzienGWTCss.togglebutton());
			add(parPlaneButton);
			setWidgetLeftWidth(parPlaneButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(parPlaneButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			//twmi.addTouchStartHandler(parPlaneButton, new ToggleTouchStartHandler());
			//parPlaneButton.addTouchStartHandler(toggleTouchStartHandler);
			//parPlaneButton.addMouseDownHandler(toggleMouseDownHandler);
			parPlaneButton.addClickHandler(toggleClickHandler);
	
			currentX += buttonWidth + leftOffset;

        }
        
        if (owner.vlakTekenOptie)
        {   
        	//deletePlaneButton = new ToggleButton(owner.deletePlaneImage);
        	
    		//deletePlaneButton.addStyleName(owner.doorzienGWTCss.togglebutton());
			add(deletePlaneButton);
			setWidgetLeftWidth(deletePlaneButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(deletePlaneButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			if (owner.numPlanes == 0)
				deletePlaneButton.setEnabled(false);
			
			//twmi.addTouchStartHandler(deletePlaneButton, new ToggleTouchStartHandler());
			//deletePlaneButton.addTouchStartHandler(toggleTouchStartHandler);
			//deletePlaneButton.addMouseDownHandler(toggleMouseDownHandler);
			deletePlaneButton.addClickHandler(toggleClickHandler);
		
			currentX += buttonWidth + leftOffset;
        	
			//planesFilledButton = new ToggleButton(owner.planesFilledImage, owner.planesEmptyImage);
			
    		//planesFilledButton.addStyleName(owner.doorzienGWTCss.togglebutton());
			add(planesFilledButton);
			setWidgetLeftWidth(planesFilledButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(planesFilledButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			if (owner.numPlanes == 0)
				planesFilledButton.setEnabled(false);
			
			//twmi.addTouchStartHandler(planesFilledButton, new ToggleTouchStartHandler());
			//planesFilledButton.addTouchStartHandler(toggleTouchStartHandler);
			//planesFilledButton.addMouseDownHandler(toggleMouseDownHandler);
			planesFilledButton.addClickHandler(toggleClickHandler);
		
			currentX += buttonWidth + leftOffset;
			
        }

        showCutButton = new ToggleButton(owner.showCutImage, owner.hideCutImage);
        
        if (owner.vlakTekenOptie && owner.toonDoorsnedeOptie)
        {	
        	
        	
        	//showCutButton = new ToggleButton(owner.showCutImage, owner.hideCutImage);
        	
			//showCutButton.addStyleName(owner.doorzienGWTCss.togglebutton());
			add(showCutButton);
			setWidgetLeftWidth(showCutButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(showCutButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			if (owner.numPlanes == 0)
				showCutButton.setEnabled(false);
		
			//twmi.addTouchStartHandler(showCutButton, new ToggleTouchStartHandler());
			//showCutButton.addTouchStartHandler(new ToggleTouchStartHandler());
			//showCutButton.addMouseDownHandler(toggleMouseHandler);
			showCutButton.addClickHandler(toggleClickHandler);
	
			currentX += buttonWidth + leftOffset;

        }
        
        cutButton = new ToggleButton(owner.cutImage, owner.glueImage);
        
        if (owner.vlakTekenOptie && owner.splitsFiguurOptie)
        {	
        	//cutButton = new ToggleButton(owner.cutImage, owner.glueImage);
        	
			//cutButton.addStyleName(owner.doorzienGWTCss.togglebutton());
			add(cutButton);
			setWidgetLeftWidth(cutButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(cutButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);

			if (owner.numPlanes == 0)
				cutButton.setEnabled(false);
	
			//twmi.addTouchStartHandler(cutButton, new ToggleTouchStartHandler());
			//cutButton.addTouchStartHandler(toggleTouchStartHandler);
			//cutButton.addMouseDownHandler(toggleMouseDownHandler);
			cutButton.addClickHandler(toggleClickHandler);

			currentX += buttonWidth + leftOffset;
   
/*			
			ListBox dropBox = new ListBox();
	        dropBox.addItem("verdeel in");
	        dropBox.addItem("2 delen");
	        dropBox.addItem("3 delen");
	        dropBox.addItem("4 delen");
	        dropBox.addItem("5 delen");
	        dropBox.addItem("6 delen");
	        
	        add(dropBox);
	    	setWidgetLeftWidth(dropBox, currentX, Style.Unit.PX, 90, Style.Unit.PX);
			setWidgetTopHeight(dropBox, currentY, Style.Unit.PX, 20, Style.Unit.PX);
*/			
        }
// dit maar altijd?        
        //if (owner.helpBarOptie)
        //{	
        	currentX = leftOffset;
        	currentY = topOffset + buttonHeight + 2;
        	owner.helpBar = new Label("help bar");
        	owner.helpBar.addStyleName(DoorzienGWT.doorzienGWTCss.helpbar());
        	add(owner.helpBar);
        	setWidgetLeftWidth(owner.helpBar, currentX, Style.Unit.PX, breedte - 2 * leftOffset, Style.Unit.PX);
        	setWidgetTopHeight(owner.helpBar, currentY, Style.Unit.PX, helpBarHeight, Style.Unit.PX);
        //}
        
        closeButton = new PushButton(DoorzienGWT.rb.sluitKnopLabel());
        closeButton.addStyleName(DoorzienGWT.doorzienGWTCss.pushbutton());
		add(closeButton);
		setWidgetLeftWidth(closeButton, breedte - buttonWidth - leftOffset, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		setWidgetTopHeight(closeButton, topOffset, Style.Unit.PX, 22, Style.Unit.PX);

		closeButton.addStyleName(DoorzienGWT.doorzienGWTCss.pushbutton());
		//twmi.addTouchStartHandler(lengLinesButton, new ToggleTouchStartHandler());
		//closeButton.addTouchStartHandler(pushTouchStartHandler);
		//closeButton.addMouseDownHandler(pushMouseDownHandler);
		closeButton.addClickHandler(pushClickHandler);

        	
    }
    
   	public void onBrowserEvent(Event e)
	{
		e.preventDefault();
   		e.stopPropagation();
	}
    
    // situation: no lines, no planes
    public void resetDefaults()
    {   // just in case
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
    
    public void unPress()
    {
/*
GWT     	
    	drawLineButton.setPressed(false);
        deleteLineButton.setPressed(false);

        drawPlaneButton.setPressed(false);
        parPlaneButton.setPressed(false);
        deletePlaneButton.setPressed(false);        
        //planesFilledButton.setOn(false);        
        showCutButton.setPressed(false);
        cutButton.setPressed(false);                                       
*/
    }
    // true: at least one line
    // false: no lines
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
    
    public void disableLineButtons()
    {   // just in case
        drawLineButton.setEnabled(false);
        deleteLineButton.setEnabled(false);
        lengLinesButton.setEnabled(false);                                                                                         
        shortLinesButton.setEnabled(false);
    }
    
    // true: at least one plane
    // false: no planes
    public void activatePlaneButtons(boolean b)
    {   drawPlaneButton.setEnabled(true);
        parPlaneButton.setEnabled(b);
        deletePlaneButton.setEnabled(b);        
        planesFilledButton.setEnabled(b);

          
         if (owner.drawingPanel.planesFilled && b)
         {  //planesFilledButton.setImage(owner.planesEmptyImage);
        	 planesFilledButton.setDown(true);
         }
         else
        	 planesFilledButton.setDown(false);

        showCutButton.setEnabled(b);
      
        if (owner.drawingPanel.showCut)
        {   //showCutButton.setImage(owner.hideCutImage);
        	showCutButton.setDown(true);
        }
        else
        {
        	showCutButton.setDown(false);
        }
           
        cutButton.setEnabled(b);                                       
    }

    public void disablePlaneButtons()
    {   drawPlaneButton.setEnabled(false);
        parPlaneButton.setEnabled(false);
        deletePlaneButton.setEnabled(false);        
        planesFilledButton.setEnabled(false);
        showCutButton.setEnabled(false);
        cutButton.setEnabled(false);                                       
    }

    public void disablePlaneButtons2()
    {   drawPlaneButton.setEnabled(false);
        parPlaneButton.setEnabled(false);
        deletePlaneButton.setEnabled(false);        
        planesFilledButton.setEnabled(false);

        showCutButton.setEnabled(false);
        //cutButton.setEnabled(false);                                       
    }
    
    //class ToggleMouseDownHandler implements MouseDownHandler
    class ToggleClickHandler implements ClickHandler
    {
    	//public void onMouseDown(MouseDownEvent e)
    	public void onClick(ClickEvent e)
		{
    		if (touchStart)
    			return;
			
			// deze LIJKT niet nodig (mag wel)
			//e.preventDefault();
			
			// deze zorgt dat je niet scollt in de DWOPlayer
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
//System.out.println("dlb is down = " + drawLineButton.isDown());				
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
    
    //class PushMouseDownHandler implements MouseDownHandler
    class PushClickHandler implements ClickHandler
    {
    	//public void onMouseDown(MouseDownEvent e)
    	public void onClick(ClickEvent e)
		{   	
    		if (touchStart)
    			return;
    		
    		//e.preventDefault();
    		e.stopPropagation();
    	
    		if (e.getSource() == lengLinesButton)
    		{
    			owner.drawingPanel.lengthenLines();
                shortLinesButton.setEnabled(true);
                if (DrawConstants.llFactor >= (owner.drawingPanel.MAXLLFACTOR - owner.drawingPanel.LLSTEP / 10))
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
    			// figuur uit popup
    			owner.figureToViewer();
    		}
    		
    		
		}
    }
    
    class ToggleTouchStartHandler implements TouchStartHandler
    {
    	public void onTouchStart(TouchStartEvent e)
		{
    		touchStart = true;
			
			// deze LIJKT niet nodig (mag wel)
			//e.preventDefault();
			
			// deze zorgt dat je niet scollt in de DWOPlayer
			e.stopPropagation();

			if (e.getSource() == drawLineButton)
			{	if (!drawLineButton.isDown())
				{	owner.drawingPanel.drawLine(0, true);
				}
				else
				{	owner.drawingPanel.drawLine(0, false);
				}
			}
			else if (e.getSource() == deleteLineButton)
			{	if (!deleteLineButton.isDown())
				{	owner.drawingPanel.deleteLine(0, true);
				}
				else
				{	owner.drawingPanel.deleteLine(0, false);
				}
			}
			else if (e.getSource() == drawPlaneButton)
			{	if (!drawPlaneButton.isDown())
				{	owner.drawingPanel.drawPlane(0, true);
				}
				else
				{	owner.drawingPanel.drawPlane(0, false);
				}
			}
			else if (e.getSource() == parPlaneButton)
			{	if (!parPlaneButton.isDown())
				{	owner.drawingPanel.drawParPlane(0, true);
				}
				else
				{	owner.drawingPanel.drawParPlane(0, false);
				}
			}
			else if (e.getSource() == deletePlaneButton)
			{	if (!deletePlaneButton.isDown())
				{	owner.drawingPanel.deletePlane(0, true);
				}
				else
				{	owner.drawingPanel.deletePlane(0, false);
				}
			}
			else if (e.getSource() == planesFilledButton)
			{	if (!planesFilledButton.isDown())
				{	owner.drawingPanel.fillPlanes(true);
				}
				else
				{	owner.drawingPanel.fillPlanes(false);
				}
			}


			else if (e.getSource() == cutButton)
			{
				if (!cutButton.isDown())
				{	owner.drawingPanel.cutObject(0, true);
				}
				else
				{	owner.drawingPanel.cutObject(0, false);
				}
			}
			
			
		}	
    }
    
    class PushTouchStartHandler implements TouchStartHandler
    {
    	public void onTouchStart(TouchStartEvent e)
		{   	
    		touchStart = true;
    		
    		//e.preventDefault();
    		e.stopPropagation();
    	
    		if (e.getSource() == lengLinesButton)
    		{
    			owner.drawingPanel.lengthenLines();
                shortLinesButton.setEnabled(true);
                if (DrawConstants.llFactor >= (owner.drawingPanel.MAXLLFACTOR - owner.drawingPanel.LLSTEP / 10))
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
    			// figuur uit popup
    			owner.figureToViewer();
    		}

		}
    }
    
    
/*    
    class DrawLineML extends MouseAdapter
    {   // remember last message
        String lastHelpMessage;
        public void mouseEntered(MouseEvent e)
        {   if (drawLineButton.enabled)
            {   
                lastHelpMessage = owner.helpBar.text;             
                if (DoorzienDWO.version == DoorzienDWO.FI)
                {
                    if (owner.drawingPanel.mouseMode != 
                        DrawingPanel.DRAWLINE)
                    {    
                        //owner.helpBar.setText(owner.tt("drawLinesText"));
                        owner.helpBar.setMessage(owner.tt("drawLinesText"),
                                                 drawLineButton.getLocation().x + buttonWidth / 2);
                        
                    }    
                }                    
                else if (DoorzienDWO.version == DoorzienDWO.EPN)
                {
                    if (owner.drawingPanel.mouseMode != 
                        DrawingPanel.DRAWLINE)
                    {    
                        //owner.helpBar.setText(owner.tt("drawLineText"));
                        owner.helpBar.setMessage(owner.tt("drawLineText"),
                                                 drawLineButton.getLocation().x + buttonWidth / 2);
                    }    
                }                    
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (drawLineButton.enabled)
            {   owner.helpBar.setMessage(null, 0);
                owner.helpBar.setText(lastHelpMessage);
            }    
        }    
    }    
*/
  
/*    
    class DeleteLineML extends MouseAdapter
    {   // remember last message
        String lastHelpMessage;
        public void mouseEntered(MouseEvent e)
        {   if (deleteLineButton.enabled)
            {   lastHelpMessage = owner.helpBar.text;
            
                if (owner.drawingPanel.mouseMode != 
                    DrawingPanel.DELETELINE)
                owner.helpBar.setMessage(owner.tt("deleteLineText"),
                                         deleteLineButton.getLocation().x + buttonWidth / 2);
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (deleteLineButton.enabled)
            {   owner.helpBar.setMessage(null, 0);
                owner.helpBar.setText(lastHelpMessage);
            }    
        }    
    }    
*/
    
/*    
    class LengLinesML extends MouseAdapter
    {   String lastHelpMessage;
        public void mouseEntered(MouseEvent e)
        {   if (lengLinesButton.enabled)
            {   lastHelpMessage = owner.helpBar.text;                
                //owner.helpBar.setText(owner.tt("lengLinesText"));
                owner.helpBar.setMessage(owner.tt("lengLinesText"),
                                         lengLinesButton.getLocation().x + buttonWidth / 2);
                
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (lengLinesButton.enabled)
            {   owner.helpBar.setMessage(null, 0); 
                owner.helpBar.setText(lastHelpMessage);
            }
        }    
    }
*/    
  
/*    
    class ShortLinesML extends MouseAdapter
    {   String lastHelpMessage;
        public void mouseEntered(MouseEvent e)
        {   if (shortLinesButton.enabled)
            {   lastHelpMessage = owner.helpBar.text;                
                //owner.helpBar.setText(owner.tt("shortLinesText"));
                owner.helpBar.setMessage(owner.tt("shortLinesText"),
                                         shortLinesButton.getLocation().x + buttonWidth / 2);
                
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (shortLinesButton.enabled)
            {   owner.helpBar.setMessage(null, 0);
                owner.helpBar.setText(lastHelpMessage);
            }
        }    
    }    
*/    
    
/*    
    class DrawPlaneML extends MouseAdapter
    {   // remember last message
        String lastHelpMessage;
        public void mouseEntered(MouseEvent e)
        {   if (drawPlaneButton.enabled)
            {   
                lastHelpMessage = owner.helpBar.text;     
                if (DoorzienDWO.version == DoorzienDWO.FI)
                {
                    if (owner.drawingPanel.mouseMode != 
                        DrawingPanel.DRAWPLANE)
                    {    
                        //owner.helpBar.setText(owner.tt("drawPlanesText"));
                        owner.helpBar.setMessage(owner.tt("drawPlanesText"),
                                                 drawPlaneButton.getLocation().x + buttonWidth / 2);
                        
                    }    
                }    
                else if (DoorzienDWO.version == DoorzienDWO.EPN)
                {
                    if (owner.drawingPanel.mouseMode != 
                        DrawingPanel.DRAWPLANE)
                    {    
                        //owner.helpBar.setText(owner.tt("drawPlaneText"));
                        owner.helpBar.setMessage(owner.tt("drawPlaneText"),
                                                 drawPlaneButton.getLocation().x + buttonWidth / 2);
                    }    
                }    

            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (drawPlaneButton.enabled)
            {   owner.helpBar.setMessage(null, 0);
                owner.helpBar.setText(lastHelpMessage);
            }    
        }    
    }    
*/
    
/*    
    class ParPlaneML extends MouseAdapter
    {   // remember last message
        String lastHelpMessage;
        public void mouseEntered(MouseEvent e)
        {   if (parPlaneButton.enabled)
            {   lastHelpMessage = owner.helpBar.text;
                //owner.helpBar.setText(owner.tt("parPlaneText"));
                if (owner.drawingPanel.mouseMode != 
                    owner.drawingPanel.DRAWPARPLANE)
                    owner.helpBar.setMessage(owner.tt("parPlaneText"),
                                             parPlaneButton.getLocation().x + buttonWidth / 2);
                
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (parPlaneButton.enabled)
            {   owner.helpBar.setMessage(null, 0);
                owner.helpBar.setText(lastHelpMessage);
            }    
        }    
    }    
*/
    
/*    
    class DeletePlaneML extends MouseAdapter
    {   // remember last message
        String lastHelpMessage;
        public void mouseEntered(MouseEvent e)
        {   if (deletePlaneButton.enabled)
            {   lastHelpMessage = owner.helpBar.text;
                if (owner.drawingPanel.mouseMode != 
                    DrawingPanel.DELETEPLANE)
                    owner.helpBar.setMessage(owner.tt("deletePlaneText"),
                                             deletePlaneButton.getLocation().x + buttonWidth / 2);
                
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (deletePlaneButton.enabled)
            {   owner.helpBar.setMessage(null, 0);
                owner.helpBar.setText(lastHelpMessage);
            }    
        }    
    }    
*/

/*
    class PlanesFilledML extends MouseAdapter
    {   String lastHelpMessage;
        public void mouseEntered(MouseEvent e)
        {   if (planesFilledButton.enabled)
            {   lastHelpMessage = owner.helpBar.text;
                if (owner.drawingPanel.planesFilled)
                {   //owner.helpBar.setText(owner.tt("planesEmptyText"));
                    owner.helpBar.setMessage(owner.tt("planesEmptyText"),
                                             planesFilledButton.getLocation().x + buttonWidth / 2);
                
                }
                else
                {   //owner.helpBar.setText(owner.tt("planesFilledText"));
                    owner.helpBar.setMessage(owner.tt("planesFilledText"),
                                             planesFilledButton.getLocation().x + buttonWidth / 2);
                
                }
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (planesFilledButton.enabled)
            {   owner.helpBar.setMessage(null, 0);          
                owner.helpBar.setText(lastHelpMessage);
            }
        }    
    }    
*/

/*
    class ShowCutML extends MouseAdapter
    {   String lastHelpMessage;
        public void mousePressed(MouseEvent e)
        {   if (showCutButton.enabled)
            {   // cut visible
                if (owner.drawingPanel.showCut)
                {   owner.drawingPanel.showCut(0, false);
                    showCutButton.setImage(owner.showCutImage);
                    owner.helpBar.setMessage(null, 0);                                         
//                    owner.helpBar.setText(owner.tt("showCutText"));                    
                }
                else
                {   // still choosing the plane
                    if (showCutButton.pressed)
                    {   owner.drawingPanel.showCut(0, false);
                    }
                    else
                    {
                        owner.drawingPanel.showCut(0, true);
//                        lastHelpMessage = owner.helpBar.text;                                
                        //showCutButton.setImage(owner.hideCutImage);
    //                    owner.helpBar.setText(owner.tt("hideCutText"));                    
                        owner.helpBar.setMessage(null, 0);                     
                    }
                }    
                lastHelpMessage = owner.helpBar.text;                
            }
        }  
        public void mouseEntered(MouseEvent e)
        {   if (showCutButton.enabled)
            {   lastHelpMessage = owner.helpBar.text;
                // cut visible
                if (owner.drawingPanel.showCut)
                {   
                    //owner.helpBar.setText(owner.tt("hideCutText"));
                    owner.helpBar.setMessage(owner.tt("hideCutText"),
                                             showCutButton.getLocation().x + buttonWidth / 2);                    
                }
                else
                {   if (showCutButton.pressed)
                    {   // geen message, er wordt nog een vlak gekozen
                    }
                    else // knop is inert
                    {
                        //owner.helpBar.setText(owner.tt("showCutText"));
                        owner.helpBar.setMessage(owner.tt("showCutText"),
                                             showCutButton.getLocation().x + buttonWidth / 2);
                    }
                }
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (showCutButton.enabled)
            {   owner.helpBar.setMessage(null, 0);                     
                owner.helpBar.setText(lastHelpMessage);
            }
        }    
    }
*/    
  
/*    
    class CutML extends MouseAdapter
    {   String lastHelpMessage;
        public void mouseEntered(MouseEvent e)
        {   if (cutButton.enabled)
            {   lastHelpMessage = owner.helpBar.text;
                if (owner.drawingPanel.mouseMode != DrawingPanel.CUTOBJECT)
                {   //owner.helpBar.setText(owner.tt("cutFigureText"));
                    owner.helpBar.setMessage(owner.tt("cutFigureText"),
                                             cutButton.getLocation().x + buttonWidth / 2);
                
                }
                else
                {   if (owner.drawingPanel.planeChoosen == null)
                    {    //owner.helpBar.setText(owner.tt("cutFigureText"));                
                    
                    }
                    else
                    {
                        //owner.helpBar.setText(owner.tt("glueFigureText"));
                        owner.helpBar.setMessage(owner.tt("glueFigureText"),
                                                 cutButton.getLocation().x + buttonWidth / 2);
                        
                    }    
                }
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (cutButton.enabled)
            {   owner.helpBar.setMessage(null, 0);          
                owner.helpBar.setText(lastHelpMessage);
            }
        }    
    }    
*/
    
} // class TopToolBar2

