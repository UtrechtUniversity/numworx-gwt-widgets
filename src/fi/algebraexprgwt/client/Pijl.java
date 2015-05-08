package fi.algebraexprgwt.client;

//import java.awt.*;
//import java.awt.event.*;
//import fi.algebraexpressies.schuifobjects.*;

//import javax.swing.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;


public class Pijl //extends JComponent //Component 
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
	boolean actief, vast;
	private boolean isStapel;
	
	CssColor black = CssColor.make(0,0,0);
	CssColor gray = CssColor.make(128,128,128);
	CssColor red = CssColor.make(255,0,0);
	
	private CssColor color = CssColor.make(0,0,0);
	//private Color color = Color.black;	
	
	//Image im;
	String im = null;	
	
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
		//addMouseListener(this);
		//addMouseMotionListener(this);
		pijlpuntEind = new Polygon();
		pijlpuntKlik = new Polygon();
		
		//setOpaque(false);
		
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

		//gIm.setColor(color);
		gIm.setStrokeStyle(color);
		
		double dx = x1-x0; double dy = y1-y0;
		if (dx!=0 || dy!=0)
		{	double s = Math.sqrt(dx*dx + dy*dy);
			double alpha,beta;
			int rmax = 20; 
			int r;
		
			int xb0,xb1,yb0,yb1, booghoek;
		
			beta = Math.atan((double)dy/Math.abs(dx));
			r = (int)(s/(4*Math.cos(beta)));
			int teken = (int)((dx/Math.abs(dx)));
			if(r < rmax)
				rmax = r;
			
			
			if((dx<2*rmax && dx>-2*rmax) && dy>=0)
			{	double h = 2*Math.sqrt(rmax*rmax - (rmax - teken*dx/2)*(rmax - teken*dx/2));
				alpha = Math.atan(h/Math.abs(dx));
				xb0 = x0 - rmax + teken*rmax;
				yb0 = y0 - rmax;
				xb1 = x1 - rmax - teken*rmax;
				yb1 = y0 + (int)h - rmax;
				booghoek = (int)((Math.PI - 2*alpha)*180/Math.PI);
				//if (color == Color.black)
				if (color.toString().equals(black.toString()))
				{	
					//Java angles: degrees and anticlockwise from positive x-axis
					//GWT angles: radians and clockwise from positive x-axis
					// 90 Java = 270 GWT = 3pi/2, 270 Java = 90 GWT = pi/2
					
					//gIm.drawArc(xb0, yb0, 2*rmax, 2*rmax, 90+teken*90, teken*(booghoek+2));
					
					double startAngle = Math.PI/2 + teken*Math.PI/2;
					double deltaAngle = -teken*(booghoek+2)*Math.PI/180;
					double endAngle = startAngle + deltaAngle;
					boolean antiClockWise = false;
					if (endAngle < startAngle)
					{	antiClockWise = true;
					}
					
					//gIm.setStrokeStyle(color);
					//gIm.setStrokeStyle(CssColor.make(255,0,0));
					
					gIm.beginPath();
					gIm.arc(xb0+rmax, yb0+rmax, rmax, startAngle, endAngle, antiClockWise);
					gIm.stroke();

					
					//gIm.drawArc(xb1, yb1, 2*rmax, 2*rmax, -90+teken*90, teken*(booghoek+2));
					
					startAngle = -Math.PI/2 + teken*Math.PI/2;
					deltaAngle = -teken*(booghoek+2)*Math.PI/180;
					endAngle = startAngle + deltaAngle;
					antiClockWise = false;
					if (endAngle < startAngle)
					{	antiClockWise = true;
					}
					
					//gIm.setStrokeStyle(color);
					//gIm.setStrokeStyle(CssColor.make(255,0,0));
					
					gIm.beginPath();
					gIm.arc(xb1+rmax, yb1+rmax, rmax, startAngle, endAngle, antiClockWise);
					gIm.stroke();
					
					//gIm.drawLine(x1, y0+(int)h, x1,y1);
					gIm.beginPath();
					gIm.moveTo(x1, y0+(int)h);
					gIm.lineTo(x1,y1);
					gIm.stroke();

				}	
			}
			else if(dy>=2*rmax && dy>=0)
			{	double h = 2*Math.sqrt(rmax*rmax - (rmax - teken*dx/2)*(rmax - teken*dx/2));
				alpha = Math.atan(h/Math.abs(dx));
				xb0 = x0 - rmax + teken*rmax;
				yb0 = y0 - rmax;
				xb1 = x1 - rmax - teken*rmax;
				yb1 = y0 + rmax;
				booghoek = 90;
				//if (color == Color.black)
				if (color.toString().equals(black.toString()))
				{	
					//gIm.drawArc(xb0, yb0, 2*rmax, 2*rmax, 90+teken*90, teken*(booghoek+2));

					double startAngle = Math.PI/2 + teken*Math.PI/2;
					double deltaAngle = -teken*(booghoek+2)*Math.PI/180;
					double endAngle = startAngle + deltaAngle;
					boolean antiClockWise = false;
					if (endAngle < startAngle)
					{	antiClockWise = true;
					}
					
					//gIm.setStrokeStyle(color);
					//gIm.setStrokeStyle(CssColor.make(255,0,0));
					
					gIm.beginPath();
					gIm.arc(xb0+rmax, yb0+rmax, rmax, startAngle, endAngle, antiClockWise);
					gIm.stroke();
					
					
					//gIm.drawArc(xb1, yb1, 2*rmax, 2*rmax, -90+teken*90, teken*(booghoek+2));
					
					startAngle = -Math.PI/2 + teken*Math.PI/2;
					deltaAngle = -teken*(booghoek+2)*Math.PI/180;
					endAngle = startAngle + deltaAngle;
					antiClockWise = false;
					if (endAngle < startAngle)
					{	antiClockWise = true;
					}
					
					//gIm.setStrokeStyle(color);
					//gIm.setStrokeStyle(CssColor.make(255,0,0));
					
					gIm.beginPath();
					gIm.arc(xb1+rmax, yb1+rmax, rmax, startAngle, endAngle, antiClockWise);
					gIm.stroke();

					
					//gIm.drawLine(x1, y0+2*rmax, x1,y1);
					gIm.beginPath();
					gIm.moveTo(x1, y0+2*rmax);
					gIm.lineTo(x1,y1);
					gIm.stroke();
					
					//gIm.drawLine(x0 + teken*rmax, y0 + rmax, x1 - teken*rmax,y0 + rmax);
					gIm.beginPath();
					gIm.moveTo(x0 + teken*rmax, y0 + rmax);
					gIm.lineTo(x1 - teken*rmax, y0 + rmax);
					gIm.stroke();

				}	
			}
			else if(dy<2*rmax  && dy>=0)
			{	double b = 2*rmax + 2*Math.sqrt(rmax*rmax - (dy/2)*(dy/2));
				alpha = Math.atan(Math.abs(dy)/b);
				xb0 = x0 - rmax + teken*rmax;
				yb0 = y0 - rmax;
				xb1 = x1 - rmax - teken*rmax;
				yb1 = y1 - rmax;
				booghoek = (int)((Math.PI - 2*alpha)*180/Math.PI);
				//if (color == Color.black)
				if (color.toString().equals(black.toString()))
				{
					//gIm.drawArc(xb0, yb0, 2*rmax, 2*rmax, 90+teken*90, teken*(booghoek+2));

					double startAngle = Math.PI/2 + teken*Math.PI/2;
					double deltaAngle = -teken*(booghoek+2)*Math.PI/180;
					double endAngle = startAngle + deltaAngle;
					boolean antiClockWise = false;
					if (endAngle < startAngle)
					{	antiClockWise = true;
					}
					
					//gIm.setStrokeStyle(color);
					//gIm.setStrokeStyle(CssColor.make(255,0,0));
					
					gIm.beginPath();
					gIm.arc(xb0+rmax, yb0+rmax, rmax, startAngle, endAngle, antiClockWise);
					gIm.stroke();
					
					
					//gIm.drawArc(xb0+ teken*(int)b-2*teken*rmax, yb1, 2*rmax, 2*rmax, 90, teken*(booghoek-90+2));

					//startAngle = Math.PI/2;
					startAngle = -Math.PI/2;
					deltaAngle = -teken*(booghoek-90+2)*Math.PI/180;
					endAngle = startAngle + deltaAngle;
					antiClockWise = false;
					if (endAngle < startAngle)
					{	antiClockWise = true;
					}
					
					//gIm.setStrokeStyle(color);
					//gIm.setStrokeStyle(CssColor.make(255,0,0));
					
					gIm.beginPath();
					gIm.arc(xb0+ teken*(int)b-2*teken*rmax + rmax, yb1+rmax, rmax, startAngle, endAngle, antiClockWise);
					gIm.stroke();
					
					
					//gIm.drawArc(xb1, yb1, 2*rmax, 2*rmax, -90+teken*90, teken*(92));

					startAngle = -Math.PI/2 + teken*Math.PI/2;
					deltaAngle = -teken*(92)*Math.PI/180;
					endAngle = startAngle + deltaAngle;
					antiClockWise = false;
					if (endAngle < startAngle)
					{	antiClockWise = true;
					}
					
					//gIm.setStrokeStyle(color);
					//gIm.setStrokeStyle(CssColor.make(255,0,0));
					
					gIm.beginPath();
					gIm.arc(xb1+rmax, yb1+rmax, rmax, startAngle, endAngle, antiClockWise);
					gIm.stroke();
					
					
					//gIm.drawLine(x0 + teken*(int)b - teken*rmax, y1 - rmax, x1-teken*rmax,y1 - rmax);
					gIm.beginPath();
					gIm.moveTo(x0 + teken*(int)b - teken*rmax, y1 - rmax);
					gIm.lineTo(x1-teken*rmax,y1 - rmax);
					gIm.stroke();

				}	
			}
			else
			{	if(Math.abs(dx/4) < rmax)rmax = Math.abs((int)dx/4);
				xb0 = x0 - rmax + teken*rmax;
				yb0 = y0 - rmax;
				xb1 = x1 - rmax - teken*rmax;
				yb1 = y1 - rmax;
				//if (color == Color.black)
				if (color.toString().equals(black.toString()))
				{	
					//gIm.drawArc(xb0, yb0, 2*rmax, 2*rmax, 90+teken*90, teken*182);
					
					double startAngle = Math.PI/2 + teken*Math.PI/2;
					double deltaAngle = -teken*(182)*Math.PI/180;
					double endAngle = startAngle + deltaAngle;
					boolean antiClockWise = false;
					if (endAngle < startAngle)
					{	antiClockWise = true;
					}
					gIm.beginPath();
					gIm.arc(xb0+rmax, yb0+rmax, rmax, startAngle, endAngle, antiClockWise);
					gIm.stroke();

					
					//gIm.drawArc(xb0+ 2*teken*rmax, yb1, 2*rmax, 2*rmax, 90, teken*92);
					
					//startAngle = Math.PI/2;
					startAngle = -Math.PI/2;
					deltaAngle = -teken*(92)*Math.PI/180;
					endAngle = startAngle + deltaAngle;
					antiClockWise = false;
					if (endAngle < startAngle)
					{	antiClockWise = true;
					}
					gIm.beginPath();
					gIm.arc(xb0+ 2*teken*rmax + rmax, yb1+rmax, rmax, startAngle, endAngle, antiClockWise);
					gIm.stroke();
					
					//gIm.drawArc(xb1, yb1, 2*rmax, 2*rmax, -90+teken*90, teken*(92));
					
					startAngle = -Math.PI/2 + teken*Math.PI/2;
					deltaAngle = -teken*(92)*Math.PI/180;
					endAngle = startAngle + deltaAngle;
					antiClockWise = false;
					if (endAngle < startAngle)
					{	antiClockWise = true;
					}
					gIm.beginPath();
					gIm.arc(xb1+rmax, yb1+rmax, rmax, startAngle, endAngle, antiClockWise);
					gIm.stroke();

					
					//gIm.drawLine(xb0 + rmax + teken*rmax , y0 , xb0 + rmax + teken*rmax ,y1 );
					gIm.beginPath();
					gIm.moveTo(xb0 + rmax + teken*rmax , y0);
					gIm.lineTo(xb0 + rmax + teken*rmax ,y1);
					gIm.stroke();
					
					//gIm.drawLine(x0 + 3*teken*rmax , y1-rmax , x1 - teken*rmax, y1-rmax );
					gIm.beginPath();
					gIm.moveTo(x0 + 3*teken*rmax , y1-rmax);
					gIm.lineTo(x1 - teken*rmax, y1-rmax);
					gIm.stroke();
				}
			}
		}

		pijlpuntBegin = new Polygon();
		pijlpuntBegin.addPoint(x0, y0);
		pijlpuntBegin.addPoint(x0-7, y0-10);
		pijlpuntBegin.addPoint(x0+7, y0-10);
		
		pijlpuntEind = new Polygon();
		pijlpuntEind.addPoint(x1, y1+10);
		pijlpuntEind.addPoint(x1-7, y1);
		pijlpuntEind.addPoint(x1+7, y1);
		
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

		
		if (actief || vast)
		{	//gIm.fillPolygon(pijlpuntEind);
			gIm.moveTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntEind.aantalPunten; k++)
			{	gIm.lineTo(pijlpuntEind.doubleX[k], pijlpuntEind.doubleY[k]);
			}
			gIm.lineTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.closePath();
			gIm.fill();
		}
		
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

		
		
		if (actief || vast)
		{	//gIm.drawPolygon(pijlpuntEind);
			gIm.moveTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.beginPath();
			for (int k = 1; k < pijlpuntEind.aantalPunten; k++)
			{	gIm.lineTo(pijlpuntEind.doubleX[k], pijlpuntEind.doubleY[k]);
			}
			gIm.lineTo(pijlpuntEind.doubleX[0], pijlpuntEind.doubleY[0]);
			gIm.stroke();

		
		}
		
//GWT: waar moet dit heen?		
		if (!isStapel && !vast && !actief && (im != null))
		{	//gIm.drawImage(im, x0, y0, this);
			//System.out.println("im = " + im);				
			if (im.equals("V"))
				gIm.setFillStyle(CssColor.make(41,156,57));
			else if (im.equals("X"))
				gIm.setFillStyle(CssColor.make(255,0,0));
			String oldFont = gIm.getFont();
			gIm.setFont("26px sans-serif");
			gIm.fillText(im,x0-10,y0+25);
			gIm.setFont(oldFont);
			
		
		}
		
	}
	
//	public void update(Graphics g)
//	{	paint(g);
//	}
	
	public boolean contains(int x, int y)
	{	Polygon pijlpuntRaak = new Polygon();
	
//gevoeligheid omhoog	
		//pijlpuntRaak.addPoint(x1, y1+15);
		//pijlpuntRaak.addPoint(x1-12, y1-3);
		//pijlpuntRaak.addPoint(x1+12, y1-3);
	
		pijlpuntRaak.addPoint(x1, y1+20);
		pijlpuntRaak.addPoint(x1-14, y1-3);
		pijlpuntRaak.addPoint(x1+14, y1-3);
		
		return pijlpuntRaak.contains(x,y);
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
		x1 = x;
		y1 = y-10;
	}
	public void zetEind(int x, int y)
	{	x1 = x;
		y1 = y-10;
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
		paint();
	}
	public void verplaatsEind(int dx, int dy)
	{	x1 = x1 + dx;
		y1 = y1 + dy;
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
		paint();
	}
	public void pijlTerug()
	{	vast = false;
		ontvanger = null;
		x1 = x0;
		y1 = y0-10;
	}
	
	public void zetVerbonden(AlgebraSchuifComponent asc)
	{	vast = true;
		ontvanger = asc;
		Pijl p = new Pijl(asv);
		zender.voegPijlToe(p);		
	}
	
	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		
		if (asv.alleenInvullen)
			return;
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;
		
		mouseDown = true;
		
//		schuifveld.start();
//		schuifveld.zetOpSchuifLaag(this);
//		requestFocus();
		
		vast = false;
		actief = true;
		if(ontvanger!=null )
		{	ontvanger.maakLos(this);
			ontvanger.zetVeranderd(20);
		}
		zender.verwijderPijl();
		laatstex = eventX;
		laatstey = eventY;
		
		asv.tekenOpnieuw();
	}	
	
	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		if (asv.alleenInvullen)
			return;
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;
		
		if (!mouseDown)
			return;
		
		if(zender.isStapel)
			return;
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
	{	
		if (asv.alleenInvullen)
			return;
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;
		
		asv.changed = true;
		
		mouseDown = false;
		
		plaatsOpGridEind();
		for (int i = 0; i < asv.aantalSc; i++)
		{	
			boolean b = asv.schuifcomponenten[i].meldAan(this,x1,y1+5);
			//boolean b = false;
			if (
				asv.schuifcomponenten[i].visible && 
				!asv.schuifcomponenten[i].isStapel && 
				!zender.isStapel)
			if(b)
			{	 vast = true;
				 ontvanger = asv.schuifcomponenten[i];
				 zender.voegPijlToe(new Pijl(asv));				
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
	//public void mouseMoved(MouseEvent e){;}
	//public void mouseExited(MouseEvent e){;}
	//public void mouseClicked(MouseEvent e){;}
	//public void mouseEntered(MouseEvent e){;}
}

