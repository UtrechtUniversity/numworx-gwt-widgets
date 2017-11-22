package fi.weblogogwt.client;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Label;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * PopupPanel for exporting code: when opened, the PopupPanel will contain the code of the 
 * current program (including subroutines); format: see method getCode() in class JavaLogoSchuifVeld; the user
 * can then select this code in the text area of the PopupPanel and copy the code to the clipboard.
 * @author huub
 */

public class ExportPopup extends PopupPanel 
{
	/**
	 * text area
	 */
	TextArea textArea;
	/**
	 * button for closing the ExportPopup
	 */
	PushButton closeButton;
	/**
	 * title label
	 */
	Label codeLabel;
	/**
	 * width and height
	 */
	int breedte, hoogte;
	/**
	 * JavaLogoSchuifVeld owns the ExportPopup
	 */
	JavaLogoSchuifVeld schuifveld;
	
	/**
	 * layouting the ExportPopup
	 */
	LayoutPanel exportPanel;
	
	/**
	 * constructor
	 * @param b width
	 * @param h height
	 * @param sv the owner of this PopupPanel
	 */
	public ExportPopup(int b, int h, JavaLogoSchuifVeld sv)
	{	super(true);
		schuifveld = sv;
		breedte = b;
		hoogte = h;

		exportPanel = new LayoutPanel();
		exportPanel.setSize("" + breedte + "px", "" + hoogte + "px");
			
		Label codeLabel = new Label(WebLogoGWT.rb.codeTekst()); //"Code van het Algorithme");
		codeLabel.addStyleName(WebLogoGWT.webLogoGWTCssResource.codelabel());
		exportPanel.add(codeLabel);
		exportPanel.setWidgetLeftWidth(codeLabel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		exportPanel.setWidgetTopHeight(codeLabel, 0, Style.Unit.PX, 20, Style.Unit.PX);

		textArea = new TextArea();
		exportPanel.add(textArea);
		exportPanel.setWidgetLeftWidth(textArea, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		exportPanel.setWidgetTopHeight(textArea, 20, Style.Unit.PX, hoogte - 40, Style.Unit.PX);
		textArea.setSize("" + (breedte-15) + "px", "" + hoogte + "px");
		
		int buttonWidth = 80;
		int buttonX = (breedte - buttonWidth) / 2;
		closeButton = new PushButton(WebLogoGWT.rb.sluitenTekst()); //"sluiten");
		closeButton.addStyleName(WebLogoGWT.webLogoGWTCssResource.pushbutton());
		exportPanel.add(closeButton);
		exportPanel.setWidgetLeftWidth(closeButton, buttonX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		exportPanel.setWidgetTopHeight(closeButton, hoogte - 20, Style.Unit.PX, 20, Style.Unit.PX);
		closeButton.addClickHandler(new PushClickHandler());
	
		setWidget(exportPanel);
	}
	
	/**
	 * write String s in the text area 
	 * @param s text to be written
	 */
	public void export(String s)
	{
		textArea.setText(s);
	}
	
	/**
	 * inner class for closeButton; note that ExportPopup also closes
	 * when clicking somewhere outside the ExportPopup  
	 * @author huub
	 */
    class PushClickHandler implements ClickHandler
    {
    	
    	public void onClick(ClickEvent e)
    	{
			e.stopPropagation();
			if (e.getSource() == closeButton)
			{
				setVisible(false);
			}
    	}
    }	
}
