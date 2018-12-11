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
	
	private FocusPanel wrap (IsWidget widget) {
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
	
	private String[] domeinStrings = new String[] {"$f" + Double.NEGATIVE_INFINITY + "@", "$f" + Double.POSITIVE_INFINITY + "@"};
	
	private boolean tabletAan;
	//private Tablet tablet;
   // private FormuleVakHouder tabletUser;
    private boolean tabletAdded;
    /**
     * Interactiepanel is nodig om het ingestelde domein door te geven.
     */
    private GraphToolGWT interactiePanel;
    /**
     * De index van de domeinbutton.
     */
    private int index;
    
    public DomeinButtonGWT(GraphToolGWT interactiePanel, int index)
    {	
		// zet tekst "D":
		super("D");

		this.interactiePanel = interactiePanel;
		this.index = index;
		
		frame = new DialogBox(false,false);
		//frame.setStyleName("domeinpanel");
		frameContents = new LayoutPanel();
		final FocusPanel wrap = wrap(frameContents);
		frame.add(wrap);
		frameContents.setSize("250px", "90px");

		String xMinTekst = "";
		String xMaxTekst = "";
		if (domeinStrings[0].equals("$f" + Double.NEGATIVE_INFINITY + "@"))
			xMinTekst = Double.toString(Double.NEGATIVE_INFINITY);
		else if (FormuleParser.geefExpressie(addFormulaCodes(domeinStrings[0])) != null)
			xMinTekst = "" + FormuleParser.geefExpressie(addFormulaCodes(domeinStrings[0])).geefWaarde();
		else
			xMinTekst = (String) domeinStrings[0].subSequence(2, domeinStrings[0].length() - 1);
		if (domeinStrings[1].equals("$f" + Double.POSITIVE_INFINITY + "@"))
			xMaxTekst = Double.toString(Double.POSITIVE_INFINITY);
		else if (FormuleParser.geefExpressie(addFormulaCodes(domeinStrings[1])) != null)
			xMaxTekst = "" + FormuleParser.geefExpressie(addFormulaCodes(domeinStrings[1])).geefWaarde();
		else
			xMaxTekst = (String) domeinStrings[1].subSequence(2, domeinStrings[1].length() - 1);

		huidigDomeinLabel = new Label(GraphToolGWT.rb.fc_huidigDomein() + " [ " + xMinTekst + ";" + xMaxTekst + "]");
		frameContents.add(huidigDomeinLabel);
		frameContents.setWidgetLeftRight(huidigDomeinLabel, 5, Style.Unit.PX, 5, Style.Unit.PX);
		frameContents.setWidgetTopHeight(huidigDomeinLabel, 5, Style.Unit.PX, 20, Style.Unit.PX);

		HorizontalPanel nieuwDomeinPanel = new HorizontalPanel();
		frameContents.add(nieuwDomeinPanel);
		frameContents.setWidgetLeftRight(nieuwDomeinPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
		frameContents.setWidgetTopHeight(nieuwDomeinPanel, 30, Style.Unit.PX, 30, Style.Unit.PX);

		Label nieuwDomein = new Label(GraphToolGWT.rb.fc_nieuwDomein());
		nieuwDomeinPanel.add(nieuwDomein);
		Label haakLinks = new Label("[");
		nieuwDomeinPanel.add(haakLinks);

		xMinVak = new DomeinVakGWT();
		xMinVak.setSize("50px", "25px");
		nieuwDomeinPanel.add(xMinVak);
		xMinVak.getEditor().requestFocus();

		Label komma = new Label(",");
		nieuwDomeinPanel.add(komma);
		xMaxVak = new DomeinVakGWT();
		xMaxVak.setSize("50px", "25px");
		nieuwDomeinPanel.add(xMaxVak);

		Label haakRechts = new Label("]");
		nieuwDomeinPanel.add(haakRechts);

		// lijkt vrij zinloos:
		xMinVak.zetMinBreedte(50);
		xMaxVak.zetMinBreedte(50);

		listener = new ClickHandler()
		{
			public void onClick(ClickEvent e)
			{
				makeDomeinString();
				frame.hide();
			}
		};

		okButton = new Button("OK", listener);
		frameContents.add(okButton);
//		frameContents.setWidgetLeftWidth(okButton, 5, Style.Unit.PX, 50, Style.Unit.PX);
		frameContents.setWidgetLeftWidth(okButton, 45, Style.Unit.PX, 60, Style.Unit.PX);
		frameContents.setWidgetTopHeight(okButton, 65, Style.Unit.PX, 20, Style.Unit.PX);

		listener = new ClickHandler()
		{
			public void onClick(ClickEvent e)
			{
//				xMinVak.maakEditorLeeg(); // waarom leegpoetsen bij cancel?
//				xMaxVak.maakEditorLeeg();
				frame.hide();
			}
		};
		cancelButton = new Button("Cancel", listener);
		frameContents.add(cancelButton);
//		frameContents.setWidgetRightWidth(cancelButton, 5, Style.Unit.PX, 50, Style.Unit.PX);
		frameContents.setWidgetRightWidth(cancelButton, 45, Style.Unit.PX, 60, Style.Unit.PX);
		frameContents.setWidgetTopHeight(cancelButton, 65, Style.Unit.PX, 20, Style.Unit.PX);

		frame.setWidget(frameContents);

		listener = new ClickHandler()
		{
			public void onClick(ClickEvent e)
			{
				frame.center();
				FocusOnTouch.requestFocus(wrap);
			}
		};

		this.addClickHandler(listener);
    }
    
	/**
	 * Surround the given string with the formule codes "$f" and "@".
	 * @param string
	 * @return
	 */
	private String addFormulaCodes(String string)
	{
		String startCode = "$f";
		String endCode = "@";
		String s = startCode + string + endCode;
		return s;
	}
    
    public void zetDomeinString(String[] domeinString)
	{
		if (domeinString == null)
			this.domeinStrings = null;
		else
		{
			this.domeinStrings = new String[2];
			this.domeinStrings[0] = domeinString[0];
			this.domeinStrings[1] = domeinString[1];
		}
		String xMinTekst = "";
		String xMaxTekst = "";
		
		// zet oud minimum
		if (domeinString[0].equals(addFormulaCodes(String.valueOf(Double.NEGATIVE_INFINITY)))
			|| domeinString[0].equals(String.valueOf(Double.NEGATIVE_INFINITY)))
			xMinTekst = Double.toString(Double.NEGATIVE_INFINITY);
		else if (FormuleParser.geefExpressie(addFormulaCodes(domeinString[0])) != null)
			xMinTekst = "" + FormuleParser.geefExpressie(addFormulaCodes(domeinString[0])).geefWaarde();
		else
			xMinTekst = (String) domeinString[0].subSequence(2, domeinString[0].length() - 1);
		
		// zet oud maximum
		if (domeinString[1].equals(addFormulaCodes(String.valueOf(Double.POSITIVE_INFINITY)))
			|| domeinString[1].equals(String.valueOf(Double.POSITIVE_INFINITY)))
			xMaxTekst = Double.toString(Double.POSITIVE_INFINITY);
		else if (FormuleParser.geefExpressie(addFormulaCodes(domeinString[1])) != null)
			xMaxTekst = "" + FormuleParser.geefExpressie(addFormulaCodes(domeinString[1])).geefWaarde();
		else
			xMaxTekst = (String) domeinString[1].subSequence(2, domeinString[1].length() - 1);

		// update huidig domein label
		huidigDomeinLabel.setText(GraphToolGWT.rb.fc_huidigDomein() + " [ " + xMinTekst + ";" + xMaxTekst + "]");
	}
    
	private void makeDomeinString()
	{
		String[] oudDomeinString = new String[2];
		if (domeinStrings != null)
		{
			oudDomeinString[0] = domeinStrings[0];
			oudDomeinString[1] = domeinStrings[1];
		}
		domeinStrings = new String[2];
		domeinStrings[0] = xMinVak.geefTekst();
		domeinStrings[1] = xMaxVak.geefTekst();
		
		// update grafiek
		double[] domein;
		if ("".equals(domeinStrings[0]))
			domeinStrings[0] = String.valueOf(Double.NEGATIVE_INFINITY);
		if ("".equals(domeinStrings[1]))
			domeinStrings[1] = String.valueOf(Double.POSITIVE_INFINITY);
		
		zetDomeinString(domeinStrings);
		
		domein = new double[2]; 
		if (String.valueOf(Double.NEGATIVE_INFINITY).equals(domeinStrings[0]))
			domein[0] = Double.NEGATIVE_INFINITY;
		else
			domein[0] = FormuleParser.geefExpressie(addFormulaCodes(domeinStrings[0])).geefWaarde();
		
		if (String.valueOf(Double.POSITIVE_INFINITY).equals(domeinStrings[1]))
			domein[1] = Double.POSITIVE_INFINITY;
		else
			domein[1] = FormuleParser.geefExpressie(addFormulaCodes(domeinStrings[1])).geefWaarde();

		interactiePanel.zetDomein(domein, index);
		interactiePanel.grafiekGWTVeld.paint();
		if (interactiePanel.formuleComponent.alsOpdracht)
			interactiePanel.kijkNa();
	}
    
    public String[] getDomeinString()
	{
		return domeinStrings;
	}
}
