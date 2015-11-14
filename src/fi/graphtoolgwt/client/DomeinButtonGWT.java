package fi.graphtoolgwt.client;



import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;

import fi.wiskopdr.FormuleParser;


public class DomeinButtonGWT extends Button {
	
	private IsWidget wrap (IsWidget widget) {
		//FocusOnTouch.installKeyboard(interactiePanel.kb);
		FocusPanel focus = FocusOnTouch.wrap (widget.asWidget(), false);
		//focus.addKeyDownHandler(interactiePanel.keyHandler);
		//focus.addKeyPressHandler(interactiePanel.keyHandler);
		//focus.add(widget);
		//focus.addMouseUpHandler(new FocusOnTouch(focus));
		return focus;
	}
	
	
	private DialogBox frame;
	private LayoutPanel frameContents;
	private DomeinVakGWT xMinVak, xMaxVak;
	
	private Label huidigDomeinLabel;
	private Button okButton, cancelButton;
	
	private ClickHandler listener;
	
	private String[] domeinString = new String[] {"$f" + Double.NEGATIVE_INFINITY + "@", "$f" + Double.POSITIVE_INFINITY + "@"};
	
	private boolean tabletAan;
	//private Tablet tablet;
   // private FormuleVakHouder tabletUser;
    private boolean tabletAdded;
    
    
    public DomeinButtonGWT()
    {	//zet tekst "D": 
    	super("D");
    	
    	frame = new DialogBox(false);
    	frame.setStyleName("domeinpanel");
    	frameContents = new LayoutPanel();
    	final IsWidget wrap = wrap(frameContents);
		frame.add(wrap);
		//this.setWidgetLeftWidth(wrap, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		//this.setWidgetTopHeight(wrap, 0, Style.Unit.PX, hoogte, Style.Unit.PX); 
    	frameContents.setSize("250px", "80px");
    	
    	//domeinPanel = new JPanel();
		//bottomPanel = new JPanel();
        
        //Box boxv = Box.createVerticalBox();
        
        //Box boxh = Box.createHorizontalBox();
        //boxh.add(Box.createHorizontalStrut(10));
        String xMinTekst = "";
        String xMaxTekst = "";
        if(domeinString[0].equals("$f" + Double.NEGATIVE_INFINITY + "@"))
        	xMinTekst = Double.toString(Double.NEGATIVE_INFINITY);
        else if(FormuleParser.geefExpressie(domeinString[0]) != null)
        	xMinTekst = "" + FormuleParser.geefExpressie(domeinString[0]).geefWaarde();
        	//xMinTekst = df.format(FormuleParser.geefExpressie(domeinString[0]).geefWaarde());
        else
        	xMinTekst = (String) domeinString[0].subSequence(2, domeinString[0].length() - 1);
        if(domeinString[1].equals("$f" + Double.POSITIVE_INFINITY + "@"))
        	xMaxTekst = Double.toString(Double.POSITIVE_INFINITY);
        else if(FormuleParser.geefExpressie(domeinString[1]) != null)
        	xMaxTekst = "" + FormuleParser.geefExpressie(domeinString[1]).geefWaarde();
        	//xMaxTekst = df.format(FormuleParser.geefExpressie(domeinString[1]).geefWaarde());
        else
        	xMaxTekst = (String) domeinString[1].subSequence(2, domeinString[1].length() - 1);
       
        huidigDomeinLabel = new Label(GraphToolGWT.rb.getString("fc_huidigDomein") + " [ " + 
        //JLabel label = new JLabel(GraphTool.rb.getString("fc_huidigDomein") + " [" + 
        		xMinTekst + ";" + xMaxTekst + "]");
        //boxh.add(label);
        frameContents.add(huidigDomeinLabel);
        frameContents.setWidgetLeftRight(huidigDomeinLabel, 5, Style.Unit.PX, 5, Style.Unit.PX);
        frameContents.setWidgetTopHeight(huidigDomeinLabel, 5, Style.Unit.PX, 20, Style.Unit.PX);
        
        
        //boxv.add(boxh);
        //boxv.add(Box.createVerticalStrut(10));
        
        //Box boxh2 = Box.createHorizontalBox();
        HorizontalPanel nieuwDomeinPanel = new HorizontalPanel();
        frameContents.add(nieuwDomeinPanel);
        frameContents.setWidgetLeftRight(nieuwDomeinPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
        frameContents.setWidgetTopHeight(nieuwDomeinPanel, 30, Style.Unit.PX, 30, Style.Unit.PX);
        
        
        Label nieuwDomein = new Label(GraphToolGWT.rb.getString("fc_nieuwDomein"));
        nieuwDomeinPanel.add(nieuwDomein);
        //boxh2.add(nieuwDomein);
        Label haakLinks = new Label("[");
        nieuwDomeinPanel.add(haakLinks);
        //boxh2.add(haakLinks);
      
        xMinVak = new DomeinVakGWT();
		//xMinVak.addActionListener(this);
        xMinVak.setSize("50px", "25px");
        //xMinVak.setPreferredSize(new Dimension(50, 25));
        nieuwDomeinPanel.add(xMinVak);
       
        Label komma = new Label(",");
        nieuwDomeinPanel.add(komma);
        xMaxVak = new DomeinVakGWT();
        //xMaxVak.addActionListener(this);
        //xMaxVak.setPreferredSize(new Dimension(50, 25));
        xMaxVak.setSize("50px", "25px");
        nieuwDomeinPanel.add(xMaxVak);
      
        Label haakRechts = new Label("]");
        nieuwDomeinPanel.add(haakRechts);
        //boxv.add(boxh2);
        //domeinPanel.add(boxv);
        
       
        // lijkt vrij zinloos:
       
        xMinVak.zetMinBreedte(50);
        //xMinVak.zetMaat();
        xMaxVak.zetMinBreedte(50);
        //xMaxVak.zetMaat();
        
        
        listener = new ClickHandler()
        {
        	public void onClick(ClickEvent e)
        	{
        		makeDomeinString();
        		frame.hide();
        	}
        };
        
        okButton = new Button("Ok", listener);
        //okButton.addActionListener(this);
        frameContents.add(okButton);
        frameContents.setWidgetLeftWidth(okButton, 5, Style.Unit.PX, 50, Style.Unit.PX);
        frameContents.setWidgetTopHeight(okButton, 65, Style.Unit.PX, 20, Style.Unit.PX);
        
       // bottomPanel.add(okButton);
        
        listener = new ClickHandler()
        {
        	public void onClick(ClickEvent e)
        	{
        		xMinVak.maakEditorLeeg();
        		xMaxVak.maakEditorLeeg();
        		frame.hide();
        	}
        };
        cancelButton = new Button("Cancel", listener);
        //cancelButton.addActionListener(this);
        frameContents.add(cancelButton);
        frameContents.setWidgetRightWidth(cancelButton, 5, Style.Unit.PX, 50, Style.Unit.PX);
        frameContents.setWidgetTopHeight(cancelButton, 65, Style.Unit.PX, 20, Style.Unit.PX);
        //bottomPanel.add(cancelButton);
        
		//scrollPane = new JScrollPane(domeinPanel);
    	
    	frame.setWidget(frameContents);
    	
    	
    	
    	listener = new ClickHandler()
    	{
    		public void onClick(ClickEvent e)
    		{
    			System.out.println("D-knop handler");
    			frame.center();
    		}
    	};
    	this.addClickHandler(listener);
    	
    	
    }
    
    public void zetDomeinString(String[] domeinString)
	{
		if(domeinString == null)
			this.domeinString = null;
		else
		{	this.domeinString = new String[2];
			this.domeinString[0] = domeinString[0];
			this.domeinString[1] = domeinString[1];
		}
		String xMinTekst = "";
        String xMaxTekst = "";
        if(domeinString[0].equals("$f" + Double.NEGATIVE_INFINITY + "@"))
        	xMinTekst = Double.toString(Double.NEGATIVE_INFINITY);
        else if(FormuleParser.geefExpressie(domeinString[0]) != null)
        	xMinTekst = "" + FormuleParser.geefExpressie(domeinString[0]).geefWaarde();
        else
        	xMinTekst = (String) domeinString[0].subSequence(2, domeinString[0].length() - 1);
        if(domeinString[1].equals("$f" + Double.POSITIVE_INFINITY + "@"))
        	xMaxTekst = Double.toString(Double.POSITIVE_INFINITY);
        else if(FormuleParser.geefExpressie(domeinString[1]) != null)
        	xMaxTekst = "" + FormuleParser.geefExpressie(domeinString[1]).geefWaarde();
        else
        	xMaxTekst = (String) domeinString[1].subSequence(2, domeinString[1].length() - 1);
		huidigDomeinLabel.setText(GraphToolGWT.rb.getString("fc_huidigDomein") + " [ " + 
        		xMinTekst + ";" + xMaxTekst + "]");
		
	}
    
    private void makeDomeinString(){   
    	String[] oudDomeinString = new String[2];
		if(domeinString != null)
		{	oudDomeinString[0] = domeinString[0];
			oudDomeinString[1] = domeinString[1];
		}
		domeinString = new String[2];
    	domeinString[0] = xMinVak.geefTekst();
    	domeinString[1] = xMaxVak.geefTekst();
    	if(domeinString[0].equals("$f@"))
    		domeinString[0] = "$f" + Double.NEGATIVE_INFINITY + "@";
    	if(domeinString[1].equals("$f@"))
    		domeinString[1] = "$f" + Double.POSITIVE_INFINITY + "@";
    	
		//produceAction("maak Domein");
	}
    
    public String[] getDomeinString()
	{
		return domeinString;
	}
    
    

}
