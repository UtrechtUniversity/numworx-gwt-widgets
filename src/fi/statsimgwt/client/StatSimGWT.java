package fi.statsimgwt.client;

import java.util.HashMap;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class StatSimGWT implements EntryPoint, InteractionView {
	
	int breedte;
	int hoogte;
	private Map<String, Object> launchState;
	boolean muntenRadio=false;
	boolean dobbelstenenRadio=false;
	boolean binomTrekkingRadio=false;
	
	boolean muntenInstellingen=false;
	boolean muntenResultaten=false;
	boolean muntenGrafiek=false;
	boolean muntenTabel=false;
	boolean muntenFrequentie=false;
	boolean dobbelstenenInstellingen=false;
	boolean dobbelstenenResultaten=false;
	boolean dobbelstenenGrafiek=false;
	boolean dobbelstenenTabel=false;
	boolean binomTrekkingInstellingen=false;
	boolean binomTrekkingGrafiek=false;
	boolean binomTrekkingTabel=false;
	boolean binomTrekkingFrequentie=false;
	boolean binomTrekkingRooster=false;
	
	public void onModuleLoad() {

			//Munten munten = new Munten();
			//Dobbelstenen dobbelstenen = new Dobbelstenen();
            //BinomTrekking binomtrekking = new BinomTrekking();			
	}
	
	public StatSimGWT(HashMap<String, Object> map, String[] randomVarNamen, HashMap randomVarWaarden) {
		//BinomTrekking binomtrekking = new BinomTrekking();
		ObjectMap h = JSONUtilities.wrapMap(map);
			
		if (h.containsKey("breedte"))
			breedte = h.getInt("breedte");
		if (h.containsKey("hoogte"))
			hoogte = h.getInt("hoogte");
		if (h.containsKey("interactiePanelLaunchState"))
			launchState = h.getMap("interactiePanelLaunchState");

		ObjectMap l=JSONUtilities.wrapMap(launchState);
		
		if (l.containsKey("muntenRadio"))
			muntenRadio = l.getBoolean("muntenRadio");
		if (l.containsKey("dobbelstenenRadio"))
			dobbelstenenRadio = l.getBoolean("dobbelstenenRadio");
		if (l.containsKey("binomTrekkingRadio"))
			binomTrekkingRadio = l.getBoolean("binomTrekkingRadio");
		
		if (l.containsKey("muntenInstellingen"))
			muntenInstellingen = l.getBoolean("muntenInstellingen");
		if (l.containsKey("muntenResultaten"))
			muntenResultaten = l.getBoolean("muntenResultaten");
		if (l.containsKey("muntenGrafiek"))
			muntenGrafiek = l.getBoolean("muntenGrafiek");
		if (l.containsKey("muntenTabel"))
			muntenTabel = l.getBoolean("muntenTabel");
		if (l.containsKey("muntenFrequentie"))
			muntenFrequentie = l.getBoolean("muntenFrequentie");
		if (l.containsKey("dobbelstenenInstellingen"))
			dobbelstenenInstellingen = l.getBoolean("dobbelstenenInstellingen");
		if (l.containsKey("dobbelstenenResultaten"))
			dobbelstenenResultaten = l.getBoolean("dobbelstenenResultaten");
		if (l.containsKey("dobbelstenenGrafiek"))
			dobbelstenenGrafiek = l.getBoolean("dobbelstenenGrafiek");
		if (l.containsKey("dobbelstenenTabel"))
			dobbelstenenTabel = l.getBoolean("dobbelstenenTabel");
		if (l.containsKey("binomTrekkingInstellingen"))
			binomTrekkingInstellingen = l.getBoolean("binomTrekkingInstellingen");
		if (l.containsKey("binomTrekkingGrafiek"))
			binomTrekkingGrafiek = l.getBoolean("binomTrekkingGrafiek");
		if (l.containsKey("binomTrekkingTabel"))
			binomTrekkingTabel = l.getBoolean("binomTrekkingTabel");
		if (l.containsKey("binomTrekkingFrequentie"))
			binomTrekkingFrequentie = l.getBoolean("binomTrekkingFrequentie");
		if (l.containsKey("binomTrekkingRooster"))
			binomTrekkingRooster = l.getBoolean("binomTrekkingRooster");
	}

	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		//LayoutPanel dlp=new LayoutPanel();
		//Label test=new Label("test");
		//dlp.add(test);
		
		if (muntenRadio) {
			Munten munten=new Munten(muntenInstellingen, muntenResultaten, muntenGrafiek, muntenTabel, muntenFrequentie);
			return munten;
		}
		if (dobbelstenenRadio) {
			Dobbelstenen dobbelstenen=new Dobbelstenen(dobbelstenenInstellingen, dobbelstenenResultaten, dobbelstenenGrafiek, dobbelstenenTabel);
			return dobbelstenen;
		}
		if (binomTrekkingRadio) {
			BinomTrekking binomtrekking = new BinomTrekking(binomTrekkingInstellingen, binomTrekkingGrafiek, binomTrekkingTabel, binomTrekkingRooster);
			return binomtrekking;
		}
		
		return null;
	}

	@Override
	public int getAsHoogte() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return hoogte;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return breedte;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashMap<String, Object> getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean isCorrect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void kijkNa() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zetNagekeken(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		// TODO Auto-generated method stub
		
	}
}
