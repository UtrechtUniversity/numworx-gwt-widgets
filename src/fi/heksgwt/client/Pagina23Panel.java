package fi.heksgwt.client;

//import java.awt.*;
//import java.applet.*;
import java.util.Map;
import java.util.HashMap;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
//import java.awt.event.*;
import fi.heksgwt.client.Pagina21Panel.PushClickHandler;
import fi.heksgwt.client.Pagina23OefenPanel.MouseHandler;
import fi.heksgwt.client.Pagina23OefenPanel.TouchHandler;
import fi.heksgwt.client.scobjects.ScContainer;
import fi.heksgwt.client.scobjects.ScLabel;
//import fi.beans.appletutil.*;
import fi.heksgwt.client.vectortek.*;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.PopupPanel;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;


public class Pagina23Panel extends ScContainer //Panel implements ActionListener 
{
	HeksGWT eigenaar;
	
	//private ScLWButton opdrachtKnop, werkKnop;
	//private ScLabel titelLabel, maalLabel, opdrachtTitel;
	private ScLabel maalLabel;
	//private OefenTafereelPanelEmmer_WN oefenTafereelPanel;

	private Pagina23OefenPanel oefenTafereelPanel;
	
	EmmerPanel emmer;
	private GetalComponent emmerTeller;
	//private ScTekstContainer uitleg, opdracht;
	//ScTextArea textArea;

	PushButton opnieuwKnop;
	TekstPopup tf;

	public Pagina23Panel(int x, int y, int b, int h, HeksGWT heksGWT) 
	{
		super(x, y, b, h);
		eigenaar = heksGWT;
		

		oefenTafereelPanel = new Pagina23OefenPanel(180, 0, b - 200, h - 5, eigenaar, this);
//GWT		
		//oefenTafereelPanel.addActionListener(this);
		add(oefenTafereelPanel);

		//emmer = new EmmerPanel(150, 10, 110, 125, heip);
//GWT		
		emmer = new EmmerPanel(95, 15, 90, 105, eigenaar);
emmer.zetNaam("emmerpanel");
		emmer.zetP23Klein();
		emmer.zetInstelbaar(false);
		add(emmer, 0);
		emmer.setVisible(false);

		//maalLabel = new ScLabel(100, 60, 50, 50, "X");
		maalLabel = new ScLabel(50, 58+5, 35, 35, "X");
		add(maalLabel);
		maalLabel.setVisible(false);

		//emmerTeller = new GetalComponent(50, 60, 50, 50);
		emmerTeller = new GetalComponent(0, 55, 55, 40, eigenaar);
		emmerTeller.p23Klein = true;
		emmerTeller.zetWaarde(0);
		add(emmerTeller);
		emmerTeller.setVisible(false);


	}
	
	public void initHandlers()
	{
		oefenTafereelPanel.initHandlers();
	}


	public void initOpnieuwKnop()
	{
		int knopY = emmerTeller.getLocation().y + emmerTeller.getSize().y + 60; 
		opnieuwKnop = new PushButton(eigenaar.rb.opnieuwKnopLabel());
		eigenaar.dlp.add(opnieuwKnop);
		eigenaar.dlp.setWidgetLeftWidth(opnieuwKnop, 20, Style.Unit.PX, 65, Style.Unit.PX);
		eigenaar.dlp.setWidgetTopHeight(opnieuwKnop, knopY, Style.Unit.PX, 25, Style.Unit.PX);
		
		opnieuwKnop.addClickHandler(new PushClickHandler());

	}

	
	public void zetAlleenErin()
	{
//System.out.println("p23 zetAlleenErin()");
		oefenTafereelPanel.alleenErin = false;
		oefenTafereelPanel.alleenEruit = false;
		oefenTafereelPanel.zetOpnieuw();
		oefenTafereelPanel.emmerbinnen.setVisible(false);
		oefenTafereelPanel.emmerbuiten.setVisible(true);
		oefenTafereelPanel.alleenErin = true;
	}

	public void zetAlleenEruit()
	{
		
//System.out.println("p23 zetAlleenEruit");
		oefenTafereelPanel.alleenErin = false;
		oefenTafereelPanel.alleenEruit = false;
		oefenTafereelPanel.zetOpnieuw();
		oefenTafereelPanel.emmerbinnen.setVisible(true);
		oefenTafereelPanel.emmerbuiten.setVisible(false);
		oefenTafereelPanel.alleenEruit = true;
	}
	
	public void zetKeuzeErinEruit()
	{
		
		oefenTafereelPanel.alleenErin = false;
		oefenTafereelPanel.alleenEruit = false;
		oefenTafereelPanel.zetOpnieuw();
	}

	
	public void setState(Map map) 
	{
		
		ObjectMap h = JSONUtilities.wrapMap(map);
System.out.println("p23 setState");		
		boolean erinMogelijk = false;
		if (h.containsKey("erinmogelijk"))
			erinMogelijk = h.getBoolean("erinmogelijk");
		boolean eruitMogelijk = false;
		if (h.containsKey("eruitmogelijk"))
			eruitMogelijk = h.getBoolean("eruitmogelijk");
		int emmerInhoud = 0; 
		if (h.containsKey("emmerinhoud"))
			emmerInhoud = h.getInt("emmerinhoud");
		oefenTafereelPanel.emmerInhoud = emmerInhoud;
		int aantalEmmers = 0; 
		if (h.containsKey("aantalemmers"))
			aantalEmmers = h.getInt("aantalemmers");
		if (erinMogelijk)
		{	
System.out.println("p23 setState erinMogelijk");

			oefenTafereelPanel.zetErinMogelijk();
			oefenTafereelPanel.emmerbuiten.zetInhoud(emmerInhoud);
			oefenTafereelPanel.emmerbinnen.setVisible(false);
			oefenTafereelPanel.emmerSleep.zetInhoud(emmerInhoud);
//System.out.println("set emmerinhoud " + emmer.geefInhoud());
//System.out.println("emmerSleep " + oefenTafereelPanel.emmerSleep.etiket.isInstelbaar());
			oefenTafereelPanel.emmerbuiten.etiket.zetInstelbaar(false);
			oefenTafereelPanel.za.zetInhoud(emmerInhoud);
//GWT?			
			//oefenTafereelPanel.za.start();
			
			emmer.zetInhoud(emmerInhoud);

			emmerTeller.zetWaarde(aantalEmmers);
			if (aantalEmmers > 0)
			{	
				emmer.setVisible(true);
				maalLabel.setVisible(true);
				emmerTeller.setVisible(true);
			}	
			
		}
		else if (eruitMogelijk)
		{	
System.out.println("p23 setState eruitMogelijk");
			oefenTafereelPanel.zetEruitMogelijk();
			oefenTafereelPanel.emmerbinnen.zetInhoud(emmerInhoud);
			oefenTafereelPanel.emmerbuiten.setVisible(false);
			oefenTafereelPanel.emmerSleep.zetInhoud(emmerInhoud);
//System.out.println("set emmerinhoud " + emmer.geefInhoud());			
//System.out.println("emmerSleep " + oefenTafereelPanel.emmerSleep.etiket.isInstelbaar());			
			oefenTafereelPanel.emmerbinnen.etiket.zetInstelbaar(false);
			oefenTafereelPanel.za.zetInhoud(emmerInhoud);
//GWT?			
			//oefenTafereelPanel.za.start();
			
			emmer.zetInhoud(emmerInhoud);
			emmerTeller.zetWaarde(aantalEmmers);
			if (aantalEmmers < 0)
			{
				emmer.setVisible(true);
				maalLabel.setVisible(true);
				emmerTeller.setVisible(true);
			}	

		}
		else
		{
			
		}
		int eindtemp = 0; 
		if (h.containsKey("eindtemp"))
			eindtemp = h.getInt("eindtemp");
		oefenTafereelPanel.tc.zetWaarde(eindtemp);
		
		//paint(eigenaar.heksGWTContext2d);


	}

	public HashMap<String,Object> getState() 
	{
		HashMap<String,Object> h = new HashMap<String,Object>();
		
		h.put("erinmogelijk", new Boolean(oefenTafereelPanel.erinMogelijk));
		h.put("eruitmogelijk", new Boolean(oefenTafereelPanel.eruitMogelijk));
		h.put("emmerinhoud", new Integer(oefenTafereelPanel.emmerInhoud));
//System.out.println("put emmerinhoud " + emmer.geefInhoud());		
		h.put("aantalemmers", new Integer(emmerTeller.geefWaarde()));
	
		h.put("eindtemp", new Integer(oefenTafereelPanel.geefTemp()));

		return h;
	}

/*	
	public double getScore() {
		if (textArea.getText() != null && textArea.getText().length() > 100)
			return 100;
		if (textArea.getText() != null && textArea.getText().length() > 5)
			return 10;
		return 0;
	}
*/
	public void start() 
	{
		//oefenTafereelPanel.zetOpnieuw();
		//emmer.zetInhoud(0);
		//emmer.setVisible(false);
		//emmerTeller.zetWaarde(0);
		//emmerTeller.setVisible(false);
		//maalLabel.setVisible(false);
	}

	public void stop() 
	{
		//oefenTafereelPanel.stop();
	}
	
	public void paint(Context2d g)
	{
		g.setFillStyle(CssColor.make(255,255,255));
		g.fillRect(0,0, eigenaar.breedte, eigenaar.hoogte);
		
		super.paint(g);
	}
	
	public void instellingAction(GetalComponent owner)
	{
		oefenTafereelPanel.instellingAction(owner);
	}

	public void emmerAction(boolean erin)
	{
		if (erin) 
		{
			
System.out.println("emmerAction erin");	
			if (oefenTafereelPanel.geefEmmerInhoud() != 0)
			{	
				emmerTeller.verhoog();
				emmer.zetInhoud(oefenTafereelPanel.geefEmmerInhoud());
				emmer.setVisible(true);
				emmerTeller.setVisible(true);
				maalLabel.setVisible(true);
			}	
		} 
		else
		{
System.out.println("emmerAction eruit");
			if (oefenTafereelPanel.geefEmmerInhoud() != 0)
			{	
				
				emmerTeller.verlaag();
				emmer.zetInhoud(oefenTafereelPanel.geefEmmerInhoud());
				emmer.setVisible(true);
				emmerTeller.setVisible(true);
				maalLabel.setVisible(true);
			}	
		}
	}
	
//GWT	
/*	
	public void actionPerformed(ActionEvent e) 
	{
		else if (e.getSource() == oefenTafereelPanel) 
		{
			//if (opdracht.isVisible())
			//	return;
			opnieuwKnop.setVisible(true);
			if (e.getActionCommand().equals("erin")) {
				emmerTeller.verhoog();
				emmer.zetInhoud(oefenTafereelPanel.geefEmmerInhoud());
				emmer.setVisible(true);
				emmerTeller.setVisible(true);
				maalLabel.setVisible(true);
			} else if (e.getActionCommand().equals("eruit")) {
				emmerTeller.verlaag();
				emmer.zetInhoud(oefenTafereelPanel.geefEmmerInhoud());
				emmer.setVisible(true);
				emmerTeller.setVisible(true);
				maalLabel.setVisible(true);
			}

		}

	}
*/	
	
	public void opnieuwAction()
	{
	
		oefenTafereelPanel.zetOpnieuw();
	
		emmer.zetInhoud(0);
		emmer.setVisible(false);
		emmerTeller.zetWaarde(0);
		emmerTeller.setVisible(false);
		maalLabel.setVisible(false);
		
		paint(eigenaar.heksGWTContext2d);
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
