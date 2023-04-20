package fi.weblogogwt.client;


import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * class implementing a Panel for displaying the variables during tracing;
 * there is a header Label and the text is displayed using a multi-line Label 	 
 */

public class VardisplayPanel extends LayoutPanel 
{
	/**
	 * multi-line label for displaying text
	 */
	private Label displayarea;
	/**
	 * width and height
	 */
	int breedte, hoogte;
		
	/**
	 * constructor
	 * @param b width
	 * @param h height
	 */
	public VardisplayPanel(int b, int h)
	{
		breedte = b;
		hoogte = h;
		// header
		Label toelichting = new Label("Variabelen:");
		toelichting.addStyleName(WebLogoGWT.webLogoGWTCssResource.varlabel());
		add(toelichting);
		setWidgetLeftWidth(toelichting, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		setWidgetTopHeight(toelichting, 0, Style.Unit.PX, 20, Style.Unit.PX);
		// multi-line label
		displayarea = new Label(); 
		displayarea.addStyleName(WebLogoGWT.webLogoGWTCssResource.vardisplay());
		add(displayarea);
		setWidgetLeftWidth(displayarea, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		setWidgetTopHeight(displayarea, 20, Style.Unit.PX, hoogte - 20, Style.Unit.PX);
	}

	/**
	 * fill the display area with text
	 * @param s text 
	 */
	public void setContent(String s)
	{
		displayarea.setText(s);
	}
}
