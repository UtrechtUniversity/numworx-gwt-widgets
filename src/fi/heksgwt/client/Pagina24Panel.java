package fi.heksgwt.client;

import java.util.Map;
import java.util.HashMap;
import fi.heksgwt.client.scobjects.*;
import fi.heksgwt.client.vectortek.*;

import com.google.gwt.user.client.ui.PushButton;


/**
 * Pagina24 is het panel om te oefenen met blokjes erin en eruit.
 * In instellingenpanel docentomgeving 'hulppagina', in HeksGWT paginanummer 4. 
 * 
 * @author borku102
 *
 */
public class Pagina24Panel extends ScContainer 
{
	HeksGWT eigenaar;

	Tekening blokjePlus, blokjeMin, blokjeSleep, blokjeSleepMin;
	Tekening pot, potEruit, potErin, beginPot, eindPot, potinhoud, vloer, schrijfheks, werkheks;
	ZinkAnimatie za;
	GetalComponent tc, beginTemp, eindTemp;
	Thermometer tm;
	BlokjesContainer eruitContainer, erinContainer;
	ScLabel beginLabel, eindLabel, erinLabel, eruitLabel, titel, opdrachtTitel;
	Polygon[] p;
	
	Pagina24OefenPanel oefenTafereelPanel;
	
	int laatstex, laatstey;
	boolean raakPlusBuiten, raakPlusBinnen, raakMinBuiten, raakMinBinnen, raakSleep, raakSleepMin;
	boolean plusEruit, minEruit, pasEruit;
	boolean[] kleurBlokjes = { true, false, true, true, true, true, false, false, false, false, true };

	PushButton opnieuwKnop;

	public Pagina24Panel(int x, int y, int b, int h, HeksGWT eigenaar) 
	{
		super(x, y, b, h);
		this.eigenaar = eigenaar;

		oefenTafereelPanel = new Pagina24OefenPanel(0, 0, b, h, eigenaar, this);
		oefenTafereelPanel.zetInstelbaar(true);
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

	public void stop()
	{
	}
}
