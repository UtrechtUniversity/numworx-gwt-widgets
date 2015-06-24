package fi.statsimgwt.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.statsimgwt.client.Munten.Experiment;

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
	
	
	boolean muntenSelected=false;
	boolean dobbelstenenSelected=false;
	boolean binomTrekkingSelected=false;
	Munten munten;
	Dobbelstenen dobbelstenen;
	BinomTrekking binomTrekking;
	
	
	
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
		
		if (l.containsKey("muntenRadio")) {
			muntenRadio = l.getBoolean("muntenRadio");
			muntenSelected=true;
		}
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
	
		if (muntenSelected)
			munten=new Munten(muntenInstellingen, muntenResultaten, muntenGrafiek, muntenTabel, muntenFrequentie);
		
		
		
	}

	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		//LayoutPanel dlp=new LayoutPanel();
		//Label test=new Label("test");
		//dlp.add(test);
		
		if (muntenRadio) {
			return munten;
		}
		if (dobbelstenenRadio) {
			dobbelstenenSelected=true;
			dobbelstenen=new Dobbelstenen(dobbelstenenInstellingen, dobbelstenenResultaten, dobbelstenenGrafiek, dobbelstenenTabel);
			return dobbelstenen;
		}
		if (binomTrekkingRadio) {
			binomTrekkingSelected=true;
			binomTrekking = new BinomTrekking(binomTrekkingInstellingen, binomTrekkingGrafiek, binomTrekkingTabel, binomTrekkingRooster);
			return binomTrekking;
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
		
		HashMap<String,Object> h = new HashMap<String,Object>();
		
		if (muntenSelected) {
			
			List<Munten.Experiment> list = (List<Munten.Experiment>) munten.dataProvider.getList();	
			List<List<String>> list1 = new ArrayList<List<String>>();
			
			for (int i=0;i<list.size();i++) {
				list1.add(Arrays.asList(list.get(i).getExpNumber(),list.get(i).getKopValue(),list.get(i).getMuntValue()));
			}
			
			h.put("table", list1);
			
			List<Munten.Experiment2> list2 = (List<Munten.Experiment2>) munten.dataProvider1.getList();	
			List<List<String>> list3 = new ArrayList<List<String>>();
			
			for (int i=0;i<list2.size();i++) {
				list3.add(Arrays.asList(list2.get(i).getExpNumber(),list2.get(i).getGeenKop(),list2.get(i).getEenKop(),list2.get(i).getTweeKop()));
			}
			
			h.put("table2", list3);
			
			
		}
		return h;
		
	}

	@Override
	public void setState(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
	
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		
		
		if (h.containsKey("table")){
			List<List<String>> list;
			
			list =  (List<List<String>>) h.get("table");
		
			List<Munten.Experiment> list1=new ArrayList<Munten.Experiment>();
			
			for (int i=0;i<list.size();i++) {
				list1.add(new Munten.Experiment(list.get(i).get(0),list.get(i).get(1), list.get(i).get(2)));
			}
			
			munten.dataProvider.getList().clear();
			munten.dataProvider.getList().addAll(list1);
			munten.dataProvider.refresh();
			munten.dataProvider.flush();
			
			List<List<String>> list2;
			
			list2 =  (List<List<String>>) h.get("table2");
			
			List<Munten.Experiment2> list3=new ArrayList<Munten.Experiment2>();
			
			for (int i=0;i<list2.size();i++) {
				list3.add(new Munten.Experiment2(list2.get(i).get(0),list2.get(i).get(1), list2.get(i).get(2), list2.get(i).get(3)));
			}
			
			munten.dataProvider1.getList().clear();
			munten.dataProvider1.getList().addAll(list3);
			munten.dataProvider1.refresh();
			munten.dataProvider1.flush();
		}
		
		

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
