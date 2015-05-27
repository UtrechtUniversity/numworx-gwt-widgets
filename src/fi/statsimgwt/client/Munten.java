package fi.statsimgwt.client;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SingleSelectionModel;

public class Munten extends FlowPanel implements ClickHandler{

	
	static final String upgradeMessage = 
			"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	public Canvas kladjeHWTCanvas;
	public Context2d gIm;
	public Canvas frequentieCanvas;
	public Context2d gIm2;
	public Button voeruit;
	public Button stap;
	public Button wis;
	public Label label;
	public Label avgResult1;
	public Label minResult1;
	public Label maxResult1;
	public Label avgResult;
	public Label minResult;
	public Label maxResult;
	public Label avgResult2;
	public Label minResult2;
	public Label maxResult2;
	public Label zeroKopResult3;
	public Label avgResult3;
	public Label minResult3;
	public Label maxResult3;
	public Label oneKopResult4;
	public Label avgResult4;
	public Label minResult4;
	public Label maxResult4;
	public Label twoKopResult5;
	public Label avgResult5;
	public Label minResult5;
	public Label maxResult5;
	public Timer elapsedTimer;
	public RadioButton eenMunt;
	public RadioButton tweeMunten;
	public RadioButton aantalKopRadio;
	public RadioButton percentageKopRadio;
	public TextBox aantalWorpenText;
	public TextBox kansOpKopText;
	Boolean[] munt;
	double[] percentageMunt;
	int muntCount;
	Boolean stopCounting;
	int experiment;
	int totaalmunt;
	int maxCount;
	Grafiek grafiek;
	Frequentie frequentie;
	ScrollPanel scrollPanel;
	ScrollPanel scrollPanel2;
	int geenKop;
	int eenKop;
	int tweeKop;
	boolean muntenTabel1;
	
	private static class Experiment {
		private final String experimentNumber;
	    private final String kopValue;
	    private final String muntValue;

	    public Experiment (String experimentNumber, String kopValue,String muntValue) {
	    	this.experimentNumber = experimentNumber;
	    	this.kopValue = kopValue;
	    	this.muntValue=muntValue;
	    }
	}

	private static class Experiment2 {
		private final String experimentNumber;
	    private final String geenKop;
	    private final String eenKop;
	    private final String tweeKop;

	    public Experiment2 (String experimentNumber, String geenKop, String eenKop,String tweeKop) {
	    	this.experimentNumber = experimentNumber;
	    	this.geenKop = geenKop;
	    	this.eenKop=eenKop;
	    	this.tweeKop=tweeKop;
	    }
	}

	CellTable<Experiment> table;
	CellTable<Experiment2> table2;
	/**
	 * The list of data to display.
	 */
	private static final List<Experiment> EXPERIMENTS = Arrays.asList(
			new Experiment("1", "53","47"),
			new Experiment("2", "48","52"),
			new Experiment("3", "49","51"));

	                   
	CssColor lijnenKleur = CssColor.make(0, 0, 0);
	CssColor agKleur = CssColor.make(255, 255, 255);

	
	public Munten (boolean muntenInstellingen, boolean muntenResultaten, boolean muntenGrafiek, boolean muntenTabel, boolean muntenFrequentie) {
		muntenTabel1=muntenTabel;
		
		kladjeHWTCanvas = Canvas.createIfSupported(); 

		kladjeHWTCanvas.setWidth("560px");
		kladjeHWTCanvas.setHeight("350px");
		kladjeHWTCanvas.setCoordinateSpaceWidth(560);
		kladjeHWTCanvas.setCoordinateSpaceHeight(350);

		frequentieCanvas = Canvas.createIfSupported(); 

		frequentieCanvas.setWidth("200px");
		frequentieCanvas.setHeight("100px");
		frequentieCanvas.setCoordinateSpaceWidth(200);
		frequentieCanvas.setCoordinateSpaceHeight(100);
		
		if (kladjeHWTCanvas == null || frequentieCanvas==null) 
		{
	      RootPanel.get().add(new Label(upgradeMessage));
	      return;
	    }
		
		gIm = kladjeHWTCanvas.getContext2d();		
		gIm2 = frequentieCanvas.getContext2d();		
		
		
	    // Make a new button that does something when you click it.
	    voeruit = new Button("Voer uit",this);
	    stap = new Button("Stap",this);
	    wis = new Button("Wis",this);
	    
	    label = new Label("Label");
	    
	    LayoutPanel panel = new LayoutPanel();
	    panel.setSize("790px", "100px");

	    VerticalPanel results2=new VerticalPanel();
	    panel.add(results2);
	    panel.setWidgetLeftRight(results2,400,Unit.PX,200,Unit.PX);
	    panel.setWidgetTopBottom(results2,25,Unit.PX,10,Unit.PX);
	    
	    avgResult2=new Label("Gemiddelde");
	    
	    minResult2=new Label("Minimum");
	    maxResult2=new Label("Maximum");
	    results2.add(avgResult2);
	    results2.add(minResult2);
	    results2.add(maxResult2);
	    
	    if (muntenResultaten==false)
	    	results2.setVisible(false);
	    
	    VerticalPanel results3=new VerticalPanel();
	    panel.add(results3);
	    panel.setWidgetLeftRight(results3,500,Unit.PX,100,Unit.PX);
	    panel.setWidgetTopBottom(results3,10,Unit.PX,10,Unit.PX);
	    
	    zeroKopResult3=new Label("0 Kop");
	    avgResult3=new Label("0.0");
	    minResult3=new Label("0.0");
	    maxResult3=new Label("0.0");
	    results3.add(zeroKopResult3);
	    results3.add(avgResult3);
	    results3.add(minResult3);
	    results3.add(maxResult3);
	    
	    if (muntenResultaten==false)
	    	results3.setVisible(false);
	    
	    VerticalPanel results4=new VerticalPanel();
	    panel.add(results4);
	    panel.setWidgetLeftRight(results4,570,Unit.PX,100,Unit.PX);
	    panel.setWidgetTopBottom(results4,10,Unit.PX,10,Unit.PX);
	    
	    oneKopResult4=new Label("1 Kop");
	    avgResult4=new Label("0.0");
	    minResult4=new Label("0.0");
	    maxResult4=new Label("0.0");
	    results4.add(oneKopResult4);
	    results4.add(avgResult4);
	    results4.add(minResult4);
	    results4.add(maxResult4);
	    
	    if (muntenResultaten==false)
	    	results4.setVisible(false);
	    

	    VerticalPanel results5=new VerticalPanel();
	    panel.add(results5);
	    panel.setWidgetLeftRight(results5,640,Unit.PX,100,Unit.PX);
	    panel.setWidgetTopBottom(results5,10,Unit.PX,10,Unit.PX);
	    
	    twoKopResult5=new Label("2 Kop");
	    avgResult5=new Label("0.0");
	    minResult5=new Label("0.0");
	    maxResult5=new Label("0.0");
	    results5.add(twoKopResult5);
	    results5.add(avgResult5);
	    results5.add(minResult5);
	    results5.add(maxResult5);
	    
	    if (muntenResultaten==false)
	    	results5.setVisible(false);
	    
	    
	    VerticalPanel results=new VerticalPanel();
	    panel.add(results);
	    panel.setWidgetLeftRight(results,570,Unit.PX,100,Unit.PX);
	    panel.setWidgetTopBottom(results,10,Unit.PX,10,Unit.PX);
	    
	    if (muntenResultaten==false)
	    	results.setVisible(false);
	    
	    avgResult = new Label("Gemiddelde");
	    minResult=new Label ("Minimum");
	    maxResult=new Label("Maximum");
	    results.add(avgResult);
	    results.add(minResult);
	    results.add(maxResult);
	    
	    VerticalPanel results1=new VerticalPanel();
	    panel.add(results1);
	    panel.setWidgetLeftRight(results1,720,Unit.PX,10,Unit.PX);
	    panel.setWidgetTopBottom(results1,10,Unit.PX,10,Unit.PX);
	    
	    if (muntenResultaten==false)
	    	results1.setVisible(false);
	    
	    avgResult1=new Label("0.0");
	    minResult1=new Label("0.0");
	    maxResult1=new Label("0.0");
	    results1.add(avgResult1);
	    results1.add(minResult1);
	    results1.add(maxResult1);
	    
	    avgResult2.setVisible(false);
		minResult2.setVisible(false);
		maxResult2.setVisible(false);
		zeroKopResult3.setVisible(false);
		avgResult3.setVisible(false);
		minResult3.setVisible(false);
		maxResult3.setVisible(false);
		oneKopResult4.setVisible(false);
		avgResult4.setVisible(false);
		minResult4.setVisible(false);
		maxResult4.setVisible(false);
		twoKopResult5.setVisible(false);
		avgResult5.setVisible(false);
		minResult5.setVisible(false);
		maxResult5.setVisible(false);
	    
	    VerticalPanel radios=new VerticalPanel();
	    panel.add(radios);
	    
	    if (muntenResultaten==false)
	    	radios.setVisible(false);
	    
	    panel.setWidgetLeftRight(radios,370,Unit.PX,100,Unit.PX);
	    panel.setWidgetTopBottom(radios,10,Unit.PX,10,Unit.PX);
	    
	    aantalKopRadio = new RadioButton("myRadioGroup1","Aantal kop");
	    aantalKopRadio.setValue(true);
	    aantalKopRadio.addClickHandler(this);
	    percentageKopRadio = new RadioButton ("myRadioGroup1","Percentage kop");
	    percentageKopRadio.addClickHandler(this);
	    
	    radios.add(aantalKopRadio);
	    radios.add(percentageKopRadio);
	    
	    FlowPanel panel2=new FlowPanel();
	    panel.add(panel2);
	    
	    panel.setWidgetLeftRight(panel2,10,Unit.PX,560,Unit.PX);
	    panel.setWidgetTopBottom(panel2,0,Unit.PX,0,Unit.PX);
	    
	    Label instellingenLabel=new Label("Instellingen");
	    eenMunt=new RadioButton("myRadioGroup","Een munt");
	    eenMunt.addClickHandler(this);
	    tweeMunten=new RadioButton("myRadioGroup","Twee munten");
	    tweeMunten.addClickHandler(this);
	    eenMunt.setValue(true);
	    
	    panel2.add(instellingenLabel);
	    panel2.add(eenMunt);
	    panel2.add(tweeMunten);
	    	
	    Label aantalWorpenLabel=new Label("Aantal worpen");
	    Label kansOpKopLabel=new Label("Kans op kop");
	    aantalWorpenText=new TextBox();
	    aantalWorpenText.setText("100");
	    aantalWorpenText.setVisibleLength(2);
	    kansOpKopText=new TextBox();
	    kansOpKopText.setText("0.5");
	    kansOpKopText.setVisibleLength(2);
	    
	    HorizontalPanel panel3=new HorizontalPanel();
	    panel3.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	    panel3.add(aantalWorpenLabel);
	    panel3.add(aantalWorpenText);
	    panel2.add(panel3);
	    HorizontalPanel panel4=new HorizontalPanel();
	    panel4.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	    panel4.add(kansOpKopLabel);
	    panel4.add(kansOpKopText);
	    panel2.add(panel4);
	    
	    if (muntenInstellingen==false)
	    	panel2.setVisible(false);
	    
	    		// Add it to the root panel.
	    VerticalPanel panel1=new VerticalPanel();
	    
	    panel1.add(voeruit);
	    panel1.add(stap);
	    panel1.add(wis);
	    //panel2.add(label);
	    panel.add(panel1);
	    panel.setWidgetLeftRight(panel1, 240, Unit.PX, 440, Unit.PX);     // Center panel
	    panel.setWidgetTopBottom(panel1, 5, Unit.PX, 5, Unit.PX);
	    
	    //RootPanel.get().add(panel);
	   add(panel);
		
		 elapsedTimer = new Timer () {
			 public void run() {
			        //count=count+1;
			        //label.setText(Integer.toString(count));
			        //if (count==100) elapsedTimer.cancel();
			        doeStap();
			 }
		 };
		 
		    // Create a CellTable.
		    table = new CellTable<Experiment>();
		    table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		    table.setPageSize(1000);
		    
		    // Add a text column to show the name.
		    TextColumn<Experiment> expColumn = new TextColumn<Experiment>() {
		      @Override
		      public String getValue(Experiment object) {
		        return object.experimentNumber;
		      }
		    };
		    table.addColumn(expColumn, "Exp.");

		    // Add a text column to show the address.
		    TextColumn<Experiment> kopValueColumn = new TextColumn<Experiment>() {
		      @Override
		      public String getValue(Experiment object) {
		        return object.kopValue;
		      }
		    };
		    table.addColumn(kopValueColumn, "Aantal kop");

		    // Add a text column to show the address.
		    TextColumn<Experiment> muntValueColumn = new TextColumn<Experiment>() {
		      @Override
		      public String getValue(Experiment object) {
		        return object.muntValue;
		      }
		    };
		    table.addColumn(muntValueColumn, "Aantal munt");

		    // Create a CellTable.
		    table2 = new CellTable<Experiment2>();
		    table2.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		    table2.setPageSize(1000);
		    
		    // Add a text column to show the name.
		    TextColumn<Experiment2> expColumn2 = new TextColumn<Experiment2>() {
		      @Override
		      public String getValue(Experiment2 object) {
		        return object.experimentNumber;
		      }
		    };
		    table2.addColumn(expColumn2, "Exp.");

		    // Add a text column to show the address.
		    TextColumn<Experiment2> geenKopValueColumn = new TextColumn<Experiment2>() {
		      @Override
		      public String getValue(Experiment2 object) {
		        return object.geenKop;
		      }
		    };
		    table2.addColumn(geenKopValueColumn, "0 kop");

		    // Add a text column to show the address.
		    TextColumn<Experiment2> eenKopValueColumn = new TextColumn<Experiment2>() {
		      @Override
		      public String getValue(Experiment2 object) {
		        return object.eenKop;
		      }
		    };
		    table2.addColumn(eenKopValueColumn, "1 kop");
		 
		    // Add a text column to show the address.
		    TextColumn<Experiment2> tweeKopValueColumn = new TextColumn<Experiment2>() {
		      @Override
		      public String getValue(Experiment2 object) {
		        return object.tweeKop;
		      }
		    };
		    table2.addColumn(tweeKopValueColumn, "2 kop");
		    
		    scrollPanel = new ScrollPanel(table);
		    scrollPanel.setSize("230px", "250px");
		    
		    scrollPanel2 = new ScrollPanel(table2);
		    scrollPanel2.setSize("230px", "350px");
		   
		    if (muntenTabel==false)
		    	scrollPanel.setVisible(false);
		    scrollPanel2.setVisible(false);
		    
		    HorizontalPanel panel5=new HorizontalPanel();
		    // Add it to the root panel.
		    VerticalPanel panel6 = new VerticalPanel();
		    panel5.add(panel6);
	
		    if (muntenFrequentie==false)
		    	frequentieCanvas.setVisible(false);
		    
		    panel6.add(frequentieCanvas);
		    panel6.add(scrollPanel);
		    panel6.add(scrollPanel2);
		    
		    maxCount=100;
		    
		    table.setRowCount(0, true);
		    table2.setRowCount(0, true);
		    
 		    grafiek=new Grafiek(this);
		    grafiek.paint();
		    
		    frequentie=new Frequentie(this);
		    frequentie.paint();
		    
		    panel5.add(kladjeHWTCanvas);
		    //RootPanel.get().add(panel5);
		    add(panel5);
			
		    if (muntenGrafiek==false)
		    	kladjeHWTCanvas.setVisible(false);
		    
			munt= new Boolean[10001];
			percentageMunt=new double[10001];
			
			
	}
	

	public void setEenMuntTweeMunten() {
		if (eenMunt.getValue()==true) {
			aantalKopRadio.setVisible(true);
			percentageKopRadio.setVisible(true);
			avgResult.setVisible(true);
			minResult.setVisible(true);
			maxResult.setVisible(true);
			avgResult1.setVisible(true);
			minResult1.setVisible(true);
			maxResult1.setVisible(true);
			avgResult2.setVisible(false);
			minResult2.setVisible(false);
			maxResult2.setVisible(false);
			zeroKopResult3.setVisible(false);
			avgResult3.setVisible(false);
			minResult3.setVisible(false);
			maxResult3.setVisible(false);
			oneKopResult4.setVisible(false);
			avgResult4.setVisible(false);
			minResult4.setVisible(false);
			maxResult4.setVisible(false);
			twoKopResult5.setVisible(false);
			avgResult5.setVisible(false);
			minResult5.setVisible(false);
			maxResult5.setVisible(false);
		} else {
			aantalKopRadio.setVisible(false);
			percentageKopRadio.setVisible(false);
			avgResult.setVisible(false);
			minResult.setVisible(false);
			maxResult.setVisible(false);
			avgResult1.setVisible(false);
			minResult1.setVisible(false);
			maxResult1.setVisible(false);
			avgResult2.setVisible(true);
			minResult2.setVisible(true);
			maxResult2.setVisible(true);
			zeroKopResult3.setVisible(true);
			avgResult3.setVisible(true);
			minResult3.setVisible(true);
			maxResult3.setVisible(true);
			oneKopResult4.setVisible(true);
			avgResult4.setVisible(true);
			minResult4.setVisible(true);
			maxResult4.setVisible(true);
			twoKopResult5.setVisible(true);
			avgResult5.setVisible(true);
			minResult5.setVisible(true);
			maxResult5.setVisible(true);			
		}
	}
	
	public void setResults () {
		if (eenMunt.getValue()==true) {
			if (aantalKopRadio.getValue()==true) {	
				double aa=Math.round(gemiddeldeKop*10);
				avgResult1.setText(Double.toString(aa/10));
				maxResult1.setText(Integer.toString(maximumKop));
				minResult1.setText(Integer.toString(minimumKop));
			} else {
				double aa=Math.round((gemiddeldeKop/maxCount)*1000);
				avgResult1.setText(Double.toString(aa/10)+"%");
				aa=Math.round(((double)maximumKop/maxCount)*1000);
				maxResult1.setText(Double.toString(aa/10)+"%");
				aa=Math.round(((double)minimumKop/maxCount)*1000);
				minResult1.setText(Double.toString(aa/10)+"%");
			}
		} else {
			double aa=Math.round(gemiddeldeGeenKop*10);
			avgResult3.setText(Double.toString(aa/10));
			maxResult3.setText(Integer.toString(maximumGeenKop));
			minResult3.setText(Integer.toString(minimumGeenKop));
			aa=Math.round(gemiddeldeEenKop*10);
			avgResult4.setText(Double.toString(aa/10));
			maxResult4.setText(Integer.toString(maximumEenKop));
			minResult4.setText(Integer.toString(minimumEenKop));
			aa=Math.round(gemiddeldeTweeKop*10);
			avgResult5.setText(Double.toString(aa/10));
			maxResult5.setText(Integer.toString(maximumTweeKop));
			minResult5.setText(Integer.toString(minimumTweeKop));			
		}
	}
	
	
	int count;
	boolean stapStarted=false;
	public void onClick (ClickEvent event) {
		if (event.getSource()==voeruit) {
			voeruit.setEnabled(false);
			stap.setEnabled(false);
			wis.setEnabled(true);
			eenMunt.setEnabled(false);
			tweeMunten.setEnabled(false);
			aantalWorpenText.setEnabled(false);
			kansOpKopText.setEnabled(false);
			maxCount=Integer.parseInt(aantalWorpenText.getText());
			if (!stapStarted) {
				muntCount=0;
				totaalmunt=0;
				geenKop=0;
				eenKop=0;
				tweeKop=0;
			} else {
				stapStarted=false;
			}
			elapsedTimer.scheduleRepeating(10);
		}
		if (event.getSource()==wis) {
			wis.setEnabled(false);
			eenMunt.setEnabled(true);
			tweeMunten.setEnabled(true);
			aantalWorpenText.setEnabled(true);
			kansOpKopText.setEnabled(true);
			experiment=0;
			muntCount=0;
			totaalmunt=0;
			table.setRowCount(0, true);
			table2.setRowCount(0, true);
			geenKop=0;
			eenKop=0;
			tweeKop=0;
			grafiek.paint();
			frequentie.paint();
			avgResult3.setText("");
			maxResult3.setText("");
			minResult3.setText("");
			avgResult4.setText("");
			maxResult4.setText("");
			minResult4.setText("");
			avgResult5.setText("");
			maxResult5.setText("");
			minResult5.setText("");			
			avgResult1.setText("");
			maxResult1.setText("");
			minResult1.setText("");

		}
		if (event.getSource()==stap) {
			wis.setEnabled(true);
			eenMunt.setEnabled(false);
			tweeMunten.setEnabled(false);
			aantalWorpenText.setEnabled(false);
			kansOpKopText.setEnabled(false);
			if (!stapStarted) {
				maxCount=Integer.parseInt(aantalWorpenText.getText());
				muntCount=0;
				totaalmunt=0;
				geenKop=0;
				eenKop=0;
				tweeKop=0;
				stapStarted=true;
			}
			doeStap();
		}
		if (event.getSource()==eenMunt) {
			frequentieCanvas.setVisible(true);
			if (muntenTabel1==true)
				scrollPanel.setVisible(true);
			scrollPanel2.setVisible(false);
			grafiek.paint();
			frequentie.paint();
			setEenMuntTweeMunten();
		}
		if (event.getSource()==tweeMunten) {
			frequentieCanvas.setVisible(false);
			scrollPanel.setVisible(false);
			if (muntenTabel1==true)
				scrollPanel2.setVisible(true);
			grafiek.paint();
			setEenMuntTweeMunten();
		}
		if (event.getSource()==aantalKopRadio) {
			setResults();
		}
		if (event.getSource()==percentageKopRadio) {
			setResults();
		}
	}
	

	public String replaceComma(String oldString)
	{
		String newString=oldString.replace(",",".");				
		return newString;
	}
	
	double gemiddeldeKop;
	int minimumKop;
	int maximumKop;
	double gemiddeldeGeenKop;
	double gemiddeldeEenKop;
	double gemiddeldeTweeKop;
	int minimumGeenKop;
	int minimumEenKop;
	int minimumTweeKop;
	int maximumGeenKop;
	int maximumEenKop;
	int maximumTweeKop;
	public void doeStap() {
		double r=Math.random();
		
		if (eenMunt.getValue()==true) {
			if (r>Double.parseDouble(replaceComma(kansOpKopText.getText()))) {
				munt[muntCount]=true;
				totaalmunt=totaalmunt+1;
				if (muntCount>0)
					percentageMunt[muntCount]=(percentageMunt[muntCount-1]*(muntCount)+1)/(muntCount+1);
				else
				   percentageMunt[muntCount]=1;
			} else {
				munt[muntCount]=false;
				if (muntCount>0)
					percentageMunt[muntCount]=(percentageMunt[muntCount-1]*(muntCount))/(muntCount+1);
				else
					percentageMunt[muntCount]=0;
			}
			muntCount++;

			List<Experiment> ADDEXP = Arrays.asList(
					new Experiment(Integer.toString(experiment+1), Integer.toString(muntCount-totaalmunt),Integer.toString(totaalmunt)));

			table.setRowData(experiment,ADDEXP);
		} else {
			   double s = Math.random();
			   
			   if (r>Double.parseDouble(kansOpKopText.getText()) && s>Double.parseDouble(kansOpKopText.getText()))
				   tweeKop++;
			   if (r>Double.parseDouble(kansOpKopText.getText()) && s<=Double.parseDouble(kansOpKopText.getText()) || r<=Double.parseDouble(kansOpKopText.getText()) && s>Double.parseDouble(kansOpKopText.getText()))
				   eenKop++;
			   if (r<=Double.parseDouble(kansOpKopText.getText()) && s<=Double.parseDouble(kansOpKopText.getText()))
				   geenKop++;
			   
			   muntCount++;
			   
			   List<Experiment2> ADDEXP2 = Arrays.asList(
						new Experiment2(Integer.toString(experiment+1), Integer.toString(geenKop),Integer.toString(eenKop),Integer.toString(tweeKop)));

				table2.setRowData(experiment,ADDEXP2);
		   }
			
		   if (muntCount>=maxCount) {
			   stopCounting=true;
			   elapsedTimer.cancel();
			   stapStarted=false;
			   voeruit.setEnabled(true);
			   stap.setEnabled(true);
			   
			   if (eenMunt.getValue()==true) {
				   gemiddeldeKop=(gemiddeldeKop*(experiment)+muntCount-totaalmunt)/(experiment+1);
				   if (muntCount-totaalmunt>maximumKop)
					   maximumKop=muntCount-totaalmunt;
				   if (muntCount-totaalmunt<minimumKop)
					   minimumKop=muntCount-totaalmunt;
				   if (experiment==0) {
					   maximumKop=muntCount-totaalmunt;
					   minimumKop=muntCount-totaalmunt;
				   }
			   } else {
				   gemiddeldeGeenKop=(gemiddeldeGeenKop*(experiment)+geenKop)/(experiment+1);
				   gemiddeldeEenKop=(gemiddeldeEenKop*(experiment)+eenKop)/(experiment+1);
				   gemiddeldeTweeKop=(gemiddeldeTweeKop*(experiment)+tweeKop)/(experiment+1);
				   if (geenKop>maximumGeenKop)
					   maximumGeenKop=geenKop;
				   if (geenKop<minimumGeenKop)
					   minimumGeenKop=geenKop;
				   if (eenKop>maximumEenKop)
					   maximumEenKop=eenKop;
				   if (eenKop<minimumEenKop)
					   minimumEenKop=eenKop;
				   if (tweeKop>maximumTweeKop)
					   maximumTweeKop=tweeKop;
				   if (tweeKop<minimumTweeKop)
					   minimumTweeKop=tweeKop;
				   if (experiment==0) {
					   maximumGeenKop=geenKop;
					   minimumGeenKop=geenKop;
					   maximumEenKop=eenKop;
					   minimumEenKop=eenKop;
					   maximumTweeKop=tweeKop;
					   minimumTweeKop=tweeKop;
				   }
			   }
			   setResults();
			   
			   experiment++;
		   }

		grafiek.paint();
		frequentie.paint();
		}
	

}
