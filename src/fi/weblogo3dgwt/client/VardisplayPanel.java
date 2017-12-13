package fi.weblogo3dgwt.client;


import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * see class VardisplayPanel in WebLogoGWT
 */
public class VardisplayPanel extends LayoutPanel 
{
	private Label displayarea;
	
	int breedte, hoogte;
		
	public VardisplayPanel(int b, int h)
	{
		breedte = b;
		hoogte = h;
		
		Label toelichting = new Label("Variabelen:");
		toelichting.addStyleName(WebLogo3dGWT.webLogo3dGWTCssResource.varlabel());
		add(toelichting);
		setWidgetLeftWidth(toelichting, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		setWidgetTopHeight(toelichting, 0, Style.Unit.PX, 20, Style.Unit.PX);
		
		displayarea = new Label(); //TextArea();
		displayarea.addStyleName(WebLogo3dGWT.webLogo3dGWTCssResource.vardisplay());
		add(displayarea);
		setWidgetLeftWidth(displayarea, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		setWidgetTopHeight(displayarea, 20, Style.Unit.PX, hoogte - 20, Style.Unit.PX);

	}
	
	public void setContent(String s)
	{
		displayarea.setText(s);
	}
}
