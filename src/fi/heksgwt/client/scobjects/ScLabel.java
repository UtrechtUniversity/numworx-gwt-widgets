package fi.heksgwt.client.scobjects;

//import java.awt.*;
//import java.awt.event.*;

//import javax.swing.JPanel;

//import fi.heks.Heks;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

public class ScLabel implements ScObject 
{
	public static int CENTER = 0;
	public static int RECHTS = 2;
	public static int LINKS = 1;

	private int uitlijning;

	protected String opschrift;
	//protected Font labelfont;
	protected String fontString;

	public double schaal;
	public double relx, rely, relb, relh;
	public boolean resized;
	
	boolean textRtoL = false;
	
	int xPos, yPos, breedte, hoogte;
	
	Context2d labelContext2d;
	
	boolean visible = true;

	public ScLabel(int x, int y, int b, int h, String str) 
	{
		opschrift = str;
		schaal = 1;
		relx = x;
		rely = y;
		relb = b;
		relh = h;
		setBounds(x, y, b, h);

		uitlijning = CENTER;
		
		//setOpaque(false);
		//boolean textRtoL = false;
		//if (Heks.rb.getLocale().getLanguage().equals("nl"))
		//	textRtoL = true; //!ComponentOrientation.getOrientation(Heks.language).isLeftToRight();

	}

	public void setVisible(boolean b)
	{
		visible = b;
	}
	
	public void setBounds(int x, int y, int b, int h)
	{
		xPos = x; yPos = y; breedte = b; hoogte = h;
	}

	public String getLabel() {
		return opschrift;
	}

	public void setLabel(String label) 
	{
		opschrift = label;
		if (labelContext2d != null)
		paint(labelContext2d);
	}

	//public void paintComponent(Graphics gr)
	public void paint(Context2d g)
	{
		if (!visible)
			return;
		
		if (labelContext2d == null)
			labelContext2d = g;
			
		//Graphics g;
		//{
		//	g = (Graphics2D) gr;
		//	if (System.getProperty("java.specification.version").equals("1.6") || System.getProperty("java.specification.version").equals("1.7")) {
		//		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		//		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, new Integer(100));
		//	} else {
		//		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//	}
		//}

		//g.setColor(getBackground());
		//g.fillRect(0, 0, getWidth(), getHeight());
		//g.setColor(this.getForeground());
		g.setFillStyle(CssColor.make(0,0,0));
		//Font f = new Font("SansSerif", Font.PLAIN, (int) (3 * schaal * relh / 4));
		int fontSize = (int) Math.round(3 * schaal * relh / 4);
		fontString = "" + fontSize + "px arial, sans-serif"; 
		g.setFont(fontString);
		//FontMetrics fm = g.getFontMetrics();
		int woordbreedte = 0;
		if (opschrift != null)
		{	//woordbreedte = fm.stringWidth(opschrift);
			TextMetrics tm = g.measureText(opschrift);
			woordbreedte = (int) Math.round(tm.getWidth());
		}
		int beginx = 0;
		if (uitlijning == 0)
			beginx = (breedte - woordbreedte) / 2;
		else if (uitlijning == 1)
			beginx = 0;
		else if (uitlijning == 2)
			beginx = breedte - woordbreedte;
		if (opschrift != null)
		{	//g.drawString(opschrift, beginx, (getSize().height + fm.getHeight()) / 2 - fm.getDescent());
			g.fillText(opschrift, xPos+beginx, yPos+2*hoogte/3);
		}
	}

	public void setResized(boolean b) 
	{
		resized = b;
	}

	public void schaal(double s) 
	{
		schaal = s;
		int x = (int) (schaal * relx);
		int y = (int) (schaal * rely);
		int b = (int) (schaal * relb);
		int h = (int) (schaal * relh);
		setBounds(x, y, b, h);
	}

	public void lijnUit(int soort) 
	{
		uitlijning = soort;
		//boolean textRtoL = !ComponentOrientation.getOrientation(Heks.language).isLeftToRight();
		if(textRtoL && uitlijning==2) uitlijning = 1;
		if(textRtoL && uitlijning==1) uitlijning = 2;
		
	}
	
	public boolean contains(int x, int y)
	{
		return (x >= xPos) && (x <= (xPos + breedte)) && (y >= yPos) && (y <= (yPos + hoogte));
		
	}

}