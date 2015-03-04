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
	}

	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		//LayoutPanel dlp=new LayoutPanel();
		//Label test=new Label("test");
		//dlp.add(test);
		
		if (muntenRadio) {
			Munten munten=new Munten();
			return munten;
		}
		if (dobbelstenenRadio) {
			Dobbelstenen dobbelstenen=new Dobbelstenen();
			return dobbelstenen;
		}
		if (binomTrekkingRadio) {
			BinomTrekking binomtrekking = new BinomTrekking();
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
