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
 * PopupPanel for importing code: when opened, program code can be pasted in the text area of
 * typed into the text area; format: see class ProgrammaImporter; upon clicking the importButton
 * the code will be imported and the ImportPopup will be closed 
 * @author huub
 */

public class ImportPopup extends PopupPanel 
{
	/**
	 * text area
	 */
	TextArea textArea;
	/**
	 * import/cancel Button
	 */
	PushButton importButton, annuleerButton;
	/**
	 * instruction label
	 */
	Label codeLabel;
	
	/**
	 * width/height
	 */
	int breedte, hoogte;
	/**
	 * offset for layout
	 */
	int offSet = 10;
	
	/**
	 * JavaLogoSchuifVeld owns the ImportPopup
	 */
	JavaLogoSchuifVeld schuifveld;
	
	/**
	 * layouting the ImportPopup
	 */
	LayoutPanel importPanel;
	
	/**
	 * constructor
	 * @param b width
	 * @param h height
	 * @param sv the owner of this PopupPanel
	 */
	public ImportPopup(int b, int h, JavaLogoSchuifVeld sv)
	{	super(true);
		schuifveld = sv;
		breedte = b;
		hoogte = h;
		importPanel = new LayoutPanel();
		importPanel.setSize("" + breedte + "px", "" + hoogte + "px");
			
		Label codeLabel = new Label(WebLogoGWT.rb.pasteCodeTekst()); //"Plak of Type de Code van het Algorithme");
		codeLabel.addStyleName(WebLogoGWT.webLogoGWTCssResource.codelabel());
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
		importButton = new PushButton(WebLogoGWT.rb.importeerTekst()); //"importeer");
		importButton.addStyleName(WebLogoGWT.webLogoGWTCssResource.pushbutton());
		importPanel.add(importButton);
		importPanel.setWidgetLeftWidth(importButton, buttonX, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		importPanel.setWidgetTopHeight(importButton, hoogte - 20, Style.Unit.PX, 20, Style.Unit.PX);
		importButton.addClickHandler(new PushClickHandler());

		annuleerButton = new PushButton(WebLogoGWT.rb.annuleerTekst()); //"annuleer");
		annuleerButton.addStyleName(WebLogoGWT.webLogoGWTCssResource.pushbutton());
		importPanel.add(annuleerButton);
		importPanel.setWidgetLeftWidth(annuleerButton, buttonX + buttonWidth + offSet, Style.Unit.PX, buttonWidth, Style.Unit.PX);
		importPanel.setWidgetTopHeight(annuleerButton, hoogte - 20, Style.Unit.PX, 20, Style.Unit.PX);
		annuleerButton.addClickHandler(new PushClickHandler());

		setWidget(importPanel);
	}
	
	/**
	 * read the code from the text area and import it
	 */
	public void importeer()
	{
		String s = textArea.getText();
		schuifveld.importeer(s);
	}
	
	/**
	 * inner class for import and cancel Button; note that ImportPopup also closes
	 * when clicking somewhere outside the ImportPopup  
	 * @author huub
	 */
    class PushClickHandler implements ClickHandler
    {
    	
    	public void onClick(ClickEvent e)
    	{
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
