package fi.normverdgwt.client;

//import java.awt.*;
//import java.awt.event.*;

//import javax.swing.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

public class DoubleSlider //extends JComponent implements MouseListener, MouseMotionListener
{	//private Image im;
	//private Graphics gIm;
	
	Context2d sliderContext2d;
	int xPos, yPos, breedte, hoogte;
	Rectangle sliderRectangle;
	
	private boolean resize;
	//protected ActionListener actionListener = null;
	
	private int lengte;
	
	private int standLinks, standRechts;
	
	private int minimumLinks = -2;
	private int maximumLinks;

	private int minimumRechts;
	private int maximumRechts; 
	
	private int muisStartX, muisStartY;

	//private Polygon schuifKnop;
	
	private boolean raakLinks, raakRechts;
	
	private boolean linksEnabled = true;
	private boolean rechtsEnabled = true;
	
	private boolean showLine = true;
	
	private CssColor knopColor = CssColor.make(255,0,0);
	
	private CssColor knopLinksColor = CssColor.make(255,153,0);
	private CssColor knopRechtsColor = CssColor.make(255,0,255);
	
	int pixDis = 10;
	
	NormaalPanel owner;
	
	public DoubleSlider(NormaalPanel o, int aantalPix, int beginLinks, int beginRechts,
						int x, int y, Context2d c2d)
	{	
		owner = o;
		
		lengte = aantalPix;
		standLinks = beginLinks;
		standRechts = beginRechts;
		// minimumLinks = -2; blijft zo
		maximumLinks = standRechts - pixDis;		
		minimumRechts = standLinks + pixDis - 2;
		maximumRechts = lengte;

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
		maximumRechts = lengte;
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

	public int getLengte()
	{	return lengte;
	}		
	
	public void zetShowLine(boolean b)
	{	showLine = b;
	}	
	
	public void zetLinksEnabled(boolean b)
	{	linksEnabled = b;
	}

	public void zetRechtsEnabled(boolean b)
	{	rechtsEnabled = b;
	}
	
	public void zetKnopLinksColor(CssColor c)
	{	knopLinksColor = c;
	}

	public void zetKnopRechtsColor(CssColor c)
	{	knopRechtsColor = c;
	}
		
	public void paint()
	{
		tekenSlider(sliderContext2d);
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
	
	//public void tekenSlider(Graphics g)
	public void tekenSlider(Context2d g)
	{	
		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		
		if (showLine)
		{	//g.drawLine(5, 5, lengte + 5, 5);
			g.beginPath();
			g.moveTo(xPos + 5, yPos + 5);
			g.lineTo(xPos + lengte + 5, yPos + 5);
			g.stroke();
	
		}
		
		if (linksEnabled)
		{
			//g.setColor(knopLinksColor);
			g.setFillStyle(knopLinksColor);
			
			//g.fillOval(5 + standLinks - 3, 2, 6, 6);
			g.beginPath();
            g.arc(xPos + 5 + standLinks, yPos + 5, 3, 0, 2 * Math.PI);
       	 	g.fill();
			
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			//g.drawOval(5 + standLinks - 3, 2, 6, 6);
			g.beginPath();
            g.arc(xPos + 5 + standLinks, yPos + 5, 3, 0, 2 * Math.PI);
       	 	g.stroke();
		}
		
		if (rechtsEnabled)
		{
			//g.setColor(knopRechtsColor);
			g.setFillStyle(knopRechtsColor);
			
			//g.fillOval(5 + standRechts - 3, 2, 6, 6);
			g.beginPath();
            g.arc(xPos + 5 + standRechts, yPos + 5, 3, 0, 2 * Math.PI);
       	 	g.fill();
			
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			//g.drawOval(5 + standRechts - 3, 2, 6, 6);
			g.beginPath();
            g.arc(xPos + 5 + standRechts, yPos + 5, 3, 0, 2 * Math.PI);
       	 	g.stroke();
		}	
	
		/*g.drawRect(5,7,lengte,6);
		schuifKnop = new Polygon();
		schuifKnop.addPoint(5+stand,0);
		schuifKnop.addPoint(5+stand+3,5);
		schuifKnop.addPoint(5+stand+3,15);
		schuifKnop.addPoint(5+stand,20);
		schuifKnop.addPoint(5+stand-3,15);
		schuifKnop.addPoint(5+stand-3,5);
		g.fillPolygon(schuifKnop);
		g.drawPolygon(schuifKnop);*/
	
	}
	
//	public void addActionListener(ActionListener l) 
// 	{	actionListener = AWTEventMulticaster.add(actionListener,l);
// 	}
 	
// 	public void removeActionListener(ActionListener l)
// 	{	actionListener = AWTEventMulticaster.remove(actionListener, l);
// 	}
	
	public int geefStandLinks()
	{	return standLinks;
	}

	public int geefStandRechts()
	{	return standRechts;
	}
	
	public void zetStandLinks(int std)
	{	if (std > maximumLinks)
			standLinks = maximumLinks;
		else if (std < minimumLinks)
			standLinks = minimumLinks;
		else 
			standLinks = std;
			
		minimumRechts = standLinks + pixDis;	
		
		paint();
		
	}

	public void zetStandRechts(int std)
	{	if (std > maximumRechts)
			standRechts = maximumRechts;
		else if (std < minimumRechts)
			standRechts = minimumRechts;
		else 
			standRechts = std;
			
		maximumLinks = standRechts - pixDis;					
		
		paint();
	}
	
	public void zetMaximumLinks(int max)
	{	maximumLinks = max;
	}

	public void zetMinimumRechts(int min)
	{	minimumRechts = min - 2;
	}
	
	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		raakLinks = linksEnabled && 
					(new Rectangle(xPos + standLinks - 3, yPos, 16, 13)).contains(eventX, eventY);
		
		//if (raakLinks && actionListener != null)
		//{	actionListener.actionPerformed(new ActionEvent(this, 0, "startLinks"));
		//}
		
		raakRechts = rechtsEnabled && 
					 (new Rectangle(xPos + standRechts - 3, yPos, 16, 13)).contains(eventX, eventY);
		
		//if (raakRechts && actionListener != null)
		//{	actionListener.actionPerformed(new ActionEvent(this, 0, "startRechts"));
		//}
		
		muisStartX = eventX;
//		muisStartY = e.getY();
		
	}
	
	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		if (!raakLinks && linksEnabled && 
			(new Rectangle(xPos + standLinks - 3, yPos, 16, 13)).contains(eventX, eventY))
		{	raakLinks = true;
			muisStartX = eventX;
			
			//if (raakLinks && actionListener != null)
			//{	actionListener.actionPerformed(new ActionEvent(this, 0, "startLinks"));
			//}
			
			//owner.lowerGrensLinksLabels = true;
			//owner.lowerGrensRechtsLabels = true;
	
			//owner.paint();
		}
		
		if (!raakRechts && rechtsEnabled && 
			(new Rectangle(xPos + standRechts - 3, yPos, 16, 13)).contains(eventX, eventY))
		{	raakRechts = true;
			muisStartX = eventX;
			
			//if (raakRechts && actionListener != null)
			//{	actionListener.actionPerformed(new ActionEvent(this, 0, "startRechts"));
			//}
			//owner.lowerGrensLinksLabels = true;
			//owner.lowerGrensRechtsLabels = true;
	
			//owner.paint();
		}
		
		if (raakLinks)
		{	int x = eventX;
			int dx = x - muisStartX;
			standLinks = standLinks + dx;
			if (standLinks > maximumLinks) 
			{	standLinks = maximumLinks;
			}
			else if (standLinks < minimumLinks) 
			{	standLinks = minimumLinks;
			}
			minimumRechts = standLinks + pixDis;
			
			if (x < 5 || x > lengte + 20)
			{	raakLinks = false;
			}
			
			//owner.paint();
			
			owner.processTweeGrenzenSlider(true);
			
			//if (actionListener != null)
 			//{	actionListener.actionPerformed(new ActionEvent(this, 0, "verschovenLinks"));
 			//}
			
			muisStartX = x;
		}

		if (raakRechts)
		{	int x = eventX;
			int dx = x - muisStartX;
			standRechts = standRechts + dx;
			if (standRechts > maximumRechts) 
			{	standRechts = maximumRechts;
			}
			else if (standRechts < minimumRechts) 
			{	standRechts = minimumRechts;
			}
			
			maximumLinks = standRechts - pixDis;		
			
			if (x < 5 || x > lengte + 20)
			{	raakRechts = false;
			}
			
			//paint();
			
			owner.processTweeGrenzenSlider(false);
			
			//if (actionListener != null)
 			//{	actionListener.actionPerformed(new ActionEvent(this, 0, "verschovenRechts"));
 			//}
			muisStartX = x;
		}
	}
	
	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction()
	{	//if (actionListener != null)
		//{	actionListener.actionPerformed( new ActionEvent(this, 0, "stop") );
		//}
		
		//owner.lowerGrensLinksLabels = false;
		//owner.lowerGrensRechtsLabels = false;

		//owner.paint();
		
	}
//	public void mouseClicked(MouseEvent e)
//	{}
//	public void mouseExited(MouseEvent e)
//	{}
//	public void mouseEntered(MouseEvent e)
//	{}
//	public void mouseMoved(MouseEvent e)
//	{}
}
