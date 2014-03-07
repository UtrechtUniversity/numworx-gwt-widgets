package fi.doorziengwt.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.PushButton;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;

import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;


//toolbar on the right
public class RightToolBar2 extends LayoutPanel
{   // owner
    DoorzienGWT owner;
    
    // components here
    // top to bottom
    ToggleButton wireSolidButton, conDrawButton;
    PushButton zoomInButton, zoomOutButton, undoButton, redoButton;
    
    int buttonWidth = 41;
    int buttonHeight = 30;
	int leftOffset = 5;
	int topOffset = 5;

	boolean touchStart = false;
	
    // constructor
    public RightToolBar2(DoorzienGWT o)
    {   owner = o;

    	//ToggleMouseDownHandler toggleMouseDownHandler = new ToggleMouseDownHandler();
    	//PushMouseDownHandler pushMouseDownHandler = new PushMouseDownHandler();

    	ToggleClickHandler toggleClickHandler = new ToggleClickHandler();
    	PushClickHandler pushClickHandler = new PushClickHandler();
    	
    	//ToggleTouchStartHandler toggleTouchStartHandler = new ToggleTouchStartHandler();
    	//PushTouchStartHandler pushTouchStartHandler = new PushTouchStartHandler();
    	
    	// create and add components top to bottom
    	int currentX = leftOffset;
    	int currentY = topOffset;
    
    	wireSolidButton = new ToggleButton(owner.solidImage, owner.wireframeImage);
		add(wireSolidButton);
		setWidgetLeftWidth(wireSolidButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		setWidgetTopHeight(wireSolidButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
	
		//twmi.addTouchStartHandler(wireSolidButton, new ToggleTouchStartHandler());
		//wireSolidButton.addTouchStartHandler(toggleTouchStartHandler);
		//wireSolidButton.addMouseDownHandler(toggleMouseDownHandler);
		wireSolidButton.addClickHandler(toggleClickHandler);
	
		currentY += buttonHeight + topOffset;
		
		zoomInButton = new PushButton(owner.zoomInImage);
		add(zoomInButton);
		setWidgetLeftWidth(zoomInButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		setWidgetTopHeight(zoomInButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
	
		//twmi.addTouchStartHandler(zoomInButton, new ToggleTouchStartHandler());
		//zoomInButton.addTouchStartHandler(pushTouchStartHandler);
		//zoomInButton.addMouseDownHandler(pushMouseDownHandler);
		zoomInButton.addClickHandler(pushClickHandler);
	
		currentY += buttonHeight + topOffset;
		
		zoomOutButton = new PushButton(owner.zoomOutImage);
		add(zoomOutButton);
		setWidgetLeftWidth(zoomOutButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		setWidgetTopHeight(zoomOutButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
	
		//twmi.addTouchStartHandler(zoomOutButton, new ToggleTouchStartHandler());
		//zoomOutButton.addTouchStartHandler(pushTouchStartHandler);
		//zoomOutButton.addMouseDownHandler(pushMouseDownHandler);
		zoomOutButton.addClickHandler(pushClickHandler);
	
		currentY += buttonHeight + topOffset;
		
		conDrawButton = new ToggleButton(owner.conDrawImage, owner.figureImage);
		
        if (owner.bouwplaatOptie)
        {	
        	//conDrawButton = new ToggleButton(owner.conDrawImage, owner.figureImage);
			add(conDrawButton);
			setWidgetLeftWidth(conDrawButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
			setWidgetTopHeight(conDrawButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
	
			//twmi.addTouchStartHandler(conDrawButton, new ToggleTouchStartHandler());
			//conDrawButton.addTouchStartHandler(toggleTouchStartHandler);
			//conDrawButton.addMouseDownHandler(toggleMouseDownHandler);
			conDrawButton.addClickHandler(toggleClickHandler);
	
			currentY += buttonHeight + topOffset;
        
        }
		undoButton = new PushButton(owner.undoImage);
		add(undoButton);
		setWidgetLeftWidth(undoButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		setWidgetTopHeight(undoButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
	
		undoButton.setEnabled(false);
		
		//twmi.addTouchStartHandler(undoButton, new ToggleTouchStartHandler());
		//undoButton.addTouchStartHandler(pushTouchStartHandler);
		//undoButton.addMouseDownHandler(pushMouseDownHandler);
		undoButton.addClickHandler(pushClickHandler);
	
		currentY += buttonHeight + topOffset;

		redoButton = new PushButton(owner.redoImage);
		add(redoButton);
		setWidgetLeftWidth(redoButton, currentX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		setWidgetTopHeight(redoButton, currentY, Style.Unit.PX, buttonHeight, Style.Unit.PX);
	
		redoButton.setEnabled(false);
		
		//twmi.addTouchStartHandler(redoButton, new ToggleTouchStartHandler());
		//redoButton.addTouchStartHandler(pushTouchStartHandler);
		//redoButton.addMouseDownHandler(pushMouseDownHandler);
		redoButton.addClickHandler(pushClickHandler);
	
		currentY += buttonHeight + topOffset;

        
    }
    
   	public void onBrowserEvent(Event e)
	{
		e.preventDefault();
   		e.stopPropagation();
	}
    
    public void resetDefaults()
    {
    	//wireSolidButton.setImage(owner.solidImage);
    	wireSolidButton.setDown(false);
        
        zoomInButton.setEnabled(true);
        zoomOutButton.setEnabled(true);

        //conDrawButton.setImage(owner.conDrawImage);
        conDrawButton.setDown(false);
        
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);

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
    	
    		if (e.getSource() == zoomInButton)
    		{
    			owner.drawingPanel.zoomIn();
                zoomOutButton.setEnabled(true);
                if (owner.drawingPanel.zoom >= (owner.drawingPanel.MAXZOOM - owner.drawingPanel.ZOOMSTEP / 10))
                {    zoomInButton.setEnabled(false);
                }
                
//GWT                
//              owner.helpBar.setMessage(null, 0);                
    		}
    		else if (e.getSource() == zoomOutButton)
    		{
    			owner.drawingPanel.zoomOut();
                zoomInButton.setEnabled(true);
                if (owner.drawingPanel.zoom <= (owner.drawingPanel.MINZOOM + owner.drawingPanel.ZOOMSTEP / 10))
                {	zoomOutButton.setEnabled(false);    
                }
//GWT      
//              owner.helpBar.setMessage(null, 0);
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
    
    class ToggleTouchStartHandler implements TouchStartHandler
    {
    	public void onTouchStart(TouchStartEvent e)
		{
    		touchStart = true;
			
			// deze LIJKT niet nodig (mag wel)
			//e.preventDefault();
			
			// deze zorgt dat je niet scollt in de DWOPlayer
			e.stopPropagation();

			if (e.getSource() == wireSolidButton)
			{	if (!wireSolidButton.isDown())
				{	owner.drawingPanel.setFilled(true);
				}
				else
				{	owner.drawingPanel.setFilled(false);
				}
			}
			else if (e.getSource() == conDrawButton)
			{	if (!conDrawButton.isDown())
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
    

    class PushTouchStartHandler implements TouchStartHandler
    {
    	public void onTouchStart(TouchStartEvent e)
		{   	
    		touchStart = true;
    		
    		//e.preventDefault();
    		e.stopPropagation();
    	
    		if (e.getSource() == zoomInButton)
    		{
    			owner.drawingPanel.zoomIn();
                zoomOutButton.setEnabled(true);
                if (owner.drawingPanel.zoom >= (owner.drawingPanel.MAXZOOM - owner.drawingPanel.ZOOMSTEP / 10))
                {    zoomInButton.setEnabled(false);
                }
                
//GWT                
//              owner.helpBar.setMessage(null, 0);                
    		}
    		else if (e.getSource() == zoomOutButton)
    		{
    			owner.drawingPanel.zoomOut();
                zoomInButton.setEnabled(true);
                if (owner.drawingPanel.zoom <= (owner.drawingPanel.MINZOOM + owner.drawingPanel.ZOOMSTEP / 10))
                {	zoomOutButton.setEnabled(false);    
                }
//GWT      
//              owner.helpBar.setMessage(null, 0);
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
    
/*    
    // listeners for each imagebutton
    class WireSolidML extends MouseAdapter
    {   String lastHelpMessage;
          
        public void mouseEntered(MouseEvent e)
        {   if (wireSolidButton.enabled)
            {   lastHelpMessage = owner.helpBar.text;
                if (owner.drawingPanel.filled)
                {   //owner.helpBar.setText(owner.tt("wireFrameText"));
                    owner.helpBar.setMessage(owner.tt("wireFrameText"),
                                             owner.helpBar.getSize().width - 
                                             owner.topToolBar.buttonWidth / 2);
                
                }
                else
                {   //owner.helpBar.setText(owner.tt("solidText"));
                    owner.helpBar.setMessage(owner.tt("solidText"),
                                             owner.helpBar.getSize().width - 
                                             owner.topToolBar.buttonWidth / 2);
                
                }
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (wireSolidButton.enabled)
            {   owner.helpBar.setMessage(null, 0);          
                owner.helpBar.setText(lastHelpMessage);
            }
        }    
    }
*/
    
/*    
    class ZoomInML extends MouseAdapter
    {   String lastHelpMessage;
        public void mouseEntered(MouseEvent e)
        {   if (zoomInButton.enabled)
            {   lastHelpMessage = owner.helpBar.text;                
                //owner.helpBar.setText(owner.tt("zoomInText"));
                owner.helpBar.setMessage(owner.tt("zoomInText"),
                                         owner.helpBar.getSize().width - 
                                         owner.topToolBar.buttonWidth / 2);
                
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (zoomInButton.enabled)
            {    owner.helpBar.setMessage(null, 0);          
                 owner.helpBar.setText(lastHelpMessage);
            }
        }    
    }
*/    
    
/*    
    class ZoomOutML extends MouseAdapter
    {   String lastHelpMessage;
        public void mouseEntered(MouseEvent e)
        {   if (zoomOutButton.enabled)
            {   lastHelpMessage = owner.helpBar.text;                
                //owner.helpBar.setText(owner.tt("zoomOutText"));
                owner.helpBar.setMessage(owner.tt("zoomOutText"),
                                         owner.helpBar.getSize().width - 
                                         owner.topToolBar.buttonWidth / 2);
                
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (zoomOutButton.enabled)
            {   owner.helpBar.setMessage(null, 0);           
                owner.helpBar.setText(lastHelpMessage);
            }
        }    
    }
*/    
    
/*    
    class ConDrawML extends MouseAdapter
    {   String lastHelpMessage;
        public void mousePressed(MouseEvent e)
        {   if (conDrawButton.enabled)
            {   if (owner.drawingPanel.mouseMode != 
                    DrawingPanel.FOLDOUT)
                {   owner.drawingPanel.makeFoldOut(0, true);
                    // figureImage is set in stepNum == 1
                }
                else
                {   // back to whole figure
                    conDrawButton.setImage(owner.conDrawImage);
                    owner.drawingPanel.makeFoldOut(0, false);
                }    
                lastHelpMessage = owner.helpBar.text;       
                owner.helpBar.setMessage(null, 0);          
            }
        }  
        public void mouseEntered(MouseEvent e)
        {   if (conDrawButton.enabled)
            {   lastHelpMessage = owner.helpBar.text;                
                if (owner.drawingPanel.mouseMode != 
                    DrawingPanel.FOLDOUT)
                {    
                    //owner.helpBar.setText(owner.tt("conDrawText"));
                    owner.helpBar.setMessage(owner.tt("conDrawText"),
                                             owner.helpBar.getSize().width - 
                                             owner.topToolBar.buttonWidth / 2);
                    
                }    
                else
                {   if (owner.drawingPanel.startFacet == null)
                    {
                    }
                    else
                    {
                        //owner.helpBar.setText(owner.tt("wholeFigureText"));
                        owner.helpBar.setMessage(owner.tt("wholeFigureText"),
                                                 owner.helpBar.getSize().width - 
                                                 owner.topToolBar.buttonWidth / 2);
                    }
                    
                }    
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (conDrawButton.enabled)
            {   owner.helpBar.setMessage(null, 0);           
                owner.helpBar.setText(lastHelpMessage);
            }
        }    
    }    
*/    

/*    
    class UndoML extends MouseAdapter
    {   // remember last message
        String lastHelpMessage;
        public void mousePressed(MouseEvent e)
        {   if (undoButton.enabled)
            {   owner.drawingPanel.undo();    
                lastHelpMessage = owner.helpBar.text;       
                owner.helpBar.setMessage(null, 0);          
            }
        }  
        public void mouseEntered(MouseEvent e)
        {   if (undoButton.enabled)
            {   lastHelpMessage = owner.helpBar.text;
                //owner.helpBar.setText(owner.tt("undoText"));
                owner.helpBar.setMessage(owner.tt("undoText"),
                						 owner.helpBar.getSize().width - 
                						 owner.topToolBar.buttonWidth / 2);
                
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (undoButton.enabled)
            {   owner.helpBar.setMessage(null, 0);          
                owner.helpBar.setText(lastHelpMessage);
            }    
        }    
    }
*/    
        
/*    
    class RedoML extends MouseAdapter
    {   // remember last message
        String lastHelpMessage;
        public void mousePressed(MouseEvent e)
        {   if (redoButton.enabled)
            {   owner.drawingPanel.redo();    
                lastHelpMessage = owner.helpBar.text;       
                owner.helpBar.setMessage(null, 0);          
            }
        }  
        public void mouseEntered(MouseEvent e)
        {   if (redoButton.enabled)
            {   lastHelpMessage = owner.helpBar.text;
                //owner.helpBar.setText(owner.tt("redoText"));
                owner.helpBar.setMessage(owner.tt("redoText"),
                						 owner.helpBar.getSize().width - 
                						 owner.topToolBar.buttonWidth / 2);
                
            }
        }    
        public void mouseExited(MouseEvent e)
        {   if (redoButton.enabled)
            {   owner.helpBar.setMessage(null, 0);          
                owner.helpBar.setText(lastHelpMessage);
            }    
        }    
    }    
*/    

} // class RightToolBar2   

