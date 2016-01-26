package fi.graphtoolgwt.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Image;

public class TekenComponentGWT extends LayoutPanel {

	// cursor
	public static int NOCUR = 0; 
	public static int DRAW = 1;
	public static int DELETE = 2;
	public static int DRAG = 3;
	
	private static int cDefault_cursorMode = DRAW;
	private int cursorMode = cDefault_cursorMode;	
	
	// verbindingen	
	public static int NONE = 0;
	public static int LINES = 1;	
	public static int CURVE = 2;	
	public static int CURVE_EXTRA = 3;
	private static int cDefault_connectMode = NONE;
	private int connectMode = cDefault_connectMode;
	
	private FlowPanel[] rechtHoekPanels;
	private ToggleButton drawButton, deleteButton, dragButton;//, noneButton;
	
	private ToggleButton puntenButton, lijnenButton, krommeButton, 
		extrapoleerButton;
	
	private ListBox grKeuze; 
	//private boolean grKeuzeOpen = false;
	private PushButton resetButton;

	private final GraphToolGWT interactiePanel;
	private GrafiekGWTVeld grafiekGWTVeld;
	
	private boolean frozen = false;
	private int aantalGrafieken = 3;
	//private boolean alleenPunten = false;
	
	private boolean lijnenZichtbaar = true;
	private boolean krommeZichtbaar = true;
	private boolean extrapoleerZichtbaar = true;
	
	int buttonSize = 20;
	int buttonOffset = 5;
	
	GraphToolGWTClientBundle graphToolGWTClientBundle; 
	static GraphToolCssResource graphToolCss;
	ImageResource drawButtonUpResource, drawButtonDownResource, deleteButtonUpResource, deleteButtonDownResource, dragButtonUpResource, 
		dragButtonDownResource, 
		puntenButtonUpResource, puntenButtonDownResource, lijnenButtonUpResource, lijnenButtonDownResource, krommeButtonUpResource,
		krommeButtonDownResource, extrapoleerButtonUpResource, extrapoleerButtonDownResource, resetButtonResource;
	Image drawButtonUpImage, drawButtonDownImage, deleteButtonUpImage, deleteButtonDownImage, dragButtonUpImage, 
		dragButtonDownImage, 
		puntenButtonUpImage, puntenButtonDownImage, lijnenButtonUpImage, lijnenButtonDownImage, krommeButtonUpImage,
		krommeButtonDownImage, extrapoleerButtonUpImage, extrapoleerButtonDownImage, resetButtonImage;
	
	boolean touchStart = false;
	
	public TekenComponentGWT(final GraphToolGWT interactiePanel, int breedte)
	{	this.interactiePanel = interactiePanel;
		getImages();
		
		int currentX = 5;
		int currentY = 2;
		
		for(int i = 0; i < 10; i++)
		{	FlowPanel panel = new FlowPanel();
			panel.getElement().getStyle().setBackgroundColor(CssColor.make(200 + 5*i, 200 + 5*i, 200 + 5*i).toString());
			this.add(panel);
			//System.out.println("getOffsetWidth: " + getOffsetWidth());
			this.setWidgetLeftWidth(panel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
			this.setWidgetTopHeight(panel, 24 - (i + 1)*24/10, Style.Unit.PX, 24/10 + 1, Style.Unit.PX);
		}	
		
		rechtHoekPanels = new FlowPanel[4];
		
		int knopBreedte = buttonSize + buttonOffset;
		int aantalLijnKnoppen = 1;
		if(lijnenZichtbaar)
			aantalLijnKnoppen++;
		if(krommeZichtbaar)
			aantalLijnKnoppen++;
		if(extrapoleerZichtbaar)
			aantalLijnKnoppen++;
		
		rechtHoekPanels[0] = addRechthoekPanel(-1, 3*knopBreedte + 2);
		rechtHoekPanels[1] = addRechthoekPanel(3*knopBreedte + 8, aantalLijnKnoppen * knopBreedte + 2);
		rechtHoekPanels[2] = addRechthoekPanel((3 + aantalLijnKnoppen) * knopBreedte + 16, breedte - ((3 + aantalLijnKnoppen) * knopBreedte + 15));
		rechtHoekPanels[3] = addRechthoekPanel(0, breedte);		
		
		drawButton = new ToggleButton(drawButtonUpImage, drawButtonDownImage);
		drawButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		this.add(drawButton);
		this.setWidgetLeftWidth(drawButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		this.setWidgetTopHeight(drawButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
		
		ToggleClickHandler toggleClickHandler = new ToggleClickHandler();
		drawButton.addClickHandler(toggleClickHandler);
	
		currentX += buttonSize + buttonOffset;
		
		deleteButton = new ToggleButton(deleteButtonUpImage, deleteButtonDownImage);
		deleteButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		this.add(deleteButton);
		this.setWidgetLeftWidth(deleteButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		this.setWidgetTopHeight(deleteButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
		
		deleteButton.addClickHandler(toggleClickHandler);
		currentX += buttonSize + buttonOffset;
		
		dragButton = new ToggleButton(dragButtonUpImage, dragButtonDownImage);
		dragButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		this.add(dragButton);
		this.setWidgetLeftWidth(dragButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		this.setWidgetTopHeight(dragButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
		
		dragButton.addClickHandler(toggleClickHandler);
		currentX += buttonSize + 3 * buttonOffset;
		
		puntenButton = new ToggleButton(puntenButtonUpImage, puntenButtonDownImage);
		puntenButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		this.add(puntenButton);
		this.setWidgetLeftWidth(puntenButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		this.setWidgetTopHeight(puntenButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
		
		puntenButton.addClickHandler(toggleClickHandler);
		currentX += buttonSize + buttonOffset;
		
		lijnenButton = new ToggleButton(lijnenButtonUpImage, lijnenButtonDownImage);
		lijnenButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		this.add(lijnenButton);
		this.setWidgetLeftWidth(lijnenButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		this.setWidgetTopHeight(lijnenButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
		
		lijnenButton.addClickHandler(toggleClickHandler);
		currentX += buttonSize + buttonOffset;
		
		krommeButton = new ToggleButton(krommeButtonUpImage, krommeButtonDownImage);
		krommeButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		this.add(krommeButton);
		this.setWidgetLeftWidth(krommeButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		this.setWidgetTopHeight(krommeButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
		
		krommeButton.addClickHandler(toggleClickHandler);
		currentX += buttonSize + buttonOffset;
		
		extrapoleerButton = new ToggleButton(extrapoleerButtonUpImage, extrapoleerButtonDownImage);
		extrapoleerButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		this.add(extrapoleerButton);
		this.setWidgetLeftWidth(extrapoleerButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		this.setWidgetTopHeight(extrapoleerButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
		
		extrapoleerButton.addClickHandler(toggleClickHandler);
		currentX += buttonSize + 3 * buttonOffset;
		
		grKeuze = new ListBox();//{
		/*public void paintComponent(Context2d g)
		{	for(int i = 0; i < 10; i++)
			{	g.setFillStyle(CssColor.make(200+5*(7*i+9)/9,200+5*(7*i+9)/9,200+5*(7*i+9)/9));
				g.fillRect(0, buttonSize - (i+1)*buttonSize/10, 50, buttonSize/10+1);
			}
		
		
		/*
			g.setFillStyle(CssColor.)
			g.setColor(getBackground().darker());
			g.drawLine(0,0,getSize().width-1,0);
			g.drawLine(0,0,0,getSize().height-1);
			g.drawLine(0,getSize().height-1,getSize().width-1,getSize().height-1);
			g.setColor(getForeground());
			g.drawString(getSelectedItem().toString(), 2, getHeight() - 5);
			
		}
		};*/
		grKeuze.setVisibleItemCount(1);
		//grKeuze.setSize("50px", "20px");
		//grKeuze.setBackground(CssColor.make(210,210,210));
		this.add(grKeuze);
		this.setWidgetLeftWidth(grKeuze, currentX, Style.Unit.PX, 53, Style.Unit.PX);
		this.setWidgetTopBottom(grKeuze, 0, Style.Unit.PX, 0, Style.Unit.PX);
		grKeuze.addItem("Gr 1");
		grKeuze.addItem("Gr 2");
		grKeuze.addItem("Gr 3");
		
		//ListBox listBox = new ListBox();
		// Assuming a bunch of items were added to listBox....
		zetGrafiekKeuzeKleuren();
		//grKeuze.getElement().getStyle().setColor(interactiePanel.getFormuleColor(0).toString());
		//grKeuze.setRenderer(new GrKeuzeRenderer());
		//grKeuze.addClickHandler(new PushClickHandler());
		grKeuze.addChangeHandler(new ChangeHandler()
		{

			@Override
			public void onChange(ChangeEvent event) {
				
				int index = grKeuze.getSelectedIndex();
    			if(index >= 0)
    			{	//grKeuze.setForeground(grafiekComponent.getColor(index));
    				//grKeuze.getElement().getStyle().setColor(interactiePanel.getFormuleColor(index).toString());
    				interactiePanel.setActiveIndex(index + 1, false);
    				grafiekGWTVeld.paint();
    			}
				
			}
		});
		//grKeuze.getElement().getStyle().setOpacity(0.8);
	
		resetButton = new PushButton(resetButtonImage);
		//resetButton.addStyleName("pushbutton");
		resetButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		this.add(resetButton);
		this.setWidgetRightWidth(resetButton, buttonOffset, Style.Unit.PX, buttonSize, Style.Unit.PX);
		//this.setWidgetLeftWidth(resetButton, currentX, Style.Unit.PX, buttonSize, Style.Unit.PX);
		this.setWidgetTopHeight(resetButton, currentY, Style.Unit.PX, buttonSize, Style.Unit.PX);
		
		resetButton.addClickHandler(new PushClickHandler());
		
	}
	
	public void setSize(String width, String height)
	{
		super.setSize(width, height);
		this.setWidgetLeftRight(rechtHoekPanels[3], 0, Style.Unit.PX, 0, Style.Unit.PX);
		//rechtHoekPanels[4].setWid
	}
	
	public FlowPanel addRechthoekPanel(int x, int width)
	{
		FlowPanel panel = new FlowPanel();
		panel.getElement().getStyle().setBorderColor(CssColor.make(211, 211, 211).toString());
		panel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		panel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		this.add(panel);
		this.setWidgetLeftWidth(panel, x, Style.Unit.PX, width, Style.Unit.PX);
		this.setWidgetTopBottom(panel, -1, Style.Unit.PX, -1, Style.Unit.PX);
		return panel;
	}
	
	public void getImages() 
	{
		graphToolGWTClientBundle = GWT.create(GraphToolGWTClientBundle.class);
		graphToolCss = graphToolGWTClientBundle.getGraphToolGWTCSS();
		graphToolCss.ensureInjected();
		
		drawButtonUpResource = graphToolGWTClientBundle.drawButtonUpResource();
		drawButtonDownResource = graphToolGWTClientBundle.drawButtonDownResource();
		drawButtonUpImage = new Image(drawButtonUpResource.getSafeUri());
		drawButtonDownImage = new Image(drawButtonDownResource.getSafeUri());
		drawButtonUpImage.addStyleName(graphToolCss.upimage());
		drawButtonDownImage.addStyleName(graphToolCss.downimage());
		
		deleteButtonUpResource = graphToolGWTClientBundle.deleteButtonUpResource();
		deleteButtonDownResource = graphToolGWTClientBundle.deleteButtonDownResource();
		deleteButtonUpImage = new Image(deleteButtonUpResource.getSafeUri());
		deleteButtonDownImage = new Image(deleteButtonDownResource.getSafeUri());
		deleteButtonUpImage.addStyleName(graphToolCss.upimage());
		deleteButtonDownImage.addStyleName(graphToolCss.downimage());
		
		dragButtonUpResource = graphToolGWTClientBundle.dragButtonUpResource();
		dragButtonDownResource = graphToolGWTClientBundle.dragButtonDownResource();
		dragButtonUpImage = new Image(dragButtonUpResource.getSafeUri());
		dragButtonDownImage = new Image(dragButtonDownResource.getSafeUri());
		dragButtonUpImage.addStyleName(graphToolCss.upimage());
		dragButtonDownImage.addStyleName(graphToolCss.downimage());
		
		puntenButtonUpResource = graphToolGWTClientBundle.puntenButtonUpResource();
		puntenButtonDownResource = graphToolGWTClientBundle.puntenButtonDownResource();
		puntenButtonUpImage = new Image(puntenButtonUpResource.getSafeUri());
		puntenButtonDownImage = new Image(puntenButtonDownResource.getSafeUri());
		puntenButtonUpImage.addStyleName(graphToolCss.upimage());
		puntenButtonDownImage.addStyleName(graphToolCss.downimage());
		
		lijnenButtonUpResource = graphToolGWTClientBundle.lijnenButtonUpResource();
		lijnenButtonDownResource = graphToolGWTClientBundle.lijnenButtonDownResource();
		lijnenButtonUpImage = new Image(lijnenButtonUpResource.getSafeUri());
		lijnenButtonDownImage = new Image(lijnenButtonDownResource.getSafeUri());
		lijnenButtonUpImage.addStyleName(graphToolCss.upimage());
		lijnenButtonDownImage.addStyleName(graphToolCss.downimage());
		
		krommeButtonUpResource = graphToolGWTClientBundle.krommeButtonUpResource();
		krommeButtonDownResource = graphToolGWTClientBundle.krommeButtonDownResource();
		krommeButtonUpImage = new Image(krommeButtonUpResource.getSafeUri());
		krommeButtonDownImage = new Image(krommeButtonDownResource.getSafeUri());
		krommeButtonUpImage.addStyleName(graphToolCss.upimage());
		krommeButtonDownImage.addStyleName(graphToolCss.downimage());
		
		extrapoleerButtonUpResource = graphToolGWTClientBundle.extrapoleerButtonUpResource();
		extrapoleerButtonDownResource = graphToolGWTClientBundle.extrapoleerButtonDownResource();
		extrapoleerButtonUpImage = new Image(extrapoleerButtonUpResource.getSafeUri());
		extrapoleerButtonDownImage = new Image(extrapoleerButtonDownResource.getSafeUri());
		extrapoleerButtonUpImage.addStyleName(graphToolCss.upimage());
		extrapoleerButtonDownImage.addStyleName(graphToolCss.downimage());
		
		resetButtonResource = graphToolGWTClientBundle.resetButtonResource();
		resetButtonImage = new Image(resetButtonResource.getSafeUri());
		resetButtonImage.addStyleName(graphToolCss.pushimage());
	}
	
	
	/*
	public ImageIcon maakImageIcon(String s)
	{
		URL imageURL = GraphTool.class.getResource(s);
		ImageIcon imageIcon = new ImageIcon();
		if (imageURL != null) 
		{
		imageIcon = new ImageIcon(imageURL);
		}
		else
		{
			System.out.println("Error reading " + s);
		}
		return imageIcon;
	}
	*/
	
	
	public void zetAantalGrafieken(int i)
	{	aantalGrafieken = i;
		if(i == 1)
		{	grKeuze.setVisible(false);
			return;
		}
		else
			grKeuze.setVisible(true);
		grKeuze.clear();
		grKeuze.addItem("Gr 1");
		grKeuze.addItem("Gr 2");
		if(i == 3)
			grKeuze.addItem("Gr 3");
		zetGrafiekKeuzeKleuren();
	}
	
	public void zetGrafiekKeuzeKleuren()
	{
		SelectElement selectElement = SelectElement.as(grKeuze.getElement());
		NodeList<OptionElement> options = selectElement.getOptions();

		for (int i = 0; i < options.getLength(); i++) {
		     options.getItem(i).getStyle().setColor(interactiePanel.getFormuleColor(i).toString());
		}
	}
	
	
	public void zetLijnenKnoppen(boolean lijnen, boolean kromme, boolean extrapoleer)
	{	lijnenZichtbaar = lijnen;
		krommeZichtbaar = kromme;
		extrapoleerZichtbaar = extrapoleer;
		
		int knopBreedte = buttonSize + buttonOffset;
		int aantalLijnKnoppen = 1;
		if(lijnenZichtbaar)
			aantalLijnKnoppen++;
		if(krommeZichtbaar)
			aantalLijnKnoppen++;
		if(extrapoleerZichtbaar)
			aantalLijnKnoppen++;
		
		this.setWidgetLeftWidth(rechtHoekPanels[0], -1, Style.Unit.PX, 3*knopBreedte + buttonOffset, Style.Unit.PX);
		this.setWidgetLeftWidth(rechtHoekPanels[1], 3*knopBreedte + 2 * buttonOffset, Style.Unit.PX, aantalLijnKnoppen * knopBreedte + buttonOffset, Style.Unit.PX);
		this.setWidgetLeftRight(rechtHoekPanels[2], (3 + aantalLijnKnoppen) * knopBreedte + 4 * buttonOffset, Style.Unit.PX, -1, Style.Unit.PX);
		
		this.remove(puntenButton);
		this.remove(lijnenButton);
		this.remove(krommeButton);
		this.remove(extrapoleerButton);
		
		int xLocatie = 3 * buttonSize + 6 * buttonOffset;
		if(lijnen || kromme || extrapoleer)
		{
			this.add(puntenButton);
			this.setWidgetLeftWidth(puntenButton, xLocatie, Style.Unit.PX, buttonSize, Style.Unit.PX);
			this.setWidgetTopHeight(puntenButton, 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
			xLocatie += buttonSize + buttonOffset;
		}
		if(lijnen)
		{	this.add(lijnenButton);
			this.setWidgetLeftWidth(lijnenButton, xLocatie, Style.Unit.PX, buttonSize, Style.Unit.PX);
			this.setWidgetTopHeight(lijnenButton, 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
			xLocatie += buttonSize + buttonOffset;
		}
		if(kromme)
		{	this.add(krommeButton);
			this.setWidgetLeftWidth(krommeButton, xLocatie, Style.Unit.PX, buttonSize, Style.Unit.PX);
			this.setWidgetTopHeight(krommeButton, 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
			xLocatie += buttonSize + buttonOffset;
		}
		if(extrapoleer)
		{	this.add(extrapoleerButton);
			this.setWidgetLeftWidth(extrapoleerButton, xLocatie, Style.Unit.PX, buttonSize, Style.Unit.PX);
			this.setWidgetTopHeight(extrapoleerButton, 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
			xLocatie += buttonSize + buttonOffset;
		}
		//lijnenButton.setVisible(lijnen);
		//krommeButton.setVisible(kromme);
		//extrapoleerButton.setVisible(extrapoleer);
		
		/*
		int xLocatie = 4*buttonSize + 7 * buttonOffset;
		if(lijnen)
			xLocatie += buttonSize + buttonOffset;
		this.setWidgetLeftWidth(krommeButton, xLocatie, Style.Unit.PX, buttonSize, Style.Unit.PX);
		if(kromme)
			xLocatie += buttonSize + buttonOffset;
		this.setWidgetLeftWidth(extrapoleerButton, xLocatie, Style.Unit.PC, buttonSize, Style.Unit.PX);
		System.out.println("extrapoleerbutton left: " + xLocatie);
		if(extrapoleer)
			xLocatie += buttonSize + buttonOffset;
			*/
		this.setWidgetLeftWidth(grKeuze, xLocatie + 8, Style.Unit.PX, 53, Style.Unit.PX);
		
		
	}
	
	
	
	/*
	public void paintComponent(Graphics g)
	{	//if("GR".equals(WiskOpdr.deployVariant)) ;
		//if("MW".equals(WiskOpdr.deployVariant) || "GR".equals(WiskOpdr.deployVariant))super.paintComponent(g);
		//else					
		for(int i=0 ; i<10 ; i++)
		{	g.setColor(new Color(200+5*i,200+5*i,200+5*i));
			g.fillRect(0,getHeight() - (i+1)*getHeight()/10, getWidth(),getHeight()/10+1);
			
		}
		
		g.setColor(Color.lightGray);
		int knopBreedte = 22;
		int aantalLijnKnoppen = 2;
		if(krommeZichtbaar && extrapoleerZichtbaar)
			aantalLijnKnoppen = 4;
		else if(krommeZichtbaar || extrapoleerZichtbaar)
			aantalLijnKnoppen = 3;
		
		g.drawRect(0, 0, 3*knopBreedte + 2, getSize().height - 1);
		//74 = 0 + 68 + 6; 90 = 4*22 + 2, ofwel 4*20 + 5*2
		g.drawRect(3*knopBreedte + 8, 0, aantalLijnKnoppen * knopBreedte + 2, getSize().height - 1);
		g.drawRect((3 + aantalLijnKnoppen) * knopBreedte + 16, 0, getSize().width - ((3 + aantalLijnKnoppen) * knopBreedte + 17), getSize().height - 1);
		
		
		/*
		if(krommeZichtbaar && extrapoleerZichtbaar)
		{	g.drawRect(3*knopBreedte + 8, 0, ..., getSize().height - 1);
			g.drawRect(170, 0, getSize().width- 170 - 1, getSize().height-1);
		}
		else if (krommeZichtbaar || extrapoleerZichtbaar)
		{	g.drawRect(74, 0, 68, getSize().height - 1);
			g.drawRect(148, 0, getSize().width- 170 - 1, getSize().height-1);
		}
		else
		{	g.drawRect(74,  0,  46,  getSize().height - 1);
			g.drawRect(126, 0, getSize().width- 170 - 1, getSize().height-1);
		}
		//
		
		
		setBorder(BorderFactory.createLineBorder(Color.lightGray));
	}
*/
	
	/*
	public void setBounds(int x, int y, int b, int h)
	{	super.setBounds(x, y, b, h);
		setLocations(b, h);
	}
	*/

	/*
	public void setSize(int b, int h)
	{	super.setSize(b, h);
		setLocations(b, h);
	}
	*/
	
	/*
	private void setLocations(int b, int h)
	{	resetButton.setLocation(getSize().width - 22, 2);
	}
	*/
	
	public int getCursorMode()
	{	return cursorMode;
	}

	public void setConnectMode(int mode)
	{	if ((mode < 0) || (mode > CURVE_EXTRA))
			connectMode = NONE;
		else //nodig?
			connectMode = mode;
		puntenButton.setDown(false);
		if (connectMode == NONE)
		{	puntenButton.setDown(true);
			//puntenButton.zetActief(true);
		}
		else if (connectMode == LINES)
		{	lijnenButton.setDown(true); 
			//lijnenButton.zetActief(true);
		}
		else if (connectMode == CURVE)
		{	krommeButton.setDown(true);
			//krommeButton.zetActief(true);
		}
		else if (connectMode == CURVE_EXTRA)
		{	extrapoleerButton.setDown(true);
			//extrapoleerButton.zetActief(true);
		}
	}
	
	public void setCursorMode(int mode)
	{
		if(mode < 0 || mode > DRAG)
			cursorMode = NOCUR;
		else
			cursorMode = mode;
		
		if (grafiekGWTVeld.grafiekGWTCanvas.getStyleName().contains("cursor_drag")) {
			grafiekGWTVeld.grafiekGWTCanvas.removeStyleName("cursor_drag");
			dragButton.setDown(false);
		}
		if (grafiekGWTVeld.grafiekGWTCanvas.getStyleName().contains("cursor_gum")) {
			grafiekGWTVeld.grafiekGWTCanvas.removeStyleName("cursor_gum");
			deleteButton.setDown(false);
		}
		if (grafiekGWTVeld.grafiekGWTCanvas.getStyleName().contains("cursor_teken")) {
			grafiekGWTVeld.grafiekGWTCanvas.removeStyleName("cursor_teken");
			drawButton.setDown(false);
		}
		
		if(cursorMode == NONE) { // waarschijnlijk nu overbodig
			drawButton.setDown(false);
			deleteButton.setDown(false);
			dragButton.setDown(false);
		}
		else if(cursorMode == DRAW)
		{
			drawButton.setDown(true);
			grafiekGWTVeld.grafiekGWTCanvas.addStyleName("cursor_teken");
		}
		else if(cursorMode == DELETE)
		{
			deleteButton.setDown(true);
			grafiekGWTVeld.grafiekGWTCanvas.addStyleName("cursor_gum");

		}
		else if(cursorMode == DRAG)
		{
			dragButton.setDown(true);
			grafiekGWTVeld.grafiekGWTCanvas.addStyleName("cursor_drag");
		}
		
	}
	

	public int getConnectMode()
	{	return connectMode;
	}
	
	public void zetGrafiekComponent(GrafiekGWTVeld gc)
	{	grafiekGWTVeld = gc;
		//grKeuze.setForeground(grafiekComponent.getColor(0));
	}
	
	public HashMap<String,Object> getState()
	{
		int connectMode = NONE;
		connectMode = this.connectMode;
		
		HashMap<String,Object> h = new HashMap<String,Object>();
		h.put("connectMode", new Integer(connectMode));
		//h.put("connectMode", connectMode);
		
		return h;
	}
	
	public void setState(Map<String, Object> launchState) {	
		int connectMode = cDefault_connectMode;
		int cursorMode =  cDefault_cursorMode;
		
		if(launchState != null)
		{	if(launchState.containsKey("connectMode"))
				connectMode = ((Number)launchState.get("connectMode")).intValue();
			if(launchState.containsKey("cursorMode"))
				cursorMode = ((Number)launchState.get("cursorMode")).intValue();
		
			this.connectMode = connectMode;
			this.cursorMode = cursorMode;
		}
		setConnectMode(this.connectMode);
		setCursorMode(this.cursorMode);
	}
	
	/*
	public Hashtable getState()
	{	int connectMode = NONE;		
		connectMode = this.connectMode;
		
		Hashtable h = new Hashtable();
		h.put("connectMode", new Integer(connectMode));		
		return h;
	}
	*/
	
	/*
	public void setState(Hashtable h)
    {	
		int connectMode = NONE;				
		
		if(h.containsKey("connectMode")) 
			connectMode = ((Integer)h.get("connectMode")).intValue();
    	
		this.connectMode = connectMode;						
		setConnectMode(connectMode);	
		
	}
	*/
	
	public void zetSelectedIndexGrKeuze(int i)
	{	grKeuze.setSelectedIndex(i);
		grKeuze.getElement().getStyle().setColor(interactiePanel.getFormuleColor(i).toString());
	}
	
	void drawButtonsUp(ToggleButton tb)
   	{
   		if (drawButton != null && !drawButton.equals(tb))
   			drawButton.setDown(false);
   		if (deleteButton != null && !deleteButton.equals(tb))
   			deleteButton.setDown(false);
 		if (dragButton != null && !dragButton.equals(tb))
   			dragButton.setDown(false);
   	}
	
	void modeButtonsUp(ToggleButton tb)
	{
		if (puntenButton != null && !puntenButton.equals(tb))
   			puntenButton.setDown(false);
		if (lijnenButton != null && !lijnenButton.equals(tb))
   			lijnenButton.setDown(false);
		if (krommeButton != null && !krommeButton.equals(tb))
   			krommeButton.setDown(false);
		if (extrapoleerButton != null && !extrapoleerButton.equals(tb))
   			extrapoleerButton.setDown(false);
   		
	}
	
	
	
	class ToggleClickHandler implements ClickHandler
	//class ToggleMouseHandler implements MouseDownHandler//, MouseMoveHandler, MouseUpHandler
	{
		//public void onMouseDown(MouseDownEvent e)
   		public void onClick(ClickEvent e)
		{
//System.out.println("mouse down");
			
			// deze LIJKT niet nodig (mag wel)
			//e.preventDefault();
			
			// deze zorgt dat je niet scrollt in de DWOPlayer
			e.stopPropagation();
			
			if (e.getSource() == drawButton || e.getSource() == deleteButton || e.getSource() == dragButton) {
				if (grafiekGWTVeld.grafiekGWTCanvas.getStyleName().contains("cursor_drag"))
					grafiekGWTVeld.grafiekGWTCanvas.removeStyleName("cursor_drag");
				if (grafiekGWTVeld.grafiekGWTCanvas.getStyleName().contains("cursor_gum"))
					grafiekGWTVeld.grafiekGWTCanvas.removeStyleName("cursor_gum");
				if (grafiekGWTVeld.grafiekGWTCanvas.getStyleName().contains("cursor_teken"))
					grafiekGWTVeld.grafiekGWTCanvas.removeStyleName("cursor_teken");
			}
			
			if (e.getSource() == drawButton && drawButton.isDown())
    		{
    			drawButtonsUp(drawButton);
    			grafiekGWTVeld.grafiekGWTCanvas.addStyleName("cursor_teken");
    			cursorMode = DRAW;
    		}
    		else if (e.getSource() == deleteButton && deleteButton.isDown())
    		{
    			drawButtonsUp(deleteButton);
    			grafiekGWTVeld.grafiekGWTCanvas.addStyleName("cursor_gum");
    			cursorMode = DELETE;
    		}
    		else if (e.getSource() == dragButton && dragButton.isDown())
    		{	drawButtonsUp(dragButton);
				grafiekGWTVeld.grafiekGWTCanvas.addStyleName("cursor_drag");
    			cursorMode = DRAG;
    		}
    		else if(!drawButton.isDown() && !deleteButton.isDown() && !dragButton.isDown()) {
    			cursorMode = NOCUR;
    		}
    		
    		if (e.getSource() == puntenButton)
    		{
    			puntenButton.setDown(true);
    			modeButtonsUp(puntenButton);
    			connectMode = NONE;
    		}
    		else if (e.getSource() == lijnenButton)
    		{
    			lijnenButton.setDown(true);
    			modeButtonsUp(lijnenButton);
    			connectMode = LINES;
    		}
    		else if (e.getSource() == krommeButton)
    		{	krommeButton.setDown(true);
    			modeButtonsUp(krommeButton);
    			connectMode = CURVE;
    		}
    		else if (e.getSource() == extrapoleerButton)
    		{	extrapoleerButton.setDown(true);
    			modeButtonsUp(extrapoleerButton);
    			connectMode = CURVE_EXTRA;
    		}
    		
    		grafiekGWTVeld.paint();
    		interactiePanel.setChanged(false);	
    	}
		
	}
	
	class PushClickHandler implements ClickHandler
    {
    	//public void onMouseDown(MouseDownEvent e)
    	public void onClick(ClickEvent e)
    	{
    		
    		if (touchStart)
    			return;
    		
			//e.preventDefault();
			e.stopPropagation();
    		
    		if (e.getSource() == resetButton)
    		{
    			if (frozen)
    				return;
    				
    			interactiePanel.removePoints(interactiePanel.getActiveIndex());//, false);
    				
    		}
//    		else if(e.getSource() == grKeuze)
//    		{
//    			//grKeuzeOpen = !grKeuzeOpen;
//    			int index = grKeuze.getSelectedIndex();
//    			if(index >= 0)
//    			{	//grKeuze.setForeground(grafiekComponent.getColor(index));
//    				//grKeuze.getElement().getStyle().setColor(interactiePanel.getFormuleColor(index).toString());
//    				interactiePanel.setActiveIndex(index + 1, false);
//    			}
//    			//if(grKeuzeOpen)
//    			//	return;
//    		}
    		grafiekGWTVeld.paint();
    		/*
    		else if (e.getSource() == roteerLinksomButton)
    		{
    			grafiekGWTVeld.hideTekstVeld(true);
    			
    				if (grafiekGWTVeld.mouseMode == grafiekGWTVeld.selecteren)
    					grafiekGWTVeld.rotateObjectSelected(- grafiekGWTVeld.rotateStep);
    		}
    		else if (e.getSource() == roteerRechtsomButton)
    		{
    			grafiekGWTVeld.hideTekstVeld(true);
    			
    			if (grafiekGWTVeld.mouseMode == grafiekGWTVeld.selecteren)
    				grafiekGWTVeld.rotateObjectSelected(grafiekGWTVeld.rotateStep);
    		}
    		else if (e.getSource() == vergrootButton)
    		{
    			grafiekGWTVeld.hideTekstVeld(true);
    			
    			if (grafiekGWTVeld.mouseMode == grafiekGWTVeld.selecteren)
    				grafiekGWTVeld.scaleObjectSelected(grafiekGWTVeld.scaleUpStep);
    		}
    		else if (e.getSource() == verkleinButton)
    		{
    			grafiekGWTVeld.hideTekstVeld(true);
    			
    			if (grafiekGWTVeld.mouseMode == grafiekGWTVeld.selecteren)
    				grafiekGWTVeld.scaleObjectSelected(grafiekGWTVeld.scaleDownStep);
    		}
    		else if (e.getSource() == kleurkeuzeButton)
    		{
    			grafiekGWTVeld.hideTekstVeld(true);
    			
    			if (colorPopup == null)
    			{
    				colorPopup = new ColorPopup(KladjeHWT.this);
    				//int showX = kleurkeuzeButton.getAbsoluteLeft() + toggleSize/2 - colorPopup.breedte/2;
    				int showX = kladjeHWTCanvas.getAbsoluteLeft() + breedte - colorPopup.breedte - 20;
    				int showY = hoogte - bottomHeight - colorPopup.hoogte - topOffset;
    				colorPopup.setPopupPosition(showX, showY);
    				colorPopup.show();
    			}
    			else
    			{
    				colorPopup.show();
    			}
    		}
    		*/
    		
    	}
    	
    }
	
	class PushTouchStartHandler implements TouchStartHandler
    {
    	public void onTouchStart(TouchStartEvent e)
    	{
			
    		touchStart = true;
    		
    		// DIT NIET toevoegen!!
    		//e.preventDefault();
			e.stopPropagation();
    		
    		
    		if (e.getSource() == resetButton)
    		{
    			if (frozen)
    				return;
    				
    				//Onderstaande moet wel terug!
    				interactiePanel.removePoints(interactiePanel.getActiveIndex());//, false);
    				grafiekGWTVeld.paint();
    		}
    		/*
    		else if (e.getSource() == roteerLinksomButton)
    		{
   				if (grafiekGWTVeld.mouseMode == grafiekGWTVeld.selecteren)
    					grafiekGWTVeld.rotateObjectSelected(- grafiekGWTVeld.rotateStep);
    		}
    		else if (e.getSource() == roteerRechtsomButton)
    		{
    			if (grafiekGWTVeld.mouseMode == grafiekGWTVeld.selecteren)
    				grafiekGWTVeld.rotateObjectSelected(grafiekGWTVeld.rotateStep);
    		}
    		else if (e.getSource() == vergrootButton)
    		{
    			if (grafiekGWTVeld.mouseMode == grafiekGWTVeld.selecteren)
    				grafiekGWTVeld.scaleObjectSelected(grafiekGWTVeld.scaleUpStep);
    		}
    		else if (e.getSource() == verkleinButton)
    		{
    			if (grafiekGWTVeld.mouseMode == grafiekGWTVeld.selecteren)
    				grafiekGWTVeld.scaleObjectSelected(grafiekGWTVeld.scaleDownStep);
    		}
    		*/
    		
    		
    	}
    }

    class PushTouchEndHandler implements TouchEndHandler
    {
    	public void onTouchEnd(TouchEndEvent e)
    	{
			
       		touchStart = true;    		
    		
    		// DIT NIET toevoegen!!
    		//e.preventDefault();
			e.stopPropagation();

/*			
    		long touchEventAt = stp.getTime();
			if (lastTouchEventAt > 0)
			{	long deltaTime = touchEventAt - lastTouchEventAt;
				lastTouchEventAt = touchEventAt;
				if (deltaTime < touchPause)
					return;
			}
*/			
    		if (e.getSource() == resetButton)
    		{
    			if (frozen)
    				return;
    				
    				interactiePanel.removePoints(interactiePanel.getActiveIndex());//, false);
    				grafiekGWTVeld.paint();
    		}
    		/*
    		else if (e.getSource() == roteerLinksomButton)
    		{
//    			if (!rotatedLeft)
//    			{	
    				if (grafiekGWTVeld.mouseMode == grafiekGWTVeld.selecteren)
    					grafiekGWTVeld.rotateObjectSelected(- grafiekGWTVeld.rotateStep);
//    				rotatedLeft = true;
//    			}
//    			else
//    			{
//    				rotatedLeft = false;
//    			}
    			
    		}
    		else if (e.getSource() == roteerRechtsomButton)
    		{
    			if (grafiekGWTVeld.mouseMode == grafiekGWTVeld.selecteren)
    				grafiekGWTVeld.rotateObjectSelected(grafiekGWTVeld.rotateStep);
    		}
    		else if (e.getSource() == vergrootButton)
    		{
    			if (grafiekGWTVeld.mouseMode == grafiekGWTVeld.selecteren)
    				grafiekGWTVeld.scaleObjectSelected(grafiekGWTVeld.scaleUpStep);
    		}
    		else if (e.getSource() == verkleinButton)
    		{
    			if (grafiekGWTVeld.mouseMode == grafiekGWTVeld.selecteren)
    				grafiekGWTVeld.scaleObjectSelected(grafiekGWTVeld.scaleDownStep);
    		}
    		*/
    		
    		
    	}
    }
	
	/*
	boolean cursorItemChanged = false;
	
	class CursorModeIL implements ItemListener
	{	public void itemStateChanged(ItemEvent e)
		{	
			if (frozen)
				return;
		
			cursorItemChanged = true;
			if (drawButton.isSelected())
			{	cursorMode = DRAW;
				deleteButton.zetActief(false);
				dragButton.zetActief(false);
			}
			else if (deleteButton.isSelected())
			{	cursorMode = DELETE;
				drawButton.zetActief(false);
				dragButton.zetActief(false);
			}
			else if (dragButton.isSelected())
			{	cursorMode = DRAG;
				drawButton.zetActief(false);
				deleteButton.zetActief(false);
			}		
			else 
			{	cursorMode = NOCUR;
				
			}
			grafiekGWTVeld.repaint();
		}
	}
	*/
	
	/*
	class DrawButtonsAL implements ActionListener
	{	public void actionPerformed(ActionEvent e)
		{	if (frozen)
				return;
			
			if (!cursorItemChanged)
			{	noneButton.setSelected(true);
				
			}
			
			cursorItemChanged = false;
			
		}
	}
	*/

	/*
	class ConnectModeIL implements ItemListener
	{	public void itemStateChanged(ItemEvent e)
		{	
			if (frozen)
				return;
			
			if (puntenButton.isSelected())
			{	connectMode = NONE;
				lijnenButton.zetActief(false);
				krommeButton.zetActief(false);
				extrapoleerButton.zetActief(false);
			}
			else if (lijnenButton.isSelected())
			{	connectMode = LINES;
				puntenButton.zetActief(false);
				krommeButton.zetActief(false);
				extrapoleerButton.zetActief(false);
			}
			else if (krommeButton.isSelected())
			{	connectMode = CURVE;
				puntenButton.zetActief(false);
				lijnenButton.zetActief(false);
				extrapoleerButton.zetActief(false);
			}			
			else if (extrapoleerButton.isSelected())
			{	connectMode = CURVE_EXTRA;
				puntenButton.zetActief(false);
				lijnenButton.zetActief(false);
				krommeButton.zetActief(false);
			}			
			grafiekComponent.repaint();
		}
	}
	*/
	
	/*
	class ConnectModeAL implements ActionListener
	{	public void actionPerformed(ActionEvent e)
		{	if (frozen)
				return;
			
			if(puntenButton.isSelected())
				puntenButton.zetActief(true);
			if(lijnenButton.isSelected())
				lijnenButton.zetActief(true);
			if(krommeButton.isSelected())
				krommeButton.zetActief(true);
			if(extrapoleerButton.isSelected())
				extrapoleerButton.zetActief(true);
				
		}
	}
	*/
	
	
	/*
	class NumGraphAL implements ActionListener
	{	public void actionPerformed(ActionEvent e)
		{	
			int index = grKeuze.getSelectedIndex();
			if(index >= 0)
			{	grKeuze.setForeground(grafiekComponent.getColor(index));
				grafiekComponent.setActiveIndex(index + 1, false);
			}

		}
	}
	*/
	
	
	
	/*
	class ResetAL implements ActionListener
	{	public void actionPerformed(ActionEvent e)
		{	if (frozen)
			return;
			
			if(!e.getActionCommand().equals("focus"))
			{	grafiekComponent.removePoints(grafiekComponent.getActiveIndex(), false);
				//grafiekComponent.tekenGrafiekButton.setText(GraphTool.rb.getString("tekenGrafiekButton"));
				grafiekComponent.leerlingGrafiek[grafiekComponent.getActiveIndex()-1] = false;
				grafiekComponent.repaint();
			}
			
		}
	}
	*/
	
	
	/*
	class GrKeuzeRenderer extends JLabel implements ListCellRenderer 
	{
     	public GrKeuzeRenderer()
     	{	setOpaque(true);
        }
     	public Component getListCellRendererComponent(
         					JList list,
         					Object value,
         					int index,
         					boolean isSelected,
         					boolean cellHasFocus)
     	{
         	setText(value.toString());
         	if (isSelected)
         		setBackground(Color.white);
         	else	
         		setBackground(new Color(210, 210, 210));
         	if (index >= 0)	
         		try{
         		setForeground(grafiekComponent.getColor(index));
         		}
         		catch(Exception e){}
         	return this;
     	}
 	}
 	*/
	
	
	/*
	//ActionProducer
	private ActionListener actionListener = null;
	
	public void addActionListener(ActionListener l) 
 	{	actionListener = AWTEventMulticaster.add(actionListener,l);
 	}
 	
 	public void removeActionListener(ActionListener l)
 	{	actionListener = AWTEventMulticaster.remove(actionListener, l);
 	}	
 	
 	public void produceAction(String command)
 	{	if (actionListener != null)
 		{	actionListener.actionPerformed( new ActionEvent(this, 0, command) );
 		}
 	}
 	//end ActionProducer
	*/
	}
	


