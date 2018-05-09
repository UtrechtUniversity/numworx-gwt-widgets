package fi.heksgwt.client;

import java.util.Map;
import java.util.HashMap;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

import fi.heksgwt.client.scobjects.ScContainer;
import fi.heksgwt.client.scobjects.ScLabel;
import fi.heksgwt.client.vectortek.Tekening;
import fi.heksgwt.client.vectortek.Polygon;
import fi.heksgwt.client.vectortek.VeelhoekTek;
import fi.heksgwt.client.vectortek.VulKrommeTek;

import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Pagina21 is het complete panel om blokjes erin te doen.
 * In instellingenpanel docentomgeving 'blokjes erin', in HeksGWT paginanummer 1. 
 * 
 * @author borku102
 *
 */
public class Pagina21Panel extends ScContainer 
{
	HeksGWT eigenaar;

	//private AchtergrondContainer achtergrond;

	Tekening blokjePlus, blokjeMin, blokjeSleep, blokjeSleepMin;
	Tekening pot, potEruit, potErin, beginPot, eindPot, potinhoud, vloer, schrijfheks, werkheks;
	
	ZinkAnimatie za;
	GetalComponent tc, beginTemp, eindTemp;
	Thermometer tm;
	BlokjesContainer eruitContainer, erinContainer;
	ScLabel beginLabel, eindLabel, erinLabel, eruitLabel;
	
	//ScPanel sleeppanel;
	Polygon[] p;

	int laatstex, laatstey;
	boolean raakPlusBuiten, raakPlusBinnen, raakMinBuiten, raakMinBinnen, raakSleep, raakSleepMin;
	boolean plusEruit, minEruit, pasEruit;
	boolean[] kleurBlokjes = { true, false, true, true, true, true, false, false, false, false, true };

	//ImageButton opnieuwKnop;
	PushButton opnieuwKnop;
	TekstPopup tf;
	
	boolean mouseDown;

	public Pagina21Panel(int x, int y, int b, int h, HeksGWT heksGWT) 
	{
		super(x, y, b, h);
		eigenaar = heksGWT;
		
		refresh = true;

		raakPlusBuiten = false;
		raakPlusBinnen = false;
		raakMinBuiten = false;
		raakMinBinnen = false;
		raakSleep = false;
		plusEruit = false;
		minEruit = false;
		pasEruit = false;

		vloer = new Tekening(340, 480, 430, 175, eigenaar.vloerMap);
		add(vloer);

		pot = new Tekening(370, 310, 380, 330, eigenaar.potMap);
		add(pot);
		
		potinhoud = new Tekening(370, 335, 375, 300, eigenaar.potinhoudMap);
		add(potinhoud);
		
		beginTemp = new GetalComponent(170, 100, 80, 40, eigenaar);
		beginTemp.zetInstelbaar(true);
		beginTemp.zetAlsTemp(true);
		add(beginTemp);

		beginPot = new Tekening(35, 85, 90, 65, eigenaar.beginEindPotMap);
		add(beginPot);
		
		beginLabel = new ScLabel(50, 105, 60, 30, "begin");
		//activeert de getalcompoenent met begintemp
		add(beginLabel);

		potErin = new Tekening(30, 170, 100, 90, eigenaar.potErinMap);
		add(potErin);
		
		erinLabel = new ScLabel(65, 215, 60, 30, "erin");
		add(erinLabel);
		
		eindTemp = new GetalComponent(170, 290, 80, 40, eigenaar);
		eindTemp.zetAlsTemp(true);
		add(eindTemp);


		eindPot = new Tekening(35, 285, 90, 65, eigenaar.beginEindPotMap);
		add(eindPot);
		
		eindLabel = new ScLabel(50, 305, 60, 30, "eind");
		add(eindLabel);
		

		erinContainer = new BlokjesContainer(180, 190, 165, 90, eigenaar);
		erinContainer.zetMaxRijen(2);
		add(erinContainer);
		blokjePlus = new Tekening(700, 110, 65, 65, eigenaar.blokjePlusMap);
		add(blokjePlus);

		blokjeMin = new Tekening(700, 180, 65, 65, eigenaar.blokjeMinMap);
		add(blokjeMin);


		za = new ZinkAnimatie(450, 320, 220, 200, eigenaar, this);
		add(za);

		blokjeSleep = new Tekening(700, 110, 65, 65, eigenaar.blokjePlusMap);
		add(blokjeSleep);

		blokjeSleepMin = new Tekening(700, 180, 65, 65, eigenaar.blokjeMinMap);
		add(blokjeSleepMin);

		tc = new GetalComponent(720, 390, 100, 40, eigenaar);
		tc.zetAlsTemp(true);
		add(tc, 0);
		

		tm = new Thermometer(450, 30, 55, 400, eigenaar.heksGWTContext2d);
		add(tm);
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
	
	public void initOpnieuwKnop()
	{
		int knopY = eindPot.getLocation().y + eindPot.getSize().y + 30; 
		opnieuwKnop = new PushButton(eigenaar.rb.opnieuwKnopLabel());
		eigenaar.dlp.add(opnieuwKnop);
		eigenaar.dlp.setWidgetLeftWidth(opnieuwKnop, 20, Style.Unit.PX, 65, Style.Unit.PX);
		eigenaar.dlp.setWidgetTopHeight(opnieuwKnop, knopY, Style.Unit.PX, 25, Style.Unit.PX);
		
		opnieuwKnop.addClickHandler(new PushClickHandler());
	}

	public void setState(Map map) 
	{
		if (map==null)
			return;

		ObjectMap h = JSONUtilities.wrapMap(map);

		int begintemp = 0; 
		if (h.containsKey("begintemp"))
			begintemp = h.getInt("begintemp");
		beginTemp.zetWaarde(begintemp);
		erinContainer.removeAll();
		
		int blokjespluserin = 0;
		if (h.containsKey("blokjespluserin"))
			blokjespluserin = h.getInt("blokjespluserin");
		for (int plusInCnt = 0; plusInCnt < blokjespluserin; plusInCnt++)
		{	
			erinContainer.voegBlokjeToe(true);
		}
		int blokjesminerin = 0;
		if (h.containsKey("blokjesminerin"))
			blokjesminerin = h.getInt("blokjesminerin");
		for (int minInCnt = 0; minInCnt < blokjesminerin; minInCnt++)
		{
			erinContainer.voegBlokjeToe(false);
		}
		
		int eindtemp = 0; 
		if (h.containsKey("eindtemp"))
			eindtemp = h.getInt("eindtemp");
		eindTemp.zetWaarde(eindtemp);
		tc.zetWaarde(eindtemp);
		
		tm.zetTemp(eindtemp);
		
		paint(eigenaar.heksGWTContext2d);
	}

	public HashMap getState() 
	{
		HashMap h = new HashMap();
		
		h.put("begintemp", new Integer(beginTemp.geefWaarde()));
		h.put("blokjespluserin", new Integer(erinContainer.getalPlus.geefWaarde()));
		h.put("blokjesminerin", new Integer(erinContainer.getalMin.geefWaarde()));
		h.put("eindtemp", new Integer(eindTemp.geefWaarde()));

		return h;
	}

	public void start() 
	{
	}

	public void stop() 
	{
	}

	public void showTekstPopup(GetalComponent gc)
	{
		int popupX = gc.xPos + eigenaar.heksGWTCanvas.getAbsoluteLeft();
		
		int popupY = gc.yPos + gc.hoogte + eigenaar.heksGWTCanvas.getAbsoluteTop();
		
		if ((tf != null) && tf.isVisible())
		{
			//zetInvulWaarde();
		}

		tf = new TekstPopup(gc, this);
		tf.setText(gc.geefWaardeText());
		tf.setWidth("35px");
		tf.setHeight("20px");
		//tf.setModal(true);
		tf.setPopupPosition(popupX, popupY);
		tf.show();
		tf.setFocus(true);
		tf.setSelected();
	}

	public void mouseDownTouchStartAction(int eventX, int eventY) 
	{
		laatstex = eventX;
		laatstey = eventY;

		if (beginLabel.contains(eventX, eventY)) 
		{
			showTekstPopup(beginTemp);
		}
		if (beginTemp.contains(eventX, eventY)) 
		{
			showTekstPopup(beginTemp);			
		}

		// dit is voor eruithalen?		
		p = new Polygon[11];
		for (int i = 0; i < 11; i++) 
		{
			p[i] = ((VeelhoekTek) (potinhoud.to[i])).basisPolygon;
		}

		if (blokjePlus.contains(eventX, eventY)) 
		{
			raakPlusBuiten = true;
		}

		if (blokjeSleep.contains(eventX, eventY)) 
		{
			raakSleep = true;
		}

		if (blokjeSleepMin.contains(eventX, eventY)) 
		{
			raakSleepMin = true;
		}
	}

	public void mouseMoveTouchMoveAction(int eventX, int eventY) 
	{
		int dx = eventX - laatstex;
		int dy = eventY - laatstey;

		if (raakSleep) 
		{
			blokjeSleep.verplaats(dx, dy);
			
			Polygon p = ((VulKrommeTek) (pot.to[2])).buigPolygon;
			int lx = 0;
			int ly = 0;
			for (int i = 0; i < p.geefAantalPunten(); i++) 
			{
				if (blokjeSleep.contains(p.geefPuntX(i) + lx, p.geefPuntY(i) + ly)) 
				{
					blokjeSleep.verplaats(-dx, -dy);
					raakSleep = false;
				}
			}
			
			if ((blokjeSleep.getLocation().x < 0) || 
					(blokjeSleep.getLocation().x + blokjeSleep.getSize().x > breedte) ||
					(blokjeSleep.getLocation().y < 0) || 
					(blokjeSleep.getLocation().y + blokjeSleep.getSize().y > hoogte))
			{
				raakSleep = false;
				blokjeSleep.setLocation((int) (blokjePlus.getLocation().x), (int) (blokjePlus.getLocation().y));
			}
			
			paint(eigenaar.heksGWTContext2d);
			laatstex = eventX;
			laatstey = eventY;

		}
		if (raakSleepMin) 
		{
			blokjeSleepMin.verplaats(dx, dy);
			
			Polygon p = ((VulKrommeTek) (pot.to[2])).buigPolygon;
			int lx = 0;
			int ly = 0;
			for (int i = 0; i < p.geefAantalPunten(); i++) 
			{
				if (blokjeSleepMin.contains(p.geefPuntX(i) + lx, p.geefPuntY(i) + ly)) 
				{
					blokjeSleepMin.verplaats(-dx, -dy);
					raakSleepMin = false;
				}
			}
			
			if ((blokjeSleepMin.getLocation().x < 0) || 
					(blokjeSleepMin.getLocation().x + blokjeSleepMin.getSize().x > breedte) ||
					(blokjeSleepMin.getLocation().y < 0) || 
					(blokjeSleepMin.getLocation().y + blokjeSleepMin.getSize().y > hoogte))
			{
				raakSleepMin = false;
				blokjeSleepMin.setLocation((int) (blokjeMin.getLocation().x), (int) (blokjeMin.getLocation().y));
			}

			paint(eigenaar.heksGWTContext2d);
			laatstex = eventX;
			laatstey = eventY;
		}

		if (!plusEruit && 
				blokjeSleep.getLocation().x + blokjeSleep.getSize().x < za.getLocation().x + za.getSize().x && 
				blokjeSleep.getLocation().x > za.getLocation().x && 
				blokjeSleep.getLocation().y > za.getLocation().y && 
				blokjeSleep.getLocation().y + blokjeSleep.getSize().y < za.getLocation().y + za.getSize().y) 
		{
			tc.verhoog();
			tm.tempPlus();
			eindTemp.verhoog();
			
			if (!pasEruit)
				erinContainer.voegBlokjeToe(true);
				
			blokjeSleep.setLocation((int) (blokjePlus.getLocation().x), (int) (blokjePlus.getLocation().y));
			raakSleep = false;
			pasEruit = false;
			
			paint(eigenaar.heksGWTContext2d);
		}

		if (!minEruit && 
				blokjeSleepMin.getLocation().x + blokjeSleepMin.getSize().x < za.getLocation().x + za.getSize().x && 
				blokjeSleepMin.getLocation().x > za.getLocation().x && 
				blokjeSleepMin.getLocation().y > za.getLocation().y && 
				blokjeSleepMin.getLocation().y + blokjeSleepMin.getSize().y < za.getLocation().y + za.getSize().y) 
		{
			tc.verlaag();
			tm.tempMin();
			eindTemp.verlaag();
			
			if (!pasEruit)
				erinContainer.voegBlokjeToe(false);
				
			blokjeSleepMin.setLocation((int) (blokjeMin.getLocation().x), (int) (blokjeMin.getLocation().y));
			raakSleepMin = false;
			pasEruit = false;
			
			paint(eigenaar.heksGWTContext2d);
		}

		if (plusEruit && blokjeSleep.getLocation().y < za.getLocation().y) 
		{
			plusEruit = false;
			pasEruit = true;
		}
		if (minEruit && blokjeSleepMin.getLocation().y < za.getLocation().y) 
		{
			minEruit = false;
			pasEruit = true;
		}
	}

	public void mouseUpTouchEndAction() 
	{
		raakSleep = false;
		raakSleepMin = false;

		if (!plusEruit && 
			blokjeSleep.getLocation().x + blokjeSleep.getSize().x < pot.getLocation().x + pot.getSize().x && 
			blokjeSleep.getLocation().x > pot.getLocation().x && 
			blokjeSleep.getLocation().y + blokjeSleep.getSize().y < za.getLocation().y + za.getSize().y) 
		{
			int x = blokjeSleep.getLocation().x;
			int y = blokjeSleep.getLocation().y;
			blokjeSleep.setLocation((int) (blokjePlus.getLocation().x), (int) (blokjePlus.getLocation().y));
			
			za.start(true, laatstex - za.getLocation().x, laatstey);
			tc.verhoog();
			tm.tempPlus();
			eindTemp.verhoog();
			
			if (!pasEruit)
				erinContainer.voegBlokjeToe(true);
			
			paint(eigenaar.heksGWTContext2d);
		} 
		else 
		{
			blokjeSleep.setLocation((int) (blokjePlus.getLocation().x), (int) (blokjePlus.getLocation().y));
			paint(eigenaar.heksGWTContext2d);
		}

		if (!minEruit && 
			blokjeSleepMin.getLocation().x + blokjeSleepMin.getSize().x < pot.getLocation().x + pot.getSize().x && 
			blokjeSleepMin.getLocation().x > pot.getLocation().x && 
			blokjeSleepMin.getLocation().y + blokjeSleepMin.getSize().y < za.getLocation().y + za.getSize().y) 
		{
			int x = blokjeSleepMin.getLocation().x;
			blokjeSleepMin.setLocation((int) (blokjeMin.getLocation().x), (int) (blokjeMin.getLocation().y));

			za.start(false, laatstex - za.getLocation().x, laatstey);
			tc.verlaag();
			tm.tempMin();
			eindTemp.verlaag();
			
			if (!pasEruit)
				erinContainer.voegBlokjeToe(false);

			paint(eigenaar.heksGWTContext2d);
		} 
		else 
		{
			blokjeSleepMin.setLocation((int) (blokjeMin.getLocation().x), (int) (blokjeMin.getLocation().y));
			paint(eigenaar.heksGWTContext2d);
		}

		plusEruit = false;
		minEruit = false;
		pasEruit = false;
	}


	
	public void opnieuwAction() 
	{
		if (beginTemp.isBekend()) 
		{
			eindTemp.zetBekend(true);
			tc.zetBekend(true);
			eindTemp.zetWaarde(beginTemp.geefWaarde());
			tc.zetWaarde(beginTemp.geefWaarde());
			tm.zetTemp(beginTemp.geefWaarde());
		} 
		else 
		{
			eindTemp.zetBekend(false);
			eindTemp.paint();
			tc.zetBekend(false);
			tc.paint();
			tm.zetTemp(0);
		}

		erinContainer.removeAll();
		erinContainer.paint();
	}

	
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
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
		
		public void onMouseMove(MouseMoveEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();

			mouseMoveTouchMoveAction(eventX, eventY);
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			mouseUpTouchEndAction();
		}
	} //MLMML


	/**
	 * tablet, dwo
	 */
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
	
    class PushClickHandler implements ClickHandler
	{
	   	public void onClick(ClickEvent e)
	   	{
			e.stopPropagation();
	   		
	   		if (e.getSource() == opnieuwKnop)
	   		{
	   			opnieuwAction();    			
	   		}
	   	}
	}
}
