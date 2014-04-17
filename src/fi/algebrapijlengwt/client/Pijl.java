package fi.algebrapijlengwt.client;

//import javax.swing.*;

//import java.awt.*;
//import java.awt.event.*;
//import fi.algebrapijlenopdr.schuifobjects.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

//import com.google.gwt.event.dom.client.MouseDownEvent;
//import com.google.gwt.event.dom.client.MouseDownHandler;
//import com.google.gwt.event.dom.client.MouseMoveEvent;
//import com.google.gwt.event.dom.client.MouseMoveHandler;
//import com.google.gwt.event.dom.client.MouseUpEvent;
//import com.google.gwt.event.dom.client.MouseUpHandler;

//import com.google.gwt.dom.client.Touch;
//import com.google.gwt.event.dom.client.TouchMoveHandler;
//import com.google.gwt.event.dom.client.TouchStartHandler;
//import com.google.gwt.event.dom.client.TouchEndHandler;

//import com.google.gwt.event.dom.client.TouchEndEvent;
//import com.google.gwt.event.dom.client.TouchMoveEvent;
//import com.google.gwt.event.dom.client.TouchStartEvent;


public class Pijl //extends JComponent 
				  //implements MouseListener, MouseMotionListener
{	
	
	double nZero = 1e-3d;
	
	int xPos, yPos, breedte, hoogte;
		
	int x0,y0,x1,y1;
	
	AlgebraSchuifVeld asv;
	
	AlgebraSchuifComponent zender, ontvanger;
	
	Polygon pijlpuntBegin, pijlpuntEind;
	Polygon pijlpuntKlik;
	private int laatstex = 0;
	private int laatstey = 0;
	boolean actief;
	boolean vast;
	private boolean isStapel;
	private boolean links = false;
	
	CssColor black = CssColor.make(0,0,0);
	CssColor gray = CssColor.make(128,128,128);
	CssColor red = CssColor.make(255,0,0);
	
	private CssColor color = CssColor.make(0,0,0);
	
	//Image im;
	
	Context2d pijlContext2d;
	
	boolean mouseDown = false;
	
	boolean visible = true;
	
	public Pijl(AlgebraSchuifVeld asv)
	{	this.asv = asv;
		actief = false;
		vast = false;
		isStapel = false;
		//setBounds(0, 0, schuifveld.getSize().width, schuifveld.getSize().height);
		xPos = 0;
		yPos = 0;
		breedte = asv.breedte;
		hoogte = asv.hoogte;
		
		pijlContext2d = asv.asvContext2d;
		
//GWT		
		//addMouseListener(this);
		//addMouseMotionListener(this);
		
		
		pijlpuntEind = new Polygon();
		pijlpuntKlik = new Polygon();
		
		//setOpaque(false);
	}
	
	public void zetLinks(boolean b)
	{	links = b;
	}
	
	
	public void setColor(CssColor color)
	{	this.color = color;
		zender.zetVakKleur(color);
	}
	
	public CssColor getColor()
	{	return color;
	}
	
	public void setVisible(boolean b)
	{
		visible = b;
		
		asv.tekenOpnieuw();
		
	}
	
	public void paint()
	{
		paint(pijlContext2d);
	}
	
	//public void paint(Graphics gIm)
	public void paint(Context2d gIm)
  	{
		if (!visible)
			return;

		// pijl naar rechts, alleen deze kan je aan de grafiek vastmaken
		if(!links)
		{	//gIm.setColor(color);
			gIm.setStrokeStyle(color);
			double dx = x1-x0; double dy = y1-y0;
			int teken = (int)((dy/Math.abs(dy)));
			double s = Math.sqrt(dx*dx + dy*dy);
			double a;
			int r0, r1;
			double dr;
			int xc0,xc1,yc0,yc1, booghoek;
			if(Math.abs((double)dy/(double)dx)>0.04 && dx>=0)
			{	a = Math.atan((double)dx/Math.abs(dy));
				r0 = (int)(s/(4*Math.cos(a)));
				dr = s/(4*Math.cos(a)) - r0;
				if(dr>0.25)r1 = r0+1;
				else r1 = r0;
				xc0 = x0;
				yc0 = y0+r0*teken;
				xc1 = x1;
				yc1 = y1-r1*teken;
				booghoek = (int)((2*a - Math.PI)*180/Math.PI);
				//if (color == Color.black)
				if (color.toString().equals(black.toString()))
				{	//gIm.drawArc(xc0-r0, yc0-r0, 2*r0, 2*r0, teken*90, teken*(booghoek-1));
				
					//Java angles: degrees and anticlockwise from positive x-axis
					//GWT angles: radians and clockwise from positive x-axis
					// 90 Java = 270 GWT = 3pi/2, 270 Java = 90 GWT = pi/2
					
					double startAngle = teken*3*Math.PI/2;
					double deltaAngle = -teken*(booghoek-1)*Math.PI/180;
					double endAngle = startAngle + deltaAngle;
					boolean antiClockWise = false;
					if (endAngle < startAngle)
					{	antiClockWise = true;
					}
					
					gIm.beginPath();
					gIm.arc(xc0, yc0, r0, startAngle, endAngle, antiClockWise);
					gIm.stroke();
					
				//}
				//if (color == Color.black)
				//{	
					//gIm.drawArc(xc1-r1, yc1-r1, 2*r1, 2*r1, teken*270, teken*(booghoek-1));
					
					startAngle = teken*Math.PI/2;
					endAngle = startAngle + deltaAngle;
					antiClockWise = false;
					if (endAngle < startAngle)
					{	antiClockWise = true;
					}
					
					gIm.beginPath();
					gIm.arc(xc1, yc1, r1, startAngle, endAngle, antiClockWise);
					gIm.stroke();
				//}
				
				}
			}
			else if(Math.abs(dy)>1 && dx<0)
			{	r0 = (int)Math.abs(dy/4);
				dr = Math.abs(dy/4) - r0;
				if(dr>0.25)r1 = r0+1;
				else r1 = r0;
				xc0 = x0;
				yc0 = y0+r0*teken;
				xc1 = x1;
				yc1 = y1-r1*teken;
				booghoek = -180;
				//if (color == Color.black)
				if (color.toString().equals(black.toString()))
				{	//gIm.drawArc(xc0-r0, yc0-r0, 2*r0, 2*r0, teken*90, teken*(booghoek-1));
				
					double startAngle = teken*3*Math.PI/2;
					double deltaAngle = -teken*(booghoek-1)*Math.PI/180;
					double endAngle = startAngle + deltaAngle;
					boolean antiClockWise = false;
					if (endAngle < startAngle)
					{	antiClockWise = true;
					}
					
					gIm.beginPath();
					gIm.arc(xc0, yc0, r0, startAngle, endAngle, antiClockWise);
					gIm.stroke();
				
				
				//}
				//if (color == Color.black)
				//{	
					//gIm.drawArc(xc1-r1, yc1-r1, 2*r1, 2*r1, teken*270, teken*(booghoek-1));
					
					startAngle = teken*Math.PI/2;
					endAngle = startAngle + deltaAngle;
					antiClockWise = false;
					if (endAngle < startAngle)
					{	antiClockWise = true;
					}
					
					gIm.beginPath();
					gIm.arc(xc1, yc1, r1, startAngle, endAngle, antiClockWise);
					gIm.stroke();

				
				//}
				//if (color == Color.black)
				//{	
					//gIm.drawLine(x0,yc0+teken*r0,x1,yc1-teken*r1);

					gIm.beginPath();
					gIm.moveTo(x0,yc0+teken*r0);
					gIm.lineTo(x1,yc1-teken*r1);
					gIm.stroke();
					
					
				
				}
			}
			else
			{	//if (color == Color.black)
				if (color.toString().equals(black.toString()))
				{
					//gIm.drawLine(x0, y0, x1, y1);
					gIm.beginPath();
					gIm.moveTo(x0, y0);
					gIm.lineTo(x1, y1);
					gIm.stroke();
				}	
			}
			if (vast)
			{	//gIm.setColor(color);
				gIm.setStrokeStyle(color);
				gIm.setFillStyle(color);
			
			}
			else 
			{	//gIm.setColor(Color.gray);
				gIm.setStrokeStyle(gray);
				gIm.setFillStyle(gray);
				
			}
		
			pijlpuntBegin = new Polygon();
			pijlpuntBegin.addPoint(x0, y0);
			pijlpuntBegin.addPoint(x0-10, y0-7);
			pijlpuntBegin.addPoint(x0-10, y0+7);
			
			//gIm.fillPolygon(pijlpuntBegin);
        	gIm.moveTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntBegin.aantalPunten; k++)
			{	gIm.lineTo(pijlpuntBegin.doubleX[k], pijlpuntBegin.doubleY[k]);
			}
			gIm.lineTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.closePath();
			gIm.fill();


		
			pijlpuntEind = new Polygon();
			pijlpuntEind.addPoint(x1+10, y1);
			pijlpuntEind.addPoint(x1, y1-7);
			pijlpuntEind.addPoint(x1, y1+7);
			
//			gIm.fillPolygon(pijlpuntEind);
        	gIm.moveTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntEind.aantalPunten; k++)
			{	gIm.lineTo(pijlpuntEind.doubleX[k], pijlpuntEind.doubleY[k]);
			}
			gIm.lineTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.closePath();
			gIm.fill();
			
			
		
			pijlpuntKlik = new Polygon();
			//if (AlgebraPijlenHWT.touchStart)
			//{
				pijlpuntKlik.addPoint(x1+20, y1);
				pijlpuntKlik.addPoint(x1-2, y1-15);
				pijlpuntKlik.addPoint(x1-2, y1+15);
			
/*				
			}
			else
			{	
				pijlpuntKlik.addPoint(x1+15, y1);
				pijlpuntKlik.addPoint(x1-2, y1-13);
				pijlpuntKlik.addPoint(x1-2, y1+13);
			}
*/		
			if (vast) 
			{	//gIm.setColor(color);
				gIm.setStrokeStyle(color);
				gIm.setFillStyle(color);
			
			}
			else 
			{	//gIm.setColor(Color.gray);
				gIm.setStrokeStyle(gray);
				gIm.setFillStyle(gray);
			
			}
			
			//gIm.fillPolygon(pijlpuntBegin);
        	gIm.moveTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntBegin.aantalPunten; k++)
			{	gIm.lineTo(pijlpuntBegin.doubleX[k], pijlpuntBegin.doubleY[k]);
			}
			gIm.lineTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.closePath();
			gIm.fill();
			
			//gIm.fillPolygon(pijlpuntEind);
        	gIm.moveTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntEind.aantalPunten; k++)
			{	gIm.lineTo(pijlpuntEind.doubleX[k], pijlpuntEind.doubleY[k]);
			}
			gIm.lineTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.closePath();
			gIm.fill();

			
			//gIm.setColor(Color.black);
			gIm.setStrokeStyle(black);
			
			//gIm.drawPolygon(pijlpuntBegin);
        	gIm.moveTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntBegin.aantalPunten; k++)
			{	gIm.lineTo(pijlpuntBegin.doubleX[k], pijlpuntBegin.doubleY[k]);
			}
			gIm.lineTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.stroke();
			
			
			//gIm.drawPolygon(pijlpuntEind);
        	gIm.moveTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntEind.aantalPunten; k++)
			{	gIm.lineTo(pijlpuntEind.doubleX[k], pijlpuntEind.doubleY[k]);
			}
			gIm.lineTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.stroke();
			
//GWT			
			//if (!isStapel && !vast && !actief && (im != null))
			//	gIm.drawImage(im, x0, y0, this);
		}
		else // pijl naar links
		{	//gIm.setColor(Color.black);
			gIm.setStrokeStyle(black);

			double dx = x0-x1; double dy = y0-y1;
			int teken = (int)((dy/Math.abs(dy)));
			double s = Math.sqrt(dx*dx + dy*dy);
			double a;
			int r0, r1;
			double dr;
			int xc0,xc1,yc0,yc1, booghoek;
			if(Math.abs((double)dy/(double)dx)>0.04 && dx>=0)
			{	a = Math.atan((double)dx/Math.abs(dy));
				r0 = (int)(s/(4*Math.cos(a)));
				dr = s/(4*Math.cos(a)) - r0;
				if(dr>0.25)r1 = r0+1;
				else r1 = r0;
				xc0 = x1;
				yc0 = y1+r0*teken;
				xc1 = x0;
				yc1 = y0-r1*teken;
				booghoek = (int)((2*a - Math.PI)*180/Math.PI);
				//gIm.drawArc(xc0-r0, yc0-r0, 2*r0, 2*r0, teken*90, teken*(booghoek-1));
				
				double startAngle = teken*3*Math.PI/2;
				double deltaAngle = -teken*(booghoek-1)*Math.PI/180;
				double endAngle = startAngle + deltaAngle;
				boolean antiClockWise = false;
				if (endAngle < startAngle)
				{	antiClockWise = true;
				}
				gIm.beginPath();
				gIm.arc(xc0, yc0, r0, startAngle, endAngle, antiClockWise);
				gIm.stroke();
				
				//gIm.drawArc(xc1-r1, yc1-r1, 2*r1, 2*r1, teken*270, teken*(booghoek-1));
				
				startAngle = teken*Math.PI/2;
				endAngle = startAngle + deltaAngle;
				antiClockWise = false;
				if (endAngle < startAngle)
				{	antiClockWise = true;
				}
				
				gIm.beginPath();
				gIm.arc(xc1, yc1, r1, startAngle, endAngle, antiClockWise);
				gIm.stroke();

			}
			else if(Math.abs(dy)>1 && dx<0)
			{	r0 = (int)Math.abs(dy/4);
				dr = Math.abs(dy/4) - r0;
				if(dr>0.25)r1 = r0+1;
				else r1 = r0;
				xc0 = x1;
				yc0 = y1+r0*teken;
				xc1 = x0;
				yc1 = y0-r1*teken;
				booghoek = -180;
				//gIm.drawArc(xc0-r0, yc0-r0, 2*r0, 2*r0, teken*90, teken*(booghoek-1));
				
				double startAngle = teken*3*Math.PI/2;
				double deltaAngle = -teken*(booghoek-1)*Math.PI/180;
				double endAngle = startAngle + deltaAngle;
				boolean antiClockWise = false;
				if (endAngle < startAngle)
				{	antiClockWise = true;
				}
				
				gIm.beginPath();
				gIm.arc(xc0, yc0, r0, startAngle, endAngle, antiClockWise);
				gIm.stroke();

				
				//gIm.drawArc(xc1-r1, yc1-r1, 2*r1, 2*r1, teken*270, teken*(booghoek-1));
				
				startAngle = teken*Math.PI/2;
				endAngle = startAngle + deltaAngle;
				antiClockWise = false;
				if (endAngle < startAngle)
				{	antiClockWise = true;
				}
				
				gIm.beginPath();
				gIm.arc(xc1, yc1, r1, startAngle, endAngle, antiClockWise);
				gIm.stroke();

				
				//gIm.drawLine(x1,yc0+teken*r0,x0,yc1-teken*r1);
				gIm.beginPath();
				gIm.moveTo(x1,yc0+teken*r0);
				gIm.lineTo(x0,yc1-teken*r1);
				gIm.stroke();
			}
			else
			{	//gIm.drawLine(x0,y0,x1,y1);
				gIm.beginPath();
				gIm.moveTo(x0, y0);
				gIm.lineTo(x1, y1);
				gIm.stroke();

			}
			if(vast)
			{	//gIm.setColor(Color.red);
				gIm.setStrokeStyle(red);
				gIm.setFillStyle(red);
			
			}
			else 
			{	//gIm.setColor(Color.gray);
				gIm.setStrokeStyle(gray);
				gIm.setFillStyle(gray);
			
			}
		
			pijlpuntBegin = new Polygon();
			pijlpuntBegin.addPoint(x0, y0);
			pijlpuntBegin.addPoint(x0+10, y0-7);
			pijlpuntBegin.addPoint(x0+10, y0+7);
			
			//gIm.fillPolygon(pijlpuntBegin);
        	gIm.moveTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntBegin.aantalPunten; k++)
			{	gIm.lineTo(pijlpuntBegin.doubleX[k], pijlpuntBegin.doubleY[k]);
			}
			gIm.lineTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.closePath();
			gIm.fill();

		
			pijlpuntEind = new Polygon();
			pijlpuntEind.addPoint(x1-10, y1);
			pijlpuntEind.addPoint(x1, y1-7);
			pijlpuntEind.addPoint(x1, y1+7);
			//gIm.fillPolygon(pijlpuntEind);
        	gIm.moveTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntEind.aantalPunten; k++)
			{	gIm.lineTo(pijlpuntEind.doubleX[k], pijlpuntEind.doubleY[k]);
			}
			gIm.lineTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.closePath();
			gIm.fill();
			
			pijlpuntKlik = new Polygon();
			//if (AlgebraPijlenHWT.touchStart)
			//{
				pijlpuntKlik.addPoint(x1-20, y1);
				pijlpuntKlik.addPoint(x1+2, y1-15);
				pijlpuntKlik.addPoint(x1+2, y1+15);
/*				
			}
			else
			{	
				pijlpuntKlik.addPoint(x1-15, y1);
				pijlpuntKlik.addPoint(x1+2, y1-13);
				pijlpuntKlik.addPoint(x1+2, y1+13);
			}
*/		
			if(vast)
			{	//gIm.setColor(Color.black);
				gIm.setStrokeStyle(black);
				gIm.setFillStyle(black);
			
			}
			else 
			{	//gIm.setColor(Color.gray);
				gIm.setStrokeStyle(gray);
				gIm.setFillStyle(gray);
			}
			
			
			//gIm.fillPolygon(pijlpuntBegin);
        	gIm.moveTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntBegin.aantalPunten; k++)
			{	gIm.lineTo(pijlpuntBegin.doubleX[k], pijlpuntBegin.doubleY[k]);
			}
			gIm.lineTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.closePath();
			gIm.fill();
			
			
			//gIm.fillPolygon(pijlpuntEind);
        	gIm.moveTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntEind.aantalPunten; k++)
			{	gIm.lineTo(pijlpuntEind.doubleX[k], pijlpuntEind.doubleY[k]);
			}
			gIm.lineTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.closePath();
			gIm.fill();
			
						
			//gIm.setColor(Color.black);
			gIm.setStrokeStyle(black);
			
			//gIm.drawPolygon(pijlpuntBegin);
        	gIm.moveTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntBegin.aantalPunten; k++)
			{	gIm.lineTo(pijlpuntBegin.doubleX[k], pijlpuntBegin.doubleY[k]);
			}
			gIm.lineTo(pijlpuntBegin.doubleX[0], pijlpuntBegin.doubleY[0]);
			gIm.stroke();
			
			//gIm.drawPolygon(pijlpuntEind);
        	gIm.moveTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntEind.aantalPunten; k++)
			{	gIm.lineTo(pijlpuntEind.doubleX[k], pijlpuntEind.doubleY[k]);
			}
			gIm.lineTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.stroke();
			
			
//GWT: waar moet dit heen?			
			//if (!isStapel && !vast && !actief && (im != null))
			//	gIm.drawImage(im,x0,y0, this);			
		}	
			
		
		
		
		
	}
	
	
/*	
	public void update(Graphics g)
	{	paint(g);
	}
*/	
	public boolean contains(int x, int y)
	{	return pijlpuntKlik.contains(x,y);
	}
	
	public void zetStapel(boolean b)
	{	isStapel = b;
	}
	public void zetZender(AlgebraSchuifComponent r)
	{	zender = r;
	}
	public void zetPlaats(int x, int y)
	{	x0 = x;
		y0 = y;
		if(ontvanger == null)
		{	if(!links)x1 = x - 10;
	        else x1 = x + 10;
			y1 = y;
		}
	}
	public void zetEind(int x, int y)
	{	
        x1 = x;
		y1 = y;
	}
	public void zetBegin(int x, int y)
	{	x0 = x;
		y0 = y;
	}
	public void verplaatsBegin(int dx, int dy)
	{	x0 = x0 + dx;
		y0 = y0 + dy;
		if(!vast)
		{	x1 = x1 + dx;
			y1 = y1 + dy;
		}
		//repaint();
		paint();
	}
	public void verplaatsEind(int dx, int dy)
	{	x1 = x1 + dx;
		y1 = y1 + dy;
		//repaint();
		paint();
	}
	public void plaatsOpGridBegin()
	{	int x;
		int y;
		x = x0+300;
		y = y0+300;
		int ex = x%10;
		int ey = y%10;
		if(ex<5)verplaatsBegin(-ex,0);
		else verplaatsBegin(10-ex,0);
		if(ey<5)verplaatsBegin(0,-ey);
		else verplaatsBegin(0,10-ey);
	}
	public void plaatsOpGridEind()
	{	int x;
		int y;
		x = x1+300;
		y = y1+300;
		int ex = x%10;
		int ey = y%10;
		if(ex<5)verplaatsEind(-ex,0);
		else verplaatsEind(10-ex,0);
		if(ey<5)verplaatsEind(0,-ey);
		else verplaatsEind(0,10-ey);
	}
	public void verplaats(int dx,int dy)
	{	if(!actief)
		{	x0 = x0 + dx;
			y0 = y0 + dy;
		}
		else
		{	x1 = x1 + dx;
			y1 = y1 + dy;
		}
		//repaint();
		paint();
	}
	public void pijlTerug()
	{	vast = false;
		
		if (ontvanger != null && ontvanger.pijlUit != null && ontvanger.pijlUit[0] != null)
		{	
//GWT			
			//ontvanger.pijlUit[0].im = null;
			
		
		}
	
		ontvanger = null;
		if(!links)x1 = x0 - 10;
        else x1 = x0 + 10;
		y1 = y0;
		
		asv.tekenOpnieuw();
	}
	
	
	public void zetVerbonden(AlgebraSchuifComponent asc)
	{	vast = true;
		ontvanger = asc;
		Pijl p = new Pijl(asv);
		p.zetLinks(links);
		zender.voegPijlToe(p);		
		
		asv.tekenOpnieuw();
	}
	
	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	if (asv.fixed)
			return;
		if (asv.alleenInvullen)
			return;
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;
		
		mouseDown = true;

		//schuifveld.start();
		//schuifveld.zetOpSchuifLaag(this);
		//requestFocus();
		
		vast = false;
		actief = true;
		
		if (ontvanger != null)
		{	ontvanger.maakLos(this);
			ontvanger.zetVeranderd(20);
		}
		zender.verwijderPijl();
		laatstex = eventX;
		laatstey = eventY;
		
		//im = null;
		
		asv.tekenOpnieuw();
	}	
	
	
	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	if(asv.fixed)
			return;
		if (asv.alleenInvullen)
			return;
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;
	
		if (!mouseDown)
			return;
	
		if (isStapel) return;
		if(actief)
		{	int dx = eventX - laatstex;
			int dy =  eventY - laatstey;
			x1 = x1 + dx;
			y1 = y1 + dy;
			paint();
			laatstex = eventX;
			laatstey = eventY;
		}
		
		asv.tekenOpnieuw();
	}
	
	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction()
	{	if (asv.fixed)
			return;
		if (asv.alleenInvullen)
			return;
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;
		
		mouseDown = false;
	
		plaatsOpGridEind();
		for (int i = 0 ; i < asv.aantalSc; i++)
		{	boolean b = false;
			if (
				asv.schuifcomponenten[i].visible && 
				!asv.schuifcomponenten[i].isStapel && 
				!zender.isStapel && asv.schuifcomponenten[i].links == links)
			{	if (!links)
					b = asv.schuifcomponenten[i].meldAan(this, x1 + 10, y1);
				else 
					b = asv.schuifcomponenten[i].meldAan(this, x1 - 10, y1);
			}
			if(b)
			{	 vast = true;
				 ontvanger = asv.schuifcomponenten[i];
				 Pijl p = new Pijl(asv);
				 p.zetLinks(links);
				 zender.voegPijlToe(p);				
				 actief = false;
				 //schuifveld.zetTerugSchuifLaag(this);
				 asv.tekenOpnieuw();
				 return;
			}
		}
		if (actief) 
			pijlTerug();
		actief = false;
		
		//schuifveld.zetTerugSchuifLaag(this);
		
		asv.tekenOpnieuw();
	}
	
}

