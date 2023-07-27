package fi.heksgwt.client;

//import java.awt.*;
//import java.applet.*;
//import java.awt.event.*;
import java.util.Map;

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

import com.google.gwt.user.client.ui.PopupPanel;

import fi.heksgwt.client.scobjects.*;
//import fi.beans.appletutil.*;
import fi.heksgwt.client.vectortek.*;

public class Pagina23OefenPanel //extends ScContainer //ScPanel implements MouseListener, MouseMotionListener, ActionListener 
{
	//private AppletUtil au;
	//HeksInteractiePanel heip;
	HeksGWT eigenaar;
	Pagina23Panel p23;
	
	//private ActionListener actionListener;
	//private AchtergrondContainer achtergrond;

	private Tekening blokjePlus, blokjeMin, blokjeSleep, blokjeSleepMin;
	Emmer emmerbinnen, emmerbuiten, emmerSleep;
	private Tekening pot, potinhoud, erinpijl, eruitpijl, vloer;
	
	ZinkAnimatieEmmer za;
	
	GetalComponent tc;
	//private ScPanel sleeppanel;
	private Polygon[] p;

	private int laatstex, laatstey;
	private boolean instelbaar;
	private boolean raakSleep, raakSleepMin;
	private boolean plusEruit, minEruit, pasEruit;
	private boolean[] kleurBlokjes = { true, false, true, true, true, true, false, false, false, false, true };

	boolean erinMogelijk, eruitMogelijk;
	private int aantalKerenGebruikt;
	int emmerInhoud;

	private boolean actief = true;
	
	boolean alleenErin = false;
	boolean alleenEruit = false;
	
	boolean mouseDown = false;
	
	int xPos, yPos;
	
	TekstPopup tf;

	public Pagina23OefenPanel(int x, int y, int b, int h, HeksGWT eigenaar, Pagina23Panel p23) 
	{
		//super(x, y, b, h);
		//this.heip = heip;
		this.eigenaar = eigenaar;
		this.p23 = p23;
		xPos = x;
		yPos = y;
		
		// setBackground(new Color(255,255,220));
		//setBackground(heip.bgColor);

		//au = new AppletUtil(applet);
		// plons = au.getAudioClip("resources/watersplash.au");
		// bubbel = au.getAudioClip("resources/bubble.au");

		// raakPlusBuiten = false;
		// raakPlusBinnen = false;
		// raakMinBuiten = false;
		// raakMinBinnen = false;
		raakSleep = false;
		plusEruit = false;
		minEruit = false;
		pasEruit = false;
		erinMogelijk = false;
		eruitMogelijk = false;
		instelbaar = false;

		// actief = false;
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
		
if (eigenaar.vloer23Map != null)		
{		vloer = new Tekening(xPos-10, yPos+430, 530, 175, eigenaar.vloer23Map);
		p23.add(vloer);
}		
else
System.out.println("vloer23 = null");

		pot = new Tekening(xPos+20, yPos+200, 480, 390, eigenaar.pot23Map);
//pot.naam = "pot";
		//achtergrond.add(pot);
		p23.add(pot);
		
		potinhoud = new Tekening(xPos+22, yPos+240+8, 475, 335, eigenaar.potinhoud23Map);
		//sleeppanel.add(potinhoud, 0);
		p23.add(potinhoud);

		za = new ZinkAnimatieEmmer(xPos+110, yPos+290, 210, 200, eigenaar,p23);
		//sleeppanel.add(za, 0);
		p23.add(za);
		
		emmerbinnen = new Emmer(xPos+120+15, yPos+320+15, 140, 155, eigenaar);
emmerbinnen.zetNaam("binnen");		
//GWT
		//emmerbinnen.addActionListener(this);
		//sleeppanel.add(emmerbinnen, 0);
		p23.add(emmerbinnen);

		emmerbuiten = new Emmer(xPos+450, yPos+25, 140, 155, eigenaar);
emmerbuiten.zetNaam("buiten");		
//GWT		
		//emmerbuiten.addActionListener(this);
		//sleeppanel.add(emmerbuiten);
		p23.add(emmerbuiten);

		emmerSleep = new Emmer(xPos+100, yPos+25, 140, 155, eigenaar);
emmerSleep.zetNaam("sleep");		
		//sleeppanel.add(emmerSleep, 0);
		emmerSleep.setVisible(false);
		p23.add(emmerSleep);

		tc = new GetalComponent(xPos+450+40, yPos+300+40, 150, 60, eigenaar);
		tc.zetAlsTemp(true);
		tc.zetWaarde(0);
		//sleeppanel.add(tc, 0);
		p23.add(tc);
//		sleeppanel.add(achtergrond);
//		add(sleeppanel);
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

	public void zetErinMogelijk() 
	{
		erinMogelijk = true;
		eruitMogelijk = false;
		//eruitpijl.setVisible(false);
		//erinpijl.setVisible(true);

	}

	public void zetEruitMogelijk() 
	{
		erinMogelijk = false;
		eruitMogelijk = true;
		//eruitpijl.setVisible(true);
		//erinpijl.setVisible(false);
	}

	public void zetOpnieuw() 
	{
//System.out.println("p23oefen zetOpnieuw");
//System.out.println("alleenErin " + alleenErin);
//System.out.println("alleenEruit " + alleenEruit);

//GWT		
		//za.stop();
		if (alleenEruit)
		{	emmerbinnen.setVisible(true);
			emmerbuiten.setVisible(false);
		}
		else if (alleenErin)
		{	emmerbuiten.setVisible(true);
			emmerbinnen.setVisible(false);
		}	
		else
		{
			emmerbuiten.setVisible(true);
			emmerbinnen.setVisible(true);
		}
		emmerSleep.setVisible(false);
		emmerbuiten.zetBegin();
		emmerbinnen.zetBegin();
		erinMogelijk = false;
		eruitMogelijk = false;
		//eruitpijl.setVisible(false);
		//erinpijl.setVisible(false);
		tc.zetWaarde(0);
//GWT?		
		// za.start();
	}

	public void stop() { // za.stop();
	}

	public void zetBeginTemp(int temp) 
	{
		tc.zetWaarde(temp);
	}

	public void zetInstelbaar(boolean b) 
	{
		instelbaar = b;
		tc.zetInstelbaar(b);
		if (b) 
		{
//GWT?			
			//tc.addActionListener(this);
		} 
		else 
		{
//GWT?			
			//tc.removeActionListener(this);
		}
	}

	public void zetActief(boolean b) 
	{
		actief = b;
		// if(!b)
		// { tc.zetBekend(b);
		// }
		emmerbinnen.zetInstelbaar(b);
		emmerbuiten.zetInstelbaar(b);
		tc.paint(eigenaar.heksGWTContext2d);
	}

	public void zetGebruikt(int aantal) 
	{
		aantalKerenGebruikt = aantal;
		tc.zetBekend(false);
		tc.paint(eigenaar.heksGWTContext2d);
	}

	public int geefEmmerInhoud() 
	{
		return emmerInhoud;
	}

	public int geefGebruikt() 
	{
		return aantalKerenGebruikt;
	}

	public int geefTemp() 
	{
		return tc.geefWaarde();
	}

/*
	public void addActionListener(ActionListener listener) 
	{
		actionListener = AWTEventMulticaster.add(actionListener, listener);
	}
*/
/*	
	public void removeActionListener(ActionListener listener) 
	{
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

		tf = new TekstPopup(gc, p23);
		tf.setText(gc.geefWaardeText());
		tf.setWidth("35px");
		tf.setHeight("20px");
		//tf.setModal(true);
		tf.setPopupPosition(popupX, popupY);
		tf.show();
//		tf.textBox.setFocus(true);
		tf.setFocus(true);
	}

	
	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		if (!actief)
		{	return;
		}
		
//System.out.println("mdts " + eventX + "," + eventY);

		laatstex = eventX;
		laatstey = eventY;
		
		if (emmerbuiten.raaktEtiket(eventX, eventY) && !erinMogelijk)
		{
			showTekstPopup(emmerbuiten.etiket);
//System.out.println("emmerbuiten.raaktEtiket && !erinMogelijk");			
		}

		if (emmerbinnen.raaktEtiket(eventX, eventY) && !eruitMogelijk)
		{
			
			showTekstPopup(emmerbinnen.etiket);
//System.out.println("emmerbinnen.raaktEtiket && !eruitMogelijk");			
		}

		if (emmerbuiten.raakt(eventX, eventY) && erinMogelijk) 
		{
			//emmerSleep.setLocation((int) (p23.schaal * 400), (int) (p23.schaal * 25));
			emmerSleep.setLocation(emmerbuiten.getLocation().x, emmerbuiten.getLocation().y);
			emmerSleep.setVisible(true);

			p23.paint(eigenaar.heksGWTContext2d);
//System.out.println("emmerbuiten.raakt && erinMogelijk");			
		}

		if (emmerbinnen.raakt(eventX, eventY) && eruitMogelijk) 
		{
			//emmerSleep.setLocation((int) (p23.schaal * 120), (int) (p23.schaal * 320));
			emmerSleep.setLocation(emmerbinnen.getLocation().x, emmerbinnen.getLocation().y);
			emmerSleep.setVisible(true);
			
			plusEruit = true;
			
			p23.paint(eigenaar.heksGWTContext2d);
//System.out.println("emmerbinnen.raakt && eruitMogelijk");			
		}

		if (emmerSleep.raakt(eventX, eventY)) 
		{
			raakSleep = true;
			
//System.out.println("emmersleep.raakt");			
		}

	}

	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{
		if (!actief)
			return;
		
//System.out.println("mmtm " + eventX + "," + eventY);		
		
		int dx = eventX - laatstex;
		int dy = eventY - laatstey;

		if (raakSleep) 
		{
			
//System.out.println("mmtm raakSleep " + eventX + "," + eventY);

			emmerSleep.setLocation(emmerSleep.getLocation().x + dx, emmerSleep.getLocation().y + dy);
			Polygon p = ((VulKrommeTek) (pot.to[2])).buigPolygon;
			int lx = 0; //pot.getLocation().x;
			int ly = 0; //pot.getLocation().y;
			for (int i = 0; i < p.geefAantalPunten(); i++) 
			{
				if (emmerSleep.raakt(p.geefPuntX(i) + lx, p.geefPuntY(i) + ly)) 
				{
					emmerSleep.setLocation(emmerSleep.getLocation().x - dx, emmerSleep.getLocation().y - dy);
					raakSleep = false;
				}
			}
			
			if ((emmerSleep.getLocation().x < 0) || 
					(emmerSleep.getLocation().x + emmerSleep.getSize().x > p23.breedte) ||
					(emmerSleep.getLocation().y < 0) || 
					(emmerSleep.getLocation().y + emmerSleep.getSize().y > p23.hoogte))
			{
				raakSleep = false;
				if (erinMogelijk)
					emmerSleep.setLocation(emmerbuiten.getLocation().x, emmerbuiten.getLocation().y);
				if (eruitMogelijk)
					emmerSleep.setLocation(emmerbinnen.getLocation().x, emmerbinnen.getLocation().y);
				
			}
			
			//emmerSleep.repaint();
			p23.paint(eigenaar.heksGWTContext2d);
		}

		else if (emmerSleep.raakt(eventX, eventY)) 
		{
//System.out.println("mmtm emsraakt");			
			raakSleep = true;
		}

		if (!plusEruit && 
			emmerSleep.getLocation().x + emmerSleep.getSize().x < za.getLocation().x + za.getSize().x && 
			emmerSleep.getLocation().x > za.getLocation().x && 
			emmerSleep.getLocation().y > za.getLocation().y && 
			emmerSleep.getLocation().y + emmerSleep.getSize().y < za.getLocation().y + za.getSize().y) 
		{ 
			
//GWT			
			//za.start(true, emmerSleep.getLocation().x - za.getLocation().x);
			
			tc.verhoog(emmerInhoud);

			p23.emmerAction(true);
//action in emmerPanel			
//GWT?			
			//if (actionListener != null) 
			//{
			//	actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "erin"));
			//}
			if (!erinMogelijk)
				emmerSleep.setLocation(-150, -150);
			else
				emmerSleep.setLocation(emmerbuiten.getLocation().x, emmerbuiten.getLocation().y);
			raakSleep = false;
			pasEruit = false;
			
			p23.paint(eigenaar.heksGWTContext2d);
		}

		if (eruitMogelijk && plusEruit && emmerSleep.getLocation().y < za.getLocation().y) 
		{
			plusEruit = false;
			pasEruit = true;
			tc.verlaag(emmerInhoud);

			p23.emmerAction(false);
//action in emmerPanel			
//GWT?			
			//if (actionListener != null) 
			//{
			//	actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "eruit"));
			//}
			
			p23.paint(eigenaar.heksGWTContext2d);
		}

		laatstex = eventX;
		laatstey = eventY;
	}
	
	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction()
	{
		
System.out.println("mute");

		if (!erinMogelijk && !eruitMogelijk)
			return;
		
		raakSleep = false;
		if (instelbaar)
			tc.zetInstelbaar(true);

		if (!plusEruit && 
			emmerSleep.getLocation().x + emmerSleep.getSize().x < pot.getLocation().x + pot.getSize().x && 
			emmerSleep.getLocation().x > pot.getLocation().x && 
			emmerSleep.getLocation().y + emmerSleep.getSize().y < za.getLocation().y + za.getSize().y) 
		{
			int x = emmerSleep.getLocation().x;
			//emmerSleep.setLocation(-150, -150);
			if (pasEruit)
			{	emmerSleep.setLocation(emmerbinnen.getLocation().x, emmerbinnen.getLocation().y);
System.out.println("mute pasEruit");			
			}
			else
			{	emmerSleep.setLocation(emmerbuiten.getLocation().x, emmerbuiten.getLocation().y);
System.out.println("mute !pasEruit");			
			}
			
			
			
			za.start(laatstex - za.getLocation().x - emmerbuiten.breedte / 2,laatstey);
			tc.verhoog(emmerInhoud);
			
			p23.emmerAction(true);
//GWT action in emmerPanel
			
			//if (actionListener != null) 
			//{
			//	actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "erin"));
			//}
			p23.paint(eigenaar.heksGWTContext2d);
		} 
		else 
		{ 	
//GWT visible??			
			//emmerSleep.setLocation(-150, -150);
			if (eruitMogelijk)
				emmerSleep.setLocation(emmerbinnen.getLocation().x, emmerbinnen.getLocation().y);
			else if (erinMogelijk)
				emmerSleep.setLocation(emmerbuiten.getLocation().x, emmerbuiten.getLocation().y);
			p23.paint(eigenaar.heksGWTContext2d);
		}

		plusEruit = false;
		minEruit = false;
		pasEruit = false;
	}

	
	public void instellingAction(GetalComponent gc)
	{
		if (gc == emmerbuiten.etiket) 
		{
			zetErinMogelijk();
			emmerbuiten.zetInhoud(emmerbuiten.etiket.geefWaarde());
			emmerInhoud = emmerbuiten.geefInhoud();
			emmerbinnen.setVisible(false);
			emmerSleep.zetInhoud(emmerInhoud);
			
			za.zetInhoud(emmerInhoud);
			//za.start();

		} 
		else if (gc == emmerbinnen.etiket) 
		{
			zetEruitMogelijk();
			emmerbinnen.zetInhoud(emmerbinnen.etiket.geefWaarde());
			emmerInhoud = emmerbinnen.geefInhoud();
			emmerbuiten.setVisible(false);
			emmerSleep.zetInhoud(emmerInhoud);
			
			za.zetInhoud(emmerInhoud);
			//za.start();
		}
		
		p23.paint(eigenaar.heksGWTContext2d);
		
	}
//GWT
/*	
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == emmerbuiten) 
		{
			zetErinMogelijk();
			emmerInhoud = emmerbuiten.geefInhoud();
			emmerbinnen.setVisible(false);
			emmerSleep.zetInhoud(emmerInhoud);
			za.zetInhoud(emmerInhoud);
			za.start();

		} 
		else if (e.getSource() == emmerbinnen) 
		{
			zetEruitMogelijk();
			emmerInhoud = emmerbinnen.geefInhoud();
			emmerbuiten.setVisible(false);
			emmerSleep.zetInhoud(emmerInhoud);
			za.zetInhoud(emmerInhoud);
			za.start();
		}
		if (actionListener != null) 
		{
			actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "instelling"));
		}

	}
*/	
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
