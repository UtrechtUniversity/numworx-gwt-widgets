package fi.heksgwt.client;

//import java.awt.*;
import fi.heksgwt.client.scobjects.ScComponent;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


public class Thermometer extends ScComponent 
{
	private int temp;
	
	Context2d tempContext2d;

	public Thermometer(int x, int y, int b, int h, Context2d c2d) 
	{
		super(x, y, b, h);
		// setBackground(Color.white);
		tempContext2d = c2d;
		
		temp = 0;
	}

	public void paint()
	{
		paint(tempContext2d);
	}
	
	//public void paint(Graphics g)
	public void paint(Context2d g)
	{
		//if (tempContext2d == null)
		//	tempContext2d = g;
		
		
		//double schaal = 1.0 * getSize().height / 300;
		double schaal = 1.0 * hoogte / 300;
		//Font f = new Font("SansSerif", Font.PLAIN, (int) (schaal * 12));
		
		int fontSize = (int) Math.round(schaal * 12);
		String fontString = "" + fontSize + "px arial, sans-serif";
		
		//g.setColor(Color.black);
		
		//FontMetrics fm = g.getFontMetrics();
		g.setFont(fontString);

		//g.setColor(Color.red);
		g.setFillStyle(CssColor.make(255,0,0));
		//g.fillOval((int) (schaal * 20), (int) (schaal * 280), (int) (schaal * 19), (int) (schaal * 19));
		g.beginPath();
		g.arc(xPos + (schaal * 30), yPos + (schaal * 290),(schaal * 10), 0, 2*Math.PI);
		g.fill();
		
		//g.setColor(Color.black);
		//g.setColor(Color.white);
		//g.setFillStyle(CssColor.make(255,255,255));
		//g.fillOval((int) (schaal * 25), 0, (int) (schaal * 9), (int) (schaal * 9));
		//g.beginPath();
		//g.arc(xPos + (schaal * 25), yPos,(schaal * 9), 0, 2*Math.PI);
		//g.fill();
		
		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		//g.drawOval((int) (schaal * 20), (int) (schaal * 280), (int) (schaal * 19), (int) (schaal * 19));
		g.beginPath();
		g.arc(xPos + (schaal * 30), yPos + (schaal * 290),(schaal * 10), 0, 2*Math.PI);
		g.stroke();

		//g.drawOval((int) (schaal * 25), 0, (int) (schaal * 9), (int) (schaal * 9));
		g.beginPath();
		g.arc(xPos + (schaal * 30), yPos ,(schaal * 5), Math.PI, 2 * Math.PI);
		g.stroke();


		//g.setColor(Color.white);
		g.setFillStyle(CssColor.make(255,255,255));
		g.fillRect(xPos + (schaal * 25), yPos + (schaal * 5), (schaal * 10), (schaal * 275));

		int h = 190 + 5 * temp;

		//g.setColor(Color.red);
		g.setFillStyle(CssColor.make(255,0,0));
		g.fillRect(xPos+(schaal * 26), yPos +(schaal * (300 - h)), (schaal * 9), (schaal * (h - 15)));

		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		//g.drawLine((int) (schaal * 25), (int) (schaal * 5), (int) (schaal * 25), (int) (schaal * 280));
		g.beginPath();
		g.moveTo(xPos + (schaal * 25), yPos + (schaal * 1));
		g.lineTo(xPos + (schaal * 25), yPos + (schaal * 280));
		g.stroke();
		//g.drawLine((int) (schaal * 34), (int) (schaal * 5), (int) (schaal * 34), (int) (schaal * 280));
		g.beginPath();
		g.moveTo(xPos +(schaal * 35), yPos + (schaal * 1));
		g.lineTo(xPos + (schaal * 35), yPos + (schaal * 280));
		g.stroke();
		
		

		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		for (int i = -20; i < 21; i++) 
		{
			if (i % 5 == 0)
			{	//g.drawLine((int) (schaal * 22), (int) (schaal * (110 + 5 * i)), (int) (schaal * 34), (int) (schaal * (110 + 5 * i)));
				g.beginPath();
				g.moveTo(xPos + (schaal * 21), yPos + (schaal * (110 + 5 * i)));
				g.lineTo(xPos + (schaal * 34), yPos + (schaal * (110 + 5 * i)));
				g.stroke();
			}
			else
			{	//g.drawLine((int) (schaal * 25), (int) (schaal * (110 + 5 * i)), (int) (schaal * 34), (int) (schaal * (110 + 5 * i)));
				g.beginPath();
				g.moveTo(xPos + (schaal * 25), yPos + (schaal * (110 + 5 * i)));
				g.lineTo(xPos + (schaal * 34), yPos + (schaal * (110 + 5 * i)));
				g.stroke();

			}

		}

		g.setFillStyle(CssColor.make(0,0,0));
		for (int i = -20; i < 21; i += 5) 
		{
			String s = Integer.toString(i) + "\u00B0"; //"�";
			TextMetrics tm = g.measureText(s);
			//int sw = fm.stringWidth(s);
			double sw = tm.getWidth();
			//int sh = fm.getHeight();
			double sh = fontSize / 2;
			//g.drawString(Integer.toString(i) + "\u00B0", //"�", 
			//		(int) (schaal * (19 - sw)), (int) (schaal * (110 - 5 * i + sh / 2)));
			g.fillText(Integer.toString(i) + "\u00B0", //"�", 
					xPos + (schaal * (15 - sw)), yPos + (schaal * (110 - 5 * i)) + sh);

		}

		// g.drawString(Integer.toString(temp)+"�",0,getSize().height);
	}

	public int geefTemp() 
	{
		return temp;

	}

	public void zetTemp(int t) 
	{
		temp = t;
		paint();
	}

	public void tempPlus() 
	{
		temp++;
		paint();
	}

	public void tempMin() 
	{
		temp--;
		paint();
	}

}
