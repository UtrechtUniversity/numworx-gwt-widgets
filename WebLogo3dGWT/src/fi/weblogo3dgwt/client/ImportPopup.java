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
 * see class ImportPopup in WebLogoGWT
 * @author huub
 */
public class ImportPopup extends PopupPanel 
{
	TextArea textArea;
	PushButton importButton, annuleerButton;
	Label codeLabel;
	
	int breedte, hoogte;
	int offSet = 10;
	
	JavaLogoSchuifVeld schuifveld;
	
	LayoutPanel importPanel;
	
	public ImportPopup(int b, int h, JavaLogoSchuifVeld sv)
	{	super(true);
		schuifveld = sv;
	
		//breedte wordt meteen bij vulIn aangepast		
		breedte = b;
		hoogte = h;

		importPanel = new LayoutPanel();
		importPanel.setSize("" + breedte + "px", "" + hoogte + "px");
			
		Label codeLabel = new Label(WebLogo3dGWT.rb.pasteCodeTekst()); 
		codeLabel.addStyleName(WebLogo3dGWT.webLogo3dGWTCssResource.codelabel());
		importPanel.add(codeLabel);
		importPanel.setWidgetLeftWidth(codeLabel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		importPanel.setWidgetTopHeight(codeLabel, 0, Style.Unit.PX, 20, Style.Unit.PX);
		
		
		textArea = new TextArea();
		importPanel.add(textArea);
		importPanel.setWidgetLeftWidth(textArea, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		importPanel.setWidgetTopHeight(textArea, 20, Style.Unit.PX, hoogte - 40, Style.Unit.PX);
		textArea.setSize("" + (breedte-15) + "px", "" + hoogte + "px");
		
		int buttonWidth = 100;
		int buttonX = (breedte - 2 * buttonWidth - offSet) / 2;
		importButton = new PushButton(WebLogo3dGWT.rb.importeerTekst()); //"importeer");
		importButton.addStyleName(WebLogo3dGWT.webLogo3dGWTCssResource.pushbutton());
		importPanel.add(importButton);
		importPanel.setWidgetLeftWidth(importButton, buttonX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		importPanel.setWidgetTopHeight(importButton, hoogte - 20, Style.Unit.PX, 20, Style.Unit.PX);
		importButton.addClickHandler(new PushClickHandler());

		annuleerButton = new PushButton(WebLogo3dGWT.rb.annuleerTekst()); //"annuleer");
		annuleerButton.addStyleName(WebLogo3dGWT.webLogo3dGWTCssResource.pushbutton());
		importPanel.add(annuleerButton);
		importPanel.setWidgetLeftWidth(annuleerButton, buttonX + buttonWidth + offSet, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		importPanel.setWidgetTopHeight(annuleerButton, hoogte - 20, Style.Unit.PX, 20, Style.Unit.PX);
		annuleerButton.addClickHandler(new PushClickHandler());

		setWidget(importPanel);
	
	}
	
	public void importeer()
	{
		String s = textArea.getText();
		schuifveld.importeer(s);
	}
	
    class PushClickHandler implements ClickHandler
    {
    	
    	public void onClick(ClickEvent e)
    	{
			//e.preventDefault();
			e.stopPropagation();
			if (e.getSource() == importButton)
			{
				importeer();
				setVisible(false);
			}
			if (e.getSource() == annuleerButton)
			{
				setVisible(false);
			}
    	}
    }
}
