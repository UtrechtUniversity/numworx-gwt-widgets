package fi.algebraexprgwt.client;

//import java.awt.*;
//import java.awt.event.*;

//import fi.beans.tooltip.ToolTipIF;
//import fi.beans.tooltip.ToolTipManager;

//import javax.swing.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


public class ZoomKnop //extends JComponent  
					  //implements ToolTipIF, MouseListener	
{	//	private Image im;
//	private Graphics gIm;	
	Context2d gIm;
	
	protected String code;
	
	//private Font defaultfont = new Font("SansSerif", Font.PLAIN, 16);	//private FontMetrics fm;
	String fontString = "10px, sans-serif";
	
	//protected Color bgColor = new Color(210,210,210);		
	// protected Color fgColor = Color.black;
	
	protected CssColor bgColor = CssColor.make(210,210,210);
	protected CssColor bgColorDarker = CssColor.make(170,170,170);
	protected CssColor bgColorBrighter = CssColor.make(250,250,250);
	protected CssColor fgColor = CssColor.make(0,0,0);
	protected boolean focus = false;
	protected boolean actief = false;
	
	private String toolTip = "";
	int xPos,yPos,breedte,hoogte;
	
	boolean visible = true;
				//public ZoomKnop(String s)
	public ZoomKnop(String s, int x, int y, int b, int h, Context2d c2d)
	{	code = s;
	
		xPos = x; yPos = y; breedte = b; hoogte = h;
	
		gIm = c2d;

		//addMouseListener(this);		//setFont(defaultfont);
		//fm = this.getFontMetrics(defaultfont);
		gIm.setFont(fontString);	}

	public void translate(int dx, int dy)
	{
		xPos += dx;
		yPos += dy;
	}

/*	
	public void setToolTip(String toolTip)
	{	this.toolTip = toolTip;
       	ToolTipManager.registerComponent(this);
	}
*/	
	public String getToolTip() 
	{	return toolTip;
    }
    
/*	
    public Component getComponent() 
	{	return this;
    }
*/    
	
		
	public void zetActief(boolean b)
	{	actief = b;
		paint();
	}			public void setBackground(CssColor c)
	{		}
	
	public void paint()
	{
		paint(gIm);
	}
	
		//public void paint(Graphics g)
	public void paint(Context2d g)
	{	
		//if(im==null)
		//{	im = createImage(getSize().width,getSize().height);
  		//	gIm = im.getGraphics();
		//}
		//	gIm.setColor(getBackground());
		//	gIm.fillRect(0,0,getSize().width,getSize().height);
			paintBuffer(gIm);
		//	g.drawImage(im, 0, 0, null);
  		
	}
/*	
	public void update(Graphics g)
	{	paint(g);
	}*/	
	//public void paintBuffer(Graphics g)
	public void paintBuffer(Context2d g)
	{			//g.setColor(fgColor);
			
/*		
			g.setColor(bgColor);
			g.fillRect(0,0,getSize().width,getSize().height);			if(focus)
			{	if(actief)
					g.setColor(bgColor.darker());				else 
					g.setColor(bgColor.brighter());
				g.drawLine(0,0,getSize().width-1,0);				g.drawLine(0,0,0,getSize().height-1);
				if(actief)
					g.setColor(bgColor.brighter());				else 
					g.setColor(bgColor.darker());
				g.drawLine(getSize().width-1,0,getSize().width-1,getSize().height-1);
				g.drawLine(0,getSize().height-1,getSize().width-1,getSize().height-1);
			}
*/
		
		if (!visible)
			return;
		
		int xPos = this.xPos;
		if (code.equals("zoomintabel") || code.equals("zoomuittabel"))
			xPos += 2;
		
		
		g.setFillStyle(bgColor);
		gIm.fillRect(xPos+0,yPos+0,breedte,hoogte);
		if(actief)
		{	g.setStrokeStyle(bgColorDarker);
		}
		else 
		{	g.setStrokeStyle(bgColorBrighter);
		}
		g.beginPath();
		g.moveTo(xPos+0,yPos+0);
		g.lineTo(xPos+breedte-1,yPos+0);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+0,yPos+0);
		g.lineTo(xPos+0,yPos+hoogte-1);
		g.stroke();
		
		if(actief)
		{	g.setStrokeStyle(bgColorBrighter);
		}
		else 
		{	g.setStrokeStyle(bgColorDarker);
		}
		g.beginPath();
		g.moveTo(xPos+breedte-1,yPos+0);
		g.lineTo(xPos+breedte-1,yPos+hoogte-1);
		g.stroke();

		//g.drawLine(0,getSize().height-1,getSize().width-1,getSize().height-1);
		g.beginPath();
		g.moveTo(xPos+0,yPos+hoogte-1);
		g.lineTo(xPos+breedte-1,yPos+hoogte-1);
		g.stroke();
				//		int b = getSize().width;
//		int h = getSize().height;		//g.setColor(Color.black);
		g.setStrokeStyle(CssColor.make(0,0,0));
		if(code.equals("zoominx"))		{	
/*			
			g.drawOval(8,3,8,8);
			g.drawLine(12,5,12,9);
			g.drawLine(10,7,14,7);
			
			g.drawLine(8,18,17,18);
			g.drawLine(8,18,10,16);
			g.drawLine(8,18,10,20);
			g.drawLine(17,18,15,16);
			g.drawLine(17,18,15,20);
*/
			//g.drawOval(8,3,8,8);
			g.beginPath();
			g.arc(xPos+12, yPos+7, 4, 0, 2*Math.PI);
			g.stroke();
			//g.drawLine(12,5,12,9);
			g.beginPath();
			g.moveTo(xPos+12,yPos+5);
			g.lineTo(xPos+12,yPos+9);
			g.stroke();

			//g.drawLine(10,7,14,7);
			g.beginPath();
			g.moveTo(xPos+10,yPos+7);
			g.lineTo(xPos+14,yPos+7);
			g.stroke();
			
			//g.drawLine(8,18,17,18);
			g.beginPath();
			g.moveTo(xPos+8,yPos+18);
			g.lineTo(xPos+17,yPos+18);
			g.stroke();

			//g.drawLine(8,18,10,16);
			g.beginPath();
			g.moveTo(xPos+8,yPos+18);
			g.lineTo(xPos+10,yPos+16);
			g.stroke();

			//g.drawLine(8,18,10,20);
			g.beginPath();
			g.moveTo(xPos+8,yPos+18);
			g.lineTo(xPos+10,yPos+20);
			g.stroke();

			//g.drawLine(17,18,15,16);
			g.beginPath();
			g.moveTo(xPos+17,yPos+18);
			g.lineTo(xPos+15,yPos+16);
			g.stroke();

			//g.drawLine(17,18,15,20);
			g.beginPath();
			g.moveTo(xPos+17,yPos+18);
			g.lineTo(xPos+15,yPos+20);
			g.stroke();

			
		}		else if(code.equals("zoomuitx"))		{	
/*			
			g.drawOval(8,3,8,8);
			g.drawLine(10,7,14,7);
			
			g.drawLine(8,18,17,18);
			g.drawLine(8,18,10,16);
			g.drawLine(8,18,10,20);
			g.drawLine(17,18,15,16);
			g.drawLine(17,18,15,20);
*/			
			//g.drawOval(8,3,8,8);
			g.beginPath();
			g.arc(xPos+12, yPos+7, 4, 0, 2*Math.PI);
			g.stroke();
			
			//g.drawLine(10,7,14,7);
			g.beginPath();
			g.moveTo(xPos+10,yPos+7);
			g.lineTo(xPos+14,yPos+7);
			g.stroke();
			
			//g.drawLine(8,18,17,18);
			g.beginPath();
			g.moveTo(xPos+8,yPos+18);
			g.lineTo(xPos+17,yPos+18);
			g.stroke();

			//g.drawLine(8,18,10,16);
			g.beginPath();
			g.moveTo(xPos+8,yPos+18);
			g.lineTo(xPos+10,yPos+16);
			g.stroke();

			//g.drawLine(8,18,10,20);
			g.beginPath();
			g.moveTo(xPos+8,yPos+18);
			g.lineTo(xPos+10,yPos+20);
			g.stroke();

			//g.drawLine(17,18,15,16);
			g.beginPath();
			g.moveTo(xPos+17,yPos+18);
			g.lineTo(xPos+15,yPos+16);
			g.stroke();

			//g.drawLine(17,18,15,20);
			g.beginPath();
			g.moveTo(xPos+17,yPos+18);
			g.lineTo(xPos+15,yPos+20);
			g.stroke();

			
		}		else if(code.equals("zoominy"))		{	
/*			
			g.drawOval(8,3,8,8);
			g.drawLine(12,5,12,9);
			g.drawLine(10,7,14,7);
			
			g.drawLine(12,14,12,22);
			g.drawLine(12,14,10,16);
			g.drawLine(12,14,14,16);
			g.drawLine(12,22,10,20);
			g.drawLine(12,22,14,20);
*/
			//g.drawOval(8,3,8,8);
			g.beginPath();
			g.arc(xPos+12, yPos+7, 4, 0, 2*Math.PI);
			g.stroke();
			
			//g.drawLine(12,5,12,9);
			g.beginPath();
			g.moveTo(xPos+12,yPos+5);
			g.lineTo(xPos+12,yPos+9);
			g.stroke();

			//g.drawLine(10,7,14,7);
			g.beginPath();
			g.moveTo(xPos+10,yPos+7);
			g.lineTo(xPos+14,yPos+7);
			g.stroke();
			
			//g.drawLine(12,14,12,22);
			g.beginPath();
			g.moveTo(xPos+12,yPos+14);
			g.lineTo(xPos+12,yPos+22);
			g.stroke();

			//g.drawLine(12,14,10,16);
			g.beginPath();
			g.moveTo(xPos+12,yPos+14);
			g.lineTo(xPos+10,yPos+16);
			g.stroke();

			//g.drawLine(12,14,14,16);
			g.beginPath();
			g.moveTo(xPos+12,yPos+14);
			g.lineTo(xPos+14,yPos+16);
			g.stroke();

			//g.drawLine(12,22,10,20);
			g.beginPath();
			g.moveTo(xPos+12,yPos+22);
			g.lineTo(xPos+10,yPos+20);
			g.stroke();

			//g.drawLine(12,22,14,20);
			g.beginPath();
			g.moveTo(xPos+12,yPos+22);
			g.lineTo(xPos+14,yPos+20);
			g.stroke();

		}
		else if(code.equals("zoominysmal"))
		{	
/*			
			g.drawLine(4,3,1,6);
			g.drawLine(4,3,7,6);
			g.drawLine(4,4,1,7);
			g.drawLine(4,4,7,7);
			g.drawLine(4,3,4,10);
			g.drawLine(4,15,4,22);
			g.drawLine(1,19,4,22);
			g.drawLine(7,19,4,22);
			g.drawLine(1,18,4,21);
			g.drawLine(7,18,4,21);
*/
			//g.drawLine(4,3,1,6);
			g.beginPath();
			g.moveTo(xPos+4,yPos+3);
			g.lineTo(xPos+1,yPos+6);
			g.stroke();

			//g.drawLine(4,3,7,6);
			g.beginPath();
			g.moveTo(xPos+4,yPos+3);
			g.lineTo(xPos+7,yPos+6);
			g.stroke();

			//g.drawLine(4,4,1,7);
			g.beginPath();
			g.moveTo(xPos+4,yPos+4);
			g.lineTo(xPos+1,yPos+7);
			g.stroke();

			//g.drawLine(4,4,7,7);
			g.beginPath();
			g.moveTo(xPos+4,yPos+4);
			g.lineTo(xPos+7,yPos+7);
			g.stroke();

			//g.drawLine(4,3,4,10);
			g.beginPath();
			g.moveTo(xPos+4,yPos+3);
			g.lineTo(xPos+4,yPos+10);
			g.stroke();

			//g.drawLine(4,15,4,22);
			g.beginPath();
			g.moveTo(xPos+4,yPos+15);
			g.lineTo(xPos+4,yPos+22);
			g.stroke();

			//g.drawLine(1,19,4,22);
			g.beginPath();
			g.moveTo(xPos+1,yPos+19);
			g.lineTo(xPos+4,yPos+22);
			g.stroke();

			//g.drawLine(7,19,4,22);
			g.beginPath();
			g.moveTo(xPos+7,yPos+19);
			g.lineTo(xPos+4,yPos+22);
			g.stroke();

			//g.drawLine(1,18,4,21);
			g.beginPath();
			g.moveTo(xPos+1,yPos+18);
			g.lineTo(xPos+4,yPos+21);
			g.stroke();

			//g.drawLine(7,18,4,21);
			g.beginPath();
			g.moveTo(xPos+7,yPos+18);
			g.lineTo(xPos+4,yPos+21);
			g.stroke();

		}		else if(code.equals("zoomuity"))		{	
/*			
			g.drawOval(8,3,8,8);
			g.drawLine(10,7,14,7);
			
			g.drawLine(12,14,12,22);
			g.drawLine(12,14,10,16);
			g.drawLine(12,14,14,16);
			g.drawLine(12,22,10,20);
			g.drawLine(12,22,14,20);
*/
			//g.drawOval(8,3,8,8);
			g.beginPath();
			g.arc(xPos+12, yPos+7, 4, 0, 2*Math.PI);
			g.stroke();
			
			//g.drawLine(10,7,14,7);
			g.beginPath();
			g.moveTo(xPos+10,yPos+7);
			g.lineTo(xPos+14,yPos+7);
			g.stroke();

			//g.drawLine(12,14,12,22);
			g.beginPath();
			g.moveTo(xPos+12,yPos+14);
			g.lineTo(xPos+12,yPos+22);
			g.stroke();

			//g.drawLine(12,14,10,16);
			g.beginPath();
			g.moveTo(xPos+12,yPos+14);
			g.lineTo(xPos+10,yPos+16);
			g.stroke();

			//g.drawLine(12,14,14,16);
			g.beginPath();
			g.moveTo(xPos+12,yPos+14);
			g.lineTo(xPos+14,yPos+16);
			g.stroke();

			//g.drawLine(12,22,10,20);
			g.beginPath();
			g.moveTo(xPos+12,yPos+22);
			g.lineTo(xPos+10,yPos+20);
			g.stroke();

			//g.drawLine(12,22,14,20);
			g.beginPath();
			g.moveTo(xPos+12,yPos+22);
			g.lineTo(xPos+14,yPos+20);
			g.stroke();

			
		}
		else if(code.equals("zoomuitysmal"))
		{	
/*			
			g.drawLine(1,7,4,10);
			g.drawLine(7,7,4,10);
			g.drawLine(1,6,4,9);
			g.drawLine(7,6,4,9);
			g.drawLine(4,3,4,10);
			g.drawLine(4,15,4,22);
			g.drawLine(1,19,4,15);
			g.drawLine(7,19,4,15);
			g.drawLine(1,18,4,18);
			g.drawLine(7,18,4,18);
*/
			//g.drawLine(1,7,4,10);
			g.beginPath();
			g.moveTo(xPos+1,yPos+7);
			g.lineTo(xPos+4,yPos+10);
			g.stroke();

			//g.drawLine(7,7,4,10);
			g.beginPath();
			g.moveTo(xPos+7,yPos+7);
			g.lineTo(xPos+4,yPos+10);
			g.stroke();

			//g.drawLine(1,6,4,9);
			g.beginPath();
			g.moveTo(xPos+1,yPos+6);
			g.lineTo(xPos+4,yPos+9);
			g.stroke();

			//g.drawLine(7,6,4,9);
			g.beginPath();
			g.moveTo(xPos+7,yPos+6);
			g.lineTo(xPos+4,yPos+9);
			g.stroke();

			//g.drawLine(4,3,4,10);
			g.beginPath();
			g.moveTo(xPos+4,yPos+3);
			g.lineTo(xPos+4,yPos+10);
			g.stroke();

			//g.drawLine(4,15,4,22);
			g.beginPath();
			g.moveTo(xPos+4,yPos+15);
			g.lineTo(xPos+4,yPos+22);
			g.stroke();

			//g.drawLine(1,19,4,15);
			g.beginPath();
			g.moveTo(xPos+1,yPos+19);
			g.lineTo(xPos+4,yPos+15);
			g.stroke();

			//g.drawLine(7,19,4,15);
			g.beginPath();
			g.moveTo(xPos+7,yPos+19);
			g.lineTo(xPos+4,yPos+15);
			g.stroke();

			//g.drawLine(1,18,4,18);
			g.beginPath();
			g.moveTo(xPos+1,yPos+18);
			g.lineTo(xPos+4,yPos+18);
			g.stroke();

			//g.drawLine(7,18,4,18);
			g.beginPath();
			g.moveTo(xPos+7,yPos+18);
			g.lineTo(xPos+4,yPos+18);
			g.stroke();

		}
		else if(code.equals("zoominxsmal"))
		{	
/*			
			g.drawOval(1,3,8,8);
			g.drawLine(5,5,5,9);
			g.drawLine(3,7,7,7);
			
			g.drawLine(1,18,10,18);
			g.drawLine(1,18,3,16);
			g.drawLine(1,18,3,20);
			g.drawLine(10,18,8,16);
			g.drawLine(10,18,8,20);
*/
			
			//g.drawOval(1,3,8,8);
			g.beginPath();
			g.arc(xPos+5, yPos+7, 4, 0, 2*Math.PI);
			g.stroke();
			
			//g.drawLine(5,5,5,9);
			g.beginPath();
			g.moveTo(xPos+5,yPos+5);
			g.lineTo(xPos+5,yPos+9);
			g.stroke();

			//g.drawLine(3,7,7,7);
			g.beginPath();
			g.moveTo(xPos+3,yPos+7);
			g.lineTo(xPos+7,yPos+7);
			g.stroke();

			//g.drawLine(1,18,10,18);
			g.beginPath();
			g.moveTo(xPos+1,yPos+18);
			g.lineTo(xPos+10,yPos+18);
			g.stroke();

			//g.drawLine(1,18,3,16);
			g.beginPath();
			g.moveTo(xPos+1,yPos+18);
			g.lineTo(xPos+3,yPos+16);
			g.stroke();

			//g.drawLine(1,18,3,20);
			g.beginPath();
			g.moveTo(xPos+1,yPos+18);
			g.lineTo(xPos+3,yPos+20);
			g.stroke();

			//g.drawLine(10,18,8,16);
			g.beginPath();
			g.moveTo(xPos+10,yPos+18);
			g.lineTo(xPos+8,yPos+16);
			g.stroke();

			//g.drawLine(10,18,8,20);
			g.beginPath();
			g.moveTo(xPos+10,yPos+18);
			g.lineTo(xPos+8,yPos+20);
			g.stroke();

			
		}
		else if(code.equals("zoomuitxsmal"))
		{	
/*			
			g.drawOval(1,3,8,8);
			g.drawLine(3,7,7,7);
			
			g.drawLine(1,18,10,18);
			g.drawLine(1,18,3,16);
			g.drawLine(1,18,3,20);
			g.drawLine(10,18,8,16);
			g.drawLine(10,18,8,20);
*/
			
			//g.drawOval(1,3,8,8);
			g.beginPath();
			g.arc(xPos+5, yPos+7, 4, 0, 2*Math.PI);
			g.stroke();
			
			//g.drawLine(3,7,7,7);
			g.beginPath();
			g.moveTo(xPos+3,yPos+7);
			g.lineTo(xPos+7,yPos+7);
			g.stroke();
		
			//g.drawLine(1,18,10,18);
			g.beginPath();
			g.moveTo(xPos+1,yPos+18);
			g.lineTo(xPos+10,yPos+18);
			g.stroke();

			//g.drawLine(1,18,3,16);
			g.beginPath();
			g.moveTo(xPos+1,yPos+18);
			g.lineTo(xPos+3,yPos+16);
			g.stroke();

			//g.drawLine(1,18,3,20);
			g.beginPath();
			g.moveTo(xPos+1,yPos+18);
			g.lineTo(xPos+3,yPos+20);
			g.stroke();

			//g.drawLine(10,18,8,16);
			g.beginPath();
			g.moveTo(xPos+10,yPos+18);
			g.lineTo(xPos+8,yPos+16);
			g.stroke();

			//g.drawLine(10,18,8,20);
			g.beginPath();
			g.moveTo(xPos+10,yPos+18);
			g.lineTo(xPos+8,yPos+20);
			g.stroke();

			
			
		}		else if(code.equals("zoomin"))		{	
/*			
			g.drawOval(4,4,10,10);
			g.drawLine(6,9,12,9);
			g.drawLine(9,6,9,12);
			//g.drawLine(14,14,20,20);
			//g.drawLine(15,14,21,20);
			
			g.drawLine(5,19,15,19);
			g.drawLine(5,19,7,17);
			g.drawLine(5,19,7,21);
			g.drawLine(15,19,13,17);
			g.drawLine(15,19,13,21);
			
			g.drawLine(19,5,19,15);
			g.drawLine(19,5,17,7);
			g.drawLine(19,5,21,7);
			g.drawLine(19,15,17,13);
			g.drawLine(19,15,21,13);
*/
			
			//g.drawOval(4,4,10,10);
			g.beginPath();
			g.arc(xPos+9, yPos+9, 5, 0, 2*Math.PI);
			g.stroke();
			
			//g.drawLine(6,9,12,9);
			g.beginPath();
			g.moveTo(xPos+6,yPos+9);
			g.lineTo(xPos+12,yPos+9);
			g.stroke();

			//g.drawLine(9,6,9,12);
			g.beginPath();
			g.moveTo(xPos+9,yPos+6);
			g.lineTo(xPos+9,yPos+12);
			g.stroke();

			//g.drawLine(5,19,15,19);
			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+15,yPos+19);
			g.stroke();

			//g.drawLine(5,19,7,17);
			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+7,yPos+17);
			g.stroke();

			//g.drawLine(5,19,7,21);
			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+7,yPos+21);
			g.stroke();

			//g.drawLine(15,19,13,17);
			g.beginPath();
			g.moveTo(xPos+15,yPos+19);
			g.lineTo(xPos+13,yPos+17);
			g.stroke();

			//g.drawLine(15,19,13,21);
			g.beginPath();
			g.moveTo(xPos+15,yPos+19);
			g.lineTo(xPos+13,yPos+21);
			g.stroke();

			//g.drawLine(19,5,19,15);
			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+19,yPos+15);
			g.stroke();

			//g.drawLine(19,5,17,7);
			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+17,yPos+7);
			g.stroke();

			//g.drawLine(19,5,21,7);
			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+21,yPos+7);
			g.stroke();

			//g.drawLine(19,15,17,13);
			g.beginPath();
			g.moveTo(xPos+19,yPos+15);
			g.lineTo(xPos+17,yPos+13);
			g.stroke();

			//g.drawLine(19,15,21,13);
			g.beginPath();
			g.moveTo(xPos+19,yPos+15);
			g.lineTo(xPos+21,yPos+13);
			g.stroke();
			
		}		else if(code.equals("zoomuit"))		{	
/*			
			g.drawOval(4,4,10,10);
			g.drawLine(6,9,12,9);
			
			g.drawLine(5,19,15,19);
			g.drawLine(5,19,7,17);
			g.drawLine(5,19,7,21);
			g.drawLine(15,19,13,17);
			g.drawLine(15,19,13,21);
			
			g.drawLine(19,5,19,15);
			g.drawLine(19,5,17,7);
			g.drawLine(19,5,21,7);
			g.drawLine(19,15,17,13);
			g.drawLine(19,15,21,13);
*/
			
			g.beginPath();
			g.arc(xPos+9, yPos+9, 5, 0, 2*Math.PI);
			g.stroke();

			//g.drawLine(6,9,12,9);
			g.beginPath();
			g.moveTo(xPos+6,yPos+9);
			g.lineTo(xPos+12,yPos+9);
			g.stroke();
			
			//g.drawLine(5,19,15,19);
			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+15,yPos+19);
			g.stroke();

			//g.drawLine(5,19,7,17);
			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+7,yPos+17);
			g.stroke();

			//g.drawLine(5,19,7,21);
			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+7,yPos+21);
			g.stroke();

			//g.drawLine(15,19,13,17);
			g.beginPath();
			g.moveTo(xPos+15,yPos+19);
			g.lineTo(xPos+13,yPos+17);
			g.stroke();

			//g.drawLine(15,19,13,21);
			g.beginPath();
			g.moveTo(xPos+15,yPos+19);
			g.lineTo(xPos+13,yPos+21);
			g.stroke();

			
			//g.drawLine(19,5,19,15);
			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+19,yPos+15);
			g.stroke();

			//g.drawLine(19,5,17,7);
			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+17,yPos+7);
			g.stroke();

			//g.drawLine(19,5,21,7);
			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+21,yPos+7);
			g.stroke();

			//g.drawLine(19,15,17,13);
			g.beginPath();
			g.moveTo(xPos+19,yPos+15);
			g.lineTo(xPos+17,yPos+13);
			g.stroke();

			//g.drawLine(19,15,21,13);
			g.beginPath();
			g.moveTo(xPos+19,yPos+15);
			g.lineTo(xPos+21,yPos+13);
			g.stroke();

		}		else if(code.equals("standaard"))		{	//g.drawLine(4,12,20,12);
			//g.drawLine(12,4,12,20);
			
			g.beginPath();
			g.moveTo(xPos+4,yPos+12);
			g.lineTo(xPos+20,yPos+12);
			g.stroke();

			g.beginPath();
			g.moveTo(xPos+12,yPos+4);
			g.lineTo(xPos+12,yPos+20);
			g.stroke();

			
		}
		
		else if(code.equals("zoomintabel"))
		{	
			
//System.out.println("zoomintabel paint");

			//g.drawOval(4,4,10,10);
			g.beginPath();
			g.arc(xPos+5, yPos+5, 5, 0, 2*Math.PI);
			g.stroke();
			
			//g.drawLine(6,9,12,9);
			g.beginPath();
			g.moveTo(xPos+2,yPos+5);
			g.lineTo(xPos+8,yPos+5);
			g.stroke();

			//g.drawLine(9,6,9,12);
			g.beginPath();
			g.moveTo(xPos+5,yPos+2);
			g.lineTo(xPos+5,yPos+8);
			g.stroke();

/*			
			//g.drawLine(5,19,15,19);
			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+15,yPos+19);
			g.stroke();

			//g.drawLine(5,19,7,17);
			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+7,yPos+17);
			g.stroke();

			//g.drawLine(5,19,7,21);
			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+7,yPos+21);
			g.stroke();

			//g.drawLine(15,19,13,17);
			g.beginPath();
			g.moveTo(xPos+15,yPos+19);
			g.lineTo(xPos+13,yPos+17);
			g.stroke();

			//g.drawLine(15,19,13,21);
			g.beginPath();
			g.moveTo(xPos+15,yPos+19);
			g.lineTo(xPos+13,yPos+21);
			g.stroke();

			//g.drawLine(19,5,19,15);
			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+19,yPos+15);
			g.stroke();

			//g.drawLine(19,5,17,7);
			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+17,yPos+7);
			g.stroke();

			//g.drawLine(19,5,21,7);
			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+21,yPos+7);
			g.stroke();

			//g.drawLine(19,15,17,13);
			g.beginPath();
			g.moveTo(xPos+19,yPos+15);
			g.lineTo(xPos+17,yPos+13);
			g.stroke();

			//g.drawLine(19,15,21,13);
			g.beginPath();
			g.moveTo(xPos+19,yPos+15);
			g.lineTo(xPos+21,yPos+13);
			g.stroke();
*/
		}
		else if(code.equals("zoomuittabel"))
		{	
			//g.drawOval(4,4,10,10);
			g.beginPath();
			g.arc(xPos+5, yPos+5, 5, 0, 2*Math.PI);
			g.stroke();

			//g.drawLine(6,9,12,9);
			g.beginPath();
			g.moveTo(xPos+2,yPos+5);
			g.lineTo(xPos+8,yPos+5);
			g.stroke();
			
/*			
			//g.drawLine(5,19,15,19);
			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+15,yPos+19);
			g.stroke();

			//g.drawLine(5,19,7,17);
			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+7,yPos+17);
			g.stroke();

			//g.drawLine(5,19,7,21);
			g.beginPath();
			g.moveTo(xPos+5,yPos+19);
			g.lineTo(xPos+7,yPos+21);
			g.stroke();

			//g.drawLine(15,19,13,17);
			g.beginPath();
			g.moveTo(xPos+15,yPos+19);
			g.lineTo(xPos+13,yPos+17);
			g.stroke();

			//g.drawLine(15,19,13,21);
			g.beginPath();
			g.moveTo(xPos+15,yPos+19);
			g.lineTo(xPos+13,yPos+21);
			g.stroke();

			
			//g.drawLine(19,5,19,15);
			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+19,yPos+15);
			g.stroke();

			//g.drawLine(19,5,17,7);
			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+17,yPos+7);
			g.stroke();

			//g.drawLine(19,5,21,7);
			g.beginPath();
			g.moveTo(xPos+19,yPos+5);
			g.lineTo(xPos+21,yPos+7);
			g.stroke();

			//g.drawLine(19,15,17,13);
			g.beginPath();
			g.moveTo(xPos+19,yPos+15);
			g.lineTo(xPos+17,yPos+13);
			g.stroke();

			//g.drawLine(19,15,21,13);
			g.beginPath();
			g.moveTo(xPos+19,yPos+15);
			g.lineTo(xPos+21,yPos+13);
			g.stroke();
*/
		}

	}
/*	
	public void mousePressed(MouseEvent e)	{	actief = true;		repaint();
	}
*/	/*	
	public void mouseReleased(MouseEvent e) 
 	{	actief = false;		if ( isEnabled() )
 		{	produceAction("knop");
 		}		repaint();
 	}
*/ 	  
/*	
	public void mouseEntered(MouseEvent e)	{	focus = true;
		setCursor(new Cursor(Cursor.HAND_CURSOR ));
		repaint();
		produceAction("focus");	}
*/	
/*	
	public void mouseExited(MouseEvent e)
	{	focus = false;		setCursor(new Cursor(Cursor.DEFAULT_CURSOR ));
		repaint();
		produceAction("focus");	}
*/	
//	public void mouseClicked(MouseEvent e){;}
	
/*	
//	ActionProducer
	private ActionListener actionListener = null;
	
	public void addActionListener(ActionListener l) 
 	{	actionListener = AWTEventMulticaster.add(actionListener,l);
 	}
 	
 	public void removeActionListener(ActionListener l)
 	{	actionListener = AWTEventMulticaster.remove(actionListener, l);
 	}	
 	
 	public void produceAction(String command)
 	{	if (actionListener != null)
 		{	actionListener.actionPerformed( new ActionEvent(this, 0, command) );
 		}
 	}
 	//
*/ 
 	
}