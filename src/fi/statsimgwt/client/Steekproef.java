package fi.statsimgwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

import fi.statsimgwt.client.BinomTrekking.Experiment;

import java.util.List;
import java.util.Random;

public class Steekproef extends FlowPanel implements ClickHandler, KeyUpHandler {
	TextBox muText;
	TextBox sigmaText;
	Label muLabel1;
	Label sigmaLabel1;
	Label sigmaLabel2;
	TextBox steekproefGrootteText;
	Button doeSteekproef;
	Button doeSteekproef100Keer;
	Button wisResultaten;
	ScrollPanel scrollPanel;
	ScrollPanel scrollPanel1;
	HTML html;
	HTML html2;
	
	int experiment=0;
	private Image gaussianImage;
	StatSimGWTClientBundle clientBundle = GWT.create(StatSimGWTClientBundle.class);
	double[] steekproefResultaat;
	
	StatSimGWT ssgwt;
	
	public static class Experiment {
		private final String experimentNumber;
	    private final String value;
	    
	    public Experiment (String experimentNumber, String value) {
	    	this.experimentNumber = experimentNumber;
	    	this.value = value;
	    }
	    
	    public String getExpNumber() {
	    	return experimentNumber;
	    }
	    public String getValue () {
	    	return value;
	    }
	}

	CellTable<Experiment> table;
	
	protected ListDataProvider<Experiment> dataProvider;

	public static class Experiment1 {
		private final String experimentNumber;
	    private final String mu;
	    private final String sigma;
	    
	    public Experiment1 (String experimentNumber, String mu, String sigma) {
	    	this.experimentNumber = experimentNumber;
	    	this.mu = mu;
	    	this.sigma = sigma;
	    }
	    
	    public String getExpNumber() {
	    	return experimentNumber;
	    }
	    public String getMu () {
	    	return mu;
	    }
	    public String getSigma () {
	    	return sigma;
	    }
	}

	CellTable<Experiment1> table1;
	
	protected ListDataProvider<Experiment1> dataProvider1;
		
	
	public Steekproef(StatSimGWT ssgwt, boolean steekproefLinkerTabel, boolean steekproefRechterTabel) {
		this.ssgwt=ssgwt;
		
		gaussianImage = new Image(clientBundle.gaussian());
		
		VerticalPanel panel0=new VerticalPanel();
		HorizontalPanel panel=new HorizontalPanel();
		
		FlowPanel panel2=new FlowPanel();	    
	    HorizontalPanel panel3=new HorizontalPanel();
	    panel3.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	    Label muLabel=new Label("\u03BC = ");
	    muText=new TextBox();
	    muText.setText("0");
	    muText.setVisibleLength(2);
	    muText.addKeyUpHandler(this);
	    panel3.add(muLabel);
	    panel3.add(muText);
	    
	    HorizontalPanel panel4=new HorizontalPanel();
	    panel4.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	    panel4.getElement().getStyle().setMarginTop(5, Unit.PX);
	    Label sigmaLabel=new Label("\u03C3 = ");
	    sigmaText=new TextBox();
	    sigmaText.setText("1");
	    sigmaText.setVisibleLength(2);
	    sigmaText.addKeyUpHandler(this);
	    panel4.add(sigmaLabel);
	    panel4.add(sigmaText);
	    
	    panel2.add(panel3);
	    panel2.add(panel4);
	    panel.add(panel2);
	    
	    VerticalPanel panel5=new VerticalPanel();
	    FlowPanel panel6=new FlowPanel();
	    LayoutPanel panel7=new LayoutPanel();
	    panel6.add(gaussianImage);
	    panel.add(panel5);
	    panel5.add(panel6);
	    panel5.add(panel7);
	    
	    muLabel1=new Label("0");
	    sigmaLabel1=new Label("-2");
	    sigmaLabel2=new Label("2");
	    
	    //muText.addActionListener(this);
		//muText.addFocusListener(this);
		
	    panel7.add(sigmaLabel1);
	    panel7.add(muLabel1);
	    panel7.add(sigmaLabel2);
	    
	    
	    panel7.setSize("297px", "20px");
    
	    panel7.setWidgetLeftRight(sigmaLabel1,41,Unit.PX,200,Unit.PX);
	    panel7.setWidgetTopBottom(sigmaLabel1,0,Unit.PX,0,Unit.PX);
	    panel7.setWidgetLeftRight(muLabel1,140,Unit.PX,120,Unit.PX);
	    panel7.setWidgetTopBottom(muLabel1,0,Unit.PX,0,Unit.PX);
	    panel7.setWidgetLeftRight(sigmaLabel2,250,Unit.PX,0,Unit.PX);
	    panel7.setWidgetTopBottom(sigmaLabel2,0,Unit.PX,0,Unit.PX);
	    
	    panel0.add(panel);
	    
	    HorizontalPanel panel8=new HorizontalPanel();
	    panel8.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	    panel0.add(panel8);
	    
	    Label steekproefGrootte = new Label("Steekproefgrootte ");
	    steekproefGrootte.getElement().getStyle().setMarginRight(5, Unit.PX);
	    panel8.add(steekproefGrootte);
	    steekproefGrootteText=new TextBox();
	    steekproefGrootteText.getElement().getStyle().setMarginRight(5, Unit.PX);
	    steekproefGrootteText.setText("10");
	    steekproefGrootteText.setVisibleLength(2);
	    panel8.add(steekproefGrootteText);
	    
	    doeSteekproef=new Button("Doe steekproef",this);
	    doeSteekproef.getElement().getStyle().setMarginRight(5, Unit.PX);
	    doeSteekproef100Keer=new Button("Doe steekproef 100 keer",this);
	    
	    panel8.add(doeSteekproef);
	    panel8.add(doeSteekproef100Keer);
	    
	    HorizontalPanel panel9=new HorizontalPanel();
	    panel0.add(panel9);
	    

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
	    //table.addColumn(expColumn, "no.");
	    table.addColumn(expColumn);
	    table.setColumnWidth(expColumn, 40.0, Unit.PX);
	    
	    // Add a text column to show the address.
	    TextColumn<Experiment> valueColumn = new TextColumn<Experiment>() {
	      @Override
	      public String getValue(Experiment object) {
	        return object.value;
	      }
	    };
	    //table.addColumn(valueColumn, "Waarde");
	    table.addColumn(valueColumn);
	    table.setColumnWidth(valueColumn, 120.0, Unit.PX);
	    
	    scrollPanel = new ScrollPanel(table);
	    scrollPanel.setSize("180px", "240px");
	    //scrollPanel.getElement().getStyle().setMarginTop(10, Unit.PX);
	    scrollPanel.getElement().getStyle().setMarginBottom(10, Unit.PX);

	    // Create a CellTable.
	    table1 = new CellTable<Experiment1>();
	    table1.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
	    table1.setPageSize(1000);
	    
	    dataProvider1 = new ListDataProvider<Experiment1>();
		// 	Add the table to the dataProvider.
		dataProvider1.addDataDisplay(table1);
		
	    
	    // Add a text column to show the name.
	    TextColumn<Experiment1> expColumn1 = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.experimentNumber;
	      }
	    };
	    //table1.addColumn(expColumn1, "no.");
	    table1.addColumn(expColumn1);
	    

	    // Add a text column to show the address.
	    TextColumn<Experiment1> muColumn = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.mu;
	      }
	    };
	    //table1.addColumn(muColumn, "\u03BC");
	    table1.addColumn(muColumn);
	
	    // Add a text column to show the address.
	    TextColumn<Experiment1> sigmaColumn = new TextColumn<Experiment1>() {
	      @Override
	      public String getValue(Experiment1 object) {
	        return object.sigma;
	      }
	    };
	    //table1.addColumn(sigmaColumn, "\u03C3");
	    table1.addColumn(sigmaColumn);
	    
	    scrollPanel1 = new ScrollPanel(table1);
	    scrollPanel1.setSize("230px", "240px");
	    //scrollPanel1.getElement().getStyle().setMarginTop(10, Unit.PX);
	    scrollPanel1.getElement().getStyle().setMarginBottom(10, Unit.PX);

	    VerticalPanel panel10=new VerticalPanel();
	    VerticalPanel panel11=new VerticalPanel();
	    
	    html = new HTML("<table width=145><tr><td><font face=arial size=2><b>no.</b></font></td><td><font face=arial size=2><b>Waarde</b></font></td></tr></table>");
	    html.getElement().getStyle().setMarginTop(10, Unit.PX);
	    
	    panel10.add(html);

	    html2 = new HTML("<table width=195><tr><td width=55><font face=arial size=2><b>no.</b></font></td><td width=75><font face=arial size=2><b>&mu;</b></font></td><td width=65><font face=arial size=2><b>&sigma;</b></font></td></tr></table>");
	    html2.getElement().getStyle().setMarginTop(10, Unit.PX);
	    
	    
	    panel10.add(scrollPanel);
	    
	    if (steekproefLinkerTabel==false) {
	    	html.setVisible(false);
	    	scrollPanel.setVisible(false);
	    }
	    
	    panel11.add(html2);
	    panel11.add(scrollPanel1);
	    
	    if (steekproefRechterTabel==false) {
	    	html2.setVisible(false);
	    	scrollPanel1.setVisible(false);
	    }
	    
	    //panel9.add(scrollPanel);
	    //panel9.add(scrollPanel1);
	    panel9.add(panel10);
	    panel9.add(panel11);
	    
	    wisResultaten=new Button("Wis resultaten",this);
	    wisResultaten.setEnabled(false);
	    panel0.add(wisResultaten);
	    
	    add(panel0);
	    
	    steekproefResultaat = new double[10000];
	}
	
	public void setGrootte(int breedte, int hoogte) {
		//if (breedte-550>0) {
		//	scrollPanel.setSize("230px", Integer.toString(breedte-550)+"px");
		//	scrollPanel1.setSize("230px", Integer.toString(breedte-550)+"px");
		//}
	}
	
	public void fireCBook() {
		
		
		List<Steekproef.Experiment> list = (List<Steekproef.Experiment>) dataProvider.getList();
		List<Steekproef.Experiment1> list1 = (List<Steekproef.Experiment1>) dataProvider1.getList();
		
		String string1="";
		String string2="";
		
		for (int i=0;i<list.size();i++) {
			string1=string1+list.get(i).getValue()+"\n";			
		}
		for (int i=0;i<list1.size();i++) {
			string2=string2+list1.get(i).getMu()+";"+list1.get(i).getSigma()+"\n";			
		}
		
		ssgwt.fireCBookSteekproef(string1, string2);
	}
	
	public void getSample() {
		List dataList=dataProvider.getList();
		dataList.clear();
		
		Random generator = new Random();
		
		double muResultaat=0;
		
		int steekproefGrootte=Integer.parseInt(steekproefGrootteText.getText());
		
		for (int i=0;i<steekproefGrootte;i++) {
			double r = generator.nextGaussian();
			r=r*Double.parseDouble(sigmaText.getText())+Double.parseDouble(muText.getText());
			
			steekproefResultaat[i]=r;
			muResultaat=muResultaat+r;

			r=((double)Math.round(r*100))/100;
			//table.setValueAt(i+1,i,0);
			//table.setValueAt(r,i,1);
			
			Experiment ADDEXP = new Experiment(Integer.toString(i+1), Double.toString(r));

			List dataList1=dataProvider.getList();
			if (dataList1.size()==i)
				dataList1.add(ADDEXP);
			else	
				dataList1.set(i, ADDEXP);

			
		}
		muResultaat=muResultaat/steekproefGrootte;
		
		double dummy=0;
		for (int i=0;i<steekproefGrootte;i++) {
			dummy=dummy+Math.pow(steekproefResultaat[i]-muResultaat, 2);
		}
		dummy=dummy/steekproefGrootte;
		double sigmaResultaat=0;
		
		sigmaResultaat=Math.sqrt(dummy);
		
		sigmaResultaat=((double)Math.round(sigmaResultaat*1000))/1000;
		muResultaat=((double)Math.round(muResultaat*1000))/1000;
		//table1.setValueAt(experiment+1,experiment,0);
		//table1.setValueAt(muResultaat,experiment,1);
		//table1.setValueAt(sigmaResultaat,experiment,2);

		Experiment1 ADDEXP1 = new Experiment1(Integer.toString(experiment+1), Double.toString(muResultaat),Double.toString(sigmaResultaat));

		List dataList2=dataProvider1.getList();
		if (dataList2.size()==experiment)
			dataList2.add(ADDEXP1);
		else
			dataList2.set(experiment, ADDEXP1);

		fireCBook();
		experiment++;
	}
	
	public void updateGraph() {
		muLabel1.setText(""+Double.parseDouble(muText.getText()));
		sigmaLabel1.setText(""+(Double.parseDouble(sigmaText.getText())*-2+Double.parseDouble(muText.getText())));
		sigmaLabel2.setText(""+(Double.parseDouble(sigmaText.getText())*2+Double.parseDouble(muText.getText())));
	}

	@Override
	public void onClick(ClickEvent event) {
		
		// TODO Auto-generated method stub
		if (event.getSource()==doeSteekproef) {
			doeSteekproef.setEnabled(false);
			doeSteekproef100Keer.setEnabled(false);
			muText.setEnabled(false);
			sigmaText.setEnabled(false);
			steekproefGrootteText.setEnabled(false);
			wisResultaten.setEnabled(true);
			getSample();
			doeSteekproef.setEnabled(true);
			doeSteekproef100Keer.setEnabled(true);
			
		}
		if (event.getSource()==doeSteekproef100Keer) {
			doeSteekproef.setEnabled(false);
			doeSteekproef100Keer.setEnabled(false);
			muText.setEnabled(false);
			sigmaText.setEnabled(false);
			steekproefGrootteText.setEnabled(false);
			wisResultaten.setEnabled(true);
			for (int i=0;i<100;i++) {
				getSample();
			}
			doeSteekproef.setEnabled(true);
			doeSteekproef100Keer.setEnabled(true);
			
		}
		if (event.getSource()==wisResultaten) {
			List dataList=dataProvider.getList();
			dataList.clear();
			List dataList1=dataProvider1.getList();
			dataList1.clear();
			experiment=0;
			wisResultaten.setEnabled(false);
			muText.setEnabled(true);
			sigmaText.setEnabled(true);
			steekproefGrootteText.setEnabled(true);
		}
		if (event.getSource()==muText) {
			updateGraph();
		}
		if (event.getSource()==sigmaText) {
			updateGraph();
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		// TODO Auto-generated method stub
		updateGraph();
	}

}
