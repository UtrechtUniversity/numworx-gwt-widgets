package fi.statsimgwt.client;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

//import fi.statsimgwt.client.Munten.Experiment;

//import fi.statsimgwt.client.Munten.Experiment;

public class Dobbelstenen extends FlowPanel implements ClickHandler{
	static final String upgradeMessage = 
			"Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	public Canvas kladjeHWTCanvas;
	public Context2d gIm;

	public RadioButton eenDobbelsteen;
	public RadioButton tweeDobbelstenen;
	public RadioButton drieDobbelstenen;
	public TextBox aantalWorpenText;
	public CheckBox toonSom;
	public Button voeruit;
	public Button wis;
	public ScrollPanel scrollPanel;
	public ScrollPanel scrollPanel2;
	public ScrollPanel scrollPanel3;
	public ScrollPanel scrollPanel4;
	public ScrollPanel scrollPanel5;
	public ScrollPanel scrollPanel6;
	public Timer elapsedTimer;
	int maxCount;
	int ogen[];
	int ogenSom[];
	Boolean stopCounting;
	double ogenGemiddeld[];
	DobbelstenenGrafiek dobbelstenenGrafiek;
	boolean dobbelstenenResultaten1;
	boolean dobbelstenenTabel1;
	
	CssColor lijnenKleur = CssColor.make(0, 0, 0);
	CssColor agKleur = CssColor.make(255, 255, 255);

	private static class Experiment {
		private final String experimentNumber;
	    private final String een;
	    private final String twee;
	    private final String drie;
	    private final String vier;
	    private final String vijf;
	    private final String zes;

	    public Experiment (String experimentNumber, String een,String twee, String drie, String vier, String vijf, String zes) {
	    	this.experimentNumber = experimentNumber;
	    	this.een = een;
	    	this.twee=twee;
	    	this.drie=drie;
	    	this.vier=vier;
	    	this.vijf=vijf;
	    	this.zes=zes;
	    }
	    	    
	}

	private static class Experiment1 {
		private final String experimentNumber;
	    private final String twee;
	    private final String drie;
	    private final String vier;
	    private final String vijf;
	    private final String zes;
	    private final String zeven;
	    private final String acht;
	    private final String negen;
	    private final String tien;
	    private final String elf;
	    private final String twaalf;

	    public Experiment1 (String experimentNumber,String twee, String drie, String vier, String vijf, String zes, String zeven, String acht, String negen, String tien, String elf, String twaalf) {
	    	this.experimentNumber = experimentNumber;
	    	this.twee=twee;
	    	this.drie=drie;
	    	this.vier=vier;
	    	this.vijf=vijf;
	    	this.zes=zes;
	    	this.zeven=zeven;
	    	this.acht=acht;
	    	this.negen=negen;
	    	this.tien=tien;
	    	this.elf=elf;
	    	this.twaalf=twaalf;
	    }
	    	    
	}

	private static class Experiment2 {
		private final String experimentNumber;
	    private final String drie;
	    private final String vier;
	    private final String vijf;
	    private final String zes;
	    private final String zeven;
	    private final String acht;
	    private final String negen;
	    private final String tien;
	    private final String elf;
	    private final String twaalf;
	    private final String dertien;
	    private final String veertien;
	    private final String vijftien;
	    private final String zestien;
	    private final String zeventien;
	    private final String achttien;

	    public Experiment2 (String experimentNumber, String drie, String vier, String vijf, String zes, String zeven, String acht, String negen, String tien, String elf, String twaalf, String dertien, String veertien, String vijftien, String zestien, String zeventien, String achttien) {
	    	this.experimentNumber = experimentNumber;
	    	this.drie=drie;
	    	this.vier=vier;
	    	this.vijf=vijf;
	    	this.zes=zes;
	    	this.zeven=zeven;
	    	this.acht=acht;
	    	this.negen=negen;
	    	this.tien=tien;
	    	this.elf=elf;
	    	this.twaalf=twaalf;
	    	this.dertien=dertien;
	    	this.veertien=veertien;
	    	this.vijftien=vijftien;
	    	this.zestien=zestien;
	    	this.zeventien=zeventien;
	    	this.achttien=achttien;
	    }
	    	    
	}

	private static class Experiment3 {
		private final String eyes;
	    private final String een;
	    private final String twee;
	    private final String drie;
	    private final String vier;
	    private final String vijf;
	    private final String zes;

	    public Experiment3 (String eyes, String een,String twee, String drie, String vier, String vijf, String zes) {
	    	this.eyes = eyes;
	    	this.een = een;
	    	this.twee=twee;
	    	this.drie=drie;
	    	this.vier=vier;
	    	this.vijf=vijf;
	    	this.zes=zes;
	    }
	    	    
	}

	private static class Experiment4 {
		private final String eyes;
	    private final String twee;
	    private final String drie;
	    private final String vier;
	    private final String vijf;
	    private final String zes;
	    private final String zeven;
	    private final String acht;
	    private final String negen;
	    private final String tien;
	    private final String elf;
	    private final String twaalf;

	    public Experiment4 (String eyes,String twee, String drie, String vier, String vijf, String zes, String zeven, String acht, String negen, String tien, String elf, String twaalf) {
	    	this.eyes = eyes;
	    	this.twee=twee;
	    	this.drie=drie;
	    	this.vier=vier;
	    	this.vijf=vijf;
	    	this.zes=zes;
	    	this.zeven=zeven;
	    	this.acht=acht;
	    	this.negen=negen;
	    	this.tien=tien;
	    	this.elf=elf;
	    	this.twaalf=twaalf;
	    }
	    	    
	}

	private static class Experiment5 {
		private final String eyes;
	    private final String drie;
	    private final String vier;
	    private final String vijf;
	    private final String zes;
	    private final String zeven;
	    private final String acht;
	    private final String negen;
	    private final String tien;
	    private final String elf;
	    private final String twaalf;
	    private final String dertien;
	    private final String veertien;
	    private final String vijftien;
	    private final String zestien;
	    private final String zeventien;
	    private final String achttien;

	    public Experiment5 (String eyes, String drie, String vier, String vijf, String zes, String zeven, String acht, String negen, String tien, String elf, String twaalf, String dertien, String veertien, String vijftien, String zestien, String zeventien, String achttien) {
	    	this.eyes = eyes;
	    	this.drie=drie;
	    	this.vier=vier;
	    	this.vijf=vijf;
	    	this.zes=zes;
	    	this.zeven=zeven;
	    	this.acht=acht;
	    	this.negen=negen;
	    	this.tien=tien;
	    	this.elf=elf;
	    	this.twaalf=twaalf;
	    	this.dertien=dertien;
	    	this.veertien=veertien;
	    	this.vijftien=vijftien;
	    	this.zestien=zestien;
	    	this.zeventien=zeventien;
	    	this.achttien=achttien;
	    }
	    	    
	}

	
	CellTable<Experiment> table;
	CellTable<Experiment1> table1;
	CellTable<Experiment2> table2;
	CellTable<Experiment3> table3;
	CellTable<Experiment4> table4;
	CellTable<Experiment5> table5;
	
	public Dobbelstenen(boolean dobbelstenenInstellingen, boolean dobbelstenenResultaten, boolean dobbelstenenGrafiek1, boolean dobbelstenenTabel) {
		dobbelstenenResultaten1=dobbelstenenResultaten;
		dobbelstenenTabel1=dobbelstenenTabel;
		
		kladjeHWTCanvas = Canvas.createIfSupported(); 

		kladjeHWTCanvas.setWidth("560px");
		kladjeHWTCanvas.setHeight("350px");
		kladjeHWTCanvas.setCoordinateSpaceWidth(560);
		kladjeHWTCanvas.setCoordinateSpaceHeight(350);

		if (kladjeHWTCanvas == null) 
		{
	      RootPanel.get().add(new Label(upgradeMessage));
	      return;
	    }
		
		gIm = kladjeHWTCanvas.getContext2d();		

	    LayoutPanel panel = new LayoutPanel();
	    panel.setSize("790px", "100px");
	    
	    FlowPanel panel2=new FlowPanel();
	    panel.add(panel2);
	    
	    VerticalPanel panel9=new VerticalPanel();
	    panel.add(panel9);
	    
	    panel.setWidgetLeftRight(panel9,360,Unit.PX,0,Unit.PX);
	    panel.setWidgetTopBottom(panel9,0,Unit.PX,0,Unit.PX);
	    
	    panel.setWidgetLeftRight(panel2,10,Unit.PX,560,Unit.PX);
	    panel.setWidgetTopBottom(panel2,0,Unit.PX,0,Unit.PX);
	    
	    Label instellingenLabel=new Label("Instellingen");
	    panel2.add(instellingenLabel);
	    
	    Label aantalDobbelstenenLabel=new Label("Aantal dobbelstenen");
	    panel2.add(aantalDobbelstenenLabel);
	    
	    VerticalPanel panel3=new VerticalPanel();
	    panel2.add(panel3);
	    
	    HorizontalPanel panel4=new HorizontalPanel();
	    panel3.add(panel4);
	    
	    eenDobbelsteen=new RadioButton("myGroup","Een");
	    eenDobbelsteen.setValue(true);
	    eenDobbelsteen.addClickHandler(this);
	    tweeDobbelstenen=new RadioButton("myGroup","Twee");
	    tweeDobbelstenen.addClickHandler(this);
	    drieDobbelstenen=new RadioButton("myGroup","Drie");
	    drieDobbelstenen.addClickHandler(this);
	    panel4.add(eenDobbelsteen);
	    panel4.add(tweeDobbelstenen);
	    panel4.add(drieDobbelstenen);
	    
	    HorizontalPanel panel5=new HorizontalPanel();
	    panel5.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	    panel3.add(panel5);
	    
	    Label aantalWorpenLabel=new Label("Aantal worpen");
	    aantalWorpenText=new TextBox();
	    aantalWorpenText.setVisibleLength(2);
	    aantalWorpenText.setText("30");
	    panel5.add(aantalWorpenLabel);
	    panel5.add(aantalWorpenText);
	
	    toonSom=new CheckBox("Toon som");
	    toonSom.addClickHandler(this);
	    panel3.add(toonSom);
	    
	 // Make a new button that does something when you click it.
	    voeruit = new Button("Voer uit",this);
	    wis = new Button("Wis",this);
	    	  
	    if (dobbelstenenInstellingen==false)
	    	panel2.setVisible(false);
	    
	    VerticalPanel panel6=new VerticalPanel();
	    panel6.add(voeruit);
	    panel6.add(wis);
	    panel.add(panel6);
	    panel.setWidgetLeftRight(panel6, 240, Unit.PX, 440, Unit.PX);     // Center panel
	    panel.setWidgetTopBottom(panel6, 5, Unit.PX, 5, Unit.PX);   
	    
	    //RootPanel.get().add(panel);
	    add(panel);

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
	    TextColumn<Experiment> eenColumn = new TextColumn<Experiment>() {
	      @Override
	      public String getValue(Experiment object) {
	        return object.een;
	      }
	    };
	    table.addColumn(eenColumn, "1");

	    // Add a text column to show the address.
	    TextColumn<Experiment> tweeColumn = new TextColumn<Experiment>() {
	      @Override
	      public String getValue(Experiment object) {
	        return object.twee;
	      }
	    };
	    table.addColumn(tweeColumn, "2");

	    // Add a text column to show the address.
	    TextColumn<Experiment> drieColumn = new TextColumn<Experiment>() {
	      @Override
	      public String getValue(Experiment object) {
	        return object.drie;
	      }
	    };
	    table.addColumn(drieColumn, "3");
	    // Add a text column to show the address.
	    TextColumn<Experiment> vierColumn = new TextColumn<Experiment>() {
	      @Override
	      public String getValue(Experiment object) {
	        return object.vier;
	      }
	    };
	    table.addColumn(vierColumn, "4");
	    // Add a text column to show the address.
	    TextColumn<Experiment> vijfColumn = new TextColumn<Experiment>() {
	      @Override
	      public String getValue(Experiment object) {
	        return object.vijf;
	      }
	    };
	    table.addColumn(vijfColumn, "5");
	    // Add a text column to show the address.
	    TextColumn<Experiment> zesColumn = new TextColumn<Experiment>() {
	      @Override
	      public String getValue(Experiment object) {
	        return object.zes;
	      }
	    };
	    table.addColumn(zesColumn, "6");


	    // Create a CellTable.
	    table1 = new CellTable<Experiment1>();
	    table1.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
	    table1.setPageSize(1000);
	    
	    // Add a text column to show the name.
	    TextColumn<Experiment1> expColumn1 = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.experimentNumber;
	      }
	    };
	    table1.addColumn(expColumn1, "Exp.");

	    // Add a text column to show the address.
	    TextColumn<Experiment1> tweeColumn1 = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.twee;
	      }
	    };
	    table1.addColumn(tweeColumn1, "2");

	    // Add a text column to show the address.
	    TextColumn<Experiment1> drieColumn1 = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.drie;
	      }
	    };
	    table1.addColumn(drieColumn1, "3");
	    // Add a text column to show the address.
	    TextColumn<Experiment1> vierColumn1 = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.vier;
	      }
	    };
	    table1.addColumn(vierColumn1, "4");
	    // Add a text column to show the address.
	    TextColumn<Experiment1> vijfColumn1 = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.vijf;
	      }
	    };
	    table1.addColumn(vijfColumn1, "5");
	    // Add a text column to show the address.
	    TextColumn<Experiment1> zesColumn1 = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.zes;
	      }
	    };
	    table1.addColumn(zesColumn1, "6");
	 // Add a text column to show the address.
	    TextColumn<Experiment1> zevenColumn1 = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.zeven;
	      }
	    };
	    table1.addColumn(zevenColumn1, "7");
	 // Add a text column to show the address.
	    TextColumn<Experiment1> achtColumn1 = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.acht;
	      }
	    };
	    table1.addColumn(achtColumn1, "8");
	 // Add a text column to show the address.
	    TextColumn<Experiment1> negenColumn1 = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.negen;
	      }
	    };
	    table1.addColumn(negenColumn1, "9");
	 // Add a text column to show the address.
	    TextColumn<Experiment1> tienColumn1 = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.tien;
	      }
	    };
	    table1.addColumn(tienColumn1, "10");
	 // Add a text column to show the address.
	    TextColumn<Experiment1> elfColumn1 = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.elf;
	      }
	    };
	    table1.addColumn(elfColumn1, "11");
	 // Add a text column to show the address.
	    TextColumn<Experiment1> twaalfColumn1 = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.twaalf;
	      }
	    };
	    table1.addColumn(twaalfColumn1, "12");
	    

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
	    TextColumn<Experiment2> drieColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.drie;
	      }
	    };
	    table2.addColumn(drieColumn2, "3");
	    // Add a text column to show the address.
	    TextColumn<Experiment2> vierColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.vier;
	      }
	    };
	    table2.addColumn(vierColumn2, "4");
	    // Add a text column to show the address.
	    TextColumn<Experiment2> vijfColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.vijf;
	      }
	    };
	    table2.addColumn(vijfColumn2, "5");
	    // Add a text column to show the address.
	    TextColumn<Experiment2> zesColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.zes;
	      }
	    };
	    table2.addColumn(zesColumn2, "6");
	 // Add a text column to show the address.
	    TextColumn<Experiment2> zevenColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.zeven;
	      }
	    };
	    table2.addColumn(zevenColumn2, "7");
	 // Add a text column to show the address.
	    TextColumn<Experiment2> achtColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.acht;
	      }
	    };
	    table2.addColumn(achtColumn2, "8");
	 // Add a text column to show the address.
	    TextColumn<Experiment2> negenColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.negen;
	      }
	    };
	    table2.addColumn(negenColumn2, "9");
	 // Add a text column to show the address.
	    TextColumn<Experiment2> tienColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.tien;
	      }
	    };
	    table2.addColumn(tienColumn2, "10");
	 // Add a text column to show the address.
	    TextColumn<Experiment2> elfColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.elf;
	      }
	    };
	    table2.addColumn(elfColumn2, "11");
	 // Add a text column to show the address.
	    TextColumn<Experiment2> twaalfColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.twaalf;
	      }
	    };
	    table2.addColumn(twaalfColumn2, "12");
		 // Add a text column to show the address.
	    TextColumn<Experiment2> dertienColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.dertien;
	      }
	    };
	    table2.addColumn(dertienColumn2, "13");
		 // Add a text column to show the address.
	    TextColumn<Experiment2> veertienColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.veertien;
	      }
	    };
	    table2.addColumn(veertienColumn2, "14");
		 // Add a text column to show the address.
	    TextColumn<Experiment2> vijftienColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.vijftien;
	      }
	    };
	    table2.addColumn(vijftienColumn2, "15");
		 // Add a text column to show the address.
	    TextColumn<Experiment2> zestienColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.zestien;
	      }
	    };
	    table2.addColumn(zestienColumn2, "16");
		 // Add a text column to show the address.
	    TextColumn<Experiment2> zeventienColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.zeventien;
	      }
	    };
	    table2.addColumn(zeventienColumn2, "17");
		 // Add a text column to show the address.
	    TextColumn<Experiment2> achttienColumn2 = new TextColumn<Experiment2>() {
	      @Override
	      public String getValue(Experiment2 object) {
	        return object.achttien;
	      }
	    };
	    table2.addColumn(achttienColumn2, "18");
	    	  
	    // Create a CellTable.
	    table3 = new CellTable<Experiment3>();
	    table3.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
	    table3.setPageSize(1000);
	    
	    // Add a text column to show the name.
	    TextColumn<Experiment3> eyesColumn3 = new TextColumn<Experiment3>() {
	      @Override
	      public String getValue(Experiment3 object) {
	        return object.eyes;
	      }
	    };
	    table3.addColumn(eyesColumn3, "Ogen");

	    // Add a text column to show the address.
	    TextColumn<Experiment3> eenColumn3 = new TextColumn<Experiment3>() {
	      @Override
	      public String getValue(Experiment3 object) {
	        return object.een;
	      }
	    };
	    table3.addColumn(eenColumn3, "1");

	    // Add a text column to show the address.
	    TextColumn<Experiment3> tweeColumn3 = new TextColumn<Experiment3>() {
	      @Override
	      public String getValue(Experiment3 object) {
	        return object.twee;
	      }
	    };
	    table3.addColumn(tweeColumn3, "2");

	    // Add a text column to show the address.
	    TextColumn<Experiment3> drieColumn3 = new TextColumn<Experiment3>() {
	      @Override
	      public String getValue(Experiment3 object) {
	        return object.drie;
	      }
	    };
	    table3.addColumn(drieColumn3, "2");
	    // Add a text column to show the address.
	    TextColumn<Experiment3> vierColumn3 = new TextColumn<Experiment3>() {
	      @Override
	      public String getValue(Experiment3 object) {
	        return object.vier;
	      }
	    };
	    table3.addColumn(vierColumn3, "4");
	    // Add a text column to show the address.
	    TextColumn<Experiment3> vijfColumn3 = new TextColumn<Experiment3>() {
	      @Override
	      public String getValue(Experiment3 object) {
	        return object.vijf;
	      }
	    };
	    table3.addColumn(vijfColumn3, "5");
	    // Add a text column to show the address.
	    TextColumn<Experiment3> zesColumn3 = new TextColumn<Experiment3>() {
	      @Override
	      public String getValue(Experiment3 object) {
	        return object.zes;
	      }
	    };
	    table3.addColumn(zesColumn3, "6");



	    // Create a CellTable.
	    table4 = new CellTable<Experiment4>();
	    table4.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
	    table4.setPageSize(1000);
	    
	    // Add a text column to show the name.
	    TextColumn<Experiment4> eyesColumn4 = new TextColumn<Experiment4>() {
	      @Override
	      public String getValue(Experiment4 object) {
	        return object.eyes;
	      }
	    };
	    table4.addColumn(eyesColumn4, "Ogen");

	    // Add a text column to show the address.
	    TextColumn<Experiment4> tweeColumn4 = new TextColumn<Experiment4>() {
	      @Override
	      public String getValue(Experiment4 object) {
	        return object.twee;
	      }
	    };
	    table4.addColumn(tweeColumn4, "2");

	    // Add a text column to show the address.
	    TextColumn<Experiment4> drieColumn4 = new TextColumn<Experiment4>() {
	      @Override
	      public String getValue(Experiment4 object) {
	        return object.drie;
	      }
	    };
	    table4.addColumn(drieColumn4, "2");
	    // Add a text column to show the address.
	    TextColumn<Experiment4> vierColumn4 = new TextColumn<Experiment4>() {
	      @Override
	      public String getValue(Experiment4 object) {
	        return object.vier;
	      }
	    };
	    table4.addColumn(vierColumn4, "4");
	    // Add a text column to show the address.
	    TextColumn<Experiment4> vijfColumn4 = new TextColumn<Experiment4>() {
	      @Override
	      public String getValue(Experiment4 object) {
	        return object.vijf;
	      }
	    };
	    table4.addColumn(vijfColumn4, "5");
	    // Add a text column to show the address.
	    TextColumn<Experiment4> zesColumn4 = new TextColumn<Experiment4>() {
	      @Override
	      public String getValue(Experiment4 object) {
	        return object.zes;
	      }
	    };
	    table4.addColumn(zesColumn4, "6");
	 // Add a text column to show the address.
	    TextColumn<Experiment4> zevenColumn4 = new TextColumn<Experiment4>() {
	      @Override
	      public String getValue(Experiment4 object) {
	        return object.zeven;
	      }
	    };
	    table4.addColumn(zevenColumn4, "7");
	 // Add a text column to show the address.
	    TextColumn<Experiment4> achtColumn4 = new TextColumn<Experiment4>() {
	      @Override
	      public String getValue(Experiment4 object) {
	        return object.acht;
	      }
	    };
	    table4.addColumn(achtColumn4, "8");
	 // Add a text column to show the address.
	    TextColumn<Experiment4> negenColumn4 = new TextColumn<Experiment4>() {
	      @Override
	      public String getValue(Experiment4 object) {
	        return object.negen;
	      }
	    };
	    table4.addColumn(negenColumn4, "9");
	 // Add a text column to show the address.
	    TextColumn<Experiment4> tienColumn4 = new TextColumn<Experiment4>() {
	      @Override
	      public String getValue(Experiment4 object) {
	        return object.tien;
	      }
	    };
	    table4.addColumn(tienColumn4, "10");
	 // Add a text column to show the address.
	    TextColumn<Experiment4> elfColumn4 = new TextColumn<Experiment4>() {
	      @Override
	      public String getValue(Experiment4 object) {
	        return object.elf;
	      }
	    };
	    table4.addColumn(elfColumn4, "11");
	 // Add a text column to show the address.
	    TextColumn<Experiment4> twaalfColumn4 = new TextColumn<Experiment4>() {
	      @Override
	      public String getValue(Experiment4 object) {
	        return object.twaalf;
	      }
	    };
	    table4.addColumn(twaalfColumn4, "12");

	    // Create a CellTable.
	    table5 = new CellTable<Experiment5>();
	    table5.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
	    table5.setPageSize(1000);
	    
	    // Add a text column to show the name.
	    TextColumn<Experiment5> eyesColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.eyes;
	      }
	    };
	    table5.addColumn(eyesColumn5, "Ogen");


	    // Add a text column to show the address.
	    TextColumn<Experiment5> drieColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.drie;
	      }
	    };
	    table5.addColumn(drieColumn5, "3");
	    // Add a text column to show the address.
	    TextColumn<Experiment5> vierColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.vier;
	      }
	    };
	    table5.addColumn(vierColumn5, "4");
	    // Add a text column to show the address.
	    TextColumn<Experiment5> vijfColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.vijf;
	      }
	    };
	    table5.addColumn(vijfColumn5, "5");
	    // Add a text column to show the address.
	    TextColumn<Experiment5> zesColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.zes;
	      }
	    };
	    table5.addColumn(zesColumn5, "6");
	 // Add a text column to show the address.
	    TextColumn<Experiment5> zevenColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.zeven;
	      }
	    };
	    table5.addColumn(zevenColumn5, "7");
	 // Add a text column to show the address.
	    TextColumn<Experiment5> achtColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.acht;
	      }
	    };
	    table5.addColumn(achtColumn5, "8");
	 // Add a text column to show the address.
	    TextColumn<Experiment5> negenColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.negen;
	      }
	    };
	    table5.addColumn(negenColumn5, "9");
	 // Add a text column to show the address.
	    TextColumn<Experiment5> tienColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.tien;
	      }
	    };
	    table5.addColumn(tienColumn5, "10");
	 // Add a text column to show the address.
	    TextColumn<Experiment5> elfColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.elf;
	      }
	    };
	    table5.addColumn(elfColumn5, "11");
	 // Add a text column to show the address.
	    TextColumn<Experiment5> twaalfColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.twaalf;
	      }
	    };
	    table5.addColumn(twaalfColumn5, "12");
		 // Add a text column to show the address.
	    TextColumn<Experiment5> dertienColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.dertien;
	      }
	    };
	    table5.addColumn(dertienColumn5, "13");
		 // Add a text column to show the address.
	    TextColumn<Experiment5> veertienColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.veertien;
	      }
	    };
	    table5.addColumn(veertienColumn5, "14");
		 // Add a text column to show the address.
	    TextColumn<Experiment5> vijftienColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.vijftien;
	      }
	    };
	    table5.addColumn(vijftienColumn5, "15");
		 // Add a text column to show the address.
	    TextColumn<Experiment5> zestienColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.zestien;
	      }
	    };
	    table5.addColumn(zestienColumn5, "16");
		 // Add a text column to show the address.
	    TextColumn<Experiment5> zeventienColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.zeventien;
	      }
	    };
	    table5.addColumn(zeventienColumn5, "17");
		 // Add a text column to show the address.
	    TextColumn<Experiment5> achttienColumn5 = new TextColumn<Experiment5>() {
	      @Override
	      public String getValue(Experiment5 object) {
	        return object.achttien;
	      }
	    };
	    table5.addColumn(achttienColumn5, "18");
	    	  
	    
	    
	    scrollPanel = new ScrollPanel(table);
	    scrollPanel.setSize("230px", "350px");
	    if (dobbelstenenTabel==false)
	    	scrollPanel.setVisible(false);
	    
	    scrollPanel2 = new ScrollPanel(table1);
	    scrollPanel2.setSize("230px", "350px");
	   
	    scrollPanel2.setVisible(false);
	   
	    scrollPanel3 = new ScrollPanel(table2);
	    scrollPanel3.setSize("230px", "350px");
	   
	    scrollPanel3.setVisible(false);
	   
	    scrollPanel4 = new ScrollPanel(table3);
	    scrollPanel4.setSize("340px", "100px");
		if (dobbelstenenResultaten==false)
			scrollPanel4.setVisible(false);
	    
	    scrollPanel5 = new ScrollPanel(table4);
	    scrollPanel5.setSize("340px", "100px");
	   
	    scrollPanel5.setVisible(false);
	   
	    scrollPanel6 = new ScrollPanel(table5);
	    scrollPanel6.setSize("340px", "100px");
	   
	    scrollPanel6.setVisible(false);
	    
	    
	    HorizontalPanel panel7=new HorizontalPanel();
	    // Add it to the root panel.
	    VerticalPanel panel8 = new VerticalPanel();
	    panel7.add(panel8);
	    
	    
	    panel8.add(scrollPanel);
	    panel8.add(scrollPanel2);
	    panel8.add(scrollPanel3);
	    
	    panel9.add(scrollPanel4);
	    panel9.add(scrollPanel5);
	    panel9.add(scrollPanel6);
	    	    
	    table.setRowCount(0, true);
	    table1.setRowCount(0, true);
	    table2.setRowCount(0, true);
	    table3.setRowCount(0, true);
	    table4.setRowCount(0, true);
	    table5.setRowCount(0, true);
	    
	    panel7.add(kladjeHWTCanvas);
	    if (dobbelstenenGrafiek1==false)
	    	kladjeHWTCanvas.setVisible(false);
	    //RootPanel.get().add(panel7);
	    add(panel7);

		 elapsedTimer = new Timer () {
			 public void run() {
			        //count=count+1;
			        //label.setText(Integer.toString(count));
			        //if (count==100) elapsedTimer.cancel();
			        doeStap();
			 }
		 };

		 ogen = new int[19];
		 ogenSom = new int[19];
		 ogenGemiddeld = new double[19];
		 maxCount=30;

		 dobbelstenenGrafiek = new DobbelstenenGrafiek(this);
		 dobbelstenenGrafiek.paint();
	    
	}
	
	public void onClick (ClickEvent event) {
		if (event.getSource()==voeruit) {
			voeruit.setEnabled(false);
			wis.setEnabled(true);
			aantalWorpenText.setEnabled(false);
			maxCount=Integer.parseInt(aantalWorpenText.getText());
			stopCounting=false;
			for (int i=0;i<19;i++) {
				ogen[i]=0;
			}

			dobbelsteenCount=0;
			elapsedTimer.scheduleRepeating(10);
		}
		if (event.getSource()==wis) {
			wis.setEnabled(false);
			aantalWorpenText.setEnabled(true);
			experiment=0;
			table.setRowCount(0, true);
			table1.setRowCount(0, true);
			table2.setRowCount(0, true);
			for (int i=0;i<19;i++) {
				ogen[i]=0;
			}
			for (int i=0;i<19;i++) {
				ogenGemiddeld[i]=0;
				ogenSom[i]=0;
			}
			experiment=0;
			dobbelsteenCount=0;
		}		
		if (event.getSource()==eenDobbelsteen) {
			if (dobbelstenenTabel1==true)
				scrollPanel.setVisible(true);
			scrollPanel2.setVisible(false);
			scrollPanel3.setVisible(false);
			if (dobbelstenenResultaten1==true)
				scrollPanel4.setVisible(true);
			scrollPanel5.setVisible(false);
			scrollPanel6.setVisible(false);
			experiment=0;
			table.setRowCount(0, true);
			table1.setRowCount(0, true);
			table2.setRowCount(0, true);
			for (int i=0;i<19;i++) {
				ogen[i]=0;
			}
			for (int i=0;i<19;i++) {
				ogenGemiddeld[i]=0;
				ogenSom[i]=0;
			}
			experiment=0;
			dobbelsteenCount=0;
		}
		if (event.getSource()==tweeDobbelstenen) {
			scrollPanel.setVisible(false);
			if (dobbelstenenTabel1==true)
				scrollPanel2.setVisible(true);
			scrollPanel3.setVisible(false);
			scrollPanel4.setVisible(false);
			if (dobbelstenenResultaten1==true)
				scrollPanel5.setVisible(true);
			scrollPanel6.setVisible(false);
			experiment=0;
			table.setRowCount(0, true);
			table1.setRowCount(0, true);
			table2.setRowCount(0, true);
			for (int i=0;i<19;i++) {
				ogen[i]=0;
			}
			for (int i=0;i<19;i++) {
				ogenGemiddeld[i]=0;
				ogenSom[i]=0;
			}
			experiment=0;
			dobbelsteenCount=0;
		}
		if (event.getSource()==drieDobbelstenen) {
			scrollPanel.setVisible(false);
			scrollPanel2.setVisible(false);
			if (dobbelstenenTabel1==true)
				scrollPanel3.setVisible(true);
			scrollPanel4.setVisible(false);
			scrollPanel5.setVisible(false);
			if (dobbelstenenResultaten1==true)
				scrollPanel6.setVisible(true);
			experiment=0;
			table.setRowCount(0, true);
			table1.setRowCount(0, true);
			table2.setRowCount(0, true);
			for (int i=0;i<19;i++) {
				ogen[i]=0;
			}
			for (int i=0;i<19;i++) {
				ogenGemiddeld[i]=0;
				ogenSom[i]=0;
			}
			experiment=0;
			dobbelsteenCount=0;
		}
		if (event.getSource()==toonSom) {
			dobbelstenenGrafiek.paint();
		}
	}
	
	int dobbelsteenCount;
	int experiment;
	public void doeStap() {
		
		double r=Math.random();
		double r2=Math.random();
		double r4=Math.random();
		
		int r1=(int)(r*6)+1;
		int r3=(int)(r2*6)+1;
		int r5=(int)(r4*6)+1;
        //Window.alert("How high?");
		if (eenDobbelsteen.getValue()==true) {
			ogen[r1]++;
			List<Experiment> ADDEXP = Arrays.asList(
					new Experiment(Integer.toString(experiment+1), Integer.toString(ogen[1]),Integer.toString(ogen[2]),Integer.toString(ogen[3]),Integer.toString(ogen[4]),Integer.toString(ogen[5]),Integer.toString(ogen[6])));

			table.setRowData(experiment,ADDEXP);

			//table.setValueAt(experiment+1, experiment, 0);
			//for (int i=0;i<6;i++) {
			//	table.setValueAt(ogen[i+1], experiment, i+1);
			//}
		}
		if (tweeDobbelstenen.getValue()==true) {
			ogen[r1+r3]++;
			List<Experiment1> ADDEXP1 = Arrays.asList(
					new Experiment1(Integer.toString(experiment+1),Integer.toString(ogen[2]),Integer.toString(ogen[3]),Integer.toString(ogen[4]),Integer.toString(ogen[5]),Integer.toString(ogen[6]),Integer.toString(ogen[7]),Integer.toString(ogen[8]),Integer.toString(ogen[9]),Integer.toString(ogen[10]),Integer.toString(ogen[11]),Integer.toString(ogen[12])));

			table1.setRowData(experiment,ADDEXP1);

			//table1.setValueAt(experiment+1, experiment, 0);
			//for (int i=0;i<11;i++) {
			//	table1.setValueAt(ogen[i+2], experiment, i+1);
			//}
		}
		if (drieDobbelstenen.getValue()==true) {
			ogen[r1+r3+r5]++;
			List<Experiment2> ADDEXP2 = Arrays.asList(
					new Experiment2(Integer.toString(experiment+1),Integer.toString(ogen[3]),Integer.toString(ogen[4]),Integer.toString(ogen[5]),Integer.toString(ogen[6]),Integer.toString(ogen[7]),Integer.toString(ogen[8]),Integer.toString(ogen[9]),Integer.toString(ogen[10]),Integer.toString(ogen[11]),Integer.toString(ogen[12]),Integer.toString(ogen[13]),Integer.toString(ogen[14]),Integer.toString(ogen[15]),Integer.toString(ogen[16]),Integer.toString(ogen[17]),Integer.toString(ogen[18])));

			table2.setRowData(experiment,ADDEXP2);

			//table2.setValueAt(experiment+1, experiment, 0);
			//for (int i=0;i<16;i++) {
			//	table2.setValueAt(ogen[i+3], experiment, i+1);
			//}
		}
		
		dobbelsteenCount++;
		//dobbelstenenGrafiek.repaint();
		dobbelstenenGrafiek.paint();
		//dobbelstenenSomGrafiek.repaint();
		
		if (dobbelsteenCount==maxCount) {
			stopCounting=true;
			elapsedTimer.cancel();
			for (int i=0;i<19;i++) {
				ogenGemiddeld[i]=(ogenGemiddeld[i]*experiment+ogen[i])/(experiment+1);
				ogenSom[i]=ogenSom[i]+ogen[i];
			}
			if (eenDobbelsteen.getValue()==true) {
				//for (int i=0;i<6;i++) {
					double dummy=Math.round(ogenGemiddeld[1]*100);
					double dummy1=Math.round(ogenGemiddeld[2]*100);
					double dummy2=Math.round(ogenGemiddeld[3]*100);
					double dummy3=Math.round(ogenGemiddeld[4]*100);
					double dummy4=Math.round(ogenGemiddeld[5]*100);
					double dummy5=Math.round(ogenGemiddeld[6]*100);
					
					List<Experiment3> ADDEXP3 = Arrays.asList(
							new Experiment3("Gemiddelde", Double.toString(dummy/100),Double.toString(dummy1/100),Double.toString(dummy2/100),Double.toString(dummy3/100),Double.toString(dummy4/100),Double.toString(dummy5/100)));

					table3.setRowData(0,ADDEXP3);

				//	table3.setValueAt(Double.toString(dummy/100), 0,i+1);
				//}
			}
			if (tweeDobbelstenen.getValue()==true) {
				//for (int i=0;i<11;i++) {
					double dummy=Math.round(ogenGemiddeld[2]*100);
					double dummy1=Math.round(ogenGemiddeld[3]*100);
					double dummy2=Math.round(ogenGemiddeld[4]*100);
					double dummy3=Math.round(ogenGemiddeld[5]*100);
					double dummy4=Math.round(ogenGemiddeld[6]*100);
					double dummy5=Math.round(ogenGemiddeld[7]*100);
					double dummy6=Math.round(ogenGemiddeld[8]*100);
					double dummy7=Math.round(ogenGemiddeld[9]*100);
					double dummy8=Math.round(ogenGemiddeld[10]*100);
					double dummy9=Math.round(ogenGemiddeld[11]*100);
					double dummy10=Math.round(ogenGemiddeld[12]*100);
					
					
					List<Experiment4> ADDEXP4 = Arrays.asList(
							new Experiment4("Gemiddelde",Double.toString(dummy/100),Double.toString(dummy1/100),Double.toString(dummy2/100),Double.toString(dummy3/100),Double.toString(dummy4/100),Double.toString(dummy5/100),Double.toString(dummy6/100),Double.toString(dummy7/100),Double.toString(dummy8/100),Double.toString(dummy9/100),Double.toString(dummy10/100)));

					table4.setRowData(0,ADDEXP4);

				//	table4.setValueAt(Double.toString(dummy/100), 0,i+1);
				//}
			}
			if (drieDobbelstenen.getValue()==true) {
				//for (int i=0;i<16;i++) {
					double dummy=Math.round(ogenGemiddeld[3]*100);
					double dummy1=Math.round(ogenGemiddeld[4]*100);
					double dummy2=Math.round(ogenGemiddeld[5]*100);
					double dummy3=Math.round(ogenGemiddeld[6]*100);
					double dummy4=Math.round(ogenGemiddeld[7]*100);
					double dummy5=Math.round(ogenGemiddeld[8]*100);
					double dummy6=Math.round(ogenGemiddeld[9]*100);
					double dummy7=Math.round(ogenGemiddeld[10]*100);
					double dummy8=Math.round(ogenGemiddeld[11]*100);
					double dummy9=Math.round(ogenGemiddeld[12]*100);
					double dummy10=Math.round(ogenGemiddeld[13]*100);
					double dummy11=Math.round(ogenGemiddeld[14]*100);
					double dummy12=Math.round(ogenGemiddeld[15]*100);
					double dummy13=Math.round(ogenGemiddeld[16]*100);
					double dummy14=Math.round(ogenGemiddeld[17]*100);
					double dummy15=Math.round(ogenGemiddeld[18]*100);
					
					List<Experiment5> ADDEXP5 = Arrays.asList(
							new Experiment5("Gemiddelde",Double.toString(dummy/100),Double.toString(dummy1/100),Double.toString(dummy2/100),Double.toString(dummy3/100),Double.toString(dummy4/100),Double.toString(dummy5/100),Double.toString(dummy6/100),Double.toString(dummy7/100),Double.toString(dummy8/100),Double.toString(dummy9/100),Double.toString(dummy10/100),Double.toString(dummy11/100),Double.toString(dummy12/100),Double.toString(dummy13/100),Double.toString(dummy14/100),Double.toString(dummy15/100)));

					table5.setRowData(0,ADDEXP5);

				//	table5.setValueAt(Double.toString(dummy/100), 0,i+1);
				//}
			}
			experiment++;
			voeruit.setEnabled(true);
			dobbelstenenGrafiek.paint();
		}
	}

}
