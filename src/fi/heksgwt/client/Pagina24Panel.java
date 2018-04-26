package fi.heksgwt.client;

//import java.awt.*;
//import java.applet.*;
//import java.awt.event.*;
import java.util.Map;
import java.util.HashMap;
import fi.heksgwt.client.scobjects.*;
//import fi.beans.appletutil.*;
import fi.heksgwt.client.vectortek.*;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.PopupPanel;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;


public class Pagina24Panel extends ScContainer // implements MouseListener, MouseMotionListener, ActionListener 
{
	// private Heks eigenaar;
	//private AppletUtil au;
	//HeksInteractiePanel heip;
	HeksGWT eigenaar;

	//private AchtergrondContainer achtergrond;

	Tekening blokjePlus, blokjeMin, blokjeSleep, blokjeSleepMin;
	Tekening pot, potEruit, potErin, beginPot, eindPot, potinhoud, vloer, schrijfheks, werkheks;
	ZinkAnimatie za;
	GetalComponent tc, beginTemp, eindTemp;
	Thermometer tm;
	BlokjesContainer eruitContainer, erinContainer;
	//ScLWButton opdrachtKnop, werkKnop;
	ScLabel beginLabel, eindLabel, erinLabel, eruitLabel, titel, opdrachtTitel;
	//ScTekstContainer uitleg, opdracht;
	//ScPanel sleeppanel;
	//ScTextArea textArea;
	Polygon[] p;
	// AudioClip plons, bubbel;
	
	Pagina24OefenPanel oefenTafereelPanel;
	
	int laatstex, laatstey;
	boolean raakPlusBuiten, raakPlusBinnen, raakMinBuiten, raakMinBinnen, raakSleep, raakSleepMin;
	boolean plusEruit, minEruit, pasEruit;
	boolean[] kleurBlokjes = { true, false, true, true, true, true, false, false, false, false, true };

	//ImageButton opnieuwKnop;
	PushButton opnieuwKnop;

	public Pagina24Panel(int x, int y, int b, int h, HeksGWT eigenaar) 
	{
		super(x, y, b, h);
		//this.heip = heip;
		this.eigenaar = eigenaar;
		// setBackground(new Color(255,255,220));
		//setBackground(heip.bgColor);
		//zetVastePlaats(true);

		//OefenTafereelPanel_WN oefenTafereelPanel = new OefenTafereelPanel_WN(0, 0, b, h, applet);
//GWT		
		oefenTafereelPanel = new Pagina24OefenPanel(0, 0, b, h, eigenaar, this);
		oefenTafereelPanel.zetInstelbaar(true);
		//oefenTafereelPanel.addActionListener(this);
		//oefenTafereelPanel.zetEruitMogelijk(false);
		add(oefenTafereelPanel);


	}
	
	public void initHandlers()
	{
		oefenTafereelPanel.initHandlers();
	}


	public void setState(Map h) 
	{
		
	}

	public HashMap getState() 
	{
		HashMap h = new HashMap();

		return h;
	}


	public void start() {}

	public void stop() { // za.stop();
	}

//	public void mousePressed(MouseEvent e) {}

//	public void mouseDragged(MouseEvent e) {}

//	public void mouseReleased(MouseEvent e) {}

//	public void mouseMoved(MouseEvent e) {}

//	public void mouseExited(MouseEvent e) {;}

//	public void mouseClicked(MouseEvent e) {}

//	public void mouseEntered(MouseEvent e) {;}

//	public void actionPerformed(ActionEvent e) {}

}
