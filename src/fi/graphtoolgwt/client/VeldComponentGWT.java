package fi.graphtoolgwt.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.HandlesAllFocusEvents;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Expressie;

public class VeldComponentGWT extends LayoutPanel { 
	
	private static Logger logger = Logger.getLogger("VeldComponentGWT");
	
	public enum FieldGraphType {QUIVER, STREAMLINE};
	public enum FieldGraphArrowSizeMode { REALVALUE, FIXEDSIZE, SCALEDSIZE }	
	
	/* component defaults */
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
	final static CssColor cSystemColor = CssColor.make(150, 150, 150); // Grey is the basic color
	
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
	ScrollPanel scrollPanel;
	
	SystemDiffEqPanelGWT systems[] = new SystemDiffEqPanelGWT[maxAantalStelsels];
	
	private final GraphToolGWT interactiePanel;
	
	private String xAsNaam = "x";
	private String yAsNaam = "y";
	
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
	
//	@override
//	void paint
	
	public void parseFunction(int stelselId, int functionId, String functionStr) {
		interactiePanel.zetVectorVeld(stelselId, functionId, 
				FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f"+functionStr+"@"))));
		resize();
	}
	
	public VeldComponentGWT(GraphToolGWT interactiePanel, Map<String, Object> launchData, int breedte , int hoogte) {
		
		this.interactiePanel = interactiePanel;
		this.veldComponentBreedte = breedte;
		this.veldComponentHoogte = hoogte;
		processLaunchData(launchData);
		
		docent = false;
		
//		regelsPanel = new LayoutPanel();
//		//contentPanel.addStyleName(graphToolCss.backgroundred());
//		//contentPanel.getElement().getStyle().setPadding(5, Unit.PX);
//		//contentPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
//		//contentPanel.getElement().getStyle().setProperty("display", "block");
//		sp = new ScrollPanel(regelsPanel);
//		sp.setWidget(regelsPanel);
		
		LayoutPanel mainPanel = new LayoutPanel();
		this.add(mainPanel);
		this.setWidgetLeftWidth(mainPanel, 0, Style.Unit.PX, veldComponentBreedte, Style.Unit.PX);
		this.setWidgetTopHeight(mainPanel, 0, Style.Unit.PX, veldComponentHoogte, Style.Unit.PX); 
		FlowPanel rechthoekPanel = new FlowPanel();
		rechthoekPanel.getElement().getStyle().setBorderColor( cVeldComponentGWT_borderColor.toString() );
		rechthoekPanel.getElement().getStyle().setBorderStyle( cVeldComponentGWT_borderStyle);  
		rechthoekPanel.getElement().getStyle().setBorderWidth( cVeldComponentGWT_borderWidthPix, Style.Unit.PX);
//		mainPanel.add(rechthoekPanel);
//		mainPanel.setWidgetLeftWidth(rechthoekPanel, cVeldComponentGWT_mainWidgetBorderMargin, Style.Unit.PX, 
//				veldComponentBreedte-cVeldComponentGWT_mainWidgetBorderMargin, Style.Unit.PX);
//		mainPanel.setWidgetTopHeight(rechthoekPanel, cVeldComponentGWT_mainWidgetBorderMargin, Style.Unit.PX, 
//				veldComponentHoogte-cVeldComponentGWT_mainWidgetBorderMargin, Style.Unit.PX);
		
		stelselsPanel = new LayoutPanel();
		scrollPanel = new ScrollPanel(stelselsPanel);
		scrollPanel.setWidget(stelselsPanel);
		
		mainPanel.add(scrollPanel);
		mainPanel.setWidgetLeftWidth(scrollPanel, 0, Style.Unit.PX, 
				veldComponentBreedte , Style.Unit.PX);
//		mainPanel.setWidgetLeftWidth(scrollPanel, cVeldComponentGWT_scrollWidgetBorderMargin, Style.Unit.PX, 
//				veldComponentBreedte-2*cVeldComponentGWT_scrollWidgetBorderMargin , Style.Unit.PX);
		mainPanel.setWidgetTopHeight(scrollPanel, cVeldComponentGWT_toolbarHeight+cVeldComponentGWT_scrollWidgetBorderMargin, Style.Unit.PX, 
				veldComponentHoogte - cVeldComponentGWT_toolbarHeight - 2* cVeldComponentGWT_scrollWidgetBorderMargin, Style.Unit.PX);
		
		String[] asNamen = new String[2];
		asNamen[0] = xAsNaam;
		asNamen[1] = yAsNaam;
		for (int i=0; i < aantalStelsels; i++) {
			systems[i] = new SystemDiffEqPanelGWT(this, i, cAantalFormulesPerStelsel, veldComponentBreedte-cVeldComponentGWT_widgetScrollMargin);
			systems[i].updateFunctionBegin(asNamen, cVeldComponentGWT_diffVarNamen[0]);
		}

		stelselsPanel.add(systems[0]);
		stelselsPanel.setWidgetLeftWidth(systems[0], 0, Style.Unit.PX, veldComponentBreedte-cVeldComponentGWT_widgetScrollMargin, Style.Unit.PX);
		stelselsPanel.setWidgetTopHeight(systems[0], 0, Style.Unit.PX, 
				systems[0].getSystemHeight()  /* 2* cVeldComponentGWT_scrollWidgetBorderMargin,*/, Style.Unit.PX);
		resize();
	}
	
//	public int berekenRegelHoogte(int aantalRegels)
//	{
//		int y = 0;
//		for(int i = 0; i < aantalRegels; i++)
//		{
////			y += Math.max(30, editors[i].getHeight()) + 5;
//		}
//		return y;
//	}

	
	public void resize() {
		for (int i=0; i <aantalStelsels; i++) {
			systems[i].adjustSize();
		}
		stelselsPanel.setWidgetTopHeight(systems[0], 0, Style.Unit.PX, 
				systems[0].getSystemHeight() , Style.Unit.PX);
	}
	
	public CssColor getSystemColor(int id) {
		return cSystemColor;
	}
	
	public class SystemDiffEqPanelGWT extends LayoutPanel { 
		
		private final int cSystemDiffEqPanelGWT_interObjectMarginX = 5; // horizontal margin between all object-borders
		private final int cSystemDiffEqPanelGWT_interObjectMarginY = 10; // vertical margin between all object-borders
		
		private final int cSystemDiffEqPanelGWT_checkBoxSize = 15;
		private final int cSystemDiffEqPanelGWT_braceWidth = 20;
		
//		private final int ccSystemDiffEqPanelGWT_rowStartX = 35;
		
		CssColor systemColor = VeldComponentGWT.cSystemColor;
		private Object parent;
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
		private DiffEqFunctionEditor[] functionEditors;
		private Canvas braceCanvas;
		CheckBox cb = new CheckBox();
		
		int width = 1;		
		
		public SystemDiffEqPanelGWT(Object parent, int id, int nrFunctions, int systemWidth) {
			super();
			
			this.parent = parent;
			this.id = id;
			this.nrFunctions = nrFunctions;
			this.systemWidth = systemWidth;

			functionsBegin = new String[nrFunctions];
			functionPanels = new LayoutPanel[nrFunctions];
			functionEditorPanels = new TouchPanel[nrFunctions];
			functionBeginViewers = new FormuleViewer[nrFunctions];
			functionEditors = new DiffEqFunctionEditor[nrFunctions];
			braceCanvas = Canvas.createIfSupported();
			
			for (int i=0; i < nrFunctions; i++) {
				functionPanels[i] = new LayoutPanel();
				layoutRegelPanel(functionPanels[i]);
				highLight(functionPanels[i], true);
				
				if(!functionBeginUserChangable) {
					functionBeginViewers[i] = new FormuleViewer("$f$bdAs" + (i+1) + "$nd" + "Der" + "@@=@");
					functionPanels[i].add(functionBeginViewers[i].getAsPanel());
				}

				functionEditors[i] = new DiffEqFunctionEditor(id, i, ((VeldComponentGWT) parent), this);
				
				functionEditors[i].setColor(systemColor);
				functionEditors[i].setFont(defaultfont);
				functionEditors[i].setDefaultFont(defaultfont);

				functionEditorPanels[i] = (TouchPanel) functionEditors[i].getAsPanel();
				addFormuleEditorListener(functionEditorPanels[i], functionEditors[i]);
				functionEditorPanels[i].getElement().getStyle().setProperty("display", "inline-block");
				
				functionEditors[i].setCurrentElementRepaint();
				
				functionPanels[i].add(functionEditorPanels[i]);				
				this.add(functionPanels[i]);				
			}
			
			this.add(cb);		
			cb.setVisible(true);
			cb.addClickHandler(new CheckBoxClickHandler());
			
			this.add(braceCanvas);
			
			adjustSize();
		}
		
		public void setWidth(int width) {
			this.width = width;
		}
		
		public void setSelected(boolean selected) {
			cb.setValue(selected);
		}
		
		public boolean getSelected() {
			return cb.getValue();
		}
		
		public int getSystemHeight() {
			if ( systemHeight < 1 ) {
				adjustSize();
			}
			return systemHeight;
		}
		
		class DiffEqFunctionEditor extends FormuleEditor {

			int systemID;
			int functionID;
			VeldComponentGWT veldComponentGWT;
			SystemDiffEqPanelGWT systemDiffEqPanel;
			DiffEqFunctionEditor(int systemID, int functionID, VeldComponentGWT veldComponentGWT, SystemDiffEqPanelGWT systemDiffEqPanel) {
				super();
				this.systemID = systemID;
				this.functionID = functionID;
				this.veldComponentGWT = veldComponentGWT;
				this.systemDiffEqPanel = systemDiffEqPanel;
				this.setFormuleToolBijFocus(true);
			}
			
			@Override
			public void enter() {
				updateFunction();
			}
			
			@Override
			public void resize() {
				super.resize();
				veldComponentGWT.resize();
			}
			
			public void updateFunction() {
				if (systemDiffEqPanel.cb.getValue()) {
					veldComponentGWT.parseFunction(systemID, functionID, this.toString());
					veldComponentGWT.interactiePanel.grafiekGWTVeld.paint();
				}
			}
			
			public String getFunction() {
				return (this.toString());
			}
		}
		
		private void addFormuleEditorListener(final TouchPanel tp, final DiffEqFunctionEditor editor ) {
			tp.addTouchHandler(new FormuleEditorTouchHandler(editor));
		}
		
		public void setFunction(int functionId, String functionStr) {
			if (functionId < nrFunctions) {
				functionEditors[functionId].clearAll();
				functionEditors[functionId].insert(functionStr);
				functionEditors[functionId].setCurrentElementRepaint();
			}
		}
		
		public String getFunction(int functionId) {
			if (functionId < nrFunctions) {
				return functionEditors[functionId].getFunction();
			} else {
				return null;
			}
		}
		
		public void updateFunctionBegin(String[] functionNames, String derivativeName) {
			if (functionNames.length == nrFunctions) {
				for (int i = 0; i < nrFunctions; i++) {
					functionsBegin[i] = "$f$bd" + functionNames[i] + "$nd" + derivativeName + "@@=@";
					
					functionPanels[i].remove(functionBeginViewers[i].getAsPanel());
					functionBeginViewers[i] = new FormuleViewer(functionsBegin[i]);
					functionBeginViewers[i].setFont(defaultfont);
					functionBeginViewers[i].setDefaultFont(defaultfont);	
					functionBeginViewers[i].setColor(systemColor);
					functionPanels[i].add(functionBeginViewers[i].getAsPanel());
				}
			}
			adjustSize();
		}
		
		public void updateFunctions(String oldVar, String newVar) {
			for (int i=0; i < nrFunctions; i++) {
				setFunction(i,getFunction(i).replaceAll(oldVar, newVar));
			}
		}
		
		public void layoutRegelPanel(Widget w)
		{
//			w.getElement().getStyle().setWidth(veldComponentBreedte - 5, Unit.PX);
			w.getElement().getStyle().setFloat(Float.LEFT);
			w.getElement().getStyle().setProperty("clear", "both");
			w.getElement().getStyle().setProperty("display", "block");
//			w.getElement().getStyle().setBackgroundColor(cRegelHighlightColor.toString());
		}
		
		public void highLight(Widget w, boolean b)
		{
//			w.getElement().getStyle().setBackgroundColor(b ? cRegelHighlightColor.toString() : cRegelBackgroundColor.toString());
		}
		
		private void adjustSize() {
			int[] rowHeight = new int[2];
			int systemHalfHeight = 0;
			double cBorderMargin = 5.0;

            logger.info("komt in adjustSize :: "+ this.systemHeight);
			// clear old brace canvas
			Context2d ctx = braceCanvas.getContext2d();
			ctx.clearRect(0, 0, cSystemDiffEqPanelGWT_braceWidth, this.systemHeight);

			// Determine RowHeigth & system Height
			int maxBeginWidth = 0;
			this.systemHeight = cSystemDiffEqPanelGWT_interObjectMarginY + (int) cBorderMargin;
			for (int i=0; i<nrFunctions; i++) {
				rowHeight[i]=Math.max(functionBeginViewers[i].getHeight(), functionEditors[i].getHeight());
				maxBeginWidth = Math.max(maxBeginWidth,functionBeginViewers[i].getWidth());
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

				this.systemHeight += cSystemDiffEqPanelGWT_interObjectMarginY + (double) cBorderMargin;
			}			
		
				
			// adjust Row Positions
			double posY = cSystemDiffEqPanelGWT_interObjectMarginY+cBorderMargin;
			for (int i=0; i<nrFunctions; i++) {

				// Position Entire Row
				int rowStartX = 3 * cSystemDiffEqPanelGWT_interObjectMarginX + cSystemDiffEqPanelGWT_checkBoxSize + cSystemDiffEqPanelGWT_braceWidth;
				this.setWidgetTopHeight(functionPanels[i], posY, Style.Unit.PX, rowHeight[i], Style.Unit.PX);
				this.setWidgetLeftWidth(functionPanels[i], rowStartX, Style.Unit.PX, systemWidth-rowStartX, Style.Unit.PX);
				posY += rowHeight[i] + cSystemDiffEqPanelGWT_interObjectMarginY;
				
				// Position Begin viewer within Row
				functionPanels[i].setWidgetTopHeight(functionBeginViewers[i].getAsPanel(), (rowHeight[i]-functionBeginViewers[i].getHeight())/2, 
						Style.Unit.PX, functionBeginViewers[i].getHeight(), Style.Unit.PX);
				functionPanels[i].setWidgetLeftWidth(functionBeginViewers[i].getAsPanel(), 0, Style.Unit.PX, 
						maxBeginWidth, Style.Unit.PX);
				
				// Position Function editor within Row
				functionPanels[i].setWidgetTopHeight(functionEditorPanels[i], (rowHeight[i]-functionEditors[i].getHeight())/2, 
						Style.Unit.PX, functionEditors[i].getHeight(), Style.Unit.PX);
				functionPanels[i].setWidgetLeftWidth(functionEditorPanels[i], maxBeginWidth+cSystemDiffEqPanelGWT_interObjectMarginX, Style.Unit.PX, 
						systemWidth-rowStartX-cSystemDiffEqPanelGWT_interObjectMarginX-maxBeginWidth, Style.Unit.PX);
				
				functionEditors[i].setFont(defaultfont);
				functionEditors[i].setDefaultFont(defaultfont);	
				functionEditors[i].setColor(systemColor);
				functionEditorPanels[i].add(functionEditors[i].getAsPanel());
			}				
			
			// adjust Checkbox Position
			this.setWidgetLeftWidth(cb, cSystemDiffEqPanelGWT_interObjectMarginX, Style.Unit.PX, cSystemDiffEqPanelGWT_checkBoxSize, Style.Unit.PX);
			this.setWidgetTopHeight(cb, systemHalfHeight-cSystemDiffEqPanelGWT_checkBoxSize/2+cBorderMargin, Style.Unit.PX, cSystemDiffEqPanelGWT_checkBoxSize, Style.Unit.PX);
				
			// adjust brace Canvas Position
			this.setWidgetLeftWidth(braceCanvas, 2* cSystemDiffEqPanelGWT_interObjectMarginX + cSystemDiffEqPanelGWT_checkBoxSize, 
					Style.Unit.PX, cSystemDiffEqPanelGWT_braceWidth, Style.Unit.PX);
			this.setWidgetTopHeight(braceCanvas, 0, Style.Unit.PX, systemHeight+2*cSystemDiffEqPanelGWT_braceWidth+2*cBorderMargin, Style.Unit.PX);
			
			// redraw brace
//			ctx.setStrokeStyle(systemColor.toString());
			ctx.setFillStyle(systemColor);
//			gIm.setStrokeStyle(VeldComponentGWT.cSystemColor);
			ctx.setLineWidth(0.25d);

			ctx.beginPath();
			ctx.arc(cSystemDiffEqPanelGWT_braceWidth, cSystemDiffEqPanelGWT_braceWidth/2.0+(double) cBorderMargin, 
					cSystemDiffEqPanelGWT_braceWidth/2.0, 1.0*Math.PI, 1.5*Math.PI, false);
			
			ctx.moveTo(cSystemDiffEqPanelGWT_braceWidth/2.0, cSystemDiffEqPanelGWT_braceWidth/2.0+(double) cBorderMargin);
			ctx.lineTo(cSystemDiffEqPanelGWT_braceWidth/2.0, systemHeight/2.0-cSystemDiffEqPanelGWT_braceWidth/2.0+(double) cBorderMargin);
			logger.info("Line 1 :: From " + 
					(cSystemDiffEqPanelGWT_braceWidth/2.0+(double) cBorderMargin) + " to " + 
					(systemHeight/2.0-cSystemDiffEqPanelGWT_braceWidth/2.0+(double) cBorderMargin)
					);
			
			ctx.arc(0.0, systemHalfHeight-cSystemDiffEqPanelGWT_braceWidth/2.0+(double) cBorderMargin, 
					cSystemDiffEqPanelGWT_braceWidth/2.0, 0, 0.5*Math.PI, false);
			
			ctx.arc(0.0, systemHalfHeight+cSystemDiffEqPanelGWT_braceWidth/2.0+(double) cBorderMargin, 
					cSystemDiffEqPanelGWT_braceWidth/2.0, 1.5*Math.PI, 2.0*Math.PI, false);
			
//			ctx.moveTo(cSystemDiffEqPanelGWT_braceWidth/2.0, systemHeight/2+cSystemDiffEqPanelGWT_braceWidth/2.0);
			ctx.moveTo(cSystemDiffEqPanelGWT_braceWidth/2.0, systemHalfHeight+cSystemDiffEqPanelGWT_braceWidth/2.0+(double) cBorderMargin);
			ctx.lineTo(cSystemDiffEqPanelGWT_braceWidth/2.0, systemHeight-cSystemDiffEqPanelGWT_braceWidth/2.0- (double) cBorderMargin);
			logger.info("Line 2 :: From " + 
					(systemHeight/2+cSystemDiffEqPanelGWT_braceWidth/2.0) + " to " + 
					(systemHeight-cSystemDiffEqPanelGWT_braceWidth/2.0- (double) cBorderMargin)
					);
			logger.info("Line 2 update :: From " + (systemHalfHeight+cSystemDiffEqPanelGWT_braceWidth/2.0+(double) cBorderMargin));
			
			ctx.arc(cSystemDiffEqPanelGWT_braceWidth,  systemHeight-cSystemDiffEqPanelGWT_braceWidth/2.0- (double) cBorderMargin, 
					cSystemDiffEqPanelGWT_braceWidth/2.0, 1.0*Math.PI, 0.5*Math.PI, true);

			ctx.stroke();
            logger.info("Uit adjustSize :: "+ this.systemHeight);

		}
		
		public void setSystemColor(CssColor systemColor){
			this.systemColor = systemColor;
			this.adjustSize(); // also repaint			
		}
		
		
		class CheckBoxClickHandler implements ClickHandler {
			
//			public CheckBoxClickHandler(int i) {	
//				super();
//			}
			
			@Override
			public void onClick(ClickEvent event) {
				if ( cb.getValue() ) {
					for (int i=0; i<nrFunctions; i++) {
						((VeldComponentGWT) parent).parseFunction(id,i,functionEditors[i].toString());
					}
				} else {
					for (int i=0; i<nrFunctions; i++) {
						((VeldComponentGWT) parent).parseFunction(id,i,"");
					}
				}
				((VeldComponentGWT) parent).interactiePanel.grafiekGWTVeld.paint();
			}
		}
		
	}		
	
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
	
	public void setState(Map<String, Object> h) {	
		String[] veldGrafiekExpressieStrings = null;
		boolean[] veldGrafiekGeselecteerd = null;
    	if (h.get("veldGrafiekExpressieStrings") != null) { 
    		veldGrafiekExpressieStrings = JSONUtilities.toStringArray(h.get("veldGrafiekExpressieStrings"));
    	}
    	
	    if (h.get("veldGrafiekGeselecteerd") != null) { 
    		List<Object> geselecteerdList = JSONUtilities.toArrayList(h.get("veldGrafiekGeselecteerd"));
    		veldGrafiekGeselecteerd = new boolean[geselecteerdList.size()];
			for(int i = 0; i < geselecteerdList.size(); i++) {	
				veldGrafiekGeselecteerd[i] = (Boolean) geselecteerdList.get(i);
			}
   		}
    	if (veldGrafiekExpressieStrings==null) {	
    		return;
    	}
    	
		aantalStelsels = veldGrafiekExpressieStrings.length / cAantalFormulesPerStelsel;

		for (int i=0; i<aantalStelsels; i++) {
			systems[i].setSelected(veldGrafiekGeselecteerd[i]);
			
			// activeer/deactiveer Stelsel			
			for (int j=0; j< cAantalFormulesPerStelsel; j++) {

				// clear begin and end of formula (if existing)
 				if ( veldGrafiekExpressieStrings[i*cAantalFormulesPerStelsel+j].startsWith("$f") 
 						&& veldGrafiekExpressieStrings[i*cAantalFormulesPerStelsel+j].endsWith("@") ) {

 					veldGrafiekExpressieStrings[i*cAantalFormulesPerStelsel+j] = 
 							veldGrafiekExpressieStrings[i*cAantalFormulesPerStelsel+j].substring(2, 
 									veldGrafiekExpressieStrings[i*cAantalFormulesPerStelsel+j].length() - 1);
 					
 				}
 				
				// voeg formule toe aan stelsel (visueel)
				systems[i].setFunction(j,  veldGrafiekExpressieStrings[i*cAantalFormulesPerStelsel+j]);
				
				// verwerk formule ook in het interactiePanel
				if (systems[i].getSelected()) {
					parseFunction(i, j, veldGrafiekExpressieStrings[i*cAantalFormulesPerStelsel+j]);
				} else {
					parseFunction(i, j, "");
				}
			}
		}
    }
	
	public HashMap<String,Object> getState() {
			
		Object[] veldGrafiekExpressieStrings = null;
		Object[] veldGrafiekGeselecteerd = null;
		
		veldGrafiekExpressieStrings = new String[cMaxAantalStelsels*cAantalFormulesPerStelsel];
		veldGrafiekGeselecteerd = new Object[cMaxAantalStelsels];
		
		for (int i=0 ; i<cMaxAantalStelsels ; i++) {	
			for (int j=0; j<cAantalFormulesPerStelsel; j++) {
				veldGrafiekExpressieStrings[i*cAantalFormulesPerStelsel+j]=systems[i].getFunction(j);
			}
			veldGrafiekGeselecteerd[i] = systems[i].getSelected();
		}		
		
		HashMap<String,Object> h = new HashMap<String,Object>();
		h.put("veldGrafiekExpressieStrings", veldGrafiekExpressieStrings);
		h.put("veldGrafiekGeselecteerd", veldGrafiekGeselecteerd);
		return h;
	}
	
	public void updateAxisName(String oldName, String newName) {
		boolean change =false;
		if (xAsNaam.equals(oldName)) {
			change = true;
			xAsNaam = newName;
		} else {
			if (yAsNaam.equals(oldName)) {
				change = true;
			}
			yAsNaam = newName;
		}
		String[] asNamen = new String[2];
		asNamen[0] = xAsNaam;
		asNamen[1] = yAsNaam;
		
		if (change) {
			for (int i=0 ; i<cMaxAantalStelsels ; i++) {	
				systems[i].updateFunctionBegin(asNamen, cVeldComponentGWT_diffVarNamen[0]);
				systems[i].updateFunctions(oldName, newName);
			}
		}
		
	}


}
