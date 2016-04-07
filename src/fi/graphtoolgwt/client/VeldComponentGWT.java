package fi.graphtoolgwt.client;

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;


import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;

//import fi.beans.stringutils.StringUtils;
//import fi.wiskopdr.GrafiekComponent;
//import fi.wiskopdr.VergelijkingVak;
//import fi.wiskopdr.GrafiekComponent;
//import fi.wiskopdr.ImageComponent;
//import fi.wiskopdr.expressies.*;



import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Expressie;

public class VeldComponentGWT extends LayoutPanel { 
	
	private static Logger logger = Logger.getLogger("VeldComponentGWT");

	
	public enum FieldGraphType {QUIVER, STREAMLINE};
	public enum FieldGraphArrowSizeMode { REALVALUE, FIXEDSIZE, SCALEDSIZE }	
	
	/* component defaults */
//	public static ArrayList<String> cVeldGrafiekTypeStrings = new ArrayList<String>();
//	public static ArrayList<String> cVeldGrafiekPijlGrootteModusStrings = new ArrayList<String>();	

	public final static int cDefault_hoogte = 150;
	public final static int cDefault_breedte = 300;
	
	public final static double cDefault_pijlSchaalFactor = 0.2;
	public final static int cDefault_pijlGroottePixels = 12;
	
	public final static FieldGraphType cDefault_grafiekType = FieldGraphType.QUIVER;
	public final static FieldGraphArrowSizeMode cDefault_pijlGrootteModus = FieldGraphArrowSizeMode.REALVALUE;
	public final static boolean cDefault_largerGridStartPoints = false;
	
	public final static int cDefault_aantalStelsels = 1;
	public final static FormuleFont cDefault_formulefont = FormuleHolder.getDefaultActiviteitFont().createCopy();
	
	/* component contstants */
	private final static int cVeldComponentGWT_accoladeXPositie = 25;
	public final static int cAantalFormulesPerStelsel = 2;
	
	private final static String[] cVeldComponentGWT_diffVarNamen = {"t"};
	
	public final static int cMaxAantalStelsels = 1;
	
	private final static CssColor cVeldComponentGWT_borderColor = CssColor.make(211, 211, 211);
	private final static BorderStyle cVeldComponentGWT_borderStyle = BorderStyle.SOLID;
	private final static int cVeldComponentGWT_borderWidthPix = 1;
	
	private final static int cVeldComponentGWT_mainWidgetBorderMargin = 0;
	private final static int cVeldComponentGWT_scrollWidgetBorderMargin = 5;
	private final static int cVeldComponentGWT_widgetScrollMargin = 20;
	private final static int cVeldComponentGWT_toolbarHeight = 0;
	
	private final static CssColor cRegelHighlightColor = CssColor.make(255, 255, 255);
	private final static CssColor cRegelBackgroundColor = CssColor.make(240, 240, 240);
	private final static CssColor cVeldComponentGWT_systemColor = CssColor.make(211, 211, 211); // Grey is the basic color
	
//	private VergelijkingVak[] formuleVakken; 

//	private GrafiekGWTVeld grafiekGWTVeld;
	
	FieldGraphType veldGrafiekType = cDefault_grafiekType;
	FieldGraphArrowSizeMode veldPijlGrootteModus = cDefault_pijlGrootteModus;
	int veldPijlGroottePixels = cDefault_pijlGroottePixels;
	double veldPijlSchaalfactor = cDefault_pijlSchaalFactor;
	boolean veldLargerGridStartPoints = cDefault_largerGridStartPoints;
	
	private int maxAantalStelsels = cMaxAantalStelsels;
	private int aantalStelsels = cDefault_aantalStelsels;
	
	private int veldComponentBreedte = cDefault_breedte;
	private int veldComponentHoogte = cDefault_hoogte;
	
	LayoutPanel stelselsPanel;
	
	SystemDiffEqPanelGWT systemPanels[] = new SystemDiffEqPanelGWT[maxAantalStelsels];
	
	private final GraphToolGWT interactiePanel;
	
//	private int actiefNummer;
	private String xAsNaam = "x";
	private String yAsNaam = "y";
	
//	private boolean functieBeginZichtbaar = true;
//	private boolean formeleFuncties = false;
//	private boolean domeinInstelbaar = false;
	public boolean docent;
	
	boolean grafiekKleurInstelbaar = true;
	boolean functieBeginAanpasbaar = false;
	
	public void processLaunchData(Map<String, Object> launchData) {

		if(launchData != null) {
			
			if(launchData.containsKey("veldGrafiekType"))
				veldGrafiekType = FieldGraphType.values()[ ((Number)launchData.get("veldGrafiekType")).intValue() ];
			
			if(launchData.containsKey("veldPijlGrootteModus"))
				veldPijlGrootteModus = FieldGraphArrowSizeMode.values()[ ((Number)launchData.get("veldPijlGrootteModus")).intValue() ];
			
			if(launchData.containsKey("veldPijlGroottePixels"))
				veldPijlGroottePixels = ((Number)launchData.get("veldPijlGroottePixels")).intValue();
			
			if(launchData.containsKey("veldPijlSchaalfactor"))
				veldPijlSchaalfactor = ((Double)launchData.get("veldPijlSchaalfactor")).doubleValue();
			
			if(launchData.containsKey("veldLargerGridStartPoints"))
				veldLargerGridStartPoints = ((Boolean)launchData.get("veldLargerGridStartPoints")).booleanValue();
		
			if(launchData.containsKey("veldComponentHoogte"))
				veldComponentHoogte = ((Number)launchData.get("veldComponentHoogte")).intValue();

//			if(launchData.containsKey("xAsNaam"))
//				xAsNaam = (String)launchData.get("xAsNaam");
//			if(launchData.containsKey("yAsNaam"))
//				yAsNaam = (String)launchData.get("yAsNaam");			
		}
	}
	
	private IsWidget wrap (IsWidget widget) {
		//FocusOnTouch.installKeyboard(interactiePanel.kb);
		FocusPanel focus = FocusOnTouch.wrap (widget.asWidget(), false);
		//focus.addKeyDownHandler(interactiePanel.keyHandler);
		//focus.addKeyPressHandler(interactiePanel.keyHandler);
		//focus.add(widget);
		//focus.addMouseUpHandler(new FocusOnTouch(focus));
		return focus;
	}
	
	public VeldComponentGWT(GraphToolGWT interactiePanel, Map<String, Object> launchData, int breedte ) {
		
//		graphToolGWTClientBundle = GWT.create(GraphToolGWTClientBundle.class);
//		graphToolCss = graphToolGWTClientBundle.getGraphToolGWTCSS();
//		graphToolCss.ensureInjected();
		
		this.interactiePanel = interactiePanel;
		this.veldComponentBreedte = breedte;
		processLaunchData(launchData);
		
		docent = false;
		
		LayoutPanel mainPanel = new LayoutPanel();
		final IsWidget wrap = wrap(mainPanel);
		this.add(wrap);
		this.setWidgetLeftWidth(wrap, 0, Style.Unit.PX, veldComponentBreedte, Style.Unit.PX);
		this.setWidgetTopHeight(wrap, 0, Style.Unit.PX, veldComponentHoogte, Style.Unit.PX); 
		FlowPanel rechthoekPanel = new FlowPanel();
		
		rechthoekPanel.getElement().getStyle().setBorderColor( cVeldComponentGWT_borderColor.toString() );
		rechthoekPanel.getElement().getStyle().setBorderStyle( cVeldComponentGWT_borderStyle);  
		rechthoekPanel.getElement().getStyle().setBorderWidth( cVeldComponentGWT_borderWidthPix, Style.Unit.PX);
		mainPanel.add(rechthoekPanel);
		mainPanel.setWidgetLeftWidth(rechthoekPanel, cVeldComponentGWT_mainWidgetBorderMargin, Style.Unit.PX, 
				veldComponentBreedte-cVeldComponentGWT_mainWidgetBorderMargin, Style.Unit.PX);
		mainPanel.setWidgetTopHeight(rechthoekPanel, cVeldComponentGWT_mainWidgetBorderMargin, Style.Unit.PX, 
				veldComponentHoogte-cVeldComponentGWT_mainWidgetBorderMargin, Style.Unit.PX);
		
		stelselsPanel = new LayoutPanel();
		ScrollPanel scrollPanel = new ScrollPanel(stelselsPanel);
		scrollPanel.setWidget(stelselsPanel);
		mainPanel.add(scrollPanel);
		mainPanel.setWidgetLeftWidth(scrollPanel, cVeldComponentGWT_scrollWidgetBorderMargin, Style.Unit.PX, 
				veldComponentBreedte-2*cVeldComponentGWT_scrollWidgetBorderMargin , Style.Unit.PX);
		mainPanel.setWidgetTopHeight(scrollPanel, cVeldComponentGWT_toolbarHeight+cVeldComponentGWT_scrollWidgetBorderMargin, Style.Unit.PX, 
				veldComponentHoogte - cVeldComponentGWT_toolbarHeight - 2* cVeldComponentGWT_scrollWidgetBorderMargin, Style.Unit.PX);
		
		String[] asNamen = new String[2];
		asNamen[0] = xAsNaam;
		asNamen[1] = yAsNaam;
		for (int i=0; i < aantalStelsels; i++) {
			
			systemPanels[i] = new SystemDiffEqPanelGWT(i,cAantalFormulesPerStelsel, veldComponentBreedte-cVeldComponentGWT_widgetScrollMargin);
			systemPanels[i].updateFunctionBegin(asNamen, cVeldComponentGWT_diffVarNamen[0]);
//			stelselsPanel.add(systemPanels[i]);
		}
		
		stelselsPanel.add(systemPanels[0]);
		stelselsPanel.setWidgetLeftWidth(systemPanels[0], 0, Style.Unit.PX, veldComponentBreedte, Style.Unit.PX);
		stelselsPanel.setWidgetTopHeight(systemPanels[0], 0, Style.Unit.PX, 300, Style.Unit.PX);
		//checkboxen[0].setValue(true);
		
		
//		fromuser = true;  // WAAROM??
		
		
//		domeinen = new double[maxAantalFormules][2];
//		for(int i = 0; i < maxAantalFormules; i++)
//		{	domeinen[i][0] = DEFAULTDOMEIN[0];
//			domeinen[i][1] = DEFAULTDOMEIN[1];
//		}		
		
		//isOngelijkheid = new boolean[maxAantalFormules];
		//for(int i = 0; i < maxAantalFormules; i++)
		//	isOngelijkheid[i] = false;
		
//		isEn = new boolean[maxAantalFormules];
//		for(int i = 0; i<isEn.length; i++)
//			isEn[i] = true;
		this.resize();

	}
	
	public void resize() {
//		for(int i = 0; i < aantalStelsels; i++)
//		{
//			int ashoogte = editors[i].getMainRegel().getAsHoogte();
//			
//			if(!functieBeginAanpasbaar)
//			{	FormuleViewer f = new FormuleViewer(functieBegin[i]);
//				if(regelPanels[i].getWidgetIndex(functieBeginViewers[i]) > -1)
//					regelPanels[i].setWidgetTopHeight(functieBeginViewers[i], Math.max(0, ashoogte - f.getMainRegel().getAsHoogte()), Style.Unit.PX, f.getHeight(), Style.Unit.PX);
//			}
//			if(regelPanels[i].getWidgetIndex(editorPanels[i]) > -1)
//				regelPanels[i].setWidgetTopHeight(editorPanels[i], 0, Style.Unit.PX, editors[i].getHeight(), Style.Unit.PX);
//			if(regelPanels[i].getWidgetIndex(checkboxen[i]) > -1)
//				regelPanels[i].setWidgetTopHeight(checkboxen[i], Math.max(ashoogte - 10, 5) , Style.Unit.PX, 15, Style.Unit.PX);
//			if(regelsPanel.getWidgetIndex(regelPanels[i]) > -1)
//				regelsPanel.setWidgetTopHeight(regelPanels[i], berekenRegelHoogte(i), Style.Unit.PX, Math.max(30, editors[i].getHeight()), Style.Unit.PX);
//			
//		}
	}
	
	public CssColor getSystemColor(int id) {
		return cVeldComponentGWT_systemColor;
	}

	
	public class SystemDiffEqPanelGWT extends LayoutPanel { 
		
		private final int cSystemDiffEqPanelGWT_interObjectMarginX = 5; // horizontal margin between all object-borders
		private final int cSystemDiffEqPanelGWT_interObjectMarginY = 10; // vertical margin between all object-borders
		
		private final int cSystemDiffEqPanelGWT_checkBoxSize = 12;
		private final int cSystemDiffEqPanelGWT_braceWidth = 20;
		
//		private final int ccSystemDiffEqPanelGWT_rowStartX = 35;

		Object parent;
		private int id;
		private int nrFunctions;
		private int systemHeight; // Height of this system of eqations in pixels
		private int systemWidth; // Width of this system of equations in pixels
		
		private boolean functionBeginUserChangable = false; // This is unchangable, for now
		
		private FormuleFont defaultfont = FormuleHolder.getDefaultActiviteitFont().createCopy();
		
		private String[] functionsBegin;
		private LayoutPanel[] functionPanels;
		private TouchPanel[] functionEditorPanels;
		private FormuleViewer[] functionBeginViewers;
		private FormuleEditor[] functionEditors;
		private Canvas braceCanvas;
		CheckBox cb = new CheckBox();
		
		int width = 1;		
		
		public void updateFunctionBegin(String[] functionNames, String derivativeName) {
			if (functionNames.length == nrFunctions) {
				for (int i = 0; i < nrFunctions; i++) {
					functionsBegin[i] = "$f$bd" + functionNames[i] + "$nd" + derivativeName + "@@=@";
					
					functionPanels[i].remove(functionBeginViewers[i].getAsPanel());
					functionBeginViewers[i] = new FormuleViewer(functionsBegin[i]);
					functionBeginViewers[i].setFont(defaultfont);
					functionBeginViewers[i].setDefaultFont(defaultfont);					
					functionPanels[i].add(functionBeginViewers[i].getAsPanel());
				}
			}
			adjustSize();
		}
		
		public void layoutRegelPanel(Widget w)
		{
			w.getElement().getStyle().setWidth(veldComponentBreedte - 5, Unit.PX);
			w.getElement().getStyle().setFloat(Float.LEFT);
			w.getElement().getStyle().setProperty("clear", "both");
			w.getElement().getStyle().setProperty("display", "block");
			w.getElement().getStyle().setBackgroundColor(cRegelHighlightColor.toString());
		}
		
		public void highLight(Widget w, boolean b)
		{
			w.getElement().getStyle().setBackgroundColor(b ? cRegelHighlightColor.toString() : cRegelBackgroundColor.toString());
		}
		
		private void adjustSize() {
			int[] rowHeight = new int[2];
			int systemHalfHeight = 0;

			// Determine RowHeigth & system Height
			int maxBeginWidth = 0;
			this.systemHeight = cSystemDiffEqPanelGWT_interObjectMarginY;
			for (int i=0; i<nrFunctions; i++) {
				rowHeight[i]=Math.max(functionBeginViewers[i].getHeight(), functionEditors[i].getHeight());
				maxBeginWidth = Math.max(maxBeginWidth,functionBeginViewers[i].getHeight());
				this.systemHeight += rowHeight[i];
				
				if ((double) (i+1) == (double) nrFunctions/2.0) {
					// nrFunctions == even & we're halfway
					systemHalfHeight = systemHeight + cSystemDiffEqPanelGWT_interObjectMarginY/2;
				} 
				
				double oddHalfTest =  ((double) (i+1)-((double) nrFunctions/2.0));
				if ((oddHalfTest > 0) && (oddHalfTest < 1)) {
					// nrFunctions == odd & we're halfway
					systemHalfHeight = systemHeight - rowHeight[i]/2;
				}

				this.systemHeight += cSystemDiffEqPanelGWT_interObjectMarginY;
			}
			
				
			// adjust Row Positions
			int posY = cSystemDiffEqPanelGWT_interObjectMarginY;
			for (int i=0; i<nrFunctions; i++) {

				// Position Entire Row
				int rowStartX = 3 * cSystemDiffEqPanelGWT_interObjectMarginX + cSystemDiffEqPanelGWT_checkBoxSize + cSystemDiffEqPanelGWT_braceWidth;
				this.setWidgetTopHeight(functionPanels[i], posY, Style.Unit.PX, rowHeight[i], Style.Unit.PX);
				this.setWidgetLeftWidth(functionPanels[i], rowStartX, Style.Unit.PX, systemWidth-rowStartX, Style.Unit.PX);
				posY += rowHeight[i] + cSystemDiffEqPanelGWT_interObjectMarginY;
				
				// Position Begin viewer within Row
				functionPanels[i].setWidgetTopHeight(functionBeginViewers[i].getAsPanel(), (rowHeight[i]-functionBeginViewers[i].getHeight())/2, Style.Unit.PX, 
						functionBeginViewers[i].getHeight(), Style.Unit.PX);
				functionPanels[i].setWidgetLeftWidth(functionBeginViewers[i].getAsPanel(), 0, Style.Unit.PX, 
						maxBeginWidth, Style.Unit.PX);
				
				// Position Function editor within Row
				functionPanels[i].setWidgetTopHeight(functionEditorPanels[i], (rowHeight[i]-functionEditors[i].getHeight())/2, Style.Unit.PX, 
						functionEditors[i].getHeight(), Style.Unit.PX);
				functionPanels[i].setWidgetLeftWidth(functionEditorPanels[i], maxBeginWidth+cSystemDiffEqPanelGWT_interObjectMarginX, Style.Unit.PX, 
						systemWidth-rowStartX-cSystemDiffEqPanelGWT_interObjectMarginX-maxBeginWidth, Style.Unit.PX);
			}				
			
			// adjust Checkbox Position
			this.setWidgetLeftWidth(cb, cSystemDiffEqPanelGWT_interObjectMarginX, Style.Unit.PX, cSystemDiffEqPanelGWT_checkBoxSize, Style.Unit.PX);
			this.setWidgetTopHeight(cb, systemHalfHeight-cSystemDiffEqPanelGWT_checkBoxSize/2, Style.Unit.PX, cSystemDiffEqPanelGWT_checkBoxSize, Style.Unit.PX);
				
			// adjust brace Canvas Position
			this.setWidgetLeftWidth(braceCanvas, 2* cSystemDiffEqPanelGWT_interObjectMarginX + cSystemDiffEqPanelGWT_checkBoxSize, 
					Style.Unit.PX, cSystemDiffEqPanelGWT_braceWidth, Style.Unit.PX);
			this.setWidgetTopHeight(braceCanvas, 0, Style.Unit.PX, systemHeight, Style.Unit.PX);
			
			// redraw brace
			Context2d ctx = braceCanvas.getContext2d();
			
			ctx.beginPath();
			ctx.arc(cSystemDiffEqPanelGWT_braceWidth, cSystemDiffEqPanelGWT_braceWidth/2.0, 
					cSystemDiffEqPanelGWT_braceWidth/2.0, 1.0*Math.PI, 1.5*Math.PI, false);
			
			ctx.moveTo(cSystemDiffEqPanelGWT_braceWidth/2.0, cSystemDiffEqPanelGWT_braceWidth/2.0);
			ctx.lineTo(cSystemDiffEqPanelGWT_braceWidth/2.0, systemHeight/2.0-cSystemDiffEqPanelGWT_braceWidth/2.0);
			
			ctx.arc(0.0, systemHalfHeight-cSystemDiffEqPanelGWT_braceWidth/2.0, 
					cSystemDiffEqPanelGWT_braceWidth/2.0, 0, 0.5*Math.PI, false);
			
			ctx.arc(0.0, systemHalfHeight+cSystemDiffEqPanelGWT_braceWidth/2.0, 
					cSystemDiffEqPanelGWT_braceWidth/2.0, 1.5*Math.PI, 2.0*Math.PI, false);
			
			ctx.moveTo(cSystemDiffEqPanelGWT_braceWidth/2.0, systemHeight/2+cSystemDiffEqPanelGWT_braceWidth/2.0);
			ctx.lineTo(cSystemDiffEqPanelGWT_braceWidth/2.0, systemHeight-cSystemDiffEqPanelGWT_braceWidth/2.0);

			ctx.arc(cSystemDiffEqPanelGWT_braceWidth, systemHeight-cSystemDiffEqPanelGWT_braceWidth/2.0, 
					cSystemDiffEqPanelGWT_braceWidth/2.0, 1.0*Math.PI, 0.5*Math.PI, true);
			ctx.stroke();
		}
		
		public SystemDiffEqPanelGWT(int id, int nrFunctions, int systemWidth) {
			super();
			
			this.id = id;
			this.nrFunctions = nrFunctions;
			this.systemWidth = systemWidth;

			functionsBegin = new String[nrFunctions];
			functionPanels = new LayoutPanel[nrFunctions];
			functionEditorPanels = new TouchPanel[nrFunctions];
			functionBeginViewers = new FormuleViewer[nrFunctions];
			functionEditors = new FormuleEditor[nrFunctions];
			braceCanvas = Canvas.createIfSupported();
			
			for (int i=0; i < nrFunctions; i++) {
				functionPanels[i] = new LayoutPanel();
				layoutRegelPanel(functionPanels[i]);
				highLight(functionPanels[i], true);
				
				if(!functionBeginUserChangable) {
					functionBeginViewers[i] = new FormuleViewer("$f$bdAs" + (i+1) + "$nd" + "Der" + "@@=@");
					functionBeginViewers[i].setFont(defaultfont);
					functionBeginViewers[i].setDefaultFont(defaultfont);					
					functionPanels[i].add(functionBeginViewers[i].getAsPanel());
				}

				functionEditors[i] = new FormuleEditor();
				((VeldComponentGWT) parent).getSystemColor(id);
				
				functionEditors[i].setColor(((VeldComponentGWT) parent).getSystemColor(id));
				functionEditors[i].setFont(defaultfont);
				functionEditors[i].setDefaultFont(defaultfont);
//				functionEditors[i].getAsPanel().getElement().getStyle().setMarginTop(5, Unit.PX);

				functionEditorPanels[i] = (TouchPanel) functionEditors[i].getAsPanel();

//				functionEditorPanels[i].getElement().getStyle().setProperty("display", "inline-block");
//				addFormulePanelListeners(functionEditorPanels[i], functionEditors[i]);
//				functionEditors[i].setCurrentElementRepaint();
				
				functionPanels[i].add(functionEditorPanels[i]);				
				this.add(functionPanels[i]);

				
//					if (functieBeginAanpasbaar)
//						editors[i].getAsPanel().getElement().getStyle().setMarginLeft(13, Unit.PX);
				
//					editors[i].getAsPanel().getElement().getStyle().setMarginTop(5, Unit.PX);
//					editorPanels[i] = (TouchPanel) editors[i].getAsPanel();
//					editors[i].setCurrent(0, 0);
					//kb = interactiePanel.kb; // THE ONE AND ONLY TODO betere interface naar interactiePanel.kb
					//editor.installKeyboard(kb);
					//editors[i].requestFocus();
					//if (!functieBeginAanpasbaar)
					//	regelPanels[i].add(functieBeginViewers[i]);
				
			}
			
			this.add(cb);		
			cb.setVisible(true);		
			
			this.add(braceCanvas);
			
			adjustSize();
		}
		
		public void setWidth(int width) {
			this.width = width;
		}
		
	}		
	
//	class CheckBoxClickHandler implements ClickHandler
//	{
//		int regelnummer;
//		
//		public CheckBoxClickHandler(int i)
//		{	super();
//			regelnummer = i;
//		}
//		
//		@Override
//		public void onClick(ClickEvent event) {
//			geselecteerd[regelnummer] = !geselecteerd[regelnummer];
//			parseFormule(regelnummer, false);
//			grafiekGWTVeld.paint();
//		}
//	}
	
//	public void setEditable(boolean b)
//	{	formuleVakken[0].formuleVak.setEditable(b);	
//	}
	
//	public void zetMaxAantalFormules(int num, boolean setState) {	
//		maxAantalFormules = num;
//		boolean knoppenNodig = maxAantalFormules > 0;
//		boolean checkboxenNodig = docent || (maxAantalFormules > 0 && (grafiekComponent == null || grafiekComponent.typeOpdracht == GraphToolInteractiePanel.GEENOPDRACHT));
//		
//		nieuweRegelKnop.setVisible(knoppenNodig);
//		verwijderRegelKnop.setVisible(knoppenNodig);
//		checkboxen[0].setVisible(checkboxenNodig);
//		formuleX = checkboxenNodig ? 30 : 10;
//		if(aantalRegels > maxAantalFormules)
//			aantalRegels = maxAantalFormules;
//				
//		zetFormuleRegels(maxAantalFormules, setState);
//		if(maxAantalFormules > domeinen.length)
//		{	double[][] oudDomeinen = new double[domeinen.length][2];
//			for(int i = 0; i < oudDomeinen.length; i++)
//			{	oudDomeinen[i][0] = domeinen[i][0];
//				oudDomeinen[i][1] = domeinen[i][1];
//			}
//			domeinen = new double[maxAantalFormules][2];
//			for(int i = 0; i < oudDomeinen.length; i++)
//			{	domeinen[i][0] = oudDomeinen[i][0];
//				domeinen[i][1] = oudDomeinen[i][1];
//			}
//			for(int i = oudDomeinen.length; i < maxAantalFormules; i++)
//			{	domeinen[i][0] = DEFAULTDOMEIN[0];
//				domeinen[i][1] = DEFAULTDOMEIN[1];
//			}
//			String[][] oudDomeinStrings = new String[domeinStrings.length][2];
//			for(int i = 0; i < oudDomeinen.length; i++)
//			{	oudDomeinStrings[i][0] = domeinStrings[i][0];
//				oudDomeinStrings[i][1] = domeinStrings[i][1];
//			}
//			domeinStrings = new String[maxAantalFormules][2];
//			for(int i = 0; i < oudDomeinStrings.length; i++)
//			{	domeinStrings[i][0] = oudDomeinStrings[i][0];
//				domeinStrings[i][1] = oudDomeinStrings[i][1];
//			}
//			for(int i = oudDomeinStrings.length; i < maxAantalFormules; i++)
//			{	domeinStrings[i][0] = "$f" + Double.NEGATIVE_INFINITY + "@";
//				domeinStrings[i][1] = "$f" + Double.POSITIVE_INFINITY + "@";
//			}
//		}
//	}
	
//	public void zetXAsNaam(String s, boolean setState)
//	{	String oudeXAsNaam = xAsNaam;
//		xAsNaam = s;
//
//		for(int i=0 ; i<formuleVakken.length ; i++)
//		{	String vervangString = formuleVakken[i].formuleVak.toString();
//			String vervangSubString = vervangString.substring(2, vervangString.length() - 1);
//			vervangString = "$f" + vervangSubString.replaceAll(oudeXAsNaam, xAsNaam) + "@";
//			formuleVakken[i].formuleVak.vulVak(vervangString);
//			
//			if(!functieBeginAanpasbaar && functieBeginZichtbaar)
//			{	vervangString = formuleVakken[i].functieBeginVak.toString();
//				vervangSubString = vervangString.substring(2, vervangString.length() - 1);
//				vervangString = "$f" + vervangSubString.replaceAll(oudeXAsNaam, xAsNaam) + "@";
//				formuleVakken[i].functieBeginVak.vulVak(vervangString);
//			}
//			parseFormule(i, setState);
//		}
//	}
	
//	public void zetYAsNaam(String s, boolean setState)
//	{	String oudeYAsNaam = yAsNaam;
//		yAsNaam = s;
//
//		for(int i=0 ; i<formuleVakken.length ; i++)
//		{	String vervangString = formuleVakken[i].formuleVak.toString();
//			String vervangSubString = vervangString.substring(2, vervangString.length() - 1);
//			vervangString = "$f" + vervangSubString.replaceAll(oudeYAsNaam, yAsNaam) + "@";
//			formuleVakken[i].formuleVak.vulVak(vervangString);
//			
//			if(!functieBeginAanpasbaar && functieBeginZichtbaar)
//			{	vervangString = formuleVakken[i].functieBeginVak.toString();
//				vervangSubString = vervangString.substring(2, vervangString.length() - 1);
//				vervangString = "$f" + vervangSubString.replaceAll(oudeYAsNaam, yAsNaam) + "@";
//				formuleVakken[i].functieBeginVak.vulVak(vervangString);
//			}
//
//			parseFormule(i, setState);
//		}
//	}
	
//	public void zetFormeleFuncties(boolean b, boolean setState)
//	{	formeleFuncties = b;
//		for(int i = 0; i < maxAantalStelsels; i++)
//			zetVoorvoegsel(i);
//		grafiekComponent.updateTabelNames(geefExpNamen(), setState);	
//	}
	
//	public void zetDomeinInstelbaar(boolean b, boolean setState)
//	{	domeinInstelbaar = b;
//		if(grafiekComponent != null && grafiekComponent.typeOpdracht == 1)
//			for(int i = 0; i < aantalRegels; i++)
//				if(i < domeinButtons.length)
//					domeinButtons[i].setVisible(domeinInstelbaar);
//		for(int i = 0; i < aantalRegels + 1; i++)
//			if(i < domeinButtons.length)
//				parseFormule(i, setState);
//	}
	
//	public void zetVoorvoegsel(int regelnummer)
//	{	String huidigeTekst = formuleVakken[regelnummer].formuleVak.toString();
//
//		boolean vervangen = huidigeTekst.equals("$f@") || huidigeTekst.endsWith("=@");
//		String differentiaalStr, asNaam="";
//		
//		if (soortVak[regelnummer]==DIFFERENTIAALX) {
//			asNaam = xAsNaam; 
//		} else { // soortVak[regelnummer] must be DIFFERENTIAALY) 
//			asNaam = yAsNaam; 
//		}
//		differentiaalStr = "$f$bd"+asNaam+"$nd"+diffVarNaam+"@@=@";
//		
//		if (functieBeginZichtbaar) {
//			if (functieBeginAanpasbaar) {
//				if (vervangen) {
//					formuleVakken[regelnummer].formuleVak.vulVak(differentiaalStr);
//				}
//			} else {
//				formuleVakken[regelnummer].functieBeginVak.vulVak(differentiaalStr);
//			}
//		}
//		
//	}
	
//	public void layoutVakken(boolean setState)
//	{	int hoogte = 10;
//		for(int i=0 ; i<maxAantalStelsels; i++) {	
//			for (int j=0; j<cAantalFormulesPerStelsel; j++) {
//				if(formuleVakken[(i*cAantalFormulesPerStelsel)+j]!=null) {	
////					formuleVakken[i].setLocation(formuleX,hoogte);
////					int breedte = this.getWidth() - 25;
////					if(getVerticalScrollBarVisible())	
////						breedte = this.getWidth() - 40;
//					if(soortVak[i * cAantalFormulesPerStelsel] == DIFFERENTIAALX && checkboxen != null && checkboxen[i] != null) {	
//						maakDifferentiaalVak( i * cAantalFormulesPerStelsel);
//					}
//					else if(checkboxen!=null && checkboxen[i]!=null)
//						checkboxen[i].setLocation(4,hoogte+formuleVakken[i].ashoogte-5);
//				
////					if(domeinButtons!=null && domeinButtons[i]!=null)domeinButtons[i].setLocation(breedte, hoogte+formuleVakken[i].ashoogte-5);
////					if(i>0 && enOfKnoppen != null && enOfKnoppen[i-1] != null)enOfKnoppen[i-1].setLocation(breedte, hoogte - 15);
//					hoogte = hoogte + formuleVakken[i].getSize().height + 10;
//				}
//			}
//		}
////		zetDomeinInstelbaar(domeinInstelbaar, setState);
//		repaint();
//	}
	
//	private void zetGrafiekComponent(GraphToolInteractiePanel gc)
//	{	grafiekComponent = gc;
//		zetGrafiekKleuren();
//	}
	
//	public void zetGrafiekKleuren() {	
//		if(formuleVakken != null && grafiekComponent != null) {
//			Color stelselColor = null;
////			int colorIndex = 0;
//			for(int i=0 ; i<formuleVakken.length ; i++) {
//				if (i % cAantalFormulesPerStelsel == 0) {
//					// stelselColor = grafiekComponent.getFormuleColor(colorIndex++);
//					// For now - all is black
//					stelselColor = Color.black;
//					formuleVakken[i].setFGColor(stelselColor); // First element in stelsel -> New color
//				} else {
//					formuleVakken[i].setFGColor(stelselColor); // Copy color of predecesor
//				}
//			}
//	}
		
//		if(formuleVakken != null && grafiekComponent != null)
//			for(int i=0 ; i<formuleVakken.length ; i++)
//				formuleVakken[i].setFGColor(grafiekComponent.getFormuleColor(i));
//		repaint();
//	}
	
//	public void zetGrafiekKleurInstelbaar(boolean b)
//	{	grafiekKleurInstelbaar = b;
//		for(int i = 0; i < checkboxen.length; i++)
//		{	if(b)
//				checkboxen[i].addMouseListener(this);
//			else
//				checkboxen[i].removeMouseListener(this);
//		}
//	}
	
//	public void zetFunctieBeginZichtbaar(boolean b, boolean setState)
//	{	functieBeginZichtbaar = b;
//		zetFormuleRegels(maxAantalStelsels, setState);
//	}
	
//	public void zetFunctieBeginAanpasbaar(boolean b, boolean setState)
//	{	functieBeginAanpasbaar = b;
//		zetFormuleRegels(maxAantalFormules, setState);
//	}
	
//	public void zetToegestaneFormules(boolean functie, boolean ongelijkheid, boolean impliciet, boolean verticaal, boolean parametrisatie, boolean setState)
//	{	functieToegestaan = functie;
//		ongelijkheidToegestaan = ongelijkheid;
//		implicieteFunctieToegestaan = impliciet;
//		verticaleLijnToegestaan = verticaal;
//		parametrisatieToegestaan = parametrisatie;
//		for(int i = 0; i < aantalRegels; i++)
//			parseFormule(i, setState);
//	}
	
	
//	public int getAantalRegels()
//	{
//		return aantalRegels;
//	}
	
//	public Hashtable getDocentState()
//	{	String[] docentExpressieStrings = null;
//		boolean[] docentGeselecteerd = null;
//		double[][] docentDomeinen = null;
//		String[][] docentDomeinStrings = null;
//		boolean[] docentIsEn = null;
//		//hier moet nog bij: ingestelde kleuren (?) Of wordt dat ergens anders bewaard?
//	
//		docentExpressieStrings = new String[maxAantalFormules];
//		docentGeselecteerd = new boolean[maxAantalFormules];
//		docentIsEn = new boolean[maxAantalFormules];
//		docentDomeinStrings = domeinStrings;
//		if(domeinen == null)
//			docentDomeinen = null;
//		else
//		{	docentDomeinen = new double[domeinen.length][2];
//			for(int i = 0; i < domeinen.length; i++)
//			{	docentDomeinen[i][0] = domeinen[i][0];
//				docentDomeinen[i][1] = domeinen[i][1];
//			}
//		}
//		for(int i=0 ; i<maxAantalFormules ; i++)
//		{	if(functieBeginAanpasbaar)
//				docentExpressieStrings[i] = formuleVakken[i].formuleVak.toString();
//			else
//			{	String s1 = formuleVakken[i].functieBeginVak.toString();
//				String s2 = formuleVakken[i].formuleVak.toString();
//				try{
//					s1 = s1.substring(0, s1.length() - 1);
//					s2 = s2.substring(2);
//				}
//				catch(Exception e){}
//				docentExpressieStrings[i] = s1 + s2;
//			}
//			docentGeselecteerd[i] = checkboxen[i].isSelected();
//			docentIsEn[i] = isEn[i];
//		}		
//	
//		Hashtable h = new Hashtable();
//		h.put("docentExpressieStrings", docentExpressieStrings);
//		h.put("docentGeselecteerd", docentGeselecteerd);
//		h.put("docentDomeinen", docentDomeinen);
//		h.put("docentDomeinStrings", docentDomeinStrings);
//		h.put("docentIsEn", docentIsEn);
//		return h;
//		
//	}
	
//	public Hashtable getState() {	
//		String[] veldGrafiekExpressieStrings = null;
//		boolean[] veldGrafiekGeselecteerd = null;
//		
//		veldGrafiekExpressieStrings = new String[maxAantalStelsels * cAantalFormulesPerStelsel];
//		veldGrafiekGeselecteerd = new boolean[maxAantalStelsels];
//		
//		for(int i=0 ; i<maxAantalStelsels ; i++) {	
//			for (int j=0; j<cAantalFormulesPerStelsel; j++) {
//				if(functieBeginAanpasbaar) {
//					veldGrafiekExpressieStrings[i * cAantalFormulesPerStelsel + j] = formuleVakken[i * cAantalFormulesPerStelsel + j].formuleVak.toString();
//				} else {
////					String s1 = formuleVakken[i * cAantalFormulesPerStelsel + j].functieBeginVak.toString();
//					String s1 = "";
//					String s2 = formuleVakken[i * cAantalFormulesPerStelsel + j].formuleVak.toString();
//					try{
//						s1 = s1.substring(0, s1.length() - 1);
//						s2 = s2.substring(2);
//					}
//					catch(Exception e){}
//					veldGrafiekExpressieStrings[i * cAantalFormulesPerStelsel + j] = s1 + s2;
//				}
//				if(veldGrafiekExpressieStrings[i * cAantalFormulesPerStelsel + j].endsWith("=@"))
//					veldGrafiekExpressieStrings[i * cAantalFormulesPerStelsel + j] = "$f@";
//				veldGrafiekGeselecteerd[i] = checkboxen[i].isSelected();
//			}
//		}
//		Hashtable h = new Hashtable();
//	    h.put("veldGrafiekExpressieStrings", veldGrafiekExpressieStrings);
//		h.put("veldGrafiekGeselecteerd", veldGrafiekGeselecteerd);
//
//	    return h;
//	}
	
//	public void setState(Map<String, Object> h)
//    {	String[] veldGrafiekExpressieStrings = null;
//		boolean[] veldGrafiekGeselecteerd = null;
//    	
//    	if (h.get("expressieStrings") != null) 
//    		veldGrafiekExpressieStrings = JSONUtilities.toStringArray(h.get("veldGrafiekExpressieStrings"));
//    		veldGrafiekExpressieStrings = GraphToolInteractiePanel.toStringArray(h.get("veldGrafiekExpressieStrings"));
//   		if(h.containsKey("veldGrafiekGeselecteerd")) 
//   			veldGrafiekGeselecteerd = GraphToolInteractiePanel.toBooleanArray(h.get("veldGrafiekGeselecteerd"));
//
//    	if (veldGrafiekExpressieStrings==null) {	
//    		return;
//    	}
//    	
//    	
//     	for(int i = 0; i < veldGrafiekExpressieStrings.length; i++)	 {	
//     		if(!veldGrafiekExpressieStrings[i].equals("$f@") && !(i > 0 && veldGrafiekExpressieStrings[i].endsWith("=@") && docent)) {	
//     			if(randomVars != null)
//     			try			
//    			{	veldGrafiekExpressieStrings[i] = FormuleParser.randomizeString(veldGrafiekExpressieStrings[i],randomVars,randomValues);
//    			}
//    			catch(Exception e)
//    			{	veldGrafiekExpressieStrings[i] = "$f???@";
//    				this.zetRandomFout(true);
//    			}
////				if(functieBeginAanpasbaar)
//				formuleVakken[i].formuleVak.vulVak(veldGrafiekExpressieStrings[i]);
//				parseFormule(i, true);
//			
////     			if(i>0)
////					add(formuleVakken[i],0);
//			}
//			formuleVakken[i].setVisible(true);
//
//     	}
//		aantalStelsels = veldGrafiekExpressieStrings.length / cAantalFormulesPerStelsel;
//     	
//    	if(veldGrafiekGeselecteerd==null) 
//    	{	return;
//    	}
//
//     	for(int i = 0; i < veldGrafiekGeselecteerd.length; i++)	 {	
//     	
//				checkboxen[i].setSelected(veldGrafiekGeselecteerd[i]);
//				if(docent || (maxAantalStelsels > 0 && (grafiekComponent == null || grafiekComponent.typeOpdracht == GraphToolInteractiePanel.GEENOPDRACHT))) {	
//					//System.out.println("hier visible gezet? " + i);
//					add(checkboxen[i],0);
//					//checkboxen[i].setVisible(true);
//				}
//				if(veldGrafiekGeselecteerd[i]) {
//					for (int j=0; j<cAantalFormulesPerStelsel; j++ ) {
//						parseFormule(i*cAantalFormulesPerStelsel+j, true);
//					}
//				}
//		}
//     	layoutVakken(true);
////     	grafiekComponent.updateTabelNames(geefExpNamen(), true);
//		
//    }
	
//	public double[] getDomein()
//	{
//		return domeinen[0];
//	}
	
//	public double[][] getDomeinen()
//	{
//		return domeinen;
//	}
	
//	public void zetDomein(double[] domein)
//	{
//		domeinen[0][0] = domein[0];
//		domeinen[0][1] = domein[1];
//	}
	
//	public String[][] getDomeinStrings()
//	{
//		return domeinStrings;
//	}
	
//	public void zetDomeinen(double[][] domein)
//	{	if(domein == null)
//			domeinen = null;
//		else
//		{	domeinen = new double[domein.length][2];
//			for(int i = 0; i < domeinen.length; i++)
//			{	domeinen[i][0] = domein[i][0];
//				domeinen[i][1] = domein[i][1];
//			}
//		}
//		for(int i = 0; i < domeinen.length; i++)
//		{	domeinStrings[i][0] = "$f" + Double.toString(domeinen[i][0]) + "@";
//			domeinStrings[i][1] = "$f" + Double.toString(domeinen[i][1]) + "@";
//		}
//		for(int i = 0; i < Math.min(domeinButtons.length, domeinen.length); i++)
//			domeinButtons[i].zetDomeinString(domeinStrings[i]);
//	}
	
//	public void zetDomein(String[] domeinStrings, int i)
//	{	
//		if(domeinen.length > i)
//		{	if(domeinStrings == null)
//			{	domeinen[i][0] = DEFAULTDOMEIN[0];
//				domeinen[i][1] = DEFAULTDOMEIN[1];
//				return;
//			}
//			if(domeinStrings[0].equals("$f" + Double.NEGATIVE_INFINITY + "@"))
//			{	domeinen[i][0] = Double.NEGATIVE_INFINITY;
//			}
//			else if(FormuleParser.geefExpressie(domeinStrings[0]) == null)
//			{	domeinen[i][0] = Double.NEGATIVE_INFINITY;
//			}
//			else
//			{	domeinen[i][0] = FormuleParser.geefExpressie(domeinStrings[0]).geefWaarde();
//			}
//			if(domeinStrings[1].equals("$f" + Double.POSITIVE_INFINITY + "@"))
//				domeinen[i][1] = Double.POSITIVE_INFINITY;
//			else if(FormuleParser.geefExpressie(domeinStrings[1]) == null)
//				domeinen[i][1] = Double.POSITIVE_INFINITY;
//			else
//				domeinen[i][1] = FormuleParser.geefExpressie(domeinStrings[1]).geefWaarde();
//		}
//	}
	
	
//	public void resetDomeinen()
//	{	domeinStrings = new String[maxAantalFormules][2];
//		for(int i = 0; i < maxAantalFormules; i++)
//		{	domeinStrings[i][0] = "$f" + Double.toString(Double.NEGATIVE_INFINITY) + "@";
//			domeinStrings[i][1] = "$f" + Double.toString(Double.POSITIVE_INFINITY) + "@";
//		}
//		
//		domeinen = new double[maxAantalFormules][2];
//		for(int i = 0; i < maxAantalFormules; i++)
//		{	domeinen[i][0] = DEFAULTDOMEIN[0];
//			domeinen[i][1] = DEFAULTDOMEIN[1];
//		}
//		
//	}
	
//	public void finish() 
//	{	for(int i=0 ; i<maxAantalFormules ; i++)
//		{	formuleVakken[i].formuleVak.finish();
//		}
//	}
	
//	public void zetDifferentiaalStelsels(int maxAantalStelsels, boolean setState) {
//		// Save relevant existing equation- descriptions
//		String[] exps = new String[cAantalFormulesPerStelsel * maxAantalStelsels];
//		for(int i = 0; i < cAantalFormulesPerStelsel * maxAantalStelsels; i++){
//			exps[i] = "$f@";
//		}	
//		
//		// clear all formulevakken
//		for(int i = 0; formuleVakken != null && i < formuleVakken.length; i++) {
//			if(formuleVakken[i] != null) {
//				if(i < maxAantalStelsels) {
//					if(functieBeginAanpasbaar) {	
//						exps[i] = formuleVakken[i].formuleVak.toString();
//					} else {	
//						String s1 = formuleVakken[i].functieBeginVak.toString();
//						String s2 = formuleVakken[i].formuleVak.toString();
//						try {
//							s1 = s1.substring(0, s1.length() - 1);
//							s2 = s2.substring(2);
//						} catch(Exception e) {
//						}
//						exps[i] = s1 + s2;
//					}
//				}
//				remove(formuleVakken[i]);
//			}
//		}
//		// Save relevant selection info + clear all checkboxes
//		boolean[] geselecteerd = new boolean[maxAantalStelsels];
//		for(int i = 0; checkboxen != null && i < checkboxen.length; i++) {
//			if(checkboxen[i] != null) {	
//				if(i < geselecteerd.length) {
//					geselecteerd[i] = checkboxen[i].isSelected();
//				}
//				remove(checkboxen[i]);
//			}
//		}
//		this.maxAantalStelsels = maxAantalStelsels; 
//		Color stelselColor = null;
//		
//		formuleVakken = new VergelijkingVak[cAantalFormulesPerStelsel * maxAantalStelsels];
//		for(int i=0 ; i<cAantalFormulesPerStelsel * maxAantalStelsels ; i++)
//		{	formuleVakken[i] = new VergelijkingVak(functieBeginAanpasbaar);
//			formuleVakken[i].setFont(WiskOpdr.formuleFont0);
//			if (i==0) {
//				formuleVakken[i].setLocation(formuleX,10);
//			} else {
//				formuleVakken[i].setLocation(formuleX,10+10*i + 41*i);
//			}
//			formuleVakken[i].setOpaque(false);
////			if(grafiekComponent != null) {
////				if (i % cAantalFormulesPerStelsel == 0) {
////					stelselColor = grafiekComponent.getFormuleColor(i);
////					formuleVakken[i].setFGColor(stelselColor); // First element in stelsel -> New color
////				} else {
////					formuleVakken[i].setFGColor(stelselColor); // Copy color of predecesor
////				}
////			}
//			if(functieBeginAanpasbaar) {
//				formuleVakken[i].formuleVak.vulVak(exps[i]);
//			}
//			formuleVakken[i].formuleVak.addActionListener(this);
////			formuleVakken[i].formuleVak.addFocusListener(this);
//			formuleVakken[i].formuleVak.geefActieveRegel().addFocusListener(this);
//		}
//		
//		if(maxAantalStelsels > 0)
//		//if(docent || (maxAantalFormules > 1 && (grafiekComponent == null || grafiekComponent.typeOpdracht == GraphToolInteractiePanel.GEENOPDRACHT)))
//		{	checkboxen = new JCheckBox[maxAantalStelsels];
//			for(int i=0 ; i<maxAantalStelsels ; i++)
//			{	checkboxen[i] = new JCheckBox();
//				checkboxen[i].setBounds(4,12 + 35*i*cAantalFormulesPerStelsel, 17, 17);
//				checkboxen[i].setOpaque(false);
//				checkboxen[i].addActionListener(this);
//				if(i < geselecteerd.length)
//					checkboxen[i].setSelected(geselecteerd[i]);
//				if(grafiekKleurInstelbaar)
//					checkboxen[i].addMouseListener(this);
//			}
//		}
//
//		for(int i = 0; i < aantalStelsels; i++) {	
//			add(formuleVakken[cAantalFormulesPerStelsel*i],0);
//			add(formuleVakken[cAantalFormulesPerStelsel*i+1],0);
//			if(docent || (maxAantalStelsels > 0 && (grafiekComponent == null || grafiekComponent.typeOpdracht == GraphToolInteractiePanel.GEENOPDRACHT)))
//				add(checkboxen[i]);
//		}
//		
//		formuleVakken[0].setVisible(true);
//		for(int i = 0; i < formuleVakken.length; i++) {
//			if(formuleVakken[i].formuleVak.toString().equals("$f@")) {
//				zetVoorvoegsel(i);
//			}
////			else if(formuleVakken[i].formuleVak.toString().equals("$f"+namen[i]+"(" + xAsNaam + ")=@") ||
////					formuleVakken[i].formuleVak.toString().equals("$f"+yAsNaam+"$s"+(i+1)+"@=@")||
////					formuleVakken[i].formuleVak.toString().equals("$f"+yAsNaam+"=@"))
////				if(!functieBeginZichtbaar)
////					formuleVakken[i].formuleVak.vulVak("$f@");
//		}
//			
//		formuleVak = formuleVakken[0].formuleVak;
//		formuleVak.requestFocus();
//		actiefNummer = 0;
//		
//		if(docent || (maxAantalStelsels > 0 && (grafiekComponent == null || grafiekComponent.typeOpdracht == GraphToolInteractiePanel.GEENOPDRACHT)))
//		{	checkboxen[0].setVisible(true);	
//			checkboxen[0].setSelected(true);
//		}
//		layoutVakken(setState);
//		
//		zetGrafiekKleuren();
//
//	} // end of zetDifferentiaalStelsels
	
	
//	public void zetFormuleRegels(int maxAantalFormules, boolean setState) {	
//		String[] exps = new String[maxAantalFormules];
//		for(int i = 0; i < maxAantalFormules; i++){
//			exps[i] = "$f@";
//		}	
//		for(int i = 0; formuleVakken != null && i < formuleVakken.length; i++)
//			if(formuleVakken[i] != null)
//			{	if(i < maxAantalFormules)
//				{	if(functieBeginAanpasbaar)
//					{	exps[i] = formuleVakken[i].formuleVak.toString();
//					}
//					else
//					{	
//						String s1 = formuleVakken[i].functieBeginVak.toString();
//						String s2 = formuleVakken[i].formuleVak.toString();
//						try{
//							s1 = s1.substring(0, s1.length() - 1);
//							s2 = s2.substring(2);
//						}
//						catch(Exception e){}
//						exps[i] = s1 + s2;
//					}
//				}
//				remove(formuleVakken[i]);
//			}
//		boolean[] geselecteerd = new boolean[maxAantalFormules];
//		for(int i = 0; checkboxen != null && i < checkboxen.length; i++)
//			if(checkboxen[i] != null)
//			{	if(i < geselecteerd.length)
//					geselecteerd[i] = checkboxen[i].isSelected();
//				remove(checkboxen[i]);
//			}
////		for(int i = 0; domeinButtons != null && i < domeinButtons.length; i++)
////			if(domeinButtons[i] != null)
////				remove(domeinButtons[i]);
//		
//		this.maxAantalStelsels = maxAantalFormules; 
//		
//		formuleVakken = new VergelijkingVak[maxAantalFormules];
//		for(int i=0 ; i<maxAantalFormules ; i++)
//		{	formuleVakken[i] = new VergelijkingVak(functieBeginAanpasbaar);
//			formuleVakken[i].setFont(WiskOpdr.formuleFont0);
//			formuleVakken[i].setLocation(formuleX,10 + 35*i);
//			formuleVakken[i].setOpaque(false);
//			if(grafiekComponent != null)
//				formuleVakken[i].setFGColor(grafiekComponent.getFormuleColor(i));
//			if(functieBeginAanpasbaar)
//				formuleVakken[i].formuleVak.vulVak(exps[i]);
////			parseFormule(exps[i], i, setState);
//			formuleVakken[i].formuleVak.addActionListener(this);
//			formuleVakken[i].formuleVak.addFocusListener(this);
//		}
//		
//		if(maxAantalFormules > 0)
//		//if(docent || (maxAantalFormules > 1 && (grafiekComponent == null || grafiekComponent.typeOpdracht == GraphToolInteractiePanel.GEENOPDRACHT)))
//		{	checkboxen = new JCheckBox[maxAantalFormules];
//			for(int i=0 ; i<maxAantalFormules ; i++)
//			{	checkboxen[i] = new JCheckBox();
//				checkboxen[i].setBounds(4,12 + 35*i, 17, 17);
//				checkboxen[i].setOpaque(false);
//				checkboxen[i].addActionListener(this);
//				if(i < geselecteerd.length)
//					checkboxen[i].setSelected(geselecteerd[i]);
//				if(grafiekKleurInstelbaar)
//					checkboxen[i].addMouseListener(this);
//			}
//		}
//
////		domeinButtons = new DomeinButton[maxAantalFormules];
////		for(int i=0; i < maxAantalFormules; i++)
////		{	domeinButtons[i] = new DomeinButton();
////			if(i < domeinStrings.length)
////				domeinButtons[i].zetDomeinString(domeinStrings[i]);
////			domeinButtons[i].addActionListener(this);
////		}
////		domeinButtons[0].setLocation(this.getWidth() - 25, 5 + formuleVakken[0].ashoogte);
//		System.out.println("4- zetFormuleRegels - soortvak 0 = " + soortVak[0]);
//		
//		for(int i = 0; i < aantalStelsels; i++)
//		{	add(formuleVakken[i],0);
//			if(docent || (maxAantalFormules > 0 && (grafiekComponent == null || grafiekComponent.typeOpdracht == GraphToolInteractiePanel.GEENOPDRACHT)))
//				add(checkboxen[i]);
////			add(domeinButtons[i]);
//		}
////		domeinButtons[0].setVisible(false);
////		isEn = new boolean[maxAantalFormules];
////		for(int i = 0; i<isEn.length; i++)
////			isEn[i] = true;
//		
////		enOfKnoppen = new JButton[maxAantalFormules];
////		for(int i=0 ; i<maxAantalFormules ; i++)
////		{	enOfKnoppen[i] = new JButton(GraphTool.rb.getString("enOfButton_En"));
////			enOfKnoppen[i].setMargin(new Insets(0,0,0,0));
////			enOfKnoppen[i].setSize(25, 20);
////			enOfKnoppen[i].setOpaque(false);
////			enOfKnoppen[i].addActionListener(this);
////		}
//		
//		formuleVakken[0].setVisible(true);
//		for(int i = 0; i < formuleVakken.length; i++) //aangepast 20-1-2014; leidt dit tot problemen? Dan terugzetten naar alleen doen voor 0 en niet voor alle i.
//		{
//			if(formuleVakken[i].formuleVak.toString().equals("$f@"))
//				zetVoorvoegsel(i);
//			else if(formuleVakken[i].formuleVak.toString().equals("$f"+namen[i]+"(" + xAsNaam + ")=@") ||
//					formuleVakken[i].formuleVak.toString().equals("$f"+yAsNaam+"$s"+(i+1)+"@=@")||
//					formuleVakken[i].formuleVak.toString().equals("$f"+yAsNaam+"=@"))
//				if(!functieBeginZichtbaar)
//					formuleVakken[i].formuleVak.vulVak("$f@");
//		}
//			
//		formuleVak = formuleVakken[0].formuleVak;
//		formuleVak.requestFocus();
//		actiefNummer = 0;
//		
//		if(docent || (maxAantalFormules > 0 && (grafiekComponent == null || grafiekComponent.typeOpdracht == GraphToolInteractiePanel.GEENOPDRACHT)))
//		{	checkboxen[0].setVisible(true);	
//			checkboxen[0].setSelected(true);
//		}
//		layoutVakken(setState);
//
//	}
	
	/*
	public static void zetPlaatjes(Image gk, Image fk)
	{	GOEDKRUL = gk;
		FOUTKRUIS = fk;
	}
	*/
	
//	public FormuleVak geefFormuleVak()
//	{	return formuleVak;
//	}
	
//	public Expressie geefExpressie() {	
//		FormuleParser p = new FormuleParser();
//		return formuleVak.geefExpressie();
//	}
	
//	public String[] geefExpNamen()
//	{	String[] expNaam = new String[maxAantalStelsels];
//		for (int i = 0; i < maxAantalStelsels; i++)
//		{	expNaam[i] = geefExpNaam(i);
//		}
//		return expNaam;
//	}
	
//	public String geefExpNaam(int i)
//	{	String expNaam = "";
//		if(formeleFuncties)
//			expNaam = namen[i] + "(" + xAsNaam + ")";
//		else if (aantalStelsels > 1)
//			expNaam = yAsNaam + (i + 1);
//		else
//			expNaam = yAsNaam;
//	
//		return expNaam;
//	}
	
	public int getMaxAantalFuncties()
	{
		return maxAantalStelsels;
	}
	
//	public boolean geefIsEn(int i)
//	{
//		return isEn[i];
//	}
	
//	public void terugNaarEenRegel(boolean setState)
//	{	for (int rCnt = aantalRegels; rCnt > 1; rCnt--)
//		{	
//			grafiekComponent.zetFunctie(aantalRegels - 1, null, "$f@", null, DEFAULTDOMEIN, true, setState, docent);
//			
//			formuleVakken[aantalRegels-1].formuleVak.vulVak("$f@");
//			remove(formuleVakken[aantalRegels-1]);
//			remove(checkboxen[aantalRegels-1]);
////			remove(domeinButtons[aantalRegels-1]);
//			layoutVakken(setState);
//			aantalRegels--;
//		}
//		if(functieBeginZichtbaar)
//		{	for(int i=0 ; i<maxAantalFormules ; i++)
//			{	zetVoorvoegsel(i);
//			}
//		}
//		else
//			for(int i = 0; i < maxAantalFormules; i++)
//				formuleVakken[i].formuleVak.vulVak("$f@");
//		
//		parseFormule("$f@", 0, setState);
//	}

//	public void parseFormule(int regelnummer, boolean setState)
//	{
//		String sExpressie = formuleVakken[regelnummer].formuleVak.toString();
//		int stelselNummer = -1;
//		Expressie expressie = null;
//		String sAs = "";
//		
//		if (soortVak[regelnummer] == DIFFERENTIAALX) {
//			sAs = "X";
//			stelselNummer = regelnummer / cAantalFormulesPerStelsel;
//		}
//		
//		if (soortVak[regelnummer] == DIFFERENTIAALY) {
//			sAs = "Y";
//			stelselNummer = (regelnummer-1) / cAantalFormulesPerStelsel;
//		}
//
//		
//		if (checkboxen[stelselNummer].isSelected()) {
//			expressie = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString(sExpressie )));
//		}
//	
//		grafiekComponent.zetVectorVeld(stelselNummer, sAs, expressie, setState);
//		
//	}
	
//	public void parseFormule(int regelnummer, boolean setState)
//	{	//System.out.println("parseFormule(" + regelnummer + ", " + Boolean.toString(setState));
//		if(regelnummer >= formuleVakken.length)
//			return;
//		if(formuleVakken[regelnummer].functieBeginVak == null || formuleVakken[regelnummer].functieBeginVak.toString().length() == 0)
//		{	String s = formuleVakken[regelnummer].formuleVak.toString();
//			parseFormule(s, regelnummer, setState);
//		}
//		else
//		{	String s1 = formuleVakken[regelnummer].functieBeginVak.toString();
//			String s2 = formuleVakken[regelnummer].formuleVak.toString();
//			try{
//				s1 = s1.substring(0, s1.length() - 1);
//				s2 = s2.substring(2);
//				String s = s1 + s2;
//				parseFormule(s, regelnummer, setState);
//			}
//			catch(Exception e){}
//		}
//	}


	
	//public Vergelijking parseFormule(String s)
//	public void parseFormule(String s, int regelnummer, boolean setState)
//	{	//System.out.println("parseFormule(" + s + ", " + regelnummer + ", " + Boolean.toString(setState));
//		//In alle lijstjes met expressies het huidige regelnummer verwijderen. 
//		//Zo voorkom je dat expressies blijven staan als het type expressie verandert.
//		//Hier moet ik nog even goed naar kijken in het geval van parametrisaties, omdat je dan twee regelnummers tegelijk nodig hebt.
//		
//		//voor parametrisaties is er een aantal opties:
//		//er staat al een xparametrisatie, dan is de volgende regel ook een y-parametrisatie. Haal je die dan ook weg?
//		//in principe wel, als je een nieuwe xparametrisatie typt, dan wordt de volgende regel automatisch weer gemarkeerd als yparam.
//
////		if(grafiekComponent != null && grafiekComponent.typeOpdracht != 1 && regelnummer < domeinButtons.length)
////			domeinButtons[regelnummer].setVisible(false);
//		if(soortVak[regelnummer] == PARAMETRISATIEX)
//		{	soortVak[regelnummer] = FUNCTIE;
//			if(regelnummer < maxAantalStelsels - 1)
//				soortVak[regelnummer + 1] = FUNCTIE;
//		}
//		else if(soortVak[regelnummer] != PARAMETRISATIEY)
//			soortVak[regelnummer] = FUNCTIE;
//		
//		//isOngelijkheid[regelnummer] = false;
//		if(grafiekComponent != null)
//		{	grafiekComponent.zetOngelijkheid(regelnummer, null, true, true, false);
//			grafiekComponent.zetFunctie(regelnummer, null, "$f@", null, DEFAULTDOMEIN, true, setState, docent);
//			grafiekComponent.zetVerticaleLijn(regelnummer, null);
//		}
//		
//		//Altijd tekst ook in formuleregel zetten, zodat geparste formule 'gelijk loopt' met wat er in de regel staat.
//		if(functieBeginAanpasbaar)
//		{	formuleVakken[regelnummer].formuleVak.vulVak(s);
//		}
//		else
//		{	try{
//			String[] splitString = StringUtils.split(s, "=");
//			formuleVakken[regelnummer].formuleVak.vulVak("$f" + splitString[1] + "@");
//			}
//			catch(Exception e)
//			{ 
//				formuleVakken[regelnummer].formuleVak.vulVak(s);
//			}
//		}
//		
//		try
//		{	s = s.substring(2,s.length()-1);
//			if(s.length()==0)
//			{	return;
//			}
//			String[] vergTekens = {"=", ">", "<", "\u2264", "\u2265"};
//			int tekenGetal = 0;
//			String[] expressieStrings = null;
//			Expressie e1 = null; //nu nog niet gebruikt, maar dat komt nog wel bij impliciete functies
//			Expressie e2 = null;
//			
//			boolean split = false;
//		    for(int j=0 ; j<vergTekens.length && !split; j++)
//		    {	expressieStrings  = StringUtils.split(s,vergTekens[j]);
//		        if(expressieStrings.length==2)
//		    	{ 	
//		        	e1 = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + expressieStrings[0] + "@")));
//	    			e2 = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + expressieStrings[1] + "@")));
//	    			
//	    			if(expressieStrings[0] == null || expressieStrings[1] == null) 
//			    	{	split = false;
//			    	}
//			    	else 
//			    	{	split = true;
//			    		tekenGetal = j;
//			    	}
//	    			break;
//		    	}
//			}
//		    
//		    if(!split)
//		    {	return;
//		    }
//		    while(expressieStrings[0].endsWith(" "))
//				expressieStrings[0] = expressieStrings[0].substring(0, expressieStrings[0].length() - 1);
//		    //if(!functieBeginAanpasbaar && expressieStrings.length == 2)
//		    	//vulFunctieRegel(expressieStrings[0], expressieStrings[1], vergTekens[tekenGetal], regelnummer);
//		    if(!functieBeginAanpasbaar && expressieStrings.length == 2)
//		    {	vulFunctieRegel(expressieStrings[0], expressieStrings[1], regelnummer);
//		    }
//		   		    
//		    /* Volgens mij niet nodig: 
//		    if(expressieStrings[0] == null || expressieStrings[1] == null)
//		    {	return;
//		    }
//		    */
//		    if(tekenGetal > 0 && !ongelijkheidToegestaan) // geval ongelijkheid
//		    {	formuleVakken[regelnummer].formuleVak.vulVak("$f@");
//		    	return;
//		    }
//		    else if(tekenGetal > 0)	
//		    {	if(expressieStrings[0].equals(xAsNaam))
//		    	{	boolean isGroterGelijk = true;
//		    		if(tekenGetal == 2 || tekenGetal == 3)
//		    			isGroterGelijk = false;
////		    		if(checkboxen[regelnummer].isSelected())
////		    			grafiekComponent.zetOngelijkheid(regelnummer, e2, false, isGroterGelijk, isEn[regelnummer]); 
//		    		soortVak[regelnummer] = ONGELIJKHEID;
//		    		//isOngelijkheid[regelnummer] = true;
//		    	}
//		    	else if(expressieStrings[0].equals(yAsNaam))
//		    	{	boolean isGroterGelijk = true;
//	    			if(tekenGetal == 2 || tekenGetal == 3)
//	    				isGroterGelijk = false;
////	    			if(checkboxen[regelnummer].isSelected())
////	    				grafiekComponent.zetOngelijkheid(regelnummer, e2, true, isGroterGelijk, isEn[regelnummer]); 
//	    			soortVak[regelnummer] = ONGELIJKHEID;
//	    			//isOngelijkheid[regelnummer] = true;
//		    	}
//		    }//let op: neemt nu ook uitdrukkingen als sin(x) mee. Zorgen dat dat soort uitdrukkingen (impliciete functies) 
//		    //er voor deze tijd al uitgefilterd zijn.
//		    else if(expressieStrings[0].equals(yAsNaam) || expressieStrings[0].endsWith("(" + xAsNaam + ")"))
//		    {	if(!functieToegestaan)
//		    	{	formuleVakken[regelnummer].formuleVak.vulVak("$f@");
//		    		return;
//		    	}
////		    	else
////			    {	if(checkboxen[regelnummer].isSelected() || docent)
////			    	{	grafiekComponent.zetFunctie(regelnummer, e2, "$f" + expressieStrings[1] +"@", expressieStrings[0], domeinen[regelnummer], true, setState, docent);
////			    		domeinButtons[regelnummer].setVisible(domeinInstelbaar);
////			    	}
////			    } 
//		    }
//		    else if(expressieStrings[0].equals(xAsNaam))
//		    {	if(!verticaleLijnToegestaan)
//		    	{	formuleVakken[regelnummer].formuleVak.vulVak("$f@");
//		    		return;
//		    	}
//		    	if(checkboxen[regelnummer].isSelected())
//		    		grafiekComponent.zetVerticaleLijn(regelnummer, e2);
//		    }
//		    else if(expressieStrings[0].startsWith(xAsNaam + "("))
//		    {	if(!parametrisatieToegestaan)
//		    	{	formuleVakken[regelnummer].formuleVak.vulVak("$f@");
//		    		return;
//		    	}
//		    	if(soortVak[regelnummer] != PARAMETRISATIEX && soortVak[regelnummer] != PARAMETRISATIEY)
//		    	{	soortVak[regelnummer] = PARAMETRISATIEX;
//		    		if(regelnummer < maxAantalStelsels - 1)
//		    		{	soortVak[regelnummer + 1] = PARAMETRISATIEY;
////		    			maakParametrisatieVak(regelnummer);
//		    		}
//		    		//layoutVakken(setState);
//		    	}
//		    	if(checkboxen[regelnummer].isSelected())
//		    	{	String variabele = "";
//		    		try{
//		    			variabele = expressieStrings[0].substring(expressieStrings[0].indexOf("(") + 1, expressieStrings[0].indexOf(")"));
//		    		}
//		    		catch(Exception e){}
//		    		grafiekComponent.zetParametrisatie(regelnummer, e2, variabele, true);
//		    	}
//		    	//maar wat moet er gebeuren/hoe moet dat eruit zien met extra regel voor y?? Ik wil het liefst dat dit in dezelfde
//		    	//formuleregel gebeurt. Andere optie is dat het wel in de volgende regel gebeurt; dan neem ik ze samen. 
//		    	//Misschien is het een idee om meer dan 9 regels mogelijk te maken, die grens is vrij willekeurig. 
//		    	//Wat gebeurt er bijvoorbeeld als ik die naar 20 leg?
//		    	//Heb ik een PARAMETRISATIEX en PARAMETRISATIEY nodig?
//		    	
//		    }
//		    else if(expressieStrings[0].startsWith(yAsNaam + "("))
//		    {
//		    	if(!parametrisatieToegestaan || soortVak[regelnummer] != PARAMETRISATIEY)
//		    	{
//		    		formuleVakken[regelnummer].formuleVak.vulVak("$F@");
//		    		return;
//		    	}
//		    	if(checkboxen[regelnummer].isSelected())
//		    	{	String variabele = "";
//		    		try{
//		    			variabele = expressieStrings[0].substring(expressieStrings[0].indexOf("(")+1, expressieStrings[0].indexOf(")"));
//		    		}
//		    		catch(Exception e){}
//		    		grafiekComponent.zetParametrisatie(regelnummer, e2, variabele, false);
//	    	
//		    		
//		    	}
//		    }
//		    else
//		    	formuleVakken[regelnummer].formuleVak.vulVak("$f@");
//		}
//		catch(Exception e)
//		{}
////		zetEnOfKnoppen();
//	}
	
////	public void maakParametrisatieVak(int regelnummer)
////	{
////		int yPositie = formuleVakken[regelnummer].getY();
////		checkboxen[regelnummer].setLocation(4, yPositie + formuleVakken[regelnummer].getSize().height);
////		if(regelnummer < maxAantalFormules - 1)
////		{	AccoladeLabel accoladeLabel = new AccoladeLabel(formuleVakken[regelnummer].getSize().height + 10  + formuleVakken[regelnummer + 1].getSize().height);
////			accoladeLabel.setLocation(15, yPositie);
////			add(accoladeLabel);
////		}
//		
//		//checken of alles al in parametrisatiestand staat. Anders daarvoor zorgen. Maar in principe moet ook alles uit parametrisatiestand
//				//aan begin van parsen... Dus moet dit altijd gebeuren. Bij het uit parametrisatiestand halen goed opletten dat je de formule wel netjes bewaart.
//				//laat de regel ook maar zichtbaar.
//				//accolade neerzetten
//				//volgende regel zichtbaar maken (als die niet al zichtbaar is).
//				//checkboxen weghalen of onzichtbaar maken (waarschijnlijk is dat laatste handiger)
//				//nieuwe checkbox terugzetten midden voor de accolade.
//				//yasnaam(variabele) klaarzetten op volgende regel, als die niet al een parametrisatie bevatte.
//				//Uberhaubt: volgende regel ook meteen zichtbaar maken.
//	}
	
	
	
//	public void maakDifferentiaalVak(int stelselNummer)
//	{
//		if(stelselNummer > maxAantalStelsels - 1)
//			return;
//		
//		soortVak[stelselNummer*2 + 1] = DIFFERENTIAALY; // alsie dat nog niet was dan issie dat nu
//		int yPositie = formuleVakken[stelselNummer*2].getY();
//
//		checkboxen[stelselNummer].setLocation(4, yPositie + formuleVakken[stelselNummer*2].getSize().height-2);
//		AccoladeLabel accoladeLabel = new AccoladeLabel(formuleVakken[stelselNummer*2].getSize().height + 10  + formuleVakken[stelselNummer*2 + 1].getSize().height);
//		accoladeLabel.setLocation(cAccoladeXPositie, yPositie);
//		add(accoladeLabel);
//
//	}
	
	
//	public void zetEnOfKnoppen()
//	{	for(int i = 0; i < maxAantalFormules - 1; i++)
//		{	if(i < enOfKnoppen.length)
//			{
//				if(soortVak[i] == ONGELIJKHEID && soortVak[i+1] == ONGELIJKHEID)
//					enOfKnoppen[i].setVisible(true);
//				else
//					enOfKnoppen[i].setVisible(false);
//			}
//		}
//		
//	}
	
//	public void vulFunctieRegel(String deel1, String deel2, int regelnummer)
//	{	//if(functieBeginAanpasbaar)
//		//	formuleVakken[regelnummer].functieBeginVak.vulVak("$f" + deel1 + vergelijkingsTeken + deel2 + "@");
//		formuleVakken[regelnummer].functieBeginVak.vulVak("$f" + deel1 + "=@");
//		formuleVakken[regelnummer].formuleVak.vulVak("$f" + deel2 + "@");
//	}
	
//	public void maakNieuweRegel()
//	{
//		parseFormule(aantalStelsels - 1, false);
//		add(formuleVakken[aantalStelsels],0);
//		zetVoorvoegsel(aantalStelsels);	
//		if(docent || (grafiekComponent == null || grafiekComponent.typeOpdracht == GraphToolInteractiePanel.GEENOPDRACHT))
//			add(checkboxen[aantalStelsels],0);
//		else
//			checkboxen[aantalStelsels].setSelected(true);
////		add(domeinButtons[aantalRegels],0);
////		domeinButtons[aantalRegels].setVisible(false);
////		add(enOfKnoppen[aantalRegels - 1], 0);
////		enOfKnoppen[aantalRegels - 1].setVisible(false);
//		layoutVakken(false);
//		formuleVakken[aantalStelsels].formuleVak.requestFocus();
//		aantalStelsels++;
//		produceAction("regel meer");
//	}
	
//	public void zetVergelijking(int regelNr, String vergelijkingString)
//	{
//		//zetFunctieBeginAanpasbaar(false, false);
//		//zetFormeleFuncties(false, false);
//		VergelijkingMeerv v = FormuleParser.parseVergelijking(vergelijkingString);
//		String functieString0 = "$f"+v.geefVergelijking(0).geefExpLinks().toString()+"@";
//		if(functieBeginAanpasbaar)
//			functieString0 = "$fy=" + functieString0.substring(2);
//		formuleVakken[0].formuleVak.vulVak(functieString0);
//		if(aantalRegels<2) maakNieuweRegel();
//		String functieString1 = "$f"+v.geefVergelijking(0).geefExpRechts().toString()+"@";
//		if(functieBeginAanpasbaar)
//			functieString1 = "$fy=" + functieString1.substring(2);
//		formuleVakken[1].formuleVak.vulVak(functieString1);
//		checkboxen[0].setSelected(true);
//		checkboxen[1].setSelected(true);
//		parseFormule(0, false);
//		parseFormule(1, false);
//	}
	
//	public void zetFunctie(int regelNr, String functieString)
//	{
//		if(functieBeginAanpasbaar)
//			functieString = "$fy=" + functieString.substring(2);
//		if(regelNr==0) 
//			formuleVakken[0].formuleVak.vulVak(functieString);
//		while(aantalRegels-1<regelNr) 
//			maakNieuweRegel();
//		formuleVakken[regelNr].formuleVak.vulVak(functieString);
//		checkboxen[regelNr].setSelected(true);
//		parseFormule(regelNr, false);
//	}
//	
//	public void zetFuncties(Map map)
//	{
//		String numberString = (String)map.get("number");
//		int number = 0;
//		try	{	
//			number = Integer.parseInt(numberString);
//		}
//		catch (NumberFormatException nfe) {
//			System.out.println(nfe.toString());
//		}
//		String clear = (String)map.get("clear");
//		String abscissa_name = (String)map.get("abscissa_name");
//		String abscissa_min = (String)map.get("abscissa_min");
//		String abscissa_max = (String)map.get("abscissa_max");
//		String ordinate_name = (String)map.get("ordinate_name");
//		String ordinate_min = (String)map.get("ordinate_min");
//		String ordinate_max = (String)map.get("ordinate_max");
//		
//		zetXAsNaam(abscissa_name,false);
//		zetYAsNaam(ordinate_name,false);
//		
//		//Expressie[] functions = new Expressie[number];
//		//Color[] colors = null;
//		//double[] thicknesses = null;
//		
//		for(int i=0 ; i<number ; i++)
//		{
//			String functionString = (String)map.get("function_"+i);
//			functionString = functionString.replaceAll("root", "sqrt");
//			functionString = functionString.replaceAll("$", "");
//
//			functionString = "$"+yAsNaam+"=" + functionString.substring(2);
//			formuleVakken[i].formuleVak.vulVak(functionString);
//			checkboxen[i].setSelected(true);
//			parseFormule(i, false);
//			if(i<number-1)
//				maakNieuweRegel();
//			//functions[i] = popcornParse(functionString);
//			//String colorString = (String)map.get("color_"+i);
//			//colors[i] = colorParse(colorString);
//			//String thicknessString = (String)map.get("thickness_"+i);
//			//try	{	
//			//	thicknesses[i] = Double.parseDouble(thicknessString);
//			//}
//			//catch (NumberFormatException nfe) {
//			//	System.out.println(nfe.toString());
//			//}
//			
//		}
//	}
	
//	public Expressie popcornParse(String s)
//	{	Expressie e = null;
//		s = s.replaceAll("root", "sqrt");
//		s = s.replaceAll("$", "");
//		e = FormuleParser.parse("$f"+s+"@");
//		return e;
//	}
	
//	public Color colorParse(String s)
//	{
//		Color c = null;
//		
//		return c;
//	}
	
//	public void actionPerformed(ActionEvent e) {

//		if (e.getSource() == nieuweRegelKnop && aantalRegels < maxAantalFormules)
//		{	maakNieuweRegel();
//			return;
//		}
//		else if (e.getSource() == nieuweRegelKnop)
//			return;
//		else if (e.getSource() == verwijderRegelKnop && aantalRegels > 1)
//		{	
//			parseFormule("$f@", aantalRegels - 1, false);
//			//formuleVakken[aantalRegels - 1].formuleVak.vulVak("$f@");
//			if(functieBeginZichtbaar)
//			{	zetVoorvoegsel(aantalRegels - 1);
//			}
//			//else
//			//	formuleVakken[aantalRegels - 1].formuleVak.vulVak("$f@");
//			remove(formuleVakken[aantalRegels-1]);
//			remove(checkboxen[aantalRegels-1]);
//			if(!docent && grafiekComponent != null && grafiekComponent.typeOpdracht != GraphToolInteractiePanel.GEENOPDRACHT)
//				checkboxen[aantalRegels-1].setSelected(false);
//			remove(domeinButtons[aantalRegels-1]);
//			remove(enOfKnoppen[aantalRegels-2]);
//			isEn[aantalRegels - 2] = true;
//			enOfKnoppen[aantalRegels-2].setText(GraphTool.rb.getString("enOfButton_En"));
//			layoutVakken(false);
//			aantalRegels--;
//			produceAction("regel minder");
//			return;
//		}
		
//		for(int i=0; i<maxAantalStelsels; i++) {
//			for (int j=0; j<cAantalFormulesPerStelsel; j++) {
//				if(e.getSource()==formuleVakken[(i*cAantalFormulesPerStelsel)+j].formuleVak &&  (e.getActionCommand().equals("ingevuld") || 
//						e.getActionCommand().equals("focuslost") || e.getActionCommand().equals("zetmaat"))) {				
//					if(checkboxen[i].isSelected()) {	
//						parseFormule((i*cAantalFormulesPerStelsel)+j, false);
//						layoutVakken(false);
//						produceAction("ingevuld");
//					}
//					break;
//				}
//			}
//			
//		}
//		for(int i=0 ; i<maxAantalStelsels ; i++) {	
//			for (int j=0; j<cAantalFormulesPerStelsel; j++) {
//				if(e.getSource()==formuleVakken[(i*cAantalFormulesPerStelsel)+j].formuleVak && 
//						e.getActionCommand().equals("focus")) {	
//					if(formuleVak != formuleVakken[(i*cAantalFormulesPerStelsel)+j].formuleVak) {	
//						formuleVak.deSelect();
//						parseFormule(actiefNummer, false);
//					}
//
//					actiefNummer = (i*cAantalFormulesPerStelsel)+j;
//					formuleVak = formuleVakken[(i*cAantalFormulesPerStelsel)+j].formuleVak;
//					
//					break;
//				}
//			}
//			
//		}
//		for(int i = 0; i<maxAantalFormules; i++)
//		{	if(e.getSource()==enOfKnoppen[i])
//			{	isEn[i] = !isEn[i];
//				if(isEn[i])
//					enOfKnoppen[i].setText(GraphTool.rb.getString("enOfButton_En"));
//				else			
//					enOfKnoppen[i].setText(GraphTool.rb.getString("enOfButton_Of"));
//				parseFormule(i, false);
//				break;
//			}
//			
//		}
//		if (checkboxen != null ) { 
//			for(int i=0 ; i<maxAantalStelsels ; i++) {	
//
//				if(e.getSource()==checkboxen[i]) {	
//					parseFormule(i*cAantalFormulesPerStelsel, false);
//					parseFormule((i*cAantalFormulesPerStelsel)+1, false);
//					if(checkboxen[i].isSelected())
//					{	produceAction("ingevuld");
//					}
//					else 
//					{	produceAction("verwijderd");
//					}
//					if(checkboxen[actiefNummer].isSelected()) 
//					{	parseFormule(actiefNummer, false);
//						produceAction("ingevuld");	
//					}	
//					break;
//				}
//			}
//		}
////	for(int i=0 ; i<maxAantalFormules ; i++)
////		{	
////		
////		if(e.getSource()==domeinButtons[i] && e.getActionCommand().equals("maak Domein"))
////			{	domeinStrings[i][0] = domeinButtons[i].getDomeinString()[0];
////				domeinStrings[i][1] = domeinButtons[i].getDomeinString()[1];
////				zetDomein(domeinStrings[i], i);
////				parseFormule(i, false);
////				
////				produceAction("ingevuld");
////			}
////		}
//		super.actionPerformed(e);
//	}
	
//	public void mousePressed(MouseEvent e) {	
//		
//		if (checkboxen != null) {
//			for(int i = 0; i < aantalStelsels; i++) {
//				if(e.getSource().equals(checkboxen[i]) && (e.getModifiers() & e.BUTTON1_MASK) == 0) {
//					Color kleur = JColorChooser.showDialog(this, GraphTool.rb.getString("kleurKiezer"), grafiekComponent.getFormuleColor(i));//new Color(255,255,180));
//					grafiekComponent.setColor(i,  kleur, false);
//					zetGrafiekKleuren();
//				}
//			}
//		}
//		
//		for(int i = 0; i < aantalStelsels; i++) {	
//			for (int j=0; j < cAantalFormulesPerStelsel; j++) {
//				int yMin = formuleVakken[(i*cAantalFormulesPerStelsel) + j].getLocation().y;
//				int yMax = formuleVakken[(i*cAantalFormulesPerStelsel) + j].getLocation().y + 
//							formuleVakken[(i*cAantalFormulesPerStelsel) + j].getSize().height+10;
//				if(e.getY() > yMin && e.getY() < yMax) {	
//					formuleVak.deSelect();
//					parseFormule(actiefNummer, false);
//					if(checkboxen[((int) actiefNummer/cAantalFormulesPerStelsel)].isSelected()) {	
//						produceAction("ingevuld");		
//					}	
//					actiefNummer = (i*cAantalFormulesPerStelsel) + j;
//					formuleVak = formuleVakken[(i*cAantalFormulesPerStelsel) + j].formuleVak;
//					formuleVak.requestFocus();
//					formuleVak.zetOpEind();
//					break;
//				}
//			}
//		}
//	}
	
//	public void focusGained(FocusEvent e) {
//	}
	
//	public void focusLost(FocusEvent e) {   
//		parseFormule(actiefNummer, false);
//		
//		if(checkboxen[(int) actiefNummer / cAantalFormulesPerStelsel].isSelected())
//		{	produceAction("ingevuld");		
//		}
////		produceAction("focusLost");
//	}
	
//	//ActionProducer
//	private ActionListener actionListener = null;
//	
//	public void addActionListener(ActionListener l) 
// 	{	actionListener = AWTEventMulticaster.add(actionListener,l);
// 	}
// 	
// 	public void removeActionListener(ActionListener l)
// 	{	actionListener = AWTEventMulticaster.remove(actionListener, l);
// 	}	
// 	
// 	public void produceAction(String command)
// 	{	if (actionListener != null)
// 		{	actionListener.actionPerformed( new ActionEvent(this, 0, command) );
// 		}
// 	}
// 	//end ActionProducer
//	
// 	class AccoladeLabel extends JLabel
// 	{
// 		int hoogte;
// 		
// 		public AccoladeLabel(int height)
// 		{
// 			super();
// 			hoogte = height;
// 			setSize(7, hoogte);
// 		}
// 		
// 		public void paintComponent(Graphics g)
// 		{
// 			g.setColor(Color.DARK_GRAY);
// 			g.drawLine(4, 1, 5, 1);
// 			g.drawLine(3, 2, 3, (hoogte - 1)/2 - 1);
// 			g.drawLine(3, (hoogte - 1)/2 - 1, 1, (hoogte - 1)/2 + 1);
// 			g.drawLine(1, (hoogte - 1)/2 + 1, 3, (hoogte - 1)/2 + 3);
// 			g.drawLine(3, (hoogte - 1)/2 + 3, 3, hoogte - 2);
// 			g.drawLine(4, hoogte - 1, 5, hoogte - 1);
// 		}
// 	}
}
