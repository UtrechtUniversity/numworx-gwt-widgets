package fi.kladjegwt.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * een PopupPanel met daarin een LayoutPanel met ToggleButtons om de tekenkleur in
 * KladjeGWTVeld te kiezen; de ToggleButtons werken zodanig dat er altijd 
 * een ingedrukt is, nl. degene die correspondeert met de actuele tekenkleur 
 * @author huub
 */

public class ColorPopup extends PopupPanel
{
	/**
	 * eigenaar van dit PopupPanel
	 */
	KladjeGWT owner;
	/**
	 * aantal beschikbare kleuren
	 */
	int numColors = 8;
	/**
	 * een Panel voor de ToggelButtons
	 */
	LayoutPanel togglePanel;
	/**
	 * afmeting van de ToggleButtons in pixels
	 */
	int toggleSize = 20;
	/**
	 * afstand tussen de ToggleButtons
	 */
	int offset = 5;
	/**
	 * breedte en hoogte van het togglePanel (en dus van dit PopupPanel) 
	 */
	int breedte, hoogte;
	
	/**
	 * de ToggleButtons
	 */
	ToggleButton zwartButton, grijsButton, roodButton, oranjeButton, groenButton, cyaanButton, blauwButton, magentaButton; 
	
	/**'
	 * constructor
	 * @param o eigenaar
	 */
	public ColorPopup(KladjeGWT o)
	{
		super(true);
		owner = o;
		breedte = (offset + toggleSize) * numColors + offset;
		hoogte = toggleSize + 2 * offset;
		togglePanel = new LayoutPanel();
		togglePanel.addStyleName(KladjeGWT.kladjeCss.colorpanel());
		togglePanel.setSize("" + breedte + "px", "" + hoogte + "px");
		
		makeTogglePanel();
		
		setWidget(togglePanel);
		
	}
	
	/**
	 * zet de ToggleButtons op het TogglePanel, voeg ClickHandlers toe
	 * en zet de ToggleButton voor zwart op ingedrukt (default tekenkleur)
	 */
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

	}

	/**
	 * ToggleButton tb werd ingedrukt, dus zorg dat alle andere
	 * ToggelButtons niet ingedrukt zijn (GWT heeft niet zoiets als een RadioButtonGroup)   
	 * @param tb de ToggleButton die werd ingedrukt
	 */
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
   	}
  	
  	/**
  	 * inner class voor ClickEvents op de ToggleButtons;
  	 * actie: zet de corresponderende tekenkleur in KladjeGWTVeld 
  	 * @author huub
  	 */
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
		}
		
	}
  	
}	