package fi.graphtoolgwt.client;


//import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;












import nl.uu.fi.dwo.interaction.client.JSONUtilities;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;

import fi.wiskopdr.expressies.Expressie;




public class TabelComponentGWT extends LayoutPanel{

		private int hoogte = 60;
	
	// gebruik in combinatie met functieEditor			 
		private int aantalExpressies;
		private int maxAantalExpressies;
		private Expressie[] functies;
		private String[] functieNamen;
		private Expressie func;
		private int funcNum;

		// voor zoomen (alleen i.c.m. functieEditor)
		private boolean zooming;
		private double beginX;
		private double schaalFactorX;
		private int factorRijNummerX;

		private boolean isTekenTool; 
		//private GraphToolInteractiePanel interactiePanel;
		private final GraphToolGWT interactiePanel;
		private GrafiekGWTVeld grafiekGWTVeld;
		
		private boolean eenTabel;
		
		// alleen i.c.m. functieEditor
		private PushButton zoomInButton, zoomUitButton;
		private ListBox functieKeuze;
		private int functieKeuzeBreedte = 49;
		boolean functieKeuzeOpen = false;
		boolean updatingList = false;
		
		private ListBox tabelKeuze;
		private PushButton resetButton;
		boolean tabelKeuzeEnabled = true;
		boolean tabelKeuzeOpen = false;
		
		private PushButton pijlLinksButton, pijlRechtsButton;

		private String xAsNaam = "x";
		private String yAsNaam = "y";
		
		private int offSet;

		private int linkerBreedteTabel;
		private int linkerBreedteTool;	
		private int linkerBreedte;
		private int rechterBreedte;	
		 
		private final int basisLabelBreedte = 15;
		private int labelBreedte;
		private Label xAsNaamLabel, yAsNaamLabel;
		
		private int vakBreedte;	 
		private int vakHoogte;
		private LayoutPanel xVakkenPanel, yVakkenPanel; // Misschien is HorizontalPanel logischer.
				 
		 // het aantal vakken bij constructie
		private int aantalVakken;
		private int[] firstIndexVisible;
		
		private Vector xVakken;
		private Vector yVakken;
		private Vector linkerGrenzen;
		private Vector vakBreedtes;
		
		private boolean xVakEditable, yVakEditable;
		
		private boolean frozen;
		
		 
		private boolean randomAllowed;
		int buttonSize = 20;
		
		GraphToolGWTClientBundle graphToolGWTClientBundle; 
		static GraphToolCssResource graphToolCss;
		ImageResource pijlLinksButtonResource, pijlRechtsButtonResource, zoomInButtonResource, zoomUitButtonResource, resetButtonResource;
		Image pijlLinksButtonImage, pijlRechtsButtonImage, zoomInButtonImage, zoomUitButtonImage, resetButtonImage;
		
		
		public TabelComponentGWT(GraphToolGWT interactiePanel, int breedte)//, boolean docent)
		{
			graphToolGWTClientBundle = GWT.create(GraphToolGWTClientBundle.class);
			graphToolCss = graphToolGWTClientBundle.getGraphToolGWTCSS();
			graphToolCss.ensureInjected();
			
			this.interactiePanel = interactiePanel;
			grafiekGWTVeld = interactiePanel.grafiekGWTVeld;
			getImages();
			
			for(int i=0 ; i<20 ; i++)
			{	FlowPanel panel = new FlowPanel();
				if(i%2 == 0)
					panel.getElement().getStyle().setBackgroundColor(CssColor.make(200 + 5*(i/2), 200 + 5*(i/2), 200 + 5*(i/2)).toString());
				if(i%2 == 1)
					panel.getElement().getStyle().setBackgroundColor(CssColor.make(202 + 5*(i/2), 202 + 5*(i/2), 202 + 5*(i/2)).toString());
				this.add(panel);
				//System.out.println("getOffsetWidth: " + getOffsetWidth());
				this.setWidgetLeftWidth(panel, 0, Style.Unit.PX, breedte, Style.Unit.PX);
				this.setWidgetTopHeight(panel, hoogte - (i + 1)*hoogte/20, Style.Unit.PX, hoogte/20 + 1, Style.Unit.PX);
			}
			//this.docent = docent;
			FlowPanel rechthoekPanel = new FlowPanel();
			rechthoekPanel.getElement().getStyle().setBorderColor(CssColor.make(211, 211, 211).toString());
			rechthoekPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			rechthoekPanel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
			this.add(rechthoekPanel);
			this.setWidgetLeftRight(rechthoekPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
			this.setWidgetTopBottom(rechthoekPanel, 0, Style.Unit.PX, -1, Style.Unit.PX);
			
			maxAantalExpressies = 50;
			functies = new Expressie[maxAantalExpressies];
			functieNamen = new String[maxAantalExpressies];
			aantalExpressies = 0;
			func = null;
			funcNum = -1;
			
			beginX = -2;
			schaalFactorX = 1;
			factorRijNummerX = 99;
		
			zooming = true;
			isTekenTool = false;
			eenTabel = false;
		
			//staan voor testen op true:
			//xVakEditable = true; //false
			//yVakEditable = true; //false
			xVakEditable = false;
			yVakEditable = false;
			frozen = false;
			randomAllowed = true;
			
			/*
			font = new Font("SansSerrif",Font.PLAIN,12);
			fm = getFontMetrics(font);
			italicFont = new Font("SansSerrif",Font.ITALIC,12);
			itFm = getFontMetrics(italicFont);
			
			dfs = new DecimalFormatSymbols();
			dfs.setDecimalSeparator('.');
			df = new DecimalFormat("0.###", dfs);
			*/
			offSet = 1;
			labelBreedte = basisLabelBreedte;	
			vakBreedte = 33;
			vakHoogte = 30;
			linkerBreedteTabel = 25;//breedte met niets
			linkerBreedteTool = 55;//breedte met grafiekKeuze tekentool
			linkerBreedte = linkerBreedteTabel;
			rechterBreedte = 25;

			//int hoogte = 2 * vakHoogte + 3 * offSet;
			//setSize(breedte, hoogte);

			zoomInButton = new PushButton(zoomInButtonImage);
			//zoomInButton = new PushButton("+");
			zoomInButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
			this.add(zoomInButton);
			this.setWidgetLeftWidth(zoomInButton, 3 * offSet, Style.Unit.PX, buttonSize, Style.Unit.PX);
			this.setWidgetTopHeight(zoomInButton, vakHoogte / 2 - buttonSize/2, Style.Unit.PX, buttonSize, Style.Unit.PX);
			zoomInButton.addClickHandler(new PushClickHandler());
			
			zoomUitButton = new PushButton(zoomUitButtonImage);
			//zoomUitButton.addStyleName("pushbutton");
			zoomUitButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
			this.add(zoomUitButton);
			this.setWidgetRightWidth(zoomUitButton, 3 * offSet, Style.Unit.PX, buttonSize, Style.Unit.PX);
			this.setWidgetTopHeight(zoomUitButton, vakHoogte / 2 - buttonSize/2, Style.Unit.PX, buttonSize, Style.Unit.PX);
			zoomUitButton.addClickHandler(new PushClickHandler());
			
									
			functieKeuze = new ListBox();
			/*{
				public void paintComponent(Graphics g)
				{	int red = getBackground().getRed();
					int green = getBackground().getGreen();
					int blue = getBackground().getBlue();
					
					for(int i = 0; i < 10; i++)
					{	g.setColor(new Color(red + (7*i+9)*(245-red)/180, green + (7*i+9)*(245-green)/180, blue + (7*i+9)*(245-blue)/180));
						g.fillRect(0,getHeight()-(i+1)*getHeight()/10, getWidth(),getHeight()/10+1);
					}
					g.setColor(getBackground().darker());
					g.drawLine(0,0,getSize().width-1,0);
					g.drawLine(0,0,0,getSize().height-1);
					g.drawLine(0,getSize().height-1,getSize().width-1,getSize().height-1);
					g.setColor(getForeground());
					g.drawString(getSelectedItem().toString(), 2, getHeight() - 5);
				}
			};*/
			//functieKeuze.setFont(italicFont);
			//functieKeuze.setBounds(linkerBreedte, 2 * offSet + vakHoogte, 49, vakHoogte);
			//functieKeuze.setBackground(new Color(210, 210, 210));
			functieKeuze.setVisibleItemCount(1);
			functieKeuze.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
			functieKeuze.getElement().getStyle().setColor(interactiePanel.getFormuleColor(0).toString());
			//functieKeuze.setVisible(false);
			//this.add(functieKeuze);
			//this.setWidgetLeftWidth(functieKeuze, linkerBreedte, Style.Unit.PX, 49, Style.Unit.PX);
			//this.setWidgetTopHeight(functieKeuze, vakHoogte, Style.Unit.PX, vakHoogte, Style.Unit.PX);
			functieKeuze.addClickHandler(new PushClickHandler());
			//functieKeuze.setRenderer(new TabelKeuzeRenderer());
			
			
			pijlLinksButton = new PushButton(pijlLinksButtonImage);
			//pijlLinksButton.addStyleName("pushbutton");
			pijlLinksButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
			this.add(pijlLinksButton);
			this.setWidgetLeftWidth(pijlLinksButton, 3 * offSet, Style.Unit.PX, buttonSize, Style.Unit.PX);
			this.setWidgetTopHeight(pijlLinksButton, 3 * vakHoogte / 2 - buttonSize / 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
			pijlLinksButton.addClickHandler(new PushClickHandler());
			
			pijlRechtsButton = new PushButton(pijlRechtsButtonImage);
			//pijlRechtsButton.addStyleName("pushbutton");
			pijlRechtsButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
			this.add(pijlRechtsButton);
			this.setWidgetRightWidth(pijlRechtsButton, 3 * offSet, Style.Unit.PX, buttonSize, Style.Unit.PX);
			this.setWidgetTopHeight(pijlRechtsButton, 3 * vakHoogte / 2 - buttonSize / 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
			pijlRechtsButton.addClickHandler(new PushClickHandler());
			
			tabelKeuze = new ListBox();//{
			/*	public void paintComponent(Graphics g)
				{	int red = getBackground().getRed();
					int green = getBackground().getGreen();
					int blue = getBackground().getBlue();
					
					for(int i = 0; i < 10; i++)
					{	g.setColor(new Color((245+red)/2 + i*(245-red)/20, (245+green)/2 + i*(245-green)/20, (245+blue)/2 + i*(245-blue)/20));
						g.fillRect(0,getHeight()-(i+1)*getHeight()/10, getWidth(),getHeight()/10+1);
					}
					g.setColor(getBackground().darker());
					g.drawLine(0,0,getSize().width-1,0);
					g.drawLine(0,0,0,getSize().height-1);
					g.drawLine(0,getSize().height-1,getSize().width-1,getSize().height-1);
					g.setColor(getForeground());
					g.drawString(getSelectedItem().toString(), 2, getHeight() - 5);
				}
			};*/
			tabelKeuze.setVisibleItemCount(1);
			//this.add(tabelKeuze);
			//this.setWidgetLeftWidth(tabelKeuze, 3* offSet, Style.Unit.PX, 50, Style.Unit.PX);
			//this.setWidgetTopHeight(tabelKeuze, vakHoogte / 2 - buttonSize / 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
			//tabelKeuze.setVisible(false);
			//this.add(tabelKeuze);
			tabelKeuze.addItem("Gr 1");
			tabelKeuze.addItem("Gr 2");
			tabelKeuze.addItem("Gr 3");
			
			SelectElement selectElement = SelectElement.as(tabelKeuze.getElement());
			NodeList<OptionElement> options = selectElement.getOptions();

			for (int i = 0; i < options.getLength(); i++) {
			     options.getItem(i).getStyle().setColor(interactiePanel.getFormuleColor(i).toString());
			}
			tabelKeuze.getElement().getStyle().setColor(interactiePanel.getFormuleColor(0).toString());
			
			tabelKeuze.addClickHandler(new PushClickHandler());
			
			
			resetButton = new PushButton(resetButtonImage);
			//resetButton.addStyleName("pushbutton");
			resetButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
			//this.add(resetButton);
			//this.setWidgetRightWidth(resetButton, 3 * offSet, Style.Unit.PX, buttonSize, Style.Unit.PX);
			//this.setWidgetTopHeight(resetButton, vakHoogte/2 - buttonSize/2, Style.Unit.PX, buttonSize, Style.Unit.PX);
			//resetButton.setVisible(false);
			resetButton.addClickHandler(new PushClickHandler());

			
			xAsNaamLabel = new Label(xAsNaam);
			//xAsNaamLabel.setOpaque(false);
			xAsNaamLabel.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
			//xAsNaamLabel.getElement().getStyle().setBackgroundColor(CssColor.make(255, 200, 200).toString());
			this.add(xAsNaamLabel);
			this.setWidgetLeftWidth(xAsNaamLabel, linkerBreedte, Style.Unit.PX, labelBreedte, Style.Unit.PX);
			this.setWidgetTopHeight(xAsNaamLabel, vakHoogte / 2 - 6, Style.Unit.PX, 18, Style.Unit.PX);
			xAsNaamLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			
			
			
			yAsNaamLabel = new Label(yAsNaam);
			//yAsNaamLabel.setOpaque(false);		
			yAsNaamLabel.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
			//yAsNaamLabel.getElement().getStyle().setBackgroundColor(CssColor.make(255, 200, 200).toString());
			
			this.add(yAsNaamLabel);
			this.setWidgetLeftWidth(yAsNaamLabel, linkerBreedte, Style.Unit.PX, labelBreedte, Style.Unit.PX);
			this.setWidgetTopHeight(yAsNaamLabel, 3 * vakHoogte / 2 - 6, Style.Unit.PX, 18, Style.Unit.PX);
			yAsNaamLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			
			
			xVakkenPanel = new LayoutPanel();
			//xVakkenPanel.addStyleName(graphToolCss.backgroundred());
			//xVakkenPanel.setOpaque(false);
			//xVakkenPanel.setLayout(null);
			//nog grootte geven voor add?
			//xVakkenPanel.setSize("50px", vakHoogte + "px");
			add(xVakkenPanel);
			this.setWidgetLeftRight(xVakkenPanel, linkerBreedte + labelBreedte, Style.Unit.PX,
				rechterBreedte, Style.Unit.PX);
			this.setWidgetTopHeight(xVakkenPanel, 0, Style.Unit.PX, vakHoogte + 1, Style.Unit.PX);
			
			/*
			xVakkenPanel.setLocation(
				xAsNaamLabel.getLocation().x + xAsNaamLabel.getSize().width + offSet,
				offSet);
			xVakkenPanel.setSize(
				getSize().width - xVakkenPanel.getLocation().x - rechterBreedte, 
				vakHoogte+1);
			add(xVakkenPanel);
			*/
			
			yVakkenPanel = new LayoutPanel();
			//yVakkenPanel.setOpaque(false);
			//yVakkenPanel.setLayout(null);
			//nog grootte geven voor add?
			//yVakkenPanel.setSize("50px", vakHoogte + "px");
			add(yVakkenPanel);
			this.setWidgetLeftRight(yVakkenPanel, linkerBreedte + labelBreedte, Style.Unit.PX,
				rechterBreedte, Style.Unit.PX);
			this.setWidgetTopHeight(yVakkenPanel, vakHoogte, Style.Unit.PX, vakHoogte, Style.Unit.PX);
			
			/*
			yVakkenPanel.setLocation(
				yAsNaamLabel.getLocation().x + yAsNaamLabel.getSize().width + offSet,
				2 * offSet + vakHoogte);
			yVakkenPanel.setSize(
				getSize().width - yVakkenPanel.getLocation().x - rechterBreedte, 
				vakHoogte);
			add(yVakkenPanel);
			*/

			aantalVakken = 50;
			firstIndexVisible = new int[3];
			firstIndexVisible[0] = 0;
			firstIndexVisible[1] = 0;
			firstIndexVisible[2] = 0;
			
			xVakken = new Vector(); 
			yVakken = new Vector(); 
			linkerGrenzen = new Vector();
			vakBreedtes = new Vector();
			
			for (int vCnt = 0; vCnt < aantalVakken; vCnt++)
			{	TabelVakGWT xVak = new TabelVakGWT(this, vCnt, vakBreedte, vakHoogte+1, xVakEditable);
								// vCnt * vakBreedte, 0, vakBreedte, vakHoogte+1, xVakEditable);
				//xVak.zetFont(font);
				//xVak.addStyleName(graphToolCss.backgroundblue());
				TabelVakGWT yVak = new TabelVakGWT(this, vCnt, vakBreedte, vakHoogte, yVakEditable);
				xVakkenPanel.add(xVak);
				yVakkenPanel.add(yVak);
				//xVakkenPanel.setWidgetLeftWidth(xVak, vCnt * vakBreedte, Style.Unit.PX, vakBreedte, Style.Unit.PX);
				xVakkenPanel.setWidgetTopHeight(xVak, 0, Style.Unit.PX, vakHoogte + 1, Style.Unit.PX);
				yVakkenPanel.setWidgetTopHeight(yVak, 0, Style.Unit.PX, vakHoogte, Style.Unit.PX);
				xVakken.addElement(xVak);
				yVakken.addElement(yVak);
				linkerGrenzen.addElement(vCnt * vakBreedte);
				vakBreedtes.addElement(vakBreedte);
				zetWidgetLinksBreedte(xVak, yVak, (Integer) linkerGrenzen.lastElement(), (Integer) vakBreedtes.lastElement());
					
			}
			/*
			for (int vCnt = 0; vCnt < aantalVakken; vCnt++)
			{	TabelVakGWT yVak = 
					new TabelVakGWT(this, vCnt, vakBreedte, vakHoogte, yVakEditable);
								// vCnt * vakBreedte, 0, vakBreedte, vakHoogte, yVakEditable);
				//yVak.zetFont(font);
				yVakkenPanel.add(yVak);
				yVakkenPanel.setWidgetLeftWidth(yVak, vCnt * vakBreedte, Style.Unit.PX, vakBreedte, Style.Unit.PX);
				yVakkenPanel.setWidgetTopHeight(yVak, 0, Style.Unit.PX, vakHoogte, Style.Unit.PX);
				yVakken.addElement(yVak);
			}
			*/
}
		
		public void getImages() 
		{
			/*
			graphToolGWTClientBundle = GWT.create(GraphToolGWTClientBundle.class);
			graphToolCss = graphToolGWTClientBundle.getGraphToolGWTCSS();
			graphToolCss.ensureInjected();
			*/
			
			resetButtonResource = graphToolGWTClientBundle.resetTabelButtonResource();
			resetButtonImage = new Image(resetButtonResource.getSafeUri());
			resetButtonImage.addStyleName(graphToolCss.pushimage());
			
			pijlLinksButtonResource = graphToolGWTClientBundle.pijlLinksButtonResource();
			pijlLinksButtonImage = new Image(pijlLinksButtonResource.getSafeUri());
			pijlLinksButtonImage.addStyleName(graphToolCss.pushimage());
			
			pijlRechtsButtonResource = graphToolGWTClientBundle.pijlRechtsButtonResource();
			pijlRechtsButtonImage = new Image(pijlRechtsButtonResource.getSafeUri());
			pijlRechtsButtonImage.addStyleName(graphToolCss.pushimage());
			
			zoomInButtonResource = graphToolGWTClientBundle.zoomInTabelButtonResource();
			zoomInButtonImage = new Image(zoomInButtonResource.getSafeUri());
			zoomInButtonImage.addStyleName(graphToolCss.pushimage());
			
			zoomUitButtonResource = graphToolGWTClientBundle.zoomUitTabelButtonResource();
			zoomUitButtonImage = new Image(zoomUitButtonResource.getSafeUri());
			zoomUitButtonImage.addStyleName(graphToolCss.pushimage());
			
		}
		
		public void zetAlsTekenTool(boolean b, boolean grafiekTekenToolAan)
		{	isTekenTool = b;
			zoomInButton.removeFromParent();
			zoomUitButton.removeFromParent();
			functieKeuze.removeFromParent();
			tabelKeuze.removeFromParent();
			resetButton.removeFromParent();
			if (isTekenTool)
			{	//zoomInButton.setVisible(false);
				//zoomUitButton.setVisible(false);
				//functieKeuze.setVisible(false);
				if(!grafiekTekenToolAan)
				{
					this.add(tabelKeuze);
					this.add(resetButton);
					this.setWidgetLeftWidth(tabelKeuze, 3* offSet, Style.Unit.PX, 50, Style.Unit.PX);
					this.setWidgetTopHeight(tabelKeuze, vakHoogte / 2 - buttonSize / 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
					this.setWidgetRightWidth(resetButton, 3 * offSet, Style.Unit.PX, buttonSize, Style.Unit.PX);
					this.setWidgetTopHeight(resetButton, vakHoogte/2 - buttonSize/2, Style.Unit.PX, buttonSize, Style.Unit.PX);
					
				}
				//tabelKeuze.setVisible(!grafiekTekenToolAan);			
				else
				{	yAsNaamLabel.setVisible(true);
					zetYAsNaam(yAsNaam, false);
				}
				//yAsNaamLabel.setVisible(true);
				
				//resetButton.setVisible(!grafiekTekenToolAan);
				if(grafiekTekenToolAan)
					linkerBreedte = linkerBreedteTabel;
				else
					linkerBreedte = linkerBreedteTool;
				setXVakEditable(true);
				setYVakEditable(true);
			}
			else
			{	if(zooming)
				{	this.add(zoomInButton);
					this.add(zoomUitButton);
					this.setWidgetLeftWidth(zoomInButton, 3 * offSet, Style.Unit.PX, buttonSize, Style.Unit.PX);
					this.setWidgetTopHeight(zoomInButton, vakHoogte / 2 - buttonSize/2, Style.Unit.PX, buttonSize, Style.Unit.PX);
					this.setWidgetRightWidth(zoomUitButton, 3 * offSet, Style.Unit.PX, buttonSize, Style.Unit.PX);
					this.setWidgetTopHeight(zoomUitButton, vakHoogte / 2 - buttonSize/2, Style.Unit.PX, buttonSize, Style.Unit.PX);
				}	
				//zoomInButton.setVisible(zooming);
				//zoomUitButton.setVisible(zooming);
				//tabelKeuze.setVisible(false);
				//resetButton.setVisible(false);
				linkerBreedte = linkerBreedteTabel;
				setXVakEditable(false);
				setYVakEditable(false);
				this.add(yAsNaamLabel);
				
				//terugzetten als er functies en opdrachten zijn
				if(interactiePanel.typeOpdracht != interactiePanel.TEKENTABELPUNTEN)
					updateFunctieList(false);
			}
			//System.out.println("xAsNaamLabel.getOffsetWidth: " + xAsNaamLabel.getOffsetWidth());
			this.setWidgetLeftWidth(xAsNaamLabel, linkerBreedte, Style.Unit.PX, labelBreedte, Style.Unit.PX);
			//this.setWidgetTopHeight(xAsNaamLabel, vakHoogte / 2 - buttonSize / 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
			this.setWidgetLeftWidth(yAsNaamLabel, linkerBreedte, Style.Unit.PX, labelBreedte, Style.Unit.PX);
			//this.setWidgetTopHeight(yAsNaamLabel, 3 * vakHoogte / 2 - buttonSize / 2, Style.Unit.PX, buttonSize, Style.Unit.PX);
			//xAsNaamLabel.setLocation(linkerBreedte, offSet);
			//yAsNaamLabel.setLocation(linkerBreedte, 2 * offSet + vakHoogte);
			if(functieKeuze.isAttached())
			{	this.setWidgetLeftRight(xVakkenPanel, linkerBreedte + functieKeuzeBreedte, Style.Unit.PX,  
					rechterBreedte, Style.Unit.PX);
				this.setWidgetLeftRight(yVakkenPanel, linkerBreedte + functieKeuzeBreedte, Style.Unit.PX,  
					rechterBreedte, Style.Unit.PX);
			}
			else
			{	
				this.setWidgetLeftRight(xVakkenPanel, linkerBreedte + labelBreedte, Style.Unit.PX, 
						rechterBreedte, Style.Unit.PX);
				//this.setWidgetTopHeight(xVakkenPanel, 0, Style.Unit.PX, vakHoogte, Style.Unit.PX);
				this.setWidgetLeftRight(yVakkenPanel, linkerBreedte + labelBreedte, Style.Unit.PX, 
						rechterBreedte, Style.Unit.PX);
				//this.setWidgetTopHeight(yVakkenPanel, vakHoogte, Style.Unit.PX, vakHoogte, Style.Unit.PX);
			}
			/*
			xVakkenPanel.setLocation(yAsNaamLabel.getLocation().x + 
					yAsNaamLabel.getSize().width + offSet, offSet);
				yVakkenPanel.setLocation(yAsNaamLabel.getLocation().x + 
					yAsNaamLabel.getSize().width + offSet, 2 * offSet + vakHoogte);
					*/
			//}
			/*
			xVakkenPanel.setSize(
				getSize().width - xVakkenPanel.getLocation().x - rechterBreedte, 
				vakHoogte);
			yVakkenPanel.setSize(
				getSize().width - yVakkenPanel.getLocation().x - rechterBreedte, 
				vakHoogte);
				*/
		}
		
		public void zetZooming(boolean b)
		{	
			zoomInButton.removeFromParent();
			zoomUitButton.removeFromParent();
			
			if (isTekenTool)
				return;
		
			zooming = b;
			if(zooming)
			{	this.add(zoomInButton);
				this.add(zoomUitButton);
				this.setWidgetLeftWidth(zoomInButton, 3 * offSet, Style.Unit.PX, buttonSize, Style.Unit.PX);
				this.setWidgetTopHeight(zoomInButton, vakHoogte / 2 - buttonSize/2, Style.Unit.PX, buttonSize, Style.Unit.PX);
				this.setWidgetRightWidth(zoomUitButton, 3 * offSet, Style.Unit.PX, buttonSize, Style.Unit.PX);
				this.setWidgetTopHeight(zoomUitButton, vakHoogte / 2 - buttonSize/2, Style.Unit.PX, buttonSize, Style.Unit.PX);
				
			}
			//zoomInButton.setVisible(zooming);
			//zoomUitButton.setVisible(zooming);
		}
		
		public void zetEenTabel(boolean b)
		{	
			eenTabel = b;
			if (b)
			{	linkerBreedte = linkerBreedteTabel;
			}
			else
			{	linkerBreedte = linkerBreedteTool;
			}
			this.setWidgetLeftWidth(xAsNaamLabel, linkerBreedte, Style.Unit.PX, labelBreedte, Style.Unit.PX);
			this.setWidgetLeftWidth(yAsNaamLabel, linkerBreedte, Style.Unit.PX, labelBreedte, Style.Unit.PX);
			this.setWidgetLeftRight(xVakkenPanel, linkerBreedte + labelBreedte, Style.Unit.PX, rechterBreedte, Style.Unit.PX);
			this.setWidgetLeftRight(yVakkenPanel, linkerBreedte + labelBreedte, Style.Unit.PX, rechterBreedte, Style.Unit.PX);
			
			tabelKeuze.setVisible(!eenTabel);		
		}
		
		public void zetReset(boolean b)
		{	resetButton.removeFromParent();
			if(b)
			{	this.add(resetButton);
				this.setWidgetRightWidth(resetButton, 3 * offSet, Style.Unit.PX, buttonSize, Style.Unit.PX);
				this.setWidgetTopHeight(resetButton, vakHoogte/2 - buttonSize/2, Style.Unit.PX, buttonSize, Style.Unit.PX);
			}
			//resetButton.setVisible(b);
		}
		
		public int getVakBreedte()
		{	return vakBreedte;
		}
		
		public void setXVakEditable(boolean b)
		{	xVakEditable = b;
			// zet alle xVakken op xVakEditable
			for (int xCnt = 0; xCnt < xVakken.size(); xCnt++)
			{	TabelVakGWT xVak = (TabelVakGWT) xVakken.elementAt(xCnt);
				xVak.zetEditable(b);
			}
		}

		public boolean getXVakEditable()
		{
			return xVakEditable;
		}
		
		public void setYVakEditable(boolean b)
		{	yVakEditable = b;
			// zet alle yVakken op yVakEditable
			for (int yCnt = 0; yCnt < yVakken.size(); yCnt++)
			{	TabelVakGWT yVak = (TabelVakGWT) yVakken.elementAt(yCnt);
				yVak.zetEditable(b);
			}
			
		}
		
		
		public void zetFirstIndexVisible(int firstIndexVis)
		{	schuifVakjesNaarRechts();
			int dx = 0;
			for (int vCnt = 0; vCnt < firstIndexVis; vCnt++)
			{	//TabelVakGWT xVak = (TabelVakGWT) xVakken.elementAt(vCnt);
				//dx += xVak.getSize().width;
				//dx += xVak.getOffsetWidth();
				dx += (Integer) vakBreedtes.elementAt(vCnt);
			}
			for (int xCnt = 0; xCnt < xVakken.size(); xCnt++)
			{	TabelVakGWT xVak = (TabelVakGWT) xVakken.elementAt(xCnt);
				TabelVakGWT yVak = (TabelVakGWT) yVakken.elementAt(xCnt);
				//xVak.translate(-dx);
				int nieuweGrens = (Integer) linkerGrenzen.get(xCnt) - dx;
				linkerGrenzen.removeElementAt(xCnt);
				linkerGrenzen.add(xCnt, nieuweGrens);
				//xVakkenPanel.setWidgetLeftWidth(xVak, xVak.getAbsoluteLeft() - xVakkenPanel.getAbsoluteLeft() - dx, Style.Unit.PX, xVak.getOffsetWidth(), Style.Unit.PX);
				zetWidgetLinksBreedte(xVak, yVak, (Integer) linkerGrenzen.elementAt(xCnt) - dx, (Integer) vakBreedtes.elementAt(xCnt));
				//xVakkenPanel.setWidgetLeftWidth(xVak, (int) linkerGrenzen.elementAt(xCnt), Style.Unit.PX, (int) vakBreedtes.elementAt(xCnt), Style.Unit.PX);
			}
			/*
			for (int yCnt = 0; yCnt < yVakken.size(); yCnt++)
			{	
				//yVak.translate(-dx);
				//yVakkenPanel.setWidgetLeftWidth(yVak, yVak.getAbsoluteLeft() - yVakkenPanel.getAbsoluteLeft() - dx, Style.Unit.PX, yVak.getOffsetWidth(), Style.Unit.PX);
			yVakkenPanel.setWidgetLeftWidth(yVak, (int) linkerGrenzen.elementAt(yCnt), Style.Unit.PX, (int) vakBreedtes.elementAt(yCnt), Style.Unit.PX);
			}
			*/
		}
		
		
		public void vernieuwFirstIndexVisible(int index, int activeIndex)
		{	//TabelVakGWT netGevuldVak = (TabelVakGWT) xVakken.elementAt(index);
			//int vakjesBreedte = netGevuldVak.getSize().width;
			//int vakjesBreedte = netGevuldVak.getOffsetWidth();
			int vakjesBreedte = (Integer) vakBreedtes.elementAt(index);
			if(index > firstIndexVisible[activeIndex - 1])
			{	do{
					index--;
					//TabelVakGWT xVak = (TabelVakGWT) xVakken.elementAt(index);
					//vakjesBreedte += xVak.getSize().width;
					//vakjesBreedte += xVak.getOffsetWidth();
					vakjesBreedte += (Integer) vakBreedtes.elementAt(index);
				}
				//while(vakjesBreedte < xVakkenPanel.getWidth() && index > firstIndexVisible[activeIndex - 1]);
				while(vakjesBreedte < xVakkenPanel.getOffsetWidth() && index > firstIndexVisible[activeIndex - 1]);
			//if(vakjesBreedte > xVakkenPanel.getWidth())
			if(vakjesBreedte > xVakkenPanel.getOffsetWidth())
					index++;
			}
			
			zetFirstIndexVisible(index);
			firstIndexVisible[activeIndex - 1] = index;
		}
		
		public HashMap<String, Object> getState()
		{	
			double beginX = -2;
			double schaalFactorX = 1;
			//int[] firstIndexVisible = new int[3];
			
			/*
			ArrayList<Integer> firstIndexVisibleList = new ArrayList<Integer>();
			for(int i = 0; i < firstIndexVisible.length; i++)
			{
				firstIndexVisibleList.add(i, firstIndexVisible[i]);
			}
			*/
			
			
			beginX = this.beginX;
			schaalFactorX = this.schaalFactorX;
			//firstIndexVisible = this.firstIndexVisible;
			HashMap<String, Object> h = new HashMap<String, Object>();
				
			h.put("tabelBeginX", new Double(beginX));
			h.put("tabelSchaalFactorX", new Double(schaalFactorX));
			h.put("tabelFirstIndexVisible", firstIndexVisible);
			
			return h;
		}
		
		public void setState(Map<String, Object> launchState)
		{	double beginX = -2;
			double schaalFactorX = 1;
			int[] firstIndexVisible = new int[3];
			
			if(launchState != null)
			{
				if (launchState.containsKey("tabelBeginX"))
					beginX = ((Double) launchState.get("tabelBeginX")).doubleValue();
		    	if (launchState.containsKey("tabelSchaalFactorX")) 
					schaalFactorX = ((Double) launchState.get("tabelSchaalFactorX")).doubleValue();
				if (launchState.get("tabelFirstIndexVisible") != null)
				{	//ArrayList<Integer> firstIndexVisibleList = (ArrayList<Integer>) h.get("tabelFirstIndexVisible");
					if(JSONUtilities.toArrayList(launchState.get("tabelFirstIndexVisible")) != null)
					{	List<Object> firstIndexVisibleList = JSONUtilities.toArrayList(launchState.get("tabelFirstIndexVisible"));
						firstIndexVisible = new int[firstIndexVisibleList.size()];
						for(int i = 0; i < firstIndexVisibleList.size(); i++)
							firstIndexVisible[i] = ((Number) firstIndexVisibleList.get(i)).intValue();
					}
					else
					{	try{
							firstIndexVisible = (int[]) launchState.get("tabelFirstIndexVisible");
						}
						catch(Exception e){
							for(int i = 0; i < firstIndexVisible.length; i++)
							firstIndexVisible[i] = 0; 
						}
					}
				}
					//firstIndexVisible = (int[])h.get("tabelFirstIndexVisible");
				
				
		    	this.beginX = beginX;	
		    	this.schaalFactorX = schaalFactorX;
		    	this.firstIndexVisible = firstIndexVisible;
		    	
			}
			
	    	if(!isTekenTool)
			{	for(int i = 0; i < this.firstIndexVisible.length; i++)
	    			this.firstIndexVisible[i] = 0;//int[] oldFirstIndexVisible = new int[3]; 
				zetFunctie(func, true);// dit bevat een reset()
			}
	    	//setRandomAllowed(false);
	    }
		
		
		public void zetGrafiekComponent(GrafiekGWTVeld gc)
		{	grafiekGWTVeld = gc;
			//grKeuze.setForeground(grafiekComponent.getColor(0));
		}
		
		public GrafiekGWTVeld getGrafiekVeld()
		{
			return grafiekGWTVeld;
		}
		
		public void zetXAsNaam(String xasnaam)
		{	xAsNaam = xasnaam;
			xAsNaamLabel.setText(xasnaam);
			
			
			//dit moet nog beter; aangepast op xAsNaam en yAsNaam, zie hieronder.
			Canvas canvas = Canvas.createIfSupported();
			Context2d g = canvas.getContext2d();
			g.setFont("italic 12px sans-serif");
			
			TextMetrics tm = g.measureText(" " + xAsNaam + " ");
			double widthX = tm.getWidth();
			tm = g.measureText(" " + yAsNaam + " ");
			double widthY = tm.getWidth();
			
			if(widthX < basisLabelBreedte)
				widthX = basisLabelBreedte;
			if(widthY < basisLabelBreedte)
				widthY = basisLabelBreedte;
			if(widthX > 2 * basisLabelBreedte)
				widthX = 2 * basisLabelBreedte;
			if(widthY > 2 * basisLabelBreedte)
				widthY = basisLabelBreedte;
			
			labelBreedte = (int) Math.max(widthX, widthY);
			setWidgetLeftWidth(xAsNaamLabel, linkerBreedte, Style.Unit.PX, labelBreedte, Style.Unit.PX);
			setWidgetLeftWidth(yAsNaamLabel, linkerBreedte, Style.Unit.PX, labelBreedte, Style.Unit.PX);
			
			/*
			int width = itFm.stringWidth(" " + xasnaam + " ");
			if (width <= labelBreedte)
			{	xAsNaamLabel.setSize(labelBreedte, vakHoogte);
				yAsNaamLabel.setSize(labelBreedte, vakHoogte);	
			} 
			else if ((width > labelBreedte) && (width <= 2 * labelBreedte))
			{	xAsNaamLabel.setSize(width, vakHoogte);
				yAsNaamLabel.setSize(width, vakHoogte);	
			}
			else
			{	xAsNaamLabel.setSize(2 * labelBreedte, vakHoogte);
				yAsNaamLabel.setSize(2 * labelBreedte, vakHoogte);	
			}
			*/
			setWidgetLeftRight(xVakkenPanel, linkerBreedte + labelBreedte, Style.Unit.PX,
					rechterBreedte, Style.Unit.PX);
			//setWidgetTopHeight(xVakkenPanel, offSet, Style.Unit.PX, vakHoogte, Style.Unit.PX);
			setWidgetLeftRight(yVakkenPanel, linkerBreedte + labelBreedte, Style.Unit.PX,
					rechterBreedte, Style.Unit.PX);
			//setWidgetTopHeight(yVakkenPanel, 2 * offSet + vakHoogte, Style.Unit.PX, vakHoogte, Style.Unit.PX);
			
			/*
			xVakkenPanel.setLocation(
				xAsNaamLabel.getLocation().x + xAsNaamLabel.getSize().width + offSet,
				offSet);
			xVakkenPanel.setSize(
				getSize().width - xVakkenPanel.getLocation().x - rechterBreedte, 
				vakHoogte);
			yVakkenPanel.setLocation(
				yAsNaamLabel.getLocation().x + yAsNaamLabel.getSize().width + offSet,
				2 * offSet + vakHoogte);
			yVakkenPanel.setSize(
				getSize().width - yVakkenPanel.getLocation().x - rechterBreedte, 
				vakHoogte);
				*/
		}

		public void zetYAsNaam(String yn, boolean b)
		{	//boolean bepaalt of alleen de tekst moet worden veranderd, of ook de variabele.
			if(b)
				yAsNaam = yn;
			yAsNaamLabel.setText(yn);
			
			//dit moet nog beter; aangepast op xAsNaam en yAsNaam, zie hieronder.
			//xAsNaamLabel.setSize(labelBreedte + "px", vakHoogte + "px");
			//yAsNaamLabel.setSize(labelBreedte + "px", vakHoogte + "px");
			Canvas canvas = Canvas.createIfSupported();
			Context2d g = canvas.getContext2d();
			g.setFont("italic 12px sans-serif");
			
			TextMetrics tm = g.measureText(" " + xAsNaam + " ");
			double widthX = tm.getWidth();
			tm = g.measureText(" " + yn + " ");
			double widthY = tm.getWidth();
			
			if(widthX < basisLabelBreedte)
				widthX = basisLabelBreedte;
			if(widthY < basisLabelBreedte)
				widthY = basisLabelBreedte;
			if(widthX > 2 * basisLabelBreedte)
				widthX = 2 * basisLabelBreedte;
			if(widthY > 2 * basisLabelBreedte)
				widthY = basisLabelBreedte;
			
			labelBreedte = (int) Math.max(widthX, widthY);
			setWidgetLeftWidth(xAsNaamLabel, linkerBreedte, Style.Unit.PX, labelBreedte, Style.Unit.PX);
			setWidgetLeftWidth(yAsNaamLabel, linkerBreedte, Style.Unit.PX, labelBreedte, Style.Unit.PX);
			/*
			int width = itFm.stringWidth(" " + yn + " ");
			if (width <= labelBreedte)
			{	xAsNaamLabel.setSize(labelBreedte, vakHoogte);
				yAsNaamLabel.setSize(labelBreedte, vakHoogte);	
			} 
			else if ((width > labelBreedte) && (width <= 2 * labelBreedte))
			{	xAsNaamLabel.setSize(width, vakHoogte);
				yAsNaamLabel.setSize(width, vakHoogte);	
			}
			else
			{	xAsNaamLabel.setSize(2 * labelBreedte, vakHoogte);
				yAsNaamLabel.setSize(2 * labelBreedte, vakHoogte);	
			}
			*/
			
			setWidgetLeftRight(xVakkenPanel, linkerBreedte + labelBreedte, Style.Unit.PX,
					rechterBreedte, Style.Unit.PX);
			//setWidgetTopHeight(xVakkenPanel, offSet, Style.Unit.PX, vakHoogte, Style.Unit.PX);
			setWidgetLeftRight(yVakkenPanel, linkerBreedte + labelBreedte, Style.Unit.PX,
					rechterBreedte, Style.Unit.PX);
			//setWidgetTopHeight(yVakkenPanel, 2 * offSet + vakHoogte, Style.Unit.PX, vakHoogte, Style.Unit.PX);
			
			/*
			xVakkenPanel.setLocation(
				xAsNaamLabel.getLocation().x + xAsNaamLabel.getSize().width + offSet,
				offSet);
			xVakkenPanel.setSize(
				getSize().width - xVakkenPanel.getLocation().x - rechterBreedte, 
				vakHoogte);
			yVakkenPanel.setLocation(
				yAsNaamLabel.getLocation().x + yAsNaamLabel.getSize().width + offSet,
				2 * offSet + vakHoogte);
			yVakkenPanel.setSize(
				getSize().width - yVakkenPanel.getLocation().x - rechterBreedte, 
				vakHoogte);
				*/
		}
		
		public void showFunctieKeuze(boolean b)
		{	functieKeuze.removeFromParent();
			if(b)
			{	this.add(functieKeuze);
				this.setWidgetLeftWidth(functieKeuze, linkerBreedte, Style.Unit.PX, functieKeuzeBreedte, Style.Unit.PX);
				this.setWidgetTopHeight(functieKeuze, vakHoogte, Style.Unit.PX, vakHoogte, Style.Unit.PX);
				
			}
			//functieKeuze.setVisible(b);
			// als zichtbaar: verschuiven
			if (b)	
			{	yAsNaamLabel.setVisible(false);
				this.setWidgetLeftRight(xVakkenPanel, linkerBreedte + functieKeuzeBreedte,
						Style.Unit.PX, rechterBreedte, Style.Unit.PX);
				this.setWidgetLeftRight(yVakkenPanel, linkerBreedte + functieKeuzeBreedte,
						Style.Unit.PX, rechterBreedte, Style.Unit.PX);
				
				/*
				xVakkenPanel.setLocation(
					xAsNaamLabel.getLocation().x +  functieKeuze.getSize().width + offSet,
					offSet);
				xVakkenPanel.setSize(
					getSize().width - xVakkenPanel.getLocation().x - rechterBreedte, 
					vakHoogte);
				yVakkenPanel.setLocation(
					yAsNaamLabel.getLocation().x + functieKeuze.getSize().width + offSet,
					2 * offSet + vakHoogte);
				yVakkenPanel.setSize(
					getSize().width - yVakkenPanel.getLocation().x - rechterBreedte, 
					vakHoogte);
					*/
			}
			else
			{	this.setWidgetLeftRight(xVakkenPanel, linkerBreedte + labelBreedte,
					Style.Unit.PX, rechterBreedte, Style.Unit.PX);
				this.setWidgetLeftRight(yVakkenPanel, linkerBreedte + labelBreedte,
					Style.Unit.PX, rechterBreedte, Style.Unit.PX);
			
				/*
				//linkerBreedte = linkerBreedteTabel;
				xVakkenPanel.setLocation(xAsNaamLabel.getLocation().x + yAsNaamLabel.getWidth() + offSet,
					offSet);
				xVakkenPanel.setSize(getSize().width - xVakkenPanel.getLocation().x - rechterBreedte, 
					vakHoogte);
				yVakkenPanel.setLocation(yAsNaamLabel.getLocation().x + yAsNaamLabel.getWidth() + offSet,
					2 * offSet + vakHoogte);
				yVakkenPanel.setSize(
					getSize().width - yVakkenPanel.getLocation().x - rechterBreedte, 
					vakHoogte);
					*/
			}
		}
		
		public void setActiveIndex(int index, boolean setState)
		{	tabelKeuzeEnabled = false;
			tabelKeuze.setSelectedIndex(index - 1);
			tabelKeuze.getElement().getStyle().setColor(interactiePanel.getFormuleColor(index - 1).toString());
			tabelKeuzeEnabled = true;
			int[] oldFirstIndexVisible = new int[3]; 
			for(int i = 0; i < oldFirstIndexVisible.length; i++)
				oldFirstIndexVisible[i] = firstIndexVisible[i];
			Vector points = interactiePanel.getPoints(index, false);//, docent);
			if(!setState) //deze voorwaarde is nodig voor random grafiekpunten
				reset();
			zetTabelPunten(points, true);
			for(int i = 0; i < firstIndexVisible.length; i++)
				firstIndexVisible[i] = oldFirstIndexVisible[i];

			zetFirstIndexVisible(firstIndexVisible[index - 1]);
			
		}
		
		
		//Deze methode stelt bewust niet firstIndexVisible ook op 0, want wordt
		//ook gebruikt in zetFirstIndexVisible.
		public void schuifVakjesNaarRechts()
		{	//TabelVakGWT firstVak = (TabelVakGWT) xVakken.elementAt(0);
			//int dx = firstVak.getLocation().x; // negatief
			//int dx = firstVak.getAbsoluteLeft();
			int dx = (Integer) linkerGrenzen.elementAt(0);
			for (int xCnt = 0; xCnt < xVakken.size(); xCnt++)
			{	TabelVakGWT xVak = (TabelVakGWT) xVakken.elementAt(xCnt);
				TabelVakGWT yVak = (TabelVakGWT) yVakken.elementAt(xCnt);
				
				zetWidgetLinksBreedte(xVak, yVak, (Integer) linkerGrenzen.elementAt(xCnt) - dx, (Integer) vakBreedtes.elementAt(xCnt));
				
				
				//int nieuweGrens = (int) linkerGrenzen.elementAt(xCnt) - dx;
				//linkerGrenzen.removeElementAt(xCnt);
				//linkerGrenzen.add(xCnt, nieuweGrens);
				//xVak.translate(- dx);
				//xVakkenPanel.setWidgetLeftWidth(xVak, xVak.getAbsoluteLeft() - dx, 
					//	Style.Unit.PX, xVak.getOffsetWidth(), Style.Unit.PX);
				//xVakkenPanel.setWidgetLeftWidth(xVak, (int) linkerGrenzen.elementAt(xCnt), 
				//		Style.Unit.PX, (int) vakBreedtes.elementAt(xCnt), Style.Unit.PX);
				//yVakkenPanel.setWidgetLeftWidth(yVak, (int) linkerGrenzen.elementAt(xCnt), 
				//		Style.Unit.PX, (int) vakBreedtes.elementAt(xCnt), Style.Unit.PX);
			} 
			/*
			for (int yCnt = 0; yCnt < yVakken.size(); yCnt++)
			{	TabelVakGWT yVak = (TabelVakGWT) yVakken.elementAt(yCnt);
				//yVak.translate(- dx);
				//yVakkenPanel.setWidgetLeftWidth(yVak, yVak.getAbsoluteLeft() - dx, 
					//	Style.Unit.PX, yVak.getOffsetWidth(), Style.Unit.PX);
				yVakkenPanel.setWidgetLeftWidth(yVak, (int) linkerGrenzen.elementAt(yCnt), 
					Style.Unit.PX, (int) vakBreedtes.elementAt(yCnt), Style.Unit.PX);
			} 
			*/
		}
		
		public void zetWidgetLinksBreedte(TabelVakGWT xVak, TabelVakGWT yVak, int linkerGrens, int vakBreedte)
		{
			int index = xVak.vakIndex;
			linkerGrenzen.removeElementAt(index);
			linkerGrenzen.add(index, linkerGrens);
			vakBreedtes.removeElementAt(index);
			vakBreedtes.add(index, vakBreedte);
			
			xVakkenPanel.setWidgetLeftWidth(xVak, linkerGrens, Style.Unit.PX, vakBreedte, Style.Unit.PX);
			yVakkenPanel.setWidgetLeftWidth(yVak, linkerGrens, Style.Unit.PX, vakBreedte, Style.Unit.PX);
		}
		
		public void reset()
		{	schuifVakjesNaarRechts();
			firstIndexVisible[interactiePanel.getActiveIndex()-1] = 0;
			for (int index = 0; index < xVakken.size(); index++)
			{	zetText(index, "", "");
			}
		}
		
		public void zetText(int index, String xText, String yText)
		{	TabelVakGWT indexXVak = (TabelVakGWT) xVakken.elementAt(index);
			TabelVakGWT indexYVak = (TabelVakGWT) yVakken.elementAt(index);
			int xBreedte = indexXVak.zetText(xText);
			int yBreedte = indexYVak.zetText(yText);
			int breedte = Math.max(xBreedte, yBreedte);
			//int oudeBreedte = indexXVak.getSize().width;
			//int oudeBreedte = indexXVak.getOffsetWidth();
			int oudeBreedte = (Integer) vakBreedtes.elementAt(index);
			if (breedte != oudeBreedte)
			{	//vakBreedtes.removeElementAt(index);
				//vakBreedtes.add(index, breedte);
				
				//System.out.println("indexXVak.getAbsoluteLeft() voor: " + indexXVak.getAbsoluteLeft());
			//Dit gaat mis, omdat de vakken hier nog geen absoluteLeft hebben; ze staan immers nog niet op het scherm.
			//Dus proberen hiervoor al neer te zetten? Het wordt pas neergezet als alles volledig ge�nitialiseerd is, dus dat is lastig.
			//Andere optie: linkergrens vd widgets, en de offsetWidths, ergens bijhouden in een array. Dan kun je al die waardes
			//daaruit halen, in plaats van gebruik te maken van de absoluteLefts.
				//xVakkenPanel.setWidgetLeftWidth(indexXVak, indexXVak.getAbsoluteLeft() - xVakkenPanel.getAbsoluteLeft(), 
					//Style.Unit.PX, breedte, Style.Unit.PX);
				//xVakkenPanel.setWidgetLeftWidth(indexXVak, (int) linkerGrenzen.elementAt(index), Style.Unit.PX,
				//		(int) vakBreedtes.elementAt(index), Style.Unit.PX);
				//yVakkenPanel.setWidgetLeftWidth(indexYVak, indexYVak.getAbsoluteLeft() - yVakkenPanel.getAbsoluteLeft(),
				//	Style.Unit.PX, breedte, Style.Unit.PX);
				zetWidgetLinksBreedte(indexXVak, indexYVak, (Integer) linkerGrenzen.elementAt(index), breedte);
				
				indexXVak.zetBreedte(breedte);
				indexYVak.zetBreedte(breedte);
				int delta = breedte - oudeBreedte;
				for (int vCnt = index + 1; vCnt < xVakken.size(); vCnt++)
				{	TabelVakGWT xVak = (TabelVakGWT) xVakken.elementAt(vCnt);
					TabelVakGWT yVak = (TabelVakGWT) yVakken.elementAt(vCnt);
					zetWidgetLinksBreedte(xVak, yVak, (Integer) linkerGrenzen.elementAt(vCnt) + delta, (Integer) vakBreedtes.elementAt(vCnt));
					//xVak.translate(delta);
					//yVak.translate(delta);
					//xVakkenPanel.setWidgetLeftWidth(xVak, xVak.getAbsoluteLeft() - xVakkenPanel.getAbsoluteLeft() + delta, 
					//		Style.Unit.PX, xVak.getOffsetWidth(), Style.Unit.PX);
					//yVakkenPanel.setWidgetLeftWidth(yVak, yVak.getAbsoluteLeft() - yVakkenPanel.getAbsoluteLeft() + delta, 
					//		Style.Unit.PX, yVak.getOffsetWidth(), Style.Unit.PX);
				}
			}
		}
		
		public Expressie getFunctie(int num)
		{	return functies[num];
		}
		
		public int getFunctie(String name)
		{	int result = -1;
			for (int eCnt = 0; eCnt < maxAantalExpressies; eCnt++)
			{	if ((functieNamen[eCnt] != null) &&
				     functieNamen[eCnt].equals(name))
					result = eCnt;     
			}
			return result;
		}
		
		public int getEersteFunctie()
		{	int result = -1;
			boolean found = false;
			for (int eCnt = 0; eCnt < maxAantalExpressies; eCnt++)
			{	if ((functies[eCnt] != null) && !found)
				{	result = eCnt;
					found = true;
				}
			}
			return result;
		}
		
		public void vindAantalExpressies()
		{	aantalExpressies = 0;
			for (int eCnt = 0; eCnt < maxAantalExpressies; eCnt++)
			{	if (functies[eCnt] != null)
					aantalExpressies++;
			}
		}
		
		public void updateTabelNames(String[] expNaam, int maxAantalFuncties, boolean setState)
		{
			for(int i = 0; i < maxAantalFuncties; i++)
				if(getFunctie(i) != null)
					zetFunctieNaam(i, expNaam[i]);
			updateFunctieList(setState);
		}
		
		public void zetFunctieNaam(int num, String expNaam)
		{	functieNamen[num] = expNaam;	
		}
		
		public void zetFunctie(int nr, Expressie e, String expNaam, boolean update, boolean setState)
		{	
			functies[nr] = e;		
			functieNamen[nr] = expNaam;
			
			if (update)
				updateFunctieList(setState);
		}
		
		public void zetFunctie(Expressie e, boolean setState)
		{	
					
			// dit zou niet moeten gebeuren
			if (e == null)
			{	return;
			}
			// als oude expressie niet null, vervangen
			if (func != null && !setState)
				reset();
			
			// exp is nu niet null maar een nieuwe expressie
			func = e;
			for (int vCnt = 0; vCnt < xVakken.size(); vCnt++)
			{	double xWaarde = beginX + vCnt * schaalFactorX;
				zetFunctieWaarde(vCnt, xWaarde);
			}
			
			//repaint();
		}
		
		public void zetFunctieWaarde(int index, double xWaarde)
		{	//String xText = df.format(xWaarde);
			String xText = Double.toString(xWaarde);
			xText = trimText(xText);
			double yWaarde = func.geefWaarde(xWaarde);
			String yText = "";
			if (Double.isNaN(yWaarde) || Double.isInfinite(yWaarde))
				yText = "-";
			else
				//yText = df.format(yWaarde);
				yText = Double.toString(yWaarde);
			yText = trimText(yText);
			
			zetText(index, xText, yText);	

		}
		
		public String trimText(String text)
		{
			if(text.contains("."))
			{	text = text.substring(0, Math.min(text.indexOf(".") + 3, text.length()));
				while(text.endsWith("0"))
					text = text.substring(0, text.length() - 1);
				if(text.endsWith("."))
					text = text.substring(0, text.length() - 1);
			}
			return text;
		}
		
		
		public void updateFunctieList(boolean setState)
		{	
			vindAantalExpressies();
			// geen expressies of alle verwijderd
			functieKeuze.removeFromParent();
			if (aantalExpressies == 0)
			{	
				// expressieKeuze onzichtbaar
				//functieKeuze.setVisible(false);
				yAsNaamLabel.setVisible(true);
				zetYAsNaam(yAsNaam, false);		 
				func = null;
				funcNum = -1;
				if(!setState)
				{	reset();
				}
			} 
			else if (aantalExpressies == 1)
			{	
				// expressieKeuze onzichtbaar
				//functieKeuze.setVisible(false);
				int eIndex = getEersteFunctie();
				yAsNaamLabel.setVisible(true);
				zetYAsNaam(functieNamen[eIndex], false);
				// geen expressie actief
				funcNum = eIndex;
				zetFunctie(functies[funcNum], setState);
			}
			else 
			{	// vermijd actionEvents op expressieKeuze
				updatingList = true;
				// verwijder oude namen
				functieKeuze.clear();
			 	// nieuwe namen
			 	for (int nCnt = 0; nCnt < maxAantalExpressies; nCnt++)
			 	{	if (functieNamen[nCnt] != null)
			 			functieKeuze.addItem(functieNamen[nCnt]);
			 	}
			 	SelectElement selectElement = SelectElement.as(functieKeuze.getElement());
				NodeList<OptionElement> options = selectElement.getOptions();

				for (int i = 0; i < options.getLength(); i++) {
				     options.getItem(i).getStyle().setColor(interactiePanel.getFormuleColor(i).toString());
				}
				
			 	// nog geen expressie gezet of oude is verwijderd
				if ((funcNum == -1) || (functies[funcNum] == null))
				{	int eIndex = getEersteFunctie();
					funcNum = eIndex;
					zetFunctie(functies[funcNum], setState);
					functieKeuze.setSelectedIndex(eIndex);
					functieKeuze.getElement().getStyle().setColor(interactiePanel.getFormuleColor(eIndex).toString());
				}
				else // oude expressie blijft geselecteerd
				{	functieKeuze.setSelectedIndex(funcNum);
					functieKeuze.getElement().getStyle().setColor(interactiePanel.getFormuleColor(funcNum).toString());
				}
				
				showFunctieKeuze(true);
				// undo vermijdt
				updatingList = false;
			}
		}
		
		
		
		
		
		
		// vakjes in de tabel invullen: pas breedte aan aan inhoud
		// aan te roepen door KeyListener van x of y vak op index
		// in slechts een van de twee ben je aan het invullen
		public void adaptToText(int vakIndex)
		{	TabelVakGWT indexXVak = (TabelVakGWT) xVakken.elementAt(vakIndex);
			TabelVakGWT indexYVak = (TabelVakGWT) yVakken.elementAt(vakIndex);
			String xText = indexXVak.geefText();
			String yText = indexYVak.geefText();
			zetText(vakIndex, xText, yText);
		}
		
		public void processRandomTabelPunt(int vakIndex, String xText, String yText)
		{	
			boolean xIsRandom = (xText.length() > 2) && (xText.charAt(0) == '#') && (xText.charAt(xText.length() - 1) == '#');
			boolean yIsRandom = (yText.length() > 2) && (yText.charAt(0) == '#') && (yText.charAt(yText.length() - 1) == '#');

			double xVal = 0;
			String xString = "";
			boolean xError = false;
			if (xIsRandom)
			{	xVal = Double.NaN;
				xString = xText;
			}
			else
			{	try
				{	if (xVakEditable)
						xVal = Double.parseDouble(xText);
				}
				catch (NumberFormatException nfe)
				{	xError = true;
				}
			}
			
			double yVal = 0;
			String yString = "";
			boolean yError = false;
			if (yIsRandom)
			{	yVal = Double.NaN;
				yString = yText;
			}
			else
			{	try
				{	if (yVakEditable)
					{	yVal = Double.parseDouble(yText);
					}
				}
				catch (NumberFormatException nfe)
				{	yError = true;
				}
			}
			
			if (xError || yError)
			{	interactiePanel.removePoint(vakIndex, interactiePanel.getActiveIndex());
			}
			else // kijk of er al een punt met vakIndex is
			{	
				RealPoint rp = new RealPoint(xVal, yVal);
				rp.setIndex(interactiePanel.getActiveIndex());
				rp.setTabelIndex(vakIndex);
				rp.setxString(xText);
				rp.setyString(yText);
				int tpIndex = getTabelPunt(vakIndex);
				if (tpIndex >= 0)
				{	interactiePanel.removePoint(vakIndex, interactiePanel.getActiveIndex());//, docent);
				}
				interactiePanel.addInsert(rp);//, docent);
			}// update interactiePanel
			
			//zoeken naar alternatief
			//produceAction("points changed");
			grafiekGWTVeld.paint();
			
				
		}
		
		public void processTabelPunt(int vakIndex)
		{	
			TabelVakGWT indexXVak = (TabelVakGWT) xVakken.elementAt(vakIndex);
			TabelVakGWT indexYVak = (TabelVakGWT) yVakken.elementAt(vakIndex);
			String xText = indexXVak.geefText();
			String yText = indexYVak.geefText();
			// het nieuwe punt is geen volledig punt
			// er is b.v. een vakje uitgeveegd
			if ((xText.equals("") && xVakEditable) || (yText.equals("") && yVakEditable))
			{	interactiePanel.removePoint(vakIndex, interactiePanel.getActiveIndex());//, docent);
				//produceAction("points changed");
				return;
			}
			
			if (randomAllowed)
			{	processRandomTabelPunt(vakIndex, xText, yText);
				return;
			}
			
			double xVal = 0;
			double yVal = 0;
			boolean error = false;
			// dit zou niet nodig moeten zijn
			try
			{	if (xVakEditable)
					xVal = Double.parseDouble(xText);
				if (yVakEditable)
					yVal = Double.parseDouble(yText);
			}
			catch (NumberFormatException nfe)
			{	error = true;
			}
			if (error)
			{	interactiePanel.removePoint(vakIndex, interactiePanel.getActiveIndex());//, docent);
			}
			else // kijk of er al een punt met vakIndex is
			{	
				RealPoint rp = new RealPoint(xVal, yVal);
				rp.setIndex(interactiePanel.getActiveIndex());			
				rp.setTabelIndex(vakIndex);
				int tpIndex = getTabelPunt(vakIndex);
				if (tpIndex >= 0)
				{	interactiePanel.removePoint(vakIndex, interactiePanel.getActiveIndex());//, docent);
				}
				interactiePanel.addInsert(rp);//, docent);
			}
			grafiekGWTVeld.paint();
			//produceAction("points changed");
		}
		
		// vind punt met gegeven vakIndex van de actieve tabel (if any)
		public int getTabelPunt(int vakIndex)
		{	// er is er hoogstens 1
			Vector points = interactiePanel.getPoints(interactiePanel.getActiveIndex(), false);//), docent);
			int tpIndex = -1;
			for (int tCnt = 0; tCnt < points.size(); tCnt++)
			{	RealPoint rp = (RealPoint) points.elementAt(tCnt);
				if (rp.getTabelIndex() == vakIndex)
					tpIndex = tCnt;
			}
			return tpIndex;
		}
		
		public void zetTabelPunten(Vector points, boolean maakLeeg)
		{ 	if(maakLeeg)
				for (int index = 0; index < xVakken.size(); index++)
				{	zetText(index, "", "");
				}
		
			int maxIndex = 0;
			for (int tCnt = 0; tCnt < points.size(); tCnt++)
			{	RealPoint rp = (RealPoint) points.elementAt(tCnt);
				maxIndex = Math.max(rp.getTabelIndex(), maxIndex);
			}
			// kijk of er voldoende vakjes zijn
			if (maxIndex > xVakken.size())
			{	int nieuweVakken = maxIndex - xVakken.size();
				for (int vCnt = 0; vCnt < nieuweVakken; vCnt++)
				{
					TabelVakGWT lastVak = (TabelVakGWT) xVakken.lastElement();
				
					//TabelVakGWT xVak = 
					//		new TabelVakGWT(this, xVakken.size() + 1, 
					//			lastVak.getLocation().x + lastVak.getSize().width, 
					//			0, vakBreedte, vakHoogte, xVakEditable);
					TabelVakGWT xVak = 
							new TabelVakGWT(this, xVakken.size() + 1, vakBreedte, vakHoogte, xVakEditable);
								//lastVak.getAbsoluteLeft() + lastVak.getOffsetWidth(), 0, vakBreedte, vakHoogte, xVakEditable);
					// constructie			
					//xVak.zetFont(font);
					//xVak.randomAllowed = randomAllowed;
					xVakkenPanel.add(xVak);
					
					//xVakkenPanel.setWidgetLeftWidth(xVak, lastVak.getAbsoluteLeft() - xVakkenPanel.getAbsoluteLeft() + lastVak.getOffsetWidth(), 
					//		Style.Unit.PX, vakBreedte, Style.Unit.PX);
					xVakkenPanel.setWidgetTopHeight(xVak, 0, Style.Unit.PX, vakHoogte, Style.Unit.PX);
					xVakken.addElement(xVak);
					
					//TabelVakGWT yVak = 
					//	new TabelVakGWT(this, xVakken.size() + 1, 
					//		lastVak.getLocation().x + lastVak.getSize().width, 
					//		0, vakBreedte, vakHoogte, yVakEditable);
					TabelVakGWT yVak = 
							new TabelVakGWT(this, xVakken.size() + 1, vakBreedte, vakHoogte, yVakEditable);
								//lastVak.getAbsoluteLeft() + lastVak.getOffsetWidth(), 0, vakBreedte, vakHoogte, yVakEditable);
						// constructie		
					//yVak.zetFont(font);
					//yVak.randomAllowed = randomAllowed;
					yVakkenPanel.add(yVak);
					//yVakkenPanel.setWidgetLeftWidth(yVak, lastVak.getAbsoluteLeft() - yVakkenPanel.getAbsoluteLeft() + lastVak.getOffsetWidth(), 
					//		Style.Unit.PX, vakBreedte, Style.Unit.PX);
					yVakkenPanel.setWidgetTopHeight(yVak, 0, Style.Unit.PX, vakHoogte, Style.Unit.PX);
					yVakken.addElement(yVak);
					linkerGrenzen.addElement((Integer) linkerGrenzen.lastElement() + (Integer) vakBreedtes.lastElement());
					vakBreedtes.addElement(vakBreedte);
					zetWidgetLinksBreedte(xVak, yVak, (Integer) linkerGrenzen.lastElement(), (Integer) vakBreedtes.lastElement());
				}	
			}
			// zet de punten
			for (int tCnt = 0; tCnt < points.size(); tCnt++)
			{	RealPoint rp = (RealPoint) points.elementAt(tCnt);	
				zetTabelPunt(rp);
			}
			
			//repaint();
			//grafiekGWTVeld.paint();
		}
		
		public void zetTabelPunt(RealPoint rp)
		{	String xText = "";
			if (!Double.isNaN(rp.getX()))
				//xText = df.format(rp.getX());
				xText = Double.toString(rp.getX());
			else if (Double.isNaN(rp.getX()) && randomAllowed)
			{	xText = rp.getxString();
			}
			xText = trimText(xText);
			String yText = "";
			if (!Double.isNaN(rp.getY()))
				//yText = df.format(rp.getY());
				yText = Double.toString(rp.getY());
			else if (Double.isNaN(rp.getY()) && randomAllowed) 
			{	yText = rp.getyString();
			}
			yText = trimText(yText);
			zetText(rp.getTabelIndex(), xText, yText);
			
			
		}
		
		public void pijlRechtsAction()
		{	// deze verdwijnt naar links
			//TabelVakGWT lxVak = (TabelVakGWT) xVakken.elementAt(firstIndexVisible[interactiePanel.getActiveIndex()-1]);
			int schuifBreedte = (Integer) vakBreedtes.elementAt(firstIndexVisible[interactiePanel.getActiveIndex() - 1]);
			firstIndexVisible[interactiePanel.getActiveIndex()-1]++;
			beginX += schaalFactorX;
			// schuif alles naar links
			for (int xCnt = 0; xCnt < xVakken.size(); xCnt++)
			{	TabelVakGWT tabelVakX = (TabelVakGWT) xVakken.elementAt(xCnt);
				TabelVakGWT tabelVakY = (TabelVakGWT) yVakken.elementAt(xCnt);
				
				//tabelVak.translate(- lxVak.getSize().width);
				//tabelVak.translate(- lxVak.getOffsetWidth());
				//xVakkenPanel.setWidgetLeftWidth(tabelVak, tabelVak.getAbsoluteLeft() - xVakkenPanel.getAbsoluteLeft() - lxVak.getOffsetWidth(), 
				//		Style.Unit.PX, tabelVak.getOffsetWidth(), Style.Unit.PX);
				zetWidgetLinksBreedte(tabelVakX, tabelVakY, (Integer) linkerGrenzen.elementAt(xCnt) - schuifBreedte, (Integer) vakBreedtes.elementAt(xCnt));
			}
			/*
			for (int yCnt = 0; yCnt < yVakken.size(); yCnt++)
			{	TabelVakGWT tabelVak = (TabelVakGWT) yVakken.elementAt(yCnt);
				//tabelVak.translate(- lxVak.getSize().width);
				//tabelVak.translate(- lxVak.getOffsetWidth());
				yVakkenPanel.setWidgetLeftWidth(tabelVak, tabelVak.getAbsoluteLeft() - yVakkenPanel.getAbsoluteLeft() - lxVak.getOffsetWidth(), 
						Style.Unit.PX, tabelVak.getOffsetWidth(), Style.Unit.PX);
			}
			*/
			// maak voor het gemak altijd maar een nieuw vakje
			TabelVakGWT lastVak = (TabelVakGWT) xVakken.lastElement();
				
			//TabelVakGWT xVak = 
			//		new TabelVakGWT(this, xVakken.size()+1, 
			//			lastVak.getLocation().x + lastVak.getSize().width, 
			//			0, vakBreedte, vakHoogte, xVakEditable);
			TabelVakGWT xVak = 
					new TabelVakGWT(this, xVakken.size(), vakBreedte, vakHoogte, xVakEditable); 
						//lastVak.getAbsoluteLeft() + lastVak.getOffsetWidth(), 0, vakBreedte, vakHoogte, xVakEditable);
			
			// constructie			
			//xVak.zetFont(font);
			//xVak.randomAllowed = randomAllowed;
			xVakkenPanel.add(xVak);
			//xVakkenPanel.setWidgetLeftWidth(xVak, lastVak.getAbsoluteLeft() - xVakkenPanel.getAbsoluteLeft() + lastVak.getOffsetWidth(), 
			//		Style.Unit.PX, vakBreedte, Style.Unit.PX);
			xVakkenPanel.setWidgetTopHeight(xVak, 0, Style.Unit.PX, vakHoogte, Style.Unit.PX);
			xVakken.addElement(xVak);

			//TabelVakGWT yVak = 
			//	new TabelVakGWT(this, yVakken.size()+1,//xVakken veranderd in yVakken; xVakken is momenteel ��n groter.
			//		lastVak.getLocation().x + lastVak.getSize().width, 
			//		0, vakBreedte, vakHoogte, yVakEditable);
			TabelVakGWT yVak = 
					new TabelVakGWT(this, yVakken.size(), vakBreedte, vakHoogte, yVakEditable);
						//lastVak.getAbsoluteLeft() + lastVak.getOffsetWidth(), 0, vakBreedte, vakHoogte, yVakEditable);
						
			// constructie		
			//yVak.zetFont(font);
			//yVak.randomAllowed = randomAllowed;
			yVakkenPanel.add(yVak);
			//yVakkenPanel.setWidgetLeftWidth(yVak, lastVak.getAbsoluteLeft() - yVakkenPanel.getAbsoluteLeft() + lastVak.getOffsetWidth(), Style.Unit.PX, vakBreedte, Style.Unit.PX);
			yVakkenPanel.setWidgetTopHeight(yVak, 0, Style.Unit.PX, vakHoogte, Style.Unit.PX);
			yVakken.addElement(yVak);
			
			linkerGrenzen.addElement((Integer) linkerGrenzen.lastElement() + (Integer) vakBreedtes.lastElement());
			vakBreedtes.addElement(vakBreedte);
			zetWidgetLinksBreedte(xVak, yVak, (Integer) linkerGrenzen.lastElement(), (Integer) vakBreedtes.lastElement());
			if (func != null)
			{	
				double lastXWaarde = lastVak.geefWaarde();
				if (!Double.isNaN(lastXWaarde))	
				{	
					zetFunctieWaarde(xVakken.size() - 1, 
						lastXWaarde + schaalFactorX);
				}
			}
			//is dit genoeg vervanging van repaint()? 
			//grafiekGWTVeld.paint();
		}


		public void pijlLinksAction(String s)
		{	pijlLinksAction(false);
		}
	
		
		
		public void pijlLinksAction(boolean updateVakIndex)
		{	
			if (firstIndexVisible[interactiePanel.getActiveIndex()-1] == 0)
			{	for (int xCnt = 0; xCnt < xVakken.size(); xCnt++)
				{	TabelVakGWT tabelVakX = (TabelVakGWT) xVakken.elementAt(xCnt);
					TabelVakGWT tabelVakY = (TabelVakGWT) yVakken.elementAt(xCnt);
					//tabelVak.translate(vakBreedte);
					//xVakkenPanel.setWidgetLeftWidth(tabelVak, tabelVak.getAbsoluteLeft() - xVakkenPanel.getAbsoluteLeft() + vakBreedte, 
					//		Style.Unit.PX, tabelVak.getOffsetWidth(), Style.Unit.PX);
					zetWidgetLinksBreedte(tabelVakX, tabelVakY, (Integer) linkerGrenzen.elementAt(xCnt) + vakBreedte, 
							(Integer) vakBreedtes.elementAt(xCnt));
					
					tabelVakX.verhoogIndex();
					tabelVakY.verhoogIndex();
					
				}
				
				
				// maak nieuwe tabelVakken voor index 0 
				TabelVakGWT xVak = 	new TabelVakGWT(this, 0, vakBreedte, vakHoogte + 1, xVakEditable);
						//0, 0, vakBreedte, vakHoogte + 1, xVakEditable);
				// constructie	
				//xVak.zetFont(font);
				//xVak.randomAllowed = randomAllowed;
				xVakkenPanel.add(xVak);
				//xVakkenPanel.setWidgetLeftWidth(xVak, 0, Style.Unit.PX, vakBreedte, Style.Unit.PX);
				xVakkenPanel.setWidgetTopHeight(xVak, 0, Style.Unit.PX, vakHoogte + 1, Style.Unit.PX);
				
				TabelVakGWT yVak = 
						new TabelVakGWT(this, 0, vakBreedte, vakHoogte, yVakEditable);
								//0, 0, vakBreedte, vakHoogte, yVakEditable);
				// constructie	
				//yVak.zetFont(font);
				//yVak.randomAllowed = randomAllowed;
				yVakkenPanel.add(yVak);
				//yVakkenPanel.setWidgetLeftWidth(yVak, 0, Style.Unit.PX, vakBreedte, Style.Unit.PX);
				yVakkenPanel.setWidgetTopHeight(yVak, 0, Style.Unit.PX, vakHoogte, Style.Unit.PX);
				
				//opletten: als je nu al de linkergrens + breedte gaat zetten, raak je misschien de vakbreedte van de huidige nr 0 kwijt.
				
				
				xVakken.insertElementAt(xVak, 0);
				yVakken.insertElementAt(yVak, 0);
				linkerGrenzen.add(0, 0);
				vakBreedtes.add(0, vakBreedte);
				zetWidgetLinksBreedte(xVak, yVak, (Integer) linkerGrenzen.elementAt(0), (Integer) vakBreedtes.elementAt(0));
				
				/*
				for (int yCnt = 0; yCnt < yVakken.size(); yCnt++)
				{	TabelVakGWT tabelVak = (TabelVakGWT) yVakken.elementAt(yCnt);
					tabelVak.verhoogIndex();
					//tabelVak.translate(vakBreedte);
					yVakkenPanel.setWidgetLeftWidth(tabelVak, tabelVak.getAbsoluteLeft() - yVakkenPanel.getAbsoluteLeft() + vakBreedte, 
							Style.Unit.PX, tabelVak.getOffsetWidth(), Style.Unit.PX);
				}
				*/
				
					
				// firstIndexVisible blijft 0
				//for (int viCnt = 0; viCnt < interactiePanel.getPoints(interactiePanel.getActiveIndex(), docent).size(); viCnt++)
				for (int viCnt = 0; viCnt < interactiePanel.getPoints(interactiePanel.getActiveIndex(), false).size(); viCnt++)
				{	//RealPoint rp = (RealPoint) interactiePanel.getPoints(interactiePanel.getActiveIndex(), docent).elementAt(viCnt);
					RealPoint rp = (RealPoint) interactiePanel.getPoints(interactiePanel.getActiveIndex(), false).elementAt(viCnt);
					int tpvi = rp.getTabelIndex();
					
					if (updateVakIndex)
					{	tpvi += 1;
						rp.setTabelIndex(tpvi);
					}
					interactiePanel.getPoints(interactiePanel.getActiveIndex(), false).removeElementAt(viCnt);
					interactiePanel.getPoints(interactiePanel.getActiveIndex(), false).insertElementAt(rp, viCnt);
				}
				
			}
			else // if (firstIndexVisible > 0)
			{	firstIndexVisible[interactiePanel.getActiveIndex()-1]--;
			
				// dit vak wordt zichtbaar
				TabelVakGWT xVak = (TabelVakGWT) xVakken.elementAt(firstIndexVisible[interactiePanel.getActiveIndex()-1]);
				int index = firstIndexVisible[interactiePanel.getActiveIndex() - 1];
				// schuif alles naar rechts
				for (int xCnt = 0; xCnt < xVakken.size(); xCnt++)
				{	TabelVakGWT tabelVakX = (TabelVakGWT) xVakken.elementAt(xCnt);
					TabelVakGWT tabelVakY = (TabelVakGWT) yVakken.elementAt(xCnt);
					zetWidgetLinksBreedte(tabelVakX, tabelVakY, 
							(Integer) linkerGrenzen.elementAt(xCnt) + (Integer) vakBreedtes.elementAt(index), (Integer) vakBreedtes.elementAt(xCnt));
				
					//tabelVak.translate(xVak.getOffsetWidth());
					//tabelVak.translate(xVak.getSize().width);
					//xVakkenPanel.setWidgetLeftWidth(tabelVak, tabelVak.getAbsoluteLeft() - xVakkenPanel.getAbsoluteLeft() + xVak.getOffsetWidth(), 
					//		Style.Unit.PX, tabelVak.getOffsetWidth(), Style.Unit.PX);
				}
				/*
				for (int yCnt = 0; yCnt < yVakken.size(); yCnt++)
				{	TabelVakGWT tabelVak = (TabelVakGWT) yVakken.elementAt(yCnt);
					//tabelVak.translate(xVak.getOffsetWidth());
					//tabelVak.translate(xVak.getSize().width);
					yVakkenPanel.setWidgetLeftWidth(tabelVak, tabelVak.getAbsoluteLeft() - yVakkenPanel.getAbsoluteLeft() + xVak.getOffsetWidth(), 
							Style.Unit.PX, tabelVak.getOffsetWidth(), Style.Unit.PX);
				}*/
			}
			if (func != null)
			{	beginX -= schaalFactorX;
				zetFunctieWaarde(0, beginX);	
			}	
			
		
		//repaint();
		//grafiekGWTVeld.paint();
		}

		
		class PushClickHandler implements ClickHandler
	    {
	    	//public void onMouseDown(MouseDownEvent e)
	    	public void onClick(ClickEvent e)
	    	{
	    		
	    		//if (touchStart)
	    		//	return;
	    		
				//e.preventDefault();
				e.stopPropagation();
	    		
	    		if (e.getSource() == resetButton)
	    		{
	    			if (frozen)
	    				return;
	    			//if(docent)
	    				//reset();	
	    			interactiePanel.removePoints(interactiePanel.getActiveIndex());//, false);
	    		}
	    		else if(e.getSource() == tabelKeuze)
	    		{
	    			tabelKeuzeOpen = !tabelKeuzeOpen;
	    			int index = tabelKeuze.getSelectedIndex();
	    			if(index >= 0)
	    			{	tabelKeuze.getElement().getStyle().setColor(interactiePanel.getFormuleColor(index).toString());
	    				interactiePanel.setActiveIndex(index + 1, false);
	    			}
	    			if(tabelKeuzeOpen)
	    				return;
	    		}
	    		else if(e.getSource() == functieKeuze)
	    		{
	    			functieKeuzeOpen = !functieKeuzeOpen;
	    			int index = functieKeuze.getSelectedIndex();
	    			if(index >= 0 && !updatingList)
	    			{	functieKeuze.getElement().getStyle().setColor(interactiePanel.getFormuleColor(index).toString());
	    				String name = (String) functieKeuze.getItemText(index);
	    				funcNum = getFunctie(name);
	    				zetFunctie(functies[funcNum], false);
	    			}
	    			if(functieKeuzeOpen)
	    				return;
	    		}
	    		else if(e.getSource() == zoomInButton)
	    		{	double factorX = 1;
					if (factorRijNummerX % 3 == 2)
					{	factorX = 0.4;
					}
					else if (factorRijNummerX % 3 == 0)
					{	factorX = 0.5;
					}
					else 
					{	factorX = 0.5;
					}
					schaalFactorX *= factorX;
					factorRijNummerX--;
					beginX *= factorX;
					int[] oldFirstIndexVisible = new int[3]; 
					for(int i = 0; i < oldFirstIndexVisible.length; i++)
						oldFirstIndexVisible[i] = firstIndexVisible[i];
					zetFunctie(func, false);// dit bevat een reset()
					
					for(int i = 0; i < firstIndexVisible.length; i++)
						firstIndexVisible[i] = oldFirstIndexVisible[i];
					zetFirstIndexVisible(firstIndexVisible[interactiePanel.getActiveIndex() - 1]);
	    		}
	    		else if(e.getSource() == zoomUitButton)
	    		{	double factorX = 1;
					if (factorRijNummerX % 3 == 1)
					{	factorX = 2.5;
					}
					else if (factorRijNummerX % 3 == 2)
					{	factorX = 2;
					}
					else 
					{	factorX = 2;
					}
					schaalFactorX *= factorX;
					factorRijNummerX++;
					beginX *= factorX;
					int[] oldFirstIndexVisible = new int[3]; 
					for(int i = 0; i < oldFirstIndexVisible.length; i++)
						oldFirstIndexVisible[i] = firstIndexVisible[i];// dit bevat een reset()
					zetFunctie(func, false);
					for(int i = 0; i < firstIndexVisible.length; i++)
						firstIndexVisible[i] = oldFirstIndexVisible[i];
		
					zetFirstIndexVisible(firstIndexVisible[interactiePanel.getActiveIndex() - 1]);
							
				}
	    		else if(e.getSource() == pijlLinksButton)
	    		{
	    			if (!frozen)
					{
						pijlLinksAction(true);	
					}	
	    		}
	    		else if(e.getSource() == pijlRechtsButton)
	    		{
	    			if (!frozen)
					{
						pijlRechtsAction();
					}
	    		}
	    		grafiekGWTVeld.paint();
	    	}
	    	
	    }
}
