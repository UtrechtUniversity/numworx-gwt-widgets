package fi.kladjegwt.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;

public class ColorPopup extends PopupPanel
{
	KladjeGWT owner;
	int numColors = 8; 
	LayoutPanel togglePanel;
	int toggleSize = 20;
	int offset = 5;
	int breedte, hoogte;
	
	ToggleButton zwartButton, grijsButton, roodButton, oranjeButton, groenButton, cyaanButton, blauwButton, magentaButton, 
		         geelButton;
	
	public ColorPopup(KladjeGWT o)
	{
		super(true);
		owner = o;
		breedte = (offset + toggleSize) * numColors + offset;
		hoogte = toggleSize + 2 * offset;
		togglePanel = new LayoutPanel();
		togglePanel.addStyleName(owner.kladjeCss.colorpanel());
		togglePanel.setSize("" + breedte + "px", "" + hoogte + "px");
		
		makeTogglePanel();
		
		setWidget(togglePanel);
		
	}
	
	public void makeTogglePanel()
	{
		int currentX = offset;
		int currentY = offset;
		
		ToggleClickHandler toggleClickHandler = new ToggleClickHandler();
		
		zwartButton = new ToggleButton(owner.zwartImage, owner.zwartImage);
		togglePanel.add(zwartButton);
		togglePanel.setWidgetLeftWidth(zwartButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		togglePanel.setWidgetTopHeight(zwartButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		zwartButton.setDown(true);
		zwartButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize + offset;

		grijsButton = new ToggleButton(owner.grijsImage, owner.grijsImage);
		togglePanel.add(grijsButton);
		togglePanel.setWidgetLeftWidth(grijsButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		togglePanel.setWidgetTopHeight(grijsButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		grijsButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize + offset;
		
		roodButton = new ToggleButton(owner.roodImage, owner.roodImage);
		togglePanel.add(roodButton);
		togglePanel.setWidgetLeftWidth(roodButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		togglePanel.setWidgetTopHeight(roodButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		roodButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize + offset;
	
		oranjeButton = new ToggleButton(owner.oranjeImage, owner.oranjeImage);
		togglePanel.add(oranjeButton);
		togglePanel.setWidgetLeftWidth(oranjeButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		togglePanel.setWidgetTopHeight(oranjeButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		oranjeButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize + offset;
				
		groenButton = new ToggleButton(owner.groenImage, owner.groenImage);
		togglePanel.add(groenButton);
		togglePanel.setWidgetLeftWidth(groenButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		togglePanel.setWidgetTopHeight(groenButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		groenButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize + offset;
		
		cyaanButton = new ToggleButton(owner.cyaanImage, owner.cyaanImage);
		togglePanel.add(cyaanButton);
		togglePanel.setWidgetLeftWidth(cyaanButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		togglePanel.setWidgetTopHeight(cyaanButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		cyaanButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize + offset;

		blauwButton = new ToggleButton(owner.blauwImage, owner.blauwImage);
		togglePanel.add(blauwButton);
		togglePanel.setWidgetLeftWidth(blauwButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		togglePanel.setWidgetTopHeight(blauwButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		blauwButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize + offset;
				
		magentaButton = new ToggleButton(owner.magentaImage, owner.magentaImage);
		togglePanel.add(magentaButton);
		togglePanel.setWidgetLeftWidth(magentaButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		togglePanel.setWidgetTopHeight(magentaButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		magentaButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize + offset;

/*		
		geelButton = new ToggleButton(owner.geelImage, owner.geelImage);
		togglePanel.add(geelButton);
		togglePanel.setWidgetLeftWidth(geelButton, currentX, Style.Unit.PX, toggleSize, Style.Unit.PX);
		togglePanel.setWidgetTopHeight(geelButton, currentY, Style.Unit.PX, toggleSize, Style.Unit.PX);
		geelButton.addClickHandler(toggleClickHandler);
		currentX += toggleSize + offset;
*/		
		
	}

  	void buttonsUp(ToggleButton tb)
   	{
   		if (!zwartButton.equals(tb))
   			zwartButton.setDown(false);
   		if (!grijsButton.equals(tb))
   			grijsButton.setDown(false);
 		if (!roodButton.equals(tb))
 			roodButton.setDown(false);
   		if (!oranjeButton.equals(tb))
   			oranjeButton.setDown(false);
   		if (!groenButton.equals(tb))
   			groenButton.setDown(false);
   		if (!cyaanButton.equals(tb))
   			cyaanButton.setDown(false);
   		if (!blauwButton.equals(tb))
   			blauwButton.setDown(false);
 		if (!magentaButton.equals(tb))
   			magentaButton.setDown(false);
// 		if (!geelButton.equals(tb))
// 			geelButton.setDown(false);
   	}
  	
   	class ToggleClickHandler implements ClickHandler
	{
   		public void onClick(ClickEvent e)
		{
    		if (e.getSource() == zwartButton)
    		{	if (zwartButton.isDown())
    			{	buttonsUp(zwartButton);
    				owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.zwart;
    			}
    		}
    		else if (e.getSource() == grijsButton)
    		{	if (grijsButton.isDown())
    			{	buttonsUp(grijsButton);
    				owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.grijs;
    			}
    		}

    		else if (e.getSource() == roodButton)
    		{	if (roodButton.isDown())
    			{	buttonsUp(roodButton);
   					owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.rood;
    			}
    		}

    		else if (e.getSource() == oranjeButton)
    		{	if (oranjeButton.isDown())
    			{	buttonsUp(oranjeButton);
   					owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.oranje;
    			}
    		}
    		else if (e.getSource() == groenButton)
    		{	if (groenButton.isDown())
    			{	buttonsUp(groenButton);
      				owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.groen;
    			}
    		}
    		else if (e.getSource() == cyaanButton)
    		{	if (cyaanButton.isDown())
    			{	buttonsUp(cyaanButton);
      				owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.cyaan;
    			}
    		}
    		else if (e.getSource() == blauwButton)
    		{	if (blauwButton.isDown())
    			{	buttonsUp(blauwButton);
      				owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.blauw;
    			}
    		}
    		else if (e.getSource() == magentaButton)
    		{	if (magentaButton.isDown())
    			{	buttonsUp(magentaButton);
     				owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.magenta;
    			}
    		}
/*    		
    		else if (e.getSource() == geelButton)
    		{
    			if (geelButton.isDown())
    			{
    				buttonsUp(geelButton);
    				
      				owner.kladjeGWTVeld.drawingColor = KladjeGWTVeld.geel;
      				
    			}
    		}
*/    		
    			
		}
		
	}
  	
}	