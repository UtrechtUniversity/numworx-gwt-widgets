package fi.heksgwt.client;

//import java.awt.*;
//import java.applet.*;
//import java.awt.event.*;
import java.util.*;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

import fi.heksgwt.client.Pagina23OefenPanel.MouseHandler;
import fi.heksgwt.client.Pagina23OefenPanel.TouchHandler;
import fi.heksgwt.client.scobjects.*;
//import fi.beans.appletutil.*;
import fi.heksgwt.client.vectortek.*;

public class Pagina24OefenPanel //extends ScContainer //Panel implements MouseListener, MouseMotionListener, ActionListener 
{
	//private AppletUtil au;
	//HeksInteractiePanel heip;
	HeksGWT eigenaar;
	Pagina24Panel p24;
	
	//private ActionListener actionListener;
	//private AchtergrondContainer achtergrond;

	Tekening blokjePlus, blokjeMin, blokjeSleep, blokjeSleepMin;
	private Tekening pot, potinhoud, erinpijl, eruitpijl;
	private ZinkAnimatie za;
	private GetalComponent tc;
	//private ScPanel sleeppanel;
	
	private Polygon[] p;

	private int laatstex, laatstey;
	private boolean instelbaar;
	private boolean raakSleep, raakSleepMin;
	private boolean plusEruit, minEruit, pasEruit;
	private boolean[] kleurBlokjes = { true, false, true, true, true, true, false, false, false, false, true };

	private boolean actief, erinMogelijk, eruitMogelijk;
	private int aantalKerenGebruikt;
	
	//int xPos, yPos;
	
	TekstPopup tf;
	
	boolean mouseDown = false; 

	public Pagina24OefenPanel(int x, int y, int b, int h, HeksGWT eigenaar, Pagina24Panel p24) 
	{
		//super(x, y, b, h);
		//this.heip = heip;
		// setBackground(new Color(255,255,220));
		//setBackground(heip.bgColor);
		this.eigenaar = eigenaar;
		this.p24 = p24;
		//xPos = x; yPos = y;

		// raakPlusBuiten = false;
		// raakPlusBinnen = false;
		// raakMinBuiten = false;
		// raakMinBinnen = false;
		raakSleep = false;
		plusEruit = false;
		minEruit = false;
		pasEruit = false;
		erinMogelijk = true;
		eruitMogelijk = true;
		instelbaar = false;

		actief = false;
		aantalKerenGebruikt = 0;

		//Color color_01 = new Color(240, 240, 240);
		//String kleurcode = applet.getParameter("color_01");
		//if (kleurcode != null)
		//	color_01 = new Color(Integer.parseInt(kleurcode.substring(1), 16));

		//sleeppanel = new ScPanel(0, 0, b, h - 5);
		//sleeppanel.setBackground(Color.white);
		//sleeppanel.addMouseListener(this);
		//sleeppanel.addMouseMotionListener(this);
		//achtergrond = new AchtergrondContainer(0, 0, b, h - 5);

		//Java 50,40
		pot = new Tekening(50+20, 40+40, 220, 180, eigenaar.pot24Map);
//pot.naam =  "pot";
		//achtergrond.add(pot);
		p24.add(pot);
		
		//Java 50,60
		potinhoud = new Tekening(50+20, 60+40, 215, 155, eigenaar.potinhoud24Map);
		//sleeppanel.add(potinhoud, 0);
		p24.add(potinhoud);

		//erinpijl = new Tekening(20, 10, 100, 75, heip, "gebogenpijl.gif");
		//erinpijl.setVisible(false);
		//achtergrond.add(erinpijl);

		//eruitpijl = new Tekening(120, 10, 100, 75, heip, "gebogenpijl.gif");
		//eruitpijl.setVisible(false);
		//achtergrond.add(eruitpijl);

		//Java 300,30
		blokjePlus = new Tekening(300+20, 30+40, 40, 40, eigenaar.blokjePlus24Map);
		//achtergrond.add(blokjePlus);
		p24.add(blokjePlus);

		//Java 300,80
		blokjeMin = new Tekening(300+20, 80+40, 40, 40, eigenaar.blokjeMin24Map);
		//achtergrond.add(blokjeMin);
		p24.add(blokjeMin);

		//Java,75,60
		za = new ZinkAnimatie(75+38, 60+40, 135, 100, eigenaar, p24);
		za.zetBellenAan(false);
		//sleeppanel.add(za, 0);
		za.fraction = 195e-2d;
		p24.add(za);

		//blokjeSleep = new Tekening(-100, -100, 40, 40, heip, "blokjePlus.gif");
		blokjeSleep = new Tekening(300+20, 30+40, 40, 40, eigenaar.blokjePlus24Map);
//blokjeSleep.naam = "blokjeSleep";
		//sleeppanel.add(blokjeSleep, 0);
		p24.add(blokjeSleep);

		//blokjeSleepMin = new Tekening(-100, -100, 40, 40, heip, "blokjeMin.gif");
		blokjeSleepMin = new Tekening(300+20, 80+40, 40, 40, eigenaar.blokjeMin24Map);
		//sleeppanel.add(blokjeSleepMin, 0);
		p24.add(blokjeSleepMin);

		//Java 130,98
		tc = new GetalComponent(130+20, 98+40, 65, 40, eigenaar);
		tc.zetAlsTemp(true);
		tc.transparent = true;
		// tc.zetBekend(false);
		//sleeppanel.add(tc, 0);
		p24.add(tc);
		
		
		//sleeppanel.add(achtergrond);
		//add(sleeppanel);
	}

	public void initHandlers()
	{
		MouseHandler mouseHandler = new MouseHandler();
		eigenaar.heksGWTCanvas.addMouseDownHandler(mouseHandler);
		eigenaar.heksGWTCanvas.addMouseMoveHandler(mouseHandler);
		eigenaar.heksGWTCanvas.addMouseUpHandler(mouseHandler);
		
		TouchHandler touchHandler = new TouchHandler();
		eigenaar.heksGWTCanvas.addTouchStartHandler(touchHandler);
		eigenaar.heksGWTCanvas.addTouchMoveHandler(touchHandler);
		eigenaar.heksGWTCanvas.addTouchEndHandler(touchHandler);

	}

//GWT?
/*	
	public void zetErinMogelijk(boolean b) 
	{
		erinMogelijk = b;
		if (!b) {
			eruitpijl.setVisible(true);
			achtergrond.remove(blokjePlus);
			achtergrond.remove(blokjeMin);
			// blokjeSleep.setLocation(-100,-100);
			// blokjeSleepMin.setLocation(-100,-100);
			blokjeSleep.repaint();
		} else {
			eruitpijl.setVisible(false);
		}
	}
*/
	
//GWT?
/*	
	public void zetEruitMogelijk(boolean b) {
		eruitMogelijk = b;
		if (!b) {
			erinpijl.setVisible(true);
		} else {
			erinpijl.setVisible(false);
		}
	}
*/
	public void zetBeginTemp(int temp) 
	{
		tc.zetWaarde(temp);
	}

	public void zetInstelbaar(boolean b) 
	{
		
		instelbaar = b;
		tc.zetInstelbaar(b);
//GWT		
		//if (b) 
		//{
		//	tc.addActionListener(this);
		//} else 
		//{
		//	tc.removeActionListener(this);
		//}
	}

//GWT?
/*	
	public void zetActief(boolean b) 
	{
		actief = b;
		if (!b)
			tc.zetBekend(false);
		tc.repaint();
	}
*/
	
//GWT?
/*	
	public void zetGebruikt(int aantal) 
	{
		aantalKerenGebruikt = aantal;
		tc.zetBekend(false);
		tc.repaint();
	}
*/
	public int geefGebruikt() 
	{
		return aantalKerenGebruikt;
	}

	public int geefTemp() 
	{
		return tc.geefWaarde();
	}

/*	
	public void addActionListener(ActionListener listener) {
		actionListener = AWTEventMulticaster.add(actionListener, listener);
	}
*/
/*	
	public void removeActionListener(ActionListener listener) {
		actionListener = AWTEventMulticaster.remove(actionListener, listener);
	}
*/
	
	public void showTekstPopup(GetalComponent gc)
	{
		int popupX = gc.xPos + eigenaar.heksGWTCanvas.getAbsoluteLeft();
		
		int popupY = gc.yPos + gc.hoogte + eigenaar.heksGWTCanvas.getAbsoluteTop();
		
		if ((tf != null) && tf.isVisible())
		{
			//zetInvulWaarde();
		}

		tf = new TekstPopup(gc, p24);
		tf.setText(gc.geefWaardeText());
		tf.setWidth("35px");
		tf.setHeight("20px");
		//tf.setModal(true);
		tf.setPopupPosition(popupX, popupY);
		tf.show();
		tf.textBox.setFocus(true);

	}

	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		laatstex = eventX;
		laatstey = eventY;
		
//System.out.println("mouseDown " + eventX + "," + eventY);		
//hier geklikt op tc
		
		if (tc.contains(eventX, eventY)) 
		{	showTekstPopup(tc);
			//beginTemp.vulIn();
		}

		if (instelbaar)
			tc.zetInstelbaar(false);

		p = new Polygon[11];
		for (int i = 0; i < 11; i++) 
		{
			p[i] = ((VeelhoekTek) (potinhoud.to[i])).basisPolygon;
		}

//dir is niet nodig??
/*		
		if ((blokjePlus.contains(eventX, eventY) || blokjeMin.contains(eventX, eventY)) && erinMogelijk) 
		{
			blokjeSleep.setLocation((int) (p24.schaal * 300), (int) (p24.schaal * 30));
			blokjeSleepMin.setLocation((int) (p24.schaal * 300), (int) (p24.schaal * 80));

		}
*/
		if (blokjeSleep.contains(eventX, eventY)) 
		{
//System.out.println("blokjeSleep contains");
			raakSleep = true;
		}

		if (blokjeSleepMin.contains(eventX, eventY)) 
		{
//System.out.println("blokjeSleepMin contains");
			raakSleepMin = true;
		}
		
// zie pa22		
		int x = eventX; // - potinhoud.getLocation().x;
		int y = eventY; // - potinhoud.getLocation().y;

		for (int i = 10; i > -1; i--) 
		{
			if (p[i].contains(x, y)) 
			{
				if (kleurBlokjes[i] && eruitMogelijk) 
				{
//System.out.println("p[" + i + "] contains");					
					blokjeSleep.setLocation(eventX - blokjeSleep.breedte / 2, eventY - blokjeSleep.hoogte / 2);
					//blokjeSleep.repaint();
					p24.paint(eigenaar.heksGWTContext2d);
					plusEruit = true;
				} 
				else if (eruitMogelijk) 
				{
					blokjeSleepMin.setLocation(eventX - blokjeSleep.breedte / 2, eventY - blokjeSleep.hoogte / 2);
					//blokjeSleepMin.repaint();
					p24.paint(eigenaar.heksGWTContext2d);
					minEruit = true;
				}

				return;
			}
		}

	}

	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{
//System.out.println("mouseMove " + eventX + "," + eventY);

		int dx = eventX - laatstex;
		int dy = eventY - laatstey;

		if (raakSleep) 
		{
			//blokjeSleep.setLocation(blokjeSleep.getLocation().x + dx, blokjeSleep.getLocation().y + dy);
			blokjeSleep.verplaats(dx, dy);
			
//System.out.println("mm " + blokjeSleep.getLocation().x);

			Polygon p = ((VulKrommeTek) (pot.to[2])).buigPolygon;
			int lx = 0; //pot.getLocation().x;
			int ly = 0; //pot.getLocation().y;
			for (int i = 0; i < p.geefAantalPunten(); i++) 
			{
				if (blokjeSleep.contains(p.geefPuntX(i) + lx, p.geefPuntY(i) + ly)) 
				{
					//blokjeSleep.setLocation(blokjeSleep.getLocation().x - dx, blokjeSleep.getLocation().y - dy);
					blokjeSleep.verplaats(-dx, -dy);
					raakSleep = false;
				}
			}
			
			if ((blokjeSleep.getLocation().x < 0) || 
					(blokjeSleep.getLocation().x + blokjeSleep.getSize().x > p24.breedte) ||
					(blokjeSleep.getLocation().y < 0) || 
					(blokjeSleep.getLocation().y + blokjeSleep.getSize().y > p24.hoogte))
				{
					raakSleep = false;
					blokjeSleep.setLocation((int) (blokjePlus.getLocation().x), (int) (blokjePlus.getLocation().y));
					
				}
			
			//blokjeSleep.repaint();
			p24.paint(eigenaar.heksGWTContext2d);
			laatstex = eventX;
			laatstey = eventY;

		}
		if (raakSleepMin) 
		{
			//blokjeSleepMin.setLocation(blokjeSleepMin.getLocation().x + dx, blokjeSleepMin.getLocation().y + dy);
			blokjeSleepMin.verplaats(dx, dy);
			
			Polygon p = ((VulKrommeTek) (pot.to[2])).buigPolygon;
			int lx = 0;//pot.getLocation().x;
			int ly = 0;//pot.getLocation().y;
			for (int i = 0; i < p.geefAantalPunten(); i++) 
			{
				if (blokjeSleepMin.contains(p.geefPuntX(i) + lx, p.geefPuntY(i) + ly)) 
				{
//System.out.println("rSMin bSM contains");

					//blokjeSleepMin.setLocation(blokjeSleepMin.getLocation().x - dx, blokjeSleepMin.getLocation().y - dy);
					blokjeSleepMin.verplaats(-dx, -dy);
					raakSleepMin = false;
				}
			}
			
			if ((blokjeSleepMin.getLocation().x < 0) || 
					(blokjeSleepMin.getLocation().x + blokjeSleepMin.getSize().x > p24.breedte) ||
					(blokjeSleepMin.getLocation().y < 0) || 
					(blokjeSleepMin.getLocation().y + blokjeSleepMin.getSize().y > p24.hoogte))
			{
				raakSleepMin = false;
				blokjeSleepMin.setLocation((int) (blokjeMin.getLocation().x), (int) (blokjeMin.getLocation().y));
					
			}
			
			//blokjeSleepMin.repaint();
			p24.paint(eigenaar.heksGWTContext2d);
			laatstex = eventX;
			laatstey = eventY;

		} 
		else if (blokjeSleep.contains(eventX, eventY)) 
		{
			raakSleep = true;
		} 
		else if (blokjeSleepMin.contains(eventX, eventY)) 
		{
			raakSleepMin = true;
		}

		if (!plusEruit && 
			blokjeSleep.getLocation().x + blokjeSleep.getSize().x < za.getLocation().x + za.getSize().x && 
			blokjeSleep.getLocation().x > za.getLocation().x && 
			blokjeSleep.getLocation().y > za.getLocation().y && 
			blokjeSleep.getLocation().y + blokjeSleep.getSize().y < za.getLocation().y + za.getSize().y) 
		{ 
//GWT			
			//za.start(true, blokjeSleep.getLocation().x - za.getLocation().x);
			tc.verhoog();
			
			if (!erinMogelijk)
				blokjeSleep.setLocation(-100, -100);
			else
				blokjeSleep.setLocation((int) (blokjePlus.getLocation().x), (int) (blokjePlus.getLocation().y));
			raakSleep = false;
			pasEruit = false;
			
			p24.paint(eigenaar.heksGWTContext2d);
		}

		if (!minEruit && 
			blokjeSleepMin.getLocation().x + blokjeSleepMin.getSize().x < za.getLocation().x + za.getSize().x && 
			blokjeSleepMin.getLocation().x > za.getLocation().x && 
			blokjeSleepMin.getLocation().y > za.getLocation().y && 
			blokjeSleepMin.getLocation().y + blokjeSleepMin.getSize().y < za.getLocation().y + za.getSize().y) 
		{ 
//GWT
			//za.start(false, blokjeSleepMin.getLocation().x - za.getLocation().x);
			tc.verlaag();
			
			if (!erinMogelijk)
				blokjeSleepMin.setLocation(-100, -100);
			else
				blokjeSleepMin.setLocation((int) (blokjeMin.getLocation().x), (int) (blokjeMin.getLocation().y));
			raakSleepMin = false;
			pasEruit = false;
			
			p24.paint(eigenaar.heksGWTContext2d);
		}

		if (plusEruit && blokjeSleep.getLocation().y < za.getLocation().y) 
		{
			plusEruit = false;
			pasEruit = true;

			tc.verlaag();
		}
		if (minEruit && blokjeSleepMin.getLocation().y < za.getLocation().y) 
		{
			minEruit = false;
			pasEruit = true;

			tc.verhoog();
		}
		laatstex = eventX;
		laatstey = eventY;
	}

//	public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction() 
	{
		
//System.out.println("mouseUp action " + laatstex + " " + plusEruit);		
		
		raakSleep = false;
		raakSleepMin = false;
		if (instelbaar)
			tc.zetInstelbaar(true);

		if (!plusEruit && 
			blokjeSleep.getLocation().x + blokjeSleep.getSize().x < pot.getLocation().x + pot.getSize().x  && 
			blokjeSleep.getLocation().x > pot.getLocation().x  && 
			blokjeSleep.getLocation().y + blokjeSleep.getSize().y < za.getLocation().y + za.getSize().y
			) 
		{
			
			int x = blokjeSleep.getLocation().x;
			if (!erinMogelijk)
				blokjeSleep.setLocation(-100, -100);
			else
				blokjeSleep.setLocation((int) (blokjePlus.getLocation().x), (int) (blokjePlus.getLocation().y));

			za.start(true, laatstex - za.getLocation().x + blokjePlus.breedte / 2, laatstey + 3 * blokjePlus.hoogte);

			tc.verhoog();
			
			p24.paint(eigenaar.heksGWTContext2d);
			
		} 
		else 
		{
			if (!erinMogelijk)
				blokjeSleep.setLocation(-100, -100);
			else
				blokjeSleep.setLocation((int) (blokjePlus.getLocation().x), (int) (blokjePlus.getLocation().y));
			
			p24.paint(eigenaar.heksGWTContext2d);
		}

		if (!minEruit && 
			blokjeSleepMin.getLocation().x + blokjeSleepMin.getSize().x < pot.getLocation().x + pot.getSize().x && 
			blokjeSleepMin.getLocation().x > pot.getLocation().x && 
			blokjeSleepMin.getLocation().y + blokjeSleepMin.getSize().y < za.getLocation().y + za.getSize().y) 
		{
			int x = blokjeSleepMin.getLocation().x;
			if (!erinMogelijk)
				blokjeSleepMin.setLocation(-100, -100);
			else
				blokjeSleepMin.setLocation((int) (blokjeMin.getLocation().x), (int) (blokjeMin.getLocation().y));

			za.start(false, laatstex - za.getLocation().x + blokjeMin.breedte / 2, laatstey + 3 * blokjeMin.hoogte);
			tc.verlaag();
			
			p24.paint(eigenaar.heksGWTContext2d);
		} 
		else 
		{
			if (!erinMogelijk)
				blokjeSleepMin.setLocation(-100, -100);
			else
				blokjeSleepMin.setLocation((int) (blokjeMin.getLocation().x), (int) (blokjeMin.getLocation().y));
			
			p24.paint(eigenaar.heksGWTContext2d);
		}

		plusEruit = false;
		minEruit = false;
		pasEruit = false;
	}

	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		
		//public void mousePressed(MouseEvent e)
		public void onMouseDown(MouseDownEvent e)
		{
			e.preventDefault();
			
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDown = true;
			
			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		//public void mouseDragged(MouseEvent e)
		public void onMouseMove(MouseMoveEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
//System.out.println("onMouseMove");			
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();

//System.out.println("sp = " + shiftPressed);

			mouseMoveTouchMoveAction(eventX, eventY);
			
			
			
		} // onMouseMove
		
		//public void mouseReleased(MouseEvent e)
		public void onMouseUp(MouseUpEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			mouseUpTouchEndAction();

		}

	} //MLMML


	// tablet, dwo 
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				
				int eventX = touch.getPageX() - eigenaar.heksGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - eigenaar.heksGWTCanvas.getAbsoluteTop();				
				
				
				mouseDownTouchStartAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);

				int eventX = touch.getPageX() - eigenaar.heksGWTCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - eigenaar.heksGWTCanvas.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			
			mouseUpTouchEndAction();
		}

	}

}
