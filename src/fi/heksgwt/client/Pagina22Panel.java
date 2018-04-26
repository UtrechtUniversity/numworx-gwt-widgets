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
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.PopupPanel;

import fi.heksgwt.client.Pagina21Panel.MouseHandler;
import fi.heksgwt.client.Pagina21Panel.PushClickHandler;
import fi.heksgwt.client.Pagina21Panel.TouchHandler;
import fi.heksgwt.client.scobjects.ScContainer;
import fi.heksgwt.client.scobjects.ScLabel;
import fi.heksgwt.client.vectortek.Tekening;
import fi.heksgwt.client.vectortek.Polygon;
import fi.heksgwt.client.vectortek.VeelhoekTek;
import fi.heksgwt.client.vectortek.VulKrommeTek;


public class Pagina22Panel extends ScContainer //Panel implements MouseListener, MouseMotionListener, ActionListener 
{
	HeksGWT eigenaar;
	
	//private AchtergrondContainer achtergrond;

	Tekening blokjePlus, blokjeMin, blokjeSleep, blokjeSleepMin;
	Tekening pot, potEruit, potErin, beginPot, eindPot, potinhoud, vloer, schrijfheks, werkheks;
	ZinkAnimatie za;
	GetalComponent tc, beginTemp, eindTemp;
	Thermometer tm;
	BlokjesContainer eruitContainer, erinContainer;
	ScLabel beginLabel, eindLabel, erinLabel, eruitLabel, titel, opdrachtTitel;

	//ScPanel sleeppanel;
	Polygon[] p;

	int laatstex, laatstey;
	boolean raakPlusBuiten, raakPlusBinnen, raakMinBuiten, raakMinBinnen, raakSleep, raakSleepMin;
	boolean plusEruit, minEruit, pasEruit;
	boolean[] kleurBlokjes = { true, false, true, true, true, true, false, false, false, false, true };

//GWT
	//ImageButton opnieuwKnop;
	PushButton opnieuwKnop;
	TekstPopup tf;
	
	boolean mouseDown;

	public Pagina22Panel(int x, int y, int b, int h, HeksGWT heksGWT) 
	{
		super(x, y, b, h);
		eigenaar = heksGWT;

		// setBackground(new Color(255,255,220));
		//setBackground(heip.bgColor);

//GWT?		
		//zetVastePlaats(true);

		//GWT		
		//Image opnieuwknop = null;
		//if (Heks.rb.getLocale().getLanguage().equals("nl")) 
		//{
		//	opnieuwknop = heip.opnieuwNLImage; //au.getImage("resources/opnieuwknop.gif");
		//} 
		//else 
		//{
			//opnieuwknop = heip.opnieuwENImage; //au.getImage("resources/againKnop.gif");
		//}

//GWT(4)
		//opnieuwKnop = new ImageButton(opnieuwknop);
		//opnieuwKnop.setBounds(20, 300, 150, 24);
		//opnieuwKnop.addActionListener(this);
		//add(opnieuwKnop);


		raakPlusBuiten = false;
		raakPlusBinnen = false;
		raakMinBuiten = false;
		raakMinBinnen = false;
		raakSleep = false;
		plusEruit = false;
		minEruit = false;
		pasEruit = false;


		//sleeppanel = new ScPanel(350, 30, b - 320, h - 5);
		//sleeppanel.setBackground(Color.white);
		//sleeppanel.addMouseListener(this);
		//sleeppanel.addMouseMotionListener(this);
		
		//achtergrond = new AchtergrondContainer(0, 0, b - 300, h - 5);

		// x plus 350, y plus 30
		//vloer = new Tekening(-10, 450, 430, 175, heip, "vloer.gif", true);
		vloer = new Tekening(340, 480, 430, 175, eigenaar.vloerMap);
		add(vloer);

		// x plus 350, y plus 30
		//pot = new Tekening(20, 280, 380, 330, heip, "potnieuw.gif", true);
		pot = new Tekening(370, 310, 380, 330, eigenaar.potMap);
		add(pot);
		
		// x plus 350, y plus 30
		//potinhoud = new Tekening(20, 305, 375, 300, heip, "inhoudnieuw.gif", true);
		potinhoud = new Tekening(370, 335, 375, 300, eigenaar.potinhoudMap);
		add(potinhoud);


		beginTemp = new GetalComponent(170, 70, 80, 40, eigenaar);
		beginTemp.zetInstelbaar(true);
		beginTemp.zetAlsTemp(true);
		//beginTemp.addActionListener(this);
		add(beginTemp);

		beginPot = new Tekening(35, 55, 90, 65, eigenaar.beginEindPotMap);
		add(beginPot);
		
		beginLabel = new ScLabel(50, 75, 60, 30, "begin");
		//activeert de getalcompoenent met begintemp
		//beginLabel.addMouseListener(this);
		add(beginLabel);

		potErin = new Tekening(30, 140, 100, 90, eigenaar.potErinMap);
		add(potErin);
		
		erinLabel = new ScLabel(65, 185, 60, 30, "erin");
		add(erinLabel);

		potEruit = new Tekening(30, 250, 100, 90, eigenaar.potEruitMap);
		add(potEruit);

		eruitLabel = new ScLabel(35, 295, 60, 30, "eruit");
		add(eruitLabel);

		eindTemp = new GetalComponent(170, 375, 80, 40, eigenaar);
		eindTemp.zetAlsTemp(true);
		add(eindTemp);

		eindPot = new Tekening(35, 360, 90, 65, eigenaar.beginEindPotMap);
		add(eindPot);

		eindLabel = new ScLabel(50, 380, 60, 30, "eind");
		add(eindLabel);

		erinContainer = new BlokjesContainer(180, 150, 165, 90, eigenaar);
		erinContainer.zetMaxRijen(2);
		add(erinContainer);

		eruitContainer = new BlokjesContainer(180, 260, 165, 90, eigenaar);
		eruitContainer.zetMaxRijen(2);
		add(eruitContainer);

		blokjePlus = new Tekening(700, 110, 65, 65, eigenaar.blokjePlusMap);
		//achtergrond.add(blokjePlus);
		add(blokjePlus);

		blokjeMin = new Tekening(700, 180, 65, 65, eigenaar.blokjeMinMap);
		//achtergrond.add(blokjeMin);
		add(blokjeMin);

		za = new ZinkAnimatie(450, 320, 220, 200, eigenaar, this);
		//sleeppanel.add(za, 0);
		add(za, 0);

		blokjeSleep = new Tekening(700, 110, 65, 65, eigenaar.blokjePlusMap);
		//sleeppanel.add(blokjeSleep, 0);
		add(blokjeSleep);

		blokjeSleepMin = new Tekening(700, 180, 65, 65, eigenaar.blokjeMinMap);
		//sleeppanel.add(blokjeSleepMin, 0);
		add(blokjeSleepMin);

		tc = new GetalComponent(720, 390, 100, 40, eigenaar);
		tc.zetAlsTemp(true);
		//sleeppanel.add(tc, 0);
		add(tc, 0);

		tm = new Thermometer(450, 30, 55, 400,eigenaar.heksGWTContext2d);
		//sleeppanel.add(tm, 0);
		add(tm);

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
		
System.out.println("p22 setState");
		ObjectMap h = JSONUtilities.wrapMap(map);

		int begintemp = 0; 
		if (h.containsKey("begintemp"))
			begintemp = h.getInt("begintemp");
		beginTemp.zetWaarde(begintemp);
		
		erinContainer.removeAll();
		eruitContainer.removeAll();
		
		int blokjespluserin = 0;
		if (h.containsKey("blokjespluserin"))
			blokjespluserin = h.getInt("blokjespluserin");
		for (int plusInCnt = 0; plusInCnt < blokjespluserin; plusInCnt++)
			erinContainer.voegBlokjeToe(true);
		int blokjesminerin = 0;
		if (h.containsKey("blokjesminerin"))
			blokjesminerin = h.getInt("blokjesminerin");
		for (int minInCnt = 0; minInCnt < blokjesminerin; minInCnt++)
			erinContainer.voegBlokjeToe(false);
		int blokjespluseruit = 0;
		if (h.containsKey("blokjespluseruit"))
			blokjespluseruit = h.getInt("blokjespluseruit");
		for (int plusInCnt = 0; plusInCnt < blokjespluseruit; plusInCnt++)
			eruitContainer.voegBlokjeToe(true);
		int blokjesmineruit = 0;
		if (h.containsKey("blokjesmineruit"))
			blokjesmineruit = h.getInt("blokjesmineruit");
		for (int minInCnt = 0; minInCnt < blokjesmineruit; minInCnt++)
			eruitContainer.voegBlokjeToe(false);
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
System.out.println("p22 getState");		
		HashMap h = new HashMap();

		
		h.put("begintemp", new Integer(beginTemp.geefWaarde()));
		h.put("blokjespluserin", new Integer(erinContainer.getalPlus.geefWaarde()));
		h.put("blokjesminerin", new Integer(erinContainer.getalMin.geefWaarde()));
		h.put("blokjespluseruit", new Integer(eruitContainer.getalPlus.geefWaarde()));
		h.put("blokjesmineruit", new Integer(eruitContainer.getalMin.geefWaarde()));
		h.put("eindtemp", new Integer(eindTemp.geefWaarde()));

		return h;
	}

	public void start() 
	{
//GWT
/*		
		beginTemp.zetWaarde(0);
		eindTemp.zetWaarde(0);
		tc.zetWaarde(0);
		tm.zetTemp(0);
		erinContainer.removeAll();
		eruitContainer.removeAll();
		za.start();
(*/		
	}

	public void stop() 
	{
//GWT		
		//za.stop();
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
//		tf.textBox.setFocus(true);
		tf.setFocus(true);
	}

	public void mouseDownTouchStartAction(int eventX, int eventY)
	{
		laatstex = eventX;
		laatstey = eventY;
		
		if (beginLabel.contains(eventX, eventY)) 
		{	
			showTekstPopup(beginTemp);
			//beginTemp.vulIn();
		}

		if (beginTemp.contains(eventX, eventY)) 
		{
			showTekstPopup(beginTemp);
			//beginTemp.vulIn();
		}
		
		
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
//System.out.println("blokjeSleep contains");
//System.out.println("blokjeSleep x = " + blokjeSleep.getLocation().x);
//System.out.println("blokjeSleep y = " + blokjeSleep.getLocation().y);
//System.out.println("blokjeSleep b = " + blokjeSleep.getSize().x);
//System.out.println("blokjeSleep h = " + blokjeSleep.getSize().y);
			
			raakSleep = true;
		}

		if (blokjeSleepMin.contains(eventX, eventY)) 
		{
//System.out.println("blokjeSleepMin contains");
//System.out.println("blokjeSleepMin x = " + blokjeSleepMin.getLocation().x);
//System.out.println("blokjeSleepMin y = " + blokjeSleepMin.getLocation().y);
			
			raakSleepMin = true;
		}
		//int x = e.getX() - potinhoud.getLocation().x;
		//int y = e.getY() - potinhoud.getLocation().y;

		for (int i = 10; i > -1; i--) 
		{
			//if (p[i].contains(x, y))
			if (p[i].contains(eventX, eventY))
			{
System.out.println("p[" + i + "] contains");

				if (kleurBlokjes[i]) 
				{
System.out.println("kleurBlokjes[" + i + "]");					
					blokjeSleep.setLocation(eventX - blokjeSleep.breedte / 2, eventY - blokjeSleep.hoogte / 2);
					//blokjeSleep.repaint();
					plusEruit = true;
					paint(eigenaar.heksGWTContext2d);
				} 
				else 
				{
System.out.println("not kleurBlokjes[" + i + "]");					
					blokjeSleepMin.setLocation(eventX - blokjeSleep.breedte / 2, eventY - blokjeSleep.hoogte / 2);
					//blokjeSleepMin.repaint();
					minEruit = true;
					paint(eigenaar.heksGWTContext2d);
				}

				return;
			}
		}

	}

	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{

		int dx = eventX - laatstex;
		int dy = eventY - laatstey;

		if (raakSleep) 
		{
			//blokjeSleep.setLocation(blokjeSleep.getLocation().x + dx, blokjeSleep.getLocation().y + dy);
			blokjeSleep.verplaats(dx, dy);
			
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
				(blokjeSleep.getLocation().x + blokjeSleep.getSize().x > breedte) ||
				(blokjeSleep.getLocation().y < 0) || 
				(blokjeSleep.getLocation().y + blokjeSleep.getSize().y > hoogte))
			{
				raakSleep = false;
				blokjeSleep.setLocation((int) (blokjePlus.getLocation().x), (int) (blokjePlus.getLocation().y));
				
			}
			
			//blokjeSleep.repaint();
			paint(eigenaar.heksGWTContext2d);
			laatstex = eventX;
			laatstey = eventY;
		}
		if (raakSleepMin) 
		{
			blokjeSleepMin.setLocation(blokjeSleepMin.getLocation().x + dx, blokjeSleepMin.getLocation().y + dy);
			Polygon p = ((VulKrommeTek) (pot.to[2])).buigPolygon;
			int lx = 0; //pot.getLocation().x;
			int ly = 0; //pot.getLocation().y;
			for (int i = 0; i < p.geefAantalPunten(); i++) 
			{
				if (blokjeSleepMin.contains(p.geefPuntX(i) + lx, p.geefPuntY(i) + ly)) 
				{
					//blokjeSleepMin.setLocation(blokjeSleepMin.getLocation().x - dx, blokjeSleepMin.getLocation().y - dy);
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
			
			//blokjeSleepMin.repaint();
			paint(eigenaar.heksGWTContext2d);
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
			//za.start(true, blokjeSleep.getLocation().x - za.getLocation().x);
			tc.verhoog();
			tm.tempPlus();
			eindTemp.verhoog();
			if (!pasEruit)
				erinContainer.voegBlokjeToe(true);
			else
				eruitContainer.verwijderBlokje();
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
			
			//za.start(false, blokjeSleepMin.getLocation().x - za.getLocation().x);
			tc.verlaag();
			tm.tempMin();
			eindTemp.verlaag();
			if (!pasEruit)
				erinContainer.voegBlokjeToe(false);
			else
				eruitContainer.verwijderBlokje();
			blokjeSleepMin.setLocation((int) (blokjeMin.getLocation().x), (int) (blokjeMin.getLocation().y));
			raakSleepMin = false;
			pasEruit = false;
			
			paint(eigenaar.heksGWTContext2d);
		}

		if (plusEruit && blokjeSleep.getLocation().y < za.getLocation().y) 
		{
			plusEruit = false;
			pasEruit = true;

			tc.verlaag();
			tm.tempMin();
			eindTemp.verlaag();
			eruitContainer.voegBlokjeToe(true);
		}
		if (minEruit && blokjeSleepMin.getLocation().y < za.getLocation().y) 
		{
			minEruit = false;
			pasEruit = true;

			tc.verhoog();
			tm.tempPlus();
			eindTemp.verhoog();
			eruitContainer.voegBlokjeToe(false);
		}
		laatstex = eventX;
		laatstey = eventY;
	}

	//public void mouseReleased(MouseEvent e)
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
			blokjeSleep.setLocation((int) (blokjePlus.getLocation().x), (int) (blokjePlus.getLocation().y));
	
			za.start(true, laatstex - za.getLocation().x, laatstey);
			tc.verhoog();
			tm.tempPlus();
			eindTemp.verhoog();
			if (!pasEruit)
				erinContainer.voegBlokjeToe(true);
			else
				eruitContainer.verwijderBlokje();
			
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
			else
				eruitContainer.verwijderBlokje();
			
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
				erinContainer.removeAll();
				erinContainer.paint();
				eruitContainer.removeAll();
				eruitContainer.paint();
			} else 
			{
				eindTemp.zetBekend(false);
				eindTemp.paint();
				tc.zetBekend(false);
				tc.paint();
				tm.zetTemp(0);
				erinContainer.removeAll();
				erinContainer.paint();
				eruitContainer.removeAll();
				eruitContainer.paint();
			}

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
    class PushClickHandler implements ClickHandler
	{
	   	//public void onMouseDown(MouseDownEvent e)
	   	public void onClick(ClickEvent e)
	   	{
			//e.preventDefault();
			e.stopPropagation();
	   		
	   		if (e.getSource() == opnieuwKnop)
	   		{
	   			opnieuwAction();    			
	   		}
	    		
	   	}
	    	
	}

}
