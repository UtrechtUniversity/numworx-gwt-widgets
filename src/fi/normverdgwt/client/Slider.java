package fi.normverdgwt.client;

//import java.awt.*;
//import java.awt.event.*;

//import javax.swing.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

public class Slider	//extends JComponent implements MouseListener,MouseMotionListener
{	//private Image im;
	//private Graphics gIm;
	
	Context2d sliderContext2d;
	int xPos, yPos, breedte, hoogte;
	Rectangle sliderRectangle;
	
	private boolean resize;
	//protected ActionListener actionListener = null;
	
	private int lengte;
	private int stand;
	private int minimum = -2;
	private int maximum;
	private int muisStartX, muisStartY;
	//private Polygon schuifKnop;
	private boolean raak;
	
	private boolean showLine = true;
	
	private CssColor knopColor = CssColor.make(255,0,0);
	
	private boolean enabled = true;
	
	NormaalPanel owner;
	String param = "";
	
	public Slider(NormaalPanel o, int aantalPix, int beginst, int x, int y, Context2d c2d, String p)
	{	
		owner = o;
		param = p;
		
		lengte = aantalPix;
		maximum = lengte;
		stand = beginst;
		
		//addMouseListener(this);
		//addMouseMotionListener(this);
		//setSize(lengte + 10, 13);
		xPos = x;
		yPos = y;
		breedte = lengte + 10;
		hoogte = 13;
		sliderRectangle = new Rectangle(xPos, yPos, breedte, hoogte);
		
		sliderContext2d = c2d;
	}
	
	public void zetLengte(int aantalPix)
	{	lengte = aantalPix;
		maximum = lengte;
		//setSize(lengte + 10, 13);
		breedte = lengte + 10;
		sliderRectangle = new Rectangle(xPos, yPos, breedte, hoogte);
		resize = true;
		paint();
	}
	
	public void setLocation(int x, int y)
	{
		xPos = x;
		yPos = y;
		sliderRectangle = new Rectangle(xPos, yPos, breedte, hoogte);
	}
		
	public void zetShowLine(boolean b)
	{	showLine = b;
	}	
	
	public void zetKnopColor(CssColor c)
	{	knopColor = c;
	}
		
	public void zetEnabled(boolean b)
	{	enabled = b;
	}
		
/*	
	public void paintComponent(Graphics g)
	{	
		{ 	if (im == null || resize)
			{	im = createImage(getSize().width, getSize().height);
  				gIm = im.getGraphics();
			}
			gIm.setColor(getBackground());
			//gIm.fillRect(0, 0, getSize().width, getSize().height);
			//tekenSlider(gIm);
			//g.drawImage(im, 0, 0, null);
			
			tekenSlider(g);
  		}
	}
*/	
/*	
	public void update(Graphics g)
	{	paint(g);
	}
*/	
	public void paint()
	{
		tekenSlider(sliderContext2d);
		
//System.out.println(param + "sl " + enabled);		
	}
	
	//public void tekenSlider(Graphics g)
	public void tekenSlider(Context2d g)
	{	

		if (enabled && !param.equals("grens"))
		{	
			g.setFillStyle(NormaalPanel.veryLightBlue);
			g.fillRect(xPos, yPos, breedte, hoogte);
		}
		
	
		if (showLine && enabled)
		{	
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			//g.drawLine(5, 5, lengte + 5, 5);
			g.beginPath();
			g.moveTo(xPos + 5, yPos + 5);
			g.lineTo(xPos + lengte + 5, yPos + 5);
			g.stroke();
		
		}
	
		if (enabled)
		{
			g.setFillStyle(knopColor);
			//g.fillOval(5 + stand - 3, 2, 6, 6);
            
			g.beginPath();
            g.arc(xPos + 5 + stand, yPos + 5, 3, 0, 2 * Math.PI);
       	 	g.fill();

			
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			//g.drawOval(5 + stand - 3, 2, 6, 6);
			
			g.beginPath();
            g.arc(xPos + 5 + stand, yPos + 5, 3, 0, 2 * Math.PI);
       	 	g.stroke();
       	 	
//Rectangle pressRect = new Rectangle(xPos + stand - 3, yPos, 16, 13);
//g.rect(pressRect.x, pressRect.y, pressRect.width, pressRect.height);
//g.stroke();
			
		}
	
	}

/*	
	public void addActionListener(ActionListener l) 
 	{	actionListener = AWTEventMulticaster.add(actionListener,l);
 	}
*/
/*	
 	public void removeActionListener(ActionListener l)
 	{	actionListener = AWTEventMulticaster.remove(actionListener, l);
 	}
*/	
	public int geefStand()
	{	return stand;
	}
	
	public void zetStand(int std)
	{	if (std > maximum)
			stand = maximum;
		else if (std < minimum)
			stand = minimum;
		else 
			stand = std;
		paint();
	}
	
	public void setMaximum(int max)
	{	maximum = max;
	}

	public void setMinimum(int min)
	{	minimum = min - 2;
	}

	public int getMaximum()
	{	return maximum;
	}
	
	public int getMinimum()
	{	return 0;//minimum;
	}
	
	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	//raak = enabled && (new Rectangle(stand, 0, 10, 20)).contains(e.getX(), e.getY());
		raak = enabled && (new Rectangle(xPos + stand - 3, yPos, 16, 13)).contains(eventX, eventY);
		muisStartX = eventX;
		muisStartY = eventY;
		
//GWT??		
		//if (raak && actionListener != null)
		//{	actionListener.actionPerformed(new ActionEvent(this, 0, "start"));
		//}
	}
	
	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	if (!raak && enabled && 
			(new Rectangle(xPos + stand - 3, yPos, 16, 13).contains(eventX, eventY)))
		{	raak = true;
			muisStartX = eventX;
			
//GWT??			
			//if (raak && actionListener != null)
			//{	actionListener.actionPerformed(new ActionEvent(this, 0, "start"));
			//}
		}
		if (raak)
		{	int x = eventX;
			int dx = x - muisStartX;
			stand = stand + dx;
			if (stand > maximum) 
			{	stand = maximum;
			}
			else if (stand < minimum) 
			{	stand = minimum;
			}
			//if (x < 5 || x > lengte + 20)
			if (x < (xPos + 5) || x > (xPos + lengte + 20))
			{	raak = false;
			}
			paint();
			
			if (param.equals("mu"))
				owner.processMuSlider();
			else if (param.equals("sigma"))
				owner.processSigmaSlider();
			else if (param.equals("grens"))
				owner.processGrensSlider();
			else if (param.equals("kans"))
				owner.processKansSlider();
			
			
//GWT??			
			//if (actionListener != null)
 			//{	actionListener.actionPerformed(new ActionEvent(this, 0, "verschoven"));
 			//}
			muisStartX = x;
		}
	}
	
	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction()
	{	
//GWT??		
		//if (actionListener != null)
		//{	actionListener.actionPerformed( new ActionEvent(this, 0, "stop") );
		//}
	}
	//public void mouseClicked(MouseEvent e)
	//{}
	//public void mouseExited(MouseEvent e)
	//{}
	//public void mouseEntered(MouseEvent e)
	//{}
	//public void mouseMoved(MouseEvent e)
	//{}
}
