package fi.statsimgwt.client;


import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

import fi.statsimgwt.client.Dobbelstenen.Experiment;

public class BinomTrekking  extends FlowPanel implements ClickHandler {


	static final String upgradeMessage = 
			"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	
	public Canvas kladjeHWTCanvas;
	public Context2d gIm;
	public Canvas binomGrafiekCanvas;
	public Context2d gIm2;
	
	TextBox kansText;
	TextBox aantalTrekkingenText;
	Button voeruit;
	Button stap;
	Button wis;
	Button keer;
	TextBox keerText;
	ScrollPanel scrollPanel;
	Timer elapsedTimer;
	int[] trekkingen;
	Boolean[] trekkingenGeschiedenis;
	int maxCount;
	int experiment;
	int trekkingCount;
	int totaal;
	Boolean multipleTimes=false;
	int numberOfTimes;
	Boolean stapStarted=false;
	Boolean stapStarted1=false;
	Boolean stopCounting;
	BinomRooster binomRooster;
	BinomGrafiek binomGrafiek;
	Boolean showKans=true;
	Boolean showPopulatieProportie=false;
	Label kansLabel;
	LayoutPanel panel;
	FlowPanel panel2;
	VerticalPanel panel1;
	HTML html;
	StatSimGWT ssgwt;
	boolean binomTrekkingRooster;
	
	public static class Experiment {
		private final String experimentNumber;
	    private final String outcome;
	    
	    public Experiment (String experimentNumber, String outcome) {
	    	this.experimentNumber = experimentNumber;
	    	this.outcome = outcome;
	    }
	    
	    public String getExpNumber() {
	    	return experimentNumber;
	    }
	    public String getOutcome () {
	    	return outcome;
	    }
	}

	CellTable<Experiment> table;
	
	protected ListDataProvider<Experiment> dataProvider;
	
	CssColor agKleur = CssColor.make(255, 255, 255);
	
	public BinomTrekking(StatSimGWT ssgwt, boolean binomTrekkingInstellingen, boolean binomTrekkingGrafiek, boolean binomTrekkingTabel, boolean binomTrekkingRooster) {
		this.ssgwt=ssgwt;
		this.binomTrekkingRooster=binomTrekkingRooster;
		
		kladjeHWTCanvas = Canvas.createIfSupported(); 

		kladjeHWTCanvas.setWidth("200px");
		kladjeHWTCanvas.setHeight("340px");
		kladjeHWTCanvas.setCoordinateSpaceWidth(200);
		kladjeHWTCanvas.setCoordinateSpaceHeight(350);

		binomGrafiekCanvas = Canvas.createIfSupported(); 

		binomGrafiekCanvas.setWidth("360px");
		binomGrafiekCanvas.setHeight("340px");
		binomGrafiekCanvas.setCoordinateSpaceWidth(360);
		binomGrafiekCanvas.setCoordinateSpaceHeight(350);

		if (kladjeHWTCanvas == null || binomGrafiekCanvas==null) 
		{
	      RootPanel.get().add(new Label(upgradeMessage));
	      return;
	    }
		
		gIm = kladjeHWTCanvas.getContext2d();		
		gIm2 = binomGrafiekCanvas.getContext2d();		
		
		
		
		
		panel = new LayoutPanel();
	    panel.setSize("790px", "110px");

	    panel2=new FlowPanel();
	    panel.add(panel2);
	    
	    panel.setWidgetLeftRight(panel2,10,Unit.PX,560,Unit.PX);
	    panel.setWidgetTopBottom(panel2,0,Unit.PX,0,Unit.PX);
	    
	    Label instellingenLabel=new Label("Instellingen");
	    instellingenLabel.getElement().getStyle().setMarginBottom(5, Unit.PX);
	    panel2.add(instellingenLabel);
	    
	    HorizontalPanel panel3=new HorizontalPanel();
	    panel3.getElement().getStyle().setMarginBottom(5, Unit.PX);
	    panel3.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	    kansLabel=new Label("Kans");
	    kansLabel.getElement().getStyle().setMarginRight(10, Unit.PX);
	    kansText=new TextBox();	    
	    kansText.setText("0.2");
	    kansText.setVisibleLength(2);
	    panel3.add(kansLabel);
	    panel3.add(kansText);
	    
	    HorizontalPanel panel4=new HorizontalPanel();
	    panel4.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	    Label aantalTrekkingenLabel=new Label("Aantal trekkingen");
	    aantalTrekkingenLabel.getElement().getStyle().setMarginRight(10, Unit.PX);
	    aantalTrekkingenText=new TextBox();
	    aantalTrekkingenText.setText("20");
	    aantalTrekkingenText.setVisibleLength(2);
	    panel4.add(aantalTrekkingenLabel);
	    panel4.add(aantalTrekkingenText);
	    
	    panel2.add(panel3);
	    panel2.add(panel4);
	    
	    if (binomTrekkingInstellingen==false)
	    	panel2.setVisible(false);
	    
	    voeruit = new Button("Voer uit",this);
	    voeruit.getElement().getStyle().setMarginRight(5, Unit.PX);
	    stap = new Button("Stap",this);
	    keer = new Button("keer uit",this);
	    wis = new Button("Wis resultaten",this);
	    
	    panel1=new VerticalPanel();
	    HorizontalPanel panel5=new HorizontalPanel();
	    panel5.getElement().getStyle().setMarginBottom(5, Unit.PX);
	    panel1.add(panel5);
	    panel5.add(voeruit);
	    panel5.add(stap);
	    
	    HorizontalPanel panel6=new HorizontalPanel();
	    panel6.getElement().getStyle().setMarginBottom(5, Unit.PX);
	    panel6.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	    panel1.add(panel6);
	    
	    Label voeruitLabel=new Label("Voer");
	    keerText=new TextBox();
	    keerText.getElement().getStyle().setMarginRight(5, Unit.PX);
	    keerText.getElement().getStyle().setMarginLeft(5, Unit.PX);
	    keerText.setText("20");
	    keerText.setVisibleLength(1);
	    
	    panel6.add(voeruitLabel);
	    panel6.add(keerText);
	    panel6.add(keer);
	    
	    panel1.add(wis);
	    panel.add(panel1);
	    panel.setWidgetLeftRight(panel1, 240, Unit.PX, 340, Unit.PX);     // Center panel
	    panel.setWidgetTopBottom(panel1, 5, Unit.PX, 5, Unit.PX);
	    

	    // Create a CellTable.
	    table = new CellTable<Experiment>();
	    table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
	    table.setPageSize(1000);
	    
	    dataProvider = new ListDataProvider<Experiment>();
		// 	Add the table to the dataProvider.
		dataProvider.addDataDisplay(table);
		
	    
	    // Add a text column to show the name.
	    TextColumn<Experiment> expColumn = new TextColumn<Experiment>() {
	      @Override
	      public String getValue(Experiment object) {
	        return object.experimentNumber;
	      }
	    };
	    //table.addColumn(expColumn, "Exp.");
	    table.addColumn(expColumn);
	    table.setColumnWidth(expColumn, 55.0, Unit.PX);

	    // Add a text column to show the address.
	    TextColumn<Experiment> outcomeColumn = new TextColumn<Experiment>() {
	      @Override
	      public String getValue(Experiment object) {
	        return object.outcome;
	      }
	    };
	    //table.addColumn(outcomeColumn, "Uitkomst");
	    table.addColumn(outcomeColumn);
	    table.setColumnWidth(outcomeColumn, 90.0, Unit.PX);
	    
	    scrollPanel = new ScrollPanel(table);
	    scrollPanel.setSize("180px", "240px");
	    
	    HorizontalPanel panel7=new HorizontalPanel();
	    // Add it to the root panel.
	    VerticalPanel panel8 = new VerticalPanel();
	    panel7.add(panel8);
	    
	    html = new HTML("<table width=145><tr><td><font face=arial size=2><b>Exp.</b></font></td><td><font face=arial size=2><b>Uitkomst</b></font></td></tr></table>");
	    panel8.add(html);
	    
	    
	    panel8.add(scrollPanel);
	    if (binomTrekkingTabel==false)
	    	scrollPanel.setVisible(false);
	    
	    table.setRowCount(0, true);
	    
	    //RootPanel.get().add(panel);
	    //RootPanel.get().add(panel7);
	    
	    add(panel);
	    add(panel7);

	    panel7.add(kladjeHWTCanvas);
	    panel7.add(binomGrafiekCanvas);
	    
	    if (binomTrekkingRooster==false)
	    	kladjeHWTCanvas.setVisible(false);
	    
	    if (binomTrekkingGrafiek==false)
	    	binomGrafiekCanvas.setVisible(false);
	    
		 elapsedTimer = new Timer () {
			 public void run() {
			        //count=count+1;
			        //label.setText(Integer.toString(count));
			        //if (count==100) elapsedTimer.cancel();
			        doeStap();
			 }
		 };

		 trekkingen = new int[10000];
		    maxCount=Integer.parseInt(aantalTrekkingenText.getText());
		    trekkingenGeschiedenis = new Boolean[10000];
		 binomRooster=new BinomRooster(this);
	     binomRooster.paint();
	     
	     binomGrafiek=new BinomGrafiek(this);
	     binomGrafiek.paint();
	}
	
	public void setGrootte(int breedte, int hoogte) {
		int dummy=(breedte-170)/2;
		if (dummy>0){
			kladjeHWTCanvas.setWidth(Integer.toString(dummy)+"px");
			kladjeHWTCanvas.setCoordinateSpaceWidth(dummy);
			binomGrafiekCanvas.setWidth(Integer.toString(dummy)+"px");
			binomGrafiekCanvas.setCoordinateSpaceWidth(dummy);			
		}
		if (hoogte-110>0) {
			kladjeHWTCanvas.setHeight(Integer.toString(hoogte-110)+"px");
			kladjeHWTCanvas.setCoordinateSpaceHeight(hoogte-110);
			binomGrafiekCanvas.setHeight(Integer.toString(hoogte-110)+"px");
			binomGrafiekCanvas.setCoordinateSpaceHeight(hoogte-110);
		}
		if (hoogte>210)
			scrollPanel.setSize("180px", Integer.toString(hoogte-210)+"px");
		
		if (dummy>0 && hoogte>210) {
			binomRooster.setGrootte(dummy,hoogte-210);
			binomGrafiek.setGrootte(dummy,hoogte-210);
		}
		panel.setSize(Integer.toString(breedte)+"px", "110px");
		//if (breedte>230)
			panel.setWidgetLeftRight(panel2,10,Unit.PX,Math.max(0, breedte-230),Unit.PX);
		//if (breedte>450)
			panel.setWidgetLeftRight(panel1, 240, Unit.PX, Math.max(0, breedte-450), Unit.PX);     // Center panel
	}
	
	public void fireCBook() {
		
		
		List<BinomTrekking.Experiment> list = (List<BinomTrekking.Experiment>) dataProvider.getList();
		
		String string1="";
		
		for (int i=0;i<list.size();i++) {
			string1=string1+list.get(i).getOutcome()+"\n";			
		}
		
		//r=((double)Math.round(r*100))/100;
		String string2="";
		for (int i=0;i<experiment;i++) {
			double proportion = Double.parseDouble(list.get(i).getOutcome())/maxCount;
			proportion=((double)Math.round(proportion*100))/100;
			string2=string2+proportion+"\n";
		}
		
		ssgwt.fireCBookBinomTrekking(string1, string2);
	}
	
	public void setZichtbaar() {
		if (showKans)
			kansLabel.setText("Kans");
		if (showPopulatieProportie)
			kansLabel.setText("PopulatieProportie");
		
	}
	
	public void setStartStop() {
		if (wis.isEnabled()==true) {
			kansText.setEnabled(false);
			aantalTrekkingenText.setEnabled(false);
		} else {
			kansText.setEnabled(true);
			aantalTrekkingenText.setEnabled(true);
		}
	}
	

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		if (event.getSource()==voeruit) {
			voeruit.setEnabled(false);
			keer.setEnabled(false);
			stap.setEnabled(false);
			wis.setEnabled(true);
			setStartStop();
			stopCounting=false;
			if (!stapStarted1) {
				trekkingCount=0;
				totaal=0;
			} else {
				stapStarted1=false;
			}
			stapStarted=true;
			maxCount=Integer.parseInt(aantalTrekkingenText.getText());
			if (binomTrekkingRooster==true) {
				elapsedTimer.scheduleRepeating(10);
			} else {
				while(stopCounting==false) {
					doeStap();
				}
			}
		}
		if (event.getSource()==stap) {
			if (!stapStarted1) {
				voeruit.setEnabled(true);
				keer.setEnabled(false);
				wis.setEnabled(true);
				setStartStop();
				stapStarted1=true;
				trekkingCount=0;
				totaal=0;
			}
			doeStap();
			if (trekkingCount==maxCount) {
				stapStarted1=false;
			}
		}
		if (event.getSource()==keer) {
			stopCounting=false;
			trekkingCount=0;
			totaal=0;
			keer.setEnabled(false);
			voeruit.setEnabled(false);
			numberOfTimes=Integer.parseInt(keerText.getText());
			maxCount=Integer.parseInt(aantalTrekkingenText.getText());
			multipleTimes=true;
			//elapsedTimer.scheduleRepeating(1);
			while (stopCounting==false) {
				doeStap();
			}
		}
		if (event.getSource()==wis) {
			wis.setEnabled(false);
			voeruit.setEnabled(true);
			keer.setEnabled(true);
			setStartStop();
			experiment=0;
			List dataList=dataProvider.getList();
			dataList.clear();
			stapStarted=false;
			trekkingCount=0;
			totaal=0;
			binomRooster.paint();
			binomGrafiek.paint();
			fireCBook();
		}
	}
	

	public String replaceComma(String oldString)
	{
		String newString=oldString.replace(",",".");				
		return newString;
	}
	
	public void doeStap() {
		double r = Math.random();
		
		if (r<Double.parseDouble(replaceComma(kansText.getText()))) {
			totaal=totaal+1;
			trekkingenGeschiedenis[trekkingCount]=true;
		} else {
			trekkingenGeschiedenis[trekkingCount]=false;
		}
		trekkingCount=trekkingCount+1;
		
		//List<Experiment> ADDEXP = Arrays.asList(
		//		new Experiment(Integer.toString(experiment+1), Integer.toString(totaal)));
        //Window.alert(Integer.toString(totaal));
		//table.setRowData(experiment,ADDEXP);
		
		Experiment ADDEXP = new Experiment(Integer.toString(experiment+1), Integer.toString(totaal));

		List dataList=dataProvider.getList();
		if (dataList.size()==experiment)
			dataList.add(ADDEXP);
		else
			dataList.set(experiment, ADDEXP);
				
		if (trekkingCount==maxCount) {
			trekkingen[experiment]=totaal;
			experiment++;
			if (multipleTimes==false) {
				fireCBook();
				stopCounting=true;
				elapsedTimer.cancel();
				keer.setEnabled(true);
				voeruit.setEnabled(true);
				stap.setEnabled(true);
			} else {
				numberOfTimes--;
				if (numberOfTimes==0) {
					fireCBook();
					stopCounting=true;
					elapsedTimer.cancel();
					multipleTimes=false;
					keer.setEnabled(true);
					voeruit.setEnabled(true);
					stap.setEnabled(true);
				}
				trekkingCount=0;
				totaal=0;
			}
		}
		
		if (multipleTimes==false || numberOfTimes==0) {
			binomRooster.paint();
			binomGrafiek.paint();
		}
	}

}

