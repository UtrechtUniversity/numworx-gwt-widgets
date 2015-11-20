package fi.weblogogwt.client;

//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.HeadlessException;
//import java.awt.Insets;
//import java.awt.event.WindowEvent;
//import java.awt.event.WindowListener;

//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HTML;

public class VardisplayPanel extends LayoutPanel //JPanel
{
	//private TextArea displayarea;
	private Label displayarea;
	//private HTML displayarea;
	
	int breedte, hoogte;
		
	public VardisplayPanel(int b, int h)
	{
		breedte = b;
		hoogte = h;
		//super(Style.Unit.PX);
		//setLayout(null);
		//setBackground(new Color(161, 255,161));
		//addStyleName(WebLogoGWT.webLogoGWTCssResource.vardisplay());
		
		Label toelichting = new Label("Variabelen:");
		toelichting.addStyleName(WebLogoGWT.webLogoGWTCssResource.varlabel());
		add(toelichting);
		setWidgetLeftWidth(toelichting, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		setWidgetTopHeight(toelichting, 0, Style.Unit.PX, 20, Style.Unit.PX);
		
		//toelichting.setBounds(10, 0, 150, 25);
		//toelichting.setFont(JavaLogoWeb.boldfont);
		//addNorth(toelichting);
		
		displayarea = new Label(); //TextArea();
		displayarea.addStyleName(WebLogoGWT.webLogoGWTCssResource.vardisplay());
		//displayarea.setMargin(new Insets(3,5,3,5));
		//displayarea.setFont(JavaLogoWeb.defaultfont);
		//displayarea.setBackground(new Color(221, 255, 221));
		//displayarea.setBounds(0, 25, 160, 475);
		add(displayarea);
		setWidgetLeftWidth(displayarea, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		setWidgetTopHeight(displayarea, 20, Style.Unit.PX, hoogte - 20, Style.Unit.PX);

	}
	
	public void setContent(String s)
	{
		displayarea.setText(s);
		//displayarea.setHTML("<pre>" + s + "</pre>");
	}
}
