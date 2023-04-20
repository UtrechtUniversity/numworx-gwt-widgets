package fi.weblogo3dgwt.client;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Label;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * see class ExportPopup in WebLogoGWT
 * @author huub
 */

public class ExportPopup extends PopupPanel 
{
	TextArea textArea;
	PushButton closeButton;
	Label codeLabel;
	
	int breedte, hoogte;
	
	JavaLogoSchuifVeld schuifveld;
	
	LayoutPanel exportPanel;
	
	public ExportPopup(int b, int h, JavaLogoSchuifVeld sv)
	{	super(true);
		schuifveld = sv;
	
		//breedte wordt meteen bij vulIn aangepast		
		breedte = b;
		hoogte = h;

		exportPanel = new LayoutPanel();
		exportPanel.setSize("" + breedte + "px", "" + hoogte + "px");
			
		Label codeLabel = new Label(WebLogo3dGWT.rb.codeTekst()); //"Code van het Algorithme");
		codeLabel.addStyleName(WebLogo3dGWT.webLogo3dGWTCssResource.codelabel());
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
		closeButton = new PushButton(WebLogo3dGWT.rb.sluitenTekst()); //"sluiten");
		closeButton.addStyleName(WebLogo3dGWT.webLogo3dGWTCssResource.pushbutton());
		exportPanel.add(closeButton);
		exportPanel.setWidgetLeftWidth(closeButton, buttonX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		exportPanel.setWidgetTopHeight(closeButton, hoogte - 20, Style.Unit.PX, 20, Style.Unit.PX);
		closeButton.addClickHandler(new PushClickHandler());
	
		setWidget(exportPanel);
	}
	
	public void export(String s)
	{
		textArea.setText(s);
	}
	
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
