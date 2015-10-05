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
		if (l.containsKey("dobbelstenenRadio")) {
			dobbelstenenRadio = l.getBoolean("dobbelstenenRadio");
			dobbelstenenSelected=true;
		}
		if (l.containsKey("binomTrekkingRadio")) {
			binomTrekkingRadio = l.getBoolean("binomTrekkingRadio");
			binomTrekkingSelected=true;
		}
		
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
			Boolean eenMuntTweeMunt=false;
			if (l.containsKey("eenMuntTweeMunt"))
				eenMuntTweeMunt = l.getBoolean("eenMuntTweeMunt");
			munten.eenMunt.setValue(eenMuntTweeMunt);
			munten.tweeMunten.setValue(!eenMuntTweeMunt);
			munten.setEenMuntTweeMunten();
			String aantalWorpen="";
			if (l.containsKey("aantalWorpen"))
				aantalWorpen = l.getString("aantalWorpen");
			munten.aantalWorpenText.setText(aantalWorpen);
			String kansOpMunt="";
			if (l.containsKey("kansOpMunt"))
				kansOpMunt = l.getString("kansOpMunt");
			munten.kansOpKopText.setText(kansOpMunt);			
		if (dobbelstenenSelected)
			dobbelstenen=new Dobbelstenen(dobbelstenenInstellingen, dobbelstenenResultaten, dobbelstenenGrafiek, dobbelstenenTabel);
			Boolean eenDobbelsteenRadio=false;
			if (l.containsKey("eenDobbelsteenRadio"))
				eenDobbelsteenRadio = l.getBoolean("eenDobbelsteenRadio");
			dobbelstenen.eenDobbelsteen.setValue(eenDobbelsteenRadio);
			Boolean tweeDobbelstenenRadio=false;
			if (l.containsKey("tweeDobbelstenenRadio"))
				tweeDobbelstenenRadio = l.getBoolean("tweeDobbelstenenRadio");
			dobbelstenen.tweeDobbelstenen.setValue(tweeDobbelstenenRadio);
			Boolean drieDobbelstenenRadio=false;
			if (l.containsKey("drieDobbelstenenRadio"))
				drieDobbelstenenRadio = l.getBoolean("drieDobbelstenenRadio");
			dobbelstenen.drieDobbelstenen.setValue(drieDobbelstenenRadio);
			dobbelstenen.doEenTweeDrieDobbelstenen();
			String aantalWorpenDobbelsteen="";
			if (l.containsKey("aantalWorpenDobbelsteen"))
				aantalWorpenDobbelsteen = l.getString("aantalWorpenDobbelsteen");
			dobbelstenen.aantalWorpenText.setText(aantalWorpenDobbelsteen);
			Boolean toonSom=false;
			if (l.containsKey("toonSom"))
				toonSom = l.getBoolean("toonSom");
			dobbelstenen.toonSom.setValue(toonSom);
			dobbelstenen.dobbelstenenGrafiek.paint();
			
			
		if (binomTrekkingSelected) {
			binomTrekking = new BinomTrekking(binomTrekkingInstellingen, binomTrekkingGrafiek, binomTrekkingTabel, binomTrekkingRooster);
			String binomKans="";
			if (l.containsKey("kans"))
				binomKans = l.getString("kans");
			binomTrekking.kansText.setText(binomKans);
			String aantalTrekkingen="";
			if (l.containsKey("aantalTrekkingen"))
				aantalTrekkingen = l.getString("aantalTrekkingen");
			binomTrekking.aantalTrekkingenText.setText(aantalTrekkingen);
			String aantalKeer="";
			if (l.containsKey("aantalKeer"))
				aantalKeer = l.getString("aantalKeer");
			binomTrekking.keerText.setText(aantalKeer);

		}
		
		
		
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
			return dobbelstenen;
		}
		if (binomTrekkingRadio) {
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
			
			
			h.put("eenMunt", new Boolean(munten.eenMunt.getValue()));
			h.put("tweeMunten", new Boolean(munten.tweeMunten.getValue()));
			h.put("aantalWorpen", new String(munten.aantalWorpenText.getText()));
			h.put("kansOpKop", new String(munten.kansOpKopText.getText()));
			h.put("aantalKop", new Boolean(munten.aantalKopRadio.getValue()));
			h.put("percentageKop", new Boolean(munten.percentageKopRadio.getValue()));
			
		}
		

		if (dobbelstenenSelected) {
			List<Dobbelstenen.Experiment> list4 = (List<Dobbelstenen.Experiment>) dobbelstenen.dataProvider.getList();	
			List<List<String>> list5 = new ArrayList<List<String>>();
			
			for (int i=0;i<list4.size();i++) {
				list5.add(Arrays.asList(list4.get(i).getExpNumber(),list4.get(i).getEen(),list4.get(i).getTwee(),list4.get(i).getDrie(),list4.get(i).getVier(),list4.get(i).getVijf(),list4.get(i).getZes()));
			}
			
			h.put("table3", list5);
			
			
			List<Dobbelstenen.Experiment1> list6 = (List<Dobbelstenen.Experiment1>) dobbelstenen.dataProvider1.getList();	
			List<List<String>> list7 = new ArrayList<List<String>>();
			
			for (int i=0;i<list6.size();i++) {
				list7.add(Arrays.asList(list6.get(i).getExpNumber(),list6.get(i).getTwee(),list6.get(i).getDrie(),list6.get(i).getVier(),list6.get(i).getVijf(),list6.get(i).getZes(),list6.get(i).getZeven(),list6.get(i).getAcht(),list6.get(i).getNegen(),list6.get(i).getTien(),list6.get(i).getElf(),list6.get(i).getTwaalf()));
			}
			
			h.put("table4", list7);
			
			List<Dobbelstenen.Experiment2> list8 = (List<Dobbelstenen.Experiment2>) dobbelstenen.dataProvider2.getList();	
			List<List<String>> list9 = new ArrayList<List<String>>();
			
			for (int i=0;i<list8.size();i++) {
				list9.add(Arrays.asList(list8.get(i).getExpNumber(),list8.get(i).getDrie(),list8.get(i).getVier(),list8.get(i).getVijf(),list8.get(i).getZes(),list8.get(i).getZeven(),list8.get(i).getAcht(),list8.get(i).getNegen(),list8.get(i).getTien(),list8.get(i).getElf(),list8.get(i).getTwaalf(),list8.get(i).getDertien(),list8.get(i).getVeertien(),list8.get(i).getVijftien(),list8.get(i).getZestien(),list8.get(i).getZeventien(),list8.get(i).getAchttien()));
			}
			
			h.put("table5", list9);

			List<Dobbelstenen.Experiment3> list10 = (List<Dobbelstenen.Experiment3>) dobbelstenen.dataProvider3.getList();	
			List<List<String>> list11 = new ArrayList<List<String>>();
			
			for (int i=0;i<list10.size();i++) {
				list11.add(Arrays.asList(list10.get(i).getEyes(),list10.get(i).getEen(),list10.get(i).getTwee(),list10.get(i).getDrie(),list10.get(i).getVier(),list10.get(i).getVijf(),list10.get(i).getZes()));
			}
			
			h.put("table6", list11);

			List<Dobbelstenen.Experiment4> list12 = (List<Dobbelstenen.Experiment4>) dobbelstenen.dataProvider4.getList();	
			List<List<String>> list13 = new ArrayList<List<String>>();
			
			for (int i=0;i<list12.size();i++) {
				list13.add(Arrays.asList(list12.get(i).getEyes(),list12.get(i).getTwee(),list12.get(i).getDrie(),list12.get(i).getVier(),list12.get(i).getVijf(),list12.get(i).getZes(),list12.get(i).getZeven(),list12.get(i).getAcht(),list12.get(i).getNegen(),list12.get(i).getTien(),list12.get(i).getElf(),list12.get(i).getTwaalf()));
			}
			
			h.put("table7", list13);


			List<Dobbelstenen.Experiment5> list14 = (List<Dobbelstenen.Experiment5>) dobbelstenen.dataProvider5.getList();	
			List<List<String>> list15 = new ArrayList<List<String>>();
			
			for (int i=0;i<list14.size();i++) {
				list15.add(Arrays.asList(list14.get(i).getEyes(),list14.get(i).getDrie(),list14.get(i).getVier(),list14.get(i).getVijf(),list14.get(i).getZes(),list14.get(i).getZeven(),list14.get(i).getAcht(),list14.get(i).getNegen(),list14.get(i).getTien(),list14.get(i).getElf(),list14.get(i).getTwaalf(),list14.get(i).getDertien(),list14.get(i).getVeertien(),list14.get(i).getVijftien(),list14.get(i).getZestien(),list14.get(i).getZeventien(),list14.get(i).getAchttien()));
			}
			
			h.put("table8", list15);

			h.put("eenDobbelsteen", new Boolean(dobbelstenen.eenDobbelsteen.getValue()));
			h.put("tweeDobbelstenen", new Boolean(dobbelstenen.tweeDobbelstenen.getValue()));
			h.put("drieDobbelstenen", new Boolean(dobbelstenen.drieDobbelstenen.getValue()));
			h.put("aantalWorpenDobbelstenen", new String(dobbelstenen.aantalWorpenText.getText()));
			h.put("toonSom", new Boolean(dobbelstenen.toonSom.getValue()));
		}

		if (binomTrekkingSelected) {
			
			List<BinomTrekking.Experiment> list16 = (List<BinomTrekking.Experiment>) binomTrekking.dataProvider.getList();	
			List<List<String>> list17 = new ArrayList<List<String>>();
			
			for (int i=0;i<list16.size();i++) {
				list17.add(Arrays.asList(list16.get(i).getExpNumber(),list16.get(i).getOutcome()));
			}
			
			h.put("table9", list17);
			
			h.put("kans", new String(binomTrekking.kansText.getText()));
			h.put("binomAantalTrekkingen", new String(binomTrekking.aantalTrekkingenText.getText()));
			h.put("binomKeer", new String(binomTrekking.keerText.getText()));
		}
			
		return h;
		
	}

	@Override
	public void setState(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
	
		ObjectMap h = JSONUtilities.wrapMap(map);
		
		
		if (muntenSelected) {
			
			Boolean eenMunt=false;
			if (h.containsKey("eenMunt"))
				eenMunt = h.getBoolean("eenMunt");
			munten.eenMunt.setValue(eenMunt);
			
			Boolean tweeMunten=false;
			if (h.containsKey("tweeMunten"))
				tweeMunten = h.getBoolean("tweeMunten");
			munten.tweeMunten.setValue(tweeMunten);
			munten.setEenMuntTweeMunten();
			
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
				
				if (eenMunt==true) {
					munten.experiment=list.size();
					//Window.alert("1:"+Integer.toString(list.size()));
				}
			}
			
			if (h.containsKey("table2")){
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
				
				if (eenMunt==false) {
					munten.experiment=list2.size();
					//Window.alert(Integer.toString(list2.size()));
				}
			}
			
						
			
			String aantalWorpen="";
			if (h.containsKey("aantalWorpen"))
				aantalWorpen = h.getString("aantalWorpen");
			munten.aantalWorpenText.setText(aantalWorpen);
			
			String kansOpKop="";
			if (h.containsKey("kansOpKop"))
				kansOpKop = h.getString("kansOpKop");
			munten.kansOpKopText.setText(kansOpKop);
			
			Boolean aantalKop=false;
			if (h.containsKey("aantalKop"))
				aantalKop = h.getBoolean("aantalKop");
			munten.aantalKopRadio.setValue(aantalKop);
			
			Boolean percentageKop=false;
			if (h.containsKey("percentageKop"))
				percentageKop = h.getBoolean("percentageKop");
			munten.percentageKopRadio.setValue(percentageKop);
			munten.setResults();

		
		}
		
		
		if (dobbelstenenSelected) {
			
			Boolean eenDobbelsteen=false;
			if (h.containsKey("eenDobbelsteen"))
				eenDobbelsteen = h.getBoolean("eenDobbelsteen");
			dobbelstenen.eenDobbelsteen.setValue(eenDobbelsteen);
			
			Boolean tweeDobbelstenen=false;
			if (h.containsKey("tweeDobbelstenen"))
				tweeDobbelstenen = h.getBoolean("tweeDobbelstenen");
			dobbelstenen.tweeDobbelstenen.setValue(tweeDobbelstenen);
			
			Boolean drieDobbelstenen=false;
			if (h.containsKey("drieDobbelstenen"))
				drieDobbelstenen = h.getBoolean("drieDobbelstenen");
			dobbelstenen.drieDobbelstenen.setValue(drieDobbelstenen);
			
			dobbelstenen.doEenTweeDrieDobbelstenen();
			
			if (h.containsKey("table3")){
				List<List<String>> list3;
				
				list3 =  (List<List<String>>) h.get("table3");
			
				List<Dobbelstenen.Experiment> list4=new ArrayList<Dobbelstenen.Experiment>();
				
				for (int i=0;i<list3.size();i++) {
					list4.add(new Dobbelstenen.Experiment(list3.get(i).get(0),list3.get(i).get(1), list3.get(i).get(2), list3.get(i).get(3), list3.get(i).get(4), list3.get(i).get(5), list3.get(i).get(6)));
				}
				
				dobbelstenen.dataProvider.getList().clear();
				dobbelstenen.dataProvider.getList().addAll(list4);
				dobbelstenen.dataProvider.refresh();
				dobbelstenen.dataProvider.flush();
				
				if (eenDobbelsteen==true) 
					dobbelstenen.experiment=list3.size();
			}
			if (h.containsKey("table4")){
				List<List<String>> list5;
				
				list5 =  (List<List<String>>) h.get("table4");
			
				List<Dobbelstenen.Experiment1> list6=new ArrayList<Dobbelstenen.Experiment1>();
				
				for (int i=0;i<list5.size();i++) {
					list6.add(new Dobbelstenen.Experiment1(list5.get(i).get(0),list5.get(i).get(1), list5.get(i).get(2), list5.get(i).get(3), list5.get(i).get(4), list5.get(i).get(5), list5.get(i).get(6),list5.get(i).get(7),list5.get(i).get(8),list5.get(i).get(9),list5.get(i).get(10),list5.get(i).get(11)));
				}
				
				dobbelstenen.dataProvider1.getList().clear();
				dobbelstenen.dataProvider1.getList().addAll(list6);
				dobbelstenen.dataProvider1.refresh();
				dobbelstenen.dataProvider1.flush();

				if (tweeDobbelstenen==true) 
					dobbelstenen.experiment=list5.size();
			}
			if (h.containsKey("table5")){
				List<List<String>> list7;
				
				list7 =  (List<List<String>>) h.get("table5");
			
				List<Dobbelstenen.Experiment2> list8=new ArrayList<Dobbelstenen.Experiment2>();
				
				for (int i=0;i<list7.size();i++) {
					list8.add(new Dobbelstenen.Experiment2(list7.get(i).get(0),list7.get(i).get(1), list7.get(i).get(2), list7.get(i).get(3), list7.get(i).get(4), list7.get(i).get(5), list7.get(i).get(6),list7.get(i).get(7),list7.get(i).get(8),list7.get(i).get(9),list7.get(i).get(10),list7.get(i).get(11),list7.get(i).get(12),list7.get(i).get(13),list7.get(i).get(14),list7.get(i).get(15),list7.get(i).get(16)));
				}
				
				dobbelstenen.dataProvider2.getList().clear();
				dobbelstenen.dataProvider2.getList().addAll(list8);
				dobbelstenen.dataProvider2.refresh();
				dobbelstenen.dataProvider2.flush();

				if (drieDobbelstenen==true) 
					dobbelstenen.experiment=list7.size();
			}
			if (h.containsKey("table6")){
				List<List<String>> list9;
				
				list9 =  (List<List<String>>) h.get("table6");
			
				List<Dobbelstenen.Experiment3> list10=new ArrayList<Dobbelstenen.Experiment3>();
				
				for (int i=0;i<list9.size();i++) {
					list10.add(new Dobbelstenen.Experiment3(list9.get(i).get(0),list9.get(i).get(1), list9.get(i).get(2), list9.get(i).get(3), list9.get(i).get(4), list9.get(i).get(5), list9.get(i).get(6)));
				}
				
				dobbelstenen.dataProvider3.getList().clear();
				dobbelstenen.dataProvider3.getList().addAll(list10);
				dobbelstenen.dataProvider3.refresh();
				dobbelstenen.dataProvider3.flush();
			}
			if (h.containsKey("table7")){
				List<List<String>> list11;
				
				list11 =  (List<List<String>>) h.get("table7");
			
				List<Dobbelstenen.Experiment4> list12=new ArrayList<Dobbelstenen.Experiment4>();
				
				for (int i=0;i<list11.size();i++) {
					list12.add(new Dobbelstenen.Experiment4(list11.get(i).get(0),list11.get(i).get(1), list11.get(i).get(2), list11.get(i).get(3), list11.get(i).get(4), list11.get(i).get(5), list11.get(i).get(6), list11.get(i).get(7), list11.get(i).get(8), list11.get(i).get(9), list11.get(i).get(10), list11.get(i).get(11)));
				}
				
				dobbelstenen.dataProvider4.getList().clear();
				dobbelstenen.dataProvider4.getList().addAll(list12);
				dobbelstenen.dataProvider4.refresh();
				dobbelstenen.dataProvider4.flush();
			}
			if (h.containsKey("table8")){
				List<List<String>> list13;
				
				list13 =  (List<List<String>>) h.get("table8");
			
				List<Dobbelstenen.Experiment5> list14=new ArrayList<Dobbelstenen.Experiment5>();
				
				for (int i=0;i<list13.size();i++) {
					list14.add(new Dobbelstenen.Experiment5(list13.get(i).get(0),list13.get(i).get(1), list13.get(i).get(2), list13.get(i).get(3), list13.get(i).get(4), list13.get(i).get(5), list13.get(i).get(6), list13.get(i).get(7), list13.get(i).get(8), list13.get(i).get(9), list13.get(i).get(10), list13.get(i).get(11), list13.get(i).get(12), list13.get(i).get(13), list13.get(i).get(14), list13.get(i).get(15), list13.get(i).get(16)));
				}
				
				dobbelstenen.dataProvider5.getList().clear();
				dobbelstenen.dataProvider5.getList().addAll(list14);
				dobbelstenen.dataProvider5.refresh();
				dobbelstenen.dataProvider5.flush();
			}
			
			String aantalWorpenDobbelstenen="";
			if (h.containsKey("aantalWorpenDobbelstenen"))
				aantalWorpenDobbelstenen = h.getString("aantalWorpenDobbelstenen");
			dobbelstenen.aantalWorpenText.setText(aantalWorpenDobbelstenen);
			
			Boolean toonSom=false;
			if (h.containsKey("toonSom"))
				toonSom = h.getBoolean("toonSom");
			dobbelstenen.toonSom.setValue(toonSom);
			dobbelstenen.dobbelstenenGrafiek.paint();
		}

		if (binomTrekkingSelected) {
			if (h.containsKey("table9")){
				List<List<String>> list15;
				
				list15 =  (List<List<String>>) h.get("table9");
			
				List<BinomTrekking.Experiment> list16=new ArrayList<BinomTrekking.Experiment>();
				
				for (int i=0;i<list15.size();i++) {
					list16.add(new BinomTrekking.Experiment(list15.get(i).get(0),list15.get(i).get(1)));
				}
				
				binomTrekking.dataProvider.getList().clear();
				binomTrekking.dataProvider.getList().addAll(list16);
				binomTrekking.dataProvider.refresh();
				binomTrekking.dataProvider.flush();
				
				binomTrekking.experiment=list15.size();
			}
			
			String binomKans="";
			if (h.containsKey("kans"))
				binomKans = h.getString("kans");
			binomTrekking.kansText.setText(binomKans);
			
			String binomAantalTrekkingen="";
			if (h.containsKey("binomAantalTrekkingen"))
				binomAantalTrekkingen = h.getString("binomAantalTrekkingen");
			binomTrekking.aantalTrekkingenText.setText(binomAantalTrekkingen);
			
			String binomKeer="";
			if (h.containsKey("binomKeer"))
				binomKeer = h.getString("binomKeer");
			binomTrekking.keerText.setText(binomKeer);	
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
